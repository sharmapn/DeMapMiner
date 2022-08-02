update trainingdata set label = trim(label);
update autoextractedreasoncandidatesentences set label = trim(label);
update autoextractedreasoncandidatesentences set sentence = trim(sentence);
select distinct(sentenceLocationHintProbability) from autoextractedreasoncandidatesentences
select * from autoextractedreasoncandidatesentences where sentenceLocationHintProbability= 0.9;
-- main probaility query ...+added 2018 oct 25 restOfParagraphProbabilityScore 
SELECT proposal,messageid,dateValue, sentence,termsMatched, probability, label, author, location,
(sentenceHintProbablity+sentenceLocationHintProbability+restOfParagraphProbabilityScore+messageLocationProbabilityScore+messageSubjectHintProbablityScore
+dateDiffProbability+authorRoleProbability+messsageTypeIsReasonMessageProbabilityScore-negationTermPenalty) as TotalProbability,
sentenceHintProbablity,sentenceLocationHintProbability,restOfParagraphProbabilityScore,messsageTypeIsReasonMessageProbabilityScore,messageLocationProbabilityScore,messageSubjectHintProbablityScore,negationTermPenalty,dateDiffProbability,authorRoleProbability
from autoextractedreasoncandidatesentences where -- sentence like '%after a long%' and
proposal = 319 order by label asc, TotalProbability desc, dateValue desc;
-- location LIKE '%sentence%' and -- and label like 'r%' -- and sentence like '%sick%'

delete from autoextractedreasoncandidatesentences where proposal = 214; 
-- >= 254  and proposal <= 296;
select * from autoextractedreasoncandidatesentences ;
where proposal = 411;
select distinct(proposal) from autoextractedreasoncandidatesentences;  -- pep = 397 and
select *  from allmessages where messageid = 262203;
select *  from allmessages where email like '%enmity it generated%';
select *  from allmessages where pep = 319 and  email like '%in favor of PEP%';	
-- pep = 348 and subject like '%Bare except clauses in PEP 348%' and
select *  from allmessages where  email like '%due to the unprecedented level of enmity%';  --  pep = 666 and
select * from trainingdata where pep = 224;
--causesentence like '%This PEP is rejected%'; and pep = 365;
select * from allmessages where  pep = 254 and email like '%either way%'; 
-- sentence like '%after a long%' ; 
select * from pepstates_danieldata_datetimestamp where email like '%%';
UPDATE pepstates_danieldata_datetimestamp set email = 'Status : ACCEPTED' where email like '%Status : APPROVED%';
-- pep = 311;
select * from results_postprocessed where pep = 408;
select * from pepstates_danieldata_datetimestamp_old where  pep = 268;
select * from allmessages where pep = 3113 and email like '%lukewarm support%';

select max(pep) from extractedrelations_clausie

select * from autoextractedreasoncandidatesentences;
select pep, count(messageid) cnt from extractedrelations_clausie
group by pep
order by pep;

select proposal, label, sentence, (sentenceHintProbablity+sentenceLocationHintProbability+messageSubjectHintProbablityScore+negationTermPenalty) as total
from autoextractedreasoncandidatesentences;

SELECT pep,messageid,author,  date,timestamp, clausie,ps,currentSentence,ns, pp,ep,np,subject,relation,object 
from results_postprocessed where pep = 201
order by pep asc, clausie asc, timestamp asc