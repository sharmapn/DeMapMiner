package sampleCodes;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import connections.MysqlConnect;
//sept 2018..script hardly used ..as positive negative not that important- till now
public class MatchCauseEffectWithPositiveNegativeTerms {
	static List<String> causeSentencesLists = new ArrayList<String>(), effectSentencesList = new ArrayList<String>(),
						positiveWords = new ArrayList<String>(), negativeWords = new ArrayList<String>(),stopWords = new ArrayList<String>();;
	static String positiveWordsFileName = "C:\\Users\\psharma\\Google Drive\\PhDOtago\\Code\\inputFiles\\positive-words.txt",
				  negativeWordsFileName = "C:\\Users\\psharma\\Google Drive\\PhDOtago\\Code\\inputFiles\\negative-words.txt",
				  stopWordsFileName     = "C:\\Users\\psharma\\Google Drive\\PhDOtago\\Code\\workspaces\\fifth\\npl-lab-master\\src\\test\\stoplist.txt";;
	static Connection conn= null;
	static Statement stmt = null;
	//each positive and negative word and their final tally in all cause and effect sentences
	static HashMap<String, Integer> positiveWordCount = new HashMap<String,Integer>();
	static HashMap<String, Integer> negativeWordCount = new HashMap<String,Integer>();
	//every word and their tally in all cause and effect sentences
	static Map<String, Integer> wordCountInCauseSentences = new TreeMap<String,Integer>();
	static Map<String, Integer> wordCountInEffectSentences = new TreeMap<String,Integer>();
	
	public static boolean ASC = true,DESC = false;
	
	public static void main(String[] args)  {
		try{  
    		MysqlConnect mc = new MysqlConnect();    		conn = mc.connect();    stmt = conn.createStatement();
    		extractCauseEffectSentences();	//get cause sentences
    		
    		positiveWords = readFileIntoArrayList(positiveWords,positiveWordsFileName);
    		negativeWords = readFileIntoArrayList(negativeWords,negativeWordsFileName);
    		stopWords     = readFileIntoArrayList(stopWords,stopWordsFileName);
    		
    		for (String p: positiveWords) {
    			positiveWordCount.put(p, 0);
    		}
    		for (String n: negativeWords) {
    			negativeWordCount.put(n, 0);
    		}
    		wordCountInCauseSentences.put("test", 0);	wordCountInEffectSentences.put("test", 0);  
    		
    		//display and match these lists
    		int totalCauseSentences=0, totalEffectSentences=0, totalPositiveMatchedInCause=0, totalPositiveMatchedInEffect=0,totalNegativeMatchedInCause=0,totalNegativeMatchedInEffect=0;
    		for (String csl: causeSentencesLists) {
    			totalCauseSentences++;
    			
    			//go word by word
    			/*for(String word : csl.split(" ")) {
    				for (Map.Entry<String, Integer> entry : wordCountInCauseSentences.entrySet()) {
        			    //System.out.println(entry.getKey() + " = " + entry.getValue());
        				if (word.equals(entry.getKey()) ){		//contains term	(key)
        					//wordCountInCauseSentences.put(entry.getKey(), entry.getValue()+1);  
    	        		} 
        				else {
        					wordCountInCauseSentences.put(word, 1);   
        				}
        			}
    			}
    			*/
    			// count occurrences    	        
    	        for(String word : csl.split(" "))
    	        {
    	            String next = word.toLowerCase();
    	            if(InStopWordList(next)==false) {
	    	            if (!wordCountInCauseSentences.containsKey(next)) {
	    	            	wordCountInCauseSentences.put(next, 1);
	    	            } else {
	    	            	wordCountInCauseSentences.put(next, wordCountInCauseSentences.get(next) + 1);
	    	            }
    	            }
    	        }
    			
    			//counts all terms in all sentences
    			for (Map.Entry<String, Integer> entry : positiveWordCount.entrySet()) {
    			    //System.out.println(entry.getKey() + " = " + entry.getValue());
    				if (csl.contains(entry.getKey()) ){		//contains term	(key)
    					positiveWordCount.put(entry.getKey(), entry.getValue()+1);  
	        		} 
    			}
    			
    			//counts all terms in all sentences
    			for (Map.Entry<String, Integer> entry : negativeWordCount.entrySet()) {
    			    //System.out.println(entry.getKey() + " = " + entry.getValue());
    				if (csl.contains(entry.getKey()) ){		//contains term	(key)
    					negativeWordCount.put(entry.getKey(), entry.getValue()+1);  
	        		} 
    			}
    			
	    		for (String pw : positiveWords) {
	        		if (csl.contains(pw) ){				
	        			totalPositiveMatchedInCause++;
	        			break;	//just need to match a term once in a sentence
	        		} // end if
	    		}
	    		for (String nw : negativeWords) {
	        		if (csl.contains(nw) ){				
	        			totalNegativeMatchedInCause++;
	        			break;	//just need one term to  match per sentence
	        		} // end if
	    		}
		    }
    		for (String esl: effectSentencesList) {
    			totalEffectSentences++;
    			
    			/* for(String word : esl.split(" ")) {
    				for (Map.Entry<String, Integer> entry : wordCountInEffectSentences.entrySet()) {
        			    //System.out.println(entry.getKey() + " = " + entry.getValue());
        				if (word.equals(entry.getKey()) ){		//contains term	(key)
  //      					wordCountInEffectSentences.put(entry.getKey(), entry.getValue()+1);  
    	        		} 
        				else {
        					wordCountInEffectSentences.put(word, 1);   
        				}
        			}
    			}
    			*/
    			for(String word : esl.split(" ")){
    				String next = word.toLowerCase();
    				if(InStopWordList(next)==false) {
	    	            if (!wordCountInEffectSentences.containsKey(next)) {
	    	            	wordCountInEffectSentences.put(next, 1);
	    	            } else {
	    	            	wordCountInEffectSentences.put(next, wordCountInEffectSentences.get(next) + 1);
	    	            }
    				}
    	        }
    			
    			//counts all terms in all sentences
    			for (Map.Entry<String, Integer> entry : positiveWordCount.entrySet()) {
    			    //System.out.println(entry.getKey() + " = " + entry.getValue());
    				if (esl.contains(entry.getKey()) ){		//contains term	(key)
    					positiveWordCount.put(entry.getKey(), entry.getValue()+1);  
	        		} 
    			}
    			
    			//counts all terms in all sentences
    			for (Map.Entry<String, Integer> entry : negativeWordCount.entrySet()) {
    			    //System.out.println(entry.getKey() + " = " + entry.getValue());
    				if (esl.contains(entry.getKey()) ){		//contains term	(key)
    					negativeWordCount.put(entry.getKey(), entry.getValue()+1);  
	        		} 
    			}
    			
    			
	    		for (String pw : positiveWords) {
	        		if (esl.contains(pw) ){				
	        			totalPositiveMatchedInEffect++;
	        			break;	//just need one term to  match per sentence
	        		} // end if
	    		}
	    		for (String nw : negativeWords) {
	        		if (esl.contains(nw) ){				
	        			totalNegativeMatchedInEffect++;
	        			break;	//just need one term to  match per sentence
	        		} // end if
	    		}
		    }
    		
    		System.out.println(" Total Cause Sentences: "  + totalCauseSentences + "  Total positive Matched in Cause: "+totalPositiveMatchedInCause + " Total negative Matched in Cause: "+totalNegativeMatchedInCause);	
    		System.out.println(" Total Effect Sentences: " + totalEffectSentences + " Total positive Matched in Cause: "+totalPositiveMatchedInEffect + " Total negative Matched in Cause: "+totalNegativeMatchedInEffect);	
    		
    		//order map
//    		System.out.println("Before sorting......");
//    	    printMap(positiveWordCount);
//  	        System.out.println("After sorting ascending order......");
//  	        Map<String, Integer> sortedMapAsc = sortByComparator(positiveWordCount, ASC);
//  	        printMap(sortedMapAsc);
   	        System.out.println("After sorting positiveWordCount in descindeng order......");
   	        Map<String, Integer> sortedMapDesc = sortByComparator(positiveWordCount, DESC);
   	        printMap(sortedMapDesc); 
   	        System.out.println("After sorting negativeWordCount in descindeng order......");
	        Map<String, Integer> sortedMapDesc2 = sortByComparator(negativeWordCount, DESC);
	        printMap(sortedMapDesc2); 
	        
//	        System.out.println("After sorting allWordCount in descindeng order......");
//	        Map<String, Integer> sortedMapDesc3 = sortByComparator(wordCountInCauseSentences, DESC);
//	        printMap(sortedMapDesc3);
//	        System.out.println("After sorting allWordCount in descindeng order......");
//	        Map<String, Integer> sortedMapDesc4 = sortByComparator(wordCountInEffectSentences, DESC);
//	        printMap(sortedMapDesc4);
	        System.out.println("After sorting WordCount in Cause Sentences in descendeng order......");
	        Map<String, Integer> sortedMapDesc5 = sortByComparator(wordCountInCauseSentences, DESC);
	        printMap(sortedMapDesc5);
	        System.out.println("After sorting WordCount in Effect Sentences in descendeng order......");
	        Map<String, Integer> sortedMapDesc6 = sortByComparator(wordCountInEffectSentences, DESC);
	        printMap(sortedMapDesc6); 
    		mc.disconnect();
 	    } catch (Exception ex) {
 	    	System.out.println("Error " + ex.getMessage() +ex.toString());
 	    	System.out.println(StackTraceToString(ex)  );	
 	    }
    }	
	
	public static boolean InStopWordList(String word) {
		for(String sw: stopWords) {
			if(word.equals(sw))
				return true;
		}
		return false;
	}
	
	private static Map<String, Integer> sortByComparator(Map<String, Integer> unsortMap, final boolean order)
    {
        List<Entry<String, Integer>> list = new LinkedList<Entry<String, Integer>>(unsortMap.entrySet());
        // Sorting the list based on values
        Collections.sort(list, new Comparator<Entry<String, Integer>>()
        {
            public int compare(Entry<String, Integer> o1,
                    Entry<String, Integer> o2)
            {
                if (order)
                {
                    return o1.getValue().compareTo(o2.getValue());
                }
                else
                {
                    return o2.getValue().compareTo(o1.getValue());
                }
            }
        });
        // Maintaining insertion order with the help of LinkedList
        Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
        for (Entry<String, Integer> entry : list)
        {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }
    public static void printMap(Map<String, Integer> map)
    {
        for (Entry<String, Integer> entry : map.entrySet())  {
        	if(entry.getValue()>0)
        		System.out.println("Key : " + entry.getKey() + " Value : "+ entry.getValue());
        }
    }	
	
	//lets first try on allmessages_ideas table first rather than allmessages table
	private static void extractCauseEffectSentences()  {
		Statement stmt2 =null;		
		int csl=0,esl=0;
		try{
			String sql = "select causeSentence, effectSentence from trainingData;"; // where pep = -1;";	// distinct		BETWEEN 200 AND 300 
			ResultSet rs = stmt.executeQuery(sql);	// limit 100	
				while (rs.next()) { // System.out.println("PEP:");
					
					String causeSentence = rs.getString(1); 		String effectSentence  = rs.getString(2);
					if(causeSentence==null || causeSentence.equals("") || causeSentence.isEmpty()) {}
					else {
						causeSentencesLists.add(causeSentence);		csl++;
					}
					if(effectSentence==null || effectSentence.equals("")  || effectSentence.isEmpty() ) {}
					else {
						effectSentencesList.add(effectSentence);	esl++;
					}
				}
				System.out.println("Total csl: " +csl + " esl: " +esl);	
		 } catch (Exception ex) {
	 	    	System.out.println("Error " + ex.getMessage() +ex.toString());
	 	    	System.out.println(StackTraceToString(ex)  );	
	 	 }
	}
	
	public static List<String> readFileIntoArrayList(List<String> positiveWords,String positiveWordsFileName) throws IOException {
		BufferedReader in = null;		
		try {   
		    in = new BufferedReader(new FileReader(positiveWordsFileName));
		    String str;
		    while ((str = in.readLine()) != null) {
		    	if (!str.startsWith(";")) {
		    		if(str==null || str.equals("")  || str.isEmpty() ) {}
		    		else {
		    			positiveWords.add(str);
		    		}
		    	}
		    }
		} catch (FileNotFoundException e) {
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		} finally {
		    if (in != null) {
		        in.close();
		    }
		}
		return positiveWords;
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
