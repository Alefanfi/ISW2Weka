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
	
	static String noSampling = "NO SAMPLING";
	
	static String overSampling = "OVERSAMPLING";
	
	static String underSampling = "UNDERSAMPLING";
	
	static String smote = "SMOTE";
	
	private WekaResult() {}
	
	public static void printResult(String project, List<String> release, String path) throws Exception {
		
		resultTraining = new ArrayList<>();
		resultTesting = new ArrayList<>();
		
		String outname = project + ".csv";
		
		try(PrintStream printer = new PrintStream(new File(outname))){
			
			printer.println("Dataset; #Training; %Training; %DefectTraining; %DefectTesting; Classifier; Balancing; FeatureSelection; TP; FP; TN; FN; Precision; "
					+ "Recall; ROC Area; Kappa");
		
		for(int i = 0; i<release.size(); i++) {
			
				int releaseLimit = Integer.parseInt(release.get(i));
				
				//For testing and traning get the number of buggy/no buggy 
				
				resultTraining = WalkForward.walkForwardTraining(project, releaseLimit, path);
				resultTesting = WalkForward.walkForwardTesting(project, releaseLimit, path);
				
				int totalData = resultTraining.get(0) + resultTesting.get(0);
				float trainingPerc = resultTraining.get(0)/(float)totalData;
				float defectiveTesting = resultTesting.get(1)/(float)resultTesting.get(0);
				float defectiveTraining = resultTraining.get(1)/(float)resultTraining.get(0);
				
				float percentClass = 1 - ( (resultTraining.get(1) + resultTesting.get(1)) / (float)(resultTraining.get(0) + resultTesting.get(0)));		
				
				// Create the ARFF file for the training, till the i-th version
				DataSource source2 = new DataSource(project + "Testing.arff");
				Instances testingNoFilter = source2.getDataSet();
				
				// Create the ARFF file for testing, with the i+1 version
				DataSource source = new DataSource(project + "Training.arff");
				Instances noFilterTraining = source.getDataSet();
				
				List<String> featureSelectionResultNoSampling = WekaFunction.applyFeatureSelection(noFilterTraining, testingNoFilter, percentClass, noSampling);
				addResult(featureSelectionResultNoSampling, trainingPerc, defectiveTesting, defectiveTraining, project, i, printer);
				
				List<String> featureSelectionResultOverSampling = WekaFunction.applyFeatureSelection(noFilterTraining, testingNoFilter, percentClass, overSampling);
				addResult(featureSelectionResultOverSampling, trainingPerc, defectiveTesting, defectiveTraining, project, i, printer);
				
				List<String> featureSelectionResultUnderSampling = WekaFunction.applyFeatureSelection(noFilterTraining, testingNoFilter, percentClass, underSampling);
				addResult(featureSelectionResultUnderSampling, trainingPerc, defectiveTesting, defectiveTraining, project, i, printer);
				
				List<String> featureSelectionResultSmote = WekaFunction.applyFeatureSelection(noFilterTraining, testingNoFilter, percentClass, smote);
				addResult(featureSelectionResultSmote, trainingPerc, defectiveTesting, defectiveTraining, project, i, printer);
				
				List<String> noSamplingResult = WekaFunction.applySampling(noFilterTraining, testingNoFilter, percentClass, noSampling, "No");
				addResult(noSamplingResult, trainingPerc, defectiveTesting, defectiveTraining, project, i, printer);
				
				List<String> overSamplingResult = WekaFunction.applySampling(noFilterTraining, testingNoFilter, percentClass, overSampling, "No");
				addResult(overSamplingResult, trainingPerc, defectiveTesting, defectiveTraining, project, i, printer);
				
				List<String> underSamplingResult = WekaFunction.applySampling(noFilterTraining, testingNoFilter, percentClass, underSampling, "No");
				addResult(underSamplingResult, trainingPerc, defectiveTesting, defectiveTraining, project, i, printer);
				
				List<String> smoteResult = WekaFunction.applySampling(noFilterTraining, testingNoFilter, percentClass, smote, "No");
				addResult(smoteResult, trainingPerc, defectiveTesting, defectiveTraining, project, i, printer);

			}
		}catch(Exception e) {
			
			LOGGER.log(Level.SEVERE, "[ERROR]", e);
		
		}
		
	}

	private static void addResult(List<String> result, float trainingPerc, float defectiveTesting, float defectiveTraining, String project, int i, PrintStream printer) {
		
		for(int j = 0; j < result.size(); j++) {
			
			String newResult = project + ";" + i + ";" + trainingPerc + ";" + defectiveTraining + ";" + defectiveTesting + ";" + result.get(j);
			
			printer.append(newResult);
		}
		
		
	}

}
