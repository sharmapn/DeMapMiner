package GeneralQueries;

import java.io.*;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import connections.MysqlConnect;
public class WriteSpecificPEPMessagesToTxtFiles {
	static Integer pepNumber;
    public static void main(String[] args) throws IOException {
    	
        pepNumber=308;    	
        String processedMessage = "I reject the pep";
		
        try {
        	MysqlConnect mc = new MysqlConnect();
    		Connection conn = mc.connect();
			writeAllPepMesagesToFile(conn);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}        
        System.out.println("Processing Finished" );
    }

	private static void writeMessageToSepratePage(String processedMessage, Integer messageCounter, Date date) throws IOException {
		File f = new File("c:\\scripts\\TonysStudy\\pepdate\\pep" + pepNumber + "_"+messageCounter + "_"+date+".txt");
        FileWriter fw = new FileWriter(f);	
		
		fw.write(processedMessage);
        //fw.write("Page 2 after page break char");
		fw.close();
	}
	
	public static void writeAllPepMesagesToFile(Connection conn) throws SQLException{
		/*
		String sql = "(SELECT subject,senderName,date2, analyseWords from allmessages WHERE            pep = " + pepNumber + " order by date2) UNION "  //there will be many rows since there is no seprate table to store this information
				+    "(SELECT subject,senderName,date2, analyseWords from allpeps_ideaspeptitles WHERE pep = " + pepNumber + " order by date2) ";
		*/
		String sql = "(SELECT subject,senderName,date2, analyseWords from allmessages WHERE pep = " + pepNumber + " order by datetimestamp) ";
		String subject =null;
		String senderName =null;
		String processedMessage =null; 
		Date date =null;
		
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery( sql );
        
        Integer messageCounter=0;
        
        try{
        	 while (rs.next())
             {
				 //Thread.sleep(500);				 
        		 subject = rs.getString(1); 
        		 senderName = rs.getString(2);
        		 date = rs.getDate(3); 
        		 processedMessage = rs.getString(4); 
        		
        		 processedMessage = "Subject: " + subject + "\n" + "From: " +senderName + "\n" + "Date: " + date + "\n" +  processedMessage;
        		 
				 writeMessageToSepratePage(processedMessage, messageCounter,date);
				 messageCounter++;
             }
        }
        catch (SQLException e)
        {
            System.out.println( e.getMessage() );
        }
        catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
