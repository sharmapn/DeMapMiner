package Process.processMessages;

import java.util.ArrayList;

public class SentenceSimplification {
	/*sentence shortening
	verb replacement	
	I have closed the issue of file name formatting thanks to the informal poll results being very clear on the
	preferred format and also closed the idea of embedding the optimization level in the bytecode file metadata (that can be another PEP if someone
	cares to write that one). 	
	I have closed the issue of file name formatting as the informal poll results were very clear on the
	preferred format and also closed the idea of embedding the optimization level in the bytecode file metadata (that can be another PEP if someone
	cares to write that one). 
		
	Since there has been some controversy about the joining syntax used in PEP 428 filesystem path objects , I would like to run an informal poll about it.	
	Since there is some controversy about the joining syntax used in PEP 428 filesystem path objects, I would like to run an informal poll about it.
	*/
	
	static ArrayList<Pair> pList = new ArrayList<Pair>();
	static String replacePhrase = "", replacePhraseWith = "";
	
	public SentenceSimplification(){	
		 Pair p  = new Pair("whether or not", "if");					pList.add(p);
		 Pair p1 = new Pair("thanks to", "as");	     					pList.add(p1);
		 Pair p2 = new Pair("has been", "is");							pList.add(p2);
		 Pair p3 = new Pair("being", "were");							pList.add(p3);
		 //Pair p3 = new Pair("rejected", "consensus");	      			pList.add(p3);
		 //Pair p4 = new Pair("rejected", "no_support"); 					pList.add(p4);
		 //Pair p5 = new Pair("rejected", "rejected_by_author"); 			pList.add(p5);
		 //Pair p6 = new Pair("withdrawn", "withdrawn_by_author");		pList.add(p6);
	}
	
	public static String simplifySentence(String sentence){
		for (int f=0; f < pList.size(); f++){	
			replacePhrase     = pList.get(f).getFirst().toLowerCase().trim(); 
			replacePhraseWith = pList.get(f).getSecond().toString().toLowerCase().trim();
			if (sentence.contains(replacePhrase))
				sentence  = sentence.replaceAll(replacePhrase, replacePhraseWith);
		}		
		return sentence;
	}
}
