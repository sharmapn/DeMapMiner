package de.mpii.clause.driver;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReadFileLinesIntoArray {
	public static String [] readFromFile(String fileName) throws IOException {
		// The name of the file to open.
	      //String fileName = "C:/Users/admin/workspace/dev/src/semanticSimilarity/input-BaseIdeas.txt";

	      // This will reference one line at a time
	      String line = null;
	      
	      Integer lineReadInCounter = 0;
	      
	      List<String> temps = new ArrayList<String>();

	      try {
	          // FileReader reads text files in the default encoding.
	          FileReader fileReader = new FileReader(fileName);

	          // Always wrap FileReader in BufferedReader.
	          BufferedReader bufferedReader = 
	              new BufferedReader(fileReader);

	          while((line = bufferedReader.readLine()) != null) {
	        	  if(line.startsWith("%")||line.startsWith("@")){
	        		  
	        	  }
	        	  else{
	        		  //System.out.println(line);
	        		  temps.add(line);
	        		  lineReadInCounter++;
	        	  }
	          }   
	          
	          System.out.println("\nRead in " + lineReadInCounter + " lines into File " + fileName);
	          
	          String[] tempsArray = temps.toArray(new String[0]);

	          // Always close files.
	          bufferedReader.close(); 
	          return tempsArray;
	      }
	      catch(FileNotFoundException ex) {
	          System.out.println(
	              "Unable to open file '" + 
	              fileName + "'");                
	      }
	      catch(IOException ex) {
	          System.out.println(
	              "Error reading file '" 
	              + fileName + "'");                  
	          // Or we could just do this: 
	          // ex.printStackTrace();
	      }
		return null;
	      
	  }

}
