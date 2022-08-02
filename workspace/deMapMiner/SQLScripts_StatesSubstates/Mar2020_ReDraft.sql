

SELECT * FROM results_postprocessed_premar2020 WHERE clausie LIKE '%redraft%'
SELECT * FROM results_postprocessed_firstmajor WHERE clausie LIKE '%redraft%'
SELECT * FROM results_first WHERE clausie LIKE '%redraft%'
SELECT * FROM results_mar2020 WHERE clausie LIKE '%redraft%'

SELECT * FROM results_postprocessed_premar2020 WHERE clausie LIKE '%draft%'
SELECT * FROM results_postprocessed_firstmajor WHERE clausie LIKE '%draft%'

SELECT * FROM results_mar2020 WHERE clausie LIKE '%draft%'


-- this one has all varieties of draft versions
SELECT * FROM results_first WHERE clausie LIKE '%draft%'
-- the posprocessed results dont contain
SELECT * FROM results_postprocessed_premar2020 WHERE clausie LIKE '%draft%'
-- but these different draft versions are not captured ion our mar 2020 results as shown in sql result below
SELECT * FROM results WHERE clausie LIKE '%2nd draft%'
-- so we have to check the substate extraction why

-- rather than runnuing the entire extraction rocess again we just insert these agaian
SELECT * FROM results_first WHERE clausie LIKE '%draft%' AND LENGTH(clausie) > 5

-- MARCH 2020 - main query for draft versions insert into the new results
-- just have redraft as clausie rather than doing later some other way -  this  way of insertig redrafts does not work  eliminates most when postprocesing is done
-- better to replace all varieties of drafts during post processing
INSERT INTO results (pep, messageid, DATE, datetimestamp, subject, currentsentence, clausie, author)
SELECT pep, messageid, DATE, datetimestamp, subject, currentsentence, clausie, author 
FROM results_first WHERE clausie LIKE '%draft%' AND LENGTH(clausie) > 5
ORDER BY pep asc, DATE ASC

-- SELECT pep, messageid, DATE, datetimestamp, subject, currentsentence, clausie, author

SELECT * FROM results 
WHERE clausie LIKE '%redraft%' 
ORDER BY pep asc, DATE ASC

-- DELETE FROM results 
-- WHERE clausie LIKE '%redraft%' 
-- ORDER BY pep asc, DATE ASC

-- We insert this in the postprocessing table for renaming
select distinct clausie 
FROM results WHERE clausie LIKE '%draft%' AND LENGTH(clausie) > 5
ORDER BY pep asc, DATE ASC

SELECT pep, date2, email FROM allmessages
WHERE 
--pep = 255 AND
email LIKE '% poll %'
AND email LIKE '% simple generator %'
ORDER BY DATE2 asc
-- AND  email LIKE '%change here is requiring a link to the archived%'

