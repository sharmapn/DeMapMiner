package readCommits.structs;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

public class insertIntoDB {
	
	static Integer mid;	//we want to start assigning state messages at a number which wont be reached while reading in messages previously..normally set to 5 million
	static Connection connection = null;
	static String tableName ="";
	
	public void initialiseVariables(String tablename, Connection conn, Integer startMID){
		tableName=tablename;
		connection = conn;
		mid = startMID;
	}
	
	public void closeDB() {
		try{
			connection.close();
		}
		catch (SQLException se){
			System.out.println("SQL Exception while closing connection" + se.toString());
  			System.out.println(StackTraceToString(se)  );
		}
	}
	
	public static void insertintoDatabase(String proposalIdentifier,String proposal, Date date, Timestamp v_dateTimeStamp, String author, String status, String proposalTitle, String entireMessage, String fileName, Integer lineCounter) 
	{
		try {
			java.sql.Date sqlDate = null;
			Statement stmt = connection.createStatement();
			mid++;
			String email = "Status : "+ status;
	
			if(date==null || date.equals("")) {
				
			}
			else {
				//convert ate to java.sql date
				java.util.Calendar cal = Calendar.getInstance();				
//				System.out.println("date " + date);
				cal.setTime(date);
				cal.set(Calendar.HOUR_OF_DAY, 0);				cal.set(Calendar.MINUTE, 0);				
				cal.set(Calendar.SECOND, 0);					cal.set(Calendar.MILLISECOND, 0);
				sqlDate = new java.sql.Date(cal.getTime().getTime()); // your sql date
			}
			//[INSERT INTO proposalStates] i am not sure what proposalstates table is used for..maybe to get different states of proposal and title at different times
			System.out.println(" Inserting proposal: " + proposal + " proposalIdentifier: " + proposalIdentifier);
			String query = " INSERT INTO "+tableName+"(messageID,"+proposalIdentifier+", "+proposalIdentifier+"title, date2,dateTimeStamp, author, email, analyseWords, sendername, statusFrom, statusTo, statusChanged,subject, required,entireMessage,file,line)"
					+ " values (?, ?, ?, ?,?, ?,?,?, ?, ?,?, ?, ?,?,?,?,?)";
	
			// create the mysql insert preparedstatement
			PreparedStatement preparedStmt = connection.prepareStatement(query);
			preparedStmt.setInt (1, mid);			preparedStmt.setString (2, proposal);				preparedStmt.setString (3, proposalTitle);	
			preparedStmt.setDate   (4, sqlDate);	preparedStmt.setTimestamp(5, v_dateTimeStamp);	preparedStmt.setString (6, author);
			preparedStmt.setString   (7, email);	preparedStmt.setString   (8, email);			preparedStmt.setString 	(9, "");
			preparedStmt.setString   (10, "");		preparedStmt.setString   (11, "");				preparedStmt.setInt 	(12, 0);
			preparedStmt.setString   (13, "");		preparedStmt.setInt   (14, 0);					preparedStmt.setString   (15, entireMessage);
			preparedStmt.setString   (16, fileName);	
			preparedStmt.setInt   (17, lineCounter);	
			preparedStmt.execute();
			stmt.close();
		}
		catch (SQLException se){
			System.out.println("SQL Exception " + se.toString());
  			System.out.println(StackTraceToString(se)  );
		}
		catch (Exception e){
			System.out.println("Exception " + e.toString());
  			System.out.println(StackTraceToString(e)  );
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

  private static void outputResultSet(ResultSet rs) throws Exception {
	    ResultSetMetaData rsMetaData = rs.getMetaData();
	    int numberOfColumns = rsMetaData.getColumnCount();
	    for (int i = 1; i < numberOfColumns + 1; i++) {
	      String columnName = rsMetaData.getColumnName(i);
	      System.out.print(columnName + "   ");
	
	    }
	    System.out.println();
	    System.out.println("----------------------");
	
	    while (rs.next()) {
	      for (int i = 1; i < numberOfColumns + 1; i++) {
	        System.out.print(rs.getString(i) + "   ");
	      }
	      System.out.println();
	    }
  }
}
