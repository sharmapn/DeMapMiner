SELECT * from dynamicweightallocation

SELECT pep, DATE, clausie, currentsentence 
FROM results WHERE pep = 572
AND length(clausie) > 0
ORDER BY DATE ASC

SELECT *
FROM results
WHERE pep = 572
 
 
SELECT * FROM  pepstates_danieldata_datetimestamp
WHERE pep = 572

insert into results (pep,messageid, date, datetimestamp,subject, currentSentence, author,        clausie) 
SELECT 					pep,messageid, date2,datetimestamp,subject, email,          senderfullname, state 
from pepstates_danieldata_datetimestamp;
-- where PEP = 572 AND length(email) > 0 
-- and pep > 0; 

-- delete april fool state
DELETE FROM results 
WHERE clausie LIKE '%april%'

SELECT COUNT(DISTINCT pep), peptype 
FROM pepstates_danieldata_datetimestamp
GROUP BY peptype