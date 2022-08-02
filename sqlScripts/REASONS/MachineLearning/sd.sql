delete from autoextractedreasoncandidatesentences where proposal = 302;

select min(proposal) from autoextractedreasoncandidatesentences;

select * from autoextractedreasoncandidatesentences_novaccepted where containsreason = 1 order by proposal
and termsmatched like '%discuss%'

select * from allmessages where messageid = 163384
-- Tarek Ziadé : ziade tarek
select * from pepdetails where author like '%tarek%'
select * from pepdetails where pep = 386
-- Tarek Ziad, 

select distinct authorrole, authorroleweight from autoextractedreasoncandidatesentences_novaccepted;

select * from pythonmembers_pepeditors
select * from pythonmembers_coredevelopers
SELECT pep,messageID, senderfullname, author, fromLine from allmessages where pep = 386 and messageid = 163384

SELECT proposal, authorrole, datediff FROM autoextractedreasoncandidatesentences 
where containsreason =1 and datediff <=14
order by proposal asc

select pep, count(*) 
-- pep, causesentence 
from trainingdata 
where length(causesentence) <1
group by pep

select id, label, pep, causesentence, notes from trainingdata where causesentence is not null and label like '%accepted%'
order by pep

select proposal, messageid, messagetype, sentence, messagesubject, authorrole, datevalue, datediff, label, location from autoextractedreasoncandidatesentences
where messsageTypeIsReasonMessage = 1
order by proposal, datediff asc ;

select proposal, messageid, messagetype, sentence, messagesubject, authorrole, datevalue, datediff, label, location 
from autoextractedreasoncandidatesentences
where messsageTypeIsReasonMessage = 1 and datediff >= 0 and messageType like '%bdflreviewing%'
order by proposal, datediff asc 
limit 1;


select * from trainingdata where pep =479;
select * from autoextractedreasoncandidatesentences where proposal = 479;
