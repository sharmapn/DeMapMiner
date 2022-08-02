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

//March 2021...new study to study the [post bdfl DM process...we we download all PEP metadat till now - march 2021

//Sept 2020...updated to read the discussions to field. This can be used for substate analysis as to which mailing list the PEP was intended to have discussions.

//oct 2018 ..this script used instead of the original GetProposalDetails.java, as this one reads titles spread over two lines

public class GetProposalDetailsWebPage {
	
	static Map peps = new HashMap();
	//authors arraylist
	ArrayList authors = new ArrayList();	
	static MysqlConnect mc = new MysqlConnect();
	static Connection conn;
	static PythonSpecificMessageProcessing pms = new PythonSpecificMessageProcessing();
	static GetDate gd = new GetDate();  //new date parser centrally located in seprate package for use from all other package code
	//The following will miss PEP 0, and we have to insert it manually
    //insert into pepdetails (pep,author,type,title) 
	//values (0,'David Goodger <goodger at python.org>, Barry Warsaw <barry at python.org>','Informational','Index of Python Enhancement Proposals (PEPs)')
	//commented in jam 2021
	//static String proposalTableName = "pepdetails2020b"; //"pepdetails";	//dec 2018 ..was pepdetails2 before
	static String proposalTableName = "pepdetails2021"; //till march 2021 was `pepdetails2020' //"pepdetails";	//dec 2018 ..was pepdetails2 before
	
	static Integer counterA=0, counterB=0, counterC=0;
	static ArrayList<Integer> proposalDone = new ArrayList<>();
	static String proposalIdentifier = ""; //"pep";
	public static void main(String[] args) throws IOException {
		conn = mc.connect();
		proposalTableName = "pepdetails2021"; 	proposalIdentifier = "pep";
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
	    File fout = new File("c:\\scripts\\PEPNumberTitleNEW2021.txt");		FileOutputStream fos = new FileOutputStream(fout); 		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

	    try {		 
	        url = new URL("https://www.python.org/dev/peps"); //https://github.com/python/peps");           // throws an IOException
	        is = url.openStream();
	        br = new BufferedReader(new InputStreamReader(is));	       // File f = new File("c:\\scripts\\output\\a.txt");
	        List<String> uniqueProposalLinkList = new ArrayList<String>();
	        
	        while ((line = br.readLine()) != null) {
	            if(line.contains("href=")){
	                //System.out.println(line.trim());
	                String[] parts = line.split("href");//"<"
	                for(String p: parts){
	                	//old way deleted
	                	//using python main links now ..not github links
	                	//if(link.contains("https://www.python.orghttps")) {}
	                	if(p.contains("/dev/peps") && !p.contains("orghttps") && !p.contains("orghttp") && !p.contains(".rss")  ) {   //("/python/peps/blob/master")){	
//	                		System.out.println("\t\tp "+ p.trim());
	                		int start = p.indexOf('"');	                		int end = p.indexOf('"', start+1);
	                		String link = p.substring(start+1, end);
	                		
	                		link = "https://www.python.org"+link; //     "https://github.com" + link; 			//https://github.com/python/peps/blob/master/pep-0001.txt
	                		if (link.contains("/pep-")) {
	                			  //for (String pl : uniqueProposalLinkList) {
	                			     if (!uniqueProposalLinkList.contains(link)) {
	                			    	 uniqueProposalLinkList.add(link);
	                			      }
	                			  //}	                		  
	                		}
	                	}
	                }
	            }
	        }
	        System.out.println("Total Unique Links: "+ uniqueProposalLinkList.size());
	        Collections.sort(uniqueProposalLinkList, String.CASE_INSENSITIVE_ORDER);
	        //Collections.sort(uniqueProposalLinkList, );
	        System.out.println("Ordered List Links: ");
	  	    for (String pl : uniqueProposalLinkList) {
	  	    	//System.out.println(pl);
		    }
	        
	        //only go through distinct links	        
	        for (String link : uniqueProposalLinkList) {
	        	String pepno = link.replace("https://www.python.org/dev/peps/pep-","");		//pep-3140.txt	                			
    			pepno = pepno.replace(".txt","");	                			pepno = pepno.replace("pep-","");
//    			System.out.println("\t\t here "+pepno); 
    			int pepNumber = Integer.parseInt(pepno);
	        	//if (pepNumber > 572) { //3145 || pepNumber== 3151) { //pepNumber !=519 && pepNumber !=527 && pepNumber !=528 && pepNumber !=526 && pepNumber !=529 && pepNumber !=3143) {
    				populateDBWithPEPDetails(link, conn);
    			//}
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
        String inputLine,pep = "", title = "", pepauthorLine = "", type = "", bdfl_delegate = "", pythonVersion = "", created = "", sponsor = "", discussionsTo = "";
        
        ArrayList<Integer> values = new ArrayList<>();
        
        while ((inputLine = in.readLine()) != null){      	
        	if (inputLine.contains(">PEP:<")){   //<tr class="field"><th class="field-name">PEP:</th><td class="field-body">1</td>
        		System.out.println("inputLine- "+ inputLine );  //System.out.println("FirstAuthor- "+ inputLine );
        		inputLine = inputLine.substring(0,inputLine.length()-1); //get rid of last '>'
        		//System.out.println("FirstAuthor- "+ inputLine );
        		int start = inputLine.lastIndexOf(">");    		int end = inputLine.lastIndexOf("</td"); 
        		//pep = pep.replace("PEP:</th><td class="field-body">");
        		pep = inputLine.substring(start+1, end);        	pep = pep.trim();      		System.out.println("pep "+pep);
        		
        		//if pep done before we exit
        		boolean found = false;
        		for (Integer k: proposalDone) {
        			if(k.equals(pep)) {
        				found=true;
        				return;        
        			}
        			if(!found) {
        				proposalDone.add(Integer.valueOf(pep));
        			}
        		}
            	
            }if (inputLine.contains(">Title:<")){
            	if(inputLine.endsWith("</td>") ) {
	            	inputLine = inputLine.substring(0,inputLine.length()-1); 
	            	int start = inputLine.lastIndexOf(">");  		int end = inputLine.lastIndexOf("</td");    
	            	title = inputLine.substring(start+1, end);        		System.out.println("title "+title);
            	}
            	else {
            		//handle cases where title goes into second line
            	}
            }
            if (inputLine.contains(">Author:<")){
//            	int start = inputLine.indexOf(">Author:");    	int end = inputLine.lastIndexOf("</td>", start+8);    pepauthorLine = inputLine.substring(start+8, end);        		System.out.println("pepauthorLine "+ pepauthorLine);
            	//if line ends with comma, we know there is another author in next line
 //           	System.out.println("FirstAuthor- "+ inputLine + " pepauthorLine: "+ pepauthorLine);           	
            	// <tr class="field"><th class="field-name">Author:</th><td class="field-body">Guido van Rossum &lt;guido at python.org&gt;,
            	// Barry Warsaw &lt;barry at python.org&gt;,
            	// Nick Coghlan &lt;ncoghlan at gmail.com&gt;</td> 
            	if(inputLine.endsWith(",") ) {
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
            } 
            
            if (inputLine.contains(">Type:<")){
            	//int start = inputLine.indexOf(">Type:");     int end = inputLine.lastIndexOf("</td>", start+6);        type = inputLine.substring(start+6, end);        		//System.out.println(title);
            	inputLine = inputLine.substring(0,inputLine.length()-1); 
            	int start = inputLine.lastIndexOf(">");  		int end = inputLine.lastIndexOf("</td");    
            	type = inputLine.substring(start+1, end);    System.out.println("Type "+type); 
            } 
            if (inputLine.contains(">BDFL-Delegate:<")){
            	//int start = inputLine.indexOf(">BDFL-Delegate:");  		int end = inputLine.lastIndexOf("</td>", start+15);   bdfl_delegate = inputLine.substring(start+15, end);  	//System.out.println(title);
            	inputLine = inputLine.substring(0,inputLine.length()-1); 
            	int start = inputLine.lastIndexOf(">");  		int end = inputLine.lastIndexOf("</td");    
            	bdfl_delegate = inputLine.substring(start+1, end);    System.out.println("bdfl_delegate "+bdfl_delegate); 
            } 
            if (inputLine.contains(">Python-Version:<")){
            	//int start = inputLine.indexOf(">Python-Version:"); 		int end = inputLine.lastIndexOf("</td>", start+16);    pythonVersion = inputLine.substring(start+16, end); 	//System.out.println(title);
            	inputLine = inputLine.substring(0,inputLine.length()-1); 
            	int start = inputLine.lastIndexOf(">");  		int end = inputLine.lastIndexOf("</td");    
            	pythonVersion = inputLine.substring(start+1, end);    System.out.println("pythonVersion "+pythonVersion); 
            } 
            
            if (inputLine.contains(">Created:<")){
            	//int start = inputLine.indexOf(">Created:");        		int end = inputLine.lastIndexOf("</td>", start+9);     created = inputLine.substring(start+9, end).trim();  	//System.out.println(title);
            	inputLine = inputLine.substring(0,inputLine.length()-1); 
            	int start = inputLine.lastIndexOf(">");  		int end = inputLine.lastIndexOf("</td");    
            	created = inputLine.substring(start+1, end);    System.out.println("created "+created); 
            }
            
            //march 2021 added
            if (inputLine.contains(">Discussions-To:<")){
            	//int start = inputLine.indexOf(">Created:");        		int end = inputLine.lastIndexOf("</td>", start+9);     created = inputLine.substring(start+9, end).trim();  	//System.out.println(title);
            	inputLine = inputLine.substring(0,inputLine.length()-1); 
            	int start = inputLine.lastIndexOf(">");  		int end = inputLine.lastIndexOf("</td");    
            	discussionsTo = inputLine.substring(start+1, end);    System.out.println("discussionsTo "+discussionsTo); 
            }
            
            if (inputLine.contains(">Sponsor:<")){
            	//int start = inputLine.indexOf(">Created:");        		int end = inputLine.lastIndexOf("</td>", start+9);     created = inputLine.substring(start+9, end).trim();  	//System.out.println(title);
            	inputLine = inputLine.substring(0,inputLine.length()-1); 
            	int start = inputLine.lastIndexOf(">");  		int end = inputLine.lastIndexOf("</td");    
            	sponsor = inputLine.substring(start+1, end);    System.out.println("sponsor "+sponsor); 
            }
            
        } 
        String authorCorrected="",bdfl_delegateCorrected="",newName ="";
        //may 2018 ...update these variables..now integerated here but were part of a separate update script
        System.out.println("A " + counterA + " B "+ counterB + " C "+ counterC);
		
		//This script populates the author email address in pep states table
		//then we can use java to match -- match author and senderemail in allmessages with authorCorrected, authorEmail in pepdetails 
		//get the email from the author column
		// sometimes pep authors change during the course of discussion - maybe we can get this information from pepstates table
        //seprating multiple authors but not storing them yet....
		String combinedAuthor ="", combinedEmail="", permanentAuthor="", permanentAuthorEmail="";
		if(pepauthorLine.contains(",")){
			//String[] authorsLine = pepauthorLine.split(",");
			for (String authorLine: pepauthorLine.split(",")){
				 // permanentAuthor and permanentAuthorEmail
				 permanentAuthor=pms.extractFullAuthorFromString(authorLine); //remove chars
				 permanentAuthorEmail=pms.returnAuthorEmail(authorLine);
				 //System.out.println("\t Processed "+ pep + " " +permanentAuthor + "," + permanentAuthorEmail);
				 System.out.format("%7s, %120s, %40s, %30s", pep, pepauthorLine, permanentAuthor, permanentAuthorEmail); 		 System.out.println();
				 combinedAuthor =combinedAuthor + permanentAuthor + ",";
				 combinedEmail = combinedEmail + permanentAuthorEmail + ",";
//				 updateauthor(pep,author,conn);
			}
			combinedAuthor = combinedAuthor.substring(0, combinedAuthor.lastIndexOf(","));		combinedEmail = combinedEmail.substring(0, combinedEmail.lastIndexOf(","));	
			//combined = combined.substring(0, combined.lastIndexOf(","));
		}
		else {
			 permanentAuthor=pms.extractFullAuthorFromString(pepauthorLine);
			 permanentAuthorEmail=pms.returnAuthorEmail(pepauthorLine);
			 System.out.format("%7s, %120s, %40s, %30s", pep, pepauthorLine, permanentAuthor, permanentAuthorEmail); 			 System.out.println();
			 //System.out.println(pep + pepauthorLine + "\t Processed "+ " " +permanentAuthor + "," + permanentAuthorEmail);
			 combinedAuthor = permanentAuthor;
			 combinedEmail = permanentAuthorEmail;
		}
		
		//bdfl delegates - would normally be one or xero only, not multiple
		if (bdfl_delegate!=null) {							
			bdfl_delegateCorrected = pms.extractFullAuthorFromString(bdfl_delegate.trim()); //pms.getAuthorFromString(bdfl_delegate.trim());
		}
		
		System.out.println("pep " + pep + " newAuthor " + permanentAuthor + " new bdfl_delegate "+ bdfl_delegateCorrected);
        
//       if(pep!=null && title!=null){
//       	int pepNumber = Integer.parseInt(pep);
//        	peps.put(pep, title);
        System.out.println(pep+" "+title+" "+permanentAuthor+" "+type+ " "+ bdfl_delegate + " " + pythonVersion + " " + created + " " + sponsor + " "+ discussionsTo);
        	///bw.write(pep+","+title);
    		//bw.newLine();
        //insertinDB(pep,title,permanentAuthor,authorCorrected,permanentAuthorEmail, type, bdfl_delegate,bdfl_delegateCorrected,pythonVersion,created, conn);
        insertinDB(pep,title,combinedAuthor,authorCorrected,combinedEmail, type, bdfl_delegate,bdfl_delegateCorrected,pythonVersion,created, sponsor, discussionsTo, conn);
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
			String pythonVersion, String created, String sponsor, String discussionsTo, Connection conn)
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
			String query = " insert into "+proposalTableName+" (pep, title, author,authorcorrected,authorEmail,type,bdfl_delegate,bdfl_delegatecorrected,created,createdYear, python_version, sponsor, discussionsTo)"
					+ " values (?,?,?,?,?,?,?,?,?,?,?,?,?)";

			// create the mysql insert preparedstatement
			PreparedStatement preparedStmt = conn.prepareStatement(query);
			preparedStmt.setInt(1, Integer.parseInt(pep));		preparedStmt.setString(2, title);				preparedStmt.setString(3, author);				preparedStmt.setString(4, author);
			preparedStmt.setString(5, authorEmail);				preparedStmt.setString(6, type);				preparedStmt.setString(7, bdfl_delegate);		preparedStmt.setString(8, bdfl_delegate); 
			preparedStmt.setDate(9, sqlDate);					preparedStmt.setInt(10, createdYear);			preparedStmt.setString(11, pythonVersion);		preparedStmt.setString(12, sponsor);
			preparedStmt.setString(13, discussionsTo);
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
			String query = "SELECT distinct pep, author FROM "+proposalTableName+" where pep = " + v_pepNumber;
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);
			
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
			authorFiltered = author.substring(author.indexOf("(")+1,author.indexOf(")"));
			author = authorFiltered;
		}
		
		return author;
	}
	
	//for all pep authors, get the pep author (can be multiple) and remove unecessary charaters and email address
	public static String correctPepAuthor(){
		String author = "";
		try
		{
			//Connection conn = mc.connect();
			String query = "SELECT distinct pep, author FROM "+proposalTableName+";";
			Statement st = conn.createStatement();			ResultSet rs = st.executeQuery(query);
			
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
		try
		{
			Connection conn = mc.connect();
			String query = "SELECT distinct pep, bdfl_delegate FROM "+proposalTableName+" where pep = " + v_pepNumber;
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);
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
				int start = inputLine.indexOf(">Title:");				int end = inputLine.indexOf("</td>", start+7);
				title = inputLine.substring(start+7, end);
				//System.out.println(title);
			}             
			
		} 
		//       if(pep!=null && title!=null){
		//       	int pepNumber = Integer.parseInt(pep);
		//        	peps.put(pep, title);
		System.out.println(pep+" "+title);
		bw.write(pep+","+title);		bw.newLine();
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
        		int start = inputLine.indexOf(">PEP:");        		int end = inputLine.indexOf("</td>", start+5);
        		pep = inputLine.substring(start+5, end);        		pep = pep.trim();
        		//System.out.println(pep);
            	
            }if (inputLine.contains(">Author:")){
            	int start = inputLine.indexOf(">Author:");        		int end = inputLine.indexOf("</td>", start+7);
        		author = inputLine.substring(start+7, end);
        		//System.out.println(title);
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
		
		try		{
			String query= "";
			if (pepType==0)
				query = "SELECT pep, type FROM "+proposalTableName+" order by pep asc";
			else	    	  
				query = "SELECT pep, type FROM "+proposalTableName+" where type like '%" + pepTypeString + "%' order by pep asc";
			
			// create the java statement
			Statement st = conn.createStatement();			ResultSet rs = st.executeQuery(query);
			Integer counter=0;			
			System.out.println("PEP Type " + pepTypeString);  
			while (rs.next())	{				
				Integer pepNumber = rs.getInt("pep");				type = rs.getString("type");
				//	        String lastName = rs.getString("last_name");				//	        Date dateCreated = rs.getDate("date_created");
				//	        boolean isAdmin = rs.getBoolean("is_admin");				//	        int numPoints = rs.getInt("num_points");
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
//		if (author.contains("(") && author.contains(")"))		{
//			 authorFiltered = author.substring(author.indexOf("(")+1,author.indexOf(")"));
//			 author = authorFiltered;
//		}		
		return peps;
	}	
	
	public static ArrayList<Integer> getallPepsForPepTypeb4(Integer pepType, boolean showOutput, Connection conn, String pepcreatedbeforedate){
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
		
		try		{
			String query= "";
			if (pepType==0)
				query = "SELECT pep, type FROM "+proposalTableName+" where created <= "+pepcreatedbeforedate+" order by pep asc";
			else	    	  
				query = "SELECT pep, type FROM "+proposalTableName+" where created <= "+pepcreatedbeforedate+" AND type like '%" + pepTypeString + "%' order by pep asc";
			
			// create the java statement
			Statement st = conn.createStatement();			ResultSet rs = st.executeQuery(query);
			Integer counter=0;			
			System.out.println("PEP Type " + pepTypeString);  
			while (rs.next())	{				
				Integer pepNumber = rs.getInt("pep");				type = rs.getString("type");
				//	        String lastName = rs.getString("last_name");				//	        Date dateCreated = rs.getDate("date_created");
				//	        boolean isAdmin = rs.getBoolean("is_admin");				//	        int numPoints = rs.getInt("num_points");
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
//		if (author.contains("(") && author.contains(")"))		{
//			 authorFiltered = author.substring(author.indexOf("(")+1,author.indexOf(")"));
//			 author = authorFiltered;
//		}		
		return peps;
	}	
	
	public static String getPepTypeForPep(Integer pep,Connection conn){
		String pepTypeString =null; 	//Integer[] peps = new Integer[500];
		ArrayList<Integer> peps = new ArrayList<Integer>();
		
		try	{
			String query= "";
			if (pep<0)
				System.out.println("Error no pep as " + pep);
			else	    	  
				query = "SELECT type FROM "+proposalTableName+" where pep = " + pep + ";";

			Statement st = conn.createStatement();			ResultSet rs = st.executeQuery(query);			
			 
			if (rs.next())  
				pepTypeString = rs.getString("type");	

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
		proposalIdentifier = prp.getProposalIdentifier();
		try	{
			System.out.println("\n\nproposalIdentifier("+proposalIdentifier+"): ");
			if(prp.getScriptPurpose().toLowerCase().equals("relationextraction")) { //dec 2018 sentence writingto db table is now included under relation extraction
				System.out.println("Get PEPID from restart = relationextraction! ");
				//query= "SELECT max(pep) as max FROM allsentences;";
			//}
			//else{ 	System.out.println("Get MID from restart = other! ");
				query= "SELECT max(" + proposalIdentifier + ") as max FROM extractedrelations_clausie";
			}
			else if (prp.getScriptPurpose().toLowerCase().equals("processmining")) {
				query= "SELECT max("+proposalIdentifier+") as max FROM results;";	
			}
			else if (prp.getScriptPurpose().toLowerCase().equals("onlysentenceextractionfortopicmodelling")) {
				query= "SELECT max(proposal) as max FROM allsentences;";	
			}
			else if (prp.getScriptPurpose().toLowerCase().equals("reasonextraction")) { //feb 2019, not in prop file, just for restart in reason extraction
				query= "SELECT max(proposal) as max FROM autoextractedreasoncandidatemessages;";	
			}
			//april 2019
			else if (prp.getScriptPurpose().toLowerCase().equals("currentandnearbysentenceretrievalforterms")) { //feb 2019, not in prop file, just for restart in reason extraction
				//query= "SELECT max(proposal) as max FROM autoextractedreasoncandidatemessages;";
				System.out.println("Ge");
				return 0;
			}
			else {
				System.out.println("Cannot get ScriptPurpose for retrieving max pep .exiting 1");  System.exit(0);
			}
			Statement st = conn.createStatement();			ResultSet rs = st.executeQuery(query);
			if (rs.next())  {
				maxProposal = rs.getInt("max");
			}
			System.out.println("\n\nMax Proposal from DB for proposalIdentifier("+proposalIdentifier+"): " + maxProposal);
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
				query = "SELECT max(messageid) as maxmid FROM extractedrelations_clausie where "+proposalIdentifier+"= "+proposal+";";
			}
			else if (prp.getScriptPurpose().toLowerCase().equals("processmining")) {	System.out.println("Get MID from restart = processmining! ");
				query = "SELECT max(messageid) as maxmid FROM results where "+proposalIdentifier+"= "+proposal+";";
			}
			else if (prp.getScriptPurpose().toLowerCase().equals("reading")) {	System.out.println("Get MID from restart = reading! ");
				query = "SELECT max(messageid) as maxmid FROM allmessages where "+proposalIdentifier+"= "+proposal+";";
			} //reading??
			else if (prp.getScriptPurpose().toLowerCase().equals("onlysentenceextractionfortopicmodelling")) {	System.out.println("Get MID from restart = reading! ");
				query = "SELECT max(messageid) as maxmid FROM allsentences where proposal = "+proposal+";";
			}
			else if (prp.getScriptPurpose().toLowerCase().equals("reasonextraction")) {	//feb 2019, not in prop file, just for restart in reason extraction
				query= "SELECT max(messageid) as maxmid FROM autoextractedreasoncandidatemessages where proposal = "+proposal+";";
			}
			//april 2019
			else if (prp.getScriptPurpose().toLowerCase().equals("currentandnearbysentenceretrievalforterms")) { //feb 2019, not in prop file, just for restart in reason extraction
				//query= "SELECT max(proposal) as max FROM autoextractedreasoncandidatemessages;";
				return 0;
			}
			else {
				System.out.println("Cannot get ScriptPurpose for retrieving max pep ..exiting 2"); System.exit(0);
			}
			Statement st = conn.createStatement();			ResultSet rs = st.executeQuery(query);
			if (rs.next())  {
				maxProcessedMessageIDForProposal = rs.getInt("maxmid"); System.out.println("Max MID from restart = " + maxProcessedMessageIDForProposal);
			}
			//System.out.println("\n\nTotal number of peps in "+ pepTypeString + " is " + counter + " list size " + peps.size());
			st.close();
		}	catch (Exception e)	{
			System.err.println("Got an exception 454! ");			System.err.println(e.getMessage());
		}		
		return maxProcessedMessageIDForProposal;
	}
	///jan 2019..get folder
	public static String getMaxProcessedMailingListForProposalFromStorageTable(Connection conn, Integer messageid, ProcessingRequiredParameters prp){
		String folder="";	//default value	
		try	{
			String query= "";	
			
			if (prp.getScriptPurpose().toLowerCase().equals("reading")) {	System.out.println("Get MID from restart = reading!, MID = "+ messageid);
				query = "SELECT folder FROM allmessages where messageid = "+messageid+" ;";
			}
			else { 
				System.out.println("Cannot get ScriptPurpose for retrieving max pep ..exiting 3"); 
				System.exit(0);
			}
			
			Statement st = conn.createStatement();			ResultSet rs = st.executeQuery(query);
			if (rs.next())  {
				folder = rs.getString("folder"); 
				System.out.println("\n Folder for maxmessageid (" + messageid+") = "+ folder);
			}else {
				System.out.println("Folder for MID ("+messageid+") not found! ");
			}
			Integer totalMessagesToProcessForProposal = prp.mc.returnRSCount(query, conn);
			if (totalMessagesToProcessForProposal>1) {  //just to make sure we are not reassigning the same messageid in different runs
				System.out.println("\nMessageID: " + messageid + " assigned to multiple Proposals, please check: " + totalMessagesToProcessForProposal);
				System.exit(0);
			}
			//System.out.println("\n\nTotal number of peps in "+ pepTypeString + " is " + counter + " list size " + peps.size());
			st.close();
		}	catch (Exception e)	{
			System.err.println("Got an exception! ");			System.err.println(e.getMessage());
		}		
		return folder;
	}
	//while matching titles, to get restart value
	//looks like this is used when processing is stuck
	public static Integer getMaxProcessedMessageIDFromDebugTable(Connection conn, Integer proposal,ProcessingRequiredParameters prp){
		Integer maxProcessedMessageIDForProposal=0;	//default value	
		
		try	{
			String query= "SELECT max(messageid) as maxmid FROM proposaldetails_titlesforeachpep_debug;";
			Statement st = conn.createStatement();			ResultSet rs = st.executeQuery(query);
			if (rs.next())  {
				maxProcessedMessageIDForProposal = rs.getInt("maxmid"); 
				System.out.println("Max MID from restart = " + maxProcessedMessageIDForProposal);
			}
			//System.out.println("\n\nTotal number of peps in "+ pepTypeString + " is " + counter + " list size " + peps.size());
			st.close();
		}	catch (Exception e)	{
			System.err.println("Got an exception 349! ");			System.err.println(e.getMessage());
		}		
		if(maxProcessedMessageIDForProposal==null)
			maxProcessedMessageIDForProposal=0;
		return maxProcessedMessageIDForProposal;
	}
	//end while matcing titles
	
	public static String getMaxProcessedFileForProposalFromStorageTable(Connection conn, Integer messageid, ProcessingRequiredParameters prp){
		String file="";	//default value	
		try	{
			String query= "";	
			
			if (prp.getScriptPurpose().toLowerCase().equals("reading")) {	System.out.println("Get MID from restart = reading!, MID = "+ messageid);
				query = "SELECT file FROM allmessages where messageid = "+messageid+" ;";
			}
			else { 
				System.out.println("Cannot get ScriptPurpose for retrieving max pep ..exiting 4"); 
				System.exit(0);
			}
			
			Statement st = conn.createStatement();			ResultSet rs = st.executeQuery(query);
			if (rs.next())  {
				file = rs.getString("file"); 
				System.out.println("\n File for maxmessageid (" + messageid+") = "+ file);
			}else {
				System.out.println("File for MID ("+messageid+") not found! ");
			}
			Integer totalMessagesToProcessForProposal = prp.mc.returnRSCount(query, conn);
			if (totalMessagesToProcessForProposal>1) {  //just to make sure we are not reassigning the same messageid in different runs
				System.out.println("\nMessageID: " + messageid + " assigned to multiple Proposals, please check: " + totalMessagesToProcessForProposal);
				System.exit(0);
			}
			//System.out.println("\n\nTotal number of peps in "+ pepTypeString + " is " + counter + " list size " + peps.size());
			st.close();
		}	catch (Exception e)	{
			System.err.println("Got an exception! ");			System.err.println(e.getMessage());
		}		
		return file;
	}
	
	public static String getProposalTitleForProposal(Integer proposal,String proposalIdentifier, String v_proposalTableName, Connection conn){
		String pepTitle ="";	
		try	{
			String query= "";
			if (proposal<0)
				System.out.println("Error no pep as " + proposal);
			else	    	  
				query = "SELECT title FROM "+v_proposalTableName+" where "+proposalIdentifier+" = " + proposal + ";";

			Statement st = conn.createStatement();			ResultSet rs = st.executeQuery(query);
			if (rs.next())  
				pepTitle = rs.getString("title").toLowerCase().trim();			
			else { 
				//System.out.println("No title found for proposal: "+ proposal);	
			}
			//System.out.println("\n\nTotal number of peps in "+ pepTypeString + " is " + counter + " list size " + peps.size());
			st.close();
		}	catch (Exception e)	{
			System.err.println("Got an exception here 674, getProposalTitleForProposal() function ! proposalTableName: "+v_proposalTableName + " proposalIdentifier: "+proposalIdentifier );				
			System.out.println(StackTraceToString(e) );	 System.err.println(e.getMessage());
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
