package miner.models;

import java.util.Date;

public class Pep {
	Integer pepNumber;
	Date startDate;
	Date endDate;
	
	Date votingStartDate;
	Date votingEndDate;
	Date alternateVotingStartDate;
	Date alternateVotingEndDate;
	Date alternateVotingResultsDate;
	Date discussionStartDate;
	Date discussionEndDate;
	
	Integer stateCounter;
	
	//private States [] states;
	//private Message [] messages;
	States[] states = new States[100];
	public Message[] messages = new Message[10000];
	
	public Pep(){
	//	this.votingStartDate = null;
		this.stateCounter = 0;
		for (int i=0; i< 100;i++){
			states[i] = new States();
		}
		for (int j=0; j< 10000;j++){
			messages[j] = new Message();
		}
	}
	
	public void destruct(){
		for (int i=0; i< 100;i++){
			states[i] = null;
		}
		for (int j=0; j< 10000;j++){
			messages[j] = null;
		}
	}
	
	public void displayMessageClassifier(){
		Boolean discussionOn = false;     
        Boolean discussionStart = null;
        Boolean discussionEnd = null;
        Integer discussionMessageCounter = 0;		
		
		for (int j=0; j< 10000;j++){
			if(messages[j].getClassifier() == "Discussion")
			{
				if (discussionOn == true)
				{
					discussionMessageCounter++;
				}
				else {
					discussionOn = true;
					System.out.println(this.pepNumber + " Discussion start" + messages[j].getClassifierDate());
				}
				
			}
			else {
				if(discussionMessageCounter>5) {
					System.out.println(this.pepNumber + " Discussion start" + messages[j].getClassifierDate());
				}
				discussionMessageCounter=0;
				discussionOn = false;
			}
			// do nothing
			//if !=discussion
			//next 
			
			
			//see last
			//if last = discussion and tis discussion
			//do nothing
			//if last != discussion and this = discussion
			// set it tp start dis
			
		}
	}
	
	public void addState(String v_state, String v_date){
	
		states[this.stateCounter].setState(v_state);
		states[this.stateCounter].setDate(v_date);
		this.stateCounter++;
	}
	
	public String outputStates(){
		String add = null;
		for (int i=0; i< stateCounter;i++){
			 System.out.println("\n " + pepNumber +  states[i].getState() + " " +  states[i].getDate() );
			 add += "\n " + pepNumber +  states[i].getState() + " " +  states[i].getDate() ;
		}
		return add;
	}
	
    public void setPepNumber(Integer v_pepNumber) {
        this.pepNumber = v_pepNumber;
    }
  
    public int getPepNumber() {
        return this.pepNumber;
    }
    public Boolean setvotingStartDate(Date v_votingStartDate) {    	
    	if (this.votingStartDate == null) {
    		this.votingStartDate = v_votingStartDate;
    		return true;  //return boolean to indicate first voting instance and therefore output
    	}
    	if (v_votingStartDate.before(this.votingStartDate)){ 
    		this.votingStartDate = v_votingStartDate;  
    	}
    	return false;
//    	System.out.println("-------------------------added voting start date " + this.votingStartDate);
    }
	
    public void setvotingEndDate(Date v_votingEndDate) {     	
    	if (this.votingEndDate == null)
    		this.votingEndDate = v_votingEndDate;
    	if (v_votingEndDate.after(this.votingEndDate))
    		this.votingEndDate = v_votingEndDate;
 //   	System.out.println("-------------------------added voting end date " + this.votingEndDate);
    }
    
    public void setalternateVotingStartDate(Date v_alternateVotingStartDate) {
    	if (this.alternateVotingStartDate == null)
    		this.alternateVotingStartDate = v_alternateVotingStartDate;  
    	if (v_alternateVotingStartDate.before(this.votingStartDate)){ 
    		this.alternateVotingStartDate = v_alternateVotingStartDate;  
    	}
    	System.out.println("-------------------------added alternate voting start date " + this.alternateVotingStartDate);
    }
	
    public void setalternateVotingEndDate(Date v_alternateVotingEndDate) {
    	if (this.alternateVotingEndDate == null)
    		this.alternateVotingEndDate = v_alternateVotingEndDate;
    	if (v_alternateVotingEndDate.after(this.votingEndDate))
    		this.alternateVotingEndDate = v_alternateVotingEndDate;
    	System.out.println("-------------------------added alternate voting end date " + this.alternateVotingEndDate);
    }
    
    public void setalternateVotingResultsDate(Date v_alternateVotingResultsDate) {
    	if (this.alternateVotingResultsDate == null)
    		this.alternateVotingResultsDate = v_alternateVotingResultsDate;
    	if (v_alternateVotingResultsDate.after(this.alternateVotingResultsDate))
    		this.alternateVotingResultsDate = v_alternateVotingResultsDate;
    }
    
    public void setdiscussionStartDate(Date v_discussionStartDate) {
        this.discussionStartDate = v_discussionStartDate;
    }
	
    public void setdiscussionEndDate(Date v_discussionEndDate) {
        this.discussionEndDate = discussionEndDate;
    }
    
    public Date getalternateVotingStartDate(){
    	return alternateVotingStartDate;
    }
    public Date getalternateVotingEndDate(){
    	return alternateVotingEndDate;
	 
    }
    public Date getVotingStartDate(){
    	 return votingStartDate;	    
    }
    public Date getVotingEndDate(){
    	   return votingEndDate;
    }
    public Date getDiscussionStartDate(){
	    return discussionStartDate;
    }
    public Date getDiscussionEndDate(){
	    return discussionEndDate;
    }
    public Date getAlternativeVoteResultsDate(){
	    return alternateVotingResultsDate;
    }
    
    public String getState(Integer index){
	    return states[index].getState();
    }
    
    public String getDate(Integer index){
    	return states[index].getDate();
    }
    
    
    class PepIterator
    {
       private int messageIndex = 0;
       public boolean hasMoreMessages ()
       {
          return messageIndex < messages.length;
       }
       public Object nextMessage ()
       {
          return !hasMoreMessages () ? null : messages [messageIndex++];
       }
    }
}
