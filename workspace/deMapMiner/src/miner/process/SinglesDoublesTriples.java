package miner.process;

public class SinglesDoublesTriples {
	String label;
	String singleOrDoubleTerms;
	boolean isSingle,isDouble,isTriple;
	String single, subject,verb,object;
	
	public SinglesDoublesTriples(){
		label="";		singleOrDoubleTerms="";		isSingle =isDouble = isTriple =false;
		single=subject=verb=object="";
	}
	
	public void setLabel(String label) {		this.label = label;	}
	
	public String getLabel() {		return this.label;	}	
		
	public void setSingle(String v_Single){		isSingle=true;		single = v_Single;	}
	public boolean isSingle(){		return isSingle;	}
	public String getSingle() {		return this.single;	}
	public void setDouble(String v_subject, String v_verb){
		subject = v_subject; 		verb    = v_verb;
		isDouble=true;
	}
	public boolean isDouble(){		return isDouble;	}
	
	public String getSingleOrDoubleTerms() {		return singleOrDoubleTerms;	}
	public void setSingleOrDoubleTerms(String singleOrDoubleTerms) {		this.singleOrDoubleTerms = singleOrDoubleTerms;	}

	public String getDouble(){		return (subject + "," + verb);	}
	
	public void setTriple(String v_subject, String v_verb, String v_object){
		subject = v_subject;		verb    = v_verb;		object  = v_object;		isTriple = true;
	}
	public boolean isTriple(){		return isTriple;	}
	public String getSubject(){		return subject;	}
	public String getVerb(){		return verb;	}
	public String getObject(){		return object;	}
	public String getTriple(){		return (subject + "," + verb + "," +object);	}
}
