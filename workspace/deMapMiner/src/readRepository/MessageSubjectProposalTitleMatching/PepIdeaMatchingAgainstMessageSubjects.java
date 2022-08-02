package readRepository.MessageSubjectProposalTitleMatching;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import connections.MysqlConnect;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.process.DocumentPreprocessor;
import javaStringSimilarity.info.debatty.java.stringsimilarity.Levenshtein;
import miner.process.LabelTriples;
import miner.process.PythonSpecificMessageProcessing;
import miner.processLabels.TripleProcessingResult;
 
//group similar subjects somehow so that pep titles can be matched with those in idea stage
//SINCE MULTIPLE PEPS CAN HAVE THE SAME MESSAGE (MESSAGEID as these peps are mentioned in the message) select messages on pep by pep basis to prevent duplicated messages returning
//dec 2018

public class PepIdeaMatchingAgainstMessageSubjects  {        
	static Levenshtein levenshtein = new Levenshtein();	// Levenshtein
	static Integer Levenshtein_distance_threshold = 6,Levenshtein_distance_threshold_small_terms = 2;
	static String sourceTable = "allmessages";		//table to get all same similar message subjects
	static String storeTable  = "allmessagesmatchingsubjects";			//table to store all processed message subjects same similar message subjects
	static Connection conn= null;
	static Statement stmt = null;
    public static void main(String[] args)  {
		
    	try{  
    		MysqlConnect mc = new MysqlConnect();    		conn = mc.connect();    stmt = conn.createStatement();
        	//comment below line after reading
    		findSameMessageSubjects(mc);	//group similar subjects somehow maybe in this part just assign a number to all exact similar message subjects
 	    	System.out.println("Same assignment complete "); 	    	
 	    	//assign these groups a pep number - maybe -ves , from -2 onwards
// 	    	findSimilarMessageSubjects(conn,mc); 
// 	    	System.out.println("Similar assignment complete ");
 	    	//then see what happened to them, "pep idea","idea", "idea rejected" "no support" 
 	    	//select last message of each cluster and see what happenned
 	    	//example message containing idea is 6516..by searching for terms positive response, idea, write pep,
 	    	mc.disconnect();
 	    } catch (Exception ex) {
 	    	System.out.println("Error " + ex.getMessage() +ex.toString());
 	    	System.out.println(StackTraceToString(ex)  );	
 	    }
    }
    //lets first try on allmessages_ideas table first rather than allmessages table
	private static void findSameMessageSubjects(MysqlConnect mc) throws IOException, SQLException {
		Integer v_sourcePEP,v_sourceMessageID,vv_MessageID, counter=0, totalMessages=0;
		String v_sourceMessageSubject="",processedMessageSubject="";				//we want to work on all unassigned pep to further assign them		
		String sql2="",sql3 = "";		Statement stmt2 =null;		
		System.out.println("Number of total messages: " + totalMessages);
				//LIKE '%" + keyWord.trim().toLowerCase() + "%' LIMIT 100");
		int rowsInserted=0;
		Integer distinctSubjectCounter=-1, updatedCounter=0;
		String v_processedSubject="",vv_processedSubject="";	
		//200 - 501
		for (int i=0; i< 5000;i++) {	System.out.println("Processing New PEP: " + i);		//and messageID = 376335
			String sql = "select pep, messageID, processedSubject from "+sourceTable+" where pep = "+i+" order by pep,messageID;"; // where pep = -1;";	// distinct		BETWEEN 200 AND 300 
			ResultSet rs = stmt.executeQuery(sql);	// limit 100
			totalMessages = mc.returnRSCount(sql, conn);
			
			while (rs.next()) { // System.out.println("PEP:");
				counter++;
				v_sourcePEP = rs.getInt(1); 		v_sourceMessageID = rs.getInt(2);			processedMessageSubject = rs.getString(3); 		
				if (v_sourceMessageSubject != null && !v_sourceMessageSubject.isEmpty()){
					//System.out.println("v_messagesubject: "+v_messagesubject + ": "+v_messageID); 
					//if(v_messagesubject.contains("[Python-ideas]"))
					//some message subjects may be same but belonging to different mailing lists, therefore we remove the mailing list which is in square brackets	
	//$				processedMessageSubject = v_sourceMessageSubject.replaceAll("\\[.*?\\] ?", "").trim();
	//$				processedMessageSubject = removeDoubleSpaces(processedMessageSubject);
	//				processedMessageSubject = v_sourceMessageSubject.replace("'", "");
				}
				//now we look for messages with subjects containing the processed messages
							//we want to work on all unassigned pep to further assign them
				
				try {			//replace(subject,'\\'','') = "+processedMessageSubject+"   ......v_sourceMessageSubject
					//get all other messages with exact same subject																		//we dont want to pair the same messageids again
					sql2 = "select DISTINCT messageID, subject from allmessages where processedSubject = '"+processedMessageSubject+"' and messageID <> "+v_sourceMessageID+" ;";		//can also match 'processedMessageSubject'
					//PreparedStatement stmt5 = new PreparedStatement(conn, sql2);
					//PreparedStatement stmt5 = conn.prepareStatement(sql2);	
					stmt2 =  conn.createStatement();  ResultSet rs21 = stmt2.executeQuery(sql2);	
					//stmt5.setString(1, sourceTable);
					//stmt5.setString(1, processedMessageSubject);	stmt5.setInt(2, v_sourceMessageID); System.out.println("PEP 1:");
					//ResultSet rs21 = stmt5.executeQuery();	System.out.println("PEP:");
					System.out.println("PEP: "+v_sourcePEP+" MessageID: "+v_sourceMessageID+" Number of similar message subjects for processed subject : ("+ processedMessageSubject + ") = "+ mc.returnRSCount(sql2, conn));				
					updatedCounter=0;
					while (rs21.next()) {  
						vv_MessageID	= rs21.getInt(1);				vv_processedSubject = rs21.getString(2);
						//System.out.println("\t"+vv_processedSubject + " messageid: " + v_messageID);
						//updateSameMessageSubjectsInDB(v_messageID, distinctSubjectCounter,conn);
						sql3 = "INSERT INTO "+storeTable+" (proposal,messageID,subject, messageIDsOfMessagesWithSimilarMessageSubjects) VALUES (?,?,?,?)";		 
						try {			
							PreparedStatement statement = conn.prepareStatement(sql3);			
							statement.setInt   (1,v_sourcePEP);					statement.setInt(2,v_sourceMessageID);					
							statement.setString(3, processedMessageSubject); 	statement.setInt(4, vv_MessageID);					
							rowsInserted = statement.executeUpdate();
							if (rowsInserted > 0) {
							    //System.out.println("A new result record was inserted in DB successfully!");
							}
						} catch (SQLException e) {					e.printStackTrace();				}
						updatedCounter++;
					}
				}
				catch(SQLException e) {
					
				}
				if(counter % 100==0) {
					System.out.println("\t"+counter+" messages processed," + (totalMessages-counter) + " left to process.");
				}
			} // end if
		}
	}
	
	//i have to simplify this function like teh baove one..too many sub functions
	private static void findSimilarMessageSubjects(Connection conn,MysqlConnect mc) throws IOException, SQLException {
		Integer pepNumber,v_messageID, distinctSubjectCounter=-1, updatedCounter=0;
		String v_author="",v_mainProcessedSubject="",vv_processedSubject="";				//we want to work on all unassigned pep to further assign them
		Multimap<Integer, String> multiMapFinalTitleText = ArrayListMultimap.create();
		
		double levenshtein_distance;
		String subset;
		boolean matched=false;
		
		//how and why did we create this table and populate it
		String sql = "select DISTINCT processedSubject from ideatitles_matching;";
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);	// limit 100 		
		System.out.println("Number of distinct message subjects : " +mc.returnRSCount(sql, conn));
		
		//populate map
    	populateMapWithMessageTitles(conn,multiMapFinalTitleText);
		
				//LIKE '%" + keyWord.trim().toLowerCase() + "%' LIMIT 100");	    		
		while (rs.next()) {  
			distinctSubjectCounter--;
			v_mainProcessedSubject = rs.getString(1);
			Integer lengthofMainProcessedSubject = v_mainProcessedSubject.length();
			//System.out.println("v_processedSubject:::"+v_processedSubject);
			if(v_mainProcessedSubject == null || v_mainProcessedSubject.isEmpty()){
				break;
			}
			if(v_mainProcessedSubject.contains("\\")){
				v_mainProcessedSubject=v_mainProcessedSubject.replaceAll("\\", "");
			}
			if(v_mainProcessedSubject.contains("\'")){
				v_mainProcessedSubject = v_mainProcessedSubject.replaceAll("\'","");
			}
			
			//get all other messages with exact same subject
			String sql2 = "select messageID, processedSubject from ideatitles_matching where processedSubject = '" + v_mainProcessedSubject +"';";
			Statement stmt2 = conn.createStatement();
			ResultSet rs2 = stmt2.executeQuery(sql2);	// limit 100 	
			System.out.println("Number of similar message subjects for subject : ("+ v_mainProcessedSubject + ") = "+ mc.returnRSCount(sql2, conn));
			updatedCounter=0;
			while (rs2.next()) {  
				matched = false;
				v_messageID	= rs2.getInt(1);
				vv_processedSubject = rs2.getString(2);
				Integer lengthofProcessedSubject = vv_processedSubject.length();
				//System.out.println("\t"+vv_processedSubject + " messageid: " + v_messageID);
				
				//start comparison
				//take the title and compare at different offsets in the subject line
				//dont test subsetting now - just run on entire subject
				/*
				for (int k=0; k< lengthofMainProcessedSubject- lengthofProcessedSubject;k++){
					subset = vv_processedSubject.substring(k, k+lengthofProcessedSubject).trim();
					//for firstpeptitle match
					levenshtein_distance = levenshtein.distance(vv_processedSubject, subset);					
					if(levenshtein_distance < Levenshtein_distance_threshold ){	//OLD WAY if (subject.toLowerCase().contains(allTitlesInFirstState.toLowerCase())){
						if (numbersInTermsMatch(vv_processedSubject, subset)){
							matched=true;
							updateSimilarMessageSubjectsInDB( v_messageID, distinctSubjectCounter,conn);
							updatedCounter++;
							System.out.println("\t"+updatedCounter+" similar subjects updated");
							System.out.println();
						}
					}
					//newTitle=false;	//while within this loop, set this variable to indicate we are within the same title
				} //end for loop
				*/
				//just test on entire subjects
				levenshtein_distance = levenshtein.distance(v_mainProcessedSubject,vv_processedSubject);					
				if(levenshtein_distance < Levenshtein_distance_threshold ){	//OLD WAY if (subject.toLowerCase().contains(allTitlesInFirstState.toLowerCase())){
					if (numbersInTermsMatch(v_mainProcessedSubject,vv_processedSubject)){
						matched=true;
						updateSimilarMessageSubjectsInDB( v_messageID, distinctSubjectCounter,conn);
						updatedCounter++;
						
					}
				}
			}			
			System.out.println("\t"+updatedCounter+" similar subjects updated");
			System.out.println();			
		} // end if
	}
	
	public static void updateSimilarMessageSubjectsInDB(Integer messageID, Integer distinctSubjectCounter,Connection conn)
	{	 
		String query = "update ideatitles_matching set editcluster = ? where messageID = ?";
		try {
			PreparedStatement preparedStmt = conn.prepareStatement(query);	
			preparedStmt = conn.prepareStatement(query);   
			preparedStmt.setInt   (1, distinctSubjectCounter);			preparedStmt.setInt	(2, messageID);
			preparedStmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
    
    public static Boolean numbersInTermsMatch(String finalTitle, String subset){
		//extrct numbers/doubles from string
		//and match them
		String numberOnlyFinalTitle= finalTitle.replaceAll("[^0-9]", "");
		String numberOnlySubset= subset.replaceAll("[^0-9]", "");
//**		System.out.println("extracted numbers: " + numberOnlyFinalTitle + " " + numberOnlySubset);
		if (numberOnlyFinalTitle.equals(numberOnlySubset))
			return true;
		else
			return false;
	}
	
	public boolean isNumeric(String s) {
	    return s != null && s.matches("[-+]?\\d*\\.?\\d+");  
	} 
	
	
	private static void populateMapWithMessageTitles(Connection conn, Multimap<Integer, String> multiMapSubject) throws SQLException {
		String messageSubject;		int pep;		String sql;
		Statement stmt;		ResultSet rs;
		// put values into map for A	        
		sql = "SELECT messageID, processedSubject from ideatitles_matching where processedSubject IS NOT NULL order by messageID asc ";  //where pep != -1 order by pep asc LIMIT 5000
		stmt = conn.createStatement();
		rs = stmt.executeQuery( sql );	
		while (rs.next()) {
			pep = rs.getInt(1);
			messageSubject = rs.getString(2);
			if (messageSubject != null){
				messageSubject = cleanTerm(messageSubject);
				multiMapSubject.put(pep, messageSubject);
			} 
		}
	}
	private static String cleanTerm(String term) {
		if (term.contains("["))			term = term.replaceAll("\\[", "").trim();
		if (term.contains("]"))			term = term.replaceAll("\\]", "").trim();		
		if (term.contains("'"))			term = term.replaceAll("'", "").trim();
		if (term.contains(","))			term = term.replaceAll(",", "").trim();
		if (term.contains("`"))			term = term.replaceAll("`", "").trim();
		if (term.contains(".")){
			Integer loc=0;
			for (char ch: term.toCharArray()) {
				if (ch=='.'){
//**					System.out.println(" here a, term (" +term +")  loc "+loc); 
				//term
					//get chars before and after space, if they are numbers, do direct match
					//boolean numberBefore = Character.isDigit(term.charAt(term.indexOf(".") - 1));
					//boolean numberAfter =  Character.isDigit(term.charAt(term.indexOf(".") + 1));
					boolean numberBefore = Character.isDigit(loc - 1);
					boolean numberAfter =  Character.isDigit(loc + 1);
					if (numberBefore && numberAfter){
						//do nothing
					}
					else{
						//term = term.replace(".", " ");
						StringBuilder myTerm = new StringBuilder(term);
						myTerm.setCharAt(loc, ' ');
					}
				}
				loc++;
			}
		}
		if (term.contains("-"))			term = term.replace("-", " ");
		if (term.contains("_"))			term = term.replace("_", " ");
		if (term.contains("\""))			term =term.replace("\"", "");
		if (term.contains("\\"))			term =term.replace("\\", "");
		if (term.contains("/"))			term =term.replace("/", "");
		if (term.contains("//"))			term =term.replace("//", "");
		if (term.contains(":"))			term =term.replace(":", "");
//		if (term.contains("(") && term.contains(")")){
//			term = term.replaceAll("(", " ");
//			term = term.replaceAll(")", " ");
//		}
		
		term = removeDoubleSpaces(term);
		term= term.trim().toLowerCase();
		return term;
	}
	
	public static String removeDoubleSpaces(String term) {
		//for (int i=0;i<5;i++){
		if(term.contains("      "))			term = term.replaceAll("      ", " ");
		if(term.contains("     "))			term = term.replaceAll("     ", " ");
		if(term.contains("    "))			term = term.replaceAll("    ", " ");
		if(term.contains("   "))			term = term.replaceAll("   ", " ");
		if(term.contains("  "))			term = term.replaceAll("  ", " ");
		//}
		term =term.trim();
		return term;
	}
}