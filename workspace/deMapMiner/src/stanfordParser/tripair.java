package stanfordParser;

class tripair {
	Integer key;
	String subject;
	String value;		
	public tripair(Integer k, String s, String v){			key = k; subject=s; value= v;		}		
	public Integer getKey() {			return key;		}
	public void setKey(Integer key) {			this.key = key;		}
	public String getSubject() {			return subject;		}
	public void setSubject(String subject) {			this.subject = subject;		}
	public String getValue() {			return value;		}
	public void setValue(String value) {			this.value = value;		}		
}