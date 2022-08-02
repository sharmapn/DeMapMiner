
-- 01 March 2021. We use this script now as the main and only script for getting bdfl delegate and proposal author values
-- Before running, make sure you ran this script

update allmessages set authorsrole2020 = clusterbysenderfullname;	
update allmessages set authorsrole2020 = authorsrole;	

-- 12 jan 2021...we include all 466 PEPs rather than the 245 PEPs
--			SELECT DISTINCT PEP, state, DATE2, peptype, (SELECT COUNT(messageid) FROM allmessages WHERE pepnum2020 = m.pep) AS summessages
--				from allpepstillBDFL m order by DATE2 asc; 

-- 13 Jan 2021

-- analysing persons thread involvement in each PEP.
-- pep
--   ->person
--      -> threads

-- for this we need, first a list of all distinct people, i.e. clusterby sendernamefull
-- for each PEP
--    go through each person
--	and go through each thread to find if he is involved 
--		(by recursively iterating threads using clusterbysenderfullname)

-- while doing so, we can store involvement in top level threads, 2nd level and third level

-- now we add columns to the existing communicationsforseedera table rather than creating another table
-- ALTER TABLE communicationsforseeders ADD COLUMN threadsinvolvement_topLevel INT; -- , secondLevelPosts INT;
-- ALTER TABLE communicationsforseeders DROP COLUMN topLevelPosts;
-- ALTER TABLE communicationsforseeders ADD COLUMN threadsinvolvement_secondLevelPosts INT;
-- ALTER TABLE communicationsforseeders DROP COLUMN secondLevelPosts;
-- ALTER TABLE communicationsforseeders ADD COLUMN allthreadsinvolvement INT;

-- now we add counts for each
-- UPDATE pepthreadsbyperson2020 SET topLevelPosts = ( SELECT COUNT(messageid) FROM allmessages WHERE pep = X AND clusterbysenderfullname = Y AND inreplytouser IS NUll)
-- maybe its better to do thuis using Java or Python 


SELECT * FROM allmessages LIMIT 15;
-- drop table subjects2020;
-- DROP TABLE pepthreadsbyperson2020_PABD;
-- lets first create this table easily
-- get first level for each thread. i.e. where inreplyto is null 

-- march 08 2021...another table with bdfl d and proposal author values
CREATE table pepthreadsbyperson2020_PABD (id INT AUTO_INCREMENT PRIMARY KEY) AS
   -- distinct is just to make sure we dont get any duplicates -- dont worry it would not combine any of teh results 
	SELECT DISTINCT '1' AS threadlevel, pepnum2020, peptype2020, messageid, author, clusterbysenderfullname, emailmessageid, inreplyto, subject, authorsrole, folder -- ,  COUNT(messageid) AS cnt
	FROM allmessages
	WHERE inreplyto IS NULL -- we first look at threads top level
	AND pepnum2020 IN -- Any one of these `= 249'  OR `(SELECT DISTINCT pep FROM accrejpeps2)'
	-- we now consider all 466 PEPs instead of the 245 acc and rej ones
	(SELECT DISTINCT PEP FROM allpepstillbdfl WHERE PEP <> -1) -- AND PEP = 227);
	-- 12 jan 2021...we include all 466 PEPs rather than the 245 PEPs
	AND (authorsrole = 'proposalAuthor' OR authorsrole = 'bdfl_delegate');

-- GROUP BY pepnum2020; -- , peptype2020, subject, clusterbysenderfullname;
-- SELECT * FROM pepthreadsbyperson2020;

-- now we set the threadid to the id value
ALTER TABLE pepthreadsbyperson2020_PABD ADD COLUMN threadid INT;
UPDATE pepthreadsbyperson2020_PABD SET threadid = id;

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

-- DROP PROCEDURE getThreads_Per_Message_PABD;
-- DROP PROCEDURE createThreadInvolvementForEachPerson_PABD;

   -- First, to get the top level
   CALL getThreads_Per_Message_PABD();
	-- Then this script needs to be run.. a recursive call to find more posts in these threads..may take much longer 
	SET max_sp_recursion_depth=255;
	-- rather than recursive, we just do one by one
	call getThreads_Per_Message_from_secondlevel_PABD(2); -- we strat with thread level = 2 and it would increment itself
	call getThreads_Per_Message_from_secondlevel_PABD(3); 
	call getThreads_Per_Message_from_secondlevel_PABD(4);
	call getThreads_Per_Message_from_secondlevel_PABD(5);
	call getThreads_Per_Message_from_secondlevel_PABD(6);
	
	--, v_pepnum2020,v_messageid, v_author, v_clusterbysenderfullname,  v_emailmessageid, 
	--																	v_subject, v_threadid, v_authorsrole2020, v_peptype2020);
	
	
-- it was running for 6 hours 10 mins

DELIMITER $$
CREATE PROCEDURE getThreads_Per_Message_PABD ()
BEGIN
	DECLARE finished INTEGER DEFAULT 0; DECLARE v_pepnum2020 INTEGER DEFAULT 0; DECLARE v_messageId INTEGER DEFAULT 0; DECLARE v_author TEXT(15); DECLARE v_date DATE;
	DECLARE v_clusterbysenderfullname TEXT(15); DECLARE v_emailmessageid TEXT(15); DECLARE v_subject TEXT(15); DECLARE v_threadid INTEGER; DECLARE v_folder TEXT(50);
	DECLARE v_inreplyto TEXT(15); DECLARE v_authorsrole TEXT(20); 	DECLARE v_peptype2020 TEXT(20);  
	DECLARE curPEPList 
			CURSOR FOR 
			-- get all threads at top level, and then in the next procedure we get replies to these threads
			SELECT pepnum2020, messageid, author, clusterbysenderfullname, emailmessageid, subject, threadid, authorsrole, peptype2020, folder -- , inreplyto,  
				FROM pepthreadsbyperson2020_PABD; 		
				DECLARE CONTINUE HANDLER 
        		FOR NOT FOUND SET finished = 1;
OPEN curPEPList;

getpepsstate: 
		LOOP
			FETCH curPEPList INTO v_pepnum2020,v_messageid, v_author, v_clusterbysenderfullname,  v_emailmessageid, v_subject, v_threadid, v_authorsrole, v_peptype2020, v_folder;
			IF finished = 1 THEN 
				LEAVE getpepsstate;
			END IF;
			call createThreadInvolvementForEachPerson_PABD (v_pepnum2020,v_messageid, v_author, v_clusterbysenderfullname,  v_emailmessageid, 
																		v_subject, v_threadid, v_authorsrole, v_peptype2020, v_folder, 2); -- pass 2 as threadlevel, we start at 1, which is the top level alrady explored
		END LOOP getpepsstate;
		CLOSE curPEPList;

END$$
DELIMITER ;

-- DROP PROCEDURE createThreadInvolvementForEachPerson_PABD;
DELIMITER $$
CREATE PROCEDURE createThreadInvolvementForEachPerson_PABD (IN v_pepnum2020 INT(10), IN v_messageid INT(10), IN v_author TEXT(15), 
																		 IN v_clusterbysenderfullname TEXT(20), IN v_emailmessageid TEXT (20), IN v_subject TEXT (20), IN v_threadid INT(10),
																		 IN v_authorsrole TEXT(20), IN v_peptype2020 TEXT(20), IN v_folder TEXT(50), IN v_threadLevel INT(10))
																		 -- IN v_inreplyto TEXT(20), IN v_authorsrole2020 TEXT(20)
BEGIN
	DECLARE finished INTEGER DEFAULT 0; 
	DECLARE vv_pepnum2020 INTEGER DEFAULT 0; DECLARE vv_messageId INTEGER DEFAULT 0; DECLARE vv_author TEXT(15); DECLARE vv_date DATE;
	DECLARE vv_clusterbysenderfullname TEXT(15); DECLARE vv_emailmessageid TEXT(15); DECLARE vv_subject TEXT(15); DECLARE vv_folder TEXT(50);
	DECLARE vv_inreplyto TEXT(15); DECLARE vv_authorsrole TEXT(15);
--	SET p_state 
	DECLARE curgetMessages 
			CURSOR FOR -- DISTINCT
				SELECT pepnum2020, messageid, author, clusterbysenderfullname, emailmessageid, subject, inreplyto, authorsrole, folder  -- , authorsrole2020 -- ,  COUNT(messageid) AS cnt
					FROM allmessages 
					WHERE (inreplyto = v_emailmessageid )
					-- this would be too much noise in the data
			--		OR subject = v_subject ) -- we first look at threads top level
					AND pepnum2020 = v_pepnum2020; -- IN (SELECT DISTINCT PEP FROM allpepstillBDFL ); -- WHERE PEP <> -1AND PEP = 227);
				-- declare NOT FOUND handler
				DECLARE CONTINUE HANDLER 
        		FOR NOT FOUND SET finished = 1;
	-- INTO OUTFILE 'c:\\scripts\\SNA2020\\SNAAllDevelopersAfterClustering_19-12-2020_h.txt';
OPEN curgetMessages;

getMessages: 
		LOOP
			FETCH curgetMessages INTO vv_pepnum2020,vv_messageid, vv_author, vv_clusterbysenderfullname,  vv_emailmessageid, vv_subject, vv_inreplyto, vv_authorsrole, vv_folder; 
			IF finished = 1 THEN 
				LEAVE getMessages;
			END IF;
			-- INSERT INTO SAME TABLE 
			INSERT INTO pepthreadsbyperson2020_PABD(threadlevel, pepnum2020,messageid, author, clusterbysenderfullname,  emailmessageid, subject, threadid, inreplyto, authorsrole, peptype2020, folder )
				VALUES (v_threadLevel, vv_pepnum2020,vv_messageid, vv_author, vv_clusterbysenderfullname, vv_emailmessageid, vv_subject, v_threadid, vv_inreplyto, vv_authorsrole, v_peptype2020, vv_folder );			
			
			
		END LOOP getMessages;
		CLOSE curgetMessages;

END$$
DELIMITER ;

-- recursive function ...is called on its own
-- DROP PROCEDURE getThreads_Per_Message_from_secondlevel_PABD;
-- DROP PROCEDURE createThreadInvolvementForEachPerson_from_secondlevel_PABD;

DELIMITER $$
CREATE PROCEDURE getThreads_Per_Message_from_secondlevel_PABD (IN v_threadlevel INT(10))
BEGIN
	DECLARE finished INTEGER DEFAULT 0; DECLARE v_pepnum2020 INTEGER DEFAULT 0; DECLARE v_messageId INTEGER DEFAULT 0; DECLARE v_author TEXT(15); DECLARE v_date DATE;
	DECLARE v_clusterbysenderfullname TEXT(15); DECLARE v_emailmessageid TEXT(15); DECLARE v_subject TEXT(15); DECLARE v_threadid INTEGER;
	DECLARE v_inreplyto TEXT(15); DECLARE v_authorsrole TEXT(20); 	DECLARE v_peptype2020 TEXT(20);   DECLARE v_folder TEXT(50); 
	DECLARE curPEPList 
			CURSOR FOR 
			-- get all threads at top level, and then in the next procedure we get replies to these threads
			SELECT pepnum2020, messageid, author, clusterbysenderfullname, emailmessageid, subject, threadid, authorsrole, peptype2020, folder -- , inreplyto,  
				FROM pepthreadsbyperson2020_PABD WHERE threadlevel = v_threadlevel; 		
				DECLARE CONTINUE HANDLER 
        		FOR NOT FOUND SET finished = 1;
OPEN curPEPList;

getpepsstate: 
		LOOP
			FETCH curPEPList INTO v_pepnum2020,v_messageid, v_author, v_clusterbysenderfullname,  v_emailmessageid, v_subject, v_threadid, v_authorsrole, v_peptype2020, v_folder;
			IF finished = 1 THEN 
				LEAVE getpepsstate;
			END IF;
			call createThreadInvolvementForEachPerson_from_secondlevel_PABD (v_pepnum2020,v_messageid, v_author, v_clusterbysenderfullname,  v_emailmessageid, 
																		v_subject, v_threadid, v_authorsrole, v_peptype2020, v_folder, v_threadlevel+1); -- add 1 to threadlevel, 
		END LOOP getpepsstate;
		CLOSE curPEPList;

END$$
DELIMITER ;


DELIMITER $$
CREATE PROCEDURE createThreadInvolvementForEachPerson_from_secondlevel_PABD (IN v_pepnum2020 INT(10), IN v_messageid INT(10), IN v_author TEXT(15), 
																		 IN v_clusterbysenderfullname TEXT(20), IN v_emailmessageid TEXT (20), IN v_subject TEXT (20), IN v_threadid INT(10),
																		 IN v_authorsrole TEXT(20), IN v_peptype2020 TEXT(20), IN v_folder TEXT(50), IN v_threadLevel INT(10))
																		 -- IN v_inreplyto TEXT(20), IN v_authorsrole2020 TEXT(20)
BEGIN
	DECLARE finished INTEGER DEFAULT 0; 
	DECLARE vv_pepnum2020 INTEGER DEFAULT 0; DECLARE vv_messageId INTEGER DEFAULT 0; DECLARE vv_author TEXT(15); DECLARE vv_date DATE;
	DECLARE vv_clusterbysenderfullname TEXT(15); DECLARE vv_emailmessageid TEXT(15); DECLARE vv_subject TEXT(15); DECLARE vv_folder TEXT(50);
	DECLARE vv_inreplyto TEXT(15); DECLARE vv_authorsrole TEXT(15);
--	SET p_state 
	DECLARE curgetMessages 
			CURSOR FOR -- DISTINCT
				SELECT pepnum2020, messageid, author, clusterbysenderfullname, emailmessageid, subject, inreplyto, authorsrole, folder  
					-- , authorsrole2020 -- ,  COUNT(messageid) AS cnt
					FROM allmessages 
					WHERE (inreplyto = v_emailmessageid )
					-- this would be too much noise in the data
			--		OR subject = v_subject ) -- we first look at threads top level
					AND pepnum2020 = v_pepnum2020; -- IN (SELECT DISTINCT PEP FROM allpepstillBDFL ); -- WHERE PEP <> -1AND PEP = 227);
				-- declare NOT FOUND handler
				DECLARE CONTINUE HANDLER 
        		FOR NOT FOUND SET finished = 1;
	-- INTO OUTFILE 'c:\\scripts\\SNA2020\\SNAAllDevelopersAfterClustering_19-12-2020_h.txt';
OPEN curgetMessages;

getMessages: 
		LOOP
			FETCH curgetMessages INTO vv_pepnum2020,vv_messageid, vv_author, vv_clusterbysenderfullname,  vv_emailmessageid, vv_subject, vv_inreplyto, vv_authorsrole, vv_folder ; 
			IF finished = 1 THEN 
				LEAVE getMessages;
			END IF;
			-- INSERT INTO SAME TABLE 
			INSERT INTO pepthreadsbyperson2020_PABD(threadlevel, pepnum2020,messageid, author, clusterbysenderfullname,  emailmessageid, subject, threadid, inreplyto, authorsrole, peptype2020, folder)
				VALUES (v_threadLevel, vv_pepnum2020,vv_messageid, vv_author, vv_clusterbysenderfullname, vv_emailmessageid, vv_subject, v_threadid, vv_inreplyto, vv_authorsrole, v_peptype2020, vv_folder );			
			-- the the intyegral part ...now it will go on in recursion
			-- call getThreads_Per_Message_recursive_from_secondlevel(v_threadLevel);
		END LOOP getMessages;
		CLOSE curgetMessages;

END$$
DELIMITER ;



SELECT * FROM pepthreadsbyperson2020 ;

-- main one used for results query
SELECT threadid , COUNT(DISTINCT messageid) AS CNT  
FROM pepthreadsbyperson2020 
GROUP BY threadid
ORDER BY CNT DESC;


SELECT pepnum2020, author, COUNT(DISTINCT messageid) AS CNT  
FROM pepthreadsbyperson2020 
GROUP BY pepnum2020, author
ORDER BY CNT DESC;


-- march 07. before running the query to get results 
-- make sure you run the above procedure on '
-- assign authorsrole to authorsrole2020 field and then run query
-- update allmessages set authorsrole2020 = authorsrole;	

-- main query for this script used for each person and role
-- SELECT clusterbysenderfullname,peptype2020, COUNT(DISTINCT threadid) AS threadCNT  

-- march 08 2021..we rather just combine results to show all results in one query
SELECT clusterbysenderfullname AS m,peptype2020, COUNT(DISTINCT threadid) AS threadCNT  
FROM pepthreadsbyperson2020 
-- where threadlevel < 3  ...for firsta nd second level results
GROUP BY clusterbysenderfullname, peptype2020
-- ORDER BY threadCNT DESC
UNION
SELECT authorsrole AS m,peptype2020, COUNT(DISTINCT threadid) AS threadCNT  
FROM pepthreadsbyperson2020_PABD 
-- where threadlevel < 3  ...for firsta nd second level results
WHERE authorsrole = 'proposalAuthor' OR authorsrole = 'bdfl_delegate'
GROUP BY authorsrole, peptype2020
ORDER BY threadCNT DESC;

-- how many rows with no cluster by sendername
SELECT COUNT(*) FROM allmessages WHERE pep <> -1 AND clusterbysenderfullname IS NULL
-- 26, 493
-- now lets check author
SELECT COUNT(*) FROM allmessages WHERE pep <> -1 AND author IS NULL
-- just 23

SELECT author, clusterbysenderfullname FROM pepthreadsbyperson2020 
WHERE clusterbysenderfullname IS NULL

-- since all author names are filled, lets use that field instead
-- just checking author..not used
SELECT author,peptype2020, COUNT(DISTINCT threadid) AS threadCNT  
FROM pepthreadsbyperson2020 
-- where threadlevel < 3  ...for firsta nd second level results
GROUP BY author, peptype2020
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