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
import java.util.Map;
import java.util.Scanner;

import org.apache.commons.io.FileUtils;

import readCommits.structs.Commit;
import readCommits.structs.FileChange;
import readCommits.structs.Proposal;
import readCommits.structs.State;
import readCommits.structs.insertIntoDB;
import utilities.GetDate;
import readCommits.structs.ReturnTimeStamp;

public class ExtractorDaniel {

	Integer MessageCounter;	
	static String proposalIdentifier;
	static Map<String, Proposal> proposals;
	static ReturnTimeStamp rts = new ReturnTimeStamp();
	static GetDate gd = new GetDate();
	
	public ExtractorDaniel(){
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
//***					System.out.println(fileEntry.getPath() );
					ReadEachMessageInsertInDatabase(proposalIdentifier, fileEntry, fullName, db,delimeter,stateChangeKeyword);
				}
			}
		}
	}

	private static boolean ReadEachMessageInsertInDatabase(String proposalIdentifier, final File fileEntry, String fullName,
			insertIntoDB db,String delimeter, String stateChangeKeyword) throws IOException {
		//process only those files which have status changes
		//Scanner scanner = new Scanner(fileEntry.getAbsolutePath());		
		int counter = 0;
		boolean toProcess=false;
		String message=null, ln=null, entireMessage=""; 
		BufferedReader br = null;
		Scanner scanner = null;
		try {			
			boolean firstCommit = false,indexReached=false;			
			String author ="", subject="", proposalNumber = "", permanentProposalNumber="", proposalTitle="",StatusFrom="", StatusTo="", Status="",dateTimeStamp = "";
			String newAuthor="",newdateTimeStamp = "";  //dec 2019 to cater for single commits and multiple associated diffs
			boolean foundDate=false, foundAuthor=false,StatusChanged=false,StatusFromChanged=false,StatusToChanged=false;
			br = new BufferedReader(new FileReader(fileEntry.getAbsolutePath()));
			String filePath = fileEntry.getAbsolutePath();
			Integer lineCounter=0, statusLine=0;
			boolean Statusfound=false;
			
			while ((ln = br.readLine()) != null)  //now read the file line by line...
			{		
				lineCounter++;
//					toProcess=true;	
//					counter++;	
//					processStatusChange(msg, fileEntry.getAbsolutePath());
//				if(lineCounter<1000){
//					entireMessage = entireMessage+ln+"\n";
//				}
				
				//dec 2019 Put all different various ways of getting proposal number in a function
				//we only read proposalnumber once from diff line later
				//proposalNumber = getProposalNumber(proposalNumber,ln);
				
				if(ln.trim().startsWith("Date") ){
					newdateTimeStamp = ln.replace("Date:", ""); 	System.out.println("\tDate Line capture: " + newdateTimeStamp);
					foundDate=true;
				}		
				else if(ln.trim().startsWith("Subject:") ){
					subject = ln.replace("Subject:", "");  //	System.out.println("dateTimeStamp " + dateTimeStamp);
					//foundDate=true;
				}	
				else if(ln.trim().startsWith("+Author: ") || ln.trim().startsWith("Author: ") || ln.trim().startsWith("! Author: ")){
					newAuthor=ln.replace("Author:", "");						newAuthor = processAuthor(author);
							//newdateTimeStamp 
					//same author variable
					//author = ln.replace("Author:", "");						author = processAuthor(author);
				}
			    
				//title added july 2017
				else if(ln.trim().startsWith("Title:") || ln.trim().startsWith("+Title:")){
					proposalTitle = ln.replace("Title:", "").trim();
					if (proposalTitle.startsWith("+"))
						proposalTitle = proposalTitle.replaceFirst("\\+", "");
//					proposalTitle = proposalTitle.replace("proposal-", "");
					proposalTitle = proposalTitle.trim();
					//if(proposalNumber!=null)	permanentProposalNumber = proposalNumber;
				}
				
				else if(ln.trim().startsWith("-  Status: ") ){  // peps do not need space but bips needs two spaces between '-' and 'Status'
					StatusFrom = ln.replace("-  Status: ", "").trim().split(" ")[0];  	//StatusFrom= StatusFrom.replace(".mediawiki", "").trim().toUpperCase();	 //for bips ... +Status: REPLACED 0079.mediawiki		
					StatusFrom = StatusFrom.replace("<", "").replace(">", ""); //jan 2021
					StatusFromChanged =true;					System.out.println("\t-  StatusFrom: " + StatusFrom + " " + proposalNumber);					//System.out.println(v_msg);
					statusLine= lineCounter;
				}  
				
				else if(ln.trim().startsWith("+  Status: ") ){	// peps do not need space but bips needs two spaces between '-' and 'Status'
					StatusTo = ln.replace("+  Status: ", "").trim().split(" ")[0];			//StatusTo = StatusTo.replace(".mediawiki", "").trim().toUpperCase();	 //added for bips ... +Status: REPLACED 0079.mediawiki		
					StatusTo = StatusTo.replace("<", "").replace(">", ""); //jan 2021
					StatusToChanged = true; System.out.println("\t +  StatusTo: " + StatusTo + " " + proposalNumber);
				}  else if(ln.trim().startsWith("Status: ") ){
						Status = ln.replace("Status:", "").trim().toUpperCase();						StatusChanged=true; System.out.println("\t Status: " + StatusTo + " " + proposalNumber);
				} 
				//to read daniel data (which has got all sections extracted into commits, we use commit as starting point of new commit)
				//	else if (ln.trim().startsWith("commit ") )
				//to read data from all different email archive files, use From - here I have to add code as two comits can exist in same message within a file
				
				//as soon as 'diff' is encountered, store the data
				else if (ln.trim().startsWith("diff --git a/") )  //commit    //only eneter if its second commit
				{	
					boolean firstLine = false;					
					//not all messages have commits, so check if status variable has data
					System.out.println("\nEncountered New 'diff --git a/' lineCounter: " +lineCounter + " line : " + ln); // " folder " +fullName ); 
//$$					System.out.println("\tproposalNumber " + proposalNumber);
					if(StatusTo==null || StatusTo.isEmpty() || StatusTo.length()==0){
						System.out.println("\t StatusTo  is empty"); 
						//sometimes only Status is available, see example above
						if(Status==null || Status.isEmpty() || Status.length()==0){	
//$$							System.out.println("here a status empty "); 
						}else {
							//dec 2019..we dont consider these anymore as we get duplicate data for the same state
							//System.out.println(proposalNumber + " Status Not Null ..inserting"); 
//							//insertListToDatabase(proposalIdentifier, proposalNumber, Status, dateTimeStamp, firstLine, author, proposalTitle, entireMessage,fullName,db,lineCounter);
						}						
					}else {
						System.out.println("\t Inserting previous diff: " + proposalNumber + " StatusTo " + StatusTo); 
						insertListToDatabase(proposalIdentifier, proposalNumber, StatusTo, dateTimeStamp, firstLine, author, proposalTitle, entireMessage,fullName,db,lineCounter);
					}
					
					author = newAuthor;  //we assign the new variable values
					dateTimeStamp = newdateTimeStamp; //we assign the new variable values
					proposalNumber = permanentProposalNumber= proposalTitle = "";
					StatusFrom= StatusTo= Status="";
					foundDate=foundAuthor=false;
					StatusChanged = StatusFromChanged = StatusToChanged = false;
					counter++;
					
					//dec 2019..here we read the new pep number for first time
					//else if(ln.trim().startsWith("diff --git") ){
						proposalNumber = getProposalNumber(proposalNumber,ln);
						System.out.println("\n\t New PEP Number just read in from diff line: (" + proposalNumber + ") line : " + ln); 
						//if(proposalNumber!=null)
						//	permanentProposalNumber = proposalNumber;
					//}					
					
				} //end else if
				//we extract the new proposal number after doing all the above to prevent it assigning the new proposal number to previous record
				//before the below block of code was way above which was causing all problems as stated in above sentence
				if(ln.startsWith("diff --git") ){
//$$					proposalNumber = ln.replace("diff --git", "").replace(".txt", "").replace(".rst", "").replace("pep-", "").replace("a/", "").replace("b/", "").trim();
					//dec 2019 we just get one proposal number from the result
					// ww proposalNumber 0217 0217 line: diff --git a/pep-0217.txt b/pep-0217.txt
//$$					proposalNumber = proposalNumber.split("\\s+")[0];
//					System.out.println("\t ww proposalNumber " + proposalNumber + " line: " + ln);
					//if(proposalNumber!=null)
					//permanentProposalNumber = proposalNumber;
				}
				else if (ln.trim().startsWith("commit ") ) { //&& !firstCommit) {
					System.out.println("Encountered New 'commit' lineCounter: " +lineCounter + " line : " + ln); // " folder " +fullName ); 
					//author = dateTimeStamp = "";
					newAuthor=newdateTimeStamp ="";
					firstCommit = true;
				}				
				
			} //end while loop reading lines
			//write to database those instances when delimeter has not been reached (commit) as file came to an end
			//this is only for reading directly from checin archve files, not daniel data
			if (StatusChanged) { //Status != null && Status.isEmpty() && StatusTo.isEmpty() && StatusTo != null && StatusFrom.isEmpty() && StatusFrom !=null ){
//				System.out.println("First line of file - all null ");
				//firstLine=true;
			}
			else{				//lineCounter+ " "+statusLine+ " b "+
				if (StatusFromChanged) //(!StatusFrom.isEmpty() && StatusFrom !=null)  //lineCounter+ " "+statusLine+" a "+
					System.out.println("Status From: | "+ proposalNumber + " | "+ proposalTitle + " | "+ author + " | " +dateTimeStamp + " | "+ StatusFrom + " | " + StatusTo + " | " + filePath);
				else if (StatusToChanged)  //(!StatusTo.isEmpty() && StatusTo != null)
					System.out.println("Status To: | "+ proposalNumber + " | "+ proposalTitle + " | "+ author + " | " +dateTimeStamp + " | "+ StatusFrom + " | " + Status + " | " + filePath);	
				else
					System.out.println("Else: | "+ proposalNumber + " | "+ proposalTitle + " | "+ author + " | " +dateTimeStamp + " | "+ StatusFrom + " | " + StatusTo + " | " + filePath);				
				
			}
			boolean firstLine = false;
			if (StatusChanged || StatusFromChanged || StatusToChanged){
				insertListToDatabase(proposalIdentifier, proposalNumber, StatusTo, dateTimeStamp, firstLine, author, proposalTitle, entireMessage,fullName,db, lineCounter);
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
			boolean firstLine, String author, String proposalTitle, String entireMessage, String fileName,insertIntoDB db,
			Integer lineCounter) {
		/*
		if (!firstLine)
		{
			try {			
				//dateTimeStamp
				Date fromDateTimeStamp =null; 
				fromDateTimeStamp = ReturnTimeStamp.returnDate(dateTimeStamp);	
				//date to timestamp				
				Timestamp now = null;
				if (fromDateTimeStamp!=null){
					now = new Timestamp(fromDateTimeStamp.getTime());
				}
			    String emailMessage = "Status : "+ StatusTo;				
				insertIntoDB db = new insertIntoDB();
				if(proposalNumber!=null)
					if(isNumeric(proposalNumber))																				//just temporarily pass 0 as linenumber
						db.insertintoDatabase(proposalIdentifier, proposalNumber, fromDateTimeStamp,now, author, StatusTo,proposalTitle,entireMessage, fileName,0); 
					//System.out.println(name + ", " + sqlDate2 + ", " + s.dateTimeStamp + ", " + s.author + ", " + s.type.toUpperCase());
			    }
		    catch (Exception e) {
		        e.printStackTrace();
		    }
		}
		*/
		if (!firstLine)
		{
			try {	
				//Date
				Date dt, doriginal = null; 	 			dt = doriginal = gd.findDate(dateTimeStamp); //pattern matching time
				if (dt==null)
					dt = ReturnTimeStamp.returnDate(dateTimeStamp);		//natty time
//				System.out.println(" ------3333333");
				//Timestamp - date to timestamp
				Timestamp now = null;
				if (dt!=null){	//fromDateTimeStamp
					now = new Timestamp(dt.getTime());	//fromDateTimeStamp
				}

				String emailMessage = "Status : "+ StatusTo;	
				
				//jan 2021..for bips
				proposalNumber = proposalNumber.replace(".mediawiki", "");

				if (proposalNumber==null || proposalNumber.isEmpty() || proposalNumber.equals(""))  {
					System.out.print(" proposal number is null (" + proposalNumber+ ")");
				}
				else {		
					System.out.print(" | proposal number is not null (" + proposalNumber+ ")");
					//we check here in order to remove
					if(!isNumeric(proposalNumber)) {
						System.out.print(" | proposal number is not numeric (" + proposalNumber+ ")");
						//proposal number not numeric..try removing 'a/0203'  'b/0203'
						proposalNumber = proposalNumber.replaceAll("a\\/","").replaceAll("b\\/","");
						if(proposalNumber.contains(" "))
							proposalNumber = proposalNumber.split(" ")[0];	
						System.out.print(" | proposal number now (" + proposalNumber+ ")");
					}
					//now we insert 
					if(isNumeric(proposalNumber)) {
						System.out.print(" | proposal number is numeric (" + proposalNumber+ ")");
						if (now==null)	//for some reason we couldnt convert time
						{ 
							System.out.println(" | date null (" + now+ ")");
							try {
							    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("c:\\scripts\\mydatefile.txt", true)));
							    System.out.println(" | filename: " + fileName + " proposal: " + proposalNumber +
							    		" statusTo "+ StatusTo +" doriginal " + doriginal + " final dateline: "+ dateTimeStamp + " datetoinsert: " + now );
							    out.println(entireMessage); 							    out.println("-----------");
							    out.close();
							} catch (IOException e) {
							    //exception handling left as an exercise for the reader
							}							
						}
						else {
							System.out.println();
							System.out.println(" going to insert proposalNumber : " + proposalNumber+ " state: "+ StatusTo + " date: "+dt);
							db.insertintoDatabase(proposalIdentifier,proposalNumber, dt,now, author, StatusTo,proposalTitle,entireMessage,fileName, lineCounter);
						}
					}
					else {
						System.out.println(" | proposal number not numeric: " + proposalNumber);
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
				//jan 2021..bips are handled differently
				if(proposalIdentifier.contains("bip")) {
					System.out.println("\n###bips: " + proposalNumber);
					proposalNumber = proposalNumber.replace("bip","").replace("bips-", ""); 
					System.out.println("\n###bips: " + proposalNumber);  
					db.insertintoDatabase(proposalIdentifier,proposalNumber, dt,now, author, StatusTo,proposalTitle,entireMessage,fileName, lineCounter);
					System.out.println("filename: " + fileName + " proposal: " + proposalNumber + " statusTo "+ StatusTo +" dateline: "+ dateTimeStamp + " datetoinsert: " + now );
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static String getProposalNumber(String proposalNumber, String ln) {
		   if(ln.trim().startsWith("--- "+proposalIdentifier) ){	
				proposalNumber = ln.replace("--- "+proposalIdentifier+"/trunk/", "");
				proposalNumber = proposalNumber.replace("(original)", "");
				proposalNumber = proposalNumber.replace(".txt", "");
				proposalNumber = proposalNumber.replace(proposalIdentifier+"-", "");
				proposalNumber = proposalNumber.trim();
				//if(proposalNumber!=null)
				//	permanentProposalNumber = proposalNumber;
			}
			else if(ln.trim().startsWith(proposalIdentifier+":") ){
				proposalNumber = ln.replace(proposalIdentifier+":", "");
				//proposalNumber = proposalNumber.replace("proposal-", "");
				proposalNumber = proposalNumber.trim();
				//if(proposalNumber!=null)
				//	permanentProposalNumber = proposalNumber;
			}
			else if(ln.trim().startsWith("+"+proposalIdentifier+":") ){
				proposalNumber = ln.replace("+"+proposalIdentifier+":", "");
				//proposalNumber = proposalNumber.replace("proposal-", "");
				proposalNumber = proposalNumber.trim();
				//if(proposalNumber!=null)
				//	permanentProposalNumber = proposalNumber;
			}
			else if(ln.trim().startsWith("--- "+proposalIdentifier+"-") ){
				proposalNumber = ln.replace("--- "+proposalIdentifier+"-", "");
				//proposalNumber = proposalNumber.replace("proposal-", "");
				//delete everything after .txt
				String str = ".txt";
				proposalNumber = deleteEverytingAfter(proposalNumber, str);
				proposalNumber = proposalNumber.trim();
				//if(proposalNumber!=null)
				//	permanentProposalNumber = proposalNumber;
			}
			else if(ln.trim().startsWith("--- a/") ){
				proposalNumber = ln.replace("--- a/", "");
				
				//delete everything after .txt
				String str = ".txt";
				proposalNumber = deleteEverytingAfter(proposalNumber, str);				
				proposalNumber = proposalNumber.replace(".txt", "");
				proposalNumber = proposalNumber.replace(proposalIdentifier+"-", "");
				proposalNumber = proposalNumber.trim();
				//if(proposalNumber!=null)
				//	permanentProposalNumber = proposalNumber;
			}
			else if(ln.trim().startsWith("+++ b/") ){
				proposalNumber = ln.replace("+++ b/", "");
				proposalNumber = proposalNumber.replace(".txt", "");
				proposalNumber = proposalNumber.replace(proposalIdentifier+"-", "");
				proposalNumber = proposalNumber.trim();
				//if(proposalNumber!=null)
				//	permanentProposalNumber = proposalNumber;
			}
			
			
			//diff --git
			else if(ln.trim().startsWith("Index:") ){
				proposalNumber = ln.replace("Index:", "");
				proposalNumber = proposalNumber.replace(".txt", "");
				proposalNumber = proposalNumber.replace(proposalIdentifier+"-", "");
				proposalNumber = proposalNumber.trim();
				//if(proposalNumber!=null)
				//	permanentProposalNumber = proposalNumber;
			}
		 //dec 2019 added
			else if(ln.startsWith("--- "+proposalIdentifier) ){
				proposalNumber = ln.replace("--- "+proposalIdentifier + "/trunk/", "");
				proposalNumber = proposalNumber.replace("(original)", "").replace(proposalIdentifier+"-", "").replace(".rst", "").trim();
				proposalNumber = proposalNumber.replace("---","").trim();
				if(proposalNumber.contains(".txt"))
					proposalNumber = proposalNumber.substring(0, proposalNumber.indexOf(".txt"));
				System.out.println("\tkk proposalNumber " + proposalNumber + " line: " + ln);
				//if(proposalNumber!=null)
				//	permanentProposalNumber = proposalNumber;
			}
			else if(ln.startsWith(proposalIdentifier+":") ){
				proposalNumber = ln.replace(proposalIdentifier+":", "").replace(".rst", "");
				//proposalNumber = proposalNumber.replace("proposal-", "");
				proposalNumber = proposalNumber.trim();	System.out.println("\tpp proposalNumber " + proposalNumber+ " line: " + ln);
				//if(proposalNumber!=null)
				//	permanentProposalNumber = proposalNumber;
			}
			else if(ln.startsWith("+"+proposalIdentifier+":") ){
				proposalNumber = ln.replace("+" +proposalIdentifier+":", "").replace(".rst", "").trim();	System.out.println("\t oo proposalNumber " + proposalNumber+ " line: " + ln);
				//proposalNumber = proposalNumber.replace("proposal-", "");
				//if(proposalNumber!=null)
				//	permanentProposalNumber = proposalNumber;
			}
			else if(ln.startsWith("--- "+proposalIdentifier+"-") ){ //jan 2021 ....was 'pep' b4
				proposalNumber = ln.replace("--- "+proposalIdentifier+"-", "").replace(".rst", "");	System.out.println("\tjj proposalNumber " + proposalNumber+ " line: " + ln);
				//proposalNumber = proposalNumber.replace("proposal-", "");
				//delete everything after .txt
				String str = ".txt";
				proposalNumber = deleteEverytingAfter(proposalNumber, str).trim();	System.out.println("\t nn proposalNumber " + proposalNumber+ " line: " + ln);
				//if(proposalNumber!=null)
				//	permanentProposalNumber = proposalNumber;
			}
			else if(ln.startsWith("--- a/") ){
				proposalNumber = ln.replace("--- a/", "").replace(".rst", "");
				//delete everything after .txt
				String str = ".txt";
				proposalNumber = deleteEverytingAfter(proposalNumber, str);				
				proposalNumber = proposalNumber.replace(".txt", "").replace("pep-", "").trim();	System.out.println("\t qq proposalNumber " + proposalNumber+ " line: " + ln);
				//if(proposalNumber!=null)
				//	permanentProposalNumber = proposalNumber;
			}
			else if(ln.startsWith("+++ b/") ){
				proposalNumber = ln.replace("+++ b/", "").replace(".rst", "");
				proposalNumber = proposalNumber.replace(".txt", "").replace("pep-", "").trim();	
				proposalNumber = proposalNumber.replace("pepdraft-", "").trim();	 // dec 2019 for cases ... +++ b/pepdraft-0435.txt
				System.out.println("\tee proposalNumber " + proposalNumber + " line: " + ln);
				//if(proposalNumber!=null)
				//	permanentProposalNumber = proposalNumber;
			}
		   
		   //jan 2021..
		   // diff --git a/bip-0137.mediawiki b/bip-0137.mediawiki
		   // index a367238..a88342c 100644
		   // --- a/bip-0137.mediawiki
		   // +++ b/bip-0137.mediawiki
		  
		   //for diif lines, mostly should come here
			else if(ln.startsWith("diff --git a/")) {
				System.out.println("\t diffline proposal number checking:  proposalNumber " + proposalNumber + " line: " + ln);
				proposalNumber = ln.replace("diff --git a/", "");
				//jan 2021..added for bips ....mediawiki
				proposalNumber = proposalNumber.replace(proposalIdentifier+"-", "").trim();
				proposalNumber = proposalNumber.replace(proposalIdentifier+"draft-", "").trim();	//dec 2019 ..for cases   ... diff --git a/pepdraft-0435.txt b/pepdraft-0435.txt
				proposalNumber = proposalNumber.replace(".txt", "").replace(".rst", "");
				proposalNumber = proposalNumber.replace("a/", "").replace("b/", "").trim();
											
				System.out.println("\t diffline proposal number checking:  proposalNumber " + proposalNumber + " line: " + ln);
				/*String str = ".txt";
				proposalNumber = deleteEverytingAfter(proposalNumber, str);
				proposalNumber = proposalNumber.trim();
				String str2 = ".rst";
				proposalNumber = deleteEverytingAfter(proposalNumber, str);
				proposalNumber = proposalNumber.trim();
				proposalNumber = ln.replace("--- pep-", "").replace(".rst", "");	proposalNumber = ln.replace("+pep:", "").replace(".rst", "").trim();
				proposalNumber = ln.replace("--- a/", "").replace(".rst", "");		proposalNumber = ln.replace("+++ b/", "").replace(".rst", "");
				proposalNumber = proposalNumber.replace(".txt", "").replace("pep-", "").trim();	System.out.println("\tee proposalNumber " + proposalNumber + " line: " + ln);
				*/
			}
		   
		   //jan 2021..added for bips ....mediawiki
		   //proposalNumber = ln.replaceAll(".mediawiki", "");
		   
		   proposalNumber = proposalNumber.replace("a/", "").replace("b/", "").trim();
		   //jan 2021 ...we add for bips
		   proposalNumber = proposalNumber.replace("-", ""); //for negative signs ###bips: -0013  //.replace("b/", "").trim();
		   
		   //dec 2019 we just get one proposal number from the result
		   // ww proposalNumber 0217 0217 line: diff --git a/pep-0217.txt b/pep-0217.txt
		   proposalNumber = proposalNumber.split("\\s+")[0];
			
		return proposalNumber;
	}
	
	public ArrayList <ProposalStates> checkStatesInsert(ArrayList <ProposalStates> proposalStatesList, String proposal, String status, String StatusTo, Date dateTime)
  	{ 
  		Boolean repeatedSentence = false;
  		Boolean found = true;
  		
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
		String [] commits = v_msg.split("Modified: "+proposalIdentifier+"/trunk/");
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
			for (String ln : lines)
			{			
			}			
			//Integer.valueOf(proposalNumber) 			
		}
	}

	private static void writeToDebugFile(String v_msg){  //, String proposalNumber) {
		//if(proposalNumber==null){
			try(FileWriter fw = new FileWriter("c:\\scripts\\outbig.txt", true);
				    BufferedWriter bw = new BufferedWriter(fw);
				    PrintWriter out = new PrintWriter(bw))
				{
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

	public static boolean isNumeric(String str)
	{
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
				change.getAuthor(),
				change.getDate(),
				change.getDateStamp(),
				change.getProposalTitle(),
				change.getEntireMessage());	
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
