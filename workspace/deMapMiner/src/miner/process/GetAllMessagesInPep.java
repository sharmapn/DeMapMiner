package miner.process;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

//import wekaExamples.MyFilteredClassifier;
import callIELibraries.JavaOllieWrapperGUIInDev;
import readRepository.readMetadataFromWeb.GetProposalDetailsWebPage;
import callIELibraries.ClausIECaller;
import callIELibraries.ReVerbFindRelations;
import callIELibraries.UseDISCOSentenceSim;
import connections.MysqlConnect;
import de.mpii.clause.driver.ClausIEMain;
import miner.processLabels.TripleProcessingResult;
import utilities.Utilities;

public class GetAllMessagesInPep {
	static PythonSpecificMessageProcessing pm;
	public GetAllMessagesInPep(){
		pm = new PythonSpecificMessageProcessing();
	}

	public static void getAllPEPMessages(ProcessingRequiredParameters prp) 
	{
		Connection connection = prp.getConn();
		Integer pepNumber = prp.getPEPNumber(), message_ID = null, counter = 0, dataFoundCounter = 0, discussionMessageCounter = 0, processedCounter=0,identifierCount;
		String message = "", author = "",authorrole="", v_date = "", status, statusFrom = "", statusTo = "", subject = "",md5 = "", outputDiscussionDate = "", wordsFoundList = "", v_inReplyTo = "", lineOutput = "",
				prevSentence = "", folder="", file="";
		Boolean v_required, discussionOn = false, discussionStart = null, discussionEnd = null,	outputDiscussion = false;
		Date m_date = null; 			String startDateString ="";
		Timestamp v_datetimestamp;		Integer messageStartIDForCommitTable = 5000000;
		//first lets process all state data for that pep and insert in db table
		//Boolean includeStateData = prp.getIncludeStateData();
		//find state data/ Daniel data
		
		System.gc();//destroy all objects
		
		Boolean v_stateFound = false;
		try {			
			//we dont process state data if we only going to extract current and bearby sentences for msoem terms
			if (prp.getIncludeStateData() && prp.getCurrentAndNearbySentences()==false) { //if we want to include statedata in the analysis
				//i think we decided that we will use danile state data instead of extracting our own as this would reduce some of our false results for the timebeing so we can just extract other states
				//one reason could be that the state commit date woudl be correct, and our extracted state dates may be different which would mess up the process flow
				String sqlStates = " SELECT date2,datetimestamp, author, analysewords, messageID, subject,folder,file, required, date2 as date3,  "
						+ "inReplyTo,IdentifierCount from "+prp.getPEPStatesTableName()  +" WHERE pep = " + pepNumber 
						+ " order by messageid asc"; //date2

				Statement stmt0 = connection.createStatement();				ResultSet rs0 = stmt0.executeQuery( sqlStates );
				System.out.println("\nPEP: " + pepNumber + " Number of PEP States: " +prp.mc.returnRSCount(sqlStates, prp.conn));

				while (rs0.next())
				{     //check every message     
					processedCounter++; 				
					counter++;			
					v_date = rs0.getString("date2");			v_datetimestamp = rs0.getTimestamp("datetimestamp");		author = rs0.getString("author");		message = rs0.getString("analysewords");
					folder =  rs0.getString("folder");			file =  rs0.getString("file");								authorrole = "bdfl"; //role for commit states is always bdfl
					message_ID = rs0.getInt("messageID");		subject = rs0.getString("subject");						v_required = rs0.getBoolean("required");
					identifierCount = rs0.getInt("IdentifierCount");

					startDateString =rs0.getString("date3");					DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
					Date startDate;		m_date  = df.parse(startDateString);	//wmay raise parse exception					 
					v_inReplyTo = rs0.getString("inReplyTo");

					if (message == "null" || message == null || message == "" || message.isEmpty() || message.length() <=5){
						if(prp.getOutputfordebug())
							System.out.println("Message empty returning : "+ message_ID);
						continue;
					}

					//prevent checking all messages, only the ones with state data only
					if (message_ID >= messageStartIDForCommitTable) {	
						if(prp.getOutputfordebug())
							System.out.println("\tSelected State: " + message); 
						//messageStartIDForCommitTable is set somewhere as 5,000,000								
						CaptureStates cs = new CaptureStates();
						v_stateFound = cs.captureStates(message, pepNumber, m_date,v_datetimestamp, message_ID, author,
								authorrole, prp.getWriterAll(), prp.getWriterForDisco(), prp.getStatusProcessingResult(), prp);
					}
				}
				stmt0.close();
			}

			//Now we process all other messages for the pep
			//due to performance issues, we process each message of the pep at a time rather than reading all messages for that pep and then rocessing, which will require too many page swaps
			//			String sqlMain = " SELECT messageID from allmessages WHERE pep = " + pepNumber + " order by messageid asc;";

			//I am not sure why we need to order the query, with date  -- as postprocessing will do ordering by datetimestamp, so no need to do ordering here
			//but for restart we need to order by messageid

			//WHY NOT INCLUDING THE FOLLOWING TABLE IN OUR MAIN STUDY july 2017 - asof now no longer needed as pep titles fuzzy matched by another script which has to be run  
			//				+    "(SELECT date2, sendername, analysewords, statusFrom, statusTo, statusChanged,  messageID, subject,folder, required, date2, inReplyTo from allpeps_ideaspeptitles WHERE pep = " + pepNumber + " order by date2) ";	


			List<String> tempMessageList = new ArrayList<String>();
			//set candidate list = null
			prp.pms.setListNull();
			//clear candidate sentence list - this list checkes repeted sentences and idea combinations
			//prp.tripleProcessingResult.clear();
			if(prp.getProcessMining())  //if we are process mining, then only 
				prp.clearProcessingLists();
			//prp.statusProcessingResult.clear();
						
			//now run the main sql for processing
			String sqlMain =  "";	
			if(prp.isProcessMessageID()) {	//if we just want to process a particular messageid
				sqlMain= " SELECT date2,datetimestamp, clusterBySenderFullName as author,authorsrole, analysewords,  messageID, subject,folder,file, required, date2 as date3,inReplyTo,IdentifierCount "  
					   + " FROM allmessages WHERE  messageID = " + prp.getMessageID() + ";";
			}
			else {  //normally we want all messages for a particular pep
				sqlMain= " SELECT date2,datetimestamp, clusterBySenderFullName as author,authorsrole, analysewords,  messageID, subject,folder,file, required, date2 as date3,inReplyTo,IdentifierCount "  
				       + " FROM allmessages WHERE  " + prp.getProposalIdentifier() + " = "  + pepNumber + " order by messageid asc";
			}
			
			Integer totalMessagesToProcessForProposal = prp.mc.returnRSCount(sqlMain, prp.conn);
			System.out.println("\nPEP: " + pepNumber + " Number of messages: " + totalMessagesToProcessForProposal);
			
			Statement stmtMain = connection.createStatement();
			ResultSet rsMain = stmtMain.executeQuery( sqlMain );
			
			//check every message for the proposal
			while(rsMain.next()) {
				processedCounter++; 
				//if(processedCounter%100==0)
				//	System.out.println("Processed message count "+processedCounter);
				message_ID	 	= rsMain.getInt("messageID");				//System.out.println("Proposal "+pepNumber+", Processing new Message_ID: " + message_ID + "---------------------------");
				//dataFoundCounter++;
				counter++;			
				v_date 			= rsMain.getString("date2");				v_datetimestamp = rsMain.getTimestamp("datetimestamp");
				author 			= rsMain.getString("author");				authorrole 		= rsMain.getString("authorsrole");
				message 		= rsMain.getString("analysewords");			folder 			= rsMain.getString("folder");				
				file 			= rsMain.getString("file");  				subject 		= rsMain.getString("subject");				
				v_required 		= rsMain.getBoolean("required");			identifierCount = rsMain.getInt("IdentifierCount");
				
				//dec 2018 quick and dirty way to check processing results for one message in pep
				/*if (message_ID != 261760) { continue; } //
				else {
					System.out.print("Found messageID " + message_ID + " processing");
				}
				*/
				try {
					if (v_date == null) {}
					else {
						startDateString =v_date; //rsMain.getString("date2");
						DateFormat df = new SimpleDateFormat("yyyy-MM-dd");			Date startDate;
						m_date  = df.parse(startDateString);			//will raise parse exception
						//String newDateString = df.format(startDate); 					//System.out.println(newDateString);
					}
				}
				catch(Exception exc) {
					System.out.print("Date error 32: "+ startDateString);		exc.printStackTrace();			System.out.println(StackTraceToString(exc)  );	
					//System.out.println(m.getMessage_ID() + " ______here  " + e.toString() + "\n" );				
				}
				v_inReplyTo = rsMain.getString("inReplyTo");
				wordsFoundList = null;
				//			System.out.println("here v");

				//BEFORE DOING ANYTHING, Lets check if we need to process this message
				boolean proceed= false;
				if(prp.getRestart()==true){	//System.out.println("\t PepRestart = true  ");
					if(pepNumber.equals(prp.getPepNumberForRestart() ) ) { //System.out.println("\t Pep number equal ");
						if(message_ID > prp.getMessageIDForRestart()) {	//lets skip that message
							proceed=true;			//System.out.println("PEP ("+pepNumber+"),message_ID ("+message_ID+") > prp.getMessageIDForRestart() - we proceed");
						}else {
							proceed = false;		//System.out.println("PEP ("+pepNumber+"),message_ID ("+message_ID+") <> prp.getMessageIDForRestart() - we skip");							
						}
					}else {	//for all other peps, dont worry about proposal numbers less than the current one, as they wont reach here, look at selected all section in main script, where the loop takes care of this
						proceed = true;	         // System.out.println("processing seprate pep now: "+ pepNumber + " messageID: "+ message_ID + "pep number for restart= "+prp.getPepNumberForRestart());
					}
				}
				else {	//just to make sure
					proceed=true;	
					//System.out.println("\t proceed = true  ");
				}
				
				//some message will give error so we skip..these are read in from the prop file				
				for(Integer d: prp.getSkipMessagesForProcessing()) {
					if(message_ID.equals(d)) 
						{proceed=false;}
				}
//$$				System.out.println("\t PepRestart:  "+prp.getRestart());
				//System.out.println("\t here AA  "+prp.getPepNumberForRestart() + " mid: "+ message_ID+ "pepidforretsrat: " + prp.getMessageIDForRestart());
				if(prp.getRelationExtraction() &&  proceed) { 
					//For relation extraction we dont want to process same message again
					if(prp.getRelationExtraction()) {	//dont need to check for sentence extraction as all messages would be there					
						Statement statement2 = prp.getConn().createStatement();
						ResultSet resultSet2 = statement2.executeQuery("select messageid from "+prp.getTableToStoreExtractedRelations()+" where messageid = "+message_ID+" LIMIT 1;");
						if (resultSet2.next()) {
							proceed = false;	
//$$$						System.out.println("\t\tMessage processed before "+message_ID);						
						}
					}
				}
				if(!proceed) {	
					System.out.println("\tMessage skipped, counter " + processedCounter + "/"+ totalMessagesToProcessForProposal +" ");
					continue;
				}	
				Integer toRunLimit = prp.getRunForNumberOfMessagesLimit(), currentCounter = prp.getProcessedMessageCounter(); //counter is initialised to 0 anyway, so dont worry
				if(currentCounter >= toRunLimit) {
					System.exit(0);		System.out.println("We exit since the RE script slows down after processing some messages, toRunLimit: "+toRunLimit+" message and Loop should start again");
				}
				else
					prp.setProcessedMessageCounter(currentCounter+1);
				
				//if we get stuck, we want to know on which message, we update value in table
/*				Calendar calendar = Calendar.getInstance();
				Timestamp currentTimestamp = new java.sql.Timestamp(calendar.getTime().getTime());
				String query1 = "insert into trackMessageProcessing (date,pep, messageID,messageSubject) VALUES (?,?,?,?);";
				PreparedStatement preparedStmt1 = prp.getConn().prepareStatement(query1);
				preparedStmt1.setTimestamp(1, currentTimestamp);	preparedStmt1.setInt   (2, pepNumber);				preparedStmt1.setInt   (3, message_ID);				preparedStmt1.setString   (4, subject);
				preparedStmt1.executeUpdate();
*/
				//initialise one message object
				Message m = new Message(); 
				m.setMessageData(message, v_date,v_datetimestamp, author,authorrole, folder, file, message_ID, subject, v_required, m_date, v_inReplyTo, wordsFoundList,identifierCount);
						//proposalHasAcceptedLabel,accepted_sentence,proposalHasRejectedLabel,rejected_sentence, accepted_date, rejected_date);
				//System.out.println("v_datetimestamp : "+ v_datetimestamp);
				boolean toProcessMessage= false;
				
				// System.out.println("\n---------------------Date UnMatched " + " date in array " + p[pepNumber].messages[counter].getClassifierDate() + " new date" + v_date);	
				//			System.out.println("Started processing another message : "+ message_ID);
				if (message == "null" || message == null || message == "" || message.isEmpty() || message.length() <=5){
					if(prp.getOutputfordebug())
						System.out.println("Message empty returning : "+ message_ID);
					/*String query2 = "update trackMessageProcessing SET messageEmpty = ? where pep = ? and messageID =?	;";
					PreparedStatement preparedStmt2 = prp.getConn().prepareStatement(query2);
					preparedStmt2.setInt   (1, 1);					preparedStmt2.setInt   (2, pepNumber);			preparedStmt2.setInt   (3, message_ID);
					preparedStmt2.executeUpdate(); */
					continue;
				}
				//System.out.println("\t Here b 1");
				//some lines in message start with a charater which causes SNLP to recognise its as sentence delimeter like !
				//we basically remove these chars if they are in front of lines in paragrapgh 
				//should be handled while reading in from txt files
				String charsToRemoveFromLine = "!";
				while (message.contains("\n!")) {		//remove all exclamation just after newline
					message = message.replace("\n!","\n");
				}
				while (message.contains("\\r\\n!")) {		//remove all exclamation just after newline
					message = message.replace("\\r\\n!","\\r\\n");	//try this as well
				}
				
				/*  not needed here as coded in a seprate script altogetehr...
				int option =0; 
				boolean allowZero = true; 
				String lowerCaseMessage = message.toLowerCase();
				///ELIMINATE MESSAGES WHICH DONT BELONG TO THIS PROPOSAL
				if(prp.getElimtaeMessages().processMessage(message_ID, message, pepNumber, subject,prp, option,allowZero,lowerCaseMessage)) {
					continue;	//if we need to eliminate, continue to next message
				}
				*/
				
				//0. check for special keywords in messages which have messages for several peps in one message
				//but they must exist in a line of its own
				//System.out.println("\t Here c prp.getRelationExtraction(): "+prp.getRelationExtraction());
				if(prp.getRelationExtraction()) {	
					processSection(prp, pepNumber, message_ID, identifierCount, message, author, m_date,v_datetimestamp, m);	
					//we dont need all the below code for relation extraction, all that matters is that all the content in the message is triple extracted
					// there is a check above to make sure a message is not double processed for triple extraction
				}
				else {	//System.out.println("\t Process Mining here 78^");  //we do process mining option
				//if we get stuck, we want to know on which message, we update value in table
				/*String query4 = "update trackMessageProcessing SET proceed = ? where pep = ? and messageID =?	;";
				PreparedStatement preparedStmt4 = prp.getConn().prepareStatement(query4);
				preparedStmt4.setInt   (1, 1);					preparedStmt4.setInt   (2, pepNumber);					preparedStmt4.setInt   (3, message_ID);
				preparedStmt4.executeUpdate();
				*/
				//first handle commit messages
				//if message is from checkin mailing list, if it has  checkin messages we split it and process in between two checkin sections seprated by
				// diff --git a/pep-0405.txt b/pep-0405.txt

				//check if its from checkins mailing list
				//if so, try splitting (if it has any 'diffs' )
				// check each section of diffs to see if its fro current pep
				//At the moment if the number of proposal identifiers (peps) are more than 5, the message is not processed
				//we can do better than this 
				// if they are checkin messages, we can split the message by the number of lines strating by diff
				String[] commitDelimeters= {"diff --git a","diff -C2 -d","--- NEW FILE:","Modified: peps/trunk/pep-","Added: peps/trunk/"};  //"diff --git a"; 
				if (subject==null || subject.isEmpty() ) {	//technically shouldnt reach here
					subject= "";
				}
				boolean messageProcessed = false,allowZero=false; int option = 1;
				if(subject.contains("checkins")) {	//if message is a commit message, we still process but upon splitting in sectiosn first and identifying the right sections for that proposal
					for (String del : commitDelimeters) {
						if(message.contains(del)) {	//System.out.println("\n\tmessage contains commitDelimeter ");							
							message = message.replaceAll(del, "pankaj"+del);//we add some charaters in front so that splitting wont loose the delimeter //System.out.println("\tmessage contains commitDelimeter ");
							//int c=0;
						}
					}
					for(String section: message.split("pankaj")){
						int start = 0; //section.indexOf(del), 
						int end = section.indexOf(".txt");
						if(start == -1 || end == -1) { continue; }
						String line = section.substring(start,end) ;	//get everything in that line somehow				
						int option2 =1; //text shud only contain current proposal/pep
						boolean allowZero2 = false;
						
						if(prp.getPms().checkTextContainsProposalNumber_WithOptions(line.toLowerCase(), pepNumber,option,allowZero)) {	//check if its for the current pep, if no disregard	
							//process section 
//$$							System.out.println("\t Section Contains OnlyCurrent Pep Number ");// + section);	
							processSection(prp, pepNumber, message_ID, identifierCount, section, author, m_date,v_datetimestamp, m);
						}
						
					}
					messageProcessed= true;
				}
				
				//maybe we can code that all checkin messages be processed the above way..depending on whether all checin messages have multiple diffs
				//have to see all checkin message to ascertain that
				//if found so, we can change the below if to be under an else 

				if(!messageProcessed && message.contains("[SJB]") || message.contains("[TAB]") || message.contains("[SNIP]"))
				{   //additional keywords like this are
					//System.out.println("Message contains [SJB] or [TAB] or [SNIP]");
					//Dont remove quoted text as in below
					//For instances where a message has many sections and they need to be checked separately
					//EACH OF THESE SECTIONS will be for one PEP only, so we split and process only those sections which mention current PEP
					//String sections[] = message.split("[SJB]|[TAB]|[SNIP]");
					String delimeters[] = {"SJB","TAM","SNIP"};						String del = "#!k&#!k&#!k&#!k&#";
					//replace delimeters with new delimeter
					for(String p: delimeters){
						if(message.contains(p))	//fileStr
							message = message.replaceAll("\\["+p+"\\]",del);
						//System.out.println(test);
					}

					//String sections[] = test.split("\\[SJB\\] | \\[TAM\\] | \\[SNIP\\]");
					//String sections[] = test.split("#!#!#!#!#");
					//System.out.println("Message has delimeters...[SJB] or [TAB] or [SNIP]....splited array length: "+sections.length);
					Integer pepNum;
					System.out.println();					
					System.out.println("Message has delimeters...[SJB] or [TAB] or [SNIP]....splited array length: "+message.split(del).length);//+sections.length);

					for(String section: message.split(del)){							
						//but pass pep number after finding it, get first line which contains pep
						//process each section separately, only process those sections which contain current pep

						//----START						
						//for messages which have lots of pep numbers, eg checkin messages, etc, we only want to process those text after a pep number
						//if only one section and one pep number, dont split further by pep number
						//check how many peps this message is assigned
						//		       		Integer count = prp.pms.getNumberOfPepsAssignedWithTheMessage(message_ID);
						//if it is more than 5, then we assume its a checkin message..note subject can be checked to see it it has checkin mentioned
						//and we extract only the text after the ppe number
						processSection(prp, pepNumber, message_ID, identifierCount, section, author, m_date,v_datetimestamp, m);
						//----END
						
						//march 2020...we need this data for thesis
//						prp.getInsertMessageTracking().insertMessageTracking( prp.getConn(),  pepNumber,  message_ID,  "SJB");
						
					}					
				}
				else{	//if message does not contain the above identifiers or is not a commit message
					processSection(prp, pepNumber, message_ID, identifierCount, message, author, m_date,v_datetimestamp, m);
				}			
			
			//else {
				/*String query5 = "update trackMessageProcessing SET proceed = ? where pep = ? and messageID =?	;";
				PreparedStatement preparedStmt5 = prp.getConn().prepareStatement(query5);
				preparedStmt5.setInt   (1, 1);					preparedStmt5.setInt   (2, pepNumber);					preparedStmt5.setInt   (3, message_ID);
				preparedStmt5.executeUpdate(); */
			//}
			//VERY IMPORTANT
			//Check with parent and child peps to see if its the same pep number for the current processing
			//      		Boolean samePeps = checkParent(message_ID, pepNumber, subject, v_inReplyTo, connection); //check the parent of the message to identify which pep it is talking about       		
			//These are messages which have state change information
			//we just output these states and do nothing else
			
			}	//end else for all other puposes other than relation extraction
				message = null;
				System.gc();    	   
				System.out.println("\tProposal "+pepNumber+" Message ID ("+message_ID+") Msg completed counter " + processedCounter + "/"+ totalMessagesToProcessForProposal +" Cleanup completed, "
						 + "Restart counter/Limit ("+ currentCounter+"/"+ toRunLimit+")"); //+ "Msgs b4 restart:" + (toRunLimit-currentCounter)); //
				
			} //END WHILE
			stmtMain.close();
			//after processing each pep, clear the arraylist of sentences
			//prp.getPms().outputAndClearCandidateSentenceList(prp.getTripleProcessingResult());
			
			//march 2020..there were some problems as not all substates were captured...so we restart processing after each pep
			//pep 102 does not capture any substates therefore it keeps repeating in loop for that value
			if(prp.getRestartAfterEachProposal()) {
				checkInsertDummyValue(prp, pepNumber);
				System.exit(0);	
			}
			
		}
		catch (ParseException e) {			e.printStackTrace();		} 
		catch (SQLException sql) {			System.out.println(StackTraceToString(sql)  );		}
		catch (Exception ex) {			ex.printStackTrace();			System.out.println(StackTraceToString(ex)  );	
			//System.out.println(m.getMessage_ID() + " ______here  " + e.toString() + "\n" );
		}
	}
	
	//MARCH 2020...a dummy value for each pep has to be inserted or the loop will keep on processing the same ppe if no substates are found
	//as happenned for pep 102
	public static void checkInsertDummyValue(ProcessingRequiredParameters v_prp, Integer pep)
	{			
		String sql = "INSERT INTO results (pep) " + "VALUES (?)"; //  ,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?)"; //44
		try {	
			PreparedStatement statement = v_prp.getConn().prepareStatement(sql);
			statement.setInt	(1, pep);		
			int rowsInserted = statement.executeUpdate();
			if (rowsInserted > 0) {
				System.out.println("DUMMY SVO Inserted in DB successfully!"); //, AR: "+ AR+ ", " +resultObject.getLabelSubject() + " , " + resultObject.getLabelRelation() + " , " + resultObject.getLabelObject());
			    //System.out.println("A new result record was inserted in DB successfully!");
			}
		} catch (SQLException e) {		
			e.printStackTrace();
		} catch (Exception ex) {
			// TODO Auto-generated catch block
			System.out.println("Exception while inserting SVO...1221" + ex.toString());
			ex.printStackTrace();
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

	private static void processSection(ProcessingRequiredParameters prp, Integer pepNumber, Integer message_ID,Integer identifierCount, String messageOrSection, String author, Date m_date,
			Timestamp v_dateTimeStamp,	Message m) {
		String wordsFoundList;
		boolean proceed = true;
//		System.out.println("Started Processing Each Section of message");				
		//Not sure if we need the code below
		if(!prp.getRelationExtraction()) {
			if (identifierCount>5){	//Number of peps in this message ( "+message_ID+" ) is more than 5, extracted text for this pep
				messageOrSection = prp.getSMUI().splitMessageByUniqueIdentifierReturnTextAfterIdentifier(prp.getPEPNumber(),message_ID,messageOrSection);
				m.setMessage(messageOrSection);
//$				System.out.println("Number of peps in this message ( "+message_ID+" ) is more than 5, extracted text for this pep "+pepNumber);
//$				System.out.println("--------------START EXTRACTED TEXT FROM MESSAGE");
//$				System.out.println(messageOrSection);
//$				System.out.println("--------------END EXTRACTED TEXT FROM MESSAGE ");
				proceed = false;
			}
		}
//		System.out.println("Started Processing extracted section text");
		if(messageOrSection.length() < 1000000) 
		{ // && (StatusInfoFound ==false)) {  //&& (v_required == true)) {	   //this is where the error is 
			//	       			System.out.println("\n A. wordsFoundList --------------------------------------------------- " + wordsFoundList+ message_ID);
			
			//System.out.println("Message - in all other cases");
			messageOrSection = prp.getPsmp().removeQuotedText(messageOrSection);  //remove quoted text from email message
			//This is temporarily disabled as we split paragraphs on blank lines
			//Message = pm.handleBlankLines(Message); //handle blank lines - should start new sentence
			
					//main processing method
			prp.casm.getAllSentencesInMessage(m,prp);
				
				//else {	//if RelationExtraction is false, then other two would be ideally set to true
				//	prp.casm.getAllSentencesInMessage(m,prp);
				//}
			
		} //end if
	}
	
	@Override
    protected void finalize() throws Throwable
    {
        //System.out.println("From Finalize Method, i = "+i);
    }

}
