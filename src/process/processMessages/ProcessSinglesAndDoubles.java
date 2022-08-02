package Process.processMessages;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import Process.processLabels.CheckAndGetReasons;
import Process.processLabels.TripleProcessingResult;
import de.mpii.clause.driver.ClausIEMain;
import de.mpii.clause.driver.Reasons;

//HERE WE HAVE FOUND THE SINGLES/DOUBLES
//HERE WE WANT TO FIND THE REASON before the record can be inserted

public class ProcessSinglesAndDoubles {
	public static boolean processSinglesAndDoubles(String entireParagraph, String LabelOfSingledoubleMatchedTerm, String prevSentence, String CurrentSentenceString, String nextSentence, 
			Integer v_pepNumber, Date v_date,Timestamp v_datetimestamp, Integer v_message_ID, String author,String authorsRole, FileWriter writerAll, FileWriter writerForDisco, String previousParagraph, String nextParagraph,  
			String [] v_reasonIdentifierTerms, ClausIEMain cie,String v_actualReasons[], ArrayList<LabelTriples> reasonLabels, ProcessingRequiredParameters prp) throws IOException{
		
   boolean stateFound = false;  
   boolean reasonFound=false;
   if (previousParagraph.isEmpty() || previousParagraph==null) {}
   else{ 
	   if(previousParagraph.contains("\n"))
		   previousParagraph = previousParagraph.replace("\n", " ");
   }
   if (entireParagraph.isEmpty() || entireParagraph==null) {}
   else{
	   if(entireParagraph.contains("\n"))
		   entireParagraph = entireParagraph.replace("\n", " ");
   }
   if (nextParagraph.isEmpty() || nextParagraph==null){}
   else{
	   if(nextParagraph.contains("\n"))
		   nextParagraph = nextParagraph.replace("\n", " ");
   }
   /* jan 2019, commented this as we are not checking reasons so maybe wont need to see this- also this adds extra maybe not useful output
   System.out.println("\tINSIDE processDoubles() try to capture doubles and the reasons");		   
   System.out.println("\tOrigCurrntSentenceStr: ("  + CurrentSentenceString + ")");
   System.out.println("\tnextSentence: 			("  + nextSentence	+ ")");
   System.out.println("\tpreviousParagraph:		(" +  previousParagraph	+ ")");
   System.out.println("\tentireParagraph: 		(" +  entireParagraph	+ ")");
   System.out.println("\tnextParagraph: 		(" +  nextParagraph	+ ")");
   
   CheckAndGetReasons cgr = new CheckAndGetReasons();
   */
   TripleProcessingResult resultObject =  new TripleProcessingResult();
   CheckInsertResult cir = new CheckInsertResult();	
   //Reasons reason = new Reasons();
   
   //TRY GET THE REASON FROM NEARBY SENTENCES AND PARAGRAPHS - store the result here
   //jan 2019 commented reason checking as for triples
   /*System.out.println("\tChecking Reasons for Doubles: ");
   
   reasonFound = cgr.checkAndGetReasonsForDoubles(resultObject, entireParagraph, CurrentSentenceString, prevSentence, nextSentence, previousParagraph, nextParagraph, v_actualReasons, cie, reasonLabels,prp);	   
  
   //if reason exists, assign it to resultObject
   //if (reason.getIfReasonExists() ==true){
   if(reasonFound){
	   System.out.println("\t########## FOUND REASON FOR SINGLES AND DOUBLES OUTPUTTING()"  +  resultObject.getReason(2));
	   //resultObject.setReason(reason.getReason(), 2);
   } else{ }
   */
   resultObject.setDataDoubles(v_pepNumber, author,authorsRole, v_date,v_datetimestamp, v_message_ID, LabelOfSingledoubleMatchedTerm, CurrentSentenceString,"", LabelOfSingledoubleMatchedTerm, "", false, "",
		   "", prevSentence, nextSentence, previousParagraph, entireParagraph,    nextParagraph);	 //reason.getReason()					   
   //insert result object to database
   try{
	   // cir.connect();
	   cir.insertDoubleResultRecordInDatabase(resultObject,false);		//after finding reason, insert the single or double (with reason) in results table	   	
	   //System.out.println("\t INSERTED IN RESULTS DB");
   }
   catch (Exception e){
	   System.out.println("\t EXCEPTION INSERTED IN RESULTS DB" + e.toString());
   }
   //cut code from here and pasted below
  
   return stateFound;		
	}
	   /*
	   String states2[] = {"draft","open","active","pending","closed","final","accepted","deferred","replaced","rejected","postponed","incomplete","superceded"};		
	   	   
	   //v_role
	   System.out.println("\tChecking States: ");
	   //check states in next and previous sentences and paragraphs
	   for (String s : states2) {	
		   System.out.println("\t here 4");
		 //  System.out.println("\t checking state " +s);
		     if (CurrentSentenceString!=null){
		    	 //System.out.println("\t a ");
			     if( CurrentSentenceString.toLowerCase().contains(s)){
		   		    stateFound = true;
		   		    writerForDisco.write("\n"+v_pepNumber + " | " + v_date+ " | " +  v_message_ID + " | " + author + " | " + s + " | " + CurrentSentenceString + " | |" +  s + " "+ doubleLabel + " | ");
					writerAll.write     ("\n"+v_pepNumber + " | " + v_date+ " | " +  v_message_ID + " | " + author + " | " + s + " | " + CurrentSentenceString + " | |" +  s + " "+ doubleLabel + " | ");   
					String finalLabel = doubleLabel+"_"+s;
					
					resultObject.setData(v_pepNumber, author, v_date, finalLabel, CurrentSentenceString, null, finalLabel, null, false, "", reason.getReason(), prevSentence, nextSentence, previousParagraph, entireParagraph, nextParagraph);						   
					resultObject.setMID(v_message_ID);
					cir.connect();
					cir.insertDoubleResultRecordInDatabase(resultObject,false);
					System.out.println("\tState " + s+ " found in CurrentSentenceString: (" + CurrentSentenceString + ")");					
			  	 }
		     }
		     if (nextSentence!=null){
		    	// System.out.println("\t b ");
			     if( nextSentence.toLowerCase().contains(s)){
			    	 stateFound = true;
			    	 System.out.println("\tState " + s+ " found in nextSentence: (" + nextSentence+ ")");	
			    	 writerForDisco.write("\n"+v_pepNumber + " | " + v_date+ " | " +  v_message_ID + " | " + author + " | " + s + " | " + CurrentSentenceString + " | |" +  s + " "+ doubleLabel + " | ");
					 writerAll.write     ("\n"+v_pepNumber + " | " + v_date+ " | " +  v_message_ID + " | " + author + " | " + s + " | " + CurrentSentenceString + " | |" +  s + " "+ doubleLabel + " | ");   
					 String finalLabel = doubleLabel+"_"+s;	
					 resultObject.setMID(v_message_ID);
					 resultObject.setData(v_pepNumber, author, v_date, finalLabel, CurrentSentenceString, null, finalLabel, null, false, "", reason.getReason(), prevSentence, nextSentence, previousParagraph, entireParagraph, nextParagraph);
					 cir.connect();
					 cir.insertDoubleResultRecordInDatabase(resultObject,false);
			     }
		     }
		     if (prevSentence!=null){
			    	// System.out.println("\t b ");
				     if( prevSentence.toLowerCase().contains(s)){
				    	 stateFound = true;
				    	 System.out.println("\tState " + s+ " found in prevSentence: (" + prevSentence+ ")");	
				    	 writerForDisco.write("\n"+v_pepNumber + " | " + v_date+ " | " +  v_message_ID + " | " + author + " | " + s + " | " + CurrentSentenceString + " | |" +  s + " "+ doubleLabel + " | ");
						 writerAll.write     ("\n"+v_pepNumber + " | " + v_date+ " | " +  v_message_ID + " | " + author + " | " + s + " | " + CurrentSentenceString + " | |" +  s + " "+ doubleLabel + " | ");   
						 String finalLabel = doubleLabel+"_"+s;	
						 resultObject.setData(v_pepNumber, author, v_date, finalLabel, CurrentSentenceString, null, finalLabel, null, false, "", reason.getReason(), prevSentence, nextSentence, previousParagraph, entireParagraph, nextParagraph);
						 resultObject.setMID(v_message_ID);
						 cir.connect();
						 cir.insertDoubleResultRecordInDatabase(resultObject,false);
				     }
			     }
		     if (nextParagraph !=null){
		    	// System.out.println("\t c s "+ s + " " + nextParagraph.toLowerCase());
			  	 if( nextParagraph.toLowerCase().contains(s.toLowerCase())){
		   		    stateFound = true;
			   		System.out.println("\tState " + s+ " found in nextParagraph");	
			   		writerForDisco.write("\n"+v_pepNumber + " | " + v_date+ " | " +  v_message_ID + " | " + author + " | " + s + " | " + CurrentSentenceString + " | |" +  s + " " + doubleLabel + " | ");
					writerAll.write     ("\n"+v_pepNumber + " | " + v_date+ " | " +  v_message_ID + " | " + author + " | " + s + " | " + CurrentSentenceString + " | |" +  s + " " + doubleLabel + " | ");   
					//System.out.println("here aa!");
					String finalLabel = doubleLabel+"_"+s;
					//System.out.println("here acccc!");//reverb  //clausie //ollie //repeated label //role
					resultObject.setData(v_pepNumber, author, v_date, finalLabel, CurrentSentenceString, 
							null, finalLabel, null, false,"", //reason.getReason()
							"", prevSentence, nextSentence, previousParagraph, entireParagraph, 
							nextParagraph);	
					resultObject.setMID(v_message_ID);
					//System.out.println("here ab!");
					cir.connect();
					cir.insertDoubleResultRecordInDatabase(resultObject,false);
					//System.out.println("here ac!");
			  	 }
	   	     }
		     if (previousParagraph !=null){
		    	// System.out.println("\t d ");
			  	 if( previousParagraph.toLowerCase().contains(s)){
		   		    stateFound = true;
			   		System.out.println("\tState " + doubleLabel+"_"+s + " found in previousParagraph: (" + previousParagraph + ")");
			   		writerForDisco.write("\n"+v_pepNumber + " | " + v_date+ " | " +  v_message_ID + " | " + author + " | " + s + " | " + CurrentSentenceString + " | |" +  s + " "+ doubleLabel + " | ");
					writerAll.write     ("\n"+v_pepNumber + " | " + v_date+ " | " +  v_message_ID + " | " + author + " | " + s + " | " + CurrentSentenceString + " | |" +  s + " "+ doubleLabel + " | ");   
					String finalLabel = doubleLabel+"_"+s;	
					resultObject.setData(v_pepNumber, author, v_date, doubleLabel+"_"+s, CurrentSentenceString, null, doubleLabel+"_"+s, null, false, "", reason.getReason(), prevSentence, nextSentence, previousParagraph, entireParagraph, nextParagraph);
					resultObject.setMID(v_message_ID);
					cir.connect();
					cir.insertDoubleResultRecordInDatabase(resultObject,false);
			  	 }
		     }			     
	   }	
	   System.out.println("\t here 5");
	  //if not found, just insert double in results
      if( stateFound == false){			   		   
	   		writerForDisco.write("\n"+v_pepNumber + " | " + v_date+ " | " +  v_message_ID + " | " + author + " | " + doubleLabel + " | " + CurrentSentenceString + " | |"  + " " + doubleLabel + " | ");
			writerAll.write     ("\n"+v_pepNumber + " | " + v_date+ " | " +  v_message_ID + " | " + author + " | " + doubleLabel + " | " + CurrentSentenceString + " | |" + " " + doubleLabel + " | ");   
			resultObject.setData(v_pepNumber, author, v_date, doubleLabel, CurrentSentenceString, null, doubleLabel, null, false, "", reason.getReason(), prevSentence, nextSentence, previousParagraph, entireParagraph, nextParagraph);						   
			cir.connect();
			cir.insertDoubleResultRecordInDatabase(resultObject,false);
	  	}
	  	*/
	public void finalize() {	    //System.out.println("Probability instance is getting destroyed"); 
	}
		
}
