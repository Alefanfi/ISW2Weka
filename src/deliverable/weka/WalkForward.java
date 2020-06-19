package deliverable.weka;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WalkForward {

	private WalkForward() {}
	
	private static final Logger LOGGER = Logger.getLogger(WalkForward.class.getName());
	
	private static BufferedReader bufferedReader;

	/* In walk-forward, the dataset is divided into parts
	 * 
	 *  The training part is execute only on the part tha precede the testing part.
	 *  
	 *  
	 *  
	 *  This function returns the result for the training part and get the number of buggy/no buggy */
	
	public static List<Integer> walkForwardTraning(String project, int release) throws IOException{
		
		int countBuggy = 0;
		int count = 0;
		List<Integer> result = null;
		
		//create file output
		
		String outname = project + "Training.arrf";
		
		try(PrintStream printer = new PrintStream(new File(outname))){
			
			printer.println("Dataset, #Training, %Training, %Defect Training, %Defect Testing, Classifier, Balancing, FeatureSelection, TP, FP, TN, FN, Precision, "
					+ "Recall, ROC Area, Kappa");
			
			printer.append("@relation " + project + "\n\n");
			printer.append("@attribute Size numeric\n");
			printer.append("@attribute LocTouched numeric\n");
			printer.append("@attribute LocAdded numeric\n");
			printer.append("@attribute MaxLocAdded numeric\n");
			printer.append("@attribute AvgLocAdded numeric\n");
			printer.append("@attribute nAuth numeric\n");
			printer.append("@attribute nFix numeric\n");
			printer.append("@attribute nR numeric\n");
			printer.append("@attribute ChgSetSize numeric\n");
			printer.append("@attribute Buggy {Yes, No}\n\n");
			printer.append("@data\n");
			
			bufferedReader = new BufferedReader(new FileReader("result/" + project + "Training.arrf"));
			
			String line = bufferedReader.readLine();
			
			while(line != null) {
				
				if(Integer.parseInt(line.split(";")[0]) <= release) {
				
					count++;
					
					if(line.contains("Yes")) {
					
						countBuggy ++;
						
						//Add line for a buggy
						
						printer.append(line);
					}
					
				}
			
			}
			
			result.add(count);
			result.add(countBuggy);
			
			printer.flush();	
			
		} catch(Exception e) {
			
		LOGGER.log(Level.SEVERE, "[ERROR]", e);
		
		}
		
		return result;
		
	}
	
	/*This function returns the result for the testing part*/
	
	public static void walkForwardTesting(String project) {
		
		//create file output
	
		String outname = project + "Training.arrf";
						
		try(PrintStream printer = new PrintStream(new File(outname))){
							
			printer.println("Dataset, #Training, %Training, %Defect Training, %Defect Testing, Classifier, Balancing, FeatureSelection, TP, FP, TN, FN, Precision, "
					+ "Recall, ROC Area, Kappa");
							
			printer.append("@relation " + project + "\n\n");
			printer.append("@attribute Size numeric\n");
			printer.append("@attribute LocTouched numeric\n");
			printer.append("@attribute LocAdded numeric\n");
			printer.append("@attribute MaxLocAdded numeric\n");
			printer.append("@attribute AvgLocAdded numeric\n");
			printer.append("@attribute nAuth numeric\n");
			printer.append("@attribute nFix numeric\n");
			printer.append("@attribute nR numeric\n");
			printer.append("@attribute ChgSetSize numeric\n");
			printer.append("@attribute Buggy {Yes, No}\n\n");
			printer.append("@data\n");
					
					
					
					
					
					
					
					
					
				
		} catch(Exception e) {
					
			LOGGER.log(Level.SEVERE, "[ERROR]", e);
				
		}		
		
	}


}
