package readRepository.readRepository;

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
import com.google.common.collect.Sets;

import connections.MysqlConnect;
import miner.process.PythonSpecificMessageProcessing;
import utilities.GetDate;
import utilities.ReturnNattyTimeStamp;
import utilities.TestMemory;

//30 sept 2017 -I have a feeling this is the main class used to read in data the last time

//This class reads all pep messages regardless if tehre is a pep number or a pep title mentioned
//This is for analysis as to how many messages the community members make
//It might not be necessary at all. 
//it reads all in one shot and its can be run separately 
//Refer to place where it says MAIN CODE HERE
public class _ReadAllMessagesFromAllLists {
	// JDBC driver name and database URL
	
	boolean readNullPeps = true;		//read messages with no peps number or title
	boolean readTitle 	 = true;		//read messages with pep titles
	
	static MysqlConnect mc = new MysqlConnect();
	static Connection conn = mc.connect();
	
	public static File dev_folder = new File("C:/datasets/python-dev/");
	public static File patches_folder = new File("C:/datasets/python-patches/");
	public static File lists_folder = new File("C:/datasets/python-lists/");
	public static File bugs_list_folder = new File("C:/datasets/python-bugs-list/");
	public static File committers_folder = new File("C:/datasets/python-committers/");
	public static File checkins_folder = new File("C:/datasets/python-checkins/");
	public static File ideas_folder = new File("C:/datasets/python-ideas/");
	public static File announce_folder = new File("C:/datasets/python-announce-list/");
	public static File distutils_sig_folder = new File("C:/datasets/python-distutils-sig/");
	public static File python3000_folder = new File("C:/datasets-additional/python-3000/");
	//public static File gmane_folder = new File("C:/scripts/gmanetxtd/");
	
	// when only few messages to be read in  new db table = allpeps_gmane
	// but when reading in next timw with all data, try have diff db with same
	// tablename = allpeps, as select statement are coded.
	
	public static File few_folder = new File("C:/scripts/fewMsgtxt/");
	public static File dev_folder2 = new File("C:/dwn/");
	static String temp = "";
	// static String searchKey = "PEP 301 ";sss
	static String searchKeyAll;
	// static StringpepTitlesSearchKeyList = null;
	static List<String> wordsList = new ArrayList<String>(); 
	static List<String> ignoreList = new ArrayList<String>();
	static List<String> statusList = new ArrayList<String>();
	
	// additional added
	static List<String> pepNumberSearchKeyList = new ArrayList<String>();
	static List<String> pepTitlesSearchKeyList = new ArrayList<String>();
	static int fileCounter; // total number of files with searchKey
	// static HashMap hm = new HashMap();
	static boolean foundInFile;
	
	static List<Integer> PEPList = new ArrayList<Integer>();
	// line lists
	// store pep number and pep title in this list and clear it after every line
	// is read
	static List<Integer> linePEPNumberList = new ArrayList<Integer>();
	static List<Integer> linePEPTitleList = new ArrayList<Integer>();
	// store pep number and pep title in this list and clear it after every
	// message is read
	static List<Integer> messagePEPNumberList = new ArrayList<Integer>();
	static List<Integer> messagePEPTitleList = new ArrayList<Integer>();
	static long t0, t1, t2, t3, t4, t5, t6, t7, t8, t9,t10;
	Boolean readPEPTitle = false; // if we just want to read pepTitles and not pep numbers, dont insert into db if pep numbers exist
	Boolean readPEPNumbers = false; // if we just want to read numbers and not pep titles, dont insert into db if pep titles exist
	// private static final Pattern UNDESIRABLES =
	// Pattern.compile("[][(){},.;!?<>%]");
	
	static MessageReadingUtils mru = new MessageReadingUtils();
	static MessageWritingUtils mwu = new MessageWritingUtils();
	
	static PythonSpecificMessageProcessing pms = new PythonSpecificMessageProcessing();
	
	static ReturnNattyTimeStamp rts = new ReturnNattyTimeStamp();
	
	// If we are restarting to read we want to start at different number. Initially all data inserted with mid started at 0
	// then extra states addition started at 500000, so we start at 600,000
	// IF STARTING FROM MIDDLE AFTER ERRO...edit and run Stuckquery to get the messageid and then minus 1
	static Integer messageID = 3000000; 	//we want to read the missed mailing lists ..python 3000 
			//2500000; // 2178486; //390871; //31470; //0;				//when restarting to read from the middle of a failed operation in the middle, put this to the last succesful read	
												//as it would increment before writing
										//python bugs lists and patches start at 2 million
	static boolean restart = false;		//true if crashed in middle
	static boolean markFound =false; 	//change to true if you want to read everything
	static String markerFolder = "python-bugs-list";	// C:\\datasets\\python-dev") && fileEntry.getName().equals("2009-January.txt"
	static String markerFile = "2011-May.txt";
	//MAke sure comment the major directory from list that you dont want to process in the directory function //eg searchFilesForFolder(ideas_folder,
	
	static Set<String> stopWordsSet = new HashSet<String>();
	
	static BufferedReader br = null;
	
	static TestMemory tm = new TestMemory();
	
	//variables from function now made global in an attempt to save memory
	static File rootFolder = null;
	static Date dateTimeStamp = null, date9 = null;
	String line, line2, From = null, emailMessage = null;
	int counter = 0;						
	static Boolean matchedPEPNumberInMessage = false, matchedPEPTitleInMessage = false, StatusChanged = false, required = true, keepOnReading = true,
			matchedPEPNumberInLine = false, matchedPEPTitleInLine  = false;
	static String location = null, lastLine = null, date2 = null, status = null, statusLine = null, type = null, author = null, statusFrom = null, statusTo = null, 
			wordsFoundList = null, emailMessageId = null, inReplyTo = null, references = null, link = null, analyseWords = "", subject = null, 
			senderName = null; 
	static Integer lineCounter, notrequiredLinesCounter;						
	static int withinFileCounter = 0;
	
	static GetDate gd = new GetDate();  //new date parser centrally located in seprate package for use from all other package code
	
	public static void main(String[] args) throws IOException {	
		
		populateStopWordlist(); 
	
		//if we are starting from scratch
		//redundant();		
		
		// emptyDatabase_gmane();
		// initialise pep numbers, in pepNumberSearchKeyList and ignoreList
		mru.initPepNumberSearchKeyLists(statusList, ignoreList, pepNumberSearchKeyList);
		// initialise pepTitles
		pepTitlesSearchKeyList.clear();
		int i = 0;
		// map to store pep titles and numbers
		Map<Integer, String> pepDetails = mru.populateAllPepTitleandNumbers(new HashMap<Integer, String>(), conn);
		addEntriesToList(pepDetails);
		// System.out.println(searchKeyAllA.get(1));
		File statText = new File("c:/datasets/output" + i + ".txt");
		// output file declaration
		FileOutputStream is = new FileOutputStream(statText);
		OutputStreamWriter osw = new OutputStreamWriter(is);
		Writer w = new BufferedWriter(osw);
		// DEBUG File
		File debugText = new File("c:/data/debug.txt");
		// output file declaration
		FileOutputStream debug = new FileOutputStream(debugText);
		OutputStreamWriter debugosw = new OutputStreamWriter(debug);
		searchLists(i, pepDetails, w, new BufferedWriter(debugosw));
		w.close();

	    // outputResults(conn);
		System.out.println("Finished processing - Total Elapsed time =" + (t7 - t1) / 1000 + " seconds");
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

	private static void populateStopWordlist() {
		stopWordsSet.add("is");		 
		stopWordsSet.add("a");		 
		stopWordsSet.add("the");		 
		stopWordsSet.add("of");
	}
		
	private static void searchLists(int i, Map<Integer, String> pepDetails, Writer w, Writer wdebug)
			throws IOException {
		//searchFilesForFolder(dev_folder2, dev_folder2, foundInFile, searchKeyAll, fileCounter, w,	wdebug, conn, i, pepTitlesSearchKeyList, ignoreList, statusList, messageID, wordsList, pepDetails);
		
		t0 = System.currentTimeMillis();
		// search all lists, not only ideas folder as messagesd in others lists
		// too sometimes use pep title and not pep number
		// searchFilesForFolder(ideas_folder, ideas_folder, foundInFile,
		// searchKeyAll,fileCounter, w, wdebug, conn , i,pepTitlesSearchKeyList,
		// ignoreList, statusList, messageID, wordsList,pepDetails );
//		searchFilesForFolder(ideas_folder, ideas_folder, foundInFile, searchKeyAll, fileCounter, w, wdebug, conn, i,pepTitlesSearchKeyList, ignoreList, statusList, messageID, wordsList, pepDetails);
		t1 = System.currentTimeMillis();
//		System.out.println("Finished processing ideas folder - Elapsed time =" + (t1 - t0) / 1000 + " minutes");
//		searchFilesForFolder(dev_folder, dev_folder, foundInFile, searchKeyAll, fileCounter, w, wdebug, conn, i,pepTitlesSearchKeyList, ignoreList, statusList, messageID, wordsList, pepDetails);
		t2 = System.currentTimeMillis();
//		System.out.println("Finished processing dev folder - Elapsed time =" + (t2 - t1) / 1000 + " seconds");
		// searchFilesForFolder(patches_folder,patches_folder, foundInFile,
		// searchKeyAll,fileCounter, w ,wdebug, conn, i,pepTitlesSearchKeyList,
		// ignoreList, statusList);
		// System.out.println("Finished processing patches folder");
//		searchFilesForFolder(lists_folder, lists_folder, foundInFile, searchKeyAll, fileCounter, w, wdebug, conn, i,pepTitlesSearchKeyList, ignoreList, statusList, messageID, wordsList, pepDetails);
		t3 = System.currentTimeMillis();
//		System.out.println("Finished processing lists folder - Elapsed time =" + (t3 - t2) / 1000 + " seconds");
		// searchFilesForFolder(bugs_list_folder,bugs_list_folder, foundInFile,
		// searchKeyAll,fileCounter, w ,wdebug, conn, i,pepTitlesSearchKeyList,
		// ignoreList, statusList);
		// System.out.println("Finished processing bugs lists folder");
		
		//The below lists will atmost contain 100,000 messages
		
//		searchFilesForFolder(distutils_sig_folder, distutils_sig_folder, foundInFile, searchKeyAll, fileCounter, w,	wdebug, conn, i, pepTitlesSearchKeyList, ignoreList, statusList, messageID, wordsList, pepDetails);
		t4 = System.currentTimeMillis();
//		System.out.println(	"Finished processing distutils_sig_folder folder - Elapsed time =" + (t4 - t3) / 1000 + " seconds");
//		searchFilesForFolder(committers_folder, committers_folder, foundInFile, searchKeyAll, fileCounter, w, wdebug,	conn, i, pepTitlesSearchKeyList, ignoreList, statusList, messageID, wordsList, pepDetails);
		t5 = System.currentTimeMillis();
//		System.out.println("Finished processing committers folder - Elapsed time =" + (t5 - t4) / 1000 + " seconds");
//		searchFilesForFolder(announce_folder, announce_folder, foundInFile, searchKeyAll, fileCounter, w, wdebug, conn,	i, pepTitlesSearchKeyList, ignoreList, statusList, messageID, wordsList, pepDetails);
		t6 = System.currentTimeMillis();
//		System.out.println(	"Finished processing announce_folder list folder - Elapsed time =" + (t6 - t5) / 1000 + " seconds");
//		searchFilesForFolder(checkins_folder, checkins_folder, foundInFile, searchKeyAll, fileCounter, w, wdebug, conn,	i, pepTitlesSearchKeyList, ignoreList, statusList, messageID, wordsList, pepDetails);
		t7 = System.currentTimeMillis();
//		System.out.println("Finished processing checkins_folder folder - Elapsed time =" + (t7 - t6) / 1000 + " seconds");
	
		//these are also important for social network analysis
//		searchFilesForFolder(patches_folder, patches_folder, foundInFile, searchKeyAll, fileCounter, w, wdebug, conn,	i, pepTitlesSearchKeyList, ignoreList, statusList, messageID, wordsList, pepDetails);
//		t8 = System.currentTimeMillis();
//		System.out.println("Finished processing patches_folder folder - Elapsed time =" + (t7 - t6) / 1000 + " seconds");	
//		searchFilesForFolder(bugs_list_folder, bugs_list_folder, foundInFile, searchKeyAll, fileCounter, w, wdebug, conn,	i, pepTitlesSearchKeyList, ignoreList, statusList, messageID, wordsList, pepDetails);
		t8 = System.currentTimeMillis();
		//below line is commneted mar 2018
//		searchFilesForFolder(distutils_sig_folder, distutils_sig_folder, foundInFile, searchKeyAll, fileCounter, w, wdebug, conn,	i, pepTitlesSearchKeyList, ignoreList, statusList, messageID, wordsList, pepDetails);				
		t9 = System.currentTimeMillis();
		//adde mar 2018
		searchFilesForFolder(python3000_folder, python3000_folder, foundInFile, searchKeyAll, fileCounter, w, wdebug, conn,	i, pepTitlesSearchKeyList, ignoreList, statusList, messageID, wordsList, pepDetails);				
		t10 = System.currentTimeMillis();
		System.out.println("Finished processing bugs_list_folder folder - Elapsed time =" + (t10 - t9) / 1000 + " seconds");
		
	}
	
	private static void addEntriesToList(Map<Integer, String> pepDetails) {
		for (Map.Entry<Integer, String> ¢ : pepDetails.entrySet())
			pepTitlesSearchKeyList.add(¢.getValue());
	}
	
	 public static void searchFilesForFolder(final File folder, final File v_rootfolder, boolean v_foundInFile,String v_searchKey, int v_fileCounter, Writer v_outputFile, Writer v_debugFile, Connection v_conn, int i, List<String> v_searchKeyList, List<String> v_ignoreList, List<String> v_statusList, Integer v_messageID,
				List<String> v_wordsList,  Map<Integer, String> pepDetails ) throws IOException {
			rootFolder = v_rootfolder;
			dateTimeStamp = date9=null;
			Integer lineConterWithinFile=0;
			for (final File fileEntry : folder.listFiles()) 
			{
				if (fileEntry.isDirectory()) {
					searchFilesForFolder(fileEntry, v_rootfolder, v_foundInFile, v_searchKey, v_fileCounter, v_outputFile,v_debugFile, v_conn, i, v_searchKeyList, v_ignoreList, v_statusList, v_messageID, v_wordsList,  pepDetails );
				} 
				else {	
					if (tm.getMemoryUsageAlmostFull()){
						System.out.println("*********************, Memory Almost Full");
						sleepGarbageCollect();		//long process sleep 1 second						
					}
					//getFileSize(fileEntry);					
				
					//use this code only if you are using to restart coding
					//otherwise comment this block and the markFound clause in the subsequent of clause
					
					/*
					if (restart && !markFound){
						if (rootFolder.toString().contains(markerFolder) && fileEntry.getName().toString().equals(markerFile)){
							markFound=true;
							System.out.println("*********************, Mark Found = true");
						}
						else{
							System.out.println("Checked New File for mark " +rootFolder +" "+ fileEntry.getName());
						}
					}
					*/
					
					if (fileEntry.isFile() ){ //&& markFound){ // && process) {
						
						temp = fileEntry.getName();		// For each File
						System.out.println("------\n Processing New File " +rootFolder +" "+ fileEntry.getName());
						br = new BufferedReader(new FileReader(folder + "\\" + fileEntry.getName()));
						String line, line2, From = null, emailMessage = null;
						int counter = 0;						
						matchedPEPNumberInMessage = matchedPEPTitleInMessage = StatusChanged = matchedPEPNumberInLine = matchedPEPTitleInLine  = false;;
						required = keepOnReading = true;								
						location = lastLine = date2 = status = statusLine = type = author = statusFrom = statusTo = wordsFoundList = emailMessageId = inReplyTo = references = link = subject = senderName = null; 
						analyseWords = "";
						lineCounter= notrequiredLinesCounter=0;						
						withinFileCounter = 0;
						lineConterWithinFile=0;
						// For each line						
						while ((line = br.readLine()) != null) 
						{	 //line lists	
							linePEPNumberList.clear();						//store pep number and pep title in this list and clear it after every line is read
							linePEPTitleList.clear();						
							matchedPEPNumberInLine = matchedPEPTitleInLine=false; 
														
							if (line.startsWith("Date: ")){								
								date2 = line.replace("Date:", "");								
								//dateTimeStamp = mru.findDate(date2);
								//BELOW LINE COMMENTED FOR PYTHON LISTS 30 MAR 2017
//***								dateTimeStamp = rts.returnDate(date2);
									dateTimeStamp =null;
								date9 = gd.findDate(date2);
								//dateTimeStamp = pms.returnDate(date2);
								//System.out.println("DATE LINE " + line + " date2 " + date2 + "  dateTimeStamp " + dateTimeStamp);
							}
							else if (line.startsWith("Type: "))   
								type = line.replace("Type: ", "");							
							else if (line.startsWith("In-Reply-To: ")) {
								inReplyTo = line.replace("In-Reply-To: ", "");
								inReplyTo = inReplyTo.replaceAll("[,:;!?(){}\\[\\]<>%]", "");
							} else if (line.startsWith("References: ")) {
								references = line.replace("References: ", ""); 
								references = references.replaceAll("[,:;!?(){}\\[\\]<>%]", "");
							} // remove punctuation
							else if (line.startsWith("Message-ID: ")) {
								emailMessageId = line.replace("Message-ID: ", "");
								emailMessageId = emailMessageId.replaceAll("[,:;!?(){}\\[\\]<>%]", "");
								// System.out.println("emailMessageId" + emailMessageId);
							} 
							else if (line.startsWith("On ") && line.endsWith(" wrote:"))
								link = line.replace("On ", ""); // link that could be used - On 16 Jan 2014 11:45,"Carl Meyer" <carl at oddbird.net> wrote:
//							else if (line.startsWith("-Status: "))
//								statusFrom = line.replace("-Status: ", "");
//							else if (line.startsWith("+Status: "))
//								statusTo = line.replace("+Status: ", "");
							else if (line.startsWith("From:")) {
								senderName = mru.getSender(line,messageID);
								author = line.replace("From: ", "");
								author =  pms.getAuthorFromString(author);								
							} 
							else if (line.startsWith("Subject:")) {
								subject = line.replace("Subject: ", "");
								br.mark(10000000); // mark that place
								lineCounter = 0;
								notrequiredLinesCounter = 0;								
								//sometimes the subject goes into the next line
								//just read one more line
																																			
								line2=br.readLine();
								if (mru.matchQuery(line2, "Message-ID:") || mru.matchQuery(line2, "References:") || mru.matchQuery(line2, "In-Reply-To:")) {
									keepOnReading = false;
									// System.out.println("here k");
								} else {
									// System.out.println("here l " +
									// lineCounter);
									lineCounter++; // increment line counter
									if (line2.endsWith(".txt") || line2.endsWith(".py") || line2.endsWith(".js")
											|| line2.endsWith(".css") || line2.endsWith(".ell")
											|| line2.endsWith(".rst") || line2.endsWith(".h") || line2.endsWith(".c")
											|| line2.endsWith(".vcproj") || line2.endsWith(".vsprops")
											|| line2.endsWith(".tex")) 
										notrequiredLinesCounter++; // if so increment f counter
									else
										subject = subject + " " + line2;		//add next line to subject
								}
								 
								br.reset(); // reset the mark to original
								
								//remove [Python checkin] - used for matching in code below - commented
								String trimmedSubject="";
								if (subject.contains("[") && subject.contains("]"))		{
									trimmedSubject = subject.replaceAll("\\[.*?\\] ?", "").trim();
								}
								//Method A - exact match - find if subject contains a pep title
								String exactMatchPEPTitle = getExactPEPTitleMatch(trimmedSubject);								
								//Method B - find pep titles in subject - do term number exact matching								
								//have to add code to check if ppe number also exists in subject, if so dont add to pep tiles as would be added to pep number list
								//String closestMatchPEPTitle = getClosestPEPTitleMatch(trimmedSubject);
								
								
								//get the pep for the max matched peptitle
								//if (mru.TitleApprximateMatchInSentence(pepTitle, trimmedSubject))  {
								if (exactMatchPEPTitle!=null){		//replace closestMatchPEPTitle
									matchedPEPTitleInMessage = true;		//temp test
									Integer pep = null;
									//GET PEP NUMBER USING PEP TITLE	for storing in database table allpeps					              
							        for (Map.Entry<Integer, String> entry : pepDetails.entrySet()) {
							    	   if (entry.getValue().contains(exactMatchPEPTitle)) {		//replace closestMatchPEPTitle
							               pep = entry.getKey();
							              }				            	   
							        }								        
//**							    System.out.println("\t Pep (" + pep + ") Title approximate (" + closestMatchPEPTitle + ") found in subject: " + line);
							        PEPList.add(pep); // add pep to list
									//add to line list
									linePEPTitleList.add(pep);	
									messagePEPTitleList.add(pep);
								}
								
								//}
							}
							// else if (line.startsWith("+")) { }
							else { // || !line.startsWith("+")
								if (!line.startsWith(">") && !line.startsWith("<") && !line.startsWith("=")
										&& !line.startsWith("@") && !line.endsWith("wrote:"))
									analyseWords = analyseWords + line + "\n"; // for
																				// classification
							}
							if ((mru.matchQuery(line, "Status:")) || (mru.matchQuery(line, "\\+Status:"))) {
								statusLine = line.replace("Status: ", "");
								statusLine = line.replaceAll("[,:;!?(){}+-\\[\\]<>%]", "");
								status = statusLine;
								StatusChanged = true;
							}
							
							// ----------------------------------------------------------------------------------------------------------------------------------------------
							// identifying a new message is stating in text file
							// add here before the next message in file
							
							// check if end of file is reaching in next two lines -for last message in file
							boolean nextLineEOF=false;
							br.mark(20000000); // mark that place
							if(br.readLine() == null)  {  
								nextLineEOF=true; 
							}
							br.reset(); // reset the mark to original
							
							//increment lineCounter
							lineConterWithinFile= lineConterWithinFile+1;
							
							//skip remaining lines from messages longer than 3000 lines
							if(lineConterWithinFile>3000){
								System.out.println("\t line counter " + lineConterWithinFile);
								//keep on reading lines to skip to the end of that message - start of next message
								while ((line = br.readLine()) != null) 
								{
									if (line.startsWith("From ")){
										break;
									}
								}
							}
							//for cases this message was last message in file and eof has been reached, just assign something dummmy
							if (line==null){
								line="From ";	//so that te messsage gets stored
							}
							//System.out.println("\t line content " + line);
							//WRITE MESSAGE TO DB, IF From found or this is last message
							if (line.startsWith("From ") || nextLineEOF) 
							{
									counter++;
									if (date9 != null) 
									{				
										//emove duplicate peps from peplist
										PEPList = mru.removeDuplicates(PEPList); // remove duplicate PEPs
										messagePEPTitleList = mru.removeDuplicates(messagePEPTitleList); 	//remove duplicates from peptitle list
										
										//dont need this as message already contains the from line, see From = line around line 333
										//PEPList = ru.getPepNumberFromSubjectLine(PEPList, subject, pepDetails,pepTitlesSearchKeyList); //try to finding matching pep numbers in the previous subject
										// check in subject
										// if the current pep number in loop exist in subject , then add only those else keep on adding
										messageID++;
										/*	No longer needed as now we use we dont read any more if message linecouter > 3000, 
										if (emailMessage != null && subject != null)  {
											if (emailMessage.length() > 1000000 || subject.contains("[Python-checkins]")) { // additional  keywords like this are
												analyseWords = ""; // set the analysewords column to null
											}
									    }
										*/
										//store the previous message in database
										//for all peps found in message, insert the message
										//MAKE SURE DONT INSERT IF PEP TITLE AND PEP NUMBER BOTH MENTIONED - ACTUALLY IF 
										// PEP NUMBER AND TITLE FOR SAME PEP IS INSERTED TWICE, THE Remove duplicates function removes entry of same pep twice									
										//for (int g = 0; g < PEPList.size(); g++)   //commented, only 									
										
										//check if both lists contain the pep //remove all peps which exist in both, from both
										// checkForDuplicatePepsInList(matchedPEPNumberInMessage, matchedPEPTitleInMessage,matchedPEPTitleInLine);									
										
										boolean inserted = false;
										//insert in found in peplist
										for (int g = 0; g < PEPList.size(); g++)
										{
											String tableName = "allmessages";
											System.out.println("\tGoing to insert data into database PEP = " + PEPList.get(g) + " | "+  tableName + " | " +  rootFolder + " | "+ fileEntry.getName() + " | " + messageID );
											mwu.storeInDatabase(tableName,counter, date9,dateTimeStamp, subject, rootFolder, fileEntry.getName(),lastLine, emailMessage, PEPList.get(g), status, statusFrom, 
													statusTo,type, author, senderName, inReplyTo, references, emailMessageId, link,StatusChanged, wordsFoundList, analyseWords, 
													messageID, required, conn);											
											inserted = true; 
										}
										//just for debugging ..insert the pep messages which only have pep title 
										//for (int g = 0; g < messagePEPTitleList.size(); g++)
										for (int g : messagePEPTitleList)
										{
											//debug info 
//**											System.out.println(" list to string " + messagePEPTitleList.toString());
//**											System.out.println("\tmessagePEPTitleList.size() " + messagePEPTitleList.size() + " g " + g);
											String tableName = "allmessages_pepTitleOnly";
											System.out.println("\tGoing to insert data into database with pep title =  " + g + " | "+  tableName + " | " +  rootFolder + " | "+ fileEntry.getName() + " | " + messageID );
											mwu.storeInDatabase(tableName,counter, date9,dateTimeStamp, subject, rootFolder, fileEntry.getName(),lastLine, emailMessage, g, status, statusFrom, 
													statusTo,type, author, senderName, inReplyTo, references, emailMessageId, link,StatusChanged, wordsFoundList, analyseWords, 
													messageID, required, conn);											
											inserted = true;
										}
										//if no pep number or title found for the current message, still insert but with pep number =0
										if (inserted==false){
											Integer pepNumber=-1;	
											String tableName = "allmessages";
											System.out.println("\tGoing to insert data into database no PEP = " + pepNumber + " | "+  tableName + " | " +  rootFolder + " | "+ fileEntry.getName() + " | " + messageID );
											mwu.storeInDatabase(tableName,counter, date9,dateTimeStamp, subject, v_rootfolder, fileEntry.getName(), lastLine, emailMessage, pepNumber, status, statusFrom, statusTo,
													type, author, senderName, inReplyTo, references, emailMessageId, link, StatusChanged, wordsFoundList, analyseWords, messageID, required, conn);
										}
//**										System.out.println("End of One email message --------------------\n");
									} 
									else {
//										System.out.println("DATE NULL date2 " + date2 + " date9 " + date2 + " , " + " i "
//												+ i + " " + rootFolder + "\\" + fileEntry.getName() + " , Line = " + line);
									}									
									// matched is true and from is found
									PEPList.clear(); // clear PEPList
									wordsFoundList = null;
//								}
								emailMessage = ""; // empty message when (matched is true & from is found) but also when not matched - meaning new message has
														// started and searchkey was not found in last message
								matchedPEPNumberInMessage = matchedPEPTitleInMessage = StatusChanged = false; // new emailMessage therefore set matched to null												
								analyseWords = "";
								author = type = statusFrom = statusTo = senderName=references =	inReplyTo = emailMessageId = subject = link = status = null;					
								required = true;
								From = line;
								
								//clear lists
								linePEPTitleList.clear();
								linePEPNumberList.clear();
								matchedPEPNumberInLine = matchedPEPTitleInLine =false; // new emailMessage therefore set matched to null
								
								messagePEPTitleList.clear();
								messagePEPNumberList.clear();	
								
								lineConterWithinFile=0;
							} 
							// if not matched, keep adding lines to message
							else {
								//if searchkey (pep number or pep title) found in last round but end of message not reached, i.e from, 
			  					// then keep on adding lines, only write to db once next From is found
								if (matchedPEPNumberInMessage == true || matchedPEPTitleInMessage==true) 
								{ 
									emailMessage = emailMessage + line + "\n";
									// if (!line.startsWith(">")) analyseWords = analyseWords + line + "\n"; //for classification
								}
							}
							
							//Match pep number and pep title- MATCH PEP NUMBERS FIRST
							matchedPEPNumberInLine = matchPEPNumbersAddToList(folder, v_debugFile, pepNumberSearchKeyList, v_ignoreList, fileEntry, line, matchedPEPNumberInMessage);						
							if (matchedPEPNumberInLine){
								matchedPEPNumberInMessage = true;
								//System.out.println("\tmatchedPEPNumber "  ); 
							}
							//MATCH PEP TITLES SECOND	
							//commented the below block to see the other function works or not 09-03-2017
							//may not need this as we now only look for pep title in subject line
							//in message matching would have been okay, but we not sure in which context it is stated and who is author
							/*
							matchedPEPTitleInLine =  matchPEPTitlesAddToList (folder, v_debugFile, pepTitlesSearchKeyList, pepDetails, fileEntry,line, matchedPEPTitleInMessage);
							if (matchedPEPTitleInLine){
								matchedPEPTitleInMessage = true;
//								System.out.println("\tmatchedPEPTitle "  + matchedPEPTitle); 
							}
							*/
							//NEW GENERAL SCRIPT NOW   11-03-2017
							//as soon as pep title is closest matched, an entry is made tp peplist as well
							//so no need to worry
							
							//NEW SCRIPT - NEW PURPOSE NEW LOGIC
							//just remove duplicates from peplist
							//insert all from peplist 
							//insert all from peptitle, but in diffrent table
							
							//PREVIOUS SCRIPT LOGIC
							// if found pep title only, insert
					        // if found pep number only, dont insert as inserted in initial script
					        // if found both dont insert
							
							//check if pep number doesnt exist for pep which's title has been found, 
							// (REMOVE THIS SECTION IF YOU DONT WANT TO READ IN JUST PEP TITLES)
							
							//Previous logic ...not needed in this script
							//if both are matched
					        /*
							if (matchedPEPNumberInLine && matchedPEPTitleInLine){	
					        	removeFromPEPList(matchedPEPTitleInLine);				        	
					        }
					        */
					        // for lines not having from and if not matched, keep on adding lines to emailmessage	
							if (matchedPEPNumberInMessage == false && matchedPEPTitleInMessage==false) {
								emailMessage = emailMessage + line + "\n"; 			//emailMessage = emailMessage + " b " + line + "\n"; 																																									
								// if (!line.startsWith(">")) analyseWords =
								// analyseWords + line + "\n"; //for classification
							}
							//NEED TO WRITE THE LAST MESSAGE AS From has not been reached							
							
						}//end while readinLine
						
						//WE HAVE TO WRITE THE LAST MESSAGE AS NO FROM MESSAGE FOUND
						
						br.close();   //close andutd  release memory
					} // end if its a file					
					v_fileCounter++;
				} // end else (For each file)
			} // end for
		} // end method

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

	private static void sleepGarbageCollect() {
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
	
	//for each message run this script for Exact matching pep title in subject
	private static String getExactPEPTitleMatch(String line) {
		String noMatch=null;
		String pepTitle;
		Boolean lengthMatch=false;		
		
		if (line==null || line.isEmpty())
		{}
		else{			
			//for punctuation, replace every punctuation with double space
			//and after the above processing, 
			//while string contains double space,  replace double spaces with single space
			line = replacePunctuationCorrectly(line);
			try {				
				for (String OriginalPEPTitle : pepTitlesSearchKeyList) 
				{	
					lengthMatch=false;
					pepTitle=OriginalPEPTitle;
					if (line!=null ){								
						//Method B - exact match 
						pepTitle = replacePunctuationCorrectly(pepTitle);		//remove all the different punctuations first 
						//also check number of terms..match no of terms as well
						//if peptitle is in subject but with additional two terms only, then match
						
						//checks lengths of both
						if(line.split(" ").length == pepTitle.split(" ").length){
							lengthMatch=true;
						}							
						
						if (lengthMatch && line.contains(pepTitle)){
							return OriginalPEPTitle;		//if pep title has matched
						}
					}
					
				}
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
		
		return noMatch;
	}

	private static String replacePunctuationCorrectly(String str) {
		if (str.contains("."))
			str = str.replaceAll(".", " ");
		str = str.replaceAll("[^a-zA-Z ]", " ").toLowerCase();			//remove all the different punctuations first 
		while(str.contains("  "))			//replace double spaces with single space
			str = str.replaceAll("  ", " ");
		str = str.toLowerCase().trim();
		return str;
	}	
	
	
	//for each message run this script
	private static String getClosestPEPTitleMatch(String line) {
		String pepTitleWithClosestMatch=null, pepTitle=null;
		Integer maxTermCounter=0;		
		Boolean stopwordsMatched=false, allPepTitleTermsMatched =false;
		
		if (line==null || line.isEmpty())
		{}
		else{
			line = line.toLowerCase();
			line= line.trim();			
			line= line.replaceAll("'","");	//remove  '
			line= line.replaceAll(",","");
		
//		if (line.contains("[") && line.contains("]"))		{
//			line = line.replaceAll("\\[.*?\\] ?", "");
//		}		
			try {				
				for (String OriginalPEPTitle : pepTitlesSearchKeyList) {
					pepTitle=OriginalPEPTitle;
					//peptitle to lowercase already done when reading it in from db table to map in void main
					//Method A - direct match
					/*
					if (line.contains(pepTitle)) // while (m.find() || m9.find())
					{   
	//					pepTitleWithClosestMatch = pepTitle;
	//			       	System.out.println("\nPep Title (" + pepTitle + ") found in message line:" + line);
					} 
					*/
					
					//Method B find intersection - number of terms - in any order
					//in the following we find how many individual terms match in both, it will remove all the different punctuations first - http://stackoverflow.com/questions/14219084/comparing-two-strings-in-java-and-identifying-duplicate-words									
					Integer termFCounter=0;		//for all terms in sentence
					Splitter splitter = Splitter.onPattern("\\W").trimResults().omitEmptyStrings();
					Set<String> intersection = Sets.intersection(//
							Sets.newHashSet(splitter.split(pepTitle)), //
							Sets.newHashSet(splitter.split(line)));
					termFCounter = intersection.size();					
					allPepTitleTermsMatched = intersection.size() == OriginalPEPTitle.split(" ").length;
				
					//Method C - just match
					/*
					pepTitle = pepTitle.replaceAll("[^a-zA-Z ]", "").toLowerCase();
					line = line.replaceAll("[^a-zA-Z ]", "").toLowerCase();
					if (line.contains(pepTitle))
						allPepTitleTermsMatched=true;					
					*/					
					//Method D Percentage match
					/*  newest code does direct match so not needed
					float diff = 100* ( (float)termFCounter/ (float) pepTitle.split(" ").length);				
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
					*/
					
					//if matched by percentage (80 %) or term counter (>=3)
					if ( allPepTitleTermsMatched) { //percentage ){		// || iftermFCounter
	//**					System.out.println("\nNew subject line processed, line: ("+line+") pepTitle (" +pepTitle+ ") MATCHED percentage " + percentage + " iftermFCounter "+ iftermFCounter );
		//				System.out.println(" matched percentage " + percentage +" termFoundCounter " + termFoundCounter + " iftermFCounter " + iftermFCounter  + " title, " + pepTitle + " sentence " +sentence);
						pepTitleWithClosestMatch = OriginalPEPTitle;
						//break;
						System.out.println(" pepTitleWithClosestMatch " + pepTitleWithClosestMatch);
						return pepTitleWithClosestMatch;
						/* newest code does direct match so not needed
						//now try and find the closest match pep title
						if (termFCounter > maxTermCounter){
							pepTitleWithClosestMatch = pepTitle;
	//**						System.out.println("\tOveratken max " +pepTitle + " maxTermCounter " + maxTermCounter);
						}
						*/
					}
					else{
						//pepTitleWithClosestMatch = null;
						//System.out.println("\nNo peptitle matched in subject "); 
					}
					//percentage title match for subject		//titles  //line
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return pepTitleWithClosestMatch;
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

	private static void checkForDuplicatePepsInList(Boolean matchedPEPNumberInMessage, Boolean matchedPEPTitleInMessage, Boolean matchedPEPTitleInLine) {
		//if both are matched
		if (matchedPEPNumberInMessage && matchedPEPTitleInMessage) {
			//remove all peps which exist in both, from both
//											System.out.println("\tmatched as number and title in message");
			removeFromMessagePEPList(matchedPEPTitleInLine);										
		} 
		//if only pep number is found, dont insert, so have to remove everything from pepNumberList
		else if (matchedPEPNumberInMessage && !matchedPEPTitleInMessage) {
			PEPList.clear();
//											System.out.println("\tmatched PEPNumber In Message");
		}
		//else if only pep title is found, let it/them be inserted
		else if (matchedPEPTitleInMessage && !matchedPEPTitleInMessage) {
//											System.out.println("\tmatched PEPTitle In Message");
		} else {
//											System.out.println("\tnot matched as number and title in message");
		}
	}
	
	// if same pep is added in both lists, then dont add in database - just
	// remove those peps from title list which are found in pep number list
	private static void removeFromMessagePEPList(Boolean matchedPEPTitleInMessage) {	
		// Prepare an intersection
		List<Integer> intersection = new ArrayList<Integer>(messagePEPTitleList);
		// retain only the common ones in both
		intersection.retainAll(messagePEPNumberList);
		// find a way to remove these common ones from both lists
		messagePEPNumberList.removeAll(intersection);
		messagePEPTitleList.removeAll(intersection);
		PEPList.removeAll(intersection);
		
		intersection.clear();
	}
	
	private static void removeFromPEPList(Boolean matchedPEPTitleInLine) {
		List<Integer> intersection = new ArrayList<Integer>(linePEPTitleList);
		// retain only the common ones in both
		intersection.retainAll(linePEPNumberList);
		// find a way to remove these common ones from both lists
		// messagePEPNumberList.removeAll(intersection);
		linePEPTitleList.removeAll(intersection);
		PEPList.removeAll(intersection);
		
		intersection.clear();
	}
	/*
	private static void removeFromPEPListOLD(Boolean matchedPEPTitleInLine) {
		// System.out.println("\t in fn a");
		// if same pep is added in both lists, then dont add in database
		// just remove those peps from title list which are found in pep number
		// list
		for (int k = 0; k < linePEPTitleList.size(); ++k)
			for (int l = 0; l < linePEPNumberList.size(); ++l)
				if (linePEPNumberList.get(l).equals(linePEPTitleList.get(k))) {
					Integer pepToRemove = linePEPNumberList.get(l);
					linePEPTitleList.remove(k);
					for (int u = 0; u < PEPList.size(); ++u)
						if (PEPList.get(u).equals(pepToRemove))
							PEPList.remove(u);
				}
	}
	*/
	// if both lists contain the pep, remove all peps which exist in both, from
	// both
	private static void removeFromMessagePEPListoldOLD(Boolean matchedPEPTitleInMessage) {
		// System.out.println("\t in fn b");
		// if same pep is added in both lists, then dont add in database
		// just remove those peps from title list which are found in pep number
		// list
		for (int k = 0; k < messagePEPTitleList.size(); ++k)
			for (int l = 0; l < messagePEPNumberList.size(); ++l)
				if (messagePEPNumberList.get(l).equals(messagePEPTitleList.get(k))) {
					Integer pepToRemove = messagePEPNumberList.get(l);
					messagePEPTitleList.remove(k);
					messagePEPNumberList.remove(l);
					--k;
					--l;
					for (Iterator<Integer> ¢ = PEPList.iterator(); ¢.hasNext();)
						if (¢.next().equals(pepToRemove))
							¢.remove();
				}
	}
	
	//this function is no longer being used as we dont consider pep title in each line of message, only in subject line
	private static Boolean matchPEPTitlesAddToList(final File folder, Writer v_debugFile,List<String> v_titleSearchKeyList, Map<Integer, String> pepDetails, final File fileEntry, String line,
			Boolean matchedPEPTitle) {
		String location, lastLine;
		Matcher m5;
		for (String pepTitle : v_titleSearchKeyList) {		
			//for each pattern found
			if (line.contains(pepTitle)) // while (m.find() || m9.find())
			{
				//withinFileCounter++;
				foundInFile = true;
				matchedPEPTitle = true; // if searchkey found, set matched to true
				Integer pep = null;
				//GET PEP NUMBER USING PEP TITLE	for storing in database table allpeps					              
		        for (Map.Entry<Integer, String> entry : pepDetails.entrySet()) {
		    	   if (entry.getValue().contains(pepTitle)) {
		               pep = entry.getKey();
		              }				            	   
		        }
		       	System.out.println("Pep (" + pep + ") Title (" + pepTitle + ") found in message line:" + line);
		        	        
				PEPList.add(pep); // add pep to list
				linePEPTitleList.add(pep);
				messagePEPTitleList.add(pep);				
			} // end while
			
		} // end for
		return matchedPEPTitle;
	}
	
	private static Boolean matchPEPNumbersAddToList(final File folder, Writer v_debugFile, List<String> v_searchKeyList, List<String> v_ignoreList, final File fileEntry, String line, Boolean $) {
		String location, lastLine;
		Matcher m5;
		for (String pattern : v_searchKeyList) {
			m5 = Pattern.compile(pattern).matcher(line);
			while (m5.find()) // while (m.find() || m9.find())
			{
				boolean Store = true;
				Matcher m5a = null;
				for (String pattern2a : v_ignoreList) { // so many combinations	we cant store e.g.	PEP 312, if we looking for PEP: 3 only
					m5a = Pattern.compile(pattern2a).matcher(line);
					while (m5a.find())
						Store = false;
				}
				// end added
				if (Store) {
					$ = foundInFile = true; // if searchkey found, set matched
											// to true
					String pepString;
					Integer pep;
					String str = pattern.replaceAll("[^-?0-9]+", " ");
					// System.out.println("Added " +
					// Arrays.asList(str.trim().split(" ")));
					// IMP MAYBE System.out.println("m5 " + m5.group(0) );
					pepString = m5.group(0).replace("PEP", "");
					// System.out.println("here a pepString " + pepString);
					// pepString = m5.group(0).replace("PEP ","");
					// pepString = pepString;
					pepString = pepString.replace(".txt", "");// ??????
					pepString = pepString.replace("pep-", "");
					pepString = pepString.replace(" ", "");
					pepString = pepString.replaceAll("[,.:;!?(){}\\[\\]<>%]", ""); // remove
																					// punctuation
					// System.out.println("here b pepString " + pepString);
					pepString = pepString.replace("txt", "");
					pep = Integer.parseInt(pepString);
//**					System.out.println("Pep (" + pep + ") Number (" + pattern + ") found in "
//**							+ (pattern.contains("Subject:") ? "subject: " : "message line:") + line);
					PEPList.add(pep); // add pep to list
					// System.out.println("added PEP in both d " + pep);
					// add to line list
					linePEPNumberList.add(pep);
					messagePEPNumberList.add(pep);
					// }
					lastLine = line;
					location = folder + "\\" + fileEntry.getName();
					// WHY THIS LINE BELOW??
					// emailMessage = emailMessage + " a " + line + "\n";
					mwu.storeInDebugFile(v_debugFile, "pattern " + pattern + " line" + line);
				}
			} // end while
		}
		return $;
	}
} // end class