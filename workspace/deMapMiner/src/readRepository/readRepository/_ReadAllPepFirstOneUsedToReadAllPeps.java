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

public class _ReadAllPepFirstOneUsedToReadAllPeps 
{
	 // JDBC driver name and database URL
//  static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
//  static final String DB_URL = "jdbc:mysql://localhost/peps";

//    //  Database credentials
//  static final String USER = "root";
//  static final String PASS = "root";
    
  public static File dev_folder = new File("C:/data/python-dev/");
  public static File patches_folder = new File("C:/data/python-patches/");
  public static File lists_folder = new File("C:/data/python-lists/");
  public static File bugs_list_folder = new File("C:/data/python-bugs-list/");
  public static File committers_folder = new File("C:/data/python-committers/");
  public static File checkins_folder = new File("C:/data/python-checkins/");
  public static File ideas_folder = new File("C:/data/python-ideas/");
  public static File announce_folder = new File("C:/data/python-announce-list/");
  public static File distutils_sig_folder = new File("C:/data/python-distutils-sig/");
  
  public static File gmane_folder = new File("C:/scripts/gmanetxtd/");
  //when only few messages to be read in 
  //new db table = allpeps_gmane
  //but when reading in next timw with all data, try have diff db with same tablename = allpeps, as select statement are coded.
  public static File few_folder = new File("C:/scripts/fewMsgtxt/");
    
  public static File dev_folder2 = new File("C:/data/dev/");
  
  static MysqlConnect mc = new MysqlConnect(); 
  static Connection conn = mc.connect();
  
  static String temp = "";
  //static String searchKey = "PEP 301 ";sss
  static String searchKeyAll = null;
  //static String searchKeyList = null;
  
  static List<String> searchKeyList = new ArrayList<String>();
  static List<String> wordsList = new ArrayList<String>();
  static List<String> ignoreList = new ArrayList<String>();
  static List<String> statusList = new ArrayList<String>();
   
  static int fileCounter = 0;                  //total number of files with searchKey 
 // static HashMap hm = new HashMap();
  
  static boolean foundInFile = false;
  
  static Integer messageID = 0; 
  
  static List<Integer> PEPList = new ArrayList<Integer>();
  
  static long t0,t1, t2, t3, t4, t5, t6, t7, t8;

  public static void main(String[] args) throws IOException {
	  
	//  JOptionPane.showMessageDialog( null, "Hello World!" );
	  emptyDatabase_gmane(conn);
	  init(searchKeyList);
	  initWordsList(wordsList);  //the words we looking for tagging like vote , BDFL
	  String regex = "\\d+";
	  int i = 0;
	  
		  searchKeyList.clear();
		  searchKeyList.add("PEP" + ": " + regex);
		  searchKeyList.add("PEP" + regex + " ");
		  searchKeyList.add("PEP " + regex + " ");
		  searchKeyList.add("PEP " + regex + "\\?");
		  searchKeyList.add("PEP " + regex + "\\."); 
		  searchKeyList.add("PEP " + regex + "\\,"); 
		  searchKeyList.add("PEP " + regex + "\\;");
		  searchKeyList.add("PEP " + regex + "\\:");
		  searchKeyList.add("PEP " + regex + "\\[");
		  
		  searchKeyList.add("pep-" + regex + ".txt");
		  
		  ignoreList.clear();
		  ignoreList.add("PEP: " + regex + ".");
		  ignoreList.add("PEP: " + regex + "..");
		  ignoreList.add("PEP: " + regex + "...");	
		 // ignoreList.add("PEP " + regex + "\\.");
		
		
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
	    
	    
	  //  searchFilesForFolder(dev_folder2, dev_folder2, foundInFile, searchKeyAll,fileCounter, w, wdebug, conn , i, searchKeyList, ignoreList, statusList, messageID, wordsList);
/*
	    t0 = System.currentTimeMillis();
	    //searchFilesForFolder(dev_folder2, dev_folder2, foundInFile, searchKeyAll,fileCounter, w, wdebug, conn , i, searchKeyList, ignoreList, statusList, messageID);
	    searchFilesForFolder(ideas_folder, ideas_folder, foundInFile, searchKeyAll,fileCounter, w, wdebug, conn , i, searchKeyList, ignoreList, statusList, messageID, wordsList);
	    t1 = System.currentTimeMillis();
	    System.out.println("Finished processing ideas folder - Elapsed time =" + (t1 - t0)/1000 + " seconds");
	 
	    searchFilesForFolder(dev_folder, dev_folder, foundInFile, searchKeyAll,fileCounter, w, wdebug, conn , i, searchKeyList, ignoreList, statusList, messageID, wordsList);
	    t2 = System.currentTimeMillis();
	    System.out.println("Finished processing dev folder - Elapsed time =" + (t2 - t1)/1000 + " seconds");
	    //searchFilesForFolder(patches_folder,patches_folder, foundInFile,  searchKeyAll,fileCounter, w ,wdebug,  conn, i, searchKeyList, ignoreList, statusList);
	    //System.out.println("Finished processing patches folder");
	    searchFilesForFolder(lists_folder,lists_folder, foundInFile,  searchKeyAll,fileCounter, w ,wdebug,  conn, i, searchKeyList, ignoreList, statusList, messageID, wordsList);
	    t3 = System.currentTimeMillis();
	    System.out.println("Finished processing lists folder - Elapsed time =" + (t3 - t2)/1000 + " seconds");
	    //searchFilesForFolder(bugs_list_folder,bugs_list_folder, foundInFile,  searchKeyAll,fileCounter, w ,wdebug,  conn, i, searchKeyList, ignoreList, statusList);
	    //System.out.println("Finished processing bugs lists folder");	    
	    searchFilesForFolder(distutils_sig_folder,distutils_sig_folder , foundInFile,  searchKeyAll,fileCounter, w , wdebug, conn, i, searchKeyList, ignoreList, statusList, messageID, wordsList);
	    t4 = System.currentTimeMillis();
	    System.out.println("Finished processing distutils_sig_folder folder - Elapsed time =" + (t4 - t3)/1000 + " seconds");  
	    searchFilesForFolder(committers_folder,committers_folder , foundInFile,  searchKeyAll,fileCounter, w , wdebug, conn, i, searchKeyList, ignoreList, statusList, messageID, wordsList);
	    t5 = System.currentTimeMillis();
	    System.out.println("Finished processing committers folder - Elapsed time =" + (t5 - t4)/1000 + " seconds");
	    searchFilesForFolder(announce_folder,announce_folder , foundInFile,  searchKeyAll,fileCounter, w , wdebug, conn, i, searchKeyList, ignoreList, statusList, messageID, wordsList);	    
	    t6 = System.currentTimeMillis();
	    System.out.println("Finished processing announce_folder list folder - Elapsed time =" + (t6 - t5)/1000 + " seconds");
	    searchFilesForFolder(checkins_folder,checkins_folder , foundInFile,  searchKeyAll,fileCounter, w , wdebug, conn, i, searchKeyList, ignoreList, statusList, messageID, wordsList);	
	    t7 = System.currentTimeMillis();
	    System.out.println("Finished processing checkins_folder folder - Elapsed time =" + (t7 - t6)/1000 + " seconds");
*/		    
	    //NOW READ GMANE FOLDERS - In Gmane there is different structure of each file
	    messageID = 100000;  //set message id to a number so woudnt conflict if pep archives and gmane archives are read separately
	    searchFilesForFolder_Gmane(gmane_folder,gmane_folder , foundInFile,  searchKeyAll,fileCounter, w , wdebug, conn, i, searchKeyList, ignoreList, statusList, messageID, wordsList);	

	    t8 = System.currentTimeMillis();
	    System.out.println("Finished processing gmane folder - Elapsed time =" + (t8 - t7)/1000 + " seconds");
	    
	    w.close();
	   // outputResults(conn);
//	  }
    //conn.close();
	  System.out.println("Finished processing");
  }
  
  public static void makeRowNULL(){
	  

	  
  }
  

  
  public static void searchFilesForFolder(final File folder, final File v_rootfolder, boolean v_foundInFile, String v_searchKey, int v_fileCounter, Writer v_outputFile, Writer v_debugFile, Connection v_conn, int i, List<String> v_searchKeyList, List<String> v_ignoreList , List<String> v_statusList, Integer v_messageID, List<String> v_wordsList) throws IOException 
  {	
	File rootFolder = v_rootfolder;
	for (final File fileEntry : folder.listFiles()) 
    {
      if (fileEntry.isDirectory()) {
        searchFilesForFolder(fileEntry, v_rootfolder, v_foundInFile, v_searchKey, v_fileCounter, v_outputFile, v_debugFile, v_conn, i, v_searchKeyList, v_ignoreList, v_statusList, v_messageID, v_wordsList);
      } 
      else 
      {        
    	  //For each File
    	  if (fileEntry.isFile()) {
          temp = fileEntry.getName();
          
          //start match          
          BufferedReader br=new BufferedReader(new FileReader(folder +  "\\" + fileEntry.getName()));
          String line;
          String line2;
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
          String statusFrom = null;
          String statusTo = null;
          String wordsFoundList = null;
          String emailMessageId = null;
          String inReplyTo = null;
          String references = null;
          String link = null;
          String analyseWords = null;
          String subject = null;
          String senderName = null;
          
          Integer lineCounter;
          Integer notrequiredLinesCounter;
          Boolean required = true; 
          
          Boolean keepOnReading = true;
          
          int withinFileCounter = 0;
          //For each line
          while((line=br.readLine())!=null)
          {      	  
    	      if (line.startsWith("Date: "))   date2 = line.replace("Date:","");
    	      else if (line.startsWith("Type: "))   type = line.replace("Type: ","");
    	      else if (line.startsWith("Author: ")) author = line.replace("Author: ","");    		  
    	      else if (line.startsWith("In-Reply-To: ")) { inReplyTo = line.replace("In-Reply-To: ",""); inReplyTo = inReplyTo.replaceAll("[,:;!?(){}\\[\\]<>%]", ""); } 
    	      else if (line.startsWith("References: "))  { references = line.replace("References: ",""); references = references.replaceAll("[,:;!?(){}\\[\\]<>%]", "");  }  //remove punctuation  
    	      else if (line.startsWith("Message-ID: "))  { emailMessageId = line.replace("Message-ID: ",""); emailMessageId = emailMessageId.replaceAll("[,:;!?(){}\\[\\]<>%]", "");  
//    	      System.out.println("emailMessageId" + emailMessageId);
    	      }  
    	      else if (line.startsWith("On ") && line.endsWith(" wrote:"))  link = line.replace("On ","");   //link that could be used - On 16 Jan 2014 11:45, "Carl Meyer" <carl at oddbird.net> wrote:    		  		  
    		//  if (line.startsWith("diff: ")) { 
    	      else if (line.startsWith("-Status: ")) statusFrom = line.replace("-Status: ",""); 
    	      else if (line.startsWith("+Status: ")) statusTo = line.replace("+Status: ",""); 
//    		  	  if ((line.startsWith("Status: ")) || (line.startsWith("\\+Status: "))) {
    	      else if (line.startsWith("From:")) {   senderName = getSender(line);    }
    	      
    	/*      else if (line.startsWith("Acceptance")) { 
    	    	  br.mark(10000000);  //mark that place 
    	    	  while((line2=br.readLine())!=null){
    	    		  if (line2.isEmpty() || line2.trim().equals("") || line2.trim().equals("\n")){
    	    			  
    	    		  }
    	    	  }
    	    	  br.reset(); //reset the mark to original 
    	      }
    	      */
    	      else if (line.startsWith("Subject:")) { 
    	   // 	  Integer mark = br.available();
    	    	//  System.out.println("here a");
    	    	  subject = line.replace("Subject: ","");  
    	    	  br.mark(10000000);  //mark that place 
    	    	  lineCounter =0;
    	    	  notrequiredLinesCounter =0;
    	    	  keepOnReading = true;
    	    	  while(keepOnReading == true && lineCounter < 10 && (line2=br.readLine())!=null)  { //keep reading line until come across Message id
    	    		 // System.out.println(br.markSupported());
    	    	//	  System.out.println("here b");
    	    		 // line2=br.readLine();
    	    		  if (matchQuery(line2, "Message-ID:") || matchQuery(line2,"References:") || matchQuery(line2,"In-Reply-To:")){
    	    			  keepOnReading = false;
    	    	//		  System.out.println("here k");
    	    		  }
    	    		  else { 
    	    	//		  System.out.println("here l " + lineCounter);
    	    			  lineCounter++; //increment line counter 
    	    		      if (line2.endsWith(".txt") || line2.endsWith(".py") || line2.endsWith(".js") || line2.endsWith(".css") || line2.endsWith(".ell")|| line2.endsWith(".rst") || line2.endsWith(".h") || line2.endsWith(".c") || line2.endsWith(".vcproj") || line2.endsWith(".vsprops") || line2.endsWith(".tex"))   //check all lines to see if they end with .c, .h, .rst etc
    	    			      notrequiredLinesCounter++;   // if so increment f counter
    	    		  }
    	    	  }  //end loop
    	    	  br.reset(); //reset the mark to original    	    	  
    	    	  float r = ((float) notrequiredLinesCounter/ (float) lineCounter);
    	    	//  System.out.println("notrequiredLinesCounter " + notrequiredLinesCounter + " lineCounter " + lineCounter + " " + r);
    	    	  if (r > 0.5)  { //if f/line > 90 percent
    	    		  required = false; // then dont read in message
    	    	//	  System.out.println("here c");
    	    	  }
    	    	   
    	      }
    //	      else if (line.startsWith("+")) {  }
    	      else {																								//|| !line.startsWith("+")
    	    	  if (!line.startsWith(">") && !line.startsWith("<") && !line.startsWith("=") && !line.startsWith("@")  && !line.endsWith("wrote:")) analyseWords = analyseWords + line + "\n";    //for classification    	    	  
    	      }
			  if ((matchQuery(line, "Status:")) || (matchQuery(line, "\\+Status:"))) {
    			  statusLine = line.replace("Status: ","");
    			  statusLine = line.replaceAll("[,:;!?(){}+-\\[\\]<>%]", "");  
    			  status = statusLine;
    			  StatusChanged = true;    			  
    		  }        		  
    		  //add here
			  //before the next message in file
    		  if (line.startsWith("From ")) {
        		  if (matched == true){        			  		
        			 Date date9 = null; 	        			         			 
 			  		  date9= findDate(date2);	        			 
        			  counter++;
        			  if (date9 != null) { 
        				  PEPList = removeDuplicates(PEPList, subject);     //remove duplicate PEPs        				  											
        				  PEPList = getPepNumberFromSubject_Match(PEPList, subject);     //try to find matching pep numbers in subject
        				//check in subject
    					  // if the current pep number in loop exist in subject , then add only those
    					  //else keep on adding        				  
        				   messageID++; 
        				   
        				   //POST PROCESSING For each message to be inserted in database
        				   //The following messages cannot be processed - these email Mesages are not to be analysed
        				   //therefore while reading the message in the database, the column used for analysis is set to null, while the message column is unchanged 
   				    	   if(emailMessage.length() > 1000000 || subject.contains("[Python-checkins]"))   //additional keywords like this are
   				  	  	   {
   				  		       //set the analysewords column to null 
   				    		   analyseWords = null;
   				  	  	   }
        				   
        				  for (int g = 0; g<PEPList.size();g++){																//g below = PEP number from list
        					  //insert into database
        					  storeInDatabase(counter, date9, subject, rootFolder , fileEntry.getName(),lastLine, emailMessage,PEPList.get(g), status, statusFrom, statusTo, type, author,senderName, inReplyTo, references, emailMessageId, link, StatusChanged, wordsFoundList, analyseWords, messageID, required, conn);
        					  storeInFile(v_outputFile, date9, rootFolder, fileEntry.getName(),lastLine, emailMessage, PEPList.get(g),status, statusFrom, statusTo, type, author, messageID);	
//        					  System.out.println("date2 " + date2 + " author " +  author +  " status " + status + " , " + " i " + i + rootFolder + fileEntry.getName() + " , " + line + " ");
//       				  	  	  System.out.println("Inserted into database PEP = " + PEPList.get(g));
        				  }
//        				  System.out.println("-------------End of One email message --------------------\n");
        			  }
        			  else{
        				  System.out.println("DATE NULL date2 " + date2 + " date9 " +  date9 + " , " + " i " + i + " " + rootFolder + "\\" + fileEntry.getName() + " , Line = " + line );
        			  }
        			  //emailMessage = null; //empty message once matched is true and from is found
        			  PEPList.clear();     //clear PEPList
        			  wordsFoundList = null;
        		  }        		  
        		  emailMessage = null; //empty message  when (matched is true & from is found) but also when not matched - meaning new message has started and searchkey was not found in last message
        		  matched = false;         //new emailMessage therefore set matched to null
        		  analyseWords = null;
        		  author = null;
        		  type = null;
        		  statusFrom = null;
        		  statusTo = null;
        		  senderName = null;
        		  references = null;
        		  inReplyTo = null;
        		  emailMessageId = null;
        		  subject = null;
        		  link = null;
	    		  StatusChanged = false;
	    		  status = null;
	    		  required = true;
	    		  From = line;   		 
        	  }
        	  else {
        		  if (matched == true) { //if searchkey found in last round but end of message not reached, i.e from, 
        			  					 // then keep on adding lines, only write to db once next From is found
        			  emailMessage = emailMessage + line + "\n";
        			  //if (!line.startsWith(">")) analyseWords = analyseWords + line + "\n";    //for classification 
        		  }
        		  
        	  }
	          Matcher m5 = null;
	          Matcher mw = null;
	          
	          // search for tag words from wordsList - that can used to describe the message (vote, voting, BDFL,kkdd etc)
	          //HAVE TO REMOVE THE <<
	          
	          for (String patternW : v_wordsList){
//        		  System.out.println("pattern " + pattern);
                  Pattern pw = Pattern.compile(patternW);
                  mw = pw.matcher(line);  
            	  if (mw.find()) //  while (m.find() || m9.find()) 
            	  {
 //           		  System.out.println("found  " + patternW);
            		  wordsFoundList += (" " + patternW);    //add pattern to word found list
            	  }
	          }
	          
        	  for (String pattern : v_searchKeyList){
//        		  System.out.println("pattern " + pattern);
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
  //           		  			System.out.println("dont store pattern " + pattern + " line " + line);
            	  			  }
            		  	  }   
            		  	  
            		  //end added
            		      if (Store == true){           		      
		            		  withinFileCounter++;
		            		  foundInFile = true;  
		            		  matched = true;                   //if searchkey found, set matched to true
		            		  String pepString;
		            		  Integer pep;
		            		  String str = pattern;      
		            		    str = str.replaceAll("[^-?0-9]+", " "); 
//		            		    System.out.println("Added " + Arrays.asList(str.trim().split(" ")));
//		IMP MAYBE      		    System.out.println("m5 " + m5.group(0) );
		            		    pepString = m5.group(0).replace("PEP","");
//		            		    System.out.println("here a pepString " + pepString);
		            		   // pepString = m5.group(0).replace("PEP ","");
		            		   // pepString = pepString; 	
		            		    pepString = pepString.replace(".txt","");//??????
		            		    pepString = pepString.replace("pep-","");
		            		    pepString = pepString.replace(" ","");
		            		    pepString = pepString.replaceAll("[,.:;!?(){}\\[\\]<>%]", "");    //remove punctuation 
//		            		    System.out.println("here b pepString " + pepString);
		            		    pep = Integer.parseInt(pepString);
//		            		    System.out.println("here e pep " + pep);
		            		    //add pep only if a duplicate does not exist
	/*	            		    boolean duplicateFound = false;
		            		    //Integer item = null;
		            		    for (Integer item : PEPList) { 
		            		    	System.out.println("Bolean duplicatefound = " + duplicateFound + " checking Pep = " + pep + " against item " + item); 
		            			    if (pep == item) {      
		            			    	duplicateFound = true;    // If PEP is not in list, set boolean to true 
		            			    	System.out.println("duplicateFound = true - PEP " + pep);	
		            			    }
		            			    System.out.println("Bolean duplicatefound = " + duplicateFound );
		            			}
	*/	  //          		  if (duplicateFound == false) {    // If PEP is not in list, add it to the list
		            		    PEPList.add(pep);   //add pep to list
//		            		    System.out.println("added PEP " + pep);
		      //      		  }
		            		  lastLine = line; 
		            		  location = folder + "\\" + fileEntry.getName(); 
		            		  
		            		  //WHY THIS LINE BELOW??
		            		  
		            		  emailMessage = emailMessage + " a " + line + "\n";
		            		  
//		           		      System.out.println("store pattern " + pattern + " line " + line);
		            		  String de = "pattern " + pattern + " line" + line;
		            		  storeInDebugFile(v_debugFile, de );
           		      }	  
            	   } //end while            	  
                } //end for
        	  if (matched == false) {
        		  emailMessage = emailMessage + " b " + line + "\n";    //for lines not having from and if not matched, keep on adding lines to emailmessage
        		//  if (!line.startsWith(">")) analyseWords = analyseWords + line + "\n";      //for classification 
        	  }
          }
          // end match
        } //end if
    	if (foundInFile = true);    	  
    		v_fileCounter++;
    	
      }   //end else (For each file)
      
    }     //end for
  }       //end method
  
  //get the list of pep number from subject line.
  //use this to narrow down the list of peps a message can belong to
  //sometimes no pep number would be mentioned in subject therefore have to asign to all pep number in message 
  // or maybe take a deeper analysis
	public static List<Integer> getPepNumberFromSubject_Match(
		List<Integer> v_PEPList, String v_subject) {
		System.out.println("v_subject " + v_subject);
		// update all
		// return all pep numbers mentioned in subject
		List<Integer> PEPListinSubject = new ArrayList<Integer>();
		Boolean store_pep = true;
		Boolean matched;
		Matcher m5 = null;
		for (String pattern : searchKeyList) 
		{
			// System.out.println("pattern " + pattern);
			Pattern p5 = Pattern.compile(pattern);
			m5 = p5.matcher(v_subject);
			while (m5.find()) // while (m.find() || m9.find())
			{
				boolean Store = true;
				Matcher m5a = null;
				for (String pattern2a : ignoreList) 
				{ // so many combinations we
														// cant store e.g. PEP
														// 312, if we looking
														// for PEP: 3 only
					Pattern p2a = Pattern.compile(pattern2a);
					m5a = p2a.matcher(v_subject);
					while (m5a.find()) // while (m.find() || m9.find())
					{
						store_pep = false;
						// System.out.println("dont store pattern " + pattern +
						// " line " + line);
					}
				}

				// end added
				if (store_pep == true) {
					foundInFile = true;
					matched = true; // if searchkey found, set matched to true
					String pepString;
					Integer pep;
					String str = pattern;
					str = str.replaceAll("[^-?0-9]+", " ");
					pepString = m5.group(0).replace("PEP", "");
					pepString = pepString.replace(".txt", "");// ??????
					pepString = pepString.replace("pep-", "");
					pepString = pepString.replace(" ", "");
					pepString = pepString.replaceAll("[,.:;!?(){}\\[\\]<>%]",
							""); // remove punctuation
					// System.out.println("here b pepString " + pepString);
					pep = Integer.parseInt(pepString);
					PEPListinSubject.add(pep); // add pep to list
					System.out.println("added PEP to subject " + pep);
				}
			} // end while
		} // end for
		// just for debugging code

//		System.out.println("PEPList " + PEPList.toString());
//		System.out.println("PEPListinSubject " + PEPListinSubject.toString());

		if (PEPList.isEmpty()) {
			System.out.println("No peps fround in message");
			// System.out.println("v_subject " + v_subject);
			//return PEPList;
			if (PEPListinSubject.isEmpty()) 
			{
				System.out.println("Also No peps found in subject line v_subject= " + v_subject);
				// System.out.println("v_subject " + v_subject);
				return PEPList;
			} 
		} 
		else {
			System.out.println("peps fround in message " + PEPList.toString());
		}
		// now match the peps in subject with those in message

		// create temporary Lists
		List<Integer> temp_PEPList = PEPList;
		List<Integer> temp_PEPListinSubject = PEPListinSubject;
//		System.out.println("PEPListinSubject after assignment" 	+ PEPListinSubject.toString());

		// if no pep number found in subject, return original list
		// this will insert a record for all pep number founds in message
		if (PEPListinSubject.isEmpty()) 
		{
			System.out.println("No peps found in subject line v_subject= " + v_subject);
			// System.out.println("v_subject " + v_subject);
		//	return PEPList;
			if (!temp_PEPList.isEmpty()) {
				System.out.println(" not in subject but in message " + temp_PEPListinSubject.toString());
				return temp_PEPList; // return intersected ones
			}
		} 
		else { //if pep found in subject
			// get intersected ones
			System.out.println("peps found in subject line " + temp_PEPListinSubject.toString());			
			if (temp_PEPList.isEmpty()) {
				System.out.println(" found in subject but not in message" + temp_PEPListinSubject.toString());
				return temp_PEPListinSubject; // return intersected ones
			}
			temp_PEPList.retainAll(temp_PEPListinSubject);
			if (!temp_PEPList.isEmpty()) {
				System.out.println("found in subject but also in message " + temp_PEPListinSubject.toString());
				return temp_PEPList; // return intersected ones
			}
		}
		// shouldn't reach till here, but just to please eclipe or Java
		return PEPList;
		
		
		// return only the list of peps to be inserted
	}
  
  
  public static void searchFilesForFolder_Gmane(final File folder, final File v_rootfolder, boolean v_foundInFile, String v_searchKey, int v_fileCounter, Writer v_outputFile, Writer v_debugFile, Connection v_conn, int i, List<String> v_searchKeyList, List<String> v_ignoreList , List<String> v_statusList, Integer v_messageID, List<String> v_wordsList) throws IOException 
  {	
	File rootFolder = v_rootfolder;
	for (final File fileEntry : folder.listFiles()) 
    {
      if (fileEntry.isDirectory()) {
        searchFilesForFolder_Gmane(fileEntry, v_rootfolder, v_foundInFile, v_searchKey, v_fileCounter, v_outputFile, v_debugFile, v_conn, i, v_searchKeyList, v_ignoreList, v_statusList, v_messageID, v_wordsList);
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

          Boolean StatusChanged = false;
          String statusFrom = null;
          String statusTo = null;
          String wordsFoundList = null;
          String emailMessageId = null;
          String inReplyTo = null;
          String references = null;
          String link = null;
          String analyseWords = null;
          String subject = null;
          String senderName = null;
          
          String author = null;
          String type = null;
          
          Integer lineCounter;
          Integer notrequiredLinesCounter;
          Boolean required = true; 
          
          Matcher m5 = null;
          Matcher mw = null;
          
          int withinFileCounter = 0;
          
          Boolean keepOnReading = false;
          while((line=br.readLine())!=null)
          {      	  
    	      if (keepOnReading == true){ 
    	    	  //process the line contents here  
    	    	  
    	    	  if (!line.startsWith(">") && !line.startsWith("<") && !line.startsWith("=") && !line.startsWith("@")  && !line.endsWith("wrote:")) analyseWords = analyseWords + line + "\n";    //for classification    	    	  
    	    	  
    	    	  //find data    	    	  
    	    	  if ((matchQuery(line, "Status:")) || (matchQuery(line, "\\+Status:")))    	    	  {
        			  statusLine = line.replace("Status: ","");
        			  statusLine = line.replaceAll("[,:;!?(){}+-\\[\\]<>%]", "");  
        			  status = statusLine;
        			  StatusChanged = true;    			  
        		  }    
    	    	  
    	    	  //match pep patters and add to peplist
    	    	  //identify pep numbers in line
    	    	  //add to message
    	    	  for (String pattern : v_searchKeyList){
//            		  System.out.println("pattern " + pattern);
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
  //           		  			System.out.println("dont store pattern " + pattern + " line " + line);
            	  			  }
            		  	  }   
                		  	  
                		  //end added
            		      if (Store == true){           		      
		            		  matched = true;                   //if searchkey found, set matched to true
		            		  String pepString;
		            		  Integer pep;
		            		  String str = pattern;      
	            		      str = str.replaceAll("[^-?0-9]+", " "); 
	            		      pepString = m5.group(0).replace("PEP","");	
	            		      pepString = pepString.replace(".txt","");//??????
	            		      pepString = pepString.replace("pep-","");
	            		      pepString = pepString.replace(" ","");
	            		      pepString = pepString.replaceAll("[,.:;!?(){}\\[\\]<>%]", "");    //remove punctuation 
	            		      pep = Integer.parseInt(pepString);
	            		      PEPList.add(pep);   //add pep to list	            		        		  
		            			
		            		  String de = "pattern " + pattern + " line" + line;
		            		  storeInDebugFile(v_debugFile, de );
           		      }	  
            	   } //end while            	  
                } //end for
    	    
    	      emailMessage = emailMessage + " " + line + "\n";
    	      }
    	      else {}
        	  
    	      String newline;
        	  if (line.startsWith("headers")) {
    	    	  keepOnReading = true;
    	    	  String senderDate =br.readLine();
    	    	  String[] data = senderDate.split("\\|");
    	    	  senderName = data[0];
//VVV    	    	  System.out.println("date 2 >> " + data[1]);
    	    	  //sometimes date is erorrnous
    	    	  try {
    	    		  date2 = data[1];
    	    	  }
    	    	  catch (Exception e) {
    	    		  System.out.println(e.toString());
    	    	  }
    	    	  
    	    	  
    	    	  
    	    	  Boolean foundSubject = false;
    	    	  while ((line = br.readLine()) != null && foundSubject ==false) {
    	    		  if ( line.trim().length() == 0 ) {
    	    		    continue;  // Skip blank lines
    	    		  } // otherwise:
    	    		  else {
    	    			  subject = line;
    	    			  if ((newline = br.readLine()) != null)  //check if netx line is not null as subject goes into next line
    	    			  {		subject += (" " + newline);	}
    	    			  foundSubject = true;
 //VVV   	    			  System.out.println("subject >> " + subject);
    	    		  }
    	    		}
    	    	     	    	  
    	      }
    	      if (line.startsWith("Permalink |")) {
    	    	  keepOnReading = false;
    	      }    	    
          } //end while
         
		  Date date9 = null; 	        			         			 
	  	  date9= findDate(date2);
//VVV	  	  System.out.println("sendername |" + senderName + " Date2 |" + date2 + " date9 |" +  date9 + " , " + " i " + i + " " + rootFolder + "\\" + fileEntry.getName() + " , Line = " + line );
	  	System.out.println();
	  	  if (date9 != null) { 
	  		  PEPList = removeDuplicates(PEPList, subject);     //remove duplicate PEPs
	  		  PEPList = getPepNumberFromSubject_Match(PEPList, subject);     //try to find matching pep numbers in subject
	  		  
	  	  }
		  else{
//vvv			  System.out.println("sendername |" + senderName + " Date2 |" + date2 + " date9 |" +  date9 + " , " + " i " + i + " " + rootFolder + "\\" + fileEntry.getName() + " , Line = " + line );
		  }
	  	  messageID++; 
	  	  System.out.println("Debug - For pep number "+  " sendername |" + senderName + " Date2 |" + date2 + " date9 |" +  date9 + " , " +" i " + i + " " + rootFolder + "\\" + fileEntry.getName() + " , Line = " + line + " subject " + subject );
    	  for (int g = 0; g<PEPList.size();g++){																	//g below = PEP number from list
    		  System.out.println("Insert - For pep number "+ PEPList.get(g) + " sendername |" + senderName + " Date2 |" + date2 + " date9 |" +  date9 + " , " +" i " + i + " " + rootFolder + "\\" + fileEntry.getName() + " , Line = " + line + " subject " + subject );
			  storeInDatabase_gmane(counter, date9, subject, rootFolder , fileEntry.getName(),lastLine, emailMessage,PEPList.get(g), status, statusFrom, statusTo, type, author,senderName, inReplyTo, references, emailMessageId, link, StatusChanged, wordsFoundList, analyseWords, messageID, required, conn);
			  storeInFile(v_outputFile, date9, rootFolder, fileEntry.getName(),lastLine, emailMessage, PEPList.get(g),status, statusFrom, statusTo, type, author, messageID);	
//			  System.out.println("date2 " + date2 + " author " +  author +  " status " + status + " , " + " i " + i + rootFolder + fileEntry.getName() + " , " + line + " ");
//		  	  	  System.out.println("Inserted into database PEP = " + PEPList.get(g));
		  }  // end for
    	  emailMessage = null;
    	  analyseWords = null;
    	  
    	  author = null;
		  type = null;
		  statusFrom = null;
		  statusTo = null;
		  senderName = null;
		  references = null;
		  inReplyTo = null;
		  emailMessageId = null;
		  subject = null;
		  link = null;
		  StatusChanged = false;
		  status = null;    	  
    	  
    	  PEPList.clear();
    	  } //end if  	 
      }   //end else
      
    }     //end for
  }       //end method 
        	        
  public static String getSender (String v_line){
	  String temp = null;
	  Integer start =0;
	  Integer end = 0;
	 // System.out.println(v_line);
	  /*
	  System.out.println(v_line);	  
	  temp = v_line.replace("From: ",""); 
	  String [] arr = temp.split(" ", 2);
	  if (arr.length == 1)
		  temp = arr[0];
	  else
		  temp = arr[1];
	  temp = temp.replaceAll("[,.:;!?(){}\\[\\]<>%]", "");
	  */
	  if (v_line.contains("(")){	  
		  start = v_line.indexOf('(');
		  end = v_line.indexOf(')');
		  if (end < 0)
			  temp = v_line.substring(0, start+1);
		  else {
//			  System.out.println("v_line " + v_line + " start " + start +  " end " + end + " name= " + temp);
			  temp = v_line.substring(start+1, end);
		  }
	  }
	  else if (v_line.contains("\"")){  
		  start = v_line.indexOf('\"');
		  end = v_line.lastIndexOf('\"');	
//		  System.out.println("v_line " + v_line + " start " + start +  " end " + end + " name= " + temp);	
		  temp = v_line.substring(start+1, end);
	  }
	 // System.out.println(v_line + " start " + start +  " end " + end + " name= " + temp);	
	return temp;
  }
  
  public static List<Integer> removeDuplicates(List<Integer> v_PEPList, String v_subject){
	  Integer pep = null;
	  boolean duplicateFound = false;
//	  System.out.println("Items in List "); 
//	  for (Integer item : v_PEPList) { 	    	
//	    	System.out.println(item); 
//		}
	  /*
	  for (Integer item : v_PEPList) { 
		  for (Integer item2 : v_PEPList) { 
			  System.out.println("Bolean value= " + duplicateFound + " checking item = " + item + " against item2 " + item2); 
			  if (item == item2) {      
		    	duplicateFound = true;    // If PEP is not in list, set boolean to true 
		    	System.out.println("duplicateFound = true - item2 " + item2);	
		    	v_PEPList.remove(item2);
		    	//v_PEPList.
			  }
			  System.out.println("Bolean value = " + duplicateFound );
		  }
		}
	  */
	  Set set = new HashSet(v_PEPList);
	  //create a new List from the Set
	  ArrayList uniqueList = new ArrayList(set);	  
//	  System.out.println("Unique Items in new Unique List "); 
//	  for (Integer item : v_PEPList) { 	    	
//	    	System.out.println(item); 
//		}
	  
	//check in subject
	  // if the current pep number in subject , then add only those
	  //else keep on adding
	  
	  
	  
	  return uniqueList;
  }
  
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
  
  

  
  public static void initWordsList(List<String> v_wordsList){
	 wordsList.add("BDFL");
	 wordsList.add("vote");		
	 wordsList.add("voting");		
	 wordsList.add("poll");
	 wordsList.add("consensus");
	 wordsList.add("survey");
	 wordsList.add("offline");
	 wordsList.add("twitter");
	 wordsList.add("plus.google.com");
	  /* statusList.add("Replaced");
	  statusList.add("Rejected");
	  statusList.add("Postponed");
	  statusList.add("Incomplete"); 
	  statusList.add("Superseded");
	  */ 	  		
  }
  
  public static Date findDate(String v_line)  { 
//	  if (v_line == null) return null;
	  
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
	            "d MMM yyyy HH:mm:ss z"};
	  String dateC= v_line; 
	  Date pubdate = null;
//	  Pattern p0=Pattern.compile("Date:");
//	  Matcher m0=p0.matcher(v_line);
//	  if (m0.find()){
//		  dateC = v_line.replace("Date:","");
//		  dateC = v_line.replace("Date:","");
	 // System.out.println("dateC " + dateC);
		  if (dateC.length() >30)
			  dateC = dateC.substring(0, 31);
		  dateC = dateC.substring(1, dateC.length());  
		  
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
  
  public static void storeInFile(Writer v_outputFile, Date v_dateC, File v_rootfolder, String v_file,  String v_line, String v_emailMessage, int i, String V_status, String V_statusFrom, String V_statusTo, String v_type, String v_author, Integer messageID){
	  try {
		        v_outputFile.write(messageID + " , " + v_dateC + " , " +  v_rootfolder + " , " + v_file + " , " + v_line + " , " + V_statusFrom + " , "+ V_statusTo + " , " + v_type + " , " + v_author + " , " + v_emailMessage + " , " + i);			  
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
  
  public static void storeInDatabase(int v_id, Date v_dateC, String v_subject, File v_rootfolder, String v_file, String v_line, String v_emailMessage, int i, String V_status, 
		  String V_statusFrom, String V_statusTo, String V_type, String V_author, String v_senderName, String v_inReplyTo, String v_references, String v_emailMessageId, 
		  String v_link, Boolean v_statusChanged, String v_wordsList, String v_analyseWords, Integer v_messageID, Boolean v_required, Connection conn){
	  
	     Statement stmt = null;
	     try{
	        //STEP 2: Register JDBC driver
//	        Class.forName("com.mysql.jdbc.Driver");
//	        conn = DriverManager.getConnection(DB_URL,USER,PASS);
	        stmt = conn.createStatement();
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
		    
    		 //the mysql insert statement																							//references,
    	      String query = " insert into allpeps_gmane( date2, subject, folder,file , line, email, PEP, statusFrom, statusTo,type, author,senderName, inReplyTo,  emailMessageId, link, StatusChanged, analyseWords, wordsList, messageID, required, pythonarchive)"
    	        + " values (?,?,?, ?,?, ?, ?, ?,?, ?,?,?,?,?,?,?,?,?,?,?)";
    	      //System.out.println("here a");
    	      
    	      //System.out.println("reply to " + v_inReplyTo + " ref = " + v_references);
    	      // create the mysql insert preparedstatement
    	      PreparedStatement preparedStmt = conn.prepareStatement(query);
    	      //preparedStmt.setInt (1, v_id);
    	      preparedStmt.setDate (1,sqlDate);
    	      preparedStmt.setString (2, v_subject);
    	      preparedStmt.setString (3, v_rootfolder.toString());
    	      preparedStmt.setString (4, v_file.toString());
    	      preparedStmt.setString  (5, v_line);
    	      preparedStmt.setString  (6,v_emailMessage);
    	      preparedStmt.setInt  (7, i);
    	      preparedStmt.setString  (8,V_statusFrom);
    	      preparedStmt.setString  (9,V_statusTo);
    	      preparedStmt.setString  (10,V_type);
    	      preparedStmt.setString  (11,V_author);
    	      preparedStmt.setString  (12,v_senderName);
    	      preparedStmt.setString  (13,v_inReplyTo);
    	      //preparedStmt.setString  (12,v_references);
    	      preparedStmt.setString  (14,v_emailMessageId);
    	      preparedStmt.setString  (15,v_link);
    	      preparedStmt.setBoolean (16,v_statusChanged);
    	      preparedStmt.setString  (17,v_analyseWords);
    	      preparedStmt.setString  (18,v_wordsList);
    	      preparedStmt.setInt(19,v_messageID);  
    	      preparedStmt.setBoolean(20,v_required);
    	      preparedStmt.setBoolean(21,true);
    	      preparedStmt.execute();		
              stmt.close();
//	        conn.close();
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
  
  public static void storeInDatabase_gmane(int v_id, Date v_dateC, String v_subject, File v_rootfolder, String v_file, String v_line, String v_emailMessage, int i, 
		  String V_status, String V_statusFrom, String V_statusTo, String V_type, String V_author, String v_senderName, String v_inReplyTo, String v_references, 
		  String v_emailMessageId, String v_link, Boolean v_statusChanged, String v_wordsList, String v_analyseWords, Integer v_messageID, Boolean v_required, Connection conn){
	  
	     Statement stmt = null;
	     try{
	        //STEP 2: Register JDBC driver
//	        Class.forName("com.mysql.jdbc.Driver");
//	        conn = DriverManager.getConnection(DB_URL,USER,PASS);
	        stmt = conn.createStatement();
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
		    
 		 //the mysql insert statement																							//references,
 	      String query = " insert into allpeps_gmane ( date2, subject, folder,file , line, email, PEP, statusFrom, statusTo,type, author,senderName, inReplyTo,  emailMessageId, link, StatusChanged, analyseWords, wordsList, messageID, required, gmane)"
 	        + " values (?,?,?, ?,?, ?, ?, ?,?, ?,?,?,?,?,?,?,?,?,?,?,?)";
 	      //System.out.println("here a");
 	      
 	      //System.out.println("reply to " + v_inReplyTo + " ref = " + v_references);
 	      // create the mysql insert preparedstatement
 	      PreparedStatement preparedStmt = conn.prepareStatement(query);
 	      //preparedStmt.setInt (1, v_id);
 	      preparedStmt.setDate (1,sqlDate);
 	      preparedStmt.setString (2, v_subject);
 	      preparedStmt.setString (3, v_rootfolder.toString());
 	      preparedStmt.setString (4, v_file.toString());
 	      preparedStmt.setString  (5, v_line);
 	      preparedStmt.setString  (6,v_emailMessage);
 	      preparedStmt.setInt  (7, i);
 	      preparedStmt.setString  (8,V_statusFrom);
 	      preparedStmt.setString  (9,V_statusTo);
 	      preparedStmt.setString  (10,V_type);
 	      preparedStmt.setString  (11,V_author);
 	      preparedStmt.setString  (12,v_senderName);
 	      preparedStmt.setString  (13,v_inReplyTo);
 	      //preparedStmt.setString  (12,v_references);
 	      preparedStmt.setString  (14,v_emailMessageId);
 	      preparedStmt.setString  (15,v_link);
 	      preparedStmt.setBoolean (16,v_statusChanged);
 	      preparedStmt.setString  (17,v_analyseWords);
 	      preparedStmt.setString  (18,v_wordsList);
 	      preparedStmt.setInt(19,v_messageID);  
 	      preparedStmt.setBoolean(20,v_required);
 	      preparedStmt.setBoolean(21,true);
 	      preparedStmt.execute();		
           stmt.close();
//	        conn.close();
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
	        stmt = conn.createStatement();
	        String sqlA;
	        
		    
		// the mysql insert statement
	      String query = " DELETE from allpeps_gmane;";
	 
	      // create the mysql insert preparedstatement
	      PreparedStatement preparedStmt = conn.prepareStatement(query);
	      //preparedStmt.setInt (1, v_id);

	      preparedStmt.execute();		
        stmt.close();
	    //    conn.close();
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
  
  public static void emptyDatabase_gmane(Connection conn){
	  
	     Statement stmt = null;
	     try{
	        //STEP 2: Register JDBC driver
//	        Class.forName("com.mysql.jdbc.Driver");
//	        conn = DriverManager.getConnection(DB_URL,USER,PASS);
	        stmt = conn.createStatement();
	        String sqlA;
	        
		    
		// the mysql insert statement
	      String query = " DELETE from allpeps_gmane2016;";
	 
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