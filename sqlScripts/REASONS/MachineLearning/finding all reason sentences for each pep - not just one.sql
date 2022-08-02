SELECT author , datediff, COUNT(datediff) AS occurence
FROM autoextractedreasoncandidatesentences_improved_beststats
where containsreason=1
GROUP BY author, datediff;

SELECT proposal,label, messageid,messagesubject,datevalue, dateofstatemessage, datediff, author, sentence, abs(datediff) as diff
FROM autoextractedreasoncandidatesentences_improved_beststats
where containsreason=1
order by diff desc;

SELECT proposal,messageid,dateValue, datediff, sentence,termsMatched, 
(sentenceHintProbablity+sentenceLocationHintProbability+messageLocationProbabilityScore+messageSubjectHintProbablityScore+dateDiffProbability+authorRoleProbability
-negationTermPenalty) as TotalProbability,probability, label, author, location, 
sentenceHintProbablity,sentenceLocationHintProbability,messageLocationProbabilityScore,messageSubjectHintProbablityScore,negationTermPenalty,dateDiffProbability,authorRoleProbability
from autoextractedreasoncandidatesentences 
where proposal = 309  -- sentence like '%after a long%' and
order by label asc, TotalProbability desc, dateValue desc;
select * from trainingdata where pep = 309 and causesentence like '%submitted for%';-- 
select * from allmessages where pep = 309 and email like '%submitted for% review%';-- 
-- where proposal = 309  and sentence like '%submitted for%';
select * from autoextractedreasoncandidatesentences where proposal = 309  and sentence like '%submitted for%'
-- if dateofstatemessage is after  the current message then the datediff would be positive
-- if dateofstatemessage is before the current message then the datefiff would be negative, sommetimes they rflect of why it was rejected or accepted 

select distinct(authorsrole) from allmessages;

select distinct(proposal) from autoextractedreasoncandidatesentences;