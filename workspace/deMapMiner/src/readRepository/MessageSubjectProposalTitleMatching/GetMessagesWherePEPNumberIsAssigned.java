package readRepository.MessageSubjectProposalTitleMatching;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mysql.jdbc.PreparedStatement;

import callIELibraries.JavaOllieWrapperGUIInDev;
import readRepository.readMetadataFromWeb.GetProposalDetailsWebPage;
import readRepository.readRepository.ReadLabels;
import callIELibraries.ClausIECaller;
import callIELibraries.ReVerbFindRelations;
import callIELibraries.UseDISCOSentenceSim;
import connections.MysqlConnect;
import de.mpii.clause.driver.ClausIEMain;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.process.DocumentPreprocessor;
import GUI.helpers2.inits;
import miner.models.Pep;
import miner.process.CheckSentenceHasTripleTerms_SendToIELibraries_InsertIntoDB;
import miner.process.GetAllMessagesInPep;
import miner.process.GetAllSentencesInMessage;
import miner.process.LabelTriples;
import miner.process.ProcessDummyFile;
import miner.process.ProcessMessageAndSentence;
import miner.process.ProcessSinglesAndDoubles;
import miner.process.ProcessingRequiredParameters;
import miner.process.PythonSpecificMessageProcessing;
import miner.process.SinglesDoublesTriples;
import miner.processLabels.ProcessLabels;
import miner.processLabels.TripleProcessingResult;
import miner.stringMatching.checkMessage;
import miner.stringMatching.checkSentence;
import readRepository.readMetadataFromWeb.GetProposalDetailsWebPage;

//This script aims to retrieve those messages where the pep editor assigns a pep number ..checks for terms 'pep number' in message subject
//Not much use for pep title matching
//remove messages with same emailmessageid
public class GetMessagesWherePEPNumberIsAssigned {
	
	static ProcessMessageAndSentence pms = new ProcessMessageAndSentence();
	static PythonSpecificMessageProcessing psmp = new PythonSpecificMessageProcessing();
	static CheckSentenceHasTripleTerms_SendToIELibraries_InsertIntoDB cesdl = new CheckSentenceHasTripleTerms_SendToIELibraries_InsertIntoDB();
	
	static List<String> wordsList = new ArrayList<String>(); 
	static List<String> statusList = new ArrayList<String>();

	static Integer max = 10000, min = 0;
	static Pep[] p = new Pep[max];

	static checkMessage cm = new checkMessage();
	static checkSentence cs = new checkSentence();
	static GetProposalDetailsWebPage pd = new GetProposalDetailsWebPage();
		
	static String scriptsHomeFolderLocation = "C:/Users/psharma/Google Drive/PhDOtago/scripts/";
	static String DummyFileLoc = "C://Users//psharma//Google Drive//PhDOtago//scripts//inputFiles//dummyFile.txt";														
	static String CurrentSentence, SentenceClassification, DISCOSimilarity; // Classification using weka text	and // DISCO similarity value between two sentences
	
	public static final String NLP_MODELS_PATH = "C:\\Users\\psharma\\Google Drive\\PhDOtago\\Code\\inputFiles\\models\\"; 
	//"models\\";
	
	// static String DISCOSim; // DISCO similarity + value between two sentences
	static String max_idea;    // store the idea returned with the most similar disco match
	
	static String first, sentencesToMatch[];																	
										// 389 and 450 added later											//479, has one message which gives error
	static Integer[] selectedListItems = {289,308,313, 318, 336, 340, 345,376,388,389, 391, 414,428, 435, 441,450, 465,  484, 488, 498, 485, 471, 505, 3003, 3103, 3131,3144}; //, };
   
	//debud messages
	static boolean showInputFileTriplesAfterLoading= false;
	
	// which peps to processeidin
	static boolean selectedAll = false, selectedMinMax = false, selectedSelected = false, selectedList = true, messageID =false , readDummyFile = false,  selectedType = false; // type; standard, process or informational

	static boolean allStandard = false; 		// about python language, functional enhancements - more action here, eg Pep 308 is standard pep
	static boolean allProcess = false; 		    // related to python processes
	static boolean allInformational = false;

	// option to include checking triples in these libraries
	static boolean checkReverb = false, checkClauseIETrue = true, checkOllieTrue = false;
	
	//what to include in output
	static Boolean repeatedLables = false, repeatedSentences = false, includeEmptyRows = false;
	static Integer pepNumber = 3103, MessageID = 120503; //39032; //32851; // 31049,31460;110261
	
	//for processing dummy files
	static Boolean readEntireFileAsMessage = true;

	static ArrayList<String> candidateSentencesList = new ArrayList<String>();

	// dont process sentences if it contains
	static String dontProcessList[] = { " if " };
	
	//Once both triples extracted from IE libraries and those from inputFile are matched, dont process any further if these labels are captured
	//because they wont have anything extra we need and just would take more processing time	
	static String dontCheckLabels[] = {}; //"discussion","poll", "vote","proposal"

	//check for singles and doubles in sentences																																									//pep  245, 275, 332, 335, 340,390, 3139, 3136, 354,410, 416,  437, 
	//static String singlesAndDoubles[] = {"accepted!","draft pep", "rough draft", "community consensus","initial draft","first draft","second draft","redraft","redrafted","third draft","fourth draft",
	//		"fifth draft","final draft","results of the vote","vocal minority","pep: xxx","new pep submission", "pep submission","four versions","request for pronouncement"}; //369
	static ArrayList<SinglesDoublesTriples> singlesDoublesTriplesList = new ArrayList<SinglesDoublesTriples>();
	
	// conditionals, negations, questionPhrases, and generalisedList
	// check inside each term in triple, ..maybe only verbs?? Example, the BDFL is willing to accept the pep if there is consensus
	//more like check if sentence starts with these terms	
	static String conditionalList[] = {"if","should","should be","when","unless","i hope","whether","once","would","might","why"};
	//generalised phrases should not be included, eg "In fact any PEP is rejected by default if no consensus is obtained."  "every pep requires acceptance", etc
	//should be checked as the term beofre the subject..or object?
	static String generalisedList[] = {"any","every"};
	static String generalisedSubjectObjectList[] = {"pep","proposal"};
	// checked at start of sentence
	// maybe not only at start, but at triple level cheking is required 
	static String questionPhrases[] = {"if ","should have been","i think","i hope","it will","unless ", "in case"};
	// check inside each verb term in extracted triple from sentence
	static String negationTerms[] = {"if","not","will be","can","is n't","nor","should","should be","would","needs to be","may","will","hav n't","have n't","be","might", "n't","whether","zero"}; // added later for test												// "should","should be"
	
	//ROLE, REASONS and STORYLINE EXTRACTION SECTION
	static String reasonIdentifierTerms[] = {"because","since", "based on","due to","thanks to","accordingly","as a result of","through"};
	// implementing this will help as not all reasons are triples
	static String ActualReasons[] = {"discussion","bdfl pronouncement","poll results","voting results","vote results","consensus","no consensus","feedback","favorable feedback","no popular support","favourable feedback","poor syntax","limited utility","difficulty of implementation","bug magnet","controversy","majority"};  
			//lack of an overwhelming majority 
	//find the order of events
	static String storyline[] = {"following", "after","later", "afterwards"};	
	// what is the role of the message author in relation to the pep, pep_author, bdfl, bdfl_delegate or standard community member
	//which states to find messageAuthor role
	//get message author roles for this states											
											//standard states changes by users i want to capture 
											//"draft" ,	dont need draft as its always by author
	static String statesToGetRolesAndReasons[]  = {"bdfl","proposal","open","active", "pending", "close", "final","accept", "defer","replace","reject","postpone", "incomplete" ,  "supersede", "update", "vote", "poll" , "consensus"}; 			//<-new states changes by user which i want to capture  
	//----------END 
	
	//single liners - these terms should exists on its own and not part of any sentence
	static String isolatedTerms[] = {"BDFL Pronouncement","Rejection Notice","Withdrawal Notice"};
	static String storyElements[] = {"discussion"};				// Story elements
	static String unwantedTerms[] = {"patch","bug"};
	
	//certain sentences end with questions, which make them conditional. But we want to include the triples extracted from certain questions as they wont be. 
	// For example, ' Does anyone object to me naming myself PEP czar for PEP 314' is a call for volunteering for pep czar which should be allowed
	//Example "So , can you please indicate your vote for or against incorporating PEP 391 into Python ?"
	//allow these labels to be captured if they exist ina  sentence which ends with question mark
	static String allowLabelsAsQuestion[] = {"call_for_vote","call_for_poll","volunteer_pep_czar"};
		
	// Files for output
	static FileWriter writerForDisco = null, writerAll = null;

	// Disable as not used at the moment
	// static DISCOSimilarity disSim = new DISCOSimilarity();

	static inits init = new inits();

	// prevent labels and states from being repeated. First instance should be okay.
	// states
	static List<String> dontAllowRepetions = Arrays.asList("pep_propose","pep_proposes", "pep_proposed", "pep_author_proposes_pep","pep_accepted", "pep_rejected", "pep_final", "propose_syntax","wrote_pep", "sig_positive"); // ,"draft","open","active","pending",
											 // "closed","final","accepted","deferred","replaced","rejected","postponed","incomplete","superceded");
	
	static ArrayList<Integer> allPepPerTypes = new ArrayList<Integer>();

	// whether to include Daniel State Data
	static Boolean includeStateData = true;

	// triple processing result array static ArrayList<TripleProcessingResult> tpllist = new
	// for each pep, create a TripleProcessingResult object 
	// list of triple processing result object which contains candidate sentences as well
	static ArrayList<TripleProcessingResult> tripleProcessingResult = new ArrayList<TripleProcessingResult>();
	// clean this arraylist after each pep
	
	//this one just for states
	static ArrayList<String> statusProcessingResult = new ArrayList<String>();

	public static final Boolean replaceCommas = false;  //and replace double empty spaces
	
	//declare variables needed for
	
	
	// initialise Disco
	static UseDISCOSentenceSim ds = null; // new UseDISCOSentenceSim();
	// ClausIE
	static ClausIECaller cie = null;

	static ClausIEMain ciem = new ClausIEMain();
	// ReVerb
	static ReVerbFindRelations rr = new ReVerbFindRelations();
	// Ollie
	static JavaOllieWrapperGUIInDev jw = new JavaOllieWrapperGUIInDev();


	// GetAllPEPMessages
	GetAllMessagesInPep gpm = new GetAllMessagesInPep();
	
	// ProcessDummyFile
	ProcessDummyFile pdf = new ProcessDummyFile();		
	//CheckAllSentencesInMessage

	static GetAllSentencesInMessage casm = new GetAllSentencesInMessage();	
	static PythonSpecificMessageProcessing pm = new PythonSpecificMessageProcessing();	
	static ArrayList<LabelTriples> AssignPEPNumbersLabels = new ArrayList<LabelTriples>();
	
	public static void main(String [] args) throws IOException
	   {
		MysqlConnect mc = new MysqlConnect();
		Connection conn = mc.connect();
		if (conn == null){
			System.exit(0);
		}
		mc.checkIfDatabaseEmpty(conn);
		String labelTableName = "labels";
		// read triples labels to match in string matching
		 ReadLabels b = new ReadLabels();
		 String baseAssignPEPNumbersLabelsFilename = "C:/Users/psharma/Google Drive/PhDOtago/Code/inputFiles/input-AssignPEPNumbers.txt";		
		 AssignPEPNumbersLabels = b.readLabelsFromFile(baseAssignPEPNumbersLabelsFilename,AssignPEPNumbersLabels,"AssignPEPNumbers Matching",labelTableName,conn,false);
		
		 String singlesAndDoublesFilename = "C:/Users/psharma/Google Drive/PhDOtago/scripts/inputFiles/input-BaseSinglesAndDoubles.txt";
		 
		 singlesDoublesTriplesList = b.readSinglesDoublesTriplesFromFile(singlesAndDoublesFilename,singlesDoublesTriplesList,"Single and Double terms",conn, false);	//false as dont output
		 
		ArrayList authors = new ArrayList();	
		
		
		Integer i =282;
		
//		for (int i=0; i<4000; i++){
			   try {
						//System.out.println("\nPep " + i); 
						getMessagesWherePEPNumberIsAssigned(i,conn);
				   }
				   catch (Exception e){
					  // continue;
				   }
//		   }
		System.out.println("\nFinished processing"); 
		   //generateCsvFile("c:\\scripts\\test.csv"); 
	   }
	
	public static void getMessagesWherePEPNumberIsAssigned(Integer pep,Connection conn){
		String message =null,messageSubject=null, sendername=null, folder=null;		
		Integer pepNum = null;
		//ArrayList<Integer> peps = new ArrayList<Integer>();
		
		//set parameters
		ProcessingRequiredParameters prp = new ProcessingRequiredParameters();
			prp.setLibraries(cie, ciem, rr, jw, ds);
			prp.setLabels(AssignPEPNumbersLabels,AssignPEPNumbersLabels, ActualReasons,dontCheckLabels);	//set labels (triples, doubles) for reasons as well
			prp.setWhichLibrariesToCheck(checkReverb,checkClauseIETrue, checkOllieTrue,includeStateData,replaceCommas);
			prp.setTermLists(singlesDoublesTriplesList,wordsList,conditionalList, negationTerms, reasonIdentifierTerms, questionPhrases,
							unwantedTerms,isolatedTerms,candidateSentencesList,allowLabelsAsQuestion);
			prp.setResulstWritingToFile(writerForDisco, writerAll);
			prp.setUtils(pm,pms,pd,casm,cesdl,tripleProcessingResult);	//initialise these class object once and pass rather than creating again and again
			prp.setLibraryModelLocation(NLP_MODELS_PATH);
			prp.setStatesToGetRolesAndReasons(statesToGetRolesAndReasons);
			//these are independent parameters for using this class
//			prp.setReadDummyFile(readDummyFile);
			prp.setNotSure(statusProcessingResult);
		
		try	{
			
			String query= "";
			if (pep<0)
				System.out.println("Error no pep as " + pep);
			else	    	  																	//pep = " + pep + " and
				query = "SELECT pep,folder, subject, clusterbysenderfULLname,analyseWords FROM allmessages where analyseWords like '%pep number%';"; // and folder = 'c:\\datasets\\python-ideas' and clusterbysenderfullname like '%Barry%';";

			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);			

			while (rs.next())  {
				message = rs.getString("analysewords");
				pepNum = rs.getInt("pep");
				messageSubject = rs.getString("subject");
				folder = rs.getString("folder");
				sendername = rs.getString("clusterbysenderfULLname");
				//System.out.println("\n\n identified ");

				//extract sentences from this message
				//get triples from each sentence
				// match triple and see if can find instances where pep number is assigned
				//   if so then get the old pep tile for that pep idea and add to the list of pep titles for each pep

				//			String title = getMorePEPTitles();
				String v_message = message;
				Integer sentenceCounter = 0, insertDiscussionCounter = 0;
				String previousParagraph = null, nextParagraph = null; 
				//temporary declare these variables
				String v_idea = "", subject = "", verb = "", object = "";	
				if (!v_message.isEmpty()) {
					try 
					{    
						//we want to check the subkect also ...so simply we ad the subject at the beginning of the message	       
						if ( subject==null || subject == "" || subject.isEmpty() )
						{}
						else 
							v_message = subject.toLowerCase() + ". \n\n" + v_message;   //this will check it for triples, and later doubles and singles

						String PreviousSentenceString = null;
						String[] paragraphs = v_message.split("\\n\\n");
						//System.out.println("----- Now Processing new Message ID: ("+v_message_ID+") total paragraphs.." + paragraphs.length);
						Integer count=0,paragraphCounter=0;        		   
						Boolean permanentMessageHasLabel = false;		//if message has any label captured set this to true

						//check message
						for (String g: paragraphs)
						{	

							if (paragraphs.length ==1){	        				
								nextParagraph = null; previousParagraph = null;
							}
							//paragraph length = 2,3,4 onwards
							else {
								if (paragraphCounter==0)
									previousParagraph = null;
								else 
									previousParagraph = paragraphs[paragraphCounter-1];

								if (paragraphCounter==paragraphs.length-1)
									nextParagraph = null;
								else
									nextParagraph = paragraphs[paragraphCounter+1];

								Reader reader = new StringReader(g);
								DocumentPreprocessor dp = new DocumentPreprocessor(reader);

								//sometimes a paragrapgh has sentences but are not captured as sentences. So if sentence identified then check, so we just add a full-stop at end of paragraph	        				   
								if(!g.contains("."))
									g = g + ".";

								for (List<HasWord> eachSentence : dp) 		        	
								{   	

									boolean dependency = true, v_stateFound = false, foundLabel = false, double_Found = false,foundReason=false;
									String nextSentence = null, CurrentSentenceString = Sentence.listToString(eachSentence);																	
									++sentenceCounter;

									//remove unnecessary words like "Python Update ..."																	
									CurrentSentenceString = psmp.removeLRBAndRRB(CurrentSentenceString);								
									CurrentSentenceString = psmp.removeDivider(CurrentSentenceString);
									CurrentSentenceString = psmp.removeUnwantedText(CurrentSentenceString);	
									//									allSentenceList.add(CurrentSentenceString);
									//System.out.println("\t SENTENCE " + CurrentSentenceString);	
									

									//if triples matched
									//   insert aditional title into table "pepdetails_titlesforeachpep"
									String dummyTitle = "test";
									//System.out.println("\nCurrentSentenceString: "+CurrentSentenceString );
									if (CurrentSentenceString.toLowerCase().contains("pep number")){
										System.out.println(pepNum +" | " + folder+" | "+messageSubject +" | "+ sendername+" | "+CurrentSentenceString );
										//System.out.println("\n\n message subject: "+messageSubject );
										//System.out.println("\n\n message sendername: "+ sendername);
										insertAdditionalTitle(pep,dummyTitle,conn);
										//System.out.println("\n\n CurrentSentenceString: "+CurrentSentenceString );
										
										//Check triple labels
										
										/*	
										foundLabel = cesdl.checkEachSentenceAgaistDifferentLibrariesForLabels(prp);
										ProcessLabels pt = new ProcessLabels();
										boolean showCIEDebugOutput = false;
										//extracted triples
										String tripleArray[][] = prp.getCm().computeClausIE(CurrentSentenceString, showCIEDebugOutput);
										tripleArray = pt.cleanArray(tripleArray);
					//$$				pt.setVariables(conditionalList, reasonTerms);
																			
//										System.out.println("\n" +"Triple Processing for CIE");
										resultObject = pt.processTriples(v_message,resultObject,tripleArray,cm,  rvr,  jw,entireParagraph, CurrentSentenceString,PreviousSentenceString, nextSentence, subject,
												object, verb,v_idea, v_pepNumber, v_date, v_message_ID,author,MessageAuthorsRole, paragraphCounter,sentenceCounter, previousParagraph, nextParagraph, 
												writerForDisco, writerAll,conditionalList,negationTerms, reasonTerms,reasons,  reasonLabels, 2, v_statesToGetRoles, dontCheckLabels); 
										 */
										
										
									}

									//maybe shud check if foundLabel is false??

									//DO MORE PROCESSSING..LOOK FOR MORE DATA for following cases
									//2. Capture Label Doubles, for all singles and doubles, - try match, and - if matched, find reason,  and - insert in results
									//check for BDFL Pronouncement, First Draft, second draft, RESULTS OF THE VOTE 
									//											System.out.println("After Checking sentence for triples in All Libraries, now checking for doubles");
									ProcessSinglesAndDoubles cpd = new ProcessSinglesAndDoubles();

									PreviousSentenceString = CurrentSentenceString;
								}//end of for loop for sentence
								paragraphCounter++;
							}//end else if

						} //end for loop for paragraphs	        		
					}  //end try       
					catch (Exception e){ 
						//System.out.println(v_message.getMessage_ID() + " ______here  " + e.toString() + "\n");
						//continue;
					} 
				}


			}

			//for all the pep titles found for a pep, 
			//  assign the pep number to messages which have the pep titles in the subject

			st.close();
		}	catch (Exception e)	{
			System.err.println("Got an exception! ");
			System.err.println(e.getMessage());
		}

		//return pepTypeString;
	}
	
	public static String getMorePEPTitles(){
		String title = "";
		
		return title;
	}
	
	public static void insertAdditionalTitle(Integer pep, String title, Connection conn){
		try
	    {
	      String query = " insert into pepdetails_titlesforeachpep (pep, additional_titles)"
	        + " values (?, ?)";

	      // create the mysql insert preparedstatement
	      java.sql.PreparedStatement preparedStmt = conn.prepareStatement(query);
	      preparedStmt.setInt (1, pep);
	      preparedStmt.setString (2, title);
	      preparedStmt.execute();
	      //conn.close();
	    }
	    catch (Exception e)
	    {
	      System.err.println("Got an exception!");
	      System.err.println(e.getMessage());
	    }
		
	}
	
	
}