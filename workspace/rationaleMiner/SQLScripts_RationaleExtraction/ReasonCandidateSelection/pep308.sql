select * from autoextractedreasoncandidatesentences 
where proposal = 308 and location like '%message%'

select * from allmessages where pep = 308 and email like '%after a long%';

select distinct(state) as state from states_reason_reasonsublabels;