package miner.process;

import java.io.FileWriter;
import java.io.IOException;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

//import wekaExamples.MyFilteredClassifier;
import callIELibraries.JavaOllieWrapperGUIInDev;
import readRepository.readMetadataFromWeb.GetProposalDetailsWebPage;
import callIELibraries.ClausIECaller;
import callIELibraries.ReVerbFindRelations;
import callIELibraries.UseDISCOSentenceSim;
import connections.MysqlConnect;
//import postProcess.OptionsToEliminateRepeatedResults;
import de.mpii.clause.driver.ClausIEMain;
import miner.processLabels.ComputePrevNextSentenceAndParagraph;
import miner.processLabels.ProcessLabels;
import miner.processLabels.TripleProcessingResult;
import utilities.NattyReturnDateFromText;

public class CheckSentenceHasTripleTerms_SendToIELibraries_InsertIntoDB {

	static boolean showCIEDebugOutput = false;

	//remove 'there' from 'them there' as clausie doesnt catch that
	//If you don't mind, I'd like to apply for one of them there PEP numbers.  
	public static String shortenSentence(String sentence){
		String s = "them there";
		if(sentence.contains(s)){
			sentence = sentence.replace("there", "");
		}
		return sentence;
	}

	public static String extendSentence(String sentence){	
		String s="";
		if(sentence.contains(s)){
			sentence = sentence.replaceAll(",", "");
		}
		//if sentence starts with lowercase..meaning not full sentence, we add "I will"
		return sentence;
	}

	public static Boolean checkEachSentenceForLabelTerms_ThenMatchAgaistDifferentLibrariesForTriples(ProcessingRequiredParameters prp, Message m, SentenceAndNearby s)
	{   
		String v_idea = "", subject = "", verb = "", object = "", second_verb = "", second_subject = "", second_object = "", space = " ";
		Integer lineNumber;
		boolean triple = false, doubleTriple = false, stateDouble = false, tripleMatched = false, checkRepeatedLabels = false, checkEmptyRows = false, matchFound = false;
		List<String> matchedLabelList = new ArrayList<String>();  	//store the matched label so we dont check it again - too many records in results

		//extract all parameters here
		//ProcessingRequiredParameters
		ArrayList<LabelTriples> labels = prp.getLabels();
		Integer v_pepNumber = prp.getPEPNumber();
		String[] questionPhrases = prp.getQuestionPhrases(),allowLabelsAsQuestion = prp.getAllowLabelsAsQuestion(),unwantedTerms = prp.getUnwantedTerms();
		String messageSubject = m.getSubject();
		boolean readDummyFile = prp.isReadDummyFile(),reverbTrue = prp.isCheckReverbTrue(),clauseIETrue = prp.isCheckClauseIETrue(),dependencyTrue = prp.isCheckReverbTrue();
		boolean checkClauseIETrue = prp.isCheckClauseIETrue(),checkOllieTrue = prp.isCheckOllieTrue(),replaceCommas = prp.isReplaceCommas();
		ReVerbFindRelations rvr =prp.getRvr();
		JavaOllieWrapperGUIInDev jw = prp.getJw();
		ClausIEMain cm = prp.getCm();
		FileWriter writerAll = prp.getWriterAll(), writerForDisco = prp.getWriterForDisco();
		String[] conditionalList = prp.getConditionalList(),negationTerms = prp.getNegationTerms(), reasonTerms = prp.getReasonTerms(), reasons = prp.getActualReasons(); 
		ArrayList<LabelTriples> reasonLabels = prp.getReasonLabels(); 
		String[] statesToGetRoles  =prp.getStatesToGetRolesAndReasons(),dontCheckLabels = prp.getDontCheckLabels();
		 
//		ArrayList<TripleProcessingResult> tripleProcessingResult = prp.getTripleProcessingResult();	//create a list of results
		TripleProcessingResult resultObject =  new TripleProcessingResult();	//create an object
		//v_date, 
		ArrayList<SinglesDoublesTriples> singlesAnddoubles = prp.getSinglesAndDoubles();
		String prevSentence = "";

		//Message Related
		Integer v_message_ID = m.getMessage_ID(); 
		//v_idea,
		Date v_date = m.getM_date();
		Timestamp v_dateTimeStamp = m.getDateTimeStamp();
		String author = m.getAuthor(), authorsRole = m.getAuthorsRole() , folder = m.getFolder(),v_message = m.getMessage(), msgSubject = m.getSubject(),
				CurrentSentenceString = s.getCurrentSentenceString(), PreviousSentenceString = s.getPreviousSentenceString();		//sentenceAndNearby related
		//replaceCommas
		//System.out.println("-----here 11 sentence: "+CurrentSentenceString);
	
		//gives duplicate
		//String prevSentence = s.getPrevSentence(); 		//String nextSentence = s.getNextSentence();
		Integer sentenceCounter = s.getSentenceCounter(),paragraphCounter= s.getParagraphCounter(), sentenceCounterInPara = s.getSentenceCounterinParagraph();
		String entireParagraph = s.getEntireParagraph(),previousParagraph = s.getPreviousParagraph(),nextParagraph = s.getNextParagraph();
		boolean isFirstParagraph = s.isFirstParagraph(), isLastParagraph  = s.isLastParagraph();
		//System.out.println("isFirstParagraph: "+ isFirstParagraph+ " isLastParagraph "+isLastParagraph);
		//some sentences contain double sets of triples which should be captured
		// for example Sentence:  "Does anybody have any > preferences as to the voting method we should use?"
		// has two sets of triples in clauseIE 
		//		Propositions     :  ("anybody", "have", "any preferences as to the voting method")
		//  						("we", "should use", "the voting method")

		//			System.out.println("Inside checkEachSentenceAgaistDifferentLibrariesForLabels(): " + CurrentSentenceString);			

		//			OptionsToEliminateRepeatedResults oerr = new OptionsToEliminateRepeatedResults();			
//		System.out.println("checkEachSentenceAgaistDifferentLibrariesForLabels: "+ CurrentSentenceString);


		if(labels.isEmpty())		{	
			System.err.println("Labels list Empty"); 
		}
		
		for (int x=0; x <labels.size(); x++) //for all labels for checking
		{ 
			if(labels.get(x).getIdea() != null)
			{ 
				//				System.out.println("inside checking labels");
				stateDouble = false;				triple = false;				doubleTriple = false;
				//this used to checks previous sentyences for part of the triple 
				//the input file has a previous sentence for checking but now will code it differenntly.when?					

				lineNumber = labels.get(x).getLineNumber();
				v_idea = labels.get(x).getIdea(); 				subject = labels.get(x).getSubject();	verb = labels.get(x).getVerb();		object = labels.get(x).getObject();  //no verb egual to pep
				if(object==null || object.isEmpty() || object.equals("")){
					stateDouble=true;		object = ""; //System.out.println("double r"); //important - set the object to null
				}
				else
					triple =true; 	//System.out.println("triple r");
//				System.out.println("v_idea: " + v_idea + " subject: "+ subject + " verb "+ verb + " object "+ object); //important - set the object to null

				//expand terms....e.g. where S,V or O has two terms, e.g. was_accepted  = was accepted
				subject = subjectContainsUnderScore(subject);
				verb = subjectContainsUnderScore(verb);
				if(triple)	object = subjectContainsUnderScore(object);				

				//and then in clauseIRE split again and check again

				//if sentence SVO has a doble term or triple matching in SVO once
				//doubleTerms ||

				//replce the underscore to seprate the combined terms
				if (subject.contains("_"))		subject = subject.replace("_", " ");
				if (verb.contains("_"))			verb = verb.replace("_", " ");
				if (object.contains("_"))		object = object.replace("_", " ");
				//commented because of lot of false positives
				//MATCH ANY SUBJECT
				//NER implemented here...as the * should be true only when we have some PERSON found through NER in the POS
				boolean subjectContainsAsterisk=false,objectContainsAsterisk=false;
				if (subject.equals("*"))	{
					//check to see if person is returned
					subject = "";					subjectContainsAsterisk=true;
					//System.out.println("\t\tsubject= * " + subject + " " + verb + " " + object);
				}
				if (object.equals("*")){
					object = "";						objectContainsAsterisk=true;
					System.out.println("Object= *:" );	
				}
				
				//these are all the triples from the input file,check if sentence has all terms in S,V,O
				//combine terms
				String svoText ="";
				if(triple) {// System.out.println("triple 234");
					svoText = subject + " " + verb + " " + object;	}
				else {		//System.out.println("else 234");
					svoText = subject + " " + verb;	}

				svoText= svoText.trim();
//				System.out.println("svoText:" + svoText+ " | " + CurrentSentenceString);
				//System.out.println("COMPARE:" + compare);

				//
				//					System.out.println("Going to check Candidate Label : " + v_idea + ", " + CurrentSentenceString);
				//					System.out.println("Going to check Candidate Label: (" + v_idea + ") subject=(" + subject + ") verb=(" + verb + ") object=(" + object + ")");

				//new added 14-feb-2017
				//if a label has been found in sentence, don't look for it again as a label can be found several times in a sentence with several triple arrangements in inputfile
				//make sure the label has not been captured before
				//updated 30nov2017- this does not work in instances where the first labeltriple  is found but not matched, as the second set of triples may get matched, but this code prevents that from happenning
				//this has to be moved to post processing section
				boolean labelMatchedBefore = false;
				for (String newLabel : matchedLabelList) {  
					if(v_idea.equals(newLabel))
						labelMatchedBefore = true;
						//  System.out.print("label Matched Before in same sentence " + v_idea + " ");
				}		
				/*					//Code not needed as implemented in GetallSentencesinMessages.java					
					//Coreference checking					
					Boolean allTermsExist = false;					
					//check if all terms exist in original sentence					
					if (prp.getPms().seeIfAllTermsinSentence(svoText, CurrentSentenceString))	{ 
						allTermsExist = true; 
					}
					//if not then check if all terms exist in coreferenced sentence
					//if so, check the coreferenced sentence for triples by replacing the variable
					else {
						if (prp.getPms().seeIfAllTermsinSentence(svoText, coReferencedSentence)) {					
							allTermsExist = true;
							CurrentSentenceString = coReferencedSentence;
						}
					}
				 */					
				//with coreference use this code below
				//if (allTermsExist && !labelMatchedBefore)
				//without corefere- use this lien below
				String permanentNextSentence="";
				
				//Maybe we need to check this for pep 391 ..Mark pep 391 as accepted. 
				//CurrentSentenceString = CurrentSentenceString.replaceAll("[^a-zA-Z ]", "").toLowerCase();	//remove all punctuation
				//CurrentSentenceString = CurrentSentenceString.replaceAll("\\s+", " ").trim(); //remove double spaces and trim
				//FOR EACH SENTENCE WE FIRST CHECK FOR STATES
				//THEN LATER WE CHECK FOR REASONS WEIGHTAGE AND ML
				if(prp.getPms().seeIfAllTermsinSentence(svoText, CurrentSentenceString) )	//&& !labelMatchedBefore
				{	
					System.out.println("\n--------------------------------------------------------- ");
					System.out.println("Candidate Label Found: " + v_idea + " (LabelLine# "+lineNumber+") Subject: (" + subject + ") Verb: (" + verb + ") Object: (" + object + ") MID " + v_message_ID +  ", Proposal: "+v_pepNumber);
					prevSentence = PreviousSentenceString;
					matchFound = true;	
					// System.out.println("here 0");
					boolean sentenceIsQuestion = false, sentenceEndsWithQuestionMark = false, sentenceHasUnwantedTerms = false, sentenceIsComplex = false, sentenceConditional = false;		

					//sometimes there is a negation in front 3 terms in sentence, e.g. "Alternatives The PEP 433 is a previous attempt proposing various other alternatives , but no consensus could be reached ."
					//so we check for negation in front of the three terms
					// check if sentence contaisn negation term and if its directly in front of the 3 terms
					boolean negationTermExistsAndPrecedingSVOTerms=false;
					
					//now thsi is changed to checking negation in front of clauses if all 3 terms in a clause
					//negationTermExistsAndPrecedingSVOTerms = negationJustBeforeSVO(negationTerms, CurrentSentenceString, subject, verb, object);
					
					//dec 2018..if msgSubject contains the label					
				    //have checked all label triple labels, and it shoudl be okay if any term in label is matched
					boolean msgSubjectContainsLabel= false;					
					String idea = v_idea.replaceAll("_", " ").toLowerCase();
					if(idea.contains(" ")) {
						for (String r: idea.split(" ")) {
							if(msgSubject==null || msgSubject.isEmpty()) {}
							else {
								if (msgSubject.toLowerCase().contains(r))
									msgSubjectContainsLabel=true;
							}
						}
					}else {
						if(msgSubject==null || msgSubject.isEmpty()) {}
						else {
							if (msgSubject.toLowerCase().contains(idea))
								msgSubjectContainsLabel=true;
						}
					}					
					//if(prp.getPms().seeIfAllTermsinSentence(v_idea, msgSubject)) {	//old way just check entire label with underscore which will not work always
					//	msgSubjectContainsLabel=true;
					//}
					
					//see if sentence is about other peps		-- dont include if its only abt other peps	
					int option = 0; 
					boolean allowZero = true, checkIfShouldProcess_IfTextContainsOnlyOtherPepNumbers = false;
					
					//jan 2019, we dont check below for some labels which we expect to only contain other peps
					//this pep is made obsolete by the acceptance <html links> of pep 461 , which introduces a more extended formatting language for bytes objects in conjunction with the modulo operator.
					if(v_idea.equals("alternative_pep")) {}
					else
						checkIfShouldProcess_IfTextContainsOnlyOtherPepNumbers = prp.getPms().checkTextContainsProposalNumber_WithOptions(CurrentSentenceString.toLowerCase(), v_pepNumber,option,allowZero);
										
					//						System.out.println("ToProcessCheckPepNumbersFromSentence shud be true --is: " +ToProcessCheckPepNumbersFromSentence);					
					if (checkIfShouldProcess_IfTextContainsOnlyOtherPepNumbers){
						System.out.println("sentenceContainsOnlyOtherPepNumbers: "+CurrentSentenceString.toLowerCase()); 
						try {
							String filename= prp.getScriptsHomeFolderLocation() +  "outputFiles\\processingData\\otherPeps\\OtherPEPs.txt"; //C:\Users\psharma\Google Drive\PhDOtago\Code\outputFiles\processingData\otherPEPs
						    FileWriter fw = new FileWriter(filename,true); //the true will append the new data
						    fw.write("PEP " + v_pepNumber + " | "+ CurrentSentenceString.toLowerCase());//appends the string to the file
						    fw.write("\r\n");
						    fw.close();
						}
						catch(IOException ioe){
						    System.err.println("IOException: " + ioe.getMessage());
						}
					}
//					System.out.println("here b");

					ProcessLabels pt = new ProcessLabels();		//create new triple processing reult object	

					// QUESTION SENTENCE, for each sentence check if it does not start with question terms	, returns true if question			
					sentenceIsQuestion = prp.getPms().checkSentenceIsQuestion(CurrentSentenceString, questionPhrases);	
					if (sentenceIsQuestion)
						System.out.println("Sentence starts with question terms");
					boolean allowableQuestionLabelFound = false;
					for (String questionLabel: allowLabelsAsQuestion){
						if (v_idea.equals(questionLabel)) {
							allowableQuestionLabelFound = true;							System.out.println("allowableQuestionLabelFound"); 
						}
					} 	

					if (!allowableQuestionLabelFound)
						sentenceEndsWithQuestionMark = prp.getPms().checkSentenceEndsWithQuestionMark(CurrentSentenceString);

					if (sentenceEndsWithQuestionMark)
						System.out.println("sentenceEndsWithQuestionMark"); 

					sentenceHasUnwantedTerms = prp.getPms().checkSentenceHasUnwantedTerms(CurrentSentenceString,unwantedTerms);
					if (sentenceHasUnwantedTerms)
						System.out.println("sentenceHasUnwantedTerms"); 

					//COMPLEX SENTENCES, sentencves creating error	, , returns true if complex					
					sentenceIsComplex = prp.getPms().checkSentenceIsComplex(CurrentSentenceString);	

					boolean reverbFound = false, clasueIETrueFound = false, OllieTrueFound = false;
					//Only for debugging, comment when run 		
					
					if(prp.getOutputfordebug())
					System.out.println("checkIfShouldProcess_IfTextContainsOnlyOtherPepNumbers: "+checkIfShouldProcess_IfTextContainsOnlyOtherPepNumbers + " sentenceHasUnwantedTerms " + sentenceHasUnwantedTerms
							+ " sentenceIsComplex: " +sentenceIsComplex + " sentenceIsQuestion: "+ sentenceIsQuestion + " sentenceEndsWithQuestionMark:" +sentenceEndsWithQuestionMark );

					if(!checkIfShouldProcess_IfTextContainsOnlyOtherPepNumbers  
							//&& negationTermExistsAndPrecedingSVOTerms == false
							&& sentenceHasUnwantedTerms ==false
							&& sentenceIsComplex ==false
							&& sentenceIsQuestion==false
							&& sentenceEndsWithQuestionMark ==false) 
					{							
						//	sentencesRemoveDuplicates.add(CurrentSentenceString.toLowerCase());
						//String MessageAuthorsRole = "";
						//temp disable
						if (!readDummyFile){
							//TEMPORARILY SUSPENDED AS CONSUMING TOO MUCH CPU
							//dec 2018..below commented as we compute this and store in table column
							//MessageAuthorsRole = ""; //pms.ExtractMessageAuthorRole(pd, v_pepNumber, author);
						}						

						//add to matched label list
						matchedLabelList.add(v_idea);

						System.out.println("MessageAuthorsRole: " + authorsRole + ", Sentence: " + "(PEP: "+v_pepNumber+") " + CurrentSentenceString );
						//System.out.println();
						//writer.write ("\n"   +  v_pepNumber + " | " + v_date+ " | " +  v_message_ID + " | " + author   + " | " + v_idea + " | " + CurrentSentenceString);

						//Dont output until certain sentence or label not dublicate
						//writerAll.write("\n\n" +  v_pepNumber + " | " + v_date+ " | " +  v_message_ID + " | " + author   + " | " + v_idea + " | " + CurrentSentenceString);
						//writerForDisco.write("\n"   +  v_pepNumber + " | " + v_date+ " | " +  v_message_ID + " | " + author   + " | " + v_idea + " | " + CurrentSentenceString);


						//create an object to store the result
						//ArrayList<TripleProcessingResult> tripleProcessingResult = prp.getTripleProcessingResult();
//					    TripleProcessingResult resultObject =  new TripleProcessingResult();
//						tripleProcessingResult.add(resultObject);	  ////ADD THE CREATED OBJECT TO ArrayList  							
						//assign it to the object instance passed in the function as it already has class attributes polulated by other libraries when this function was claaed for another library
						// this function will just add to the fields specific to the library this function is checking now	

						resultObject.setMetadata(v_pepNumber, v_date,v_dateTimeStamp, v_message_ID, v_idea, author,authorsRole,messageSubject,folder, CurrentSentenceString,lineNumber,msgSubjectContainsLabel);
						//dec 2018
						resultObject.setParagraphCounter(paragraphCounter);  resultObject.setSentenceCounter(sentenceCounter); resultObject.setSentenceCounterInParagraph(sentenceCounterInPara);
						resultObject.setFirstParagraph(isFirstParagraph);    resultObject.setLastParagraph(isLastParagraph);
						//remove all commas //i dont think this will ever be used
						//							if(replaceCommas){
						//								CurrentSentenceString = CurrentSentenceString.replaceAll(",", "");
						//								CurrentSentenceString = CurrentSentenceString.replaceAll(" ", "");
						//							}

						CurrentSentenceString = shortenSentence(CurrentSentenceString);

						//if match was found, classify and show result and also take base idea, add statistical disco approach and semantic also
						// but ideally this should be done on all sentences,not only whch ones the string matching has found

						//Clasify the sentence
						//System.out.println("Classify sentence using model : " + CurrentSentenceString);
						//**						SentenceClassification = fc.classifySentence (CurrentSentenceString);
						//**						System.out.println("SentenceClassification: " +SentenceClassification);

						//take base idea also 
						//use DISCO statistical approach
						//**						DISCOSimilarity = disSim.getDISCOSimilarity(v_idea, CurrentSentenceString, ds, sentencesToMatch);
						//**						System.out.println("DISCOSimilarity: " +DISCOSimilarity);

						//REVERB ONLY DOES TRIPLES , SO STATES/DOUBLES SHOULD NOT BE PASSED HERE
						//PASS IT TO CLAUSIE AND TRY OLLIE
						//REVERB		&& stateDouble == false

						try 
						{
							String nextSentence = "", ns = "";
							//								String nextParagraph = null;
							//								String prevParagraph = null;
							//								System.out.println("\t\t---going to ComputePrevNextSentenceAndParagraph()-----------");
							/* jan 2019, we comment this for now as we dont need at the moment
							ComputePrevNextSentenceAndParagraph csp = new ComputePrevNextSentenceAndParagraph();
							//i asssume the only reason we doing this is to find next sentence
							resultObject = csp.computeSentencesAndParagraph(resultObject, v_message, prevSentence, CurrentSentenceString, nextSentence, sentenceCounter, 
									entireParagraph, previousParagraph, nextParagraph, paragraphCounter, ns); //entireMessage									
							//								System.out.println("\t### nextSentence asb " + result.getNextSentence());
							//set prev and Next Paragraph
							//String previousPara graph, String nextParagraph,
							resultObject.setCurrParagraph(entireParagraph);				resultObject.setPrevParagraph(previousParagraph);
							resultObject.setNextParagraph(nextParagraph);				resultObject.setPrevSentence(prevSentence);
							*/
							permanentNextSentence=""; //first empty it
							permanentNextSentence= nextSentence;
							
							if (reverbTrue){ /*
								System.out.println("\n" +"Reverb Output");
								String tripleArray[][] = rvr.findRelations(CurrentSentenceString, subject, object, verb,v_idea, v_pepNumber, v_date, v_message_ID,author,  sentenceCounter, 
										writerForDisco, writerAll, conditionalList, reasonTerms, reasonLabels, 2);
								tripleArray = pt.cleanArray(tripleArray);
								resultObject = pt.processTriples(v_message,resultObject, tripleArray,cm, rvr, jw, entireParagraph, CurrentSentenceString,PreviousSentenceString, nextSentence, subject, 
										object, verb,v_idea, v_pepNumber, v_date, v_message_ID,author,authorsRole,  paragraphCounter, sentenceCounter, previousParagraph, nextParagraph, 
										writerForDisco,writerAll, conditionalList, negationTerms, reasonTerms, reasons, reasonLabels, 1,  statesToGetRoles, dontCheckLabels,
										subjectContainsAsterisk,objectContainsAsterisk,prp,isFirstParagraph,isLastParagraph); //, snlp);	
								*/
							}
							if (dependencyTrue){   //get dependency	
								//String dependency = snlptdg.processText(CurrentSentenceString, false, bw, v_idea);
							}
							if (checkClauseIETrue){
								try {
								//System.out.println("\n" +"ClausIE Output w");
								String tripleArray[][] = cm.computeClausIE(CurrentSentenceString, showCIEDebugOutput);
								tripleArray = pt.cleanArray(tripleArray);
								pt.setVariables(conditionalList, reasonTerms);	
//								System.out.println("MessageAuthorsRole 20: " + authorsRole + " resultObject: "+resultObject.getAuthorsRole());
								//										System.out.println("\n" +"Triple Processing for CIE");
								if(prp.getOutputfordebug())
								System.out.println(" line numnber " + resultObject.getLineNumber() + " object "+ object);
								resultObject = pt.processTriples(v_message,resultObject,tripleArray,cm,  rvr,  jw,entireParagraph, CurrentSentenceString,PreviousSentenceString, nextSentence, subject,
										object, verb,v_idea, v_pepNumber, v_date, v_message_ID,author,authorsRole, paragraphCounter,sentenceCounter, previousParagraph, nextParagraph, 
										writerForDisco, writerAll,conditionalList,negationTerms, reasonTerms,reasons,  reasonLabels, 2, statesToGetRoles, dontCheckLabels,
										subjectContainsAsterisk,objectContainsAsterisk,prp, isFirstParagraph, isLastParagraph); //, snlp);			
//								System.out.println("MessageAuthorsRole 21: " + authorsRole + " resultObject: "+resultObject.getAuthorsRole());
								//sometimes comma and spaces mess up the result, so hear we remove them and process again
								/*
										if (resultObject.getClausIE()==null){ 
											//remove command and spaces and try again
											System.out.println("\n" +"ClausIE Output - Removing comma and spaces and trying again");
											CurrentSentenceString = CurrentSentenceString.replaceAll(",", "");
											CurrentSentenceString = CurrentSentenceString.replaceAll(" ", "");	
											resultObject = pt.processTriples(v_message,resultObject,tripleArray,cm,  rvr,  jw,entireParagraph, CurrentSentenceString,PreviousSentenceString, nextSentence, subject, object, verb,v_idea, v_pepNumber, v_date, v_message_ID,author,MessageAuthorsRole, paragraphCounter,sentenceCounter, previousParagraph, nextParagraph, writerForDisco, writerAll,conditionalList,negationTerms, reasonTerms,reasons,  reasonLabels, 2, v_statesToGetRoles, dontCheckLabels);								
										}
								 */

								//Just trying a different issue
								//splitting sentence by comma and trying again eliminates lots of false positives
								/*
										if (CurrentSentenceString.contains(",")){ 
											String split[] = CurrentSentenceString.split(",");
											for (String sentenceFragment : split){
												String tripleArray[][] = cm.computeClausIE(CurrentSentenceString, showCIEDebugOutput);
												tripleArray = pt.cleanArray(tripleArray);
												pt.setVariables(conditionalList, reasonTerms);
//												System.out.println("\n" +"Triple Processing for CIE");
												resultObject = pt.processTriples(v_message,resultObject,tripleArray,cm,  rvr,  jw,entireParagraph, CurrentSentenceString,PreviousSentenceString, nextSentence, subject,
														object, verb,v_idea, v_pepNumber, v_date, v_message_ID,author,MessageAuthorsRole, paragraphCounter,sentenceCounter, previousParagraph, nextParagraph, 
														writerForDisco, writerAll,conditionalList,negationTerms, reasonTerms,reasons,  reasonLabels, 2, statesToGetRoles, dontCheckLabels); //, snlp);	
											}
										}
								 */
								//System.out.println("\n" +"ClausIE Output- processed later");
								//System.out.println("\n" +"resultObject CIE After returned: " + resultObject.getLabels(2));	
								}catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

							}
							if (checkOllieTrue){
								System.out.println("\n" +"Ollie Output");
								String tripleArray[][] = jw.computOllie(CurrentSentenceString, subject, object, verb,v_idea, v_pepNumber, v_date, v_message_ID,author,  sentenceCounter, writerAll, 
										writerForDisco, conditionalList, reasonTerms, reasonLabels, 2);
								tripleArray = pt.cleanArray(tripleArray);											
								//System.out.println("\n" +"Now Processing Triples for Ollie");
								resultObject = pt.processTriples(v_message,resultObject,tripleArray,cm, rvr, jw, entireParagraph, CurrentSentenceString,PreviousSentenceString, nextSentence, subject, 
										object, verb,v_idea, v_pepNumber, v_date, v_message_ID,author,authorsRole,  paragraphCounter,sentenceCounter, previousParagraph, nextParagraph, 
										writerForDisco,writerAll, conditionalList,negationTerms, reasonTerms,reasons,  reasonLabels, 3,statesToGetRoles, dontCheckLabels,subjectContainsAsterisk,
										objectContainsAsterisk,prp, isFirstParagraph, isLastParagraph); //, snlp);																			
							}
						} 
						catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						//							Instant clausIEStart = Instant.now();							
						//							Instant ClausIEEnd = Instant.now();
						//							Duration timeElapsed = Duration.between(clausIEStart, ClausIEEnd);
						//							System.out.println("clausIE Time taken: "+ timeElapsed.toMillis() +" milliseconds");							
						
						//INSERT RESULT INTO DATABASE, even if extracted triples dont match, but they exist in sentence
						//we check the date of the captured label and if the year is beofre the date of the message, we insert with the new date returned by natty 
						try { 
							NattyReturnDateFromText ndf = new NattyReturnDateFromText();//now set the date according to natty	
							ndf.returnEventDateInText(CurrentSentenceString);
							//INSERT RESULT INTO DATABASE				
							outputTripleExtractionMatchingResultsToFileAndDatabase(resultObject,CurrentSentenceString,v_idea,
									v_pepNumber,reverbTrue,checkClauseIETrue,checkOllieTrue,dependencyTrue,writerAll,writerForDisco,prp);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}//end if not matched - if( dontProcessSentence == false && ToProcessCheckPepNumbersFromSentence ==true

					else{
						//							System.out.println("Else part");
					}

					//cheack for reasons						
					//MEANING THAT SOMETHING HAS BEEN MATCHED
					if(reverbFound==true ||clasueIETrueFound == true ||OllieTrueFound ==true)
					{
						//we have matched some triple in teh sentence 
						tripleMatched = true;	
					} //end if any library triple matched and found 
				} //end if S,V, O found in sentence
				
				//else if only 2 terms found, we write to db for later analysis for patterns of subject
				else if (prp.getPms().seeIfAnyTwoTermsinSentence(svoText, CurrentSentenceString))
				{
					Connection conn = prp.getConn();
					//jan 2019, maybe we can sabve some time writing, and use this for debug later
/*					insertNearbySentencesInDB(v_pepNumber, v_message_ID, v_message, v_idea,  subject,  verb,  object,
							CurrentSentenceString, //need current sentence: current sentence in earlier round
							PreviousSentenceString, 		  //need previous sentence: next sentence in earlier round but current sentence
							permanentNextSentence, 		  //need next sentence: currentSentence in this round  
							author, messageSubject,conn);	*/		
					
				}
				
				//DEC 2018, WE INTEGERATE THE REASONS CHECKING HERE RATHER THAN A SEPRATE SCRIPT
				
				
				
				//if idea is not null
			}  //end for loop checking each labels existence in sentence
		}// end if idea is not null	
		return tripleMatched;
	}
	
	public static void insertNearbySentencesInDB(Integer pep, Integer messageID,String v_message, String idea, String subject, String verb, String object,
    		String currentSentence, String previousSentence, String nextSentence,String v_author,String message_subject, Connection conn)
	{	 
		
		
//		//Now output to file and Console
		//boolean repeatedSentenecAndLabel = false,repeatedLabel = false, repeatedSentence = false, emptyRow = false;	
		//Main insertion												//email, we maybe dont need this here...maybe we can read from allmessages table using messageid
		String sql = "INSERT INTO keywords_twotermsmatched (pep, messageID,  label,subject, relation, object, currentSentence,ps,ns , author, message_subject) "
				+ "VALUES (?,?,?,?,?,?,?,?,?,?,?)";
		 
		try {			
			PreparedStatement statement = conn.prepareStatement(sql);			
			statement.setInt	 (1,pep);			statement.setInt	 (2, messageID);
			//statement.setString	 (3, v_message);	
			statement.setString	 (3, idea);
			statement.setString	 (4, subject);			statement.setString	 (5, verb);			statement.setString	 (6, object);
			statement.setString  (7, currentSentence);	statement.setString  (8, previousSentence);			statement.setString  (9, nextSentence);
			statement.setString  (10, v_author);		statement.setString  (11, message_subject);
			
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

	private static boolean negationJustBeforeSVO(String[] negationTerms, String CurrentSentenceString, String subject, String verb, String object) {
		String termsInSentence[] = CurrentSentenceString.split(" ");				
		Integer negationLoc=0,subjectLoc=0,verbLoc=0,objectLoc=0;
		for(String tis: termsInSentence) {
			for (int i = 0; i < termsInSentence.length; i++) {
		        if (termsInSentence[i].equals(subject)) {
		            //now make sure all S,V,O terms are after
		        	subjectLoc=i;
		        }
		    }
			for (int j = 0; j < termsInSentence.length; j++) {
		        if (termsInSentence[j].equals(verb)) {
		            //now make sure all S,V,O terms are after
		        	verbLoc=j;
		        }
		    }
			//check if verb ==null
			if(object==null || object.length() ==0 || object.isEmpty()) 
			{}
			else {
				for (int k = 0; k < termsInSentence.length; k++) {
			        if (termsInSentence[k].equals(verb)) {
			            //now make sure all S,V,O terms are after
			        	objectLoc=k;
			        }
			    }
			}
				
		}
		for(String tis: termsInSentence) {
			for(String ns: negationTerms) {
				if(tis.equals(ns)) {		//negation term matched
					for (int i = 0; i < termsInSentence.length; i++) {
				        if (termsInSentence[i].equals(ns)) {
				            //now make sure all S,V,O terms are after
				        	negationLoc=i;
				        }
				    }
					//check if precedes just by 2 places
					if(negationLoc < subjectLoc && (subjectLoc-negationLoc<2)) {
						return true;
					} 
					if(negationLoc < verbLoc    && (verbLoc-negationLoc<2)) {
						return true;
					}
					if(negationLoc < objectLoc   && (objectLoc-negationLoc<2)) {
						return true;
					}					
				}
			}
		}
		return false;
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

	public static void outputTripleExtractionMatchingResultsToFileAndDatabase(TripleProcessingResult resultObject, String CurrentSentenceString, String v_idea, Integer v_pepNumber,
			Boolean checkReverbTrue, Boolean checkClauseIETrue, Boolean checkOllieTrue, Boolean dependencyTrue, FileWriter writerAll, FileWriter writerForDisco,
			ProcessingRequiredParameters prp) throws IOException{		
		//		System.out.println("here 56 inside"); 
		//writerForDisco.write("here 56 inside"); 

		//System.out.println("ClauseIETrue ==true " + checkClauseIETrue); 
		//writerForDisco.write("ClauseIETrue ==true " + checkClauseIETrue );

		/*
		if (checkReverbTrue ==true ){
			//writerForDisco.write("here 56 reverb"); 
			try {
				System.out.println("\n" +"Reverb Output");

				if(resultObject.getTripleMatched(1)!=null){				
						writerForDisco.write("| R: " +resultObject.getLabels(1));
				}

				else {
					writerAll.write("\n"+"No Triples Matched in Reverb Library");				  
					writerForDisco.write("| R:");
				}
				//END NEW	
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

//		if (checkDependencyTrue ==true){
//			try {
//				//rvr.findRelations(CurrentSentenceString, subject, object, verb,v_idea, v_pepNumber, v_date, v_message_ID,author,  sentenceCounter);
//				//$$							String dependency = snlptdg.processText(CurrentSentenceString, false, bw, v_idea);
//			} catch (Exception e) {		
//				e.printStackTrace();
//			}
//		}

		if (checkClauseIETrue ==true){
			//writerForDisco.write("here 56 clausie"); 
			try {
				//System.out.println("\n" +"ClausIE Output r");
				if (resultObject.getTripleMatched(2)!=null){
					//System.out.println("a");					
					//System.out.println("b v_idea" + resultObject.getLabels(2));
					writerForDisco.write("|" + resultObject.getLabels(2));		//writerForDisco.write("| C: " + resultObject.getFinalIdea(2));
				}
					//System.out.println("d");
					writerAll.write("\n"+"No Triples Matched in CIE Library");
					writerForDisco.write("|");

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (checkOllieTrue ==true)
		{
			//writerForDisco.write("here 56 ollie"); 
			try {
				System.out.println("\n" +"Ollie Output");
				if(resultObject.getTripleMatched(3)!=null){					 
						writerForDisco.write("| O: " +resultObject.getLabels(3));
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		 */
		//write previous sentence
		//		writerForDisco.write("|Prev Sen " +resultObject.getPrevSentence());
		//write next sentence
		//		writerForDisco.write("|Next Sen " +resultObject.getNextSentence());	
		//write curr paragraph
		//		writerForDisco.write("|" +resultObject.getCurrParagraph());	
		//write prev paragraph
		//		writerForDisco.write("|" +resultObject.getPrevParagraph());	
		//write next paragraph
		//		writerForDisco.write("|" +resultObject.getNextParagraph());	

		//INSERT INTO DATABASE

		try {
			CheckInsertResult cir = new CheckInsertResult();
			//cir.connect();
			if(prp.getOutputfordebug())
				System.out.println("Going to write Result to database");
			cir.checkInsertResult(resultObject, prp);
			//cir.closeConnection();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	//only output that discussion is taking place
	public static void outputDiscussionStateToFile(Integer v_pepNumber, Date v_date, Integer v_message_ID, String author, String CurrentSentenceString, 
			String v_idea, FileWriter writerAll, FileWriter writerForDisco ) throws IOException
	{
		writerAll.write("\n\n" +  v_pepNumber + " | " + v_date+ " | " +  v_message_ID + " | " + author   + " | " + v_idea + " | " + CurrentSentenceString);
		writerForDisco.write("\n"   +  v_pepNumber + " | " + v_date+ " | " +  v_message_ID + " | " + author   + " | " + v_idea + " | " + CurrentSentenceString);

		writerForDisco.write("|R: discussion " + "| discussion" + "|O: discussion");
		writerAll.write("|R: discussion " + "| discussion" + "|O: discussion");
	}

	//if all terms in triple in current sentence
	public static boolean ifSentenceContainsInputFileTripleTerms(String CurrentSentenceString, String v_subject, String v_verb, String v_object){
		boolean foundSubject = false, foundVerb = false;
		boolean foundObject = false;

		System.out.println("\nChecking SV,O in sentence " + v_subject + " " + v_verb + " "+ v_object);

		//deal != ideal therefore we use equals function below
		String[] sentence = CurrentSentenceString.split(" ");
		for(String word: sentence)
		{
			if(word.toLowerCase().equals(v_subject))
				foundSubject= true;
			if(word.toLowerCase().equals(v_verb))
				foundVerb= true;
			if(word.toLowerCase().equals(v_object))
				foundObject= true;
		}		
		return foundSubject && foundVerb && foundObject;
	}

	//this is never called..bu why not called?  as of aug 201y7
	public static Boolean checkEachSentenceAgaistDifferentLibrariesForReasons(String v_message, String entireParagraph, String CurrentSentenceString,  ArrayList<LabelTriples> labels, 
			String PreviousSentenceString, String wordsFoundList, Integer v_pepNumber, Date v_date, Integer v_message_ID, String author,  Integer paragraphCounter, Integer sentenceCounter, 
			String prevSentence, String previousParagraph, String nextParagraph, ReVerbFindRelations rvr, JavaOllieWrapperGUIInDev jw, ClausIECaller cie, ClausIEMain cm, boolean reverbTrue, boolean dependencyTrue,
			List<String> sentencesRemoveDuplicates, UseDISCOSentenceSim ds, ArrayList<LabelTriples> reasonLabels, FileWriter writerAll,           //MyFilteredClassifier fc,
			FileWriter writerForDisco, String[] conditionalList,String[] negationTerms, 
			String[] reasonTerms, String[] reasons, String [] v_statesToGetRoles , String [] dontCheckLabels,ProcessingRequiredParameters prp,
			boolean isFirstParagraph, boolean isLastParagraph) throws IOException{    //, StanfordNLPToolGUI snlptdg   StanfordCoreNLPFunctions snlp
		String v_idea = "", subject = "",verb = "", object = "", PrevSentence = "", CurrSentence = "";

		System.out.println("Checking EachSentence Agaist Different Libraries For Reasons");

		//store the result here
		TripleProcessingResult resultObject =  new TripleProcessingResult();

		//some sentences contain double sets of triples which should be captured
		// for example Sentence:  "Does anybody have any > preferences as to the voting method we should use?"
		// has two sets of triples in clauseIE 
		//		Propositions     :  ("anybody", "have", "any preferences as to the voting method")
		//  						("we", "should use", "the voting method")

		String second_verb = "", second_subject = "", second_object = "", nextSentence = "temp", space = " ";
		boolean matchFound = false, triple = false, doubleTriple = false, stateDouble = false,	$ = false;		

		//			for (String bi : Labels) {
		//if(!labels.isEmpty()){
		//	int counter =0;

		doubleTriple = triple = stateDouble = false;
		//this used to checks previous sentences for part of the triple 
		//the input file has a previous sentence for checking but now will code it differenntly.when?

		if(!labels.isEmpty())
		{
			int counter =0;
			for (int x=0; x <labels.size(); x++)
			{
				if(labels.get(x).getIdea() != null)
				{
					v_idea = labels.get(x).getIdea();			subject = labels.get(x).getSubject();			verb = labels.get(x).getVerb();		//no verb egual to pep
					object = labels.get(x).getObject();

					if (object.isEmpty() || object == null)
						object="";

					//System.out.println("here " + words[0] + words[1] + words[2]+ words[3]);

					//check previous and next sentences

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
					//NER Checking will be done on these asterisk, but for the moment let it be empty so it can be matched 
					//but triple checking time NER will be done and checked if the asterisk * is a PERSON
					boolean subjectContainsAsterisk=false,objectContainsAsterisk=false;
					if (subject.equals("*"))	{
						//check to see if person is returned
						subject = "";						subjectContainsAsterisk=true;
						//System.out.println("\t\tsubject= * " + subject + " " + verb + " " + object);
					}
					if (object.equals("*")){
						object = "";							objectContainsAsterisk=true;						System.out.println("Object= *:" );	
					}	

					boolean include = false;
					//if previous sentence contains subject or object and current sentnce contains verb								
					if ((PreviousSentenceString.toLowerCase().contains(subject) || PreviousSentenceString.toLowerCase().contains(object))										
							&& (CurrentSentenceString.toLowerCase().contains(verb)))
					{ 
						//mmm											System.out.println("TS Prev: " + PreviousSentenceString + " Curr: " + CurrentSentenceString);
						wordsFoundList = v_idea + ", " + PreviousSentenceString + ", " + CurrentSentenceString;        //one string that would be appended
						//System.out.println(v_pepnumber + " idea=" + idea + " entity=" + entity + " action=" + action + " pep=" + pep + " wordsFoundList in Fn " + wordsFoundList);			
						prevSentence = PreviousSentenceString;						matchFound = true;						include =true;
					}

					//i happens very often so seprate
					boolean i = false;
					if(subject.toLowerCase().equals("i")){
						i = true;						subject = " " + subject + " ";
						//CurrentSentenceString = " " + CurrentSentenceString;
					}

					//add sentence to candidateSentencesList - if does not exist, add and then carry on - else if exists, dont carry on				
					//whetherToProcessSentence(CurrentSentenceString)==true && 
					//if all terms in triple in current sentence
					//(include ==true) ||  removes this clause
					//check triples in sentences	
					boolean foundTripleTermsinSentence = ifSentenceContainsInputFileTripleTerms(CurrentSentenceString, subject, verb, object);
					System.out.println("FILTERING CANDIDATE SENTENCES \n Comparing sentence " + CurrentSentenceString + " against inputfile :  subject=" + subject + " verb=" + verb + " object=" + object);

					if (foundTripleTermsinSentence) {			
						wordsFoundList = v_idea + ", " + CurrentSentenceString;        //one string that would be appended
						//System.out.println(" subject=" + subject + " verb=" + verb + " object=" + object);
						//System.out.println(v_pepnumber + " idea=" + idea + " entity=" + entity + " action=" + action + " pep=" + pep + " wordsFoundList in Fn " + wordsFoundList);			
						prevSentence = PreviousSentenceString;
						matchFound = true;	

						//TEMPORARY way to exclude repeated sentences for each pep from processing
						//0. check that this sentence is not already in the list for current pep
						// if exists, dont inlude 
						// else add this sentence to the list along with the pep number and process

						boolean ProcessSentence = true, found = false;
						//For repeated sentences and instances where there are multiple triples coded for a label
						//i.e. bdfl accepted pep has many triples stated in input file
						//STSRT HERE
						//						if(!candidateSentencesList.isEmpty()){
						//							System.out.println("candidate list not empty ");
						//							int counter =0;
						//					        for (String temp : candidateSentencesList) {					        	
						//					        	//System.out.println("COUNTER " + temp + " "+ candidateSentencesList.size() + " CurrentSentenceString "+CurrentSentenceString);
						////					        	String sections[] = temp.split("|");
						////					        	String id = sections[0]; 
						////					        	String CurrSen = sections[1]; 
						//					        	//&& v_idea.toLowerCase().equals(id.toLowerCase())
						//					        	//CurrSen.toLowerCase()
						//					        	if( CurrentSentenceString.toLowerCase().equals(temp)    )	{ 	
						//									ProcessSentence= false;
						//									found=true;
						//									System.out.println("Sentence and Idea FOUND in list " + CurrentSentenceString);						 				 	
						//								}							
						//								System.out.println(" list not empty but not found in list");	
						//							}
						//					        //if candidatelist is not empty, but does not have that sentence, then add taht sentence
						//							if(found==false){
						//								System.out.println("Candidate list not empty - SENTENCE NOT FOUND in list - and added ");
						//								//v_idea+"|"+
						//								candidateSentencesList.add(CurrentSentenceString.toLowerCase());
						//							}
						//					    }
						//						else {
						//							System.out.println("SENTENCE NOT FOUND in list and added ");
						//							//v_idea+"|"+
						//							candidateSentencesList.add(CurrentSentenceString.toLowerCase());						
						//						}

						//check to see conditional sentences
						boolean SentenceConditional = false;
						String questionPhrases[] = {"if","should have been","i think", "i hope", "it will"};

						// check for each verb, if the verb doesnt fit in 

						// for each questionphrase
						// add the current verb/relation (which is being checked) at end
						// if match, dont process
						// else process
						for (String qp: questionPhrases){
							qp = qp + " " + verb;
							if (CurrentSentenceString.toLowerCase().contains(qp)) {
								SentenceConditional = true;
							}
						}

						//sentencves creating error
						String [] complex = {"Mortensen  > XX  XX  01  04  03  02","<bhopyel at web.de>","bhopyel at web.de> 11 | 12 | 04 | 03 | 05 | ","jjpym at tiscali.dk> xx", "11 | 12 | 04 | 03 | 05 | 01 | 06 | ","01 | 04 | 03 | 02 | xx | xx | xx | "," i\\/o <br> <br> <br> to download python 3.4.0 visit : <br> <br> <a href ","major new features and <br> changes in the 3.4 release series include : <br> <br>", "pep : xxx title : cofunctions version : $ revision $ last-modified : $ date $ status : draft content-type :"};

						for (String c: complex){
							if (CurrentSentenceString.toLowerCase().contains(c.toLowerCase()))	{	
								ProcessSentence = false;
								System.out.println("COMPLEX SENTENCE");
							}
						}

						if (CurrentSentenceString.length() > 1000)	{	
							ProcessSentence = false;
							System.out.println("Too Long SENTENCE");
						}

						boolean reverbFound = false, clasueIETrueFound = false, OllieTrueFound = false;

						if(ProcessSentence == true ) {
							//	sentencesRemoveDuplicates.add(CurrentSentenceString.toLowerCase());					

							//							System.out.println("---------------------------------------------------------");
							//							System.out.println("Sentence: " + "("+v_pepNumber+") " + CurrentSentenceString );
							//							System.out.println("Reason: " + v_idea + " Subject: " + subject + " Verb: " + verb + " Object: " + object);
							//							writer.write ("\n"+v_pepNumber + " | " + v_date+ " | " +  v_message_ID + " | " + author + " | " + v_idea + " | " + CurrentSentenceString);
							//							writer2.write("\n\n"+v_pepNumber + " | " + v_date+ " | " +  v_message_ID + " | " + author + " | " + v_idea + " | " + CurrentSentenceString);

							//remove all commas
							//CurrentSentenceString = CurrentSentenceString.replace(",", " ");


							//just null to pass in the function below
							String MessageAuthorsRole = "";

							//allowing which library to proces is important

							//PASS TRIPLES TO ALL
							//FOR THE MOMENT JUST OUTPUT ALL TO SCREEN, NOT TO FILE. That can be done later

							//REVERB ONLY DOES TRIPLES , SO STATES/DOUBLES SHOULD NOT BE PASSED HERE
							//PASS IT TO CLAUSIE AND TRY OLLIE
							//REVERB		&& stateDouble == false
							ProcessLabels pt = new ProcessLabels();
							if (reverbTrue ==true ){
								try {
									System.out.println("\n" +"Reverb Reason Output");
									String tripleArray[][]  = rvr.findRelations(CurrentSentenceString, subject, object, verb,v_idea, v_pepNumber, v_date, v_message_ID,author,  sentenceCounter,  writerForDisco,
											writerAll, conditionalList, reasonTerms, reasonLabels, 2);

									//now send thisfor processing

									//resultObject = pt.processTriples(v_message,resultObject, tripleArray,cm, rvr, jw, entireParagraph, CurrentSentenceString,PreviousSentenceString, subject, object, verb,v_idea, v_pepNumber, v_date, v_message_ID,author,MessageAuthorsRole,  paragraphCounter, sentenceCounter, previousParagraph, nextParagraph, writerForDisco,writerAll, conditionalList, negationTerms, reasonTerms, reasons, reasonLabels, 1,  v_statesToGetRoles);
									//									resultObject = pt.processTriples(v_message, resultObject, tripleArray,cm, rvr, jw,entireParagraph, CurrentSentenceString,PreviousSentenceString, subject, object, verb,v_idea, v_pepNumber, v_date, v_message_ID,author, MessageAuthorsRole, paragraphCounter, sentenceCounter, previousParagraph, nextParagraph, writerForDisco, writerAll, conditionalList,negationTerms, reasonTerms,reasons,  reasonLabels, 2, v_statesToGetRoles);
									resultObject = pt.processTriples(v_message, resultObject, tripleArray,cm, rvr, jw,entireParagraph, CurrentSentenceString,PreviousSentenceString,nextSentence, 
											subject, object, verb,v_idea, v_pepNumber, v_date, v_message_ID,author, MessageAuthorsRole, paragraphCounter, sentenceCounter, previousParagraph, nextParagraph, 
											writerForDisco, writerAll, conditionalList,negationTerms, reasonTerms,reasons,  reasonLabels, 2, v_statesToGetRoles, dontCheckLabels,subjectContainsAsterisk,objectContainsAsterisk,prp,
											isFirstParagraph, isLastParagraph); //, snlp);

									//from before
									//writer.write ("\n"+v_pepNumber + " | " + v_date+ " | " +  v_message_ID + " | " + author + " | " + v_idea + " | " + CurrentSentenceString);

									//write result to file
									writerAll.write("\n Triple Matched in new code:  " + resultObject.getTripleMatched(1));									
									resultObject.getTripleMatched(1);

									//use this to only output matched triples in library, not unmatched ones
									//BUT THESE WILL ONLY DO IT FOR CLAUSEIE
									if (resultObject.getTripleMatched(1)!=null){
										//then output
									}
									else {
										//dont
									}

								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
							//pass both triples and doubles
							//make sure for doubles, change around while comparing
							if (dependencyTrue ==true){
								try {
									//rvr.findRelations(CurrentSentenceString, subject, object, verb,v_idea, v_pepNumber, v_date, v_message_ID,author,  sentenceCounter);
									//$$							String dependency = snlptdg.processText(CurrentSentenceString, false, bw, v_idea);
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
							//pass both triples and doubles
							//make sure for doubles, change around while comparing
							boolean clasueIETrue = true;
							if (clasueIETrue ==true){
								try {
									System.out.println("\n" +"ClausIE Reason Output");
									//clasueIETrueFound = cm.computeClausIE(CurrentSentenceString, subject, object, verb,v_idea, v_pepNumber, v_date, v_message_ID,author,  sentenceCounter, writer, writer2, conditionalList, why, reasonLabels, 2);

									//									resultObject = pt.processTriples(v_message, resultObject, tripleArray,cm, rvr, jw,entireParagraph, CurrentSentenceString,PreviousSentenceString, 
									//											subject, object, verb,v_idea, v_pepNumber, v_date, v_message_ID,author, MessageAuthorsRole,paragraphCounter, sentenceCounter,  writerAll,
									//											writerForDisco, conditionalList,negationTerms, reasonTerms,reasons,  reasonLabels, 2, v_statesToGetRoles);
									String tripleArray[][]  = cm.computeClausIE(CurrentSentenceString, showCIEDebugOutput);
									resultObject = pt.processTriples(v_message, resultObject,tripleArray ,cm, rvr, jw,entireParagraph, CurrentSentenceString,PreviousSentenceString, nextSentence, 
											subject, object, verb,v_idea, v_pepNumber, v_date, v_message_ID,author, MessageAuthorsRole, paragraphCounter, sentenceCounter, previousParagraph, nextParagraph, 
											writerForDisco, writerAll, conditionalList,negationTerms, reasonTerms,reasons,  reasonLabels, 2, v_statesToGetRoles, dontCheckLabels,subjectContainsAsterisk,
											objectContainsAsterisk,prp,isFirstParagraph, isLastParagraph); //, snlp);

									//resultObject.getTripleMatched(2);

									//use this to only output matched triples in library, not unmatched ones
									//BUT THESE WILL ONLY DO IT FOR CLAUSEIE
									if (resultObject.getTripleMatched(2)!=null && !resultObject.getTripleMatched(2).isEmpty()){
										//then output
										System.out.println("\n" +"ClausIE not empty or null");
										resultObject.setClausIE(v_idea);
									}
									else {
										System.out.println("\n" +"ClausIE is empty or null");
										//System.out.println("------psubj " + psub + " pverb " + pverb + " pobj " + pobj);
									}


									//write another similar function to above which just returns the double ayyar of clauses
									// and do teh processing in another function and call it here which can be used by the 2 other libraries, reverb and ollie

									//rvr.findRelations(CurrentSentenceString, subject, object, verb,v_idea, v_pepNumber, v_date, v_message_ID,author,  sentenceCounter);
									//ClausIE
									//will return a number of popositions
									/*
									Propositions     : ("I", "believe", "PEP 3144 is ready for your review When you get a chance")
									                   ("PEP 3144", "is", "ready for your review When you get a chance")
									                   ("PEP 3144", "is", "ready for your review")
									                   ("your", "has", "for review")
									                   ("you", "get", "a chance When")
									                   ("you", "get", "a chance")
									 */
									//pass the triple to check if it is contained within any of the propositions found
									//returns the idea
									//		String  idea = cie.call(CurrentSentenceString, subject, verb, object);
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
							boolean OllieTrue = true;
							if (OllieTrue ==true){
								try {
									System.out.println("\n" +"Ollie Reason Output");
									String tripleArray[][] = jw.computOllie(CurrentSentenceString, subject, object, verb,v_idea, v_pepNumber, v_date, v_message_ID,author,  sentenceCounter, writerForDisco, 
											writerAll, conditionalList, reasonTerms, reasonLabels, 2);

									//now send thisfor processing

									//									resultObject = pt.processTriples(v_message, resultObject, tripleArray,cm, rvr, jw, entireParagraph, CurrentSentenceString,PreviousSentenceString, subject, object, verb,v_idea, v_pepNumber, v_date, v_message_ID,author,MessageAuthorsRole,  paragraphCounter,sentenceCounter,previousParagraph, nextParagraph, writerForDisco, writerAll, conditionalList,negationTerms, reasonTerms,reasons,  reasonLabels, 2, v_statesToGetRoles);
									resultObject = pt.processTriples(v_message, resultObject, tripleArray,cm, rvr, jw,entireParagraph, CurrentSentenceString,PreviousSentenceString,nextSentence, 
											subject, object, verb,v_idea, v_pepNumber, v_date, v_message_ID,author, MessageAuthorsRole, paragraphCounter, sentenceCounter, previousParagraph, nextParagraph, 
											writerForDisco, writerAll, conditionalList,negationTerms, reasonTerms,reasons,  reasonLabels, 2, v_statesToGetRoles, dontCheckLabels,subjectContainsAsterisk,
											objectContainsAsterisk,prp,isFirstParagraph, isLastParagraph); //, snlp);

									//from before
									//writer.write ("\n"+v_pepNumber + " | " + v_date+ " | " +  v_message_ID + " | " + author + " | " + v_idea + " | " + CurrentSentenceString);

									//write result to file
									writerAll.write("\n Triple Matched in new code:  " + resultObject.getTripleMatched(3));

									resultObject.getTripleMatched(3);

									//use this to only output matched triples in library, not unmatched ones
									//BUT THESE WILL ONLY DO IT FOR CLAUSEIE
									if (resultObject.getTripleMatched(3)!=null){
										//then output
									}
									else {
										//dont
									}

									//rvr.findRelations(CurrentSentenceString, subject, object, verb,v_idea, v_pepNumber, v_date, v_message_ID,author,  sentenceCounter);
									//ClausIE
									//will return a number of popositions
									/*
									Propositions     : ("I", "believe", "PEP 3144 is ready for your review When you get a chance")
									                   ("PEP 3144", "is", "ready for your review When you get a chance")
									                   ("PEP 3144", "is", "ready for your review")
									                   ("your", "has", "for review")
									                   ("you", "get", "a chance When")
									                   ("you", "get", "a chance")
									 */
									//pass the triple to check if it is contained within any of the propositions found
									//returns the idea
									//String  idea = cie.call(CurrentSentenceString, subject, verb, object);
								} catch (Exception e) {								
									e.printStackTrace();
								}
							}

						}//end if

						//cheack for reasons
						if(reverbFound==true ||clasueIETrueFound == true ||OllieTrueFound ==true){
							//add additional column stating wwhther to include
							System.out.println("\n"+"Reason Found");
							writerAll.write (" | " + " Reason Found " );
							writerForDisco.write(" | " + " Reason Found " );

							//we have matched some triple in teh sentence 
							$ = true;

						}

						/*
						System.out.print("\n" + v_pep + " , " + v_date + ", " + v_message_ID+ " , " + author  + ", KeywordMatch="+ v_idea + " Sentence=| " + CurrentSentenceString + " |, SentenceClassification=" + SentenceClassification +  " , DISCO Sim=" + DISCOSim ); // + wordsFoundList   + statusFrom + " -> " + statusTo);
				//		System.out.println("CurrentSentenceString " + CurrentSentenceString +" PreviousSentenceString " + PreviousSentenceString);
						//writer.append("\n" + v_pep + " , " + v_date + ", " + author  + ", " + "Sentence=| " + CurrentSentenceString + " |, KeywordMatch=" + v_idea + " , SentenceClassification=" + SentenceClassification +  " , DISCO Sim=" + DISCOSim );
						writer2.append("\n" + v_pep + " , " + v_date + ", " + v_message_ID+ " , " + author + ", "  + "Sentence=| " + CurrentSentenceString + " |, KeywordMatch=" + v_idea + " , SentenceClassification=" + SentenceClassification +  " , DISCO Sim=" + DISCOSim);
						//JTable
				        v_model.addRow(new Object[] { v_pep,v_message_ID, CurrentSentenceString, v_idea, SentenceClassification, DISCOSim});
						 */

						//with dependency
						//***										 System.out.println("\nSentence: " + CurrentSentenceString);
						//***										 System.out.print("dependency=" + dependency);
						//xxx										 v_model.addRow(new Object[] { v_pep + " " + v_message_ID, idea, CurrentSentenceString, v_idea, SentenceClassification, "" });  //idea - clausIE output
						/*
				        System.out.print("\n" + v_pep + " , " + v_date + ", " + v_message_ID+ " , " + author  + ", KeywordMatch="+ v_idea + " Sentence=| " + CurrentSentenceString + " |, SentenceClassification=" + SentenceClassification +  " , dependency=" + dependency ); // + wordsFoundList   + statusFrom + " -> " + statusTo);
				//		System.out.println("CurrentSentenceString " + CurrentSentenceString +" PreviousSentenceString " + PreviousSentenceString);
						//writer.append("\n" + v_pep + " , " + v_date + ", " + author  + ", " + "Sentence=| " + CurrentSentenceString + " |, KeywordMatch=" + v_idea + " , SentenceClassification=" + SentenceClassification +  " , DISCO Sim=" + DISCOSim );
						writer2.append("\n" + v_pep + " , " + v_date + ", " + v_message_ID+ " , " + author + ", "  + "Sentence=| " + CurrentSentenceString + " |, KeywordMatch=" + v_idea + " , SentenceClassification=" + SentenceClassification +  " , dependency=" + dependency);
						//JTable
				        v_model.addRow(new Object[] { v_pep,v_message_ID, CurrentSentenceString, v_idea, SentenceClassification, dependency});

						 */
					} //end if

				}
			}	
		}		
		//}  //END ELSE							

		return $;
	}
	@Override
	protected void finalize() throws Throwable
	{
		// System.out.println("From Finalize Method, i = "+i);
	}

	//System.out.println("here " + words[0] + words[1] + words[2]+ words[3]);					
	//check previous and next sentences

	//if previous sentence contains subject or object and current sentnce contains verb			

	/* not sure if this is the right place to implement this
	if ((PreviousSentenceString.toLowerCase().contains(subject) || PreviousSentenceString.toLowerCase().contains(object))										
		&& (CurrentSentenceString.toLowerCase().contains(verb)))
		{ 
//mmm											System.out.println("TS Prev: " + PreviousSentenceString + " Curr: " + CurrentSentenceString);
			wordsFoundList = v_idea + ", " + PreviousSentenceString + ", " + CurrentSentenceString;        //one string that would be appended
			//System.out.println(v_pepnumber + " idea=" + idea + " entity=" + entity + " action=" + action + " pep=" + pep + " wordsFoundList in Fn " + wordsFoundList);			
			prevSentence = PreviousSentenceString;
			matchFound = true;
			include =true;
	}
	 */

	//i happens very often so seprate
	//no longer needed as terms are checked individually ..like word matches ...3-feb-2017 
	//					boolean i = false;
	//					if(subject.toLowerCase().equals("i")){
	//						i = true;
	//						subject = " " + subject + " ";
	//						//CurrentSentenceString = " " + CurrentSentenceString;
	//					}

	//add sentence to candidateSentencesList - if does not exist, add and then carry on - else if exists, dont carry on				
	//whetherToProcessSentence(CurrentSentenceString)==true && 
	//if all terms in triple in current sentence
	//(include ==true) ||  removes this clause
	//check triples in sentences	

	//check for instances where we check tw or more terms in S,V or O
	//    ("the full results of the everyone 's vote", "is disclosed", "at the close of voting")
	// the subject requires we to make sure the terms subject has results and vote in any combination
	/* 
	boolean doubleTerms=false; 
	if (subject.toLowerCase().contains("|")){
		//split and check if sentence has all terms
		 String s[] = subject.split("|");
		 boolean subTerm = pms.stringContains(CurrentSentenceString.toLowerCase(), s);
		 doubleTerms = subTerm;
	}
	if (verb.toLowerCase().contains("|")){
		//split and check if sentence has all terms
		 String v[] = verb.split("|");
		 boolean verbTerm = pms.stringContains(CurrentSentenceString.toLowerCase(), v);
		 doubleTerms = verbTerm;   //keep checking will make sure that its checks for all terms in SVO
	}
	if (object.toLowerCase().contains("|")){
		//split and check if sentence has all terms
		 String o[] = object.split("|");
		 boolean obTerm = pms.stringContains(CurrentSentenceString.toLowerCase(), o);
		 doubleTerms = obTerm;		//keep checking will make sure that its checks for all terms in SVO
	}
	 */
	
}
