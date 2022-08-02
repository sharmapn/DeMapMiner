SELECT COUNT(*) FROM allpeps
WHERE pep > -1
-- 98041
SELECT COUNT(DISTINCT messageid) FROM allpeps
WHERE pep > -1
-- 7093434


-- DELETE FROM results WHERE pep = 201
SELECT * FROM results 
ORDER BY pep DESC

SELECT * FROM allmessages WHERE sendername LIKE '%skip%' LIMIT 5

SELECT * FROM  results_b4feb2020run
WHERE pep = 308

SELECT * FROM  results
WHERE pep = 308
AND length(clausie) > 0

SELECT pep, messageid, DATE, clausie FROM  results
WHERE pep = 308
AND length(clausie) > 0
ORDER BY date

SELECT * FROM trainingdata WHERE pep = 318

SELECT * 
FROM pepstates_danieldata_datetimestamp
WHERE pep = 435


SELECT  pep, subject FROM allmessages 
WHERE 
--subject LIKE '% round%'
subject LIKE '% seven%' 
OR subject LIKE '% eight%' 
-- LIMIT 10

SELECT  pep, email, subject FROM allmessages 
WHERE subject LIKE '% round 7%'

-- OR subject LIKE '% eight%'


