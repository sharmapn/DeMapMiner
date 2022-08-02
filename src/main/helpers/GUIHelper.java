package main.helpers;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.text.BadLocationException;

import Read.readRepository.DateDemo;
import main.stillToSort.GUIGenerateCsv;
import utilities.WordSearcher;

public class GUIHelper {
	
	public int returnRowCount(String v_statement){
		  final String JDBC_DRIVER = "com.mysql.jdbc.Driver"; 
	      try {
	          Class.forName("com.mysql.jdbc.Driver").newInstance();
	       } 
	      catch (Exception e) {
	          System.err.println("Unable to find and load driver");
	          System.exit(1);
	        }
	      //  Connect to an MySQL Database, run query, get result set
	      String url = "jdbc:mysql://localhost:3306/peps_new";
	      String userid = "root";
	      String password = "root";
	      String sql = v_statement;
	      int totalRows = 0;
	      
	      try (Connection connection = DriverManager.getConnection( url, userid, password );
	              Statement stmt = connection.createStatement();
	              ResultSet rs = stmt.executeQuery( sql ))
	       {	
	    	  rs.last();
	          totalRows = rs.getRow();
	          rs.beforeFirst();
		   } 
	      catch(Exception ex)  {
	          return 0;
	      }
	      return totalRows ;  
	  }
	
	//I am sure this 2 functions are never used
	
	public Dimension getPreferredSize(){
		return new Dimension(200, 200);
	}

	public void DisplayWordsList(Integer v_pep,  JTable v_table)
	  {
	  	//Integer v_pep = 451;
	  	
	  	ArrayList columnNames = new ArrayList();
	      ArrayList data = new ArrayList();

	      final String JDBC_DRIVER = "com.mysql.jdbc.Driver";   
	      
	      try {
	          Class.forName("com.mysql.jdbc.Driver").newInstance();
	        } catch (Exception e) {
	          System.err.println("Unable to find and load driver");
	          System.exit(1);
	        }
	      
	      //  Connect to an MySQL Database, run query, get result set
	      String url = "jdbc:mysql://localhost:3306/peps_new";
	      String userid = "root";
	      String password = "root";
	      String sql = "select messageid, wordslist from allmessages where statusChanged = 1 AND PEP = " + v_pep + " order by date2;";
	      //can add messageid later
	      
	      // Java SE 7 has try-with-resources
	      // This will ensure that the sql objects are closed when the program 
	      // is finished with them
	      try (Connection connection = DriverManager.getConnection( url, userid, password );
	          Statement stmt = connection.createStatement();
	          ResultSet rs = stmt.executeQuery( sql ))
	      {
	          ResultSetMetaData md = rs.getMetaData();
	          int columns = md.getColumnCount();

	          //  Get column names
	          for (int i = 1; i <= columns; i++)
	          {
	              columnNames.add( md.getColumnName(i) );
	          }

	          //  Get row data
	          while (rs.next())
	          {
	              ArrayList row = new ArrayList(columns);

	              for (int i = 1; i <= columns; i++)
	              {
	                  row.add( rs.getObject(i) );
	              }

	              data.add( row );
	          }
	      }
	      catch (SQLException e)
	      {
	          System.out.println( e.getMessage() );
	      }

	      // Create Vectors and copy over elements from ArrayLists to them
	      // Vector is deprecated but I am using them in this example to keep 
	      // things simple - the best practice would be to create a custom defined
	      // class which inherits from the AbstractTableModel class
	      Vector columnNamesVector = new Vector();
	      Vector dataVector = new Vector();

	      for (int i = 0; i < data.size(); i++)
	      {
	          ArrayList subArray = (ArrayList)data.get(i);
	          Vector subVector = new Vector();
	          for (int j = 0; j < subArray.size(); j++)
	          {
	              subVector.add(subArray.get(j));
	          }
	          dataVector.add(subVector);
	      }

	      for (int i = 0; i < columnNames.size(); i++ )
	          columnNamesVector.add(columnNames.get(i));

	      //  Create table with database data    
	      v_table = new JTable(dataVector, columnNamesVector)
	      {
	          public Class getColumnClass(int column)
	          {
	              for (int row = 0; row < getRowCount(); row++)
	              {
	                  Object o = getValueAt(row, column);

	                  if (o != null)
	                  {
	                      return o.getClass();
	                  }
	              }

	              return Object.class;
	          }
	      };
	      
	      

	     // JPanel buttonPanel = new JPanel();
	     // getContentPane().add( buttonPanel, BorderLayout.SOUTH );
	      
	      //setDefaultCloseOperation( EXIT_ON_CLOSE );
	      //setLocation(800,600);
	      //pack();
	     // setVisible(true);

	     
	  }
	
	public void createPage1(JPanel panel1)
	{
		panel1 = new JPanel();
		panel1.setLayout( null );
       
		JLabel label1 = new JLabel( "identifier:" );
		label1.setBounds( 10, 15, 150, 20 );
		panel1.add( label1 );		
			    
	    //identifier
	    JTextField identifier = new JTextField("eg. PEP");
	    identifier.setBounds( 80, 15, 150, 20 );
		panel1.add( identifier );
		
		JLabel label2 = new JLabel( "mainFolder:" );
		label2.setBounds( 10, 60, 150, 20 );
		panel1.add( label2 );
	 
	    JTextField mainFolder = new JTextField("eg. c:\\datasets\\");
	    mainFolder.setBounds( 80, 60, 150, 20 );
		panel1.add( mainFolder );	   
	    
		JButton go = new JButton("Read Archive Folder");	
		go.setBounds( 10, 120, 150, 20 );
		panel1.add(go);
		
		go.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) { 
		    	DateDemo d = new DateDemo();
		    	d.main(null);
		    } 
		});
	    
	}
	
	public void createPage2(JPanel panel2)
	{
		panel2 = new JPanel();
		panel2.setLayout( new BorderLayout() );

		panel2.add( new JButton( "North" ), BorderLayout.NORTH );
		panel2.add( new JButton( "South" ), BorderLayout.SOUTH );
		panel2.add( new JButton( "East" ), BorderLayout.EAST );
		panel2.add( new JButton( "West" ), BorderLayout.WEST );
		panel2.add( new JButton( "Center" ), BorderLayout.CENTER );
	}

	public void createPage3(JPanel panel3)
	{
		panel3 = new JPanel();
		panel3.setLayout( new GridLayout( 3, 2 ) );

		panel3.add( new JLabel( "Field 1:" ) );
		panel3.add( new TextArea() );
		panel3.add( new JLabel( "Field 2:" ) );
		panel3.add( new TextArea() );
		panel3.add( new JLabel( "Field 3:" ) );
		panel3.add( new TextArea() );
	}
	
	public void createPage4()
	{
		  String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
		  String DB_URL = "jdbc:mysql://localhost/peps";	  
		  String USER = "root";
		  String PASS = "root";

		   JButton getAccountButton, nextButton, previousButton, lastButton,
		      			  firstButton, gotoButton, freeQueryButton, freeQueryButton2, searchWithDatesQueryButton, StatusChangedButton, classifyButton, changeButton, 
		      			  gotoMessageIdButton, SQLButton, FindWordsButton, FindWordsSameSentenceButton, locationQueryButton;

		   JList accountNumberList;

		   JTextField messageIDText, dateText = null, locationText = null, tsText = null, wordsText = null, gotoText, freeQueryText, PEPText = null, StatusText = null,
		  				  StatusTo, searchText, classificationText, rowCountText, messageIdText, DateFromText, DateToText;

		   JTextArea activeTSText, errorText, analyseWordsText, classificationMessage, classificationWords, SQLText;
		   Connection connection;
		   Statement statement;
		   ResultSet rs;  
		    Integer PEP_global;  
		  boolean folderChanged = false;  
		   JTree tree;  
		   JLabel selectedLabel = null;  
		   
//		   Container c = getContentPane();
//		    c.setLayout(new FlowLayout());   //new BorderLayout());
		    List<String> searchKeyList = new ArrayList<String>();	    
		    JTable wordListTable = null; //table to display words
		
//		    setGUIElementValues();
		    
		    JCheckBox statusCheck = new JCheckBox("", false);
		    JCheckBox addParameters = new JCheckBox("", false);
		    JCheckBox statusChangedCheck = new JCheckBox("", false);
		    //statusCheck.add(new JLabel("Status "), "West");
		
//$$		    exploreAnalyseMessages = new JPanel();
//$$		    exploreAnalyseMessages.setLayout(new GridLayout(7, 1));
		
		    messageIDText = new JTextField("");
		    
//$$		    exploreAnalyseMessages.add(messageIDText);
//		    second.add(dateText);
//		    second.add(locationText);
//		    second.add(tsText);
//		    second.add(PEPText);
//		    second.add(StatusText);
//		    second.add(wordsText);
	}
	
	/*
	 private JButton setValues3(JTable wordListTable, final WordSearcher searcher) {
		JButton classifyButton = new JButton("Classify");
		//rspButton.add(new JLabel("Status List "), "North");
		classifyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {       
				//previous way of doing - just pass to classifier and indicate in textfield what classification it belongs
				//          String textToClassify = activeTSText.getText();
				//          TextClassifier tc = new TextClassifier(textToClassify);

				//JOptionPane.showMessageDialog(null, tc.returnClassification(), "InfoBox: " + "Classification", JOptionPane.INFORMATION_MESSAGE);
				//         classificationText.setText(tc.returnClassification());

				//new way of doing once you enter a PEP number and press classify, it would show all mesages with that pep number mentioned
				//and it has the classification there as well
				//and it would write these to csv file as well
				Integer rowCount;
				try {

					//	if (folderChanged == true){
					//		rs = statement.executeQuery("SELECT * FROM "+messagesTableName+" WHERE PEP = " + Integer.parseInt(gotoText.getText()) + " AND folder LIKE '"+ selectedFolder + "' AND email LIKE '%" + searchText.getText()  + "%' AND statusChanged =1 order by date2, messageid asc, pep asc;");
					//	}

					rs = statement.executeQuery("SELECT * FROM "+messagesTableName+" WHERE PEP = " + Integer.parseInt(gotoText.getText()) + "  AND email LIKE '%" + searchText.getText()  + "%' order by date2, messageid asc, pep asc;");
					rowCount = guih.returnRowCount("SELECT * FROM "+messagesTableName+" WHERE PEP = " + Integer.parseInt(gotoText.getText()) + "  AND email LIKE '%" + searchText.getText()  + "%' order by date2, messageid asc, pep asc;");

					// if (freeQueryText.getText().toUpperCase().indexOf("SELECT") >= 0) {
					//  rs = statement.executeQuery(freeQueryText.getText());

					System.out.println("resultset rowCount = " + rowCount);  
					if (rs.next()) {
						messageIDText.setText(rs.getString("messageid"));
						PEPText.setText(rs.getString("PEP"));
						dateText.setText(rs.getString("date2"));
						locationText.setText(rs.getString("folder"));
						tsText.setText(rs.getString("file"));
						activeTSText.setText(rs.getString("email")); 
						analyseWordsText.setText(rs.getString("analysewords")); 
						wordsText.setText(rs.getString("wordslist"));
						rowCountText.setText(String.valueOf(rowCount));
						StatusText.setText("statusChanged: " + rs.getString("statusChanged") + " statusTo: " + rs.getString("statusTo"));
						activeTSText.setCaretPosition(0);
						analyseWordsText.setCaretPosition(0);
						//XXX                   classificationWords.setText()

					}
					//  } 
					//  else {
					int i = statement
							.executeUpdate(freeQueryText.getText()); 
					errorText.append("Rows affected = " + i);
					// loadAccounts();
					//    }
				} catch (SQLException insertException) {
					displaySQLErrors(insertException);
				}
				tf.setText("PEP " + PEPText.getText());

				word = tf.getText().trim();                       //get text from keyword box
				int offset = searcher.search(word);
				if (offset != -1) {
					try {
						textPane.scrollRectToVisible(textPane
								.modelToView(offset));
					} catch (BadLocationException g) {
					}
				}   

				searchWords = searchText.getText().trim();                       //get text from keyword box
				int offset2 = searcher.search(searchWords);
				if (offset2 != -1) {
					try {
						textPane.scrollRectToVisible(textPane
								.modelToView(offset));
					} catch (BadLocationException g) {
					}
				}   

				PEP_global = Integer.parseInt(gotoText.getText()); 
				//$$             DisplayWordsList(PEP_global,  wordListTable);


				//show classification
				String textToClassify = activeTSText.getText();
//				          TextClassifier tc = new TextClassifier(textToClassify);
//
//				//JOptionPane.showMessageDialog(null, tc.returnClassification(), "InfoBox: " + "Classification", JOptionPane.INFORMATION_MESSAGE);
//				//***          classificationText.setText(tc.returnClassification());
//
//				//Determine flow
//				/*
//           String flow = activeTSText.getText();
//           try {
//			   GUIGenerateCsv gcsv = new GUIGenerateCsv();
//			   //JOptionPane.showMessageDialog(null, tc.returnClassification(), "InfoBox: " + "Classification", JOptionPane.INFORMATION_MESSAGE);
//			   Integer a = Integer.parseInt(gotoText.getText());         
//			   classificationWords.setText(GUIGenerateCsv.getAllPEPMessages(a));
//           } 
//           catch (IOException e1) {
//        	   // TODO Auto-generated catch block
//        	   e1.printStackTrace();
//           } 
				        Integer a;
				 String b;
				 try {
					 GUIGenerateCsv gcsv = new GUIGenerateCsv();
					 a = Integer.parseInt(gotoText.getText()); 
					 // classificationWords.setText(GUIGenerateCsv.getAllPEPMessages(a));
					 b = GUIGenerateCsv.getAllPEPMessages(a); 
					 System.out.println(b);  
					 //classificationWords.setText("test");
					 classificationWords.setText(b);
					 classificationWords.setLineWrap(true);
					 Font font = new Font("defaultFont", Font.PLAIN , 9);
					 classificationWords.setFont(font);
					 classificationWords.setCaretPosition(0);

				 } catch (IOException e1) {
					 // TODO Auto-generated catch block
					 e1.printStackTrace();
				 }


				 //------        TreeExample(PEP_global, tree, model );
				 //       TreeExample(PEP_global, tree);
				 //  setVisible(true);
				 //   model.reload();
				 //       tree.updateUI();

				 //     revalidate();

				 // pack();
				 //             /this.setVisible(true);
				 repaint();
				 //  System.out.println("here tree");



			}
		});
		return classifyButton;
	}
	 
	
	
	
	 * 
		..cut just after maintabbed.add ..line 295
		//    try {
		////pp		PEPStateTransitionStorylineFindRelationsReVerb pr = new PEPStateTransitionStorylineFindRelationsReVerb();
		////pp		 mainTabbedPane.add("PEP StoryLine", pr);
		//	} catch (IOException e1) {
		//		// TODO Auto-generated catch block
		//		e1.printStackTrace();
		//	}



		//XXXXX19-9-16 mainTabbedPane.add("ClausIE", cie);


		//***mainTabbedPane.add("ReVerb", reverb);
	 */
	@Override
	protected void finalize() throws Throwable
	{
		//        System.out.println("From Finalize Method, i = "+i);
	}
	 
	
}
