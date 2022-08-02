package Process.processLabels;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import de.mpii.clause.driver.Reasons;

public class TripleProcessingResult {
	// metadata
	Boolean tripleMatched;
	Integer pepNumber,message_ID, numberOfPEPsMentionedInMessage;
	Date date;
	Timestamp dateTimeStamp;
	String author,authorsRole,messageSubject,folder,file,messageAuthorRole;
	public String currentSentence,currentSentenceUnique;
	// label captured
	public String label;
	public Boolean repeatedIdea;
	Boolean repeatedSentence;
	
	// label captured
	String reverbCapturedLabel,ollieCapturedLabel;
	public String clausIECapturedLabel;
	String subject, verb, object;//
	String v_subject,v_relation,v_object;
	// if sentence is
	Boolean conditional,negation;	
	// negations
	Boolean reverbNegation,clausIENegation,ollieNegation;	
	// negations
	Boolean reverbConditional,clausIEConditional,ollieConditional;	
	// reason
	// Boolean reasonFound;
	// String reason;
	Boolean reasonReverbFound,reasonClausIEFound,reasonOllieFound,msgSubjectContainsLabel;
	String reasonReverb,reasonClausIE,reasonOllie;
	String currParagraph, nextSentence, previousSentence, nextParagraph, previousParagraph;
	String eventBefore,storyLine;
	// There variables contain whether the triples matched and are therefore
	// true
	String reverb, clausIE,ollie;
	String finalReverbIdea,finalClausIEIdea,finalOllieIdea;
	// if conditional, set the above to false
	String comments,role;
	String finalLabel;
	Integer lineNumber;   //line number where the triple is found..for knowing until which line maximum,does the main triples occur..should not be towards teh end of message 
	
	//Reasons list - just for postprocessing
	//In post-processing, labels which are deleted sometimes have reasons which we dont want to lose and therefore add to this string
	//this is different from the original reason string	
	ArrayList<String> finalReasonsFoundList = new ArrayList<String>();
	
	//august 2017 added reasons to be part of the result
	Reasons r;
	String reasonTriplesInSameSentence;
	String reasonsInNearbyStates,reasonsInSentence,reasonTermsInMatchedTriple, reasonTermsInNearbySentencesParagraphs, reasonTriplesInNearbySentencesParagraphs;
	String reasonsTermsInPreviousSentence,reasonsTermsInNextSentence,reasonTriplesInPreviousSentences,reasonTriplesInNextentence;
	Boolean isFirstParagraph, isLastParagraph;
	//dec 2018
	Integer paragraphCounter,sentenceCounter, sentenceCounterInParagraph;
	
	//june 2020
	String peptype;
	
	TripleProcessingResult(String v_sentence, String v_label) {
		this.currentSentence = v_sentence;
		this.label = v_label;
	}	
		
	// initialise to default values
	public TripleProcessingResult() {
		tripleMatched = false;
		pepNumber = message_ID = null;
		date = dateTimeStamp = null;		
		peptype=author = authorsRole=folder=file="";
		messageSubject=messageAuthorRole = "";
		// label captured
		label = "";
//		reverbCapturedLabel =clausIECapturedLabel = ollieCapturedLabel = null;
		repeatedIdea = repeatedSentence = false;
		currentSentenceUnique = "";
		subject = verb = object = "";
		v_subject = v_relation = v_object = "";
		// reason
		reasonReverbFound = ollieNegation = clausIENegation = reverbNegation = ollieConditional = clausIEConditional = reverbConditional = false;
		reasonReverb = reasonClausIE = reasonOllie = "";
		reasonClausIEFound = reasonOllieFound = false;
		currentSentence = currParagraph = nextSentence = previousSentence = nextParagraph = previousParagraph = "";
		eventBefore = storyLine = "";
		// There variables contain whether the triples matched and are therefore
		// true
		reverb = clausIE = ollie = "";
		// if conditional, set the above to false
		// store the final idea ..idea + reason
//		finalReverbIdea = finalClausIEIdea = finalOllieIdea = null;
		comments = "";
		finalLabel = "";
//		finalReason = "";
		msgSubjectContainsLabel=false;
		//initialise reason
		r = new Reasons();	
		this.isFirstParagraph = false; 			this.isLastParagraph =false;  //dec 2018
		paragraphCounter=null; sentenceCounter=null; sentenceCounterInParagraph=null;
	}	
	
	
	//set reason
	public void setTripleMatched(Reasons v){		this.r = v;	}	
	public Reasons getReasonObject(){		return r;	}	
	public void addComments(String v_comments) {		comments += " " + v_comments;	}	
	public void setMetadata(Integer v_pepNumber, Date v_date,Timestamp v_dateTimeStamp, Integer v_message_ID, String v_label, String v_author,String v_authorsRole,
			String messageSubject, String v_folder,String sentence,	Integer lineNum, Boolean v_msgSubjectContainsLabel) {
		this.pepNumber = v_pepNumber;		this.date = v_date;		this.dateTimeStamp = v_dateTimeStamp;
		this.message_ID = v_message_ID;		this.author = v_author;		this.authorsRole = v_authorsRole;		this.messageSubject = messageSubject;
		this.folder = v_folder;		this.label = v_label;		this.currentSentence = sentence;
		this.comments = "";		this.lineNumber = lineNum; 		this.msgSubjectContainsLabel = v_msgSubjectContainsLabel;
		// set the libraries' values to false
		setTripleMatched(null, 1);		setTripleMatched(null, 2);		setTripleMatched(null, 3);
	}	
	// when populating through database //Integer v_message_ID, 
	//String ptype, 
	public void setData(Integer v_pepNumber, String v_author,String v_authorsRole,String v_messageSubject,Integer numberOfPEPsMentionedInMessage, Date v_date,Timestamp datetimestamp, String v_folder, 
			String v_file, Integer mid, String v_label, String subject, String verb, String object, String sentence,
			String v_reverb, String v_clausie, String v_ollie, Boolean v_repeatedLabel, String v_role,String v_reason, String ps, String s, String pp, String ep, 
			String np,String v_reasonsInSentence, String v_reasonTermsInMatchedTriple, String v_reasonTermsInNearbySentencesParagraphs, String v_reasonTriplesInNearbySentencesParagraphs,
			String reasonsTermsInPreviousSentence,String reasonsTermsInNextSentence, String reasonTriplesInPreviousSentences, String reasonTriplesInNextentence,String reasonTriplesInSameSentence,
			Boolean v_labelFoundInMsgSubject, Boolean v_isFirstParagraph, Boolean v_isLastParagraph) {
	
		//this.peptype = ptype;
		this.pepNumber = v_pepNumber;	this.date = v_date;				this.dateTimeStamp = datetimestamp;			this.message_ID = mid;
		this.author = v_author;			this.authorsRole = v_authorsRole; 	this.messageSubject = v_messageSubject;		this.numberOfPEPsMentionedInMessage = numberOfPEPsMentionedInMessage;
		this.folder = v_folder;			//System.out.println("v_folder set: "+v_folder);
		
		this.file = v_file;				this.label = v_label;		this.subject = subject;
		this.verb = verb;				this.object = object;		this.currentSentence = sentence;
		this.comments = "";				this.msgSubjectContainsLabel = v_labelFoundInMsgSubject; this.isFirstParagraph= v_isFirstParagraph; this.isLastParagraph = v_isLastParagraph;
		//this.currentSentence = sentence;
		// pep| author| date| label| currentSentence| reverb| clausie| ollie|
		// repeatedLabel|ps|ns|pp|ep|np		
		this.reverb = v_reverb;		this.clausIE = v_clausie;		this.ollie = v_ollie;
		this.repeatedIdea = v_repeatedLabel;		this.role = v_role;
//		this.finalReason = v_reason;
		//add the reason from the results table, this may be appended with more reasons from deleted labels found in the postprocessing stage
		if (v_reason==null || v_reason.isEmpty())
		{}
		else{
				addToFinalReasonListArray(v_reason);
		}
		this.currParagraph = checkReplaceNewline(ep);		this.nextParagraph = checkReplaceNewline(np);		this.previousParagraph = checkReplaceNewline(pp);		
		this.nextSentence = checkReplaceNewline(s);		this.previousSentence = checkReplaceNewline(ps);
		
		// set the libraries' values to false
		//not sure if this should be here
//		setTripleMatched(null, 1);
//		setTripleMatched(null, 2);
//		setTripleMatched(null, 3);
		r.setReasonInSentence(v_reasonsInSentence );		r.setReasonTermsInMatchedTriple(v_reasonTermsInMatchedTriple);
		r.setReasonTermsInNearbySentencesParagraphs(v_reasonTermsInNearbySentencesParagraphs);		r.setReasonTriplesInNearbySentencesParagraphs(v_reasonTriplesInNearbySentencesParagraphs);
		r.setReasonTermsInNearbySentencesParagraphs(reasonsTermsInNextSentence);		r.setReasonTermsInPreviousSentence(reasonsTermsInPreviousSentence);
		r.setReasonTriplesInPreviousSentence(reasonTriplesInPreviousSentences);		r.setReasonTriplesInNextSentence(reasonTriplesInNextentence);
		r.setReasonTriplesInSentence(reasonTriplesInSameSentence);			
	}	
	
	
	
	public String getPeptype() {		return peptype;	}
	public void setPeptype(String v_peptype) {		this.peptype = v_peptype;	}

	// when populating through database //Integer v_message_ID,
	public void setDataDoubles(Integer v_pepNumber, String v_author,String v_authorsRole, Date v_date,Timestamp datetimestamp,Integer v_message_ID, String v_LabelOfSingledoubleMatchedTerm,String sentence,
				String v_reverb, String v_clausie, String v_ollie, Boolean v_repeatedLabel, String v_role, 	String v_reason, String ps, String s, String pp, String ep, 
				String np) {
		
			this.pepNumber = v_pepNumber;			this.date = v_date;			this.dateTimeStamp = datetimestamp;			this.message_ID = v_message_ID;
			this.author = v_author;					this.authorsRole = v_authorsRole;	 this.label = v_LabelOfSingledoubleMatchedTerm;
//			this.subject = subject;//			this.verb = verb;//			this.object = object;
			this.currentSentence = sentence;			this.comments = "";
			//this.currentSentence = sentence;
			// pep| author| date| label| currentSentence| reverb| clausie| ollie|
			// repeatedLabel|ps|ns|pp|ep|np		
			this.reverb = v_reverb;			this.clausIE = v_clausie;			this.ollie = v_ollie;
			this.repeatedIdea = v_repeatedLabel;			this.role = v_role;
//			this.finalReason = v_reason;			
			this.currParagraph = checkReplaceNewline(ep);			this.nextSentence = checkReplaceNewline(s);			this.previousSentence = checkReplaceNewline(ps);
			this.nextParagraph = checkReplaceNewline(np);			this.previousParagraph = checkReplaceNewline(pp);
			// set the libraries' values to false
			//not sure if this should be here
//			setTripleMatched(null, 1);//			setTripleMatched(null, 2);//			setTripleMatched(null, 3);
		}	
	
	public String checkReplaceNewline(String ¢) {
		return ¢ = ¢ == null || !¢.contains("\n") ? ¢ : ¢.replace("\n", "");
	}
	
	// when populating through database //Integer v_message_ID,
	public void setStateData(Integer v_pepNumber, Date v_date,Timestamp v_dateTimeStamp, Integer v_message_ID, String v_author,  String v_authorsRole, String v_label, String sentence,    //,String subject, String verb, String object, 
				String v_reverb, String v_clausie, String v_ollie) {
			this.pepNumber = v_pepNumber;			this.date = v_date;			this.dateTimeStamp = v_dateTimeStamp;			this.message_ID = v_message_ID;
			this.author = v_author;			this.authorsRole = v_authorsRole;  this.label = v_label;
//			this.subject = subject;//			this.verb = verb;//			this.object = object;
			this.currentSentence = sentence;			this.comments = "";
			//this.currentSentence = sentence;
			// pep| author| date| label| currentSentence| reverb| clausie| ollie|
			// repeatedLabel|ps|ns|pp|ep|np
			reverb = v_reverb;			clausIE = v_clausie;			ollie = v_ollie;
	}
		
	public Boolean isFirstParagraph() {		return isFirstParagraph;	}
	public void setFirstParagraph(Boolean isFirstParagraph) {		this.isFirstParagraph = isFirstParagraph;	}
	public Boolean isLastParagraph() {		return isLastParagraph;	}
	public void setLastParagraph(Boolean isLastParagraph) {		this.isLastParagraph = isLastParagraph;		}
		
	public void setDateTimeStamp(Timestamp v_dateTimeStamp){		dateTimeStamp = v_dateTimeStamp;	}			
	public Timestamp getDateTimeStamp() {		return dateTimeStamp;	}	
	public void addSentence(String v_sentence) {		this.currentSentence = v_sentence;	}	
	public String getCurrentSentence() {		return currentSentence;	}	
	public String getLabel() {		return label;	}	
	public void setFinalLabel(String v_finalLabel) {		finalLabel = v_finalLabel;	}	
	public String getFinalLabel() {		return finalLabel;	}	
	//reasons
//	public void setFinalReason(String v_addReason) {
//		if (v_finalReason.startsWith("null"))
//			v_finalReason = v_finalReason.replace("null", "");		
//		if  (finalReason==null || finalReason.isEmpty() )
//			finalReason= v_finalReason;
//		else {
//			//here we want to check for reason existence
//			Boolean found=false;
//			if (finalReason.contains(",")){
//				String split[] = finalReason.split(",");
//			
//				for(String r: split)
//					if(r.contains(v_finalReason)) {
//						found = true;
//						System.out.println("Reason found in Final Reason, not adding: "+v_finalReason);
//					}
//				
//				if (found ==false){
//					//reasonsFoundList.add(v_finalReason);
//					finalReason += " " + v_finalReason;
//					System.out.println("Reason not foundin Final Reason, adding: "+v_finalReason);
//				}
//			}
//			else{
//				if(finalReason.contains(v_finalReason)) {
//					found = true;
//					System.out.println("Reason found in Final Reason, not adding: "+v_finalReason);
//				}
//			}
//			
//			//finalReason += " " + v_finalReason;
//			finalReason = finalReason.trim();
//		}
		
		
//	}
	
//	public String getFinalReason() {
//		return finalReason;
//	}
	
	//this code is where postprocessing sends all new reasons found 
	public void addToFinalReasonListArray(String v_reason) {
		v_reason = v_reason.trim();
		String[] reasons = null;

		if (v_reason.contains(","))
		{
			reasons = v_reason.split(",");	
			for (String reason: reasons){
				finalReasonsFoundList.add(reason.trim());
//##				System.out.println("\t\t#Adding Reasons: " + reason);
			}
		}
	}

	public String getFinalReasonListArrayAsString() {
		String r = "";
		if (finalReasonsFoundList.isEmpty())
			return "";
		else{
			//for (String rf : finalReasonsFoundList)
			//	r += rf + " ";
			//List<String> reasonsList = // create list with duplicates...
			Set<String> uniqueReasons = new HashSet<String>(finalReasonsFoundList);
			r = StringUtils.join(uniqueReasons, ",");
			//System.out.println("Unique gas count: " + uniqueGas.size());
		}
		return r.trim();
	}
	
	public String getReasonTermsInMatchedTriple() {		return r.getReasonTermsInMatchedTriple();	}
	public void setReasonTermsInMatchedTriple(String reasonTermsInMatchedTriple) {		r.setReasonTermsInMatchedTriple(reasonTermsInMatchedTriple);	}
	public String getReasonTermsInNearbySentencesParagraphs() {		return r.getReasonTermsInNearbySentencesParagraphs();	}
	public void setReasonTermsInNearbySentencesParagraphs(String reasonTermsInNearbySentencesParagraphs) {		r.setReasonTermsInNearbySentencesParagraphs(reasonTermsInNearbySentencesParagraphs);	}
	public String getReasonTriplesInNearbySentencesParagraphs() {		return r.getReasonTriplesInNearbySentencesParagraphs();	}
	public void setReasonTriplesInNearbySentencesParagraphs(String reasonTriplesInNearbySentencesParagraphs) {		r.setReasonTriplesInNearbySentencesParagraphs(reasonTriplesInNearbySentencesParagraphs);	}
	
	//reasonsInNearbyStates
	public String getReasonsInNearbyStates() {		return r.getReasonsInNearbyStates();	}
	public void setReasonsInNearbyStates(String reasonsInNearbyStates) {		r.setReasonsInNearbyStates(reasonsInNearbyStates);	}	
	public String getReasonsInSentence() {		return r.getReasonsInSentence();	}
	public void setReasonsInSentence(String reasonsInSentence) {		r.setReasonInSentence(reasonsInSentence);	}
	public ArrayList<String> getFinalReasonListArrayAsList() {		return finalReasonsFoundList;	}	
	public String getMessageSubject() {		return messageSubject;	}
	public void setMessageSubject(String messageSubject) {		this.messageSubject = messageSubject;	}
	public Integer getNumberOfPEPsMentionedInMessage() {		return numberOfPEPsMentionedInMessage;	}
	public void setNumberOfPEPsMentionedInMessage(Integer numberOfPEPsMentionedInMessage) {		this.numberOfPEPsMentionedInMessage = numberOfPEPsMentionedInMessage;	}
	public void setRole(String v_role) {		role = v_role;	}	
	public void setFolder(String v_folder) {		role = v_folder;	}
	public void setFile(String v_file) {		role = v_file;	}
	public void setMID(Integer mid)	{		message_ID = mid;	}	
	public String getRole() {		return role;	}	
	public String getFolder() {		return folder;	}
	public String getFile() {		return file;	}
	public void setUniqueSentence(String v_currentSentenceUnique) {		currentSentenceUnique = v_currentSentenceUnique;	}	
	public String getUniqueSentence() {		return currentSentenceUnique;	}	
	public void setRepeatedSentence(Boolean v_repeatedSentence) {		this.repeatedSentence = v_repeatedSentence;	}	
	public void setRepeatedLabel(Boolean v_repeatedLabel) {		this.repeatedIdea = v_repeatedLabel;	}	
	public Boolean getIfRepeatedFoundLabel() {		return repeatedIdea;	}	
	public Boolean getIfRepeatedSentenceAndFoundLabel() {		return repeatedIdea && repeatedSentence;	}	
	public void setLabel(String idea) {		this.label = idea;	}	
	public void setLabelSubject(String v_subject) {		this.subject = v_subject;	}
	public void setLabelRelation(String v_verb) {		this.verb = v_verb;	}
	public void setLabelObject(String v_object) {		this.object = v_object;	}
	public String getLabelSubject() {		return this.subject;	}
	public String getLabelRelation() {		return this.verb;	}
	public String getLabelObject() {		return this.object;	}
	public Boolean getMsgSubjectContainsLabel(){		return this.msgSubjectContainsLabel;	}
	
	public void setTripleMatched(String v_tripleMatched, Integer v_library) {
		if (v_library == 1)			this.reverb = v_tripleMatched;
		else if (v_library == 2)	this.clausIE = v_tripleMatched;
		else if (v_library == 3)	this.ollie = v_tripleMatched;
		else System.out.println("ERROR WHILE SETTING TRIPLE MATCHED");
	}	
//	public void setTripleLabelMatched(String v_tripleLabelMatched, Integer v_library) {
//		if (v_library == 1) {
//			reverbCapturedLabel = v_tripleLabelMatched;
//		} else if (v_library == 2) {
//			clausIECapturedLabel = v_tripleLabelMatched;
//		} else if (v_library == 3) {
//			ollieCapturedLabel = v_tripleLabelMatched;
//		} else {
//			System.out.println("ERROR WHILE SETTING TRIPLE LABEL MATCHED");
//		}
//		// tripleMatched = v_tripleMatched;
//	}	
	public void setReverb(String v_reverb) {		this.reverb = v_reverb;	}	
	public void setClausIE(String v_clausIE) {		this.clausIE = v_clausIE;	}	
	public void setOllie(String v_ollie) {		this.ollie = v_ollie;	}	
	public void setMessageAuthorRole(String v_messageAuthorRole) {		messageAuthorRole = v_messageAuthorRole;	}	
	public void setConditional(Boolean v_conditional, Integer v_libraryToCheck) {
		if (v_libraryToCheck == 1)			this.reverbConditional = v_conditional;
		else if (v_libraryToCheck == 2)			this.clausIEConditional = v_conditional;
		else if (v_libraryToCheck == 3)			this.ollieConditional = v_conditional;
		else System.out.println("ERROR WHILE SETTING CONDITIONAL");
	}	
	public void setNegation(Boolean v_negation, Integer v_libraryToCheck) {
		if (v_libraryToCheck == 1) {
			this.reverb = null;
			this.reverbNegation = v_negation;
		} else if (v_libraryToCheck == 2) {
			this.clausIE = null;
			this.clausIENegation = v_negation;
		}
		else if (v_libraryToCheck != 3)
			System.out.println("ERROR WHILE SETTING NEGATION");
		else {
			this.ollie = null;
			this.ollieNegation = v_negation;
		}
	}	
	public void setReason(String v_reason, Integer v_libraryToCheck) {
		// reason = v_reason;
		if (v_libraryToCheck == 1) {
			this.reasonReverbFound = true;
			
			this.reasonReverb = this.reasonReverb == null ? v_reason : this.reasonReverb + " " + v_reason;			
		} 
		else if (v_libraryToCheck == 2) {
			this.reasonClausIEFound = true;
			
			this.reasonClausIE = this.reasonClausIE == null ? v_reason : this.reasonClausIE + v_reason;
		} else if (v_libraryToCheck != 3)
			System.out.println("ERROR WHILE SETTING REASON");
		else {
			this.reasonOllieFound = true;
			this.reasonOllie = this.reasonOllie == null ? v_reason : this.reasonOllie + v_reason;
		}
	}	
	public void setCurrParagraph(String v_currParagraph) {
		if (v_currParagraph != null && v_currParagraph.contains("\n") && v_currParagraph.contains("\n"))
			v_currParagraph = v_currParagraph.replace("\n", "");
		currParagraph = v_currParagraph;
	}	
	public void setNextParagraph(String v_nextParagraph) {
		if (v_nextParagraph != null && v_nextParagraph.contains("\n") && v_nextParagraph.contains("\n"))
			v_nextParagraph = v_nextParagraph.replace("\n", "");
		nextParagraph = v_nextParagraph;
	}	
	public void setPrevParagraph(String v_prevParagraph) {
		if (v_prevParagraph != null && v_prevParagraph.contains("\n") && v_prevParagraph.contains("\n"))
			v_prevParagraph = v_prevParagraph.replace("\n", "");
		previousParagraph = v_prevParagraph;
	}	
	// GETS
	public Integer getPepNumber() {		return pepNumber;	}	
	public String getReverb() {		return this.reverb;	}	
	public String getClausIE() {		return this.clausIE;	}	
	public String getOllie() {		return this.ollie;	}	
	public String getMessageAuthorRole() {		return messageAuthorRole;	}	
	public String getComments() {		return comments;	}	
	public Boolean getIfRepeatedSentence() {		return repeatedSentence;	}	
	public String getReason(Integer v_libraryToCheck) {
		String reason = "";
		if (v_libraryToCheck == 1) 			return this.reasonReverb;
		else if (v_libraryToCheck == 2 )			return this.reasonClausIE;
		else if (v_libraryToCheck == 3)			return this.reasonOllie;
		else
			System.out.println("ERROR WHILE GETTING REASON");
		return reason;
	}	
	public void setStoryLine(String storyLine) {		storyLine = storyLine;	}	
	public Date getDate() {		return date;	}	
	public String getAuthor() {		return author;	}	
	public void setAuthorsRole(String v_authorsRole) {		this.authorsRole = v_authorsRole;	}
	public String getAuthorsRole() {		return authorsRole;	}	
	public Integer getMessageID() {		return message_ID;	}
	public String getTripleMatched(Integer v_library) {
		if (v_library == 1)			return reverb;
		if (v_library == 2)			return clausIE;
		if (v_library == 3)			return ollie;
		System.out.println("ERROR WHILE GETTING TRIPLE MATCHED");
		return null;
	}	
	public void setFinalIdea(String finalIdea, Integer v_library) {
		if (v_library == 1)			this.finalReverbIdea = finalIdea;
		else if (v_library == 2)			this.finalClausIEIdea = finalIdea;
		else if (v_library == 3)			this.finalOllieIdea = finalIdea;
		else
			System.out.println("ERROR WHILE setFinalIdea()");
	}	
	public String getLabels(Integer v_library) {
		if (v_library == 1)			return reverb;
		if (v_library == 2)			return clausIE;
		if (v_library == 3)			return ollie;
		System.out.println("ERROR WHILE GETTING NEGATION");
		return null;
	}	
	public Boolean getNegation(Integer v_library) {
		if (v_library == 1)			return reverbNegation;
		if (v_library == 2)			return clausIENegation;
		if (v_library == 3)			return ollieNegation;
		System.out.println("ERROR WHILE GETTING NEGATION");
		return null;
	}	
	public void setNextSentence(String v_nextSentence) {		this.nextSentence = v_nextSentence;	}	
	public void setPrevSentence(String v_prevSentence) {		previousSentence = v_prevSentence;	}	
	public String getNextSentence() {		return this.nextSentence;	}	
	public String getPrevSentence() {		return this.previousSentence;	}	
	public String getStoryLine() {			return this.storyLine;	}	
	public String getCurrParagraph() {		return this.currParagraph;	}	
	public String getNextParagraph() {		return this.nextParagraph;	}	
	public String getPrevParagraph() {		return this.previousParagraph;	}	
	public Boolean getConditional() {		return conditional;	}	
	public Boolean getNegation() {			return negation;	}	
	public String getTripleResultString() {
		return pepNumber + "|" + date + "|" + message_ID + "|" + author + "|" + label + "|" + currentSentence + "|"
				+ getLabels(1) + "|" + getLabels(2) + "|" + getLabels(3);
	}
	public void setLineNumber(Integer ln) { this.lineNumber = ln; }
	public Integer getLineNumber() { 	return this.lineNumber; }
	public void writeAllToFile() {
		// writer2.write("\n" + "ClausIE Triples Matched but conditional, " +
		// v_idea + " | " + subject + " | " + verb + " | " + object + " | " +
		// v_subject + " | " + v_relation + " | " + v_object);
	}
	public Integer getParagraphCounter() {		return paragraphCounter;	}
	public void setParagraphCounter(Integer paragraphCounter) {		this.paragraphCounter = paragraphCounter;	}
	public Integer getSentenceCounter() {		return sentenceCounter;	}
	public void setSentenceCounter(Integer sentenceCounter) {		this.sentenceCounter = sentenceCounter;	}
	public Integer getSentenceCounterInParagraph() {		return sentenceCounterInParagraph;	}
	public void setSentenceCounterInParagraph(Integer sentenceCounterInParagraph) {		this.sentenceCounterInParagraph = sentenceCounterInParagraph;	}

	@Override
    protected void finalize() throws Throwable    {
        //System.out.println("From Finalize Method, i = ");
    }
}
