-- 06 march 2021..we mainly use this script to set the clusterbysenderfullname field of those = null

SELECT distinct authorsrole FROM allmessages
WHERE pep <> -1 

SELECT pep, messageid, author, authorsrole, clusterbysenderfullname
FROM allmessages
where authorsrole = 'proposalauthor' OR authorsrole = 'bdfl_delegate'
AND pep <> 1
LIMIT 200

SELECT count(distinct pep) -- , messageid, author, authorsrole, clusterbysenderfullname
FROM allmessages
WHERE -- authorsrole = 'proposalauthor' OR 
authorsrole = 'bdfl_delegate'
AND pep <> -1
-- GROUP BY pep

SELECT  pep, messageid, author, authorsrole, clusterbysenderfullname
FROM allmessages
WHERE -- authorsrole = 'proposalauthor' OR 
authorsrole = 'bdfl_delegate'
AND pep <> -1
ORDER BY pep ASC

-- see all contributiosn of bdfl_delegates 
SELECT  pep, folder, messageid, author, authorsrole, clusterbysenderfullname, COUNT(messageid)
FROM allmessages
WHERE -- authorsrole = 'proposalauthor' OR 
authorsrole = 'bdfl_delegate'
AND pep <> -1
GROUP BY pep -- , folder
ORDER BY pep ASC

SELECT 

SELECT author,clusterbysenderfullname, inreplytouser, inReplyToUserUsingClusteredSender
FROM allmessages 
WHERE author IS NULL OR inReplyToUserUsingClusteredSender IS null

-- see unassigned clusterbysendername full
SELECT fromline, author, sendername, clusterbysenderfullname, COUNT(*) AS cnt
FROM allmessages 
WHERE clusterbysenderfullname IS NULL
GROUP BY author
ORDER BY cnt DESC

-- see unassigned inreplyto
SELECT inreplyto, inreplytouser, inReplyToUserUsingClusteredSender, COUNT(*) AS cnt
FROM allmessages 
WHERE inreplytouser IS NULL
AND inreplyto IS not NULL
GROUP BY inreplyto
ORDER BY cnt DESC

SELECT inreplyto, inreplytouser, inReplyToUserUsingClusteredSender, COUNT(*) AS cnt
FROM allmessages 
WHERE inReplyToUserUsingClusteredSender IS NULL
AND inreplytouser IS not NULL
GROUP BY inreplytouser
ORDER BY cnt DESC


-- we should have communications (emails sent) by the proposal suthor for all 52 peps , not just the 45...lets check if they did communicate in the 7 remaining peps
-- after checking those delegates did make communications
CREATE TABLE test20 as
SELECT pep -- , title, author, bdfl_delegate 
FROM pepdetails
WHERE LENGTH(bdfl_delegate) > 0

CREATE TABLE checkpeps as
SELECT pep FROM test20
LEFT JOIN test21 USING (pep)  
WHERE test21.pep IS NULL;  
-- SELECT  pep  FROM allmessages
-- WHERE authorsrole = 'bdfl_delegate'
-- AND pep <> -1 GROUP BY pep  

SELECT * FROM checkpeps;
SELECT * FROM allmessages WHERE pep IN (SELECT pep FROM checkpeps)
SELECT pep, bdfl_delegatecorrected FROM pepdetails WHERE pep IN (SELECT pep FROM checkpeps)
"pep"	"bdfl_delegatecorrected"
"451"	"Brett Cannon ;brett@python.org;, Nick Coghlan ;ncoghlan@gmail.com;"
"539"	"Nick Coghlan"
"541"	"Mark Mangoba ;mmangoba@python.org;"
"544"	"Guido van Rossum ;guido@python.org;"
"546"	"Benjamin Peterson ;benjamin@python.org;"
"566"	"Daniel Holth"
"571"	"Nick Coghlan ;ncoghlan@gmail.com;"

SELECT * FROM allmessages WHERE pep = 451 AND (author LIKE '%brett%' OR author LIKE '%coghlan%')
-- 208 rows
SELECT * FROM allmessages WHERE pep = 539 AND (author LIKE '%coghlan%')
-- 8 rows
SELECT * FROM allmessages WHERE pep = 541 AND (author LIKE '%Mangoba%')
-- 2 rows
SELECT * FROM allmessages WHERE pep = 544 AND (author LIKE '%rossum%')
-- 18 rows
SELECT * FROM allmessages WHERE pep = 546 AND (author LIKE '%peterson%')
-- 3 rows
SELECT * FROM allmessages WHERE pep = 566 AND (author LIKE '%holth%')
-- 9 rows
SELECT * FROM allmessages WHERE pep = 571 AND (author LIKE '%coghlan%')
-- 8 rows

-- pep, originalpepnumber,
DROP TABLE seetest 
CREATE TABLE seetest AS 
	SELECT pep, messageid, fromline, author, sendername, senderfullname, clusterbysenderfullname, authorsrole, authorsrole2020
	-- , inreplyto, inreplytouser, inreplytouserusingclusteredsender, inreplytouserusingclusteredsenderrole 
	FROM allmessages WHERE PEP <> -1
	ORDER BY pep ASC, author ASC

SELECT * FROM seetest
ORDER BY pep asc

-- see errors
SELECT pep, messageid, fromline, author, sendername, senderfullname, clusterbysenderfullname -- , authorsrole, authorsrole2020
	-- , inreplyto, inreplytouser, inreplytouserusingclusteredsender, inreplytouserusingclusteredsenderrole 
FROM allmessages WHERE PEP <> -1
and pep = 308 ORDER BY messageid



-- main checking sql ...mainly used the subquery first
SELECT author, COUNT(*) as cnt FROM
(
	SELECT pep, messageid, fromline, author, sendername, senderfullname, clusterbysenderfullname -- , authorsrole, authorsrole2020
		-- , inreplyto, inreplytouser, inreplytouserusingclusteredsender, inreplytouserusingclusteredsenderrole 
	FROM allmessages 
	WHERE PEP <> -1 
	-- and pep = 308  
	-- AND AUTHOR LIKE '%skip%'
	AND (clusterbysenderfullname not LIKE '% %' OR clusterbysenderfullname LIKE '%.%')
	ORDER BY author ASC
) AS m
GROUP BY author
ORDER BY cnt desc

UPDATE allmessages SET clusterbysenderfullname ='Aahz Maruch' 	WHERE clusterbysenderfullname = 'aahz' and author = 'Aahz Maruch';
UPDATE allmessages SET clusterbysenderfullname ='Andrew Kuchling' 	WHERE clusterbysenderfullname = 'A.M. Kuchling';
UPDATE allmessages SET clusterbysenderfullname ='Andrew Kuchling' 	WHERE clusterbysenderfullname = 'A M Kuchling';
UPDATE allmessages SET clusterbysenderfullname ='Adam Jiang' 	WHERE clusterbysenderfullname = 'Adam' and author = 'Adam Jiang';
UPDATE allmessages SET clusterbysenderfullname ='Andrew Kuchling' 	WHERE clusterbysenderfullname = 'akuchlin' and author = 'akuchling';
UPDATE allmessages SET clusterbysenderfullname ='Andrew Kuchling' 	WHERE clusterbysenderfullname = 'akuchlin' and author = 'Andrew Kuchling';
UPDATE allmessages SET clusterbysenderfullname ='Eric Smith' 	WHERE clusterbysenderfullname = 'Eric V. Smith';
UPDATE allmessages SET clusterbysenderfullname ='Eric Raymond' 	WHERE clusterbysenderfullname = 'esr' and author = 'Eric S. Raymond';
UPDATE allmessages SET clusterbysenderfullname ='Eric Raymond' 	WHERE clusterbysenderfullname = 'esr' and author = 'esr';
UPDATE allmessages SET clusterbysenderfullname ='Jeremy Hylton' 	WHERE clusterbysenderfullname = 'hylton' and author = 'Jeremy Hylton';
UPDATE allmessages SET clusterbysenderfullname ='Ron Adam' 	WHERE clusterbysenderfullname = 'ron3200' and author = 'Ron Adam';
UPDATE allmessages SET clusterbysenderfullname ='Richard Oudkerk' 	WHERE clusterbysenderfullname = 'shibturn' and author = 'Richard Oudkerk';
UPDATE allmessages SET clusterbysenderfullname ='Marcus Smith' 	WHERE clusterbysenderfullname = 'asmith' and author = 'Marcus Smith';
UPDATE allmessages SET clusterbysenderfullname ='Skip Montanaro' 	WHERE clusterbysenderfullname = 'skip' and author = 'skip';
UPDATE allmessages SET clusterbysenderfullname ='Arthur Siegel' 	WHERE clusterbysenderfullname = 'ajsiegel' and author = 'Arthur Siegel';
UPDATE allmessages SET clusterbysenderfullname ='Guido van Rossum' 	WHERE clusterbysenderfullname = 'gvanrossum' and author = 'gvanrossum';
UPDATE allmessages SET clusterbysenderfullname ='Greg Wilson' 	WHERE clusterbysenderfullname = 'gvwilson' and author = 'Greg Wilson';
UPDATE allmessages SET clusterbysenderfullname ='Dan Sommers' 	WHERE clusterbysenderfullname = 'Gregarius' and author = 'Dan Sommers';

UPDATE allmessages SET clusterbysenderfullname ='geremy condra' 	WHERE clusterbysenderfullname = 'Octo' and author = 'geremy condra';
UPDATE allmessages SET clusterbysenderfullname ='Gonçalo Rodrigues' 	WHERE clusterbysenderfullname = 'op73418' and author = 'Gonçalo Rodrigues';
UPDATE allmessages SET clusterbysenderfullname ='Python tracker' 	WHERE clusterbysenderfullname = 'cracker' and author = 'Python tracker';
UPDATE allmessages SET clusterbysenderfullname ='Lukasz Langa' 	WHERE clusterbysenderfullname = 'lukasz' and author = 'lukasz';
UPDATE allmessages SET clusterbysenderfullname = 'Jeremy Hylton'  WHERE clusterbysenderfullname = 'hylton' and author = 'jhylton';
UPDATE allmessages SET clusterbysenderfullname = 'James Althoff'  WHERE clusterbysenderfullname = 'James_Althoff' and author = 'James_Althoff';
UPDATE allmessages SET clusterbysenderfullname ='Martin Maney' 	WHERE clusterbysenderfullname = 'maney' and author = 'Martin Maney';
UPDATE allmessages SET clusterbysenderfullname ='Mark \'Kamikaze\' Hughes' 	WHERE clusterbysenderfullname = 'kamikaze' and author = 'Mark \'Kamikaze\' Hughes';
UPDATE allmessages SET clusterbysenderfullname ='Ryan Gonzalez' 	WHERE clusterbysenderfullname = 'RyanL' and author = 'Ryan Gonzalez';
UPDATE allmessages SET clusterbysenderfullname ='Mark Janssen' 	WHERE clusterbysenderfullname = 'average' and author = 'Mark Janssen';
UPDATE allmessages SET clusterbysenderfullname ='Trent Mick' 	WHERE clusterbysenderfullname = 'Tmack' and author = 'Trent Mick';
UPDATE allmessages SET clusterbysenderfullname ='Tarek Ziad' 	WHERE clusterbysenderfullname = 'tarek' and author = 'tarek';
UPDATE allmessages SET clusterbysenderfullname ='Michael Foord' 	WHERE clusterbysenderfullname = 'MichaelW' and author = 'Michael Foord';
UPDATE allmessages SET clusterbysenderfullname ='Paul Rubin' 	WHERE author = 'Paul Rubin'; -- too many different values for clusterby...
UPDATE allmessages SET clusterbysenderfullname ='Russell Turpin' 	WHERE clusterbysenderfullname = 'rturpin' and author = 'Russell Turpin';
UPDATE allmessages SET clusterbysenderfullname ='Walter Dörwald' 	WHERE clusterbysenderfullname = 'doerwalter' and author = 'doerwalter';

-- now we look at those records where clusterbysenderfullname IS NULL
UPDATE allmessages SET clusterbysenderfullname ='Guido van Rossum' 	WHERE clusterbysenderfullname IS NULL AND (fromline LIKE '%guido van rossum%' OR author = 'guido van rossum');
UPDATE allmessages SET clusterbysenderfullname ='Ivan Levkivskyi' 	WHERE clusterbysenderfullname IS NULL AND (author = 'ivan.levkivskyi' AND  sendername = 'Ivan Levkivskyi');


-- there is a pattern, so we write a generic one, base on this example
UPDATE allmessages SET clusterbysenderfullname ='Ivan Levkivskyi' 	WHERE clusterbysenderfullname IS NULL AND (author = 'ivan.levkivskyi' AND  sendername = 'Ivan Levkivskyi');
SELECT pep, messageid, EMAIL, fromline, author, sendername, senderfullname, clusterbysenderfullname 
FROM allmessages 
WHERE PEP <> -1 AND clusterbysenderfullname IS NULL AND author = REPLACE(sendername,'.',' ') AND author = REPLACE(senderfullname,'.',' ') 

-- main query to solve one main proklem
-- this has been run but should have included that not for peps which are -1
UPDATE allmessages SET clusterbysenderfullname = senderfullname WHERE clusterbysenderfullname IS NULL AND author = REPLACE(sendername,'.',' ') AND author = REPLACE(senderfullname,'.',' ');
-- just recheck
SELECT pep, messageid, EMAIL, fromline, author, sendername, senderfullname, clusterbysenderfullname 
FROM allmessages WHERE PEP = -1 AND clusterbysenderfullname IS NOT NULL AND author = REPLACE(sendername,'.',' ') AND author = REPLACE(senderfullname,'.',' ');


SELECT pep, messageid, EMAIL, fromline, author, sendername, senderfullname, clusterbysenderfullname 
FROM allmessages WHERE pep <> -1 and clusterbysenderfullname IS NULL AND senderfullname = REPLACE(author,'.','') AND fromline LIKE CONCAT('%', author, '%')
AND author <> 'noreply' AND author <> 'report';
-- the accompanying sql to update
UPDATE allmessages SET clusterbysenderfullname = senderfullname 
WHERE pep <> -1 and clusterbysenderfullname IS NULL AND senderfullname = REPLACE(author,'.','') AND fromline LIKE CONCAT('%', author, '%')
AND author <> 'noreply' AND author <> 'report';
-- another simialr one
-- SELECT pep, messageid, EMAIL, fromline, author, sendername, senderfullname, clusterbysenderfullname FROM allmessages
-- UPDATE allmessages SET clusterbysenderfullname = senderfullname -- pep <> -1 
WHERE clusterbysenderfullname IS NULL AND senderfullname = REPLACE(author,'.',' ') AND fromline LIKE CONCAT('%', senderfullname, '%') AND author <> 'noreply' AND author <> 'report';
-- was main one as 'Affected rows: 181,043'
-- another similar one
-- SELECT pep, messageid, EMAIL, fromline, author, sendername, senderfullname, clusterbysenderfullname FROM allmessages
UPDATE allmessages SET clusterbysenderfullname = senderfullname 
-- pep <> -1 
WHERE clusterbysenderfullname IS NULL AND fromline LIKE CONCAT('%', author, '%') AND senderfullname = REPLACE(author,'.',' ') AND author <> 'noreply' AND author <> 'report' AND author <> 'sourceforge.net';
-- /* Affected rows: 47,827 
-- another similar one
-- SELECT pep, messageid, EMAIL, fromline, author, sendername, senderfullname, clusterbysenderfullname FROM allmessages
UPDATE allmessages SET clusterbysenderfullname = senderfullname 
-- pep <> -1 
WHERE clusterbysenderfullname IS NULL AND fromline like CONCAT('%', sendername, '%') AND senderfullname = REPLACE(author,'.',' ') AND author <> 'noreply' AND author <> 'report' AND author <> 'sourceforge.net';
-- Affected rows: 0  Found rows: 11,213 

-- another similar one
-- SELECT pep, messageid, EMAIL, fromline, author, sendername, senderfullname, clusterbysenderfullname FROM allmessages
UPDATE allmessages SET clusterbysenderfullname = sendername 
-- pep <> -1 
WHERE clusterbysenderfullname IS NULL AND email like CONCAT('%(', sendername, ')%') AND sendername = REPLACE(author,'.',' ') AND author <> 'noreply' AND author <> 'report' AND author <> 'sourceforge.net';
AND senderfullname <> 'noreply' AND senderfullname <> 'SourceForge net'
-- main one  Affected rows: 72,115 

-- another similar one
-- SELECT pep, messageid, EMAIL, fromline, author, sendername, senderfullname, clusterbysenderfullname FROM allmessages
UPDATE allmessages SET clusterbysenderfullname = sendername 
WHERE sendername NOT LIKE '%utf%' AND sendername NOT LIKE '%iso%' AND sendername NOT LIKE '%=%' AND LENGTH(sendername) >0 AND clusterbysenderfullname IS NULL AND email like CONCAT('%(', sendername, ')%') 
-- AND pep <> -1 -- AND sendername = REPLACE(author,'.',' ') 
AND author <> 'noreply' AND author <> 'report' AND author <> 'sourceforge.net';
AND senderfullname <> 'noreply' AND senderfullname <> 'SourceForge net'
-- Affected rows: 8,440 

-- correct these in clusterbysenderfullname
-- Antoine Brodin.FreeBSD
-- Miss Islington (bot
-- solipsis at pitrou.net

-- SELECT pep, messageid, EMAIL, fromline, author, sendername, senderfullname, clusterbysenderfullname FROM allmessages
UPDATE allmessages SET clusterbysenderfullname = sendername 
WHERE sendername NOT LIKE '%utf%' AND sendername NOT LIKE '%iso%' AND sendername NOT LIKE '%=%' AND LENGTH(sendername) >0 AND clusterbysenderfullname IS NULL 
AND email like CONCAT('%(', author, ')%') 
-- AND pep <> -1 -- AND sendername = REPLACE(author,'.',' ') 
AND author <> 'noreply' AND author <> 'report' AND author <> 'sourceforge.net';
AND senderfullname <> 'noreply' AND senderfullname <> 'SourceForge net'

-- 372 remaining
UPDATE allmessages SET clusterbysenderfullname = REPLACE(author,'.','') 
-- SELECT pep, messageid, EMAIL, fromline, author, sendername, senderfullname, clusterbysenderfullname -- , authorsrole, authorsrole2020
-- FROM allmessages 
--  PEP <> -1 AND
WHERE  clusterbysenderfullname IS NULL 
AND email LIKE '%gregory.lielens%' AND author = 'gregory.lielens'
-- we add this part in the end to reduce the resultset to see whoch ones are left
AND author <> 'noreply' AND author <> 'report' AND author <> 'sourceforge.net'
AND senderfullname <> 'noreply' AND senderfullname <> 'SourceForge net'
-- 24 rows

UPDATE allmessages SET clusterbysenderfullname = 'Martin von Loewis' 
-- SELECT pep, messageid, EMAIL, fromline, author, sendername, senderfullname, clusterbysenderfullname 
-- FROM allmessages 
--  PEP <> -1 AND
WHERE clusterbysenderfullname IS NULL 
AND email LIKE '%From martin at v.loewis.de%' AND author = 'martin'
AND author <> 'noreply' AND author <> 'report' AND author <> 'sourceforge.net';
AND senderfullname <> 'noreply' AND senderfullname <> 'SourceForge net'
-- 214 rows

-- SELECT * FROM allmessages WHERE pep <> -1 and clusterbysenderfullname LIKE '%Langa%' LIMIT 1

UPDATE allmessages SET clusterbysenderfullname = 'lukasz langa' 
-- SELECT pep, messageid, EMAIL, fromline, author, sendername, senderfullname, clusterbysenderfullname 
-- FROM allmessages --  PEP <> -1 AND
WHERE clusterbysenderfullname IS NULL 
AND email LIKE '%From lukasz at langa.pl%' AND author = 'lukasz'
AND author <> 'noreply' AND author <> 'report' AND author <> 'sourceforge.net'
AND senderfullname <> 'noreply' AND senderfullname <> 'SourceForge net'
-- 19 ROWS

-- some charatecter issues in string
SELECT clusterbysenderfullname FROM allmessages WHERE clusterbysenderfullname LIKE '%langa%' limit 1000
UPDATE allmessages SET clusterbysenderfullname ='Lukasz Langa' 	WHERE clusterbysenderfullname = 'lukasz langa'
SELECT * FROM pepdetails WHERE pep = 563
SELECT * FROM allmessages WHERE pep  = 563 and clusterbysenderfullname LIKE '%langa%'
--UPDATE allmessages set authorsrole = 'proposalAuthor' WHERE pep = 563 and clusterbysenderfullname LIKE '%langa%'

-- peps where guido did not participate
SELECT DISTINCT PEP from allpepstillbdfl  -- all 466 peps
WHERE PEP NOT IN (SELECT DISTINCT pep FROM allmessages WHERE clusterbysenderfullname LIKE '%guido%') -- minus those without guido
-- 527, 543, 551, 556, 566, 569, 570, 571, 574, 578, 801, 3102,3143
-- peps where we cannot find any proposal authprs
SELECT * FROM pepstates_danieldata WHERE pep IN -- check which states they are in
( 
	SELECT DISTINCT PEP from allpepstillBDFL  
	WHERE PEP NOT IN (SELECT DISTINCT pep FROM allmessages WHERE authorsrole LIKE '%proposalauthor%')
)
-- "PEP", 3 ,13 210, 220,244,276,281,286,299,316,332,336,337,345,347,353,355,379,382,383,384,393,459,482,497,503,522,542,563,569,570,578,3111,3112,3120,3121,3123,3127,3131,3134,
SELECT * FROM pepdetails WHERE author LIKE '%martin%' -- pep = 3131
-- lets see why guido did not participate in those 13 peps, one by one
-- fromline,
SELECT pep, datetimestamp, messageid, email,  author, sendername, senderfullname, clusterbysenderfullname, authorsrole -- , authorsrole2020
	-- , inreplyto, inreplytouser, inreplytouserusingclusteredsender, inreplytouserusingclusteredsenderrole 
FROM allmessages 
WHERE PEP = 276 ORDER BY datetimestamp -- AND clusterbysenderfullname LIKE '%guido%'
SELECT * FROM pepstates_danieldata WHERE pep = 276
SELECT * FROM allmessages WHERE pep = 569 AND messageid = 8698914;
-- main query to see each peprson each time
SELECT pep, originalpepnumber, messageid, EMAIL, fromline, author, sendername, senderfullname, clusterbysenderfullname -- , authorsrole, authorsrole2020
	-- , inreplyto, inreplytouser, inreplytouserusingclusteredsender, inreplytouserusingclusteredsenderrole 
FROM allmessages 
WHERE  PEP <> -1 AND 
clusterbysenderfullname IS NULL 
-- we add this part in the end to reduce the resultset to see whoch ones are left
AND author <> 'noreply' AND author <> 'report' AND author <> 'sourceforge.net';
AND senderfullname <> 'noreply' AND senderfullname <> 'SourceForge net'
-- AND author LIKE '%%ivan.levkivskyi%%'
-- AND (fromline LIKE '%guido van rossum%' or author LIKE '%guido van rossum%')

--march 08 correctiosn from the above
UPDATE allmessages SET clusterbysenderfullname = 'Martin von Loewis' WHERE clusterbysenderfullname = 'Martin v Loewis';
SELECT * FROM pepdetails WHERE pep = 3112 -- author LIKE '%martin%'
-- we manually corrected the names in the resultset from the above sql

-- pep 459 and 503 have suthors who were also bdfl_delegates, so we just assign them to proposal authors
update allmessages set authorsrole = 'proposalAuthor' WHERE pep = 459 and authorsrole = 'bdfl_delegate'  
update allmessages set authorsrole = 'proposalAuthor' WHERE pep = 503 and authorsrole = 'bdfl_delegate' 

-- once associations are done
-- 06-mar 2021.. associate people again, as we have added so many 
-- one query instead of all three
UPDATE allmessages a1
JOIN allmessages a2 ON a2.emailMessageID = a1.inReplyTo
SET a1.inReplyToUser = a2.clusterbysenderfullname, a1.inReplyToUserUsingClusteredSender = a2.clusterbysenderfullname, a1.inReplyToUserUsingClusteredSenderRole = a2.authorsrole
WHERE a1.inReplyTo IS NOT NULL
-- Duration for 1 query: 00:59:33.7 */

-- do above individually...but will take time
-- associate inreplytouser
UPDATE allmessages a1
JOIN allmessages a2 ON a2.emailMessageID = a1.inReplyTo
SET a1.inReplyToUser = a2.clusterbysenderfullname
WHERE a1.inReplyTo IS NOT NULL
-- Duration for 1 query: 00:30:50.4 */
UPDATE allmessages a1
JOIN allmessages a2 ON a2.emailMessageID = a1.inReplyTo
SET a1.inReplyToUserUsingClusteredSender = a2.clusterbysenderfullname
WHERE a1.inReplyTo IS NOT NULL
-- Duration for 1 query: 01:32:20.8 */
UPDATE allmessages a1
JOIN allmessages a2 ON a2.emailMessageID = a1.inReplyTo
SET a1.inReplyToUserUsingClusteredSenderRole = a2.authorsrole
WHERE a1.inReplyTo IS NOT NULL
-- Duration for 1 query: 01:11:53.0 */

-- just checking if clusterbysenderfullname is correct for all guido
SELECT pep, messageid, fromline, author, sendername, senderfullname, clusterbysenderfullname -- , authorsrole, authorsrole2020
	-- , inreplyto, inreplytouser, inreplytouserusingclusteredsender, inreplytouserusingclusteredsenderrole 
FROM allmessages 
WHERE PEP <> -1 AND clusterbysenderfullname LIKE '%guido%'

-- just checking if we have assigned the folliwng author as bdfl delegate
SELECT pep, author, authorsrole2020 FROM allmessages
WHERE pep <> -1 and authorsrole2020 = 'bdfl_delegate'
-- AND author LIKE '%Just van Rossum%'
ORDER BY pep asc

-- see which messages for peps are null fpr authorsrole2020 field
SELECT pep, COUNT(*) 
FROM allmessages 
WHERE authorsrole2020 IS NULL AND pep <> -1
GROUP BY pep

-- 
SELECT pep, COUNT(*) 
FROM allmessages 
WHERE author LIKE '% %' AND pep <> -1
GROUP BY pep

-- where clusterbysenderfullname has not been assigned
SELECT pep, COUNT(*) 
FROM allmessages 
WHERE pep <> -1 and author IS NULL
GROUP BY pep

-- author and sendername seem to be the fileds we can use to link, 
-- lets just see if there are cases where these two are different
SELECT pep, COUNT(*) 
FROM allmessages 
WHERE pep <> -1 and author <> sendername
GROUP BY pep

SELECT originalpepnumber, pep, messageid, fromline, author,sendername, senderfullname, clusterbysenderfullname
FROM allmessages 
WHERE pep <> -1 and clusterbysenderfullname IS NULL

-- see which messages for peps are null fpr authorsrole2020 field
SELECT pep, COUNT(*) 
FROM allmessages 
WHERE clusterbysenderfullname IS NULL AND pep <> -1
GROUP BY pep

302, 306,316,328,335,338,342,343,345,352,370,373,384,397,420,425,426,432,448,463,468,484,487,489,
492,498,501,505,508,513,517,518,519,521,523,526,529,530,538,540,543,545,547,549,550,
552,553,554,555,557,560,561,562,563,564,567,572,580,3118,3131,3150,

UPDATE allmessages WHERE sendername = author WHERE pep = 539;

-- see the different sendernames adn se if they are correctly formed
DROP TABLE test23 
CREATE TABLE test23 AS SELECT DISTINCT sendername FROM allmessages 
SELECT * FROM test23 ORDER BY sendername

CREATE TABLE test24 AS SELECT DISTINCT sendername,clusterbysenderfullname  FROM allmessages 
SELECT * FROM test24 ORDER BY sendername

SELECT * FROM allmessages
WHERE pep <> -1
AND author LIKE '%proposalAuthor%'
OR author LIKE '%othercommunitymember%'
OR author LIKE '%coredeveloper%'

-- see if the bdfl delegate role has been assigned to any of the emails message sauthors
SELECT * FROM allmessages WHERE pep = 451 AND authorsrole = 'bdfl_delegate';
-- 208 rows
SELECT * FROM allmessages WHERE pep = 539 AND authorsrole = 'bdfl_delegate';
-- 8 rows
SELECT * FROM allmessages WHERE pep = 541 AND (author LIKE '%Mangoba%')
-- 2 rows
SELECT * FROM allmessages WHERE pep = 544 AND (author LIKE '%rossum%')
-- 18 rows
SELECT * FROM allmessages WHERE pep = 546 AND (author LIKE '%peterson%')
-- 3 rows
SELECT * FROM allmessages WHERE pep = 566 AND (author LIKE '%holth%')
-- 9 rows
SELECT * FROM allmessages WHERE pep = 571 AND (author LIKE '%coghlan%')

-- DROP TABLE test21;
CREATE TABLE test21 AS 
SELECT  pep -- , folder, messageid, author, authorsrole, clusterbysenderfullname, COUNT(messageid)
 FROM allmessages
WHERE -- authorsrole = 'proposalauthor' OR 
authorsrole = 'bdfl_delegate'
AND pep <> -1
GROUP BY pep -- , folder
ORDER BY pep ASC



-- messages for peps whih had authorsrole2020 as null before
-- see excel file
