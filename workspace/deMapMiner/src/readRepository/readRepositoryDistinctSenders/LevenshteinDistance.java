package readRepository.readRepositoryDistinctSenders;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import connections.MysqlConnectForQueries;
import connections.MysqlConnect;

public class LevenshteinDistance {
	
	static MysqlConnect mc = new MysqlConnect();// static MysqlConnectForQueries mc = new MysqlConnectForQueries();
	
	//do a fullname match for each member
	static void matchLevenshteinDistanceMatchFullNamesUpdate(){
		Integer mid = null, ld=0;
		String  emailAddress=null, firstName=null, middleName=null, lastName=null;
		boolean first=false, second =false;
		
		ArrayList<String> distinctNames = new ArrayList<String>();			
		try		{
			Connection conn = mc.connect();	
			distinctNames = returnAllDistinctNames(conn);
			Integer id=0;
			boolean toCheck = false;
			
			for (String name: distinctNames){
				//System.out.println("id "+ id+ " name "+ name);
				//assign null
				firstName=null; middleName=null; lastName=null;
				//split name here				
				//look for names in table				
			
				//String toCheckName = firstName.substring(0,1)+lastName;
				//match fullname
				String sql1 = "select distinct (sendername), levenshtein_distance('"+name+"',sendername) as t "
						+ "from distinctsenders  where sendername IS NOT NULL "
						+ "order by t asc limit 7";  //there will be many rows since there is no seprate table to store this information
				Statement stmt1;
				stmt1 = conn.createStatement();				ResultSet rs1 = stmt1.executeQuery( sql1 );
				Integer counter=0, totalMessagesCount=0;
				//System.out.println("name "+ name);
				String nm=null;
				
				System.out.format("%32s%32s%16s", "name", "name", "distance");
				System.out.println();
				while (rs1.next())
				{	
					first= second =false;
					nm =rs1.getString(1);					ld=rs1.getInt(2);					id=id+1;	
					//if(id%10==0)
					System.out.format("%32s%32s%16d", name, nm, id);					System.out.println();
					//updateDistinctSendersTableWithSenderNames(conn, id, null,name,totalMessagesCount);				
					counter++;				
				} //end while
			}
			toCheck = false;
			id++;
			ld=0;			
		}  //end try 
		catch (SQLException se)	    {
			System.err.println("Got an sql exception! ");
			System.err.println(se.getMessage() + " email " + emailAddress + " first "+first + " second "+ second);
		}   
		catch (Exception e)	    {
			System.err.println("Got an exception! ");
			System.err.println(e.getMessage() + " email " + emailAddress + " first "+first + " second "+ second);
		}
	}
	//matchLevenshteinDistanceMatchFirstNameLastNameUpdate
	static void matchLevenshteinDistanceMatchFirstNameLastNameUpdate(){
		Integer mid = null, ld=0;
		String  emailAddress=null, firstName=null, middleName=null, lastName=null;
		boolean first=false, second =false;
		
		ArrayList<String> distinctNames = new ArrayList<String>();			
		try
		{
			Connection conn = mc.connect();	
			distinctNames = returnAllDistinctNames(conn);
			Integer id=0;
			boolean toCheck = false;
			
			for (String name: distinctNames){
				//System.out.println("id "+ id+ " name "+ name);
				//assign null
				firstName=null; middleName=null; lastName=null;
				//split name here	
				//xxxx
				//look for names in table				
			
				//String toCheckName = firstName.substring(0,1)+lastName;
				//match fullname
				String sql1 = "select distinct (sendername), levenshtein_distance('"+name+"',sendername) as t "
						+ "from distinctsenders  "
						+ "where sendername IS NOT NULL "
						+ "order by t asc limit 7";  //there will be many rows since there is no seprate table to store this information
				Statement stmt1;
				stmt1 = conn.createStatement();
				ResultSet rs1 = stmt1.executeQuery( sql1 );
				Integer counter=0, totalMessagesCount=0;
				//System.out.println("name "+ name);
				String nm=null;
				
				System.out.format("%32s%32s%16s", "name", "name", "distance");				System.out.println();
				while (rs1.next())
				{	
					first= second =false;
					nm =rs1.getString(1);						ld=rs1.getInt(2);					id=id+1;	
					//if(id%10==0)
					System.out.format("%32s%32s%16d", name, nm, id);					System.out.println();
					//updateDistinctSendersTableWithSenderNames(conn, id, null,name,totalMessagesCount);				
					counter++;				
				} //end while
			}
			toCheck = false;
			id++;				ld=0;
			
		}  //end try 
		catch (SQLException se)	    {
			System.err.println("Got an sql exception! ");
			System.err.println(se.getMessage() + " email " + emailAddress + " first "+first + " second "+ second);
		}   
		catch (Exception e)	    {
			System.err.println("Got an exception! ");
			System.err.println(e.getMessage() + " email " + emailAddress + " first "+first + " second "+ second);
		}
	}
	
	public static ArrayList<String> returnAllDistinctNames(Connection connection) {
		//Integer numberUniqueMessages=0;		
		ArrayList<String> distinctNames = new ArrayList<String>();
		try {								//messageID						//Allows this program to be restarted with just play button
			String sql2 = "SELECT senderName from distinctsenders where senderName IS NOT NULL;"; //order by senderName;";  //there will be many rows since there is no seprate table to store this information
			Statement stmt2;
			stmt2 = connection.createStatement();
			ResultSet rs2 = stmt2.executeQuery( sql2 );
			while (rs2.next()){    
				distinctNames.add(rs2.getString(1));
			}
			System.out.println("DistinctNames count : " + distinctNames.size());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return distinctNames;
	}
}
