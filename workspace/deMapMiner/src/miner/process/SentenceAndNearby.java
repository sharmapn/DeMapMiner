package miner.process;

public class SentenceAndNearby {
	String CurrentSentenceString,PreviousSentenceString,prevSentence,nextSentence;
	Integer paragraphCounter,sentenceCounter, sentenceCounterinParagraph, lineNumber; 	
	String previousParagraph, entireParagraph,nextParagraph;
	//dec 2018
	boolean isFirstParagraph, isLastParagraph;
	
	public SentenceAndNearby(){
		this.CurrentSentenceString = "";		this.PreviousSentenceString = "";		this.prevSentence = "";				this.nextSentence = "";
		this.paragraphCounter = null;			this.sentenceCounter = null;			this.previousParagraph = "";		this.entireParagraph = "";
		this.nextParagraph = "";				this.isFirstParagraph = false; 			this.isLastParagraph =false;
	}
	
	//CurrentSentenceString, PreviousSentenceString, prevSentence, paragraphCounter, sentenceCounter, previousParagraph, entireParagraph,nextParagraph
	public SentenceAndNearby(String currentSentenceString, String previousSentenceString, String prevSentence,String nextSentence,
			Integer paragraphCounter, Integer sentenceCounter, Integer sentenceCounterinParagraph, String previousParagraph, String entireParagraph,String nextParagraph,Integer lineNum) {
		//super();
		this.CurrentSentenceString = currentSentenceString;		this.PreviousSentenceString = previousSentenceString;		this.prevSentence = prevSentence;
		this.nextSentence = nextSentence;		this.paragraphCounter = paragraphCounter;		this.sentenceCounter = sentenceCounter;
		this.previousParagraph = previousParagraph;		this.entireParagraph = entireParagraph;		this.nextParagraph = nextParagraph; this.lineNumber = lineNum;
	}

	public String getCurrentSentenceString() {		return this.CurrentSentenceString;	}
	public void setCurrentSentenceString(String currentSentenceString) {		CurrentSentenceString = currentSentenceString;	}
	public String getPreviousSentenceString() {
		if(this.PreviousSentenceString==null)			return "";
		return this.PreviousSentenceString;
	}
	public void setPreviousSentenceString(String previousSentenceString) {		PreviousSentenceString = previousSentenceString;	}
	public String getPrevSentence() {		return this.prevSentence;	}
	public void setPrevSentence(String prevSentence) {		this.prevSentence = prevSentence;	}	
	public String getNextSentence() {		return nextSentence;	}
	public void setNextSentence(String nextSentence) {		this.nextSentence = nextSentence;	}
	public Integer getParagraphCounter() {		return paragraphCounter;	}
	public void setParagraphCounter(Integer paragraphCounter) {		this.paragraphCounter = paragraphCounter;	}
	public Integer getSentenceCounter() {		return sentenceCounter;	}
	public void setSentenceCounter(Integer sentenceCounter) {		this.sentenceCounter = sentenceCounter;	}
	public String getPreviousParagraph() {		return previousParagraph;	}
	public void setPreviousParagraph(String previousParagraph) {		this.previousParagraph = previousParagraph;	}
	public String getEntireParagraph() {		return entireParagraph;	}
	public void setEntireParagraph(String entireParagraph) {		this.entireParagraph = entireParagraph;	}
	public String getNextParagraph() {		return nextParagraph;}
	public void setNextParagraph(String nextParagraph) {		this.nextParagraph = nextParagraph;	}
	public Integer getSentenceLineNumber() {		return lineNumber;}
	public void setSentenceLineNumbe(Integer ln) {		this.lineNumber = ln;	}
	public boolean isFirstParagraph() {		return isFirstParagraph;	}
	public void setFirstParagraph(boolean isFirstParagraph) {		this.isFirstParagraph = isFirstParagraph;	}
	public boolean isLastParagraph() {		return isLastParagraph;	}
	public void setLastParagraph(boolean isLastParagraph) {		this.isLastParagraph = isLastParagraph;	}			
	public Integer getSentenceCounterinParagraph() {		return sentenceCounterinParagraph;	}
	public void setSentenceCounterinParagraph(Integer sentenceCounterinParagraph) {		this.sentenceCounterinParagraph = sentenceCounterinParagraph;	}

	public void finalize() {	    //System.out.println("Probability instance is getting destroyed"); 
	}
}
