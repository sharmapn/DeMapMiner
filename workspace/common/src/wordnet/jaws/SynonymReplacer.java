package wordnet.jaws;
/*
	The MIT License (MIT)
	Copyright (c) 2015 Prateek Mathur
	Permission is hereby granted, free of charge, to any person obtaining a copy
	of this software and associated documentation files (the "Software"), to deal
	in the Software without restriction, including without limitation the rights
	to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
	copies of the Software, and to permit persons to whom the Software is
	furnished to do so, subject to the following conditions:
	The above copyright notice and this permission notice shall be included in all
	copies or substantial portions of the Software.
	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
	IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
	FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
	AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
	LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
	OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
	SOFTWARE.
*/
/**
 * Uses RiTa WordNet to interact with the WordNet database,
 * and replaces a word with its first found synonym.
 * 
 * @author Prateek Mathur
 *
 */
import java.util.Random;
//import rita.RiWordNet;
public class SynonymReplacer {
	/**
	 * Takes a word and a POS as input and finds the first synonym of the word
	 * 
	 * @param word
	 * @return Synonym of the given word, null if none is found
	 */
	
	/*
	public String findSynonym(String word)	{
		
		if (word == null || word.length() == 0)	{
			// System.err.println("Empty word");
		}
		
		WordNet wordNet = new WordNet("WordNet-3.1", false, false);
		
		String [] poses = wordNet.getPos(word);
		String bestPos = wordNet.getBestPos(word);
		
		if (bestPos != null)	{
			if (bestPos.equals("n"))	{
				// We don't want to replace a noun
				return word;
			}
			String [] synonyms = wordNet.getAllSynonyms(word, bestPos);
			
			if (synonyms.length > 0)	{
				return synonyms[new Random().nextInt(synonyms.length)];
			}
		}
		
		 if (poses.length > 0) {
			for (String p : poses) {
				 if (p.equals("n"))	{
					 // We don't want to replace a noun
					 continue;
				 } 
				
				// System.out.println("Current POS- " + p + " ");
				
				String [] synonyms = wordNet.getAllSynonyms(word, p);
				if (synonyms.length > 0) {
					return synonyms[new Random().nextInt(synonyms.length)];
				} else {
					// System.err.println("No Synonyms found");
				}
			}
			System.out.println();
		} else	{
			// System.err.println("Cannot recognize the POS of the word");
			return null;
		} 
		
		return null;
	}	
	*/
}