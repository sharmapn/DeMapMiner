package main.helpers;

import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Highlighter;

import connections.MysqlConnect;
import utilities.UnderlineHighlighter;
import utilities.WordSearcher;

public class ShowMessage {

	static String first;
	static MysqlConnect mc = new MysqlConnect();
	static Connection connection = mc.connect();
	static String v_message = "", v_subject;

	public static void ShowMessageForID(Integer v_mid){
		Statement stmt = null;			
		ResultSet rs;

		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery("select email,subject from allmessages where  messageID = " + v_mid);
			int sentenceCounter = 0;
			//				System.out.println("here tree 0" + rs.toString());
			while (rs.next()) {
				//					System.out.println("here tree 0");
				v_message = rs.getString(1);
				v_subject = rs.getString(2);
			}
		}
		catch (SQLException e) {

		}

		JFrame frame = new JFrame(v_subject);
		//    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		FlowLayout flowLayout = new FlowLayout();
		frame.getContentPane().setLayout(flowLayout);
		JTextArea txtArea = new JTextArea(v_message, 50,50);	       
		JScrollPane scroll = new JScrollPane(txtArea);
		
		//find 
		JTextField findField = null;
        JButton findButton = null;
        JTextArea textArea = null;
        int pos = 0;
		

		frame.getContentPane().add(scroll);
		//  txtArea.setCaretPosition(LocOfSearchWord);

		frame.getContentPane().setComponentOrientation(ComponentOrientation.UNKNOWN);

		frame.setSize(300,400);
		frame.pack();
		frame.setVisible(true);

		//search for the words in mysql column ..like vote, etc

		//get text from keyword box
		Highlighter highlighter = new UnderlineHighlighter(null);
		final JTextPane textPane = new JTextPane();
		textPane.setHighlighter(highlighter);
		final WordSearcher searcher = new WordSearcher(txtArea, Color.red);
		int offset = searcher.search(first);
		if (offset != -1) {
			try {
				scroll.scrollRectToVisible(textPane.modelToView(offset));
			} catch (BadLocationException e) {
			}
		}	

		//find
		
		
	}
}
