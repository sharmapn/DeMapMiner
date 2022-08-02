package miner.models;

import java.util.Date;

public class Message {
	
	public class IdeaSentencePair {
		String idea;
		String sentence;
		
		//IdeaSentencePair(String v_idea, String v_sentence){
			
		//}
		String getIdea(){
			return this.idea;
		}
		String getSentence(){
			return this.sentence;
		}
		
		void getIdeaAndSentence(){
			System.out.println(this.idea + " " + this.sentence);
		}
		public void IdeaSentencePair(String v_idea, String v_sentence) {
			 this.idea = v_idea;
			 this.sentence = v_sentence;			
		}
	}
	
	  private Integer message_id = null;
	  private String classifier = null;
	  private String classifierDate = null;
	  private String wordList = null;
	  private String author = null;
	  private String message = null;
	  private Integer messageIdOfMessageInDB = null;
	  private String message_gist = null;
	  
	  private String requestIdeas = null;
	  private String requestComments = null;
	  private String requestFeedback = null;	
	  
	  public String[] sentences = new String[1000];
	  Integer sentenceCounter = 0;
	  
	  // the identified ideas and the sentences they are in
	  IdeaSentencePair[] is = new IdeaSentencePair[50];
	  Integer isCounter = 0;
	  
	  public enum message_classifier {discussion,voting, accepting, rejecting};
	  
	  private message_classifier mc;
	  public message_classifier getmc() { return mc; }
	  public void setResult(message_classifier v_mc) { this.mc = v_mc; }
	  
	  public void addIdeaSentence(String v_idea, String v_sentence){
		  is[isCounter].IdeaSentencePair(v_idea,v_sentence);
			// is[isCounter].sentence = v_sentence;
		  this.isCounter++;
	  }
	  
	  public Integer getCounter(){
		  return this.isCounter;
	  }
	  
	  public void getAllIdeasSentence(){
		 // for (int n=0; n< getCounter();n++){
			  is[0].getIdeaAndSentence();			  
		//  }	  
	  }
	  
	  public Message(){
			//	this.votingStartDate = null;
				this.classifier = "Discussion";
			}
	    
	    public void tellItLikeItIs() {
	        switch (mc) {
	            case discussion:
	                System.out.println("Mondays are bad.");
	                break;
	                    
	            case voting:
	                System.out.println("Fridays are better.");
	                break;
	                         
	            case accepting: case rejecting:
	                System.out.println("Weekends are best.");
	                break;
	                        
	            default:
	                System.out.println("Midweek days are so-so.");
	                break;
	        }
	    }
	  
	  public void setMessageId(Integer m_id){
		  this.message_id = m_id;
	  }
	  
	  public void setSentence(String v_sentence){
		  sentences[sentenceCounter] = v_sentence;
		  sentenceCounter++;
	  }
	  
	  public String getSentence(Integer v_sentenceCounter){
		  return sentences[v_sentenceCounter];		  
	  }
	  
	  public Integer getCurrentSentenceCounter() {
		  return sentenceCounter;
	  }
	  
	  public String getPreviousSentence(Integer sentenceIndex) {
		  return sentences[sentenceIndex];
	  }
	  
	  public String getNextSentence(Integer sentenceIndex) {
		  return sentences[sentenceIndex];
	  }
	  
	  public void setMessageIdOfMessageInDB(Integer v_MessageIdOfMessageInDB){
	  	this.messageIdOfMessageInDB = v_MessageIdOfMessageInDB;
	  }
	  
	  public void setMessageGist(String v_message_gist){
		  this.message_gist = v_message_gist;
	  }
	  
	  public void setClassifier(String v_classifier){
		  this.classifier = v_classifier;
	  }
	  
	  public void setClassifierDate(String v_classifierDate){
		  this.classifierDate = v_classifierDate;
	  }
	  
	  public void setWordList(String v_wordlist){
		  this.wordList = v_wordlist;
	  }
	  
	  public void setAuthor(String v_author){
		  this.author = v_author;
	  }
	    
	  public void setMessage(String v_message){
		  this.message = v_message;
	  }  
	  
	  public Integer getMessageId(){
		    return this.message_id;
	  }
	  
	  public String getMessageGist(){
		    return this.message_gist;
	  }
	  
	  public String getClassifier(){
		    return this.classifier;
	  }
	  
	  public String getClassifierDate(){
		    return this.classifierDate;
	  }
	  
	  public String getAuthor(){
		    return this.author;
	  }
	  
	  public String getWordList(){
		    return this.wordList;
	  }
	  
	  public String getMessage(){
		    return this.message;
	  }
	  
	  public Integer getMessageLength(){
		    return this.message.length();
	  }
	  
	  public Integer getMessageIdOfMessageInDB(){
		    return this.messageIdOfMessageInDB;
	  }
	
	  
	  public String toString(){
		  //return car details as formatted string
		  //output should be single line containing the make model and reg number
		  //followed by either "Available for hire" or "On hire to: <name>"
		     return "Message ID number: "+ getMessageId()+"-"+"Classifier is: "+ getClassifier();
		 } 
}
