SELECT COUNT(messageid) 
FROM allpeps
GROUP BY pep

SELECT pep, COUNT(messageid) AS cnt
FROM allpeps
WHERE pep > -1
AND folder NOT LIKE '%announce%'
AND folder NOT LIKE '%gmane%'
AND folder NOT LIKE '%distutils%'
GROUP BY pep

SELECT * from allpeps LIMIT 10

SELECT DISTINCT folder 
FROM allpeps

pepcountsbysubphases

SELECT pep, peptype, folder, msgcount
FROM pepcountsbysubphases
WHERE 
-- pep > -1


SELECT folder, SUM(msgcount) AS cnt
FROM pepcountsbysubphases
WHERE folder NOT LIKE '%announce%'
AND folder NOT LIKE '%gmane%'
AND folder NOT LIKE '%distutils%'
GROUP BY folder


SELECT folder, COUNT(DISTINCT messageid)
FROM allmessages
WHERE DATE2 < '2017-03-01'
GROUP BY folder