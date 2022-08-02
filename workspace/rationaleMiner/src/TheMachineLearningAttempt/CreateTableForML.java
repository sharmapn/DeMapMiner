package TheMachineLearningAttempt;

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

import peopleAspect.SeparateStudyGroup2Functions;
import peopleAspect.MysqlConnectForQueries;
import readRepository.readRepository.MessageReadingUtils;
import connections.MysqlConnect;
import miner.process.PythonSpecificMessageProcessing;
import utilities.ReturnNattyTimeStamp;
//dec 2018
//Create table suing values from results postprocessed for ML 	
public class CreateTableForML {		
	static MessageReadingUtils mru = new MessageReadingUtils();
	static PythonSpecificMessageProcessing pms = new PythonSpecificMessageProcessing();	
	static SeparateStudyGroup2Functions f = new SeparateStudyGroup2Functions();	
	static MysqlConnect mc = new MysqlConnect();
	static String updateQuery = null;
	static int i;
	
	public static void main(String args[])
	{	
		Integer days = null, pep = 0,rs0,rs1;
		String sql,sql0,sql1,column="", dropSQL= "DROP TABLE IF EXISTS AgentDetail;", sqlString="CREATE TABLE AgentDetail (";		
		Statement stmt, stmt0,stmt1;		ResultSet rs;		
		try
		{
			Connection conn = mc.connect();
			f.totalNumberOfMessages(conn);			f.totalNumberOfUniqueMessages(conn);			
			//read and store the pep and peptype in memory
			HashMap<Integer,String> hashmap=new HashMap();	        
			sql = "SELECT distinct clausie from results_postprocessed_dec2018 order by clausie";  //where pep != -1 order by pep asc LIMIT 5000
			stmt = conn.createStatement();			rs = stmt.executeQuery( sql );
			while (rs.next()){
				column = rs.getString("clausie").toLowerCase();		column = column.replaceAll("-", "_");	
				String line = " " + column + " TEXT DEFAULT NULL, "+ column +"_Days INT DEFAULT NULL, ";
				sqlString = sqlString + line;
				System.out.println("line "+ line);
			} 
			sqlString = sqlString + "ISAccepted INT, ISRejected INT);"; /* sqlString = sqlString.replace(", )", " )"); */	System.out.println("sqlString "+ sqlString);
			Statement stmt2 = conn.createStatement();	        
			stmt2.executeUpdate(dropSQL); 	stmt2.executeUpdate(sqlString);
	        System.out.println("Table Created");	        
		}   catch (SQLException se)	    {
			System.err.println("Got an sql exception! ");
			System.err.println(se.getMessage() + " pep " + pep);
		}   catch (Exception e)	    {
			System.err.println("Got an exception! ");
			System.err.println(e.getMessage() + " pep " + pep);
		}
	}
}
