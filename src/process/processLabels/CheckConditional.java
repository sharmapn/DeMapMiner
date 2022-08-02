package Process.processLabels;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import callIELibraries.JavaOllieWrapperGUIInDev;
import Process.processMessages.LabelTriples;
import Process.processMessages.ProcessingRequiredParameters;
import callIELibraries.ReVerbFindRelations;
import de.mpii.clause.driver.ClausIEMain;

public class CheckConditional {
	
	static boolean showCIEDebugOutput = false;
	
	//used to for passing multiple parameters
	private static class Conditional {
	    private boolean conditionalExists;
	    private String leftOverAfterCondition;
	    
	    public Conditional(){
	    	
	    }
	    public void SetValues(boolean v_conditionalExists, String v_leftOverAfterCondition) {
	        this.conditionalExists = v_conditionalExists;	        this.leftOverAfterCondition = v_leftOverAfterCondition;
	    }
	    public boolean getIfConditional() {	        return conditionalExists;	    }
	    public String getLeftOverAfterCondition() {	        return leftOverAfterCondition;	    }
	}
	
	
	//for extracted triple, check if any has any conditional terms
	//if it contains, get the leftover for processing to check if leftover contains the captured label by sending to cie again
	public static Conditional checkIfConditionalOnce(String[] v_conditionalStrings, String v_subject, String v_relation, String v_object, FileWriter writer5, ProcessingRequiredParameters prp) throws IOException
	{			
			String text = "", leftOver = "";
			boolean subjectFound = false, relationFound = false, objectFound = false;
			Conditional c = new Conditional();			
//			System.out.println("--------------checking conditional");			
			
			//if array is empty, return false	
			if (v_conditionalStrings == null || v_conditionalStrings.length == 0){
//				System.out.println("v_conditional array is null or empty");
				//return false(not conditional) and leftover = null
				c.SetValues(false, null);
				return c;
			}			
			
			for (String cs : v_conditionalStrings) 
			{
				// if extracted triple is all null, do not proceed
				if ((v_subject == null || v_subject.isEmpty() ) && ( v_relation == null || v_relation.isEmpty())  && (v_object == null || v_object.isEmpty() ))
					break;
				if (v_subject == ""   && v_relation == "" 	&& v_object == "")
					break;			
				
	//ppp		System.out.println("\tChecking conditional in S, R and O, conditional term: (" + cs + ") v_subject: ("+ v_subject + ") v_relation: (" +v_relation + ") v_object: (" +v_object + ")");
				//if v_subject contains cs
				if (checkTerms(cs, v_subject))
				{
				//if (v_subject.toLowerCase().contains(cs)) {
					subjectFound = true;
					text = v_subject;
	//				System.out.println("\tconditional in subject");
	//				writer5.write("\t conditional in subject");
					int spaceIndex = text.toLowerCase().indexOf(cs) + cs.length();
					if (spaceIndex != -1) { // -2 to handle these 2 characters:		
						try {
							leftOver = text.toLowerCase().substring(spaceIndex,	v_subject.length() - 2);
						}
						catch (Exception e){
							if(prp.getOutputfordebug())
								System.out.println("\t leftover error at Check conditionals 1 "+ e.toString());
						}
					}
					// leftOver = text.replace(v_subject,"");
	//				System.out.println("subjectFound, leftOver: " + leftOver);
				}
				if (checkTerms(cs, v_relation)) {
					relationFound = true;
					text = v_relation;
	//				System.out.println("\tconditional in relation");
	//				writer5.write("\t conditional in relation");
					// 				find the location of the where the conditional term ends in the pos, thats why i add the length of the term
					int spaceIndex = text.toLowerCase().indexOf(cs) + cs.length();
													
					//added this to handle out of bounds errors				
					//old code if(text.length() > 0 && spaceIndex != -1 && v_relation.length() > 2)
					
					                        //also have to take care of instances where the term is the last term in teh sentence
					if (spaceIndex != -1 && (spaceIndex != text.length()) ) { // -2 to handle these 2 characters:
											// "}
	//					System.out.println("\tcs "+ cs);
	//					System.out.println("\tv_relation "+ v_relation);
	//					System.out.println("\ttext.toLowerCase() "+ text.toLowerCase());
	//					System.out.println("\tspaceIndex "+ spaceIndex);
	//					System.out.println("\tcs.length() "+ cs.length());
	//					System.out.println("\tv_relation.length() "+ v_relation.length());
						try {
							leftOver = text.toLowerCase().substring(spaceIndex,	v_relation.length() - 2);
						}
						catch (Exception e){
							if(prp.getOutputfordebug())
								System.out.println("\t leftover error at Check conditionals 2 "+ e.toString());
						}
					}
	//				System.out.println("relationFound, leftOver: " + leftOver);
				}
				if (checkTerms(cs,v_object)) {
					objectFound = true;
					text = v_object;					
	//				System.out.println("\t conditional in object");	
					int spaceIndex = text.toLowerCase().indexOf(cs) + cs.length();
					if (spaceIndex != -1) { // -2 to handle these 2 characters:	
						try {
							leftOver = text.toLowerCase().substring(spaceIndex,	v_object.length() - 2);
						}
						catch (Exception e){
							if(prp.getOutputfordebug())
								System.out.println("\t leftover error at Check conditionals 2 "+ e.toString());
						}
					}
	//				System.out.println("objectFound, leftOver: " + leftOver);
				}
	
				// if contains, then subtract it from the next proposition
				// section = remaining
				if (subjectFound || relationFound || objectFound) {
					//set conditional to true with leftover
					c.SetValues(true, leftOver);
//					System.out.println("\t+++++++++conditional set to true");
					return c;
				}			
				
			} // end forloop conditional check
			
			return c;			
	}
	
	// note its not necessary that only the matched triple from the sentence would have the negation, 
	// other triples extracted from the sentence can have the conditional as well which will make the sentence conditional and the label captured conditional
	public static Boolean checkConditionals(String v_tripleArray[][], ClausIEMain cm, ReVerbFindRelations rvr, JavaOllieWrapperGUIInDev jw, String entireParagraph, String currentSentence,String previousSentence, String subject, String object, String verb, String v_idea, Integer v_pepNumber,
			Date v_date, Integer v_message_ID, String author, Integer sentenceCounter, 
			FileWriter writerForDisco, FileWriter writerAll, String conditionalStrings[], 
			String v_negationTerms[], String [] reasonIdentifierTerms, ArrayList<LabelTriples> reasonLabels, Integer libraryToCheck, ProcessingRequiredParameters prp) throws IOException
	{		
		Integer y_counter=0;
		String library = "", v_subject = "", v_relation = "", v_object = "", text = "", leftOver = "", conditionalLeftover = "";		
		boolean labelConditional = false, subjectFound = false, relationFound = false, objectFound = false,	v_conditional = false, foundConditionalOnce = false;
		Conditional a = new Conditional();			
		//System.out.println("\tInside checkConditionals() Fn");
		
		for (y_counter=0; y_counter<10;y_counter++)				///go through all extracted triples from sentence
		{
			//assign array contents
			v_subject  = v_tripleArray[y_counter][0];
			v_relation = v_tripleArray[y_counter][1];
			v_object   = v_tripleArray[y_counter][2];
			
			//pass all triples one by one to check for conditional terms like if, should be
			a = checkIfConditionalOnce(conditionalStrings, v_subject, v_relation,  v_object, writerForDisco , prp);
			foundConditionalOnce = a.getIfConditional();	//assign if conditional		
			conditionalLeftover = a.getLeftOverAfterCondition();		// assign the leftover if conditional
			
			//assuming there is onlyone conditional term in sentence
			//if more have to move this code into the loop
//								writer5.write("\n\tInside function - middle inside if - Checking foundConditionalOnce ==true");
//			System.out.println("\tChecking foundConditionalOnce ==true");
			if (foundConditionalOnce == true) 
			{
				//System.out.println("\tfoundConditionalOnce ==true");
				// go over all propositiona once again and match - use the remaining to extract relation again
				String[] v_partsOfPreposition = null;
				try {
	//xx				System.out.println("trying to compute the propositiona again");
										
					String[][] tripleArrayOfLecftover = null;					
					//System.out.println("\t now process the double array to see if the extracted clause after the conditional term is the same one identified");
				    if (libraryToCheck ==1)
				    {
				    	// extract the triples form te leftover (after conditional)
						try {
							tripleArrayOfLecftover = rvr.findRelations(conditionalLeftover, subject, object, verb,v_idea, v_pepNumber, v_date, v_message_ID,author,  
																				   sentenceCounter, writerForDisco, writerAll, conditionalStrings, reasonIdentifierTerms, reasonLabels, 1);
						} catch (Exception e) {
							e.printStackTrace();
						}
						//now process the double array to see if the extracted clause after the conditional term is the same one identified
						//v_conditional = computeForConditional(tripleArrayOfLecftover, conditional_v_subject, conditional_v_relation, conditional_v_object, 1, writer5);
						v_conditional = computeForConditional(tripleArrayOfLecftover, subject, verb, object, 1, writerForDisco);					    
				    }
					else if (libraryToCheck ==2){
//						tripleArrayOfLecftover = cm.computeClausIEForConditionalChecking(conditionalLeftover, showCIEDebugOutput);
//						v_conditional = computeForConditional(tripleArrayOfLecftover, subject, verb, object, 2,writerForDisco);
					}
					else if (libraryToCheck ==3)
					{
//						System.out.println("\tlibraryToCheck ==3");
						// v_conditional = jw.computOllie(CurrentSentenceString, subject, object, verb,v_idea, v_pepNumber, v_date, v_message_ID,author,  sentenceCounter, writer, writer2);
						// extract the triples form te leftover (after conditional)
						tripleArrayOfLecftover = jw.computOllie(conditionalLeftover, subject, object, verb,v_idea, v_pepNumber, v_date, v_message_ID,author,  
																			   sentenceCounter, writerForDisco, writerAll, conditionalStrings, reasonIdentifierTerms, reasonLabels, 3);
						//now process the double array to see if the xextracted clause after the conditional term is the same one identified
						v_conditional = computeForConditional(tripleArrayOfLecftover, subject, verb, object, 3,writerForDisco);	
					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				if (v_conditional ==true){
					System.out.println("\n " + library + " Triples Matched but conditional, " + v_idea + " | " + subject + " | " + verb + " | " + object + " | " + v_subject + " | " + v_relation + " | " + v_object);
					//SET RESULT, 
					//conditional = true and result = false					
					//r.setTripleMatched(false, libraryToCheck);					
					return true;
				}
				else{
				//	System.out.println("ClausIE Triples Matched and conditional, but idea not conditional");
				}				
			}			
			else{		//else if conditional was not found
//kk				System.out.println("\n\t here foundConditionalOnce == false");
			}
	
		} //END FOR LOOP		
		return false;
	}
	
	// this function checks if the sentence is conditional
    // the terms after te conditional term is passed here for matching with the triples from the input file
    // calls the clauseIE procedure to get the triples after the conditional
	// it is passed with the triples extracted from the leftover text, not the entire sentence
    
	public static boolean computeForConditional(String[][] v_tripleArrayOfLecftover, String v_subject, String v_relation, String v_object, Integer library, 
			FileWriter writer5) 
    		throws IOException
	{
		String conditional_v_subject = "", conditional_v_relation = "", conditional_v_object = "";
		Boolean conditional = false;
		Integer y_counter = 0;
		
		for (y_counter = 0; y_counter < 10; y_counter++) {
			// writer5.write("\ninside check for conditional - for loop");
			// assign array contents
			conditional_v_subject = v_tripleArrayOfLecftover[y_counter][0];
			conditional_v_relation = v_tripleArrayOfLecftover[y_counter][1];
			conditional_v_object = v_tripleArrayOfLecftover[y_counter][2];

			// if extracted triple is all null, do not proceed
			if ((conditional_v_subject == null || conditional_v_subject.isEmpty()) && 
				(conditional_v_relation == null || conditional_v_relation.isEmpty()) && 
				(conditional_v_object == null || conditional_v_object.isEmpty()))
				break;
			if (conditional_v_subject == "" && conditional_v_relation == "" 	&& conditional_v_object == "")
				break;

			// have to find a way to put this in a function as its repeated
			// check if not null and remove ( and " chars
			if ((conditional_v_relation == "") || conditional_v_relation == null) {
				// if (v_relation.isEmpty() &&
				// v_relation.toLowerCase().contains(verb.toLowerCase())) {
				System.out.println("\t verb Null - dont process");
				//
				// writer5.write("\nverb Null - dont process");
			}

			// debug and clean
//xx			debugMessage(conditional_v_subject, conditional_v_relation, conditional_v_object, v_subject, v_relation, v_object, writer5);

			// have to add code here similar to main comparison

			boolean firstPart = (POScontainsAllTerms(v_subject, conditional_v_subject) || POScontainsAllTerms(v_object, conditional_v_subject));
			boolean secondPart = (POScontainsAllTerms(v_relation, conditional_v_relation));
			boolean thirdPart = POScontainsAllTerms(v_subject, conditional_v_object)   || POScontainsAllTerms(v_object, conditional_v_object);

			// new code
			// if double, set third part to true, as they wont need to be
			// matched
			// this may wel;l have to be extended to check when both objects are
			// null, the one frm inputfile and the one extracted
			if (v_object == null || v_object == "" || v_object.isEmpty()) {
				thirdPart = true;
//				System.out.println("Third part set to matched ");
			}
			// if all triples of both match, then its conditional
			if (firstPart && secondPart && thirdPart) {
				// writer5.write("\ninside check for conditional - middle full inside");
//				System.out.println("conditional");
//				System.out.println("\tLibrary Triples Matched but conditional,"  + v_subject + " , " + v_relation + " , " + v_object);
				conditional = true;
				break;
			} else {
//				System.out.println("not conditional");
//				System.out.println("\nnot conditional");
				conditional = false;
			}
		} // end for loop
		// writer5.write("\n\t\t\t inside function check for conditional - end");
		return conditional;
	}
	
	
    // make sure all terms are in the pos
	// little //big
	public static Boolean checkTerms(String term, String pos) 
	{
		if (term == "" || term == null || term.isEmpty())
			return false;

		//temporray
		if (pos == "" || pos == null || pos.isEmpty())
			return false;
		
			if (pos.toLowerCase().contains(term)) {
//					System.out.println(" one not matched");
				return true;
			}
	
//			System.out.println("All terms : " + terms + " exist in POS: " + pos);
		return false;
	}
	
	// make sure all terms are in the pos
	// little //big
	public static Boolean POScontainsAllTerms(String terms, String pos) {

//		System.out.println("terms to check : " + terms);
//		System.out.println("part-of-sentence : " + pos);
		
		if (terms == "" || terms == null || terms.isEmpty())
			return false;

		//temporray
		if (pos == "" || pos == null || pos.isEmpty())
			return false;
		
//		if (terms.contains("rough"))
//			System.out.println("Testing terms: " + terms);		
		
		String[] splited = terms.split(" ");
		for (String s : splited) {
			if (!pos.toLowerCase().contains(s)) {
//				System.out.println(" one not matched");
				return false;
			}
		}
//		System.out.println("All terms : " + terms + " exist in POS: " + pos);
		return true;
	}

}
