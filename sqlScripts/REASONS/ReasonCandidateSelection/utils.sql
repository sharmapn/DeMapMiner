select distinct pep from allmessages where pep > 330
select id, messageid, state,label, consider, causesentence from trainingdata where pep = 335
select * from allmessages where pep = 289
and  email like '%the PEP has been resurrected%'
select id, pep, state, causesentence, consider from trainingdata where pep = 289
select * from allmessages where messageid = 356987;

select id, pep, state, messageid, causesentence from trainingdata where pep = 308 and state = 'accepted'

select * from autoextractedreasoncandidatesentences