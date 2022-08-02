-- Sentence level
-- select rank, proposal, messageid from (
SELECT @curRank := @curRank + 1 AS rank, messageTypeIsReasonMessage as RM,messageType as MT, proposal,messageid,dateValue,datediff, sentence,termsMatched, 
(sentenceHintProbablity+sentenceLocationHintProbability+restOfParagraphProbabilityScore+messageLocationProbabilityScore+messageSubjectHintProbablityScore
+dateDiffProbability+authorRoleProbability+messageTypeIsReasonMessageProbabilityScore+sentenceLocationInMessageProbabilityScore+sameMsgSubAsStateTripleProbabilityScore
+reasonLabelFoundUsingTripleExtractionProbabilityScore+messageContainsSpecialTermProbabilityScore+prevParagraphSpecialTermProbabilityScore-negationTermPenalty) as TotalProbability, 
label, authorRole, location,
sentenceHintProbablity,sentenceLocationHintProbability,restOfParagraphProbabilityScore,messageTypeIsReasonMessageProbabilityScore,messageLocationProbabilityScore,
messageSubjectHintProbablityScore,negationTermPenalty,dateDiffProbability,authorRoleProbability,sentenceLocationInMessageProbabilityScore,
sameMsgSubAsStateTripleProbabilityScore, reasonLabelFoundUsingTripleExtractionProbabilityScore,messageContainsSpecialTermProbabilityScore
from autoextractedreasoncandidatesentences, (SELECT @curRank := 0) r 
where location = 'sentence'  and -- messageid = 2074922 and
 label = 'accepted'  and  -- sentence like '% seemed generally%' and
proposal = 285 -- and messsageTypeIsReasonMessage =1; 
-- and sentence like '%problems%' 
-- group by sentence -- messageid,  -- jan 2020 ... this is necesary to remove duplicate sentences
order by label asc, TotalProbability desc, dateValue DESC

SELECT * 
FROM autoextractedreasoncandidatesentences
WHERE proposal = 3127

-- -- feb 2020 Message Level, we use subquery to get only the unique messages
-- march 2019. message level based on sentence level - mostly same as above 

SELECT * FROM 
(
	SELECT @curRank := @curRank + 1 AS rank, messageTypeIsReasonMessage as RM,messageType as MT, proposal,messageid,dateValue,datediff, sentence,termsMatched, 
	(select analysewords from allmessages w where w.messageid = x.messageid limit 1) as message, 
	(sentenceHintProbablity+sentenceLocationHintProbability+restOfParagraphProbabilityScore+messageLocationProbabilityScore+messageSubjectHintProbablityScore
	+dateDiffProbability+authorRoleProbability+messageTypeIsReasonMessageProbabilityScore+sentenceLocationInMessageProbabilityScore+sameMsgSubAsStateTripleProbabilityScore
	+reasonLabelFoundUsingTripleExtractionProbabilityScore+messageContainsSpecialTermProbabilityScore+prevParagraphSpecialTermProbabilityScore-negationTermPenalty) as TotalProbability, 
	label, authorRole, location,
	sentenceHintProbablity,sentenceLocationHintProbability,restOfParagraphProbabilityScore,messageTypeIsReasonMessageProbabilityScore,messageLocationProbabilityScore,
	messageSubjectHintProbablityScore,negationTermPenalty,dateDiffProbability,authorRoleProbability,sentenceLocationInMessageProbabilityScore,
	sameMsgSubAsStateTripleProbabilityScore, reasonLabelFoundUsingTripleExtractionProbabilityScore,messageContainsSpecialTermProbabilityScore
	from autoextractedreasoncandidatesentences x, (SELECT @curRank := 0) r 
	where location = 'sentence'  
	and -- messageid = 2074922 and
	 label = 'accepted'  and  -- sentence like '% seemed generally%' and
	proposal = 3131 -- and messsageTypeIsReasonMessage =1; 
	GROUP BY sentence	-- jan 2020 ... this is necesary to remove duplicate sentences
	order by label asc, TotalProbability desc, dateValue desc
)  AS messages
GROUP BY messages.messageid
order by label asc, TotalProbability desc, dateValue desc;