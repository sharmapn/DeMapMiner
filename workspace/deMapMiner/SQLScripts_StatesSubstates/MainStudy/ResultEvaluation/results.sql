select count(*) from allmessages
where pep in (388,389)
 391, 414,428, 435, 441,450, 465,  484, 488, 498, 485, 471, 505, 3003, 3103, 3131,3144)
and analysewords like '%after a long discussion%'

select * from pepdetails where title like '%s'

select * from results where pep = 308 and clausie IS NOT NULL ORDER BY DATE ASC
select * from results where CLAUSIE IS NOT NULL AND reason IS NOT NULL

SELECT date2, sendername, analysewords, statusFrom, statusTo, statusChanged, messageID, subject, required, date2, inReplyTo 
from allmessages WHERE folder = 'C:\\datasets\\python-dev' 
limit 1

select distinct(pep) from results
select * from results

select distinct(pep) from relations_clausie
select * from relations_clausie

select * from pepstates_danieldata where pep = 289
select * from results order by pep,date	-- where pep = 289 
SELECT distinct(pep) from pepdetails
SELECT distinct(pep) from results
SELECT  max(pep) from results