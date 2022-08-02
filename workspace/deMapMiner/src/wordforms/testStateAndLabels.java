package wordforms;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.smu.tspell.wordnet.SynsetType;
import wordnet.jaws.WordnetSynonyms;
import stanfordParser.StanfordLemmatizer;
import utilities.StateAndReasonLabels;

public class testStateAndLabels {

	//we added a main just to check the word forms of all these terms
		static GetWordForms wf = new GetWordForms();
		static WordnetSynonyms ws = new WordnetSynonyms();
		
		static List<String>  verbs  = new ArrayList<String>();
		static List<String>  committedStates  = new ArrayList<String>();
		static List<String>  subStates  = new ArrayList<String>();
		static ArrayList<ArrayList<String>>  allStatesList_StatesSubstates  = new ArrayList<ArrayList<String>>();
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
		static ArrayList<ArrayList<String>>  decisionTerms = new ArrayList<ArrayList<String>>(); 
		
		public static void main(String[] args) {	
			wf.initialise();	ws.init();	
			StanfordLemmatizer sl = new StanfordLemmatizer();  //we need lemmatizer
			
			StateAndReasonLabels srl = new StateAndReasonLabels();
			srl.initStateAndReasonLabels(ws, true);
			
			verbs = srl.getVerbs(); 	//ready for review/pronouncement
			committedStates = srl.getCommittedStates();	//main state changes  e.g. 'Status: Accepted'
			System.out.print("FINAL committedStates >> ");  for (String line : committedStates)     {                	System.out.print(line+ ", ");            }		  System.out.println(" ");  
			//States and substates
			allStatesList_StatesSubstates = srl.getAllStatesList_StatesSubstates();	
			System.out.print("FINAL getAllStatesList_StatesSubstates >> ");
			for (ArrayList<String> line : allStatesList_StatesSubstates) {   //for each list as line 
	    		for (String term : line) {	//for each term in line
	            	System.out.print("'"+term+ "', "); // ("+ sl.lemmatize(term) +") ");        		
	    		}	System.out.println(); 
	    	}	//	  System.out.println(" "); 
			Set<String> finalStateSet = new HashSet<String>();
				 // now we add the different wordforms and wordnet word forms // For states we only use wordforms and deriavationallyform
				 // since the terms make a lot of different in capturing exact events, we dont look for noun and verb synonyms...they are not synonyms for this set anyway
				 // DerivationallyForm is useful but we compute it on the stemmed word form 
				 for (ArrayList<String> ss : allStatesList_StatesSubstates) { 
					String t = ss.get(0);	//get first element from arraylist
					String r = ""; //sl.lemmatize(t).get(0);   //get lemmatised form, mostly first element from lemmatised list should be only and main one 
					// all word forms
					System.out.println(r);
					Set stateListWordForms = wf.getAllWordForms(r);	
					finalStateSet.add(t);		//add current term
					finalStateSet.addAll(stateListWordForms);		//add word forms of that term
					// NOUN Synonyms
					Set<String> synonyms = ws.getSynonym(r, SynsetType.NOUN); finalStateSet.addAll(synonyms);   //region 
					System.out.print("\tNOUN Synonyms for word: " + r + " = "); 	for (String s : synonyms) {  System.out.print(s + ", ");	} System.out.println();				
					// VERB synonyms
					Set<String> synonymsVERB = ws.getSynonym(r, SynsetType.VERB); finalStateSet.addAll(synonymsVERB);  //region 
					System.out.print("\tVERB Synonyms for word: " + r + " = ");  for (String s : synonymsVERB) {  System.out.print(s + ", ");	} System.out.println();
					// DerivationallyForm 
					Set<String> derivationallyForm = ws.getDerivationallyRelatedForms(r, SynsetType.VERB); 	finalStateSet.addAll(derivationallyForm); //develop
					System.out.print("\tDerivationallyForm for word: " + r + " = ");	for (String s : derivationallyForm) {  System.out.print(s + ", ");	}	System.out.println();				
				} 
				System.out.println("allStatesList_StatesSubstates Set >> "); 
			    for (String f : finalStateSet)	{
			    	System.out.print( f + ", " );
			    } 
			    //convert to list
			    List<String> allStatesList_StatesSubstates_List = new ArrayList<String>();
			    allStatesList_StatesSubstates_List.addAll(finalStateSet);
			    
			    System.out.println("");
			//reason list
			reasonsList = srl.getReasonTerms();
			System.out.print("FINAL reasonsList >> ");  for (ArrayList<String> line : reasonsList)     {                	System.out.print(line+ ", ");            }		  System.out.println(" ");
			Set<String> finalReasonSet = new HashSet<String>();
			    // Looking at the outputs from below, we can use Word forms, NOUN Synonyms, VERB synonyms, and DerivationallyForm, BUT NOT Verb Hyponyms
			    // For states we only use wordforms and deriavationallyform
				// now we add the different wordforms and wordnet word forms
				for (ArrayList<String> t : reasonsList) {
					// all word forms
					for (String r : t) {
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
			    List<String> allReasonListWordForms_List = new ArrayList<String>();
			    allReasonListWordForms_List.addAll(finalReasonSet);
			 
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
			for (String r : subStates) {
				// all word forms
				System.out.println(r);
				Set reasonListWordForms = wf.getAllWordForms(r);	
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
			negationTerms = srl.getNegationTerms();
			System.out.print("FINAL negationTerms >> ");  for (String line : negationTerms)     {                	System.out.print(line+ ", ");            }		  System.out.println(", ");
			conditionalTerms= srl.getConditionalTerms();
			System.out.print("FINAL conditionalTerms >> ");  for (String line : conditionalTerms)     {                	System.out.print(line+ ", ");            }		  System.out.println(", ");
			//decision terms
			decisionTerms= srl.getDecisionTerms();
			System.out.print("FINAL deisionTerms >> ");  for (ArrayList<String> line : decisionTerms)     {                	System.out.print(line+ ", ");            }		  System.out.println(", ");
			// For states we only use wordforms and deriavationallyform
			// now we add the different wordforms and wordnet word forms
			Set<String> finalDecisionSet = new HashSet<String>();
			for (ArrayList<String> t : decisionTerms) {
				for (String r : t) {
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
			}
			//convert to list
		    List<String> allDecisionWordForms_List = new ArrayList<String>();
		    allDecisionWordForms_List.addAll(finalDecisionSet);
			
			//statesList = ObjectArrays.concat(statesList, decisionMechanismsSubStates, String.class); //we will combine statesList and decisionMechanismsSubStates
			//statesList.addAll(decisionMechanismsSubStates);
			//specialTerms = ObjectArrays.concat(specialTerms, entities, String.class);	//special terms and entities are combined
			//specialTerms.addAll(entitiesList);
			positiveWords = srl.getPositiveWords();
			negativeWords = srl.getNegativeWords();
			
			
			/*
			String words[] = {"decide","decisive","decision","reason","accept","accepted"};
			for(String wordForm : words) {	
				System.out.println(wordForm);
				wf.getAllWordForms(wordForm);
				System.out.println("\tWordNet Synonyms for "+ wordForm);	System.out.println("\t"+ ws.returnSynonyms(wordForm));	System.out.println();
				
				// taken from https://github.com/xiejuncs/cross-document-coreference-resolution/blob/master/featureExtractor/Wordnet.java
				// https://www.cs.uic.edu/~spurohit/documents/Algorithm%20details.pdf
				Set<String> synonyms = ws.getSynonym(wordForm, SynsetType.NOUN); //region 
				System.out.println("\tNOUN Synonyms for word: " + wordForm); System.out.print("\t");	for (String s : synonyms) {  System.out.print(s + ", ");	} System.out.println();
				Set<String> synonymsVERB = ws.getSynonym(wordForm, SynsetType.VERB); //region 
				System.out.println("\tVERB Synonyms for word: " + wordForm); System.out.print("\t");	for (String s : synonymsVERB) {  System.out.print(s + ", ");	} System.out.println();
				Set<String> nounHypernyms = ws.getNounHypernym(wordForm);  //tent
				System.out.println("\tNounHypernyms for word: " + wordForm); System.out.print("\t");	for (String s : nounHypernyms) {  System.out.print(s + ", ");	}	System.out.println();
				Set<String> verbHypernyms = ws.getVerbHypernym(wordForm); //shout
				System.out.println("\tVerbHypernyms for word: " + wordForm); System.out.print("\t");	for (String s : verbHypernyms) {  System.out.print(s + ", ");	}	System.out.println();
				Set<String> derivationallyForm = ws.getDerivationallyRelatedForms(wordForm, SynsetType.VERB); //develop
				System.out.println("\tDerivationallyForm for word: " + wordForm);	System.out.print("\t");	for (String s : derivationallyForm) {  System.out.print(s + ", ");	}	System.out.println();
			}
			*/
		}

}
