SELECT id, @curRank := @curRank + 1 AS rank, proposal,messageid,dateValue, sentence as text,termsMatched, probability, label, authorRole, location,datediff, messageTypeIsReasonMessage as RM, messageType as MT, 
(sentenceHintProbablity+sentenceLocationHintProbability+restOfParagraphProbabilityScore+messageLocationProbabilityScore+messageSubjectHintProbablityScore+dateDiffProbability+authorRoleProbability+
+messageTypeIsReasonMessageProbabilityScore+sentenceLocationInMessageProbabilityScore+sameMsgSubAsStateTripleProbabilityScore
 +reasonLabelFoundUsingTripleExtractionProbabilityScore+messageContainsSpecialTermProbabilityScore+prevParagraphSpecialTermProbabilityScore-negationTermPenalty) as TotalProbability, 
-- + "  sentenceHintProbablity,sentenceLocationHintProbability,messageSubjectHintProbablityScore,negationTermPenalty,dateDiffProbability,authorRoleProbability,restOfParagraphProbabilityScore "
sentenceHintProbablity,sentenceLocationHintProbability,restOfParagraphProbabilityScore,messageLocationProbabilityScore,messageSubjectHintProbablityScore,dateDiffProbability,authorRoleProbability, 
messageTypeIsReasonMessageProbabilityScore,sentenceLocationInMessageProbabilityScore,sameMsgSubAsStateTripleProbabilityScore, 
reasonLabelFoundUsingTripleExtractionProbabilityScore,messageContainsSpecialTermProbabilityScore,prevParagraphSpecialTermProbabilityScore,negationTermPenalty, 
								
ORIGsentenceHintProbablity, ORIGmessageSubjectHintProbablityScore, ORIGsentenceLocationHintProbability, ORIGdateDiffProbability,  
ORIGnegationTermPenalty,ORIGauthorRoleProbability,ORIGrestOfParagraphProbabilityScore, ORIGmessageLocationProbabilityScore,  
ORIGmessageTypeIsReasonMessageProbabilityScore, ORIGsentenceLocationInMessageProbabilityScore,  
ORIGsameMsgSubAsStateTripleProbabilityScore, ORIGreasonLabelFoundUsingTripleExtractionProbabilityScore,   
ORIGmessageContainsSpecialTermProbabilityScore, ORIGprevParagraphSpecialTermProbabilityScore 
								
FROM autoextractedreasoncandidatesentences_nodupsranked_sent, (SELECT @curRank := 0) r  
WHERE location = 'sentence' 
AND proposal = 391 AND label = 'ACCEPTED' 
ORDER	by proposal, label asc, TotalProbability desc, dateValue DESC

SELECT * FROM autoextractedreasoncandidatesentences_nodupsranked_msg
WHERE proposal = 391 
AND label = 'accepted' 
and location = 'sentence' 

SELECT * FROM pepstates_danieldata_datetimestamp WHERE pep = 349

SELECT * FROM autoextractedreasoncandidatesentences  
WHERE proposal = 349 
AND label = 'accepted' 
and location = 'sentence' 


SELECT proposal, COUNT(messageid) FROM autoextractedreasoncandidatesentences_nodupsranked_msg
GROUP BY proposal
SELECT proposal, COUNT(messageid) FROM autoextractedreasoncandidatesentences
GROUP BY proposal
SELECT * FROM autoextractedreasoncandidatesentences_nodupsranked_sent


SELECT distinct label from autoextractedreasoncandidatesentences
WHERE proposal = 391

