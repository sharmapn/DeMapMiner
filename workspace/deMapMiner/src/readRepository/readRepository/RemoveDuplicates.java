package readRepository.readRepository;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;


//remove messages with same emailmessageid
public class RemoveDuplicates {
	
	public static void main(String [] args) throws IOException
	   {
		
		for (int i=0; i<4000; i++){
			   try {
						//System.out.println("\nPep " + i); 
						RemoveDuplicates(i);
				   }
				   catch (Exception e){
					   continue;
				   }
		   }
		System.out.println("\nFinished processing"); 
		   //generateCsvFile("c:\\scripts\\test.csv"); 
	   }
	
	public static void RemoveDuplicates(Integer v_PEP){
	       try {
	           Class.forName("com.mysql.jdbc.Driver").newInstance();
	         } catch (Exception e) {
	           System.err.println("Unable to find and load driver");
	           System.exit(1);
	         }
	       
	       Integer pepNumber = v_PEP;	       
	       //  Connect to an MySQL Database, run query, get result set
	       //DELETE t1 FROM table t1, table t2 WHERE t1.name = t2.name AND t1.id > t2.id;
	       String sql = "DELETE t1 FROM allpeps t1, allmessages t2 WHERE t1.emailMessageId = t2.emailMessageId AND t1.messageID > t2.messageID AND t1.pep = " + pepNumber + ";"; 
	       //can add messageid later	       
	       Connection conn = null;
	       Statement stmt = null;	       
	      // System.out.println("\nPep " + v_PEP + " " + sql.toString());
	       int rs = 0;
	    
	       try{
	    	   Class.forName("com.mysql.jdbc.Driver");
	    	   // Create a url for accessing the MySQL
	    	   // database CarDB
	    	   String url = "jdbc:mysql://localhost:3306/peps_new";
	    	   // user and password to access the database
	    	   String username = "root";
	    	   String password = "root";
		       conn = DriverManager.getConnection(url, username, password);
		       stmt = conn.createStatement();
		       rs = stmt.executeUpdate( sql );
	       }
	       catch (Exception e) {
	    	   
	       }
	       System.out.printf("\nFor pep " + pepNumber + " %d row(s) updated!", rs); 
	}
}