package peopleAspect;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;

public class ReturnDifferentCommunityMembers {
	
	public static ArrayList<String> getallCoreDevelopers(Connection conn){
		String type = "", pepTypeString =null;
		Boolean showOutput = false;
//		if (pepType == 0)
//			pepTypeString = "All";
//		else if (pepType == 1)
//			pepTypeString = "Standards Track";
//		else if (pepType == 2)
//			pepTypeString = "Informational";
//		else if (pepType == 3)
//			pepTypeString = "Process";
//		else
//			System.out.format("Incorrect pep type");
		
		//Integer[] peps = new Integer[500];
		ArrayList<String> coredevelopers = new ArrayList<String>();
		
		try
		{
			String query= "";
//			if (pepType==0)
//				query = "SELECT pep, type FROM pepdetails order by pep asc";
//			else	    	  
				query = "SELECT coredeveloper FROM coredevelopers order by coredeveloper asc";
			
			// create the java statement
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);
			Integer counter=0;
			
			//System.out.println("PEP Type " + pepTypeString);  
			while (rs.next())
			{	
				String coreDeveloper = rs.getString(1);				
				// print the results
				//System.out.format("%s, %s, %s, %s, %s, %s\n", id, firstName, lastName, dateCreated, isAdmin, numPoints);
				coredevelopers.add(coreDeveloper);
				if (showOutput){
					//System.out.println("\t"+pepNumber);
//					 System.out.print(pepNumber+ ((pepNumber%20==19) ? "\n" : " "));
				}
				counter++;
			}
			System.out.println("\n\nTotal number of coredevelopers " + coredevelopers.size());
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
		
		return coredevelopers;
	}	
	
	//may 2020 getallPEPEditorsWithDateAddedToPEPEditors 
	public static ArrayList<pepeditor> getallPEPEditorsWithDateAddedToPEPEditors(Connection conn){
		String type = "", query= "", pepTypeString =null;
		Boolean showOutput = false;
		ArrayList<pepeditor> pepeditors = new ArrayList<pepeditor>();
		try
		{   	  
				query = "SELECT pepeditor, dateadded FROM pepeditors order by pepeditor asc";
			
			// create the java statement
			Statement st = conn.createStatement();			ResultSet rs = st.executeQuery(query);
			Integer counter=0; 			String peped ="";  
			Date dateAdded=null;
			//System.out.println("PEP Type " + pepTypeString);  
			while (rs.next()){
				dateAdded=null;	
				peped = rs.getString(1);	 dateAdded = rs.getDate(2);			
				// print the results
				//System.out.format("%s, %s, %s, %s, %s, %s\n", id, firstName, lastName, dateCreated, isAdmin, numPoints);
				pepeditor pe = new pepeditor();
				pe.setPEPEditor(peped);  pe.setDateaddedtopepeditorlist(dateAdded);
				pepeditors.add(pe);
				counter++;
			}
			System.out.println("Total number of coredevelopers " + pepeditors.size());
			st.close();
		}	catch (Exception e)	{
			System.err.println("Got an exception! ");
			System.err.println(e.getMessage());
		}
		return pepeditors;
	}	
	
	
	//jan 2010
	public static ArrayList<coredev> getallCoreDevelopersWithDateAddedToCoreDevList(Connection conn){
		String type = "", pepTypeString =null;
		Boolean showOutput = false;
		ArrayList<coredev> coredevelopers = new ArrayList<coredev>();
		
		try
		{
			String query= "";
//			if (pepType==0)
//				query = "SELECT pep, type FROM pepdetails order by pep asc";
//			else	    	  
				query = "SELECT coredeveloper, dateadded FROM coredevelopers order by coredeveloper asc";
			
			// create the java statement
			Statement st = conn.createStatement();			ResultSet rs = st.executeQuery(query);
			Integer counter=0;
			String coreDeveloper ="";  
			Date dateAdded=null;
			//System.out.println("PEP Type " + pepTypeString);  
			while (rs.next()){
				dateAdded=null;	
				coreDeveloper = rs.getString(1);	 dateAdded = rs.getDate(2);			
				// print the results
				//System.out.format("%s, %s, %s, %s, %s, %s\n", id, firstName, lastName, dateCreated, isAdmin, numPoints);
				coredev cd = new coredev();
				cd.setCoredev(coreDeveloper);  cd.setDateaddedtocoredevlist(dateAdded);
				coredevelopers.add(cd);
				if (showOutput){
					//System.out.println("\t"+pepNumber);
//					 System.out.print(pepNumber+ ((pepNumber%20==19) ? "\n" : " "));
				}
				counter++;
			}
			System.out.println("Total number of coredevelopers " + coredevelopers.size());
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
		
		return coredevelopers;
	}	
	
	public static ArrayList<String> getallMembers(Connection conn){
		String type = "", pepTypeString =null;
		Boolean showOutput = false;
		ArrayList<String> developers = new ArrayList<String>();
		
		try
		{												//distinctauthorspepnumbers
			//String query="SELECT DISTINCT(senderName) FROM alldevelopers order by senderName asc";
			//above column was somehow modified by myself into cluster representation, so new column is 'clusterBySenderFullName'
			String query="SELECT DISTINCT(clusterBySenderFullName) FROM alldevelopers order by clusterBySenderFullName asc";
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
			System.out.println("Total number of distint develoopers " + developers.size());
			st.close();
		}	catch (Exception e)	{
			System.err.println("Got an exception! ");
			System.err.println(e.getMessage());
		}		
		return developers;
	}	
	
	//jan 2020...we cretaed a table to reduce computation timr doing it everytime
	public static int getallMessageForPEP(Connection conn, int pep){
		String type = "", pepTypeString =null;
		Boolean showOutput = false;
		ArrayList<String> developers = new ArrayList<String>();
		int count=0;
		try
		{												//distinctauthorspepnumbers
			//String query="SELECT DISTINCT(senderName) FROM alldevelopers order by senderName asc";
			//above column was somehow modified by myself into cluster representation, so new column is 'clusterBySenderFullName'
			String query="SELECT nummsgs FROM jan2020messgepostbyrole where pep = "+pep+";";
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);
			count=0;  
			if (rs.next())
			{	
				count = rs.getInt(1);	
				
					//System.out.println("\t"+pepNumber);
				
				if (showOutput){
					//System.out.println("\t"+pepNumber);
//					 System.out.print(pepNumber+ ((pepNumber%20==19) ? "\n" : " "));
				}
				//counter++;
			}
			//System.out.println("Total number of distint develoopers " + developers.size());
			st.close();
		}	catch (Exception e)	{
			System.err.println("Got an exception! ");
			System.err.println(e.getMessage());
		}		
		return count;
	}
	//getAllMembersPerRole_MessageForPEP(connection, pepNum);
	public static int getAllMembersPerRole_MessageForPEP(Connection conn, int pep){
		String type = "", pepTypeString =null;
		Boolean showOutput = false;
		ArrayList<String> developers = new ArrayList<String>();
		int count=0;
		try
		{												//distinctauthorspepnumbers
			//String query="SELECT DISTINCT(senderName) FROM alldevelopers order by senderName asc";
			//above column was somehow modified by myself into cluster representation, so new column is 'clusterBySenderFullName'
			String query="SELECT allPostsByThisAuthor, allPostsByThisAuthorInInformationalPEPs,"
					+ " allPostsByThisAuthorInProcessPEPs,allPostsByThisAuthorInStandardPEPs FROM distinctauthorpepnumbers where pep = "+pep+";";
			Statement st = conn.createStatement();			ResultSet rs = st.executeQuery(query);
			count=0;  
			if (rs.next())
			{	
				count = rs.getInt(1);	
				
					//System.out.println("\t"+pepNumber);
				
				if (showOutput){
					//System.out.println("\t"+pepNumber);
//					 System.out.print(pepNumber+ ((pepNumber%20==19) ? "\n" : " "));
				}
				//counter++;
			}
			//System.out.println("Total number of distint develoopers " + developers.size());
			st.close();
		}	catch (Exception e)	{
			System.err.println("Got an exception! ");
			System.err.println(e.getMessage());
		}		
		return count;
	}
}
