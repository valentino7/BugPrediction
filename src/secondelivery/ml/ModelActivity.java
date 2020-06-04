package secondelivery.ml;

import weka.core.Instances;

import java.io.File;
import java.io.IOException;
import java.util.List;
import secondelivery.entity.OutputMl;
import secondelivery.strings.StringsSecondDelivery;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Normalize;
import weka.filters.unsupervised.attribute.Remove;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.LibSVM;
import weka.classifiers.lazy.IBk;
import weka.classifiers.meta.AdaBoostM1;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.converters.CSVLoader;
import weka.filters.supervised.instance.Resample;
import weka.filters.supervised.instance.SMOTE;
import weka.filters.supervised.instance.SpreadSubsample;


public class ModelActivity {

	private ModelActivity() {}


	public static Instances loadData() throws IOException  {
		CSVLoader loader = new CSVLoader();
		loader.setSource(new File(StringsSecondDelivery.PROJECT_NAME+StringsSecondDelivery.OUTPUTFILE));
		return loader.getDataSet();//get instances object		
	}
	
	private static Instances preprocessing(Instances dataset) throws Exception {
		//use a simple filter to remove a certain attribute	
		//set up options to remove 1st attribute	
		String[] opts = new String[]{ "-R", "2"};
		//create a Remove object (this is the filter class)
		Remove remove = new Remove();
		//set the filter options
		remove.setOptions(opts);
		//pass the dataset to the filter
		remove.setInputFormat(dataset);
		//apply the filter
		dataset = Filter.useFilter(dataset, remove);
		return dataset;
	}


	public static void walkForward(Instances dataset, List<OutputMl> listOutput) throws Exception {

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


			

			

			
			launchModelsFs(trainingSet, testSet, releaseIndex, listOutput);
			trainModel(trainingSet, "no fs", testSet, releaseIndex, listOutput);		

			releaseIndex = releaseIndex + 1.0;
		}
	}

	
	private static void launchModelsFs(Instances trainingSet, Instances testSet, double releaseIndex, List<OutputMl> listOutput) throws Exception {
		
		TypeFilter pcaFilter = new PcaSelection();
		TypeFilter filter = new FilterSelection();
		TypeFilter wrapperFilter = new WrapperSelection();
		
		//Feature selections PCA

		//rimuovo attributo stringa
		Instances rmtrainingSet = preprocessing(trainingSet);
		Instances rmtestset = preprocessing(testSet);

		Filter pcafilter = pcaFilter.getFilter(rmtrainingSet);
		Instances pcaTrainingSet = Filter.useFilter(rmtrainingSet, pcafilter);
		Instances pcaTestSet = Filter.useFilter(rmtestset, pcafilter);

		//normalizzazione dei dati, pca la fa in automatico
		trainingSet = normalization(trainingSet);
		testSet = normalization(testSet);

		//filter selection
		Filter filterSelection = filter.getFilter(trainingSet);
		Instances filteredTrainingSet = Filter.useFilter(trainingSet, filterSelection);
		Instances filterTestSet = Filter.useFilter(testSet, filterSelection);

		//wrapper filter con nayve bayes
		Filter wrapperSelection = wrapperFilter.getFilter(trainingSet);
		Instances wrapperedTrainingSet = Filter.useFilter(trainingSet, wrapperSelection);
		Instances wrapperTestSet = Filter.useFilter(testSet, wrapperSelection);
		
		if(pcaTrainingSet.numAttributes() > 1)
			trainModel(pcaTrainingSet, "pca", pcaTestSet, releaseIndex, listOutput);
		if(filteredTrainingSet.numAttributes() > 1)
			trainModel(filteredTrainingSet, "filter", filterTestSet, releaseIndex, listOutput);
		if(wrapperedTrainingSet.numAttributes() > 1)
			trainModel(wrapperedTrainingSet, "wrapper", wrapperTestSet, releaseIndex, listOutput);
	}


	private static Instances normalization(Instances dataset) throws Exception {
		//normalizzazione
		Normalize filterNorm = new Normalize();
		filterNorm.setInputFormat(dataset);
		return Filter.useFilter(dataset, filterNorm);
	}

	private static void trainModel(Instances trainingSet, String nameFs, Instances testSet, double releaseIndex, List<OutputMl> listOutput) throws Exception {
		//MODELLO
		RandomForest randomForest = new RandomForest();
		LibSVM svm = new LibSVM();
		AdaBoostM1 adaBoost = new AdaBoostM1();
		J48 j48 = new J48();
		IBk ibk = new IBk();
		NaiveBayes naiveBayes = new NaiveBayes();


		SpreadSubsample  spreadSubsample = new SpreadSubsample();
		String[] opts = new String[]{"-M", "1.0"};
		spreadSubsample.setOptions(opts);


		Resample resample = new Resample();
		resample.setInputFormat(trainingSet);


		SMOTE smote = new SMOTE();
		smote.setInputFormat(trainingSet);

		String strUndersampling = "undersampling";
		String strOversampling = "oversampling";
		String strSmote = "smote";

		String m1 = "Random Forest";

		sampling(trainingSet, testSet, randomForest, resample, new OutputMl(nameFs, strOversampling, releaseIndex, m1), listOutput);
		sampling(trainingSet, testSet, randomForest, spreadSubsample, new OutputMl(nameFs, strUndersampling, releaseIndex, m1), listOutput);
		sampling(trainingSet, testSet, randomForest, smote, new OutputMl(nameFs, strSmote, releaseIndex, m1), listOutput);

		String m2 = "svm";
		sampling(trainingSet, testSet, svm, resample, new OutputMl(nameFs, strOversampling, releaseIndex, m2), listOutput);
		sampling(trainingSet, testSet, svm, spreadSubsample, new OutputMl(nameFs, strUndersampling, releaseIndex, m2), listOutput);
		sampling(trainingSet, testSet, svm, smote, new OutputMl(nameFs, strSmote, releaseIndex, m2), listOutput);

		String m3 = "Ada boost";
		sampling(trainingSet, testSet, adaBoost, resample, new OutputMl(nameFs, strOversampling, releaseIndex, m3), listOutput);
		sampling(trainingSet, testSet, adaBoost, spreadSubsample, new OutputMl(nameFs, strUndersampling, releaseIndex, m3), listOutput);
		sampling(trainingSet, testSet, adaBoost, smote, new OutputMl(nameFs, strSmote, releaseIndex, m3), listOutput);

		String m4 = "j48";
		sampling(trainingSet, testSet, j48, resample, new OutputMl(nameFs, strOversampling, releaseIndex, m4), listOutput);
		sampling(trainingSet, testSet, j48, spreadSubsample, new OutputMl(nameFs, strUndersampling, releaseIndex, m4), listOutput);
		sampling(trainingSet, testSet, j48, smote, new OutputMl(nameFs, strSmote, releaseIndex, m4), listOutput);

		String m5 = "Ibk";
		sampling(trainingSet, testSet, ibk, resample, new OutputMl(nameFs, strOversampling, releaseIndex, m5), listOutput);
		sampling(trainingSet, testSet, ibk, spreadSubsample, new OutputMl(nameFs, strUndersampling, releaseIndex, m5), listOutput);
		sampling(trainingSet, testSet, ibk, smote, new OutputMl(nameFs, strSmote, releaseIndex, m5), listOutput);

		String m6 = "Naive Bayes";
		sampling(trainingSet, testSet, naiveBayes, resample, new OutputMl(nameFs, strOversampling, releaseIndex, m6), listOutput);
		sampling(trainingSet, testSet, naiveBayes, spreadSubsample, new OutputMl(nameFs, strUndersampling, releaseIndex, m6), listOutput);
		sampling(trainingSet, testSet, naiveBayes, smote, new OutputMl(nameFs, strSmote, releaseIndex, m6), listOutput);


		//nosampling
		String noSampling = "No sampling";
		noSamplingEvaluation(randomForest, trainingSet, testSet, new OutputMl(nameFs, noSampling, releaseIndex, m1), listOutput);
		noSamplingEvaluation(svm, trainingSet, testSet, new OutputMl(nameFs, noSampling, releaseIndex, m2), listOutput);
		noSamplingEvaluation(j48, trainingSet, testSet, new OutputMl(nameFs, noSampling, releaseIndex, m3), listOutput);
		noSamplingEvaluation(adaBoost, trainingSet, testSet, new OutputMl(nameFs, noSampling, releaseIndex, m4), listOutput);
		noSamplingEvaluation(ibk, trainingSet, testSet, new OutputMl(nameFs, noSampling, releaseIndex, m5), listOutput);
		noSamplingEvaluation(naiveBayes, trainingSet, testSet, new OutputMl(nameFs, noSampling, releaseIndex, m6), listOutput);

	}

	private static void noSamplingEvaluation(Classifier classifier, Instances train, Instances test,
			OutputMl o, List<OutputMl> listOutput) throws Exception {
		classifier.buildClassifier(train);
		evaluation(classifier, test, o, listOutput);
	}

	private static void sampling(Instances train, Instances test, Classifier classifier, Filter filter,
			OutputMl o, List<OutputMl> listOutput) throws Exception {

		FilteredClassifier fc = new FilteredClassifier();
		fc.setFilter(filter);
		fc.setClassifier(classifier);
		fc.buildClassifier(train);

		evaluation(classifier, test, o, listOutput);
	}


	private static void evaluation(Classifier classifier, Instances test, OutputMl o, List<OutputMl> listOutput) throws Exception {
		Evaluation eval = new Evaluation(test);	
		eval.evaluateModel(classifier, test); 	
		o.setAuc(eval.areaUnderROC(1));
		o.setKappa(eval.kappa());
		o.setPrecision(eval.precision(1));
		o.setRecall(eval.recall(1));
		listOutput.add(o);

	}


}
