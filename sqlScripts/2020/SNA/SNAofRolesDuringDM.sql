-- 23 dec 2020
-- by individuals
SELECT -- authorsrole, 
	author, inReplyToUserUsingClusteredSender 
-- , COUNT(messageid) AS cnt -- messageid, author, authorsrole, DATE2, datetimestamp
	FROM allmessages m
	WHERE PEP = 308 -- IN (SELECT PEP FROM accrejpeps)			
	-- AND DATE2 <= v_date2 -- (SELECT DATE2 FROM pepstates_danieldata_datetimestamp WHERE PEP = m.PEP AND (STATE LIKE 'acc' OR state LIKE '%rej%') order by DATE2 asc LIMIT 1)			
	AND folder LIKE '%dev%'
	AND LENGTH(sendername) > 0 AND LENGTH(inReplyToUserUsingClusteredSender) > 0 
--	GROUP BY authorsrole, author
--	ORDER BY cnt desc, authorsrole, author;
	ORDER BY author, inReplyToUserUsingClusteredSender;
	
-- dec 2020 ...for paper --- per pep, individual communications 
SELECT pep, author, inReplyToUserUsingClusteredSender 
   , COUNT(messageid) AS cnt -- messageid, author, authorsrole, DATE2, datetimestamp
	FROM allmessages m
	WHERE PEP = 308 -- IN (SELECT PEP FROM accrejpeps)			
	AND DATE2 <= (SELECT DATE2 FROM pepstates_danieldata_datetimestamp WHERE PEP = m.PEP AND (STATE LIKE 'acc' OR state LIKE '%rej%') order by DATE2 asc LIMIT 1)			
	AND folder LIKE '%dev%'
	AND LENGTH(sendername) > 0 AND LENGTH(inReplyToUserUsingClusteredSender) > 0 
	GROUP BY pep, author  -- authorsrole,
	ORDER BY cnt desc, author;  -- authorsrole, 
--	ORDER BY author, inReplyToUserUsingClusteredSender;	
	
CREATE TABLE 2020_memberroles AS
SELECT DISTINCT author, authorsrole FROM allmessages;	
SELECT DISTINCT author, authorsrole FROM allmessages LIMIT 5000; 
SELECT DISTINCT author, authorsrole FROM 2020_memberroles WHERE author LIKE '%guido%';	
-- SELECT * FROM allmembers LIMIT 5;
-- SELECT * FROM alldevelopers LIMIT 5;
SELECT * FROM distinctsenders;	

ALTER TABLE allmessages ADD inReplyToUserUsingClusteredSenderRole TEXT(20);
-- Previously, in the thesis, we may not have assigned pep number to all in reply messages of those messages which we found to have a pep numberwhile in the thesis
-- we do this now in dec 2020 for the influence fo roles paper
alter table allmessages add pepnum2020 INT;
/* Affected rows: 1,589,676  Found rows: 0  Warnings: 0  Duration for 1 query: 02:03:26.9 */
UPDATE allmessages SET pepnum2020 = pep; 
	
	-- by roles of the individuals
SELECT authorsrole, inReplyToUserUsingClusteredSender, (SELECT )
-- , COUNT(messageid) AS cnt -- messageid, author, authorsrole, DATE2, datetimestamp
	FROM allmessages m
	WHERE PEP = 308 -- IN (SELECT PEP FROM accrejpeps)			
	-- AND DATE2 <= v_date2 -- (SELECT DATE2 FROM pepstates_danieldata_datetimestamp WHERE PEP = m.PEP AND (STATE LIKE 'acc' OR state LIKE '%rej%') order by DATE2 asc LIMIT 1)			
	AND folder LIKE '%dev%'
	AND LENGTH(sendername) > 0 AND LENGTH(inReplyToUserUsingClusteredSender) > 0 
--	GROUP BY authorsrole, author
--	ORDER BY cnt desc, authorsrole, author;
	ORDER BY author, inReplyToUserUsingClusteredSender;
	
	
