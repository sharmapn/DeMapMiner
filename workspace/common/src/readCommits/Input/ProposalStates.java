package readCommits.Input;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ProposalStates {
	
	String proposalNumber;
	String Status;
	String StatusTo;
	Date dateTimeStamp;
	String proposalTitle;
	
	public ProposalStates(String v_proposalNumber,String v_status, String v_statusTo, Date v_dateTimeStamp, String v_proposalTitle){
		proposalNumber = v_proposalNumber;
		Status = v_status;
		StatusTo= v_statusTo;
		dateTimeStamp = v_dateTimeStamp;
		proposalTitle = v_proposalTitle;
	}
	
	public String getProposalNumber() {
		return proposalNumber;
	}

	public void setProposalNumber(String proposalNumber) {
		this.proposalNumber = proposalNumber;
	}

	public String getProposalTitle() {
		return proposalTitle;
	}

	public void setProposalTitle(String proposalTitle) {
		this.proposalTitle = proposalTitle;
	}

	public void setStatus(String status) {
		Status = status;
	}

	public void setStatusTo(String statusTo) {
		StatusTo = statusTo;
	}

	public void setDateTimeStamp(Date dateTimeStamp) {
		this.dateTimeStamp = dateTimeStamp;
	}

	public String getProposal(){
		return proposalNumber;
	}
	public String getStatus(){
		return Status;
	}	
	public String getStatusTo(){
		return StatusTo;
	}
	public Date getDateTimeStamp(){
		return dateTimeStamp;
	}

}
