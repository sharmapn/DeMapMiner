package GUI.helpers2;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;

public class OutputAllMessagesForASpecifiedPEP
{
	
   public static void main(String [] args) throws IOException
   {
	   	// a jframe here isn't strictly necessary, but it makes the example a
		// little more real
		JFrame frame = new JFrame("Output All Messages For A Specified PEP");
		// prompt the user to enter their name
		String name = JOptionPane.showInputDialog(frame, "Enter PEP Number");
		int pepNum = Integer.parseInt(name);
		getAllPEPMessages(pepNum);
   }
   
   public static void getAllPEPMessages(Integer v_PEP) throws IOException {
	    
   	//Integer v_pep = 451;
		   FileWriter writer = new FileWriter("c:\\scripts\\pep" + v_PEP + ".csv");
       final String JDBC_DRIVER = "com.mysql.jdbc.Driver";   
       try {
           Class.forName("com.mysql.jdbc.Driver").newInstance();
         } catch (Exception e) {
           System.err.println("Unable to find and load driver");
           System.exit(1);
         }
       
       Integer pepNumber = v_PEP;
       String Message;
       String author;
       String v_date;
       String statusFrom;
       String statusTo;
       String status;
       Matcher m5 = null;
       Matcher mw = null;
       String wordsFoundList = null;
       
       //  Connect to an MySQL Database, run query, get result set
       String url = "jdbc:mysql://localhost:3306/peps";
       String userid = "root";
       String password = "root";
       String sql = "SELECT date2, sendername, analysewords, statusFrom, statusTo, statusChanged from allpeps WHERE pep = " + pepNumber + " order by date2;"; 
       //can add messageid later
       
       Integer counter =0;
       String newMessage = null;
       
       // Java SE 7 has try-with-resources
       // This will ensure that the sql objects are closed when the program 
       // is finished with them
       try (Connection connection = DriverManager.getConnection( url, userid, password );
           Statement stmt = connection.createStatement();
           ResultSet rs = stmt.executeQuery( sql ))
       {
       	 while (rs.next())
            {
       		counter++;
   	    	 //add chaild node
       		Message = rs.getString(3);
       		v_date = rs.getString(1);
       		author = rs.getString(2);
       		statusFrom = rs.getString(4);
       		statusTo = rs.getString(5);
       		status = rs.getString(6);
            wordsFoundList = null;       

            	System.out.println(v_date + ", " + author + "\n" );
            	System.out.println( Message + "\n" );
          //  else if (wordsFoundList !=null && status!=null)
          //  	System.out.println( v_date + ", " + author  + " , " + " " + wordsFoundList + " " + status);
            //write to csv file
            writer.append(v_date);
            writer.append(',');
    	    writer.append(',');
    	    writer.append(author);
		    writer.append('\n');
	          	    	 
            }
       	 System.out.println("PEP " + pepNumber + " has " + counter + " messages ");
       	 writer.flush();
		    writer.close();
       }
       catch (SQLException e)
       {
           System.out.println( e.getMessage() );
       }
       
   }
   
}
