package readRepository.readRepository;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Writer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class GenericMailingListReader_Methods extends GenericMailingListReader_BaseElements {
	
	public static void searchFilesForFolder(final File folder, final File v_rootfolder, boolean v_foundInFile,String v_searchKey, int v_fileCounter, Writer v_outputFile, Writer v_debugFile, 
			Connection v_conn, int i, List<String> v_searchKeyList, List<String> v_ignoreList, List<String> v_statusList, Integer v_messageID,
			List<String> v_wordsList,  Map<Integer, String> proposalDetails ) throws IOException {
		rootFolder = v_rootfolder;
		justDate=null;
		patternMatchingDateTimeStamp=null; nattyDateTimeStamp=null; timestampToUse=null;
		Integer lineConterWithinFile=0,msgNumInFile=0;
		
		for (final File fileEntry : folder.listFiles()) 
		{
			if (fileEntry.isDirectory()) {
				searchFilesForFolder(fileEntry, v_rootfolder, v_foundInFile, v_searchKey, v_fileCounter, v_outputFile,v_debugFile, v_conn, i, v_searchKeyList, v_ignoreList, v_statusList, v_messageID, v_wordsList,  proposalDetails );
			} 
			else {	
				if (tm.getMemoryUsageAlmostFull()){
					System.out.println("*********************, Memory Almost Full");			sleepGarbageCollect();		//long process sleep 1 second						
				}
				//getFileSize(fileEntry);					
			
				//use this code only if you are using to restart coding
				//otherwise comment this block and the markFound clause in the subsequent of clause
				//skip some files which cause error in natty or other code
				//if ( rootFolder.toString().contains("authors")) {
				//	 System.exit(0);
				//}
				//skip problem files									
				if ( rootFolder.toString().contains("C:\\datasets\\python-lists") && fileEntry.getName().toString().equals("2018-June.txt")) {
					System.out.println("*********************, skipped file ");
					continue;
				} 
				//core-libs-dev 2018-June.txt
				
				if (restart && !markFound){
					if (rootFolder.toString().contains(markerFolder) && fileEntry.getName().toString().equals(markerFile)){
						markFound=true;							System.out.println("*********************, Mark Found = true");
					} else{
						System.out.println("Checking & Skipping New File in folder " +rootFolder +" \\ "+ fileEntry.getName() + " MarkerFolder "+ markerFolder + " MarkerFile "+ markerFile);
					}
				}					
				
				if (fileEntry.isFile() && markFound) { //){ // && process) {	
					msgNumInFile = 0; //initialise this to zero as first message in file
					temp = fileEntry.getName();		// For each File
					System.out.println("------ Processing New File " +rootFolder +" "+ fileEntry.getName());
					br = new BufferedReader(new FileReader(folder + "\\" + fileEntry.getName()));
					String line, line2, From = null, emailMessage = null;						int counter = 0;						
					matchedproposalNumberInMessage = matchedproposalTitleInMessage = StatusChanged = matchedproposalNumberInLine = matchedproposalTitleInLine  = false;;
					required = keepOnReading = true;								
					location = fromLine = lastLine = dateLine = status = statusLine = type = author = statusFrom = statusTo = wordsFoundList = emailMessageId = inReplyTo = references = link = subject = senderName = null; 
					analyseWords = "";						lineCounter= notrequiredLinesCounter=0;				withinFileCounter = 0; 						lineConterWithinFile=0;
					// For each line						
					while ((line = br.readLine()) != null) 
					{	 //line lists	
						lineproposalNumberList.clear();						//store proposal number and proposal title in this list and clear it after every line is read
						lineproposalTitleList.clear();						
						matchedproposalNumberInLine = matchedproposalTitleInLine=false; 
													
						if (line.startsWith("Date: ")){								
							dateLine = line.replace("Date:", "").trim();	
//$$							System.out.println(" original dateLine " + dateLine);
							//some dates (16 Mar 95 06:08:16 GMT) gives eror while inserting into mysql, so we maybe can add 19 in front of 95, take note of filename also 
							String yearFromFilename = fileEntry.getName().split("-")[0]; //get year part 
							String yearFromDateLine = null;
							if (yearFromFilename.startsWith("19")){	//we only do it for 1990s...
								if(dateLine.contains(" ")) {
									yearFromDateLine = dateLine.split(" ")[2];
								} else {	yearFromDateLine = " "; }
								if (yearFromFilename.startsWith("19") && yearFromDateLine.startsWith("9")) {	
									//we need to add full year to replace just final 2 digits of year
									dateLine = dateLine.replace(yearFromDateLine, yearFromFilename);
	//								System.out.println(" yearFromFilename " + yearFromFilename + "yearFromDateLine" + yearFromDateLine + " new dateLine: "+ dateLine);
								}
							}
							
							// Sat, 15 Nov 2008 11:46:31 -0500 (Eastern Standard Time)
							//if (dateLine.contains("(Eastern Standard Time)"))
							dateLine = dateLine.replaceAll("(Eastern Standard Time)", "");
							dateLine = dateLine.replaceAll("(Eastern Daylight Time)", "");
							dateLine = dateLine.replaceAll("US/Eastern", "");
							
							//Pattern Matching TimeStamp
							patternMatchingDateTimeStamp = gd.findDate(dateLine);
							//BELOW LINE COMMENTED FOR PYTHON LISTS 30 MAR 2017...why? maybe cos of errors
							//Natty Result
							nattyDateTimeStamp = rts.returnNattyDate(dateLine);
							//just the date
							justDate = gd.findDate(dateLine);
							//dateTimeStamp = pms.returnDate(dateLine);
//$$						    System.out.print(" DATELINE " + line + " dateLine " + dateLine + "  justDate " + justDate);
//$$						    System.out.println(", PMDTS: " + patternMatchingDateTimeStamp + ", nattyDTS: " + nattyDateTimeStamp);
							//may 2018 ..added to cater for datetimestamp here rather than in post reading update script	
							if(patternMatchingDateTimeStamp==null){
								if(nattyDateTimeStamp!=null){
									timestampToUse = nattyDateTimeStamp;
									System.out.println("\t\tPattern matching is null, used natty ,mid: " + v_messageID);
								}else{
									System.out.println("\t\tPattern matching and Natty nboth are null, (CONVERTED DATE TO TS) mid: " + v_messageID);
									//here we must try to convert existing date to timestamp
									//Date justdate = null;					
									//Timestamp timestamp = new java.sql.Timestamp(justdate.getTime());
									//timestampToUse= timestamp;
									//USE DATE IN FROM LINE ...From mark@per.dem.csiro.au  Wed May  9 01:53:01 2001
								}				
							}else{
								timestampToUse=patternMatchingDateTimeStamp;
							}
							
							//we convert the "timestamp to use" variable from date into a timestamp
							if (timestampToUse!=null){
								dateTimeStamp = new Timestamp(timestampToUse.getTime());
							}
						}
						else if (line.startsWith("Type: "))   
							type = line.replace("Type: ", "");							
						else if (line.startsWith("In-Reply-To: ")) {
							inReplyTo = line.replace("In-Reply-To: ", "");				inReplyTo = inReplyTo.replaceAll("[,:;!?(){}\\[\\]<>%]", "");
						} else if (line.startsWith("References: ")) {
							references = line.replace("References: ", ""); 				references = references.replaceAll("[,:;!?(){}\\[\\]<>%]", "");
						} // remove punctuation
						else if (line.startsWith("Message-ID: ")) {
							emailMessageId = line.replace("Message-ID: ", "");			emailMessageId = emailMessageId.replaceAll("[,:;!?(){}\\[\\]<>%]", "");
							// System.out.println("emailMessageId" + emailMessageId);
						} 
						else if (line.startsWith("On ") && line.endsWith(" wrote:"))
							link = line.replace("On ", ""); // link that could be used - On 16 Jan 2014 11:45,"Carl Meyer" <carl at oddbird.net> wrote:
//						else if (line.startsWith("-Status: "))
//							statusFrom = line.replace("-Status: ", "");
//						else if (line.startsWith("+Status: "))
//							statusTo = line.replace("+Status: ", "");
						else if (line.startsWith("From:")) {
							senderName = mru.getSender(line,messageID);				author = line.replace("From: ", "");	author =  pms.getAuthorFromString(author);								
						} 
						else if (line.startsWith("Subject:")) {
							subject = line.replace("Subject: ", "");
							br.mark(10000000); // mark that place
							lineCounter = 0;								notrequiredLinesCounter = 0;								
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
								if (line2.endsWith(".txt") || line2.endsWith(".py") || line2.endsWith(".js")	|| line2.endsWith(".css") || line2.endsWith(".ell")
										|| line2.endsWith(".rst") || line2.endsWith(".h") || line2.endsWith(".c")	|| line2.endsWith(".vcproj") || line2.endsWith(".vsprops")
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
							//find if subject contains a proposal title - All Methods, exact and levenstein distance matching 
							// june 2018..extended..what if multiple pep titles foujd in messahe subject?
							//july 2018
							//we also want to allow levenshtein distances matches at different offsets within the message subject
							//as pep 308 subject line = conditional expression resolution while final tile is conditional expressions..so will need levestein distance
							//both, exact and ld are covered in this function - but didnt work last time and neeeded to run the other scruipt which does this 'MatchProposalTitlesToProposalNumbers.java'
							boolean exactMatch = true,levenshteinDistanceMatch=true;
							List<Integer> exactMatchedproposalTitles = null;
							try {
									exactMatchedproposalTitles = mpt.matchFirstAndFinalTitlesWithSubject(0,trimmedSubject,0,exactMatch,levenshteinDistanceMatch, conn);
								 //System.out.println("here 456 size() "+ exactMatchedproposalTitles.size());								
								//get the proposal for the max matched proposaltitle
								//if (mru.TitleApprximateMatchInSentence(proposalTitle, trimmedSubject))  {
								if(exactMatchedproposalTitles != null ) { //so many errors of null pointer
									for(Integer proposal : exactMatchedproposalTitles){		//replace closestMatchproposalTitle
										//System.out.println("here 4561");
										matchedproposalTitleInMessage = true;		//temp test
										/* Integer proposal = null;
										//GET proposal NUMBER USING proposal TITLE	for storing in database table allproposals					              
								         for (Map.Entry<Integer, String> entry : proposalDetails.entrySet()) {
								    	   if (entry.getValue().contains(exactMatchproposalTitle)) {		//replace closestMatchproposalTitle
								               proposal = entry.getKey();
								              }				            	   
								        }	*/							        
		//**							    System.out.println("\t proposal (" + proposal + ") Title approximate (" + closestMatchproposalTitle + ") found in subject: " + line);
								        proposalList.add(proposal); // add proposal to list
										//add to line list
										lineproposalTitleList.add(proposal);	
										//these lists are needed to know where proposal was found..is stored in the allmessages table
										proposalTitleFoundInMessageSubjectList.add(proposal);	
										//do this here as well but within the block
										
									}	
								}
								//aug 2018..put here instead of keeping it inside above loop
								//it didnt work, if i kept it in the lopp..dont know why i kept it there...so i keep a seprate script for it
								//Check for pep # in message subject using different patterms....sub main Proposal number matching - check in proposal number is found in message subject
								//we pass a different list this time - proposalNumberFoundInMessageSubjectList
								boolean matchedproposalNumberInessageSubject = matchproposalNumbersAddToList(proposalNumberSearchKeyList, v_ignoreList, line, matchedproposalNumberInMessage,
										proposalNumberFoundInMessageSubjectList);		
								for(Integer proposal: proposalNumberFoundInMessageSubjectList){		//add to main list	
									matchedproposalNumberInMessage = true;		//temp test								
							        proposalList.add(proposal); // add proposal to list									
									lineproposalTitleList.add(proposal);	//add to line list
									//these lists are needed to know where proposal was found..is stored in the allmessages table								
								}		
								
							} catch (Exception e) {
								System.err.println("Got an sql exception! ");	System.err.println(e.getMessage() + " mid here 43 ");		System.out.println(StackTraceToString(e)  );
							}
								
							//the proposal number would be added to list ..proposalNumberFoundInMessageSubjectList	
						}
						// else if (line.startsWith("+")) { }
						else { // || !line.startsWith("+")
							if (!line.startsWith(">") && !line.startsWith("<") && !line.startsWith("=") && !line.startsWith("@") && !line.endsWith("wrote:"))
								analyseWords = analyseWords + line + "\n"; // for
																			// classification
						}
						if ((mru.matchQuery(line, "Status:")) || (mru.matchQuery(line, "\\+Status:"))) {
							statusLine = line.replace("Status: ", "");		statusLine = line.replaceAll("[,:;!?(){}+-\\[\\]<>%]", "");
							status = statusLine;							StatusChanged = true;
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
						
						lineConterWithinFile= lineConterWithinFile+1;	//increment lineCounter
						
						//skip remaining lines from messages longer than 3000 lines
						if(lineConterWithinFile>3000){
							System.out.println("\t line counter " + lineConterWithinFile);
							//keep on reading lines to skip to the end of that message - start of next message
							while ((line = br.readLine()) != null)	{
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
						if (( line.startsWith("From ") && 	(line.contains("@") || line.contains(" at "))	)		//Some lines within message do start with "From " but are sentences, not from lines 
							|| nextLineEOF){
							fromLine = line;
//$$$								System.out.println("New Message " + fromLine + ",");
							counter++;
							
							//march 2021 ..we want to make sure all messgae sin the the txt files are written to the databasae. A notepadd++ query for 'From: ' in the directory of datafiles
							//showed more instances than the disctinct messages in the database
							storeInDatabase("allmessages_debug",counter, justDate,dateTimeStamp, subject, rootFolder, fileEntry.getName(),lastLine, emailMessage, -2, status, 
									type, author, senderName, inReplyTo, references, emailMessageId, link,proposalTitlefoundInMessageSubject, proposalNumberfoundInMessageSubject, analyseWords, 
									messageID, required,msgNumInFile,fromLine, conn);	
							
							
							// why this line ?... justDate = new Timestamp(new Date().getTime());
							if (dateLine != null) //justDate != null
							{			
//									System.out.println("\t HERE " + line);
								//emove duplicate proposals from proposallist
								proposalList = mru.removeDuplicates(proposalList); // remove duplicate proposals
								proposalTitleFoundInMessageSubjectList = mru.removeDuplicates(proposalTitleFoundInMessageSubjectList); 	//remove duplicates from proposaltitle list
								
								//dont need this as message already contains the from line, see From = line around line 333
								//proposalList = ru.getproposalNumberFromSubjectLine(proposalList, subject, proposalDetails,proposalTitlesSearchKeyList); //try to finding matching proposal numbers in the previous subject
								// check in subject
								// if the current proposal number in loop exist in subject , then add only those else keep on adding
								messageID++;
								/*	No longer needed as now we use we dont read any more if message linecouter > 3000, 
								if (emailMessage != null && subject != null)  {
									if (emailMessage.length() > 1000000 || subject.contains("[Python-checkins]")) { // additional  keywords like this are
										analyseWords = ""; // set the analysewords column to null
									}
							    }
								*/
								//store the previous message in database
								//for all proposals found in message, insert the message
								//MAKE SURE DONT INSERT IF proposal TITLE AND proposal NUMBER BOTH MENTIONED - ACTUALLY IF 
								// proposal NUMBER AND TITLE FOR SAME proposal IS INSERTED TWICE, THE Remove duplicates function removes entry of same proposal twice									
								//for (int g = 0; g < proposalList.size(); g++)   //commented, only 									
								
								//check if both lists contain the proposal //remove all proposals which exist in both, from both
								// checkForDuplicateproposalsInList(matchedproposalNumberInMessage, matchedproposalTitleInMessage,matchedproposalTitleInLine);									
								//May 2018...DATETIMESTAMP is computed within this function
								
								boolean inserted = false;
								//insert in found in proposallist
								for (int g = 0; g < proposalList.size(); g++)
								{
									//may 2018,These fields: [proposalTitlefoundInMessageSubject] now we dont store in seprate table, we just store in same table in three more fields and update it
									proposalTitlefoundInMessageSubject = false;
									for (Integer o : proposalTitleFoundInMessageSubjectList){
										if(proposalList.get(g).equals(o)) {
											proposalTitlefoundInMessageSubject = true;
										}
									}
									proposalNumberfoundInMessageSubject = false;	//we dont need this block of code, as message subject is checked earlier and added to propsallist
									for (Integer p : proposalNumberFoundInMessageSubjectList) {
										if(proposalList.get(g).equals(p)) {
											proposalNumberfoundInMessageSubject = true;
										}
									}
									
									String tableName = "allmessages";
//$$										System.out.println("\t\t\tGoing to insert data into database proposal = " + proposalList.get(g) + " | "+  tableName + " | " +  rootFolder + " | "+ fileEntry.getName() + " | " + messageID + "|" + justDate);
									storeInDatabase(tableName,counter, justDate,dateTimeStamp, subject, rootFolder, fileEntry.getName(),lastLine, emailMessage, proposalList.get(g), status, 
											type, author, senderName, inReplyTo, references, emailMessageId, link,proposalTitlefoundInMessageSubject, proposalNumberfoundInMessageSubject, analyseWords, 
											messageID, required,msgNumInFile,fromLine, conn);											
									inserted = true; 
								}
								proposalTitlefoundInMessageSubject =proposalNumberfoundInMessageSubject= false;
								//just for debugging ..insert the proposal messages which only have proposal title 
								//for (int g = 0; g < proposalTitleFoundInMessageSubjectList.size(); g++)
								/* may 2018, now we dont store in seprate table, we just store in same table in three more fields and update it...see above
								for (int g : proposalTitleFoundInMessageSubjectList){
									//debug info 
//**											System.out.println(" list to string " + proposalTitleFoundInMessageSubjectList.toString());
//**											System.out.println("\tproposalTitleFoundInMessageSubjectList.size() " + proposalTitleFoundInMessageSubjectList.size() + " g " + g);
									
									String tableName = "allmessages_"+proposalIdentifier+"TitleOnly";
//										System.out.println("\tGoing to insert data into database with proposal title =  " + g + " | "+  tableName + " | " +  rootFolder + " | "+ fileEntry.getName() + " | " + messageID );
									storeInDatabase(tableName,counter, justDate,dateTimeStamp, subject, rootFolder, fileEntry.getName(),lastLine, emailMessage, g, status, 
											type, author, senderName, inReplyTo, references, emailMessageId, link,StatusChanged, wordsFoundList, analyseWords, 
											messageID, required,msgNumInFile,fromLine, conn);
									inserted = true;
								}
								*/
								//if no proposal number or title found for the current message, still insert but with proposal number = -1
								if (inserted==false){
									Integer proposalNumber=-1;	
									String tableName = "allmessages";
//$$										System.out.println("\tGoing to insert data into database no proposal = " + proposalNumber + " | "+  tableName + " | " +  rootFolder + " | "+ fileEntry.getName() + " | " + messageID );
									storeInDatabase(tableName,counter, justDate,dateTimeStamp, subject, v_rootfolder, fileEntry.getName(), lastLine, emailMessage, proposalNumber, status, 
											type, author, senderName, inReplyTo, references, emailMessageId, link, proposalTitlefoundInMessageSubject, proposalNumberfoundInMessageSubject, analyseWords, messageID, required,msgNumInFile,fromLine, conn);
								}
//**										System.out.println("End of One email message --------------------\n");
							} 
							else {
//									System.out.println("DATE NULL dateLine " + dateLine + " justDate " + dateLine + " , " + " i "
//											+ i + " " + rootFolder + "\\" + fileEntry.getName() + " , Line = " + line);
							}									
							// matched is true and from is found
							proposalList.clear(); // clear proposalList
							wordsFoundList = null;
//							}
							emailMessage = ""; // empty message when (matched is true & from is found) but also when not matched - meaning new message has
													// started and searchkey was not found in last message
							matchedproposalNumberInMessage = matchedproposalTitleInMessage = StatusChanged = false; // new emailMessage therefore set matched to null												
							analyseWords = "";	author = type = fromLine = statusFrom = statusTo = senderName=references =	inReplyTo = emailMessageId = subject = link = status = null;					
							required = true;		From = line;
							
							//clear lists
							lineproposalTitleList.clear();					lineproposalNumberList.clear();
							matchedproposalNumberInLine = matchedproposalTitleInLine =false; // new emailMessage therefore set matched to null							
							proposalTitleFoundInMessageSubjectList.clear();		proposalNumberFoundInMessageSubjectList.clear();
							messageproposalNumberList.clear();	
							
							lineConterWithinFile=0;							msgNumInFile++;
						} 
						// if not matched, keep adding lines to message
						else {
							//if searchkey (proposal number or proposal title) found in last round but end of message not reached, i.e from, 
		  					// then keep on adding lines, only write to db once next From is found
							if (matchedproposalNumberInMessage == true || matchedproposalTitleInMessage==true) 
							{ 
								emailMessage = emailMessage + line + "\n";
								// if (!line.startsWith(">")) analyseWords = analyseWords + line + "\n"; //for classification
							}
						}
						
						//Main Proposal number matching - In all cases, we do the following matching
						//Match proposal number and proposal title- MATCH proposal NUMBERS FIRST
						matchedproposalNumberInLine = matchproposalNumbersAddToList(proposalNumberSearchKeyList, v_ignoreList, line, matchedproposalNumberInMessage,proposalList);						
						if (matchedproposalNumberInLine)
							matchedproposalNumberInMessage = true;
							//System.out.println("\tmatchedproposalNumber "  ); 
						
						//MATCH proposal TITLES SECOND	
						//commented the below block to see the other function works or not 09-03-2017
						//may not need this as we now only look for proposal title in subject line
						//in message matching would have been okay, but we not sure in which context it is stated and who is author
						/*
						matchedproposalTitleInLine =  matchproposalTitlesAddToList (folder, v_debugFile, proposalTitlesSearchKeyList, proposalDetails, fileEntry,line, matchedproposalTitleInMessage);
						if (matchedproposalTitleInLine){
							matchedproposalTitleInMessage = true;
//							System.out.println("\tmatchedproposalTitle "  + matchedproposalTitle); 
						}
						*/
						//NEW GENERAL SCRIPT NOW   11-03-2017
						//as soon as proposal title is closest matched, an entry is made tp proposallist as well
						//so no need to worry
						
						//NEW SCRIPT - NEW PURPOSE NEW LOGIC
						//just remove duplicates from proposallist
						//insert all from proposallist 
						//insert all from proposaltitle, but in diffrent table
						
						//PREVIOUS SCRIPT LOGIC
						// if found proposal title only, insert
				        // if found proposal number only, dont insert as inserted in initial script
				        // if found both dont insert
						
						//check if proposal number doesnt exist for proposal which's title has been found, 
						// (REMOVE THIS SECTION IF YOU DONT WANT TO READ IN JUST proposal TITLES)
						
						//Previous logic ...not needed in this script
						//if both are matched
				        /*
						if (matchedproposalNumberInLine && matchedproposalTitleInLine){	
				        	removeFromproposalList(matchedproposalTitleInLine);				        	
				        }
				        */
				        // for lines not having from and if not matched, keep on adding lines to emailmessage	
						if (matchedproposalNumberInMessage == false && matchedproposalTitleInMessage==false) {
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

	@SuppressWarnings("deprecation")
	public static void storeInDatabase(String tableName, int v_id, Date v_dateC, Date dateTimeStamp, String v_subject, File v_rootfolder, String v_file, String v_line, String v_emailMessage, int i, String V_status, 
			String V_type, String V_author, String v_senderName, String v_inReplyTo, String v_references, String v_emailMessageId, 
			String v_link, Boolean v_proposalTitlefoundInMessageSubject, Boolean v_proposalNumberfoundInMessageSubject, String v_analyseWords, Integer v_messageID, Boolean v_required, Integer v_msgNumInFile, String fromLine, Connection conn){
		
		Statement stmt = null;
		try{
			stmt = conn.createStatement();				String sqlA;				java.sql.Date sqlDate = null;
			//convert date to java.sql date
			if (v_dateC	!= null){
				java.util.Calendar cal = Calendar.getInstance();					cal.setTime(v_dateC);  //v_dateC
				cal.set(Calendar.HOUR_OF_DAY, 0);				cal.set(Calendar.MINUTE, 0);				cal.set(Calendar.SECOND, 0);				cal.set(Calendar.MILLISECOND, 0);
				sqlDate = new java.sql.Date(cal.getTime().getTime()); // your sql date
				//System.out.println("\t\t\tutilDate:" + v_dateC + "	sqlDate: " + sqlDate);
			}
			Timestamp now = null;
			if (dateTimeStamp!=null){
				now = new Timestamp(dateTimeStamp.getTime());
			}
			
			//System.out.println("\t\t\tdateTimeStamp: " + dateTimeStamp + "	now: " + now);
			
			//Can look up the proposaltype if we want here..but better still sepratey after reading in
			
			//the mysql insert statement																							//references,
			String query = " insert into "+tableName+"( date2,dateTimeStamp, subject, folder,file , line, email, "+proposalIdentifier+", "+proposalIdentifier+"type, "
					+ "author,senderName, inReplyTo,  emailMessageId, link,analyseWords, messageID, required,msgNumInFile,fromLine, pTitleMatchedinMsgSub, pNumMatchedinMsgSub, lastdir)"
					+ " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			//System.out.println("here a");
			
			//String lastDir = v_rootfolder.toString().replace("C:\\OSSDRepositories\\JEPs\\","");
			String lastDir = v_rootfolder.toString().replace("D:\\datasets\\postBDFL_may2021\\mailingLists","");
			
			//System.out.println("reply to " + v_inReplyTo + " ref = " + v_references);
			// create the mysql insert preparedstatement
			PreparedStatement preparedStmt = conn.prepareStatement(query);
			//preparedStmt.setInt (1, v_id);
			preparedStmt.setDate (1,sqlDate);/*sqlDate */ 				preparedStmt.setTimestamp (2,now); /*dateTimeStamp);sqlDate */  preparedStmt.setString (3, v_subject); 
			preparedStmt.setString (4, v_rootfolder.toString());		preparedStmt.setString (5, v_file.toString());					preparedStmt.setString  (6, v_line);	
			preparedStmt.setString  (7,v_emailMessage); 				preparedStmt.setInt  (8, i);			        				preparedStmt.setString  (9,V_type);							
			preparedStmt.setString  (10,V_author);						preparedStmt.setString  (11,v_senderName);						preparedStmt.setString  (12,v_inReplyTo); 					
			preparedStmt.setString  (13,v_emailMessageId);				preparedStmt.setString  (14,v_link);							preparedStmt.setString  (15,v_analyseWords);	
			preparedStmt.setInt(16,v_messageID);  					    preparedStmt.setBoolean(17,v_required);							preparedStmt.setInt(18,v_msgNumInFile);		    			
			preparedStmt.setString(19,fromLine);		preparedStmt.setBoolean(20,v_proposalTitlefoundInMessageSubject);				preparedStmt.setBoolean(21,v_proposalNumberfoundInMessageSubject);	
			preparedStmt.setString(22,lastDir);
			
			preparedStmt.execute();		
			stmt.close();
			//conn.close();
		}catch(SQLException se){
			//Handle errors for JDBC
			se.printStackTrace(); System.out.println(StackTraceToString(se)  );	
		}catch(Exception e){
			//Handle errors for Class.forName
			e.printStackTrace(); System.out.println(StackTraceToString(e)  );	
		}finally{
			//finally block used to close resources
			try{
				if(stmt!=null)
					stmt.close();
			}catch(SQLException se2){   System.out.println(StackTraceToString(se2)  );	
			}// nothing we can do
			
		}//end try	     
	}
 
}
