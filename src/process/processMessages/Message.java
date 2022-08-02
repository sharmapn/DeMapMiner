package Process.processMessages;

import java.sql.Timestamp;
import java.util.Date;

public class Message {
	
	String Message,v_date,author,authorsrole,folder,file,subject,inReplyTo, wordsFoundList;	
	Integer message_ID, pepNumber;
	//String clusterBySenderFullName;	//String inReplyToUserUsingClusteredSender;
	Boolean required;
	Date m_date, accepted_date, rejected_date;
	Timestamp dateTimeStamp;
	//md5 = rs.getString(12);
	Integer identifierCount; //how many peps has this message 
			
	boolean proposalHasAcceptedLabel,proposalHasRejectedLabel;
	String accepted_sentence,rejected_sentence;
	
	public Message(){
		this.Message = "";		this.pepNumber = null;		this.v_date = null;			this.dateTimeStamp=null;
		this.author = ""; 		this.authorsrole="";		this.folder = "";			this.file = "";			
		this.message_ID = null;	this.subject = "";			this.required = false;		this.m_date = null;
		this.inReplyTo = "";	this.wordsFoundList = "";
		//this.proposalHasAcceptedLabel=false; this.proposalHasRejectedLabel=false; this.accepted_sentence=""; this.rejected_sentence="";
		//this.accepted_date = null; this.rejected_date = null;
	}
	
	public void setMessageData(String message, String v_date,Timestamp v_datetimestamp, String author,String authorsrole, String folder, String file, Integer message_ID, String subject, Boolean required, Date m_date,
			String inReplyTo, String wordsFoundList, Integer v_identifierCount) {
			//boolean proposalHasAcceptedLabel,String accepted_sentence,boolean proposalHasRejectedLabel,String rejected_sentence, Date v_accepted_date, Date v_rejected_date) {
		//super();
		this.Message = message;
		//this.pepNumber = pepNumber;
		this.v_date = v_date;				this.dateTimeStamp = v_datetimestamp;
		//System.out.println("\tdatetimestamp this.dateTimeStamp : "+ this.dateTimeStamp );
		this.author = author;				this.authorsrole = authorsrole;   this.folder = folder;		this.file = file;		
		this.message_ID = message_ID;		this.subject = subject;		this.required = required;
		this.m_date = m_date;				this.inReplyTo = inReplyTo;	this.wordsFoundList = wordsFoundList;
		this.identifierCount = v_identifierCount;
		//this.proposalHasAcceptedLabel=false; this.proposalHasRejectedLabel=false; this.accepted_sentence=""; this.rejected_sentence="";
		//this.accepted_date = v_accepted_date; this.rejected_date = v_rejected_date;
	}

	public String getMessage() {		return this.Message;	}
	public void setMessage(String message) {		this.Message = message;	}	
	public void setMessage(Integer pepNumber) {		this.pepNumber = pepNumber;	}
	public String getV_date() {		return this.v_date;	}	
	public void setV_date(String v_date) {		this.v_date = v_date;	}	
	public Timestamp getDateTimeStamp() {		return this.dateTimeStamp;	}	
	public void setDateTimeStamp(Timestamp v_dateTimeStamp) {		this.dateTimeStamp = v_dateTimeStamp;	}
	public String getAuthor() {		return author;	}
	public String getAuthorsRole() {		return authorsrole;	}
	public void setAuthor(String author) {		this.author = author;	}
	public String getFolder() {		return folder;	}
	public void setFolder(String folder) {		this.folder = folder;	}
	public String getFile() {		return file;	}
	public void setFile(String file) {		this.file = file;	}	
	public Integer getMessage_ID() {		return message_ID;}
	public void setMessage_ID(Integer message_ID) {		this.message_ID = message_ID;	}
	public String getSubject() {		return subject;	}
	public void setSubject(String subject) {	this.subject = subject;	}
	public Boolean getRequired() {	return required;	}
	public void setRequired(Boolean required) {		this.required = required;	}
	public Date getM_date() {		return m_date;	}
	public void setM_date(Date m_date) {		this.m_date = m_date;	}
	public String getInReplyTo() {		return inReplyTo;	}
	public void setInReplyTo(String inReplyTo) {		this.inReplyTo = inReplyTo;	}
	public String getWordsFoundList() {		return wordsFoundList;	}
	public void setWordsFoundList(String wordsFoundList) {		this.wordsFoundList = wordsFoundList;	}
	public Integer getIdentifierCount() {		return identifierCount;	}

	/*
	public boolean proposalHasAcceptedLabel() {		return proposalHasAcceptedLabel;	}
	public void setProposalHasAcceptedLabel(boolean proposalHasAcceptedLabel) {		this.proposalHasAcceptedLabel = proposalHasAcceptedLabel;	}
	public boolean proposalHasRejectedLabel() {		return proposalHasRejectedLabel;	}
	public void setProposalHasRejectedLabel(boolean proposalHasRejectedLabel) {		this.proposalHasRejectedLabel = proposalHasRejectedLabel;	}
	public String getAccepted_sentence() {		return accepted_sentence;	}
	public void setAccepted_sentence(String accepted_sentence) {		this.accepted_sentence = accepted_sentence;	}
	public String getRejected_sentence() {		return rejected_sentence;	}
	public void setRejected_sentence(String rejected_sentence) {		this.rejected_sentence = rejected_sentence;	}

	public Date getAccepted_date() {		return accepted_date;	}
	public void setAccepted_date(Date accepted_date) {		this.accepted_date = accepted_date;	}
	public Date getRejected_date() {		return rejected_date;	}
	public void setRejected_date(Date rejected_date) {		this.rejected_date = rejected_date;	}	
	*/
}
