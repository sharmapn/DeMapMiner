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
//import miner.process.PythonSpecificMessageProcessing;
import utilities.PepUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.joestelmach.natty.*;

//import readRepository.readMetadataFromWeb.GetProposalDetailsWebPage;

// 2020 Jan...i want to see why so high numbers for developers writing standard peps - more than 400

//2019 Update . 
// This script is for Boundary Spanners Analysis
// First clustering of members is done
public class SeparateStudyGroup2 extends JFrame
{	
	static GetProposalDetailsWebPage pd = new GetProposalDetailsWebPage();
	static ArrayList<Integer> allPepPerTypes;
	
	static PythonSpecificMessageProcessing pms = new PythonSpecificMessageProcessing();
	static SeparateStudyGroup2Functions f = new SeparateStudyGroup2Functions();
	static ReturnDifferentCommunityMembers rdcm = new ReturnDifferentCommunityMembers();
	
	static String pepDetailTable = "pepdetailsold"; //pepdetails //old";    //we changed this from pepdetails
	//may 2020, we want to insert all DM roles we found for each pep
	static String debugTable = "pepDMContributionsByRole"; //old";    //we changed this from pepdetails
	
	public static void main(String[] args) {		
		ArrayList<Double> vlist = new ArrayList<>();	
		MysqlConnectForQueries mc = new MysqlConnectForQueries();
		Connection connection = mc.connect();
		
		f.totalNumberOfMessages(connection);
		f.totalNumberOfUniqueMessages(connection);
		
		//2020 - just comment...we run some of these again, researchQuestionOne(0,connection) and researchQuestionTwo(0,connection); were commented as were finalised
		
		//Research GROUP 2  Clustering		
		//finalised
//		researchQuestionOne(0,connection);		//get the number of posts made by each author
		researchQuestionTwo(0,connection);
		//2020 jan commnted as we run the above again
		//researchQuestionThree(0,connection);		//uses new table allmessages
		
		//not finalised				
//		researchQuestionFour(0,connection);			//uses new table allmessages
//***		researchQuestionFive_updateCount(0,connection);	
				
		mc.disconnect();
	}
	
	//get the number of posts made by each author (all community members)
	
	//0 = all peps in database, 
	//1 = Standard about python language, functional enhancements - more action here, eg Pep 308 is standard pep
	//2 = Process
	//3 = Informational
	
	//First decide if you want to run this query for all members or just developers
	//all members will select from and update different tables
	//all members     will select, run and update the table 	- distinctauthorsallmessages
	//just developers will select, run and update the table     - distinctauthorspepnumbers
	private static void researchQuestionOne(Integer pepType, Connection connection) {
		//for churn analysis, after clustering, use 'distinctAuthorPepNumbers' table - it is populated using clusteredsenderfullname column of allmessages tabl;e
		String tablename = "distinctAuthorPepNumbers"; // "alldevelopers"; //distinctauthorspepnumbers //distinctauthorsallmessages
		String pepAuthor=null;
		Integer pepNumber=null, totalNumberOfMessagesPerAuthor=0,totalNumberOfUniqueMessagesPerAuthor=0,totalNumberOfUniqueMessagesPerAuthorPerPEPType=0,
				allPostsByThisAuthorInUniquePeps=0,totalNumberOfSeedingUniqueMessagesPerAuthor=0,totalNumberOfMessagesPerAuthorPerPEPType=0, 
				nullUniqueCount=0,nullTotalCount=0,
				maxPostsInAPepByThisAuthor=0,minPostsInAPepByThisAuthor=0,
				totalProcess=0, totalInfomational=0, totalStandard=0, totalNumberMessages = 0;
		boolean showOutput=false;		
		ArrayList<String> distinctAuthors = new ArrayList<String>();
		ArrayList<Integer> distinctMessagesPerAuthor = new ArrayList<Integer>();
		Integer nullCounter =0, infoCounter = 0,processCounter=0, standardsCounter =0;
		
		String query = "";
		PreparedStatement preparedStmt =null;
		String columnName = null;
		
		try{
			Statement stmt = connection.createStatement();
								//select senderName from alldevelopers/distinctauthorsallmessages/distinctauthorspepnumbers where senderName IS NOT NULL Order By SenderName ASC
			distinctAuthors = f.getAllDistinctAuthorsOrderedByName(connection, tablename);	//this we get from pep details table
			System.out.println("Table: "+tablename+", Distinct Authors Length "+distinctAuthors.size());
//			System.out.println("Author , Null , Info, Process, Standards, Total Unique Messages, Total Number Messages ");
			
			//WE HAVE STORED ALL DISTINCT AUTHORS IN THE DISTINCTAUTHOR TABLE 
			//System.out.println("author, totalNumberOfUniqueMessages,I,P,S, allPostsByThisAuthorInUniquePeps,totalNumberOfSeedingUniqueMessagesPerAuthor, MAX, MIN");
			String a = "AUTHOR,",tm= "TOT#MSG,",tum= "TOT#UNQMSG,", nT="NULLTot,", iT="ITot,",pT="PTot,",sT="STot,",iU="IUnq,",pU="PUnq,",sU="SUnq,",apUNQPEPs="apINUNQPEPs,",
					apUNQNULLPEPs="apUNQNULLPEPs,",apUNQStandardPEPs="apUNQStaPEPs,",apUNQProcessPEPs="apUNQProPEPs,",apUNQInformationalPEPs="apUNQInfoPEPs,", allSeedPosts="allSeedPosts,",ma="MAX,",mi="MIN";
			//"%32s%10d%10d%10d%10d%10d%10d%10d%10d		//%7s%7s%7s
			System.out.format("%30s%11s%11s%9s",a,tm,tum, nT);
			System.out.format("%7s%7s%7s%7s%7s%7s", iT,iU,sT,sU,pT,pU);
			System.out.format("%13s%14s",apUNQPEPs,apUNQNULLPEPs);
			System.out.format("%13s%13s%14s",apUNQStandardPEPs,apUNQProcessPEPs,apUNQInformationalPEPs);
			System.out.format("%15s%7s%7s", allSeedPosts,ma,mi);
			System.out.println();  
			for (String author : distinctAuthors) {
				totalNumberOfMessagesPerAuthor=totalNumberOfUniqueMessagesPerAuthorPerPEPType=totalNumberOfUniqueMessagesPerAuthor=allPostsByThisAuthorInUniquePeps=
				nullUniqueCount=nullTotalCount=totalNumberOfSeedingUniqueMessagesPerAuthor=totalNumberOfMessagesPerAuthorPerPEPType=maxPostsInAPepByThisAuthor=0;minPostsInAPepByThisAuthor=50;
				
				//THI ALSO NEEDS TO BE PUT IN DATABSE WHILE GETTING AUTHORS AS THEY ARE NOT MATCHED
				//this function has few functions to eliminate those sendernames which mess up queries.like single quotes etc,
				author = pms.modifyAuthorNames(author);
					
				if (author.isEmpty() || author==null || author.equals("null"))
				{}
				else{							
					
					//Count of all TOTAL and DISTINCT messages by this author								//was sendername before clustering										//AND pep != -1  -- removed this as we want to count all
					String sql2 = "SELECT COUNT(DISTINCT(messageID)),COUNT(messageID) from allmessages WHERE clusterBySenderFullName = '" + author + "';";  //there will be many rows since there is no seprate table to store this information
					Statement stmt2 = connection.createStatement();
					ResultSet rs2 = stmt2.executeQuery( sql2 );					
					if (rs2.next()){   
						totalNumberOfUniqueMessagesPerAuthor = rs2.getInt(1);
						totalNumberOfMessagesPerAuthor = rs2.getInt(2);
						//if (numberMessagesPerPEP>0)	
//						System.out.print(author + "," + totalNumberOfUniqueMessagesPerAuthor );
						
						//UPDATE all posts by this author	
						updateTableWithParameter(connection, tablename, "allPostsByThisAuthor", totalNumberOfMessagesPerAuthor, author);
						updateTableWithParameter(connection, tablename, "allUniquePostsByThisAuthor", totalNumberOfUniqueMessagesPerAuthor, author);	//UPDATE all unique posts by this author	 		        
					}											
																														//was sendername before clustering		//AND pepType = '"+ type +"'				//and PepType IS NOT NULL
						String sql2A = "SELECT pepType, COUNT(DISTINCT(messageID)),COUNT(messageID) from allmessages WHERE clusterBySenderFullName = '" + author + "'  GROUP BY pepType;";  //there will be many rows since there is no seprate table to store this information
						Statement stmt2A = connection.createStatement();																				//AND pep != -1  we want to count all messages
						ResultSet rs2A = stmt2A.executeQuery( sql2A );	
						Integer iUniqueCount=0,iTotalCount=0, pUniqueCount=0,pTotalCount=0, sUniqueCount=0, sTotalCount=0;
						while (rs2A.next()){   
							//pepNumber = rs2.getInt(1);
							char pt;
							if (rs2A.getString(1)==null)
								pt = ' ';
							else	
								pt = rs2A.getString(1).charAt(0);
							totalNumberOfUniqueMessagesPerAuthorPerPEPType = rs2A.getInt(2);
							totalNumberOfMessagesPerAuthorPerPEPType = rs2A.getInt(3);
							
							//String columnName = getTableColumn(pt);
								
							if(pt=='I'){
								iUniqueCount=totalNumberOfUniqueMessagesPerAuthorPerPEPType;
								iTotalCount=totalNumberOfMessagesPerAuthorPerPEPType;								
								updateTableWithParameter(connection, tablename, "allUniquePostsByThisAuthorInInformationalPeps", iUniqueCount, author);	
								updateTableWithParameter(connection, tablename, "allPostsByThisAuthorInInformationalPEPs", iTotalCount, author);	
							}
							else if(pt=='S'){
								sUniqueCount=totalNumberOfUniqueMessagesPerAuthorPerPEPType;
								sTotalCount=totalNumberOfMessagesPerAuthorPerPEPType;
								updateTableWithParameter(connection, tablename, "allUniquePostsByThisAuthorInStandardPeps", sUniqueCount, author);	
								updateTableWithParameter(connection, tablename, "allPostsByThisAuthorInStandardPEPs", sTotalCount, author);
							}
							else if (pt=='P'){
								pUniqueCount=totalNumberOfUniqueMessagesPerAuthorPerPEPType;
								pTotalCount=totalNumberOfMessagesPerAuthorPerPEPType;
								updateTableWithParameter(connection, tablename,"allUniquePostsByThisAuthorInStandardPeps", pUniqueCount, author);	
								updateTableWithParameter(connection, tablename, "allPostsByThisAuthorInProcessPEPs", pTotalCount, author);
							}
							else if (pt==' '){
								nullTotalCount=totalNumberOfMessagesPerAuthorPerPEPType;							
                    			updateTableWithParameter(connection, tablename, "allPostsByThisAuthorInPepTypeEqualToNULL", nullTotalCount, author);	
							}									
							
						}
						
					//count of all unique peps in which this author made posts
					//seprated by pepTypes
     				//first one just does all unique peps		                 //was sendername before clustering		// AND PEP != -1 removed this as we want to count all messages
					String sql9 = "SELECT COUNT(DISTINCT(pep)) from allmessages WHERE clusterBySenderFullName = '" + author + "';";  //there will be many rows since there is no seprate table to store this information
					Statement stmt9 = connection.createStatement();
					ResultSet rs9 = stmt9.executeQuery( sql9 );					
					if (rs9.next()){	
						allPostsByThisAuthorInUniquePeps = rs9.getInt(1);											
						updateTableWithParameter(connection, tablename, "allPostsByThisAuthorInTotalUniquePeps", allPostsByThisAuthorInUniquePeps, author);	
					}		
					//second section does all pep types
					//Get number of unique peps by pep types in Which Author Posted
					Integer iUniqueCount2=0, pUniqueCount2=0, sUniqueCount2=0, nullUniqueCount2=0,allUniquePepsPerPEPType=0;	// AND PEP != -1  removed this from the below sql as we want to count all messages
					String sql1 = "SELECT pepType, COUNT(DISTINCT(pep)) from allmessages WHERE clusterBySenderFullName = '" + author + "' group by pepType;";  //there will be many rows since there is no seprate table to store this information
					Statement stmt1 = connection.createStatement();
					ResultSet rs1 = stmt1.executeQuery( sql1 );					
					while (rs1.next()){
						//pepNumber = rs2.getInt(1);
						allUniquePepsPerPEPType = rs1.getInt(2);
						//if (numberMessagesPerPEP>0)	
//						System.out.print("," + allPostsByThisAuthorInUniquePeps );
						
						char kl;
						if (rs1.getString(1)==null)
							kl = ' ';
						else	
							kl = rs1.getString(1).charAt(0);						
						
						if(kl=='I'){
							iUniqueCount2=allUniquePepsPerPEPType;														
							updateTableWithParameter(connection, tablename, "totalNumberOfUniqueInformationalPEPTypeInWhichAuthorPosted", iUniqueCount, author);
						}
						else if(kl=='S'){
							sUniqueCount2=allUniquePepsPerPEPType;
							updateTableWithParameter(connection, tablename, "totalNumberOfUniqueStandardPEPTypeInWhichAuthorPosted", iUniqueCount, author);
						}
						else if (kl=='P'){
							pUniqueCount2=allUniquePepsPerPEPType;
							updateTableWithParameter(connection, tablename, "totalNumberOfUniqueProcessPEPTypeInWhichAuthorPosted", iUniqueCount, author);
						}
						else if (kl==' '){
							nullUniqueCount2=allUniquePepsPerPEPType;
							updateTableWithParameter(connection, tablename, "totalNumberOfUniqueNULLPEPTypeInWhichAuthorPosted", iUniqueCount, author);
						}
						
						//all posts by this author
//						String query = "update "+tablename+" set allPostsByThisAuthorInUniquePeps = ? where senderName = ?";
//				        PreparedStatement preparedStmt = connection.prepareStatement(query);
//				        preparedStmt.setInt   (1, allPostsByThisAuthorInUniquePeps);
//		 		        preparedStmt.setString(2, author);
//		 		        preparedStmt.executeUpdate();
					}		
					
					//all seedings posts by this author					//was sendername before clustering			//// AND PEP != -1  removed this from the below sql as we want to count all messages
					String sql3 = "SELECT COUNT(DISTINCT(messageID)) from allmessages WHERE clusterBySenderFullName = '" + author + "' and inReplyTo IS NULL;";  //there will be many rows since there is no seprate table to store this information
					Statement stmt3 = connection.createStatement();
					ResultSet rs3 = stmt3.executeQuery( sql3 );					
					if (rs3.next()){   
						//pepNumber = rs2.getInt(1);
						totalNumberOfSeedingUniqueMessagesPerAuthor = rs3.getInt(1);
						//if (numberMessagesPerPEP>0)	
//						System.out.print("," + totalNumberOfSeedingUniqueMessagesPerAuthor );
						
						//all posts by this author						
						updateTableWithParameter(connection, tablename, "allSeedingPostsByThisAuthor", totalNumberOfSeedingUniqueMessagesPerAuthor, author);
					}
					//for each author, in which pep did he post the max messages - we want the number
					//same this we want for min
																																//   inluded in the below sql as we dont want to include values for when pep = -1
					String sql14 = "SELECT pep, COUNT(DISTINCT(messageID)) as count from allmessages WHERE clusterBySenderFullName = '" + author + "' AND PEP != -1 Group By pep;";  //there will be many rows since there is no seprate table to store this information
					Statement stmt14 = connection.createStatement();
					ResultSet rs14 = stmt1.executeQuery( sql14 );	
					maxPostsInAPepByThisAuthor=0; minPostsInAPepByThisAuthor=50;
					Integer pep=0,currPEPMessageCount=0;
					while(rs14.next()){   
						//compare with current max and current min
						pep = rs14.getInt(1);
						currPEPMessageCount = rs14.getInt(2);
						if (currPEPMessageCount>maxPostsInAPepByThisAuthor)
							maxPostsInAPepByThisAuthor = currPEPMessageCount;
						if (currPEPMessageCount<minPostsInAPepByThisAuthor)
							minPostsInAPepByThisAuthor = currPEPMessageCount;					
					}		
//					System.out.println("," +maxPostsInAPepByThisAuthor + "," +minPostsInAPepByThisAuthor );
					//update max and min values for this author
					updateTableWithParameter(connection, tablename, "maxPostsInAPepByThisAuthor", maxPostsInAPepByThisAuthor, author);
					updateTableWithParameter(connection, tablename, "minPostsInAPepByThisAuthor", minPostsInAPepByThisAuthor, author);
	 		        					
			        //output all
			        String comma = ",";
			        System.out.format("%30s%11s%11s%9s",author+comma,totalNumberOfMessagesPerAuthor+comma, totalNumberOfUniqueMessagesPerAuthor+comma,nullTotalCount+comma,nullUniqueCount+comma);
					System.out.format("%7s%7s%7s%7s%7s%7s", iTotalCount+comma,iUniqueCount+comma,sTotalCount+comma,sUniqueCount+comma,pTotalCount+comma,pUniqueCount+comma);
					System.out.format("%13s%14s",allPostsByThisAuthorInUniquePeps+comma,nullUniqueCount2+comma);
					System.out.format("%13s%13s%14s",sUniqueCount2+comma,pUniqueCount2+comma,iUniqueCount2+comma);
					System.out.format("%15s%7s%7s", totalNumberOfSeedingUniqueMessagesPerAuthor+comma, maxPostsInAPepByThisAuthor+comma,minPostsInAPepByThisAuthor);		         
			        System.out.println();
			        
					nullCounter = infoCounter = processCounter = standardsCounter =0;
					maxPostsInAPepByThisAuthor=0; minPostsInAPepByThisAuthor=50;
					totalNumberOfMessagesPerAuthor=totalNumberOfUniqueMessagesPerAuthor=totalProcess=totalInfomational=totalStandard=totalNumberMessages = 0;
				}  //end else
			}//end for
			//for nulls
			String sql3 = "SELECT COUNT(DISTINCT(messageID)) from allmessages WHERE clusterBySenderFullName IS NULL;";  //there will be many rows since there is no seprate table to store this information
			Statement stmt3 = connection.createStatement();
			ResultSet rs3 = stmt3.executeQuery( sql3 );					
			if (rs3.next()){   
				//pepNumber = rs2.getInt(1);
				totalNumberOfUniqueMessagesPerAuthor = rs3.getInt(1);
				//if (numberMessagesPerPEP>0)	
				System.out.println("NULL" + totalNumberOfUniqueMessagesPerAuthor );
			}
			//System.out.println("Total number of messages "+ totalNumberMessages);			
		}
		catch (SQLException e)	{
			System.out.println( e.getMessage() );
		}
		catch (Exception ex)	{
			System.out.println( ex.getMessage() );
		}
	}

	private static void updateTableWithParameter(Connection connection, String tablename,String columnName, Integer totalNumberOfMessagesPerAuthor, String author) throws SQLException {
		String query = "update "+tablename+" set "+columnName+" = ? where senderName = ?";
		PreparedStatement preparedStmt = connection.prepareStatement(query);
		preparedStmt.setInt   (1, totalNumberOfMessagesPerAuthor);
		preparedStmt.setString(2, author);
		preparedStmt.executeUpdate();
	}

	private static String getTableColumn(char type) {
		String columnName = null;
		if (type == 'I')
			columnName = "allPostsByThisAuthorInInformationalPEPs";
		else if(type == 'S')
			columnName = "allPostsByThisAuthorInStandardPEPs";
		else if(type == 'P')
			columnName = "allPostsByThisAuthorInProcessPEPs";
		return columnName;
	}	
	
	//to find community churn
	//users who have been inactive for the last year starting today
	//for each user find the day of his last post
	//we first update the distinct authors table with the first post and last post dates
	// then using sql we group by year to shows ho many became inactive or joined per year
	//the sql is in file - 
	private static void researchQuestionThree(Integer pepType, Connection connection) 
	{
		ArrayList<String> distinctAuthors = new ArrayList<String>();
		ArrayList<Integer> distinctMessagesPerAuthor = new ArrayList<Integer>();
		Date firstPostDate, lastPostDate,curDate;//maxDate ="2017-03-16"; 
		Integer daysTillLastPost, countTotalPostByAuthor;
		java.sql.Date todaysDate = new java.sql.Date(Calendar.getInstance().getTime().getTime());
		boolean authorCurrent = false;
		char a='0',b='1';
		long t0, t1, t2;
		Integer totalAuthors=0, totalChurn=0,totalStayed=0;
		Integer yearMax=null,yearMin=null;
		String tableName = "distinctauthorpepnumbers"; //"alldevelopers";
		
		try{
			distinctAuthors = f.getAllDistinctAuthorsOrderedByName(connection,"distinctauthorpepnumbers"); //"alldevelopers"); //getAllDistinctAuthorsOrderedbyNumberPosts(connection);	//this we get from pep details table, odered by most posts author
			System.out.println("Distinct Authors Length "+distinctAuthors.size());
			System.out.format("%50s%10s%15s%25s%10s%25s%10s%25s","author","churn","daysLP","LPDate","LPYear","FirstPostDate","FPYear","countTotalPostByAuthor");
			System.out.println();
			//for each author, get the last date of post
			for (String author : distinctAuthors) 
			{								
				daysTillLastPost=0;
				lastPostDate=null;
				t0 = System.currentTimeMillis();
				if (author.contains("%"))
					author = author.replace("%", " ");	
				if (author.contains("'"))
					author = author.replace("'", " "); 
																									//AND messageID < 10000
				//date of collection/max date from table = 2017-03-16 																									//was sendername before
				String sql3 = "SELECT max(date2),min(date2),YEAR(max(date2)),YEAR(min(date2)), DATEDIFF(CURDATE(),max(date2)), COUNT(messageid) from allmessages WHERE clusterBySenderFullName = '" + author + "' ;";  //there will be many rows since there is no seprate table to store this information
				PreparedStatement stmt3 = connection.prepareStatement(sql3);
				ResultSet rs3 = stmt3.executeQuery( sql3 );	
				if (rs3.next())
				{ 
					totalAuthors++;					
					lastPostDate=rs3.getDate(1);
					firstPostDate= rs3.getDate(2);
					yearMax = rs3.getInt(3);
					yearMin = rs3.getInt(4);
					daysTillLastPost=rs3.getInt(5);
					countTotalPostByAuthor =rs3.getInt(6);
					if (daysTillLastPost>365){
					    //System.out.println(author +","+curDate+",1,"+daysTillLastPost + "," + lastPostDate);
     				 	System.out.format("%50s%10s%15s%25s%10s%25s%10s%25s",author+",",b+",",daysTillLastPost+",",lastPostDate+",",yearMax+",",firstPostDate+",",yearMin+",",countTotalPostByAuthor);
					 	System.out.println();
					 	totalChurn++;
					}
					else{
						//System.out.println(author +","+curDate+",0,"+daysTillLastPost + "," + lastPostDate);
						System.out.format("%50s%10s%15s%25s%10s%25s%10s%25s",author+",",a+",",daysTillLastPost+",",lastPostDate+",",yearMax+",",firstPostDate+",",yearMin+",",countTotalPostByAuthor);
					 	System.out.println();
					 	totalStayed++;
					}
					String query = "update "+tableName+" set firstPostDate =?, yearFirstPostDate = ?, LastPostDate = ?, "
							+ "yearLastPostDate = ?, daysTillLastPost=?  where sendername = ?";	//was sendername before
			        PreparedStatement preparedStmt = connection.prepareStatement(query);
			        preparedStmt.setDate   (1, firstPostDate);
	 		        preparedStmt.setInt   (2, yearMin);
	 		        preparedStmt.setDate    (3, lastPostDate);
	 		        preparedStmt.setInt    (4, yearMax);
	 		        preparedStmt.setInt    (5, daysTillLastPost);
	 		        //preparedStmt.setInt    (6, countTotalPostByAuthor);
	 		        preparedStmt.setString (6, author);
	 		        preparedStmt.executeUpdate();
				}
				t1 = System.currentTimeMillis();				
				//System.out.println("Finished author - Elapsed time =" + (t1 - t0) / 1000 + " minutes");				
			}
			System.out.println("totalAuthors: "+totalAuthors+ " totalChurn: " +totalChurn+" totalStayed:" + totalStayed);
			
		}
		catch (SQLException e)	{
			System.out.println( e.getMessage() );
		}
		catch (Exception ex)	{
			System.out.println( ex.getMessage() );
		}
	}
	
	//	social network analysis
	private static void researchQuestionFour(Integer pepType, Connection connection) 
	{
		Integer seedingCount=0, totalDistinctCounter=0;
		String author,comma=",";
		ArrayList<String> distinctAuthors = new ArrayList<String>();
		
		Integer pepNumber=null, totalNumberOfMessagesPerAuthor=0,totalNumberOfUniqueMessagesPerAuthor=0,totalNumberOfUniqueMessagesPerAuthorPerPEPType=0,
				totalNumberOfMessagesPerAuthorPerPEPType=0, 
				nullUniqueCount=0,nullTotalCount=0,				
				totalNumberMessages = 0;
		
		try{
			Statement stmt = connection.createStatement();
			
			distinctAuthors = f.getAllDistinctAuthorsOrderedByName(connection,"distinctauthorspepNumbers"); //getAllDistinctAuthorsOrderedbyNumberPosts(connection);	//this we get from pep details table, odered by most posts author
			System.out.println("Distinct Authors Length "+distinctAuthors.size());
			System.out.format("%50s%20s%10s%10s%10s%10s","Author","Seeding Posts","Null","I","S","P");
			System.out.println();
			//for each author, get the last date of post
			for (String au : distinctAuthors) 
			{		
																												//AND messageID < 100000   //AND folder = '" + list + "'
				String sql3 = "SELECT COUNT( DISTINCT(messageID)) c, senderName from allmessages WHERE inReplyTo IS NULL AND senderName = '"+au+"' ORDER BY c DESC;";  //there will be many rows since there is no seprate table to store this information
				PreparedStatement stmt3 = connection.prepareStatement(sql3);
				ResultSet rs3 = stmt3.executeQuery( sql3 );	
				while (rs3.next()){   
					seedingCount=rs3.getInt(1);
					author = rs3.getString(2);
//					System.out.format("%50s%7s",au,count);
//					System.out.println();
					totalDistinctCounter = totalDistinctCounter+seedingCount;
					//updateDistinctAuthorTable
					String query = "update distinctauthors set countSeedingPosts = ? where author = ?";
			        PreparedStatement preparedStmt = connection.prepareStatement(query);
			        preparedStmt.setInt   (1, seedingCount);
	 		        preparedStmt.setString(2, au);
	 		        preparedStmt.executeUpdate();
				}
			
				//now for each pepType
				String sql4 = "SELECT COUNT( DISTINCT(messageID)) c, senderName, pepType from allmessages WHERE inReplyTo IS NULL AND senderName = '"+au+"' GROUP by pepType ORDER BY c DESC;";  //there will be many rows since there is no seprate table to store this information
				PreparedStatement stmt4 = connection.prepareStatement(sql3);
				ResultSet rs4 = stmt4.executeQuery( sql4 );	
				Integer count, iUniqueCount=0, pUniqueCount=0, sUniqueCount=0;
				while (rs4.next()){ 
					totalNumberOfMessagesPerAuthor=totalNumberOfUniqueMessagesPerAuthor=totalNumberMessages = 0;
					
					count=rs4.getInt(1);
					author = rs4.getString(2);
					//System.out.println(author+ ", "+count );
					totalDistinctCounter = totalDistinctCounter+count;					
					char pt;
					if (rs4.getString(3)==null)
						pt = ' ';
					else	
						pt = rs4.getString(3).charAt(0);
					
					totalNumberOfUniqueMessagesPerAuthorPerPEPType = rs4.getInt(1);
					//totalNumberOfMessagesPerAuthorPerPEPType = rs4.getString(2);
					
	//				System.out.print(totalNumberOfUniqueMessagesPerAuthorPerPEPType + ",");
					String columnName = getTableColumn(pt);
						
					if(pt=='I'){
						iUniqueCount=totalNumberOfUniqueMessagesPerAuthorPerPEPType;
						//iTotalCount=totalNumberOfMessagesPerAuthorPerPEPType;
					}
					else if(pt=='S'){
						sUniqueCount=totalNumberOfUniqueMessagesPerAuthorPerPEPType;
						//sTotalCount=totalNumberOfMessagesPerAuthorPerPEPType;
					}
					else if (pt=='P'){
						pUniqueCount=totalNumberOfUniqueMessagesPerAuthorPerPEPType;
						//pTotalCount=totalNumberOfMessagesPerAuthorPerPEPType;
					}
					else if (pt==' '){
						nullUniqueCount=totalNumberOfUniqueMessagesPerAuthorPerPEPType;
						//nullTotalCount=totalNumberOfMessagesPerAuthorPerPEPType;
					}
					
					if (pt != ' ')
					{
						//all posts by this author
	//					String query = "update "+tablename+" set "+columnName+" = ? where senderName = ?";
	//			        PreparedStatement preparedStmt = connection.prepareStatement(query);
	//			        preparedStmt.setInt   (1, totalNumberOfUniqueMessagesPerAuthorPerPEPType);
	//	 		        preparedStmt.setString(2, author);
	//	 		        preparedStmt.executeUpdate();
					}				
					
					//updateDistinctAuthorTable
					/*
					String query = "update distinctauthors set countSeedingPosts = ? where author = ?";
			        PreparedStatement preparedStmt = connection.prepareStatement(query);
			        preparedStmt.setInt   (1, count);
	 		        preparedStmt.setString(2, author);
	 		        preparedStmt.executeUpdate();
	 		        */	 		       
				}		
				System.out.format("%50s%20s%10s%10s%10s%10s",au+comma, seedingCount+comma,nullUniqueCount+comma,iUniqueCount+comma,sUniqueCount+comma,pUniqueCount);
	 		    System.out.println();
			
				//System.out.println("totalDistinct Counter = "+totalDistinctCounter );
			}
		}
		catch (SQLException e)	{
			System.out.println( e.getMessage() );
		}
		catch (Exception ex)	{
			System.out.println( ex.getMessage() );
		}
	}
	
	private static void researchQuestionFive_updateCount(Integer pepType, Connection connection) 
	{
		//create an arraylist of authors and replytocount
		//update this as you find more replies to this author
		ArrayList<Integer> distinctMessagesPerAuthor = new ArrayList<Integer>();
		
		Integer counter=0;
		Date postDate,replyPostDate; 
		String subject, emailMessageID, sender, replySender,replySubject;
		Integer mid;
		long t0, t1, t2;

		Integer countReplies=0;
		try{
			Statement stmt = connection.createStatement();
			t0 = System.currentTimeMillis();
			String sql3 = "SELECT messageID, emailMessageID from all_distinctmessages;";  //there will be many rows since there is no seprate table to store this information
			PreparedStatement stmt3 = connection.prepareStatement(sql3);
			ResultSet rs3 = stmt3.executeQuery( sql3 );	
			
			while (rs3.next()){   
				mid=rs3.getInt(1);						
				emailMessageID= rs3.getString(2);
					System.out.println("mid " +mid+ " # Replies To message: "+countReplies);
					String query = "update distinctauthor set RepliesTo = (SELECT count(emailMessageID) from all_distinctmessages WHERE inReplyTo = '"+emailMessageID+"' order by date2) where messageID = ?";
				    PreparedStatement preparedStmt = connection.prepareStatement(query);
				    //preparedStmt.setInt   (1, countReplies);
				    preparedStmt.setInt   (1, mid);
				    preparedStmt.executeUpdate();				
			}
			
			/*
			while (rs3.next()){   
				mid=rs3.getInt(1);						
				emailMessageID= rs3.getString(2);			
				String sql4 = "SELECT count(emailMessageID) from all_distinctmessages WHERE inReplyTo = '"+emailMessageID+"' order by date2;";  //there will be many rows since there is no seprate table to store this information
				PreparedStatement stmt4 = connection.prepareStatement(sql4);
				ResultSet rs4 = stmt4.executeQuery( sql4 );	
				//now update the record
				if (rs4.next()){ 
					countReplies= rs4.getInt(1);
					System.out.println("mid " +mid+ " # Replies To message: "+countReplies);
					String query = "update all_distinctmessages set countRepliesToThisMessage = ? where messageID = ?";
				    PreparedStatement preparedStmt = connection.prepareStatement(query);
				    preparedStmt.setInt   (1, countReplies);
				    preparedStmt.setInt   (2, mid);
				    preparedStmt.executeUpdate();
				}
			}
			*/
			t1 = System.currentTimeMillis();
			System.out.println("Finished author - Elapsed time =" + (t1 - t0) / 1000 + " minutes");							
		}
		catch (SQLException e)	{
			System.out.println( e.getMessage() );
		}
		catch (Exception ex)	{
			System.out.println( ex.getMessage() );
		}
	}
	
	//RUN THIS PROCEDURE ON THE DISTINCT MESAGES TABLE

	//try for the month of february 2017,and for python dev, find all messages which are parent messages
	private static void researchQuestionFive(Integer pepType, Connection connection) 
	{
		Integer counter=0;
		ArrayList<String>distinctPythonLists = new ArrayList<String>();
		ArrayList<Integer> distinctMessagesPerAuthor = new ArrayList<Integer>();
		ArrayList<Integer> years = new ArrayList<Integer>();
		ArrayList<Integer> months = new ArrayList<Integer>();
		Date postDate,replyPostDate; 
		String subject, emailMessageID, sender, replySender,replySubject;
		java.sql.Date todaysDate = new java.sql.Date(Calendar.getInstance().getTime().getTime());
		boolean authorCurrent = false;
		long t0, t1, t2;
		years.add(2016);
		years.add(2017);
		months.add(1);
		months.add(2);
		months.add(3);
		months.add(4);
		months.add(5);
		months.add(6);
		months.add(7);
		months.add(8);
		months.add(9);
		months.add(10);
		months.add(11);
		months.add(12);
		Integer countReplies=0;
		try{
			Statement stmt = connection.createStatement();
			distinctPythonLists.add("C:\\datasets\\python-dev");	//this we get from pep details table, odered by most posts author
			System.out.println("Distinct Python List Length "+distinctPythonLists.size());
			System.out.println("List , Churn, days Till LastPost, lastpost date ");
			
			//for each year, month get messages which are not replies themselves
			for (Integer year : years) 	{
				for (Integer month : months) {
					
					t0 = System.currentTimeMillis();
					
					//start from the first thread in date order
					//System.out.print("List "+ list + " is not current ");									//AND messageID < 100000   //AND folder = '" + list + "'
					String sql3 = "SELECT emailMessageID,date2, senderName, subject from allmessages WHERE YEAR(date2) = "+year+" AND MONTH(date2) = "+month+" AND inReplyTo IS NULL order by date2;";  //there will be many rows since there is no seprate table to store this information
					PreparedStatement stmt3 = connection.prepareStatement(sql3);
					ResultSet rs3 = stmt3.executeQuery( sql3 );	
					while (rs3.next()){   
						postDate=rs3.getDate(1);
						subject=rs3.getString(2);
						emailMessageID= rs3.getString(3);
						sender = rs3.getString(4);
						System.out.print(sender+ ", "+postDate + ", "+ subject);
						
						//FOR EACH MESSAGE, note that author, if there are replies to that message, count all those replies to that message
						//and add that count to the author						
						//queryEmailMsgID(connection, emailMessageID, counter);
//						Date replyPostDate;
//						String replySender;
//						String replySubject;
						String sql4 = "SELECT count(emailMessageID) from allmessages WHERE inReplyTo = '"+emailMessageID+"' order by date2;";  //there will be many rows since there is no seprate table to store this information
						PreparedStatement stmt4 = connection.prepareStatement(sql4);
						ResultSet rs4 = stmt4.executeQuery( sql4 );	
						while (rs4.next()){   
//							replyPostDate=rs4.getDate(1);
//							replySubject=rs4.getString(2);
//							emailMessageID= rs4.getString(3);
//							replySender = rs4.getString(4);
							countReplies= rs4.getInt(1);
							System.out.print("# Replies To message: "+countReplies);
							
							//Recursive query for each emailmessageID found 
							queryEmailMsgID(connection, emailMessageID, counter);			
						}
					}
					t1 = System.currentTimeMillis();
					System.out.println("Finished author - Elapsed time =" + (t1 - t0) / 1000 + " minutes");				
				}
			}
			
		}
		catch (SQLException e)	{
			System.out.println( e.getMessage() );
		}
		catch (Exception ex)	{
			System.out.println( ex.getMessage() );
		}
	}

	private static void queryEmailMsgID(Connection connection, String emailMessageID, Integer counter) throws SQLException {
		counter++;
		String str= "";
		for (int i=0; i<counter;i++){
			str = str +"\t";
		}
		
		Date replyPostDate;
		String replySender;
		String replySubject;
		String sql4 = "SELECT date2, subject, emailMessageID, senderName from allmessages WHERE inReplyTo = '"+emailMessageID+"' order by date2;";  //there will be many rows since there is no seprate table to store this information
		PreparedStatement stmt4 = connection.prepareStatement(sql4);
		ResultSet rs4 = stmt4.executeQuery( sql4 );	
		while (rs4.next()){   
			replyPostDate=rs4.getDate(1);
			replySubject=rs4.getString(2);
			emailMessageID= rs4.getString(3);
			replySender = rs4.getString(4);
			System.out.println(str+replySender+ ", " +replyPostDate+ ", " +replySubject +" ");
			
			//Recursive query for each emailmessageID found 
			queryEmailMsgID(connection, emailMessageID, counter);			
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
	
	//second question...different members developers/coredevelopers/bdfl as pep authors and bdfl delegates
	//then by each pepType
	private static void researchQuestionTwo(Integer pepType, Connection connection) {
		Integer pepNumber, numberMessages;
		ArrayList<coredev> coredevelopers = new ArrayList<coredev>();  //ArrayList<String> coredevelopers = new ArrayList<String>();
		ArrayList<String> allmembers = new ArrayList<String>();
		//ArrayList<String> pepEditors = new ArrayList<String>();
		String comma = ",";
		try{	
			String[] pepEditors = {"guido van rossum","nick coghlan","chris angelico","anthony baxter","georg brandl","brett cannon","david goodger",
					"jesse noller","berker peksag","barry warsaw"};	
			
			coredevelopers = rdcm.getallCoreDevelopersWithDateAddedToCoreDevList(connection);	
						
						
			//jan 2002 lets try another way..here we also populate the ppedetailsold table with the authorrole and bdfldelegate role
			String sql0 = "SELECT pep, authorCorrected,bdfl_delegateCorrected,type,created from "+pepDetailTable+";"; // WHERE pep = " + j + " and messageID BETWEEN 500000 AND 510000 order by dateTime;";  //there will be many rows since there is no seprate table to store this information
			int pepCounter=0,BDFLCounter=0, pepEditorCounter=0,coredevCounter=0,devCounter=0, PEPAuthorUnmatchedCounter=0;
			Statement stmt0 = connection.createStatement();
			ResultSet rs0 = stmt0.executeQuery( sql0 );
			//System.out.println("\tcd "+ cd);
			boolean PEPAuthorMatched = false, matched = false, updateRole=false;
			
			//jan 2020..we sum the counts here
			int bdflCounts=0, pepeditor_counts=0, coredev_counts=0, dev_counts=0;
			int total_bdfl_messageCount =0;
			//more peps were added after our first study, we ignore them - we got this by minusing the 'pepdetailsold' table from the pepdetails table 
			//we do this because chapter 5 data is only till march 2017, and the pepdetails latest table has more peps
			int morePEPsAdded[] = {12, 13, 487, 519, 526, 527, 528, 529, 543, 547, 548, 549, 550, 551, 552, 553, 554, 555,
					 556, 557, 558, 559, 560, 561, 562, 563, 564, 565, 566, 567, 568, 569, 570, 571, 572, 573, 574, 575, 
					 576, 577, 578, 579, 580, 581, 582, 801, 3143, 8000, 8001, 8002, 8010, 8011, 8012, 8013, 8014, 8015};
			
			
			//For each PEP, we check, the pep author, and get the message counts
			//May 2020...we dont include this analysis in our thesis, maybe for later research
			undertakeMessageCountAnalysis(connection, coredevelopers, allmembers, pepEditors, pepCounter, BDFLCounter,
					pepEditorCounter, coredevCounter, devCounter, PEPAuthorUnmatchedCounter, rs0, PEPAuthorMatched,
					updateRole, bdflCounts, pepeditor_counts, coredev_counts, dev_counts, total_bdfl_messageCount,
					morePEPsAdded);
			
			//coredevelopers = rdcm.getallCoreDevelopers(connection);	//this we get from pep details table
			//jan 2002 ...we put this function on top
			//coredevelopers = rdcm.getallCoreDevelopersWithDateAddedToCoreDevList(connection);	
			
			System.out.println("\nSecond set of Analysis, based on PEP Authorship and BDFL Delegate\n");
			//System.out.println("total coredevelopers size " + coredevelopers.size());						
			Integer count_coreDevelopersAsAuthorsTotal=0,count_coreDevelopersAsAuthorsS=0,count_coreDevelopersAsAuthorsP=0,count_coreDevelopersAsAuthorsI=0;
			Integer count_coreDevelopersAsBDFLDelegateTotal=0,count_coreDevelopersAsBDFLDelegateS=0,count_coreDevelopersAsBDFLDelegateP=0,count_coreDevelopersAsBDFLDelegateI=0;
			//System.out.println("Pep ID, PEP Type, Pairwise-Transition, Count" );
			System.out.format("%50s%15s%15s%15s%15s","Metric:","Total"+comma,"Process"+comma,"Standard"+comma,"Informational"+comma);
			System.out.println();
			
			//coredeveloper details
			String cd = "";
			Date coredevdateaddedtolist = null;
		
			Integer count_developersAsAuthorsTotal0=0,count_developersAsAuthorsS0=0,count_developersAsAuthorsP0=0,count_developersAsAuthorsI0=0;
			Integer count_developersAsBDFLDelegateTotal0=0,count_developersAsBDFLDelegateS0=0,count_developersAsBDFLDelegateP0=0,count_developersAsBDFLDelegateI0=0;
			
			//Analysis for each core dev
			//get pep author and bdfl delegate before and after he was made core dev
			//the core developers list is from the python website, which has the dates they have been added
			//this is correlated with the bdfl delegate and pep authorship metrics from the pep details table  
			
			//the bdfl delagate and pep authorship before they were made, coredev counts as contributers, 
			// and shoudl be just added to data of the contributors, but no need to relate to each different member, as total number is important  
			
			outerouterloop:
			for (coredev cdev : coredevelopers) {  //for (String cd : coredevelopers) {	
				//we running this script again in 2020, we
				cd = cdev.getCoredev().trim().toLowerCase();
				coredevdateaddedtolist = (Date) cdev.getDateaddedtocoredevlist();
				
				//we simply ignore bdfl and pep editors 
				//Guido van Rossum	..is also part of pep editors
								
				for (String ignore : pepEditors) {
					if(cd.toLowerCase().contains(ignore.toLowerCase()))  //we dont consider these members so we ignore them
						continue outerouterloop;
				}
				
				String sql2 = "SELECT pep, authorCorrected,bdfl_delegateCorrected,type,created from "+pepDetailTable+";"; // WHERE pep = " + j + " and messageID BETWEEN 500000 AND 510000 order by dateTime;";  //there will be many rows since there is no seprate table to store this information
				
				Statement stmt2 = connection.createStatement();
				ResultSet rs2 = stmt2.executeQuery( sql2 );
				//System.out.println("\tcd "+ cd);
				if (getRowCount(rs2)>0) {
					// for each pep 
					outerloop:
					while (rs2.next()){ 
						Integer pepNum =  rs2.getInt(1);		String author =  rs2.getString(2).toLowerCase().trim();		String bdflDelegate =  rs2.getString(3).toLowerCase().trim();		String type =  rs2.getString(4);
						Date pepCreatedDate = rs2.getDate(5);
						
						//more peps were added after our first study, we ignore them - we got this by minusing the 'pepdetailsold' table from the pepdetails table
						for (int x : morePEPsAdded) {  
							if(x == pepNum) {
								continue outerloop;
							}
						}
						
						//dont consider pep editors and core developers
						for(String pe: pepEditors) { //dont consider pep editors
							if(author.contains(pe)) {
								continue outerloop;
							}
						}	
						
						//get the date the pep was created,
						//if pep date creation date is before the date the developer was added to coredev list, we ignore
						//MAIN CHECK - or put it another way, if 'the date the developer was added to coredev list' is after pep creation date, we ignore the pep
						
						//if(coredevdateaddedtolist > pepCreatedDate)
						
						if(coredevdateaddedtolist== null) {					System.out.println("coredevdateaddedtolist== null");						    }
						if(pepCreatedDate== null) {							System.out.println("pep " +pepNum+ " pepCreatedDate== null");					}
						
						// COMPARE CODEVELOPERS AS AUTHORS OR BDFL DELEGATES
						//if member added to core devlist after pepcreation date, meaning at that data they were still as contributor (only developer) 
						if(coredevdateaddedtolist.compareTo(pepCreatedDate) >= 0) {
					      //   System.out.println("For PEP "+ pepNum +" ignored, as Date coredev date added to coredevlist occurs after Date pepCreatedDate, "
					      //   		+ "meaning pep was created when coredev was still a developer");
							
							//jan 2020 ..we do some calculation here
							if (author != null){
								if (author.contains(cd.trim())){
									count_developersAsAuthorsTotal0++;
									if (type.toLowerCase().contains("process")){				count_developersAsAuthorsP0++;	insertPEPAuthorRoleBDFLDelegate_forDebug(pepNum, "process", "allmembers", "allmembers", bdflDelegate, connection);			}	
									else if(type.toLowerCase().contains("standards track")){	count_developersAsAuthorsS0++;	insertPEPAuthorRoleBDFLDelegate_forDebug(pepNum, "standards track", "allmembers", "allmembers", bdflDelegate, connection);	}   
									else if (type.toLowerCase().contains("informational")) {	count_developersAsAuthorsI0++;	insertPEPAuthorRoleBDFLDelegate_forDebug(pepNum, "informational", "allmembers", "allmembers", bdflDelegate, connection);	}
									//DONT NEED TO SHOW THIS
									//System.out.println("\t"+d + " as author for pep "+ pepNum + "|");
								}
							}
							if (bdflDelegate != null){
								if (bdflDelegate.contains(cd.trim())){
									count_developersAsBDFLDelegateTotal0++;
									if (type.toLowerCase().contains("process")){				count_developersAsBDFLDelegateP0++;		insertPEPAuthorRoleBDFLDelegate_forDebug(pepNum, "process", "allmembers", author, "allmembers", connection);		}	
									else if(type.toLowerCase().contains("standards track")){	count_developersAsBDFLDelegateS0++;		insertPEPAuthorRoleBDFLDelegate_forDebug(pepNum, "standards track", "allmembers", author, "allmembers", connection);   }
									else if (type.toLowerCase().contains("informational")) {	count_developersAsBDFLDelegateI0++;		insertPEPAuthorRoleBDFLDelegate_forDebug(pepNum, "informational", "allmembers", author, "allmembers", connection);	 	}
								}
									//DONT NEED TO SHOW THIS
									//System.out.println("\t"+d + " as bdfl delegate for pep "+ pepNum );
							}
							
							
						   continue outerloop; //we go and check next pep -  dont consider that pep for that members he/she may be is coredeveloper at that point
					    } else if(coredevdateaddedtolist.compareTo(pepCreatedDate) < 0) {
					         //System.out.println("Date coredevdateaddedtolist occurs before Date pepCreatedDate");
					    } else if(coredevdateaddedtolist.compareTo(pepCreatedDate) == 0) {
					        // System.out.println("Both dates are equal");  we include these cases as well - we dont ignore
					    }							
						
						
						
						//same applies for developers ..but from there we minus coredevs - 
						//specifically we only the dates when these developers when these dev were not made coredevs
						
						// similar for pep editors..we dont onclude any pep editors in coredev calculation, if we find out when they were added topep editors list
						
						// COMPARE CORE-DEVELOPERS AS AUTHORS OR BDFL DELEGATES
						
						//System.out.println("\tpepNum "+ pepNum + "| author "+author+ " |bdflDelegate "+bdflDelegate);
						if (author != null)
							if (author.contains(cd.trim())){
								count_coreDevelopersAsAuthorsTotal++;
								if (type.toLowerCase().contains("process")){				count_coreDevelopersAsAuthorsP++;		insertPEPAuthorRoleBDFLDelegate_forDebug(pepNum, "process", "coredevs", "coredevs", bdflDelegate, connection);		}	
								else if(type.toLowerCase().contains("standards track")){	count_coreDevelopersAsAuthorsS++;		insertPEPAuthorRoleBDFLDelegate_forDebug(pepNum, "standards track", "coredevs", "coredevs", bdflDelegate, connection);		} 
								else if (type.toLowerCase().contains("informational")) {	count_coreDevelopersAsAuthorsI++;		insertPEPAuthorRoleBDFLDelegate_forDebug(pepNum, "informational", "coredevs", "coredevs", bdflDelegate, connection);		}	
								//DONT NEED TO SHOW THIS
								//System.out.println("\t"+cd + " as author for pep "+ pepNum + "|");
							}
						if (bdflDelegate != null)
							if (bdflDelegate.contains(cd.trim())){
								count_coreDevelopersAsBDFLDelegateTotal++;
								if (type.toLowerCase().contains("process")){				count_coreDevelopersAsBDFLDelegateP++;	insertPEPAuthorRoleBDFLDelegate_forDebug(pepNum, "process", "coredevs", author, "coredevs", connection);			}	
								else if(type.toLowerCase().contains("standards track")){	count_coreDevelopersAsBDFLDelegateS++;	insertPEPAuthorRoleBDFLDelegate_forDebug(pepNum, "standards track", "coredevs", author, "coredevs", connection);			}	
								else if (type.toLowerCase().contains("informational")) {	count_coreDevelopersAsBDFLDelegateI++;	insertPEPAuthorRoleBDFLDelegate_forDebug(pepNum, "informational", "coredevs", author, "coredevs", connection);			}	
								//DONT NEED TO SHOW THIS
								//System.out.println("\t"+cd + " as bdfl delegate for pep "+ pepNum );
							}
					}					  
				}				
			}
			
			
			//System.out.println("Count Of coreDevelopersAsAuthors: " +count_coreDevelopersAsAuthorsTotal);
			System.out.println("Data from Core-dev Analysis : \nContributors minused \nNOTE WE HAVE TO ADD THIS DATA TO CONTRIBUTERS");
			System.out.println("For Contributors minused");
			System.out.format("%50s%15s%15s%15s%15s","Contributors As Authors0:",count_developersAsAuthorsTotal0+comma,count_developersAsAuthorsP0+comma,count_developersAsAuthorsS0+comma,count_developersAsAuthorsI0+comma);
			System.out.println();
			System.out.format("%50s%15s%15s%15s%15s","Contributors As BDFLDelegates0",count_developersAsBDFLDelegateTotal0+comma,count_developersAsBDFLDelegateP0+comma,count_developersAsBDFLDelegateS0+comma,count_developersAsBDFLDelegateI0+comma);			
			System.out.println("\n\n");
			
			
			System.out.format("%50s%15s%15s%15s%15s","Core Developers As Authors:",count_coreDevelopersAsAuthorsTotal+comma,count_coreDevelopersAsAuthorsP+comma,count_coreDevelopersAsAuthorsS+comma,count_coreDevelopersAsAuthorsI+comma);
			System.out.println();
			System.out.format("%50s%15s%15s%15s%15s","Core Developers As BDFL Delegates",count_coreDevelopersAsBDFLDelegateTotal+comma,count_coreDevelopersAsBDFLDelegateP+comma,count_coreDevelopersAsBDFLDelegateS+comma,count_coreDevelopersAsBDFLDelegateI+comma);			
			System.out.println();
			//System.out.println("Count Of coreDevelopersAsBDFLDelegate: " + count_coreDevelopersAsBDFLDelegateTotal);
			
			//contributors. 
			//now we carry out the analysis of contributors, as not all would be promoted to core devs..some will be left, so we analyse them
			//but in this analysis we omit the bdfl, pep editors and core devs (we we already analyses above)
			
			allmembers = rdcm.getallMembers(connection);	//this we get from pep details table
			//developers = rdcm.getallDevelopers(connection);	//this we get from pep details table			
			System.out.println("\nCONTRIBUTORS ONLY ANALYSIS: Total Contributors size " + allmembers.size());						
			//Integer count_developersAsAuthors=0,count_developersAsBDFLDelegate=0;
			Integer count_developersAsAuthorsTotal=0,count_developersAsAuthorsS=0,count_developersAsAuthorsP=0,count_developersAsAuthorsI=0;
			Integer count_developersAsBDFLDelegateTotal=0,count_developersAsBDFLDelegateS=0,count_developersAsBDFLDelegateP=0,count_developersAsBDFLDelegateI=0;
			
			//System.out.println("Pep ID, PEP Type, Pairwise-Transition, Count" );
			//Now we run analysis for each developer
			outerouterloop2:
			for (String d : allmembers) { 
				d = d.trim().toLowerCase();
				String sql3 = "SELECT pep, authorCorrected,bdfl_delegateCorrected,type,created from "+pepDetailTable+";"; // WHERE pep = " + j + " and messageID BETWEEN 500000 AND 510000 order by dateTime;";  //there will be many rows since there is no seprate table to store this information
				Statement stmt3 = connection.createStatement();
				ResultSet rs3 = stmt3.executeQuery( sql3 );
				//System.out.println("developers "+d);
				if (getRowCount(rs3)>0 && d != null && d.length() >3) {
					
					//we simply ignore bdfl and pep editors 
					//Guido van Rossum	..is also part of pep editors				
					
					for (String ignore : pepEditors) { //note bdfl is also part of pep editors
						if(d.toLowerCase().contains(ignore.toLowerCase()))  //we dont consider these members so we ignore them
							continue outerouterloop2;
					}
					
					//for each pep	
					outerloop2:
					while (rs3.next()){
						Integer pepNum =  rs3.getInt(1);	String author =  rs3.getString(2).toLowerCase().trim();		String bdflDelegate =  rs3.getString(3).toLowerCase().trim();
						String type =  rs3.getString(4);	Date pepCreatedDate = rs3.getDate(5);
						
						//more peps were added after our first study, we ignore them - we got this by minusing the 'pepdetailsold' table from the pepdetails table
						for (int x : morePEPsAdded) {  
							if(x == pepNum) {
								continue outerloop2;
							}
						}
						
						//dont consider pep editors and core developers
						for(String pe: pepEditors) { //dont consider pep editors
							if(author.contains(pe)) {
								continue outerloop2;
							}
						}						
						
						//we remove all core developers who were made core developers before the pep was created - meaning they were already core devs, not developers at that time				
						//check if developer is part of coredevelopers, if so check when
						for (coredev cdev : coredevelopers) {  //for (String cd : coredevelopers) {	
							//we running this script again in 2020, we
							cd = cdev.getCoredev().trim().toLowerCase();
							//if if current developer is also part of coredev group, then we check when was he/she made
							
							//just checking without dates
							//if(author.contains(cd)) {
							//	continue outerloop2;
							//}
							
							//check if developer is part of coredevelopers,
							if(d.toLowerCase().equals(cd.toLowerCase())) {							
								coredevdateaddedtolist = (Date) cdev.getDateaddedtocoredevlist();
								// if so check when												
								if(coredevdateaddedtolist.compareTo(pepCreatedDate) >= 0) {  //opposite of above, if made coredev b4 pep creation date we ignore
							      //   System.out.println("For PEP "+ pepNum +" ignored, as Date coredev date added to coredevlist occurs after Date pepCreatedDate, "
							      //   		+ "meaning pep was created when coredev was still a developer");
								   continue outerloop2; //we go to check next pep -  we dont copsider that member as developer at that point - as he/she is part of coredeveloper
							    } else if(coredevdateaddedtolist.compareTo(pepCreatedDate) < 0) {
							         //System.out.println("Date coredevdateaddedtolist occurs before Date pepCreatedDate");
							    } else if(coredevdateaddedtolist.compareTo(pepCreatedDate) == 0) {
							        // System.out.println("Both dates are equal");  we include these cases as well - we dont ignore
							    }
							}
							
						}
						
						//actual totals done here
						
						if (author != null)
							if (author.contains(d)){
								count_developersAsAuthorsTotal++;															 // insertPEPAuthorRoleBDFLDelegate_forDebug(int pep, String peptype, String rolebeingcheckedfor, String author, String delegate, Connection connection)
								if (type.toLowerCase().contains("process")){				count_developersAsAuthorsP++;	insertPEPAuthorRoleBDFLDelegate_forDebug(pepNum, "process", "allmembers", "allmembers", bdflDelegate, connection);			}	
								else if(type.toLowerCase().contains("standards track")){	count_developersAsAuthorsS++;	insertPEPAuthorRoleBDFLDelegate_forDebug(pepNum, "standards track", "allmembers", "allmembers", bdflDelegate, connection);			}   
								else if (type.toLowerCase().contains("informational")) {	count_developersAsAuthorsI++;	insertPEPAuthorRoleBDFLDelegate_forDebug(pepNum, "informational", "allmembers", "allmembers", bdflDelegate, connection);			}
								//DONT NEED TO SHOW THIS
								//System.out.println("\t"+d + " as author for pep "+ pepNum + "|");
							}
						if (bdflDelegate != null)
							if (bdflDelegate.contains(d)){
								count_developersAsBDFLDelegateTotal++;															 // insertPEPAuthorRoleBDFLDelegate_forDebug(int pep, String peptype, String rolebeingcheckedfor, String author, String delegate, Connection connection)
								if (type.toLowerCase().contains("process")){				count_developersAsBDFLDelegateP++;		insertPEPAuthorRoleBDFLDelegate_forDebug(pepNum, "process", "allmembers", author, "allmembers", connection);		}	
								else if(type.toLowerCase().contains("standards track")){	count_developersAsBDFLDelegateS++;		insertPEPAuthorRoleBDFLDelegate_forDebug(pepNum, "standards track", "allmembers", author, "allmembers", connection);		}	
								else if (type.toLowerCase().contains("informational")) {	count_developersAsBDFLDelegateI++;		insertPEPAuthorRoleBDFLDelegate_forDebug(pepNum, "informational", "allmembers", author, "allmembers", connection);	    }	
								//DONT NEED TO SHOW THIS
								//System.out.println("\t"+d + " as bdfl delegate for pep "+ pepNum );
							}
					}
				}
				
			}
			//System.out.println("Count Of DevelopersAsAuthors: " + count_developersAsAuthors);
			//System.out.println("Count Of DevelopersAsBDFLDelegate: " + count_developersAsBDFLDelegate);
//			System.out.format("%30s%15s%15s%15s%15s","Metric:","Total"+comma,"Process"+comma,"Standard"+comma,"Informational"+comma);
//			System.out.println();
			System.out.format("%50s%15s%15s%15s%15s","developersAsAuthors:",count_developersAsAuthorsTotal+comma,count_developersAsAuthorsP+comma,count_developersAsAuthorsS+comma,count_developersAsAuthorsI+comma);
			System.out.println();
			System.out.format("%50s%15s%15s%15s%15s","developersAsBDFLDelegates",count_developersAsBDFLDelegateTotal+comma,count_developersAsBDFLDelegateP+comma,count_developersAsBDFLDelegateS+comma,count_developersAsBDFLDelegateI+comma);			
			System.out.println();
			
			//pep editors
			//String[] pepEditors = {"guido.van.rossum","nick.cogan","chris.angelico","anthony.baxter","georg.brandl","brett.cannon","david.goodger",
			//		"jesse.noller","berker.peksag","barry.warsaw"};			
			
					
					
			System.out.println("\n\nTotal pepeditors size " + pepEditors.length);				
			//Integer count_pepEditorsAsAuthors=0,count_pepEditorsAsBDFLDelegate=0;
			//System.out.println("Pep ID, PEP Type, Pairwise-Transition, Count" );
			Integer count_pepEditorsAsAuthorsTotal=0,count_pepEditorsAsAuthorsS=0,count_pepEditorsAsAuthorsP=0,count_pepEditorsAsAuthorsI=0;
			Integer count_pepEditorsAsBDFLDelegateTotal=0,count_pepEditorsAsBDFLDelegateS=0,count_pepEditorsAsBDFLDelegateP=0,count_pepEditorsAsBDFLDelegateI=0;
			
			for (String pe : pepEditors) {	
				//jan 2020, we split the pep editors on dots			//pe = pe.replace("\\.", " "); ...not needed as we cleaned the editors list of the dots 
				//System.out.println("Pep Editor: " + pe);
				String sql4 = "SELECT pep, authorCorrected,bdfl_delegateCorrected,type from "+pepDetailTable+";"; // WHERE pep = " + j + " and messageID BETWEEN 500000 AND 510000 order by dateTime;";  //there will be many rows since there is no seprate table to store this information
				Statement stmt4 = connection.createStatement();
				ResultSet rs4 = stmt4.executeQuery( sql4 );

				if (getRowCount(rs4)>0) {
					outerloop3:
					while (rs4.next()){    
						Integer pepNum =  rs4.getInt(1); 	String author =  rs4.getString(2).toLowerCase().trim();	
						String bdflDelegate =  rs4.getString(3).toLowerCase().trim();		String type =  rs4.getString(4);
						//System.out.println("\t\t\t\t\t added state here " + state +" for j "+ j);
						
						//more peps were added after our first study, we ignore them - we got this by minusing the 'pepdetailsold' table from the pepdetails table
						for (int x : morePEPsAdded) {  
							if(x == pepNum) {
								continue outerloop3;
							}
						}						
						
						if(author.contains("guido van rossum")) {
							continue outerloop3;
						}
						
						if (author != null) {
							author = author.toLowerCase();
							if (author.contains(pe)){
								count_pepEditorsAsAuthorsTotal++;														 // insertPEPAuthorRoleBDFLDelegate_forDebug(int pep, String peptype, String rolebeingcheckedfor, String author, String delegate, Connection connection)
								if (type.toLowerCase().contains("process")){ 				count_pepEditorsAsAuthorsP++;	insertPEPAuthorRoleBDFLDelegate_forDebug(pepNum, "standards track", "pepEditors", "pepEditors", bdflDelegate, connection);								}	
								else if(type.toLowerCase().contains("standards track")){ 	count_pepEditorsAsAuthorsS++;	insertPEPAuthorRoleBDFLDelegate_forDebug(pepNum, "standards track", "pepEditors", "pepEditors", bdflDelegate, connection);								}   
								else if (type.toLowerCase().contains("informational")) {	count_pepEditorsAsAuthorsI++;	insertPEPAuthorRoleBDFLDelegate_forDebug(pepNum, "standards track", "pepEditors", "pepEditors", bdflDelegate, connection);								}
								//DONT NEED TO SHOW THIS
								//System.out.println("\t"+pe+ " as author for pep "+ pepNum + "|");
							}
						}
						if (bdflDelegate != null) {
							bdflDelegate = bdflDelegate.toLowerCase();
							if (bdflDelegate.contains(pe)){
								count_pepEditorsAsBDFLDelegateTotal++;													       //insertPEPAuthorRoleBDFLDelegate_forDebug(int pep, String peptype, String rolebeingcheckedfor, String author, String delegate, Connection connection)
								if (type.toLowerCase().contains("process")){				count_pepEditorsAsBDFLDelegateP++;	insertPEPAuthorRoleBDFLDelegate_forDebug(pepNum, "standards track", "pepEditors", author, "pepEditors", connection);								}	
								else if(type.toLowerCase().contains("standards track")){	count_pepEditorsAsBDFLDelegateS++;	insertPEPAuthorRoleBDFLDelegate_forDebug(pepNum, "standards track", "pepEditors", author, "pepEditors", connection);								}	
								else if (type.toLowerCase().contains("informational")) {	count_pepEditorsAsBDFLDelegateI++;	insertPEPAuthorRoleBDFLDelegate_forDebug(pepNum, "standards track", "pepEditors", author, "pepEditors", connection);								}	
								//DONT NEED TO SHOW THIS
								//System.out.println("\t"+pe + " as bdfl delegate for pep "+ pepNum );
							}
						}
					}					  
				}				
			}
			
			//System.out.println("Count Of pepEditorsAsAuthors: " + count_pepEditorsAsAuthors);
			//System.out.println("Count Of pepEditorsAsBDFLDelegate: " + count_pepEditorsAsBDFLDelegate);
//			System.out.format("%30s%15s%15s%15s%15s","Metric:","Total"+comma,"Process"+comma,"Standard"+comma,"Informational"+comma);
//			System.out.println();
			System.out.format("%50s%15s%15s%15s%15s","pepEditorsAsAuthors:",count_pepEditorsAsAuthorsTotal+comma,count_pepEditorsAsAuthorsP+comma,count_pepEditorsAsAuthorsS+comma,count_pepEditorsAsAuthorsI+comma);
			System.out.println();
			System.out.format("%50s%15s%15s%15s%15s","pepEditorsAsBDFLDelegates",count_pepEditorsAsBDFLDelegateTotal+comma,count_pepEditorsAsBDFLDelegateP+comma,count_pepEditorsAsBDFLDelegateS+comma,count_pepEditorsAsBDFLDelegateI+comma);			
			System.out.println();
			
			//BdFL
			String bdfl = "guido van rossum";	//this we get from pep details table	
			System.out.println("\n\nBDFL size " + pepEditors.length);
			//Integer count_BDFLAsAuthors=0,count_BDFLAsBDFLDelegate=0;
			Integer count_BDFLAsAuthorsTotal=0,count_BDFLAsAuthorsS=0,count_BDFLAsAuthorsP=0,count_BDFLAsAuthorsI=0;
			Integer count_BDFLAsBDFLDelegateTotal=0,count_BDFLAsBDFLDelegateS=0,count_BDFLAsBDFLDelegateP=0,count_BDFLAsBDFLDelegateI=0;			
			
			String sql5 = "SELECT pep, authorCorrected,bdfl_delegateCorrected,type from "+pepDetailTable+";"; // WHERE pep = " + j + " and messageID BETWEEN 500000 AND 510000 order by dateTime;";  //there will be many rows since there is no seprate table to store this information			
			Statement stmt5 = connection.createStatement();	
			ResultSet rs5 = stmt5.executeQuery( sql5 );
			
			
			//For each PEP, in the pepdetails table we check the bdfl delegate and pep author 
			if (getRowCount(rs5)>0) {
				outerloop4:
				while (rs5.next()){     
					Integer pepNum =  rs5.getInt(1); String author =  rs5.getString(2).toLowerCase().trim();		
					String bdflDelegate =  rs5.getString(3).toLowerCase().trim();		String type = rs5.getString(4);
					//System.out.println("\t\t\t\t\t added state here " + state +" for j "+ j);
					
					//more peps were added after our first study, we ignore them - we got this by minusing the 'pepdetailsold' table from the pepdetails table
					for (int x : morePEPsAdded) {  
						if(x == pepNum) {
							continue outerloop4;
						}
					}
					//PEPs in which the BDFL is author 
					if (author != null){
						author = author.toLowerCase();
						if (author.contains(bdfl)){
							count_BDFLAsAuthorsTotal++;															 // insertPEPAuthorRoleBDFLDelegate_forDebug(int pep, String peptype, String rolebeingcheckedfor, String author, String delegate, Connection connection)
							if (type.toLowerCase().contains("process")){				count_BDFLAsAuthorsP++;		insertPEPAuthorRoleBDFLDelegate_forDebug(pepNum, "process", "BDFL", "BDFL", bdflDelegate, connection);					}	
							else if(type.toLowerCase().contains("standards track")){	count_BDFLAsAuthorsS++;		insertPEPAuthorRoleBDFLDelegate_forDebug(pepNum, "standards track", "BDFL", "BDFL", bdflDelegate, connection);					}	
							else if (type.toLowerCase().contains("informational")) {	count_BDFLAsAuthorsI++;		insertPEPAuthorRoleBDFLDelegate_forDebug(pepNum, "informational", "BDFL", "BDFL", bdflDelegate, connection);					}
						}
					}
					//PEPs in which the BDFL is delegate - woudl be different from above set 
					if (bdflDelegate != null){
						bdflDelegate = bdflDelegate.toLowerCase();
						if (bdflDelegate.contains(bdfl)){
							count_BDFLAsBDFLDelegateTotal++;
							if (type.toLowerCase().contains("process")){				count_BDFLAsBDFLDelegateP++;	insertPEPAuthorRoleBDFLDelegate_forDebug(pepNum, "process", "BDFL", author, "BDFL", connection);							}	
							else if(type.toLowerCase().contains("standards track")){	count_BDFLAsBDFLDelegateS++;	insertPEPAuthorRoleBDFLDelegate_forDebug(pepNum, "standards track", "BDFL", author, "BDFL", connection);							}	
							else if (type.toLowerCase().contains("informational")) {	count_BDFLAsBDFLDelegateI++;	insertPEPAuthorRoleBDFLDelegate_forDebug(pepNum, "informational", "BDFL", author, "BDFL", connection);							}	
						}
					}
				}					  
			}				
			
			//System.out.println("count_BDFLAsAuthors: " + count_BDFLAsAuthors);
			//System.out.println("count_BDFLAsBDFLDelegate: " + count_BDFLAsBDFLDelegate);
//			System.out.format("%30s%15s%15s%15s%15s","Metric:","Total"+comma,"Process"+comma,"Standard"+comma,"Informational"+comma);
//			System.out.println();
			System.out.format("%50s%15s%15s%15s%15s","BDFLAsAuthors:",count_BDFLAsAuthorsTotal+comma,count_BDFLAsAuthorsP+comma,count_BDFLAsAuthorsS+comma,count_BDFLAsAuthorsI+comma);
			System.out.println();
			System.out.format("%50s%15s%15s%15s%15s","BDFLAsBDFLDelegates",count_BDFLAsBDFLDelegateTotal+comma,count_BDFLAsBDFLDelegateP+comma,count_BDFLAsBDFLDelegateS+comma,count_BDFLAsBDFLDelegateI+comma);			
			System.out.println();
			
		}
		catch (SQLException e) {
			System.out.println( e.getMessage() );
		} catch (Exception ex)	{
			System.out.println( ex.getMessage() );
		}
	}

	/**
	 * @param connection
	 * @param coredevelopers
	 * @param developers
	 * @param pepEditors
	 * @param pepCounter
	 * @param BDFLCounter
	 * @param pepEditorCounter
	 * @param coredevCounter
	 * @param devCounter
	 * @param PEPAuthorUnmatchedCounter
	 * @param rs0
	 * @param PEPAuthorMatched
	 * @param updateRole
	 * @param bdflCounts
	 * @param pepeditor_counts
	 * @param coredev_counts
	 * @param dev_counts
	 * @param total_bdfl_messageCount
	 * @param morePEPsAdded
	 * @throws SQLException
	 */
	public static void undertakeMessageCountAnalysis(Connection connection, ArrayList<coredev> coredevelopers,
			ArrayList<String> developers, String[] pepEditors, int pepCounter, int BDFLCounter, int pepEditorCounter,
			int coredevCounter, int devCounter, int PEPAuthorUnmatchedCounter, ResultSet rs0, boolean PEPAuthorMatched,
			boolean updateRole, int bdflCounts, int pepeditor_counts, int coredev_counts, int dev_counts,
			int total_bdfl_messageCount, int[] morePEPsAdded) throws SQLException {
		boolean matched;
		boolean undertakeMessageCountAnalysis = false;
		if(undertakeMessageCountAnalysis) {
			
			thatLoop:
			while (rs0.next()){ //for each pep								
				Integer pepNum =  rs0.getInt(1);		String author =  rs0.getString(2).toLowerCase().trim();		String bdflDelegate =  rs0.getString(3).toLowerCase().trim();		
				String type =  rs0.getString(4);		Date pepCreatedDate = rs0.getDate(5);
						
				//more peps were added after our first study, we ignore them - we got this by minusing the 'pepdetailsold' table from the pepdetails table
				for (int x : morePEPsAdded) {  
					if(x == pepNum) {
						continue thatLoop;
					}
				}
				pepCounter++;	
				
				PEPAuthorMatched = false;
				matched = false;
				
				//debug
//				if(!author.contains("maupin"))
//					continue;
				
				if(author.contains("guido van rossum")) {
					BDFLCounter++; 	
					//get all messages for the peps					
					bdflCounts = bdflCounts + rdcm.getallMessageForPEP(connection, pepNum);
					//get count for bdfl messages only
					total_bdfl_messageCount = total_bdfl_messageCount + rdcm.getAllMembersPerRole_MessageForPEP(connection, pepNum);
					
					PEPAuthorMatched=true; 		System.out.println("\t0 (bdfl) PEP Author Matched, PEP " + pepNum + " " +author);
					if(updateRole) {
						updatePEPAuthorRole(pepNum,"BDFL", connection);
						//System.out.println("\t0 here a");
					}
				}
				
				outerLoop0:
				for(String pe: pepEditors) {
					if(author.contains("guido van rossum")) {
						continue outerLoop0;
					}
					
					if(author.contains(pe)) {
						pepEditorCounter++;  pepeditor_counts = pepeditor_counts + rdcm.getallMessageForPEP(connection, pepNum);
						if (PEPAuthorMatched)
							System.out.println("x PEP Author Already Matched " + pepNum + " " +author + " PEP Editor: " + pe);
						PEPAuthorMatched=true; 		System.out.println("\t1 (pep editor) PEP Author Matched " + pepNum + " " +author + " PE: "+ pe);
						if(updateRole) 
							updatePEPAuthorRole(pepNum,"PEP Editor",connection);
					}
				}
				String cd =""; //Date coredevdateaddedtolist=null;
				
				outerLoop:
				for (coredev cdev : coredevelopers) {  //for (String cd : coredevelopers) {	
					//we running this script again in 2020, we
					cd = cdev.getCoredev().trim().toLowerCase();
					for(String pe: pepEditors) {
						if(author.contains(pe)) {
							continue outerLoop;
						}
					}
					if(author.contains(cd)) {
						coredevCounter++;	coredev_counts = coredev_counts + rdcm.getallMessageForPEP(connection, pepNum);
						if (PEPAuthorMatched)
							System.out.println("y PEP Author Already Matched " + pepNum + " " +author + " CoreDev: "+ cd);
						PEPAuthorMatched=true;		System.out.println("\t2 (coredev) PEP Author Matched " + pepNum + " " +author + " CoreDev: "+ cd);
						if(updateRole) 
							updatePEPAuthorRole(pepNum,"Core Developer",connection);
					}
				
				}
//				System.out.println("PEPAuthorMatched = " + PEPAuthorMatched);
				outerLoop2:					
				for (String d : developers) { //for each developer
					d = d.trim().toLowerCase();
					
					//sometimes the developer clusters are just one char which will match the previous developer just checked, so we have a check
					if(d.length() < 3)
						continue;
					
					for(String pe: pepEditors) { //dont consider pep editors
						if(author.contains(pe)) {
							continue outerLoop2;
						}
					}
					for (coredev cdev : coredevelopers) {  //dont consider coredevelopers
						//we running this script again in 2020, we
						cd = cdev.getCoredev().trim().toLowerCase();
						if(author.contains(cd)) {
							continue outerLoop2;
						}
					}
					
					if(author.contains(d)) {
						devCounter++;		dev_counts = dev_counts + rdcm.getallMessageForPEP(connection, pepNum);
						if (PEPAuthorMatched)
							System.out.println("z PEP Author Already Matched " + pepNum + " " +author + " developer: " + d);
						PEPAuthorMatched=true;  
//						System.out.println("0 (dev) PEP Author Matched " + pepNum + " " +author + " d " + d);
						if(updateRole) 
							updatePEPAuthorRole(pepNum,"Developer",connection);
					}
				}
				
				if(!PEPAuthorMatched) {
					PEPAuthorUnmatchedCounter++;
					System.out.println("PEP Author Unmatched " + pepNum + " " +author);
					if(updateRole) 
						updatePEPAuthorRole(pepNum,"unmatched",connection);
				}
				
			}
			System.out.println("\nFirst set of Analysis, based on PEP Messages -- we dont consider in tyhe theis..maybe for future research");
			System.out.println(" \tPEP counter = " + pepCounter 
					+ "\n\t BDFL As PEP Authors Counter: "+ BDFLCounter + ",  BDFL_counts: " +bdflCounts + " , "
					+ "\n\t PEP Editors As PEP Authors Counter: "+ pepEditorCounter + ",  pepEditor_counts: " + pepeditor_counts + " , "
					+ "\n\t Coredev As PEP Authors Counter: " +coredevCounter + " ,  coredev_counts: " + coredev_counts + " , "
					+ "\n\t Contributors As PEP Authors Counter: " + devCounter + " , dev_counts: " + dev_counts + " , "
					+ "\n\t PEP Author Unmatched Counter "+ PEPAuthorUnmatchedCounter);
			System.out.println("");
		}
	}	
	
	public static void insertPEPAuthorRoleBDFLDelegate_forDebug(int pep, String peptype, String rolebeingcheckedfor, String author, String delegate, Connection connection) {
		// create the java mysql update preparedstatement
	      String query = "insert into " + debugTable +" (pep, peptype, rolecheckingfor, author,delegate) VALUES (?,?,?,?,?) ";
	      
	      try {
	    	  PreparedStatement preparedStmt = connection.prepareStatement(query);
	    	  preparedStmt.setInt(1, pep); 	   			  preparedStmt.setString(2, peptype); 	    	
	    	  preparedStmt.setString(3, rolebeingcheckedfor);	    	  preparedStmt.setString(4, author);			
	    	  preparedStmt.setString(5, delegate);
		      // execute the java preparedstatement
		      preparedStmt.executeUpdate();
	      }
		 catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		  }
	      
	      Boolean updateRole = true;
	      
	      if(updateRole) {
				updatePEPAuthorRole(pep,delegate, connection);
				//System.out.println("\t0 here a");
			}
	      
	      
	}
	
	public static void updatePEPAuthorRole(int pep, String authorRole, Connection connection) {
		// create the java mysql update preparedstatement
	      String query = "update " + pepDetailTable +" set authorrole = ? where pep = ?";
	      
	      try {
	    	  PreparedStatement preparedStmt = connection.prepareStatement(query);
	    	  preparedStmt.setString  (1, authorRole);
	    	  preparedStmt.setInt(2, pep);
		      // execute the java preparedstatement
		      preparedStmt.executeUpdate();
	      }
		 catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
				query = "SELECT pep, type FROM "+pepDetailTable+" order by pep asc";
			else	    	  
				query = "SELECT pep, type FROM "+pepDetailTable+" where type like '%" + pepTypeString + "%' order by pep asc";
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
				String sql2 = "SELECT python_version from "+pepDetailTable+" WHERE pep = " + pepNumber + ";";  //there will be many rows since there is no seprate table to store this information
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
}
