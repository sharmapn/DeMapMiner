package readRepository.readRepository;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

//import javax.swing.text.MutableAttributeSet;
//import javax.swing.text.html.HTML.Tag;
//import javax.swing.text.html.HTMLEditorKit.ParserCallback;
//import javax.swing.text.html.parser.ParserDelegator;

import connections.MysqlConnect;
import utilities.PepUtils;

import java.io.IOException;
import java.io.FileReader;
import java.io.Reader;
import java.util.List;
import java.util.ArrayList;

//import javax.swing.text.html.parser.ParserDelegator;
//import javax.swing.text.html.HTMLEditorKit.ParserCallback;
//import javax.swing.text.html.HTML.Tag;
//import javax.swing.text.MutableAttributeSet;


import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

//get the pep summary and store in MAIN TABLE
public class _GetStorePEPSummary {
	
	
	public static File pepSummaryFolder = new File("C:\\Users\\psharma\\Google Drive\\PhDOtago\\scripts\\inputFiles\\peps-master");
	
	public static void main(String[] args) throws IOException, SQLException {
		InputStream is; BufferedReader br; String line; StringBuilder sb;
		
		MysqlConnect mc = new MysqlConnect();
		Connection conn = mc.connect();
		
		PepUtils pu = new PepUtils();
		ArrayList<Integer> UniquePeps = pu.returnUniquePepsInDatabasePEPDetails();
		
		Integer messageID = 700000;
		
		for (int j = 0; j < UniquePeps.size() - 2; j++) { // -2 because last 2 values 																// are too big
			Integer i = UniquePeps.get(j);				
			String pepNumber = String.valueOf(i);
			pepNumber=fixedLengthString(pepNumber,4);
			br=new BufferedReader(new FileReader(pepSummaryFolder +  "\\" +"pep-"+pepNumber + ".txt"));
			String fileLoc = pepSummaryFolder +  "\\" +"pep-"+pepNumber + ".txt";
			try {
				extractMessageFromFile(br,conn,i, messageID, fileLoc );
				//System.out.println(message);
				//updateDatabase(conn,i,message);				
				System.out.println("Processed and Updated DB sucessfully for: " + fileLoc);
			}
			
			catch (Exception e) {
				System.out.println("Error reading pep " +  fileLoc);
				System.out.println(e.toString());
	        }	
			messageID++;
		}		    
		 conn.close();	
		 System.out.println("Processing Finished");
	}
	
	public static String fixedLengthString(String string, int length) {
		if (string.length() == 1)
			string = "000" + string;
		else if (string.length() == 2)
			string = "00" + string;
		else if (string.length() == 3)
			string = "0" + string;
		return string;
	}
	
	public static void extractMessageFromFile(BufferedReader br, Connection conn, Integer pepNumber,
		Integer v_messageID, String fileLoc) {
		String From = "";
		String createdDate = null;
		String author = "";
		String subject = "";
		// String title = null;
		String message = "", line2;
		boolean start = false;
		Date date9 = null;
		java.sql.Date sqlDate = null;
		// List<String> lines = extractText(br);
		try {
			while ((line2 = br.readLine()) != null) {
				if (line2.contains("Created: ")) {
					createdDate = line2.replace("Created:", "");
					createdDate = createdDate.trim();
					date9 = findDate(createdDate);
					// sqlDate = java.sql.Date.valueOf( createdDate );
					// System.out.println(createdDate);
				}
				// else if (line2.startsWith("Title: "))
				// title = line2.replace("Title: ","");
				else if (line2.startsWith("Author: "))
					author = line2.replace("Author: ", "");
				if (line2.contains("PEP:"))
					start = true;
				if (start == true) {
					message = message + line2 + "\n";
					// System.out.println(line2);
				}
				if (line2.contains("© 2017"))
					start = false;
				if ("".equals(line2.trim())) // empty lines
					message = message + line2 + "\n";
			}
			insertIntoDatabase(conn, pepNumber, message, v_messageID, author, fileLoc, sqlDate);
		} catch (Exception e) {
			System.out.println("Exception " + e.toString());
			e.printStackTrace();
		}
	}
	
	public static void insertIntoDatabase(Connection conn, Integer pepNumber, String message, Integer v_messageID, String author, String fileLoc, java.sql.Date date9){		
		 String query = " insert into allpeps (pep, email ,analysewords, folder, messageID, author, date2)" + " values (?, ?,?, ?,?,?,?)";		 
		 try {
			//calculate date
			 //convert ate to java.sql date
//         java.util.Calendar cal = Calendar.getInstance();
//         cal.setTime(date9);
//	     cal.set(Calendar.HOUR_OF_DAY, 0);
//	     cal.set(Calendar.MINUTE, 0);
//	     cal.set(Calendar.SECOND, 0);
//	     cal.set(Calendar.MILLISECOND, 0);
//	     java.sql.Date sqlDate = new java.sql.Date(cal.getTime().getTime()); // your sql date    
	     //java.sql.Date sqlDate = new java.sql.Date(date9.
	    
		 //just insert today's date
		 java.sql.Date sqlDate = null;
		 try {
			 DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			 Date date = new Date();
			 Date myDate;
			 sqlDate = new java.sql.Date(date.getTime());
		 } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		 }
		 
	      // create the mysql insert preparedstatement
	      PreparedStatement preparedStmt = conn.prepareStatement(query);
	      preparedStmt.setInt    (1, pepNumber);
	      preparedStmt.setString (2, message);
	      preparedStmt.setString (3, message);
	      preparedStmt.setString (4, fileLoc);
	      preparedStmt.setInt (5, v_messageID);
	      preparedStmt.setString (6, author);
	      preparedStmt.setDate (7, sqlDate);
	      // execute the preparedstatement
	      preparedStmt.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("\t\tError inserting pep " + pepNumber);
		}		
	}

//	private static void extractMessage(BufferedReader br, Connection conn, Integer pepNumber, URL url, Integer v_messageID) throws IOException {
//		 String From = null; 
//         String createdDate = null; 
//         String author = null;
//         String subject = null;
//        // String title = null;
//		
//		String message = "";
//		boolean start=false;
//		List<String> lines = extractText(br);
//		for (String line2 : lines) {
//			if (line2.startsWith("Created: "))   
//				createdDate = line2.replace("Created:","");
//  	      	//else if (line2.startsWith("Title: "))  
//  	      	//	title = line2.replace("Title: ","");
//  	      	else if (line2.startsWith("Author: ")) 
//  	      		author = line2.replace("Author: ",""); 
//			
//			if(line2.contains("PEP:"))
//				start=true;
//			if(start==true){
//				message = message + line2 + "\n";
////				System.out.println(line2);
//			}
//			if(line2.contains("© 2017"))
//				start=false;
//		}
////		System.out.println(message);
//		//return message;
//		insertIntoDatabase(conn,pepNumber,message, url, v_messageID, author);
//	}
	
	public static Date findDate(String v_line)  { 
//		  if (v_line == null) return null;
		  
		  String[] patterns = {//"EEE, dd MMM yyyy hh:mm:ss UTC",
		            "yyyy.MM.dd G 'at' HH:mm:ss z",
		            "EEE, MMM dd, yyyy 'at' HH:mm a",
		            "EEE, MMMM dd, yyyy HH:mm a",       // Trying
		            "EEE, MMM dd, yyyy 'at' HH:mm",
		            "MMM dd, yyyy HH:mm a",
		            "EEE, dd MMM yyyy HH:mm:ss ZZZZ",
		            "EEE, MMM d, ''yy",
		            "yyyyy.MMMMM.dd GGG hh:mm aaa",
		            "EEE, d MMM yyyy HH:mm:ss Z",
		            "yyMMddHHmmssZ",
		            "d MMM HH:mm yyyy",			// 5 Feb 16:53 2003
		            "d MMM yyyy HH:mm:ss z",
		            "yyyy-MM-dd'T'HH:mm:ss",
		            "yyyy-MM-dd",   
		            "yyyy-MMM-dd", 
		            "yyyy-MM-dd HH:mm",   
		            "yyyy/MM/dd HH:mm:ss",   
		            "yyyy-MM-dd'T'HH:mm:ss'Z'",
		            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
		            "yyyy-MM-dd'T'HH:mm:ssZ",
		            "yyyy-MM-dd'T'HH:mm:ss.SSSZ",
		            "yyyy-MM-dd'T'HH:mm:ssz",
		            "yyyy-MM-dd'T'HH:mm:ss.SSSz",
		            "EEE, d MMM yyyy HH:mm:ssz",    //NEW
		            "EEE, d MMM yy HH:mm:ssz",
		            "EEE, d MMM yy HH:mm:ss",             
		            "EEE, d MMM yy HH:mm z",
		            "EEE, d MMM yy HH:mm Z",
		            "EEE MMM  d HH:mm:ss yyyy", 
		            "EEE MMM dd HH:mm:ss yyyy",  
		            "EEE, dd MMM yyyy HH:mm:ss z",  
		            "EEE, dd MMM yyyy HH:mm:ss zzzz",  
		            "EEE, d MMM yyyy HH:mm:ss z",
		            "EEE, d MMM yyyy HH:mm:ss Z",
		            "EEE, d MMM yyyy HH:mm:ss ZZZZ",
		            "EEE, d MMM yyyy HH:mm z",
		            "EEE, d MMM yyyy HH:mm Z",	            
		            "d MMM yy HH:mm z",
		            "d MMM yy HH:mm:ss z",
		            "d MMM yyyy HH:mm z",
		            "d MMM yyyy HH:mm:ss z"};
		  String dateC= v_line; 
		  Date pubdate = null;
//		  Pattern p0=Pattern.compile("Date:");
//		  Matcher m0=p0.matcher(v_line);
//		  if (m0.find()){
//			  dateC = v_line.replace("Date:","");
//			  dateC = v_line.replace("Date:","");
		 // System.out.println("dateC " + dateC);
			  if (dateC.length() >30)
				  dateC = dateC.substring(0, 31);
			  dateC = dateC.substring(1, dateC.length());  
			  
			  for (int i = 0; i < patterns.length; i++) {
			        SimpleDateFormat sdf = new SimpleDateFormat(patterns[i], Locale.US);
			        try {
			            pubdate = sdf.parse(dateC);
			          //  System.out.println("hello " + pubdate);	
			            break;
			        } catch (Exception e) {
//			        	System.out.println("hello " + e.getMessage());
			        }
			    }
			//  System.out.println("pubdate " + pubdate);		  
		 // }
		 // System.out.println("pubdate " + pubdate);
		  return pubdate;
	  }
	
	public static void updateDatabase(Connection conn, Integer pepNumber, String message){
		String query = "update pepDetails set pepSummary = ? where pep = ?";
		PreparedStatement preparedStmt;
		try {
			preparedStmt = conn.prepareStatement(query);
			preparedStmt.setString   (1, message);
			preparedStmt.setInt(2, pepNumber);
			// execute the java preparedstatement
			preparedStmt.executeUpdate(); 
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("\t\tError inserting pep " + pepNumber);
		}		
	}
	
//	public static List<String> extractText(Reader reader) throws IOException {
//	    final ArrayList<String> list = new ArrayList<String>();
//	    ParserDelegator parserDelegator = new ParserDelegator();
//	    ParserCallback parserCallback = new ParserCallback() {
//	      public void handleText(final char[] data, final int pos) {
//	        list.add(new String(data));
//	      }
//
//	      public void handleStartTag(Tag tag, MutableAttributeSet attribute, int pos) { }
//	      public void handleEndTag(Tag t, final int pos) {  }
//	      public void handleSimpleTag(Tag t, MutableAttributeSet a, final int pos) { }
//	      public void handleComment(final char[] data, final int pos) { }
//	      public void handleError(final java.lang.String errMsg, final int pos) { }
//	    };
//	    parserDelegator.parse(reader, parserCallback, true);
//	    return list;
//	  }
}
