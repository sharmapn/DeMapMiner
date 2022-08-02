package ManualAnalysis.NearbySentences;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Highlighter;

import readRepository.readRepository.ReadLabels;
import connections.MysqlConnect;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JComboBox;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.process.DocumentPreprocessor;
import GUI.helpers.MoveToText.FindTextPane;
import miner.process.LabelTriples;
import miner.process.PythonSpecificMessageProcessing;
import utilities.TableCellLongTextRenderer;
import utilities.UnderlineHighlighter;
import utilities.WordSearcher;

import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.ActionEvent;

//UI code from MoveToText.jaav and ShowMessage.java
public class NearbySentencesForTerms_QueryingGUI extends JFrame {
	private JTextField txtKeyword, txtPepNumber;
	private static JTable table;	
	static DefaultTableModel model;	
	static MysqlConnect mc = new MysqlConnect();
	static Connection conn = mc.connect();	
	static String first;	
	static Boolean postback = false;	
	static Integer pepNum  = -1;
	
	static PythonSpecificMessageProcessing pm = new PythonSpecificMessageProcessing();	
	static String baseIdeas[];	
	static String searchWords, searchWordsB,searchWordsC,v_message = "", v_subject = "", termToHighlight="",tableName = "keywordMatching29peps";	
	
	public static void main(String[] args) {			  
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				NearbySentencesForTerms_QueryingGUI frame = new NearbySentencesForTerms_QueryingGUI();
				frame.setVisible(true);
			}
		});
	}

	public NearbySentencesForTerms_QueryingGUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(0, 0, 1350, 750);
		setTitle("Get Nearby Sentences For Provided Words");
		getContentPane().setLayout(new BorderLayout());
		
		// ScrollPane
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(50, 50, 1200, 600);
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		// Table
		table = new JTable();
		scrollPane.setViewportView(table);
		
		JPanel panelNorth = new JPanel();
		
		JLabel lblComboBox = new JLabel("Select Labels");
		lblComboBox.setBounds(70, 27, 84, 14);
		panelNorth.add(lblComboBox);			

	    JComboBox comboBox = new JComboBox();
	    //comboBox.  "Choose Label"
	    comboBox.setBounds(170, 24, 50, 20);
	    panelNorth.add(comboBox);
	    	    
	    try {
			populateLbelsComboBox(comboBox);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	    
	    comboBox.addItemListener(new ItemListener() {
	        public void itemStateChanged(ItemEvent arg0) {
	        	String x = String.valueOf(comboBox.getSelectedItem());
	        	System.out.println("here  a" );
	        	queryAndDisplayDataInGrid(x, -1,"");
	        }
	    });
		
		// Label Search
		JLabel lblSearch = new JLabel("Search Words");
		lblSearch.setBounds(101, 27, 84, 14);
		panelNorth.add(lblSearch);		
		
		// Keyword
		txtKeyword = new JTextField();
		txtKeyword.setBounds(195, 24, 160, 20);
		panelNorth.add(txtKeyword);
		txtKeyword.setColumns(10);
		// Label Search
		JLabel lblPepNumber = new JLabel("Pep #");
		lblPepNumber.setBounds(200, 27, 84, 14);
		panelNorth.add(lblPepNumber);
		//pepnumber
		txtPepNumber = new JTextField();
		txtPepNumber.setBounds(300, 24, 160, 20);
		panelNorth.add(txtPepNumber);
		txtPepNumber.setColumns(10);
		
		 //list of terms
	    String[] termList = new String[] {"States", "DecisionMechanisms SubStates","Discussion","IdeaStage","Entities","Reasons"};
	    JComboBox<String> list = new JComboBox<>(termList);	list.setBounds(350, 24, 160, 20);
	    panelNorth.add(list);//add to the parent container (e.g. a JFrame):
	    String selectedTerm = (String) list.getSelectedItem();//get the selected item:
	    System.out.println("You seleted the book: " + selectedTerm);
		
		// Button Search
		JButton btnSearch = new JButton("Search");
		btnSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// Bind new Data.
				//PopulateData();
				
				//empty table contents, in cases where new search is issued
				  model.setRowCount(0);
				  System.out.println("here 55: ");
                 // searchWords = txtKeyword.getText().trim();  
                  //first = txtKeyword.getText().trim();  
				  searchWords = txtKeyword.getText();  
                  first = txtKeyword.getText(); 
                  termToHighlight = searchWords;
                  //once user click submit button, export the data to file                  			  
				  if (txtPepNumber.getText() != null){
					  pepNum = Integer.parseInt(txtPepNumber.getText());
					  System.out.println("here 323 "); 
					  queryAndDisplayDataInGrid(searchWords, pepNum,"");
				  }
				  else{
					  System.out.println("here 324 ");
					  queryAndDisplayDataInGrid(searchWords, -1,"");
				  }				
			}
		});
		btnSearch.setBounds(365, 23, 79, 23);
		panelNorth.add(btnSearch);
		
		//compareInputLabels	
		JButton btnCompareLabels = new JButton("Compare Labels");
		btnCompareLabels.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				//empty table contents, in cases where new search is issued
				  model.setRowCount(0);				
				// Bind new Data.
				//PopulateData();
				String v_idea = "", subject = "", verb = "",object = 	"",PrevSentence = "",CurrSentence = "";				  
				for (String bi : baseIdeas) {
					String words[] = bi.split(" ");
//					v_idea =   words[0];
//					entity = words[1];
//					action = words[2];
//					pep = 	 words[3];
					
					if(words.length==3 ){
						v_idea =  words[0];	subject =   words[1];	verb =  words[2];						
					}
					//capture object as well - form triples
					else if(words.length==4){
						v_idea =  words[0];		subject =   words[1];		verb =  words[2];			object =  words[3];
						
					}				
//					//capture the second set of triple, if more columns are provided in the inputfile
//					else if (words.length>5){
//						v_idea =  words[0] ;
//						subject =   words[1];
//						verb =  words[2];
//						second_subject = words[4];
//						second_verb = words[5];
//						second_object = words[6];
//						
//					}
					
					//String searchWords = words[1] + " " + words[2] + " " + words[3];
					String searchWords = subject + " " + verb + " " + object;
										
					//once user click submit button, export the data to file                  			  
					  if (txtPepNumber.getText() != null){
						  pepNum = Integer.parseInt(txtPepNumber.getText());
						  System.out.println("o ");
						  queryAndDisplayDataInGrid(searchWords, pepNum, v_idea);
					  }
					  else{
						  System.out.println("i ");
						  queryAndDisplayDataInGrid(searchWords, -1, v_idea);
					  }
				}
			}
		});
		panelNorth.add(btnCompareLabels);		
		getContentPane().add(panelNorth,BorderLayout.NORTH);		
		JButton button = new JButton("See Message Details of Row");
		 //JScrollPane scroll = new JScrollPane(table);	        
		
		 //JTextField jtf = new JTextField(20);
		JTextField firstSearchword, secondSearchword, thirdSearchword;
		firstSearchword = new JTextField(20);
		// firstSearchword.add(new JLabel("ST "), "West");
	    secondSearchword = new JTextField(20);
	    //  secondSearchword.add(new JLabel("ST "), "West");
	    thirdSearchword = new JTextField(20);
	    JButton b1;
	    b1 = new JButton("submit");
	    JLabel label = new JLabel("Enter search keyswords");
	    JPanel panel = new JPanel();
	    panel.add(label, BorderLayout.SOUTH);
	    panel.add(firstSearchword);	    panel.add(secondSearchword);	    panel.add(thirdSearchword);
	    panel.add(button,BorderLayout.SOUTH);
	    panel.add(b1,BorderLayout.SOUTH);
     
        getContentPane().add(panel, BorderLayout.SOUTH);
    //  getContentPane().add(scroll);
		  b1.addActionListener(new ActionListener() {
       
          public void actionPerformed(ActionEvent e) {           
          	  String searchWords;                  
                searchWords = firstSearchword.getText().trim();  
                first = firstSearchword.getText().trim();  
                //once user click submit button, export the data to file
                System.out.println("p ");
                queryAndDisplayDataInGrid(searchWords,pepNum,"");            
          }
        });
     
        button.addActionListener(new ActionListener() {       
         public void actionPerformed(ActionEvent e) {           	
           int i = table.getSelectedRow();
           Integer p = Integer.parseInt( model.getValueAt(i, 1).toString() );
           ShowMessageForID(p);
           System.out.println("p " + p + " i " + i);
         }
        });			
		// Bind and PopulateData'
		ShowTableColumnHeaders();
	}
	
	void populateLbelsComboBox(JComboBox comboBox) throws SQLException {
	       Statement  st1 = null;
	       String rq1 = "SELECT DISTINCT label  FROM  "+tableName+"";// order by label UNION SELECT A ";
	       try {
	    	   st1 = conn.createStatement();
	           ResultSet rs1 = st1.executeQuery(rq1);
	           while (rs1.next()) {
	        	   comboBox.addItem(rs1.getString(1));
	           }

	       }  catch (SQLException e) { 
	            e.printStackTrace();
	       }
	       finally{	        
	           st1.close();
	           //rs1.close();
	       }
	    }
		
	private  void ShowTableColumnHeaders() {		
		String[] columnNames = {"PepNumber","MessageID", "previous sentence", "current sentence", "next sentence"};		
		// Clear table
		table.setModel(new DefaultTableModel());		
		// Model for Table
		model = (DefaultTableModel)table.getModel();
		model.addColumn("PepNumber");	model.addColumn("MessageID");	model.addColumn("Label");	model.addColumn("previous sentence");	model.addColumn("current sentence");	model.addColumn("next sentence");		
	}
	
	public static void queryAndDisplayDataInGrid(String wordList, Integer v_pepNum, String v_idea) {
		//String[] columnNames = {"previous sentence", "current sentence", "next sentence", "MessageID"};
		String[] columnNames = {"PepNumber","MessageID", "previous sentence", "current sentence", "next sentence"};
		JButton button = new JButton("See Message Details of Row");
		System.out.println("a selected: "+ wordList);
		String pepNumber,label="",previousSentence = "",currentSentence = "",nextSentence = "",v_message = "",v_messageID = "",cou = "",forOutput_mainSentence = "",forOutput_previousSentence = "",
				forOutput_nextSentence = "",output = "";
		String[] sentences = new String[1000];
		Statement stmt = null;		ResultSet rs;	
		Integer forOutput_pepNumber = null, resultCounter=0;	
		try {
			stmt = conn.createStatement();

			
			//pepNumber not supplied
			if(v_pepNum == -1){
				rs = stmt.executeQuery("select messageID, pep,label,ps, currentSentence,ns,dateTimeStamp from "+tableName+" where label like '%"+wordList+"%'  order by pep,dateTimeStamp"); 
			}
			else
				rs = stmt.executeQuery("select messageID, pep,label,ps, currentSentence,ns,dateTimeStamp from "+tableName+" where pep = " + v_pepNum + " and label like '%"+wordList+"%' order by pep,dateTimeStamp");

			while (rs.next()) {
				//v_message = rs.getString(1).toLowerCase();
				v_messageID = rs.getString(1);				pepNumber = rs.getString(2);				label = rs.getString(3);
				previousSentence = rs.getString(4);			currentSentence = rs.getString(5);			nextSentence = rs.getString(6);
				model.addRow(new Object[]{pepNumber, v_messageID,label, previousSentence, currentSentence, nextSentence});
				resultCounter++;
			}

			table.getColumnModel().getColumn(2).setCellRenderer(new TableCellLongTextRenderer ());  
			table.getColumnModel().getColumn(3).setCellRenderer(new TableCellLongTextRenderer ()); 
			table.getColumnModel().getColumn(4).setCellRenderer(new TableCellLongTextRenderer ());  
			table.setRowHeight(100);

			TableColumn column = null;
			for (int i = 0; i < 4; i++) {
				column = table.getColumnModel().getColumn(i);
				if (i == 0) {
					column.setPreferredWidth(10); //third column is bigger
				}
				else if (i == 1) {
					column.setPreferredWidth(10); //third column is bigger
				} 
				else if (i == 2) {
					column.setPreferredWidth(10); //third column is bigger
				} 
				else if (i==3){
					column.setPreferredWidth(50);
				}
				else {
					column.setPreferredWidth(75);
				}
				System.out.println(resultCounter+" rows");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	//test all words exist in sentence
	// its used but not sure how
	public static boolean contains(String [] array, String v_sentenceString){
		boolean test = true;
		for (String w : array){
			if (!v_sentenceString.contains(w))
					test = false;
			//else 
				//test = false;
		}		
		return test;
	}
	
	public void ShowMessageForID(Integer v_mid) {		 
	    	Statement stmt = null;			
			ResultSet rs;	
			try {
				stmt = conn.createStatement();
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
	
			//JFrame frame = new JFrame(v_subject);
	    	
	        EventQueue.invokeLater(new Runnable() {
	            @Override
	            public void run() {	                
	                JFrame frame = new JFrame(v_subject);
	                //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	                frame.setLayout(new BorderLayout());
	                frame.add(new FindTextPane());
	                frame.setSize(500, 700);
	                //frame.setLocationRelativeTo(null);
	                frame.setVisible(true);
	            }
	        });
	    }
	
	 public class FindTextPane extends JPanel {
	        private JTextField findField;
	        private JButton findButton;
	        private JTextArea textArea;
	        private int pos = 0;
	        public FindTextPane() {
	            setLayout(new BorderLayout());
	            findButton = new JButton("Next");
	            findField = new JTextField("", 10);
	            textArea = new JTextArea();
	            textArea.setWrapStyleWord(true);
	            textArea.setLineWrap(true);
	             textArea.setText(v_message);
	            	           
	            JPanel header = new JPanel(new GridBagLayout());
	            GridBagConstraints gbc = new GridBagConstraints();
	            gbc.gridx = 0;
	            gbc.gridy = 0;
	            gbc.anchor = GridBagConstraints.WEST;
	            header.add(findField, gbc);
	            gbc.gridx++;
	            header.add(findButton, gbc);
	            add(header, BorderLayout.NORTH);
	            add(new JScrollPane(textArea));
	            
	            Highlighter highlighter = new UnderlineHighlighter(null);
	            final JTextPane textPane = new JTextPane();
	            textPane.setHighlighter(highlighter);
	            final WordSearcher searcher = new WordSearcher(textArea, Color.red);
	                  int offset = searcher.search(termToHighlight);
	                  if (offset != -1) {
	                    try {
	                    	scrollRectToVisible(textPane.modelToView(offset));
	                    } catch (BadLocationException e) {
	                    }
	                  }
	                
	            findButton.addActionListener(new ActionListener() {
	                @Override
	                public void actionPerformed(ActionEvent e) {
	                    // Get the text to find...convert it to lower case for eaiser comparision
	                    String find = findField.getText().toLowerCase();
	                    // Focus the text area, otherwise the highlighting won't show up
	                    textArea.requestFocusInWindow();
	                    // Make sure we have a valid search term
	                    if (find != null && find.length() > 0) {
	                        Document document = textArea.getDocument();
	                        int findLength = find.length();
	                        try {
	                            boolean found = false;
	                            // Rest the search position if we're at the end of the document
	                            if (pos + findLength > document.getLength()) {
	                                pos = 0;
	                            }
	                            // While we haven't reached the end...
	                            // "<=" Correction
	                            while (pos + findLength <= document.getLength()) {
	                                // Extract the text from teh docuemnt
	                                String match = document.getText(pos, findLength).toLowerCase();
	                                // Check to see if it matches or request
	                                if (match.equals(find)) {
	                                    found = true;
	                                    break;
	                                }
	                                pos++;
	                            }
	                            // Did we find something...
	                            if (found) {
	                                // Get the rectangle of the where the text would be visible...
	                                Rectangle viewRect = textArea.modelToView(pos);
	                                // Scroll to make the rectangle visible
	                                textArea.scrollRectToVisible(viewRect);
	                                // Highlight the text
	                                textArea.setCaretPosition(pos + findLength);
	                                textArea.moveCaretPosition(pos);
	                                // Move the search position beyond the current match
	                                pos += findLength;
	                            }
	                        } catch (Exception exp) {
	                            exp.printStackTrace();
	                        }
	                    }
	                }
	            });
	        }
	    }
	
	/*
	public static void showMessageForID(Integer v_mid, String termToHighlight){
		Statement stmt = null;		
		ResultSet rs;
		String v_message = "";
		try {
			
			stmt = conn.createStatement();
			rs = stmt.executeQuery("select email from allmessages where  messageID = " + v_mid);
			int sentenceCounter = 0;
//			System.out.println("here tree 0" + rs.toString());
			while (rs.next()) {
//				System.out.println("here tree 0");
				v_message = rs.getString(1);
			
			}
		}
		catch (SQLException e) {

		}
		
		JFrame frame = new JFrame("JTextArea example");
        FlowLayout flowLayout = new FlowLayout();
        frame.getContentPane().setLayout(flowLayout);
        JTextArea txtArea = new JTextArea(v_message, 50,50);        
        JScrollPane scroll = new JScrollPane(txtArea);
        
        frame.getContentPane().add(scroll);       
        frame.getContentPane().setComponentOrientation(ComponentOrientation.UNKNOWN);
        frame.setSize(300,400);
        frame.pack();
        frame.setVisible(true);  
        
        Highlighter highlighter = new UnderlineHighlighter(null);
        final JTextPane textPane = new JTextPane();
        textPane.setHighlighter(highlighter);
        final WordSearcher searcher = new WordSearcher(txtArea);
              int offset = searcher.search(termToHighlight);
              if (offset != -1) {
                try {
                	scroll.scrollRectToVisible(textPane
                      .modelToView(offset));
                } catch (BadLocationException e) {
                }
              }
        
	}
	*/
}
