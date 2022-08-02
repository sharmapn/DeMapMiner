select * from allmessages where messageid = 308452;
select * from allmessages where pep = 345 and analysewords like '%reviewed PEP 345 and PEP 386%'
select distinct(proposal) from autoextractedreasoncandidatesentences
select * from autoextractedreasoncandidatesentences where location like '%state%'
delete from autoextractedreasoncandidatesentences where proposal >= 289;

SELECT pep,label,causeSentence, effectSentence, causeCategory, causeSubcategory from trainingdata
where label like '%accepted%';