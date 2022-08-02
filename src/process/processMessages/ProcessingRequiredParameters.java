package Process.processMessages;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

//import org.apache.lucene.index.CorruptIndexException;

import EnglishOrJavaCode.EnglishOrCode;
import callIELibraries.JavaOllieWrapperGUIInDev;
import Process.ReasonExtraction.PrepareTrainingTestingData.ProcessPastMessages;
import Process.models.Pep;
//import Process.StanfordNLP.CoRefClient;
import Process.processLabels.TripleProcessingResult;
import Process.stringMatching.checkMessage;
import Process.stringMatching.checkSentence;
import Read.postReadingUpdates.UpdateAllMessages_EliminateMessagesNotBelongingToCurrentProposal;
import Read.readMetadataFromWeb.GetProposalDetailsWebPage;
import Read.readRepository.ReadLabels;
import callIELibraries.ClausIECaller;
import callIELibraries.ReVerbFindRelations;
import callIELibraries.SocketClientForNLP;
import callIELibraries.UseDISCOSentenceSim;
//import causalRelExtractor.create_explicit_corpus;
//import causalRelExtractor.create_explicit_corpus;
import connections.MysqlConnect;
import de.mpii.clause.driver.ClausIEMain;
import jaws.WordnetSynonyms;
import main.DeMAP_Miner_Process;
import main.stillToSort.inits;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import properties.PropertiesFile;
import stanfordParser.Coreferencer;
import stanfordParser.StanfordNLPTools;
import utilities.ComputeMessageDifference;
import utilities.PepUtils;
import utilities.StateAndReasonLabels;

// may 20221...since we focusssing on members influence,
// i added clusterbysenderfullname field (from allmessages) as sendername in results tables autoextractedinfluencecandidatemessages, autoextractedinfluencecandidatesentences;

public class ProcessingRequiredParameters {
	
	//feb 2020
	boolean outputfordebug;
	
	//purpose of script, once read from file is updated into the booleans below
	String scriptPurpose, proposalIdentifier;	
	boolean currentAndNearbySentences, 
		processMining, 							//substate extraction
		relationExtraction,						//relation extraction
		sentenceExtractionForTopicModelling,	//topic modelling
		manualReasonExtraction,					//manual reason extraction
		candidateReasonSentenceSuggestion,  	//automated reason extraction
		sentenceParagraphWritingToTable;
	
	ProcessPastMessages tdm;	//for sentence labeling..for initializing terms lists and extracting whether these terms exist in message subject and sentences in message
	boolean reasonExtraction; //feb 2019 do we want to extract reasons
	//dataset
	boolean selectedAll, selectedMinMax, selectedSelected, selectedList, processMessageID, readDummyFile,  selectedType, rerunResults; // type; standard, process or informational
	Integer messageID; //messageID for processing specific message ID
	// Standard = about python language, functional enhancements - more action here, eg Pep 308 is standard pep, Process = related to python processes
	boolean allStandard, allProcess, allInformational; 		// 
	boolean checkReverb, checkClauseIETrue, checkOllieTrue;	// option to include checking triples in these libraries	
	boolean checkReverbTrue,dependencyTrue,includeStateData;
	//processing restart parameters
	boolean restart, restartAfterEachProposal;	
	Integer pepNumberForRestart, messageIDForRestart,linesToProcess;
	Integer runForNumberOfMessagesLimit, processedMessageCounter;
	String scriptsHomeFolderLocation = null,outputFileNameForTopicModellingSentences = null,tableToStoreExtractedRelations = null;	
	PrintWriter pwForTopicModellingSentences = null;
	String proposalTableName; //pepdetails or jepdetails
	String PEPStatesTableName; //pepstates_danieldata_datetimestamp
	
	//REASON SECTIONS 
	//manual reason terms extraction and automatic reason candidate extraction
	WordnetSynonyms wn;
	//create_explicit_corpus cec;

	boolean outputRelationExtractionDetails; 

	Integer pep = null;	Integer proposal = null;  //just for dummyfile processing which would need a identifier	

	public MysqlConnect mc;	Connection conn;	//connections	
	FileWriter writerAll, writerForDisco;//writing result to file

	//initialised libraries
	ReVerbFindRelations rvr;	JavaOllieWrapperGUIInDev jw;	ClausIEMain cm;	UseDISCOSentenceSim ds;  // MyFilteredClassifier fc,
	EnglishOrCode ec = null;	//check english or code
	String NLP_MODELS_PATH;
	ClausIECaller cie;
	
	//parameters for processing
	//check using which libraries	
	boolean showInputFileTriplesAfterLoading;	//debug messages	

	// which peps to processeidin  //true															//normally true

	//message processing parameters
	boolean performCoReference, checkReasons;
	boolean repeatedLables, repeatedSentences, includeEmptyRows;//what to include in outputinit
	boolean readEntireFileAsMessage;	//for processing dummy files
	Integer linesToAnalyse = 25;		//limit our analysis to the first x number of lines of each message, except for commits and checkins messages which will have a particular pep well below this number 
	//-1 for all lines				//but will many times have reasons for acceptance as well ...
	boolean processBasedOnProposals; //sometimes a message will have several proposals. How to differentiate which sentences is for which
	//Although very hard, an approach is to, between multiple proposals, if set to true, this will cause the analysis to skip everything as soon as another proposal is encountered and carry on as asoon as current proposal is encountered
	boolean replaceCommas;  //and replace double empty spaces

	String dummyFileLoc;	//process dummy file location //shift this in code folder
	String singlesAndDoublesFilename,termsListFileName;
	String topicModellingSentencesOutFile;

	//these are common classes to be used
	PepUtils pu;
	ProcessDummyFile pdf;
	GetProposalDetailsWebPage pd;	
	
	GetAllMessagesInPep gpm;
	GetAllSentencesInMessage casm;
	checkMessage chkm;
	checkSentence cs;
	CheckSentenceHasTripleTerms_SendToIELibraries_InsertIntoDB cesdl;
	ComputeMessageDifference c;// computer message difference		// System.out.println("diff " + c.difference("apple a","apple b"));
	DeMAP_Miner_Process pm;
	ProcessMessageAndSentence pms; 
	public PythonSpecificMessageProcessing psmp;
	SplitMessageByUniqueIdentifier smui;	
	UpdateAllMessages_EliminateMessagesNotBelongingToCurrentProposal emnbcp;
	StanfordNLPTools snlp;
	//	CoRefClient cr;
	//	SocketClientForNLP scnlp;	

	//jan 2019 coreferencing
	Coreferencer coref;
	
	//HOLDING Data
	String[] coreferenceSubjects;
	// array of terms for different purposes
	String[] reasonTerms,reasons,statesToGetRoles,ActualReasons;
	ArrayList<SinglesDoublesTriples> singlesAndDoubles = new ArrayList<SinglesDoublesTriples>();	
	String[] reasonIdentifierTerms,isolatedTerms,statesToGetRolesAndReasons;
	List<String> wordsList,candidateSentencesList,sentencesRemoveDuplicates;
	//Lists to hold data
	// triple processing result array static ArrayList<TripleProcessingResult> tpllist = new
	// for each pep, create a TripleProcessingResult object 
	// list of triple processing result object which contains candidate sentences as well
	ArrayList<TripleProcessingResult> tripleProcessingResult;
	// clean this arraylist after each pep
	ArrayList<String> statusProcessingResult;
	ArrayList<LabelTriples> labels,reasonLabels;	
	ArrayList<SinglesDoublesTriples> singlesDoublesTriplesList;
	String skipMessagesForProcessingLine;
	ArrayList<Integer> skipMessagesForProcessing;	//these messages have some error and cannot be processed by Claus IE
	
	//counter ffor how many messages the scriot has processed
	
	//dec 2018
	static InputStream inputStream;
	static SentenceModel model;
	static SentenceDetectorME detector;
	//jan 2019
	static SentenceSimplification ss = new SentenceSimplification();

	public SentenceDetectorME getDetector() {
		return detector;
	}
	
	//jan 2019, terms list, can be moved to file and read from there but some terms have space
	String dontProcessList[] = { " if " };	
	Boolean checkMessageSubject = false;	
	String conditionalList[] = {"if","should","should be","when","can","can be","unless","i hope","whether","once","would","might","why","assuming","although","until"};
	String generalisedList[] = {"any","every", "that","a","such"}; //dec 2018, added 'that' (that pep instead of this one), and 'a pep', and 'such proposals' 
	String antiGnerelisedList[] = {"a pep new","a new"};	//jan 2019, if we catch a generalised term, there are some cases which can be excluded 
	String generalisedSubjectObjectList[] = {"pep","proposal"};
	String questionPhrases[] = {"if ","should have been","i think","i hope","it will","unless ", "in case"};
	String negationTerms[] = {"if","not","do not","will be","can","can be","is n't","nor","should","should be","would","needs to be","may","will","hav n't","had n't","have n't","might",
								"n't","whether","zero","no","never","never been","need","would need","before","zero","once","building","be","suggest","probably","after","never","hope",
								"could be","couldn't","could not","rather","when","come","neither","but","expected","difficulty","unable"}; // added later for test	//jan 2019, unable to reach consensus
								//dec 2018, added 'building' (author responsible for building consensus), be in (i suggest the pep be rejected)
								// "should","should be" 
	//jan 2019, dont check negation for some labels as its wont be able to capture otherwise
	String dontCheckNegationForLabels[] = {"no_consensus"}; 
	String unwantedTerms[] = {"patch","bug"};
	String allowLabelsAsQuestion[] = {"request_pep_number","call_for_vote","inviting_votes","call_for_poll","volunteer_pep_cz"};
	String dontCheckLabels[] = {""}; //"discussion","poll", "vote","proposal"	 xxx= placeholder

	InsertMessageTracking imt;
	
	//initialise all common parameters, with DEFAULT VALUES...errors and exit statements
	public ProcessingRequiredParameters() {
		pep=null;

		//initialise database connections		
		this.mc = new MysqlConnect();		this.conn = mc.connect();
		if (this.conn == null){			System.exit(0);		}

		this.scriptsHomeFolderLocation = null;			proposalTableName="";
		//this.ds = new UseDISCOSentenceSim();

		//commonly used classes across the application
		this.pu = 	new PepUtils();		this.psmp = new PythonSpecificMessageProcessing();		this.pms = 	new ProcessMessageAndSentence();	
		this.gpm = 	new GetAllMessagesInPep();// GetAllPEPMessages		
		// GetMessage		
		this.pdf =	new ProcessDummyFile();	// ProcessDummyFile	
		this.casm = new GetAllSentencesInMessage();	//CheckAllSentencesInMessage		
		this.emnbcp = new UpdateAllMessages_EliminateMessagesNotBelongingToCurrentProposal();
		this.ec = 	new EnglishOrCode();  //check whether text is english or Code
		this.chkm = new checkMessage();		this. cs =	new checkSentence();		this.pd = 	new GetProposalDetailsWebPage();

		//labels
		labels = new ArrayList<LabelTriples>();		
		singlesDoublesTriplesList = new ArrayList<SinglesDoublesTriples>();
		reasonLabels = new ArrayList<LabelTriples>();

		c = new ComputeMessageDifference();// computer message difference		// System.out.println("diff " + c.difference("apple a","apple b"));
		//checkEachSentenceAgaistDifferentLibrariesForLabels
		cesdl = new CheckSentenceHasTripleTerms_SendToIELibraries_InsertIntoDB();

		wn = new WordnetSynonyms();
//		cec = new create_explicit_corpus();
		
		//march 2020 ...we need this for thesis
		imt = new InsertMessageTracking();
		
		//certain parameters we define the default value..in case they are not read in from file..same vales as in input file...
		this.restart=true; this.showInputFileTriplesAfterLoading=false; this.performCoReference=true; this.outputRelationExtractionDetails=false;
		this.selectedAll=true; this.selectedMinMax=false; this.selectedSelected=false; this.selectedList=false; this.processMessageID=false; this.sentenceParagraphWritingToTable=false;
		this.readDummyFile=false;	this.selectedType=false;
		this.rerunResults=false; this.checkReasons=false;
		this.allStandard=false; this.allProcess=false; this.allInformational=false; 
		this.checkClauseIETrue=true; this.checkOllieTrue=false; this.checkReverb=false;
		this.repeatedLables=false; this.repeatedSentences=false; this.includeEmptyRows=false; 
		this.readEntireFileAsMessage=true; 	this.processBasedOnProposals=true;
		this.replaceCommas=false;
		
		skipMessagesForProcessing = new ArrayList<Integer>();
		//dec 2018
		try {
			inputStream = new FileInputStream("D://lib//openNLP//en-sent.bin");		    model = new SentenceModel(inputStream); 	    									
			detector = new SentenceDetectorME(model);  //Instantiating the SentenceDetectorME class
		}	catch(Exception e){ System.out.println("Exception Initialising OpenNLP");	}
		
		//jan 2019
		//SinglesDoublesTriples f1  = new SinglesDoublesTriples(); f1.setLabel(label); f1.setSingleOrDoubleTerms(singleOrDoubleTerms);  singlesAndDoubles.add(f1);
		SinglesDoublesTriples f2  = new SinglesDoublesTriples(); f2.setLabel("pre-pep"); f2.setSingleOrDoubleTerms("pre-pep");  singlesAndDoubles.add(f2); 
		//SinglesDoublesTriples f3  = new SinglesDoublesTriples(); f3.setLabel(label); f3.setSingleOrDoubleTerms(singleOrDoubleTerms);  singlesAndDoubles.add(f3);
		//SinglesDoublesTriples f4  = new SinglesDoublesTriples(); f4.setLabel(label); f4.setSingleOrDoubleTerms(singleOrDoubleTerms);  singlesAndDoubles.add(f4); 
		SinglesDoublesTriples f5  = new SinglesDoublesTriples(); f5.setLabel("secondDraft"); f5.setSingleOrDoubleTerms("second draft");  singlesAndDoubles.add(f5); 
		SinglesDoublesTriples f6  = new SinglesDoublesTriples(); f6.setLabel("secondDraft"); f6.setSingleOrDoubleTerms("draft 2");  singlesAndDoubles.add(f6); 
		SinglesDoublesTriples f7  = new SinglesDoublesTriples(); f7.setLabel("thirdDraft"); f7.setSingleOrDoubleTerms("third draft");  singlesAndDoubles.add(f7); 
		SinglesDoublesTriples f8  = new SinglesDoublesTriples(); f8.setLabel("thirdDraft"); f8.setSingleOrDoubleTerms("draft 3");  singlesAndDoubles.add(f8); 
		SinglesDoublesTriples f9  = new SinglesDoublesTriples(); f9.setLabel("fourthDraft"); f9.setSingleOrDoubleTerms("fourth draft");  singlesAndDoubles.add(f9); 
		SinglesDoublesTriples f10 = new SinglesDoublesTriples(); f10.setLabel("fourthDraft"); f10.setSingleOrDoubleTerms("draft 4");  singlesAndDoubles.add(f10); 
		SinglesDoublesTriples f11 = new SinglesDoublesTriples(); f11.setLabel("fifthDraft"); f11.setSingleOrDoubleTerms("fifth draft");  singlesAndDoubles.add(f11); 
		SinglesDoublesTriples f12 = new SinglesDoublesTriples(); f12.setLabel("fifthDraft"); f12.setSingleOrDoubleTerms("draft 5");  singlesAndDoubles.add(f12); 
		SinglesDoublesTriples f13 = new SinglesDoublesTriples(); f13.setLabel("round2"); f13.setSingleOrDoubleTerms("second round");   singlesAndDoubles.add(f13); 
		SinglesDoublesTriples f14 = new SinglesDoublesTriples(); f14.setLabel("round2"); f14.setSingleOrDoubleTerms("round 2");  		singlesAndDoubles.add(f14); 
		SinglesDoublesTriples f15 = new SinglesDoublesTriples(); f15.setLabel("round3"); f15.setSingleOrDoubleTerms("third round");  singlesAndDoubles.add(f15); 
		SinglesDoublesTriples f16 = new SinglesDoublesTriples(); f16.setLabel("round3"); f16.setSingleOrDoubleTerms("round 3");  singlesAndDoubles.add(f16); 
		SinglesDoublesTriples f17 = new SinglesDoublesTriples(); f17.setLabel("round4"); f17.setSingleOrDoubleTerms("fourth round");  singlesAndDoubles.add(f17); 
		SinglesDoublesTriples f18 = new SinglesDoublesTriples(); f18.setLabel("round4"); f18.setSingleOrDoubleTerms("round 4");  singlesAndDoubles.add(f18);
		SinglesDoublesTriples f19 = new SinglesDoublesTriples(); f19.setLabel("round5"); f19.setSingleOrDoubleTerms("fifth round");  singlesAndDoubles.add(f19); 
		SinglesDoublesTriples f20 = new SinglesDoublesTriples(); f20.setLabel("round5"); f20.setSingleOrDoubleTerms("round 5");  singlesAndDoubles.add(f20); 
		SinglesDoublesTriples f21 = new SinglesDoublesTriples(); f21.setLabel("round6"); f21.setSingleOrDoubleTerms("sixth round");  singlesAndDoubles.add(f21);
		SinglesDoublesTriples f22 = new SinglesDoublesTriples(); f22.setLabel("round6"); f22.setSingleOrDoubleTerms("round 6");  singlesAndDoubles.add(f22); 
		SinglesDoublesTriples f23 = new SinglesDoublesTriples(); f23.setLabel("round7"); f23.setSingleOrDoubleTerms("seventh round");  singlesAndDoubles.add(f23); 
		SinglesDoublesTriples f24 = new SinglesDoublesTriples(); f24.setLabel("round7"); f24.setSingleOrDoubleTerms("round 7");  singlesAndDoubles.add(f24); 
		SinglesDoublesTriples f25 = new SinglesDoublesTriples(); f25.setLabel("lastRound"); f25.setSingleOrDoubleTerms("last round");  singlesAndDoubles.add(f25);
	}

	//THESE are VARIABLES which can be changed but APPLIES TO ALL CHOICES OF USE OF THIS SCRIPT
	//these are independent parameters for using this class
	public void readPropertiesFile_setProcessingRequiredParameters	() {
		PropertiesFile wpf = new PropertiesFile();		// wpf.WriteToPropertiesFile("includeEmptyRows", includeEmptyRows.toString());
		//includeStateData
		try {
			proposalIdentifier = wpf.readFromPropertiesFile("proposalIdentifier",false).toLowerCase();		
				System.out.println("proposalIdentifier: " +proposalIdentifier);
			//empty results table
			//Integer rowCount = mc.checkIfDatabaseEmpty(conn);		//show # of records in allmessages table				
			//resultsdbcsv.emptyResultsDatabaseTable(conn);

			//Input and OutputFile - we use for outputting infomation
			scriptsHomeFolderLocation       = wpf.readFromPropertiesFile("scriptsHomeFolderLocation",true);	//everything is read in and stored here
			NLP_MODELS_PATH    			    = scriptsHomeFolderLocation + "inputFiles\\models\\"; 
			dummyFileLoc                    = scriptsHomeFolderLocation + "inputFiles//dummyFile.txt";	//process dummy file location //shift this in code folder
			singlesAndDoublesFilename       = scriptsHomeFolderLocation + "inputFiles//input-LabelsSinglesAndDoublesTriples.txt";
			termsListFileName				= scriptsHomeFolderLocation + "inputFiles//statesSubStateExtraction//termsList.txt";
			topicModellingSentencesOutFile  = scriptsHomeFolderLocation + "TopicModelling//PEPtopic-sentences.txt";
			//processing parameters
			PEPStatesTableName				= wpf.readFromPropertiesFile("proposalStateTableName",true);
			scriptsHomeFolderLocation       = wpf.readFromPropertiesFile("scriptsHomeFolderLocation",true);
			sentenceParagraphWritingToTable = Boolean.valueOf(wpf.readFromPropertiesFile("sentenceParagraphWritingToTable",true));
			restart							= Boolean.valueOf(wpf.readFromPropertiesFile("restart",true));
			restartAfterEachProposal		= Boolean.valueOf(wpf.readFromPropertiesFile("restartaftereachproposal",true));
			checkReasons					= Boolean.valueOf(wpf.readFromPropertiesFile("checkReasons",true));
			showInputFileTriplesAfterLoading= Boolean.valueOf(wpf.readFromPropertiesFile("showInputFileTriplesAfterLoading",true));		
			performCoReference				= Boolean.valueOf(wpf.readFromPropertiesFile("performCoReference",true));		//perform coreferencing
			String coreferenceSubjectsLine  = wpf.readFromPropertiesFile("coreferenceSubjects",true);
			outputRelationExtractionDetails = Boolean.valueOf(wpf.readFromPropertiesFile("outputRelationExtractionDetails",true));
			selectedAll						= Boolean.valueOf(wpf.readFromPropertiesFile("selectedAll",true));
			selectedMinMax					= Boolean.valueOf(wpf.readFromPropertiesFile("selectedMinMax",true));
			selectedSelected				= Boolean.valueOf(wpf.readFromPropertiesFile("selectedSelected",true));
			selectedList					= Boolean.valueOf(wpf.readFromPropertiesFile("selectedList",true));
			processMessageID				= Boolean.valueOf(wpf.readFromPropertiesFile("processMessageID",true));
			messageID						= Integer.parseInt(wpf.readFromPropertiesFile("messageID",true));
			readDummyFile					= Boolean.valueOf(wpf.readFromPropertiesFile("readDummyFile",true));
			selectedType					= Boolean.valueOf(wpf.readFromPropertiesFile("selectedType",true));
			rerunResults					= Boolean.valueOf(wpf.readFromPropertiesFile("rerunResults",true));
			allStandard						= Boolean.valueOf(wpf.readFromPropertiesFile("allStandard",true));
			allProcess						= Boolean.valueOf(wpf.readFromPropertiesFile("allProcess",true));
			allInformational				= Boolean.valueOf(wpf.readFromPropertiesFile("allInformational",true));	 
			checkReverb						= Boolean.valueOf(wpf.readFromPropertiesFile("checkReverb",true));
			checkClauseIETrue				= Boolean.valueOf(wpf.readFromPropertiesFile("checkClauseIETrue",true));
			checkOllieTrue					= Boolean.valueOf(wpf.readFromPropertiesFile("checkOllieTrue",true));
			repeatedLables					= Boolean.valueOf(wpf.readFromPropertiesFile("repeatedLables",true));
			repeatedSentences				= Boolean.valueOf(wpf.readFromPropertiesFile("repeatedSentences",true));
			includeEmptyRows				= Boolean.valueOf(wpf.readFromPropertiesFile("includeEmptyRows",true));
			readEntireFileAsMessage			= Boolean.valueOf(wpf.readFromPropertiesFile("readEntireFileAsMessage",true));
			linesToAnalyse					= Integer.parseInt(wpf.readFromPropertiesFile("linesToAnalyse",true));
			processBasedOnProposals			= Boolean.valueOf(wpf.readFromPropertiesFile("processBasedOnProposals",true));
			replaceCommas					= Boolean.valueOf(wpf.readFromPropertiesFile("replaceCommas",true));
			//dec 2018
			includeStateData				= Boolean.valueOf(wpf.readFromPropertiesFile("includeStateData",true));
			skipMessagesForProcessingLine   = wpf.readFromPropertiesFile("skipMessagesForProcessingLine",true);
			if(skipMessagesForProcessingLine.contains(" ")) {
				for(String k: skipMessagesForProcessingLine.split(" ")) 
					skipMessagesForProcessing.add(Integer.parseInt(k));				
			}else {
				if(skipMessagesForProcessingLine==null || skipMessagesForProcessingLine.isEmpty() || skipMessagesForProcessingLine.length()==0) 
					skipMessagesForProcessing.add(0);	//add a 0...some value has to be tehre i guess..we going to skip messageid 0, which wont exist anyway so should be fine
				else 
					skipMessagesForProcessing.add(Integer.parseInt(skipMessagesForProcessingLine));				
			}
			//coreference subjects
			if(coreferenceSubjectsLine.contains(" ")) {				
					coreferenceSubjects = coreferenceSubjectsLine.split(" ");				
			} else 
				coreferenceSubjects[0] = coreferenceSubjectsLine;			
		}
		catch (Exception ex) {
			ex.printStackTrace();			System.out.println(StackTraceToString(ex)  );	
		}
		
		//initialise nlp libraries
		//initialise stanford nlptools  in stanfordparser package for coreference, entity extraction, etc
		//snlp = new StanfordNLPTools(); System.out.println("Initilaised SNLP");
		//libraries
		this.rvr = null;  //new ReVerbFindRelations();		System.out.println("Initialsied New reverb");
		this.jw = null; //new JavaOllieWrapperGUIInDev();	System.out.println("Initialsied New Ollie");	
		this.cm = new ClausIEMain();				System.out.println("Initialsied New CIE");
		// ClausIE
//		this.cie = new ClausIECaller();				System.out.println("Initialised ClausIECaller");
				
		//initialise stanford nlptools  in stanfordparser package for coreference, entity extraction, etc
		StanfordNLPTools snlp = new StanfordNLPTools();
				
		Integer runForNumberOfMessagesLimit,runForNumberOfMessagesCounter;	//this script for triple extractions seems to slow down after procesing soem messages, 
		//so we will exit after these number of message and start the jar again in a batch file
		//parameters common in all - also used when stuck for restart 
		//		Integer pepNumber = 2, MessageID = 1215532; //260659; //39032; //32851; // 31049,31460;110261

		//		Integer pepNumberForRestart,messageIDForRestart; //(8,30563,329974) 432, 381730 will give error

		//February 2018  Things updated for proposals
		//1.replaced "pep" with "proposal" in this file
		//2. added these two external functions as functions in this class PEPUtils - > ProposalUtils.java
		//3. identifier for matching in files
		String proposalIdentifier = null;
		//Relation Extraction variables
		PrintWriter pwForTopicModellingSentences = null;
		Integer sentenceCounterForTopicModelling =0;
		String sentenceTermsForCheckingDots[]=null;
		List<String> wordsList = new ArrayList<String>(), statusList = new ArrayList<String>();
		String CurrentSentence, SentenceClassification, DISCOSimilarity; // Classification using weka text	and // DISCO similarity valu e between two sentences
		// static String DISCOSim; // DISCO similarity + value between two sentences
		String max_idea;    // store the idea returned with the most similar disco match	
		String first, sentencesToMatch[];																	
		List<String> candidateSentencesList = new ArrayList<String>();
		// static DISCOSimilarity disSim = new DISCOSimilarity();// Disable as not used at the moment
		inits init = new inits();
		wordsList = init.initWordsList(wordsList);	
	
		// prevent labels and states from being repeated. First instance should be okay.
		// states
		List<String> dontAllowRepetions = Arrays.asList("pep_propose","pep_proposes", "pep_proposed", "pep_author_proposes_pep","pep_accepted", "pep_rejected", "pep_final", "propose_syntax","wrote_pep", "sig_positive"); // ,"draft","open","active","pending",
		// "closed","final","accepted","deferred","replaced","rejected","postponed","incomplete","superceded");
				
		ArrayList<String> statusProcessingResult = new ArrayList<String>();//this one just for states

		setReadDummyFile(readDummyFile);		setNotSure(statusProcessingResult);

		//assign coreferencing
		//prp.setSocketClientNLP(scnlp);
		setPerformCoreference(performCoReference);  System.out.println("setPerformCoreference(performCoReference): " + performCoReference);
		//String[] coreferenceSubjects = {"pep,proposal, bdfl"};
		setCoreferenceSubjects(coreferenceSubjects);		
		setCheckMessageSubject(checkMessageSubject);
		//Four reason related variables
		// reason identifier terms - 'reasonIdentifierTerms'  - eg because
		// reason labels (triples) read in from file -  reasonLabels
		// actual reasons - ActualReasons - eg favourable feedback
		// states To Get Roles And Reasons - statesToGetRolesAndReasons - we only want to get roles and reasons for specific states, eg accepted, rejected, etc

		setRestart(restart); //restart = false unless its 'selectedAll' option
		setEliminateMessage(emnbcp);
		setLinesToAnalyse(linesToAnalyse);

		ec.initializeTokenizer(); // initialise the Class for english or code

		//			String regex = "\\d+";

		ArrayList<Integer> numbers = new ArrayList<>();
		// add the process and standard peps define in the function
		numbers = init.addPeps(numbers);
		//set script running values
		runForNumberOfMessagesLimit = Integer.parseInt(wpf.readFromPropertiesFile("runForNumberOfMessagesLimit",false));	
		runForNumberOfMessagesCounter=0;
		setRunForNumberOfMessagesLimit(runForNumberOfMessagesLimit); System.out.println("Message Processing Limit: "+ runForNumberOfMessagesLimit);
		setProcessedMessageCounter(runForNumberOfMessagesCounter);

		// date of output
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
		
		//REASONS ...i have no idea when these code woudl be used.
		StateAndReasonLabels l = new StateAndReasonLabels();
		boolean output=true;
		wn.init();
//XX5	cec.init();	
		System.out.print(" stateList >> ");
		l.initStateAndReasonLabels(wn, output);
		List<String>  verbs = l.getVerbs(); 	//ready for review/pronouncement
		List<String>  committedStates = l.getCommittedStates();	//main state changes  e.g. 'Status: Accepted'
		//		System.out.print(" stateList >> ");  for (String line : states)     {                	System.out.print(line+ " ");            }		  System.out.println(" ");  
		ArrayList<ArrayList<String>> getAllStatesList_StatesSubstates = l.getAllStatesList_StatesSubstates();
		ArrayList<ArrayList<String>> reasonsList = l.getReasonTerms();
		List<String>  reasonIdentifierTerms = l.getReasonIdentifierTerms(), proposalIdentifiers = l.getProposalIdentifierTerms();
		ArrayList<ArrayList<String>> specialOSCommunitySpecificTerms = l.getSpecialOSCommunitySpecificTerms();
		List<String>  entitiesList = l.getEntities();
		List<String>  subStates = l.getSubStates();

		setTableToStoreExtractedRelations(tableToStoreExtractedRelations);
		setOutputRelationExtractionDetails(outputRelationExtractionDetails);
		if(candidateReasonSentenceSuggestion)
			setScriptsHomeFolderLocation("c:\\scripts\\");
		else
			setScriptsHomeFolderLocation(scriptsHomeFolderLocation);
		
		ArrayList<String> i = new ArrayList<String>();
		for (ArrayList<String> h: reasonsList) {
			for (String j : h) 
				i.add(j);			
		}
				
		ArrayList<String> a = new ArrayList<String>();
		for (ArrayList<String> h: specialOSCommunitySpecificTerms) {
			for (String j : h) 
				a.add(j);			
		}
		//dec 2018..generalised terms, etc are moved up
		
		setLabels(labels,reasonLabels, listToArray(i) ,dontCheckLabels);	//set labels (triples, doubles) for reasons as well
		setWhichLibrariesToCheck(checkReverb,checkClauseIETrue, checkOllieTrue,includeStateData,replaceCommas);
		setTermLists(singlesDoublesTriplesList,wordsList,conditionalList, negationTerms, listToArray(reasonIdentifierTerms), questionPhrases,unwantedTerms,listToArray(a),candidateSentencesList,allowLabelsAsQuestion);
		//prp.setResulstWritingToFile(writerForDisco, writerAll);
		setUtils(psmp,pms,pd,casm,cesdl,tripleProcessingResult);	//initialise these class object once and pass rather than creating again and again
		setLibraryModelLocation(NLP_MODELS_PATH);
		
		ArrayList<String> termList = new ArrayList<String>(); 
		for (ArrayList<String> line : getAllStatesList_StatesSubstates) {
    		for (String term : line) {
    			termList.add(term);
    		}
    	}
		setStatesToGetRolesAndReasons(listToArray(termList));		
		
		setEnglishOrCode(ec);
		//jan 2019
		try { //coreferencing
			coref = new Coreferencer(); coref.init();
		} catch(Exception e){ System.out.println("Exception Initialising Coreferencer ");	}
		
		
		
//		{"accepted!","draft pep", "rough draft", "community consensus","initial draft","first draft","second draft","redraft","redrafted","third draft","fourth draft",
//				"fifth draft","final draft","results of the vote","vocal minority","pep: xxx","new pep submission", "pep submission","four versions","request for pronouncement"}; //369
	}

	public void initialiseLibraries() {
		/*
		//initialise libraries
		if (getProcessMining() || getRelationExtraction() || getManualReasonExtraction() || getCandidateReasonSentenceSuggestion()) {	
			if (getRelationExtraction() ) {
				// ClausIE
				cie = new ClausIECaller();
				//ClausIE clausIE;
				//ClausIEMain cm = null;
				//initialise stanford nlptools  in stanfordparser package for coreference, entity extraction, etc
			//	snlp = new StanfordNLPTools();
	//			this.cm = new ClausIEMain();				System.out.println("Initialsied New CIE");
			}
			if (getProcessMining()) {
				//initialise stanford nlptools  in stanfordparser package for coreference, entity extraction, etc
				snlp = new StanfordNLPTools(); System.out.println("Initilaised SNLP");

				//libraries
				this.rvr = null;  //new ReVerbFindRelations();		System.out.println("Initialsied New reverb");
				this.jw = null; //new JavaOllieWrapperGUIInDev();	System.out.println("Initialsied New Ollie");	
				this.cm = new ClausIEMain();				System.out.println("Initialsied New CIE");

				// read subject-verb or vberb-object doubles to match in string matching
				// ReadTextToMatchFromFile dd = new ReadTextToMatchFromFile();
				// String doubleFilename = "C:/scripts/input-DoubleLabels.txt";
				// String doubleLabels[] = dd.readBaseIdeasFromFile(baseIdeasFilename,
				// "Doubles Matching");

				//String v_idea = "", entity = "", action = "", pep = "";
				// read sentences to match in disco statistical
				//	ReadTextToMatchFromFile d = new ReadTextToMatchFromFile();
				//	String sentencesFilename = "C:/Users/psharma/Google Drive/PhDOtago/scripts/inputFiles/pep.small.arff";
				//	sentencesToMatch = d.readBaseIdeasFromFile(sentencesFilename,	"Classification sentences");
				// Classification
				//	MyFilteredClassifier fc = new MyFilteredClassifier();

				// initialise Disco
				UseDISCOSentenceSim ds = null; // new UseDISCOSentenceSim();

				// semantic similarity
				// **SentenceSimilarity ss = new SentenceSimilarity();
				// END TEST

				// Dependency
				// ^^^ StanfordNLPToolGUI snlptdg = new StanfordNLPToolGUI();

				//ClausIEMain cm = new ClausIEMain();

				// ClausIE
				ClausIECaller cie = new ClausIECaller();	System.out.println("Initialised ClausIECaller");
				//ClausIE clausIE;
				//	ClausIEMain cm = null;

				// ReVerb
				//				ReVerbFindRelations rr = null; //new ReVerbFindRelations();
				// Ollie
				//				JavaOllieWrapperGUIInDev jw = null; //new JavaOllieWrapperGUIInDev();

				//initialise the Stanford NLP Tools, like coreferencing
				/*
				SocketClientForNLP scnlp = new SocketClientForNLP();
				try {
					scnlp.init();
				}  catch (Exception e2) {
					// TODO Auto-generated catch block
					System.out.println("Erroe while Initialing Error while Initialising Stanford NLP Socket: ");
					e2.printStackTrace();
				}
				 */	
//			}
//		}
		
	}
	//for term initialisationa dn extraction from message subjecta dn messages
	public ProcessPastMessages getTdm() {		return tdm;	}
	public void setTDM() {		this.tdm = new ProcessPastMessages();	}
	
	public void ReadAllLabels() {
		// TRIPLE LABELS FOR MAIN PROCESSING..
		System.out.println("Reading Labels now");
		ReadLabels b = new ReadLabels();			// read triples labels to match in string matching						 
		//this in coded in ReadLabelsInDatabase- run that first				//scripts
		//String baseIdeasFilename = "C:/Users/psharma/Google Drive/PhDOtago/code/inputFiles/input-Labels.txt";	//input-BaseIdeasSept2017.txt
		//		ArrayList<LabelTriples> labels = new ArrayList<LabelTriples>();		
		//old way below . ...now we must already have the database table for labels populated and just read from it
		//labels = b.readLabelsFromFile(baseIdeasFilename,labels,"Triples Matching",conn, true);	//false as dont output
		String labelsTableName ="labels";
		labels = b.getallLabelsFromTable(labelsTableName,getConn()); //conn

		//Subject array
		//we want to create a list of subjects from the triple array above
		/*	not needed as we just do for 'pep' and ;bdfl'
		PrintWriter out = new PrintWriter("C:/Users/psharma/Google Drive/PhDOtago/scripts/outSubjects.txt");
		List<String> uniqueSubjects = new ArrayList<>();		
		for (Label label : labels){
			String i = label.getSubject();
			if(!uniqueSubjects.contains(i)){
				uniqueSubjects.add(i);
				out.println(i);
			}
		}
		out.close();
		 */

		//add reasons for state change
		//commented because too many false positives
		//tripleLabels = addStateReasonsToTriples(tripleLabels, statesToGetRolesAndReasons,reasonIdentifierTerms);

		//output to test if the doubles are appended
		System.out.println("Triple and Doubles in 'tripleLabels' Array");		
		for (LabelTriples d: labels){
			//				 System.out.println("\t "+d);			
		}

		//do similar for single and double and triple terms
		//check for singles and doubles in sentences and Doubles,Triples for checking in message Subject																																								//pep  245, 275, 332, 335, 340,390, 3139, 3136, 354,410, 416,  437, 
		//singlesAndDoubles = {"accepted!","draft pep", "rough draft", "community consensus","initial draft","first draft","second draft","redraft","redrafted","third draft","fourth draft",
		//		"fifth draft","final draft","results of the vote","vocal minority","pep: xxx","new pep submission", "pep submission","four versions","request for pronouncement"}; //369

		//		ArrayList<SinglesDoublesTriples> singlesDoublesTriplesList = new ArrayList<SinglesDoublesTriples>();
		//jan 2019, doesnt work so i commented..is initialised above somewhere
		//singlesDoublesTriplesList = b.readSinglesDoublesTriplesFromFile(singlesAndDoublesFilename,singlesDoublesTriplesList,"Single, Double and Triple terms",getConn(), true);	//false as dont output

		
		//Reasons
		//add reason identifiers and terms to the array
		//sometimes reasons for a state change may just exists on its own without any triples existing to be matched in the sentence
		//so we add these additional doubles in the array to be checked.		
		// statesToGetRolesAndReasons_reasonIdentifierTerms statesToGetRolesAndReasons reasonIdentifierTerms
		// rejected_because rejected because
		// tripleLabels = addStateReasonsToTriples(tripleLabels, statesToGetRolesAndReasons,reasonIdentifierTerms);

		// read triples labels to match in string matching
		//ReadLabels e = new ReadLabels();
		//String reasonsFilename = "C:/Users/psharma/Google Drive/PhDOtago/Code/inputFiles/input-Reasons.txt";
		//		ArrayList<LabelTriples> reasonLabels = new ArrayList<LabelTriples>();
		//reasonLabels = e.readLabelsFromFile(reasonsFilename,reasonLabels,"Reasons Matching",conn, false);	
		String reasonTableName = "reasonlabels";
		reasonLabels = b.getallLabelsFromTable(reasonTableName,getConn()); //conn);
		//output to test if the doubles are appended
		System.out.println("Reason Triple and Doubles loaded in 'tripleLabels' Array");		
		for (LabelTriples d: reasonLabels){
			//				 System.out.println("\t "+d);			
		}
	}
	
	public void setScriptPurpose(String v_scriptPurpose) { this.scriptPurpose = v_scriptPurpose; }
	public String getScriptPurpose() { return this.scriptPurpose;}
	
	//prp.setOutputfordebug(outputfordebug);
	public void setOutputfordebug(Boolean v_outputfordebug) { this.outputfordebug = v_outputfordebug; }
	public Boolean getOutputfordebug() { return this.outputfordebug;}

	public void initialiseTerms() {
		// dont process sentences if it contains thse terms
//		String dontProcessList[] = { " if " };	
		//Once both triples extracted from IE libraries and those from inputFile are matched, dont process any further if these labels are captured
		//because they wont have anything extra we need and just would take more processing time	
		String dontCheckLabels[] = {""}; //"discussion","poll", "vote","proposal"	
//		Boolean checkMessageSubject = false;	
		// conditionals, negations, questionPhrases, and generalisedList
		// check inside each term in triple, ..maybe only verbs?? Example, the BDFL is willing to accept the pep if there is consensus
		//more like check if sentence starts with these terms	
//		String conditionalList[] = {"if","should","should be","when","can","can be","unless","i hope","whether","once","would","might","why","assuming"};
		//generalised phrases should not be included, eg "In fact any PEP is rejected by default if no consensus is obtained."  "every pep requires acceptance", etc
		//should be checked as the term beofre the subject..or object?
//		String generalisedList[] = {"any","every"};
//		String generalisedSubjectObjectList[] = {"pep","proposal"};
		// checked at start of sentence
		// maybe not only at start, but at triple level cheking is required 
//		String questionPhrases[] = {"if ","should have been","i think","i hope","it will","unless ", "in case"};
		// check inside each verb term in extracted triple from sentence																						//"be",
		//maybe we can extend this to check subject and object so they are checked also
		//Consensus is that we should adopt C3 . ["Consensus"	"is"	"that we should adopt C3"]		
		//make sure we check if these nagation terms occur before the pos
		//need some way to find a source for these negations, some sort of prepared list from the web ..we cannt keep on adding from test set
//		String negationTerms[] = {"if","not","do not","will be","can","can be","is n't","nor","should","should be","would","needs to be","may","will","hav n't","had n't","have n't","might", "n't","whether","zero","no","never","never been","need","would need","before","zero","once"}; // added later for test												// "should","should be"

		//Conditional Lists and Negation Terms are combined in main

		//ROLE, REASONS and STORYLINE EXTRACTION SECTION

		//Four reason related variables
		// reason identifier terms - 'reasonIdentifierTerms'  - eg because
		// reason labels (triples) read in from file -  reasonLabels
		// actual reasons - ActualReasons - eg favourable feedback
		// states To Get Roles And Reasons - statesToGetRolesAndReasons - we only want to get roles and reasons for specific states, eg accepted, rejected, etc

		//standard states changes by users i want to capture 
		//"draft" ,	dont need draft as its always by author
		/*
		static String statesToGetRolesAndReasons[]  = {"bdfl","proposal","open","active", "pending", "close", "final","accept", "defer","replace","reject","postpone", "incomplete" ,  "supersede", 
														"update", "vote", "poll"};//removed "consensus" 			//<-new states changes by user which i want to capture
		static String reasonIdentifierTerms[] = {"because","since", "based on","due to","thanks to","accordingly","as a result of","through"};
		// implementing this will help as not all reasons are triples
		static String ActualReasons[] = {"discussion","debate","bdfl pronouncement","poll result","voting result","vote result","consensus","no consensus","feedback","favorable feedback",
				"support","no popular support","favourable feedback","poor syntax","limited utility","difficulty of implementation","bug magnet","controversy","majority","wrangling"};  
				//lack of an overwhelming majority 

		//find the order of events
		//"the PEP"	"was accepted"	"by Guido + After a short discussion
		static String storyline[] = {"following", "after","later", "afterwards"};	
		// what is the role of the message author in relation to the pep, pep_author, bdfl, bdfl_delegate or standard community member
		//which states to find messageAuthor role
		//get message author roles for this states

		//single liners - these terms should exists on its own and not part of any sentence
		static String singleLineTerms[] = {"BDFL Pronouncement","Rejection Notice","Withdrawal Notice","+Withdrawal","Withdrawal","+PEP Acceptance","+Approval"};//"Pronouncement",
		static String storyElements[] = {"discussion"};				// Story elements	
		 */	
//		String unwantedTerms[] = {"patch","bug"};

		//certain sentences end with questions, which make them conditional. But we want to include the triples extracted from certain questions as they wont be. 
		// For example, ' Does anyone object to me naming myself PEP czar for PEP 314' is a call for volunteering for pep czar which should be allowed
		//Example "So , can you please indicate your vote for or against incorporating PEP 391 into Python ?"
		//allow these labels to be captured if they exist ina  sentence which ends with question mark
//		String allowLabelsAsQuestion[] = {"request_pep_number","call_for_vote","inviting_votes","call_for_poll","volunteer_pep_czar"};
		
		//dec 2018
		setQuestionPhrases(questionPhrases);
	}

	//	public void setCoref(CoRefClient v_cr){
	//		this.cr = v_cr; 
	//	}
	//	
	//	public CoRefClient getCoref(){
	//		return this.cr;
	//	}

	//scripts purpose
	
	public boolean getManualReasonExtraction() {		return manualReasonExtraction;	}
	public String getProposalIdentifier() {		return proposalIdentifier;	}
	public void setProposalIdentifier(String proposalIdentifier) {		this.proposalIdentifier = proposalIdentifier;	}

	public void setManualReasonExtraction(boolean manualReasonExtraction) {		this.manualReasonExtraction = manualReasonExtraction;	}	
	public boolean getCandidateReasonSentenceSuggestion() {		return candidateReasonSentenceSuggestion;	}
	public void setCandidateReasonSentenceSuggestion(boolean candidateReasonSentenceSuggestion) {		this.candidateReasonSentenceSuggestion = candidateReasonSentenceSuggestion;	}
	public void setProcessMining(boolean processMining) {		this.processMining = processMining;	}
	public Boolean getProcessMining() {		return processMining;	}
	public void setRelationExtraction(boolean relationExtraction) {		this.relationExtraction = relationExtraction;	}		
	public Boolean getRelationExtraction() {		return relationExtraction;	}	
	public void setSentenceExtractionForTopicModelling(boolean sentenceExtractionForTopicModelling) {		this.sentenceExtractionForTopicModelling = sentenceExtractionForTopicModelling;	}
	public Boolean getSentenceExtractionForTopicModelling() {		return sentenceExtractionForTopicModelling;	}	
	public void setSentenceParagraphWritingToTable(boolean v_sentenceWritingToTable) {		this.sentenceParagraphWritingToTable = v_sentenceWritingToTable;	}
	public Boolean getSentenceParagraphWritingToTable() {		return sentenceParagraphWritingToTable;	}	
	//april 2019
	public void setCurrentAndNearbySentences(boolean v_currentAndNearbySentences) {		this.currentAndNearbySentences = v_currentAndNearbySentences;	}
	public Boolean getCurrentAndNearbySentences() {		return currentAndNearbySentences;	}	
	
	public Integer getProposal() {		return proposal;	}
	public void setProposal(Integer proposal) {		this.proposal = proposal;	}
	public void setProcessMining(Boolean v_processMining){		this.processMining = v_processMining;	}	

	public ArrayList<Integer> getSkipMessagesForProcessing() {		return skipMessagesForProcessing;	}
	//public void setSkipMessagesForProcessing(ArrayList<Integer> skipMessagesForProcessing) {		this.skipMessagesForProcessing = skipMessagesForProcessing;	}
	
	//selection of messages 
	public boolean isSelectedAll() {		return selectedAll;	}
	public void setSelectedAll(boolean selectedAll) {		this.selectedAll = selectedAll;	}
	public boolean isSelectedMinMax() {		return selectedMinMax;	}
	public void setSelectedMinMax(boolean selectedMinMax) {		this.selectedMinMax = selectedMinMax;	}
	public boolean isSelectedSelected() {		return selectedSelected;	}
	public void setSelectedSelected(boolean selectedSelected) {		this.selectedSelected = selectedSelected;	}
	public boolean isSelectedList() {		return selectedList;	}
	public void setSelectedList(boolean selectedList) {		this.selectedList = selectedList;	}
	public boolean isProcessMessageID() {		return processMessageID;	}
	public void setProcessMessageID(boolean messageID) {		this.processMessageID = messageID;	}
	public boolean isSelectedType() {		return selectedType;	}
	public void setSelectedType(boolean selectedType) {		this.selectedType = selectedType;	}
	public boolean isRerunResults() {		return rerunResults;	}
	public void setRerunResults(boolean rerunResults) {		this.rerunResults = rerunResults;	}
	public Integer getMessageID() {		return messageID;	}
	public void setMessageID(Integer messageID) {		this.messageID = messageID;	}

	public String getScriptsHomeFolderLocation() {		return scriptsHomeFolderLocation;	}
	public void setScriptsHomeFolderLocation(String scriptsHomeFolderLocation) {		this.scriptsHomeFolderLocation = scriptsHomeFolderLocation;	}

	public String getOutputFileNameForTopicModellingSentences() {		return outputFileNameForTopicModellingSentences;	}
	public void setOutputFileNameForTopicModellingSentences(String outputFileNameForTopicModellingSentences) {		this.outputFileNameForTopicModellingSentences = outputFileNameForTopicModellingSentences;	}
	public PrintWriter getPwForTopicModellingSentences() {		return pwForTopicModellingSentences;	}
	public void setPwForTopicModellingSentences(PrintWriter pwForTopicModellingSentences) {		this.pwForTopicModellingSentences = pwForTopicModellingSentences;	}	
	public String getTableToStoreExtractedRelations() {		return tableToStoreExtractedRelations;	}
	public void setTableToStoreExtractedRelations(String tableToStoreExtractedRelations) {		this.tableToStoreExtractedRelations = tableToStoreExtractedRelations;	}		
	public boolean getOutputRelationExtractionDetails() {		return outputRelationExtractionDetails;	}
	public void setOutputRelationExtractionDetails(boolean outputRelationExtractionDetails) {		this.outputRelationExtractionDetails = outputRelationExtractionDetails;	}	
	public void setPerformCoreference(Boolean v_performCoreference){		this.performCoReference = v_performCoreference;	}	
	public Boolean getRestart() 			{		return restart;	}
	public void setRestart(Boolean restart) {		this.restart = restart;	}
	public Integer getPepNumberForRestart() {		return pepNumberForRestart;	}
	public void setPepNumberForRestart(Integer pepNumberForRestart) {		this.pepNumberForRestart = pepNumberForRestart;	}
	public Integer getMessageIDForRestart() {		return messageIDForRestart;	}
	public void setMessageIDForRestart(Integer messageIDForRestart) {		this.messageIDForRestart = messageIDForRestart;	}
	public Boolean getPerformCoreference(){		return this.performCoReference;}	
	public void setCheckMessageSubject(Boolean v_checkMessageSubject){		this.checkMessageSubject = v_checkMessageSubject;	}	
	public Boolean getCheckMessageSubject(){		return this.checkMessageSubject;	}

	public Integer getRunForNumberOfMessagesLimit() {		return runForNumberOfMessagesLimit;	}
	public void setRunForNumberOfMessagesLimit(Integer runForNumberOfMessagesLimit) {		this.runForNumberOfMessagesLimit = runForNumberOfMessagesLimit;	}
	public Integer getProcessedMessageCounter() {		return processedMessageCounter;	}
	public void setProcessedMessageCounter(Integer processedMessageCounter) {		this.processedMessageCounter = processedMessageCounter;	}

	public PepUtils getPu() {		return pu;	}
	public void setPu(PepUtils pu) {		this.pu = pu;	}
	public GetAllMessagesInPep getGpm() {		return gpm;	}
	public void setGpm(GetAllMessagesInPep gpm) {		this.gpm = gpm;	}	
	public String getDummyFileLoc() {		return dummyFileLoc;	}
	public void setDummyFileLoc(String dummyFileLoc) {		this.dummyFileLoc = dummyFileLoc;	}
	public boolean isReadEntireFileAsMessage() {		return readEntireFileAsMessage;	}
	public void setReadEntireFileAsMessage(boolean readEntireFileAsMessage) {		this.readEntireFileAsMessage = readEntireFileAsMessage;	}
	public ProcessDummyFile getPdf() {		return pdf;	}
	public void setPdf(ProcessDummyFile pdf) {		this.pdf = pdf;	}

	//pep types	
	public boolean isAllStandard() {		return allStandard;	}
	public void setAllStandard(boolean allStandard) {		this.allStandard = allStandard;	}
	public boolean isAllProcess() {		return allProcess;	}
	public void setAllProcess(boolean allProcess) {		this.allProcess = allProcess;	}
	public boolean isAllInformational() {		return allInformational;	}
	public void setAllInformational(boolean allInformational) {		this.allInformational = allInformational;	}

	//	public void setSocketClientNLP(SocketClientForNLP v_scnlp){
	//		this.scnlp = v_scnlp; 
	//	}
	////	
	//	public SocketClientForNLP getSCNLP(){
	//		return this.scnlp;
	//	}
	public void setEnglishOrCode(EnglishOrCode ec) {		this.ec = ec;	}
	public EnglishOrCode getEnglishOrCode() {		return ec;	}
	public void setCoreferenceSubjects(String[] v_coreferenceSubjects){		this.coreferenceSubjects = v_coreferenceSubjects;	}	
	public String[] getCoreferenceSubjects(){		return this.coreferenceSubjects;	}	
	public SplitMessageByUniqueIdentifier getSMUI(){		return this.smui;	}	
	public PythonSpecificMessageProcessing getPsmp() {		return psmp;	}
	public void setPsmp(PythonSpecificMessageProcessing psmp) {		this.psmp = psmp;	}

	public Coreferencer getCoref() {		return coref;	}
	public void setCoref(Coreferencer coref) {		this.coref = coref;	}

	//start special setter functions
	public void setPEPNumber(Integer i){		this.pep = i;	}	
	public Integer getPEPNumber(){		return this.pep;	}	
	public void setConnections(MysqlConnect v_mc,Connection v_conn){		this.mc = v_mc;		this.conn = v_conn;	}	
	public void setLibraries(ClausIECaller cie, ClausIEMain cm, ReVerbFindRelations rr, JavaOllieWrapperGUIInDev jw, UseDISCOSentenceSim ds){
		this.rvr = rr;		this.jw = jw;		this.cm = cm;		this.ds = ds;		
	}

	public void setStanfordNLP(StanfordNLPTools snlp) {		this.snlp = snlp;	}	
	public StanfordNLPTools getStanfordNLP() {		return this.snlp;	}	
	public void setLabels(ArrayList<LabelTriples> v_labels, ArrayList<LabelTriples> v_reasonLabels, String[] v_ActualReasons, String[] v_dontCheckLabels){	//set labels (triples, doubles) for reasons as well
		this.labels = v_labels;		this.reasonLabels = v_reasonLabels;		this.ActualReasons= v_ActualReasons;		this.dontCheckLabels = v_dontCheckLabels;
	}
	public void setWhichLibrariesToCheck(boolean v_checkReverb,boolean v_checkClauseIETrue, boolean v_checkOllieTrue,boolean v_includeStateData,boolean v_replaceCommas){
		this.checkReverbTrue = v_checkReverb;		this.checkClauseIETrue = v_checkClauseIETrue;		this.checkOllieTrue = v_checkOllieTrue;		this.includeStateData= v_includeStateData;
		this.replaceCommas=v_replaceCommas;
	}
	public void setTermLists(ArrayList<SinglesDoublesTriples> v_singlesDoublesTriplesList,List<String> v_wordsList,String[] v_conditionalList, String[] v_negationTerms,String[] v_reasonIdentifierTerms, String[] v_questionPhrases,
			String[] v_unwantedTerms,String[] v_isolatedTerms, List<String> v_candidateSentencesList,String[] allowLabelsAsQuestion){
		/*this.singlesAndDoubles=v_singlesDoublesTriplesList;	*/	this.wordsList=v_wordsList;		this.conditionalList= v_conditionalList;
		this.negationTerms= v_negationTerms;		this.reasonIdentifierTerms=v_reasonIdentifierTerms;		this.questionPhrases = v_questionPhrases;
		this.unwantedTerms = v_unwantedTerms;		this.isolatedTerms = v_isolatedTerms;		this.candidateSentencesList = v_candidateSentencesList;
		this.allowLabelsAsQuestion = allowLabelsAsQuestion;
	}
	public void setResulstWritingToFile(FileWriter v_writerForDisco,FileWriter v_writerAll){
		this.writerForDisco = v_writerForDisco;		this.writerAll = v_writerAll;		
	}
	public void setUtils(PythonSpecificMessageProcessing psmp,ProcessMessageAndSentence pms,GetProposalDetailsWebPage pd,GetAllSentencesInMessage casm,CheckSentenceHasTripleTerms_SendToIELibraries_InsertIntoDB cesdl,
			ArrayList<TripleProcessingResult> tripleProcessingResult)	//initialise these class object once and pass rather than creating again and again
	{
		this.psmp = psmp;		this.pms = pms;		this.pd = pd;		this.casm = casm;		this.cesdl = cesdl;		this.tripleProcessingResult = tripleProcessingResult;
	}

	public void clearProcessingLists(){
		if(tripleProcessingResult == null)	{}		
			else	tripleProcessingResult.clear();
		if(statusProcessingResult== null) {}			
			else	statusProcessingResult.clear();
	}
	public String[] getActualReasons() {		return ActualReasons;	}
	public void setActualReasons(String[] actualReasons) {		ActualReasons = actualReasons;	}
	public String getNLP_MODELS_PATH() {		return NLP_MODELS_PATH;	}
	public void setNLP_MODELS_PATH(String nLP_MODELS_PATH) {		NLP_MODELS_PATH = nLP_MODELS_PATH;	}
	public ArrayList<LabelTriples> getLabels() {		return labels;	}
	public void setLabels(ArrayList<LabelTriples> labels) {		this.labels = labels;	}
	public void setIncludeStateData(boolean includeStateData) {		this.includeStateData = includeStateData;	}	
	public boolean getIncludeStateData() {		return includeStateData;	}
	public void setGetincludeStateData(boolean v_includeStateData) {		this.includeStateData = v_includeStateData;	}
	public String[] getStatesToGetRoles() {		return statesToGetRoles;	}
	public void setStatesToGetRoles(String[] statesToGetRoles) {		this.statesToGetRoles = statesToGetRoles;	}
	public String[] getUnwantedTerms() {		return unwantedTerms;	}

	public void setUnwantedTerms(String[] unwantedTerms) {		this.unwantedTerms = unwantedTerms;	}
	public String[] getAllowLabelsAsQuestion() {		return allowLabelsAsQuestion;	}
	public void setAllowLabelsAsQuestion(String[] allowLabelsAsQuestion) {		this.allowLabelsAsQuestion = allowLabelsAsQuestion;	}
	public ArrayList<SinglesDoublesTriples> getSinglesAndDoubles() {		return singlesAndDoubles;	}
	public void setSinglesAndDoubles(ArrayList<SinglesDoublesTriples> singlesAndDoubles) {		this.singlesAndDoubles = singlesAndDoubles;	}
	public List<String> getWordsList() {		return wordsList;	}
	public void setWordsList(List<String> wordsList) {		this.wordsList = wordsList;	}
	public String[] getReasonIdentifierTerms() {		return reasonIdentifierTerms;	}
	public void setReasonIdentifierTerms(String[] reasonIdentifierTerms) {		this.reasonIdentifierTerms = reasonIdentifierTerms;	}
	public String[] getIsolatedTerms() {		return isolatedTerms;	}
	public void setIsolatedTerms(String[] isolatedTerms) {		this.isolatedTerms = isolatedTerms;	}
	public List<String> getCandidateSentencesList() {		return candidateSentencesList;	}
	public void setCandidateSentencesList(List<String> candidateSentencesList) {		this.candidateSentencesList = candidateSentencesList;	}
	public String[] getStatesToGetRolesAndReasons() {		return statesToGetRolesAndReasons;	}
	public void setStatesToGetRolesAndReasons(String[] statesToGetRolesAndReasons) {		this.statesToGetRolesAndReasons = statesToGetRolesAndReasons;	}
	public CheckSentenceHasTripleTerms_SendToIELibraries_InsertIntoDB getCesdl() {		return cesdl;	}
	public void setCesdl(CheckSentenceHasTripleTerms_SendToIELibraries_InsertIntoDB cesdl) {		this.cesdl = cesdl;	}
	public void setLibraryModelLocation(String v_NLP_MODELS_PATH){		NLP_MODELS_PATH=v_NLP_MODELS_PATH;	}
	//these are independent parameters for using this class
	public void setReadDummyFile(boolean readDummyFile){		this.readDummyFile = readDummyFile;			}
	public void setNotSure(ArrayList<String> statusProcessingResult){

	}
	//end special setter functions
	
	public Integer getPep() {		return pep;	}
	public boolean getCheckReasons() {		return checkReasons;	}
	public void setCheckReasons(boolean checkReasons) {		this.checkReasons = checkReasons;	}
	public void setPep(Integer pep) {		this.pep = pep;	}
	public MysqlConnect getMc() {		return mc;	}
	public void setMc(MysqlConnect mc) {	this.mc = mc;	}
	public Connection getConn() {	return conn;	}
	public void setConn(Connection conn) {		this.conn = conn;	}
	public void DBdisconnect() { mc.disconnect();  }
	public DeMAP_Miner_Process getPm() {		return pm;	}
	public void setPm(DeMAP_Miner_Process pm) {		this.pm = pm;	}
	public GetAllSentencesInMessage getCasm() {		return casm;	}
	public void setCasm(GetAllSentencesInMessage casm) {		this.casm = casm;	}
	public ReVerbFindRelations getRvr() {		return rvr;	}
	public void setRvr(ReVerbFindRelations rvr) {		this.rvr = rvr;	}
	public JavaOllieWrapperGUIInDev getJw() {		return jw;	}
	public void setJw(JavaOllieWrapperGUIInDev jw) {		this.jw = jw;	}
	public ClausIEMain getCm() {		return cm;	}
	public void setCm(ClausIEMain cm) {		this.cm = cm;	}
	public UseDISCOSentenceSim getDs() {		return ds;	}
	public void setDs(UseDISCOSentenceSim ds) {		this.ds = ds;	}
	public boolean isReadDummyFile() {		return readDummyFile;	}
	//	public void setReadDummyFile(boolean readDummyFile) {
	//		this.readDummyFile = readDummyFile;
	//	}
	public boolean isReplaceCommas() {		return replaceCommas;	}
	public void setReplaceCommas(boolean replaceCommas) {		this.replaceCommas = replaceCommas;	}
	public boolean isCheckClauseIETrue() {		return checkClauseIETrue;	}
	public void setCheckClauseIETrue(boolean checkClauseIETrue) {		this.checkClauseIETrue = checkClauseIETrue;	}
	public boolean isCheckOllieTrue() {		return checkOllieTrue;	}
	public void setCheckOllieTrue(boolean checkOllieTrue) {		this.checkOllieTrue = checkOllieTrue;}
	public boolean isCheckReverbTrue() {		return checkReverbTrue;	}
	public void setCheckReverbTrue(boolean checkReverbTrue) {		this.checkReverbTrue = checkReverbTrue;	}
	public boolean isDependencyTrue() {		return dependencyTrue;	}
	public void setDependencyTrue(boolean dependencyTrue) {		this.dependencyTrue = dependencyTrue;	}
	public FileWriter getWriterAll() {		return writerAll;	}
	public void setWriterAll(FileWriter writerAll) {		this.writerAll = writerAll;	}
	public FileWriter getWriterForDisco() {		return writerForDisco;	}
	public void setWriterForDisco(FileWriter writerForDisco) {		this.writerForDisco = writerForDisco;	}
	public String[] getConditionalList() {		return conditionalList;	}
	public void setConditionalList(String[] conditionalList) {		this.conditionalList = conditionalList;	}
	public String[] getNegationTerms() {		return negationTerms;	}
	public void setNegationTerms(String[] negationTerms) {		this.negationTerms = negationTerms; }
	public String[] getReasonTerms() {		return reasonTerms;	}
	public void setReasonTerms(String[] reasonTerms) {		this.reasonTerms = reasonTerms;	}

	//	public String[] getReasons() {
	//		return reasons;
	//	}
	//	public void setReasons(String[] reasons) {
	//		this.reasons = reasons;
	//	}
	public String[] getQuestionPhrases() {		return questionPhrases;	}
	public void setQuestionPhrases(String[] questionPhrases) {		this.questionPhrases = questionPhrases;	}
	public String[] getV_statesToGetRoles() {		return statesToGetRoles;	}
	public void setV_statesToGetRoles(String[] v_statesToGetRoles) {		this.statesToGetRoles = v_statesToGetRoles;	}
	public String[] getV_unwantedTerms() {		return unwantedTerms;	}
	public void setV_unwantedTerms(String[] v_unwantedTerms) {		this.unwantedTerms = v_unwantedTerms;	}
	public String[] getDontCheckLabels() {		return dontCheckLabels;	}
	public void setDontCheckLabels(String[] dontCheckLabels) {		this.dontCheckLabels = dontCheckLabels;	}
	public void setV_allowLabelsAsQuestion(String[] v_allowLabelsAsQuestion) {		this.allowLabelsAsQuestion = v_allowLabelsAsQuestion;	}
	public ProcessMessageAndSentence getPms() {		return pms;	}
	public void setPms(ProcessMessageAndSentence pms) {		this.pms = pms;	}
	public ArrayList<TripleProcessingResult> getTripleProcessingResult() {		return tripleProcessingResult;	}
	public void setTripleProcessingResult(ArrayList<TripleProcessingResult> tripleProcessingResult) {	this.tripleProcessingResult = tripleProcessingResult;	}
	public ArrayList<String> getStatusProcessingResult() {		return statusProcessingResult;	}
	public void setStatusProcessingResult(ArrayList<String> statusProcessingResult) {		this.statusProcessingResult = statusProcessingResult;	}
	public List<String> getSentencesRemoveDuplicates() {		return sentencesRemoveDuplicates;	}
	public void setSentencesRemoveDuplicates(List<String> sentencesRemoveDuplicates) {		this.sentencesRemoveDuplicates = sentencesRemoveDuplicates;	}
	public ArrayList<LabelTriples> getReasonLabels() {		return reasonLabels;	}
	public void setReasonLabels(ArrayList<LabelTriples> reasonLabels) {		this.reasonLabels = reasonLabels;		}
	public GetProposalDetailsWebPage getPd() {		return pd;	}
	public void setPd(GetProposalDetailsWebPage pd) {		this.pd = pd;	}
	public void setEliminateMessage(UpdateAllMessages_EliminateMessagesNotBelongingToCurrentProposal v_emnbcp ) { this.emnbcp = v_emnbcp; }
	public UpdateAllMessages_EliminateMessagesNotBelongingToCurrentProposal getElimtaeMessages() { return this.emnbcp; }	
	public Integer getLinesToAnalyse() {		return linesToProcess;	}
	public void setLinesToAnalyse(Integer ltp) {		this.linesToProcess = ltp;	}

	public String[] getDontProcessList() {		return dontProcessList;	}
	public void setDontProcessList(String[] dontProcessList) {		this.dontProcessList = dontProcessList;	}
	public String[] getGeneralisedList() {		return generalisedList;	}
	public void setGeneralisedList(String[] generalisedList) {		this.generalisedList = generalisedList;	}
	//jan 2019	
	public String[] getGeneralisedSubjectObjectList() {		return generalisedSubjectObjectList;	}
	public String[] getAntiGnerelisedList() {		return antiGnerelisedList;	}
	public void setAntiGnerelisedList(String[] antiGnerelisedList) {		this.antiGnerelisedList = antiGnerelisedList;	}
	public String[] getDontCheckNegationForLabels() {		return dontCheckNegationForLabels;	}
	public void setDontCheckNegationForLabels(String[] dontCheckNegationForLabels) {		this.dontCheckNegationForLabels = dontCheckNegationForLabels;	}
	
	public void setGeneralisedSubjectObjectList(String[] generalisedSubjectObjectList) {		this.generalisedSubjectObjectList = generalisedSubjectObjectList;	}
	public SentenceSimplification getSentenceSimplification() {		return ss;	}
	
	//feb 2019
	public boolean isReasonExtraction() {		return reasonExtraction;	}
	public void setReasonExtraction(boolean reasonExtraction) {		this.reasonExtraction = reasonExtraction;	}
	
	public static String StackTraceToString(Exception ex) {
		String result = ex.toString() + "\n";
		StackTraceElement[] trace = ex.getStackTrace();
		for (int i=0;i<trace.length;i++) {
			result += trace[i].toString() + "\n";
		}
		return result;
	}

	public static String[] listToArray(List<String> reasonsList) {
		String[] Array = new String[reasonsList.size()];		Array = reasonsList.toArray(Array); //convert list to array
		return Array;
	}

	public String getProposalTableName() {		return proposalTableName;	}
	public void setProposalTableName(String v_proposalTableName) {	this.proposalTableName = v_proposalTableName;	}
	
	public String getPEPStatesTableName() {		return PEPStatesTableName;	}
	public void setPEPStatesTableName(String v_PEPStatesTableName) {	this.PEPStatesTableName = v_PEPStatesTableName;	}
	
	public Boolean getRestartAfterEachProposal() 			{		return restartAfterEachProposal;	}
	public void setRestartAfterEachProposal(Boolean v_restartAfterEachProposal) {		this.restartAfterEachProposal = v_restartAfterEachProposal;	}
	
	public InsertMessageTracking getInsertMessageTracking() 			{		return imt;	}
	
}
