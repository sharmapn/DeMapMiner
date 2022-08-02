package miner.stringMatching;

import miner.models.Pep;

public class checkRegion {
	
	public static String checkRegion (Pep[] p, String wordsFoundList, Integer v_pepNumber, Integer v_messageCounter){
		
		String v_sentence, v_nextSentence,v_nextNextSentence, v_previousSentence,v_previousPreviousSentence;
		
		for (Integer i=0;i< 1000;i++) {
			v_sentence = p[v_pepNumber].messages[v_messageCounter].sentences[i];
			if (v_sentence.toLowerCase().contains("accepted")){
				v_nextSentence = p[v_pepNumber].messages[v_messageCounter].sentences[i+1];
				v_nextNextSentence = p[v_pepNumber].messages[v_messageCounter].sentences[i+2];
				v_previousSentence = p[v_pepNumber].messages[v_messageCounter].sentences[i-1];
				v_previousPreviousSentence = p[v_pepNumber].messages[v_messageCounter].sentences[i-2];
			//	System.out.println(" v_nextSentence - " + v_nextSentence);
			//	System.out.println(" v_nextNextSentence - " + v_nextNextSentence);
			//	System.out.println(" v_previousSentence - " + v_previousSentence);
			//	System.out.println(" v_previousPreviousSentence - " + v_previousPreviousSentence);
			//	System.out.println(" here ----messageid " + p[v_pepNumber].messages[v_messageCounter].getMessageId());
				
				if (v_previousPreviousSentence.toLowerCase().contains("bdfl pronouncement")) {
					System.out.println(" v_nextSentence - ");
					wordsFoundList += " pep-accepted-bdfl-pronouncement";  
					p[v_pepNumber].messages[v_messageCounter].setMessageGist("pep-accepted-bdfl-pronouncement");
				}
				
			}
		}		
		return wordsFoundList;
		
	}
	
	static String checkForSpecificRegions(String v_message, String Word) {
		   //first fine location of word
		   
		   Integer a = v_message.toLowerCase().indexOf(Word);	   
		   Integer b,c,d, e; 
		   String line;
		   
		   if (a>0){
			   System.out.println("hello");
		   }
		   /*
			   	   b = v_message.indexOf("\n", a+1); // end of line of acceptance
			   for (int v=0;v<10;v++){			    
				   c = v_message.indexOf("\n", b+1);
				   d = v_message.indexOf("\n", c+1);
				   line = v_message.substring(c, d);
				   if (line.length() >0) {
						  System.out.println(line);
					  }
				   b=d;
			   }		   
		   }
		   */
		   
//		   if (a>0){
//			   System.out.println("hello");
//			   f = v_message.indexOf("\n");
		//	   g = v_message.indexOf("\n", f);
			   
			   
		
		 //  }
		   
		   if (v_message.toLowerCase().contains(Word))  {
//			  	System.out.println( "here xxx");
		       }
		   if (v_message.toLowerCase().contains("\n"))  {
//			  	System.out.println( "here yyy");
		       }
		   
	 //      if (sentence.toLowerCase().contains("Acceptance") )  {
	 //   	   if (sentence.toLowerCase().contains("decision"))
	  //  		   wordsFoundList += " bdfl decision &"; 
	    	     //This PEP was accepted by the BDFL
				// Because of the exceptionally short period from first draft to acceptance, the main objections brought up after acceptance were carefully considered and
				 //have been reflected in the "Alternate proposals" section below.
				 //none of the discussion changed the BDFL's mind and the PEP's acceptance is now final.  
				 //Suggestions for clarifying edits are still welcome 
//			 }
		   return " REGION FOUND";
	   }

}
