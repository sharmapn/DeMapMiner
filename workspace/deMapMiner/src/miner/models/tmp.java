package miner.models;

public class tmp{
	String ListOfWordsFound;
	String previousSentence;
	
	tmp(String v_previousSentence, String v_ListOfWordsFound)
	{	
		ListOfWordsFound = v_ListOfWordsFound;
		previousSentence = v_previousSentence;
		}
	
	public void setLOW(String v_ListOfWordsFound){
		this.ListOfWordsFound = v_ListOfWordsFound;
	}
	
	public void setPS(String v_previousSentence){
		this.previousSentence = v_previousSentence;
	}

	public String getLOW(){
		return ListOfWordsFound;
	}
	public String getPS(){
		return previousSentence;
	}
}   