-- JEP Topic Modelling

SELECT proposal, messageid, sentence,isEnglishOrCode,msgSubject 
from allsentences 
where pep > -1 and isEnglishOrCode = 1 and (islastparagraph =1 or isfirstparagraph =1 )
order by pep asc, datetimestamp asc

select * from allmessages limit 5
select folder, count(messageid) as cnt from allmessages where jep = -1 
group by folder

-- create table distinctml as select distinct(folder) from allmessages;
select * from distinctml
-- update allmessages set originaljepnumber = jep;