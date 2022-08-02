package readCommits.Input;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

import connections.MysqlConnect;
import readCommits.structs.Commit;
import readCommits.structs.FileChange;
import readCommits.structs.Proposal;
import readCommits.structs.State;
import readCommits.structs.insertIntoDB;
import utilities.GetDate;
import readCommits.structs.ReturnTimeStamp;

public class Extractor {

	Integer MessageCounter;	
	static String proposalIdentifier;
	static Map<String, Proposal> proposals;
	static ReturnTimeStamp rts = new ReturnTimeStamp();
	static GetDate gd = new GetDate();
	
	//static MessageReadingUtils mru = new MessageReadingUtils();
	
	public Extractor(){
		proposals = new HashMap<String, Proposal>();
	}
	
	public static void setProposalIdentifier(String v_proposalIdentifier) {
		proposalIdentifier =v_proposalIdentifier;
	}
	
	
	public static void searchFilesForFolder(String proposalIdentifier, final File folder, final File v_rootfolder, insertIntoDB db, String delimeter,
			String stateChangeKeyword) throws IOException {
		String temp="";
		for (final File fileEntry : folder.listFiles()) 
		{
			if (fileEntry.isDirectory()) {
				searchFilesForFolder(proposalIdentifier, fileEntry, v_rootfolder, db,delimeter,stateChangeKeyword);
			} 
			else {
				if (fileEntry.isFile() ) { //&& markFound){ // && process) {					
					temp = fileEntry.getName();		// For each File
					String fullName = fileEntry.getPath();
					//String t = fileEntry.getAbsolutePath();
					System.out.println(fileEntry.getPath() );
					ReadEachFileInsertInDatabase(proposalIdentifier, fileEntry, fullName,db,delimeter,stateChangeKeyword);
				}
			}
		}
	}
	
	public static String getProposalNumberUsingProposalTitle(String title){
		MysqlConnect mc = new MysqlConnect();
		//mc.testConnection();       
		String jepNo =null;
		System.out.println(proposalIdentifier+" Title lookup "+title);
		String sql = "SELECT "+proposalIdentifier+" from "+proposalIdentifier+"Details where title LIKE '" + title + "';"; 
		//can add messageid later
		Connection connection = mc.connect();
		Statement stmt;
		try {
			stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery( sql );
			if (rs.next()){     //check every message       
				jepNo = rs.getString(1);
			}
			else {
				System.out.println("No "+proposalIdentifier+" Number exists for title: "+title);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jepNo;
	}

	private static boolean ReadEachFileInsertInDatabase(String proposalIdentifier, final File fileEntry, String fullName,
			insertIntoDB db,String delimeter, String stateChangeKeyword) throws IOException {
		//process only those files which have status changes
		//Scanner scanner = new Scanner(fileEntry.getAbsolutePath());
		//now read the file line by line...
		int counter = 0;		boolean toProcess=false;
		String message="", ln="", entireMessage=""; 
		BufferedReader br = null;		Scanner scanner = null;
		try {
			//File file =new File(fileEntry.getAbsolutePath());	
			//scanner = new Scanner( new File(fileEntry.getAbsolutePath()), "UTF-8" );
			//String text = scanner.useDelimiter("\\A").next();
//			writeToDebugFile(text);
			//String [] messages = text.split("\\r?\\nFrom ");
			//System.out.println("File "+ fileEntry.getAbsolutePath() + " messageCount " + messages.length);
			//for (String msg: messages){
//			System.out.println("here a propidentifier : " + proposalIdentifier);
			//SHOW FILENAME
//$$			System.out.println(fullName);
			
			boolean firstCommit = false,Statusfound=false,indexReached=false;
			String author ="", subject="",proposalNumber = "", permanentProposalNumber="", proposalTitle="",StatusFrom="", StatusTo="", Status="",dateTimeStamp = "";
			boolean foundDate=false, foundAuthor=false,StatusChanged=false,StatusFromChanged=false,StatusToChanged=false;
			
			br = new BufferedReader(new FileReader(fileEntry.getAbsolutePath()));
			String filePath = fileEntry.getAbsolutePath();
			Integer lineCounter=0,statusLine=0;
			
			while ((ln = br.readLine()) != null) 
			{	
				indexReached=false;		//testing this	'Index: proposal-0257.txt'
				ln= ln.trim();	
				lineCounter++;
//					toProcess=true;	
//					counter++;	
//					processStatusChange(msg, fileEntry.getAbsolutePath());
				if(lineCounter<1000){	//we want to insert only the first 1000 lines of the message in db table
					entireMessage = entireMessage+ln+"\n";
				}
				if(ln.startsWith("Date") ){
					dateTimeStamp = ln.replace("Date:", "");
					dateTimeStamp= dateTimeStamp.trim();
//					System.out.print("\tdateTimeStamp " + dateTimeStamp);
					foundDate=true;
				}	
				//sometimes date is stored in proposal line  ...  +++ proposals/trunk/proposal-0341.txt	Fri Dec 16 22:28:04 2005
				if(ln.startsWith("+++ "+proposalIdentifier+"/trunk/")) {	//+proposalIdentifier+
					//System.out.println("\tline: " + ln);
					if(ln.contains(".txt")) {
						ln = ln.split("\\.txt")[1];
						dateTimeStamp = ln.trim(); //.replace("Date:", "");
						dateTimeStamp= dateTimeStamp.trim();
	///					System.out.print("\tdateTimeStamp " + dateTimeStamp);
						foundDate=true;
					}
					else if(ln.contains("..py")) {
						ln = ln.split("\\.py")[1];
						dateTimeStamp = ln.trim(); //.replace("Date:", "");
//						System.out.print("\tdateTimeStamp " + dateTimeStamp);
						foundDate=true;
					}
				}
				
				else if(ln.startsWith("Subject:") ){
					subject = ln.replace("Subject:", "");
//					System.out.print("\tsubject " + subject);
					//foundDate=true;
				}	
				else if(ln.startsWith("+Author: ") ){
					author = ln.replace("+Author:", "");				
					author = processAuthor(author);
//					System.out.print("\tauthor" + author);
					//foundAuthor=true;
				}
			    else if(ln.startsWith("Author: ") || ln.startsWith("! Author: ")){
					author = ln.replace("Author:", "");				
					author = processAuthor(author);
//					System.out.print("\tauthor " + author);
				}
				
			    else if(ln.startsWith("--- "+proposalIdentifier) ){
					proposalNumber = ln.replace("--- peps/trunk/", "");
					proposalNumber = proposalNumber.replace("(original)", "").replace("pep-", "").trim();
					proposalNumber = proposalNumber.replace("---","").trim();
					if(proposalNumber.contains(".txt"))
						proposalNumber = proposalNumber.substring(0, proposalNumber.indexOf(".txt"));
					System.out.println("\tkk proposalNumber " + proposalNumber + " line: " + ln);
					//if(proposalNumber!=null)
					//	permanentProposalNumber = proposalNumber;
				}
				else if(ln.startsWith(proposalIdentifier+":") ){
					proposalNumber = ln.replace(proposalIdentifier+":", "");
					//proposalNumber = proposalNumber.replace("proposal-", "");
					proposalNumber = proposalNumber.trim();	System.out.println("\tpp proposalNumber " + proposalNumber+ " line: " + ln);
					//if(proposalNumber!=null)
					//	permanentProposalNumber = proposalNumber;
				}
				else if(ln.startsWith(proposalIdentifier+":") ){
					proposalNumber = ln.replace(proposalIdentifier+":", "").trim();	System.out.println("\t oo proposalNumber " + proposalNumber+ " line: " + ln);
					//proposalNumber = proposalNumber.replace("proposal-", "");
					//if(proposalNumber!=null)
					//	permanentProposalNumber = proposalNumber;
				}
				else if(ln.startsWith("--- "+proposalIdentifier+"-") ){
					proposalNumber = ln.replace("--- "+proposalIdentifier+"-", "");	System.out.println("\tjj proposalNumber " + proposalNumber+ " line: " + ln);
					//proposalNumber = proposalNumber.replace("proposal-", "");
					//delete everything after .txt
					String str = ".txt";
					proposalNumber = deleteEverytingAfter(proposalNumber, str).trim();	System.out.println("\t nn proposalNumber " + proposalNumber+ " line: " + ln);
					//if(proposalNumber!=null)
					//	permanentProposalNumber = proposalNumber;
				}
				else if(ln.startsWith("--- a/") ){
					proposalNumber = ln.replace("--- a/", "");
					//delete everything after .txt
					String str = ".txt";
					proposalNumber = deleteEverytingAfter(proposalNumber, str);				
					proposalNumber = proposalNumber.replace(".txt", "").replace(proposalIdentifier+"-", "").trim();	System.out.println("\t qq proposalNumber " + proposalNumber+ " line: " + ln);
					//if(proposalNumber!=null)
					//	permanentProposalNumber = proposalNumber;
				}
				else if(ln.startsWith("+++ b/") ){
					proposalNumber = ln.replace("+++ b/", "");
					proposalNumber = proposalNumber.replace(".txt", "").replace(proposalIdentifier+"-", "").trim();	System.out.println("\tee proposalNumber " + proposalNumber + " line: " + ln);
					//if(proposalNumber!=null)
					//	permanentProposalNumber = proposalNumber;
				}
				//Index: proposal-0257.txt
				//diff --git
				else if(ln.startsWith("Index: "+proposalIdentifier+"-") ){
					//we just set the index reached to true, but we assign proposal numbers later (in the line of code where '//index$$' is specified )
					//added dec 28,2017 to handle index lines
					ln = br.readLine();
					if(ln.equals("===================================================================")) {
						indexReached=true;
					}
					//if(proposalNumber!=null)
					//	permanentProposalNumber = proposalNumber;
//					System.out.println("\tproposalNumber" + proposalNumber);
				}
				//title added july 2017
				else if(ln.startsWith("Title:") || ln.startsWith("+Title:")){
					proposalTitle = ln.replace("Title:", "").trim();
					if (proposalTitle.startsWith("+"))
						proposalTitle = proposalTitle.replaceFirst("\\+", "");
//					proposalTitle = proposalTitle.replace("proposal-", "");
					proposalTitle = proposalTitle.trim();
//$$					System.out.println("proposalTitle " + proposalTitle);
					
					//FOR JEPS ONLY, JEP STATUS BLOCKS DONT CONTAIN JEP NUMBER BUT JEP TITLE, 
					//SO WE HAVE TO QUERY JEPDETAILS TABLE FOR JEP NUMBER
					if (proposalIdentifier.contains("jep")) {
						if(proposalNumber==null || proposalNumber.isEmpty() || proposalNumber.length()==0) {
							//permanentProposalNumber = proposalNumber;
							proposalNumber = getProposalNumberUsingProposalTitle(proposalTitle);	System.out.println("\t title proposalNumber " + proposalNumber + " line: " + ln);
						}
					}
				}
				
				//temp ...debug
				else if(ln.contains("-Status") ){	
				System.out.println("line contains -Status: " + Status + " line " + ln);
				}  //end debug
				else if(ln.startsWith("-Status: ") ){	System.out.println("pppp");
					StatusFrom = ln.replace("-Status: ", "").trim().toUpperCase();
					StatusFromChanged =true;
					System.out.println("minus " + StatusFrom + " " + proposalNumber);
					//System.out.println(v_msg);
					statusLine= lineCounter;
				}  
				//"+stateChangeKeyword+"
				else if(ln.startsWith("! Status: ") ){	System.out.println("qqqq");		//! Status is used for both from and to
					if (Statusfound){
						StatusTo= ln.replace("! Status: ", "").trim().toUpperCase();
						StatusToChanged=true;
				    }
					else{
						StatusFrom = ln.replace("! Status: ", "").trim().toUpperCase();
						StatusFromChanged = true;			Statusfound=true;
					}
					statusLine= lineCounter;	
				}  else if(ln.startsWith("+Status: ") ){	System.out.println("+Status");
					StatusTo = ln.replace("+Status: ", "").trim().toUpperCase();
					StatusToChanged = true;
					System.out.println("+ " + StatusFrom + " " + proposalNumber);
				}  else if(ln.startsWith("Status: ") ){		System.out.println("Status");
						Status = ln.replace("Status:", "").trim().toUpperCase();
						StatusChanged=true;
						System.out.println(StatusFrom + ":" + proposalNumber);
						//sometimes if no 'StatusTo' data is found, 'Status' becomes that new state change..look at example below
//						if(StatusTo==null || StatusTo.isEmpty() || StatusTo.length()==0 || StatusTo.equals("")){
//							StatusTo = Status;
//						}
				} 
				//to read daniel data (which has got all sections extracted into commits, we use commit as starting point of new commit)
				//	else if (ln.trim().startsWith("commit ") )
				//to read data from all different email archive files, use From - here I have to add code as two comits can exist in same message within a file
								
				//but commit is not end of message as in "From: " so we have to maybe see if there are multiple commits
				//then we write when we encounter the second commit, as one set of status information would have been read
				//but it shoudl be fine, as even if its the first commit, and we write then as the statusto is empty, so nothing wud be inserted
				
				//sometimes there are several of this in a file which is missed, look at line 319298 in 'text.txt'
				/*
				diff --git a/proposal-0257.txt b/proposal-0257.txt
				index 23094c5..54e8350 100644
				--- a/proposal-0257.txt
				+++ b/proposal-0257.txt
				@@ -2,7 +2,7 @@ Proposal: 257
				 Title: Docstring Conventions
				 Version: $Revision$
				 Last-Modified: $Date$
				-Author: David Goodger <goodger@users.sourceforge.net>,
				+Author: David Goodger <goodger@python.org>,
				         Guido van Rossum <guido@python.org>
				 Discussions-To: doc-sig@python.org
				 Status: Active
				*/
				//not sure where he got te exact data above, what we have is below
				/*				
				Index: proposal-0257.txt
				===================================================================
				RCS file: /cvsroot/python/python/nondist/proposals/proposal-0257.txt,v
				retrieving revision 1.4
				retrieving revision 1.5
				diff -C2 -d -r1.4 -r1.5
				*** proposal-0257.txt	5 Jul 2001 19:20:16 -0000	1.4
				--- proposal-0257.txt	1 Aug 2002 22:32:33 -0000	1.5
				***************
				*** 3,10 ****
				  Version: $Revision$
				  Last-Modified: $Date$
				! Author: dgoodger@bigfoot.com (David Goodger),
				!         guido@digicool.com (Guido van Rossum)
				  Discussions-To: doc-sig@python.org
				! Status: Draft
				  Type: Informational
				  Created: 29-May-2001
				--- 3,10 ----
				  Version: $Revision$
				  Last-Modified: $Date$
				! Author: goodger@users.sourceforge.net (David Goodger),
				!         guido@python.org (Guido van Rossum)
				  Discussions-To: doc-sig@python.org
				! Status: Active
				  Type: Informational
				  Created: 29-May-2001
				***************
				*** 33,39 ****
				*/
				
				
				//Nov 2019 for commit based data from githib
				//as soon as new commit sequence is reached
				if(ln.trim().startsWith("commit ") ) //Nov 2019 added ' commit ' 
				
				//for message based checkin or commit messages which was stopped in 2016
				// - not sure about this clause
				/*	
				if (ln.trim().startsWith("From: ") || ln.trim().startsWith("Modified: pep/trunk/pep-") ||  ln.trim().startsWith("diff --git a")  
						|| indexReached ) // && firstCommit)  //only eneter if its second commit
				*/
				{	
					//check if its exists, if exists check is datetime is before the datetime before
					//else ignore				
					boolean firstLine = false;	
					//sometimes only status change is registered, not the other two
				
					//######## Temporarily commented
					/*
					if (StatusChanged){ //Status != null && Status.isEmpty() && StatusTo.isEmpty() && StatusTo != null && StatusFrom.isEmpty() && StatusFrom !=null ){
//							System.out.println("First line of file - all null ");
						firstLine=true;
					}
					else{				//lineCounter+ " "+statusLine+ " b "+
						if (StatusFromChanged) //!StatusFrom.isEmpty() && StatusFrom != null)  //lineCounter+ " "+statusLine+" a "+
							System.out.println("Status From: | "+ proposalNumber + " | "+ proposalTitle + " | "+ author + " | " +dateTimeStamp + " | "+ StatusFrom + " | " + StatusTo + " | " + filePath);
						if (StatusToChanged) //(!StatusTo.isEmpty() && StatusTo != null)
							System.out.println("Status To: | "+ proposalNumber + " | "+ proposalTitle + " | "+ author + " | " +dateTimeStamp + " | "+ StatusFrom + " | " + Status + " | " + filePath);	
						else
							System.out.println("Else: | "+ proposalNumber + " | "+ proposalTitle + " | "+ author + " | " +dateTimeStamp + " | "+ StatusFrom + " | " + StatusTo + " | " + filePath);	
					}
					*/
					
					//writeToDebugFile(v_msg, proposalNumber);
					
					//insert into db *** DONT INSERT FOR FIRST ENCOUNTER OF FROM: ABOVE
					//System.out.println("going insert");
					
					//not all messages have commits, so check if status variable has data
//					System.out.println("here lineCounter: " +lineCounter + " folder " +fullName ); 
//$$					System.out.println("\tproposalNumber " + proposalNumber);
					if(StatusTo==null || StatusTo.isEmpty() || StatusTo.length()==0){	
						//sometimes only Status is available, see example above
						if(Status==null || Status.isEmpty() || Status.length()==0){	
//$$							System.out.println("here a status empty "); 
						}else {
							System.out.println(proposalNumber + " Status Not Null ..inserting"); 
							insertListToDatabase(proposalIdentifier, proposalNumber, Status, dateTimeStamp, firstLine, author, proposalTitle, entireMessage,fullName,db,lineCounter);
						}						
					}else {
						System.out.println(proposalNumber + " Status To Not Null "); 
						insertListToDatabase(proposalIdentifier, proposalNumber, StatusTo, dateTimeStamp, firstLine, author, proposalTitle, entireMessage,fullName,db,lineCounter);
					}
					author = proposalTitle = proposalNumber = permanentProposalNumber= StatusFrom= StatusTo= Status=entireMessage="";
					//lineCounter=0;
					//dateTimeStamp = "";   for all commits in a message, we share the same dta, as not all commits come with a date 	
					foundDate=foundAuthor=Statusfound=false;	//once written, we empty this
					StatusChanged = StatusFromChanged = StatusToChanged = false;
					counter++;
				} //end else if
				//we extract the new proposal number after doing all the above to prevent it assigning the new proposal number to previous record
				//before the below block of code was way above which was causing all problems as stated in above sentence
				if(ln.startsWith("diff --git") ){
					proposalNumber = ln.replace("diff --git", "").replace(".txt", "").replace(".rst", "").replace("pep-", "").trim();	System.out.println("\t ww proposalNumber " + proposalNumber + " line: " + ln);
					//if(proposalNumber!=null)
					//permanentProposalNumber = proposalNumber;
				}
				else if (ln.trim().startsWith("commit ") && !firstCommit) {
					firstCommit = true;
				}
				//index$$
				if(ln.startsWith("Index: pep-") ){	//Index: proposal-0257.txt
					proposalNumber = ln.replace("Index:", "").replace(".txt", "").replace("pep-", "").trim();	System.out.println("\t vv proposalNumber " + proposalNumber + " line: " + ln);
					//added dec 28,2017 to handle index lines
					ln = br.readLine();
					if(ln.equals("===================================================================")) {
						indexReached=true;
					}
					//if(proposalNumber!=null)
					//	permanentProposalNumber = proposalNumber;
//					System.out.println("\tproposalNumber" + proposalNumber);
				}
			} //end while loop reading lines
			//write to database those instances when delimeter has not been reached (commit) as file came to an end
			//this is only for reading directly from checin archve files, not daniel data
			if (StatusChanged) { //Status != null && Status.isEmpty() && StatusTo.isEmpty() && StatusTo != null && StatusFrom.isEmpty() && StatusFrom !=null ){
//				System.out.println("First line of file - all null ");
				//firstLine=true;
			}
			else{				//lineCounter+ " "+statusLine+ " b "+
	/*
				if (StatusFromChanged) //(!StatusFrom.isEmpty() && StatusFrom !=null)  //lineCounter+ " "+statusLine+" a "+
					System.out.println("Status From: | "+ proposalNumber + " | "+ proposalTitle + " | "+ author + " | " +dateTimeStamp + " | "+ StatusFrom + " | " + StatusTo + " | " + filePath);
				else if (StatusToChanged)  //(!StatusTo.isEmpty() && StatusTo != null)
					System.out.println("Status To: | "+ proposalNumber + " | "+ proposalTitle + " | "+ author + " | " +dateTimeStamp + " | "+ StatusFrom + " | " + Status + " | " + filePath);	
				else
					System.out.println("Else: | "+ proposalNumber + " | "+ proposalTitle + " | "+ author + " | " +dateTimeStamp + " | "+ StatusFrom + " | " + StatusTo + " | " + filePath);				
	*/			
			}
			boolean firstLine = false;
			if (StatusChanged || StatusFromChanged || StatusToChanged){
				insertListToDatabase(proposalIdentifier, proposalNumber, StatusTo, dateTimeStamp, firstLine, author, proposalTitle, entireMessage,fullName,db,lineCounter);
			}
			//should be fine without these two lines, but just to make sure
			StatusFrom= StatusTo= Status="";
			StatusChanged = StatusFromChanged = StatusToChanged = false;
			
			//System.out.println("no of messages = "+ messages.length + " no of status change = " + counter);
			entireMessage = "";
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally{
			//scanner.close();
		}
		return toProcess;
	}


	private static void insertListToDatabase(String proposalIdentifier, String proposalNumber, String StatusTo, String dateTimeStamp, 
			boolean firstLine, String author, String proposalTitle, String entireMessage, String fileName, insertIntoDB db,
			Integer lineCounter) {
		if (!firstLine)
		{
			try {	
				//Date
				Date dt = null; 				dt = gd.findDate(dateTimeStamp); //pattern matching time
				if (dt==null)
					dt = ReturnTimeStamp.returnDate(dateTimeStamp);		//natty time
				System.out.println(" ------3333333");
				//Timestamp - date to timestamp
				Timestamp now = null;
				if (dt!=null){	//fromDateTimeStamp
					now = new Timestamp(dt.getTime());	//fromDateTimeStamp
				}

				String emailMessage = "Status : "+ StatusTo;				

				if (proposalNumber==null || proposalNumber.isEmpty() || proposalNumber.equals(""))  {
					System.out.println(" proposal number is null (" + proposalNumber+ ")");
				}
				else {		
					System.out.println(" proposal number is not null (" + proposalNumber+ ")");
					//we check here in order to remove
					if(!isNumeric(proposalNumber)) {
						System.out.println(" proposal number is not numeric (" + proposalNumber+ ")");
						//proposal number not numeric..try removing 'a/0203'  'b/0203'
						proposalNumber = proposalNumber.replaceAll("a\\/","").replaceAll("b\\/","");
						if(proposalNumber.contains(" "))
							proposalNumber = proposalNumber.split(" ")[0];						
					}
					//now we insert 
					if(isNumeric(proposalNumber)) {
						System.out.println(" proposal number is numeric (" + proposalNumber+ ")");
						if (now==null)	//for some reason we couldnt convert time
						{ 
							System.out.println(" date null (" + now+ ")");
							try {
							    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("c:\\scripts\\mydatefile.txt", true)));
							    System.out.println("filename: " + fileName + " proposal: " + proposalNumber + " statusTo "+ StatusTo +" dateline: "+ dateTimeStamp + " datetoinsert: " + now );
							    out.println(entireMessage); 							    out.println("-----------");
							    out.close();
							} catch (IOException e) {
							    //exception handling left as an exercise for the reader
							}							
						}
						else {
							System.out.println(" going to insert proposalNumber : " + proposalNumber+ " state: "+ StatusTo);
							db.insertintoDatabase(proposalIdentifier,proposalNumber, dt,now, author, StatusTo,proposalTitle,entireMessage,fileName, lineCounter);
						}
					}
					else {
						System.out.println(" proposal number not numeric" + proposalNumber);
						//System.out.println(name + ", " + sqlDate2 + ", " + s.dateTimeStamp + ", " + s.author + ", " + s.type.toUpperCase());
						System.out.println("Orig Date: " + dateTimeStamp + " fromDateTimeStamp " + dt + " now " + now + " proposal " + proposalNumber);
						System.out.format("%10s%40s%40s%40s",proposalNumber, dateTimeStamp, dt,now);
					}
				}
				//FOR JEPS WE INSERT..why
				if(proposalIdentifier.equals("jep")) {
					db.insertintoDatabase(proposalIdentifier,proposalNumber, dt,now, author, StatusTo,proposalTitle,entireMessage,fileName, lineCounter);
					System.out.println("filename: " + fileName + " proposal: " + proposalNumber + " statusTo "+ StatusTo +" dateline: "+ dateTimeStamp + " datetoinsert: " + now );
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public ArrayList <ProposalStates> checkStatesInsert(ArrayList <ProposalStates> proposalStatesList, String proposal, String status, String StatusTo, Date dateTime) 	{ 
  		Boolean repeatedSentence = false,found = true;  		
  		ArrayList<Integer> al = new ArrayList<Integer>(); 
  		
  		if(!proposalStatesList.isEmpty()){
  			int counter =0;
  			for (int x=0; x <proposalStatesList.size(); x++){	
  					
  				//if proposal and state match, check datetime 
	  			/*
  				if( proposalStatesList.get(x).getProposal().equals(proposal) && proposalStatesList.get(x).getStatus().equals(status))
	  				{
	  					String xValue = proposalStatesList.get(x).getCurrentSentence().toLowerCase();
	  					String yValue = proposalStatesList.get(y).getCurrentSentence().toLowerCase();		
	  					if( xValue.toLowerCase().equals(yValue.toLowerCase()) )   	        				
		  	        	{
		  	        		repeatedSentence= true;
		  					found=true;	
		  					//mark for deletion by adding to array
		  					System.out.println("sentence matched--adding to remove list x: "+x + " y: "+ y + " xSentence: " + xValue);
//		  					al.add(y); //--delete the second instance, not the first
		  					proposalStatesList.remove(y);
		  					y--;
		  				}  				
	  				}
	  				*/
  			}
  			
  			//do deletion from list array
//  			for (Integer a: al){	
//  				tripleProcessingResult.remove(a);
//  				System.out.println("Removed from list xValue: " + a);
//  			}
//  			try {
//				outputListToFile(tripleProcessingResult,fw);
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}  			
  			
  	    }
  		return proposalStatesList;
  	}
	
	public static void processStatusChange(String v_msg, String filepath){
		//assume that one message wil only contain one status change		
		//split message by newlines
		
		String [] initialLines = v_msg.split("\\r?\\n");
		
		for (String ln : initialLines){
			//every seprate commit would have a date and an author
			
			//if (foundDate&& foundAuthor)
			//	break;
		}
		String [] commits = v_msg.split("Modified: pep/trunk/");
//		System.out.println("----- "+ dateTimeStamp+ " -- " + commits.length);
		
		Integer counter=0;
		for (String c : commits)
		{	
			// skip if first iteration, as nothing is there in the first round
			// counter is incremented at the end of the loop
			//if(counter==0){
			//	continue;	//start next element in loop
			///}
			String [] lines = c.split("\\r?\\n");			
			
//			if (counter>0)
//				System.out.println("-----");
//			counter++;
			for (String ln : lines)	{ }			
			//Integer.valueOf(proposalNumber) 			
		}
	}

	private static void writeToDebugFile(String v_msg){  //, String proposalNumber) {
		//if(proposalNumber==null){
		try(FileWriter fw = new FileWriter("c:\\scripts\\outbig.txt", true); BufferedWriter bw = new BufferedWriter(fw);  PrintWriter out = new PrintWriter(bw)) {
			    out.println(v_msg + "\n");				    
			} catch (IOException e) {
			    //exception handling left as an exercise for the reader
			}
		//}
	}


	private static String deleteEverytingAfter(String proposalNumber, String str) {
		if (proposalNumber.contains(str)){
			int spaceIndex = proposalNumber.indexOf(str);
			if (spaceIndex != -1) {
				proposalNumber = proposalNumber.replace(proposalNumber.substring(spaceIndex, proposalNumber.length()),"");					   
			}
		}
		return proposalNumber;
	}

	public static boolean isNumeric(String str)	{
	    return str.matches("-?\\d+(.\\d+)?");
	}

	private static String processAuthor(String author) {
		if (author.contains(".")){
			author = author.replace(".", " ");
		}
		if (author.contains("<")){
			int spaceIndex = author.indexOf("<");
			if (spaceIndex != -1) {
				author = author.replace(author.substring(spaceIndex, author.length()),"");					   
			}
		}
		if (author.contains("(") && author.contains(")")){
			author = author.substring(author.indexOf("(")+1,author.indexOf(")"));
		}
		if (author.contains("!"))
			author = author.replace("!", "");
		
		return author.trim();
	}
	
	public static void run(String fileName){		
		Reader r = new Reader(fileName);
		r.run();
		List<FileChange> fileChanges = new ArrayList<FileChange>();
		
		for(Commit c : r.getCommits()){
			fileChanges.addAll(c.getChanges());
		}
		
		for(FileChange f : fileChanges){
			if(f.getStateFlag()){
				mergeMap(f.getName(), extractState(f));
			}
		}
	}
	
	
	private static State extractState(FileChange change){		
		State newState = new State(
				change.getContent().substring(change.getContent().indexOf("\n+Status:") + 10, change.getContent().indexOf("\n", change.getContent().indexOf("\n+Status:") + 10)),
				change.getAuthor(),	change.getDate(),	change.getDateStamp(),	change.getProposalTitle(),	change.getEntireMessage());	
		return newState;
	}
	
	private static void mergeMap(String fileName, State state){		
		Proposal p = proposals.get(fileName);
		if (p == null) { //not found, new thread
			
			Proposal newProposal = new Proposal(fileName, state);
			proposals.put(fileName, newProposal);
		}
		else { //else merge
			p.addState(state);
			proposals.put(fileName, p);
		}
	}
	
	public String getResults() throws Exception{
		StringBuilder sb = new StringBuilder();		
		for(Proposal p : proposals.values()){
			sb.append(p.toString2());
		}		
		return sb.toString();
	}
}
