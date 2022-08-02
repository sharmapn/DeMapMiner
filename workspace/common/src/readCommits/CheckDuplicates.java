package readCommits;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import connections.MysqlConnect;
//Feb 2019, important to check if any extra or missing data so we check tables
//Run file 'feb2019missingStates3.sql' to check which table has more peps.
//I checked but new table does not have all the peps from teh old one, so we use the old one and it has been renamed back to 'pepstates_danieldata_datetimestamp' so other scipts could use that
//just store the messageid for the state change
public class CheckDuplicates {	
	static String tablename 		= 	"pepstates_danieldata_datetimestamp_old";
	static String tablenameToCheck  = 	"pepstates_danieldata_datetimestamp";
	
	
	public static void main(String args[])	{	
		MysqlConnect mc = new MysqlConnect();		Connection connection = mc.connect();		
		Integer sum=0,totalCounter=0;	
		String tableName[] = {tablename,tablenameToCheck};
		try	{
			for (int i=0; i<5000;i++) {
				boolean newLine= false;
				Integer counter=0,numberInTableA=0, numberInTableB=0;
				//System.out.print("Proposal: " + i+ " ");
				for (String table : tableName) { //check data from two tables
					counter++;
					String query= "SELECT pep, date2, email, messageid  FROM "+ table+" where pep = "+i+" order by date2 asc;";
					Statement st = connection.createStatement();			ResultSet rs = st.executeQuery(query);
					int pep, mid; 
					String status;			Date date2;	
					
					Integer count = returnRSCount(query, connection);
					if(counter==1)
						numberInTableA = count;
					else if (counter==2)
						numberInTableB = count;
										
					if (count > 0) {
						if(!newLine) {
							//System.out.println();	
							newLine=true;
						}
						System.out.print(i+ " , ");  //"Proposal: "+ i + " " +   //" + table+ "
					}
					while (rs.next()){	
						pep = rs.getInt("pep");	date2 = rs.getDate("date2");	status = rs.getString("email");	mid = rs.getInt("messageid");
						status = status.replace("Status :", "Status:");
						System.out.print(status.replace("Status: ", "") + ", ");			//date2 + " "+ 	
						/*String query2="SELECT pep, messageid, email, date2 FROM allmessages where pep = "+pep+" and date2 = '" + date2 + "' and email like '%"+status+"%' ;";
						Statement st2 = connection.createStatement();				ResultSet rs2 = st2.executeQuery(query2);				
						if (rs2.next())	{
							int messageid = rs2.getInt("messageid");
							try	{
								Statement sMainParent=connection.createStatement();
								int update;
								String sqlReflect = "update "+tablename+" set messageID_allmessages = "+ messageid +" where messageid = " + mid +";" ;
								//email similarity, modify to check as long as 'senderEmailFirstSegment' is as long as 2 characters long					
								update = sMainParent.executeUpdate(sqlReflect);
								//						if(sum>1)	//only want to see which messages are assigned under multiple pepss 
								System.out.println("\tUpdated table with messageid "+messageid+" rows for messageid " + messageid);
								sMainParent.close();
							}	
							catch (Exception e)	{
								System.err.println("Got an exception! ");						System.err.println(e.getMessage());
							}
						} */
					}
					if(counter==2){
						if(numberInTableB ==0 && numberInTableA==0) {}
						else
							System.out.print(" "+numberInTableA + "-" + numberInTableB);
					}
					if(counter==2 && numberInTableB != numberInTableA){
						System.out.println(" UNEVEN");
					}
					//System.out.println("Total rows updated in table  "+totalCounter );
					st.close();
				}
				//System.out.println("");
			} //end for loop
		}	
		catch (Exception e)	{
			System.err.println("Got an exception! ");			System.err.println(e.getMessage());
		}		
	}
	
	public static Integer returnRSCount (String v_sql, Connection conn){   
		   //testConnection();       
	       //  Connect to an MySQL Database, run query, get result set

	       //check for parent messages - i.e. he message to which this message is a reply
	       //get the message id of these messages
	       String sql = v_sql; 
	       Integer counter = null; 	       //String v_inReplyTo;
	       String v_Message;
		   //now for each of the above message, 
	       Statement stmt = null;
		   try{		   
			   Connection connection = conn;
	           stmt = connection.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
	           ResultSet rs = stmt.executeQuery( sql );
			   if (rs.next()) {
	               rs.last(); 	               counter = rs.getRow();
	              // return rs.getRow();
	           } 
	           else {
	          //     System.out.println("No Data");
	               counter = 0;
	           }
		   }   
	           catch (SQLException e1){
	               System.out.println( e1.getMessage() );
	            }
	     	   catch(Exception e){
	     	        //Handle errors for Class.forName
	     	        e.printStackTrace();
	     	   }
	            finally{
	     	        //finally block used to close resources
	                   try{
	     		           if( stmt != null)
	     		              stmt.close();
	                   	}
	                   catch(SQLException se2){
	                   	
	                   }// nothing we can do
	               }  
		   return counter;		 
	   }
}
