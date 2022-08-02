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

public class UpdateAllMessages_CountRepliesToThisMessage {	
	static MessageReadingUtils mru = new MessageReadingUtils();
	static PythonSpecificMessageProcessing pms = new PythonSpecificMessageProcessing();	
	static GetMessageStatisticsFunctions f = new GetMessageStatisticsFunctions();	
	static MysqlConnectForQueries mc = new MysqlConnectForQueries();
	static PreparedStatement preparedStmt =null;
	static String updateQuery = null;
	
	public static void main(String args[])
	{
		Integer count = 0, mid=null;
		String sql0;
		Statement stmt0;
		ResultSet rs0;	
		
		try
		{
			Connection conn = mc.connect();	
			
			f.totalNumberOfMessages(conn);
			f.totalNumberOfUniqueMessages(conn);
			
			ArrayList<String> allUniqueMessageIDs = new ArrayList<String>();
			allUniqueMessageIDs = f.returnAllUniqueEmailMessagesID(conn);
			System.out.println("Count allUniqueMessageIDs "+ allUniqueMessageIDs.size());
			Integer counter=0;
			
			
			long t0, t1;
			t0 =t1= System.currentTimeMillis();
			
			for (String i : allUniqueMessageIDs)		//div
			{	
				if(counter%1000==0){
					t0=t1;
					t1 = System.currentTimeMillis();
					System.out.println("counter "+ counter + " emailmessageid "+i + " Elapsed time = " + (t1 - t0) / 1000 + " seconds");
				}
	//			PROCESS one pep at a time as otherwise gives error....if we wanted to give the machine less recordsets to work with at a time			//LIMIT "+x+","+y+"
				sql0 = "SELECT COUNT(emailmessageid) from allmessages where inReplyto = '"+i+"';";  //where pep != -1 order by pep asc LIMIT 5000
				stmt0 = conn.createStatement();
				rs0 = stmt0.executeQuery( sql0 );				
				if (rs0.next())  {
					count = rs0.getInt(1);	
					//updateTableWithInReplyToCount(conn, i, count);
					updateQuery = "update allmessages set countRepliesToThisMessage = ? where emailmessageID = ?";
					preparedStmt = conn.prepareStatement(updateQuery);
					preparedStmt.setInt (1, count);
					preparedStmt.setString(2, i);
					Integer j = preparedStmt.executeUpdate();
					if(j>0)   {
				          //System.out.println("success");
					}
					else  {
				         System.out.println("stuck somewhere emailmid " + i);
				    }
					updateQuery =null;
				}
					
				stmt0.close();
				sql0=null;
				stmt0=null;
				rs0=null;
				counter++;
			} //end for
			System.out.println("Processing Finished sucessfully counter "+ counter);
		}   catch (SQLException se)	    {
			System.err.println("Got an sql exception! ");
			System.err.println(se.getMessage() + " mid " + mid);
		}   catch (Exception e)	    {
			System.err.println("Got an exception! ");
			System.err.println(e.getMessage() + " mid " + mid);
		}
	}	
}
