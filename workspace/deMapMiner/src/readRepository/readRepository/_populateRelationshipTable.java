package readRepository.readRepository;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class _populateRelationshipTable {
	
	public static void main(String [] args) throws IOException
	   {
		
		for (int i=1; i<4000; i++){
			   try {
						//System.out.println("\nPep " + i); 
				   populateRelationshipTable(i);
				   }
				   catch (Exception e){
					   continue;
				   }
		   }
		System.out.println("\nFinished processing"); 
		   //generateCsvFile("c:\\scripts\\test.csv"); 
	   }
	
	public static void populateRelationshipTable(Integer v_PEP){
	       try {
	           Class.forName("com.mysql.jdbc.Driver").newInstance();
	         } catch (Exception e) {
	           System.err.println("Unable to find and load driver");
	           System.exit(1);
	         }
	       
	   //  Connect to an MySQL Database, run query, get result set
	       String url = "jdbc:mysql://localhost:3306/peps";
	       String userid = "root";
	       String password = "root";
	       String sql = "SELECT messageID, inReplyTo from allpeps where pep = " + v_PEP + " order by date2;"; 	//all messages where pep is mentioned - get distinct messageids
	       
	       Integer v_parent_MessageID;
	       Integer v_child_MessageID = null;
	       Integer v_child_pepNumber = null;
	       String v_inReplyTo;
	       Integer counter_pepMessages=0;
	       Integer counter_pepMessage_Replies=0;
	       
	       //can add messageid later
	       try (Connection connection = DriverManager.getConnection( url, userid, password );
	               Statement stmt = connection.createStatement();
	               ResultSet rs = stmt.executeQuery( sql ))
	           {
	           	   while (rs.next()){     //check every message       
	           		counter_pepMessages++;
	           		   
	           		   v_parent_MessageID = rs.getInt(1);
	           		   v_inReplyTo = rs.getString(2);
	           		   if (v_inReplyTo != null)	{
		           		   String sql2 = "SELECT pep, messageID from allpeps where EmailmessageID = '" + v_inReplyTo + "' order by date2;";
		           		   Statement stmt2 = connection.createStatement();
		           		   ResultSet rs2 = stmt2.executeQuery( sql2 );
		           		   counter_pepMessage_Replies =0;
		           		   while (rs2.next()){     //check every message       
		           			   counter_pepMessage_Replies++;
		           			   v_child_pepNumber = rs2.getInt(1);
		           			   v_child_MessageID = rs2.getInt(2);
			           		   
		      	           	   Integer pepNumber = v_PEP;	       
				     	       //  Connect to an MySQL Database, run query, get result set
				     	       //DELETE t1 FROM table t1, table t2 WHERE t1.name = t2.name AND t1.id > t2.id;
		      	           	   String query = " insert into relatepeps (pep, parent_messageID, child_messageID, child_pepnumber)"
		      	                  + " values (?, ?, ?, ?)";
		      	           
		      	                // create the mysql insert preparedstatement
		      	                PreparedStatement preparedStmt = connection.prepareStatement(query);
		      	                preparedStmt.setInt (1, v_PEP);
		      	                preparedStmt.setInt (2, v_parent_MessageID);
		      	                preparedStmt.setInt   (3, v_child_MessageID);
		      	                preparedStmt.setInt(4, v_child_pepNumber);
		      	           
		      	                // execute the preparedstatement
		      	                preparedStmt.execute();
		      	                System.out.printf("\nFor pep " + v_PEP + " inserted record " + v_parent_MessageID + ", " + v_child_pepNumber + ", " + v_child_MessageID);
		      	               // System.out.printf("\nFor pep " + v_PEP + " %d row(s) updated!", rs); 
		           		   }
		           		   System.out.printf("\n For pep " + v_PEP + "Counter pepMessage Replies " + counter_pepMessage_Replies);
	           		   }
	           		   else {
	           			   	String query2 = " insert into relatepeps (pep, parent_messageID)"
		      	                  + " values (?, ?)";
		      	           
		      	                // create the mysql insert preparedstatement
		      	                PreparedStatement preparedStmt = connection.prepareStatement(query2);
		      	                preparedStmt.setInt (1, v_PEP);
		      	                preparedStmt.setInt (2, v_parent_MessageID);
		      	                //preparedStmt.setInt   (3, v_child_MessageID);
		      	                //preparedStmt.setInt(4, v_child_pepNumber);
		      	           
		      	                // execute the preparedstatement
		      	                preparedStmt.execute();
		      	              System.out.printf("\nFor pep " + v_PEP + " inserted record " + v_parent_MessageID);
	           		   } //end else
	           	   }  //end while
	           	   System.out.printf("\npep "+ v_PEP + "has " + counter_pepMessages);
	           } //end try
	       catch (SQLException e)
	       {
	           System.out.println( e.getMessage() );
	       }
	       
	       
	       
	}
}
