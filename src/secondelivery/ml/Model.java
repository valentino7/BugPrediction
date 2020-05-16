package secondelivery.ml;

import java.io.File;

import secondelivery.strings.StringsSecondDelivery;
import weka.core.Debug.Random;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;

import weka.attributeSelection.CfsSubsetEval;
import weka.attributeSelection.GreedyStepwise;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;
import weka.filters.supervised.attribute.Discretize;
import weka.filters.unsupervised.attribute.PrincipalComponents;
import weka.filters.unsupervised.attribute.Remove;
import weka.attributeSelection.Ranker;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.converters.ConverterUtils.DataSource;

public class Model {



	//lista dei repository
	private static final String[] directories = {"bookkeeper"};

	public static void main(String[] args) throws Exception  {

		// load CSV
		CSVLoader loader = new CSVLoader();
		loader.setSource(new File(directories[0]+ StringsSecondDelivery.OUTPUTFILE));
		Instances data = loader.getDataSet();//get instances object

		// save ARFF
		ArffSaver saver = new ArffSaver();
		saver.setInstances(data);//set the dataset we want to convert
		//and save as ARFF
		saver.setFile(new File("prova.arff"));
		saver.writeBatch();

//				attributeSelection();

		pca();

	}


	private static void pca() throws Exception {
		
		
		
		//load dataset
		DataSource source = new DataSource("prova.arff");
		Instances dataset = source.getDataSet();
		//        trainingSet.setClassIndex(trainingSet.numAttributes()-1);

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
		Instances trainingSet = Filter.useFilter(dataset, remove);
		System.out.println(trainingSet.get(0));

		System.out.println("num attributi iniziali: " + trainingSet.numAttributes());


		//		 Normalize filterNorm = new Normalize();
		//	     filterNorm.setInputFormat(trainingSet);
		//	     trainingSet = Filter.useFilter(trainingSet, filterNorm);


		//create AttributeSelection object

		

        trainingSet.setClassIndex(trainingSet.numAttributes()-1);

//		AttributeSelection filter = new AttributeSelection(); // package weka.filters.supervised.attribute!
		PrincipalComponents pca = new PrincipalComponents();
		//standardizza i dati
		pca.setCenterData(false);
		
		String[] op = new String[]{ "-R", "0.95","-A","5","-M","-1"};
		pca.setOptions(op);
//		pca.setMaximumAttributes(9);
//		pca.setVarianceCovered(0.95);
		//pca.setTransformBackToOriginal(true);
		//		for(int i=0;i!=18;i++)
		//			System.out.println(pca.evaluateAttribute(i));
		//		System.out.println(pca.getCorrelationMatrix());
		//		System.out.println(pca.getEigenValues());
		//		
//		Ranker rank = new Ranker();
//		//		rank.setThreshold(0);
//		filter.setEvaluator(pca);
//		filter.setSearch(rank);
		
		pca.setInputFormat(trainingSet);
		
		//apply
		Instances newData = Filter.useFilter(trainingSet, pca);
		System.out.println("num attributi PCA " + newData.numAttributes());

//		newData.insertAttributeAt(trainingSet1.attribute(18), 7);
//		newData.insertAttributeAt(trainingSet1.attribute(0), 0);
//		newData.insertAttributeAt(trainingSet1.attribute(1), 1);
//		newData.addAll(trainingSet1.attribute(18));



		//get porzione del dataset come versione
		int i=0;
		for(;;) {
			if(trainingSet.get(i).value(0) != 1.0)
				break;
			i++;
		}
		System.out.println(newData.get(0));




		Instances trainingSetVersion = new Instances(newData, 0, i);
		System.out.println(trainingSetVersion.size());

		int trainSize = (int) Math.round(trainingSetVersion.numInstances() * 0.8);
		int testSize = trainingSetVersion.numInstances() - trainSize;
		Instances train = new Instances(trainingSetVersion, 0, trainSize);
		Instances testing = new Instances(trainingSetVersion, trainSize, testSize);


		//MODELLO
		int numAttr = train.numAttributes();
//		train.setClassIndex(numAttr - 1);
//		testing.setClassIndex(numAttr - 1);

		System.out.println(train.classAttribute());
		NaiveBayes classifier = new NaiveBayes();

		classifier.buildClassifier(testing);

		Evaluation eval = new Evaluation(testing);	

		eval.evaluateModel(classifier, testing); 

		System.out.println("AUC = "+eval.areaUnderROC(1));
		System.out.println("kappa = "+eval.kappa());	
		System.out.println("Precision = "+eval.precision(1));
		System.out.println("Recall = "+eval.recall(1));
//		System.out.println("recall = "+eval.);






		//save
//		ArffSaver saver = new ArffSaver();
//		saver.setInstances(newData);
//		saver.setFile(new File("provaPCA.arff"));
//		saver.writeBatch();
		//				train = Filter.useFilter(train, filter);
		//				test = Filter.useFilter(test, filter);	

	}


	private static void attributeSelection() throws Exception {
		//load dataset
		DataSource source = new DataSource("prova.arff");
		Instances dataset = source.getDataSet();




		//create AttributeSelection object
		AttributeSelection filter = new AttributeSelection();
		//create evaluator and search algorithm objects
		CfsSubsetEval eval = new CfsSubsetEval();
		GreedyStepwise search = new GreedyStepwise();
		//set the algorithm to search backward
		search.setSearchBackwards(true);
		//set the filter to use the evaluator and search algorithm
		filter.setEvaluator(eval);
		filter.setSearch(search);
		//specify the dataset
		filter.setInputFormat(dataset);
		//apply
		Instances trainingSet = Filter.useFilter(dataset, filter);
		
		
		
		int i=0;
		for(;;) {
			if(dataset.get(i).value(0) != 1.0)
				break;
			i++;
		}
		System.out.println("dopo filtro "+trainingSet.get(0));




		Instances trainingSetVersion = new Instances(trainingSet, 0, i);
		System.out.println(trainingSetVersion.size());

		int trainSize = (int) Math.round(trainingSetVersion.numInstances() * 0.8);
		int testSize = trainingSet.numInstances() - trainSize;
		Instances train = new Instances(trainingSet, 0, trainSize);
		Instances testing = new Instances(trainingSet, trainSize, testSize);


		//MODELLO
		int numAttr = trainingSet.numAttributes();
		train.setClassIndex(numAttr - 1);
		testing.setClassIndex(numAttr - 1);

		NaiveBayes classifier = new NaiveBayes();

		classifier.buildClassifier(train);

		Evaluation e = new Evaluation(testing);	

		e.evaluateModel(classifier, testing); 

		System.out.println("AUC = "+e.areaUnderROC(1));
		System.out.println("kappa = "+e.kappa());
		
		
		
		
		
		
		
		//save
//		ArffSaver saver = new ArffSaver();
//		saver.setInstances(newData);
//		saver.setFile(new File("provaWithSelection.arff"));
//		saver.writeBatch();
		
	}


}
