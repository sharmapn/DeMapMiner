package de.mpii.clause.driver;

//STEP 1. Import required packages
import java.sql.*;


public class InsertExtractedTripleIntoRelationTable {

	public static void insertTriplesintoTable(Connection connec, String proposalIdentifier, String ar1, String re, String ar2, String v_sentence,String prevSentence,String nextSentence, 
			String entireParagraph,String prevParagraph,String nextParagraph, String v_tablename, Integer pep, Integer MID,Integer sentenceCounter, Integer paragraphCounter, Integer lineNumber,
			boolean isEnglish,boolean isFirstParagraph, boolean isLastParagraph, String msgSubject, String msgAuthorRole, boolean isCorefParagraph,
			String  allVerbsInSub, String allVerbsInRel,String allVerbsInObj,String allVerbsInMsgSub,
			String  allNounsInSub, String allNounsInRel,String allNounsInObj,String allNounsInMsgSub) {
		Integer epSize=0,npSize=0,ppSize=0;
		Statement stmt = null;
		try {		
			stmt = connec.createStatement();
			ar1 = ar1.replace("/", "").replace("\"", "").replace("\\", "").replace("//", "");			//ar1.replace("'", "").
			ar2 = ar2.replace("/", "").replace("\"", "").replace("\\", "").replace("//", "");			//ar2.replace("'", "").
			re = re.replace("/", "").replace("\"", "").replace("\\", "").replace("//", "");				//re.replace("'", "").
			v_sentence = v_sentence.replace("/", "").replace("\"", "").replace("\\", "").replace("//", "");	//replace("'", "").
						
			//String sql = "INSERT INTO " + v_tablename + " (arg1, relation, arg2, sentence, pep, messageID) VALUES ('"+ ar1 + "','" + re + "','" + ar2 + "','" + v_sentence + "')";
			String sql = "INSERT INTO " + v_tablename + " (arg1, relation, arg2, sentence,previousSentence,nextSentence,entireParagraph, prevParagraph, nextParagraph, "+proposalIdentifier+", messageID,sentenceCounter, " //12
					+ "paragraphCounter, lineNumber,isEnglish,isFirstParagraph,isLastParagraph,msgSubject, msgAuthorRole,isCorefParagraph,allVerbsInSub, allVerbsInRel, allVerbsInObj,allVerbsInMsgSub,"
					+ "allNounsInSub, allNounsInRel, allNounsInObj,allNounsInMsgSub) " //24
					+ "VALUES (?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?)";
				//	+ "'" + ar1 + "','" + re + "','" + ar2 + "','" + v_sentence + "','" + prevSentence + "','" + nextSentence + "'," + pep + "," +  MID + ")";
			PreparedStatement preparedStmt = connec.prepareStatement(sql);
			preparedStmt.setString (1, ar1);    	    preparedStmt.setString (2, re);				preparedStmt.setString(3, ar2);    	    preparedStmt.setString (4, v_sentence);
			preparedStmt.setString (5, ""); /*prevSentence);*/   preparedStmt.setString (6, ""); /*nextSentence);*/	
			if(entireParagraph.length() < 5000)				epSize= entireParagraph.length();			else				epSize=5000;
			preparedStmt.setString (7, "");/* entireParagraph.substring(0, epSize));*/	//had to substring because of GC overhead limit 
			if(prevParagraph.length() < 5000)				ppSize= prevParagraph.length();			else				ppSize=5000;
			preparedStmt.setString (8, ""); /*prevParagraph.substring(0, ppSize)); */   	    
			if(nextParagraph.length() < 5000)				npSize= nextParagraph.length();			else				npSize=5000;			
			preparedStmt.setString (9, "") ; /*nextParagraph.substring(0, npSize));*/  			
			preparedStmt.setInt    (10, pep);		    preparedStmt.setInt    (11, MID);
			preparedStmt.setInt    (12, sentenceCounter);		preparedStmt.setInt    (13, paragraphCounter);		preparedStmt.setInt    (14, lineNumber);	preparedStmt.setBoolean  (15, isEnglish);
			preparedStmt.setBoolean(16, isFirstParagraph);	preparedStmt.setBoolean(17, isLastParagraph);  
			preparedStmt.setString (18, msgSubject);  preparedStmt.setString (19, msgAuthorRole);  preparedStmt.setBoolean(20, isCorefParagraph); 
			preparedStmt.setString (21, allVerbsInSub);	preparedStmt.setString (22, allVerbsInRel);	preparedStmt.setString (23, allVerbsInObj);	preparedStmt.setString (24, allVerbsInMsgSub);
			preparedStmt.setString (25, allNounsInSub);	preparedStmt.setString (26, allNounsInRel);	preparedStmt.setString (27, allNounsInObj);	preparedStmt.setString (28, allNounsInMsgSub);
		    preparedStmt.execute();
			//System.out.println("\tInserted records into the  " + v_tablename + " table...");

		} catch (SQLException se) {
			// Handle errors for JDBC
			se.printStackTrace();			System.out.println("Error..." + stmt.toString());			System.out.println("Error..." + ar1 + "," + re + "," + ar2);
		} catch (Exception e) {
			// Handle errors for Class.forName
			e.printStackTrace();			System.out.println("Error..." + stmt.toString());			System.out.println("Error..." + ar1 + "," + re + "," + ar2);
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
