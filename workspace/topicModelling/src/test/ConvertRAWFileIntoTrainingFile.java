package test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import java.io.*;
//reads the input files for training
//extracts the main sentences
// puts them into Mallet reader format
public class ConvertRAWFileIntoTrainingFile {
	
	static FileWriter fw = null, fw2 =null;
	static StanfordParser sp = null;
	
	public static final char OPEN_CHAR = '[';
	public static final char CLOSE_CHAR = ']';
	public static final char CAUSE_SHORTHAND = 'C';
	public static final char EFFECT_SHORTHAND = 'E';
	
	public static final String CAUSE_PHRASE = "CP";	
	public static final String EFFECT_PHRASE = "EP";			
	public static final String END_SENT_PUNC = ".!?;";	
	public static final String MISC_PUNC = ".?!:;-()[]'\"/,";	
	public static final String INTRA_SENT_PUNC = ",:";
	public static final String NOUN_REGEXP = "(nn|nns|np)";	
	public static final String EXTENDED_NOUN_REGEXP = "(nn\\$|nns\\$|np\\$)";	
//	public static final String CAUSE_TAG = "<CAUSE>";	
//	public static final String EFFECT_TAG = "<EFFECT>";
	
	public static final String SENT_DELIM =  ". .";  //"_END_OF_SENTENCE_ N";
	public static final String SENT_DELIM_REDUX = "_END_OF_SENTENCE_";
	public static final String CAUSE_TAG = "C";
	public static final String CAUSE_BEGIN_TAG = "CB";
	public static final String CAUSE_INTERMEDIATE_TAG = "CI";
	public static final String CAUSE_END_TAG = "CE";
	
	public static final String EFFECT_TAG = "E";
	public static final String EFFECT_BEGIN_TAG = "EB";
	public static final String EFFECT_INTERMEDIATE_TAG = "EI";
	public static final String EFFECT_END_TAG = "EE";
	
	public static final String RELN_TAG = "R";
	public static final String RELN_BEGIN_TAG = "RB";
	public static final String RELN_INTERMEDIATE_TAG = "RI";
	public static final String RELN_END_TAG = "RE";
	
	public static final String NEITHER_TAG = "N";	
	public static final String CAUSE_REGEXP = "(" + CAUSE_TAG + "|" + CAUSE_BEGIN_TAG + "|" + CAUSE_INTERMEDIATE_TAG + "|" + CAUSE_END_TAG + ")";	
	public static final String EFFECT_REGEXP = "(" + EFFECT_TAG + "|" + EFFECT_BEGIN_TAG + 	"|" + EFFECT_INTERMEDIATE_TAG + "|" + EFFECT_END_TAG + ")";
	
	static String outFileNameMain = "C:\\CRF++\\example\\reasons\\TrainingDataForReasons.txt";
	static Integer trainingColumnCount = 2;
	
	static boolean includeTermOnly = true, includeTermAndPOS = false, includePOSOnly =false;
	
	public static void main(String[] args) {
//		String labelsRAWFilename	= "C:\\Users\\psharma\\Google Drive\\PhDOtago\\Code\\inputFiles\\reason\\input-ReasonSentencesTaggedNewLines.txt";	//exactly same as the one in google drive, but modified as well
//		String labelsInputFilename	= "C:\\Users\\psharma\\Google Drive\\PhDOtago\\Code\\inputFiles\\reason\\input-ReasonSentencesTaggedNewLinesCRFInput.txt";
		//testing with one sentence
		String inFileName= 	"C:\\CRF++\\example\\reasons\\inputReasonSentences.txt";	//RAW File would be changed to CRF format removing all comments, line with multiple sentences and parsing each term in sentences 
		String outFileName= "C:\\CRF++\\example\\reasons\\POSOnly.txt";
		
		//initialise stanford parer		
		sp = new StanfordParser();
		sp.init();
		
		//we start with a RAW file with comments and C,E tags
		try {
			fw = new FileWriter(outFileName,true); //labelsInputFilename //the true will append the new data
			fw2 = new FileWriter(outFileNameMain,true);
			// we also dont read in lines which have multiple sentences...they are commented by ##
			readLabelsFromFile_parser_writeToFile(inFileName,true); //labelsRAWFilename
			fw.close();
			fw2.close();
			 
			//we want to make sure the outputfile has 3 or n number of columns and also no double spaces
			 String line = "";
			 Integer lineCounter=0, columnCount=0;
			 
			 System.out.println("Checking number of columns in File");
			 FileReader fileReader = new FileReader(outFileNameMain);		            // Always wrap FileReader in BufferedReader.
		            BufferedReader bufferedReader =   new BufferedReader(fileReader);
		            while((line = bufferedReader.readLine()) != null) {
		            	lineCounter++;
		            	columnCount = line.split(" ").length;
		            	if (columnCount != trainingColumnCount) {
		            		System.out.println("Error in column count in line: "+ lineCounter+ " Line: " + line);
		            	}
		            }   
		            // Always close files.
		            bufferedReader.close();  
		}
		catch(IOException ioe)
		{
		    System.err.println("IOException: " + ioe.getMessage());
		} 
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//reads into database
	public static void readLabelsFromFile_parser_writeToFile(String v_filename, boolean output) {
		// The name of the file to open.
	      String fileName = v_filename, line = ""; // This will reference one line at a time
	      Integer lineReadInCounter = 0;
	      List<String> temps = new ArrayList<String>(),emptyLinesNotRead = new ArrayList<String>(),comments = new ArrayList<String>();
	      String sentence="";
	      
	      try {
	    	  // FileReader reads text files in the default encoding.
	          FileReader fileReader = new FileReader(fileName);
	          // Always wrap FileReader in BufferedReader.
	          BufferedReader bufferedReader =  new BufferedReader(fileReader);
	          //Integer lineReadInCounter = 0;
	          Integer emptyLineCounter =0, lineNumber=0;
	          	          
	          while((line = bufferedReader.readLine()) != null) {
	        	 // line= line.trim();
	        	  line = line.trim().replaceAll("\\s+", " ");
	      		  line = line.trim().replaceAll(" +", " ");
	        	  lineNumber++;
	        	  
	        	  if(line.startsWith("%")||line.startsWith("@") || line.startsWith("#")){
	        		  comments.add("Line: "+lineNumber + " comments = [" + line+ "]");
	        		  //parameters of below function(Integer lineNumber,String comments, String idea,String subject,String verb, String object, Connection conn
	        		  //dont need comments in table
	        		  //insertLabelsInDB(lineNumber,line,"","", "", "",conn);	//insert comments	//1,2,-1,"k"
	        	  }
	        	  else if (line.trim().length()<2 || line.isEmpty() || line== null || line.equals(""))	//empty spaces, 2 is just a small number, could have been 1 , and highly unlikely triple or double can be coded in 2 characters 
	        	  {
	        		  emptyLinesNotRead.add("Line: "+lineNumber + " string = [" + line+"]");
	        		  emptyLineCounter++;
	        		  //System.out.println("Empty or almost empty Line not read, linenumber "+lineNumber); 
	        	  }
	        	  else{
	        		  sentence="";
	        		  //lines in inputfile contains senetences from which the triple was extracted - we leave them out
	        		  if (line.contains("$")) {
	        			  sentence = line.split("\\$")[1];
	        			  line 	   = line.split("\\$")[0]; 			  line     = line.trim();
	        			  sentence=sentence.trim();
	        		  }
		        	  else{
		        		  sentence = line.trim();
		        	  }// end else
	        		  
	        		  //if line contains multiple sentences	        		  
        			  sentence=line.trim();
        			  System.out.println(lineNumber+ ", " + sentence);
        			  
        			  //we get rid of the []C brackets and tag before parsing sentence
        			  /*
        			  String[] segs = line.split(" ");
        			  for (int i = segs.length-1; i >= 0; i--) {
        				  String seg = segs[i];
        				  System.out.println(line);
        				  
        				  
        			  } */
        			  String tmpsentence = sentence.replaceAll("\\[", "").replaceAll("\\]C","").replaceAll("\\]E","").replaceAll("\\]R","");
        			  
        			  //parse the sentence
        			  ArrayList<String> parsedSentence =new ArrayList<String>(); 
        			  parsedSentence = sp.parseSentence(tmpsentence,includeTermOnly,includeTermAndPOS, includePOSOnly); 
        			  //reverse
        			  ArrayList<String> reverseParsedSentence =new ArrayList<String>(); 
        			  reverseParsedSentence =parsedSentence;
        			  //Collections.reverse(reverseParsedSentence);
        			  
        			  //append into file
        			  for(String sent : parsedSentence) {
 //$$      				System.out.println(sent);
        			  	fw.write(sent +"\n");	//appends the string to the file 
        			  }        			 
        			  
        			  //send the tagged sentence with the reverse of parsed sentence
        			  processLine(line,reverseParsedSentence);
        			  
	        	  } //end else
	          }
	          System.out.println("\nRead in " + lineReadInCounter + " labels into File " + fileName + " for " );
	          String[] tempsArray = temps.toArray(new String[0]);
//    		  System.out.println("Empty Lines not read: "+emptyLineCounter);
//	    		  for (String lnr :  emptyLinesNotRead){
//	    			  System.out.println(lnr);
//	    		  }
//    		  System.out.println("Comment Lines not read below: "+comments.size());
//	    		  for (String lnr :  comments){
//	    			  System.out.println(lnr);
//	    		  }
	          // Always close files.
	          bufferedReader.close();
	          fw.close();
	          //return sentence;
	      }
	      catch(FileNotFoundException ex) {
	          System.out.println("Unable to open file '" +  fileName + "'");                
	      }
	      catch(IOException ex) {
	          System.out.println("Error reading file '"  + fileName + "'");                  
	          // Or we could just do this: 
	          // ex.printStackTrace();
	      }
	      catch(Exception e) {
	    	  System.out.println(StackTraceToString(e)  );
	      }
		//return null;	      
	  }
	
	public static void processLine (String line,ArrayList<String> reverseParsedSentence) {
		boolean inCause = false;
		boolean inEffect = false;
		String[] segs = line.split(" ");
		String pos = "";
		ArrayList<String> reverse = new ArrayList<String>();
		for (int i = segs.length-1; i >= 0; i--) {
			pos = reverseParsedSentence.get(i);
			String seg = segs[i];
//			System.out.println(line);
			if (seg.charAt(0) == OPEN_CHAR && seg.charAt(seg.length()-2) == CLOSE_CHAR) {
				if(includeTermOnly)
					pos="";
				else
					pos = reverseParsedSentence.get(i);
				
				reverse.add(seg.substring(1, seg.length()-2) + " " + pos +  " "+ getCRFLabel(seg.charAt(seg.length()-1),true,false,false));
				System.out.println("\t\t"+seg.substring(1, seg.length()-2) + " " + pos +  " "+ getCRFLabel(seg.charAt(seg.length()-1),true,false,false));
			} else if (seg.charAt(0) == OPEN_CHAR) {
				if(includeTermOnly)
					pos="";
				else
					pos = reverseParsedSentence.get(i);
				
				String l = "";
				l += seg.substring(1) + " " + pos +  " ";
				if (inCause)
					l += getCRFLabel(CAUSE_SHORTHAND,false,false,true);
				else if (inEffect)
					l += getCRFLabel(EFFECT_SHORTHAND,false,false,true);
				inCause = false; inEffect = false;
				reverse.add(l);
				System.out.println("\t\t"+l);
			} else if (seg.length() > 2 && seg.charAt(seg.length()-2) == CLOSE_CHAR) {
				if(includeTermOnly)
					pos="";
				else
					pos = reverseParsedSentence.get(i);
				
				if (seg.charAt(seg.length()-1) == CAUSE_SHORTHAND)
					inCause = true;
				else if (seg.charAt(seg.length()-1) == EFFECT_SHORTHAND)
					inEffect = true;
				reverse.add(seg.substring(0, seg.length()-2) + " " + pos +  " " + getCRFLabel(seg.charAt(seg.length()-1),false,true,false));
				System.out.println("\t\t"+seg.substring(0, seg.length()-2) + " " + pos +  " " + getCRFLabel(seg.charAt(seg.length()-1),false,true,false));
			} else if (inCause) {
				if(includeTermOnly)
					pos="";
				else
					pos = reverseParsedSentence.get(i);
				
				reverse.add(seg + " " + pos +  " " + getCRFLabel(CAUSE_SHORTHAND,false,false,false));
				System.out.println("\t\t"+ seg + " " + pos +  " " + getCRFLabel(CAUSE_SHORTHAND,false,false,false));
			} else if (inEffect) {
				if(includeTermOnly)
					pos="";
				else
					pos = reverseParsedSentence.get(i);
				
				reverse.add(seg + " " + pos +  " " + getCRFLabel(EFFECT_SHORTHAND, false,false,false));
				System.out.println("\t\t"+ seg + " " + pos +  " " + getCRFLabel(EFFECT_SHORTHAND, false,false,false));
			} else {
				if(includeTermOnly)
					pos="";
				else
					pos = reverseParsedSentence.get(i);

				reverse.add(seg + " " + pos +  " " + getCRFLabel('N',false,false,false));
				System.out.println("\t\t"+ seg + " " + pos +  " " + getCRFLabel('N',false,false,false));
			}
		}
		try {
			String lineToWrite="";
			for (int i = reverse.size() -1; i >= 0; i--) {
				lineToWrite = reverse.get(i).trim().replaceAll(" +", " ");
				fw2.write(lineToWrite +"\n");
			}
			fw2.write(SENT_DELIM +"\n");
		}catch (Exception e) {
			
		}
	}
	
	/**
	 * Translates the shorthand label [storm]C into our CRF label
	 * such as CI.
	 */
	public static String getCRFLabel (char shorthand_label, boolean singular, boolean end, boolean begin) {
		if (shorthand_label == CAUSE_SHORTHAND) {
			if (singular)
				return CAUSE_TAG;
			if (begin)
				return CAUSE_BEGIN_TAG;
			if (end)
				return CAUSE_END_TAG;
			return CAUSE_INTERMEDIATE_TAG;
		}
		
		if (shorthand_label == EFFECT_SHORTHAND) {
			if (singular)
				return EFFECT_TAG;
			if (begin)
				return EFFECT_BEGIN_TAG;
			if (end)
				return EFFECT_END_TAG;
			return EFFECT_INTERMEDIATE_TAG;
		}
		
		return NEITHER_TAG;
	}
	
	public static String StackTraceToString(Exception ex) {
		String result = ex.toString() + "\n";
		StackTraceElement[] trace = ex.getStackTrace();
		for (int i=0;i<trace.length;i++) {
			result += trace[i].toString() + "\n";
		}
		return result;
	}
}
