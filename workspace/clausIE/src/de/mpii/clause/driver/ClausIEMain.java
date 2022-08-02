package de.mpii.clause.driver;

import java.io.FileWriter;
import java.io.IOException;
import java.security.cert.CertPathValidatorException.Reason;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import de.mpii.clausie.ClausIE;
import de.mpii.clausie.Clause;
import de.mpii.clausie.Proposition;


public class ClausIEMain  {
	
	static InsertExtractedTripleIntoRelationTable ir = new InsertExtractedTripleIntoRelationTable();
	static ClausIE clausIE;
	
	public void dispose(){
		clausIE.clear();
	}	
	
	public ClausIEMain() { 
		clausIE = new ClausIE();	
		clausIE.initParser();		
	}
	
	// THIS ONE IS USED --use this one- Latest Feb 2017
	public static String[][] computeClausIE(String sentence, boolean showCIEDebugOutput) throws IOException {		
//		System.out.println("\tCIE Propositions: [Inside computeClausIE() Fn]");	
		
		try{
			clausIE.parse(sentence);	// parse tree			
			clausIE.detectClauses();	// clause detection
			clausIE.generatePropositions();
		}
		catch (OutOfMemoryError e) {
			System.out.println("\nOut of memory Java Error  : ");
		}
		
		String v_subject = null, v_relation = null, v_object = null;
		
		//xx		System.out.println("Clauses          : ");
		String sep = "";
		for (Clause clause : clausIE.getClauses()) {
			//			System.out.println(clause.toString(clausIE.getOptions())); // sep +
			sep = "                   ";
		}
		
		sep = "";	
		String [][] triple_array = new String [100][3];		//in the above declaration y max is set to 100		// and x max is set to 3
		Integer y_counter= 0;
		
		// generate propositions
		for (Proposition prop : clausIE.getPropositions()) {
			if(showCIEDebugOutput)
				System.out.println("\t\t"+prop.toString()); // sep +
			sep = "                   ";
			
			// split the string
			String[] partsOfPreposition = prop.toString().split(",");
			v_subject = partsOfPreposition[0];
			v_relation = partsOfPreposition[1];
			// sometimes the clauseIE just has two terms, subject and verb - no object. The following to prevent null pointer error
			if (partsOfPreposition.length == 3)
				v_object = partsOfPreposition[2];
			else
				v_object = "";
			
			//assign the extracted triples
			triple_array[y_counter][0] = v_subject;			triple_array[y_counter][1] = v_relation;			triple_array[y_counter][2] = v_object;									
			
			y_counter++;		//increment y counter
		} // end for
		return triple_array;	//RETURN THE TRIPLE IN DOUBLKE ARRAY
	}
	
	// THIS ONE IS USED --use this one- latest used 23 rd j8une 2016 kkvl
	public static String[][] computeClausIEForConditionalChecking(String sentence, boolean showCIEDebugOutput) throws IOException {
		//System.out.println("\t Computing ClausIE For Conditional Checking");			
			
		try{			
			clausIE.parse(sentence);			clausIE.detectClauses();			clausIE.generatePropositions();//				System.out.println("\nSentence (Inside computeClausIE) : " + sentence);
		}
		catch (OutOfMemoryError e) {
			System.out.println("\nOut of memory Java Error  : 231 ");
		}
		String v_subject = null, v_relation = null, v_object = null;				
		String sep = "";
		//xx		System.out.println("Clauses          : ");
		for (Clause clause : clausIE.getClauses()) {			
			sep = "                   ";	//				System.out.println(clause.toString(clausIE.getOptions())); // sep +		
		}				
		sep = "";// generate propositions	
				
		String [][] triple_array = new String [100][3];	// additional code needed for the below code		
		Integer y_counter= 0;
		
		//in the above declaration y max is set to 100 and x max is set to 3			
		for (Proposition prop : clausIE.getPropositions()) {
			if(showCIEDebugOutput)
				System.out.println("\t\t\t"+prop.toString()); // sep +
			sep = "                   ";
			
			// split the string
			String[] partsOfPreposition = prop.toString().split(",");
			v_subject = partsOfPreposition[0];			v_relation = partsOfPreposition[1];
			// sometimes the clauseIE just has two terms, subject and verb - no
			// object. the following to prevent null pointer error
			if (partsOfPreposition.length == 3)
				v_object = partsOfPreposition[2];
			else
				v_object = "";			
			//assign the extracted triples
			triple_array[y_counter][0] = v_subject;			triple_array[y_counter][1] = v_relation;			triple_array[y_counter][2] = v_object;			
			//RETURN THE TRIPLE IN DOUBLKE ARRAY			
			//increment y counter
			y_counter++;
		} // end for		
		return triple_array;
	}    
	
	public static void computeClausIEOnly(String sentence) throws IOException{		
		clausIE.parse(sentence);		clausIE.detectClauses();		clausIE.generatePropositions();		
		String v_subject = null, v_relation = null, v_object = null, sep = "";
		for (Clause clause : clausIE.getClauses()) {
			//pp           System.out.println( clause.toString(clausIE.getOptions()));		//sep +
			sep = "                   "; 
		}
		
		// generate propositions
		//pp        System.out.println("Propositions     : ");
		sep = "";
		for (Proposition prop : clausIE.getPropositions()) {
			System.out.println( prop.toString());							// sep +
			sep = "                   ";
			//split the string
			String[] partsOfPreposition = prop.toString().split(",");			
			v_subject = partsOfPreposition[0];			v_relation = partsOfPreposition[1];
			//sometimes the clauseIE just has two terms, subject and verb - no object. the following to prevent null pointer error
			if (partsOfPreposition.length ==3) 
				v_object = partsOfPreposition[2];
			else
				v_object = "";         
		}		
	}
	
	public static boolean computeClausIEOnlyForConditional(String sentence, String conditional_v_subject, String conditional_v_relation, String conditional_v_object)			throws IOException{
		clausIE.parse(sentence);		clausIE.detectClauses();		clausIE.generatePropositions();
		
		String v_subject = "", v_relation = "", v_object = "", sep = "";
		for (Clause clause : clausIE.getClauses()) {
			//pp           System.out.println( clause.toString(clausIE.getOptions()));		//sep +
			sep = "                   "; 
		}
		
		boolean conditional = false;
		
		// generate propositions
		//pp       System.out.println("Propositions     : ");
		sep = "";
		String[] partsOfPreposition = null;
		for (Proposition prop : clausIE.getPropositions()) {
			//pp            System.out.println( prop.toString());							// sep +
			sep = "                   ";			
			//split the string
			partsOfPreposition = prop.toString().split(",");
			v_subject = partsOfPreposition[0];			v_relation = partsOfPreposition[1];
			//sometimes the clauseIE just has two terms, subject and verb - no object. the following to prevent null pointer error
			if (partsOfPreposition.length ==3) 
				v_object = partsOfPreposition[2];
			else
				v_object = "";
			//check conditional matches
			if (conditional_v_subject.toLowerCase().equals(v_subject.toLowerCase()) || conditional_v_relation.toLowerCase().equals(v_relation.toLowerCase()) 
					|| conditional_v_object.toLowerCase().equals(v_object.toLowerCase())) {
				System.out.println("conditional");
				System.out.println("\tClausIE Triples Matched but conditional,"	+ v_subject + " , " + v_relation + " , " + v_object);
				conditional = true;				return conditional;
			} 
			else {
				System.out.println("not conditional");				conditional = false;
			}
		}
		return conditional;
	}
	
	public static Reasons computeClausIEOnlyForReasons(String sentence, String leftOverReason, String [] reasonLabels, Reasons r) throws IOException{   		
		//should be passed as parameter
		String reasons[]         = {"poll results","voting results","vote results","consensus","favourable feedback","limited utility and difficulty of implementation "};		
		clausIE.parse(sentence);		clausIE.detectClauses();		clausIE.generatePropositions();		
		String v_reason = "", subject = "", verb = "", object = "";
		boolean foundReason = false;
		String reasonMatched = null;
		
		// two ways to check for reasons...
		// from predefined terms and triples
		
		//check reason in predefined list
		for (String reasonA : reasons)	{
			if(leftOverReason.contains(reasonA))	{
				System.out.println("\tFound Reason in term: "	+ reasonA);
				//writer.write(" Reason: "+ reasonA);
				foundReason = true;
			}
		}		
		
		//check reasons in triples 
		for (String reason : reasonLabels)		{
			String words[] = reason.split(" ");
			if(words.length==3 ){
				v_reason =  words[0] ;				subject =   words[1];				verb =  words[2];	   			
			}
			//capture object as well - form triples
			else if(words.length==4){
				v_reason =  words[0] ;				subject =   words[1];				verb =  words[2];				object =  words[3];	   			
			}				
			//capture the second set of triple, if more columns are provided in the inputfile
			else if (words.length>5){
				v_reason =  words[0] ;				subject =   words[1];				verb =  words[2];	   			
			}
			
			System.out.println("subject "+ subject + " verb " + verb + " object " + object);
			
			String v_subject = "", v_relation = "", v_object = "", sep = "";
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
				partsOfPreposition = prop.toString().split(",");//split the string
				v_subject = partsOfPreposition[0];				v_relation = partsOfPreposition[1];
				//sometimes the clauseIE just has two terms, subject and verb - no object. the following to prevent null pointer error
				if (partsOfPreposition.length == 3) 
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
	
	//this one is for exctracting relations and inserting into database
	public static void extractClauseIERelations(Connection connection,String proposalIdentifier, String sentStr,String prevSentence,String nextSentence, String entireParagraph,String prevParagraph,String nextParagraph,
			Integer v_pepNumber, Integer v_message_ID, String author, Date v_date, String v_tablename,boolean outputDetails, Integer sentenceCounter, Integer paragraphCounter, Integer lineNumber,
			boolean isEnglish, boolean isFirstParagraph, boolean isLastParagraph, String msgSubject, String msgAuthorRole, Boolean isCorefParagraph, String allVerbsInSentence,
			String allNounsInSentence,String allVerbsInMsgSubject, String allNounsInMsgSubject) throws Exception {
		String allVerbsInSub="", allVerbsInRel="", allVerbsInObj="",allNounsInSub="", allNounsInRel="", allNounsInObj="";
		//InsertIntoRelationTable ir = new InsertIntoRelationTable();
		try {
			if(outputDetails)
				System.out.println("\t Input sentence: " + sentStr);  //ClausIE, MID: " + v_message_ID + " 
			//System.out.println("\nClausIE ...");		
			clausIE.parse(sentStr);			//System.out.println("ClausIE parse");	
			clausIE.detectClauses();		//System.out.println("ClausIE detectClauses");	
			clausIE.generatePropositions();	//System.out.println("ClausIE generatePropositions");
			
			String v_subject = "", v_relation = "", v_object = "", sep = "";// allVerbsInMsgSub="",allNounsInMsgSub="";
			for (Clause clause : clausIE.getClauses()) {
				//           System.out.println( clause.toString(clausIE.getOptions()));		//sep +
				sep = "                   "; 
			}
			
			String tempMsgSub = msgSubject; //remove everything in square brackets from msg subject
			if ( tempMsgSub==null || tempMsgSub == "" || tempMsgSub.isEmpty() )	{}
			else{
				//e.g. [Python-Dev] Propose rejection of PEPs 239 and 240 -- a builtin  rational type and rational literals
				if(tempMsgSub.contains("[") && tempMsgSub.contains("]")) {
					tempMsgSub = tempMsgSub.replaceAll("\\[.*?\\]","");
				}
			}
			
			// generate propositions   System.out.println("Propositions     : ");
			sep = "";
			for (Proposition prop : clausIE.getPropositions()) {
				if(outputDetails)
					System.out.println("\t\t"+ prop.toString());							// sep +
				sep = "                   ";			
			
				String[] partsOfPreposition = prop.toString().split(",");	//split the string
				v_subject = partsOfPreposition[0];			v_relation = partsOfPreposition[1];
				//sometimes the clauseIE just has two terms, subject and verb - no object. the following to prevent null pointer error
				if (partsOfPreposition.length ==3) 
					v_object = partsOfPreposition[2];
				else
					v_object = "";
				
				v_subject = v_subject.replace("(", "");		v_relation = v_relation.replaceAll("\\p{P}","");	/*replace brackets */ 	v_object = v_object.replace(")", "");
				
				partsOfPreposition = null;	
				//jan 2019, b4 inserting we wanty to find out which verbs exist in sub, rel and obj
				allVerbsInSub = returnCommonTerms_Order(v_subject,allVerbsInSentence);	allVerbsInRel = returnCommonTerms_Order(v_relation,allVerbsInSentence);	allVerbsInObj = returnCommonTerms_Order(v_object,allVerbsInSentence);
				//allVerbsInMsgSub = returnCommonTerms_Order(v_subject,tempMsgSub);
				
				allNounsInSub= returnCommonTerms_Order(v_subject,allNounsInSentence);  allNounsInRel= returnCommonTerms_Order(v_subject,allNounsInSentence); allNounsInObj= returnCommonTerms_Order(v_subject,allNounsInSentence);
				//allNounsInMsgSub = returnCommonTerms_Order(v_subject,tempMsgSub);
				
				//jan 2019
				//message subject
				//pair p = new pair();
				//p = prp.getStanfordNLP().returnAllVerbsAndNounsInSentence(msgSubject,p);
				//String allVerbsInMsgSubject = p.getVerb(); 	String allNounsInMsgSubject = p.getNoun();
				
				
				ir.insertTriplesintoTable(connection,proposalIdentifier, v_subject, v_relation, v_object, sentStr, prevSentence,nextSentence,entireParagraph, prevParagraph, nextParagraph,v_tablename, v_pepNumber, v_message_ID,
						sentenceCounter, paragraphCounter, lineNumber, isEnglish, isFirstParagraph,  isLastParagraph,  msgSubject,  msgAuthorRole,isCorefParagraph, allVerbsInSub, allVerbsInRel, allVerbsInObj,
						allVerbsInMsgSubject,allNounsInSub, allNounsInRel, allNounsInObj,allNounsInMsgSubject);
			}	
			clausIE.clear();
		}catch (Exception e){ 
			System.out.println(" Sentence ClausIE error  " + e.toString() + "\n Sentence: " + sentStr );			//System.out.println(Thread.currentThread().getStackTrace()  );		//+  " \n" + e.printStackTrace()
			System.out.println(StackTraceToString(e)  );	
			//continue;
		} 
	}
	
	public static String returnCommonTerms_Order(String pos,String allVerbsInSentence) {
		
		if(allVerbsInSentence==null || allVerbsInSentence.equals("") || allVerbsInSentence.length()==0) { return "";}
		if(pos==null || pos.equals("") || pos.length()==0) { return "";}
		
		pos= pos.toLowerCase();		allVerbsInSentence=allVerbsInSentence.toLowerCase();
		List<String> posTerms =  new ArrayList<String>();	List<String> allVerbsInSentenceTerms =  new ArrayList<String>();
		String finalStr="";
		
		if(pos.contains(" ")) {			for(String p: pos.split(" "))	posTerms.add(p);		}
		else { posTerms.add(pos);	}
		if(allVerbsInSentence.contains(" ")) {			for(String a: allVerbsInSentence.split(" "))	allVerbsInSentenceTerms.add(a);		}
		else { allVerbsInSentenceTerms.add(allVerbsInSentence);	}
		//sort
		Collections.sort(posTerms); Collections.sort(allVerbsInSentenceTerms); 
		
		for (String p : posTerms) {	//look for terms //System.out.print(n + " ");
			for (String v: allVerbsInSentenceTerms)
				if (p.equals(v) ) {
					finalStr = finalStr + " " + v;
				}
		}
		return finalStr;
	}
	
	public static String StackTraceToString(Exception ex) {
		String result = ex.toString() + "\n";
		StackTraceElement[] trace = ex.getStackTrace();
		for (int i=0;i<trace.length;i++) {
			result += trace[i].toString() + "\n";
		}
		return result;
	}
	
	public String checkProposition(String sentence, String subject, String verb, String object){	
		System.out.println("\nInput sentenced  q : " + sentence);
		
		// parse tree
		clausIE.parse(sentence);		clausIE.detectClauses();		clausIE.generatePropositions();
		
		System.out.print("Clauses          : ");
		String sep = "";
		
		List<String> c = new ArrayList<String>(); 
		
		for (Clause clause : clausIE.getClauses()) {
			System.out.println(sep + clause.toString(clausIE.getOptions()));
			sep = "                   ";
			c.add(clause.toString(clausIE.getOptions()));
		}
		
		List<String> p = new ArrayList<String>(); 
		
		// generate propositions
		System.out.print("Propositions     : ");
		sep = "";
		
		String s,all = null;
		
		for (Proposition prop : clausIE.getPropositions()) {
			System.out.println(sep + prop.toString());
			sep = "                   ";
		}
		
		for (Proposition prop : clausIE.getPropositions()) {
			System.out.println(sep + prop.toString());
			sep = "                   ";
			p.add(prop.toString());
			
			s=prop.toString();
			//or combine all strings in one, just for now
			all += s;            
			
			s= s.replace("(", "");			s= s.replace(")", "");
			
			String terms[] = s.split(",");
			String v_idea = terms[0], v_subject = terms[1], v_verb = terms[2], v_object = terms[3];
			//if the poposistion contains the triple terms, return the lable/idea
			//can be further refines to see if it does not contain not, etc which would change meaning
			//also it return the first match found, can be further coded to handle cases when the idea label would be further down in the array
			if (v_verb.contains(verb)&& v_subject.contains(subject) && v_object.contains(v_object)  ){
				//return v_idea;
				System.out.println("MATCH FOUND");
			}			
		}
		return null;		
	}
	
	@Override
	protected void finalize() throws Throwable	{
		//   System.out.println("From Finalize Method, i = "+i);
	}
}
