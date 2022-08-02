package utilities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import EnglishOrJavaCode.*;

public class ParagraphSentence {
	static EnglishOrCode ec = new EnglishOrCode();
	//test case pass an entire commit message
	public static void main(String[] args) throws ClassNotFoundException, IOException {
		ec.initializeTokenizer();
		String s = "do something in English";
		if(ec.isEnglish(s)){
			System.out.println("English");
		}else{
			System.out.println("Java Code");
		}
		
		s = "for (int i = 0; i < b.size(); i++) {";
		if(ec.isEnglish(s)){
			System.out.println("English");
		}else{
			System.out.println("Java Code");
		}		
	}
	
	public static void initTokenizer() {
		ec.initializeTokenizer();
	}
	
	//This function is used to determine if the clausie parser should parse the piece of text or not
	//regardless of that, the status checking (Status: Accepted and Special Identifier check, Rejection Notice ) will be done all all of this text. Thats will be string matching, so should be fine
	//the text can contain code or headers
	//check if string passed is sentence..applies only to sentences	
		
	public static String ifTextIsSentence_ReturnSentence(String text) {
		System.out.println("\nSentence: "+text);
		String sentence="";
		String[] lines = text.split("\\r?\\n");	//if its a multiline text, split it into multilines
		int counter=0;	//we wont consider the first line
		for (String line: lines) {//for each multiline
			//check if its code or english sentence
			System.out.println("\tLine Num: "+ counter + ": "+ line);
			if(ec.isEnglish(line)){
				//System.out.println("English");
				if(counter>0 && Character.isUpperCase(line.charAt(0)) && line.length() < 40) {	//if the first character in next line is capital and existing line is less than 30 chars
					System.out.print("\t\tLine in Sentence Skipped: " + line); continue;	//treat as headers and remove that line
				}
				else {//else is sentence and add to string
					sentence +=sentence;	
					System.out.print("\t\tLine in Sentence Not Skipped: " + line);
				}
			}else{
				System.out.print("\t\tLine = Code: "+ line); continue; 
			}
			counter++;	System.out.println();
		}	
		return sentence;
	}
	
	//make sure all three terms exist in sentence
	public static boolean containsSVO(String subject, String verb, String object, String sentence, Boolean matchWholeTerms){
		String terms = subject + " " + verb + " " +object;		
		String[] splited=null;		

		if(terms.contains(" ")){
			terms = terms.replace("  "," ");		//just in case the string contains double spaces	
			splited = terms.toLowerCase().split(" ");				
		}
		String[] sentenceStr =null;
		
		if (sentence == "null" || sentence == null || sentence.length() == 0 || sentence.isEmpty()){
			return false;
		}
		else{
			if (sentence.contains(" ")){
				sentenceStr = sentence.toLowerCase().split(" ");
			}
			else{	//sentence may not have space, so therefore just one logical term and in this case return false
				return false;
			}
		}
		//we dont want this..as we will want to see stemmed terms
		//if(matchWholeTerms)
			return Arrays.asList(sentenceStr).containsAll(Arrays.asList(splited));
		//else
		//	return (sentence.contains(subject) && sentence.contains(verb) && sentence.contains(object));
	}
	//we do all this so we can match whole words....poll instead of polluted
	//make sure all three terms exist in sentence as it is 'no support' is searched as no support in sentence
	//we check for terms and then check if it exits subsequently
	public static boolean containsAllTermsAsTheyAre(String termAsItIs, String sentence, Boolean matchWholeTerms){			
		String terms = termAsItIs.trim();		
		List<String> splited = new ArrayList<String>();		
		try {
			if(terms.contains(" ")){
				//terms = terms.replace("  "," ");		//just in case the string contains double spaces
				terms = terms.replaceAll("\\s{2,}", " "); //remove double spaces
				//splited.add(termAsItIs); 
				for(String s: terms.toLowerCase().split(" ")) {
					splited.add(s); 
				}			
			}else {
				splited.add(termAsItIs);
			}
			String[] sentenceStr =null;
			
			if (sentence == "null" || sentence == null || sentence.length() == 0 || sentence.isEmpty()){
				return false;
			}
			else{
				//sometimes a term is directly followed by a comma...."he did ..decisively," and comma woudl not allow decisively to be matched
				if (sentence.contains(",")) {
					sentence  = sentence.replace(",", "");
				}
				if (sentence.contains(".")) {
					sentence  = sentence.replace(".", "");
				}
				sentence = sentence.replaceAll("[^a-zA-Z ]", "").toLowerCase();	//remove all punctuation
				sentence = sentence.replaceAll("\\s+", " ").trim(); //remove double spaces and trim
				sentence = sentence.replaceAll("\\?", "");
				
				if (sentence.contains(" ")){
					sentenceStr = sentence.toLowerCase().split(" ");
				}
				else{	//sentence may not have space, so therefore just one logical term and in this case return false
					return false;
				}
			}
			//we dont want this..as we will want to see stemmed terms
			//if(matchWholeTerms)
//			System.out.println("sentence: "+sentence + " termAsItIs: " + termAsItIs);
			if( Arrays.asList(sentenceStr).containsAll(splited) ) { //all terms are found
//				System.out.println("contains terms: ");
				if (sentence.contains(termAsItIs)) { //System.out.println("true ");
					return true;
				} else {// System.out.println("false: ");
					return false;
				}
			}
		} catch (Exception e) {
			System.out.println(" ______here 23  " + e.toString() + "\n" );
			System.out.println(StackTraceToString(e)  );	
		}
		//else
		//	return (sentence.contains(subject) && sentence.contains(verb) && sentence.contains(object));
		return false;
	}
	
	public static boolean containsArray(String[] terms, String sentence, Boolean matchWholeTerms){
				
		if(terms==null){
			return false;				
		}
		String[] sentenceStr =null;
		
		if (sentence == "null" || sentence == null || sentence.length() == 0 || sentence.isEmpty()){
			return false;
		}
		else{
			if (sentence.contains(" ")){
				sentenceStr = sentence.toLowerCase().split(" ");
			}
			else{	//sentence may not have space, so therefore just one logical term and in this case return false
				return false;
			}
		}
		//we dont want this..as we will want to see stemmed terms
		//if(matchWholeTerms)
			return Arrays.asList(sentenceStr).containsAll(Arrays.asList(terms));
		//else
		//	return (sentence.contains(subject) && sentence.contains(verb) && sentence.contains(object));
	}
	//return the terms found in Sentence
	public static boolean termsInSentence(String subject, String verb, String object, String sentence, Boolean matchWholeTerms){
		String terms = subject + " " + verb + " " +object;		
		String[] splited=null;		

		if(terms.contains(" ")){
			terms = terms.replace("  "," ");			splited = terms.toLowerCase().split(" ");				
		}
		String[] sentenceStr =null;
		
		if (sentence == "null" || sentence == null || sentence.length() == 0 || sentence.isEmpty()){
			return false;
		}
		else{
			if (sentence.contains(" ")){
				sentenceStr = sentence.toLowerCase().split(" ");
			}
			else{	//sentence may not have space, so therefore just one logical term and in this case return false
				return false;
			}
		}
		//we dont want this..as we will want to see stemmed terms
		if(matchWholeTerms)
			return Arrays.asList(sentenceStr).containsAll(Arrays.asList(splited));
		else
			return (sentence.contains(subject) && sentence.contains(verb) && sentence.contains(object));
	}
    
    public static String StackTraceToString(Exception ex) {
	    String result = ex.toString() + "\n";
	    StackTraceElement[] trace = ex.getStackTrace();
	    for (int i=0;i<trace.length;i++) {
	        result += trace[i].toString() + "\n";
	    }
	    return result;
	}

}
