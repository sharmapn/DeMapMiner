-- multiple labels captured in same message

select pep, messageid from results_postprocessed 
where 
limit 5

SELECT pep,messageid, COUNT(messageid) c FROM table GROUP BY pep HAVING c > 1;

-- see count for multiple messageid per pep
SELECT pep,messageid, 
COUNT(messageid) AS NumOccurrences
FROM results_postprocessed
-- where messageid = 278988
GROUP BY pep,messageid
HAVING ( COUNT(messageid) > 1 )

select pep, messageid, clausie
from results_postprocessed 
order by pep, messageid
-- where pep = 1 and messageid  = 278988;
-- GROUP BY pep,messageid,clausie
-- HAVING ( counter > 1 )

SELECT pep, messageid, clausie
FROM results_postprocessed
WHERE pep IN (
	 SELECT pep,messageid from (
		 SELECT pep,messageid, clausie,COUNT(messageid) AS NumOccurrences
	    FROM results_postprocessed
	    GROUP BY pep,messageid
	    HAVING NumOccurrences > 1
	 ) AS subquery
)

-- lets try views
drop view duplicateMessageID
create view duplicateMessageID as
		 SELECT pep,messageid,timestamp, COUNT(messageid) AS NumOccurrences
	    FROM results_postprocessed
	    GROUP BY pep,messageid
	    HAVING NumOccurrences > 1

	 
select pep, messageid,NumOccurrences from duplicateMessageID
select pep, messageid from duplicateMessageID

-- main query to see multiple labels in a message
SELECT 
    DISTINCT t1.pep, t1.messageid,t1.clausie, t1.timestamp, t1.currentSentence 
FROM
    results_postprocessed AS t1
    INNER JOIN duplicateMessageID AS t2 ON t2.pep = t1.pep  
	 INNER JOIN duplicateMessageID AS t3 ON t3.messageid = t1.messageid; 

select * from results_postprocessed where pep = 6 and messageid 284278;
