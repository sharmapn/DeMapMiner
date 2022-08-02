package relationExtraction;

import java.util.Scanner;

import java.util.SortedSet;
import java.util.TreeSet;

import GUI.helpers2.result;
import relationExtraction.CountWords.Word;
import utilities.ReadFileLinesIntoArray;
import utilities.TableBuilder;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
//what do we need this for?
public class WordCoOccurence {

	static String[] initialWords = {"bdfl", "accept", "pep"};  //initial words - can be passed as parameter
	static String[] commonWords = {"the","and","are", "a", "to", "by", "as", "in","it","or","that","for"};  //initial words - can be passed as parameter
	static result[] re = new result[2000];
	static Integer Acounter =0;
	
public static void main(String[] args) {	
    //initialise result arrray
 //   for(int i=0;i<2000;i++){
 //   	re[i] = new result();
 //	}
    
    //actual sentences
    LinkedHashMap<Integer, String[]> cc = new LinkedHashMap<Integer, String[]>();
    //hashmapTally;
    LinkedHashMap<Integer, Integer> counterMap = new LinkedHashMap<Integer, Integer>();
    //all permutations
    
    String[] tokens = null, splitSentenceUniqueWords;    
    
    //System.out.println("Zero assignment > "+ "bdfl "+ "accept "+ "pep" );
    assignToHashMap(cc,initialWords , 0, counterMap);
    
    //Pankaj test
    ReadFileLinesIntoArray rf = new ReadFileLinesIntoArray();
    //frequency of words in same sentence
    Integer index=0;
    //READ ALL SENTENCES INTO HASHMAP
    try {
		String[] tmp = rf.readFromFile("C:/Users/psharma/Google Drive/PhDOtago/scripts/output/SelectedSentencespepacceptedpep.txt");
		//For each sentence
		for (String t : tmp){ 				
			tokens = preProcess(t);						//preprocess remove all non words
	    	splitSentenceUniqueWords = getUniqueKeys(tokens);     //get unique words            
	    	//SHOW EACH SENTENCE
	    	//System.out.println( " assignment > " + t );
	    	assignToHashMap(cc,tokens , index, counterMap);	
			 // Tokeniser tk = new Tokeniser(t);			  
			index++;        	  
		}
		//after all sentences have been read in
		displayHashMap(cc);
//VVV		compareHashMap(cc,tokens , index, counterMap);
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    
    //READ ALL SENTENCES AND SHOW ALL WORD FREQUENCY
    //This will also print the word and tally
    CountWords wc = new CountWords();
    //Map<String, Word> countMap = new HashMap<String, Word>();
    SortedSet<Word> v_sortedWords = new TreeSet<Word>();
    
    try {
    	v_sortedWords = wc.countWords();  //This will also print the word and tally
    	showWordCount(v_sortedWords);
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    //DISPLAY WORD FREQUENCY    
    FindsWordFrequencyInSentences(v_sortedWords, cc, false, 1);
   
    //if (v_counter == 0)  //if only one record in hashmap
    //	return;
    //for each record in hashmap
    //FOR EACH SENTENCE, ITERATE OVER EACH WORD 
    System.out.println("\nLAST OUTPUT "); 
//VVV    display(cc);
    System.out.println("\nSorting Array "); 
    //Arrays.sort(re);
    System.out.println("\nOutputting Array "); 
	int h=0;
//	for(result temp: re){
//	   System.out.println(" " + h + " : " + temp.getCombination() + " : " + temp.getCounter());
//	}
	
	TableBuilder tb = new TableBuilder();
	tb.addRow("Combination", "Counter");
	tb.addRow("-----------", "-------");
	//for all counters
	//System.out.println("re.length" + re.length);
	for (int p=180;p>=0;p--){
		//for all combinations
		for (int g=0;g<500;g++){    //1880
			//show how many for each number, coming down from 200, since the other way is not working 
			//System.out.println("  g  " + g + " re[g].getCounter() " +re[g].getCounter());
			if (re[g].getCounter() ==p){				
				tb.addRow( "BDFL accept pep | " +re[g].getCombination(), re[g].getCounter().toString());
				//System.out.println("Combination " + re[g].getCombination() + " " + re[g].getCounter());
			}
		}    
	}
	System.out.println(tb.toString());
}

public static void callWordCoOccurenceWithParamters(String inputFilename, String [] v_initialWords, String v_outputFilename) throws FileNotFoundException, UnsupportedEncodingException {	
    
    //actual sentences
    LinkedHashMap<Integer, String[]> cc = new LinkedHashMap<Integer, String[]>();
    //hashmapTally;
    LinkedHashMap<Integer, Integer> counterMap = new LinkedHashMap<Integer, Integer>();
    //all permutations
    
    String[] tokens = null;
    String[] splitSentenceUniqueWords;
    
    assignToHashMap(cc,v_initialWords , 0, counterMap);
    
    ReadFileLinesIntoArray rf = new ReadFileLinesIntoArray();
    //frequency of words in same sentence
    Integer index=0;
    //READ ALL SENTENCES INTO HASHMAP
    try {
		String[] tmp = rf.readFromFile(inputFilename);
		//For each sentence
		for (String t : tmp){ 				
			tokens = preProcess(t);						//preprocess remove all non words
	    	splitSentenceUniqueWords = getUniqueKeys(tokens);     //get unique words            
	    	//SHOW EACH SENTENCE
	    	//System.out.println( " assignment > " + t );
	    	assignToHashMap(cc,tokens , index, counterMap);	
			 // Tokeniser tk = new Tokeniser(t);			  
			index++;        	  
		}
		//after all sentences have been read in
		displayHashMap(cc);
//VVV		compareHashMap(cc,tokens , index, counterMap);
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    
    //READ ALL SENTENCES AND SHOW ALL WORD FREQUENCY
    //This will also print the word and tally
    CountWords wc = new CountWords();
    //Map<String, Word> countMap = new HashMap<String, Word>();
    SortedSet<Word> v_sortedWords = new TreeSet<Word>();
    
    try {
    	v_sortedWords = wc.countWords();  //This will also print the word and tally
    	showWordCount(v_sortedWords);
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    //DISPLAY WORD FREQUENCY    
    FindsWordFrequencyInSentences(v_sortedWords, cc, false, 1); 

	int h=0;
	
	TableBuilder tb = new TableBuilder();
	tb.addRow("Combination", "Counter");
	tb.addRow("-----------", "-------");
	//for all counters
	PrintWriter writer = new PrintWriter(v_outputFilename, "UTF-8");	
	
	//compute the initial words form array into string
	String str1 = Arrays.toString(v_initialWords);               
    //replace starting "[" and ending "]" and ","
    str1 = str1.substring(1, str1.length()-1).replaceAll(",", "");
	
	for (int p=180;p>=0;p--){
		//for all combinations
		for (int g=0;g<500;g++){    //1880
			//show how many for each number, coming down from 200, since the other way is not working 
			//System.out.println("  g  " + g + " re[g].getCounter() " +re[g].getCounter());
			if (re[g].getCounter() ==p){				
				tb.addRow( str1 + " | " +re[g].getCombination(), re[g].getCounter().toString());
				writer.println(str1 + " | " +re[g].getCombination() + "\t" + re[g].getCounter().toString());
				//System.out.println("Combination " + re[g].getCombination() + " " + re[g].getCounter());
			}
		}    
	}
	writer.close();
	System.out.println(tb.toString());
}

	public static void showWordCount(SortedSet<Word> sortedWords){
		int i = 0;
		System.out.println();
		for (Word word : sortedWords) {
			if (i > 100) {
				break;
			}
			System.out.println(word.count + "\t" + word.word);
			
			i++;
		}
	}
	
	public static void FindsWordFrequencyInSentences(SortedSet<Word> v_sortedWords, LinkedHashMap<Integer, String[]> cc, 
			boolean repeat, Integer level){
	    int i = 0;
	    Integer wordInSentencesCounter;
	    String tmp="";
	    //LOOP OVER EACH WORD
	    for (Word word : v_sortedWords) {
	    	//System.out.println("Start of First Level Sentence Processing with '" + word  + "' as first word");
	    	wordInSentencesCounter = 0;    //For each word initialise to 0 
			if (i > 100) {
				break;
			}
//IF NEED			System.out.println("\nWORD: " + word.word);		
			i++;
			//for each record in hashmap
		    Integer v_counter = 0;
		    Integer counterOnceInEachSentence = 0; 
		    //FOR EACH SENTENCE, ITERATE OVER EACH WORD 
		    for (Integer key:cc.keySet())
		    {	    	
		    	Integer counterAllInEachSentence = 0;
			    boolean foundInSentence = false;
//IMP		    	System.out.print("\nSENTENCE key "+ key + " ");
				//For each word in sentence
				for(String str : cc.get(key))
		        {
//IMP		            System.out.print(" " + str);
					//IF MATCH FOUND OUTPUT MATCH
					if (str.equals(word.word)){
//IMP						System.out.print(" |MATCH " + str + " | ");
						counterAllInEachSentence++;
						foundInSentence = true;	
					}
		        }
				//per sentence
				if (foundInSentence ==true) {
					counterOnceInEachSentence++;					
//IMP					System.out.println("\nFound word in sentence " + counterAllInEachSentence + " times.");
//IMPIMP					System.out.print("\nPer Sentence: BDFL accept pep + " + word.word + " = " + counterAllInEachSentence + " times.");
				}
		    	//System.out.println(c.get(key));    // print 1 then 2 finally 3
		    }
		    
		    String message = "In all sentences: BDFL accept pep + ";
		    if (level ==2)
		    	message = "\t";
		    boolean found = false;
	    	for (String s: initialWords) {           
	    		if (word.word.equals(s))
	    		{
	    			found = true;
	    		}	
	        }
	    	//do not include common words
	    	for (String s: commonWords) {           
	    		if (word.word.equals(s))
	    		{
	    			found = true;
	    		}	
	        }
	    	if (found==false){	    		
	    		result x = new result(word.word, counterOnceInEachSentence);
	    		re[Acounter]=x;
	    		Acounter++;
	    	}		    

//		    System.out.println("Acounter a1 " + re[Acounter].getCombination() + " " + re[Acounter].getCounter());
//TEST			Arrays.sort(re);  										//sort according to counter
//IMPPP		    System.out.println(message + word.word + " = " + counterOnceInEachSentence + " times.");
		    FindsWordFrequencyInSentencesLevel2(v_sortedWords, cc, false, 2, word.word);
		    //Second level results should be output here
		    // how many times BDFL accept pep + word + counter + Plus every other word counter
		    //just as you finish checking each sentence, if you find a match then
		    // loop over all other words and find counter for each sentence and at end counter in all sentences
		    tmp = word.word;
		}
	    System.out.println("End of First Level Sentence Processing. Ended with '" + tmp  + "' as first word");
	}
	
	public static void FindsWordFrequencyInSentencesLevel2(SortedSet<Word> v_sortedWords, LinkedHashMap<Integer, String[]> cc, 
			boolean repeat, Integer level, String firstWord){		
	    int i = 0;
	    Integer wordInSentencesCounter;
	    String tmp="";
	    //LOOP OVER EACH WORD
	    for (Word word : v_sortedWords) {
//	    	System.out.println("Start of Second Level Sentence Processing with '" + word  + "' as first word");
	    	wordInSentencesCounter = 0;    //For each word initialise to 0 
			if (i > 100) {
				break;
			}
			i++;
			//for each record in hashmap
		    Integer v_counter = 0;
		    Integer counterOnceInEachSentence = 0; 
		    //FOR EACH SENTENCE, ITERATE OVER EACH WORD 
		    for (Integer key:cc.keySet())
		    {	    	
		    	Integer counterAllInEachSentence = 0;
			    boolean foundSecondWordInSentence = false,foundFirstWordInSentence = false;
//IMP		    	System.out.print("\nSENTENCE key "+ key + " ");
				//For each word in sentence
				for(String str : cc.get(key))
		        {
//IMP		            System.out.print(" " + str);
					//IF MATCH FOUND OUTPUT MATCH
					if (str.equals(word.word)){
//IMP						System.out.print(" |MATCH " + str + " | ");
						counterAllInEachSentence++;
						foundSecondWordInSentence = true;	
					}
					if (str.equals(firstWord)){
						//counterAllInEachSentence++;
						foundFirstWordInSentence = true;	
					}
					
		        }
				//per sentence
				if (foundFirstWordInSentence ==true && foundSecondWordInSentence==true) {
					counterOnceInEachSentence++;
				}
		    }
		    //show only those combinations which exist (i.e. >0)
		    if (counterOnceInEachSentence >0){
		    	//System.out.println("\t\t\t\t\t\t\t\t" + firstWord+ " + "+ word.word  + " = " + counterOnceInEachSentence + " times.");
		    	
//		    	System.out.println("Acounter B " + Acounter);
		    	 //IMPORTANT CHECK TO REMOVE REPEATED WORDS - THOSE IN THE INITIAL LIST
		    	//String[] initialWords = {"bdfl", "accept", "pep"};
		    	boolean found = false;
		    	for (String s: initialWords) {           
		    		if (firstWord.equals(s) || word.word.equals(s))
		    		{
		    			found = true;
		    		}
		    		//dont show occurence of two words, which normally ends up same as single occurence
		    		if (firstWord.equals(word.word))
		    		{
		    			found = true;
		    		}		    		
		        }
		    	//do not include common words
		    	for (String s: commonWords) {           
		    		if (firstWord.equals(s) || word.word.equals(s))
		    		{
		    			found = true;
		    		}	
		        }
		    	if (found==false){		    		
		    		result x = new result(firstWord+ " + "+ word.word, counterOnceInEachSentence);
		    		re[Acounter]=x;
		    		Acounter++;
		    	}
		    	 
		    }
		    
		    tmp = word.word;
		}
//	    System.out.println("End of Second Level Sentence Processing. Ended with '" + firstWord  + "' as first word and '" + tmp + "' as second word");
	}

	public static void display(LinkedHashMap<Integer, String[]> cc){
		Integer v_counter = 0;
	    for (Integer key:cc.keySet())
	    {	    	
			System.out.print("\nkey "+ key + " ");
			for(String str : cc.get(key))
	        {
	           //System.out.print("key "+ key+ " " +str);
				System.out.print(str + " ");
	        }
	    	//System.out.println(c.get(key));    // print 1 then 2 finally 3
	    }
	}

	public static String[] preProcess(String userInput){
		userInput = userInput.toLowerCase();

	    userInput = userInput.replaceAll( "\\W", " " );     // strip out any non words.
	    userInput = userInput.replaceAll( "  ", " " );      // strip out any double spaces
	                                                        //   created from stripping out non words
	                                                        //   in the first place!
	    String[] tokens = userInput.split( " " );
	//    System.out.println( userInput );

		return tokens;
	}
	
	public static void compareHashMap(LinkedHashMap<Integer, String[]> c, String [] input, Integer v_counter, 
			LinkedHashMap<Integer, Integer> counterArray){
		if (v_counter == 0)  //if only one record in hashmap
	    	return;
	    //for each record in hashmap
	    for (Integer key : c.keySet())   //KEY IS RECORD COUNTER
	    {	    	
	    	if (key >0) {
	    		System.out.println(" key " +key + " c.keySet() " + c.keySet());
	    		System.out.println("hashmap counter " + v_counter + " ");
	    	 
		    	//for each word in string of current hashmap record
				for(String str : c.get(key))
		        {
		           //compare with previous hashmap records words
					for(String str2 : c.get(key-1))
			        {
						//System.out.print(" key " +key + "c.get(key) " + c.get(key));    //each word
			           if (str.equals(str2)){ 
			        	   System.out.println(" matched " + str + " " + str2);
			        	   }
			        }
		        }
	    	}
	    	else 
	    	{System.out.println(" key skipped " +key + " c.keySet() " + c.keySet());}
	    	//System.out.println(c.get(key));    // print 1 then 2 finally 3
	    }
	}
	
	public static void assignToHashMap(LinkedHashMap<Integer, String[]> c, String [] input, Integer v_counter, 
			LinkedHashMap<Integer, Integer> counterArray){
		    //c.put(3,'a');
		    c.put(v_counter,input);
//		    for (Integer key:b.keySet())
//		    {
//		    	System.out.println("b elements");
//		    	System.out.println(b.get(key));    // print 1 then 2 finally 3
//		    }  
		    
	}
	
	public static void displayHashMap(LinkedHashMap<Integer, String[]> c){
		System.out.println("HashMap elements");
		for (Integer key:c.keySet())
	    {	    	
			System.out.print("\nkey "+ key + " ");
			for(String str : c.get(key))
	        {
	           //System.out.print("key "+ key+ " " +str);
				System.out.print(str + " ");
	        }
	    	//System.out.println(c.get(key));    // print 1 then 2 finally 3
	    }
	}

	public static void loopOverWords(ArrayList< String > words){
		int count = 0;
		for( int i = 0; i < words.size(); i++ )
	    {
	        System.out.printf( "%s: ", words.get( i ) );
	        for( int j = 0; j < words.size(); j++ )
	        {
	            if( words.get( i ).equals( words.get( j ) ) )
	                count++;
	            if( words.get( i ).equals( words.get( j ) ) && count > 1 )
	            	words.remove( j );                      // after having counted at least
	        }                                               // one, remove duplicates from List        
	        System.out.printf( "%d\n", count );
	        count = 0;
	    }
	}
	
	private static String[] getUniqueKeys(String[] keys) {
		String[] uniqueKeys = new String[keys.length];

		uniqueKeys[0] = keys[0];
		int uniqueKeyIndex = 1;
		boolean keyAlreadyExists = false;

		for (int i = 1; i < keys.length; i++) {
			for (int j = 0; j <= uniqueKeyIndex; j++) {
				if (keys[i].equals(uniqueKeys[j])) {
					keyAlreadyExists = true;
				}
			}

			if (!keyAlreadyExists) {
				uniqueKeys[uniqueKeyIndex] = keys[i];
				uniqueKeyIndex++;
			}
			keyAlreadyExists = false;
		}
		return uniqueKeys;
	}
}

//Pankaj test end



//for (int i=1;i<2;i++){
//	//spilt sentence abd get unique words
//	tokens = preProcess(userInput);						//preprocess remove all non words
//	splitSentenceUniqueWords = getUniqueKeys(tokens);     //get unique words            
//	 System.out.println( "First assignment > " + userInput );
//	assignToHashMap(cc,tokens , 1, counterMap);
//  
//	//try
//  tokens = preProcess(userInput2);			//preprocess remove all non words
//  splitSentenceUniqueWords = getUniqueKeys(tokens); 
//  System.out.println( " second assignment " + userInput2 );
//  assignToHashMap(cc,tokens , 2, counterMap);
//  
//  displayHashMap(cc);
//}

//c.put(1,initialWords);      						
//c.put(2,userInput2.split(" "));



// words.addAll( Arrays.asList( tokens ) );    
//loopOverWords(words);   


//ArrayList< String > words = new ArrayList< String >();

//VVV loopOverWords(words); 