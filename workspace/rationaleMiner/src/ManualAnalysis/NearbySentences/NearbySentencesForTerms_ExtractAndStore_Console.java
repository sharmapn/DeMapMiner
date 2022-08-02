package ManualAnalysis.NearbySentences;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import com.google.common.collect.ObjectArrays;

import readRepository.readRepository.ReadLabels;
import connections.MysqlConnect;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.process.DocumentPreprocessor;
import miner.process.LabelTriples;
import miner.process.PythonSpecificMessageProcessing;
import miner.processLabels.TripleProcessingResult;
import utilities.ParagraphSentence;
import utilities.PepUtils;

//march 2020. we used this script to amnd modified it to list the number of sentences we initially got from 30 peps which had the 
// initial list of concepts

//This script is now main script fro current and nearby sentence and paragraph extraction for specified terms,
//But rather than quering the entire repository one by one for each string set, we query the allsentences table and all paragraphs table
//This has to be integrated in the main script as an option

//This script is the first step in exploring any Repository for the first time.
//It takes the Proposals identifier, and STATES, and returns sentences which have a PI and a state
//April 2019. This script is modified for querying JEPs

//The script checks if all provided terms are in the same sentence 
//sometimes we can shorten the results by adding additional term "pep" and "proposal" additionally to each term 
public class NearbySentencesForTerms_ExtractAndStore_Console  {
	static PythonSpecificMessageProcessing pm = new PythonSpecificMessageProcessing();
	static ParagraphSentence ps = new ParagraphSentence();
    //reading keywords
    static boolean hardCodedTerms = true, readTermsFromFile = false, labelsFromDB=false;
    //storing results
    static boolean resultsToConsole=true, resultsToFile = false, resultsToDatabse = true;
    //search which PEPs
    static boolean allPeps = false, selectedPEPs = true;
    static Integer[] selectedListItems = {289,308,313,318,336,340,345,376,388,389,391,414,428,435,441,450,465,484,488,498,485,471,505,3003,3103,3131,3144};
    static String proposals = "";
    //search how
    static boolean adjacentTermsInSentence = false, termsAnywhereInSentence = true, termsAnywhereInParagraph = false; //previous, current and next paragraph
    //add these terms to each term in the array    
    static boolean addPEP = true, addProposal = true;
    static String tableToStore = "initialsentences";// "keywordmatchingremainingpeps" ; //keywordMatching29PEPs";	//table to store the sentences which contain the keywords 
    static Boolean matchExactTerms = false;
    static String proposalIdentifier = "pep"; //jep"; //pep
    
    static String tablename = "allsentences", field = "sentence"; //allsentences
    
    static PepUtils pu = new PepUtils();
        
  //we want to find the reasons for the following states for the 29 PEPs
    static String[] mainStates =  {"accept","approve","reject"} ; //,"wrote","written","propose","draft","update","revise","withdraw","defer","postpone","replace","incomplete","supercede","final","close","pending"};
  	//additional states we have found
    static String[] decisionMechanismsSubStates =  {"poll","vote","voting","consensus","no consensus","majority","support","feedback","lazy","bdfl"};	//additional states we have found
    //discussion
    static String[] discussion = {"discussion pep","discussion idea","discussion syntax"};	//"discussion",	//takes too long - shud be done individually
    //idea stage			
    static String[] ideaStage = {"idea no support","idea support","idea","support idea","pep number","request pep number","pep editor","no consensus", "czar"};
    //all below is WHO decided
    static String[] entities = {"pep dictator","pep champion","bdfl delegate committee","committers vote core developers","pep czar","BPD","BPC","FLUFL","committee decision","committee decisions","dictatorship","dictator","delegate"}; 
		//all below is on HOW pep was decided
    static String[] reasons = {"bdfl majority","guido community consensus","bdfl community consensus","decision overridden","best judgement","community consensus","rant","dictatorship","dictator","decision",
    		"no consensus reached","community consensus","approve","consensus","rough consensus","support","majority","no majority","no support","poll","vote","decision","pronouncement", 
    	    "feedback","stalled","delegate", "overidden"}; 
    
    //now about reasons
    //standard states changes by users i want to capture 
	//"draft" ,	dont need draft as its always by author
    static String reasonIdentifierTerms[] = {"because","since", "based on","due to","thanks to","accordingly","as a result of","through"};
	// implementing this will help as not all reasons are triples
    static String ActualReasons[] = {"discussion","debate","bdfl pronouncement","poll result","voting result","vote result","consensus","no consensus","feedback","favorable feedback",
			"support","no popular support","favourable feedback","poor syntax","limited utility","difficulty of implementation","bug magnet","controversy","majority","wrangling"};  
			//lack of an overwhelming majority 
	
       
	//find the order of events
	//"the PEP"	"was accepted"	"by Guido + After a short discussion
    static String storyline[] = {"following", "after","later", "afterwards"};	
    
     //Note: lazy majority and lazy consensus returns zero rows
    static String fileName    = "c:\\scripts\\pepLabels\\pep.txt", outFielName = "c:\\scripts\\outer.txt";
    static ReadLabels b = new ReadLabels();
            
    static FileWriter writer = null,writer2 =null,writer3 =null;
    static Date date = new Date();
    static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
    
    private static String removeLastChar(String str) {
        return str.substring(0, str.length() - 1);
    } 
    
    public static void main(String[] args)  {
    	
    	//march 2020..for different purpose ..to create the initial list    
        String[] initialList = ObjectArrays.concat(decisionMechanismsSubStates, ideaStage, String.class);
        String[] tempList = ObjectArrays.concat(reasons, ActualReasons, String.class);
        initialList = ObjectArrays.concat(initialList, tempList, String.class);
    	
    	
        String[] sentences = new String[1000]; //core developer
                
        //which combination we want to process
        String[] commonStatesList = ObjectArrays.concat(mainStates, decisionMechanismsSubStates, String.class);	//mostly important
        String[] reasonsList = ObjectArrays.concat(entities, reasons, String.class);
        //String[] finalTerms = ObjectArrays.concat(commonStatesList, reasonsList, String.class);
        
        //Main adder
        String[] finalTerms = mainStates;
        
        //new for reasons and reason identifier terms, mainstates and reasonIdentifier terms and 
        String[] StatesAndSubStates = ObjectArrays.concat(mainStates, decisionMechanismsSubStates, String.class);
        String[] finalLine = new String[10000];
        Integer counter=0;
        String[] third = {"pep"};
        
        //original
//        for (String word1 : mainStates) {  //StatesAndSubStates) {
//            for (String word2 : reasonIdentifierTerms) {
//                if ( !word1.equals(word2)) {
//                    for (String word3 : third) {	//ActualReasons
//                        if ( !word3.equals(word2) && !word3.equals(word1)) {
//                            System.out.println(word1 + " "+ word2 + " "+ word3);
//                            finalLine[counter] = (word1 +" "+ word2 +" "+ word3);
//                            counter++;
//                        }
//                    }
//                }
//            }
//        }
        //march 2020...only for initial list ..should change to above 
        for (String word1 : initialList) {  //StatesAndSubStates) {
            //for (String word2 : reasonIdentifierTerms) {
                //if ( !word1.equals(word2)) {
                    for (String word3 : third) {	//ActualReasons
                        if ( !word3.equals(word1) ) {
                            System.out.println(word1 + " "+ word3);
                            finalLine[counter] = (word1 +" "+ word3);
                            counter++;
                        }
                    }
               // }
            //}
        }
        
        
        
        System.out.println("proposals 0: " + proposals);
        //march 2020..create pep list
        if(selectedPEPs) {
        	for(Integer k : selectedListItems) {
        		proposals = proposals + k.toString() + "," ;
        	}
        	//System.out.println("proposals a: " + proposals);
        	proposals = proposals.trim(); 	//System.out.println("proposals b: " + proposals);
        	proposals = removeLastChar(proposals); 	//System.out.println("proposals c: " + proposals);
        	proposals = "proposal IN (" + proposals + ")"; 	
        	System.out.println("proposals d: " + proposals);
        }
        else {
        	proposals = "";
        }
        
        //now we query these combinations in the 'allsentences' table and 'allparagraphs' table    
    	try{  
    		MysqlConnect mc = new MysqlConnect();     	    Connection conn = mc.connect();
        	Statement stmt = conn.createStatement();            	
        	
            if(readTermsFromFile)	//READ keywords
            	finalTerms = readKeywordsFromFile(fileName);
            if(labelsFromDB)
            	finalTerms = b.getallLabelsFromTableAsString(conn);	//or we can read directly from databse
            String delimeter="";
    		//for adjacent terms in sentence, we dont split string into multiple terms, we just split by a delimeter which doesnt exists 
    		if (adjacentTermsInSentence)
    			delimeter = "!";
    		if (termsAnywhereInSentence)
    			delimeter = " ";	//when we want to serach for   2 or 3 terms occuring seprately in same sentence
    		String[] paragraphs  = null; 
            
            //PROCESS
            //we want to add terms "pep" and "proposal" to each term
    		;
    		int counterk=0;
 	    	for(String lineOfKeyWords : finalLine) { //finalTerms) { //finalTerms) { //LinesOfkeyWords){
 	    		System.out.println("finalLine length: " + finalLine.length + " counterk: " + counterk);
 	    		searchEachTermCombinationInSentencesAndParagraphsTable(date, dateFormat, conn, stmt, lineOfKeyWords,matchExactTerms);	//lineOfKeyWords
 	    		if(addPEP) {
 	    			//we commenting bwlow, as we add pep in code above
 	    			//lineOfKeyWords = lineOfKeyWords + " pep";
 	    			//processEachLabel(date, dateFormat, conn, stmt, lineOfKeyWords,matchExactTerms);	//lineOfKeyWords
 	    		}
 	    		counterk++;
 	    	} //end while
// 	    	for(String lineOfKeyWords : LinesOfkeyWords){
// 	    		if (addProposal)
// 	    			lineOfKeyWords = lineOfKeyWords + " proposal";
// 	    		processEachLabel(date, dateFormat, conn, stmt, lineOfKeyWords);
// 	    	}	    		   	    	
 	    	mc.disconnect();           
 	    } catch (Exception ex) {
 	    	System.out.println("Error " + ex.getMessage() +ex.toString()); 	    	System.out.println(ps.StackTraceToString(ex)  );	
 	    }
    }

    private static void searchEachTermCombinationInSentencesAndParagraphsTable(Date date, SimpleDateFormat dateFormat, Connection conn, Statement stmt,String lineOfKeyWords,Boolean matchExactTerms) throws IOException, SQLException {
    	FileWriter writer,writer2,writer3;
    	String[] paragraphs;
    	String pepNumber,previousSentence = "",currentSentence = "", nextSentence = "",v_sentence = "",v_messageID = "",cou = "", v_author="",v_subject="",
    			v_idea="", subject="", verb="",object = "",output = "",permanentCurrentSentence = "",permanentPreviousSentence = "",permanentNextSentence = "";
    	Boolean foundinLastRound = false;
    	Integer forOutput_pepNumber = null,sentenceCounter = 0, dataInsertedCounter=0;
    	Timestamp dtTimeStamp=null; 

    	try {
    		TimeUnit.SECONDS.sleep(1);
    		System.gc();     Runtime.getRuntime().gc();         //OR call 
    	} catch (InterruptedException e2) {
    		// TODO Auto-generated catch block
    		e2.printStackTrace();
    	}

    	//int sentenceCounter = 0;
    	//extract keywords 	 
    	
    	if(lineOfKeyWords==null || lineOfKeyWords.isEmpty() || lineOfKeyWords.length()==0) {
    		lineOfKeyWords = " ";
    	}
    	
    	String words[] = lineOfKeyWords.split(" ");
    	//for terms anywhere in sentence we split by space
    	//change this to comma when searching for continuous terms
    	//String words[] = terms; 
    	String str = " "+field+" like '%consensus%'";

    	if(words.length==1 ){
    		v_idea = subject= words[0]; 
    		str = " LOWER("+field+") LIKE '%" + subject + "%'";// and LOWER(email) LIKE '%" + verb + "%'  ";
    	}
    	else if(words.length==2 ){
    		//v_idea =  words[0];	
    		v_idea= words[0] + " " + words[1];     		subject = words[0];
    		verb = words[1]; 
    		str = " LOWER("+field+") LIKE '%" + subject + "%' and LOWER("+field+") LIKE '%" + verb + "%'  ";
    	}
    	else if(words.length==3){
    		v_idea =  words[0]+ " "+ words[1] + " "+ words[2];     		subject = words[0]; verb = words[1]; object = words[2];	 
    		str = " LOWER("+field+") LIKE '%" + subject + "%' and LOWER("+field+ ") LIKE '%" + verb + "%'  and LOWER("+field+") LIKE '%" + object + "%'  ";
    	}
    	//capture object as well - form triples - inputfiles
    	else if(words.length>3){
    		v_idea =  words[0]; subject = words[1]; verb = words[2]; object = words[3];	 
    		str = " LOWER("+field+") LIKE '%" + subject + "%' and LOWER("+field+") LIKE '%" + verb + "%'  and LOWER("+field+") LIKE '%" + object + "%'  ";
    	}

    	writer  = new FileWriter("C:\\DeMap_Miner\\datafiles\\outputFiles\\NearbySentencesParagraph\\Previous-Selected-Next-SentencesParagraph_"+ v_idea+"-"+ subject+"-"+verb+"-"+object  + dateFormat.format(date) + ".txt");  //All the previous, selected and next sentences are output in tHis file.
    	writer2 = new FileWriter("C:\\DeMap_Miner\\datafiles\\outputFiles\\NearbySentencesParagraph\\SelectedSentencesOnly_"+ v_idea+"-"+ subject+"-"+verb+"-"+object  + dateFormat.format(date) +  ".txt");				
    	writer3 = new FileWriter("C:\\DeMap_Miner\\datafiles\\outputFiles\\NearbySentencesParagraph\\WithAllParagraphs_"+ v_idea+"-"+ subject+"-"+verb+"-"+object  + dateFormat.format(date) +  ".txt");

    	//pst = con.prepareStatement("select email from allpeps where pep = 488 AND email LIKE ='%" + keyWord + "%'");
    	// distinct

    	ArrayList<Integer> UniquePeps = new ArrayList<Integer>();;
    	if (allPeps) 
    		UniquePeps = pu.returnUniqueProposalsInDatabase(proposalIdentifier);
    	else if (selectedPEPs) {
    		for (int index = 0; index < selectedListItems.length; index++)	{
    			UniquePeps.add(selectedListItems[index]); 
    			//System.out.println("selectedListItems " + selectedListItems[index]);     			
    		}
    	}
    	System.out.println("Selected All---------------------------Unique Peps count = "+ UniquePeps.size());
    	System.out.println("str: " + str);
    	System.out.println("proposals: " + proposals);
    	//no need for this as we querying sentences and paragrapghs table directly
    	try {
    		
    		String qry = "select messageID, proposal,  sentence ,authorrole, msgsubject from "+tablename+" where " + proposals + " AND " + str +";";  //datetimestamp
    		ResultSet rs = stmt.executeQuery(qry);	// limit 100 
    		//LIKE '%" + keyWord.trim().toLowerCase() + "%' LIMIT 100");
    	
    		
	    	while (rs.next()) 		{  
	    		v_messageID = rs.getString(1);	pepNumber  = rs.getString(2);	v_sentence= rs.getString(3); 
	    		v_author = rs.getString(4); 	v_subject = rs.getString(5); 	dtTimeStamp = null; //dtTimeStamp =rs.getTimestamp(6);
	
	    		//model.addRow(new Object[]{ v_messageID});
	    		if ((v_sentence == "null") || (v_sentence == "") || (v_sentence.isEmpty())) 
	    		{}
	    		else {
	    			Integer paragraphCounter = 0;    				Boolean foundInLastParagraph =false;    				String previousParagraph = "";
	    			//if we want to check in nearby paragraphs , previous, curr, and next
	    			if(termsAnywhereInParagraph){
	    				if (ps.containsSVO(subject, verb,object, v_sentence, matchExactTerms)) {
	    					insertNearbySentencesInDB( Integer.valueOf(pepNumber), Integer.valueOf(v_messageID), v_idea,  subject,  verb,  object,
	    							v_sentence, //need current sentence: current sentence in earlier round
	    							"", 		  //need previous sentence: next sentence in earlier round but current sentence
	    							"", 		  //need next sentence: currentSentence in this round  
	    							v_author, v_subject,dtTimeStamp,
	    							conn);	
	    				}
	    			}					
	    			//once we have the next sentnce, we can write to file 
	    			try	{    								
	    				if(resultsToFile){										//next sentence			//paragraph
	    					writer.append (output + " |ns " + currentSentence  + " |cp " + v_sentence); // + " |pp " + previousParagraph.replace("\n", ""));
	    					writer3.append(output + " |ns " + currentSentence  + " |cp " + v_sentence + " |pp " + previousParagraph);
	    				}
	    				if(resultsToDatabse){//insert into database
	    					insertNearbySentencesInDB( Integer.valueOf(pepNumber), Integer.valueOf(v_messageID),v_idea,  subject,  verb,  object,
	    							permanentCurrentSentence, //need current sentence: current sentence in earlier round
	    							permanentPreviousSentence, 		  //need previous sentence: next sentence in earlier round but current sentence
	    							v_sentence, 		  //need next sentence: currentSentence in this round  
	    							v_author, v_subject,dtTimeStamp,
	    							conn);												
	    				}
	    				if (resultsToConsole)
	    					System.out.println(pepNumber  + " | " + v_idea + " | " + v_messageID + " | "+ subject  + "|" + previousSentence + " |cs " + v_sentence);
	    				dataInsertedCounter++;
	    			} catch (IOException e) {
	    				// TODO Auto-generated catch block
	    				e.printStackTrace();
	    			}		
	
	    			//CHECKING IF THE Sentence contains the search terms
	    			if (ps.containsSVO(subject, verb,object, currentSentence, matchExactTerms)) {
	    				sentenceCounter++;
	    				output = "\n" + v_idea + " | " + pepNumber + " | " + v_messageID + "|pp "   + " |ps " + previousSentence + " |cs " + v_sentence;
	    				//String forOutput_nextSentence = "";
	    				forOutput_pepNumber = Integer.parseInt(pepNumber);												
	    				//set that ste has been found
	    				foundInLastParagraph=true;												
	    				writer2.append("\n" + v_sentence);
	    				foundinLastRound = true;
	    			}		
	    			// end for
	    		} //end else
	    		// System.out.println("paragraphs in message" + paragraphCounter);
	    		paragraphs  = null; 
	    	} // end while

    	}
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (Exception e){
  			System.out.println("Exception 282 (DATABASE) " + e.toString());   			System.out.println(StackTraceToString(e)  );
  		}
    	
    	//call garbage collector      
    	System.gc();  Runtime.getRuntime().gc();            //OR call 
    	System.out.println(sentenceCounter + " Records Found, Data Inserted " +dataInsertedCounter );  
    	writer.close();    	writer2.close();    	writer3.close();
    	words=null;
    }
    
    public static String StackTraceToString(Exception ex) {
		String result = ex.toString() + "\n";
		StackTraceElement[] trace = ex.getStackTrace();
		for (int i=0;i<trace.length;i++) {
			result += trace[i].toString() + "\n";
		}
		return result;
	}

    public static void insertNearbySentencesInDB(Integer pep, Integer messageID, String idea, String subject, String verb, String object,
    		String currentSentence, String previousSentence, String nextSentence,String v_author,String message_subject, Timestamp dTimeStamp, Connection conn)
	{	 
//		//Now output to file and Console
		//boolean repeatedSentenecAndLabel = false,repeatedLabel = false, repeatedSentence = false, emptyRow = false;	
		//Main insertion
		String sql = "INSERT INTO "+tableToStore+" (pep, messageID,  label,subject, relation, object, currentSentence,ps,ns , author, message_subject,datetimestamp) "
				+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
		 
		try {			
			PreparedStatement statement = conn.prepareStatement(sql);			
			statement.setInt	 (1,pep);			statement.setInt	 (2, messageID);			//statement.setString	 (3, v_message);	
			statement.setString	 (3, idea);			statement.setString	 (4, subject);			statement.setString	 (5, verb);
			statement.setString	 (6, object);		statement.setString  (7, currentSentence);	statement.setString  (8, previousSentence);
			statement.setString  (9, nextSentence);			statement.setString  (10, v_author);
			statement.setString  (11, message_subject);
			statement.setTimestamp  (12, dTimeStamp);
			
			int rowsInserted = statement.executeUpdate();
			if (rowsInserted > 0) {
			    //System.out.println("A new result record was inserted in DB successfully!");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (Exception ex) {
			// TODO Auto-generated catch block
			System.out.println("Exception" + ex.toString());
			ex.printStackTrace();
		}
	}

	

	private static String[] readKeywordsFromFile(String fileName) throws FileNotFoundException, IOException {
		String[] keyWords;
		BufferedReader in = new BufferedReader(new FileReader(fileName));
		String str;
		List<String> list = new ArrayList<String>();
		while((str = in.readLine()) != null){
			
			if (str.startsWith("%"))
			{}
			else if(str.startsWith("##") ){	//|| str.trim().isEmpty()
				System.out.println("breaking out of loop");
				break;	//break out of loop
			}
		    else{
		    	list.add(str);
		    	System.out.println("added to list: "+str);
		    }
		}
		keyWords = list.toArray(new String[0]);
		System.out.println("returning array");
		return keyWords;
	}
 
	public void exportData(Connection conn, String filename) {
		Statement stmt;
		String query, query2,filename2 = "C:/Users/psharma/Google Drive/PhDOtago/scripts/outqrytest.txt";
		try {
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
					ResultSet.CONCUR_UPDATABLE);

			// For comma separated file
			query = "SELECT id,text,price into OUTFILE  '" + filename
					+ "' FIELDS TERMINATED BY ',' FROM testtable t";
			// firstSearchword.getText()
			String key = "wrangling";
			query2 = "select messageID, pep into OUTFILE  '"
					+ filename2
					+ "' FIELDS TERMINATED BY ',' from allmessages where  email LIKE '%"
					+ key + "%'";

			stmt.executeQuery(query2);

		} catch (Exception e) {
			e.printStackTrace();
			stmt = null;
		}
	}
    
    
}
