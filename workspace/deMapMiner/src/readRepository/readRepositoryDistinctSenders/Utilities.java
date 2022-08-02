package readRepository.readRepositoryDistinctSenders;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;

import connections.MysqlConnectForQueries;
import connections.MysqlConnect;

public class Utilities {
	
	static MysqlConnect mc = new MysqlConnect(); //static MysqlConnectForQueries mc = new MysqlConnectForQueries();
	
	static void splitFullNameIntoFirstAndLastName(){
		Integer mid = null;
		String  emailAddress=null, firstName=null, middleName=null, lastName=null;
		boolean first=false, second =false;
		
		ArrayList<String> distinctNames = new ArrayList<String>();			
		try		{
			Connection conn = mc.connect();
			distinctNames = returnAllDistinctNames(conn);
			Integer id=0;
			//this feature allows it to be restarted
			//id = getMaxIDOrNULL(conn, id);
				//assign distinct id for records
			
			boolean toUpdate = false;
			
			for (String name: distinctNames){
				//System.out.println("id "+ id+ " name "+ name);
				//assign null
				firstName=null; middleName=null; lastName=null;
				//split name here
				if (name.contains("@")){
					//split the name part from email and set that as name
				}
				if (name.contains(" ") && !name.contains("@")) {
					String names[] = name.split(" ");
					if (names.length==3){
						toUpdate = true;
						firstName = names[0];						middleName = names[1];						lastName = names[2];
					}
					else if (names.length==2){
						toUpdate = true;
						firstName = names[0];						lastName = names[1];
					}
					else if (names.length==1){
						
					}
					else if (names.length>2){
						firstName = names[0];
						lastName = names[names.length-1];
					}
				}
				//look for names in table 
				
				if(toUpdate) {	
					String toCheckName = firstName.substring(0,1)+lastName;
					String sql1 = "update distinctsenders set firstName = '"+firstName+"', lastName = '"+lastName+"' where sendername = '"+name+"' ";  //there will be many rows since there is no seprate table to store this information
					Statement stmt1;
					stmt1 = conn.createStatement();
					int rs1 = stmt1.executeUpdate(sql1);
					
					if (rs1>0)					{						
						System.out.println("update success ");									
					} //end while
				}
				toUpdate = false;
				id++;
			}
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
	
	public static ArrayList<DistinctSendersDetails> returnAllDistinctSendersDetailsData(Connection connection, String tableName) {
		//Integer numberUniqueMessages=0;		
		//ArrayList<String> numberFromLine = new ArrayList<String>();
		ArrayList<DistinctSendersDetails> DistinctSendersDetailsData = new ArrayList<DistinctSendersDetails>();
		DistinctSendersDetails record;
		try {								//messageID						//Allows this program to be restarted with just play button
			String sql2 = "SELECT id,emailaddress,sendername,senderfirstname,senderlastname,senderEmailFirstSegment from "+tableName+";";// where totalMessageCount IS NULL;";  //there will be many rows since there is no seprate table to store this information
			Statement stmt2;
			stmt2 = connection.createStatement();
			ResultSet rs2 = stmt2.executeQuery( sql2 );
			while (rs2.next()){   
				record = new DistinctSendersDetails();
				Integer id  = rs2.getInt("id");				String emailaddress  = rs2.getString("emailaddress");				String sendername  = rs2.getString("sendername");
				String senderfirstname  = rs2.getString("senderfirstname");				String senderlastname  = rs2.getString("senderlastname");
				String senderEmailFirstSegment  = rs2.getString("senderEmailFirstSegment");
				//numberFromLine.add(rs2.getString(1));
				record.setId(id);				record.setEmailAddress(emailaddress);				record.setFullName(sendername);
				record.setFirstName(senderfirstname);				record.setLastName(senderlastname);				record.setLastName(senderEmailFirstSegment);
				DistinctSendersDetailsData.add(record);
			}
			//System.out.println("DistinctEmails count : " + numberFromLine.size());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return DistinctSendersDetailsData;
	}
	
	public static ArrayList<String> returnAllDistinctEmailAddresses(Connection connection, String tableName) {
		//Integer numberUniqueMessages=0;		
		ArrayList<String> numberFromLine = new ArrayList<String>();
		try {								//messageID						//Allows this program to be restarted with just play button
			String sql2 = "SELECT emailaddress from "+tableName+" where totalMessageCount IS NULL;";  //there will be many rows since there is no seprate table to store this information
			Statement stmt2;
			stmt2 = connection.createStatement();
			ResultSet rs2 = stmt2.executeQuery( sql2 );
			while (rs2.next()){    
				numberFromLine.add(rs2.getString(1));
			}
			System.out.println("DistinctEmails count : " + numberFromLine.size());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return numberFromLine;
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
	
	static String removeParenthesis(String email) {
		if (email==null)			return null;
		if(email.startsWith("("))		email = email.replace("(", "");
		if(email.endsWith(")"))			email = email.replace(")", "");
		if(email.startsWith("\'"))			email = email.replaceAll("\'", "");
		if(email.endsWith("\'"))			email = email.replaceAll("\'", "");
		return email;
	}
	
	public static String decodeParameterSpciallyJapanese(String s) throws ParseException {
		try {
			boolean unicode = false;
			for (int i = 0; i < s.length(); i++) {
				if (s.charAt(i) > 0xff) { // Unicode
					unicode = true;
					break;
				}
			}

			if (!unicode) {
				// decode by character encoding.
				s = new String(s.getBytes("ISO-8859-1"), "JISAutoDetect");
			}

			// decode by RFC2047.
			// if variable s isn't encoded-word, it's ignored.
		//	return MimeUtility.decodeText(s);
		} 
		catch (UnsupportedEncodingException e) {
		}
		//throw new ParseException("Unsupported Encoding");
		return s;
	}
	
	//eventually we would update the senderName column in the allmesaages table
	public static ArrayList<String> returnAllFromLine(Connection connection) {
		//Integer numberUniqueMessages=0;		
		ArrayList<String> numberFromLine = new ArrayList<String>();
		try {								//messageID						//where messageid > 1494862;
			String sql2 = "SELECT fromLine from allmessages LIMIT 5000";  //there will be many rows since there is no seprate table to store this information
			Statement stmt2;
			stmt2 = connection.createStatement();
			ResultSet rs2 = stmt2.executeQuery( sql2 );
			while (rs2.next()){    
				numberFromLine.add(rs2.getString(1));
			}
			System.out.println("fromLine count : " + numberFromLine.size());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return numberFromLine;
	}
	
	static void updateDistinctSendersTableWithSenderNames(Connection conn, Integer id, String email, String name,String firstName, String lastName, String senderEmailFirstSegment, Integer totalMessageCount,Integer totalSenderCount, String tableName)
			throws SQLException {
		  String updateQuery = "update "+ tableName+" set id = ?, senderName= ?,  senderFirstName=?, senderLastName=?,senderEmailFirstSegment=?, totalMessageCount=?, totalSenderCount=? where emailAddress = ?";
		  PreparedStatement preparedStmt = conn.prepareStatement(updateQuery);
		  preparedStmt.setInt (1, id);		  preparedStmt.setString (2, name);		  preparedStmt.setString (3, firstName);		  preparedStmt.setString (4, lastName);
		  preparedStmt.setString (5,senderEmailFirstSegment);		  preparedStmt.setInt (6, totalMessageCount);		  preparedStmt.setInt (7,totalSenderCount);	preparedStmt.setString (8, email);
		  Integer i = preparedStmt.executeUpdate();
		  if(i>0)   {
		          //System.out.println("success");
		  }
		  else  {
		         System.out.println("stuck somewhere email " + email + " name " + name);
		    }
		  updateQuery =null;
	}
	static void updateAllMessagesTableWithSenderEmail(Connection conn, Integer mid, String email, String name, String firstName, String lastName, String nameSegment)
			throws SQLException {
		String updateQuery = "update allmessages set senderEmail = ?, senderFullName= ?, senderFirstName=?, senderLastName=?, senderEmailFirstSegment=? where messageID = ?";
		  PreparedStatement preparedStmt = conn.prepareStatement(updateQuery);		  
		  preparedStmt.setString (1, email);		  preparedStmt.setString (2, name);		  preparedStmt.setString (3, firstName);		  preparedStmt.setString (4, lastName);
		  preparedStmt.setString (5, nameSegment);		  preparedStmt.setInt(6, mid);
		  Integer i = preparedStmt.executeUpdate();
		  if(i>0)   {
		          //System.out.println("success");
		  }
		  else  {
		         System.out.println("stuck somewhere mid " + mid + " newName " + name);
		    }
		  updateQuery =null;
	}
}
