package deliverable.weka;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import weka.attributeSelection.CfsSubsetEval;
import weka.attributeSelection.GreedyStepwise;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.lazy.IBk;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;
import weka.filters.supervised.instance.Resample;
import weka.filters.supervised.instance.SMOTE;
import weka.filters.supervised.instance.SpreadSubsample;

public final class WekaFunction {
	
	private WekaFunction() {}
	
	private static final Logger LOGGER = Logger.getLogger(WekaFunction.class.getName());
	
	private static final String IBK = "IBK";
	
	private static final String RANDOMFOREST = "RandomForest";
	
	private static final String NAIVEBAYES = "NaiveBayes";
	
	private static final String NOSAMPLING = "NO SAMPLING";
	
	private static final String OVERSAMPLING = "OVERSAMPLING";
	
	private static final String UNDERSAMPLING = "UNDERSAMPLING";
	
	private static final String SMOTE = "SMOTE";
	
	private static final String ERRORE = "[ERROR]";
	
	public static List<String> applyFeatureSelection(Instances training, Instances testing, float percentClass) {
		
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
			
			filter.setInputFormat(training);
			Instances filteredTraining =  Filter.useFilter(training, filter);
			Instances testingFiltered = Filter.useFilter(testing, filter);
			int numAttrFiltered = filteredTraining.numAttributes();
			filteredTraining.setClassIndex(numAttrFiltered - 1);
			testingFiltered.setClassIndex(numAttrFiltered - 1);
			
		}catch(Exception e) {
			
			LOGGER.log(Level.SEVERE, ERRORE, e);
			
		}
		
		return applySampling(training, testing, percentClass, "Yes");

	}
	
	/* apply the balancing */
	
	public static List<String> applySampling(Instances training, Instances testing, float percentClass, String featureSelection) {

		List<String> samplingResult = new ArrayList<>();
	
		int numAttr = training.numAttributes();
		training.setClassIndex(numAttr - 1);
		testing.setClassIndex(numAttr - 1);		
		
		IBk cIBK = new IBk();
		RandomForest randomForest = new RandomForest();
		NaiveBayes naiveBayes = new NaiveBayes();

		try {
			
			cIBK.buildClassifier(training);
			randomForest.buildClassifier(training);
			naiveBayes.buildClassifier(training);
			
		}catch(Exception e) {
			
			LOGGER.log(Level.SEVERE, ERRORE, e);
		
		}
		
		Evaluation eval;
		FilteredClassifier fc;
		Resample resample = new Resample();
		String[] opts;
		SpreadSubsample  spreadSubsample = new SpreadSubsample();
	
		try {
				/*NO SAMPLING, no filtered  classifier*/
				
				eval = new Evaluation(testing);
				
				eval.evaluateModel(cIBK, testing);
				samplingResult.add(setResult(IBK, NOSAMPLING, featureSelection, eval));
				
				eval.evaluateModel(randomForest, testing);
				samplingResult.add(setResult(RANDOMFOREST, NOSAMPLING, featureSelection, eval));
				
				eval.evaluateModel(naiveBayes, testing);
				samplingResult.add(setResult(NAIVEBAYES, NOSAMPLING, featureSelection, eval));
			
				/*Apply OVERSAMPLING*/
				
				eval = new Evaluation(testing);
				fc = new FilteredClassifier();
				resample.setInputFormat(training);
				opts = new String[]  {"-B", "1.0", "-Z", String.valueOf(2*percentClass*100)};
				resample.setOptions(opts);
				fc.setFilter(resample);
				
				/*Evaluation of IBK classifier*/
				
				classifier(training, testing, eval, fc, cIBK);
				samplingResult.add(setResult(IBK, OVERSAMPLING, featureSelection, eval));
				
				/*Evaluation of RandomForest classifier*/
			
				classifier(training, testing, eval, fc, randomForest);
				samplingResult.add(setResult(RANDOMFOREST, OVERSAMPLING, featureSelection, eval));
				
				/*Evaluation of NaiveBaye classifier*/
				
				classifier(training, testing, eval, fc, naiveBayes);
				samplingResult.add(setResult(NAIVEBAYES, OVERSAMPLING, featureSelection, eval));
				
				/*Apply UNDERSAMPLING*/
				
				eval = new Evaluation(testing);
				fc = new FilteredClassifier();
				spreadSubsample.setInputFormat(training);
				opts = new String[]{ "-M", "1.0"};
				spreadSubsample.setOptions(opts);
				fc.setFilter(spreadSubsample);
				
				/*Evaluation of IBK classifier*/
				
				classifier(training, testing, eval, fc, cIBK);
				samplingResult.add(setResult(IBK, UNDERSAMPLING, featureSelection, eval));
				
				/*Evaluation of RandomForest classifier*/
				
				classifier(training, testing, eval, fc, randomForest);
				samplingResult.add(setResult(RANDOMFOREST, UNDERSAMPLING, featureSelection, eval));
				
				/*Evaluation of NaiveBaye classifier*/

				classifier(training, testing, eval, fc, naiveBayes);				
				samplingResult.add(setResult(NAIVEBAYES, UNDERSAMPLING, featureSelection, eval));
				

				/*SMOTE*/
				
				eval = new Evaluation(testing);
				SMOTE smote = new SMOTE();
				fc = new FilteredClassifier();
				smote.setInputFormat(training);
				fc.setFilter(smote);
	
				/*Evaluation of IBK classifier*/
				
				classifier(training, testing, eval, fc, cIBK);
				samplingResult.add(setResult(IBK, SMOTE, featureSelection, eval));
			   
				/*Evaluation of RandomForest classifier*/
				
				classifier(training, testing, eval, fc, randomForest);
				samplingResult.add(setResult(RANDOMFOREST, SMOTE, featureSelection, eval));
				
				/*Evaluation of NaiveBaye classifier*/
				
				classifier(training, testing, eval, fc, naiveBayes);
				samplingResult.add(setResult(NAIVEBAYES, SMOTE, featureSelection, eval));
		
		}catch(Exception e) {
			
			LOGGER.log(Level.SEVERE, ERRORE, e);
		
		}

		
	   return samplingResult;
	
	}
	
	//Set the classifier to use
	
	public static Evaluation classifier(Instances training, Instances testing, Evaluation eval, FilteredClassifier fc, AbstractClassifier c){
	
		try {
			
			fc.setClassifier(c);
			
			fc.buildClassifier(training);
			
			eval.evaluateModel(fc, testing);
		
		}catch(Exception e) {
			
			LOGGER.log(Level.SEVERE, ERRORE, e);
		
		}
			
		return eval;
			
	}

	public static String setResult(String fc, String sampling, String featureSelection, Evaluation eval) {
		
		String result = null;
	
		result = fc + ";" + sampling + ";" + featureSelection + ";" + 
					eval.numTruePositives(1)  + ";" + eval.numFalsePositives(1)  + ";" + eval.numTrueNegatives(1)  + ";" + eval.numFalseNegatives(1)  + ";" +
					eval.precision(1)  + ";" + eval.recall(1)  + ";" + eval.areaUnderROC(1)  + ";" + eval.kappa() + "\n";
					
		return result;
		
	}

}
