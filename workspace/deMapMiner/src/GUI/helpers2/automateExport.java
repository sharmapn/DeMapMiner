package GUI.helpers2;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.process.DocumentPreprocessor;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.awt.*;
import java.awt.event.*;
import java.io.Reader;
import java.io.StringReader;
import java.sql.*;
import java.util.List;
import java.util.Vector;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class automateExport {

	public static void main(String[] args) {
		DBase db = new DBase();
		Connection conn = db.connect("jdbc:mysql://localhost:3306/peps",
				"root", "root");
		/*
		 * if (args.length != 1) { System.out.println(
		 * "Usage: java automateExport [outputfile path] "); return; }
		 */
		String filename = "c:\\scripts\\tblout.txt";
		// db.exportData(conn,filename);
		
		JFrame frame1;
	    JLabel l0, l1, l2;
	    JComboBox c1;
	    
	    JTextField firstSearchword, secondSearchword;
	    
	    JButton b1;
	    firstSearchword = new JTextField(20);
       // firstSearchword.add(new JLabel("ST "), "West");
        secondSearchword = new JTextField(20);
      //  secondSearchword.add(new JLabel("ST "), "West");
        b1 = new JButton("submit");
	    
        
        
		
		db.exportdata2();
	}

}

class DBase {
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost/peps";

	// Database credentials
	static final String USER = "root";
	static final String PASS = "root";

	public DBase() {
		
		
	}

	public Connection connect(String db_connect_str, String db_userid,
			String db_password) {
		Connection conn;
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(db_connect_str, db_userid,
					db_password);

		} catch (Exception e) {
			e.printStackTrace();
			conn = null;
		}
		return conn;
	}

	public void exportdata2() {
		//String s2 = "wrangling";
		//String s2 = "BDFL Pronouncement";
		String s1 = "please";
		String s2 = "vote";
		Statement stmt = null;
		Connection conn = null;
		String previousSentence = "";
		String currentSentence = "";
		String nextSentence = "";
		String v_message = "";
		String v_messageID = "";
		String cou = "";
		String[] sentences = new String[1000];
		String pepNumber;
		ResultSet rs;
		String output = "";
		
		Boolean foundinLastRound = false;

		FileWriter writer = null;
		try {
			writer = new FileWriter("c:\\scripts\\outer.txt");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			stmt = conn.createStatement();
			rs = stmt.executeQuery("select email, messageID, pep from allmessages where  email LIKE '%"
							+ s1 + "%' AND email LIKE '%"
							+ s2 + "%' order by pep");
			int sentenceCounter = 0;
			System.out.println("here tree 0" + rs.toString());
			while (rs.next()) {
				System.out.println("here tree 0");
				v_message = rs.getString(1);
				v_messageID = rs.getString(2);
				pepNumber = rs.getString(3);
				// model.addRow(new Object[]{ v_messageID});
				if (!(v_message == "null") || !(v_message == "") || !(v_message.isEmpty())) {
					System.out.println("here tree 00");
					try {
						if (v_message.length() > 0) {
							System.out.println("here tree 000");
							Reader reader = new StringReader(v_message);
							DocumentPreprocessor dp = new DocumentPreprocessor(reader);
							previousSentence = "";
							for (List<HasWord> eachSentence : dp) 
							{
								String sentenceString = Sentence.listToString(eachSentence);
								currentSentence = sentenceString;
								if (foundinLastRound == true) 
								{
									try 
									{
										writer.append("\n" + output + " ^ " + currentSentence);										
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}									
									foundinLastRound = false;
								}
								
								
								// sentences[sentenceCounter] = sentenceString;
								// if ( secondSearchword.getText().isEmpty()){
								System.out.println("here tree 1");
								if (sentenceString.contains(s2) && sentenceString.contains(s1) && !(sentenceString.contains("Lemburg")) ) 
								{
								//	previousSentence = sentenceString;
								//	if (foundinLastRound == true) 
								//	{
									output = pepNumber + " ^ " + v_messageID + " ^ " + previousSentence + " ^ " + currentSentence;
									//	
									//}
									foundinLastRound = true;
								}
								sentenceCounter++;
								previousSentence = currentSentence;
								
							} // end for
						} // end if
					} // end try
					finally { }
				} // end if
			} // end while
			if (sentenceCounter < 1) {
				JOptionPane.showMessageDialog(null, "No Record Found", "Error",
						JOptionPane.ERROR_MESSAGE);
			}
			if (sentenceCounter == 1) {
				System.out.println(sentenceCounter + " Record Found");
			} else {
				System.out.println(sentenceCounter + " Records Found");
			}
			try {
				writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (SQLException e) {

		}

		System.out
				.println("here tree c1 "
						+ "select email, messageID, pep from allmessages where  email LIKE '%"
						+ s2 + "%'");

	}

	public void exportData(Connection conn, String filename) {
		Statement stmt;
		String query, query2;
		String filename2 = "C:/Users/psharma/Google Drive/PhDOtago/scripts/outqrytest.txt";
		try {
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
					ResultSet.CONCUR_UPDATABLE);

			// For comma separated file
			query = "SELECT id,text,price into OUTFILE  '" + filename
					+ "' FIELDS TERMINATED BY ',' FROM testtable t";
			// firstSearchword.getText()
			String key = "wrangling";
			query2 = "select messageID, pep into OUTFILE  '"
					+ filename2
					+ "' FIELDS TERMINATED BY ',' from allmessages where  email LIKE '%"
					+ key + "%'";

			stmt.executeQuery(query2);

		} catch (Exception e) {
			e.printStackTrace();
			stmt = null;
		}
	}
};
