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
import java.sql.*;

import javax.swing.JOptionPane;

import miner.process.PythonSpecificMessageProcessing;
import utilities.GetDate;
import utilities.ReturnNattyTimeStamp;

//This class contains all code to read python archives, gmane and mail archive
//it reads pep numbers and titles
//it reads all in one shot and its can be run separately 
//Refer to place where it says MAIN CODE HERE


public class _ReadAllPeps_NumbersAndTitlesFromAllLists 
{
	 // JDBC driver name and database URL
  static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
  static final String DB_URL = "jdbc:mysql://localhost/peps";

    //  Database credentials
  static final String USER = "root";
  static final String PASS = "root";
    
  public static File dev_folder = new File("C:/data/python-dev/");
  public static File patches_folder = new File("C:/data/python-patches/");
  public static File lists_folder = new File("C:/data/python-lists/");
  public static File bugs_list_folder = new File("C:/data/python-bugs-list/");
  public static File committers_folder = new File("C:/data/python-committers/");
  public static File checkins_folder = new File("C:/data/python-checkins/");
  public static File ideas_folder = new File("C:/data/python-ideas/");
  public static File announce_folder = new File("C:/data/python-announce-list/");
  public static File distutils_sig_folder = new File("C:/data/python-distutils-sig/");
  
  public static File gmane_folder = new File("C:/scripts/gmanetxtd/");
  //when only few messages to be read in 
  //new db table = allpeps_gmane
  //but when reading in next timw with all data, try have diff db with same tablename = allpeps, as select statement are coded.
  public static File few_folder = new File("C:/scripts/fewMsgtxt/");
    
  public static File dev_folder2 = new File("C:/data/dev/");
  static Connection conn = null;
  
  static String temp = "";
  //static String searchKey = "PEP 301 ";sss
  static String searchKeyAll = null;
  //static StringpepTitlesSearchKeyList = null;
  
  static List<String> wordsList = new ArrayList<String>();
  static List<String> ignoreList = new ArrayList<String>();
  static List<String> statusList = new ArrayList<String>();
  //additional added
  static List<String> pepNumberSearchKeyList = new ArrayList<String>();
  static List<String> pepTitlesSearchKeyList = new ArrayList<String>();
  
  
  static int fileCounter = 0;                  //total number of files with searchKey 
 // static HashMap hm = new HashMap();
  
  static boolean foundInFile = false;
  
  //since we inserting more data, we want to start at different number
  //initially all data inserted with mid started at 0
  //then extra states addition started at 500000, so we start at 600,000 
  static Integer messageID = 0; 
  
  static List<Integer> PEPList = new ArrayList<Integer>();
  //line lists
  //store pep number and pep title in this list and clear it after every line is read
  static List<Integer> linePEPNumberList = new ArrayList<Integer>();
  static List<Integer> linePEPTitleList = new ArrayList<Integer>();
  //store pep number and pep title in this list and clear it after every message is read
  static List<Integer> messagePEPNumberList = new ArrayList<Integer>();
  static List<Integer> messagePEPTitleList = new ArrayList<Integer>();
  
  static long t0,t1, t2, t3, t4, t5, t6, t7, t8;
  
  Boolean readPEPTitle = true;		//if we just want to read pepTitles and not pep numbers, dont insert into db if pep numbers exist
  Boolean readPEPNumbers = false; 	//if we just want to read numbers and not pep titles, dont insert into db if pep titles exist
  
  //private static final Pattern UNDESIRABLES = Pattern.compile("[][(){},.;!?<>%]");
  
  static MessageReadingUtils mru = new MessageReadingUtils();
  static MessageWritingUtils mwu = new MessageWritingUtils();

  static PythonSpecificMessageProcessing pms = new PythonSpecificMessageProcessing();
  
  static ReturnNattyTimeStamp rts = new ReturnNattyTimeStamp();
  
  static GetDate gd = new GetDate();  //new date parser centrally located in seprate package for use from all other package code
  
  public static void main(String[] args) throws IOException {
	  
	  //emptyDatabase_gmane();
	  //initialise pep numbers, in pepNumberSearchKeyList and ignoreList
	  mru.initPepNumberSearchKeyLists(statusList,ignoreList, pepNumberSearchKeyList);   
	  //initialise pepTitles
	  pepTitlesSearchKeyList.clear();
	  int i = 0;
	  //map to store pep titles
	  Map<Integer, String> pepDetails = new HashMap<Integer, String>();
	  //get pep and title from db and return as map and show
	  pepDetails = mru.populateAllPepTitleandNumbers(pepDetails, conn);   
	  addEntriesToList(pepDetails);
	  
	  //System.out.println(searchKeyAllA.get(1));	  
	  File statText = new File("c:/data/output" + i + ".txt");		 
	  //output file declaration		
	  FileOutputStream is = new FileOutputStream(statText);
	  OutputStreamWriter osw = new OutputStreamWriter(is);    
	  Writer w = new BufferedWriter(osw);
	  
	  //DEBUG File
	  File debugText = new File("c:/data/debug.txt");		 
	  //output file declaration		
	  FileOutputStream debug = new FileOutputStream(debugText);
	  OutputStreamWriter debugosw = new OutputStreamWriter(debug);    
	  Writer wdebug = new BufferedWriter(debugosw);    
	  // Connection conn = null;
	  searchLists(i, pepDetails, w, wdebug);
	  
	  w.close();
	  // outputResults(conn);
	  System.out.println("Finished processing - Total Elapsed time =" + (t7 - t1)/1000 + " seconds");
  }

private static void searchLists(int i, Map<Integer, String> pepDetails, Writer w, Writer wdebug) throws IOException {
	t0 = System.currentTimeMillis(); 
	  //search all lits, not only ideas folder as messagesd in others lists too sometimes use pep title and not pep number
	  //searchFilesForFolder(ideas_folder, ideas_folder, foundInFile, searchKeyAll,fileCounter, w, wdebug, conn , i,pepTitlesSearchKeyList, ignoreList, statusList, messageID, wordsList,pepDetails );
	  
	  searchFilesForFolder(ideas_folder, ideas_folder, foundInFile, searchKeyAll,fileCounter, w, wdebug, conn , i,pepTitlesSearchKeyList, ignoreList, statusList, messageID, wordsList,pepDetails );
	  t1 = System.currentTimeMillis();
	  System.out.println("Finished processing ideas folder - Elapsed time =" + ((t1 - t0)/1000) + " minutes");

	  searchFilesForFolder(dev_folder, dev_folder, foundInFile, searchKeyAll,fileCounter, w, wdebug, conn , i,pepTitlesSearchKeyList, ignoreList, statusList, messageID, wordsList,pepDetails );
	  t2 = System.currentTimeMillis();
	  System.out.println("Finished processing dev folder - Elapsed time =" + (t2 - t1)/1000 + " seconds");
	  //searchFilesForFolder(patches_folder,patches_folder, foundInFile,  searchKeyAll,fileCounter, w ,wdebug,  conn, i,pepTitlesSearchKeyList, ignoreList, statusList);
	  //System.out.println("Finished processing patches folder");
	  searchFilesForFolder(lists_folder,lists_folder, foundInFile,  searchKeyAll,fileCounter, w ,wdebug,  conn, i,pepTitlesSearchKeyList, ignoreList, statusList, messageID, wordsList,pepDetails );
	  t3 = System.currentTimeMillis();
	  System.out.println("Finished processing lists folder - Elapsed time =" + (t3 - t2)/1000 + " seconds");
	  //searchFilesForFolder(bugs_list_folder,bugs_list_folder, foundInFile,  searchKeyAll,fileCounter, w ,wdebug,  conn, i,pepTitlesSearchKeyList, ignoreList, statusList);
	  //System.out.println("Finished processing bugs lists folder");	    
	  searchFilesForFolder(distutils_sig_folder,distutils_sig_folder , foundInFile,  searchKeyAll,fileCounter, w , wdebug, conn, i,pepTitlesSearchKeyList, ignoreList, statusList, messageID, wordsList,pepDetails );
	  t4 = System.currentTimeMillis();
	  System.out.println("Finished processing distutils_sig_folder folder - Elapsed time =" + (t4 - t3)/1000 + " seconds");  
	  searchFilesForFolder(committers_folder,committers_folder , foundInFile,  searchKeyAll,fileCounter, w , wdebug, conn, i,pepTitlesSearchKeyList, ignoreList, statusList, messageID, wordsList,pepDetails );
	  t5 = System.currentTimeMillis();
	  System.out.println("Finished processing committers folder - Elapsed time =" + (t5 - t4)/1000 + " seconds");
	  searchFilesForFolder(announce_folder,announce_folder , foundInFile,  searchKeyAll,fileCounter, w , wdebug, conn, i,pepTitlesSearchKeyList, ignoreList, statusList, messageID, wordsList,pepDetails );	    
	  t6 = System.currentTimeMillis();
	  System.out.println("Finished processing announce_folder list folder - Elapsed time =" + (t6 - t5)/1000 + " seconds");
	  searchFilesForFolder(checkins_folder,checkins_folder , foundInFile,  searchKeyAll,fileCounter, w , wdebug, conn, i,pepTitlesSearchKeyList, ignoreList, statusList, messageID, wordsList,pepDetails );	
	  t7 = System.currentTimeMillis();
	  System.out.println("Finished processing checkins_folder folder - Elapsed time =" + (t7 - t6)/1000 + " seconds");
	  
}

private static void addEntriesToList(Map<Integer, String> pepDetails) {
	for (Map.Entry<Integer, String> entry : pepDetails.entrySet()) {
//		  System.out.println(entry.getKey() + " / " + entry.getValue());
		  //add to searchkey
		  pepTitlesSearchKeyList.add(entry.getValue());		
	  }
}
  
 
  
  public static void searchFilesForFolder(final File folder, final File v_rootfolder, boolean v_foundInFile,String v_searchKey, int v_fileCounter, Writer v_outputFile, Writer v_debugFile, Connection v_conn, int i, List<String> v_searchKeyList, List<String> v_ignoreList, List<String> v_statusList, Integer v_messageID,
			List<String> v_wordsList,  Map<Integer, String> pepDetails ) throws IOException {
		File rootFolder = v_rootfolder;
		for (final File fileEntry : folder.listFiles()) 
		{
			if (fileEntry.isDirectory()) {
				searchFilesForFolder(fileEntry, v_rootfolder, v_foundInFile, v_searchKey, v_fileCounter, v_outputFile,v_debugFile, v_conn, i, v_searchKeyList, v_ignoreList, v_statusList, v_messageID, v_wordsList,  pepDetails );
			} else {
				// For each File
				System.out.println("------\n Processing New File ");
				if (fileEntry.isFile()) {
					temp = fileEntry.getName();
					// start match
					BufferedReader br = new BufferedReader(new FileReader(folder + "\\" + fileEntry.getName()));
					String line;
					String line2;
					int counter = 0;
					String From = null;
					String emailMessage = null;
					Boolean matchedPEPNumberInMessage = false;
					Boolean matchedPEPTitleInMessage = false;
					String location = null;
					String lastLine = null;
					String date2 = null;
					String status = null;
					String statusLine = null;
					String type = null;
					String author = null;
					Boolean StatusChanged = false;
					String statusFrom = null;
					String statusTo = null;
					String wordsFoundList = null;
					String emailMessageId = null;
					String inReplyTo = null;
					String references = null;
					String link = null;
					String analyseWords = null;
					String subject = null;
					String senderName = null;
					Integer lineCounter;
					Integer notrequiredLinesCounter;
					Boolean required = true;
					Boolean keepOnReading = true;
					int withinFileCounter = 0;
					Date dateTimeStamp = null, date9=null;
					// For each line
					Boolean matchedPEPNumberInLine = false; 
					Boolean matchedPEPTitleInLine  = false;
					while ((line = br.readLine()) != null) 
					{
					    //line lists
						//store pep number and pep title in this list and clear it after every line is read
						linePEPNumberList.clear();
						linePEPTitleList.clear();						
						matchedPEPNumberInLine = false; 
						matchedPEPTitleInLine  = false;
						
						if (line.startsWith("Date: ")){
							date2 = line.replace("Date:", "");
							dateTimeStamp = rts.returnNattyDate(date2);
							date9 = gd.findDate(date2);							
						}
						else if (line.startsWith("Type: "))
							type = line.replace("Type: ", "");
						else if (line.startsWith("Author: "))
							author = line.replace("Author: ", "");
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
						} else if (line.startsWith("On ") && line.endsWith(" wrote:"))
							link = line.replace("On ", ""); // link that could be used - On 16 Jan 2014 11:45,"Carl Meyer" <carl at oddbird.net> wrote:
						else if (line.startsWith("-Status: "))
							statusFrom = line.replace("-Status: ", "");
						else if (line.startsWith("+Status: "))
							statusTo = line.replace("+Status: ", "");
						else if (line.startsWith("From:")) {
							senderName = mru.getSender(line,messageID);
						} else if (line.startsWith("Subject:")) {
							subject = line.replace("Subject: ", "");
							br.mark(10000000); // mark that place
							lineCounter = 0;
							notrequiredLinesCounter = 0;
							keepOnReading = true;
							while (keepOnReading == true && lineCounter < 10 && (line2 = br.readLine()) != null) { // keep reading lijne until come across Message id																													
								// System.out.println(br.markSupported());
								// System.out.println("here b");
								// line2=br.readLine();
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
								}
							} // end loop
							br.reset(); // reset the mark to original
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
						if (line.startsWith("From ")) 
						{
							//System.out.println("heer 0 ");
							//if pep found in previous rounds, then insert in db
							if (matchedPEPNumberInMessage==true || matchedPEPTitleInMessage==true) 
							{	
								counter++;
								if (date9 != null) 
								{									
									PEPList = mru.removeDuplicates(PEPList); // remove duplicate PEPs
									//dont need this as message already contains the from line, see From = line around line 333
									//PEPList = ru.getPepNumberFromSubjectLine(PEPList, subject, pepDetails,pepTitlesSearchKeyList); //try to finding matching pep numbers in the previous subject
									// check in subject
									// if the current pep number in loop exist in subject , then add only those else keep on adding
									messageID++;
									// POST PROCESSING For each message to be inserted in database
									// The following messages cannot be processed - these email Messages are not to be analyzed
									// therefore while reading the message in the database, the column used for
									// analysis is set to null, while the message column is unchanged
									if (emailMessage != null && subject != null)
									{
										if (emailMessage.length() > 1000000 || subject.contains("[Python-checkins]")) // additional  keywords like this are																													
										{										
											analyseWords = null; // set the analysewords column to null
										}
								    }
									//store the previous message in database
									//for all peps found in message, insert the message
									//MAKE SURE DONT INSERT IF PEP TITLE AND PEP NUMBER BOTH MENTIONED - ACTUALLY IF 
									// PEP NUMBER AND TITLE FOR SAME PEP IS INSERTED TWICE, THE Remove duplicates function removes entry of same pep twice									
									//for (int g = 0; g < PEPList.size(); g++)   //commented, only 									
									
									//check if both lists contain the pep //remove all peps which exist in both, from both
									//if both are matched
									if (matchedPEPNumberInMessage && matchedPEPTitleInMessage) {
										//remove all peps which exist in both, from both
//										System.out.println("\tmatched as number and title in message");
										removeFromMessagePEPList(matchedPEPTitleInLine);										
									} 
									//if only pep number is found, dont insert, so have to remove everything from pepNumberList
									else if (matchedPEPNumberInMessage && !matchedPEPTitleInMessage) {
										PEPList.clear();
//										System.out.println("\tmatched PEPNumber In Message");
									}
									//else if only pep title is found, let it/them be inserted
									else if (matchedPEPTitleInMessage && !matchedPEPTitleInMessage) {
//										System.out.println("\tmatched PEPTitle In Message");
									} else {
//										System.out.println("\tnot matched as number and title in message");
									}									
									
									for (int g = 0; g < PEPList.size(); g++)
									{	
										// g below = pep number from list																			
										// insert into database
										//System.out.println("\tnot 1");
										String tableName = "allmessages";
										mwu.storeInDatabase(tableName,counter, date9,dateTimeStamp, subject, rootFolder, fileEntry.getName(),lastLine, emailMessage, PEPList.get(g), status, statusFrom, 
												statusTo,type, author, senderName, inReplyTo, references, emailMessageId, link,StatusChanged, wordsFoundList, analyseWords, 
												messageID, required, conn);
									}
									//if we running this script, there is no need to read the messages with no ppe number or pep title
									
									// System.out.println("-------------End of
									// One email message
									// --------------------\n");
								} else {
									System.out.println("DATE NULL date2 " + date2 + " date9 " + date2 + " , " + " i "
											+ i + " " + rootFolder + "\\" + fileEntry.getName() + " , Line = " + line);
								}
								// emailMessage = null; //empty message once
								// matched is true and from is found
								PEPList.clear(); // clear PEPList
								wordsFoundList = null;
							}
							emailMessage = null; // empty message when (matched is true & from is found) but also when not matched - meaning new message has
													// started and searchkey was not found in last message
							matchedPEPNumberInMessage = false; // new emailMessage therefore set matched to null
							matchedPEPTitleInMessage  = false;  												
							analyseWords = null;
							author = null;
							type = null;
							statusFrom = null;
							statusTo = null;
							senderName = null;
							references = null;
							inReplyTo = null;
							emailMessageId = null;
							subject = null;
							link = null;
							StatusChanged = false;
							status = null;
							required = true;
							From = line;
							
							//clear lists
							linePEPTitleList.clear();
							linePEPNumberList.clear();
							matchedPEPNumberInLine = false; // new emailMessage therefore set matched to null
							matchedPEPTitleInLine  = false;
							
							messagePEPTitleList.clear();
							messagePEPNumberList.clear();
							System.out.println("Start reading new message-------"); 
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
						
						//FOR ALL OTHER INSTANCES WHEN LINE DOES NOT CONTAIN ANY TERMS CAPTURED ABOVE						
						//MAIN CODE HERE WHERE MATCHING TAKES PLACE - IN THE BODY OF MESSAGE						
						Matcher m5 = null;
						Matcher mw = null;
						
						//MATCH PEP NUMBERS FIRST
						//comment temporarily
						matchedPEPNumberInLine = matchPEPNumbersAddToList(folder, v_debugFile, pepNumberSearchKeyList, v_ignoreList, fileEntry, line, matchedPEPNumberInMessage);						
						if (matchedPEPNumberInLine){
							matchedPEPNumberInMessage = true;
							//System.out.println("\tmatchedPEPNumber "  ); 
						}
						//MATCH PEP TITLES SECOND						
						matchedPEPTitleInLine =  matchPEPTitlesAddToList (folder, v_debugFile, pepTitlesSearchKeyList, pepDetails, fileEntry,line, matchedPEPTitleInMessage);
						if (matchedPEPTitleInLine){
							matchedPEPTitleInMessage = true;
//							System.out.println("\tmatchedPEPTitle "  + matchedPEPTitle); 
						}
						// if found pep title only, insert
				        // if found pep number only, dont insert as inserted in initial script
				        // if found both dont insert
						
						//check if pep number doesnt exist for pep which's title has been found, 
						// (REMOVE THIS SECTION IF YOU DONT WANT TO READ IN JUST PEP TITLES)
						
						//if both are matched
				        if (matchedPEPNumberInLine && matchedPEPTitleInLine){	
				        	removeFromPEPList(matchedPEPTitleInLine);				        	
				        }				        	
				        // for lines not having from and if not matched, keep on adding lines to emailmessage	
						if (matchedPEPNumberInMessage == false && matchedPEPTitleInMessage==false) {
							emailMessage = emailMessage + line + "\n"; 			//emailMessage = emailMessage + " b " + line + "\n"; 																																									
							// if (!line.startsWith(">")) analyseWords =
							// analyseWords + line + "\n"; //for classification
						}
					}//end while readinLine
					
				} // end if its a file
				if (foundInFile = true)
					;
				v_fileCounter++;
			} // end else (For each file)
		} // end for
	} // end method
  
  private static void removeFromMessagePEPList(Boolean matchedPEPTitleInMessage) {
		//if same pep is added in both lists, then dont add in database - just remove those peps from title list which are found in pep number list
		// Prepare an intersection
		List<Integer> intersection = new ArrayList<Integer>(messagePEPTitleList);
		//retain only the common ones in both
		intersection.retainAll(messagePEPNumberList);
		//find a way to remove these common ones from both lists
		messagePEPNumberList.removeAll(intersection);
		messagePEPTitleList.removeAll(intersection);
		PEPList.removeAll(intersection);
	}  
  
  private static void removeFromPEPList(Boolean matchedPEPTitleInLine) {
	  List<Integer> intersection = new ArrayList<Integer>(linePEPTitleList);
		//retain only the common ones in both
		intersection.retainAll(linePEPNumberList);
		//find a way to remove these common ones from both lists
		//messagePEPNumberList.removeAll(intersection);
		linePEPTitleList.removeAll(intersection);
		PEPList.removeAll(intersection);
  }
  
private static void removeFromPEPListOLD(Boolean matchedPEPTitleInLine) {
//	System.out.println("\t in fn a"); 
	//if same pep is added in both lists, then dont add in database
	//just remove those peps from title list which are found in pep number list
	for(int k=0; k < linePEPTitleList.size(); k++){
//		System.out.println("\t\t\t linePEPTitleList: " + linePEPTitleList.get(k));
		for(int l=0; l < linePEPNumberList.size(); l++){
//			System.out.println("\t\t\t linePEPNumberList: " +linePEPNumberList.get(l));
			if(linePEPNumberList.get(l).equals(linePEPTitleList.get(k))  ){
//				System.out.println("\t\t\t =same match ");
				Integer pepToRemove = linePEPNumberList.get(l);
				linePEPTitleList.remove(k);
//				System.out.println("\t j removed PEPTitle "  + matchedPEPTitleInLine); 
				//now remove the same element from main peplist which containsa all peps in message
				for(int u=0; u< PEPList.size(); u++)
				{
//					System.out.println("\t\t PepList items "  + PEPList.get(u)); 
					if (PEPList.get(u).equals(pepToRemove)){
						PEPList.remove(u);
						//if (!PEPList.isEmpty())
						//	PEPList.remove(u);
//						System.out.println("\t\t k removed PEPNumber "  + pepToRemove); 
						//u--;
					}
					
				}
				
			}
		}
	}
}

// if both lists contain the pep, remove all peps which exist in both, from both 
private static void removeFromMessagePEPListoldOLD(Boolean matchedPEPTitleInMessage) {
//	System.out.println("\t in fn b"); 
	//if same pep is added in both lists, then dont add in database
	//just remove those peps from title list which are found in pep number list
	for(int k=0; k < messagePEPTitleList.size(); k++){
//xx		System.out.println("\t\t\t messagePEPTitleList: " + messagePEPTitleList.get(k));
		for(int l=0; l < messagePEPNumberList.size(); l++){
//			System.out.println("\t\t\t messagePEPNumberList: " +messagePEPNumberList.get(l));
			if(messagePEPNumberList.get(l).equals(messagePEPTitleList.get(k))  ){
//				System.out.println("\t\t\t =same match ");
				Integer pepToRemove = messagePEPNumberList.get(l);
				//we remove from both, because if its exists in both, then it wud have been already inserted in original script
				messagePEPTitleList.remove(k);
				messagePEPNumberList.remove(l);
				k--;
				l--;
//				System.out.println("\t j removed PEPTitle "  + matchedPEPTitleInMessage); 
				//now remove the same element from main peplist which containsa all peps in message
				
//				for(int u=0; u< PEPList.size(); u++)
//				{
//					System.out.println("\t\t PepList items "  + PEPList.get(u)); 
//					if (PEPList.get(u).equals(pepToRemove)){
//						PEPList.remove(u);
//						//if (!PEPList.isEmpty()){
//						//	PEPList.remove(u); }
//						//u--;
//						System.out.println("\t\t k removed PEPNumber "  + pepToRemove); 
//					}
//				}
				//now remove the same element from main peplist which contains a all peps in message
				for (Iterator<Integer> iterator = PEPList.iterator(); iterator.hasNext(); ) {
//					System.out.println("a");
					Integer fr = iterator.next();
//xx					System.out.println("\t\t PepList items " +fr ); 				    
				    if (fr.equals(pepToRemove)) {
				        iterator.remove();
//				        System.out.println("\t\t k removed PEPNumber "  + pepToRemove); 
				    }
				   // System.out.println(fr);
				}
				//System.out.println("\tnot 3");
			}
			//System.out.println("\tnot 4");
		}		
		//System.out.println("\tnot 5");
	}	
	//System.out.println("\tnot 6");
}

private static Boolean matchPEPTitlesAddToList(final File folder, Writer v_debugFile, List<String> v_titleSearchKeyList,Map<Integer, String> pepDetails, final File fileEntry, String line, Boolean matchedPEPTitle) 
{	
	//Boolean matched = false;
	String location;
	String lastLine;
	Matcher m5;
	for (String pattern : v_titleSearchKeyList) {
		// System.out.println("pattern " + pattern);
		Pattern p5 = Pattern.compile(pattern);
		m5 = p5.matcher(line);
		//for each pattern found
		while (m5.find()) // while (m.find() || m9.find())
		{
			//withinFileCounter++;
			foundInFile = true;
			matchedPEPTitle = true; // if searchkey found, set matched to true
			String pepTitle;
			pepTitle = pattern;
			Integer pep = null;
			//GET PEP NUMBER USING PEP TITLE	for storing in database table allpeps					              
	        for (Map.Entry<Integer, String> entry : pepDetails.entrySet()) {
	    	   if (entry.getValue().contains(pepTitle)) {
	               pep = entry.getKey();
	              }				            	   
	        }
	        if (pattern.contains("Subject:"))
	        	System.out.println("\tPep (" + pep + ") Title (" + pattern + ") found in subject: " + line);
	        else
	        	System.out.println("\tPep (" + pep + ") Title (" + pattern + ") found in message line:" + line);
	        	        
			PEPList.add(pep); // add pep to list
//			System.out.println("added PEP in both c " + pep);
			//add to line list
			linePEPTitleList.add(pep);
			messagePEPTitleList.add(pep);
			
			lastLine = line;
			location = folder + "\\" + fileEntry.getName();
			// WHY THIS LINE BELOW??
			//emailMessage = emailMessage + " a " + line + "\n";
			// System.out.println("store pattern " + pattern
			// + " line " + line);
			String de = "pattern " + pattern + " line" + line;
			mwu.storeInDebugFile(v_debugFile, de);
		} // end while
		
		//percentage match		//titles  //line
		//only do for subject				
		if (line.toLowerCase().contains("subject:")  && mru.TitleApprximateMatchInSentence(pattern, line))
		{
			String pepTitle;
			pepTitle = pattern;
			Integer pep = null;
			//GET PEP NUMBER USING PEP TITLE	for storing in database table allpeps					              
	        for (Map.Entry<Integer, String> entry : pepDetails.entrySet()) {
	    	   if (entry.getValue().contains(pepTitle)) {
	               pep = entry.getKey();
	              }				            	   
	        }
	        if (pattern.contains("Subject:"))
	        	System.out.println("\tPep (" + pep + ") Title approximate (" + pattern + ") found in subject: " + line);
	        else
	        	System.out.println("\tPep (" + pep + ") Title approximate (" + pattern + ") found in message line:" + line);
			PEPList.add(pep); // add pep to list
//			System.out.println("added PEP in both b " + pep);
			//add to line list
			linePEPTitleList.add(pep);	
			messagePEPTitleList.add(pep);	
		}
		
		
	} // end for
	return matchedPEPTitle;
}

private static Boolean matchPEPNumbersAddToList(final File folder, Writer v_debugFile, List<String> v_searchKeyList,
		List<String> v_ignoreList, final File fileEntry, String line, Boolean matchedPEPNumber) {
	//Boolean matched = false;
	String location;
	String lastLine;
	Matcher m5;
	for (String pattern : v_searchKeyList){
//			        		  System.out.println("pattern " + pattern);
	      Pattern p5 = Pattern.compile(pattern);
	      m5 = p5.matcher(line);  
		  while (m5.find()) //  while (m.find() || m9.find()) 
		  {
			  boolean Store = true;            			  
		  	  Matcher m5a = null;            		  	  
		  	  for (String pattern2a : v_ignoreList){               //so many combinations we cant store e.g. PEP 312, if we looking for PEP: 3 only
		  		  Pattern p2a = Pattern.compile(pattern2a);
		  		  m5a = p2a.matcher(line);  
		  		  while (m5a.find()) //while (m.find() || m9.find()) 
		  		  {
		  			Store = false;
  //           		  			System.out.println("dont store pattern " + pattern + " line " + line);
	  			  }
		  	  }   
			  	  
			  //end added
		      if (Store == true){           		      
        		 // withinFileCounter++;
        		  foundInFile = true;  
        		  matchedPEPNumber = true;                   //if searchkey found, set matched to true
        		  String pepString;
        		  Integer pep;
        		  String str = pattern;      
        		  str = str.replaceAll("[^-?0-9]+", " "); 
//					            		    System.out.println("Added " + Arrays.asList(str.trim().split(" ")));
//					IMP MAYBE      		    System.out.println("m5 " + m5.group(0) );
        		  pepString = m5.group(0).replace("PEP","");
//					            		    System.out.println("here a pepString " + pepString);
    		   // pepString = m5.group(0).replace("PEP ","");
    		   // pepString = pepString; 	
        		  pepString = pepString.replace(".txt","");//??????
        		  pepString = pepString.replace("pep-","");
        		  pepString = pepString.replace(" ","");
        		  pepString = pepString.replaceAll("[,.:;!?(){}\\[\\]<>%]", "");    //remove punctuation 
//					            		    System.out.println("here b pepString " + pepString);
        		  pep = Integer.parseInt(pepString);
//					            		    System.out.println("here e pep " + pep);
	        		    //add pep only if a duplicate does not exist
/*	            		    boolean duplicateFound = false;
	        		    //Integer item = null;
	        		    for (Integer item : PEPList) { 
	        		    	System.out.println("Bolean duplicatefound = " + duplicateFound + " checking Pep = " + pep + " against item " + item); 
	        			    if (pep == item) {      
	        			    	duplicateFound = true;    // If PEP is not in list, set boolean to true 
	        			    	System.out.println("duplicateFound = true - PEP " + pep);	
	        			    }
	        			    System.out.println("Bolean duplicatefound = " + duplicateFound );
	        			}
*/	  //          		  if (duplicateFound == false) {    // If PEP is not in list, add it to the list
        		  if (pattern.contains("Subject:"))
      	        	System.out.println("Pep (" + pep + ") Number (" + pattern + ") found in subject: " + line);
        		  else
      	        	System.out.println("Pep (" + pep + ") Number (" + pattern + ") found in message line:" + line);
        		    
        		  PEPList.add(pep);   //add pep to list
 //       		  System.out.println("added PEP in both d " + pep);
        		  //add to line list
      			  linePEPNumberList.add(pep);	
      			  messagePEPNumberList.add(pep);	
  //      		  }
        		  lastLine = line; 
        		  location = folder + "\\" + fileEntry.getName(); 
        		  
        		  //WHY THIS LINE BELOW??
        		  
        		  //emailMessage = emailMessage + " a " + line + "\n";
        		  
//					           		      System.out.println("store pattern " + pattern + " line " + line);
        		  String de = "pattern " + pattern + " line" + line;
        		  mwu.storeInDebugFile(v_debugFile, de );
		      }	  
		   } //end while            	  
	 }
	return matchedPEPNumber;
}


  
}		  //end class