package utilities;

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
	          BufferedReader bufferedReader = new BufferedReader(fileReader);

	      		//initialise variables
	          boolean triple = false;
			  boolean doubleTriple = false;
	          boolean stateDouble = false;
	          boolean multiOptional = false;
	          String v_idea = "";
	          String subject = "";
	          String verb = "";
	          String object = 	"";
	          
	          while((line = bufferedReader.readLine()) != null) {
	        	  if(line.startsWith("%")||line.startsWith("@")){
	        		  
	        	  }
	        	  else{
	        		  //System.out.println(line);
	        		  /*
	        		  //if 
	        		  String words[] = line.split(" ");
	        		  
	        		  //capture states - doubles
	        		  
	        		  if(words.length==3 ){
	        			  v_idea =  words[0] ;
	        			  subject =   words[1];
	        			  verb =  words[2];
	        			  //important - set the object to null
	        			  object = "";
	        			  stateDouble=true;
	        		  }
	        		  //capture object as well - form triples
	        		  else if(words.length==4){
	        			  v_idea =  words[0] ;
	        			  subject =   words[1];
	        			  verb =  words[2];
	        			  object =  words[3];
	        			  triple =true;
	        		  }				
	        		  //capture the second set of triple, if more columns are provided in the inputfile
	        		  else if (words.length>5){
	        			  v_idea =  words[0] ;
	        			  subject =   words[1];
	        			  verb =  words[2];
	        			  object =  words[3];
	        			  //	second_subject = words[4];
	        			  //	second_verb = words[5];
	        			  //	second_object = words[6];
	        			  doubleTriple=true;
	        		  }
	        		  
	        		  String subjectArray[] = null;
	        		  String verbArray[]    = null;
	        		  String objectArray[]  = null;   
	        		  
	        		  //replce the underscore to seprate the combined terms
	        		  if (subject.contains("-")){
	        			  subjectArray = subject.split("-");		
	        			  multiOptional = true;
	        			  //split subject - make into as many as possible and insert in subject array
	        			  //use for loop to insert in array
	        		  }
	        		  else 
	        			  subjectArray[0] =subject;
	        		  
	        		  if (verb.contains("-")){
	        			  verbArray = verb.split("-");
	        			  multiOptional = true;
	        		  }
	        		  else 
	        			  verbArray[0] =verb;
	        		  
	        		  if (object.contains("-")){
	        			  objectArray = verb.split("-");	
	        			  multiOptional = true;
	        		  }
	        		  else 
	        			  objectArray[0] =object;
	        		  
	        		  if (multiOptional = true){
	        			  for (String sub: subjectArray){
	        				  for (String v: verbArray){
	        					  for (String obj: objectArray){
	        						  line = v_idea + " " + sub + " " + v + " " + obj;
	        						  temps.add(line);
			        			  }
		        			  }
	        			  }
	        		  }
	        		  else{
	        			  temps.add(line);
	        		  }
	        		  */
	        		  
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
