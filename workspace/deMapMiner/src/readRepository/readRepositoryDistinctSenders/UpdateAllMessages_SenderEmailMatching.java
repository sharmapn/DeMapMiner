package readRepository.readRepositoryDistinctSenders;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import GeneralQueries.GetMessageStatisticsFunctions;
import connections.MysqlConnectForQueries;
import readRepository.readRepository.MessageReadingUtils;
import connections.MysqlConnect;
import miner.process.PythonSpecificMessageProcessing;
import utilities.ReturnNattyTimeStamp;
//nov 2018, main script for clustering sendernames..so start here
//This code gets the email adddress and senderfullname and stores it
// This is the last script for clustering for reflecting teh changes back to the allmessages table 
public class UpdateAllMessages_SenderEmailMatching {
	//query each message
	//compute SENDERNAME - should be in format firstname.lastname or firstname.midlename.lastname - all in lowercase
	//store in same message
	
	static MessageReadingUtils mru = new MessageReadingUtils();
	static PythonSpecificMessageProcessing pms = new PythonSpecificMessageProcessing();	
	static GetMessageStatisticsFunctions f = new GetMessageStatisticsFunctions();	
	static MysqlConnect mc = new MysqlConnect(); //static MysqlConnectForQueries mc = new MysqlConnectForQueries();
	static Utilities u = new Utilities();
	static LevenshteinDistance ld = new LevenshteinDistance();
	static ClusterSameSendersAutomatic cs = new ClusterSameSendersAutomatic();
	
	static String updateQuery = null;
	static int i;
	
	public static void main(String args[])
	{	
		//FIRST ONE - first part set email address..//this is run first to set the email and sendername -- run this before running thsi function
		//it sets these fields ..[allmessages set senderEmail = ?, senderFullName= ?, senderFirstName=?, senderLastName=?, senderEmailFirstSegment=?] 
		// update allmessages set senderFullName = NULL; AND // update allmessages set senderemail = NULL;
		//run this few times as query is limited to 400000 rows
		//SOME ERROR ROWS...HAVE TO CHECK SO DOING ONLY WHERE EMAILA ND NAME < 100 and 200 rows respectively
//		updateAllMessagesTableExtractEmailSenderFullName();
		
		//SECOND PART - once the above is done we can insert the same and similar fullnames from allmessages table -- run this sql
//		insert into distinctsenders (emailaddress) select distinct(senderemail) from  allmessages; //create table distinctsenders(id int, emailaddress tinytext, senderName tinytext );
				
		//THIRD PART populate the tables - tables for allmembers and developers are different And truncate them first , eg truncate table distinctsenders/distinctdevsenders
		//Note this will take loong..maybe half hour 11.21 start
		populateDistinctSendersTableUsingEmailAddress();		
	
		//before running, run SQL script to not inlude some email addresses in computation as they have many senders for each email - see all..sql 
		// when selecting for ld matching dont select report@bugs.python.org, python-checkins@python.org, noreply@sourceforge.net,, python-dev@python.org, python@mrabarnett.plus.com
		// buildbot@python.org is just one user using it
		
		//MAIN PART 
		//CLUSTERING - for each email/record in allmessages, check which cluster it belongs using email, update teh cluster, 
		//then later update the senderfullname to the longest sendername 
		//these two can be combined into the first one, but just keeping them seprate to see results of teh second one - maybe second one includes unwanted rows as well
//		cs.AutomaticallyClusterUsingLevenshteinDistance();
//		cs.ClusterBySenderName();
		//to check if the sendername clustering is done
		//SELECT * FROM distinctsenders WHERE clusterbysendername IS NULL AND senderfirstname IS NULL AND senderlastname IS NULL AND clusteredbysendername <> 2;
		
		//FINAL PART Reflect the clusters in distinctsender table to allmessages table. 
		//Basically assign all records/all senders in allmessages table to clusters		
//$$	summaryDistinctClusters(); 
		//*** RUN THESE CODE BEFORE REFLECTING
		//UPDATE ALLMESsAGES set clusterBySenderFullName = NULL where clusterBySenderFullName IS NOT NULL;
		//update distinctsenders set reflected =NULL;
//$$	reflectClustersToMainTableUsingResults();
		System.out.println("Processing Complete :");
		
		//last part...update to cater for the new column  'inReplyToUserUsingClusteredSender'
		//UPDATE allmessages a1
		//JOIN allmessages a2 ON a2.emailMessageID = a1.inReplyTo
		//SET a1.inReplyToUserUsingClusteredSender = a2.clusterBySenderFullName
		//WHERE a1.inReplyTo IS NOT NULL
		
		//FOR ANALYSIS, you have to recreate teh alldevelopers table
		//create table allDevelopers
		//AS select DISTINCT(clusterBySenderFullName) from allmessages
		//where folder = 'C:\\datasets\\python-dev' and pep != -1; 
	}
	
	private static void summaryDistinctClusters() {
		 MysqlConnectForQueries mc = new MysqlConnectForQueries();
		 Connection conn = mc.connect();
			Integer id=0;
			String sql0 = "select count(DISTINCT(cluster)) from distinctsenders WHERE clustered IS NOT NULL;";  //there will be many rows since there is no seprate table to store this information
			Statement stmt0;
			try {
				stmt0 = conn.createStatement();
				ResultSet rs0 = stmt0.executeQuery( sql0 );			
				if (rs0.next())	{				
					id =rs0.getInt(1);	
				}
				if (id==null)
					id=0;
				System.out.println("Distinct Clusters :"+ id);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
		
	
	//this is run second to get senderfullnames for an email address 
	// this could have been run as part of sql but the amout of disting authors makes the group by funtion take too long so have to do it with java
	//
	private static void populateDistinctSendersTableUsingEmailAddress() {	
		Integer mid = null;
		String name = null, firstName=null, lastName=null, emailAddress=null, senderEmailFirstSegment=null;
		boolean first=false, second =false;
		
		ArrayList<String> distinctEmails = new ArrayList<String>();			
		try	{
			Connection conn = mc.connect();	
			
			//f.totalNumberOfMessages(conn);		//f.totalNumberOfUniqueMessages(conn);
			distinctEmails = u.returnAllDistinctEmailAddresses(conn,"distinctSenders");	//distinctDevSenders
			Integer id=0;
			//we will also give a id for each based on the max id in current table 
			//so make sure the ids are null before you start
			id = getMaxIDOrNULL(conn, id, "distinctSenders");	//distinctDevSenders
				//assign distinct id for records
		
			for (String email: distinctEmails)			{	
//				emailAddress = email;																								//this one added for all developers only	//and folder = 'C:\\datasets\\python-dev'						/ and senderFullName IS NOT NULL
				String sql1 = "SELECT senderFullName,senderfirstname, senderlastname,senderEmailFirstSegment, count(distinct(messageid)) "
						+ "from allmessages where senderEmail = '"+email+"' ";
						// + "group by senderFullName "
						//		+ "ORDER BY len desc;";  //there will be many rows since there is no seprate table to store this information
				Statement stmt1;				stmt1 = conn.createStatement();				ResultSet rs1 = stmt1.executeQuery( sql1 );
				Integer counter=0, totalMessagesCount=0;
				
				//get the number of different people associated with this email address
				Integer countTotalIndividuals = returnNumberIndividualsUsingAnEmailAddress(conn, email);				
				if (rs1.next())			{		
					first= second =false;
					name =rs1.getString(1);						firstName =rs1.getString(2);					lastName =rs1.getString(3);
					senderEmailFirstSegment = rs1.getString(4);						totalMessagesCount = rs1.getInt(5);	
					//System.out.println("counter 6" + name);
					name = u.removeParenthesis(name);
					id=id+1;	
					if(id%10==0)
						System.out.println("counter "+ id + " email "+email + " name "+name);
					u.updateDistinctSendersTableWithSenderNames(conn, id, email,name,firstName,lastName,senderEmailFirstSegment,totalMessagesCount,countTotalIndividuals,"distinctSenders");	//distinctDevSenders			
					counter++;				
				} //end while
			}
		}  //end try 
		catch (SQLException se)	    {
			System.err.println("Got an sql exception! ");			System.err.println(se.getMessage() + " email " + emailAddress + " first "+first + " second "+ second);
		}   
		catch (Exception e)	    {
			System.err.println("Got an exception! ");			System.err.println(e.getMessage() + " email " + emailAddress + " first "+first + " second "+ second);
		}
	}

	// FINAL Step - update allmessage table with clusters found
	// for each cluster in distinctsenders, get the longest sendername 
	// find the longest senderfullname within the records of that cluster, longest senderfullname as it would have the most information
	// lookup all records in allmessages table which have that email, and update with the longest senderfullname
	
	//may 2018 ..found a problem with this approach last year. using longest name does not work for all cases fo guido
	
	//*** RUN THESE CODE BEFORE
	//UPDATE ALLMESsAGES set clusterBySenderFullName = NULL where clusterBySenderFullName IS NOT NULL;
	//update distinctsenders set reflected =NULL;	
	private static void reflectClustersToMainTableUsingResults() {	
		Statement sMainParent=null, sChild = null, sParent=null, sUpdate=null,sUpdate2=null;
		try {	
			Connection conn = mc.connect();
			//if there are still records in table to cluster -- allow to restart if hangs/stopped in middle
			System.out.format("%8s %40s %20s %40s %15s %15s", "cluster", "longestSenderName","distinct emails","senderemail"," updated records","reflectedUpdateTotal"," Time (ms)");
			System.out.println();
			while (getClustersLeftToReflect(conn)>0)	
			{
				sParent = conn.createStatement();			
				//PARENT
				String parentSQL = "SELECT cluster, SUM(totalMessageCount) sum, MAX(LENGTH(sendername)) maxSendernameLength "
						  		 + "FROM distinctsenders "
						         + "WHERE cluster IS NOT NULL AND clustered =1 " //and clusteredbysendername IS NOT NULL "
						         + "AND reflected IS NULL "
						  		 + "GROUP by cluster "
						         + "ORDER by sum DESC LIMIT 1;"; //limit 20 limit 1  LIMIT 50
				
				ResultSet recP = sParent.executeQuery(parentSQL);
				//Populate THE FIELDS FOR THE CHILD QUERY		    
				long startTime,stopTime,elapsedTime;
				Integer updateCounter=0; //, id=0;    			
				String longestSenderName=null;			
			
				//FOR EACH CLUSTER, FIND EMAIL AND THE ONGEST NAME AND UPDATE 
				if ((recP != null) && (recP.next())) 					//get all emails for that cluster
				{			
					startTime = System.currentTimeMillis();
					//id = recP.getInt("id");
					Integer maxSendernameLength = recP.getInt("maxSendernameLength");
					Integer cluster = recP.getInt("cluster");				
					longestSenderName = getLongestSenderNameForCluster(maxSendernameLength, cluster);							
					sChild = conn.createStatement();
									
					//String clusterUsingSenderName = "select clusterbysendername,  count(clusterbysendername) as cnt  from distinctsenders where cluster = "+ cluster + " group by clusterbysendername order by cnt desc;";
					//get all sendernames and email addresses in the same cluster
					Integer clusterBySenderName = getClusterBySenderName(cluster);	//RUN THIS TO GET THE CLUSTERS FOUND USING SENDERNAME //put into function - return one value
					//System.out.println("cluster "+cluster + " longestsendername " + longestSenderName + " clusterBySenderName " + clusterBySenderName);
					String childSQL =null;
					if (clusterBySenderName==null)
						childSQL = "select id, emailaddress from distinctsenders where cluster = "+ cluster + "; "; //and add the cluster from above					
					else	
						childSQL = "select id, emailaddress from distinctsenders where cluster = "+ cluster + " or clusterbysendername = " + clusterBySenderName + "; "; //and add the cluster from above
					
					ResultSet recC = sChild.executeQuery(childSQL);
					int emailCounter=0; 				
					int recSQLUpdate=0, totalRecSQLUpdate=0, reflectedUpdate=0, reflectedUpdateTotal=0; //number of rows updated
					String newStr2 = null;
					//For each email in cluster, update the allmessages table wherever that email exists
					while ((recC != null) && (recC.next())) 	
					{
						emailCounter++;
						int cid = recC.getInt("id");					
						String emailaddress = recC.getString("emailaddress");
						if(longestSenderName.contains("\'"))		//remove "
							longestSenderName = longestSenderName.replace("\'", "");				
						String newStr = emailaddress.replaceAll("^['\"]*", "").replaceAll("['\"]*$", "");
						newStr2 = newStr.replaceAll("^['\"]*", "").replaceAll("['\"]*$", "");
							
						sUpdate = conn.createStatement();
						sUpdate2 = conn.createStatement();
						String sqlUpdate = "update allmessages set clusterBySenderFullName = '" +longestSenderName+ "' where senderemail = '" + newStr2+ "';" ;
				 		//email similarity, modify to check as long as 'senderEmailFirstSegment' is as long as 2 characters long					
						recSQLUpdate = sUpdate.executeUpdate(sqlUpdate);
						totalRecSQLUpdate= totalRecSQLUpdate+recSQLUpdate;
						
						//update set reflected=1
						String sqlReflect = "update distinctsenders set reflected = 1 where id = " + cid+ ";" ;
				 		//email similarity, modify to check as long as 'senderEmailFirstSegment' is as long as 2 characters long					
						reflectedUpdate = sUpdate2.executeUpdate(sqlReflect);
						//OUTPUT HOW MANY DISTINCTSENDERs UOPDATED
						reflectedUpdateTotal= reflectedUpdateTotal+reflectedUpdate;
						sUpdate.close();
						sUpdate2.close();
						
					}	
					recC.close();
					stopTime = System.currentTimeMillis();
				    elapsedTime = stopTime - startTime;	
					updateCounter++;				
					System.out.format("%8d %40s %20d %40s %15s %15d", cluster,	longestSenderName,   emailCounter,newStr2,totalRecSQLUpdate,reflectedUpdateTotal,elapsedTime);
					System.out.println();
					sChild.close();
			}
			//System.out.println();
		}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//JOptionPane.showMessageDialog(null, e.getMessage());
			e.printStackTrace();
		}
		
	}
	
	private static Integer getClustersLeftToReflect(Connection conn){
		//Connection conn = mc.connect();	
		Integer rowCount =0;
		Statement sMainParent;
		try {
			sMainParent = conn.createStatement();
			//main parent - while unclustered records exists/ clustered=null
			//String mainParentSQL = "select count(*) from distinctsenders WHERE clustered IS NULL";
			String mainParentSQL = "SELECT count(cluster) " //, SUM(totalMessageCount) sum, MAX(LENGTH(sendername)) maxSendernameLength "
			  		 + "FROM distinctsenders "
			         + "WHERE cluster IS NOT NULL AND clustered = 1 " //and clusteredbysendername IS NOT NULL "
			         + "AND reflected IS NULL; ";
			  		 //+ "GROUP by cluster "
			         //+ "ORDER by sum DESC LIMIT 1;"; //limit 20 limit 1  LIMIT 50
			ResultSet recMP = sMainParent.executeQuery(mainParentSQL);
			recMP.last(); 
			rowCount = recMP.getRow();
			//conn.close();
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (rowCount==null)
			rowCount=0;
		return rowCount;
	}
	
	private static Integer getClusterBySenderName(Integer cluster){
		Statement sMainParent;
		Integer clusterBySenderName = null;
		try {
			Connection conn = mc.connect();	
			sMainParent = conn.createStatement();
			String mainParentSQL = "select clusterbysendername,  count(clusterbysendername) as cnt  "
									+ "from distinctsenders where cluster = "+ cluster + " "
									+ "group by clusterbysendername order by cnt desc;";
			ResultSet recMP = sMainParent.executeQuery(mainParentSQL);
			
			if ((recMP != null) && (recMP.next())) 		{
				clusterBySenderName = recMP.getInt("clusterbysendername");
			}
			else{
				clusterBySenderName = null;
			}
			sMainParent.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//main parent - while unclustered records exists/ clustered=null
		
		return clusterBySenderName;
	}

	private static String getLongestSenderNameForCluster(Integer length, Integer cluster){
		Statement sMainParent;
		String max = null;
		try {
			Connection conn = mc.connect();	
			sMainParent = conn.createStatement();
			String mainParentSQL = "select sendername as max from distinctsenders WHERE LENGTH(sendername) = "+length+" and cluster = "+cluster+" ORDER BY max LIMIT 1;";
			ResultSet recMP = sMainParent.executeQuery(mainParentSQL);
			
			if ((recMP != null) && (recMP.next())) 		{
				max = recMP.getString("max");
			}
			sMainParent.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//main parent - while unclustered records exists/ clustered=null
		
		return max;
	}

	private static Integer returnNumberIndividualsUsingAnEmailAddress(Connection conn, String email)
			throws SQLException {
		String sql2= "SELECT count(distinct(senderFullName)) "
				+ "from allmessages where senderEmail = '"+email+"' ";
				// + "group by senderFullName "
				//		+ "ORDER BY len desc;";  //there will be many rows since there is no seprate table to store this information
		Statement stmt2;
		stmt2 = conn.createStatement();
		ResultSet rs2 = stmt2.executeQuery( sql2 );
		Integer count = null;
		if (rs2.next())
		{
			count =rs2.getInt(1);	
		}
		return count;
	}

	

	private static void matchFirstMiddleLastNamesUpdate() {	
		Integer mid = null;
		String  emailAddress=null, firstName=null, middleName=null, lastName=null;
		boolean first=false, second =false;
		
		ArrayList<String> distinctNames = new ArrayList<String>();			
		try
		{
			Connection conn = mc.connect();				
			
			distinctNames = u.returnAllDistinctNames(conn);
			Integer id=0;
			//this feature allows it to be restarted
			//id = getMaxIDOrNULL(conn, id);
				//assign distinct id for records
			
			boolean toCheck = false;
			
			for (String name: distinctNames){
				//System.out.println("id "+ id+ " name "+ name);
				//assign null
				firstName=null; middleName=null; lastName=null;
				//split name here
				if (name.contains("@")){
					//split the name part from email and set that as name
				}
				if (name.contains(".") && !name.contains("@")) {
					String names[] = name.split("\\.");
					if (names.length==3){
						toCheck = true;
						firstName = names[0];
						middleName = names[1];
						lastName = names[2];
					}
					else if (names.length==2){
						toCheck = true;
						firstName = names[0];
						lastName = names[1];
					}
					else if (names.length==1){
						
					}
					else if (names.length>2){
						
					}
				}
				//look for names in table 
				
				if(toCheck) {	
					String toCheckName = firstName.substring(0,1)+lastName;
					String sql1 = "SELECT senderName, emailaddress from distinctsenders where senderName = '"+toCheckName+"'";  //there will be many rows since there is no seprate table to store this information
					Statement stmt1;
					stmt1 = conn.createStatement();
					ResultSet rs1 = stmt1.executeQuery( sql1 );
					Integer counter=0, totalMessagesCount=0;
					//System.out.println("name "+ name);
					String nm=null;
					while (rs1.next())
					{	
						first= second =false;
						nm =rs1.getString(1);	
						emailAddress=rs1.getString(2);	
						//totalMessagesCount = rs1.getInt(3);	
						//name = removeParenthesis(name);
						id=id+1;	
						//if(id%10==0)
						System.out.println("name "+ name);
						System.out.println("\t\temailAddress "+ emailAddress +  " name "+nm);
						//updateDistinctSendersTableWithSenderNames(conn, id, null,name,totalMessagesCount);				
						counter++;				
					} //end while
				}
				toCheck = false;
				id++;
			}
		}  //end try 
		catch (SQLException se)	    {
			System.err.println("Got an sql exception! ");
			System.err.println(se.getMessage() + " email " + emailAddress + " first "+first + " second "+ second);
		}   
		catch (Exception e)	    {
			System.err.println("Got an exception! ");
			System.err.println(e.getMessage() + " email " + emailAddress + " first "+first + " second "+ second);
		}
	}
	
	private static Integer getMaxIDOrNULL(Connection conn, Integer id, String tablename) throws SQLException {
		String sql0 = "SELECT max(id) from "+tablename+";";  //there will be many rows since there is no seprate table to store this information
		Statement stmt0;
		stmt0 = conn.createStatement();
		ResultSet rs0 = stmt0.executeQuery( sql0 );			
		if (rs0.next())	{				
			id =rs0.getInt(1);	
		}
		if (id==null)
			id=0;
		return id;
	}	
	
	//this is run first to set the email and sendername
	private static void updateAllMessagesTableExtractEmailSenderFullName() {		
		String cleanedFromLine=null, email=null, fromLine=null;	String newName = null;	String  emailAddress=null, firstName=null, middleName=null, lastName=null;
		Integer mid = null;				
		boolean first=false, second =false, third=false;
			
		try	{
			Connection conn = mc.connect();				
			//f.totalNumberOfMessages(conn);			//f.totalNumberOfUniqueMessages(conn);		
																										//100 k NULLs at a time, 
			String sql1 = "SELECT messageid,fromLine from allmessages where senderFullName IS NULL LIMIT 400000";  //there will be many rows since there is no seprate table to store this information
			Statement stmt1; 			stmt1 = conn.createStatement();			ResultSet rs1 = stmt1.executeQuery( sql1 );
			Integer counter=0;			
			while (rs1.next())	{	
				first= second =third=false;				mid = rs1.getInt(1);				fromLine =rs1.getString(2);
				if (fromLine.contains("\n")) 
					fromLine = fromLine.replace("\n", "");
				
				cleanedFromLine = fromLine.replace("From:", "").trim();												
				newName = pms.getFullAuthorFromString(cleanedFromLine);				newName= u.removeParenthesis(newName);
				
				//get fikrstname and lastname
				firstName=null; middleName=null; lastName=null;
				if (newName.contains(" ") && !newName.contains("@")) {
					String names[] = newName.split(" ");
					if (names.length==3){						//toUpdate = true;
						firstName = names[0];						middleName = names[1];						lastName = names[2];
					}
					else if (names.length==2){						//toUpdate = true;
						firstName = names[0];						lastName = names[1];
					}
					else if (names.length==1){}
					else if (names.length>2){
						firstName = names[0];						lastName = names[names.length-1];
					}
				}
				
				//email = getEmailFromString();
				first=true;
				if (cleanedFromLine.contains(" at "))	{
					String terms[] = cleanedFromLine.split(" ");					//Integer pos = cleanedFromLine.indexOf(" at ");
					Integer count=0, pos=0;
					for (String t: terms){						
						if (t.equals("at"))
							pos = count;
						count++;
					}
					email = terms[pos-1] + "@" +terms[pos+1];
				}
				else if (cleanedFromLine.contains("@")){
					String terms[] = cleanedFromLine.split(" ");					//Integer pos = cleanedFromLine.indexOf(" at ");
					Integer count=0, pos=0;
					for (String t: terms){						
						if (t.equals("@"))
							pos = count;
						count++;
					}
					email = terms[pos]; // + " " + terms[pos] + " " +terms[pos+1]; 					
				}
				if (email==null)		//sometimes if email is null, it gives error
					email = newName;
				email = u.removeParenthesis(email);				email = u.removeParenthesis(email);	//do 2 times as some emails were still showing up with both parenthesis at the ends 
				if(email.startsWith("\""))		//remove "
					email = email.replace("\"", "");
				if(email.startsWith("!"))		//remove "
					email = email.replace("!", "");
				if(email.contains("\\"))		//remove "
					email = email.replace("\\", "");
				if(email.contains("\'"))		//remove "
					email = email.replace("\'", "");
				second=true;

				//if sendername email length = 1 and email split using @, and split using .
				String nameSegment="",firstNameFromEmail=null;				Integer nameLength=0;
				if (email.contains("@")){
					nameSegment = email.split("@")[0];					//System.out.println("newName 0");
					if (nameSegment.contains(".")){
						nameLength=nameSegment.split("\\.").length;		//System.out.println("newName 1 nameSegment "+ nameSegment);
						//System.out.println("try "+nameSegment.split("\\.")[0]);
						firstNameFromEmail=nameSegment.split("\\.")[0];			//System.out.println("newName 2");
						//System.out.println("newName "+ newName + " email "+ email + " nameLength " + nameLength); 
						if (firstNameFromEmail.toLowerCase().equals(newName.toLowerCase()) && nameLength>1)
							newName = nameSegment.replace("\\."," ");
					}	
					
				}
				third=true;
				if(counter%1000==0)
					System.out.println("counter "+ counter + " fromLine "+fromLine + " newName "+newName + " email "+email);	
				//nov 2018, some gave erros we have to check
				if(email.length() > 200 || newName.length() > 200) {}
				else
					u.updateAllMessagesTableWithSenderEmail(conn, mid, email,newName, firstName, lastName, nameSegment);				
				counter++;				
			} //end while
			
		}  //end try 
		catch (SQLException se)	    {
			System.err.println("Got an sql exception! ");			System.err.println(se.getMessage() + " mid " + mid + " first "+first + " second "+ second);
			System.out.println(StackTraceToString(se)  );	
		}   
		catch (Exception e)	    {
			System.err.println("Got an exception! "+ e.toString());			System.err.println(e.getMessage() + " mid " + mid + " first "+first + " second "+ second + " third "+ third + " email " +email + " newname "+newName);
			System.out.println(StackTraceToString(e)  );	
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
