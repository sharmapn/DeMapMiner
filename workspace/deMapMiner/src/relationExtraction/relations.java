package relationExtraction;

public class relations {
	private String subject;
	private String verb;
	private String object;
	private String sentence;
	
	public relations(String subject, String verb, String object, String sentence) {
		this.subject = subject;
		this.verb = verb;
		this.object = object;
		this.sentence = sentence;
	}
	
	public String getSubject() {
		return subject;
	}
	public String getVerb() {
		return verb;
	}
	public String getObject() {
		return object;
	}
	public String getSentence() {
		return sentence;
	}
	
}