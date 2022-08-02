package miner.process;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class InsertMessageTracking {
	
	static String v_tablename = "trackmessagesplit";
	static String v_sentence_tablename = "tracksentencemultiplePEPs";
	
	public static void insertMessageTracking(Connection connec, Integer pep, Integer messageid, String marker) {
		Integer epSize=0,npSize=0,ppSize=0;
		Statement stmt = null;
		try {		
			
						
			//String sql = "INSERT INTO " + v_tablename + " (arg1, relation, arg2, sentence, pep, messageID) VALUES ('"+ ar1 + "','" + re + "','" + ar2 + "','" + v_sentence + "')";
			String sql = "INSERT INTO " + v_tablename + " (pep, messageid,splitmarker) " //24
					+ "VALUES (?,?,?)";
				//	+ "'" + ar1 + "','" + re + "','" + ar2 + "','" + v_sentence + "','" + prevSentence + "','" + nextSentence + "'," + pep + "," +  MID + ")";
			PreparedStatement preparedStmt = connec.prepareStatement(sql);
			preparedStmt.setInt (1, pep);    	    preparedStmt.setInt(2, messageid);				preparedStmt.setString(3, marker);    	    
		    preparedStmt.execute();
			//System.out.println("\tInserted records into the  " + v_tablename + " table...");

		} catch (SQLException se) {
			// Handle errors for JDBC
			se.printStackTrace();			System.out.println("Error..." + stmt.toString());			//System.out.println("Error..." + ar1 + "," + re + "," + ar2);
		} catch (Exception e) {
			// Handle errors for Class.forName
			e.printStackTrace();			System.out.println("Error..." + stmt.toString());			//System.out.println("Error..." + ar1 + "," + re + "," + ar2);
		} finally {
			// finally block used to close resources
//			try {
//				if (stmt != null)
//					conn.close();
//			} catch (SQLException se) {
//			}// do nothing
//			try {
//				if (conn != null)
//					conn.close();
//			} catch (SQLException se) {
//				se.printStackTrace();
//			}// end finally try
		}// end try
		//System.out.println("Goodbye!");
	// end main
}// end JDBCExample
	
	public static void insertMultiplePEPsInSentenceTracking(Connection connec, Integer pep, Integer messageid, String sentence, Integer path) {
		Integer epSize=0,npSize=0,ppSize=0;
		Statement stmt = null;
		try {		
			//path = 0 - only another pep in sentence
			//       1 - multiple PEPs in sentence
					 
			
			String sql = "INSERT INTO " + v_sentence_tablename + " (originalpep, messageid,sentence, path) " //24
					+ "VALUES (?,?,?,?)";
				//	+ "'" + ar1 + "','" + re + "','" + ar2 + "','" + v_sentence + "','" + prevSentence + "','" + nextSentence + "'," + pep + "," +  MID + ")";
			PreparedStatement preparedStmt = connec.prepareStatement(sql);
			preparedStmt.setInt (1, pep);    	    preparedStmt.setInt(2, messageid);				preparedStmt.setString(3, sentence);    	 preparedStmt.setInt(3, path);     
		    preparedStmt.execute();
			//System.out.println("\tInserted records into the  " + v_tablename + " table...");

		} catch (SQLException se) {
			// Handle errors for JDBC
			se.printStackTrace();			System.out.println("Error..." + stmt.toString());			//System.out.println("Error..." + ar1 + "," + re + "," + ar2);
		} catch (Exception e) {
			// Handle errors for Class.forName
			e.printStackTrace();			System.out.println("Error..." + stmt.toString());			//System.out.println("Error..." + ar1 + "," + re + "," + ar2);
		} finally {
			// finally block used to close resources
//			try {
//				if (stmt != null)
//					conn.close();
//			} catch (SQLException se) {
//			}// do nothing
//			try {
//				if (conn != null)
//					conn.close();
//			} catch (SQLException se) {
//				se.printStackTrace();
//			}// end finally try
		}// end try
		//System.out.println("Goodbye!");
	// end main
}// end JDBCExample

}
