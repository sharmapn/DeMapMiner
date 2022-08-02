package readRepository.readMetadataFromWeb;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import connections.MysqlConnect;
import miner.process.ProcessingRequiredParameters;
import miner.process.PythonSpecificMessageProcessing;
import connections.PropertiesFile;
import utilities.GetDate;
// jan 2021 ..this script is for bip details extraction..few fields left,{cope="",candidate="", 
// component="", discussion="", reviewedby="", endorsedby="", created="", updated="", issue="",summary="",} 
// but necessary ones for now extracted 

// oct 2018 ..this script used instead of the original GetProposalDetails.java, as this one reads titles spread over two lines
public class GetProposalDetailsWebPageBIPs {
	
	static Map bips = new HashMap();
	static ArrayList<Integer> BIPs = new ArrayList<Integer>();		//get all bips in database allmessages table
	Integer bipCounter= 347; //get the current limit of bip Counter 
	//authors arraylist
	ArrayList authors = new ArrayList();	
	static MysqlConnect mc = new MysqlConnect();
	static Connection conn;
	static PythonSpecificMessageProcessing pms = new PythonSpecificMessageProcessing();
	static GetDate gd = new GetDate();  //new date parser centrally located in seprate package for use from all other package code
	//The following will miss PEP 0, and we have to insert it manually
    //insert into pepdetails (bip,author,type,title) 
	//values (0,'David Goodger <goodger at python.org>, Barry Warsaw <barry at python.org>','Informational','Index of Python Enhancement Proposals (PEPs)')
	static String allmessagesTableName = "allmessages";	//dec 2018 ..was pepdetails2 before
	static String proposalTableName = "bipdetails";	//dec 2018 ..was pepdetails2 before
	static Integer counterA=0, counterB=0, counterC=0;
	static ArrayList<Integer> proposalDone = new ArrayList<>();
	public static void main(String[] args) throws IOException {
		conn = mc.connect();
		// main function which returns and populates database table
		queryLink(conn);
		
		//test these return methods in this class which are called by other classes - queries the db table
		// getPepAuthor(308);
		// getPepBDFLDelegate(308);			
		mc.disconnect();
	}
	
	public static void queryLink(Connection conn) throws IOException {
	    URL url;	    InputStream is = null;	    BufferedReader br;
	    String line;	    
	    //write final data
	    File fout = new File("c:\\scripts\\BIPNumberTitle.txt");		FileOutputStream fos = new FileOutputStream(fout); 		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

	    try {		    	
	    	List<String> uniqueProposalLinkList = new ArrayList<String>();
	    	String urllink = "https://github.com/bitcoin/bips/blob/master/bip-";  //https://github.com/bitcoin/bips/blob/master/bip-0074.mediawiki

	    	//Jan 2019..we get all distinct bips from allmessages table where we just read in all files
	    	BIPs = 	getallbips(conn);
	    	//we populate the links using the bip number
	    	String padded= "";
	    	for(Integer a : BIPs) {  //only go through distinct links from db
	    		padded = String.format("%04d" , a);
	    		String newlink = urllink + padded + ".mediawiki"; 
	    		uniqueProposalLinkList.add(newlink);
	    		downloadProposalDetails_populateDB(newlink, conn, a);
	    	}	    	//https://github.com/bitcoin/bips/blob/master/bip-0008.mediawiki
	    	
	        System.out.println("Total Unique Links: "+ uniqueProposalLinkList.size());
	        Collections.sort(uniqueProposalLinkList, String.CASE_INSENSITIVE_ORDER);
	    } 
//	    catch (MalformedURLException mue) {	         mue.printStackTrace();	    } 
//	    catch (IOException ioe) {	         ioe.printStackTrace();	    } 
	    finally {
	         is.close();	         bw.close();
	    }	    
	   	    
	    System.out.println("Total peps: " + bips.size());
	    	    
	}
	
	static void downloadProposalDetails_populateDB(String v_link, Connection conn, Integer bipno) {
		try {	
			URL biplink = new URL(v_link);
	        BufferedReader in = new BufferedReader( new InputStreamReader(biplink.openStream()));
	        String inputLine,bipNo = "", bipTitle = "", owner = "", scope="",candidate="", 
	        		component="", discussion="", reviewedby="", endorsedby="", created="", updated="", issue="",summary="",
	        		type = "", bdfl_delegate = "", pythonVersion = "";
	        
	        ArrayList<Integer> values = new ArrayList<>();
	        
	        while ((inputLine = in.readLine()) != null){    
	        	
	        	//there are multiple author title for bip 0 so it will keep on reading
	        	//thus we end as soon as we finish reading the main bit
	        	if (inputLine.contains("</a>Table of Contents</h2>")){
	        		break;
	        	}
	        	if (inputLine.contains("Title:")){ //System.out.println("title "+inputLine);
	            	//if(inputLine.endsWith("</td>") ) {
		            	//inputLine = inputLine.substring(0,inputLine.length()-1); 
		            	int start = inputLine.lastIndexOf("Title:"); // 		int end = inputLine.lastIndexOf("</title>"); 
		            	int end = inputLine.length(); // lastIndexOf(inputLine);
		            	bipTitle = inputLine.substring(start+7, end); 
//		            	String tempbipNo = bipTitle.split(":",0)[0];
//		            	bipNo = tempbipNo.replaceAll("bip","").trim();
		            	//bipTitle = bipTitle.split(":")[1].trim().replace("&amp;", "");
//		            	bipTitle = bipTitle.replace(bipNo, "");  	//what ever is remaining - we do this we can't always split on : as there are sometimes double : in string
//		            	bipTitle = bipTitle.replace("bip :","");	
//		            	bipTitle = bipTitle.trim().replace("&amp;", "");
//		            	bipTitle = bipTitle.replaceAll("\\s{2,}", " ").trim();  //remove double spaces
		            	System.out.println("bip No: " + bipNo+ " Title: "+bipTitle);
	            	//}
	            	//else {
	            		//handle cases where title goes into second line
	            	//}
	            }
	        	//	<td>Owner</td><td>Kim Barrett</td></tr><tr><td>Type</td
	            if (inputLine.contains("Author:")){ 
	            	System.out.println("Author: "+inputLine);
	            	//int start = inputLine.lastIndexOf("Author:");  		
	            	//int end = inputLine.replace("Author: ", "");   //lastIndexOf("<td>Type</td>") - 14;    
	            	owner = inputLine.replace("Author:","").replace("&gt", "").replace("&lt", "");
	            	
	            	System.out.println("Author: "+owner);
	            //we can get owner, type, scope, created, updated from this line
	            
	//            	int start = inputLine.indexOf(">Author:");    	int end = inputLine.lastIndexOf("</td>", start+8);    pepauthorLine = inputLine.substring(start+8, end);        		System.out.println("pepauthorLine "+ pepauthorLine);
	            	//if line ends with comma, we know there is another author in next line
	 //           	System.out.println("FirstAuthor- "+ inputLine + " pepauthorLine: "+ pepauthorLine);           	
	            	// <tr class="field"><th class="field-name">Author:</th><td class="field-body">Guido van Rossum &lt;guido at python.org&gt;,
	            	// Barry Warsaw &lt;barry at python.org&gt;,
	            	// Nick Coghlan &lt;ncoghlan at gmail.com&gt;</td> 
	            	/* if(inputLine.endsWith(",") ) {
	            		counterA++;	System.out.println("inputLine "+inputLine); 
	            		inputLine = inputLine.substring(0,inputLine.length()); 
	            		int start = inputLine.lastIndexOf(">");  		int end = inputLine.lastIndexOf(",");    
	            		pepauthorLine = inputLine.substring(start+1, inputLine.length());    System.out.println("pepauthorLine "+pepauthorLine); 
	            		//read and keep adding next lines until a line ends with <td>
	            		boolean keepReading=true;
	            		while (keepReading) {
	            			inputLine = in.readLine();
	            			pepauthorLine = pepauthorLine + inputLine;
	            			if (inputLine.endsWith("</td>")) {
	            				//extract and add
	            				//start = inputLine.lastIndexOf(">");  		
	            				end = inputLine.lastIndexOf("</td>");   
	            				//pepauthorLine = inputLine.substring(0, end);
	            				pepauthorLine = pepauthorLine + inputLine.substring(0, end);
	            				break;
	            			}
	            		}
	            	}
	            	else if (inputLine.endsWith(";")) {
	            		counterB++;
	            	}
	            	else {
	            		counterC++;
	            		inputLine = inputLine.substring(0,inputLine.length()-1); 
	            		int start = inputLine.lastIndexOf(">");  		int end = inputLine.lastIndexOf("</td");    
	            		pepauthorLine = inputLine.substring(start+1, end);    System.out.println("pepauthorLine "+pepauthorLine); 
	            	}
	            	*/
	            } 
            
	            if (inputLine.contains("Type:")){
	            	//int start = inputLine.indexOf(">Type:");     int end = inputLine.lastIndexOf("</td>", start+6);        type = inputLine.substring(start+6, end);        		//System.out.println(title);
	            	//inputLine = inputLine.substring(0,inputLine.length()-1); 
	            	//int start = inputLine.lastIndexOf(">");  		int end = inputLine.lastIndexOf("</td");    
	            	//type = inputLine.substring(start+1, end);    
	            	type = inputLine.replace("Type:","").replace("&gt", "").replace("&lt", "");
	            	System.out.println("Type: "+type); 
	            } 
	            if (inputLine.contains("<td>Candidate</td>")){
	            	//int start = inputLine.indexOf(">BDFL-Delegate:");  		int end = inputLine.lastIndexOf("</td>", start+15);   bdfl_delegate = inputLine.substring(start+15, end);  	//System.out.println(title);
	            	inputLine = inputLine.substring(0,inputLine.length()-1); 
	            	int start = inputLine.lastIndexOf(">");  		int end = inputLine.lastIndexOf("</td");    
	            	bdfl_delegate = inputLine.substring(start+1, end);    System.out.println("bdfl_delegate "+bdfl_delegate); 
	            } 
	            if (inputLine.contains("<td>Component</td>")){
	            	//int start = inputLine.indexOf(">Python-Version:"); 		int end = inputLine.lastIndexOf("</td>", start+16);    pythonVersion = inputLine.substring(start+16, end); 	//System.out.println(title);
	            	inputLine = inputLine.substring(0,inputLine.length()-1); 
	            	int start = inputLine.lastIndexOf(">");  		int end = inputLine.lastIndexOf("</td");    
	            	pythonVersion = inputLine.substring(start+1, end);    System.out.println("pythonVersion "+pythonVersion); 
	            } 
	           	            
	            if (inputLine.contains("Created:")){
	            	//int start = inputLine.indexOf(">Created:");        		int end = inputLine.lastIndexOf("</td>", start+9);     created = inputLine.substring(start+9, end).trim();  	//System.out.println(title);
	            	//inputLine = inputLine.substring(0,inputLine.length()-1); 
	            	//int start = inputLine.lastIndexOf(">");  		int end = inputLine.lastIndexOf("</td");    
	            	//created = inputLine.substring(start+1, end);    System.out.println("created "+created); 
	            	created = inputLine.replace("Created:","").replace("&gt", "").replace("&lt", "");
	            	System.out.println("Created: "+created); 
	            }
	            /*	
	        } 
	        String authorCorrected="",bdfl_delegateCorrected="",newName ="";
	        //may 2018 ...update these variables..now integerated here but were part of a separate update script
	        System.out.println("A " + counterA + " B "+ counterB + " C "+ counterC);
			
			//This script populates the author email address in bip states table
			//then we can use java to match -- match author and senderemail in allmessages with authorCorrected, authorEmail in pepdetails 
			//get the email from the author column
			// sometimes bip authors change during the course of discussion - maybe we can get this information from pepstates table
	        //seprating multiple authors but not storing them yet....
			String combinedAuthor ="", combinedEmail="", permanentAuthor="", permanentAuthorEmail="";
			if(pepauthorLine.contains(",")){
				//String[] authorsLine = pepauthorLine.split(",");
				for (String authorLine: pepauthorLine.split(",")){
					 // permanentAuthor and permanentAuthorEmail
					 permanentAuthor=pms.extractFullAuthorFromString(authorLine); //remove chars
					 permanentAuthorEmail=pms.returnAuthorEmail(authorLine);
					 //System.out.println("\t Processed "+ bip + " " +permanentAuthor + "," + permanentAuthorEmail);
					 System.out.format("%7s, %120s, %40s, %30s", bipNo, pepauthorLine, permanentAuthor, permanentAuthorEmail); 		 System.out.println();
					 combinedAuthor =combinedAuthor + permanentAuthor + ",";
					 combinedEmail = combinedEmail + permanentAuthorEmail + ",";
	//				 updateauthor(bip,author,conn);
				}
				combinedAuthor = combinedAuthor.substring(0, combinedAuthor.lastIndexOf(","));		combinedEmail = combinedEmail.substring(0, combinedEmail.lastIndexOf(","));	
				//combined = combined.substring(0, combined.lastIndexOf(","));
			}
			else {
				 permanentAuthor=pms.extractFullAuthorFromString(pepauthorLine);
				 permanentAuthorEmail=pms.returnAuthorEmail(pepauthorLine);
				 System.out.format("%7s, %120s, %40s, %30s", bipNo, pepauthorLine, permanentAuthor, permanentAuthorEmail); 			 System.out.println();
				 //System.out.println(bip + pepauthorLine + "\t Processed "+ " " +permanentAuthor + "," + permanentAuthorEmail);
				 combinedAuthor = permanentAuthor;
				 combinedEmail = permanentAuthorEmail;
			}
			
			//bdfl delegates - would normally be one or xero only, not multiple
			if (bdfl_delegate!=null) {							
				bdfl_delegateCorrected = pms.extractFullAuthorFromString(bdfl_delegate.trim()); //pms.getAuthorFromString(bdfl_delegate.trim());
			}
			
			System.out.println("bip " + bipNo + " newAuthor " + permanentAuthor + " new bdfl_delegate "+ bdfl_delegateCorrected);
	        
	//       if(bip!=null && title!=null){
	//       	int pepNumber = Integer.parseInt(bip);
	//        	peps.put(bip, title);
	        System.out.println(bipNo+" "+bipTitle+" "+permanentAuthor+" "+type+ " "+ bdfl_delegate + " " + pythonVersion + " " + created);
	        	///bw.write(bip+","+title);
	    		//bw.newLine();
	        //insertinDB(bip,title,permanentAuthor,authorCorrected,permanentAuthorEmail, type, bdfl_delegate,bdfl_delegateCorrected,pythonVersion,created, conn);
	        insertinDB(bipNo,bipTitle,combinedAuthor,authorCorrected,combinedEmail, type, bdfl_delegate,bdfl_delegateCorrected,pythonVersion,created, conn);
	//      }
	        in.close();	
	  */      
	        }
	        System.out.println("bip " + bipno + " bipTitle " + bipTitle + " Owner: "+ owner );
			insertinDB(bipno,bipTitle,owner,"","", type, bdfl_delegate,"",pythonVersion,created, conn);
	    }
		catch (Exception e) {
			System.err.println("Got an exception! ");			System.err.println(e.getMessage());
			System.out.println(StackTraceToString(e)  );
		}
	}
	
	public static Date returnDate(String candidate){
		List<SimpleDateFormat> knownPatterns = new ArrayList<SimpleDateFormat>();
		knownPatterns.add(new SimpleDateFormat("dd-MMM-yyyy"));		knownPatterns.add(new SimpleDateFormat("yyyy-MM-dd"));		knownPatterns.add(new SimpleDateFormat("dd MMM yyyy"));
		//knownPatterns.add(new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss"));
		//knownPatterns.add(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX"));

		for (SimpleDateFormat pattern : knownPatterns) {
		    try {
		        // Take a try
		        return new Date(pattern.parse(candidate).getTime());
		    } catch (ParseException pe) {
		        // Loop on
		    }
		}
		System.err.println("No known Date format found: " + candidate);
		return null;
	}
	
	public static void insertinDB(Integer bip,String title,String author,String author_corrected,String authorEmail, String type, String bdfl_delegate,  String bdfl_delegate_corrected, 
			String pythonVersion, String created, Connection conn)
	  {
		Integer createdYear = null;
		try {
			// get java date from string
			//String startDateString = "06-27/2007";
			java.util.Date createdDate =null;			java.sql.Date sqlDate = null;
			if (created==null){				sqlDate = null;			}
			else{
				/*
					DateFormat df = new SimpleDateFormat("dd-MMM-yyyy");				
					//System.out.println("a");
					createdDate = (java.util.Date) df.parse(created);
				*/
				createdDate = gd.findDate(created); 	//System.out.println("b");				
				if (createdDate != null) {
					java.util.Calendar cal = Calendar.getInstance();
					cal.setTime(createdDate);
					cal.set(Calendar.HOUR_OF_DAY, 0);					cal.set(Calendar.MINUTE, 0);					cal.set(Calendar.SECOND, 0);					cal.set(Calendar.MILLISECOND, 0);
					sqlDate = new java.sql.Date(cal.getTime().getTime());					Calendar cal2 = Calendar.getInstance();					cal2.setTime(createdDate);
					createdYear = cal2.get(Calendar.YEAR);
				}				 
			}
			//if (createdDate==null)	createdDate = 0;
			if (createdYear==null)	createdYear = 0;
			// the mysql insert statement
			String query = " insert into "+proposalTableName+" (bip, title, author,authorcorrected,authorEmail,type,bdfl_delegate,bdfl_delegatecorrected,created,createdYear, python_version)"
					+ " values (?,?,?,?,?,?,?,?,?,?,?)";

			// create the mysql insert preparedstatement
			PreparedStatement preparedStmt = conn.prepareStatement(query);
			preparedStmt.setInt(1, bip);		preparedStmt.setString(2, title);				preparedStmt.setString(3, author);				preparedStmt.setString(4, author);
			preparedStmt.setString(5, authorEmail);				preparedStmt.setString(6, type);				preparedStmt.setString(7, bdfl_delegate);		preparedStmt.setString(8, bdfl_delegate); 
			preparedStmt.setDate(9, sqlDate);					preparedStmt.setInt(10, createdYear);			preparedStmt.setString(11, pythonVersion);
			// preparedStmt.setBoolean(4, false);			// preparedStmt.setInt (5, 5000);
			// execute the preparedstatement
			preparedStmt.execute();
			createdYear = null; 
		}  catch (Exception e) {
			System.out.println(StackTraceToString(e)  );			System.err.println("Got an exception!");			System.err.println(e.getMessage());
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
	
	public static String getPepAuthor(Integer v_pepNumber){
		String author = "";
		try
		{
			//Connection conn = mc.connect();
			String query = "SELECT distinct bip, author FROM "+proposalTableName+" where bip = " + v_pepNumber;
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);
			
			// iterate through the java resultset
			if (rs.next())   {
				int pepNumber = rs.getInt("bip");				author = rs.getString("author");
				System.out.format("%s, %s\n", pepNumber, author);
			}
			st.close();
		}  catch (Exception e)	    {
			System.err.println("Got an exception! ");			System.err.println(e.getMessage());
		}
		
		//if author has comma, meaning multiple authors, then insert in authors array
		String authorFiltered = "";		
		// if it has brackets
		//paul@prescod.net (Paul Prescod)
		if (author.contains("(") && author.contains(")")) {
			authorFiltered = author.substring(author.indexOf("(")+1,author.indexOf(")"));
			author = authorFiltered;
		}
		
		return author;
	}
	
	//for all bip authors, get the bip author (can be multiple) and remove unecessary charaters and email address
	public static String correctPepAuthor(){
		String author = "";
		try
		{
			//Connection conn = mc.connect();
			String query = "SELECT distinct bip, author FROM "+proposalTableName+";";
			Statement st = conn.createStatement();			ResultSet rs = st.executeQuery(query);
			
			// iterate through the java resultset
			while(rs.next())   {
				int pepNumber = rs.getInt("bip");				author = rs.getString("author");
				System.out.format("%s, %s\n", pepNumber, author);
			}
			st.close();
		}  catch (Exception e)	    {
			System.err.println("Got an exception! ");			System.err.println(e.getMessage());
		}
		
		//if author has comma, meaning multiple authors, then insert in authors array
		String authorFiltered = "";		
		// if it has brackets
		//paul@prescod.net (Paul Prescod)
		if (author.contains("(") && author.contains(")")) {
			authorFiltered = author.substring(author.indexOf("(")+1,author.indexOf(")"));
			author = authorFiltered;
		}
		
		return author;
	}
	
	public static String getPepBDFLDelegate(Integer v_pepNumber){
		String delegate = "";
		try
		{
			Connection conn = mc.connect();
			String query = "SELECT distinct bip, bdfl_delegate FROM "+proposalTableName+" where bip = " + v_pepNumber;
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);
			if (rs.next())    {
				int pepNumber = rs.getInt("bip");				delegate = rs.getString("bdfl_delegate");
				System.out.format("%s, %s\n", pepNumber, delegate);
			}
			st.close();
		}   catch (Exception e)	    {
			System.err.println("Got an exception! ");			System.err.println(e.getMessage());
		}
		
		return delegate;
	}
	
	static void readFileGetPEPTitle(String v_link, BufferedWriter bw) throws IOException{
		URL peplink = new URL(v_link);
		BufferedReader in = new BufferedReader(new InputStreamReader(peplink.openStream()));
		String inputLine, bip = null, title = null;
		
		while ((inputLine = in.readLine()) != null){      	
			if (inputLine.contains(">PEP:")){
				int start = inputLine.indexOf(">PEP:");				int end = inputLine.indexOf("</td>", start+5);
				bip = inputLine.substring(start+5, end);				bip = bip.trim();
				//System.out.println(bip);
				
			}if (inputLine.contains(">Title:")){
				int start = inputLine.indexOf(">Title:");				int end = inputLine.indexOf("</td>", start+7);
				title = inputLine.substring(start+7, end);
				//System.out.println(title);
			}             
			
		} 
		//       if(bip!=null && title!=null){
		//       	int pepNumber = Integer.parseInt(bip);
		//        	peps.put(bip, title);
		System.out.println(bip+" "+title);
		bw.write(bip+","+title);		bw.newLine();
		//      }
		in.close();		
	}
	
	static void readFileGetPEPAuthor(String v_link, BufferedWriter bw) throws IOException{
		//String v_link = "https://github.com/python/peps";
		//v_link +=v_pepNumber;
		URL peplink = new URL(v_link);
        BufferedReader in = new BufferedReader(new InputStreamReader(peplink.openStream()));
        String inputLine, bip = null, author = null;
        
        while ((inputLine = in.readLine()) != null){      	
        	if (inputLine.contains(">PEP:")){
        		int start = inputLine.indexOf(">PEP:");        		int end = inputLine.indexOf("</td>", start+5);
        		bip = inputLine.substring(start+5, end);        		bip = bip.trim();
        		//System.out.println(bip);
            	
            }if (inputLine.contains(">Author:")){
            	int start = inputLine.indexOf(">Author:");        		int end = inputLine.indexOf("</td>", start+7);
        		author = inputLine.substring(start+7, end);
        		//System.out.println(title);
            }             
            
        } 
 //       if(bip!=null && title!=null){
 //       	int pepNumber = Integer.parseInt(bip);
//        	peps.put(bip, title);
        	System.out.println(bip+" "+author);
        	//xx bw.write(bip+","+title);
    		//xx bw.newLine();
  //      }
        in.close();		
	}
	
	//Jan 2019, get all distinct bip from allmessage table
	
	public static ArrayList<Integer> getallbips(Connection conn){		
		ArrayList<Integer> BIPs = new ArrayList<Integer>();		
		try
		{
			String query= "SELECT DISTINCT bip  FROM "+allmessagesTableName+" order by bip asc";			
			// create the java statement
			Statement st = conn.createStatement();			ResultSet rs = st.executeQuery(query);
			Integer counter=0;	 
			Boolean fiftydone= false, hundreddone= false,hundredfiftydone= false;
			while (rs.next())			{				
				Integer pepNumber = rs.getInt("bip");				
				//System.out.format("%s, %s, %s, %s, %s, %s\n", id, firstName, lastName, dateCreated, isAdmin, numPoints);
				BIPs.add(pepNumber);				
				counter++;
				System.out.print(pepNumber+", ");
				if(counter>50 && fiftydone == false) 	{System.out.println(); fiftydone =true;		}
				if(counter>50 && hundreddone == false) 	{System.out.println(); hundreddone= true; 	}
				if(counter>50 && hundredfiftydone == false) 	{System.out.println(); hundredfiftydone= true; 	}
			}
			System.out.println("\n\nTotal number of peps is " + counter + " list size " + BIPs.size());
			st.close();
		}	catch (Exception e)	{
			System.err.println("Got an exception! ");			System.err.println(e.getMessage());
		}		
		return BIPs;
	}	
	
	public static ArrayList<Integer> getallPepsForPepType(Integer pepType, boolean showOutput, Connection conn){
		String type = "", pepTypeString =null;
		
		if (pepType == 0)
			pepTypeString = "All";
		else if (pepType == 1)
			pepTypeString = "Standards Track";
		else if (pepType == 2)
			pepTypeString = "Informational";
		else if (pepType == 3)
			pepTypeString = "Process";
		else
			System.out.format("Incorrect bip type");
		
		//Integer[] peps = new Integer[500];
		ArrayList<Integer> peps = new ArrayList<Integer>();
		
		try
		{
			String query= "";
			if (pepType==0)
				query = "SELECT bip, type FROM "+proposalTableName+" order by bip asc";
			else	    	  
				query = "SELECT bip, type FROM "+proposalTableName+" where type like '%" + pepTypeString + "%' order by bip asc";
			
			// create the java statement
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);
			Integer counter=0;
			
			System.out.println("PEP Type " + pepTypeString);  
			while (rs.next())
			{
				
				Integer pepNumber = rs.getInt("bip");
				type = rs.getString("type");
				//	        String lastName = rs.getString("last_name");
				//	        Date dateCreated = rs.getDate("date_created");
				//	        boolean isAdmin = rs.getBoolean("is_admin");
				//	        int numPoints = rs.getInt("num_points");
				
				// print the results
				//System.out.format("%s, %s, %s, %s, %s, %s\n", id, firstName, lastName, dateCreated, isAdmin, numPoints);
				peps.add(pepNumber);
				if (showOutput){
					//System.out.println("\t"+pepNumber);
					 System.out.print(pepNumber+ ((pepNumber%20==19) ? "\n" : " "));
				}
				counter++;
			}
			System.out.println("\n\nTotal number of peps in "+ pepTypeString + " is " + counter + " list size " + peps.size());
			st.close();
		}	catch (Exception e)	{
			System.err.println("Got an exception! ");
			System.err.println(e.getMessage());
		}
		
		
		//if author has comma, meaning multiple authors, then insert in authors array
		//String authorFiltered = "";
		
		// if it has brackets
		//paul@prescod.net (Paul Prescod)
//		if (author.contains("(") && author.contains(")")) 
//		{
//			 authorFiltered = author.substring(author.indexOf("(")+1,author.indexOf(")"));
//			 author = authorFiltered;
//		}
		
		return peps;
	}	
	
	public static String getPepTypeForPep(Integer bip,Connection conn){
		String pepTypeString =null;		
		//Integer[] peps = new Integer[500];
		ArrayList<Integer> peps = new ArrayList<Integer>();
		
		try	{
			String query= "";
			if (bip<0)
				System.out.println("Error no bip as " + bip);
			else	    	  
				query = "SELECT type FROM "+proposalTableName+" where bip = " + bip + ";";

			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);			
			 
			if (rs.next())  {
				pepTypeString = rs.getString("type");	

			}
			//System.out.println("\n\nTotal number of peps in "+ pepTypeString + " is " + counter + " list size " + peps.size());
			st.close();
		}	catch (Exception e)	{
			System.err.println("Got an exception! ");			System.err.println(e.getMessage());
		}		
		return pepTypeString.trim();
	}
	
	//looks like this is used when processing is stuck
	public static Integer getMaxProcessedProposalFromStorageTable(Connection conn, ProcessingRequiredParameters prp){
		Integer maxProposal=0;	String query=""; //default value
		try	{
			if(prp.getScriptPurpose().toLowerCase().equals("relationextraction")) { //dec 2018 sentence writingto db table is now included under relation extraction
				System.out.println("Get PEPID from restart = relationextraction! ");
				//query= "SELECT max(bip) as max FROM allsentences;";
			//}
			//else{ 	System.out.println("Get MID from restart = other! ");
				query= "SELECT max(bip) as max FROM extractedrelations_clausie;";
			}
			else if (prp.getScriptPurpose().toLowerCase().equals("processmining")) {
				query= "SELECT max(bip) as max FROM results;";
			}
			else {
				System.out.println("Cannot get ScriptPurpose for retrieving max bip .exiting");  System.exit(0);
			}
			Statement st = conn.createStatement();			ResultSet rs = st.executeQuery(query);
			if (rs.next())  {
				maxProposal = rs.getInt("max");
			}
			//System.out.println("\n\nTotal number of peps in "+ pepTypeString + " is " + counter + " list size " + peps.size());
			st.close();
		}	catch (Exception e)	{
			System.err.println("Got an exception! ");			System.err.println(e.getMessage());
		}		
		return maxProposal;
	}
	
	//looks like this is used when processing is stuck
	public static Integer getMaxProcessedMessageIDForProposalFromStorageTable(Connection conn, Integer proposal,ProcessingRequiredParameters prp){
		Integer maxProcessedMessageIDForProposal=0;	//default value	
		
		try	{
			String query= "";	
			if(prp.getScriptPurpose().toLowerCase().equals("relationextraction")) {	System.out.println("Get MID from restart = relationextraction! ");
				query = "SELECT max(messageid) as maxmid FROM extractedrelations_clausie where bip= "+proposal+";";
			}
			else if (prp.getScriptPurpose().toLowerCase().equals("processmining")) {	System.out.println("Get MID from restart = processmining! ");
				query = "SELECT max(messageid) as maxmid FROM results where bip= "+proposal+";";
			}
			else {
				System.out.println("Cannot get ScriptPurpose for retrieving max bip ..exiting"); System.exit(0);
			}
			Statement st = conn.createStatement();			ResultSet rs = st.executeQuery(query);
			if (rs.next())  {
				maxProcessedMessageIDForProposal = rs.getInt("maxmid");
			}
			//System.out.println("\n\nTotal number of peps in "+ pepTypeString + " is " + counter + " list size " + peps.size());
			st.close();
		}	catch (Exception e)	{
			System.err.println("Got an exception! ");			System.err.println(e.getMessage());
		}		
		return maxProcessedMessageIDForProposal;
	}
	
	public static String getPepTitleForPep(Integer proposal,Connection conn){
		String pepTitle ="";	
		try	{
			String query= "";
			if (proposal<0)
				System.out.println("Error no bip as " + proposal);
			else	    	  
				query = "SELECT title FROM "+proposalTableName+" where bip = " + proposal + ";";

			Statement st = conn.createStatement();			ResultSet rs = st.executeQuery(query);
			if (rs.next())  
				pepTitle = rs.getString("title").toLowerCase().trim();			
			else { 
				//System.out.println("No title found for proposal: "+ proposal);	
			}
			//System.out.println("\n\nTotal number of peps in "+ pepTypeString + " is " + counter + " list size " + peps.size());
			st.close();
		}	catch (Exception e)	{
			System.err.println("Got an exception! ");			System.err.println(e.getMessage());
		}		
		return pepTitle;
	}

	public static Map<Integer,String> populateAllproposalTitleandNumbers(Map<Integer,String> proposalDetails, Connection conn) {
		String title = "";
		String proposalIdentifier = null; 
		try
		{

			//write to properties file
			PropertiesFile wpf = new PropertiesFile();
			// wpf.WriteToPropertiesFile("includeEmptyRows", includeEmptyRows.toString());
			//includeStateData
			proposalIdentifier = wpf.readFromPropertiesFile("proposalIdentifier",false).toLowerCase();
			//System.out.println("proposalIdentifier: " +proposalIdentifier);	


			String query = "SELECT "+proposalIdentifier+", title FROM "+proposalIdentifier+"details order by "+proposalIdentifier+";";
			Statement st = conn.createStatement();	      
			ResultSet rs = st.executeQuery(query);     
			while (rs.next()) {
				int proposalNumber = rs.getInt(proposalIdentifier);
				title = rs.getString("title").trim();
				title = RemovePunct(title);
				title = title.toLowerCase();
				//title = processWord(title);
				// title = RemovePunct(title);
				//		        String lastName = rs.getString("last_name");
				//		        Date dateCreated = rs.getDate("date_created");
				//		        boolean isAdmin = rs.getBoolean("is_admin");
				//		        int numPoints = rs.getInt("num_points");	        
				proposalDetails.put(proposalNumber,title);
				//		        System.out.println(proposalNumber + " " +title);
			}
			st.close();
		}
		catch (Exception e) {
			System.err.println("Got an exception! ");			System.err.println(e.getMessage());
		}		
		return proposalDetails;
	}

	 static String RemovePunct(String input) 	  {
			char[] output = new char[input.length()];
			int i = 0;
			for (char ch : input.toCharArray()) {
				if (Character.isLetterOrDigit(ch) || Character.isWhitespace(ch)) 
					output[i++] = ch;				
			}
			return new String(output, 0, i);
	  }
}
