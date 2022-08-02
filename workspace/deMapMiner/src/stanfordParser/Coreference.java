package stanfordParser;
//jan 2019, this is coreference script for version 3.9..we just placed it here to write simialr code, one line by line
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
/*
import edu.stanford.nlp.coref.CorefCoreAnnotations;
import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.coref.data.Mention; */
import edu.stanford.nlp.dcoref.CorefChain.CorefMention;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations.CorefChainAnnotation;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations.CorefClusterIdAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentenceIndexAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
//dec 2018, noe we only check paragraphs which have a term from the subjects array - which should reduce the processing 
public class Coreference {
	static StanfordCoreNLP pipeline;
	static Properties props;
	static Annotation document;
	static String subjects[] = {"pep","proposal","bdfl","guido","john","judy","obama"};
	static ArrayList<tripair> tripairList = new ArrayList<tripair>();
	
	public Coreference() {		
		document = new Annotation("");
		props = new Properties();
		props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,parse,mention,coref");//props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,parse,mention,coref");
		pipeline = new StanfordCoreNLP(props);
	}
	
	public static void main(String[] args) throws Exception {	
		init();
		String text = "Barack Obama was born in Hawaii.  He is the president. Obama was elected in 2008.";
		//"The pep was not accepted. It didn't have consensus."
		String text0 = "John drove Judy home. He made her dinner.";
		String text1 = " The history of PEP 289 is a good example of this. It was originally rejected because of poor syntax and a lack of use cases. It was " 
				+ "recently revived with much better syntax and a small boatload of use cases, and it looks like it's on the fast path to being included into 2.4.";  
		String text2 = "This PEP was originally written for inclusion in Python 2.3. It was withdrawn after observation that substantially all of its benefits were subsumed by generator expressions coupled "
				+ "with the dict() constructor.";
		String text3 = "I have tweaked some text in the PEP and approved it.";
		  	
		System.out.println(".. Resolved text:"+ coreference(text));	/*coreference(text0);*/ 	//System.out.println(".. Resolved text:"+ resolveCoRef(text0));
		System.out.println(".. Resolved text:"+ coreference(text0));  	//		coreference(text);   	System.out.println(".. Resolved text:"+ resolveCoRef(text));
		System.out.println(".. Resolved text:"+ coreference(text1));	//		coreference(text1);		System.out.println(".. Resolved text:"+ resolveCoRef(text1));
		System.out.println(".. Resolved text:"+ coreference(text2));	//		coreference(text2);		System.out.println(".. Resolved text:"+ resolveCoRef(text2));
		System.out.println(".. Resolved text:"+ coreference(text3)); 	//		coreference(text3);		System.out.println(".. Resolved text:"+ resolveCoRef(text3));
		//new 
	}
	
	public static void init(){	System.out.println("init");
		document = new Annotation("");
		props = new Properties();
		props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,parse,mention,coref");
		pipeline = new StanfordCoreNLP(props);
	}

	//create function, it should be passed with sentence and next sentence
	// if coreference found in next sentence
	//send second sentence with coref term subsitituted with pep or subject
	public static String coreference(String text1) {		
		document = new Annotation(text1);		pipeline.annotate(document);
		System.out.println("\n---New Text: "+ text1);	System.out.println("coref chains");
		Integer sentenceCounter=1; String finalSentence = "";
		
		//populate tripair list
		String subjectMatchedString = "";	
	/*	for (CorefChain cc : document.get(CorefCoreAnnotations.CorefChainAnnotation.class).values()) {
			String d = cc.toString();			System.out.println("\t  cc " + d);
			String p =  d.substring(d.indexOf("[")+1,d.indexOf("]"));	System.out.println("\t  removed brackets " + p);
			String splits[] = p.split(",");
			for (String s: splits) {	System.out.println("\t  splitted sentences in coref " + s);				
				String q = s.replace(" in sentence", ""); //.replace("\"", "").trim();				
				//get everything in quote and assign it to value variable
				//trim whatever is left and assign to key value				
				System.out.println("\t\t remaining pair string "+q);
				String pairs[] = q.split(" ");
				// hmap.put(Integer.parseInt(pairs[1]), pairs[0]);		//int foo = Integer.parseInt(myString);
				// we create a new class object and store the 
				String kk = pairs[pairs.length-1].trim();	
				Integer k = Integer.parseInt(kk); 
				String v = q.replaceAll(kk,"").replace("\"", "").trim();    //String v = pairs[0]; 
				System.out.println("\t\t pairs length= "+(pairs.length-1)+" + kk "+kk + " value: "+ v);
				for (String sub : subjects) {
					if(v.toLowerCase().contains(sub.toLowerCase())) {
							subjectMatchedString=sub; 	System.out.println("\t\tsubjectMatchedString: "+ subjectMatchedString);
					}
				}				
				tripair tp = new tripair(k,subjectMatchedString,v);	System.out.println("\t\tpopulating tripair list; key: "+ k + ", value: " + v + ",subjectMatchedString "+subjectMatchedString);
				tripairList.add(tp);	//add to list of tripairs...
			}
		} */
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
	     for (CoreMap sentence : document.get(CoreAnnotations.SentencesAnnotation.class)) {	//for each sentence
	    	String tempSentence = sentence.toString();
			System.out.println("Sentence: "+sentence);			System.out.println("mentions");
			/*for (Mention m : sentence.get(CorefCoreAnnotations.CorefMentionsAnnotation.class)) {	//for each mention
				System.out.println("\t" + m);
			} */
			//iterate over the list of tripairs, if subjectFound= true and sentence counter = key				
			for(tripair tp : tripairList) {
				//if subject matched in previous loop, and sentencecounter matches, we replace
				keyNum =  tp.getKey(); val = tp.getValue(); sub = tp.getSubject();
				System.out.println("\tkey is: "+ keyNum + " & Value is: " + val + " subject " + sub);
				if(subjectMatched && keyNum==sentenceCounter) {
					//we replace
					tempSentence = tempSentence.replace(val,sub);	System.out.println("\t\ttempSentence: "+ tempSentence);
					//finalSentence += tempSentence;					System.out.println("\t\tfinalSentence: "+ tempSentence);
				}
				//if the key at current location is the subject we looking for, we replace the value with the subject thereafter
				for (String subj : subjects) {	//for each subject
					if(val.toLowerCase().contains(subj.toLowerCase())) {
						subjectMatched=true;	System.out.println("\t\tsubjectMatched: "+ subjectMatched);
					}
				}			
				
			//}
				
				
			}
			finalSentence += tempSentence + " ";					System.out.println("\t\tfinalSentence: "+ tempSentence);
			sentenceCounter++;
	     }
		return finalSentence;
	}
	
	public static String resolveCoRef(String text) {
		String resolved = new String();// to hold resolved string
		//Annotation document = runPipeline(text);// run the pipeline
		document = new Annotation(text);	pipeline.annotate(document);
	/*	Map<Integer, CorefChain> corefs = document.get(CorefCoreAnnotations.CorefChainAnnotation.class);	// get all coref chains and sentences
		List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
		//go over each sentence, over each token, adding to resolved string as it goes
		//
		for (CoreMap sentence : sentences) {	// process each sentence 	
			System.out.println("\tSentence: "+ sentence.toString());
			int curSentIdx = sentence.get(SentenceIndexAnnotation.class);	System.out.println("\tcurSentIdx: "+ curSentIdx); 
			List<CoreLabel> tokens = sentence.get(TokensAnnotation.class);	//get each token from the sentence
			boolean isPronoun = false;
			for (CoreLabel token : tokens) {	// process each token
				isPronoun = false;
				String pos = token.get(PartOfSpeechAnnotation.class);  //get part of speech
				if (pos.equals("PRP") || pos.equals("PP$")) {	//check for pronouns
					isPronoun = true;	System.out.println("\tIs Pronoun is True: "+ pos + ", token "+ token.toString());
				}
				Integer corefClustId = token.get(CorefClusterIdAnnotation.class);	System.out.println("\tcorefClustId: "+ corefClustId);
				CorefChain chain = corefs.get(corefClustId);	if (chain==null) {} else { System.out.println("\tchain: "+ chain.toString());}
				// if there is no chain to replace
				if (chain == null || chain.getMentionsInTextualOrder().size() == 1 // || isPronoun == false  
						) {
					resolved += token.word() + token.after();	System.out.println("\t resolved "+resolved);
				} else {
					int sentIndx = chain.getRepresentativeMention().sentNum - 1;	System.out.println("\tsentIndx: "+ sentIndx);
					edu.stanford.nlp.coref.data.CorefChain.CorefMention reprMent = chain.getRepresentativeMention();	System.out.println("\treprMent: "+ reprMent);
					String rootWord = sentences.get(sentIndx)
							.get(TokensAnnotation.class)
							.get(reprMent.headIndex - 1)
							.originalText();		System.out.println("\trootWord: "+ rootWord);
					if (curSentIdx != sentIndx || token.index() < reprMent.startIndex
							|| token.index() > reprMent.endIndex) {		System.out.println("\tif section ");
						if (Character.isUpperCase(token.originalText().charAt(0))) {	System.out.println("\tif if section ");
							//						rootWord = WordUtils.capitalize(rootWord);
						}
						resolved += rootWord + token.after();	System.out.println("\t if resolved "+resolved);
					} else {
						resolved += token.word() + token.after();	System.out.println("\t else resolved "+resolved);
					}
				}
			}
		} */
		return resolved;
	}
}