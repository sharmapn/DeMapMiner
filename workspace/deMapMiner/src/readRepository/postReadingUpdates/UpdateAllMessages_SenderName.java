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
//import AnalysisQueries.MysqlConnectForQueries;
import readRepository.readRepository.MessageReadingUtils;
import connections.MysqlConnect;
import miner.process.PythonSpecificMessageProcessing;
import utilities.ReturnNattyTimeStamp;
//query each message
//compute SENDERNAME - should be in format firstname.lastname or firstname.midlename.lastname - all in lowercase
//store in same message
//nov 2018, i think this is now integrated in the script to read in txt files, so maybe not needed..as sendername is already populated
//nov 2018, maybe we can use to correct for those rows where sendername is null
public class UpdateAllMessages_SenderName {
	static MessageReadingUtils mru = new MessageReadingUtils();
	static PythonSpecificMessageProcessing pms = new PythonSpecificMessageProcessing();	
	static GetMessageStatisticsFunctions f = new GetMessageStatisticsFunctions();	
	static MysqlConnect mc = new MysqlConnect(); //static MysqlConnectForQueries mc = new MysqlConnectForQueries();
	static String updateQuery = null;
	static int i;
	
	public static void main(String args[])
	{	
		ReturnNattyTimeStamp rts = new ReturnNattyTimeStamp();
		Date dateTimeStamp = null, date9=null;
		String senderName=null, email=null,newName = null,sql0;
		Integer mid = null;
		int pepNumber;
		String lines[];		
		Statement stmt0;		ResultSet rs0;
		
		try	{
			Connection conn = mc.connect();
			f.totalNumberOfMessages(conn);			f.totalNumberOfUniqueMessages(conn);
			//nov 2018, commented below as there was error,java.sql.SQLException: Invalid value for getInt() - '3k8kkg$1us@klaava.helsinki.fi' 
			//at AnalysisQueries.SeparateStudyGroup2Functions.returnAllUniqueMessages(SeparateStudyGroup2Functions.java:290)
//			ArrayList<Integer> allUniqueMessages = new ArrayList<Integer>();
//			allUniqueMessages = f.returnAllUniqueMessages(conn);
			
			Integer counter=0;
//$$		for (Integer i : allUniqueMessages) {		//div
				if(counter%100==0)
					System.out.println("counter "+ counter + " messageid "+i);				
	//			PROCESS one pep at a time as otherwise gives error....if we wanted to give the machine less recordsets to work with at a time			//LIMIT "+x+","+y+"
				//nov 2018, added to check only for rows where sendername is null
				sql0 = "SELECT pep, messageID,SUBSTRING_INDEX(email,'Date:',1), senderName from allmessages where sendername IS NULL"; //messageID = "+i+";";  //where pep != -1 order by pep asc LIMIT 5000
				stmt0 = conn.createStatement();				rs0 = stmt0.executeQuery( sql0 );			
				//  Date: Thu Apr  1 00:28:01 2004	
				
				while (rs0.next()){
					pepNumber = rs0.getInt(1);					mid = rs0.getInt(2);					email = rs0.getString(3); //.contains("From:"))
					if(counter%1000==0)
						System.out.println("counter "+ counter + " messageid "+i);	
					lines = email.split("\\n");
					for (String line : lines)	{					
						if (line.isEmpty() || line==null)	{}
						else
						{
							if (line.contains("From:")){							
								senderName = line.replace("From:", "");			senderName= senderName.trim();
								newName = senderName;			newName = pms.getAuthorFromString(newName);
									//"Original Name:   "+ senderName + " 			System.out.println(" new name " + newName);								
								updateTableWithSenderName(conn, mid, newName);						
								break;   //break on first find
							}
						}						
					}
					lines=null; email=null;
					counter++; //end if	
				}	//end while
				stmt0.close();				sql0=null;				stmt0=null;				rs0=null;			
//$$		} //end for
			
		}   catch (SQLException se)	    {
			System.err.println("Got an sql exception! ");			System.err.println(se.getMessage() + " mid " + mid);
		}   catch (Exception e)	    {
			System.err.println("Got an exception! ");			System.err.println(e.getMessage() + " mid " + mid);
		}
	}
	//eventually we would update the senderName column in the allmesaages table
	private static void updateTableWithSenderName(Connection conn, Integer mid, String name)
			throws SQLException {
		  updateQuery = "update allmessages set senderName = ? where messageID = ?";
		  PreparedStatement preparedStmt = conn.prepareStatement(updateQuery);
		  preparedStmt.setString (1, name);		  preparedStmt.setInt(2, mid);
		  i = preparedStmt.executeUpdate();
		  if(i>0)   {
		          System.out.println("success");
		  }
		  else  {
		         System.out.println("stuck somewhere mid " + mid + " newName " + name);
		  }
		  updateQuery =null;
	}
}