 -- main tally to check 
	SELECT dmconcept, label,  peptype, state,  count(distinct pep) AS cnt,  GROUP_CONCAT(DISTINCT pep ORDER BY pep ASC SEPARATOR ', ') AS peps
	FROM trainingdata
	WHERE (state LIKE '%acc%' OR state LIKE '%rej%')
	GROUP BY dmconcept, label, peptype, state
	ORDER BY cnt desc

-- ORDER BY dmconcept, label, peptype desc, state asc

SELECT * FROM trainingdata WHERE pep = 326
SELECT * FROM trainingdata WHERE pep = 318
SELECT * FROM trainingdata WHERE causesentence like '%democracy%'

SELECT * FROM pepstates_danieldata_datetimestamp WHERE pep = 345
SELECT * FROM pepstates_danieldata_datetimestamp_firstoneused WHERE pep = 318

----
select a.pep, a.state from trainingdata as a 
where state LIKE '%rej%' AND a.pep NOT IN (select b.pep 
from  pepstates_danieldata_datetimestamp as b where state LIKE '%rej%')

	SELECT * from pepstates_danieldata_datetimestamp WHERE PEP = 3114
	SELECT * from trainingdata WHERE PEP = 3114

 
 SELECT * FROM pepstates_danieldata_datetimestamp WHERE pep = 563
  SELECT * FROM trainingdata WHERE pep = 563
  
SELECT 'r' AS test FROM trainingdata  


select pep, state from pepstates_danieldata_datetimestamp, 'eed' AS r
from trainingdata 
where state LIKE '%rej%'
---

SELECT COUNT(DISTINCT pep) FROM trainingdata 
WHERE state LIKE '%acc%' OR state LIKE '%rej%'
	SELECT pep,  COUNT(DISTINCT state) as cnt 
	FROM trainingdata 
	WHERE state LIKE '%acc%' OR state LIKE '%rej%'
	GROUP BY pep -- , state
	HAVING cnt > 1
	-- peps 289, 308  and 464 seems to have double states
	SELECT pep, state
	FROM pepstates_danieldata_datetimestamp WHERE pep IN (289,308,464)
	-- peps 225, and 3152 dont have staes updated in the column
	SELECT pep, state
	FROM pepstates_danieldata_datetimestamp WHERE pep IN (225, 3152)
	SELECT id, pep, PEPTYPE, state, dmconcept, label, consider, toconsiderwhilereportingresults, causesentence
	FROM trainingdata WHERE pep IN (225, 3152)
	-- pep 308 does multiple reasons for 
	SELECT id, pep, PEPTYPE, state, dmconcept, label, consider, toconsiderwhilereportingresults, causesentence
	FROM trainingdata WHERE pep IN (203, 341, 343, 345, 451)
	
	select distinct pep from pepstates_danieldata_datetimestamp
	select distinct pep from pepstates_danieldata_datetimestamp

SELECT COUNT(DISTINCT pep) FROM pepstates_danieldata_datetimestamp 
WHERE state LIKE '%acc%' OR state LIKE '%rej%'

SELECT COUNT(DISTINCT pep)  FROM trainingdata 
WHERE (state LIKE '%acc%' OR state LIKE '%rej%')
-- AND label LIKE '%not stated%'
ORDER BY pep asc

SELECT state, peptype, COUNT(DISTINCT pep)  FROM trainingdata 
WHERE (state LIKE '%acc%' OR state LIKE '%rej%')
AND label LIKE '%not stated%'
GROUP BY state, peptype

-- Main tally trainingdta wth epstates daniel data commit states

-- feb 2020 main tally for NUMBER OF PEPS the trainingdata gatherered
SELECT state, peptype, COUNT(DISTINCT pep)  FROM trainingdata 
WHERE (state LIKE '%acc%' OR state LIKE '%rej%')
-- AND label LIKE '%not stated%'
GROUP BY state, peptype

SELECT * FROM pepstates_danieldata_datetimestamp
-- tally with this
SELECT peptype, state, COUNT(DISTINCT pep) 
FROM pepstates_danieldata_datetimestamp
WHERE state LIKE '%acc%' OR state LIKE '%Rej%'
GROUP BY peptype, state

-- THE NUMBER OF PEPS DOES TALLY in these two queries, but the other two queries (below) does not
	select dmconcept, label, count(*) as cnt,  peptype, state, count(distinct pep), GROUP_CONCAT(DISTINCT pep ORDER BY pep ASC SEPARATOR ', ') AS peps
	from trainingdata
	where toconsiderwhilereportingresults = 1  -- consider= 1
	and (STATE LIKE '%ACCEPT%' OR state like '%Rejected%')
	group by dmconcept, label, state, peptype
	order by dmconcept asc,  label asc, peptype desc, state asc
	
	SELECT * FROM 
	(                    -- 
		select dmconcept, label, peptype, state,  count(distinct pep) AS dis
		from trainingdata
		where toconsiderwhilereportingresults = 1   -- consider
		and (STATE LIKE '%ACCEPT%' OR state like '%Rejected%')
		group by dmconcept, label, state, peptype
		order by dmconcept asc,  dis DESC, peptype
	) AS t
	ORDER BY dmconcept, label, peptype desc, state ASC

-- possible reasons peps with different reason for same state
	SELECT pep, state, label -- ,  count(distinct pep) AS cnt,  GROUP_CONCAT(DISTINCT pep ORDER BY pep ASC SEPARATOR ', ') AS peps
	FROM trainingdata
	WHERE (state LIKE '%acc%' OR state LIKE '%rej%')
	AND toconsiderwhilereportingresults = 1
	GROUP BY pep, state, label
--	ORDER BY cnt desc


--, state, label -- ,  count(distinct pep) AS cnt,  GROUP_CONCAT(DISTINCT pep ORDER BY pep ASC SEPARATOR ', ') AS peps
	SELECT *
	FROM trainingdata
	WHERE (state LIKE '%acc%' OR state LIKE '%rej%')
	AND consider = 1
--	GROUP BY pep, state, label

-- possible reason we found is that we have some peps, for which there are multiple labels and sentences for each states 
-- solution...we add another column so that we only consider a pep once
-- 	ALTER TABLE trainingdata ADD COLUMN toconsiderwhilereportingresults INT
-- 	UPDATE trainingdata SET toconsiderwhilereportingresults = 1
-- Done	
-- and then we set those labels and sentences that we dont want to include in the reposrting to 0
	SELECT id, pep, state, dmconcept, label, consider, toconsiderwhilereportingresults, causesentence
	FROM trainingdata
	WHERE (state LIKE '%acc%' OR state LIKE '%rej%')
	AND consider = 1
--	AND pep = 201
	ORDER BY pep ASC, state asc
	--cheged but just to check
	
	SELECT pep, state,  COUNT(DISTINCT label) AS cnt
	FROM trainingdata
	WHERE (state LIKE '%acc%' OR state LIKE '%rej%')
   AND toconsiderwhilereportingresults =1 
--   AND pep = 201
	GROUP BY pep, state
	having cnt > 1
		-- AND toconsiderwhilereportingresults = 1
	-- DESC trainingdata
	
	-- AND CHECK EACH PEP using sql below
	SELECT id, pep, state, dmconcept, label, consider, toconsiderwhilereportingresults, causesentence
	FROM trainingdata 
	WHERE (state LIKE '%acc%' OR state LIKE '%rej%')
	AND pep IN (506)
	
--	feb 2020 ..we have removed multiple labels for peps with same state with multiple labels


-- we run the main QUERY again AND still we GET five extra peps
-- this may be due to peps having both accepted states
	SELECT pep, COUNT(DISTINCT state) AS cnt
	FROM trainingdata
	WHERE (state LIKE '%acc%' OR state LIKE '%rej%')
   AND toconsiderwhilereportingresults =1 
--   AND pep = 201
	GROUP BY pep
	having cnt > 1
	
-- just comparing peps
-- danile data 
SELECT peptype, state, COUNT(DISTINCT pep) FROM pepstates_danieldata_datetimestamp 
WHERE state LIKE '%acc%' OR state LIKE '%rej%'
GROUP BY peptype, state
-- trainingdata
SELECT peptype, state, COUNT(DISTINCT pep)  FROM trainingdata 
WHERE (state LIKE '%acc%' OR state LIKE '%rej%')
AND toconsiderwhilereportingresults =1 
GROUP BY peptype, state

-- in danile data but not entered accepted rowsn in trainingdata
-- CHANGE ACC TO REJ
-- This we used to insert rows rows as reasons not found
INSERT INTO trainingdata (pep, peptype, state, LABEL, consider, toconsiderwhilereportingresults, dmconcept) 
	select a.pep, a.peptype, a.state, 'Reason Not Found',0 AS consider, 1 AS toconsiderwhilereportingresults, 'Informal Voting' AS dmconcept
	from pepstates_danieldata_datetimestamp as a 
	-- , "REJECTED", "Reason Not Found", "0", "0"
	where state LIKE '%acc%'  AND a.pep NOT IN (select b.pep 
	from trainingdata as b where state LIKE '%acc%')
-- in trainingdata but not in state data -- opposite minus

-- SELECT * FROM trainingdata ORDER BY id DESC
-- delete FROM trainingdata WHERE id > 990


-- run this again to get tallies
select dmconcept, label, count(*) as cnt,  peptype, state, count(distinct pep), GROUP_CONCAT(DISTINCT pep ORDER BY pep ASC SEPARATOR ', ') AS peps
	from trainingdata
	where toconsiderwhilereportingresults = 1  -- consider= 1
	and (STATE LIKE '%ACCEPT%' OR state like '%Rejected%')
	group by dmconcept, label, state, peptype
	order by dmconcept asc,  label ASC, peptype
-- for latex appndix table
select dmconcept, label, count(*) as cnt,  peptype, state, count(distinct pep) AS cnt2, GROUP_CONCAT(DISTINCT pep ORDER BY pep ASC SEPARATOR ', ') AS peps
	from trainingdata
	where toconsiderwhilereportingresults = 1  -- consider= 1
	and (STATE LIKE '%ACCEPT%' OR state like '%Rejected%')
	group by dmconcept, label, state, peptype
	order by dmconcept asc, label asc, cnt2 DESC, peptype asc, state asc
 
-- get tallies by peptype
-- by peptype
select LEFT(peptype,1), dmconcept, label,  count(distinct pep), GROUP_CONCAT(DISTINCT pep ORDER BY pep ASC SEPARATOR ', ') AS peps
from trainingdata
WHERE (STATE LIKE '%ACCEPT%' OR state like '%Rejected%')
and toconsiderwhilereportingresults = 1
group by  peptype,   dmconcept, label

select peptype, dmconcept, label,  /*state, */ count(distinct pep), GROUP_CONCAT(DISTINCT pep ORDER BY pep ASC SEPARATOR ', ') AS peps
from trainingdata
where toconsiderwhilereportingresults = 1
AND (STATE LIKE '%ACCEPT%' OR state like '%Rejected%')
group by  peptype, dmconcept, label  /*, state */


-- data for chart - overall all pep types
select label, count(distinct pep) AS cnt /*state, */ 
-- , GROUP_CONCAT(DISTINCT pep ORDER BY pep ASC SEPARATOR ', ') AS peps
from trainingdata
where toconsiderwhilereportingresults = 1
AND (STATE LIKE '%ACCEPT%' OR state like '%Rejected%')
group by   label  /*, state */
ORDER BY cnt DESC

-- standard peps
select label, count(distinct pep) AS cnt /*state, */ 
-- , GROUP_CONCAT(DISTINCT pep ORDER BY pep ASC SEPARATOR ', ') AS peps
from trainingdata
where toconsiderwhilereportingresults = 1
AND peptype LIKE '%standard%'  -- change for other peps
AND (STATE LIKE '%ACCEPT%' OR state like '%Rejected%')
group by   label  /*, state */
ORDER BY cnt DESC

-- info peps
select label, count(distinct pep) AS cnt /*state, */ 
-- , GROUP_CONCAT(DISTINCT pep ORDER BY pep ASC SEPARATOR ', ') AS peps
from trainingdata
where toconsiderwhilereportingresults = 1
AND peptype LIKE '%infor%'  -- change for other peps
AND (STATE LIKE '%ACCEPT%' OR state like '%Rejected%')
group by   label  /*, state */
ORDER BY cnt DESC

-- process peps
select label, count(distinct pep) AS cnt /*state, */ 
-- , GROUP_CONCAT(DISTINCT pep ORDER BY pep ASC SEPARATOR ', ') AS peps
from trainingdata
where toconsiderwhilereportingresults = 1
AND peptype LIKE '%process%'  -- change for other peps
AND (STATE LIKE '%ACCEPT%' OR state like '%Rejected%')
group by   label  /*, state */
ORDER BY cnt DESC

SELECT id, label FROM trainingdata WHERE label LIKE '%Reason Not stated%'
SELECT pep, label FROM trainingdata WHERE  label LIKE '%reason not stated%'
-- UPDATE trainingdata SET label = 'Reason Not Found' WHERE label LIKE '%reason not stated%'
SELECT * FROM trainingdata WHERE label = 'no consensus' AND state LIKE '%acc%'
-- BDFL Pronouncement over No Consensus
SELECT PEP, PEPTYPE FROM trainingdata WHERE PEP = 308