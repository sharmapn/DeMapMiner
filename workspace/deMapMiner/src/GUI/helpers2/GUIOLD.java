package GUI.helpers2;

/*

MySQL and Java Developer's Guide

Mark Matthews, Jim Cole, Joseph D. Gradecki
Publisher Wiley,
Published February 2003, 
ISBN 0471269239

*/


import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JCheckBox;
import javax.swing.JTree;





















//highlight example
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.io.IOException;

import javax.swing.JLabel;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Highlighter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;

import GUI.helpers.DisplayNormativeStates;
import miner.models.CustomUserObject;
import utilities.UnderlineHighlighter;
import utilities.WordSearcher;

public class GUIOLD extends JFrame {
	
	  static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	  static final String DB_URL = "jdbc:mysql://localhost/peps";
	  
	  static final String USER = "root";
	  static final String PASS = "root";

  private JButton getAccountButton, insertAccountButton, deleteAccountButton,
      updateAccountButton, nextButton, previousButton, lastButton,
      firstButton, gotoButton, freeQueryButton, freeQueryButton2,  StatusChangedButton, classifyButton, changeButton, gotoMessageIdButton;

  private JList accountNumberList;

  private JTextField accountIDText, messageIDText, dateText, locationText, tsText, wordsText,
      gotoText, freeQueryText, PEPText, StatusText, StatusTo, searchText, classificationText, rowCountText, messageIdText;

  private JTextArea activeTSText, errorText, analyseWordsText, classificationMessage, classificationWords;

  private Connection connection;

  private Statement statement;

  private ResultSet rs;
  
  private static Integer PEP_global;
  
  boolean folderChanged = false;
  
  private JTree tree;
  
  private JLabel selectedLabel = null;
  
  public GUIOLD() {
    try {
      Class.forName("com.mysql.jdbc.Driver").newInstance();
    } catch (Exception e) {
      System.err.println("Unable to find and load driver");
      System.exit(1);
    }
  }
  
  private void init() {
	    connectToDB();
	  }

  public static void main(String[] args) {
    GUIOLD accounts = new GUIOLD();

    accounts.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        System.exit(0);
      }
    });

    accounts.init();
    accounts.buildGUI();
    
  }
  
  
  private static class ButtonHandler implements ActionListener {
      public void actionPerformed(ActionEvent e) {
        // System.exit(0);
    	 // DisplayNormativeStates dns = new DisplayNormativeStates(451);   
    	  DisplayNormativeStates dns = new DisplayNormativeStates(PEP_global);
         // dns.main(PEP_global);
      }
   }
/*
  private void loadAccounts() {
    Vector v = new Vector();
    try {
      rs = statement.executeQuery("SELECT PEP, id, folder, file, date2, location, line, email, statusTo, statusChanged FROM peps.allpeps order by messageid asc, pep asc");

      while (rs.next()) {
        v.addElement(rs.getString("id"));
      }
    } catch (SQLException e) {
      displaySQLErrors(e);
    }
    accountNumberList.setListData(v);
  }
  
  private void loadStatusChanges() {
	    Vector v = new Vector();
	    try {
	      rs = statement.executeQuery("SELECT PEP, id, folder, file, date2, location, line, email, statusTo, statusChanged FROM peps.results2 WHERE order by messageid asc, pep asc");

	      while (rs.next()) {
	        v.addElement(rs.getString("id"));
	      }
	    } catch (SQLException e) {
	      displaySQLErrors(e);
	    }
	    accountNumberList.setListData(v);
	  }
	  
	  
*/
  JPanel third = new JPanel(new BorderLayout());  
  final JTextField tf = new JTextField(20);
  final JTextPane textPane = new JTextPane();
  
  private void buildGUI() {
    Container c = getContentPane();
    c.setLayout(new FlowLayout());
    
   // JScrollPane scroll = new JScrollPane( c );
   // setContentPane( scroll );
    
    List<String> searchKeyList = new ArrayList<String>();
    
    JTable wordListTable = null; //table to display words

    accountNumberList = new JList();
 //   loadAccounts();
    accountNumberList.setVisibleRowCount(2);
    JScrollPane accountNumberListScrollPane = new JScrollPane(accountNumberList);
    gotoText = new JTextField(3);
    freeQueryText = new JTextField(40);
   
    searchText = new JTextField(10);
    searchText.add(new JLabel("ST "), "West");
    
    classificationText =  new JTextField(15);
    classificationText.add(new JLabel("C "), "West");
    
    
    
//	JPanel first = new JPanel(new GridLayout(5, 1));
   // first.add(accountNumberListScrollPane);
//    first.add(getAccountButton);
   // first.add(insertAccountButton);
    //first.add(deleteAccountButton);
    //first.add(updateAccountButton);
    
    accountIDText = new JTextField(50);
    messageIDText = new JTextField(50);
    dateText = new JTextField(50);
    locationText = new JTextField(50);
    tsText = new JTextField(50);
    PEPText = new JTextField(50);
    StatusText = new JTextField(50);
    StatusTo = new JTextField(20);
    wordsText = new JTextField(50);
    rowCountText = new JTextField(10);
    messageIdText = new JTextField(10);
    //activeTSText = new JTextField(20);
    activeTSText = new JTextArea(60, 70);
    activeTSText.setLineWrap(true);
    activeTSText.setEditable(false);
    Font font = new Font("defaultFont", Font.PLAIN , 10);
    activeTSText.setFont(font);
    
    //analyse words textfield
    analyseWordsText = new JTextArea(60, 70);
    analyseWordsText.setLineWrap(true);
    analyseWordsText.setEditable(false);
    //Font font = new Font("defaultFont", Font.PLAIN , 10);
    analyseWordsText.setFont(font);
    
    classificationMessage = new JTextArea(5, 5);
    classificationWords = new JTextArea(60, 35);
    
    JCheckBox statusCheck = new JCheckBox("", false);
    JCheckBox addParameters = new JCheckBox("", false);
    JCheckBox statusChangedCheck = new JCheckBox("", false);
    //statusCheck.add(new JLabel("Status "), "West");
    
    errorText = new JTextArea(5, 15);
    errorText.setEditable(false);

    JPanel second = new JPanel();
    second.setLayout(new GridLayout(7, 1));
    

    
    
    //test multiline
 //   JTextArea taText = new JTextArea();
  //  taText.setText("\tHI\nMY name\nis Raku");
   //scrollpane
 //   JScrollPane scrollPane = new JScrollPane(activeTSText);
  //  setPreferredSize(new Dimension(600, 600));
  //  add(scrollPane, BorderLayout.CENTER);

   // second.add(accountIDText);
    second.add(messageIDText);
    second.add(dateText);
    second.add(locationText);
    second.add(tsText);
    second.add(PEPText);
    second.add(StatusText);
    second.add(wordsText);
   
 //   second.add(statusCheck);
    
  //  second.add(StatusTo);
 //   second.add(activeTSText);

//    JPanel third = new JPanel();
//    third.add(new JScrollPane(errorText));

 //   fourth.add(taText);                 //added
//    fourth.add(freeQueryText);
   // JPanel fifth = new JPanel();
   // fifth.add(freeQueryText);

   // c.add(fourth);
    //add to c
    //c.add(pane);
//  c.add(fifth);
    //c.add(freeQueryButton);
 //   c.add(first);
    //c.add(third);
   // c.add(second);
//    c.add(third);
    
    //wordhighlighter
    try {
        UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
    } catch (Exception evt) {}
  
  //  JFrame f = new JFrame("Highlight example");
//    final JTextPane textPane = new JTextPane();
    textPane.setHighlighter(highlighter);
    JPanel pane = new JPanel();
    pane.setLayout(new BorderLayout());
    pane.add(new JLabel("Enter word: "), "North");
 //   final JTextField tf = new JTextField(20);
    pane.add(tf, "Center");   //enter keyword
    
   // pane.add(textPane, "Center");  
  //  f.getContentPane().add(pane, "South");
  //  f.getContentPane().add(new JScrollPane(textPane), "Center");

    //add to c
  //  c.add(pane);
    /* 
    try {
      textPane.read(new FileReader("c:\\scripts\\content.txt"), null);
    } catch (Exception e) {
      System.out.println("Failed to load file " );
      System.out.println(e);
    }
    */
    //final WordSearcher searcher = new WordSearcher(textPane);
    final WordSearcher searcher = new WordSearcher(activeTSText, Color.red);

    tf.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        word = tf.getText().trim();                       //get text from keyword box
        int offset = searcher.search(word);
        if (offset != -1) {
          try {
            textPane.scrollRectToVisible(textPane
                .modelToView(offset));
          } catch (BadLocationException e) {
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
        
      }
    });
    
    //search for the words in mysql column ..like vote, etc
    searchText.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
          searchWords = tf.getText().trim();                       //get text from keyword box
          int offset = searcher.search(searchWords);
          if (offset != -1) {
            try {
              textPane.scrollRectToVisible(textPane
                  .modelToView(offset));
            } catch (BadLocationException e) {
            }
          }
          
          
          
        }
      });
    
    //search for the words in mysql column ..like vote, etc
    /*
    classifyButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
        	TextClassifier tc = new TextClassifier("vote for me");                      //get text from keyword box
         // int offset = searcher.search(searchWords);
         
          
          
          
        }
      });

*/
    
    textPane.getDocument().addDocumentListener(new DocumentListener() {
      public void insertUpdate(DocumentEvent evt) {
        searcher.search(word);
      }

      public void removeUpdate(DocumentEvent evt) {
        searcher.search(word);
      }

      public void changedUpdate(DocumentEvent evt) {
      }
    });
    
    
    //
    //Do Get Account Button
    getAccountButton = new JButton("Get Account");
    getAccountButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        try {
          rs.first();
          while (rs.next()) {
            if (rs.getString("id").equals(
                accountNumberList.getSelectedValue()))
              break;
          }
          if (!rs.isAfterLast()) {
            accountIDText.setText("Account ID " + rs.getString("id"));
            messageIDText.setText("Message ID " + rs.getString("messageid"));
            PEPText.setText("PEP " + rs.getString("PEP"));
            dateText.setText("Date " + rs.getString("date2"));
            locationText.setText("Loc " + rs.getString("folder"));
            tsText.setText("Line:" + rs.getString("line"));
            activeTSText.setText(rs.getString("email"));
            analyseWordsText.setText(rs.getString("analysewords")); 
            wordsText.setText(rs.getString("wordslist"));
            StatusText.setText("statusChanged: " + rs.getString("statusChanged") + " statusTo: " + rs.getString("statusTo"));
          //  StatusTo.setText("statusTo: " + rs.getString( "statusTo"));
          }
        } catch (SQLException selectException) {
          displaySQLErrors(selectException);
        }
      }
    });
   
    //Do Next Button
    nextButton = new JButton(">");
    nextButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        try {
          if (!rs.isLast()) {
            rs.next();
            accountIDText.setText("Account ID " + rs.getString("id"));
            messageIDText.setText("Message ID " + rs.getString("messageid"));
            PEPText.setText("PEP " + rs.getString("PEP"));
            dateText.setText("Date " + rs.getString("date2"));
            locationText.setText("Loc " + rs.getString("folder"));
            tsText.setText("Line:" + rs.getString("line"));
            activeTSText.setText(rs.getString("email"));
            analyseWordsText.setText(rs.getString("analysewords")); 
            wordsText.setText(rs.getString("wordslist"));
            StatusText.setText("statusChanged: " + rs.getString("statusChanged") + " statusTo: " + rs.getString("statusTo"));
            //  StatusTo.setText("statusTo: " + rs.getString( "statusTo"));
            tf.setText(PEPText.getText());
            //tf.update(null);
            activeTSText.setCaretPosition(0);
            analyseWordsText.setCaretPosition(0);
          }
        } catch (SQLException insertException) {
          displaySQLErrors(insertException);
        }    	
       
        String i = PEPText.getText();
        tf.setText(i);
    
        searchKeyList.add("PEP" + ": " + i);
	    searchKeyList.add("PEP " + i + " ");
	    searchKeyList.add("PEP " + i + "\\?");
	    searchKeyList.add("PEP " + i + "\\."); 
	    searchKeyList.add("PEP " + i + "\\,"); 
	    searchKeyList.add("PEP " + i + "\\;");
        
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

        //classify text
        String textToClassify = activeTSText.getText();
//***        TextClassifier tc = new TextClassifier(textToClassify);        
        //JOptionPane.showMessageDialog(null, tc.returnClassification(), "InfoBox: " + "Classification", JOptionPane.INFORMATION_MESSAGE);
//**        classificationText.setText(tc.returnClassification());        
      }
    });

    
  //Do Next Button
    previousButton = new JButton("<");
    previousButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        try {
          if (!rs.isFirst()) {
            rs.previous();
            accountIDText.setText("Account ID " + rs.getString("id"));
            messageIDText.setText("Message ID " + rs.getString("messageid"));
            PEPText.setText("PEP " + rs.getString("PEP"));
            dateText.setText("Date " + rs.getString("date2"));
            locationText.setText("Loc " + rs.getString("folder"));
            tsText.setText("Line:" + rs.getString("line"));
            activeTSText.setText(rs.getString("email"));
            analyseWordsText.setText(rs.getString("analysewords")); 
            wordsText.setText(rs.getString("wordslist"));
            StatusText.setText("statusChanged: " + rs.getString("statusChanged") + " statusTo: " + rs.getString("statusTo"));
            //  StatusTo.setText("statusTo: " + rs.getString( "statusTo"));
            activeTSText.setCaretPosition(0);
            analyseWordsText.setCaretPosition(0);
          }
        } catch (SQLException insertException) {
          displaySQLErrors(insertException);
        }
    tf.setText(PEPText.getText());
        
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
        
        //classify text
        String textToClassify = activeTSText.getText();
//**        TextClassifier tc = new TextClassifier(textToClassify);        
        //JOptionPane.showMessageDialog(null, tc.returnClassification(), "InfoBox: " + "Classification", JOptionPane.INFORMATION_MESSAGE);
//**        classificationText.setText(tc.returnClassification());  
      }
    });

    //Do last Button
    lastButton = new JButton(">|");
    lastButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        try {
          rs.last();
          accountIDText.setText("Account ID " + rs.getString("id"));
          messageIDText.setText("Message ID " + rs.getString("messageid"));
          PEPText.setText("PEP " + rs.getString("PEP"));
          dateText.setText("Date " + rs.getString("date2"));
          locationText.setText("Loc " + rs.getString("folder"));
          tsText.setText("Line:" + rs.getString("line"));
          activeTSText.setText(rs.getString("email"));
          analyseWordsText.setText(rs.getString("analysewords")); 
          wordsText.setText(rs.getString("wordslist"));
          StatusText.setText("statusChanged: " + rs.getString("statusChanged") + " statusTo: " + rs.getString("statusTo"));
          //  StatusTo.setText("statusTo: " + rs.getString( "statusTo"));
          activeTSText.setCaretPosition(0);
          analyseWordsText.setCaretPosition(0);
        } catch (SQLException insertException) {
          displaySQLErrors(insertException);
        }
      }
    });

    //Do first Button
    firstButton = new JButton("|<");
    firstButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        try {
          rs.first();
          accountIDText.setText("Account ID " + rs.getString("id"));
          messageIDText.setText("Message ID " + rs.getString("messageid"));
          PEPText.setText("PEP " + rs.getString("PEP"));
          dateText.setText("Date " + rs.getString("date2"));
          locationText.setText("Loc " + rs.getString("folder"));
          tsText.setText("Line:" + rs.getString("line"));
          activeTSText.setText(rs.getString("email"));
          analyseWordsText.setText(rs.getString("analysewords")); 
          wordsText.setText(rs.getString("wordslist"));
          StatusText.setText("statusChanged: " + rs.getString("statusChanged") + " statusTo: " + rs.getString("statusTo"));
          //  StatusTo.setText("statusTo: " + rs.getString( "statusTo"));
          activeTSText.setCaretPosition(0);
          analyseWordsText.setCaretPosition(0);
        } catch (SQLException insertException) {
          displaySQLErrors(insertException);
        }
      }
    });
/*
    //Do gotoButton
    gotoButton = new JButton("Goto");
    gotoButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        try {
          rs.absolute(Integer.parseInt(gotoText.getText()));
          accountIDText.setText("Account ID " + rs.getString("id"));
          messageIDText.setText("Message ID " + rs.getString("messageid"));
          PEPText.setText("PEP " + rs.getString("PEP"));
          dateText.setText("Date " + rs.getString("date2"));
          locationText.setText("Loc " + rs.getString("folder"));
          tsText.setText("Line:" + rs.getString("line"));
          activeTSText.setText(rs.getString("email"));
          StatusText.setText("statusChanged: " + rs.getString("statusChanged") + " statusTo: " + rs.getString("statusTo"));
          //  StatusTo.setText("statusTo: " + rs.getString( "statusTo"));
        } catch (SQLException insertException) {
          displaySQLErrors(insertException);
        }
      }
    });
    
 */ 
    String[] folders = new String[] {"python-dev", "python-lists",
            "python-ideas","distutils", "python-committers","python-announce-list", "python-checkins"};
    JComboBox<String> location = new JComboBox<>(folders);
    //add to the parent container (e.g. a JFrame):    
    //get the selected item:
    String selectedFolder = (String) location.getSelectedItem();
    //System.out.println("You seleted the book: " + selectedBook);    
    location.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {          
          String selectedFolder = (String) location.getSelectedItem();
          //TextClassifier tc = new TextClassifier(textToClassify);
          folderChanged = true;
          //update();
          //create and excute query here          
          //JOptionPane.showMessageDialog(null, tc.returnClassification(), "InfoBox: " + "Classification", JOptionPane.INFORMATION_MESSAGE);
          //classificationText.setText(tc.returnClassification());
        }
      });    
    
    String[] statusFrom = new String[] {"Draft","Open","Active","Pending","Closed","Final","Accepted","Deferred","Replaced","Rejected","Postponed","Incomplete","Superseded"};    
    JComboBox<String> sfromCBox = new JComboBox<>(statusFrom);
    //add to the parent container (e.g. a JFrame):    
    //get the selected item:
    String selectedsFrom = (String) sfromCBox.getSelectedItem();
    //System.out.println("You seleted the book: " + selectedBook);    
    sfromCBox.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {          
          String selectedsFrom = (String) sfromCBox.getSelectedItem();
         // update();
          //TextClassifier tc = new TextClassifier(textToClassify);          
          //create and excute query here          
          //JOptionPane.showMessageDialog(null, tc.returnClassification(), "InfoBox: " + "Classification", JOptionPane.INFORMATION_MESSAGE);
          //classificationText.setText(tc.returnClassification());
        }
      });
    
    String[] statusTo = new String[] {"Draft","Open","Active","Pending","Closed","Final","Accepted","Deferred","Replaced","Rejected","Postponed","Incomplete","Superseded"};
    JComboBox<String> sToCBox = new JComboBox<>(statusTo);
    //add to the parent container (e.g. a JFrame):    
    //get the selected item:
    String selectedsTo = (String) sToCBox.getSelectedItem();
    //System.out.println("You seleted the book: " + selectedBook);    
    sToCBox.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {          
          String selectedsTo = (String) sToCBox.getSelectedItem();
          //TextClassifier tc = new TextClassifier(textToClassify);          
          //create and excute query here          
          //JOptionPane.showMessageDialog(null, tc.returnClassification(), "InfoBox: " + "Classification", JOptionPane.INFORMATION_MESSAGE);
          //classificationText.setText(tc.returnClassification());
        }
      });    
    
    //Do freeQueryButton
    freeQueryButton = new JButton("Search");
    freeQueryButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
    	  int rowCount = 0;
        try {
        	if(statusChangedCheck.isSelected()){  //check to see if we want to see all retrieved messages where status has changed 
        		//rs = statement.executeQuery("SELECT * FROM peps.allpeps WHERE PEP = " + Integer.parseInt(gotoText.getText()) + " AND email LIKE '%" + searchText.getText()  + "%' AND statusChanged =1 order by date2, messageid asc, pep asc;");
        		rs = statement.executeQuery("SELECT * FROM peps.allpeps WHERE email statusChanged =1  AND statusTO LIKE '%" + (String) sToCBox.getSelectedItem() + "%' and ststusFrom LIKE '%" + (String) sfromCBox.getSelectedItem() + "%' order by date2, pep asc;");
        		rowCount = returnRowCount("SELECT * FROM peps.allpeps WHERE email statusChanged =1  AND statusTO LIKE '%" + (String) sToCBox.getSelectedItem() + "%' and ststusFrom LIKE '%" + (String) sfromCBox.getSelectedItem() + "%' order by date2, pep asc; ");
        	}
        	
        	else if(statusCheck.isSelected()){  //check to see if we want to see all retrieved messages where status has changed 
        		rs = statement.executeQuery("SELECT * FROM peps.allpeps WHERE PEP = " + Integer.parseInt(gotoText.getText()) + " AND email LIKE '%" + searchText.getText()  + "%' AND statusChanged =1 order by date2, messageid asc, pep asc;");
        	
        		if(addParameters.isSelected()){  //check to see if we want to see all retrieved messages where status has changed 
        			String temp = (String) location.getSelectedItem();
        			//JOptionPane.showMessageDialog(null,"hello","TITLE",JOptionPane.WARNING_MESSAGE);
        			rs = statement.executeQuery("SELECT * FROM peps.allpeps WHERE PEP = " + Integer.parseInt(gotoText.getText()) + " AND email LIKE '%" + searchText.getText()  + "%' AND statusChanged =1 AND folder LIKE '%" + (String) location.getSelectedItem() + "%' order by date2, messageid asc, pep asc;");
        			//JOptionPane.showMessageDialog(null,statement.toString(),"TITLE",JOptionPane.WARNING_MESSAGE);
        			rowCount = returnRowCount("SELECT * FROM peps.allpeps WHERE PEP = " + Integer.parseInt(gotoText.getText()) + " AND email LIKE '%" + searchText.getText()  + "%' AND statusChanged =1 AND folder LIKE '%" + (String) location.getSelectedItem() + "%' order by date2, messageid asc, pep asc;");
        			System.out.println(temp);
        		}
        	}
        //	if (folderChanged == true){
        //		rs = statement.executeQuery("SELECT * FROM peps.allpeps WHERE PEP = " + Integer.parseInt(gotoText.getText()) + " AND folder LIKE '"+ selectedFolder + "' AND email LIKE '%" + searchText.getText()  + "%' AND statusChanged =1 order by date2, messageid asc, pep asc;");
        //	}
        	else{
        		rs = statement.executeQuery("SELECT * FROM peps.allpeps WHERE PEP = " + Integer.parseInt(gotoText.getText()) + "  AND email LIKE '%" + searchText.getText()  + "%' order by date2, messageid asc, pep asc;");
        		rowCount = returnRowCount("SELECT * FROM peps.allpeps WHERE PEP = " + Integer.parseInt(gotoText.getText()) + "  AND email LIKE '%" + searchText.getText()  + "%' order by date2, messageid asc, pep asc;");
        	}																													
         // if (freeQueryText.getText().toUpperCase().indexOf("SELECT") >= 0) {
          //  rs = statement.executeQuery(freeQueryText.getText());
        	
            System.out.println("resultset rowCount = " + rowCount);  
            if (rs.next()) {
              accountIDText.setText(rs.getString("id"));
              PEPText.setText(rs.getString("PEP"));
              dateText.setText(rs.getString("date"));
              locationText.setText(rs.getString("location"));
              tsText.setText(rs.getString("line"));
              activeTSText.setText(rs.getString("email")); 
              analyseWordsText.setText(rs.getString("analysewords")); 
              wordsText.setText(rs.getString("wordslist"));
              rowCountText.setText(String.valueOf(rowCount));
              StatusText.setText("statusChanged: " + rs.getString("statusChanged") + " statusTo: " + rs.getString("statusTo"));
              activeTSText.setCaretPosition(0);
              analyseWordsText.setCaretPosition(0);
              
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
        DisplayWordsList(PEP_global,  wordListTable);
//------        TreeExample(PEP_global, tree, model );
 //       TreeExample(PEP_global, tree);
      //  setVisible(true);
     //   model.reload();
 //       tree.updateUI();
        
   //     revalidate();
        
       // pack();
//        /this.setVisible(true);
        repaint();
      //  System.out.println("here tree");
        
      }
    });
    
  //Do freeQueryButton
    gotoMessageIdButton = new JButton("SearchMID");
    gotoMessageIdButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
    	  int rowCount = 0;
        try {
        	
        		//rs = statement.executeQuery("SELECT * FROM peps.allpeps WHERE PEP = " + Integer.parseInt(gotoText.getText()) + " AND email LIKE '%" + searchText.getText()  + "%' AND statusChanged =1 order by date2, messageid asc, pep asc;");
        		rs = statement.executeQuery("SELECT * FROM peps.allpeps WHERE messageID =  " + Integer.parseInt(messageIdText.getText()) + ";");
        		rowCount = returnRowCount("SELECT * FROM peps.allpeps WHERE messageID =  " + Integer.parseInt(messageIdText.getText()) + ";");
        	
        	if (rowCount ==0)
        		JOptionPane.showMessageDialog (null, "no record found");
        		
            System.out.println("resultset rowCount = " + rowCount);  
            if (rs.next()) {
              accountIDText.setText(rs.getString("id"));
              PEPText.setText(rs.getString("PEP"));
              dateText.setText(rs.getString("date"));
              locationText.setText(rs.getString("location"));
              tsText.setText(rs.getString("line"));
              activeTSText.setText(rs.getString("email")); 
              analyseWordsText.setText(rs.getString("analysewords")); 
              wordsText.setText(rs.getString("wordslist"));
              rowCountText.setText(String.valueOf(rowCount));
              StatusText.setText("statusChanged: " + rs.getString("statusChanged") + " statusTo: " + rs.getString("statusTo"));
              activeTSText.setCaretPosition(0);
              analyseWordsText.setCaretPosition(0);
              
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
        
 
        

       
        PEP_global = Integer.parseInt(gotoText.getText()); 
        DisplayWordsList(PEP_global,  wordListTable);
//------        TreeExample(PEP_global, tree, model );
 //       TreeExample(PEP_global, tree);
      //  setVisible(true);
     //   model.reload();
 //       tree.updateUI();
        
   //     revalidate();
        
       // pack();
//        /this.setVisible(true);
        repaint();
      //  System.out.println("here tree");
        
      }
    });
    
    
    freeQueryButton2 = new JButton("Search wTree");
    freeQueryButton2.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
    	  int rowCount = 0;
        try {
        	if(statusChangedCheck.isSelected()){  //check to see if we want to see all retrieved messages where status has changed 
        		//rs = statement.executeQuery("SELECT * FROM peps.allpeps WHERE PEP = " + Integer.parseInt(gotoText.getText()) + " AND email LIKE '%" + searchText.getText()  + "%' AND statusChanged =1 order by date2, messageid asc, pep asc;");
        		rs = statement.executeQuery("SELECT * FROM peps.allpeps WHERE email statusChanged =1  AND statusTO LIKE '%" + (String) sToCBox.getSelectedItem() + "%' and ststusFrom LIKE '%" + (String) sfromCBox.getSelectedItem() + "%' order by date2, pep asc;");
        		rowCount = returnRowCount("SELECT * FROM peps.allpeps WHERE email statusChanged =1  AND statusTO LIKE '%" + (String) sToCBox.getSelectedItem() + "%' and ststusFrom LIKE '%" + (String) sfromCBox.getSelectedItem() + "%' order by date2, pep asc; ");
        	}
        	
        	else if(statusCheck.isSelected()){  //check to see if we want to see all retrieved messages where status has changed 
        		rs = statement.executeQuery("SELECT * FROM peps.allpeps WHERE PEP = " + Integer.parseInt(gotoText.getText()) + " AND email LIKE '%" + searchText.getText()  + "%' AND statusChanged =1 order by date2, messageid asc, pep asc;");
        	
        		if(addParameters.isSelected()){  //check to see if we want to see all retrieved messages where status has changed 
        			String temp = (String) location.getSelectedItem();
        			//JOptionPane.showMessageDialog(null,"hello","TITLE",JOptionPane.WARNING_MESSAGE);
        			rs = statement.executeQuery("SELECT * FROM peps.allpeps WHERE PEP = " + Integer.parseInt(gotoText.getText()) + " AND email LIKE '%" + searchText.getText()  + "%' AND statusChanged =1 AND folder LIKE '%" + (String) location.getSelectedItem() + "%' order by date2, messageid asc, pep asc;");
        			//JOptionPane.showMessageDialog(null,statement.toString(),"TITLE",JOptionPane.WARNING_MESSAGE);
        			rowCount = returnRowCount("SELECT * FROM peps.allpeps WHERE PEP = " + Integer.parseInt(gotoText.getText()) + " AND email LIKE '%" + searchText.getText()  + "%' AND statusChanged =1 AND folder LIKE '%" + (String) location.getSelectedItem() + "%' order by date2, messageid asc, pep asc;");
        			System.out.println(temp);
        		}
        	}
        //	if (folderChanged == true){
        //		rs = statement.executeQuery("SELECT * FROM peps.allpeps WHERE PEP = " + Integer.parseInt(gotoText.getText()) + " AND folder LIKE '"+ selectedFolder + "' AND email LIKE '%" + searchText.getText()  + "%' AND statusChanged =1 order by date2, messageid asc, pep asc;");
        //	}
        	else{
        		rs = statement.executeQuery("SELECT * FROM peps.allpeps WHERE PEP = " + Integer.parseInt(gotoText.getText()) + "  AND email LIKE '%" + searchText.getText()  + "%' order by date2, messageid asc, pep asc;");
        		rowCount = returnRowCount("SELECT * FROM peps.allpeps WHERE PEP = " + Integer.parseInt(gotoText.getText()) + "  AND email LIKE '%" + searchText.getText()  + "%' order by date2, messageid asc, pep asc;");
        	}																													
         // if (freeQueryText.getText().toUpperCase().indexOf("SELECT") >= 0) {
          //  rs = statement.executeQuery(freeQueryText.getText());
        	  
            if (rs.next()) {
              accountIDText.setText(rs.getString("id"));
              PEPText.setText(rs.getString("PEP"));
              dateText.setText(rs.getString("date"));
              locationText.setText(rs.getString("location"));
              tsText.setText(rs.getString("line"));
              activeTSText.setText(rs.getString("email")); 
              analyseWordsText.setText(rs.getString("analysewords")); 
              wordsText.setText(rs.getString("wordslist"));
              rowCountText.setText(String.valueOf(rowCount));
              StatusText.setText("statusChanged: " + rs.getString("statusChanged") + " statusTo: " + rs.getString("statusTo"));
              activeTSText.setCaretPosition(0);
              analyseWordsText.setCaretPosition(0);
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
        
        //get Pep number and make tree 
        PEP_global = Integer.parseInt(gotoText.getText()); 
        DisplayWordsList(PEP_global,  wordListTable);
//------        TreeExample(PEP_global, tree, model );
        TreeExample(PEP_global, tree);
      //  setVisible(true);
     //   model.reload();
        tree.updateUI();
        
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
       
   
        
   //     revalidate();
        
       // pack();
//        /this.setVisible(true);
     //   repaint();
     //   System.out.println("here tree");
      }
    });
    //
    
    JButton rspButton = new JButton("Status List");
    //rspButton.add(new JLabel("Status List "), "North");
    ButtonHandler listener = new ButtonHandler();
    rspButton.addActionListener(listener);
    
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
             //		rs = statement.executeQuery("SELECT * FROM peps.allpeps WHERE PEP = " + Integer.parseInt(gotoText.getText()) + " AND folder LIKE '"+ selectedFolder + "' AND email LIKE '%" + searchText.getText()  + "%' AND statusChanged =1 order by date2, messageid asc, pep asc;");
             //	}
             	
             		rs = statement.executeQuery("SELECT * FROM peps.allpeps WHERE PEP = " + Integer.parseInt(gotoText.getText()) + "  AND email LIKE '%" + searchText.getText()  + "%' order by date2, messageid asc, pep asc;");
             		rowCount = returnRowCount("SELECT * FROM peps.allpeps WHERE PEP = " + Integer.parseInt(gotoText.getText()) + "  AND email LIKE '%" + searchText.getText()  + "%' order by date2, messageid asc, pep asc;");
             																														
              // if (freeQueryText.getText().toUpperCase().indexOf("SELECT") >= 0) {
               //  rs = statement.executeQuery(freeQueryText.getText());
             	
                 System.out.println("resultset rowCount = " + rowCount);  
                 if (rs.next()) {
                   accountIDText.setText(rs.getString("id"));
                   PEPText.setText(rs.getString("PEP"));
                   dateText.setText(rs.getString("date"));
                   locationText.setText(rs.getString("location"));
                   tsText.setText(rs.getString("line"));
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
             DisplayWordsList(PEP_global,  wordListTable);
             
             
             //show classification
           String textToClassify = activeTSText.getText();
//           TextClassifier tc = new TextClassifier(textToClassify);
           
           //JOptionPane.showMessageDialog(null, tc.returnClassification(), "InfoBox: " + "Classification", JOptionPane.INFORMATION_MESSAGE);
//           classificationText.setText(tc.returnClassification());
             
           //Determine flow
 /*
           String flow = activeTSText.getText();
           try {
			   GUIGenerateCsv gcsv = new GUIGenerateCsv();
			   //JOptionPane.showMessageDialog(null, tc.returnClassification(), "InfoBox: " + "Classification", JOptionPane.INFORMATION_MESSAGE);
			   Integer a = Integer.parseInt(gotoText.getText());         
			   classificationWords.setText(GUIGenerateCsv.getAllPEPMessages(a));
           } 
           catch (IOException e1) {
        	   // TODO Auto-generated catch block
        	   e1.printStackTrace();
           } 
  */       Integer a;
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
    
    JButton wordsListButton = new JButton("Words List");
    //rspButton.add(new JLabel("Status List "), "North");
    wordsListButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {          
        	DisplayWordsList(PEP_global,  wordListTable);
        	
        }
      });    
	
    JScrollPane scrollPane2 = new JScrollPane( wordListTable );
    JScrollPane jp = new JScrollPane(activeTSText); 
    JScrollPane jp2 = new JScrollPane(analyseWordsText); 
    
    //JPanel third = new JPanel(new BorderLayout());  
    third.setLayout(new GridLayout(1, 3));
 //   third.setLayout(new BorderLayout(2,2));
 //   TreeExample(461, tree);
    
      	     
    //add(tree);  
    JScrollPane sp3 = new JScrollPane(tree);
    //third.add(sp3, BorderLayout.WEST);
    
    //JLabel myLabel = new JLabel("classification");
 //   JPanel orig = new JPanel(new GridLayout(2,2));
 //   JPanel analyse = new JPanel(new GridLayout(2,2));
    
  //  third.add(orig, BorderLayout.WEST);
  //  third.add(analyse, BorderLayout.CENTER);
    
 //   orig.add(classificationMessage, BorderLayout.EAST); 
 //   analyse.add(classificationWords, BorderLayout.WEST);
    third.add(jp, BorderLayout.EAST); 
    third.add(jp2, BorderLayout.WEST); 
    
 //   third.add(classificationMessage, BorderLayout.EAST);  
 //   third.add(classificationWords, BorderLayout.WEST); 
//    third.add(jp, BorderLayout.EAST);  
//    third.add(jp2, BorderLayout.WEST);  
    
    //selectedLabel = new JLabel();
    
//    third.add( scrollPane2, BorderLayout.EAST);
    //jtree
  
   
    
    //setVisible(true);
    //scrollPane2.setVisible(true);
      
    //pack();
    //setVisible(true);
    
    
    JPanel fourth = new JPanel();
    fourth.add(firstButton);
    fourth.add(previousButton);
    fourth.add(nextButton);
    fourth.add(lastButton);
    
    
    JPanel sixth = new JPanel();
    sixth.setLayout(new BorderLayout(5,5));
    
    //JLabel myLabel = new JLabel("classification");
    JPanel labels = new JPanel(new GridLayout(6,1));
    JPanel controls = new JPanel(new GridLayout(6,1));
    
    sixth.add(labels, BorderLayout.WEST);
    sixth.add(controls, BorderLayout.CENTER);
    
    labels.add(new JLabel("Status: "));
    controls.add(statusCheck);
   // labels.add(new JLabel("RSP: "));
    
    //labels.add(new JLabel("Classify: "));
    
    labels.add(new JLabel("Classification: "));
    controls.add(classificationText);
    labels.add(new JLabel("searchText: "));
    controls.add(searchText);
    labels.add(new JLabel("PEP: "));
    controls.add(gotoText);
    labels.add(new JLabel("Rowcount: "));
    controls.add(rowCountText);
    labels.add(new JLabel("MessageID: "));
    controls.add(messageIdText);
    
    //gotoMessageIdButton
    
    //another panel for setting values
    JPanel seventh = new JPanel();
    seventh.setLayout(new BorderLayout(5,5));    
    //JLabel myLabel = new JLabel("classification");
    JPanel labels7 = new JPanel(new GridLayout(5,1));
    JPanel controls7 = new JPanel(new GridLayout(5,1));    
    seventh.add(labels7, BorderLayout.WEST);
    seventh.add(controls7, BorderLayout.CENTER);   
    
    labels7.add(new JLabel("ChooseFolder"));
    controls7.add(addParameters);
    labels7.add(new JLabel("Location: "));
    controls7.add(location);
    labels7.add(new JLabel("ChooseStatus"));
    controls7.add(statusChangedCheck);
    labels7.add(new JLabel("StatusFrom: "));
    controls7.add(sfromCBox);
    labels7.add(new JLabel("StatusTo: "));
    controls7.add(sToCBox);
    //fourth.add(bookList);
  //  fourth.add(gotoButton);
     
    JPanel eigth = new JPanel(new GridLayout(6,1));    
    eigth.add(classifyButton);
    eigth.add(rspButton);
    eigth.add(freeQueryButton);
    eigth.add(freeQueryButton2);
    eigth.add(wordsListButton);
    eigth.add(gotoMessageIdButton);
    
    JPanel tenth = new JPanel();
    JScrollPane scrollPaneAB = new JScrollPane( classificationWords );   
    tenth.add(scrollPaneAB);
    
    c.add(pane);
    c.add(fourth);
    c.add(sixth);
    c.add(seventh);
    //add to c
    
//  c.add(fifth);
   c.add(eigth);
  //  c.add(StatusChangedButton);
    
 //   c.add(first);
    c.add(second);
    c.add(third);
    c.add(tenth);
    
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
   // JScrollPane jp = new JScrollPane(activeTSText);
   // c.add(jp, BorderLayout.CENTER);
//    pack();
    //setSize(500, 500);
    setLocationByPlatform(true);
//    setVisible(true);
  
//    c.add(third);
    //show the original frame - with grid
    setSize(2000, 2000);
    setLocation(0, 0);
    show();
    
   
//    f.setSize(400, 400);
//    f.setVisible(true);
    
  }

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
      String url = "jdbc:mysql://localhost:3306/peps";
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
  
  public static String word;
  public static String searchWords;

  public static Highlighter highlighter = new UnderlineHighlighter(null);
  
  
  public void TreeExample(Integer v_PEP_global, JTree v_tree ) //, JTree v_tree, DefaultTreeModel v_model )
  {	  
	  //Statement stm = null;	
	 // DefaultMutableTreeNode root = null;
	  final String JDBC_DRIVER = "com.mysql.jdbc.Driver"; 
      try {
          Class.forName("com.mysql.jdbc.Driver").newInstance();
       } 
      catch (Exception e) {
          System.err.println("Unable to find and load driver");
          System.exit(1);
        }
      System.out.println("Populating tree");
      //  Connect to an MySQL Database, run query, get result set
      String url = "jdbc:mysql://localhost:3306/peps";
      String userid = "root";
      String password = "root";
      String sql = "SELECT DISTINCT(subject) from peps.allpeps where pep = " + v_PEP_global + " order by date2 asc, subject asc"; // limit 0, 10;";
      //can add messageid later
      // Java SE 7 has try-with-resources
      // This will ensure that the sql objects are closed when the program 
      // is finished with them
      
      DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");
      JTree tree4 = null;
//      System.out.println("here tree 2");
      String vsub = null;
      String subjectNode = null;
      String v_emailMessageId = null;
      
 //     tree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
  //        @Override
  //        public void valueChanged(TreeSelectionEvent e) {        	  
      //        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
           //   selectedLabel.setText(selectedNode.getUserObject().toString());
 //             JOptionPane.showMessageDialog(null, selectedNode.getUserObject().toString(), "InfoBox: " + "title", JOptionPane.INFORMATION_MESSAGE);
              //select that message from database and display on screen
              int counter = 0;
              try (Connection connection = DriverManager.getConnection( url, userid, password );
                  Statement stmt = connection.createStatement();
                  ResultSet rs = stmt.executeQuery( sql ))
              {	
            	     while (rs.next())
                     {
            	    	 //add chaild node
            	    	 subjectNode = rs.getString(1);
        	    		 System.out.println("Added subject " + subjectNode);
        	    //	 while (rs2.next()){
        	             //DefaultMutableTreeNode child = new DefaultMutableTreeNode(rs.getString(1));   //add inreplyto as child
        	    		 DefaultMutableTreeNode child = new DefaultMutableTreeNode(subjectNode);     //add subject as child
            	    	 root.add(child);
            	    	 MutableTreeNode grandchild;
            	    	 
            	    	 //add grandchild node
            	    	 //vsub = rs.getString(1);
            	    	 //using the inreplyto - try to get the senders name or subject
            	    	 String sql2 = "SELECT messageid, sendername from allpeps where subject LIKE '" + subjectNode + "' AND subject NOT LIKE '[Python-Dev] Summary of Python tracker Issues' order by date2";  //there will be many rows since there is no seprate table to store this information
            	    	 Statement stmt2 = connection.createStatement();
            	    	 ResultSet rs2 = stmt2.executeQuery( sql2 );
            	    	 while (rs2.next()){ 
            	    		 
                            // for (int childIndex = 0; childIndex < L1Nam.length; childIndex++) {
                             //    child = new DefaultMutableTreeNode(L1Nam[childIndex]);
                                // node.add(child);//add each created child to root
                                 //String sql3 = "SELECT messageid, emailMessageId, subject , sendername from peps.allpeps where pep = " + v_PEP_global + " AND inreplyto = '" + vsub + "'";
//                                 String sql3 = "SELECT emailMessageId,sendername from peps.allpeps where pep = " + v_PEP_global + " AND inreplyto = '" + vsub + "'";
 //                                Statement stm5 = connection.createStatement();
//                                 ResultSet rs3 = stm5.executeQuery(sql3);
//                                 while (rs3.next()) {
//            	    		 		 //String senderPerson = rs2.getString(1);
            	    		 		 CustomUserObject co = new CustomUserObject(rs2.getString(1),rs2.getString(2));
                                     grandchild = new DefaultMutableTreeNode(co);
            	    		// grandchild = ;
                                     child.add(grandchild);//add each grandchild to each child
 //                                }
                           //  }  
            	    	 }
            	          //create the child nodes
            	          //create the tree by passing in the root node  	          	    	 
                     }
            	     tree = new JTree(root);    	     
            	     //third.add(tree);  
            	     
            	     JScrollPane sp3 = new JScrollPane(tree);
            	     third.add(sp3, BorderLayout.WEST);
            	//     third.add(jp, BorderLayout.EAST);       	     
//            	     third.add(tree, BorderLayout.WEST);
        	          setVisible(true);
                 }
                 catch (SQLException e1)
                 {
                     System.out.println( e1.getMessage() );
                 }
              
              //removed from here
              third.revalidate();
              third.repaint();
      //repaint();   
      //v_tree = tree4;
      //v_tree.updateUI();
//      System.out.println("end of method");
      
      tree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
          @Override
          public void valueChanged(TreeSelectionEvent e) {
 //       	  System.out.println("here tree 00");
        	  DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
 //       	  System.out.println("here tree A");
        	//TreeExample(PEP_global);
        	  
        	try {
 //       		 System.out.println("here tree b");
              //	if(statusCheck.isSelected()){  //check to see if we want to see all retrieved messages where status has changed 
        		CustomUserObject uObj = (CustomUserObject) selectedNode.getUserObject();
 //       		 System.out.println("here tree c");
        	//	String test = (String) selectedNode.getUserObject();
        	//	 System.out.println("here tree c" + test);
        		System.out.println("Tree node selected with Emailmessageid " + uObj.getId() );
              		rs = statement.executeQuery("SELECT * FROM peps.allpeps WHERE messageid = '" + uObj.getId() + "';"); // AND email LIKE '%" + searchText.getText()  + "%' AND statusChanged =1 order by date2, messageid asc, pep asc;");
              	//}
              //	if (folderChanged == true){
              //		rs = statement.executeQuery("SELECT * FROM peps.allpeps WHERE PEP = " + Integer.parseInt(gotoText.getText()) + " AND folder LIKE '"+ selectedFolder + "' AND email LIKE '%" + searchText.getText()  + "%' AND statusChanged =1 order by date2, messageid asc, pep asc;");
              //	}
              	///else{
              	//	rs = statement.executeQuery("SELECT * FROM peps.allpeps WHERE PEP = " + Integer.parseInt(gotoText.getText()) + "  AND email LIKE '%" + searchText.getText()  + "%' order by date2, messageid asc, pep asc;");
              	//}																													
               // if (freeQueryText.getText().toUpperCase().indexOf("SELECT") >= 0) {
                //  rs = statement.executeQuery(freeQueryText.getText());
              	  
                  if (rs.next()) {
                    accountIDText.setText(rs.getString("id"));
                    PEPText.setText(rs.getString("PEP"));
                    dateText.setText(rs.getString("date"));
                    locationText.setText(rs.getString("location"));
                    tsText.setText(rs.getString("line"));
                    activeTSText.setText(rs.getString("email")); 
                    analyseWordsText.setText(rs.getString("analysewords")); 
                    wordsText.setText(rs.getString("wordslist"));
                    StatusText.setText("statusChanged: " + rs.getString("statusChanged") + " statusTo: " + rs.getString("statusTo"));
                    activeTSText.setCaretPosition(0);
                    analyseWordsText.setCaretPosition(0);
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
        	
        	final WordSearcher searcher = new WordSearcher(activeTSText, Color.red);
        	//highlight words
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
        	
        	
        	
          }
         });
         
  }
 

  public void connectToDB() {
    try {
      connection = DriverManager
          .getConnection(DB_URL,USER,PASS);
      statement = connection.createStatement();

    } catch (SQLException connectException) {
      System.out.println(connectException.getMessage());
      System.out.println(connectException.getSQLState());
      System.out.println(connectException.getErrorCode());
      System.exit(1);
    }
  }

  private void displaySQLErrors(SQLException e) {
    errorText.append("SQLException: " + e.getMessage() + "\n");
    errorText.append("SQLState:     " + e.getSQLState() + "\n");
    errorText.append("VendorError:  " + e.getErrorCode() + "\n");
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
      String url = "jdbc:mysql://localhost:3306/peps";
      String userid = "root";
      String password = "root";
      String sql = "select messageid, wordslist from peps.allpeps where statusChanged = 1 AND PEP = " + v_pep + " order by date2;";
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
  

  
}

//A simple class that searches for a word in
//a document and highlights occurrences of that word



