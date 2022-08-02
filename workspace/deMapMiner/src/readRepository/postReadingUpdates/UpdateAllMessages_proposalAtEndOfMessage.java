package readRepository.postReadingUpdates;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import connections.MysqlConnectForQueries;
import readRepository.readRepository.MessageReadingUtils;
import connections.MysqlConnect;
import GUI.helpers.GUIHelper;
import GeneralQueries.GetMessageStatisticsFunctions;
import miner.process.ProcessingRequiredParameters;
import miner.process.PythonSpecificMessageProcessing;
import utilities.ReturnNattyTimeStamp;

//using the message author, this class finds if message author is the pep author, bdfl or delegate or other community members and updates it in allmessages table 
//can be extended to see if they are core developers 
//query each message in each pep, compute author role and store using same messageid in table
public class UpdateAllMessages_proposalAtEndOfMessage {
	static MessageReadingUtils mru = new MessageReadingUtils();
	static PythonSpecificMessageProcessing pms = new PythonSpecificMessageProcessing();	
	static GetMessageStatisticsFunctions f = new GetMessageStatisticsFunctions();	
	static MysqlConnectForQueries mc = new MysqlConnectForQueries();
	static GUIHelper guih = new GUIHelper();
	static String updateQuery = null;
	static int i;
	static Connection conn = null;
	
	public static void main(String args[])
	{	
		ProcessingRequiredParameters prp = new ProcessingRequiredParameters();
		String sql0, message; 
		int mid = 0,pepNumber; 
		Statement stmt0;		ResultSet rs0;
		try
		{
			conn = mc.connect();
			ArrayList<Integer> allUniqueProposals = new ArrayList<Integer>();
			allUniqueProposals = f.getAllDistinctProposals(conn);
			
			for(Integer i : allUniqueProposals){	
				System.out.println("proposal started: "+ i);					
	//			PROCESS one pep at a time as otherwise gives error....if we wanted to give the machine less recordsets to work with at a time			//LIMIT "+x+","+y+"
				sql0 = "SELECT pep, message from allmessages where pep = "+i+";";  //where pep != -1 order by pep asc LIMIT 5000
				stmt0 = conn.createStatement(); 			rs0 = stmt0.executeQuery( sql0 );	
				while (rs0.next())   	{
					pepNumber = rs0.getInt(1);					message = rs0.getString(2);		
					//proposalAtTheEndofMessage				
					boolean ifProposalAtTheEndofMessage = prp.getPms().proposalAtTheEndofMessage(message, pepNumber);	
					if(ifProposalAtTheEndofMessage) {	//we skip this message and process the next one
						System.out.println("\tProposal At The End of Message, we skip. MID: "+ mid);
						continue;	//skip current message
					}
					updateTableWithProposalAtEndOfMessage( mid, ifProposalAtTheEndofMessage);
				}	//end while
				stmt0.close();
				sql0=null;				stmt0=null;				rs0=null;			
			} //end for
		}   catch (SQLException se)	    {
			System.err.println("Got an sql exception! ");	System.err.println(se.getMessage() + " mid " + mid);		System.out.println(StackTraceToString(se)  );
		}   catch (Exception e)	    {
			System.err.println("Got an exception! ");		System.err.println(e.getMessage() + " mid " + mid);		System.out.println(StackTraceToString(e)  );	
		}
	}
	//eventually we would update the senderName column in the allmesaages table
	private static void updateTableWithProposalAtEndOfMessage ( Integer mid, boolean ifProposalAtTheEndofMessage )
			throws SQLException {
		  updateQuery = "update allmessages set ifProposalAtTheEndofMessage = ? where messageID = ?";
		  PreparedStatement preparedStmt = conn.prepareStatement(updateQuery);
		  preparedStmt.setBoolean (1, ifProposalAtTheEndofMessage);			  preparedStmt.setInt(2, mid);
		  i = preparedStmt.executeUpdate();
		  if(i>0)   { 		          //System.out.println("success");
		  }
		  else  {
		         System.out.println("stuck somewhere mid " + mid + " ifProposalAtTheEndofMessage  " + ifProposalAtTheEndofMessage);
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
	
	//using the message author, this function returns if message author is the pep author, bdfl or delegate or other community members - can be extended to see if they are core developers 
	public static String getMessageAuthorsRole(int proposal, String authorInMessage) {
		ResultSet rs;  Statement statement;
		try {
			statement = conn.createStatement();
			String authorCorrected="",authorEmail="",bdfl_delegate="";
			String sql = "select authorCorrected, authorEmail, bdfl_delegate from pepdetails where pep = "+ proposal +";"; 
			rs = statement.executeQuery(sql);	//reuse this rs variable
			int rowCount = guih.returnRowCount(sql);	
			String str = ""; 
			while (rs.next()) {
				authorCorrected= rs.getString("authorCorrected"); 	authorEmail= rs.getString("authorEmail");	bdfl_delegate  = rs.getString("bdfl_delegate");
				//System.out.println("Proposal: "+ proposal + " author :" + authorCorrected + " bdfl_delegate: " + bdfl_delegate);
				if (authorCorrected==null || authorCorrected.isEmpty() || authorCorrected =="") {}
				else {
					if (authorCorrected.contains(",")) {
						for (String s : authorCorrected.split(",")) {
							//System.out.println("\tMultiple Authors, authorCorrected: "+ s + ", authorEmail: "+ authorEmail);	//HAVE TO SPLIT AUTHOR EMAIL
							if(authorInMessage.contains(s)) return "proposalAuthor";						
						}
					} else {
						if(authorInMessage.contains(authorCorrected)) return "proposalAuthor";
					}
				}
				if (bdfl_delegate==null || bdfl_delegate.isEmpty() || bdfl_delegate =="") {}
				else {
					if (bdfl_delegate.contains(",")) {
						for (String s : bdfl_delegate.split(",")) {
							//System.out.println("\tMultiple Authors, authorCorrected: "+ s + ", authorEmail: "+ authorEmail);	//HAVE TO SPLIT AUTHOR EMAIL
							if(authorInMessage.contains(s)) return "bdfl_delegate";						
						}
					} else {
						//System.out.println("Single Author, authorCorrected: "+ authorCorrected + ", authorEmail: "+ authorEmail);
						if(authorInMessage.contains(bdfl_delegate)) return "bdfl_delegate";	
					}
				}
				if(authorInMessage.toLowerCase().contains("guido")) 
					return "bdfl";				
				else 
					return "otherCommunityMember";
			}
		} catch (SQLException se)	    {
			System.err.println("Got an sql exception! ");	System.err.println(se.getMessage() + " proposal " + proposal);		System.out.println(StackTraceToString(se)  );
		}   catch (Exception e)	    {
			System.err.println("Got an exception! ");		System.err.println(e.getMessage() + " proposal " + proposal);		System.out.println(StackTraceToString(e)  );	
		}
		return "";
	} 
}