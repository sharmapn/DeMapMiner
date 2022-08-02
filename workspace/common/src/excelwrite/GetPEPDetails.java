package excelwrite;

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

public class GetPEPDetails {
	
	static Map peps = new HashMap();
	//authors arraylist
	ArrayList authors = new ArrayList();	
	static MysqlConnect mc = new MysqlConnect();	 
	
	public static void main(String[] args) throws IOException {
		Connection conn = mc.connect();
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
	        url = new URL("https://github.com/python/peps");
	        is = url.openStream();  // throws an IOException
	        br = new BufferedReader(new InputStreamReader(is));
	       // File f = new File("c:\\scripts\\output\\a.txt");

	        while ((line = br.readLine()) != null) {
	            if(line.contains("href=")){
	               // System.out.println(line.trim());
	                String[] parts = line.split("<");
	                for(String p: parts){
	                	if(p.contains("href")){	                		
	                		int start = p.indexOf('"');
	                		int end = p.indexOf('"', start+1);
	                		String link = p.substring(start+1, end);
	                		link = "https://github.com" + link; 			//https://github.com/python/peps/blob/master/pep-0001.txt
	                		if (link.endsWith(".txt")&& !link.endsWith("README.txt")){	                				                			
	                			String pepno = link.substring(link.length() - 12);		//pep-3140.txt	                			
	                			pepno = pepno.replace(".txt","");
	                			pepno = pepno.replace("pep-","");
	                			//System.out.println(pepno); 
	                			int pepNumber = Integer.parseInt(pepno);
	                			//506, 514, 
	                			
	                			//choose what you want to do here
	                			//some links have problems
	                			if (pepNumber !=519 && pepNumber !=527 && pepNumber !=528 && pepNumber !=526 && pepNumber !=529) {
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
        String inputLine;        
        String pep = null, title = null, author = null, type = null, bdfl_delegate = null, pythonVersion = null;
        String created = null;
        
        while ((inputLine = in.readLine()) != null){      	
        	if (inputLine.contains(">PEP:")){
        		int start = inputLine.indexOf(">PEP:");
        		int end = inputLine.indexOf("</td>", start+5);
        		pep = inputLine.substring(start+5, end);
        		pep = pep.trim();
        		//System.out.println(pep);
            	
            }if (inputLine.contains(">Title:")){
            	int start = inputLine.indexOf(">Title:");
        		int end = inputLine.indexOf("</td>", start+7);
        		title = inputLine.substring(start+7, end);
        		//System.out.println(title);
            }
            if (inputLine.contains(">Author:")){
            	int start = inputLine.indexOf(">Author:");
        		int end = inputLine.indexOf("</td>", start+8);
        		author = inputLine.substring(start+8, end);
        		//System.out.println(title);
            }  
            if (inputLine.contains(">Type:")){
            	int start = inputLine.indexOf(">Type:");
        		int end = inputLine.indexOf("</td>", start+6);
        		type = inputLine.substring(start+6, end);
        		//System.out.println(title);
            } 
            if (inputLine.contains(">BDFL-Delegate:")){
            	int start = inputLine.indexOf(">BDFL-Delegate:");
        		int end = inputLine.indexOf("</td>", start+15);
        		bdfl_delegate = inputLine.substring(start+15, end);
        		//System.out.println(title);
            } 
            if (inputLine.contains(">Python-Version:")){
            	int start = inputLine.indexOf(">Python-Version:");
        		int end = inputLine.indexOf("</td>", start+16);
        		pythonVersion = inputLine.substring(start+16, end);
        		//System.out.println(title);
            } 
            
            if (inputLine.contains(">Created:")){
            	int start = inputLine.indexOf(">Created:");
        		int end = inputLine.indexOf("</td>", start+9);
        		created = inputLine.substring(start+9, end).trim();
        		//System.out.println(title);
            } 
            
            
        } 
 //       if(pep!=null && title!=null){
 //       	int pepNumber = Integer.parseInt(pep);
//        	peps.put(pep, title);
        	System.out.println(pep+" "+title+" "+author+" "+type+ " "+ bdfl_delegate + " " + pythonVersion + " " + created);
        	///bw.write(pep+","+title);
    		//bw.newLine();
        	insertinDB(pep,title,author, type, bdfl_delegate,pythonVersion,created, conn);
  //      }
        in.close();		
	}
	
	public static Date returnDate(String candidate){
		List<SimpleDateFormat> knownPatterns = new ArrayList<SimpleDateFormat>();
		knownPatterns.add(new SimpleDateFormat("dd-MMM-yyyy"));
		knownPatterns.add(new SimpleDateFormat("yyyy-MM-dd"));
		knownPatterns.add(new SimpleDateFormat("dd MMM yyyy"));
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
	
	public static void insertinDB(String pep,String title,String author, String type, String bdfl_delegate, String pythonVersion, String created, Connection conn)
	  {
		try {
			// get java date from string
			//String startDateString = "06-27/2007";
			java.util.Date createdDate =null;
			java.sql.Date sqlDate = null;
			if (created==null){
				sqlDate = null;
			}
			else{
				DateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
				
				//System.out.println("a");
				createdDate = (java.util.Date) df.parse(created);
				//System.out.println("b");
				//convert java util date to java.sql date
				java.util.Calendar cal = Calendar.getInstance();
				cal.setTime(createdDate);  //v_dateC
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 0);
				sqlDate = new java.sql.Date(cal.getTime().getTime()); // your sql date
				//System.out.println("c");
			}
			// the mysql insert statement
			String query = " insert into pepdetails (pep, title, author,type,bdfl_delegate,created,createdYear, python_version)"
					+ " values (?, ?, ?,?,?,?,?,?)";

			// create the mysql insert preparedstatement
			PreparedStatement preparedStmt = conn.prepareStatement(query);
			preparedStmt.setInt(1, Integer.parseInt(pep));
			preparedStmt.setString(2, title);
			preparedStmt.setString(3, author);
			preparedStmt.setString(4, type);
			preparedStmt.setString(5, bdfl_delegate);
			preparedStmt.setDate(6, sqlDate);
				Calendar cal2 = Calendar.getInstance();
				cal2.setTime(createdDate);
				int createdYear = cal2.get(Calendar.YEAR);			
			preparedStmt.setInt(7, createdYear);
			preparedStmt.setString(8, pythonVersion);
			// preparedStmt.setBoolean(4, false);
			// preparedStmt.setInt (5, 5000);
			// execute the preparedstatement
			preparedStmt.execute();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (Exception e) {
			System.err.println("Got an exception!");
			System.err.println(e.getMessage());
		}
	  }
	
	public static String getPepAuthor(Integer v_pepNumber){
		String author = "";
		try
		{
			Connection conn = mc.connect();
			String query = "SELECT pep, author FROM pepdetails where pep = " + v_pepNumber;
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);
			
			// iterate through the java resultset
			if (rs.next())   {
				int pepNumber = rs.getInt("pep");
				author = rs.getString("author");
				System.out.format("%s, %s\n", pepNumber, author);
			}
			st.close();
		}  catch (Exception e)	    {
			System.err.println("Got an exception! ");
			System.err.println(e.getMessage());
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
	
	

	public static ArrayList<Integer> getAllPeps(){
		ArrayList<Integer> list =new ArrayList<Integer>();
		try
		{
			Connection conn = mc.connect();
			String query = "SELECT distinct pep FROM pepdetails ";
			Statement st = conn.createStatement();			ResultSet rs = st.executeQuery(query);
			Integer pepNumber;
			// iterate through the java resultset
			if (rs.next())   {
				pepNumber = rs.getInt("pep");			//	author = rs.getString("author");
				//System.out.format("%s, %s\n", pepNumber, author);
				list.add(pepNumber);
			}
			st.close();
		}  catch (Exception e)	    {
			System.err.println("Got an exception! ");
			System.err.println(e.getMessage());
		}
		
		//if author has comma, meaning multiple authors, then insert in authors array
		//String authorFiltered = "";		
		// if it has brackets
		//paul@prescod.net (Paul Prescod)
		///if (author.contains("(") && author.contains(")")) {
		//	authorFiltered = author.substring(author.indexOf("(")+1,author.indexOf(")"));
		//	author = authorFiltered;
		//}
		
		return list;
	}
	
	
	//for all pep authors, get the pep author (can be multiple) and remove unecessary charaters and email address
	public static String correctPepAuthor(){
		String author = "";
		try
		{
			Connection conn = mc.connect();
			String query = "SELECT pep, author FROM pepdetails;";
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);
			
			// iterate through the java resultset
			while(rs.next())   {
				int pepNumber = rs.getInt("pep");
				author = rs.getString("author");
				System.out.format("%s, %s\n", pepNumber, author);
			}
			st.close();
		}  catch (Exception e)	    {
			System.err.println("Got an exception! ");
			System.err.println(e.getMessage());
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
			String query = "SELECT pep, bdfl_delegate FROM pepdetails where pep = " + v_pepNumber;
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);
			if (rs.next())    {
				int pepNumber = rs.getInt("pep");
				delegate = rs.getString("bdfl_delegate");
				System.out.format("%s, %s\n", pepNumber, delegate);
			}
			st.close();
		}   catch (Exception e)	    {
			System.err.println("Got an exception! ");
			System.err.println(e.getMessage());
		}
		
		return delegate;
	}
	
	static void readFileGetPEPTitle(String v_link, BufferedWriter bw) throws IOException{
		URL peplink = new URL(v_link);
		BufferedReader in = new BufferedReader(new InputStreamReader(peplink.openStream()));
		String inputLine, pep = null, title = null;
		
		while ((inputLine = in.readLine()) != null){      	
			if (inputLine.contains(">PEP:")){
				int start = inputLine.indexOf(">PEP:");
				int end = inputLine.indexOf("</td>", start+5);
				pep = inputLine.substring(start+5, end);
				pep = pep.trim();
				//System.out.println(pep);
				
			}if (inputLine.contains(">Title:")){
				int start = inputLine.indexOf(">Title:");
				int end = inputLine.indexOf("</td>", start+7);
				title = inputLine.substring(start+7, end);
				//System.out.println(title);
			}             
			
		} 
		//       if(pep!=null && title!=null){
		//       	int pepNumber = Integer.parseInt(pep);
		//        	peps.put(pep, title);
		System.out.println(pep+" "+title);
		bw.write(pep+","+title);
		bw.newLine();
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
        		int start = inputLine.indexOf(">PEP:");
        		int end = inputLine.indexOf("</td>", start+5);
        		pep = inputLine.substring(start+5, end);
        		pep = pep.trim();
        		//System.out.println(pep);
            	
            }if (inputLine.contains(">Author:")){
            	int start = inputLine.indexOf(">Author:");
        		int end = inputLine.indexOf("</td>", start+7);
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
		
		try
		{
			String query= "";
			if (pepType==0)
				query = "SELECT pep, type FROM pepdetails order by pep asc";
			else	    	  
				query = "SELECT pep, type FROM pepdetails where type like '%" + pepTypeString + "%' order by pep asc";
			
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
	
	public static String getPepTypeForPep(Integer pep,Connection conn){
		String pepTypeString =null;		
		//Integer[] peps = new Integer[500];
		ArrayList<Integer> peps = new ArrayList<Integer>();		
		try	{
			String query= "";
			if (pep<0)
				System.out.println("Error no pep as " + pep);
			else	    	  
				query = "SELECT type FROM pepdetails where pep = " + pep + ";";
			Statement st = conn.createStatement();			ResultSet rs = st.executeQuery(query);				 
			if (rs.next())  {
				pepTypeString = rs.getString("type");	
			}
			//System.out.println("\n\nTotal number of peps in "+ pepTypeString + " is " + counter + " list size " + peps.size());
			st.close();
		}	catch (Exception e)	{
			System.err.println("Got an exception! ");			System.err.println(e.getMessage());
		}		
		if (pepTypeString == null) {pepTypeString="";} 
		pepTypeString = pepTypeString.trim();
		return pepTypeString;
	}
	
	public static String getPepTitleForPep(Integer pep,Connection conn){
		String pepTypeString =null;		
		//Integer[] peps = new Integer[500];
		//ArrayList<Integer> peps = new ArrayList<Integer>();
		
		try	{
			String query= "";
			if (pep<0)
				System.out.println("Error no pep as " + pep);
			else	    	  
				query = "SELECT title FROM pepdetails where pep = " + pep + ";";

			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);			
			
			if (rs.next())  {
				pepTypeString = rs.getString("title");
			}
			
			//System.out.println("\n\nTotal number of peps in "+ pepTypeString + " is " + counter + " list size " + peps.size());
			st.close();
		}	catch (Exception e)	{
			System.err.println("Got an exception! ");
			System.err.println(e.getMessage());
		}
		
		return pepTypeString;
	}
	
	
}
