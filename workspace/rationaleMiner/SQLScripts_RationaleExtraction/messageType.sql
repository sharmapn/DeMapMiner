select * from allmessages where pep = 279;
select * from trainingdata where pep = 366;


select proposal, messageSubject, datevalue, datediff, authorrole, sentence, termsmatched, messageTypeIsReasonMessage, containsreason
from autoextractedreasoncandidatesentences_dec 
where proposal = 386 and messageTypeIsReasonMessage =1 and datediff > - 15
and messageSubject NOT LIKE '%checkins%' -- and containsReason = 0
and messageid < 6000000
order by datediff asc;

select distinct messageid
from autoextractedreasoncandidatesentences_dec 
where proposal = 386 and messageTypeIsReasonMessage =1 and datediff > 0
and messageSubject NOT LIKE '%checkins%'
and messageid < 6000000
order by datediff asc
LIMIT 5; -- can be less