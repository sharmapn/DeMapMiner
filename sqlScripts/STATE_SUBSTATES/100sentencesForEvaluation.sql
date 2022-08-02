-- 100 sentences for evaluation from results and postprocessed sentences
select * from results_jan2019 
where clausie <> 'draft' and clausie <> 'accepted' and clausie <> 'rejected' and clausie <> 'active'
and clausie <> 'withdrawn' and clausie <> 'postponed' and clausie <> 'complete' and clausie <> 'incomplete'
and clausie <> 'final' and clausie <> 'deferred' and clausie <> 'approved' and clausie <> 'finished'
and clausie <> 'superceded' and clausie <> 'replaced'
and clausie <> 'proposal'
and length(subject) > 0  and length(relation) > 0
ORDER BY RAND() LIMIT 50;

select pep, messageid, date, datetimestamp, messagesubject, label, subject, relation, object, currentsentence, clausie,
author, authorrole, folder, repeatedlabel, peptype, labelfoundinmsgsubject, isfirstparagraph, islastparagraph, 
linenumber
from results;

select * from labels where idea like '%bdfl_pronouncement%' 
order by idea, subject

-- 100 sentences from results
drop table evalResults;
-- original table name = evalResults, and results_jan instead of results
create table evalResultsJuly2019 as
SELECT pep, messageid,date, currentsentence,label, subject, relation, object, clausie  FROM results
where messageid < 5000000 and label <> 'proposal' and length(relation) >0
ORDER BY RAND() LIMIT 0,100;

-- select * from results_jan limit 5
select * from evalresults;
select pep, messageid,date, currentsentence,label, subject, relation, object, clausie  FROM results
where pep=308 and messageid = 261760 and label = 'consensus'

select pep, messageid, date, currentsentence, label, subject, relation, object, clausie from evalresults

-- evaluation of postprocessed 
-- original table name = evalResultspost
create table evalResultspostJuly2019 as  
SELECT pep, messageid,date, currentsentence,label, subject, relation, object, clausie  FROM results_postprocessed
where messageid < 5000000 and label <> 'proposal' and length(relation) >0
ORDER BY RAND() LIMIT 0,100;
SELECT * FROM RESULTS;
-- select * from results_jan limit 5
select * from evalResultspost;
-- corss checking FP just to make sure
select pep, messageid,date, currentsentence,label, subject, relation, object, clausie  FROM results_jan
where pep=440 and messageid = 398043
 and label like '%FEED%'
-- 308	126682

select pep, messageid, date, currentsentence, label, subject, relation, object, clausie from evalresults


