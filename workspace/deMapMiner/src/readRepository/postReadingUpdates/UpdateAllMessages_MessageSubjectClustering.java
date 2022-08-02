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
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import readRepository.readRepository.MessageReadingUtils;
import connections.MysqlConnect;
import GUI.helpers.GUIHelper;
import GeneralQueries.GetMessageStatisticsFunctions;
import miner.process.PythonSpecificMessageProcessing;
import utilities.ReturnNattyTimeStamp;

//dec 2018 using the message subject for each pep, we cluseter the messages 
//update allmessages set messageType = 'BDFLReviewing'  where messageid > 6000000;
public class UpdateAllMessages_MessageSubjectClustering {
	static MessageReadingUtils mru = new MessageReadingUtils();
	static PythonSpecificMessageProcessing pms = new PythonSpecificMessageProcessing();	
	static GetMessageStatisticsFunctions f = new GetMessageStatisticsFunctions();	

	static GUIHelper guih = new GUIHelper();
	static String updateQuery = null,proposalDetailsTableName = "pepDetails";	//proposalDetails;
	static int i;
	static Connection conn = null;
	
	public static void main(String args[])
	{	
		String subject="",author="",fromLine="", role= "",sql0;
		int mid = 0,pepNumber;
		Statement stmt0;		ResultSet rs0;
		try	{
			connections.MysqlConnect mc = new MysqlConnect();		conn = mc.connect();
			ArrayList<Integer> allUniqueProposals = new ArrayList<Integer>();
			allUniqueProposals = f.getAllDistinctProposals(conn);
			HashMap<Integer, String> messageSubjectsForProposal = new HashMap<Integer, String>();
			
			for( int k=279; k < 280; k++) { //Integer k : allUniqueProposals){	//int k=0; k< 10000;k++) { //  //nov 2018 = for(int k=0; k< 10000;k++) 
				System.out.println("proposal started: "+ i);					
	//			PROCESS one pep at a time as otherwise gives error....if we wanted to give the machine less recordsets to work with at a time			//LIMIT "+x+","+y+"
				sql0 = "SELECT DISTINCT messageID, subject from allmessages where pep = "+k + " ORDER BY subject asc;"; // and messageid = 163384;"; // ;"; //;\"; //
				//		+ " and pep <> originalPEPNumberNum";   
				// this line above added to sql as these records were missed suring initial assignment as the pep number was not assigned based on messafe subject and was -1 at that time
				//oct 2018, now that it is done, it is nnot needed, unless we read it whole thing in again?
						//where pep != -1 order by pep asc LIMIT 5000 ...author instead of senderfullname
				stmt0 = conn.createStatement(); 			rs0 = stmt0.executeQuery( sql0 );	
				while (rs0.next())   	{	//add to arraylist
					subject=author=fromLine=role= ""; //make sure they are empty
					mid = rs0.getInt("messageID");	subject = rs0.getString("subject"); 		
					
					if (subject ==null || subject.isEmpty() || subject.equals("")) {
						subject=""; System.out.println(" subject: "+subject );
					}
					else { 
						//remove these
						subject = subject.toLowerCase();
						subject = subject.replaceAll("\\[.*?\\]","").trim();
						String neg[] = {"re:","fwd","fw:",": re:"};
						for (String m: neg) {
							if(subject.toLowerCase().trim().startsWith(m)) {
								subject=subject.replaceAll(m, "");
							}
						}
						//if(subject.toLowerCase().trim().contains("re:")) { System.out.println(" subject: "+subject );
						//	subject=subject.replaceAll("re: ", "");
						//}
						subject =subject.toLowerCase().trim();				
						messageSubjectsForProposal.put(mid, subject);
					}
					System.out.println("Message ID: "+mid+", subject: "+subject + " message author: " + author); 
					//dec 2018
					//we only want the original message no replies or forwarded messages, especially for the message subjects where decision terms exist
				}
				/*
				Set set = messageSubjectsForProposal.entrySet();
			    Iterator iterator = set.iterator();
			    while(iterator.hasNext()) {
			    	Map.Entry mentry = (Map.Entry)iterator.next();
			    	System.out.print("MID is: "+ mentry.getKey() + " & Subject is: " + mentry.getValue());
			    
			    	
					//dec 2018, if the mesage shares teh same subject with the acceptance/rejection message, then we are more sure this is reason message/sentence
					String stateSubstateResultsTableName = "results";
					ResultSet rs = null; Statement stmt = null;  		
					try {	stmt = conn.createStatement();			
						if(messageSubject.isEmpty() ||  messageSubject.equals("")) {}
						//System.out.println("here b "); 
						else {
							String cleanedMsgSubject = messageSubject.toLowerCase().replace("re:", "").replace("fw:", ""); //get rid of fwd and reply
							cleanedMsgSubject = remove_parenthesis(cleanedMsgSubject, "[]");
							//System.out.println("here search Same Message Subjects: "+ cleanedMsgSubject);
							//System.out.println("here c ");
							String query = "SELECT * FROM "+stateSubstateResultsTableName+" WHERE pep = "+proposal+" and label = '"+label+"' and messagesubject like '%"+ cleanedMsgSubject  +"%' order by dateTimeStamp;";
							//check to see if we want to see all retrieved messages where status has changed 
							//rs = statement.executeQuery("SELECT * FROM "+messagesTableName+" WHERE "+proposalIdentifier+" + Integer.parseInt(proposalNumberText.getText()) + " AND email LIKE '%" + searchText.getText()  + "%' AND statusChanged =1 order by dateTimeStamp, messageid asc, Proposal asc;");
							rs = stmt.executeQuery(query);						int rowCount = guih.returnRowCount(query);
							//System.out.println("here d");
//							System.out.println("(" + sentenceorParagrapghString +") similar message subjects ("+cleanedMsgSubject+") - resultset rowCount = " + rowCount);  
							if (rs.next()) {  //we can get several results for same label, here we just check even if any subject from any result row is matched, as post-processing should be very good to let go of important results 
								p.setSameMsgSubAsStateTriple(true);		p.setSameMsgSubAsStateTripleProbabilityScore(0.9); }  //set same message subject boolean value to true
							else {
								p.setSameMsgSubAsStateTriple(false);	p.setSameMsgSubAsStateTripleProbabilityScore(0.0); 
							} 
						}			
					} catch (SQLException insertException) {					//displaySQLErrors(insertException);	
						System.out.println("Exception 300 (FILE) " + insertException.toString()); 
						System.out.println(StackTraceToString(insertException)  );
					}
					//end december
					
					//nov 2018, sometimes the author is shortforms while 'fromline' in email message has the name 
					if(senderfullname==null || fromLine ==null) { System.out.println("\tNull error : "+ mid);   }
					else {
						role = getMessageAuthorsRole(k, senderfullname.toLowerCase().trim(), fromLine.toLowerCase().trim(), proposalDetailsTableName); 
						updateTableWithAuthorRole(mid, k, role);
					}
					*/
					//end while
				stmt0.close();
				sql0=null;				stmt0=null;				rs0=null;
				System.out.println("\tFinished Proposal: "+ k);  
			} //end for
			System.out.println("Finished Processing");  
		}   catch (SQLException se)	    {
			System.err.println("Got an sql exception! ");	System.err.println(se.getMessage() + " mid " + mid);		System.out.println(StackTraceToString(se)  );
		}   catch (Exception e)	    {
			System.err.println("Got an exception! ");		System.err.println(e.getMessage() + " mid " + mid);		System.out.println(StackTraceToString(e)  );	
		}
	}
	//eventually we would update the senderName column in the allmesaages table
	private static void updateTableWithAuthorRole( Integer mid, Integer proposal, String authorRole) throws SQLException {
		  updateQuery = "update allmessages set authorsrole = ? where pep = ? and messageID = ?";
		  PreparedStatement preparedStmt = conn.prepareStatement(updateQuery);
		  preparedStmt.setString (1, authorRole);			  preparedStmt.setInt(2, proposal); preparedStmt.setInt(3, mid);
		  i = preparedStmt.executeUpdate();
		  if(i>0)   {  		          System.out.println("\tupdated authorsrole ="+authorRole+" where messageID = "+ mid);				  
		  }
		  else  {	         System.out.println("stuck somewhere mid " + mid + " authorRole  " + authorRole);		  }
		  updateQuery =null;
	}
	
	public static String StackTraceToString(Exception ex) {
		String result = ex.toString() + "\n";
		StackTraceElement[] trace = ex.getStackTrace();
		for (int i=0;i<trace.length;i++) 
			result += trace[i].toString() + "\n";		
		return result;
	}
	// order of assignment of roles to a member, 
	// bdfl,bdfl_delegte pep editor, core developers, other
	//using the message author, this function returns if message author is the pep author, bdfl or delegate or other community members - can be extended to see if they are core developers 
	//checking is done by using equals, tehrefore it direct match
	public static String getMessageAuthorsRole(int proposal, String authorInMessage, String fromLine, String proposalDetailsTableName) {
		ResultSet rs,rs2,rs3;  Statement statement;
		try {
			statement = conn.createStatement();
			String authorCorrected="",authorEmail="",bdfl_delegate="",bdfl_delegateEmail="", bdflLastName="";
			String sql = "select pep, authorCorrected, authorEmail, bdfl_delegatecorrected from "+proposalDetailsTableName+ " where pep = "+ proposal +";"; 
			rs = statement.executeQuery(sql);	//reuse this rs variable
			int rowCount = guih.returnRowCount(sql);	
			String str = ""; 
			while (rs.next()) {
				authorCorrected=authorEmail=bdfl_delegate=bdfl_delegateEmail=bdflLastName="";
				authorCorrected= rs.getString("authorCorrected").toLowerCase().trim(); 	authorEmail= rs.getString("authorEmail").toLowerCase().trim();	
				bdfl_delegate  = rs.getString("bdfl_delegatecorrected").toLowerCase().trim();
				
				System.out.println("\t(Proposal: "+ rs.getInt("pep") + ", author :" + authorCorrected + ", bdfl_delegate: " + bdfl_delegate + " proposalDetailsTableName "+proposalDetailsTableName+")");
				
				//first check if member is bdfl 
				if(authorInMessage.contains("guido") || authorInMessage.contains("gvr") || authorInMessage.contains("gvanrossum")) 
					return "bdfl";				
				
//				System.out.println("Check if BDFL Delegate");  //then check if member is bdfl delagate
				if (bdfl_delegate==null || bdfl_delegate.isEmpty() || bdfl_delegate =="") {}
				else {
					if (bdfl_delegate.contains(",")) {
						for (String s : bdfl_delegate.split(",")) {
//							System.out.println("\tMultiple BDFL Delegates, BDFL DelegateCorrected: "+ s + ", authorEmail: ");	//HAVE TO SPLIT AUTHOR EMAIL						
							
							if(bdfl_delegate.split(";").length>1)
								bdfl_delegateEmail = bdfl_delegate.split(";")[1];
							bdfl_delegate= bdfl_delegate.split(";")[0];	//for bdfl corrected, we remove the email, remove everything after ;
							if(matchNames(authorInMessage,fromLine,s)) return "bdfl_delegate";
							//nov 2018 added above, sometimes the suthor is just initials but the fromline in message has the name
							
							//nov 2018, sometimes the bdfl delegate and authors also, names are complicated but can be matched using email
							// pep 454, bdfl delagete = [Charles-François Natali ;cf.natali@gmail.com;] but in message is [From: cf.natali at gmail.com (=?ISO-8859-1?Q?Charles=2DFran=E7ois_Natali?=)]
							if(bdfl_delegate.contains(" ")) {
								int last = bdfl_delegate.length()-1;
								bdflLastName = bdfl_delegate.split(" ")[last];
							}
							
							if(fromLine.contains(bdfl_delegateEmail) && fromLine.contains(bdflLastName)) //bdfl delegate lastname
								return "bdfl_delegate";
							//to cater for [ at ] 
							if(fromLine.contains(" at "))	fromLine = fromLine.replace(" at ", "@");
							if(fromLine.contains(bdfl_delegateEmail) && fromLine.contains(bdflLastName)) //bdfl delegate lastname
								return "bdfl_delegate";
						}
					} else {
//						System.out.println("\tSingle BDFL Delegate, BDFL DelegatesCorrected: "+ bdfl_delegate + ", BDFLDelegateEmail: ");
						bdfl_delegate= bdfl_delegate.split(";")[0];	//for bdfl corrected, we remove the email, remove everything after ;
						if(matchNames(authorInMessage,fromLine,bdfl_delegate)) return "bdfl_delegate";	
//						else System.out.println("\tnot found");
					}
				}
//				System.out.println("Check if PEP Author");  //then check if member is pep author
				if (authorCorrected==null || authorCorrected.isEmpty() || authorCorrected =="") {}
				else {
					if (authorCorrected.contains(",")) {
						for (String s : authorCorrected.split(",")) {
//							System.out.println("\tMultiple Authors, authorCorrected: "+ s + ", authorEmail: "+ authorEmail);	//HAVE TO SPLIT AUTHOR EMAIL
							if(matchNames(authorInMessage,fromLine,s)) return "proposalAuthor";						
						}
					} else {
						if(matchNames(authorInMessage,fromLine,authorCorrected))  return "proposalAuthor";
					}
				}			
			}
//			System.out.println("Check if PEP Editors"); 
			//if not found, check if member is pep editor		
			String sql2 = "select name_corrected from pythonmembers_pepeditors;"; 
			rs2 = statement.executeQuery(sql2);	//reuse this rs variable
			int rowCount2 = guih.returnRowCount(sql2);	
			String str2 = ""; 
			while (rs2.next()) {
				authorCorrected= rs2.getString("name_corrected").toLowerCase().trim(); 	
//				System.out.println("\tProposal: "+ proposal + " author :" + authorCorrected + " bdfl_delegate: " + bdfl_delegate);				
				if(matchNames(authorInMessage,fromLine,authorCorrected)) 
					return "pepeditors";
			}
			//check if member is core developer			
			String sql3 = "select name_corrected from pythonmembers_coredevelopers;"; 
			rs3 = statement.executeQuery(sql3);	//reuse this rs variable
			int rowCount3 = guih.returnRowCount(sql3);	
			String str3 = ""; 
			while (rs3.next()) {
				authorCorrected= rs3.getString("name_corrected").toLowerCase().trim(); 	
				//System.out.println("Proposal: "+ proposal + " author :" + authorCorrected + " bdfl_delegate: " + bdfl_delegate);				
				if(matchNames(authorInMessage,fromLine,authorCorrected)) 
					return "coredeveloper";
			}
			
		} catch (SQLException se)	    {
			System.err.println("Got an sql exception! ");	System.err.println(se.getMessage() + " proposal " + proposal);		System.out.println(StackTraceToString(se)  );
		}   catch (Exception e)	    {
			System.err.println("Got an exception! ");		System.err.println(e.getMessage() + " proposal " + proposal);		System.out.println(StackTraceToString(e)  );	
		}
		//if non of these, then return other community member
		return "otherCommunityMember";	
	} 
	
	//check for null before passing here
	public static boolean matchNames(String authorInMessage, String fromLineInMessage, String authorToMatch) {
		
		if (authorInMessage==null || authorInMessage.isEmpty() || authorInMessage.equals(""))
			return false;
		else {
			authorInMessage = authorInMessage.toLowerCase().trim();
		}
		if (authorToMatch==null || authorToMatch.isEmpty() || authorToMatch.equals(""))
			return false;
		else {
			authorToMatch = authorToMatch.toLowerCase().trim();
		}
		//declare variables
		String firstNameAuthorInMessage="",lastNameAuthorInMessage="",firstNameauthorToMatch="",lastNameauthorToMatch="";
		//assign first and last names
		if(authorInMessage.contains(" ")) {
			firstNameAuthorInMessage = authorInMessage.split(" ")[0];
			//for charles-françois
			if(firstNameAuthorInMessage.contains("-")) 
				firstNameAuthorInMessage = firstNameAuthorInMessage.split("-")[0];
			String[] last = authorInMessage.split(" ");
			lastNameAuthorInMessage = last[last.length-1];
		}else { //we assign the only term as firstname, assuming the term to be first name
			firstNameAuthorInMessage = authorInMessage;
		}	
		if(authorToMatch.contains(" ")) {
			firstNameauthorToMatch = authorToMatch.split(" ")[0];
			//for charles-françois
			if(firstNameauthorToMatch.contains("-")) 
				firstNameauthorToMatch = firstNameauthorToMatch.split("-")[0];
			String[] last2 = authorToMatch.split(" ");
			lastNameauthorToMatch = last2[last2.length-1];
		} else {
			firstNameauthorToMatch = authorToMatch;
			//for charles-françois
			if(firstNameauthorToMatch.contains("-")) 
				firstNameauthorToMatch = firstNameauthorToMatch.split("-")[0];
		}
		
		if(authorInMessage.equals(authorToMatch))
			return true;
		if (firstNameAuthorInMessage.equals(firstNameauthorToMatch) && lastNameAuthorInMessage.equals(lastNameauthorToMatch)) //we just compare first and last names..not middle names
			return true;		
		//oct 2018...if message author just has one term as name, and in pep detyails tehre are two terms, first and last name, then we only match the firstname
		if (lastNameAuthorInMessage.equals("") || lastNameauthorToMatch.equals("")) {
			if (firstNameAuthorInMessage.equals(firstNameauthorToMatch)) 
				return true;
		}
		//oct 2018..if firstname only contaisn a letter, we only check the first letter but the full lastname
		if(firstNameauthorToMatch.length()==1 || firstNameAuthorInMessage.length()==1) {
			//just to allow char comparison, i get char vales of both
			if((firstNameAuthorInMessage.charAt(0) == firstNameauthorToMatch.charAt(0))  && lastNameAuthorInMessage.equals(lastNameauthorToMatch))
				return true;
		}
		//nov 2018, we compare when names are changed, -- Tarek Ziadé : ziade tarek..for pep 386
		if (firstNameAuthorInMessage.equals(lastNameauthorToMatch) && lastNameAuthorInMessage.equals(firstNameauthorToMatch)) //we just compare first and last names..not middle names
			return true;
		//nov 2018, we compare when names are shortened, pep 485, chris barker in message to Christopher Barker <Chris.Barker at noaa.gov>
		//one at a time, so we only code those needed, second one ios neee, not sure if firsone is needed at this point - could return false positives
		//if (firstNameAuthorInMessage.contains(firstNameauthorToMatch) && lastNameAuthorInMessage.contains(lastNameauthorToMatch)) //we just check first and last names..not middle names
		//	return true;
		if (firstNameauthorToMatch.contains(firstNameAuthorInMessage) && lastNameauthorToMatch.contains(lastNameAuthorInMessage)) //we just check first and last names..not middle names
			return true;
		
//		System.out.println("\t\tInside Fn, fromLineInMessage"+ fromLineInMessage + " firstNameauthorToMatch " + firstNameauthorToMatch + " lastNameauthorToMatch: "+lastNameauthorToMatch);
		//nov 2018 added, sometimes authorname just contains the initials and the 
		if(fromLineInMessage.contains(authorToMatch))
			return true;
		//main one below
		if (fromLineInMessage.contains(firstNameauthorToMatch) && fromLineInMessage.contains(lastNameauthorToMatch)) //we just compare first and last names..not middle names
			return true;
		if (lastNameauthorToMatch.equals("")) {
			if (fromLineInMessage.equals(firstNameauthorToMatch)) 
				return true;
		}	
		
		return false;		
	}
}