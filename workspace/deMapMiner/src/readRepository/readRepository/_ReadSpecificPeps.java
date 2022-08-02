package readRepository.readRepository;

import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.*;
import java.util.Date;
import java.sql.*;

import javax.swing.JOptionPane;

import connections.MysqlConnect;

public class _ReadSpecificPeps 
{
	 // JDBC driver name and database URL
  static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
  static final String DB_URL = "jdbc:mysql://localhost/peps";

    //  Database credentials
  static final String USER = "root";
  static final String PASS = "";
    
  public static File dev_folder = new File("C:/data/python-dev/");
  public static File patches_folder = new File("C:/data/python-patches/");
  public static File lists_folder = new File("C:/data/python-lists/");
  public static File bugs_list_folder = new File("C:/data/python-bugs-list/");
  public static File committers_folder = new File("C:/data/python-committers/");
  public static File checkins_folder = new File("C:/data/python-checkins/");
  public static File ideas_folder = new File("C:/data/python-ideas/");
  
  public static File dev_folder2 = new File("C:/data/dev/");
  static Connection conn = null;
  
  static String temp = "";
  //static String searchKey = "PEP 301 ";sss
  static String searchKeyAll = null;
  //static String searchKeyList = null;
  
  static List<String> searchKeyList = new ArrayList<String>();
  static List<String> ignoreList = new ArrayList<String>();
  static List<String> statusList = new ArrayList<String>();
   
  static int fileCounter = 0;                  //total number of files with searchKey 
 // static HashMap hm = new HashMap();
  
  static boolean foundInFile = false;
  
  static List<Integer> PEPList = new ArrayList<Integer>();

  public static void main(String[] args) throws IOException {
	  
	//  JOptionPane.showMessageDialog( null, "Hello World!" );
	  MysqlConnect mc = new MysqlConnect();
	  Connection conn = mc.connect();
	  emptyDatabase(conn);
	  init(searchKeyList);
	  	  
	// add 4 different values to list
	 // PEPList.add(405);
	 // PEPList.add(492);
	 // PEPList.add(263);
	  //PEPList.add(249);
	  PEPList.add(246);
	  /*
	  PEPList.add(263);
	  PEPList.add(3105);
	  PEPList.add(255);
	  PEPList.add(352);
	  PEPList.add(328);
	  PEPList.add(301);
	  PEPList.add(476);
	  PEPList.add(247); 	  
	*/  
	//  System.out.println("PEP 0 " + PEPList.get(0));
	  
//	  for (int i=435; i<436;i++){
	  int i = 0;	 
	   for (int j = 0; j<PEPList.size();j++){
//		   System.out.println("\nStarted  processing PEP " + i + " At time " + LocalDateTime.now());
		  System.out.println("PEP 0 " + PEPList.get(j));
		  i = PEPList.get(j);
		  System.out.println("\nStarted  processing PEP " + PEPList.get(j) + " At time " + LocalDateTime.now());
		  searchKeyAll = "PEP " + PEPList.get(0) + " ";
		
		  searchKeyList.clear();
		  searchKeyList.add("PEP" + ": " + i);
		  searchKeyList.add("PEP " + i + " ");
		  searchKeyList.add("PEP " + i + "\\?");
		  searchKeyList.add("PEP " + i + "\\."); 
		  searchKeyList.add("PEP " + i + "\\,"); 
		  searchKeyList.add("PEP " + i + "\\;");
		  searchKeyList.add("PEP " + i + "\\:");
		  searchKeyList.add("PEP " + i + "\\[");
		
		  ignoreList.clear();
		  ignoreList.add("PEP: " + i + ".");
		  ignoreList.add("PEP: " + i + "..");
		  ignoreList.add("PEP: " + i + "...");	
		
		
		//System.out.println(searchKeyAllA.get(1));
		
		File statText = new File("c:/data/output" + i + ".txt");		 
	        //output file declaration		
		    FileOutputStream is = new FileOutputStream(statText);
		    OutputStreamWriter osw = new OutputStreamWriter(is);    
		    Writer w = new BufferedWriter(osw);
		    
		//DEBUG File
	    File debugText = new File("c:/data/debug.txt");		 
        //output file declaration		
	    FileOutputStream debug = new FileOutputStream(debugText);
	    OutputStreamWriter debugosw = new OutputStreamWriter(debug);    
	    Writer wdebug = new BufferedWriter(debugosw);    
		   // Connection conn = null;
	    searchFilesForFolder(dev_folder2, dev_folder2, foundInFile, searchKeyAll,fileCounter, w, wdebug, conn , i, searchKeyList, ignoreList, statusList);
	    System.out.println("Finished processing ideas folder");
//	    searchFilesForFolder(dev_folder, dev_folder, foundInFile, searchKeyAll,fileCounter, w, wdebug, conn , i, searchKeyList, ignoreList, statusList);
	    System.out.println("Finished processing dev folder");
	//    searchFilesForFolder(patches_folder,patches_folder, foundInFile,  searchKeyAll,fileCounter, w ,wdebug,  conn, i, searchKeyList, ignoreList, statusList);
	//    System.out.println("Finished processing patches folder");
//	    searchFilesForFolder(lists_folder,lists_folder, foundInFile,  searchKeyAll,fileCounter, w ,wdebug,  conn, i, searchKeyList, ignoreList, statusList);
	    System.out.println("Finished processing lists folder");
	//    searchFilesForFolder(bugs_list_folder,bugs_list_folder, foundInFile,  searchKeyAll,fileCounter, w ,wdebug,  conn, i, searchKeyList, ignoreList, statusList);
	//    System.out.println("Finished processing bugs lists folder");
//	    searchFilesForFolder(committers_folder,committers_folder , foundInFile,  searchKeyAll,fileCounter, w , wdebug, conn, i, searchKeyList, ignoreList, statusList);
	    System.out.println("Finished processing committers folder");
//	    searchFilesForFolder(checkins_folder,committers_folder , foundInFile,  searchKeyAll,fileCounter, w , wdebug, conn, i, searchKeyList, ignoreList, statusList);
//	    System.out.println("Finished processing checkins folder");
	    //showSortedHashMap(hm);
	    // search  folder
	    w.close();
	   // outputResults(conn);
	  }
    //conn.close();
	  System.out.println("Finished processing");
  }
  
  public static void searchFilesForFolder(final File folder, final File v_rootfolder, boolean v_foundInFile, String v_searchKey, int v_fileCounter, Writer v_outputFile, Writer v_debugFile, Connection v_conn, int i, List<String> v_searchKeyList, List<String> v_ignoreList , List<String> v_statusList) throws IOException 
  {

	File rootFolder = v_rootfolder;
	for (final File fileEntry : folder.listFiles()) 
    {
      if (fileEntry.isDirectory()) {
        searchFilesForFolder(fileEntry, v_rootfolder, v_foundInFile, v_searchKey, v_fileCounter, v_outputFile, v_debugFile, v_conn, i, v_searchKeyList, v_ignoreList, v_statusList);
      } 
      else 
      {        
    	  if (fileEntry.isFile()) {
          temp = fileEntry.getName();
          
          //start match          
          BufferedReader br=new BufferedReader(new FileReader(folder +  "\\" + fileEntry.getName()));
          String line;
          int counter =0;          
          String From = null;         
          String emailMessage = null;
          Boolean matched = false;
          String location = null;
          String lastLine =null;          
          String date2 = null;  
          String status = null;
          String statusLine = null;
          String type = null;
          String author = null;
          Boolean StatusChanged = false;
          
          int withinFileCounter = 0;
          while((line=br.readLine())!=null)
          {      	  
    	      if (line.startsWith("Date: "))   date2 = line.replace("Date:","");
    	      if (line.startsWith("Type: "))   type = line.replace("Type: ","");
    		  if (line.startsWith("Author: ")) author = line.replace("Author: ",""); 
//    		  if ((line.startsWith("Status: ")) || (line.startsWith("\\+Status: "))) {
    		  if ((matchQuery(line, "Status:")) || (matchQuery(line, "\\+Status:"))){
    		 // if (matchQuery(line, "\\+Status:")){
    			  
    //			  if(matchQuery(line, "489")){
    //            	  System.out.println("here " + line);	    	                 
    //              }
    			  
    			  
   // 			  System.out.println("\n Status, line = " + line);
	    			  statusLine = line.replace("Status: ","");
	    			  statusLine = line.replace(">>","");
	    			  statusLine = line.replace(">","");
	    			  statusLine = line.replace("+","");
	    			  statusLine = line.replace("-","");
	    			   Matcher mA = null;
	    			   for (String patternmA : v_statusList){
	    			//	   System.out.println("line = " + line + " pattern testing= " + patternmA);
	    	                  Pattern pA = Pattern.compile(patternmA);
	    	                  mA = pA.matcher(line);  
	    	                
	    	            	  if (mA.find()) //  while (m.find() || m9.find()) 
	    	            	  {
	    	            		  status = patternmA;
	    	            		  StatusChanged = true;
//	    	            		  System.out.println("Status " + status + " StatusChanged " + statusLine);
	    	            	  }
	    			   }
    		  }        		  
    		  //add here
    		  if (line.startsWith("From ")) {
        		  if (matched == true){        			  		
        			 Date date9 = null; 	        			         			 
 			  		  date9= findDate(date2);	        			 
        			  counter++;
        			  if (date9 != null) {
        				  storeInDatabase(counter, date9, rootFolder , fileEntry.getName(),lastLine, emailMessage, i, status, type, author, StatusChanged, conn);
        				  storeInFile(v_outputFile, date9, rootFolder, fileEntry.getName(),lastLine, emailMessage, i,status, type, author);	
 //       				  System.out.println("Status " + status + " StatusChanged " + statusLine);
        			  }
        			  else{
        				  System.out.println("DATE NULL date2 " + date2 + date9 + " , " + " i " + i + rootFolder + fileEntry.getName() + " , " + line + " ");
        			  }
        			  emailMessage = null; //empty message once matched is true and from is found
        		  }
        		  else 
        			  emailMessage = null; //empty message also when from is found but not matched - meaning new message has started and searchkey was not found in last message
	    		  matched = false;         //new emailMessage therefore set matched to null
	    		  StatusChanged = false;
	    		  status = null;
	    		  From = line;   		 
        	  }
        	  else 
        		  if (matched == true) ///if searchkey found in last round but end of message not reached, i.e from, then keep on adding lines, only write to db once next From is found
        			  emailMessage = emailMessage + line + "\n";

	          Matcher m5 = null;
        	  for (String pattern : v_searchKeyList){
                  Pattern p5 = Pattern.compile(pattern);
                  m5 = p5.matcher(line);  
            	  while (m5.find()) //  while (m.find() || m9.find()) 
            	  {
            		  boolean Store = true;            			  
            		  	  Matcher m5a = null;            		  	  
            		  	  for (String pattern2a : v_ignoreList){               //so many combinations we cant store e.g. PEP 312, if we looking for PEP: 3 only
            		  		  Pattern p2a = Pattern.compile(pattern2a);
            		  		  m5a = p2a.matcher(line);  
            		  		  while (m5a.find()) //  while (m.find() || m9.find()) 
            		  		  {
            		  			Store = false;
//            		  			System.out.println("dont store pattern " + pattern + " line " + line);
            	  			  }
            		  	  }   
            		  	  
            		  //end added
            		      if (Store == true){           		      
		            		  withinFileCounter++;
		            		  foundInFile = true;  
		            		  matched = true;                   //if searchkey found, set matched to true
		            		  lastLine = line; 
		            		  location = folder + "\\" + fileEntry.getName();            		  
		            		  emailMessage = emailMessage + " " + line + "\n";	
//		           		      System.out.println("store pattern " + pattern + " line " + line);
		            		  String de = "pattern " + pattern + " line" + line;
		            		  storeInDebugFile(v_debugFile, de );
           		      }	  
            	   } //end while            	  
                } //end for
        	  if (matched == false)
        		  emailMessage = emailMessage + " " + line + "\n";    //for lines not having from and if not matched, keep on adding lines to emailmessage
          }
          // end match
        } //end if
    	if (foundInFile = true);
		  v_fileCounter++;
      }   //end else
      
    }     //end for
	
	
  }       //end method
  
  public static boolean matchQuery (String v_line, String v_Query){
	  Pattern p=Pattern.compile(v_Query);
	  Matcher m=p.matcher(v_line);
	  if (m.find()){
	//	System.out.println(" matched v_searchKey " + v_Query);
		  return true;
	  }
	return false;	  
  }
  
  public static boolean stringContainsItemFromList(String inputString, String[] items)
  {
      for(int i =0; i < items.length; i++)
      {
          if(inputString.contains(items[i]))
          {
              return true;
          }
      }
      return false;
  }
  
  public static void init(List<String> v_searchKeyList){
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
  }
  
  public static Date findDate(String v_line)  { 
	  
	  String[] patterns = {//"EEE, dd MMM yyyy hh:mm:ss UTC",
	            "yyyy.MM.dd G 'at' HH:mm:ss z",
	            "EEE, MMM d, ''yy",
	            "yyyyy.MMMMM.dd GGG hh:mm aaa",
	            "EEE, d MMM yyyy HH:mm:ss Z",
	            "yyMMddHHmmssZ",
	            "d MMM yyyy HH:mm:ss z",
	            "yyyy-MM-dd'T'HH:mm:ss",
	            "yyyy-MM-dd HH:mm",   //added
	            "yyyy/MM/dd HH:mm:ss",   //added
	            "yyyy-MM-dd'T'HH:mm:ss'Z'",
	            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
	            "yyyy-MM-dd'T'HH:mm:ssZ",
	            "yyyy-MM-dd'T'HH:mm:ss.SSSZ",
	            "yyyy-MM-dd'T'HH:mm:ssz",
	            "yyyy-MM-dd'T'HH:mm:ss.SSSz",
	            "EEE, d MMM yy HH:mm:ssz",
	            "EEE, d MMM yy HH:mm:ss",             
	            "EEE, d MMM yy HH:mm z",
	            "EEE, d MMM yy HH:mm Z",
	            "EEE MMM  d HH:mm:ss yyyy",  //added
	            "EEE MMM dd HH:mm:ss yyyy",  //added
	            "EEE, dd MMM yyyy HH:mm:ss z",  //added
	            "EEE, dd MMM yyyy HH:mm:ss zzzz",  //added
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
//	  Pattern p0=Pattern.compile("Date:");
//	  Matcher m0=p0.matcher(v_line);
//	  if (m0.find()){
//		  dateC = v_line.replace("Date:","");
//		  dateC = v_line.replace("Date:","");
		  if (dateC.length() >30)
			  dateC = dateC.substring(0, 31);
		  dateC = dateC.substring(1, dateC.length());
		  
		  
		  
		//  System.out.println("dateC " + dateC);
		  for (int i = 0; i < patterns.length; i++) {
		        SimpleDateFormat sdf = new SimpleDateFormat(patterns[i], Locale.US);
		        try {
		            pubdate = sdf.parse(dateC);
		          //  System.out.println("hello " + pubdate);
	
		            break;
		        } catch (Exception e) {
//		        	System.out.println("hello " + e.getMessage());
		        }
		    }
		//  System.out.println("pubdate " + pubdate);
		  
	 // }
	 // System.out.println("pubdate " + pubdate);
	  return pubdate;
  }
  
  public static void storeInFile(Writer v_outputFile, Date v_dateC, File v_rootfolder, String v_file,  String v_line, String v_emailMessage, int i, String v_status, String v_type, String v_author){
	  try {
		        v_outputFile.write(v_dateC + " , " +  v_rootfolder + " , " + v_file + " , " + v_line + " , " + v_status + " , " + v_type + " , " + v_author + " , " + v_emailMessage + " , " + i);			  
		   v_outputFile.append('\n');
		  }
		  catch (IOException e) {
	            System.err.println("Problem writing to the data file");
	      } 
  }
  
  public static void storeInDebugFile(Writer v_debugFile, String debug){
	  try {
		        v_debugFile.write(debug);			  
		   v_debugFile.append('\n');
		  }
		  catch (IOException e) {
	            System.err.println("Problem writing to the file debug file");
	      } 
  }
  
  public static void storeInDatabase(int v_id, Date v_dateC, File v_rootfolder, String v_file, String v_line, String v_emailMessage, int i, String V_status, String V_type, 
		  String V_author, Boolean v_statusChanged, Connection conn){
	  
	     Statement stmt = null;
	     try{
	        //STEP 2: Register JDBC driver
//	        Class.forName("com.mysql.jdbc.Driver");
//	        conn = DriverManager.getConnection(DB_URL,USER,PASS);
//	        stmt = conn.createStatement();
	        String sqlA;
	        
	        //convert ate to java.sql date
	        java.util.Calendar cal = Calendar.getInstance();
	        cal.setTime(v_dateC);
		    cal.set(Calendar.HOUR_OF_DAY, 0);
		    cal.set(Calendar.MINUTE, 0);
		    cal.set(Calendar.SECOND, 0);
		    cal.set(Calendar.MILLISECOND, 0);
		    java.sql.Date sqlDate = new java.sql.Date(cal.getTime().getTime()); // your sql date
		//    System.out.println("utilDate:" + v_dateC);
		//    System.out.println("sqlDate:" + sqlDate);
	        
		    //add newline character
		    v_line = v_line  + "\n";
		   // System.out.println("v_line" + v_line);
		    
    		// the mysql insert statement
    	      String query = " insert into specificpeps ( date2, folder,file , line, email, PEP, statusTo,type, author, StatusChanged )"
    	        + " values (?, ?,?, ?, ?, ?, ?, ?,?,?)";
    	 
    	      // create the mysql insert preparedstatement
    	      PreparedStatement preparedStmt = conn.prepareStatement(query);
    	      //preparedStmt.setInt (1, v_id);
    	      preparedStmt.setDate (1,sqlDate);
    	      preparedStmt.setString (2, v_rootfolder.toString());
    	      preparedStmt.setString (3, v_file.toString());
    	      preparedStmt.setString  (4, v_line);
    	      preparedStmt.setString  (5,v_emailMessage);
    	      preparedStmt.setInt  (6, i);
    	      preparedStmt.setString  (7,V_status);
    	      preparedStmt.setString  (8,V_type);
    	      preparedStmt.setString  (9,V_author);
    	      preparedStmt.setBoolean (10,v_statusChanged);
    	      preparedStmt.execute();		
              stmt.close();
	       // conn.close();
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
  
  public static void emptyDatabase(Connection conn){
	  
	     Statement stmt = null;
	     try{
	        //STEP 2: Register JDBC driver
//	        Class.forName("com.mysql.jdbc.Driver");
//	        conn = DriverManager.getConnection(DB_URL,USER,PASS);
//	        stmt = conn.createStatement();
	        String sqlA;
	        
		    
		// the mysql insert statement
	      String query = " DELETE from specificpeps;";
	 
	      // create the mysql insert preparedstatement
	      PreparedStatement preparedStmt = conn.prepareStatement(query);
	      //preparedStmt.setInt (1, v_id);

	      preparedStmt.execute();		
        stmt.close();
	       // conn.close();
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
  
  public void OpenFileForOutput() throws IOException
  {
     //   String text = v_output;
        BufferedWriter output = null;
        try {
            File file = new File("c:\\data\\output.txt");
            output = new BufferedWriter(new FileWriter(file));
 //           output.write(text);
        } 
        catch ( IOException e ) {
            e.printStackTrace();
        } 
        finally {
            if ( output != null ) 
            	output.close();
        }		
  }  
  
 public static void  outputResults (Connection v_conn){
		try {
			Statement s = v_conn.createStatement();
			ResultSet rs = s
					.executeQuery("SELECT id, date2, location, line, email FROM Results oredr by date ascending;");
			System.out.println("Database Output ");
			while (rs.next()) {
				System.out.println(rs.getString(1) + " , " + rs.getString(2) + " , " + rs.getString(3) + " , "
						+ rs.getString(4) + " , " + rs.getString(5));
			}
			s.close();
//			try {
//				if (conn != null)
//					conn.close();
//			} catch (SQLException se) {
//				se.printStackTrace();
//			} // end finally try
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
  
}		  //end class