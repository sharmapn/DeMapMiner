package Process.processLabels;

import java.util.Arrays;
import java.util.List;
//very primitive way of matching concepts in text..maybe from 2015 
public class processText {
	
	//pep accepted bdfl-guido-gvr
	//pep rejected bdfl-guido-gvr
	//pep accepted commnity
	
	//artifact action entity ...reason
	
	static List<String> Entity =	Arrays.asList(new String[]{"GUIDO","BDFL","BDFL DELEGATE","CONSENSUS"});
	static List<String> Action = 	Arrays.asList(new String[]{"ACCEPT","REJECT","WITHDRAW","DEFER"});
	static List<String> Artifact = 	Arrays.asList(new String[]{"PEP"});
	static List<String> Reason = 	Arrays.asList(new String[]{"CONSENSUS","PRONOUNCEMENT"});	
	
	//concepts
	//pep acceptance has these words (fom wordlist)
	
	public static void main(String[] args){		
		SearchCriteria scr = new SearchCriteria();		
		String str = "Pep has been accepted by bdfl";
		for(String s : str.split(" ")){ //splits your String using space char		    
			processWord(s,scr);	
			//if contains question
			
		}
	}	
	
	public static void processWord(String word, SearchCriteria sc){
	    if(Entity.contains(word.toUpperCase()))	    {
	        sc.setEntity(word.toUpperCase());	        System.out.println("word " + word + " is entity " + sc.getEntity());
	    }	    
	    if(Action.contains(word.toUpperCase()))	    {
	        sc.setAction(word.toUpperCase());	        System.out.println("word " + word + " is action " + sc.getAction());	        
	    }
	    
	    if(Artifact.contains(word.toUpperCase()))	    {
	        sc.setArtifact(word.toUpperCase());	        System.out.println("word " + word + " is artifact " + sc.getArtifact());	        
	    }
	    if(Reason.contains(word.toUpperCase()))	    {
	        sc.setReason(word.toUpperCase());	        System.out.println("word " + word + " is reason " + sc.getReason());	        
	    }	    
	}
}
