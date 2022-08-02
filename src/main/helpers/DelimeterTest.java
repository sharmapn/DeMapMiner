package main.helpers;

import java.awt.*;
import java.sql.*;
import java.util.*;

import javax.swing.*;
import javax.swing.table.*;

import connections.MysqlConnect;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DelimeterTest extends JFrame
{	
	public static Connection conn;
    public static void main(String[] args) {
    	MysqlConnect my = new MysqlConnect();    	
    	conn= my.connect();
    	int mid = 3005449; //327811;
    	String main = "";
    	
    	JFrame frame = new JFrame("DropDemo");
        //frame.add(new JLabel("enter code"));
        JTextArea area=new JTextArea();         area.setBounds(100,100, 600,600);
        JScrollPane js=new JScrollPane(area);		js.setVisible(true);
        frame.add(js);          frame.setSize(300,300);     	
          
    	Statement stmt = null;	ResultSet rs;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery("select email,messageid from allmessages where  messageID = " + mid);
			int sentenceCounter = 0;
			//				System.out.println("here tree 0" + rs.toString());
			while (rs.next()) {
				//					System.out.println("here tree 0");
				String v_message = rs.getString(1);
				int v_mid 		 = rs.getInt(2);
				
				String delimeters[] = {"\r?\n","\n\n","\r?\n+\r?\n","\r?\n-","\r?\n>","\r?\n >","\r?\n>>","\r?\n> >"};						String del = "\r?\n";
				
				//replace delimeters with new delimeter  
				
				/*for(String p: delimeters){
					if(v_message.contains(p)) {	//fileStr
						v_message = v_message.replaceAll("\\["+p+"\\]",del);
						
					//System.out.println(test);
					}
				} */
				//we want to cater for all line contain only plus sign +, and these we replace with newline
				main = strip(main, v_message);				
				//main = v_message;
				
				area.setText(main);
				System.out.println(main);
			}
		}
		catch (SQLException e) {

		}
        
		frame.setDefaultCloseOperation( EXIT_ON_CLOSE );
		frame.setLocation(800,600);
		frame.pack();
		frame.setVisible(true);
    }
	public static String strip(String main, String v_message) {
		//FINALLY DECIDED WE JUST STRIP THESE CHARACTERS OFF..BEST SOLUTION.
		v_message = v_message.replaceAll("\r?\n\\+","\r\n");
		v_message = v_message.replaceAll("\r?\n\\-","\r\n");
		v_message = v_message.replaceAll("\r?\n>","\r\n");
		v_message = v_message.replaceAll("\r?\n >","\r\n");
		v_message = v_message.replaceAll("\r?\n>>","\r\n");
		v_message = v_message.replaceAll("\r?\n> >","\r\n");
		v_message = v_message.replaceAll("\r?\n ","\r\n");
		
		//v_message = v_message.replaceAll("\r?\n ","pankaj");
		/* for(String section: v_message.split("\\r?\\n")){	//	"\r?\n|\r"
			if (section.equals("+"))
			{	main = main + "\\r?\\n";				}
			else {
				main += section;
			}
		} */
		//"\\r?\\n"); //(("(?:\r\n?|\n){2}")); // System.getProperty(line.Separator));  //"\\n\\n");	//("(\\n\\r|\\n|\\r){2}"); //("\\n\\n");  //"\r?\n\r?\n");
		String[] paragraphs = v_message.split("\\r?\\n\\r?\\n"); 
		int p=0;
		for (String g: paragraphs)	{
			main= main + g + "{p:"+p+"}" + "\n\n";
			p++;
		} 
		
		return main; //v_message;
	}
}