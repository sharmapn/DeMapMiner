SELECT distinct messageid 
-- , dateDiffProbability,authorRoleProbability 
from autoextractedreasoncandidatesentences_dec 
where proposal = 308 order by messageid asc


SELECT messageid, dateDiffProbability,authorRoleProbability, count(*) as reasonsentencesTotal 
from autoextractedreasoncandidatesentences_dec 
where proposal = 308 
and messageTypeIsReasonMessage =1 -- should be include this, only adds first and last sentence
and messageSubject NOT LIKE '%checkins%' and messageid < 6000000 -- gm ids are not good for some reason
group by messageid
order by reasonsentencesTotal desc

-- combine above with below
select distinct messageid
from autoextractedreasoncandidatesentences_dec 
where proposal = 386 and messageTypeIsReasonMessage =1 and datediff > 0
and messageSubject NOT LIKE '%checkins%'
and messageid < 6000000
order by datediff asc
LIMIT 5; -- can be less



select count(*) as reasonsentencesTotal  from autoextractedreasoncandidatesentences_dec
where messageid = 1216974