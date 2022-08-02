package peopleAspect;


import java.sql.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.regex.Pattern;

import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



import javax.swing.*;
import javax.swing.table.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

//import org.joda.time.DateTime;
//import org.joda.time.format.DateTimeFormat;

import connections.MysqlConnect;
//import PythonSpecificMessageProcessing;
import utilities.PepUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.joestelmach.natty.*;

//import readRepository.readMetadataFromWeb.GetProposalDetailsWebPage;

//This script is for Email Analysis of developers

//Jan 2021..now we want to investigate the roles asscoated with these contributions
// and we also want to investigate the 466 PEPs till BDFL, not till 2017 only
// thus we use new tables with suffix of '2021'
// we also used new columns, namely pepnum2020 and peptype2020 as they are more records to assigned to these columns


public class InfluenceOfRolesEmailContributions2021 extends JFrame
{	
	static GetProposalDetailsWebPage pd = new GetProposalDetailsWebPage();
	static ArrayList<Integer> allPepPerTypes;
	//input db tables
	static String emailDataTable = "allmessages"; //allpeps in previous code fo ease 2017 paper
	static String stateDataTable = "pepstates_danieldata_datetimestamp"; //allpeps in previous code fo ease 2017 paper
	//output db tables
	//rq1.1
	//static String firstDBoutputtable = "pepcountsbysubphases"; 
	//jan 2021...new tablenames
	static String firstDBoutputtable = "pepcountsbysubphases_2021"; 
	
	//march 2020..we only consider peps discussed before '2017-03-10'
	//Jan 2021, we have to use all PEPs which were created before 12 July 2018...bdfl resignation date
	//but add discussions of these if they occurred afterwards
	static String pepcreatedbeforedate = "'2017-03-10'";
	
	static String mailingLists[] = {"python-dev","python-ideas","python-list","python-checkins","python-committers","python-patches"};
	
	static PythonSpecificMessageProcessing pms = new PythonSpecificMessageProcessing();
	static SeparateStudyGroupFunctions f = new SeparateStudyGroupFunctions();
	
	static Boolean detailedData = true, overallDataOnly = true;
	//note overall write to different table and detailed write to different
	//rq1.1
	//static String detailedDataTablename = "pepcountsRQ2bysubphases_detailed", overallDataOnlyTablename = "pepcountsRQ2bysubphases_overall"; 
	//jan 2021...new tablenames
	static String detailedDataTablename = "pepcountsRQ2bysubphases_detailed_2021", overallDataOnlyTablename = "pepcountsRQ2bysubphases_overall_2021"; 
	
	
	public static void main(String[] args) {		
		ArrayList<Double> vlist = new ArrayList<>();	
		MysqlConnect mc = new MysqlConnect();
		Connection connection = mc.connect();
		
		//0 = all peps in database, 
		//1 = Standard about python language, functional enhancements - more action here, eg Pep 308 is standard pep
		//2 = Process
		//3 = Informational
		
		f.totalNumberOfMessages(connection);
		f.totalNumberOfUniqueMessages(connection);
		System.out.println("here a ");
		//Research GROUP 1		
		//finalised
		
		//march 2020..we only consider peps discussed before '2017-03-10'
		//String pepcreatedbeforedate = "'2017-03-10'";
		
//		researchQuestionOne(0,connection, pepcreatedbeforedate);   //Total Number of Messages Per PEP
		researchQuestionTwo(0,connection);   //Get number of messages between all pairwise states for a pep

		
		
		//researchQuestionThree(0,connection); //Get discussions per python version
		
		//not finalised
//		researchQuestionFour(0,connection);		
		//RQ4(0,connection);
		
		//Research Group 2
		//getDateTime(connection);
		
		//getAuthor(connection);
		ArrayList<Author> authorList = new ArrayList<Author>();
//		settleDistinctAuthors(authorList,connection);
//		processAuthorList(authorList);	
		mc.disconnect();
	}
	
	
	private static void researchQuestionOne(Integer pepType, Connection connection, String pepcreatedbeforedate) {	
		String pepTypeString = f.assignPEPTypes(pepType);
		Integer pepNumber, numberMessagesPerPEP=0, totalNumberMessages = 0;
		boolean showOutput=false;		
		ArrayList<Integer> pepsPerType = new ArrayList<Integer>();
		String folder = "";
		
		try{
			Statement stmt = connection.createStatement();			
			pepsPerType = pd.getallPepsForPepTypeb4(pepType, showOutput, connection,pepcreatedbeforedate);	//this we get from pep details table				
						
			System.out.println("PEP , numberMessagesPerPEP, pepType ");
			for (int j : pepsPerType) { 
				pepNumber = j; //peps.get(j-1); //start from 0-
				//if (pepNumber!=435)
					
					String thePEPType =pd.getPepTypeForPep(pepNumber, connection);
					for (ResultSet rs2 = connection.createStatement().executeQuery(("SELECT count(*), folder from " + emailDataTable + " WHERE pep = "
									+ pepNumber + " AND date2 <= "+pepcreatedbeforedate+" GROUP BY folder;")); rs2.next();) {
						numberMessagesPerPEP = rs2.getInt(1);
						folder = rs2.getString(2);
						if (numberMessagesPerPEP > 0)
							System.out.println(pepNumber + "," + numberMessagesPerPEP + "," + thePEPType);
						PreparedStatement statement3 = connection.prepareStatement(
								"INSERT INTO "+ firstDBoutputtable +" (pep, peptype,msgcount,folder) values (?, ?,?,?)");
						statement3.setInt(1, j);
						statement3.setString(2, thePEPType);
						statement3.setString(4, folder);
						statement3.setInt(3, numberMessagesPerPEP);
						int row3 = statement3.executeUpdate();
						if (row3 > 0) {
						}
						totalNumberMessages = totalNumberMessages + numberMessagesPerPEP;
					}
			}
			System.out.println("Total number of messages "+ totalNumberMessages);			
		}
		catch (SQLException e)	{
			System.out.println( e.getMessage() );
		}
		catch (Exception ex)	{
			System.out.println( ex.getMessage() );
		}
	}	
	
	//second question
	private static void researchQuestionTwo(Integer pepType, Connection connection) {
		Integer pepNumber, numberMessages;
		ArrayList<String> stateList = new ArrayList<String>();
		
		//jan 2021
		ArrayList<String> topmembers = new ArrayList<String>();
		try {
			topmembers = getTopMembers(connection,topmembers);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();			System.out.println("e.str "+ e1.toString());			System.out.println(StackTraceToString(e1)  );
		}
		System.out.println("here 0" );
		
		try{
			//Get List of PEPs in each PEPType List	
			allPepPerTypes = getallPepsForPepTypeb4(pepType, false, connection, pepcreatedbeforedate);	//this we get from pep details table
		
			//not used now as they are retrieved dynamically
			//String states[] = {"DRAFT","OPEN","ACTIVE", "PENDING", "CLOSED", "FINAL","ACCEPTED", "DEFERED","REPLACED","REJECTED","POSTPONED", "INCOMPLETE","SUPERSEDED", "WITHDRAWN"};
			//String states[] = {"DRAFT","ACCEPTED","REJECTED","FINAL"};
			Date startDate = null, endDate = null;
			System.out.println("total pep size " + allPepPerTypes.size());						
			Integer count=0;
			System.out.println("Pep ID, PEP Type, Pairwise-Transition, Count" );
			
			//For each PEP in each PEPType List
			for (int j : allPepPerTypes) {	
				String thePEPType =getPepTypeForPep(j, connection);
				
				//get the list of states for the pep from the above stateList					//we just want email message and daniel state data, ignore gmane data and pep summary
				//previous version based on 'allpeps' table
				//String sql2 = "SELECT email,dateTime from "+dataTable+" WHERE pep = " + j + " and messageID BETWEEN 500000 AND 510000 order by dateTime;";  //there will be many rows since there is no seprate table to store this information
				//new version code
				
				//jan 2021 ..we consider all communicatiosn for these peps till bdfl resigned...and remove the cutoff date
																									//and date2 <= "+pepcreatedbeforedate+"
				String sql2 = "SELECT email, dateTimeStamp from "+stateDataTable+" WHERE pep = " + j + "  order by dateTimeStamp;";  //there will be many rows since there is no seprate table to store this information
				Statement stmtCount = connection.createStatement(), stmt2 = connection.createStatement();
				ResultSet rsCount = stmtCount.executeQuery( sql2 ), rs2 = stmt2.executeQuery( sql2 );
				
				int rowcount = getRowCount(rsCount);
				//System.out.println("rowcount"+ rowcount);
				stateList.clear();
				
				//For each state in PEP 
				if (rowcount>0) {
				  //rowcount = rs2.getRow();
				  //rs2.beforeFirst(); // not rs.first() because the rs.next() below will move on, missing the first element
				  while (rs2.next()){     
						String state =  rs2.getString(1);	//email field contains state						
						String date =  rs2.getString(2);
						state = state.replaceAll("Status :", ""); 						state = state.trim();							stateList.add(state);
						//System.out.println("\t\t\t\t\t added state here " + state +" for j "+ j);
				   }
				  
				  startDate = endDate = null;
				  count =0;
				  pepNumber = j;
				  if (stateList.size()!=0){
					
					startDate = f.getStartDate(pepNumber, stateList.get(0) ,connection);
//						System.out.println("c");
					if(detailedData) {
						
					//jan 2021..lets do this for the authorsrole
						count= f.getNumberDiscussions_AuthorRoleBeforeDates(connection, pepNumber, startDate, stateList.get(0),thePEPType,detailedDataTablename, "");
						//local_insertPairwiseStateIntoDB(connection, "PRE-"+ stateList.get(0), count, thePEPType, pepNumber,"",overallDataOnlyTablename, "");
						
					//jan 2021...for each member in the top 20 and proposal author and bdfl delegate
						for (String author: topmembers) {
					//	for (String ml: mailingLists) {
							count= f.getNumberDiscussions_AuthorBeforeDates(connection, pepNumber, startDate, stateList.get(0),thePEPType,detailedDataTablename, author);
							//local_insertPairwiseStateIntoDB(connection, "PRE-"+ stateList.get(0), count, thePEPType, pepNumber,"",overallDataOnlyTablename, author);
							//System.out.println(pepNumber +  ","+ "" + ","+ thePEPType+","+  "PREDRAFT-"+ stateList.get(0)  +","  +count);		//can add "Startdate: " + startDate + ", Enddate " + endDate + ",
							//insertPairwiseStateIntoDB(connection, "PREDRAFT-"+ stateList.get(0), count, thePEPType, pepNumber,"");
					 // 	}
						}
					}
					if (overallDataOnly){  //overall  //june 2020 made 'predraft' to just 'pre'
						count= f.getNumberDiscussionsBeforeDate(connection, pepNumber, startDate);
						System.out.println(pepNumber +  ","+ "" + ","+ thePEPType+","+  "PRE-"+ stateList.get(0)  +","  +count);		//can add "Startdate: " + startDate + ", Enddate " + endDate + ",
						//local_insertPairwiseStateIntoDB(connection, "PRE-"+ stateList.get(0), count, thePEPType, pepNumber,"",overallDataOnlyTablename, author);
					}
				  }
				  
				  //For each state in statelist for each PEP
				  String lastState =stateList.get(0);
				  for(int k=0; k<stateList.size()-1; k++){						  
						startDate = f.getStartDate(pepNumber, stateList.get(k) ,connection);	
						
						endDate = f.getEndDate(pepNumber, stateList.get(k+1) ,connection);
						
						//|| startDate.toString().equals("null")
						if (startDate==null || endDate ==null)
						{
							System.out.println( "null----- "+ stateList.get(k) + " " + startDate +" "+ stateList.get(k+1)+" " + endDate);
						}							
						else{
							if(detailedData) {
								//jan 2021...for authorsrole
								count = f.getNumberDiscussions_AuthorsRoleBetweenDates(connection, pepNumber, startDate, endDate,thePEPType,stateList.get(k), stateList.get(k+1),detailedDataTablename, "");
								//jan 2021...for each member in the top 20 and proposal author and bdfl delegate
								for (String author: topmembers) {
									count = f.getNumberDiscussions_AuthorBetweenDates(connection, pepNumber, startDate, endDate,thePEPType,stateList.get(k), stateList.get(k+1),detailedDataTablename, author);
									//local_insertPairwiseStateIntoDB(connection, stateList.get(k)  + "-"+ stateList.get(k+1) , count, thePEPType, pepNumber,"",overallDataOnlyTablename, author);
								}
							}
							//for all mailing lists -- from an array, get th number of emaiol messages
							if (overallDataOnly){
								count = f.getNumberDiscussionsBetweenDates(connection, pepNumber, startDate, endDate);
								System.out.println(pepNumber + ","+ "" + ","+ thePEPType+","+ stateList.get(k)  + "-"+ stateList.get(k+1)  +","  +count);		//can add "Startdate: " + startDate + ", Enddate " + endDate + ",
								//local_insertPairwiseStateIntoDB(connection, stateList.get(k)  + "-"+ stateList.get(k+1) , count, thePEPType, pepNumber,"",overallDataOnlyTablename, author);
							}	
							
						}
						lastState =stateList.get(k+1);
						if (lastState==null || lastState.isEmpty())
							lastState =stateList.get(k);
						//System.out.println("Pep " + pepNumber + " "+ stateList.get(k) +" Startdate: " + startDate + " "+ stateList.get(k+1) +" Enddate " + endDate + " Count:"  +count);
					}
				  
				  	//get final-post (have to check if draft exists)
					//use the enddarte of the last major state captured from above for loop
					if(stateList.size()!=0){
						if(detailedData) {
						//	for (String ml: mailingLists) {
							//jan 2021...for each member in the top 20 and proposal author and bdfl delegate
							count=f.getNumberDiscussions_AuthorsRoleAfterDate(connection, pepNumber, endDate, lastState,thePEPType,detailedDataTablename, "");
							for (String author: topmembers) {
								count=f.getNumberDiscussions_AuthorAfterDate(connection, pepNumber, endDate, lastState,thePEPType,detailedDataTablename, author);
								//local_insertPairwiseStateIntoDB(connection, lastState  +"-POST", count, thePEPType, pepNumber,"",overallDataOnlyTablename, author);
							}
								//System.out.println(pepNumber +  ","+ ml + ","+ thePEPType+ ","+   lastState  +"-FINALPOST,"  +count);		//can add "Startdate: " + startDate + ", Enddate " + endDate + ",
								//local_insertPairwiseStateIntoDB(connection, lastState  +"-FINALPOST", count, thePEPType, pepNumber,ml);
						//	}
						}
						if (overallDataOnly){ //june 2020 made finalpost to post
							count=f.getNumberDiscussionsAfterDate(connection, pepNumber, endDate);					
							System.out.println(pepNumber +  ","+ "" + ","+ thePEPType+ ","+   lastState  +"-POST,"  +count);		//can add "Startdate: " + startDate + ", Enddate " + endDate + ",
							//local_insertPairwiseStateIntoDB(connection, lastState  +"-POST", count, thePEPType, pepNumber,"",overallDataOnlyTablename, author);
						}
					}
				  
				}
				else 
					 rowcount =0;   //no state found for the pep
//					System.out.println("final part of loop");
				//allPepPerTypes.get(j-1); //start from 0
//					System.out.println("a");
				//get pre draft (have to check if draft exists)
				//use startdate from the first major state date
				
				
//					System.out.println("d");
				
				
				
				//HAVE A FOR LOOP FOR ALL STATE COMBINATIONS
				/*
				for (String startState: states)  {
					for (String endState: states)  {
						if (!startState.equals(endState))  {
							startDate = getStartDate(pepNumber, startState,connection);						
							endDate = getEndDate(pepNumber, endState,connection);
							//|| startDate.toString().equals("null")
							if (startDate==null || endDate ==null)
							{}							
							else{
								count = getNumberDiscussionsBetweenDates(connection, pepNumber, startDate, endDate, count);
								System.out.println("Pep " + pepNumber + " "+ startState +" Startdate: " + startDate + " "+ endState +" Enddate " + endDate + " Count:"  +count);						
							}
						}
					}
				}
				*/
				//System.out.println("");
			}			
		}
		catch (SQLException e)
		{
			System.out.println( e.getMessage() );
		}
		catch (Exception ex)	{
			System.out.println( ex.getMessage() );
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
	
	static ArrayList<String> getTopMembers(Connection connection, ArrayList<String> topmembers) throws SQLException {
		
		Integer count = 0; String author ="";
		//and messageID <100000;
		//folder like '%"+folder+"%' 
		try {
			String sql3 = "SELECT author FROM top10authors WHERE author IS NOT NULL";  //there will be many rows since there is no seprate table to store this information
			Statement stmt3 = connection.createStatement();
			ResultSet rs3 = stmt3.executeQuery( sql3 );
			while (rs3.next()){    
				author = rs3.getString("author");  topmembers.add(author);	
				//		folder = rs3.getString(2);		
				//JAN 2021
				//folder = folder.substring(12);
				//System.out.println("\t"+pepNumber +  ","+ folder+ ","+ thePEPType+","+  "PREDRAFT-"+ state  +","  +count);		//can add "Startdate: " + startDate + ", Enddate " + endDate + ",
				//insertPairwiseStateIntoDB(connection, "PREDRAFT-"+ state, count, thePEPType, pepNumber,folder,tablename);
				//insertPairwiseStateIntoDB(Connection connection, String pairwisestate, Integer count, String thePEPType, Integer k, String folder)
			}
			//else{
	//						System.out.println("State end not found for pep " + pepNumber);
			//}
		}	catch (Exception e)	{
			e.printStackTrace();			System.out.println("e.str "+ e.toString());			System.out.println(StackTraceToString(e)  );
		}	
		return topmembers;
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
				query = "SELECT pep, peptype FROM allpepstillbdfl order by pep asc";
			else	    	  
				query = "SELECT pep, peptype FROM allpepstillbdfl AND peptype like '%" + pepTypeString + "%' order by pep asc";
			
			// create the java statement
			Statement st = conn.createStatement();			ResultSet rs = st.executeQuery(query);
			Integer counter=0;			
			System.out.println("PEP Type " + pepTypeString);  
			while (rs.next())	{			
				Integer pepNumber = rs.getInt("pep");				type = rs.getString("peptype");
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
			e.printStackTrace();			System.out.println("e.str "+ e.toString());			System.out.println(StackTraceToString(e)  );
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
	
	public static String getPepTypeForPep(Integer pep, Connection conn){
		String pepTypeString =null; 	//Integer[] peps = new Integer[500];
		ArrayList<Integer> peps = new ArrayList<Integer>();
		
		try	{
			String query= "";
			if (pep<0)
				System.out.println("Error no pep as " + pep);
			else	    	  
				query = "SELECT peptype FROM allpepstillbdfl where pep = " + pep + ";";

			Statement st = conn.createStatement();			ResultSet rs = st.executeQuery(query);			
			 
			if (rs.next())  
				pepTypeString = rs.getString("peptype");	

			//System.out.println("\n\nTotal number of peps in "+ pepTypeString + " is " + counter + " list size " + peps.size());
			st.close();
		}	catch (Exception e)	{
			e.printStackTrace();			System.out.println("e.str "+ e.toString());			System.out.println(StackTraceToString(e)  );
		}		
		return pepTypeString.trim();
	}


	public static void local_insertPairwiseStateIntoDB(Connection connection, String pairwisestate, Integer count, String thePEPType, Integer k, String folder, String tablename, String author) throws SQLException {
		try {
			//PreparedStatement statement3 = connection.prepareStatement("INSERT INTO pepcountsRQ2bysubphases (pep, peptype,pairwise,msgcount,folder) values (?, ?,?,?,?)");
			PreparedStatement statement3 = connection.prepareStatement("INSERT INTO "+tablename+" (pep, peptype,pairwise,msgcount,folder, author) values (?, ?,?,?,?,?)");
			statement3.setInt(1, k);							statement3.setString(2, thePEPType);
			statement3.setString(3, pairwisestate);			statement3.setInt(4, count);
			statement3.setString(5, folder);				statement3.setString(6, author);
			int row3 = statement3.executeUpdate();
		}
		catch (SQLException e)	{
			System.out.println( e.getMessage() );
		}
		catch (Exception ex)	{
			ex.printStackTrace();			System.out.println("e.str "+ ex.toString());			System.out.println(StackTraceToString(ex)  );
		}
	}

	//third question
	private static void researchQuestionThree(Integer pepType, Connection connection) {
		ArrayList<Double> vlist;
		try{			
			vlist = rq3(pepType,connection);			//RQ3.
			//System.out.println("output List ");
			for (Double d : vlist){
				//System.out.println("\nList "+ d);
				String sql3 = "SELECT pep from pepdetails WHERE python_version like '%" + d + "%';";  //there will be many rows since there is no seprate table to store this information
				Statement stmt3 = connection.createStatement();
				ResultSet rs3 = stmt3.executeQuery( sql3 );
				Integer total=0;
				while (rs3.next()){    
					String pep =  rs3.getString(1);
					System.out.print("\n" + d + "," + pep+",");
					//get the number of discussion in this pep
					Integer num = f.getDiscussionsForPEPNumber(connection,Integer.parseInt(pep), d);
					total +=num;					
				}
				System.out.println("\nTotal,,"+total);
			}
		}
		catch (SQLException e)	{
			System.out.println( e.getMessage() );
		}
	}
	
	
	private static ArrayList<Double> rq3(Integer pepType, Connection connection)
    {	
		Integer pepNumber, numberMessages;
		ArrayList<Integer> PepList = new ArrayList<Integer>();
		ArrayList<Double> versionList = new ArrayList<Double>();
		Integer counter=0;
//		Integer pepType=1;
		String pepTypeString =f.assignPEPTypes(pepType);
		try{		
			
			String query= "";
			if (pepType==0)
				query = "SELECT pep, type FROM pepdetails order by pep asc";
			else	    	  
				query = "SELECT pep, type FROM pepdetails where type like '%" + pepTypeString + "%' order by pep asc";
			Statement st0 = connection.createStatement();
			ResultSet rs0 = st0.executeQuery(query);

			//get all standard peps
			System.out.println("PEP Type " + pepTypeString); 
			while (rs0.next())
			{					
				pepNumber = rs0.getInt("pep");	
				System.out.print(pepNumber+ ((pepNumber%20==19) ? "\n" : " "));
				//System.out.println(pepNumber);
				PepList.add(pepNumber);
				counter++;
			}
			System.out.println("\n\nTotal number of peps in " +pepTypeString+ " is " + counter + ", Populated list size " + PepList.size());
			st0.close();

			//process all standard peps
			Integer count=0;			
			System.out.println("PEPs in Pep Type:");			
			for (Integer k : PepList) { 		
				pepNumber = k; //start from 0
				//get python version number
				String sql2 = "SELECT python_version from pepdetails WHERE pep = " + pepNumber + ";";  //there will be many rows since there is no seprate table to store this information
				Statement stmt2 = connection.createStatement();
				ResultSet rs2 = stmt2.executeQuery( sql2 );
				if (rs2.next()){    
					String version =  rs2.getString(1);
     				System.out.println("PEP " + pepNumber + " is python_version is " + version);
					if(version==null)	{
//						System.out.print("| is null");
//						System.out.println("PEP " + pepNumber + " is python_version is " + version);
					}
					else if (!f.isDouble(version)){
//						System.out.print("| not double");
//						System.out.println("PEP " + pepNumber + " is python_version is not double " + version.trim());
					}
					else{
//						System.out.print("| proper");
						//System.out.println("PEP " + pepNumber + " is python_version is " + version.trim() );
						Double ver = Double.valueOf(version.trim());
						//if list does not have the version number, add 
						if(!versionList.contains(ver))	{	
							versionList.add(ver);
							//System.out.println("\tadded ver to list "); 
						}
//						System.out.println("PEP " + pepNumber + " python_version is double " + ver);
					}
				}
				else{
					System.out.println("PEP " + pepNumber + " is python_version field not provided");
				}
				
//				for (Double d : list){
//					System.out.println("List "+ d);
//				}
				count++;
				
			}	
			//query all peps in pepdetails table which have this python vesion in the version number			
		}		
		catch (SQLException ex)	{
			System.out.println( ex.getMessage() );
		}
		catch (Exception e)	{
			System.out.println( e.getMessage() );
		}
		
		return versionList;
	}
	
	
	private static void researchQuestionFour(Integer pepType, Connection connection ) {		
		Integer pepNumber, numberMessages = 0;
		boolean showOutput=false;		
		Integer jan=0, feb = 0, mar=0, apr = 0, may = 0, jun = 0, jul = 0, aug = 0, sep=0, oct=0, nov=0, dec = 0;
		Integer[][] monthWeek = new Integer [12][5];
		
		for(int i=0; i<monthWeek.length; i++) {
	        for(int j=0; j<monthWeek[i].length; j++) {
	        	monthWeek[i][j]=0;	            
	        }
		}
		
		//Arrays.fill(monthWeek,new Integer(0));
		Integer week = 0;
		
		try{			
			//Integer pep =318;
			allPepPerTypes = pd.getallPepsForPepTypeb4(pepType, false, connection,pepcreatedbeforedate);	//this we get from pep details table
			System.out.println("Processing PEPs ");
			for (int j : allPepPerTypes) 
			{		
				System.out.print(j+ " ");
				Statement stmt = connection.createStatement();
				//, distinct messageID 								//pep
				String sql2 = "SELECT date2 from allpeps where pep ="+j+" and messageID < 100000;";  //there will be many rows since there is no seprate table to store this information
				Statement stmt2 = connection.createStatement();
				ResultSet rs2 = stmt2.executeQuery( sql2 );
				
				while (rs2.next()){					
					Date date = rs2.getDate(1);
					int month = date.getMonth();
					month = month+1;			
					
					int dateOfMonth = date.getDate();
					if (dateOfMonth >= 1 && dateOfMonth <= 7){
						week=1;
					}
					else if (dateOfMonth >= 8 && dateOfMonth <= 14){
						week=2;
					}
					else if (dateOfMonth >= 15 && dateOfMonth <= 21){
						week=3;
					}
					else if (dateOfMonth >= 22 && dateOfMonth <= 28){
						week=4;
					}
					else if (dateOfMonth >= 29 && dateOfMonth <= 31){
						week=5;
					}
					else{
						System.out.println("week error");
					}
//					System.out.println("date "+date+" week = " + week + " month " + month);
					//increment double array
					monthWeek[month-1][week-1] = monthWeek[month-1][week-1] + 1; 
					
					numberMessages++;
					//System.out.println("PEP " + 318 + " date in " + date + " month: " + month);
					
					if(month==1)
						++jan;
					else if(month==2)
						++feb;
					else if(month==3)
						++mar;
					else if(month==4)
						++apr;
					else if(month==5)
						++may;
					else if(month==6)
						++jun;
					else if(month==7)
						++jul;
					else if(month==8)
						++aug;
					else if(month==9)
						++sep;
					else if(month==10)
						++oct;
					else if(month==11)
						++nov;
					else if(month==12)
						++dec;					
					//else{
					//	System.out.println("PEP " + 318 + " has no date ");
				}
				
			} //end for loop	
				
				
			System.out.println("\nTotal messages for all peps " + numberMessages);
			float percent = (float) jan/numberMessages * 100;
			System.out.println("Jan," + jan + ", " + percent);			
			percent = (float) feb/numberMessages * 100;
			System.out.println("Feb," + feb + ", " + percent);			
			percent = (float) mar/numberMessages * 100;
			System.out.println("Mar," + mar + ", " + percent);			
			percent = (float) apr/numberMessages * 100;
			System.out.println("Apr," + apr + ", " + percent);			
			percent = (float) may/numberMessages * 100;
			System.out.println("May," + may + ", " + percent);
			percent = (float) jun/numberMessages * 100;
			System.out.println("Jun, " + jun + ", " + percent);
			percent = (float) jul/numberMessages * 100;
			System.out.println("Jul, " + jul + ", " + percent);
			percent = (float) aug/numberMessages * 100;
			System.out.println("Aug, " + aug + ", " + percent);
			percent = (float) sep/numberMessages * 100;
			System.out.println("Sep, " + sep + ", " + percent);
			percent = (float) oct/numberMessages * 100;
			System.out.println("Oct, " + oct + ", " + percent);
			percent = (float) nov/numberMessages * 100;
			System.out.println("Nov, " + nov + ", " + percent);
			percent = (float) dec/numberMessages * 100;
			System.out.println("Dec,1 " + dec + ", " + percent);	
			
			//output final double array
			for(int i=0; i<monthWeek.length; i++) {
		        for(int j=0; j<monthWeek[i].length; j++) {
		            System.out.println("Values at arr["+i+"]["+j+"] is "+monthWeek[i][j]);
		        }
			}
			
		}
		catch (SQLException e)
		{
			System.out.println( e.getMessage() );
		}
		catch (Exception ex)	{
			System.out.println( ex.getMessage() );
		}
	}
	
	private static void RQ4(Integer pepType, Connection connection) {
		try{
			PrintWriter writer = new PrintWriter("c:\\scripts\\outRQ4.txt", "UTF-8");			
			String sql3 = "SELECT distinct messageID, dateTime from allpeps where messageID < 100000;";  //there will be many rows since there is no seprate table to store this information
			Statement stmt3 = connection.createStatement();
			ResultSet rs3 = stmt3.executeQuery( sql3 );
			while (rs3.next()){    
				Integer messageID =  rs3.getInt(1);
				Timestamp dateTime =  rs3.getTimestamp(2);
				//System.out.print("\n" + messageID + "," + dateTime);
				
				writer.println(messageID + "," + dateTime);
			}
			
			writer.close();
		}
		catch (SQLException e)	{
			System.out.println( e.getMessage() );
		}
		catch (IOException e) {
			// do something
		}
	}
	
	private static int getRowCount(ResultSet resultSet) {
	    if (resultSet == null) {
	        return 0;
	    }

	    try {
	        resultSet.last();
	        return resultSet.getRow();
	    } catch (SQLException exp) {
	        exp.printStackTrace();
	    } finally {
	        try {
	            resultSet.beforeFirst();
	        } catch (SQLException exp) {
	            exp.printStackTrace();
	        }
	    }
	    return 0;
	}
	
}
