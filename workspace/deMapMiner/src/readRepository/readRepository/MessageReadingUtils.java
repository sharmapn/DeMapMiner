package readRepository.readRepository;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Splitter;
import com.google.common.collect.Sets;

import connections.MysqlConnect;

public class MessageReadingUtils {
	 // JDBC driver name and database URL
	 static MysqlConnect mc = new MysqlConnect();
	 static Connection conn = mc.connect();
	
	//Use the pep title stored in the database to query python ideas list text files (already downloaded)
	//instead on pep numbers use pep titles	and store in the related pep number db
	//EG PEP 308 Title is Conditional expressions and search for this term in ideas list
	
	//for loop
	//  get a title
	//  search in ideas list if the title exist in any message
	//        for all messages where the title exist
	//		     store in db with the pep number --if not exists beofre for that pep	
	
	public static void main(String[] args) throws IOException {
		Map<Integer,String> pepDetails = new HashMap<Integer,String>();
		pepDetails = getTitle(pepDetails,conn);
		
		for (Map.Entry<Integer, String> entry : pepDetails.entrySet())		
		    System.out.println(entry.getKey() + " / " + entry.getValue());
				
	}
	
	public static Map<Integer,String> getTitle(Map<Integer,String> pepDetails, Connection conn) {
		String title = "";	
		try
	    {
	      // if you only need a few columns, specify them by name instead of using "*"
	      String query = "SELECT pep, title FROM pepdetails order by pep";	     
	      Statement st = conn.createStatement(); // create the java statement 
	      ResultSet rs = st.executeQuery(query);// execute the query, and get a java resultset
	      // iterate through the java resultset
	      while (rs.next())	      {
	        int pepNumber = rs.getInt("pep");	        title = rs.getString("title").trim();
//	        String lastName = rs.getString("last_name");//	        Date dateCreated = rs.getDate("date_created");
//	        boolean isAdmin = rs.getBoolean("is_admin");//	        int numPoints = rs.getInt("num_points");	        
	        pepDetails.put(pepNumber,title);
	        // print the results
	        //System.out.format("%s, %s, %s, %s, %s, %s\n", id, firstName, lastName, dateCreated, isAdmin, numPoints);
//xxx	        System.out.format("%s, %s\n", pepNumber, title);
	      }
	      st.close();
	    }
	    catch (Exception e)    {
	      System.err.println("Got an exception! ");	      System.err.println(e.getMessage());
	    }		
		return pepDetails;
	}
	
	 //output All PepTitle and Numbers
	  public static Map<Integer,String> populateAllPepTitleandNumbers(Map<Integer,String> pepDetails, Connection conn) {
			String title = "";	
			try
		    {
		      String query = "SELECT pep, title FROM pepdetails order by pep";
		      Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(query);     
		      while (rs.next()) {
		        int pepNumber = rs.getInt("pep");
		        title = rs.getString("title").trim();		        title = RemovePunct(title);		        title = title.toLowerCase();
		        //title = processWord(title);		       // title = RemovePunct(title);
//		        String lastName = rs.getString("last_name");//		        Date dateCreated = rs.getDate("date_created");
//		        boolean isAdmin = rs.getBoolean("is_admin");//		        int numPoints = rs.getInt("num_points");	        
		        pepDetails.put(pepNumber,title);
		        System.out.println(pepNumber + " " +title);
		      }
		      st.close();
		    }
		    catch (Exception e) {
		      System.err.println("Got an exception! ");		      System.err.println(e.getMessage());
		    }		
			return pepDetails;
		}
	  
	  static String RemovePunct(String input) 
	  {
			char[] output = new char[input.length()];
			int i = 0;
			for (char ch : input.toCharArray()) {
				if (Character.isLetterOrDigit(ch) || Character.isWhitespace(ch)) {
					output[i++] = ch;
				}
			}
			return new String(output, 0, i);
	  }
	  
	//get the list of pep number from subject line.
	  //use this to narrow down the list of peps a message can belong to
	  //sometimes no pep number would be mentioned in subject therefore have to asign to all pep number in message 
	  // or maybe take a deeper analysis
	 public static List<Integer> getPepNumberFromSubjectLine(List<Integer> v_PEPList, String v_subject, Map<Integer, String> pepDetails, List<String> pepTitlesSearchKeyList) {
			//System.out.println("Message subject: " + v_subject);			
			// return all pep numbers mentioned in subject
			List<Integer> PEPListinSubject = new ArrayList<Integer>();
			Boolean store_pep = true, matched;
			Matcher m5 = null;
			Integer pepNumber = null;
			//check for pep titles in the subject
			
			//just some dirty way to prevent nullpointer exception
			if(v_subject==null)
				v_subject= "test";
			
			for (String pattern : pepTitlesSearchKeyList) 			{
				// System.out.println("pattern " + pattern);
				Pattern p5 = Pattern.compile(pattern);
				m5 = p5.matcher(v_subject);
				while (m5.find()) // while (m.find() || m9.find())
				{
					//get the pep number for that title
					//GET PEP NUMBER USING PEP TITLE	for storing in database table allpeps					              
	                for (Map.Entry<Integer, String> entry : pepDetails.entrySet()) {
	            	   if (entry.getValue().contains(pattern)) {
			               pepNumber = entry.getKey();
			              }				            	   
	                }	
	                System.out.println("Found PEP Title in line of subject: " + pattern + " with pep number: " + pepNumber + " Message subject: " + v_subject);
					PEPListinSubject.add(pepNumber);
					//System.out.println("Pep Found " + pepNumber);			
				} // end while
			} // end for
			// just for debugging code

			
			// return only the list of peps to be inserted
			return PEPListinSubject;
		}
	  
	 //returns the alias in brackets or quotes
	  public static String getSender (String v_line, Integer v_messageID){
		  String temp = null;
		  Integer start =0,end = 0;
		 // System.out.println(v_line);
		  /*
		  System.out.println(v_line);	  
		  temp = v_line.replace("From: ",""); 
		  String [] arr = temp.split(" ", 2);
		  if (arr.length == 1)
			  temp = arr[0];
		  else
			  temp = arr[1];
		  temp = temp.replaceAll("[,.:;!?(){}\\[\\]<>%]", "");
		  */
		  if (v_line.contains("(")){	  
			  start = v_line.indexOf('(');
			  end = v_line.indexOf(')');
			  if (end < 0)
				  temp = v_line.substring(0, start+1);
			  else {
//				  System.out.println("v_line " + v_line + " start " + start +  " end " + end + " name= " + temp);
				  temp = v_line.substring(start+1, end);
			  }
		  }
		  else if (v_line.contains("\"")){  
			  start = v_line.indexOf('\"');
			  end = v_line.lastIndexOf('\"');	
			  if(v_messageID> 2129590 && v_messageID < 2129600)
				  System.out.println("v_line " + v_line + " start " + start +  " end " + end + " name= " + temp);
			  if (start<end)
				  temp = v_line.substring(start+1, end); 
		  }
		 // System.out.println(v_line + " start " + start +  " end " + end + " name= " + temp);	
		return temp;
	  }
	  
	  //It will start deleting all messageID after
	  public static Integer deleteHalfdone(Connection conn, String folder, String file) {
		  Integer messageID = null,minMessageID=null;
		  Connection dbConnection = conn;
		  PreparedStatement preparedStatement0 = null, preparedStatement1 = null;		  
		  String selectSQL = "select min(messageID) AS min from allmessages where folder like '%"+ folder+"%' and file like '%"+file+"%';";
		  try {			  
				Statement st = conn.createStatement();				ResultSet rs = st.executeQuery(selectSQL);				 
				// iterate through the java resultset
				if(rs.next())   {
					minMessageID = rs.getInt(1);
					System.out.println("Minumum messageID from last file found! " + minMessageID);
				} 
			  String deleteSQL = "DELETE from allMessages WHERE messageID > ?";
			  preparedStatement1 = dbConnection.prepareStatement(deleteSQL);			  
			  preparedStatement1.setInt(1, minMessageID-1);	//we want to delete the first one as well as we can start writing this again
			  
			  // execute delete SQL stetement
			  preparedStatement1.executeUpdate();
			  System.out.println("Half done file message records are deleted!");
			  preparedStatement1.close();
			  
		  } catch (SQLException e) {
			  System.out.println(e.getMessage());			  
		  } finally {
			  
		  }
		  return minMessageID;
	  }
	  //select last messageID inserted in database
	  static Integer selectMAX(Connection conn, String folder, String file) {
		  Integer messageID = null,maxMessageID=null;
		  Connection dbConnection = conn;
		  PreparedStatement preparedStatement0 = null, preparedStatement1 = null;		  
		  String selectSQL = "select MAX(messageID) AS max from allmessages;";
		  try {			  
				Statement st = conn.createStatement();				ResultSet rs = st.executeQuery(selectSQL);
				// iterate through the java resultset
				if(rs.next())   {
					maxMessageID = rs.getInt(1);
					//System.out.format("%s, %s\n", pepNumber, author);
				}
				else
					maxMessageID=0;
			  
		  } catch (SQLException e) {
			  System.out.println(e.getMessage());			  
		  } finally {			  
		  }
		  System.out.format("Selected MAX messageID " + maxMessageID);
		  return maxMessageID;
	  }
	  
	  
	  public static List<Integer> removeDuplicates(List<Integer> v_PEPList){
		  //Integer pep = null;
		  //boolean duplicateFound = false;
		  //		  System.out.println("Items in List "); 
		  //		  for (Integer item : v_PEPList) { 	    	
		  //		    	System.out.println(item); 
		  //			}
		  /*
		  for (Integer item : v_PEPList) { 
			  for (Integer item2 : v_PEPList) { 
				  System.out.println("Bolean value= " + duplicateFound + " checking item = " + item + " against item2 " + item2); 
				  if (item == item2) {      
			    	duplicateFound = true;    // If PEP is not in list, set boolean to true 
			    	System.out.println("duplicateFound = true - item2 " + item2);	
			    	v_PEPList.remove(item2);
			    	//v_PEPList.
				  }
				  System.out.println("Bolean value = " + duplicateFound );
			  }
			}
		   */
		  Set set = new HashSet(v_PEPList);
		  //create a new List from the Set
		  ArrayList uniqueList = new ArrayList(set);	  
		  //		  System.out.println("Unique Items in new Unique List "); 
		  //		  for (Integer item : v_PEPList) { 	    	
		  //		    	System.out.println(item); 
		  //			}
		  
		  //check in subject
		  // if the current pep number in subject , then add only those
		  //else keep on adding
		  
		  set.clear(); //clear set save memory..the proces from which this function is called is very memory intensive and reaches limit often
		  
		  return uniqueList;
	  }
	  
	  public static boolean matchQuery (String v_line, String v_Query){
		  Pattern p=Pattern.compile(v_Query);
		  Matcher m=p.matcher(v_line);
		  if (m.find()){
			  //	System.out.println(" matched v_searchKey " + v_Query);
			  return true;
		  }
		  return false;	  
	  }
	  
	  public static boolean stringContainsItemFromList(String inputString, String[] items) {
		  for (int i = 0; i < items.length; i++) {
			  if (inputString.contains(items[i])) {
				  return true;
			  }
		  }
		  return false;
	  }
	  
	  public static boolean TitleApprximateMatchInSentence(String title, String sentence) {
		  //			System.out.println("title= " + title);
		  //			System.out.println("sentence= " + sentence);
		  //approach a
		  title= title.trim();
		  sentence = sentence.trim();
		  //remove  '
		  sentence= sentence.replaceAll("'","");
		  sentence= sentence.replaceAll(",","");
		  //for all terms in sentence
		  Integer termFoundCounter=0, termFCounter=0;
		  if (sentence!=null ){
			  //				System.out.print("Found ");
			  //for (String sentenceTerm: sentence.split("\\s+")){
			  for (String titleTerm: title.split(" ")){					
				  if (sentence.contains(titleTerm.trim())){
					  termFoundCounter++;
					  //							System.out.print("[" + titleTerm+"] ");
				  }
			  }
			  //find intersection
			  Splitter splitter = Splitter.onPattern("\\W").trimResults().omitEmptyStrings();
			  Set<String> intersection = Sets.intersection(//
			          Sets.newHashSet(splitter.split(title)), //
			          Sets.newHashSet(splitter.split(sentence)));
			  termFCounter = intersection.size();
			 // System.out.println("intersection = " + intersection.toString());
			  
			  
			  //}
		  }
		  //			System.out.println();
		  //			System.out.println("termFoundCounter= " + (float) termFoundCounter);
		  //			System.out.println("title.length()= " + (float) title.split(" ").length);
		  float diff = 100* ( (float)termFoundCounter/ (float) title.split(" ").length);   
		  //			System.out.println("similarity= " + diff);
		  //System.out.println("similarity= " +  100* ( (float)termFoundCounter/ (float) title.split(" ").length)   );
		  
		  //return if 75% similar  or 3 or more terms matched
		  boolean percentage = diff >= (float) 80;
		  boolean termCounter = termFoundCounter>=3;
		  boolean iftermFCounter = termFCounter>=3;
		  if ( percentage || iftermFCounter){
			  System.out.println(" matched percentage " + percentage +" termFoundCounter " + termFoundCounter + " iftermFCounter " + iftermFCounter  + " title, " + title + " sentence " +sentence);
			  return true;
		  }
		  else{ 
			  return false;
		  }
		  //return diff;
	  }
	  
	  
	  
	  public static void initPepNumberSearchKeyLists(List<String> statusList,List<String> ignoreList, List<String> pepNumberSearchKeyList){
		  statusList.add("Draft");	
		  statusList.add("Open");		
		  statusList.add("Active");
		  statusList.add("Pending");
		  statusList.add("Closed");
		  statusList.add("Final");
		  statusList.add("Accepted");
		  statusList.add("Deferred");
		  statusList.add("Replaced");
		  statusList.add("Rejected");
		  statusList.add("Postponed");
		  statusList.add("Incomplete"); 
		  statusList.add("Superseded"); 
		  
		  String regex = "\\d+";  //match digits
		  
		  //pep numbers
		  pepNumberSearchKeyList.clear();
		  pepNumberSearchKeyList.add("PEP" + ": " + regex);
		  pepNumberSearchKeyList.add("PEP" + regex + " ");
		  pepNumberSearchKeyList.add("PEP " + regex + " ");
		  pepNumberSearchKeyList.add("PEP " + regex + "\\?");
		  pepNumberSearchKeyList.add("PEP " + regex + "\\."); 
		  pepNumberSearchKeyList.add("PEP " + regex + "\\,"); 
		  pepNumberSearchKeyList.add("PEP " + regex + "\\;");
		  pepNumberSearchKeyList.add("PEP " + regex + "\\:");
		  pepNumberSearchKeyList.add("PEP " + regex + "\\[");	  
		  pepNumberSearchKeyList.add("pep-" + regex + ".txt");
		  
		  ignoreList.clear();
		  ignoreList.add("PEP: " + regex + ".");
		  ignoreList.add("PEP: " + regex + "..");
		  ignoreList.add("PEP: " + regex + "...");	
		  
	  }
	  
	  
	  public static void initWordsList(List<String> wordsList){
		  wordsList.add("BDFL");
		  wordsList.add("vote");		
		  wordsList.add("voting");		
		  wordsList.add("poll");
		  wordsList.add("consensus");
		  wordsList.add("survey");
		  wordsList.add("offline");
		  wordsList.add("twitter");
		  wordsList.add("plus.google.com");
		  /* statusList.add("Replaced");
			  statusList.add("Rejected");
			  statusList.add("Postponed");
			  statusList.add("Incomplete"); 
			  statusList.add("Superseded");
		   */ 	  		
	  }
	  
	 
	  
	  public static void emptyDatabase(Connection conn){
		  
		  Statement stmt = null;
		  try{
			  //STEP 2: Register JDBC driver
//			  Class.forName("com.mysql.jdbc.Driver");
//			  conn = DriverManager.getConnection(DB_URL,USER,PASS);
			  stmt = conn.createStatement();
			  String sqlA;
			  
			  
			  // the mysql insert statement
			  String query = " DELETE from allpeps_ideaspeptitles;";
			  
			  // create the mysql insert preparedstatement
			  PreparedStatement preparedStmt = conn.prepareStatement(query);
			  //preparedStmt.setInt (1, v_id);
			  
			  preparedStmt.execute();		
			  stmt.close();
			  //conn.close();
		  }catch(SQLException se){
			  //Handle errors for JDBC
			  se.printStackTrace();
		  }catch(Exception e){
			  //Handle errors for Class.forName
			  e.printStackTrace();
		  }finally{
			  //finally block used to close resources
			  try{
				  if(stmt!=null)
					  stmt.close();
			  }catch(SQLException se2){
			  }// nothing we can do
			  
		  }//end try	     
	  }
	  

}
