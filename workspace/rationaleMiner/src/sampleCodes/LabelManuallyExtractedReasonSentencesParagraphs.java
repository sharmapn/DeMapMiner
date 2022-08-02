package sampleCodes;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import connections.MysqlConnect;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.process.DocumentPreprocessor;
import wordnet.jaws.WordnetSynonyms;
import GUI.helpers.GUIHelper;
import miner.process.ProcessMessageAndSentence;
import miner.process.ProcessSinglesAndDoubles;
import miner.process.ProcessingRequiredParameters;
import miner.process.PythonSpecificMessageProcessing;
import miner.process.SentenceAndNearby;
import connections.PropertiesFile;
import utilities.ParagraphSentence;
import utilities.StateAndReasonLabels;

//label each sentence or paragraph with a pattern for elimination, then written to file...script hardly used
//aug 2018 updated...we label for learning
public class LabelManuallyExtractedReasonSentencesParagraphs {
	
	static ProcessingRequiredParameters prp;
	static ParagraphSentence ps;
	static PythonSpecificMessageProcessing pm;
	static ProcessMessageAndSentence pms;
	static GUIHelper guih = new GUIHelper();
	
	static String tableNameToStore = "autoExtractedReasonCandidateSentences"; 
	static Connection connection;
	//too many parameters of lists beings passed between too many functions so better to declare it here as class level variable 
	static List<String>  verbs  			= new ArrayList<String>();
	static List<String>  committedStates  	= new ArrayList<String>();
	static List<String>  mainStates  		= new ArrayList<String>();
	static ArrayList<ArrayList<String>>  allStatesList_StatesSubstates  = new ArrayList<ArrayList<String>>();
	static ArrayList<ArrayList<String>>  labelList  = new ArrayList<ArrayList<String>>();	//temp list will hold only terms related to the current label, the reasons of which are being looked for
	static List<String>  subStatesList  	= new ArrayList<String>();
	static ArrayList<ArrayList<String>>  reasonsList  		= new ArrayList<ArrayList<String>>();
	static List<String>  reasonIdentifierTerms  = new ArrayList<String>(); 
	static List<String>  proposalIdentifiers  = new ArrayList<String>(); 
	static List<String>  entitiesList 		= new ArrayList<String>(); 
	static ArrayList<ArrayList<String>>  specialOSCommunitySpecificTerms = new ArrayList<ArrayList<String>>();
	static List<String>  negationTerms 		= new ArrayList<String>(); 
	static List<String>  conditionalTerms 	= new ArrayList<String>();
	static List<String>  positiveWords 		= new ArrayList<String>(); 	
	static List<String>  negativeWords 		= new ArrayList<String>();	
	static ArrayList<ArrayList<String>>  decisionTerms 		= new ArrayList<ArrayList<String>>();
	
	static boolean messageSubjectContainsReasonsTerms =false ,   messageSubjectContainsReasonsIdentifierTerms=false, 	  	messageSubjectContainsStatesSubstates=false, 
		messageSubjectContainsProposalIdentifierTermList=false , messageSubjectContainsPositiveWords=false, 		 		messageSubjectContainsNegativeWords=false,
		messageSubjectContainsVerbTerms=false,			  		messageSubjectContainsEntitiesTerms=false,		  		  	messageSubjectContainsSpecialTerms=false,
		messageSubjectContainsDecisionTerms=false,				messageSubjectContainsProposalIdentifierTerms=false,		messageSubjectNegationFound =false;
	static boolean sentenceOrParagraph_containsReasonsTerms=false , 	sentenceOrParagraph_containsReasonsIdentifierTerms=false, sentenceOrParagraph_containsStatesSubstates=false, 
		sentenceOrParagraph_containsIdentifierTermList=false , 	sentenceOrParagraph_containsPositiveWords=false, 		  		sentenceOrParagraph_containsNegativeWords=false,
		sentenceOrParagraph_containsVerbTerms=false,			sentenceOrParagraph_containsIdentifierTerms=false,		  		sentenceOrParagraph_containsEntitiesTerms=false,
		sentenceOrParagraph_containsSpecialTerms=false,			sentenceOrParagraph_containsSpecialTermsInMessageSubject=false,
		sentenceOrParagraph_containsNegationTermList=false,		sentenceOrParagraph_containsDecisionTerms=false;

	static String manualreasonextractionTable = "trainingData";
	static boolean output= false;
	//aug 2018, split message into sentences, label and then store 
	static boolean splitMessageIntoSentences = true, labelSentences=true,checkMessageSubject=true;
	
	public static void main(String[] args) throws SQLException, IOException {		
		connections.MysqlConnect mc = new MysqlConnect();		Connection connection = mc.connect();
		Statement stmt = null,stmt2=null;
		
		int pNum=0;
		//for wordcloud and knowledge graph
		boolean extractSentencesForWordCloud=false;
		FileWriter fw = new FileWriter("c:\\scripts\\manuallyExtractedSentenceLabelled.txt", true);
		BufferedWriter bw = new BufferedWriter(fw);

		String sentenceOrParagraph = "", mid = "", dateVal = "", termsMatched = "", level = "", folderML = "",	author = "",sentenceorParagrapghString="";
		boolean fullText = true, labelWithCombination=false,matchWholeTerms=true;
		
		int arrayCounter = 0, recordSetCounter = 0, proposalNum=0;
		initialiseTermLists();
		String labelSToCheck[] = {"accepted","rejected"};	
		for(String labels: labelSToCheck) {	
//			System.out.println("New Proposal : " + pNum + " Label: "+labels);
			//THIRD GET MANUALLY EXTRACTED CAUSE SENTENCE FOR THAT PEP and Label
			//IF a corresponding manual sentence for that label or sentence is not entered, then we displaya dn dont count
			String sqlManual = "SELECT pep,label,causeSentence, effectSentence, causeCategory, causeSubcategory from " + manualreasonextractionTable + " "
					+ " where label like '"+labels+"' order by pep ;" ; 	//and pep = 311  //and pep = "+pNum+" 
			stmt2 = connection.createStatement(); 		ResultSet rsB = stmt2.executeQuery(sqlManual); // date asc
			int rowCount2 = guih.returnRowCount(sqlManual); //System.out.println("\tManually Extracted Reasons records found: " + rowCount2);
			String manualLabel="",causeSentence="",effectSentence="",causeCategory="",causeSubcategory="";
			//FOR EACH PROPOSAL ... GET ALL records but concentrate on the top ones
			boolean sentenceLabelFound = false;
			while (rsB.next()) {   //currDate.before(goToDate)) {
				messageSubjectContainsReasonsTerms = messageSubjectContainsReasonsIdentifierTerms=messageSubjectContainsStatesSubstates= 
				messageSubjectContainsProposalIdentifierTermList= 	messageSubjectContainsPositiveWords= 		 		messageSubjectContainsNegativeWords=
				messageSubjectContainsVerbTerms=			  		messageSubjectContainsEntitiesTerms=		  		messageSubjectContainsSpecialTerms=
				messageSubjectContainsDecisionTerms=				messageSubjectContainsProposalIdentifierTerms=		messageSubjectNegationFound =
				sentenceOrParagraph_containsReasonsTerms= 			sentenceOrParagraph_containsReasonsIdentifierTerms= sentenceOrParagraph_containsStatesSubstates=
				sentenceOrParagraph_containsIdentifierTermList= 	sentenceOrParagraph_containsPositiveWords= 		  	sentenceOrParagraph_containsNegativeWords=
				sentenceOrParagraph_containsVerbTerms=				sentenceOrParagraph_containsIdentifierTerms=		sentenceOrParagraph_containsEntitiesTerms=
				sentenceOrParagraph_containsSpecialTerms=			sentenceOrParagraph_containsSpecialTermsInMessageSubject=
				sentenceOrParagraph_containsNegationTermList=		sentenceOrParagraph_containsDecisionTerms=false;
			
				sentenceLabelFound=true;
				proposalNum = rsB.getInt("pep");		manualLabel = rsB.getString("label");		causeSentence = rsB.getString("causeSentence");	
				effectSentence = rsB.getString("effectSentence"); 		causeCategory = rsB.getString("causeCategory");				causeSubcategory = rsB.getString("causeSubcategory");
				recordSetCounter++;				
				
				//process sentence
				causeSentence = pm.removeUnwantedText(causeSentence);		//a lot of things are removed here which may be important ..like punctuation for finding out of sentence is code or not		
				causeSentence = pm.removeLRBAndRRB(causeSentence);
				causeSentence = causeSentence.replaceAll("\\p{P}", " "); //remove all punctuation ..maybe we can use this here (term matching for candidates) but not in CIE triple extraction as it wmay need commas and other punctuation
				causeSentence = causeSentence.replaceAll("\\s+", " ").trim(); //remove double spaces and trim
				System.out.println(proposalNum+" "+ manualLabel+" "+causeSentence);
				
				//append the sentence into a file for wordcloud and knowledge graph 					
				if(fullText) {
					if(causeSentence==null || causeSentence.isEmpty()) {}
					else  	{
	//					bw.write(proposalNum+"\t"+causeSentence);				    bw.newLine(); //continue; //continue to next record
					}
				}
				if(labelWithCombination) {
					if(causeSentence==null || causeSentence.isEmpty()) {}
					else  	
					{  
						sentenceorParagrapghString = causeSentence;
						sentenceorParagrapghString=sentenceorParagrapghString.toLowerCase();
//						System.out.println("\nLocation:"+ location+", "+sentenceorParagrapghString);
						//FIRST CHECK...CHECK FOR JUST THE REASON TERMS
						String sentenceorParagrapghString_markedWithCombination = containsMarkAllTermsListOfLists(sentenceorParagrapghString,reasonsList, matchWholeTerms,"R");
						if(sentenceorParagrapghString_markedWithCombination == null || sentenceorParagrapghString_markedWithCombination.isEmpty() ||sentenceorParagrapghString_markedWithCombination.equals("") ||sentenceorParagrapghString_markedWithCombination.length()==0) {}
						else {		sentenceOrParagraph_containsReasonsTerms =true;	//System.out.print(" reasonsTermsFoundList: ("+ sentenceorParagrapghString_reasonsTermsFoundList+ ")");	
						}			
					
						//SECOND CHECK...CHECK FOR JUST THE REASON TERMS
						sentenceorParagrapghString_markedWithCombination = containsMarkAllTerms(sentenceorParagrapghString,reasonIdentifierTerms, matchWholeTerms,"RI");
						if(sentenceorParagrapghString_markedWithCombination == null || sentenceorParagrapghString_markedWithCombination.isEmpty() ||sentenceorParagrapghString_markedWithCombination.equals("") ||sentenceorParagrapghString_markedWithCombination.length()==0) {}
						else {		sentenceOrParagraph_containsReasonsIdentifierTerms=true;	//System.out.print(" reasonsIdentifierTermsList: ("+ sentenceorParagrapghString_reasonsIdentifierTermsList+ ")");		
						}	
							
						//THIRD CHECK...CHECK FOR JUST THE REASON TERMS							//labelList
						sentenceorParagrapghString_markedWithCombination = containsMarkAllTermsListOfLists(sentenceorParagrapghString,allStatesList_StatesSubstates, matchWholeTerms,"S");	//allStatesList_StatesSubstates
						if(sentenceorParagrapghString_markedWithCombination == null || sentenceorParagrapghString_markedWithCombination.isEmpty() ||sentenceorParagrapghString_markedWithCombination.equals("") ||sentenceorParagrapghString_markedWithCombination.length()==0) {}
						else { sentenceOrParagraph_containsStatesSubstates=true;	//System.out.print(" statesSubstatesList: ("+ sentenceorParagrapghString_statesSubstatesList+ ")");		
						}	
							
						//FOURTH CHECK...CHECK FOR JUST THE VERB TERMS
						String sentenceorParagrapghString_verbList = containsAllTerms(sentenceorParagrapghString,verbs, matchWholeTerms);
						if(sentenceorParagrapghString_verbList == null || sentenceorParagrapghString_verbList.isEmpty() || sentenceorParagrapghString_verbList.length()==0) {}
						else {			sentenceOrParagraph_containsVerbTerms=true; System.out.print(" verbList: ("+ sentenceorParagrapghString_verbList+ ")");       
						}			//System.out.println("verbList: "+ verbList);
						
						//FIFTH CHECK...CHECK FOR JUST THE identifiersTermList TERMS		
						sentenceorParagrapghString_markedWithCombination = containsMarkAllTerms(sentenceorParagrapghString,proposalIdentifiers, matchWholeTerms,"PI");
						if(sentenceorParagrapghString_markedWithCombination == null || sentenceorParagrapghString_markedWithCombination.isEmpty() || sentenceorParagrapghString_markedWithCombination.length()==0) {}
						else {			sentenceOrParagraph_containsIdentifierTerms=true;	//System.out.print(" identifiersTermList: ("+ sentenceorParagrapghString_identifiersTermList+ ")");   	
						}	
						
//						System.out.print("CHECKING entitiesList >> ");  for (String line : entitiesList)     {                	System.out.print(line+ " ");            }		  System.out.println(" ");
						//SIXTH CHECK...CHECK FOR JUST THE identifiersTermList TERMS		
						sentenceorParagrapghString_markedWithCombination = containsMarkAllTerms(sentenceorParagrapghString,entitiesList, matchWholeTerms,"E");
						if(sentenceorParagrapghString_markedWithCombination == null || sentenceorParagrapghString_markedWithCombination.isEmpty() || sentenceorParagrapghString_markedWithCombination.length()==0) {}
						else {			sentenceOrParagraph_containsEntitiesTerms=true;		//System.out.print(" entitiesTermList: ("+ sentenceorParagrapghString_entitiesTermList+ ")");  
						}	
						
//						System.out.print("CHECKING specialTerms >> ");  for (String line : specialOSCommunitySpecificTerms)     {                	System.out.print(line+ " ");            }		  System.out.println(" ");
						//SEVENTH CHECK...CHECK FOR JUST THE special TERMS	- contains entities	
						sentenceorParagrapghString_markedWithCombination = containsMarkAllTermsListOfLists(sentenceorParagrapghString,specialOSCommunitySpecificTerms, matchWholeTerms,"ST");
						if(sentenceorParagrapghString_markedWithCombination == null || sentenceorParagrapghString_markedWithCombination.isEmpty() || sentenceorParagrapghString_markedWithCombination.length()==0) {}
						else {			sentenceOrParagraph_containsSpecialTerms=true;		//System.out.print("specialTermList: ("+ sentenceorParagrapghString_specialTermList+ ")");  
						}	

						sentenceorParagrapghString_markedWithCombination = containsMarkAllTermsListOfLists(sentenceorParagrapghString,decisionTerms, matchWholeTerms,"D");
						if(sentenceorParagrapghString_markedWithCombination == null || sentenceorParagrapghString_markedWithCombination.isEmpty() || sentenceorParagrapghString_markedWithCombination.length()==0) {}
						else {			sentenceOrParagraph_containsDecisionTerms=true;		//System.out.print(" decisionTermList: ("+ sentenceorParagrapghString_decisionTermList + ")");
						}	
						
						//in case of a sentence
						//check negation or conditional terms dont exist, ideally not before state or reason term
//						if (stringLoc=="sentence") {
							//also need to consider conditionalterms		
						sentenceorParagrapghString_markedWithCombination = containsMarkAllTerms(sentenceorParagrapghString,negationTerms, matchWholeTerms,"N");
						if(sentenceorParagrapghString_markedWithCombination == null || sentenceorParagrapghString_markedWithCombination.isEmpty() || sentenceorParagrapghString_markedWithCombination.length()==0) {}
						else {
//							System.out.print(" MATCHED negationTermList: "+ negationTermList);
							//july 2018..there are some combinations (in reasonlist) where negation is part of the terms ... eg  'no objections'..so we consider them
								
						}
						
						//contains positive or negative words like flak, wrangling, 
						String sentenceorParagrapghString_positiveWordList = containsAllTerms(sentenceorParagrapghString,positiveWords, matchWholeTerms);
						if(sentenceorParagrapghString_positiveWordList == null || sentenceorParagrapghString_positiveWordList.isEmpty() || sentenceorParagrapghString_positiveWordList.length()==0) {}
						else {			sentenceOrParagraph_containsPositiveWords=true;	}
						
						String sentenceorParagrapghString_negativeWordList = containsAllTerms(sentenceorParagrapghString,negativeWords, matchWholeTerms);
						if(sentenceorParagrapghString_negativeWordList == null || sentenceorParagrapghString_negativeWordList.isEmpty() || sentenceorParagrapghString_negativeWordList.length()==0) {}
						else {			sentenceOrParagraph_containsNegativeWords=true;		}
												
						
						//COMPUTE PATTERN
						String pattern = "";
						//if (location.equals("sameMessage") && containsReasonsTerms && containsReasonsIdentifierTerms && containsStateTerms) {probability = "very high"; } //same message as state
						//the following witll be covered in the subsequent if, but i have it for flexibility later
						//all those 'very high'
						if(sentenceOrParagraph_containsReasonsTerms && sentenceOrParagraph_containsStatesSubstates && sentenceOrParagraph_containsIdentifierTerms) {  //most common combination
							pattern = "R S PI";
						}	//even same sentence 
						else if(sentenceOrParagraph_containsReasonsTerms && sentenceOrParagraph_containsStatesSubstates && sentenceOrParagraph_containsEntitiesTerms) {  //after discussion i accept OR I decided add
							pattern = "R S E"; 
						}	//even same sentence 
						//july 2018 added new one 'There have been some comments in favour of the proposal, no objections to the proposal as a whole, and some questions and objections about specific details. 
						//These are believed by the author to have been addressed by making changes to the PEP.'
						else if(sentenceOrParagraph_containsReasonsTerms && sentenceOrParagraph_containsIdentifierTerms && sentenceOrParagraph_containsEntitiesTerms) {  // comments/questions/objections addressed author pep
							pattern = "R PI E";  
						}
						else if(sentenceOrParagraph_containsReasonsTerms && sentenceOrParagraph_containsStatesSubstates && sentenceOrParagraph_containsReasonsIdentifierTerms) {
							pattern = "R S RI"; 
						}	
						//this helps for some pep, but for pep 311, it moves the actual sentence be lower in ranking
						else if(sentenceOrParagraph_containsReasonsTerms && sentenceOrParagraph_containsStatesSubstates) {
							pattern = "R S";
						}		
						else if(sentenceOrParagraph_containsDecisionTerms && sentenceOrParagraph_containsReasonsTerms && sentenceOrParagraph_containsEntitiesTerms) {
							pattern = "R D E";
						}
						//added aug 2018..for pep 311
						//if message author = proposal author, -- we have given more weightage in the message subject probabilities 
						//we just need 'proposal identifier' and 'reason' in sentence. and within dates - as its more likely
						else if(sentenceOrParagraph_containsReasonsTerms && sentenceOrParagraph_containsIdentifierTerms) {
							pattern = "R PI";	//to many false postives so we give little weightage
						}
						// Entity, state and proposal identifier as in pepe 365 = 'After reading all this I really don t believe that adding egg support to the stdlib at this time is the right thing to do I am therefore rejecting the PEP'
						else if(sentenceOrParagraph_containsEntitiesTerms && sentenceOrParagraph_containsStatesSubstates && sentenceOrParagraph_containsIdentifierTerms) {
							pattern = "E S PI";
						}
						//aug 2018 'If everyone who claims they understand the issues actually does, why is it so hard to reach a consensus?'
						else if(sentenceOrParagraph_containsReasonsTerms && sentenceOrParagraph_containsEntitiesTerms) {
							pattern = "R E";	//to many false postives so we give little weightage
						}
						//aug 2018...pep 3139 manual reason
						else if(sentenceOrParagraph_containsIdentifierTerms && sentenceOrParagraph_containsDecisionTerms ) {
							pattern = "PI D";	//to many false postives so we give little weightage
						}
						else if(sentenceOrParagraph_containsEntitiesTerms && sentenceOrParagraph_containsDecisionTerms ) {	//pep 507
							pattern = "D E";	//to many false postives so we give little weightage
						}
						else { //allother cases..just in case
							pattern = "";
						}
						
						bw.write(proposalNum+", "+pattern+","+sentenceorParagrapghString_markedWithCombination);				    bw.newLine(); //continue; //continue to next record
						
						}					
					
				}
				
				//aug 2018 updated...for sentences starting with a heading like 'Rejection Notice', we skip these terms 
				//check manual sentence after two terms
				String headers[]= {"BDFL Pronouncement","Rejection Notice","Rejection","PEP Rejection","Pronouncement","Acceptance"};
				for (String s: headers) {
					if(causeSentence.startsWith(s))
						causeSentence = causeSentence.replaceAll(s, "");
				}					
				
				//System.out.println(); //else {				}
				List<String> manualCauseSentenceTerms = Arrays.asList(causeSentence.toLowerCase().split(" ")); //causeSentence.toLowerCase().split(" ");			
			}	//end for each manual reason ..
		}
		bw.close();
	}
	
	public static ArrayList<String> ListOfListsToList(ArrayList<ArrayList<String>> list) {
		ArrayList<String> n = new ArrayList<String>();
		for (ArrayList<String> f : list) {			
				n.addAll(f);
		}
		return n;
	}
	
	public static String StackTraceToString(Exception ex) {
		String result = ex.toString() + "\n";
		StackTraceElement[] trace = ex.getStackTrace();
		for (int i=0;i<trace.length;i++) {
			result += trace[i].toString() + "\n";
		}
		return result;
	}
	
	public static List<String> readLines(String filename) throws IOException 
    {
        FileReader fileReader = new FileReader(filename);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        List<String> lines = new ArrayList<String>();
        String line = null;         

        while ((line = bufferedReader.readLine()) != null) 
        {
        	line= line.trim();
        	if(line.startsWith("--") || line.startsWith(";") || line.isEmpty() || line.equals("")) {}
        	else {
        		lines.add(line);
            }
        }       
        bufferedReader.close();
        return lines; //lines.toArray(new String[lines.size()]);
    } 
	
	public static ArrayList<ArrayList<String>> readLinesIntoListsOfLists(String filename) throws IOException 
    {
        FileReader fileReader = new FileReader(filename);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        ArrayList<ArrayList<String>> listOLists = new ArrayList<ArrayList<String>>();
        String line = null;         

        while ((line = bufferedReader.readLine()) != null) 
        {
        	line= line.trim();
        	if(line.startsWith("--") || line.startsWith(";") || line.isEmpty() || line.equals("")) {}
        	else {
        		//lines.add(line);
        		ArrayList<String> singleList = new ArrayList<String>(); //create a list
        		if (line.contains(" ")) {
        			for (String term: line.split(" ")) {	//split line and read into that list
        				singleList.add(term);		
        			}
        		}
        		else {
        			singleList.add(line);
        		}
        		listOLists.add(singleList); //add to main list
            }
        }       
        bufferedReader.close();
        return listOLists; //lines.toArray(new String[lines.size()]);
    } 
	
	public static void initialiseTermLists(WordnetSynonyms wn) {
		prp = new ProcessingRequiredParameters();		ps  = new ParagraphSentence();		pm  = new PythonSpecificMessageProcessing();		pms = new ProcessMessageAndSentence();
		
		StateAndReasonLabels l = new StateAndReasonLabels();
		boolean output=true;
		l.initStateAndReasonLabels(wn, output);
		verbs = l.getVerbs(); 	//ready for review/pronouncement
		committedStates = l.getCommittedStates();	//main state changes  e.g. 'Status: Accepted'
		System.out.print("FINAL committedStates >> ");  for (String line : committedStates)     {                	System.out.print(line+ " ");            }		  System.out.println(" ");  
		allStatesList_StatesSubstates = l.getAllStatesList_StatesSubstates();
		reasonsList = l.getReasonTerms(); 
		reasonIdentifierTerms = l.getReasonIdentifierTerms(); 
		proposalIdentifiers = l.getProposalIdentifierTerms();
		System.out.print("FINAL getAllStatesList_StatesSubstates >> ");
		for (ArrayList<String> line : allStatesList_StatesSubstates) {   //for each list as line 
			for (String term : line) {	//for each term in line
	        	System.out.print("'"+term+ "', ");        		
			}	System.out.println(); 
		}	//	  System.out.println(" ");  
		System.out.print("FINAL reasonsList >> ");  for (ArrayList<String> line : reasonsList)     {                	System.out.print(line+ ", ");            }		  System.out.println(" "); 
		System.out.print("FINAL reasonIdentifierTerms >> ");  for (String line : reasonIdentifierTerms)     {                	System.out.print(line+ ", ");            }		  System.out.println(" ");  
		System.out.print("FINAL proposalIdentifiers >> ");  for (String line : proposalIdentifiers)     {                	System.out.print(line+ ", ");            }		  System.out.println(" ");
		specialOSCommunitySpecificTerms = l.getSpecialOSCommunitySpecificTerms(); 
		entitiesList = l.getEntities();
		System.out.print("FINAL specialOSCommunitySpecificTerms >> ");  for (ArrayList<String> line : specialOSCommunitySpecificTerms)     {                	System.out.print(line+ ", ");            }		  System.out.println(" ");
		mainStates = l.getSubStates();
		System.out.print("FINAL entitiesList >> ");  for (String line : entitiesList)     {                	System.out.print(line+ ", ");            }		  System.out.println(", ");
		System.out.print("FINAL subStates >> ");  for (String line : mainStates)     {                	System.out.print(line+ ", ");            }		  System.out.println(", ");
		negationTerms = l.getNegationTerms();
		System.out.print("FINAL negationTerms >> ");  for (String line : negationTerms)     {                	System.out.print(line+ ", ");            }		  System.out.println(", ");
		conditionalTerms= l.getConditionalTerms();
		System.out.print("FINAL conditionalTerms >> ");  for (String line : conditionalTerms)     {                	System.out.print(line+ ", ");            }		  System.out.println(", ");
		decisionTerms= l.getDecisionTerms();
		System.out.print("FINAL deisionTerms >> ");  for (ArrayList<String> line : decisionTerms)     {                	System.out.print(line+ ", ");            }		  System.out.println(", ");
		//statesList = ObjectArrays.concat(statesList, decisionMechanismsSubStates, String.class); //we will combine statesList and decisionMechanismsSubStates
		//statesList.addAll(decisionMechanismsSubStates);
		//specialTerms = ObjectArrays.concat(specialTerms, entities, String.class);	//special terms and entities are combined
		//specialTerms.addAll(entitiesList);
		positiveWords = l.getPositiveWords();
		negativeWords = l.getNegativeWords();
	}
	
	public static void initialiseTermLists() {		
		try {
			//read the fileNames from the propoerties file
			PropertiesFile wpf = new PropertiesFile();
    		// wpf.WriteToPropertiesFile("includeEmptyRows", includeEmptyRows.toString());
    		//includeStateData						
            committedStates = readLines(wpf.readFromPropertiesFile("stateCommitsFileName",false));	//each of the lists are read in from a separate file	
            System.out.println("committedStates:");   for (String line : committedStates)     {                	System.out.print(line+ " ");            }		  System.out.println(" ");  
            allStatesList_StatesSubstates = readLinesIntoListsOfLists(wpf.readFromPropertiesFile("mainStatesFileName",false));	//each of the lists are read in from a separate file	
            System.out.println("commonStatesAllTermForms:"); 
            if(output) {
            	for (ArrayList<String> line : allStatesList_StatesSubstates) {  //for each line     	
            		System.out.println("Read in allStates line: "+ line+ " : ");
            		for (String val : line) {  //for each term in line
	            		ArrayList<String> al = new ArrayList<String>(); 
	            		//al = wn.returnSynonyms(val);
	            		System.out.print("Term: "+ val+ " : ");
	            		for (String l : al) {                	
	                		//System.out.print("'"+l+ "', ");
	            		}
	            		//System.out.println(" "); 
            		}
            	}		  //System.out.println(" ");  
            }  
//            mainStates = readLines(wpf.readFromPropertiesFile("decisionMechanismsSubStatesFileName",false));	//each of the lists are read in from a separate file	
//            System.out.println("subStates:");     for (String line : mainStates)     {   System.out.print(line+ " ");            }		  System.out.println(" ");
            reasonsList = readLinesIntoListsOfLists(wpf.readFromPropertiesFile("reasonsFileName",false));	//each of the lists are read in from a separate file	
            System.out.println("reasonTerms:");   for (ArrayList<String> line : reasonsList)     {                		System.out.print(line+ " ");            }		  System.out.println(" ");  
            reasonIdentifierTerms = readLines(wpf.readFromPropertiesFile("reasonIdentifierTermsFileName",false));	//each of the lists are read in from a separate file	
            System.out.println("reasonIdentifierTerms:"); for (String line : reasonIdentifierTerms)     {                System.out.print(line+ " ");            }		  System.out.println(" ");  
            proposalIdentifiers = readLines(wpf.readFromPropertiesFile("identifiersFileName",false));	//each of the lists are read in from a separate file	
            System.out.println("proposalIdentifiers:"); for (String line : proposalIdentifiers)     {                System.out.print(line+ " ");            }		  System.out.println(" ");
            entitiesList = readLines(wpf.readFromPropertiesFile("entitiesFileName",false));	//each of the lists are read in from a separate file	
            System.out.println("entities:"); 	  for (String line : entitiesList)     {               		System.out.print(line+ " ");            }		  System.out.println(" ");  
            specialOSCommunitySpecificTerms = readLinesIntoListsOfLists(wpf.readFromPropertiesFile("specialTermsFileName",false));	//each of the lists are read in from a separate file	
            System.out.println("specialOSCommunitySpecificTerms:"); for (ArrayList<String> line : specialOSCommunitySpecificTerms)     {                  System.out.print(line+ " ");            }		  System.out.println(" ");  
            positiveWords = readLines(wpf.readFromPropertiesFile("positiveTermsFileName",false));	//each of the lists are read in from a separate file	
            System.out.println("positiveWords:"); for (String line : positiveWords)     {                 System.out.print(line+ " ");            }		  System.out.println(" ");  
            negativeWords = readLines(wpf.readFromPropertiesFile("negativeTermsFileName",false));	//each of the lists are read in from a separate file	
            System.out.println("negativeWords:"); for (String line : negativeWords)     {                 System.out.print(line+ " ");            }		  System.out.println(" ");
            negationTerms = readLines(wpf.readFromPropertiesFile("negationTermsFileName",false));	//each of the lists are read in from a separate file	
            System.out.println("negationTerms:"); for (String line : negationTerms)     {                 System.out.print(line+ " ");            }		  System.out.println(" "); 
            conditionalTerms = readLines(wpf.readFromPropertiesFile("conditionalTermsFileName",false));	//each of the lists are read in from a separate file	
            System.out.println("conditionalTerms:"); for (String line : conditionalTerms)     {                 System.out.print(line+ " ");            }		  System.out.println(" "); 
            decisionTerms = readLinesIntoListsOfLists(wpf.readFromPropertiesFile("decisionTermsFileName",false));	//each of the lists are read in from a separate file	
            System.out.println("decisionTerms:"); for (ArrayList<String> line : decisionTerms)     {                 System.out.print(line+ " ");            }		  System.out.println(" ");
        }
        catch(IOException e)   {
            // Print out the exception that occurred
            System.out.println("Unable to read from file : "+e.getMessage());
            e.printStackTrace();
			System.out.println(StackTraceToString(e)  );	

        }
	}	
	
	public static String containsAllTerms(String sentenceorParagrapghString, List<String> list, boolean matchWholeTerms) {
		String combination = "",finalCombination="";
		ArrayList<String> listofTermsMatched = new ArrayList<String>();
		for (String r : list ) {
			//check state,identifier, and reasons only
//			if(r.split(" ").length ==1) { continue;}	//dont capture single words reasons here as too many false positives ..but they are in the list because they will be used in combination
			if (ps.containsAllTermsAsTheyAre(r,sentenceorParagrapghString, matchWholeTerms)) {	//subject+" "+verb+" "+object
//				combination = r;						
//				if(location.equals("sentence") && listofTermsMatched.contains(combination))	//paragraph
//				{}
//				else {	//sentence
					finalCombination= finalCombination + " " + r; 
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
	public static String containsMarkAllTerms(String sentenceorParagrapghString, List<String> list, boolean matchWholeTerms, String combinationLabel) {
		String combination = "",finalCombination="";
		ArrayList<String> listofTermsMatched = new ArrayList<String>();
		for (String r : list ) {
			//check state,identifier, and reasons only
//			if(r.split(" ").length ==1) { continue;}	//dont capture single words reasons here as too many false positives ..but they are in the list because they will be used in combination
			if (ps.containsAllTermsAsTheyAre(r,sentenceorParagrapghString, matchWholeTerms)) {	//subject+" "+verb+" "+object
				//get the index of the word, add label in brackets
				int wordIndex = sentenceorParagrapghString.indexOf(r) + r.length();
				finalCombination = finalCombination.substring(0, wordIndex) + "["+combinationLabel+"]" + sentenceorParagrapghString.substring(wordIndex, sentenceorParagrapghString.length());
			}
		}
		return finalCombination.trim();
	}
	//in each sentence, we look for all combinations for each state, for accepted, we look for accept, accepting, accepted
	public static String containsAllTermsListOfLists(String sentenceorParagrapghString, ArrayList<ArrayList<String>> listOfLists,boolean matchWholeTerms) {
		String combination = "",finalCombination="";
		ArrayList<String> listofTermsMatched = new ArrayList<String>();
		for (ArrayList<String> line : listOfLists) {    //for each list            	
//$$    		System.out.println("Checking Line: "+line+ " : ");
    		for (String term : line) {      	//for each term in list 
    			//System.out.print("'"+term+ "' ");
    			//some terms in list of list are a combination of double term ..so we split here
    			if(term.contains("-"))
    				term = term.replace("-", " ");
    			if (ps.containsAllTermsAsTheyAre(term,sentenceorParagrapghString, matchWholeTerms)) {	//subject+" "+verb+" "+object
					finalCombination= finalCombination + " " + term; 								
    			}
    		}	//System.out.println("");
    	}
		return finalCombination.trim();
	}
	public static String containsMarkAllTermsListOfLists(String sentenceorParagrapghString, ArrayList<ArrayList<String>> listOfLists,boolean matchWholeTerms,String combinationLabel) {
		String combination = "",finalCombination="";
		ArrayList<String> listofTermsMatched = new ArrayList<String>();
		for (ArrayList<String> line : listOfLists) {    //for each list            	
//$$    		System.out.println("Checking Line: "+line+ " : ");
    		for (String term : line) {      	//for each term in list 
    			//System.out.print("'"+term+ "' ");
    			//some terms in list of list are a combination of double term ..so we split here
    			if(term.contains("-"))
    				term = term.replace("-", " ");
    			if (ps.containsAllTermsAsTheyAre(term,sentenceorParagrapghString, matchWholeTerms)) {	//subject+" "+verb+" "+object
    				int wordIndex = sentenceorParagrapghString.indexOf(term) + term.length();
    				finalCombination = finalCombination.substring(0, wordIndex) + "["+combinationLabel+"]" + sentenceorParagrapghString.substring(wordIndex, sentenceorParagrapghString.length());								
    			}
    		}	//System.out.println("");
    	}
		return finalCombination.trim();
	}

}
