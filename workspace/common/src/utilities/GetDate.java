package utilities;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class GetDate {

	public static void main(String[] args) {
		String testDate = "Wed, 27 Dec 2006 17:49:11 -0800";
		Date dateTimeStamp = null, justDate = null;
		justDate = findDate(testDate);
		System.out.println("pubdate " + justDate);
	}
	
	private static Date updateDateTimeStampColumnUsingPattermMatchingOrNatty(ReturnNattyTimeStamp rts, String dateTimeStampString,Date dt, Connection conn,	Integer mid) throws SQLException {
		Date nattyDateTimeStamp,patternMatchingDateTimeStamp;
		try {
			
			//Natty Result
			nattyDateTimeStamp = rts.returnNattyDate(dateTimeStampString.trim()); //newDate.trim());

			//Pattern Matching 
			patternMatchingDateTimeStamp = findDate(dateTimeStampString);	
//@@@		System.out.format("%10s%40s%40s%40s", mid,dateTimeStampString, nattyDateTimeStamp, patternMatchingDateTimeStamp);
//@@		System.out.println();
			//System.out.print("\n" + newTimeStamp);
			//prefer pattern matching as its mostly okay, unless null when can use natty
			if(patternMatchingDateTimeStamp==null){
				if(nattyDateTimeStamp!=null){
					System.out.println("Pattern matching is null, used natty ,mid: " + mid);
					return nattyDateTimeStamp;
				}else{
					System.out.println("Pattern matching and Natty nboth are null, (CONVERTED DATE TO TS) mid: " + mid);
					//here we must try to convert existing date to timestamp
					Date justdate = dt;					
					Timestamp timestamp = new java.sql.Timestamp(justdate.getTime());
					return timestamp;
				}				
			}else{
				return patternMatchingDateTimeStamp;
			}
		} 
		catch (Exception e){
			System.out.println("Error: "+mid);
			e.printStackTrace();
			System.err.println("Got an exception 56! " + e.toString());
			System.err.println(e.getMessage());
			System.out.println(StackTraceToString(e));
		}
		return null;
	}

	public static Date findDate(String v_line)  { 
		String dateC= v_line.trim(); 
		Date pubdate = null;
		
		if (v_line == null) return null;

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
				"d MMM yyyy HH:mm:ss z",
				"dd-MMM-yyyy",
				"yyyy-MM-dd",
				"dd MMM YYYY" //16 June 2016
				
		};
		
		//			  Pattern p0=Pattern.compile("Date:");
		//			  Matcher m0=p0.matcher(v_line);
		//			  if (m0.find()){
		//				  dateC = v_line.replace("Date:","");
		//				  dateC = v_line.replace("Date:","");
		// System.out.println("dateC " + dateC);
		if (dateC.length() >30)
			dateC = dateC.substring(0, 30);
		dateC = dateC.substring(0, dateC.length());  

		for (int i = 0; i < patterns.length; i++) {
			SimpleDateFormat sdf = new SimpleDateFormat(patterns[i], Locale.US);
			try {
				pubdate = sdf.parse(dateC);
//				System.out.println("hello " + pubdate);	
				break;
			} catch (Exception e) {
				//				        	System.out.println("hello " + e.getMessage());
			}
		}
		//  System.out.println("pubdate " + pubdate);		  
		// }
		// System.out.println("pubdate " + pubdate);		  	  
		return pubdate;
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
