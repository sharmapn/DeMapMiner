package miner.processLabels;

import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.process.DocumentPreprocessor;
import miner.process.PythonSpecificMessageProcessing;

public class ComputePrevNextSentenceAndParagraph {
	//get the next previous sentence and paragraphs 
		//have to go over teh entire mesage again, but since it only for captured triples, it will be less instances of these
		public static TripleProcessingResult computeSentencesAndParagraph(TripleProcessingResult result, String v_message, String prevSentence, String v_currentSentenceString, String v_nextSentence,	Integer sentenceCounter, 
				String entireParagraph, String prevParagraph, String nextParagraph, Integer v_paragraphCounter, String ns) //entireMessage		
		{		
			PythonSpecificMessageProcessing pm = new PythonSpecificMessageProcessing();
			//at the moment we just need previous and next sentence		
			Boolean currSentenceFound = false;
			//String[] paragraphs = v_message.split("\\r\\n", -1);
			//String[] paragraphs = v_message.split("\\\\n");
			String[] paragraphs = v_message.split("\\n\\n");
			
			Integer counter=0;
//     		System.out.println("\t\t\t$$$$$$ compute nearby sentences and paragraphs");
//			System.out.println("\t paragraphs length " + paragraphs.length);
			for (String g: paragraphs)
			{
//				System.out.println("main for loop..");
				//if 
				//minus one and plus one
				if(counter == v_paragraphCounter-1)
					prevParagraph = g;
				else if(counter == v_paragraphCounter+1)
					nextParagraph = g;
				
				String permanentPrevSentence = null, permanentCurrentSentence = null, permanentNextSentence = null;
				Reader reader = new StringReader(g);
				DocumentPreprocessor dp = new DocumentPreprocessor(reader);
				// System.out.println("method.." + v_message);
				for (List<HasWord> eachSentence : dp) 
				{
//					System.out.println("inside for loop..");
					sentenceCounter++;				
					String CurrentSentenceString = Sentence.listToString(eachSentence);
					CurrentSentenceString = pm.removeUnwantedText(CurrentSentenceString);
					//nextSentence = null;						
					CurrentSentenceString = pm.removeLRBAndRRB(CurrentSentenceString);
					CurrentSentenceString = pm.removeDivider(CurrentSentenceString);
					
					//or we can just loop until the sentence counter, no need to check all sentences
					
					//if found in previous round, get next sentence and return
					if (currSentenceFound==true){
						permanentNextSentence = CurrentSentenceString;						
						//assign the values so it can be passed back						
						prevSentence = permanentPrevSentence;
						v_currentSentenceString = permanentCurrentSentence;
						v_nextSentence = permanentNextSentence;
						ns = permanentNextSentence;
						
						//set prev and next sentence
						result.setPrevSentence(prevSentence);
						result.setNextSentence(v_nextSentence);
						System.out.println("\txxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
						System.out.println("\tresult.nextSentence			(" + result.getNextSentence() + ")");
						// we dont show entire paragraph	
						//System.out.println("\tentire paragraph 			(" + g.replace("\n","") + ")");
						System.out.println("\tpermanent previous sentence 		(" + permanentPrevSentence + ")");
						System.out.println("\tpermanent current sentence  		("  + permanentCurrentSentence + ")");
						System.out.println("\tpermanent next sentence     		("     + permanentNextSentence + ")");
						System.out.println("\tnext sentence     			("     +v_nextSentence+ ")");
						System.out.println("\tprev paragraph     			("     +prevParagraph.replace("\n","")+ ")");
						System.out.println("\tnext paragraph     			("     +nextParagraph.replace("\n","")+ ")");
						return result;
					}
					// as soon as sentence matched
					if (CurrentSentenceString.equals(v_currentSentenceString)){
						currSentenceFound = true;						
						//set permanent values
						permanentCurrentSentence = CurrentSentenceString;
						permanentPrevSentence = prevSentence;		
//						System.out.println("setting permanent previous sentence (" + permanentPrevSentence + ") when sentence matched: "+ CurrentSentenceString);
					}
					prevSentence = CurrentSentenceString;
				}
				v_paragraphCounter++;
			}
			return result;
		}
}
