package Process.processLabels;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;

import callIELibraries.JavaOllieWrapperGUIInDev;
import Process.processMessages.LabelTriples;
import Process.processMessages.ProcessingRequiredParameters;
import Process.processMessages.TemporalReasoningFromText;
import callIELibraries.ReVerbFindRelations;
import de.mpii.clause.driver.ClausIEMain;
import de.mpii.clause.driver.Reasons;
import de.mpii.clausie.Clause;
import de.mpii.clausie.Proposition;

import java.time.Duration;
import java.time.Instant;
public class ProcessLabels {
	
	public static String conditionalList[];	  
    public static String why[]; 
      	
    public void setVariables(String v_conditionalList[], String v_why[]){
    	conditionalList = v_conditionalList;
    	why = v_why;
    } 
    
    //ArrayList<Label>
    
	// make sure InputFile S,V,O terms are in the sentence triples extracted
    // Example call_for_vote i plan run_vote
	// boolean thirdPart = POScontainsAllTerms(subject,v_object) ||   POScontainsAllTerms(object,v_object); 
    // sometimes the inputfile S,V,O term may contain multiple terms
														// little   //big
														//InputFile //sentence POS
	public static Boolean POScontainsAllTerms(String inputFileTerm, String sentencePOS, ProcessingRequiredParameters prp) 
	{
//		System.out.println("\t\t\t POS contains AllTerms from InputFile POS, terms to check : (" + inputFileTerm + ") sentencePOS : (" + sentencePOS+ ")");
		
		//if nothing is specified here, return true (matching doubles doesnt require object..:) ) and we want to match any term - concentrating more on secondTerm and ThirdTerm 
		if (inputFileTerm == "" || inputFileTerm == null || inputFileTerm.isEmpty()){		
			if(prp.getOutputfordebug())
				System.out.println("\t\t\tEmpty inputFileTerm returning true: " + inputFileTerm);	
			return true;
		}
		// temporray
		if (sentencePOS == "" || sentencePOS == null) {
			if(prp.getOutputfordebug())
				System.out.println("\t\t\tSentence POS is null or empty, returning false: " + sentencePOS);	
			return false;
		}
		String generalisedList[] = prp.getGeneralisedList();  			//String generalisedList[] = {"any","every"};
		String generalisedSubjectObjectList[] = prp.getGeneralisedSubjectObjectList(); 	//String generalisedSubjectObjectList[] = {"pep","proposal"};
		String combination=null;		
		//jan 2019
		String antiGeneralisedList[] = prp.getAntiGnerelisedList();  	
		boolean antiGeneralisedfound = false;
		
		for (String s: generalisedSubjectObjectList){
			if (inputFileTerm.contains(s)){								//if inputfile label contains "pep" or "proposal", check if its prefixed by any or every
				for (String g: generalisedList){
					for (String gsv: generalisedSubjectObjectList){
						combination = g + " " + gsv;
						//if inputFile term contains "any pep" or "every pep"
						antiGeneralisedfound = false;
						if (sentencePOS.contains(combination)) {
							for (String ag: antiGeneralisedList){ System.out.println("\t\t\t\tTesting Anti Generalised Term ("+ ag +")");
								if (sentencePOS.contains(ag)) {
									antiGeneralisedfound=true;	System.out.println("\t\t\tGeneralised Term Exists: ("+combination+",) but Anti Generalised Term also exists ("+ ag +")"); 
								}
							}
							if (!antiGeneralisedfound) {
								System.out.println("\t\t\tGeneralised Term Exists: "+combination+", sentencePOS ("+sentencePOS+")"); 
								return false;
							}
						}
					}
				}
			}
		}		
				
		// if (terms.contains("rough"))
		// System.out.println("Testing terms: " + terms);
		//Every term from inputfile should be in extracted sentencePOS
		String[] splited = inputFileTerm.split(" ");	
		
		//for all terms from inputfile for a S,V,O
		for (String s : splited) {
			if (!sentencePOS.toLowerCase().contains(s)) { //if atleast one term does not exist
				// System.out.println(" one not matched");
				return false;
			}
		}
//		System.out.println("\t\t\t Found all terms in inputFile POS : (" + inputFileTerm + ") exist in sentence POS: " + sentencePOS);
		return true;
	}
    
	public static String removePunctuations(String s) {
	    String res = "";
	    for (Character c : s.toCharArray()) {
	        if(Character.isLetterOrDigit(c) || Character.isSpaceChar(c) || Character.toString(c).equals("'"))
	            res += c;
	    }
	    return res;
	}
	//if pos contains all 
	public static boolean clauseContainsAll(String pos,String s, String v, String o){
		System.out.println("\t\tclauseIEContainsAll() pos ("+ pos+ ") s: " +s +" v: "+ v+ " o: "+o );
		String[] posStr =null,vStr=null,sStr=null,oStr=null;		
		
		List<String> toCheckList=  new ArrayList<String>();
		
		//deal != ideal therefore we use equals function below
		if (pos == "null" || pos == null || pos.length() == 0){
			return false;
		}
		else{
			if (pos.contains(" ")){
				posStr = pos.toLowerCase().split(" ");
			}
			else{	//sentence may not have space, so therefore just one logical term and in this case return false
				return false;
			}
			if (s.contains(" ")){
				sStr = s.toLowerCase().split(" ");
				 // Add all elements to the ArrayList from an array.
		        Collections.addAll(toCheckList, sStr);
			}
			else{	//sentence may not have space, so therefore just one logical term and in this case return false
				toCheckList.add(s);
			}
			if (v.contains(" ")){
				vStr = v.toLowerCase().split(" ");
				Collections.addAll(toCheckList, vStr);
			}
			else{	//sentence may not have space, so therefore just one logical term and in this case return false
				toCheckList.add(v);
			}
				 //System.out.println("sStr "+ sStr + " vStr" +vStr);
		}
		if (o.isEmpty() || o.equals("")|| o == null || o.length() == 0){
			return Arrays.asList(posStr).containsAll(toCheckList);	
		}
		else {
			if (o.contains(" ")){
				oStr = o.toLowerCase().split(" ");
				Collections.addAll(toCheckList, oStr);
			}
			else{	//sentence may not have space, so therefore just one logical term and in this case return false
				toCheckList.add(o);
			}
			return Arrays.asList(posStr).containsAll(toCheckList);	
		}
//		if(pos.contains(s) && pos.contains(v) && pos.contains(o)){
//			return true;
//		}
//		else 
//			return false;
		
	}
	//we process the triples, current label triples from input file passed to this function
    public static TripleProcessingResult processTriples(String v_message, TripleProcessingResult result, String[][] v_tripleArray,ClausIEMain cm, ReVerbFindRelations rvr, 
    		JavaOllieWrapperGUIInDev jw, String entireParagraph, 
    		String currentSentence, String previousSentence, String nextSentence, String inputFileSubject, String inputFileObject, String inputFileVerb, String v_idea, Integer v_pepNumber,
			Date v_date, Integer v_message_ID, String author,String v_MessageAuthorsRole, Integer paragraphCounter, Integer sentenceCounter, 				//, reasonTerms, reasons, reasonLabels,			
			String previousParagraph, String nextParagraph, FileWriter writerForDisco, FileWriter writerAll, String conditionalStrings[], String v_negationTerms[], String [] reasonTerms, 
			String[] v_reasons, ArrayList<LabelTriples> reasonLabels,  Integer libraryToCheck, String [] v_statesToGetRolesAndReasons, String[] dontCheckLabels,
			boolean subjectContainsAsterisk, boolean objectContainsAsterisk,ProcessingRequiredParameters prp,
			boolean isFirstParagraph, boolean isLastParagraph) throws IOException    //, StanfordCoreNLPFunctions snlp
	{    	
    	//System.out.println("Triple Processing (matching extracted CIE triples from candidate sentence with inputFile triples one by one)");
		String library = assignLibrary(libraryToCheck, "");//libraryToCheck = 1(ReVerb),2 (ClausIE) and 3 (Ollie)
		result.setTripleMatched(null,libraryToCheck);	//set default value as false
		result.setAuthorsRole(v_MessageAuthorsRole);
		//maybe set the triple here
				
		String v_extractedSubject = "", v_extractedRelation = "", v_extractedObject = "",vv_extractedSubject = "", vv_extractedRelation = "", vv_extractedObject = "";
		boolean tripleMatchedOnce = false, foundNow = false, foundInPrevious = false,subjectFound = false, relationFound = false, objectFound = false;
		String text = "", leftOver = "", leftOverReason = "", leftOverStoryLine = "", conditional_v_extractedSubject = "", conditional_v_extractedRelation = "",conditional_v_extractedObject = "";
		
		//REASONS 	//Reasons foundReasonOnce = new Reasons();
		boolean foundReasonOnce = false;
		CheckAndGetReasons cgr = new CheckAndGetReasons();
		CheckConditional  cc = new CheckConditional();
		
//**		SentenceSimplification ss = new SentenceSimplification();	
		Integer y_counter=0;
		
		boolean permanentNegationJustPrecedes_TripleOrDouble_TermsInSameClause=false;
		
		for (y_counter=0; y_counter<10;y_counter++)	//FOR LOOP ALL THE TRIPLES HERE
		{			//assign array contents
			v_extractedSubject  = v_tripleArray[y_counter][0];			v_extractedRelation = v_tripleArray[y_counter][1];			v_extractedObject   = v_tripleArray[y_counter][2];
			if(prp.getOutputfordebug())
				System.out.println("CHECKING EACH EXTRACTED TRIPLE using Library: v_extractedSubject: ("+ v_extractedSubject + ") v_extractedRelation: (" +v_extractedRelation + ") v_extractedObject: (" +v_extractedObject + ")");
			//exit loop if nulls - dont process
			if ((v_extractedRelation=="")  || v_extractedRelation == null || v_extractedRelation.isEmpty() ){
					if(prp.getOutputfordebug())
						System.out.println("\t inputFileVerb Null - dont process");
					break;				
			}			
			//System.out.println("b4 v_extractedSubject 1: "+ v_extractedSubject + " v_extractedRelation: " +v_extractedRelation + " v_extractedObject: " +v_extractedObject);								
			v_extractedSubject =  cleanPOS(v_extractedSubject); //cleanAndOutput(v_extractedSubject, inputFileSubject,1);
			v_extractedRelation = cleanPOS(v_extractedRelation); //cleanAndOutput(v_extractedRelation, inputFileVerb,2);
			v_extractedObject =   cleanPOS(v_extractedObject); //cleanAndOutput(v_extractedObject, inputFileObject,3);
//			System.out.println("v_extractedSubject 2: "+ v_extractedSubject + " v_extractedRelation: " +v_extractedRelation + " v_extractedObject: " +v_extractedObject);	
			v_extractedRelation = removePunctuations(v_extractedRelation);
						
//			System.out.println("v_extractedSubject 3: "+ v_extractedSubject + " v_extractedRelation: " +v_extractedRelation + " v_extractedObject: " +v_extractedObject);			
			// this is dome for doubles instances - ie where inputFileObject is null in label so the third part obviously wont match

			//Negation Checking.
			//check and store value outside loop
			
			//very important see triple extracted from sentence below. Two sets of triples extracted but negation captured only in one of them 
			//sometimes checking terms in sentence is incorrect as all pos are in one part of triple and so we check for negation in verb, if it exists
			//First check...if a pos contains all terms of triple and the verb part has negation, we stop and dont process an further for that triple in that sentence as its already negated
			//eg "I would n't object to seeing this PEP approved as an informational PEP that described reST as an optional format for docstrings ."
			//Returns these triples and which are negative 
			// "I"	"would n't object"	"to seeing this PEP approved as an informational PEP"
			// "this PEP"	"be approved"	"as an informational PEP"
			// "an informational PEP"	"described"	"reST as an optional format"
			// "an informational PEP"	"described"	"reST for docstrings"
			// "an informational PEP"	"described"	"reST"
			
			//See Example above why we doing this. 
			//if extracted  object has all 3 terms we check the verb for negation, containsall checks to see if first argument contains all three other parameters
			//we will have to extend this to check for negation in the preceding pos if all svo in in one pos
			//Alternatives The PEP 433 is a previous attempt proposing various other alternatives , but no consensus could be reached .
			//"no consensus could be reached"	"be proposing"	"various other alternatives"
			//"no consensus"	"could be reached"
			
			//why we need to check subject
			//Please do n't waste time changing modules for which there is no consensus that this should be done .
			//"no consensus"	"there is"	"that this should be done for time changing modules"
			//"no consensus"	"there is"	"that this should be done"
			//"this"	"should be done"
		
			//does checking here, cause true negatives? In extreme cases only where although negation is present but triple is true
			boolean negationInVerb = false,currentNegationJustPrecedes_TripleOrDouble_TermsInSameClause = false;
			
			//jan 2019, we dont check negation for some labels like 'no consensus' as it wont be catched
			String dcl[] = prp.getDontCheckNegationForLabels(); boolean dontCheckThisLabelForNegation = false;
//			System.out.println("\t\tChecking getDontCheckLabels :");
			for (String s : dcl) { //System.out.println("\t\tChecking getDontCheckLabels : "+s);
				if(s.equals(v_idea)) { //System.out.println("\t\t.equals(v_idea)");
					dontCheckThisLabelForNegation = true;	System.out.println("\t\tDont check negation = true for label: "+v_idea);
				}
			}
			//CHECKING NEGATION IN OBJECT
			//why we checking here in beginning..as to eliminate unnecessary processing, 
			//if negation found in object and all s,v,o terms follow (meaning all s,v,o terms in same pos example 'before a consensus was reached unfortunately' ) in as part of the object, 
			//then we have negation and dont need to process any other triple from array
			
			//but for matched triples, we need to check negation in them
			//have to extend to check negation in same clause with triples
			//"The thread"	"died off"	"before a consensus was reached unfortunately"
			//"The thread"	"died off"
			//"a consensus"	"was reached"	"unfortunately"
			//we dont know in which extraced triple will be matched, so we set the negation to true and  check in end
			//the last triple will be matched, but first triple object must be checked and negation "before" captured
			if(!dontCheckThisLabelForNegation) {			
				if(prp.getOutputfordebug())
					System.out.println("\t\tFirst Check, Checking If Negation Precedes all SVOTerms() within Object ("+v_extractedObject+")");
			    //if all the 'inputFileSubject,inputFileVerb,inputFileObject,' terms occur after a negation in object part of triple
				if(checkClauseContainsAllSVO_IfNegationJustPrecedesSVOTermsInSameClause(v_extractedObject,inputFileSubject,inputFileVerb,inputFileObject,v_negationTerms, prp)) {	//if a clause contains all terms we check verb and object
					if(prp.getOutputfordebug())
						System.out.println("\t\t\tNegationJustPrecedesSVOTermsInSameClause. Found negation in before triple/double in OBJECT when all triple terms in same clause.Exiting Loop.");
							permanentNegationJustPrecedes_TripleOrDouble_TermsInSameClause=true;
							//if triple matched but in another triple there is negation in object make that triple negative for that idea
							result.setNegation(true, libraryToCheck);	//maybe this setting needs to move to the last part of function
							if(prp.getOutputfordebug())
								System.out.println("\t\t\tEnded Processing 345");
								break;	//end processing
				}
				else {
					if(prp.getOutputfordebug())
					System.out.println("\t\t\tDidnt Find Negation in before triple/double in object when all triple terms in same clause");
				}
			}
			//jan 2019, this check is not a replacement mechanism for checking negation in object, and this following example, the object has to be checked for negation in front on obj term
			//"The PEP author is responsible for building consensus within the community and documenting dissenting opinions."
			//"The PEP author"	"is"	"responsible for building consensus within the community"
			//"The PEP author"	"is"	"responsible for documenting dissenting opinions"
			//"The PEP author"	"is"	"responsible"			
			//END Negation Checking
			
			//Triple Matching
			boolean firstPart = false, secondPart = false, thirdPart= false;			
			//(Note: //if nothing is specified as inputFile object, return true and we want to match any term - concentrating more on secondTerm and ThirdTerm )
			//there was an issue with empty inputfile object, we dont want to make it tru if its empty, but the POScontainsAllTerms function, does it by default
			//so we add a check
			boolean inputFileSubjectMatched=false,inputFileObjectMatched=false;
			if(inputFileObject==null || inputFileObject.isEmpty() || inputFileObject.length()==0 || inputFileObject.equals("")) 
			{
				if(prp.getOutputfordebug())
					System.out.println("\t\t\tinputFileObject ("+inputFileObject+") == null");
				firstPart = POScontainsAllTerms(inputFileSubject,v_extractedSubject,prp);	//for doubles, we want to check extracted subject with inputfile subject only, checking with object bring many false positives
				secondPart = (POScontainsAllTerms(inputFileVerb,v_extractedRelation,prp));
				thirdPart = true;
			}
			//Matching can be diagonal..meaning inputfile subject could match in extracted subject or extracted object. Same for inputfile object, could be matched in extracted object or subject
			//Note: if triple to match is "the" "is" "consensus"
			//something we dont want to happen is a term, "the, or any other term" to be matched in both extracted subject and extracted object, but it should have been matched in only one of them 
			else {
				if(prp.getOutputfordebug())
					System.out.println("\t\t\tinputFileObject ("+inputFileObject+") == not null");
				boolean inputFileSubjectMatchedInExtractedSubject = POScontainsAllTerms(inputFileSubject,v_extractedSubject,prp);
				boolean inputFileObjectMatchedInExtractedSubject  = POScontainsAllTerms(inputFileObject,v_extractedSubject,prp); 
				firstPart = ( inputFileSubjectMatchedInExtractedSubject || inputFileObjectMatchedInExtractedSubject );
				
				secondPart = (POScontainsAllTerms(inputFileVerb,v_extractedRelation,prp));
				
				boolean inputFileSubjectMatchedInExtractedObject  = POScontainsAllTerms(inputFileSubject,v_extractedObject,prp);
				boolean inputFileObjectMatchedInExtractedObject  = 	POScontainsAllTerms(inputFileObject,v_extractedObject,prp);
				thirdPart = ( inputFileSubjectMatchedInExtractedObject ||  inputFileObjectMatchedInExtractedObject);
				
				//both subject and object should have been matched
				inputFileSubjectMatched = (inputFileSubjectMatchedInExtractedSubject || inputFileSubjectMatchedInExtractedObject);
				inputFileObjectMatched	= (inputFileObjectMatchedInExtractedSubject  || inputFileObjectMatchedInExtractedObject);
				
				//quick and dirty way to assign that the inputFileSubjectMatched and  inputFileObjectMatched have been matched
				if(inputFileSubjectMatched && inputFileObjectMatched)	{}
				else {
					if(prp.getOutputfordebug())
						System.out.println("\t\t\tinputFileSubjectMatched && inputFileObjectMatched not matched"); 
					thirdPart =false;
				}
			}
			
			
			//There is a problem with the above approach as label consensus, represented as a double (consensus, is) will match verb and object
			//consensus	is		The PEP author is responsible for building consensus within the community and documenting dissenting opinions .
			//"The PEP author"	"is"	"responsible for building consensus within the community"
			//"The PEP author"	"is"	"responsible for documenting dissenting opinions"
			//"The PEP author"	"is"	"responsible"
			//but the above is not an instance of consensus, so for doubles we dont match subject or object for first pos
			//SO we check the first part again in below block and set it to false if ..we dont match inputfile subject with extracted object
			//this will eliminate lots of false positives for this label consensus, consensus, is
			//and allow at teh same time to match instances like "Consensus is that we should adopt C3 ."
			if(prp.getOutputfordebug())
				System.out.println("\tfirstPart " + firstPart + " secondPart " + secondPart + " thirdPart " +thirdPart);
			//if double, set third part to true, as they wont need to be matched
			//this may wel;l have to be extended to check when both objects are null, the one frm inputfile and the one extracted
			/*
			if (inputFileObject == null || inputFileObject =="" || inputFileObject.isEmpty()){
				if(POScontainsAllTerms(inputFileSubject,v_extractedSubject))
						thirdPart = true;				
//				System.out.println("\tThird part set to matched ");
			}
			else{
			//	System.out.println("Third part was already true");
			}
			*/
			//System.out.println("2 firstPart " + firstPart + " secondPart " + secondPart + " thirdPart " +thirdPart);
			//NER check if PERSON exists in POS, if inputfile subject or object contains '*'
			/* Jan 2091, maybe not needed now so we can speed up processing
			 * String entityType= "PERSON";
			Boolean entityFoundInSubject=false, entityFoundInObject=false;
			if(subjectContainsAsterisk){	//meaning we allow for any entity in object, like [barry] [accepted] [pep] 
				// prp.getSCNLP().dos.writeUTF("N"+v_extractedSubject);
				// String nerFromPOS = prp.getSCNLP().dis.readUTF();				
				prp.getStanfordNLP().extractEntities(v_extractedSubject);	//currently nothing gets returned
				String nerFromPOS="";				
				entityFoundInSubject=checkForEntityType(nerFromPOS,entityType);
				if(entityFoundInSubject){			System.out.println("Entity Found In Subject");			firstPart = true;		}
				else		firstPart = false;		
	//								System.out.println(" coReferencedSentence sent: [" + strToSend + "] Received [" +coReferencedSentence +"]");
				//if NER returned part contains PERSON
				//if(coReferencedSentence.contains(PreviousSentenceString))
			}
			
			//not sure why we doing this for object
			
			if(objectContainsAsterisk){ //meaning we allow for any entity in object, like [pep] [was accepted by] [barry] 
				//prp.getSCNLP().dos.writeUTF("N"+v_extractedObject);
				//String nerFromPOS = prp.getSCNLP().dis.readUTF();
				prp.getStanfordNLP().extractEntities(v_extractedObject);	//currently nothing gets returned
				String nerFromPOS="";				
				entityFoundInObject=checkForEntityType(nerFromPOS,entityType);				
				if(entityFoundInObject){		System.out.println("Entity Found In Object");					thirdPart = true;				}
				else		thirdPart = false;		
	//								System.out.println(" coReferencedSentence sent: [" + strToSend + "] Received [" +coReferencedSentence +"]");
				//if NER returned part contains PERSON
				//if(coReferencedSentence.contains(PreviousSentenceString))
			}
			*/
			
			//MATCH ANY SUBJECT OR OBJECT
			/* Too many flase positives therefore commented
			if (inputFileSubject.equals("*")){
				firstPart = true;				
				System.out.println("\tFirst part set to matched because of any matcher *");
			}
			if (inputFileObject.equals("*")){
				thirdPart = true;				
				System.out.println("\tThird part set to matched because of any matcher *");
			}
			*/
			
			boolean tripleMatched = false;	//FOR Each Triple check if the S,V,O triples extracted from the IE library contains the triples from input file
			boolean foundNegationOnceInAnyExtractedPOS = false;
			
			if(firstPart && secondPart && thirdPart ){	//if current triple matched //some code cut from this if's else part put in a txt file in home pc desktop
				tripleMatched =true;			
//				System.out.println("Checking MATCHED Triple/Double WITH THOSE FROM INPUTFILE");
				if(prp.getOutputfordebug())
					System.out.println("\tMatched Triple in Sentence, idea: "+v_idea+", inputFileSubject: ("+ inputFileSubject.toLowerCase() + ") inputFileVerb: (" + inputFileVerb + ") inputFileObject: (" + inputFileObject + ")");
				
				Boolean checkFurther = true;
				/*for(String d: dontCheckLabels){
					if (v_idea.equals(d))
						checkFurther = false;	System.out.println("\tDont check labels = true idea: "+ v_idea + " current label: " + d);
				} */
//				System.out.println("CheckFurther: "+ checkFurther);
				
				//here we check neagation in matched triple, subject and relation of matched triple should be checjked for negation
				//CHECKING NEGATION IN SUBJECT
				boolean negationInSubjectBeforeSubjectTerm = false,checkIfNegationAnywhereInRelation = false, ifConditional = false;	
				if(!dontCheckThisLabelForNegation) { //we dont check negation is false
					negationInSubjectBeforeSubjectTerm=checkIfNegationInSubjectBeforeSubjectTerm(v_negationTerms, inputFileSubject,  v_extractedSubject.toLowerCase(),prp); 
					if(prp.getOutputfordebug())
						System.out.println("\t\tChecking Negation InSubject Before SubjectTerm: ("+v_extractedSubject+") "+ negationInSubjectBeforeSubjectTerm);
					if(prp.getOutputfordebug())
						System.out.println("\t\tChecked Negation InSubject Before SubjectTerm: ("+v_extractedSubject+") "+ negationInSubjectBeforeSubjectTerm);
					//CHECKING NEGATION IN RELATION
					//if the triples have been matched, check if current matched triple is negation, negation checking looks simple, just check if its exists in inputFileVerb
					//String [] negationStrings = {"not"}; - now expanded to include list of negation terms
					//System.out.println("\t\tChecking negation in extractedRelation (" + v_extractedRelation+")");				
					//Note not all negation would be caught here as some exist in another triples which may not match for triples so to do that process it in the else part right nelow	
					//13Oct2017, we extend to include negation checking in subject and object 
					 
					checkIfNegationAnywhereInRelation = (checkIfNegationAnywhereInRelation(v_negationTerms,inputFileSubject.toLowerCase(),v_extractedSubject.toLowerCase(),
																							inputFileVerb.toLowerCase(),v_extractedRelation.toLowerCase(),prp)
															&& checkFurther);	
					if(prp.getOutputfordebug())
						System.out.println("\t\tChecked If Negation Anywhere In Relation (" + v_extractedRelation+"): "+ checkIfNegationAnywhereInRelation);
									
					//catch conditional in object as first term, maybe code it to check anywherein object later
					//"the PEP"	"is accepted"	"once it 's finalized"
					//"the PEP"	"is accepted"	"once it 's ready for a decision"
					//"the PEP"	"is rejected"	"once it 's finalized"
					//"the PEP"	"is rejected"	"once it 's ready for a decision"
					
					boolean negationInObjectAsFirstTerm = false, negationInObjectBeforeObjectTerm = false;
					if(inputFileObject == null || inputFileObject.equals("") || inputFileObject.isEmpty() || inputFileSubject.length()==0) {
						if(prp.getOutputfordebug())
							System.out.println("\t\tInputFileObject is empty");
					}  //if inputfile object is empty we dont check object
					else { //System.out.println("\t\tInputFileObject is not empty");
						if(prp.getOutputfordebug())
							System.out.println("\t\tChecking Negation InObject As First Term: ("+v_extractedObject+") "+ negationInObjectAsFirstTerm);
						negationInObjectAsFirstTerm=checkIfNegationInObjectAsFirstTerm(v_negationTerms, v_extractedObject.toLowerCase(), prp);	
						if(prp.getOutputfordebug())
							System.out.println("\t\tChecked Negation In Object As First Term: ("+v_extractedObject+") = "+ negationInObjectAsFirstTerm);
					
						//shoukd extend to anywhere in object
						//once guido goes to town he accept pep.
						//"he"	"accept"	"pep once guido goes to town"
						//"he"	"accept"	"pep"
						//jan 2019, for cases like this
						//"The PEP author is responsible for building consensus within the community and documenting dissenting opinions."
						//"The PEP author"	"is"	"responsible for building consensus within the community"
						//"The PEP author"	"is"	"responsible for documenting dissenting opinions"
						//"The PEP author"	"is"	"responsible"
					
						if(prp.getOutputfordebug())
							System.out.println("\t\tChecking Negation InObject Before ObjectTerm: ("+v_extractedObject+") "+ negationInObjectBeforeObjectTerm);
						negationInObjectBeforeObjectTerm=checkIfNegationInObjectBeforeObjectTerm(v_negationTerms, inputFileObject,  v_extractedObject.toLowerCase(), prp);
						if(prp.getOutputfordebug())
							System.out.println("\t\tChecked Negation InObject Before ObjectTerm: ("+v_extractedObject+") = "+ negationInObjectBeforeObjectTerm);				
					}
					//Assigning negation in subject or relation
					if(negationInSubjectBeforeSubjectTerm || checkIfNegationAnywhereInRelation || negationInObjectAsFirstTerm || negationInObjectBeforeObjectTerm){
					//currently commented as what will happen to rsults if we check for negation in all POS
							foundNegationOnceInAnyExtractedPOS = true;
							//tripleMatchedOnce=false;   //just for output in the end which depends on this
							if(prp.getOutputfordebug())
								System.out.println("\t\t\tFound negation in Either: negationInSubjectBeforeSubjectTerm || checkIfNegationAnywhereInRelation || negationInObjectAsFirstTerm");	
							result.setNegation(true, libraryToCheck);
							
					} 
				
	/*				//CHECKING CONDITIONAL
					//if the triples have been matched, check if current matched triple is negation (checked above) and check if the triples have conditional
					//remember the conditional can be part of another triple which will be matched in subsequesnt loops
					
					//Good Example below
					// second set of triples matched, but 3rd set of triples have conditional so go through all the original extracted triples for sentence
					// if you find conditional in any S,V,O of triple, then check reminder
					// if found then set the entire sentence to conditional (for the particular library) error in current - it ony checks the current extracted triple and the one which has it gets avoided
					
					// Propositions     : ("This", "is", "the community 's one chance")
	                //1. ("the community", "has", "one chance")
	                //2.  ("this PEP", "is approved", "with a clear majority")
	                //3.  ("it", "will be implemented", "in Python 2.4 if this PEP is approved with a clear majority")
	*/				
					//System.out.println("\tChecking conditional in S, R and O");									
					
					ifConditional = cc.checkConditionals(v_tripleArray,cm, rvr, jw, entireParagraph, currentSentence,previousSentence, inputFileSubject, inputFileObject, inputFileVerb,v_idea, v_pepNumber, v_date, v_message_ID,author,  
							sentenceCounter, writerForDisco, writerAll, conditionalList, v_negationTerms, why, reasonLabels,libraryToCheck, prp );
					if (ifConditional ==true){
						if(prp.getOutputfordebug())
							System.out.println("\n"  + library + " Triples Matched but conditional, " + v_idea + " | " + inputFileSubject + " | " + inputFileVerb + " | " + inputFileObject + " | " + v_extractedSubject + " | " + v_extractedRelation + " | " + v_extractedObject);							
						result.setConditional(true,libraryToCheck);
					}
					else{ 
						//System.out.println("\t\tCould not Find conditional in sentence ");
						
						//Now check for conditional in dependency tree
						Boolean foundDependencyConditional = false;	
	//					currentSentence = ss.simplifySentence(currentSentence);
						
						//set conditional to true, if dependency found a conditional
						if(foundDependencyConditional)
							ifConditional=true;
					}	
				}
				//have to accomodate instances when the conditional in other triples
				if(prp.getOutputfordebug())
					System.out.println("\t\t\t--foundInPrevious  : " + foundInPrevious + "  foundNegationOnceInAnyExtractedPOS: " + foundNegationOnceInAnyExtractedPOS + "  ifConditional    : " + ifConditional +
									   "  checkFurther     : " + checkFurther);	
				if (foundInPrevious==false &&      //if not matched previously -- (many triples can be retrieved and matched from one sentence)
						foundNegationOnceInAnyExtractedPOS==false && (ifConditional==false) && checkFurther) 
				{
					//System.out.println("\t\t---INSIDE foundInPrevious==false &&   foundNegationOnce==false && (ifConditional==false)-----------");					
					foundInPrevious = true;     //set this flag to true for later loops to know
					tripleMatchedOnce = true;					
					foundNow = true;					
					result.setTripleMatched(v_idea, libraryToCheck);  	//depending on library, set the library matched to true	
					//System.out.println("\t CIE " + result.getClausIE());
					result.setFinalLabel(v_idea);
					result.setLabelSubject(inputFileSubject);					result.setLabelRelation(inputFileVerb);					result.setLabelObject(inputFileObject);
					if(prp.getOutputfordebug())
						System.out.println("\t\t\t Label matched: "+result.getClausIE()); //+", Now Going to Check for Reasons in matched triple"); 
					
					//FINDING REASONS HERE-- but do we need to check reaons in all different ways for each triple? only check roles and reasons for specified labels, not all
					if(prp.getCheckReasons()) {
	     				for (String r: v_statesToGetRolesAndReasons){
	     					//LOOK FOR TERM AND IF FOUND, check for reasons
	     					if (v_idea.contains(r)){
	     						if(prp.getOutputfordebug())
	     							System.out.println("\t\t\tChecking Reasons in Matched Triple, only for specific label " + v_idea);	
								foundReasonOnce = cgr.checkAndGetReasons_for_sentenceTriple_whereTriplesMatched(result, v_message, v_tripleArray,cm, rvr, jw, entireParagraph, 
										currentSentence,previousSentence, nextSentence,previousParagraph, nextParagraph, v_extractedSubject, 
										v_extractedObject, v_extractedRelation,v_idea, v_pepNumber, v_date, v_message_ID,author,  sentenceCounter, writerForDisco, 
										writerAll, conditionalList, v_negationTerms,why, reasonLabels, v_reasons, libraryToCheck , prp);
	     					}     					
	     				}
						//check all code below
						if (foundReasonOnce){
							if(prp.getOutputfordebug())
								System.out.println("\t\t\t########## FOUND REASON TRIPLES IN MATCHED TREIPLE OUTPUTTING()"  + result.getReasonObject() + " " + libraryToCheck);
							//result.setReason(foundReasonOnce.getReason(), libraryToCheck);
						}
						else{
	//							System.out.println("not conditional");
						}			
						
						//if not conditional, try find storyline
						TemporalReasoningFromText gs = new TemporalReasoningFromText();
					}
					//check if storyline - uncomment this for it to work - some errors	System.out.println("\tTemporal Reasoning"); //					
					//we call to get storyline - this occurs only if triple has been matched, so we pass the inputFileSubject, inputFileObject and inputFileVerb
//					String storyLine = gs.getstoryLineTerms(v_tripleArray,cm, rvr, jw, currentSentence,inputFileSubject, inputFileObject, inputFileVerb,v_idea, v_pepNumber,
//							v_date, v_message_ID,author,  sentenceCounter,writerForDisco,writerAll, conditionalList, v_negationTerms, why, reasonLabels,
//							libraryToCheck,  storyLineTerms, storyLineElements);
					
					
				} //end if found block
				
		
			} 
		    // sometimes conditional terms, if, etc, exists in other extracted clauses - which are not matched with triples
			// for each triple (S,V,O) extracted, we feed that in the function again
			// example sentence - This is the community 's one chance : if this PEP is approved with a clear majority it will be implemented in Python 2.4 .
			// Propositions     : ("This", "is", "the community 's one chance")
            //  ("the community", "has", "one chance")
            //  ("this PEP", "is approved", "with a clear majority")
            //  ("it", "will be implemented", "in Python 2.4 if this PEP is approved with a clear majority")			
			// conditional is matched out of the matched triple			
			// we assumed before that conditional terms only exists in extracted triple clauses

		    // so we need to look for these terms in all extracted triples and then check the following text	after the conditional term
		    // to see if it matches the triple, if so, then we can say that its conditional also.
			
			//WE MAY HAVE TO CHECK FOR REASON TERMS IN UNMATCHED TRIPLES, AS A TRIPLE MAY BE MATCHED LATER
			else {
				if(prp.getOutputfordebug())
					System.out.println("\tUnmatched Triple/Double, Checking for conditional, reasons (have to check reasons in all triples of sentence - those unmatched as well)");
//				System.out.println("\tChecking Triple which is UNMatched (), inputFileSubject: "+ inputFileSubject.toLowerCase() + " inputFileVerb: " + inputFileVerb + " inputFileObject:" + inputFileObject);
				Boolean ifConditional = false;				
				
				//calculate time
//				Instant conditionalStart = Instant.now();
				ifConditional = cc.checkConditionals(v_tripleArray,cm, rvr, jw, entireParagraph, currentSentence, previousSentence, inputFileSubject, inputFileObject, inputFileVerb,v_idea, v_pepNumber, v_date, v_message_ID,author,  
						sentenceCounter, writerForDisco, writerAll, conditionalList, v_negationTerms, why, reasonLabels,libraryToCheck, prp);
				//calculate conditional time and show
//				Instant conditionalEnd = Instant.now();
//				Duration timeElapsed = Duration.between(conditionalStart, conditionalEnd);
//				System.out.println("Conditional Time taken: "+ timeElapsed.toMillis() +" milliseconds");
				
					if (ifConditional ==true){
						//System.out.println("\tv_conditional ==true");
//						System.out.println("\t " + library + "  Triples Matched but conditional");
						if(prp.getOutputfordebug())
							System.out.println("\n\t\t"  + library + " Triples Matched but conditional, " + v_idea + " | " + inputFileSubject + " | " + inputFileVerb + " | " + inputFileObject + " | " + v_extractedSubject + " | " + v_extractedRelation + " | " + v_extractedObject);
//						writerAll.write("|o" + library.charAt(0) + ":  " + "conditional");	
//						writer5.write("|o" + library.charAt(0) + ":  " + "conditional");	
						
						result.setConditional(true, libraryToCheck);
						
						//if conditional, we here below we want to set the triple matched to false, for that library
						result.setTripleMatched(null, libraryToCheck);
					}
					
					//Jan 2019, its important we set these, although triple is unmatched as we want them to show in db for results evaluation
					result.setFinalLabel(v_idea);	result.setLabelSubject(inputFileSubject);		result.setLabelRelation(inputFileVerb);		result.setLabelObject(inputFileObject);
					
					//REASON CHECKING									
					//what are we checking? 					//triples or whole sentence?					
					//Checks previous, next sentences also and the entire paragraph also
//     				System.out.println("\tIn Else section, Going to checkAndGetReasons()");					
					//just send the inputFileSubject, inputFileVerb, and inputFileObject matched in this look and look for reason because the SVO has just been matched
					//also put an similar block of code in else section for instances where the reason is in other triple which is not matched`
					
     				//only check for reasons we flag is set...only check roles and reasons for specified labels, not all
					// Jan 2019, we dont do the reason part for now
					if(prp.getCheckReasons()) {
	     				for (String r: v_statesToGetRolesAndReasons){
	     					if (v_idea.contains(r)){
	     						if(prp.getOutputfordebug())
	     							System.out.println("\t\t\tChecking reasons in unmatched triple, only for specific label " + v_idea);	
		     					foundReasonOnce = cgr.checkAndGetReasons_for_sentenceTriple_whereTriplesMatched(result, v_message, v_tripleArray,cm, rvr, jw, entireParagraph, currentSentence,previousSentence, nextSentence, previousParagraph, nextParagraph, 
									v_extractedSubject, 	v_extractedObject, v_extractedRelation,v_idea, v_pepNumber, v_date, v_message_ID,author,  sentenceCounter, writerForDisco, 
									writerAll, conditionalList, v_negationTerms, why, reasonLabels, v_reasons, libraryToCheck, prp);
	     					}
	     				}
						//check all code below
						if (foundReasonOnce ==true){
							if(prp.getOutputfordebug())
								System.out.println("\t\t\t******* FOUND REASON IN UNMATCHED TRIPLE OUTPUTTING()" + result.getReasonObject() + " " + libraryToCheck);
							//result.setReason(foundReasonOnce.getReason(), libraryToCheck);
						}
					}
					
			}  //END ELSE PART 
			
	  } //end for loop - for all triples
		
	  //we can check for reaons here 
	  //only if a triple was matched in sentence, not for all triples as they may be unmatched triples  
	  if (tripleMatchedOnce){
		  if(prp.getOutputfordebug())
			  System.out.println("\tTriple Matched Atleast Once in all Extracted Triples");
		  if(prp.getCheckReasons()) {
			  for (String r: v_statesToGetRolesAndReasons){
				if (v_idea.contains(r)){	//only check roles and reasons for specified labels, not all
					if(prp.getOutputfordebug())
						System.out.println("\t\t Reason term found for label: " + v_idea + " now checking reason terms and triples in nearby sentences and paragraphs");
					foundReasonOnce = cgr.checkAndGetReasons_for_sentence_whereTriplesMatched(result, v_message, v_tripleArray,cm, rvr, jw, entireParagraph, currentSentence,previousSentence, nextSentence, previousParagraph, nextParagraph, 
					v_extractedSubject, 	v_extractedObject, v_extractedRelation,v_idea, v_pepNumber, v_date, v_message_ID,author, paragraphCounter,  sentenceCounter, writerForDisco, 
					writerAll, conditionalList, v_negationTerms, why, reasonLabels, v_reasons, libraryToCheck, prp);
				}
			  }	
		  }
	  }
		
		
	//check message author role and add if they are these roles
	//jan 2019 commented
	//computeRoleOfMessageAuthor(result, v_idea, v_MessageAuthorsRole, v_statesToGetRolesAndReasons);	
	//add reason if exists
	if (foundReasonOnce==true){			
		//comment this for the time been
		//v_idea = v_idea + "_BECAUSE_" + foundReasonOnce.getReason();
//			System.out.println("\t\t\t\t-----------------"	+ "_BECAUSE_"  + v_idea);
		//auguwt 2017 dont need this as reaons is now part of tpr
		//result.setFinalReason(foundReasonOnce.getReason());
		//result.setFinalLabel(v_idea);
	}
	
		//Now assign the v_idea to the appropriate library result, add more details to the label, only if label matched
		assignResultToRightLibraryInResult(result, v_idea, libraryToCheck,prp);
					
		return result;
	}
    //Check if there is Negation term before double /triple terms in same clause for a provided pos (can be object, verb or subject)
    																						//v_extractedObject,inputFileSubject,inputFileVerb,inputFileObject,v_negationTerms
    public static boolean checkClauseContainsAllSVO_IfNegationJustPrecedesSVOTermsInSameClause(String pos,String s, String v, String o,String [] v_negationTerms,
    		ProcessingRequiredParameters prp){
		//System.out.println("\t\tclauseIEContainsAll() pos ("+ pos+ ") s " +s +" v "+ v+ " o "+o );
		String[] posStr =null,vStr=null,sStr=null,oStr=null;		
		boolean negationFound=false;
		List<String> toCheckList=  new ArrayList<String>();		
		//deal != ideal therefore we use equals function below
		if (pos == "null" || pos == null || pos.length() == 0)
			return false; // no pos = no negation found
		else{
			if (pos.contains(" "))				posStr = pos.toLowerCase().split(" ");	//store all terms in array		
			else return false; //pos may not have space, so therefore just one logical term and in this case return false
			if (s.contains(" ")){
				sStr = s.toLowerCase().split(" ");	
				Collections.addAll(toCheckList, sStr);	// Add all elements to the ArrayList from an array.
			}
			else	toCheckList.add(s);	//sentence may not have space, so therefore just one logical term and in this case return false
			if (v.contains(" ")){
				vStr = v.toLowerCase().split(" ");	
				Collections.addAll(toCheckList, vStr);
			}
			else	toCheckList.add(v);	//sentence may not have space, so therefore just one logical term and in this case return false
				 //System.out.println("sStr "+ sStr + " vStr" +vStr);
		}
		//if object is empty, we check only subject and verb appended in toCheckList till now
		if (o.isEmpty() || o.equals("")|| o == null || o.length() == 0){
			if(prp.getOutputfordebug()) 
				System.out.println("\t\t\tinputfile object = empty ("+o + ") So checking If Negation Precedes only Subjecta dn Verb in POS ");
			negationFound=checkIfNegationPrecedesSVOTerms(pos, v_negationTerms, posStr, toCheckList,prp);	
		}
		else { //else we check subject, verb and object
			if (o.contains(" ")){	//split object
				oStr = o.toLowerCase().split(" ");	
				Collections.addAll(toCheckList, oStr);
			}
			else 
				toCheckList.add(o);	//sentence may not have space, so therefore just one logical term and in this case return false				
			if(prp.getOutputfordebug())
			System.out.println("\t\tChecking If Negation Precedes SVO Terms in POS Object ("+o+")");	//first check..check if posStr contains the s,v
			negationFound=checkIfNegationPrecedesSVOTerms(pos, v_negationTerms, posStr, toCheckList,prp);		
		}
		return negationFound;
	}
    //called from above function
    //If negation preceded any of the S,V,O terms...any of which can be passed as parameter for checking
	private static boolean checkIfNegationPrecedesSVOTerms(String pos, String[] v_negationTerms, String[] posStr,List<String> toCheckList,
			ProcessingRequiredParameters prp) {
		if( Arrays.asList(posStr).containsAll(toCheckList)) {
			//now check if negation precedes the pos
			for (String ns : v_negationTerms) {
				if(Arrays.asList(posStr).contains(ns)) {		//if pos string contains negation
//					System.out.println("Negation term found in posStr: "+ ns);
					//check text after negation to see if it contains all terms of double triple
					String textAfterNegation = pos.split(ns)[1].trim();	//extract text after negation
//					System.out.println("textAfterNegation: " + textAfterNegation);
					if(textAfterNegation.contains(" ")){	//make sure not null
						String termsAfterNegation[] = textAfterNegation.split(" ");
//						System.out.println("termsAfterNegation " + termsAfterNegation[0] + " " +termsAfterNegation[1]);
						
							if(Arrays.asList(termsAfterNegation).containsAll(toCheckList)){	//text after negation contains all terms of double triple
								if(prp.getOutputfordebug())
									System.out.println("\t\t\tNegation term found ("+ns+") before triple terms in same clause: " + pos);
								return true;	//return true if all double or triple terms found after negation term
							}
					}		
				}
			}
		}
		return false;
	}
    
    //we also cater for the case when the inputfile subject also has a negation, in that case we ignore
    public static Boolean checkIfNegationInSubjectBeforeSubjectTerm(String [] v_negationTerms, String inputFileSubject, String v_extractedSubjectTerms, 
    		ProcessingRequiredParameters prp) {
    	boolean foundNegationInSubject = false;
    	try 
    	{
	    	//jan 2019, we remove everything after inputFileSubject in v_extractedSubjectTerms and check that remaining text for negation
	    	if(inputFileSubject.contains(" ")) {}	//sometimes the inputFileSubject contains multiple terms and that will cause null pointer exception	
	    	else {	//get all text before the object term, and check that for negation
	    		if(prp.getMessageID()==56104)
	    		if(inputFileSubject.contains(inputFileSubject)) {
	    			v_extractedSubjectTerms= inputFileSubject.substring(0, v_extractedSubjectTerms.indexOf(inputFileSubject));
	    			System.out.println("\t\t\t v_extractedSubjectTerms ("+v_extractedSubjectTerms + ") inputFileSubject : " + inputFileSubject);
	    		}
	    	}
	    	//ERROR IN ABIVE LINE, i DONT KNOW WHY
	    	String[] extractedSubjectTerms = v_extractedSubjectTerms.split(" ");		String[] inputFileSubTerms = inputFileSubject.split(" ");
			//System.out.println("\t\t\tChecking Negation Terms: "); 
			for (String n : v_negationTerms) {	//System.out.print(n + " ");
				if (ArrayUtils.contains(extractedSubjectTerms, n) ) {
					if(prp.getOutputfordebug())
						System.out.println("\t\t\tNegation ("+n + ") found in subject : " + v_extractedSubjectTerms);
	//				if (ArrayUtils.contains(extractedSubjectTerms, n) ) 
	//					System.out.println("\t\t\tNegation also found in inputfile subject : " + inputFileSubject);				
	//				else {
					if(prp.getOutputfordebug())
						System.out.println("\t\t\tNegation only ("+n + ") found in subject : (" + v_extractedSubjectTerms + ")");
	//					return true;
	//				}
				}
			}
    	}
    	catch(Exception ex) {
    		ex.printStackTrace();
    		System.out.println(StackTraceToString(ex));
			  System.out.println("Error checkIfNegationInSubjectBeforeSubjectTerm ");
    	}
	    return false;	//if no negation found
    }
    
    public static String StackTraceToString(Exception ex) {
		String result = ex.toString() + "\n";
		StackTraceElement[] trace = ex.getStackTrace();
		for (int i=0;i<trace.length;i++) {
			result += trace[i].toString() + "\n";
		}
		return result;
	}
    
    //same function for object
    //we also cater for the case when the inputfile subject also has a negation, in that case we ignore
    public static Boolean checkIfNegationInObjectBeforeObjectTerm(String [] v_negationTerms, String inputFileObject, String v_extractedObjectTerms,
    		ProcessingRequiredParameters prp) {
    	try {
	    	//List<String> termsIndex = Arrays.asList(v_extractedObjectTerms.split(" ")); //v_extractedObjectTerms.split(" ");
	    	boolean foundNegationInObject = false;
	    	//jan 2019, we remove everything after inputFileObject in v_extractedObjectTerms and check that remaining text for negation
	    	if(inputFileObject.contains(" ")) {}	//sometimes the inputFileObject contains multiple terms and that will cause null pointer exception	
	    	else	//get all text before the object term, and check that for negation
	    		v_extractedObjectTerms= v_extractedObjectTerms.substring(0, v_extractedObjectTerms.indexOf(inputFileObject)); 
	//    	System.out.println("\t\t\tv_extractedObjectTerms: "+v_extractedObjectTerms);
	    	String[] extractedObjectTerms = v_extractedObjectTerms.split(" ");		String[] inputFileSubTerms = inputFileObject.split(" ");
	    	Integer len = extractedObjectTerms.length;	
	    	if(prp.getOutputfordebug())
	    		System.out.println("\t\t\tlen: "+len); 
	//		System.out.println("\t\t\tChecking Negation Terms: "); 
			for (String n : v_negationTerms) {	//System.out.print(n + " ");
				if (ArrayUtils.contains(extractedObjectTerms, n) ) {
					if(prp.getOutputfordebug())
						System.out.println("\t\t\tNegation ("+n + ") found in Object : " + v_extractedObjectTerms);
					//if (ArrayUtils.contains(extractedObjectTerms, n) ) 
					//	System.out.println("\t\t\tNegation also found in inputfile Object : " + inputFileObject);				
					//else {
					//	System.out.println("\t\t\tNegation only ("+n + ") found in Object : (" + v_extractedObjectTerms + ")");
						//return true;
					//}
					//jan 2019, we want to make sure the negation is just before, or two terms before object
					Integer j=0;
					for (String k: extractedObjectTerms) {
						if(k.equals(n)) {	//System.out.println("\t\t\tj: "+j); 
							if(len - j < 2)	return true;
						}
						j++;
					}
				}
			}
    	}
    	catch (Exception ex) 
    	{}
	    return false;	//if no negation found
    }
    
    //checkIfNegationInObjectAsFirstTerm
    public static Boolean checkIfNegationInObjectAsFirstTerm(String [] v_negationTerms, String v_extractedObjectTerms, ProcessingRequiredParameters prp) {
    	boolean foundNegationInSubject = false; String firstTerm= "";
    	if(v_extractedObjectTerms.contains(" ")) {
    		firstTerm = v_extractedObjectTerms.split(" ")[0];
    	}
    	else {
    		firstTerm = v_extractedObjectTerms;
    	}
    	
		//for (String s: terms) {
			for (String n : v_negationTerms) {
				if (firstTerm.equals(n) ) {
					if(prp.getOutputfordebug())
						System.out.println("\t\t\tNegation ("+n + ") found as first term in object : " + v_extractedObjectTerms);
					return true;
				}
			}
    	//}
    	
//    	else { //only one term check that term
//			for (String n : v_negationTerms) {
//				if (v_extractedObjectTerms.equals(n) ) {
//					System.out.println("\t\t\tNegation ("+n + ") found as first term in object : " + v_extractedObjectTerms);
//					return true;
//				}
//			}
//    	}
	    return false;	//if no negation found
    }
    
	//check for negation ANYWHERE in relation/verb ..dec 2017 
    //we also cater for the case when the inputfile verb/relation also has a negation, in that case we ignore
	public static Boolean checkIfNegationAnywhereInRelation(String [] v_negationTerms, String inputFileSubject, String v_subjectTerms,String inputFileRelation,  String v_relationTerms,
			ProcessingRequiredParameters prp){
		boolean foundNegationInSubject = false, foundNegationInRelation = false;
		String[] extractedSubjectTerms = v_subjectTerms.split(" "), extractedRelationTerms = v_relationTerms.split(" ");
		String[] inputFileSubTerms = inputFileSubject.split(" "), inputFileRelTerms = inputFileRelation.split(" ");
		for (String n : v_negationTerms) {
			if (ArrayUtils.contains(extractedRelationTerms, n)) {
				if(prp.getOutputfordebug())
					System.out.print("\t\t\tNegation ("+n + ") found in extracted relation : "+v_relationTerms);
				if (ArrayUtils.contains(inputFileRelTerms, n) ) {
					if(prp.getOutputfordebug())
						System.out.println("	Negation also found in inputfile relation : " + inputFileRelation);
				}
				else {
					if(prp.getOutputfordebug())
						System.out.println("	Negation only ("+n + ") found in extracted relation : (" + v_relationTerms + ")");
					return true;
				}
			}
		}		
	    return false;//if no negation found
	}

	private static Boolean checkForEntityType(String nerFromPOS,String entityType) {
		if(nerFromPOS==null || nerFromPOS.isEmpty())		{}
		else{
			String ar[];
			if(nerFromPOS.contains("|")){
				ar=nerFromPOS.split("|");
				String key="",val="";				String ar2[];
				for(String s: ar){
					ar2=s.split("!");			key=ar2[0];			val=ar2[1];
					if(key.contains(entityType))
						return true;					
				}
			}
			ar=null;
		}
		return false;
	}

	private static void assignResultToRightLibraryInResult(TripleProcessingResult result, String v_idea,Integer libraryToCheck, ProcessingRequiredParameters prp)
	{
		if (libraryToCheck == 1) { // (" +MessageAuthorsRole+")
			if (result.getReverb() != null && !result.getReverb().isEmpty()) 				
				result.setFinalIdea(v_idea, 1);
			else 
				result.setFinalIdea("", 1);			
			System.out.println("Final Reverb: "	+   result.getReverb() + " Reasons : "	+   result.getReason(1));
		} else if (libraryToCheck == 2) {
			// System.out.println("TTTTTT--------in clausie " + "_BECAUSE_"  + v_idea + "result.getClausIE() " + result.getClausIE());
			if (result.getClausIE() == null  || result.getClausIE().isEmpty()) 				
				result.setFinalIdea("", 2); 
			else 
				result.setFinalIdea(v_idea, 2);	
			if(prp.getOutputfordebug())
				System.out.println("CIE: "	+   result.getClausIE() + " Reasons : "	+  result.getReasonObject().getDistinctReasons()); // result.getReason(2));
		} else if (libraryToCheck == 3) { 
			if (result.getOllie() != null && !result.getOllie().isEmpty()) 				
				result.setFinalIdea(v_idea, 1);
			else 
				result.setFinalIdea("", 1);			
			System.out.println("O 3: "	+   result.getOllie() + " Reasons : "	+   result.getReason(3)) ;
		}
	}
	//dec 2018 dot knoiw why we doing this as we already computer message author role and stored in table which is read in and stored in prp class as variable
	//We can find out core-developers from the list online and also pep editors, and bdfl delegates from online pep metadata
	//core developers	https://hg.python.org/committers.txt
	//pep editors 		http://legacy.python.org/dev/peps/pep-0001/#pep-editors
	//bdfl delegates	https://github.com/python/peps
	/*
	private static void computeRoleOfMessageAuthor(TripleProcessingResult result, String v_idea,String v_MessageAuthorsRole, String[] v_statesToGetRoles) {
		if(v_MessageAuthorsRole.equals("bdfl") || v_MessageAuthorsRole.equals("author") || v_MessageAuthorsRole.equals("bdfl_delegate")){
			//check only for those states we want to capture
			for(String statesToGetRoles: v_statesToGetRoles){
				//see if label captured contains one of the states we want to capture so that we can get the role of the message author
				if (v_idea.toLowerCase().equals(statesToGetRoles)){
					System.out.println("\t\t\t\t--------------------MessageAuthorsRole.equals");
					//v_idea = v_idea + "_by_" + v_MessageAuthorsRole;
					result.setRole(v_MessageAuthorsRole);
				//	result.setFinalLabel(v_idea);
				}				
			}
		}
	}
	*/
	private static String assignLibrary(Integer libraryToCheck, String library) {
		if (libraryToCheck==1){
			library="Reverb";
		}  
		else  if (libraryToCheck==2){
			library="ClausIE";			
		}  
		else  if (libraryToCheck==3){
			library="Ollie";
		}
		return library;
	}
/*
	private static void notSureWhy(String subject, String object, String verb, String v_subject, String v_relation,
			String v_object) {
		if (POScontainsAllTerms(subject,v_subject.toLowerCase(),prp)  ||   POScontainsAllTerms(object,v_subject.toLowerCase().prp) )	{	
//					System.out.println("first matched");
			if(POScontainsAllTerms(subject,v_subject.toLowerCase(),prp) ){
//						System.out.println("first first matched");
			}
			if(POScontainsAllTerms(object,v_subject.toLowerCase())){
//						System.out.println("first second matched " + object + " v_subj " + v_subject.toLowerCase());
			}
		}
		if  (POScontainsAllTerms(verb,v_relation.toLowerCase(),prp) ) {
//					System.out.println("second matched");
		}
		if (POScontainsAllTerms(subject,v_object.toLowerCase())  ||   POScontainsAllTerms(object,v_object.toLowerCase(),prp) )		{	
//					System.out.println("third matched ");
			if(POScontainsAllTerms(subject,v_object.toLowerCase()) ){
//						System.out.println("third first matched");
			}
			if( POScontainsAllTerms(object,v_object.toLowerCase())){
//						System.out.println("third second matched " + object + " v_obj " + v_object.toLowerCase());
			}
		}
		
		
		// ar1 = processWord(extr.getArgument1().toString());
		// rel = processWord(extr.getRelation().toString());
		// ar2 = processWord(extr.getArgument2().toString());
		// System.out.println( ar1 + " , " + rel + " , " + ar2);
	}
*/

	//remove all punctuation
	public static String cleanPOS(String v_pos){
		//System.out.println("v_pos" + v_pos);
		if(v_pos==null || v_pos=="" || v_pos.isEmpty())
		{}
		else{			
			v_pos = v_pos.toLowerCase();	
			v_pos = v_pos.replaceAll("[^a-zA-Z0-9.',?!\\s]+"," ");
//			if(v_pos.contains("\"")){
//				v_pos.replaceAll("\"","");
//		    }
//			if(v_pos.contains("(")){
//				v_pos.replaceAll("(","");
//		    }
//			if(v_pos.contains(")")){
//				v_pos.replaceAll(")","");
//		    }
			v_pos = v_pos.trim();
		}
		return v_pos;
	}
	//remove all punctuation
    public static String cleanAndOutput(String v_pos, String pos, Integer posInt)
    {
    	String partOfSpeech = "";
    	// System.out.println("\t inside cleanandoutput " + v_pos + " v_pos");
		if (posInt == 1)
			partOfSpeech = "subject";
		else if (posInt == 2)
			partOfSpeech = "verb";
		else if (posInt == 3)
			partOfSpeech = "object";

		if (v_pos == null || v_pos.equals("")) {
			// System.out.println("\t " + partOfSpeech + " empty or null");
		}
		else {
			// string cleaning - very important to do this
			v_pos = cleanPOS(v_pos);

			// just output the following message - for debug purposes
			if (v_pos.toLowerCase().contains(pos.toLowerCase())) {
				// System.out.println("\t " + partOfSpeech + " matched");
			} else {
				// System.out.println("\t " + partOfSpeech + " not matched");
			}
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
		//System.out.println("\t inside cleanandoutput 2" + v_pos + " v_pos");
    	return v_pos;
    	
    }
	
	//reasons
	/*
	public static Reason computeForReasons(String sentence, String leftOverReason, String [] reasonLabels, Reason r, FileWriter writer) 
    		throws IOException{
     	 
  //  	ClausIE clausIE = new ClausIE();
  //      clausIE.initParser();
 //       clausIE.getOptions().print(System.out, "# ");        
        
    	//should be passed as parameter
    	String reasons[]         = {"poll results","voting results","vote results","consensus","favourable feedback","limited utility and difficulty of implementation "};
   	      	
	    clausIE.parse(sentence);
        clausIE.detectClauses();
        clausIE.generatePropositions();
        
        String v_reason = "";
        String subject = "";
		String verb = "";
		String object = 	"";
		
		 boolean foundReason = false;
		 String reasonMatched = null;
	  
		 // two ways to check for reasons...
		 // from predefined terms and triples
		 
		 //check reason in predefined list
		 for (String reasonA : reasons)
	       {
			 if(leftOverReason.contains(reasonA))
			 {
				 System.out.println("\tFound Reason in term: "	+ reasonA);
				 writer.write(" Reason: "+ reasonA);
				 foundReason = true;
			 }
	       }
		 
		 
		//check reasons in triples 
       for (String reason : reasonLabels)
       {
    	   String words[] = reason.split(" ");
//   		v_idea =  space+ words[0] + space;
//   		subject = space + words[1] + space;
//   		verb = space + words[2] +space;
//   		object = space + words[3] +space;
	   		//System.out.println("words.length: " + words.length);
	   		if(words.length==3 ){
	   			v_reason =  words[0] ;
	   			subject =   words[1];
	   			verb =  words[2];	   			
	   		}
	   		//capture object as well - form triples
	   		else if(words.length==4){
	   			v_reason =  words[0] ;
	   			subject =   words[1];
	   			verb =  words[2];
	   			object =  words[3];	   			
	   		}				
	   		//capture the second set of triple, if more columns are provided in the inputfile
	   		else if (words.length>5){
	   			v_reason =  words[0] ;
	   			subject =   words[1];
	   			verb =  words[2];	   			
	   		}
	
	   		System.out.println("subject "+ subject + " verb " + verb + " object " + object);
	   		
	        String v_subject = "";
	        String v_relation = "";
	        String v_object = "";        
	        
//pp	        System.out.println("Clauses          : ");
	        String sep = "";
	        for (Clause clause : clausIE.getClauses()) {
//pp	            System.out.println( clause.toString(clausIE.getOptions()));		//sep +
	            sep = "                   "; 
	        }
	        
	        // generate propositions
//pp	        System.out.println("Propositions     : ");
	        sep = "";
	        String[] partsOfPreposition = null;
	        for (Proposition prop : clausIE.getPropositions()) {
//pp	            System.out.println( prop.toString());							// sep +
	            sep = "                   ";
	            
	            //split the string
	            partsOfPreposition = prop.toString().split(",");
	            v_subject = partsOfPreposition[0];
	            v_relation = partsOfPreposition[1];
	            //sometimes the clauseIE just has two terms, subject and verb - no object. the following to prevent null pointer error
	            if (partsOfPreposition.length ==3) 
	            	v_object = partsOfPreposition[2];
	            else
	            	v_object = "";
	            //check conditional matches
	            if (v_subject.toLowerCase().contains(subject.toLowerCase()) || v_relation.toLowerCase().contains(verb.toLowerCase()) 
						|| v_object.toLowerCase().equals(object.toLowerCase())) {
					System.out.println("Reason");
					System.out.println("\tClausIE Triples Matched Found reason,"	+ v_subject + " , " + v_relation + " , " + v_object);
					foundReason = true;		
					reasonMatched = v_subject+" " +v_relation+" "+v_object;
				} 
				
	        }
	     
       } 
        
        r.SetValues(foundReason,reasonMatched);
	        
        return r;
	}
*/	
											//negation terms array       //pos to check       //just for outputting - never used       
	
	
	// clean the array
	public static String[][] cleanArray(String v_tripleArray[][]) {
		Integer y_counter = 0;
		String v_subject = null;
		String v_relation = null;
		String v_object = null;

		for (y_counter = 0; y_counter < 10; y_counter++) {
			// assign array contents
			v_subject = v_tripleArray[y_counter][0];
			v_relation = v_tripleArray[y_counter][1];
			v_object = v_tripleArray[y_counter][2];

			// clean
			v_subject = cleanPOS(v_subject);
			v_relation = cleanPOS(v_relation);
			v_object = cleanPOS(v_object);
			// assign back
			v_tripleArray[y_counter][0] = v_subject;
			v_tripleArray[y_counter][1] = v_relation;
			v_tripleArray[y_counter][2] = v_object;

		}
		return v_tripleArray;
	}
	
	
	public static void debugMessage(String conditional_v_subject, String conditional_v_relation, String conditional_v_object, String v_subject, String v_relation, String v_object, FileWriter writer5) throws IOException
	    {
	    	
//		 System.out.println("\ninside check for conditional - for loop a");
			//very important part
			if (!(conditional_v_subject=="") && conditional_v_subject != null)
			{
//				System.out.println("\ninside check for conditional - for loop a---4");
				//string cleaning - very important to do this	
				conditional_v_subject = conditional_v_subject.toLowerCase();
//				System.out.println("\ninside check for conditional - for loop a---44");
				if(conditional_v_subject.contains("\"")){
					conditional_v_subject.replace("\"","");
//					System.out.println("\ninside check for conditional - for loop a---41");
			    }
				if(conditional_v_subject.contains(")")){
					conditional_v_subject.replace(")","");
//					System.out.println("\ninside check for conditional - for loop a---42");
			    }
//				System.out.println("\ninside check for conditional - for loop a---5");
				//just output the following message - for debug purposes
				if (   conditional_v_subject.toLowerCase().contains(v_subject.toLowerCase())) {
//					System.out.println("\t subject matched");
//					System.out.println("\ninside check for conditional - for loop a---6");
				}
				else {
//					System.out.println("\t subject not matched");
//					System.out.println("\ninside check for conditional - for loop a---7");
				}
		    }
//			System.out.println("\ninside check for conditional - for loop b");
			if (!(conditional_v_relation=="")  && conditional_v_relation != null)
			{
				//string cleaning - very important to do this	
				conditional_v_relation = conditional_v_relation.toLowerCase();
				
				if(conditional_v_relation.contains("\"")){
					conditional_v_relation.replace("\"","");
			    }
				if(conditional_v_relation.contains(")")){
					conditional_v_relation.replace(")","");
			    }
				
				//just output the following message - for debug purposes
				if (!conditional_v_relation.isEmpty() &&  v_relation.toLowerCase().contains(v_relation.toLowerCase())) {
//					System.out.println("\t verb matched");
				}
				else {
//					System.out.println("\t verb not matched");
				}
			}
//			writer5.write("\ninside check for conditional - for loop c");
			if (!(conditional_v_object=="")  && conditional_v_object != null)
			{
				//string cleaning - very important to do this				
				conditional_v_object =conditional_v_object.toLowerCase();
				
				if(conditional_v_object.contains("\"")){
					conditional_v_object.replace("\"","");
			    }
				if(conditional_v_object.contains(")")){
					conditional_v_object.replace(")","");
			    }				
				
				//just output the following message - for debug purposes
				if (!conditional_v_object.isEmpty() &&   conditional_v_object.toLowerCase().contains(v_object.toLowerCase())) {
//					System.out.println("\t object matched");
				}
				else {
//					System.out.println("\t object not matched");
				}
			}
	    	
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
	public void finalize() {	    //System.out.println("Probability instance is getting destroyed"); 
	}
	
}
