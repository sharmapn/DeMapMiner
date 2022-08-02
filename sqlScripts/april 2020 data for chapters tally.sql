-- the main dataset for both chapters 4 and 5
	SELECT folder, count(DISTINCT messageid)
	FROM allmessages
	WHERE DATE2 <= '2017-03-10'
	AND PEP <> -1                        -- DM MESSAAGES
--	AND folder LIKE '%dev%'  just to check if count values are grouped correctly
	AND folder NOT LIKE '%announce%'
	AND folder NOT LIKE '%gmane%'
	AND folder NOT LIKE '%distutils%'
	AND folder NOT LIKE '%import%'
	AND folder NOT LIKE '%datetime%'
	AND folder NOT LIKE '%workflow%'
	AND folder NOT LIKE '%3000%'
	GROUP BY folder
	
"folder"	"count(DISTINCT messageid)"
"C:\datasets\python-bugs-list"	"8796"
"C:\datasets\python-checkins"	"10071"
"C:\datasets\python-committers"	"271"
"C:\datasets\python-dev"	"32966"
"C:\datasets\python-ideas"	"7208"
"C:\datasets\python-lists"	"13987"
"C:\datasets\python-patches"	"1221"

 	
-- TOTAL amounts where direct assihnment of pep number to messages
SELECT COUNT(DISTINCT messageid) FROM allmessages  
WHERE pep <> -1 AND DATE2 <= '2017-03-10'
AND folder NOT LIKE '%bugs%' AND folder NOT LIKE '%distutils%'
AND folder NOT LIKE '%announce%' AND folder NOT LIKE '%3000%'
AND folder NOT LIKE '%authors%';

"COUNT(DISTINCT messageid)"
"65727"


-- for all message which we caught using subject only
 -- we created this table before
 DESC allmessages_subjectsonly
SELECT COUNT(DISTINCT messageid) FROM allmessages_subjectsonly WHERE DATE2 <= '2017-03-10' AND pep <> -1 -- LIMIT 5
-- 
"COUNT(DISTINCT messageid)"
"48165"

SELECT * FROM allmessages_subjectsonly WHERE pep <> -1 LIMIT 5
SELECT * FROM allmessages WHERE messageid = 89125

SELECT * FROM allmessages_subjectsonly WHERE DATE2 > '2018-01-01'

--- see pep titles only
SELECT * from allmessages_peptitleonly LIMIT 5

SELECT * FROM allmessages_peptitleonly LIMIT 500 

SELECT * FROM allmessagesmatchingsubjects LIMIT 500 

select COUNT(*) from allmessages_peptitleonly 
WHERE DATE2 <= '2017-03-10' 
-- 
select COUNT(DISTINCT messageid) from allmessages_peptitleonly
WHERE DATE2 <= '2017-03-10' 
-- 2347 
select COUNT(DISTINCT messageid) from allmessages_subjectsonly
WHERE DATE2 <= '2017-03-10' 
-- 
