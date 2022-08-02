package relationExtraction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

//import StanfordTest.StanfordCoreNLPFunctions;
//import allStanfordToolsInOne.StanfordNLPTools;
import callIELibraries.SocketClientForNLP;
import connections.MysqlConnect;
import javaStringSimilarity.info.debatty.java.stringsimilarity.Levenshtein;
import utilities.PepUtils;
import utilities.StateAndReasonLabels;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;

//Extract information from extracted CIE Triple Relations

//Jan 2019, now most of the relation extraction has been refined and therefore a lot of code here may not be needed.
//as we now extract the verb amnd noun from message subject as our starting point, and select distinct combinations, a lot of our work is done
//SQL: select distinct msgsubject,allverbsinmsgsub,allnounsinmsgsub from extractedrelations_clausie
//aFTER RUNNING THE ABOVE SQL, WE CAN SEE WHICH terms need to be removed further, like patch, etc, then, we can try grouping by only  allverbsinmsgsub,allnounsinmsgsub to see frequency
//maybe select only distinct allverbsinmsgsub,allnounsinmsgsub to see shorter list for analysis. we start by looking at those for pep proposal, and idea, - verbs associated with these as nouns
//second level, we see the above for the main states - we extract the verbs on 'accept' as noun, we may get vote, consensus, review, bdfl
//third level, we use these verbs in second level, to get reasons...?? ambitious

//it looks like now all we want is to remove noisy terms and select distinct allverbsinmsgsub,allnounsinmsgsub from this list above
//we can use sql, see these noisy terms, go back code in java, but since we dont have enough time, we can let the RE keep going and remove them here and store it back for sql distinct selection

//Dec 2018 ...
//This script basically, for each of the states, shows, substates and reasons, and removes them from resultset. This leaves the remaining resultset to be manually analysed by human for labelling
//Feed-in some known terms from domain ..metadata like proposal identifiers, eg. PEP,BDFL  or JEP for Java,  
// this script would find the states and substates that are used in decision making
//Note generally there are some states that would be known (8 main states in Python and similar number of states in JEP), but not all, eg second draft, etc

//First, it would use the proposal identifiers,(PEP, proposal, idea, JEP, etc) needs some seed terms, as stated above and aims to output the main relations on these concepts - states and substates
//pep was accepted

//Second layer, July 2018, we would built a second layer to find reasons. use states and substates to find rlated concepts, eg, accepted as consensus 
//Third layer, when we have to find reasons for substates Example, vote because controversy
//would be good idea to make up a knowledge graph, and these terms would feed in into our reason terms identifying

//dec 2018, we add isparagraph first or isparagraph last, messagesuthor role, and message subject
//dec 2018, we change teh approacha  bot, this time we only look for distinct operations of an subject, eg pep ..how? we query all triples where sub or obj is pep/proposal. then we get tally of obj or sub terms by frequency in the other column
//then we remove all records with these terms so we can have the lesser frequent verbs and object which we can judge using human being

//dec 2018 we have two modes, extraction all various (1) states and substates and 2) reason extraction for state and substate extraction, we eliminate each state/substate from teh resultset so we can look for new ones
//while for reason extraction we extract these states from db only one by one so we can see the reasons

//Note the new RE script (which is part of the main script now) checks to make sure each messageid is RE once only regardless if its assigned under multiple PEPs

public class ExtractKnowledgeFromExtractedTriples {
	
	static SocketClientForNLP scnlp = new SocketClientForNLP();	
	static InsertIntoRelationTable irt = new InsertIntoRelationTable();	
	static String extractedRelationsTableName    = "extractedrelations_clausie";  //source tablename
	static String labelextraction_Sub_tablename  = "labelextraction_sub";	//results table
	static String labelextraction_Obj_tablename  = "labelextraction_ob";		//results table
	static StateAndReasonLabels sr = new StateAndReasonLabels();
	static Connection conn;
	//added jan 2019
	//here we increase and decrease Levenshtein_distance_threshold and see which is best value
	static Levenshtein levenshtein = new Levenshtein();	// Levenshtein
	static Integer Levenshtein_distance_threshold = 3,Levenshtein_distance_threshold_small_terms = 2;
	static ArrayList<Integer> uniquePropsals = new ArrayList<Integer>(); 
	static Multimap<Integer, String> multiMapFirstTitle,multiMapFinalTitle; //jan 2019
	static List<Integer> proposalsMatched = new ArrayList<Integer>();		//list of all the proposal numbers matched in each message, should be emptied after checking each message
	static PepUtils pu = new PepUtils();
	static boolean debugMode= false, insertIntoMainTableMode=false;
	static String debugTable = "proposaldetails_titlesforeachpep_debug";
	static String tableWithFirstLastProposalTitle = "pepstates_danieldata_wide";	//table to get first and last proposal titles
	static String tablenameToCheck  = "extractedrelations_clausie";	
	static String tablenameToUpdate = "extractedrelations_clausieXXXXXXX";
	static boolean test = false, database = true ,removeNumbersAndPunctuation=true;	//do we remove Numbers And Punctuation from dataset
	static boolean  removePEPTitlesFromDataset= true, //pre-processing stage
					firstLevel=false, 		secondLevel=false, 				thirdLevel=false;
	static String proposalIdentifier = "pep";
	
	public static void init() {		
		try {
			uniquePropsals = pu.returnUniqueProposalsInDatabase(proposalIdentifier);
			if(uniquePropsals.size() <= 1)
				System.out.println("No proposal Titles found in Table");
			else {
				// create multimap to store key and values
		        multiMapFirstTitle = ArrayListMultimap.create();		        multiMapFinalTitle = ArrayListMultimap.create();
				populateMapWithPEPTitles(multiMapFirstTitle,multiMapFinalTitle);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void populateMapWithPEPTitles(Multimap<Integer, String> multiMapFirstTitle, Multimap<Integer, String> multiMapFinalTitle)	throws SQLException {
		String titleinfirststate,finalTitle,sql;
		int pep;	
		Statement stmt;		ResultSet rs;
		// put values into map for A	        
		sql = "SELECT pep, titleinfirststate, finaltitle from "+tableWithFirstLastProposalTitle+" where pep IS NOT NULL order by pep asc ";  //where pep != -1 order by pep asc LIMIT 5000
		stmt = conn.createStatement();
		rs = stmt.executeQuery( sql );	
		while (rs.next()) {
			pep = rs.getInt(1);			titleinfirststate = rs.getString(2);
			finalTitle = rs.getString(3);
			//System.out.println("pep : "+ pep +" titleinfirststate " +titleinfirststate);
			if (titleinfirststate != null){
				titleinfirststate = cleanTerm(titleinfirststate);	multiMapFirstTitle.put(pep, titleinfirststate);
				finalTitle = cleanTerm(finalTitle);					multiMapFinalTitle.put(pep, finalTitle);
//$$			System.out.println(pep +", 	  " +titleinfirststate+",    "+finalTitle);
			} 
		}
	}
	
	public static void main(String[] args) throws IOException, SQLException {		
		//scnlp.init(); //lets hope we wont need this		
		List<String> lines = new ArrayList<String>();
		BufferedReader reader = null;
		try {
			//readStopWordsFile(lines);
			MysqlConnect mc = new MysqlConnect();			conn= mc.connect();			
			String subject="",allNounsInMsgSubject="", titleinfirststate="",subset="",sql,sql0; //,newName = null, bdflDelegate, newBDFLDelegate;
			Integer mid = null, counts=0;
			int pepNumber,pep;
			//double levenshtein_distance,levenshtein_distance_final;
			Statement stmt,stmt0; ResultSet rs,rs0;
			boolean firstMatched = false, finalMatched=false, exactMatch=true,levenshteinDistanceMatch=true;
			
			//init();	//do the pep title reading part
						
			if (test){
	        	mid= -2; pep = -2;	uniquePropsals.add(pep); //dummy pep title vales
	        	// create multimap to store key and values
		        multiMapFirstTitle = ArrayListMultimap.create();		        multiMapFinalTitle = ArrayListMultimap.create();
	        	multiMapFirstTitle.clear();  multiMapFinalTitle.clear();   //just to be sure
	        	multiMapFirstTitle.put(pep, "python 3.0 plans"); //dummy pep first title values	'python 3.0 plans'
	        	multiMapFinalTitle.put(pep, "python 3000");		 //dummy pep final title values
	        	subject="python 3.0 unicode and os environ";	 // test message subject to check
	        	Set<Integer> keys = multiMapFirstTitle.keySet();
	        	//check only this subject with the pep titles fed in
	        	if(removePEPTitlesFromDataset) {
		        	proposalsMatched = matchFirstAndFinalTitlesWithSubject(subject, mid,exactMatch,levenshteinDistanceMatch, conn); //end for loop  multiMapFirstTitleText, multiMapFinalTitleText,
		        	//we modify the above function to update the mgg subject in table as well, only if titles matched 
		        	for (Integer k : proposalsMatched )
		        		System.out.println("Proposal Matched: " + k);
	        	}
	        }
	        
	        //perform on database
	        if(database){	
		        // get all the set of keys - keys for both maps should be same
	        	
	        	//PreparedStatement stat = c.prepareStatement("SELECT * FROM big_table",ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY); 
	        	//stat.setFetchSize(Integer.MIN_VALUE);	        	ResultSet results = stat.executeQuery();	
	        	
	        	
	        	//Jan 2019, First step, we go through the extracted data and remove unwanted terms
				//best way is to get a summary of all distinct 'allnounsinmsgsubject' by frequency and remove the common occuring ones
	        	
		        Set<Integer> keys = multiMapFirstTitle.keySet();		        
				//sql0 = "SELECT messageid, pep, subject from "+tablenameToCheck+" where "  //messageid = 90910";
				//	 + " pep = -1 and subject IS NOT NULL and folder NOT like '%lists%';"; //LIMIT 5000 //order by pep asc LIMIT 5000  ..or folder like '%idea%'
				sql0 = "SELECT allNounsInMsgSubject, count(*) as cnt from "+tablenameToCheck+" where "  //messageid = 90910";
						 + " pep >= -1 and allNounsInMsgSubject IS NOT NULL and folder NOT like '%lists%' "
						 + " GROUP BY CNT DESC;"; //LIMIT 5000 //order by pep asc LIMIT 5000  ..or folder like '%idea%'
				stmt0 = conn.createStatement();				rs0 = stmt0.executeQuery( sql0 );	
				//for each message
				while (rs0.next()) {
					proposalsMatched.clear();
					counts = rs0.getInt("cnt");					allNounsInMsgSubject = rs0.getString("allNounsInMsgSubject");	
					if (subject.trim().toLowerCase().startsWith("re ")){
						subject = subject.replace("re", "").trim();
					}
					//1. First and last proposal title match
					//we can match several proposals here from the list of proposal titles, so we insert for each
					//jan 2019, pre-processing stage - remove all pep title from the noun aspect
					/*
					if(removePEPTitlesFromDataset) {	 
						matchFirstAndFinalTitlesWithSubject(subject, mid, exactMatch,levenshteinDistanceMatch, conn); //end for loop multiMapFirstTitle, multiMapFinalTitle,
						List<Integer> listWithoutDuplicates = new ArrayList<>(new HashSet<>(proposalsMatched));
					}
					*/
					System.out.println("Proposal Matched: ");// + k);
					
					
					//store		..ideally there should just be one proposal matched, not multiple
	//				for (Integer p: listWithoutDuplicates) {
	//					updateTableWithPEPNumber(conn, mid, p);
	//				}
				}	//end while loop
		    }
			
			
			
			//HAVE TO REMOVE BRACKETS FROM VERB
//			String test = "watching tv (at home)"; 
//			test = test.replaceAll("\\p{P}","");
			
			//remove stopwords from S,V,O triples
			//remoreStopwords(conn);

			//extract all different verbs for known terms
			//String terms[] = {"pep","idea","bdfl","guido"};
			//for second lavel, manually selected terms
			
			//String terms[] = {"vote", "poll", "consensus", "guido", "pronouncement", "disagree"};
			//well known domain knowledge
			//dec 2018..just commit as we are no able to read
			//List<String> proposalIdentifiers = sr.getProposalIdentifierTerms();  //proposal identifiers which may be well known
			List<String> proposalIdentifiers = new ArrayList<String>();	
						 proposalIdentifiers.add("pep");	proposalIdentifiers.add("proposal");	proposalIdentifiers.add("idea");
			//List<String> mainStates = sr.getCommittedStates();  //main states which are well known
			List<String> mainStates = new ArrayList<String>();
						 mainStates.add("draft");	mainStates.add("accepted");		mainStates.add("rejected");		mainStates.add("withdrawn");	mainStates.add("superceded");
						 mainStates.add("final");	mainStates.add("incomplete");	mainStates.add("postponed");	mainStates.add("deferred");		mainStates.add("active");						 
						 mainStates.add("pending");	mainStates.add("replaced"); 	mainStates.add("closed");
			ArrayList<String> verbList=new ArrayList<String>(); 	//verbs is, was,has, - retrieved after checking the 30 peps
						verbList.add("is");	verbList.add("has"); verbList.add("was");
			
			//dec 2018, we first filter messages from bdfl,delegate or proposalauthor
			String where = " and messageAuthor = 'bdfl' OR messageAuthor ='proposalAuthor' ";			
			String where2 = " and firstParagraph = 1 OR lastparagraph =1 ";	//and are either from first or last paragraph
			//Note just to speed things up, we can add filter to return distinct triples before any queries can be performed 	
			
			
			
			//Dec 2018 ...
			//This script basically, for each of the states, shows, substates and reasons, and removes them from resultset. 
			//This leaves the remaining resultset to be manually analysed by human for labelling
			
			//A gui would have been for these purpose where we can keep adding terms to remove, keeping the proposal identifer in one of the args
			//i think we first we extract all distinct arg1, relation and arg2 and perform below operations on them to reduce workload
			//A) Distinct state and substate extraction
			//extract all triples where arg1 or arg2 are proposal identifiers
				//then feed in each known state as arg1 or arg2 and repeat the process
					//State, substate extraction
						// This will show substates "there was vote on pep". this will return the substates on the states, and maybe reasons of state change
					//Reason Extraction..
						// This will also show Operations on states (pep accepted after consensus) ..
				//then remove from this list where a known state is included in opposite arg 
					//This will even remove where proposal identifier and state is included in same column 'pep was accepted' 'as' 'consesnusus'  -  but we want these
				//Then we can go through the records one by one and remove identified verbs one by one from query. If a substate is found, we remove them from query as well
				//Of we can create a list of all common terms in opposite arg using highest frequency. We can use verb-pair extraction here instead of just terms
					//then we can feed in the substate or reason as arg1 or arg2 and retrieve more verbs - maybe useful 
					//These may display operations on substates (these are the vote results) ..
			
			//maybe we can feed these verbs is, was,has, - retrieved after checking the 30 peps
			
			//FIRST LAYER, GET All States and substates which are known and not known ON EACH IDENTIFIER TYPE
			//EXAMPLE, PEP was accepted
			//for each identifier term, output all related concepts, i.e. when subject or object is identifier, what are the opposite subject or objects
			//we would store this in a table to prevent have ing to do it over and over again for improvement
			if(firstLevel) {	
				//verbList = mainStates;
				for(String identifier : proposalIdentifiers) {	//System.out.println("Start 2");
					//show passive voice ..pep was accepted
					System.out.println("Start - When Subject is "+identifier+"-----Showing all distinct triple combinations---------------------");
					extractCandidateLabelTriplesForTermAsSubject_InsertInDB("",identifier,"arg1","arg2",labelextraction_Sub_tablename);
					//displayDistinctLabelsForTerm(identifier,"arg1","arg2",labelextraction_Sub_tablename);	//output distinct triples from database
					extractCandidateLabelTriplesForTermAsSubject_InsertInDB("",identifier,"arg2","arg1",labelextraction_Obj_tablename);
					//displayDistinctLabelsForTerm(identifier,"arg1","arg2",labelextraction_Obj_tablename);	//output distinct triples from database
					
					//SECOND Level Analysis, For all the extracted States and substates, extract the reasons
					//Example accepted as there was consensus/vote, vote would then be substate, the reason for which we need to go third level analysis
					//same process as above, but this time feed in the states/substates as main object or subject
					//also we can use reason identifier terms in the verb part to minimise results
					
					//To select which terms to check recursively in this second level, its better to be done manually...thats why we have the select option
					for(String st: mainStates) {
						System.out.println("Start - When Subject is "+identifier+" and state "+ st +"-----Showing all distinct triple combinations---------------------");
						extractCandidateLabelTriplesForTermAsSubject_InsertInDB(st,identifier,"arg1","arg2",labelextraction_Sub_tablename);
						extractCandidateLabelTriplesForTermAsSubject_InsertInDB(st,identifier,"arg2","arg1",labelextraction_Obj_tablename);
					}
					System.out.println("End - When Subject is "+identifier+"----------------------------------------------------------------------");					
				}
			}
			
			//dec 2018..new version of second level
			if(secondLevel) {
				for(String st: mainStates) {
					System.out.println("Start - When Subject is "+st+" and state "+ st +"-----Showing all distinct triple combinations---------------------");
					extractCandidateLabelTriplesForTermAsSubject_InsertInDB("",st,"arg1","arg2",labelextraction_Sub_tablename);
					extractCandidateLabelTriplesForTermAsSubject_InsertInDB("",st,"arg2","arg1",labelextraction_Obj_tablename);
				}
			}
				
			
			/*
			//old way
			//For a term, once the relations and opposite sub/obj are stored in the table, we extract the related concept/term by the following approaches
			//FIRST APPROACH .APPROACH.first verb and noun found in each clause
			//Second, Most common verbs in the opposite table field
			
			//SECOND Level Analysis, For all the extracted States and substates, extract the reasons
			//Example accepted as there was consensus/vote, vote would then be substate, the reason for which we need to go third level analysis
			//same process as above, but this time feed in the states/substates as main object or subject
			//also we can use reason identifier terms in the verb part to minimise results
			
			//To select which terms to check recursively in this second level, its better to be done manually...thats why we have the select option
			if(secondLevel) {
				//when term is subject, for each of the unique object found, treat them as subject and find the triple and objects combinations
				//just do it for top 10 (according to count) at the moment 
				for(String identifier : proposalIdentifiers) {
					//show passive voice ..pep was accepted
					System.out.println("Start - When Subject is "+identifier+"-----Showing all distinct triple combinations---------------------");
					extractCandidateLabelTriplesForTermAsSubject_InsertInDB("",identifier,"arg1","arg2",labelextraction_Sub_tablename);	//verbList
					
					//displayDistinctLabelsForTerm(identifier,"arg1","arg2",labelextraction_Sub_tablename);	//output distinct triples from database
					System.out.println("End - When Subject is "+identifier+"----------------------------------------------------------------------");
					//show active ..vote on pep
				}
			}
			
			//THIRD Level Analysis, For all the extracted States and substates, extract the reasons
			//Example, if second level returns accepted as there was consensus/vote, then since vote would then be substate, 
			//the reason for which we need to go third level analysis. Example, vote because controversy
			//same process as above, but this time feed in the substates as main object or subject
			//also we can use reason identifier terms in the verb part to minimise results			
			//Again, to select which terms to check recursively in this third level, its better to be done manually...thats why we have the select option
			if(thirdLevel) {
				//when term is subject, for each of the unique object found, treat them as subject and find the triple and objects combinations
				//just do it for top 10 (according to count) at the moment 
				for(String identifier : proposalIdentifiers) {
					//show passive voice ..pep was accepted
					System.out.println("Start - When Subject is "+identifier+"-----Showing all distinct triple combinations---------------------");
					extractCandidateLabelTriplesForTermAsSubject_InsertInDB("",identifier,"arg1","arg2",labelextraction_Sub_tablename);	//verbList	
					displayDistinctLabelsForTerm(identifier,"arg1","arg2",labelextraction_Sub_tablename);	//output distinct triples from database
					System.out.println("End - When Subject is "+identifier+"----------------------------------------------------------------------");	
				}
			}	
			*/
		}  
		catch(Exception ex) {
			//Thread.currentThread().interrupt();
			System.out.printf(ex.toString()); System.out.println(StackTraceToString(ex)  );
		}
	}
	
	//jan 2019. match and remove first and last pep titles, function from 'MatchProposalTitlesToProposalNumbers.java'
	//match passed text with each different proposals first and last title
	public static List<Integer> matchFirstAndFinalTitlesWithSubject(String messageSubject, Integer mid,//Multimap<Integer,  String> multiMapFirstTitle, Multimap<Integer, String> multiMapFinalTitle,	
			boolean exactMatch,		//do we want to allow exact match
			boolean levenshteinDistanceMatch,		//do we want to allow levenshtein distances matches at different offsets within the message subject
			Connection conn) throws SQLException {
		double levenshtein_distance,levenshtein_distance_final;
		String subset,remove, subject = messageSubject;
		boolean titleMatched;
		List<Integer> proposalsMatched = new ArrayList<Integer>();		
		if(subject.equals("") || subject==null || subject.isEmpty()){  			return null;		}
		else{
			if (subject.contains("[") && subject.contains("]")){
				remove = subject.substring(subject.indexOf("["),subject.indexOf("]")+1); 				subject = subject.replace(remove, "");
			}
			subject = cleanTerm(subject).toLowerCase(); System.out.println("subject: " + subject);
		}
		//System.out.println(" amid:"+mid + " pep: "+pepNumber);
		//for each pep titles in first stage,compare with the subject of the message 
		for (Integer key : uniquePropsals)  {	//get unique peps in db
			//newTitle=true;
			String firstTitle = multiMapFirstTitle.get(key).toString().toLowerCase();		firstTitle = cleanTerm(firstTitle);		
			String finalTitle = multiMapFinalTitle.get(key).toString().toLowerCase();		finalTitle = cleanTerm(finalTitle);
			//System.out.println(" b " + allTitlesInFirstState);			
			titleMatched=false;
			//System.out.println(" b");
			String tempSubject="",tempfirstTitle="",tempfinalTitle="";
			//1.First do a exact match of the proposal first and final title anywhere in the subject line 
			if(exactMatch) {
				//QUESTION...what if the term is just one term, it could match in so many subejcts??? 
				System.out.println("Doing Exact Matching, Checking with Proposal "+key+" firsttitle: " + firstTitle + " finalTitle: "+finalTitle + " subject: " + subject);
				matchFirstAndFinalTerm_AddToList(subject, key, firstTitle, finalTitle,mid,"exact");
				
				//do same but this time removing punctuation, 
				//for punctuation, replace every punctuation with double space and after the above processing, while string contains double space,  replace double spaces with single space
				tempSubject = replacePunctuationCorrectly(subject);	System.out.println("temp subject: " + tempSubject);
				if(backPad(tempSubject).contains(backPad(firstTitle)) || tempSubject.contains(backPad(finalTitle))){
					proposalsMatched.add(key);	//add to list of proposals matched
					System.out.println("\tExact match of first or final proposal title, after removing punctuations in subject line("+tempSubject+")");
				}
				
				matchFirstAndFinalTerm_AddToList(tempSubject, key, firstTitle, finalTitle,mid,"tempsubject");
				
				//this time remove punctuation from first an final title
				//Method B - exact match, but this time replace punctuations in all the proposal tiles in the list also 
				tempfirstTitle = replacePunctuationCorrectly(firstTitle);		//remove all the different punctuations first 
				tempfinalTitle = replacePunctuationCorrectly(finalTitle);		//remove all the different punctuations first 
				if(backPad(tempSubject).contains(backPad(tempfirstTitle)) || tempSubject.contains(backPad(tempfinalTitle))){
					proposalsMatched.add(key);	//add to list of proposals matched
					System.out.println("\tExact match of first or final proposal title, after removing punctuations in both, teh first and final proposal titles and subject line ("+tempSubject+")");
				}
				matchFirstAndFinalTerm_AddToList(tempSubject, key, tempfirstTitle, tempfinalTitle,mid,"tempTitles");
			}
			
			//2. Using levenshtein distances at different offsets within the message subject
			//basically we apply LD to same terms in above section, rather than exact match 
			//We dont pad in Levestein distance matching as stemmed terms are okay, but we do remove punctuation
			if(levenshteinDistanceMatch) {
				System.out.println("Levenshtein Distance Matching, Checking with Proposal "+key+" firsttitle: " + firstTitle + " finalTitle: "+finalTitle + " subject: " + subject);
				subset="";						
				//for small terms, just match the first numbers of terms equal to the length of the firsttitle
				//do the same with final title
				String[] titles = {tempfirstTitle,tempfinalTitle};//firstTitle,finalTitle};
				Integer x;
				x = 0;
				for (String t: titles) {					
					if((t == null || t.isEmpty() || t.equals("") || t.length() == 0) || t.split(" ").length <=1 ) { continue; }
					
					Integer lengthofTitle = t.length(),lengthofSubject = tempSubject.length(); //,lengthofFinalTitle = finalTitle.length(),lengthofSubject = subject.length();									
					if(lengthofTitle <11 && lengthofSubject > 10){	//for small terms, matching in longer subjects						
						subset = tempSubject.substring(0, lengthofTitle).trim();					
						//new way do direct matching 
						if(t.equals(subset) && (t.charAt(0)==subset.charAt(0))){  //match first letter from both strings
							titleMatched=true;		System.out.println("Levenshtein distances of title matched, after removing punctuations. Title: "+ t+ " in Subject: "+ tempSubject);
							if(debugMode) {
								if(x==0)	insertMatchedTitlesForDebug(key, t,"",subset,"levenshteinDistanceMatch, "+"smallTerms",mid);
								else insertMatchedTitlesForDebug(key, t,"",subset,"levenshteinDistanceMatch, smallTerms",mid);
							}
							if(insertIntoMainTableMode)
								updateTableWithPEPNumber(mid,key);
							proposalsMatched.add(key);	//add to list of proposals matched
						}						
					}					
					else{	//for bigger than 11 length terms		
//							System.out.println("levenshteinDistanceMatch, Checking with Proposal "+key+" firsttitle: " + firstTitle + " finalTitle: "+finalTitle + " tempSubject: " + tempSubject);
						//take the title and compare at different offsets in the subject line
						for (int k=0; k< lengthofSubject- lengthofTitle;++k){
							subset = tempSubject.substring(k, k+lengthofTitle).trim();
							//for firstpeptitle match
							levenshtein_distance = levenshtein.distance(t, subset);					
							if(levenshtein_distance <= Levenshtein_distance_threshold ){	//OLD WAY if (subject.toLowerCase().contains(allTitlesInFirstState.toLowerCase())){
								// h finalmatched, firstTitle: (python 3000) subject: (python 3 0 unicode and os environ) subset (python 3 0) levenshtein_distance_final 2.0
								//in the above case python 3000 and python 3 0 give ld = 2 which matches
								//therefore within the terms which have been matched, we try and match the numbers directly
								if (numbersInTermsMatch(t, subset)  && (t.charAt(0)==subset.charAt(0)) ){	 //match first letter from both strings
									titleMatched=true;		System.out.println("Levenshtein distances of title matched(else part), after removing punctuations. Title: "+ t+ " in Subject: "+ subset);
									if(debugMode) {
										if (x==0) insertMatchedTitlesForDebug(key, backPad(firstTitle),"",subset,"levenshteinDistanceMatch, biggerTerms",mid);
										else insertMatchedTitlesForDebug(key, "",t,subset,"levenshteinDistanceMatch, "+"biggerTerms",mid);
									}
									if(insertIntoMainTableMode)
										updateTableWithPEPNumber(mid,key);
									proposalsMatched.add(key);
									break; //we don't want to keep matching again and again the same title in the same subject
								}
							}
							//newTitle=false;	//while within this loop, set this variable to indicate we are within the same title
						} //end for loop
					}
					x++;
				}
				
			}
			
			//3. Find intersection - number of terms - in any order = Not ideal
			//in the following we find how many individual terms match in both, it will remove all the different punctuations first - http://stackoverflow.com/questions/14219084/comparing-two-strings-in-java-and-identifying-duplicate-words									
			/*
			 * Integer termFCounter=0;		//for all terms in sentence
			Splitter splitter = Splitter.onPattern("\\W").trimResults().omitEmptyStrings();
			Set<String> intersection = Sets.intersection(//
					Sets.newHashSet(splitter.split(proposalTitle)), //
					Sets.newHashSet(splitter.split(line)));
			termFCounter = intersection.size();					
			allproposalTitleTermsMatched = intersection.size() == OriginalproposalTitle.split(" ").length;
										
			//Method D Percentage match = Never ideal
			//  newest code does direct match so not needed
			float diff = 100* ( (float)termFCounter/ (float) proposalTitle.split(" ").length);				
			//return if 75% similar  or 3 or more terms matched
			boolean percentage = diff > (float) 99;		//check if all matched, 50	
			boolean iftermFCounter = termFCounter>=2;
			*/
			//test see if stopwords were matched 	
			//test see if stopwords were matched to give number = 2
			//stopwordsMatched =
			/*  not needed for direct matches
				Integer stopWordsmatched = getNumberOfStopWordsMatched(intersection);	
				if (stopWordsmatched>0)
					termFCounter=0;		//just assign is to 0 so it doesnt match in calculations
				
				//if matched by percentage (80 %) or term counter (>=3)
				if ( allproposalTitleTermsMatched) { //percentage ){		// || iftermFCounter
	//**					System.out.println("\nNew subject line processed, line: ("+line+") proposalTitle (" +proposalTitle+ ") MATCHED percentage " + percentage + " iftermFCounter "+ iftermFCounter );
//					System.out.println(" matched percentage " + percentage +" termFoundCounter " + termFoundCounter + " iftermFCounter " + iftermFCounter  + " title, " + proposalTitle + " sentence " +sentence);
					proposalTitleWithClosestMatch = OriginalproposalTitle;
					//break;
					System.out.println(" proposalTitleWithClosestMatch " + proposalTitleWithClosestMatch);
					return proposalTitleWithClosestMatch;
					/* newest code does direct match so not needed
					//now try and find the closest match proposal title
					if (termFCounter > maxTermCounter){
						proposalTitleWithClosestMatch = proposalTitle;
	//**						System.out.println("\tOveratken max " +proposalTitle + " maxTermCounter " + maxTermCounter);
					}
					
				}
				else{
					//proposalTitleWithClosestMatch = null;
					//System.out.println("\nNo proposaltitle matched in subject "); 
				}
				*/	
			//store in db..just for reference i guess.
//				for (Integer proposal: proposalsMatched) {
//					insertAdditionalTitle(proposal, firstTitle,finalTitle,subject, conn);
//				}
		}
		return proposalsMatched;
	}
	
	public static void insertMatchedTitlesForDebug(Integer pep, String firsttitle, String finaltitle,String subject, String matchedLocation, Integer messageID){
		try
	    {
	      String query = " insert into "+debugTable+" (proposal, firsttitle, finaltitle, subject,matchedLocation, messageID) values (?,?,?,?,?,?)";
	      // create the mysql insert preparedstatement
	      java.sql.PreparedStatement preparedStmt = conn.prepareStatement(query);
	      preparedStmt.setInt (1, pep);	      preparedStmt.setString (2, firsttitle);	preparedStmt.setString (3, finaltitle);		preparedStmt.setString (4, subject);  
	      preparedStmt.setString (5, matchedLocation); preparedStmt.setInt (6, messageID); preparedStmt.execute();
	    }
	    catch (Exception e)  {
	      System.err.println("Got an exception 67!");	      System.err.println(e.getMessage());	System.out.println(StackTraceToString(e)  );
	    }		
	}
	
	private static String replacePunctuationCorrectly(String str) {
		if (str.contains(".")) {
			str = str.replaceAll("\\.", "");  //System.out.println(" str a: " +str); 
		}
		//we remove numbers when matching
		if(removeNumbersAndPunctuation)
			str = str.replaceAll("[^a-zA-Z ]", " ").toLowerCase();	//System.out.println(" str b: " +str);		//remove all the different punctuations first 
		while(str.contains("  "))			//replace double spaces with single space
			str = str.replaceAll("  ", " ");
		str = str.toLowerCase().trim();  //System.out.println(" str c: " +str);
		return str;
	}
	
	public static String backPad(String text) { return text+" "; }
	
	public static void matchFirstAndFinalTerm_AddToList(String subject, Integer key, String firstTitle, String finalTitle, Integer messageID, String locationMatched) throws SQLException {
		if(firstTitle== null || firstTitle.isEmpty() || firstTitle.equals("") || firstTitle.length()==0 || firstTitle.split(" ").length <=1 ) {}
		else { 			
			//we pad all strings with a single space at teh back to make sure no stem terms matched  proposal title: "new io" should not match subject line: "new ioman" 
			if(backPad(subject).contains(backPad(firstTitle))) { 						
				proposalsMatched.add(key);	System.out.println("MessageID: " +messageID+ " Exact match of first proposal title: " + backPad(firstTitle) + " in subject: " +backPad(subject) ); //add to list of proposals matched
				if(debugMode)
					insertMatchedTitlesForDebug(key, backPad(firstTitle),"",backPad(subject),"Exact match of first proposal title, "+locationMatched,messageID);
				if(insertIntoMainTableMode)
					updateTableWithPEPNumber(messageID,key);
			}
		}
		if(finalTitle== null || finalTitle.isEmpty() || finalTitle.equals("") || finalTitle.length()==0 || finalTitle.split(" ").length <=1) { }
		else {	//System.out.println("here B,backPad(subject) "+ backPad(subject) + " backPad(finalTitle): "+ backPad(finalTitle));	
			if(backPad(subject).contains(backPad(finalTitle))) {
				proposalsMatched.add(key);	System.out.println("MessageID: " +messageID+ " Exact match of final proposal title: " + backPad(finalTitle) + " in subject: " +backPad(subject)); //add to list of proposals matched	
				if(debugMode)
					insertMatchedTitlesForDebug(key, "",backPad(finalTitle),backPad(subject),"Exact match of final proposal title, "+locationMatched,messageID);
				if(insertIntoMainTableMode)
					updateTableWithPEPNumber(messageID,key);
			}
		}
	}
	
	public static Boolean numbersInTermsMatch(String finalTitle, String subset){
		//extrct numbers/doubles from string
		//and match them
		String numberOnlyFinalTitle= finalTitle.replaceAll("[^0-9]", "");
		String numberOnlySubset= subset.replaceAll("[^0-9]", "");
//**		System.out.println("extracted numbers: " + numberOnlyFinalTitle + " " + numberOnlySubset);
		if (numberOnlyFinalTitle.equals(numberOnlySubset))
			return true;
		else
			return false;
	}
	
	private static String cleanTerm(String term) {
		if (term.contains("["))			term = term.replaceAll("\\[", "").trim();
		if (term.contains("]"))			term = term.replaceAll("\\]", "").trim();
		
		if (term.contains("'"))			term = term.replaceAll("'", "").trim();
		if (term.contains(","))			term = term.replaceAll(",", "").trim();
		if (term.contains("`"))			term = term.replaceAll("`", "").trim();
		if (term.contains(".")){
			Integer loc=0;
			for (char ch: term.toCharArray()) {
				if (ch=='.'){
//**					System.out.println(" here a, term (" +term +")  loc "+loc); 
				//term
					//get chars before and after space, if they are numbers, do direct match
					//boolean numberBefore = Character.isDigit(term.charAt(term.indexOf(".") - 1));
					//boolean numberAfter =  Character.isDigit(term.charAt(term.indexOf(".") + 1));
					boolean numberBefore = Character.isDigit(loc - 1);
					boolean numberAfter =  Character.isDigit(loc + 1);
					if (numberBefore && numberAfter){
						//do nothing
					}
					else{
						//term = term.replace(".", " ");
						StringBuilder myTerm = new StringBuilder(term);
						myTerm.setCharAt(loc, ' ');
					}
				}
				loc++;
			}
		}
		if (term.contains("-"))			term = term.replace("-", " ");
		if (term.contains("_"))			term = term.replace("_", " ");
		if (term.contains("\""))		term = term.replace("\"", "");
		if (term.contains("\\"))		term = term.replace("\\", "");
		if (term.contains("/"))			term = term.replace("/", "");
		if (term.contains("//"))		term = term.replace("//", "");
		if (term.contains(":"))			term = term.replace(":", "");
//		if (term.contains("(") && term.contains(")")){
//			term = term.replaceAll("(", " ");
//			term = term.replaceAll(")", " ");
//		}
		
		term = removeDoubleSpaces(term);
		term= term.trim().toLowerCase();
		return term;
	}
	
	public static String removeDoubleSpaces(String term) {
		//for (int i=0;i<5;i++){
		if(term.contains("    ")) 		term = term.replaceAll("    ", " ");
		if(term.contains("   "))		term = term.replaceAll("   ", " ");
		if(term.contains("  "))			term = term.replaceAll("  ", " ");
		//}
		return term.trim();		
	}
	
	// before running if theya rfe the same
	  //select * from allmessages_dev where pep <> originalPEPNumber;
	//eventually we would update the senderName column in the allmesaages table
	private static void updateTableWithPEPNumber(Integer mid, Integer pep)		throws SQLException {
		  String updateQuery = "update "+tablenameToUpdate+" set pep = ? where messageID = ?";
		  PreparedStatement preparedStmt = conn.prepareStatement(updateQuery);
		  preparedStmt.setInt (1, pep);		  preparedStmt.setInt(2, mid);
		  Integer i = preparedStmt.executeUpdate();
		  if(i>0)   {
		          System.out.println("success");
		  }
		  else  {
		         System.out.println("stuck somewhere mid " + mid + " NewPEP " + pep);
		    }
		  updateQuery =null;
	}
	
	
	private static void removeDuplicatesOrderDisplayList(ArrayList<String> verbList, String t) {
		Set<String> hs = new HashSet<>();		hs.addAll(verbList);
		verbList.clear();		verbList.addAll(hs);
		verbList.sort(String::compareToIgnoreCase);		
		for(String v: verbList) {
			System.out.printf("%5s%50s", t,v);			System.out.println();
		}
	}
	//we want to display the extracted rows in extracted table for the provided term from source table 
	private static void displayDistinctLabelsForTerm(String t, String mainField,String secondField, String tablename) throws SQLException {
		//String sql = "SELECT DISTINCT arg1, relation, arg2 from "+tablename+" where arg1 LIKE '%" +t+ "%' order by arg1, relation, arg2";	 
		String sql = "SELECT "+mainField+", relation, "+secondField+", count(*) as count " + 
					 "from  " + tablename + " " +
					 "where "+mainField+" LIKE '%" + t + "%' " + 
					 "group by "+mainField+", relation, "+secondField+" " + 
					 "order by "+mainField+" asc, count desc ";//"order by arg1, arg2,relation ";                //
		Statement stmt = conn.createStatement();	ResultSet rs = stmt.executeQuery( sql );	//stmt.setFetchSize(500);					 
		Integer counter=0,count=0;		String sub="",rel="",obj="";
		while (rs.next())	{   //check every message 
			//just give the computer some rest
			//Thread.sleep(500);                 //1000 milliseconds is one second.
			//TimeUnit.SECONDS.sleep(30); 
			counter++;
			if(counter%500 == 0)
				System.out.println(counter);
			sub = rs.getString(mainField).toLowerCase();			rel = rs.getString("relation").trim().replaceAll(" +", " ").toLowerCase();			obj = rs.getString(secondField).toLowerCase();
			count = rs.getInt("count");
			System.out.printf("%15s%25s%25s%5d", sub,rel,obj,count);			System.out.println();
		}
	}
	
	//NOT SURE IF This function is useful
	private static void readStopWordsFile(List<String> lines) throws FileNotFoundException, IOException {
		BufferedReader reader;
		reader = new BufferedReader(new FileReader("c:\\scripts\\stopwords2.txt"));
		String line = null;
		while ((line = reader.readLine()) != null) {
		    lines.add(line);
		    System.out.println("line: " + line);
		} 
		reader.close();
		Object[] array = lines.toArray();
		
		//display
		for (Object myArray : array) {
			   System.out.println(myArray);
			}
	}
	
	//For a term passed, this functions returns all relations and the opposite term - subject or object, based on the arg passed, 
	//removing the need to have to separate functions
	//at the moment we just store the S,V,O without any more processing which will be done later on
	//when storing we put wherever the term passed as parameter is found, arg1 or arg2, as arg1 in table
	//dec 2018, we select only distinct rows		//method type ArrayList<String>				//ArrayList<String> verbList
	private static void  extractCandidateLabelTriplesForTermAsSubject_InsertInDB(String verb, String term, String field,String field2, String labelextraction_tablename)  {
		String subject,relation,object="";	
		try {
			//String sql = "SELECT id,arg1, relation, arg2 from "+extractedRelationsTableName+" where "+field+ " LIKE '%" +term+ "%'";// or arg2 LIKE '%" +term+ "%';";
			String sql = "SELECT distinct arg1, relation, arg2 from "+extractedRelationsTableName+" where "+field+ " LIKE '%" +term+ "%' "
					   + " and ("+field2+" LIKE '%"+verb+"%' OR relation LIKE '%"+verb+"%') ";// or arg2 LIKE '%" +term+ "%';";
			Statement stmt = conn.createStatement();	ResultSet rs = stmt.executeQuery( sql );				//stmt.setFetchSize(500);			 
			Integer counter=0;		
			while (rs.next()) {	   //check every triple in table
				counter++;
				if(counter%500 == 0)
					System.out.println(counter);
				subject = rs.getString("arg1").toLowerCase();	relation = rs.getString("relation").trim().replaceAll(" +", " ").toLowerCase();	 object = rs.getString("arg2").toLowerCase();
				subject = subject.trim().replaceAll(" +", " ");	//remove all empty spaces like double spaces	
				//dec 2018
				System.out.printf("%25s%25s%25s", subject,relation,object);			System.out.println();
				
				String relationToInsert ="",arg2ToInsert="";
				//here we want to find the first verb in relation and first noun in arg2
				//each of the verbs you find, you want to feed that into the system again and it should pull out all the triples for that 
				//using teh new nouns found i guess keep on going one level down
				//System.out.println("Sending for Matching Doubles/Triples Result at Server: (" + subject+")");
				/*
				if(returnNumberOfTerms(relation) ==1 )	{///if there is only one term extracted while triple extraction
						relationToInsert=relation;
				}
				else {
					String rel="";
					//Extract first verb from the extracted relation 
					//	old way using socket
					scnlp.dos.writeUTF("T"+relation);					String output = scnlp.dis.readUTF();				
					//new way
					Boolean showGraphs=false;
					//String text = "Propose rejection of PEP";
					String triple= prp.getStanfordNLP().matchDoublesTripleInSentence(subject,showGraphs);			
					//System.out.println(" coReferencedSentence sent: [" + strToSend + "] Received [" +coReferencedSentence +"]");
					
					try {
						String filename= "c:\\scripts\\snlp\\matchDoublesTripleInSentence.txt";
					    FileWriter fw = new FileWriter(filename,true); //the true will append the new data
					    fw.write("PEP " + prp.getPep() + " | "+ subject + " | " + triple ); //+ " | " + coReferencedSentence);//appends the string to the file
					    fw.write("\r\n");					    fw.close();
					}
					catch(IOException ioe)					{					    System.err.println("IOException: " + ioe.getMessage());					}
										
					String[] svo = output.split(",");
					if(svo.length <=1) {					}
					else if(svo.length ==2) {
						rel = svo[1];
						if(rel==null || rel.isEmpty() || rel.equals("") || rel.length()<1){
							//dont insert if relation is null							
						}
						else {							relationToInsert = rel;						}
					}
				}
				
				if(returnNumberOfTerms(object)   ==1 )	{///if there is only one term found
					arg2ToInsert=object;
				}
				else {
					String arg2="";
					//extract first noun from the extracted object
					scnlp.dos.writeUTF("T"+object);					String output2 = scnlp.dis.readUTF();					String[] svo2 = output2.split(",");
					if(svo2.length <=1) {					}					
					else if (svo2.length ==2 || svo2.length ==3){
						arg2= svo2[0];
						if(arg2==null || arg2.isEmpty() || arg2.equals("") || arg2.length()<1) {
							arg2="";
						}						
						else {							arg2ToInsert = arg2;						}	
					}
				}
				*/
				if (field.equals("arg1")) {
				//	irt.insert(term, relationToInsert, arg2ToInsert, "", labelextraction_tablename,conn);
					irt.insert(subject, relation, object, "", labelextraction_tablename,conn);
				}
				else {
				//	irt.insert(term, relationToInsert, arg2ToInsert, "", labelextraction_tablename,conn);
					irt.insert(object, relationToInsert, subject, "", labelextraction_tablename,conn);
				}
			}			
		}
		catch( SQLException sqlEx) {			System.out.println(StackTraceToString(sqlEx));		}   
		//catch( IOException ioe) {
		//	System.out.println(StackTraceToString(ioe));
		//}	
		catch(Exception ex) {			System.out.println(StackTraceToString(ex));		}
		//return verbList;
	}
	
	private static Integer returnNumberOfTerms(String relation) {
		String trim = relation.trim();
		if (trim.isEmpty())
		    return 0;
		return trim.split("\\s+").length;
	}	
	
	public static String StackTraceToString(Exception ex) {
		String result = ex.toString() + "\n";
		StackTraceElement[] trace = ex.getStackTrace();
		for (int i=0;i<trace.length;i++) {
			result += trace[i].toString() + "\n";
		}
		return result;
	}
	
	private static void remoreStopwords(Connection conn) throws SQLException, InterruptedException {
		String subject,relation,object="";
	       String sql = "SELECT id,arg1, relation, arg2 from "+extractedRelationsTableName+"  order by messageID;"; 	 //relations_clausie
	       Statement stmt = conn.createStatement();		   ResultSet rs = stmt.executeQuery( sql );	 
		   Instant start = Instant.now();
		   while (rs.next()) {	   //check every message  	   
	    		//just give the computer some rest
				    Thread.sleep(500);                 //1000 milliseconds is one second.
				    //TimeUnit.SECONDS.sleep(30);
	       		subject = rs.getString("subject");	       		relation = rs.getString("relation");	       		object = rs.getString("object");	       		
	       		subject = removeStopwords(subject);	       		relation = removeStopwords(relation);	       		object = removeStopwords(object);
	       		//updateTriples(subject,relation,object);
		   }
	}
	
	public static String removeStopwords(String pos) {
//		for(String st: stopwords) {
//			if(pos.contains(st)){
//				pos= pos.replace(st, "");
//			}
//		}
		return null;
	}
	
	

}
