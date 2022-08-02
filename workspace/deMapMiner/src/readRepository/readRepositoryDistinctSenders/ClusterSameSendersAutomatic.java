package readRepository.readRepositoryDistinctSenders;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import connections.MysqlConnectForQueries;
//nov 2018, first script to cluster senders
//how it works... the allmessages table has all messages by different authors,
//first the distinct authors are extracted and put in a seprate table - distinctsenders and most of the clustering work is done there 
//and then the clusters are reflected back in the allmessages table
import connections.MysqlConnect;

public class ClusterSameSendersAutomatic {
	static MysqlConnect mc = new MysqlConnect(); //static MysqlConnectForQueries mc = new MysqlConnectForQueries();
	static Connection conn = mc.connect();
	
	public static void main(String[] args) {		
		AutomaticallyClusterUsingLevenshteinDistance();		
		//closeConnection(conn);		
	}
	
	static void AutomaticallyClusterUsingLevenshteinDistance() {
		Statement sMainParent=null, sChild = null, sParent=null, sUpdate=null, sUpdate2=null;
		//Connection conn = null;
		try {			
			sParent = conn.createStatement();			sChild = conn.createStatement();			sUpdate = conn.createStatement();	sUpdate2 = conn.createStatement();	
			System.out.format("%8s %40s %20s %20s %40s %15s %15s", "cluster", "SenderName","senderFirstName","senderLastName","senderemail"," cluster members"," Time (ms)");
			System.out.println();
			
			int id,totalMessageCount,idInCluster;
			String sendername,emailaddres,firstName,lastName ,senderEmailFirstSegment,sendernamesInCluster,emailaddressInCluster,firstNamesInCluster,lastNamesInCluster , senderEmailFirstSegmentInCluster;
			
			while (getNumberSendersLeftToCluster()>0)	//if there are still records in table to cluster
			{
				//PARENT
				//just process one record at a time as table is updated/clustered and we dont want to repeat processing for similar sender records, guido clusters was assigned 1 but then n270 again...so have to prevent that
				String parentSQL = "select id,sendername, emailaddress,senderFirstName, senderLastName, senderEmailFirstSegment, totalMessageCount "
						  		 + "from distinctsenders "
						         + "WHERE clustered IS NULL order by totalMessageCount DESC limit 1"; //limit 20
				
				ResultSet recP = sParent.executeQuery(parentSQL);
				int rowParent = 0,parentID = 0, updateCntr =0; //parentID 	
				//Populate THE FIELDS FOR THE CHILD QUERY		    
				String parentSendername = null,parentEmailaddress, parentFirstName = null, parentLastName = null;
				long startTime,stopTime,elapsedTime;
				//as stated above have to do one cluster at a time
				if ((recP != null) && (recP.next())) 
				{
					startTime = System.currentTimeMillis();
					 id = recP.getInt("id");	 sendername = recP.getString("sendername");				emailaddres = recP.getString("emailaddress");						
					 firstName = recP.getString("senderFirstName");			lastName = recP.getString("senderLastName");
					 senderEmailFirstSegment = recP.getString("senderEmailFirstSegment");					 totalMessageCount = recP.getInt("totalMessageCount");
					
					//.lowercase after checking not null
					if (sendername !=null) sendername = sendername.toLowerCase();
					if (emailaddres !=null) emailaddres = emailaddres.toLowerCase();
					if (firstName !=null) firstName = firstName.toLowerCase();
					if (lastName !=null) lastName = lastName.toLowerCase();
					if (senderEmailFirstSegment !=null) senderEmailFirstSegment = senderEmailFirstSegment.toLowerCase();
					
					System.out.format("%8s %40s %20s %20s %40s %15s %15s", id, sendername, firstName, lastName,  emailaddres,"this repeated","in last record");
					System.out.println();
					
					String nameEmailSimilarity="";
					//replace(col_name , ' ','') 
					//have set Levenshtein distance =1 for firstname and lastname combination match, because with 2, 'Roy' was matching 'Ray'
					String sqlChild = 
							//-- 1. A fullname similarity (MAIN QUERY)
							  "SELECT id,sendername, emailaddress, senderFirstName, senderLastName, "	//-- not sure why i put in front DISTINCT (sendername) AS MATCHING,
							+ "NULL AS 'DUMMY', "
							+ "Levenshtein('"+sendername+"',LOWER(sendername)) AS LevenshteinDistance,'FULL SENDERNAME MATCH' AS 'match' "
							+ "FROM distinctsenders "
							+ "WHERE clustered IS NULL AND LENGTH(replace(TRIM(sendername),' ','') ) > 3 "   // if sendername is atleast 3 characters then use sendername match, else use email first segment
							+ "AND (sendername NOT IN (select senderfirstname from distinctsendersfirstname WHERE senderfirstname IS NOT NULL)  AND senderfirstname IS NULL AND senderLastName is NULL) " // we dont want to match when sendernames only contains firstname as it wouild match everyone with taht firstname, eg Brian
							+ "HAVING LevenshteinDistance <2 " //-- order by t asc limit 15   

							//B. names similarity (combined firstname and lastname)
				   			+ "UNION " 
						    + "SELECT id,sendername, emailaddress, senderFirstName, senderLastName, "
						    + "Levenshtein('"+firstName+"',LOWER(senderfirstname)) AS 'firstnamematch', Levenshtein('"+ lastName+"',LOWER(senderlastname)) AS 'lastnamematch', 'COMBINED FIRST&LASTNAME MATCH' AS 'match' "
						    + "FROM distinctsenders " 
						    + "WHERE clustered IS NULL AND senderfirstname IS NOT NULL AND senderlastname IS NOT NULL AND LENGTH(replace(TRIM(sendername),' ','') ) > 3 "  //add to make sure incorrect matches dont happen
						    + "HAVING lastnamematch <2 AND firstnamematch <2 "  //order by Levenshtein   -- order by lastnamematch asc, firstnamematch asc 
													//WHERE clustered IS NULL	//where id != "+parentID+" -- we dont want to include the 
					 		+ " UNION "
					 		//-- 3. names-email similarity (email vs combined firstname and lastname) -- first part --  'guido' = email
							+ "SELECT id,sendername, emailaddress, senderFirstName, senderLastName, "
							+ "NULL AS 'DUMMY', "
							+ "Levenshtein('"+firstName+" "+ lastName+"', LOWER(CONCAT(senderfirstname,' ',senderLastName))) AS 'COMBINEDFIRSTLASTNAMEMATCH', 'EMAIL VS COMB F&L NAME MATCH' AS 'match' "
							+ "FROM distinctsenders "
							+ "WHERE clustered IS NULL AND senderfirstname IS NOT NULL AND senderlastname IS NOT NULL AND LENGTH(replace(TRIM(sendername),' ','') ) > 3  "
							+ "HAVING COMBINEDFIRSTLASTNAMEMATCH <2 -- order by COMBINEDFIRSTLASTNAMEMATCH asc "
							// second part - EMAIL VS FIRST LETTER F&L NAME MATCH, 'guido' = email   
							+ "UNION "						
							+ "SELECT id,sendername, emailaddress, senderFirstName, senderLastName, "
							+ "Levenshtein('"+senderEmailFirstSegment+"', LOWER(CONCAT(SUBSTRING(senderfirstname,0,1),senderLastName))) AS 'emailVs1stLetterFirstNameRestLName', "
							+ "Levenshtein('"+senderEmailFirstSegment+"', LOWER(CONCAT(senderfirstname, SUBSTRING(senderLastname,0,1)))) AS 'emailVsFirstNameFirstLetterLname', "
							+ "'EMAIL VS FIRST LETTER F&L NAME MATCH' AS 'match' "
							+ "FROM distinctsenders "
							+ "WHERE clustered IS NULL AND senderfirstname IS NOT NULL AND senderlastname IS NOT NULL LENGTH(replace(TRIM(sendername),' ','') ) > 3  "
							+ "HAVING emailVs1stLetterFirstNameRestLName <2 OR emailVsFirstNameFirstLetterLname <2 -- order by COMBINEDFIRSTLASTNAMEMATCH asc "
							//-- 4. email similarity (firstsegment only)
							+ " UNION "						
						    + "SELECT id,sendername, emailaddress, senderFirstName, senderLastName, " 
						    + "NULL AS 'DUMMY', "
						    + "Levenshtein('"+senderEmailFirstSegment+"',LOWER(emailFirstSegment)) AS LevenshteinDistance,'EMAIL MATCH' AS 'match' "
						    + "FROM distinctsenders "
						    + "WHERE clustered IS NULL AND emailFirstSegment IS NOT NULL "	//dont check sendername length here - allow comparison using emailfisrt segment
						    + "HAVING LevenshteinDistance <2 -- order by t asc limit 15; ";
		
			 		//email similarity, modify to check as long as 'senderEmailFirstSegment' is as long as 2 characters long
					
					ResultSet recChild = sChild.executeQuery(sqlChild);
					
					//get last max cluster id from distinctdevsender
					Integer maxID = getMaxIDOrNULL(conn, "distinctSenders");	//distinctDevSenders
					maxID++;
					//now update the above using the passed ids....update in distinctsenders
					Integer sid=null, updateCounter=0;
					
					while ((recChild != null) && (recChild.next())) {						
						sid = recChild.getInt("id");
						//idInCluster  = recP.getInt("id");
						sendernamesInCluster = recChild.getString("sendername");						emailaddressInCluster = recChild.getString("emailaddress");						
						firstNamesInCluster = recChild.getString("senderFirstName");					lastNamesInCluster = recChild.getString("senderLastName");
						//senderEmailFirstSegmentInCluster = recChild.getString("senderEmailFirstSegment");
						//int totalMessageCountInCluster = recChild.getInt("totalMessageCount");
						
						//update					
						String updateSQL = "UPDATE distinctsenders SET clustered = 1, cluster = "+ maxID +" WHERE id=" + sid + ";";
						sUpdate.execute(updateSQL);
						updateCounter++;
						System.out.format("%8s %40s %20s %20s %40s %15s %15s", sid, sendernamesInCluster, firstNamesInCluster, lastNamesInCluster,  emailaddressInCluster,maxID,updateCounter);
						System.out.println();
					}
					//ALSO NEED TO COMBINE WITH THE RECORD WHICH WE USED TO MATCH
					String updateSQLOriginal = "UPDATE distinctsenders SET clustered = 1, cluster = "+ maxID +" WHERE id=" + id + ";";
					sUpdate2.execute(updateSQLOriginal);
					//rowParent++;
					stopTime = System.currentTimeMillis();
				    elapsedTime = stopTime - startTime;				
				    System.out.format("%8s %40s %20s %20s %40s %15s %15s", id, sendername,firstName,lastName,emailaddres,maxID,updateCounter);
					System.out.println();
				}	
				System.out.println();
				//CHILD
				//distinctsenders containsall senders, distinctdevsenders contains only developers								
			}	

		} catch (Exception e) {
			// TODO Auto-generated catch block
			//JOptionPane.showMessageDialog(null, e.getMessage());
			e.printStackTrace();
		}
	}
	
	static void ClusterBySenderName() {
		Statement sMainParent=null, sChild = null, sParent=null, sUpdate=null, sUpdate2=null;
		//Connection conn = null;
		try {			
			sParent = conn.createStatement();			sChild = conn.createStatement();			sUpdate = conn.createStatement();						sUpdate2 = conn.createStatement();
			
			while (getNumberSendersLeftToClusterBySenderName()>0)	//if there are still records in table to cluster
			{
				//PARENT
				//just process one record at a time as table is updated/clustered and we dont want to repeat processing for similar sender records, rhetinger clusters was assigned 1 but then n270 again...so have to prevent that
				String parentSQL = "select id,sendername, emailaddress,senderFirstName, senderLastName, senderEmailFirstSegment, totalMessageCount "
						  		 + "from distinctsenders "
						// we just trying to find matching records for those which dont have firstname and lastname therefore dont include all records, only those with fnam and lname = null
						         + "WHERE clusteredBySenderName IS NULL AND clusterBySenderName IS NULL AND senderFirstName IS NULL AND senderLastName IS NULL "  //emailaddress = 'rhettinger@users.sourceforge.net' AND 
						         + "order by totalMessageCount DESC LIMIT 1"; //limit 20
				
				ResultSet recP = sParent.executeQuery(parentSQL);
				int rowParent = 0,parentID = 0; //parentID 	
				//Populate THE FIELDS FOR THE CHILD QUERY		    
				String parentSendername = null,parentEmailaddress, parentFirstName = null, parentLastName = null, fname=null,lname=null;
				long startTime,stopTime,elapsedTime;
				//as stated above have to do one cluster at a time
				Integer maxID = getMaxIDOrNULLForClusteredBySenderName(conn, "distinctSenders");
				//since the above results are changed as table is modified, we need to query everytime
				if ((recP != null) && (recP.next())) 
				{
					startTime = System.currentTimeMillis();
					int id = recP.getInt("id");
					String emailFirstSegment =  recP.getString("senderEmailFirstSegment").toLowerCase();
					String sendername = recP.getString("sendername").toLowerCase();				
					String nameEmailSimilarity="";
					
					//ASSUMING SENDERNAME IS COMBINATION OF FIRST LETTER IF FIRSTNAME AND the full lastname
					//get the lastname from the sendername			
					//we only do sendername match
					//match lastname with lastname and match first letter of firstname with first letter of firstname
					
					String sqlChild =  
							//-- 1. A fullname similarity (MAIN QUERY)
							// firstname + (fisrtletter)lastname
							"SELECT id,sendername, emailaddress, senderFirstName, senderLastName, "
							+ "Levenshtein('"+sendername+"', lower(CONCAT(SUBSTRING(senderfirstname,1,1),senderLastName))) AS 'sendernameVs1stLetterFirstNameRestLName' "
							//+ "Levenshtein('"+senderEmailFirstSegment+"', CONCAT(senderfirstname, SUBSTRING(senderLastname,0,1))) AS 'emailVsFirstNameFirstLetterLname', "
							//+ "'SENDERNAME VS FIRST LETTER F&L NAME MATCH' AS 'match' "
							+ "FROM distinctsenders "
							+ "WHERE clusteredBySenderName IS NULL AND clusterBySenderName IS NULL AND sendername NOT IN (select senderfirstname from distinctsendersfirstname WHERE senderfirstname IS NOT NULL) "  //to make sure sendername is not just the firstname - will match a lot of members with that sendername
							+ "HAVING sendernameVs1stLetterFirstNameRestLName <1 "
					
							//the above wil miss those which have senderfirstname and senderlastname = null
					       //therefore need one select on sendername
							+ "UNION "
							+ "SELECT id,sendername, emailaddress, senderFirstName, senderLastName, "
							+ "Levenshtein('"+sendername+"', lower(senderName)) AS 'sendernameDistance' "
							//+ "Levenshtein('"+senderEmailFirstSegment+"', CONCAT(senderfirstname, SUBSTRING(senderLastname,0,1))) AS 'emailVsFirstNameFirstLetterLname', "
							//+ "'SENDERNAME VS FIRST LETTER F&L NAME MATCH' AS 'match' "
							+ "FROM distinctsenders "
							+ "WHERE clusteredBySenderName IS NULL AND clusterBySenderName IS NULL AND sendername NOT IN (select senderfirstname from distinctsendersfirstname WHERE senderfirstname IS NOT NULL) "  //to make sure sendername is not just the firstname - will match a lot of members with that sendername
							+ "HAVING sendernameDistance <1;";
					
							// lastname + (firstletter)firstname
							/*
							+ " UNION "	
							+ "SELECT id,sendername, emailaddress, senderFirstName, senderLastName, "
							+ "Levenshtein('"+sendername+"', lower(CONCAT(SUBSTRING(senderlastname,1,1),senderFirstName))) AS 'sendernameVs1stLetterLastNameRestFName' "
							//+ "Levenshtein('"+senderEmailFirstSegment+"', CONCAT(senderfirstname, SUBSTRING(senderLastname,0,1))) AS 'emailVsFirstNameFirstLetterLname', "
							//+ "'SENDERNAME VS FIRST LETTER F&L NAME MATCH' AS 'match' "
							+ "FROM distinctsenders "
							+ "WHERE clusteredBySenderName IS NULL AND clusterBySenderName IS NULL "
							+ "HAVING sendernameVs1stLetterLastNameRestFName <1;";
							
							//why use this select below because it clusters unwanted email first segments together - there are 76 email firstsegment addresses with term 'python' 
							+ " UNION "						
							+ "SELECT id,sendername, emailaddress, senderFirstName, senderLastName, " 
							//+ "NULL AS 'DUMMY', "
							+ "Levenshtein('"+emailFirstSegment+"',LOWER(senderEmailFirstSegment)) AS LevenshteinDistance "
							+ "FROM distinctsenders "
							+ "WHERE clusteredBySenderName IS NULL AND clusterBySenderName IS NULL "
							+ "HAVING LevenshteinDistance <1; ";
						    */
					//UNION EMAILFIRSTSEGNMENT
					
					
		
			 		//email similarity, modify to check as long as 'senderEmailFirstSegment' is as long as 2 characters long
					
					ResultSet recChild = sChild.executeQuery(sqlChild);
					
					//get last max cluster id from distinctdevsender
						//distinctDevSenders
					maxID++;
					//System.out.println(" maxid = "+maxID); 
					
					//now update the above using the passed ids....update in distinctsenders
					Integer sid=null, clusterSenders=0;
					while ((recChild != null) && (recChild.next())) {
						sid = recChild.getInt("id");
						//update					
						String updateSQL = "UPDATE distinctsenders SET clusteredBySenderName = 1, clusterBySenderName = "+ maxID +" WHERE id=" + sid + ";";
						sUpdate.execute(updateSQL);
						clusterSenders++;
					}
					//ALSO NEED TO COMBINE WITH THE RECORD WHICH WE USED TO MATCH
					String updateSQLOriginal = "UPDATE distinctsenders SET clusteredBySenderName = 1, clusterBySenderName = "+ maxID +" WHERE id=" + id + ";";
					sUpdate2.execute(updateSQLOriginal);
										
					//rowParent++;
					stopTime = System.currentTimeMillis();
				    elapsedTime = stopTime - startTime;
				
					System.out.println("Clustering completed for sendername " + sendername + " with maxid = "+maxID + " elapsedTime: " + elapsedTime + " clusterSenders: "+ clusterSenders);
				}
							
				//CHILD
				//distinctsenders containsall senders, distinctdevsenders contains only developers
			}	
			System.out.println("Processing Finished");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//JOptionPane.showMessageDialog(null, e.getMessage());
			e.printStackTrace();
		}
	}
	
	private static Integer getNumberSendersLeftToCluster() throws SQLException {
		Statement sMainParent;
		sMainParent = conn.createStatement();
		//main parent - while unclustered records exists/ clustered=null
		String mainParentSQL = "select count(*) from distinctsenders WHERE clustered IS NULL";
		ResultSet recMP = sMainParent.executeQuery(mainParentSQL);
		recMP.last(); 
		Integer rowCount = recMP.getRow();
		return rowCount;
	}
	
	private static Integer getNumberSendersLeftToClusterBySenderName() throws SQLException {
		Statement sMainParent;
		sMainParent = conn.createStatement();
		//main parent - while unclustered records exists/ clustered=null
		String mainParentSQL = "select count(*) from distinctsenders WHERE clusteredBySenderName IS NULL";
		ResultSet recMP = sMainParent.executeQuery(mainParentSQL);
		recMP.last(); 
		Integer rowCount = recMP.getRow();
		return rowCount;
	}

	private static Integer getMaxIDOrNULL(Connection conn, String tablename) throws SQLException {
		Integer id=0;
		String sql0 = "SELECT max(cluster) from "+tablename+";";  //there will be many rows since there is no seprate table to store this information
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
	
	private static Integer getMaxIDOrNULLForClusteredBySenderName(Connection conn, String tablename) throws SQLException {
		Integer id=0;
		String sql0 = "SELECT max(clusterBySenderName) from "+tablename+";";  //there will be many rows since there is no seprate table to store this information
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
	
	// Delete
	private void ClusterIDs(String ID, Connection conn) {

		//Connection conn = null;
		Statement s = null;

		try {
			conn = mc.connect();	
			s = conn.createStatement();

			//get last max cluster id from distinctdevsender
			Integer maxID = getMaxIDOrNULL(conn, "distinctDevSenders");
			
			//String sql = "DELETE FROM customer  WHERE " + "CustomerID = '"
//			String sql = "INSERT INTO distinctdevsenders (id, emailaddress, sendername,totalMessageCount,senderFirstName, senderLastName)"
//							+ " SELECT id, emailaddress, sendername,totalMessageCount,senderFirstName, senderLastName"
//							+ " FROM   distinctdevsenders"
//							+ " WHERE  id IN ("+ ID +")";			
					//+ strCustomerID + "' ";
						
			//update table set clusters marked = true
			String updateSQL = "UPDATE distinctdevsenders SET clustered = 1, cluster = "+ maxID +" WHERE id=" + ID + ";";
			s.execute(updateSQL);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			//JOptionPane.showMessageDialog(null, e.getMessage());
			e.printStackTrace();
		}

//		try {
//			if (s != null) {
//				s.close();
//				conn.close();
//			}
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			System.out.println(e.getMessage());
//			e.printStackTrace();
//		}

	}
	
	public static void closeConnection(Connection conn){
		try {
			if (conn != null) {
				//s.close();
				conn.close();
			}
		} 
		catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
	}

}
