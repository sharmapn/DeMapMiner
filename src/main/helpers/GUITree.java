package main.helpers;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

import Process.models.CustomUserObject;
import utilities.WordSearcher;

public class GUITree {
	/*
	 * public void TreeExample(Integer v_PEP_global, JTree v_tree ) //, JTree v_tree, DefaultTreeModel v_model )
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
      String url = "jdbc:mysql://localhost:3306/peps_new";
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
        	
        	final WordSearcher searcher = new WordSearcher(activeTSText);
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
  
  	//THIS PRICE OF CODE EXTRACTED FROM MAIN SECTION
  	 * freeQueryButton2 = new JButton("Search wTree");
		freeQueryButton2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int rowCount = 0;
				try {
					if(statusChangedCheck.isSelected()){  //check to see if we want to see all retrieved messages where status has changed 
						//rs = statement.executeQuery("SELECT * FROM "+messagesTableName+" WHERE PEP = " + Integer.parseInt(gotoText.getText()) + " AND email LIKE '%" + searchText.getText()  + "%' AND statusChanged =1 order by date2, messageid asc, pep asc;");
						rs = statement.executeQuery("SELECT * FROM "+messagesTableName+" WHERE email statusChanged =1  AND statusTO LIKE '%" + (String) sToCBox.getSelectedItem() + "%' and ststusFrom LIKE '%" + (String) sfromCBox.getSelectedItem() + "%' order by date2, pep asc;");
						rowCount = guih.returnRowCount("SELECT * FROM "+messagesTableName+" WHERE email statusChanged =1  AND statusTO LIKE '%" + (String) sToCBox.getSelectedItem() + "%' and ststusFrom LIKE '%" + (String) sfromCBox.getSelectedItem() + "%' order by date2, pep asc; ");
					}

					else if(statusCheck.isSelected()){  //check to see if we want to see all retrieved messages where status has changed 
						rs = statement.executeQuery("SELECT * FROM "+messagesTableName+" WHERE PEP = " + Integer.parseInt(gotoText.getText()) + " AND email LIKE '%" + searchText.getText()  + "%' AND statusChanged =1 order by date2, messageid asc, pep asc;");

						if(addParameters.isSelected()){  //check to see if we want to see all retrieved messages where status has changed 
							String temp = (String) location.getSelectedItem();
							//JOptionPane.showMessageDialog(null,"hello","TITLE",JOptionPane.WARNING_MESSAGE);
							rs = statement.executeQuery("SELECT * FROM "+messagesTableName+" WHERE PEP = " + Integer.parseInt(gotoText.getText()) + " AND email LIKE '%" + searchText.getText()  + "%' AND statusChanged =1 AND folder LIKE '%" + (String) location.getSelectedItem() + "%' order by date2, messageid asc, pep asc;");
							//JOptionPane.showMessageDialog(null,statement.toString(),"TITLE",JOptionPane.WARNING_MESSAGE);
							rowCount = guih.returnRowCount("SELECT * FROM "+messagesTableName+" WHERE PEP = " + Integer.parseInt(gotoText.getText()) + " AND email LIKE '%" + searchText.getText()  + "%' AND statusChanged =1 AND folder LIKE '%" + (String) location.getSelectedItem() + "%' order by date2, messageid asc, pep asc;");
							System.out.println(temp);
						}
					}
					//	if (folderChanged == true){
					//		rs = statement.executeQuery("SELECT * FROM "+messagesTableName+" WHERE PEP = " + Integer.parseInt(gotoText.getText()) + " AND folder LIKE '"+ selectedFolder + "' AND email LIKE '%" + searchText.getText()  + "%' AND statusChanged =1 order by date2, messageid asc, pep asc;");
					//	}
					else{
						rs = statement.executeQuery("SELECT * FROM "+messagesTableName+" WHERE PEP = " + Integer.parseInt(gotoText.getText()) + "  AND email LIKE '%" + searchText.getText()  + "%' order by date2, messageid asc, pep asc;");
						rowCount = guih.returnRowCount("SELECT * FROM "+messagesTableName+" WHERE PEP = " + Integer.parseInt(gotoText.getText()) + "  AND email LIKE '%" + searchText.getText()  + "%' order by date2, messageid asc, pep asc;");
					}																													
					// if (freeQueryText.getText().toUpperCase().indexOf("SELECT") >= 0) {
					//  rs = statement.executeQuery(freeQueryText.getText());

					if (rs.next()) {
						messageIDText.setText(rs.getString("messageid"));	PEPText.setText(rs.getString("PEP"));	dateText.setText(rs.getString("date2"));
						locationText.setText(rs.getString("folder"));		tsText.setText(rs.getString("file"));	activeTSText.setText(rs.getString("email")); 	analyseWordsText.setText(rs.getString("analysewords")); 
						wordsText.setText(rs.getString("wordslist"));		rowCountText.setText(String.valueOf(rowCount));
						StatusText.setText("statusChanged: " + rs.getString("statusChanged") + " statusTo: " + rs.getString("statusTo"));
						activeTSText.setCaretPosition(0);					analyseWordsText.setCaretPosition(0);
					}
					int i = statement.executeUpdate(freeQueryText.getText()); 					errorText.append("Rows affected = " + i);
				} catch (SQLException insertException) {
					displaySQLErrors(insertException);
				}
				
				PEP_global = Integer.parseInt(gotoText.getText()); //get Pep number and make tree 
				//$$       DisplayWordsList(PEP_global,  wordListTable);	//------        TreeExample(PEP_global, tree, model );
				//BELOW LINE COMMENTED AS TREE FUNCTIONS MOVED AND COMMNETED IN ANOTHER CLASS
				//       TreeExample(PEP_global, tree);
				//  setVisible(true);		//   model.reload();
				//tree.updateUI();
				tf.setText("PEP " + PEPText.getText());

				word = tf.getText().trim();     int offset = searcher.search(word);                  //get text from keyword box				
				if (offset != -1) {
					try {
						textPane.scrollRectToVisible(textPane.modelToView(offset));
					} catch (BadLocationException g) {	}
				}   

				searchWords = searchText.getText().trim();                       //get text from keyword box
				int offset2 = searcher.search(searchWords);
				if (offset2 != -1) {
					try {
						textPane.scrollRectToVisible(textPane.modelToView(offset));
					} catch (BadLocationException g) {	}
				} 
			}
		});
  

	 */
}
