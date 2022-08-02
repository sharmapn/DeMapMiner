package readRepository.readRepository;

import java.io.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.*;
import java.util.Date;
import java.sql.*;

public class _ReadStringsFromFolders 
{
	 // JDBC driver name and database URL
  static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
  static final String DB_URL = "jdbc:mysql://localhost/test";

    //  Database credentials
  static final String USER = "root";
  static final String PASS = "";
    
  public static File dev_folder = new File("C:/data/python-dev/");
  public static File patches_folder = new File("C:/data/python-patches/");
  public static File lists_folder = new File("C:/data/python-lists/");
  public static File bugs_list_folder = new File("C:/data/python-bugs-list/");
  public static File committers_folder = new File("C:/data/python-committers/");
  public static File checkins_folder = new File("C:/data/python-checkins/");
  
  public static File dev_folder2 = new File("C:/data/dev/");
  
  static String temp = "";
  //static String searchKey = "PEP 301 ";
  static String searchKeyAll = null;
  
  static int fileCounter = 0;                  //total number of files with searchKey 
 // static HashMap hm = new HashMap();
  
  static boolean foundInFile = false;

  public static void main(String[] args) throws IOException {
	  for (int i=461; i<463;i++){
		//searchKeyAll = "PEP " + i + " ";
		  searchKeyAll = "Status: Final";
		File statText = new File("c:/data/output" + i + ".txt");		
		// search dev folder
//	    System.out.println("Reading files under the folder "+ dev_folder.getAbsolutePath());
	   // OpenFileForOutput();	    
	        //output file declaration		
		    FileOutputStream is = new FileOutputStream(statText);
		    OutputStreamWriter osw = new OutputStreamWriter(is);    
		    Writer w = new BufferedWriter(osw);
		   // Connection conn = null;
	    searchFilesForFolder(dev_folder, foundInFile, searchKeyAll,fileCounter, w );
	    searchFilesForFolder(patches_folder, foundInFile,  searchKeyAll,fileCounter, w );
	    searchFilesForFolder(lists_folder, foundInFile,  searchKeyAll,fileCounter, w );
	    searchFilesForFolder(bugs_list_folder, foundInFile,  searchKeyAll,fileCounter, w );
	    searchFilesForFolder(committers_folder, foundInFile,  searchKeyAll,fileCounter, w );
	    searchFilesForFolder(checkins_folder, foundInFile,  searchKeyAll,fileCounter, w );
	    //showSortedHashMap(hm);
	    // search  folder
	    w.close();
	 //   outputResults(conn);
		
	  }
   // conn.close();
  }
  
  public static void searchFilesForFolder(final File folder, boolean v_foundInFile, String v_searchKey, int v_fileCounter, Writer v_outputFile) throws IOException 
  {
	 // System.out.println("checking folder " + folder );
	for (final File fileEntry : folder.listFiles()) 
    {
      if (fileEntry.isDirectory()) {
        // System.out.println("Reading files under the folder "+folder.getAbsolutePath());
        searchFilesForFolder(fileEntry, v_foundInFile, v_searchKey, v_fileCounter, v_outputFile);
      } 
      else 
      {
        
    	if (fileEntry.isFile()) {
          temp = fileEntry.getName();
 //         if ((temp.substring(temp.lastIndexOf('.') + 1, temp.length()).toLowerCase()).equals("txt"))
           // System.out.println("File= " + folder.getAbsolutePath()+ "\\" + fileEntry.getName());
 //       	  System.out.println(fileEntry.getName());
          
          //start match          
          BufferedReader br=new BufferedReader(new FileReader(folder +  "\\" + fileEntry.getName()));
          String line;
          String folderName;
          String date = "k";
          int index1 = 0;
          int index2 =0;
          
          String From = null;
          String dateC = null;
          String messageId;
          String emailMessage = null;
          Boolean matched = false;
          String location = null;
          String lastLine =null;
          String PEP = null;
          
          int withinFileCounter = 0;
          while((line=br.readLine())!=null)
          {
        	  
        	  //check the detail here
        	  // if the keyword matches then these details are required
        	  //date pattern
        	  
        	  Pattern p7=Pattern.compile("PEP ");
        	  Matcher m7=p7.matcher(line);
        	  if (m7.find()) {
        		  PEP = line;
        	  }
        	  dateC= findDate(dateC, line);
        	  Pattern p2=Pattern.compile("From ");
        	  Matcher m2=p2.matcher(line);
        	  if (m2.find()) {
        		  if (matched == true){
 //       			  storeInDatabase(dateC, location ,lastLine, emailMessage);
 //       			  storeInFile(v_outputFile, dateC, location ,line);	
  //      			  System.out.println(dateC +  fileEntry.getName() + " , " + line + " ");
        			  emailMessage = null;
        		  }
	    		  matched = false;
	    		  From = line;
	    		  emailMessage = emailMessage + line;	    		 
        	  }
        	  else 
        		  if (matched == true)
        			  emailMessage = emailMessage + line;
        	 // System.out.println("emailMessage " + emailMessage);
        	  Pattern p=Pattern.compile(v_searchKey);
        	  Matcher m=p.matcher(line);
        	  int count=0;
        	  
        	  while (m.find()) 
        	  {
        		  count++;
        		  withinFileCounter++;
        		  foundInFile = true;
        		  matched = true;
        		  
        		  //Get more details
        		  //end getting details
        		  lastLine = line;
        		  //System.out.println(date + " "+  v_fileCounter + " " + withinFileCounter + " "  + fileEntry.getName() + folder +  "\\" + fileEntry.getName() +" " + line);
 //       		  System.out.println(dateC +  folder + "\\" + fileEntry.getName() + " , " + line);
        		  System.out.println("PEP " + PEP + " Date " + dateC + " " + folder + " " + fileEntry.getName() + " , " + line + " ");
        		  location = folder + "\\" + fileEntry.getName();
///        		  storeInFile(v_outputFile, dateC, location ,line);		  //write to file
 //       		  storeInDatabase(dateC, location ,line, emailMessage);                 //store in database
        		  //m.replaceAll("abc");
        	  }          
          }
          // end match
        } //end if
    	if (foundInFile = true);
		  v_fileCounter++;
      }   //end else
      
    }     //end for
	
	
  }       //end method
  
  public static String findDate(String dateC, String v_line) {
	  Pattern p0=Pattern.compile("Date:");
	  Matcher m0=p0.matcher(v_line);
	  if (m0.find())
		  dateC = v_line;
	return dateC;
  }
  
  public static void storeInFile(Writer v_outputFile, String v_dateC, String v_location, String v_line){
	  try {
		  // v_outputFile.write(v_fileCounter + " " + withinFileCounter + " " + folder +  "\\" + fileEntry.getName() +" " + line);
			 // v_outputFile.write(dateC + " , " + s4 + " , " + folder + " , " + folder +  "\\" + fileEntry.getName() + " , " + line);
			  //v_outputFile.write(v_dateC + " , " + folder +  "\\" + fileEntry.getName() + " , " + line);
		        v_outputFile.write(v_dateC + " , " +  v_location + " , " + v_line);			  
		   v_outputFile.append('\n');
		  }
		  catch (IOException e) {
	            System.err.println("Problem writing to the file statsTest.txt");
	      } 
  }
  
  public static void storeInDatabase(String v_dateC, String v_location, String v_line, String v_emailMessage, Connection conn){
	//  Connection conn = null;
	     Statement stmt = null;
	     try{
	        //STEP 2: Register JDBC driver
	  //      Class.forName("com.mysql.jdbc.Driver");

	        //STEP 3: Open a connection
	//        System.out.println("Connecting to database...");
	//        conn = DriverManager.getConnection(DB_URL,USER,PASS);

	        //STEP 4: Execute a query
	//        System.out.println("Creating statement...");
	        stmt = conn.createStatement();
	        String sqlA;
	        	        		
    		// the mysql insert statement
    	      String query = " insert into results (date, location, line, email)"
    	        + " values (?, ?, ?, ?)";
    	 
    	      // create the mysql insert preparedstatement
    	      PreparedStatement preparedStmt = conn.prepareStatement(query);
    	      preparedStmt.setString (1, v_dateC);
    	      preparedStmt.setString (2, v_location);
    	      preparedStmt.setString  (3, v_line);
    	      preparedStmt.setString  (4,v_emailMessage);
    	     // preparedStmt.setBoolean(4, false);
    	     // preparedStmt.setInt    (5, 5000);
    	 
    	      // execute the preparedstatement
    	      preparedStmt.execute();		
	       /* 
	        String sqlB;
	        sqlB = "SELECT date, location, line FROM results";
	        ResultSet rs = stmt.executeQuery(sqlB);

	        //STEP 5: Extract data from result set
	        while(rs.next()){
	           //Retrieve by column name
	           //int id  = rs.getInt("id");
	           String location = rs.getString("location");
	           String date = rs.getString("date");
	           String line = rs.getString("line");

	           //Display values
	           //System.out.print("ID: " + id);
	           System.out.print(", location: " + location);
	           System.out.print(", date: " + date);
	           System.out.println(", line: " + line);
	        }
	        
	        
	        //STEP 6: Clean-up environment
	        rs.close();
	        
	        */
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
	        try{
	           //if(conn!=null) {
	        	//   ;
	           //}
	             // conn.close();
	        }catch(Exception se){
	           se.printStackTrace();
	        }//end finally try
	     }//end try
	//     System.out.println("Goodbye!");
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
	    	  ResultSet rs = s.executeQuery("SELECT [lineout] FROM [Results]");
	    	  while (rs.next()) {
	    	//    System.out.println(rs.getString(1));
	    	  }
          s.close();
	  }
	  catch(Exception ex)
      {
          ex.printStackTrace();
      }
  }
  
}		  //end class