package postProcess;

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

import Process.processMessages.LabelTriples;
import Process.processMessages.PythonSpecificMessageProcessing;
import Read.readRepository.ReadLabels;
import connections.MysqlConnect;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JComboBox;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.process.DocumentPreprocessor;
import utilities.TableCellLongTextRenderer;
import utilities.UnderlineHighlighter;
import utilities.WordSearcher;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class visualizeResults extends JPanel { 	//JFrame
	private JTextField txtKeyword, txtPepNumber;
	private static JTable table;
	static DefaultTableModel model;	
	static JButton btnSearch = new JButton("Search");// Button Search
	//shud replace above variables
	static MysqlConnect mc = new MysqlConnect();
	static Connection conn = mc.connect();
	
	static String first;	
	static Boolean postback = false;	
	static Integer pepNum  = -1;	
	static PythonSpecificMessageProcessing pm = new PythonSpecificMessageProcessing();
	static String baseIdeas[];	
	static String labelTableName = "labels",dataTableName = "";
	//can be a standloane applcation or can be called from anotehr gui, therfore some linesare commented and JFrame is convereted to JPanel
	public static void main(String[] args) {		
		//read base ideas to match in string matching
	   ReadLabels b = new ReadLabels();
	   ArrayList<LabelTriples> labels = new ArrayList<LabelTriples>();
	   									//"C:/scripts/input-BaseIdeas8.txt";
	   String baseIdeasFilename = "C:/Users/psharma/Google Drive/PhDOtago/code/scripts/inputFiles/input-BaseIdeas.txt";
	   try {
		   labels = b.readLabelsFromFile(baseIdeasFilename,labels, "Keyword matching using these base concepts",labelTableName,conn,false);
		} 
	    catch (Exception e) {
			e.printStackTrace();
		}
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				visualizeResults frame = new visualizeResults();
				frame.setVisible(true);
			}
		});
	}

	//initiate from main
	public void initiateFromMain(int p) {
		String pep = String.valueOf(p);
		if(pep==null || pep.isEmpty() || pep.equals("")) {}
		else {
			txtPepNumber.setText(pep);		
			btnSearch.doClick();
		}
	}
	
	// Create the frame.
	public visualizeResults() {
		
		//These are commented as the frame cannot be called into another frame, so we change it to JPanel and com ment the following lines
//$$		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//$$		setBounds(0, 0, 1350, 750);		setTitle("Visualising Results");
//$$		getContentPane().
		setLayout(new BorderLayout());
		
		// ScrollPane
		JScrollPane scrollPane = new JScrollPane();		scrollPane.setBounds(50, 50, 1200, 600);
//$$		getContentPane().
		add(scrollPane, BorderLayout.CENTER);
		
		// Table
		table = new JTable(); scrollPane.setViewportView(table);		//table.setFont(new Font("Serif", Font.PLAIN, 10));
		
		JPanel panelNorth = new JPanel();	
		//allow user to chose from which triple table he wants to use
		String[] tableNames = new String[] {"results","results_postprocessed"};
		JComboBox<String> tableNamesList = new JComboBox<>(tableNames);
		//add to the parent container (e.g. a JFrame):
		JLabel lblTable = new JLabel("Select Table");		lblTable.setBounds(80, 27, 84, 14);		// Label Search
		panelNorth.add(lblTable);	
		panelNorth.add(tableNamesList);
		//get the selected item:
		tableNamesList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {          
				//String selectedsTo = (String) tableNamesList.getSelectedItem();
				dataTableName = (String) tableNamesList.getSelectedItem();
			}
		});
		
		
		JLabel lblSearch = new JLabel("Search Words");		lblSearch.setBounds(101, 27, 84, 14);		// Label Search
		panelNorth.add(lblSearch);		
		
		// Keyword
		txtKeyword = new JTextField(); 		txtKeyword.setBounds(195, 24, 160, 20);
		panelNorth.add(txtKeyword);			txtKeyword.setColumns(10);
		// Label Search
		JLabel lblPepNumber = new JLabel("Pep #");			lblPepNumber.setBounds(200, 27, 84, 14);		panelNorth.add(lblPepNumber);
		//pepnumber
		txtPepNumber = new JTextField(); 		txtPepNumber.setBounds(300, 24, 160, 20);
		panelNorth.add(txtPepNumber); 			txtPepNumber.setColumns(10);
		
		// Button Search
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
				String v_idea = "",subject = "",verb = "",object = 	"",PrevSentence = "",CurrSentence = "";
				  
				for (String bi : baseIdeas) {
					String words[] = bi.split(" ");
//					v_idea =   words[0]; entity = words[1]; action = words[2]; 	pep = 	 words[3];
				
					if(words.length==3 ){
						v_idea =  words[0] ;	subject =   words[1];	verb =  words[2];
					}
					//capture object as well - form triples
					else if(words.length==4){
						v_idea =  words[0] ;	subject =   words[1];	verb =  words[2];		object =  words[3];
					}				
//					//capture the second set of triple, if more columns are provided in the inputfile
//					else if (words.length>5){
//						v_idea =  words[0] ;	subject =   words[1];	verb =  words[2];		second_subject = words[4];			second_verb = words[5];
//						second_object = words[6];
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
		JButton button = new JButton("See Message Details of Row");
		JTextField firstSearchword = new JTextField(20),secondSearchword = new JTextField(20),thirdSearchword = new JTextField(20);
	    JButton b1= new JButton("submit");    
	    JPanel panel = new JPanel(); 	    JLabel label = new JLabel("		Enter search keyswords"); 		JLabel spacer = new JLabel("	||	  "); 				
	    panelNorth.add(spacer, BorderLayout.SOUTH);	panelNorth.add(label, BorderLayout.SOUTH);
	    panelNorth.add(firstSearchword); 	panelNorth.add(secondSearchword);		panelNorth.add(thirdSearchword);	panelNorth.add(button,BorderLayout.SOUTH);		panelNorth.add(b1,BorderLayout.SOUTH);
		add(panelNorth,BorderLayout.NORTH);
	    
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
		//show the data
		//we want to see results pep by pep, therefore we dont want to see all results one time
		//but we have this option for later use
//$$		exportdata2("", -1, "");

	}
	
	private  void ShowTableColumnHeaders() {		
		String[] columnNames = {"PepNumber","MessageID", "Date", "current sentence", "next sentence"};
		table.setModel(new DefaultTableModel());// Clear table
		model = (DefaultTableModel)table.getModel();// Model for Table
		model.addColumn("PepNumber");	model.addColumn("MessageID");//		model.addColumn("Date"); //		model.addColumn("Label");
		model.addColumn("ClausIE"); 	model.addColumn("Reasons");		model.addColumn("ps");			model.addColumn("CurrentSentence");
		model.addColumn("ns"); 			model.addColumn("pp");			model.addColumn("ep");			model.addColumn("np");	
	}
	
	public static void exportdata2(String wordList, Integer v_pepNum, String v_idea) {
		//String[] columnNames = {"previous sentence", "current sentence", "next sentence", "MessageID"};
		String[] columnNames = {"PepNumber", "MessageID", "ClausIE","ps","CurrentSentence","ns","pp","ep","np"};
		//"MessageID", "Date","Label",
		JButton button = new JButton("See Message Details of Row");		
		Statement stmt = null;		ResultSet rs;		Date v_date =null;
		String label = null,cie = null,reason = null,ps = "",ns = "",pp = "",ep = "",np = "",currentSentence = "",nextSentence = "",v_message = "",v_messageID = "",cou = "",pepNumber,output = "",
				forOutput_mainSentence = "",forOutput_previousSentence = "",forOutput_nextSentence = "";
		String[] sentences = new String[1000];		String str = "";  //combines search string
		Integer forOutput_pepNumber = null;		String[] words = wordList.split(" ");//assign to all words to array
		Boolean foundinLastRound = false;
		//empty table contents, in cases where new search is issued
		//model.setRowCount(0);

		FileWriter writer = null,writer2 = null;
		try {
			writer = new FileWriter ("C:\\DeMap_Miner\\datafiles\\outputFiles\\ReasonOutput\\prevSelectedNextSentences"+wordList+".txt");  //All the previous, selected and next sentences are output in tHis file.
			writer2 = new FileWriter("C:\\DeMap_Miner\\datafiles\\outputFiles\\ReasonOutput\\SelectedSentences"+ wordList+".txt");	
			str = "";
			for (String w : words){
				//+ s1 + "%' AND email LIKE '%"
				//+ s2 + "%' AND email LIKE '%"
				//				 str += "AND analysewords LIKE '% " + w + " %' ";
			}
			//str = str.substring(3, str.length()); //remove THE FIRST 'AND' 
			stmt = conn.createStatement();
			//pepNumber not supplied
			if(v_pepNum == -1){														//where " + str + "
				rs = stmt.executeQuery("select pep, messageID, date,label, currentSentence, clausie,allReasons, ps, ns, pp, ep, np from "+ dataTableName+ " where clausie IS NOT NULL order by pep, date;"); 
			}
			else {
				rs = stmt.executeQuery("select pep, messageID, date,label, currentSentence, clausie,allReasons, ps, ns, pp, ep, np from " + dataTableName + " where pep = " + v_pepNum + " order by date;");	//" and " + str + 
			}
			int sentenceCounter = 0;				
			System.out.println("here 344 " + rs.toString());
			while (rs.next()) {										
				pepNumber = rs.getString(1);					v_messageID = rs.getString(2);		
					java.util.Date utilDate = rs.getDate(3);					java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());						java.sql.Date finalDate = sqlDate;					
				//v_date = finalDate;		//v_date = rs.getDate(3); //.toLowerCase();
				label = rs.getString(4);					currentSentence = rs.getString(5);				cie = rs.getString(6);					reason = rs.getString(7);
				ps = rs.getString(8);					ns = rs.getString(9);				pp = rs.getString(10);					ep = rs.getString(11);				np = rs.getString(12);					
				String added = pepNumber + "\n, " +v_messageID+ "\n, " + label + "\n, " + cie; 
				//v_messageID,"", label,
				model.addRow(new Object[]{added,v_messageID, cie, reason,ps,  currentSentence,ns,pp,ep,np});

				/*
					if (!(v_message == "null") || !(v_message == "") || !(v_message.isEmpty())) 
					{
			//			System.out.println("here tree 00");
						try {
							if (v_message.length() > 0) {
	//							System.out.println("here tree 000");
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

				 */
			} // end while

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

			//working - sets the cell wrapping
			table.getColumnModel().getColumn(2).setCellRenderer(new TableCellLongTextRenderer ());			table.getColumnModel().getColumn(3).setCellRenderer(new TableCellLongTextRenderer ()); 
			table.getColumnModel().getColumn(4).setCellRenderer(new TableCellLongTextRenderer ());			table.getColumnModel().getColumn(5).setCellRenderer(new TableCellLongTextRenderer ());  
			table.getColumnModel().getColumn(6).setCellRenderer(new TableCellLongTextRenderer ()); 			table.getColumnModel().getColumn(7).setCellRenderer(new TableCellLongTextRenderer ());  
			table.getColumnModel().getColumn(8).setCellRenderer(new TableCellLongTextRenderer ()); 			//table.getColumnModel().getColumn(9).setCellRenderer(new TableCellLongTextRenderer ());  
			//table.getColumnModel().getColumn(9).setCellRenderer(new TableCellLongTextRenderer ()); 		//table.getColumnModel().getColumn(10).setCellRenderer(new TableCellLongTextRenderer ());  
			//table.getColumnModel().getColumn(11).setCellRenderer(new TableCellLongTextRenderer ());
			table.setRowHeight(80);
			table.getColumnModel().removeColumn( table.getColumnModel().getColumn(1) );

			TableColumn column = null;
			for (int i = 0; i < 8; i++) {
				column = table.getColumnModel().getColumn(i);
				if (i == 0) {		column.setPreferredWidth(4); //third column is bigger
				}
				else if (i == 1) {	column.setPreferredWidth(5); //third column is bigger
				} 
				else if (i == 2) {	column.setPreferredWidth(12); //third column is bigger
				} 
				else if (i==3){	column.setPreferredWidth(12);
				}
				else {		column.setPreferredWidth(12);			}
			}
			//column.setPreferredWidth(50);

			if (sentenceCounter < 1) {
				//JOptionPane.showMessageDialog(null, "No Record Found", "Error",
				//		JOptionPane.ERROR_MESSAGE);
				System.out.println(sentenceCounter + " Records Found");
			}
			if (sentenceCounter == 1) {				System.out.println(sentenceCounter + " Record Found");			} 
			else {				System.out.println(sentenceCounter + " Records Found");			}
			
			writer.close();			writer2.close();
		} catch (SQLException e) {
			e.printStackTrace();			System.out.println("e.str "+ e.toString());			System.out.println(StackTraceToString(e)  );
		} catch (IOException e1) {
			// TODO Auto-generated catch block				
			e1.printStackTrace();			System.out.println(StackTraceToString(e1)  );
		}
		catch (Exception e2) {
			// TODO Auto-generated catch block				
			e2.printStackTrace();			System.out.println(StackTraceToString(e2)  );
		}
		System.out.println("str " + str);

		/*
		//Run Word CoOccurence 
		WordCoOccurence wco = new WordCoOccurence();
		String inputFilename = "c:\\scripts\\output\\SelectedSentences"+ wordList+".txt";
		String outputFilename = "c:\\scripts\\output\\SelectedSentences"+ wordList+"CoOccurence.txt";
		try {
			wco.callWordCoOccurenceWithParamters(inputFilename,words, outputFilename);
		} catch (FileNotFoundException | UnsupportedEncodingException e) {			
			e.printStackTrace();
		}
		 */
	}
	
	public static String StackTraceToString(Exception ex) {
		String result = ex.toString() + "\n";
		StackTraceElement[] trace = ex.getStackTrace();
		for (int i=0;i<trace.length;i++) {
			result += trace[i].toString() + "\n";
		}
		return result;
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
        FlowLayout flowLayout = new FlowLayout();        frame.getContentPane().setLayout(flowLayout);
        JTextArea txtArea = new JTextArea(v_message, 50,50);

        JScrollPane scroll = new JScrollPane(txtArea);
        frame.getContentPane().add(scroll);
      //  txtArea.setCaretPosition(LocOfSearchWord);        
        frame.getContentPane().setComponentOrientation(ComponentOrientation.UNKNOWN);
        frame.setSize(300,400);        frame.pack();        frame.setVisible(true);        
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
