package utilities;

import java.awt.Color;
import java.awt.Rectangle;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

//import edu.stanford.nlp.ling.HasWord;
//import edu.stanford.nlp.process.DocumentPreprocessor;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.Span;
//import edu.stanford.nlp.ling.HasWord;
//import edu.stanford.nlp.ling.Sentence;
//import edu.stanford.nlp.process.DocumentPreprocessor;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.util.Span;

public class WordSearcher {
	protected JTextComponent comp;
	protected Highlighter.HighlightPainter painter;
	ParagraphSentence ps;
	
	public WordSearcher(JTextComponent comp, Color c) {
		ps = new ParagraphSentence();
		this.comp = comp;
		if(c==Color.blue)
			this.painter = new UnderlineHighlighter.UnderlineHighlightPainter(Color.blue);
		else if(c==Color.red)
			this.painter = new UnderlineHighlighter.UnderlineHighlightPainter(Color.red);
	}
	//just to make sure..may not be needed
	public void init (JTextComponent comp) {
		this.comp = comp;
		this.painter = new UnderlineHighlighter.UnderlineHighlightPainter(Color.blue);
	}
	
	
	
	//returns sentence which have all the terms so for highlighting
	//public val returnSentenceWithTerms(String words) {
	public String returnSentenceWithTerms(String words) throws FileNotFoundException {
		InputStream inputStream = new FileInputStream("C:/lib/OpenNLP/en-sent.bin"); 
		
		//val v = new val();
		int counterStart=0, counterEnd=0;
		// Look for the word we are given - insensitive search
		System.out.println("\n\tWords to look for>> "+ words  );
		String content = null;
		try {
		      SentenceModel model = new SentenceModel(inputStream);
			Document d = comp.getDocument();  
			content = d.getText(0, d.getLength()).toLowerCase();
			
			String[] paragraphs = content.split("\\n\\n"); //"\\r?\\n\\r?\\n");		
			//String[] paragraphs = v_message.split("\\r?\\n\\r?\\n"); //mar 2018...new improved way implemented   "\\n\\n");
						
			
			for (String para: paragraphs){ //System.out.println("new para: "+para);	
				 //sometimes a paragrapgh has sentences but are not captured as sentences. So if sentence identified then check, so we just add a full-stop at end of paragraph	        				   
				 if(!para.endsWith("."))
					para = para + ".";
				
				  //Loading sentence detector model 			     

			      //Instantiating the SentenceDetectorME class 
			      SentenceDetectorME detector = new SentenceDetectorME(model);
			      //Detecting the position of the sentences in the raw text 
			      Span spans[] = detector.sentPosDetect(para); 			       
			      //Printing the sentences and their spans of a paragraph
			      for (Span span : spans) {
			         //
			         String str = para.substring(span.getStart(), span.getEnd());
			         boolean found =true;
			         for (String w: words.split(" ")) {
							if (!str.contains(w)) {	
								found=false;					
								//break;
							}
					 }
			         if(found) {
							System.out.println("\n\tfound sentence cntaining terms >> "+ str  );
							System.out.println(para.substring(span.getStart(), span.getEnd())+" "+ span);
							return str;
							//return v;
					 }
			      }
			      //Printing the spans of the sentences in the paragraph 
			      for (Span span : spans) {         
			       //  System.out.println(span); 
			      }
				
				/*
				Reader reader = new StringReader(para);
				DocumentPreprocessor dp = new DocumentPreprocessor(reader);
	
				//sometimes a paragrapgh has sentences but are not captured as sentences. So if sentence identified then check, so we just add a full-stop at end of paragraph	        				   
				//if(!para.endsWith("."))
				//	para = para + ".";
				
				for (List<HasWord> eachSentence : dp){
					boolean found=true;
					String CurrentSentenceString = Sentence.listToString(eachSentence);
					counterStart = counterEnd;
					counterEnd = counterEnd + CurrentSentenceString.length();
					//v.setVal(counterStart, counterEnd);
					for (String w: words.split(" ")) {
						if (!CurrentSentenceString.contains(w)) {	
							found=false;					
							//break;
						}
					} 
					if(found) {
						System.out.println("\n\tfound sentence cntaining terms >> "+ CurrentSentenceString  );
						return CurrentSentenceString;
						//return v;
					}
				}
				*/
			}
			
		} catch (BadLocationException e) {
			// Cannot happen
			return ""; //v; //"";
		}
		catch (Exception ex) {
			System.out.println(StackTraceToString(ex)  );
		}
		
		
		return "";
		//return v; //"";
	}
	
	// Search for a word and return the offset of the first occurrence. Highlights are added for all occurrences found.
	//April 2020..same as previous version, but we try to split into sentences
	public int searchAllWordsInParagraph2(String[] words, String toHighlightSentence) {
		int firstOffset = -1;
		
		if (words == null || words.equals("") ) {			return -1;		}		
		Highlighter highlighterX = comp.getHighlighter();
		//System.out.print("To trim content: " + totrimContent);
		System.out.print("Words to check: ");
		for (String d: words)
			System.out.print(d + " ");
		System.out.println();
		
		// Look for the word we are given - insensitive search
		String content = "", contentX = "";
		try {
			Document d = comp.getDocument();  
			content = d.getText(0, d.getLength()).toLowerCase();
			//contentX = content.replaceAll(".", " "); 
			contentX = content.replaceAll("\\p{P}", " "); 
//			contentX = contentX.replaceAll("\\r?\\n", " ");
//			contentX = contentX.replaceAll("\\s+", " ").toLowerCase().trim();
			
			//if(totrimContent==1) {
			//	contentX = contentX.replaceAll("\\s+", " ").toLowerCase().trim(); //remove double spaces and trim
			//}		
			
//			System.out.print("contentX: " + contentX);
		} catch (BadLocationException e) {
			// Cannot happen
			return -1;
		}
		catch (Exception ex) {
			System.out.println(StackTraceToString(ex)  );
		}
		
		//july 2018 ..we going to split the message into paragraphs		
		String sentenceToCapture="";
		for (String w: words ) {	 
			sentenceToCapture = sentenceToCapture + w +" "; 
		} 
		sentenceToCapture=sentenceToCapture.trim();
				
		String[] paragraphs = content.split("\\n\\n"); //"\\r?\\n\\r?\\n");
		Integer paragraphLength=0;
		outer:
		for (String para: paragraphs){ 
//			System.out.println("\nnew para2: "+para);
						
//			para = para.replaceAll("\\p{P}", " "); //remove all punctuation ..maybe we can use this here (term matching for candidates) but not in CIE triple extraction as it wmay need commas and other punctuation
//			para = para.replaceAll("\\r?\\n", " ");
//			para = para.replaceAll("\\s+", " ").toLowerCase().trim(); //remove double spaces and trim
					
//			System.out.println("new para3: "+para);
			
			String[] paraTerms = para.split(" ");
			boolean foundAll = true;
			for (String w: words ) {
			    if (!para.contains(w.trim())) {
			        foundAll = false;			       
			        //System.out.println( "The value is not found! ("+ w+")");
			        continue outer;	//fo to next paragraph
			    }			   
			}		
			
			InputStream inputStream;
			SentenceModel model;
			SentenceDetectorME detector = null;
			try {
				inputStream = new FileInputStream("C://lib//openNLP//en-sent.bin");
				model = new SentenceModel(inputStream); 
				detector = new SentenceDetectorME(model);  
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} 
			catch (InvalidFormatException w) {
				
			}
			catch (IOException w) {
				
			}
		    //SentenceModel model = new SentenceModel(inputStream); 	    									
			//SentenceDetectorME detector = new SentenceDetectorME(model);  
			Span[] spans = detector.sentPosDetect(para);   //Detecting the position of the sentences in the paragraph  
		     
		    String sen = para;
		    
		    //Printing the sentences and their spans of a paragraph 
		    String extracted="";
		    for (Span span : spans) {        
		    	 extracted = sen.substring(span.getStart(), span.getEnd()); //get each sentence
		        System.out.println("\t\t\t Span2: " + extracted +" : ");//+ span.toString());  
		        
		    }
		    
		    //get first three words and last three words to get their indexes
		    String firstthreewords="", lastthreewords="";
		    for (int y=0;y< 3; y++) {
		    	firstthreewords = firstthreewords + " "+ words[y];
		    }
		    firstthreewords = firstthreewords.trim();
			System.out.println("firstthreewords: " + firstthreewords);
			
			for (int y=words.length-3; y< words.length; y++) {
		    	lastthreewords = lastthreewords + " "+ words[y];
		    }
			lastthreewords = lastthreewords.trim();
			System.out.println("lastthreewords: " + lastthreewords);
			
			
			try {
				int startX = contentX.toLowerCase().indexOf(firstthreewords); // System.out.println("startX: " + startX);
				int endX = contentX.indexOf(lastthreewords) + lastthreewords.length();		//System.out.println("endX: " + endX);
				if(startX<0 || endX <0)	{
					System.out.println(" Error -1. startx: " + startX + " endx: " +endX );
				}
				else {
					highlighterX.addHighlight(startX, endX, painter);
				}
				System.out.println(" startx: " + startX + " endx: " +endX );
			} catch (BadLocationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				System.out.println(StackTraceToString(e1)  );
			}
			catch (Exception ep) {
				System.out.println(StackTraceToString(ep)  );
			}
			
		//	if (Arrays.asList(paraTerms).containsAll(Arrays.asList(words))) 
		//	{ //if all words is found in the paragraph
			/*
				if(foundAll) {	
					System.out.println("\tAll terms in para, words: "+ sentenceToCapture);
					System.out.println("\tParagraph contains all words on paragraph : " + para);	set 
					for (String word : words) {	//make sure all words in same paragraph
						word = word.trim().toLowerCase();
						int lastIndex = 0, wordSize = word.length();
						while ((lastIndex = para.indexOf(word, lastIndex)) != -1) {
							int endIndex = lastIndex + wordSize;
							try {
								//make sure that its a word on its own
								//just check to make sure that previous and next char is space char
								//	if(para.charAt(lastIndex-1) == ' ' || para.charAt(endIndex+1) == ' ' ) {
								System.out.println("\tTerm: "+ para.substring(lastIndex, endIndex));
								int start = content.indexOf(word,paragraphLength);
								//highlighterX.addHighlight(lastIndex+paragraphLength, endIndex+paragraphLength, painter);
								highlighterX.addHighlight(start, start+wordSize, painter);
								//added aug 2018
								
								//	}
							} catch (BadLocationException e) {
								// Nothing to do
							}
							if (firstOffset == -1) {
								firstOffset = lastIndex;
							}
							lastIndex = endIndex;
						}
					}
				}			
				else {
				//	System.out.println("\nall terms not in para, words: ");
					for (String w: words) {
					//	System.out.print(w+" ");	
					}
					//System.out.println();
				}
				*/
				paragraphLength = paragraphLength + para.length();
		//	}
		}
		return firstOffset;
	}
	
	// Search for a word and return the offset of the first occurrence. Highlights are added for all occurrences found.
	//April 2020..same as previous version, but we try to split into sentences
	public int searchAllWordsInParagraph3(String[] words, String toHighlightSentence) {
		int firstOffset = -1;
		
		if (words == null || words.equals("") ) {			return -1;		}		
		Highlighter highlighterX = comp.getHighlighter();
		//System.out.print("To trim content: " + totrimContent);
		System.out.print("Words to check: ");
		for (String d: words)
			System.out.print(d + " ");
		System.out.println();
		
		// Look for the word we are given - insensitive search
		String content = "", contentX = "";
		try {
			Document d = comp.getDocument();  
			contentX = d.getText(0, d.getLength()); //.toLowerCase();
			//contentX = content.replaceAll(".", " "); 
//			contentX = content.replaceAll("\\p{P}", " "); 
//			contentX = contentX.replaceAll("\\r?\\n", " ");
//			contentX = contentX.replaceAll("\\s+", " ").toLowerCase().trim();
			
			//if(totrimContent==1) {
			//	contentX = contentX.replaceAll("\\s+", " ").toLowerCase().trim(); //remove double spaces and trim
			//}		
			
			System.out.print("contentX: " + contentX);
		} catch (BadLocationException e) {
			// Cannot happen
			return -1;
		}
		catch (Exception ex) {
			System.out.println(StackTraceToString(ex)  );
		}
		
		//july 2018 ..we going to split the message into paragraphs		
		String sentenceToCapture="";
		for (String w: words ) {	 
			sentenceToCapture = sentenceToCapture + w +" "; 
		} 
		sentenceToCapture=sentenceToCapture.trim();
				
		String[] paragraphs = contentX.split("\\n\\n"); //"\\r?\\n\\r?\\n");
		Integer paragraphLength=0;
		outer:
		for (String para: paragraphs){ 
//			System.out.println("\nnew para2: "+para);
						
//			para = para.replaceAll("\\p{P}", " "); //remove all punctuation ..maybe we can use this here (term matching for candidates) but not in CIE triple extraction as it wmay need commas and other punctuation
//			para = para.replaceAll("\\r?\\n", " ");
//			para = para.replaceAll("\\s+", " ").toLowerCase().trim(); //remove double spaces and trim
					
//			System.out.println("new para3: "+para);
			
			String[] paraTerms = para.split(" ");
			boolean foundAll = true;
			for (String w: words ) {
			    if (!para.contains(w.trim())) {
			        foundAll = false;			       
			        //System.out.println( "The value is not found! ("+ w+")");
			        continue outer;	//fo to next paragraph
			    }			   
			}		
			
			InputStream inputStream;
			SentenceModel model;
			SentenceDetectorME detector = null;
			try {
				inputStream = new FileInputStream("C://lib//openNLP//en-sent.bin");
				model = new SentenceModel(inputStream); 
				detector = new SentenceDetectorME(model);  
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} 
			catch (InvalidFormatException w) {
				
			}
			catch (IOException w) {
				
			}
		    //SentenceModel model = new SentenceModel(inputStream); 	    									
			//SentenceDetectorME detector = new SentenceDetectorME(model);  
			Span[] spans = detector.sentPosDetect(para);   //Detecting the position of the sentences in the paragraph  
		     
		    String sen = para;
		    
		    //Printing the sentences and their spans of a paragraph 
		    String extracted="";
		    for (Span span : spans) {        
		    	 extracted = sen.substring(span.getStart(), span.getEnd()); //get each sentence
//		        System.out.println("\t\t\t Span2: " + extracted +" : ");//+ span.toString());  
		        
		    }
		    
		    //get first three words and last three words to get their indexes
		    String firstthreewords="", lastthreewords="";
		    for (int y=0;y< 3; y++) {
		    	firstthreewords = firstthreewords + " "+ words[y];
		    }
		    firstthreewords = firstthreewords.trim();
			System.out.println("firstthreewords: " + firstthreewords);
			
			for (int y=words.length-3; y< words.length; y++) {
		    	lastthreewords = lastthreewords + " "+ words[y];
		    }
			lastthreewords = lastthreewords.trim();
			System.out.println("lastthreewords: " + lastthreewords);
			
			
			try {
				int startX = contentX.indexOf(firstthreewords); // System.out.println("startX: " + startX);
				int endX = contentX.indexOf(lastthreewords) + lastthreewords.length();		//System.out.println("endX: " + endX);
				if(startX<0 || endX <0)	{
					System.out.println(" Error -1. startx: " + startX + " endx: " +endX );
				}
				else {
					highlighterX.addHighlight(startX, endX, painter);
				}
				System.out.println(" startx: " + startX + " endx: " +endX );
			} catch (BadLocationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				System.out.println(StackTraceToString(e1)  );
			}
			catch (Exception ep) {
				System.out.println(StackTraceToString(ep)  );
			}
			
		//	if (Arrays.asList(paraTerms).containsAll(Arrays.asList(words))) 
		//	{ //if all words is found in the paragraph
			/*
				if(foundAll) {	
					System.out.println("\tAll terms in para, words: "+ sentenceToCapture);
					System.out.println("\tParagraph contains all words on paragraph : " + para);	set 
					for (String word : words) {	//make sure all words in same paragraph
						word = word.trim().toLowerCase();
						int lastIndex = 0, wordSize = word.length();
						while ((lastIndex = para.indexOf(word, lastIndex)) != -1) {
							int endIndex = lastIndex + wordSize;
							try {
								//make sure that its a word on its own
								//just check to make sure that previous and next char is space char
								//	if(para.charAt(lastIndex-1) == ' ' || para.charAt(endIndex+1) == ' ' ) {
								System.out.println("\tTerm: "+ para.substring(lastIndex, endIndex));
								int start = content.indexOf(word,paragraphLength);
								//highlighterX.addHighlight(lastIndex+paragraphLength, endIndex+paragraphLength, painter);
								highlighterX.addHighlight(start, start+wordSize, painter);
								//added aug 2018
								
								//	}
							} catch (BadLocationException e) {
								// Nothing to do
							}
							if (firstOffset == -1) {
								firstOffset = lastIndex;
							}
							lastIndex = endIndex;
						}
					}
				}			
				else {
				//	System.out.println("\nall terms not in para, words: ");
					for (String w: words) {
					//	System.out.print(w+" ");	
					}
					//System.out.println();
				}
				*/
				paragraphLength = paragraphLength + para.length();
		//	}
		}
		return firstOffset;
	}

	//just for pep 572
	//April 2020..same as previous version, but we try to split into sentences
	public int searchAllWordsInParagraph4(String[] words, String toHighlightSentence) {
		int firstOffset = -1;
		
		if (words == null || words.equals("") ) {			return -1;		}		
		Highlighter highlighterX = comp.getHighlighter();
		//System.out.print("To trim content: " + totrimContent);
		System.out.print("Words to check: ");
		for (String d: words)
			System.out.print(d + " ");
		System.out.println();
		
		// Look for the word we are given - insensitive search
		String content = "", contentX = "";
		try {
			Document d = comp.getDocument();  
			contentX = d.getText(0, d.getLength()).toLowerCase();
			//contentX = content.replaceAll(".", " "); 
//				contentX = content.replaceAll("\\p{P}", " "); 
//				contentX = contentX.replaceAll("\\r?\\n", " ");
//				contentX = contentX.replaceAll("\\s+", " ").toLowerCase().trim();
			
			//if(totrimContent==1) {
			//	contentX = contentX.replaceAll("\\s+", " ").toLowerCase().trim(); //remove double spaces and trim
			//}		
			
			System.out.print("contentX: " + contentX);
		} catch (BadLocationException e) {
			// Cannot happen
			return -1;
		}
		catch (Exception ex) {
			System.out.println(StackTraceToString(ex)  );
		}
		
		//july 2018 ..we going to split the message into paragraphs		
		String sentenceToCapture="";
		for (String w: words ) {	 
			sentenceToCapture = sentenceToCapture + w +" "; 
		} 
		sentenceToCapture=sentenceToCapture.trim();
				
		String[] paragraphs = contentX.split("\\n\\n"); //"\\r?\\n\\r?\\n");
		Integer paragraphLength=0;
		outer:
		for (String para: paragraphs){ 
//				System.out.println("\nnew para2: "+para);
						
//				para = para.replaceAll("\\p{P}", " "); //remove all punctuation ..maybe we can use this here (term matching for candidates) but not in CIE triple extraction as it wmay need commas and other punctuation
//				para = para.replaceAll("\\r?\\n", " ");
//				para = para.replaceAll("\\s+", " ").toLowerCase().trim(); //remove double spaces and trim
					
//				System.out.println("new para3: "+para);
			
			String[] paraTerms = para.split(" ");
			boolean foundAll = true;
			for (String w: words ) {
			    if (!para.contains(w.trim())) {
			        foundAll = false;			       
			        //System.out.println( "The value is not found! ("+ w+")");
			        continue outer;	//fo to next paragraph
			    }			   
			}		
			
			InputStream inputStream;
			SentenceModel model;
			SentenceDetectorME detector = null;
			try {
				inputStream = new FileInputStream("C://lib//openNLP//en-sent.bin");
				model = new SentenceModel(inputStream); 
				detector = new SentenceDetectorME(model);  
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} 
			catch (InvalidFormatException w) {
				
			}
			catch (IOException w) {
				
			}
		    //SentenceModel model = new SentenceModel(inputStream); 	    									
			//SentenceDetectorME detector = new SentenceDetectorME(model);  
			Span[] spans = detector.sentPosDetect(para);   //Detecting the position of the sentences in the paragraph  
		     
		    String sen = para;
		    
		    //Printing the sentences and their spans of a paragraph 
		    String extracted="";
		    for (Span span : spans) {        
		    	 extracted = sen.substring(span.getStart(), span.getEnd()); //get each sentence
//			        System.out.println("\t\t\t Span2: " + extracted +" : ");//+ span.toString());  
		        
		    }
		    
		    //get first three words and last three words to get their indexes
		    String firstthreewords="", lastthreewords="";
		    for (int y=0;y< 3; y++) {
		    	firstthreewords = firstthreewords + " "+ words[y];
		    }
		    firstthreewords = firstthreewords.trim();
			System.out.println("firstthreewords: " + firstthreewords);
			
			for (int y=words.length-3; y< words.length; y++) {
		    	lastthreewords = lastthreewords + " "+ words[y];
		    }
			lastthreewords = lastthreewords.trim();
			System.out.println("lastthreewords: " + lastthreewords);
			
			
			try {
				int startX = contentX.indexOf(firstthreewords); // System.out.println("startX: " + startX);
				int endX = contentX.indexOf(lastthreewords) + lastthreewords.length();		//System.out.println("endX: " + endX);
				if(startX<0 || endX <0)	{
					System.out.println(" Error -1. startx: " + startX + " endx: " +endX );
				}
				else {
					highlighterX.addHighlight(startX, endX, painter);
				}
				System.out.println(" startx: " + startX + " endx: " +endX );
			} catch (BadLocationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				System.out.println(StackTraceToString(e1)  );
			}
			catch (Exception ep) {
				System.out.println(StackTraceToString(ep)  );
			}
		
				paragraphLength = paragraphLength + para.length();
		//	}
		}
		return firstOffset;
	}
	
	// Search for a word and return the offset of the first occurrence. Highlights are added for all occurrences found.
	public int searchAllWordsInParagraph(String[] words) {
		int firstOffset = -1;
		
		if (words == null || words.equals("") ) {			return -1;		}		
		Highlighter highlighterX = comp.getHighlighter();

		// Look for the word we are given - insensitive search
		String content = null;
		try {
			Document d = comp.getDocument();  
			content = d.getText(0, d.getLength()).toLowerCase();
		} catch (BadLocationException e) {
			// Cannot happen
			return -1;
		}
		catch (Exception ex) {
			System.out.println(StackTraceToString(ex)  );
		}
		
		//july 2018 ..we going to split the message into paragraphs		
		String sentence="";
		for (String w: words ) {	 
			sentence = sentence + w +" "; 
		} 
		sentence=sentence.trim();
		
		String[] paragraphs = content.split("\\n\\n"); //"\\r?\\n\\r?\\n");
		Integer paragraphLength=0;
		for (String para: paragraphs){ //System.out.println("new para: "+para);
			
			
			para = para.replaceAll("\\p{P}", " "); //remove all punctuation ..maybe we can use this here (term matching for candidates) but not in CIE triple extraction as it wmay need commas and other punctuation
			para = para.replaceAll("\\r?\\n", " ");
			para = para.replaceAll("\\s+", " ").toLowerCase().trim(); //remove double spaces and trim
			
			String[] paraTerms = para.split(" ");
			boolean foundAll = true;
			for (String w: words ) {
			    if (!para.contains(w.trim())) {
			        foundAll = false;			        //System.out.println( "The value is not found! ("+ w+")");
			    }			   
			}			
			
		//	if (Arrays.asList(paraTerms).containsAll(Arrays.asList(words))) 
		//	{ //if all words is found in the paragraph
				if(foundAll) {	
					
	                
					System.out.println("\tAll terms in para, words: "+ sentence);
					System.out.println("\tParagraph contains all words on paragraph : " + para);	
					for (String word : words) {	//make sure all words in same paragraph
						word = word.trim().toLowerCase();
						int lastIndex = 0, wordSize = word.length();
						while ((lastIndex = para.indexOf(word, lastIndex)) != -1) {
							int endIndex = lastIndex + wordSize;
							try {
								//make sure that its a word on its own
								//just check to make sure that previous and next char is space char
								//	if(para.charAt(lastIndex-1) == ' ' || para.charAt(endIndex+1) == ' ' ) {
								System.out.println("\tTerm: "+ para.substring(lastIndex, endIndex));
								int start = content.indexOf(word,paragraphLength);
								//highlighterX.addHighlight(lastIndex+paragraphLength, endIndex+paragraphLength, painter);
								highlighterX.addHighlight(start, start+wordSize, painter);
								//added aug 2018
								
								//	}
							} catch (BadLocationException e) {
								// Nothing to do
							}
							if (firstOffset == -1) {
								firstOffset = lastIndex;
							}
							lastIndex = endIndex;
						}
					}
				}			
				else {
				//	System.out.println("\nall terms not in para, words: ");
					for (String w: words) {
					//	System.out.print(w+" ");	
					}
					//System.out.println();
				}
				paragraphLength = paragraphLength + para.length();
		//	}
		}
		return firstOffset;
	}
	// Search for a word and return the offset of the first occurrence. Highlights are added for all occurrences found.
	public int search(String word) {
		int firstOffset = -1;
		
		if (word == null || word.equals("") ) {
			return -1;
		}
		
		Highlighter highlighter = comp.getHighlighter();

		// Remove any existing highlights for last word
//			Highlighter.Highlight[] highlights = highlighter.getHighlights();
//			for (int i = 0; i < highlights.length; i++) {
//				Highlighter.Highlight h = highlights[i];
//				if (h.getPainter() instanceof UnderlineHighlighter.UnderlineHighlightPainter) {
//					highlighter.removeHighlight(h);
//				}
//			}

		// Look for the word we are given - insensitive search
		String content = null;
		try {
			Document d = comp.getDocument();
			content = d.getText(0, d.getLength()).toLowerCase();
		} catch (BadLocationException e) {
			// Cannot happen
			return -1;
		}
		catch (Exception ex) {
			System.out.println(StackTraceToString(ex)  );
		}

		word = word.toLowerCase();
		int lastIndex = 0;
		int wordSize = word.length();

		while ((lastIndex = content.indexOf(word, lastIndex)) != -1) {
			int endIndex = lastIndex + wordSize;
			try {
				highlighter.addHighlight(lastIndex, endIndex, painter);
			} catch (BadLocationException e) {
				// Nothing to do
			}
			if (firstOffset == -1) {
				firstOffset = lastIndex;
			}
			lastIndex = endIndex;
		}
		return firstOffset;
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
