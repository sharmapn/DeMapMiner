package GeneralQueries;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import connections.MysqlConnect;
import miner.process.PythonSpecificMessageProcessing;

public class ExtractMessageSubjectsForStatesForDoublesTriples {
	
	public static void main(String args[])
	{
	
		MysqlConnect mc = new MysqlConnect();
		Connection connection = mc.connect();
		getallSubjects(connection);
	}
	
	public static void getallSubjects(Connection conn){
		String type = "", pepTypeString =null;
		Boolean showOutput = true;

		String[] states = {"consensus"}; //{"bdfl","propose","proposal","open","active","pending","close","final","accept","defer","replace","reject","postpone","incomplete","supersede",	"update","vote","poll","consensus","pep","draft"};
		
		try
		{
			
			String query= "";
			for(String state : states)
			{
				System.out.println("Selecting rows for state " + state);  
				query = "SELECT pep,messageid,subject FROM allmessages where subject like '%"+state +"%' order by messageid";
			
				// create the java statement
				Statement st = conn.createStatement();
				ResultSet rs = st.executeQuery(query);
				Integer counter=0;
			
				//System.out.println("PEP Type " + pepTypeString);  
				while (rs.next())
				{	
					counter++;
					Integer pep = rs.getInt(1);
					Integer mid = rs.getInt(2);
					String sub = rs.getString(3);
					
					String sqlReflect = "insert into allmessages_subjectsonly (pep,messageid,subject,state) values (?,?,?,?);" ;
					
					PreparedStatement statement = conn.prepareStatement(sqlReflect);			
					statement.setInt	(1, pep);
					statement.setInt	(2, mid);
					statement.setString (3, sub);
					statement.setString (4, state);
					
					int rowsInserted = statement.executeUpdate();
			
				}
				System.out.println("Inserted " + counter + " rows for state " + state);  
			st.close();
			}	
		}
		catch (Exception e)	{
		System.err.println("Got an exception! ");
		System.err.println(e.getMessage());
		}
	}	
	
	

}
