-- once the other script is done, the queries in this script are run...
-- the one to get proposalauthor values are in a different script



-- for messages which has pep as -1, some were replies to messages with pep number
-- so we set that here using th inreplyto columns
 
-- sample 
-- UPDATE all_distinctmessages AS a, (SELECT COUNT(messageID) as ct FROM all_distinctmessages WHERE inReplyTo = emailMessageID) AS p
-- SET a.countRepliesToThisMessage = p.ct
 
 -- SQL does not work
 -- UPDATE allmessages AS a, 
 -- (SELECT pep FROM allmessages m WHERE m.emailmessageid = a.inreplyto LIMIT 1) AS b
 -- SET a.pepnum2020 = b.pep
 
UPDATE allmessages a, allmessages b SET b.pepnum2020 = a.pep
WHERE b.emailmessageid = a.inreplyto AND b.pepnum2020 = -1;
-- this was needed
ALTER TABLE `allmessages` ADD INDEX `pepnum2020_index` (`pepnum2020`)
-- reset
UPDATE allmessages SET pepnum2020 = pep;

-- see if we found new rows
-- old pep num
SELECT pep, COUNT(messageid) FROM allmessages WHERE pep = 308 GROUP BY pep;
-- 2735
-- new pep num
SELECT pepnum2020, COUNT(messageid) FROM allmessages WHERE pepnum2020 = 308 GROUP BY pepnum2020;
-- 2762
--so we found a little bit more messages, for pep 308 we got 27 messagess

-- jan 2021, we can run it a second time to assign pep numbers to messages whch are replies to messages whoch we just assigned a pep number using the sql above

-- Results --------------------------------------------------------------------------------------------------------------------------------------------------------------------


-- DESC allmessages

-- try for this spcific query... seeders
SELECT pep, author, COUNT(messageid) AS totalmsgs,
  SUM(CASE WHEN inReplyTo IS NULL THEN 1 ELSE 0 END) As msg_which_are_not_replies, -- inReplyToUserUsingClusteredSender
  SUM(CASE WHEN inReplyTo IS NOT NULL THEN 1 ELSE 0 END) AS msg_which_are_replies
FROM allmessages
WHERE pep = 308
AND (folder LIKE '%dev%' OR folder LIKE '%ideas%')
GROUP BY author
ORDER BY msg_which_are_not_replies DESC

-- we have to combine with the below query
SELECT pep, inReplyToUserUsingClusteredSender, COUNT(inReplyTo) AS totalinReplyTo  -- inReplyTo, 
FROM allmessages
WHERE pep = 308
AND (folder LIKE '%dev%' OR folder LIKE '%ideas%')
GROUP BY inReplyToUserUsingClusteredSender  
ORDER BY totalinReplyTo desc
--- inReplyToUserUsingClusteredSender,
--  SUM(CASE WHEN inReplyTo IS NULL THEN 1 ELSE 0 END) As msg_which_are_not_replies, -- inReplyToUserUsingClusteredSender
--  SUM(CASE WHEN inReplyTo IS NOT NULL THEN 1 ELSE 0 END) AS msg_which_are_replies
-- inReplyToUserUsingClusteredSender, 

-- Final joining of the above query
SELECT * 
FROM (SELECT pep, author, COUNT(messageid) AS totalmsgs,
  			SUM(CASE WHEN inReplyTo IS NULL THEN 1 ELSE 0 END) As msg_which_are_not_replies, -- inReplyToUserUsingClusteredSender
  			SUM(CASE WHEN inReplyTo IS NOT NULL THEN 1 ELSE 0 END) AS msg_which_are_replies
		FROM allmessages
		WHERE pep = 308
		AND (folder LIKE '%dev%' OR folder LIKE '%ideas%')
		GROUP BY author
		ORDER BY msg_which_are_not_replies DESC) AS tbA
LEFT JOIN (SELECT inReplyToUserUsingClusteredSender, COUNT(inReplyTo) AS totalinReplyTo  -- inReplyTo, 
				FROM allmessages
				WHERE pep = 308
				AND (folder LIKE '%dev%' OR folder LIKE '%ideas%')
				GROUP BY inReplyToUserUsingClusteredSender  
				ORDER BY totalinReplyTo desc) as tbC
ON tbA.author = tbC.inReplyToUserUsingClusteredSender

-- just to test this on pep 308
SELECT pep, author, COUNT(messageid) AS totalmsgs,
  SUM(CASE WHEN inReplyTo IS NULL THEN 1 ELSE 0 END) As msg_which_are_not_replies, -- inReplyToUserUsingClusteredSender
  SUM(CASE WHEN inReplyTo IS NOT NULL THEN 1 ELSE 0 END) AS msg_which_are_replies
FROM allmessages
WHERE pep = 308
AND (folder LIKE '%dev%' OR folder LIKE '%ideas%')
GROUP BY author
ORDER BY msg_which_are_not_replies DESC

-- we have to combine with the below query
SELECT pep, inReplyToUserUsingClusteredSender, COUNT(inReplyTo) AS totalinReplyTo  -- inReplyTo, 
FROM allmessages
WHERE pep = 308
AND (folder LIKE '%dev%' OR folder LIKE '%ideas%')
GROUP BY inReplyToUserUsingClusteredSender  
ORDER BY totalinReplyTo DESC

-- now test the results 
SELECT *  -- author, -- *
-- author, totalmsgs AS TotalMsgByPerson, totalInReplyTo AS TotalMsgByPerson_whichGotReplies, msgs_not_replies AS TotalMsg_whichGotNoReplies 
from communicationsforseeders
ORDER BY totalmsgs desc

-- SELECT author, state , peptype, totalmsgs, msgs_not_replies, author_again, totalinreplyto, summessages, dominance_index
-- from communicationsforseeders
-- GROUP BY author, peptype, state
-- ORDER BY totalmsgs DESC

-- values for each member -- values summed for all peps
SELECT author, SUM(totalmsgs) AS SUM_totalmsgs, SUM(msgs_not_replies), SUM(totalinreplyto) -- , SUM(summessages) -- , dominance_index
from communicationsforseeders
GROUP BY author -- , peptype, state
ORDER BY SUM_totalmsgs DESC

-- COMBINE AUTHORS
UPDATE communicationsforseeders SET author = 'Nick Coghlan' WHERE author = 'nick.coghlan';
UPDATE communicationsforseeders SET author = 'Guido van Rossum' WHERE author = 'guido.van.rossum'; 
UPDATE communicationsforseeders SET author = 'Barry A. Warsaw' WHERE author = 'Barry Warsaw'; 
UPDATE communicationsforseeders SET author = 'Chris Angelico' WHERE author = 'chris.angelico'; 
UPDATE communicationsforseeders SET author = 'Paul Moore' WHERE author = 'paul.moore'; 
UPDATE communicationsforseeders SET author = 'Victor Stinner' WHERE author = 'victor.stinner';
UPDATE communicationsforseeders SET author = 'Antoine Pitrou' WHERE author = 'antoine.pitrou';
UPDATE communicationsforseeders SET author = 'Tim Peters' WHERE author =	'tim.peters';
-- UPDATE communicationsforseeders SET author = 'Victor Stinner' WHERE author = 'ivan.levkivskyi';
--	'steven.d'aprano'
UPDATE communicationsforseeders SET author = 'Eric V. Smith' WHERE author = 'eric.v.smith';
UPDATE communicationsforseeders SET author = 'Barry A. Warsaw' WHERE author = 'barry.warsaw';
-- UPDATE communicationsforseeders SET author = 'Victor Stinner' WHERE author = 'serhiy.storchaka';
-- UPDATE communicationsforseeders SET author = 'Victor Stinner' WHERE author =	'lukasz.langa'
-- UPDATE communicationsforseeders SET author = 'Victor Stinner' WHERE author =	'lukasz'
UPDATE communicationsforseeders SET author = 'Brett Cannon' WHERE author =	'brett.cannon';
-- UPDATE communicationsforseeders SET author = 'Victor Stinner' WHERE author =	'nathaniel.smith'
UPDATE communicationsforseeders SET author = 'Greg Ewing' WHERE author =	'greg.ewing';
UPDATE communicationsforseeders SET author = 'Terry Reedy' WHERE author =	'terry.reedy';
-- UPDATE communicationsforseeders SET author = 'Victor Stinner' WHERE author =	'Martin v. Loewis' ?? can we find ANY others

-- JAN 2021 as above, we saw the same problems in the ammlessages table...some authors were not combined BECAUSE THE EMAIL address were totally different, i.e. python trackers
-- so we combine these
-- COMBINE AUTHORS
UPDATE allmessages SET clusterbysenderfullname = 'Nick Coghlan' 		WHERE clusterbysenderfullname IS NULL AND author = 'nick.coghlan';
UPDATE allmessages SET clusterbysenderfullname ='Guido van Rossum' 	WHERE clusterbysenderfullname IS NULL AND author = 'guido.van.rossum'; 
UPDATE allmessages SET clusterbysenderfullname = 'Barry A. Warsaw' 	WHERE clusterbysenderfullname IS NULL AND author = 'Barry Warsaw'; 
UPDATE allmessages SET clusterbysenderfullname = 'Barry A. Warsaw' 	WHERE clusterbysenderfullname IS NULL AND author = 'barry.a.warsaw';		
UPDATE allmessages SET clusterbysenderfullname = 'Chris Angelico' 	WHERE clusterbysenderfullname IS NULL AND author = 'chris.angelico'; 
UPDATE allmessages SET clusterbysenderfullname = 'Paul Moore' 			WHERE clusterbysenderfullname IS NULL AND author = 'paul.moore'; 
UPDATE allmessages SET clusterbysenderfullname = 'Victor Stinner' 	WHERE clusterbysenderfullname IS NULL AND author = 'victor.stinner';
UPDATE allmessages SET clusterbysenderfullname = 'Antoine Pitrou' 	WHERE clusterbysenderfullname IS NULL AND author = 'antoine.pitrou';
UPDATE allmessages SET clusterbysenderfullname ='Tim Peters' 			WHERE clusterbysenderfullname IS NULL AND author =	'tim.peters';
-- UPDATE communicationsforseeders SET author = 'Victor Stinner' WHERE author = 'ivan.levkivskyi';
--	'steven.d'aprano'
UPDATE allmessages SET clusterbysenderfullname = 'Eric V. Smith' 		WHERE clusterbysenderfullname IS NULL AND author = 'eric.v.smith';
UPDATE allmessages SET clusterbysenderfullname ='Barry A. Warsaw' 	WHERE clusterbysenderfullname IS NULL AND author = 'barry.warsaw';
-- UPDATE communicationsforseeders SET author = 'Victor Stinner' WHERE author = 'serhiy.storchaka';
-- UPDATE communicationsforseeders SET author = 'Victor Stinner' WHERE author =	'lukasz.langa'
-- UPDATE communicationsforseeders SET author = 'Victor Stinner' WHERE author =	'lukasz'
UPDATE allmessages SET clusterbysenderfullname = 'Brett Cannon' 		WHERE clusterbysenderfullname IS NULL AND author =	'brett.cannon';
-- UPDATE communicationsforseeders SET author = 'Victor Stinner' WHERE author =	'nathaniel.smith'
UPDATE allmessages SET clusterbysenderfullname = 'Greg Ewing' 			WHERE clusterbysenderfullname IS NULL AND author =	'greg.ewing';
UPDATE allmessages SET clusterbysenderfullname = 'Terry Reedy' 		WHERE clusterbysenderfullname IS NULL AND author =	'terry.reedy';
-- more combinations needed
UPDATE allmessages SET clusterbysenderfullname ='Barry A. Warsaw' 	WHERE clusterbysenderfullname = 'Barry A Warsaw';
UPDATE allmessages SET clusterbysenderfullname ='Georg Brandl' 	WHERE clusterbysenderfullname IS NULL AND author = 'georg.brandl';
UPDATE allmessages SET clusterbysenderfullname ='Richard A Jones' 	WHERE clusterbysenderfullname IS NULL AND author = 'richard.jones';
UPDATE allmessages SET clusterbysenderfullname ='Benjamin Peterson' 	WHERE clusterbysenderfullname IS NULL AND author = 'benjamin.peterson';
UPDATE allmessages SET clusterbysenderfullname ='INADA Naoki' 	WHERE clusterbysenderfullname IS NULL AND author = 'inada.naoki';
UPDATE allmessages SET clusterbysenderfullname ='Donald Stufft' 	WHERE clusterbysenderfullname IS NULL AND author = 'donald.stufft';
UPDATE allmessages SET clusterbysenderfullname ='Eric Snow' 	WHERE clusterbysenderfullname IS NULL AND author = 'eric.snow';
UPDATE allmessages SET clusterbysenderfullname ='Mark Shannon' 	WHERE clusterbysenderfullname IS NULL AND author = 'mark.shannon';
-- 01 march ...important one..needs to be done
UPDATE allmessages SET clusterbysenderfullname ='Raymond Hettinger' 	WHERE author = 'raymond.hettinger';
-- not needed... UPDATE allmessages SET clusterbysenderfullname ='Raymond Hettinger' 	WHERE author = 'rdhettinger';
UPDATE allmessages SET clusterbysenderfullname ='Raymond Hettinger' 	WHERE clusterbysenderfullname = 'rdhettinger';

-- march 2021..lets make this easier
UPDATE allmessages SET clusterbysenderfullname ='Barry Warsaw' 	WHERE clusterbysenderfullname = 'Barry A. Warsaw';

UPDATE allmessages SET clusterbysenderfullname ='Skip Montanaro' 	WHERE clusterbysenderfullname = 'skip' and author = 'Skip Montanaro';
UPDATE allmessages SET clusterbysenderfullname ='Aahz Maruch' 	WHERE clusterbysenderfullname = 'Aahz' and author = 'Aahz';
UPDATE allmessages SET clusterbysenderfullname ='Sean Ross' 	WHERE clusterbysenderfullname = 'Cross' and author = 'Sean Ross';
UPDATE allmessages SET clusterbysenderfullname ='Greg Ewing' 	WHERE clusterbysenderfullname = 'Gregarius' and author = 'Greg Ewing';
UPDATE allmessages SET clusterbysenderfullname ='A.M. Kuchling' 	WHERE clusterbysenderfullname = 'akuchlin' and author = 'A.M. Kuchling';
UPDATE allmessages SET clusterbysenderfullname ='Andrew Kuchling' 	WHERE clusterbysenderfullname = 'A.M. Kuchling';


SELECT pep, author, fromline, clusterbysenderfullname 
FROM allmessages WHERE clusterbysenderfullname LIKE '%hettinger%' 
AND pep <> -1;

CREATE or replace VIEW top10authors AS
	SELECT DISTINCT AUTHOR, SUM(totalmsgs) AS S FROM communicationsforseeders group by author ORDER BY s DESC LIMIT 20;

-- Now we change pep to pepnum2020 and author to clusterbysenderfullname for the main cursoe queries above
SELECT * FROM  communicationsforseeders
-- NOW MAIN QUERY ON THE RESULTS
-- First, FOR ALL MEMBERS
SELECT pep, LOWER(author), peptype, state, SUM(totalmsgs) AS TotalMsgsByPerson, SUM(msgs_not_replies) AS TotalTopLevelMsgsByPerson, 
SUM(msgs_are_replies) AS TotalRepliesByPersonToDiffMsgs, 
SUM(totalinreplyto) AS TotalRepliesToThisPerson, SUM(summessages) AS TotalMsgsForPEP -- , dominance_index
from communicationsforseeders
-- WHERE author IN (SELECT author FROM top10authors)
GROUP BY pep, LOWER(author) -- , peptype, state
ORDER BY TotalMsgsByPerson DESC -- (SELECT AUTHOR FROM communicationsforseeders ORDER BY SUM(totalmsgs) ) DESC

SELECT count(distinct pep) -- , authorsrole
FROM allmessages where authorsrole2020 = 'bdfl_delegate'
-- GROUP BY pep

-- see where we going wrong
SELECT distinct pep -- , authorsrole
FROM communicationsforseeders
where author = 'proposalAuthor' ORDER BY pep asc


-- 01 March 2021
-- main script to get the computed cursor (previous script) results in the correct format
-- Second, only for top 20 by now grouping by for values for each member -- values summed for all peps
-- ANOTHER PART OF THE MAIN QUERY ... 
-- ALTER TABLE communicationsforseeders ADD COLUMN folder TEXT(30);
-- remove the folder column in this query and in the main cursor, both for top members andfor proposal author
				-- peptype,							-- state
SELECT author, peptype,
	COUNT(distinct pep) AS pepInvolvement, SUM(totalmsgs) AS TotalMsgByPerson, 	-- SUM(totalmsgs)/COUNT(pep) AS AVEG, 
	SUM(msgs_not_replies) AS TotalTopLevelMsgsByPerson, 	-- SUM(msgs_not_replies)/COUNT(pep) AS AVEG2, 
	SUM(msgs_are_replies) AS TotalRepliesByPersonToDiffMsgs, 	-- SUM(msgs_are_replies)/COUNT(pep) AS AVEG3,
	SUM(totalinreplyto) AS TotalRepliesToThisPerson 	-- SUM(totalinreplyto)/COUNT(pep) AS AVEG4,
	-- SUM(summessages) AS TotalMsgsForPEP -- , dominance_index
from communicationsforseeders
-- WHERE author IN (SELECT author FROM top10authors)
WHERE (author IN (SELECT author FROM top10authors) OR author = 'proposalauthor'  OR author = 'bdfl_delegate' )
	GROUP BY author, peptype -- , folder -- , peptype -- , state -- , peptype, state
	ORDER BY pepInvolvement DESC, TotalMsgByPerson DESC

SELECT DISTINCT pep FROM pepdetails WHERE created < '2018-07-11'
SELECT pep, author, bdfl_delegate FROM pepdetails WHERE created < '2018-07-11' and author LIKE '%guido%'

-- 01 march 2021 checking if proposal author assignemnt is correct
-- basic check
SELECT DISTINCT allmessages.pep, allmessages.authorsrole, allmessages.clusterbysenderfullname -- ,  pepdetails.bdfl_delegate 
	FROM allmessages WHERE pep <> -1 and authorsrole = 'proposalAuthor' ORDER BY pep ASC
-- just if duplicates
SELECT  * FROM 
(
	SELECT DISTINCT allmessages.pep, allmessages.authorsrole, allmessages.clusterbysenderfullname , pepdetails.author, pepdetails.bdfl_delegate 
	FROM allmessages 
	-- WHERE allmessages.pep <> -1 and allmessages.authorsrole2020 = 'proposalAuthor'
	INNER JOIN pepdetails ON allmessages.pep =  pepdetails.pep
	ORDER BY pep ASC
) AS t
WHERE t.pep <> -1 and t.authorsrole = 'proposalAuthor' and pep = 554

AND clusterbysenderfullname <> author AND REPLACE(author,',',' ') NOT LIKE CONCAT('%',clusterbysenderfullname,'%') -- make it easier to see which ones are different
-- AND author NOT LIKE 'clusterbysenderfullname%' AND author NOT LIKE '%clusterbysenderfullname' 
AND LENGTH(clusterbysenderfullname) > 2

UPDATE allmessages SET clusterbysenderfullname ='Phillip Eby' 	WHERE clusterbysenderfullname = 'Phillip J Eby';

UPDATE allmessages SET clusterbysenderfullname ='Talin' 	WHERE clusterbysenderfullname = 'Balin' AND pep = 3101 OR pep = 3102;
UPDATE allmessages SET clusterbysenderfullname ='Roman Suzi' 	WHERE clusterbysenderfullname = 'Roman Susi';
UPDATE allmessages SET clusterbysenderfullname ='Philipp Angerer' 	WHERE clusterbysenderfullname = 'Philipp A';
UPDATE allmessages SET clusterbysenderfullname ='Lukasz Langa' 	WHERE clusterbysenderfullname = 'lukasz';
UPDATE allmessages SET clusterbysenderfullname ='Tim Peters' 	WHERE clusterbysenderfullname = 'Tim peters';
UPDATE allmessages SET clusterbysenderfullname ='Vinay Sajip' 	WHERE clusterbysenderfullname = 'vinay sajip';
UPDATE allmessages SET clusterbysenderfullname ='Christopher Barker' 	WHERE clusterbysenderfullname = 'Chris Barker';
UPDATE allmessages SET clusterbysenderfullname ='Tarek Ziade' 	WHERE clusterbysenderfullname = 'Tarek Ziad';
UPDATE allmessages SET clusterbysenderfullname ='Aahz Maruch' 	WHERE clusterbysenderfullname = 'Aahz';
UPDATE allmessages SET clusterbysenderfullname ='Greg Wilson' 	WHERE clusterbysenderfullname = 'Greg Tilson'; 
UPDATE allmessages SET clusterbysenderfullname ='Jim Jewett' 	WHERE clusterbysenderfullname = 'Jim J Jewett'; 
UPDATE allmessages SET clusterbysenderfullname ='Terry Reedy' 	WHERE clusterbysenderfullname = 'Terry J Reedy'; 
UPDATE allmessages SET clusterbysenderfullname ='Travis Oliphant' 	WHERE clusterbysenderfullname = 'Travis E. Oliphant'; 
UPDATE allmessages SET clusterbysenderfullname ='Gregory Lielens' 	WHERE clusterbysenderfullname = 'gregorylielens'; 
UPDATE allmessages SET clusterbysenderfullname ='Oleg Broytman' 	WHERE clusterbysenderfullname = 'Oleg Broytmann'; --  and author = 'A.M. Kuchling';
UPDATE allmessages SET clusterbysenderfullname ='Phillip Eby' 	WHERE clusterbysenderfullname = 'Phillip J. Eby';
UPDATE allmessages SET clusterbysenderfullname ='Mark Haase' 	WHERE clusterbysenderfullname = 'Mark E. Haase';
UPDATE allmessages SET clusterbysenderfullname ='Sebastian Kreft' 	WHERE clusterbysenderfullname = 'Sebastian Kraft';

SELECT pep FROM pepdetails WHERE author LIKE '%guido%'
SELECT pep, messageid, fromline, clusterbysenderfullname, authorsrole FROM allmessages  WHERE  pep = 279; -- clusterbysenderfullname LIKE '%balin%' AND
SELECT pep, author, sendername, senderfullname, clusterbysenderfullname, authorsrole FROM allmessages  WHERE clusterbysenderfullname LIKE '%balin%' AND pep = 3101;
SELECT pep, messageid, email, fromline, author, sendername, senderfullname, clusterbysenderfullname, authorsrole FROM allmessages  WHERE clusterbysenderfullname LIKE '%p%' AND pep = 324;
SELECT pep, messageid, email, fromline, author, sendername, senderfullname, clusterbysenderfullname, authorsrole FROM allmessages  WHERE clusterbysenderfullname LIKE '%g%' AND pep = 343;
SELECT pep, messageid, email, fromline, author, sendername, senderfullname, clusterbysenderfullname, authorsrole FROM allmessages  WHERE  pep = 517 and clusterbysenderfullname IS null;

SELECT * FROM allmessages WHERE pep = 517 AND messageid = 8307525

-- checking if bdfl_delegate assignemnt is correct
-- basic check
SELECT DISTINCT allmessages.pep, allmessages.authorsrole, allmessages.clusterbysenderfullname -- ,  pepdetails.bdfl_delegate 
	FROM allmessages WHERE pep <> -1 and authorsrole = 'bdfl_delegate' ORDER BY pep asc
-- just if duplicates
SELECT  * FROM 
(
	SELECT DISTINCT allmessages.pep, allmessages.authorsrole, allmessages.clusterbysenderfullname ,  pepdetails.bdfl_delegate 
	FROM allmessages 
	-- WHERE allmessages.pep <> -1 and allmessages.authorsrole2020 = 'proposalAuthor'
	INNER JOIN pepdetails ON allmessages.pep =  pepdetails.pep
	ORDER BY pep ASC
) AS t
WHERE t.pep <> -1 and t.authorsrole = 'bdfl_delegate' AND t.bdfl_delegate IS NOT null
AND clusterbysenderfullname <> bdfl_delegate AND REPLACE(bdfl_delegate,',',' ') NOT LIKE CONCAT('%',clusterbysenderfullname,'%') -- make it easier to see which ones are different
--AND bdfl_delegate NOT LIKE 'clusterbysenderfullname%' AND author NOT LIKE '%clusterbysenderfullname' 

SELECT 

-- 07 march correcting for uniformity
UPDATE allmessages SET clusterbysenderfullname ='Richard Jones' 	WHERE clusterbysenderfullname = 'Richard A Jones';
UPDATE allmessages SET clusterbysenderfullname ='Charles-Francois Natali' 	WHERE clusterbysenderfullname = 'cf natali';
UPDATE allmessages SET clusterbysenderfullname ='Charles-Francois Natali' 	WHERE clusterbysenderfullname = 'cf natali';
-- Victor Stinner
UPDATE allmessages SET clusterbysenderfullname ='Victor Stinner' 	WHERE clusterbysenderfullname = 'Stinner Victor';
UPDATE allmessages SET clusterbysenderfullname ='Nick Coghlan' 	WHERE clusterbysenderfullname = 'nick coghlan';
UPDATE allmessages SET clusterbysenderfullname ='Marc-Andre Lemburg' 	WHERE clusterbysenderfullname = 'M -A Lemburg'; 
UPDATE allmessages SET clusterbysenderfullname ='Marc-Andre Lemburg' 	WHERE clusterbysenderfullname =	'M.-A. Lemburg';
UPDATE allmessages SET clusterbysenderfullname ='Fred Drake' 	WHERE clusterbysenderfullname = 'Jr Fred Drake'; 
UPDATE allmessages SET clusterbysenderfullname ='Fred Drake' 	WHERE clusterbysenderfullname = 'Fred L Drake'; 
UPDATE allmessages SET clusterbysenderfullname ='Fred Drake' 	WHERE clusterbysenderfullname = 'Jr Fred L Drake';

-- UPDATE allmessages SET clusterbysenderfullname ='A.M. Kuchling' 	WHERE clusterbysenderfullname = 'akuchlin' and author = 'A.M. Kuchling';
UPDATE allmessages SET clusterbysenderfullname ='Andrew Kuchling' 	WHERE clusterbysenderfullname = 'andrew.m.kuchling';
UPDATE allmessages SET clusterbysenderfullname ='Phillip Eby' 	WHERE clusterbysenderfullname = 'phillip eby';
UPDATE allmessages SET clusterbysenderfullname ='Tarek Ziade' 	WHERE clusterbysenderfullname = 'ziade tarek';
UPDATE allmessages SET clusterbysenderfullname ='Terry Reedy' 	WHERE clusterbysenderfullname = 'Terry J. Reedy';
UPDATE allmessages SET clusterbysenderfullname ='Eric Smith' 	WHERE clusterbysenderfullname = 'eric smith';
UPDATE allmessages SET clusterbysenderfullname ='Steven Aprano' 	WHERE clusterbysenderfullname = 'Steven DAprano';
UPDATE allmessages SET clusterbysenderfullname ='Steven Aprano' 	WHERE clusterbysenderfullname = 'Steven D\'Aprano';
UPDATE allmessages SET clusterbysenderfullname ='Brett Cannon' 	WHERE clusterbysenderfullname = 'Dr Brett Cannon';
UPDATE allmessages SET clusterbysenderfullname = 'Matthew Barnett' WHERE clusterbysenderfullname = 'MRAB';
UPDATE allmessages SET clusterbysenderfullname = 'Travis Oliphant' WHERE clusterbysenderfullname = 'Travis E Oliphant';
UPDATE allmessages SET clusterbysenderfullname = 'Inada Naoki' WHERE clusterbysenderfullname = 'INADA Naoki';
-- SELECT pep, author, bdfl_delegate FROM pepdetails WHERE created < '2018-07-11' and author LIKE '%guido%'

-- some problems in assignment of roles , its based on fromline and thus needs to be corrceted
UPDATE allmessages SET authorsrole = NULL WHERE pep = 517
UPDATE allmessages SET authorsrole = NULL WHERE pep = 572

SELECT * FROM pepdetails WHERE pep = 572
SELECT DISTINCT authorsrole, clusterbysenderfullname from allmessages WHERE pep = 572 ORDER BY authorsrole

SELECT pep, messageid, EMAIL, fromline, author, authorsrole, authorsrole2020, sendername, senderfullname, clusterbysenderfullname 
FROM allmessages WHERE pep = 517 AND  clusterbysenderfullname = 'Greg Ewing'

-- the fromlone needs to be corrected
update allmessages set fromLine = substr(SUBSTRING_INDEX(email,'Date:',1), POSITION("From:" IN email))  
WHERE pep = -1 
-- AND  clusterbysenderfullname = 'Greg Ewing'

SELECT pep, originalpepnumber, messageid, EMAIL, fromline, author, sendername, senderfullname, clusterbysenderfullname -- , authorsrole, authorsrole2020
	-- , inreplyto, inreplytouser, inreplytouserusingclusteredsender, inreplytouserusingclusteredsenderrole 
FROM allmessages WHERE clusterbysenderfullname = 'Dr Brett Cannon'

SELECT Orders.OrderID, Customers.CustomerName, Orders.OrderDate
FROM Orders
INNER JOIN Customers ON Orders.CustomerID=Customers.CustomerID;

