package readRepository.postReadingUpdates;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import GeneralQueries.GetMessageStatisticsFunctions;
import connections.MysqlConnectForQueries;
import readRepository.readRepository.MessageReadingUtils;
import connections.MysqlConnect;
import miner.process.PythonSpecificMessageProcessing;
import utilities.ReturnNattyTimeStamp;
//may and aug 2018 now not needed ...integrated in GetProposalDetailsWebPage.java and GetProposalDetails.java
public class UpdatePEPDetails_AuthorBDFLDelegate {
	//query each message
	//compute SENDERNAME - should be in format firstname.lastname or firstname.midlename.lastname - all in lowercase
	//store in same message
	
	static MessageReadingUtils mru = new MessageReadingUtils();
	static PythonSpecificMessageProcessing pms = new PythonSpecificMessageProcessing();	
	static GetMessageStatisticsFunctions f = new GetMessageStatisticsFunctions();	
	static MysqlConnectForQueries mc = new MysqlConnectForQueries();
	static String updateQuery = null;
	static int i;
	
	public static void main(String args[])
	{
		String author=null,newName = null, bdflDelegate, newBDFLDelegate;
		Integer mid = null;		int pepNumber;
		String sql0;		Statement stmt0;		ResultSet rs0;	
		
		try
		{
			Connection conn = mc.connect();				
			f.totalNumberOfMessages(conn);			f.totalNumberOfUniqueMessages(conn);
			
			sql0 = "SELECT pep, author, bdfl_delegate from pepdetails";  //where pep != -1 order by pep asc LIMIT 5000
			stmt0 = conn.createStatement();
			rs0 = stmt0.executeQuery( sql0 );		
			
			while (rs0.next()) {
				pepNumber = rs0.getInt(1);				author = rs0.getString(2);				bdflDelegate = rs0.getString(3);
				System.out.println("pep " + pepNumber + " oldAuthor " + author + " old delegate " + bdflDelegate);
				String authors[];
				if (author.contains(",")){
					String authorFinal = null;					authors = author.split(",");				
					for(String a: authors){
						a= a.trim();							
						newName = pms.getAuthorFromString(a);						authorFinal = authorFinal+ " , "+ newName;
						System.out.println("\tpep " + pepNumber + " oldAuthor " + a + " new name "+ newName);
					}
					updateTableWithSenderName(conn, pepNumber, authorFinal);
				}
				else{
					author= author.trim();	newName = pms.getAuthorFromString(author);								
					updateTableWithSenderName(conn, pepNumber, newName);						
					System.out.println("pep " + pepNumber + " oldAuthor " + author + " new name "+ newName);
				}
				//bdfl delegates - would normally be one or xero only, not multiple
				if (bdflDelegate!=null)
				{
					bdflDelegate= bdflDelegate.trim();							
					newBDFLDelegate = pms.getAuthorFromString(bdflDelegate);								
					updateTableWithBDFLDelegate(conn, pepNumber, newBDFLDelegate);						
					System.out.println("pep " + pepNumber + " oldAuthor " + author + " new name "+ newName);
				}
			}			
		}   catch (SQLException se)	    {
			System.err.println("Got an sql exception! ");			System.err.println(se.getMessage() + " mid " + mid);
		}   catch (Exception e)	    {
			System.err.println("Got an exception! ");			System.err.println(e.getMessage() + " mid " + mid);
		}
	}
	//eventually we would update the senderName column in the allmesaages table
	private static void updateTableWithSenderName(Connection conn, Integer pep, String name)
			throws SQLException {
		  updateQuery = "update pepdetails set authorCorrected = ? where pep = ?";
		  PreparedStatement preparedStmt = conn.prepareStatement(updateQuery);
		  preparedStmt.setString (1, name);		  preparedStmt.setInt(2, pep);
		  i = preparedStmt.executeUpdate();
		  if(i>0)   {
		          //System.out.println("success");
		  }
		  else  {
		         System.out.println("stuck somewhere mid " + pep + " newName " + name);
		    }
		  updateQuery =null;
	}
	//updateTableWithBDFLDelegate
	private static void updateTableWithBDFLDelegate(Connection conn, Integer pep, String bdflDelegate)
			throws SQLException {
		  updateQuery = "update pepdetails set bdfl_delegateCorrected = ? where pep = ?";
		  PreparedStatement preparedStmt = conn.prepareStatement(updateQuery);
		  preparedStmt.setString (1, bdflDelegate);
		  preparedStmt.setInt(2, pep);
		  i = preparedStmt.executeUpdate();
		  if(i>0)   {
		          //System.out.println("success");
		  }
		  else  {
		         System.out.println("stuck somewhere mid " + pep + " bdflDelegate " + bdflDelegate);
		    }
		  updateQuery =null;
	}
}