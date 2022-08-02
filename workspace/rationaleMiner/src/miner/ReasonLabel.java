package miner;


import java.sql.Timestamp;

//This class instance would hold one training data record
public class ReasonLabel {
	Integer proposal,messageid; //not really needed
	String label, causeCategory, causeSubCategory;	
	String causeSentence,effectSentence;	
	String communityReview, proposalAuthorReview, bdfldelegatePronouncement;
	
	public void setData(Integer v_proposal, String v_label, String v_causeCategory, String v_causeSubCategory,	String v_causeSentence,	String v_effectSentence, int v_messageID,	String v_communityReview, 
			String v_proposalAuthorReview, String v_bdfldelegatePronouncement) {
		this.proposal = v_proposal; //not really needed
		this.messageid = v_messageID;
		this.label = v_label;  
		this.causeCategory = v_causeCategory; this.causeSubCategory= v_causeSubCategory;
		this.causeSentence = v_causeSentence; this.effectSentence = v_effectSentence; 
		this.communityReview = v_communityReview;  this.proposalAuthorReview = v_proposalAuthorReview;
		this.bdfldelegatePronouncement = v_bdfldelegatePronouncement;
	}

	public String getCauseCategory() {		return causeCategory;	}
	public void setCauseCategory(String causeCategory) {		this.causeCategory = causeCategory;	}
	public String getCauseSubCategory() {		return causeSubCategory;	}
	public void setCauseSubCategory(String causeSubCategory) {		this.causeSubCategory = causeSubCategory;	}
	public Integer getProposal() {		return proposal;	}
	public void setProposal(Integer proposal) {		this.proposal = proposal;	}
	public Integer getMessageid() {		return messageid;	}
	public void setMessageid(Integer messageid) {		this.messageid = messageid;	}
	public String getLabel() {		return label;	}
	public void setLabel(String label) {		this.label = label;	}
	
	public String getCauseSentence() {		return causeSentence;	}
	public void setCauseSentence(String causeSentence) {		this.causeSentence = causeSentence;	}
	public String getEffectSentence() {		return effectSentence;	}
	public void setEffectSentence(String effectSentence) {		this.effectSentence = effectSentence;	}
	public String getCommunityReview() {		return communityReview;	}
	public void setCommunityReview(String communityReview) {		this.communityReview = communityReview;	}
	public String getProposalAuthorReview() {		return proposalAuthorReview;	}
	public void setProposalAuthorReview(String proposalAuthorReview) {		this.proposalAuthorReview = proposalAuthorReview;	}
	public String getBdfldelegatePronouncement() {		return bdfldelegatePronouncement;	}
	public void setBdfldelegatePronouncement(String bdfldelegatePronouncement) {		this.bdfldelegatePronouncement = bdfldelegatePronouncement;	}		
}
