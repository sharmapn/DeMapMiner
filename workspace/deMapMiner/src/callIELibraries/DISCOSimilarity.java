package callIELibraries;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.index.CorruptIndexException;

//import semanticSimilarity.SentenceSimilarity;
//import wekaExamples.MyFilteredClassifier;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Sentence;
import miner.models.tmp;

public class DISCOSimilarity {
	
	public DISCOSimilarity(){
		
	}
	
	public static String getDISCOSimilarity(String v_idea, String v_CurrentSentenceString,UseDISCOSentenceSim ds, String[] sentencesToMatch) throws IOException {
		   double maximum = -2;
		   String max_idea = "";
		   String max_ideaSentence = "";
		   double current=0;
		   int count=0;
		   String ideaSentences="";  //sentence to match
		   for (String sen : sentencesToMatch) {
				count++;											
				if (count>6)
				{												
					String[] sections = sen.split(",'");
					//just assign idea and sentence part - had some problem doing it the conventional way
					for(int y=0;y<sections.length; y++){
						if(y==1){
							v_idea = sections[y-1];
							ideaSentences = sections[y];
//							System.out.println("v_idea  "+v_idea);
//							System.out.println("sent "+sent);							
						}
					}
					//using DISCO, match entire training model in pep.small.arff with sentence
					// pick out most similar
					
					current = ds.matchSentences(ideaSentences, v_CurrentSentenceString);
		//			System.out.println("Disco similarity: "+ current+ " idea " + v_idea + " " + ideaSentences + " || Sentence: " + v_CurrentSentenceString);
					if (current>maximum){
						maximum = current;   //max value
						max_idea = v_idea;
						max_ideaSentence = ideaSentences;
					}
					//and add to output line
					//Semantic similarity SEMANTIC (WORDNET lexical database and WS4J)
//												System.out.println("Semantic similarity: " + sent + " " + CurrentSentenceString);
//												ss.callSentenceSimilarityFromExternal(sent, CurrentSentenceString);
				}											
			}
		//   System.out.println("Max Disco similarity: " + max_idea + " maximum " + maximum);
		   //return maximum;
		  // return (max_idea + " " + max_ideaSentence + " " + maximum);
		   return (max_idea + " " + maximum);
	   }
	   
	   
	   public static void computeSimilarity(tmp t, List<HasWord> v_eachSentence, String v_PreviousSentenceString, String v_wordsFoundList, String [] v_baseIdeas, 
			   String v_prevSentence, UseDISCOSentenceSim v_ds,   String[] sentencesToMatch) throws CorruptIndexException, IOException{     //SentenceSimilarity v_ss,
		   String CurrentSentenceString = Sentence.listToString(v_eachSentence);
			//p[v_pepNumber].messages[v_counter].setSentence(sentenceString);
			// sentence = sentence.toString();
			// wordsFoundList = checkEachSentence (sentenceString, wordsFoundList, author, statusFrom, statusTo , v_pep, v_date, v_pepNumber, v_counter );

			// Check base ideas and output sentences
	//JJJ						wordsFoundList = cs.checkAllSentencesForBaseIdeasInMessage2(p, sentenceString, wordsFoundList, author, statusFrom, statusTo, v_pep, v_date, v_pepNumber, v_counter, baseIdeas);
			
			//someday make this work, rather than the one below it
//			for (String bi : baseIdeas) {
//				String words[] = bi.split(" ");
//				String idea = 	words[0];					
//				//get the words to match for each idea
//				SentenceUtility s = new SentenceUtility();
//				if(s.containsAllButFirst(words, CurrentSentenceString)){
//					wordsFoundList = idea + ", " + CurrentSentenceString;        //one string that would be appended
//					//System.out.println(v_pepnumber + " idea=" + idea + " entity=" + entity + " action=" + action + " pep=" + pep + " wordsFoundList in Fn " + wordsFoundList);			
//					prevSentence = PreviousSentenceString;
//				}
			
			//initialise variables
			String idea = "";
			String entity = "";
			String action = "";
			String pep = 	"";
			String PrevSentence = "";
			String CurrSentence = "";
			
			Boolean matchFound = false;
			
			for (String bi : v_baseIdeas) {
				if (bi.contains(",")){
					
					String sections[] = bi.split(",");
					idea = 		   sections[0];
					PrevSentence = sections[1];
					CurrSentence = sections[2];
					
					String words[] = PrevSentence.split(" ");								
					entity = words[0];
					action = words[1];
					pep = 	 words[2];
					
					if (v_PreviousSentenceString.toLowerCase().contains(entity) && v_PreviousSentenceString.toLowerCase().contains(action) 
							&& v_PreviousSentenceString.toLowerCase().contains(pep)
							&& CurrentSentenceString.toLowerCase().contains(CurrSentence)) {			
						System.out.println("/////////////////");
						v_wordsFoundList = idea + ", " + v_PreviousSentenceString + ", " + CurrentSentenceString;        //one string that would be appended
						
						//System.out.println(v_pepnumber + " idea=" + idea + " entity=" + entity + " action=" + action + " pep=" + pep + " wordsFoundList in Fn " + wordsFoundList);			
						v_prevSentence = v_PreviousSentenceString;
						t.setPS(v_PreviousSentenceString);
						t.setLOW(v_wordsFoundList);
						matchFound = true;
					}
					//check next sentence
					
				}
				else {
					String words[] = bi.split(" ");
					idea =   words[0];
					entity = words[1];
					action = words[2];
					pep = 	 words[3];
					
					if (CurrentSentenceString.toLowerCase().contains(entity) && CurrentSentenceString.toLowerCase().contains(action) && CurrentSentenceString.toLowerCase().contains(pep)) {			
						v_wordsFoundList = idea + ", " + CurrentSentenceString;        //one string that would be appended
						//System.out.println(v_pepnumber + " idea=" + idea + " entity=" + entity + " action=" + action + " pep=" + pep + " wordsFoundList in Fn " + wordsFoundList);			
						v_prevSentence = v_PreviousSentenceString;
						t.setPS(v_PreviousSentenceString);
						t.setLOW(v_wordsFoundList);
						matchFound = true;
						
						//if match was found, classify and show result and also take base idea
						//add statistical disco approach and semantic also
						// but ideally this should be done on all sentences,not only whch ones the string matching has found
						//clasify the sentence
//**						MyFilteredClassifier fc = new MyFilteredClassifier();
//**						fc.classifySentence (CurrentSentenceString);
						//take base idea also 
						//use DISCO statistical approach
						int count=0;
						String sent="";  //sentence to match
						for (String sen : sentencesToMatch) {
						    //System.out.println("helo" + sen);
							count++;
							if (count>6){
								System.out.println("helo55 count " + count+ " " + sen);
								String sections[] = sen.split("','");
								idea =   sections[0];
								sent = sections[1];
								//send sent and CurrentSentenceString for matching	
								//DISCO
								System.out.println("here 4" );
								v_ds.matchSentences(sent, CurrentSentenceString);
								//and add to output line
								//Semantic
								//use semantic similarity
//**								v_ss.callSentenceSimilarityFromExternal(sent, CurrentSentenceString);
							}
						}
						
						 
						
					}
				}
				
//				if (matchFound){
//					//clasify the sentence
//					MyFilteredClassifier fc = new MyFilteredClassifier();
//					 fc.classifySentence (CurrentSentenceString);
//					//take base idea also 
//				}
				
				//System.out.println("\n---End of For Loop ");
			}
			
//			System.out.println("wordsFoundList IN MAIN " + wordsFoundList);
			// String Matching
			// System.out.println("sentenceString " + sentenceString);
			// TEMP wordsFoundList = cs.checkEachSentenceNew (p, sentenceString, wordsFoundList, author, statusFrom, statusTo , v_pep, v_date, v_pepNumber, v_counter );
			 
			// WOrdnet and WS4J
			// vvv MessageSimilarity semantic = new
			// MessageSimilarity();
			// vvv wordsFoundList = semantic.calculateSentenceSimilarity(sentenceString, baseIdeas);
			v_PreviousSentenceString = CurrentSentenceString;
			//return v_PreviousSentenceString;
			
	   }
}
