package miner.process;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.joestelmach.natty.DateGroup;
import com.joestelmach.natty.Parser;

public class PythonSpecificMessageProcessing {
	
	//return the author from meail from line
	public static String getAuthorFromString(String author) {
		String remove;
		
		//for cases '=?ISO-8859-1?Q?S=E9bastien_Durand?='
		if (author.toLowerCase().contains("=?iso-8859-1?q?"))			author =author.replace("=?iso-8859-1?q?", "");
		
		//if there is text within brackets, extract and process that as most time it contains a name
		if (author.contains("(") && author.contains(")")){
			String temp = author.substring(author.indexOf("(")+1,author.indexOf(")"));	
			author = temp.contains("?") ? author.replace(temp, "") : temp;								
		}
		//remove text within [] and <>
		if (author.contains("[") && author.contains("]")){
			remove = author.substring(author.indexOf("["),author.indexOf("]")+1);
			author = author.replace(remove, "");
		}							
		
		if (author.contains("<") && !author.contains(">"))			author = author.replace("<", "");
		if (author.contains(">") && !author.contains("<"))			author = author.replace(">", "");
		
		if (author.contains("@"))			author= author.split("\\@", 2)[0];
		
		author =author.trim();
		
		while (author.startsWith(">")){			author = author.replaceFirst(">", "");			author =author.trim();		}
		
		//remove " 
		if (author.contains("\""))			author =author.replace("\"", "");
		
		if (author.contains("<") && author.contains(">"))			author = author.split("\\<", 2)[0];

		//if still there is some leftover, remove
		if (author.contains("("))			author = author.split("\\(", 2)[0];
		
		if (author.contains(" at "))			author = author.split("\\ at ", 2)[0];
		
		if(author.contains(",")){
			String[] nameSplitted = author.split(",");
			if (nameSplitted.length>1)
				author = nameSplitted[1] + " " + nameSplitted[0];
			else if (nameSplitted.length==1){				
				author = nameSplitted[0];
			}
			else
				author = author;
		}		
		
		//for cases like "steven.bethard at gmail.com"
		if (author.contains(" at ")){
			String newStr = author.substring(0, author.indexOf("at"));
			author= newStr;
		}
		//@
		if (author.contains("@")){
			String newStr = author.substring(0, author.indexOf("@"));
			author= newStr;
		}
		//<
		if (author.contains("<")){
			String newStr = author.substring(0, author.indexOf("<"));
			author= newStr;
		}
		
		//&lt;  remove everything after
		if (author.contains("&lt;")){
			String newStr = author.substring(0, author.indexOf("&lt;"));
			author= newStr;
		}		
		
		//remove double spaces
		author = removeDoubleSpaces(author);
		
		//return in format firstname.lastname or firstname.middlename.lastname		
		//remove dot aftre middlename
		if (author.contains("."))
			author = author.replace(".", " ");
		author = removeDoubleSpaces(author);		
		if (author.contains(" "))
			author = author.replace(" ", ".");	
		
		//if contains Jr, remove it
		if (author.contains("Jr "))
			author = author.replace("Jr ", " ");
		
		return author.trim().toLowerCase();
	}
	
	public static String extractFullAuthorFromString(String authorLine) {
		String remove,author = authorLine, email="";
		//for cases '=?ISO-8859-1?Q?S=E9bastien_Durand?='
		if (author.toLowerCase().contains("=?iso-8859-1?q?"))
			author =author.replace("=?iso-8859-1?q?", "");
		
		//if there is text within brackets, extract and process that as most time it contains a name
		if (author.contains("(") && author.contains(")")){
			String temp = author.substring(author.indexOf("(")+1,author.indexOf(")"));	
			author = temp.contains("?") ? author.replace(temp, "") : temp;								
		}
		//remove text within [] and <>
		if (author.contains("[") && author.contains("]")){
			remove = author.substring(author.indexOf("["),author.indexOf("]")+1);
			author = author.replace(remove, "");
		}							
		
		if (author.contains("<") && !author.contains(">"))
			author = author.replace("<", "");
		if (author.contains(">") && !author.contains("<"))
			author = author.replace(">", "");
		
		if (author.contains("@"))
			author= author.split("\\@", 2)[0];
		
		author =author.trim();
		
		while (author.startsWith(">")){
			author = author.replaceFirst(">", "").trim();
		}		
		 
		if (author.contains("\"")){
			author =author.replace("\"", "");
			
		}
		//remove backslash '\'
		//author = author.replace("\\","");
		if (author.contains("\\")){
			author = author.replace("\\","");		
		}
		
		if (author.contains("<") && author.contains(">"))
			author = author.split("\\<", 2)[0];

		//if still there is some leftover, remove
		if (author.contains("("))
			author = author.split("\\(", 2)[0];
		
		if (author.contains(" at "))
			author = author.split("\\ at ", 2)[0];
		
//		if(author.contains(",")){
//			String[] nameSplitted = author.split(",");
//			if (nameSplitted.length>1)
//				author = nameSplitted[1] + " " + nameSplitted[0];
//			else if (nameSplitted.length==1){				
//				author = nameSplitted[0];
//			}
//			else
//				author = author;
//		}		
		
		//for cases like "steven.bethard at gmail.com"
//		if (author.contains(" at ")){
//			String newStr = author.substring(0, author.indexOf("at"));
//			author= newStr;
//		}
		//@
//		if (author.contains("@")){
//			String newStr = author.substring(0, author.indexOf("@"));
//			author= newStr;
//		}
		//<
		if (author.contains("<")){
			String newStr = author.substring(0, author.indexOf("<"));
			author= newStr;
		}
		
		//&lt;  remove everything after
		if (author.contains("&lt;")){
			String newStr = author.substring(0, author.indexOf("&lt;"));
			author= newStr;
		}	
		
		author = author.replace("&gt;", "");
		author = author.replace("&lt;", "");
		
		//remove double spaces
		author = removeDoubleSpaces(author);
		
		//return in format firstname.lastname or firstname.middlename.lastname		
		//remove dot aftre middlename
		if (author.contains("."))
			author = author.replace(".", " ");
		//remove single quote
		if (author.contains("'"))
			author =author.replace("'", "");
		
		author = removeDoubleSpaces(author);		
		//if (author.contains(" "))
		//	author = author.replace(" ", ".");
		
		//if contains Jr, remove it
		if (author.contains("Jr "))
			author = author.replace("Jr ", " ");
		if (author.contains(","))
			author = author.replace(",", "");
		
		return author.trim();
	}

	public static String returnAuthorEmail(String authorLine) {
		//Extract email from original authorLine string	
		String email="";
		if (authorLine.contains("(")){
			if(authorLine.indexOf("(") > 3)	//some author names start with open bracket, which causes error 
				email = authorLine.substring(0, authorLine.indexOf("(")-1);
		}
		else{
			if (authorLine.contains("<")) {
			email = StringUtils.substringBetween(authorLine, "<", ">");
			}
			if (authorLine.contains("&lt;")) {
				authorLine = authorLine.split("&lt;")[1];
				authorLine =authorLine.replace("&gt;", "");
				email = authorLine; //.substring(authorLine.indexOf("&lt;"), authorLine.length());
			}
		}		
		return email.trim();
	}
	
	public static String removeSpamFromSAuthor(String author){
		return author;
	}
	
	public static String getFullAuthorFromString(String author) {
		String remove;
		
		//for cases '=?ISO-8859-1?Q?S=E9bastien_Durand?='
		if (author.toLowerCase().contains("=?iso-8859-1?q?"))
			author =author.replace("=?iso-8859-1?q?", "");
		
		//if there is text within brackets, extract and process that as most time it contains a name
		if (author.contains("(") && author.contains(")")){
			String temp = author.substring(author.indexOf("(")+1,author.indexOf(")"));	
			author = temp.contains("?") ? author.replace(temp, "") : temp;								
		}
		//remove text within [] and <>
		if (author.contains("[") && author.contains("]")){
			remove = author.substring(author.indexOf("["),author.indexOf("]")+1);
			author = author.replace(remove, "");
		}							
		
		if (author.contains("<") && !author.contains(">"))
			author = author.replace("<", "");
		if (author.contains(">") && !author.contains("<"))
			author = author.replace(">", "");
		
		if (author.contains("@"))
			author= author.split("\\@", 2)[0];
		
		author =author.trim();
		
		while (author.startsWith(">")){
			author = author.replaceFirst(">", "");
			author =author.trim();
		}		
		 
		if (author.contains("\"")){
			author =author.replace("\"", "");
			
		}
		//remove backslash '\'
		//author = author.replace("\\","");
		if (author.contains("\\")){
			author = author.replace("\\","");		
		}
		
		if (author.contains("<") && author.contains(">"))
			author = author.split("\\<", 2)[0];

		//if still there is some leftover, remove
		if (author.contains("("))
			author = author.split("\\(", 2)[0];
		
		if (author.contains(" at "))
			author = author.split("\\ at ", 2)[0];
		
		if(author.contains(",")){
			String[] nameSplitted = author.split(",");
			if (nameSplitted.length>1)
				author = nameSplitted[1] + " " + nameSplitted[0];
			else if (nameSplitted.length==1){				
				author = nameSplitted[0];
			}
			else
				author = author;
		}		
		
		//for cases like "steven.bethard at gmail.com"
//		if (author.contains(" at ")){
//			String newStr = author.substring(0, author.indexOf("at"));
//			author= newStr;
//		}
		//@
//		if (author.contains("@")){
//			String newStr = author.substring(0, author.indexOf("@"));
//			author= newStr;
//		}
		//<
		if (author.contains("<")){
			String newStr = author.substring(0, author.indexOf("<"));
			author= newStr;
		}
		
		//&lt;  remove everything after
		if (author.contains("&lt;")){
			String newStr = author.substring(0, author.indexOf("&lt;"));
			author= newStr;
		}		
		
		//remove double spaces
		author = removeDoubleSpaces(author);
		
		//return in format firstname.lastname or firstname.middlename.lastname		
		//remove dot aftre middlename
		if (author.contains("."))
			author = author.replace(".", " ");
		//remove single quote
		if (author.contains("'"))
			author =author.replace("'", "");
		
		author = removeDoubleSpaces(author);		
		//if (author.contains(" "))
		//	author = author.replace(" ", ".");
		
		//if contains Jr, remove it
		if (author.contains("Jr "))
			author = author.replace("Jr ", " ");
		
		return author.trim();  //.toLowerCase();
	}

	private static String removeDoubleSpaces(String author) {
		if(author.contains("  "))
			author = author.replaceAll("  ", " ");
		author =author.trim();
		return author;
	}
	
	public static String modifyAuthorNames(String author) {
		if (author.contains("\\")){
			author = author.replaceAll("\\\\", " ");
		}
		if (author.contains("%"))
			author = author.replaceAll("%", " ");
		if (author.contains("'"))
			author = author.replaceAll("'", " ");
		if (author.contains("\\"))
			author = author.replaceAll("\\", " ");
		//System.out.println("Author "+ author);
		return author;
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
	
	//if blank line occurs, it should mean new sentence
	// not yet sure how to cnfigure that in standford nlp
	//so just add a full stop
	public static String handleBlankLines(String v_message){
		//while (v_message.contains("\n\n")) {
		v_message = v_message.replaceAll("\n\n","\n.");
		// }
		return v_message;
	}
	
	//stanford tokeniser changes all ( and ) to LRB and RRB 
	//this method changes it back.
	public static String removeLRBAndRRB(String str){
		//remove " -LRB-" and " -RRB-"
		while (str.contains(" -LRB-")) {			str = str.replace(" -LRB-","(");		}		
		while (str.contains("LRB")) {			str = str.replace("LRB","");		}
		while (str.contains("LSB")) {			str = str.replace("LSB","");		}
		while (str.contains(" -RRB-")) {			str = str.replace(" -RRB-",")");		}
		while (str.contains("RRB")) {			str = str.replace("RRB",")");		}
		//remove tags
		while (str.contains("<br>)")) {			str = str.replace("<br>)",")");		}
		return str;
	}
	
	//sentences with | jumbles up the output columns
	public static String removeDivider(String str){
		//remove " -LRB-" and " -RRB-"
		while (str.contains("|")) {			str = str.replace("|","");		} 
		//() \/ 
		while (str.contains("\\/")) {			str = str.replace("\\/","");		}	 
		while (str.contains("()")) {			str = str.replace("()","");		}
		return str;
	}
	
	public static String extractInBracketsText(String v_sentence){
		String v_textInBrackets = null;
		Integer startIndex = 0,endIndex = 0;
		
		if(v_sentence.contains("(") && v_sentence.contains(")") ){
			startIndex = v_sentence.indexOf("(");
			endIndex = v_sentence.indexOf(")");
			if (endIndex > startIndex){
				v_textInBrackets = v_sentence.substring(startIndex, endIndex);
			}
		}
		return v_textInBrackets;	
	}
	
	//remove quoted text in a message
	public static String removeQuotedText(String v_message) {	    
		Integer start =0, end=0, countchar = 0;
		String sub = null;
		
		//remove all whitespaces just after newline
		while (v_message.contains("\n ")) {
			v_message = v_message.replace("\n ","\n");
		}
		
		// countchar = number_of_cccurrences_of_char_in_string_java(Message);
		//|| v_message.contains(" >> ") || v_message.contains(" >>> ")
		//      char[] quoting = {'>','['};  
		
		while (v_message.contains("\n>")) {
			// System.out.println(countchar);
			// for (int h=0; h<countchar; h++) { 
			//  if (Message.toLowerCase().contains("\n>")){
			start =  v_message.indexOf("\n>");
			end = v_message.indexOf("\n", start+1);
			if (start != -1 && end != -1){
				sub = v_message.substring(start, end);
				//          	 		System.out.println(start + " " + end + " sub" + sub);
				v_message = v_message.replace(sub,"");
				//		 //           	 		System.out.println(countchar);
				//		 //           	 		System.out.println("start end " + start + end + " replaced" + sub);
			}           	 
			//   }
			// }
		}
		return v_message;
	}
	
	public static String removeDoubleSpacesAndTrim(String v_sentence){
		v_sentence = v_sentence.trim().replaceAll("\\s+", " ");
		String after = v_sentence.trim().replaceAll(" +", " ");
		//CurrentSentenceString = CurrentSentenceString.replaceAll("\\s+", " ").trim();	//remove double spaces and trim
		return after.trim();
	}
	
	//these are also quoted text	
	/*
	  >>> I was going over the PEP index this morning, and I noticed a large
	  >>> number of PEPs listed under the "open" list that would seem to me
	  >>> to be "accepted", if not "done" in some cases, according to the
	  >>> criteria described by the headings.  (Specifically, PEPs 218, 237,
	  >>> 273, 282, 283, 301, 302, 305, and 307.)	
	  > It's not a big deal, but it's very hard to see from the list which
	  > things are "in progress", "need revisions", or are "unlikely to make
	  > it". So since I already took the trouble to work out the answers for
	  > myself, I thought I'd offer to help the next person who came along.
	  > :)
	  >>> Others under "open" I would guess are in fact "rejected", notably
	  >>> 294 (the patch was closed rejected)
	  >> Correct -- this *issue* is still open, but the solution from the
	  >> PEP is rejected.
	 */
	//also these ones
	/*
	 *  [ Mark wants to apply a patch that prevents crashes on windows when loading
 		extentions linked with old Python DLLs, but doesn't know where to document
 		it: PEP 42 or the bug report(s) ]

		[ Skip proposes a separate PEP ]

		[ Mark doesn't have time to write a PEP ]
	 * 
	 */
	
	public static String removeUnwantedText(String v_sentence){
		// remove '\*'
		
		//remove quotations
		//A vote was held , a majority said `` no change '' , and suddenly now you 're claiming that `` obviously a vote is n't the important thing '' ? .		
		//remove the two types of quotes
		
		//		String quotes = "\""; 			
		//		if (v_sentence.contains(quotes))
		//		{
		//			v_sentence = v_sentence.replace(quotes, "");
		//		}
		//		
		//		String quotes2 = "\''"; 			
		//		if (v_sentence.contains(quotes2))
		//		{
		//			v_sentence = v_sentence.replace(quotes2, "");
		//		}
		//System.out.println("\t\t here 43");
											//may need brackets ()
		v_sentence = v_sentence.replaceAll("[^a-zA-Z0-9.',?!\\s]+"," ");
		/*
	   String[] removeStrList = {"\\","/","\\*","+","-",":", "\"", "\''"};
	   for (String removeStr: removeStrList){
		   if (v_sentence.contains(removeStr) )		{
				v_sentence = v_sentence.replaceAll(removeStr, "");
				//v_sentence.replace(un, "");
			}
	   }
		 */
		
		String unwanted = "Update of / cvsroot/python/python"; 
		//for (String un :unwanted){
		if (v_sentence.contains(unwanted) )	{
			//find loc of "Modified Files: "
			int loc= v_sentence.indexOf("Modified Files: ");
			//trim this part
			v_sentence = v_sentence.substring(loc, v_sentence.length());
			//v_sentence.replace(un, "");
		}	
		//}
		//unwanted messages
		//just make sentence to null - but this code should be implemented while reading messages
		String unwantedMessage = "Patch / Bug Summary";
		if (v_sentence.contains(unwantedMessage))	{
			v_sentence = "";
		}
		
		//remove checkin messages text
		//LSB- Python-checkins -RSB- python/nondist/peps pep-0329.txt  1.2  1.3 >
		//only remove LSB- Python-checkins -RSB- python/nondist/peps
		String checkIns = "LSB- Python-checkins -RSB- python/nondist/peps";
		if (v_sentence.contains(checkIns))	{
			v_sentence = v_sentence.replace(checkIns, "");
		}
		
		//remove sentences starting with "null"
		String nullStart = "null";
		if (v_sentence.startsWith(nullStart))		{
			v_sentence = v_sentence.replace(nullStart, "");
		}
		
		//remove email addresses
		if (v_sentence.contains("<") && v_sentence.contains(">") && v_sentence.contains("@")){
			//extract all instances of this 
			//<5.1.1.6.0.20050112140929.03d52e30@mail.telecommunity.com>
			Integer counter = 0;
			while (v_sentence.contains("<") && v_sentence.contains(">") && v_sentence.contains("@") && counter<20)
			{
				counter++;
				//find first instance of email adress and remove
				int firstOpenSign = v_sentence.indexOf("<");
				//int firstAt = v_sentence.indexOf("@");
				int firstCloseSign = v_sentence.indexOf(">");					
				if (firstCloseSign > firstOpenSign){
					String sub = v_sentence.substring(firstOpenSign, firstCloseSign);
					v_sentence = v_sentence.replace(sub, "");
				}
			}
		}
		return v_sentence;
	}
	
	/*
	public static void outputVotes(int v_i){
		   Date v_votingStartDate = p[v_i].getVotingStartDate();
		   Date v_votingEndDate = p[v_i].getVotingEndDate();
		   Date v_alternateVotingStartDate = p[v_i].getalternateVotingStartDate();
		   Date v_alternateVotingEndDate = p[v_i].getalternateVotingEndDate();
		   
		   if (v_votingStartDate != null && v_votingEndDate != null) {
	//VVV not required		   System.out.println(v_votingStartDate  + ", voting-start, all ");
		   	   System.out.println(v_votingEndDate  + ", voting-end, all ");
	   		}
		   if (v_alternateVotingStartDate != null && v_alternateVotingEndDate != null) {
			   System.out.println(v_alternateVotingStartDate  + " alternate-voting-start, all ");
		   	   System.out.println(v_alternateVotingEndDate  + " alternate-voting-end, all ");
	   		}
		} 
	 */
	private static void generateCsvFile(String sFileName)
	{
		try		{
			FileWriter writer = new FileWriter(sFileName);			
			writer.append("DisplayName");			writer.append(',');			writer.append("Age");			writer.append('\n');			
			writer.append("MKYONG");			writer.append(',');			writer.append("26");			writer.append('\n');			
			writer.append("YOUR NAME");			writer.append(',');			writer.append("29");			writer.append('\n');			
			//generate whatever data you want			
			writer.flush();			writer.close();
		}
		catch(IOException e)		{
			e.printStackTrace();
		} 
	}
	
	public static int  number_of_cccurrences_of_char_in_string_java(String stringToSearch) {	    
		String letter = "\n>";
		int i = 0, count = 0;
		while ((i = stringToSearch.indexOf(letter, i++)) != -1) {
			count++;
			i += letter.length();
		}
		return count;
	}
	
	public static void deletePreviousFilesFromOutputDirectory(String dir){		
		// Lists all files in folder
		File folder = new File(dir);
		File fList[] = folder.listFiles();
		// Searchs .lck
		for (int i = 0; i < fList.length; i++) {
			File pes = fList[i];
			if (pes.getName().endsWith(".csv") || pes.getName().endsWith(".txt") ) {
				// and deletes
				pes.delete();
				//boolean success = (new File(fList[i]).delete());
			}
		}
	}	
	public void finalize() {	    //System.out.println("Probability instance is getting destroyed"); 
	}
}
