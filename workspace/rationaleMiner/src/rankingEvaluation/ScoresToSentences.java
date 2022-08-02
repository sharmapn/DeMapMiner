package rankingEvaluation;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import utilities.ParagraphSentence;

//March 2019..This script is alos used for another purpose to store db read values during evaluation
//script used to determine the final probability of reason in text(sentence, adjacent sentences and entire paragraph)
public class ScoresToSentences  implements Comparable< ScoresToSentences >  {	//each sentence/etc probaility	
	 ParagraphSentence ps = new ParagraphSentence();
	
	 Integer id,proposalNum, mid, dateDiff; 
	 Date dateVal, dateOfStateMessage;
	//we hold these values for determining the final probability of reason in text
	 String label,messageSubject, sentenceOrParagraph, termsmatched, author,authorsRole,
		messageLocation, //the message can either be the a) state message, b) pep summary from python website for each pep or c) other messages. First two get higher probability
		location, // for the third option above (other messages), we check a) a sentence, then b) the entire paragraph
		labelledSentenceOrParagraph,	//we label the sentence or paragraph
		labelledMessageSubject, 		//Labeled Message subject
		messageType;					//what kind of message it is (proposalAuthorAskingReview, BDFL Reviewing, BDFLPronouncement, communityMemberReviewing)
		
	 boolean sameMessageAsState=false,sameSentenceAsState=false,nearbySentenceAsState=false, sameParagrapghAsState=false,sameMsgSubAsStateTriple=false; 
	//contains terms lists. ... message subject
	 boolean messageSubjectContainsReasonsTerms ,       	messageSubjectContainsReasonsIdentifierTerms, 	  			messageSubjectContainsStatesSubstates, 
			messageSubjectContainsProposalIdentifierTermList , 	messageSubjectContainsPositiveWords, 		 		  		messageSubjectContainsNegativeWords,
			messageSubjectContainsVerbTerms,			  		messageSubjectContainsEntitiesTerms,		  		  		messageSubjectContainsSpecialTerms,
			messageSubjectContainsDecisionTerms,				messageSubjectContainsProposalIdentifierTerms,	 			messageSubjectNegationFound;
	 boolean sentenceOrParagraph_containsReasonsTerms , 	sentenceOrParagraph_containsReasonsIdentifierTerms, 		sentenceOrParagraph_containsStatesSubstates, 
			sentenceOrParagraph_containsIdentifierTermList , 	sentenceOrParagraph_containsPositiveWords, 		  			sentenceOrParagraph_containsNegativeWords,
			sentenceOrParagraph_containsVerbTerms,				sentenceOrParagraph_containsIdentifierTerms,		  		sentenceOrParagraph_containsEntitiesTerms,
			sentenceOrParagraph_containsSpecialTerms,			sentenceOrParagraph_containsSpecialTermsInMessageSubject,
			sentenceOrParagraph_containsNegationTermList,		sentenceOrParagraph_containsDecisionTerms,
			
			restOfParagraph_containsStatesSubstates, restOfParagraph_containsIdentifierTerms, restOfParagraph_containsReasonsTerms, restOfParagraph_containsEntitiesTerms, restOfParagraph_containsSpecialTerms, 
			restOfParagraph_containsDecisionTerms,
			messsageTypeIsReasonMessage,
			isFirstParagraph,  isLastParagraph, //if reason sentence found, is in first or last paragraph ..carries more weight as many times reason are found to be there
			reasonLabelFoundUsingTripleExtraction,messageContainsSpecialTerm,	//Feb 2019
			prevParagraph_containsSpecialTerms;									//feb 2019
			
	//probabilities
	 double finalProbability,					 //the final score of all the below probabilities
				  reasonProbabilityScore,		     //the probability score based on the  addition of = messageSubjectHintProbablityScore + sentenceOrParagraphHintProbablity ..maybe doesnt matter
				  sentenceOrParagraphHintProbablity, //THE MAIN DETERMINANT.. based on different combination of terms found in a sentence
				  messageSubjectHintProbablityScore, //if terms (like pep&accepted OR pep&resolution) are found in the message subject, a probability is assigned
				  negationTermPenalty,				 //a negative word like 'no' in a  sentence gets a '-0.8' as negation penalty
				  messageLocationProbabilityScore,	 //the location of the message also gives if different scores..state message, pep summary or 'other messages'
				  termsLocationWithMessageProbabilityScore, // if the combination is found in a sentence, adjacent sentences or paragraph ALSO FOR STATE MESSAGE sentence and nearby sentences, paragraph or nearby paragraphs
				  dateDiffProbability, 	  			 //less than 7 days from state message gets high probability, all others don't get any
				  authorRoleProbability,  			 //different probabilities assigned to  message author based on author {bdfl, proposalauthor, pep editors, core developers and other members}
				  restOfParagraphProbabilityScore,	 //
				  //november 2018
				  messsageTypeIsReasonMessageProbabilityScore,  //all these get 0.9 -(proposalAuthorAskingReview, BDFL Reviewing, BDFLPronouncement, communityMemberReviewing), else zero
				  sentenceLocationInMessageProbabilityScore,    //if its isFirstParagraph,  isLastParagraph,. then they get 0.9 probability score, else zero
				  sameMsgSubAsStateTripleProbabilityScore,  	//if message subject has same subject as state triple then we give more weight..0.9, else zero				
				  reasonLabelFoundUsingTripleExtractionProbabilityScore, //Feb 2019 
				  messageContainsSpecialTermProbabilityScore, //Feb 2019 
				  prevParagraphSpecialTermProbabilityScore,	  //feb 2019
				  totalProbabilityScore,						//march 2019
	 			  origTotalProbabilityScore ; //added dec 2019
	 	
	 //March 2019....original values
	 double  ORIGsentenceHintProbablity=0.0,ORIGsentenceLocationHintProbability=0.0,ORIGmessageSubjectHintProbablityScore=0.0, ORIGdateDiffProbability=0.0, ORIGauthorRoleProbability=0.0, 
			 ORIGnegationTermPenalty=0.0, ORIGrestOfParagraphProbabilityScore=0.0, ORIGmessageLocationProbabilityScore=0.0, 
			 ORIGmessageTypeIsReasonMessageProbabilityScore=0.0,ORIGsentenceLocationInMessageProbabilityScore=0.0,ORIGsameMsgSubAsStateTripleProbabilityScore=0.0,
			 ORIGreasonLabelFoundUsingTripleExtractionProbabilityScore=0.0,ORIGmessageContainsSpecialTermProbabilityScore=0.0,ORIGprevParagraphSpecialTermProbabilityScore=0.0;
	
	//for each sentence or paragraph, we contains the lists of each type of terms
	 String reasonsTermsFoundList, reasonsIdentifierTermsList, statesSubstatesList, identifiersTermList, entitiesTermList,
		 specialTermList, decisionTermList, negationTermList, positiveWordList, negativeWordList,
	// message subject term lists
		 messageSubjectStatesSubstatesList, messageSubjectDecisionTermList,messageSubjectVerbList,
		 messageSubjectProposalIdentifiersTermList,messageSubjectEntitiesTermList, messageSubjectSpecialTermList,
		 //rest of paragraph
		 restOfParagraph_StatesSubstatesList,restOfParagraph_identifiersTermList,restOfParagraph_ReasonsTermsList, restOfParagraph_entitiesTermList, 
		 restOfParagraph_specialTermList, restOfParagraph_decisionTermList,prevParagraph_specialTermList;
	
	double v_termsLocationWithMessageProbabilityScore;
	
	//counts for machine learning 
	 Integer 
		//messagesubject level
		messageSubjectStatesSubstatesListCount, messageSubjectDecisionTermListCount,messageSubjectVerbListCount,
		messageSubjectProposalIdentifiersTermListCount,messageSubjectEntitiesTermListCount, messageSubjectSpecialTermListCount,
		//sentenceOrParagraph level
		reasonsTermsFoundCount, reasonsIdentifierTermsCount, statesSubstatesCount, identifiersTermCount, entitiesTermCount,
		specialTermCount, decisionTermCount, negationTermCount, positiveWordCount, negativeWordCount,
		restOfParagraphStatesSubstatesTermsCount, restOfParagraph_identifiersTermCount,  restOfParagraph_entitiesTermCount, restOfParagraph_specialTermCount, restOfParagraph_decisionTermCount,
		restOfParagraph_ReasonsTermsCount,prevParagraphSpecialTermsCount,
		//weights for other factors
		dateDiffWeight, 
		messageAuthorRoleWeight,
		containsReason;
	
	 boolean writeToFile, isEnglishOrCode , actermsfound;	//whether to write to file to databse for machine learning
	
	 String restOfParagraph, entireParagraph,firstParagraph, lastParagraph;
	
	//March 2019 -entire message
	String text;
	
	//April 2019, we store all Ranking evaluation metrics here
	int rank,                   //updated ranking value based on 'message based on sentence probability'
		rankByTotalProbability, //first ranking is by total probability
	 	matched; //by default we will set the rank to -1 and matched to 0
	float precision, recall, discountedCumulatioveGain, sum_discountedCumulatioveGain;
	
	public ScoresToSentences() {
		//march 2019...added id for db reading
		id=null;
		proposalNum=mid=dateDiff=null; dateOfStateMessage = null; 
		label = messageSubject = sentenceOrParagraph = termsmatched = author = messageLocation = location="";
		finalProbability = reasonProbabilityScore = sentenceOrParagraphHintProbablity = messageSubjectHintProbablityScore =  
		negationTermPenalty = termsLocationWithMessageProbabilityScore= dateDiffProbability = authorRoleProbability= restOfParagraphProbabilityScore =
		v_termsLocationWithMessageProbabilityScore = messsageTypeIsReasonMessageProbabilityScore = messsageTypeIsReasonMessageProbabilityScore =
		sentenceLocationInMessageProbabilityScore = sameMsgSubAsStateTripleProbabilityScore = 
		//Feb 2019 
		reasonLabelFoundUsingTripleExtractionProbabilityScore= messageContainsSpecialTermProbabilityScore = prevParagraphSpecialTermProbabilityScore= 
		totalProbabilityScore = 0.0; //not sure what this is and where this goes
		
		//march 2019
		ORIGsentenceHintProbablity=ORIGsentenceLocationHintProbability=ORIGmessageSubjectHintProbablityScore= ORIGdateDiffProbability= ORIGauthorRoleProbability= 
		ORIGnegationTermPenalty= ORIGrestOfParagraphProbabilityScore= ORIGmessageLocationProbabilityScore= 
		ORIGmessageTypeIsReasonMessageProbabilityScore=ORIGsentenceLocationInMessageProbabilityScore=ORIGsameMsgSubAsStateTripleProbabilityScore=
		ORIGreasonLabelFoundUsingTripleExtractionProbabilityScore=ORIGmessageContainsSpecialTermProbabilityScore=ORIGprevParagraphSpecialTermProbabilityScore=0.0;
		
		//april 2019
		rank = rankByTotalProbability= -1; 
		matched = 0;
		precision=recall=discountedCumulatioveGain=sum_discountedCumulatioveGain = 0;
		
		sameMessageAsState=sameSentenceAsState=nearbySentenceAsState=sameParagrapghAsState=sameMsgSubAsStateTriple=
		messageSubjectContainsReasonsTerms =messageSubjectContainsReasonsIdentifierTerms=messageSubjectContainsStatesSubstates=
		messageSubjectContainsProposalIdentifierTermList = 	messageSubjectContainsPositiveWords=	 		  		messageSubjectContainsNegativeWords=
		messageSubjectContainsVerbTerms=			  		messageSubjectContainsEntitiesTerms=		  		  	messageSubjectContainsSpecialTerms= 
		messageSubjectContainsDecisionTerms=				messageSubjectContainsProposalIdentifierTerms=	 		messageSubjectNegationFound=
		sentenceOrParagraph_containsReasonsTerms =  		sentenceOrParagraph_containsReasonsIdentifierTerms= 	sentenceOrParagraph_containsStatesSubstates=
		sentenceOrParagraph_containsIdentifierTermList =  	sentenceOrParagraph_containsPositiveWords= 		sentenceOrParagraph_containsNegativeWords=
		sentenceOrParagraph_containsVerbTerms=				sentenceOrParagraph_containsIdentifierTerms=		  	sentenceOrParagraph_containsEntitiesTerms=
		sentenceOrParagraph_containsSpecialTerms= 			sentenceOrParagraph_containsSpecialTermsInMessageSubject=
		sentenceOrParagraph_containsNegationTermList=		sentenceOrParagraph_containsDecisionTerms=
		
		restOfParagraph_containsStatesSubstates= restOfParagraph_containsIdentifierTerms= restOfParagraph_containsEntitiesTerms= restOfParagraph_containsSpecialTerms= 
		restOfParagraph_containsDecisionTerms= restOfParagraph_containsReasonsTerms=false;
		//feb 2019
		reasonLabelFoundUsingTripleExtraction = messageContainsSpecialTerm =prevParagraph_containsSpecialTerms=false;
		
		messageSubjectStatesSubstatesListCount= messageSubjectDecisionTermListCount=messageSubjectVerbListCount=
		messageSubjectProposalIdentifiersTermListCount=messageSubjectEntitiesTermListCount= messageSubjectSpecialTermListCount=
		//sentenceOrParagraph level
		reasonsTermsFoundCount= reasonsIdentifierTermsCount= statesSubstatesCount= identifiersTermCount= entitiesTermCount=
		specialTermCount= decisionTermCount=negationTermCount=positiveWordCount= negativeWordCount=
		restOfParagraphStatesSubstatesTermsCount=restOfParagraph_identifiersTermCount=restOfParagraph_entitiesTermCount=restOfParagraph_specialTermCount=
		restOfParagraph_decisionTermCount=restOfParagraph_ReasonsTermsCount=prevParagraphSpecialTermsCount=0;
		
		//for each sentence or paragraph, we contains the lists of each type of terms
		reasonsTermsFoundList=reasonsIdentifierTermsList=statesSubstatesList= identifiersTermList= entitiesTermList=
			 specialTermList= decisionTermList=negationTermList= positiveWordList=negativeWordList=
		 messageSubjectStatesSubstatesList= messageSubjectDecisionTermList=messageSubjectVerbList=
		 	messageSubjectProposalIdentifiersTermList=messageSubjectEntitiesTermList= messageSubjectSpecialTermList =prevParagraph_specialTermList= "";		
		
		restOfParagraph_StatesSubstatesList=restOfParagraph_identifiersTermList=restOfParagraph_entitiesTermList=restOfParagraph_specialTermList= 
				restOfParagraph_decisionTermList=restOfParagraph_ReasonsTermsList=""; 
		text= "";
		//counts for machine learning
		//message subject level
		messageSubjectStatesSubstatesListCount= messageSubjectDecisionTermListCount=messageSubjectVerbListCount=
		messageSubjectProposalIdentifiersTermListCount=messageSubjectEntitiesTermListCount= messageSubjectSpecialTermListCount=
		//sentenceorparagraph level
		reasonsTermsFoundCount=reasonsIdentifierTermsCount=statesSubstatesCount=identifiersTermCount=entitiesTermCount=
		specialTermCount=decisionTermCount=negationTermCount=positiveWordCount=negativeWordCount=0;
		writeToFile= false; isEnglishOrCode=false;
		containsReason=0;  messsageTypeIsReasonMessage=isFirstParagraph = isLastParagraph = false;
		restOfParagraph = entireParagraph=firstParagraph=lastParagraph="";
		actermsfound = false; //mar 2019, accept reject terms found
	}
	
	/*@Override
    public String toString() {
        return "Probability [tps=" + totalProbabilityScore + "]";
    }
	@Override
    public int compareTo(Probability o) {
        //return new Double (this.getTotalProbabilityScore().compareTo(o.getTotalProbabilityScore()) );
        return Double.compare(getTotalProbabilityScore(),o.getTotalProbabilityScore());
    } */
	
	
	//March 2019 - we will sort by proosal number so that we can easily get a subset
	
	@Override
    public String toString() {
        return "Proposal Number [tps=" + proposalNum + "]";
    }
    @Override
    public int compareTo(ScoresToSentences o) {
        //return new Double (this.getTotalProbabilityScore().compareTo(o.getTotalProbabilityScore()) );
    	//return Double.compare(getProposalNum(),o.getProposalNum()); This works
        int sComp = Double.compare(getProposalNum(),o.getProposalNum());
        	
         if (sComp != 0) {
            return sComp;
         } 
         //if borth rows are same proposal number, then we sort by total probability
         double x1 = getTotalProbabilityScore();
         double x2 = o.getTotalProbabilityScore();
         return Double.compare(x2,x1);
    }
    
    //august 2020..baseline 1 and baseline 2
    public void calculateAllProbabilities_UpdateMessageType_B1(Connection conn) {
    	
    	if(sentenceOrParagraph_containsDecisionTerms && sentenceOrParagraph_containsStatesSubstates && sentenceOrParagraph_containsReasonsIdentifierTerms) {
			sentenceOrParagraphHintProbablity=0.7; 
		}
    	else { //allother cases..just in case
			sentenceOrParagraphHintProbablity=0.0;
		}
    	//else if(sentenceOrParagraph_containsReasonsTerms && sentenceOrParagraph_containsStatesSubstates && sentenceOrParagraph_containsEntitiesTerms) {  //after discussion i accept OR I decided add
		//	sentenceOrParagraphHintProbablity=0.7;  
		//}
    	reasonProbabilityScore = sentenceOrParagraphHintProbablity;
    }
    public void calculateAllProbabilities_UpdateMessageType_B2(Connection conn) {
    	
    	if(sentenceOrParagraph_containsDecisionTerms && sentenceOrParagraph_containsStatesSubstates && sentenceOrParagraph_containsReasonsIdentifierTerms) {
			sentenceOrParagraphHintProbablity=0.7; 
		}
    	else if(sentenceOrParagraph_containsDecisionTerms && sentenceOrParagraph_containsEntitiesTerms) {	//pep 507
			sentenceOrParagraphHintProbablity=0.3;  //to many false postives so we give little weightage
		}
    	else { //allother cases..just in case
			sentenceOrParagraphHintProbablity=0.0;
		}
    	
    	//add entity and date
    	
    	//else if(sentenceOrParagraph_containsReasonsTerms && sentenceOrParagraph_containsStatesSubstates && sentenceOrParagraph_containsEntitiesTerms) {  //after discussion i accept OR I decided add
		//	sentenceOrParagraphHintProbablity=0.7;  
		//}
    	if(messageLocation.equals("stateMesssage")) {	//reasons sentence found in same message in which state is found			
			dateDiffProbability=0.5;  
		}
    	else if(messageLocation.equals("proposalSummary")) { //each pep has a final pep summary written on python website 
    		dateDiffProbability=0.5;
    	}
    	else if(messageLocation.equals("nearbyMessages")) {	//all other messages of the pep		
			if (Math.abs(dateDiff) <= 7) { //less than 7 days from state message gets higher score
				dateDiffProbability=0.5;
				//continue;	//skip messages more than specified no of days
			}
    	}
    	
    	 if (Math.abs(dateDiff) <= 3) { dateDiffProbability=0.9;		//less than 7 days from state message gets higher score					
			//continue;	//skip messages more than specified no of days
    	 }else if (Math.abs(dateDiff) > 3 && Math.abs(dateDiff) <= 7){				  	dateDiffProbability=0.6;			  				  }
    	 else if (Math.abs(dateDiff) > 7 && Math.abs(dateDiff) <= 150){				  	dateDiffProbability=0.2;				  			  }
    	 else {	  	dateDiffProbability=0.0;				   			  }
    	 
    	 reasonProbabilityScore = sentenceOrParagraphHintProbablity + dateDiffProbability;
    	
    } 
    
    
	//calculate probabilities
	//for every case, we assign a value to each variable, considering its influence in that case 
	//for each variable, 0 - no probability, 0.5 full probability and anything above is to make the case have more influence
	//	  sentenceOrParagraphHintProbablity, 			//THE MAIN DETERMINANT.. based on different combination of terms found in a sentence
	//    messageSubjectHintProbablityScore, 			//if terms (like pep&accepted OR pep&resolution) are found in the message subject, a probability is assigned
	//    negationTermPenalty,				 			//a negative word like 'no' in a  sentence gets a '-0.8' as negation penalty
	//    messageLocationProbabilityScore,	 			//the location of the message also gives if different scores..state message, pep summary or 'other messages'
	//    termsLocationWithMessageProbabilityScore, 	//if the combination is found in a sentence, adjacent sentences or paragraph ALSO FOR STATE MESSAGE sentence and nearby sentences, paragraph or nearby paragraphs
	//    dateDiffProbability, 	  						//less than 7 days from state message gets high probability, all others don't get any
	//    authorRoleProbability;  						//different probabilities assigned to  message author based on author {bdfl, proposalauthor, pep editors, core developers and other members}
	public void calculateAllProbabilities_UpdateMessageType(Connection conn
			//String reasonsTermsFoundList, String reasonsIdentifierTermsList, String statesSubstatesList,String identifiersTermList,String entitiesTermList,
			//String specialTermList,String negationTermList,String positiveWordList,String negativeWordList,		double v_termsLocationWithMessageProbabilityScore    //for state message, we pass the location of sentence, (sentence, sentence + previous, paragraph, etc)
			) {
		//System.out.println("calculateAllProbabilities_UpdateMessageType: ");
		//MESSAGE LOCATION
		//FEB 2019, ALSO MESSAGE LEVEL RULE WEIGTAGE ASSIGNMENT
		messageSubjectHintProbablityScore=0.0; //start value ,,its not set before here 
		if(messageLocation.equals("stateMesssage")) {	//reasons sentence found in same message in which state is found
			messageLocationProbabilityScore=0.9;
			dateDiffProbability=0.5;  authorRoleProbability = 0.5;  
			messageSubjectHintProbablityScore=0.5;   //because its the state message, highly likely its as important message as 'pep 308 resolution', therefrore we give high weitage although there is no message subject
			termsLocationWithMessageProbabilityScore = v_termsLocationWithMessageProbabilityScore; //different vales are passed to this function as parameter based on sentence, sentence + previous, etc
		}
		else if(messageLocation.equals("proposalSummary")) { //each pep has a final pep summary written on python website 
			messageLocationProbabilityScore=0.9;
			dateDiffProbability=0.5;	// the datediff probability is high 
			authorRoleProbability=0.5; //the proposal summary does have higher probability as its written by decision makers
			messageSubjectHintProbablityScore=0.5;   //because its the proposal summary, highly likely its as important message as 'pep 308 resolution', therefrore we give high weitage although there is no message subject			
			
			if(location.equals("sentence")) 				{		termsLocationWithMessageProbabilityScore = 0.8;			}   
			else if (location.equals("adjacentSentence")) 	{		termsLocationWithMessageProbabilityScore = 0.6;			}
			else if (location.equals("paragraph")) 			{		termsLocationWithMessageProbabilityScore = 0.3;			}
		}
		else if(messageLocation.equals("nearbyMessages")) {	//all other messages of the pep
			messageLocationProbabilityScore=0.5;			
			//double messageDateDiffProbability=0.0, messageAuthorRoleProbability=0.0;
			if (Math.abs(dateDiff) <= 7) { //less than 7 days from state message gets higher score
				dateDiffProbability=0.5;
				//continue;	//skip messages more than specified no of days
			}	
//$$ System.out.println("Checking message: "+mid+" label: " + label + " date:" + v_date2);
			//dateDiffProbability=messageDateDiffProbability;
			
			//"authorsrole", "otherCommunityMember", "bdfl", "coredeveloper","pepeditors", "proposalAuthor", "bdfl_delegate"			
			String decisionMembers[] = {"proposalAuthor","bdfl","coredeveloper","bdfl_delegate"}; //if message is written by these members, they get higher score
			for(String dm: decisionMembers) {
				if(authorsRole.equals(dm)) {
					authorRoleProbability=0.5;
				}
			}
			//authorRoleProbability=messageAuthorRoleProbability;
			//these probabilities i think are already assigned
			if(location.equals("sentence")) 				{		termsLocationWithMessageProbabilityScore = 0.8;			}   
			else if (location.equals("adjacentSentence")) 	{		termsLocationWithMessageProbabilityScore = 0.6;			}
			else if (location.equals("paragraph")) 			{		termsLocationWithMessageProbabilityScore = 0.3;			}
		}
		else { // should not reach this far
			System.out.println("ERROR ...Message Location not assigned inside code");
		}
		//MESSAGE SUBJECT
		//if (location.equals("sameMessage") && containsReasonsTerms && containsReasonsIdentifierTerms && containsStateTerms) {probability = "very high"; } //same message as state
		//the following witll be covered in the subsequent if, but i have it for flexibility later
		//this combination i expect to be most common in message subject
		if(messageSubjectContainsProposalIdentifierTerms && messageSubjectContainsDecisionTerms) {						//"very very high";							
			messageSubjectHintProbablityScore= 1.2;	//System.out.println("messageSubjectHintProbablityScore: "+messageSubjectHintProbablityScore);
		}//following combination may be lesser
		else if((messageSubjectContainsProposalIdentifierTerms && messageSubjectContainsStatesSubstates) || (messageSubjectContainsProposalIdentifierTerms && messageSubjectContainsSpecialTerms)  ) {	//"very high";							
				messageSubjectHintProbablityScore= 0.5;	//System.out.println("messageSubjectHintProbablityScore: "+messageSubjectHintProbablityScore);
		}					
		else if(messageSubjectContainsEntitiesTerms && messageSubjectContainsSpecialTerms) { // bdfl pronouncement				//"very high";							
				messageSubjectHintProbablityScore= 0.5; //System.out.println("messageSubjectHintProbablityScore: "+messageSubjectHintProbablityScore);
		}							
		//else {//low	
		//	messageSubjectHintProbablityScore= 0.0;	//System.out.println("messageSubjectHintProbablityScore: "+messageSubjectHintProbablityScore);
		//}
		//we only want the original message no replies or forwarded messages, especially for the message subjects where decision terms exist		
		String neg[] = {"re","fwd"};
		for (String m: neg) {
			if(messageSubject.trim().startsWith(m)) {
				messageSubjectNegationFound=true;
			}
		}
		//cut the score if negation found...maybe we just dont consider these messages...and skip them
		if(messageSubjectNegationFound) {
			if(messageSubjectHintProbablityScore>=0.5) {	messageSubjectHintProbablityScore= messageSubjectHintProbablityScore-0.5;		}	//we dont want to make it negative						
		}
		
		//SENTENCE OR PARAGRAPH PROBABILITY
		//if (location.equals("sameMessage") && containsReasonsTerms && containsReasonsIdentifierTerms && containsStateTerms) {probability = "very high"; } //same message as state
		//the following witll be covered in the subsequent if, but i have it for flexibility later
		//all those 'very high'
		if(sentenceOrParagraph_containsReasonsTerms && sentenceOrParagraph_containsStatesSubstates && sentenceOrParagraph_containsIdentifierTerms) {  //most common combination
			sentenceOrParagraphHintProbablity=0.7; 
		}	//even same sentence 
		else if(sentenceOrParagraph_containsReasonsTerms && sentenceOrParagraph_containsStatesSubstates && sentenceOrParagraph_containsEntitiesTerms) {  //after discussion i accept OR I decided add
			sentenceOrParagraphHintProbablity=0.7;  
		}	//even same sentence 
		//july 2018 added new one 'There have been some comments in favour of the proposal, no objections to the proposal as a whole, and some questions and objections about specific details. 
		//These are believed by the author to have been addressed by making changes to the PEP.'
		else if(sentenceOrParagraph_containsReasonsTerms && sentenceOrParagraph_containsIdentifierTerms && sentenceOrParagraph_containsEntitiesTerms) {  // comments/questions/objections addressed author pep
			sentenceOrParagraphHintProbablity=0.7;  
		}
		else if(sentenceOrParagraph_containsReasonsTerms && sentenceOrParagraph_containsStatesSubstates && sentenceOrParagraph_containsReasonsIdentifierTerms) {
			sentenceOrParagraphHintProbablity=0.7; 
		}		
		else if(sentenceOrParagraph_containsDecisionTerms && sentenceOrParagraph_containsReasonsTerms && sentenceOrParagraph_containsEntitiesTerms) {
			sentenceOrParagraphHintProbablity=0.7;  
		}
		//all below combinations added aug 2018(or later)..for pep 311
		// Entity, state and proposal identifier as in pepe 365 = 'After reading all this I really don t believe that adding egg support to the stdlib at this time is the right thing to do I am therefore rejecting the PEP'
		else if(sentenceOrParagraph_containsEntitiesTerms && sentenceOrParagraph_containsStatesSubstates && sentenceOrParagraph_containsIdentifierTerms) {
			sentenceOrParagraphHintProbablity=0.3;  
		}
		//if message author = proposal author, -- we have given more weightage in the message subject probabilities 
		//we just need 'proposal identifier' and 'reason' in sentence. and within dates - as its more likely
		else if(sentenceOrParagraph_containsIdentifierTerms && sentenceOrParagraph_containsReasonsTerms) {
			sentenceOrParagraphHintProbablity=0.3;  //to many false postives so we give little weightage
		}
		//this combination was added before/maybe first time .. this helps for some pep, but for pep 311, it moves the actual sentence be lower in ranking
		else if(sentenceOrParagraph_containsStatesSubstates && sentenceOrParagraph_containsReasonsTerms) {
			sentenceOrParagraphHintProbablity=0.7;  
		}
		//aug 2018 'If everyone who claims they understand the issues actually does, why is it so hard to reach a consensus?'
		else if(sentenceOrParagraph_containsEntitiesTerms && sentenceOrParagraph_containsReasonsTerms) {
			sentenceOrParagraphHintProbablity=0.3;  //to many false postives so we give little weightage
		}
		//aug 2018...pep 3139 manual reason
		else if(sentenceOrParagraph_containsIdentifierTerms && sentenceOrParagraph_containsDecisionTerms ) {
			sentenceOrParagraphHintProbablity=0.3;  //to many false postives so we give little weightage
		}
		else if(sentenceOrParagraph_containsEntitiesTerms && sentenceOrParagraph_containsDecisionTerms ) {	//pep 507
			sentenceOrParagraphHintProbablity=0.3;  //to many false postives so we give little weightage
		}
		//added late oct 2018.. E + R we found many instances of this E + R combination in sentences ..Eg. PEP386 =  "As you said, consensus has been reached, so just Guido's BDFL stamp of approval is all I can think of."
		else if(sentenceOrParagraph_containsEntitiesTerms && sentenceOrParagraph_containsReasonsTerms ) {	//pep 507
			sentenceOrParagraphHintProbablity=0.3;  //to many false postives so we give little weightage
		}
		//added late feb 2019.. S + PI + R1 .. we found an instance of this in pep 265 =  "This PEP is rejected because the need for it has been largely  fulfilled by Py2.4's sorted() builtin function:."
		//the actual reason is hard to capture here
		else if(sentenceOrParagraph_containsIdentifierTerms && sentenceOrParagraph_containsStatesSubstates && sentenceOrParagraph_containsReasonsIdentifierTerms) {	
			sentenceOrParagraphHintProbablity=0.3;  //rare instances of this case and to prevent flase postives so we give little weightage
		}
		else { //allother cases..just in case
			sentenceOrParagraphHintProbablity=0.0;
		}
		//only reason terms exists...like pep 3113 ...": I see lukewarm support for keeping these at most and probably lukewarm support for removing them at well" 
		if(sentenceOrParagraphHintProbablity== 0.0) { //probability.equals("")) {	//for unmatched instances.....and those not 'very high'
				/* if(containsReasonsTerms && containsReasonsIdentifierTerms) 
							{	 sentenceOrParagraphHintProbablity=0.4;	// probability = "high"; System.out.print(" Here e1");		
							}
							else if(containsReasonsTerms ) {
//								System.out.println("sentenceorParagrapghString: ("+sentenceorParagrapghString+") reasonsTermsFoundList: "+reasonsTermsFoundList + " noOfTerms: "+noOfTerms);
								//WE DONT NEED THE BELOW CHECK AS IT WOULD BE CAPTURED AT SENTENCE LEVEL
								if(location.contains("sentence") && noOfTerms > 1 ) { // we dont consider if a single term is found in an entire paragrapgh, unless there are more than two of the reason terms found, which would be rare	
									sentenceOrParagraphHintProbablity = 0.7; 
								} 
								if(location.contains("sentence")) {
									sentenceOrParagraphHintProbablity = 0.7; 
								} 
							}	
							else {
								sentenceOrParagraphHintProbablity = 0.5; 	//for all other cases
							}
							*/
		}
		//Rest of Paragraph Probability Score...added oct 2018...
		//we have to update/correct this as if all the terms are matched in teh rest of teh paragraph, then whats going to be macthed in curr sentence?
		//something has to be matched in curr sentence. Maybe in curr sentence, only reason exists, so the S and PI terms can be matched in Rest of Pragrapgh
		restOfParagraphProbabilityScore = 0.0;
		//restOfParagraph_containsStatesSubstates= restOfParagraph_containsidentifierTerms= restOfParagraph_containsEntitiesTerms= restOfParagraph_containsSpecialTerms= 
		//restOfParagraph_containsDecisionTerms
		if(sentenceOrParagraph_containsReasonsTerms && restOfParagraph_containsStatesSubstates && restOfParagraph_containsIdentifierTerms) {  //most common combination
			restOfParagraphProbabilityScore=0.7; 
		}	//even same sentence 
		else if(sentenceOrParagraph_containsReasonsTerms && restOfParagraph_containsStatesSubstates && restOfParagraph_containsEntitiesTerms) {  //after discussion i accept OR I decided add
			restOfParagraphProbabilityScore=0.7;  
		}	//even same sentence 
		//july 2018 added new one 'There have been some comments in favour of the proposal, no objections to the proposal as a whole, and some questions and objections about specific details. 
		//These are believed by the author to have been addressed by making changes to the PEP.'
		else if(sentenceOrParagraph_containsReasonsTerms && restOfParagraph_containsIdentifierTerms && restOfParagraph_containsEntitiesTerms) {  // comments/questions/objections addressed author pep
			restOfParagraphProbabilityScore=0.7;  
		}
//		else if(restOfParagraph_containsReasonsTerms && restOfParagraph_containsStatesSubstates && restOfParagraph_containsReasonsIdentifierTerms) {
//			restOfParagraphProbabilityScore=0.7; 
//		}	
		//this helps for some pep, but for pep 311, it moves the actual sentence be lower in ranking
		else if(sentenceOrParagraph_containsReasonsTerms && restOfParagraph_containsStatesSubstates) {
			restOfParagraphProbabilityScore=0.5;  
		}		
		else if(sentenceOrParagraph_containsReasonsTerms && restOfParagraph_containsDecisionTerms && restOfParagraph_containsEntitiesTerms) {
			restOfParagraphProbabilityScore=0.7;  
		}
		//added aug 2018..for pep 311
		//if message author = proposal author, -- we have given more weightage in the message subject probabilities 
		//we just need 'proposal identifier' and 'reason' in sentence. and within dates - as its more likely
		else if(sentenceOrParagraph_containsReasonsTerms && restOfParagraph_containsIdentifierTerms) {
			restOfParagraphProbabilityScore=0.3;  //to many false postives so we give little weightage
		}
		// Entity, state and proposal identifier as in pepe 365 = 'After reading all this I really don t believe that adding egg support to the stdlib at this time is the right thing to do I am therefore rejecting the PEP'
		else if(sentenceOrParagraph_containsEntitiesTerms && restOfParagraph_containsStatesSubstates && restOfParagraph_containsIdentifierTerms) {
			restOfParagraphProbabilityScore=0.5;  
		}
		//aug 2018 'If everyone who claims they understand the issues actually does, why is it so hard to reach a consensus?'
		else if(sentenceOrParagraph_containsReasonsTerms && restOfParagraph_containsEntitiesTerms) {
			restOfParagraphProbabilityScore=0.3;  //to many false postives so we give little weightage
		}
		//aug 2018...pep 3139 manual reason
		//After a long conversation on the stdlib-sig list, I'd like to bring this before you. For those of you not on the peps mailing list, Guido has expressed lukewarmness (well -0.5) to the idea.
		else if(sentenceOrParagraph_containsReasonsTerms && restOfParagraph_containsIdentifierTerms && restOfParagraph_containsDecisionTerms ) {
			restOfParagraphProbabilityScore=0.6;  //to many false postives so we give little weightage
		}
		else if(sentenceOrParagraph_containsReasonsTerms && restOfParagraph_containsEntitiesTerms && restOfParagraph_containsDecisionTerms ) {	//pep 507
			restOfParagraphProbabilityScore=0.6;  //to many false postives so we give little weightage
		}
		else { //allother cases..just in case
			restOfParagraphProbabilityScore=0.0;
		}
		//end of restofparagraph	
		
		//Feb 2019 - previous paragraph
		if(prevParagraph_containsSpecialTerms) {  //most common combination
			prevParagraphSpecialTermProbabilityScore=0.9; 
		}	
		
		//march 2019
		//only for strings which have some probability as some may be ignored for negation, conditional etc
		if(actermsfound && sentenceOrParagraphHintProbablity > 0.1) //if ac terms are foudn and there is some probability already, we increment
			sentenceOrParagraphHintProbablity =sentenceOrParagraphHintProbablity + 0.1;
		//System.out.print(" Finished Calc Probabilities:");
	  //proposalAuthorAskingPronouncement= false,BDFLReviewing=false,BDFLPronouncement=false, communtyReflecting= false;
		
		reasonProbabilityScore = messageSubjectHintProbablityScore + sentenceOrParagraphHintProbablity + restOfParagraphProbabilityScore + 
				sentenceOrParagraphHintProbablity +sentenceLocationInMessageProbabilityScore+sameMsgSubAsStateTripleProbabilityScore + messsageTypeIsReasonMessageProbabilityScore;  
		//maybe sentencehintprobability wil, get updated above code block
		
	}
	
	public void writeToDatabase(Connection connection, String tableNameToStore, boolean outputForDebug, boolean baseline1, boolean baseline2) {
		//System.out.println("writingToDatabase:");
		String prefix="";
		
		if(location.equals("sentence")) 				{		tableNameToStore = "autoextractedreasoncandidatesentences";		prefix = "restofParagraph";		} 
		else if (location.equals("paragraph")) 			{		tableNameToStore = "autoextractedreasoncandidateparagraphs";	prefix = "nearbyParagraphs";		}	
		else if (location.equals("message")) 			{		tableNameToStore = "autoextractedreasoncandidatemessages";		prefix = "nearbyParagraphs";		}	
		//for debug   if(location.length()>0) { //
		//to prevent too much output in console, so we just output if we have some probability, dont out put of nothing found in sentences
		if (sentenceOrParagraphHintProbablity > 0.0 || restOfParagraphProbabilityScore > 0.0) {
			//System.out.println();	
			if(outputForDebug) {
	//			System.out.println("tableNameToStore="+tableNameToStore + "location: "+location);
				System.out.println("\tXXXX Final Step, Enough Probability - INSERTING sentenceOrParagraphHintProbablity = "+sentenceOrParagraphHintProbablity+" || restOfParagraphProbabilityScore = "+restOfParagraphProbabilityScore + 
						"\n\tMessageType " + messageType +	" \t Sentence: "+sentenceOrParagraph);	
				System.out.println("\tMessageID "+mid + "(Proposal:"+proposalNum+"), messageLocation: "+messageLocation+", Location:" +location); //+sentenceOrParagraph);
				System.out.println("\treasonPS: ("+reasonProbabilityScore + ", sentenceOrParagraphHintPS: "+sentenceOrParagraphHintProbablity + ", messageSubjectHintPS: ("+messageSubjectHintProbablityScore 
						+ ") termsLocationWithMessageProbabilityScore:("+termsLocationWithMessageProbabilityScore + "), negationTermPenalty: ("+negationTermPenalty+")");	
				System.out.println("\tMATCHED Reasons("+reasonsTermsFoundList+")" + "ReasonsIdentifiers(" + reasonsIdentifierTermsList+")" +  " StatesSubstates("+ statesSubstatesList+")");	
				System.out.println("\tIdentifiers("+ identifiersTermList+")"+  " EntitiesTerm("+ entitiesTermList+")" + " SpecialTerms("+ specialTermList+")");	
				//System.out.print(" MATCHED negationTermList also in reasonList : "+ negationTermList);
				System.out.println("\tnegationTerms("+ negationTermList+")"+ "positiveWords( "+ positiveWordList+")"+ " negativeWords("+ negativeWordList+")");			
			}
			if(messageSubjectHintProbablityScore>0.0) {
				if(outputForDebug)
					System.out.println("\tMessageSubject containsIdentifierTerms: "+sentenceOrParagraph_containsIdentifierTerms   + " Message Subject: "+ messageSubject);
			}			
			
			//OLD WAY, WRITE PROBABILITIES TO DB TABLE
			PreparedStatement pstmt0 = null;
		    try {
		    	  double totalProbability = 0.0;
		    	  //march 2019
			      //variables for calculation of probabilities using machine learning
			      int dd=0,mar=0;
			      //if(dateDiffProbability==0.5) 	  dd=1;
			      //if(authorRoleProbability==0.5) 	  mar=1;
			      //new version....
			      if (Math.abs(dateDiff) <= 3) { dateDiffProbability=0.9;	dd=3;	//less than 7 days from state message gets higher score					
						//continue;	//skip messages more than specified no of days
				  }else if (Math.abs(dateDiff) > 3 && Math.abs(dateDiff) <= 7){				  	dateDiffProbability=0.6;				  	dd=2;			  }
				  else if (Math.abs(dateDiff) > 7 && Math.abs(dateDiff) <= 150){				  	dateDiffProbability=0.2;				  	dd=1;			  }
				  else {	  	dateDiffProbability=0.0;				    dd=0;			  }	
		//$$ System.out.println("Checking message: "+mid+" label: " + label + " date:" + v_date2);
					//dateDiffProbability=messageDateDiffProbability;
			      //"authorsrole", "otherCommunityMember", "bdfl", "coredeveloper","pepeditors", "proposalAuthor", "bdfl_delegate"			
				  String decisionMembers[] = {"proposalAuthor","bdfl","coredeveloper","bdfl_delegate"}; //if message is written by these members, they get higher score
				
				  if(authorsRole.contains("bdfl")) {  authorRoleProbability=0.9;						mar=1;	} //bdfl or delegate					
				  else if (authorsRole.equals("proposalAuthor")) {  authorRoleProbability=0.6;					mar=2;				}//bdfl or delegate		
				  else if (authorsRole.equals("pepeditors")) { authorRoleProbability=0.5;					mar=3;				}	 //nov 2018, added pep editors
				  else if (authorsRole.equals("coredeveloper")) { authorRoleProbability=0.4;					mar=4;				}	 //bdfl or delegate					      
				  else {					authorRoleProbability=0.0;					mar=0;				} //othercommunitymember
		    	
				  
				  if (baseline1) {
			    	  termsLocationWithMessageProbabilityScore=restOfParagraphProbabilityScore=messageLocationProbabilityScore=
		    		  messageSubjectHintProbablityScore=dateDiffProbability=authorRoleProbability=messsageTypeIsReasonMessageProbabilityScore=
		    		  sentenceLocationInMessageProbabilityScore=sameMsgSubAsStateTripleProbabilityScore=reasonLabelFoundUsingTripleExtractionProbabilityScore=
		    		  messageContainsSpecialTermProbabilityScore=prevParagraphSpecialTermProbabilityScore=negationTermPenalty=0;
			    	  
			    	  totalProbability = sentenceOrParagraphHintProbablity;
			      }
			      else if (baseline2) {
			    	  termsLocationWithMessageProbabilityScore=restOfParagraphProbabilityScore=messageLocationProbabilityScore=
				      messageSubjectHintProbablityScore=authorRoleProbability=messsageTypeIsReasonMessageProbabilityScore=
				      sentenceLocationInMessageProbabilityScore=sameMsgSubAsStateTripleProbabilityScore=reasonLabelFoundUsingTripleExtractionProbabilityScore=
				      messageContainsSpecialTermProbabilityScore=prevParagraphSpecialTermProbabilityScore=negationTermPenalty=0;
					    	  
					  totalProbability = sentenceOrParagraphHintProbablity + dateDiffProbability;
			      }
		    	
		    	
		    	
		    	
		    	//   reasonsTermsFoundList, reasonsIdentifierTermsList, statesSubstatesList, identifiersTermList, entitiesTermList,
				 //  specialTermList, decisionTermList, negationTermList,
		      String query = "insert into "+tableNameToStore+"("
		      		+ "proposal, dateValue, messageID, "+location+",isenglishorcode, "
		      		+ " termsMatched,probability,label, authorRole,location,"
		      		+ " messageSubjectHintProbablityScore,"+location+"HintProbablity,"+location+"LocationHintProbability,negationTermPenalty,dateDiffProbability, " //15
		      		+ " authorRoleProbability,messageLocationProbabilityScore, messageTypeIsReasonMessageProbabilityScore, "+location+"LocationInMessageProbabilityScore, sameMsgSubAsStateTripleProbabilityScore, "
		      		+ " "+location+"inFirstParagraph,"+location+"inLastParagraph, "
		      		// fields for machine learning
		      		+ " datediffWeight,authorRoleWeight, "
		      		+ " messageSubjectStatesSubstatesListCount,messageSubjectDecisionTermListCount,messageSubjectProposalIdentifiersTermListCount,messageSubjectEntitiesTermListCount, "
		      		+ " messageSubjectSpecialTermListCount, " //24
		      		//currentSentence
		      		+ " curr"+location+"ReasonsTermsFoundCount,	curr"+location+"ReasonsIdentifierTermsCount,	curr"+location+"StatesSubstatesCount,	curr"+location+"IdentifiersTermCount,"
		      		+ " curr"+location+"EntitiesTermCount,		curr"+location+"SpecialTermCount,				curr"+location+"DecisionTermCount,dateDiff,"		      		
		      		+ " curr"+location+"ReasonsTermsFoundList, 	curr"+location+"ReasonsIdentifierTermsList, 	curr"+location+"StatesSubstatesList, 	curr"+location+"IdentifiersTermList, curr"+location+"EntitiesTermList,"
		      		+ " curr"+location+"SpecialTermList, 		curr"+location+"DecisionTermList, 				curr"+location+"NegationTermList," //40
		      		
		      		+ " messageSubjectStatesSubstatesList, messageSubjectDecisionTermList, messageSubjectVerbList, " 
		      		+ " messageSubjectIdentifiersTermList, messageSubjectEntitiesTermList, messageSubjectSpecialTermList, "
		      		
		      		+ " entireParagraph, restOfParagraph, " //48
		      				      				      		
					+ " "+prefix+"StateTermList,"   +prefix+"IdentifiersTermList, "+prefix+"EntityTermList, "
					+ "" +prefix+"SpecialTermList, "+prefix+"DecisionTermList,    "+prefix+"ReasonsTermsList," //54
		      		//+ " restOfParagraphContainsStateTerms, restOfParagraphContainsIdentifierTerms, restOfParagraphContainsEntitiesTerms, restOfParagraphContainsSpecialTerms, restOfParagraphContainsDecisionTerms, "
		      		+ " "+prefix+"StateTermsCount, "+prefix+"IdentifiersTermsCount, "+prefix+"EntityTermsCount,"+prefix+"SpecialTermsCount,"+prefix+"DecisionTermsCount, "+prefix+"ReasonsTermsCount," //60
		      		+ " "+prefix+"ProbabilityScore, reasonLabelFoundUsingTripleExtractionProbabilityScore,messageContainsSpecialTerm, messageContainsSpecialTermProbabilityScore, "
		      		+ " dateOfStateMessage, messageSubject,messageType,messageTypeIsReasonMessage,SameMsgSubAsStateTriple,prevParagraphSpecialTermsCount,prevParagraphSpecialTermProbabilityScore, "
		      		+ " containsReason, totalProbability) " //64
		      		+ " values (?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?,  ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?,  ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, "
		      		+ " ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?) ";
		      pstmt0 = connection.prepareStatement(query); // create a statement
		      //message metadata
		      pstmt0.setInt(1,proposalNum); 						pstmt0.setDate(2, dateVal);      			
		      pstmt0.setInt(3, mid); pstmt0.setString(4,sentenceOrParagraph.trim());	//SHOUDL ADD NEXT SENTENCE HERE..just to see if 'sentence' does have adjacent sentences - i think it does..
		      pstmt0.setBoolean(5,isEnglishOrCode);
		      pstmt0.setString(6, termsmatched ); 					pstmt0.setDouble(7, finalProbability); // set input parameter 3
		      if(label==null|| label.isEmpty()) {} 		      else { label=label.trim(); }
		      if(authorsRole==null|| authorsRole.isEmpty()) {}		      else { authorsRole=authorsRole.trim(); }
		      
		     //code - for calcu;ation shifted above
		      
		      
		      //for calculation of probabilities using if/else probabilities 
		      pstmt0.setString(8, label); 		      				pstmt0.setString(9, authorsRole);	
		      pstmt0.setString(10, location );		      	pstmt0.setDouble(11, messageSubjectHintProbablityScore); 
		      pstmt0.setDouble(12, sentenceOrParagraphHintProbablity);	pstmt0.setDouble(13,termsLocationWithMessageProbabilityScore);
		      pstmt0.setDouble(14, negationTermPenalty); 			pstmt0.setDouble(15,dateDiffProbability);	
		      pstmt0.setDouble(16, authorRoleProbability);			pstmt0.setDouble(17,messageLocationProbabilityScore);		pstmt0.setDouble(18,messsageTypeIsReasonMessageProbabilityScore);	
		      pstmt0.setDouble(19,sentenceLocationInMessageProbabilityScore);		pstmt0.setDouble(20,sameMsgSubAsStateTripleProbabilityScore);	
		      pstmt0.setBoolean(21,isFirstParagraph);				pstmt0.setBoolean(22,isLastParagraph);
		      pstmt0.setInt(23, dd);												pstmt0.setInt(24, mar);
		      pstmt0.setInt(25,messageSubjectStatesSubstatesListCount);		    	pstmt0.setInt(26,messageSubjectDecisionTermListCount);		      
		      pstmt0.setInt(27,messageSubjectProposalIdentifiersTermListCount);		pstmt0.setInt(28,messageSubjectEntitiesTermListCount);		      
		      pstmt0.setInt(29,messageSubjectSpecialTermListCount);
		      //sentence or paragraph terms counts to 1, can change to original value if needed
		      if (reasonsTermsFoundCount > 0) reasonsTermsFoundCount=1;	
		      if (reasonsIdentifierTermsCount > 0) reasonsIdentifierTermsCount=1;
		      if (statesSubstatesCount > 0) statesSubstatesCount=1;
		      if (identifiersTermCount > 0) identifiersTermCount=1;
		      if (entitiesTermCount > 0) entitiesTermCount=1;
		      if (specialTermCount  > 0) specialTermCount=1;
		      if (decisionTermCount > 0) decisionTermCount=1;
		      
		      pstmt0.setInt(30,reasonsTermsFoundCount); pstmt0.setInt(31,reasonsIdentifierTermsCount); 
		      pstmt0.setInt(32,statesSubstatesCount); 	pstmt0.setInt(33,identifiersTermCount); 
		      pstmt0.setInt(34,entitiesTermCount); 		pstmt0.setInt(35,specialTermCount); 
		      pstmt0.setInt(36,decisionTermCount); 		
		      
		      pstmt0.setInt(37,dateDiff); 	
		      
		      pstmt0.setString(38,reasonsTermsFoundList); 	pstmt0.setString(39,reasonsIdentifierTermsList); 	pstmt0.setString(40,statesSubstatesList); pstmt0.setString(41,identifiersTermList);
		      pstmt0.setString(42,entitiesTermList); 		pstmt0.setString(43,specialTermList); 				pstmt0.setString(44,decisionTermList);	  pstmt0.setString(45,negationTermList);
		      
		      pstmt0.setString(46,messageSubjectStatesSubstatesList);		      	pstmt0.setString(47, messageSubjectDecisionTermList);
		      pstmt0.setString(48,messageSubjectVerbList);		      				pstmt0.setString(49,messageSubjectProposalIdentifiersTermList);
		      pstmt0.setString(50,messageSubjectEntitiesTermList);		      		pstmt0.setString(51,messageSubjectSpecialTermList);
		      
		      pstmt0.setString(52,entireParagraph); 	pstmt0.setString(53,restOfParagraph); 
		      
		      pstmt0.setString(54,restOfParagraph_StatesSubstatesList);	pstmt0.setString(55,restOfParagraph_identifiersTermList); 		pstmt0.setString(56,restOfParagraph_entitiesTermList);
		      pstmt0.setString(57,restOfParagraph_specialTermList);   pstmt0.setString(58,restOfParagraph_decisionTermList); 			pstmt0.setString(59,restOfParagraph_ReasonsTermsList);
		     
		      //rest of paragraph counts to 1, can change to original value if needed
		      if (restOfParagraphStatesSubstatesTermsCount > 0) restOfParagraphStatesSubstatesTermsCount=1;	
		      if (restOfParagraph_identifiersTermCount > 0) 	restOfParagraph_identifiersTermCount=1;
		      if (restOfParagraph_entitiesTermCount > 0) 		restOfParagraph_entitiesTermCount=1;
		      if (restOfParagraph_specialTermCount  > 0) 		restOfParagraph_specialTermCount=1;
		      if (restOfParagraph_decisionTermCount > 0) 		restOfParagraph_decisionTermCount=1;
		      if (restOfParagraph_ReasonsTermsCount > 0) 		restOfParagraph_ReasonsTermsCount=1;
		      
		      pstmt0.setInt(60,restOfParagraphStatesSubstatesTermsCount);   pstmt0.setInt(61,restOfParagraph_identifiersTermCount);   	pstmt0.setInt(62,restOfParagraph_entitiesTermCount);
		      pstmt0.setInt(63,restOfParagraph_specialTermCount);    pstmt0.setInt(64,restOfParagraph_decisionTermCount);				pstmt0.setInt(65,restOfParagraph_ReasonsTermsCount);
		      
		      pstmt0.setDouble(66,restOfParagraphProbabilityScore); 	 pstmt0.setDouble(67,reasonLabelFoundUsingTripleExtractionProbabilityScore);
		      pstmt0.setBoolean(68,messageContainsSpecialTerm); pstmt0.setDouble(69,messageContainsSpecialTermProbabilityScore); //FEB 2019
		      pstmt0.setDate(70,dateOfStateMessage);	pstmt0.setString(71,messageSubject);		 pstmt0.setString(72,messageType);	     
		      //System.out.println("\t sameMsgSubAsStateTriple" + sameMsgSubAsStateTriple);
		      pstmt0.setBoolean(73,messsageTypeIsReasonMessage);   pstmt0.setBoolean(74,sameMsgSubAsStateTriple);    
		      pstmt0.setInt(75,containsReason);		pstmt0.setDouble(76,prevParagraphSpecialTermProbabilityScore);  
		      
		      
		      
		      if (baseline1) {
		    	  //termsLocationWithMessageProbabilityScore=restOfParagraphProbabilityScore=messageLocationProbabilityScore=
	    		  //messageSubjectHintProbablityScore=dateDiffProbability=authorRoleProbability=messsageTypeIsReasonMessageProbabilityScore
	    		  //sentenceLocationInMessageProbabilityScore=sameMsgSubAsStateTripleProbabilityScore=reasonLabelFoundUsingTripleExtractionProbabilityScore
	    		  //messageContainsSpecialTermProbabilityScore=prevParagraphSpecialTermProbabilityScore=negationTermPenalty=0;
		    	  
		    	  totalProbability = sentenceOrParagraphHintProbablity;
		      }
		      else if (baseline2) {
		    	  //termsLocationWithMessageProbabilityScore=restOfParagraphProbabilityScore=messageLocationProbabilityScore=
			      //messageSubjectHintProbablityScore=authorRoleProbability=messsageTypeIsReasonMessageProbabilityScore
			      //sentenceLocationInMessageProbabilityScore=sameMsgSubAsStateTripleProbabilityScore=reasonLabelFoundUsingTripleExtractionProbabilityScore
			      //messageContainsSpecialTermProbabilityScore=prevParagraphSpecialTermProbabilityScore=negationTermPenalty=0;
				    	  
				  totalProbability = sentenceOrParagraphHintProbablity + dateDiffProbability;
		      }
		      else {	//for our method	    	  
			      totalProbability = sentenceOrParagraphHintProbablity
			    		  +termsLocationWithMessageProbabilityScore
			    		  +restOfParagraphProbabilityScore+messageLocationProbabilityScore
			    		  +messageSubjectHintProbablityScore
			    		  +dateDiffProbability+authorRoleProbability
			    		  +messsageTypeIsReasonMessageProbabilityScore
			    		  +sentenceLocationInMessageProbabilityScore
			    		  +sameMsgSubAsStateTripleProbabilityScore
			    		  +reasonLabelFoundUsingTripleExtractionProbabilityScore
			    		  +messageContainsSpecialTermProbabilityScore
			    		  +prevParagraphSpecialTermProbabilityScore
			    		  -negationTermPenalty;
		      }
		      
		      pstmt0.setInt(77,prevParagraphSpecialTermsCount);	pstmt0.setDouble(78,totalProbability); 
		      pstmt0.executeUpdate(); // execute insert statement	
		      pstmt0.close();
//$$			      System.out.println("\t inserted row in db  combination: "+r.getCombination() + " sentence " + r.getSentenceString());
		    } catch (Exception e) {
		      e.printStackTrace();	System.out.println(StackTraceToString(e)  );	
		    }  
		}
		else {
			//System.out.println("\nElse NOT ENOUGH PROBABILITY - sentenceOrParagraphHintProbablity < 0.0 && restOfParagraphProbabilityScore < 0.0), tableNameToStore="+tableNameToStore );
		}
		if(outputForDebug)
			System.out.println("");
	}
	
	public void writeMessageToDatabase(Connection connection, String tableNameToStoreMessage, boolean outputForDebug) {
		//System.out.println("writingToDatabase:");
		location= "message";
		if(messageSubjectHintProbablityScore>0.0) {
			if(outputForDebug)
				System.out.println("\tMessageSubject containsIdentifierTerms: "+sentenceOrParagraph_containsIdentifierTerms   + " Message Subject: "+ messageSubject);
		}			
		//|| (tableNameToStore.equals("autoextractedreasoncandidatemessages")
		//OLD WAY, WRITE PROBABILITIES TO DB TABLE
		PreparedStatement pstmt0 = null;
	    try {
	    	//   reasonsTermsFoundList, reasonsIdentifierTermsList, statesSubstatesList, identifiersTermList, entitiesTermList,
			 //  specialTermList, decisionTermList, negationTermList,
	      String query = "insert into "+tableNameToStoreMessage+"("
	      		+ "proposal, dateValue, messageID, "+location+",isenglishorcode, "
	      		+ " termsMatched,probability,label,authorRole,location," //10
	      		+ " messageSubjectHintProbablityScore, " //12
	      		+ " messageLocationProbabilityScore, messageTypeIsReasonMessageProbabilityScore, sameMsgSubAsStateTripleProbabilityScore, " //16
	      		// fields for machine learning
	      		+ " datediffWeight,authorRoleWeight, " //18
	      		+ " messageSubjectStatesSubstatesListCount,messageSubjectDecisionTermListCount,messageSubjectProposalIdentifiersTermListCount,messageSubjectEntitiesTermListCount, " //22
	      		+ " messageSubjectSpecialTermListCount, " //23
	      		
	      		+ " messageSubjectStatesSubstatesList, messageSubjectDecisionTermList, messageSubjectVerbList, " //26
	      		+ " messageSubjectIdentifiersTermList, messageSubjectEntitiesTermList, messageSubjectSpecialTermList, " //29
	      		
	      		+ " messageContainsSpecialTerm, messageContainsSpecialTermProbabilityScore, " //31
	      		+ " dateOfStateMessage, messageSubject,messageType,messageTypeIsReasonMessage,SameMsgSubAsStateTriple, containsReason,datediff,dateDiffProbability,authorRoleProbability, totalProbability) " //37
	      		+ " values (?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?,  ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?,  ?,?,?,?) ";
	      pstmt0 = connection.prepareStatement(query); // create a statement
	      //message metadata
	      pstmt0.setInt(1,proposalNum); 						pstmt0.setDate(2, dateVal);      			
	      pstmt0.setInt(3, mid); pstmt0.setString(4,"");	//SHOUDL ADD NEXT SENTENCE HERE..just to see if 'sentence' does have adjacent sentences - i think it does..
	      pstmt0.setBoolean(5,isEnglishOrCode);
	      pstmt0.setString(6, termsmatched ); 					pstmt0.setDouble(7, finalProbability); // set input parameter 3
	      if(label==null|| label.isEmpty()) {} 		     			  else { label=label.trim(); }
	      if(authorsRole==null|| authorsRole.isEmpty()) {}		      else { authorsRole=authorsRole.trim(); }
	      //for calculation of probabilities using if/else probabilities 
	      pstmt0.setString(8, label); 		      				pstmt0.setString(9, authorsRole);	
	      pstmt0.setString(10, location );		      			pstmt0.setDouble(11, messageSubjectHintProbablityScore);  		
	      //pstmt0.setDouble(13, authorRoleProbability);			
	      pstmt0.setDouble(12,messageLocationProbabilityScore);		pstmt0.setDouble(13,messsageTypeIsReasonMessageProbabilityScore);	
	      pstmt0.setDouble(14,sameMsgSubAsStateTripleProbabilityScore);	
	      
	      //variables for calculation of probabilities using machine learning
	      int dd=0,mar=0;
	      if (Math.abs(dateDiff) <= 3) { dateDiffProbability=0.9;	dd=3;	//less than 7 days from state message gets higher score					
				//continue;	//skip messages more than specified no of days
		  }else if (Math.abs(dateDiff) > 3 && Math.abs(dateDiff) <= 7){				  	dateDiffProbability=0.6;				  	dd=2;			  }
		  else if (Math.abs(dateDiff) > 7 && Math.abs(dateDiff) <= 150){				  	dateDiffProbability=0.2;				  	dd=1;			  }
		  else {	  	dateDiffProbability=0.0;				    dd=0;			  }	
//$$ System.out.println("Checking message: "+mid+" label: " + label + " date:" + v_date2);
			//dateDiffProbability=messageDateDiffProbability;			
		  //"authorsrole", "otherCommunityMember", "bdfl", "coredeveloper","pepeditors", "proposalAuthor", "bdfl_delegate"			
		  String decisionMembers[] = {"proposalAuthor","bdfl","coredeveloper","bdfl_delegate"}; //if message is written by these members, they get higher score
		
		  if(authorsRole.contains("bdfl")) {  authorRoleProbability=0.9;						mar=1;	} //bdfl or delegate					
		  else if (authorsRole.equals("proposalAuthor")) {  authorRoleProbability=0.6;					mar=2;				}//bdfl or delegate		
		  else if (authorsRole.equals("pepeditors")) { authorRoleProbability=0.5;					mar=3;				}	 //nov 2018, added pep editors
		  else if (authorsRole.equals("coredeveloper")) { authorRoleProbability=0.4;					mar=4;				}	 //bdfl or delegate					      
		  else {					authorRoleProbability=0.0;					mar=0;				} //othercommunitymember
			
	      pstmt0.setInt(15, dd);												pstmt0.setInt(16, mar);
	      pstmt0.setInt(17,messageSubjectStatesSubstatesListCount);		    	pstmt0.setInt(18,messageSubjectDecisionTermListCount);		      
	      pstmt0.setInt(19,messageSubjectProposalIdentifiersTermListCount);		pstmt0.setInt(20,messageSubjectEntitiesTermListCount);		      
	      pstmt0.setInt(21,messageSubjectSpecialTermListCount);
	      //sentence or paragraph terms counts to 1, can change to original value if needed
	      if (reasonsTermsFoundCount > 0) reasonsTermsFoundCount=1;	
	      if (reasonsIdentifierTermsCount > 0) reasonsIdentifierTermsCount=1;
	      if (statesSubstatesCount > 0) statesSubstatesCount=1;
	      if (identifiersTermCount > 0) identifiersTermCount=1;
	      if (entitiesTermCount > 0) entitiesTermCount=1;
	      if (specialTermCount  > 0) specialTermCount=1;
	      if (decisionTermCount > 0) decisionTermCount=1;
	      
	      //pstmt0.setInt(37,dateDiff); 	
	      pstmt0.setString(22,messageSubjectStatesSubstatesList);		      	pstmt0.setString(23, messageSubjectDecisionTermList);
	      pstmt0.setString(24,messageSubjectVerbList);		      				pstmt0.setString(25,messageSubjectProposalIdentifiersTermList);
	      pstmt0.setString(26,messageSubjectEntitiesTermList);		      		pstmt0.setString(27,messageSubjectSpecialTermList);
	     	      
	      pstmt0.setBoolean(28,messageContainsSpecialTerm); pstmt0.setDouble(29,messageContainsSpecialTermProbabilityScore); //FEB 2019
	      pstmt0.setDate(30,dateOfStateMessage);				pstmt0.setString(31,messageSubject);		 		pstmt0.setString(32,messageType);	     
	      //System.out.println("\t sameMsgSubAsStateTriple" + sameMsgSubAsStateTriple);
	      pstmt0.setBoolean(33,messsageTypeIsReasonMessage);   	pstmt0.setBoolean(34,sameMsgSubAsStateTriple);   	pstmt0.setInt(35,containsReason);
	      pstmt0.setInt(36,dateDiff);
	      pstmt0.setDouble(37,dateDiffProbability);	pstmt0.setDouble(38,authorRoleProbability);
	      
	      totalProbabilityScore = messageLocationProbabilityScore+messageSubjectHintProbablityScore
	    		  +dateDiffProbability+authorRoleProbability+messsageTypeIsReasonMessageProbabilityScore+sameMsgSubAsStateTripleProbabilityScore
	    		  +messageContainsSpecialTermProbabilityScore;
	      
	      pstmt0.setDouble(39,totalProbabilityScore); 
	      
	      pstmt0.executeUpdate(); // execute insert statement
	      pstmt0.close();
//$$	  System.out.println("\t inserted row in db  combination: "+r.getCombination() + " sentence " + r.getSentenceString());
	    } catch (Exception e) {
	      e.printStackTrace();	System.out.println(StackTraceToString(e)  );	
	    }  
	}
	
	public  String StackTraceToString(Exception ex) {
		String result = ex.toString() + "\n";
		StackTraceElement[] trace = ex.getStackTrace();
		for (int i=0;i<trace.length;i++) {
			result += trace[i].toString() + "\n";
		}
		return result;
	}
	
	//march 2019	
	
	public Integer getId() {		return id;	}
	public String getText() {		return text;	}
	public void setText(String v_text) {		text = v_text;	}

	public void setId(Integer v_id) {		id = v_id;	}
	public String getLabel() {		return label;	}
	public void setLabel(String label) {		this.label = label;	}
	public Integer getProposalNum() {		return proposalNum;	}
	public void setProposalNum(int proposalNum) {		this.proposalNum = proposalNum;	}
	public Integer getMid() {		return mid;	}
	public void setMid(int mid) {		this.mid = mid;	}
	public Date getDateVal() {		return dateVal;	}
	public void setDateVal(Date dateVal) {		this.dateVal = dateVal;	}
	public String getSentenceOrParagraph() {		return sentenceOrParagraph;	}
	public void setSentenceOrParagraph(String sentence) {		this.sentenceOrParagraph = sentence;	}
	public String getTermsmatched() {		return termsmatched;	}
	public void setTermsmatched(String termsmatched) {		this.termsmatched = termsmatched;	}
	public String getAuthor() {		return author;	}
	public void setAuthor(String author) {		this.author = author;	}
	//public void setFinalProbability(Double v_finalProbability) {		this.finalProbability = v_finalProbability;	}
			
	public  void setParaMinusCurrSentence(String v_restOfParagraph) {		restOfParagraph = v_restOfParagraph;	}
	public  String getEntireParagraph() {		return entireParagraph;	}
	public  void setEntireParagraph(String v_entireParagraph) {		entireParagraph = v_entireParagraph;	}
	
	//rst of paragraph
	public  boolean isRestOfParagraph_containsStatesSubstates() {		return restOfParagraph_containsStatesSubstates;	}
	public  void setRestOfParagraph_containsStatesSubstates(boolean v_restOfParagraph_containsStatesSubstates) {		restOfParagraph_containsStatesSubstates = v_restOfParagraph_containsStatesSubstates;	}
	public  String getRestOfParagraph_StatesSubstatesList() {		return restOfParagraph_StatesSubstatesList;	}
	public  void setRestOfParagraph_StatesSubstatesList(String v_restOfParagraph_StatesSubstatesList) {		restOfParagraph_StatesSubstatesList = v_restOfParagraph_StatesSubstatesList;	}
	public  Integer getRestOfParagraphStatesSubstatesTermsCount() {		return restOfParagraphStatesSubstatesTermsCount;	}
	public  void setRestOfParagraphStatesSubstatesTermsCount(Integer v_restOfParagraphStatesSubstatesTermsCount) { restOfParagraphStatesSubstatesTermsCount = v_restOfParagraphStatesSubstatesTermsCount; }
	public  void setRestOfParagraph(String v_restOfParagraph) {		restOfParagraph = v_restOfParagraph;	}
	public  String getRestOfParagraph() {		return restOfParagraph;	}
	
		
	public  boolean isRestOfParagraph_containsIdentifierTerms() {		return restOfParagraph_containsIdentifierTerms;	}
	public  void setRestOfParagraph_containsIdentifierTerms(boolean v_restOfParagraph_containsidentifierTerms) { restOfParagraph_containsIdentifierTerms = v_restOfParagraph_containsidentifierTerms;	}
	public  boolean isRestOfParagraph_containsEntitiesTerms() {	return restOfParagraph_containsEntitiesTerms;	}
	public  void setRestOfParagraph_containsEntitiesTerms(boolean v_restOfParagraph_containsEntitiesTerms) {	restOfParagraph_containsEntitiesTerms = v_restOfParagraph_containsEntitiesTerms;	}
	public  boolean isRestOfParagraph_containsSpecialTerms() {		return restOfParagraph_containsSpecialTerms;	}
	public  void setRestOfParagraph_containsSpecialTerms(boolean v_restOfParagraph_containsSpecialTerms) {    restOfParagraph_containsSpecialTerms = v_restOfParagraph_containsSpecialTerms;	}
	public  boolean isRestOfParagraph_containsDecisionTerms() {		return restOfParagraph_containsDecisionTerms;	}
	public  void setRestOfParagraph_containsDecisionTerms(boolean v_restOfParagraph_containsDecisionTerms) {		restOfParagraph_containsDecisionTerms = v_restOfParagraph_containsDecisionTerms;	}
	public  String getRestOfParagraph_identifiersTermList() {		return restOfParagraph_identifiersTermList;	}
	public  void setRestOfParagraph_identifiersTermList(String v_restOfParagraph_identifiersTermList) {		restOfParagraph_identifiersTermList = v_restOfParagraph_identifiersTermList;	}
	public  String getRestOfParagraph_entitiesTermList() {		return restOfParagraph_entitiesTermList;	}
	public  void setRestOfParagraph_entitiesTermList(String v_restOfParagraph_entitiesTermList) {		restOfParagraph_entitiesTermList = v_restOfParagraph_entitiesTermList;	}
	public  String getRestOfParagraph_specialTermList() {		return restOfParagraph_specialTermList;	}
	public  void setRestOfParagraph_specialTermList(String v_restOfParagraph_specialTermList) {		restOfParagraph_specialTermList = v_restOfParagraph_specialTermList;	}
	public  String getRestOfParagraph_decisionTermList() {		return restOfParagraph_decisionTermList;	}
	public  void setRestOfParagraph_decisionTermList(String v_restOfParagraph_decisionTermList) {		restOfParagraph_decisionTermList = v_restOfParagraph_decisionTermList;	}
	public  Integer getRestOfParagraph_identifiersTermCount() {		return restOfParagraph_identifiersTermCount;	}
	public  void setRestOfParagraph_identifiersTermCount(Integer v_restOfParagraph_identifiersTermCount) {		restOfParagraph_identifiersTermCount = v_restOfParagraph_identifiersTermCount;	}
	public  Integer getRestOfParagraph_entitiesTermCount() {		return restOfParagraph_entitiesTermCount;	}
	public  void setRestOfParagraph_entitiesTermCount(Integer v_restOfParagraph_entitiesTermCount) {		restOfParagraph_entitiesTermCount = v_restOfParagraph_entitiesTermCount;	}
	public  Integer getRestOfParagraph_specialTermCount() {		return restOfParagraph_specialTermCount;	}
	public  void setRestOfParagraph_specialTermCount(Integer v_restOfParagraph_specialTermCount) {		restOfParagraph_specialTermCount = v_restOfParagraph_specialTermCount;	}
	public  Integer getRestOfParagraph_decisionTermCount() {		return restOfParagraph_decisionTermCount;	}
	public  void setRestOfParagraph_decisionTermCount(Integer v_restOfParagraph_decisionTermCount) {		restOfParagraph_decisionTermCount = v_restOfParagraph_decisionTermCount;	}
	public Date getDateOfStateMessage() {		return dateOfStateMessage;	}
	public void setDateOfStateMessage(Date dateOfStateMessage) {		this.dateOfStateMessage = dateOfStateMessage;	}
	
	public  String getMessageLocation() {		return messageLocation;	}
	public  void setMessageLocation(String v_messageLocation) {		this.messageLocation = v_messageLocation;	}
	public  String getLocation() {		return location;	}
	public  void setLocation(String v_location) {		this.location = v_location;	}
	
	public double getMessageSubjectHintProbablityScore() {		return messageSubjectHintProbablityScore;	}
	public void setMessageSubjectHintProbablityScore(double messageSubjectHintProbablityScore) {		this.messageSubjectHintProbablityScore = messageSubjectHintProbablityScore;	}	

	public  double getMessageLocationProbabilityScore() {		return messageLocationProbabilityScore;	}
	public  void setMessageLocationProbabilityScore(double v_messageLocationProbabilityScore) {		this.messageLocationProbabilityScore = v_messageLocationProbabilityScore;	}
	public  double getTermsLocationWithMessageProbabilityScore() {		return termsLocationWithMessageProbabilityScore;	}
	public  void setTermsLocationWithMessageProbabilityScore(double termsLocationWithMessageProbabilityScore) {		this.termsLocationWithMessageProbabilityScore = termsLocationWithMessageProbabilityScore;	}
	public  void setFinalProbability(double v_finalProbability) {		this.finalProbability = v_finalProbability;	}
	public  double getFinalProbability() {		return finalProbability;	}
	public double getNegationTermPenalty() {		return negationTermPenalty;	}
	public void setNegationTermPenalty(double negationTermPenalty) {		this.negationTermPenalty = negationTermPenalty;	}
	public double getDateDiffProbability() {		return dateDiffProbability;	}
	public void setDateDiffProbability(double dateDiffProbability) {		this.dateDiffProbability = dateDiffProbability;	}
	public double getAuthorRoleProbability() {		return authorRoleProbability;	}
	public void setAuthorRoleProbability(double authorRoleProbability) {		this.authorRoleProbability = authorRoleProbability;	}
	
	public  int getDateDiff() {		return dateDiff;	}
	public  void setDateDiff(int v_dateDiff) {		this.dateDiff = v_dateDiff;	}
	public  String getAuthorsRole() {		return authorsRole;	}
	public  void setAuthorsRole(String v_authorsRole) {		this.authorsRole = v_authorsRole;	}
	
	//message subject
	public boolean isMessageSubjectContainsProposalIdentifierTerms() {		return messageSubjectContainsProposalIdentifierTerms;	}
	public void setMessageSubjectContainsProposalIdentifierTerms(boolean messageSubjectContainsProposalIdentifierTerms) {		this.messageSubjectContainsProposalIdentifierTerms = messageSubjectContainsProposalIdentifierTerms;	}
	public boolean isMessageSubjectContainsReasonsTerms() {		return messageSubjectContainsReasonsTerms;	}
	public void setMessageSubjectContainsReasonsTerms(boolean messageSubjectContainsReasonsTerms) {		this.messageSubjectContainsReasonsTerms = messageSubjectContainsReasonsTerms;	}
	public boolean isMessageSubjectContainsReasonsIdentifierTerms() {		return messageSubjectContainsReasonsIdentifierTerms;	}
	public void setMessageSubjectContainsReasonsIdentifierTerms(boolean messageSubjectContainsReasonsIdentifierTerms) {		this.messageSubjectContainsReasonsIdentifierTerms = messageSubjectContainsReasonsIdentifierTerms;	}
	public boolean isMessageSubjectContainsStatesSubstates() {		return messageSubjectContainsStatesSubstates;	}
	public void setMessageSubjectContainsStatesSubstates(boolean messageSubjectContainsStatesSubstates) {		this.messageSubjectContainsStatesSubstates = messageSubjectContainsStatesSubstates;	}
	public boolean isMessageSubjectContainsProposalIdentifierTermList() {		return messageSubjectContainsProposalIdentifierTermList;	}
	public void setMessageSubjectContainsProposalIdentifierTermList(boolean messageSubjectContainsProposalIdentifierTermList) {		this.messageSubjectContainsProposalIdentifierTermList = messageSubjectContainsProposalIdentifierTermList;	}
	public boolean isMessageSubjectContainsPositiveWords() {		return messageSubjectContainsPositiveWords;	}
	public void setMessageSubjectContainsPositiveWords(boolean messageSubjectContainsPositiveWords) {		this.messageSubjectContainsPositiveWords = messageSubjectContainsPositiveWords;	}
	public boolean isMessageSubjectContainsNegativeWords() {		return messageSubjectContainsNegativeWords;	}
	public void setMessageSubjectContainsNegativeWords(boolean messageSubjectContainsNegativeWords) {		this.messageSubjectContainsNegativeWords = messageSubjectContainsNegativeWords;	}
	public boolean isMessageSubjectContainsVerbTerms() {		return messageSubjectContainsVerbTerms;	}
	public void setMessageSubjectContainsVerbTerms(boolean messageSubjectContainsVerbTerms) {		this.messageSubjectContainsVerbTerms = messageSubjectContainsVerbTerms;	}
	public boolean isMessageSubjectContainsEntitiesTerms() {		return messageSubjectContainsEntitiesTerms;	}
	public void setMessageSubjectContainsEntitiesTerms(boolean messageSubjectContainsEntitiesTerms) {		this.messageSubjectContainsEntitiesTerms = messageSubjectContainsEntitiesTerms;	}
	public boolean isMessageSubjectContainsSpecialTerms() {		return messageSubjectContainsSpecialTerms;	}
	public void setMessageSubjectContainsSpecialTerms(boolean messageSubjectContainsSpecialTerms) {		this.messageSubjectContainsSpecialTerms = messageSubjectContainsSpecialTerms;	}
	public boolean isMessageSubjectContainsDecisionTerms() {		return messageSubjectContainsDecisionTerms;	}
	public void setMessageSubjectContainsDecisionTerms(boolean messageSubjectContainsDecisionTerms) {		this.messageSubjectContainsDecisionTerms = messageSubjectContainsDecisionTerms;	}
	
	
	
	public  double getRestOfParagraphProbabilityScore() {		return restOfParagraphProbabilityScore;	}
	public  void setRestOfParagraphProbabilityScore(double v_restOfParagraphProbabilityScore) {		restOfParagraphProbabilityScore = v_restOfParagraphProbabilityScore;
	}

	public String getMessageSubject() {		return messageSubject;	}
	public void setMessageSubject(String messageSubject) {		this.messageSubject = messageSubject;	}
	public boolean isMessageSubjectNegationFound() {		return messageSubjectNegationFound;	}
	public void setMessageSubjectNegationFound(boolean messageSubjectNegationFound) {		this.messageSubjectNegationFound = messageSubjectNegationFound;	}
	public double getReasonProbabilityScore() {		return reasonProbabilityScore;	}
	public void setReasonProbabilityScore(double reasonProbalilityScore) {		this.reasonProbabilityScore = reasonProbalilityScore;	}
	public double getSentenceOrParagraphHintProbablity() {		return sentenceOrParagraphHintProbablity;	}
	public void setSentenceOrParagraphHintProbablity(double sentenceOrParagraphHintProbablity) {		this.sentenceOrParagraphHintProbablity = sentenceOrParagraphHintProbablity;	}
	
	
	
	public boolean isSentenceOrParagraph_containsReasonsTerms() {		return sentenceOrParagraph_containsReasonsTerms;	}
	public void setSentenceOrParagraph_containsReasonsTerms(boolean sentenceOrParagraph_containsReasonsTerms) {		this.sentenceOrParagraph_containsReasonsTerms = sentenceOrParagraph_containsReasonsTerms;	}
	public boolean isSentenceOrParagraph_containsReasonsIdentifierTerms() {		return sentenceOrParagraph_containsReasonsIdentifierTerms;	}
	public void setSentenceOrParagraph_containsReasonsIdentifierTerms(			boolean sentenceOrParagraph_containsReasonsIdentifierTerms) {		this.sentenceOrParagraph_containsReasonsIdentifierTerms = sentenceOrParagraph_containsReasonsIdentifierTerms;	}
	public boolean isSentenceOrParagraph_containsStatesSubstates() {		return sentenceOrParagraph_containsStatesSubstates;	}
	public void setSentenceOrParagraph_containsStatesSubstates(boolean sentenceOrParagraph_containsStatesSubstates) {		this.sentenceOrParagraph_containsStatesSubstates = sentenceOrParagraph_containsStatesSubstates;	}
	public boolean isSentenceOrParagraph_containsIdentifierTermList() {		return sentenceOrParagraph_containsIdentifierTermList;	}
	public void setSentenceOrParagraph_containsIdentifierTermList(boolean sentenceOrParagraph_containsIdentifierTermList) {		this.sentenceOrParagraph_containsIdentifierTermList = sentenceOrParagraph_containsIdentifierTermList;	}
	public boolean isSentenceOrParagraph_containsPositiveWords() {		return sentenceOrParagraph_containsPositiveWords;	}
	public void setSentenceOrParagraph_containsPositiveWords(boolean sentenceOrParagraph_containsPositiveWords) {		this.sentenceOrParagraph_containsPositiveWords = sentenceOrParagraph_containsPositiveWords;	}
	public boolean isSentenceOrParagraph_containsNegativeWords() {		return sentenceOrParagraph_containsNegativeWords;	}
	public void setSentenceOrParagraph_containsNegativeWords(boolean sentenceOrParagraph_containsNegativeWords) {		this.sentenceOrParagraph_containsNegativeWords = sentenceOrParagraph_containsNegativeWords;	}
	public boolean isSentenceOrParagraph_containsVerbTerms() {		return sentenceOrParagraph_containsVerbTerms;	}
	public void setSentenceOrParagraph_containsVerbTerms(boolean sentenceOrParagraph_containsVerbTerms) {		this.sentenceOrParagraph_containsVerbTerms = sentenceOrParagraph_containsVerbTerms;	}
	public boolean isSentenceOrParagraph_containsIdentifierTerms() {		return sentenceOrParagraph_containsIdentifierTerms;	}
	public void setSentenceOrParagraph_containsIdentifierTerms(boolean sentenceOrParagraph_containsIdentifierTerms) {		this.sentenceOrParagraph_containsIdentifierTerms = sentenceOrParagraph_containsIdentifierTerms;	}
	public boolean isSentenceOrParagraph_containsEntitiesTerms() {		return sentenceOrParagraph_containsEntitiesTerms;	}
	public void setSentenceOrParagraph_containsEntitiesTerms(boolean sentenceOrParagraph_containsEntitiesTerms) {		this.sentenceOrParagraph_containsEntitiesTerms = sentenceOrParagraph_containsEntitiesTerms;	}
	public boolean isSentenceOrParagraph_containsSpecialTerms() {		return sentenceOrParagraph_containsSpecialTerms;	}
	public void setSentenceOrParagraph_containsSpecialTerms(boolean sentenceOrParagraph_containsSpecialTerms) {		this.sentenceOrParagraph_containsSpecialTerms = sentenceOrParagraph_containsSpecialTerms;	}
	public boolean isSentenceOrParagraph_containsSpecialTermsInMessageSubject() {		return sentenceOrParagraph_containsSpecialTermsInMessageSubject;	}
	public void setSentenceOrParagraph_containsSpecialTermsInMessageSubject(boolean sentenceOrParagraph_containsSpecialTermsInMessageSubject) {		this.sentenceOrParagraph_containsSpecialTermsInMessageSubject = sentenceOrParagraph_containsSpecialTermsInMessageSubject;	}
	public boolean isSentenceOrParagraph_containsNegationTermList() {		return sentenceOrParagraph_containsNegationTermList;	}
	public void setSentenceOrParagraph_containsNegationTermList(boolean sentenceOrParagraph_containsNegationTermList) {		this.sentenceOrParagraph_containsNegationTermList = sentenceOrParagraph_containsNegationTermList;	}
	public boolean isSentenceOrParagraph_containsDecisionTerms() {		return sentenceOrParagraph_containsDecisionTerms;	}
	public void setSentenceOrParagraph_containsDecisionTerms(boolean sentenceOrParagraph_containsDecisionTerms) {		this.sentenceOrParagraph_containsDecisionTerms = sentenceOrParagraph_containsDecisionTerms;	}
	
	
	
	public boolean isSameMessageAsState() {		return sameMessageAsState;	}
	public void setSameMessageAsState(boolean sameMessageAsState) {		this.sameMessageAsState = sameMessageAsState;	}
	public boolean isSameSentenceAsState() {		return sameSentenceAsState;	}
	public void setSameSentenceAsState(boolean sameSentenceAsState) {		this.sameSentenceAsState = sameSentenceAsState;	}	
	public boolean isNearbySentenceAsState() {		return nearbySentenceAsState;	}
	public void setNearbySentenceAsState(boolean nearbySentenceAsState) {		this.nearbySentenceAsState = nearbySentenceAsState;	}
	public boolean isSameParagrapghAsState() {		return sameParagrapghAsState;	}
	public void setSameParagrapghAsState(boolean sameParagrapghAsState) {		this.sameParagrapghAsState = sameParagrapghAsState;	}
	public  String getLabelledSentenceOrParagraph() {
		return labelledSentenceOrParagraph;
	}
		
	public  String getMessageSubjectStatesSubstatesList() {		return messageSubjectStatesSubstatesList;	}
	public  void setMessageSubjectStatesSubstatesList(String v_messageSubjectStatesSubstatesList) {		messageSubjectStatesSubstatesList = v_messageSubjectStatesSubstatesList;	}
	public  String getMessageSubjectDecisionTermList() {		return messageSubjectDecisionTermList;	}
	public  void setMessageSubjectDecisionTermList(String v_messageSubjectDecisionTermList) {		this.messageSubjectDecisionTermList = v_messageSubjectDecisionTermList;	}
	public  String getMessageSubjectVerbList() {		return messageSubjectVerbList;	}
	public  void setMessageSubjectVerbList(String v_messageSubjectVerbList) {		this.messageSubjectVerbList = v_messageSubjectVerbList;	}
	public  String getMessageSubjectProposalIdentifiersTermList() {		return messageSubjectProposalIdentifiersTermList;	}
	public  void setMessageSubjectProposalIdentifiersTermList(String v_messageSubjectProposalIdentifiersTermList) {		this.messageSubjectProposalIdentifiersTermList = v_messageSubjectProposalIdentifiersTermList;	}
	public  String getMessageSubjectEntitiesTermList() {		return messageSubjectEntitiesTermList;	}
	public  void setMessageSubjectEntitiesTermList(String v_messageSubjectEntitiesTermList) {		this.messageSubjectEntitiesTermList = v_messageSubjectEntitiesTermList;	}
	public  String getMessageSubjectSpecialTermList() {		return messageSubjectSpecialTermList;	}
	public  void setMessageSubjectSpecialTermList(String v_messageSubjectSpecialTermList) {		this.messageSubjectSpecialTermList = v_messageSubjectSpecialTermList;	}
	
	public  void setLabelledSentenceOrParagraph(String labelledSentenceOrParagraph) {		this.labelledSentenceOrParagraph = labelledSentenceOrParagraph;	}
	public  String getLabelledMessageSubject() {		return labelledMessageSubject;	}
	public  void setLabelledMessageSubject(String labelledMessageSubject) {		this.labelledMessageSubject = labelledMessageSubject;	}
	public  String getReasonsTermsFoundList() {		return reasonsTermsFoundList;	}
	public  void setReasonsTermsFoundList(String reasonsTermsFoundList) {		this.reasonsTermsFoundList = reasonsTermsFoundList;	}
	public  String getReasonsIdentifierTermsList() {		return reasonsIdentifierTermsList;	}
	public  void setReasonsIdentifierTermsList(String reasonsIdentifierTermsList) {		this.reasonsIdentifierTermsList = reasonsIdentifierTermsList;	}
	public  String getStatesSubstatesList() {		return statesSubstatesList;	}
	public  void setStatesSubstatesList(String statesSubstatesList) {		this.statesSubstatesList = statesSubstatesList;	}
	public  String getIdentifiersTermList() {		return identifiersTermList;	}
	public  void setIdentifiersTermList(String identifiersTermList) {		this.identifiersTermList = identifiersTermList;	}
	public  String getEntitiesTermList() {		return entitiesTermList;	}
	public  void setEntitiesTermList(String entitiesTermList) {		this.entitiesTermList = entitiesTermList;	}
	public  String getSpecialTermList() {		return specialTermList;	}
	public  void setSpecialTermList(String specialTermList) {		this.specialTermList = specialTermList;	}
	public  String getNegationTermList() {		return negationTermList;	}
	public  void setNegationTermList(String negationTermList) {		this.negationTermList = negationTermList;	}
	public  String getPositiveWordList() {		return positiveWordList;	}
	public  void setPositiveWordList(String positiveWordList) {		this.positiveWordList = positiveWordList;	}
	public  String getNegativeWordList() {		return negativeWordList;	}
	public  void setNegativeWordList(String negativeWordList) {		this.negativeWordList = negativeWordList;	}
	public double getV_termsLocationWithMessageProbabilityScore() {		return v_termsLocationWithMessageProbabilityScore;	}
	public void setV_termsLocationWithMessageProbabilityScore(double v_termsLocationWithMessageProbabilityScore) {		this.v_termsLocationWithMessageProbabilityScore = v_termsLocationWithMessageProbabilityScore;	}
	public  void setProposalNum(Integer proposalNum) {		this.proposalNum = proposalNum;	}
	public  void setMid(Integer mid) {		this.mid = mid;	}
	public  void setDateDiff(Integer dateDiff) {		this.dateDiff = dateDiff;	}	
		
	public  String getDecisionTermList() {		return decisionTermList;	}
	public  void setDecisionTermList(String v_decisionTermList) {		this.decisionTermList = v_decisionTermList;	}
		
	// counts for learning
	//message subject level

	public  Integer getMessageSubjectStatesSubstatesListCount() {		return messageSubjectStatesSubstatesListCount;	}
	public  void setMessageSubjectStatesSubstatesListCount(Integer messageSubjectStatesSubstatesListCount) {		this.messageSubjectStatesSubstatesListCount = messageSubjectStatesSubstatesListCount;	}
	public  Integer getMessageSubjectDecisionTermListCount() {		return messageSubjectDecisionTermListCount;	}
	public  void setMessageSubjectDecisionTermListCount(Integer messageSubjectDecisionTermListCount) {		this.messageSubjectDecisionTermListCount = messageSubjectDecisionTermListCount;	}
	public  Integer getMessageSubjectVerbListCount() {		return messageSubjectVerbListCount;	}
	public  void setMessageSubjectVerbListCount(Integer messageSubjectVerbListCount) {		this.messageSubjectVerbListCount = messageSubjectVerbListCount;	}
	public  Integer getMessageSubjectProposalIdentifiersTermListCount() {		return messageSubjectProposalIdentifiersTermListCount;	}
	public  void setMessageSubjectProposalIdentifiersTermListCount(			Integer messageSubjectProposalIdentifiersTermListCount) {		this.messageSubjectProposalIdentifiersTermListCount = messageSubjectProposalIdentifiersTermListCount;	}	
	public  Integer getMessageSubjectEntitiesTermListCount() {		return messageSubjectEntitiesTermListCount;	}
	public  void setMessageSubjectEntitiesTermListCount(Integer messageSubjectEntitiesTermListCount) {		this.messageSubjectEntitiesTermListCount = messageSubjectEntitiesTermListCount;	}
	public  Integer getMessageSubjectSpecialTermListCount() {		return messageSubjectSpecialTermListCount;	}
	public  void setMessageSubjectSpecialTermListCount(Integer messageSubjectSpecialTermListCount) {		this.messageSubjectSpecialTermListCount = messageSubjectSpecialTermListCount;	}
	
	// sentence or paragraph level	
	public Integer getReasonsTermsFoundCount() {		return reasonsTermsFoundCount;	}
	public  void setReasonsTermsFoundCount(Integer reasonsTermsFoundCount) {		this.reasonsTermsFoundCount = reasonsTermsFoundCount;	}
	public  Integer getReasonsIdentifierTermsCount() {		return reasonsIdentifierTermsCount;	}
	public  void setReasonsIdentifierTermsCount(Integer reasonsIdentifierTermsCount) {		this.reasonsIdentifierTermsCount = reasonsIdentifierTermsCount;	}
	public  Integer getStatesSubstatesCount() {		return statesSubstatesCount;	}
	public  void setStatesSubstatesCount(Integer statesSubstatesCount) {		this.statesSubstatesCount = statesSubstatesCount;	}
	public  Integer getIdentifiersTermCount() {		return identifiersTermCount;	}
	public  void setIdentifiersTermCount(Integer identifiersTermCount) {		this.identifiersTermCount = identifiersTermCount;	}
	public  Integer getEntitiesTermCount() {		return entitiesTermCount;	}
	public  void setEntitiesTermCount(Integer entitiesTermCount) {		this.entitiesTermCount = entitiesTermCount;	}
	public  Integer getSpecialTermCount() {		return specialTermCount;	}
	public  void setSpecialTermCount(Integer specialTermCount) {		this.specialTermCount = specialTermCount;	}
	public  Integer getDecisionTermCount() {		return decisionTermCount;	}
	public  void setDecisionTermCount(Integer decisionTermCount) {		this.decisionTermCount = decisionTermCount;	}
	public  Integer getNegationTermCount() {		return negationTermCount;	}
	public  void setNegationTermCount(Integer negationTermCount) {		this.negationTermCount = negationTermCount;	}
	public  Integer getPositiveWordCount() {		return positiveWordCount;	}
	public  void setPositiveWordCount(Integer positiveWordCount) {		this.positiveWordCount = positiveWordCount;	}
	public  Integer getNegativeWordCount() {		return negativeWordCount;	}
	public  void setNegativeWordCount(Integer negativeWordCount) {		this.negativeWordCount = negativeWordCount;	}
	
	public  Integer getDateDiffWeight() {		return dateDiffWeight;	}
	public  void setDateDiffWeight(Integer dateDiffWeight) {		this.dateDiffWeight = dateDiffWeight;	}
	public  Integer getMessageAuthorRoleWeight() {		return messageAuthorRoleWeight;	}
	public  void setMessageAuthorRoleWeight(Integer messageAuthorRoleWeight) {		this.messageAuthorRoleWeight = messageAuthorRoleWeight;	}
	
	public  boolean isWriteToFile() {		return writeToFile;	}
	public void setWriteToFile(boolean writeToFile) {		this.writeToFile = writeToFile;	}
	
	public boolean isEnglishOrCode() {		return isEnglishOrCode;	}
	public void setEnglishOrCode(boolean isEnglishOrCode) {		this.isEnglishOrCode = isEnglishOrCode;	}
	
	public String getFirstParagraph() {		return firstParagraph;	}
	public void setFirstParagraph(String firstParagraph) {		this.firstParagraph = firstParagraph;	}
	public String getLastParagraph() {		return lastParagraph;	}
	public void setLastParagraph(String lastParagraph) {		this.lastParagraph = lastParagraph;	}
	//message type
	public  boolean isMesssageTypeIsReasonMessage() {		return messsageTypeIsReasonMessage;	}
	public  void setMesssageTypeIsReasonMessage(boolean messsageTypeIsReasonMessage) {		this.messsageTypeIsReasonMessage = messsageTypeIsReasonMessage;	}
	public  double getMesssageTypeIsReasonMessageProbabilityScore() {		return messsageTypeIsReasonMessageProbabilityScore;	}
	public  void setMesssageTypeIsReasonMessageProbabilityScore(double v_messsageTypeIsReasonMessageProbabilityScore) {		this.messsageTypeIsReasonMessageProbabilityScore = v_messsageTypeIsReasonMessageProbabilityScore;	}	
	//paragraph location
	public  boolean isFirstParagraph() {		return isFirstParagraph;	}
	public  void setIsFirstParagraph(boolean isFirstParagraph) {		this.isFirstParagraph = isFirstParagraph;	}
	public  boolean isLastParagraph() {		return isLastParagraph;	}
	public  void setIsLastParagraph(boolean isLastParagraph) {		this.isLastParagraph = isLastParagraph;	}
	public  boolean isSameMsgSubAsStateTriple() {		return sameMsgSubAsStateTriple;	}
	public  void setSameMsgSubAsStateTriple(boolean sameMsgSubAsStateTriple) {		this.sameMsgSubAsStateTriple = sameMsgSubAsStateTriple;	}
	//sentence location	
	public double getSentenceLocationInMessageProbabilityScore() {		return sentenceLocationInMessageProbabilityScore;	}
	public void setSentenceLocationInMessageProbabilityScore(double sentenceLocationInMessageProbabilityScore) {this.sentenceLocationInMessageProbabilityScore = sentenceLocationInMessageProbabilityScore;	}
	public double getSameMsgSubAsStateTripleProbabilityScore() {		return sameMsgSubAsStateTripleProbabilityScore;	}
	public void setSameMsgSubAsStateTripleProbabilityScore(double sameMsgSubAsStateTripleProbabilityScore) {	this.sameMsgSubAsStateTripleProbabilityScore = sameMsgSubAsStateTripleProbabilityScore;	}	
	public String getMessageType() {		return messageType;	}
	public void setMessageType(String messageType) {		this.messageType = messageType;	}
	
	//feb 2019
	public  boolean isReasonLabelFoundUsingTripleExtraction() {		return reasonLabelFoundUsingTripleExtraction;	}
	public  void setReasonLabelFoundUsingTripleExtraction(boolean v_reasonLabelFoundUsingTripleExtraction) {		reasonLabelFoundUsingTripleExtraction = v_reasonLabelFoundUsingTripleExtraction;	}
	public  double getReasonLabelFoundUsingTripleExtractionProbabilityScore() {		return reasonLabelFoundUsingTripleExtractionProbabilityScore;	}
	public  void setReasonLabelFoundUsingTripleExtractionProbabilityScore( double v_reasonLabelFoundUsingTripleExtractionProbabilityScore) {
		reasonLabelFoundUsingTripleExtractionProbabilityScore = v_reasonLabelFoundUsingTripleExtractionProbabilityScore;
	}	
	public  boolean messageContainsSpecialTerm() {		return messageContainsSpecialTerm;	}
	public  void setMessageContainsSpecialTerm(boolean v_messageContainsSpecialTerm) {		messageContainsSpecialTerm = v_messageContainsSpecialTerm;	}
	public  double getMessageContainsSpecialTermProbabilityScore() {		return messageContainsSpecialTermProbabilityScore;	}
	public  void setMessageContainsSpecialTermProbabilityScore(double v_messageContainsSpecialTermProbabilityScore) {
		messageContainsSpecialTermProbabilityScore = v_messageContainsSpecialTermProbabilityScore;
	}
	public  boolean getPrevParagraph_containsSpecialTerms() {		return prevParagraph_containsSpecialTerms;	}
	public  void setPrevParagraph_containsSpecialTerms(boolean v_prevParagraph_containsSpecialTerms) {		prevParagraph_containsSpecialTerms = v_prevParagraph_containsSpecialTerms;	}
	
	public  double getPrevParagraphSpecialTermProbabilityScore() {	return prevParagraphSpecialTermProbabilityScore;	}
	public  void setPrevParagraphSpecialTermProbabilityScore(double v_prevParagraphSpecialTermProbabilityScore) {	
		prevParagraphSpecialTermProbabilityScore = v_prevParagraphSpecialTermProbabilityScore;
	}	
	public  String getPrevParagraph_specialTermList() {		return prevParagraph_specialTermList;	}
	public  void setPrevParagraph_specialTermList(String v_prevParagraph_specialTermList) {		prevParagraph_specialTermList = v_prevParagraph_specialTermList;	}
	
	
	public  Integer getPrevParagraphSpecialTermsCount() {		return prevParagraphSpecialTermsCount;	}
	public  void setPrevParagraphSpecialTermsCount(Integer v_prevParagraphSpecialTermsCount) {		prevParagraphSpecialTermsCount = v_prevParagraphSpecialTermsCount;	}
	
	//march 2019
	
	
	public  boolean getActermsfound() {		return actermsfound;	}
	public  double getTotalProbabilityScore() {		return totalProbabilityScore;	}
	public  void setTotalProbabilityScore(double v_totalProbabilityScore) {		totalProbabilityScore = v_totalProbabilityScore;	}
	public  void setActermsfound(boolean v_actermsfound) {		actermsfound = v_actermsfound;	}

	//march 2019, dynamic weight allocation
	
	public double getORIGsentenceHintProbablity() {		return ORIGsentenceHintProbablity;	}
	public void setORIGsentenceHintProbablity(double oRIGsentenceHintProbablity) {		ORIGsentenceHintProbablity = oRIGsentenceHintProbablity;	}
	public double getORIGsentenceLocationHintProbability() {		return ORIGsentenceLocationHintProbability;	}
	public void setORIGsentenceLocationHintProbability(double oRIGsentenceLocationHintProbability) {		ORIGsentenceLocationHintProbability = oRIGsentenceLocationHintProbability;	}
	public double getORIGmessageSubjectHintProbablityScore() {		return ORIGmessageSubjectHintProbablityScore;	}
	public void setORIGmessageSubjectHintProbablityScore(double oRIGmessageSubjectHintProbablityScore) {		ORIGmessageSubjectHintProbablityScore = oRIGmessageSubjectHintProbablityScore;	}
	public double getORIGdateDiffProbability() {		return ORIGdateDiffProbability;	}
	public void setORIGdateDiffProbability(double oRIGdateDiffProbability) {		ORIGdateDiffProbability = oRIGdateDiffProbability;	}
	public double getORIGauthorRoleProbability() {		return ORIGauthorRoleProbability;	}
	public void setORIGauthorRoleProbability(double oRIGauthorRoleProbability) {		ORIGauthorRoleProbability = oRIGauthorRoleProbability;	}
	public double getORIGnegationTermPenalty() {		return ORIGnegationTermPenalty;	}
	public void setORIGnegationTermPenalty(double oRIGnegationTermPenalty) {		ORIGnegationTermPenalty = oRIGnegationTermPenalty;	}
	public double getORIGrestOfParagraphProbabilityScore() {		return ORIGrestOfParagraphProbabilityScore;	}
	public void setORIGrestOfParagraphProbabilityScore(double oRIGrestOfParagraphProbabilityScore) {		ORIGrestOfParagraphProbabilityScore = oRIGrestOfParagraphProbabilityScore;	}
	public double getORIGmessageLocationProbabilityScore() {		return ORIGmessageLocationProbabilityScore;	}
	public void setORIGmessageLocationProbabilityScore(double oRIGmessageLocationProbabilityScore) {		ORIGmessageLocationProbabilityScore = oRIGmessageLocationProbabilityScore;	}
	public double getORIGmessageTypeIsReasonMessageProbabilityScore() {		return ORIGmessageTypeIsReasonMessageProbabilityScore;	}
	public void setORIGmessageTypeIsReasonMessageProbabilityScore(double oRIGmessageTypeIsReasonMessageProbabilityScore) {	
		ORIGmessageTypeIsReasonMessageProbabilityScore = oRIGmessageTypeIsReasonMessageProbabilityScore;
	}
	public double getORIGsentenceLocationInMessageProbabilityScore() {		return ORIGsentenceLocationInMessageProbabilityScore;	}
	public void setORIGsentenceLocationInMessageProbabilityScore(double oRIGsentenceLocationInMessageProbabilityScore) {
		ORIGsentenceLocationInMessageProbabilityScore = oRIGsentenceLocationInMessageProbabilityScore;
	}
	public double getORIGsameMsgSubAsStateTripleProbabilityScore() {		return ORIGsameMsgSubAsStateTripleProbabilityScore;	}
	public void setORIGsameMsgSubAsStateTripleProbabilityScore(double oRIGsameMsgSubAsStateTripleProbabilityScore) {
		ORIGsameMsgSubAsStateTripleProbabilityScore = oRIGsameMsgSubAsStateTripleProbabilityScore;
	}
	public double getORIGreasonLabelFoundUsingTripleExtractionProbabilityScore() {
		return ORIGreasonLabelFoundUsingTripleExtractionProbabilityScore;
	}
	public void setORIGreasonLabelFoundUsingTripleExtractionProbabilityScore(
			double oRIGreasonLabelFoundUsingTripleExtractionProbabilityScore) {
		ORIGreasonLabelFoundUsingTripleExtractionProbabilityScore = oRIGreasonLabelFoundUsingTripleExtractionProbabilityScore;
	}
	public double getORIGmessageContainsSpecialTermProbabilityScore() {		return ORIGmessageContainsSpecialTermProbabilityScore;	}
	public void setORIGmessageContainsSpecialTermProbabilityScore(double oRIGmessageContainsSpecialTermProbabilityScore) {
		ORIGmessageContainsSpecialTermProbabilityScore = oRIGmessageContainsSpecialTermProbabilityScore;
	}
	public double getORIGprevParagraphSpecialTermProbabilityScore() {		return ORIGprevParagraphSpecialTermProbabilityScore;	}
	public void setORIGprevParagraphSpecialTermProbabilityScore(double oRIGprevParagraphSpecialTermProbabilityScore) {
		ORIGprevParagraphSpecialTermProbabilityScore = oRIGprevParagraphSpecialTermProbabilityScore;
	}
	
	//DEC 2019
	public double getOrigTotalProbabilityScore() {		return origTotalProbabilityScore;	}
	public void setOrigTotalProbabilityScore(double v_origTotalProbabilityScore) {
		origTotalProbabilityScore = v_origTotalProbabilityScore;
	}
	
	//april 2019, ranking evaluation metrics
	
	public int getRank() {		return rank;	}
	public int getMatched() {		return matched;	}
	public void setMatched(int matched) {		this.matched = matched;	}
	public void setRank(int rank) {		this.rank = rank;	}
	public float getPrecision() {		return precision;	}
	public void setPrecision(float precision) {	this.precision = precision;	}
	public float getRecall() {		return recall;	}
	public void setRecall(float recall) {		this.recall = recall;	}
	
	public int getRankByTotalProbability() {		return rankByTotalProbability;	}
	public void setRankByTotalProbability(int v_rankByTotalProbability) {		this.rankByTotalProbability = v_rankByTotalProbability;	}

	public float getDiscountedCumulatioveGain() {		return discountedCumulatioveGain;	}
	public void setDiscountedCumulatioveGain(float v_discountedCumulatioveGain) {		discountedCumulatioveGain = v_discountedCumulatioveGain;	}
	public float getSum_discountedCumulatioveGain() {		return sum_discountedCumulatioveGain;	}
	public void setSum_discountedCumulatioveGain(float v_sum_discountedCumulatioveGain) {		sum_discountedCumulatioveGain = v_sum_discountedCumulatioveGain;  	}

	@Override
	public void finalize() {	    //System.out.println("Probability instance is getting destroyed");
		//this.dispose();
		// Invoke the finalizer of our superclass
		// We haven't discussed superclasses or this syntax yet
		  try {
			super.finalize();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
}
