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

//just store the messageid for the state change
public class UpdateMessageID {	
	static String tablename = 	"pepstates_danieldata_datetimestamp";
	
	public static void main(String args[])	{	
		MysqlConnect mc = new MysqlConnect();		Connection connection = mc.connect();		
		Integer sum=0,totalCounter=0;		
		try	{
			String query= "SELECT pep, date2, email, messageid  FROM pepstates_danieldata_datetimestamp;";
			Statement st = connection.createStatement();			ResultSet rs = st.executeQuery(query);
			int pep, mid; 
			String status;			Date date2;	
			
			while (rs.next()){	
				pep = rs.getInt("pep");	date2 = rs.getDate("date2");	status = rs.getString("email");	mid = rs.getInt("messageid");
				status = status.replace("Status :", "Status:");
				System.out.println(pep+" "+ date2 + " "+ status);				
				String query2="SELECT pep, messageid, email, date2 FROM allmessages where pep = "+pep+" and date2 = '" + date2 + "' and email like '%"+status+"%' ;";
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
				}
			}
			System.out.println("Total rows updated in table  "+totalCounter );
			st.close();
		}	
			catch (Exception e)	{
			System.err.println("Got an exception! ");			System.err.println(e.getMessage());
		}		
	}
}
