package miner;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

//import com.google.common.collect.ObjectArrays;

import ManualAnalysis.StatesAndReasons.reasonRow;
import readRepository.readRepository.ReadLabels;
import callIELibraries.ClausIECaller;
import callIELibraries.JavaOllieWrapperGUIInDev;
import callIELibraries.ReVerbFindRelations;
import connections.MysqlConnect;
import de.mpii.clause.driver.ClausIEMain;
//import edu.stanford.nlp.ling.HasWord;
//import edu.stanford.nlp.ling.Sentence;
//import edu.stanford.nlp.process.DocumentPreprocessor;
import wordnet.jaws.WordnetSynonyms;
import miner.process.LabelTriples;
import miner.process.ProcessMessageAndSentence;
import miner.process.ProcessingRequiredParameters;
import miner.process.PythonSpecificMessageProcessing;
import miner.processLabels.CheckAndGetReasons;
import miner.processLabels.ProcessLabels;
import miner.processLabels.TripleProcessingResult;
import utilities.ParagraphSentence;
import utilities.StateAndReasonLabels;
//import causalRelExtractor.create_explicit_corpus;

//import org.drools.compiler.compiler.DroolsParserException;
//import org.drools.compiler.compiler.PackageBuilder;
//import org.drools.core.RuleBase;
//import org.drools.core.RuleBaseFactory;
//import org.drools.core.WorkingMemory;

//14-July-2022. This script is for the actual rationale extraction, i.e. the candidate rationale sentence extraction.
//It can never be part of the process mining as rationale miner needs to begin based on the states (i.e. Accepted or Rejected)

//TableName to store is defined in 'ProcessPastMessages.java'
//static String tableNameToStore = "autoExtractedReasonCandidateSentences"; 
//static String tableNameToStoreMessage = "autoExtractedReasonCandidateMessages"; 

//August 2020, we now use this script to parry out two baseline methods:
//1. Reason Identifier + 2. State + 3. Reason Marker / Reasons
//The three above + Days from state change + Entity

//Dec 2019..we run this again as we added data till late 2018/early 2019 

//Feb 2019..we are trying to integrate this script with the processmining (because we want to use triple extraction) so this script wont be used 
//BUT IT CAN NEVER BE INTEGRATED AS PART OF PROCESS MINING as the reason extraction starts with state info
//so we added check to see any reason triples for that sentence was captured with reason labels

//This script is used for an attempt to automate the reason extraction process
//Feb 2019...extract candidate reason sentences based ion rules and populate db table, then another script will evaluate it
//This cannot be pat of the main DeMap Miner script as weights are calculated based on distance from mainstates

//The effect sentence below would be captured in state matching. We have to extract the cause sentence as candidates list.
//CAUSESENTENCE
//There have been some comments in favour of the proposal, no objections to the proposal as a whole, and some questions and objections about specific details. 
//These are believed by the author to have been addressed by making changes to the PEP.	
//EFFECT SENTENCE
//Marked PEP 391 as Accepted.

//2018 June, new ways to capture reasons for state changes..we are trying tp find reasons for all state changes, even sub states, 
	//for major states like accepted, rejected, etc and also sub  states like poll, vote, etc
	//but now we can do this process separately.. the reason finding from teh main state and sub state extraction, (as if we use our classification scheme it would give )
	//when, once a state or substate is found, we look for reasons in same message and previous message  - maybe not future messages of the proposal
	//what are we looking for...
	//we look for messages close to the state, e.g. accepted, rejected etc and go backwards
	//we also look at the person who writes these emails, 
	//		checkin and commit messages themselves have reasons
	// 		concentrating first at bdfl messages for decision reason
	//		then we look at proposal author messages for 'author thoughts on how the proposal was received'
	//		we look at other community members messages to find their thoughts on the final versions of the proposal
	//by looking at all these, we wouild come up with a more rich reason facts
	//the bdfl can always overide the community and author views, thats what we want to know and thats why its important to capture these.
	//how can we get
	//as said earlier, we look for messages which have these states changes and work backwards
	//		we use term matching tdidf matching and wordnet synsets for word senses ambiguation to get candidate sentences which may convey the reason for the state
	//which sentences to get for which state? the state which has been just captured, we query the database to find sentences for those 
		
	//go through each label in results table and get the label captured, get pep, label, datetimestamp, messageid
	//if label is accepted, look for all (cause -effect) sentences for accepted and match the terms in all sentences of 
	//	that message
	// 	previous message (by date) sentences by bdfl, message author  

	//June 2018, add facility to add these candidate sentences to database 
	
//aug 2018..there are two different things this script can do...calculate probabilities, and/or write sentences to db table with weights for machine learning
//after that you have to run the evaluation_autoamticvsmanual.java script to populate the ones in the ML table

//nov 2018,  make sure you run this
//update allmessages set messageType = 'BDFLReviewing'  where messageid > 6000000;

//dec 2018...now to add code to compute message level weightage system which can be later used for message level ML
//DEC 2018...We are now moving the reason checking code to state checking, so both state ad reason checking are integerated
	
public class AutomaticRationaleExtraction {
												//"proposal","open","active", "pending", "closed", "final", ..add oll, vote, etc
												//"final",
	static String[] statesToGetReasons  = {"accepted","rejected"}; //, "deferred","replaced","postponed","incomplete","superseded", "update", "vote", "poll"};//removed "consensus" 			//<-new states changes by user which i want to capture	
	static String reasonIdentifierTerms[] = {"because","since", "based on","due to","thanks to","accordingly","as a result of","through"};
	// implementing this will help as not all reasons are triples
//	static String ActualReasons[] = {"discussion","debate","bdfl pronouncement","poll result","voting result","vote result","consensus","no consensus","feedback","favorable feedback",
//			"support","no popular support","favourable feedback","poor syntax","limited utility","difficulty of implementation","bug magnet","controversy","majority","wrangling",
//			"superseded"};  //rejected reason
	
	//negation and conditional terms are needed to be checked as accepted label in pep 308 comes up with some cadidates which have 'if'
	//'So not only is this system not democratic in the obvious sense of `` majority rules '' -LRB- if you get more + votes than - , the PEP is approved -RRB- , it 's also almost never even about gathering numerical support -LRB- which is what this * would * be able to show -RRB- .
	//'Unfortunately , if we were to accept Raymond 's proposal , we 'd have to revisit the discussion , since his proposal removes several ways we currently avoid the need .
	//these terms are now read in from file
	//static String negationTerms[] = {"if","not","do not","will be","can","can be","is n't","nor","should","should be","would","needs to be","may","will","hav n't","have n't","might", "n't","whether","zero","no","never","never been","need","would need","before","zero"}; // added later for test												// "should","should be"
	//static String conditionalList[] = {"if","should","should be","when","can","can be","unless","i hope","whether","once","would","might","why"};
	
	//August 2020. Baseline 1 and 2
	//static Boolean baseline1 = false, baseline2 = false;
	
	
	//what we want this script to do
	static boolean retrieveKeywords =false, //first stage - script would retrieve sample terms and allow the use to choose the keywords (provided with sentences)  
				   suggestCandidateSentences = true;  //second stage - using the keywords above, as part of our heuristics system, the script would output candidate sentences
	//the candidate reason sentences would be extracted based on the terms identified in the first stage
	//the first stage consists of triple extraction and verb pair extraction
	static String probability =""; //highest, very high,high, probable
	//maybe we can extend to get these reason terms from database table "leserstates and reasons"
	static String tableToStoreExtractedRelations = "extractedRelations_clausie_reasonTriples";
	static String stateTable = "pepstates_danieldata_datetimestamp";
	
	static List<String> candidateSentencesList = new ArrayList<String>();  //we dont want a sentence to show up multiple times as reason candidate for a proposal, unless for different states
																	//empty this after every proposal
	static ProcessingRequiredParameters prp = new ProcessingRequiredParameters();
	static ParagraphSentence ps = new ParagraphSentence();
	static PythonSpecificMessageProcessing pm = new PythonSpecificMessageProcessing();
	static ProcessMessageAndSentence pms = new ProcessMessageAndSentence();
	static ProcessEachMessage ppm = new ProcessEachMessage();
	static ProcessLabels pt = new ProcessLabels();	
	static CheckAndGetReasons cgr = new CheckAndGetReasons();
	static ReadLabels b = new ReadLabels();
	
	static WordnetSynonyms wn = new WordnetSynonyms();
//	static create_explicit_corpus cec = new create_explicit_corpus();
	
	static boolean outputList = true;
	
	public static class StringPair {	//to store reason label and sentence, to prevent each label have two instances of the same sentences, too many same sentences, one after another
		   String label;
		   String SentenceOrParagraph;
		   public StringPair(String v_label, String v_sentenceOrParagraph) {
			   this.label = v_label;	this.SentenceOrParagraph = v_sentenceOrParagraph;	
		   }
		   public String getLabel() 										{			return label;									}
		   public void setLabel(String label) 								{			this.label = label;								}	
		   public String getSentenceOrParagraph()							{			return SentenceOrParagraph;						}
		   public void setSentenceOrParagraph(String sentenceOrParagraph)   {			this.SentenceOrParagraph = sentenceOrParagraph;		}
		   //NOTE: override hashcode and equals methods
	}
	static ArrayList<StringPair> spList = new ArrayList<StringPair>();	
	
	public static void outputheaders() {
		if(outputList)
			System.out.format("%5s | %15s| %7s | %10s | %30s | %25s | %15s | %15s |%15s |", "pepNo","State/Substate","MID","Date","Reason Identifier Terms","Actual Reasons","Location","Reason Label","Probability");System.out.println("");
	
	}
	/*
	public void executeDrools() throws DroolsParserException, IOException {
		PackageBuilder packageBuilder = new PackageBuilder();
		String ruleFile = "/com/rules/droolsRule.drl";
		InputStream resourceAsStream = getClass().getResourceAsStream(ruleFile);
		Reader reader = new InputStreamReader(resourceAsStream);
		packageBuilder.addPackageFromDrl(reader);
		org.drools.core.rule.Package rulesPackage = packageBuilder.getPackage();
		RuleBase ruleBase = RuleBaseFactory.newRuleBase();
		ruleBase.addPackage(rulesPackage);
		WorkingMemory workingMemory = ruleBase.newStatefulSession();
		Person person = new Person();
		person.setName("Shamik Mitra");
		person.setTime(7);
		workingMemory.insert(person);
		workingMemory.fireAllRules();
		System.out.println(person.getGreet());
	}
	*/
	static Connection conn;
	static ClausIEMain ciem;
	static boolean restart = true;  //CRITICAL VARIABE MAKE SURE CHECK
	
	public static void main(String[] args) throws UnsupportedEncodingException, FileNotFoundException {				
		connections.MysqlConnect mc = new MysqlConnect();		conn = mc.connect();
		boolean foundReasonOnce = false,showCIEDebugOutput = false,matchExactTerms = false;;
		if(retrieveKeywords)
			ciem = new ClausIEMain();	//ClausIEMain ciem = new ClausIEMain();
		
		wn.init();
//		cec.init();
		System.out.println("RESTART="+restart);
		Integer libraryToCheck =2,proposal = 268;		
		Timestamp v_dateTimeStamp = null;
		//String tripleArray[][];	//Causal Extraction Results
		ps.initTokenizer();
//		ClausIEMain cm = new ClausIEMain();	
//		ReVerbFindRelations rvr = null; //new ReVerbFindRelations();
//		JavaOllieWrapperGUIInDev jw = null; //new JavaOllieWrapperGUIInDev();
//		ProcessLabels pt = new ProcessLabels();		//create new triple processing reult object	
		ArrayList<TripleProcessingResult> tripleProcessingResultList = new ArrayList<TripleProcessingResult>();
		Statement stmt = null;	
		
		ArrayList<LabelTriples> reasonLabels = new ArrayList<LabelTriples>();
		//reasonLabels = e.readLabelsFromFile(reasonsFilename,reasonLabels,"Reasons Matching",conn, false);	
		String reasonTableName = "reasonlabels";
		reasonLabels = b.getallLabelsFromTable(reasonTableName,conn);
		//we implement improved version of this function below which has wordforms and wordnet synony
		ppm.initialiseTermLists(wn);    //init the classes objects needed in that class
		//ppm.initialiseTermListsAndWordForms(wn);	//new improved version
				
		//for each of the result rows where label is matched, try to run the causal relation extracter
		//where found, output pep, label, causal connector and reason, 
		//match reason and label
		//output 
		
		prp.setScriptsHomeFolderLocation("C:\\DeMapMiner\\datafiles\\");		
		prp.setScriptPurpose("reasonextraction");
		//prp.setRunForNumberOfMessagesLimit(5000);
		//prp.setProcessedMessageCounter(0);
		
		//training data list
		ArrayList<ReasonLabel> rl =new ArrayList<ReasonLabel>(); 
		
		String messageSubjectStatesSubstatesList="",  messageSubjectDecisionTermList="", messageSubjectVerbList= "",
				messageSubjectProposalIdentifiersTermList="",  messageSubjectEntitiesTermList="", messageSubjectSpecialTermList="";
		
		try {
			 System.out.println("Populating Reason Label List");
			 rl = ppm.populateReasonLabelsFromTrainingData(rl, conn); //populate reason list from training data table	
				
			 stmt = conn.createStatement();
			 //do for each major label
			 //maybe get reason from lesserstates and reasons table
			 // maybe get reason for reason label inputfile
			 //if reason triple is matched in same sentence or nearby sentence or paragrapgh, we can be certain as its very near to label
			 //if reasonterm is found in matched triple, we have to use dependency to be more sure
			 //if reasonterm is matched in next or previous sentences, we have to use some sort of classification to match
			 //if reasonterm is matched in next or previous paragraph, we have to see if teh above scheme can be extended to cater.

			 outputheaders();
			 
			 //for each of the states found in the proposal, we get the nearby messages and we look for the candidate reason sentences
			 //we get these by commit states, states using triple extraction
			 String state_str="";
			// for(String labelToCheck : statesToGetReasons) {	
				 //state_str = state_str + " OR clausie LIKE '%" + majorState + "%' ";
			 //}
			 //state_str = state_str.replaceFirst("OR", "");  //remove first OR
//			 System.out.println("\nstate_str:"+state_str);
			 // results table or postprocessed table										//5											//10
			 //String labelToCheck[] = {"accepted","rejected"};
//&&			 ArrayList<Integer> pepsWithLabel = getallPepsWithLabel(labelToCheck,conn); //only get those peps with the label
			 //stuck 479, mid 29834
			 //279,289,313 not done..have to redo
			 //Integer[] selectedListItems = {279,289,313,336,340,345,376,388,389,391,414,428,435,441,450,484,488,498,485,471,505,3003,3103,3144}; //, }; 308, 318
			 //int[] selectedListItems = {402}; //204,270,275,289,295,299,330,336,340,382,402,437,438,439,443,449,451,464,470,485,495,507,508,518,519,3103,3134,3139,3152}; //, }; 308, 318
			 int restartLastProposalCompleted = 0, messageIDForRestart = 0, processedCounter=0,totalMessagesToProcessForProposal=0;
//			 for (int i : selectedListItems)
			 //pep 1 has too many unwanted states in one line in satte table
			 //feb 2019, since this script is so complicated, we restart after every pep, not after some number of messages
			 //9 to 5000, now 10000 as PEP 8000 exists
			 //we start at 42 as previous peps are mostly still active and hav lots of discussions 
			 //for (int i = 42;i< 10000;i++) //(int i = 352;i< 353; i++) //(int i = 214;i> 213;i--)  //pepsWithLabel) //5000  = 414; i< 415;i++)  `````````(int i = 09;i< 5000;i--)
			 //DEC 2019, we rerun for selected peps which were not done 552,553,557,566,572
			 //9  43??
			 for (int i = 9;i< 10000;i++) //(int i = 566;i< 567;i++)
			 //for (int i = 308;i< 309;i++) //(int i = 566;i< 567;i++) 
			 {
				System.gc(); //System.out.println("i="+i);
				//feb 2019
				//FOR all different purposes, these   functions get the max pep and max message id
				
				restartLastProposalCompleted = i; messageIDForRestart = 0; processedCounter=0; totalMessagesToProcessForProposal=0;	
				if(restart) {
					try {
	//					System.out.println("Current PEP Number="+i + ", Original restartLastProposalCompleted="+restartLastProposalCompleted);
	//					System.out.println("RESTART = TRUE, Getting RESTART PEP AND MESSAGEID");
						restartLastProposalCompleted = prp.getPd().getMaxProcessedProposalFromStorageTable(conn, prp); //select max pep from table
						System.out.println("Restart Last Proposal Completed = "+restartLastProposalCompleted);
						//not sure if used	
						//messageIDForRestart =	prp.getPd().getMaxProcessedMessageIDForProposalFromStorageTable(conn, restartLastProposalCompleted,prp);  //select max messageid from tabkle for that particular pep
	//					System.out.println("messageIDForRestart="+messageIDForRestart);
						//XX FEB 2019 FOR CONTINOUS LIST 
	//					restartLastProposalCompleted++; //we add to the last processed proposal
						prp.setPepNumberForRestart(restartLastProposalCompleted);
						prp.setMessageIDForRestart(messageIDForRestart);	//if there was an error, we dont want to attempt to proces sit again, thats why we add 1
						//if (prp.isSelectedAll())	System.out.println("Chosen Selected All---------------------------Unique Peps count = "+ UniquePeps.size() );
						//if (prp.isSelectedList())	System.out.println("Chosen Selected List---------------------------");
						System.out.println(" Extracted: RestartLastProposalCompleted: " +restartLastProposalCompleted + ", messageIDForRestart: " +messageIDForRestart);
					}
					catch (Exception io) {
						System.out.println("Exception 342");
					}
					
					//FEB 2019//for selected list
					if(restartLastProposalCompleted==i) {	System.out.println("## RestartLastProposalCompleted==i, we increment");
						//continue; //move to next item in selected list
						//rather than continue (which will repeat the above block) we assign and increment, so that we can start from next proposal
						i = restartLastProposalCompleted + 1;	
					}
					//assuming the process processes peps in ascending order					
					if(restartLastProposalCompleted==0  || restartLastProposalCompleted < i) { //if we have no stored messages, means we have not processed any
						restartLastProposalCompleted = i; //if continue from the beginning of the passed pep
						messageIDForRestart = 0;	//its a different pep and we start from zero for that pep
						System.out.println("\t Here changing (restartLastProposalCompleted==0  || restartLastProposalCompleted<i), restartLastProposalCompleted="+restartLastProposalCompleted); 
						//System.out.println("i="+i);
					}				
					//main skipping ..restartLastProposalCompleted value is used here to come to that proposal
					boolean skipped=false;  //
					if(restartLastProposalCompleted > i ) { //|| restartLastProposalCompleted.equals(i) ){	// || restartLastProposalCompleted==i  	//if
						//System.out.println("here skipping (restartLastProposalCompleted>i), restartLastProposalCompleted="+restartLastProposalCompleted);
						skipped=true;					
						//continue;		//we start skipping so that we come to that proposal
						i = restartLastProposalCompleted + 1;	//rather than continue (which will repeat the above block) we assign and increment
					}
					if(skipped) {	System.out.print(" restartLastProposalCompleted="+restartLastProposalCompleted + "skipped till "+ i);		}
					System.out.println("Restart: ProposalNumberForRestart+1: " +(restartLastProposalCompleted+1) + ", messageIDForRestart+1: " +messageIDForRestart);
				}
				//System.out.println("dddddddddi="+i);
			 boolean checkedAccepted= false;	
			 
			 for(String labelToCheck : statesToGetReasons) {
				 
			 //proposal = i;
			 candidateSentencesList.clear();//clear this after/before every new proposal
			 int count=0; ResultSet rs = null; String sqlString = "";
			 /* sept 2018 This block of code had to be commented as it gave incorrect states (states which did not exist). Eg for pep 246, it the triple clausie checking output accepted but it did not exist
			    this affected teh ML as too many false records were collected for accepted, so we only get the states from the daniel data - commit messages
			    //maybe for substates we can still use this section of code  
			 String sqlString = "SELECT pep,messageid,author,date,timestamp, clausie,ps,currentSentence,ns, pp,ep,np,subject,relation,object "
					// + " from results_postprocessed where messageID = " + mid //test if sentence is code or english
					 + " from results_postprocessed where pep = " + i + " and clausie like '"+labelToCheck+"' "  // use %label% to match similar labels + " AND ( " + state_str  + ")"   //and pep =308 "
					 + " order by pep asc, clausie asc, timestamp asc";
			 ResultSet rs = stmt.executeQuery(sqlString);  //date asc		
//			 System.out.println("\nLooking for Reasons for States : "+ state_str.replaceAll(" clausie LIKE", "") + " Num of Proposals: " + prp.mc.returnRSCount(sqlString, conn));
			 //System.out.println("\nLooking In Current Message:");
			 int count = prp.mc.returnRSCount(sqlString, conn);
			 System.out.println("\nProposals: " + i + " Num of Result(Postprocessed) Labels: " + count);
			 */
			 
			 //if for some reason we did not find any label in our state/substate extraction..we check commit messages..sometimes out state extraction might not capture
			 //feb 2019, make sure we have distinct states here, sometimes the state table has duplicate states 
			// if(count==0) {
			 sqlString = "SELECT pep,messageid,author,date2 as date,datetimestamp as timestamp, email as clausie,'' as ps,'' as currentSentence,'' as ns,'' as pp,''as ep,'' as np,'' as subject,'' as relation,'' as object "
						// + " from results_postprocessed where messageID = " + mid //test if sentence is code or english
						 + " from "+stateTable+" where pep = " + i + " and email like '%"+labelToCheck+"%' "  // + " AND ( " + state_str  + ")"   //and pep =308 "
						 //+ " and messageid > 204000 AND messageid < 205000  "
						 + " order by pep asc, clausie asc, timestamp asc";
				 rs = stmt.executeQuery(sqlString);  //date asc		
//					 System.out.println("\nLooking for Reasons for States : "+ state_str.replaceAll(" clausie LIKE", "") + " Num of Proposals: " + prp.mc.returnRSCount(sqlString, conn));
				 //System.out.println("\nLooking In Current Message:");
				 count = prp.mc.returnRSCount(sqlString, conn);
				 System.out.println("\tFrom Commit Messages: Proposals: " + i + " Num of Committed States for state ("+labelToCheck+") = " + count);
				 //Feb 2019, if we are going to process 'final' and we have already found the 'accepted' states, then we skip 'final'
				 if(count>0) {
					 if(labelToCheck.equals("accepted"))	 checkedAccepted = true;
					 if(labelToCheck.equals("final") && checkedAccepted) {
						 System.out.println("Already Checked Accepted so skipping Final"); continue; //skip
					 }
				 }
				 
			 //}
			 Integer processed =0; //we want to process atleast one message before we exist after every proposal
			 Integer pepNumber,mid;
			 String label="",ps="",currentSentence="",ns="",ep="",np="",pp="",sub="",rel="",obj="",author="";
			 ArrayList<ArrayList<String>> templistOLists = new ArrayList<ArrayList<String>>();
			 ArrayList<String> listHavingCurrentLabel = new ArrayList<String>();
			 ArrayList<String> locations = new ArrayList<String>();
			 boolean processedMessage = false,sameMessageAsStateLabel=true;
			 Integer paragraphCounter=0,totalParagraphs=0;
			 String messageLocation= "stateMessage";
			 double sentencelocationProbability[] = {0.9,0.7,0.7,0.5,0.4,0.4}; 
			 boolean isEnglish=false; 
			 
			 while (rs.next()) {
				 processed++;
				 System.gc();
				 //So many fields are read as i initially thought we will extract from the substate results table - which we will need in case of JEPs where there are no states committed
				 pepNumber = rs.getInt("pep");				label = rs.getString("clausie").replace("Status : ","");				ps = rs.getString("ps");					
				 currentSentence = rs.getString("currentSentence");	ns = rs.getString("ns");				pp = rs.getString("pp");				//System.out.println("Showing Causal Relation for pep "+pepNumber + " Label (" + label + ") Paragraph(" + pp +")");
				 //extractCausalFromParagraphs(lp, tokenizerFactory, pp);
				 ep = rs.getString("ep");				//System.out.println("Showing Causal Relation for pep "+pepNumber + " Label (" + label + ") Paragraph(" + ep +")");				 
				 np = rs.getString("np");					sub = rs.getString("subject");					rel = rs.getString("relation");					obj = rs.getString("object"); 				//System.out.format("%5d%30s%150s%150s%150s", pepNumber,label,ps,currentSentence,pp);
				 mid = rs.getInt("messageID");				Date dateOfStateMessage = rs.getDate("date"); 			author = rs.getString("author");	
				 currentSentence = prp.psmp.removeLRBAndRRB(currentSentence);		currentSentence = prp.psmp.removeDivider(currentSentence);
				 currentSentence = prp.psmp.removeUnwantedText(currentSentence);		currentSentence = prp.psmp.removeDoubleSpacesAndTrim(currentSentence);	
				 //new added june 2018
				 currentSentence = currentSentence.replaceAll("``", "").replaceAll("\"", ""); //.replaceAll("+", "").replaceAll("\\+", "");
				 System.out.println("\tLABEL TO CHECK: " + label + " dateOfStateMessage: " + dateOfStateMessage);
				 
				 //we limit the labels we are searching for to the label we just found. i.e looking for reasons for 'accepted' label, we dont want to match 'deferred'				 
				 //go through the list of lists for states and substates and remove all otehrs, other than accepted
				 
				 templistOLists = ppm.getAllStatesList_StatesSubstates();				 				 
				 templistOLists = returnLabelList(label, templistOLists);
				//set this label list
				 ppm.setLabelList(templistOLists); //if label is not found in list, then it will search in entire lists of lists - but it should find..make sure it finds
				 
				 //new Probabilty 
				 //Probability prob = new Probability();	
				 //prob.setProposalNum(pepNumber);		prob.setAuthor(author);	 prob.setMid(mid); 
				 //FINDING REASONS HERE-- but do we need to check reaons in all different ways for each triple? only check roles and reasons for specified labels, not all				 
				 foundReasonOnce = false;showCIEDebugOutput=false;
				 processedMessage = false;sameMessageAsStateLabel=true;
				 try {
//					 System.out.println("\nLooking In Current Message: for Reasons for State: " + label + " Date: "+dateOfStateMessage + "MID: " + mid);
					 //System.out.println("\t\t\t Reasons_for_sentence_whereTriplesMatched");							
					 
					 //FIRST Layer...Looking for reason in the same message as the state
					 //previous, next sentences and nearby paragraphs
					 //cue phrases, positive and negative words...
					 //temp check if effect sentence contains the label					 					 
					 messageLocation= "stateMessage";	//System.out.println("\n\nparagraph:"+para+" \n\n");
					 //prob.setMessageLocation(messageLocation); 
					 paragraphCounter=0;totalParagraphs=0;
					 // we pass these combinations to the final probability calculator, with the termlocator probability after calculating it
					 locations.add(currentSentence);	locations.add(currentSentence+ps); locations.add(currentSentence+ns);  
					 locations.add(ep);	locations.add(ep+pp); locations.add(ep+np);
					 
					 int counter=0;
					 //although this message will also be processed later as its within the dates, we wonmt be able to know the next and previosu sentenecs
					 //and paragraphs and the probability is higher is reason sentence is different for each 
					 for (String p: locations) {
						//for getting keyword terms through relation extraction and verb-pair matching for reason extraction later 
						 if(retrieveKeywords) {  
							 //extract clauses and  triples from same sentence, nearby sentences and paragraphs
							 //for nearby messages - this is going to be tough
							 //and identify the subjct, verb and objects here - maybe we can come across the multiple terms which make up a reason, like 'community consensus'
								//System.out.println("here a: "+p);
							 //start..extract triples from same and nearby sentences
							 if(currentSentence==null|| currentSentence.isEmpty() || currentSentence.equals("") || currentSentence.length()==0) {}
							 else {	//System.out.println("here b: " +p);
	//$$								 String tripleArray[][] = ciem.computeClausIE(p, showCIEDebugOutput);
								 //extract badn insert into db table agt the same time
	//								 ciem.extractClauseIERelations(conn,p, ps,ns, ep,pp,np,proposal, mid, author, dateOfStateMessage, tableToStoreExtractedRelations,false );		
								 
								//can conmtinue to process onto next sentence if not
								isEnglish = prp.getEnglishOrCode().isEnglish(currentSentence); //Check if sentence is Englsh or code
								 
								 //extract and insert into db table agt the same time
								//dec 2018, we set isfirstpara and islast para as false for the timebeing
								 ciem.extractClauseIERelations(conn,prp.getProposalIdentifier(),currentSentence, ps,ns, pp,ep,np,pepNumber, mid, author, dateOfStateMessage, tableToStoreExtractedRelations,
										 false,0,0,0,isEnglish, false, false,"","",false,"","","","");	
								 //we dont want to do this for each sentence in every nearby message
								 //it would give better reasults if its done in the state message
				/*				if(processedMessage == false) {
									 cec.docWordCounter(currentSentence);
								 }							 
								 //pass it to verb pair, etc functions
								 cec.appendToCorpus(currentSentence); 		*/
								 processedMessage= true;
							 }							 
						 }
						 if(suggestCandidateSentences)	{ 
							//for each label in a proposal, make sure sentence is not checked before..too many instances of same sentence show up as candidate, one after another
							boolean found= false;
							for (StringPair sp : spList) { 
								if (sp.getLabel().equals(label) && sp.getSentenceOrParagraph().equals(currentSentence)) {
									found=true; 	break;
								}
							}
							if(!found) {
								//sentenceLocationHintProbability
								//if all reason terms found in a sentence but location is 
								//same sentence as state label..probability is 0.9, if previous and next sentence ..probability is 0.7
								//if previous and next paragraph ...probability is 0.5, if found in nearby messages < 2 weeks..probability is 0.5
								
								//but if the terms are spanning across
								//combination of same sentence as label and either next and previous sentence..probability is 0.7
								//combination of same paragraph as label and either next and previous sentence..probability is 0.5
								System.out.println("\tChecking same message for label: " + label); //in this case could be state commit message ir triple message
								//double dateDiffProbability =0.5, authorRoleProbability = 0.5;
								//,0.0,locationProbability[counter]
//								prob.setProposalNum(pepNumber);		prob.setAuthor(author);	 prob.setMid(mid);		//prob.setTermsLocationWithMessageProbabilityScore(sentencelocationProbability[counter]);
//								prob.setMessageLocation(messageLocation); 
								int dateDiff=0; String authorsRole = "bdfl"; String sentenceLocation="";
								//prob.setDateDiff(dateDiff);    	prob.setAuthorsRole(authorsRole);	//assign high values
								//prob.setMessageSubjectHintProbablityScore(0.5);   //because its the state message, highly likely its as important message as 'pep 308 resolution', therefrore we give high weitage although there is no message subject
								
								ppm.checkSentenceOrParagraphForReasonTermsCombinations(conn,paragraphCounter,totalParagraphs,label, matchExactTerms,
										mid,sub,pepNumber, dateOfStateMessage, dateOfStateMessage,currentSentence,"","","","",false,false, false,false,false,"", "",  sentenceLocation, authorsRole, dateDiff,messageLocation, 
										false,false,false,false,false,false,0,0,0,0,0,
										messageSubjectStatesSubstatesList, messageSubjectDecisionTermList, messageSubjectVerbList,
										messageSubjectProposalIdentifiersTermList, messageSubjectEntitiesTermList, messageSubjectSpecialTermList,
										"PEPSummary", //messageType 
										true, 0.5, //messsageTypeIsReasonMessage, messsageTypeIsReasonMessageProbabilityScore,  ..we just assign some values for the time being
										false, //if special terms exist in message we give hogher weightage
										sentencelocationProbability[counter]);	//we pass a different probability depending on location
										candidateSentencesList.add(currentSentence);	//add sentence to List
										StringPair spair = new StringPair(label, currentSentence);
										spList.add(spair);									
								//String v_tableNameToStore = "autoExtractedReasonCandidateSentences"; 								
							}						 
						 }
						 counter++;
					 }	//end loop for each section (sentence or paragraph)
					 
					 //for each of the label, get and show candidate reasons, starting with the current and nearby sentences - using causal connectives
					 
					 //Second layer- check in previous messages, Steps below:
					 //System.out.println("\nLooking In Previous Messages: for Reasons for State: " + label + " Date: "+dateOfStateMessage);
					 //for bdfl and pep author messages about 2 weeks before 							//and pep =308 "
					 //query past messages by bdfl and author
					 //handle checkin messages and process the message
					 //first check for combination of terms in emtire message and then paragrapgh and sentence level.. line 119 on that script
					 if(suggestCandidateSentences)	{ 
						 messageLocation="proposalSummary";
						 //prob.setProposalNum(pepNumber);		prob.setAuthor(author);	 prob.setMid(mid);
						 //prob.setMessageLocation(messageLocation);   
						 int dateDiff=0; String authorsRole = "bdfl";
						 //prob.setDateDiff(dateDiff);    	prob.setAuthorsRole(authorsRole);	//assign high values
						 //prob.setMessageSubjectHintProbablityScore(0.5);   //because its the proposal summary, highly likely its as important message as 'pep 308 resolution', therefrore we give high weitage although there is no message subject
						 //first check the pep summary 
						 System.out.println("\txxx Checking proposal summary for label: " + label);
						 ppm.checkProposalSummary(conn, prp,label,  v_dateTimeStamp, matchExactTerms, //majorState,
								 pepNumber, mid,sub, dateOfStateMessage,retrieveKeywords, /*cec, */ ciem, showCIEDebugOutput, suggestCandidateSentences, tableToStoreExtractedRelations,false, 
								 candidateSentencesList,spList,authorsRole,dateDiff, messageLocation);	//terms Location Hint Probability  is high = 0.9
						 //System.out.println("\txxx FINISHED");
						 System.out.println("\tChecking nearby messages for label: " + label);
						 messageLocation="nearbyMessages";					 //prob.setMessageLocation(messageLocation);  
						 ppm.cheackNearbyMessages(conn, prp,label,  v_dateTimeStamp, matchExactTerms, //majorState,
							 pepNumber, messageIDForRestart, sub, dateOfStateMessage,retrieveKeywords, /*cec,*/ ciem, showCIEDebugOutput, suggestCandidateSentences, tableToStoreExtractedRelations,false, 
							 candidateSentencesList,spList,messageLocation);  //for all nearby messages, the terms Location Hint Probability  is always 0.5
						 		//last parameter = false, which is whether to check in the same message only	
					 }
					 
				 } catch (Exception e) {
					 // TODO Auto-generated catch block
					 e.printStackTrace();
				 }
				 //							resultObject.setClausIE(label);
				 //						}						
				 //					}

				 //check all code below
				 if (foundReasonOnce){
					 System.out.println("\t\t\t########## FOUND REASON TRIPLES IN MATCHED TREIPLE OUTPUTTING()"  +  " " + libraryToCheck);
					 //result.setReason(foundReasonOnce.getReason(), libraryToCheck);
				 }
				 else{
					 //									System.out.println("not conditional");
				 }	
				 //prob=null;
			 }	//end while
			 } //end for each labelto check
			 if(retrieveKeywords) {
				 //create_explicit_corpus cec
//xx				 List<String>  s = l.getAllStatesList_StatesSubstates();
//xx				 cec.writeGatheredDataToFile(s);
				 System.out.println("\naccumulate done");
			 }
			 //Feb 2019, if atleast one record is written in table for this pep, we exit, or we continue processing
			 try{
					String query= "SELECT proposal FROM autoextractedreasoncandidatemessages where proposal = "+i+" ;";
					Statement st = conn.createStatement();			ResultSet rs = st.executeQuery(query);
					if (rs.next())			{	
						System.out.println("Atleast one record written in autoextractedreasoncandidatemessages");
						System.exit(0);	//we exit after each proposal
					}					
					st.close();
				}	catch (Exception e)	{
					System.err.println("Got an exception 234! ");	System.out.println(StackTraceToString(e)  );	System.err.println(e.getMessage());
				}	
			 
			 
			 } //end for loop for each proposal/pep
			 stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}				
		ppm.displayResultsReasons(tripleProcessingResultList);				
		//causal extraction relation extracted sentences
		//select previous,current and next sentences from results_postprocessed
		//try only for selected labels ...accepted, rejected etc
	}
	
	public static String StackTraceToString(Exception ex) {
		String result = ex.toString() + "\n";
		StackTraceElement[] trace = ex.getStackTrace();
		for (int i=0;i<trace.length;i++) {
			result += trace[i].toString() + "\n";
		}
		return result;
	}

	private static ArrayList<ArrayList<String>> returnLabelList(String label, ArrayList<ArrayList<String>> templistOLists) {
		ArrayList<ArrayList<String>> tempListOfLists = new ArrayList<ArrayList<String>>();
		ArrayList<String> listHavingCurrentLabel = new ArrayList<String>();
		for (ArrayList<String> line : templistOLists) {  //for each line              	
				//System.out.print(line+ " : ");
				for (String term : line) {	//for each term in line
		    		//ArrayList<String> tempLine = new ArrayList<String>(); //line with found label will be stored here if found 
		    		//search for the label in the terms
		    		if(term.toLowerCase().contains(label.toLowerCase())) {     	
		    			//if in any line, any term matches, then return only this line and forget all other lines...
		    			//else return error or just consider all lines or consider closest match 
		    			listHavingCurrentLabel = line;
		    			//assign line to temp list
		    			tempListOfLists.add(listHavingCurrentLabel);
		    			System.out.println("##################### Label term: "+label+" found in term '"+term+ "', ");
		    			return tempListOfLists;
		    		}
				}
		}
		return templistOLists; //return original lists of lists if term not found in any list
	}
	
	public static String cleanAndOutput(String v_pos, String pos, Integer posInt)
    {
    	String partOfSpeech = "";
    	// System.out.println("\t inside cleanandoutput " + v_pos + " v_pos");
		if (posInt == 1)
			partOfSpeech = "subject";
		else if (posInt == 2)
			partOfSpeech = "verb";
		else if (posInt == 3)
			partOfSpeech = "object";

		if (!(v_pos == "") && v_pos != null) {
			// string cleaning - very important to do this
			v_pos = cleanPOS(v_pos);

			// just output the following message - for debug purposes
			if (v_pos.toLowerCase().contains(pos.toLowerCase())) {
				// System.out.println("\t " + partOfSpeech + " matched");
			} else {
				// System.out.println("\t " + partOfSpeech + " not matched");
			}
		} else {
			// System.out.println("\t " + partOfSpeech + " empty or null");
		}
    	return v_pos;
    }
	
	public static String cleanPOS(String v_pos){
		//System.out.println("v_pos" + v_pos);
		if(v_pos==null || v_pos=="" || v_pos.isEmpty())
		{}
		else{			
			v_pos = v_pos.toLowerCase();	
			v_pos = v_pos.replaceAll("[^a-zA-Z0-9.',?!\\s]+"," ");
		}
		return v_pos;
	}
	
	public static ArrayList<Integer> getallPepsWithLabel(String label, Connection conn){
		ArrayList<Integer> peps = new ArrayList<Integer>();		
		try	{
			String query= "SELECT pep FROM results_postprocessed where clausie like '%" + label + "%' order by pep asc";			
			// create the java statement
			Statement st = conn.createStatement();			ResultSet rs = st.executeQuery(query);
			Integer counter=0;
			while (rs.next())	{				
				Integer pepNumber = rs.getInt("pep");							
				peps.add(pepNumber);		
				counter++;
			}
			System.out.println("\n\nTotal number of peps for label "+ label + " is " + counter + " list size " + peps.size());
			st.close();
		}	catch (Exception e)	{
			System.err.println("Got an exception! ");
			System.err.println(e.getMessage());
		}
		return peps;
	}
}
