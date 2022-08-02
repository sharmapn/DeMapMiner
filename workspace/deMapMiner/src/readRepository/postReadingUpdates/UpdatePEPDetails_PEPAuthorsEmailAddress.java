package readRepository.postReadingUpdates;

import java.sql.Connection;

import connections.MysqlConnect;
import miner.process.PythonSpecificMessageProcessing;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import connections.MysqlConnect;
//may and aug 2018 now not needed ...integrated in GetProposalDetailsWebPage.java and GetProposalDetails.java

//This script populates the author email address in pep states table
//then we can use java to match -- match author and senderemail in allmessages with authorCorrected, authorEmail in pepdetails 

//How its done
// get all messages for a pep by the pep author
	//get the email from the author column
// sometimes pep authors change during the course of discussion - maybe we can get this information from pepstates table 
public class UpdatePEPDetails_PEPAuthorsEmailAddress {
	static String permanentAuthor="", permanentAuthorEmail="";
	static PythonSpecificMessageProcessing psmp = new PythonSpecificMessageProcessing();
	public static void main(String args[])
	{	
		MysqlConnect mc = new MysqlConnect();		Connection connection = mc.connect();
		for(int i=0; i<10000;i++) {
			getallPEPAuthors(connection, i);
		}
	}
	
	public static ArrayList<String> getallPEPAuthors(Connection conn, Integer proposal){
		String type = "", pepTypeString =null;
		Boolean showOutput = true;
		ArrayList<String> PEPAuthors = new ArrayList<String>();		
		try
		{
			String query= "";	Integer counter=0;
//			if (pepType==0)
				query = "SELECT pep, author FROM pepdetails  where pep = "+proposal+" order by pep asc";
//			else	    	  
//				query = "select TRIM(author) as ath, COUNT(author) as cnt from pepdetails "
//						+ "where author IS NOT NULL group by ath order by ath asc";
			Statement st = conn.createStatement();			ResultSet rs = st.executeQuery(query);
			//System.out.println("PEP Type " + pepTypeString);
			String combined ="";
			while (rs.next()){	
				Integer pep = rs.getInt(1);				String pepauthorLine = rs.getString(2);
				if(pepauthorLine.contains(","))	{
					String[] authorsLine = pepauthorLine.split(",");
					combined =""; permanentAuthor=""; permanentAuthorEmail="";
					for (String authorLine: authorsLine){
						 // permanentAuthor and permanentAuthorEmail
						 permanentAuthor=psmp.extractFullAuthorFromString(authorLine);
						 permanentAuthorEmail=psmp.returnAuthorEmail(pepauthorLine);
						 //System.out.println("\t Processed "+ pep + " " +permanentAuthor + "," + permanentAuthorEmail);
						 System.out.format("%7d, %120s, %40s, %30s", pep, pepauthorLine, permanentAuthor, permanentAuthorEmail);
						 System.out.println();
						 combined =combined + authorLine + ",";
//						 updateauthor(pep,author,conn);
					}
					//combined = combined.replaceAll(", $", "");
					combined = combined.substring(0, combined.lastIndexOf(","));
				}
				else {
					 permanentAuthor=psmp.extractFullAuthorFromString(pepauthorLine);
					 permanentAuthorEmail=psmp.returnAuthorEmail(pepauthorLine);
					 System.out.format("%7d, %120s, %40s, %30s", pep, pepauthorLine, permanentAuthor, permanentAuthorEmail);
					 System.out.println();
					 //System.out.println(pep + pepauthorLine + "\t Processed "+ " " +permanentAuthor + "," + permanentAuthorEmail);
				}
				PEPAuthors.add(permanentAuthor);
				counter++;
				//for each author update the email in the pepstates table
				updateAuthorEmail(pep, permanentAuthorEmail, conn);
			}
			//System.out.println("\n\nTotal number of PEPAuthors " + PEPAuthors.size());
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
	
	public static void updateAuthorEmail(Integer pep, String authorEmail, Connection conn) throws SQLException{
		//System.out.println("to update");
		Statement sMainParent=conn.createStatement();
		int update;
		String sqlReflect = "update pepdetails SET authorEmail = '"+ authorEmail +"' where pep = "+pep+";" ;
 		//email similarity, modify to check as long as 'senderEmailFirstSegment' is as long as 2 characters long					
		update = sMainParent.executeUpdate(sqlReflect);
		//System.out.println("updated");
	}
		
	

	private static String removeDoubleSpaces(String author) {
		if(author.contains("  "))
			author = author.replaceAll("  ", " ");
		author =author.trim();
		return author;
	}

}
