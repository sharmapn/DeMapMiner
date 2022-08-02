package readRepository.postReadingUpdates;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;

import readRepository.readRepository.MessageReadingUtils;
import connections.MysqlConnect;
import miner.process.PythonSpecificMessageProcessing;
import utilities.GetDate;
import utilities.ReturnNattyTimeStamp;

//the timestamp has been read in and assigned for 95% of messages, but not for all message records
//This is main program which is going to do for it all pep messages, or only the ones which has dts not assigned/ is null
//The two options for the above are process and verify()

//there are two dates, one in from line and one in date line
//we use the date from date line and have used pattern matching while reading in repository
//now while updating we can use the same 

//first we extract the dateline from the message and then store in datetimestampstring column
//once here we 

//sql 
//select count(messageid) from allmessages where datetimestampstring IS NULL
//update allmessages set datetimestampstring = NULL;
//should run this before
//update allmessages set datetimestamp = NULL;

//if error comes up, skip that 

//errors will arse due to natty library and memory, especially for -1 peps
//so do verification on these data and do this in chunks 100k, 200k, 400k using descending

public class UpdateAllMessages_DateTimeStamps {
	//query each message
	//compute timestamp
	//store in the same table message record in the timestamp column
	static MessageReadingUtils mru = new MessageReadingUtils();
	static PythonSpecificMessageProcessing pms = new PythonSpecificMessageProcessing();
	static String tableName = "allmessages";
	static Boolean process = true,verify = true;
	static MysqlConnect mc = new MysqlConnect();
	
	//look for mid in last errors
	static Integer errorMID=1553821; //1104655; //278768;	//shud be 0 in the beginning
	//errors = 1129335,1128892	
	static GetDate gd = new GetDate();  //new date parser centrally located in seprate package for use from all other package code
	static int distinctPepNumber,pepNumber; 
	static String email=""; 
	static Date dt; 
	//static Timestamp dts= null;
	
	//april 2021, for bips, we add proposal identifier
	static String proposalIdentifier = "bip"; //pep
	static String proposalDetailsTable = "bipdetails"; //"pepdetails";
			
	public static void main(String args[])	{	
		ReturnNattyTimeStamp rnts = new ReturnNattyTimeStamp();		
		try	{
			Connection conn = mc.connect();
			//distinct formats
			//Sat, 14 Jun 2003 10:14:10 -0700
			//Thu,  6 Apr 2006 20:36:57 +0200 (CEST)
			//Sep 22 00:51:56 2003
			if(process){
				//CHUNKING REQUIRED - divide into one pep at a time to give the machine less recordsets to work with at a time
				//do we want to process -1 and 0 pep, then use select from dual
				String sql0 = "SELECT distinct("+proposalIdentifier+") from "+proposalDetailsTable  //;
						+ " UNION SELECT 0 from DUAL UNION SELECT -1 from DUAL;"; // ";
				//order by "+proposalIdentifier+"
//						String sql0 = "SELECT 0 from DUAL ;";	//we want to update those with pep as -1 and 0 also
				//Second run this
				//String test = "SELECT -1 from DUAL;";	//THIS HAS TO BE RUN SEPRATELY AS IT WILL GIVE ERRO
				Statement stmt0 = conn.createStatement();				ResultSet rs0 = stmt0.executeQuery( sql0 );
	//			System.out.format("%10s%40s%40s%40s", "mid","dateTimeStampString", "nattyDateTimeStamp", "patternMatchingDateTimeStamp");System.out.println();				
				while (rs0.next()) {  //for each pep 
					distinctPepNumber = rs0.getInt(1);
					String dateTimeStampString;					Statement st;					Integer mid=null;
					System.out.println("Processing new "+proposalIdentifier+" " + distinctPepNumber);
					if(distinctPepNumber != -1){	//Main query for all peps---doesnt work for -1 peps as they are 1.4 million in number									
						String query = "SELECT "+proposalIdentifier+", messageID,dateTimeStamp, date2, email FROM allmessages where "+proposalIdentifier+"=" + distinctPepNumber + ";"; // and messageID = 9239 order by messageID ";					
						st = conn.createStatement();						ResultSet rs = st.executeQuery(query);
	//					System.out.format("%40s%40s%40s%40s", "FromLine","FromTimestamp", "DateLine", "DateTimeStamp");
						while (rs.next()) {	
							pepNumber = rs.getInt(proposalIdentifier);							mid 		  = rs.getInt("messageID");
							email  = rs.getString("email");							dt  	  = rs.getDate("date2"); //dts = rs.getTimestamp("dateTimeStamp");
							
							if(mid!=9239) continue;
							
							 //if(mid>errorMID){
							dateTimeStampString=updateDateTimeStampStringColumn(conn, mid, email);			//update datetimestamp column
							if (dateTimeStampString != null) { //and after the above we try assign the datetimestamp column a value using pattern matching or natty	
								updateDateTimeStampColumnUsingPattermMatchingOrNatty(rnts, dateTimeStampString, dt, conn, mid);
							}
							else{
								System.out.println("DateTimeStamp String is null " + mid);
							}							
							//}
						}					
						st.close();
					}
					else{
						//query for -1 peps -- the following qiery will work good but for messages which dont have a "Subject:" line..therefore the next query is needed 
						String query = "SELECT "+proposalIdentifier+",date2,messageID, LEFT(email,POSITION('Subject:' IN email)) as email FROM "+tableName+"  where "+proposalIdentifier+" = "+distinctPepNumber+" and datetimestamp IS NULL and messageid > "+errorMID+" order by messageid asc ;"; // and datetimestamp IS NULL ";//messageID < 100000 // order by messageID LIMIT 2";
						//this is needed
						//String query = "SELECT distinct(messageID), LEFT(email,500) as email,datetimestamp FROM "+tableName+"  where pep = "+distinctPepNumber+" and datetimestampstring IS NULL;"; // and datetimestamp IS NULL ";//messageID < 100000 // order by messageID LIMIT 2";
						//String query = "SELECT distinct(messageID), email,datetimestamp FROM "+tableName+"  where datetimestampstring IS NULL "
						//		+ " and messageID >= "+x+" and messageid < "+y+";"; // and datetimestamp IS NULL ";//messageID < 100000 // order by messageID LIMIT 2";
						st = conn.createStatement();
						ResultSet rs = st.executeQuery(query);
						//					System.out.format("%40s%40s%40s%40s", "FromLine","FromTimestamp", "DateLine", "DateTimeStamp");
						while (rs.next()) {	
							int pepNumber = rs.getInt(proposalIdentifier);							mid 		  = rs.getInt("messageID");
							String email  = rs.getString("email");						Date dt  	  = rs.getDate("date2");
							
							if(mid!=9239) continue;
							
							//if(mid>errorMID){
							dateTimeStampString=updateDateTimeStampStringColumn(conn, mid, email);			//update datetimestamp column
							if (dateTimeStampString != null) { //and after the above we try assign the datetimestamp column a value using pattern matching or natty	
								updateDateTimeStampColumnUsingPattermMatchingOrNatty(rnts, dateTimeStampString,dt, conn, mid);
							}
							else{
								System.out.println("DateTimeStamp String is null " + mid);
							}
							//}
						}
						st.close();
					}
				}
			}
			//verify stage - when some datetimestampstring columns remain null, we attempt again
			//after this stage, only 18 rows are left which have 'datetimestampstring' as NULL and 2 rows which have 'datetimestamp' as null and they dont even have a dateline 'Date:'
			if(verify){
				String dateTimeStampString;				Statement st;				Integer mid=null;				
				//for all other peps																		//"+proposalIdentifier+" <> -1 and
				String query = "SELECT "+proposalIdentifier+", messageID, date2, email FROM allmessages where  dateTimeStampString IS NULL;";// and messageID < 100000 order by messageID ";					
				//for pep with -1
				//String query = "SELECT pep,date2,messageID, LEFT(email,POSITION('Subject:' IN email)) as email FROM "+tableName+"  where pep = -1 and dateTimeStampString IS NULL "
				//		+ "and messageid > "+errorMID+" order by messageid asc LIMIT 200000;"; // and datetimestamp IS NULL ";//messageID < 100000 // order by messageID LIMIT 2";
																						//try 100,000 and then 200000 and then 400000 using messageid desc
				st = conn.createStatement();				ResultSet rs = st.executeQuery(query);
				//					System.out.format("%40s%40s%40s%40s", "FromLine","FromTimestamp", "DateLine", "DateTimeStamp");
				while (rs.next()) {	
					int pepNumber = rs.getInt(proposalIdentifier);					mid 		  = rs.getInt("messageID");
					String email  = rs.getString("email");					Date dt  	  = rs.getDate("date2");
					//if(mid>errorMID){
					dateTimeStampString=updateDateTimeStampStringColumn(conn, mid, email);			//update dateTimeStampString column
					if (dateTimeStampString != null) { //and after the above we try assign the datetimestamp column a value using pattern matching or natty	
						updateDateTimeStampColumnUsingPattermMatchingOrNatty(rnts, dateTimeStampString,dt, conn, mid);
					}
					else{
						System.out.println("DateTimeStamp String is null " + mid);
					}
					//}
				}					
				st.close();
			}
			
		}   catch (Exception e)	    {
			//System.out.println("Error: "+mid);
			System.err.println("Got an exception! " + e.toString());			System.err.println(e.getMessage());			System.out.println(StackTraceToString(e));
		}		
	}

	private static void updateDateTimeStampColumnUsingPattermMatchingOrNatty(ReturnNattyTimeStamp rts, String dateTimeStampString,Date dt, Connection conn,	Integer mid) throws SQLException {
		Date nattyDateTimeStamp;		Date patternMatchingDateTimeStamp;
		try {
			try(FileWriter fw = new FileWriter("c:\\scripts\\debugOutfile.txt", true);
				    BufferedWriter bw = new BufferedWriter(fw);
				    PrintWriter out = new PrintWriter(bw))
				{
				    out.println(mid);			    //more code
				    //out.println("more text");
				    //more code
				} catch (IOException e) {
					System.out.println("Error: "+mid);					e.printStackTrace();					System.err.println("Got an exception! " + e.toString());
					System.err.println(e.getMessage());					System.out.println(StackTraceToString(e));
				}
			
			//Natty Result
			nattyDateTimeStamp = rts.returnNattyDate(dateTimeStampString.trim()); //newDate.trim());
			//Pattern Matching 
			patternMatchingDateTimeStamp = gd.findDate(dateTimeStampString);	
		System.out.format("%10s%40s%40s%40s", mid,dateTimeStampString, nattyDateTimeStamp, patternMatchingDateTimeStamp);
		System.out.println();
			//System.out.print("\n" + newTimeStamp);
			//prefer pattern matching as its mostly okay, unless null when can use natty
			if(patternMatchingDateTimeStamp==null){
				if(nattyDateTimeStamp!=null){
					updateTableWithDateTimeStamp(conn, mid, nattyDateTimeStamp);
					System.out.println("Pattern matching is null, used natty ,mid: " + mid);
				}else{
					System.out.println("Pattern matching and Natty both are null, (CONVERTED DATE TO TS) mid: " + mid);
					//here we must try to convert existing date to timestamp
					Date justdate = dt;					
					Timestamp timestamp = new java.sql.Timestamp(justdate.getTime());
					updateTableWithDateTimeStamp(conn, mid, timestamp);
				}				
			}else{
				updateTableWithDateTimeStamp(conn, mid, patternMatchingDateTimeStamp);
			}
		} catch (SQLException e) {
			System.out.println("Error: "+mid);			e.printStackTrace();			System.err.println("Got an exception! " + e.toString());			
			System.err.println(e.getMessage());			System.out.println(StackTraceToString(e));
		}
		catch (Exception e){
			System.out.println("Error: "+mid);			e.printStackTrace();			System.err.println("Got an exception! " + e.toString());
			System.err.println(e.getMessage());			System.out.println(StackTraceToString(e));
		}
	}

	private static String updateDateTimeStampStringColumn(Connection conn, Integer mid, String email)  {
		String dateTimeStampString = "";
		String lines[] = email.split("\\n",10);
		boolean fromFound = false,dateFound=false;
		try {
			for (String line : lines)	{					
				//from line sometimes has date as well
				if (line.contains("Date: ") && dateFound==false){	
					dateFound =true;
					dateTimeStampString = line.replace("Date:", "");	
					dateTimeStampString= dateTimeStampString.trim();

					String updateQuery = "update "+tableName+"  set dateTimeStampString = ? where messageID = ?";
					PreparedStatement preparedStmt = conn.prepareStatement(updateQuery);
					preparedStmt.setString (1, dateTimeStampString);
					preparedStmt.setInt(2, mid);
					int j = preparedStmt.executeUpdate();
					if(j>0)   {
						//System.out.println("success");
					}
					else  {
						System.out.println("stuck somewhere mid " + mid + " newTimeStamp " + dateTimeStampString);
					}

					//System.out.format("%40s%40s%40s%40s","","",date2,date2);

					lines=null;
					break;
				}							
			}
		} catch (SQLException e) {
			System.out.println("Error: "+mid);			e.printStackTrace();
			System.err.println("Got an exception! " + e.toString());			System.err.println(e.getMessage());			System.out.println(StackTraceToString(e));
		}
		 catch (Exception e) {
				System.out.println("Error: "+mid);				e.printStackTrace();				System.err.println("Got an exception! " + e.toString());
				System.err.println(e.getMessage());				System.out.println(StackTraceToString(e));
		}
		return dateTimeStampString;
	}

	private static void populateDateTimeStampStringColumn(ReturnNattyTimeStamp rts, Connection conn, Integer mid, String email) throws SQLException {
		Date dateDateTimeStamp;
		String date2;
		String lines[] = email.split("\\n",10);
		boolean fromFound = false,dateFound=false;
		for (String line : lines)	
		{					
			//from line sometimes has date as well
			/*
			if (line.contains("From ") && line.length() > 20 && fromFound==false)
			{	
				fromFound=true;						
				date2 = line.replace("From ", "");	
				date2 = line.replace("null", "");
				//delete everything before 'at'						
				//System.out.println("k2 " + date2);							
				//delete everything after permCounter
				//String toDelete = string[PermCounter+1];
				//System.out.println("\ntoDelete" +toDelete);
				if (date2.contains("  ")){
					int spaceIndex = date2.indexOf("  ");
					if (spaceIndex != -1)
					{
					    date2 = date2.replace(date2.substring(0, spaceIndex),"");

					}
				}
				// System.out.println("\nk" +date2);

				date2= date2.trim();
				String initialDate = date2;

				//if first 2 digits of last atring is 19 or 20, put it before time 
				String[] dateTimeParts = line.split(" ");
				Integer last = dateTimeParts.length-1; 

				if (dateTimeParts[last].startsWith("19") || dateTimeParts[last].startsWith("20"))	{
					fromDateTimeStamp = swapAndGetDate(rts, date2);
//								System.out.println("\n test a" + date2 + " , " + dateTimeStamp);
				}							
				else{
					fromDateTimeStamp = rts.returnDate(initialDate);
//								System.out.println("dateTimeStamp "+initialDate +  " line " + line);
//								System.out.println("\nb");
				}				



//***							break;   //break on first find
				//break;
			}
			 */
			if (line.contains("Date: ") && dateFound==false){	
				dateFound =true;
				date2 = line.replace("Date:", "");	
				date2= date2.trim();
				String initialDate = date2;

				//if first 2 digits of last atring is 19 or 20, put it before time 
				if (line.contains(" "))
				{
					String[] dateTimeParts = line.split(" ");
					Integer last = dateTimeParts.length-1; 
					if (dateTimeParts[last].startsWith("19") || dateTimeParts[last].startsWith("20"))
					{
						dateDateTimeStamp = swapAndGetDate(rts, date2);
					}							
					else{
						dateDateTimeStamp = rts.returnNattyDate(initialDate);
					}				

					//											System.out.println("kk Original date:   "+ newDate);
					//											System.out.println("kk Natty Result dateTime " + dateTimeStamp);	
					Timestamp newTimeStamp = new Timestamp(dateDateTimeStamp.getTime());
					//											date9 = mru.findDate(date2);	//returns null							
					//											System.out.println("Original date:   "+ date2);
					//											System.out.println("Pattern Matching Result dateTime " + date9);
					//												System.out.print(", Date Line, "+ initialDate+ ", " + dateTimeStamp + ", " );
					//main				System.out.print(", "  +date2 + " , "+ dateDateTimeStamp );
					//***							updateTableWithDateTimeStamp(conn, mid, newTimeStamp);						
					//***							break;   //break on first find
					//System.out.println("");
					//break;

					//just uopdate datetimestamp column
					//updateTableDateTimeStampColumn( conn,  mid, date2);
					//System.out.format("%40s%40s%40s%40s","","",date2,date2);
				}
				lines=null;
				break;
			}							
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

	private static Date swapAndGetDate(ReturnNattyTimeStamp rts, String date2) {
		Date v_dateTimeStamp;
		//get last 4 digits of year and put it just before time 
		String substring = date2.substring(0, date2.length()-4);
		String yearSubstring = date2.substring(date2.length()-4, date2.length());
		//								System.out.println("substringe:   "+ substring);
		//								System.out.println("year substringe:   "+ yearSubstring);

		date2 = date2.trim();
		substring = substring.trim();
		String firstPart = substring.substring(0, substring.lastIndexOf(" "));							
		String lastPart = substring.substring(substring.lastIndexOf(" "), substring.length());							
		String newDate = firstPart.trim() + " " + yearSubstring + " " + lastPart.trim(); 


		newDate = newDate.trim();
		v_dateTimeStamp = rts.returnNattyDate(newDate.trim());
		//		System.out.println("\nOrig date " +date2 + " | New date:   "+ newDate + " | natty date "+v_dateTimeStamp);
		return v_dateTimeStamp;
	}
	private static void updateTableWithDateTimeStamp(Connection conn, Integer mid, Date newTimeStamp)	throws SQLException {
		//from natty time to datetimestamp
		Timestamp now = null;
		if (newTimeStamp!=null){
			now = new Timestamp(newTimeStamp.getTime());
		}

		String updateQuery = "update "+tableName+"  set dateTimeStamp = ? where messageID = ?";
		PreparedStatement preparedStmt = conn.prepareStatement(updateQuery);
		preparedStmt.setTimestamp (1, now);
		preparedStmt.setInt(2, mid);
		int i = preparedStmt.executeUpdate();
		if(i>0)   {
			//System.out.println("success");
		}
		else  {
			System.out.println("stuck somewhere mid " + mid + " newTimeStamp " + newTimeStamp);
		}
	}
	//	private static void updateTableDateTimeStampColumn(Connection conn, Integer mid, String newTimeStamp)
	//			throws SQLException {
	//		  String updateQuery = "update "+tableName+"  set dateTimeStampString = ? where messageID = ?";
	//		  PreparedStatement preparedStmt = conn.prepareStatement(updateQuery);
	//		  preparedStmt.setString (1, newTimeStamp);
	//		  preparedStmt.setInt(2, mid);
	//		  int i = preparedStmt.executeUpdate();
	//		  if(i>0)   {
	//		          //System.out.println("success");
	//		  }
	//		  else  {
	//		         System.out.println("stuck somewhere mid " + mid + " newTimeStamp " + newTimeStamp);
	//		    }
	//	}
}
