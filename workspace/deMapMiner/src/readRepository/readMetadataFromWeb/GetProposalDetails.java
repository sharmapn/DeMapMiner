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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import connections.MysqlConnect;
import miner.process.PythonSpecificMessageProcessing;
import connections.PropertiesFile;
import utilities.GetDate;
//oct 2018 ..the other script (GetProposalDetailswebpage.java) used as this one was not reading titles spread over two lines
public class GetProposalDetails {
	
	static Map peps = new HashMap();
	//authors arraylist
	ArrayList authors = new ArrayList();	
	static MysqlConnect mc = new MysqlConnect();
	static PythonSpecificMessageProcessing pms = new PythonSpecificMessageProcessing();
	static GetDate gd = new GetDate();  //new date parser centrally located in seprate package for use from all other package code
	//The following will miss PEP 0, and we have to insert it manually
    //insert into pepdetails (pep,author,type,title) 
	//values (0,'David Goodger <goodger at python.org>, Barry Warsaw <barry at python.org>','Informational','Index of Python Enhancement Proposals (PEPs)')
	static String proposalTableName = "pepdetails";  //dec 2018 it was 'pepdetails2'		
	static Connection conn;
	
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
	    URL url;
	    InputStream is = null;
	    BufferedReader br;
	    String line;
	    
	    //write final data
	    File fout = new File("c:\\scripts\\PEPNumberTitleNEW.txt");
		FileOutputStream fos = new FileOutputStream(fout);	 
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

	    try {
	        url = new URL("https://github.com/python/peps");	        is = url.openStream();  // throws an IOException
	        br = new BufferedReader(new InputStreamReader(is));	       // File f = new File("c:\\scripts\\output\\a.txt");

	        while ((line = br.readLine()) != null) {
	            if(line.contains("href=")){
	                //System.out.println(line.trim());
	                String[] parts = line.split("href");//"<"
	                for(String p: parts){
	                	if(p.contains("/python/peps/blob/master")){	
	                		System.out.println("\t\t"+ p.trim());
	                		int start = p.indexOf('"');
	                		int end = p.indexOf('"', start+1);
	                		String link = p.substring(start+1, end);
	                		link = "https://github.com" + link; 			//https://github.com/python/peps/blob/master/pep-0001.txt
	                		if (link.endsWith(".txt")&& !link.endsWith("README.txt")){	                				                			
	                			String pepno = link.substring(link.length() - 12);		//pep-3140.txt	                			
	                			pepno = pepno.replace(".txt","");
	                			pepno = pepno.replace("pep-","");
	                			System.out.println("\t\t"+pepno); 
	                			int pepNumber = Integer.parseInt(pepno);
	                			//506, 514, 
	                			
	                			//choose what you want to do here
	                			//some links have problems
	                			//if (pepNumber !=519 && pepNumber !=527 && pepNumber !=528 && pepNumber !=526 && pepNumber !=529 //these peps are no more problematic
	                			if( pepNumber ==3143) { //check whats wrong with this pep
	                				//populateDBWithPEPDetails(link, conn);
	                			}
	                			else {
	                				populateDBWithPEPDetails(link, conn);
	                			}
	                			Integer v_pepNumber = 308;
	                			
	                			if(pepNumber==v_pepNumber)   //(pepNumber !=506 && pepNumber !=514)
	                				readFileGetPEPAuthor(link, bw);
	                				
	                			//downloadFileFromURL(link,f);
//	                			System.out.println(start + " " + end+ " " + " link= " + link); // + " line= "+ p);
	                		}
	                	}
	                }
	            }
	        }
	    } catch (MalformedURLException mue) {
	         mue.printStackTrace();
	    } catch (IOException ioe) {
	         ioe.printStackTrace();
	    } finally {
	        try {
	            if (is != null) is.close();
	        } catch (IOException ioe) {
	            //exception
	        }
	    }	    
	    bw.close();	    
	    System.out.println("Total peps: " + peps.size());
	    /*
	    System.out.print("Enter pep numbet to get title:");
	    String searchKey = System.console().readLine();	    
	    
	    if(peps.containsKey(searchKey))
	       System.out.println("Found total " + peps.get(searchKey) + " " + searchKey + " peps!\n");
	    else
	    	 System.out.println("Pep " + " " + searchKey + " not found!\n");
	    */
	    
	    //return "";
	    
	}
	
	static void populateDBWithPEPDetails(String v_link, Connection conn) throws IOException{
		URL peplink = new URL(v_link);
        BufferedReader in = new BufferedReader(
        new InputStreamReader(peplink.openStream()));
        String inputLine,pep = "", title = "", pepauthorLine = "", type = "", bdfl_delegate = "", pythonVersion = "", created = "";
        
        while ((inputLine = in.readLine()) != null){      	
        	if (inputLine.contains(">PEP:")){
        		int start = inputLine.indexOf(">PEP:");    		int end = inputLine.indexOf("</td>", start+5);   pep = inputLine.substring(start+5, end);        	pep = pep.trim();      		//System.out.println(pep);
            	
            }if (inputLine.contains(">Title:")){
            	int start = inputLine.indexOf(">Title:");  		int end = inputLine.indexOf("</td>", start+7);    title = inputLine.substring(start+7, end);        		//System.out.println(title);
            }
            if (inputLine.contains(">Author:")){
            	int start = inputLine.indexOf(">Author:");    	int end = inputLine.indexOf("</td>", start+8);    pepauthorLine = inputLine.substring(start+8, end);        		//System.out.println(title);
            }  
            if (inputLine.contains(">Type:")){
            	int start = inputLine.indexOf(">Type:");     int end = inputLine.indexOf("</td>", start+6);        type = inputLine.substring(start+6, end);        		//System.out.println(title);
            } 
            if (inputLine.contains(">BDFL-Delegate:")){
            	int start = inputLine.indexOf(">BDFL-Delegate:");  		int end = inputLine.indexOf("</td>", start+15);   bdfl_delegate = inputLine.substring(start+15, end);  	//System.out.println(title);
            } 
            if (inputLine.contains(">Python-Version:")){
            	int start = inputLine.indexOf(">Python-Version:"); 		int end = inputLine.indexOf("</td>", start+16);    pythonVersion = inputLine.substring(start+16, end); 	//System.out.println(title);
            } 
            
            if (inputLine.contains(">Created:")){
            	int start = inputLine.indexOf(">Created:");        		int end = inputLine.indexOf("</td>", start+9);     created = inputLine.substring(start+9, end).trim();  	//System.out.println(title);
            }
        } 
        String authorCorrected="",bdfl_delegateCorrected="",newName ="";
        //may 2018 ...update these variables..now integerated here but were part of a separate update script
        
		
		//This script populates the author email address in pep states table
		//then we can use java to match -- match author and senderemail in allmessages with authorCorrected, authorEmail in pepdetails 
		//get the email from the author column
		// sometimes pep authors change during the course of discussion - maybe we can get this information from pepstates table
        //seprating multiple authors but not storing them yet....
		String combined ="", permanentAuthor="", permanentAuthorEmail="";
		if(pepauthorLine.contains(","))
		{
			String[] authorsLine = pepauthorLine.split(",");
			for (String authorLine: authorsLine){
				 // permanentAuthor and permanentAuthorEmail
				 permanentAuthor=pms.extractFullAuthorFromString(authorLine);
				 permanentAuthorEmail=pms.returnAuthorEmail(pepauthorLine);
				 //System.out.println("\t Processed "+ pep + " " +permanentAuthor + "," + permanentAuthorEmail);
				 System.out.format("%7s, %120s, %40s, %30s", pep, pepauthorLine, permanentAuthor, permanentAuthorEmail); 		 System.out.println();
				 combined =combined + authorLine + ",";
//				 updateauthor(pep,author,conn);
			}
			//combined = combined.replaceAll(", $", "");
			combined = combined.substring(0, combined.lastIndexOf(","));
		}
		else {
			 permanentAuthor=pms.extractFullAuthorFromString(pepauthorLine);
			 permanentAuthorEmail=pms.returnAuthorEmail(pepauthorLine);
			 System.out.format("%7s, %120s, %40s, %30s", pep, pepauthorLine, permanentAuthor, permanentAuthorEmail); 			 System.out.println();
			 //System.out.println(pep + pepauthorLine + "\t Processed "+ " " +permanentAuthor + "," + permanentAuthorEmail);
		}
		
		//bdfl delegates - would normally be one or xero only, not multiple
		if (bdfl_delegate!=null) {							
			bdfl_delegateCorrected = pms.extractFullAuthorFromString(bdfl_delegate.trim()); //pms.getAuthorFromString(bdfl_delegate.trim());
		}
		
		System.out.println("pep " + pep + " newAuthor " + permanentAuthor + " new bdfl_delegate "+ bdfl_delegateCorrected);
        
//       if(pep!=null && title!=null){
//       	int pepNumber = Integer.parseInt(pep);
//        	peps.put(pep, title);
        System.out.println(pep+" "+title+" "+permanentAuthor+" "+type+ " "+ bdfl_delegate + " " + pythonVersion + " " + created);
        	///bw.write(pep+","+title);
    		//bw.newLine();
        insertinDB(pep,title,permanentAuthor,authorCorrected,permanentAuthorEmail, type, bdfl_delegate,bdfl_delegateCorrected,pythonVersion,created, conn);
//      }
        in.close();		
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
	
	public static void insertinDB(String pep,String title,String author,String author_corrected,String authorEmail, String type, String bdfl_delegate,  String bdfl_delegate_corrected, 
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
			String query = " insert into "+proposalTableName+"  (pep, title, author,authorcorrected,authorEmail,type,bdfl_delegate,bdfl_delegatecorrected,created,createdYear, python_version)"
					+ " values (?,?,?,?,?,?,?,?,?,?,?)";

			// create the mysql insert preparedstatement
			PreparedStatement preparedStmt = conn.prepareStatement(query);
			preparedStmt.setInt(1, Integer.parseInt(pep));		preparedStmt.setString(2, title);				preparedStmt.setString(3, author);	preparedStmt.setString(4, author);
			preparedStmt.setString(5, authorEmail);			preparedStmt.setString(6, type);					preparedStmt.setString(7, bdfl_delegate);		preparedStmt.setString(8, bdfl_delegate); 
			preparedStmt.setDate(9, sqlDate);			preparedStmt.setInt(10, createdYear);				preparedStmt.setString(11, pythonVersion);
			// preparedStmt.setBoolean(4, false);			// preparedStmt.setInt (5, 5000);
			// execute the preparedstatement
			preparedStmt.execute();
			createdYear = null; 
		}  catch (Exception e) {
			System.out.println(StackTraceToString(e)  );
			System.err.println("Got an exception!");
			System.err.println(e.getMessage());
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
		try		{
			//Connection conn = mc.connect();
			String query = "SELECT pep, author FROM "+proposalTableName+"  where pep = " + v_pepNumber;
			Statement st = conn.createStatement();			ResultSet rs = st.executeQuery(query);			
			// iterate through the java resultset
			if (rs.next())   {
				int pepNumber = rs.getInt("pep");				author = rs.getString("author");
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
			authorFiltered = author.substring(author.indexOf("(")+1,author.indexOf(")"));			author = authorFiltered;
		}
		
		return author;
	}
	
	//for all pep authors, get the pep author (can be multiple) and remove unecessary charaters and email address
	public static String correctPepAuthor(){
		String author = "";
		try		{
			//Connection conn = mc.connect();
			String query = "SELECT pep, author FROM "+proposalTableName+" ;";			Statement st = conn.createStatement();			ResultSet rs = st.executeQuery(query);
			
			// iterate through the java resultset
			while(rs.next())   {
				int pepNumber = rs.getInt("pep");				author = rs.getString("author");
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
		try		{
			Connection conn = mc.connect();
			String query = "SELECT pep, bdfl_delegate FROM "+proposalTableName+"  where pep = " + v_pepNumber;
			Statement st = conn.createStatement();			ResultSet rs = st.executeQuery(query);
			if (rs.next())    {
				int pepNumber = rs.getInt("pep");				delegate = rs.getString("bdfl_delegate");
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
		String inputLine, pep = null, title = null;
		
		while ((inputLine = in.readLine()) != null){      	
			if (inputLine.contains(">PEP:")){
				int start = inputLine.indexOf(">PEP:");				int end = inputLine.indexOf("</td>", start+5);
				pep = inputLine.substring(start+5, end);				pep = pep.trim();
				//System.out.println(pep);
				
			}if (inputLine.contains(">Title:")){
				int start = inputLine.indexOf(">Title:");				int end = inputLine.indexOf("</td>", start+7);				title = inputLine.substring(start+7, end);
				//System.out.println(title);
			}             
			
		} 
		//       if(pep!=null && title!=null){
		//       	int pepNumber = Integer.parseInt(pep);
		//        	peps.put(pep, title);
		System.out.println(pep+" "+title);		bw.write(pep+","+title);		bw.newLine();
		//      }
		in.close();		
	}
	
	static void readFileGetPEPAuthor(String v_link, BufferedWriter bw) throws IOException{
		//String v_link = "https://github.com/python/peps";
		//v_link +=v_pepNumber;
		URL peplink = new URL(v_link);
        BufferedReader in = new BufferedReader(new InputStreamReader(peplink.openStream()));
        String inputLine, pep = null, author = null;
        
        while ((inputLine = in.readLine()) != null){      	
        	if (inputLine.contains(">PEP:")){
        		int start = inputLine.indexOf(">PEP:");        		int end = inputLine.indexOf("</td>", start+5);        		pep = inputLine.substring(start+5, end);        		pep = pep.trim();
        		//System.out.println(pep);
            	
            }if (inputLine.contains(">Author:")){
            	int start = inputLine.indexOf(">Author:");        		int end = inputLine.indexOf("</td>", start+7);        		author = inputLine.substring(start+7, end);        		//System.out.println(title);
            }             
            
        } 
 //       if(pep!=null && title!=null){
 //       	int pepNumber = Integer.parseInt(pep);
//        	peps.put(pep, title);
        	System.out.println(pep+" "+author);
        	//xx bw.write(pep+","+title);
    		//xx bw.newLine();
  //      }
        in.close();		
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
			System.out.format("Incorrect pep type");
		
		//Integer[] peps = new Integer[500];
		ArrayList<Integer> peps = new ArrayList<Integer>();
		
		try
		{
			String query= "";
			if (pepType==0)
				query = "SELECT pep, type FROM "+proposalTableName+"  order by pep asc";
			else	    	  
				query = "SELECT pep, type FROM "+proposalTableName+"  where type like '%" + pepTypeString + "%' order by pep asc";
			
			// create the java statement
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);
			Integer counter=0;
			
			System.out.println("PEP Type " + pepTypeString);  
			while (rs.next())
			{
				
				Integer pepNumber = rs.getInt("pep");
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
			System.err.println("Got an exception! ");			System.err.println(e.getMessage());
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
	
	public static String getPepTypeForPep(Integer pep,Connection conn){
		String pepTypeString =null;		
		//Integer[] peps = new Integer[500];
		ArrayList<Integer> peps = new ArrayList<Integer>();
		
		try	{
			String query= "";
			if (pep<0)
				System.out.println("Error no pep as " + pep);
			else	    	  
				query = "SELECT type FROM "+proposalTableName+"  where pep = " + pep + ";";

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
	
	public static Integer getMaxProcessedProposalFromRelationsTable(Connection conn){
		Integer maxProposal=0;	//default value	
		
		try	{
			String query= "";				    	  
				query = "SELECT max(pep) as max FROM extractedrelations_clausie;";
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
	
	public static Integer getMaxProcessedMessageIDForProposalFromRelationsTable(Connection conn, Integer proposal){
		Integer maxProcessedMessageIDForProposal=0;	//default value	
		
		try	{
			String query= "";				    	  
				query = "SELECT max(messageid) as maxmid FROM extractedrelations_clausie where pep= "+proposal+";";
			Statement st = conn.createStatement();			ResultSet rs = st.executeQuery(query);
			if (rs.next())  {
				maxProcessedMessageIDForProposal = rs.getInt("maxmid");
			}
			//System.out.println("\n\nTotal number of peps in "+ pepTypeString + " is " + counter + " list size " + peps.size());
			st.close();
		}	catch (Exception e)	{
			System.err.println("Got an exception 232! ");			System.err.println(e.getMessage());
		}		
		return maxProcessedMessageIDForProposal;
	}
	
	public static String getPepTitleForPep(Integer proposal,Connection conn){
		String pepTypeString =null;		
		//Integer[] peps = new Integer[500];
		//ArrayList<Integer> peps = new ArrayList<Integer>();
		
		try	{
			String query= "";
			if (proposal<0)
				System.out.println("Error no pep as " + proposal);
			else	    	  
				query = "SELECT title FROM "+proposalTableName+"  where pep = " + proposal + ";";

			Statement st = conn.createStatement();			ResultSet rs = st.executeQuery(query);
			if (rs.next())  {
				pepTypeString = rs.getString("title");
			}
			else {
				System.out.println("No title found for proposal: "+ proposal);	
			}
			//System.out.println("\n\nTotal number of peps in "+ pepTypeString + " is " + counter + " list size " + peps.size());
			st.close();
		}	catch (Exception e)	{
			System.err.println("Got an exception! ");			System.err.println(e.getMessage());
		}		
		return pepTypeString;
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
			System.err.println("Got an exception! ");
			System.err.println(e.getMessage());
		}		
		return proposalDetails;
	}

	 static String RemovePunct(String input) 
	  {
			char[] output = new char[input.length()];
			int i = 0;
			for (char ch : input.toCharArray()) {
				if (Character.isLetterOrDigit(ch) || Character.isWhitespace(ch)) {
					output[i++] = ch;
				}
			}
			return new String(output, 0, i);
	  }
}
