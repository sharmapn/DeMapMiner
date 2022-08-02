select * from trainingdata where pep = 221;
delete from autoextractedreasoncandidatesentences where proposal = 308;
update trainingdata set effectsentence = trim(effectsentence);
update trainingdata set causesentence = trim(causesentence);
select label, pep, messageid, causesentence,effectsentence from trainingdata order by label,PEP;
, pep where LENGTH(label) > 0; 

SELECT distinct(proposal) from autoextractedreasoncandidatesentences WHERE label like 'accepted';