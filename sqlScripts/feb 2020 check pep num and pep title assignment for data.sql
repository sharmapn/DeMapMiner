-- for final thesis - data from ho many peps  numbers were were captured and assigned using different schemes

-- A = email comunications chapter
-- B = Boundary spanner chapter
-- C = Substates and Reasons chapter

-- TOTAL data for both datasets
-- date format = YYYY-MM-DD.
SELECT COUNT(DISTINCT messageid) FROM allmessages WHERE DATE2 < '2015-06-04' AND pep <> -1 
-- 75,113
SELECT COUNT(DISTINCT messageid) FROM allmessages WHERE DATE2 < '2015-06-04' 
-- 1366145

-- SET A
SELECT COUNT(DISTINCT messageid) FROM allmessages WHERE DATE2 < '2015-06-04' 
AND folder NOT LIKE '%bugs%' AND folder NOT LIKE '%distutils%'
AND folder NOT LIKE '%announce%' AND folder NOT LIKE '%3000%'
AND folder NOT LIKE '%authors%';
-- 1041660

-- SET B
SELECT COUNT(DISTINCT messageid) FROM allmessages WHERE DATE2 < '2017-03-01' 
AND folder NOT LIKE '%bugs%' AND folder NOT LIKE '%distutils%'
AND folder NOT LIKE '%announce%' AND folder NOT LIKE '%3000%'
AND folder NOT LIKE '%authors%';
-- 1100283

-- SET C -- 12 jULY 2018
SELECT COUNT(DISTINCT messageid) FROM allmessages WHERE DATE2 < '2018-07-12' 

SELECT COUNT(DISTINCT messageid) FROM allmessages WHERE pep <> -1 -- LIMIT 5
-- 91907
SELECT COUNT(DISTINCT messageid) FROM allmessages 
-- 1570856

--first case only pep numbers in message body or subject
SELECT COUNT(DISTINCT messageid) FROM allmessages  WHERE originalPEPNumber <> -1
-- 75,203
SELECT COUNT(DISTINCT messageid) FROM allmessages  WHERE originalPEPNumber <> -1 AND DATE2 < '2015-06-04'
-- 58802

DESC allmessages
-- for all message which we caught using subject only
-- but first we update teh date column whcih was not populated before
	UPDATE allmessages_subjectsonly t1 
	        INNER JOIN allmessages t2 
	             ON t1.messageid = t2.messageid
	SET t1.date2 = t2.date2 
-- SELECT * FROM allmessages_subjectsonly WHERE pep <> -1 LIMIT 5
-- SELECT * FROM allmessages WHERE messageid = 89125
DESC allmessages_subjectsonly
SELECT COUNT(DISTINCT messageid) FROM allmessages_subjectsonly WHERE DATE2 < '2015-06-04' AND pep <> -1 -- LIMIT 5
-- 43,864
SELECT COUNT(DISTINCT messageid) FROM allmessages_subjectsonly WHERE pep <> -1 -- LIMIT 5
-- 48,165

-- first we get the max date of the 'peps' database -- for the email contribution analysis

SELECT MAX(DATE2) FROM allpeps;
-- result = '2015-10-25'
SELECT DISTINCT(FOLDER) FROM allpeps;

-- then we see max date for each folder
SELECT folder, MAX(DATE2) FROM allpeps
GROUP BY folder;
"folder"	"MAX(DATE2)"
	"2015-02-02"
"C:\data\python-announce-list"	"2015-03-31"
"C:\data\python-checkins"	"2015-06-24"
"C:\data\python-committers"	"2015-04-03"
"C:\data\python-dev"	"2015-06-04"
"C:\data\python-distutils-sig"	"2015-07-03"
"C:\data\python-ideas"	"2015-07-01"
"C:\data\python-lists"	"2015-06-05"
"C:\scripts\gmanetxtd"	"2015-10-25"

-- SO we use this max date as its for python-dev - "2015-06-04"

-- now we get the message numbers
SELECT folder, COUNT(DISTINCT messageid) FROM allpeps
GROUP BY folder;
"folder"	"COUNT(DISTINCT messageid)"
	"887"
"C:\data\python-announce-list"	"459"
"C:\data\python-checkins"	"4981"
"C:\data\python-committers"	"205"
"C:\data\python-dev"	"24408"
"C:\data\python-distutils-sig"	"3147"
"C:\data\python-ideas"	"4277"
"C:\data\python-lists"	"14834"
"C:\scripts\gmanetxtd"	"17736"

SELECT folder, COUNT(DISTINCT messageid) 
FROM allmessages
where DATE2 < '2015-06-04' AND pep <> -1 
GROUP BY folder

SELECT folder, COUNT(DISTINCT messageid) FROM allpeps
WHERE PEP <> -1
GROUP BY folder;
-- same results as above ..meaning we didnt consider messages which dont have pep numbers

SELECT DISTINCT pep FROM allpeps
-- yep to above..no we didnt consider -1 peps

----------------------------------------------------------------------------------------
-- Actual queries from allmessages table from where we will query the different dates for both sets:
-- email communication analysis and substates datasets

SELECT pep, count(messageid)
FROM allmessages
WHERE pep <> -1
GROUP BY pep

DESCRIBE allmessages
DESCRIBE allpeps

SELECT pep, count(messageid)
FROM allmessages
WHERE originalPEPNumber <> pep
AND pep <> -1
GROUP BY pep


SELECT count(messageid)
FROM allmessages
WHERE originalPEPNumber <> pep
-- 13,914
-- AND pep <> -1

SELECT count(messageid)
FROM allmessages
WHERE originalPEPNumber <> pep
AND pep <> -1
-- 13,914 ..same numbers as above
-- and it is slightly more thna the first dataset -- slightly more meaning that previously more peps were refereed to with pep titles
-- now old dataset
SELECT count(messageid)
FROM allmessages
WHERE originalPEPNumber <> pep
AND DATE2 < '2015-06-04'
-- 12,520

-- Multiple peps mentioned in the same message -- hoe many messages of these type were there

SELECT messageid, COUNT(*) AS multiple
FROM allmessages
-- WHERE pep = 308 -- wont see as only one instance of a messageid will be stored fro each pep
GROUP BY messageid
HAVING multiple > 1
-- 28551

--choose the higheest to give as an example in thesis
SELECT * FROM allmessages WHERE messageid = 409828 
-- 19 DIFFERENT PEPS were mentioned in this message
-- link -- https://mail.python.org/pipermail/python-checkins/2015-October/139459.html
-- 8698620
-- 8698622

SELECT messageid, COUNT(*) AS multiple
FROM allmessages
WHERE DATE2 < '2015-06-04'
GROUP BY messageid
HAVING multiple > 1
-- 20919

-- now the inreplyto
-- SELECT senderName,emailMessageID, inReplyTo,
-- (SELECT senderName from allmessages where m.inReplyTo = emailMessageID)
-- from allmessages m
-- WHERE inReplyTo IS NOT NULL
-- LIMIT 5
-- 
-- SELECT pep, messageid, senderName,emailMessageID, inReplyTo
-- FROM
-- (
-- SELECT pep, messageid, senderName,emailMessageID, inReplyTo
-- FROM allmessages
-- WHERE PEP <> -1 AND emailMessageID IS NOT NULL
-- LIMIT 5
-- ) AS d
-- WHERE 

-- WE TRY THIS
-- LETS create a table of the above set and then query the replies to these messages
-- (SELECT senderName from allmessages where m.inReplyTo = emailMessageID) 
-- FIRST SET include ALL pep numbers apart FROM -1
-- second set include only peps with -1

-- MAIN QUERY - FINAL DATASET

CREATE VIEW allmessages_parent AS
SELECT * FROM allmessages 
WHERE emailmessageid  IS NOT NULL -- only those which we can match
AND PEP <> -1;		-- those that have been assigned a PEP

CREATE VIEW allmessages_child AS
SELECT * FROM allmessages 
WHERE inReplyTo  IS NOT NULL -- only those which we can match
AND PEP = -1;		-- those that have been assigned a PEP

SELECT COUNT(p.messageid)
-- c.PEP, c.messageID
-- p.*, c2.*
FROM allmessages_parent p
INNER JOIN allmessages_child c 
    ON c.inReplyTo = p.emailmessageid
-- 15,018 -- for all data    

-- FIRST STUDY SUBSET


CREATE VIEW allmessages_parent_first AS
SELECT * FROM allmessages 
WHERE DATE2 < '2015-06-04'
AND emailmessageid  IS NOT NULL -- only those which we can match
AND PEP <> -1;		-- those that have been assigned a PEP

CREATE VIEW allmessages_child_first AS
SELECT * FROM allmessages 
WHERE DATE2 < '2015-06-04' 
AND inReplyTo  IS NOT NULL -- only those which we can match
AND PEP = -1;		-- those that have been assigned a PEP

SELECT COUNT(p.messageid)
-- c.PEP, c.messageID
-- p.*, c2.*
FROM allmessages_parent_first p
INNER JOIN allmessages_child_first c 
    ON c.inReplyTo = p.emailmessageid
-- 10, 778 messages

select COUNT(*) from allmessages_peptitleonly
-- 7483
select COUNT(DISTINCT messageid) from allmessages_peptitleonly
-- 4021

select COUNT(DISTINCT messageid) from allmessages_subjectsonly
-- 131, 468

select COUNT(DISTINCT messageid) from allmessages_withpepnumbers
-- 25, 896

SELECT * FROM allmessages_subjectsonly WHERE pep <> -1 LIMIT 5
SELECT * FROM allmessages_withpepnumbers LIMIT 5


SELECT * FROM allmessages_subjectsonly WHERE pep <> -1 -- LIMIT 5


UPDATE allmessages_subjectsonly SET DATE2 = 






















-- PREVIOUSLY USED SQL IN THE STUDY WHICH MAY BE USEFUL

SELECT date2, subject,folder, inReplyTo, emailMessageID
FROM allmessages
WHERE inReplyTo IN (select * from (
	SELECT emailMessageID 
	from allmessages
	WHERE YEAR(date2) = 2017 AND MONTH(date2) = 1 
	AND folder like '%python-dev%' 
	AND inReplyTo IS NULL
	ORDER BY date2
	LIMIT 50
	) temp_tab)

SELECT emailMessageID as m,messageID, date2, senderName, subject, folder,
(SELECT count(emailMessageID) from all_distinctmessages WHERE inReplyTo = m)
from all_distinctmessages 
WHERE YEAR(date2) = 2016 
AND MONTH(date2) = 1 
AND inReplyTo IS NULL 
-- and subject like '%PEP 511%'
order by dateTimeStamp


SELECT t1.senderName AS lev1, t2.senderName as lev2, t3.senderName as lev3, t4.senderName as lev4
FROM allmessages AS t1
LEFT JOIN allmessages AS t2 ON t2.inReplyTo = t1.emailMessageID
LEFT JOIN allmessages AS t3 ON t3.inReplyTo = t2.emailMessageID
LEFT JOIN allmessages AS t4 ON t4.inReplyTo = t3.emailMessageID
WHERE t1.inReplyTo IS NULL
AND YEAR(t1.date2) = 2017 AND MONTH(t1.date2) = 1 ;

update all_distinctmessages
set countRepliesToThisMessage = (SELECT COUNT(messageID) FROM (select * FROM all_distinctmessages) as dm WHERE inReplyTo = dm.emailMessageID)
-- where s.messageID = p.messageID

UPDATE all_distinctmessages AS a, (SELECT COUNT(messageID) as ct FROM all_distinctmessages WHERE inReplyTo = emailMessageID) AS p
SET a.countRepliesToThisMessage = p.ct

SELECT * FROM allmessages LIMIT 5