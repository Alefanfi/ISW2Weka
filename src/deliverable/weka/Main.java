package deliverable.weka;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;


import util.PropertiesUtils;
import util.ReadPropertyFile;

public class Main {
	
	private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
	
	static List <String> release = new ArrayList<>();
	
	public static void main(String[] args) throws IOException {
		
		String project = PropertiesUtils.getProperty(ReadPropertyFile.PROJECT);
		
		String pathFile = PropertiesUtils.getProperty(ReadPropertyFile.PATH);
	
		//Read the csv and take the release number of the project
		
		try(Scanner sc = new Scanner(new File(pathFile))){
				
			sc.useDelimiter("\n");
			
			while(sc.hasNext()) {
				
				if(!release.contains(sc.next().split(";")[0])) {
					
					release.add(sc.next().split(";")[0]);
				}
			}
			
		}catch(Exception e) {
				
			LOGGER.log(Level.SEVERE, "[ERROR]", e);
			
		}
		
		WekaResult.printResult(project, release, pathFile);
		
	}

}
