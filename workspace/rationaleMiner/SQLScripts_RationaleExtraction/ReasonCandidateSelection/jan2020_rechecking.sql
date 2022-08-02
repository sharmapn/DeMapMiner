SELECT proposal,messageid,dateValue,datediff, sentence,  label, location, termsMatched, messageTypeIsReasonMessage,messageType, 
sentenceHintProbablity,sentenceLocationHintProbability,restOfParagraphProbabilityScore,messageLocationProbabilityScore,messageSubjectHintProbablityScore, 
dateDiffProbability,authorRoleProbability,messageTypeIsReasonMessageProbabilityScore,sentenceLocationInMessageProbabilityScore,sameMsgSubAsStateTripleProbabilityScore, 
reasonLabelFoundUsingTripleExtractionProbabilityScore,messageContainsSpecialTermProbabilityScore,prevParagraphSpecialTermProbabilityScore,negationTermPenalty, 
								
ORIGsentenceHintProbablity,ORIGsentenceLocationHintProbability,ORIGmessageSubjectHintProbablityScore,ORIGdateDiffProbability,ORIGauthorRoleProbability,
ORIGnegationTermPenalty,ORIGrestOfParagraphProbabilityScore,ORIGmessageLocationProbabilityScore,
ORIGmessageTypeIsReasonMessageProbabilityScore,ORIGsentenceLocationInMessageProbabilityScore,ORIGsameMsgSubAsStateTripleProbabilityScore,
ORIGreasonLabelFoundUsingTripleExtractionProbabilityScore,ORIGmessageContainsSpecialTermProbabilityScore,ORIGprevParagraphSpecialTermProbabilityScore 
								
FROM autoextractedreasoncandidatesentences_nodupsranked_msg 
WHERE proposal = 391 and label = 'accepted' 
AND location = 'sentence' 
GROUP by sentence 
ORDER by TotalProbability desc, dateValue DESC

DELETE FROM autoextractedreasoncandidatesentences_nodupsranked_sent WHERE proposal = 391;
DELETE FROM autoextractedreasoncandidatesentences_nodupsranked_msg WHERE proposal = 391;


SELECT * FROM autoextractedreasoncandidatesentences_nodupsranked_sent 
WHERE proposal = 391 

DELETE FROM dynamicweightallocation  WHERE run > 6
-- BETWEEN 72 AND 90

SELECT  run, RIGHT(evalLevel,8), label, 
top5, top10, top15, top30, top50, top100, outsidetop100, notfound, totalprobability,
sentenceHintProbablity as shp, sentenceLocationInMessageProbabilityScore as slimps, dateDiffProbability as ddp,
-- sentenceLocationHintProbability as slhp, not needed - not useful as remains constant
restOfParagraphProbabilityScore as ropps, messageLocationProbabilityScore as mlps, 
messageSubjectHintProbablityScore as mshps, authorRoleProbability as arp, messageTypeIsReasonMessageProbabilityScore as mtrmps,  
sameMsgSubAsStateTripleProbabilityScore as smsstps,reasonLabelFoundUsingTripleExtractionProbabilityScore as rlfteps, messageContainsSpecialTermProbabilityScore as mcstps, 
prevParagraphSpecialTermProbabilityScore as ppstps, negationTermPenalty as nps
FROM dynamicweightallocation  
-- where label = 'accepted'  -- rejected -- final  -- dynamicweightallocation_copy2 
-- WHERE evalLevel not like '%MESSAGES%'
order by top5 DESC, top10 desc, top15 desc, top30 desc;
order by run ASC; 

SELECT DISTINCT proposal FROM trackrankingforsentences


UPDATE dynamicweightallocation SET RUN = RUN + 6;

INSERT INTO dynamicweightallocation  
SELECT * FROM dynamicweightallocation_jan2020 
WHERE RUN <7

