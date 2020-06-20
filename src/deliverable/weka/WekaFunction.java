package deliverable.weka;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import weka.core.Instances;
import weka.attributeSelection.CfsSubsetEval;
import weka.attributeSelection.GreedyStepwise;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.lazy.IBk;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.trees.RandomForest;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;
import weka.filters.supervised.instance.Resample;
import weka.filters.supervised.instance.SMOTE;
import weka.filters.supervised.instance.SpreadSubsample;


public final class WekaFunction {
	
	private WekaFunction() {}
	
	private static final Logger LOGGER = Logger.getLogger(WekaFunction.class.getName());
	
	
	public static List<String> applyFeatureSelection(Instances training, Instances testing, float percentClass, String sampling) throws Exception {
		
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
		
		try {
			
			//specify the dataset
			filter.setInputFormat(training);
			
			//apply
			Instances filteredTraining = Filter.useFilter(training, filter);
			
			Instances testingFiltered = Filter.useFilter(testing, filter);
			
			int numAttrFiltered = filteredTraining.numAttributes();
			filteredTraining.setClassIndex(numAttrFiltered - 1);
			testingFiltered.setClassIndex(numAttrFiltered - 1);
			
		
		}catch(Exception e) {
			
			LOGGER.log(Level.SEVERE, "[ERROR]", e);
			
			System.out.println("!4545");
		
		}
		
		return applySampling(training, testing, percentClass, sampling, "Yes");

	}
	
	public static List<String> applySampling(Instances training, Instances testing, float percentClass, String sampling, String featureSelection) throws Exception {
		
		List<String> samplingResult = new ArrayList<>();
	
		int numAttr = training.numAttributes();
		training.setClassIndex(numAttr - 1);
		testing.setClassIndex(numAttr - 1);		
		
		IBk cIBK = new IBk();
		RandomForest RandomForest = new RandomForest();
		NaiveBayes NaiveBaye = new NaiveBayes();

		try {
			
			cIBK.buildClassifier(training);
			RandomForest.buildClassifier(training);
			NaiveBaye.buildClassifier(training);
			
		}catch(Exception e) {
			
			LOGGER.log(Level.SEVERE, "[ERROR]", e);
		
		}
		
		Evaluation eval;
		FilteredClassifier fc;
		Resample resample = new Resample();
		String result;
		
		String[] opts;
	
		
		switch(sampling) {
		
			case "NO SAMPLING":
	
				/*NO SAMPLING, no filtered  classifier*/
				
				eval = new Evaluation(testing);
				
				eval.evaluateModel(cIBK, testing);
				result = setResult(cIBK, sampling, featureSelection, eval);
				samplingResult.add(result);
				
				eval.evaluateModel(RandomForest, testing);
				result = setResult(RandomForest, sampling, featureSelection, eval);
				samplingResult.add(result);
				
				eval.evaluateModel(NaiveBaye, testing);
				result = setResult(NaiveBaye, sampling, featureSelection, eval);
				samplingResult.add(result);
				
				break;
				
			case "OVERSAMPLING":
			
				/*Apply OVERSAMPLING*/
				
				eval = new Evaluation(testing);
				fc = new FilteredClassifier();
				resample.setInputFormat(training);
				opts = new String[]  {"-B", "1.0", "-Z", String.valueOf(2*percentClass*100)};
				resample.setOptions(opts);
				fc.setFilter(resample);
				
				/*Evaluation of IBK classifier*/
				
				fc.setClassifier(cIBK);
				fc.buildClassifier(training);
				eval.evaluateModel(fc, testing);
				result = setResult(cIBK, sampling, featureSelection, eval);
				samplingResult.add(result);
				
				/*Evaluation of RandomForest classifier*/
				
				fc.setClassifier(RandomForest);
				fc.buildClassifier(training);
				eval.evaluateModel(fc, testing);
				result = setResult(RandomForest, sampling, featureSelection, eval);
				samplingResult.add(result);
				
				/*Evaluation of NaiveBaye classifier*/
				
				fc.setClassifier(NaiveBaye);
				fc.buildClassifier(training);
				eval.evaluateModel(fc, testing);
				result = setResult(NaiveBaye, sampling, featureSelection, eval);
				samplingResult.add(result);
				
				break;
				
				
			case "UNDERSAMPLING":
				
				/*Apply UNDERSAMPLING*/
				
				eval = new Evaluation(training);
				fc = new FilteredClassifier();
				SpreadSubsample  spreadSubsample = new SpreadSubsample();
				spreadSubsample.setInputFormat(training);
				opts = new String[]{ "-M", "1.0"};
				spreadSubsample.setOptions(opts);
				fc.setFilter(spreadSubsample);
				
				/*Evaluation of IBK classifier*/
				
				fc.setClassifier(cIBK);
				fc.buildClassifier(training);
				eval.evaluateModel(fc, testing);
				result = setResult(cIBK, sampling, featureSelection, eval);
				samplingResult.add(result);
				
				/*Evaluation of RandomForest classifier*/
				
				fc.setClassifier(RandomForest);
				fc.buildClassifier(training);
				eval.evaluateModel(fc, testing);
				result = setResult(RandomForest, sampling, featureSelection, eval);
				samplingResult.add(result);
				
				/*Evaluation of NaiveBaye classifier*/
				
				fc.setClassifier(NaiveBaye);
				fc.buildClassifier(training);
				eval.evaluateModel(fc, testing);
				result = setResult(NaiveBaye, sampling, featureSelection, eval);
				samplingResult.add(result);
				
				break;
			
			case "SMOTE":
			
				/*SMOTE*/
				eval = new Evaluation(testing);
				SMOTE smote = new SMOTE();
				fc = new FilteredClassifier();
				smote.setInputFormat(training);
				fc.setFilter(smote);
	
				/*Evaluation of IBK classifier*/
				
				fc.setClassifier(cIBK);
				fc.buildClassifier(training);			   
				eval.evaluateModel(fc, testing);
				result = setResult(cIBK, sampling, featureSelection, eval);
				samplingResult.add(result);
			   
				/*Evaluation of RandomForest classifier*/

				fc.setClassifier(RandomForest);
				fc.buildClassifier(training);
				eval.evaluateModel(fc, testing);
				result = setResult(RandomForest, sampling, featureSelection, eval);
				samplingResult.add(result);
				
				/*Evaluation of NaiveBaye classifier*/
							   
				fc.setClassifier(NaiveBaye);			   
				fc.buildClassifier(training);			   
				eval.evaluateModel(fc, testing);
				result = setResult(NaiveBaye, sampling, featureSelection, eval);
				samplingResult.add(result);
				
				break;
		}
		
	   return samplingResult;
	}
	
	public static String setResult(Classifier fc, String sampling, String featureSelection, Evaluation eval) {
		
		String result;
		
		if(featureSelection.compareTo("Yes")==0) {
			result = fc + ";" + sampling + ";" + featureSelection + ";" + 
					eval.numTruePositives(1)  + ";" + eval.numFalsePositives(1)  + "," + eval.numTrueNegatives(1)  + ";" + eval.numFalseNegatives(1)  + ";" +
					eval.precision(1)  + ";" + eval.recall(1)  + ";" + eval.areaUnderROC(1)  + ";" + eval.kappa() + "\n";
		}else {
			result = fc + ";" + sampling + ";" + featureSelection + ";" + 
					eval.numTruePositives(1)  + ";" + eval.numFalsePositives(1)  + "," + eval.numTrueNegatives(1)  + ";" + eval.numFalseNegatives(1)  + ";" +
					eval.precision(1)  + ";" + eval.recall(1)  + ";" + eval.areaUnderROC(1)  + ";" + eval.kappa() + "\n";
		}
		
		return result;
		
	}

}
