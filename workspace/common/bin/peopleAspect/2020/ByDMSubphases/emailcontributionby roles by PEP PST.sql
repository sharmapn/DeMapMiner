
-- 12 march 2021...we only consider those peps 466 - 51

-- we now need to investigate tese metrics for the 466 PEPs
-- and for the top 20 or so members

-- TRUNCATE pepcountsbysubphases_2021;
-- TRUNCATE pepcountsrq2bysubphases_overall_2021;
-- TRUNCATE pepcountsrq2bysubphases_detailed_2021;

SELECT * FROM pepcountsrq2bysubphases_overall_2021
SELECT * FROM pepcountsRQ2bysubphases_detailed_2021

-- main query
-- can also be used to get which pairwisw we want to focus
-- can be used for for separating by PEP type Standards Track as well.
SELECT pairwise, author, 
	-- if you dont want by DM subphase, comment this
	CASE folder
		WHEN 'python-ideas' THEN 'idea_generation'
		WHEN 'python-dev' THEN 'idea_discussion' WHEN 'python-checkins' THEN 'idea_discussion' 
		WHEN 'python-bugs-list' THEN 'idea_discussion' WHEN 'python-committers' THEN 'idea_discussion' 
		WHEN 'python-announce-list' THEN 'idea_discussion' WHEN 'python-distutils-sig' THEN 'idea_discussion'
		WHEN 'python-3000' THEN 'idea_discussion' WHEN 'python-patches' THEN 'idea_discussion'
		WHEN 'python-lists' THEN 'user_support'
	END AS DMsubphase, SUM(msgcount) AS summsg
	FROM pepcountsRQ2bysubphases_detailed_2021
	WHERE pairwise IN (SELECT pairwise FROM top10pairwise)
		AND (author = 'Guido van Rossum' OR author = 'Nick Coghlan' OR author = 'proposalAuthor')  -- author = 'bdfl_delegate'
		AND PEP NOT IN (SELECT PEP FROM bdflddpeps)
		AND peptype = 'Informational' -- 'Standards Track'
	-- AND pep IN (SELECT pep FROM pepdetails WHERE LENGTH (bdfl_delegatecorrected) > 0 ) -- if you only want the peps decided by bdfl delegate
	GROUP BY pairwise, author, DMsubphase
	-- ORDER BY summsg desc
	ORDER BY pairwise, summsg desc;
	
	UPDATE pepcountsrq2bysubphases_detailed_2021 SET folder = replace(folder,'C:\\datasets-additional\\','')
	-- UPDATE pepcountsrq2bysubphases_detailed_2021 SET folder = replace(folder,'C:\\datasets-additional\\','')
	SELECT * FROM pepcountsrq2bysubphases_detailed_2021 WHERE folder IS NULL
	SELECT folder, COUNT(*) FROM pepcountsrq2bysubphases_detailed_2021 GROUP BY folder -- WHERE folder IS null	
	--same query but for only the 52 peps decided by bdfl delegates
-- ####### last used query.....thus important one
SELECT pairwise, author, 
	-- if you dont want by DM subphase, comment this
	CASE folder
		WHEN 'python-ideas' THEN 'idea_generation'
		WHEN 'python-dev' THEN 'idea_discussion' WHEN 'python-checkins' THEN 'idea_discussion' 
		WHEN 'python-bugs-list' THEN 'idea_discussion' WHEN 'python-committers' THEN 'idea_discussion' 
		WHEN 'python-announce-list' THEN 'idea_discussion' WHEN 'python-distutils-sig' THEN 'idea_discussion'
		WHEN 'additional\\python-3000' THEN 'idea_discussion' 
		WHEN 'python-lists' THEN 'user_support'
	END AS DMsubphase, SUM(msgcount) AS summsg
	FROM pepcountsRQ2bysubphases_detailed_2021
	WHERE pairwise IN (SELECT pairwise FROM top10pairwise)
		AND (author = 'Guido van Rossum' OR author = 'bdfl_delegate' OR author = 'proposalAuthor')  -- author = 'bdfl_delegate'
		AND pep IN (SELECT PEP FROM bdflddpeps) -- (SELECT pep FROM pepdetails WHERE LENGTH (bdfl_delegatecorrected) > 0 ) -- if you only want the peps decided by bdfl delegate
			AND peptype = 'Process' -- 'Standards Track'
	GROUP BY pairwise, author, DMsubphase
	-- ORDER BY summsg desc
	ORDER BY pairwise, author, dmsubphase	

-- select * FROM pepcountsrq2bysubphases_detailed_2021 WHERE author LIKE '%delegate%'

-- same above query by pep types -- still working on it
SELECT pairwise, author, pepetype, SUM(msgcount) AS summsg
	FROM pepcountsRQ2bysubphases_detailed_2021
	WHERE pairwise IN (SELECT pairwise FROM top10pairwise)
	--	AND (author = 'Guido van Rossum' OR author = 'Nick Coghlan' OR author = 'proposalAuthor')  -- author = 'bdfl_delegate'
	GROUP BY pairwise, author, peptype
	-- ORDER BY summsg desc
	ORDER BY pairwise, author
	
-- get the top 10 pairwise we want to focus on
CREATE TABLE top10pairwise AS
	SELECT pairwise, author, SUM(msgcount) AS summsg
		FROM pepcountsRQ2bysubphases_detailed_2021
		GROUP BY pairwise, author
		ORDER BY summsg desc
		LIMIT 10



SELECT * FROM pepcountsrq2bysubphases_detailed_2021 
SELECT * FROM pepcountsbysubphases_2021

SELECT DISTINCT pep FROM pepcountsrq2bysubphases_overall_2021

CREATE TABLE pepcountsRQ2bysubphases_overall_2021 LIKE  pepcountsrq2bysubphases_overall;
CREATE TABLE pepcountsRQ2bysubphases_detailed_2021 LIKE pepcountsrq2bysubphases_detailed;
TRUNCATE pepcountsRQ2bysubphases_detailed_2021;
TRUNCATE pepcountsrq2bysubphases_overall_2021;
CREATE TABLE pepcountsbysubphases_2021 LIKE pepcountsbysubphases;

SELECT * FROM allpepstillbdfl;

SELECT * FROM top10authors WHERE author IS NOT NULL LIMIT 20;

	clusterbysenderfullname 
	authorsrole = 'proposalauthor'
	
	ALTER TABLE pepcountsrq2bysubphases_overall_2021 ADD COLUMN author TEXT(15);
	ALTER TABLE pepcountsRQ2bysubphases_detailed_2021 ADD COLUMN author TEXT(15);