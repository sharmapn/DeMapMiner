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

import GeneralQueries.GetMessageStatisticsFunctions;
import connections.MysqlConnectForQueries;
import readRepository.readRepository.MessageReadingUtils;
import connections.MysqlConnect;
import miner.process.PythonSpecificMessageProcessing;
import utilities.ReturnNattyTimeStamp;
	
public class UpdateMessageNumberInFile {
	
	static MessageReadingUtils mru = new MessageReadingUtils();
	static PythonSpecificMessageProcessing pms = new PythonSpecificMessageProcessing();
	static GetMessageStatisticsFunctions f = new GetMessageStatisticsFunctions();
	static MysqlConnectForQueries mc = new MysqlConnectForQueries();
	static String updateQuery = null;
	static int i;
	
	public static void main(String args[])
	{	
		Integer mid = null, msgNumInFile,messageCounterinFileTracker=0;
		String folder,file,sql,sql0,sql1;
		Statement stmt, stmt0,stmt1;	PreparedStatement preparedStmt;
		ResultSet rs,rs0,rs1;
		try
		{
			Connection conn = mc.connect();	
			f.totalNumberOfMessages(conn);
			f.totalNumberOfUniqueMessages(conn);
	        
			sql = "select distinct(folder) from allmessages  where folder LIKE '%dev%';";
//					+ "where folder NOT LIKE '%ideas%' and folder NOT LIKE '%dev%' and folder NOT LIKE '%distutils-sig%' "
//					+ "and folder NOT LIKE '%committers%' and folder NOT LIKE '%announce-list%' and folder NOT LIKE '%checkins%' and folder NOT LIKE '%lists%' and folder NOT LIKE '%patches%' ;";  //will take around 30 seconds  //
			stmt = conn.createStatement();
			rs = stmt.executeQuery( sql );			
			
			while (rs.next()){	//for each folder
				folder = rs.getString(1); 
				System.out.println("Started processing for mailing List "+ folder );
				folder= folder.replace("C:\\datasets\\","");
				//for each mailing list, we go through each file
				sql0 = "select distinct(file) from allmessages where folder LIKE '%"+folder+"%' ;";  //will take around 30 seconds
				stmt0 = conn.createStatement();		stmt0.setFetchSize(1000);	//too many rows returned
				rs0 = stmt0.executeQuery( sql0 );
				while (rs0.next()){	//for each file
					file = rs0.getString(1);
					
					sql1 = "select distinct(messageID) from allmessages where folder LIKE '%"+folder+"%' and file = '"+file+"' order by messageid asc;";  //will take around 30 seconds
					stmt1 = conn.createStatement();
					rs1 = stmt1.executeQuery( sql1 );
					messageCounterinFileTracker=1;
					while (rs1.next()){	//for each message in file
					  mid = rs1.getInt(1);					  
					  updateQuery = "update allmessages set msgNumInFile = ? where messageID = ?";//start updating 'messageNumberInFile' as already ordered
					  preparedStmt = conn.prepareStatement(updateQuery);
					  preparedStmt.setInt(1, messageCounterinFileTracker);	  preparedStmt.setInt(2, mid);
					  i = preparedStmt.executeUpdate();
					  if(i>0)   {
					          //System.out.println("success");
					  }  else{
					         System.out.println("stuck somewhere mid " + mid + " file " + file);
					  }
					  updateQuery =null;  mid=null;
					  messageCounterinFileTracker++;
					}
					System.out.println("\tUpdated for file "+ file +" in maling list "+ folder);				//output message
				}
				System.out.println("Updated for maling list "+ folder );				//output message 
			}
		}   catch (SQLException se)	    {
			System.out.println(StackTraceToString(se)  );	
			System.err.println("Got an sql exception! ");
			System.err.println(se.getMessage() + " pep ");
		}   catch (Exception e)	    {
			System.out.println(StackTraceToString(e)  );	
			System.err.println("Got an exception! ");
			System.err.println(e.getMessage() + " pep ");
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
}