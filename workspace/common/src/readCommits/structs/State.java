package readCommits.structs;

import java.util.Date;

public class State {

	String type;
	String author;
	String date;
	//added
	Date dateTimeStamp;
	String proposalTitle;
	String entireMessage;
	
	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public Date getDateTimeStamp() {
		return dateTimeStamp;
	}

	public void setDateTimeStamp(Date dateTimeStamp) {
		this.dateTimeStamp = dateTimeStamp;
	}

	public String getPepTitle() {
		return proposalTitle;
	}

	public void setPepTitle(String proposalTitle) {
		this.proposalTitle = proposalTitle;
	}

	public String getEntireMessage() {
		return entireMessage;
	}

	public void setEntireMessage(String entireMessage) {
		this.entireMessage = entireMessage;
	}

	public void setType(String type) {
		this.type = type;
	}

	public State(String type, String author, String date, Date v_dateTimeStamp,String v_proposalTitle, String v_entireMessage) {
		this.type = type.trim();
		this.author = author;
		this.date = date;
		this.dateTimeStamp = v_dateTimeStamp;
		this.proposalTitle = v_proposalTitle;
		this.entireMessage = v_entireMessage;
	}
	
	public String getType(){
		return type;
	}
	
	public String toString(){
		return type + ", " + dateTimeStamp + ", " + author + ", ";
	}
}
