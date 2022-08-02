

SELECT * FROM datedifftally

SELECT id, proposal, messageid, datevalue, dateofstatemessage, DATEDIFF, authorrole, sentence, label
from autoextractedreasoncandidatesentences_thesissubmitted 
WHERE containsreason =1
AND label NOT LIKE '%final%'
order BY proposal ASC;

-- lets do for just one pep
SELECT pep, datetimestamp, state
FROM pepstates_danieldata_datetimestamp
WHERE pep =308
ORDER BY pep ASC

SELECT pep, messageid, date2, datetimestamp, author, authorsrole
FROM allmessages 
WHERE pep = 308
-- AND DATE2 BETWEEN ('2003-08-13', '2003-08-13' - INTERVAL 1 DAY)
-- lets manually assign three days less
AND DATE2  BETWEEN '2003-07-13' AND '2003-08-13'
ORDER BY DATE2

SELECT pep, messageid, date2, datetimestamp, author, authorsrole
FROM allmessages 
WHERE pep = 308
AND DATE2  < '2003-08-13'
ORDER BY DATE2

SELECT * FROM pepstates_danieldata_datetimestamp  
where state like '%acc%' or state like '%rej%'

-- author role							-- author,
SELECT pep, authorsrole,  count(messageid),  count(DISTINCT author)
-- AVG(count(messageid)/count(DISTINCT author))
-- date2, datetimestamp, author, authorsrole
FROM allmessages where pep = 308 
AND DATE2 BETWEEN ('2003-08-13'-7) AND '2003-08-13'
AND folder LIKE "%dev%" 
-- OR folder LIKE "%ideas%")
GROUP BY authorsrole -- , author
ORDER BY DATE2

-- now we focus on the distinct authors
SELECT pep, authorsrole, author,  count(messageid) AS cnt -- ,  count(DISTINCT author)
-- AVG(count(messageid)/count(DISTINCT author))
-- date2, datetimestamp, author, authorsrole
FROM allmessages where pep = 308 
AND DATE2 BETWEEN ('2003-08-13'-7) AND '2003-08-13'
AND folder LIKE "%dev%" 
-- OR folder LIKE "%ideas%")
GROUP BY authorsrole, author -- , author
ORDER BY cnt DESC

-- combined into one query

-- now we focus on the distinct authors
SELECT pep, authorsrole, author,  count(messageid) AS cnt, -- ,  count(DISTINCT author)
-- AVG(count(messageid)/count(DISTINCT author))
-- date2, datetimestamp, author, authorsrole
-- SELECT NOW()
( rolesmsgcountSELECT COUNT(messageid) FROM allmessages WHERE pep = 308 AND author LIKE a.author AND folder LIKE "%dev%" )   AS ALLguidocontributions
FROM allmessages a where pep = 308 
AND DATE2 BETWEEN ('2003-08-13'-7) AND '2003-08-13'
AND folder LIKE "%dev%" 
-- OR folder LIKE "%ideas%")
GROUP BY authorsrole, author -- , author
ORDER BY cnt DESC


SELECT * from rolesmsgcount
SELECT * from rolespersonmsgcount

-- main query for this sql file
SELECT pep, state, author, authorsrole, msgcountalldates, msgcountfordates, msgcountbeforestate
FROM rolespersonmsgcount
-- WHERE PEP = 308
ORDER BY pep ASC, state asc, msgcountalldates DESC, msgcountfordates desc

TRUNCATE rolespersonmsgcount;
TRUNCATE rolespersonmsgcount_nodatefilter;

-- this is correct dataset
SELECT DISTINCT PEP, state, date2 
FROM pepstates_danieldata_datetimestamp 
WHERE (state like '%acc%' or state like '%rej%')
ORDER BY pep ASC, state

SELECT DISTINCT pep, state FROM rolespersonmsgcount
-- result gives 149 ROWS

-- find common core devs
select pep, author,count(*) as PEPCount from rolespersonmsgcount
group by pep
having PEPCount > 10;

-- main query to get top common roles
DROP VIEW commonroles;
CREATE VIEW commonroles AS 
	select author, authorsrole, count(distinct pep) as PEPCount 
	from rolespersonmsgcount
	-- IMPORTANT. For acceptance and rejection 
	-- WHERE state LIKE '%rej%' -- for overall data comment this line
	group by author
	having PEPCount > 10
	ORDER BY pepcount DESC;
	
SELECT * FROM commonroles;


SELECT * 
FROM pepstates_danieldata_datetimestamp
WHERE PEP = 3155

SELECT * FROM commonroles;


SELECT pep, messageid, date2 -- count(messageid)  
FROM allmessages
WHERE pep = 3155 
-- AND folder LIKE '%dev%' 
-- AND DATE2 <= '2011-11-21'
AND author LIKE '%Georg Brandl%' 



