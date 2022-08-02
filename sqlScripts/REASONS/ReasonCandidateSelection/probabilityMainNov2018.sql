select * from allmessages where pep = 443 and messageid = 204307;
select * from allmessages where pep = 443 and email like '%open issues left%';
select * from trainingdata where pep = 326  -- order by messageid
where messageid = 204293;

SELECT messageTypeIsReasonMessage as RM, proposal,messageid,dateValue, sentence,termsMatched, probability, label, authorRole, location,
(sentenceHintProbablity+sentenceLocationHintProbability+restOfParagraphProbabilityScore+messageLocationProbabilityScore+messageSubjectHintProbablityScore
+dateDiffProbability+authorRoleProbability+messageTypeIsReasonMessageProbabilityScore+sentenceLocationInMessageProbabilityScore+sameMsgSubAsStateTripleProbabilityScore
+reasonLabelFoundUsingTripleExtractionProbabilityScore-negationTermPenalty) as TotalProbability,
sentenceHintProbablity,sentenceLocationHintProbability,restOfParagraphProbabilityScore,messsageTypeIsReasonMessageProbabilityScore,messageLocationProbabilityScore,
messageSubjectHintProbablityScore,negationTermPenalty,dateDiffProbability,authorRoleProbability,messsageTypeIsReasonMessageProbabilityScore,sentenceLocationInMessageProbabilityScore,
sameMsgSubAsStateTripleProbabilityScore, reasonLabelFoundUsingTripleExtractionProbabilityScore
from autoextractedreasoncandidatesentences where location = 'sentence' and -- sentence like '%unprecedented%' and
proposal = 319  
-- and messsageTypeIsReasonMessage =1; 
order by label asc, TotalProbability desc, dateValue desc;
-- and messageid = 204293

select * from allmessages where pep = 295 and email like '%BDFL decree%'
messageid = 204293;
select * from autoextractedreasoncandidatesentences ;
update pepdetails set messageid = (6000000 + proposal);

select 