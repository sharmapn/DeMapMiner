package readCommits.structs;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.joestelmach.natty.DateGroup;
import com.joestelmach.natty.Parser;

import readCommits.structs.ReturnTimeStamp;

public class Commit {
	
	String contents;
	String date;
	//added
	Date dateTimeStamp;
	String author;
	List<FileChange> changes;
	String pepTitle;
	String entireMessage;
	
	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public Date getDateTimeStamp() {
		return dateTimeStamp;
	}

	public void setDateTimeStamp(Date dateTimeStamp) {
		this.dateTimeStamp = dateTimeStamp;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getPepTitle() {
		return pepTitle;
	}

	public void setPepTitle(String pepTitle) {
		this.pepTitle = pepTitle;
	}

	public String getEntireMessage() {
		return entireMessage;
	}

	public void setGetEntireMessage(String getEntireMessage) {
		this.entireMessage = getEntireMessage;
	}

	public void setContents(String contents) {
		this.contents = contents;
	}

	public void setChanges(List<FileChange> changes) {
		this.changes = changes;
	}

	private static final String  regPat = "--- .*\\n\\+\\+\\+";
	private static Pattern pattern;
	
	public Commit(String contents) {
		this.contents = contents;
		changes = new ArrayList<FileChange>();
		pattern = Pattern.compile(regPat);
			
		findAuthor();
		findDate();
		//findDateTime();
		findFileChanges();
	}
	
	private void findAuthor(){
		
		//get authors full details including email
		author = contents.substring(contents.indexOf("Author: ") + 8 , contents.indexOf("\n", contents.indexOf("Author: ") + 8));
		//gets last name only
		author = author.substring(author.indexOf(" "), author.indexOf("<") - 1);
	}
	
	private void findDate(){
		ReturnTimeStamp rts = new ReturnTimeStamp();
		//gets entire date line
		String dateT = "Thu Aug 11 2011 20:34:07";
		date = contents.substring(contents.indexOf("Date:   ") + 8 , contents.indexOf("\n", contents.indexOf("Date: ") + 8));
		
		String month = date.substring(4, 7);
		String day = date.substring(8, date.indexOf(" ", 8));	
		String year = date.substring(date.lastIndexOf(" ") - 4, date.lastIndexOf(" ")); 
		
		String hours = date.substring(date.indexOf(":")-3,date.indexOf(":"));
		String min = date.substring(date.indexOf(":")+1,date.indexOf(":")+3);
		String sec = date.substring(date.lastIndexOf(":")+1,date.lastIndexOf(":")+3);
		
		//date = day + "-" + month + "-" + year;
		
		String testDate = day + " " + month + " " + year + " " + hours + ":" + min + ":" + sec;
		date = testDate.trim();
		dateTimeStamp = rts.returnDate(testDate.trim());
		System.out.println("Original date:   "+ testDate + ", Natty Result dateTime " + dateTimeStamp);		
		
		Date newDate = findDate(date);
		//System.out.println("Parsing Result date: "+ testDate + ", dateTime " + newDate); // + ", newDateTime " + newDate);
	}
	
	private void findDateTime(){
		
		//gets entire date line		
		String dateTime = contents.substring(contents.indexOf("Date:   ") + 8 , contents.indexOf("\n", contents.indexOf("Date: ") + 8));
		dateTimeStamp = returnDate(dateTime);
		Date newDate = findDate(dateTime);
		System.out.println("date: "+ dateTime + ", dateTime " + dateTimeStamp + ", newDateTime " + newDate);
	}
	
	//nlp datetime parser for all possible datetime formats	
	public static Date returnDate(String dateStr) {
		List<java.util.Date> dateList = new ArrayList<java.util.Date>();
		
		Parser parser = new Parser();
		//List<DateGroup> groups = parser.parse("the day before next thursday");
		List<DateGroup> groups = parser.parse(dateStr);
		for (DateGroup group : groups) {
			List<java.util.Date> dates = group.getDates();
			int line = group.getLine();
			int column = group.getPosition();
			String matchingValue = group.getText();
			String syntaxTree = group.getSyntaxTree().toStringTree();
			Map parseMap = group.getParseLocations();
			boolean isRecurreing = group.isRecurring();
			java.util.Date recursUntil = group.getRecursUntil();

			/* if any Dates are present in current group then add them to dateList */
			if (group.getDates() != null) {
				dateList.addAll(group.getDates());
			}
		}
		for (java.util.Date d : dateList){
			return d;	//just return first date found
		}
		return null;
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
		            "E MMM dd HH:mm:ss yyyy ZZZZ",
		            "EEE MMM d HH:mm:ss yyyy Z",
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
	
	private void findFileChanges(){
		
		//System.out.println(contents.substring(0, 100));
		
		int startIndex = 0;
		int endIndex = 0;
		final int shift = 5;
		String file;
		String newFile;
		
		if(indexOf(contents) == -1)
				return;
		file = contents.substring(indexOf(contents));
		
		newFile = file.substring(shift);
		
		boolean trigger = true;
		while(trigger){
			endIndex = indexOf(newFile);
			if(endIndex == -1){
				trigger = false;
				endIndex = file.length() - shift;
			}
			String changeContent = file.substring(0, endIndex + shift);
			FileChange addChange= new FileChange(getFileName(changeContent), author, date, dateTimeStamp, changeContent, pepTitle, entireMessage);
			changes.add(addChange);
			if(trigger == false)
				break;
			startIndex = endIndex + shift;
			file = file.substring(endIndex + shift);
			newFile = file.substring(shift);
			
			//System.out.println("file = " + file.length() + "\nnewFile = " +  newFile.length());
		}
	}
	
	private String getFileName(String Content){
		
		int x = Content.indexOf("+++");
		int d = Content.indexOf("\n", Content.indexOf("+++"));
		
		String fileNameLine = Content.substring(x, d);
		return fileNameLine.substring(fileNameLine.lastIndexOf("/"));
	}
	
    /** @return index of pattern in s or -1, if not found */
	private static int indexOf(String s) {
	    Matcher matcher = pattern.matcher(s);
	    return matcher.find() ? matcher.start() : -1;
	}
	
	public String getContents() {
		return contents;
	}

	public List<FileChange> getChanges() {
		return changes;
	}	
	
	public String toString(){
			
		StringBuilder sb = new StringBuilder();
		for(FileChange f : changes){
			sb.append(f.toString());
		}
		
		return sb.toString();
	}
}
