package utilities;

import com.google.common.collect.ObjectArrays;


import wordnet.jaws.WordnetSynonyms;
import connections.PropertiesFile;


import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

//For final part, these terms have to be mined..
//maybe we can use triple extraction from jep message sentences to find the nouns/verbs which occur in same sentence as 'jep' and 'jep state'

public class StateAndReasonLabels {
	boolean showList=false;
	//below terms and probability for reasons
	//String reasonTerm;	//for example, support or consensus
	//float probablity;	//consensus has higher probability than support
	
	List<String> committedStates = new ArrayList<String>();// List<String> stateCommits = {"Status: Draft","Status: Active","Status: Accepted","Status: Rejected","Status: Final","Status: Defer","Status: Withdraw","Status: Postpone","Status: Replace","Status: Incomplete",
														//"Status: Supercede","Status: Superseded","Status: Close","Status: Pending"};
	List<String> proposalIdentifiers  = new ArrayList<String>(); //TERMS   List<String> identifiers = {"pep","proposal","idea"};
	List<String> verbs 		  = new ArrayList<String>();	//verbs associated with states and decisionMechanismsSubStates .. List<String> verbs = {"ready"};  //ready for review/pronouncement
	//List<String> mainStates   = new ArrayList<String>();	//  //states
	ArrayList<ArrayList<String>> mainStates = new ArrayList<ArrayList<String>>();	//one sttae in each line with all its varients and similar terms in teh same line
	//List<String> mainStates =  {"accept","approve","reject","wrote","written","propose","draft","update","revise","withdraw","defer","postpone","replace","incomplete","supercede","final","close",
	//		"accepting","approving","rejecting","writing","proposing","drafting","updating","revising","withdrawing","deferring","postponing","replacing","incompleting","superceding","finalizing","closing","pending",
	//		"accepted","approved","rejected","wrote","written","proposed","updated","revised","withdrawn","deferred","postponed","replaced","incompleted","superceded","closed"};
															
	List<String> subStates = new ArrayList<String>();	//additional states we have found List<String> decisionMechanismsSubStates =  
																	 	//{"poll","poll result","vote","vote result","voted","voting","survey","survey results","review","pronounce","pronouncement",
																		//"request number","implement","resolution", "notice"}; //added reviewing as a state???
    
	List<String> discussion = new ArrayList<String>();	//discussion ..List<String> discussion = {"discussion pep","discussion idea","discussion syntax"};	//"discussion",	//takes too long - shud be done individually
	ArrayList<ArrayList<String>> reasonTerms    = new ArrayList<ArrayList<String>>();		//REASONS all below is on HOW pep was decided..reasons do exist on its own
	/* String reasons[] = {"debate","discussion", //- to many results would be returned
  			"issues raised","concerns raised","addressed issues raised",
  			"consensus","no consensus","no consensus reached","rough consensus","community consensus", "guido community consensus","bdfl community consensus",
  			"feedback","favorable feedback",  		
  			"little interest", "python community",
  			"support","no support","no popular support",
  			"majority","no majority","overwhelming majority",
  			"poor syntax","limited utility","difficulty of implementation","bug magnet","stalled",
  			"controversy","wrangling","rant","dictatorship","dictator",
  			"decision", "overidden", "decision overridden",
  			"bdfl majority","bdfl","bdfl pronouncement","bdfl pronouncements","best judgement","pronounce","pronouncement","delegate",
  			"lazy consensus","lazy majority"  							
  			};   
	*/
	

	
	//REASON IDENTIFIER TERMS
	List<String> reasonIdentifierTerms = new ArrayList<String>(); //String reasonIdentifierTerms[] = {"because","since", "based on","due to","thanks to","accordingly","as a result of","through"};
	
    //idea stage			
    //List<String> ideaStage = {};  integrated in other lists
    
    //ENTITIES AND special terms used together  	
  	//Entities all below is WHO decided
	List<String> entities = new ArrayList<String>();  //String[] entities = {"pep dictator","pep champion","bdfl delegate committee","community","committers vote core developers","czar","pep czar","ppe-bdfl","pep editor","bpd","bpc","flufl","committee decision","committee decisions","dictatorship","dictator","delegate"}; 	
    //Special terms .. additional 'points in time' which are important we have found
	ArrayList<ArrayList<String>> specialOSCommunitySpecificTerms = new ArrayList<ArrayList<String>>(); //String[] specialTerms = {"+pronouncement","quick pronouncement","+pep acceptance:","pep rejection","+pep deferral","+acceptance","+resolution:","rejection notice","bdfl pronouncement","+pep deferral","+rejection"};	//"resolution",
	//standard states changes by users i want to capture 
	//"draft" ,	dont need draft as its always by author
	
	//find the order of events
	List<String> storyline = new ArrayList<String>(); //String storyline[] = {"following", "after","later", "afterwards"};	//"the PEP"	"was accepted"	"by Guido + After a short discussion
		
	List<String> positiveWords = new ArrayList<String>(); 
	List<String> negativeWords = new ArrayList<String>(); 
	
	List<String> negationTerms = new ArrayList<String>(); //static String negationTerms[] = {"if","not","do not","will be","can","can be","is n't","nor","should","should be","would","needs to be","may","will","hav n't","have n't","might", "n't","whether","zero","no","never","never been","need","would need","before","zero"}; // added later for test												// "should","should be"
	List<String> conditionalTerms = new ArrayList<String>(); 	//static String conditionalList[] = {"if","should","should be","when","can","can be","unless","i hope","whether","once","would","might","why"};
	ArrayList<ArrayList<String>> decisionTerms= new ArrayList<ArrayList<String>>();
	
	//dec 2018
	List<String> notCoOccurTerms = new ArrayList<String>();  
	
	public void initStateAndReasonLabels(WordnetSynonyms wn, boolean output) {	
		
		try {
			//read the fileNames from the propoerties file
			PropertiesFile wpf = new PropertiesFile();
    		// wpf.WriteToPropertiesFile("includeEmptyRows", includeEmptyRows.toString());
    		//includeStateData						
            committedStates = readLines(wpf.readFromPropertiesFile("stateCommitsFileName",false));	//each of the lists are read in from a separate file	
            if(showList) {
            	System.out.println("\ncommittedStates readIn:");   for (String line : committedStates)     {                	System.out.print(line+ " ");            }		  System.out.println(" ");  
            }
            mainStates = readLinesIntoListsOfLists(wpf.readFromPropertiesFile("mainStatesFileName",false));	//each of the lists are read in from a separate file	
            if(showList) {
            	System.out.println("\ncommonStatesAllTermForms readIn::"); 
            }
            if(output) {
            	for (ArrayList<String> line : mainStates) {  //for each line     	
            		if(showList) {
            			 System.out.println("\nRead in allStates line: "+ line+ " : ");
            		}
            		for (String val : line) {  //for each term in line
	            		ArrayList<String> al = new ArrayList<String>(); 
	            		//al = wn.returnSynonyms(val);			//return the synonyms
	            		if(showList) {
	            			System.out.print("Term: "+ val+ " : ");
	            		}
//	            		for (String l : al) {                	
//	                		System.out.print("'"+l+ "', ");
//	            		}
//	            		System.out.println(" "); 
            		}
            	}		  System.out.println(" ");  
            }
            subStates = readLines(wpf.readFromPropertiesFile("decisionMechanismsSubStatesFileName",false));	//each of the lists are read in from a separate file	
            if(showList) {
            	System.out.println("\nsubStates readIn:");     for (String line : subStates)     {   System.out.print(line+ ", ");            }		  System.out.println(" ");
		    }
            reasonTerms = readLinesIntoListsOfLists(wpf.readFromPropertiesFile("reasonsFileName",false));	//each of the lists are read in from a separate file	
            if(showList) {
            	System.out.println("\nreasonTerms readIn:");   for (ArrayList<String> line : reasonTerms)     {                		System.out.print(line+ ", ");            }		  System.out.println(" ");  
            }
            reasonIdentifierTerms = readLines(wpf.readFromPropertiesFile("reasonIdentifierTermsFileName",false));	//each of the lists are read in from a separate file	
            if(showList) {
            	System.out.println("\nreasonIdentifierTerms readIn:"); for (String line : reasonIdentifierTerms)     {                System.out.print(line+ ", ");            }		  System.out.println(" ");  
            }
            proposalIdentifiers = readLines(wpf.readFromPropertiesFile("identifiersFileName",false));	//each of the lists are read in from a separate file	
            if(showList) {
            	System.out.println("\nproposalIdentifiers readIn:"); for (String line : proposalIdentifiers)     {                System.out.print(line+ ", ");            }		  System.out.println(" ");
            }
            entities = readLines(wpf.readFromPropertiesFile("entitiesFileName",false));	//each of the lists are read in from a separate file	
            if(showList) {
            	System.out.println("\nentities readIn:"); 	  for (String line : entities)     {               		System.out.print(line+ ", ");            }		  System.out.println(" ");  
            }
            specialOSCommunitySpecificTerms = readLinesIntoListsOfLists(wpf.readFromPropertiesFile("specialTermsFileName",false));	//each of the lists are read in from a separate file	
            if(showList) {
            	System.out.println("\nspecialOSCommunitySpecificTerms readIn:"); for (ArrayList<String> line : specialOSCommunitySpecificTerms)     {                  System.out.print(line+ " ");            }		  System.out.println(" ");  
            }
            positiveWords = readLines(wpf.readFromPropertiesFile("positiveTermsFileName",false));	//each of the lists are read in from a separate file	
            if(showList) {
            	System.out.println("\npositiveWords readIn:"); for (String line : positiveWords)     {                 System.out.print(line+ ", ");            }		  System.out.println(" ");  
            }
            negativeWords = readLines(wpf.readFromPropertiesFile("negativeTermsFileName",false));	//each of the lists are read in from a separate file	
            if(showList) {
            	System.out.println("\nnegativeWords readIn:"); for (String line : negativeWords)     {                 System.out.print(line+ ", ");            }		  System.out.println(" ");
            }
            negationTerms = readLines(wpf.readFromPropertiesFile("negationTermsFileName",false));	//each of the lists are read in from a separate file	
            if(showList) {
            System.out.println("\nnegationTerms readIn:"); for (String line : negationTerms)     {                 System.out.print(line+ ", ");            }		  System.out.println(" "); 
            }
            conditionalTerms = readLines(wpf.readFromPropertiesFile("conditionalTermsFileName",false));	//each of the lists are read in from a separate file	
            if(showList) {
            	System.out.println("\nconditionalTerms readIn:"); for (String line : conditionalTerms)     {                 System.out.print(line+ ", ");            }		  System.out.println(" "); 
            }
            decisionTerms = readLinesIntoListsOfLists(wpf.readFromPropertiesFile("decisionTermsFileName",false));	//each of the lists are read in from a separate file	
            if(showList) {
            	System.out.println("\ndecisionTerms readIn:"); for (ArrayList<String> line : decisionTerms)     {                 System.out.print(line+ ", ");            }		  System.out.println(" ");
            }
            //nov 2018
            notCoOccurTerms = readLines(wpf.readFromPropertiesFile("notCoOccurTermsFileName",false));	
            if(showList) {
            	System.out.println("\nnotCoOccurTerms readIn:"); for (String line : notCoOccurTerms)     {                 System.out.print(line+ ", ");            }		  System.out.println(" ");
            }
            //dec 2018
  //          notCoOccurTerms = readLines(wpf.readFromPropertiesFile("notCoOccurTermsFileName",false));	
  //          System.out.println("\nnotCoOccurTerms readIn:"); for (String line : notCoOccurTerms)     {                 System.out.print(line+ ", ");            }		  System.out.println(" ");
		}
        catch(IOException e)   {
            // Print out the exception that occurred
            System.out.println("Unable to read from file : "+e.getMessage());
            e.printStackTrace();
			System.out.println(StackTraceToString(e)  );	

        }
	}
    
    //COMBINATION.. final list we want to process 
	String[] finalLine = new String[100000];
    Integer counter=0;
//    List<String> statesList = ObjectArrays.concat(mainStates, decisionMechanismsSubStates, String.class);	//mostly important
//    List<String> reasonsList = ObjectArrays.concat(entities, reasons, String.class);
	   
    public List<String> getVerbs() 						{ 	return verbs; 			}
    public List<String> getCommittedStates()  			{ 	return committedStates; 	}
    public List<String> getReasonIdentifierTerms() 		{ 	return reasonIdentifierTerms; 	}//reason identifier terms
	public ArrayList<ArrayList<String>> getMainStatesAllTermForms() 	{ 	return mainStates; 		}
	public List<String> getSubStates() 				{ 	return subStates; 	}
	public List<String> getDiscussion() 				{ 	return discussion; 		}
	//public List<String> getIdeaStage() 				{ 	return ideaStage; 		}
	public List<String> getEntities() 					{	return entities; 		}
	public ArrayList<ArrayList<String>> getSpecialOSCommunitySpecificTerms() 	{	return specialOSCommunitySpecificTerms;	}	
	public ArrayList<ArrayList<String>> getReasonTerms() 					{	return reasonTerms;			}
	
	public List<String> getPositiveWords() 				{	return positiveWords;	}	
	public List<String> getNegativeWords() 				{	return negativeWords;	}
	
	public List<String> getNegationTerms() 				{	return negationTerms;	}	
	public List<String> getConditionalTerms() 				{	return conditionalTerms;	}
	public ArrayList<ArrayList<String>>  getDecisionTerms() 				{	return decisionTerms;	}
	
	public List<String> getNotCoOccurTerms() {		return notCoOccurTerms;	}
	public void setNotCoOccurTerms(List<String> notCoOccurTerms) {		this.notCoOccurTerms = notCoOccurTerms;	}
	
	//This is what we want as all types of states
	public ArrayList<ArrayList<String>> getAllStatesList_StatesSubstates() {
		
		
        //al.addAll(mainStates);        al.addAll(subStates);
		for(String d : subStates) {
			ArrayList<String> al= new ArrayList<String>();
			al.add(d);
			mainStates.add(al);
		}
        return mainStates;
		//return ObjectArrays.concat(mainStates, decisionMechanismsSubStates, String.class);
		//return statesList;	
	}
	//This is what we want as all types of reasons
/*	public List<String> getReasonsList() {	
		ArrayList<String> al= new ArrayList<String>();
        al.addAll(entities);        al.addAll(reasonTerms);
        return al;
		//return ObjectArrays.concat(entities, reasons, String.class);
		//return reasonsList;	
	} */
	
	public List<String> getProposalIdentifierTerms() {		return proposalIdentifiers;	}
	
	public List<String> readLines(String filename) throws IOException 
    {
        FileReader fileReader = new FileReader(filename);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        List<String> lines = new ArrayList<String>();
        String line = null;         

        while ((line = bufferedReader.readLine()) != null)       {
        	line= line.trim();
        	if(line.startsWith("--") || line.startsWith(";") || line.isEmpty() || line.equals("")) {}
        	else {
        		lines.add(line);
            }
        }       
        bufferedReader.close();
        return lines; //lines.toArray(new String[lines.size()]);
    } 
	//read into Lists of Lists
	public ArrayList<ArrayList<String>> readLinesIntoListsOfLists(String filename) throws IOException 
    {
        FileReader fileReader = new FileReader(filename);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        ArrayList<ArrayList<String>> listOLists = new ArrayList<ArrayList<String>>();
        String line = null;         

        while ((line = bufferedReader.readLine()) != null)         {
        	line= line.trim();
        	if(line.startsWith("--") || line.startsWith(";") || line.isEmpty() || line.equals("")) {}
        	else {
        		//lines.add(line);
        		ArrayList<String> singleList = new ArrayList<String>(); //create a list
        		if (line.contains(",")) {
        			for (String term: line.split(",")) {	//split line and read into that list
        				singleList.add(term);		
        			}
        		}
        		else {
        			singleList.add(line);
        		}
        		listOLists.add(singleList); //add to main list
            }
        }       
        bufferedReader.close();
        return listOLists; //lines.toArray(new String[lines.size()]);
    } 
	
	public static String StackTraceToString(Exception ex) {
		String result = ex.toString() + "\n";
		StackTraceElement[] trace = ex.getStackTrace();
		for (int i=0;i<trace.length;i++) {
			result += trace[i].toString() + "\n";
		}
		return result;
	}

	//THE BELOW FUNCTION MAY NOT BE USED
	//main issue is that we dont have to match the exact term
	/*
	public List<String> prepareTerms(Integer stateTermsChoice) {	//Locations would be handled in parent code
    	if(stateTermsChoice==0) {	//terms [and pep and proposal]
             for (String word1 : statesList) {  //StatesAndSubStates) {
                     for (String word3 : constant) {	//ActualReasons
                         if ( !word3.equals(word1)) {
                             //System.out.println(word1 +  " "+ word3);
                             finalLine[counter] = (word1 +" "+ word3);
                             counter++;
                         }
                     }
             }
    	}
    	else if (stateTermsChoice==1) { //terms and reasons [and peps and proposal]
    		for (String word1 : statesList) {  //StatesAndSubStates) {
                for (String word2 : reasonsList) {
                    if ( !word1.equals(word2)) {
                        for (String word3 : constant) {	//ActualReasons
                            if ( !word3.equals(word2) && !word3.equals(word1)) {
                                //System.out.println(word1 + " "+ word2 + " "+ word3);
                                finalLine[counter] = (word1 +" "+ word2 +" "+ word3);
                                counter++;
                            }
                        }
                    }
                }
            }
    	}
    	else if (stateTermsChoice==2) {
            for (String word1 : statesList) {  //StatesAndSubStates) {
                for (String word2 : reasonIdentifierTerms) {
                    if ( !word1.equals(word2)) {
                        for (String word3 : constant) {	//ActualReasons
                            if ( !word3.equals(word2) && !word3.equals(word1)) {
                            	for (String word4 : reasonsList) {
                            		if ( !word3.equals(word2) && !word3.equals(word1) && !word4.equals(word1) && !word4.equals(word2) && !word4.equals(word3)) {
		                                //System.out.println(word1 + " "+ word2 + " "+ word3);
		                                finalLine[counter] = (word1 +" "+ word2 +" "+ word3 + " " + word4);
		                                counter++;
                            		}
                            	}
                            }
                        }
                    }
                }
            }
    	}
    	return finalLine;
	}
	*/
}