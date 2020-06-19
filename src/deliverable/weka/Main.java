package deliverable.weka;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

import util.PropertiesUtils;
import util.ReadPropertyFile;

public class Main {
	
	private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
	
	static List <Integer> release = null;
	
	public static void main(String[] args) throws Exception {
		
		String project = PropertiesUtils.getProperty(ReadPropertyFile.PROJECT);
	
		//Read the csv and take the release number of the project
		
		try(BufferedReader br = new BufferedReader(new FileReader("result/" + project + "DatasetInfo.csv"))) {
			
			String line = br.readLine();
			
			while(line != null) {
			
				if(release.contains(Integer.parseInt(line.split(";")[0]))) {
					
					release.add(Integer.parseInt(line.split(";")[0]));
				}
			
			}
			
		}catch(Exception e) {
				
			LOGGER.log(Level.SEVERE, "[ERROR]", e);
			
		}
		
		for(int i = 0; i<release.size(); i++) {
			
			int releaseLimit = release.get(i);
			
			//For testing and traning get the number of buggy/no buggy 
			WalkForward.walkForwardTraning(project, releaseLimit);
			WalkForward.walkForwardTesting(project);
			
			// Create the ARFF file for the training, till the i-th version
			DataSource source2 = new DataSource(project + "Training.arrf");
			Instances testingNoFilter = source2.getDataSet();
			
			// Create the ARFF file for testing, with the i+1 version
			DataSource source = new DataSource(project + "Testing.arrf");
			Instances noFilterTraining = source.getDataSet();
			
			
			
			

		}
		
	}

}
