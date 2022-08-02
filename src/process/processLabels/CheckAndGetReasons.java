package Process.processLabels;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import callIELibraries.JavaOllieWrapperGUIInDev;
import Process.processMessages.LabelTriples;
import Process.processMessages.ProcessingRequiredParameters;
import Process.processMessages.PythonSpecificMessageProcessing;
import callIELibraries.ReVerbFindRelations;
import de.mpii.clause.driver.ClausIEMain;
import de.mpii.clause.driver.Reasons;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.process.DocumentPreprocessor;

public class CheckAndGetReasons {
	// Four reason related variables
	// reason identifier terms - 'reasonIdentifierTerms'  - eg because
	// reason labels (triples) read in from file -  reasonLabels
	// actual reasons - ActualReasons - eg favourable feedback
	// states To Get Roles And Reasons - statesToGetRolesAndReasons - we only want to get roles and reasons for specific states, eg accepted, rejected, etc
	
	
	// How I Check for reasons:
	// - via reason terms in extracted triple (E.g. "I 've updated PEP 345 with this feedback ." - extracted = "I"	"'ve"	"updated PEP 345 with this feedback")
	// - via reason terms in nearby sentences and paragraphs
	// - via reason triple matching  in nearby sentences and paragraphs
	
	// later in other sentences of pep messages, the reason triples are also matched
	
	// Example reasons
	// I 've updated PEP 345 with this feedback .
	// I 've updated the PEP accordingly , and also made some changes after the discussions we had with Martin on Distutils-SIG on how versions are defined . .
	// Having read the PEP I heartily approve .
	// Since there has been some controversy about the joining syntax used in PEP 428( filesystem path objects) I would like to run an informal poll about it .
	// I 've updated PEP 428 following the previous discussion . Following our discussion of last week , here is a first draft of the PEP .
	// but this was rejected for being too magical , and too confusing to those who are n't expecting it . . .
	// this one is split into 2 sentences
	// I have reviewed the latest version of PEP 435 and I see that it is very good .
	// I hereby declare PEP 435 as Accepted .
	// check reason triples in the entire paragrapgh
	// check reasons in triples	
		
	static boolean showCIEDebugOutput = false;
	
	public static boolean checkAndGetReasons_for_sentenceTriple_whereTriplesMatched(TripleProcessingResult result, String entireMessage,String v_tripleArray[][], ClausIEMain cm, ReVerbFindRelations rvr, JavaOllieWrapperGUIInDev jw,
			String entireParagraph, String currentSentence, String prevSentence, String nextSentence, String previousParagraph, String nextParagraph, 
			String extracted_subject, 	String extracted_object, String extracted_relation, String v_idea, Integer v_pepNumber, Date v_date, Integer v_message_ID, String author, Integer sentenceCounter, 
			FileWriter writerForDisco, FileWriter writerAll, String conditionalStrings[], String v_negationTerms[], String [] v_reasonIdentifierTerms, ArrayList<LabelTriples>  reasonTripleLabels, String v_ActualReasons[], 
			Integer libraryToCheck, ProcessingRequiredParameters prp) throws IOException{
		
		boolean foundReasonOnce = false;
		//dont need these as reasons are part of triple processing result
		/*
		Reasons reason = new Reasons();
		// set default value
		reason.SetValues(false, "");
		*/
		nextSentence = result.getNextSentence();
		
		// check for terms like because
		// System.out.println("Inside checking reason, current matched S,V,O subject (" + v_subject + ") verb (" + v_relation + ") object (" + v_object +")");
		// in the code below we want to check with pos the resonsTerm exists so that the text afterwards can be extracted - which then can be evaluated foR triples
		// System.out.println("heer a");  "because","based on","due to","thanks to"
		
		// 1. check ExtractedTriple For ReasonTerms
		// Example "I 've updated PEP 345 with this feedback ."
		System.out.println("\t\t\t<! Checking ReasonTerms in ExtractedTriples !>");
		foundReasonOnce = checkExtractedTripleForReasonTerms(v_ActualReasons, reasonTripleLabels,extracted_subject, extracted_object, extracted_relation, foundReasonOnce, result,prp);
		
		return foundReasonOnce;
	}
	
	
	//this function is only called if certain labels are captured -  statesToGetRolesAndReasons
	//and then for those labels , the reasons are extracted 
	//we add results to reason object
	public static boolean checkAndGetReasons_for_sentence_whereTriplesMatched(TripleProcessingResult result, String entireMessage,String v_tripleArray[][], ClausIEMain cm, ReVerbFindRelations rvr, JavaOllieWrapperGUIInDev jw,
			String entireParagraph, String currentSentence, String prevSentence, String nextSentence, String previousParagraph, String nextParagraph, 
			String vv_subject, 	String vv_object, String vv_relation, String v_idea, Integer v_pepNumber, Date v_date, Integer v_message_ID, String author, Integer paragraphCounter, Integer sentenceCounter, 
			FileWriter writerForDisco, FileWriter writerAll, String conditionalStrings[], String v_negationTerms[], String [] v_reasonIdentifierTerms, ArrayList<LabelTriples>  reasonTripleLabels, String v_ActualReasons[], 
			Integer libraryToCheck, ProcessingRequiredParameters prp) throws IOException
	{		
		System.out.println("\t\t\t<! Inside Starting Check Reasons Function!>");
		boolean foundReasonOnce = false;
		//dont need these as reasons are part of triple processing result
		/*
		Reasons reason = new Reasons();
		// set default value
		reason.SetValues(false, "");
		*/
		nextSentence = result.getNextSentence();
		
		// check for terms like because
		// System.out.println("Inside checking reason, current matched S,V,O subject (" + v_subject + ") verb (" + v_relation + ") object (" + v_object +")");
		// in the code below we want to check with pos the resonsTerm exists so that the text afterwards can be extracted - which then can be evaluated foR triples
		// System.out.println("heer a");  "because","based on","due to","thanks to"
		
		//this code moved up
		// 1. check ExtractedTriple For ReasonTerms
		// Example "I 've updated PEP 345 with this feedback ."
//xx		System.out.println("\t\t\t<! Checking ReasonTerms in ExtractedTriples !>");
//xx		foundReasonOnce = checkExtractedTripleForReasonTerms(v_ActualReasons, reasonTripleLabels, foundReasonOnce, result);
			
		//added new code for seprate task - get reason in the sentence where label is matched
		System.out.println("\t\t\t<! Checking Reasons in same Sentences !>");
		foundReasonOnce = checkingReasonsTermsTriplesInSameSentences(currentSentence,v_ActualReasons, foundReasonOnce, result, cm,  reasonTripleLabels,prp);
		
		// 2. Check for single terms in nearby sentences and paragraphs - Look for reasons, go over all reasons and see if they
		// Example "I 've updated PEP 428 following the previous discussion . Following our discussion of last week , here is a first draft of the PEP ."
		System.out.println("\t\t\t<! Checking ReasonTerms in Nearby Sentences and Paragraphs !>");
		foundReasonOnce = checkNearbySentencesAndParagraphsForReasonTerms(entireParagraph, currentSentence,	prevSentence, nextSentence, previousParagraph, nextParagraph, v_ActualReasons, foundReasonOnce, result,prp);
		
		
		//3. CHECK REASON TRIPLES IN CURR AND NEARBY PARAGRAPHS
		//First check triples from reason input file in: same sentence the rest of the triples
		//Then extract triples from these and check curr, next and previous sentence and also in curr, previous and next paragraph
		System.out.println("\t\t\t<! Computing ClausIE For Reason Triples in nearby Sentences and Paragraphs!>");		
		//extract and return triples from paragraphs, previous, current and next paragraph
		
		foundReasonOnce = checkNearbySentencesParagraphsForReasonTriples(prevSentence, nextSentence,entireParagraph, previousParagraph, nextParagraph, result,cm, reasonTripleLabels,prp);
	
		//NOT NEEDED as this is performed while checking curr paragraph
//		foundReasonOnce = checkSentenceForReasonTriples(v_tripleArray, cm, currentSentence, v_idea, v_pepNumber, v_date,v_message_ID, author, sentenceCounter, writerForDisco, writerAll, conditionalStrings, v_reasonTerms,
		
		//???? should add or not
		if (foundReasonOnce = false) {
			// if bdfl pronouncement
			// check next paragraph
		}
		return foundReasonOnce;
	}

	private static boolean checkingReasonsTermsTriplesInSameSentences(String currentSentence,String[] v_ActualReasons, boolean foundReasonOnce, TripleProcessingResult result,ClausIEMain cm, 
			ArrayList<LabelTriples> reasonLabels, ProcessingRequiredParameters prp)
	{
		//we want to make sure the reason captured is not the same as the label
		String label = result.getClausIE();
		
		//check terms in that sentence
		for (String r : v_ActualReasons) {
			//check for reason terms in previous sentence, just simple string matching	
			
			Integer c=0;						
			if (currentSentence != null) {
				if (currentSentence.contains(r) && !label.equals(r)) {
					foundReasonOnce = true;
					System.out.println("\t\t\t<!\t\tThe reason Term found in same sentences (counter) " + c +  " = " + r + " !>");
					if (r.contains(" "))
						r = r.replaceAll(" ","_");
					// set reason						
					//result.setFinalReason(r);
					result.getReasonObject().SetValues(true, r);
					result.getReasonObject().setReasonInSentence(r);
					result.getReasonObject().addToReasonListArray(r);
					try {
						String filename= "c:\\scripts\\Reasons\\ReasonTermsInSameSentence.txt";
					    FileWriter fw = new FileWriter(filename,true); //the true will append the new data
					    fw.write("PEP " + result.getPepNumber() + " | "+ result.getClausIE()+ " | " + r + " | " + result.getCurrentSentence() + " | "+ result.getPrevSentence() + " | "+ result.getNextSentence() );//appends the string to the file
					    //fw.write("\r\n");
					    //fw.write("Reason Term found: "+ cs);
					    fw.write("\r\n");
					    fw.close();
					}
					catch(IOException ioe)
					{
					    System.err.println("IOException: " + ioe.getMessage());
					}
				}
			}
			c++;			
		}
		String location = "sameSentence";
		//check triples in same sentence
		if (currentSentence.split(" ").length < 75)
		{					
			//ONLY CHECK FOR TRIPLES IF SENTENCE CONTAINS TWO OR THREE TERMS
			try {
				if (checkIfSentenceContainsTripleDoubleTerms(currentSentence, reasonLabels))	{	
					foundReasonOnce = checkSentenceForReasonTriples(currentSentence, result, cm, reasonLabels,location,prp);
					//result.getReasonObject().setReasonInSentence(reason);
					//result.getReasonObject().addToReasonListArray(reason);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		
		return foundReasonOnce;
	}
	
	// we look for reasons in nearby sentences and paragraphs using actual reasons
	private static boolean checkNearbySentencesAndParagraphsForReasonTerms(String entireParagraph, String currentSentence, String prevSentence, String nextSentence, String previousParagraph,
			String nextParagraph, String[] v_actualReasons, boolean foundReasonOnce, TripleProcessingResult result, ProcessingRequiredParameters prp) {
		
		//we want to make sure the reason captured is not the same as the label
		String label = result.getClausIE();
		
		Integer counter=0;
		String filename = "";
		String[] strings = {prevSentence,currentSentence, nextSentence, previousParagraph,entireParagraph, nextParagraph};
		for (String r : v_actualReasons) {
			// System.out.println("checking reasons one by one: (" + r + ")
			// in leftOverReason (" + leftOverReason+")");
			// SECOND way check for reason terms in previous sentence, just simple string matching			
			// the reason will get captured twice is its in sentences, as will be caught in paragraphs also
			
			//Integer c=0;
			counter=0;
			for (String s: strings){
				if (s != null) {
					if (s.contains(r)	&& !label.equals(r)) {	// 
						foundReasonOnce = true;
						System.out.println("\t\t\t<!\t\tThe reason found in nearby sentences or paragraphs (counter) " + counter +  " = " + r + " !>");
						if (r.contains(" "))
							r = r.replaceAll(" ","_");
						// set reason						
						//result.setFinalReason(r);
						result.getReasonObject().SetValues(true, r);
						result.getReasonObject().addToReasonListArray(r);
						result.getReasonObject().setReasonTermsInNearbySentencesParagraphs(r);
						
						//for previous and next sentences
						if(counter==0){
							result.getReasonObject().setReasonTermsInPreviousSentence(r);
							filename= prp.getScriptsHomeFolderLocation() + "outputFiles\\ReasonOutput\\ReasonTermsInPreviousSentences.txt";
							try{								
							    FileWriter fw = new FileWriter(filename,true); //the true will append the new data							    
							    fw.write("PEP " + result.getPepNumber() + "| " + result.getClausIE() + " | "+ result.getPrevSentence() + " | "+ result.getCurrentSentence() + " | " + r + " | " + s);//appends the string to the file
							    fw.write("\r\n");
							    fw.close();
							}
							catch(IOException ioe)	{
							    System.err.println("IOException: " + ioe.getMessage());
							}							
						}
						else if(counter==2) {
							result.getReasonObject().setReasonTermsInNextSentence(r);
							filename= prp.getScriptsHomeFolderLocation() + "outputFiles\\ReasonOutput\\ReasonTermsInNextSentences.txt";
							try	{								
							    FileWriter fw = new FileWriter(filename,true); //the true will append the new data
							    fw.write("PEP " + result.getPepNumber() + "| " + result.getClausIE() + " | "+r + " | " +  result.getCurrentSentence() + "| " + result.getNextSentence() + " | " + s);//appends the string to the file
							    fw.write("\r\n");
							    fw.close();
							}
							catch(IOException ioe) {
							    System.err.println("IOException: " + ioe.getMessage());
							}
						}
						else {
							Integer sentenceCounter=0;
							if (s==null || s.isEmpty())
							{}
							else {
								Reader reader = new StringReader(s);
								DocumentPreprocessor dp = new DocumentPreprocessor(reader);
								//find which sentence contains the term
								for (List<HasWord> eachSentence : dp) 		        	
								{	
									sentenceCounter++;
									if(sentenceCounter<10) {
										String CurrentSentenceString = Sentence.listToString(eachSentence);
										//System.out.println("Sentence: "+CurrentSentenceString);
										if (CurrentSentenceString.split(" ").length < 75  && CurrentSentenceString.contains(r)) {	
											//first check terms, in each of the sentences
											filename= prp.getScriptsHomeFolderLocation() +"outputFiles\\ReasonOutput\\ReasonTermsInEntire-PreviousAndNextParagraphs.txt";
											try	{
												result.getReasonObject().setReasonTermsInNearbySentencesParagraphs(r);
											    FileWriter fw = new FileWriter(filename,true); //the true will append the new data
											    fw.write("PEP " + result.getPepNumber() + " | "+ result.getClausIE()+ "| " + r + " | " + result.getCurrentSentence() + "|" + CurrentSentenceString);//appends the string to the file
											    fw.write("\r\n");
											    fw.close();
											}
											catch(IOException ioe)	{
											    System.err.println("IOException: " + ioe.getMessage());
											}											
										}
									}
									else {
										break;	//exit loop if more than 10 sentences
									}
								}
								sentenceCounter=0;
							}							
						}
					}
				}
				counter++;
			}
			counter=0;
		}
		return foundReasonOnce;
	}

	private static boolean checkNearbySentencesParagraphsForReasonTriples(String previosuSentence, String nextSentence,String entireParagraph, String previousParagraph, String nextParagraph, 
			TripleProcessingResult result, ClausIEMain cm, ArrayList<LabelTriples> reasonLabels, ProcessingRequiredParameters prp)
			throws IOException {			
		String subject = "", object = "", verb = "", location="";
		boolean reasonFound=false;
		//System.out.println("\t\tChecking each sentence for Reason Triples");
		String[] sentences = {previosuSentence,nextSentence}; 
		String[] paragraphs = {previousParagraph,entireParagraph, nextParagraph};
		Integer sentenceCounter=0; //sometimes the number of sentences can be huge and actually be code
		Integer counter=0;
		
		//check sentences
		for (String sentence: sentences){
			if(counter==0)
				location = "previousSentence";
			else if(counter==1)
				location = "nextSentence";
			//ONLY CHECK FOR TRIPLES IF SENTENCE CONTAINS TWO OR THREE TERMS
			if (checkIfSentenceContainsTripleDoubleTerms(sentence, reasonLabels))		{	
				//System.out.println("\t\tReason Triple Terms Found in sentence");
				reasonFound = checkSentenceForReasonTriples(sentence, result, cm, reasonLabels,location,prp);
			}
			
		}
				
		//now check paragraphs
		for (String para: paragraphs){
			
			sentenceCounter=0;
			if (para==null || para.isEmpty())
			{}
			else {
				Reader reader = new StringReader(para);
				DocumentPreprocessor dp = new DocumentPreprocessor(reader);
//				System.out.println("here b..");
				
				for (List<HasWord> eachSentence : dp) 		        	
				{	
					sentenceCounter++;
					if(sentenceCounter<10) {
						String CurrentSentenceString = Sentence.listToString(eachSentence);
						//System.out.println("Sentence: "+CurrentSentenceString);
						if (CurrentSentenceString.split(" ").length < 75)
						{	
							//first check terms, in each of the sentences
							if(counter==0)
								location = "sentenceInPrevParagraph";
							else if (counter==1)
								location="restOfCurrParagraph";
							else if(counter==2)
								location="sentenceInNextParagraph";
							
							
							//ONLY CHECK FOR TRIPLES IF SENTENCE CONTAINS TWO OR THREE TERMS
							if (checkIfSentenceContainsTripleDoubleTerms(CurrentSentenceString, reasonLabels))		{	
								//System.out.println("\t\tReason Triple Terms Found in sentence");
								reasonFound = checkSentenceForReasonTriples(CurrentSentenceString, result, cm, reasonLabels,location,prp);
							}
						}
					}
					else {
						break;	//exit loop if mopre than 10 sentences
					}
				}
			}
			counter++;
		}
		return reasonFound;
	}

	//we check the extracted triple against reason labels
	private static boolean checkExtractedTripleForReasonTerms(String[] v_actualReasons,ArrayList<LabelTriples> v_reasonLabels, String v_subject, String v_object,String v_relation, boolean foundReasonOnce, 
			TripleProcessingResult result, ProcessingRequiredParameters prp) {
		boolean subjectFound = false, relationFound = false, objectFound = false, v_conditional = false;
		String leftOverReason = "", text = "";
		
		for (String cs : v_actualReasons) {
			subjectFound = false; 
			relationFound = false; 
			objectFound = false; 
			v_conditional = false;
			// System.out.println("heer c");
			// System.out.println("inside reasonTerm: " + cs);
			// error at v_subject below
			if (checkTerms(cs, v_subject)) {
				System.out.println("\t\t\t<!\t\tfound reasonTerm: " + cs + " in v_subject: " + v_subject + "!>");
				subjectFound = true;
				text = v_subject;
				// System.out.println("reason in subject");
				int spaceIndex = text.toLowerCase().indexOf(cs) + cs.length();
				if (spaceIndex != -1 && (spaceIndex != text.length())) { // -2 to handle these 2 characters																				
					// "}
					//temp commented
//###					leftOverReason = text.toLowerCase().substring(spaceIndex, v_subject.length() - 2);
				}
				// leftOver = text.replace(v_subject,"");
				// System.out.println("subjectFound, leftOver: " + leftOverReason);
			}
			if (checkTerms(cs, v_relation)) {
				System.out.println("\t\t\t<!\t\tfound reasonTerm: " + cs + " in v_relation: " + v_relation+ "!>");
				relationFound = true;
				text = v_relation;
				// System.out.println("reason in relation");
				int spaceIndex = text.toLowerCase().indexOf(cs) + cs.length();
				if (spaceIndex != -1 && (spaceIndex != text.length())) { // -2 to handle these 2 characters	
					// "}
					//temp commented
//##					leftOverReason = text.toLowerCase().substring(spaceIndex, v_relation.length() - 2);
				}
				// System.out.println("relationFound, leftOver: " + leftOverReason);
			}
			if (checkTerms(cs, v_object)) {
				System.out.println("\t\t\t<!\t\tfound reasonTerm: " + cs + " in v_object: " + v_object+ "!>");
				objectFound = true;
				text = v_object;
				// System.out.println("reason in object");
				int spaceIndex = text.toLowerCase().indexOf(cs) + cs.length();
				if (spaceIndex != -1 && (spaceIndex != text.length())) { // -2 to handle these 2 characters	
					// "}
					//temp commented
//##					leftOverReason = text.toLowerCase().substring(spaceIndex, v_object.length() - 2);
				}
				// System.out.println("objectFound, v_object: " + v_object);
				// System.out.println("objectFound, leftOver: " + leftOverReason);
			}
			// if contains, then subtract it from the next proposition
			// section = remaining
			// NOW WE CHECK FOR ACTUAL REASONS
			// static String reasons[] = { "poll results", "voting results","vote results", "consensus", "favorable feedback", "favourable feedback", "poor syntax","limited utility and difficulty of implementation", "bug magnet" };
			String cie = result.getClausIE();
			if (cie==null || cie.equals("")) cie = "";			
			if (subjectFound || relationFound || objectFound && !cie.equals(cs)) 
			{
				foundReasonOnce = true;
				System.out.println("\t\t\t<!\t\tfound reason = true !>");
				//for (String r : v_reasons) {
				//not sure we need the lopp
				/*
				for (int x=0; x <v_reasonLabels.size(); x++){
					/// System.out.println("checking reasons one by one: ("
					/// + r + ") in leftOverReason (" + leftOverReason+")");
					
					//STILL LEFT TO CORRECT THIS
					String r = v_reasonLabels.get(x).getIdea().toLowerCase();
					if (leftOverReason == null || leftOverReason.isEmpty()) {
						break;
					}
					// go over all reasons and see if they
					if (leftOverReason.contains(r)) {
						System.out.println("\t\t\t<!\t\tThe reason found IN TRIPLES = " + r + " !>");
						// set reason which is part of tripleprocvessing result
						//result.setFinalReason(r);
						result.getReason().SetValues(true, r);
						result.getReason().addToReasonListArray(r);
						result.getReason().setReasonTermsInMatchedTriple(r);
					}
				}
				*/
				System.out.println("\t\t\t<!\t\tThe reason term found IN TRIPLES = " + cs + " !>");
				// set reason which is part of tripleprocvessing result
				
				//Just add reason ..AUG 2017 
				//result.setFinalReason(cs);
				result.getReasonObject().SetValues(true, cs);
				result.getReasonObject().addToReasonListArray(cs);
				result.getReasonObject().setReasonTermsInMatchedTriple(cs);
				
				try
				{
				    String filename= prp.getScriptsHomeFolderLocation() + "outputFiles\\ReasonOutput\\ReasonTermInMatchedTriple.txt";
				    FileWriter fw = new FileWriter(filename,true); //the true will append the new data
				    fw.write("PEP " + result.getPepNumber() + " | "+ result.getClausIE() + " | " + cs + " | " + result.getCurrentSentence() + " | "+ result.getPrevSentence() + " | "+ result.getNextSentence());//appends the string to the file
				    //fw.write("\r\n");
				    //fw.write("Reason Term found: "+ cs);
				    fw.write("\r\n");
				    fw.close();
				}
				catch(IOException ioe)
				{
				    System.err.println("IOException: " + ioe.getMessage());
				}
			}
		} // end forloop reason check	
		return foundReasonOnce;
	}
	
	//Extract triples and check each against reason triples
	//For every extracted triple from sentence, 
	private static boolean checkSentenceForReasonTriples( String currentSentence,TripleProcessingResult result, ClausIEMain cm, ArrayList<LabelTriples> reasonLabels, String location,
			ProcessingRequiredParameters prp) throws IOException 
	{
		//extract triples from sentence
		ProcessLabels pt = new ProcessLabels();					//showCIEDebugOutput = true
		boolean foundReasonOnce=false;
		try {
			String tripleArray[][] = cm.computeClausIE(currentSentence, false);
			tripleArray = pt.cleanArray(tripleArray);
			
	//		pt.setVariables(conditionalList, reasonTerms);
	//		System.out.println("\n" +"Triple Processing for CIE");
	//		resultObject = pt.processTriples(v_message,resultObject,tripleArray,cm,  rvr,  jw,entireParagraph, CurrentSentenceString,PreviousSentenceString, nextSentence, subject, object, verb,v_idea, v_pepNumber, v_date, v_message_ID,author,MessageAuthorsRole, paragraphCounter,sentenceCounter, previousParagraph, nextParagraph, writerForDisco, writerAll,conditionalList,negationTerms, reasonTerms,reasons,  reasonLabels, 2, v_statesToGetRoles, dontCheckLabels);								
			
			//FOR LOOP ALL THE TRIPLES HERE
			Integer y_counter=0;
			//System.out.println("Outside FOR LOOP 1");
			System.out.println("\t\t\t Reason Triples for sentence: " + currentSentence);
			//for all extracted triples extracted from sentence
			for (y_counter=0; y_counter<10;y_counter++)
			{
				String v_extractedSubject  = tripleArray[y_counter][0];
				String v_extractedRelation = tripleArray[y_counter][1];
				String v_extractedObject   = tripleArray[y_counter][2];
				
				//exit loop if nulls - dont process 
				if ((v_extractedRelation=="")  || v_extractedRelation == null){
					break;				
				}
				
				//very important part	- replace brackets from parts of speech		
				//second parameter redundant..needs correction
				v_extractedSubject = cleanAndOutput(v_extractedSubject, "",1);
				v_extractedRelation    = cleanAndOutput(v_extractedRelation, "",2);
				v_extractedObject  = cleanAndOutput(v_extractedObject, "",3);						
				
				//System.out.println("\t\t (sentenceSubject)" + sentenceSubject + " (sentenceVerb) " + sentenceVerb + " (sentenceObject) " + sentenceObject );
				
				String v_reason = "", inputFileReasonSubject = "", inputFileReasonVerb = "", inputFileReasonObject = "";
				
				//check against all reason triples from inputfile
				//for (String reasonTriple : reasonLabels) {
				for (int x=0; x <reasonLabels.size(); x++){									
					v_reason = reasonLabels.get(x).getIdea().toLowerCase();
					inputFileReasonSubject = reasonLabels.get(x).getSubject().toLowerCase();
					inputFileReasonVerb = reasonLabels.get(x).getVerb().toLowerCase();		//no verb egual to pep
					inputFileReasonObject = reasonLabels.get(x).getObject().toLowerCase();				
					
					inputFileReasonSubject = subjectContainsUnderScore(inputFileReasonSubject);
					inputFileReasonVerb = subjectContainsUnderScore(inputFileReasonVerb);
					inputFileReasonObject = subjectContainsUnderScore(inputFileReasonObject);	
					//replce the underscore to seprate the combined terms
					if (inputFileReasonSubject.contains("_"))
						inputFileReasonSubject = inputFileReasonSubject.replace("_", " ");
					if (inputFileReasonVerb.contains("_"))
						inputFileReasonVerb = inputFileReasonVerb.replace("_", " ");
					if (inputFileReasonObject.contains("_"))
						inputFileReasonObject = inputFileReasonObject.replace("_", " ");
					
					//System.out.println("\t\t\tChecking Against Reason Triples: v_reason " + v_reason + " reasonSubject " + reasonSubject + " reasonVerb " + reasonVerb + " reasonObject " + reasonObject);
					
					/* Previous way of doing things
					// if extracted triples contains the triples from input file
											//// little //big
					boolean firstPart = (POScontainsAllTerms(reasonSubject,sentenceSubject) ||   POScontainsAllTerms(reasonObject,sentenceSubject));
					boolean secondPart = (POScontainsAllTerms(reasonVerb,sentenceVerb));
					boolean thirdPart = POScontainsAllTerms(reasonSubject,sentenceObject) ||   POScontainsAllTerms(reasonObject,sentenceObject);
					
					//if double, set third part to true, as they wont need to be matched
					//this may wel;l have to be extended to check when both objects are null, the one frm inputfile and the one extracted
					if (sentenceObject == null || sentenceObject ==""){
						thirdPart = true;
	//					System.out.println("Third part set to matched ");
					}
					*/
					//new way
					//Triple Matching
					boolean firstPart = false, secondPart = false, thirdPart= false;	
					boolean inputFileSubjectMatched=false,inputFileObjectMatched=false;
					if(inputFileReasonObject==null || inputFileReasonObject.isEmpty() || inputFileReasonObject.length()==0 || inputFileReasonObject.equals("")) 
					{
	//					System.out.println("\t\t\tinputFileReasonObject ("+inputFileReasonObject+") == null");
						firstPart = POScontainsAllTerms(inputFileReasonSubject,v_extractedSubject);	//for doubles, we want to check extracted subject with inputfile subject only, checking with object bring many false positives
						secondPart = (POScontainsAllTerms(inputFileReasonVerb,v_extractedRelation));
						thirdPart = true;
					}
					//if triple to match is "the" "is" "consensus"
					//something we dont want to happen is a term, "the, or any other term" to be matched in both extracted subject and extracted object, but it should have been matched in only one of them 
					else {
	//					System.out.println("\t\t\tinputFileReasonObject ("+inputFileReasonSubject+") == not null");
						boolean inputFileSubjectMatchedInExtractedSubject = POScontainsAllTerms(inputFileReasonSubject,v_extractedSubject);
						boolean inputFileObjectMatchedInExtractedSubject  = POScontainsAllTerms(inputFileReasonObject,v_extractedSubject); 
						firstPart = ( inputFileSubjectMatchedInExtractedSubject || inputFileObjectMatchedInExtractedSubject );
						
						secondPart = (POScontainsAllTerms(inputFileReasonVerb,v_extractedRelation));
						
						boolean inputFileSubjectMatchedInExtractedObject  = POScontainsAllTerms(inputFileReasonSubject,v_extractedObject);
						boolean inputFileObjectMatchedInExtractedObject  = 	POScontainsAllTerms(inputFileReasonObject,v_extractedObject);
						thirdPart = ( inputFileSubjectMatchedInExtractedObject ||  inputFileObjectMatchedInExtractedObject);
						
						//both subject and object should have been matched
						inputFileSubjectMatched = (inputFileSubjectMatchedInExtractedSubject || inputFileSubjectMatchedInExtractedObject);
						inputFileObjectMatched	= (inputFileObjectMatchedInExtractedSubject  || inputFileObjectMatchedInExtractedObject);
						
						//quick and dirty way to assign that the inputFileSubjectMatched and  inputFileObjectMatched have been matched
						if(inputFileSubjectMatched && inputFileObjectMatched)
						{}
						else {
	//						System.out.println("\t\t\tinputFileReasonSubjectMatched && inputFileReasonObjectMatched not matched"); 
							thirdPart =false;
						}
					}
					
					//System.out.println("\t\t\tfirstPart " + firstPart +  " secondPart " + secondPart + " thirdPart " + thirdPart);
					if(firstPart && secondPart && thirdPart ){
						System.out.println("\t\t <!\t Reason Triples Matched Found in " +location +"," + inputFileReasonSubject + " , " + inputFileReasonVerb + " , " + inputFileReasonObject + " !>");
						foundReasonOnce = true;
						// reasonMatched = v_subject+" " +v_relation+" "+v_object;
							
						//result.setFinalReason(v_reason);
						result.getReasonObject().SetValues(true, v_reason);
						result.getReasonObject().addToReasonListArray(v_reason);
						result.getReasonObject().setReasonTriplesInNearbySentencesParagraphs(v_reason);
						
						if(location.equals("sameSentence")) {							
								result.getReasonObject().setReasonTriplesInSentence(v_reason);
								String filename= "c:\\scripts\\Reasons\\ReasonTripleInSameSentence.txt";
							    FileWriter fw = new FileWriter(filename,true); //the true will append the new data
							    fw.write("PEP " + result.getPepNumber() + " | " +result.getClausIE() + " | " + v_reason + " | " +inputFileReasonSubject + " | " + inputFileReasonVerb + " | " + inputFileReasonObject +" | " + result.getCurrentSentence() );//appends the string to the file	    
							    fw.write("\r\n");							    fw.close();							
						}
						else if(location.equals("previousSentence")) {							
								result.getReasonObject().setReasonTriplesInPreviousSentence(v_reason);
								String filename= prp.getScriptsHomeFolderLocation() +"Reasons\\ReasonTripleInPreviousSentence.txt";
							    FileWriter fw = new FileWriter(filename,true); //the true will append the new data
							    fw.write("PEP " + result.getPepNumber() + " | " +result.getClausIE() + " | " + v_reason + " | " +inputFileReasonSubject + " | " + inputFileReasonVerb + " | " + inputFileReasonObject +" | " + result.getCurrentSentence() );//appends the string to the file
							    fw.write("\r\n");							    fw.close();							
						}
						else if(location.equals("nextSentence")) {							
								result.getReasonObject().setReasonTriplesInNextSentence(v_reason);
								String filename= prp.getScriptsHomeFolderLocation() +"Reasons\\ReasonTripleInNextSentence.txt";
							    FileWriter fw = new FileWriter(filename,true); //the true will append the new data
							    fw.write("PEP " + result.getPepNumber() + " | " +result.getClausIE() + " | " + v_reason + " | " +inputFileReasonSubject + " | " + inputFileReasonVerb + " | " + inputFileReasonObject +" | " + result.getCurrentSentence() );//appends the string to the file						    
							    fw.write("\r\n");							    fw.close();							
						}
						else if(location.equals("restOfCurrParagraph")) {							
								String filename= prp.getScriptsHomeFolderLocation() +"Reasons\\ReasonTripleInRestOfCurrParagraph.txt";
							    FileWriter fw = new FileWriter(filename,true); //the true will append the new data
							    fw.write("PEP " + result.getPepNumber() + " | " +result.getClausIE() + " | " + v_reason + " | " +inputFileReasonSubject + " | " + inputFileReasonVerb + " | " + inputFileReasonObject +" | " + result.getCurrentSentence() );//appends the string to the file			  
							    fw.write("\r\n");							    fw.close();							
						}
						else if(location.equals("sentenceInPrevParagraph")) {							
								String filename= prp.getScriptsHomeFolderLocation() +"Reasons\\ReasonTripleInSentenceInPrevParagraph.txt";
							    FileWriter fw = new FileWriter(filename,true); //the true will append the new data
							    fw.write("PEP " + result.getPepNumber() + " | " +result.getClausIE() + " | " + v_reason + " | " +inputFileReasonSubject + " | " + inputFileReasonVerb + " | " + inputFileReasonObject +" | " + result.getCurrentSentence() );//appends the string to the file 
							    fw.write("\r\n");							    fw.close();							
						}
						else if(location.equals("sentenceInNextParagraph")) {							
								String filename= prp.getScriptsHomeFolderLocation() + "Reasons\\ReasonTripleInSentenceInNextParagraph.txt";
							    FileWriter fw = new FileWriter(filename,true); //the true will append the new data
							    fw.write("PEP " + result.getPepNumber() + " | " +result.getClausIE() + " | " + v_reason + " | " +inputFileReasonSubject + " | " + inputFileReasonVerb + " | " + inputFileReasonObject +" | " + result.getCurrentSentence() );//appends the string to the file
							    fw.write("\r\n");							    fw.close();	
						}						
					}
					else{
						//System.out.println("<!\t No Reason Triples Matched");
					}
				}
			}
		}
		catch(IOException ioe)	{
		    System.err.println("IOException: " + ioe.getMessage());
		}catch (Exception e){
			System.out.println("Exception 300 (FILE) " + e.toString()); 
			System.out.println(StackTraceToString(e)  );
		}
		//System.out.println("\t\t\t End Reason Triples Checking for sentence:");
		return foundReasonOnce;
	}
	
	public static String StackTraceToString(Exception ex) {
		String result = ex.toString() + "\n";
		StackTraceElement[] trace = ex.getStackTrace();
		for (int i=0;i<trace.length;i++) {
			result += trace[i].toString() + "\n";
		}
		return result;
	}
	
	private static String subjectContainsUnderScore(String subject) {
		if (subject.contains("_")){
			String[] subjectInclude = subject.split("_");								
			String str1 = Arrays.toString(subjectInclude)
					.replace(",", "")  //remove the commas
				    .replace("[", "")  //remove the right bracket
				    .replace("]", "")  //remove the left bracket
				    .trim();           //remove trailing spaces from partially initialized arrays
		    //replace starting "[" and ending "]" and ","
		    //str1 = str1.substring(1, str1.length()-1).replaceAll(",", "");						
			//subjectOptions = subject.split("-");
			subject = str1;
		}
		return subject;
	}
	
	private static boolean checkIfSentenceContainsTripleDoubleTerms(String sentence,ArrayList<LabelTriples>  reasonLabels) {
		String v_reason = "", subject = "", verb = "", object = "";
		//for (String reasonTriple : reasonLabels) {
		for (int x=0; x <reasonLabels.size(); x++){
						
		    v_reason = reasonLabels.get(x).getIdea().toLowerCase();
			subject = reasonLabels.get(x).getSubject().toLowerCase();
			verb = reasonLabels.get(x).getVerb().toLowerCase();		//no verb egual to pep
			object = reasonLabels.get(x).getObject().toLowerCase();
			
			//System.out.println("\tChecking Triples: subject " + subject + " verb " + verb + " object " + object);
			//check for doubles and triples in sentence
			
			//check subject or object
			boolean containsSubjectORObject = sentence.toLowerCase().contains(subject.toLowerCase()) || sentence.toLowerCase().contains(object.toLowerCase());
			//check verb must exist
			boolean containsVerb = sentence.toLowerCase().contains(verb.toLowerCase());
			if (containsSubjectORObject &&  containsVerb)
		    {
				return true;
		    }
		}
		return false;
	}
	
	// make sure all terms are in the pos
		// little //big
	public static Boolean POScontainsAllTerms(String terms, String pos) {

		// System.out.println("terms to check : " + terms);
		// System.out.println("part-of-sentence : " + pos);

		if (terms == "" || terms == null)
			return false;

		// temporray
		if (pos == "" || pos == null)
			return false;

		// if (terms.contains("rough"))
		// System.out.println("Testing terms: " + terms);

		String[] splited = terms.split(" ");
		for (String s : splited) {
			if (!pos.toLowerCase().contains(s)) {
				// System.out.println(" one not matched");
				return false;
			}
		}
		// System.out.println("All terms : " + terms + " exist in POS: " + pos);
		return true;
	}
	
	public static String cleanAndOutput(String v_pos, String pos, Integer posInt)
    {
    	String partOfSpeech = "";
    	
		if (posInt == 1)
			partOfSpeech = "subject";
		else if (posInt == 2)
			partOfSpeech = "verb";
		else if (posInt == 3)
			partOfSpeech = "object";

		if (!(v_pos == "") && v_pos != null) {
			// string cleaning - very important to do this
			v_pos = cleanPOS(v_pos);

			// just output the following message - for debug purposes
			if (v_pos.toLowerCase().contains(pos.toLowerCase())) {
				// System.out.println("\t " + partOfSpeech + " matched");
			} else {
				// System.out.println("\t " + partOfSpeech + " not matched");
			}
		} else {
			// System.out.println("\t " + partOfSpeech + " empty or null");
		}
    	
    	/*
    	 if (!(v_relation=="")  && v_relation != null)
			{
				//string cleaning - very important to do this	
				v_relation = cleanPOS(v_relation);
				
				//just output the following message - for debug purposes
				if (!v_relation.isEmpty() &&  v_relation.toLowerCase().contains(verb.toLowerCase())) {
//					System.out.println("\t verb matched");
				}
				else {
//					System.out.println("\t verb not matched");
				}
			}
			else{
//				System.out.println("\t relation empty or null");
			}
			
			if (!(v_object=="")  && v_object != null)
			{
				//string cleaning - very important to do this				
				v_object =cleanPOS(v_object);		
				
				//just output the following message - for debug purposes
				if (!v_object.isEmpty() &&   v_object.toLowerCase().contains(object.toLowerCase())) {
//					System.out.println("\t object matched");
				}
				else {
//					System.out.println("\t object not matched");
				}				
			}
			else{
//				System.out.println("\t object empty or null");
			}
    	  
    	 */
    	
    	return v_pos;
    	
    }
	
	public static String cleanPOS(String v_pos){
		if(v_pos!=null && v_pos!="" ){
			v_pos = v_pos.toLowerCase();		
			if(v_pos.contains("\"")){
				v_pos.replace("\"","");
		    }
			if(v_pos.contains(")")){
				v_pos.replace(")","");
		    }
		}
		return v_pos;
	}
	
	//check reasons for doubles captured
	public static boolean checkAndGetReasonsForDoubles(TripleProcessingResult result, String entireParagraph, String currentSentence,  String prevSentence, String nextSentence, 
			String previousParagraph, String nextParagraph, String [] v_actualReasons, ClausIEMain cie, ArrayList<LabelTriples> reasonLabels, ProcessingRequiredParameters prp) throws IOException
	{	
		System.out.println("<!\tInside checking Reason for doubles !>");
	
		boolean foundReasonOnce = false;
		//Reasons reason = new Reasons();
		//reason.SetValues(false, "");		// set default value
		nextSentence = result.getNextSentence();
		
		//NOT REQUIRED FOR DOUBLES
		// 1. check ExtractedTriple For ReasonTerms
		// Example "I 've updated PEP 345 with this feedback ."
		//System.out.println("<! \tChecking ReasonTerms in ExtractedTriples for Reasons !>");
		//foundReasonOnce = checkExtractedTripleForReasonTerms(v_reasonTerms, v_reasons, foundReasonOnce, reason);
			
		// 2. Check for single terms in nearby sentences and paragraphs - Look for reasons, go over all reasons and see if they
		// Example "I 've updated PEP 428 following the previous discussion . Following our discussion of last week , here is a first draft of the PEP ."
		System.out.println("<! \tChecking ReasonTerms in Nearby Sentences and Paragraphs for Reasons !>");
			foundReasonOnce = checkNearbySentencesAndParagraphsForReasonTerms(entireParagraph, currentSentence,	prevSentence, nextSentence, previousParagraph, nextParagraph, v_actualReasons, foundReasonOnce, result,prp);

		//3. CHECK REASON TRIPLES IN CURR AND NEARBY PARAGRAPHS
		//First check triples from reason input file in: same sentence the rest of the triples
		//Then extract triples from these and check curr, next and previous sentence and also in curr, previous and next paragraph
		System.out.println("<! \tComputing ClausIE For Reason Triples in nearby Sentences and Paragraphs for Reasons !>");				
		
		//extract and return triples from paragraphs, previous, current and next paragraph			//maybe pass conditionalStrings to check if triple is condtional??
		foundReasonOnce = checkNearbySentencesParagraphsForReasonTriples(	prevSentence, nextSentence,entireParagraph, previousParagraph, nextParagraph, result, cie,  reasonLabels,prp);

		//NOT NEEDED
//		foundReasonOnce = checkSentenceForReasonTriples(v_tripleArray, cm, currentSentence, v_idea, v_pepNumber, v_date,v_message_ID, author, sentenceCounter, writerForDisco, writerAll, conditionalStrings, v_reasonTerms,
		
		//???? should add or not
		if (foundReasonOnce = false) {
			// if bdfl pronouncement
			// check next paragraph
		}
		return foundReasonOnce;
	}
		
	// make sure all terms are in the pos
		// little //big
	public static Boolean checkTerms(String term, String pos) {
		if (term == "" || term == null)
			return false;
		// temporray
		if (pos == "" || pos == null)
			return false;
		if (pos.toLowerCase().contains(term)) {
			// System.out.println(" one not matched");
			return true;
		}
		// System.out.println("All terms : " + terms + " exist in POS: " + pos);
		return false;
	}
	
	@Override
    protected void finalize() throws Throwable
    {
       // System.out.println("From Finalize Method, i = "+i);
    }
	
	/*
	 * 
	 * if (prevSentence != null) {
				if (prevSentence.contains(r)) {
					foundReasonOnce = true;
					System.out.println("\t\tThe reason found in Previous sentence = " + r);
					// set reaon
					reason.SetValues(true, r);
					reason.addToReasonListArray(r);
				}
			}
			// FIRST way check for reason terms in sentence, just simple
			// string matching
			// if (prevSentence !=null){
			if (currentSentence.contains(r) )   //prevent appending of reasons  && foundReasonOnce == false
			{
				foundReasonOnce = true;
				System.out.println("\t\tThe reason found in Current Sentence = " + r);
				// set reaon
				reason.SetValues(true, r);
				reason.addToReasonListArray(r);
			}
			// }
			// THIRD way check for reason terms in next sentence, just
			// simple string matching
			if (nextSentence != null) {
				// System.out.println("xxxxxxxxxxInside checkAndGetReasons,
				// Next sentence = " + nextSentence);
				if (nextSentence.contains(r) ) {  //&& foundReasonOnce == false
					foundReasonOnce = true;
					System.out.println("\t\tThe reason found in Next sentence = " + r);
					// set reaon
					reason.SetValues(true, r);
					reason.addToReasonListArray(r);
				}
			}
			// Paragraph checking
			// temporary quick and dirty way to check for reasons in
			// previous and next paragraphs
			if (previousParagraph != null) {
				// System.out.println("xxxxxxxxxxInside checkAndGetReasons,
				// Next sentence = " + nextSentence);
				if (previousParagraph.contains(r) ) {   // && foundReasonOnce == false
					foundReasonOnce = true;
					System.out.println("\t\tThe reason found in Previous Paragraph = " + r);
					// set reaon
					reason.SetValues(true, r);
					reason.addToReasonListArray(r);
				}
			}
			if (entireParagraph != null) {
				// System.out.println("xxxxxxxxxxInside checkAndGetReasons,
				// Next sentence = " + nextSentence);
				if (entireParagraph.contains(r)  && foundReasonOnce == false) {
					foundReasonOnce = true;
					System.out.println("\t\tThe reason found in Current paragraph = " + r);
					// set reaon
					reason.SetValues(true, r);
					reason.addToReasonListArray(r);
				}
			}
			if (nextParagraph != null) {
				// System.out.println("xxxxxxxxxxInside checkAndGetReasons,
				// Next sentence = " + nextSentence);
				if (nextParagraph.contains(r) ) {    // && foundReasonOnce == false
					foundReasonOnce = true;
					System.out.println("\t\tThe reason found in Next paragraph = " + r);
					// set reaon
					reason.SetValues(true, r);
					reason.addToReasonListArray(r);
				}
			} else {
				// System.out.println("Next sentence null");
			}
	 */
}
