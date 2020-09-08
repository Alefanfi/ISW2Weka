package deliverable.weka;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class WekaResult {
	
	private static final Logger LOGGER = Logger.getLogger(WekaResult.class.getName());
	
	static List<Integer> resultTraining;
	static List<Integer> resultTesting;
	
	private WekaResult() {}
	
	public static void printResult(String project, List<String> release, String path) {
		
		resultTraining = new ArrayList<>();
		resultTesting = new ArrayList<>();
		
		String outname = project + ".csv";
		
		//create the csv file 
		
		try(PrintStream printer = new PrintStream(new File(outname))){
			
			printer.println("Dataset; #Training; %Training; %DefectTraining; %DefectTesting; Classifier; Balancing; FeatureSelection; TP; FP; TN; FN; Precision; "
					+ "Recall; ROC Area; Kappa");
		
		for(int i = 1; i<release.size(); i++) {
			
				int releaseLimit = Integer.parseInt(release.get(i));
				
				/*For testing and traning get the number of buggy/no buggy and create the file arff with all the information we need */
				
				resultTraining = WalkForward.walkForwardTraining(project, releaseLimit, path);
				resultTesting = WalkForward.walkForwardTesting(project, releaseLimit, path);
				
				//compute the value of #Training, %Training, %DefectTraining, %DefectTesting,
				
				double trainingPerc = resultTraining.get(0)/(double)(resultTraining.get(0) + resultTesting.get(0));
				double defectiveTesting = resultTesting.get(1)/(double)resultTesting.get(0);
				double defectiveTraining = resultTraining.get(1)/(double)resultTraining.get(0);
				
				double percentClass = 1 - ( (resultTraining.get(1) + resultTesting.get(1)) / (double)(resultTraining.get(0) + resultTesting.get(0)));		
				
				// Create the ARFF file for testing, with the i+1 version
				
				DataSource source2 = new DataSource(project + "Testing.arff");
				Instances testingNoFilter = source2.getDataSet();
				
				// Create the ARFF file for the training, till the i-th version
				
				DataSource source = new DataSource(project + "Training.arff");
				Instances noFilterTraining = source.getDataSet();
				
				/* apply feature selection before the sampling*/
				
				List<String> featureSelectionResult = WekaFunction.applyFeatureSelection(noFilterTraining, testingNoFilter, percentClass);
				addResult(featureSelectionResult, trainingPerc, defectiveTesting, defectiveTraining, project, i, printer);
				
				/* apply only the sampling without the feature selection*/
				
				List<String> samplingResult = WekaFunction.applySampling(noFilterTraining, testingNoFilter, percentClass, "No");
				addResult(samplingResult, trainingPerc, defectiveTesting, defectiveTraining, project, i, printer);

			}
		
		}catch(Exception e) {
			
			LOGGER.log(Level.SEVERE, "[ERROR]", e);
		
		}
		
		LOGGER.info("Done!");
		
	}
	
	/* take the list of result of the function and build the raw that will be append to the csv file */

	private static void addResult(List<String> result, double trainingPerc, double defectiveTesting, double defectiveTraining, String project, int i, PrintStream printer) {
		
		for(int j = 0; j < result.size(); j++) {
			
			String newResult = project + ";" + i + ";" + trainingPerc + ";" + defectiveTraining + ";" + defectiveTesting + ";" + result.get(j);
			
			printer.append(newResult);
		}
		
		
	}

}
