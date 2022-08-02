package postProcess;

import java.io.File;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import Process.processLabels.TripleProcessingResult;
import Read.readMetadataFromWeb.GetProposalDetailsWebPage;
import connections.MysqlConnect;
import excelwrite.ApachePOIExcelWrite;

import java.sql.ResultSet;
import java.sql.SQLException;

import postProcess.pair;

				//ResultsFromDBToCSVFile 

//march 2020 run this SQL script to do the state data transfer
// main query to transfer state data
// UPDATE results set label = SUBSTRING( currentsentence,10)
// WHERE messageid > 5000000 AND messageid < 6000000
//ALos insert redraft values from previous results using 'mar2020_redraft.sql'

//Feb 2020, we have found some anomalies in the state data. 
//These are PEPs for which the states start from rejected..eg PEP 259 as states in the thesis
//So we add code to omit these PEPs

//dec 2018, added code to not select pep summary messages - they have messageids 6000000
//dec 2018, since we decided to use commit state data instead, we have an array cjheck which will prevent selection of these states
//dec 2018, added code to output excel sheet containing Main States only
public class PostProcess{		
	
	static Boolean onlyBetweenStates = false;	//when we only want data in between 2 states
//	static String startState = "draft", endState = "accepted";
	//change tablenames 
	static String resultsTableName = "results"; //_dec2018b	//main selection table results
	static String resultsPostProcessedTableName = "results_postprocessed"; //_dec2018	//main selection table results_postprocessed
	//do we want to include Daniel data or not?
	//Danile data is added at tyhe end of postprocessing and will be merged
	//merged meaning if for example, accepted state is there, then it wont add, but if it isnt, it will add
	static boolean mergeDanielData = true;	
	// for each pep, create a TripleProcessingResult object 
	// list of triple processing result object which contains candidate
	// sentences as well
	static ArrayList<TripleProcessingResult> tripleProcessingResult = new ArrayList<TripleProcessingResult>();
	// clean this arraylist after each pep		
	static ApachePOIExcelWrite ew = new ApachePOIExcelWrite();//write to excel	
	// mar 2020 ...added withdrawn to both sets
	static String mainStates[]  = {"draft","open","active", "pending", "closed", "final","accepted", "deferred","replaced","rejected","postponed","incomplete","superseded","provisional","withdrawn","finished"};	
	//dec 2018...since we already have these states from daniel commit data, we dont include these at al --- also messes up the diagram
	static String dontConsiderStates[]  = {"draft","open","active", "pending", "closed", "final","accepted", "deferred","replaced","rejected","postponed","incomplete","superseded","withdrawn","finished"};
	
	//March 2020 remove some substates 
	static String removeSubstatesList[] = {"uncontroversial","check_in_pep","concerns_addressed","renamed","idea_postponed","co_bdfl_delegate_accepted_pep","implemented","small_edits_left"};
	
	//june 2020...sone substates to uppercase
	static String uppercaseSubStates[] = {"BDFL"};
	
	//Feb 2020
	//ignore these PEPs as they have states in wrong order
	static Boolean ignorePEPs = true;
	static Integer ignorePEPsList[] = {100, 508};
	
	//juggle list
	static ArrayList<pair> juggleList = new ArrayList<pair>();
	//March 2020 rename some substates 
	static ArrayList<pair> renameSpecificSubstatesForPEPs = new ArrayList<pair>();
	
	//March 2020 remove some substates  for some peps	
	static ArrayList<pairp> removeSpecificSubstatesForPEPs = new ArrayList<pairp>();
	
	//june 2020 further clean results
	static ArrayList<pair> cleanSubstatesForPEPs = new ArrayList<pair>();
	 
	static ArrayList<pair> immediatejuggleList = new ArrayList<pair>();
	
	//March 2020 rename some substates 
	
	// OutputPEPResultsToCSV(Boolean showRepeatedLabel)
	public static void main(String[] args) {	
		
		//March 2020 rename some substates 
		//ArrayList<pair> renameSpecificSubstatesForPEPs = new ArrayList<pair>();
		 pair b0 = new pair("willing_bdfl", "member_volunteers_BDFL_delegate");		renameSpecificSubstatesForPEPs.add(b0);
		 pair b = new pair("member_willing_to_be_bdfl_delegate", "member_volunteers_BDFL_delegate");		renameSpecificSubstatesForPEPs.add(b);
		 pair b1 = new pair("bdfl_allocated", "bdfl_delegate_allocated");				renameSpecificSubstatesForPEPs.add(b1);
		 pair b2 = new pair("bdfl_asked_volunteer", "bdfl_asked_delegate_volunteer");	renameSpecificSubstatesForPEPs.add(b2);
		 pair b3 = new pair("ready_review", "ready_bdfl_review");	renameSpecificSubstatesForPEPs.add(b3);
		 pair b4 = new pair("2nd Draft", "ReDraft");	renameSpecificSubstatesForPEPs.add(b4);
		 pair b5 = new pair("3rd Draft", "ReDraft");	renameSpecificSubstatesForPEPs.add(b5);	
		 pair b6 = new pair("4th Draft", "ReDraft");	renameSpecificSubstatesForPEPs.add(b6);
		 pair b7 = new pair("5th Draft", "ReDraft");	renameSpecificSubstatesForPEPs.add(b7);
		 pair b8 = new pair("6th Draft", "ReDraft");	renameSpecificSubstatesForPEPs.add(b8);	
		 pair b9 = new pair("7th Draft", "ReDraft");	renameSpecificSubstatesForPEPs.add(b9);	
		 pair b9B = new pair("bdfl_delegate_accepted_pep", "bdfl_delegate_accepted");	renameSpecificSubstatesForPEPs.add(b9B);		
		 //pair p1 = new pair("withdrawn_by_author", "rejected_by_author");	     			pList.add(p1);
		 //pair p2 = new pair("positive_feedback", "idea_received_positively");	     			pList.add(p2);
		 
		 //oct 2020
		 pair b9C = new pair("poll_results_majority", "majority");	renameSpecificSubstatesForPEPs.add(b9C);
		 pair b9C1 = new pair("poll_results_no_majority", "no_majority");	renameSpecificSubstatesForPEPs.add(b9C1);
		 pair b9D = new pair("vote_results_majority", "majority");	renameSpecificSubstatesForPEPs.add(b9D);
		 pair b9D1 = new pair("vote_result_no_majority", "no_majority");	renameSpecificSubstatesForPEPs.add(b9D1);
		
		//juggle list.. declare the pairs that neeed to be exchanged
		//parameter 1 = that should be after...parameter2 = that should be before but is currently after
		pair p = new pair("accepted", "bdfl_pronouncement_accepted");	juggleList.add(p);
		pair p1 = new pair("accepted", "consensus");	     			juggleList.add(p1);
		pair p2 = new pair("rejected", "bdfl_pronouncement_rejected"); 	juggleList.add(p2);
		pair p3 = new pair("rejected", "consensus");	      			juggleList.add(p3);
		pair p3b = new pair("rejected", "no_consensus");	      		juggleList.add(p3b);
		pair p4 = new pair("rejected", "no_support"); 					juggleList.add(p4);
		pair p5 = new pair("rejected", "rejected_by_author"); 			juggleList.add(p5);
		pair p6 = new pair("withdrawn", "withdrawn_by_author");			juggleList.add(p6);
		//added march 2020
		pair p7 = new pair("rejected", "not_enough_support");	    	juggleList.add(p7);
		pair p8 = new pair("rejected", "no_support"); 					juggleList.add(p8);
		pair p9 = new pair("rejected", "lazy_consensus");	      		juggleList.add(p9);
		pair p10 = new pair("assigned_pep_number", "request_pep_number");	      		juggleList.add(p10);
		pair p11 = new pair("bdfl_delegate_accepted_pep", "member_volunteers_bdfl_delegate");	      		juggleList.add(p11);
		pair p12 = new pair("accepted", "co_bdfl_delegate_accepted_pep");	      		juggleList.add(p12);
		pair p13 = new pair("bdfl_allocated", "willing_bdfl");	      		juggleList.add(p13);
		pair p14 = new pair("bdfl_pronouncement_rejected", "no_consensus");	      		juggleList.add(p14);
		pair p15 = new pair("co_bdfl_delegate_accepted_pep", "bdfl_pronouncement_accepted");	      		juggleList.add(p15);
		pair p16 = new pair("bdfl_allocated", "willing_bdfl");	      		juggleList.add(p16);
		pair p17 = new pair("bdfl_delegate_reviewed", "ready_bdfl_review");	      		juggleList.add(p17);
		pair p18 = new pair("bdfl_delegate_accepted_pep", "consensus");	      		juggleList.add(p18);
		pair p19 = new pair("bdfl_delegate_accepted_pep", "bdfl_delegate_reviewed");	      		juggleList.add(p19);
		pair p29b = new pair("withdrawn", "no_consensus");	      		juggleList.add(p29b);
		pair p29c = new pair("ready_bdfl_review", "redraft");	      		juggleList.add(p29c);
		pair p29d = new pair("active", "consensus");						juggleList.add(p29d);
		//pair p29c = new pair("final", "positive_feedback");	      		juggleList.add(p29c);
		//pair p14 = new pair("bdfl_pronouncement_rejected", "no_consensus");	      		pList.add(p14);
		
		//june 2002 added...for 
		pair p20 = new pair("consensus", "ready_bdfl_review");	      		juggleList.add(p20);
		pair p21 = new pair("withdrawal", "no_consensus");	      		juggleList.add(p21);
		pair p22 = new pair("consensus", "requests_feedback");	      		juggleList.add(p22);
		pair p23 = new pair("bdfl_delegate_accepted_pep", "redraft");	      		juggleList.add(p23);
		pair p24 = new pair("bdfl_pronouncement_accepted", "redraft");	      		juggleList.add(p24);
		pair p25 = new pair("consensus", "active");	      		juggleList.add(p25);
		//try do this again..it isnt working 
		//pair p25 = new pair("assigned_pep_number", "request_pep_number");	      		juggleList.add(p25);
		
		//June 2020 ... immediate jugglelist ..only swap immediate results
		pair im1 = new pair("final", "positive_feedback");				immediatejuggleList.add(im1);
		pair im2 = new pair("final", "no_objections");	      			immediatejuggleList.add(im2);
		pair im3 = new pair("final", "bdfl_pronouncement_rejected");	immediatejuggleList.add(im3);
		pair im4 = new pair("accepted", "bdfl_pronouncement_accepted");	immediatejuggleList.add(im4);
		pair im5 = new pair("consensus", "active");						immediatejuggleList.add(im5);
		pair im6 = new pair("deferred", "no_consensus");				immediatejuggleList.add(im6);
		pair im7 = new pair("withdrawn", "no_consensus");				immediatejuggleList.add(im7);
		pair im8 = new pair("consensus", "redraft");					immediatejuggleList.add(im8);
		pair im9 = new pair("consensus", "poll");						immediatejuggleList.add(im9);
		pair im10 = new pair("ready_bdfl_review", "consensus");			immediatejuggleList.add(im10);
		 
		//March 2020 remove some substates  for some peps		 
		 pairp pk = new pairp(308, "bdfl_pronouncement_rejected");	removeSpecificSubstatesForPEPs.add(pk);
		 
		//June 2020 further clean some substates  for some peps	
		 pair c = new pair("consensus", "no consensus");		cleanSubstatesForPEPs.add(c);
		 //pair c1 = new pair("member_willing_to_be_bdfl_delegate", "member_volunteers_BDFL_delegate");		renameSpecificSubstatesForPEPs.add(c1);
		 
		
		MysqlConnect mc = new MysqlConnect();		Connection conn = mc.connect();
		// date of output
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
		String newdate = dateFormat.format(date);
		String fileDir  = "C:\\DeMap_Miner\\results\\2020\\MainStudy\\postProcessResults";
		//String filename = "C:\\Users\\psharma\\Google Drive\\PhDOtago\\Code\\outputFiles\\postProcessResults_"  +  ".csv";		
		File theDir = new File(fileDir + newdate);	 System.out.println("Output directory: " + fileDir);
		createDateDirectory(theDir);		
		String rawResultsExcelFilename 	   = theDir + "\\"    + "RawResults.xlsx";
		String allPEPTypesExcelFilename    = theDir + "\\"    + "All.xlsx";
		String standardsTrackExcelFilename = theDir + "\\"    + "Standard Track"  + ".xlsx";
		String ProcessExcelFilename =		 theDir + "\\"    + "Process"  	      + ".xlsx";
		String InformationalExcelFilename =  theDir + "\\"    + "Informational"   + ".xlsx";
		String PEPReasonsExcelFilename 	  =  theDir + "\\"    + "PEPReasons"      + ".xlsx";		
		String MainStatesExcelFilename 	  =  theDir + "\\"    + "MainStates"      + ".xlsx";	 //added dec 2018 to see state table
		//just output the raw Results before post processing
		ew.writeRawResultsToExcelFile(rawResultsExcelFilename ,ew,conn);			
		//Post Process
		boolean postProcess = true;
		
		//just output all data from db to file
		//OutputPEPResultsToCSV(true);		
		emptyPostProcessedResultsDatabaseTable(conn);	//empty results_postprocessed table		
		//options are implemented here
		//get reasons - comment above	
		String where = null;	//" where pep = 289 ";	//"where label like '% accepted %' ";   //rejected					
							//wherePEP
		PostProcess(allPEPTypesExcelFilename,standardsTrackExcelFilename,ProcessExcelFilename,InformationalExcelFilename,PEPReasonsExcelFilename,MainStatesExcelFilename, where, conn, postProcess,ew, juggleList);	
	}
	
	//if "previous_version_rejected", move to top
	
	public static void PostProcess(String allPEPTypesExcelFilename,String standardsTrackExcelFilename,String ProcessExcelFilename,String InformationalExcelFilename,String PEPReasonsExcelFilename, 
			String MainStatesExcelFilename,	String where, Connection conn, boolean postProcess,ApachePOIExcelWrite ew,  ArrayList<pair>  juggleList ){		
		String str = "";
		if (where==null)
			str = "";
		else
			str = where;
		
		//list to hold all results
		// for each pep, create a TripleProcessingResult object 
		// list of triple processing result object which contains candidate
		// sentences as well
		ArrayList<TripleProcessingResult> tprList = new ArrayList<TripleProcessingResult>();
		// clean this arraylist after each pep				
		try {
			tprList = extractResults_PopulateTPR(conn, str, tprList); //Extract results from database
			
			//jun 2020 debug
					
			//do the checking here
			//ALLOW REPEATED SENTENCES, BUT NOT REPEATED LABELS
			// DUPLICATE SENTENCE, remove duplicate sentences, returns true if sentence is repeated
			PostProcessOptions oerr = new PostProcessOptions();
			oerr.setResultsPostProcessedTableName(resultsPostProcessedTableName);
			//CHOOSE YOUR OPTION(S)			
			
			//remove empty labels - just to reduce the result space-- include when evaluating
     		tprList = oerr.removeEmptyMatches(tprList);	     		
     		//lowercase all result data
     		//tprList = lowercaseResults(tprList);
			
     		//repeated sentence
     		//very important  ..a lot of pep 8 sentences and paragraphs are repeated/quoted and based on the days when pep 8 was discussed, we can eliminate
     		//for example ....extract pep 8 last major state from commit/chekin data
     		//jan 2019, commented as those sentences can be stated in any date
//			tprList = oerr.checkRepetitionForSpecificSentences(tprList, fw);
     		     		
     		//postProcess=false;     		
     		
     		//NORMALLY START COMMENTING HERE
     		if (postProcess) {
				//duplicate label - delete repeated labels, in case when duplicate found and one is daniel state data labels, we delete daniel state data 
     			//THIS NEEDS MORE WORK..WE CANT DELETE STATE CHANGE DATA AS IT HAS IMPORTANT TIMESTAMP
     			//jan 2019, we dont need this, now we not capturing state data as already provided by daniel/commit data
     			//this is needed, as his removes repeated labels from pep, removes the second one
				tprList = oerr.removeRepeatedMatchedLabel_WithinSameProposal(tprList);
				
								
				//to addd
				//if a label has been found in sentence, don't look for it again as a label can be found several times in a sentence with several triple arrangements in inputfile
				//make sure the label has not been captured before
				//updated 30nov2017- this does not work in instances where the first labeltriple  is found but not matched, as the second set of triples may get matched, but this code prevents that from happenning
				//this has to be moved to post processing section
				boolean labelMatchedBefore = false;
//				for (String newLabel : matchedLabelList) {  
//					if(v_idea.equals(newLabel)){
//						labelMatchedBefore = true;
//						//  System.out.print("label Matched Before in same sentence " + v_idea + " "); 
//					}
//				}
				
				
				
				//FOR A SENTENCE only keep higher priority labels, e.g. if same sentence returns these two labels, then accept the large labels
				//check all labels in sentence, if a labels is subset of another label, remove it eg .accepted_because_favorable feedback vs bdfl_accepted_pep_because_favorable feedback
				tprList = oerr.removeLowerPriorityLabelsForSameSentence(tprList);
								
				
				//REMOVE CONTINUOUS LABELS the above allows certain labels to be repeated in a pep, like discussion and updated
				//the below removes continuous label repetition just one after the other
				tprList = oerr.removeContinuosAllowableRepeatedStates(tprList, conn);
				
				Integer a=null; 
				String b="";
				for (int x=0; x <tprList.size(); x++){
					a= tprList.get(x).getPepNumber();
					b= tprList.get(x).getClausIE();
					//if(b==null || b.isEmpty()) {}
					//else {
//						System.out.println(a + " == " + b);
					//}
				}
				
				//combine draft and proposal to draft only
				//exception should be when it is proposed as an idea - have to figure out how - it would be states in ideas folder, and state would be proposed_idea
				//if a pep contains both labels; proposal and draft, change proposal to draft else if proposal exists, change proposal to draft 
				
				//jan 2019, we dont need to do this i we no longer looking for mai states already shown by daniel data/commit data
				//String statesToRemoveChangeFromResults[] = {"proposal-draft","draft_pep-draft"};		
				//tprList = oerr.combineResultLabels(tprList, statesToRemoveChangeFromResults);			
			
				
				//ReasonsInNearbyStates
				//for a major captured label(accepted), find reasons in last 5 and next 5 labels as assign to column within the captured labels i.e accepted 
				//Populate results_postprocessed table column for reasons extracted in nearby states
				
				//also change ordering - in some cases the reasons of state changeare after the state, eg bdfl_accepted_pep comes after accepted, in cases of summary messages
				//for nearby states of accepted, we check if another accepted state exists with reasons, if so we move the reasons just above the first accepted and delete the second accepted
				//do this for major states only at this moment
				
				//for each major state captured, identify the reasons nearby and then insert in the column next to the major column
				/* dec 2018, we not checking for reasons at the moment, save time and reason checking is different process altogether
				 * plus its not of any use in state substate extraction
				 * ArrayList<StatesReasons> statesReasonsList = new ArrayList<StatesReasons>();				
				Statement stmt2 = null;
				stmt2 = conn.createStatement();				
				ResultSet rs2 = stmt2.executeQuery("SELECT label,lesserState, reason from labelsLesserStatesReasonlabels order by label asc,lesserState asc");  //date asc				
				while (rs2.next()) {
					StatesReasons sr = new StatesReasons();
					sr.setData(rs2.getString("label"), rs2.getString("lesserState"), rs2.getString("reason") );
					statesReasonsList.add(sr);		
				}				
				*/
			
				//String reasonIdentifierTerms[] = {"because","since", "based on","due to","thanks to","accordingly","as a result of","through"};
					// implementing this will help as not all reasons are triples
				
				//lack of an overwhelming majority 
//$$$			tprList = oerr.extractReasonsPreviousPastCapturedStatesAndShiftLesserStatesUp_ForMajorStates(tprList,statesReasonsList);
				//note for above
				//look for instances in a pep, where accepted and bdfl_accepted or co_bdfl accepted are in the same pep results
				//if so then move the bdfl ones up and before the accepted
				//once done, this will allow us to code reaosn from later to before the state label like consensus before accepted in cases of reporting/summary messages
				
				
				//REMVE STATE DATA remove repeated ACCEPTED and accepted both confuses a lot unless we have bdfl_accepted where more information is present
				//THIS NEEDS MORE WORK..WE CANT DELETE STATE CHANGE DATA AS IT HAS IMPORTANT TIMESTAMP
				// so if two labels exists for the same pep, remove the previous study labels, - the ones in uppercase
				// The above function should be called before this function
				//temp commented
//$$			tprList = oerr.removePreviousStudyLabels(tprList);
				
				
				
				
				
				//march 2020, just output certain peps which have these pairs of state and substates accepted and no consensus
				tprList = oerr.showLabelsInSameProposal(tprList);
				
				//march 2020, just output certain peps which have these pairs of state and substates accepted and no consensus
				tprList = oerr.combineLabelsInSameProposal(tprList);
				
				//March 2020 rename some substates 
				//example "willing_bdfl" to willing bdfl delegate
				tprList = oerr.renamesubstates(tprList);	
				
				//March 2020 remove some substates 
				//example "uncontroversial"
				tprList = oerr.removesubstates(tprList);				
				
				//jan 2019, we want to juggle some labels, like accepted and bdfl_accepted. Bdfl_accepted should be put before
				//FOR A PEP juggle higher priority labels, e.g. if same pep has these two labels, then shift the large labels
				//check all labels in sentence, if a labels is subset of another label, remove it eg .accepted_because_favorable feedback vs bdfl_accepted_pep_because_favorable feedback
				tprList = oerr.juggleLabelsInSameProposal(tprList, juggleList);
				
				//june 2020, new jgglelist to swap only the immediate results
				tprList = oerr.juggleImmediateLabelsInSameProposal(tprList, immediatejuggleList, conn);
				
				Integer a1=null; 
				String b1="";
				for (int x=0; x <tprList.size(); x++){
					a1= tprList.get(x).getPepNumber();
					b1= tprList.get(x).getClausIE();
					//if(b==null || b.isEmpty()) {}
					//else {
						System.out.println(a1 + " __ " + b1);
					//}
				}
				
				//june 2020
				tprList = oerr.differentiateSomeSubstates(tprList);
				
				
				//uppercase main states as lowercase and uppercase 'accepted' and 'ACCEPTED' creates double states
				tprList = upperCaseMainStatesInResults_removeUnderscore(tprList);
				
				//June 2020 ..uppercase some parts of sub-states e.g. bdfl to BDFL 
				//tprList = upperCaseMainSubStatesInResults(tprList);
				
     		}

     		
			//match labels to roles to reason
			
			//tprList = oerr.prioritiseLabelinSameSentenceMultipleLabelMatches(tprList, fw);
			
			//shift states up 
//			tprList = oerr.shiftStates(tprList, fw);			
			
			//only want data between two states
//$$		String startState = "draft",endState = "accepted";
			//commented but works			
        	//tprList = oerr.returnDataOnlyBetweenTwoStates(tprList, startState, endState);
			
			//output to file
//xxx			fw.append("pep| author| date| label| currentSentence| reverb| clausie| ollie| repeatedLabel|ps|ns|pp|ep|np \n");
	//		oerr.outputListToFile(tprList,fw);
			//use for loop to output everything from listarray	
	  		//System.out.println("here 6");
			//send the list for output to file and table
  	  		try {
  	  			//write to file - still works - but we dont need now
  	  			//oerr.outputMinimalListToFile(tprList, fw,ew );
  	  			//write postprocessed results to database table
  	  			oerr.outputListToDatabase(tprList, conn);
  	  			//once its written to table, extract it and write to excel
  	  			//allTypes, Standard Track, Process, Informational
  	  			ew.writePostProcessResultsToExcelFile(allPEPTypesExcelFilename,ew,conn,"All Types");  
  	  			ew.writePostProcessResultsToExcelFile(standardsTrackExcelFilename,ew,conn,"Standards Track"); 
  	  			ew.writePostProcessResultsToExcelFile(ProcessExcelFilename,ew,conn,"Process"); 
  	  			ew.writePostProcessResultsToExcelFile(InformationalExcelFilename,ew,conn,"Informational");
  	  			//dec 2018..create state procedd diagram
  	  			ew.writePostProcessResultsToExcelFile_ForMainStates(MainStatesExcelFilename,ew,conn);
  	  			
  	  			//Reasons ..dec 2018 commented...we may not need in states extraction
  	  			//ew.writePostProcessResultsReasonsToExcelFile(PEPReasonsExcelFilename,ew,conn); //function name is correct..just using same function rather than creating exact similar one
				  	  			
  			} catch (IOException e) {
  				// TODO Auto-generated catch block
  				e.printStackTrace();
  			}			
			//fw.flush();
			//fw.close();
			//conn.close();
			//System.out.println("CSV File is created successfully.");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	//june 2020 ..updated to include messgaes with id > 7 million
	private static ArrayList <TripleProcessingResult> extractResults_PopulateTPR(Connection conn, String str,	ArrayList<TripleProcessingResult> tprList) throws SQLException {
		Statement stmt = null;			stmt = conn.createStatement();
		String pType = "";
		// the mysql insert statement												//5											//10
		ResultSet rs = stmt.executeQuery("SELECT pep, author,authorRole,messageSubject,numberOfPEPsMentionedInMessage, date,"
											+ "datetimestamp,folder,file, messageID, label, subject, relation, object, currentSentence, "
											+ "reverb, clausie, ollie, repeatedLabel,role, "
											+ "allReasons, ps,ns,pp,ep,np, "
											+ "reasonInSentence,reasonTermsInMatchedTriple, reasonTermsInNearbySentencesParagraphs, reasonTriplesInNearbySentencesParagraphs, "
											+ "reasonTermsInPreviousSentence,reasonTermsInNextSentence,reasonTriplesInPreviousSentence,reasonTriplesInNextSentence,reasonTriplesInSameSentence, "
											+ "labelFoundInMsgSubject,isFirstParagraph, isLastParagraph "
											+ "from " + resultsTableName + " "
											//dec 2018
											+ " where (messageid < 6000000  or messageid >= 7000000 ) "		//we dont include the pep summary messages as they have states, but pepsummary message has incorrect date 
										    + " and PEP > -1 "
										    + " and PEP < 8000 "  //this peps shoudl not have been inluded anyway as they are after bdfl resigns
											//+ " and PEP =298 "
										    + " and DATE(datetimestamp) < '2018-07-12' "
											+ " and label <> 'proposal' "  //too many
										    + " and label NOT LIKE '%discussion%' "  //too many
										    + " and label NOT LIKE '%small%' "  //too many small edits left
										    + " and label NOT LIKE '%benefits%' "  //too many benefits marginal
										    + " and label NOT LIKE '%updated%' "  //lets try removing and maybe we can have a smaller diagram
										    //+ " and clausie NOT LIKE '%final%' "  //lets try removing and maybe we can have a smaller diagram..tried cant save anymore space
										    + " and label NOT LIKE '%comments%' "  //comments addressed - should be updated
										    + " and label NOT LIKE '%previous%' "  //previous version rejected
											+ str
											+ " order by pep asc, datetimestamp asc, messageID asc");  //date asc
		
		while (rs.next()) {
			
			TripleProcessingResult tpr = new TripleProcessingResult();				
			Integer pepNumber = rs.getInt(1);
			
			//pType = getPepTypeForPep(pepNumber, conn);
			
			
			//if tiemstamp column is null, assign date column by converting it to timestamp
			Date justdate = rs.getDate("date");
			Timestamp timestamp = rs.getTimestamp("datetimestamp");
			if(timestamp==null){
				if(justdate==null) {}
				else {
					timestamp = new java.sql.Timestamp(justdate.getTime());
				}
			}				
			//in case when we want to see all peptype result for diagram OR a specific type
			//System.out.println("Record ");
			//dec 2018, we filter
			String label = rs.getString("clausie"); boolean dontConsider=false;
			
			System.out.print(rs.getString("pep") + " | " + label + " ");
			
			for (String d: dontConsiderStates) {
				if(label.equals(d)) 
					dontConsider = true;						
			}
			
			//Feb 2020..leave out PEPs with states anamolies
			if (ignorePEPs) {
				for (Integer ip: ignorePEPsList) {
					if(ip.equals(pepNumber))
						dontConsider = true;	
				}
			}
			
			String folderX = rs.getString("folder");	
			
				if (dontConsider) {
					System.out.println("Dont consider: " +rs.getString("pep") + " | " + label);
				}
				else {
				//set the data here,
//					System.out.println(rs.getString("pep") + " | " + label);
					tpr.setData(pepNumber,rs.getString("author"),rs.getString("authorRole"), rs.getString("messageSubject"),rs.getInt("numberOfPEPsMentionedInMessage"),justdate ,
							timestamp, folderX ,rs.getString("file"),
							rs.getInt("messageID"),rs.getString("label"), rs.getString("subject"), 
							 rs.getString("relation"),  rs.getString("object"), rs.getString("currentSentence"), 
							rs.getString("reverb"), label, rs.getString("ollie"),rs.getBoolean("repeatedLabel"),rs.getString("role"),
							rs.getString("allReasons"), rs.getString("ps"), rs.getString("ns"), rs.getString("pp"), rs.getString("ep"), 
							rs.getString("np"), rs.getString("reasonInSentence"),rs.getString("reasonTermsInMatchedTriple"),rs.getString("reasonTermsInNearbySentencesParagraphs"),rs.getString("reasonTriplesInNearbySentencesParagraphs"),
							rs.getString("reasonTermsInPreviousSentence"), rs.getString("reasonTermsInNextSentence"),rs.getString("reasonTriplesInPreviousSentence"),rs.getString("reasonTriplesInNextSentence"),							
							rs.getString("reasonTriplesInSameSentence"), rs.getBoolean("labelFoundInMsgSubject"),rs.getBoolean("isFirstParagraph"),rs.getBoolean("isLastParagraph"));					
					tprList.add(tpr);
				}
//			}
//				else {
				
//				}				
		}
		return tprList; 
	}
	
	private static void createDateDirectory(File theDir) {
		// if the directory does not exist, create it
		if (!theDir.exists()) {
		    System.out.println("creating directory: " + theDir.getName());
		    boolean result = false;
		    try{		        theDir.mkdir();		        result = true;		    } 
		    catch(SecurityException se){	}	        //handle it		    	
		    if(result) 
		        System.out.println("DIR created");		    
		}
	}

	private static ArrayList<TripleProcessingResult> lowercaseResults(ArrayList <TripleProcessingResult> tripleProcessingResult) {
		for (int x=0; x <tripleProcessingResult.size(); x++){
			String lowercase = tripleProcessingResult.get(x).getClausIE().toLowerCase();
			tripleProcessingResult.get(x).setClausIE(lowercase);
		}
		return tripleProcessingResult;
	}
	
	private static ArrayList<TripleProcessingResult> upperCaseMainStatesInResults_removeUnderscore(ArrayList <TripleProcessingResult> tripleProcessingResult) {
		for (int x=0; x <tripleProcessingResult.size(); x++){
			String result = tripleProcessingResult.get(x).getClausIE();
			
			if (result.contains("_")) {
				result = result.replaceAll("_", " ").toLowerCase().trim();
				tripleProcessingResult.get(x).setClausIE(result);
			}
			
			if (result.contains("bdfl")) {
				result = result.replaceAll("bdfl", "BDFL").trim();
				tripleProcessingResult.get(x).setClausIE(result);
			}
			
			//if (result.contains("pep")) {
			//	result = result.replaceAll("pep", "").trim();
			//	tripleProcessingResult.get(x).setClausIE(result);
			//}
			
			if (result.contains("ReDraft")) {
				result = result.replaceAll("ReDraft", "reDraft").trim();
				tripleProcessingResult.get(x).setClausIE(result);
			}
			
			//oct 2020
			//SIG folder communication can be different region in process model
			String folderX = tripleProcessingResult.get(x).getFolder();
			if (folderX!= null)
				if(folderX.contains("distutils")) 
				{
					result = "SIG "+result;
					tripleProcessingResult.get(x).setClausIE(result); 				System.out.println("Label changed: " +result);
				}
			
			
			
			//check against all main states
			for(String state : mainStates) {
				if(result.equals(state)) {
					tripleProcessingResult.get(x).setClausIE(result.toUpperCase());
				}
			}
		}
		return tripleProcessingResult;
	}
	
	private static ArrayList<TripleProcessingResult> upperCaseMainSubStatesInResults(ArrayList <TripleProcessingResult> tripleProcessingResult) {
		for (int x=0; x <tripleProcessingResult.size(); x++){
			String result = tripleProcessingResult.get(x).getClausIE();
			//check against all main states
			for(String substate : uppercaseSubStates) {
				if(result.contains(substate.toLowerCase())) {
					//result = 
					tripleProcessingResult.get(x).setClausIE(result);
				}
			}
		}
		return tripleProcessingResult;
	}
	
	//i dont know this function is used for which purpose
	public static void OutputPEPResultsToCSV(String filename, Boolean showRepeatedLabel, Connection conn){		
		String str = null;
		if (showRepeatedLabel)
			str = "";
		else
			str = "where repeatedLabel = 0 ";
		
		//if (filename==null)
		//	filename = "C:\\Users\\admin\\Google Drive\\PhDOtago\\scripts\\outputResultsFromDB.csv";
		
		try {
			FileWriter fw = new FileWriter(filename);
			Statement stmt = null;
			stmt = conn.createStatement();
			// the mysql insert statement
			ResultSet rs = stmt.executeQuery("SELECT pep, author, date,mid, label,subject, relation, object, currentSentence, reverb, clausie, ollie, role, reason, repeatedLabel,ps,ns,pp,ep,np "
					+ "from " + resultsTableName + " " 
				//	+ str
					+ " order by pep asc, date asc");

			fw.append("pep| author| date| mid| label|subject| relation|object| currentSentence| reverb| clausie| ollie| role| reason|repeatedLabel|ps|ns|pp|ep|np \n");
			while (rs.next()) {
//				System.out.println("record.");
				fw.append(rs.getString(1));				fw.append('|');				fw.append(rs.getString(2));				fw.append('|');				fw.append(rs.getString(3));
				fw.append('|');				fw.append(rs.getString(4));				fw.append('|');				fw.append(rs.getString(5));				fw.append('|');
				fw.append(rs.getString(6));				fw.append('|');				fw.append(rs.getString(7));				fw.append('|');				fw.append(rs.getString(8));
				fw.append('|');				fw.append(rs.getString(9));				fw.append('|');				fw.append(rs.getString(10));				fw.append('|');
				fw.append(rs.getString(11));				fw.append('|');				fw.append(rs.getString(12));				fw.append('|');				fw.append(rs.getString(13));
				fw.append('|');				fw.append(rs.getString(14));				fw.append('|');				fw.append(rs.getString(15));				fw.append('|');
				fw.append(rs.getString(16));				fw.append('|');				fw.append(rs.getString(17));				fw.append('|');				fw.append(rs.getString(18));
				fw.append('|');				fw.append(rs.getString(19));				fw.append('\n');
			}
			//fw.flush();
			fw.close();
			//conn.close();
			System.out.println("CSV File is created successfully.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
    public static void emptyPostProcessedResultsDatabaseTable(Connection conn){		  
	     Statement stmt = null;
	     try{
	        stmt = conn.createStatement();	       
	        ResultSet resultSet = stmt.executeQuery("TRUNCATE results_postprocessed");	        // the mysql insert statement
	        //conn.close();
	     }catch(SQLException se){
	         se.printStackTrace();//Handle errors for JDBC	       
	     }catch(Exception e){
	        e.printStackTrace();	//Handle errors for Class.forName	        
	     }finally{
	        //finally block used to close resources
	        try{
	           if(stmt!=null)
	              stmt.close();
	        }catch(SQLException se2){
	        }// nothing we can do
	        
	     }//end try	     
	}
    public void finalize() {	    //System.out.println("Probability instance is getting destroyed"); 
	}
}