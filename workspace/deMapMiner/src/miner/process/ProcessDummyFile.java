package miner.process;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import connections.MysqlConnect;
//import wekaExamples.MyFilteredClassifier;
import callIELibraries.JavaOllieWrapperGUIInDev;
import readRepository.readMetadataFromWeb.GetProposalDetailsWebPage;
import callIELibraries.ClausIECaller;
import callIELibraries.ReVerbFindRelations;
import callIELibraries.UseDISCOSentenceSim;
import de.mpii.clause.driver.ClausIEMain;
import miner.processLabels.TripleProcessingResult;
import utilities.Utilities;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ProcessDummyFile {
	
	public ProcessDummyFile(){
		
	}
	
	/*
  public static void processDummyFile(Integer v_PEP, List<String> v_wordsList, ArrayList<Label> labels,ArrayList<Label> reasonLabels, String [] v_reasons,  
		  ClausIEMain cm, ReVerbFindRelations rr , JavaOllieWrapperGUIInDev jw,  UseDISCOSentenceSim ds, List<String> candidateSentencesList, //MyFilteredClassifier fc,
		  PythonSpecificMessageProcessing pm,  GetAllSentencesInMessage casm, String NLP_MODELS_PATH, Boolean includeStateData,Boolean checkReverb, 
		  EachSentenceToIELibrariesToFile cesdl, FileWriter writerForDisco, FileWriter writerAll, ProcessMessageAndSentence pms,
		  String[] conditionalList,String[] negationTerms, String[] reasonTerms, GetPEPDetails pd, String [] questionPhrases,
		  Boolean readDummyFile, Boolean replaceCommas, Boolean checkClauseIETrue, Boolean checkOllieTrue, ArrayList<TripleProcessingResult> tripleProcessingResult,
		  ArrayList<String> statusProcessingResult, String [] v_statesToGetRoles, FileInputStream fis, Boolean readEntireFileAsMessage, String DummyFileLoc,
		  String[] v_unwantedTerms, String[] doubles, String [] dontCheckLabels, String[] isolatedTerms, String[] allowLabelsAsQuestion) 
  */
	public static void processDummyFile(ProcessingRequiredParameters prp,boolean readEntireFileAsMessage,List<String> candidateSentencesList,String DummyFileLoc, FileInputStream fis)  throws IOException {
	  
	   Integer pepNumber = prp.getPEPNumber(), message_ID = null;    Integer counter = 0, dataFoundCounter = 0, discussionMessageCounter = 0, processedCounter = 0; ;
       String author = "", folder= "",file = "", md5 = "", wordsFoundList = "", v_inReplyTo = "", lineOutput = "", prevSentence = "", statusTo="", statusFrom="", subject ="";
       Date m_date = null;       
       Boolean discussionStart = null, discussionEnd = null;
    
	   //set sentencesRemoveDuplicates equal to null
	   candidateSentencesList = null;
	   
	   //compare message make sure no repeated messages are processed
	   //initialise hashcode array to null
	   //declared here is fine  - as one hashlist for each pep
	   List<Integer> hashList = new ArrayList<Integer>();   
	   List<String> md5List = new ArrayList<String>(), tempMessageList = new ArrayList<String>();
	   //UtilsPythonSpecificMessageProcessing pm = new UtilsPythonSpecificMessageProcessing();
	   Message m = new Message();
	   	   
	   //if read entire file as message
	   if(readEntireFileAsMessage)
	   {
		   System.out.println("readEntireFileAsMessage "); 
		   try {
				/*   
			   BufferedReader br=new BufferedReader(new FileReader("C://Users//psharma//Google Drive//PhDOtago//scripts//inputFiles//dummyNov.txt"));
				   String line = null;
				   String emailMessage = null;
				   while((line=br.readLine())!=null)
				   {
					   emailMessage = emailMessage + line + "\n";
				   }
				   */
			   
			   Scanner scanner = new Scanner(new FileReader(DummyFileLoc));
			   String emailMessage = "";
			    try {
			      //first use a Scanner to get each line
			      while ( scanner.hasNextLine() ){
			    	  emailMessage = emailMessage + scanner.nextLine()+ "\n";
			      }
			    }
			    finally {
			      //ensure the underlying stream is always closed
			      //this only has any effect if the item passed to the Scanner
			      //constructor implements Closeable (which it does in this case).
			      scanner.close();
			    }
		   
			
//			   String content = new String(Files.readAllBytes(Paths.get("C://Users//psharma//Google Drive//PhDOtago//scripts//inputFiles//dummyNov.txt")));
//			   System.out.println("content "+ content);
			    
			    m.setMessage(emailMessage);
			   //wordsFoundList = 
					   prp.casm.getAllSentencesInMessage(m,prp);	
		   }
		   catch (Exception e){
			   System.out.println("exception " + e.toString());
		   }
		   finally{
			   
		   }
	   }
	   else
	   {
		   //reading file line by line in Java using BufferedReader       
	       //  FileInputStream fis = null;
	       BufferedReader reader = null;	     
	       String Message = "";      
       
	       try {
	           //fis = new FileInputStream("C://scripts//inputFiles//dummyOctoo.txt");	//inputReasonSentences.txt
	           reader = new BufferedReader(new InputStreamReader(fis));         
	           System.out.println("Reading File line by line using BufferedReader");
	         
	           String line = reader.readLine();
	           System.out.println("File Contents:");
	           	           	           
	           //here is an option to read each line one by one treating each as different message
	           //or read entire file as message
	           
	           // read each line as different message
           
	           while(line != null)
	           {
	               System.out.println(line);
	               
	               //process here
	               line = prp.psmp.removeQuotedText(line);  //remove quoted text from email message
	               //System.out.println("After initial processing A " + line);
	               
	               //although the blank lines need to be inserted with fullstop to indicate end of sentence for nlp to recognise it as e o s	               
	               // the line below needs to be commented to seprate the paragraphs
	               line = prp.psmp.handleBlankLines(line); //handle blank lines - should start new sentence
	   	    	   
	               //System.out.println("After initial processing B " + line);
	               try {   
	            	   
	               	//System.out.println("here 00");
	               		// check entire message and check sentence.
	   					// STRING MATCHING
	   					// TEMP wordsFoundList = cm.checkEntireMessage (p, Message, wordsFoundList, author, statusFrom, statusTo ,v_PEP, m_date, subject, message_ID, pepNumber, counter ); //1. Check entire message.
	   					
	            	   //wordsFoundList = 
	            			   prp.casm.getAllSentencesInMessage(m,prp);
	   					// 2. Check sentences after Splitting message text into sentences
	   					// Note: for the moment we are just testing the above schemes over sentences only.
	   					// Try and output previous, current and next sentence.
	               }
	               catch (Exception e){ 
	               	
	               }	
	               line = reader.readLine();
	               //Message = line;
	           }           
         
	       } catch (FileNotFoundException ex) {
	    	   System.out.println("FileNotFoundException "+ex.toString());	          
	       } catch (IOException ex) {
	    	   System.out.println("IOException "+ex.toString());	 
	         
	       } finally {
	           try {
	               reader.close();
	               fis.close();
	           } catch (IOException ex) {
	        	   System.out.println("IOException "+ex.toString());	
	           }
	       }
	      
    	   dataFoundCounter=0;
    	   //if status change info found in message, dont contnue processing
    	   boolean StatusInfoFound = false; 
    	   processedCounter++; 
	   } //end else
      
   } //end function
	
} //end class
