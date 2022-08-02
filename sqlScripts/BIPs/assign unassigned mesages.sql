SELECT * FROM allmessages 
WHERE bip =342 IN (9,141, 341, 342)

select subject, bip, COUNT(*) AS cnt
FROM allmessages
WHERE bip IN (9,141, 341, 342)
GROUP BY subject, bip
ORDER BY cnt DESC

-- group by date, month
select bip, subject, datetimestamp, YEAR(datetimestamp) AS yr, MONTH(datetimestamp) AS dt
FROM allmessages
WHERE bip IN (9,141, 341, 342)
-- GROUP BY bip, subject
GROUP BY yr, dt
-- ORDER BY cnt DESC

-- group by ssubject discussion tart and end date
select bip, subject, datetimestamp, YEAR(datetimestamp) AS yr, MONTH(datetimestamp) AS dt
FROM allmessages
WHERE bip IN (9,141, 341, 342)
-- GROUP BY bip, subject
GROUP BY yr, dt

-- just create a table of top message subject
CREATE TABLE topsubjectsfourbips AS 
	SELECT subject, COUNT(*) AS cnt 
	FROM allmessages 
	WHERE bip IN (9,141, 341, 342) 
	GROUP BY subject 
	ORDER BY cnt desc LIMIT 70
	
	-- update this above table by adding the author of thread, who has mostposts to the thread

-- main one showing start dat an end date
select subject, MIN(datetimestamp), MAX(datetimestamp),
-- date(min(datetimestamp)), date(max(datetimestamp)), 
DATEDIFF(date(MAX(datetimestamp)), date(MIN(datetimestamp)) )  AS daysdiscussed,  COUNT(*) AS nummessages 
FROM allmessages
WHERE subject IN (SELECT subject FROM topsubjectsfourbips)
AND bip IN (9,141, 341, 342) 
GROUP BY subject
ORDER BY nummessages DESC

SELECT subject, bip, COUNT(*) AS cnt
FROM allmessages
WHERE subject IN (SELECT subject FROM topsubjectsfourbips)
AND bip IN (9,141, 341, 342) 
GROUP BY subject, bip
ORDER BY subject, cnt desc

SELECT * FROM allmessages
WHERE subject = '[bitcoin-dev] Start time for BIP141 (segwit)'
ORDER BY datetimestamp asc

-- assign ore messages to BIPs
select bip, subject, COUNT(*) AS cnt
FROM allmessages
WHERE bip = -1
GROUP BY subject
ORDER BY cnt desc
-- AND subject LIKE '%segwit%'