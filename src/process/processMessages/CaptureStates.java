package Process.processMessages;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import Process.processLabels.TripleProcessingResult;

public class CaptureStates {
	//we define what states we want to capture, most of the time we want to capture all of these states
	static String states [] = {"draft","open","active","pending", "closed","final","accepted","deferred","replaced","rejected","postponed","incomplete","superceded","withdrawn","withdrawal"};
	
	public static Boolean captureStates(String Message, Integer v_pepNumber, Date v_date,Timestamp v_dateTimeStamp, Integer v_message_ID, String author,String authorRole, FileWriter writerAll, 
			   FileWriter writerForDisco, ArrayList<String> statusProcessingResult, ProcessingRequiredParameters prp) throws IOException
	   {   
		   //set result object to return\
		   StatusResult sr = new StatusResult();
		   //check if state exists, 		   //if not add and show		   
		   //int counter =0;			   
		   //create a resulobject just to hold the many parameters, easier this way rather than sending all parameters
		   TripleProcessingResult ro = new TripleProcessingResult();		  
		   CheckInsertResult cir = new CheckInsertResult();		 //to insert result into db   
		   boolean stateFound = false, repeatedstate = false;		   
		   for (String s : states){	  
			   String text = "status : " + s;
		   	   if(Message.toLowerCase().contains(text))   	   {
		   		    stateFound = true;
			   		System.out.println("---------------------------------------------------------");					System.out.println("Sentence: " + "("+v_pepNumber+") " + Message );
					System.out.println("Status captured, now checking states: " + s);						
/*
					//checking if state is not repeated for that pep
					for (int y = 0; y < statusProcessingResult.size(); y++) {
						if (statusProcessingResult.get(y) != null) {
							System.out.println("\tStatus not null ");
							// currentSentenceUnique is only populated once triples are matched
							if (statusProcessingResult.get(y).toLowerCase().equals(text)) {
								repeatedstate = true;
								System.out.println("\t\tRepeated state: " + text);
								break;
								// System.out.println("\t\tReturning ");
								// if matched even once,, return true
								// ?? return repeatedstate;
								// System.out.println("Sentence and Idea FOUND in list " + CurrentSentenceString);
							} else {
								System.out.println("\t\tStatus repeated " + text);
							}
						} else {
							System.out.println("\t\tEither the sentence or label is null, or both");
						}
					}
					
					if(repeatedstate==true){
						
					}
					else{
*/
						System.out.println("\t\tInserting state result into file and database " + text);
						//addto arraylist
//						statusProcessingResult.add(text);
						//write data
//						writer.write ("\n"+  v_pepNumber + " | " + v_date+ " | " +  v_message_ID + " | " + author + " | " + s.toUpperCase() + " | " + CurrentSentenceString + " | R: " +  s.toUpperCase() + " | C: " +  s.toUpperCase() + " | O: "+s.toUpperCase());
//						writerForDisco.write("\n"+v_pepNumber + " | " + v_date+ " | " +  v_message_ID + " | " + author + " | " + s.toUpperCase() + " | " + Message + " | " +  s.toUpperCase() + " | " +  s.toUpperCase() + " | "+s.toUpperCase());
//						writerAll.write("\n"+v_pepNumber + " | " + v_date+ " | " +  v_message_ID + " | " + author + " | " + s.toUpperCase() + " | " + Message + " | " +  s.toUpperCase() + " | " +  s.toUpperCase() + " | "+s.toUpperCase());
						
						//populate result object
						ro.setStateData(v_pepNumber,v_date,v_dateTimeStamp,v_message_ID, author,authorRole, s.toUpperCase() ,Message,s.toUpperCase(), s.toUpperCase(),s.toUpperCase());
						ro.setLineNumber(1);   //for state data we just set line number to 1
						//insert into database
						//cir.connect();
						cir.insertResultStatesInDatabase(ro, false,prp);
//					}
					
				}
		   }		   
		   //add the list to the object being returned
		   //sr.statusProcessingResult = statusProcessingResult;
		   return stateFound;		
	   }
}
