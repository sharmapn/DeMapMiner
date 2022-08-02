package readRepository.postReadingUpdates;

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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import connections.MysqlConnect;
import miner.process.PythonSpecificMessageProcessing;
import connections.PropertiesFile;
import utilities.GetDate;

public class UpdatePEPDetails_DownloadInsertProposalSummary {	
	static Map peps = new HashMap();	
	ArrayList authors = new ArrayList();//authors arraylist	
	static MysqlConnect mc = new MysqlConnect();	static Connection conn;
	static PythonSpecificMessageProcessing pms = new PythonSpecificMessageProcessing();
	static GetDate gd = new GetDate();  //new date parser centrally located in seprate package for use from all other package code
	//The following will miss PEP 0, and we have to insert it manually
    //insert into pepdetails (pep,author,type,title) 
	//values (0,'David Goodger <goodger at python.org>, Barry Warsaw <barry at python.org>','Informational','Index of Python Enhancement Proposals (PEPs)')
		
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
	    String line, baseURL = "https://www.python.org/dev/peps/", proposalURL = "/dev/peps/pep-";
	    
	    //write final data
	    File fout = new File("c:\\scripts\\PEPNumberTitleNEW.txt");
		FileOutputStream fos = new FileOutputStream(fout);	 
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

	    try {
	        url = new URL(baseURL);
	        is = url.openStream();  // throws an IOException
	        br = new BufferedReader(new InputStreamReader(is));
	       // File f = new File("c:\\scripts\\output\\a.txt");

	        ArrayList<Integer> peps = new ArrayList<Integer>();
	        
	        while ((line = br.readLine()) != null) {	 //System.out.println(line.trim());
	            if(line.contains(proposalURL)){			 //get links for all other proposals (which have above url as part of url) https://www.python.org/dev/peps/pep-0001 
	                System.out.println("\t" + line.trim());	            	
	                String splitter = "href";
	                if(!line.contains(splitter)) { continue; }
	                String[] parts = line.split("href");//"<"
	                for(String p: parts){ //System.out.println("part: "+ p);
	                	if(p.contains(proposalURL)){	
	                		System.out.println("\t\tPART: "+ p.trim());
	                		int start = p.indexOf('"');
	                		int end = p.indexOf('"', start+1);
	                		String link = p.substring(start+1, end);
	                		link = "https://www.python.org" + link; 			//https://www.python.org
	                		System.out.println("\t\tLINK TO CHECK: "+ link.trim());
	                		
	                		String pepno = link.substring(link.length() - 4);		//pep-3140.txt	                			
                			pepno = pepno.replace(".txt","");
                			pepno = pepno.replace("pep-","");
                			
                			int pepNumber = Integer.parseInt(pepno);	System.out.println("\t\tProposal Number: "+pepNumber); 
                			
            				if(peps.contains(pepNumber)) {}
            				else {
            					//if(pepNumber==391)                			
            					downLoadWebPage(link,pepNumber);//downLoadWebPage with link, pepNumber
            					//you can add more code to read ebery detail of the pep here rather than the previous script
            					peps.add(pepNumber);
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
	}
	
	static void downLoadWebPage(String link, Integer pepNumber) {
		Document document;
		String webPage="";
	    try {
	        document = Jsoup.connect(link).get();
	        Elements paragraphs = document.select("p");	        Element firstParagraph = paragraphs.first();	        Element lastParagraph = paragraphs.last();
	        Element p;
	        int i=1;
	        p=firstParagraph;
	       // System.out.println("*  " +p.text());
	        int paragraphCounter=0;
	        while (p!=lastParagraph){
	            p=paragraphs.get(i);
	            webPage=webPage + p.text() + "\n\n";
	            i++;
	            paragraphCounter++;
	        }
	        //System.out.println(webPage);
	        String query = " update pepdetails set pepSummary = ?, pepurl= ? where pep = "+pepNumber+";";

			// create the mysql insert preparedstatement
			PreparedStatement preparedStmt = conn.prepareStatement(query);
			preparedStmt.setString(1, webPage);		preparedStmt.setString(2, link);				
			preparedStmt.execute();
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
	
	
	
	
}
