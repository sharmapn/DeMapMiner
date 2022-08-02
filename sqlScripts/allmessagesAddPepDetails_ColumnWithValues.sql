SELECT pepsummary from pepdetails where pep = 308
select * from pepdetails;
update pepdetails set messageid = (6000000+pep);
-- main sql which i ran
insert into allmessages (pep, originalPEPNumber, messageid, email,analysewords, authorsrole, author)
select pep,pep, messageid, pepsummary, pepsummary,'bdfl', 'Guido van Rossum'
from pepdetails;
-- delete from allmessages where messageid >= 6000000;
select distinct(author) from allmessages where pep = 308 and authorsrole like '%bdfl%';
select distinct(authorsrole) from allmessages where pep = 308;
update allmessages set author = trim(author);
select * from results_postprocessed where messageid = 5000571;
delete from autoextractedreasoncandidatesentences where proposal = 308;
select * from autoextractedreasoncandidatesentences where proposal = 308;