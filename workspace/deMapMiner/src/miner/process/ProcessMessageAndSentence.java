package miner.process;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import readRepository.readMetadataFromWeb.GetProposalDetailsWebPage;
import connections.MysqlConnect;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.process.DocumentPreprocessor;
import miner.processLabels.TripleProcessingResult;
import utilities.ReadFileLinesIntoArray;

import java.lang.String;
import java.lang.Throwable;
import java.lang.StackTraceElement;

public class ProcessMessageAndSentence {
	
	static List<String> localCandidateSentencesList;
	
	// additional added
	static List<String> proposalNumberSearchKeyList = new ArrayList<String>(),proposalTitlesSearchKeyList = new ArrayList<String>();
	static List<String> wordsList = new ArrayList<String>(),ignoreList = new ArrayList<String>(),statusList = new ArrayList<String>();
	
	//march 2019, if sentences like this occur, we count as reference to same PEP 'This PEP is rejected in favor of PEP 343.'
	static String selfRef[] = {"this pep","acceptance of","based on","in favour","weaker version"};
		
	public ProcessMessageAndSentence(){	}
	
	public void setListNull(){
		localCandidateSentencesList = null;
	}
	
	//see if sentence contains any combination of two terms  subject and verb, subject and object , object and verb
	public static Boolean seeIfAnyTwoTermsinSentence(String terms, String sentence){
		//System.out.println("terms" + terms + " sentence "+sentence);
				
		//deal != ideal therefore we use equals function below
		if (sentence == "null" || sentence == null || sentence.length() == 0){
			return false;
		}
		else{
			if (sentence.contains(" ")){
				String[] sentenceStr = sentence.toLowerCase().split(" ");				
				//String[] splited=null;
				if(terms.contains(" ")){ terms = terms.replace("  "," "); 	}	//replace double spaces
				
				String[] splited = terms.toLowerCase().split(" ");  //nothing will happen if space not found, it would create an array of one term				
				
				//if length ==2, it would be captured in teh results automatically and checked, so no need to check and store in db again
				boolean foundAnyTwoTerms=false;
				if(splited.length ==3) {
					//some triples have same subject and object
					if(splited[0].equals(splited[2])) {
						foundAnyTwoTerms = (					
								Arrays.asList(sentenceStr).contains(splited[0]) &&  Arrays.asList(sentenceStr).contains(splited[1])	//sub and verb
								||
								Arrays.asList(sentenceStr).contains(splited[1]) &&  Arrays.asList(sentenceStr).contains(splited[2])	//verb and obj
								);
					}
					else {
						foundAnyTwoTerms = (					
								Arrays.asList(sentenceStr).contains(splited[0]) &&  Arrays.asList(sentenceStr).contains(splited[1])	//sub and verb
								||
								Arrays.asList(sentenceStr).contains(splited[1]) &&  Arrays.asList(sentenceStr).contains(splited[2])	//verb and obj
								||
								Arrays.asList(sentenceStr).contains(splited[0]) &&  Arrays.asList(sentenceStr).contains(splited[2])	//sub and obj
								);
					}
				}
				return foundAnyTwoTerms;
			}
			else{	//sentence may not have space, so therefore just one logical term and in this case return false
				return false;
			}
		}
		//				if (Arrays.asList(sentenceStr).containsAll(Arrays.asList(splited))){
		//					System.out.println("Contains");
		//					System.out.println("\nChecking if S,V,O (" + Arrays.toString(splited) + " ) in sentence " + sentence);
		//				}		
		//				else{
		//					//System.out.println("Doesnt Contain");
		//				}
		//System.out.println("Arrays.asList(sentenceStr) "+Arrays.asList(sentenceStr));
		//System.out.println("Arrays.asList(splited) "+Arrays.asList(splited));
						
	}
	//deal != ideal therefore we use equals function below
	public static Boolean seeIfAllTermsinSentence(String terms, String sentence){
		//String[] sentenceStr =null;			//String[] splited=null;
		try{
		//	System.out.println("terms: " + terms + " sentence: "+sentence);			
			if (sentence == "null" || sentence == null || sentence.length() == 0){
				return false;
			}
			else{
				sentence = sentence.replace(".", " .").replace(",", " ,").replace("!", " !").replace("-", " -").replace(";", " ;").replace(":", " :");	//dec 2018
				if (sentence.contains(" ")){
					String[] sentenceStr = sentence.toLowerCase().split(" ");					
					if(terms.contains(" ")){	terms = terms.replace("  "," ");  } //replace double spaces
					
					String[] splited = terms.toLowerCase().split(" ");	//if terms does not contain space no spaces, it woudl create an array of one element
						return Arrays.asList(sentenceStr).containsAll(Arrays.asList(splited));
					
				}
				else{	return false; }//sentence may not have space, so therefore just one logical term and in this case return false					
			}
			//return Arrays.asList(sentenceStr).containsAll(Arrays.asList(splited));
		}  //end try       
		catch (Exception e){ //			System.out.println("Error splited: " + splited + " sentenceStr: "+sentenceStr );
			System.out.println("Error Checking terms______here 348  " + e.toString() + "\n" );			
			//System.out.println(Thread.currentThread().getStackTrace()  );		//+  " \n" + e.printStackTrace()
			System.out.println(StackTraceToString(e)  );	
			//continue;
		}
		return false;
	}
	
	public static String StackTraceToString(Exception ex) {
		String result = ex.toString() + "\n";
		StackTraceElement[] trace = ex.getStackTrace();
		for (int i=0;i<trace.length;i++) {
			result += trace[i].toString() + "\n";
		}
		return result;
	}
	
	// for each sentence check if it does not start with question terms				
	// if match, dont process, else process
	public Boolean checkSentenceIsQuestion(String CurrentSentenceString, String	questionPhrases[]){	    	
		Boolean SentenceConditional = false;    	
		for (String qp: questionPhrases){
			//				qp = qp + " " + verb;
			//if (CurrentSentenceString.toLowerCase().contains(qp)){
			if (CurrentSentenceString.toLowerCase().startsWith(qp)){
				SentenceConditional = true;
			}
		}
		//you can check if sentence ends with question mark
		return SentenceConditional;
	}
	
	public Boolean checkSentenceEndsWithQuestionMark(String CurrentSentenceString){	    	
		if (CurrentSentenceString.trim().endsWith("?")){
			return true;
		}				
		return false;
	}
	
	//dont consider sentences with these terms
	//patches and bugs
	public Boolean checkSentenceHasUnwantedTerms(String CurrentSentenceString, String	unwantedTerms[]){	    	
		Boolean unwanted = false;    	
		for (String ut: unwantedTerms){
			//				qp = qp + " " + verb;
			//if (CurrentSentenceString.toLowerCase().contains(qp)){
			if (CurrentSentenceString.toLowerCase().contains(ut)){
				unwanted = true;
			}
		}		
		return unwanted;
	}
	
	public Boolean checkSentenceIsComplex(String CurrentSentenceString){
		Boolean complexSentence = false;
		String [] complex = {"\\* PEP 380 , syntax for delegating to a subgenerator( `` yield from ) \\* PEP 393 , flexible string","Mortensen  > XX  XX  01  04  03  02","Carl Banks  <pep308PY@aerojockey.com>","<bhoPYel at web.de>","bhopyel at web.de> 11 | 12 | 04 | 03 | 05 | ","jjpym at tiscali.dk> xx", "11 | 12 | 04 | 03 | 05 | 01 | 06 | ","01 | 04 | 03 | 02 | xx | xx | xx | "," i\\/o <br> <br> <br> to download python 3.4.0 visit : <br> <br> <a href ","major new features and <br> changes in the 3.4 release series include : <br> <br>", "pep : xxx title : cofunctions version : $ revision $ last-modified : $ date $ status : draft content-type :","Any unfamiliar punctuation is probably markup for reST _","= 0A = Several key postings( from our point of view) that may help to = 0A = navigate the discussions include : = 0A"};
		
		for (String c: complex){
			if (CurrentSentenceString.toLowerCase().contains(c.toLowerCase()))
			{	
				complexSentence = true;
				//							System.out.println("COMPLEX SENTENCE");
			}
		}
		return complexSentence;
	} 
	
	//july 2018
//	Category: Macintosh
//	Group: None
//	Status: Open
//	Resolution: None
//	Priority: 5
//	Submitted By: Bob Ippolito (etrepum)
//	Assigned to: Nobody/Anonymous (nobody)
//	Summary: Enhance PackageManager functionality
	
	//Another issue is headers, example below
	//Abstract
	//This is..
	//We handle this by checking if there are one or two terms 
	public String stripHeadingsInParagraph(String paragragh) {
		String newParagraph="",line,nextLine=""; char nextLineFirstLetter; 
		Integer numTermsInLine;
		//split paragraph using newline
		//if newline charater is capital and the line length is < 30 and comtaisn semi colon , it is heading
		String[] lines = paragragh.split("\\n");
		//for(String l : lines) {
		//	System.out.println("\tOriginal Line: "+ l);
		//}
		for (int p=0; p < lines.length; p++) { 
			line = lines[p].trim();  
			
			//REMOVE UNNECESSARY LINES
			//--
			//<a href="http://www.tuxedo.org/~esr">Eric S. Raymond</a>
			if(line.trim().startsWith("--") //--
				|| line.trim().startsWith("<") && line.trim().endsWith(">") //<a href="http://www.tuxedo.org/~esr">Eric S. Raymond</a>
				|| line.trim().startsWith("http") && line.trim().endsWith("html") //<a href="http://www.tuxedo.org/~esr">Eric S. Raymond</a>
				|| line.trim().startsWith("From ") 
			) {
//				System.out.println("\n line num: "+ p + ": skipped continued. "+ line); 
				continue;	//skip thee lines
			} 
			
			//CALCULATE THE # of TERMS
//			System.out.println("\t line num: "+ p + ": " + line);
			if(line.contains(" ")) {	
				numTermsInLine = line.split(" ").length;  }
			else { 
				numTermsInLine=1; 
			}	
			//MAKE SURE WE DONT TRY TO ACCESS THE NEXT LINE IF ITS LAST LINE
			if(p == lines.length-1) //when we have reached the last line, make sure we dont try reference the next line if we are currently on teh last line
			{
				nextLine=""; nextLineFirstLetter='a'; 				
			}else {					//when its not the last line yet...for cases when there is a next line, but empty - wonder how that can be ..lolz
				if (lines[p+1]==null || lines[p+1] == "" || lines[p+1].isEmpty()) { 
					nextLine=""; nextLineFirstLetter='a'; //just dummy value..would work as its not capital anyway	
				} 
				else {
					nextLine = lines[p+1].trim();	
					if(nextLine.equals("null") || nextLine == null || nextLine == "" || nextLine.isEmpty())
						nextLineFirstLetter='a';	//dummy value
					else						
						nextLineFirstLetter= nextLine.charAt(0);
				}
			}//			System.out.println("\t line num: "+ p + ": " + line);

			
			//MAIN PROCESSING FOR EACH LINE
			if (line.equals("null") || line == null || line == "" || line.isEmpty())
			{	
				//System.out.println("\tempty line continued. num: "+ p + ": "+ line); 
				continue; }
			if (Character.isUpperCase(line.charAt(0))  //first letter of line is uppercase
					&& line.contains(":") && line.length() < 50 //line contains ':' 
					&& Character.isUpperCase(nextLineFirstLetter) ) {  //nextline starts with uppercase
//				System.out.println("\tFirst case: "+ line); //we identify this as not a sentence and dont add to new paragraph
			}
			else if (Character.isUpperCase(line.charAt(0))  //first letter of line is uppercase
					&& line.length() < 50 && (numTermsInLine < 2) //only one or two terms in sentence
					&& Character.isUpperCase(nextLineFirstLetter) ) {  //nextline starts with uppercase
//				System.out.println("\tSecond case: "+ line);//we identify this as not a sentence and dont add to new paragraph
			}else {
				newParagraph = newParagraph+line + " ";	
//				System.out.println("\tElse case: "+ line);
			}
		}
		return newParagraph;
	}
	
	public static void showMessgaeWithSameMD5(String v_md5, Integer v_pepNumber) throws SQLException{
		String url = "jdbc:mysql://localhost:3306/peps";
		String userid = "root";
		String password = "root";
		String sql = "SELECT messageID from allmessages WHERE md5 = '" + v_md5 + "' AND pep = " + v_pepNumber + ";"; 
		
		String mid = null;
		
		try (Connection connection = DriverManager.getConnection( url, userid, password );
				Statement stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery( sql ))
		{        	  
			
			while (rs.next()){     //check every message 
				mid = rs.getString(1);
				System.out.print(" mid: " + mid);
			}
			System.out.println();
		}
		catch (SQLException e){
			System.out.println( e.getMessage() );
		}
		catch (Exception e){
			//continue;
			System.out.println(e.toString());
		}
	}
	
	//maybe store this hashvalue while reading it in
	public static boolean whetherToProcessMessage(Integer v_pepNumber, String v_md5, Integer v_code, List<String> md5List , List<String> v_tempMessageList, Integer mid, String v_Message) throws SQLException{
		//System.out.println("MessageID " +mid+  " v_md5 "+ v_md5);
		boolean hashFound = false;
		for (String temp : md5List) {
			// System.out.println("digest  :  "+ digestBytes + "   hashcode:  "+ Message.hashCode()); 
			//System.out.println(temp + " hash " + code);
			if(temp.equals(v_md5)){
				//					   System.out.println("HASHCODE MATCHED:  " + temp + " for mid: " + mid);
				showMessgaeWithSameMD5(v_md5, v_pepNumber);
				hashFound= true;
			}     		   
		}
		if(hashFound==false){
			md5List.add(v_md5);    			//add the hashcode to array
			//  System.out.println("hashcode doesnt exist and added  for MessageID " +mid+  " HashCode "+ v_code);
		}
		//return hashFound;	   
		
		
		boolean msgFound = false;
		for (String tmp : v_tempMessageList) {
			// System.out.println("digest  :  "+ digestBytes + "   hashcode:  "+ Message.hashCode()); 
			//System.out.println(temp + " hash " + code);
			
			//compare the v_message with the tmp in terms of string similarity.
			//if > 90 oercent output
			
			if(tmp.equals(v_Message)){
				//					   System.out.println("msg MATCHED:  " + tmp + " for mid: " + mid);
				//showMessgaeWithSameMD5(v_md5, v_pepNumber);
				msgFound= true;
			}     		   
		}
		if(msgFound==false){
			md5List.add(v_Message);    			//add the hashcode to array
			//  System.out.println("hashcode doesnt exist and added  for MessageID " +mid+  " HashCode "+ v_code);
		}
		return msgFound;
		
	}
	
	public static Integer getNumberOfPepsAssignedWithTheMessage(Integer mid){
		Integer pepCount = 0; 
		//first get the message
		String url = "jdbc:mysql://localhost:3306/peps_new";
		String userid = "root";
		String password = "root";
		String sql = "SELECT count(pep) from allmessages WHERE messageid = " + mid  + ";"; 
						
		try (Connection connection = DriverManager.getConnection( url, userid, password );
				Statement stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery( sql ))
		{        	  
			
			if (rs.next()){     //check every message 
				pepCount = rs.getInt(1);
				//System.out.print(" v_Message: \n" + v_Message);
			}
			System.out.println();
		}
		catch (SQLException e){
			System.out.println( e.getMessage() );
		}
		catch (Exception e){
			//continue;
			System.out.println(e.toString());
		}
		return pepCount;		
	}
	
	
//	//If a label is captured, we check to see how many other peps have that message assigned under it
//	//if more than one, then we split the message and split by pep number
//	//and then assign the text after a pep number and before another pep number as text of the former pep
//	// Still have to code for instances where two peps are mentioned in a message
//	public static String splitMessageByUniqueIdentifierReturnTextAfterIdentifier(Integer currentIdentifierNumber,Integer mid,String v_Message){
//		
//		String pepNumber = currentIdentifierNumber.toString(); //"267";
//		
//		//added		
//		String text = "",part1,part2;		
//		String extractedTextForcurrentIdentifierNumber="",CurrentSentenceString="";
//				
//		List<String> v_searchKeyList = new ArrayList<String>();
//		List<String> v_ignoreList = new ArrayList<String>();
//		Integer i = null;
//		String regex = "\\d+";
//		
//		//v_searchKeyList.clear();
//		System.out.println("\t##splitting message");
//		
//		v_searchKeyList.add("PEP" + ": " + regex);//		v_searchKeyList.add("PEP" + regex + " ");//		v_searchKeyList.add("PEP " + regex + " ");
//		v_searchKeyList.add("PEP " + regex + "\\?");//		v_searchKeyList.add("PEP " + regex + "\\."); //		v_searchKeyList.add("PEP " + regex + "\\,"); 
//		v_searchKeyList.add("PEP " + regex + "\\;");//		v_searchKeyList.add("PEP " + regex + "\\:");//		v_searchKeyList.add("PEP " + regex + "\\[");	 
//		v_searchKeyList.add("pep" + regex + "-");	
//
//		v_ignoreList.clear();
//		v_ignoreList.add("pep: " + regex + ".");//		v_ignoreList.add("pep: " + regex + "..");//		v_ignoreList.add("pep: " + regex + "...");
//		
//		// we want to store the text after a pep number has been found
//		
//		//best is to process one line each time
//		//populate a variable "text" if pep if found, keep that line aside and keep adding to text until next pep found
//		
//		Matcher m5 = null;
//		
//		String[] lines = v_Message.split("\\n");
//		String previousIdentifier="";
//		
//		boolean found=false;	
//		
//	    for (String entireLine: lines){		//for all lines in message	    	
//			for (String pattern : v_searchKeyList){		//for all patterns
//				// System.out.println("pattern " + pattern);
//				Pattern p5 = Pattern.compile(pattern);
//				m5 = p5.matcher(entireLine);
//				//pattern matched the pep number passed as parameter
//				
//				while (m5.find()){ // while (m.find() || m9.find())
//					System.out.println(" Match found in mid ("+mid+"): "+m5.toString());
//					if (found){	//output sentences next time a identifier is found, but output the previous identifier
//						
//						//show all text in between two peps, in each case show text after the pep
//						System.out.println(" Identifier: " + pepNumber +"-was found in previous round------"+ text);
//						text="";
//						found=false;
//						
//					}
//					//m5.matches();
//					//split before and after of message
//					part1 = m5.group();
//					part2 = m5.group(0);	
//					
//					if(part1.contains(pepNumber)){
//						found =true;
//						
//					}						
//						//empty text
//					previousIdentifier = part1;
//				} // end while
//				
//				
//			} // end for
//			
//			if (found){	//once a match is found, keep adding current sentence to variable -- and in that case, clear
//    			text= text + entireLine + "\n";   
//    		}
//	    }		
//		return extractedTextForcurrentIdentifierNumber;
//	}
	
	private static int indexOf(String string) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public String checkNull(String v_wordsFoundList, String to_add){
		if (v_wordsFoundList == "")
			v_wordsFoundList += to_add;
		else 
			v_wordsFoundList = v_wordsFoundList + "& " + to_add;
		return v_wordsFoundList;	   
	}
	
	public void compare(){
		ReadFileLinesIntoArray rf = new ReadFileLinesIntoArray();
		//			   String subject[] = rf.readFromFile("");
		//			   String object[] = rf.readFromFile("");
		//			   String relation[] = rf.readFromFile("");
	}
	
	public String[] findWornetEquivalents(String term){
		String[] synonyms = null;
		//query wornet
		//return all synonyms for the word			   
		return synonyms;	   
	}
	
	//check if all list elements are in sentence string
	public static boolean stringContains(String str, String[] list){
		
		boolean found = true;
		for(String l :list){
			//has_been_accepted - split into terms accouring to number of "_" and then compare
			//co-bdfl-delegate - this one just compare as it is, no processing reguired
			String adjacentwords[]; 
			if (l.contains("_")){
				adjacentwords = l.split("_");
				//check if all these terms are in sentence
				//if not set to false
				for(String a :adjacentwords){
					if(!a.contains(l)){
						found = false;
						return false;
					}
				}
			}
			
			if(!str.contains(l)){
				found = false;
				return false;
			}
		}
		
		return found;
	}
	
	//check if all sentence starts conditional terms, like if
	public static boolean sentenceIsConditional(String v_CurrentSentenceString, String [] conditionalList){
		boolean conditional = false;
		for (String q : conditionalList) {
			//System.out.println(q);
			if(v_CurrentSentenceString.toLowerCase().startsWith(q)){
				//						System.out.println("QUESTION Sentence: " + v_CurrentSentenceString);
				//add Maybe
				conditional=true;
				break;
			}
		}
		return conditional;
	}
	
	//check if all sentence contains conditional terms, like if
	/*
	   public static boolean sentenceIsForAnotherPep(String v_CurrentSentenceString){
		   
		   boolean IsForAnotherPep = false;	
		   List<String> v_searchKeyList = new ArrayList<String>();
		   List<String> v_ignoreList = new ArrayList<String>();		   
		   Integer i = null;		   
		   String regex = "\\d+";
			  
		  v_searchKeyList.clear();
		  v_searchKeyList.add("PEP" + ": " + regex);		  v_searchKeyList.add("PEP" + regex + " ");		  v_searchKeyList.add("PEP " + regex + " ");		  v_searchKeyList.add("PEP " + regex + "\\?");
		  v_searchKeyList.add("PEP " + regex + "\\."); 		  v_searchKeyList.add("PEP " + regex + "\\,"); 		  v_searchKeyList.add("PEP " + regex + "\\;");		  v_searchKeyList.add("PEP " + regex + "\\:");
		  v_searchKeyList.add("PEP " + regex + "\\[");		  v_searchKeyList.add("pep-" + regex + ".txt");
		  v_ignoreList.clear();		  v_ignoreList.add("PEP: " + regex + ".");		  v_ignoreList.add("PEP: " + regex + "..");		  v_ignoreList.add("PEP: " + regex + "...");	
	
		  Matcher m5 = null;
		   
		  for (String pattern : v_searchKeyList){
	          Pattern p5 = Pattern.compile(pattern);	           m5 = p5.matcher(v_CurrentSentenceString);  
	     	  while (m5.find()) //  while (m.find() || m9.find()) 
	     	  {
	     		           			  
	     		  	  Matcher m5a = null;            		  	  
	     		  	  for (String pattern2a : v_ignoreList){               //so many combinations we cant store e.g. PEP 312, if we looking for PEP: 3 only
	     		  		  Pattern p2a = Pattern.compile(pattern2a);
	     		  		  m5a = p2a.matcher(v_CurrentSentenceString);  
	     		  		  while (m5a.find()) //  while (m.find() || m9.find()) 
	     		  		  {
	     		  			IsForAnotherPep = true;
	         		  			System.out.println("another epe found " + pattern + " in "+v_CurrentSentenceString);
	     	  			  }
	     		  	  }   
	     		  	  
	     		  //end added
	     		      if (IsForAnotherPep == true){           		      
		            		
	    		      }	  
	     	   } //end while            	  
	         } //end for
		   
		   return IsForAnotherPep;
   }
	*/
	
	//some  message belong to other proposals but because they have been mentioned in a message they are included
	//one way to eliminate this is to remove those where current message is at the end of message
	public boolean proposalAtTheEndofMessage(String message, int proposal) {
		boolean anotherProposalInMessage=false, proposalAtTheEndofMessage= false, allowProposalZero = false;;
		
		//proceed only if another pep is mentioned in message, else do nothing
		if (checkTextContainsProposalNumber_WithOptions(message, proposal, 2, allowProposalZero)) {	//option 2 - just to find out of another proposal exists
			//find location of current pep, if towards the end of message, then leave it out
			//towards the end id decided if its in the last 10 percent of message
			Integer counter=0;	
			for( String part : message.split("(?<=\\G.{10})")) {
				//find if current pep is mentioned in message part
				if (checkTextContainsProposalNumber_WithOptions(part, proposal, 1, allowProposalZero)) {
					if (counter < 9)		//if current proposal is found in the first 90 percent of message, then return true
						return true;
				}	
				counter++;
			}
		}
		else {
			//message either contains no peps or only current pep
		}
		
		return false;
	}
	
	//USAGE:  This function can be used for subject line, sentence and even entire message, use this to narrow down the list of peps a string can belong to
	//get the list of pep number from subject line.
	//sometimes no pep number would be mentioned in subject therefore have to asign to all pep number in message, or maybe take a deeper analysis
	
	//OPTIONS: Option 0: make sure string passed doesn't contain other proposals (option 0) and  - returns true if it does 
	//         Option 1: it contains only current pep (option 1)	- returns true if it does
	//		   Option 2: if another pep exists, regardless if current pep exists, 
	
	//new use for the below function - check to see if text contains current pep of other pep
	// to be used when triples are extracted but they contains other pep, like "pep 426" "was" "accepted" - but the sentence is for pep 318..
	//if contains both, then return true
	
	//main script for checking..
	// feb 2018 updated for instances where in a message subject there are multiple peps mentioned
	// initial use, see if we need to process the sentence, as it may be for another pep - identified by having only another pep
	// if contains no peps - fine - return true
	// if only this pep - fine - return true
	// if contains one pep - which is other pep, different from this one - not okay - return false 
	// if contains multiple peps - none is this pep, not okay - return false
	// if contains multipe peps - one is this one - okay - return true	
	// there is a test script for this function..which outputs test only on message subjects and can be used for all sentences in message but may be overkill
	// mar 2018 update pep 0 is updated sometimes when several peps are updated at the same message but these peps are not included in subject
	// so we add a piece of code to allow pep 0 to be not counted as a single pep when there is only pep 0 mentioned..line 626
	//late march 2018 update
	
	//FULL EXPLANATION HERE
	//sometimes we want a function similar to this one but it should check adn make sure only the current pep is mentioned
	//we can code everything in this function with these options
	//   0 -- only other peps, 
	//   1 -- only current pep, 
	//   2 - other pep exists, regardless if current pep exists, 
	//   3 (may 2018)- current pep exists, regardless if other pep exists,
	//	 boolean allowProposalZero -- allow pep 0 as several peps  have pep zero modified and therefore included in their message subjects 
	// 	 -- message subject or message body -- not needed
	
	//june 2018 ..added pep title matching in message subject. get the ppe number and get title using that number and match it in the msq subject
	public static boolean checkTextContainsProposalNumber_WithOptions(String v_sentence, int v_pep, int option, boolean allowProposalZero) 	//all text passed in as sentence is lowercased,
	{
		v_sentence = v_sentence.toLowerCase();	//System.out.println("v_sentence " + v_sentence);
//$$		System.out.println("Proposal To Match: " + v_pep);
		boolean shouldProcess = false, IsForAnotherPep = false;
		List<Integer> PEPList = new ArrayList<Integer>();
		List<String> v_searchKeyList = new ArrayList<String>(), v_ignoreList = new ArrayList<String>();
		Integer i = null;
		String regex = "\\d+"; // v_pep.toString().toLowerCase(); // "\\d+";	//d is a digit (a character in the range 0-9), and + means 1 or more times. So, \d+ is 1 or more digits.
		String regexIgnore = "\\d";
		
		if(v_sentence.isEmpty() || v_sentence==null || v_sentence.length()==0 || v_sentence.equals(""))
			return false;	//allow proceed
		
		v_searchKeyList.clear();
		v_searchKeyList.add("pep" + ": " + regex);		v_searchKeyList.add("pep" + " : " + regex);		v_searchKeyList.add("pep" + regex);
		v_searchKeyList.add("pep" + regex + " ");		v_searchKeyList.add("pep " + regex + " ");		v_searchKeyList.add(" pep" + regex + " ");		
		v_searchKeyList.add("pep " + regex + "\\?");	v_searchKeyList.add("pep " + regex + "\\."); 	v_searchKeyList.add(" pep-" + regex + " ");		v_searchKeyList.add(" pep-" + regex);		
		v_searchKeyList.add("pep " + regex + "\\,"); 	v_searchKeyList.add("pep " + regex + "\\;");	v_searchKeyList.add(" pep-" + regex + ": ");		
		v_searchKeyList.add("pep " + regex + "\\:");	v_searchKeyList.add("pep " + regex + "\\[");	v_searchKeyList.add(" pep " + regex + ": ");		   
		v_searchKeyList.add("pep-" + regex + ".txt");	v_searchKeyList.add("pep " + regex);
		//pep-0008.txt,1.26,1.27
		v_searchKeyList.add("pep-..." + regex+".txt");	v_searchKeyList.add("pep-..." + regex);
		// for cases when we want to extract pep 308 from ....[  pep-0308.txt, ]  ..similar to above regex
		v_searchKeyList.add("pep-.." + regex+".txt,");	v_searchKeyList.add("pep-0" + regex);
		//since we are doing for three and one space, might as wll do for 2 spaces , just in case, so we add these regex also
		v_searchKeyList.add("pep-.." + regex+".txt");	v_searchKeyList.add("pep-.." + regex);
		
//		v_ignoreList.clear();
//		v_ignoreList.add("pep: " + regexIgnore + ".");	v_ignoreList.add("pep: " + regexIgnore + "..");		
//		v_ignoreList.add("pep: " + regexIgnore + "...");	
		//v_ignoreList.add("pep ." + regex + ".");	//IGNORE PEP 484 when we looking for pep 8
		
		//for cases like pep 308)  v_ignoreList.add("pep: " + regex + "(");
		
		//first match proposal title
		GetProposalDetailsWebPage gpd = new GetProposalDetailsWebPage();
		MysqlConnect mc = new MysqlConnect(); 
		Connection conn = mc.connect();
/*		String proposalTitle = gpd.getPepTitleForPep(v_pep, conn);		
		if(v_sentence.contains(proposalTitle)) {
			PEPList.add(v_pep);
		}
				
		// initialise proposal numbers, in proposalNumberSearchKeyList and ignoreList
		initproposalNumberSearchKeyLists(ignoreList, proposalNumberSearchKeyList);
		//show all patterns being used to match
		for (String proposal : proposalNumberSearchKeyList) {
			//System.out.println(proposal);
		} */
		
		// initialise proposalTitles
		proposalTitlesSearchKeyList.clear();
		boolean foundTitle=false;
		// map to store proposal titles and numbers, WE Have to pre-poipulate this if we want to use this feature
		Map<Integer, String> proposalDetails = gpd.populateAllproposalTitleandNumbers(new HashMap<Integer, String>(), conn);
//$$		System.out.println("\tMatching all proposal titles in message subject : "+ v_sentence);
		for (Map.Entry<Integer, String> entry : proposalDetails.entrySet()) {	
			if(v_sentence.contains(entry.getValue())) {
				PEPList.add(entry.getKey());
//$$				System.out.println("\t\tFound Proposal Number , title : "+ +entry.getKey() + "/" + entry.getValue());//show all proposal titles
				foundTitle= true;
			}
		}
		if(!foundTitle) {
//$$			System.out.println("\t\tCouldn't Match any Proposal title in message subject ");
		}
		
		
		// MATCH and ADD ALL PEPs to List ....return all pep numbers mentioned in subject
		List<Integer> PEPListinSubject = new ArrayList<Integer>();
		Boolean store_pep = true,matched = false;		Matcher m5 = null;
		for (String pattern : v_searchKeyList) {			
			Pattern p5 = Pattern.compile(pattern);
			m5 = p5.matcher(v_sentence);
			while (m5.find()){ // while matched	
//$$				System.out.println("\t\t\t pattern matched [" + pattern+"]");
				boolean Store = true;
				Matcher m5a = null;
				//we dont want patterns from ignorelist, so many combinations, we cant store e.g. PEP 312, if we looking for PEP: 3 only
//				for (String pattern2a : v_ignoreList) { 
//					System.out.println("\t\t\t ignore pattern [" + pattern2a+"]");
//					Pattern p2a = Pattern.compile(pattern2a);					
//					m5a = p2a.matcher(v_sentence);
//					while (m5a.find()){ // while (m.find() || m9.find())					
//						store_pep = false;
//						System.out.println("dont store pattern " + pattern + " line " + v_sentence);
//					}
//				}

				//if still true, meaning we found some peps in message and those were not for the case when (PEP 312, if we looking for PEP: 3 only), but doesnt cater for cases where two peps exist and one is current pep we
				//if (store_pep == true) {	 
					//foundInFile = true;					
					matched = true; // if searchkey found, set matched to true
					String pepString,str = pattern;					Integer pep;
					//str = str.replaceAll("[^-?0-9]+", " ");
					pepString = m5.group(0).replace("pep", "");
//					System.out.println("\tMATCHED pepString (" + pepString + ")"  );
					pepString = pepString.replace(".txt", "").replace("pep-", "").replace(" ", "");			pepString = pepString.replaceAll("[,.:;!?(){}\\[\\]<>%]",""); // remove punctuation
//					System.out.println("\tMATCHED pepString (" + pepString + "), adding to List. Sentence: "+v_sentence);
					Pattern p = Pattern.compile("[a-z]");	//make sure the string contains numbers
					if (p.matcher(pepString).find()) {
						continue;
					}
					else {
						pep = Integer.parseInt(pepString);
						if(pep<0) { pep = pep * -1; }	//sometimes - minus signs exists					
						PEPList.add(pep); // add pep to list
//$$						System.out.println("\t\t\t\tadded PEP to list " + pep);
					}
				//}
			} // end while
		} // end for
//		System.out.println("\nPEPList In Sentence");
		for(Integer p: PEPList) {
//			System.out.println(p);
		}
		
		//remove duplicates
		List<Integer> al = new ArrayList<>();// add elements to al, including duplicates		
		Set<Integer> hs = new HashSet<>();		hs.addAll(PEPList);		al.clear();		al.addAll(hs);		
		// just for debugging code
		// 		System.out.println("PEPList " + PEPList.toString()); 		System.out.println("PEPListinSubject " + PEPListinSubject.toString());

		// CHECKING DONE HERE - should be
		//FIRST SECTION options
		//   0 -- only other peps, 1 -- only current pep, 
		
		//SECOND SECTION
		// if contains no peps - fine - return true
		// if only this pep - fine - return true
		// if contains one pep - which is other pep, different from this one - not okay - return false 
		// if contains multiple peps - none is this pep, not okay - return false
		// if contains multipe peps - one is this one - okay - return true
		
		//Most of teh time, we are only concerned with the fact taht we dont want only any otehr pep to be mentioned in the sentence. Option 0 is for that.
		//This block is fine and works for both options 0 and 1, if the list is empty,
		// it doesnt contain other proposals (option 0) and it also doent contain current pep (option 1)
		
		//march 2019, we had to add a modifier that if sentences like these occur we skip 'This PEP is rejected in favor of PEP 343.'
		
		if (option==0) {	//System.out.println("\tChecking For option 0 : only other peps");
			if (al.isEmpty()) {
				//shouldProcess = true;	
//				System.out.println("\tNo peps found in message, v_subject " + v_sentence);
				return false;	//return that the pep is not only for another pep - checked this is correct way, as we just want to check instances of just another pep
			} 
			else if (al.size()==1) {	//if just one pep found in text - HAPPENS MOST OF THE TIME
//				System.out.println("\t\t one pep found in message " + al.get(0));
				//if just one pep number is mentioned and its pep 0, we process that text, and so retrun true 		
				if (al.get(0).equals(0)) {
					//return false;
					if(allowProposalZero)
						return false;	//return that the pep is for pep 0, which we want to allow, not other than that
					else { //System.out.println("here 12");
						return true; }
				}
				//if just one pep number is mentioned and its current pep, we process that text, and so retrun true
				else if(al.get(0).equals(v_pep)){
//					System.out.println("\tOne PEP found  = Current pep: "+al.get(0));
					// boolean allowProposalZero -- allow pep 0 as several peps  have pep zero modified and therefore included in their message subjects 
					//but in some cases we dont want to allow pep 0
					return false;
				}
				else if (al.get(0) != v_pep){
//					System.out.println("\tOne PEP found  = Another pep: " + al.toString());		
					
					//march 2019, if a self reference is made, retrun false
					for (String f : selfRef) {
						if(v_sentence.toLowerCase().contains(f)) {
							return false;
						}	
					}
					return true;	//return that the pep is for another pep - checked this is correct way, as we just want to check instances of just another pep
				}
			}
			else if (al.size()>1){		//if multiple peps found in text
//				System.out.println("\tMultiple peps found in text");
				for(int p : al) {	//iterate over all peps in list
					if (p==v_pep){	//al.get(0)==v_pep
//						System.out.println("\tBut our current PEP found, so we process");				
						return false;	//return that the pep is not only for another pep, in this case it contains current pep also			
					}
				}
				//else we return that the message is only for another pep, as its contains multiple, but none of it is current pep 
				//System.out.println("here 13"); 
				return true;
			}
			else {
//				System.out.println("\tElse");
			}
		}
		else if (option==1) {	//System.out.println("\tChecking For option 1: only current pep,"); 
			if (al.isEmpty()) {
//				System.out.println("\tNo peps found in message, v_subject " + v_sentence);
				return false;	//return false as that the current pep is not found 
			} 
			else if (al.size()==1) {	//if just one pep found in text - HAPPENS MOST OF THE TIME
//				System.out.println("\t\t one pep found in message " + al.get(0));
				//if just one pep number is mentioned and its pep 0, we return false ...in the previous section we allowed pep 0		
				if (al.get(0).equals(0)) {
					return false;
				}
				//if just one pep number is mentioned and its current pep, we process that text, and so retrun true
				else if(al.get(0).equals(v_pep)){
//					System.out.println("\tOne PEP found  = Current pep: "+al.get(0));				
					return true;	//the current pep is found 
				}
				else if (al.get(0)!=v_pep){
//					System.out.println("\tOne PEP found  = Another pep: " + al.toString());				
					return false;	//the current pep is not found 
				}
			}
			else if (al.size()>1){		//if multiple peps found in text..we can just return false if multiple peps are found..but the following code should be be full proof i guess
//				System.out.println("\tMultiple peps found in text");
				for(int p : al) {	//iterate over all peps in list
					if (p!=v_pep){	//al.get(0)==v_pep
//						System.out.println("\tBut our current PEP found, so we process");				
						return false;	//multiple peps found and one is not current pep --as soon as any other pep is found we return false		
					}
				}
				//else we return that the pep is only for current pep
//				System.out.println("here 15");
				return true;
			}
			else {
//				System.out.println("\tElse");
			}
		}
		else if (option==2) {	//System.out.println("\tChecking For option 2 : other pep exists, regardless if current pep exists,"); 
			if (al.isEmpty()) {
//				System.out.println("\tNo peps found in message, v_subject " + v_sentence);
				return false;	//return false as that the current pep is not found 
			} 
			else if (al.size()==1) {	//if just one pep found in text - HAPPENS MOST OF THE TIME
//				System.out.println("\t\t one pep found in message " + al.get(0));
				//if just one pep number is mentioned and its pep 0, we return false ...in the previous section we allowed pep 0		
				
				//if just one pep number is mentioned and its current pep, we process that text, and so retrun true
				if(al.get(0).equals(v_pep)){
//					System.out.println("\tOne PEP found  = Current pep: "+al.get(0));				
					return false;	//the current pep is found , therefore other pep is not found
				}
				else if (al.get(0)!=v_pep){
//					System.out.println("\tOne PEP found  = Another pep: " + al.toString());				
					return true;	//the other pep is  found 
				}
			}
			else if (al.size()>1){		//if multiple peps found in text..we can just return false if multiple peps are found..but the following code should be be full proof i guess
//				System.out.println("\tMultiple peps found in text");
				for(int p : al) {	//iterate over all peps in list
					if (p!=v_pep){	//al.get(0)==v_pep
//						System.out.println("\tBut our current PEP found, so we process");				
						return true;	//multiple peps found and one is not current pep --as soon as any other pep is found we return true		
					}
				}
				//else we return that the message is only for current pep
				return false;
			}
			else {
//				System.out.println("\tElse");
			}
		}
		else if (option==3) {	//System.out.println("\tChecking For Option 3: current pep exists, regardless if other pep exists"); 
			if (al.isEmpty()) {
//				System.out.println("\tNo peps found in message, v_subject " + v_sentence);
				return false;	//return false as that the current pep is not found 
			} 
			else if (al.size()==1) {	//if just one pep found in text - HAPPENS MOST OF THE TIME
//				System.out.println("\t\t one pep found in message, Proposal: " + al.get(0));
				//if just one pep number is mentioned and its pep 0, we return false ...in the previous section we allowed pep 0		
				
				//if just one pep number is mentioned and its current pep, we process that text, and so retrun true
				if(al.get(0).equals(v_pep)){
//					System.out.println("\tOne PEP found  = Current pep: "+al.get(0));				
					return true;	//the current pep is found , therefore other pep is not found
				}
				else if (al.get(0)!=v_pep){
//					System.out.println("\tOne PEP found  = Another pep: " + al.toString());				
					return false;	//the other pep is  found 
				}
			}
			else if (al.size()>1){		//if multiple peps found in text..we can just return false if multiple peps are found..but the following code should be be full proof i guess
//				System.out.println("\tMultiple peps found in text");
				for(int p : al) {	//iterate over all peps in list
					if (p==v_pep){	//al.get(0)==v_pep
//						System.out.println("\tBut our current PEP found, so we process");				
						return true;	//multiple peps found and one is current pep --as soon as any current pep is found we return true		
					}
				}
				//else we return that the message is only for another pep
				return false;
			}
			else {
//				System.out.println("\tElse");
			}
		}
		return shouldProcess;
	}
	
	//for some reason puuting it here does not work, so put in main
	public static void whetherToProcessSentence(String v_CurrentSentenceString, Integer v_m_ID, Integer v_pep)
	{
		// System.out.println("v_pep: " + v_pep+" v_m_ID: "+v_m_ID+" CurrentSentenceString: "+v_CurrentSentenceString);	
		//			   System.out.println("-------------check sentence after match found"); 
		//			   boolean v_process =false;
		//v_process = true;
		
		if(!localCandidateSentencesList.isEmpty()){
			//					System.out.println("localCandidateSentencesList NOT EMPTY ");	
			for (String temp : localCandidateSentencesList) {
				//					   System.out.println("1");
				if( v_CurrentSentenceString.toLowerCase().equals(temp) )
				{ 	
					//v_process= false;
					//							System.out.println("SENTENCE FOUND in list");
					//return false;						 				 	
				}						
			}					
		}
		else {
			//					System.out.println("SENTENCE NOT FOUND in list and added ");
			localCandidateSentencesList.add(v_CurrentSentenceString.toLowerCase());
			//v_process = true;
			//return true;
		}
		//return false;
		
		//System.out.println("v_process: "+v_process);	
		//return v_process;
	}
	
	public static String ExtractMessageAuthorRole(GetProposalDetailsWebPage pd, Integer v_pep_number, String messageAuthor){
		Boolean found = false;
		String pepAuthors = null;
		String pepBDFLDelegates = null;	   
		//			   System.out.println("Inside method, "); 
		
		//assign default role
		// messageAuthorRole mar = messageAuthorRole.community_member;
		
		String theRole = "community_member";			   	
		try
		{
			
			//we have to figure out 3 roles.
			//1. The author of the message. When he proposes and when he updates, call for poll, vote.
			//2. The BDFL - when he approves, calls for revision, revises himself
			//3. The BDFL Delegate, if one is created in the PEP.
			
			//see which one the messageAuthor matches
			
			//1. AUTHOR
			//use pep number to query author from database (from website)
			//if messageAuthor similar to pep author in db
			//role = author	   
			
			pepAuthors = pd.getPepAuthor(v_pep_number);
			
			String authors[] = null;
			
			//			    System.out.println("messageAuthor: " + messageAuthor);
			//			    System.out.println("pep author:    " + pepAuthors );
			
			//			    System.out.println("here 1");	
			
			//pepAuthors.isEmpty() ||
			if (pepAuthors ==null){
				//do nothing
			}
			else{	    
				// Guido van Rossum, Raymond Hettinger
				if (pepAuthors.contains(","))
				{
					authors = pepAuthors.split(",");
					//System.out.println("messageAuthor: " + messageAuthor + " | pep author: "+ pepAuthors );	
					//				    	System.out.println(" multiple authors of pep: ");
					for(String author : authors){
						//				    		 System.out.println(" author: " + author);	    		 
						//check if ,messageauthors is one from the list	    		 
						if( checkStrings(author,messageAuthor)){
							theRole = "author";
							//				    			 System.out.println("role is author"); 
						} else{
							//	    			 System.out.println(" diff authors messageAuthor different for pep author");
							//	    			 System.out.println(" messageAuthor is not actual pep author");
						}
						//	 	    	 System.out.println("If true, then matched, else false " + d);	    		 
					}
				}	    
				else{
					//				    	System.out.println(" single author of pep: "+ pepAuthors);	    	
					if(checkStrings(pepAuthors,messageAuthor)) 	{
						theRole = "author";
						//				    		 System.out.println("role is author");
					}
				}
			}
			//			    System.out.println("here 2");	    
			
			//2. BDFL DELEGATE
			//get delegate from database
			//try and find the dates when he was made delegate
			//if this message takes place after he was made dleegate, then add delegate in front
			pepBDFLDelegates = pd.getPepBDFLDelegate(v_pep_number);	
			//				System.out.println("here 3");
			//					//pepBDFLDelegates.isEmpty() ||
			if (pepBDFLDelegates == null )
			{
				//do nothing
				//					System.out.println("here 4");
			}
			else{
				//				    System.out.println("here 4");		    		
				if (pepBDFLDelegates.contains(","))
				{
					//				    	System.out.println("here 5");
					authors = pepBDFLDelegates.split(",");
					//				    	System.out.println("messageAuthor: " + messageAuthor + " | pep author: "+ pepBDFLDelegates );	
					//				    	System.out.println(" multiple pepBDFLDelegates of pep: ");
					for(String a: authors){
						//				    		 System.out.println(" delegate: " + a);  		 
						if(checkStrings(a,messageAuthor)) {
							theRole = "bdfl_delegate";
							//				    			 System.out.println("bdfl_delegate");
						} else {
							//				    			 System.out.println(" multiple delgates, but none delegates matched with this messageAuthor");
						}	    		 
					}
				}	    
				else{
					//				    	System.out.println("here 6");
					//				    	System.out.println(" single delegate  of pep: "+ pepAuthors);	    	
					if(checkStrings(pepBDFLDelegates,messageAuthor)) {
						theRole = "bdfl_delegate";
						//				    		System.out.println("bdfl_delegate");
					} else {
						//			   			 System.out.println(" single delgate but not matched with this messageAuthor");
					}
				}
			}
			
			//2. BDFL
			//if message author contains guido, etc
			if (messageAuthor.toLowerCase().contains("guido") || messageAuthor.toLowerCase().contains("gvr")){
				pepAuthors = "BDFL";
				//	mar = messageAuthorRole.bdfl;
				theRole = "bdfl";
			}
			
			//			    System.out.println("here 7");
		}
		catch(Exception e){
			showExceptions(e);
		}
		
		System.out.println(" FINAL ROLE IS: " +   theRole);		
		//role shud just be bdfl, bdfl-delegate or author		
		return theRole;
	}
	
	public static void showExceptions(Exception e){
		System.out.println("Exception " + e.toString());
		System.err.println("1");
		System.err.println(e);
		System.err.println("\n2");
		String s2;
		s2 = e.getMessage();
		System.err.println(s2);
		System.err.println("\n3");
		String s1;
		s1 = e.getLocalizedMessage();
		System.err.println(s1);
		System.err.println("\n4");
		Throwable t1;
		t1 = e.getCause();
		System.err.println(t1);
		System.err.println("\n5");
		String s3;
		StackTraceElement[] es1;
		es1 = e.getStackTrace();
		s3 = Arrays.toString(es1);
		System.err.println(s3);
		System.err.println("\n6");
		e.printStackTrace();
	}
	
	//check for any terms match in both strings
	public static Boolean checkStrings(String actual_pep, String current_message){
		
		try {
			String actual_terms[] = actual_pep.split(" ");
			String current_terms[] = current_message.split(" ");
			
			for(String s: actual_terms){
				for(String t: current_terms){
					if (s.toLowerCase().contains(t.toLowerCase()))
						return true;			   
				}		   
			}
		}
		catch (Exception ¢) {
			System.out.println("Error " + ¢);
		}
		return false;	   
	}
	
	//return the next sentence
	public static String returnNextSentenceFromParagraph(String g,String CurrentSentenceString ){
		String nextSent = "";
		Reader reader = new StringReader(g);
		DocumentPreprocessor dp = new DocumentPreprocessor(reader);
		boolean found = false;
		for (List<HasWord> eachSentence : dp) {
			String currSent = Sentence.listToString(eachSentence);
			if (found==true){
				return currSent;
			}
			
			if (currSent.equals(CurrentSentenceString)){
				found = true;
			}
		}		
		return "";	   
	}
	
	//after every pep, clear te arraylist
	public static void outputAndClearCandidateSentenceList(ArrayList<TripleProcessingResult> tripleProcessingResult){
		System.out.println("Candidate Sentencelist");
		//					    for (String temp : candidateSentencesList) {	
		//								System.out.println(temp);	
		//						}
		try
		{
			if (tripleProcessingResult != null)
			{ 
				for (int y=0; y <tripleProcessingResult.size(); y++){
					System.out.println(String.format("%-20s %s" , tripleProcessingResult.get(y).label.toLowerCase(), tripleProcessingResult.get(y).currentSentence.toLowerCase()));
					//System.out.println(canSen.get(y).label.toLowerCase()+" : "+canSen.get(y).sentence.toLowerCase());				
				}
			}
			else{
				System.out.println("TripleProcessingResult NOT Empty");
			}
		}
		catch(NullPointerException e){
			System.out.println("NullPointerException..101");
		}
		
		//clear sentence list after each pep
		//canSen.clear();
	}		   
	
	// the cut discussion code
	/*
	 * if (discussionOn == true){ //if there was a discussion going oin until
	 * now //set discussion = false; //set wordfound list to discussion //
	 * output date, discussion, all //VVV DISCUSSION
	 * System.out.println(outputDiscussionDate + ", All , " + "Discussion," + " " + message_ID); //now output with all details discussionOn = false;
	 * //set to false to start over outputDiscussion = false; //has the
	 * discussion info already been output? - set output to false - }	 * 
	 * discussionOn = false; discussionMessageCounter=0;
	 */
	
	// cut p array code
	// p[ System.out.println(
	// "------------------------------------------------------" );
	// for (int r=0;r<1;r++){
	// for (int s=0;s<1;s++){
	// System.out.println("idea and sentence ..Message id in DB " +
	// p[pepNumber].messages[counter].getMessageIdOfMessageInDB());
	// p[pepNumber].messages[counter].getAllIdeasSentence();
	
	// }
	// }
	
	// cut code of last part after output storyline relating to discussion
	/*
	 * else {		    	  
				    	   // if message exists and is not captured in above set, it is set as discussion 
				    	   if (discussionOn == false) {
				    		 //  discussionStart = true;
				    		   //check if they are having discussion
				    		   discussionOn = true;
				    		   discussionMessageCounter=1;
				    		  // discussionEnd = true;
		//VVV		    		   System.out.println(v_date + ", " + author  + " , " + " " + "Discussion," + " " + message_ID);
				    	   }
				    	   //if message exists and not captured in above set, it is set as discussion but as well as is part of exiisting discussion
				    	   else if (discussionOn == true) {
				    		   discussionMessageCounter++;
				    		   if(discussionMessageCounter ==5) {
				    			   outputDiscussion = true;     // the discussion info already been output
					    		   outputDiscussionDate = v_date;   // set the date to first instance after 5 messages
				    		   }
				    		   else if(discussionMessageCounter >5) {
//				    			   System.out.println(v_date + ", " + author  + " , " + " " + "Discuission," + " " + message_ID);
		//VVV		    		   System.out.println("discussionOn == true -----------------discussionMessageCounter " + discussionMessageCounter + " , " + v_date + ", " + author  + " , " + " " + "Discussion," + " " + message_ID);
				    		   outputDiscussion = true;          // the discussion info already been output
				    		   
				    		   //set discussion date and message gist = discussion
				    		   
				    		   }
				    		//   discussionEnd = true;
				    		   //store discussion end date as new date and output at end
				    		  // discussionOn = true;
				    		//   System.out.println(v_date + ", " + author  + " , " + " " + "Discuission" + " " + message_ID);
				    	   }
				       }
	 */
	public void finalize() {	    //System.out.println("Probability instance is getting destroyed"); 
	}
}
