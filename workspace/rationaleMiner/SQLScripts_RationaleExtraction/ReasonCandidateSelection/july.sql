select * from autoextractedreasoncandidatesentences where proposal = 308 and sentence like '%discussion%'
update autoextractedreasoncandidatesentences set location = TRIM(location);

select * from allmessages where pep = 308 and email like '%After a long%'