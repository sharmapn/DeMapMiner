package GeneralQueries;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import connections.MysqlConnect;
import miner.process.PythonSpecificMessageProcessing;

public class PEPAuthorsAndCount {
	
	public static void main(String args[])
	{
	
		MysqlConnect mc = new MysqlConnect();
		Connection connection = mc.connect();
		getallPEPAuthors(connection);
	}
	
	public static ArrayList<String> getallPEPAuthors(Connection conn){
		String type = "", pepTypeString =null;
		Boolean showOutput = true;
//		if (pepType == 0)
//			pepTypeString = "All";
//		else if (pepType == 1)
//			pepTypeString = "Standards Track";
//		else if (pepType == 2)WHERE messageid = messageid)
//			pepTypeString = "Informational";
//		else if (pepType == 3)
//			pepTypeString = "Process";
//		else
//			System.out.format("Incorrect pep type");
		
		//Integer[] peps = new Integer[500];
		ArrayList<String> PEPAuthors = new ArrayList<String>();
		
		try
		{
			String query= "";
//			if (pepType==0)
				query = "SELECT pep, author FROM pepdetails order by pep asc";
//			else	    	  
//				query = "select TRIM(author) as ath, COUNT(author) as cnt from pepdetails "
//						+ "where author IS NOT NULL group by ath order by ath asc";
			
			// create the java statement
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);
			Integer counter=0;
			
			//System.out.println("PEP Type " + pepTypeString);  
			while (rs.next())
			{	
				Integer pep = rs.getInt(1);
				String pepauthor = rs.getString(2);
					
				// print the results
				//System.out.format("%s, %s, %s, %s, %s, %s\n", id, firstName, lastName, dateCreated, isAdmin, numPoints);
				System.out.println(pep + " " +pepauthor);
				if(pepauthor.contains(","))
				{
					String[] authors = pepauthor.split(",");
					String combined ="";
					for (String author: authors){
						 author= getFullAuthorFromString(author);
						 System.out.println("\t "+ pep + " " +author);
						 combined =combined + author + ",";
						 updateauthor(pep,author,conn);
					}
					//combined = combined.replaceAll(", $", "");
					combined = combined.substring(0, combined.lastIndexOf(","));
					System.out.println("a: "  +combined);
					
					//System.out.println("b");
				}
				else {
					pepauthor= getFullAuthorFromString(pepauthor);
					//System.out.println("c");
					updateauthor(pep,pepauthor,conn);
					//System.out.println("d");
					 System.out.println("\t { "+ pep + " " +pepauthor);
					 
				}
				
				PEPAuthors.add(pepauthor);
				counter++;
		
			
			}
			System.out.println("\n\nTotal number of PEPAuthors " + PEPAuthors.size());
			st.close();
		}	
			catch (Exception e)	{
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
		
		return PEPAuthors;
	}	
	
	public static void updateauthor(Integer pep, String combined, Connection conn) throws SQLException{
		//System.out.println("to update");
		Statement sMainParent=conn.createStatement();
		int update;
		String sqlReflect = "insert into pepdetails_authorcount (author) values ('"+combined+"');" ;
 		//email similarity, modify to check as long as 'senderEmailFirstSegment' is as long as 2 characters long					
		update = sMainParent.executeUpdate(sqlReflect);
		//System.out.println("updated");
	}
	
	public static ArrayList<String> getallDevelopers(Connection conn){
		String type = "", pepTypeString =null;
		Boolean showOutput = false;
		ArrayList<String> developers = new ArrayList<String>();
		
		try
		{												//distinctauthorspepnumbers
			String query="SELECT DISTINCT(senderName) FROM alldevelopers order by senderName asc";
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);
			Integer counter=0;
  
			while (rs.next())
			{	
				String developer = rs.getString(1);	
				developers.add(developer);
				if (showOutput){
					//System.out.println("\t"+pepNumber);
//					 System.out.print(pepNumber+ ((pepNumber%20==19) ? "\n" : " "));
				}
				counter++;
			}
			System.out.println("\n\nTotal number of develoopers " + developers.size());
			st.close();
		}	catch (Exception e)	{
			System.err.println("Got an exception! ");
			System.err.println(e.getMessage());
		}		
		return developers;
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
	

}
