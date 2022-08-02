

-- we have to insert some roes into results those were omitted from the results while prostprocessing
-- mainly due to higher priority labels catched from the same sentence

SELECT pep, messageid, DATE, label, currentsentence ,clausie
FROM results 
WHERE clausie LIKE '%poll%'
ORDER BY pep

SELECT pep, messageid, DATE, TIMESTAMP,label, currentsentence 
FROM results_postprocessed 
WHERE clausie LIKE '%poll%'
ORDER BY pep

-- main query fro adding poll
SELECT *
-- pep, messageid, DATE, label, currentsentence ,clausie
FROM results 
WHERE pep = 255 AND messageid = 89112
and clausie LIKE '%poll%'
ORDER BY pep
-- and then change the label 'poll_results_no_majority' to poll

-- NOT NEEDED ..DONE IN POST PROCESSING... main query for inserting
INSERT INTO results_postprocessed (pep,MESSAGEID, date, timestamp,label,currentsentence, clausie) 
SELECT distinct pep, messageid, DATE, dateTIMESTAMP, label, currentsentence ,clausie
FROM results 
WHERE (pep = 465 AND messageid = 216890 AND clausie LIKE 'poll')
	or 	(pep = 572 AND messageid = 8699321 AND clausie LIKE 'poll')
	or 	(pep = 3103 AND messageid = 329737 AND clausie LIKE 'poll')
	or 	(pep = 3147 AND messageid = 216890 AND clausie LIKE 'poll')
ORDER BY pep


SELECT pep, messageid, DATE, label, currentsentence ,clausie
FROM results 
WHERE clausie LIKE '%vot%'
ORDER BY pep

SELECT pep, messageid, DATE, TIMESTAMP,label, currentsentence 
FROM results_postprocessed 
WHERE clausie LIKE '%vote%'
-- OR clausie LIKE '%poll%'
ORDER BY pep

SELECT pep, messageid, DATE, TIMESTAMP,label, currentsentence 
FROM results_postprocessed 
WHERE pep = 488
SELECT pep, messageid, DATE, dateTIMESTAMP,label, currentsentence 
FROM results
WHERE pep = 572

SELECT pep, messageid, DATE, TIMESTAMP,label, currentsentence 
FROM results_postprocessed 
WHERE pep = 308
WHERE clausie LIKE '%voting_method%'


