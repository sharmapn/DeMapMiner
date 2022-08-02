package GeneralQueries;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import connections.MysqlConnect;
import miner.process.PythonSpecificMessageProcessing;

public class CountUpdatePepsWithMessage {
	//have to run this sql
	//alter table allmessages add COLUMN IdentifierCount INTEGER;
	//to see result
	//SELECT IdentifierCount,COUNT(*) as count FROM allmessages_dev GROUP BY IdentifierCount ORDER BY count DESC;
	
	public static void main(String args[])
	{	
		MysqlConnect mc = new MysqlConnect();		Connection connection = mc.connect();
		String tablename = 	"allmessages";		Integer sum=0,totalCounter=0;
		
		try
		{
			String query= "SELECT DISTINCT(messageid) FROM "+tablename+";";
			// create the java statement
			Statement st = connection.createStatement();			ResultSet rs = st.executeQuery(query);
			Integer messageid=0;
			  
			while (rs.next())			{	
				messageid = rs.getInt(1);
				
				String query2="SELECT COUNT(DISTINCT(pep)) FROM "+tablename+" where messageid = " + messageid + ";";
				Statement st2 = connection.createStatement();
				ResultSet rs2 = st2.executeQuery(query2);
				
				if (rs2.next())
				{	
					sum = rs2.getInt(1);	
				}
				
				UpdateMessagesWithCount(sum,messageid,connection,tablename);				
				totalCounter++;
			}
			System.out.println("Total rows updated in table  "+totalCounter );
			st.close();
		}	
			catch (Exception e)	{
			System.err.println("Got an exception! ");			System.err.println(e.getMessage());
		}		
	}	
	
	public static void UpdateMessagesWithCount(Integer sum,Integer messageid,Connection conn, String tablename) throws SQLException{
		//System.out.println("to update");
		try
		{
			Statement sMainParent=conn.createStatement();
			int update;
			String sqlReflect = "update "+tablename+" set identifiercount = "+ sum +" where messageid = " + messageid +";" ;
	 		//email similarity, modify to check as long as 'senderEmailFirstSegment' is as long as 2 characters long					
			update = sMainParent.executeUpdate(sqlReflect);
//			if(sum>1)	//only want to see which messages are assigned under multiple pepss 
//				System.out.println("Updated "+sum+" rows for messageid " + messageid);
			sMainParent.close();
		}	
			catch (Exception e)	{
			System.err.println("Got an exception! ");			System.err.println(e.getMessage());
		}
	}
}
