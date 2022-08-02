select * from autoextractedreasoncandidatesentences
where LENGTH(messageSubjectStatesSubstatesList) > 0
OR LENGTH(messageSubjectDecisionTermList) > 0
OR LENGTH(messageSubjectVerbList) > 0
OR LENGTH(messageSubjectIdentifiersTermList) > 0
OR LENGTH(messageSubjectEntitiesTermList) > 0
OR LENGTH(messageSubjectSpecialTermList) > 0

select distinct(proposal) from autoextractedreasoncandidatesentences;
delete from autoextractedreasoncandidatesentences
where proposal = 214;

select * from autoextractedreasoncandidatesentences where containsreason=1;
select author from autoextractedreasoncandidatesentences where containsreason=1;
select mean(datediff) from autoextractedreasoncandidatesentences where containsreason=1;

SELECT datediff, COUNT(datediff) AS occurence
-- FROM autoextractedreasoncandidatesentences 
from autoextractedreasoncandidatesentences_improved_beststats
where containsreason=1
GROUP BY  datediff;


SELECT author , datediff, COUNT(datediff) AS occurence
-- FROM autoextractedreasoncandidatesentences 
from autoextractedreasoncandidatesentences_improved_beststats
where containsreason=1
GROUP BY author, datediff;


SELECT proposal,messageid,dateValue, sentence,termsMatched, probability, label, author, location, 
(sentenceHintProbablity+sentenceLocationHintProbability+messageLocationProbabilityScore+messageSubjectHintProbablityScore+dateDiffProbability+authorRoleProbability
-negationTermPenalty) as TotalProbability,
sentenceHintProbablity,sentenceLocationHintProbability,messageLocationProbabilityScore,messageSubjectHintProbablityScore,negationTermPenalty,dateDiffProbability,authorRoleProbability
from autoextractedreasoncandidatesentences where -- sentence like '%after a long%' and
proposal = 214 order by label asc, TotalProbability desc, dateValue desc;

select proposal,messageid, messagesubject, sentence, termsmatched, label, location , 
(sentenceHintProbablity+sentenceLocationHintProbability+messageLocationProbabilityScore+messageSubjectHintProbablityScore+dateDiffProbability+authorRoleProbability
-negationTermPenalty) as TotalProbability,
messageSubjectStatesSubstatesList, messageSubjectDecisionTermList as msDecision,messageSubjectIdentifiersTermList as msIdentifier, 
messageSubjectEntitiesTermList as msEntities, messageSubjectSpecialTermList as msSpecial,
reasonsTermsFoundList as reason, reasonsIdentifierTermsList as reasonIdent,statesSubstatesList as states,identifiersTermList as ident,entitiesTermList as entities,
specialTermList as special, decisionTermList as decision
FROM autoextractedreasoncandidatesentences 
where proposal = 214 
-- containsreason=1
order by label asc, TotalProbability desc, dateValue desc;
