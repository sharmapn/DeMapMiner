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

import connections.MysqlConnect;

public class MessageWritingUtils {
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
	
	
	public static void storeInDatabase(String tableName, int v_id, Date v_dateC, Date dateTimeStamp, String v_subject, File v_rootfolder, String v_file, String v_line, String v_emailMessage, int i, String V_status, 
			String V_statusFrom, String V_statusTo, String V_type, String V_author, String v_senderName, String v_inReplyTo, String v_references, String v_emailMessageId, 
			String v_link, Boolean v_statusChanged, String v_wordsList, String v_analyseWords, Integer v_messageID, Boolean v_required, Connection conn){
		
		Statement stmt = null;
		try{
			//STEP 2: Register JDBC driver
			//			  Class.forName("com.mysql.jdbc.Driver");
			//			  conn = DriverManager.getConnection(DB_URL,USER,PASS);
			stmt = conn.createStatement();
			String sqlA;
			
			//convert ate to java.sql date
			java.util.Calendar cal = Calendar.getInstance();
			cal.setTime(v_dateC);  //v_dateC
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			java.sql.Date sqlDate = new java.sql.Date(cal.getTime().getTime()); // your sql date
			//    System.out.println("utilDate:" + v_dateC);
			//    System.out.println("sqlDate:" + sqlDate);
			
			Timestamp now = null;
			if (dateTimeStamp!=null){
				now = new Timestamp(dateTimeStamp.getTime());
			}
			
			//Can look up the peptype if we want here..but better still sepratey after reading in
			
			//the mysql insert statement																							//references,
			String query = " insert into "+tableName+"( date2,dateTimeStamp, subject, folder,file , line, email, PEP, peptype, "
					+ "author,senderName, inReplyTo,  emailMessageId, link,analyseWords, wordsList, messageID, required)"
					+ " values (?,?,?, ?,?, ?, ?,?, ?, ?,?,?,?,?,?,?,?,?)";
			//System.out.println("here a");
			
			//System.out.println("reply to " + v_inReplyTo + " ref = " + v_references);
			// create the mysql insert preparedstatement
			PreparedStatement preparedStmt = conn.prepareStatement(query);
			//preparedStmt.setInt (1, v_id);
			preparedStmt.setDate (1,sqlDate);//sqlDate
			preparedStmt.setTimestamp (2,now); //dateTimeStamp);//sqlDate
			preparedStmt.setString (3, v_subject);
			preparedStmt.setString (4, v_rootfolder.toString());
			preparedStmt.setString (5, v_file.toString());
			preparedStmt.setString  (6, v_line);
			preparedStmt.setString  (7,v_emailMessage);
			preparedStmt.setInt  (8, i);
			//preparedStmt.setString  (9,V_statusFrom);
			//preparedStmt.setString  (10,V_statusTo);
			preparedStmt.setString  (9,V_type);
			preparedStmt.setString  (10,V_author);
			preparedStmt.setString  (11,v_senderName);
			preparedStmt.setString  (12,v_inReplyTo);
			//preparedStmt.setString  (12,v_references);
			preparedStmt.setString  (13,v_emailMessageId);
			preparedStmt.setString  (14,v_link);
			//preparedStmt.setBoolean (15,v_statusChanged);
			preparedStmt.setString  (15,v_analyseWords);
			preparedStmt.setString  (16,v_wordsList);
			preparedStmt.setInt(17,v_messageID);  
			preparedStmt.setBoolean(18,v_required);
			//preparedStmt.setBoolean(21,true);
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
			ResultSet rs = s.executeQuery("SELECT id, date2, location, line, email FROM Results oredr by date ascending;");
			System.out.println("Database Output ");
			while (rs.next()) {
				System.out.println(rs.getString(1) + " , " + rs.getString(2)+ " , " + rs.getString(3) + " , " + rs.getString(4) + " , " + rs.getString(5));
			}
			s.close();
			//			  try{
			//				  if(conn!=null)
			//					  conn.close();
			//			  }catch(SQLException se){
			//				  se.printStackTrace();
			//			  }//end finally try
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws IOException {
		Map<Integer,String> pepDetails = new HashMap<Integer,String>();
		//pepDetails = getTitle(pepDetails,conn);
		
		for (Map.Entry<Integer, String> entry : pepDetails.entrySet())
		{
			System.out.println(entry.getKey() + " / " + entry.getValue());
		}
		
	}
	
	public static void storeInFile(Writer v_outputFile, Date v_dateC, File v_rootfolder, String v_file, String v_line, String v_emailMessage, int i, String V_status, String V_statusFrom, String V_statusTo, String v_type,String v_author, Integer messageID) {
		try {
			v_outputFile.write(messageID + " , " + v_dateC + " , " + v_rootfolder + " , " + v_file + " , " + v_line
					+ " , " + V_statusFrom + " , " + V_statusTo + " , " + v_type + " , " + v_author + " , " + v_emailMessage + " , " + i);
			v_outputFile.append('\n');
		} catch (IOException e) {
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
	//
}
