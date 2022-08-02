package readRepository.MessageSubjectProposalTitleMatching;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import connections.MysqlConnect;
import javaStringSimilarity.info.debatty.java.stringsimilarity.Levenshtein;
import miner.process.ProcessingRequiredParameters;
import miner.process.PythonSpecificMessageProcessing;
import utilities.PepUtils;
import utilities.ReturnNattyTimeStamp;

import com.google.common.base.Splitter;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

//import PeopleAspectAnalysis.SeparateStudyGroup2Functions;
//import PeopleAspectAnalysis.MysqlConnectForQueries;
import readRepository.readRepository.MessageReadingUtils;

//Jan 2019, this script is for JEps, we created a separate script because jeps dont have firsta dn last title

//june 2018, This script has been extended to check in all messages for all cases 
//1 - first and last title match (original purpose) - Some proposals change titles during teh process, this script, this script matches message subject for messages with -1, (unallocated messages) with first and final pep title
//2 - match message subject with all proposal titles using: 
//							exact match, 
//							matching after removing punctuation from both
//							(this reason was pep 308 acceptance message. no pep number mentioned in message but "conditional expressions" was mentioned in subject line)
//3. matching using levestian distance

//After all this, for test purposes, all these extra messages which are matched are also inserted in a db table
//not just those with -1, as there can be ??? not really but do check 


//When to use. 
//1. after reading all messages in - is part of post message update script, but can be script on its own for other purposes
//manually run this sql - " update allmessages set originalPEPNumber = pep;"
//2. at the time of reading messages in...exact matches are part of the reading in script at the moment but using levenstein distance will be implemented after testing ,,seeing results in db table
//3. when checking if message is for a pep in explore analyse repository gui

//Before running just check-  select * from allmessages_dev where pep <> originalPEPNumber; to make sure these two fields have same value,as the originalPEPNumber is a backup field for the original value

//july 2018, its not completely correct, but final version is very correct
//After running this version, I can say the script is working and is finalised. Just have to change if we want to include lists mailing list of every other....
//without including lists ml, its assigns pep number to most of the critical messages..90210 atleast 

public class MatchProposalTitlesToProposalNumbersJEPs {
	//for cases when no pep number exists in message
	//we use the  pep tile to match
	//assign pep numbers to rows which dont have based on subject if they match the pep titles
	static MessageReadingUtils mru = new MessageReadingUtils();
	static PythonSpecificMessageProcessing pms = new PythonSpecificMessageProcessing();	
	static ProcessingRequiredParameters prp = new ProcessingRequiredParameters();
	//static SeparateStudyGroup2Functions f = new SeparateStudyGroup2Functions();	
	static MysqlConnect mc = new MysqlConnect();
	static Connection conn;
	static String updateQuery;
	static int i;
	static String tablenameToCheck = "allmessages";		//tablename to check...only ideas and dev and maybe try checkins
	static String tablenameToUpdate = "allmessages";
	static String debugTable = "proposaldetails_titlesforeachpep_debug";
	static String tableWithProposalTitle = "jepdetails";	//table to get first and last proposal titles
	static Levenshtein levenshtein = new Levenshtein();	// Levenshtein
	//here we increase and decrease Levenshtein_distance_threshold and see which is best value
	static Integer Levenshtein_distance_threshold = 3,Levenshtein_distance_threshold_small_terms = 2;
	static boolean toUpdate;
	static PepUtils pu = new PepUtils();
	static ArrayList<Integer> uniquePropsals = new ArrayList<Integer>();
	static Multimap<Integer, String> multiMapFirstTitle,multiMapFinalTitle;
	//nov 2017 - can this not be part of the main repository reading script?
	static List<Integer> proposalsMatched = new ArrayList<Integer>();		//list of all the proposal numbers matched in each message, should be emptied after checking each message
	//IMPORTANT SET THESE, just see or update the 'allmessages' table
	static boolean debugMode= true, insertIntoMainTableMode=true; //if we want to be able to restart, we should keep both true so we can use the debug table for tracking
	static String proposalIdentifier = "pep";
	
	public static void init() {		
		try {
			uniquePropsals = pu.returnUniqueProposalsInDatabase(proposalIdentifier);
			if(uniquePropsals.size()<=1)
				System.out.println("No proposal Titles found in Table");
			else {
				// create multimap to store key and values
		        multiMapFirstTitle = ArrayListMultimap.create();	       
		        multiMapFinalTitle = ArrayListMultimap.create();
				populateMapWithJEPTitles(multiMapFirstTitle,multiMapFinalTitle);
			}
		} catch (Exception e) {			e.printStackTrace();		}
	}
	
	public static void main(String args[])
	{
		String subject="",titleinfirststate="",subset="",sql,sql0; //,newName = null, bdflDelegate, newBDFLDelegate;
		Integer mid = null;
		int jepNumber,pep;
		double levenshtein_distance,levenshtein_distance_final;
		Statement stmt,stmt0;
		ResultSet rs,rs0;
		boolean firstMatched = false, finalMatched=false,database = true, test = false, exactMatch=true,levenshteinDistanceMatch=true;
				
		try	{
			conn = mc.connect();	
			init();
			//f.totalNumberOfMessages(conn);		//modify this to include tablename
			//f.totalNumberOfUniqueMessages(conn);	//modify this to include tablename
			/*
			// initialise proposal numbers, in proposalNumberSearchKeyList and ignoreList
			initproposalNumberSearchKeyLists(ignoreList, proposalNumberSearchKeyList);
			//show all patterns being used to match
			for (String proposal : proposalNumberSearchKeyList) {
				//System.out.println(proposal);
			}
			
			// initialise proposalTitles
			proposalTitlesSearchKeyList.clear();
			
			// map to store proposal titles and numbers, WE Have to pre-poipulate this if we want to use this feature
			Map<Integer, String> proposalDetails = populateAllproposalTitleandNumbers(new HashMap<Integer, String>(), conn);
			System.out.println("Show all proposal titles");
			for (Map.Entry<Integer, String> entry : proposalDetails.entrySet()) {				
				proposalTitlesSearchKeyList.add(entry.getValue());
			    System.out.println(entry.getKey() + "/" + entry.getValue());//show all proposal titles
			}
			*/
	        //perform processing on text, just to test  and evaluate instances
	        if (test){
	        	mid= -2;	//dummy values
	        	pep = -2;	//dummy values
	        	//Multimap<Integer, String> multiMapFirstTitleText = ArrayListMultimap.create();
		        //Multimap<Integer, String> multiMapFinalTitleText = ArrayListMultimap.create();
	        	multiMapFirstTitle.clear();multiMapFinalTitle.clear();
	        	multiMapFirstTitle.put(pep, "python 3.0 plans");
	        	multiMapFinalTitle.put(pep, "python 3000");
	        	subject="python 3.0 unicode and os environ";
	        	Set<Integer> keys = multiMapFirstTitle.keySet();
	        	proposalsMatched = matchFirstAndFinalTitlesWithSubject(pep,subject, mid,exactMatch,levenshteinDistanceMatch, conn); //end for loop  multiMapFirstTitleText, multiMapFinalTitleText,
	        }
	        
	        //perform on database
	        if(database){	
		        // get all the set of keys - keys for both maps should be same
	        	
	        	//PreparedStatement stat = c.prepareStatement("SELECT * FROM big_table",ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY); 
	        	//stat.setFetchSize(Integer.MIN_VALUE);	        	ResultSet results = stat.executeQuery();	
	        	
	        	//Do this in chunks as there are many rows with -1
	        	//update allmessages set originaljepnumber = jep;
	        	Integer maxMid = prp.getPd().getMaxProcessedMessageIDFromDebugTable(conn,0, prp); 
	        	ArrayList<String> uniqueJEPsML = new ArrayList<>();
	        	uniqueJEPsML = pu.returnUniqueJEPsMLInDatabase();
	        	Integer count=0;
	        	for (String ml : uniqueJEPsML) { //System.out.println("Selecting from Mailing List: " + ml);
	        		count=0;
			        Set<Integer> keys = multiMapFirstTitle.keySet();	
			        Integer lastIndex = ml.lastIndexOf("\\"); 
			        String last = ml.substring(lastIndex+1,ml.length()).trim();   //System.out.println("\tMailing List: " + last);
					sql0 = " SELECT messageid, jep, subject from "+tablenameToCheck    //messageid = 90910";
						 + " WHERE jep = -1 and subject IS NOT NULL and lastdir = '"+last+"' "
						 + " and messageid > " +maxMid
						 + " order by messageid;";  // and folder NOT like '%lists%';"; //LIMIT 5000 //order by pep asc LIMIT 5000  ..or folder like '%idea%'
					stmt0 = conn.createStatement();				rs0 = stmt0.executeQuery( sql0 );
					
					Integer totalMessagesToProcessForProposal = prp.mc.returnRSCount(sql0, conn);
					System.out.println("Mailing List: " + last + " Number of messages: " + totalMessagesToProcessForProposal);
					
					//for each message
					while (rs0.next()) {
						proposalsMatched.clear();	count++;
						mid = rs0.getInt(1);					jepNumber = rs0.getInt(2);					subject = rs0.getString(3);	
						if (subject.trim().toLowerCase().startsWith("re ")){
							subject = subject.replace("re", "").trim(); subject = subject.replace("fw", "").trim();	subject = subject.replace("fwd", "").trim();
						}
						//1. First and last proposal title match
						//we can match several proposals here from the list of proposal titles, so we insert for each
						matchFirstAndFinalTitlesWithSubject(jepNumber,subject, mid, exactMatch,levenshteinDistanceMatch, conn); //end for loop multiMapFirstTitle, multiMapFinalTitle,
						List<Integer> listWithoutDuplicates = new ArrayList<>(new HashSet<>(proposalsMatched));
						//store		..ideally there should just be one proposal matched, not multiple
		//				for (Integer p: listWithoutDuplicates) {
		//					updateTableWithPEPNumber(conn, mid, p);
		//				}
//						System.out.println("\tMailing List ("+last+") Processed Counter: " + count);
					}	//end while loop
	        	}
		    }
			System.out.println("Processing Finished");
		}   catch (SQLException se)	    {
			System.err.println("Got an sql exception! ");			System.err.println(se.getMessage() + " mid " + mid);
		}   catch (Exception e)	    {
			System.err.println("Got an exception 877! ");			System.err.println(e.getMessage() + " mid " + mid); System.out.println(StackTraceToString(e)  );
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

	private static void populateMapWithJEPTitles(Multimap<Integer, String> multiMapFirstTitle, Multimap<Integer, String> multiMapFinalTitle)	throws SQLException {
		String titleinfirststate,finalTitle, temp, sql;
		int jep;			Statement stmt;		ResultSet rs;
		// put values into map for A	        
		sql = "SELECT jep, title from "+tableWithProposalTitle+" where jep IS NOT NULL order by jep asc ";  //where pep != -1 order by pep asc LIMIT 5000
		stmt = conn.createStatement();		rs = stmt.executeQuery( sql );	
		while (rs.next()) {
			jep = rs.getInt(1);			temp = rs.getString(2);
			finalTitle = titleinfirststate = temp; //rs.getString(3);
			//System.out.println("pep : "+ pep +" titleinfirststate " +titleinfirststate);
			if (titleinfirststate != null){
				titleinfirststate = cleanTerm(titleinfirststate);	multiMapFirstTitle.put(jep, titleinfirststate);
				finalTitle = cleanTerm(finalTitle);					multiMapFinalTitle.put(jep, finalTitle);
//				System.out.println(pep +", 	  " +titleinfirststate+",    "+finalTitle);
			} 
		}
	}
		
	public static String backPad(String text) { return text+" "; }
	
	public static boolean matchFirstAndFinalTerm_AddToList(Integer proposal, String subject, Integer key, String firstTitle, String finalTitle, Integer messageID, String locationMatched) 
			throws SQLException {
		if(firstTitle== null || firstTitle.isEmpty() || firstTitle.equals("") || firstTitle.length()==0 || firstTitle.split(" ").length <=1 ) {}
		else { 			
			//we pad all strings with a single space at teh back to make sure no stem terms matched  proposal title: "new io" should not match subject line: "new ioman" 
			if(backPad(subject).contains(backPad(firstTitle))) { 
				String changedSubject = subject.replace(firstTitle, "JEP");	//jan to capture verbs on JEP we change the jep title with jep no
				proposalsMatched.add(key);	
				System.out.println("\tJEP, "+proposal+", New ("+key+") MessageID: " +messageID+ " Exact match of first proposal title: " + backPad(firstTitle) + " in subject: " +backPad(subject) ); //add to list of proposals matched
				if(debugMode)
					insertMatchedTitlesForDebug(proposal,key, backPad(firstTitle),"",backPad(subject),"Exact match of first proposal title, "+locationMatched,messageID,changedSubject );
				if(insertIntoMainTableMode)
					updateTableWithPEPNumber(messageID,key,changedSubject);			
				return true;
			}
		}
		if(finalTitle== null || finalTitle.isEmpty() || finalTitle.equals("") || finalTitle.length()==0 || finalTitle.split(" ").length <=1) { }
		else {	//System.out.println("here B,backPad(subject) "+ backPad(subject) + " backPad(finalTitle): "+ backPad(finalTitle));	
			if(backPad(subject).contains(backPad(finalTitle))) {
				String changedSubject = subject.replace(firstTitle, "JEP");	//jan to capture verbs on JEP we change the jep title with jep no
				proposalsMatched.add(key);	
				System.out.println("\tJEP, "+proposal+ ", New ("+key+") MessageID: " +messageID+ " Exact match of final proposal title: " + backPad(finalTitle) + " in subject: " +backPad(subject)); //add to list of proposals matched	
				if(debugMode)
					insertMatchedTitlesForDebug(proposal,key, "",backPad(finalTitle),backPad(subject),"Exact match of final proposal title, "+locationMatched,messageID,changedSubject);
				if(insertIntoMainTableMode)
					updateTableWithPEPNumber(messageID,key,changedSubject);
				return true;
			}
		}
		return false;
	}
	//match passed text with each different proposals first and last title
	public static List<Integer> matchFirstAndFinalTitlesWithSubject(Integer proposal,String subject, Integer mid,//Multimap<Integer,  String> multiMapFirstTitle, Multimap<Integer, String> multiMapFinalTitle,	
			boolean exactMatch,		//do we want to allow exact match
			boolean levenshteinDistanceMatch,		//do we want to allow levenshtein distances matches at different offsets within the message subject
			Connection conn) throws SQLException {
		double levenshtein_distance,levenshtein_distance_final;
		String subset,remove;
		boolean titleMatched;
		List<Integer> proposalsMatched = new ArrayList<Integer>();		
		if(subject.equals("") || subject==null || subject.isEmpty()){ 
			return null;
		}
		else{
			try {
				if (subject.contains("[") && subject.contains("]")){
					remove = subject.substring(subject.indexOf("["),subject.indexOf("]")+1);
					subject = subject.replace(remove, "");
				}
				subject = cleanTerm(subject).toLowerCase();
			}catch (Exception ex) {
				System.out.println(ex.toString());
			}
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
			boolean found= false;
			if(exactMatch) {
				//QUESTION...what if the term is just one term, it could match in so many subejcts??? 
				//System.out.println("Exact Matching, Checking with Proposal "+key+" firsttitle: " + firstTitle + " finalTitle: "+finalTitle + " subject: " + subject);
				found = matchFirstAndFinalTerm_AddToList(proposal, subject, key, firstTitle, finalTitle,mid,"exact");
				
				//do same but this time removing punctuation, 
				//for punctuation, replace every punctuation with double space and after the above processing, while string contains double space,  replace double spaces with single space
				tempSubject = replacePunctuationCorrectly(subject);
				//if(backPad(tempSubject).contains(backPad(firstTitle)) || tempSubject.contains(backPad(finalTitle))){
				//	proposalsMatched.add(key);	//add to list of proposals matched
				//	System.out.println("Exact match of first or final proposal title, after removing punctuations in subject line");
				//}
				if(!found)
					found=matchFirstAndFinalTerm_AddToList(proposal,tempSubject, key, firstTitle, finalTitle,mid,"tempsubject");
				
				//this time remove punctuation from first an final title
				//Method B - exact match, but this time replace punctuations in all the proposal tiles in the list also 
				tempfirstTitle = replacePunctuationCorrectly(firstTitle);		//remove all the different punctuations first 
				tempfinalTitle = replacePunctuationCorrectly(finalTitle);		//remove all the different punctuations first 
				//if(backPad(tempSubject).contains(backPad(tempfirstTitle)) || tempSubject.contains(backPad(tempfinalTitle))){
				//	proposalsMatched.add(key);	//add to list of proposals matched
				//	System.out.println("Exact match of first or final proposal title, after removing punctuations in both, teh first and final proposal titles and subject line");
				//}
				if(!found)
					matchFirstAndFinalTerm_AddToList(proposal, tempSubject, key, tempfirstTitle, tempfinalTitle,mid,"tempTitles");
			}
			
			//2. Using levenshtein distances at different offsets within the message subject
			//basically we apply LD to same terms in above section, rather than exact match 
			//We dont pad in Levestein distance matching as stemmed terms are okay, but we do remove punctuation
			if(levenshteinDistanceMatch && !found) {
				subset="";						
				//for small terms, just match the first numbers of terms equal to the length of the firsttitle
				//do the same with final title
				String[] titles = {tempfirstTitle}; //since there is only one title for JEPs, //tempfinalTitle};//firstTitle,finalTitle};
				Integer x;
				x = 0;
				for (String t: titles) {					
					if((t == null || t.isEmpty() || t.equals("") || t.length() == 0) || t.split(" ").length <=1 ) { continue; }
					
					Integer lengthofTitle = t.length(),lengthofSubject = tempSubject.length(); //,lengthofFinalTitle = finalTitle.length(),lengthofSubject = subject.length();									
					if(lengthofTitle <11 && lengthofSubject > 10){	//for small terms, matching in longer subjects						
						subset = tempSubject.substring(0, lengthofTitle).trim();					
						//new way do direct matching 
						if(t.equals(subset) && (t.charAt(0)==subset.charAt(0))){  //match first letter from both strings
							String changedSubject = subject.replace(subset, "JEP");	//jan 2019 to capture verbs on JEP we change the jep title with jep no
							titleMatched=true;		
							System.out.println("\tJEP, "+proposal+", New ("+key+") MessageID: " +mid+ " Levenshtein distances of title matched, after removing punctuations. Title: "
									+ t+ " in Subject: "+ tempSubject);
							if(debugMode) {
								if(x==0)	insertMatchedTitlesForDebug(proposal, key, t,"",subset,"levenshteinDistanceMatch, "+"smallTerms",mid, changedSubject);
								else insertMatchedTitlesForDebug(proposal,key, t,"",subset,"levenshteinDistanceMatch, smallTerms",mid, changedSubject);
							}
							if(insertIntoMainTableMode)
								updateTableWithPEPNumber(mid,key,changedSubject);
							proposalsMatched.add(key);	//add to list of proposals matched
						}						
					}					
					else{	//for bigger than 11 length terms		
//						System.out.println("levenshteinDistanceMatch, Checking with Proposal "+key+" firsttitle: " + firstTitle + " finalTitle: "+finalTitle + " tempSubject: " + tempSubject);
						//take the title and compare at different offsets in the subject line
						for (int k=0; k< lengthofSubject- lengthofTitle;++k){
							subset = tempSubject.substring(k, k+lengthofTitle).trim();
							//for firstpeptitle match
							levenshtein_distance = levenshtein.distance(t, subset);		
							String changedSubject = subject.replace(subset, "JEP");	//jan 2019 to capture verbs on JEP we change the jep title with jep no
							if(levenshtein_distance <= Levenshtein_distance_threshold ){	//OLD WAY if (subject.toLowerCase().contains(allTitlesInFirstState.toLowerCase())){
								// h finalmatched, firstTitle: (python 3000) subject: (python 3 0 unicode and os environ) subset (python 3 0) levenshtein_distance_final 2.0
								//in the above case python 3000 and python 3 0 give ld = 2 which matches
								//therefore within the terms which have been matched, we try and match the numbers directly
								if (numbersInTermsMatch(t, subset)  && (t.charAt(0)==subset.charAt(0)) ){	 //match first letter from both strings
									titleMatched=true;		
									System.out.println("\tJEP, "+proposal+", New ("+key+")  MessageID: " +mid+ " Levenshtein distances of title matched(else part), after removing punctuations. Title: "+ t+ " in Subject: "+ subset);
									if(debugMode) {
										if (x==0) insertMatchedTitlesForDebug(proposal,key, backPad(firstTitle),"",subset,"levenshteinDistanceMatch, biggerTerms",mid, changedSubject);
										else insertMatchedTitlesForDebug(proposal,key, "",t,subset,"levenshteinDistanceMatch, "+"biggerTerms",mid,changedSubject);
									}
									if(insertIntoMainTableMode)
										updateTableWithPEPNumber(mid,key,changedSubject);
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
//				System.out.println(" matched percentage " + percentage +" termFoundCounter " + termFoundCounter + " iftermFCounter " + iftermFCounter  + " title, " + proposalTitle + " sentence " +sentence);
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
//			for (Integer proposal: proposalsMatched) {
//				insertAdditionalTitle(proposal, firstTitle,finalTitle,subject, conn);
//			}
		}
		return proposalsMatched;
	}
	
	private static String replacePunctuationCorrectly(String str) {
		if (str.contains("."))
			str = str.replaceAll("\\.", " ");
		str = str.replaceAll("[^a-zA-Z ]", " ").toLowerCase();			//remove all the different punctuations first 
		while(str.contains("  "))			//replace double spaces with single space
			str = str.replaceAll("  ", " ");
		str = str.toLowerCase().trim();
		return str;
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
	
	public boolean isNumeric(String s) {
	    return s != null && s.matches("[-+]?\\d*\\.?\\d+");  
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
	
	public static void insertMatchedTitlesForDebug(Integer origProposal, Integer pep, String firsttitle, String finaltitle,String subject, String matchedLocation, Integer messageID,
			String changedSubject){
		try
	    {
	      String query = " insert into "+debugTable+" (oldproposal, proposal, firsttitle, finaltitle, subject,matchedLocation, messageID, proposalSubject) values (?,?,?,?,?,?,?,?)";
	      // create the mysql insert preparedstatement
	      java.sql.PreparedStatement preparedStmt = conn.prepareStatement(query);
	      preparedStmt.setInt (1, origProposal);	 preparedStmt.setInt (2, pep);	      preparedStmt.setString (3, firsttitle);	preparedStmt.setString (4, finaltitle);		
	      preparedStmt.setString (5, subject);       preparedStmt.setString (6, matchedLocation); preparedStmt.setInt (7, messageID); 	preparedStmt.setString (8, changedSubject);
	      preparedStmt.execute();
	    }
	    catch (Exception e)  {
	      System.err.println("Got an exception!");	      System.err.println(e.getMessage());
	    }		
	}
	
	// before running if theya rfe the same
	  //select * from allmessages_dev where pep <> originalPEPNumber;
	//eventually we would update the senderName column in the allmesaages table
	private static void updateTableWithPEPNumber(Integer mid, Integer pep, String changedSubject)		throws SQLException {
		  updateQuery = "update "+tablenameToUpdate+" set jep = ?, proposalSubject=? where messageID = ? and jep = -1";
		  PreparedStatement preparedStmt = conn.prepareStatement(updateQuery);
		  preparedStmt.setInt (1, pep);		  preparedStmt.setString(2, changedSubject); preparedStmt.setInt(3, mid);
		  i = preparedStmt.executeUpdate();
		  if(i>0)   {
		         // System.out.println("success");
		  }
		  else  {
		         System.out.println("stuck somewhere mid " + mid + " NewPEP " + pep);
		    }
		  updateQuery =null;
	}
}
