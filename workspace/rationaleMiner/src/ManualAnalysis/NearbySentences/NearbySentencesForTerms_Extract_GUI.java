package ManualAnalysis.NearbySentences;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.EventQueue;
import java.awt.FlowLayout;
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
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.text.BadLocationException;
import javax.swing.text.Highlighter;

import readRepository.readRepository.ReadLabels;
import connections.MysqlConnect;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.process.DocumentPreprocessor;
import miner.process.LabelTriples;
import miner.process.PythonSpecificMessageProcessing;
import utilities.TableCellLongTextRenderer;
import utilities.UnderlineHighlighter;
import utilities.WordSearcher;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class NearbySentencesForTerms_Extract_GUI extends JFrame {
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
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				NearbySentencesForTerms_Extract_GUI frame = new NearbySentencesForTerms_Extract_GUI();
				frame.setVisible(true);
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public NearbySentencesForTerms_Extract_GUI() {
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
		
		// Button Search
		JButton btnSearch = new JButton("Search");
		btnSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// Bind new Data.
				//PopulateData();
				
				//empty table contents, in cases where new search is issued
				  model.setRowCount(0);
				
				  String searchWords, searchWordsB,searchWordsC;
				 
                 // searchWords = txtKeyword.getText().trim();  
                  //first = txtKeyword.getText().trim();  
				  searchWords = txtKeyword.getText();  
                  first = txtKeyword.getText(); 
                  //once user click submit button, export the data to file                  			  
				  if (txtPepNumber.getText() != null){
					  pepNum = Integer.parseInt(txtPepNumber.getText());
					  exportdata2(searchWords, pepNum,"");
				  }
				  else{
					  exportdata2(searchWords, -1,"");
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
				String v_idea = "";
				String subject = "";
				String verb = "";
				String object = 	"";
				String PrevSentence = "";
				String CurrSentence = "";
				  
				for (String bi : baseIdeas) {
					String words[] = bi.split(" ");
//					v_idea =   words[0];
//					entity = words[1];
//					action = words[2];
//					pep = 	 words[3];
					
					if(words.length==3 ){
						v_idea =  words[0] ;
						subject =   words[1];
						verb =  words[2];
						
					}
					//capture object as well - form triples
					else if(words.length==4){
						v_idea =  words[0] ;
						subject =   words[1];
						verb =  words[2];
						object =  words[3];
						
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
						  exportdata2(searchWords, pepNum, v_idea);
					  }
					  else{
						  exportdata2(searchWords, -1, v_idea);
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
	    panel.add(firstSearchword);
	    panel.add(secondSearchword);
	    panel.add(thirdSearchword);
	    panel.add(button,BorderLayout.SOUTH);
	    panel.add(b1,BorderLayout.SOUTH);
     
        getContentPane().add(panel, BorderLayout.SOUTH);
    //  getContentPane().add(scroll);
		  b1.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {           
          	  String searchWords;                  
                searchWords = firstSearchword.getText().trim();  
                first = firstSearchword.getText().trim();  
                //once user click submit button, export the data to file
                exportdata2(searchWords,pepNum,"");
            
          }
        });
     
        button.addActionListener(new ActionListener() {

         @Override
         public void actionPerformed(ActionEvent e) {           	
           int i = table.getSelectedRow();
           Integer p = Integer.parseInt( model.getValueAt(i, 1).toString() );
           showMessageForID(p);
           System.out.println("p " + p + " i " + i);
         }
        });	
		
		// Bind and PopulateData
		ShowTableColumnHeaders();
		

	}
	
	private  void ShowTableColumnHeaders() {
		
		String[] columnNames = {"PepNumber","MessageID", "previous sentence", "current sentence", "next sentence"};
		
		// Clear table
		table.setModel(new DefaultTableModel());
		
		// Model for Table
		model = (DefaultTableModel)table.getModel();
		model.addColumn("PepNumber");
		model.addColumn("MessageID");
		model.addColumn("Label");
		model.addColumn("previous sentence");
		model.addColumn("current sentence");
		model.addColumn("next sentence");
				
	}
	
	public static void exportdata2(String wordList, Integer v_pepNum, String v_idea) {
		//String[] columnNames = {"previous sentence", "current sentence", "next sentence", "MessageID"};
		String[] columnNames = {"PepNumber","MessageID", "previous sentence", "current sentence", "next sentence"};
		JButton button = new JButton("See Message Details of Row");
		Statement stmt = null;
		//Connection conn = null;
		String pepNumber,previousSentence = "",currentSentence = "",nextSentence = "",v_message = "",v_messageID = "",cou = "",output = "",forOutput_mainSentence = "",forOutput_previousSentence = "",forOutput_nextSentence = "";;
		String[] sentences = new String[1000];
		ResultSet rs;		
		Integer forOutput_pepNumber = null;
		
		//assign to all words to array
		String[] words = wordList.split(" ");
		Boolean foundinLastRound = false;		
		String str = "";  //combines search string
		
			FileWriter writer = null;
			FileWriter writer2 = null;
			try {
				writer = new FileWriter("C:\\Users\\psharma\\Google Drive\\PhDOtago\\scripts\\ReasonOutput\\prevSelectedNextSentences"+wordList+".txt");  //All the previous, selected and next sentences are output in tHis file.
				writer2 = new FileWriter("C:\\Users\\psharma\\Google Drive\\PhDOtago\\scripts\\ReasonOutput\\SelectedSentences"+ wordList+".txt");				
			} catch (IOException e1) {
				// TODO Auto-generated catch block				
				e1.printStackTrace();
			}
			
			try {
				str = "";
				for (String w : words){
					//+ s1 + "%' AND email LIKE '%"
					//+ s2 + "%' AND email LIKE '%"
					 str += "AND analysewords LIKE '% " + w + " %' ";
				}
				str = str.substring(3, str.length()); //remove THE FIRST 'AND' 
				
				stmt = conn.createStatement();
				
				//pepNumber not supplied
				if(v_pepNum == -1){
					rs = stmt.executeQuery("select email, messageID, pep, from keywordMatching where " + str + " order by pep"); 
				}
				else
					rs = stmt.executeQuery("select email, messageID, pep, from keywordMatching where pep = " + v_pepNum + " and " + str + " order by pep");
						
				int sentenceCounter = 0;				
	//			System.out.println("here tree 0" + rs.toString());
				while (rs.next()) {					
					v_message = rs.getString(1).toLowerCase();
					v_messageID = rs.getString(2);
					pepNumber = rs.getString(3);
					//System.out.println(pepNumber + "here tree 0");
					// model.addRow(new Object[]{ v_messageID});
					if (!(v_message == "null") || !(v_message == "") || !(v_message.isEmpty())) {
			//			System.out.println("here tree 00");
						try {
							if (v_message.length() > 0) {
	//							System.out.println("here tree 000");
								Reader reader = new StringReader(v_message);
								DocumentPreprocessor dp = new DocumentPreprocessor(reader);
								previousSentence = "";
								for (List<HasWord> eachSentence : dp)	{
									String sentenceString = Sentence.listToString(eachSentence);
									currentSentence = sentenceString;
									if (foundinLastRound == true)	{
										try {
											writer.append(output + " | " + currentSentence);	
											
											//model.addRow(new Object[]{ pepNumber ,v_messageID, output, currentSentence});
											//working
											//model.addRow(new Object[]{forOutput_pepNumber, v_messageID, output, forOutput_mainSentence});
											//previous
											model.addRow(new Object[]{forOutput_pepNumber, v_messageID,v_idea, forOutput_previousSentence, forOutput_mainSentence, currentSentence});
											
										} catch (IOException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}									
										foundinLastRound = false;
									}
									
									
									// sentences[sentenceCounter] = sentenceString;
									// if ( secondSearchword.getText().isEmpty()){
	//								System.out.println("here tree 1");
									//if (sentenceString.contains(s2) && sentenceString.contains(s1)  && sentenceString.contains(s3) && !(sentenceString.contains("Lemburg")) ) 
									if (contains(words, sentenceString))										
									{
										System.out.println("words " + words.toString() + " sentenceString " + sentenceString);
									//	previousSentence = sentenceString;
									//	if (foundinLastRound == true) 
									//	{
										output = "\n" + pepNumber + " | " + v_messageID + " | " + previousSentence + " | " + sentenceString;
										forOutput_mainSentence = currentSentence;
										forOutput_previousSentence = previousSentence;
										//String forOutput_nextSentence = "";
										forOutput_pepNumber = Integer.parseInt(pepNumber);
								        //set data to the model
		//						        model.setDataVector(pepNumber, output);        
		//						        model.addRow(output);
								//		LocOfSearchWord = 
		//								Integer locOfSentence = v_message.indexOf(sentenceString);
		//								LocOfSearchWord = v_message.indexOf(sentenceString,locOfSentence );
										
										//	
										//}
										try {
											writer2.append("\n" + sentenceString);
										} catch (IOException e1) {
											// TODO Auto-generated catch block
											e1.printStackTrace();
										}	
										foundinLastRound = true;
									}
									sentenceCounter++;
									previousSentence = sentenceString;
									
								} // end for
							} // end if
						} // end try
						finally { }
					} // end if
				} // end while
			
			//working - sets the cell wrapping
				table.getColumnModel().getColumn(2).setCellRenderer(new TableCellLongTextRenderer ());  
				table.getColumnModel().getColumn(3).setCellRenderer(new TableCellLongTextRenderer ()); 
				table.getColumnModel().getColumn(4).setCellRenderer(new TableCellLongTextRenderer ());  
				
	        table.setRowHeight(100);
	        
	        TableColumn column = null;
	        for (int i = 0; i < 4; i++) {
	            column = table.getColumnModel().getColumn(i);
	            if (i == 0) {
	                column.setPreferredWidth(40); //third column is bigger
	            }
	            else if (i == 1) {
	                column.setPreferredWidth(40); //third column is bigger
	            } 
	            else if (i == 2) {
	                column.setPreferredWidth(200); //third column is bigger
	            } 
	            else if (i==3){
	                column.setPreferredWidth(500);
	            }
	            else {
	                column.setPreferredWidth(200);
	            }
	        }
	        //column.setPreferredWidth(50);
	 
			
			if (sentenceCounter < 1) {
				//JOptionPane.showMessageDialog(null, "No Record Found", "Error",
				//		JOptionPane.ERROR_MESSAGE);
				System.out.println(sentenceCounter + " Records Found");
			}
			if (sentenceCounter == 1) {
				System.out.println(sentenceCounter + " Record Found");
			} else {
				System.out.println(sentenceCounter + " Records Found");
			}
			try {
				writer.close();
				writer2.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
			
		System.out.println("str " + str);
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
	
	public static void showMessageForID(Integer v_mid){
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
    //    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        FlowLayout flowLayout = new FlowLayout();
        frame.getContentPane().setLayout(flowLayout);

        JTextArea txtArea = new JTextArea(v_message, 50,50);
        
        JScrollPane scroll = new JScrollPane(txtArea);

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
                	scroll.scrollRectToVisible(textPane
                      .modelToView(offset));
                } catch (BadLocationException e) {
                }
              }
		
	}
	
}
