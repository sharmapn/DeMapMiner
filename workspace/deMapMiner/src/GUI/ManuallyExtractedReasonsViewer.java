package GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.text.BadLocationException;
import javax.swing.text.Highlighter;

import readRepository.readRepository.ReadLabels;
import connections.MysqlConnect;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.process.DocumentPreprocessor;
import miner.process.LabelTriples;
import miner.process.PythonSpecificMessageProcessing;
import utilities.TableCellLongTextRenderer;
import utilities.UnderlineHighlighter;
import utilities.WordSearcher;

import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;

public class ManuallyExtractedReasonsViewer extends JPanel { 	//JFrame
	private JTextField txtKeyword, txtPepNumber;
	private static JTable table;
	static DefaultTableModel model;
	
	//shud replace above variables
	static MysqlConnect mc = new MysqlConnect();
	static Connection conn = mc.connect();
	static String sql = ""; 
	static Integer pepNum  = -1;	
	static PythonSpecificMessageProcessing pm = new PythonSpecificMessageProcessing();		
	static String trainingDataTable = "trainingdata_june2020";
	
	static JComboBox<String> causeCategoryText = setValuesCauseCategory();//, labelText = setValuesSubReasons();
	static JComboBox<String> labelText = setValuesLabels();
	
	JButton btnSearch = new JButton("Search");	// Button Search
	
	//initiate from main
	public void initiateFromMain(int p) {
		String pep = String.valueOf(p);
		if(pep==null || pep.isEmpty() || pep.equals("")) {}
		else {
			txtPepNumber.setText(pep);		
			btnSearch.doClick();
		}
	}
	
	public ManuallyExtractedReasonsViewer() {
		setLayout(new BorderLayout());
		JScrollPane scrollPane = new JScrollPane();		scrollPane.setBounds(50, 50, 1200, 600);
		add(scrollPane, BorderLayout.CENTER);
						
		table = new JTable(){
		    public boolean isCellEditable(int rowIndex, int colIndex) {
		    	if(colIndex==0) {
		    		return false; // Disallow Column 0
		    	} else {
		    		return true;   // Allow the editing 
		    	}
		    }
		    
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component comp = super.prepareRenderer(renderer, row, column);
                if (column==0) {
                    comp.setBackground(Color.lightGray);
                } else {
                	comp.setBackground(Color.white);
                }
                return comp;
            }
            
            //Implement table cell tool tips.
            public String getToolTipText(MouseEvent e) {
                String tip = null;
                java.awt.Point p = e.getPoint();
                int rowIndex = rowAtPoint(p);                int colIndex = columnAtPoint(p);
                
                try {
                    tip = getValueAt(rowIndex, colIndex).toString();
                } catch (RuntimeException e1) {
                    //catch null pointer exception if mouse is over an empty line
                }
                return tip;
            }
		};		
		scrollPane.setViewportView(table);		//table.setFont(new Font("Serif", Font.PLAIN, 10));		
		
		JPanel panelNorth = new JPanel();
		JLabel lblSearch = new JLabel("State & Reason");		lblSearch.setBounds(101, 27, 84, 14);		// Label Search
		panelNorth.add(lblSearch);		panelNorth.add(labelText);		//state comboBox
		panelNorth.add(causeCategoryText);
		// Keyword
		//txtKeyword = new JTextField(); 		txtKeyword.setBounds(195, 24, 160, 20);
		//panelNorth.add(txtKeyword);			txtKeyword.setColumns(10);
		// Label Search
		JLabel lblPepNumber = new JLabel("Pep");			lblPepNumber.setBounds(200, 27, 84, 14);		panelNorth.add(lblPepNumber);
		//pepnumber
		txtPepNumber = new JTextField(); 		txtPepNumber.setBounds(300, 24, 160, 20);
		panelNorth.add(txtPepNumber); 			txtPepNumber.setColumns(10);
			
		
		// Button Search
		btnSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				  model.setRowCount(0);			//empty table contents, in cases where new search is issued	
				  String label = (String) labelText.getSelectedItem();  //txtKeyword.getText();
				  label = label.trim();
				 // if(label.isEmpty() || label.equals("") || label ==null ) {	}
				  String proposal = txtPepNumber.getText();
				  
				  if(proposal == null || proposal.isEmpty() || proposal.equals("") ) {
					  //System.out.println("here a");
					  if (label.isEmpty() || label.equals("") || label == null) {	
						  label= "%%";	//System.out.println("here b");
						  //if proposal is empty and label is empty, but cause category is populated, then we search by cause category
						  String causeCategory = (String) causeCategoryText.getSelectedItem(); 
						  if(causeCategory == null || causeCategory.isEmpty() || causeCategory.equals("")) {	//System.out.println("here c");
							   sql = "select * from "+ trainingDataTable+ " order by state, pep";
						  }
						  else {	//System.out.println("here d");
							  sql = "select * from " + trainingDataTable + " where  label LIKE '"+ causeCategory +"' order by dmscheme, pep"; 	
						  }					  
					  }   
					  else { 	//System.out.println("here e");
						  String causeCategory = (String) causeCategoryText.getSelectedItem();
						  if(causeCategory == null || causeCategory.isEmpty() || causeCategory.equals("")) {	//System.out.println("here c");
							  sql = "select * from " + trainingDataTable + " where  state LIKE '"+ label +"' order by reason, pep"; 
						  }
						  else {	//System.out.println("here d");
							  sql = "select * from " + trainingDataTable + " where  state LIKE '"+ label +"' and label LIKE '"+ causeCategory +"' order by state, pep"; 	
						  }
					  }
				  }
				  else {	System.out.println("here f");
					  pepNum = Integer.parseInt(txtPepNumber.getText());
					  if (label.isEmpty() || label.equals("") || label == null) {	System.out.println("here g");
						  sql = "select * from " + trainingDataTable + " where pep = " + pepNum + " order by reason, causemessageid";						  
					  }   else { 	System.out.println("here h");
						  sql = "select * from " + trainingDataTable + " where pep = " + pepNum + " and state LIKE '"+ label +"' order by state, causemessageid";
					  }
				  }	
				  
				  
				  
				  populateData(sql);			
			}
		});
		btnSearch.setBounds(365, 23, 79, 23);
		panelNorth.add(btnSearch);
		JLabel spacer = new JLabel("	||	  "); 
		panelNorth.add(spacer, BorderLayout.SOUTH);	
		   // Button Search - but oredr by pep ..needed to see until which pep has been manually extracted
		JCheckBox checkbox = new JCheckBox("asc or desc");	checkbox.setBounds(395, 23, 79, 23);
		panelNorth.add(checkbox);// add to a container
		checkbox.setSelected(true);		// set state
		
		JButton btnSearch2 = new JButton("Search (order by Proposal)");
		btnSearch2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				  model.setRowCount(0);			//empty table contents, in cases where new search is issued	
		
				   //get value of ordering
				    String ascOrDesc; 
					
					// check state
					if (checkbox.isSelected()) {
						ascOrDesc = "asc";
					} else {
						ascOrDesc = "desc";
					}
				  
				  String label = (String) labelText.getSelectedItem();  //txtKeyword.getText();  
				  if(label.isEmpty() || label.equals("") || label ==null ) {	label= "%%";	}
				  String proposal = txtPepNumber.getText();
				  
				  if(proposal == null || proposal.isEmpty()) {
					  if (label.isEmpty() || label.equals("") || label == null) {
						  sql = "select * from "+ trainingDataTable+ " order by pep "+ascOrDesc+",label";						  
					  }   else { 
						  sql = "select * from " + trainingDataTable + " where  label LIKE '"+ label +"' order by pep "+ascOrDesc+", label"; 						  
					  }
				  }
				  else {
					  pepNum = Integer.parseInt(txtPepNumber.getText());
					  if (label.isEmpty() || label.equals("") || label == null) {
						  sql = "select * from " + trainingDataTable + " where pep = " + pepNum + " order by label, messageid";						  
					  }   else { 
						  sql = "select * from " + trainingDataTable + " where pep = " + pepNum + " and label LIKE '"+ label +"' order by label, messageid";
					  }
				  }	
				  populateData(sql);			
			}
		});
		btnSearch2.setBounds(385, 23, 79, 23);
		panelNorth.add(btnSearch2);
		
		JButton btnSave = new JButton("Save");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				UpdateData(); // UpdateData
				btnSearch.doClick();
			}
		});				
		panelNorth.add(btnSave);
		
		JButton button = new JButton("See Message Details of Row");
		JTextField firstSearchword = new JTextField(20),secondSearchword = new JTextField(20),thirdSearchword = new JTextField(20);
	    JButton b1= new JButton("submit");    
	    JPanel panel = new JPanel(); 	    JLabel label = new JLabel("		Enter search keyswords"); 						
	    panelNorth.add(spacer, BorderLayout.SOUTH);	panelNorth.add(label, BorderLayout.SOUTH);
	    panelNorth.add(firstSearchword); 	panelNorth.add(secondSearchword);		panelNorth.add(thirdSearchword);	panelNorth.add(button,BorderLayout.SOUTH);		panelNorth.add(b1,BorderLayout.SOUTH);
		add(panelNorth,BorderLayout.NORTH);

		  b1.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {           
          	  String searchWords;                  
                searchWords = firstSearchword.getText().trim();  
               // first = firstSearchword.getText().trim();  
                //once user click submit button, export the data to file
                populateData(sql);        		
          }
        });
     
        button.addActionListener(new ActionListener() {

         @Override
         public void actionPerformed(ActionEvent e) {           	
           int i = table.getSelectedRow();
           Integer p = Integer.parseInt( model.getValueAt(i, 2).toString() );
           showMessageForID(p);
           System.out.println("p " + p + " i " + i);
         }
        });	
		
		// Bind and PopulateData
		ShowTableColumnHeaders();
		//show the data
		//we want to see results pep by pep, therefore we dont want to see all results one time
		//but we have this option for later use
//$$		exportdata2("", -1, "");
		//set up combobox in tabel
		setUpLabelColumn(table, table.getColumnModel().getColumn(3));
		//setUpReasonColumn(table, table.getColumnModel().getColumn(6));
		//setUpSubReasonColumn(table, table.getColumnModel().getColumn(7));
	}
	/*
	 * //reason category
			JComboBox<String> sToCBox2 = new JComboBox<>();
			Statement stmt2 = null;		ResultSet rs2;			
			try {
				stmt2 = conn.createStatement();	
				rs2 = stmt2.executeQuery("select distinct(subReason) as subReason from states_reason_reasonsublabels");			
				while (rs2.next()) {
					String subReason = rs2.getString("subReason");					
					sToCBox2.addItem(subReason);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			sToCBox2.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {          
					String selectedsFrom = (String) sToCBox2.getSelectedItem();
					// update();				
				}
			});
	 */
	
	public void setUpLabelColumn(JTable table, TableColumn sportColumn) {
		//Set up the editor for the sport cells.
		JComboBox<String> sToCBox = new JComboBox<>();
		Statement stmt = null;		ResultSet rs;
		Integer forOutput_pepNumber = null, resultCounter=0;	
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery("select distinct(label) as label from trainingdata");			
			while (rs.next()) {
				String reason = rs.getString("label");					
				sToCBox.addItem(reason);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		sToCBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {          
				
				String selectedsFrom;
				selectedsFrom = (String) sToCBox.getSelectedItem();
			}
		});			
		
		sportColumn.setCellEditor(new DefaultCellEditor(sToCBox));
		//Set up tool tips for the sport cells.
		DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
		renderer.setToolTipText("Click for combo box");
		sportColumn.setCellRenderer(renderer);
	}
	
	public void setUpReasonColumn(JTable table, TableColumn sportColumn) {
			//Set up the editor for the sport cells.
			JComboBox<String> sToCBox = new JComboBox<>();
			Statement stmt = null;		ResultSet rs;
			Integer forOutput_pepNumber = null, resultCounter=0;	
			try {
				stmt = conn.createStatement();
				rs = stmt.executeQuery("select distinct(reason) as reason from states_reason_reasonsublabels");			
				while (rs.next()) {
					String reason = rs.getString("reason");					
					sToCBox.addItem(reason);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			sToCBox.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {          
					
					String selectedsFrom;
					selectedsFrom = (String) sToCBox.getSelectedItem();
				}
			});			
			
			sportColumn.setCellEditor(new DefaultCellEditor(sToCBox));
			//Set up tool tips for the sport cells.
			DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
			renderer.setToolTipText("Click for combo box");
			sportColumn.setCellRenderer(renderer);
	}
	
	public void setUpSubReasonColumn(JTable table, TableColumn sportColumn) {
		//Set up the editor for the sport cells.
		JComboBox<String> sToCBox = new JComboBox<>();
		Statement stmt = null;		ResultSet rs;
		Integer forOutput_pepNumber = null, resultCounter=0;	
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery("select distinct(subreason) as subreason from states_reason_reasonsublabels");			
			while (rs.next()) {
				String subreason = rs.getString("subreason");					
				sToCBox.addItem(subreason);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		sToCBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {          
				
				String selectedsFrom;
				selectedsFrom = (String) sToCBox.getSelectedItem();
			}
		});			
		
		sportColumn.setCellEditor(new DefaultCellEditor(sToCBox));
		//Set up tool tips for the sport cells.
		DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
		renderer.setToolTipText("Click for combo box");
		sportColumn.setCellRenderer(renderer);
}
	
	private void UpdateData()	{	// UpdateData	
		Statement s = null;
		try {
			s = conn.createStatement();
			int column = 0;			int row = table.getSelectedRow();
			System.out.println("column " + column + " row " + row);			System.out.println(table.getModel().getValueAt(row, column));
			int value = 	Integer.parseInt(table.getModel().getValueAt(row, column).toString() ); 			System.out.println("id value to update " + value);
			int id = 		Integer.parseInt(table.getValueAt(row, 0).toString()); 	System.out.println("id to update " + value);		
			int proposal =  Integer.parseInt(table.getValueAt(row, 1).toString());	
			int messageID;
			if   (table.getValueAt(row, 2) ==  null) {}
			else {messageID = Integer.parseInt(table.getValueAt(row, 2).toString()); }
			String effectSentence;
			if   (table.getValueAt(row, 5) ==  null) {effectSentence="";}
			else {effectSentence = table.getValueAt(row, 5).toString(); }
			String causeCategory;
			if   (table.getValueAt(row, 6) ==  null) {causeCategory="";}
			else {causeCategory = table.getValueAt(row, 6).toString(); }
			String causeSubCategory;
			if   (table.getValueAt(row, 7) ==  null) {causeSubCategory="";}
			else {causeSubCategory = table.getValueAt(row, 7).toString(); }
			String communityReason;
			if   (table.getValueAt(row, 8) ==  null) {communityReason="";}
			else {communityReason = table.getValueAt(row, 8).toString(); }
			String proposalAuthorReview;
			if   (table.getValueAt(row, 9) ==  null) {proposalAuthorReview="";}
			else {proposalAuthorReview = table.getValueAt(row, 9).toString(); }
			String bdflPronouncement;
			if   (table.getValueAt(row, 10) ==  null) {bdflPronouncement="";}
			else {bdflPronouncement = table.getValueAt(row, 10).toString(); }
			
			String label = table.getModel().getValueAt(row, 3).toString();			System.out.println("label to update to " + label);	//label = "accepted";
			String label2 = table.getValueAt(row, 3).toString();			System.out.println("label to update to " + label2);	//label = "accepted";
			String causeSentence = table.getValueAt(row, 4).toString();				
				
			
			String query3 = "update trainingData SET causeCategory = ?,causeSubCategory= ?, causeSentence = ?,effectSentence = ?,label = ? where id = ?";
			PreparedStatement preparedStmt3 = conn.prepareStatement(query3);
			preparedStmt3.setString(1, causeCategory);	preparedStmt3.setString(2, causeSubCategory); preparedStmt3.setString(3, causeSentence);	preparedStmt3.setString(4, effectSentence);	preparedStmt3.setString(5, label);	
			preparedStmt3.setInt(6, id);
			
			// execute the java preparedstatement
			int rowsUpdated = preparedStmt3.executeUpdate();	System.out.println("Rows impacted : " + rowsUpdated );
			
     		JOptionPane.showMessageDialog(null, "Record Update Successfully: Rows impacted : " + rowsUpdated );
     		populateData(sql); // Reload Data
     		table.repaint();
		} catch (Exception e) {
			System.out.println(StackTraceToString(e)  );
			JOptionPane.showMessageDialog(null, e.getMessage()); 			e.printStackTrace();
		}
	}	
	//these columns have been changed in may 2020
	private  void ShowTableColumnHeaders() {
		table.setModel(new DefaultTableModel());// Clear table
		model = (DefaultTableModel)table.getModel();// Model for Table
		model.addColumn("id");			model.addColumn("State"); /*	model.addColumn("PEP");	*/					model.addColumn("causeMessageID");		 
					//was causesentence before		
		model.addColumn("DM Scheme");	model.addColumn("Reason"); model.addColumn("reasonsentence");
		/*model.addColumn("effectsentence");*/		
		//model.addColumn("communityReview");	model.addColumn("proposalAuthorReview");	model.addColumn("bdfldelegatePronouncement");	//model.addColumn("notes");
	}
	
	private static JComboBox<String> setValuesLabels() {
		JComboBox<String> sToCBox = new JComboBox<>();
		Statement stmt = null;		ResultSet rs;
		Integer forOutput_pepNumber = null, resultCounter=0;	
		try {
			//add empty string to allow searching for all labels
			sToCBox.addItem("");
			stmt = conn.createStatement();
			rs = stmt.executeQuery("select distinct(state) as state from states_reason_reasonsublabels");			
			while (rs.next()) {
				String state = rs.getString("state");					
				sToCBox.addItem(state);
			}			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		sToCBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {          
				
				String selectedsFrom;
				selectedsFrom = (String) sToCBox.getSelectedItem();
			}
		});
		return sToCBox;
	}
	
	private static JComboBox<String> setValuesReasons() {
		JComboBox<String> sToCBox = new JComboBox<>();
		Statement stmt = null;		ResultSet rs;
		Integer forOutput_pepNumber = null, resultCounter=0;	
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery("select distinct(reason) as reason from states_reason_reasonsublabels");			
			while (rs.next()) {
				String reason = rs.getString("reason");					
				sToCBox.addItem(reason);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		sToCBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {          
				
				String selectedsFrom;
				selectedsFrom = (String) sToCBox.getSelectedItem();
			}
		});
		return sToCBox;
	}
	private static JComboBox<String> setValuesSubReasons() {
		JComboBox<String> sToCBox = new JComboBox<>();
		Statement stmt = null;		ResultSet rs;
		Integer forOutput_pepNumber = null, resultCounter=0;	
		try {
			stmt = conn.createStatement();	
			rs = stmt.executeQuery("select distinct(subReason) as subReason from states_reason_reasonsublabels");			
			while (rs.next()) {
				String subReason = rs.getString("subReason");					
				sToCBox.addItem(subReason);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		sToCBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {          
				String selectedsFrom = (String) sToCBox.getSelectedItem();
				// update();				
			}
		});
		return sToCBox;
	}
	private static JComboBox<String> setValuesCauseCategory() {
		JComboBox<String> sToCBox = new JComboBox<>();
		Statement stmt = null;		ResultSet rs;
		Integer forOutput_pepNumber = null, resultCounter=0;	
		try {
			stmt = conn.createStatement();	
			rs = stmt.executeQuery("select distinct(label) as causecategory from trainingData");			
			while (rs.next()) {
				String causecategory = rs.getString("causecategory");					
				sToCBox.addItem(causecategory);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		sToCBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {          
				String selectedsFrom = (String) sToCBox.getSelectedItem();
				// update();				
			}
		});
		return sToCBox;
	}
	
	/*
	private static JComboBox<String> setValuesStates() {
		String[] statusTo = new String[] {"","Draft","Open","Active","Pending","Closed","Final","Accepted","Deferred","Replaced","Rejected","Postponed","Incomplete","Superseded","Withdrawn"};
		JComboBox<String> sToCBox = new JComboBox<>(statusTo);		String selectedsTo = (String) sToCBox.getSelectedItem();    
		sToCBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {          
				String selectedsTo = (String) sToCBox.getSelectedItem();
			}
		});
		return sToCBox;
	}
	private static JComboBox<String> setValuesReasons() {
		String[] reasonsCategory = new String[] {"None","Basic, No Community Status","Consensus","No Consensus","Little interest","BDFL Pronouncement over Community Split","BDFL pronouncement over Consensus"};    
		JComboBox<String> sfromCBox = new JComboBox<>(reasonsCategory);			String selectedsFrom = (String) sfromCBox.getSelectedItem();    
		sfromCBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {          
				String selectedsFrom = (String) sfromCBox.getSelectedItem();
				// update();				
			}
		});
		return sfromCBox;
	} */
	
	public static void populateData(String sql) {
		JButton button = new JButton("See Message Details of Row");		
		Statement stmt = null;		ResultSet rs;

		try {			
			stmt = conn.createStatement();
			//pepNumber not supplied
			
			int sentenceCounter = 0,row = 0;;				
//			System.out.println("here 344 " + rs.toString());
			ResultSet rec = stmt.executeQuery(sql);
			
			while(rec.next())     {			
				model.addRow(new Object[0]);
				model.setValueAt(rec.getString("id"), row, 0);			/*	model.setValueAt(rec.getString("PEP"), row, 1);	*/			model.setValueAt(rec.getString("causeMessageID"), row, 2);	
				model.setValueAt(rec.getString("state"), row, 1);			model.setValueAt(rec.getString("reason"), row, 4);   model.setValueAt(rec.getString("causesentence"), row, 5);	
				//model.setValueAt(rec.getString("effectsentence"), row, 5);		
				model.setValueAt(rec.getString("dmscheme"), row, 3);   
				/*
				String cc = rec.getString("reason");
				if (cc==null|| cc.isEmpty() || cc.isEmpty()){ 
					
				}else {
					model.setValueAt(cc, row, 7);	//choose value in the combo box
				} 
				*/
				 	//model.setValueAt(rec.getString("communityReview"), row, 8);		
				//model.setValueAt(rec.getString("proposalAuthorReview"), row, 9); model.setValueAt(rec.getString("bdfldelegatePronouncement"), row, 10);	model.setValueAt(rec.getString("notes"), row, 11);
				row++;
            }
			rec.close();

			//color rows
			table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
				@Override
				public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) 
				{
					Component c = super.getTableCellRendererComponent(table,value, isSelected, hasFocus, row, column);
					c.setBackground(row%2==0 ? Color.white : Color.yellow);                        
					return c;
				};
			});
			/*
			table.setFont(new Font("Arial", Font.PLAIN, 10));
			//working - sets the cell wrapping
			table.getColumnModel().getColumn(2).setCellRenderer(new TableCellLongTextRenderer ());			table.getColumnModel().getColumn(3).setCellRenderer(new TableCellLongTextRenderer ()); 
			table.getColumnModel().getColumn(4).setCellRenderer(new TableCellLongTextRenderer ());			table.getColumnModel().getColumn(5).setCellRenderer(new TableCellLongTextRenderer ());  
	//		table.getColumnModel().getColumn(6).setCellRenderer(new TableCellLongTextRenderer ()); 			//table.getColumnModel().getColumn(7).setCellRenderer(new TableCellLongTextRenderer ());  			
			
			table.setRowHeight(40);
			table.getColumnModel().removeColumn( table.getColumnModel().getColumn(1) );
			
			*/
			
			TableColumn column = null;
			for (int i = 0; i < 4; i++) {
				column = table.getColumnModel().getColumn(i);
				if (i == 0) 	 {	column.setPreferredWidth(7); } //third column is bigger
				else if (i == 1) {	column.setPreferredWidth(7); }//third column is bigger
				else if (i == 2) {	column.setPreferredWidth(7); } 
				else if (i==3)   {	column.setPreferredWidth(7); }	else if (i==4){		column.setPreferredWidth(7); }
	//			else { 	column.setPreferredWidth();  }
			}
		} catch (SQLException e) {
			e.printStackTrace();			System.out.println("e.str "+ e.toString());			System.out.println(StackTraceToString(e)  );
		} catch (Exception e1) {
			// TODO Auto-generated catch block				
			e1.printStackTrace();			System.out.println(StackTraceToString(e1)  );
		}
		System.out.println("str " + sql);
	}
	
	public static String StackTraceToString(Exception ex) {
		String result = ex.toString() + "\n";
		StackTraceElement[] trace = ex.getStackTrace();
		for (int i=0;i<trace.length;i++) {
			result += trace[i].toString() + "\n";
		}
		return result;
	}
	
	//test all words exist in sentence 	// its used but not sure how
	public static boolean contains(String [] array, String v_sentenceString){
		boolean test = true;
		for (String w : array){
			if (!v_sentenceString.contains(w))
					test = false;
		}
		return test;
	}
	
	public static void showMessageForID(Integer v_mid){
		//if this is needed, then changeover to previous tabbed panel and show that messageid 
	}
	
	//can be a standloane applcation or can be called from anotehr gui, therfore some linesare commented and JFrame is convereted to JPanel
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				ManuallyExtractedReasonsViewer frame = new ManuallyExtractedReasonsViewer();
				frame.setVisible(true);
			}
		});
	}
}