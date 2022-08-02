package ManualAnalysis.StatesAndReasons;

import java.sql.Timestamp;
import java.util.Date;

public class reasonRow {
	int proposal,mid; 
	Timestamp dateVal;
	String sentenceString, location, combination,authorsRole;
	boolean reason;

	void setData(int p, int mid, Timestamp dt, String sent, String loc, String com, boolean v_reason, String v_author) {
		this.proposal = p;				this.location = loc;
		this.mid = mid;					this.combination = com; 
		this.dateVal = dt;				this.sentenceString = sent;
		this.reason = v_reason;			this.authorsRole = v_author;
	}
	
	public int getProposal() {
		return proposal;
	}
	public void setProposal(int proposal) {
		this.proposal = proposal;
	}
	public int getMid() {
		return mid;
	}
	public void setMid(int mid) {
		this.mid = mid;
	}
	public Timestamp getDateVal() {
		return dateVal;
	}
	public void setDateVal(Timestamp dateVal) {
		this.dateVal = dateVal;
	}
	public String getSentenceString() {
		return sentenceString;
	}
	public void setSentenceString(String sentenceString) {
		this.sentenceString = sentenceString;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getCombination() {
		return combination;
	}
	public void setCombination(String combination) {
		this.combination = combination;
	}
	public Boolean getReason() {
		return reason;
	}
	public String getAuthorsRole() {
		return authorsRole;
	}
	public void setAuthorsRole(String v_authorsRole) {
		this.authorsRole = v_authorsRole;
	}
}
