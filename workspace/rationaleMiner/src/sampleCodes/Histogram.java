package sampleCodes;


import java.util.*;

import connections.MysqlConnect;

import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;
import java.io.*;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import static java.util.stream.Collectors.*;
import static java.util.Map.Entry.*;


/** 
 * A program to generate a histogram of words in a text document.
 * 
 * Given a filename on the command line, this program will output
 * a list of "word: freq" pairs, where "word" is a word appearing in
 * text file and "freq" is the number of occurrences of that word.
 * 
 * The results are printed in alphabetical order, by word.
 * 
 * All words are converted to lower case so "Day" and "day" would
 * count as two occurrences of the word "day".
 *
 */
//alos more help on stemming, stopwords etc from here ..https://github.com/harryaskham/Twitter-L-LDA/blob/master/util/Stopwords.java
public class Histogram {
	public static void main(String[] args) {
		Map<String,Integer> freq = new TreeMap<String,Integer>();
		MysqlConnect mc = new MysqlConnect();		Connection connection = mc.connect();
		String tablename = 	"trainingdata";
		Integer sum=0,totalCounter=0;		
		try	{
			String query= "SELECT pep, causesentence  FROM trainingdata where label like '%rejected%';";
			Statement st = connection.createStatement();			ResultSet rs = st.executeQuery(query);
			int pep; String sentence;			
			while (rs.next()){	
				pep = rs.getInt("pep");	sentence = rs.getString("causesentence");			
				//System.out.println(pep+" "+ sentence);
				if(sentence == null || sentence.isEmpty()) {}
				else {
					sentence = sentence.replaceAll("\\p{P}", " ").toLowerCase();	//remove all punctuation
					//sentence = sentence.replaceAll("\\s+", " ").trim(); //remove double spaces and trim
					String[] g = sentence.trim().toLowerCase().split(" ");
					//a list for each sentence so we have one term for each sentence
					List<String> sentenceTerms = new ArrayList<String>();
					//for (String y : sentence.trim().toLowerCase().split(" ")) {
					//	sentenceTerms.add(y);
					//}
					
					for (String s : g) {
						String word = s.toLowerCase();						
						//first check if terms is in sentence list
						if(sentenceTerms.contains(word)) {}
						else {
							sentenceTerms.add(word);
							// iterate through the words, maintaining a count in the Map
		//					while (s.hasNext()) {
		//						String word = s.next().toLowerCase();
								if (freq.containsKey(word)) {								
									Integer i = freq.get(word);// only need to bump up the count
									freq.put(word, i + 1);					
								} else {								
									freq.put(word, 1);// insert a new binding
								}
						}
	//					}
					}
				}
				
			}
			/*
			// let's sort this map by values first
			Map<String, Integer> sorted = freq.entrySet().stream().sorted(comparingByValue()).collect(toMap(e -> e.getKey(), e -> e.getValue(), (e1, e2) -> e2, LinkedHashMap::new));
			// iterate through the key-value bindings, printing them out
			PrintWriter w = new PrintWriter(System.out);
			//for (int i=50; i>=0;i++) {
				for (Map.Entry<String, Integer> kv : sorted.entrySet()) {
					//if(kv.getValue().intValue()==i)
						w.printf("%s , %d\n", kv.getKey(), kv.getValue().intValue());
				}
			//}
			*/	
			//remove stopwords
			String[] stopwords = {"a", "as", "able", "about", "above", "according", "accordingly", "across", "actually", "after", "afterwards", "again", "against", "aint", "all", "allow", "allows", "almost", "alone", "along", "already", "also", "although", "always", "am", "among", "amongst", "an", "and", "another", "any", "anybody", "anyhow", "anyone", "anything", "anyway", "anyways", "anywhere", "apart", "appear", "appreciate", "appropriate", "are", "arent", "around", "as", "aside", "ask", "asking", "associated", "at", "available", "away", "awfully", "be", "became", "because", "become", "becomes", "becoming", "been", "before", "beforehand", "behind", "being", "believe", "below", "beside", "besides", "best", "better", "between", "beyond", "both", "brief", "but", "by", "cmon", "cs", "came", "can", "cant", "cannot", "cant", "cause", "causes", "certain", "certainly", "changes", "clearly", "co", "com", "come", "comes", "concerning", "consequently", "consider", "considering", "contain", "containing", "contains", "corresponding", "could", "couldnt", "course", "currently", "definitely", "described", "despite", "did", "didnt", "different", "do", "does", "doesnt", "doing", "dont", "done", "down", "downwards", "during", "each", "edu", "eg", "eight", "either", "else", "elsewhere", "enough", "entirely", "especially", "et", "etc", "even", "ever", "every", "everybody", "everyone", "everything", "everywhere", "ex", "exactly", "example", "except", "far", "few", "ff", "fifth", "first", "five", "followed", "following", "follows", "for", "former", "formerly", "forth", "four", "from", "further", "furthermore", "get", "gets", "getting", "given", "gives", "go", "goes", "going", "gone", "got", "gotten", "greetings", "had", "hadnt", "happens", "hardly", "has", "hasnt", "have", "havent", "having", "he", "hes", "hello", "help", "hence", "her", "here", "heres", "hereafter", "hereby", "herein", "hereupon", "hers", "herself", "hi", "him", "himself", "his", "hither", "hopefully", "how", "howbeit", "however", "i", "id", "ill", "im", "ive", "ie", "if", "ignored", "immediate", "in", "inasmuch", "inc", "indeed", "indicate", "indicated", "indicates", "inner", "insofar", "instead", "into", "inward", "is", "isnt", "it", "itd", "itll", "its", "its", "itself", "just", "keep", "keeps", "kept", "know", "knows", "known", "last", "lately", "later", "latter", "latterly", "least", "less", "lest", "let", "lets", "like", "liked", "likely", "little", "look", "looking", "looks", "ltd", "mainly", "many", "may", "maybe", "me", "mean", "meanwhile", "merely", "might", "more", "moreover", "most", "mostly", "much", "must", "my", "myself", "name", "namely", "nd", "near", "nearly", "necessary", "need", "needs", "neither", "never", "nevertheless", "new", "next", "nine", "no", "nobody", "non", "none", "noone", "nor", "normally", "not", "nothing", "novel", "now", "nowhere", "obviously", "of", "off", "often", "oh", "ok", "okay", "old", "on", "once", "one", "ones", "only", "onto", "or", "other", "others", "otherwise", "ought", "our", "ours", "ourselves", "out", "outside", "over", "overall", "own", "particular", "particularly", "per", "perhaps", "placed", "please", "plus", "possible", "presumably", "probably", "provides", "que", "quite", "qv", "rather", "rd", "re", "really", "reasonably", "regarding", "regardless", "regards", "relatively", "respectively", "right", "said", "same", "saw", "say", "saying", "says", "second", "secondly", "see", "seeing", "seem", "seemed", "seeming", "seems", "seen", "self", "selves", "sensible", "sent", "serious", "seriously", "seven", "several", "shall", "she", "should", "shouldnt", "since", "six", "so", "some", "somebody", "somehow", "someone", "something", "sometime", "sometimes", "somewhat", "somewhere", "soon", "sorry", "specified", "specify", "specifying", "still", "sub", "such", "sup", "sure", "ts", "take", "taken", "tell", "tends", "th", "than", "thank", "thanks", "thanx", "that", "thats", "thats", "the", "their", "theirs", "them", "themselves", "then", "thence", "there", "theres", "thereafter", "thereby", "therefore", "therein", "theres", "thereupon", "these", "they", "theyd", "theyll", "theyre", "theyve", "think", "third", "this", "thorough", "thoroughly", "those", "though", "three", "through", "throughout", "thru", "thus", "to", "together", "too", "took", "toward", "towards", "tried", "tries", "truly", "try", "trying", "twice", "two", "un", "under", "unfortunately", "unless", "unlikely", "until", "unto", "up", "upon", "us", "use", "used", "useful", "uses", "using", "usually", "value", "various", "very", "via", "viz", "vs", "want", "wants", "was", "wasnt", "way", "we", "wed", "well", "were", "weve", "welcome", "well", "went", "were", "werent", "what", "whats", "whatever", "when", "whence", "whenever", "where", "wheres", "whereafter", "whereas", "whereby", "wherein", "whereupon", "wherever", "whether", "which", "while", "whither", "who", "whos", "whoever", "whole", "whom", "whose", "why", "will", "willing", "wish", "with", "within", "without", "wont", "wonder", "would", "would", "wouldnt", "yes", "yet", "you", "youd", "youll", "youre", "youve", "your", "yours", "yourself", "yourselves", "zero"};
			//Set<String> stopWordSet = new HashSet<String>(Arrays.asList(stopwords));
			List<String> stopwordsE = new ArrayList<String>(Arrays.asList(stopwords));			
			freq.keySet().removeAll(stopwordsE);			
			for (int u=0; u < 5000; u++) {	//remove all numbers from set
				freq.keySet().remove(Integer.toString(u));
			}
			
			Map<String, Integer> sortedMap = sortByValue(freq);
	        printMap(sortedMap);	
				
				
			st.close();
		}	
			catch (Exception e)	{
			System.err.println("Got an exception! ");			System.err.println(e.getMessage());
			System.out.println(StackTraceToString(e)  );
		}
	}
	private static Map<String, Integer> sortByValue(Map<String, Integer> unsortMap) {
        // 1. Convert Map to List of Map
        List<Map.Entry<String, Integer>> list =
                new LinkedList<Map.Entry<String, Integer>>(unsortMap.entrySet());
        // 2. Sort list with Collections.sort(), provide a custom Comparator
        //    Try switch the o1 o2 position for a different order
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });
        // 3. Loop the sorted list and put it into a new insertion order Map LinkedHashMap
        Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        /*
        //classic iterator example
        for (Iterator<Map.Entry<String, Integer>> it = list.iterator(); it.hasNext(); ) {
            Map.Entry<String, Integer> entry = it.next();
            sortedMap.put(entry.getKey(), entry.getValue());
        }*/
        return sortedMap;
    }
	public static <K, V> void printMap(Map<K, V> map) {
        for (Map.Entry<K, V> entry : map.entrySet()) {
            System.out.println(entry.getKey() +" , " + entry.getValue());
        }
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
