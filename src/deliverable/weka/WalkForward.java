package deliverable.weka;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import java.util.logging.Level;
import java.util.logging.Logger;

public class WalkForward {

	private WalkForward() {}
	
	private static final Logger LOGGER = Logger.getLogger(WalkForward.class.getName());

	/* In walk-forward, the dataset is divided into parts
	 * 
	 *  The training part is execute only on the part tha precede the testing part.
	 *  
	 *  
	 *  
	 *  This function returns the result for the training part and get the number of buggy/no buggy */
	
	public static List<Integer> walkForwardTraining(String project, int release, String path) throws IOException{
		
		int countBuggy = 0;
		int count = 0;
		List<Integer> result = new ArrayList<>();
		
		BufferedReader br;
		
		//create file output
		
		String outname = project + "Training.arff";
		
		try(PrintStream printer = new PrintStream(new File(outname))){
			
			printer.append("@relation " + project + "\n");
			printer.append("@attribute Size numeric\n");
			printer.append("@attribute LocTouched numeric\n");
			printer.append("@attribute LocAdded numeric\n");
			printer.append("@attribute MaxLocAdded numeric\n");
			printer.append("@attribute AvgLocAdded numeric\n");
			printer.append("@attribute nAuth numeric\n");
			printer.append("@attribute nFix numeric\n");
			printer.append("@attribute nR numeric\n");
			printer.append("@attribute ChgSetSize numeric\n");
			printer.append("@attribute Buggy {Si, No}\n");
			printer.append("@data\n");
			
			String line = null;
			
			br = new BufferedReader(new FileReader(path));
			
			while((line =br.readLine()) != null) {
				
				if(line.split(";")[0] == null || line.split(";")[0].startsWith("Release")) {
					
					continue;
				}
				
				if(Integer.parseInt(line.split(";")[0]) <= release) {
					
					count++;
					
					countBuggy = countBuggy + addLine(printer, line);
					
				}
				
				
			}
			br.close();
			
			result.add(count);
			result.add(countBuggy);
			
			printer.flush();	
			
			
		} catch(Exception e) {
			
		LOGGER.log(Level.SEVERE, "[ERROR]", e);
		
		}
		
		return result;
		
	}
	
	/*This function returns the result for the testing part*/
	
	public static List<Integer> walkForwardTesting(String project, int release, String path) {
		
		int countBuggy = 0;
		int count = 0;
		List<Integer> result = new ArrayList<>();
		
		BufferedReader br;

		//create file output
	
		String outname = project + "Testing.arff";
						
		try(PrintStream printer2 = new PrintStream(new File(outname))){
							
			printer2.append("@relation " + project + "\n");
			printer2.append("@attribute Size numeric\n");
			printer2.append("@attribute LocTouched numeric\n");
			printer2.append("@attribute LocAdded numeric\n");
			printer2.append("@attribute MaxLocAdded numeric\n");
			printer2.append("@attribute AvgLocAdded numeric\n");
			printer2.append("@attribute nAuth numeric\n");
			printer2.append("@attribute nFix numeric\n");
			printer2.append("@attribute nR numeric\n");
			printer2.append("@attribute ChgSetSize numeric\n");
			printer2.append("@attribute Buggy {Si, No}\n");
			printer2.append("@data\n");
			
			String line = null;
			
			br = new BufferedReader(new FileReader(path));
			
			while((line =br.readLine()) != null) {
				
				if(line.split(";")[0] == null || line.split(";")[0].startsWith("Release")) {
					
					continue;
				}
				
				if(Integer.parseInt(line.split(";")[0]) == release) {
					
					count++;
					
					countBuggy = countBuggy + addLine(printer2, line);
					
				}
				
				
			}
			
			br.close();
			
			result.add(count);
			result.add(countBuggy);
			
			printer2.flush();	
		
		} catch(Exception e) {
					
			LOGGER.log(Level.SEVERE, "[ERROR]", e);
				
		}	
		
		return result;
		
	}
	
	
	public static int addLine(PrintStream file, String line) throws IOException {
		
		int countBuggy = 0;
		
		// Append the row readed from the CSV file, but without the first 2 column
		
		String[] array = line.split(";");
		
		for (int i = 2; i < array.length; i++) {
		
			if (i == array.length - 1) {
			
				if(array[i].equals("Si")) {
					countBuggy ++;
				}
			
				file.append(array[i] + "\n");
			
			} else {
				
				file.append(array[i] + ",");
			}
		}
		return countBuggy;
	}

}
