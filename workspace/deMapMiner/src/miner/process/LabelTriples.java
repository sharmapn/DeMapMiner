package miner.process;

public class LabelTriples {
	//Integer generalOrPEPSpecific;  // 1 = general, 2 = PEP Specific
	//Integer type;		//triple or double
	//Integer pepNumber;  //pep number - needed only for pep titles
	//String pepTitle;
	Integer lineNumber; //linenumber from inoutfile for debugging
	String idea;
	String subject;
	String verb;
	String object;
	String sentence;
	
	public void setLabel(Integer v_lineNumber,//Integer v_generalOrPEPSpecific, Integer v_type, /*Integer v_pepNumber, String v_pepTitle,*/ 
			String v_idea, String v_subject, String v_verb, String v_object, String v_sentence){
//		this.generalOrPEPSpecific = v_generalOrPEPSpecific;
//		this.type = v_type;
//		this.pepNumber = v_pepNumber;
//		this.pepTitle = v_pepTitle;
		this.lineNumber = v_lineNumber;
		//this.comment = v_comment;
		this.idea = v_idea;
		this.subject = v_subject;
		this.verb = v_verb;
		this.object = v_object;
		this.sentence = v_sentence;
	}
	
//	public Integer getGeneralOrPEPSpecific() {
//		return generalOrPEPSpecific;
//	}
//	public void setGeneralOrPEPSpecific(Integer generalOrPEPSpecific) {
//		this.generalOrPEPSpecific = generalOrPEPSpecific;
//	}
//	public Integer getType() {
//		return type;
//	}
//	public void setType(Integer type) {
//		this.type = type;
//	}
//	public Integer getPepNumber() {
//		return pepNumber;
//	}
//	public void setPepNumber(Integer pepNumber) {
//		this.pepNumber = pepNumber;
//	}
//	public String getPepTitle() {
//		return pepTitle;
//	}
//	public void setPepTitle(String pepTitle) {
//		this.pepTitle = pepTitle;
//	}
	public String getIdea() {
		return idea;
	}
	public void setIdea(String idea) {
		this.idea = idea;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getVerb() {
		return verb;
	}
	public void setVerb(String verb) {
		this.verb = verb;
	}
	public String getObject() {
		return object;
	}
	public void setObject(String object) {
		this.object = object;
		
	}
	public String getSentence() {
		return this.sentence;
	}
	
	public Integer getLineNumber() {
		return this.lineNumber;
	}
	public void setLineNumber(Integer v_lineNumber) {
		this.lineNumber = v_lineNumber;
	}
	
	public String getLabelTripleToString(){
		return (this.subject + " " + this.verb + " " + this.object);
	}
}
