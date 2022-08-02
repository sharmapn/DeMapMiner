package readRepository.postReadingUpdates;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import readRepository.readRepository.MessageReadingUtils;
import connections.MysqlConnect;
import miner.process.PythonSpecificMessageProcessing;
import utilities.GetDate;
import utilities.ReturnNattyTimeStamp;

//april 2021..We do this for BIPs as well. As you can see from the row below
// "2015-07-22 12:52:20" |	"From pieter.wuille at gmail.com  Wed Jul 22 16:52:20 2015

//nov 2017 ..this is main script 

//the timestamp has been read in and assigned for 95% of messages while reading flat files, but for for all message records
//This is main program which is going to do for all pep messages

//there are two dates, one in from line and one in date line
//we use the date from date line and have used pattern matching while reading in repository

//But pattern matching has not been able to do correct for all records, and then in those cases, we use natty time
//now while updating we can use the same 

public class TryTimestampUpdate {
	//query each message
	//compute timestamp
	//store in same message

	static MessageReadingUtils mru = new MessageReadingUtils();
	static PythonSpecificMessageProcessing pms = new PythonSpecificMessageProcessing();	
	static MysqlConnect mc = new MysqlConnect();	
	static String tableName = "allmessages";
	static boolean updateTable = true;
	
	static GetDate gd = new GetDate();  //new date parser centrally located in seprate package for use from all other package code

	public static void main(String args[])
	{	
		ReturnNattyTimeStamp rts = new ReturnNattyTimeStamp();
		Date dateTimeStamp = null, date9=null;
		String date2=null;
		try
		{
			Connection conn = mc.connect();			
			//divide into one pep at a time to give the machine less recordsets to work with at a time
			//April 2021
			String sql0 = "SELECT bip, messageID,email from "+tableName+";";// where messageid = 9239;"; //messageID = '8407' order by pep ;";  
			//String sql0 = "SELECT pep, messageID,email from "+tableName+" where datetimestamp IS NULL limit 5;"; //messageID = '8407' order by pep ;";  
			Statement stmt0 = conn.createStatement();
			ResultSet rs0 = stmt0.executeQuery( sql0 );			
			//  Date: Thu Apr  1 00:28:01 2004	
			System.out.format("%10s%40s%40s%40s%40s", "mid","originalDate", "newdate","NattyDate", "PatternMatchingDate");
			System.out.println();
			while (rs0.next())    
			{			
				int pepNumber = rs0.getInt("bip"); //pep
				Integer mid = rs0.getInt("messageID");
				String email = rs0.getString("email");

				//mid < 18500 || 
				if(mid==18581 || mid == 18584 || mid==18620) //problematic messages
					continue;
				
				String lines[] = email.split("\\n");
				for (String line : lines)	{					
					if (line.contains("Date: ")){							
						date2 = line.replace("Date:", "");	
						date2= date2.trim();
						
//						if(date2.endsWith("("))
//							date2 = date2.replace("(", "");
						
						//get last 4 digits of year and put it just before time 
						String substring = date2.substring(0, date2.length()-4);
						String yearSubstring = date2.substring(date2.length()-4, date2.length());
						//							System.out.println("substringe:   "+ substring);
						//							System.out.println("year substringe:   "+ yearSubstring);

						date2 = date2.trim();
						substring = substring.trim();
						String firstPart = substring.substring(0, substring.lastIndexOf(" "));							
						String lastPart = substring.substring(substring.lastIndexOf(" "), substring.length());							
						String newDate = firstPart.trim() + " " + yearSubstring + " " + lastPart.trim(); 
						newDate = newDate.trim();
//**						System.out.println("New date:   "+ newDate);

						//Natty Result
						dateTimeStamp = rts.returnNattyDate(date2.trim()); //newDate.trim());
						//*						System.out.println("kk Original date:   "+ newDate);
						//*						System.out.println("kk Natty Result dateTime " + dateTimeStamp);	

						//Pattern Matching 
						date9 = gd.findDate(date2);	//returns null							
						//*						System.out.println("Original date:   "+ date2);
						//*						System.out.println("Pattern Matching Result dateTime " + date9);

						System.out.format("%10s%40s%40s%40s%40s",mid, date2, newDate,dateTimeStamp, date9);
						System.out.println();
						//**							updateTableWithDateTimeStamp(conn, mid, newTimeStamp);

						//to see why the datetimestamp column is empty for some records we want to update
						if(updateTable) {
							updateTableDateTimeStampColumn(conn, mid, date9);
							//we try insert natty time instead of pattern matching
							updateTableWithDateTimeStamp(conn, mid, dateTimeStamp);
						}
						if(date9==null)	//if pattern matching time is null, we use natty time
							
							
						

						break;   //break on first find
					}
				}					
			}				
			stmt0.close();
		}   catch (Exception e)	    {
			System.err.println("Got an exception! ");
			System.err.println(e.getMessage());
			System.out.println(StackTraceToString(e));
		}


	}
	private static void updateTableWithDateTimeStamp(Connection conn, Integer mid, Date nattyDateTime)
			throws SQLException {
		
		//from natty time to datetimestamp
		Timestamp now = null;
		if (nattyDateTime!=null){
			now = new Timestamp(nattyDateTime.getTime());
		}
		
		String updateQuery = "update "+tableName+" set dateTimeStamp = ? where messageID = ?";
		PreparedStatement preparedStmt = conn.prepareStatement(updateQuery);
		preparedStmt.setTimestamp (1, now);
		preparedStmt.setInt(2, mid);
		int i = preparedStmt.executeUpdate();
		if(i>0)   {
			//System.out.println("success");
		}
		else  {
			System.out.println("stuck somewhere mid " + mid + " newTimeStamp " + now);
		}
	}
	private static void updateTableDateTimeStampColumn(Connection conn, Integer mid, Date date9) throws SQLException {

		Timestamp now = null;
		if (date9!=null){
			now = new Timestamp(date9.getTime());
		}
		
		String updateQuery = "update "+tableName+"  set dateTimeStampString = ? where messageID = ?";
		PreparedStatement preparedStmt = conn.prepareStatement(updateQuery);
		preparedStmt.setTimestamp (1, now);
		preparedStmt.setInt(2, mid);
		int i = preparedStmt.executeUpdate();
		if(i>0)   {
			//System.out.println("success");
		}
		else  {
			System.out.println("stuck somewhere mid " + mid + " newTimeStamp " + date9);
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
}
