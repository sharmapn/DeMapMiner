package peopleAspect;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class VertexRetrievalForSocialNetworkAnalysis {
	public static void main(String args[]){
		
	}
	
	private static void retrieveData(Integer pepType, Connection connection) 
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
			String sql3 = "SELECT senderName, (SELECT senderName from allmessages where ) allmessages;";  //there will be many rows since there is no seprate table to store this information
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

}
