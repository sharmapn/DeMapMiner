package wordnet.jaws;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import edu.smu.tspell.wordnet.*;

public class WordnetSynonyms{
	static File f;
	static WordNetDatabase database;
	public static void init(){
		f=new File("C:\\DeMapMiner\\datafiles\\inputFiles\\WordNet-3.0\\dict");
		System.setProperty("wordnet.database.dir", f.toString());	//setting path for the WordNet Directory
		database = WordNetDatabase.getFileInstance();
	}
	
	public static ArrayList<String> returnSynonyms(String wordForm){		
		Synset[] synsets = database.getSynsets(wordForm);
		//  Display the word forms and definitions for synsets retrieved
		ArrayList<String> al = new ArrayList<String>();		
		if (synsets.length > 0){
			// add elements to al, including duplicates
			HashSet hs = new HashSet();
			for (int i = 0; i < synsets.length; i++){
				String[] wordForms = synsets[i].getWordForms();
				for (int j = 0; j < wordForms.length; j++)	{
					al.add(wordForms[j]);
				}				
				//removing duplicates
				hs.addAll(al);				al.clear();				al.addAll(hs);
				
				//showing all synsets
				for (int T = 0; T < al.size(); T++) {
//$$					System.out.println(al.get(T));
				}
			}
		}
		else	{
			System.err.println("No synsets exist that contain the word form '" + wordForm + "'");
		}
		return al;
	} 
	
	public static void main(String[] args){
		String wordForm = "decide";
		//  Get the synsets containing the word form=capicity
		
		init();
		System.setProperty("wordnet.database.dir", f.toString());	//setting path for the WordNet Directory
		
		WordNetDatabase database = WordNetDatabase.getFileInstance();
		Synset[] synsets = database.getSynsets(wordForm);
		//  Display the word forms and definitions for synsets retrieved
		
		if (synsets.length > 0){
			ArrayList<String> al = new ArrayList<String>();
			// add elements to al, including duplicates
			HashSet hs = new HashSet();
			for (int i = 0; i < synsets.length; i++){
				String[] wordForms = synsets[i].getWordForms();
				for (int j = 0; j < wordForms.length; j++)	{
					al.add(wordForms[j]);
					
				}				
				//removing duplicates
				hs.addAll(al);				al.clear();				al.addAll(hs);
				
				//showing all synsets
				System.out.println("showing all synsets for " + wordForm );
				for (int T = 0; T < al.size(); T++) {
					System.out.print(al.get(T) + ", ");
				}
			}
		}
		else	{
			System.err.println("No synsets exist that contain the word form '" + wordForm + "'");
		}
		
		// taken from https://github.com/xiejuncs/cross-document-coreference-resolution/blob/master/featureExtractor/Wordnet.java
		// https://www.cs.uic.edu/~spurohit/documents/Algorithm%20details.pdf
		Set<String> synonyms = getSynonym(wordForm, SynsetType.NOUN); //region 
		System.out.println("NOUN Synonyms for word: " + wordForm);	for (String s : synonyms) {  System.out.print(s + ", ");	} System.out.println();
		Set<String> synonymsVERB = getSynonym(wordForm, SynsetType.VERB); //region 
		System.out.println("VERB Synonyms for word: " + wordForm);	for (String s : synonymsVERB) {  System.out.print(s + ", ");	} System.out.println();
		Set<String> nounHypernyms = getNounHypernym(wordForm);  //tent
		System.out.println("NounHypernyms for word: " + wordForm);	for (String s : nounHypernyms) {  System.out.print(s + ", ");	}	System.out.println();
		Set<String> verbHypernyms = getVerbHypernym(wordForm); //shout
		System.out.println("VerbHypernyms for word: " + wordForm);	for (String s : verbHypernyms) {  System.out.print(s + ", ");	}	System.out.println();
		Set<String> derivationallyForm = getDerivationallyRelatedForms(wordForm, SynsetType.VERB); //develop
		System.out.println("DerivationallyForm for word: " + wordForm);	for (String s : derivationallyForm) {  System.out.print(s + ", ");	}	System.out.println();
		System.out.println("done");
		
	}
	
	public static Set<String> getSynonym(String lemma, SynsetType type) {
		Set<String> synonyms = new HashSet<String>();
		Synset[] synsets = database.getSynsets(lemma, type);
		for (Synset synset : synsets) {
			String[] wordforms = synset.getWordForms();
			synonyms.addAll(Arrays.asList(wordforms));
		}

		return synonyms;
	}

	/**
	 * get derivationally form
	 * 
	 * @param lemma
	 * @param type
	 * @return
	 */
	public static Set<String> getDerivationallyRelatedForms(String lemma, SynsetType type) {
		Set<String> derivationallyForm = new HashSet<String>();
		Synset[] synsets = database.getSynsets(lemma, type);
		for (Synset synset : synsets) {
			WordSense[] senses = synset.getDerivationallyRelatedForms(lemma);
			for (WordSense sense : senses) {
				derivationallyForm.add(sense.getWordForm());
			}
		}

		return derivationallyForm;
	}

	/**
	 * get noun hypernym
	 * 
	 * @param lemma
	 * @param type
	 * @return
	 */
	public static Set<String> getNounHypernym(String lemma) {
		Set<String> hypernyms = new HashSet<String>();
		Synset[] synsets = database.getSynsets(lemma, SynsetType.NOUN);
		for (Synset synset : synsets) {
			NounSynset nounSynset = (NounSynset) synset;
			NounSynset[] hypernymSynset = nounSynset.getHypernyms();
			for (NounSynset set : hypernymSynset) {
				hypernyms.addAll(Arrays.asList(set.getWordForms()));
			}
		}

		return hypernyms;
	}

	/**
	 * get verb hypernym
	 * 
	 * @param lemma
	 * @param type
	 * @return
	 */
	public static Set<String> getVerbHypernym(String lemma) {
		Set<String> hypernyms = new HashSet<String>();
		Synset[] synsets = database.getSynsets(lemma, SynsetType.VERB);
		for (Synset synset : synsets) {
			VerbSynset verbSynset = (VerbSynset) synset;
			VerbSynset[] hypernymSynset = verbSynset.getHypernyms();
			for (VerbSynset set : hypernymSynset) {
				hypernyms.addAll(Arrays.asList(set.getWordForms()));
			}
		}

		return hypernyms;
	}	

	
}
