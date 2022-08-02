package stanfordParser;

import java.util.ArrayList;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

//import EmbeddedToken;

//import StanfordTest.EmbeddedToken;

//import org.apache.commons.lang3.text.WordUtils;

import edu.stanford.nlp.dcoref.CorefChain;
import edu.stanford.nlp.dcoref.CorefChain.CorefMention;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations.CorefChainAnnotation;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations.CorefClusterIdAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentenceIndexAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

//import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.util.CoreMap;
//import stanfordParser.EmbeddedToken;
//import stanfordParser.StanfordNLPTools;

import java.net.*;
import java.io.*;
//Feb 2019,,,copied this script to be part of Topic Modelling
//jan 2019..we added a function to get the verbs from a sentence, 
//this will only be used to identify verbs in Relation extraction and remove the rest of the terms
//dec 2018..updated with new code
public class StanfordNLPTools {
	// singleton instance
	private static StanfordNLPTools nlp = null;	
	private static StanfordCoreNLP pipeline;
	static String subjects[] = {"pep","proposal","bdfl","guido","john","judy","obama"};
	static ArrayList<tripair> tripairList = new ArrayList<tripair>();
	static Annotation document;
	static Properties props;
	
	static String str = "";
	
	public StanfordNLPTools() {
		// load all required models
//		Properties props = new Properties();
//		props.put("annotators", "tokenize, ssplit, pos, lemma, ner, depparse, parse, dcoref");
//		props.put("enforceRequirements", false);
//		pipeline = new StanfordCoreNLP(props);
		init();
	}	

	private static Annotation runPipeline(String text) {
		document = new Annotation(text);// create an empty Annotation just with the given text		
		pipeline.annotate(document);// run pipeline on this text
		return document;
	}

	public static void init(){		
		//StanfordNLPTools nlp = new StanfordNLPTools();// to perform any NLP operations required
		props = new Properties();
		props.put("annotators", "tokenize, ssplit, pos, lemma, ner, depparse, parse, dcoref");
		props.put("enforceRequirements", false);
		pipeline = new StanfordCoreNLP(props);
	}

	public static String returnCoreferencedText(String text){
		//StanfordNLPTools nlp = StanfordNLPTools.getInstance();// to perform any NLP operations required
		String coRefText = nlp.resolveCoRef(text);// resolve co reference
		System.out.println(coRefText);
		return coRefText;
	}
	
//	public static StanfordNLPTools getInstance() {
//		if (nlp == null)
//			nlp = new StanfordNLPTools();
//		return nlp;
//	}

	//this code only works in stanford nlp version 3.5.2
	public static void main(String[] args) throws Exception {
		//StanfordNLPTools nlp = StanfordNLPTools.getInstance();// to perform any NLP operations required
		//getInstance();
//		Properties props = new Properties();
//		props.put("annotators", "tokenize, ssplit, pos, lemma, ner, depparse, parse, dcoref");
//		props.put("enforceRequirements", false);
//		pipeline = new StanfordCoreNLP(props);
		init();
		System.out.println("test: " );
		try {
			/*
			ServerSocket ss = new ServerSocket(10450); // 10450 = port number
			System.out.println("Stanford NLP Tools Server Setup success: ");
			Socket clientSocket = ss.accept();
			DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());
			DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
			while(true){
				String input = dis.readUTF(),output="";
//				System.out.println("input: " + input);
				String libraryToCheck = input.substring(0,1);	//get first letter
				input =input.substring(1);
//				System.out.println("sentence: " + input);
				if(libraryToCheck.equals("N")){		//ner
					//String[] tests = {"Partial invoice (€100,000, so roughly 40%) for the consignment C27655 we shipped on 15th August to London from the Make Believe Town depot. "
					///	+ "INV2345 is for the balance.. Customer contact (Sigourney) says they will pay this on the usual credit terms (30 days)."	};
					String[] tests = {input};
					extractEntities(pipeline, tests);
					output = str;
					//we must find someway to retun this information
				}				
				else if(libraryToCheck.equals("T")){		//return doubles or triples
					//extract doubles - noun verb
//					System.out.println("Matching Doubles/Triples: " );
					Boolean showGraphs=false;
					//String text = "Propose rejection of PEP";
					output = matchDoublesTripleInSentence(input,showGraphs);
					System.out.println("Matching Doubles/Triples Result at Server:" + output);
				}				
				else if(libraryToCheck.equals("C")){	//coreference
					//Show coreference
					System.out.println("Checking Coreference: " );
					//String text1 = "John drove to Judy’s house. He made her dinner.";
					//String text2 = "Barack Obama was born in Hawaii.  He is the president. Obama was elected in 2008. Barack Obama was born in Hawaii.  He is the president. Obama was elected in 2008.";
					output = resolveCoRef(input);
					System.out.println("Coref Output at Server:" + output);
				}				
				//write back
				dos.writeUTF(output);
			}
			*/
			//String input = "John drove to Judy’s house. He made her dinner.";
			//"The pep was not accepted." ,"It didn't have consensus."
			//"The pep was not accepted." ,"John didn't have consensus."			
			//The history of PEP 289 is a good example of this
			String input = "PEP 289 is a good example of this. It was originally rejected because of poor syntax and a lack of use cases. It was " 
					  + "recently revived with much better syntax and a small boatload of use cases, and it looks like it's on the fast path to being included into 2.4.";  
			
			//String input = "I've tweaked some text in this PEP and approved it..";
			System.out.println("input: " + input);
//			String output = resolveCoRef(input);
//			System.out.println("output: " + output);
			String inputX = "I have tweaked some text in the PEP and approved it";
//			System.out.println("inputX: " + inputX);String outputX = resolveCoRef(inputX);	System.out.println("outputX: " + outputX);
//			System.out.println("new coref: " + coreference(input));
			//jan 2019, return all verbs
			pair p = new pair(); p = returnAllVerbsAndNounsInSentence(inputX,p);
			System.out.println("Return All Verbs In Sentence: ("+inputX+") " + p.getVerb());
			
		} catch (Exception e) {
			System.out.println("Exception: " + e);
			System.out.println(StackTraceToString(e)  );	
		}	
		//		String str = "I 've tweaked some text in this PEP and approved it .";
		//			String coRefText = nlp.resolveCoRef(str);
		//			System.out.println(coRefText);
		
		//Scanner s=new Scanner(System.in);
		//System.out.println("Press enter to exit.....");
		//s.nextLine();	
	}

	public static String StackTraceToString(Exception ex) {
		String result = ex.toString() + "\n";
		StackTraceElement[] trace = ex.getStackTrace();
		for (int i=0;i<trace.length;i++) {
			result += trace[i].toString() + "\n";
		}
		return result;
	}
	//NER
	public static void extractEntities(String input) {	//StanfordCoreNLP pipeline, 
		String[] tests = {input};
		List tokens = new ArrayList<>();
		for (String s : tests) {
			// run all Annotators on the passed-in text
			Annotation document = new Annotation(s);			pipeline.annotate(document);
			// these are all the sentences in this document
			// a CoreMap is essentially a Map that uses class objects as keys and has values with
			// custom types
			List<CoreMap> sentences = document.get(SentencesAnnotation.class);
			StringBuilder sb = new StringBuilder();
			//I don't know why I can't get this code out of the box from StanfordNLP, multi-token entities
			//are far more interesting and useful..
			//TODO make this code simpler..
			for (CoreMap sentence : sentences) {
				// traversing the words in the current sentence, "O" is a sensible default to initialise
				// tokens to since we're not interested in unclassified / unknown things..
				String prevNeToken = "O",currNeToken = "O";
				boolean newToken = true;
				for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
					currNeToken = token.get(NamedEntityTagAnnotation.class);
					String word = token.get(TextAnnotation.class);
					// Strip out "O"s completely, makes code below easier to understand
					if (currNeToken.equals("O")) {
						// LOG.debug("Skipping '{}' classified as {}", word, currNeToken);
						if (!prevNeToken.equals("O") && (sb.length() > 0)) {
							handleEntity(prevNeToken, sb, tokens);	//System.out.println("here 77"); 							
							newToken = true;
						}
						continue;
					}
					if (newToken) {
						prevNeToken = currNeToken;		newToken = false;	sb.append(word);			continue;
					}
					if (currNeToken.equals(prevNeToken)) {
						sb.append(" " + word);
					} else {
						// We're done with the current entity - print it out and reset
						// TODO save this token into an appropriate ADT to return for useful processing..
						handleEntity(prevNeToken, sb, tokens);						newToken = true;
					}
					prevNeToken = currNeToken;
				}
			}
			//TODO - do some cool stuff with these tokens!
			System.out.println("We extracted "+tokens.size()+" tokens of interest from the input text");
		}
	}

	private static void handleEntity(String inKey, StringBuilder inSb, List inTokens) {
		System.out.println("Entity ("+ inSb + ") Type ("+inKey+")"); 
		//LOG.debug("'{}' is a {}", inSb, inKey);
		inTokens.add(new EmbeddedToken(inKey, inSb.toString()));
		inSb.setLength(0);
		//prepare string to return
		str= str+inKey+"!"+inSb+"|";
	}

	public static String matchDoublesTripleInSentence(String text, Boolean showGraphs) {
		text = text.toLowerCase();	//just making sure
		String svo="";	
		Annotation document = runPipeline(text);	// run the pipeline
		// these are all the sentences in this document
		// a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		String subject = "",subjectTerm = "",relation = "",relationTerm="",objectTerm = "";
		for(CoreMap sentence: sentences) 		{
			//we are interested in the first subject or object in each sentence
			boolean foundFirst=false,foundSecond=false, foundThird=false;
			subject = subjectTerm =relation =relationTerm=objectTerm = "";
			System.out.println("");
			// traversing the words in the current sentence
			// a CoreLabel is a CoreMap with additional token-specific methods
			for (CoreLabel token: sentence.get(TokensAnnotation.class)) 			{				
				String word = token.get(TextAnnotation.class);// this is the text of the token				
				String pos = token.get(PartOfSpeechAnnotation.class);// this is the POS tag of the token				
				String ne = token.get(NamedEntityTagAnnotation.class);// this is the NER label of the token
				//        System.out.println("word: " + word + " pos: " + pos + " ne:" + ne);
				if(foundFirst && foundSecond && foundThird){					break;				}

				if(pos.contains("NN")){
					subject = pos;
					if(!foundThird && foundFirst){						objectTerm = word;						foundThird=true;					}
					if(!foundFirst){						subjectTerm = word;						foundFirst=true;					}

				}
				if(pos.contains("VB") || pos.contains("VBG") || pos.contains("VBN")){
					relation = pos;					relationTerm = word;					foundSecond=true;
				}

				//System.out.println("\tSentence subjectTerm: " + subjectTerm + " relationTerm: " + relationTerm);
			}
			System.out.println("Sentence	[" + sentence.toString() +"] "
					+ "subjectTerm: (" + subjectTerm + ") relationTerm: (" + relationTerm+") objectTerm: (" + objectTerm+") ");

			if(showGraphs)			{
				// this is the parse tree of the current sentence
				Tree tree = sentence.get(TreeAnnotation.class);
				System.out.println("parse tree:\n" + tree);

				// this is the Stanford dependency graph of the current sentence
//				SemanticGraph dependencies = sentence.get(CollapsedCCProcessedDependenciesAnnotation.class);
//				System.out.println("dependency graph:\n" + dependencies);

				//added
				System.out.println("Root");
				Tree tree2 = sentence.get(TreeAnnotation.class);
				List<Tree> leaves = new ArrayList<>();
				leaves = tree2.getLeaves(leaves);
				for (Tree leave : leaves) {
					String compare = leave.toString().toLowerCase();
					//String pos = leave.
					System.out.println("leave.toString() " + compare);
					if(compare.equals("bottles") == true) {
						System.out.println(tree);			System.out.println("---");		System.out.println(leave);		System.out.println(leave.parent(tree2));
						System.out.println((leave.parent(tree2)).parent(tree2));
					}
				}
				//noun phrases - from another source - may not work
				//      String parse  = "(ROOT (S (NP (DT The) (NN dog)) (VP (VBD ran) (PP (IN after) (NP (DT the) (JJ intruding) (JJR bigger) (NN dog))))))";
				//      String target = "dog";
				//      String result = null;
				//      String regex  = "\\(NP \\s (?: \\( .+? \\) )* \\)"; // an NP contains an arbitrary number of sub-phrases
				//      Pattern patt  = Pattern.compile(regex, Pattern.COMMENTS);
				//      Matcher match = patt.matcher(parse);
				//      while(match.find() && result == null) {
				//          if (match.group().contains(target)) {
				//              result = match.group();
				//          }
				//      }
				//      if (result != null) {
				//          System.out.println(result);
				//      }      

			}
			// This is the coreference link graph
			// Each chain stores a set of mentions that link to each other,
			// along with a method for getting the most representative mention
			// Both sentence and token offsets start at 1!
			Map<Integer, CorefChain> graph = document.get(CorefChainAnnotation.class);
		}
		//System.out.println("---SVO "+subjectTerm + " " + relationTerm + " " + objectTerm);
		svo = subjectTerm + "," + relationTerm + "," + objectTerm;
		return svo;
	}
	
	//jan 2019, return all verbs from sentence, we use this for relation extraction to eliminate terms
	public static pair returnAllVerbsAndNounsInSentence(String text,pair p) {
		text = text.toLowerCase();	//just making sure
		String svo="", allVerbs="",allNouns="";		
		Annotation document = runPipeline(text);	// run the pipeline
		// these are all the sentences in this document
		// a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		String subject = "",subjectTerm = "",relation = "",relationTerm="",objectTerm = "";
		for(CoreMap sentence: sentences)	{
			//we are interested in the first subject or object in each sentence
			boolean foundFirst=false,foundSecond=false, foundThird=false;
			subject = subjectTerm =relation =relationTerm=objectTerm = "";
			//System.out.println("sentence: " + sentence.toString());
			// traversing the words in the current sentence
			// a CoreLabel is a CoreMap with additional token-specific methods
			for (CoreLabel token: sentence.get(TokensAnnotation.class))			{				
				String word = token.get(TextAnnotation.class);// this is the text of the token				
				String pos = token.get(PartOfSpeechAnnotation.class);	//System.out.println("word "+word+", pos : " + pos);	// this is the POS tag of the token				
				String ne = token.get(NamedEntityTagAnnotation.class);// this is the NER label of the token
				//        System.out.println("word: " + word + " pos: " + pos + " ne:" + ne);
//				if(foundFirst && foundSecond && foundThird){		break;				}
				if(pos.contains("NN")){		//System.out.println("\tword "+word+" is Noun : " + pos);	
					subject = pos;		allNouns = allNouns + " " + word;
//					if(!foundThird && foundFirst){						objectTerm = word;						foundThird=true;					}
//					if(!foundFirst){						subjectTerm = word;						foundFirst=true;					}
				}
				if(pos.contains("VB") || pos.contains("VBG") || pos.contains("VBN")){	//System.out.println("\tverb : " + pos);
					relation = pos;					relationTerm = word;					foundSecond=true;	allVerbs = allVerbs + " " + word;
				}
				//System.out.println("\tSentence subjectTerm: " + subjectTerm + " relationTerm: " + relationTerm);
			}
//			System.out.println("Sentence	[" + sentence.toString() +"] "
//					+ "subjectTerm: (" + subjectTerm + ") relationTerm: (" + relationTerm+") objectTerm: (" + objectTerm+") ");

			//we can add the showgraphs code as in the otehr block, but it took too much space, 
			// This is the coreference link graph
			// Each chain stores a set of mentions that link to each other,
			// along with a method for getting the most representative mention
			// Both sentence and token offsets start at 1!
//			Map<Integer, CorefChain> graph = document.get(CorefChainAnnotation.class);
		}
		//System.out.println("---SVO "+subjectTerm + " " + relationTerm + " " + objectTerm);
//		svo = subjectTerm + "," + relationTerm + "," + objectTerm;
//		return svo;
		//pair p = new pair();
		p.setVerb(allVerbs.toLowerCase()); p.setNoun(allNouns.toLowerCase());
		return p;
	}
	
	//jan 2019, new function trying to use the code style from coreference.java class - which was written for scnlp version 3.92, to make it work for 3.52
	//we only have to output the mentions as the code works on that
	//create function, it should be passed with sentence and next sentence
	// if coreference found in next sentence
	//send second sentence with coref term subsitituted with pep or subject
	public static String coreference(String text1) {		
		document = new Annotation(text1);		pipeline.annotate(document);
		System.out.println("\n---New Text: "+ text1);	System.out.println("coref chains");
		Integer sentenceCounter=1; String finalSentence = "";

		//populate tripair list
		String subjectMatchedString = "";	
		for (CorefChain cc : document.get(CorefCoreAnnotations.CorefChainAnnotation.class).values()) {
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
		}
		//for each sentence
		// iterate through the tripair list
		//	if sentence counter matches the key
		//		if subjectmatched
		//			replace the value  in the sentence with the subject
		
		// WE COMMENTED THESE as its hard to code to get the mentions here, so we use the coreferencer.java
		//it can happen that the subject exists in the paragraph, but its coreferenced
		//if we have found subject, meaning that the subject is part of the chain, we resolve	
		/* boolean subjectMatched=false;
		Integer keyNum =  null; String val = ""; String sub = "";
		System.out.println("Sentence Matching ###");	
		for (CoreMap sentence : document.get(CoreAnnotations.SentencesAnnotation.class)) {	//for each sentence
			String tempSentence = sentence.toString();
			System.out.println("Sentence: "+sentence);			System.out.println("mentions");
			for (Mention m : sentence.get(CorefCoreAnnotations.CorefMentionsAnnotation.class)) {	//for each mention
				System.out.println("\t" + m);
			}
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
		}  */
		return finalSentence;
	}

	
	//old way..doesnt work always with different scenarious of text input
	public static String resolveCoRef(String text) {
		String resolved = new String();// to hold resolved string
		Annotation document = runPipeline(text);// run the pipeline		
		Map<Integer, CorefChain> corefs = document.get(CorefChainAnnotation.class);	// get all coref chains and sentences		
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		
		for (CoreMap sentence : sentences) {	// process each sentence	
			int curSentIdx = sentence.get(SentenceIndexAnnotation.class);
			List<CoreLabel> tokens = sentence.get(TokensAnnotation.class);
			boolean isPronoun = false;
			for (CoreLabel token : tokens) {	// process each sentence
				isPronoun = false;
				String pos = token.get(PartOfSpeechAnnotation.class);
				if (pos.equals("PRP") || pos.equals("PP$")) {
					isPronoun = true;
				}
				Integer corefClustId = token.get(CorefClusterIdAnnotation.class);
				CorefChain chain = corefs.get(corefClustId);
				// if there is no chain to replace
				if (chain == null || chain.getMentionsInTextualOrder().size() == 1 || isPronoun == false ) {
					resolved += token.word() + token.after();
				} else {
					int sentIndx = chain.getRepresentativeMention().sentNum - 1;
					CorefMention reprMent = chain.getRepresentativeMention();
					String rootWord = sentences.get(sentIndx)
							.get(TokensAnnotation.class)
							.get(reprMent.headIndex - 1)
							.originalText();
					if (curSentIdx != sentIndx || token.index() < reprMent.startIndex
							|| token.index() > reprMent.endIndex) {
						if (Character.isUpperCase(token.originalText().charAt(0))) {
							//						rootWord = WordUtils.capitalize(rootWord);
						}
						resolved += rootWord + token.after();
					} else {
						resolved += token.word() + token.after();
					}
				}
			}
		}
		return resolved;
	}
	//if the sentence contains a negation term, but negation not found with previous negation checking methods, we pass it here for checking
	public boolean checkNegation(String text, String [] negations) {
		//text = "To some extent , anything else does n't match the oficial PEP process where the PEP is modified to match the proposed solution , and then the PEP is accepted or rejected once it 's finalized and ready for a decision .";
        Annotation annotation = new Annotation(text);
        pipeline.annotate(annotation);
        List<CoreMap> sentences = annotation.get(SentencesAnnotation.class);
        for (CoreMap sentence : sentences) {
            Tree tree = sentence.get(TreeAnnotation.class);
            List<Tree> leaves = new ArrayList<>();
            leaves = tree.getLeaves(leaves);
            //display entire tree
            //printAllRootToLeafPaths(tree, new ArrayList<String>());
            for (Tree leave : leaves) {		//treat every tree as a leave
                String compare = leave.toString().toLowerCase();	//check leave 
                for(String neg: negations) {
	                if(compare.equals(neg) == true) {	                    
	                    System.out.println(leave);
	                    Tree curr = leave.parent(tree).parent(tree).parent(tree);
	                    List<String> nodes = printAllRootToLeafPaths(curr, new ArrayList<String>());
	                    System.out.println("displaying each node with negation");
	                    for (String node : nodes) {
	//                    	String node = test.toString().toLowerCase();                    	
	                    	//code here to check if the terms exist
	                    	System.out.println(node.toString());
	                    }
	                }
                }
            }
        }
        return false;
    }
    
    private static List<String> printAllRootToLeafPaths(Tree tree, List<String> path) {
        if(tree != null) {
            if(tree.isLeaf()) {
                path.add(tree.nodeString());
            }
            if(tree.children().length == 0) {
              //  System.out.println(path.toString().replace("[", "").replace("]", ""));
            } else {
                for(Tree child : tree.children()) {
                    printAllRootToLeafPaths(child, path);
                }
            }
            //path.remove(tree.nodeString());
        }
        return path;
    }
	
}


