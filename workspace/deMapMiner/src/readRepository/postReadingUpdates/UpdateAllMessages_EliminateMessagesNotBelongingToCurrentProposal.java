package readRepository.postReadingUpdates;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import connections.MysqlConnect;
import miner.process.ProcessingRequiredParameters;

//how to evaluate- check all these records where eliminateMessage is set to true
public class UpdateAllMessages_EliminateMessagesNotBelongingToCurrentProposal  {
	static Connection conn =null;
	static String updateQuery="";
	static ProcessingRequiredParameters prp = new ProcessingRequiredParameters();
	static boolean  ifMessageSubjectContainsCurrentPepNumber = false,ifMessageSubjectContainsOnlyOtherPepNumbers = false, ifProposalAtTheEndofMessage = false, ifLastMessagesectionContainsPepNumbers = false;	
	public static void main(String[] args) {
		//we want to run this script across all peps and populate the allmessages table with an additional column - and see how it goes
		//and we can evaluate that column while manual analysis..if ok, we can consider the column while automatic processing and wont need this script while processing reducing processinmg time
		
		connections.MysqlConnect mc = new MysqlConnect();		conn = mc.connect();		Statement stmt = null;
		int option =0; 		boolean allowZero = true, processMessage=true; 
		try {
			
			System.out.println();
			for(int i=0;i<4000; i++) { //0 - 4000
				stmt = conn.createStatement();
				// results table or postprocessed table										//5											//10
				ResultSet rs = stmt.executeQuery("SELECT pep,date2,subject,datetimestamp, messageid, email from allmessages where pep = "+ i +" order by datetimestamp asc");  //date asc
				
				while (rs.next()) {					
					Integer pepNumber = rs.getInt("pep"); 	Date dt = rs.getDate("date2"); 	Timestamp dts = rs.getTimestamp("datetimestamp");	String email = rs.getString("email");
					Integer message_ID = rs.getInt("messageID"); String subject = rs.getString("subject");
					if (message_ID.equals(43235)) {
						processMessage= true;
						processMessage = processMessage( message_ID,  email,  pepNumber, subject, prp,  option,  allowZero, email.toLowerCase());
						updateTableWithProcessMessageField(conn, message_ID, processMessage);
						System.out.println("Processed PEP " + i + " messageid " + message_ID + " processMessage: "+processMessage);  
					}
				}	//end while
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//returns true if we should skip processing the current message, false we was want to process same message
	//we may want to implement this while automatic processing, but may not want to during manual analysis - as we want to see all that there is
	//have to also cater for pep titles
	
	//June 2018 ...ONE THING LEFT ..IF MESSAGE BODY CONTAINS MORE THAN ONE PROPOSAL ..we will have to see example of these cases and then code it..its complex this one
	//ITS IMPLEMENTED FOR COMMITS AND CHEKINS AT LINE 228 OF GETALLMESSAGES.JAVA
	//We can implement similar way here...splitting message by proposal and cheking which one it belongs to - based on which one teh text section follows
	//we would have to consider the 'numberofpepsmentionedinmessagefield'
	
	//FULL EXPLANATION HERE
	//sometimes we want a function similar to this one but it should check adn make sure only the current pep is mentioned
	//we can code everything in this function with these options
	//   0 -- only other peps, 
	//   1 -- only current pep, 
	//   2 - other pep exists, regardless if current pep exists, 
	//   3 - current pep exists, regardless if other pep exists, (added may 2018)
	//	 boolean allowProposalZero -- allow pep 0 as several peps  have pep zero modified and therefore included in their message subjects 
	
	public static boolean processMessage(int message_ID, String message, Integer pepNumber,String subject, ProcessingRequiredParameters prp, int option, boolean allowZero,String lowerCaseMessage) {		
		allowZero = false;	
		try {
			System.out.println("Matching of Proposal Number and Title Started");
			System.out.println("First Check: Checking if Message Subject is for current Proposal , if so we process message Msg Subject: || " + subject);
			lowerCaseMessage = lowerCaseMessage.toLowerCase();			
			//0. First and foremost check, which if is true, then we dont need to proceed further
			//if the current pep number is found in message subject, we return false and process current message
			ifMessageSubjectContainsCurrentPepNumber = false;					//we dont allow zero in subject line
			ifMessageSubjectContainsCurrentPepNumber = prp.getPms().checkTextContainsProposalNumber_WithOptions(subject.toLowerCase(), pepNumber,3,allowZero);	//option=3, allowzero is false
			if (ifMessageSubjectContainsCurrentPepNumber) {	
				System.out.println("\tMessageSubjectContainsCurrentPepNumber - therefore we process. Msg Subject: || " + subject);
				return true;	//current pep is found, no need to prove anything more, just process processing current message
			}	
			else { System.out.println("\tMessage Subject Does NOT Contain Current PepNumber - we check more to decide if process Msg Subject: || " + subject);  }
			//can also check pep title here..its checking inside teh function
			
			//1. Message Subject. If message subject has only another pep mentioned...we dont want to process that
			//check subject if it contains other PEP
			//if so, dont process teh message		--  its only abt other peps	
			System.out.println("Second Check: Checking if Message Subject is for any other Proposal , if so we skip message Msg Subject: || " + subject);
			ifMessageSubjectContainsOnlyOtherPepNumbers = false;					//we allow zero in subject line
			ifMessageSubjectContainsOnlyOtherPepNumbers = prp.getPms().checkTextContainsProposalNumber_WithOptions(subject.toLowerCase(), pepNumber,option,allowZero);				
			//we keep this code here as a state or reason for a pep wont be included in a message which has some other pep mentioned
			//but for some reason at some other place this code is commented as the price of information would be useful tehre..not usre where though			
			if(ifMessageSubjectContainsOnlyOtherPepNumbers) {	//we skip this message and process the next one
				System.out.println("\tMessage Subject contains only other PEPs b, we skip. MID: "+ message_ID + " Message Subject: || " + subject);
				String query3 = "update trackMessageProcessing SET MessageSubjectContainsOnlyAnotherPEP = ? where pep = ? and messageID =?	;";
				PreparedStatement preparedStmt3 = prp.getConn().prepareStatement(query3);
				preparedStmt3.setInt   (1, 1);					preparedStmt3.setInt   (2, pepNumber);					preparedStmt3.setInt   (3, message_ID);
				// execute the java preparedstatement
				preparedStmt3.executeUpdate();
				return false;	//continue //skip current message
			}
			else {
				System.out.println("\tMessage Subject Does NOT contains only other PEPs, - we check more to decide if process ");
			}
			//2. proposalAtTheEndofMessage	
			//Note. we have to make sure that the current pep number is not mentioned in the subject - this has been checked in step 0,
			// and the message shoudl be more than 25 lines, pep proposals - which has references - for which is this is targeting to eliminate, has references at end
			//is a long message
			System.out.println("Third Check: Checking if Proposal at The End of Message");
			ifProposalAtTheEndofMessage = prp.getPms().proposalAtTheEndofMessage(message, pepNumber);	
			if(ifProposalAtTheEndofMessage) {	//we skip this message and process the next one
				System.out.println("\tProposal At The End of Message, we skip. MID: "+ message_ID);
				return false; //continue;	//skip current message
			}else {
				System.out.println("\tProposal NOT at The End of Message, - we check more to decide if process ");
			}
			//3. Checking if Message Contains PepNumbers only After Reference Section, if so we we skip message
			System.out.println("Fourth Check: If Message Contains Current PepNumber only After Reference Section, if so we we skip message");
			//some messages have multiple peps mentioned and the current pep we are looking for, only occcurs towards the very end
			// this will bring false positives whole looking for states for current proposal and in this function we will try to limit this
			//if the current pep occurs after the term references, then we wont include the message in our processing
			int PEPoption =1;	//option =1, if text contains only other peps 
			boolean allowZeroSearch = false, ifMessagesectionContainsPepNumbers = false;					//we allow zero in subject line			
			boolean referenceFound = false;
			for (String l: lowerCaseMessage.split("\\r?\\n")) {	//for each line
				if(referenceFound) {
					//check each line now..if pep is foudn in any line, it means that the current pep is just used as reference, even though it may be mentioned before the reference sections, 
					//- it would be in reference to the ppe - not about the pep
					ifMessagesectionContainsPepNumbers = prp.getPms().checkTextContainsProposalNumber_WithOptions(l, pepNumber,option,allowZero);	
					if(ifMessagesectionContainsPepNumbers) {    
						System.out.println("\tafter reference - meaning message is not about current pep - current pep is just reference - return false");
						return false;
					}
				}
				if (l.startsWith("references") ) {	
					referenceFound = true;	System.out.println("\tReferences Section Found");	
					//String[] msgSplit = lowerCaseMessage.split("references");
					//if the first section section contains the pep number, then fine
				}
	//			else if (msgSplit[last] contains current pep) {then continue}
			}
			System.out.println("\tlowerCase Message does not contain references, - we check more to decide if process ");			
			//seeing that the main idea is in the first 20-30 lines would be a part of the analysis section as because here we have no way to be sure that every state and reason will be 
			//accompanied by the pep number			
		}
		catch (SQLException se)	    {
			System.err.println("Got an sql exception! ");	System.err.println(se.getMessage() + " mid h " + message_ID);		System.out.println(StackTraceToString(se)  );
		}   catch (Exception e)	    {
			System.err.println("Got an exception! ");		System.err.println(e.getMessage() + " mid h " + message_ID);		System.out.println(StackTraceToString(e)  );	
		}
		
		//else return false allowing them to proceed processing current message
		System.out.println(" here g - last..returns true by default ");
		return true;
	}
	
	//eventually we would update the senderName column in the allmesaages table
	private static void updateTableWithProcessMessageField(Connection conn, Integer mid, boolean messageForThisPEP)
			throws SQLException {
		  updateQuery = "update allmessages set processMessage = ? where messageID = ?";
		  PreparedStatement preparedStmt = conn.prepareStatement(updateQuery);
		  preparedStmt.setBoolean (1,messageForThisPEP);		  preparedStmt.setInt(2, mid);
		  int i = preparedStmt.executeUpdate();
		  if(i>0)   {
		          //System.out.println("success");
		  }
		  else  {
		         System.out.println("stuck somewhere mid h " + mid + " messageForThisPEP " + messageForThisPEP);
		  }
		  updateQuery =null;
	}
	
	public static String StackTraceToString(Exception ex) {
		String result = ex.toString() + "\n";
		StackTraceElement[] trace = ex.getStackTrace();
		for (int i=0;i<trace.length;i++) {
			result += trace[i].toString() + "\n";
		}
		return result;
	}
}
