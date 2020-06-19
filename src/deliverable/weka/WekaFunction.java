package deliverable.weka;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import weka.core.Instances;
import weka.attributeSelection.CfsSubsetEval;
import weka.attributeSelection.GreedyStepwise;

import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;


public final class WekaFunction {
	
	private WekaFunction() {}
	
	private static final Logger LOGGER = Logger.getLogger(WekaFunction.class.getName());
	
	public static List<String> applyFeatureSelection(Instances filterTraining, Instances filterTesting) {
		
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
			filter.setInputFormat(filterTraining);
			
			//apply
			Instances filteredTraining = Filter.useFilter(filterTraining, filter);
			
			Instances testingFiltered = Filter.useFilter(filterTesting, filter);
			
			int numAttrFiltered = filteredTraining.numAttributes();
			filteredTraining.setClassIndex(numAttrFiltered - 1);
			testingFiltered.setClassIndex(numAttrFiltered - 1);
			
			System.out.println("Filtered attr: "+ numAttrFiltered);
		
		}catch(Exception e) {
			
			LOGGER.log(Level.SEVERE, "[ERROR]", e);
		
		}
		
		return null;

	}

}
