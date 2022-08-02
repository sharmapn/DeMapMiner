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
		//uodate peptype column after reading - thos can eb done while reading.... :)
public class UpdateAfterReading_PEPType {
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
		Integer mid = null;
		int pep = 0;
		String newName = null;
		char pepType;
		String sql,sql0,sql1;
		Statement stmt, stmt0,stmt1;
		ResultSet rs;
		int rs0,rs1;	
		
		try
		{
			Connection conn = mc.connect();	
			
			f.totalNumberOfMessages(conn);
			f.totalNumberOfUniqueMessages(conn);
			
			//read and store the pep and peptype in memory
			HashMap<Integer,String> hashmap=new HashMap();
	        
			sql = "SELECT pep, type from pepDetails order by pep;";  //where pep != -1 order by pep asc LIMIT 5000
			stmt = conn.createStatement();
			rs = stmt.executeQuery( sql );			
			
			while (rs.next()){
				pep = rs.getInt(1);
				pepType = rs.getString(2).trim().charAt(0);	 //just include S, I and P  ..trim just to make sure
				
				//update
				sql0 = "UPDATE allmessages SET pepType = '"+pepType+"' where pep = "+pep+";";  //where pep != -1 order by pep asc LIMIT 5000
				stmt0 = conn.createStatement();
				rs0 = stmt0.executeUpdate(sql0);
				sql1 = "UPDATE allpepNumbers SET pepType = '"+pepType+"' where pep = "+pep+";";  //where pep != -1 order by pep asc LIMIT 5000
				stmt1 = conn.createStatement();
				rs1 = stmt1.executeUpdate(sql1);
				//output message 
				System.out.println("Updated for pep "+ pep + " rows " + rs0+rs1);
			}
			
		}   catch (SQLException se)	    {
			System.err.println("Got an sql exception! ");
			System.err.println(se.getMessage() + " pep " + pep);
		}   catch (Exception e)	    {
			System.err.println("Got an exception! ");
			System.err.println(e.getMessage() + " pep " + pep);
		}
	}
}
