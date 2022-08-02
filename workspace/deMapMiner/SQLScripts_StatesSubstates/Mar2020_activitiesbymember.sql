-- several rows with author column were empty in results table..so we need to lookup the allmessages table

SELECT pep, messageid, author , senderfullname, clusterbysenderfullname
FROM allmessages 
-- WHERE author IS NULL
WHERE clusterbysenderfullname is null
LIMIT 5


SELECT pep, messageid, author  FROM results
WHERE author IS NULL

ALTER TABLE results CHANGE COLUMN author authorold TEXT;
ALTER TABLE results ADD COLUMN author TEXT;

ALTER TABLE results CHANGE COLUMN author authorold TEXT;

-- MAIN Update
UPDATE results t1
	INNER JOIN allmessages t2
		ON t1.messageID = t2.messageid
		SET t1.author = t2.clusterbysenderfullname;` 
		
SELECT distinct messageid 
FROM results
WHERE author IS NULL	

SELECT distinct messageid 
FROM results
WHERE author IS not NULL	

-- there are still 3k ROWS which are null	
UPDATE results t1
	INNER JOIN allmessages t2
		ON t1.messageID = t2.messageid
		SET t1.author = t2.senderfullname
		WHERE t1.author IS NULL;

--still 1.6 k which are null
SELECT *
FROM results
WHERE author IS NULL	


UPDATE results t1
	INNER JOIN allmessages t2
		ON t1.messageID = t2.messageid
		SET t1.author = t2.author
		WHERE t1.author IS NULL;
		
-- now only tracking records for each pep exist and commit rows
-- tracking rows will be omitted during post procesing
-- we just need to take care of the pepstacommits table

SELECT * FROM pepstates_danieldata_datetimestamp
SELECT * FROM  pepstates_danieldata_datetimestamp_firstoneused

SELECT DISTINCT idea FROM labels
-- 116..athough there are mani states and some reduntatnt sunstates which canbe removed
SELECT DISTINCT clausie FROM results_postprocessed
WHERE clausie NOT IN ('draft','open','active', 'pending', 'closed', 'final','accepted', 'deferred','replaced','rejected','postponed',
								'incomplete','superseded','provisional','withdrawn','finished', 'complete','approved','withdrawal')
								ORDER BY clausie asc
-- 60 substates - from which we can remove main states
-- left with 44
SELECT clausie, COUNT(*) AS cnt
FROM results_postprocessed
WHERE clausie NOT IN ('draft','open','active', 'pending', 'closed', 'final','accepted', 'deferred','replaced','rejected','postponed',
								'incomplete','superseded','provisional','withdrawn','finished', 'complete','approved','withdrawal')
GROUP BY clausie 
order by cnt DESC

-- we have 44 substates ..now we get all the s,v,o's

SELECT idea, subject, verb, object 
FROM labels
WHERE idea IN 
(
	SELECT clausie
	FROM results_postprocessed
	WHERE clausie NOT IN ('draft','open','active', 'pending', 'closed', 'final','accepted', 'deferred','replaced','rejected','postponed',
								'incomplete','superseded','provisional','withdrawn','finished', 'complete','approved','withdrawal')
) 
ORDER BY idea asc




