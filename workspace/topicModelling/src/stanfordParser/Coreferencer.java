package stanfordParser;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import edu.stanford.nlp.dcoref.CorefChain;
import edu.stanford.nlp.dcoref.CorefChain.CorefMention;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations.CorefChainAnnotation;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.util.IntPair;
//Jan 2019, works ok, but not for cases we need, run for case when string is pepText  
public class Coreferencer {
	static StanfordCoreNLP pipeline;
	static Properties props;
	static String subjects[] = {"pep","proposal","bdfl","guido","john","judy","obama"};
	static ArrayList<tripair> tripairList = new ArrayList<tripair>();
	//for 3.5.2 version of snlp
	
	public static void init() {
		props = new Properties();
		props.put("annotators", "tokenize,ssplit,pos,lemma,ner,parse,dcoref");
		pipeline = new StanfordCoreNLP(props);
	}
	
	public static void main(String[] args){
		
		//Properties props = new Properties();
		//props.put("annotators", "tokenize,ssplit,pos,lemma,ner,parse,dcoref");
		//pipeline = new StanfordCoreNLP(props);
		init();
//		String text="The women met for coffee ."
//				+ "The cafe reopened in a new location ."
//				+ "They wanted to catch up with each other . ";
		
//		String text = "The animal species became endangered ."
//				+ "Their habitat was destroyed ."
//				+ "Predators went extinct.";
		
		String textOrig = "The man wrote a will. " 
					+ "He was dying. "
					+ "He was a widower. ";
		String textOrig2 = "The man wrote a will. He was dying. He was a widower. ";
		String pepText = " The history of PEP 289 is a good example of this. " 
						+" It was originally rejected because of poor syntax and a lack of use cases. " 
						+" It was recently revived with much better syntax and a small boatload of use cases, and it looks like it's on the fast path to being included into 2.4. ";
		//c: CHAIN2-["The history of PEP 289" in sentence 1, "a good example of this" in sentence 1, "It" in sentence 2, "It" in sentence 3, "it" in sentence 3, "it" in sentence 3]
		//jan 2019, lets see how this script handles these sentences
		String texts[] = {"Barack Obama was born in Hawaii.  He is the president. Obama was elected in 2008.",
		//"The pep was not accepted. It didn't have consensus."
				"John drove Judy home. He made her dinner.",
				"The history of PEP 289 is a good example of this. It was originally rejected because of poor syntax and a lack of use cases. It was recently revived with much better syntax and a "
				+ " small boatload of use cases, and it looks like it's on the fast path to being included into 2.4.", 
				"This PEP was originally written for inclusion in Python 2.3. It was withdrawn after observation that substantially all of its benefits were subsumed by generator expressions coupled "
				+ " with the dict() constructor.",
				"I have tweaked some text in the PEP and approved it."};
//		for (String s: texts) {
		    // arraylist version
			//ArrayList<String> sentences = Coreferencer.getdCoreferencedText(pepText);		
			//for (String se :sentences){			System.out.print(se + " ");		}	System.out.println();
			String sentences = Coreferencer.getdCoreferencedText(pepText);	//string version
//		}
	}
		
	public static void perClusterUpdateSen(ArrayList<List<HasWord>> processedText,
			int common_sentNum, int representative_sentNum,
		int coreStartIndex, int coreEndIndex,
		int commonStartIndex, int commonEndIndex){
		
		List<HasWord> representative_sentence = processedText.get(representative_sentNum-1);
		List<HasWord> common_sentence = processedText.get(common_sentNum-1);
		
		HasWord replace = new Word();
		String replaceStr = "";
		for (int i = coreStartIndex-1; i < coreEndIndex - 1; i++){
			replaceStr += representative_sentence.get(i).toString();
			replaceStr += " ";
		}
		replace.setWord(replaceStr.trim());
		for (int i=commonStartIndex-1; i < commonEndIndex-1; i++){
			common_sentence.set(i,new Word());			common_sentence.get(i).setWord("");
		}
		common_sentence.set(commonStartIndex-1, replace);
		
	}

	//public static ArrayList<String> getdCoreferencedText(String text){
	public static String getdCoreferencedText(String text){
		Annotation document = new Annotation(text);
		pipeline.annotate(document);
		ArrayList<String> sentences = new ArrayList<String>();
		DocumentPreprocessor dp = new DocumentPreprocessor(	new StringReader(text));
		ArrayList<List<HasWord>> processedText = new ArrayList<List<HasWord>>();
		for (List<HasWord> sentence : dp){
			processedText.add(sentence);
		}
		Integer sentenceCounter=1; String finalSentence = "";	//jan 2019 added code
		//Ã§â€�Â¨ representative mention Ã¦Å Å  mentionÃ¦â€ºÂ¿Ã¦ï¿½Â¢Ã¦Å½â€°
		String subjectMatchedString = "";	
		Map<Integer, CorefChain> graph = document.get(CorefChainAnnotation.class);
		//populate tripair list
		boolean subFound=false; //we only add to list if subject found
		for (Map.Entry<Integer, CorefChain> entry : graph.entrySet()){
			CorefChain c = entry.getValue(); 
			//jan 2019 added code
			subFound=false;
			String d = c.toString(); 	
//			System.out.println("c: " + d);
			String p =  d.substring(d.indexOf("[")+1,d.indexOf("]"));	
//			System.out.println("\t  removed brackets " + p);
			String splits[] = p.split(",");
			for (String s: splits) {	
//				System.out.println("\t  splitted sentences in coref " + s);				
				String q = s.replace(" in sentence", ""); //.replace("\"", "").trim();				
				//get everything in quote and assign it to value variable
				//trim whatever is left and assign to key value				
//				System.out.println("\t\t remaining pair string "+q);
				String pairs[] = q.split(" ");
				// hmap.put(Integer.parseInt(pairs[1]), pairs[0]);		//int foo = Integer.parseInt(myString);
				// we create a new class object and store the 
				String kk = pairs[pairs.length-1].trim();	
				Integer k = Integer.parseInt(kk); 
				String v = q.replaceAll(kk,"").replace("\"", "").trim();    //String v = pairs[0]; 
//				System.out.println("\t\t pairs length= "+(pairs.length-1)+" + kk "+kk + " value: "+ v);
				for (String sub : subjects) {
					if(v.toLowerCase().contains(sub.toLowerCase())) {
							subjectMatchedString=sub; 	
//							System.out.println("\t\tsubjectMatchedString: "+ subjectMatchedString);
							subFound=true;
							//tripair tp = new tripair(k,subjectMatchedString,v);	System.out.println("\t\tpopulating tripair list; key: "+ k + ", value: " + v + ",subjectMatchedString "+subjectMatchedString);
							//tripairList.add(tp);	//add to list of tripairs...
					}
				}
				if (subFound) {	//we only add to list if subject found
					tripair tp = new tripair(k,subjectMatchedString,v);	
//					System.out.println("\t\tpopulating tripair list; key: "+ k + ", value: " + v + ",subjectMatchedString "+subjectMatchedString);
					tripairList.add(tp);	//add to list of tripairs...
				}
			}
			
			//end
			
			CorefMention cm = c.getRepresentativeMention();
			for (Entry<IntPair, Set<CorefMention>> e : 	c.getMentionMap().entrySet()){
				if (cm.endIndex - cm.startIndex >2){
					continue; //Ã¥Â¦â€šÃ¦Å¾Å“representative mention Ã¨Â¯ï¿½Ã¦â€¢Â°Ã¥Â¤Â§Ã¤ÂºÅ½2 Ã¥Â°Â±Ã¤Â¸ï¿½Ã¦ï¿½Â¢Ã¤Âºâ€ 
				}
				for(CorefMention mention : e.getValue()){
					perClusterUpdateSen(processedText, mention.sentNum,cm.sentNum,	cm.startIndex,cm.endIndex,	mention.startIndex,mention.endIndex);
				}
			}
		}
		//for each sentence
		// iterate through the tripair list
		//	if sentence counter matches the key
		//		if subjectmatched
		//			replace the value  in the sentence with the subject
		
		//it can happen that the subject exists in the paragraph, but its coreferenced
		//if we have found subject, meaning that the subject is part of the chain, we resolve	
		boolean subjectMatched=false;
		Integer keyNum =  null; String val = ""; String sub = "";
		System.out.println("Sentence Matching ###");
		for (List<HasWord> senlist : processedText){
			sentences.add("");
			for (HasWord word:senlist){
				if (!word.toString().equals("")){
					//System.out.print(word.toString()+" ");
					//String str = sentences.get(sentences.size()-1) + word.toString().toLowerCase()+" ";
					String str = sentences.get(sentences.size()-1) + word.toString()+" ";
					sentences.set(sentences.size()-1, str);
				}
			}
			
			//System.out.println();
		}
		for (int i=0; i < sentences.size(); i++){
			String tempSentence = sentences.get(i);
			System.out.println("Sentence: "+tempSentence);			//how can we get mentions? System.out.println("mentions");
			sentences.set(i, (""+tempSentence.charAt(0)).toUpperCase() + tempSentence.substring(1)) ;		
			//jan 2019
			for(tripair tp : tripairList) {
				//if subject matched in previous loop, and sentencecounter matches, we replace
				keyNum =  tp.getKey(); val = tp.getValue(); sub = tp.getSubject();
//				System.out.println("\tkey is: "+ keyNum + " & Value is: " + val + " subject " + sub);
				if(subjectMatched && keyNum==sentenceCounter) {
					//we replace
					tempSentence = tempSentence.replace(val,sub);	
//					System.out.println("\t\ttempSentence: "+ tempSentence);
					//finalSentence += tempSentence;					System.out.println("\t\tfinalSentence: "+ tempSentence);
				}
				//if the key at current location is the subject we looking for, we replace the value with the subject thereafter
				for (String subj : subjects) {	//for each subject
					if(val.toLowerCase().contains(subj.toLowerCase())) {
						subjectMatched=true;	
//						System.out.println("\t\tsubjectMatched: "+ subjectMatched);
					}
				}
			//}
			}
			finalSentence += tempSentence + " ";					System.out.println("\t\tfinalSentence: "+ tempSentence);
			sentenceCounter++;
		}
		System.out.println("\t\tfinalSentence b5 returning: "+ finalSentence);
		return finalSentence; //return sentences;
	}
}
