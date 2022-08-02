SELECT * FROM allmessages WHERE bip = -1
AND subject LIKE '%pep%' 
AND subject REGEXP '[0-9]' 
AND subject LIKE '%9%' 

-- bip unassigned
SELECT bip, subject -- subject --, COUNT(messageid) as cnt, MIN(datetimestamp) as st, MAX(datetimestamp) AS en, DATEDIFF(MAX(datetimestamp), MIN(datetimestamp) ) as dif  
FROM allmessages
WHERE bip = -1

create TABLE tempdata AS 
SELECT a.subject, COUNT(a.messageid) as cnt, MIN(a.datetimestamp) as st, MAX(a.datetimestamp) AS en, DATEDIFF(MAX(a.datetimestamp), MIN(a.datetimestamp) ) as dif
-- (SELECT distinct bip FROM allmesssages b WHERE b.subject LIKE a.subject) 
FROM allmessages a
-- SELECT Orders.OrderID, Customers.CustomerName
-- FROM Orders
-- INNER JOIN allmessages b ON a.subject = b.subject
WHERE a.bip = -1 -- AND folder NOT LIKE '%list%'
-- AND subject LIKE '%bip%' 
GROUP BY a.subject
ORDER BY cnt DESC



-- main query to get which bip numbers have been assigned -- only for cross checking
 SELECT d.*, -- m.subject, COUNT(m.messageid) as cnt, MIN(m.datetimestamp) as st, MAX(m.datetimestamp) AS en, DATEDIFF(MAX(m.datetimestamp), MIN(m.datetimestamp) ) as dif, 
        GROUP_CONCAT(distinct m.bip) bips -- , 
        -- GROUP_CONCAT(dish_name) dish_names
 FROM allmessages m JOIN tempdata d ON (m.subject = d.subject)
 WHERE m.bip = -1 
 GROUP BY m.subject
 ORDER BY cnt DESC


SELECT bip, subject, email, analysewords FROM allmessages WHERE subject = '[bitcoin-dev] BIP Process and Votes'

SELECT bip, messageid, subject, emailmessageid, sendername, DATE2, email, analysewords 
FROM allmessages WHERE subject = '[bitcoin-dev] A Small Modification to Segwit' 
ORDER BY DATE2 asc

-- we delete this one
-- SELECT * FROM mysql.general_log;

SELECT bip, COUNT(DISTINCT emailmessageid)
SELECT * from bipstates_danieldata_datetimestamp WHERE bip = 148 -- 91 -- 341 -- 9

-- check this
SELECT bip, DATE2, email, subject, sendername,file
FROM allmessages 
WHERE BIP = -1
AND (subject LIKE '%verion%' OR email LIKE '%version bits%')
AND DATE2 < '2016-08-20'
-- '%Version bits%' -- AND ANALYSEWORDS LIKE '%like this%'
ORDER BY date2 ASC, messageid ASC 

UPDATE allmessages SET originalbipnumber = bip;
UPDATE allmessages SET bip = 9 WHERE bip = -1 and subject LIKE '%Version bits proposal%'

-- bip 9
SELECT bip, DATE2, email, subject, sendername,file
FROM allmessages 
WHERE BIP = -1
AND (subject LIKE '%verion%' OR email LIKE '%version bits%')
AND DATE2 < '2016-08-20'
-- '%Version bits%' -- AND ANALYSEWORDS LIKE '%like this%'
ORDER BY date2 ASC, messageid ASC 
-- bip 91 ..added later on 17th April 2021
SELECT bip, DATE2, email, subject, sendername,file
FROM allmessages 
WHERE BIP = -1
AND (subject LIKE '%reduced%' OR email LIKE '%MASF%')
AND DATE2 < '2017-08-11'
-- '%Version bits%' -- AND ANALYSEWORDS LIKE '%like this%'
ORDER BY date2 ASC, messageid ASC 
-- 141
SELECT bip, DATE2, email, subject, sendername -- ,COUNT(messageid) AS cnt
FROM allmessages 
WHERE BIP = -1
AND (subject LIKE '%segwit%' OR subject LIKE '%segregated%' OR email LIKE '%segwit%' OR email LIKE '%segregated%')
AND DATE2 < '2017-08-30'
-- '%Version bits%' -- AND ANALYSEWORDS LIKE '%like this%'
GROUP BY subject
ORDER BY  datetimestamp ASC, messageid ASC -- cnt DESC,
-- bip 148 ..added later on 17th April 2021
SELECT bip, DATE2, email, subject, sendername,file
FROM allmessages 
WHERE BIP = -1
AND (subject LIKE '%soft fork%' OR email LIKE '%consensus%')
AND DATE2 < '2017-08-11'
-- '%Version bits%' -- AND ANALYSEWORDS LIKE '%like this%'
ORDER BY date2 ASC, messageid ASC 
-- 341
SELECT bip, DATE2, email, subject, sendername, COUNT(messageid) AS cnt
FROM allmessages 
WHERE BIP = -1
AND (subject LIKE '%taproot%' OR subject LIKE '%consensus%' OR email LIKE '%taproot%' OR email LIKE '%consensus%')
-- AND DATE2 < '2017-08-30' -- not finalised yet
-- '%Version bits%' -- AND ANALYSEWORDS LIKE '%like this%'
GROUP BY subject
ORDER BY cnt DESC, date2 DESC, messageid ASC 
-- 342
SELECT bip, DATE2, email, subject, sendername, COUNT(messageid) AS cnt
FROM allmessages 
WHERE BIP = -1
AND (subject LIKE '%taproot%' OR subject LIKE '%validation%' OR email LIKE '%taproot%' OR email LIKE '%validation%')
-- AND DATE2 < '2017-08-30' -- not finalised yet
-- '%Version bits%' -- AND ANALYSEWORDS LIKE '%like this%'
GROUP BY subject
ORDER BY cnt DESC, date2 DESC, messageid ASC 

SELECT subject, COUNT(DISTINCT messageid) AS cnt, MIN(DATE2) AS startdate
FROM allmessages WHERE bip = -1 
AND DATE2 < '2016-08-20'
GROUP BY subject ORDER BY cnt desc


-- LEFT TO ASSIGN = 14 rows = [Bitcoin-development] Version bits proposal
-- ASSIGNED...BUT MAYBE WE NEED TO ASSIGN ALL MESSAGESTO THIS BIP- [bitcoin-dev] [BIP Proposal] Version bits with timeout and delay.

GROUP BY bip 

SELECT DISTINCT folder FROM allmessages

SELECT * FROM allmessages WHERE email LIKE '%From g.rowe at froot.co.uk  Sun Jun 30 16:39:02 2013%'

SELECT DISTINCT(messageid) FROM allmessages
-- 18,653 messages

SELECT DISTINCT(messageid) FROM allmessages_debug
SELECT DISTINCT(messageid) FROM allmessages_olda
-- _olda bipdetails2020
ORDER BY bip ASC

-- so many bips were assigne dthe same message
SELECT * FROM allmessages -- _debug
WHERE email LIKE '%User Activated Soft Fork Split Protection%' 
AND email LIKE '%james.hilliard1 at gmail.com  Thu Jun  8 09:20:28 2017%'
LIMIT 5

SELECT bip, title from bipdetails where bip IS NOT NULL order by bip asc

SELECT bip, COUNT(messageid)
FROM allmessages -- _olda
GROUP BY bip
-- see results at the end of this file

-- ALTER TABLE allmessages add COLUMN quotedLine bool;

SELECT bip, COUNT(DISTINCT messageid) AS cnt FROM allmessages
GROUP BY bip

SELECT bip, quotedLine, email FROM allmessages
WHERE quotedLine = 1


UPDATE allmessages SET originalbipnumber = bip;

SELECT bip, messageid, originalbipnumber, email, analysewords FROM allmessages 
WHERE bip = -1
ORDER BY bip asc

-- who has written the most messages
CREATE VIEW top10members AS 
SELECT sendername, COUNT(DISTINCT bip) AS bips -- , COUNT(messageid) -- , COUNT(DISTINCT emailmessageid) AS messages 
FROM allmessages WHERE bip <> -1
GROUP BY sendername -- , bip
ORDER BY bips DESC
LIMIT 10

SELECT * FROM pepdetails


SELECT COUNT(DISTINCT bip) FROM allmessages

-- march 24 2021, we want to add how many people were involved in the discussion sfor this subject
SELECT sendername FROM allmessages WHERE bip = 9 
SELECT sendername, COUNT(DISTINCT messageid) as cnt FROM allmessages WHERE bip = 9 
GROUP BY author
ORDER BY cnt desc

-- march 24..see decision date
SELECT bip, state, DATE2 FROM bipstates_danieldata_datetimestamp WHERE bip = 9
-- 2016-08-20

-- see bip 9 subjects which are assigned -1 --- and needs to be assigned
SELECT subject, TotalMessagesForThisBIP, TotalMessagesAllBIPs, TotalPeopleThisBIP, TotalPeopleAllBIPs, startdate, enddate, daysdiscussed, unasssigned,  bips, senders FROM (
	SELECT * FROM (
		SELECT subject, COUNT(DISTINCT m.emailmessageid) as TotalMessagesForThisBIP, MIN(m.datetimestamp) as startdate, MAX(m.datetimestamp) AS enddate, 
		DATEDIFF(MAX(m.datetimestamp), MIN(m.datetimestamp) ) as daysdiscussed
	--	GROUP_CONCAT(distinct m.bip) bips
		-- d.*, -- m.subject, COUNT(m.messageid) as cnt, MIN(m.datetimestamp) as st, MAX(m.datetimestamp) AS en, DATEDIFF(MAX(m.datetimestamp), MIN(m.datetimestamp) ) as dif, 
		--        GROUP_CONCAT(distinct m.bip) bips
		-- DISTINCT subject
		-- , d.subject, COUNT(*)
		FROM allmessages m -- a 
	--	JOIN tempdata d ON (m.subject = d.subject)
		WHERE bip = 9 -- 9. 141, 341, 342 -- change as per requirement
		GROUP BY m.subject
		ORDER BY daysdiscussed desc
		) AS tbA
		-- We join with how many times this particular person has been replied to
								-- inReplyToUserUsingClusteredSender
							--	DESC pepthreadsbyperson2020;
		LEFT JOIN (	
			SELECT subject as sub2, COUNT(DISTINCT emailmessageid)  AS unasssigned FROM allmessages 
			WHERE bip = -1
			GROUP BY subject
		) AS tbC
		ON (tbA.subject = tbC.sub2)
		
		-- the number of people involved for this BIP for this subject
		LEFT JOIN (	
			SELECT subject as sub6, COUNT(DISTINCT sendername) as TotalPeopleThisBIP FROM allmessages
			WHERE BIP = 9
			-- we just dont check from te same bip, but from all bips that have this subject			 
			GROUP BY subject
		) AS tbG
		ON (tbA.subject = tbG.sub6)
		
		-- the number of people involved in all BIPs for this subject
		LEFT JOIN (	
			SELECT subject as sub4, COUNT(DISTINCT sendername) as TotalPeopleAllBIPs FROM allmessages
			-- we just dont check from te same bip, but from all bips that have this subject			 
			GROUP BY subject
		) AS tbE
		ON (tbA.subject = tbE.sub4)
		
		-- the number of messages with this subject in all BIPs 
		LEFT JOIN (	                  -- maybe just here we dont need DISTINCT
			SELECT subject as sub5, COUNT(emailmessageid) as TotalMessagesAllBIPs FROM allmessages
			-- we just dont check from te same bip, but from all bips that have this subject			 
			GROUP BY subject
		) AS tbF
		ON (tbA.subject = tbF.sub5)
		
		-- Bips with messages with the same subject 
		LEFT JOIN (
	 		SELECT subject AS sub3, GROUP_CONCAT(distinct bip) bips FROM allmessages
	 		GROUP BY subject
	 	) AS tbD	
		ON (tbA.subject = tbD.sub3)
		
		-- lets see the different people writing these mesages 
		LEFT JOIN (
	 		SELECT subject AS sub7, GROUP_CONCAT(distinct sendername) senders FROM allmessages
	 		WHERE sendername IN (SELECT sendername FROM top10members) -- LIKE '%gregory%' OR sendername LIKE '%james%'
	 		GROUP BY subject
	 	) AS tbH	
		ON (tbA.subject = tbH.sub7)
		
		ORDER BY TotalMessagesForThisBIP DESC -- startdate ASC -- TotalMessages DESC 
) AS t

-- SELECT Persons.Name, Persons.SS, Fears.Fear FROM Persons
-- LEFT JOIN Person_Fear
-- INNER JOIN Fears
-- ON Person_Fear.FearID = Fears.FearID
-- ON Person_Fear.PersonID = Persons.PersonID


SELECT * FROM bipdetails 

-- peps unassigned
CREATE TABLE subjects2021 as
SELECT subject, COUNT(messageid) as cnt, MIN(datetimestamp) as st, MAX(datetimestamp) AS en, DATEDIFF(MAX(datetimestamp), MIN(datetimestamp) ) as dif  
FROM allmessages
WHERE pep = -1 AND folder NOT LIKE '%list%'
-- AND subject LIKE '%bip%' 
GROUP BY subject
ORDER BY cnt desc

UPDATE allmessages SET originalbipnumber = bip;

SELECT * FROM allmessages WHERE bip = -1 AND subject LIKE '%009%'
UPDATE allmessages SET bip = 9 WHERE bip = -1 AND subject LIKE '%009%';



"bip"	"COUNT(messageid)"
"-1"	"13860"
"0"	"32"
"1"	"121"
"2"	"89"
"4"	"9"
"5"	"1"
"6"	"2"
"7"	"8"
"8"	"102"
"9"	"323"
"10"	"27"
"11"	"8"
"12"	"21"
"13"	"12"
"14"	"17"
"15"	"108"
"16"	"163"
"17"	"14"
"18"	"2"
"19"	"3"
"20"	"51"
"21"	"162"
"22"	"24"
"23"	"4"
"30"	"60"
"31"	"7"
"32"	"646"
"33"	"11"
"34"	"161"
"35"	"27"
"36"	"1"
"37"	"139"
"38"	"61"
"39"	"283"
"40"	"6"
"41"	"2"
"42"	"11"
"43"	"105"
"44"	"162"
"45"	"41"
"46"	"11"
"47"	"17"
"48"	"12"
"49"	"29"
"50"	"24"
"60"	"16"
"61"	"46"
"62"	"112"
"63"	"5"
"64"	"40"
"65"	"217"
"66"	"231"
"67"	"18"
"68"	"137"
"69"	"25"
"70"	"666"
"71"	"7"
"72"	"89"
"73"	"12"
"74"	"22"
"75"	"60"
"76"	"1"
"78"	"1"
"79"	"9"
"80"	"4"
"81"	"1"
"82"	"1"
"84"	"14"
"90"	"16"
"91"	"79"
"98"	"2"
"99"	"96"
"100"	"248"
"101"	"173"
"102"	"122"
"103"	"44"
"104"	"5"
"105"	"3"
"106"	"1"
"107"	"2"
"109"	"18"
"111"	"22"
"112"	"88"
"113"	"36"
"114"	"36"
"115"	"15"
"116"	"9"
"117"	"19"
"118"	"47"
"119"	"2"
"120"	"6"
"121"	"5"
"122"	"1"
"123"	"16"
"125"	"133"
"126"	"1"
"127"	"1"
"130"	"33"
"131"	"2"
"133"	"11"
"134"	"12"
"135"	"1"
"136"	"1"
"137"	"1"
"140"	"11"
"141"	"283"
"142"	"4"
"143"	"139"
"144"	"23"
"145"	"7"
"146"	"9"
"147"	"59"
"148"	"203"
"149"	"87"
"150"	"32"
"151"	"97"
"152"	"28"
"153"	"10"
"155"	"6"
"157"	"74"
"158"	"106"
"159"	"7"
"160"	"6"
"170"	"3"
"171"	"2"
"173"	"92"
"174"	"142"
"175"	"2"
"176"	"11"
"177"	"3"
"178"	"2"
"199"	"2"
"202"	"13"
"301"	"3"
"322"	"20"
"324"	"2"
"325"	"6"
"339"	"12"
"340"	"37"
"341"	"32"
"342"	"10"
"420"	"2"
"999"	"1"
"7075"	"1"
"9999"	"1"
"91148"	"1"
"392048"	"3"

-- after bip title assignment...e.g. bip 120 had more messages
"bip"	"COUNT(messageid)"
"-1"	"13027"
"0"	"32"
"1"	"121"
"2"	"89"
"4"	"9"
"5"	"1"
"6"	"2"
"7"	"8"
"8"	"102"
"9"	"341"
"10"	"27"
"11"	"8"
"12"	"58"
"13"	"12"
"14"	"17"
"15"	"108"
"16"	"171"
"17"	"14"
"18"	"2"
"19"	"3"
"20"	"94"
"21"	"162"
"22"	"24"
"23"	"4"
"30"	"72"
"31"	"14"
"32"	"646"
"33"	"11"
"34"	"161"
"35"	"27"
"36"	"6"
"37"	"139"
"38"	"61"
"39"	"283"
"40"	"6"
"41"	"2"
"42"	"11"
"43"	"105"
"44"	"162"
"45"	"41"
"46"	"11"
"47"	"17"
"48"	"12"
"49"	"31"
"50"	"24"
"60"	"16"
"61"	"71"
"62"	"150"
"63"	"5"
"64"	"40"
"65"	"226"
"66"	"247"
"67"	"18"
"68"	"137"
"69"	"34"
"70"	"921"
"71"	"7"
"72"	"89"
"73"	"12"
"74"	"22"
"75"	"60"
"76"	"1"
"78"	"1"
"79"	"9"
"80"	"4"
"81"	"1"
"82"	"1"
"84"	"14"
"90"	"18"
"91"	"79"
"98"	"10"
"99"	"96"
"100"	"248"
"101"	"173"
"102"	"122"
"103"	"115"
"104"	"5"
"105"	"13"
"106"	"32"
"107"	"2"
"109"	"18"
"111"	"28"
"112"	"115"
"113"	"36"
"114"	"36"
"115"	"15"
"116"	"9"
"117"	"19"
"118"	"109"
"119"	"21"
"120"	"30"
"121"	"5"
"122"	"1"
"123"	"16"
"125"	"133"
"126"	"4"
"127"	"2"
"130"	"33"
"131"	"2"
"133"	"11"
"134"	"32"
"135"	"1"
"136"	"1"
"137"	"1"
"140"	"11"
"141"	"283"
"142"	"5"
"143"	"139"
"144"	"23"
"145"	"7"
"146"	"9"
"147"	"59"
"148"	"203"
"149"	"87"
"150"	"32"
"151"	"97"
"152"	"49"
"153"	"10"
"155"	"9"
"157"	"74"
"158"	"106"
"159"	"7"
"160"	"6"
"170"	"3"
"171"	"15"
"173"	"92"
"174"	"142"
"175"	"2"
"176"	"22"
"177"	"3"
"178"	"2"
"199"	"2"
"202"	"13"
"301"	"3"
"322"	"20"
"324"	"2"
"325"	"22"
"339"	"12"
"340"	"37"
"341"	"32"
"342"	"10"
"420"	"2"
"999"	"1"
"7075"	"1"
"9999"	"1"
"91148"	"1"
"392048"	"3"
