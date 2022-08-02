package readRepository.postReadingUpdates;

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
import miner.process.PythonSpecificMessageProcessing;
import connections.PropertiesFile;
import utilities.PepUtils;
import utilities.ReturnNattyTimeStamp;

import com.google.common.base.Splitter;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

//import PeopleAspectAnalysis.SeparateStudyGroup2Functions;
import readRepository.readRepository.GenericMailingListReader_BaseElements;
import readRepository.readRepository.MessageReadingUtils;

//aug 2018...different combinatiosn of pep number in message subject
// this is implemented in teh generic read in script, but wasnt placed in right place  
//so this script is just to check and improve 
// if pep number is -1, and message subject has a pep number, assign it 
// else if there are pep numbers assigned based on the pep number found in message or message subject
//nov 2018, just keep running tis script, it will keep eliminating as peps with -1 with a pep in message subject will get assigned the proper pep, so with each loop lesser ones will be left
//When to use. 
//1. after reading all messages in - is part of post message update script, but can be script on its own for other purposes
//manually run this sql - " update allmessages set originalPEPNumber = pep;"
//2. at the time of reading messages in...exact matches are part of the reading in script at the moment but using levenstein distance will be implemented after testing ,,seeing results in db table
//3. when checking if message is for a pep in explore analyse repository gui

//Before running just check-  select * from allmessages_dev where pep <> originalPEPNumber; to make sure these two fields have same value,as the originalPEPNumber is a backup field for the original value

//july 2018, its not completely correct, but final version is very correct
//After running this version, I can say the script is working and is finalised. Just have to change if we want to include lists mailing list of every other....
//without including lists ml, its assigns pep number to most of the critical messages..90210 atleast 

public class UpdateAfterReading_MatchProposalNumbersInMsgSubject {
	//for cases when no pep number exists in message
	//we use the  pep tile to match
	//assign pep numbers to rows which dont have based on subject if they match the pep titles
	static MessageReadingUtils mru = new MessageReadingUtils();
	static PythonSpecificMessageProcessing pms = new PythonSpecificMessageProcessing();	
	//static SeparateStudyGroup2Functions f = new SeparateStudyGroup2Functions();	
	static MysqlConnect mc = new MysqlConnect();
	static Connection conn;
	static String updateQuery;
	static int i;
	static String tablenameToCheck = "allmessages";		//tablename to check...only ideas and dev and maybe try checkins
	static String tablenameToUpdate = "allmessages";
	static String debugTable = "proposaldetails_titlesforeachpep_debug";
	static String tableWithFirstLastProposalTitle = "pepstates_danieldata_wide";	//table to get first and last proposal titles
	static Levenshtein levenshtein = new Levenshtein();	// Levenshtein
	//here we increase and decrease Levenshtein_distance_threshold and see which is best value
	static Integer Levenshtein_distance_threshold = 3,Levenshtein_distance_threshold_small_terms = 2;
	static boolean toUpdate;
	static PepUtils pu = new PepUtils();	
	//nov 2017 - can this not be part of the main repository reading script?
	static List<Integer> proposalsMatched = new ArrayList<Integer>();		//list of all the proposal numbers matched in each message, should be emptied after checking each message
	static String proposalIdentifier = null; 	//identifier for matching in files
	
	//aug 2018
	static List<String> proposalNumberSearchKeyList = new ArrayList<String>(),proposalTitlesSearchKeyList = new ArrayList<String>();
	static List<Integer> proposalList = new ArrayList<Integer>();
	static List<String> ignoreList = new ArrayList<String>();
	static List<Integer> messageproposalNumberList = new ArrayList<Integer>(),proposalNumberFoundInMessageSubjectList = new ArrayList<Integer>();;
	
	
	public static void main(String args[])
	{
		String subject="",titleinfirststate="",subset="",sql,sql0,sql1,sql2; //,newName = null, bdflDelegate, newBDFLDelegate;
		Integer mid = null;
		int pepNumber,pep;
		Statement stmt,stmt0,stmt1,stmt2;		ResultSet rs,rs0,rs1,rs2;

		GenericMailingListReader_BaseElements gb = new GenericMailingListReader_BaseElements();
		PepUtils pu = new PepUtils();
		try
		{
			conn = mc.connect();	
			//f.totalNumberOfMessages(conn);		//modify this to include tablename
			//f.totalNumberOfUniqueMessages(conn);	//modify this to include tablename

			//write to properties file
			PropertiesFile wpf = new PropertiesFile();
			// wpf.WriteToPropertiesFile("includeEmptyRows", includeEmptyRows.toString());
			//includeStateData
			proposalIdentifier = wpf.readFromPropertiesFile("proposalIdentifier",false).toLowerCase();
			System.out.println("proposalIdentifier: " +proposalIdentifier);

			// initialise proposal numbers, in proposalNumberSearchKeyList and ignoreList
			gb.initProposalIdentifier(proposalIdentifier);	//is set in main when reading messages, but we need to call this function from here
			gb.initproposalNumberSearchKeyLists(ignoreList, proposalNumberSearchKeyList);
			//show all patterns being used to match
			for (String proposal : proposalNumberSearchKeyList) {
				//System.out.println(proposal);
			}

			// initialise proposalTitles
			proposalTitlesSearchKeyList.clear();
			boolean matchedproposalNumberInMessage,matchedproposalNumberInessageSubject = false;

			// get all the set of keys - keys for both maps should be same
			ArrayList<Integer> UniquePeps = pu.returnUniquePepsInDatabasePEPDetails();
			//PreparedStatement stat = c.prepareStatement("SELECT * FROM big_table",ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY); 
			//stat.setFetchSize(Integer.MIN_VALUE);	        	ResultSet results = stat.executeQuery();	
			//for (int j = UniquePeps.size()-1; j >=0 ; j--) {	//start from back as 1 matches so many instances 1, 10, 11, 21, 31..etc
			//	Integer k = UniquePeps.get(j);	System.out.println("Checking Propsal number: " +k);
			//just select max(messageid) from allmessages
			for(int k=0; k < 888584; k++) {  //if stuck change starting point    ...3015239  ..7000000 or nearest 100,000
				if(k%100000==0) {
					System.out.println("100000 done,k: "+ k);
				}
				sql0 = "SELECT messageid, pep, subject from "+tablenameToCheck+" where "  //messageid = 90910";
						+ " messageid = " + k  + " and "
						+ " pep = -1 and subject IS NOT NULL ";						
						//+ " and folder NOT like '%lists%' "  //LIMIT 5000 //order by pep asc LIMIT 5000  ..or folder like '%idea%'
						//+ " and messageid = 128616 ;";
						//+ " and (subject like '%"+k+"%') ;"; 		//we dont for pep 100, 200, 300, 400 by replacing digit here
				
				//second part you have to run is for those instances where for a message, based on message body a pep number is assigned but not based on mesage subject
				///so i have to add a new row for that message with the newfound pep number
				//so for each mid, check for peeps in message subject and then create a new row and assign the newfound pep# 
								
				stmt0 = conn.createStatement();				rs0 = stmt0.executeQuery( sql0 );	
				//for each message
				while (rs0.next()) {	
					proposalsMatched.clear();
					mid = rs0.getInt(1);					pepNumber = rs0.getInt(2);					subject = rs0.getString(3);	
					if(subject==null || subject.isEmpty()) {}
					else {
						if (subject.trim().toLowerCase().startsWith("re ")){
							subject = subject.replace("re", "").trim();
						}
					
						//System.out.println("\tFound Candidate ("+pepNumber+") found in Msg Subject: "+subject);
						//aug 2018..matching pep number in message subject...this was incorrectly code in the read in script so have to run it here				
						//it didnt work, if i kept it in the lopp..dont know why i kept it there...so i keep a seprate script for it
						//Now for all unassigned and assigned messages we:
						//Check for pep # in message subject using different patterms....sub main Proposal number matching - check in proposal number is found in message subject
						//we pass a different list this time - proposalNumberFoundInMessageSubjectList
						matchedproposalNumberInessageSubject = gb.matchproposalNumbersAddToList(proposalNumberSearchKeyList, ignoreList, subject, matchedproposalNumberInessageSubject,proposalNumberFoundInMessageSubjectList);						
						//the proposal number would be added to list ..proposalNumberFoundInMessageSubjectList	
						String tableName = "allmessages";
						//$$										System.out.println("\t\t\tGoing to insert data into database proposal = " + proposalList.get(g) + " | "+  tableName + " | " +  rootFolder + " | "+ fileEntry.getName() + " | " + messageID + "|" + justDate);
						for (int g = 0; g < proposalNumberFoundInMessageSubjectList.size(); g++)
						{
							int h = proposalNumberFoundInMessageSubjectList.get(g);
							
							//if the identified pep is allocated for that messageid, then do nothing
							//else 
							// 	if pep = -1, update pep number
							//	else, add...select for update
//							sql1 = "SELECT messageid, pep, subject from "+tablenameToCheck+" where messageid = " + k + " and pep = "+h+" ;";
//							stmt1 = conn.createStatement();				rs1= stmt1.executeQuery( sql1 );
							//boolean found=false;
//							if (rs0.next())	{	//if there is an entry for each pep number found in msg subject
								//do nothing
//							}
//							else {
								//check if there is an entry in -1							
								if(pepNumber == -1) {	//if only -1 has been assigned, meaning no other pep number is assigned
									updateTableWithPEPNumber(mid,h);	
								}
								else { //for instances where messages is assigned to other peps but not to current pepe, we insert
									//select for update..with new messageid
								}
//							}
							System.out.println("\t\tUpdating PEP number ("+pepNumber+") with ("+h+") found in MsgID ("+mid+") Subject: "+subject);
						}
					}
					proposalNumberFoundInMessageSubjectList.clear();
				}	//end while loop
		    }  //endfor loop
			System.out.println("Processing Finished");
		}   catch (SQLException se)	    {
			System.err.println("Got an sql exception! ");			System.err.println(se.getMessage() + " mid " + mid);
		}   catch (Exception e)	    {
			System.err.println("Got an exception! ");			System.err.println(e.getMessage() + " mid " + mid);
			System.out.println(StackTraceToString(e)  );	
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
	
	// before running if theya rfe the same
	//select * from allmessages_dev where pep <> originalPEPNumber;
	//eventually we would update the senderName column in the allmesaages table

	
	private static void updateTableWithPEPNumber(Integer mid, Integer pep)		throws SQLException {
		  updateQuery = "update "+tablenameToUpdate+" set pep = ? where messageID = ?";
		  PreparedStatement preparedStmt = conn.prepareStatement(updateQuery);
		  preparedStmt.setInt (1, pep);		  preparedStmt.setInt(2, mid);
		  i = preparedStmt.executeUpdate();
		  if(i>0)   {
		          System.out.println("success");
		  }
		  else  {
		         System.out.println("stuck somewhere mid " + mid + " NewPEP " + pep);
		    }
		  updateQuery =null;
	}
		
	public static String backPad(String text) { return text+" "; }
	
	
	private static String replacePunctuationCorrectly(String str) {
		if (str.contains("."))
			str = str.replaceAll(".", " ");
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
}
