package utilities;

import org.apache.commons.lang3.ArrayUtils;

public class SentenceUtility {
	public static Boolean stringAfter(String v_sentence, String firstString, String secondString) {
		   Integer a;
		   Integer b;
		   a = v_sentence.indexOf(firstString);
		   b = v_sentence.indexOf(secondString);
		   if (b > a)
		   	   return true;
		   else 
			   return false;
	   }
	
	//if a sentence has all strings in the string array
	public static boolean contains(String [] array, String v_sentenceString){
		boolean test = true;
		for (String w : array){
			if (!v_sentenceString.contains(w))
					test = false;
			//else 
				//test = false;
		}
		
		return test;
	}
	
	//if a sentence has all strings in the string array
	public static boolean containsAllButFirst(String [] v_array, String v_sentenceString){
		//remove first element -which is idea
		v_array = ArrayUtils.removeElement(v_array, 0);
		boolean test = true;
		for (String w : v_array){
			if (!v_sentenceString.contains(w))
					test = false;
			//else 
				//test = false;
		}
		
		return test;
	}

}
