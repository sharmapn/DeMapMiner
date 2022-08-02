package miner;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import ManualAnalysis.StatesAndReasons.reasonRow;
//import causalRelExtractor.create_explicit_corpus;
import de.mpii.clause.driver.ClausIEMain;
/*
import edu.smu.tspell.wordnet.SynsetType;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.process.DocumentPreprocessor;
*/
import javaStringSimilarity.info.debatty.java.stringsimilarity.Levenshtein;
import wordnet.jaws.WordnetSynonyms;
import GUI.helpers.GUIHelper;
import miner.AutomaticRationaleExtraction.StringPair;
import miner.process.ProcessMessageAndSentence;
import miner.process.ProcessingRequiredParameters;
import miner.process.PythonSpecificMessageProcessing;
import miner.processLabels.TripleProcessingResult;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.util.Span;
import rankingEvaluation.ScoresToSentences;
import stanfordParser.StanfordLemmatizer;
import utilities.ParagraphSentence;
import utilities.StateAndReasonLabels;
import wordforms.GetWordForms;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;


//This class is called by some other class, but the main machine learning count is computed and written from here to db
public class ProcessEachMessage {
	
	static ProcessingRequiredParameters prp;
	static ParagraphSentence ps;
	static PythonSpecificMessageProcessing pm;
	static ProcessMessageAndSentence pms;
	
	static String tableNameToStore = "autoExtractedReasonCandidateSentences"; 
	static String tableNameToStoreMessage = "autoExtractedReasonCandidateMessages"; 
	static Connection connection;
	//too many parameters of lists beings passed between too many functions so better to declare it here as class level variable 
	static List<String>  verbs  = new ArrayList<String>();
	static List<String>  committedStates  = new ArrayList<String>();
	static List<String>  subStates  = new ArrayList<String>();
	static ArrayList<ArrayList<String>>  allStatesList_StatesSubstates  = new ArrayList<ArrayList<String>>();
	static ArrayList<ArrayList<String>>  temp_allStatesList_StatesSubstates  = new ArrayList<ArrayList<String>>();
	static ArrayList<ArrayList<String>>  labelList  = new ArrayList<ArrayList<String>>();	//temp list will hold only terms related to the current label, the reasons of which are being looked for
	static List<String>  subStatesList  = new ArrayList<String>();
	static ArrayList<ArrayList<String>>  reasonsList  = new ArrayList<ArrayList<String>>();
	static List<String>  reasonIdentifierTerms  = new ArrayList<String>(); 
	static List<String>  proposalIdentifiers  = new ArrayList<String>(); 
	static List<String>  entitiesList = new ArrayList<String>(); 
	static ArrayList<ArrayList<String>>  specialOSCommunitySpecificTerms = new ArrayList<ArrayList<String>>();
	static List<String>  negationTerms = new ArrayList<String>(); 
	static List<String>  conditionalTerms = new ArrayList<String>();
	static List<String>  positiveWords = new ArrayList<String>(); 
	static List<String>  negativeWords = new ArrayList<String>();
	static List<String>  notCoOccurTerms = new ArrayList<String>(); //dec 2018
	static ArrayList<ArrayList<String>>  decisionTerms = new ArrayList<ArrayList<String>>();
	
	static GetWordForms wf = new GetWordForms();
	static WordnetSynonyms ws = new WordnetSynonyms();
	static GUIHelper guih = new GUIHelper(); 
	
	static InputStream inputStream;
	static SentenceModel model;
	static SentenceDetectorME detector;
	
	static boolean outputForDebug = false, outputList=false;
	
	//MAR 2019, we try to capture BDFL/Delegate pronouncement messages
	//static String acceptRejectTerms[] = {"accept","accepting","accepted","add","approve","approving","reject","rejecting","rejected"}; 
	boolean  matchWholeTerms=true;
	
	public static void initialiseTermLists(WordnetSynonyms wn) {
		try {
			inputStream = new FileInputStream("C://lib//openNLP//en-sent.bin"); 
		    model = new SentenceModel(inputStream); 	    									
			detector = new SentenceDetectorME(model);  //Instantiating the SentenceDetectorME class
		}
		catch(Exception e){
			
		}
		
		wf.initialise();
		ws.init();
		
		prp = new ProcessingRequiredParameters();		ps  = new ParagraphSentence();		pm  = new PythonSpecificMessageProcessing();		pms = new ProcessMessageAndSentence();		
		StateAndReasonLabels l = new StateAndReasonLabels();
		boolean output=true;
		l.initStateAndReasonLabels(wn, output);
		verbs = l.getVerbs(); 	//ready for review/pronouncement
		committedStates = l.getCommittedStates();	//main state changes  e.g. 'Status: Accepted'
		if(outputList) {
			System.out.print("FINAL committedStates >> ");  for (String line : committedStates)     {                	System.out.print(line+ " ");            }		  System.out.println(" ");  
		}
		allStatesList_StatesSubstates = l.getAllStatesList_StatesSubstates();	
		if(outputList) {
			System.out.print("FINAL getAllStatesList_StatesSubstates >> ");
		}
		Integer countA=0;
		if(outputList) { 
			for (ArrayList<String> line : allStatesList_StatesSubstates) {   //for each list as line 
	    		for (String term : line) {	//for each term in line
	    				System.out.print(term+ ",");	
	            	countA++; if (countA%10==0) {	System.out.println();	System.out.print("\t"); }
	    		}	//System.out.println(); 
	    	}		//  System.out.println(" ");  
		}
		reasonsList = l.getReasonTerms(); 
		if(outputList) {
			System.out.print("\nFINAL reasonsList >> ");  
		}
		Integer count=0;
		if(outputList) {
			for (ArrayList<String> line : reasonsList) { 
				for (String f : line) { 
						System.out.print(f+ ",");
					count++; if (count%10==0) {	System.out.println();	System.out.print("\t");	}
				}		 // System.out.println(); 			
			}
		}
		    // Looking at the outputs from below, we can use Word forms, NOUN Synonyms, VERB synonyms, and DerivationallyForm, BUT NOT Verb Hyponyms
		    // For states we only use wordforms and deriavationallyform
			// now we add the different wordforms and wordnet word forms
			/* for (String r : reasonsList) {
				// all word forms
				Set reasonListWordForms = wf.getAllWordForms(r);	
				// NOUN Synonyms
				Set<String> synonyms = ws.getSynonym(r, SynsetType.NOUN); //region 
				System.out.println("\tNOUN Synonyms for word: " + r); System.out.print("\t");	for (String s : synonyms) {  System.out.print(s + ", ");	} System.out.println();				
				// VERB synonyms
				Set<String> synonymsVERB = ws.getSynonym(r, SynsetType.VERB); //region 
				System.out.println("\tVERB Synonyms for word: " + r); System.out.print("\t");	for (String s : synonymsVERB) {  System.out.print(s + ", ");	} System.out.println();
				// DerivationallyForm 
				Set<String> derivationallyForm = ws.getDerivationallyRelatedForms(r, SynsetType.VERB); //develop
				System.out.println("\tDerivationallyForm for word: " + r);	System.out.print("\t");	for (String s : derivationallyForm) {  System.out.print(s + ", ");	}	System.out.println();				
			} */
			
			// Set<String> finalReasonSet = new HashSet<String>();
		    // Looking at the outputs from below, we can use Word forms, NOUN Synonyms, VERB synonyms, and DerivationallyForm, BUT NOT Verb Hyponyms
		    // For states we only use wordforms and deriavationallyform
			// now we add the different wordforms and wordnet word forms
			/* for (String r : reasonsList) {
				// all word forms
				System.out.println(r);
				Set reasonListWordForms = wf.getAllWordForms(r);
				finalReasonSet.add(r);		//add current term
				finalReasonSet.addAll(reasonListWordForms);		//add word forms of that term
				// NOUN Synonyms 
				Set<String> synonyms = ws.getSynonym(r, SynsetType.NOUN); //region 
				System.out.print("\tNOUN Synonyms for word: " + r + " = "); for (String s : synonyms) {  System.out.print(s + ", ");	} System.out.println();		 finalReasonSet.addAll(synonyms);			
				// VERB synonyms
				Set<String> synonymsVERB = ws.getSynonym(r, SynsetType.VERB); //region 
				System.out.print("\tVERB Synonyms for word: " + r + " = "); for (String s : synonymsVERB) {  System.out.print(s + ", ");	} System.out.println();  finalReasonSet.addAll(synonymsVERB);	
				// DerivationallyForm 
				Set<String> derivationallyForm = ws.getDerivationallyRelatedForms(r, SynsetType.VERB); //develop
				System.out.print("\tDerivationallyForm for word: " + r + " = ");		for (String s : derivationallyForm) {  System.out.print(s + ", ");	}	System.out.println(); finalReasonSet.addAll(derivationallyForm);					
			}
			
			//convert to list
		    List<String> allReasonListWordForms_List = new ArrayList<String>();
		    allReasonListWordForms_List.addAll(finalReasonSet);
		    reasonsList = allReasonListWordForms_List;  //assign it to the main variable
		    System.out.print("FINAL WORDFORMS reasonsList >> ");  for (String line : reasonsList)     {                	System.out.print(line+ ", ");            }		  System.out.println(" ");
			*/
		reasonIdentifierTerms = l.getReasonIdentifierTerms(); 
			//now we add the different wordforms and wordnet word forms		
		if(outputList) {
			System.out.print("FINAL reasonIdentifierTerms >> ");  for (String line : reasonIdentifierTerms)     {                	System.out.print(line+ ", ");            }		  System.out.println(" ");  
		}
		proposalIdentifiers = l.getProposalIdentifierTerms();
		if(outputList) {
			System.out.print("FINAL proposalIdentifiers >> ");  for (String line : proposalIdentifiers)     {                	System.out.print(line+ ", ");            }		  System.out.println(" ");
		}
		specialOSCommunitySpecificTerms = l.getSpecialOSCommunitySpecificTerms(); 
		entitiesList = l.getEntities();
		if(outputList) {
			System.out.print("FINAL specialOSCommunitySpecificTerms >> ");  
		}
		Integer countC=0;
		if(outputList) {
			for (ArrayList<String> line : specialOSCommunitySpecificTerms) {    
				for (String f : line) { 
						System.out.print(f+ ",");
					countC++; if (countC%10==0) {	System.out.println(); 	System.out.print("\t");	}
				}			  //System.out.println();
			}
		}
		subStates = l.getSubStates();
		if(outputList) {
			System.out.print("FINAL entitiesList >> ");  for (String line : entitiesList)     {                	System.out.print(line+ ", ");            }		  System.out.println(", ");
		}
		if(outputList) {
			System.out.print("FINAL subStates >> ");  for (String line : subStates)     {                	System.out.print(line+ ", ");            }		  System.out.println(", ");
		}
		negationTerms = l.getNegationTerms();
		if(outputList) {
			System.out.print("FINAL negationTerms >> ");  for (String line : negationTerms)     {                	System.out.print(line+ ", ");            }		  System.out.println(", ");
		}
		conditionalTerms= l.getConditionalTerms();
		if(outputList) {
			System.out.print("FINAL conditionalTerms >> ");  for (String line : conditionalTerms)     {                	System.out.print(line+ ", ");            }		  System.out.println(", ");
		}
		decisionTerms= l.getDecisionTerms();
		if(outputList) {
			System.out.print("FINAL deisionTerms >> "); 
		}
		Integer countD=0;
		if(outputList) {
			for (ArrayList<String> line : decisionTerms) {    
				for (String f : line) {
						System.out.print(f+ ",");
					countD++; if (countD%10==0) {	System.out.println(); 	System.out.print("\t");	}
				}			  //System.out.println();
			}
		}
				// For states we only use wordforms and deriavationallyform
				// now we add the different wordforms and wordnet word forms
				/*
				Set<String> finalDecisionSet = new HashSet<String>();
				for (String r : decisionTerms) {
					// all word forms
					System.out.println(r);
					Set decisionListWordForms = wf.getAllWordForms(r);
					finalDecisionSet.add(r);		//add current term
					finalDecisionSet.addAll(decisionListWordForms);
					// NOUN Synonyms
					Set<String> synonyms = ws.getSynonym(r, SynsetType.NOUN); //region 
					System.out.print("\tNOUN Synonyms for word: " + r + " = "); 	for (String s : synonyms) {  System.out.print(s + ", ");	} System.out.println();		finalDecisionSet.addAll(synonyms);			
					// VERB synonyms
					Set<String> synonymsVERB = ws.getSynonym(r, SynsetType.VERB); //region 
					System.out.print("\tVERB Synonyms for word: " + r + " = "); 	for (String s : synonymsVERB) {  System.out.print(s + ", ");	} System.out.println(); finalDecisionSet.addAll(synonymsVERB);
					// DerivationallyForm 
					Set<String> derivationallyForm = ws.getDerivationallyRelatedForms(r, SynsetType.VERB); //develop
					System.out.print("\tDerivationallyForm for word: " + r + " = ");	for (String s : derivationallyForm) {  System.out.print(s + ", ");	}	System.out.println();	 finalDecisionSet.addAll(derivationallyForm);			
				}
				//convert to list
			    List<String> allDecisionWordForms_List = new ArrayList<String>();
			    allDecisionWordForms_List.addAll(finalDecisionSet);
			    decisionTerms = allDecisionWordForms_List;		//assign it to the main variable
				//statesList = ObjectArrays.concat(statesList, decisionMechanismsSubStates, String.class); //we will combine statesList and decisionMechanismsSubStates
				//statesList.addAll(decisionMechanismsSubStates);
				//specialTerms = ObjectArrays.concat(specialTerms, entities, String.class);	//special terms and entities are combined
				//specialTerms.addAll(entitiesList);
			    System.out.print("FINAL WORDFORMS deisionTerms >> ");  for (String line : decisionTerms)     {                	System.out.print(line+ ", ");            }		  System.out.println(", ");
				*/
		//statesList = ObjectArrays.concat(statesList, decisionMechanismsSubStates, String.class); //we will combine statesList and decisionMechanismsSubStates
		//statesList.addAll(decisionMechanismsSubStates);
		//specialTerms = ObjectArrays.concat(specialTerms, entities, String.class);	//special terms and entities are combined
		//specialTerms.addAll(entitiesList);
		positiveWords = l.getPositiveWords();
		negativeWords = l.getNegativeWords();
		//dec 2018 terms that should not cooccur as they have double meaning
		notCoOccurTerms = l.getNotCoOccurTerms();
	}
	
	//this function instead of the above, includes the synonyms and wordforms
	public static void initialiseTermListsAndWordForms(WordnetSynonyms wn) {
		wf.initialise();	ws.init();	
		//StanfordLemmatizer sl = new StanfordLemmatizer();  //we need lemmatizer		
		StateAndReasonLabels srl = new StateAndReasonLabels();
		srl.initStateAndReasonLabels(ws, true);
		
		verbs = srl.getVerbs(); 	//ready for review/pronouncement
		committedStates = srl.getCommittedStates();	//main state changes  e.g. 'Status: Accepted'
		System.out.print("\nFINAL committedStates >> ");  for (String line : committedStates)     {                	System.out.print(line+ ", ");            }		  System.out.println(" ");  
		//States and substates
		allStatesList_StatesSubstates = srl.getAllStatesList_StatesSubstates();	
			System.out.print("\nFINAL getAllStatesList_StatesSubstates >> ");
			for (ArrayList<String> line : allStatesList_StatesSubstates) {   //for each list as line 
	    		for (String term : line) {	//for each term in line
	            	System.out.print(term+ ", "); // ("+ sl.lemmatize(term) +") ");        		
	    		}	System.out.println(); 
	    	}	//	  System.out.println(" "); 
	/*		Set<String> finalStateSet = new HashSet<String>();
			 // now we add the different wordforms and wordnet word forms // For states we only use wordforms and deriavationallyform
			 // since the terms make a lot of different in capturing exact events, we dont look for noun and verb synonyms...they are not synonyms for this set anyway
			 // DerivationallyForm is useful but we compute it on the stemmed word form 
			for (ArrayList<String> ss : allStatesList_StatesSubstates) { 
				String t = ss.get(0);	//get first element from arraylist
				String r = sl.lemmatize(t).get(0);   //get lemmatised form, mostly first element from lemmatised list should be only and main one 
				// all word forms
				System.out.println(r);
				Set stateListWordForms = wf.getAllWordForms(r);	
				finalStateSet.add(t);		//add current term
				finalStateSet.addAll(stateListWordForms);		//add word forms of that term
				// NOUN Synonyms
//				Set<String> synonyms = ws.getSynonym(r, SynsetType.NOUN); finalStateSet.addAll(synonyms);   //region 
//				System.out.print("\tNOUN Synonyms for word: " + r + " = "); 	for (String s : synonyms) {  System.out.print(s + ", ");	} System.out.println();				
				// VERB synonyms
//				Set<String> synonymsVERB = ws.getSynonym(r, SynsetType.VERB); finalStateSet.addAll(synonymsVERB);  //region 
//				System.out.print("\tVERB Synonyms for word: " + r + " = ");  for (String s : synonymsVERB) {  System.out.print(s + ", ");	} System.out.println();
				// DerivationallyForm 
				Set<String> derivationallyForm = ws.getDerivationallyRelatedForms(r, SynsetType.VERB); 	finalStateSet.addAll(derivationallyForm); //develop
				System.out.print("\n\tDerivationallyForm for word: " + r + " = ");	for (String s : derivationallyForm) {  System.out.print(s + ", ");	}	System.out.println();
				//create a new list of lists and add to it
				ArrayList<String> temp = new ArrayList<String>();
				temp.addAll(finalStateSet); //add set to list
				temp_allStatesList_StatesSubstates.add(temp);	//add list to list of lists
			} 
			System.out.print("\nallStatesList_StatesSubstates Set >> "); 
		    for (String f : finalStateSet)	{
		    	System.out.print( f + ", " );
		    } 
		    allStatesList_StatesSubstates = temp_allStatesList_StatesSubstates;  //replace the list with this one with word forms and wordnet synonyms
		    System.out.print("\nREPLACED FINAL getAllStatesList_StatesSubstates >> ");
			for (ArrayList<String> line : allStatesList_StatesSubstates) {   //for each list as line 
	    		for (String term : line) {	//for each term in line
	            	System.out.print("'"+term+ "', "); // ("+ sl.lemmatize(term) +") ");        		
	    		}	System.out.println(); 
	    	}
	*/
		    //convert to list
		    /* ArrayList<String> allStatesList_StatesSubstates_List = new ArrayList<String>();
		    allStatesList_StatesSubstates_List.addAll(finalStateSet);
		    allStatesList_StatesSubstates = null; 
		    try {
			    for(String f: allStatesList_StatesSubstates_List) {  //assign it to main variable
			    	 ArrayList<String> t = new ArrayList<String>();
			    	 t.add(f);
			    	allStatesList_StatesSubstates.add(t);
			    }
		    }
		    catch (Exception e){
    			System.out.println("Exception 34 (FILE) " + e.toString()); 
    			System.out.println(StackTraceToString(e)  );
	    	}  
		    */
		    System.out.println("");
		//reason list
		reasonsList = srl.getReasonTerms();
		System.out.println("\n");
		System.out.print("\nFINAL reasonsList >> ");  for (ArrayList<String> line : reasonsList)     {                	System.out.print(line+ ", ");            }		  System.out.println(" ");
		/*
		Set<String> finalReasonSet = new HashSet<String>();
		    // Looking at the outputs from below, we can use Word forms, NOUN Synonyms, VERB synonyms, and DerivationallyForm, BUT NOT Verb Hyponyms
		    // For states we only use wordforms and deriavationallyform
			// now we add the different wordforms and wordnet word forms
			for (ArrayList<String> t : reasonsList) {
				for (String r : t) {
				// all word forms
				System.out.println(r);
				Set reasonListWordForms = wf.getAllWordForms(r);
				finalReasonSet.add(r);		//add current term
				finalReasonSet.addAll(reasonListWordForms);		//add word forms of that term
				// NOUN Synonyms 
				Set<String> synonyms = ws.getSynonym(r, SynsetType.NOUN); //region 
				System.out.print("\tNOUN Synonyms for word: " + r + " = "); for (String s : synonyms) {  System.out.print(s + ", ");	} System.out.println();		 finalReasonSet.addAll(synonyms);			
				// VERB synonyms
				Set<String> synonymsVERB = ws.getSynonym(r, SynsetType.VERB); //region 
				System.out.print("\tVERB Synonyms for word: " + r + " = "); for (String s : synonymsVERB) {  System.out.print(s + ", ");	} System.out.println();  finalReasonSet.addAll(synonymsVERB);	
				// DerivationallyForm 
				Set<String> derivationallyForm = ws.getDerivationallyRelatedForms(r, SynsetType.VERB); //develop
				System.out.print("\tDerivationallyForm for word: " + r + " = ");		for (String s : derivationallyForm) {  System.out.print(s + ", ");	}	System.out.println(); finalReasonSet.addAll(derivationallyForm);					
				}
			}	
			//convert to list
		    ArrayList<String> allReasonListWordForms_List = new ArrayList<String>();
		    allReasonListWordForms_List.addAll(finalReasonSet);
		    reasonsList = allReasonListWordForms_List;  //assign it to the main variable
		    */
		System.out.print("FINAL WORDFORMS reasonsList >> ");  for (ArrayList<String> line : reasonsList)     {                	System.out.print(line+ ", ");            }		  System.out.println(" ");
		//System.out.print("FINAL reasonsList >> ");  for (String line : reasonsList)     {                	System.out.print(line+ ", ");            }		  System.out.println(" "); 
		reasonIdentifierTerms =srl.getReasonIdentifierTerms(); 
		//now we cant add the different wordforms and wordnet word forms as reason identifiers wont have these..i think?
		System.out.print("FINAL reasonIdentifierTerms >> ");  for (String line : reasonIdentifierTerms)     {                	System.out.print(line+ ", ");            }		  System.out.println(" ");  
		proposalIdentifiers =srl.getProposalIdentifierTerms();
		System.out.print("FINAL proposalIdentifiers >> ");  for (String line : proposalIdentifiers)     {                	System.out.print(line+ ", ");            }		  System.out.println(" ");
		specialOSCommunitySpecificTerms = srl.getSpecialOSCommunitySpecificTerms(); 
		entitiesList = srl.getEntities();
		System.out.print("FINAL specialOSCommunitySpecificTerms >> ");  for (ArrayList<String> line : specialOSCommunitySpecificTerms)     {                	System.out.print(line+ ", ");            }		  System.out.println(" ");
		subStates = srl.getSubStates();
		System.out.print("FINAL entitiesList >> ");  for (String line : entitiesList)     {                	System.out.print(line+ ", ");            }		  System.out.println(", ");
		System.out.print("FINAL subStates >> ");  for (String line : subStates)     {                	System.out.print(line+ ", ");            }		  System.out.println(", ");
		/* for (String r : subStates) {
			// all word forms
			System.out.println(r);
			//Set reasonListWordForms = wf.getAllWordForms(r);	
			// NOUN Synonyms
			Set<String> synonyms = ws.getSynonym(r, SynsetType.NOUN); //region 
			System.out.print("\tNOUN Synonyms for word: " + r + " = "); 	for (String s : synonyms) {  System.out.print(s + ", ");	} System.out.println();				
			// VERB synonyms
			Set<String> synonymsVERB = ws.getSynonym(r, SynsetType.VERB); //region 
			System.out.print("\tVERB Synonyms for word: " + r + " = "); 	for (String s : synonymsVERB) {  System.out.print(s + ", ");	} System.out.println();
			// DerivationallyForm 
			Set<String> derivationallyForm = ws.getDerivationallyRelatedForms(r, SynsetType.VERB); //develop
			System.out.print("\tDerivationallyForm for word: " + r + " = ");		for (String s : derivationallyForm) {  System.out.print(s + ", ");	}	System.out.println();				
		}
		*/
		negationTerms = srl.getNegationTerms();
		System.out.print("FINAL negationTerms >> ");  for (String line : negationTerms)     {                	System.out.print(line+ ", ");            }		  System.out.println(", ");
		conditionalTerms= srl.getConditionalTerms();
		System.out.print("FINAL conditionalTerms >> ");  for (String line : conditionalTerms)     {                	System.out.print(line+ ", ");            }		  System.out.println(", ");
		//decision terms
		decisionTerms= srl.getDecisionTerms();
		System.out.print("FINAL deisionTerms >> ");  for (ArrayList<String> line : decisionTerms)     {                	System.out.print(line+ ", ");            }		  System.out.println(", ");
		// For states we only use wordforms and deriavationallyform
		// now we add the different wordforms and wordnet word forms
		/*Set<String> finalDecisionSet = new HashSet<String>();
		 for (String r : decisionTerms) {
			// all word forms
			System.out.println(r);
			Set decisionListWordForms = wf.getAllWordForms(r);
			finalDecisionSet.add(r);		//add current term
			finalDecisionSet.addAll(decisionListWordForms);
			// NOUN Synonyms
			Set<String> synonyms = ws.getSynonym(r, SynsetType.NOUN); //region 
			System.out.print("\tNOUN Synonyms for word: " + r + " = "); 	for (String s : synonyms) {  System.out.print(s + ", ");	} System.out.println();		finalDecisionSet.addAll(synonyms);			
			// VERB synonyms
			Set<String> synonymsVERB = ws.getSynonym(r, SynsetType.VERB); //region 
			System.out.print("\tVERB Synonyms for word: " + r + " = "); 	for (String s : synonymsVERB) {  System.out.print(s + ", ");	} System.out.println(); finalDecisionSet.addAll(synonymsVERB);
			// DerivationallyForm 
			Set<String> derivationallyForm = ws.getDerivationallyRelatedForms(r, SynsetType.VERB); //develop
			System.out.print("\tDerivationallyForm for word: " + r + " = ");	for (String s : derivationallyForm) {  System.out.print(s + ", ");	}	System.out.println();	 finalDecisionSet.addAll(derivationallyForm);			
		}
		
		//convert to list
	    List<String> allDecisionWordForms_List = new ArrayList<String>();
	    allDecisionWordForms_List.addAll(finalDecisionSet);
	    decisionTerms = allDecisionWordForms_List;		//assign it to the main variable
		*/
		//statesList = ObjectArrays.concat(statesList, decisionMechanismsSubStates, String.class); //we will combine statesList and decisionMechanismsSubStates
		//statesList.addAll(decisionMechanismsSubStates);
		//specialTerms = ObjectArrays.concat(specialTerms, entities, String.class);	//special terms and entities are combined
		//specialTerms.addAll(entitiesList);
	    System.out.print("FINAL WORDFORMS deisionTerms >> ");  for (ArrayList<String> line : decisionTerms)     {                	System.out.print(line+ ", ");            }		  System.out.println(", ");
		positiveWords = srl.getPositiveWords();
		negativeWords = srl.getNegativeWords();
		//dec 2018 terms that should not cooccur as they have double meaning
		notCoOccurTerms = srl.getNotCoOccurTerms();
		System.out.print("FINAL notCoOccurTerms >> ");  for (String line : notCoOccurTerms)     {                	System.out.print(line+ ", ");            }		  System.out.println(", ");
	}
	
	public static ArrayList<ArrayList<String>> getAllStatesList_StatesSubstates() {		return allStatesList_StatesSubstates;	}
	public static void setAllStatesList_StatesSubstates(ArrayList<ArrayList<String>> v_allStatesList_StatesSubstates) {		allStatesList_StatesSubstates = v_allStatesList_StatesSubstates;	}
	public static void setLabelList(ArrayList<ArrayList<String>> v_labelList) {
		labelList = v_labelList; //label list coudl list be a list if term is matched, but the original list of lists of not matched 
	} 
	
	public static void checkProposalSummary(Connection conn,ProcessingRequiredParameters prp,  String label, Timestamp v_dateTimeStamp,  boolean matchExactTerms,
			Integer pepNumber, Integer mid, String messageSubject, Date dateOfStateMessage,boolean retrieveKeywords, /*create_explicit_corpus cec, */ ClausIEMain ciem, boolean showCIEDebugOutput, boolean suggestCandidateSentences, 
			String tableToStoreExtractedRelations, boolean sameMessage,	List<String> candidateSentencesList,ArrayList<StringPair> spList,  String authorsRole, int dateDiff,
			String messageLocation) { //double termsLocationHintProbability
		String str="";
		String  messageSubjectStatesSubstatesList="", messageSubjectDecisionTermList="",	messageSubjectVerbList="",
				messageSubjectIdentifiersTermList="", messageSubjectEntitiesTermList="",	messageSubjectSpecialTermList="";
		connection= conn;		
		ScoresToSentences p = new ScoresToSentences();
		try {
			Statement stmt2 = conn.createStatement();	//System.out.println("\n mid, dateOfStateMessage " + dateOfStateMessage);	
			String sqlString2 = "SELECT pepsummary, pepurl, messageid from pepdetails where pep = "+pepNumber + ";";  //+ " and messageID = " + mid
		
			ResultSet rs2 = stmt2.executeQuery(sqlString2);  //date asc		
			System.out.println("\nLooking for Reasons for State in PEPSummary, Number of Messages to Process : " + prp.mc.returnRSCount(sqlString2, conn));
			while (rs2.next()) {		
				String pepSummary = rs2.getString("pepsummary");		mid = rs2.getInt("messageID");
				//several parameters here we sending as dummy values just to make the function work
				processSectionOrMessage(matchExactTerms, label, pepSummary, mid,messageSubject, dateOfStateMessage, dateOfStateMessage, pepNumber, "bdfl",
						 ciem, retrieveKeywords,  showCIEDebugOutput,  suggestCandidateSentences,  tableToStoreExtractedRelations,  conn, /*cec,*/ candidateSentencesList, spList,
						 authorsRole, dateDiff,messageLocation,false, false, false,false,false,false,0,0,0,0,0,
						 messageSubjectStatesSubstatesList, messageSubjectDecisionTermList,	messageSubjectVerbList,
							messageSubjectIdentifiersTermList, messageSubjectEntitiesTermList,messageSubjectSpecialTermList,false); //anotherProposalMatchedInMsgSubject=false
						 //false, false, false,false,false,false);
						 //messageSubjectHintProbablityScore,termsLocationHintProbability,dateDiffProbability, authorRoleProbability);	//termsLocationHintProbability should be high cos its pep summary
			}
			System.out.println("\nEND Looking for Reasons for State in PEPSummary, ");
		}
		catch (SQLException io) {
			
		}
	}
	
	public static String removeMessageHeaders(String message) {
		
		String lines[] = message.split("\\n");
		for (String line : lines)	{	//System.out.println("split line: " + line);					
			if (line.startsWith("From ")) 
				message = message.replace(line, ""); //System.out.println("'From' found replaced");	
			if (line.startsWith("From: ")) 
				message = message.replace(line, ""); //System.out.println("'From: ' found replaced");	
			if (line.startsWith("Date: ")) 
				message = message.replace(line, "");  //System.out.println("'Date' found replaced");	
			if (line.startsWith("Message-ID: ")) 
				message = message.replace(line, "");  //System.out.println("'Message-ID:' found replaced");	
			//these lines are needed as theya re delimeters
			/*if (line.startsWith("Update of /cvsroot/python")) 
				message = message.replace(line, "");  //System.out.println("'Update of /cvsroot/python' found replaced");
			if (line.startsWith("In directory"))
				message = message.replace(line, "");  //System.out.println("'In directory' found replaced");			
			if (line.startsWith("Modified Files:"))
				message = message.replace(line, "");  //System.out.println("'Modified Files:' found replaced");	
			*/
		}
		
		/*
		if (message.trim().startsWith("From ")) {		 System.out.println("'From' found ");	
			Integer start = message.indexOf("From ") + 1, end =  message.indexOf("\\r?\\n",5);	System.out.println("start "+start + " end "+end );
			String requiredString = message.substring(message.indexOf("From: ") + 1, message.indexOf("\\r\\n")); //  \n
			//requiredString = message.substring(message.indexOf("From: ") + 1, message.indexOf("\n")); //   \r\n
			//requiredString = message.substring(message.indexOf("From: ") + 1, message.indexOf("\n"));
			message = message.replace(message, requiredString);
		} else {
			 System.out.println("ELSE From: found ");
		} */
		return message.trim();
	}
	
	public static void cheackNearbyMessages(Connection conn,ProcessingRequiredParameters prp,  String label, Timestamp v_dateTimeStamp,  boolean matchExactTerms,
			Integer pepNumber,Integer messageIDForRestart, String messageSubject, Date dateOfStateMessage,boolean retrieveKeywords, /*create_explicit_corpus cec, */ ClausIEMain ciem, boolean showCIEDebugOutput, boolean suggestCandidateSentences, 
			String tableToStoreExtractedRelations, boolean sameMessage,	List<String> candidateSentencesList,ArrayList<StringPair> spList,  String messageLocation) throws SQLException {
		String str="";
		
		//double messageSubjectHintProbablityScore=0.0; //double termsLocationHintProbability	
		//if(authorFlexibility)	//if we want to process only the message with teh state change
		//	str = ""; //" and messageID = "+ mid;
		//else {
			//str = " and DATEDIFF('" + dateOfStateMessage + "',date2) < 7 and authorsRole IN('proposalAuthor','bdfl','coredeveloper','bdfl') ";  //try 7 instead of 14 as well
		//	str = " and authorsRole IN('proposalAuthor','bdfl','coredeveloper','bdfl_delegate') ";
		//}
		
		//feb 2019
		//FOR all different purposes, these   functions get the max pep and max message id
		Integer pepNumberForRestart = pepNumber, processedCounter=0,totalMessagesToProcessForProposal=0;
		/*
		try {
			System.out.println("Current PEP Number="+pepNumber + ", pepNumberForRestart="+pepNumberForRestart);
			System.out.println("RESTART = TRUE, Getting RESTART PEP AND MESSAGEID");
			pepNumberForRestart = prp.getPd().getMaxProcessedProposalFromStorageTable(conn, prp); //select max pep from table
			System.out.println("pepNumberForRestart="+pepNumberForRestart);
			messageIDForRestart =	prp.getPd().getMaxProcessedMessageIDForProposalFromStorageTable(conn, pepNumberForRestart,prp);  //select max messageid from tabkle for that particular pep
			System.out.println("messageIDForRestart="+messageIDForRestart);
			prp.setPepNumberForRestart(pepNumberForRestart);
			prp.setMessageIDForRestart(messageIDForRestart);	//if there was an error, we dont want to attempt to proces sit again, thats why we add 1
			//if (prp.isSelectedAll())	System.out.println("Chosen Selected All---------------------------Unique Peps count = "+ UniquePeps.size() );
			//if (prp.isSelectedList())	System.out.println("Chosen Selected List---------------------------");
			System.out.println(" Restart: ProposalNumberForRestart: " +pepNumberForRestart + ", messageIDForRestart+1: " +messageIDForRestart);
		}
		catch (Exception io) {
			System.out.println("Exception 342");
		}
		//assuming the process processes peps in ascending order
		if(pepNumberForRestart==0 || pepNumberForRestart<pepNumber) { //if we have no stored messages, means we have not processed any
			pepNumberForRestart = pepNumber; //if continue from the beginning of the passed pep
			messageIDForRestart=0;	//its a different pep and we start from zero for that pep
			System.out.println("here changing, pepNumberForRestart="+pepNumberForRestart);
		}
		System.out.println("FINAL pepNumberForRestart="+pepNumberForRestart);
		*/
		Statement stmt2 = conn.createStatement();	//System.out.println("\n mid, dateOfStateMessage " + dateOfStateMessage);	
		String sqlString2 = "SELECT pep,originalpepnumber,messageID, subject, date2,datetimestamp, analysewords,author, authorsRole, DATEDIFF('" + dateOfStateMessage + "',date2) as datediff "
				+ " from allmessages where pep = " + pepNumber //+ " and messageid > " + messageIDForRestart //     +pepNumber  //+ " and messageID = " + mid //pepNumberForRestart
//				+ " and messageID = 138933 " //OR messageid = 385062)"  //if we are debugging and checking a message
				//+ str
				+ " order by messageid asc";	//+ " order by datetimestamp desc";
		//SELECT DATEDIFF("2017-01-01", "2016-12-24"); returns 8, meaning big one first
		//we want within the last 7 or 14 days, so datetocompare first and message record date is second
		ResultSet rs2 = stmt2.executeQuery(sqlString2);  //date asc		
		totalMessagesToProcessForProposal = prp.mc.returnRSCount(sqlString2, conn);
		System.out.println("\nLooking for Reasons for State in Nearby Messages For PEP ("+pepNumberForRestart+"), Number of Messages to Process : " + " Number of messages: " + totalMessagesToProcessForProposal);

		boolean messageSubjectContainsReasonsTerms = false,		messageSubjectContainsReasonsIdentifierTerms= false, 	messageSubjectContainsStatesSubstates= false, 
				messageSubjectContainsProposalIdentifierTermList = false,	messageSubjectContainsPositiveWords= false,		 	messageSubjectContainsNegativeWords= false,
				messageSubjectContainsVerbTerms= false,			  		messageSubjectContainsEntitiesTerms= false,		  		messageSubjectContainsSpecialTerms= false,
				messageSubjectContainsDecisionTerms= false,				messageSubjectContainsProposalIdentifierTerms= false,	messageSubjectNegationFound= false;
		
		String  messageSubjectStatesSubstatesList="", messageSubjectDecisionTermList="",	messageSubjectVerbList="",
				messageSubjectProposalIdentifiersTermList="", messageSubjectEntitiesTermList="",	messageSubjectSpecialTermList="";
				
		int messageSubjectStatesSubstatesListCount=0, messageSubjectDecisionTermListCount=0,messageSubjectProposalIdentifiersTermListCount=0,
				messageSubjectEntitiesTermListCount=0, messageSubjectSpecialTermListCount=0;
		
		Integer proposalNum, mid,originalProposalNum,dateDiff;	int noOfTerms;
		String author="",authorsRole="",message="";	Timestamp v_date; Date v_date2;
		String commitDelimeters[] = {"diff --git a","diff -C2 -d","--- NEW FILE:","Modified: peps/trunk/pep-","Added: peps/trunk/","RCS file: /cvsroot/python/python/nondist/peps/"};
		boolean matchWholeTerms = true, anotherProposalMatchedInMsgSubject= false;
		String finalListOfAllTermsFound="";
		ArrayList<String> uniqueList =new ArrayList<String>(); 
		int option =0; //check if message subject contains only other peps 
		boolean allowZero = true;
		String decisionMembers[] = {"proposalAuthor","bdfl","coredeveloper","bdfl_delegate"}; //if message is written by these members, they get higher score
		int dd=0,mar=0; 
		double dateDiffProbability=0.0, aRoleProbability =0.0;
		List<String> lines = new ArrayList<String>(); //Arrays.asList(section.split("\\n")));
		String line ="";
		Integer countReasonSentencesInMessage=0, messageID=null,reasonsentencesTotal=0; double v_dateDiffProbability= 0.0,authorRoleProbability=0.0;
		
		while (rs2.next()) {			
			processedCounter++; 
			proposalNum = rs2.getInt("pep");					originalProposalNum = rs2.getInt("originalpepnumber");   message = rs2.getString("analysewords");			
			mid = rs2.getInt("messageID");		 v_date2 = 	rs2.getDate("date2"); 			v_date = rs2.getTimestamp("dateTimeStamp");		messageSubject  = rs2.getString("subject");		
			author = rs2.getString("author"); 	 authorsRole = rs2.getString("authorsRole");		 dateDiff = rs2.getInt("datediff");
			//System.out.println("NEW MESSAGE, Proposal: " +proposalNum + " MID: "+ mid); 
			
			if (messageSubject==null || messageSubject.isEmpty()) {
				messageSubject= ""; System.out.println("messageSubject==null");  continue; 
			}
			messageSubjectContainsReasonsTerms = messageSubjectContainsReasonsIdentifierTerms= messageSubjectContainsStatesSubstates=  
			messageSubjectContainsProposalIdentifierTermList = messageSubjectContainsPositiveWords= messageSubjectContainsNegativeWords= 
			messageSubjectContainsVerbTerms= messageSubjectContainsEntitiesTerms= messageSubjectContainsSpecialTerms= 
			messageSubjectContainsDecisionTerms= messageSubjectContainsProposalIdentifierTerms= messageSubjectNegationFound;
			
			messageSubjectStatesSubstatesList=messageSubjectDecisionTermList=messageSubjectVerbList=
					messageSubjectProposalIdentifiersTermList=messageSubjectEntitiesTermList=messageSubjectSpecialTermList="";
					
			messageSubjectStatesSubstatesListCount=messageSubjectDecisionTermListCount=messageSubjectProposalIdentifiersTermListCount=
					messageSubjectEntitiesTermListCount=messageSubjectSpecialTermListCount=0;
			//check proposalAtTheEndofMessage				
			//System.out.println("\n\t\tChecking New Message: "+mid); 
			
			//feb 2019
			/*   //since this script is so complicated, we restart after every pep, rather than after processing every x number of messages
			Integer toRunLimit = prp.getRunForNumberOfMessagesLimit(), currentCounter = prp.getProcessedMessageCounter(); //counter is initialised to 0 anyway, so dont worry
			//System.out.println("toRunLimit: " +toRunLimit);
			//System.out.println("currentCounter: " +currentCounter);
			if(currentCounter >= toRunLimit) {
				System.exit(0);		System.out.println("We exit since the RE script slows down after processing some messages, toRunLimit: "+toRunLimit+" message and Loop should start again");
			}
			else
				prp.setProcessedMessageCounter(currentCounter+1);
			*/
			if(message==null || message.isEmpty() || message.equals("")) {
				continue;	//we skip empty messages
			}
			
			//checking message subject
			matchWholeTerms = true;
			if(messageSubject==null || messageSubject.length() ==0 || messageSubject.isEmpty())    {}		//.toLowerCase()
			else {	//THIRD CHECK...CHECK FOR JUST THE REASON TERMS
				messageSubject=messageSubject.toLowerCase();
				messageSubjectStatesSubstatesList = containsTermsFromListOfLists(messageSubject,labelList, matchWholeTerms);//allStatesList_StatesSubstates
				messageSubjectStatesSubstatesList = messageSubjectStatesSubstatesList.startsWith(",") ? messageSubjectStatesSubstatesList.substring(1) : messageSubjectStatesSubstatesList;
				if(messageSubjectStatesSubstatesList == null || messageSubjectStatesSubstatesList.isEmpty() || messageSubjectStatesSubstatesList.equals("")  || messageSubjectStatesSubstatesList.length()==0) {}
				else { 	messageSubjectContainsStatesSubstates=true;					
						messageSubjectStatesSubstatesListCount = countTermsInString(messageSubjectStatesSubstatesList);
						if (outputForDebug) {
							System.out.println(" messageSubjectStatesSubstatesList: ("+ messageSubjectStatesSubstatesList + ")	 "
								+ "messageSubjectStatesSubstatesTermsCount: " + countTermsInString(messageSubjectStatesSubstatesList));	
						}
				}
				
				//most common is decision terms  used in message subject
				messageSubjectDecisionTermList = containsTermsFromListOfLists(messageSubject,decisionTerms, matchWholeTerms);
				messageSubjectDecisionTermList = messageSubjectDecisionTermList.startsWith(",") ? messageSubjectDecisionTermList.substring(1) : messageSubjectDecisionTermList;
				if(messageSubjectDecisionTermList == null || messageSubjectDecisionTermList.isEmpty() || messageSubjectDecisionTermList.length()==0) {}
				else { messageSubjectContainsDecisionTerms=true;		 
					messageSubjectDecisionTermListCount=countTermsInString(messageSubjectDecisionTermList);
					if (outputForDebug) {
						System.out.println(" messageSubjectDecisionTermList: ("+ messageSubjectDecisionTermList + ")  messageSubjectDecisionTermsCount: " + countTermsInString(messageSubjectDecisionTermList));				
					}
				}		
				
				//FOURTH CHECK...CHECK FOR JUST THE VERB TERMS
				messageSubjectVerbList = containsAllTerms(messageSubject,verbs, matchWholeTerms);
				messageSubjectVerbList = messageSubjectVerbList.startsWith(",") ? messageSubjectVerbList.substring(1) : messageSubjectVerbList;
				if(messageSubjectVerbList == null || messageSubjectVerbList.isEmpty() || messageSubjectVerbList.length()==0) {}
				else {	messageSubjectContainsVerbTerms=true;	
					//int messageSubjectContainsVerbTerms(messageSubjectContainsVerbTerms);  not needed yet
					if (outputForDebug) {
						System.out.println(messageSubjectVerbList);
					}
				}	
				
				//FIFTH CHECK...CHECK FOR JUST THE identifiersTermList TERMS		
				messageSubjectProposalIdentifiersTermList = containsAllTerms(messageSubject,proposalIdentifiers, matchWholeTerms);
				messageSubjectProposalIdentifiersTermList = messageSubjectProposalIdentifiersTermList.startsWith(",") ? messageSubjectProposalIdentifiersTermList.substring(1) : messageSubjectProposalIdentifiersTermList;
				if(messageSubjectProposalIdentifiersTermList == null || messageSubjectProposalIdentifiersTermList.isEmpty() || messageSubjectProposalIdentifiersTermList.length()==0) {}
				else {	messageSubjectContainsProposalIdentifierTermList=true;
					messageSubjectProposalIdentifiersTermListCount=countTermsInString(messageSubjectProposalIdentifiersTermList);
					if (outputForDebug) {
						System.out.println("messageSubjectProposalIdentifiersTermList: ("+messageSubjectProposalIdentifiersTermList +") "
								+ "messageSubjectProposalIdentifiersTermCount: " + countTermsInString(messageSubjectProposalIdentifiersTermList));
					}										
				}
				
				//SIXTH CHECK...CHECK FOR JUST THE identifiersTermList TERMS		
				messageSubjectEntitiesTermList = containsAllTerms(messageSubject,entitiesList, matchWholeTerms);
				messageSubjectEntitiesTermList = messageSubjectEntitiesTermList.startsWith(",") ? messageSubjectEntitiesTermList.substring(1) : messageSubjectEntitiesTermList;
				if(messageSubjectEntitiesTermList == null || messageSubjectEntitiesTermList.isEmpty() || messageSubjectEntitiesTermList.length()==0) {}
				else {	messageSubjectContainsEntitiesTerms=true;
					messageSubjectEntitiesTermListCount=countTermsInString(messageSubjectEntitiesTermList);
					if (outputForDebug) {
						System.out.println("messageSubjectEntitiesTermList: (" + messageSubjectEntitiesTermList + ") messageSubjectEntitiesTermListCount: " + countTermsInString(messageSubjectEntitiesTermList));
					}										
				}
				
				//SEVENTH CHECK...CHECK FOR JUST THE special TERMS	- contains entities	
				messageSubjectSpecialTermList = containsTermsFromListOfLists(messageSubject,specialOSCommunitySpecificTerms, matchWholeTerms);
				messageSubjectSpecialTermList = messageSubjectSpecialTermList.startsWith(",") ? messageSubjectSpecialTermList.substring(1) : messageSubjectSpecialTermList;
				if(messageSubjectSpecialTermList == null || messageSubjectSpecialTermList.isEmpty() || messageSubjectSpecialTermList.length()==0) { /*System.out.println("$$$$$$$$$$$4does not containsSpecialTerms: "+ containsSpecialTerms);*/	}
				else {	messageSubjectContainsSpecialTerms=true;						
					messageSubjectSpecialTermListCount=countTermsInString(messageSubjectSpecialTermList);
					if (outputForDebug) {
						System.out.println("messageSubjectSpecialTermList: "+ messageSubjectSpecialTermList + ") messageSubjectEntitiesTermListCount: " +
								countTermsInString(messageSubjectSpecialTermList));	
					}
				}	//System.out.println(identifiersTermList);		
														
				finalListOfAllTermsFound = messageSubjectStatesSubstatesList + " " + messageSubjectProposalIdentifiersTermList + " " +messageSubjectVerbList + " "+messageSubjectEntitiesTermList 
						+ " "+messageSubjectSpecialTermList+ " "+messageSubjectDecisionTermList; 
				//return distinct words in finalListOfAllTermsFound
				
				String s[]=finalListOfAllTermsFound.split(" ");
				finalListOfAllTermsFound="";
				for (int i = 1; i < s.length; i++) {
				    if (!(uniqueList.contains(s[i]))) {
				    	uniqueList.add(s[i]);    	finalListOfAllTermsFound = finalListOfAllTermsFound + " " + s[i];
				    }
				}
				noOfTerms = finalListOfAllTermsFound.trim().split(" ").length;

			}	
			//check anotherProposalMatchedInMsgSubject
			anotherProposalMatchedInMsgSubject= false;
			if(messageSubject==null || messageSubject.length() ==0) { System.out.println("\tMessage Subject is null");  }
			else {
				//This block of code has been removed from checkin messages section above, as some checkin  message subjects dont contain the pep number we are looking for, but its the pep number does exist in the message body
				// but it is important here, to check in all other messages apart from checkin messages
				option =0; //check if message subject contains only other peps 
				allowZero = true;
				if(pms.checkTextContainsProposalNumber_WithOptions(messageSubject.toLowerCase(), proposalNum,option,allowZero)) {  
					//	|| messageSubject.toLowerCase().contains("python-patches")) {	//wasnt not sure why we dont consider bugs list messages
//$$						System.out.println("\tMessage Subject contains only other PEPs b, we skip. MID: "+ mid + " Message Subject: " + messageSubject);
					//Oct 2018 ..added we consider messages with ptehr peps in msg subject, but only those sentences which have current pep number, eg.
					//msg subject = "PEP318 alternate syntax idea"
					//"Well, PEP 309 has been submitted for review, so with any luck (since it's not very controversial) we'll have somewhere to put compose().
					anotherProposalMatchedInMsgSubject= true;
					//continue;	//skip current message and process the next one
				} //else {System.out.println("\tMessage Subject contains current PEP"); }
				
			}	//System.out.println("\tMess");
			if(outputForDebug)
				System.out.println("\tRemoving Email Message Headers, From and Date ");
			message = removeMessageHeaders(message);
			
			//Feb 2019, write the message level data for each message we write one record and wil update this later, 
			//certain fields from above with cumulative count of sentences caught and paragraphs cught
			//count the number if candidate sentences and candidate messages we have for that message
			String location = "message";
			ScoresToSentences p0 = new ScoresToSentences();
			p0.setLabel(label);					p0.setLocation(location); //sentence or paragraph
//		    p0.setTermsLocationWithMessageProbabilityScore(v_sentenceOrParagraphLocationHintProbability);
		    p0.setProposalNum(proposalNum);			    p0.setMid(mid);
		    p0.setMessageSubject(messageSubject);		p0.setDateVal(v_date2); 
			p0.setMessageLocation(messageLocation); 	p0.setDateDiff(dateDiff);    	p0.setAuthorsRole(authorsRole);	//assign high values
			p0.setDateOfStateMessage(dateOfStateMessage);
			
			dd=0; mar=0; 
			dateDiffProbability=0.0;
		      //if(dateDiffProbability==0.5) 	  dd=1;
		      //if(authorRoleProbability==0.5) 	  mar=1;
		      //new version....
		      if (Math.abs(dateDiff) <= 3) { dateDiffProbability=0.9;	dd=3;	//less than 7 days from state message gets higher score					
					//continue;	//skip messages more than specified no of days
			  }else if (Math.abs(dateDiff) > 3 && Math.abs(dateDiff) <= 7){				  	dateDiffProbability=0.6;				  	dd=2;			  }
			  else if (Math.abs(dateDiff) > 7 && Math.abs(dateDiff) <= 150){				  	dateDiffProbability=0.2;				  	dd=1;			  }
			  else {	  	dateDiffProbability=0.0;				    dd=0;			  }	
		    p0.setDateDiffProbability(dateDiffProbability);
//		    System.out.println("Debug 4");
		    aRoleProbability =0.0;
		    if (authorsRole == null || authorsRole.isEmpty() || authorsRole.equals(""))    {    //othercommunitymember
		    	aRoleProbability=0.0;					mar=0;		
		    }
		    else {
			    if(authorsRole.contains("bdfl")) {  aRoleProbability=0.9;						mar=1;	} //bdfl or delegate					
			    else if (authorsRole.equals("proposalAuthor")) {  aRoleProbability=0.6;					mar=2;				}//bdfl or delegate		
			    else if (authorsRole.equals("pepeditors")) { aRoleProbability=0.5;					mar=3;				}	 //nov 2018, added pep editors
			    else if (authorsRole.equals("coredeveloper")) { aRoleProbability=0.4;					mar=4;				}	 //bdfl or delegate	
			}
		    p0.setAuthorRoleProbability(aRoleProbability);
		    
		    p0.setMessageSubjectContainsStatesSubstates(messageSubjectContainsStatesSubstates);
			p0.setMessageSubjectContainsDecisionTerms(messageSubjectContainsDecisionTerms);
			p0.setMessageSubjectContainsVerbTerms(messageSubjectContainsVerbTerms);
			p0.setMessageSubjectContainsProposalIdentifierTerms(messageSubjectContainsProposalIdentifierTerms);
			p0.setMessageSubjectContainsEntitiesTerms(messageSubjectContainsEntitiesTerms);
			p0.setMessageSubjectContainsSpecialTerms(messageSubjectContainsSpecialTerms);
			
			//message subject term lists
			p0.setMessageSubjectDecisionTermList(messageSubjectDecisionTermList);
			p0.setMessageSubjectProposalIdentifiersTermList(messageSubjectProposalIdentifiersTermList);
			p0.setMessageSubjectStatesSubstatesList(messageSubjectStatesSubstatesList);
			p0.setMessageSubjectSpecialTermList(messageSubjectSpecialTermList);
			p0.setMessageSubjectVerbList(messageSubjectVerbList);
			p0.setMessageSubjectEntitiesTermList(messageSubjectEntitiesTermList);
							
			p0.setMessageSubjectStatesSubstatesListCount(messageSubjectStatesSubstatesListCount);
			p0.setMessageSubjectContainsDecisionTerms(messageSubjectContainsDecisionTerms);
			//p0.setMessageSubjectVerbListCount(messageSubjectVerbListCount);
			p0.setMessageSubjectProposalIdentifiersTermListCount(messageSubjectProposalIdentifiersTermListCount);
			p0.setMessageSubjectEntitiesTermListCount(messageSubjectEntitiesTermListCount);
			p0.setMessageSubjectSpecialTermListCount(messageSubjectSpecialTermListCount);
			//Feb 2019 write the message info, some fields will be calculated later amd this record updated
			if(outputForDebug)
			System.out.println("*** Writing Message Data: " + mid);
			//tableNameToStoreMessage = "autoextractedreasoncandidatemessages";
			p0.writeMessageToDatabase(connection, tableNameToStoreMessage, outputForDebug);
			//p0.dispose();
			p0.finalize();	p0=null;
//			System.out.println("Debug 5 ");
			//NOW WE PROCESS THE MESSAGE
			//split checkin messages
			if(messageSubject.contains("checkins")) {	
				if(outputForDebug)
					System.out.println("\tFolder is checkins ");						
				for (String del : commitDelimeters) {
					if(message.contains(del)) {	//System.out.println("\n\tmessage contains commitDelimeter ");							
						message = message.replaceAll(del, "pankaj"+del);//we add some charaters in front so that splitting wont loose the delimeter //System.out.println("\tmessage contains commitDelimeter ");						
					}
				}			
				//aug 2018 update..some commit message for other peps, contain references to current pep. 
				//eg. 'Record that PEP 264 resolved some open issues left hanging in 236.' - this is captured under ppe 264 but commit message is for pep 234
				if(outputForDebug)
					System.out.println("\tSplitting Message into Sectiosn using Delimeters ");
				for(String section: message.split("pankaj")){
					//Debugging...
//						System.out.println("\n\n\n\n\t START HERE sections first 50 chars: " + section.substring(0,50));		
					if(outputForDebug)	
						System.out.println("\tSTART HERE sections section: \n" );
//$$					for (String l : section.split("\\n"))
//$$						System.out.println("\t" + l);
//											if (section.startsWith(del)) {	//if first line contains current pep then go ahead and process or else dont process //System.out.println("\t commit sections ");
						line = "";
						/*if(section.contains(".txt")) {
							int start = 0; //section.indexOf(del), 
							int end = section.indexOf(".txt");
							if(start == -1 || end == -1) { continue; }
							line = section.substring(start,end) ;	//get everything in that line somehow				
						} */
						
						//String lines[] = section.split("\\n");
						// To ArrayList
						//List<String> lines = new ArrayList<>(Arrays.asList(section.split("\\n")));
						lines = Arrays.asList(section.split("\\n"));
						//we have to check for pep number line by line
						for (String vline : lines)	{	//System.out.println("split line: " + line);					
							if (vline.contains(".txt")){  
								if(outputForDebug)
									System.out.println("\tdelimeter found in line: " + vline);
								/*int start = 0; //section.indexOf(del), 
								int end = section.indexOf(".txt");
								if(start == -1 || end == -1) { continue; }
								line = section.substring(start,end) ; */	//get everything in that line somehow	
								line = vline; 	break;
							}	
							if (vline.contains("pep") && !vline.contains("peps")) {	
								if(outputForDebug)
									System.out.println("\tdelimeter found in line: " + vline); //not all checkin message sections contains '.txt', so we split
								line = vline;	break;	//get everything in that line somehow	
							}
						}
						
						//int option = 1; //text shud only contain current proposal/pep
						option = 3;   //text current pep exists, regardless if other pep exists,
						allowZero = true; //Feb 2019, we had to allow zero as sometimes tyhey are in same line 'pep-0000.txt pep-0295.txt '
						if(prp.getPms().checkTextContainsProposalNumber_WithOptions(line.toLowerCase(), proposalNum,option,allowZero)) {	//check if its for the current pep, if no disregard	
							//process section
							if(outputForDebug)
							System.out.println("\tSECTION Contains OnlyCurrent Pep Number, (processing SectionOrMessage) showing section's first 50 chars: "); // + section);//.substring(0,50));
//							System.out.println(section);//.substring(0,50));
							processSectionOrMessage(matchExactTerms, label,section, mid,messageSubject, v_date2,dateOfStateMessage, proposalNum, author, ciem, retrieveKeywords, showCIEDebugOutput, 
									suggestCandidateSentences, tableToStoreExtractedRelations, conn, /*cec,*/ candidateSentencesList, spList, authorsRole,dateDiff,messageLocation,
									messageSubjectContainsStatesSubstates,messageSubjectContainsDecisionTerms,messageSubjectContainsVerbTerms,
									messageSubjectContainsProposalIdentifierTermList,messageSubjectContainsEntitiesTerms,messageSubjectContainsSpecialTerms,
									messageSubjectStatesSubstatesListCount, messageSubjectDecisionTermListCount,messageSubjectProposalIdentifiersTermListCount,
									messageSubjectEntitiesTermListCount, messageSubjectSpecialTermListCount,
									messageSubjectStatesSubstatesList, messageSubjectDecisionTermList,	messageSubjectVerbList,
									messageSubjectProposalIdentifiersTermList, messageSubjectEntitiesTermList,messageSubjectSpecialTermList,anotherProposalMatchedInMsgSubject); //anotherProposalMatchedInMsgSubject = false..just temp
									//messageSubjectHintProbablityScore,termsLocationHintProbability,messageDateDiffProbability, messageAuthorRoleProbability);	//System.out.print("\tProcessing done");
//													orderReasonListForProposal(proposalNum);	//System.out.print("\tOrdering done");
//													storePEPReasonsFromArrayListToDB(proposalNum);	//System.out.print("\tStoring done");
//													System.out.println("Processing, ordering, storing Finished for mid = "+mid+" proposal: " + proposalNum + ", "+processedCounter+"/"+totalRows);
//													processedCounter++;	
//													rr.clear();		//clear the arraylis
						}
						else {	//System.out.println("\t ifSection Contains Only Other Pep Numbers "); 
						}						
				}//end for								
			}//end if								
			else {
				if(outputForDebug)
					System.out.println("\t else if its not a message from checkins mailing list"); //process message ... if its not a message from checkins mailing list
				//We check if message subject contains the pep title instead of pep#. Just like pep 308 acceptance message "Conditional Expression Resolution" = "PEP 308 Resolution" 
				//This will add weigtage to the reason extraction  
				//check if pepnumber not equal to original pep number, if so, pep number was found in the message subject - i mena thats why its was changed
				boolean proposalFoundInMessageSubject=false;
				if(!originalProposalNum.equals(proposalNum)) {
					proposalFoundInMessageSubject=true;
					messageSubjectContainsProposalIdentifierTerms=true;  	
					if(outputForDebug)
						System.out.println("xxxxxxxxxxxxxxxx MessageSubject containsIdentifierTerms: "); //+containsIdentifierTerms);
				} else {
					//System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxx MessageSubject  containsIdentifierTerms: "); //+containsIdentifierTerms);
				}
				//if(proposalFoundInMessageSubject) {
					//more weight  if there are special terms here
				//}
				
				
				
				//int messageSubjectStatesSubstatesListCount=0, messageSubjectDecisionTermListCount=0,messageSubjectProposalIdentifiersTermListCount=0,
				//		messageSubjectEntitiesTermListCount=0, messageSubjectSpecialTermListCount=0;
							
				processSectionOrMessage(matchExactTerms, label, message, mid, messageSubject, v_date2,
						dateOfStateMessage, proposalNum, author, ciem, retrieveKeywords, showCIEDebugOutput,
						suggestCandidateSentences, tableToStoreExtractedRelations, conn, candidateSentencesList, spList,
						authorsRole, dateDiff, messageLocation, messageSubjectContainsStatesSubstates,
						messageSubjectContainsDecisionTerms, messageSubjectContainsVerbTerms,
						messageSubjectContainsProposalIdentifierTermList, messageSubjectContainsEntitiesTerms,
						messageSubjectContainsSpecialTerms, messageSubjectStatesSubstatesListCount,
						messageSubjectDecisionTermListCount, messageSubjectProposalIdentifiersTermListCount,
						messageSubjectEntitiesTermListCount, messageSubjectSpecialTermListCount,
						messageSubjectStatesSubstatesList, messageSubjectDecisionTermList, messageSubjectVerbList,
						messageSubjectProposalIdentifiersTermList, messageSubjectEntitiesTermList,
						messageSubjectSpecialTermList, anotherProposalMatchedInMsgSubject);
				
						// messageSubjectHintProbablityScore,termsLocationHintProbability, messageDateDiffProbability, messageAuthorRoleProbability); //System.out.print("\tProcessing done");					
//										orderReasonListForProposal(proposalNum);	//System.out.print("\tOrdering done");
//										storePEPReasonsFromArrayListToDB(proposalNum);	//System.out.print("\tStoring done");
//										System.out.println("Processing, ordering, storing Finished for mid = "+mid+" proposal: " + proposalNum + ", "+processedCounter+"/"+totalRows);
//										processedCounter++;	
//										rr.clear();		//clear the arraylist
				//compute verb pairs
				//start with showing list of message ids of messages
				
			}
			System.out.println("Proposal "+proposalNum+" Message ID ("+mid+") Msg completed counter " + processedCounter + "/"+ totalMessagesToProcessForProposal +" Cleanup completed, "
					 //+ "Restart counter/Limit ("+ currentCounter+"/"+ toRunLimit+")" 
					 );
			//dec 2018...now here we do weights for message level analysis
			//authorrole can be read from column
			//select count(*) from autoextracted...where pep = currPep ...based on count give, that number
			//assign weight to all rows for that message
			//Feb 2019, how many reason sentences in message - will get higher weightage
			countReasonSentencesInMessage=0; messageID=null;reasonsentencesTotal=0; v_dateDiffProbability= 0.0;authorRoleProbability=0.0;
			try {
				/*Statement stmt20 = conn.createStatement();	//System.out.println("\n mid, dateOfStateMessage " + dateOfStateMessage);	
				String sqlString20 = "SELECT distinct messageid, dateDiffProbability,authorRoleProbability, count(*) as reasonsentencesTotal  "
						+ "  from " + tableNameToStore + " where proposal = "+pepNumber + " order by dateOfStateMessage desc";
				//SELECT DATEDIFF("2017-01-01", "2016-12-24"); returns 8, meaning big one first
				//we want within the last 7 or 14 days, so datetocompare first and message record date is second
				ResultSet rs20 = stmt20.executeQuery(sqlString20);  //date asc		
				//System.out.println("\nLooking for Reasons for State in Nearby Messages, Number of Messages to Process : " + prp.mc.returnRSCount(sqlString2, conn));
				while (rs20.next()) {		
					messageID = rs20.getInt("messageid"); 				v_dateDiffProbability = rs20.getInt("dateDiffProbability"); 
					authorRoleProbability = rs20.getInt("messageid"); 	reasonsentencesTotal = rs20.getInt("reasonsentencesTotal");
					//now we compute values from each message in the proposal
					Statement stmt21 = conn.createStatement();	//System.out.println("\n mid, dateOfStateMessage " + dateOfStateMessage);	
					//now we get the closest reasontype message
					String sqlString21 = " select proposal, messageSubject, datevalue, datediff, authorrole, sentence, termsmatched, messageTypeIsReasonMessage, containsreason "
										  + " from autoextractedreasoncandidatesentences_dec " 
										  + " where proposal = 386 and messageTypeIsReasonMessage =1 and datediff > - 15 "
										  + " and messageSubject NOT LIKE '%checkins%' " // -- and containsReason = 0 and messageid < 6000000 "
										  + " order by datediff asc";
					//SELECT DATEDIFF("2017-01-01", "2016-12-24"); returns 8, meaning big one first
					//we want within the last 7 or 14 days, so datetocompare first and message record date is second
					ResultSet rs21 = stmt21.executeQuery(sqlString21);  //date asc		
					//System.out.println("\nLooking for Reasons for State in Nearby Messages, Number of Messages to Process : " + prp.mc.returnRSCount(sqlString2, conn));
					if (rs21.next()) {		
						countReasonSentencesInMessage = rs21.getInt("countReasonSentences");	
					} 
				} */
				//dateiff weight and authorroleweight for taht message ..can be done in one sql
				//dateDiffProbability..authorRoleProbability
				/* Statement stmt23 = conn.createStatement();	//System.out.println("\n mid, dateOfStateMessage " + dateOfStateMessage);	
				String sqlString23 = "SELECT dateDiffProbability,authorRoleProbability from " + tableNameToStore + " where pep = "+pepNumber and 	+ " order by datetimestamp desc";
				//SELECT DATEDIFF("2017-01-01", "2016-12-24"); returns 8, meaning big one first
				//we want within the last 7 or 14 days, so datetocompare first and message record date is second
				ResultSet rs23 = stmt23.executeQuery(sqlString23);  //date asc		
				//System.out.println("\nLooking for Reasons for State in Nearby Messages, Number of Messages to Process : " + prp.mc.returnRSCount(sqlString2, conn));
				while (rs23.next()) {		
					messageID = rs23.getInt("messageid");
				} */
				
				
				//insert into database
				/*PreparedStatement pstmt0 = null;		    
		    	//   reasonsTermsFoundList, reasonsIdentifierTermsList, statesSubstatesList, identifiersTermList, entitiesTermList,
				 //  specialTermList, decisionTermList, negationTermList,
				String query = "updatate into "+tableNameToStore+" SET countReasonSentencesInMessage = ? where proposal = ? and  messageID = ?; "; //64
				pstmt0 = connection.prepareStatement(query); // create a statement
				//message metadata
				pstmt0.setInt(1,countReasonSentencesInMessage); 		
				pstmt0.setInt(2, pepNumber); 	
				pstmt0.setInt(3, messageID);
				pstmt0.executeUpdate();  */ // execute insert statement
	//$$			      System.out.println("\t inserted row in db  combination: "+r.getCombination() + " sentence " + r.getSentenceString());
				
				
								
		    } catch (Exception e) {
		      e.printStackTrace();	System.out.println(StackTraceToString(e)  );	
		    }
			message = null;
			System.gc();			
		}
	}
	/*
	public static Probability extractTermsInMessageSubject(Probability p, String messageSubject) {
		boolean messageSubjectContainsReasonsTerms = false,		messageSubjectContainsReasonsIdentifierTerms= false, 	messageSubjectContainsStatesSubstates= false, 
				messageSubjectContainsProposalIdentifierTermList = false,	messageSubjectContainsPositiveWords= false,		 	messageSubjectContainsNegativeWords= false,
				messageSubjectContainsVerbTerms= false,			  		messageSubjectContainsEntitiesTerms= false,		  		messageSubjectContainsSpecialTerms= false,
				messageSubjectContainsDecisionTerms= false,				messageSubjectContainsProposalIdentifierTerms= false,	messageSubjectNegationFound= false;

		//if(proposalFoundInMessageSubject) {
		//more weight  if there are special terms here
		//}

		//CHECK MESSAGE SUBJECT ..added july 2018
		//for the moment, just output if matched, and if not matched later in below code, maybe work wih positive negative words
		String sentenceLocation="messageSubject",messageSubjectProbability=""; boolean matchWholeTerms = true;
		//p.setMessageSubject(messageSubjectProbability);		
		
		if(messageSubject==null || messageSubject.length() ==0 || messageSubject.isEmpty())    {}		//.toLowerCase()
		else {	//THIRD CHECK...CHECK FOR JUST THE REASON TERMS
			messageSubject=messageSubject.toLowerCase();
			String messageSubjectStatesSubstatesList = containsAllTermsListOfLists(messageSubject,labelList, matchWholeTerms);//allStatesList_StatesSubstates
			if(messageSubjectStatesSubstatesList == null || messageSubjectStatesSubstatesList.isEmpty() || messageSubjectStatesSubstatesList.equals("")  || messageSubjectStatesSubstatesList.length()==0) {}
			else {messageSubjectContainsStatesSubstates=true;	
				p.setMessageSubjectContainsStatesSubstates(messageSubjectContainsStatesSubstates); p.setMessageSubjectStatesSubstatesListCount(countTermsInString(messageSubjectStatesSubstatesList));
				System.out.println(" messageSubjectStatesSubstatesList: ("+ messageSubjectStatesSubstatesList + ")	 messageSubjectStatesSubstatesTermsCount: " + 
						countTermsInString(messageSubjectStatesSubstatesList));					
			}

			//most common is decision terms  used in message subject
			String messageSubjectDecisionTermList = containsAllTerms(messageSubject,decisionTerms, matchWholeTerms);
			if(messageSubjectDecisionTermList == null || messageSubjectDecisionTermList.isEmpty() || messageSubjectDecisionTermList.length()==0) {}
			else { messageSubjectContainsDecisionTerms=true;		
				p.setMessageSubjectContainsDecisionTerms(messageSubjectContainsDecisionTerms); p.setMessageSubjectDecisionTermListCount(countTermsInString(messageSubjectDecisionTermList));
				System.out.println(" messageSubjectDecisionTermList: ("+ messageSubjectDecisionTermList + ")  messageSubjectDecisionTermsCount: " +  
						countTermsInString(messageSubjectDecisionTermList));				
			}	

			//FOURTH CHECK...CHECK FOR JUST THE VERB TERMS
			String messageSubjectVerbList = containsAllTerms(messageSubject,verbs, matchWholeTerms);
			if(messageSubjectVerbList == null || messageSubjectVerbList.isEmpty() || messageSubjectVerbList.length()==0) {}
			else {	messageSubjectContainsVerbTerms=true;	
				p.setMessageSubjectContainsVerbTerms(messageSubjectContainsVerbTerms);
				System.out.println(messageSubjectVerbList); 
				
			}			

			//FIFTH CHECK...CHECK FOR JUST THE identifiersTermList TERMS		
			String messageSubjectProposalIdentifiersTermList = containsAllTerms(messageSubject,proposalIdentifiers, matchWholeTerms);
			if(messageSubjectProposalIdentifiersTermList == null || messageSubjectProposalIdentifiersTermList.isEmpty() || messageSubjectProposalIdentifiersTermList.length()==0) {}
			else {	messageSubjectContainsProposalIdentifierTermList=true;	
				p.setMessageSubjectContainsProposalIdentifierTerms(messageSubjectContainsProposalIdentifierTerms); p.setMessageSubjectProposalIdentifiersTermListCount(countTermsInString(messageSubjectProposalIdentifiersTermList));
				System.out.println("messageSubjectProposalIdentifiersTermList: ("+messageSubjectProposalIdentifiersTermList +") messageSubjectProposalIdentifiersTermCount: " + 
						countTermsInString(messageSubjectProposalIdentifiersTermList));				
			}	

			//SIXTH CHECK...CHECK FOR JUST THE identifiersTermList TERMS		
			String messageSubjectEntitiesTermList = containsAllTerms(messageSubject,entitiesList, matchWholeTerms);
			if(messageSubjectEntitiesTermList == null || messageSubjectEntitiesTermList.isEmpty() || messageSubjectEntitiesTermList.length()==0) {}
			else {	messageSubjectContainsEntitiesTerms=true;	
				p.setMessageSubjectContainsEntitiesTerms(messageSubjectContainsEntitiesTerms); p.setMessageSubjectEntitiesTermListCount(countTermsInString(messageSubjectEntitiesTermList));
				System.out.println("messageSubjectEntitiesTermList: (" + messageSubjectEntitiesTermList + ") messageSubjectEntitiesTermListCount: " +
						countTermsInString(messageSubjectEntitiesTermList));				
			}	

			//SEVENTH CHECK...CHECK FOR JUST THE special TERMS	- contains entities	
			String messageSubjectSpecialTermList = containsAllTerms(messageSubject,specialOSCommunitySpecificTerms, matchWholeTerms);
			if(messageSubjectSpecialTermList == null || messageSubjectSpecialTermList.isEmpty() || messageSubjectSpecialTermList.length()==0) { 
			//System.out.println("$$$$$$$$$$$4does not containsSpecialTerms: "+ containsSpecialTerms);	
			}
			else {	messageSubjectContainsSpecialTerms=true;
				p.setMessageSubjectContainsSpecialTerms(messageSubjectContainsSpecialTerms); p.setMessageSubjectSpecialTermListCount(countTermsInString(messageSubjectSpecialTermList));
				System.out.println("messageSubjectSpecialTermList: "+ messageSubjectSpecialTermList + ") messageSubjectEntitiesTermListCount: " +
						countTermsInString(messageSubjectSpecialTermList));				
			}	//System.out.println(identifiersTermList);	

			String finalListOfAllTermsFound = messageSubjectStatesSubstatesList + " " + messageSubjectProposalIdentifiersTermList + " " +messageSubjectVerbList + " "+messageSubjectEntitiesTermList 
					+ " "+messageSubjectSpecialTermList+ " "+messageSubjectDecisionTermList; 
			//return distinct words in finalListOfAllTermsFound
			ArrayList<String> uniqueList =new ArrayList<String>(); 
			String s[]=finalListOfAllTermsFound.split(" ");
			finalListOfAllTermsFound="";
			for (int i = 1; i < s.length; i++) {
				if (!(uniqueList.contains(s[i]))) {
					uniqueList.add(s[i]);    	finalListOfAllTermsFound = finalListOfAllTermsFound + " " + s[i];
				}
			}
			int noOfTerms = finalListOfAllTermsFound.trim().split(" ").length;
			
			//set message subject values
		}
		return p;
	}
	*/
	
	public static String[] removeNullOrEmpty(String[] a) {
	    String[] tmp = new String[a.length];
	    int counter = 0;
	    for (String s : a) {
	        if (s == null || s.isEmpty() || s.equals("")) {}
	        else {
	            tmp[counter++] = s;
	        }
	    }
	    String[] ret = new String[counter];
	    System.arraycopy(tmp, 0, ret, 0, counter);
	    return ret;
	}

	public static void processSectionOrMessage(boolean matchExactTerms, String label, String messageOrSection, Integer mid,String messageSubject, Date dateVal,Date dateOfStateMessage, Integer proposalNum, 
			String author, ClausIEMain ciem, boolean retrieveKeywords, boolean showCIEDebugOutput, boolean suggestCandidateSentences, String tableToStoreExtractedRelations, Connection conn, /*create_explicit_corpus cec,*/
			List<String> candidateSentencesList,ArrayList<StringPair> spList,String authorsRole, int dateDiff,String messageLocation,			
			boolean messageSubjectContainsStatesSubstates,boolean messageSubjectContainsDecisionTerms,boolean messageSubjectContainsVerbTerms,
			boolean messageSubjectContainsProposalIdentifierTerms,boolean messageSubjectContainsEntitiesTerms,boolean messageSubjectContainsSpecialTerms,
			int messageSubjectStatesSubstatesListCount, int messageSubjectDecisionTermListCount,int messageSubjectProposalIdentifiersTermListCount,
			int messageSubjectEntitiesTermListCount, int messageSubjectSpecialTermListCount,
			String messageSubjectStatesSubstatesList, String messageSubjectDecisionTermList, String	messageSubjectVerbList,
			String messageSubjectProposalIdentifiersTermList, String messageSubjectEntitiesTermList, String messageSubjectSpecialTermList, boolean anotherProposalMatchedInMsgSubject) {
			//double messageSubjectHintProbablityScore, double termsLocationHintProbability, double dateDiffProbability, double authorRoleProbability) {
		String[] paragraphs;		String location, previousSentenceString="", nextSentence="";
//		System.out.println("\tInside processSectionOrMessage: ");
		//remove the plus signs..which make the entire lines part of the same sentence
		//+    way to chain exceptions explicitly.  This PEP addresses both.
		//+
		//+    Several attribute names for chained exceptions have been suggested
		//System.out.println("messageOrSection "+messageOrSection);
		//Tabbed output, ew way

//$$		for (String line : messageOrSection.split("\\n"))	
//$$			System.out.println("\t"+line);
		
		
		
/*		
		//START messageOrSection LEVEL 
		System.out.println("messageOrSection "+messageOrSection);
		for (String s : states) {	//System.out.println("checking Status "+s);
			if(messageOrSection.toLowerCase().contains(s.toLowerCase())) {		//.toLowerCase()
					//model.addRow(new Object[]{dateVal, mid,"State Change",s, s,"Single line"});
					reasonRow r = new reasonRow();
//$$					r.setData(proposalNum, mid, dateVal, "Entire messageOrSection", "Entire messageOrSection", s, false, author);  //last 3 parameters are sentence, location and combination
//					rr.add(r);					r=null;
					//System.out.println("added Status "+s);
					System.out.format("%5s | %15s| %7s | %10s | %30s | %25s | %15s | %15s |", proposalNum,"label",mid,dateVal,s,"State","Previous message","reasonlabel");	System.out.println("Entire Message");
			}
		}	//end for loop for each state
*/		
		//Feb 2019...for all messages, proposal summary and nearby messages of states, we check if the message contains special terms, 
		//like 'BDFL Ponouncement' on a line alone and authorsrole = 'bdfl'
		//if so we give more weightage
		if(outputForDebug) {		
			System.out.println("ZZZZ SPECIAL TERM CHECKING ON A LINE ON ITS OWN: MID: "+mid);
			System.out.println(messageOrSection);
		}
		Boolean decisionMessage=false;
		String specialTerm="";
		outerloop:
		for (String line : messageOrSection.split("\\n"))	{
			if (line ==null || line.isEmpty()) { continue;}
			line = line.replaceAll("[^a-zA-Z ]", "").toLowerCase();	//remove all punctuation
			line = line.replaceAll("\\s+", " ").trim(); //remove double spaces and trim
			//line = line.replaceAll("\\?", "");   not sure what this is for
			//System.out.println("\t Line: "+line);
			for (ArrayList<String> st: specialOSCommunitySpecificTerms) { 
				specialTerm = st.toString(); 
				specialTerm = specialTerm.replaceAll("[^a-zA-Z ]", "").toLowerCase();	//remove all punctuation
				specialTerm = specialTerm.replaceAll("\\s+", " ").trim();		//remove double spaces and trim
				//System.out.println("\t Checking specialOSCommunitySpecificTerms : "+specialTerm);
				if(line.toLowerCase().equals(specialTerm.toLowerCase())) {	//special term should be on its own in a line
					System.out.println("ZZZZ FOUND SPECIAL TERM ON A LINE ON ITS OWN: "+line);
					decisionMessage=true; // messsageTypeIsReasonMessage
					break outerloop;
				}
			}
		}
		if(outputForDebug)
		System.out.println("ZZZZ decisionMessage: "+decisionMessage);
		//END messageOrSection LEVEL
		
		//FINALLY DECIDED WE JUST STRIP THESE CHARACTERS OFF..BEST SOLUTION. We check for states and special terms before as they contain plus (+) signs which are removed here or else they wont be captured
		//this is only for manual reason extraction and wont be part of automated process as here we look at entire messge but in automated proces we look at analyse words only 
		messageOrSection = messageOrSection.replaceAll("\r?\n\\+","\r\n");		messageOrSection = messageOrSection.replaceAll("\r?\n\\-","\r\n");
		messageOrSection = messageOrSection.replaceAll("\r?\n>","\r\n");		messageOrSection = messageOrSection.replaceAll("\r?\n >","\r\n");
		messageOrSection = messageOrSection.replaceAll("\r?\n>>","\r\n");		messageOrSection = messageOrSection.replaceAll("\r?\n> >","\r\n");	messageOrSection = messageOrSection.replaceAll("\r?\n> > >","\r\n");
		messageOrSection = messageOrSection.replaceAll("\r?\n ","\r\n");		messageOrSection = messageOrSection.replaceAll("\r?\n! ","\r\n");
		
		//START PARAGRAPH LEVEL
		paragraphs = messageOrSection.split("\\r?\\n\\r?\\n"); 	int paragraphCounter = 0;	//"\r?\n\r?\n");
		boolean foundInLastParagraph =false, processedMessage = false;
		
		//for(String para: paragraphs) { System.out.println("Paragraph: "+ "c" + " [" + para +"]");}
		
		//nov 2018, remove empty or null paragraphs - those that are just empty lines
		paragraphs = removeNullOrEmpty(paragraphs);
		
		//String[] paragraphs = doc.split("\\n\\n"); //"\\r?\\n\\r?\\n");		
		//String[] paragraphs = v_message.split("\\r?\\n\\r?\\n"); //mar 2018...new improved way implemented   "\\n\\n");
		
		//added oct 2018
		String paraMinusCurrSentence=""; //paragraph minus curr sentence		
		String firstParagraph = paragraphs[0],secondParagraph, thirdParagraph="", secondLastParagraph = "", lastParagraph=paragraphs[paragraphs.length-1], messageUntilLastParagraph="",
				previousParagraph="", twoParagraphsBack=""; //default values
		Integer totalParagraphs = paragraphs.length-1;
		
		if(totalParagraphs>0)
			secondParagraph = paragraphs[1];
		else 
			secondParagraph = paragraphs[0];
		//March 2019, add a third paragraph option to give probability
		if(totalParagraphs>1)
			thirdParagraph = paragraphs[2];
		else 
			thirdParagraph = paragraphs[0];
		
		//oct 2018, we want to cetagorize the message into types now
		//oct 2018...we want to get the last sentence in a message, therefore we have to first get the main message and then get the last sentence from that
		//we keep processing paragraphs until we come to a paragraph which has very few words and contains the firstname of the message author
		//also may have terms like 'regards', ..terms used in signature 
		//so we have a integer to store the index until that point
		author = author.toLowerCase();		
		//System.out.println("----- Now Processing new Message ID: ("+v_message_ID+") total paragraphs.." + paragraphs.length);
		Integer count=0,sentenceCounterinParagraph=1;        		   
		Boolean permanentMessageHasLabel = false;		//if message has any label captured set this to true
		Integer messageEndIndex= messageOrSection.length(); 
		boolean firstParagraphFound=false, secondParagraphFound=false,thirdParagraphFound=false, endFoundInMessage=false,endFoundInParagraph=false;
		String authorFirstName= "";
		if(author.contains(" ")) {
			//if (m.getAuthor().split(" ").length > 1) {
				authorFirstName = author.split(" ")[0];
			//}
		}
		else {
			authorFirstName = author;
		}
		author = author.toLowerCase(); authorFirstName = authorFirstName.toLowerCase();
		Levenshtein levenshtein = new Levenshtein();
        //System.out.println("levenshtein.distance "+ levenshtein.distance("apple", "appl3")   );
		String debug = ""; String endingSentimentWords[] = {"regards","best regards","cheers","thanks"};
		
		//nov 2018 ... we make one round to get the first and last paragraph
		int pcounter=0;
		//dec 2018
		boolean pepTextAttachedFound= false; //in pep 479, reason was attche in pep text in side mesage
		if(outputForDebug)
			System.out.println("START we make one round to get the first and last paragraph, author: " +author + " authorFirstName: " + authorFirstName);
		outerloop:  //nov, 2018...marker used to break 
		for(String para: paragraphs) { //System.out.println("Paragraph: "+pcounter + " [" + para +"]");
			if(para.equals("") || para.trim().length() <1) {	pcounter++;   continue; }
			para = para.toLowerCase();
			
			//mar 2019,,assign second para..we need secondpara sometimes instead for firstpara  
			if(firstParagraphFound && secondParagraphFound == false) {
				secondParagraph=para;	//as soon as first para is found (since second para not found means that it has just been found)
				secondParagraphFound=true;
			}
			//march 2019, we have to add the option for third paragraph
			if(secondParagraphFound && thirdParagraphFound == false) {
				thirdParagraph=para;	//as soon as first para is found (since second para not found means that it has just been found)
				thirdParagraphFound=true;
			}
			
			//if terms found in first loop
			if(previousParagraph.equals("") || previousParagraph.length() < 1)
				previousParagraph = para;  //just assign current para as previos para
						
			if (pcounter >= 500) {	//som,e messages like 328159 , have code and therefore about 10,000 paragraphs
				System.out.println("\tParagraph counter >= 500: breaking out "); break;
			}
			//oct 2018, find what type of ,message it is
			Integer l = para.split(" ").length;
			
			//get first paragraph, but not short ones (les than 5 terms)
			if(!endFoundInMessage && !firstParagraphFound && paragraphCounter<=4) {
				if(l > 5) {	//we dont consider short greetings at first paragraph
					//nov 2018 added check as these are headers
					//Author: jesse.noller
					//Date:
					if( (para.toLowerCase().contains("author: ") && para.toLowerCase().contains("date: ") )
						||
						(para.toLowerCase().contains("from: ") && para.toLowerCase().contains("date: ") )
					  )	{	} //these are not first paragraphs
					else {
						firstParagraph = para; 					firstParagraphFound=true; //System.out.println("firstParagraph: "+firstParagraph);
					}
				}
			}
			endFoundInParagraph = false; //end is
			//PEP: 443
			//Title: Single-dispatch generic functions
			//Version: $Revision$
			//Last-Modified: $Date$
			//some message sdont have any name towaards teh end, but teh author proposes a pep, so we just look for this combination
//			System.out.println("\nxxxxxxxxxxxpara " +para);
			
			if(para.contains("pep: ") && para.contains("title: ") && para.contains("version: ")) {
				lastParagraph = previousParagraph; endFoundInMessage=true;  //System.out.println("\npara3 " +para + "lastParagraph 2: "+lastParagraph);
				messageEndIndex = messageOrSection.indexOf(para);   //get index of paragraph
				//We had to comment this cos of pep 479 when reason was in the pep section
				//break; //we better break or else it will keep assigning if in the next round it enters any other if/else block
				//we counter by adding the clause in the if section
				pepTextAttachedFound = true;
			}
		
			//oct 2018 ..get last paragraph....lets check to see if its message ending
			if(!endFoundInMessage && l < 7) { //if end not found in message till yet, and length of paragraph is less than 7 terms
				String terms[] = para.split(" "); //get terms
				for (String t : terms) {
					t= t.toLowerCase();		t = t.replaceAll("[-+.^:,]",""); //remove special characters like: - + ^ . : ,
					if(t.contains("."))		t= t.replace(".", "");
					if(t.contains(","))		t= t.replace(",", "");
					
					for (String ew: endingSentimentWords) {	//paragraph with sentiment words, more likely to be end of message
						if(t.contains(ew)) {
							endFoundInMessage=true;	lastParagraph = previousParagraph;	//System.out.println("endingSentimentWords " +ew + " \nlastParagraph 1: "+lastParagraph);	//firstParagraphFound=true;
							secondLastParagraph = twoParagraphsBack;
							break outerloop; //we better break or else it will keep assigning if in the next round it enters any other if/else block
						}
					}
					
					if(t.contains(author) ||   t.contains(authorFirstName)) {	//firstname of author
						lastParagraph = previousParagraph; endFoundInMessage=true; 	 //System.out.println("author found in para " + para + "lastParagraph 3: "+lastParagraph);
						secondLastParagraph = twoParagraphsBack; //march 2019
						messageEndIndex = messageOrSection.indexOf(para);   //get index of paragraph
						break outerloop; //we better break or else it will keep assigning if in the next round it enters any other if/else block
					}
					//try levestein distance
					if(levenshtein.distance(t,authorFirstName) < 3) {
						lastParagraph = previousParagraph; endFoundInMessage=true; 	//System.out.println("author found in para ED " +para + "lastParagraph 4: "+lastParagraph);
						secondLastParagraph = twoParagraphsBack; //march 2019
						messageEndIndex = messageOrSection.indexOf(para);   //get index of paragraph
						break outerloop; //we better break or else it will keep assigning if in the next round it enters any other if/else block
					} 
					//debug = debug+t;
				}
			}
			pcounter++;  	
			twoParagraphsBack = previousParagraph;   //the paragraph before previous
			previousParagraph = para; 
//			System.out.println(pcounter+ " First Paragraph: "+firstParagraph);
//			System.out.println(pcounter+ " Last Paragraph: "+lastParagraph);
		} //System.out.println("END we make one round to get the first and last paragraph" );
		if(outputForDebug)
			System.out.println("END   we make one round to get the first and last paragraph");
		if(!endFoundInMessage) {
			//nov 2018, even after processing, we see last paragraph assigned to [from jack@oratrix.nl  fri aug 17 10:20:14 2001], thne we assign it to second last paragraph
			if(lastParagraph.trim().toLowerCase().startsWith("from ")) {
				lastParagraph = paragraphs[paragraphs.length-2]; //System.out.println("----------------");
			}else {
				lastParagraph = previousParagraph; //assign the last paragraph as the last paragraph
			}
		}
//		System.out.println("Extracted First Paragraph 5: "+firstParagraph);
//		System.out.println("Extracted Last Paragraph 5: "+lastParagraph);
				
		//nov 2018..we calculate message type..NOV 2018 ...MESSAGE TYPE CATEGORIZATION
		//we consider the first paragraph and last paragraph of that message
		//make sure curr sentence (and rest of paragraph), is not the first or last paragraph as to prevent double weightage being given
		//most of these messages seem to be having a mixture of terms..which may belong to all categories below.
		double messsageTypeIsReasonMessageProbabilityScore =0.0;
		//was good for initial, but now using decisionTerms
		//NOV 2018, we dont need these arrays anymore as we compare with the terms from file
		/*
		String requestPronouncementTerms[] = {"review, pronouncement","open issues","remaining issues","decision","discussing"};	//proposal author asking pronouncement
		//also add pep, 'guido'
		//ukasz, are there any open issues Otherwise I'm ready to accept the PEP.
		//these 3 arrays were good for initial, but now using states substates list
		String bdflPronouncementTerms[] = {"accept","reviewed"};	//bdfl reviewing
		String acceptRejectTerms[] = {"accept,accepting,accepted,add,approve,reject,rejecting,rejected"}; //during acceptance/rejection
		String acceptedRejectedTerms[] = {"accepted,rejected"};	//community reflection
		*/
		//MAR 2019, we try to capture BDFL/Delegate pronouncement messages
		String acceptRejectTerms[] = {"accept","accepting","accepted","add","approve","approving","reject","rejecting","rejected"}; //during acceptance/rejection
		
		boolean proposalAuthorAskingPronouncement= false,BDFLReviewing=false,BDFLPronouncement=false, communtyReflecting= false, messsageTypeIsReasonMessage=false;
		//we find out what type of message it is...authorAskingPrnouncement, BDFLAboutToPronounce, BDFLPronouncement, CommuntyReflecting
		// nov 2018, before we used to calculate this for only selected messages which had some probability, now we do for all messages and update the value in allmessages column
//				if (sentenceOrParagraphHintProbablity > 0.0 || restOfParagraphProbabilityScore > 0.0) { 
		String messageType="normalDiscussion";
		boolean  matchWholeTerms=true;
//		firstParagraph = firstParagraph.replaceAll("\\?", "");				lastParagraph = lastParagraph.replaceAll("\\?", ""); //do we need to do this?
//					System.out.println("firstParagraph a2: "+ firstParagraph);
//					System.out.println("lastParagraph a2: "+ lastParagraph);
		//we consider dates as well...
		//if before state commit date, 
			//if message author = pep author AND first sentence contains
				//terms = 'i, pep, proposal, request, pronounce, pronouncement'
		
			//if message author = bdfl or delegate 
				//if first or last paragraph contains 'pronouncement'
		
		//String decisionMembers[] = {"proposalAuthor","bdfl","coredeveloper","bdfl_delegate"}; //if message is written by these members, they get higher score
		//"authorsrole", "otherCommunityMember", "bdfl", "coredeveloper","pepeditors", "proposalAuthor", "bdfl_delegate"	
		//DATEDIFF('" + dateOfStateMessage + "',date2) as datediff = first parameter minus second
//		System.out.println("firstParagraph: "+firstParagraph+ " : ");
//		System.out.println("lastParagraph: "+lastParagraph+ " : ");
		if(outputForDebug)
			System.out.println("Computing MessageType");
		//nov 2018, changed from 0 to -7, as for pep 338 where datediff was -3 
		boolean foundRM = false;
		if (dateDiff >= -57){		
			if(outputForDebug)
				System.out.println("\tdateDiff >= 0");	 //before the state commit
			//AUTHOR REQUEST PRONOUNCEMENT
			if (authorsRole == null || authorsRole.isEmpty() || authorsRole.equals(""))   {
			 
			}
			else {
			if (authorsRole.equals("proposalAuthor")) {  	
				if(outputForDebug)
					System.out.println("\tproposalAuthor");
				/* for (String x : requestPronouncementTerms) {
	//				if(ps.containsAllTermsAsTheyAre(x,firstParagraph,matchWholeTerms) || ps.containsAllTermsAsTheyAre(x,lastParagraph,matchWholeTerms) || ps.containsAllTermsAsTheyAre(x,messageSubject,matchWholeTerms) ) { //pep 441,Request for Pronouncement:
	//					proposalAuthorAskingPronouncement = true;	messageType = "proposalAuthorAskingPronouncement"; System.out.println("proposalAuthorAskingPronouncement");
	//				}
					if(firstParagraph.contains(x) || lastParagraph.contains(x) || messageSubject.contains(x) ) {
						proposalAuthorAskingPronouncement = true;	messageType = "proposalAuthorAskingPronouncement"; System.out.println("proposalAuthorAskingPronouncement");
					}
				} */ //new way nov 2018
				for (ArrayList<String> line : decisionTerms) {    //for each list            	
//					System.out.println("Checking Line: "+line+ " : ");
		    		for (String term : line) {      	//for each term in list , each line has many sets of terms seprated by comma
		    			term = term.trim();
//		    			System.out.println("' checking "+term+ "' ");				    		
		    			if(term.contains("-")) term = term.replace("-", " ");		//some terms in list of list are a combination of double term ..so we split here
		    			if (ps.containsAllTermsAsTheyAre(term,firstParagraph, matchWholeTerms)  || ps.containsAllTermsAsTheyAre(term,lastParagraph, matchWholeTerms) 
		    					 || ps.containsAllTermsAsTheyAre(term,messageSubject, matchWholeTerms) 	) {	//subject+" "+verb+" "+object	
		    				proposalAuthorAskingPronouncement = true;	messageType = "proposalAuthorAskingPronouncement"; 
		    				foundRM=true; 
		    				if(outputForDebug)
		    					System.out.println("\tfound dt, proposalAuthorAskingPronouncement"); 
		    				break;
		    			}
		    		}	//System.out.println("");
				}
				//nov 2018, forgot to add main list..reasons terms				
				for (ArrayList<String> line : reasonsList) {    //for each list            	
//					System.out.println("Checking Line: "+line+ " : ");
		    		for (String term : line) {      	//for each term in list 
		    			term = term.trim();
//		    			System.out.println("' checking "+term+ "' ");				    		
		    			if(term.contains("-")) term = term.replace("-", " ");		//some terms in list of list are a combination of double term ..so we split here
		    			if (ps.containsAllTermsAsTheyAre(term,firstParagraph, matchWholeTerms)  || ps.containsAllTermsAsTheyAre(term,lastParagraph, matchWholeTerms) 
		    					 || ps.containsAllTermsAsTheyAre(term,messageSubject, matchWholeTerms) 	) {	//subject+" "+verb+" "+object	
		    				proposalAuthorAskingPronouncement = true;	messageType = "proposalAuthorAskingPronouncement"; 
		    				foundRM=true; 
		    				if(outputForDebug)	
		    					System.out.println("\tfound rl,"+term+" proposalAuthorAskingPronouncement"); 
		    				break;
		    			}
		    		}	//System.out.println("");
				}
				if(!foundRM) {	
					if(outputForDebug) 
					 System.out.println("\tNot Assigned"); 
				}
			}
			//bdfl or delegate	ASKING FOR REVIEW
			else if(authorsRole.contains("bdfl")) { 
				if(outputForDebug) 
					System.out.println("\tbdfl");
//				for (String x : bdflPronouncementTerms) {
//					if(ps.containsAllTermsAsTheyAre(x,firstParagraph,matchWholeTerms) || ps.containsAllTermsAsTheyAre(x,lastParagraph,matchWholeTerms) || ps.containsAllTermsAsTheyAre(x,messageSubject,matchWholeTerms) ) { //pep 441,Request for Pronouncement:
//							BDFLReviewing = true;  messageType = "BDFLReviewing"; System.out.println("BDFLReviewing");
//					}
					/* if(firstParagraph.contains(x) || lastParagraph.contains(x) || messageSubject.contains(x) ) {
						BDFLReviewing = true;  messageType = "BDFLReviewing"; System.out.println("BDFLReviewing");
					}
					*/
					//new way nov 2018
					outerLoopA:
					for (ArrayList<String> line : decisionTerms) {    //for each list            	
					//$$    		System.out.println("Checking Line: "+line+ " : ");
			    		for (String term : line) {      	//for each term in list 
			    			term = term.trim();
			    			//System.out.println("' checking "+term+ "' ");				    		
			    			if(term.contains("-")) term = term.replace("-", " ");		//some terms in list of list are a combination of double term ..so we split here
			    			if (ps.containsAllTermsAsTheyAre(term,firstParagraph, matchWholeTerms)  || ps.containsAllTermsAsTheyAre(term,lastParagraph, matchWholeTerms) 
			    					 || ps.containsAllTermsAsTheyAre(term,messageSubject, matchWholeTerms) 	) {	//subject+" "+verb+" "+object	
			    				BDFLReviewing = true;  messageType = "BDFLReviewing"; 
			    				if(outputForDebug)
			    					System.out.println("\tfound dt, BDFLReviewing"); 
			    				foundRM=true; 
			    				break outerLoopA;
			    			}
			    		}	//System.out.println("");
					}
					outerLoopB:
					for (ArrayList<String> line : labelList) {    //for each list   //$$    		System.out.println("Checking Line: "+line+ " : ");
			    		for (String term : line) {      	//for each term in list 
			    			term = term.trim();
			    			//System.out.println("' checking "+term+ "' ");				    		
			    			if(term.contains("-")) term = term.replace("-", " ");		//some terms in list of list are a combination of double term ..so we split here
			    			if (ps.containsAllTermsAsTheyAre(term,firstParagraph, matchWholeTerms)  || ps.containsAllTermsAsTheyAre(term,lastParagraph, matchWholeTerms) 
			    					 || ps.containsAllTermsAsTheyAre(term,messageSubject, matchWholeTerms) 	) {	//subject+" "+verb+" "+object	
			    				BDFLReviewing = true;  messageType = "BDFLReviewing"; 
			    				if(outputForDebug)
			    					System.out.println("\tfound ll, BDFLReviewing"); 
			    				foundRM=true; 
			    				break outerLoopB;
			    			}
			    		}	//System.out.println("");
					}
					//nov 2018, forgot to add main list..reasons terms
					outerLoopC:
					for (ArrayList<String> line : reasonsList) {    //for each list            	
//						System.out.println("Checking Line: "+line+ " : ");
			    		for (String term : line) {      	//for each term in list 
			    			term = term.trim();
//			    			System.out.println("' checking "+term+ "' ");				    		
			    			if(term.contains("-")) term = term.replace("-", " ");		//some terms in list of list are a combination of double term ..so we split here
			    			if (ps.containsAllTermsAsTheyAre(term,firstParagraph, matchWholeTerms)  || ps.containsAllTermsAsTheyAre(term,lastParagraph, matchWholeTerms) 
			    					 || ps.containsAllTermsAsTheyAre(term,messageSubject, matchWholeTerms) 	) {	//subject+" "+verb+" "+object	
			    				proposalAuthorAskingPronouncement = true;	messageType = "BDFLReviewing"; 
			    				if(outputForDebug)
			    					System.out.println("\tBDFLReviewing"); 
			    				foundRM=true; 
			    				break outerLoopC;
			    			}
			    		}	//System.out.println("");
					}
					
//				}
				//System.out.println("firstParagraph a2: "+ firstParagraph);
				//System.out.println("lastParagraph a2: "+ lastParagraph);
			} //bdfl or delegate
			else {//if (authorsRole.equals("coredeveloper")) { //coredeveloper  any other member ..saying things like..why is it hard to reach consensus....
//						for (String x : requestPronouncementTerms) {
//							if(firstParagraph.contains(x) || lastParagraph.contains(x))
//								proposalAuthorAskingPronouncement = true;
//						}
			}
			}
		}			    
		if (foundRM == false && dateDiff == 0) { 
			if(outputForDebug)
				System.out.println("\tdateDiff == 0");	
			// BDFL ACCEPTING OR REJECTING
			if (authorsRole == null || authorsRole.isEmpty() || authorsRole.equals(""))   {
			
			}
			else {
				if(authorsRole.contains("bdfl")) {  
					if(outputForDebug)
						System.out.println("\tbdfl 2");
	//					System.out.println("\tfirstParagraph: " +firstParagraph);		System.out.println("\tsecondParagraph: "+secondParagraph);		System.out.println("\tlastParagraph: " + lastParagraph);
					//WE WANT TO IDENTIFY BDFL PRONOUNCEMENT / BDFL DELEGATE PRONOUNCEMENT
					/*
					for (String x : acceptRejectTerms) {
						if(ps.containsAllTermsAsTheyAre(x,firstParagraph,matchWholeTerms) || ps.containsAllTermsAsTheyAre(x,lastParagraph,matchWholeTerms) || ps.containsAllTermsAsTheyAre(x,messageSubject,matchWholeTerms) ) { //pep 441,Request for Pronouncement:
								BDFLPronouncement = true; messageType = "BDFLPronouncement"; System.out.println("BDFLPronouncement");
						}
						if(firstParagraph.contains(x) || lastParagraph.contains(x) || messageSubject.contains(x) ) {
							proposalAuthorAskingPronouncement = true;	messageType = "proposalAuthorAskingPronouncement"; System.out.println("proposalAuthorAskingPronouncement");
						}
					}  //new way nov 2018
					*/
					//outerLoopD:
					//for (ArrayList<String> line : labelList) {    //for each list   //$$    		System.out.println("Checking Line: "+line+ " : ");
			    		for (String term : acceptRejectTerms) {      	//for each term in list 
			    			term = term.trim();
	//		    			System.out.println("' checking terms ");			    			System.out.print(term+ ", ");		    			System.out.println();
			    			if(term.contains("-")) term = term.replace("-", " ");		//some terms in list of list are a combination of double term ..so we split here
			    			if (ps.containsAllTermsAsTheyAre(term,firstParagraph, matchWholeTerms)  
			    					 || ps.containsAllTermsAsTheyAre(term,secondParagraph, matchWholeTerms) 
			    					 || ps.containsAllTermsAsTheyAre(term,lastParagraph,   matchWholeTerms) 
			    					 || ps.containsAllTermsAsTheyAre(term,messageSubject,  matchWholeTerms) 	) {	//subject+" "+verb+" "+object	
			    				BDFLPronouncement = true;  messageType = "BDFLPronouncement"; 
			    				//if(outputForDebug)
			    					System.out.println("BDFLPronouncement");
			    				foundRM=true;	break;
			    			}
			    		}	//System.out.println("");
					//}
					outerLoopE:					
					for (ArrayList<String> line : labelList) {    //for each list   //$$    		System.out.println("Checking Line: "+line+ " : ");
			    		for (String term : line) {      	//for each term in list 
			    			term = term.trim();
			    			//System.out.println("' checking "+term+ "' ");				    		
			    			if(term.contains("-")) term = term.replace("-", " ");		//some terms in list of list are a combination of double term ..so we split here
			    			if (ps.containsAllTermsAsTheyAre(term,firstParagraph, matchWholeTerms)  || ps.containsAllTermsAsTheyAre(term,lastParagraph, matchWholeTerms) 
			    					 || ps.containsAllTermsAsTheyAre(term,messageSubject, matchWholeTerms) 	) {	//subject+" "+verb+" "+object	
			    				BDFLReviewing = true;  messageType = "BDFLReviewing"; 
			    				if(outputForDebug)
			    					System.out.println("\tBDFLReviewing"); 
			    				foundRM=true;
			    				break outerLoopE;
			    			}
			    		}	//System.out.println("");
					}
				} 
		}
		}
		if (foundRM == false && dateDiff < -7) {  
			if(outputForDebug)
				System.out.println("\telse ..datediff < 0"); //if datediff < 0	..other community members review decision	
			//if after state commit date		
			//if message author = othermember
			//then its a reflection message..message just to reflect
			/*for (String x : acceptedRejectedTerms) {
//				if(ps.containsAllTermsAsTheyAre(x,firstParagraph,matchWholeTerms) || ps.containsAllTermsAsTheyAre(x,lastParagraph,matchWholeTerms) || ps.containsAllTermsAsTheyAre(x,messageSubject,matchWholeTerms) ) { //pep 441,Request for Pronouncement:
//						communtyReflecting = true;	messageType = "communtyReflecting";  System.out.println("else --- communtyReflecting");
//				}
				if(firstParagraph.contains(x) || lastParagraph.contains(x) || messageSubject.contains(x) ) {
					communtyReflecting = true;	messageType = "communtyReflecting";  System.out.println("else --- communtyReflecting");
				}
			}
			*/  //new way nov 2018
			for (ArrayList<String> line : labelList) {    //for each list   //$$    		System.out.println("Checking Line: "+line+ " : ");
	    		for (String term : line) {      	//for each term in list 
	    			term = term.trim();
	    			//System.out.println("' checking "+term+ "' ");				    		
	    			if(term.contains("-")) term = term.replace("-", " ");		//some terms in list of list are a combination of double term ..so we split here
	    			if (ps.containsAllTermsAsTheyAre(term,firstParagraph, matchWholeTerms)  || ps.containsAllTermsAsTheyAre(term,lastParagraph, matchWholeTerms) 
	    					 || ps.containsAllTermsAsTheyAre(term,messageSubject, matchWholeTerms) 	) {	//subject+" "+verb+" "+object	
	    				communtyReflecting = true;	messageType = "communtyReflecting";  
	    				if(outputForDebug) 
	    					System.out.println("\telse --- communtyReflecting");
	    			}
	    		}	//System.out.println("");
			}
		}
		if(outputForDebug)
			System.out.println("END Computing MessageType");
//				}
		//update message with messagetype
		try {
			updateTableWithMessageType_UpdateMessageType(conn,mid,proposalNum, messageType); 
		} catch (SQLException e) {	System.out.println("stuck somewhere mid 87 "+ mid);		}
		  catch (Exception e) {		System.out.println("stuck somewhere mid 86 "+ mid);		}
		//else we see what kind of message it is, and assign new category
		
		//i am certain that atleast a reason term should be tehre in teh message to trigger this message type categorisation
		//but we only categorise, if sentence probability or ROP probability >0
		//categorise messsage into type
	    messsageTypeIsReasonMessage=false; //just to make sure we make it false
	    if(proposalAuthorAskingPronouncement || BDFLReviewing ||BDFLPronouncement ||communtyReflecting || decisionMessage) { //decisionMessage added feb 2019
			//in case of any of the above, we set the messsageTypeReasonMessage to true
			messsageTypeIsReasonMessage=true; messsageTypeIsReasonMessageProbabilityScore = 0.9;  //System.out.println("\n inside here 342");
	    }else {
	    	messsageTypeIsReasonMessage=false; messsageTypeIsReasonMessageProbabilityScore = 0.0;
	    }
	    
	    //Feb 2019, we computer the rest of teh message data and update the record we just wrote in the previous callig function
	    location = "message";
		ScoresToSentences p0 = new ScoresToSentences();		p0.setMessageContainsSpecialTerm(decisionMessage);
		double messageContainsSpecialTermProbabilityScore= 0.0;
		if(decisionMessage) 
			messageContainsSpecialTermProbabilityScore= 0.9; //p0.setMessageContainsSpecialTermProbabilityScore(0.9); //we give weightage to the message here, if the message contains special terms	//set reason message
		else 
			messageContainsSpecialTermProbabilityScore = 0.0; //p0.setMessageContainsSpecialTermProbabilityScore(0.0);			//set that its not reason message
		
		//p0.setMesssageTypeIsReasonMessage(messsageTypeIsReasonMessage);
		//p0.setMessageType(messageType);
		//p0.setMesssageTypeIsReasonMessageProbabilityScore(messsageTypeIsReasonMessageProbabilityScore);
		//messageTypeIsReasonMessage
		try {
			String updateQuery = "update "+tableNameToStoreMessage+" set messageTypeIsReasonMessage = ?, messageType= ?, "
					+ " messageTypeIsReasonMessageProbabilityScore=?, messageContainsSpecialTermProbabilityScore=?" //, dateDiffProbability=?,authorroleprobability "
					+ " where proposal = ? and messageID = ? ";
			PreparedStatement preparedStmt = conn.prepareStatement(updateQuery);
			preparedStmt.setBoolean (1, messsageTypeIsReasonMessage);		  preparedStmt.setString(2, messageType);	preparedStmt.setDouble (3, messsageTypeIsReasonMessageProbabilityScore);
			preparedStmt.setDouble (4, messageContainsSpecialTermProbabilityScore);    //preparedStmt.setDouble (5, dateDiffProbability);	preparedStmt.setDouble (6, authorroleprobability);
			preparedStmt.setInt (5, proposalNum);	preparedStmt.setInt (6, mid);	
			Integer i = preparedStmt.executeUpdate();
			if(i>0) {
				   if(outputForDebug)
			       System.out.println("update success");
			}
			else  
			       System.out.println("No pep and messageid found for update: " + mid + " NewPEP " + proposalNum);
			updateQuery =null;
		} catch (SQLException e) {	System.out.println("stuck somewhere mid 5634 "+ mid);		}
		  catch (Exception e) {		System.out.println("stuck somewhere mid 56543"+ mid);		}
		//end feb 2019
	    
	    //End computing message type 
		boolean isFirstParagraph=false, isSecondParagraph= false, isThirdParagraph= false, isSecondLastParagraph = false, isLastParagraph = false;
		
		//Main reason checking
		previousParagraph=""; //Feb 2019, note we reuse the previousParagraph from abobe code section
		char kMinus1, k, kPlus1, kPlus2;
		StringBuilder sb, sb2;
		ArrayList<String> listofTermsMatched = new ArrayList<String>();
		
		for (String para: paragraphs) 
		{ 	//System.out.println("mid "+mid+", processing paragraph "+paragraphCounter+" in para "+ para); 	
			if (paragraphCounter >= 500)	//som,e messages like 328159 , have code and therefore about 10,000 paragraphs
				break;
			
			//aug 2018 ...snlp stops when it encounters dots as fullstops like these...we therefore remove the dot in '.py'
			//' Perhaps also let you  
			//  choose an arbitrary .py file and scan for 'distutils' -- but I've....' 
			//remove every instance of 'dot directly followed by a letter'
				//entireParagraph = entireParagraph=entireParagraph.replaceAll("\\.\\W", "");  this doesnt work as it affects sentences being separated properly
			//find all indexes of dots, and check the character after if its letter, then remove the dot
			para = removeDotsDependinOnLocation(para);
			
//			System.out.println("mid "+mid+", processing paragraph "+paragraphCounter+" in para "+ para); 
			
			
			/*
			Reader reader = new StringReader(para);
			DocumentPreprocessor dp = new DocumentPreprocessor(reader);
			
			//after we have processed each sentence in the paragraph, we check against the entire paragraph for terms spanning across
			//but at the same time dont include theose terms found together in the same sentence
			for (List<HasWord> eachSentence : dp) {		
				String CurrentSentenceString = Sentence.listToString(eachSentence);
			*/
			//new code oct 2018
			Span[] spans = detector.sentPosDetect(para);   //Detecting the position of the sentences in the paragraph  
			      
				//Printing the sentences and their spans of a paragraph
			String CurrentSentenceString="",sentenceString="";
			int option =0; //check if text contains other peps only 
			boolean allowZero = false,ifSentenceContainsOnlyOtherPepNumbers = false;	
			
			for (Span span : spans) {        
				CurrentSentenceString = para.substring(span.getStart(), span.getEnd()); //System.out.println("\tCurrentSentenceString: "+CurrentSentenceString);
				paraMinusCurrSentence = para.replace(CurrentSentenceString, "");
				
				//find out if curr sentence is in first paragraph or last paragraph
//				System.out.println("\tfirstParagraph: "+firstParagraph);
//				System.out.println("\tlastParagraph: "+lastParagraph);
				if(outputForDebug)
					System.out.println("CurrentSentenceString 12: "+CurrentSentenceString);
				if (CurrentSentenceString.split(" ").length <2) {	//if there is only one term, we dont consider
				//	continue;
				}
				
				if(firstParagraph.contains(CurrentSentenceString.toLowerCase())) {				isFirstParagraph= true;		/*System.out.println("\nisFirstParagraph= true"); */	}
				else {isFirstParagraph= false;	}
				//added march 2019
				if(secondParagraph.contains(CurrentSentenceString.toLowerCase())) {				isSecondParagraph= true;		/*System.out.println("\nisFirstParagraph= true"); */	}
				else {isSecondParagraph= false;	}				
				if(lastParagraph.contains(CurrentSentenceString.toLowerCase())) {				isLastParagraph= true;		/*System.out.println("\nisLastParagraph= true"); */		}
				else {isLastParagraph= false;	}
			
				//remove unnecessary words like "Python Update ..."
				CurrentSentenceString = pm.removeUnwantedText(CurrentSentenceString);		//a lot of things are removed here which may be important ..like punctuation for finding out of sentence is code or not		
				CurrentSentenceString = pm.removeLRBAndRRB(CurrentSentenceString);
				//System.out.println("here 201");
				sentenceString  = CurrentSentenceString;
				//NOTE uncomment the follwing line if using SNLP
				//String sentenceString = Sentence.listToString(eachSentence);	//System.out.println("Sentence: "+ sentenceString);
				//sentenceString = 	sentenceString.replaceAll(".", "");		//we remove the fullstop as if a term we looking for ends with fullstop, it wont match our 'whole' term matcher
				//sentenceString = 	sentenceString.replaceAll(",", "");		//we remove the fullstop as if a term we looking for ends with fullstop, it wont match our 'whole' term matcher
			 
				//Sentence = Marked PEP 391 as Accepted.  ....IF A TERM WE LOOKING FOR, eg 'accepted' ENDS with fullstop, it wont match as entire  words are matched
				//sentenceString = sentenceString.replaceAll("[^a-zA-Z ]", "").toLowerCase();	
				//sentenceString = sentenceString.replaceAll("\\p{Punct}", "");
				sentenceString = sentenceString.replaceAll("\\p{P}", " "); //remove all punctuation ..maybe we can use this here (term matching for candidates) but not in CIE triple extraction as it wmay need commas and other punctuation
				sentenceString = sentenceString.replaceAll("\\s+", " ").trim(); //remove double spaces and trim
				//System.out.println("here 202");
//				System.out.println("Sentence : "+ sentenceString);
				//july 2018 added to skip email senders <fb6fbf560703031757y4312ffdax5db78050d6914859@mail.gmail.com>
				//<bbaeab100703032043y17fbddb9id68ff4b31b36986@mail.gmail.com>
				if(sentenceString.startsWith("<") && sentenceString.endsWith(">")) {
					System.out.println("\n sentenceString.startsWith < && sentenceString.endsWith > continue "); continue;
				}//	System.out.println("\n here 9");	
				//if we want to check in nearby paragraphs , previous, curr, and next

				//check current sentence for all combinations
				location= "sentence";	//System.out.println("New Sentence: "+ sentenceString); 
				//first we should check if sentence contains only that pep....and make sure not to include pep zero here...pep zero only in message subject
				option =0; //check if text contains other peps only 
				allowZero = false;	ifSentenceContainsOnlyOtherPepNumbers = false;					
				ifSentenceContainsOnlyOtherPepNumbers = prp.getPms().checkTextContainsProposalNumber_WithOptions(sentenceString.toLowerCase(), proposalNum,option,allowZero);
				//System.out.println("here 203");
				if(ifSentenceContainsOnlyOtherPepNumbers) { //if text contains only other peps, we dont consider, and continue to next sentence
					if(outputForDebug)
						System.out.println("\n SentenceContainsOnlyOtherPepNumbers Skipping," + ifSentenceContainsOnlyOtherPepNumbers + " sentence "+sentenceString);				
					continue; //skip the sentence
				}
				//oct 2018 added code ..why did we add this?? nov 2018
				//If another Proposal Matched In Msg Subject, we may not consider
				option=1; boolean ifSentenceContainsCurrentPepNumber = false;  //option = 1, check current pep	
				//but if curr sentence contains current pep number, we consider
				ifSentenceContainsCurrentPepNumber = prp.getPms().checkTextContainsProposalNumber_WithOptions(sentenceString.toLowerCase(), proposalNum,option,allowZero);
				if(anotherProposalMatchedInMsgSubject) {  
					if(outputForDebug)
						System.out.println("\n Another Proposal Matched In Msg Subject ("+messageSubject+")"); 
					if(ifSentenceContainsCurrentPepNumber) //consider
					{}	//proceed =true;
					else { //dont consider
						 if(outputForDebug)
							 System.out.println("\n ifSentenceContainsCurrentPepNumber"); 	 continue; //exit here..dont processa ny further
					}
				}
				//System.out.println("here 204");	
				//We dont want to repeat what has been found in sentence, in the paragraph again 
				//so for each sentence we add the terms found to list
//				System.out.println("Reached Sentence: "+ sentenceString);
//				sentenceString = ps.ifTextIsSentence_ReturnSentence(sentenceString); 

				if (sentenceString==null || sentenceString == "" || sentenceString.isEmpty() ) {
					//System.out.println("Sentence Skipped: "+ sentenceString); 
					continue;  
				}
				else {
					if(retrieveKeywords) {
							System.out.println("\nretrieveKeywords ");							 						     
						 try {
							 if (sentenceString.split(" ").length < 75) {}
					         else {continue;} //next sentence
							 showCIEDebugOutput=false;
//$							 String tripleArray[][] = ciem.computeClausIE(sentenceString, showCIEDebugOutput);
							 //extract badn insert into db table agt the same time
//$							 ciem.extractClauseIERelations(conn,sentenceString, "","", para,"","",proposalNum, mid, author, dateVal, tableToStoreExtractedRelations,false );

							 //we dont want to do this for each sentence in every nearby message
							 //it would give better reasults if its done in the state message
/*							 if(processedMessage == false) {
								 cec.docWordCounter(messageOrSection);
							 }	
							 processedMessage=true;
							 //pass it to verb pair, etc functions
							 cec.appendToCorpus(sentenceString);
*/							 
						} //catch (IOException e) {
							// TODO Auto-generated catch block
							//e.printStackTrace();
						//} 
						 catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}	 
					}
					else if(suggestCandidateSentences) {						//System .out.println("\nsuggestCandidateSentences: ");
						//Create an Array List
				        						
//						System .out.println("Sentence Not Skipped: "+ sentenceString);
						//make sure sentence is not checked before..too many instances of same sentence show up as candidate, one after another
						//dec 2018, we need this for pep 479..also not sure we need this since different messages have different probabilities regardless if they share same sentence
						boolean found= false;
						/* for (StringPair sp : spList) { 
							if (sp.getLabel().equals(label) && sp.getSentenceOrParagraph().equals(sentenceString)) {
								found=true; //* System.out.println("\n code break here 11221"); 	
								break;
							}
						} */
						//we do several things here
						//1. we continue the old way of calculating probabilities and writing to db table
						//2. we do new way by writing all weights to db table for machine learning
						if(!found) { //System.out.println("\n found b4 = false");
							//check current sentence
							location= "sentence";
							//START OLD WAY
							//oct 2018..now we check the sentence and nearby sentences as well, better to check the entire paragraph
							//String paraMinusCurrSentence = para.replace(sentenceString,"");
//							System.out.println("\nlastParagraph b4 assigning 2: "  + lastParagraph + " first " + firstParagraph);
//							System.out.println("ZZZZ 2 decisionMessage: "+decisionMessage);
							
							checkSentenceOrParagraphForReasonTermsCombinations(conn, paragraphCounter,totalParagraphs, label,matchExactTerms,mid,messageSubject,proposalNum, dateVal,dateOfStateMessage,
									sentenceString,firstParagraph, para,secondLastParagraph, lastParagraph, isFirstParagraph,isSecondParagraph,isThirdParagraph, isSecondLastParagraph,isLastParagraph,  
									previousParagraph, paraMinusCurrSentence,location, authorsRole,dateDiff,messageLocation,									
									messageSubjectContainsStatesSubstates,messageSubjectContainsDecisionTerms,  messageSubjectContainsVerbTerms,
									messageSubjectContainsProposalIdentifierTerms, messageSubjectContainsEntitiesTerms, messageSubjectContainsSpecialTerms,
									messageSubjectStatesSubstatesListCount, messageSubjectDecisionTermListCount, messageSubjectProposalIdentifiersTermListCount,
									messageSubjectEntitiesTermListCount, messageSubjectSpecialTermListCount,
									messageSubjectStatesSubstatesList, messageSubjectDecisionTermList, messageSubjectVerbList,
									messageSubjectProposalIdentifiersTermList, messageSubjectEntitiesTermList, messageSubjectSpecialTermList,messageType,messsageTypeIsReasonMessage, 
									messsageTypeIsReasonMessageProbabilityScore,decisionMessage,0.8); //v_sentenceLocationHintProbability = sentence 							
							//END FIRST WAY
							//SECOND WAY IS DONE IN WRITETODATABASE Function ABOVE							
							candidateSentencesList.add(sentenceString);	//add sentence to List
							StringPair spair = new StringPair(label, sentenceString);
							spList.add(spair);
							//check the combination of current sentence + previous sentence ...some terms span across two adjacent sentences like pep 391 acceptance reason
							//nov 2018, we dont consider adjacent sentences, we handle that in rest of paragraph							
						}
					}
				}
				previousSentenceString = CurrentSentenceString;
				spans=null;
			}//for each sentence
			//check current paragraph for all combinations
			//maintain a list of terms matched within the sentences of paragraph and then dont add them if found in paragraph
			location= "paragraph";	//System.out.println("\n\nparagraph:"+para+" \n\n");
			boolean foundp= false;
			/*for (StringPair sp : spList) { 
				if (sp.getLabel().equals(label) && sp.getSentenceOrParagraph().equals(para)) {
					foundp=true; 	break;
				}
			} */
			
			//Feb 2019, Message Level	- we use this now
            //nov 2018, we dont check paragraphs anymore, but we check rest of paragraph of a sentence which contais reasons
			//if(!foundp) {  // why paragraph check is not implemented..for pep 3133 it is needed, maybe it is commented because too many false positives
//$$		System.out.println("DDDD Check Paragraph For ReasonTerms Combinations CalculateProbabilityScoreWriteToDb:");
			//we not checking paragraphs this way
			if (outputForDebug)
				System.out.println("Checking Paragraph For ReasonTerms Combinations: ("+ para + ")"); 
			para=para.trim();
			if(para.startsWith("<") && para.endsWith(">")) {}
			else {
				checkSentenceOrParagraphForReasonTermsCombinations(conn,paragraphCounter,totalParagraphs, label, matchExactTerms,mid,messageSubject,proposalNum, dateVal,dateOfStateMessage,
					para, firstParagraph, para,secondLastParagraph,lastParagraph,isFirstParagraph,isSecondParagraph,isThirdParagraph,isSecondLastParagraph,isLastParagraph,previousParagraph,paraMinusCurrSentence,location, authorsRole,dateDiff,messageLocation,		//paraMinusCurrSentence = empty				
					messageSubjectContainsStatesSubstates,messageSubjectContainsDecisionTerms, messageSubjectContainsVerbTerms,
					messageSubjectContainsProposalIdentifierTerms,messageSubjectContainsEntitiesTerms, messageSubjectContainsSpecialTerms,
					messageSubjectStatesSubstatesListCount, messageSubjectDecisionTermListCount, messageSubjectProposalIdentifiersTermListCount,
					messageSubjectEntitiesTermListCount, messageSubjectSpecialTermListCount,
					messageSubjectStatesSubstatesList, messageSubjectDecisionTermList, messageSubjectVerbList,
					messageSubjectProposalIdentifiersTermList, messageSubjectEntitiesTermList, messageSubjectSpecialTermList,
					messageType,messsageTypeIsReasonMessage, messsageTypeIsReasonMessageProbabilityScore,decisionMessage,0.8);	//v_sentenceLocationHintProbability = paragraph gets lowest probability 
				//p.calculateAllProbabilities();
				//p.writeToDatabase(conn, tableNameToStore );
				//termsLocationWithMessageProbabilityScore=0.0 as its not yet determined and would be determined later, unlike state messages);
				//messageSubjectHintProbablityScore,termsLocationHintProbability,dateDiffProbability, authorRoleProbability);
				candidateSentencesList.add(para);
			} //end else
			//} 
			//have to code adjacent paragraphs

			paragraphCounter++;								
			listofTermsMatched.clear(); //clear list for each paragraph
			previousParagraph = para;
		}//for each paragraph
		
		
		//System.out.println("\tMessage Processed: " + mid);
	}

	private static String removeDotsDependinOnLocation(String para) {
		char kMinus1, k,kPlus1,kPlus2;
		StringBuilder sb, sb2;
		for(int s=2; s< para.length()-3; s++) {		//no sentence would be 3 chars long only, so we can have thuis value as 3
			kMinus1 = para.charAt(s-1);	
			k = para.charAt(s);	//entireParagraph.substring(s,s+1);
			kPlus1 = para.charAt(s+1);	//entireParagraph.substring(s,s+2);	
			kPlus2 = para.charAt(s+2);	//entireParagraph.substring(s,s+3);
			//System.out.println("xxxxxxx k (" +k + ") kplus1 (" + kPlus1+ ") kplus2 ("+ kPlus2+ ")" );
			if(  k=='.' && Character.isLetter(kPlus1)) {		//kMinus1 == ' ' &&
				sb = new StringBuilder();	sb2 = new StringBuilder();
			    sb.append(kMinus1);		sb.append(k);  	 sb.append(kPlus1);
			    sb2.append(kMinus1);	sb2.append(' '); sb2.append(kPlus1);
			    String str = sb.toString(),str2 = sb2.toString();					
			    para = para.replace(str, str2);
				//entireParagraph= entireParagraph.substring(0,k)+" "+entireParagraph.substring(k);
//					System.out.println("@@@@@@@@@@@@@@@@@@@@@ kMinus1 ("+kMinus1+") k(" +k + ") kplus1 (" + kPlus1+ ") kplus2 ("+ kPlus2+ ")" );
			}
		//	int dot1 = input.indexOf(".");
		//	int dot2 = input.indexOf(".", dot1 + 1);
			//String substr = input.substring(0, dot2);
		}
		return para;
	}
	
	//we update all messagetype in the allmessages table
	public static void updateTableWithMessageType_UpdateMessageType(Connection conn, Integer mid, Integer proposal, String messageType) throws SQLException {
		  String updateQuery = "update allmessages set messageType = ? where pep = ? and messageID = ?";
		  PreparedStatement preparedStmt = conn.prepareStatement(updateQuery);
		  preparedStmt.setString (1, messageType);			  preparedStmt.setInt(2, proposal); preparedStmt.setInt(3, mid);
		  int i = preparedStmt.executeUpdate();
		  if(i>0)   { 		          //System.out.println("updated authorsrole ="+authorRole+" where messageID = "+ mid);				  
		  }
		  else  {	         System.out.println("stuck somewhere mid " + mid + " messageType  " + messageType);		  }
		  updateQuery =null;
	}
	
	public static String containsAllTerms(String sentenceorParagrapghString, List<String> list, boolean matchWholeTerms) {
		String combination = "",finalCombination="";
		ArrayList<String> listofTermsMatched = new ArrayList<String>();
		for (String r : list ) {
			r = r.trim();
			//check state,identifier, and reasons only
//			if(r.split(" ").length ==1) { continue;}	//dont capture single words reasons here as too many false positives ..but they are in the list because they will be used in combination
			if (ps.containsAllTermsAsTheyAre(r,sentenceorParagrapghString, matchWholeTerms)) {	//subject+" "+verb+" "+object
//				combination = r;						
//				if(location.equals("sentence") && listofTermsMatched.contains(combination))	//paragraph
//				{}
//				else {	//sentence
					finalCombination= finalCombination + "," + r; 
					//insertReasonTermsFoundRow(proposal,mid, dateVal, sentenceString, location, combination);
//					reasonRow r1 = new reasonRow(); 	r1.setData(proposal, mid, dateVal, sentenceorParagrapghString, location, combination); 		rr.add(r1);	r1=null;
//					listofTermsMatched.add(r);
//					System.out.println("\t mid "+mid+" new terms [" + r + "] found in "+location+". Paragraph "+paragraphCounter); 
					//System.out.format("%5s | %30s| %7s | %15s | %30s | %25s | %15s | %15s |%15s |", "pepNo","State/Substate","MID","Date","Reason Identifier Terms","Actual Reasons","Location","Reason Label", probability);System.out.println("");
//				}					
			}
		}		
		return finalCombination.trim();
	}
	//in each sentence, we look for all combinations for each state, for accepted, we look for accept, accepting, accepted
	public static String containsTermsFromListOfLists(String sentenceorParagrapghString, ArrayList<ArrayList<String>> listOfLists,boolean matchWholeTerms) {
		String finalCombination="";
		//ArrayList<String> listofTermsMatched = new ArrayList<String>();
		for (ArrayList<String> line : listOfLists) {    //for each list            	
//$$    		System.out.println("Checking Line: "+line+ " : ");
    		for (String term : line) {      	//for each term in list 
    			term = term.trim();
    			//System.out.println("' checking "+term+ "' ");
    			//some terms in list of list are a combination of double term ..so we split here
    			if(term.contains("-"))
    				term = term.replace("-", " ");
    			if (ps.containsAllTermsAsTheyAre(term,sentenceorParagrapghString, matchWholeTerms)) {	//subject+" "+verb+" "+object
					finalCombination= finalCombination + "," + term; 								
    			}
    		}	//System.out.println("");
    	}
		return finalCombination.trim();
	}
	
	//March 2019 - only for current label
	//in each sentence, we look for all combinations for each state, for accepted, we look for accept, accepting, accepted
	public static String containsTermsFromListOfListsForLabel(String sentenceorParagrapghString, ArrayList<ArrayList<String>> listOfLists,boolean matchWholeTerms, String label) {
		String finalCombination="";
		//ArrayList<String> listofTermsMatched = new ArrayList<String>();
		for (ArrayList<String> line : listOfLists) {    //for each list            	
//$$    		System.out.println("Checking Line: "+line+ " : ");
			
			if(!line.get(0).equals(label))
				continue;
    		for (String term : line) {      	//for each term in list 
    			term = term.trim();
    			//System.out.println("' checking "+term+ "' ");
    			//some terms in list of list are a combination of double term ..so we split here
    			if(term.contains("-"))
    				term = term.replace("-", " ");
    			if (ps.containsAllTermsAsTheyAre(term,sentenceorParagrapghString, matchWholeTerms)) {	//subject+" "+verb+" "+object
					finalCombination= finalCombination + "," + term; 								
    			}
    		}	//System.out.println("");
    	}
		return finalCombination.trim();
	}
	
	//ABOUT FUNCTION: This function checks the current sentence for reason terms and computes a probability score ...probability calculation - sentenceLocationHintProbability
	//CALL: We call this function while checking a message, 
		//each time we encounter a state label match and therefore pass next previous sentence and paragraph
			//combination passed...and we also pass the 'termsLocationSpanningProbabilityScore'
				//current sentence, current sentence with next/previous sentence, current entire paragraph, current paragraph with next/previous paragraph 
		//each time we check nearby messages ..and for each sentence we also send the previous and next sentence and paragraphs
	
	//HOW IT WORKS: Function considers a sentence and also the nearby sentences and paragraphs as well
	//Therefore, we pass to this function, a string to check which could be:
	//same message as state.. if all reason terms found but found within the 
		//same sentence as state label..probability is 0.9
		//the sentence with state with previous sentence or sentence with next sentence..probability is 0.7
		//anywhere in the same paragraph as the sentence with state (this will include the state sentence)..0.5 
		//the sentence with state with next or previous paragraph..0.3	
	//different message as state ..	if found in nearby messages < 2 weeks..probability is 0.5
	//if all reason terms found but found within 
		// a sentence each time  ...0.5
		// a sentence with previous sentence or sentence with next sentence ..0.4
		//entire paragraph  ...0.3
		//next or previous paragraph ..0.2
			
	//but if the terms are spanning across
	//combination of same sentence as label and either next and previous sentence..probability is 0.7
	//combination of same paragraph as label and either next and previous sentence..probability is 0.5
	
	public static Integer countTermsInString(String list) {
		if(list==null || list.isEmpty() || list.equals("") || list.length()==0) {	return 0;		}
		else {
			if (list.startsWith(","))
				list= list.replaceFirst(",", "");
			return list.split(",").length;
		}
	}
	
	public static String remove_parenthesis(String input_string, String parenthesis_symbol){
	    // removing parenthesis and everything inside them, works for (),[] and {}
	    if(parenthesis_symbol.contains("[]")){
	        return input_string.replaceAll("\\s*\\[[^\\]]*\\]\\s*", " ");
	    }else if(parenthesis_symbol.contains("{}")){
	        return input_string.replaceAll("\\s*\\{[^\\}]*\\}\\s*", " ");
	    }else{
	        return input_string.replaceAll("\\s*\\([^\\)]*\\)\\s*", " ");
	    }
	}
	//sentenceString,firstParagraph, para,lastParagraph, isFirstParagraph,isSecondParagraph,isThirdParagraph, isSecondLastParagraph,isLastParagraph, 
	public static ScoresToSentences checkSentenceOrParagraphForReasonTermsCombinations(Connection conn,Integer paragraphCounter,Integer totalParagraphs, String label, boolean matchWholeTerms,
			Integer mid,String messageSubject, Integer proposal, Date dateVal, Date dateOfStateMessage, String sentenceorParagrapghString,  
			String firstParagraph, String entireParagraph, String secondLastParagraph, String lastParagraph,  boolean isFirstParagraph,boolean isSecondParagraph, boolean isThirdParagraph,
			boolean isSecondLastParagraph, boolean isLastParagraph, String previousParagraph, String paraMinusCurrSentence,
			String location, String authorsRole,Integer dateDiff, String messageLocation,			
			boolean messageSubjectContainsStatesSubstates,boolean messageSubjectContainsDecisionTerms, boolean messageSubjectContainsVerbTerms,
			boolean messageSubjectContainsProposalIdentifierTerms,boolean messageSubjectContainsEntitiesTerms,boolean messageSubjectContainsSpecialTerms,
			int messageSubjectStatesSubstatesListCount, int messageSubjectDecisionTermListCount,int messageSubjectProposalIdentifiersTermListCount,
			int messageSubjectEntitiesTermListCount, int messageSubjectSpecialTermListCount,
			String messageSubjectStatesSubstatesList, String messageSubjectDecisionTermList, String	messageSubjectVerbList,
			String messageSubjectProposalIdentifiersTermList, String messageSubjectEntitiesTermList, String messageSubjectSpecialTermList, String messageType,
			boolean messsageTypeIsReasonMessage, double messsageTypeIsReasonMessageProbabilityScore,
			boolean decisionMessage, double v_sentenceOrParagraphLocationHintProbability) {	//for state message and other messages
		String combination = "",finalCombination="";
//		System.out.println("ZZZZ 3 decisionMessage: "+decisionMessage);
//		System.out.println("\n In Function ...lastParagraph b4 assigning 2: "  + lastParagraph + " first " + firstParagraph);
		//CHECK EACH SENTENCE
		//check in previous and next sentence as well
//&&		sentenceorParagrapghString = sentenceorParagrapghString.replace("\n", "").replace("\r", " ");
		
		//HERE WE DO THE MAIN MATCHING FOR CANDIDATE SENTENCES..WE ALSO ADD A SCORE OF PROBABILITY
		//do a synonyms/wordnet variant for all of these below terms
		//if a reason term, reason identifier term  and state/substate exists,and its near/within the sentence with the state/substate, there is a highest probability
		//if a reason term, reason identifier term  and state/substate exists, there is a high probability
		//if a reason term, reason identifier term/ or state,  there is a high probability
		//if a reason term, there is probability						
		
		//make sure sentence does not contain any other state from the list of states/substates compared to the label we are only looking for current label matched - label is passed as parameter
				
		//positive words or negative words ..there is a probability
		ScoresToSentences p = new ScoresToSentences();
		p.setLabel(label);		
		sentenceorParagrapghString=sentenceorParagrapghString.toLowerCase();
		p.setSentenceOrParagraph(sentenceorParagrapghString); 
		p.setLocation(location); //sentence or paragraph
	    p.setTermsLocationWithMessageProbabilityScore(v_sentenceOrParagraphLocationHintProbability);
	    p.setProposalNum(proposal);
	    p.setMid(mid);
	    p.setMessageSubject(messageSubject);		p.setDateVal(dateVal); 
		p.setMessageLocation(messageLocation);  
		p.setDateDiff(dateDiff);    	p.setAuthorsRole(authorsRole);	//assign high values
		p.setDateOfStateMessage(dateOfStateMessage);
		
	    p.setMessageSubjectContainsStatesSubstates(messageSubjectContainsStatesSubstates);
		p.setMessageSubjectContainsDecisionTerms(messageSubjectContainsDecisionTerms);
		p.setMessageSubjectContainsVerbTerms(messageSubjectContainsVerbTerms);
		p.setMessageSubjectContainsProposalIdentifierTerms(messageSubjectContainsProposalIdentifierTerms);
		p.setMessageSubjectContainsEntitiesTerms(messageSubjectContainsEntitiesTerms);
		p.setMessageSubjectContainsSpecialTerms(messageSubjectContainsSpecialTerms);
		
		//message subject term lists
		p.setMessageSubjectDecisionTermList(messageSubjectDecisionTermList);
		p.setMessageSubjectProposalIdentifiersTermList(messageSubjectProposalIdentifiersTermList);
		p.setMessageSubjectStatesSubstatesList(messageSubjectStatesSubstatesList);
		p.setMessageSubjectSpecialTermList(messageSubjectSpecialTermList);
		p.setMessageSubjectVerbList(messageSubjectVerbList);
		p.setMessageSubjectEntitiesTermList(messageSubjectEntitiesTermList);
		
		
		p.setMessageSubjectStatesSubstatesListCount(messageSubjectStatesSubstatesListCount);
		p.setMessageSubjectContainsDecisionTerms(messageSubjectContainsDecisionTerms);
		//p.setMessageSubjectVerbListCount(messageSubjectVerbListCount);
		p.setMessageSubjectProposalIdentifiersTermListCount(messageSubjectProposalIdentifiersTermListCount);
		p.setMessageSubjectEntitiesTermListCount(messageSubjectEntitiesTermListCount);
		p.setMessageSubjectSpecialTermListCount(messageSubjectSpecialTermListCount);
		
		p.setEntireParagraph(entireParagraph);
		p.setParaMinusCurrSentence(paraMinusCurrSentence);
		p.setIsFirstParagraph(isFirstParagraph);
		p.setIsLastParagraph(isLastParagraph);
		if(outputForDebug)
			System.out.println("ZZZZ 3 decisionMessage 123 VALUE : "+decisionMessage);
		//feb 2019
		p.setMessageContainsSpecialTerm(decisionMessage);
		if(decisionMessage) {
			p.setMessageContainsSpecialTermProbabilityScore(0.9); //we give weightage to the message here, if the message contains special terms
			//set reason message
		}
		else {
			p.setMessageContainsSpecialTermProbabilityScore(0.0);
			//set that its not reason message
		}
		//message type classification	
		p.setFirstParagraph(firstParagraph); 		p.setLastParagraph(lastParagraph);
		//march 2019...added second paragrapgh also
		if(isFirstParagraph || isSecondParagraph ||isLastParagraph) //if current sentence, is first or last paragraph, then we assign a probability score for location in message
			p.setSentenceLocationInMessageProbabilityScore(0.9);
//		if(isThirdParagraph || isSecondLastParagraph) //if current sentence, is first or last paragraph, then we assign a probability score for location in message
//			p.setSentenceLocationInMessageProbabilityScore(0.5);
		p.setMesssageTypeIsReasonMessage(messsageTypeIsReasonMessage); p.setMessageType(messageType);
		p.setMesssageTypeIsReasonMessageProbabilityScore(messsageTypeIsReasonMessageProbabilityScore);
		
		//feb 2019, we check to see if sentence has been captured for a reason label during process mining using triple extraction
		//for the pep and messageid, we check each sentence in 'results' table (not results-postprocessed as lots of sentences are removed here) if a reason label is matched
		//we then check if the label is for reasonlabels 
		//SQL TO CHECK
		// select proposal,messageid, datediff, sentence,reasonLabelFoundUsingTripleExtractionProbabilityScore  from autoextractedreasoncandidatesentences 
		// where reasonLabelFoundUsingTripleExtractionProbabilityScore > 0
		// order by proposal
		
		String reasonLabels[] = {"consensus","majority","bdfl","problems"};  //just one term from reason labels - will allow matching of several labels 
		boolean reasonLabelFound=false,matchedWithReasonTripleSentence=false;
		String stateSubstateResultsTableName = "results"; String reasonLabel="", eachSentence = "";
		List<String> eachSentenceTerms = null;
		ResultSet rs7 = null; Statement stmt7 = null; 	
		List<String> mainSentenceTerms = new ArrayList<String>();
		String query7="";		int rowCount;	float  matchPercent;
		List<String> commonTerms = new ArrayList<String>();
		boolean wordOrdersame = false;
		
		try {	stmt7 = conn.createStatement();			
			if(sentenceorParagrapghString.isEmpty() ||  sentenceorParagrapghString.equals("") || sentenceorParagrapghString.length()==0  || 
					location.equals("paragraph")) {}  //we dont do this for paragraphs
			//System.out.println("here b "); 
			else { //System.out.println("ZZZZ 3 decisionMessage: 12345 1111 sentenceorParagrapghString: "+ sentenceorParagrapghString);	
				mainSentenceTerms = Arrays.asList(sentenceorParagrapghString.toLowerCase().split(" ")); //causeSentence.toLowerCase().split(" ");
				
				//System.out.println("here search Same Message Subjects: "+ cleanedMsgSubject);
				query7 = "SELECT * FROM "+stateSubstateResultsTableName+" WHERE pep = "+proposal+" and messageid = "+mid+" order by dateTimeStamp;"; 	//label checking is done in below code
				//check to see if we want to see all retrieved messages where status has changed 
				rs7 = stmt7.executeQuery(query7);						rowCount = guih.returnRowCount(query7);
//				System.out.println("(" + sentenceorParagrapghString +") similar message subjects () - resultset rowCount = " + rowCount);  
				while (rs7.next()) {  //we can get several results for same label, here we just check even if any subject from any result row is matched, as post-processing should be very good to let go of important results 
					//we check for all reason labels 
//					System.out.println("ZZZZ 3 decisionMessage: 12345 88");
					reasonLabel = rs7.getString("label");  eachSentence = rs7.getString("currentSentence");
					//for each sentence we check if it matched with exact and fuzzzy checking
					for (String rl: reasonLabels)			//checking this way prevents putting all varieties of reason labels in sql query		
						if(reasonLabel==null || reasonLabel.isEmpty() || reasonLabel.equals("")){}
						else {
							if(reasonLabel.equals(rl)){	
								reasonLabelFound=true; break; 
							} //we dont check any further
						}
//					System.out.println("ZZZZ 3 decisionMessage: 12345 11112");	
					if(reasonLabelFound) {
						//check each sentence if it matches mainSentence
						//replace unwanted chars;
						//march 2019, remove everything inside square brackets, cos of sentence of PEP 488 'An informal poll was taken ...PEP [9]' - is considered as PEP 9, if we just remove punctuation
						eachSentence = eachSentence.replaceAll("\\[.*?\\]", ""); // remove everything inside square brackets
						eachSentence = eachSentence.replaceAll("\\p{P}", " "); //remove all punctuation ..maybe we can use this here (term matching for candidates) but not in CIE triple extraction as it wmay need commas and other punctuation
						eachSentence = eachSentence.replaceAll("\\s+", " ").trim(); //remove double spaces and trim
						eachSentence = eachSentence.replaceAll("\'","").trim();						
						eachSentence = eachSentence.replaceAll(" n t ","n t ");//change 'There has n t been a ' to 'There hasn t been a '
						
						if(eachSentence==null || eachSentence.isEmpty() || eachSentence.length()==0 ) {
							continue;
						}
						else {
							if(eachSentence.contains(" ")) {
								eachSentenceTerms =  Arrays.asList(eachSentence.toLowerCase().split(" "));	//autosentenceOrParagraph.toLowerCase().split(" ");								
							}
							else {
								eachSentenceTerms = new ArrayList<String>();
								eachSentenceTerms.add(eachSentence);	
							} 
							//System.out.println("\tChecking against ranking: "+ranking+", Automatic Sentence ("+sentenceOrParagraph+") ");
							commonTerms = findCommonElement(eachSentenceTerms,mainSentenceTerms);  //each sentence we have found we compare to main sentence
							//if length of common terms > 70% of manual extracted sentence
							//if(Arrays.asList(automaticSentenceOrParagraph).containsAll(Arrays.asList(causeSentenceTerms))) {
							//System.out.println("\tAutomatic label ("+autolabel+") Sentence ("+autosentenceOrParagraph+") ");							
							//System.out.print("\t\tcommonTermsSize: "+ commonTerms.size() + " manualCauseSentenceTerms.size(): "+ manualCauseSentenceTerms.size());  printList(commonTerms);
							matchPercent = (commonTerms.size() * 100.0f /mainSentenceTerms.size())  ;
							//get first three terms in both and make sure they are not numbers and punctuation and same in both								
							wordOrdersame = checkWordOrder(eachSentenceTerms,mainSentenceTerms,3);							
//$							System.out.println("\t\tmatchPercent: "+ matchPercent + " wordOrdersame: "+wordOrdersame ); 
							if(matchPercent > 60 && wordOrdersame ) { // && wordOrdersame || proposalNum==369) {	//
								//ASSIGN WEIGHTAGE
								p.setReasonLabelFoundUsingTripleExtraction(true);   //reasonLabelFoundUsingTripleExtraction
								p.setReasonLabelFoundUsingTripleExtractionProbabilityScore(5.0);   //reasonLabelFoundUsingTripleExtractionProbabilityScore								
								System.out.println("\t\t Sentence matched as having reason Label ("+reasonLabel+") Sentence ("+sentenceorParagrapghString+")");
								//					System.out.println("\tcommonTermsSize: "+ commonTerms.size() +" causeSentenceTerms.length: "+causeSentenceTerms.length); printList(commonTerms); 
								//update row class column for machine learning , if sentence is matched
								//updateRowForMachineLearning(id);
								//updateTableWithMessageType_UpdateMessageType(id,Integer.valueOf(mid),datediff,proposalNum,state,causeSentence) ; //dec 2018
								//if it wasnt matched before, and as soon as rank found, we write only first ranking match record (above) in a separate excel sheet
								matchedWithReasonTripleSentence=true;	
								break;	//dec 2018, as soon as first match is found..we dont want to match all matches of same sentence which are below in order
							}
						}						
					} //end if reson label is found
				}	//end for each sentence for that pep and mid
				if (!matchedWithReasonTripleSentence){	//if no reason label is matched OR if reason label is matched but no sentence is matched
					p.setReasonLabelFoundUsingTripleExtraction(false);   //reasonLabelFoundUsingTripleExtraction
					p.setReasonLabelFoundUsingTripleExtractionProbabilityScore(0.0);   //reasonLabelFoundUsingTripleExtractionProbabilityScore	
				}
				stmt7.close();
			}	//END ELSE		
		} catch (SQLException insertException) {					//displaySQLErrors(insertException);	
			System.out.println("Exception 300 (FILE) " + insertException.toString()); 
			System.out.println(StackTraceToString(insertException)  );
		}
		
		//Mar 2019, if sentence had been found to have state/substate through triple matching, 
		//we match the sentence or message subject and if it matches, we give more weight
		//all messages which have same message subject as substate triple
		//nov 2018, if the mesage shares teh same subject with the acceptance/rejection message, then we are more sure this is reason message/sentence
		//String stateSubstateResultsTableName = "results";
		ResultSet rs = null; Statement stmt = null;  		
		try {	stmt = conn.createStatement();			
			if(messageSubject.isEmpty() ||  messageSubject.equals("")) {}
			//System.out.println("here b "); 
			else {
				String cleanedMsgSubject = messageSubject.toLowerCase().replace("re:", "").replace("fw:", ""); //get rid of fwd and reply
				cleanedMsgSubject = remove_parenthesis(cleanedMsgSubject, "[]");
				//cleanedMsgSubject = ;
				cleanedMsgSubject = cleanedMsgSubject.replaceAll("[,.:;!?(){}\\[\\]<>%]",""); // remove punctuation
				cleanedMsgSubject = cleanedMsgSubject.replaceAll("[^a-zA-Z ]", "").toLowerCase();	//remove all punctuation
				//System.out.println("here search Same Message Subjects: "+ cleanedMsgSubject);
				//System.out.println("here c "); //results table
				String query = "SELECT * FROM "+stateSubstateResultsTableName+" WHERE pep = "+proposal+" and label = '"+label+"' and messagesubject like '%"+ cleanedMsgSubject  +"%' order by dateTimeStamp;";
				//check to see if we want to see all retrieved messages where status has changed 
				//rs = statement.executeQuery("SELECT * FROM "+messagesTableName+" WHERE "+proposalIdentifier+" + Integer.parseInt(proposalNumberText.getText()) + " AND email LIKE '%" + searchText.getText()  + "%' AND statusChanged =1 order by dateTimeStamp, messageid asc, Proposal asc;");
				rs = stmt.executeQuery(query);						rowCount = guih.returnRowCount(query);
				//System.out.println("here d");
//				System.out.println("(" + sentenceorParagrapghString +") similar message subjects ("+cleanedMsgSubject+") - resultset rowCount = " + rowCount);  
				if (rs.next()) {  //we can get several results for same label, here we just check even if any subject from any result row is matched, as post-processing should be very good to let go of important results 
					p.setSameMsgSubAsStateTriple(true);		p.setSameMsgSubAsStateTripleProbabilityScore(0.9); }  //set same message subject boolean value to true
				else {
					p.setSameMsgSubAsStateTriple(false);	p.setSameMsgSubAsStateTripleProbabilityScore(0.0); 
				} 
				stmt.close();
			}			
		} catch (SQLException insertException) {					//displaySQLErrors(insertException);	
			System.out.println("Exception 300 (FILE) " + insertException.toString()); 
			System.out.println(StackTraceToString(insertException)  );
		}
		
		//dec 2018, we dont check if sentence contains terms which should not co-occur
		//the first reason term should not cooccur with any of teh rest
		//line 1. argument, function, call, parameter,parameters, return,returns,returned, keyword, keywords, attribute, attributes
		//line 2. resolution, high, monitor
		//List<String> newList = new ArrayList<String>();
		//newList = notCoOccurTerms.subList(1, notCoOccurTerms.size());
		boolean foundNotCoOccurringTerms = false; String firstTerm = "";
		try {	
			for (String line : notCoOccurTerms){ //for each line
				if (line.contains(",")) {
					firstTerm = line.split(",")[0].trim();  String restTerms[] = line.split(",");
					for (int i=1; i< restTerms.length; i++) {
						if(ps.containsAllTermsAsTheyAre(firstTerm,sentenceorParagrapghString, matchWholeTerms)  && ps.containsAllTermsAsTheyAre(restTerms[i].trim(),sentenceorParagrapghString, matchWholeTerms))  {
							foundNotCoOccurringTerms = true;
						}
					}
					restTerms = null;
				}
			}	firstTerm=null;			
			if(foundNotCoOccurringTerms) {
//				System.out.println("Unwanted co occuring terms found in sentence: "+ sentenceorParagrapghString);  return p;  //we return early without writing anything to database table
			}
		}
		catch (Exception e) {					//displaySQLErrors(insertException);	
			System.out.println("Exception 300 (FILE) " + e.toString()); 
			System.out.println(StackTraceToString(e)  );
		}
		//can conmtinue to process onto next sentence if not
		//boolean isEnglish = prp.getEnglishOrCode().isEnglish(sentenceorParagrapghString); //Check if sentence is Englsh or code
		//p.setEnglishOrCode(isEnglish);
	    
//		System.out.println("\nLocation123:"+ sentenceLocation+", "+sentenceorParagrapghString);
		//FIRST CHECK...CHECK FOR JUST THE REASON TERMS
		String sentenceorParagrapghString_reasonsTermsFoundList = containsTermsFromListOfLists(sentenceorParagrapghString,reasonsList, matchWholeTerms);
		sentenceorParagrapghString_reasonsTermsFoundList = sentenceorParagrapghString_reasonsTermsFoundList.startsWith(",") ? sentenceorParagrapghString_reasonsTermsFoundList.substring(1) : sentenceorParagrapghString_reasonsTermsFoundList;
		if(sentenceorParagrapghString_reasonsTermsFoundList == null || sentenceorParagrapghString_reasonsTermsFoundList.isEmpty() ||sentenceorParagrapghString_reasonsTermsFoundList.equals("") ||sentenceorParagrapghString_reasonsTermsFoundList.length()==0) {}
		else {		p.setSentenceOrParagraph_containsReasonsTerms(true);	p.setReasonsTermsFoundList(sentenceorParagrapghString_reasonsTermsFoundList);   
					p.setReasonsTermsFoundCount(countTermsInString(sentenceorParagrapghString_reasonsTermsFoundList));
					if(outputForDebug) {
						System.out.println("\tReasonsTermsFoundList: ("+ sentenceorParagrapghString_reasonsTermsFoundList+ ") ReasonsTermsFoundCount: "+	countTermsInString(sentenceorParagrapghString_reasonsTermsFoundList));	
					}
					p.setWriteToFile(true);
		}			
	
		//SECOND CHECK...CHECK FOR JUST THE REASON TERMS
		String sentenceorParagrapghString_reasonsIdentifierTermsList = containsAllTerms(sentenceorParagrapghString,reasonIdentifierTerms, matchWholeTerms);
		sentenceorParagrapghString_reasonsIdentifierTermsList = sentenceorParagrapghString_reasonsIdentifierTermsList.startsWith(",") ? sentenceorParagrapghString_reasonsIdentifierTermsList.substring(1) : sentenceorParagrapghString_reasonsIdentifierTermsList;
		if(sentenceorParagrapghString_reasonsIdentifierTermsList == null || sentenceorParagrapghString_reasonsIdentifierTermsList.isEmpty() ||sentenceorParagrapghString_reasonsIdentifierTermsList.equals("") || sentenceorParagrapghString_reasonsIdentifierTermsList.length()==0) {}
		else {		p.setSentenceOrParagraph_containsReasonsIdentifierTerms(true);	p.setReasonsIdentifierTermsList(sentenceorParagrapghString_reasonsIdentifierTermsList);
					p.setReasonsIdentifierTermsCount(countTermsInString(sentenceorParagrapghString_reasonsIdentifierTermsList));
					if(outputForDebug) {
						System.out.println("\tReasonsIdentifierTermsList: ("+ sentenceorParagrapghString_reasonsIdentifierTermsList+ ") ReasonsIdentifierTermsFoundCount: "+ countTermsInString(sentenceorParagrapghString_reasonsIdentifierTermsList));	
					}
					p.setWriteToFile(true);
		}	
			
		//THIRD CHECK...CHECK FOR JUST THE STATE/SUBSTATES TERMS
		String sentenceorParagrapghString_statesSubstatesList = containsTermsFromListOfLists(sentenceorParagrapghString,labelList, matchWholeTerms);	//allStatesList_StatesSubstates
		sentenceorParagrapghString_statesSubstatesList = sentenceorParagrapghString_statesSubstatesList.startsWith(",") ? sentenceorParagrapghString_statesSubstatesList.substring(1) : sentenceorParagrapghString_statesSubstatesList;
		if(sentenceorParagrapghString_statesSubstatesList == null || sentenceorParagrapghString_statesSubstatesList.isEmpty() || sentenceorParagrapghString_statesSubstatesList.equals("")  || sentenceorParagrapghString_statesSubstatesList.length()==0) {}
		else { p.setSentenceOrParagraph_containsStatesSubstates(true);	p.setStatesSubstatesList(sentenceorParagrapghString_statesSubstatesList);
			   p.setStatesSubstatesCount(countTermsInString(sentenceorParagrapghString_statesSubstatesList));
			   if(outputForDebug) {
				   System.out.println("\tStatesSubstatesList: ("+ sentenceorParagrapghString_statesSubstatesList+ ") statesSubstatesListTermsFoundCount: "+ countTermsInString(sentenceorParagrapghString_statesSubstatesList));	
			   }
			   p.setWriteToFile(true);
		}	
			
		//FOURTH CHECK...CHECK FOR JUST THE VERB TERMS
		String sentenceorParagrapghString_verbList = containsAllTerms(sentenceorParagrapghString,verbs, matchWholeTerms);
		sentenceorParagrapghString_verbList = sentenceorParagrapghString_verbList.startsWith(",") ? sentenceorParagrapghString_verbList.substring(1) : sentenceorParagrapghString_verbList;
		if(sentenceorParagrapghString_verbList == null || sentenceorParagrapghString_verbList.isEmpty() || sentenceorParagrapghString_verbList.length()==0) {}
		else {			p.setSentenceOrParagraph_containsVerbTerms(true); 	//have to set verblist setting function
						if(outputForDebug) {
							System.out.println("\tVerbList: ("+ sentenceorParagrapghString_verbList+ ")");       
						}
		}			//System.out.println("verbList: "+ verbList);
		
		//FIFTH CHECK...CHECK FOR JUST THE identifiersTermList TERMS		
		String sentenceorParagrapghString_identifiersTermList = containsAllTerms(sentenceorParagrapghString,proposalIdentifiers, matchWholeTerms);
		sentenceorParagrapghString_identifiersTermList = sentenceorParagrapghString_identifiersTermList.startsWith(",") ? sentenceorParagrapghString_identifiersTermList.substring(1) : sentenceorParagrapghString_identifiersTermList;
		if(sentenceorParagrapghString_identifiersTermList == null || sentenceorParagrapghString_identifiersTermList.isEmpty() || sentenceorParagrapghString_identifiersTermList.length()==0) {}
		else {			p.setSentenceOrParagraph_containsIdentifierTerms(true);	p.setIdentifiersTermList(sentenceorParagrapghString_identifiersTermList);
		  				p.setIdentifiersTermCount(countTermsInString(sentenceorParagrapghString_identifiersTermList));
		  				if(outputForDebug) {
		  					System.out.println("\tIdentifiersTermList: ("+ sentenceorParagrapghString_identifiersTermList+ ") identifiersTermListCount: " + countTermsInString(sentenceorParagrapghString_identifiersTermList));				 
		  				}
		  				p.setWriteToFile(true);
		}	
		
//		System.out.print("CHECKING entitiesList >> ");  for (String line : entitiesList)     {                	System.out.print(line+ " ");            }		  System.out.println(" ");
		//SIXTH CHECK...CHECK FOR JUST THE identifiersTermList TERMS		
		String sentenceorParagrapghString_entitiesTermList = containsAllTerms(sentenceorParagrapghString,entitiesList, matchWholeTerms);
		sentenceorParagrapghString_entitiesTermList = sentenceorParagrapghString_entitiesTermList.startsWith(",") ? sentenceorParagrapghString_entitiesTermList.substring(1) : sentenceorParagrapghString_entitiesTermList;
		if(sentenceorParagrapghString_entitiesTermList == null || sentenceorParagrapghString_entitiesTermList.isEmpty() || sentenceorParagrapghString_entitiesTermList.length()==0) {}
		else {			p.setSentenceOrParagraph_containsEntitiesTerms(true);		p.setEntitiesTermList(sentenceorParagrapghString_entitiesTermList);
						p.setEntitiesTermCount(countTermsInString(sentenceorParagrapghString_entitiesTermList));
						if(outputForDebug) {
							System.out.println("\tEntitiesTermList: ("+ sentenceorParagrapghString_entitiesTermList+ ") entitiesTermListCount    " + 	countTermsInString(sentenceorParagrapghString_entitiesTermList));
						}
						p.setWriteToFile(true);
		}	
		
//		System.out.print("CHECKING specialTerms >> ");  for (String line : specialOSCommunitySpecificTerms)     {                	System.out.print(line+ " ");            }		  System.out.println(" ");
		//SEVENTH CHECK...CHECK FOR JUST THE special TERMS	- contains entities	
		String sentenceorParagrapghString_specialTermList = containsTermsFromListOfLists(sentenceorParagrapghString,specialOSCommunitySpecificTerms, matchWholeTerms);
		sentenceorParagrapghString_specialTermList = sentenceorParagrapghString_specialTermList.startsWith(",") ? sentenceorParagrapghString_specialTermList.substring(1) : sentenceorParagrapghString_specialTermList;
		if(sentenceorParagrapghString_specialTermList == null || sentenceorParagrapghString_specialTermList.isEmpty() || sentenceorParagrapghString_specialTermList.length()==0) {}
		else {			p.setSentenceOrParagraph_containsSpecialTerms(true);		p.setSpecialTermList(sentenceorParagrapghString_specialTermList);
						p.setSpecialTermCount(countTermsInString(sentenceorParagrapghString_specialTermList));
						if(outputForDebug) {
							System.out.println("\tSpecialTermList: ("+ sentenceorParagrapghString_specialTermList+ ")  specialTermList "   + countTermsInString(sentenceorParagrapghString_specialTermList));
						}
		}	

		String sentenceorParagrapghString_decisionTermList = containsTermsFromListOfLists(sentenceorParagrapghString,decisionTerms, matchWholeTerms);
		sentenceorParagrapghString_decisionTermList = sentenceorParagrapghString_decisionTermList.startsWith(",") ? sentenceorParagrapghString_decisionTermList.substring(1) : sentenceorParagrapghString_decisionTermList;
		if(sentenceorParagrapghString_decisionTermList == null || sentenceorParagrapghString_decisionTermList.isEmpty() || sentenceorParagrapghString_decisionTermList.length()==0) {}
		else {			p.setSentenceOrParagraph_containsDecisionTerms(true);	p.setDecisionTermList(sentenceorParagrapghString_decisionTermList);	
						p.setDecisionTermCount(countTermsInString(sentenceorParagrapghString_decisionTermList));
						if(outputForDebug) {
							System.out.println("\tDecisionTermList: ("+ sentenceorParagrapghString_decisionTermList + ")  decisionTermCount  " + countTermsInString(sentenceorParagrapghString_decisionTermList));
						}
						p.setWriteToFile(true);
		}	
		
		//in case of a sentence
		//check negation or conditional terms dont exist, ideally not before state or reason term
//		if (stringLoc=="sentence") {
			//also need to consider conditionalterms		
		String sentenceorParagrapghString_negationTermList = containsAllTerms(sentenceorParagrapghString,negationTerms, matchWholeTerms);
		if(sentenceorParagrapghString_negationTermList == null || sentenceorParagrapghString_negationTermList.isEmpty() || sentenceorParagrapghString_negationTermList.length()==0) {}
		else {
//			System.out.print(" MATCHED negationTermList: "+ negationTermList);
			//july 2018..there are some combinations (in reasonlist) where negation is part of the terms ... eg  'no objections'..so we consider them
			if(sentenceorParagrapghString_reasonsTermsFoundList.contains(sentenceorParagrapghString_negationTermList)) {	// ...try to improve code that contains atleast one
				//containsNegationTermList=false; negationTermPenalty = 0.0; //System.out.print(" MATCHED negationTermList in reasonList : "+ negationTermList);
				p.setNegationTermPenalty(0.0);
			}
			else {
				//containsNegationTermList=true;	negationTermPenalty = 0.8;	//penalty higher if just before and within few terms of the state or reason 
				p.setNegationTermPenalty(0.8);
			}	
		}
		
		//contains positive or negative words like flak, wrangling, 
		String sentenceorParagrapghString_positiveWordList = containsAllTerms(sentenceorParagrapghString,positiveWords, matchWholeTerms);
		sentenceorParagrapghString_positiveWordList = sentenceorParagrapghString_positiveWordList.startsWith(",") ? sentenceorParagrapghString_positiveWordList.substring(1) : sentenceorParagrapghString_positiveWordList;
		if(sentenceorParagrapghString_positiveWordList == null || sentenceorParagrapghString_positiveWordList.isEmpty() || sentenceorParagrapghString_positiveWordList.length()==0) {}
		else {			p.setSentenceOrParagraph_containsPositiveWords(true);	}
		
		String sentenceorParagrapghString_negativeWordList = containsAllTerms(sentenceorParagrapghString,negativeWords, matchWholeTerms);
		sentenceorParagrapghString_negativeWordList = sentenceorParagrapghString_negativeWordList.startsWith(",") ? sentenceorParagrapghString_negativeWordList.substring(1) : sentenceorParagrapghString_negativeWordList;
		if(sentenceorParagrapghString_negativeWordList == null || sentenceorParagrapghString_negativeWordList.isEmpty() || sentenceorParagrapghString_negativeWordList.length()==0) {}
		else {			p.setSentenceOrParagraph_containsNegativeWords(true);		}	
		
		String sentenceorParagrapghString_finalListOfAllTermsFound = sentenceorParagrapghString_reasonsTermsFoundList + " " + sentenceorParagrapghString_reasonsIdentifierTermsList+ " " 
																	+sentenceorParagrapghString_statesSubstatesList   + " " + sentenceorParagrapghString_identifiersTermList + " " 
																	+sentenceorParagrapghString_verbList 			  + " " + sentenceorParagrapghString_entitiesTermList	+ " "
																	+sentenceorParagrapghString_specialTermList       + " " + sentenceorParagrapghString_decisionTermList; 
		sentenceorParagrapghString_finalListOfAllTermsFound = sentenceorParagrapghString_finalListOfAllTermsFound.replaceAll("\\s+", " ").trim(); //remove double spaces and trim
		p.setTermsmatched(sentenceorParagrapghString_finalListOfAllTermsFound);
		
		
		//we check the rest of the sentences within the paragraph..just 
		//for the STATE/SUBSTATES TERMS
		String restOfParagraph_statesSubstatesList = containsTermsFromListOfLists(paraMinusCurrSentence,labelList, matchWholeTerms);	//allStatesList_StatesSubstates
		restOfParagraph_statesSubstatesList = restOfParagraph_statesSubstatesList.startsWith(",") ? restOfParagraph_statesSubstatesList.substring(1) : restOfParagraph_statesSubstatesList;		if(restOfParagraph_statesSubstatesList == null || restOfParagraph_statesSubstatesList.isEmpty() || restOfParagraph_statesSubstatesList.equals("")  || sentenceorParagrapghString_statesSubstatesList.length()==0) {}
		else { p.setRestOfParagraph_containsStatesSubstates(true);	p.setRestOfParagraph_StatesSubstatesList(restOfParagraph_statesSubstatesList);
			   p.setRestOfParagraphStatesSubstatesTermsCount(countTermsInString(restOfParagraph_statesSubstatesList));
			   if(outputForDebug) {
				   System.out.println("\tRestOfParagraph: ("+ restOfParagraph_statesSubstatesList+ ") restOfParagraphFoundCount: "+ 	countTermsInString(restOfParagraph_statesSubstatesList));	
			   }
			   p.setWriteToFile(true);
		}
		//proposal identifier.. THE identifiersTermList TERMS		
		String restOfParagraph_identifiersTermList = containsAllTerms(paraMinusCurrSentence,proposalIdentifiers, matchWholeTerms);
		restOfParagraph_identifiersTermList = restOfParagraph_identifiersTermList.startsWith(",") ? restOfParagraph_identifiersTermList.substring(1) : restOfParagraph_identifiersTermList;
		if(restOfParagraph_identifiersTermList == null || restOfParagraph_identifiersTermList.isEmpty() || restOfParagraph_identifiersTermList.length()==0) {}
		else {			p.setRestOfParagraph_containsIdentifierTerms(true);	p.setRestOfParagraph_identifiersTermList(restOfParagraph_identifiersTermList);
		  				p.setRestOfParagraph_identifiersTermCount(countTermsInString(restOfParagraph_identifiersTermList));
		  				if(outputForDebug) {
		  					System.out.println("\tIdentifiersTermList: ("+ restOfParagraph_identifiersTermList+ ") identifiersTermListCount: " + countTermsInString(restOfParagraph_identifiersTermList));				 
		  				}
						p.setWriteToFile(true);
		}	
		
		//THE identifiersTermList TERMS		
		String restOfParagraph_entitiesTermList = containsAllTerms(paraMinusCurrSentence,entitiesList, matchWholeTerms);
		restOfParagraph_entitiesTermList = restOfParagraph_entitiesTermList.startsWith(",") ? restOfParagraph_entitiesTermList.substring(1) : restOfParagraph_entitiesTermList;
		if(restOfParagraph_entitiesTermList == null || restOfParagraph_entitiesTermList.isEmpty() || restOfParagraph_entitiesTermList.length()==0) {}
		else {			p.setRestOfParagraph_containsEntitiesTerms(true);		p.setRestOfParagraph_entitiesTermList(restOfParagraph_entitiesTermList);
						p.setRestOfParagraph_entitiesTermCount(countTermsInString(restOfParagraph_entitiesTermList));
						if(outputForDebug) {
							System.out.println("\tEntitiesTermList: ("+ restOfParagraph_entitiesTermList+ ") entitiesTermListCount    " + countTermsInString(restOfParagraph_entitiesTermList));
						}
						
						p.setWriteToFile(true);
		}	
		
		//CHECK FOR JUST THE special TERMS	- contains entities	
		String restOfParagraph_specialTermList = containsTermsFromListOfLists(paraMinusCurrSentence,specialOSCommunitySpecificTerms, matchWholeTerms);
		restOfParagraph_specialTermList = restOfParagraph_specialTermList.startsWith(",") ? restOfParagraph_specialTermList.substring(1) : restOfParagraph_specialTermList;
		if(restOfParagraph_specialTermList == null || restOfParagraph_specialTermList.isEmpty() || restOfParagraph_specialTermList.length()==0) {}
		else {			p.setRestOfParagraph_containsSpecialTerms(true);		p.setRestOfParagraph_specialTermList(restOfParagraph_specialTermList);
						p.setRestOfParagraph_specialTermCount(countTermsInString(restOfParagraph_specialTermList));
						if(outputForDebug) {
							System.out.println("\tSpecialTermList: ("+ restOfParagraph_specialTermList+ ")  specialTermList "   + 	countTermsInString(restOfParagraph_specialTermList));
						}
		}
		//decision terms
		String restOfParagraph_decisionTermList = containsTermsFromListOfLists(paraMinusCurrSentence,decisionTerms, matchWholeTerms);
		restOfParagraph_decisionTermList = restOfParagraph_decisionTermList.startsWith(",") ? restOfParagraph_decisionTermList.substring(1) : restOfParagraph_decisionTermList;
		if(restOfParagraph_decisionTermList == null || restOfParagraph_decisionTermList.isEmpty() || restOfParagraph_decisionTermList.length()==0) {}
		else {			p.setRestOfParagraph_containsDecisionTerms(true);	p.setRestOfParagraph_decisionTermList(restOfParagraph_decisionTermList);	
						p.setRestOfParagraph_decisionTermCount(countTermsInString(restOfParagraph_decisionTermList));
						if(outputForDebug) {
							System.out.println("\tDecisionTermList: ("+ restOfParagraph_decisionTermList + ")  decisionTermCount  " +	countTermsInString(restOfParagraph_decisionTermList));
						}
						p.setWriteToFile(true);
		}
		
		//return distinct words in finalListOfAllTermsFound
		String s[]=sentenceorParagrapghString_finalListOfAllTermsFound.split(" ");
		sentenceorParagrapghString_finalListOfAllTermsFound="";//put this list to empty
		ArrayList<String> uniqueList = new ArrayList<String>();
		for (int i = 0; i < s.length; i++) {
		    if (!(uniqueList.contains(s[i]))) {
		    	uniqueList.add(s[i]);    	sentenceorParagrapghString_finalListOfAllTermsFound = sentenceorParagrapghString_finalListOfAllTermsFound + " " + s[i];
		    }
		}
		int noOfTerms = sentenceorParagrapghString_finalListOfAllTermsFound.trim().split(" ").length;
		//we want to match states/substates with the label we are checking for
		//for instance, for the label accepted, we only look for that state 'accept', 'accepted', and 'add' or all other synonyms
		
		//Feb 2019, special terms in previous para ...Example, 'PEP Acceptance' can be a heading for the current paragraph, but since there is a space, it would be the previous paragraph
		String prevParagraph_specialTermList = containsTermsFromListOfLists(previousParagraph,specialOSCommunitySpecificTerms, matchWholeTerms);
		prevParagraph_specialTermList = prevParagraph_specialTermList.startsWith(",") ? prevParagraph_specialTermList.substring(1) : prevParagraph_specialTermList;
		if(prevParagraph_specialTermList == null || prevParagraph_specialTermList.isEmpty() || prevParagraph_specialTermList.length()==0) {}
		else {			p.setPrevParagraph_containsSpecialTerms(true);		p.setPrevParagraph_specialTermList(prevParagraph_specialTermList);
						p.setPrevParagraphSpecialTermsCount(countTermsInString(prevParagraph_specialTermList));
						if(outputForDebug) {
							System.out.println("\tSpecialTermList: ("+ prevParagraph_specialTermList+ ")  specialTermList "   + 	countTermsInString(prevParagraph_specialTermList));
						}
		}
		
		//CALL THE CALCULATE PROBABILITY FUNCTION  WHICH WILL WRITE TO DB AS WELL
		/*
		p.calculateAllProbabilities_StoreInDB(connection,  tableNameToStore,  sentenceorParagrapghString_reasonsTermsFoundList,  sentenceorParagrapghString_reasonsIdentifierTermsList, 
				sentenceorParagrapghString_statesSubstatesList, sentenceorParagrapghString_identifiersTermList, sentenceorParagrapghString_entitiesTermList,
				sentenceorParagrapghString_specialTermList, sentenceorParagrapghString_negationTermList, sentenceorParagrapghString_positiveWordList, 
				sentenceorParagrapghString_negativeWordList,
				0.0); //termsLocationWithMessageProbabilityScore=0.0 as its not yet determined and would be determined later, unlike state messages);
		*/
		
		//march 2019, if the string contains accept reject terms, we give a slightly higher probability
		//march 2019, if the sentence contains any of the accept reject terms, we give a slightly higher probability to cater for tie breakers when several results have same probability
		String labelInSentenceOrParagraphList = containsTermsFromListOfListsForLabel(sentenceorParagrapghString,labelList, matchWholeTerms,label);
		if(labelInSentenceOrParagraphList.length() > 0)	
			p.setActermsfound(true); //SentenceOrParagraphHintProbablity=sentenceOrParagraphHintProbablity + 0.1; 
		
		//August 2020. Baseline 1 and 2
		Boolean baseline1 = false, baseline2 = true;
		
		if(baseline1)
			p.calculateAllProbabilities_UpdateMessageType_B1(connection);
		else if(baseline2)	
			p.calculateAllProbabilities_UpdateMessageType_B2(connection);
		else
			p.calculateAllProbabilities_UpdateMessageType(connection);
		//System.out.print(" writeToDatabase:");
		p.writeToDatabase(connection, tableNameToStore , outputForDebug, baseline1, baseline2);
		
		//for inserting one result row per paragraph
		//remove duplicates from string -- 
		if(finalCombination.length() >0) {
			finalCombination=  deDup(finalCombination);  //removeDuplicates(finalCombination, " ");
//			System.out.println("\t mid "+mid+" new terms [" + finalCombination + "] found in "+location+". Paragraph "+paragraphCounter); 
//			r1.setData(proposal, mid, dateVal, sentenceorParagrapghString, location, finalCombination, false, author);	//System.out.println("here b");
//			rr.add(r1);
		}
		//r1=null;	
		finalCombination="";
		//return listofTermsMatched;
		return p;
	}
	
	public static String deDup(String s) {
	    return new LinkedHashSet<String>(Arrays.asList(s.split(" "))).toString().replaceAll("(^\\[|\\]$)", "").replace(", ", " ");
	}
	
	//output All PepTitle and Numbers
	public static ArrayList<ReasonLabel> populateReasonLabelsFromTrainingData(ArrayList<ReasonLabel> rl, Connection conn) {		
		try
	    {
		  int counter=0;
	      String query = "SELECT * FROM trainingdata order by pep;";
	      Statement st = conn.createStatement();	  ResultSet rs = st.executeQuery(query);     
	      while (rs.next()) {
	        int proposalNumber = rs.getInt("pep");						int messageid = rs.getInt("messageid");	        			String label = rs.getString("label");
	        String state = rs.getString("state");	 	String dmconcept = rs.getString("dmconcept");	String causeSentence = rs.getString("causeSentence");	 	
	        String effectSentence = rs.getString("effectSentence");	    String communityReview = rs.getString("communityReview");	String proposalAuthorReview = rs.getString("proposalAuthorReview");	        
	        String bdfldelegatePronouncement = rs.getString("bdfldelegatePronouncement");
	        ReasonLabel r = new ReasonLabel();
	        String message = "";
	        //dec 2019 ... causesubcategory now = label
	        r.setData(proposalNumber, state,label, dmconcept, causeSentence, effectSentence, messageid, communityReview, proposalAuthorReview, bdfldelegatePronouncement);
	        rl.add(r);	counter++;
//	        System.out.println("Label added to list : "+ label + " causecategory: "+causeCategory + " causesubcategory: "+causesubcategory + " causeSentence: "+causeSentence);
	      }
	      st.close();	System.out.println("Reasons added to list : "+ counter);
	    }
	    catch (Exception e) {
	      System.err.println("Got an exception! ");	      System.err.println(e.getMessage());
	    }		
		return rl;
	}

	public static void displayResultsReasons(ArrayList<TripleProcessingResult> tripleProcessingResultList) {
		try 
		{		
			if(!tripleProcessingResultList.isEmpty())
			{				
				for (int y=0; y <tripleProcessingResultList.size(); y++)
				{
					System.out.print(tripleProcessingResultList.get(y).getPepNumber().toString()+",");					
					System.out.print(tripleProcessingResultList.get(y).getLabel().toString()+",");					
					System.out.print(tripleProcessingResultList.get(y).getCurrentSentence().toString()+",");									
					System.out.print(tripleProcessingResultList.get(y).getClausIE()+",");				
//					System.out.println(tripleProcessingResultList.get(y).getRole());
//					System.out.println('|');
					System.out.print(tripleProcessingResultList.get(y).getFinalReasonListArrayAsString().toLowerCase()+",");	
					System.out.print(tripleProcessingResultList.get(y).getIfRepeatedFoundLabel()+",");					
					System.out.print(tripleProcessingResultList.get(y).getPrevSentence()+",");	
					System.out.print(tripleProcessingResultList.get(y).getNextSentence()+",");	
					System.out.print(tripleProcessingResultList.get(y).getPrevParagraph()+",");	
					System.out.print(tripleProcessingResultList.get(y).getCurrParagraph()+",");	
					System.out.print(tripleProcessingResultList.get(y).getNextParagraph()+'\n');
					
					System.out.print(tripleProcessingResultList.get(y).getReasonTermsInMatchedTriple()+",");	
					System.out.print(tripleProcessingResultList.get(y).getReasonsInSentence() +",");	
					System.out.print(tripleProcessingResultList.get(y).getReasonTermsInNearbySentencesParagraphs() +",");	
					System.out.print(tripleProcessingResultList.get(y).getReasonTriplesInNearbySentencesParagraphs()+'\n');
				}
			}
		}
		catch (Exception e){
			System.out.println("Exception 300 (FILE) " + e.toString()); 
			System.out.println(StackTraceToString(e)  );
		}
	}
	
	//feb 2019
	//check that first n words of manual sentence exists in automatic sentnec
	//pass both manual and automatic sentence as parameter
	//pass number of terms to check
	public static boolean checkWordOrder(List<String> automaticSentenceOrParagraph, List<String> manualCauseSentenceTerms, int numOfTermsToCheck ){
		try {
			//feb 2019..check no of terms first			
			while(manualCauseSentenceTerms.size() < numOfTermsToCheck) {
				numOfTermsToCheck--;
			}
			List<String> manualTermsSubset = new ArrayList<String>(manualCauseSentenceTerms.subList(0, numOfTermsToCheck));
			List<String> autoTermsSubset;// = new ArrayList<String>(automaticSentenceOrParagraph.subList(0, numOfTermsToCheck));
			List<String> subsetCommonTerms = new ArrayList<String>();
			//find the first manual term in automatic..can be multiple occurence
			String firstManualTerm = manualTermsSubset.get(0); //get first manual term to check
	//$$		System.out.println(" firstManualTerm: "+firstManualTerm);
			for (int i=0; i< automaticSentenceOrParagraph.size()-numOfTermsToCheck; i++) {		//we dont want to reach arrayindex out of range therefore we minus
				String autoTerm = automaticSentenceOrParagraph.get(i);
				if(autoTerm.contains(firstManualTerm) ) {			// as soon as first manual term is matched in auto sentence
					//extract subset 
					autoTermsSubset = new ArrayList<String>(automaticSentenceOrParagraph.subList(i, i+numOfTermsToCheck));	//check next n number of terms
	//$$				System.out.println(" i: "+i+" manualTermsSubset: "+manualTermsSubset+", autoTermsSubset: "+autoTermsSubset.toString());
					//make sure next x terms are the same in both the subsets
					subsetCommonTerms=findCommonElement(manualTermsSubset,autoTermsSubset); //System.out.println(" subsetCommonTerms: "+subsetCommonTerms.toString());
					//if(subsetCommonTerms.size() == numOfTermsToCheck) 
					//	return true;
					for (int j=0; j< numOfTermsToCheck; j++) {
						if(!autoTermsSubset.get(j).equals(manualTermsSubset.get(j)))
							return false;				//if any of these elements is not matched, return false
					}
					return true;
				}
			}
		}
		catch (Exception e){
			System.out.println("Exception 300 (FILE) " + e.toString()); 
			System.out.println(StackTraceToString(e)  );
		}
		return false;
	}
		
	public static List<String> findCommonElement(List<String>  a, List<String>  b) {
        List<String> commonElements = new ArrayList<String>();
        for(String i: a) {
            for(String j: b) {
                    if(i.equals(j)) {  
                    //Check if the list already contains the common element
                      //  if(!commonElements.contains(a[i])) {
                            //add the common element into the list
                            commonElements.add(i);
                      //  }
                    }
            }
        }
        return commonElements;
	}

	public static String StackTraceToString(Exception ex) {
		String result = ex.toString() + "\n";
		StackTraceElement[] trace = ex.getStackTrace();
		for (int i=0;i<trace.length;i++) {
			result += trace[i].toString() + "\n";
		}
		return result;
	}
	
	@Override
	public void finalize() {	    //System.out.println("Probability instance is getting destroyed"); 
		// Invoke the finalizer of our superclass
		// We haven't discussed superclasses or this syntax yet
		  try {
			super.finalize();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
