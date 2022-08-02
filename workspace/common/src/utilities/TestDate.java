package utilities;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TestDate {
	
	public static void main(String args[]){
		ReturnNattyTimeStamp rts = new ReturnNattyTimeStamp();
		Date dateTimeStamp=null;
		
		String date = "Thu Aug 11 2011 20:34:07";
		
		String month = date.substring(4, 7);
		String day = date.substring(8, date.indexOf(" ", 8));	
		String year = date.substring(date.lastIndexOf(" ") - 4, date.lastIndexOf(" ")); 
		
		String hours = date.substring(date.indexOf(":")-3,date.indexOf(":"));
		String min = date.substring(date.indexOf(":")+1,date.indexOf(":")+3);
		String sec = date.substring(date.lastIndexOf(":")+1,date.lastIndexOf(":")+3);
		
		date = day + "-" + month + "-" + year;
		
		String testDate = day + " " + month + " " + year + " " + hours + ":" + min + ":" + sec;
		dateTimeStamp = rts.returnNattyDate(testDate);
		System.out.println("Natty Result date:   "+ testDate + ", dateTime " + dateTimeStamp);		
		
		Date newDate = findDate(date);
		System.out.println("Parsing Result date: "+ testDate + ", dateTime " + newDate); // + ", newDateTime " + newDate);
	}
	
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
		            "EEE MMM d HH:mm:ss yyyy Z",    //new added for checkin messages - Daniel
		            "EEE MMM dd HH:mm:ss yyyy Z",     //new added for checkin messages - Daniel
		            "yyyy-MMM-dd",					//new added for checkin messages - Daniel
		            "EEE MMM dd HH:mm:ss yyyy ZZZZ",
		            "EEE MMM d HH:mm:ss yyyy ZZZZ",
		            
		            "E MMM dd HH:mm:ss yyyy ZZZZ",
		            "E MMM d HH:mm:ss yyyy ZZZZ",
		            "E MMM dd HH:mm:ss yyyy Z",
		            "E MMM d HH:mm:ss yyyy Z",
		            
		            "EEE MMM dd HH:mm:ss yyyy Z",		            
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

		  Date pubdate = null;
		  if (v_line.length() >30)
			  v_line = v_line.substring(0, 31);
		  v_line = v_line.substring(1, v_line.length());  
		  
		  for (int i = 0; i < patterns.length; i++) {
			  SimpleDateFormat sdf = new SimpleDateFormat(patterns[i]);
			  try {
				  pubdate = sdf.parse(v_line);
				  //  System.out.println("hello " + pubdate);	
				  break;
			  } catch (Exception e) {
				  //			        	System.out.println("hello " + e.getMessage());
			  }
		  }
		  return pubdate;
	  }
}
