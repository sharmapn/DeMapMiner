package readRepository.readRepositoryBitcoin;

import java.io.*;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.*;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.sql.*;

import javax.swing.JOptionPane;

import com.google.common.base.Splitter;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

import readRepository.readRepository.MessageReadingUtils;
import readRepository.readRepository.MessageWritingUtils;
import connections.MysqlConnect;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.process.DocumentPreprocessor;
import miner.process.ProcessSinglesAndDoubles;
import miner.process.ProcessingRequiredParameters;
import miner.process.PythonSpecificMessageProcessing;
import connections.PropertiesFile;
import readRepository.MessageSubjectProposalTitleMatching.MatchProposalTitlesToProposalNumbers;
import readRepository.MessageSubjectProposalTitleMatching.MatchProposalTitlesToProposalNumbersJEPs;
import utilities.GetDate;
import utilities.ReturnNattyTimeStamp;
import utilities.TestMemory;

//Pre Reading tasks

//1. Set which class you reading for, this si different fro PEPs and JEPS
// static MatchProposalTitlesToProposalNumbers mpt = new MatchProposalTitlesToProposalNumbers();
//OR static MatchProposalTitlesToProposalNumbersJEPs mpt = new MatchProposalTitlesToProposalNumbersJEPs();

//Set these values:
// restart =  false if starting from beginning,true if restarting in middle of process, maybe after crash or slowdown 
// messageID - very important set to 0, if starting from beginning or set last succesful mid if from middle
// markFound, markerFolder, markerFile

//nov 2018, this is main script to read in txt data
//30 sept 2017 -I have a feeling this is the main class used to read in data the last time

//after this has done its work,. we can check by checking all records inserted with -1 as proposal identifies
// the following case was found and can be added mauybe...http://openjdk.java.net/jeps/175
//maybe more patterns would come up

//This class reads all proposal messages regardless if tehre is a proposal number or a proposal title mentioned
//This is for analysis as to how many messages the community members make, It might not be necessary at all. 
//it reads all in one shot and its can be run separately ..Refer to place where it says MAIN CODE HERE

//jan 2018. testing of patterns added
//when mesages have been read into the database table, and we want to test further patterns to match more patterns in some messages
//in these cases the messages would be already in the db and messageid would be used to match...therefore this time we would read from the table instead of file.

//mar 2018 generic works fine even for peps ..i read in the python 3000 mailing list on top of existing data in db
// dont see a need to keep the non generic script (original script) now

//may 2018 ..using this script to read all mew pep data from 2017 to 2018 and also to cater for more combinations of regex added to list e.g. "PEP3101"
//integrated post reading update code..sendername, timestamp, messagenumberinfile

//Post reading updates
// After running this script you need to run few post reading update scripts
//  1. Pep title translating to pep number,  first sql statement 'update allmessages set originalPEPNumber = pep;'
//     Then run the script 'MatchProposalTitlesToProposalNumbers.java' in the Read package, under MessageSubjectProposalTitleMatching
//     The functionality is integrated within this script but to what extent, I have to check
//	2. UpdateAllMessages_MessageAuthorsRole.java
//  3. Of course the sendername clustering scripts, messageauthorrole script,
public class GenericMailingListReader_BaseElements {
	// JDBC driver name and database URL
	
	boolean readNullproposals = true;		//read messages with no proposals number or title
	boolean readTitle 	 = true;			//read messages with proposal titles
	
	static MysqlConnect mc = new MysqlConnect();	static Connection conn = mc.connect();
	
	//Things updated for proposals
	//1.replaced "pep" with "proposal" in this file
	//2.added the proposal folder below. In this case it would point to JEPs
	//	public static File proposal_folder = new File("C:/OSSDRepositories/JEPs/");
	//3. added these two external functions as functions in this class
	//mru.initproposalNumberSearchKeyLists(statusList, ignoreList, proposalNumberSearchKeyList);
	//Map<Integer, String> proposalDetails = mru.populateAllproposalTitleandNumbers(new HashMap<Integer, String>(), conn);
	//4 identifier for matching in files
	public static String proposalIdentifier = null; 
	
	//certain mru and mwr functiosn have been copied here, but once the entire projects(s) code has been made generic, they would 
	//reside in those classes
	
	/* this would be read in from properties file
	public static File dev_folder = new File("C:/datasets/python-dev/");
	public static File patches_folder = new File("C:/datasets/python-patches/");
	public static File lists_folder = new File("C:/datasets/python-lists/");
	public static File bugs_list_folder = new File("C:/datasets/python-bugs-list/");
	public static File committers_folder = new File("C:/datasets/python-committers/");
	public static File checkins_folder = new File("C:/datasets/python-checkins/");
	public static File ideas_folder = new File("C:/datasets/python-ideas/");
	public static File announce_folder = new File("C:/datasets/python-announce-list/");
	public static File distutils_sig_folder = new File("C:/datasets/python-distutils-sig/");
	*/
	//public static File gmane_folder = new File("C:/scripts/gmanetxtd/");
	
	// when only few messages to be read in  new db table = allproposals_gmane
	// but when reading in next timw with all data, try have diff db with same
	// tablename = allproposals, as select statement are coded.
	
	public static File few_folder = new File("C:/scripts/fewMsgtxt/"),dev_folder2 = new File("C:/dwn/");
	static String temp = "";
	// static String searchKey = "proposal 301 ";sss
	static String searchKeyAll;
	// static StringproposalTitlesSearchKeyList = null;
	static List<String> wordsList = new ArrayList<String>(),ignoreList = new ArrayList<String>(),statusList = new ArrayList<String>();
	
	// additional added
	static List<String> proposalNumberSearchKeyList = new ArrayList<String>(),proposalTitlesSearchKeyList = new ArrayList<String>();
	static int fileCounter; // total number of files with searchKey
	// static HashMap hm = new HashMap();
	static boolean foundInFile;
		
	static List<Integer> proposalList = new ArrayList<Integer>();
	// line lists
	// store proposal number and proposal title in this list and clear it after every line is read
	// 02 april 2021, we now use lists to store bips numbers that are stated in quotedLine variable as well
	static List<Integer> proposalNumbersInQuotedLinesList = new ArrayList<Integer>();
	
	static List<Integer> lineproposalNumberList = new ArrayList<Integer>(),lineproposalTitleList = new ArrayList<Integer>();
	// store proposal number and proposal title in this list and clear it after every
	// message is read
	static List<Integer> messageproposalNumberList = new ArrayList<Integer>(),proposalTitleFoundInMessageSubjectList = new ArrayList<Integer>(),proposalNumberFoundInMessageSubjectList = new ArrayList<Integer>();;
	static long t0, t1, t2, t3, t4, t5, t6, t7, t8, t9;
	Boolean readproposalTitle = false; // if we just want to read proposalTitles and not proposal numbers, dont insert into db if proposal numbers exist
	Boolean readproposalNumbers = false; // if we just want to read numbers and not proposal titles, dont insert into db if proposal titles exist
	// private static final Pattern UNDESIRABLES =
	// Pattern.compile("[][(){},.;!?<>%]");
	
	static MessageReadingUtils mru = new MessageReadingUtils();
	static MessageWritingUtils mwu = new MessageWritingUtils();	
	static PythonSpecificMessageProcessing pms = new PythonSpecificMessageProcessing();	
	static ReturnNattyTimeStamp rts = new ReturnNattyTimeStamp();
	static GetDate gd = new GetDate();  //new date parser centrally located in seprate package for use from all other package code
	static Date patternMatchingDateTimeStamp=null,nattyDateTimeStamp=null,timestampToUse=null;
	static MatchProposalTitlesToProposalNumbersJEPs mpt = new MatchProposalTitlesToProposalNumbersJEPs();
	// If we are restarting to read we want to start at different number. Initially all data inserted with mid started at 0
	// then extra states addition started at 500000, so we start at 600,000
	// IF STARTING FROM MIDDLE AFTER ERRO...edit and run Stuckquery to get the messageid and then minus 1
	static Integer messageID = 0; //always set to 0, will be populated later //1543295; //0; //3000000; //109653; //155850; //155599; //155732; //155599; // 2178486; //390871; //31470; //0;				
	//when restarting to read from the middle of a failed operation in the middle, put this to the last successful read, as it would increment before writing
	//FAILURE TO DO 
										//python bugs lists and patches start at 2 million
	//if stuck and need to restart, need to delete the messages already read in from the file ..in sql/readinmessages/stuckquery.sql, below	
	//select min(messageID) from allmessages where folder = 'C:\\datasets\\python-dev' and file = '2013-November.txt'
	//SELECT and then DELETE FROM allmessages where messageID > 159369
	static boolean restart      = true;		//always true...true if crashed in middle..always set to true..MAKE SURE SET THE MESSAGEID ALSO
	static boolean markFound    = false;  	//always false..(is the marker found?)...change to true if you are starting & want to read everything and false if restarting
	static String  markerFolder = ""; //always leave empty..will be populated automatically  //C:\\OSSDRepositories\\JEPs\\core-libs-dev //"C:\\datasets\\python-checkins"; //python-dev";// jmh-dev";	// C:\\datasets\\python-dev") && fileEntry.getName().equals("2009-January.txt"
	static String  markerFile   = ""; //always leave empty..will be populated automatically  //2014-August.txt  //2013-November.txt";
	//MAke sure comment the major directory from list that you dont want to process in the directory function //eg searchFilesForFolder(ideas_folder,
	static String  baseDir      = "C:\\OSSDRepositories\\JEPs\\";  //"c:\\datasets\\"  //remove this basedir from folder path when just extracting last dir
	
	static Set<String> stopWordsSet = new HashSet<String>();	
	static BufferedReader br = null;	
	static TestMemory tm = new TestMemory();
	
	//variables from function now made global in an attempt to save memory
	static File rootFolder = null;
	static Date dateTimeStamp = null, justDate = null;
	String line, line2, From = null, emailMessage = null;
	int counter = 0;						
	static Boolean matchedproposalNumberInMessage = false, matchedproposalTitleInMessage = false, StatusChanged = false, required = true, keepOnReading = true,
			matchedproposalNumberInLine = false, matchedproposalTitleInLine  = false;
	static String location = null, fromLine = null, lastLine = null, dateLine = null, status = null, statusLine = null, type = null, author = null, statusFrom = null, statusTo = null, 
			wordsFoundList = null, emailMessageId = null, inReplyTo = null, references = null, link = null, analyseWords = "", subject = null, 
			senderName = null; 
	static Integer lineCounter, notrequiredLinesCounter;						
	static int withinFileCounter = 0;
	static boolean proposalTitlefoundInMessageSubject = false,proposalNumberfoundInMessageSubject=false;
	static boolean test = false, process=  true;
	
	
		
	public static void initProposalIdentifier(String v_proposalIdentifier) {
		proposalIdentifier = v_proposalIdentifier;
	}
	//nov 2018, Not sure where this function is used, maybe called in some other class
	public static void getMessageUsingMessageID(Integer messageID,Connection conn){
		String message =null,messageSubject=null, sendername=null, folder=null;		
		Integer proposal = null;	
		Boolean quotedLine = false; //April 02 2021
		try	{			
			String query= "";
			if (messageID<0)
				System.out.println("Error no messageID as " + messageID);
			else	    	  																	//pep = " + pep + " and
				query = "SELECT "+proposalIdentifier+",folder, subject, clusterbysenderfULLname,email FROM allmessages where messageID ="+messageID+";"; // and folder = 'c:\\datasets\\python-ideas' and clusterbysenderfullname like '%Barry%';";
			Statement st = conn.createStatement(); 		ResultSet rs = st.executeQuery(query);
			while (rs.next())  {
				message = rs.getString("email");				proposal = rs.getInt(proposalIdentifier);				messageSubject = rs.getString("subject");
				folder = rs.getString("folder");				sendername = rs.getString("clusterbysenderfULLname");
				//System.out.println("\n\n identified ");

				//extract sentences from this message
				//get triples from each sentence
				// match triple and see if can find instances where pep number is assigned
				//   if so then get the old pep tile for that pep idea and add to the list of pep titles for each pep

				//			String title = getMorePEPTitles();
				String v_message = message,previousParagraph = null, nextParagraph = null; ;
				Integer sentenceCounter = 0, insertDiscussionCounter = 0;				
				//temporary declare these variables
				String v_idea = "", subject = "", verb = "", object = "";	
				if (!v_message.isEmpty()) {
					try {    
						//we want to check the subkect also ...so simply we ad the subject at the beginning of the message	       
						if ( subject==null || subject == "" || subject.isEmpty() )
						{}
						else {
							v_message = subject.toLowerCase() + ". \n\n" + v_message;   //this will check it for triples, and later doubles and singles
							//the above will allow the msg subject will be checked for existence of pep number, but just to make sure, we will add code here
							
							//Check for pep # in message subject using different patterns....sub main Proposal number matching - check in proposal number is found in message subject
							//the last two lists passed as parameter should be empty at this moment
							boolean matchedproposalNumberInessageSubject = matchproposalNumbersAddToList(proposalNumberSearchKeyList, ignoreList, subject, matchedproposalNumberInMessage, 
									proposalNumberFoundInMessageSubjectList); //, quotedLine, proposalNumbersInQuotedLinesList);						
							//the proposal number would be added to list ..proposalNumberFoundInMessageSubjectList	
						}
						String PreviousSentenceString = null;
						String[] paragraphs = v_message.split("\\n\\n");
						//System.out.println("----- Now Processing new Message ID: ("+v_message_ID+") total paragraphs.." + paragraphs.length);
						Integer count=0,paragraphCounter=0;        		   
						Boolean permanentMessageHasLabel = false;		//if message has any label captured set this to true

						//check message
						for (String g: paragraphs)	{	

							if (paragraphs.length ==1){	        				
								nextParagraph = null; previousParagraph = null;
							}
							//paragraph length = 2,3,4 onwards
							else {
								if (paragraphCounter==0)
									previousParagraph = null;
								else 
									previousParagraph = paragraphs[paragraphCounter-1];

								if (paragraphCounter==paragraphs.length-1)
									nextParagraph = null;
								else
									nextParagraph = paragraphs[paragraphCounter+1];

								Reader reader = new StringReader(g);
								DocumentPreprocessor dp = new DocumentPreprocessor(reader);

								//sometimes a paragrapgh has sentences but are not captured as sentences. So if sentence identified then check, so we just add a full-stop at end of paragraph	        				   
								if(!g.contains("."))
									g = g + ".";

								for (List<HasWord> eachSentence : dp){   	

									boolean dependency = true, v_stateFound = false, foundLabel = false, double_Found = false,foundReason=false;
									String nextSentence = null, CurrentSentenceString = Sentence.listToString(eachSentence);																	
									++sentenceCounter;

									//remove unnecessary words like "Python Update ..."																	
									CurrentSentenceString = pms.removeLRBAndRRB(CurrentSentenceString);								
									CurrentSentenceString = pms.removeDivider(CurrentSentenceString);
									CurrentSentenceString = pms.removeUnwantedText(CurrentSentenceString);	
									//									allSentenceList.add(CurrentSentenceString);
									//temp debug
									if(CurrentSentenceString.toLowerCase().contains("jep")) {
										System.out.println("\t SENTENCE " + CurrentSentenceString);	
										
										matchedproposalNumberInLine = matchproposalNumbersAddToList(proposalNumberSearchKeyList, ignoreList,  CurrentSentenceString, matchedproposalNumberInMessage,proposalList); 
												//quotedLine,proposalNumbersInQuotedLinesList);					
										if (matchedproposalNumberInLine){
											matchedproposalNumberInMessage = true;
											//System.out.println("\tmatchedproposalNumber "  ); 
										}
									}

									PreviousSentenceString = CurrentSentenceString;
								}//end of for loop for sentence
								paragraphCounter++;
							}//end else if

						} //end for loop for paragraphs	        		
					}  //end try       
					catch (Exception e){ 
						//System.out.println(v_message.getMessage_ID() + " ______here  " + e.toString() + "\n");
						//continue;
					} 
				}
			}

			//for all the pep titles found for a pep, 
			//  assign the pep number to messages which have the pep titles in the subject

			st.close();
		}	catch (Exception e)	{
			System.err.println("Got an exception! ");
			System.err.println(e.getMessage());
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
	
	//add all the main status list here
	public static void initStatusLists(List<String> statusList, String proposalStatus) {
		String status[] = proposalStatus.split(" ");
		
		for(String s: status) {
			statusList.add(s);
		}		
//		statusList.add("Draft");		  statusList.add("Open");			  statusList.add("Active");
//		statusList.add("Pending");	  	  statusList.add("Closed");		  	  statusList.add("Final");
//		statusList.add("Accepted");		  statusList.add("Deferred");		  statusList.add("Replaced");
//		statusList.add("Rejected");		  statusList.add("Postponed");		  statusList.add("Incomplete"); 
//		statusList.add("Superseded"); 
	}
	
	//this function would have to be generalised
	 public static void initproposalNumberSearchKeyLists(List<String> ignoreList, List<String> proposalNumberSearchKeyList){
		  
		  String regex = "\\d+";  //match digits - ANY NUMBER OF TERMS
		  
		  //proposal numbers
		  proposalNumberSearchKeyList.clear();
		  proposalNumberSearchKeyList.add(proposalIdentifier + ", " + regex);

		  //added in jan 2018, additional ones to the above are those patterns which mainly exist in message subject
		  proposalNumberSearchKeyList.add(proposalIdentifier + regex);
		  proposalNumberSearchKeyList.add(proposalIdentifier + ": " + regex);		proposalNumberSearchKeyList.add(proposalIdentifier + " : " + regex);		
		  proposalNumberSearchKeyList.add(proposalIdentifier + regex + " ");		proposalNumberSearchKeyList.add(proposalIdentifier+ " " + regex + " ");			
		  proposalNumberSearchKeyList.add(proposalIdentifier+ " " + regex + "\\?");	proposalNumberSearchKeyList.add(proposalIdentifier+" " + regex + "\\."); 		
		  proposalNumberSearchKeyList.add(proposalIdentifier+ " " + regex + "\\,"); proposalNumberSearchKeyList.add(proposalIdentifier+" " + regex + "\\;");		
		  proposalNumberSearchKeyList.add(proposalIdentifier+ " " + regex + "\\:");	proposalNumberSearchKeyList.add(proposalIdentifier+" " + regex + "\\[");		   
		  proposalNumberSearchKeyList.add(proposalIdentifier+"-" + regex + ".txt");	proposalNumberSearchKeyList.add(proposalIdentifier+" " + regex);
		  //not sure why we need spaces in front here, so removed - proposalNumberSearchKeyList.add(" "+proposalIdentifier+"-" + regex);	
		  proposalNumberSearchKeyList.add(proposalIdentifier+"-" + regex);	
		  proposalNumberSearchKeyList.add(proposalIdentifier + regex + " ");		proposalNumberSearchKeyList.add(proposalIdentifier +"-" + regex + ": ");	
		  proposalNumberSearchKeyList.add(proposalIdentifier+"-" + regex + " ");	proposalNumberSearchKeyList.add(proposalIdentifier+ " " + regex + ": ");	
		  //added jan 2018
		  proposalNumberSearchKeyList.add(proposalIdentifier+"-" + regex); //(JEP-124)
		  proposalNumberSearchKeyList.add(proposalIdentifier+"s\\/" + regex); //(jeps/162)
		  proposalNumberSearchKeyList.add(proposalIdentifier + " " + regex+":"); //(jep 254:)
		  proposalNumberSearchKeyList.add(proposalIdentifier+"s " + regex); //(JEPS 81820)
		  proposalNumberSearchKeyList.add(proposalIdentifier+"s" + regex); 
		  //pep-0008.txt,1.26,1.27
		  proposalNumberSearchKeyList.add(proposalIdentifier+"..." + regex+".txt");	proposalNumberSearchKeyList.add(proposalIdentifier+"-..." + regex);
		  proposalNumberSearchKeyList.add(proposalIdentifier+".." + regex+".txt");	proposalNumberSearchKeyList.add(proposalIdentifier+"-.." + regex);
		  proposalNumberSearchKeyList.add(proposalIdentifier+"." + regex+".txt");	proposalNumberSearchKeyList.add(proposalIdentifier+"-." + regex);
		  //end added in jan 2018
		  
		  //	 http://openjdk.java.net/jeps/175 - have to test if this works
		  proposalNumberSearchKeyList.add(proposalIdentifier + "s//" + regex);
		  
		  ignoreList.clear();
		  ignoreList.add(proposalIdentifier + ": " + regex + ".");
		  ignoreList.add(proposalIdentifier + ": " + regex + "..");
		  ignoreList.add(proposalIdentifier + ": " + regex + "...");	
		  
	  }
	
	 //output All PepTitle and Numbers
	  public static Map<Integer,String> populateAllproposalTitleandNumbers(Map<Integer,String> proposalDetails, Connection conn) {
			String title = "";	
			try
		    {
		      String query = "SELECT "+proposalIdentifier+", title FROM "+proposalIdentifier+"details order by "+proposalIdentifier+";";
		      Statement st = conn.createStatement();  ResultSet rs = st.executeQuery(query);     
		      while (rs.next()) {
		        int proposalNumber = rs.getInt(proposalIdentifier);		  title = rs.getString("title").trim();		      title = RemovePunct(title);	      title = title.toLowerCase();
		        //title = processWord(title); 		       // title = RemovePunct(title);
//		        String lastName = rs.getString("last_name");//		        Date dateCreated = rs.getDate("date_created");
//		        boolean isAdmin = rs.getBoolean("is_admin");//		        int numPoints = rs.getInt("num_points");	        
		        proposalDetails.put(proposalNumber,title);	//		        System.out.println(proposalNumber + " " +title);
		      }
		      st.close();
		    }
		    catch (Exception e) {
		      System.err.println("Got an exception while reading Titles! ");		      System.err.println(e.getMessage());
		    }		
			return proposalDetails;
		}
	  
	  static String RemovePunct(String input)   {
			char[] output = new char[input.length()];
			int i = 0;
			for (char ch : input.toCharArray()) {
				if (Character.isLetterOrDigit(ch) || Character.isWhitespace(ch)) {
					output[i++] = ch;
				}
			}
			return new String(output, 0, i);
	  }
	
	//SINCE WE WILL START READING FROM THE BEGINNNING OF THAT FILE, DELETE THE laready read in messages
	private static void redundant() {
		if (!restart){
			messageID=0;
		}
		//if we are restarting
		else{
			if(!markFound){		
				//find the mark
				//change to delete all messages in table which are from the file which gave error....
				//as processing will start from the beginning of that fiel again.
				 messageID = mru.deleteHalfdone(conn,markerFolder, markerFile);
			}
			//not sure when will the below code be called
			//else	//get max messageID so to start from the last one
			//	messageID = mru.selectMAX(conn,markerFolder, markerFile);
		}
	}

	protected static void populateStopWordlist() {
		stopWordsSet.add("is");		 		stopWordsSet.add("a"); 		stopWordsSet.add("the"); 	stopWordsSet.add("of");
	}			
	 
	private static void getFileSize(final File fileEntry) {
		//get the file size - dont process anything bigger than 1mb
		// Get length of file in bytes
		long fileSizeInBytes = fileEntry.length();
		// Convert the bytes to Kilobytes (1 KB = 1024 Bytes)
		long fileSizeInKB = fileSizeInBytes / 1024;
		// Convert the KB to MegaBytes (1 MB = 1024 KBytes)
		long fileSizeInMB = fileSizeInKB / 1024;
		boolean process = true;
		if (fileSizeInMB > 1) {
		  process = false;
		}
	}

	protected static void sleepGarbageCollect() {
		try {
			TimeUnit.SECONDS.sleep(1);
			  //call garbage collector      
			   System.gc();              //OR call 
			   Runtime.getRuntime().gc();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	 
	public static String removeStopwords(String s){		 
		 String[] words = s.split(" ");		 
		 ArrayList<String> wordsList = new ArrayList<String>();
		 
		 for(String word : words) {			 
			 String wordCompare = word.toLowerCase();			 
			 if(!stopWordsSet.contains(wordCompare)) {				 
				 wordsList.add(word);				 
			 }			 
		 }
		 
		 for (String str : wordsList){			 
			 System.out.print(str+" ");			 
		 }
		 wordsList = null;
		 return wordsList.toString();
	 }
	
	private static Integer getNumberOfStopWordsMatched(Set<String> intersection) {
		Integer stopWordsmatched=0;
		//Set<String> stopWordsSet = new HashSet<String>();
		//line saving version
		Set<String> stopWordsSet = new HashSet<String>(Arrays.asList("is", "a", "the","of","for","and","as","to","an","with","in"));
//		stopWordsSet.add("is");		stopWordsSet.add("as"); 
//		stopWordsSet.add("a");		stopWordsSet.add("to"); 
//		stopWordsSet.add("the");	stopWordsSet.add("an");	 
//		stopWordsSet.add("of"); 
//		stopWordsSet.add("and");
		 
		Set<String> intersectionStopWords = Sets.intersection(intersection, stopWordsSet);
		stopWordsmatched = intersectionStopWords.size();
		return stopWordsmatched;
	}
	/* Not used 
	private static void checkForDuplicateproposalsInList(Boolean matchedproposalNumberInMessage, Boolean matchedproposalTitleInMessage, Boolean matchedproposalTitleInLine) {
		//if both are matched
		if (matchedproposalNumberInMessage && matchedproposalTitleInMessage) {
			//remove all proposals which exist in both, from both
//											System.out.println("\tmatched as number and title in message");
			removeFromMessageproposalList(matchedproposalTitleInLine);										
		} 
		//if only proposal number is found, dont insert, so have to remove everything from proposalNumberList
		else if (matchedproposalNumberInMessage && !matchedproposalTitleInMessage) {
			proposalList.clear();
//											System.out.println("\tmatched proposalNumber In Message");
		}
		//else if only proposal title is found, let it/them be inserted
		else if (matchedproposalTitleInMessage && !matchedproposalTitleInMessage) {
//											System.out.println("\tmatched proposalTitle In Message");
		} else {
//											System.out.println("\tnot matched as number and title in message");
		}
	}
	*/
	// if same proposal is added in both lists, then dont add in database - just
	// remove those proposals from title list which are found in proposal number list
	private static void removeFromMessageproposalList(Boolean matchedproposalTitleInMessage) {	
		// Prepare an intersection
		List<Integer> intersection = new ArrayList<Integer>(proposalTitleFoundInMessageSubjectList);
		// retain only the common ones in both
		intersection.retainAll(messageproposalNumberList);
		// find a way to remove these common ones from both lists
		messageproposalNumberList.removeAll(intersection);
		proposalTitleFoundInMessageSubjectList.removeAll(intersection);
		proposalList.removeAll(intersection);
		
		intersection.clear();
	}
	
	private static void removeFromproposalList(Boolean matchedproposalTitleInLine) {
		List<Integer> intersection = new ArrayList<Integer>(lineproposalTitleList);
		// retain only the common ones in both
		intersection.retainAll(lineproposalNumberList);
		// find a way to remove these common ones from both lists
		// messageproposalNumberList.removeAll(intersection);
		lineproposalTitleList.removeAll(intersection);
		proposalList.removeAll(intersection);		
		intersection.clear();
	}
	/*
	private static void removeFromproposalListOLD(Boolean matchedproposalTitleInLine) {
		// System.out.println("\t in fn a");
		// if same proposal is added in both lists, then dont add in database
		// just remove those proposals from title list which are found in proposal number
		// list
		for (int k = 0; k < lineproposalTitleList.size(); ++k)
			for (int l = 0; l < lineproposalNumberList.size(); ++l)
				if (lineproposalNumberList.get(l).equals(lineproposalTitleList.get(k))) {
					Integer proposalToRemove = lineproposalNumberList.get(l);
					lineproposalTitleList.remove(k);
					for (int u = 0; u < proposalList.size(); ++u)
						if (proposalList.get(u).equals(proposalToRemove))
							proposalList.remove(u);
				}
	}
	*/
	// if both lists contain the proposal, remove all proposals which exist in both, from
	// both
	private static void removeFromMessageproposalListoldOLD(Boolean matchedproposalTitleInMessage) {
		// System.out.println("\t in fn b");
		// if same proposal is added in both lists, then dont add in database
		// just remove those proposals from title list which are found in proposal number
		// list
		for (int k = 0; k < proposalTitleFoundInMessageSubjectList.size(); ++k)
			for (int l = 0; l < messageproposalNumberList.size(); ++l)
				if (messageproposalNumberList.get(l).equals(proposalTitleFoundInMessageSubjectList.get(k))) {
					Integer proposalToRemove = messageproposalNumberList.get(l);
					proposalTitleFoundInMessageSubjectList.remove(k);
					messageproposalNumberList.remove(l);
					--k;
					--l;
					for (Iterator<Integer> ¢ = proposalList.iterator(); ¢.hasNext();)
						if (¢.next().equals(proposalToRemove))
							¢.remove();
				}
	}
	
	//this function is no longer being used as we dont consider proposal title in each line of message, only in subject line -- TOO MANY MATCHES INSIDE THE BODY MAY REFERE TO SOME OTHER CONCEPT ALTHOUGH TERMS MATCH WITH PEP TITLE
	private static Boolean matchproposalTitlesAddToList(final File folder, Writer v_debugFile,List<String> v_titleSearchKeyList, Map<Integer, String> proposalDetails, final File fileEntry, String line,
			Boolean matchedproposalTitle) {
		String location, lastLine;
		Matcher m5;
		for (String proposalTitle : v_titleSearchKeyList) {		
			//for each pattern found
			if (line.contains(proposalTitle)) // while (m.find() || m9.find())
			{
				//withinFileCounter++;
				foundInFile = true;
				matchedproposalTitle = true; // if searchkey found, set matched to true
				Integer proposal = null;
				//GET proposal NUMBER USING proposal TITLE	for storing in database table allproposals					              
		        for (Map.Entry<Integer, String> entry : proposalDetails.entrySet()) {
		    	   if (entry.getValue().contains(proposalTitle)) {
		               proposal = entry.getKey();
		              }				            	   
		        }
		       	System.out.println("proposal (" + proposal + ") Title (" + proposalTitle + ") found in message line:" + line);
		        	        
				proposalList.add(proposal); // add proposal to list
				lineproposalTitleList.add(proposal);
				proposalTitleFoundInMessageSubjectList.add(proposal);				
			} // end while
			
		} // end for
		return matchedproposalTitle;
	}
	
	//match all proposal numbers here in this function
	//list populated in another function beforehand
	public static Boolean matchproposalNumbersAddToList(List<String> v_searchKeyList, List<String> v_ignoreList,  String sentence, Boolean $, 
			List<Integer> v_proposalList) { //we update two lists at different cases ..one when nwe wantnto store proposal number matched in body and the other in message subject
		String location, lastLine;                           //, Boolean quotedLine, List<Integer> proposalNumbersInQuotedLinesList
		sentence = sentence.toLowerCase();
		Matcher m5;
		for (String pattern : v_searchKeyList) {
			m5 = Pattern.compile(pattern).matcher(sentence);
			while (m5.find()) // while (m.find() || m9.find())
			{
//$$				System.out.print("\t\tfound pattern: " + pattern);
				boolean Store = true;
				Matcher m5a = null;
				for (String pattern2a : v_ignoreList) { // so many combinations	we cant store e.g.	proposal 312, if we looking for proposal: 3 only
					m5a = Pattern.compile(pattern2a).matcher(sentence);
					while (m5a.find()) {
						Store = false;
					}
				}
				// end added
				if (Store) {
					//System.out.println("store= true: ");
					$ = foundInFile = true; // if searchkey found, set matched
											// to true
					String proposalString= null;
					Integer proposal;
					String str = pattern.replaceAll("[^-?0-9]+", " ");
					// System.out.println("Added " +
					// Arrays.asList(str.trim().split(" ")));
					// IMP MAYBE System.out.println("m5 " + m5.group(0) );
					proposalString = m5.group(0).replace(proposalIdentifier+"s", "");
					proposalString = proposalString.replace(proposalIdentifier, "");
					//System.out.println("here a proposalString " + proposalString);
						//if(proposalString == null || proposalString.isEmpty()) {
						//	proposalString = m5.group(0).replace(proposalIdentifier, "");
						//	System.out.println("here00");
						//}
						//else {System.out.println("here0"); }
						// System.out.println("here b proposalString " + proposalString);
					
					// proposalString = m5.group(0).replace("proposal ","");
					// proposalString = proposalString;
					proposalString = proposalString.replace(".txt", "").replace("-", "").replace(" ", "");
					proposalString = proposalString.replaceAll("[,.:;!?(){}\\[\\]<>%]", ""); // remove punctuation
					// System.out.println("here b proposalString " + proposalString);
					proposalString = proposalString.replace("txt", "");
					//jan 2019
					proposalString=proposalString.replaceAll("\\/","");
					
//$$					System.out.println("\tproposalString: " + proposalString);
					if(isNumeric(proposalString)) {
						proposal = Integer.parseInt(proposalString);
						//sometimes pep number shows up as negative, a byproduct of our pattern matching that we would come up with negative numbers as pep-311 will be matched as -311
						proposal = Math.abs(proposal);
//$$						System.out.println("\t\t\tProposal (" + proposal + ") Number (" + pattern + ") found in "+ (pattern.contains("Subject:") ? "subject: " : "message line:") + sentence);
						v_proposalList.add(proposal); // add proposal to list
//$$						System.out.println("\t\t\tAdded proposal in both d " + proposal + " sentence: [" + sentence+"]");
						// add to line list
						lineproposalNumberList.add(proposal);
						messageproposalNumberList.add(proposal);
						proposalNumbersInQuotedLinesList.add(proposal); ///added april 2021
					}
					else {
						System.out.println(" non numeric: " + proposalString );
					}
					// }
					lastLine = sentence;
					//location = folder + "\\" + fileEntry.getName();
					// WHY THIS LINE BELOW??
					// emailMessage = emailMessage + " a " + line + "\n";
					//mwu.storeInDebugFile(v_debugFile, "pattern " + pattern + " line" + line);
				}
			} // end while
		}
		return $;
	}
	
	public static boolean isNumeric(String str)	{
	    for (char c : str.toCharArray())	    {
	        if (!Character.isDigit(c)) return false;
	    }
	    return true;
	}
	
} // end class