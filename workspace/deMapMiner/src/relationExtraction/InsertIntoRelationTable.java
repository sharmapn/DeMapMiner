package relationExtraction;

//STEP 1. Import required packages
import java.sql.*;

public class InsertIntoRelationTable {
	public static void insert(String ar1, String re, String ar2, String v_sentence, String v_tablename, Connection conn) {
		//Connection conn = null;
		Statement stmt = null;
		try {
			// STEP 4: Execute a query
			// System.out.println("Inserting records into the table...");
			stmt = conn.createStatement();
//			String ar1 = "bdfl";//			String re = "did";//			String ar2 = "pep";			
			ar1 = ar1.replace("'", "").replace("/", "").replace("\"", "").replace("\\", "").replace("//", "");			
			ar2 = ar2.replace("'", "").replace("/", "").replace("\"", "").replace("\\", "").replace("//", "");			
			re = re.replace("'", "").replace("/", "").replace("\"", "").replace("\\", "").replace("//", "");
			v_sentence = v_sentence.replace("'", "").replace("/", "").replace("\"", "").replace("\\", "").replace("//", "");
			String sql = "INSERT INTO " + v_tablename + " (arg1, relation, arg2, sentence) VALUES ('"+ ar1 + "','" + re + "','" + ar2 + "','" + v_sentence + "')";
			stmt.executeUpdate(sql);			
//			System.out.println("Inserted records into the " + v_tablename + " table...");

		} catch (SQLException se) {
			se.printStackTrace();
			System.out.println("Error..." + stmt.toString());			System.out.println("Error..." + ar1 + "," + re + "," + ar2);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error..." + stmt.toString());			System.out.println("Error..." + ar1 + "," + re + "," + ar2);
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
	}// end main
}// end JDBCExample
