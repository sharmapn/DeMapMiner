package readRepository.postReadingUpdates;

import static org.junit.Assert.assertArrayEquals;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import readRepository.readRepository.MessageReadingUtils;
import connections.MysqlConnect;
import GUI.helpers.GUIHelper;
import GeneralQueries.GetMessageStatisticsFunctions;
import miner.process.PythonSpecificMessageProcessing;
import utilities.ReturnNattyTimeStamp;

//March 2021. some messges in bdfl delegate written peps were not assigned with the 'authorsrole' ...so may be more of them existed
// so we run this sscript again to assign these author role and also the inreplytousecluster
// jan 2021..we have to improve the results of this script ..seems like not all messages authors roles are not assigned properly

//dec 23 2020 This script is used to determine what was the role of the person being replied to in a message, i.e. 'inReplyToUserUsingClusteredSenderRole'
// it is derived from another script whoch was used to find the role of the message sender

//using the message author, this class finds if message author is the pep author, bdfl or delegate or other community members and updates it in allmessages table 
//can be extended to see if they are core developers 
//query each message in each pep, compute author role and store using same messageid in table
//update 2018
//to match the different authors to the ones in pep editors and core developers table, a similar approach has to be taken
// maybe we can get the cluster of the messageauthor in distintsenders and match the nametobematched (core developer, pep editor) to the names in the cluster
//
// still left to improve several areas...remove <guido in allmessages author field, M. A. Lunberg .. 
//nov 2018, this is important script to run to get teh message author role right..
// but before, the senderfullname has to be assigned..and for that the sendername clustering has to be done
//nov 2018,  make sure you run this
//update allmessages set messageType = 'BDFLReviewing'  where messageid > 6000000;
public class UpdateAllMessages_MessageAuthorsRole {
	static MessageReadingUtils mru = new MessageReadingUtils();
	static PythonSpecificMessageProcessing pms = new PythonSpecificMessageProcessing();	
	static GetMessageStatisticsFunctions f = new GetMessageStatisticsFunctions();	

	static GUIHelper guih = new GUIHelper();
	static String updateQuery = null,proposalDetailsTableName = "pepDetails";	//proposalDetails;
	static int i;
	static Connection conn = null;
	static boolean correctAuthor = false; // some author values in the field were not correct so we can also correct it here
	
	public static void main(String args[])
	{	
		String inReplyToUser="",inReplyToUserUsingClusteredSender="",inReplyToUserUsingClusteredSenderRole="",author="",fromLine="", role= "", sendername="", clusterbysenderfullname="", sql0;
		int mid = 0,pepNumber;
		Statement stmt0;		ResultSet rs0;
		try	{
			connections.MysqlConnect mc = new MysqlConnect();		conn = mc.connect();
			ArrayList<Integer> allUniqueProposals = new ArrayList<Integer>();
			allUniqueProposals = f.getAllDistinctProposals(conn);
			/* if there is a utf problem, we update teh values here
			updateQuery = "update pepdetails set authorcorrected = ?, author = ? where pep = ?";
			PreparedStatement preparedStmt = conn.prepareStatement(updateQuery);
			String author2 = "Lukasz Langa";
			preparedStmt.setString (1, author2);	 preparedStmt.setString (2, author2);			  preparedStmt.setInt(3, 443);
			i = preparedStmt.executeUpdate();			 
			if(i>0)   { 		          System.out.println("updated authorsrole ="+author2+" where messageID = "+ mid);	  }
			else  {	         System.out.println("stuck somewhere mid " + mid + " authorRole  " + "LÂ�ukasz Langa");		  }
			updateQuery =null; */
			
			//checking for all resulted in some errors, so we just checked with those peps which had many messages where authorsrole2020 unassigned
			Integer[] pepsToCheck = {302, 306,316,328,335,338,342,343,345,352,370,373,384,397,420,425,426,432,448,463,468,484,487,489,
					492,498,501,505,508,513,517,518,519,521,523,526,529,530,538,540,543,545,547,549,550,
					552,553,554,555,557,560,561,562,563,564,567,572,580,3118,3131,3150};
			
			Integer[] BDFLPEPsToCheck = {7, 8, 101, 102, 207, 228, 230, 234, 237, 238, 251, 252, 253, 254, 257, 259, 260, 280, 283,
					285,307,308,340,342,343,352,356,358,479,483,484,526,572,3000,3003,3103,3106,3116,3119,3137, 3156};
			
			Integer[] martinPEPs = {4,11,244,263,286,347,353,381,382,383,384,393,397,472,487,3120,3121,3123,3131}; //his name in the 'pepdetails' table had to be changed to match in the message header
			
			/// dec 23..we just try for all peps
			//for( int k=454; k < 455; k++)
			//for( int k=0; k < 4000; k++) { //Integer k : allUniqueProposals){	//int k=0; k< 10000;k++) { //  //nov 2018 = for(int k=0; k< 10000;k++) 
			for(int proposalNum= 276; proposalNum < 277; proposalNum++) { //main one
			//for(int proposalNum= 235; proposalNum < 236; proposalNum++) {
			//for(int proposalNum= 572; proposalNum < 573; proposalNum++) {
			//for(Integer proposalNum : martinPEPs) { //BDFLPEPsToCheck) {
				//int proposalNum = p;
				System.out.println("proposal started: "+ proposalNum);
				showRoles(proposalNum);
				System.out.println();
	//			PROCESS one pep at a time as otherwise gives error....if we wanted to give the machine less recordsets to work with at a time			//LIMIT "+x+","+y+"
				sql0 = "SELECT pep,messageID, inReplyToUser,inReplyToUserUsingClusteredSender,inReplyToUserUsingClusteredSenderRole, author, fromLine, sendername, clusterbysenderfullname from allmessages where pep = "+proposalNum + ";"; // and messageid = 163384;"; // ;"; //;\"; //
				//		+ " and pep <> originalPEPNumberNum";   
				// this line above added to sql as these records were missed suring initial assignment as the pep number was not assigned based on messafe subject and was -1 at that time
				//oct 2018, now that it is done, it is nnot needed, unless we read it whole thing in again?
						//where pep != -1 order by pep asc LIMIT 5000 ...author instead of senderfullname
				stmt0 = conn.createStatement(); 			rs0 = stmt0.executeQuery( sql0 );	
				
				while (rs0.next())   	{										
					inReplyToUser=inReplyToUserUsingClusteredSender=author=fromLine=role=inReplyToUserUsingClusteredSenderRole= ""; //make sure they are empty
					pepNumber = rs0.getInt("pep");					mid = rs0.getInt("messageID");
					inReplyToUser = rs0.getString("inReplyToUser");	
					inReplyToUserUsingClusteredSender = rs0.getString("inReplyToUserUsingClusteredSender");					
					author = rs0.getString("author"); //.contains("From:"))
					fromLine = rs0.getString("fromLine");		sendername = rs0.getString("sendername"); 
					clusterbysenderfullname = rs0.getString("clusterbysenderfullname");
					inReplyToUserUsingClusteredSenderRole = rs0.getString("inReplyToUserUsingClusteredSenderRole"); 
					
					//if(mid!=8182213)
					//{ continue; }
					
					System.out.println("New Msg: " +mid+" sendername: " + sendername + ", message author: " + author + ", clusterbysenderfullname: " + clusterbysenderfullname + ", inReplyToUser " + inReplyToUser + ", inReplyToUserUsingClusteredSender: " + inReplyToUserUsingClusteredSender + " inReplyToUserUsingClusteredSenderRole: " + inReplyToUserUsingClusteredSenderRole);
					
					// if we want author name to be corrected
					if(correctAuthor) {
						System.out.println("Old Author Msg: " +author);
						author = fromLine.replace("From: ", "");	author =  pms.getAuthorFromString(author);	
						System.out.println("New Author: " + author);
					}
					
					//March 01, 2021. Don't know why i checked 'inreplytoclusteredsenderrole' field few month back, but lets keep it now 
					//authorsrole and inreplytoclusteredsenderrole
					//String[] fieldsToSet = {"authorsrole2020","inReplyToUserUsingClusteredSenderRole"};	
					String fieldToUpdate = "authorsrole"; //authorsrole2020"; // "inReplyToUserUsingClusteredSenderRole"
					String valueFromMessage = clusterbysenderfullname; //sendername; //field to check
					//for(String field: fieldsToSet){
						System.out.println("Field To Update: " +fieldToUpdate + " valueFromMessage: " + valueFromMessage);
						if(!is_empty(valueFromMessage, fromLine, mid)) {
							// March 02 we assign roles to both the 'ClusteredSenderRole' and the 'inReplyToUserUsingClusteredSenderRole'
							//if(field.equals("authorsrole2020")) valueFromMessage = author;
							//else if(field.equals("sendername")) 
							
							//else if(f.equals("inReplyToUserUsingClusteredSenderRole")) valueFromMessage = inReplyToUserUsingClusteredSenderRole;
							System.out.println("value: " + valueFromMessage);
							role = getMessage_Role(proposalNum, valueFromMessage.toLowerCase().trim(), fromLine.toLowerCase().trim(), proposalDetailsTableName);
							if(role == null || role.isEmpty()) {}
							else
								updateTableWith_Role(mid, proposalNum, role, fieldToUpdate);
						}
					//}
				}	//end while
				stmt0.close();
				sql0=null;				stmt0=null;				rs0=null;
				System.out.println("\tFinished Proposal: "+ proposalNum);  
			} //end for
			System.out.println("Finished Processing");  
		}   catch (SQLException se)	    {
			System.err.println("Got an sql exception! ");	System.err.println(se.getMessage() + " mid " + mid);		System.out.println(StackTraceToString(se)  );
		}   catch (Exception e)	    {
			System.err.println("Got an exception! ");		System.err.println(e.getMessage() + " mid " + mid);		System.out.println(StackTraceToString(e)  );	
		}
	}

	private static boolean  is_empty(String r, String fromLine, Integer mid) {
		if (r ==null || r.isEmpty() || r.equals("")) {
			if (r ==null || r.isEmpty() || r.equals("")) {
				return true;
			}
			else {
				r = r.toLowerCase().trim();
				return false;
			}
		}
		else {
			r= r.toLowerCase().trim();
		}
		//nov 2018, sometimes the author is shortforms while 'fromline' in email message has the name 
		if(r==null || fromLine ==null) {
			 System.out.println("\tNull error : "+ mid);
			 return true;
		}
		return false;
	}

	//eventually we would update the senderName column in the allmesaages table
	private static void updateTableWith_Role( Integer mid, Integer proposal, String role, String field) throws SQLException {
		  updateQuery = "update allmessages set "+field+" = ? where pep = ? and messageID = ?";
		  PreparedStatement preparedStmt = conn.prepareStatement(updateQuery);
		  preparedStmt.setString (1, role);			  preparedStmt.setInt(2, proposal); preparedStmt.setInt(3, mid);
		  i = preparedStmt.executeUpdate();
		  if(i>0)   {  		          System.out.println("\tupdated "+field+" ="+role+" where messageID = "+ mid);				  
		  }
		  else  {	         System.out.println("stuck somewhere mid " + mid + " "+field+"  " + field);		  }
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
	public static String getMessage_Role(int proposal, String valueFromMessage, String fromLine, String proposalDetailsTableName) {
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
				if(valueFromMessage.contains("guido") || valueFromMessage.contains("gvr") || valueFromMessage.contains("gvanrossum")) {
					if (authorCorrected.contains("guido")) //in peps he is author
						return "proposalAuthor";
					else //for all other peps
						return "bdfl";				
				}
				
//				System.out.println("Check if BDFL Delegate");  //then check if member is bdfl delagate
				if (bdfl_delegate==null || bdfl_delegate.isEmpty() || bdfl_delegate =="") {}
				else {
					if (bdfl_delegate.contains(",")) {
						for (String s : bdfl_delegate.split(",")) {
							System.out.println("\tMultiple BDFL Delegates, BDFL DelegateCorrected: "+ s + ", authorEmail: ");	//HAVE TO SPLIT AUTHOR EMAIL						
							
							if(bdfl_delegate.split(";").length>1)
								bdfl_delegateEmail = bdfl_delegate.split(";")[1];
							bdfl_delegate= bdfl_delegate.split(";")[0];	//for bdfl corrected, we remove the email, remove everything after ;
							if(matchNames(valueFromMessage,fromLine,s)) return "bdfl_delegate";
							//nov 2018 added above, sometimes the suthor is just initials but the fromline in message has the name
							
							//nov 2018, sometimes the bdfl delegate and authors also, names are complicated but can be matched using email
							// pep 454, bdfl delagete = [Charles-FranÃ§ois Natali ;cf.natali@gmail.com;] but in message is [From: cf.natali at gmail.com (=?ISO-8859-1?Q?Charles=2DFran=E7ois_Natali?=)]
							if(bdfl_delegate.contains(" ")) {
//								int last = bdfl_delegate.length()-1;  System.out.println("bdfl_delegate: " + bdfl_delegate + " last: " + last);
//								System.out.println("\t last: "+ last);
//								bdflLastName = bdfl_delegate.split(" ")[last];	System.out.println("\t bdflLastName: "+ bdflLastName);
								System.out.println("bdfl_delegate: " + bdfl_delegate);
								String[] bits = bdfl_delegate.split(" "); //System.out.println("\t bits: "+ bits);
								String lastOne = bits[bits.length-1]; //System.out.println("\t lastOne: "+ lastOne);
							}
							
							if(fromLine.contains(bdfl_delegateEmail) && fromLine.contains(bdflLastName)) //bdfl delegate lastname
								return "bdfl_delegate";
							//to cater for [ at ] 
							if(fromLine.contains(" at "))	fromLine = fromLine.replace(" at ", "@");
							if(fromLine.contains(bdfl_delegateEmail) && fromLine.contains(bdflLastName)) //bdfl delegate lastname
								return "bdfl_delegate";
						}
					} else {
						System.out.println("\tSingle BDFL Delegate, BDFL DelegatesCorrected: "+ bdfl_delegate + ", BDFLDelegateEmail: " + ",r =" + valueFromMessage);
						bdfl_delegate= bdfl_delegate.split(";")[0];	//for bdfl corrected, we remove the email, remove everything after ;
						         //r=from email header, bdfl_delegate= pepdetails table
						if(matchNames(valueFromMessage,fromLine,bdfl_delegate)) return "bdfl_delegate";	
						else System.out.println("\tnot found");
					}
				}
//				System.out.println("Check if PEP Author");  //then check if member is pep author
				if (authorCorrected==null || authorCorrected.isEmpty() || authorCorrected =="") {}
				else {
					if (authorCorrected.contains(",")) {
						for (String s : authorCorrected.split(",")) {
//							System.out.println("\tMultiple Authors, authorCorrected: "+ s + ", authorEmail: "+ authorEmail);	//HAVE TO SPLIT AUTHOR EMAIL
							if(matchNames(valueFromMessage,fromLine,s)) return "proposalAuthor";						
						}
					} else {
						if(matchNames(valueFromMessage,fromLine,authorCorrected))  return "proposalAuthor";
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
				if(matchNames(valueFromMessage,fromLine,authorCorrected)) 
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
				if(matchNames(valueFromMessage,fromLine,authorCorrected)) 
					return "coredeveloper";
			}
			
		} catch (SQLException se)	    {
			System.err.println("Got an sql exception! ");	System.err.println(se.getMessage() + " proposal " + proposal);		System.out.println(StackTraceToString(se)  );
		}  catch (Exception e)	    {
			System.err.println("Got an exception! ");		System.err.println(e.getMessage() + " proposal " + proposal);		System.out.println(StackTraceToString(e)  );	
		}
		//if non of these, then return other community member
		return "otherCommunityMember";	
	} 
	
	//march 2021
	public static void showRoles(Integer proposal) {
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
				
				System.out.println("\t author: " + authorCorrected + ", bdfl_delegate: " + bdfl_delegate);// + " proposalDetailsTableName "+proposalDetailsTableName+")");
			}
		}
		catch (SQLException se)	    {
			System.err.println("Got an sql exception! ");	System.err.println(se.getMessage() + " proposal " + proposal);		System.out.println(StackTraceToString(se)  );
		}   catch (Exception e)	    {
			System.err.println("Got an exception! ");		System.err.println(e.getMessage() + " proposal " + proposal);		System.out.println(StackTraceToString(e)  );	
		}
	}
	
	//check for null before passing here
	public static boolean matchNames(String valueFromMessage, String fromLineInMessage, String authorToMatch) {
//		System.out.println("\t\tInside Fn, fromLineInMessage"+ fromLineInMessage + " valueFromMessage " + valueFromMessage + " authorToMatch: "+authorToMatch);
		if (valueFromMessage==null || valueFromMessage.isEmpty() || valueFromMessage.equals(""))
			return false;
		else {
			valueFromMessage = valueFromMessage.toLowerCase().trim();
		}
		if (authorToMatch==null || authorToMatch.isEmpty() || authorToMatch.equals(""))
			return false;
		else {
			authorToMatch = authorToMatch.toLowerCase().trim();
		}
		//declare variables
		String firstNameAuthorInMessage="",lastNameAuthorInMessage="",firstNameauthorToMatch="",lastNameauthorToMatch="";
		//assign first and last names
		if(valueFromMessage.contains(" ")) {
			firstNameAuthorInMessage = valueFromMessage.split(" ")[0];
			//for charles-franÃ§ois
			if(firstNameAuthorInMessage.contains("-")) 
				firstNameAuthorInMessage = firstNameAuthorInMessage.split("-")[0];
			String[] last = valueFromMessage.split(" ");
			lastNameAuthorInMessage = last[last.length-1];
		}else { //we assign the only term as firstname, assuming the term to be first name
			firstNameAuthorInMessage = valueFromMessage;
		}	
		if(authorToMatch.contains(" ")) {
			firstNameauthorToMatch = authorToMatch.split(" ")[0];
			//for charles-franÃ§ois
			if(firstNameauthorToMatch.contains("-")) 
				firstNameauthorToMatch = firstNameauthorToMatch.split("-")[0];
			String[] last2 = authorToMatch.split(" ");
			lastNameauthorToMatch = last2[last2.length-1];
		} else {
			firstNameauthorToMatch = authorToMatch;
			//for charles-franÃ§ois
			if(firstNameauthorToMatch.contains("-")) 
				firstNameauthorToMatch = firstNameauthorToMatch.split("-")[0];
		}
		
		if(valueFromMessage.equals(authorToMatch))
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
		//nov 2018, we compare when names are changed, -- Tarek ZiadÃ© : ziade tarek..for pep 386
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