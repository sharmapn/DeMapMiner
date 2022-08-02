select * from allmessages where messageid > 6000000
select * from allmessages where messageid = 36326;
select count(*) from allmessages where pep = 486;

update allmessages set messageType = 'BDFLReviewing'  where messageid > 6000000;

select * from pepdetails where pep = 454

select * from trainingdata where pep = 397

select * from trainingdata where label like '%accepted%'

select  datediff,count(*) as counter 
from autoextractedreasoncandidatesentences_dec
where containsreason = 1 
group by datediff 
order by datediff asc, counter desc
-- 479, 376, 289
delete from autoextractedreasoncandidatesentences where proposal = 289;

select * from allmessages where pep = 397 and subject like '%accepting%' -- messageid = 195815;
select * from allmessages where pep = 386 and email like '%no more feedback%';
select * from allmessages_dev where date2 = '2016-05-16' and author like '%Brett%'
 and email like '%steady%'
 
select  proposal,label, messageid, messageSubject, datediff, authorrole, sentence,messageTypeIsReasonMessage
from autoextractedreasoncandidatesentences_dec
where 
-- datediff > 14 
-- essageTypeIsReasonMessage=1 -- and proposal > 1
-- containsreason =1
proposal = 397 and containsreason =1
and messageid = 195870
order by proposal, datediff asc  -- , messageType