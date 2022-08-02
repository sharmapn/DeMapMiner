
-- all peps
SELECT folder, COUNT(distinct pep)
FROM pepcountsbysubphases
WHERE pep > -1
AND folder NOT LIKE '%announce%'
AND folder NOT LIKE '%gmane%'
AND folder NOT LIKE '%distutils%'
GROUP BY folder

SELECT folder, sum(msgcount)
FROM pepcountsbysubphases
WHERE pep > -1
AND folder NOT LIKE '%announce%'
AND folder NOT LIKE '%gmane%'
AND folder NOT LIKE '%distutils%'
AND folder NOT LIKE '%import%'
AND folder NOT LIKE '%datetime%'
AND folder NOT LIKE '%workflow%'
AND folder NOT LIKE '%3000%'
GROUP BY folder

-- by pep
SELECT pep, sum(msgcount)
FROM pepcountsbysubphases
WHERE pep > -1
AND folder NOT LIKE '%announce%'
AND folder NOT LIKE '%gmane%'
AND folder NOT LIKE '%distutils%'
AND folder NOT LIKE '%import%'
AND folder NOT LIKE '%datetime%'
AND folder NOT LIKE '%workflow%'
AND folder NOT LIKE '%3000%'
GROUP BY pep



SELECT DISTINCT pep
FROM allmessages
WHERE pep > 0
AND DATE2 < '2017-03-01'

-- 178, 314 = very close to original value of dev reported in paper
SELECT folder, count(DISTINCT messageid)
FROM allmessages
WHERE 
-- pep <> -1
FOLDER LIKE '%python-dev%'
AND DATE2 < '2017-03-01'
-- GROUP BY folder

SELECT folder, count(DISTINCT messageid)
FROM allmessages
WHERE DATE2 <= '2017-03-10'
AND folder NOT LIKE '%announce%'
AND folder NOT LIKE '%gmane%'
AND folder NOT LIKE '%distutils%'
AND folder NOT LIKE '%import%'
AND folder NOT LIKE '%datetime%'
AND folder NOT LIKE '%workflow%'
AND folder NOT LIKE '%3000%'
GROUP BY folder

-- ------------------------------------------------------------------------------------
-- RQ1.1
-- FIRST TABLE IN THESIS ON CHAPYER 4

-- in general
SELECT peptype, COUNT(DISTINCT pep), MIN(msgcount), MAX(msgcount), AVG(msgcount), sum(msgcount)
FROM pepcountsbysubphases
WHERE pep > -1
AND folder NOT LIKE '%bugs-list%'
AND folder NOT LIKE '%announce-list%'
AND folder NOT LIKE '%gmane%'
AND folder NOT LIKE '%distutils%'
AND folder NOT LIKE '%import%'
AND folder NOT LIKE '%datetime%'
AND folder NOT LIKE '%workflow%'
AND folder NOT LIKE '%3000%'
AND PEP NOT IN (426,440,391,8,3000,308,318,376,302,484,238,285,393,263,343,3131,3101,383,492,3107,345,3108)
GROUP BY  peptype

-- does not have the sub-phases, so we add this
SELECT folder,peptype, COUNT(DISTINCT pep), MIN(msgcount), MAX(msgcount), AVG(msgcount), sum(msgcount)
FROM pepcountsbysubphases
WHERE pep > -1
AND folder NOT LIKE '%bugs-list%'
AND folder NOT LIKE '%announce-list%'
AND folder NOT LIKE '%gmane%'
AND folder NOT LIKE '%distutils%'
AND folder NOT LIKE '%import%'
AND folder NOT LIKE '%datetime%'
AND folder NOT LIKE '%workflow%'
AND folder NOT LIKE '%3000%'
GROUP BY folder, peptype

-- by subphases..AND AVERAGE USING AND NOT USING OUTLIERS
SELECT peptype, COUNT(DISTINCT pep) AS cnt, MIN(msgcount), MAX(msgcount), sum(msgcount)/(COUNT(DISTINCT pep) ), sum(msgcount)
FROM pepcountsbysubphases
WHERE pep > -1
-- THIS LIST can apply to all to get average values without outliers  
AND PEP NOT IN (426,440,391,8,3000,308,318,376,302,484,238,285,393,263,343,3131,3101,383,492,3107,345,3108)
-- AND folder LIKE '%ideas%' -- idea generation-- 
and folder LIKE '%python-list%' -- software use
-- AND (folder LIKE '%dev%' OR folder LIKE '%checkins%' OR folder LIKE '%patches%' OR folder LIKE '%committers%' ) -- idea discussion
GROUP BY peptype

-- mim and max depends on pep so need to be queried differently
SELECT pep, folder, msgcount
-- , COUNT(DISTINCT pep), MIN(msgcount), MAX(msgcount), sum(msgcount)
-- , MIN(msgcount), MAX(msgcount), AVG(msgcount), sum(msgcount)
FROM pepcountsbysubphases
WHERE pep > -1
-- THIS LIST can apply to all to get average values without outliers  
-- AND PEP NOT IN (426,440,391, 3,3000,308,318,376,302,484,238,285,393,263,343,3131,3101,383,492,3107,345,3108)
-- AND folder LIKE '%ideas%' -- idea generation
-- and folder LIKE '%python-list%' -- software use
AND (folder LIKE '%dev%' OR folder LIKE '%checkins%' OR folder LIKE '%patches%' OR folder LIKE '%committers%' ) -- idea discussion
-- GROUP BY pep




-- - easier TO ADD a column
-- SELECT * FROM pepcountsbysubphases
-- ALTER TABLE pepcountsbysubphases ADD COLUMN subphase TINYTEXT;
-- 
-- UPDATE pepcountsbysubphases SET subphase = 'idea-generation' WHERE folder LIKE '%ideas%'
-- UPDATE pepcountsbysubphases SET subphase = 'idea-discussion'
-- WHERE  (folder LIKE '%python-dev%' OR folder LIKE '%python-checkins%' OR folder LIKE '%python-patches%' OR folder LIKE '%python-committers%' )
-- UPDATE pepcountsbysubphases SET subphase = 'software-use' WHERE folder LIKE '%python-list%'
-- 
-- -- UPDATE TABLE pepcountsbysubphases SET subphase = "";
-- SELECT * FROM pepcountsbysubphases
-- 
-- -- NOW HAVING ASSIGNED THE SUB-PHASE COLUMN WE RUN TEH COLUMN AGAIN
-- SELECT subphase, pep, peptype, sum(msgcount) AS m
-- FROM pepcountsbysubphases
-- WHERE length(subphase) > 0
-- GROUP BY pep
-- ORDER BY peptype, m DESC


-- main query .not average is not coded correctly
SELECT peptype, COUNT(DISTINCT pep), MIN(msgcount), MAX(msgcount), AVG(msgcount), sum(msgcount)
FROM pepcountsbysubphases
WHERE pep > -1
AND folder NOT LIKE '%bugs-list%'
AND folder NOT LIKE '%announce-list%'
AND folder NOT LIKE '%gmane%'
AND folder NOT LIKE '%distutils%'
AND folder NOT LIKE '%import%'
AND folder NOT LIKE '%datetime%'
AND folder NOT LIKE '%workflow%'
AND folder NOT LIKE '%3000%'
-- WHERE DATE2 <= '2017-03-10'
GROUP BY peptype

-- pep number of messages
SELECT pep, COUNT(DISTINCT messageid)
-- peptype, COUNT(DISTINCT pep), MIN(msgcount), MAX(msgcount), AVG(msgcount), sum(msgcount)
FROM allmessages
WHERE pep > -1
AND folder NOT LIKE '%bugs-list%'
AND folder NOT LIKE '%announce-list%'
AND folder NOT LIKE '%gmane%'
AND folder NOT LIKE '%distutils%'
AND folder NOT LIKE '%import%'
AND folder NOT LIKE '%datetime%'
AND folder NOT LIKE '%workflow%'
AND folder NOT LIKE '%3000%'
-- and DATE2 <= '2017-03-10'
GROUP BY pep

-- MAIL QUERY FOR THESIS FIRST TABLE
-- above query by folder
SELECT folder,peptype, COUNT(DISTINCT pep), MIN(msgcount), MAX(msgcount), AVG(msgcount), sum(msgcount)
FROM pepcountsbysubphases
WHERE pep > -1
AND folder NOT LIKE '%bugs-list%'
AND folder NOT LIKE '%announce-list%'
AND folder NOT LIKE '%gmane%'
AND folder NOT LIKE '%distutils%'
AND folder NOT LIKE '%import%'
AND folder NOT LIKE '%datetime%'
AND folder NOT LIKE '%workflow%'
AND folder NOT LIKE '%3000%'
GROUP BY folder, peptype

-- arpril 2020 ..grouped query for each sub phase

SELECT folder,peptype, COUNT(DISTINCT pep), MIN(msgcount), MAX(msgcount), AVG(msgcount), sum(msgcount)
FROM pepcountsbysubphases
WHERE pep > -1
AND 
--   folder LIKE '%ideas%' -- idea generation
-- folder LIKE '%python-list%' -- software use
 (folder LIKE '%python-dev%' OR folder LIKE '%python-checkins%' OR folder LIKE '%python-patches%' OR folder LIKE '%python-committers%' ) -- idea discussion

AND folder NOT LIKE '%bugs-list%' AND folder NOT LIKE '%announce-list%'
AND folder NOT LIKE '%gmane%' AND folder NOT LIKE '%distutils%'
AND folder NOT LIKE '%import%' AND folder NOT LIKE '%datetime%'
AND folder NOT LIKE '%workflow%' AND folder NOT LIKE '%3000%'
GROUP BY  peptype
-- GROUP BY folder, peptype
SELECT peptype, COUNT(DISTINCT pep), MIN(msgcount), MAX(msgcount), AVG(msgcount), sum(msgcount)
FROM pepcountsbysubphases
WHERE pep > -1
AND folder LIKE '%python-dev%' 
OR folder LIKE '%python-committers%' 
OR folder LIKE '%python-checkins%' 
OR folder LIKE '%python-patches%'-- idea discussion

AND folder NOT LIKE '%bugs-list%' AND folder NOT LIKE '%announce-list%'
AND folder NOT LIKE '%gmane%' AND folder NOT LIKE '%distutils%'
AND folder NOT LIKE '%import%' AND folder NOT LIKE '%datetime%'
AND folder NOT LIKE '%workflow%' AND folder NOT LIKE '%3000%'
GROUP BY peptype


--second main query
SELECT pep, peptype, sum(msgcount) AS m
FROM pepcountsbysubphases
GROUP BY pep
ORDER BY m DESC


SELECT folder, MAX(DATE2) 
FROM allpeps
GROUP BY FOLDER

SELECT * FROM allmessages
WHERE messageid = 400001
OR messageid = 5000000
or messageid = 6000000
OR messageid = 7000000
or messageid = 8000000
OR messageid = 9000000

SELECT * FROM pepstates_danieldata_datetimestamp
SELECT * FROM pepdetails
SELECT * FROM allmessages LIMIT 4 
pepcountsrq2bysubphases

SELECT DISTINCT pep 
FROM pepstates_danieldata_datetimestamp
ORDER BY pep

SELECT DISTINCT pep 
FROM pepdetails
ORDER BY pep

SELECT * FROM pepdetails

SELECT * FROM pepstates_danieldata_datetimestamp
WHERE pep =42




SELECT pairwise, COUNT(DISTINCT PEP) AS p
FROM pepcountsrq2bysubphases
GROUP BY pairwise
ORDER BY p DESC

SELECT folder, COUNT(pep) AS peps, SUM(msgcount) AS summsgcount  
FROM pepcountsrq2bysubphases
GROUP BY  folder
-- ORDER BY summsgcount DESC
SELECT folder, COUNT(pep) AS peps
FROM pepcountsrq2bysubphases
GROUP BY  folder

SELECT * 
FROM pepcountsrq2bysubphases
WHERE pairwise LIKE '%final-finalpost%'
ORDER BY pairwise

SELECT state, COUNT(DISTINCT pep) 
FROM pepstates_danieldata_datetimestamp
GROUP BY state

-- for outliers
SELECT peptype, pep, SUM(msgcount) as sum
FROM pepcountsbysubphases
GROUP BY peptype, pep
ORDER BY PEPTYPE, SUM DESC

----------------------------------------------------------------------------
-- RQ 1.2
-- main query
SELECT pairwise, COUNT(DISTINCT pep) AS peps, SUM(msgcount) AS summsgcount, ( (SUM(msgcount)) /  (COUNT(DISTINCT pep))   )  AS avg  
FROM pepcountsrq2bysubphases_detailed 
WHERE folder NOT LIKE '%bugs-list%' AND folder NOT LIKE '%announce-list%'
AND folder NOT LIKE '%gmane%' AND folder NOT LIKE '%distutils%'
AND folder NOT LIKE '%import%' AND folder NOT LIKE '%datetime%'
AND folder NOT LIKE '%workflow%' AND folder NOT LIKE '%3000%'
GROUP BY pairwise
ORDER BY summsgcount DESC
-- COUNT(*) AS cnt,

-- query by all lists
SELECT pairwise, folder,   COUNT(DISTINCT pep) AS peps, SUM(msgcount) AS summsgcount, ( (SUM(msgcount)) /  (COUNT(DISTINCT pep))   )  AS avg  
FROM pepcountsrq2bysubphases_detailed
WHERE folder NOT LIKE '%bugs-list%' AND folder NOT LIKE '%announce-list%'
AND folder NOT LIKE '%gmane%' AND folder NOT LIKE '%distutils%'
AND folder NOT LIKE '%import%' AND folder NOT LIKE '%datetime%'
AND folder NOT LIKE '%workflow%' AND folder NOT LIKE '%3000%'
GROUP BY pairwise, folder
ORDER BY summsgcount desc

-- for each sub-phase
SELECT pairwise, 'idea generation' AS folder,  COUNT(DISTINCT pep) AS peps, SUM(msgcount) AS summsgcount
FROM pepcountsrq2bysubphases_detailed
-- WHERE folder LIKE '%ideas%' -- idea generation
WHERE folder LIKE '%python-list%' -- software use
-- WHERE (folder LIKE '%python-dev%' OR folder LIKE '%python-checkins%' OR folder LIKE '%python-patches%' OR folder LIKE '%python-committers%' ) -- idea discussion
AND folder NOT LIKE '%gmane%' AND folder NOT LIKE '%distutils%'
AND folder NOT LIKE '%import%' AND folder NOT LIKE '%datetime%'
AND folder NOT LIKE '%workflow%' AND folder NOT LIKE '%3000%'
GROUP BY pairwise
ORDER BY summsgcount DESC
-- ORDER BY pairwise asc

--main query by folder
SELECT pairwise, folder, COUNT(DISTINCT pep) AS peps, SUM(msgcount) AS summsgcount  
FROM pepcountsrq2bysubphases
GROUP BY pairwise, folder
ORDER BY summsgcount DESC

SELECT pairwise, folder, COUNT(DISTINCT pep) AS peps, SUM(msgcount) AS summsgcount  
-- folder,peptype, COUNT(DISTINCT pep), MIN(msgcount), MAX(msgcount), AVG(msgcount), sum(msgcount)
FROM pepcountsrq2bysubphases
WHERE pep > -1
 AND folder LIKE '%ideas%' -- idea generation
-- AND folder LIKE '%python-list%' -- software use
-- AND folder LIKE '%python-dev%' OR folder LIKE '%python-committers%' OR folder LIKE '%python-checkins%' OR folder LIKE '%python-patches%'-- idea discussion

AND folder NOT LIKE '%bugs-list%' AND folder NOT LIKE '%announce-list%'
AND folder NOT LIKE '%gmane%' AND folder NOT LIKE '%distutils%'
AND folder NOT LIKE '%import%' AND folder NOT LIKE '%datetime%'
AND folder NOT LIKE '%workflow%' AND folder NOT LIKE '%3000%'
GROUP BY folder, peptype


SELECT pairwise, COUNT(DISTINCT pep) AS peps, SUM(msgcount) AS summsgcount  
-- folder,peptype, COUNT(DISTINCT pep), MIN(msgcount), MAX(msgcount), AVG(msgcount), sum(msgcount)
FROM pepcountsrq2bysubphases
WHERE pep > -1
-- AND folder LIKE '%ideas%' -- idea generation
-- AND folder LIKE '%python-list%' -- software use
AND (folder LIKE '%python-dev%' OR folder LIKE '%python-committers%' OR folder LIKE '%python-checkins%' OR folder LIKE '%python-patches%')-- idea discussion

AND folder NOT LIKE '%bugs-list%' AND folder NOT LIKE '%announce-list%'
AND folder NOT LIKE '%gmane%' AND folder NOT LIKE '%distutils%'
AND folder NOT LIKE '%import%' AND folder NOT LIKE '%datetime%'
AND folder NOT LIKE '%workflow%' AND folder NOT LIKE '%3000%'
GROUP BY pairwise
ORDER BY summsgcount desc