-- jan 14th 2021, we add subject matching as well but later found out that its not a good ideas as thousans of messages get assigned to peps in messages at deeep levels
-- Jan 2021 pep number assignment to third level replies 

-- see how much difference ou first assignment of inreplyto mesages with a pep number did
SELECT COUNT(messageid) AS cnt FROM allmessages WHERE pep <> -1
-- 110201
SELECT COUNT(messageid) AS cnt FROM allmessages WHERE pepnum2020 <> -1
-- 113896
--Since there may be thord level replies, we run the script a third time to get even more messages if they were replies to the new pep numbers assigned to messages (the above step)

-- The two scrips below is better than the above. Results were stored in excel file for comparison

-- SELECT SUM(cnt) FROM (
	SELECT pep, COUNT(messageid) AS cnt FROM allmessages 
	WHERE PEP IN (
		SELECT DISTINCT PEP						
		FROM pepstates_danieldata_datetimestamp  
		WHERE (STATE LIKE '%acc%' OR state LIKE '%rej%'))	
	 GROUP BY pep; -- ) AS t;

-- 2. new pep num
SELECT pepnum2020, COUNT(messageid) AS cnt FROM allmessages 
	WHERE PEPnum2020 IN (
		SELECT DISTINCT PEP						
		FROM pepstates_danieldata_datetimestamp  
		WHERE (STATE LIKE '%acc%' OR state LIKE '%rej%'))	
	 GROUP BY pepnum2020;
	 
-- jan 2021..lets try to do this again..maybe we will get more replies
SELECT * from
	(SELECT b.pepnum2020 AS parent, a.pepnum2020 AS child 
	FROM allmessages a, allmessages b 
	WHERE b.emailmessageid = a.inreplyto AND b.pepnum2020 = -1) AS t
	WHERE t.child <> -1;
-- runinning this ansd trying to assign it the second time, i.e. second level of replies returns 5498 records	
-- thus we run this script
UPDATE allmessages a, allmessages b SET b.pepnum2020 = a.pepnum2020
WHERE b.emailmessageid = a.inreplyto AND b.pepnum2020 = -1;	 
-- says affected 1319 records, thne running it again says affected 682 records, then 4728 records...dont know why decreases then increases 
-- so i stopped after total excuting 4 times (in all of histroy I have just run it 4 times--- 3 times results shown above)
-- Now we run the script names 2. above.
-- running script 2 shows another 500 messages assigned


-- jan 14 2021..we use the subject matching as well, not always threads will match
-- this approach is not advisable -- try the select as shown below
-- SELECT * FROM subjectmatch2020 LIMIT 200;
-- i saw that many messages are now assigned a pep number which may be only mentiond once in some extreme reply level of one of the threads of the subject
-- thus it may not be ok tomassign all subjects the same ppe number as the papep number within one extreme level may be referrring to another pep,
-- not the current one being discussed

DROP TABLE subjectmatch2020;
CREATE TABLE subjectmatch2020 AS 
	SELECT a.pep AS apep, a.pepnum2020 AS apepnum2020, a.messageid AS amessageid, b.pep AS bpep, b.pepnum2020 AS bpepnum2020, b.subject, b.messageid AS bmessageid
	FROM allmessages a
	-- LEFT OUTER 
	INNER JOIN allmessages b ON a.subject = b.subject AND b.pepnum2020 = -1 AND a.pepnum2020 > 0;
--LIMIT 200;
-- WHERE
-- a.meta_value = 'abc'
-- AND b.post_id IS null
SELECT * FROM subjectmatch2020 LIMIT 200;
-- Found rows: 852,740 

-- UPDATE allmessages a, allmessages b SET b.pepnum2020 = a.pepnum2020
-- WHERE b.subject = a.subject AND b.pepnum2020 = -1;	

-- Previously, in the computations for the influence paper, we may not have assigned pep number to all in messages which share the same subject 
-- of those messages which we found to have a pep numberwhile in the analysis
-- we do this now in jan2021 for the influence fo roles paper
ALTER TABLE allmessages ADD pepnum2021 INT;
-- this was needed
ALTER TABLE `allmessages` ADD INDEX `pepnum2021_index` (`pepnum2021`);
-- SET
UPDATE allmessages SET pepnum2021 = pepnum2020;

