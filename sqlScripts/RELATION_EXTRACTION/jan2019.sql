-- jan 2019 -- MAIN QUERY
-- distinct
create table remaingConcepts as
	select distinct allverbsinmsgsub,allnounsinmsgsub from extractedrelations_clausie 
	where  pep >= -1 and messageid < 704121 
	AND (allnounsinmsgsub like '%pep%' or allnounsinmsgsub like '%proposal%' or allnounsinmsgsub like '%idea%')
	and LENGTH(allverbsinmsgsub) > 0 AND LENGTH(allnounsinmsgsub) > 0;
-- LIMIT 500
select * from remaingConcepts where allverbsinmsgsub like '%consensus%'  OR allnounsinmsgsub like '%consensus%'

-- helping queries
-- now lets start with seeing what occurs for these nouns and remove the unwanted terms
select allverbsinmsgsub,allnounsinmsgsub from extractedrelations_clausie 
where (msgSubject like '%pep&' or msgSubject like '%proposal&' or msgSubject like '%idea%') 
and (allverbsinmsgsub is not null AND allnounsinmsgsub is not null)
group by allverbsinmsgsub,allnounsinmsgsub;
-- MAIN QUERY
select allverbsinmsgsub,allnounsinmsgsub from extractedrelations_clausie 
where (LENGTH(allverbsinmsgsub) > 0 OR LENGTH(allnounsinmsgsub) > 0 )
AND pep >= -1 and allNounsInMsgSub IS NOT NULL and messageid < 704121
group by allverbsinmsgsub,allnounsinmsgsub;
select * from extractedrelations_clausie where msgsubject like '%revisited%'
select distinct msgsubject,allverbsinmsgsub,allnounsinmsgsub from extractedrelations_clausie
select * from extractedrelations_clausie limit 3
select distinct msgsubject, allverbsinmsgsub,allnounsinmsgsub  from extractedrelations_clausie  limit 5
select distinct pep from extractedrelations_clausie
select * from extractedrelations_clausie  where pep = 345 and messageid = 364992
select * from extractedrelations_clausie;
SELECT min(messageid) from allmessages where subject like '%list%'
select folder, count(messageid), min(messageid), max(messageid) from allmessages 
group by folder
-- lists start at 704121
SELECT allNounsInMsgSub, count(*) as cnt from extractedrelations_clausie 
where  pep >= -1 and allNounsInMsgSub IS NOT NULL and messageid < 704121
GROUP BY allNounsInMsgSub order by cnt DESC LIMIT 50;

-- these quesries were used to arrive at above main queries
select * from allparagraphs where pep = 279 and messageid 
select count(*) from allmessages
select max(date2) from allmessages where pep > 3100
select pep, messageid,author, authorrole, date, label, subject,relation, object, currentsentence 
from results_postprocessed where clausie like '%consensus%'
select * from  extractedrelations_clausie where pep = 279 and messageid = 97268  where sentence like '%voting%'
select * from allmessages where pep <> originalpepnumber limit 5
-- returned 13908 messages
select * from results where pep = 20 and messageid = 5855
-- delete from results_postprocessed  where label = 'proposal'
select * from allmessages where pep = 242 and email like '%was no opposition%'
select * from allmessages where pep = 308 and email like '%voting method%' and email like '%anybody%' 
select * from allmessages where pep = 308 and messageid = 1273620;
select * from allmessages limit 1
select count(distinct clusterbysenderfullname) from allmessages where pep > -1 and folder not like '%lists%'
select * from extractedrelations_clausie where pep = 336 where pep = 279 and messageid = 95876 -- 97268
select * from pepstates_danieldata_datetimestamp where pep = 318
select distinct pep from extractedrelations_clausie
-- see the rows returned
select pep, count(messageid) as mids from extractedrelations_clausie group by pep 

-- delete from results where pep > 221;

select * from results where pep = 222;
