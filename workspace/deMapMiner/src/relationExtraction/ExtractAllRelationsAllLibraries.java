package relationExtraction;

import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import callIELibraries.JavaOllieWrapperGUIInDev;
import readRepository.readRepository.ReadLabels;
import callIELibraries.ClausIECaller;
import callIELibraries.ReVerbFindRelations;
import callIELibraries.SocketClientForNLP;
import callIELibraries.UseDISCOSentenceSim;
import de.mpii.clause.driver.ClausIEMain;
import GUI.helpers2.TabbedPane;
import miner.models.Message;
import miner.models.Pep;
import miner.models.tmp;
import miner.process.LabelTriples;
import miner.process.PythonSpecificMessageProcessing;
import miner.processLabels.TripleProcessingResult;
import miner.stringMatching.checkMessage;
import miner.stringMatching.checkSentence;
import connections.MysqlConnect;
import utilities.ComputeMessageDifference;
import utilities.PepUtils;
import utilities.ReadFileLinesIntoArray;
import utilities.TableCellLongTextRenderer;
import utilities.UnderlineHighlighter;
import utilities.Utilities;
import utilities.WordSearcher;

//import example.ShowMessage;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

import org.apache.lucene.index.CorruptIndexException;

import EnglishOrJavaCode.EnglishOrCode;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
//JTable
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.text.BadLocationException;
import javax.swing.text.Highlighter;

// This is the main class for extracting relations
// 29-Nov-2017
// This now stores previous adn next sentences also

//dec 2018, this has already been integrated in the main package
public class ExtractAllRelationsAllLibraries extends JInternalFrame 
{   
	  static List<String> wordsList = new ArrayList<String>();
	  static List<String> statusList = new ArrayList<String>();
	  
	  static Integer min = 0, max = 10000;
	  static Pep[] p = new Pep[max];
      
      static PythonSpecificMessageProcessing pm = new PythonSpecificMessageProcessing();
      
     //## static SocketClientForNLP scnlp = new SocketClientForNLP();	
       
      static boolean selectedAll = false;   //all needed, for reverb only 240 and plus
      static boolean selectedMinMax = false,selectedList = true,justone = false ; 
      static Integer[] selectedListItems = {308,428};//,308,313, 318, 336, 340, 345,376,388,389, 391, 414,428, 435, 441,450, 465,  484, 488, 498, 485, 471, 505, 3003, 3103, 3131,3144}; //, };
      
      static Integer pepNumber = 308;
      static String[] catchTerms = {"pep","bdfl", "vote", "poll", "consensus", "guido", "pronouncement", "disagree"};
      //choose which library to use...can use multiple
      static boolean useReverb = false, useClausIE =true, useOllie = false; 	//which library to use for relation extraction, clausie returns most triples
      static boolean outputDetails = false; 
      
      static String tableToStore = "extractedRelations_clausie";
      static String dev = "C:\\datasets\\python-dev", ideas = "C:\\datasets\\python-dev"; 
      
      //static Integer id=0;
      //for cases when processing gets stuck in middle
      static Integer MID_restart_mark=0,PEP_restart_mark=0; //308;  mid  345507 for pep 8	
      //provide mid and pep # of the case where stuck, don't increment or decrement if error is because of memeory
      //if error cos if message in email cannot be processed, then increment by one
      //errors pep 103, mid?  ...pep 205, mid 281377...pep 214, mid 1022230, 246, 1143350
      //pepe 249, mid 2264228, 
      //288, 1218266
      //pep 318, 1209726 = 723th message of that pep/2000 ~
      //451- 216909
      
      static EnglishOrCode ec = new EnglishOrCode();
      static String proposalIdentifier = "pep";
      
   public static void main(String [] args) throws IOException
   //public PEPStateTransitionStoryline() throws IOException
   {
	   MysqlConnect mc = new MysqlConnect();
	   Connection conn= mc.connect();
		if (conn==null)
			System.exit(0);		   

		mc.checkIfDatabaseEmpty(conn);
		
		ec.initializeTokenizer();
		
		// initialise variables
		//String pep = "",regex = "\\d+";	
		//ArrayList<Integer> numbers = new ArrayList<>();

		PepUtils pu = new PepUtils();
		ArrayList<Integer> UniquePeps = pu.returnUniqueProposalsInDatabase(proposalIdentifier);
		UniquePeps.remove(103);
		UniquePeps.remove(205);
		UniquePeps.remove(214);
		UniquePeps.remove(246);
		UniquePeps.remove(249);
				
		//ReVerb
		ReVerbFindRelations rr  = new ReVerbFindRelations();
		//ClausIE		
		//ClausIECaller cie = null;
		//System.out.println("CLAUSSSSSSSSSSSSSSSSSSS");
		ClausIEMain cie = new ClausIEMain();
		//System.out.println("CLAUSSSSSSSSSSSSSSSSSSS");
		
		//Ollie
		JavaOllieWrapperGUIInDev jw = new JavaOllieWrapperGUIInDev();
	    
	    //JTable 
        System.out.println("waiting for button press event");
	   
        //STSRT
        if (justone){
 		   Integer i = pepNumber; 
	    		   try {
	    					//System.out.println("Pep " + 308); 
	    			   	  // p[i] = new Pep();
	    			   	  // p[i].setPepNumber(i);			   	   
	    				   getAllPEPMessages(mc,conn,i, wordsList, rr, jw, cie); //, snlpt);				   
	    				 //  pm.outputVotes(i); not required vote starts with "call for votes" and end with " vote results"
	    			   	  // p[i].destruct();
	    			   }  catch (Exception e1) {
	    				   System.out.println(e1.toString());
	    			   }
 		   
 		   System.out.println("Message completed ");
	    }
        if (selectedList){
    		   //Integer i = pepNumber;
    		   for (int j = 0; j < selectedListItems.length; j++) { 
    			   Integer i = selectedListItems[j];
	    		   try {
	    			   if(i>=PEP_restart_mark){
		    					//System.out.println("Pep " + 308); 
		    			   	  // p[i] = new Pep();
		    			   	  // p[i].setPepNumber(i);			   	   
		    				   getAllPEPMessages(mc,conn,i, wordsList, rr, jw, cie); //, snlpt);				   
		    				 //  pm.outputVotes(i); not required vote starts with "call for votes" and end with " vote results"
		    			   	  // p[i].destruct();
	    			   }
    			   }   catch (Exception e1)   {
    				   System.out.println(e1.toString());
    			   }
    		   }
    		   System.out.println("Message completed counter ");
	    }
	       	   //selected pep 
        if (selectedMinMax){   		
	       		System.out.println("Selected Min Max");
   		   Integer i = pepNumber;
   		   try {
   			//JFrame parent2 = new JFrame();
    	   // JOptionPane.showMessageDialog(parent2, "button press + all");
//       			   System.out.println("\n");
//       			   System.out.println("Pep " + i + "---------------------------------------------");
   					System.out.println("Pep all"); 
   			   	   //p[i] = new Pep();
   			   	   //p[i].setPepNumber(i);			   	   
   				   getAllPEPMessages(mc,conn,i, wordsList, rr, jw, cie); //, snlpt);				   
   				 //  pm.outputVotes(i); not required vote starts with "call for votes" and end with " vote results"
   			   	   //p[i].destruct();
   			   } catch (Exception e2) {
   				   //continue;
   				   System.out.println(e2.toString());
   			   }
   	   }
   	   //for all peps
   	   if (selectedAll) {
   		System.out.println("Selected All");
   		 //for (int i=289; i<290; i++){        //FOR certain peps		   
   		   //for (int i=min; i<max; i++){        //FOR min and max peps	   
   		   //for (int j = 0; j < numbers.size(); j++) {		//FOR THE Definite NUMBERS
   		   // Integer i = numbers.KJKget(j);									//minmax peps	   
   		   for (int j = 0; j < UniquePeps.size()-20; j++) { 		//-2 because last 2 values are too big
   		   //for (int j = 308; j < 309; j++) {
   			   Integer i = UniquePeps.get(j);			//For the all unique peps
   			 //  Integer i=j;
   			   try { 
   				   		//1 to half of seven done
   				   		//so lets just do te o0ne swe interested in
   				   //we were in halfway for 240
   				   //LATEST WE were middayway with 356, so start with that
  				   if(i>=PEP_restart_mark){
	   				   	  // p[i] = new Pep();
	   				   	  // p[i].setPepNumber(i);			   	   
	   					   getAllPEPMessages(mc,conn,i, wordsList,   rr, jw, cie); //, snlpt);
	   					  // System.gc();
	   				       System.out.println("Cleanup completed..."); 
	   					 //  pm.outputVotes(i); not required vote starts with "call for votes" and end with " vote results"
	   				   	  // p[i].destruct();
	   				   }
   				   } catch (Exception e3) {
   					   //continue;
   					   //System.out.println(" hello"); 
   					   System.out.println(e3.toString());
   				   }
   			System.out.println("PEP completed counter " + j);
   		   }		   
   	   }
   	   //restart
//   	   String test[] = {};
//   	   boolean restart=true;
//   	   do {
//   		   new ExtractAllRelationsAllLibraries().main(test);
//   	   } while (restart);
   	   
        //END
   	   System.out.println("End of processing ");
   	   mc.disconnect();
   } 
   
   public static void showJTable(String wordList) {
  // public static void exportdata2(String wordList) {
		//String[] columnNames = {"previous sentence", "current sentence", "next sentence", "MessageID"};
		String[] columnNames = {"PepNumber","MessageID", "previous sentence", "current sentence", "next sentence"};
	
		JTable myJTable = new JTable();	   
		
	    JFrame frame1 = new JFrame("Database Search Result");
       frame1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       frame1.setLayout(new BorderLayout());
//TableModel tm = new TableModel();
       
       DefaultTableModel model = new DefaultTableModel();
       model.setColumnIdentifiers(columnNames);
       
       
       JButton button = new JButton("See Message Details of Row");
    	        
       myJTable.setModel(model);
       myJTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
       myJTable.setFillsViewportHeight(true);
       JScrollPane scroll = new JScrollPane(myJTable);
       scroll.setBounds(0, 0, 980, 200);
       scroll.setHorizontalScrollBarPolicy(
               JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
       scroll.setVerticalScrollBarPolicy(
               JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
       
//XXX       model.addRow(new Object[]{forOutput_pepNumber, v_messageID, forOutput_previousSentence, forOutput_mainSentence, currentSentence});
       
     //working - sets the cell wrapping
		myJTable.getColumnModel().getColumn(0).setCellRenderer(new TableCellLongTextRenderer ());  
		myJTable.getColumnModel().getColumn(1).setCellRenderer(new TableCellLongTextRenderer ()); 
		myJTable.getColumnModel().getColumn(2).setCellRenderer(new TableCellLongTextRenderer ());  
//	        table.getColumnModel().getColumn(3).setPreferredWidth(100);
	      //  table.getColumnModel().getColumn(4).setPreferredWidth(100);
		
		
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
       
       frame1.add(panel, BorderLayout.SOUTH);
       
       TabbedPane tp = new TabbedPane();
       JTabbedPane jtp = new JTabbedPane();
       
       JPanel jp1 = new JPanel();
       JPanel jp2 = new JPanel();
       JLabel label1 = new JLabel();
       label1.setText("You are in area of Tab1");
       JLabel label2 = new JLabel();
       label2.setText("You are in area of Tab2");
       jp1.add(label1);
       jp2.add(label2);
       jtp.addTab("Tab1", scroll);
       jtp.addTab("Tab2", jp2);
       
   //**    frame1.add(jtp);
  //**     frame1.add(scroll);
      
       
       button.addActionListener(new ActionListener() {

           @Override
           public void actionPerformed(ActionEvent e) {           	
             int i = myJTable.getSelectedRow();
             Integer p = Integer.parseInt( model.getValueAt(i, 1).toString() );
//             ShowMessage sm = new ShowMessage();
 //            sm.ShowMessageForID(2);
             System.out.println("p " + p + " i " + i);
           }
       });
       
       myJTable.setRowHeight(200);
       
       TableColumn column = null;
       for (int i = 0; i < 4; i++) {
           column = myJTable.getColumnModel().getColumn(i);
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
       
       frame1.setLocationRelativeTo(null);
       frame1.pack();
       frame1.setVisible(true);
       frame1.setSize(1500,800);
   }	
      																																										//, StanfordNLPTool snlpt 
   public static void getAllPEPMessages(MysqlConnect mc,Connection conn,Integer v_PEP, List<String> v_wordsList, ReVerbFindRelations rr, JavaOllieWrapperGUIInDev jw, ClausIEMain cie) throws IOException, SQLException 
   { 
       String Message,author,v_date,status,statusFrom,statusTo,subject,lineOutput = null,prevSentence = "";
       Integer message_ID = null,counter =0,dataFoundCounter = 0,processedCounter=0;
       Boolean v_required;
       String wordsFoundList = "",v_inReplyTo = "",outputDiscussionDate = "";
       Date m_date;             
       Boolean outputDiscussion = false;
        
       //we only analyse dev and ideas mailing lists
       //we dont analyse the entire message, we analyse the 'analysewords' rather than email column
       //maybe not analyse messages which are repeated under different peps, some SQL work required
       String sql = "SELECT date2, sendername, analysewords, statusFrom, statusTo, statusChanged, messageID, subject, required, date2, inReplyTo "
       		//+ "from allmessages WHERE pep = " + v_PEP + " and folder  = '"+dev+"'  or folder= '"+ideas+"' order by date2;"; 	//and (folder = 'C:\\datasets\\python-dev' OR folder= 'C:\\datasets\\python-ideas') 
    		   + "from allmessages WHERE pep = " + v_PEP + "  order by messageID;"; 	//and (folder = 'C:\\datasets\\python-dev' OR folder= 'C:\\datasets\\python-ideas')       // (folder like '%python-dev%' OR folder like '%python-ideas%') 
       //and (folder like '%python-dev%' OR folder like '%python-ideas%')
       Statement stmt = conn.createStatement();
	   ResultSet rs = stmt.executeQuery( sql );	 
	   System.out.println("PEP: " + v_PEP + " Number of messages: "+  +mc.returnRSCount(sql,conn));	   	
	   Instant start = Instant.now();
	   while (rs.next())	   //check every message 
	   {  
    	   dataFoundCounter=0;
    	 
    		//just give the computer some rest
/*		   try {
			    Thread.sleep(500);                 //1000 milliseconds is one second.
			    TimeUnit.SECONDS.sleep(30); 
			} catch(InterruptedException ex) {
			    Thread.currentThread().interrupt();
			}   
*/

    		processedCounter++;    		
       		dataFoundCounter++;
       		counter++;
       		Message = rs.getString(3);
       		v_date = rs.getString(1);
       		author = rs.getString(2);
       		statusFrom = rs.getString(4);
       		statusTo = rs.getString(5);
       		status = rs.getString(6);
       		message_ID = rs.getInt(7);
       		subject = rs.getString(8);
       		v_required = rs.getBoolean(9);
       		m_date = rs.getDate(10);
       		v_inReplyTo = rs.getString(11);
       		wordsFoundList = null;
		      
       		System.out.print("PEP "+v_PEP+", Processing new Message_ID: " + message_ID + "---------------------------");
       		
       		//allocate to PEP restart mark to file
       		//PEP_restart_mark = message_ID+1;
       		
       		//check if message has been processed before
       		boolean messageAlreadyProcessed=false;
       		Statement statement2 = conn.createStatement();
       		ResultSet resultSet2 = statement2.executeQuery("select count(messageid) from "+tableToStore+" where messageid = "+message_ID+";");
       	    while (resultSet2.next()) {
       	    	Integer count= resultSet2.getInt(1);
       	    	if(count>0) {
       	    		messageAlreadyProcessed=true;
       	    		System.out.print("\t\tMessage processed before "+message_ID);
       	    	}
       	    }
       	
       		//for instances when the processing gets stuck
       	    boolean proceed = false;
       	    if(v_PEP==PEP_restart_mark)	{	//	only do this for the pep with error message, not other peps
       	    	if (message_ID> MID_restart_mark) {
       	    		proceed= true;
       	    	}
       	    }
       	    else {
       	    	proceed= true;
       	    }
       	    //WE WANT TO CHECK THE MESSAGE SUBJECT..its not complete sentences so we want to extract svo
       	    /*
       	     * scnlp.dos.writeUTF("T"+subject);
			String output = scnlp.dis.readUTF();
			//					System.out.println("received: " +output);
			String[] svo = output.split(",");
			//					System.out.println("svo[0]"+svo[0]+ " svo[1] " + svo[1] + " svo[2] "+svo[2]);
			String rel ="",arg2="";
			if(svo.length <=1) {

			}
       	    */
	   	    //For instances where a message has many sections and they need to be checked separately
									   //&& (StatusInfoFound ==false)
       	    //
       		if(proceed && Message.length() < 1000000 && !messageAlreadyProcessed) {  //&& (v_required == true)) {	   //this is where the error is 
       			//CODE CUT ..refer to other versions  
       		    	//System.out.println("\tExtracting SVOs : " + message_ID);
       		    	Message = pm.removeQuotedText(Message);  //remove quoted text from email message
       		    	Message = pm.handleBlankLines(Message); //handle blank lines - should start new sentence
		            try {
							// check entire message and check sentence.
							// STRING MATCHING
							// TEMP wordsFoundList = cm.checkEntireMessage (p, Message, wordsFoundList, author, statusFrom, statusTo ,v_PEP, m_date, subject, message_ID, pepNumber, counter ); //1. Check entire message.
							wordsFoundList = checkAllSentencesInMessage(Message, wordsFoundList, author,subject, statusFrom,
									statusTo, v_PEP, m_date, v_PEP, counter, prevSentence,  message_ID, rr, jw, cie,conn); //,snlpt );
							// 2. Check sentences after Splitting message text into sentences
							// Note: for the moment we are just testing the above schemes over sentences only.
							// Try and output previous, current and next sentence.
		            }
		            catch (Exception e){ 
		            	continue;
		            }	      		 
       		   //CUT CODE PLEASE SEE PREVIOUS VERSION SOF THIS FILE
       		} //end if
       		//added july 2016
       		else {
       			System.out.println("Message.length() < 1000000 && (StatusInfoFound ==false))");
       		}
       		System.gc();    	   
       	    System.out.println("\tMessage completed counter " + processedCounter + " Cleanup completed...");
	      } // end while
	    Instant end = Instant.now();
		Duration timeElapsed = Duration.between(start, end);
		System.out.println("PEP "+v_PEP+", Processing Finished, Time taken: " + (timeElapsed.toMillis() / 1000) / 60 + " minutes");

   }
   
   private static int indexOf(String string) {
	// TODO Auto-generated method stub
	return 0;
   }
   
   public String checkNull(String v_wordsFoundList, String to_add){
	   if (v_wordsFoundList == "")
		   v_wordsFoundList += to_add;
	   else 
		   v_wordsFoundList = v_wordsFoundList + "& " + to_add;
	return v_wordsFoundList;	   
   }
   
   public void compare(){
	   ReadFileLinesIntoArray rf = new ReadFileLinesIntoArray();
//	   String subject[] = rf.readFromFile("");
//	   String object[] = rf.readFromFile("");
//	   String relation[] = rf.readFromFile("");
   }
   
   
   //sometime this can be moved to checksentence.java file
   public static String checkAllSentencesInMessage(String v_message, String wordsFoundList, String author,String subject, String statusFrom, 
		   String statusTo , Integer v_pep, Date v_date, Integer v_pepNumber, Integer v_counter, String prevSentence, 
		   Integer v_message_ID,  ReVerbFindRelations rvr, JavaOllieWrapperGUIInDev jw, ClausIEMain cie,Connection conn){ //,StanfordNLPTool snlpt ){
	 //String str = "This is how I tried to split a paragraph into a sentence. But, there is a problem. My paragraph includes dates like Jan.13, 2014 , words like U.S and numbers like 2.2. They all got splitted by the above code.";
       Matcher reMatcher = null;
       //String filename = "c:\\scripts\\test.txt";  
       ArrayList<String> allSentenceList = new ArrayList<String>(); 
       Integer sentenceCounter=0;
       
       if (!(v_message.isEmpty()) && v_message.length() > 0 ) {    	   
           try {
	           	    //we want to check the subkect also ...so simply we ad the subject at the beginning of the message	       
	     	        if ( subject==null || subject == "" || subject.isEmpty() )
	     	        {}
	     	        else {
	     	        	//LETS REMOVE EXCLAMATION first
	     	        	//some lines in message start with a charater which causes SNLP to recognise its as sentence delimeter like !
	     				//we basically remove these chars if they are in front of lines in paragrapgh 
	     	       		//should be handled while reading in from txt files
	     	       		String charsToRemoveFromLine = "!";
	     	    		while (v_message.contains("\n!")) {		//remove all exclamation just after newline
	     	    			v_message = v_message.replace("\n!","\n");
	     	    		}
	     	    	   v_message = subject.toLowerCase() + ". \n\n" + v_message;   //this will check it for triples, and later doubles and singles
	     	        }
	           		//for a new message, set the previous sentence to null
	           		String PreviousSentenceString = "",nextSentenceString="";           		
	           		
	 //**          		System.out.println("MESSAGE:  " + v_message);
           		
	           	   // we need to split into paragraphs as to prevent long sentences which dont have delimeter
	           	   String[] paragraphs = v_message.split("\\r\\n", -1);
	        	   for (String g: paragraphs)
	        	   {    	/* dec 2018 ..alredy moved to main class       		
						Reader reader = new StringReader(g);           			
						DocumentPreprocessor dp = new DocumentPreprocessor(reader);
						
						 //sometimes a paragrapgh has sentences but are not captured as sentences. So if sentence identified then check, so we just add a full-stop at end of paragraph	        				   
						   if(!g.endsWith("."))
							   g = g + ".";
						
//						System.out.println("Paragraph:  " + v_message);
						for (List<HasWord> eachSentence : dp) {
							sentenceCounter++;
//***							System.out.println("eachSentence.." + Sentence.listToString(eachSentence));
							String CurrentSentenceString = Sentence.listToString(eachSentence);
							
							CurrentSentenceString = pm.removeLRBAndRRB(CurrentSentenceString);								
							CurrentSentenceString = pm.removeDivider(CurrentSentenceString);
							CurrentSentenceString = pm.removeUnwantedText(CurrentSentenceString);		//remove unnecessary words like "Python Update ..."
							
							
							//get the next previous sentence and paragraphs 
							//have to go over teh entire mesage again, but since it only for captured triples, it will be less instances of these									
							nextSentenceString=getNextSentenceInCurrentParagraph(g,CurrentSentenceString);
								

							//the follwoing does not apply as thats the whole point of this extraction - to discover the S,V,O triples
							//capture only sentences which contain the terms pep, bdfl, consensus, etc - defined in array
							
//										for (String c :catchTerms) {
//											CurrentSentenceString = CurrentSentenceString.replace(",", " ");
//											
//											if(CurrentSentenceString.toLowerCase().contains(c)){
//												//Dont consider if sentence starts with If  - More on this later
//												if(CurrentSentenceString.startsWith("If")){
//													//add Maybe
//													question=true;
//													break;
//												}
//												else	//REVERB
//													rvr.extractRelations(CurrentSentenceString, v_pepNumber, v_message_ID,author, v_date);
//											}
//										}
							
							//capture all relations
							
							//Long sentences give error in clausIE
							//so we only look at sentences which have less than 50 terms, note average english sentence has 14 terms
							//come up with a suitable length. maybe 
							if (CurrentSentenceString.split(" ").length < 35){	
								//JUST TEMP COMMENT THIS 
								//AS REVERB IS DONE  TILL PEP 240
								if(useReverb){																		//relations_reverb
									rvr.extractRelations(CurrentSentenceString, v_pepNumber, v_message_ID,author, v_date, "relations");
									//reverb currently stopped at 240
									//and also middle of 7
									//maybe for 240 onwards, code it to insert in all three types of relation tables
								}
								if (useClausIE){
									//clausIE
									//cie completed pep 4..middle of pep 5 - jave hout of memory
	//								System.out.println("process here.." );
									//Check if sentence is Englsh or code
									//can conmtinue to process onto next sentence if not
									boolean isEnglish = ec.isEnglish(CurrentSentenceString);
									
									cie.extractClauseIERelations(conn,CurrentSentenceString, PreviousSentenceString.trim(),nextSentenceString.trim(),"","","",v_pepNumber, v_message_ID,author, v_date, 
											tableToStore,outputDetails,0,0,0,isEnglish, false,false,"","");//temporarily set isfistpara and islastpara to false and latter 2 also temporary
									cie.dispose();
								}
								if(useOllie){
									//Ollie
									jw.extractOllieRelationsw(CurrentSentenceString, v_pepNumber, v_message_ID,author, v_date, "relations_ollie",conn);
								}
							}
							PreviousSentenceString = CurrentSentenceString;
						}//end of for loop for sentence
	        	   */   		  
	        	   } //end for lop for paragraphs
           }
        catch (Exception e){ 
//	  	            	System.out.println(message_ID + " ____________________________________________________________heer  " + e.toString() + "\n");
        	//continue;
        }             	   
       }       
       return wordsFoundList;
   } 
   
   public static String getNextSentenceInCurrentParagraph(String entireParagraph,String v_currentSentenceString) {
	    String v_nextSentence = "";	Integer sentenceCounter; 		 
	    PythonSpecificMessageProcessing pm = new PythonSpecificMessageProcessing();
		Boolean currSentenceFound = false;
		Integer counter=0;
		/* dec 2018 ..alredy moved to main class
		Reader reader = new StringReader(entireParagraph);
		DocumentPreprocessor dp = new DocumentPreprocessor(reader);

		for (List<HasWord> eachSentence : dp) 
		{			
			String CurrentSentenceString = Sentence.listToString(eachSentence);
			CurrentSentenceString = pm.removeUnwantedText(CurrentSentenceString);				
			CurrentSentenceString = pm.removeLRBAndRRB(CurrentSentenceString);
			CurrentSentenceString = pm.removeDivider(CurrentSentenceString);
			
			//if found in previous round, get next sentence and return
			if (currSentenceFound==true){
				v_nextSentence = CurrentSentenceString;
				return v_nextSentence;				
			}
			// as soon as sentence matched
			if (CurrentSentenceString.equals(v_currentSentenceString)){
				currSentenceFound = true;						
			}
		}
		*/
		
		return v_nextSentence;
   }
   
   public static int  number_of_cccurrences_of_char_in_string_java(String stringToSearch) {	    
	    String letter = "\n>";
	    int i = 0, count = 0;
	    while ((i = stringToSearch.indexOf(letter, i++)) != -1) {
	        count++;
	        i += letter.length();
	    }
	    return count;
	}   
}

