package postProcess;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.*;

import Process.processLabels.TripleProcessingResult;
import connections.MysqlConnect;
import excelwrite.ApachePOIExcelWrite;
import excelwrite.GetPEPDetails;

import java.io.File;

import jxl.CellView;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.UnderlineStyle;
import jxl.write.Formula;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

//jan 2019. The following post-processing functions from this script is actually used, several is no longer used
	//first   - removeEmptyMatches
	//second  - removeLowerPriorityLabelsForSameSentence
	//third   - removeContinuosAllowableRepeatedStates - when 'discussion', 'update' occur in same pep results one after another
	//fourth  - juggleLabelsInSameProposal - added jan 2019, put 'bdfl_accepted' before 'accepted'
	//fifth   - upperCaseMainStatesInResults - definition included in main class

	//These are no longer used
	//checkRepetitionForSpecificSentences - we dont want pep 8 sentences to be repeated, so this function used those PEP 8 dates
	//shiftStates - not complete - only removes one state from results- can be done in SQL anyway
	//matchStatesRoleAndReason - if label match, then check if it has role and author, not referenced, 
								//maybe we can add code to make useful to check if accepted label sentences are from bdfl and assign 'bdfl_accepted'
	//valueNotInAllowRepeatedStates
	//removeRepeatedMatchedLabel - in case when duplicate found and one is daniel state data labels, we delete daniel state data - no longer needed as we only consider daniel states than ours
	//returnDataOnlyBetweenTwoStates - not needed
	//checkCurrentMessageHasMoreLabelsCaptured
	//removePreviousStudyLabels
	//stateContainsReasons
	//combineResultLabels

	//checkRepeatedSentenceAndFinalLabel 
	//checkRepeatedSentenceAndLabel - dont repeat sentences and labels for the pep

	//For output
	//outputListToFile, outputListToDatabase
	//outputMinimalListToFile - i dont think this is used at all

public class PostProcessOptions extends PostProcess {
	//The First Check
    //dont repeat sentences for the pep
    //this may not be right as many times the previous time the same sentences are not processed, triples are not found to be true
  	//pass the current sentence to check against the all sentences captured for the pep
																			// , TripleProcessingResult result, String CurrentSentenceString, String v_idea, Integer v_pepNumber
  	static String allRepeatedStates[] = {"discussion", "updated"};
  	static String resultsPostProcessedTableName = ""; //will be populated in calling class by calling below function
  	//call this from teh main driver class
  	public void setResultsPostProcessedTableName(String v_resultsPostProcessedTableName) { 
  		this.resultsPostProcessedTableName = resultsPostProcessedTableName;
  	}
  	
  	//First Function used - remove empty clausie columns
  	public ArrayList <TripleProcessingResult> removeEmptyMatches(ArrayList <TripleProcessingResult> tripleProcessingResult)  	{   		
  		ArrayList<Integer> al = new ArrayList<Integer>();   		
  		if(!tripleProcessingResult.isEmpty()){
  			int counter =0;
  			for (int x=0; x <tripleProcessingResult.size(); x++){
  				//System.out.println(tripleProcessingResult.get(x).getPepNumber() + " --- " + tripleProcessingResult.get(x).getClausIE());
  				//System.out.println(counter + " R CIE O: " + tripleProcessingResult.get(x).getReverb() + " " + tripleProcessingResult.get(x).getClausIE() + " " +tripleProcessingResult.get(x).getOllie()); 		
  				if (   (tripleProcessingResult.get(x).getReverb() == null  || tripleProcessingResult.get(x).getReverb().isEmpty()  )
  					&& (tripleProcessingResult.get(x).getClausIE() == null || tripleProcessingResult.get(x).getClausIE().isEmpty() ) 
  					&& (tripleProcessingResult.get(x).getOllie() == null   || tripleProcessingResult.get(x).getOllie().isEmpty()   ) 
  				   )
				{  		
  					//System.out.println("\t REMOVED " + counter + " all nulls=> R CIE O: " + tripleProcessingResult.get(x).getReverb() + " " + tripleProcessingResult.get(x).getClausIE() + " " +tripleProcessingResult.get(x).getOllie()); 
  					tripleProcessingResult.remove(x);
  					x--;
//  					if (tripleProcessingResult.get(x).getReverb().equals("null") && tripleProcessingResult.get(x).getClausIE().equals("null") && tripleProcessingResult.get(x).getOllie().equals("null"))
//  					{  	
//  						System.out.println("all nulls "); 
//  	  					tripleProcessingResult.remove(x); 	//do deletion from list array
//  					}
				}
  				counter++;
  			 //System.out.println(counter + " CIE: " + tripleProcessingResult.get(x).getClausIE() + " "); 
  			}  			 
  	    }
  		return tripleProcessingResult;
  	}
  	
  	//Second Function used
    //if sentence is same and label is same, only keep higher priority labels, e.g. if same sentence returns these two labels, then accept the large labels
	//check all labels in sentence, if a labels is subset of another label, remove it
	//accepted_because_favorable feedback, bdfl_accepted_pep_because_favorable feedback
  	
  	//march 2020..we have to differentiate as 'poll' is removed same sentence as 'poll results' and other labels
  	//so in these cases we ignore
  	
    public static ArrayList <TripleProcessingResult> removeLowerPriorityLabelsForSameSentence(ArrayList <TripleProcessingResult> tripleProcessingResult) throws IOException{
    	System.out.println("Function removeLowerPriorityLabelsForSameSentence()");	
    	//ArrayList<Integer> al = new ArrayList<Integer>(); 
  		
  		try	{
	  		if(!tripleProcessingResult.isEmpty()){
	  			int counter =0;
	  			for (int x=0; x <tripleProcessingResult.size(); x++){	
		  			for (int y=0; y <tripleProcessingResult.size(); y++){					        	
		  				if(x !=y 
		  					&&  tripleProcessingResult.get(y).getCurrentSentence() != null && !tripleProcessingResult.get(y).getCurrentSentence().isEmpty() 
		  				    &&  tripleProcessingResult.get(x).getClausIE() != null         && !tripleProcessingResult.get(x).getClausIE().isEmpty())
		  				{	
		  					//want to make sure both records are of same pep
		  					Integer xPep = tripleProcessingResult.get(x).getPepNumber();
		  					Integer yPep = tripleProcessingResult.get(y).getPepNumber();
		  					//want to make sure both records are for same sentences
		  					String xSentence = tripleProcessingResult.get(x).getCurrentSentence().toLowerCase();		//.toLowerCase()
		  					String ySentence= tripleProcessingResult.get(y).getCurrentSentence().toLowerCase();		//.toLowerCase()
		  					
		  					//get next two labels- to remove subsets
		  					String xLabel = tripleProcessingResult.get(x).getClausIE().toLowerCase();		//.toLowerCase()
		  					String yLabel = tripleProcessingResult.get(y).getClausIE().toLowerCase();	//.toLowerCase()
		  					
		  					//System.out.println("x-val:" + xValue + "y-value: " + yValue);
		  						//.toLowerCase()									//allow discussion states to be repeated
		  					//same pep				//same sentence
		  					if(xPep.equals(yPep) && xSentence.equals(ySentence)) //&& (xPep==yPep) doesnt work	        				
			  	        	{
			  					//mark for deletion by adding to array
			  					//al.add(y); //--delete the second instance, not the first
		  						System.out.println("Same pep and same sentence, label repeated (xPep "+xPep+" + yPep "+yPep+") xSentence: " + ySentence);
		  						
		  						//march 2020, we skip for these substates as we want to show 'poll' and their substates which are only stated in the same sentence
		  						if (xLabel.contains("poll") || yLabel.contains("poll"))
		  							continue;
		  						
		  						if (xLabel.contains("vote") || yLabel.contains("vote"))
		  							continue;
		  								  						
		  						//see which is subset and remove, which one is 'bdfl_accepted_pep' and has 'accepted' x or y,  
		  						// remove y, if all y in x
		  						if (allIn(xLabel,yLabel))  { 
		  							//if to be deleted record has reason, add it to current record
			  						String reason = tripleProcessingResult.get(y).getFinalReasonListArrayAsString().toLowerCase();		  						
			  						//System.out.println("Reason: " + reason);
			  						if (reason!=null)
			  							tripleProcessingResult.get(x).addToFinalReasonListArray(reason);
		  							
		  							System.out.println("\tRemoved label (xPep "+xPep+" + yPep "+yPep+") xLabel: " + ySentence + " Sentence " + tripleProcessingResult.get(y).getCurrentSentence());
		  							tripleProcessingResult.remove(y);
		  						}
		  						if (allIn(yLabel,xLabel))  {
		  							//if to be deleted record has reason, add it to current record
			  						String reason = tripleProcessingResult.get(y).getFinalReasonListArrayAsString();		  						
			  						//System.out.println("Reason: " + reason);
			  						if (reason!=null)
			  							tripleProcessingResult.get(x).addToFinalReasonListArray(reason);
			  						
			  						System.out.println("\tRemoved label (xPep "+xPep+" + yPep "+yPep+") xLabel: " + ySentence + " Sentence " + tripleProcessingResult.get(x).getCurrentSentence());
		  							tripleProcessingResult.remove(x);		  							
		  						}
//		  						else if (yLabel.isIn(xLabel)) {
//		  							tripleProcessingResult.remove(x);
//		  						}
			  					
//			  					if (x==0 || y==0){
//			  						
//			  					}
//			  					else{
//			  						y--;
//			  						x--;
//			  					}
			  					//System.out.println("-------------------------end");
			  				}
		  					//System.out.println("here b");
		  				}
		  			}
	  			}
	  			
	  			//do deletion from list array
	//  			for (Integer a: al){	
	//  				tripleProcessingResult.remove(a);
	//  			} 
	  	    }
  		}
  		catch(Exception e){
  			System.out.println("Exception " + e.toString());  			System.out.println(StackTraceToString(e)  );
  		}
  		return tripleProcessingResult;
    }
    
  	//Third Function used
    // delete continuous discussion states
    // allowable states would be allowed to be repeated in the triple array
    // and this will remove the repeated label from being continuous
    public static ArrayList <TripleProcessingResult> removeContinuosAllowableRepeatedStates(ArrayList <TripleProcessingResult> tripleProcessingResult, Connection conn) throws IOException{
    	System.out.println("Function removeContinuosAllowableRepeatedStates()");	
    	ArrayList<Integer> al = new ArrayList<Integer>(); 
  		
  		try
  		{
	  		if(!tripleProcessingResult.isEmpty()){
	  			int counter =0;
	  			
	  			//june 2020, active state from pep 7 was revmoved, so we have to include pep check for same pep
	  			//it was notworking the pther way
	  			ArrayList<Integer> o = getAllPeps(conn);
	  			
	  			//we loop for each pep
	  			for (Integer k: o) {
	  				for (int x=0; x <tripleProcessingResult.size()-1; x++){	
	  					Integer xPep = tripleProcessingResult.get(x).getPepNumber();
		  					if(k!=xPep)	//skip if not for current pep
		  						continue;		  									
		  				
			  			//for (int y=0; y <tripleProcessingResult.size(); y++){					        	
			  				if(   tripleProcessingResult.get(x).getClausIE() != null   && !tripleProcessingResult.get(x).getClausIE().isEmpty()
			  				   && tripleProcessingResult.get(x+1).getClausIE() != null && !tripleProcessingResult.get(x+1).getClausIE().isEmpty() )
			  				{
			  					String xValue = tripleProcessingResult.get(x).getClausIE().toLowerCase();		//.toLowerCase()
			  					String xNextValue = tripleProcessingResult.get(x+1).getClausIE().toLowerCase();	
			  					//String yValue = tripleProcessingResult.get(y).getClausIE();		//.toLowerCase()
			  					//
			  					//Integer yPep = tripleProcessingResult.get(y).getPepNumber();
			  					//System.out.println("x-val:" + xValue + "y-value: " + yValue);
			  						//.toLowerCase()									
			  					if( xValue.equals(xNextValue)) //&& (xPep==yPep) doesnt work	        				
				  	        	{
				  					//mark for deletion by adding to array
				  					//al.add(y); //--delete the second instance, not the first
			  						System.out.println("allowable repeated label continuous () -- x: "+x + " x+1: "+ x+1 + " xValue: " + xValue + " xNextValue: " + xNextValue);
		//		  					al.add(y); //--delete the second instance, not the first
			  						
			  						//if to be deleted record has reason, add it to current record
			  						String reason = tripleProcessingResult.get(x+1).getFinalReasonListArrayAsString().toLowerCase();		  						
			  						//System.out.println("Reason: " + reason);
			  						if (reason!=null)
			  							tripleProcessingResult.get(x).addToFinalReasonListArray(reason);
			  						
			  						System.out.println("label repeated (xValue "+xValue+" + xNextValue "+xNextValue+") -- x: "+xValue + " y: "+ xNextValue + " reason: " + reason + " sentence " + tripleProcessingResult.get(x+1).getCurrentSentence());
				  					tripleProcessingResult.remove(x+1);
				  					if (x==0){			  						
				  					}
				  					else{
				  						x--;
				  					}
				  				}  				
			  				}
			  			//}
		  			}
	  			
	  			
	  			}
	  			
	  			//do deletion from list array
	//  			for (Integer a: al){	
	//  				tripleProcessingResult.remove(a);
	//  			} 
	  	    }
  		}
  		catch(Exception e){
  			System.out.println("Exception " + e.toString());  			System.out.println(StackTraceToString(e)  );
  		}
  		return tripleProcessingResult;
    }
    
    //Fourth Function...Jan 2019, juggleLabelsInSameProposal, we want to juggle some labels, like accepted and bdfl_accepted. Bdfl_accepted should be put before
  	//For A PEP juggle higher priority labels, e.g. if same pep has these two labels, then shift the large labels
  	//this is different form sentence level removal of higher priority label
	public ArrayList <TripleProcessingResult> juggleLabelsInSameProposal(ArrayList <TripleProcessingResult> tripleProcessingResult, ArrayList<pair> juggleList) 	{ 
		System.out.println("Function juggleLabelsInSameProposal()");	
    	//ArrayList<Integer> al = new ArrayList<Integer>(); 
		/*
		class pair { String first,second;
			pair(String f, String s){	this.first = f;	this.second = s;}
			public String getFirst() {			return first;		}
			public void setFirst(String first) {			this.first = first;		}
			public String getSecond() {			return second;		}
			public void setSecond(String second) {			this.second = second;		} 
		 };
		
		
		//declare the pairs that neeed to be exchanged
		 
		 ArrayList<pair> pList = new ArrayList<pair>();
		 pair p = new pair("accepted", "bdfl_pronouncement_accepted");	pList.add(p);
		 pair p1 = new pair("accepted", "consensus");	     			pList.add(p1);
		 pair p2 = new pair("rejected", "bdfl_pronouncement_rejected"); pList.add(p2);
		 pair p3 = new pair("rejected", "consensus");	      			pList.add(p3);
		 pair p3b = new pair("rejected", "no_consensus");	      		pList.add(p3b);
		 pair p4 = new pair("rejected", "no_support"); 					pList.add(p4);
		 pair p5 = new pair("rejected", "rejected_by_author"); 			pList.add(p5);
		 pair p6 = new pair("withdrawn", "withdrawn_by_author");		pList.add(p6);
		 //added march 2020
		 pair p7 = new pair("rejected", "not_enough_support");	    	pList.add(p7);
		 pair p8 = new pair("rejected", "no_support"); 					pList.add(p8);
		 pair p9 = new pair("rejected", "lazy_consensus");	      		pList.add(p9);
		 pair p10 = new pair("assigned_pep_number", "request_pep_number");	      		pList.add(p10);
		 pair p11 = new pair("bdfl_delegate_accepted_pep", "member_volunteers_bdfl_delegate");	      		pList.add(p11);
		 pair p12 = new pair("accepted", "co_bdfl_delegate_accepted_pep");	      		pList.add(p12);
		 pair p13 = new pair("bdfl_allocated", "willing_bdfl");	      		pList.add(p13);
		 pair p14 = new pair("bdfl_pronouncement_rejected", "no_consensus");	      		pList.add(p14);
		 pair p15 = new pair("co_bdfl_delegate_accepted_pep", "bdfl_pronouncement_accepted");	      		pList.add(p15);
		 pair p16 = new pair("bdfl_allocated", "willing_bdfl");	      		pList.add(p16);
		 pair p17 = new pair("bdfl_delegate_reviewed", "ready_bdfl_review");	      		pList.add(p17);
		 pair p18 = new pair("bdfl_delegate_accepted_pep", "consensus");	      		pList.add(p18);
		 pair p19 = new pair("bdfl_delegate_accepted_pep", "bdfl_delegate_reviewed");	      		pList.add(p19);
		*/
		 
		 //this is for pep 308 "voting_method_enquiry" and "bdfl_pronouncement_rejected"
		// pair p16 = new pair("voting_method_enquiry", "bdfl_pronouncement_rejected");	      		pList.add(p16);
		 
	    //we check to see if the first one in map comes first, we replace with the second one in map
  		try	{
	  		if(!tripleProcessingResult.isEmpty()){
	  			int counter =0;
	  			for (int x=0; x <tripleProcessingResult.size(); x++){	
		  			for (int y=0; y <tripleProcessingResult.size(); y++){					        	
		  				if(x !=y 
		  					&&  tripleProcessingResult.get(y).getCurrentSentence() != null && !tripleProcessingResult.get(y).getCurrentSentence().isEmpty() 
		  				    &&  tripleProcessingResult.get(x).getClausIE() != null         && !tripleProcessingResult.get(x).getClausIE().isEmpty())
		  				{	
		  					//want to make sure both records are of same pep
		  					Integer xPep = tripleProcessingResult.get(x).getPepNumber();		Integer yPep = tripleProcessingResult.get(y).getPepNumber();
		  					//get next two labels- to remove subsets
		  					String xLabel = tripleProcessingResult.get(x).getClausIE().toLowerCase().trim();		//.toLowerCase()
		  					String yLabel = tripleProcessingResult.get(y).getClausIE().toLowerCase().trim();	//.toLowerCase()
		  					//check if second label is after first label date, othersie all occurences of pair match in every PEP will be changed
		  					Timestamp firstLabelTS = tripleProcessingResult.get(x).getDateTimeStamp(), secondLabelTS = tripleProcessingResult.get(y).getDateTimeStamp();
		  					
		  					int sec = -60;  //minus one minute
		  					
		  					Calendar cal = Calendar.getInstance();
		  			        cal.setTimeInMillis(firstLabelTS.getTime());
		  			        cal.add(Calendar.SECOND, sec);
		  			        Timestamp NEW_secondLabelTS = new Timestamp(cal.getTime().getTime());
		  					
		  					//Timestamp NEW_secondLabelTS = (tripleProcessingResult.get(y).getDateTimeStamp().getTime() - TimeUnit.MINUTES.toMillis(120000) ).getTime() ;
		  					
		  					//Set set = hmap.entrySet();	  						Iterator iterator = set.iterator();
		  					//while(iterator.hasNext()) {
	  						//	Map.Entry mentry = (Map.Entry)iterator.next();
	  						//	String firstLabel = mentry.getKey().toString().toLowerCase().trim(), secondLabel = mentry.getValue().toString().toLowerCase().trim();
		  					for (int f=0; f < juggleList.size(); f++){	
		  						String  firstLabel = juggleList.get(f).getFirst().toLowerCase().trim() , 
		  								secondLabel = juggleList.get(f).getSecond().toString().toLowerCase().trim();
	  						//	if(xPep.equals(265) && yPep.equals(265)) {}
	  						//	else continue;
	  						//	System.out.println("checking labels (xPep "+xPep+" + yPep "+yPep+") xLabel: (" + xLabel + ") yLabel (" + yLabel+") firstLabel("+firstLabel+") secondLabel ("+secondLabel+")");
	  							
	  							
//	  							System.out.print("firstLabel is: "+ firstLabel+ " & Value is: " +secondLabel);	  						
	  							//if first and second labels match from the hashmap
	  							if(xLabel.equals(firstLabel) && yLabel.equals(secondLabel) && xPep.equals(yPep)) { 
	  								System.out.println("Same pep and juggling labels mached (xPep "+xPep+" + yPep "+yPep+") xLabel: " + xLabel + " yLabel " + yLabel);
	  								if(secondLabelTS.after(firstLabelTS)  || secondLabelTS.equals(firstLabelTS) ) {
	  									//System.out.println("timestamp b after");
		  								//System.out.println("Same pep and juggling labels mached (xPep "+xPep+" + yPep "+yPep+") xLabel: " + xLabel + " yLabel " + yLabel);
		  								String temp="";
		  								//juggle labels set temp=x,x=y,y=temp
	//	  								temp = xLabel; 
	//	  								tripleProcessingResult.get(x).setClausIE(yLabel);
	//	  								tripleProcessingResult.get(y).setClausIE(temp);
		  								
		  								//just change the dates of second to just 1 min before that of first
	//	  								System.out.println("\tfirstLabelTS: "+firstLabelTS + " secondLabelTS: "+ secondLabelTS );
	//	  								System.out.println("\t\t ORIG TIME: FIRST " + (firstLabelTS.getTime() ) + " SECOND " + secondLabelTS.getTime()   );
		  								
		  								secondLabelTS.setTime(firstLabelTS.getTime() - TimeUnit.MINUTES.toMillis(120000) ); //minus 1 minute, 1000 millisecnds = 1 second
		  								
	//	  								System.out.println("\tfirstLabelTS: "+firstLabelTS + " new secondLabelTS: "+ secondLabelTS );
		  								System.out.println("\t\t NEW TIME: FIRST " + (firstLabelTS.getTime() ) + " SECOND " + NEW_secondLabelTS); //secondLabelTS.getTime()   );
		  								
		  								//tripleProcessingResult.get(y).setDateTimeStamp(firstLabelTS);
		  								tripleProcessingResult.get(y).setDateTimeStamp(NEW_secondLabelTS); //tripleProcessingResult.get(x).getDateTimeStamp());
		  								
		  								//tripleProcessingResult.add(x, tripleProcessingResult.get(y)); //put y in position of x
		  								
		  								//delete from existing place and insert ahead of first one, change datetimestamp to few minutes before
		  								
		  								//changing only label, attaches label to xlabel's date,  which should be okay as the order will be changed
		  							}
		  							//QUESTION, how about cases when Accepted, consensus and no consensus is in same pep? - only accepted and consensus will be changed
		  							//QUESTION, how about cases when Accepted, Rejected and consensus is in same pep? 
		  								//first change will tale place, second change and i guess third change will bring it back to original..lolz
	  							}
	  						}
	  						//String var= hmap.get(2); /* Get values based on key*/
	  						//System.out.println("Value at index 2 is: "+var);	  						
		  				}
		  			}
	  			}  			
	  	    }
  		}
  		catch(Exception e){
  			System.out.println("Exception " + e.toString());  			System.out.println(StackTraceToString(e)  );
  		}
  		return tripleProcessingResult;
	}
	
    //Fourth Function...Jan 2019, juggleLabelsInSameProposal, we want to juggle some labels, like accepted and bdfl_accepted. Bdfl_accepted should be put before
  	//For A PEP juggle higher priority labels, e.g. if same pep has these two labels, then shift the large labels
  	//this is different form sentence level removal of higher priority label
	public ArrayList <TripleProcessingResult> juggleImmediateLabelsInSameProposal(ArrayList <TripleProcessingResult> tripleProcessingResult, ArrayList<pair> juggleList, Connection conn) 	{ 
		System.out.println("Function juggleLabelsInSameProposal()");
	    //we check to see if the first one in map comes first, we replace with the second one in map
  		try	{
	  		if(!tripleProcessingResult.isEmpty()){
	  			int counter =0;
	  			
	  		//june 2020, active state from pep 7 was revmoved, so we have to include pep check for same pep
	  			//it was notworking the pther way
	  			ArrayList<Integer> o = getAllPeps(conn);
	  			
	  			//we loop for each pep
	  			for (Integer k: o) {
		  			for (int x=0; x <tripleProcessingResult.size(); x++){	
			  			//for (int y=0; y <tripleProcessingResult.size(); y++){					        	
			  				if(  tripleProcessingResult.get(x).getCurrentSentence() != null && !tripleProcessingResult.get(x).getCurrentSentence().isEmpty() 
			  				    &&  tripleProcessingResult.get(x+1).getClausIE() != null         && !tripleProcessingResult.get(x+1).getClausIE().isEmpty())
			  				{	
			  					//want to make sure both records are of same pep
			  					Integer xPep = tripleProcessingResult.get(x).getPepNumber();		Integer yPep = tripleProcessingResult.get(x+1).getPepNumber();
			  					//get next two labels- to remove subsets
			  					String xLabel = tripleProcessingResult.get(x).getClausIE().toLowerCase().trim();		//.toLowerCase()
			  					String xNextLabel = tripleProcessingResult.get(x+1).getClausIE().toLowerCase().trim();	//.toLowerCase()
			  					//check if second label is after first label date, othersie all occurences of pair match in every PEP will be changed
			  					Timestamp firstLabelTS = tripleProcessingResult.get(x).getDateTimeStamp(), secondLabelTS = tripleProcessingResult.get(x+1).getDateTimeStamp();
			  					
			  					int sec = -60;  //minus one minute
			  					
			  					Calendar cal = Calendar.getInstance();
			  			        cal.setTimeInMillis(firstLabelTS.getTime());
			  			        cal.add(Calendar.SECOND, sec);
			  			        Timestamp NEW_secondLabelTS = new Timestamp(cal.getTime().getTime());
			  					
			  					//Timestamp NEW_secondLabelTS = (tripleProcessingResult.get(y).getDateTimeStamp().getTime() - TimeUnit.MINUTES.toMillis(120000) ).getTime() ;
			  					
			  					//Set set = hmap.entrySet();	  						Iterator iterator = set.iterator();
			  					//while(iterator.hasNext()) {
		  						//	Map.Entry mentry = (Map.Entry)iterator.next();
		  						//	String firstLabel = mentry.getKey().toString().toLowerCase().trim(), secondLabel = mentry.getValue().toString().toLowerCase().trim();
			  					for (int f=0; f < juggleList.size(); f++){	
			  						String  firstLabel = juggleList.get(f).getFirst().toLowerCase().trim() , 
			  								secondLabel = juggleList.get(f).getSecond().toString().toLowerCase().trim();
		  						//	if(xPep.equals(265) && yPep.equals(265)) {}
		  						//	else continue;
		  						//	System.out.println("checking labels (xPep "+xPep+" + yPep "+yPep+") xLabel: (" + xLabel + ") yLabel (" + yLabel+") firstLabel("+firstLabel+") secondLabel ("+secondLabel+")");
		  							
		  							
	//	  							System.out.print("firstLabel is: "+ firstLabel+ " & Value is: " +secondLabel);	  						
		  							//if first and second labels match from the hashmap
		  							if(xLabel.equals(firstLabel) && xNextLabel.equals(secondLabel) && xPep.equals(yPep)) { 
		  								System.out.println("Same pep and juggling labels mached (xPep "+xPep+" + yPep "+yPep+") xLabel: " + xLabel + " yLabel " + xNextLabel);
		  								if(secondLabelTS.after(firstLabelTS)  || secondLabelTS.equals(firstLabelTS) ) {
		  									//System.out.println("timestamp b after");
			  								//System.out.println("Same pep and juggling labels mached (xPep "+xPep+" + yPep "+yPep+") xLabel: " + xLabel + " yLabel " + yLabel);
			  								String temp="";
			  								//juggle labels set temp=x,x=y,y=temp
		//	  								temp = xLabel; 
		//	  								tripleProcessingResult.get(x).setClausIE(yLabel);
		//	  								tripleProcessingResult.get(y).setClausIE(temp);
			  								
			  								//just change the dates of second to just 1 min before that of first
		//	  								System.out.println("\tfirstLabelTS: "+firstLabelTS + " secondLabelTS: "+ secondLabelTS );
		//	  								System.out.println("\t\t ORIG TIME: FIRST " + (firstLabelTS.getTime() ) + " SECOND " + secondLabelTS.getTime()   );
			  								
			  								secondLabelTS.setTime(firstLabelTS.getTime() - TimeUnit.MINUTES.toMillis(120000) ); //minus 1 minute, 1000 millisecnds = 1 second
			  								
		//	  								System.out.println("\tfirstLabelTS: "+firstLabelTS + " new secondLabelTS: "+ secondLabelTS );
			  								System.out.println("\t\t NEW TIME: FIRST " + (firstLabelTS.getTime() ) + " SECOND " + NEW_secondLabelTS); //secondLabelTS.getTime()   );
			  								
			  								//tripleProcessingResult.get(y).setDateTimeStamp(firstLabelTS);
			  								tripleProcessingResult.get(x+1).setDateTimeStamp(NEW_secondLabelTS); //tripleProcessingResult.get(x).getDateTimeStamp());
			  								
			  								//tripleProcessingResult.add(x, tripleProcessingResult.get(y)); //put y in position of x
			  								
			  								//delete from existing place and insert ahead of first one, change datetimestamp to few minutes before
			  								
			  								//changing only label, attaches label to xlabel's date,  which should be okay as the order will be changed
			  							}
			  							//QUESTION, how about cases when Accepted, consensus and no consensus is in same pep? - only accepted and consensus will be changed
			  							//QUESTION, how about cases when Accepted, Rejected and consensus is in same pep? 
			  								//first change will tale place, second change and i guess third change will bring it back to original..lolz
		  							}
		  						}
		  						//String var= hmap.get(2); /* Get values based on key*/
		  						//System.out.println("Value at index 2 is: "+var);	  						
			  				}
			  		//	}
		  			}  	
	  			}
	  	    }
  		}
  		catch(Exception e){
  			System.out.println("Exception " + e.toString());  			System.out.println(StackTraceToString(e)  );
  		}
  		return tripleProcessingResult;
	}
	
	//march 2020 ..we just display the peps which have both 'accepted' and 'no_consensus'
	public ArrayList <TripleProcessingResult> showLabelsInSameProposal(ArrayList <TripleProcessingResult> tripleProcessingResult) 	{ 
		System.out.println("Function removeLowerPriorityLabelsForSameSentence()");	
    	//ArrayList<Integer> al = new ArrayList<Integer>(); 
		class pair { String first,second;
			pair(String f, String s){	this.first = f;	this.second = s;}
			public String getFirst() {			return first;		}
			public void setFirst(String first) {			this.first = first;		}
			public String getSecond() {			return second;		}
			public void setSecond(String second) {			this.second = second;		} 
		 };
		
		
		//declare the pairs that need to be showed
		 ArrayList<pair> pList = new ArrayList<pair>();
		 pair p = new pair("accepted", "no_consensus");	pList.add(p);
		 pair p1 = new pair("rejected", "consensus");	     			pList.add(p1);
		 
	    //we check to see if the first one in map comes first, we replace with the second one in map
  		try	{
	  		if(!tripleProcessingResult.isEmpty()){
	  			int counter =0;
	  			for (int x=0; x <tripleProcessingResult.size(); x++){	
		  			for (int y=0; y <tripleProcessingResult.size(); y++){					        	
		  				if(x !=y 
		  					&&  tripleProcessingResult.get(y).getCurrentSentence() != null && !tripleProcessingResult.get(y).getCurrentSentence().isEmpty() 
		  				    &&  tripleProcessingResult.get(x).getClausIE() != null         && !tripleProcessingResult.get(x).getClausIE().isEmpty())
		  				{	
		  					//want to make sure both records are of same pep
		  					Integer xPep = tripleProcessingResult.get(x).getPepNumber();		Integer yPep = tripleProcessingResult.get(y).getPepNumber();
		  					//get next two labels- to remove subsets
		  					String xLabel = tripleProcessingResult.get(x).getClausIE().toLowerCase();		//.toLowerCase()
		  					String yLabel = tripleProcessingResult.get(y).getClausIE().toLowerCase();	//.toLowerCase()
		  					//check if second label is after first label date, othersie all occurences of pair match in every PEP will be changed
		  					Timestamp firstLabelTS = tripleProcessingResult.get(x).getDateTimeStamp(), secondLabelTS = tripleProcessingResult.get(y).getDateTimeStamp();
		  					
		  					//Set set = hmap.entrySet();	  						Iterator iterator = set.iterator();
		  					//while(iterator.hasNext()) {
	  						//	Map.Entry mentry = (Map.Entry)iterator.next();
	  						//	String firstLabel = mentry.getKey().toString().toLowerCase().trim(), secondLabel = mentry.getValue().toString().toLowerCase().trim();
		  					for (int f=0; f < pList.size(); f++){	
		  						String firstLabel = pList.get(f).getFirst().toLowerCase().trim() , secondLabel = pList.get(f).getSecond().toString().toLowerCase().trim();
	  						//	if(xPep.equals(265) && yPep.equals(265)) {}
	  						//	else continue;
	  						//	System.out.println("checking labels (xPep "+xPep+" + yPep "+yPep+") xLabel: (" + xLabel + ") yLabel (" + yLabel+") firstLabel("+firstLabel+") secondLabel ("+secondLabel+")");
	  							
	  							
//	  							System.out.print("firstLabel is: "+ firstLabel+ " & Value is: " +secondLabel);	  						
	  							//if first and second labels match from the hashmap
	  							if(xLabel.equals(firstLabel) && yLabel.equals(secondLabel) && xPep.equals(yPep)) { 
	  								System.out.println("Same pep and showing labels mached (xPep "+xPep+" + yPep "+yPep+") xLabel: " + xLabel + " yLabel " + yLabel);
//	  								if(secondLabelTS.after(firstLabelTS)) {
//	  									System.out.println("timestamp b after");
//		  								//System.out.println("Same pep and juggling labels mached (xPep "+xPep+" + yPep "+yPep+") xLabel: " + xLabel + " yLabel " + yLabel);
//		  								String temp="";
//		  								//juggle labels set temp=x,x=y,y=temp
//	//	  								temp = xLabel; 
//	//	  								tripleProcessingResult.get(x).setClausIE(yLabel);
//	//	  								tripleProcessingResult.get(y).setClausIE(temp);
//		  								
//		  								//just change the dates of second to just 1 min before that of first
//		  								System.out.println("\tfirstLabelTS: "+firstLabelTS + " secondLabelTS: "+ secondLabelTS);
//		  								secondLabelTS.setTime(firstLabelTS.getTime() - TimeUnit.MINUTES.toMillis(120000) ); //minus 1 minute, 1000 millisecnds = 1 second
//		  								System.out.println("\tfirstLabelTS: "+firstLabelTS + " new secondLabelTS: "+ secondLabelTS);
//		  								tripleProcessingResult.get(y).setDateTimeStamp(firstLabelTS);
//		  								
//		  								
//		  								//tripleProcessingResult.add(x, tripleProcessingResult.get(y)); //put y in position of x
//		  								
//		  								//delete from existing place and insert ahead of first one, change datetimestamp to few minutes before
//		  								
//		  								//changing only label, attaches label to xlabel's date,  which should be okay as the order will be changed
//		  							}
		  							//QUESTION, how about cases when Accepted, consensus and no consensus is in same pep? - only accepted and consensus will be changed
		  							//QUESTION, how about cases when Accepted, Rejected and consensus is in same pep? 
		  								//first change will tale place, second change and i guess third change will bring it back to original..lolz
	  							}
	  						}
	  						//String var= hmap.get(2); /* Get values based on key*/
	  						//System.out.println("Value at index 2 is: "+var);	  						
		  				}
		  			}
	  			}  			
	  	    }
  		}
  		catch(Exception e){
  			System.out.println("Exception " + e.toString());  			System.out.println(StackTraceToString(e)  );
  		}
  		return tripleProcessingResult;
	}
	
	//march 2020 ..combine some substates which dont add much
	//example..all "no_support" into "not_enough_support", 
	public ArrayList <TripleProcessingResult> combineLabelsInSameProposal(ArrayList <TripleProcessingResult> tripleProcessingResult) 	{ 
		System.out.println("Function removeLowerPriorityLabelsForSameSentence()");	
    	//ArrayList<Integer> al = new ArrayList<Integer>(); 
		class pair { String first,second;
			pair(String f, String s){	this.first = f;	this.second = s;}
			public String getFirst() {			return first;		}
			public void setFirst(String first) {			this.first = first;		}
			public String getSecond() {			return second;		}
			public void setSecond(String second) {			this.second = second;		} 
		 };
		
		
		//declare the pairs that neeed to be exchanged
		 ArrayList<pair> pList = new ArrayList<pair>();
		 pair p = new pair("not_enough_support", "no_support");	pList.add(p);
		 pair p1 = new pair("withdrawn_by_author", "rejected_by_author");	     			pList.add(p1);
		 pair p2 = new pair("positive_feedback", "idea_received_positively");	     			pList.add(p2);
		 
	    //we check to see if the first one in map comes first, we replace with the second one in map
  		try	{
	  		if(!tripleProcessingResult.isEmpty()){
	  			int counter =0;
	  			for (int x=0; x <tripleProcessingResult.size(); x++){	
		  			for (int y=0; y <tripleProcessingResult.size(); y++){					        	
		  				if(x !=y 
		  					&&  tripleProcessingResult.get(y).getCurrentSentence() != null && !tripleProcessingResult.get(y).getCurrentSentence().isEmpty() 
		  				    &&  tripleProcessingResult.get(x).getClausIE() != null         && !tripleProcessingResult.get(x).getClausIE().isEmpty())
		  				{	
		  					//want to make sure both records are of same pep
		  					Integer xPep = tripleProcessingResult.get(x).getPepNumber();		Integer yPep = tripleProcessingResult.get(y).getPepNumber();
		  					//get next two labels- to remove subsets
		  					String xLabel = tripleProcessingResult.get(x).getClausIE().toLowerCase();		//.toLowerCase()
		  					String yLabel = tripleProcessingResult.get(y).getClausIE().toLowerCase();	//.toLowerCase()
		  					//check if second label is after first label date, othersie all occurences of pair match in every PEP will be changed
		  					Timestamp firstLabelTS = tripleProcessingResult.get(x).getDateTimeStamp(), secondLabelTS = tripleProcessingResult.get(y).getDateTimeStamp();
		  					
		  					for (int f=0; f < pList.size(); f++){	
		  						String firstLabel = pList.get(f).getFirst().toLowerCase().trim() , secondLabel = pList.get(f).getSecond().toString().toLowerCase().trim();
	  						
	  							
//	  							System.out.print("firstLabel is: "+ firstLabel+ " & Value is: " +secondLabel);	  						
	  							//if first and second labels match from the hashmap
	  							if(xLabel.equals(firstLabel) && yLabel.equals(secondLabel)) { 
	  								System.out.println("Similar labels ...combining labels mached (xPep "+xPep+" + yPep "+yPep+") xLabel: " + xLabel + " yLabel " + yLabel);
		  								//combine
		  								tripleProcessingResult.get(y).setClausIE(firstLabel);
		  								System.out.println("\tfirstLabelTS: "+firstLabelTS + " new secondLabelTS: "+ secondLabelTS);		  								
	  							}
	  						}  						
		  				}
		  			}
	  			}  			
	  	    }
  		}
  		catch(Exception e){
  			System.out.println("Exception " + e.toString());  			System.out.println(StackTraceToString(e)  );
  		}
  		return tripleProcessingResult;
	}
	
	//March 2020 remove some substates 
	//example "uncontroversial"
    public static ArrayList <TripleProcessingResult> removesubstates(ArrayList <TripleProcessingResult> tripleProcessingResult) throws IOException{
    	System.out.println("Function removesubstates()");	
    	//ArrayList<Integer> al = new ArrayList<Integer>(); 
  		
    	
    	
 //   	class pairp { Integer first; String second;
//			pairp(Integer f, String s){	this.first = f;	this.second = s;}
//			public Integer getFirst() {			return first;		}
//			public void setFirst(Integer first) {			this.first = first;		}
//			public String getSecond() {			return second;		}
//			public void setSecond(String second) {			this.second = second;		} 
 //   	};
    	
    	//String removeSpecificSubstatesForPEPs[] = {"bdfl_pronouncement_rejected"};
  //  	 ArrayList<pair> removeSpecificSubstatesForPEPs = new ArrayList<pair>();
//		 pair p = new pair(308, "bdfl_pronouncement_rejected");	removeSpecificSubstatesForPEPs.add(p);
		 //pair p1 = new pair("withdrawn_by_author", "rejected_by_author");	     			pList.add(p1);
		 //pair p2 = new pair("positive_feedback", "idea_received_positively");	     			pList.add(p2);
    	
  		try
  		{
	  		if(!tripleProcessingResult.isEmpty()){
	  			int counter =0;
	  			for (int x=0; x <tripleProcessingResult.size()-1; x++){	
		  			//for (int y=0; y <tripleProcessingResult.size(); y++){					        	
		  				if(   tripleProcessingResult.get(x).getClausIE() != null   && !tripleProcessingResult.get(x).getClausIE().isEmpty()
		  				   //&& tripleProcessingResult.get(x+1).getClausIE() != null && !tripleProcessingResult.get(x+1).getClausIE().isEmpty() 
		  				   )
		  				{
		  					String xValue = tripleProcessingResult.get(x).getClausIE().toLowerCase().trim();		//.toLowerCase()
		  					//String xNextValue = tripleProcessingResult.get(x+1).getClausIE().toLowerCase();	
		  					
		  					Integer currPEP = tripleProcessingResult.get(x).getPepNumber();
		  					
		  					for (String rs: removeSubstatesList)
		  					{
			  					if( xValue.equals(rs)) //&& (xPep==yPep) doesnt work	        				
				  	        	{
				  					//mark for deletion by adding to array
				  					//al.add(y); //--delete the second instance, not the first
			  						System.out.println("remove substates --currPEP "+currPEP+" x: "+x + " x+1: "+ x+1 + " xValue: " + xValue); // + " xNextValue: " + xNextValue);
		//		  					al.add(y); //--delete the second instance, not the first
			  						
			  						//if to be deleted record has reason, add it to current record
			  						//String reason = tripleProcessingResult.get(x+1).getFinalReasonListArrayAsString().toLowerCase();		  						
			  						//System.out.println("Reason: " + reason);
			  						//if (reason!=null)
			  						//	tripleProcessingResult.get(x).addToFinalReasonListArray(reason);
			  						
			  						//+ xNextValue "+xNextValue+") -- x: "+xValue + " y: "+ xNextValue + " reason: " + reason + " sentence " + tripleProcessingResult.get(x+1).getCurrentSentence());
				  					tripleProcessingResult.remove(x);
				  					if (x==0){			  						
				  					}
				  					else{
				  						x--;
				  					}
				  					System.out.println("remove substate (xValue "+xValue+" ) "); 
				  				}
		  					}
		  					
		  					//now for specific substates in PEP
		  					//pep 308 we want to get rid of 'bdfl_pronouncement_rejected' as its just messes things up
		  					for (int f=0; f < removeSpecificSubstatesForPEPs.size(); f++){	
		  						Integer pep = removeSpecificSubstatesForPEPs.get(f).getFirst();
		  						String label = removeSpecificSubstatesForPEPs.get(f).getSecond().toLowerCase().trim();
		  						//System.out.println("remove substates for specific PEPs("+currPEP+")-- xf: "+f + " f+1: "+ f+1 + " label: " + label); 
			  					
		  						if(currPEP.equals(pep) && xValue.equals(label)) //&& (xPep==yPep) doesnt work	        				
				  	        	{
				  					//mark for deletion by adding to array
				  					//al.add(y); //--delete the second instance, not the first
			  						System.out.println("in if block ... remove substates for specific PEPs("+pep+")-- x: "+x + " x+1: "+ x+1 + " xValue: " + xValue); // + " xNextValue: " + xNextValue);
		//		  					al.add(y); //--delete the second instance, not the first
			  						
			  						//if to be deleted record has reason, add it to current record
			  						//String reason = tripleProcessingResult.get(x+1).getFinalReasonListArrayAsString().toLowerCase();		  						
			  						//System.out.println("Reason: " + reason);
			  						//if (reason!=null)
			  						//	tripleProcessingResult.get(x).addToFinalReasonListArray(reason);
			  						
			  						//+ xNextValue "+xNextValue+") -- x: "+xValue + " y: "+ xNextValue + " reason: " + reason + " sentence " + tripleProcessingResult.get(x+1).getCurrentSentence());
				  					tripleProcessingResult.remove(x);
				  					if (x==0){			  						
				  					}
				  					else{
				  						x--;
				  					}
				  					System.out.println("removed specific substate (xValue "+xValue+" ) specific PEPs("+pep+") "); 
				  				}
		  					}
		  					
		  				}
		  			//}
	  			}
	  			
	  			//do deletion from list array
	//  			for (Integer a: al){	
	//  				tripleProcessingResult.remove(a);
	//  			} 
	  	    }
  		}
  		catch(Exception e){
  			System.out.println("Exception " + e.toString());  			System.out.println(StackTraceToString(e)  );
  		}
  		return tripleProcessingResult;
    }
	
    //june 2020 ...differentiate consensus and no consensus
    public static ArrayList <TripleProcessingResult> differentiateSomeSubstates(ArrayList <TripleProcessingResult> tripleProcessingResult) throws IOException{
    	System.out.println("Function differentiate some substates()");	
  		
  		try
  		{
	  		if(!tripleProcessingResult.isEmpty()){
	  			int counter =0;
	  			for (int x=0; x <tripleProcessingResult.size()-1; x++){					        	
		  				
	  				if(tripleProcessingResult.get(x).getClausIE() != null   && !tripleProcessingResult.get(x).getClausIE().isEmpty()	   )
		  				{
		  					String xValue = tripleProcessingResult.get(x).getClausIE().toLowerCase().trim();		//.toLowerCase()
		  					//String xNextValue = tripleProcessingResult.get(x+1).getClausIE().toLowerCase();	
		  					String sentence = tripleProcessingResult.get(x).getCurrentSentence().toLowerCase().trim();
		  					String label = tripleProcessingResult.get(x).getLabel().toLowerCase().trim();
		  					Integer currPEP = tripleProcessingResult.get(x).getPepNumber();
		  					
		  					//for (String rs: cleanSubstatesForPEPs)
		  					//{
			  					if( (xValue.equals("consensus") && sentence.contains("no consensus"))
			  						||	(xValue.equals("vote") && (currPEP == 385)) //PEP 385 vote delete 	
			  						||	(xValue.equals("consensus") && (currPEP == 407)) //PEP 385 vote delete
			  						||	(xValue.equals("no_consensus") && (currPEP == 3003)) //PEP 3003 no consensus 
			  						||	(xValue.equals("bdfl_pronouncement_rejected") && (currPEP == 347)) //PEP 3003 no consensus 
			  						||	(xValue.equals("consensus") && (currPEP == 8)) //PEP 3003 no consensus 
			  						||	(xValue.equals("consensus") && (currPEP == 289)) //PEP 3003 no consensus
			  						||  (label.equals("rejected_by_author") && (currPEP == 369))  ///self loop can only be removed by checking the label
			  						) //&& (xPep==yPep) doesnt work	        				
				  	        	{
			  						System.out.println("\tremove substates --currPEP "+currPEP+" x: "+x + " x+1: "+ x+1 + " xValue: " + xValue); // + " xNextValue: " + xNextValue);	
				  					tripleProcessingResult.remove(x);
				  					if (x==0){			  						
				  					}
				  					else{
				  						x--;
				  					}
				  					System.out.println("\tremove substate (xValue "+xValue+" ) "); 
				  				}
		  					//}		  					
		  				}
	  			}
	  	    }
  		}
  		catch(Exception e){
  			System.out.println("Exception " + e.toString());  			System.out.println(StackTraceToString(e)  );
  		}
  		return tripleProcessingResult;
    }
    
    
    
  //March 2020 rename some substates 
  //example "uncontroversial"
  public static ArrayList <TripleProcessingResult> renamesubstates(ArrayList <TripleProcessingResult> tripleProcessingResult) throws IOException{
  	System.out.println("Function removesubstates()");	
  	//ArrayList<Integer> al = new ArrayList<Integer>(); 
		
  	//String removeSubstatesList[] = {"uncontroversial","check_in_pep","concerns_addressed","renamed"};
  	/*
  	class pair { String first; String second;
		pair(String f, String s){	this.first = f;	this.second = s;}
		public String getFirst() {			return first;		}
		public void setFirst(String first) {			this.first = first;		}
		public String getSecond() {			return second;		}
		public void setSecond(String second) {			this.second = second;		} 
  	};
  	
  	//String removeSpecificSubstatesForPEPs[] = {"bdfl_pronouncement_rejected"};
  	 ArrayList<pair> renameSpecificSubstatesForPEPs = new ArrayList<pair>();
	 pair p = new pair("willing_bdfl", "member_willing_to_be_bdfl_delegate");		renameSpecificSubstatesForPEPs.add(p);
	 pair p1 = new pair("bdfl_allocated", "bdfl_delegate_allocated");				renameSpecificSubstatesForPEPs.add(p1);
	 pair p2 = new pair("bdfl_asked_volunteer", "bdfl_asked_delegate_volunteer");	renameSpecificSubstatesForPEPs.add(p2);
	 pair p3 = new pair("ready_review", "ready_bdfl_review");	renameSpecificSubstatesForPEPs.add(p3);
	 pair p4 = new pair("2nd Draft", "ReDraft");	renameSpecificSubstatesForPEPs.add(p4);
	 pair p5 = new pair("3rd Draft", "ReDraft");	renameSpecificSubstatesForPEPs.add(p5);	
	 pair p6 = new pair("4th Draft", "ReDraft");	renameSpecificSubstatesForPEPs.add(p6);
	 pair p7 = new pair("5th Draft", "ReDraft");	renameSpecificSubstatesForPEPs.add(p7);
	 pair p8 = new pair("6th Draft", "ReDraft");	renameSpecificSubstatesForPEPs.add(p8);	
	 pair p9 = new pair("7th Draft", "ReDraft");	renameSpecificSubstatesForPEPs.add(p9);
	 
	 //pair p1 = new pair("withdrawn_by_author", "rejected_by_author");	     			pList.add(p1);
	 //pair p2 = new pair("positive_feedback", "idea_received_positively");	     			pList.add(p2);
  	*/
		try
		{
  		if(!tripleProcessingResult.isEmpty()){
  			int counter =0;
  			for (int x=0; x <tripleProcessingResult.size()-1; x++){	
	  			//for (int y=0; y <tripleProcessingResult.size(); y++){					        	
	  				if(   tripleProcessingResult.get(x).getClausIE() != null   && !tripleProcessingResult.get(x).getClausIE().isEmpty()
	  				   //&& tripleProcessingResult.get(x+1).getClausIE() != null && !tripleProcessingResult.get(x+1).getClausIE().isEmpty() 
	  				   )
	  				{
	  					String xValue = tripleProcessingResult.get(x).getClausIE().toLowerCase();		//.toLowerCase()
	  					//String xNextValue = tripleProcessingResult.get(x+1).getClausIE().toLowerCase();	
	  					Integer currPEP = tripleProcessingResult.get(x).getPepNumber();
	  					
	  					for (pair rs: renameSpecificSubstatesForPEPs)
	  					{
	  						String sub = rs.getFirst().toLowerCase().trim();
	  						String change = rs.getSecond().toLowerCase().trim();
		  					if( xValue.equals(sub)) //&& (xPep==yPep) doesnt work	        				
			  	        	{
			  					//mark for deletion by adding to array
			  					//al.add(y); //--delete the second instance, not the first
		  						System.out.println("rename substates --currPEP "+currPEP+" x: "+x + " x+1: "+ x+1 + " xValue: " + xValue + " changed to: "+ change); // + " xNextValue: " + xNextValue);
		  						tripleProcessingResult.get(x).setClausIE(change);
			  					System.out.println("renamed substate (xValue "+xValue+" ) "); 
			  				}
	  					}
	  				}
  			}
  	    }
		}
		catch(Exception e){
			System.out.println("Exception " + e.toString());  			System.out.println(StackTraceToString(e)  );
		}
		return tripleProcessingResult;
  }
    
	private static String stateContainsLesserStates(ArrayList<StatesReasons> statereasons,String v_stateToCheck, String yValue) {
		String str = "";
		try{
			for(StatesReasons sr: statereasons)	{
				String state =  sr.getState();				String ls = sr.getLesserState();				
				if(state==null|| state.isEmpty() || state.length()==0 || ls==null|| ls.isEmpty() || ls.length()==0 ) 
				{}
				else {
					//we want to match reasons for accepted state only
					if(state.equals(v_stateToCheck)  && yValue.contains(ls)){
						str = str+ ls+ ",";
					}
				}
			}
		}
		catch(Exception e){
			System.out.println(StackTraceToString(e)  );
		}
		return str;
	}
	//for same pep, same sentence, if two labels are captured, 
    //remove less priority labels
    //accepted will be less priority then bdfl accepted pep
    public static ArrayList <TripleProcessingResult> prioritiseLabelinSameSentenceMultipleLabelMatches(ArrayList <TripleProcessingResult> tripleProcessingResult, FileWriter fw) throws IOException{
  		ArrayList<Integer> al = new ArrayList<Integer>();   		
  		if(!tripleProcessingResult.isEmpty()){
  			int counter =0;
  			for (int x=0; x <tripleProcessingResult.size(); x++){	
	  			for (int y=0; y <tripleProcessingResult.size(); y++){					        	
	  				if(x !=y && tripleProcessingResult.get(y).getClausIE() != null && !tripleProcessingResult.get(y).getClausIE().isEmpty()
	  						 && tripleProcessingResult.get(x).getClausIE() != null && !tripleProcessingResult.get(x).getClausIE().isEmpty())
	  				{
	  					String xValue = tripleProcessingResult.get(x).getClausIE().toLowerCase();
	  					String yValue = tripleProcessingResult.get(y).getClausIE().toLowerCase();
	  					//System.out.println("x-val:" + xValue + "y-value: " + yValue);
	  					if( xValue.toLowerCase().contains(yValue) )   	        				
		  	        	{
	  						//if to be deleted record has reason, add it to current record
	  						String reason = tripleProcessingResult.get(y).getFinalReasonListArrayAsString().toLowerCase();		  						
	  						//System.out.println("Reason: " + reason);
	  						if (reason!=null)
	  							tripleProcessingResult.get(x).addToFinalReasonListArray(reason);
	  						
	  						
	  						//mark for deletion by adding to array
		  					//al.add(y); //--delete the second instance, not the first
		  					System.out.println("label contains-- x: "+x + " y: "+ y + " xLabel: " + yValue);
//		  					al.add(y); //--delete the second instance, not the first
		  					tripleProcessingResult.remove(y);
		  					y--;
		  					x--;
		  				}  				
	  				}
	  			}
  			}
  			
  			//do deletion from list array
//  			for (Integer a: al){	
//  				tripleProcessingResult.remove(a);
//  			} 
  	    }
  		return tripleProcessingResult;
    }
    
	
	public ArrayList <TripleProcessingResult> checkRepetitionForSpecificSentences(ArrayList <TripleProcessingResult> tripleProcessingResult, FileWriter fw)
  	{ 
  		Boolean repeatedSentence = false,found = true;  		
  		ArrayList<Integer> al = new ArrayList<Integer>();   		
  		if(!tripleProcessingResult.isEmpty()){
  			int counter =0;
  			for (int x=0; x <tripleProcessingResult.size(); x++){	
	  			for (int y=0; y <tripleProcessingResult.size(); y++){					        	
	  				if(x !=y && tripleProcessingResult.get(y).getCurrentSentence() != null && !tripleProcessingResult.get(y).getCurrentSentence().isEmpty())
	  				{
	  					String xValue = tripleProcessingResult.get(x).getCurrentSentence().toLowerCase();
	  					String yValue = tripleProcessingResult.get(y).getCurrentSentence().toLowerCase();		
	  					if( xValue.toLowerCase().equals(yValue.toLowerCase()) )   	        				
		  	        	{
		  	        		repeatedSentence= true;
		  					found=true;	
		  					//mark for deletion by adding to array
		  					System.out.println("sentence matched--adding to remove list x: "+x + " y: "+ y + " xSentence: " + xValue);
//		  					al.add(y); //--delete the second instance, not the first
		  					tripleProcessingResult.remove(y);
		  					y--;
		  				}  				
	  				}
	  			}
  			}
  			
  			//do deletion from list array
//  			for (Integer a: al){	
//  				tripleProcessingResult.remove(a);
//  				System.out.println("Removed from list xValue: " + a);
//  			}
//  			try {
//				outputListToFile(tripleProcessingResult,fw);
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}  			
  			
  	    }
  		return tripleProcessingResult;
  	}
  	
  	//move state to top, eg previous version should be moved to top
  	public ArrayList <TripleProcessingResult> shiftStates(ArrayList <TripleProcessingResult> tripleProcessingResult, FileWriter fw) 	{ 
  		Boolean repeatedSentence = false,found = true;  		
  		ArrayList<Integer> al = new ArrayList<Integer>();
  		if(!tripleProcessingResult.isEmpty()){
  			int counter =0;
  			for (int x=0; x <tripleProcessingResult.size(); x++){
  				if(tripleProcessingResult.get(x).getFinalLabel() != null 
  						&& !tripleProcessingResult.get(x).getFinalLabel().isEmpty())				{
  					String xValue = tripleProcessingResult.get(x).getFinalLabel().toLowerCase();  						
  					if( xValue.toLowerCase().equals("previous_version") ){
	  	        		//shift the record to the top	
  						System.out.println("found previous state "); 
	  				}  				
  				}	  			
  			}
  			//do deletion from list array  			
  	    }
  		return tripleProcessingResult;
  	}
  	//if label match, then check if it has role and sauthor
  	public ArrayList <TripleProcessingResult> matchStatesRoleAndReason(ArrayList <TripleProcessingResult> tripleProcessingResult)  	{  		
  		ArrayList<Integer> al = new ArrayList<Integer>();   		
  		if(!tripleProcessingResult.isEmpty()){
  			int counter =0;
  			for (int x=0; x <tripleProcessingResult.size(); x++){	
	  			for (int y=0; y <tripleProcessingResult.size(); y++){					        	
	  				if(x !=y &&  tripleProcessingResult.get(y).getCurrentSentence() != null && !tripleProcessingResult.get(y).getCurrentSentence().isEmpty())			{
	  					//if label match, then check if it has role and sauthor 
	  					//if not then populate and delete current record 
	  					//keep the first record and populate that, not the latter one	  					
	  					
	  					String xValue = tripleProcessingResult.get(x).getCurrentSentence().toLowerCase();		String yValue = tripleProcessingResult.get(y).getCurrentSentence().toLowerCase();		
	  					if( xValue.toLowerCase().equals(yValue) )   	            	{
		  					//mark for deletion by adding to array
		  					al.add(y); //--delete the second instance, not the first		  					
		  				}  				
	  				}
	  			}
  			}
  			//do deletion from list array
  			for (Integer a: al){	
  				tripleProcessingResult.remove(a);
  			}
  	    }
  		return tripleProcessingResult;
  	}
  	
  	
  	//for instance between draft and accepted
  	public ArrayList <TripleProcessingResult> returnDataOnlyBetweenTwoStates(ArrayList <TripleProcessingResult> tripleProcessingResult, String startState, String endState )  	{   	
  		//get list of peps
  		ArrayList<Integer> pepList = new ArrayList<Integer>();  
  		Integer xPep;
  		for (int x=0; x <tripleProcessingResult.size(); x++){
  			xPep = tripleProcessingResult.get(x).getPepNumber();
  			pepList.add(xPep);
  		}
  		// remove duplicates
  		Set<Integer> hs = new HashSet<>();
  		hs.addAll(pepList);  		pepList.clear();  		pepList.addAll(hs);  		
  		
  		//for all peps in list, one by one  		
  		for (int a=0; a <pepList.size(); a++)  		{
  			Integer k = pepList.get(a);  //start with one pepNumber in List  			
  			for (int b=0; b <tripleProcessingResult.size(); b++)  			{
  				//but only limit to one pep at a time
		  		if(!tripleProcessingResult.isEmpty() && tripleProcessingResult.get(b).getPepNumber()==k)		  		{
		  			int counter =0;
		  			//delete all before startstate
//		  			for (int x=0; x <tripleProcessingResult.size(); x++){
		  				//System.out.println(counter + " R CIE O: " + tripleProcessingResult.get(x).getReverb() + " " + tripleProcessingResult.get(x).getClausIE() + " " +tripleProcessingResult.get(x).getOllie()); 		
		  															//to lowercase important as we want to capture state data/danile data as well
		  				if (!tripleProcessingResult.get(b).getClausIE().toLowerCase().contains(startState) )						{  		
		  					//System.out.println("\t" + counter + " all nulls=> R CIE O: " + tripleProcessingResult.get(x).getReverb() + " " + tripleProcessingResult.get(x).getClausIE() + " " +tripleProcessingResult.get(x).getOllie()); 
		  						tripleProcessingResult.remove(b);
		  						b--;
		  					
		//  					if (tripleProcessingResult.get(x).getReverb().equals("null") && tripleProcessingResult.get(x).getClausIE().equals("null") && tripleProcessingResult.get(x).getOllie().equals("null"))
		//  					{  	
		//  						System.out.println("all nulls "); 
		//  	  					tripleProcessingResult.remove(x); 	//do deletion from list array
		//  					}
						}
		  				//exit as soon as startstate is found
		  				else		  				{
		  					//System.out.println("-----------------------------------------------------start state found");
		  					//just remove for that pep
		  						break;  //we found start state and now exit
		  				}
		  				counter++;
		  		}
  			}
		  			//delete all after end state
  			Boolean startDeleting = false;
  			for (int b=0; b <tripleProcessingResult.size(); b++)  			{
  				if(!tripleProcessingResult.isEmpty() && tripleProcessingResult.get(b).getPepNumber()==k)	{
	  				int counter =0;
	  				//System.out.println(counter + " R CIE O: " + tripleProcessingResult.get(x).getReverb() + " " + tripleProcessingResult.get(x).getClausIE() + " " +tripleProcessingResult.get(x).getOllie()); 		
	  															//to lowercase important as we want to capture state data/danile data as well
	  				if (tripleProcessingResult.get(b).getClausIE().toLowerCase().contains(endState) )		{
	  						startDeleting = true;
					}
	  				else{
	  					if (startDeleting == true)	  					{
		  					//System.out.println("\t" + counter + " all nulls=> R CIE O: " + tripleProcessingResult.get(x).getReverb() + " " + tripleProcessingResult.get(x).getClausIE() + " " +tripleProcessingResult.get(x).getOllie()); 
		  					tripleProcessingResult.remove(b);
		  					b--;
	  					}
					}  				
	  				counter++;
		  		}
  			}  	//end for loop	
		  	     //end if
  			
  		} //end for loop
  		return tripleProcessingResult;
  	}
  	
  	public static Boolean valueNotInAllowRepeatedStates(String str)  	{
  		for(String rs: allRepeatedStates)  		{
  			if (rs.equals(str))
  				return false;
  		}
  		return true;
  	}
  	
  	//for a sentence, if two labels are captured, which one to choose?
  	//example pep and form rejected, which one is right one
  	
  	//318	 2004-08-04 	8771	 Kurt B. Kaiser 	 closed 	 Implementation for PEP 318 using java-style syntax( 2004-06-25) http:python.orgsf979728 closed by anthonybaxter .	 R:	 C: closed
  	//318	 2004-08-04 	8771	 Kurt B. Kaiser 	 implementation_closed 	 Implementation for PEP 318 using java-style syntax( 2004-06-25) http:python.orgsf979728 closed by anthonybaxter .	 R:	 C: implementation_closed

  	//if closed and implmentation closed both have been found in same sentence, prefer implmentation closed and dont show closed.
     
  	//CHECK if label captured are repeated
  	//in case when duplicate found and one is daniel state data labels, we delete daniel state data
    public static ArrayList <TripleProcessingResult> removeRepeatedMatchedLabel_WithinSameProposal(ArrayList <TripleProcessingResult> tripleProcessingResult) throws IOException{
    	System.out.println("Function removeRepeatedMatchedLabel_WithinSameProposal()");	
    	ArrayList<Integer> al = new ArrayList<Integer>(); 
  		
  		try  		{
	  		if(!tripleProcessingResult.isEmpty()){
	  			int counter =0;
	  			for (int x=0; x <tripleProcessingResult.size(); x++){	
		  			for (int y=0; y <tripleProcessingResult.size(); y++){					        	
		  				if(x !=y 
		  						&& tripleProcessingResult.get(y).getClausIE() != null  && !tripleProcessingResult.get(y).getClausIE().isEmpty()
		  						&& tripleProcessingResult.get(x).getClausIE() != null  && !tripleProcessingResult.get(x).getClausIE().isEmpty())
		  				{
		  					String xValue = tripleProcessingResult.get(x).getClausIE().toLowerCase();	String yValue = tripleProcessingResult.get(y).getClausIE().toLowerCase();		//.toLowerCase()
		  					Integer xPep = tripleProcessingResult.get(x).getPepNumber();				Integer yPep = tripleProcessingResult.get(y).getPepNumber();
		  					//System.out.println("x-val:" + xValue + "y-value: " + yValue);
		  						//.toLowerCase()									//allow discussion states to be repeated
		  					
		  					//Main thing here
		  					//compare each record with every other record one by one
		  					// if both labels match, pep # matches and 		  captured labels not in the list we want to allow to be repeated in final results
		  					if( xValue.equals(yValue) && xPep.equals(yPep) && valueNotInAllowRepeatedStates(xValue)) //&& (xPep==yPep) doesnt work	        				
			  	        	{
			  					//mark for deletion by adding to array
			  					//al.add(y); //--delete the second instance, not the first
		  						System.out.println("label repeated (xPep "+xPep+" + yPep "+yPep+") -- x: "+x + " y: "+ y + " xLabel: " + yValue);
	//		  					al.add(y); //--delete the second instance, not the first
		  						
		  						//in case when duplicate found (y part) and one is daniel state data labels, we delete daniel state data
		  						String xsentence = tripleProcessingResult.get(x).getCurrentSentence().toLowerCase();
		  						if (xsentence==null || xsentence.isEmpty())
		  							xsentence = " ";	//just to prevent anu null errors
		  						
		  						/* jan 2019, we dont need to delete daniel states, as they are the only states we consider now,  
		  						 * our own states capturing has uncertainity with dates so we rely on daniel data
		  						if (xsentence.toLowerCase().contains("status :")){		  							
		  							//if to be deleted record has reason, add it to current record
			  						String reason = tripleProcessingResult.get(y).getFinalReasonListArrayAsString();		  						
			  						//System.out.println("Reason: " + reason);
			  						
			  						//we save the main states and delete the other, we delete the y part, rather than the x part		  						
		  							tripleProcessingResult.remove(y);
			  						
			  						if (reason==null || reason.isEmpty() || reason.equals(""))			  						{}
			  						else{
			  							tripleProcessingResult.get(x).addToFinalReasonListArray(reason);
			  						//	System.out.println("Second matched reason not null Reason: " + reason);
			  						}
			  						System.out.println("\tRemoved Label : (" + xValue + ") Reason " + reason + " Sentence " + tripleProcessingResult.get(y).getCurrentSentence());
		  						}
		  						else{			  */ 						
		  							//if to be deleted record has reason, add it to current record
			  						String reason = tripleProcessingResult.get(x).getFinalReasonListArrayAsString();		  						
			  						//System.out.println("Reason: " + reason);
			  						
			  						//we save the main states and delete the other, we delete the y part, rather than the x part	
			  						//jan 2019, it seems this will just remove the first one, whereas we should remove the second one
		  							//tripleProcessingResult.remove(x);
			  						tripleProcessingResult.remove(y);
			  						
			  						if (reason==null || reason.isEmpty() || reason.equals(""))
			  						{}
			  						else{
			  							tripleProcessingResult.get(y).addToFinalReasonListArray(reason);
			  						//	System.out.println("Second matched reason not null Reason: " + reason);
			  						}
				  					System.out.println("\tRemoved Label : (" + xValue + ") Reason " + reason + " Sentence " + tripleProcessingResult.get(y).getCurrentSentence());
//$$		  						}
			  					if (x==0 || y==0){
			  						
			  					}
			  					else{
			  						y--;
			  						x--;
			  					}
			  				}  				
		  				}
		  			}
	  			}
	  			
	  			//do deletion from list array
	//  			for (Integer a: al){	
	//  				tripleProcessingResult.remove(a);
	//  			} 
	  	    }
  		}
  		catch(Exception e){
  			System.out.println("Exception " + e.toString());  			System.out.println(StackTraceToString(e)  );
  		}
  		return tripleProcessingResult;
    }
    
    
  //CHECK if label captured are repeated
    public static ArrayList <TripleProcessingResult> removePreviousStudyLabels(ArrayList <TripleProcessingResult> tripleProcessingResult) throws IOException{
    	System.out.println("Function removePreviousStudyLabels()");	
  		try		{
	  		if(!tripleProcessingResult.isEmpty()){
	  			int counter =0;
	  			for (int x=0; x <tripleProcessingResult.size(); x++){	
		  			for (int y=0; y <tripleProcessingResult.size(); y++){					        	
		  				if(x !=y && tripleProcessingResult.get(y).getClausIE() != null && !tripleProcessingResult.get(y).getClausIE().isEmpty()
		  						 && tripleProcessingResult.get(x).getClausIE() != null && !tripleProcessingResult.get(x).getClausIE().isEmpty())
		  				{
		  					String xValue = tripleProcessingResult.get(x).getClausIE().toLowerCase();
		  					String yValue = tripleProcessingResult.get(y).getClausIE().toLowerCase();
		  					Integer xPep = tripleProcessingResult.get(x).getPepNumber();
		  					Integer yPep = tripleProcessingResult.get(y).getPepNumber();		  				
		  					
		  					//System.out.println("x-val:" + xValue + "y-value: " + yValue);
		  						//.toLowerCase()									//allow discussion states to be repeated
		  					if( xValue.equals(yValue) && xPep.equals(yPep) && valueNotInAllowRepeatedStates(xValue)) //&& (xPep==yPep) doesnt work	        				
			  	        	{
		  						//get uppercase string and remove, we want all previous study results removed
		  						//but if they are the only ones tehre, we want to keep them and lowercase it
			  					String upperCase = tripleProcessingResult.get(y).getClausIE().toUpperCase();
		  						//find which one is uppercase and remove
		  						if  (xValue.toUpperCase().equals(upperCase)){
			  						System.out.println("\tRemoved label (xPep "+xPep+" + yPep "+yPep+") xLabel: " + yValue + " Sentence " + tripleProcessingResult.get(x).getCurrentSentence());
		  							tripleProcessingResult.remove(x);
		  						}
		  						else if(yValue.toUpperCase().equals(upperCase)){
			  						System.out.println("\tRemoved label (xPep "+xPep+" + yPep "+yPep+") xLabel: " + yValue + " Sentence " + tripleProcessingResult.get(y).getCurrentSentence());
		  							tripleProcessingResult.remove(y);
		  						}
		  						System.out.println("label repeated (xPep "+xPep+" + yPep "+yPep+") -- x: "+x + " y: "+ y + " xLabel: " + yValue);
	//		  					al.add(y); //--delete the second instance, not the first
			  					
			  					if (x==0 || y==0){
			  						
			  					}
			  					else{
			  						y--;
			  						x--;
			  					}
			  				}  				
		  				}
		  			}
	  			}
	  	    }
  		}
  		catch(Exception e){
  			System.out.println("Exception " + e.toString());  			System.out.println(StackTraceToString(e)  );
  		}
  		return tripleProcessingResult;
    }
    
    public static Boolean checkCurrentMessageHasMoreLabelsCaptured(Integer pep, Integer messageID) {
    	MysqlConnect mc = new MysqlConnect();
    	//String tableName= "results_postprocessed";
    	int counter=0;
    	try
		{
			Connection conn = mc.connect();			
			//divide into one pep at a time to give the machine less recordsets to work with at a time
			String sql0 = "select pep, messageid, count(*) as counter from "+resultsPostProcessedTableName+" where pep = "+pep+" and messageid  = "+messageID+";"; 
			Statement stmt0 = conn.createStatement();
			ResultSet rs0 = stmt0.executeQuery( sql0 );			
			//  Date: Thu Apr  1 00:28:01 2004	
			
			if (rs0.next()){
				counter = rs0.getInt("counter");
				if (counter >1)
				 return true;
			}
		}
    	catch(Exception e){
  			System.out.println("Exception " + e.toString());
  			System.out.println(StackTraceToString(e)  );
  		}
    	return false;
    }
    
	//also populate results_postprocessed table column for reasons extracted in nearby states
	//for each major state captured, identify the reasons nearby and then insert in the column next to the major column
    //2018 jan, we need to check also that if these must be from the same message, 
    //if the reason is from a previous message (use datetimestamp to make sure it is from before), then its not related to the event/state as it would be just a prediction by the member
    //so we only change position of reasona dn lesser states before/up than the label, if thats for same message
    //if the reason or lesser state if from a message which comes after the label matched message, and this can happen often, we can shift it up...but we have to see examples of this  first
    //but if they are from the same message, and especially the nearby sentences, we woudl be very sure of them being the labels reason
    //sql 
    
    
    public static ArrayList <TripleProcessingResult> extractReasonsPreviousPastCapturedStatesAndShiftLesserStatesUp_ForMajorStates(ArrayList <TripleProcessingResult> tripleProcessingResult, 
    		ArrayList<StatesReasons> sr) throws IOException{
    	System.out.println("Function extractReasonsPreviousPastCapturedStatesAndShiftLesserStatesUp_ForMajorStates()");	
    	
  		try
  		{
	  		if(tripleProcessingResult.isEmpty()){
	  			System.out.println("tripleProcessingResult ISEMPTY ");
	  			return tripleProcessingResult;
	  		}
  			int counter =0;
  			for (int x=0; x <tripleProcessingResult.size(); x++){  				
  				String xVal = tripleProcessingResult.get(x).getClausIE();
  				if(xVal != null && !xVal.isEmpty())
  					xVal=xVal.toLowerCase();
  				//only process if the captured label is one of the major states
				for (StatesReasons r : sr){					
					String stateToCheck = r.getState();	//contains states
					if(xVal.contains(stateToCheck)){
						//now check nearby labels for the same pep to get reasons...only check 5 states up and 5 states down
						
						//find minimum x value
						//here we compare the current record label, identified by position x, with 5 next records
						Integer startPos= 0,offset=x+5, endLIMIT=tripleProcessingResult.size();
						if(x<=5)
							startPos=0;
						else
							startPos=x-5;
						if(offset > endLIMIT)
							offset = endLIMIT;
						//we only check next 5 records
						for (int y=startPos; y < offset; y++){	
							
							//we compare the current record label, identified by position x, with 5 next records
			  				if(x !=y && tripleProcessingResult.get(y).getClausIE() != null && !tripleProcessingResult.get(y).getClausIE().isEmpty()
			  						 && tripleProcessingResult.get(x).getClausIE() != null && !tripleProcessingResult.get(x).getClausIE().isEmpty())
			  				{
			  					//String xValue = tripleProcessingResult.get(x).getClausIE().toLowerCase();
			  					String yValue = tripleProcessingResult.get(y).getClausIE().toLowerCase();
			  					Integer xPep  = tripleProcessingResult.get(x).getPepNumber();
			  					Integer yPep  = tripleProcessingResult.get(y).getPepNumber();
			  					
			  					//only labels from teh same message are shifted up
		  						//then we maybe later look at reason captures in offset records as we yteh result is jumbled up
			  					Integer xMessageID = tripleProcessingResult.get(x).getMessageID();
			  					Integer yMessageID = tripleProcessingResult.get(y).getMessageID();
			  					//the following function is not needed 
		  						//boolean currentMessageHasMoreLabelsCaptured = checkCurrentMessageHasMoreLabelsCaptured(xPep,mid);
			  					
			  					//only if the pep number and messageid is same, check for reason states
			  					//note, two different state data can be in same message but probably wont be captured as the second one is not a reason
			  					//but we can just make sure by checking uppercase as both would be uppercase
			  					if(xPep.equals(yPep)  && xMessageID.equals(yMessageID)) // && xPep.equals(yPep) && valueNotInAllowRepeatedStates(xValue)) //&& (xPep==yPep) doesnt work	        				
				  	        	{
			  						//if the second record has a reason captured, then we go ahead
			  						String reason = stateContainsReasons(sr,stateToCheck, yValue);
			  						//Integer mid = tripleProcessingResult.get(x).getMessageID();
			  						
				  					if(reason==null || reason.equals("") || reason.length()==0)
				  					{}
				  					else{
				  						//update reason column
				  						tripleProcessingResult.get(x).setReasonsInNearbyStates(reason);
				  						//we have to shift the reason state above..
				  						//actually no shifting required now..we just change datetimestamp to few seconds before
				  						Timestamp mainStateTimeStamp = tripleProcessingResult.get(x).getDateTimeStamp();
				  						int sec = -600;
				  				        Calendar cal = Calendar.getInstance();
				  				        cal.setTimeInMillis(mainStateTimeStamp.getTime());
				  				        cal.add(Calendar.SECOND, sec);
				  				        Timestamp before = new Timestamp(cal.getTime().getTime());
				  				        System.out.println("-------Changing Time ");
				  				        tripleProcessingResult.get(y).setDateTimeStamp(before);	//add new time
				  					}
				  					
				  					//shift states ..move the major state upper than the reason states 
				  					String lesserStatesExist = stateContainsLesserStates(sr,stateToCheck, yValue);
				  					if(lesserStatesExist==null || lesserStatesExist.equals("") || lesserStatesExist.length()==0 )
				  					{}
				  					else{
				  						//update reason column
				  						//we just minus some seconds from the timestamps so it will appear on top
				  						Timestamp mainStateTimeStamp = tripleProcessingResult.get(x).getDateTimeStamp();
				  						int sec = -600;
				  				        Calendar cal = Calendar.getInstance();
				  				        cal.setTimeInMillis(mainStateTimeStamp.getTime());
				  				        cal.add(Calendar.SECOND, sec);
				  				        Timestamp before = new Timestamp(cal.getTime().getTime());
				  				        System.out.println("-------Changing Time ");
				  				        tripleProcessingResult.get(y).setDateTimeStamp(before);	//add new time
				  					}
				  				}	
			  				}
			  			}
					}
				}
  					  			
  			}	  		
  		}
  		catch(Exception e){
  			System.out.println("Exception " + e.toString());
  			System.out.println(StackTraceToString(e)  );
  		}
  		return tripleProcessingResult;
    }
    
	private static String stateContainsReasons(ArrayList<StatesReasons> statereasons,String v_stateToCheck, String yValue) {
		String str = "";
		try{
			for(StatesReasons sr: statereasons)	{
				String state =  sr.getState();	//System.out.println("state "+state);
				String reason = sr.getReason();	//System.out.println("reason "+reason);
				//we want to match reasons for accepted state only
				if(state==null|| state.isEmpty() || state.length()==0 || reason==null || reason.isEmpty() || reason.length()==0) 
				{}
				else {
					if(state.equals(v_stateToCheck) && yValue.contains(reason)) {
						str = str+ reason+ ",";
					}
				}
			}
		}
		catch(Exception e){
			System.out.println(StackTraceToString(e)  );
		}
		return str;
	}
    
    //if you have both propose and draft, remove propose
	//else if you only have propose, change to draft
    public static ArrayList <TripleProcessingResult> combineResultLabels(ArrayList <TripleProcessingResult> tripleProcessingResult, String[] statesToRemoveChangeFromResults ) throws IOException{
    	//String statesToRemoveChangeFromResults[] = {"proposal-draft","draft_pep-draft"};
    	try
  		{
	  		if(!tripleProcessingResult.isEmpty()){
	  			int counter =0;
	  			for (int x=0; x <tripleProcessingResult.size(); x++){	
		  			for (int y=0; y <tripleProcessingResult.size(); y++){					        	
		  				if(x !=y && tripleProcessingResult.get(y).getClausIE() != null &&  !tripleProcessingResult.get(y).getClausIE().isEmpty()
		  						 && tripleProcessingResult.get(x).getClausIE() != null &&  !tripleProcessingResult.get(x).getClausIE().isEmpty())
		  				{
		  					String xValue = tripleProcessingResult.get(x).getClausIE().toLowerCase();
		  					String yValue = tripleProcessingResult.get(y).getClausIE().toLowerCase();
		  					Integer xPep = tripleProcessingResult.get(x).getPepNumber();
		  					Integer yPep = tripleProcessingResult.get(y).getPepNumber();		  				
		  					
		  					//if you have both propose and draft, remove propose
		  					//else if you only have propose, change to draft
		  						//.toLowerCase()									//allow discussion states to be repeated
		  					if( xValue.equals("draft") && (yValue.equals("propose") || yValue.equals("proposal") )  && xPep.equals(yPep) && valueNotInAllowRepeatedStates(xValue)) //&& (xPep==yPep) doesnt work	        				
			  	        	{		  						
		  						//if to be deleted record has reason, add it to current record
		  						String reason = tripleProcessingResult.get(y).getFinalReasonListArrayAsString();		  						
		  						//System.out.println("Reason: " + reason);
		  						if (reason!=null)
		  							tripleProcessingResult.get(x).addToFinalReasonListArray(reason);
		  						
		  						//remove propose
		  						System.out.println("\tRemoved label (xPep "+xPep+" + yPep "+yPep+") xLabel: " + yValue + " Sentence " + tripleProcessingResult.get(y).getCurrentSentence());
	  							tripleProcessingResult.remove(y);
		  						
	//		  					al.add(y); //--delete the second instance, not the first
			  					
			  					if (x==0 || y==0){
			  						
			  					}
			  					else{
			  						y--;
			  						x--;
			  					}
			  				}  				
		  				}
		  			}
	  			}
	  			//if both are not found in same pep, but only one found, change propose to draft
	  			for (int y=0; y <tripleProcessingResult.size(); y++){					        	
	  				if( tripleProcessingResult.get(y).getClausIE() != null && !tripleProcessingResult.get(y).getClausIE().isEmpty())
	  				{
	  					String yValue = tripleProcessingResult.get(y).getClausIE().toLowerCase();
	  					Integer yPep = tripleProcessingResult.get(y).getPepNumber();	  			
	  					//else if you only have propose, change to draft
	  						//.toLowerCase()									//allow discussion states to be repeated
	  					if(( yValue.equals("propose") || yValue.equals("proposal") ) ) //&& (xPep==yPep) doesnt work	        				
		  	        	{
	  						tripleProcessingResult.get(y).setClausIE("draft");	  					
		  				}  				
	  				}
	  			}
	  	    }
  		}
  		catch(Exception e){
  			System.out.println("Exception " + e.toString());
  			System.out.println(StackTraceToString(e)  );
  		}
  		return tripleProcessingResult;
    }
    
    //if x contains all terms in y,(all y in x)
    public static Boolean allIn(String xLabel, String yLabel){
    	//Boolean found = false;
    	if (xLabel != null && yLabel != null){
    		//x = x.replace("_", " "); 	//y = y.replace("_", " ");
    		String[] x = xLabel.split("_"),y = yLabel.split("_");
    		//if x contains all y   		
    			for (String y1 :y)  
        			if (!xLabel.contains(y1))
        				return false;
    	}
    	return true;
    }
    
    
    
    
    //The First Check
    //dont repeat sentences for the pep
  	//pass the current sentence to check against the all sentences captured for the pep
  	public Boolean checkRepeatedSentenceAndLabel(ArrayList <TripleProcessingResult> tripleProcessingResult, TripleProcessingResult result, String CurrentSentenceString, String v_idea, Integer v_pepNumber,
  			Integer v_library){  		
  		//we want to create the result of procesing all aspects of this sentence as an object
  		//TripleProcessingResult r = result;
//  		result.setMetadata(v_pepNumber, v_date, v_message_ID, v_idea, author, CurrentSentenceString);
  		//set default value as false
  		//r.setTripleMatched(false,libraryToCheck);
//  		System.out.println("=================Inside checkRepeatedSentenceAndLabel()");
  		
  		Boolean repeatedSentenceAndLabel = false;
  		//Boolean repeatedLabel = false;
  		
  		try {  			
	  		if(!tripleProcessingResult.isEmpty())	  		{
	 			System.out.println("candidate list not empty tripleProcessingResult.size(): "+ tripleProcessingResult.size());
	  			int counter =0;
				for (int y = 0; y < tripleProcessingResult.size(); y++) {
				
//					System.out.println("ArrayList Sentence " + tripleProcessingResult.get(y).currentSentence.toLowerCase() + " ArrayList Label" + tripleProcessingResult.get(y).label.toLowerCase());
					// candidateSentencesList.size() +
					// " CurrentSentenceString "+CurrentSentenceString);
					// String sections[] = temp.split("|");
					// String id = sections[0];
					// String CurrSen = sections[1];
					// && v_idea.toLowerCase().equals(id.toLowerCase())
					// CurrSen.toLowerCase()
					// InfoList.get(i).user.equals(a)
//pp					System.out.println("\n--------------------");
//pp					System.out.println("y counter "+ y+ " sentence: " +tripleProcessingResult.get(y).currentSentence.toLowerCase() + " clausIECapturedLabel " + tripleProcessingResult.get(y).clausIECapturedLabel);
					
					//clausIE
					
					if (   tripleProcessingResult.get(y).currentSentenceUnique != null && !tripleProcessingResult.get(y).currentSentenceUnique.isEmpty()
						&& tripleProcessingResult.get(y).clausIECapturedLabel  != null && !tripleProcessingResult.get(y).clausIECapturedLabel.isEmpty())
					{
						System.out.println("\t\tBoth not null ");
						//currentSentenceUnique is only populated once triples are matched
						if ( 
						   (tripleProcessingResult.get(y).currentSentenceUnique.toLowerCase().equals(CurrentSentenceString.toLowerCase())   ) 
						      && 
						   (tripleProcessingResult.get(y).getLabels(v_library) ==v_idea)
						   )
						{
							repeatedSentenceAndLabel = true;
							System.out.println("\t\tRepeated sentence: " + CurrentSentenceString.toLowerCase() + " "
									+ "\n\t\t and clausIECapturedLabel " + tripleProcessingResult.get(y).clausIECapturedLabel);
							//System.out.println("\t\tReturning ");
							
							//if matched even once,, return true
							return repeatedSentenceAndLabel;
							// System.out.println("Sentence and Idea FOUND in list " +
							// CurrentSentenceString);
						}
						else{
							System.out.println("Sentence and label are not repeated together, - one or both maybe different, Sentence: " + CurrentSentenceString.toLowerCase()  + " and ClausIECapturedLabel " + tripleProcessingResult.get(y).clausIECapturedLabel);							
						}
					}
					else{
						//System.out.println("\t\tEither the sentence or label is null, or both");
					}
					// if labels has been encountered before and is in list then some labels we want to allow repetition //&&
					// dontAllowRepetions.contains(v_idea.toLowerCase())
					
//errors here		if (tripleProcessingResult.get(y).label.toLowerCase().equals(v_idea.toLowerCase())) {
//						repeatedLabel = true;
						// System.out.println("set ProcessSentence =false 2");
						// System.out.println("Idea is duplicate in list " +
						// CurrentSentenceString);
//					}
					// System.out.println(" list not empty but not found in list");
				}
	  	        //if candidatelist is not empty, but does not have that sentence, then add taht sentence
	  			
				// below code not needed
				/*
				 * if(found==false){ // System.out.println(
				 * "Candidate list not empty - SENTENCE NOT FOUND in list - and added "
				 * ); tripleProcessingResult k = new tripleProcessingResult(CurrentSentenceString.toLowerCase(), v_idea.toLowerCase()); tripleProcessingResult.add(k);
				 * //v_idea+"|"+
				 * //candidateSentencesList.add(CurrentSentenceString.toLowerCase * ()); }
				 */				
			}
	  		else{
	  			System.out.println("ArrayList Empty "); 
	  		}
		/*
		 * else { // System.out.println("here y"); //
		 * System.out.println("SENTENCE NOT FOUND in list and added ");
		 * tripleProcessingResult k = new
		 * tripleProcessingResult(CurrentSentenceString.toLowerCase(),
		 * v_idea.toLowerCase()); tripleProcessingResult.add(k); //v_idea+"|"+
		 * //candidateSentencesList.add(CurrentSentenceString.toLowerCase()); }
		 */
	  		
  		}
  		catch (NullPointerException e) {
            System.out.print("Caught the NullPointerException 280");
        }
  		catch (Exception e){
  			System.out.println("Exception 280 " + e.toString());   			System.out.println(StackTraceToString(e)  );
  		}  		
  		
  		return repeatedSentenceAndLabel;
  	}
  	
    public Boolean checkRepeatedSentenceAndFinalLabel(ArrayList <TripleProcessingResult> tripleProcessingResult, TripleProcessingResult result, String CurrentSentenceString, String v_idea, Integer v_pepNumber,
		Integer v_library){
  		
  		//we want to create the result of procesing all aspects of this sentence as an object
  		//TripleProcessingResult r = result;
//  		result.setMetadata(v_pepNumber, v_date, v_message_ID, v_idea, author, CurrentSentenceString);
  		//set default value as false
  		//r.setTripleMatched(false,libraryToCheck);
//  		System.out.println("=================Inside checkRepeatedSentenceAndLabel()");
  		
  		Boolean repeatedSentenceAndLabel = false;
  		//Boolean repeatedLabel = false;
  		
  		try {  			
	  		if(!tripleProcessingResult.isEmpty()) 		{
	 			System.out.println("candidate list not empty tripleProcessingResult.size(): "+ tripleProcessingResult.size());
	  			int counter =0;
				for (int y = 0; y < tripleProcessingResult.size(); y++) 	{
				
//					System.out.println("ArrayList Sentence " + tripleProcessingResult.get(y).currentSentence.toLowerCase() + " ArrayList Label" + tripleProcessingResult.get(y).label.toLowerCase());
					// candidateSentencesList.size() +
					// " CurrentSentenceString "+CurrentSentenceString);
					// String sections[] = temp.split("|");
					// String id = sections[0];
					// String CurrSen = sections[1];
					// && v_idea.toLowerCase().equals(id.toLowerCase())
					// CurrSen.toLowerCase()
					// InfoList.get(i).user.equals(a)
//pp					System.out.println("\n--------------------");
//pp					System.out.println("y counter "+ y+ " sentence: " +tripleProcessingResult.get(y).currentSentence.toLowerCase() + " clausIECapturedLabel " + tripleProcessingResult.get(y).clausIECapturedLabel);
					
					//clausIE
					
					if (   tripleProcessingResult.get(y).currentSentenceUnique != null  && !tripleProcessingResult.get(y).currentSentenceUnique.isEmpty()
						&& tripleProcessingResult.get(y).clausIECapturedLabel  != null  && !tripleProcessingResult.get(y).clausIECapturedLabel.isEmpty())
					{
						System.out.println("\t\tBoth not null ");
						//currentSentenceUnique is only populated once triples are matched
						if ( (tripleProcessingResult.get(y).currentSentenceUnique.toLowerCase().equals(CurrentSentenceString.toLowerCase())   ) 
						      && (tripleProcessingResult.get(y).getLabels(v_library) ==v_idea)  )   
						{
							repeatedSentenceAndLabel = true;
							System.out.println("\t\tRepeated sentence: " + CurrentSentenceString.toLowerCase() + " "
									+ "\n\t\t and clausIECapturedLabel " + tripleProcessingResult.get(y).clausIECapturedLabel);
							//System.out.println("\t\tReturning ");
							
							//if matched even once,, return true
							return repeatedSentenceAndLabel;
							// System.out.println("Sentence and Idea FOUND in list " +
							// CurrentSentenceString);
						}
						else{
							System.out.println("Sentence and label are not repeated together, - one or both maybe different, Sentence: " + CurrentSentenceString.toLowerCase()  + " and ClausIECapturedLabel " + tripleProcessingResult.get(y).clausIECapturedLabel);							
						}
					}
					else{
						//System.out.println("\t\tEither the sentence or label is null, or both");
					}
					// if labels has been encountered before and is in list then some labels we want to allow repetition //&&
					// dontAllowRepetions.contains(v_idea.toLowerCase())
					
//errors here		if (tripleProcessingResult.get(y).label.toLowerCase().equals(v_idea.toLowerCase())) {
//						repeatedLabel = true;
						// System.out.println("set ProcessSentence =false 2");
						// System.out.println("Idea is duplicate in list " +
						// CurrentSentenceString);
//					}
					// System.out.println(" list not empty but not found in list");
				}
	  	        //if candidatelist is not empty, but does not have that sentence, then add taht sentence
	  			
				// below code not needed
				/*
				 * if(found==false){ // System.out.println(
				 * "Candidate list not empty - SENTENCE NOT FOUND in list - and added "
				 * ); tripleProcessingResult k = new tripleProcessingResult(CurrentSentenceString.toLowerCase(), v_idea.toLowerCase()); tripleProcessingResult.add(k);
				 * //v_idea+"|"+
				 * //candidateSentencesList.add(CurrentSentenceString.toLowerCase * ()); }
				 */				
			}
	  		else{
	  			System.out.println("ArrayList Empty "); 
	  		}
		/*
		 * else { // System.out.println("here y"); //
		 * System.out.println("SENTENCE NOT FOUND in list and added ");
		 * tripleProcessingResult k = new
		 * tripleProcessingResult(CurrentSentenceString.toLowerCase(),
		 * v_idea.toLowerCase()); tripleProcessingResult.add(k); //v_idea+"|"+
		 * //candidateSentencesList.add(CurrentSentenceString.toLowerCase()); }
		 */
	  		
  		}
  		catch (NullPointerException e) {            System.out.print("Caught the NullPointerException 280");        }
  		catch (Exception e){
  			System.out.println("Exception 280 " + e.toString());   			System.out.println(StackTraceToString(e)  );
  		}  		  		
  		return repeatedSentenceAndLabel;
  	}

    public static void outputMinimalListToFile(ArrayList <TripleProcessingResult> tripleProcessingResult, FileWriter fw,ApachePOIExcelWrite ew) throws IOException
{  	
	//Date date = new Date();
	//SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
	
	
	//Boolean repeatedSentence = false;
	//Boolean found = true;
	try
	{
		fw.append("pep| author| timestamp|mid|#PEPsInMsg| label| currentSentence| clausie| role| reason \n");
		if(!tripleProcessingResult.isEmpty())
		{				
			for (int y=0; y <tripleProcessingResult.size(); y++)
			{	     	        		
				//repeatedLabel|ps|ns|pp|ep|np
				fw.append(tripleProcessingResult.get(y).getPepNumber().toString());
				fw.append('|');	
				//System.out.println("a");
				if (tripleProcessingResult.get(y).getAuthor()==null){
					fw.append('|');
				}
				else{
					fw.append(tripleProcessingResult.get(y).getAuthor().toString());
					fw.append('|');
				}
				//System.out.println("b");
				if (tripleProcessingResult.get(y).getDateTimeStamp().toString()==null){
					fw.append('|');
				}
				else{
					fw.append(tripleProcessingResult.get(y).getDateTimeStamp().toString());
					fw.append('|');
				}
				fw.append('|');	
				//System.out.println("c");
				
				if (tripleProcessingResult.get(y).getMessageID()==null)
				{	
					fw.append("");
				}
				else {
					fw.append(tripleProcessingResult.get(y).getMessageID().toString());
				}
				
				fw.append('|');
				fw.append(tripleProcessingResult.get(y).getNumberOfPEPsMentionedInMessage().toString());
				fw.append('|');
				//System.out.println("d");
				fw.append(tripleProcessingResult.get(y).getLabel().toString());
				fw.append('|');
				//System.out.println("e");
				fw.append(tripleProcessingResult.get(y).getCurrentSentence().toString());					
				fw.append('|');
				//System.out.println("e");
				//fw.append(tripleProcessingResult.get(y).getReverb());
				//fw.append('|');					
				fw.append(tripleProcessingResult.get(y).getClausIE());
				fw.append('|');
				//fw.append(tripleProcessingResult.get(y).getOllie());
//				if (tripleProcessingResult.get(y).getRole()==null){
					
//				}
//				else{
					//fw.append('|');
					fw.append(tripleProcessingResult.get(y).getRole());
					fw.append('|');	
					//fw.append(tripleProcessingResult.get(y).getFinalReason());
					fw.append(tripleProcessingResult.get(y).getFinalReasonListArrayAsString().toLowerCase());
					
//					System.out.println("z");
//				}
				fw.append('\n');
				//fw.append('|');
			}
	    }
		fw.close();			
		System.out.println("CSV File is created successfully.");
	}
	catch (Exception e){
			System.out.println("Exception 281 (FILE) " + e.toString());			System.out.println(StackTraceToString(e)  );	
		}
	}

	public static String StackTraceToString(Exception ex) {
		String result = ex.toString() + "\n";
		StackTraceElement[] trace = ex.getStackTrace();
		for (int i=0;i<trace.length;i++) {
			result += trace[i].toString() + "\n";
		}
		return result;
	}

	public static void outputListToFile(ArrayList <TripleProcessingResult> tripleProcessingResult, FileWriter fw) throws IOException
	{  	
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
		String filename = "C:\\Users\\psharma\\Google Drive\\PhDOtago\\scripts\\postProcessResults"+  dateFormat.format(date)  +".csv";
		
		//"C:\\Users\\psharma\\Google Drive\\PhDOtago\\scripts\\postProcessResults\\postProcessResults"+  dateFormat.format(date)  +".xls"
		
		//WritableWorkbook workbook = Workbook.createWorkbook(new File("C:\\Users\\psharma\\Google Drive\\PhDOtago\\scripts\\postProcessResults\\postProcessResults"+  dateFormat.format(date)  +".xls"));
		//WritableSheet sheet = workbook.createSheet("Results", 0);
		
		Boolean repeatedSentence = false, found = true;
		try		{
			fw.append("pep| author| timestamp|mid|#PEPsInMsg| label| currentSentence| clausie| role| reason|repeatedLabel|ps|ns|pp|ep|np \n");
			if(!tripleProcessingResult.isEmpty())			{				
				for (int y=0; y <tripleProcessingResult.size(); y++)	{	     	        		
					//repeatedLabel|ps|ns|pp|ep|np
					fw.append(tripleProcessingResult.get(y).getPepNumber().toString());
					fw.append('|');	
					//System.out.println("a");
					if (tripleProcessingResult.get(y).getAuthor()==null)
						fw.append('|');					
					else{
						fw.append(tripleProcessingResult.get(y).getAuthor().toString());
						fw.append('|');
					}
					//System.out.println("b");
					fw.append(tripleProcessingResult.get(y).getDateTimeStamp().toString());
					fw.append('|');	
					//System.out.println("c");
					
					if (tripleProcessingResult.get(y).getMessageID()==null)						
						fw.append("");					
					else 
						fw.append(tripleProcessingResult.get(y).getMessageID().toString());					
					
					fw.append('|');
					fw.append(tripleProcessingResult.get(y).getNumberOfPEPsMentionedInMessage().toString());
					fw.append('|');
					//System.out.println("d");
					fw.append(tripleProcessingResult.get(y).getLabel().toString());
					fw.append('|');
					//System.out.println("e");
					fw.append(tripleProcessingResult.get(y).getCurrentSentence().toString());					
					fw.append('|');
					//System.out.println("e");
					//fw.append(tripleProcessingResult.get(y).getReverb());
					//fw.append('|');					
					fw.append(tripleProcessingResult.get(y).getClausIE());
					fw.append('|');
					//fw.append(tripleProcessingResult.get(y).getOllie());
//					if (tripleProcessingResult.get(y).getRole()==null){
						
//					}
//					else{
						//fw.append('|');
						fw.append(tripleProcessingResult.get(y).getRole());
						fw.append('|');	
						//fw.append(tripleProcessingResult.get(y).getFinalReason());
						fw.append(tripleProcessingResult.get(y).getFinalReasonListArrayAsString().toLowerCase());
						fw.append('|');	
						fw.append(tripleProcessingResult.get(y).getIfRepeatedFoundLabel().toString());
						fw.append('|');					
						fw.append(tripleProcessingResult.get(y).getPrevSentence());
						fw.append('|');	
						fw.append(tripleProcessingResult.get(y).getNextSentence());
						fw.append('|');	
						fw.append(tripleProcessingResult.get(y).getPrevParagraph());
						fw.append('|');	
						fw.append(tripleProcessingResult.get(y).getCurrParagraph());
						fw.append('|');	
						fw.append(tripleProcessingResult.get(y).getNextParagraph());
//						System.out.println("z");
//					}
					fw.append('\n');
					//fw.append('|');
				}
		    }
			fw.close();	System.out.println("CSV File is created successfully.");
		}
		catch (Exception e){
  			System.out.println("Exception 281 (FILE) " + e.toString());   			System.out.println(StackTraceToString(e)  );
  		}
	}
	
	public static void outputListToDatabase(ArrayList <TripleProcessingResult> tripleProcessingResult, Connection conn) throws IOException
	{  		
		Boolean repeatedSentence = false,found = true;	
		try		{
			/*  StringTokenizer tokens = new StringTokenizer(data, ",");
			    String[] fields = new String[4];
			    for (int i = 0; i < fields.length; i++) {
			        fields[i] = stripQuotes(tokens.nextToken());
			    } 
			 */
					
			
//			fw.append("pep| author| date|mid| label| currentSentence| reverb| clausie| ollie| role| reason|repeatedLabel|ps|ns|pp|ep|np \n");
			if(!tripleProcessingResult.isEmpty())			{				
				for (int y=0; y <tripleProcessingResult.size(); y++)				{	
					String sql = "INSERT INTO results_postprocessed (pep, messageID, author,authorRole, folder, date, label,subject, relation, object, "
							+ "currentSentence, reverb, clausie, ollie, repeatedLabel,ps,ns,pp,ep,np,"
							+ "role, reasonsInSentence,allReasons,reasonsInNearbyStates,reasonTermsInMatchedTriple,"
							+ "reasonTermsInNearbySentencesParagraphs, reasonTriplesInNearbySentencesParagraphs,numberOfPEPsMentionedInMessage,timestamp,"
							+ "labelFoundInMsgSubject,isFirstParagraph, isLastParagraph) "
							+ "VALUES (?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,? )";			
					PreparedStatement statement = conn.prepareStatement(sql);				
					
					statement.setInt(1, tripleProcessingResult.get(y).getPepNumber());
					
					if (tripleProcessingResult.get(y).getMessageID()!= null){	statement.setInt(2, tripleProcessingResult.get(y).getMessageID());					}
					else{		statement.setString(2,null); 					}
					if (tripleProcessingResult.get(y).getAuthor()!=null){		statement.setString(3, tripleProcessingResult.get(y).getAuthor());					}
					else{		statement.setString(3,null);					}	
					
					if (tripleProcessingResult.get(y).getAuthorsRole()!=null){		statement.setString(4, tripleProcessingResult.get(y).getAuthorsRole());					}
					else{		statement.setString(4,null);					}	
					
					if (tripleProcessingResult.get(y).getFolder()!=null){		statement.setString(5, tripleProcessingResult.get(y).getFolder());	}
					else{		statement.setString(5,null);					}
					
					//sqlDate ...	
					java.util.Date utilDate = tripleProcessingResult.get(y).getDate();
					java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());	
					java.sql.Date finalDate = sqlDate;		
					
					if(finalDate != null){	statement.setDate(6, finalDate);	}
					else{		statement.setString(6,null);					}					
					
					if(tripleProcessingResult.get(y).getLabel() != null){			statement.setString(7,tripleProcessingResult.get(y).getLabel());					}
					else{		statement.setString(7,null);					}
					
					if(tripleProcessingResult.get(y).getLabelSubject() != null){	statement.setString(8,tripleProcessingResult.get(y).getLabelSubject());					}
					else{		statement.setString(8,null);					}
					
					if(tripleProcessingResult.get(y).getLabelRelation() != null){	statement.setString(9,tripleProcessingResult.get(y).getLabelRelation());				}
					else{		statement.setString(9,null);					}
					
					if(tripleProcessingResult.get(y).getLabelObject() != null){		statement.setString(10,tripleProcessingResult.get(y).getLabelObject());					}
					else{		statement.setString(10,null);					}
									
					if(tripleProcessingResult.get(y).getCurrentSentence() != null){						statement.setString(11,tripleProcessingResult.get(y).getCurrentSentence());					}
					else{						statement.setString(11,null);					}
										
					if(tripleProcessingResult.get(y).getReverb() != null){				statement.setString(12,tripleProcessingResult.get(y).getReverb());					}
					else{						statement.setString(12,null);					}
					
					if(tripleProcessingResult.get(y).getClausIE() != null){			statement.setString(13,tripleProcessingResult.get(y).getClausIE());					}
					else{						statement.setString(13,null);					}
					
					if(tripleProcessingResult.get(y).getOllie() != null){			statement.setString(14,tripleProcessingResult.get(y).getOllie());					}
					else{						statement.setString(14,null);					}
					
					//repeatedLabel,ps,ns,pp,ep,np, role, reason
					if(tripleProcessingResult.get(y).getIfRepeatedFoundLabel() != null){	statement.setBoolean(15,tripleProcessingResult.get(y).getIfRepeatedFoundLabel());					}
					else{						statement.setString(15,null);					}
					
					if(tripleProcessingResult.get(y).getPrevSentence() != null){	statement.setString(16,tripleProcessingResult.get(y).getPrevSentence());					}
					else{				statement.setString(16,null);					}
					
					if(tripleProcessingResult.get(y).getNextSentence() != null){		statement.setString(17,tripleProcessingResult.get(y).getNextSentence());					}
					else{						statement.setString(17,null);					}
					
					if(tripleProcessingResult.get(y).getPrevParagraph() != null){		statement.setString(18,tripleProcessingResult.get(y).getPrevParagraph());					}		
					else{						statement.setString(18,null);					}
					
					if(tripleProcessingResult.get(y).getCurrParagraph() != null){		statement.setString(19,tripleProcessingResult.get(y).getCurrParagraph());					}		
					else{						statement.setString(19,null);					}
					
					if(tripleProcessingResult.get(y).getNextParagraph() != null){		statement.setString(20,tripleProcessingResult.get(y).getNextParagraph());					}
					else{						statement.setString(20,null);					}
					
					if(tripleProcessingResult.get(y).getRole() != null){			statement.setString(21,tripleProcessingResult.get(y).getRole());					}
					else{						statement.setString(21,null);					}
					
					if(tripleProcessingResult.get(y).getReasonsInSentence() != null){			statement.setString(22,tripleProcessingResult.get(y).getReasonsInSentence()  );					}	
					else{						statement.setString(22,null);					}
					
					if(tripleProcessingResult.get(y).getFinalReasonListArrayAsString() != null){	statement.setString(23,tripleProcessingResult.get(y).getFinalReasonListArrayAsString());					}	
					else{						statement.setString(23,null);				}
					
					//reasons
					//reasonsInNearbyStates
					if(tripleProcessingResult.get(y).getReasonsInNearbyStates() != null){		statement.setString(24,tripleProcessingResult.get(y).getReasonsInNearbyStates());				}	
					else{					statement.setString(24,null);					}
					
					
					if(tripleProcessingResult.get(y).getReasonTermsInMatchedTriple() != null){	statement.setString(25,tripleProcessingResult.get(y).getReasonTermsInMatchedTriple());				}	
					else{					statement.setString(25,null);					}
					if(tripleProcessingResult.get(y).getReasonTermsInNearbySentencesParagraphs() != null){
						statement.setString(26,tripleProcessingResult.get(y).getReasonTermsInNearbySentencesParagraphs());
					}	
					else{					statement.setString(26,null);					}
					if(tripleProcessingResult.get(y).getReasonTriplesInNearbySentencesParagraphs() != null){
						statement.setString(27,tripleProcessingResult.get(y).getReasonTriplesInNearbySentencesParagraphs());
					}	
					else{					statement.setString(27,null);					}
					
					if(tripleProcessingResult.get(y).getNumberOfPEPsMentionedInMessage() != null){
						statement.setInt(28,tripleProcessingResult.get(y).getNumberOfPEPsMentionedInMessage());
					}	
					else{					statement.setString(28,null);					}
					
					if(tripleProcessingResult.get(y).getDateTimeStamp() != null){
						statement.setTimestamp(29,tripleProcessingResult.get(y).getDateTimeStamp());
					}	
					else{				statement.setString(29,null);					}
					//dec 2018
					if(tripleProcessingResult.get(y).getMsgSubjectContainsLabel() == null){
						statement.setBoolean(30,tripleProcessingResult.get(y).getMsgSubjectContainsLabel());
					}	
					else{				statement.setBoolean(30,false);					}
					
					if(tripleProcessingResult.get(y).isFirstParagraph() == null){
						statement.setBoolean(31,tripleProcessingResult.get(y).isFirstParagraph());
					}	
					else{				statement.setBoolean(31,false);					}
					if(tripleProcessingResult.get(y).isLastParagraph() == null){
						statement.setBoolean(32,tripleProcessingResult.get(y).isLastParagraph());
					}	
					else{				statement.setBoolean(32,false);					}
									
					int rowsInserted = statement.executeUpdate();						
				}
		    }
					
			System.out.println("Post-processed Results inserted in Database successfully.");
		}
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (Exception e){
  			System.out.println("Exception 282 (DATABASE) " + e.toString());   			System.out.println(StackTraceToString(e)  );
  		}
	}
	
	public static ArrayList<Integer> getAllPeps(Connection conn){
		ArrayList<Integer> list =new ArrayList<Integer>();
		try
		{
			Statement stmt = null;			stmt = conn.createStatement();
			String query = "SELECT distinct pep FROM pepdetails ";
			Statement st = conn.createStatement();			ResultSet rs = st.executeQuery(query);
			Integer pepNumber;
			// iterate through the java resultset
			if (rs.next())   {
				pepNumber = rs.getInt("pep");			//	author = rs.getString("author");
				//System.out.format("%s, %s\n", pepNumber, author);
				list.add(pepNumber);
			}
			st.close();
		}  catch (Exception e)	    {
			System.err.println("Got an exception! ");
			System.err.println(e.getMessage());
		}
		
		//if author has comma, meaning multiple authors, then insert in authors array
		//String authorFiltered = "";		
		// if it has brackets
		//paul@prescod.net (Paul Prescod)
		///if (author.contains("(") && author.contains(")")) {
		//	authorFiltered = author.substring(author.indexOf("(")+1,author.indexOf(")"));
		//	author = authorFiltered;
		//}
		
		return list;
	}
	
	private String stripQuotes(String input) {
	    StringBuffer output = new StringBuffer();
	    for (int i = 0; i < input.length(); i++) {
	        if (input.charAt(i) != '\"') {
	            output.append(input.charAt(i));
	        }
	    }
	    return output.toString();
	}
	public void finalize() {	    //System.out.println("Probability instance is getting destroyed"); 
	}
}
