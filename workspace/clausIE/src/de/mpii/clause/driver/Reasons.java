package de.mpii.clause.driver;

import java.util.ArrayList;

public final class Reasons {

	private boolean reasonExists;
	private String allReasons;
	private String reasonsInNearbyStates;
	private String reasonInSentence;
	private String reasonTriplesInSentence;
	private String reasonTermsInMatchedTriple;
	private String reasonTermsInNearbySentencesParagraphs;
	private String reasonTermsInPreviousSentence;
	private String reasonTermsInNextSentence;
	private String reasonTriplesInPreviousSentence;
	private String reasonTriplesInNextSentence;
	private String reasonTriplesInNearbySentencesParagraphs;
	ArrayList<String> reasonsFoundList;

	public Reasons() {
		reasonExists=false;
		allReasons="";
		reasonInSentence="";
		reasonTermsInMatchedTriple="";
		reasonTermsInNearbySentencesParagraphs="";
		reasonTriplesInNearbySentencesParagraphs="";
		reasonTermsInPreviousSentence="";
		reasonTermsInNextSentence="";
		reasonTriplesInPreviousSentence="";
		reasonTriplesInNextSentence="";
		//reasonsFoundList=null;
		reasonsFoundList = new ArrayList<String>();
	}

	public void SetValues(boolean v_reasonExists, String v_reason) {

		this.reasonExists = v_reasonExists;
		this.allReasons = v_reason;
	}

	public void addToReasonListArray(String v_reason) {
		Boolean found= false;
		if(reasonsFoundList.isEmpty())
			reasonsFoundList.add(v_reason);	
		
		else{
			//make sure reason doesnt exist already in the list
			for(String r: reasonsFoundList)
				if(r.equals(v_reason)) {
					found = true;
					System.out.println("\t\t\tReason found not adding: "+v_reason);
				}
			
			if (found ==false){
				reasonsFoundList.add(v_reason);
				System.out.println("\t\t\tReason not found, adding: "+v_reason);
			}
		}
	}

	public boolean getIfReasonExists() {
		return reasonExists;

	}	
	
	public boolean isReasonExists() {
		return reasonExists;
	}

	public void setReasonExists(boolean reasonExists) {
		this.reasonExists = reasonExists;
	}

	public String getReasonTermsInMatchedTriple() {
		return reasonTermsInMatchedTriple;
	}

	public void setReasonTermsInMatchedTriple(String reasonTermsInMatchedTriple) {
		this.reasonTermsInMatchedTriple = reasonTermsInMatchedTriple;
	}

	public String getReasonTermsInNearbySentencesParagraphs() {
		return reasonTermsInNearbySentencesParagraphs;
	}
		
	public String getReasonTermsInPreviousSentence() {
		return reasonTermsInPreviousSentence;
	}

	public void setReasonTermsInPreviousSentence(String reasonTermsInPreviousSentence) {
		this.reasonTermsInPreviousSentence = reasonTermsInPreviousSentence;
	}

	public String getReasonTermsInNextSentence() {
		return reasonTermsInNextSentence;
	}

	public void setReasonTermsInNextSentence(String reasonTermsInNextSentence) {
		this.reasonTermsInNextSentence = reasonTermsInNextSentence;
	}

	public String getReasonTriplesInPreviousSentence() {
		return reasonTriplesInPreviousSentence;
	}

	public void setReasonTriplesInPreviousSentence(String reasonTriplesInPreviousSentence) {
		this.reasonTriplesInPreviousSentence = reasonTriplesInPreviousSentence;
	}

	public String getReasonTriplesInNextSentence() {
		return reasonTriplesInNextSentence;
	}

	public void setReasonTriplesInNextSentence(String reasonTriplesInNextSentence) {
		this.reasonTriplesInNextSentence = reasonTriplesInNextSentence;
	}

	public void setReasonTermsInNearbySentencesParagraphs(String reasonTermsInNearbySentencesParagraphs) {
		this.reasonTermsInNearbySentencesParagraphs = reasonTermsInNearbySentencesParagraphs;
	}

	public String getReasonTriplesInNearbySentencesParagraphs() {
		return reasonTriplesInNearbySentencesParagraphs;
	}

	public void setReasonTriplesInNearbySentencesParagraphs(String reasonTriplesInNearbySentencesParagraphs) {
		this.reasonTriplesInNearbySentencesParagraphs = reasonTriplesInNearbySentencesParagraphs;
	}

	public ArrayList<String> getReasonsFoundList() {
		return reasonsFoundList;
	}

	public void setReasonsFoundList(ArrayList<String> reasonsFoundList) {
		this.reasonsFoundList = reasonsFoundList;
	}

	public void setReasonInSentence(String reason) {
//		System.out.println("\t\t\t<!\t\tReason added in sentence:" +reason);
		this.reasonInSentence = reason;
	}
	
	public String getReasonsInSentence()
	{
		return this.reasonInSentence;
	}	
	
	public String getReasonTriplesInSentence() {
		return reasonTriplesInSentence;
	}

	public void setReasonTriplesInSentence(String reasonTriplesInSentence) {
		this.reasonTriplesInSentence = reasonTriplesInSentence;
	}

	//reasonsInNearbyStates
	public void setReasonsInNearbyStates(String reason) {
//		System.out.println("\t\t\t<!\t\tReason added in sentence:" +reason);
		this.reasonsInNearbyStates = reason;
	}
	
	public String getReasonsInNearbyStates()
	{
		return this.reasonsInNearbyStates;
	}
	
	public String getDistinctReasons() {
		String addReasons = "";
		for (String r : reasonsFoundList) {
			addReasons += r + " , ";
		}		
		
		//remove last comma
		addReasons = addReasons.trim();
		if (addReasons.endsWith(",")) {
			addReasons = addReasons.substring(0, addReasons.length() - 1);
			}
		
		// return reason;
		return addReasons.trim();

	}

	public void showAllReasons() {
		for (String r : reasonsFoundList) {
			System.out.println("reason : " + r);
		}
	}

}
