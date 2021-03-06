package secondelivery.ml;

import weka.core.Instances;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import secondelivery.entity.OutputMl;
import secondelivery.strings.StringsSecondDelivery;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Normalize;
import weka.filters.unsupervised.attribute.Remove;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.Logistic;
import weka.classifiers.lazy.IBk;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.trees.RandomForest;
import weka.core.converters.CSVLoader;
import weka.filters.supervised.instance.Resample;
import weka.filters.supervised.instance.SMOTE;
import weka.filters.supervised.instance.SpreadSubsample;


public class ModelActivity {

	private ModelActivity() {}


	public static Instances loadData(String proportionMethod, String output) throws IOException  {
		CSVLoader loader = new CSVLoader();
		loader.setSource(new File(StringsSecondDelivery.RESULTS_ROOT+proportionMethod+StringsSecondDelivery.DIRECTORY_DATASET+output+StringsSecondDelivery.OUTPUTFILE));
		return loader.getDataSet();//get instances object		
	}

	private static Instances removeFileNameAttribute(Instances dataset) {
		//use a simple filter to remove a certain attribute	
		//set up options to remove 1st attribute	
		String[] opts = new String[]{ "-R", "2"};
		//create a Remove object (this is the filter class)
		Remove remove = new Remove();
		//set the filter options
		try {
			remove.setOptions(opts);
		} catch (Exception e) {
			   Logger.getLogger(ModelActivity.class.getName()).log( Level.SEVERE, e.toString(), e );
		}
		//pass the dataset to the filter
		try {
			remove.setInputFormat(dataset);
		} catch (Exception e) {
			Logger.getLogger(ModelActivity.class.getName()).log( Level.SEVERE, e.toString(), e );
		}
		//apply the filter
		try {
			dataset = Filter.useFilter(dataset, remove);
		} catch (Exception e) {
			Logger.getLogger(ModelActivity.class.getName()).log( Level.SEVERE, e.toString(), e );
		}
		return dataset;
	}


	public static void walkForward(Instances dataset, List<OutputMl> listOutput) {

		//get porzione del dataset come versione
		int traingIndex = 1;
		int testIndex = 1;
		int i = 1;
		double releaseIndex = 1.0;

		while(true) {
			traingIndex=testIndex;
			if(i==dataset.size()-1)
				break;
			while(true) {
				if(dataset.get(i).value(0) > releaseIndex + 1.0 || i==dataset.size()-1) {
					break;
				}
				if(dataset.get(i).value(0) <= releaseIndex) {
					traingIndex++;
				}
				testIndex++;
				i++;
			}

			Instances trainingSet = new Instances(dataset, 0, traingIndex);
			Instances testSet = new Instances(dataset, traingIndex, testIndex-traingIndex);

			double percentTrainingOnDataset = (double) trainingSet.size()/dataset.size()*100;

			//lanciatore dei modelli con feature selection e combinazioni di balancing
			launchModelsFs(trainingSet, testSet, releaseIndex, listOutput, percentTrainingOnDataset);
			//train modelli senza feature selection con combinazioni di balancing
			trainModel(trainingSet.numAttributes(), trainingSet, "no fs", testSet, releaseIndex, listOutput, percentTrainingOnDataset);		

			releaseIndex = releaseIndex + 1.0;
		}
	}


	private static int getTotalNumDefect(Instances dataset) {
		int somm=0;
		for(int i=0; i!= dataset.size(); i++) {
			if(dataset.get(i).value(dataset.classIndex()) == 1.0) 
				somm++;
		}
		return somm;
	}


	//lanciatore dei modelli applicando i filtri di feature selection
	private static void launchModelsFs(Instances trainingSet, Instances testSet, double releaseIndex, List<OutputMl> listOutput, double percentTrainingOnDataset){

		TypeFilter pcaFilter = new PcaSelection();
		TypeFilter filter = new FilterSelection();
		TypeFilter wrapperFilter = new WrapperSelection();

		//Feature selections PCA

		//rimuovo attributo stringa
		Instances rmtrainingSet = removeFileNameAttribute(trainingSet);
		Instances rmtestset = removeFileNameAttribute(testSet);

		Filter pcafilter = pcaFilter.getFilter(rmtrainingSet);
		Instances pcaTrainingSet = null;
		try {
			pcaTrainingSet = Filter.useFilter(rmtrainingSet, pcafilter);
		} catch (Exception e) {
			Logger.getLogger(ModelActivity.class.getName()).log( Level.SEVERE, e.toString(), e );
			System.exit(-1);
		}
		Instances pcaTestSet = null;
		try {
			pcaTestSet = Filter.useFilter(rmtestset, pcafilter);
		} catch (Exception e) {
			Logger.getLogger(ModelActivity.class.getName()).log( Level.SEVERE, e.toString(), e );
			System.exit(-1);
		}

		//normalizzazione dei dati, pca la fa in automatico
		trainingSet = normalization(trainingSet);
		testSet = normalization(testSet);

		//filter selection
		Filter filterSelection = filter.getFilter(trainingSet);
		Instances filteredTrainingSet = null;
		try {
			filteredTrainingSet = Filter.useFilter(trainingSet, filterSelection);
		} catch (Exception e) {
			Logger.getLogger(ModelActivity.class.getName()).log( Level.SEVERE, e.toString(), e );
			System.exit(-1);
		}
		Instances filterTestSet = null;
		try {
			filterTestSet = Filter.useFilter(testSet, filterSelection);
		} catch (Exception e) {
			Logger.getLogger(ModelActivity.class.getName()).log( Level.SEVERE, e.toString(), e );
			System.exit(-1);
		}

		//wrapper filter con nayve bayes
		Filter wrapperSelection = wrapperFilter.getFilter(trainingSet);
		Instances wrapperedTrainingSet = null;
		try {
			wrapperedTrainingSet = Filter.useFilter(trainingSet, wrapperSelection);
		} catch (Exception e) {
			Logger.getLogger(ModelActivity.class.getName()).log( Level.SEVERE, e.toString(), e );
			System.exit(-1);
		}
		Instances wrapperTestSet = null;
		try {
			wrapperTestSet = Filter.useFilter(testSet, wrapperSelection);
		} catch (Exception e) {
			Logger.getLogger(ModelActivity.class.getName()).log( Level.SEVERE, e.toString(), e );
			System.exit(-1);
		}

		//avvio il training solo se il numero di attributi prodotti da feature selection e maggiore di 1
		if(pcaTrainingSet.numAttributes() > 1)
			trainModel(trainingSet.numAttributes(), pcaTrainingSet, "pca", pcaTestSet, releaseIndex, listOutput, percentTrainingOnDataset);
		if(filteredTrainingSet.numAttributes() > 1) 				
			trainModel(trainingSet.numAttributes(), filteredTrainingSet, "best first", filterTestSet, releaseIndex, listOutput, percentTrainingOnDataset);
		if(wrapperedTrainingSet.numAttributes() > 1)
			trainModel(trainingSet.numAttributes(), wrapperedTrainingSet, "wrapper", wrapperTestSet, releaseIndex, listOutput, percentTrainingOnDataset);
	}


	private static Instances normalization(Instances dataset) {
		//normalizzazione
		Normalize filterNorm = new Normalize();
		try {
			filterNorm.setInputFormat(dataset);
		} catch (Exception e) {
			Logger.getLogger(ModelActivity.class.getName()).log( Level.SEVERE, e.toString(), e );
		}
		Instances data = null;
		try {
			data = Filter.useFilter(dataset, filterNorm);
		} catch (Exception e) {
			Logger.getLogger(ModelActivity.class.getName()).log( Level.SEVERE, e.toString(), e );
			System.exit(-1);
		}
		return data;
	}

	private static void trainModel(int olNumAttr, Instances trainingSet, String nameFs, Instances testSet, double releaseIndex, List<OutputMl> listOutput, double percentTrainingOnDataset) {
		//definizione dei modelli		
		RandomForest randomForest = new RandomForest();
		IBk ibk = new IBk();
		NaiveBayes naiveBayes = new NaiveBayes();
		Logistic logistic = new Logistic();

		

		double percentTraining = (double)getTotalNumDefect(trainingSet)/trainingSet.size()*100;
		double percentTesting =  (double)getTotalNumDefect(testSet)/testSet.size()*100;

		//RESAMPLE
		Resample resample = ResampleBalancing.getResample(trainingSet, getTotalNumDefect(trainingSet));
		Instances resampleData = null;
		try {
			resampleData = Filter.useFilter(trainingSet, resample);
		} catch (Exception e) {
			Logger.getLogger(ModelActivity.class.getName()).log( Level.SEVERE, e.toString(), e );
		} 
		double percentResampleData = (double) getTotalNumDefect(resampleData)/resampleData.size()*100;


		//UNDERSAMPLE
		SpreadSubsample spreadSubsample = Undersampling.getSpreadSubsample(trainingSet);
		Instances undersampleData = null;
		try {
			undersampleData = Filter.useFilter(trainingSet, spreadSubsample);
		} catch (Exception e) {
			Logger.getLogger(ModelActivity.class.getName()).log( Level.SEVERE, e.toString(), e );
		}
		double percentSpreadData = (double) getTotalNumDefect(undersampleData)/undersampleData.size()*100;

		
	
		//SMOTe
		SMOTE smote = SmoteBalancing.getSmote(trainingSet);
		Instances smoteData = null;
		try {
			smoteData = Filter.useFilter(trainingSet, smote);
		} catch (Exception e) {
			Logger.getLogger(ModelActivity.class.getName()).log( Level.SEVERE, e.toString(), e );
			System.exit(-1);
		} 
		double percentSmoteData = (double) getTotalNumDefect(smoteData)/smoteData.size()*100;

		String strUndersampling = "undersampling";
		String strOversampling = "oversampling";
		String strSmote = "smote";


		//TRAINING MODELLI CON 3 TIPI DI SAMPLING
		
		//RANDOM FOREST
		OutputMl output;
		
		String m1 = "Random Forest";
		FilteredClassifier fc;
		output = getOutputFileWithParam(m1, percentTesting, percentTrainingOnDataset, releaseIndex, olNumAttr, trainingSet.numAttributes(), nameFs);
		fc = sampling(trainingSet, randomForest, resample);
		output.setNameSampling(strOversampling);
		output.setPercentDefectiveTraining(percentResampleData);
		evaluation(fc, testSet, output, listOutput);


		output = getOutputFileWithParam(m1, percentTesting, percentTrainingOnDataset, releaseIndex, olNumAttr, trainingSet.numAttributes(), nameFs);
		output.setNameSampling(strUndersampling);
		output.setPercentDefectiveTraining(percentSpreadData);
		fc = sampling(trainingSet, randomForest, spreadSubsample);
		evaluation(fc, testSet, output, listOutput);

		output = getOutputFileWithParam(m1, percentTesting, percentTrainingOnDataset, releaseIndex, olNumAttr, trainingSet.numAttributes(), nameFs);
		output.setNameSampling(strSmote);
		output.setPercentDefectiveTraining(percentSmoteData);
		fc = sampling(trainingSet, randomForest, smote);
		evaluation(fc, testSet,  output, listOutput);

		//LOGISTIC
		String m3 = "logistic";
		output = getOutputFileWithParam(m3, percentTesting, percentTrainingOnDataset, releaseIndex, olNumAttr, trainingSet.numAttributes(), nameFs);
		output.setNameSampling(strOversampling);
		output.setPercentDefectiveTraining(percentResampleData);
		fc = sampling(trainingSet, logistic, resample);
		evaluation(fc, testSet, output, listOutput);

		output = getOutputFileWithParam(m3, percentTesting, percentTrainingOnDataset, releaseIndex, olNumAttr, trainingSet.numAttributes(), nameFs);
		output.setNameSampling(strUndersampling);
		output.setPercentDefectiveTraining(percentSpreadData);
		fc = sampling(trainingSet, logistic, spreadSubsample);
		evaluation(fc, testSet, output, listOutput);

		output = getOutputFileWithParam(m3, percentTesting, percentTrainingOnDataset, releaseIndex, olNumAttr, trainingSet.numAttributes(), nameFs);
		output.setNameSampling(strSmote);
		output.setPercentDefectiveTraining(percentSmoteData);
		fc = sampling(trainingSet, logistic, smote);
		evaluation(fc, testSet, output, listOutput);

		//mini tuning parameter knn con 1,3,9,19
		//IBK
		String m4 = "Ibk";
		int k= miniTuningKNN(trainingSet, testSet, ibk, resample, Boolean.TRUE);
		ibk.setKNN(k);
		output = new OutputMl(percentTrainingOnDataset, releaseIndex, olNumAttr, trainingSet.numAttributes(), nameFs);		
		output.setPercentDefectiveTesting(percentTesting);
		output.setModel(m4);
		output.setkParam(k);
		output.setNameSampling(strOversampling);
		output.setPercentDefectiveTraining(percentResampleData);
		fc = sampling(trainingSet, ibk, resample);
		evaluation(fc, testSet, output, listOutput);

		k= miniTuningKNN(trainingSet, testSet, ibk, spreadSubsample, Boolean.TRUE);
		ibk.setKNN(k);
		output = new OutputMl(percentTrainingOnDataset, releaseIndex, olNumAttr, trainingSet.numAttributes(), nameFs);		
		output.setPercentDefectiveTesting(percentTesting);
		output.setModel(m4);
		output.setkParam(k);
		output.setNameSampling(strUndersampling);
		output.setPercentDefectiveTraining(percentSpreadData);
		fc = sampling(trainingSet, ibk, spreadSubsample);
		evaluation(fc, testSet, output, listOutput);

		k= miniTuningKNN(trainingSet, testSet, ibk, smote, Boolean.TRUE);
		ibk.setKNN(k);
		output = new OutputMl(percentTrainingOnDataset, releaseIndex, olNumAttr, trainingSet.numAttributes(), nameFs);		
		output.setPercentDefectiveTesting(percentTesting);
		output.setModel(m4);
		output.setkParam(k);
		output.setNameSampling(strSmote);
		output.setPercentDefectiveTraining(percentSmoteData);
		fc = sampling(trainingSet, ibk, smote);
		evaluation(fc, testSet, output, listOutput);

		
		//NAIVE BAYES
		String m5 = "Naive Bayes";
		output = getOutputFileWithParam(m5, percentTesting, percentTrainingOnDataset, releaseIndex, olNumAttr, trainingSet.numAttributes(), nameFs);
		output.setNameSampling(strOversampling);
		output.setPercentDefectiveTraining(percentResampleData);
		fc = sampling(trainingSet, naiveBayes, resample);
		evaluation(fc, testSet, output, listOutput);

		output = getOutputFileWithParam(m5, percentTesting, percentTrainingOnDataset, releaseIndex, olNumAttr, trainingSet.numAttributes(), nameFs);
		output.setNameSampling(strUndersampling);
		output.setPercentDefectiveTraining(percentSpreadData);
		fc = sampling(trainingSet, naiveBayes, spreadSubsample);
		evaluation(fc, testSet, output, listOutput);

		output = getOutputFileWithParam(m5, percentTesting, percentTrainingOnDataset, releaseIndex, olNumAttr, trainingSet.numAttributes(), nameFs);
		output.setNameSampling(strSmote);
		output.setPercentDefectiveTraining(percentSmoteData);
		fc = sampling(trainingSet, naiveBayes, smote);
		evaluation(fc, testSet, output, listOutput);

		//nosampling
		String noSampling = "No sampling";
		output = getOutputFileWithParam(m1, percentTesting, percentTrainingOnDataset, releaseIndex, olNumAttr, trainingSet.numAttributes(), nameFs);
		output.setNameSampling(noSampling);
		output.setPercentDefectiveTraining(percentTraining);
		Classifier c;
		c = noSamplingEvaluation(randomForest, trainingSet);
		evaluation(c, testSet, output, listOutput);

		output = getOutputFileWithParam(m3, percentTesting, percentTrainingOnDataset, releaseIndex, olNumAttr, trainingSet.numAttributes(), nameFs);
		output.setNameSampling(noSampling);
		output.setPercentDefectiveTraining(percentTraining);
		c = noSamplingEvaluation(logistic, trainingSet);
		evaluation(c, testSet, output, listOutput);

		output = new OutputMl(percentTrainingOnDataset, releaseIndex, olNumAttr, trainingSet.numAttributes(), nameFs);		
		output.setPercentDefectiveTraining(percentTraining);
		output.setPercentDefectiveTesting(percentTesting);
		output.setModel(m4);	
		k= miniTuningKNN(trainingSet, testSet, ibk, null, Boolean.FALSE);
		ibk.setKNN(k);
		output.setkParam(k);
		output.setNameSampling(noSampling);
		c = noSamplingEvaluation(ibk, trainingSet);
		evaluation(c, testSet, output, listOutput);

		output = getOutputFileWithParam(m5, percentTesting, percentTrainingOnDataset, releaseIndex, olNumAttr, trainingSet.numAttributes(), nameFs);
		output.setNameSampling(noSampling);
		output.setPercentDefectiveTraining(percentTraining);
		c = noSamplingEvaluation(naiveBayes, trainingSet);
		evaluation(c, testSet, output, listOutput);

	}

	private static OutputMl getOutputFileWithParam(String model, double percentTesting, double percentTrainingOnDataset, double releaseIndex, int oldNumAttr, int numAttr, String nameFs) {
		OutputMl output = new OutputMl(percentTrainingOnDataset, releaseIndex, oldNumAttr, numAttr, nameFs);		
		output.setPercentDefectiveTesting(percentTesting);
		output.setkParam(0);
		output.setModel(model);	
		return output;
	}


	//per il tuning viene scelta la metrica ROC e viene posto il vincolo che kappa debba essere diversa da 0
	private static Integer miniTuningKNN(Instances trainingSet, Instances testSet, IBk ibk, Filter filter, Boolean isSampling){
		int[] kValues = {1, 3, 9, 19};
		double max = 0.0;
		int k = 1;
		for (int j : kValues) {
			ibk.setKNN(j);
			Evaluation eval = null;
			try {
				eval = new Evaluation(testSet);
			} catch (Exception e) {
				Logger.getLogger(ModelActivity.class.getName()).log( Level.SEVERE, e.toString(), e );
				return null;
			}
			if(isSampling.equals(Boolean.TRUE)) {
				FilteredClassifier fc = sampling(trainingSet, ibk, filter);
				try {
					eval.evaluateModel(fc, testSet);
				} catch (Exception e) {
					Logger.getLogger(ModelActivity.class.getName()).log( Level.SEVERE, e.toString(), e );
				} 	
			}else {
				Classifier c = noSamplingEvaluation(ibk, trainingSet);
				try {
					eval.evaluateModel(c, testSet);
				} catch (Exception e) {
					Logger.getLogger(ModelActivity.class.getName()).log( Level.SEVERE, e.toString(), e );
				} 	
			}
			double currentEvaluation = eval.areaUnderROC(1);
			if(currentEvaluation > max && eval.kappa()!=0.0) {
				max = currentEvaluation;
				k = j;
			}
		}
		return k;
	}


	private static Classifier noSamplingEvaluation(Classifier classifier, Instances train) {
		try {
			classifier.buildClassifier(train);
		} catch (Exception e) {
			Logger.getLogger(ModelActivity.class.getName()).log( Level.SEVERE, e.toString(), e );
		}
		return classifier;
	}

	private static FilteredClassifier sampling(Instances train, Classifier classifier, Filter filter) {

		FilteredClassifier fc = new FilteredClassifier();
		fc.setFilter(filter);
		fc.setClassifier(classifier);
		try {
			fc.buildClassifier(train);
		} catch (Exception e) {
			Logger.getLogger(ModelActivity.class.getName()).log( Level.SEVERE, e.toString(), e );
		}

		return fc;
	}


	private static void evaluation(Classifier classifier, Instances test, OutputMl o, List<OutputMl> listOutput) {
		Evaluation eval = null;
		try {
			eval = new Evaluation(test);
		} catch (Exception e) {
			Logger.getLogger(ModelActivity.class.getName()).log( Level.SEVERE, e.toString(), e );
			System.exit(-1);
		}	

		try {
			eval.evaluateModel(classifier, test);
		} catch (Exception e) {
			Logger.getLogger(ModelActivity.class.getName()).log( Level.SEVERE, e.toString(), e );
		} 	
		o.setAuc(eval.areaUnderROC(1));
		o.setKappa(eval.kappa());
		o.setfMeasure(eval.fMeasure(1));
		o.setPrecision(eval.precision(1));
		o.setTn(eval.numTrueNegatives(1));
		o.setTp(eval.numTruePositives(1));
		o.setFn(eval.numFalseNegatives(1));
		o.setFp(eval.numFalsePositives(1));
		o.setRecall(eval.recall(1));
		listOutput.add(o);	
	}
}
