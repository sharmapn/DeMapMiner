select id, pep, state, messageid, causesentence, consider from trainingdata where pep = 201

select * from allmessages where pep = 382 and messageid = 342717

select distinct messageType from autoextractedreasoncandidatesentences

select * from allmessages where pep = 285 and messageid = 259354;

select * from allmessages where pep = 485 and -- email like '%little chance for pep%'
messageid = 33608;
select * from allmessages where pep = -1 and subject like '%3000%' and email like '%After a long conversation%' 
-- pep = 3134 and

select distinct messagetype from allmessages where pep = 308
select * from allmessages where pep = 488 and email like '% informal poll %' order by date2
select * from allmessages where folder like '%distutils-sig%' and subject like '%Simplify 426%' 
and email like '%I plan to reject PEP 390%' order by date2