-- feb 2021..we change this script for bips...to get the influence in each thread



SELECT * FROM allmessages LIMIT 15;
-- drop table subjects2020;
-- DROP TABLE bipthreads2021;
-- lets first create this table easily
CREATE table bipthreads2021 (id INT AUTO_INCREMENT PRIMARY KEY) AS
   -- distinct is just to make sure we dont get any duplicates -- dont worry it would not combine any of teh results 
	SELECT DISTINCT '1' AS threadlevel, bip, biptype, messageid, author, sendername, datetimestamp, emailmessageid, inreplyto, inreplytouser, subject, authorsrole -- ,  COUNT(messageid) AS cnt
	FROM allmessages
	WHERE inreplyto IS NULL -- we first look at threads top level
	AND bip IN (9,141,341,342) -- Any one of these `= 249'  OR `(SELECT DISTINCT pep FROM accrejpeps2)'
	-- we now consider all 466 PEPs instead of the 245 acc and rej ones
	-- 12 jan 2021...we include all 466 PEPs rather than the 245 PEPs
	-- (SELECT DISTINCT PEP FROM allpepstillBDFL WHERE PEP <> -1); -- AND PEP = 227);
-- GROUP BY pepnum2020; -- , peptype2020, subject, clusterbysenderfullname;
-- SELECT * FROM pepthreadsbyperson2020;

-- now we set the threadid to the id value
ALTER TABLE bipthreads2021 ADD COLUMN threadid INT;
UPDATE bipthreads2021 SET threadid = id;

-- now we select the distinct ones
-- CREATE table pepthreadsbyperson2020_derived as
--	SELECT DISTINCT threadlevel, pepnum2020, messageid, author, clusterbysenderfullname, emailmessageid, inreplyto, subject -- , authorsrole2020 -- ,  COUNT(messageid) AS cnt
--	FROM pepthreadsbyperson2020;

-- CREATE table pepthreadsbyperson_replies_2020 as
-- SELECT DISTINCT pepnum2020, messageid, author, clusterbysenderfullname, emailmessageid, subject -- , authorsrole2020 -- ,  COUNT(messageid) AS cnt
-- FROM allmessages
-- WHERE inreplyto = (SELECT emailmessageid FROM pepthreadsbyperson2020)
-- OR subject = (SELECT subject FROM pepthreadsbyperson2020) -- we first look at threads top level
-- AND pepnum2020 IN -- (SELECT DISTINCT pep FROM accrejpeps2)
-- -- we now consider all 466 PEPs instead of the 245 acc and rej ones
-- -- 12 jan 2021...we include all 466 PEPs rather than the 245 PEPs
-- 		(SELECT DISTINCT PEP FROM allpepstillBDFL WHERE PEP <> -1 AND PEP = 227);

-- DROP PROCEDURE getThreads_Per_Message;
-- DROP PROCEDURE createThreadInvolvementForEachPerson;
   -- First, to get the top level
   -- BELOW two commands not needed in how we have impleted in BIPs what we want
--   CALL getBIPThreads_Per_Message();
	-- Then this script needs to be run.. a recursive call to find more posts in these threads..may take much longer 
--	SET max_sp_recursion_depth=255;
	-- rather than recursive, we just do one by one,,bip number first
	SET @bipno = 342;  -- 9, 141, 341, 342 
	call getBIPThreads_Per_Message_from_secondlevel(@bipno, 1); 
	call getBIPThreads_Per_Message_from_secondlevel(@bipno, 2); 
	call getBIPThreads_Per_Message_from_secondlevel(@bipno, 3);
	call getBIPThreads_Per_Message_from_secondlevel(@bipno, 4);
	call getBIPThreads_Per_Message_from_secondlevel(@bipno, 5);
	-- until here should be fine
	call getBIPThreads_Per_Message_from_secondlevel(@bipno, 6);
	call getBIPThreads_Per_Message_from_secondlevel(@bipno, 7);
	call getBIPThreads_Per_Message_from_secondlevel(@bipno, 8);
	call getBIPThreads_Per_Message_from_secondlevel(@bipno, 9);
	
	-- SEE RESULTS..maybe more bips assigned here
	SELECT bip, threadlevel, messageid, author, sendername, emailmessageid, subject, threadid, authorsrole, biptype, inreplyto,  inreplytouser
			FROM bipthreads2021;
	
-- recursive function ...is called on its own
-- DROP PROCEDURE getThreads_Per_Message_from_secondlevel;
-- DROP PROCEDURE getBIPThreads_Per_Message_from_secondlevel;

--  SELECT bip, threadlevel, messageid, author, sendername, emailmessageid, subject, threadid, authorsrole, biptype FROM bipthreads2021

DELIMITER $$
CREATE PROCEDURE getBIPThreads_Per_Message_from_secondlevel (IN vin_bip INT(10), IN v_threadlevel INT(10))
BEGIN
	DECLARE finished INTEGER DEFAULT 0; DECLARE v_bip INTEGER DEFAULT 0; DECLARE v_messageId INTEGER DEFAULT 0; DECLARE v_author TEXT(15); DECLARE v_date DATE;
	DECLARE v_sendername TEXT(15); DECLARE v_emailmessageid TEXT(15); DECLARE v_subject TEXT(15); DECLARE v_threadid INTEGER;
	DECLARE v_inreplyto TEXT(15); DECLARE v_inreplytouser TEXT(15);  DECLARE v_authorsrole TEXT(20); 	DECLARE v_biptype TEXT(20);  
	DECLARE curPEPList 
			CURSOR FOR 
			-- get all threads at top level, and then in the next procedure we get replies to these threads
			SELECT bip, messageid, author, sendername, emailmessageid, subject, threadid, authorsrole, biptype, inreplyto, inreplytouser 
				FROM bipthreads2021 WHERE threadlevel = v_threadlevel AND bip = vin_bip; 		
				DECLARE CONTINUE HANDLER 
        		FOR NOT FOUND SET finished = 1;
OPEN curPEPList;

getpepsstate: 
		LOOP
			FETCH curPEPList INTO v_bip,v_messageid, v_author, v_sendername,  v_emailmessageid, v_subject, v_threadid, v_authorsrole, v_biptype, v_inreplyto, v_inreplytouser; 
			IF finished = 1 THEN 
				LEAVE getpepsstate;
			END IF;
			call createBIPThreadInvolvementForEachPerson_from_secondlevel (v_bip,v_messageid, v_author, v_sendername,  v_emailmessageid, 
																		v_subject, v_threadid, v_authorsrole, v_biptype, v_inreplyto, v_inreplytouser, v_threadlevel+1); -- add 1 to threadlevel, 
		
		END LOOP getpepsstate;
		CLOSE curPEPList;

END$$
DELIMITER ;

-- DROP PROCEDURE createBIPThreadInvolvementForEachPerson_from_secondlevel;
DELIMITER $$
CREATE PROCEDURE createBIPThreadInvolvementForEachPerson_from_secondlevel (IN v_bip INT(10), IN v_messageid INT(10), IN v_author TEXT(15), 
																		 IN v_sendername TEXT(20), IN v_emailmessageid TEXT (20), IN v_subject TEXT (20), IN v_threadid INT(10),
																		 IN v_authorsrole TEXT(20), IN v_biptype TEXT(20), 
																		 IN v_inreplyto TEXT(20), IN v_inreplytouser TEXT(20), IN v_threadLevel INT(10) )
BEGIN
	DECLARE finished INTEGER DEFAULT 0; 
	DECLARE vv_bip INTEGER DEFAULT 0; DECLARE vv_messageId INTEGER DEFAULT 0; DECLARE vv_author TEXT(15); DECLARE vv_date DATE;
	DECLARE vv_sendername TEXT(15); DECLARE vv_emailmessageid TEXT(15); DECLARE vv_subject TEXT(15);
	DECLARE vv_authorsrole TEXT(15); DECLARE vv_datetimestamp TIMESTAMP ; 
	DECLARE vv_inreplyto TEXT(15);  DECLARE vv_inreplytouser TEXT(15);
--	SET p_state 
	DECLARE curgetMessages 
			CURSOR FOR -- DISTINCT
				SELECT bip, messageid, author, sendername, emailmessageid, datetimestamp, subject, inreplyto, authorsrole, inreplytouser -- , authorsrole2020 -- ,  COUNT(messageid) AS cnt
					FROM allmessages 
					WHERE (inreplyto = v_emailmessageid )
					-- this would be too much noise in the data
			--		OR subject = v_subject ) -- we first look at threads top level
					AND bip = v_bip; -- IN (SELECT DISTINCT PEP FROM allpepstillBDFL ); -- WHERE PEP <> -1AND PEP = 227);
				-- declare NOT FOUND handler
				DECLARE CONTINUE HANDLER 
        		FOR NOT FOUND SET finished = 1;
	-- INTO OUTFILE 'c:\\scripts\\SNA2020\\SNAAllDevelopersAfterClustering_19-12-2020_h.txt';
OPEN curgetMessages;

getMessages: 
		LOOP
			FETCH curgetMessages INTO vv_bip,vv_messageid, vv_author, vv_sendername,  vv_emailmessageid, vv_datetimestamp, vv_subject, vv_inreplyto, vv_authorsrole, vv_inreplytouser; 
			IF finished = 1 THEN 
				LEAVE getMessages;
			END IF;
			-- INSERT INTO SAME TABLE 
			INSERT INTO bipthreads2021(threadlevel, bip, messageid, author, sendername,  emailmessageid, datetimestamp, subject, threadid, inreplyto, authorsrole, biptype, inreplytouser)
				VALUES (v_threadLevel, vv_bip,vv_messageid, vv_author, vv_sendername, vv_emailmessageid, vv_datetimestamp, vv_subject, v_threadid, vv_inreplyto, vv_authorsrole, v_biptype, 
				vv_inreplytouser);			
			-- the the intyegral part ...now it will go on in recursion
			-- call getThreads_Per_Message_recursive_from_secondlevel(v_threadLevel);
		END LOOP getMessages;
		CLOSE curgetMessages;

END$$
DELIMITER ;

-- associate inreplytouser
-- UPDATE allmessages a1
-- JOIN allmessages a2 ON a2.emailMessageID = a1.inReplyTo
-- SET a1.inReplyToUser = a2.senderName
-- WHERE a1.inReplyTo IS NOT NULL


SELECT * FROM bipthreads2021 WHERE inreplyto IS NULL;
-- ALTER TABLE bipthreads2021 ADD inreplytoold TEXT(30);
-- UPDATE bipthreads2021 SET inreplytoold = inreplyto;
UPDATE bipthreads2021 SET inreplyto = 'bitcoin-dev' WHERE threadlevel = 1; -- inreplyto IS NULL;

-- main one used for results query
SELECT threadid , COUNT(DISTINCT messageid) AS CNT  
FROM bipthreads2021 
GROUP BY threadid
ORDER BY CNT DESC;

-- feb 2021...one used for process mining of communications
CREATE TABLE bip9threads2021 AS
SELECT threadid,  "," AS a, threadlevel AS replylevel, sendername, "," AS b, inreplytouser,  "," AS c, datetimestamp, subject
FROM bipthreads2021 
WHERE bip = 141
-- 141, 341, 342
-- AND datetimestamp <=  '2016-08-20 23:50:00'    --  '20/08/2016 23:50'
-- GROUP BY bip, author
ORDER BY threadid ASC, threadlevel ASC -- datetimestamp asc
INTO OUTFILE 'c:\\scripts\\BIPs\\BIPSSNA_2021_AllDevelopers_24-02-2021_bip341_new.txt';

SELECT bip, DATE2, datetimestamp, email, author, subject, sendername 
FROM allmessages 
WHERE subject LIKE '%[bitcoin-dev] Forcenet:%'
ORDER BY bip ASC, datetimestamp asc

SELECT * FROM bipthreads2021 WHERE bip = 9
AND datetimestamp <=  '2016-08-20 23:50:00'  ;


SELECT bip, author, COUNT(DISTINCT messageid) AS CNT  
FROM bipthreads2021 
GROUP BY bip, author
ORDER BY CNT DESC;


-- main query for this script used for each person and role
SELECT author,bip, COUNT(DISTINCT threadid) AS threadCNT  
FROM bipthreads2021 
-- where threadlevel < 3  ...for firsta nd second level results
GROUP BY author, bip
ORDER BY threadCNT DESC;

-- -- drop table subjects2020;
-- CREATE table subjects2020 as
-- SELECT pepnum2020, peptype2020, subject, clusterbysenderfullname, authorsrole2020,  COUNT(messageid) AS cnt
-- FROM allmessages
-- WHERE pepnum2020 IN -- (SELECT DISTINCT pep FROM accrejpeps2)
-- -- we now consider all 466 PEPs instead of the 245 acc and rej ones
-- -- 12 jan 2021...we include all 466 PEPs rather than the 245 PEPs
-- (SELECT DISTINCT PEP, state, DATE2, peptype FROM allpepstillBDFL )
-- GROUP BY pepnum2020, peptype2020, subject, clusterbysenderfullname;

-- SELECT * FROM subjects2020;
-- 
-- -- top subjects
-- SELECT subject,  SUM(cnt) AS sumc FROM subjects2020
-- GROUP BY subject ORDER BY sumc desc;
-- 
-- SELECT subject, authorsrole2020, SUM(cnt) AS sumc
-- FROM subjects2020
-- GROUP BY subject, authorsrole2020
-- ORDER BY sumc desc; -- subject; -- , sumc desc;
-- 
-- -- the above method by just using the subject is not correct as there still are 'Re:' and 'Fwd:'
-- 
-- CREATE TABLE threads AS
-- SELECT subject, COUNT(messageid)
-- FROM allmessages
-- WHERE pepnum2020 IN (SELECT DISTINCT pep FROM accrejpeps2)
-- and inreplyto = messageid;
-- 
-- CREATE TABLE threads AS
-- SELECT subject, COUNT(messageid)
-- FROM allmessages
-- WHERE pepnum2020 IN (SELECT DISTINCT pep FROM accrejpeps2)
-- and inreplyto = messageid; 



-- select pep, TYPE, authorcorrected, bdfl_delegatecorrected FROM pepdetails
-- WHERE created <= '2018-04-12';

DROP TABLE threadstest2020;
CREATE TABLE threadstest2020 AS
SELECT DISTINCT (emailmessageid), inreplyto, pepnum2020, peptype2020, subject, author, clusterbysenderfullname, authorsrole2020  
FROM allmessages 
WHERE pepnum2020 IN (SELECT DISTINCT pep FROM allpepstillBDFL)
--- m
-- (SELECT COUNT(messageID) as ct FROM allmessages WHERE inReplyTo = emailMessageID) AS p
-- SET a.countRepliesToThisMessage = p.ct
SELECT * from threadstest2020;
-- now we add the replies to each message
-- ALTER TABLE `threadstest2020` ADD COLUMN `firstlevelreplies` INT NULL DEFAULT NULL;
UPDATE threadstest2020 AS a, (SELECT COUNT(emailmessageid) as ct FROM threadstest2020 WHERE inReplyTo = emailMessageID) AS p
SET a.firstlevelreplies = p.ct