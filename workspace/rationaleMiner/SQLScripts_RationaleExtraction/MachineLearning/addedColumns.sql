select pep, label, causesentence, effectsentence from trainingdata order by label asc, pep;
where pep = 3151 and causesentence like '%bdfop%';
select * from autoextractedreasoncandidatesentences
where proposal = 3151 and sentence like '%bdfop%';
delete from trainingdata where pep = 3144 and LENGTH(causesentence) =0;

select sentence, entireparagraph, entireparagraphminuscurrsentence from autoextractedreasoncandidatesentences
where location like '%sentence%';

select 
-- proposal, dateValue, messageID, sentence,isenglishorcode,termsMatched,probability,label, author,location,
-- messageSubjectHintProbablityScore,sentenceHintProbablity,sentenceLocationHintProbability,negationTermPenalty,dateDiffProbability, 
-- authorRoleProbability,messageLocationProbabilityScore, datediffWeight,authorRoleWeight, 
-- messageSubjectStatesSubstatesListCount,messageSubjectDecisionTermListCount,messageSubjectProposalIdentifiersTermListCount,messageSubjectEntitiesTermListCount, 
-- messageSubjectSpecialTermListCount, 
-- currSentenceReasonsTermsFoundCount,currSentenceReasonsIdentifierTermsCount,currSentenceStatesSubstatesCount,currSentenceIdentifiersTermCount,
-- currSentenceEntitiesTermCount,currSentenceSpecialTermCount,currSentenceDecisionTermCount,dateDiff,		      		
-- currSentenceReasonsTermsFoundList, currSentenceReasonsIdentifierTermsList, currSentenceStatesSubstatesList, currSentenceIdentifiersTermList, currSentenceEntitiesTermList,"
-- currSentenceSpecialTermList, currSentenceDecisionTermList, currSentenceNegationTermList,
-- messageSubjectStatesSubstatesList, messageSubjectDecisionTermList, messageSubjectVerbList, 
-- messageSubjectIdentifiersTermList, messageSubjectEntitiesTermList, messageSubjectSpecialTermList,
-- entireParagraph, restOfParagraph, " 
restOfParagraphStateTermList,restOfParagraphIdentifiersTermList, restOfParagraphEntityTermList, restOfParagraphSpecialTermList, restOfParagraphDecisionTermList, 
restOfParagraphStateTermsCount, restOfParagraphIdentifiersTermsCount, restOfParagraphEntityTermsCount,restOfParagraphDecisionTermsCount, restOfParagraphSpecialTermsCount 
-- dateOfStateMessage, messageSubject, containsReason
from autoextractedreasoncandidatesentences;

-- just to check that, for'accepted' label, the code only checks for 'accepted' related state terms
select label, currSentenceStatesSubstatesList from autoextractedreasoncandidatesentences; 

-- checking of count is true
select restOfParagraphEntityTermList, restOfParagraphEntityTermsCount from autoextractedreasoncandidatesentences where LENGTH(restOfParagraphEntityTermList) >0;
select restOfParagraphStateTermList, restOfParagraphStateTermsCount from autoextractedreasoncandidatesentences where LENGTH(restOfParagraphStateTermList) >0;
select restOfParagraphIdentifiersTermList, restOfParagraphIdentifiersTermsCount from autoextractedreasoncandidatesentences where LENGTH(restOfParagraphIdentifiersTermList) >0;
select restOfParagraphSpecialTermList, restOfParagraphSpecialTermsCount from autoextractedreasoncandidatesentences where LENGTH(restOfParagraphSpecialTermList) >0;
select restOfParagraphDecisionTermList, restOfParagraphDecisionTermsCount from autoextractedreasoncandidatesentences where LENGTH(restOfParagraphDecisionTermList) >0;
select restOfParagraphReasonsTermsList, restOfParagraphReasonsTermsCount from autoextractedreasoncandidatesentences where restOfParagraphReasonsTermsCount>0 ; -- LENGTH(restOfParagraphReasonsTermsList) >0;

select distinct restOfParagraphEntityTermsCount 	from autoextractedreasoncandidatesentences;
select distinct restOfParagraphStateTermsCount 		from autoextractedreasoncandidatesentences;
select distinct restOfParagraphIdentifiersTermsCount from autoextractedreasoncandidatesentences;
select distinct restOfParagraphSpecialTermsCount 	from autoextractedreasoncandidatesentences;
select distinct restOfParagraphDecisionTermsCount 	from autoextractedreasoncandidatesentences;
select distinct restOfParagraphReasonsTermsCount 	from autoextractedreasoncandidatesentences;