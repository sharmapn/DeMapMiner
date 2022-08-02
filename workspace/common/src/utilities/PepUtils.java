package utilities;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import connections.MysqlConnect;

public class PepUtils {
	//static ProcessingRequiredParameters prp = new ProcessingRequiredParameters();
	//static String proposalIdentifier = "jep";
	public static ArrayList returnUniqueProposalsInDatabase(String proposalIdentifier){
		   //Utilities u = new Utilities();
		   MysqlConnect mc = new MysqlConnect();		   Connection connection = mc.connect();   
		   //mc.testConnection();       
	       Integer counter =0,pepNumber;	       
	       ArrayList<Integer> uniquePeps = new ArrayList<>();
		   //numbers.add(308);		            
           String sql = "SELECT distinct("+proposalIdentifier+") from "+proposalIdentifier+"details;"; 
	       try {
	    	   Statement stmt = connection.createStatement(); 	    	   ResultSet rs = stmt.executeQuery( sql );
	           	 while (rs.next()){     //check every message       
	           		 pepNumber = rs.getInt(1);	           		 uniquePeps.add(pepNumber);
	           	 }
	           	 System.out.println("Unique Proposal Returned = " + uniquePeps.size());
	           	stmt.close();
	       }
	       catch (SQLException e){	           System.out.println( e.getMessage() );	       }	       
		return uniquePeps;
	 } 
	// jan 2019..anothert one for jeps
	/*public static ArrayList returnUniqueJEPsInDatabase(String proposalIdentifier){
		   //Utilities u = new Utilities();
		   MysqlConnect mc = new MysqlConnect();		   Connection connection = mc.connect();   
		   //mc.testConnection();       
	       Integer counter =0,pepNumber;	       
	       ArrayList<Integer> uniquePeps = new ArrayList<>();
		   //numbers.add(308);		            
        String sql = "SELECT distinct("+proposalIdentifier+") from jepdetails;"; 
	       try {
	    	   Statement stmt = connection.createStatement(); 	    	   ResultSet rs = stmt.executeQuery( sql );
	           	 while (rs.next()){     //check every message       
	           		 pepNumber = rs.getInt(1);	           		 uniquePeps.add(pepNumber);
	           	 }
	           	 System.out.println("Unique Proposal Returned = " + uniquePeps.size());
	       }
	       catch (SQLException e){	           System.out.println( e.getMessage() );	       }	       
		return uniquePeps;
	 }  */
	//Jan 2019, return all mailing lists
	public static ArrayList returnUniqueJEPsMLInDatabase(){
		   //Utilities u = new Utilities();
		   MysqlConnect mc = new MysqlConnect();		   Connection connection = mc.connect();   
		   //mc.testConnection();       
	       Integer counter =0,pepNumber;		String folder="";       
	       ArrayList<String> uniqueJEPsML = new ArrayList<>();
		   //numbers.add(308);		            
	       String sql = "SELECT DISTINCT(folder) from allmessages;"; // String sql = "SELECT folder from distinctml;"; //-- create table distinctml as select distinct(folder) from allmessages;
	       try {
	    	   Statement stmt = connection.createStatement(); 	    	   ResultSet rs = stmt.executeQuery( sql );
	           	 while (rs.next()){     //check every message       
	           		 folder = rs.getString(1);	           		 uniqueJEPsML.add(folder);
	           	 }
	           	 System.out.println("Unique Proposal Returned = " + uniqueJEPsML.size());
	           	stmt.close();
	       }
	       catch (SQLException e){	           System.out.println( e.getMessage() );	       }	       
		return uniqueJEPsML;
	 } 
	
	public static ArrayList returnUniquePepsInDatabasePEPDetails(){
		   //Utilities u = new Utilities();
		   MysqlConnect mc = new MysqlConnect();
		 //mc.testConnection(); 
	       ArrayList<Integer> uniquePeps = new ArrayList<>();
		   //numbers.add(308);
		   
	       Integer pepNumber;	       
	       Connection connection = mc.connect();           
	       String sql = "SELECT distinct(pep) from pepdetails ;"; 
	       //can add messageid later
	       try {
	    	   Statement stmt = connection.createStatement();
	    	   ResultSet rs = stmt.executeQuery( sql );
	           	 while (rs.next()){     //check every message       
	           		 pepNumber = rs.getInt(1);
	           		 uniquePeps.add(pepNumber);
	           	 }
	           	stmt.close();
	           }
	       catch (SQLException e)	       {
	           System.out.println( e.getMessage() );
	       }	       
		return uniquePeps;
	   } 
	
	//same thing but for all different variants of proposals
	public static ArrayList returnUniqueProposalsInDatabaseProposalDetails(String proposalIdentifier){
		   //Utilities u = new Utilities();
		   MysqlConnect mc = new MysqlConnect();
		 //mc.testConnection(); 
	       ArrayList<Integer> uniquePeps = new ArrayList<>();
		   //numbers.add(308);
		   
	       Integer pepNumber;	       
	       Connection connection = mc.connect();           
	       String sql = "SELECT distinct("+proposalIdentifier+") from "+proposalIdentifier+"details ;"; 
	       //can add messageid later
	       try {
	    	   Statement stmt = connection.createStatement();
	    	   ResultSet rs = stmt.executeQuery( sql );
	           	 while (rs.next()){     //check every message       
	           		 pepNumber = rs.getInt(1);
	           		 uniquePeps.add(pepNumber);
	           	 }
	           	stmt.close();
	           }
	       catch (SQLException e)	       {
	           System.out.println( e.getMessage() );
	       }	       
		return uniquePeps;
	   } 
	                                                                                                                        
		public static int returnMessagePeps(int v_messageID){
			MysqlConnect mc = new MysqlConnect();
			//mc.testConnection();       
			Integer counter =0;
	
			String sql = "SELECT distinct (pep) from allmessages where messageID = " + v_messageID + " order by date2;"; 
			//can add messageid later
			Connection connection = mc.connect();
			Statement stmt;
			try {
				stmt = connection.createStatement();				ResultSet rs = stmt.executeQuery( sql );
				while (rs.next()){     //check every message       
					counter++;
				}
				stmt.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return counter;
	
		} 

	   public static Integer returnLastPEPInResultsTable(){
		   //Utilities u = new Utilities();
		   MysqlConnect mc = new MysqlConnect();
		   //mc.testConnection();   
		   
	       Integer pepNumber=null;
	       Connection connection = mc.connect();
           Statement stmt;
           String sql = "SELECT max(pep) from results;"; 
                  		   
	       try {
				stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery( sql );
		
	           	 if (rs.next()){     //check every message       
	           		 pepNumber = rs.getInt(1);
	           		 //uniquePeps.add(pepNumber);
	           	 }
	           	 else {
	           		 pepNumber=null;
	           	 }
	           	stmt.close();
	           }
	       catch (SQLException e){
	           System.out.println( e.getMessage() );
	       }
	       
		return pepNumber;
	   } 
	
}
