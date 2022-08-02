package relationExtraction;

import java.awt.BorderLayout;
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

public class ExtractFileLabelsTriplesFromExtractedRelations {
	private JTextField txtKeyword, txtPepNumber;
	private static JTable table;	
	static DefaultTableModel model;
	static String first;	
	static Boolean postback = false;	
	static Integer pepNum  = -1;	
	static PythonSpecificMessageProcessing pm = new PythonSpecificMessageProcessing();
	
	static String baseIdeas[];
	static String labelTableName = "labels";
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {		
		MysqlConnect mc = new MysqlConnect();
		Connection conn= mc.connect();
		
		//read base ideas to match in string matching
		ReadLabels b = new ReadLabels();
		String baseIdeasFilename = "C:/Users/psharma/Google Drive/PhDOtago/scripts/checkReasonsInSentences.txt";
		ArrayList<LabelTriples> labels = new ArrayList<LabelTriples>();
		labels = b.readLabelsFromFile(baseIdeasFilename,labels, "Keyword matching using these base concepts",labelTableName,conn,false);	
	   									//"C:/scripts/input-BaseIdeas8.txt";				//ForReasons	  	   
	    String v_idea = "",subject = "",verb = "", object = 	"",PrevSentence = "",CurrSentence = "";		
		FileWriter writer = null, writer2 = null;
	
		  
		for (String bi : baseIdeas) {
			String words[] = bi.split(" ");
//			v_idea =   words[0];//			entity = words[1];//			action = words[2];//			pep = 	 words[3];
			
			if(words.length==3 ){
				v_idea =  words[0] ;				subject =   words[1];				verb =  words[2];
				
			}
			//capture object as well - form triples
			else if(words.length==4){
				v_idea =  words[0] ;				subject =   words[1];				verb =  words[2];				object =  words[3];
				
			}				
//			//capture the second set of triple, if more columns are provided in the inputfile
//			else if (words.length>5){
//				v_idea =  words[0] ;		subject =   words[1];			verb =  words[2];		second_subject = words[4];		second_verb = words[5];		second_object = words[6];			
//			}
			
			//i happens very often so seprate	
			if(subject.toLowerCase().equals("i")){		
				subject = " " + subject + " ";
				//CurrentSentenceString = " " + CurrentSentenceString;
			}
			
			//replce the underscore to seprate the combined terms
			if (subject.contains("_")){
				subject = subject.replace("_"," ");
			}
			if (verb.contains("_")){
				verb = verb.replace("_"," ");
			}
			if (object.contains("_")){
				object = object.replace("_"," ");
			}
			
			//String searchWords = words[1] + " " + words[2] + " " + words[3];
			String searchWords = subject + " " + verb + " " + object;
			//exportdata2(searchWords, -1, v_idea);
			exportdata2(subject, verb, object, -1, v_idea);
			System.out.println("Processed idea: " + searchWords);
		}
	}
	
	public static void exportdata2(String subject, String verb, String object, Integer v_pepNum, String v_idea) {
		
		Statement stmt = null;		Connection conn = null;
		String pepNumber,previousSentence = "",currentSentence = "",nextSentence = "", v_message = "", v_messageID = "",cou = "",output = "", forOutput_mainSentence = "",forOutput_previousSentence = "",forOutput_nextSentence = "";
		String[] sentences = new String[1000];		
		ResultSet rs;		
		Integer forOutput_pepNumber = null;
		
		//assign to all words to array
//xxx		String[] words = wordList.split(" ");
		
		Boolean foundinLastRound = false;
		
		//empty table contents, in cases where new search is issued
		//model.setRowCount(0);
		FileWriter writer = null;
		
		try {
			writer  = new FileWriter("C:\\Users\\psharma\\Google Drive\\PhDOtago\\scripts\\outputFiles\\ExtractedRelations_"+ v_idea+"-"+ subject+"-"+verb+"-"+object + ".txt");  //All the previous, selected and next sentences are output in tHis file.
			//writer2 = new FileWriter("C:\\Users\\psharma\\Google Drive\\PhDOtago\\scripts\\outputFiles\\SelectedSentencesOnly_"+ v_idea+"-"+ subject+"-"+verb+"-"+object + ".txt");				
			//writer3 = new FileWriter("C:\\Users\\psharma\\Google Drive\\PhDOtago\\scripts\\outputFiles\\WithAllParagraphs_"+ v_idea+"-"+ subject+"-"+verb+"-"+object + ".txt");
		} catch (IOException e1) {
			// TODO Auto-generated catch block				
			e1.printStackTrace();
		}
		
		String arg1 =null, relation = null,arg2 = null,sentence = null, str = "";  //combines search string
			
			try {			
				str = "";		
				 try {
					Class.forName("com.mysql.jdbc.Driver");
				} catch (ClassNotFoundException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				stmt = conn.createStatement();
				
				//pepNumber not supplied
				if(v_pepNum == -1){																	//" + str + "   - commented because we now check for terms in sentence, not entire paragraph
					rs = stmt.executeQuery("select arg1, relation, arg2, sentence from relations "
							+ "where (arg1 like '%" +subject + "%'  OR arg1 like '%" +object + "%')"  
							+ "AND (relation like '%" +verb + "%' )"  
							+ "AND (arg2 like '%" +subject + "%'  OR arg2 like '%" +object + "%')");  							
				}
				else																									// and " + str + "  - as above comment
					rs = stmt.executeQuery("select arg1, relation, arg2, sentence from allpeps where pep = " + v_pepNum + " order by date2");
						
				int relationsCounter = 0;
				
	//			System.out.println("here tree 0" + rs.toString());
				while (rs.next()) {
					arg1 = rs.getString(1).toLowerCase(); 					relation = rs.getString(2).toLowerCase(); 					arg2 = rs.getString(3).toLowerCase();
					sentence = rs.getString(4).toLowerCase();
	
					try	{									//next sentence			//paragraph
						writer.append (arg1 + " " + relation + " " + arg2 + " "+sentence);
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}	
					finally { }
					 // end if
					relationsCounter=relationsCounter+1;
				} // end while
			
	
			if (relationsCounter < 1) {
				//JOptionPane.showMessageDialog(null, "No Record Found", "Error",
				//		JOptionPane.ERROR_MESSAGE);
				System.out.println(relationsCounter + " Records Found");
			}
			if (relationsCounter == 1) {
				System.out.println(relationsCounter + " Record Found");
			} else {
				System.out.println(relationsCounter + " Records Found");
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
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
		
		
		try {
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//test all words exist in sentence
	// its used but not sure how
	public static boolean contains(String [] array, String v_sentenceString){
		boolean test = true;
		for (String w : array){
			if (!v_sentenceString.toLowerCase().contains(w.toLowerCase()))
					test = false;
			//else 
				//test = false;
		}		
		return test;
	}
	
	public static boolean containsSVO(String subject, String verb, String object, String v_sentenceString){
		boolean test = false;		
		if (v_sentenceString.toLowerCase().contains(subject) &&			v_sentenceString.toLowerCase().contains(verb) &&			v_sentenceString.toLowerCase().contains(object)  )		{
				test = true;
		}
		//else 
			//test = false;		
		return test;
	}
	
	public static void showMessageForID(Integer v_mid){
		Statement stmt = null;	Connection conn = null;		ResultSet rs;
		String v_message = "";
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery("select email from allpeps where  messageID = " + v_mid);
			int sentenceCounter = 0;
//			System.out.println("here tree 0" + rs.toString());
			while (rs.next()) {
//				System.out.println("here tree 0");
				v_message = rs.getString(1);			
			}
		}
		catch (SQLException e) {
		}		
	}
	
	public static Connection connect(String db_connect_str, String db_userid,String db_password) {
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
}
