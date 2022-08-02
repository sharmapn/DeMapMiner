package rankingEvaluation;

public class messageText {
	int messageID;
	String text;
	messageText(int m, String t){
		messageID = m;  text =t;
	}
	public int getMessageID() {		return messageID;		}
	public void setMessageID(int v_messageID) {			this.messageID = messageID;		}
	public String getText() {			return text;		}
	public void setText(String v_text) {			this.text = text;		}
};
