package rankingEvaluation;

class EvalPair {
	Integer id, messageid;
	String sentence;
	
	public EvalPair(Integer f, Integer mid, String s){	
		this.id = f;	this.messageid = mid;	this.sentence = s;
	}	
	public Integer getID() {	return this.id;	}	
	public void setID(Integer v_id) {			this.id = v_id;	}	
	
	public Integer getMessageID() {	return this.messageid;	}	
	public void setMessageID(Integer mid) {			this.messageid = mid;	}
	
	public String getSentence()	{	return sentence;	}	
	public void setSentence(String sent) {	this.sentence = sent;} 
};
