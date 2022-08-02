package miner;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import com.google.common.collect.ObjectArrays;

import readRepository.readRepository.ReadLabels;
import connections.MysqlConnect;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.process.DocumentPreprocessor;
import wordnet.jaws.WordnetSynonyms;
import GUI.helpers.GUIHelper;
import miner.process.LabelTriples;
import miner.process.PythonSpecificMessageProcessing;
import miner.processLabels.TripleProcessingResult;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import utilities.ParagraphSentence;
import utilities.PepUtils;
import utilities.StateAndReasonLabels;
import wordforms.GetWordForms;

//sept 2020. We use this script modified it to gte the regular patterns in the 300 reason sentences.


//march 2020. we used this script to amnd modified it to list the number of sentences we initially got from 30 peps which had the 
// initial list of concepts

//This script is now main script fro current and nearby sentence and paragraph extraction for specified terms,
//But rather than quering the entire repository one by one for each string set, we query the allsentences table and all paragraphs table
//This has to be integrated in the main script as an option

//This script is the first step in exploring any Repository for the first time.
//It takes the Proposals identifier, and STATES, and returns sentences which have a PI and a state
//April 2019. This script is modified for querying JEPs

//The script checks if all provided terms are in the same sentence 
//sometimes we can shorten the results by adding additional term "pep" and "proposal" additionally to each term 
public class FindWhichTermPatternsAreFrequent  {
	static PythonSpecificMessageProcessing pm = new PythonSpecificMessageProcessing();
	static ParagraphSentence ps = new ParagraphSentence();
    //reading keywords
    static boolean hardCodedTerms = true, readTermsFromFile = false, labelsFromDB=false;
    //storing results
    static boolean resultsToConsole=true, resultsToFile = false, resultsToDatabse = true;
    //search which PEPs
    static boolean allPeps = false, selectedPEPs = true;
    static Integer[] selectedListItems = {289,308,313,318,336,340,345,376,388,389,391,414,428,435,441,450,465,484,488,498,485,471,505,3003,3103,3131,3144};
    static String proposals = "";
    //search how
    static boolean adjacentTermsInSentence = false, termsAnywhereInSentence = true, termsAnywhereInParagraph = false; //previous, current and next paragraph
    //add these terms to each term in the array    
    static boolean addPEP = true, addProposal = true;
    static String tableToStore = "initialsentences";// "keywordmatchingremainingpeps" ; //keywordMatching29PEPs";	//table to store the sentences which contain the keywords 
    static Boolean matchExactTerms = false;
    static String proposalIdentifier = "pep"; //jep"; //pep
    
    static String tablename = "allsentences", field = "sentence"; //allsentences
    
    //static Boolean outputForDebug = false, matchWholeTerms = false;
    
    static PepUtils pu = new PepUtils();
        
  //we want to find the reasons for the following states for the 29 PEPs
    static String[] mainStates =  {"accept","approve","reject"} ; //,"wrote","written","propose","draft","update","revise","withdraw","defer","postpone","replace","incomplete","supercede","final","close","pending"};
  	//additional states we have found
    static String[] DMConcepts =  {"poll","vote","voting","consensus","no consensus","majority","support","feedback","lazy","bdfl",
    		"bdfl delegate","delegate","pronouncement","decision","controversy","pep number"," vote results","poll results","informal voting",
    		"delegate accepted","guido","support","feedback","bdfl pronouncement","debate","controversy","wrangling","stalled","discussion","round",
    		"propose","proposal","draft","idea","editor","review","update",
    		"accept","approve","reject","wrote","written","propose","draft","update","revise","withdraw","defer","postpone","replace","incomplete","supercede","final","close","pending"};	//additional states we have found
    //discussion
    //static String[] discussion = {"discussion pep","discussion idea","discussion syntax"};	//"discussion",	//takes too long - shud be done individually
    //idea stage			
    //static String[] ideaStage = {"idea no support","idea support","idea","support idea","pep number","request pep number","pep editor","no consensus", "czar"};
    //all below is WHO decided
    static String[] entities = {"pep dictator","pep champion","bdfl delegate committee","committers vote core developers","pep czar","BPD","BPC","FLUFL","committee decision","committee decisions","dictatorship","dictator","delegate"}; 
		//all below is on HOW pep was decided
    static String[] reasons = {"bdfl majority","guido community consensus","bdfl community consensus","decision overridden","best judgement","community consensus","rant","dictatorship","dictator","decision",
    		"no consensus reached","community consensus","approve","consensus","rough consensus","support","majority","no majority","no support","poll","vote","decision","pronouncement", 
    	    "feedback","stalled","delegate", "overidden"}; 
    
    //now about reasons
    //standard states changes by users i want to capture 
	//"draft" ,	dont need draft as its always by author
    
    static String ActualReasons[] = {"discussion","debate","bdfl pronouncement","poll result","voting result","vote result","consensus","no consensus","feedback","favorable feedback",
			"support","no popular support","favourable feedback","poor syntax","limited utility","difficulty of implementation","bug magnet","controversy","majority","wrangling"};  
			//lack of an overwhelming majority 
	
       
	//find the order of events
	//"the PEP"	"was accepted"	"by Guido + After a short discussion
    static String storyline[] = {"following", "after","later", "afterwards"};	
    
     //Note: lazy majority and lazy consensus returns zero rows
    static String fileName    = "c:\\scripts\\pepLabels\\pep.txt", outFielName = "c:\\scripts\\outer.txt";
    static ReadLabels b = new ReadLabels();
            
    static FileWriter writer = null,writer2 =null,writer3 =null;
    static Date date = new Date();
    static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
    
    private static String removeLastChar(String str) {
        return str.substring(0, str.length() - 1);
    } 
    
    static boolean sentenceOrParagraph_containsReasonsTerms , 	sentenceOrParagraph_containsReasonsIdentifierTerms, 		sentenceOrParagraph_containsStatesSubstates, 
	sentenceOrParagraph_containsIdentifierTermList , 	sentenceOrParagraph_containsPositiveWords, 		  			sentenceOrParagraph_containsNegativeWords,
	sentenceOrParagraph_containsVerbTerms,				sentenceOrParagraph_containsIdentifierTerms,		  		sentenceOrParagraph_containsEntitiesTerms,
	sentenceOrParagraph_containsSpecialTerms,			sentenceOrParagraph_containsSpecialTermsInMessageSubject,
	sentenceOrParagraph_containsNegationTerms,		sentenceOrParagraph_containsDecisionTerms,
	
	restOfParagraph_containsStatesSubstates, restOfParagraph_containsIdentifierTerms, restOfParagraph_containsReasonsTerms, restOfParagraph_containsEntitiesTerms, restOfParagraph_containsSpecialTerms, 
	restOfParagraph_containsDecisionTerms,
	messsageTypeIsReasonMessage,
	isFirstParagraph,  isLastParagraph, //if reason sentence found, is in first or last paragraph ..carries more weight as many times reason are found to be there
	reasonLabelFoundUsingTripleExtraction,messageContainsSpecialTerm,	//Feb 2019
	prevParagraph_containsSpecialTerms;									//feb 2019
    
  //too many parameters of lists beings passed between too many functions so better to declare it here as class level variable 
  	static List<String>  verbs  = new ArrayList<String>();
  	static List<String>  committedStates  = new ArrayList<String>();
  	static List<String>  subStates  = new ArrayList<String>();
  	static ArrayList<ArrayList<String>>  allStatesList_StatesSubstates  = new ArrayList<ArrayList<String>>();
  	static ArrayList<ArrayList<String>>  temp_allStatesList_StatesSubstates  = new ArrayList<ArrayList<String>>();
  	static ArrayList<ArrayList<String>>  labelList  = new ArrayList<ArrayList<String>>();	//temp list will hold only terms related to the current label, the reasons of which are being looked for
  	static List<String>  subStatesList  = new ArrayList<String>();
  	static ArrayList<ArrayList<String>>  reasonsList  = new ArrayList<ArrayList<String>>();
  	static List<String>  reasonIdentifierTerms  = new ArrayList<String>(); 
  	static List<String>  proposalIdentifiers  = new ArrayList<String>(); 
  	static List<String>  entitiesList = new ArrayList<String>(); 
  	static ArrayList<ArrayList<String>>  specialOSCommunitySpecificTerms = new ArrayList<ArrayList<String>>();
  	static List<String>  negationTerms = new ArrayList<String>(); 
  	static List<String>  conditionalTerms = new ArrayList<String>();
  	static List<String>  positiveWords = new ArrayList<String>(); 
  	static List<String>  negativeWords = new ArrayList<String>();
  	static List<String>  notCoOccurTerms = new ArrayList<String>(); //dec 2018
  	static ArrayList<ArrayList<String>>  decisionTerms = new ArrayList<ArrayList<String>>();
  	
  	static GetWordForms wf = new GetWordForms();
  	static WordnetSynonyms ws = new WordnetSynonyms();
  	static GUIHelper guih = new GUIHelper(); 
  	
  	static InputStream inputStream;
  	static SentenceModel model;
  	static SentenceDetectorME detector;
  	
  	static boolean outputForDebug = true, outputList=true;
  	
  	//MAR 2019, we try to capture BDFL/Delegate pronouncement messages
  	//static String acceptRejectTerms[] = {"accept","accepting","accepted","add","approve","approving","reject","rejecting","rejected"}; 
  	static boolean  matchWholeTerms=true, patternFound=false;
  	
  	static Integer no_pattern_count=0, reasonsTermsFoundCount=0, substatesTermsFoundCount=0,identifierTermsFoundCount=0,reasonsidentifierTermsFoundCount=0,decisionTermsFoundCount=0,
  			verbsListFoundCount=0,entitiesTermsFoundCount=0,specialTermsFoundCount=0, negationTermsFoundCount=0;
  	static Integer pattern_RT_S_PI = 0, pattern_R_S_PI=0, pattern_R_S_E = 0, 
      pattern_R_PI_E = 0, pattern_R_S_RI=0, pattern_DT_R_E=0, pattern_S_PI_E=0, pattern_PI_R=0, pattern_S_R=0, pattern_E_R=0, pattern_PI_DT=0, 
      pattern_E_DT=0,  pattern_PI_S_RI=0;
  	static Integer pattern_S_DT_RI_OR_DT_E=0, pattern_S_DT_RI=0;
    
    public static void main(String[] args)  {
    	
    	initialiseTermLists(ws);
      
    	String sentenceorParagrapghString = "";
        //now we query these combinations in the 'allsentences' table and 'allparagraphs' table    
    	try{  
    		Integer sentenceCount =0;
    		MysqlConnect mc = new MysqlConnect();     	    Connection conn = mc.connect();
        	//Statement stmt = conn.createStatement();
    		//main query used in evaluation 
    		// String sqlManual = "SELECT pep,state,causeSentence, effectSentence, label, dmconcept from " + manualreasonextractionTable + " "
			// + " where consider = 1 and state like '%"+state+"%' and pep = "+pNum+" and LENGTH(causesentence) > 1 order by pep ;" ; 	//and pep = 311
        	String sql = "SELECT causesentence from trainingdata where (state like 'accepted' or state like 'rejected') AND length(causesentence) > 1 AND consider = 1 AND pep < 5000 ;"; 
        	Statement stmt = conn.createStatement(); 	    	   ResultSet rs = stmt.executeQuery( sql );
  	        while (rs.next()){     //check every message   
  	        	sentenceCount++;
  	           	// pepNumber = rs.getInt(1);	           		 uniquePeps.add(pepNumber);
  	           	
  	         	sentenceorParagrapghString = rs.getString(1);
  	         	System.out.println(sentenceCount + ": Sentence = " + sentenceorParagrapghString);  	        
  	           	// System.out.println("Unique Proposal Returned = " + uniquePeps.size());
  	           	//stmt.close();
  	           	
	  	        //september 2020
	  	        String sentenceorParagrapghString_reasonsTermsFoundList = containsTermsFromListOfLists(sentenceorParagrapghString,reasonsList, matchWholeTerms);
	  			sentenceorParagrapghString_reasonsTermsFoundList = sentenceorParagrapghString_reasonsTermsFoundList.startsWith(",") ? sentenceorParagrapghString_reasonsTermsFoundList.substring(1) : sentenceorParagrapghString_reasonsTermsFoundList;
	  			if(sentenceorParagrapghString_reasonsTermsFoundList == null || sentenceorParagrapghString_reasonsTermsFoundList.isEmpty() ||sentenceorParagrapghString_reasonsTermsFoundList.equals("") ||sentenceorParagrapghString_reasonsTermsFoundList.length()==0) {}
	  			else {		
	  						sentenceOrParagraph_containsReasonsTerms = true;  
	  						reasonsTermsFoundCount++; //(countTermsInString(sentenceorParagrapghString_reasonsTermsFoundList));
	  						if(outputForDebug) {
	  							System.out.println("\tReasonsTermsFoundList: ("+ sentenceorParagrapghString_reasonsTermsFoundList+ ") ReasonsTermsFoundCount: "+	countTermsInString(sentenceorParagrapghString_reasonsTermsFoundList));	
	  						}		//p.setWriteToFile(true);
	  			}	  			
	  			//THIRD CHECK...CHECK FOR JUST THE STATE/SUBSTATES TERMS
	  			String sentenceorParagrapghString_statesSubstatesList = containsTermsFromListOfLists(sentenceorParagrapghString, allStatesList_StatesSubstates, matchWholeTerms);	//  labelList
	  			sentenceorParagrapghString_statesSubstatesList = sentenceorParagrapghString_statesSubstatesList.startsWith(",") ? sentenceorParagrapghString_statesSubstatesList.substring(1) : sentenceorParagrapghString_statesSubstatesList;
	  			if(sentenceorParagrapghString_statesSubstatesList == null || sentenceorParagrapghString_statesSubstatesList.isEmpty() || sentenceorParagrapghString_statesSubstatesList.equals("")  || sentenceorParagrapghString_statesSubstatesList.length()==0) {}
	  			else { 
	  					sentenceOrParagraph_containsStatesSubstates = true;
	  					substatesTermsFoundCount++;
	  					if(outputForDebug) {
	  					   System.out.println("\tStatesSubstatesList: ("+ sentenceorParagrapghString_statesSubstatesList+ ") statesSubstatesListTermsFoundCount: "+ countTermsInString(sentenceorParagrapghString_statesSubstatesList));	
	  					} //p.setWriteToFile(true);
	  			}	  			
	  			//FIFTH CHECK...CHECK FOR JUST THE identifiersTermList TERMS		
	  			String sentenceorParagrapghString_identifiersTermList = containsAllTerms(sentenceorParagrapghString,proposalIdentifiers, matchWholeTerms);
	  			sentenceorParagrapghString_identifiersTermList = sentenceorParagrapghString_identifiersTermList.startsWith(",") ? sentenceorParagrapghString_identifiersTermList.substring(1) : sentenceorParagrapghString_identifiersTermList;
	  			if(sentenceorParagrapghString_identifiersTermList == null || sentenceorParagrapghString_identifiersTermList.isEmpty() || sentenceorParagrapghString_identifiersTermList.length()==0) {}
	  			else {			
	  					sentenceOrParagraph_containsIdentifierTerms=true;	
	  					identifierTermsFoundCount++;
	  			  		if(outputForDebug) {
	  			  			System.out.println("\tIdentifiersTermList: ("+ sentenceorParagrapghString_identifiersTermList+ ") identifiersTermListCount: " + countTermsInString(sentenceorParagrapghString_identifiersTermList));				 
	  			  		}	//p.setWriteToFile(true);
	  			}	
	  			//sentenceOrParagraph_containsReasonsTerms && sentenceOrParagraph_containsStatesSubstates && sentenceOrParagraph_containsIdentifierTerms
	  			
	  			//SECOND CHECK...CHECK FOR JUST THE REASON TERMS
	  			String sentenceorParagrapghString_reasonsIdentifierTermsList = containsAllTerms(sentenceorParagrapghString,reasonIdentifierTerms, matchWholeTerms);
	  			sentenceorParagrapghString_reasonsIdentifierTermsList = sentenceorParagrapghString_reasonsIdentifierTermsList.startsWith(",") ? sentenceorParagrapghString_reasonsIdentifierTermsList.substring(1) : sentenceorParagrapghString_reasonsIdentifierTermsList;
	  			if(sentenceorParagrapghString_reasonsIdentifierTermsList == null || sentenceorParagrapghString_reasonsIdentifierTermsList.isEmpty() ||sentenceorParagrapghString_reasonsIdentifierTermsList.equals("") || sentenceorParagrapghString_reasonsIdentifierTermsList.length()==0) {}
	  			else {		
	  					sentenceOrParagraph_containsReasonsIdentifierTerms=true;	
	  					reasonsidentifierTermsFoundCount++;
	  					if(outputForDebug) {
	  						System.out.println("\tReasonsIdentifierTermsList: ("+ sentenceorParagrapghString_reasonsIdentifierTermsList+ ") ReasonsIdentifierTermsFoundCount: "+ countTermsInString(sentenceorParagrapghString_reasonsIdentifierTermsList));	
	  					}	//p.setWriteToFile(true);
	  			}  				
	  			//FOURTH CHECK...CHECK FOR JUST THE VERB TERMS
	  			String sentenceorParagrapghString_verbList = containsAllTerms(sentenceorParagrapghString,verbs, matchWholeTerms);
	  			sentenceorParagrapghString_verbList = sentenceorParagrapghString_verbList.startsWith(",") ? sentenceorParagrapghString_verbList.substring(1) : sentenceorParagrapghString_verbList;
	  			if(sentenceorParagrapghString_verbList == null || sentenceorParagrapghString_verbList.isEmpty() || sentenceorParagrapghString_verbList.length()==0) {}
	  			else {			
	  					sentenceOrParagraph_containsVerbTerms=true; 	//have to set verblist setting function
	  					verbsListFoundCount++;
	  					if(outputForDebug) {
	  							System.out.println("\tVerbList: ("+ sentenceorParagrapghString_verbList+ ")");       
	  					}  
	  				}			//System.out.println("verbList: "+ verbList);
	//  			System.out.print("CHECKING entitiesList >> ");  for (String line : entitiesList)     {                	System.out.print(line+ " ");            }		  System.out.println(" ");
	  			//SIXTH CHECK...CHECK FOR JUST THE identifiersTermList TERMS		
	  			String sentenceorParagrapghString_entitiesTermList = containsAllTerms(sentenceorParagrapghString,entitiesList, matchWholeTerms);
	  			sentenceorParagrapghString_entitiesTermList = sentenceorParagrapghString_entitiesTermList.startsWith(",") ? sentenceorParagrapghString_entitiesTermList.substring(1) : sentenceorParagrapghString_entitiesTermList;
	  			if(sentenceorParagrapghString_entitiesTermList == null || sentenceorParagrapghString_entitiesTermList.isEmpty() || sentenceorParagrapghString_entitiesTermList.length()==0) {}
	  			else {			
	  					sentenceOrParagraph_containsEntitiesTerms=true;	
	  					entitiesTermsFoundCount++;
	  					if(outputForDebug) {
	  							System.out.println("\tEntitiesTermList: ("+ sentenceorParagrapghString_entitiesTermList+ ") entitiesTermListCount    " + 	countTermsInString(sentenceorParagrapghString_entitiesTermList));
	  					} //					setWriteToFile(true);
	  			}	
	  			
	//  			System.out.print("CHECKING specialTerms >> ");  for (String line : specialOSCommunitySpecificTerms)     {                	System.out.print(line+ " ");            }		  System.out.println(" ");
  			//SEVENTH CHECK...CHECK FOR JUST THE special TERMS	- contains entities	
	  			String sentenceorParagrapghString_specialTermList = containsTermsFromListOfLists(sentenceorParagrapghString,specialOSCommunitySpecificTerms, matchWholeTerms);
	  			sentenceorParagrapghString_specialTermList = sentenceorParagrapghString_specialTermList.startsWith(",") ? sentenceorParagrapghString_specialTermList.substring(1) : sentenceorParagrapghString_specialTermList;
	  			if(sentenceorParagrapghString_specialTermList == null || sentenceorParagrapghString_specialTermList.isEmpty() || sentenceorParagrapghString_specialTermList.length()==0) {}
	  			else {			
	  					sentenceOrParagraph_containsSpecialTerms=true;
	  					specialTermsFoundCount++;
	  					if(outputForDebug) {
	  						System.out.println("\tSpecialTermList: ("+ sentenceorParagrapghString_specialTermList+ ")  specialTermList "   + countTermsInString(sentenceorParagrapghString_specialTermList));
	  					}
	  			}	
	
	  			String sentenceorParagrapghString_decisionTermList = containsTermsFromListOfLists(sentenceorParagrapghString,decisionTerms, matchWholeTerms);
	  			sentenceorParagrapghString_decisionTermList = sentenceorParagrapghString_decisionTermList.startsWith(",") ? sentenceorParagrapghString_decisionTermList.substring(1) : sentenceorParagrapghString_decisionTermList;
	  			if(sentenceorParagrapghString_decisionTermList == null || sentenceorParagrapghString_decisionTermList.isEmpty() || sentenceorParagrapghString_decisionTermList.length()==0) {}
	  			else {			
	  					sentenceOrParagraph_containsDecisionTerms=true;	decisionTermsFoundCount++;
	  					if(outputForDebug) {
	  						System.out.println("\tDecisionTermList: ("+ sentenceorParagrapghString_decisionTermList + ")  decisionTermCount  " + countTermsInString(sentenceorParagrapghString_decisionTermList));
	  					}//		p.setWriteToFile(true);
	  			}	
	  			
	  			//in case of a sentence
	  			//check negation or conditional terms dont exist, ideally not before state or reason term
	//  			if (stringLoc=="sentence") {
	  				//also need to consider conditionalterms		
	  			String sentenceorParagrapghString_negationTermList = containsAllTerms(sentenceorParagrapghString,negationTerms, matchWholeTerms);
	  			if(sentenceorParagrapghString_negationTermList == null || sentenceorParagrapghString_negationTermList.isEmpty() || sentenceorParagrapghString_negationTermList.length()==0) {}
	  			else {
	  				sentenceOrParagraph_containsNegationTerms=true;	negationTermsFoundCount++;
	//  				System.out.print(" MATCHED negationTermList: "+ negationTermList);
	  				//july 2018..there are some combinations (in reasonlist) where negation is part of the terms ... eg  'no objections'..so we consider them
	  				if(sentenceorParagrapghString_reasonsTermsFoundList.contains(sentenceorParagrapghString_negationTermList)) {	// ...try to improve code that contains atleast one	  					
	  				}
	  				else {
	  					//sentenceOrParagraph_containsNegationTermList=true;	//negationTermPenalty = 0.8;	//penalty higher if just before and within few terms of the state or reason 
	  					//p.setNegationTermPenalty(0.8);
	  				}	
	  			}
	  /*	  			
	  			//contains positive or negative words like flak, wrangling, 
	  			String sentenceorParagrapghString_positiveWordList = containsAllTerms(sentenceorParagrapghString,positiveWords, matchWholeTerms);
	  			sentenceorParagrapghString_positiveWordList = sentenceorParagrapghString_positiveWordList.startsWith(",") ? sentenceorParagrapghString_positiveWordList.substring(1) : sentenceorParagrapghString_positiveWordList;
	  			if(sentenceorParagrapghString_positiveWordList == null || sentenceorParagrapghString_positiveWordList.isEmpty() || sentenceorParagrapghString_positiveWordList.length()==0) {}
	  			else {			p.setSentenceOrParagraph_containsPositiveWords(true);	}
	  			
	  			String sentenceorParagrapghString_negativeWordList = containsAllTerms(sentenceorParagrapghString,negativeWords, matchWholeTerms);
	  			sentenceorParagrapghString_negativeWordList = sentenceorParagrapghString_negativeWordList.startsWith(",") ? sentenceorParagrapghString_negativeWordList.substring(1) : sentenceorParagrapghString_negativeWordList;
	  			if(sentenceorParagrapghString_negativeWordList == null || sentenceorParagrapghString_negativeWordList.isEmpty() || sentenceorParagrapghString_negativeWordList.length()==0) {}
	  			else {			p.setSentenceOrParagraph_containsNegativeWords(true);		}	
	  			
	  			
	  			
	  			

*/  			
	  			
	  			
	  			//march 2019, if the string contains accept reject terms, we give a slightly higher probability
	  			//march 2019, if the sentence contains any of the accept reject terms, we give a slightly higher probability to cater for tie breakers when several results have same probability
	  			
//$$$$$$$$$$$$$$$$
	  			//String labelInSentenceOrParagraphList = containsTermsFromListOfListsForLabel(sentenceorParagrapghString,labelList, matchWholeTerms,label);
	  			//if(labelInSentenceOrParagraphList.length() > 0)
	  			//{
	  				//p.setActermsfound(true); //SentenceOrParagraphHintProbablity=sentenceOrParagraphHintProbablity + 0.1;    	
	  			//}  	
	  	           	
	  	  		//SENTENCE OR PARAGRAPH PROBABILITY
	  	  		//if (location.equals("sameMessage") && containsReasonsTerms && containsReasonsIdentifierTerms && containsStateTerms) {probability = "very high"; } //same message as state
	  	  		//the following witll be covered in the subsequent if, but i have it for flexibility later
	  	  		//all those 'very high'
	  	  		if(sentenceOrParagraph_containsReasonsTerms && sentenceOrParagraph_containsStatesSubstates && sentenceOrParagraph_containsIdentifierTerms) {  //most common combination
	  	  			pattern_R_S_PI++; patternFound=true;
	  	  		}	//even same sentence 
	  	  		if(sentenceOrParagraph_containsReasonsTerms && sentenceOrParagraph_containsStatesSubstates && sentenceOrParagraph_containsEntitiesTerms) {  //after discussion i accept OR I decided add
	  	  			pattern_R_S_E++; patternFound=true; //sentenceOrParagraphHintProbablity=0.7;  	  			
	  	  		}	//even same sentence 
	  	  		//july 2018 added new one 'There have been some comments in favour of the proposal, no objections to the proposal as a whole, and some questions and objections about specific details. 
	  	  		//These are believed by the author to have been addressed by making changes to the PEP.'
	  	  		if(sentenceOrParagraph_containsReasonsTerms && sentenceOrParagraph_containsIdentifierTerms && sentenceOrParagraph_containsEntitiesTerms) {  // comments/questions/objections addressed author pep
	  	  			pattern_R_PI_E++; patternFound=true; //sentenceOrParagraphHintProbablity=0.7;  
	  	  		}
	  	  		if(sentenceOrParagraph_containsReasonsTerms && sentenceOrParagraph_containsStatesSubstates && sentenceOrParagraph_containsReasonsIdentifierTerms) {
	  	  			pattern_R_S_RI++; patternFound=true; //sentenceOrParagraphHintProbablity=0.7; 
	  	  		}		
	  	  		if(sentenceOrParagraph_containsDecisionTerms && sentenceOrParagraph_containsReasonsTerms && sentenceOrParagraph_containsEntitiesTerms) {
	  	  			pattern_DT_R_E++; patternFound=true; //sentenceOrParagraphHintProbablity=0.7;  
	  	  		}
	  	  		//all below combinations added aug 2018(or later)..for pep 311
	  	  		// Entity, state and proposal identifier as in pepe 365 = 'After reading all this I really don t believe that adding egg support to the stdlib at this time is the right thing to do I am therefore rejecting the PEP'
	  	  		if(sentenceOrParagraph_containsEntitiesTerms && sentenceOrParagraph_containsStatesSubstates && sentenceOrParagraph_containsIdentifierTerms) {
	  	  			pattern_S_PI_E++; patternFound=true; //sentenceOrParagraphHintProbablity=0.3;  
	  	  		}
	  	  		//if message author = proposal author, -- we have given more weightage in the message subject probabilities 
	  	  		//we just need 'proposal identifier' and 'reason' in sentence. and within dates - as its more likely
	  	  		if(sentenceOrParagraph_containsIdentifierTerms && sentenceOrParagraph_containsReasonsTerms) {
	  	  			pattern_PI_R++; patternFound=true; //sentenceOrParagraphHintProbablity=0.3;  //to many false postives so we give little weightage
	  	  		}
	  	  		//this combination was added before/maybe first time .. this helps for some pep, but for pep 311, it moves the actual sentence be lower in ranking
	  	  		if(sentenceOrParagraph_containsStatesSubstates && sentenceOrParagraph_containsReasonsTerms) {
	  	  			pattern_S_R++;	patternFound=true; //sentenceOrParagraphHintProbablity=0.7;  
	  	  		}
	  	  		//aug 2018 'If everyone who claims they understand the issues actually does, why is it so hard to reach a consensus?'
	  	  		if(sentenceOrParagraph_containsEntitiesTerms && sentenceOrParagraph_containsReasonsTerms) {
	  	  			pattern_E_R++; patternFound=true; //sentenceOrParagraphHintProbablity=0.3;  //to many false postives so we give little weightage
	  	  		}
	  	  		//aug 2018...pep 3139 manual reason
	  	  		if(sentenceOrParagraph_containsIdentifierTerms && sentenceOrParagraph_containsDecisionTerms ) {
	  	  			pattern_PI_DT++; patternFound=true; //sentenceOrParagraphHintProbablity=0.3;  //to many false postives so we give little weightage
	  	  		}
	  	  		if(sentenceOrParagraph_containsEntitiesTerms && sentenceOrParagraph_containsDecisionTerms ) {	//pep 507
	  	  			pattern_E_DT++; patternFound=true; //sentenceOrParagraphHintProbablity=0.3;  //to many false postives so we give little weightage
	  	  		}
	  	  		//added late oct 2018.. E + R we found many instances of this E + R combination in sentences ..Eg. PEP386 =  "As you said, consensus has been reached, so just Guido's BDFL stamp of approval is all I can think of."
	  	  		//duplicate
	  	  		//if(sentenceOrParagraph_containsEntitiesTerms && sentenceOrParagraph_containsReasonsTerms ) {	//pep 507
	  	  		//	pattern_E_R;  //sentenceOrParagraphHintProbablity=0.3;  //to many false postives so we give little weightage
	  	  		//}
	  	  		//added late feb 2019.. S + PI + R1 .. we found an instance of this in pep 265 =  "This PEP is rejected because the need for it has been largely  fulfilled by Py2.4's sorted() builtin function:."
	  	  		//the actual reason is hard to capture here
	  	  		if(sentenceOrParagraph_containsIdentifierTerms && sentenceOrParagraph_containsStatesSubstates && sentenceOrParagraph_containsReasonsIdentifierTerms) {	
	  	  			pattern_PI_S_RI++; patternFound=true; //sentenceOrParagraphHintProbablity=0.3;  //rare instances of this case and to prevent flase postives so we give little weightage
	  	  		}
	  	  		
	  	  		//Baseline 1
	  	  		if(sentenceOrParagraph_containsStatesSubstates && sentenceOrParagraph_containsDecisionTerms && sentenceOrParagraph_containsReasonsIdentifierTerms) {	
	  	  			pattern_S_DT_RI++; patternFound=true; //sentenceOrParagraphHintProbablity=0.3;  //rare instances of this case and to prevent flase postives so we give little weightage
	  	  		}
	  	  		//Baselien 2
		  	  	if((sentenceOrParagraph_containsStatesSubstates && sentenceOrParagraph_containsDecisionTerms && sentenceOrParagraph_containsReasonsIdentifierTerms)
		  	  			|| (sentenceOrParagraph_containsDecisionTerms && sentenceOrParagraph_containsEntitiesTerms) ) {	
	  	  			pattern_S_DT_RI_OR_DT_E++; patternFound=true; //sentenceOrParagraphHintProbablity=0.3;  //rare instances of this case and to prevent flase postives so we give little weightage
	  	  		}
	  	  		
	  	  		if(!patternFound)
	  	  			no_pattern_count++;
	  	  		
	  	  		//maybe sentencehintprobability wil, get updated above code block	  	  		
	  	  		//set all to false
	  	  		sentenceOrParagraph_containsReasonsTerms = sentenceOrParagraph_containsStatesSubstates = sentenceOrParagraph_containsIdentifierTerms = 
	  	  			sentenceOrParagraph_containsReasonsIdentifierTerms= sentenceOrParagraph_containsEntitiesTerms=sentenceOrParagraph_containsEntitiesTerms=
	  	  				sentenceOrParagraph_containsDecisionTerms=sentenceOrParagraph_containsNegationTerms=false;
	  	  		patternFound=false;
  	        
  	        } //end for each sentence   		
  	        
  	        System.out.println("\nReasons Terms Found Count: " + reasonsTermsFoundCount + " Percentage: " + Math.round(reasonsTermsFoundCount * 100.0/sentenceCount) + "%");  	       
  	        System.out.println("Substates Terms Found Count: " + substatesTermsFoundCount + " Percentage: " + Math.round(substatesTermsFoundCount * 100.0/sentenceCount) + "%");  
  	        System.out.println("Identifier Terms Found Count: " + identifierTermsFoundCount + " Percentage: " + Math.round(identifierTermsFoundCount * 100.0/sentenceCount) + "%");  
  	        
  	        System.out.println("\nReasonsIdentifier Terms Found Count: " + reasonsidentifierTermsFoundCount + " Percentage: " + Math.round(reasonsidentifierTermsFoundCount * 100.0/sentenceCount) + "%");  	       
	        System.out.println("Verbs Terms Found Count: " + verbsListFoundCount + " Percentage: " + Math.round(verbsListFoundCount * 100.0/sentenceCount) + "%");  
	        System.out.println("Entities Terms Found Count: " + entitiesTermsFoundCount + " Percentage: " + Math.round(entitiesTermsFoundCount * 100.0/sentenceCount) + "%");
	        
	        System.out.println("\nSpecial Terms Found Count: " + specialTermsFoundCount + " Percentage: " + Math.round(specialTermsFoundCount * 100.0/sentenceCount) + "%");  	       
  	        System.out.println("Decision Terms Found Count: " + decisionTermsFoundCount + " Percentage: " + Math.round(decisionTermsFoundCount * 100.0/sentenceCount) + "%");  
  	        System.out.println("Negation Terms Found Count: " + negationTermsFoundCount + " Percentage: " + Math.round(negationTermsFoundCount * 100.0/sentenceCount) + "%");
  	        
  	        
  	        System.out.println("\npattern_R_S_PI Count: " + pattern_R_S_PI + " Percentage: " + Math.round(pattern_R_S_PI * 100.0/sentenceCount) + "%");
  	        System.out.println("pattern_R_S_E Count: " + pattern_R_S_E + " Percentage: " + Math.round(pattern_R_S_E * 100.0/sentenceCount) + "%");
  	        System.out.println("pattern_R_PI_E Count: " + pattern_R_PI_E + " Percentage: " + Math.round(pattern_R_PI_E * 100.0/sentenceCount) + "%");
  	        System.out.println("pattern_R_S_RI Count: " + pattern_R_S_RI + " Percentage: " + Math.round(pattern_R_S_RI * 100.0/sentenceCount) + "%");
  	        
  	        System.out.println("pattern_DT_R_E Count: " + pattern_DT_R_E + " Percentage: " + Math.round(pattern_DT_R_E * 100.0/sentenceCount) + "%");
  	        System.out.println("pattern_S_PI_E Count: " + pattern_S_PI_E + " Percentage: " + Math.round(pattern_S_PI_E * 100.0/sentenceCount) + "%");
  	        System.out.println("pattern_PI_R Count: " + pattern_PI_R + " Percentage: " + Math.round(pattern_PI_R * 100.0/sentenceCount) + "%");
  	        System.out.println("pattern_S_R Count: " + pattern_S_R + " Percentage: " + Math.round(pattern_S_R * 100.0/sentenceCount) + "%");
  	        System.out.println("pattern_E_R Count: " + pattern_E_R + " Percentage: " + Math.round(pattern_E_R * 100.0/sentenceCount) + "%");
  	        System.out.println("pattern_PI_DT Count: " + pattern_PI_DT + " Percentage: " + Math.round(pattern_PI_DT * 100.0/sentenceCount) + "%");
  	        System.out.println("pattern_E_DT Count: " + pattern_E_DT + " Percentage: " + Math.round(pattern_E_DT * 100.0/sentenceCount) + "%");
  	        System.out.println("pattern_E_R Count: " + pattern_E_DT + " Percentage: " + Math.round(pattern_E_DT * 100.0/sentenceCount) + "%");
  	        System.out.println("pattern_PI_S_RI Count: " + pattern_PI_S_RI + " Percentage: " + Math.round(pattern_PI_S_RI * 100.0/sentenceCount) + "%");
  	        
  	        //pattern_S_DT_RI_OR_DT_E
  	        System.out.println("\nBaseline 1: pattern_S_DT_RI Count: " + pattern_S_DT_RI + " Percentage: " + Math.round(pattern_S_DT_RI * 100.0/sentenceCount) + "%");
	        System.out.println("Baseline 2: pattern_S_DT_RI_OR_DT_E Count: " + pattern_S_DT_RI_OR_DT_E + " Percentage: " + Math.round(pattern_S_DT_RI_OR_DT_E * 100.0/sentenceCount) + "%");
	        
  	        System.out.println("\nNo pattern Count: " + no_pattern_count + " Percentage: " + Math.round(no_pattern_count * 100.0/sentenceCount) + "%");
 	    	
  	        mc.disconnect();           
 	    	
 	    } catch (Exception ex) {
 	    	System.out.println("Error " + ex.getMessage() +ex.toString()); 	    	System.out.println(ps.StackTraceToString(ex)  );	
 	    }
    }

    private static void searchEachTermCombinationInSentencesAndParagraphsTable(Date date, SimpleDateFormat dateFormat, Connection conn, Statement stmt,String lineOfKeyWords,Boolean matchExactTerms) throws IOException, SQLException {
    	FileWriter writer,writer2,writer3;
    	String[] paragraphs;
    	String pepNumber,previousSentence = "",currentSentence = "", nextSentence = "",v_sentence = "",v_messageID = "",cou = "", v_author="",v_subject="",
    			v_idea="", subject="", verb="",object = "",output = "",permanentCurrentSentence = "",permanentPreviousSentence = "",permanentNextSentence = "";
    	Boolean foundinLastRound = false;
    	Integer forOutput_pepNumber = null,sentenceCounter = 0, dataInsertedCounter=0;
    	Timestamp dtTimeStamp=null; 

    	try {
    		TimeUnit.SECONDS.sleep(1);
    		System.gc();     Runtime.getRuntime().gc();         //OR call 
    	} catch (InterruptedException e2) {
    		// TODO Auto-generated catch block
    		e2.printStackTrace();
    	}

    	//int sentenceCounter = 0;
    	//extract keywords 	 
    	
    	if(lineOfKeyWords==null || lineOfKeyWords.isEmpty() || lineOfKeyWords.length()==0) {
    		lineOfKeyWords = " ";
    	}
    	
    	String words[] = lineOfKeyWords.split(" ");
    	//for terms anywhere in sentence we split by space
    	//change this to comma when searching for continuous terms
    	//String words[] = terms; 
    	String str = " "+field+" like '%consensus%'";

    	if(words.length==1 ){
    		v_idea = subject= words[0]; 
    		str = " LOWER("+field+") LIKE '%" + subject + "%'";// and LOWER(email) LIKE '%" + verb + "%'  ";
    	}
    	else if(words.length==2 ){
    		//v_idea =  words[0];	
    		v_idea= words[0] + " " + words[1];     		subject = words[0];
    		verb = words[1]; 
    		str = " LOWER("+field+") LIKE '%" + subject + "%' and LOWER("+field+") LIKE '%" + verb + "%'  ";
    	}
    	else if(words.length==3){
    		v_idea =  words[0]+ " "+ words[1] + " "+ words[2];     		subject = words[0]; verb = words[1]; object = words[2];	 
    		str = " LOWER("+field+") LIKE '%" + subject + "%' and LOWER("+field+ ") LIKE '%" + verb + "%'  and LOWER("+field+") LIKE '%" + object + "%'  ";
    	}
    	//capture object as well - form triples - inputfiles
    	else if(words.length>3){
    		v_idea =  words[0]; subject = words[1]; verb = words[2]; object = words[3];	 
    		str = " LOWER("+field+") LIKE '%" + subject + "%' and LOWER("+field+") LIKE '%" + verb + "%'  and LOWER("+field+") LIKE '%" + object + "%'  ";
    	}

    	writer  = new FileWriter("C:\\DeMap_Miner\\datafiles\\outputFiles\\NearbySentencesParagraph\\Previous-Selected-Next-SentencesParagraph_"+ v_idea+"-"+ subject+"-"+verb+"-"+object  + dateFormat.format(date) + ".txt");  //All the previous, selected and next sentences are output in tHis file.
    	writer2 = new FileWriter("C:\\DeMap_Miner\\datafiles\\outputFiles\\NearbySentencesParagraph\\SelectedSentencesOnly_"+ v_idea+"-"+ subject+"-"+verb+"-"+object  + dateFormat.format(date) +  ".txt");				
    	writer3 = new FileWriter("C:\\DeMap_Miner\\datafiles\\outputFiles\\NearbySentencesParagraph\\WithAllParagraphs_"+ v_idea+"-"+ subject+"-"+verb+"-"+object  + dateFormat.format(date) +  ".txt");

    	//pst = con.prepareStatement("select email from allpeps where pep = 488 AND email LIKE ='%" + keyWord + "%'");
    	// distinct

    	ArrayList<Integer> UniquePeps = new ArrayList<Integer>();;
    	if (allPeps) 
    		UniquePeps = pu.returnUniqueProposalsInDatabase(proposalIdentifier);
    	else if (selectedPEPs) {
    		for (int index = 0; index < selectedListItems.length; index++)	{
    			UniquePeps.add(selectedListItems[index]); 
    			//System.out.println("selectedListItems " + selectedListItems[index]);     			
    		}
    	}
    	System.out.println("Selected All---------------------------Unique Peps count = "+ UniquePeps.size());
    	System.out.println("str: " + str);
    	System.out.println("proposals: " + proposals);
    	//no need for this as we querying sentences and paragrapghs table directly
    	try {
    		
    		String qry = "select messageID, proposal,  sentence ,authorrole, msgsubject from "+tablename+" where " + proposals + " AND " + str +";";  //datetimestamp
    		ResultSet rs = stmt.executeQuery(qry);	// limit 100 
    		//LIKE '%" + keyWord.trim().toLowerCase() + "%' LIMIT 100");
    	
    		
	    	while (rs.next()) 		{  
	    		v_messageID = rs.getString(1);	pepNumber  = rs.getString(2);	v_sentence= rs.getString(3); 
	    		v_author = rs.getString(4); 	v_subject = rs.getString(5); 	dtTimeStamp = null; //dtTimeStamp =rs.getTimestamp(6);
	
	    		//model.addRow(new Object[]{ v_messageID});
	    		if ((v_sentence == "null") || (v_sentence == "") || (v_sentence.isEmpty())) 
	    		{}
	    		else {
	    			Integer paragraphCounter = 0;    				Boolean foundInLastParagraph =false;    				String previousParagraph = "";
	    			//if we want to check in nearby paragraphs , previous, curr, and next
	    			if(termsAnywhereInParagraph){
	    				if (ps.containsSVO(subject, verb,object, v_sentence, matchExactTerms)) {
	    					insertNearbySentencesInDB( Integer.valueOf(pepNumber), Integer.valueOf(v_messageID), v_idea,  subject,  verb,  object,
	    							v_sentence, //need current sentence: current sentence in earlier round
	    							"", 		  //need previous sentence: next sentence in earlier round but current sentence
	    							"", 		  //need next sentence: currentSentence in this round  
	    							v_author, v_subject,dtTimeStamp,
	    							conn);	
	    				}
	    			}					
	    			//once we have the next sentnce, we can write to file 
	    			try	{    								
	    				if(resultsToFile){										//next sentence			//paragraph
	    					writer.append (output + " |ns " + currentSentence  + " |cp " + v_sentence); // + " |pp " + previousParagraph.replace("\n", ""));
	    					writer3.append(output + " |ns " + currentSentence  + " |cp " + v_sentence + " |pp " + previousParagraph);
	    				}
	    				if(resultsToDatabse){//insert into database
	    					insertNearbySentencesInDB( Integer.valueOf(pepNumber), Integer.valueOf(v_messageID),v_idea,  subject,  verb,  object,
	    							permanentCurrentSentence, //need current sentence: current sentence in earlier round
	    							permanentPreviousSentence, 		  //need previous sentence: next sentence in earlier round but current sentence
	    							v_sentence, 		  //need next sentence: currentSentence in this round  
	    							v_author, v_subject,dtTimeStamp,
	    							conn);												
	    				}
	    				if (resultsToConsole)
	    					System.out.println(pepNumber  + " | " + v_idea + " | " + v_messageID + " | "+ subject  + "|" + previousSentence + " |cs " + v_sentence);
	    				dataInsertedCounter++;
	    			} catch (IOException e) {
	    				// TODO Auto-generated catch block
	    				e.printStackTrace();
	    			}		
	
	    			//CHECKING IF THE Sentence contains the search terms
	    			if (ps.containsSVO(subject, verb,object, currentSentence, matchExactTerms)) {
	    				sentenceCounter++;
	    				output = "\n" + v_idea + " | " + pepNumber + " | " + v_messageID + "|pp "   + " |ps " + previousSentence + " |cs " + v_sentence;
	    				//String forOutput_nextSentence = "";
	    				forOutput_pepNumber = Integer.parseInt(pepNumber);												
	    				//set that ste has been found
	    				foundInLastParagraph=true;												
	    				writer2.append("\n" + v_sentence);
	    				foundinLastRound = true;
	    			}		
	    			// end for
	    		} //end else
	    		// System.out.println("paragraphs in message" + paragraphCounter);
	    		paragraphs  = null; 
	    	} // end while

    	}
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (Exception e){
  			System.out.println("Exception 282 (DATABASE) " + e.toString());   			System.out.println(StackTraceToString(e)  );
  		}
    	
    	//call garbage collector      
    	System.gc();  Runtime.getRuntime().gc();            //OR call 
    	System.out.println(sentenceCounter + " Records Found, Data Inserted " +dataInsertedCounter );  
    	writer.close();    	writer2.close();    	writer3.close();
    	words=null;
    }
    
    public static String StackTraceToString(Exception ex) {
		String result = ex.toString() + "\n";
		StackTraceElement[] trace = ex.getStackTrace();
		for (int i=0;i<trace.length;i++) {
			result += trace[i].toString() + "\n";
		}
		return result;
	}
    
    public static void initialiseTermLists(WordnetSynonyms wn) {
//		try {
//			inputStream = new FileInputStream("C://lib//openNLP//en-sent.bin"); 
//		    model = new SentenceModel(inputStream); 	    									
//			detector = new SentenceDetectorME(model);  //Instantiating the SentenceDetectorME class
//		}
//		catch(Exception e){
//			
//		}
		
		wf.initialise();
		ws.init();
		
		//prp = new ProcessingRequiredParameters();		
		ps  = new ParagraphSentence();		
		pm  = new PythonSpecificMessageProcessing();		
		//pms = new ProcessMessageAndSentence();		
		StateAndReasonLabels l = new StateAndReasonLabels();
		boolean output=true;
		l.initStateAndReasonLabels(wn, output);
		verbs = l.getVerbs(); 	//ready for review/pronouncement
		committedStates = l.getCommittedStates();	//main state changes  e.g. 'Status: Accepted'
		if(outputList) {
			System.out.print("FINAL committedStates >> ");  for (String line : committedStates)     {                	System.out.print(line+ " ");            }		  System.out.println(" ");  
		}
		allStatesList_StatesSubstates = l.getAllStatesList_StatesSubstates();	
		if(outputList) {
			System.out.print("FINAL getAllStatesList_StatesSubstates >> ");
		}
		Integer countA=0;
		if(outputList) { 
			for (ArrayList<String> line : allStatesList_StatesSubstates) {   //for each list as line 
	    		for (String term : line) {	//for each term in line
	    				System.out.print(term+ ",");	
	            	countA++; if (countA%10==0) {	System.out.println();	System.out.print("\t"); }
	    		}	//System.out.println(); 
	    	}		//  System.out.println(" ");  
		}
		reasonsList = l.getReasonTerms(); 
		if(outputList) {
			System.out.print("\nFINAL reasonsList >> ");  
		}
		Integer count=0;
		if(outputList) {
			for (ArrayList<String> line : reasonsList) { 
				for (String f : line) { 
						System.out.print(f+ ",");
					count++; if (count%10==0) {	System.out.println();	System.out.print("\t");	}
				}		 // System.out.println(); 			
			}
		}
		    // Looking at the outputs from below, we can use Word forms, NOUN Synonyms, VERB synonyms, and DerivationallyForm, BUT NOT Verb Hyponyms
		    // For states we only use wordforms and deriavationallyform
			// now we add the different wordforms and wordnet word forms
			/* for (String r : reasonsList) {
				// all word forms
				Set reasonListWordForms = wf.getAllWordForms(r);	
				// NOUN Synonyms
				Set<String> synonyms = ws.getSynonym(r, SynsetType.NOUN); //region 
				System.out.println("\tNOUN Synonyms for word: " + r); System.out.print("\t");	for (String s : synonyms) {  System.out.print(s + ", ");	} System.out.println();				
				// VERB synonyms
				Set<String> synonymsVERB = ws.getSynonym(r, SynsetType.VERB); //region 
				System.out.println("\tVERB Synonyms for word: " + r); System.out.print("\t");	for (String s : synonymsVERB) {  System.out.print(s + ", ");	} System.out.println();
				// DerivationallyForm 
				Set<String> derivationallyForm = ws.getDerivationallyRelatedForms(r, SynsetType.VERB); //develop
				System.out.println("\tDerivationallyForm for word: " + r);	System.out.print("\t");	for (String s : derivationallyForm) {  System.out.print(s + ", ");	}	System.out.println();				
			} */
			
			// Set<String> finalReasonSet = new HashSet<String>();
		    // Looking at the outputs from below, we can use Word forms, NOUN Synonyms, VERB synonyms, and DerivationallyForm, BUT NOT Verb Hyponyms
		    // For states we only use wordforms and deriavationallyform
			// now we add the different wordforms and wordnet word forms
			/* for (String r : reasonsList) {
				// all word forms
				System.out.println(r);
				Set reasonListWordForms = wf.getAllWordForms(r);
				finalReasonSet.add(r);		//add current term
				finalReasonSet.addAll(reasonListWordForms);		//add word forms of that term
				// NOUN Synonyms 
				Set<String> synonyms = ws.getSynonym(r, SynsetType.NOUN); //region 
				System.out.print("\tNOUN Synonyms for word: " + r + " = "); for (String s : synonyms) {  System.out.print(s + ", ");	} System.out.println();		 finalReasonSet.addAll(synonyms);			
				// VERB synonyms
				Set<String> synonymsVERB = ws.getSynonym(r, SynsetType.VERB); //region 
				System.out.print("\tVERB Synonyms for word: " + r + " = "); for (String s : synonymsVERB) {  System.out.print(s + ", ");	} System.out.println();  finalReasonSet.addAll(synonymsVERB);	
				// DerivationallyForm 
				Set<String> derivationallyForm = ws.getDerivationallyRelatedForms(r, SynsetType.VERB); //develop
				System.out.print("\tDerivationallyForm for word: " + r + " = ");		for (String s : derivationallyForm) {  System.out.print(s + ", ");	}	System.out.println(); finalReasonSet.addAll(derivationallyForm);					
			}
			
			//convert to list
		    List<String> allReasonListWordForms_List = new ArrayList<String>();
		    allReasonListWordForms_List.addAll(finalReasonSet);
		    reasonsList = allReasonListWordForms_List;  //assign it to the main variable
		    System.out.print("FINAL WORDFORMS reasonsList >> ");  for (String line : reasonsList)     {                	System.out.print(line+ ", ");            }		  System.out.println(" ");
			*/
		reasonIdentifierTerms = l.getReasonIdentifierTerms(); 
			//now we add the different wordforms and wordnet word forms		
		if(outputList) {
			System.out.print("FINAL reasonIdentifierTerms >> ");  for (String line : reasonIdentifierTerms)     {                	System.out.print(line+ ", ");            }		  System.out.println(" ");  
		}
		proposalIdentifiers = l.getProposalIdentifierTerms();
		if(outputList) {
			System.out.print("FINAL proposalIdentifiers >> ");  for (String line : proposalIdentifiers)     {                	System.out.print(line+ ", ");            }		  System.out.println(" ");
		}
		specialOSCommunitySpecificTerms = l.getSpecialOSCommunitySpecificTerms(); 
		entitiesList = l.getEntities();
		if(outputList) {
			System.out.print("FINAL specialOSCommunitySpecificTerms >> ");  
		}
		Integer countC=0;
		if(outputList) {
			for (ArrayList<String> line : specialOSCommunitySpecificTerms) {    
				for (String f : line) { 
						System.out.print(f+ ",");
					countC++; if (countC%10==0) {	System.out.println(); 	System.out.print("\t");	}
				}			  //System.out.println();
			}
		}
		subStates = l.getSubStates();
		if(outputList) {
			System.out.print("FINAL entitiesList >> ");  for (String line : entitiesList)     {                	System.out.print(line+ ", ");            }		  System.out.println(", ");
		}
		if(outputList) {
			System.out.print("FINAL subStates >> ");  for (String line : subStates)     {                	System.out.print(line+ ", ");            }		  System.out.println(", ");
		}
		negationTerms = l.getNegationTerms();
		if(outputList) {
			System.out.print("FINAL negationTerms >> ");  for (String line : negationTerms)     {                	System.out.print(line+ ", ");            }		  System.out.println(", ");
		}
		conditionalTerms= l.getConditionalTerms();
		if(outputList) {
			System.out.print("FINAL conditionalTerms >> ");  for (String line : conditionalTerms)     {                	System.out.print(line+ ", ");            }		  System.out.println(", ");
		}
		decisionTerms= l.getDecisionTerms();
		if(outputList) {
			System.out.print("FINAL deisionTerms >> "); 
		}
		Integer countD=0;
		if(outputList) {
			for (ArrayList<String> line : decisionTerms) {    
				for (String f : line) {
						System.out.print(f+ ",");
					countD++; if (countD%10==0) {	System.out.println(); 	System.out.print("\t");	}
				}			  //System.out.println();
			}
		}
				// For states we only use wordforms and deriavationallyform
				// now we add the different wordforms and wordnet word forms
				/*
				Set<String> finalDecisionSet = new HashSet<String>();
				for (String r : decisionTerms) {
					// all word forms
					System.out.println(r);
					Set decisionListWordForms = wf.getAllWordForms(r);
					finalDecisionSet.add(r);		//add current term
					finalDecisionSet.addAll(decisionListWordForms);
					// NOUN Synonyms
					Set<String> synonyms = ws.getSynonym(r, SynsetType.NOUN); //region 
					System.out.print("\tNOUN Synonyms for word: " + r + " = "); 	for (String s : synonyms) {  System.out.print(s + ", ");	} System.out.println();		finalDecisionSet.addAll(synonyms);			
					// VERB synonyms
					Set<String> synonymsVERB = ws.getSynonym(r, SynsetType.VERB); //region 
					System.out.print("\tVERB Synonyms for word: " + r + " = "); 	for (String s : synonymsVERB) {  System.out.print(s + ", ");	} System.out.println(); finalDecisionSet.addAll(synonymsVERB);
					// DerivationallyForm 
					Set<String> derivationallyForm = ws.getDerivationallyRelatedForms(r, SynsetType.VERB); //develop
					System.out.print("\tDerivationallyForm for word: " + r + " = ");	for (String s : derivationallyForm) {  System.out.print(s + ", ");	}	System.out.println();	 finalDecisionSet.addAll(derivationallyForm);			
				}
				//convert to list
			    List<String> allDecisionWordForms_List = new ArrayList<String>();
			    allDecisionWordForms_List.addAll(finalDecisionSet);
			    decisionTerms = allDecisionWordForms_List;		//assign it to the main variable
				//statesList = ObjectArrays.concat(statesList, decisionMechanismsSubStates, String.class); //we will combine statesList and decisionMechanismsSubStates
				//statesList.addAll(decisionMechanismsSubStates);
				//specialTerms = ObjectArrays.concat(specialTerms, entities, String.class);	//special terms and entities are combined
				//specialTerms.addAll(entitiesList);
			    System.out.print("FINAL WORDFORMS deisionTerms >> ");  for (String line : decisionTerms)     {                	System.out.print(line+ ", ");            }		  System.out.println(", ");
				*/
		//statesList = ObjectArrays.concat(statesList, decisionMechanismsSubStates, String.class); //we will combine statesList and decisionMechanismsSubStates
		//statesList.addAll(decisionMechanismsSubStates);
		//specialTerms = ObjectArrays.concat(specialTerms, entities, String.class);	//special terms and entities are combined
		//specialTerms.addAll(entitiesList);
		positiveWords = l.getPositiveWords();
		negativeWords = l.getNegativeWords();
		//dec 2018 terms that should not cooccur as they have double meaning
		notCoOccurTerms = l.getNotCoOccurTerms();
		
		 //we limit the labels we are searching for to the label we just found. i.e looking for reasons for 'accepted' label, we dont want to match 'deferred'				 
		 //go through the list of lists for states and substates and remove all otehrs, other than accepted
		 
//		 templistOLists = ppm.getAllStatesList_StatesSubstates();				 				 
//		 templistOLists = returnLabelList(label, templistOLists);
		//set this label list
//		 ppm.setLabelList(templistOLists); //if label is not found in list, then it will search in entire lists of lists - but it should find..make sure it finds
		
	}
	
	//this function instead of the above, includes the synonyms and wordforms
	public static void initialiseTermListsAndWordForms(WordnetSynonyms wn) {
		wf.initialise();	ws.init();	
		//StanfordLemmatizer sl = new StanfordLemmatizer();  //we need lemmatizer		
		StateAndReasonLabels srl = new StateAndReasonLabels();
		srl.initStateAndReasonLabels(ws, true);
		
		verbs = srl.getVerbs(); 	//ready for review/pronouncement
		committedStates = srl.getCommittedStates();	//main state changes  e.g. 'Status: Accepted'
		System.out.print("\nFINAL committedStates >> ");  for (String line : committedStates)     {                	System.out.print(line+ ", ");            }		  System.out.println(" ");  
		//States and substates
		allStatesList_StatesSubstates = srl.getAllStatesList_StatesSubstates();	
			System.out.print("\nFINAL getAllStatesList_StatesSubstates >> ");
			for (ArrayList<String> line : allStatesList_StatesSubstates) {   //for each list as line 
	    		for (String term : line) {	//for each term in line
	            	System.out.print(term+ ", "); // ("+ sl.lemmatize(term) +") ");        		
	    		}	System.out.println(); 
	    	}	//	  System.out.println(" "); 
	/*		Set<String> finalStateSet = new HashSet<String>();
			 // now we add the different wordforms and wordnet word forms // For states we only use wordforms and deriavationallyform
			 // since the terms make a lot of different in capturing exact events, we dont look for noun and verb synonyms...they are not synonyms for this set anyway
			 // DerivationallyForm is useful but we compute it on the stemmed word form 
			for (ArrayList<String> ss : allStatesList_StatesSubstates) { 
				String t = ss.get(0);	//get first element from arraylist
				String r = sl.lemmatize(t).get(0);   //get lemmatised form, mostly first element from lemmatised list should be only and main one 
				// all word forms
				System.out.println(r);
				Set stateListWordForms = wf.getAllWordForms(r);	
				finalStateSet.add(t);		//add current term
				finalStateSet.addAll(stateListWordForms);		//add word forms of that term
				// NOUN Synonyms
//				Set<String> synonyms = ws.getSynonym(r, SynsetType.NOUN); finalStateSet.addAll(synonyms);   //region 
//				System.out.print("\tNOUN Synonyms for word: " + r + " = "); 	for (String s : synonyms) {  System.out.print(s + ", ");	} System.out.println();				
				// VERB synonyms
//				Set<String> synonymsVERB = ws.getSynonym(r, SynsetType.VERB); finalStateSet.addAll(synonymsVERB);  //region 
//				System.out.print("\tVERB Synonyms for word: " + r + " = ");  for (String s : synonymsVERB) {  System.out.print(s + ", ");	} System.out.println();
				// DerivationallyForm 
				Set<String> derivationallyForm = ws.getDerivationallyRelatedForms(r, SynsetType.VERB); 	finalStateSet.addAll(derivationallyForm); //develop
				System.out.print("\n\tDerivationallyForm for word: " + r + " = ");	for (String s : derivationallyForm) {  System.out.print(s + ", ");	}	System.out.println();
				//create a new list of lists and add to it
				ArrayList<String> temp = new ArrayList<String>();
				temp.addAll(finalStateSet); //add set to list
				temp_allStatesList_StatesSubstates.add(temp);	//add list to list of lists
			} 
			System.out.print("\nallStatesList_StatesSubstates Set >> "); 
		    for (String f : finalStateSet)	{
		    	System.out.print( f + ", " );
		    } 
		    allStatesList_StatesSubstates = temp_allStatesList_StatesSubstates;  //replace the list with this one with word forms and wordnet synonyms
		    System.out.print("\nREPLACED FINAL getAllStatesList_StatesSubstates >> ");
			for (ArrayList<String> line : allStatesList_StatesSubstates) {   //for each list as line 
	    		for (String term : line) {	//for each term in line
	            	System.out.print("'"+term+ "', "); // ("+ sl.lemmatize(term) +") ");        		
	    		}	System.out.println(); 
	    	}
	*/
		    //convert to list
		    /* ArrayList<String> allStatesList_StatesSubstates_List = new ArrayList<String>();
		    allStatesList_StatesSubstates_List.addAll(finalStateSet);
		    allStatesList_StatesSubstates = null; 
		    try {
			    for(String f: allStatesList_StatesSubstates_List) {  //assign it to main variable
			    	 ArrayList<String> t = new ArrayList<String>();
			    	 t.add(f);
			    	allStatesList_StatesSubstates.add(t);
			    }
		    }
		    catch (Exception e){
    			System.out.println("Exception 34 (FILE) " + e.toString()); 
    			System.out.println(StackTraceToString(e)  );
	    	}  
		    */
		    System.out.println("");
		//reason list
		reasonsList = srl.getReasonTerms();
		System.out.println("\n");
		System.out.print("\nFINAL reasonsList >> ");  for (ArrayList<String> line : reasonsList)     {                	System.out.print(line+ ", ");            }		  System.out.println(" ");
		/*
		Set<String> finalReasonSet = new HashSet<String>();
		    // Looking at the outputs from below, we can use Word forms, NOUN Synonyms, VERB synonyms, and DerivationallyForm, BUT NOT Verb Hyponyms
		    // For states we only use wordforms and deriavationallyform
			// now we add the different wordforms and wordnet word forms
			for (ArrayList<String> t : reasonsList) {
				for (String r : t) {
				// all word forms
				System.out.println(r);
				Set reasonListWordForms = wf.getAllWordForms(r);
				finalReasonSet.add(r);		//add current term
				finalReasonSet.addAll(reasonListWordForms);		//add word forms of that term
				// NOUN Synonyms 
				Set<String> synonyms = ws.getSynonym(r, SynsetType.NOUN); //region 
				System.out.print("\tNOUN Synonyms for word: " + r + " = "); for (String s : synonyms) {  System.out.print(s + ", ");	} System.out.println();		 finalReasonSet.addAll(synonyms);			
				// VERB synonyms
				Set<String> synonymsVERB = ws.getSynonym(r, SynsetType.VERB); //region 
				System.out.print("\tVERB Synonyms for word: " + r + " = "); for (String s : synonymsVERB) {  System.out.print(s + ", ");	} System.out.println();  finalReasonSet.addAll(synonymsVERB);	
				// DerivationallyForm 
				Set<String> derivationallyForm = ws.getDerivationallyRelatedForms(r, SynsetType.VERB); //develop
				System.out.print("\tDerivationallyForm for word: " + r + " = ");		for (String s : derivationallyForm) {  System.out.print(s + ", ");	}	System.out.println(); finalReasonSet.addAll(derivationallyForm);					
				}
			}	
			//convert to list
		    ArrayList<String> allReasonListWordForms_List = new ArrayList<String>();
		    allReasonListWordForms_List.addAll(finalReasonSet);
		    reasonsList = allReasonListWordForms_List;  //assign it to the main variable
		    */
		System.out.print("FINAL WORDFORMS reasonsList >> ");  for (ArrayList<String> line : reasonsList)     {                	System.out.print(line+ ", ");            }		  System.out.println(" ");
		//System.out.print("FINAL reasonsList >> ");  for (String line : reasonsList)     {                	System.out.print(line+ ", ");            }		  System.out.println(" "); 
		reasonIdentifierTerms =srl.getReasonIdentifierTerms(); 
		//now we cant add the different wordforms and wordnet word forms as reason identifiers wont have these..i think?
		System.out.print("FINAL reasonIdentifierTerms >> ");  for (String line : reasonIdentifierTerms)     {                	System.out.print(line+ ", ");            }		  System.out.println(" ");  
		proposalIdentifiers =srl.getProposalIdentifierTerms();
		System.out.print("FINAL proposalIdentifiers >> ");  for (String line : proposalIdentifiers)     {                	System.out.print(line+ ", ");            }		  System.out.println(" ");
		specialOSCommunitySpecificTerms = srl.getSpecialOSCommunitySpecificTerms(); 
		entitiesList = srl.getEntities();
		System.out.print("FINAL specialOSCommunitySpecificTerms >> ");  for (ArrayList<String> line : specialOSCommunitySpecificTerms)     {                	System.out.print(line+ ", ");            }		  System.out.println(" ");
		subStates = srl.getSubStates();
		System.out.print("FINAL entitiesList >> ");  for (String line : entitiesList)     {                	System.out.print(line+ ", ");            }		  System.out.println(", ");
		System.out.print("FINAL subStates >> ");  for (String line : subStates)     {                	System.out.print(line+ ", ");            }		  System.out.println(", ");
		/* for (String r : subStates) {
			// all word forms
			System.out.println(r);
			//Set reasonListWordForms = wf.getAllWordForms(r);	
			// NOUN Synonyms
			Set<String> synonyms = ws.getSynonym(r, SynsetType.NOUN); //region 
			System.out.print("\tNOUN Synonyms for word: " + r + " = "); 	for (String s : synonyms) {  System.out.print(s + ", ");	} System.out.println();				
			// VERB synonyms
			Set<String> synonymsVERB = ws.getSynonym(r, SynsetType.VERB); //region 
			System.out.print("\tVERB Synonyms for word: " + r + " = "); 	for (String s : synonymsVERB) {  System.out.print(s + ", ");	} System.out.println();
			// DerivationallyForm 
			Set<String> derivationallyForm = ws.getDerivationallyRelatedForms(r, SynsetType.VERB); //develop
			System.out.print("\tDerivationallyForm for word: " + r + " = ");		for (String s : derivationallyForm) {  System.out.print(s + ", ");	}	System.out.println();				
		}
		*/
		negationTerms = srl.getNegationTerms();
		System.out.print("FINAL negationTerms >> ");  for (String line : negationTerms)     {                	System.out.print(line+ ", ");            }		  System.out.println(", ");
		conditionalTerms= srl.getConditionalTerms();
		System.out.print("FINAL conditionalTerms >> ");  for (String line : conditionalTerms)     {                	System.out.print(line+ ", ");            }		  System.out.println(", ");
		//decision terms
		decisionTerms= srl.getDecisionTerms();
		System.out.print("FINAL deisionTerms >> ");  for (ArrayList<String> line : decisionTerms)     {                	System.out.print(line+ ", ");            }		  System.out.println(", ");
		// For states we only use wordforms and deriavationallyform
		// now we add the different wordforms and wordnet word forms
		/*Set<String> finalDecisionSet = new HashSet<String>();
		 for (String r : decisionTerms) {
			// all word forms
			System.out.println(r);
			Set decisionListWordForms = wf.getAllWordForms(r);
			finalDecisionSet.add(r);		//add current term
			finalDecisionSet.addAll(decisionListWordForms);
			// NOUN Synonyms
			Set<String> synonyms = ws.getSynonym(r, SynsetType.NOUN); //region 
			System.out.print("\tNOUN Synonyms for word: " + r + " = "); 	for (String s : synonyms) {  System.out.print(s + ", ");	} System.out.println();		finalDecisionSet.addAll(synonyms);			
			// VERB synonyms
			Set<String> synonymsVERB = ws.getSynonym(r, SynsetType.VERB); //region 
			System.out.print("\tVERB Synonyms for word: " + r + " = "); 	for (String s : synonymsVERB) {  System.out.print(s + ", ");	} System.out.println(); finalDecisionSet.addAll(synonymsVERB);
			// DerivationallyForm 
			Set<String> derivationallyForm = ws.getDerivationallyRelatedForms(r, SynsetType.VERB); //develop
			System.out.print("\tDerivationallyForm for word: " + r + " = ");	for (String s : derivationallyForm) {  System.out.print(s + ", ");	}	System.out.println();	 finalDecisionSet.addAll(derivationallyForm);			
		}
		
		//convert to list
	    List<String> allDecisionWordForms_List = new ArrayList<String>();
	    allDecisionWordForms_List.addAll(finalDecisionSet);
	    decisionTerms = allDecisionWordForms_List;		//assign it to the main variable
		*/
		//statesList = ObjectArrays.concat(statesList, decisionMechanismsSubStates, String.class); //we will combine statesList and decisionMechanismsSubStates
		//statesList.addAll(decisionMechanismsSubStates);
		//specialTerms = ObjectArrays.concat(specialTerms, entities, String.class);	//special terms and entities are combined
		//specialTerms.addAll(entitiesList);
	    System.out.print("FINAL WORDFORMS deisionTerms >> ");  for (ArrayList<String> line : decisionTerms)     {                	System.out.print(line+ ", ");            }		  System.out.println(", ");
		positiveWords = srl.getPositiveWords();
		negativeWords = srl.getNegativeWords();
		//dec 2018 terms that should not cooccur as they have double meaning
		notCoOccurTerms = srl.getNotCoOccurTerms();
		System.out.print("FINAL notCoOccurTerms >> ");  for (String line : notCoOccurTerms)     {                	System.out.print(line+ ", ");            }		  System.out.println(", ");
	}

    public static void insertNearbySentencesInDB(Integer pep, Integer messageID, String idea, String subject, String verb, String object,
    		String currentSentence, String previousSentence, String nextSentence,String v_author,String message_subject, Timestamp dTimeStamp, Connection conn)
	{	 
//		//Now output to file and Console
		//boolean repeatedSentenecAndLabel = false,repeatedLabel = false, repeatedSentence = false, emptyRow = false;	
		//Main insertion
		String sql = "INSERT INTO "+tableToStore+" (pep, messageID,  label,subject, relation, object, currentSentence,ps,ns , author, message_subject,datetimestamp) "
				+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
		 
		try {			
			PreparedStatement statement = conn.prepareStatement(sql);			
			statement.setInt	 (1,pep);			statement.setInt	 (2, messageID);			//statement.setString	 (3, v_message);	
			statement.setString	 (3, idea);			statement.setString	 (4, subject);			statement.setString	 (5, verb);
			statement.setString	 (6, object);		statement.setString  (7, currentSentence);	statement.setString  (8, previousSentence);
			statement.setString  (9, nextSentence);			statement.setString  (10, v_author);
			statement.setString  (11, message_subject);
			statement.setTimestamp  (12, dTimeStamp);
			
			int rowsInserted = statement.executeUpdate();
			if (rowsInserted > 0) {
			    //System.out.println("A new result record was inserted in DB successfully!");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (Exception ex) {
			// TODO Auto-generated catch block
			System.out.println("Exception" + ex.toString());
			ex.printStackTrace();
		}
	}

	

	private static String[] readKeywordsFromFile(String fileName) throws FileNotFoundException, IOException {
		String[] keyWords;
		BufferedReader in = new BufferedReader(new FileReader(fileName));
		String str;
		List<String> list = new ArrayList<String>();
		while((str = in.readLine()) != null){
			
			if (str.startsWith("%"))
			{}
			else if(str.startsWith("##") ){	//|| str.trim().isEmpty()
				System.out.println("breaking out of loop");
				break;	//break out of loop
			}
		    else{
		    	list.add(str);
		    	System.out.println("added to list: "+str);
		    }
		}
		keyWords = list.toArray(new String[0]);
		System.out.println("returning array");
		return keyWords;
	}
 
	public void exportData(Connection conn, String filename) {
		Statement stmt;
		String query, query2,filename2 = "C:/Users/psharma/Google Drive/PhDOtago/scripts/outqrytest.txt";
		try {
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
					ResultSet.CONCUR_UPDATABLE);

			// For comma separated file
			query = "SELECT id,text,price into OUTFILE  '" + filename
					+ "' FIELDS TERMINATED BY ',' FROM testtable t";
			// firstSearchword.getText()
			String key = "wrangling";
			query2 = "select messageID, pep into OUTFILE  '"
					+ filename2
					+ "' FIELDS TERMINATED BY ',' from allmessages where  email LIKE '%"
					+ key + "%'";

			stmt.executeQuery(query2);

		} catch (Exception e) {
			e.printStackTrace();
			stmt = null;
		}
	}
	
	public static Integer countTermsInString(String list) {
		if(list==null || list.isEmpty() || list.equals("") || list.length()==0) {	return 0;		}
		else {
			if (list.startsWith(","))
				list= list.replaceFirst(",", "");
			return list.split(",").length;
		}
	}
	public static String containsAllTerms(String sentenceorParagrapghString, List<String> list, boolean matchWholeTerms) {
		String combination = "",finalCombination="";
		ArrayList<String> listofTermsMatched = new ArrayList<String>();
		for (String r : list ) {
			r = r.trim();
			//check state,identifier, and reasons only
//			if(r.split(" ").length ==1) { continue;}	//dont capture single words reasons here as too many false positives ..but they are in the list because they will be used in combination
			if (ps.containsAllTermsAsTheyAre(r,sentenceorParagrapghString, matchWholeTerms)) {	//subject+" "+verb+" "+object
//				combination = r;						
//				if(location.equals("sentence") && listofTermsMatched.contains(combination))	//paragraph
//				{}
//				else {	//sentence
					finalCombination= finalCombination + "," + r; 
					//insertReasonTermsFoundRow(proposal,mid, dateVal, sentenceString, location, combination);
//					reasonRow r1 = new reasonRow(); 	r1.setData(proposal, mid, dateVal, sentenceorParagrapghString, location, combination); 		rr.add(r1);	r1=null;
//					listofTermsMatched.add(r);
//					System.out.println("\t mid "+mid+" new terms [" + r + "] found in "+location+". Paragraph "+paragraphCounter); 
					//System.out.format("%5s | %30s| %7s | %15s | %30s | %25s | %15s | %15s |%15s |", "pepNo","State/Substate","MID","Date","Reason Identifier Terms","Actual Reasons","Location","Reason Label", probability);System.out.println("");
//				}					
			}
		}		
		return finalCombination.trim();
	}
	
	public static String containsTermsFromListOfLists(String sentenceorParagrapghString, ArrayList<ArrayList<String>> listOfLists,boolean matchWholeTerms) {
		String finalCombination="";
		//ArrayList<String> listofTermsMatched = new ArrayList<String>();
		for (ArrayList<String> line : listOfLists) {    //for each list            	
//$$    		System.out.println("Checking Line: "+line+ " : ");
    		for (String term : line) {      	//for each term in list 
    			term = term.trim();
    			//System.out.println("' checking "+term+ "' ");
    			//some terms in list of list are a combination of double term ..so we split here
    			if(term.contains("-"))
    				term = term.replace("-", " ");
    			if (ps.containsAllTermsAsTheyAre(term,sentenceorParagrapghString, matchWholeTerms)) {	//subject+" "+verb+" "+object
					finalCombination= finalCombination + "," + term; 								
    			}
    		}	//System.out.println("");
    	}
		return finalCombination.trim();
	}
    
}

