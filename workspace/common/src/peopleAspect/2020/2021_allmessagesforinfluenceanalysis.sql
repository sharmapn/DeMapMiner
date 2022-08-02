-- 16th Jan 2021

-- FIRST PART.... create the staging area ... a table seprate from allmessages where we can do analysis
-- DROP TABLE allmessagesforinfluenceanalysis;
CREATE TABLE allmessagesforinfluenceanalysis (id INT AUTO_INCREMENT PRIMARY KEY) AS 
	SELECT pep, peptype, pepnum2020,peptype2020, messageid, emailmessageid, 
		author, authorsrole, authorsrole2020, clusterbysenderfullname, sendername,
		subject,  
		inreplyto, inReplyToUserUsingClusteredSender, inReplyToUserUsingClusteredSenderRole	
	FROM allmessages
	WHERE pepnum2020 IN (SELECT PEP from allpepstillBDFL);
-- 114,203 records

ALTER TABLE `allmessagesforinfluenceanalysis` ADD INDEX `pepnum2020_index` (`pepnum2020`);
ALTER TABLE `allmessagesforinfluenceanalysis` ADD INDEX `emailmessageid_index` (emailmessageid(15));
ALTER TABLE `allmessagesforinfluenceanalysis` ADD INDEX `inreplyto_index` (inreplyto(15));
ALTER TABLE `allmessagesforinfluenceanalysis` ADD INDEX `inReplyToUserUsingClusteredSender_index` (inReplyToUserUsingClusteredSender(15));
ALTER TABLE `allmessagesforinfluenceanalysis` ADD INDEX `inReplyToUserUsingClusteredSenderRole_index` (inReplyToUserUsingClusteredSenderRole(15));
ALTER TABLE `allmessagesforinfluenceanalysis` ADD INDEX `clusterbysenderfullname_index` (clusterbysenderfullname(15));
ALTER TABLE `allmessagesforinfluenceanalysis` ADD INDEX `peptype2020_index` (peptype2020(15));
ALTER TABLE `allmessagesforinfluenceanalysis` ADD INDEX `subject_index` (subject(30));
-- remember we have to maybe clean the subject of fwd: and re:

-- SECOND PART
-- NOw we add more rows to this table which are unassigned thread messages 
-- and same subject for top level threads

ALTER TABLE `allmessagesforinfluenceanalysis` ADD COLUMN threadlevel INT; 
-- ALL THREADS Which are not replies are set at level 1
UPDATE allmessagesforinfluenceanalysis SET threadlevel = 1 WHERE inreplyto IS NULL;
ALTER TABLE `allmessagesforinfluenceanalysis` ADD INDEX `threadlevel_index` (threadlevel);

ALTER TABLE `allmessagesforinfluenceanalysis` ADD COLUMN matchedusingsubject INT; 
-- correcting the misassignment
-- UPDATE allmessagesforinfluenceanalysis SET matchedusingsubject = threadlevel WHERE threadlevel = 2; -- doing the changeover
-- UPDATE allmessagesforinfluenceanalysis SET threadlevel = NULL WHERE threadlevel = 2; -- doing the changeover
-- UPDATE allmessagesforinfluenceanalysis SET matchedusingsubject = 1 WHERE matchedusingsubject = 2; -- doing the changeover
ALTER TABLE `allmessagesforinfluenceanalysis` ADD INDEX matchedusingsubject_index (matchedusingsubject);

ALTER TABLE allmessagesforinfluenceanalysis ADD COLUMN threadid INT;
UPDATE allmessagesforinfluenceanalysis SET threadid = id;
ALTER TABLE `allmessagesforinfluenceanalysis` ADD INDEX threadid_index (threadid);

-- now we assign more messages.
-- but make sure for matcging subjects, only match them if parent is top level message and has pep number i.e. pepnum2020
-- THIS IS JUST THE FOUNDATION SCRIPT --IT IS FOR REFERENCE ONLY
SELECT pepnum2020, messageid, author, clusterbysenderfullname, emailmessageid, subject, inreplyto, authorsrole2020 -- , authorsrole2020 -- ,  COUNT(messageid) AS cnt
					FROM allmessages 
					WHERE (inreplyto = v_messageid 
					OR subject = v_subject ) -- we first look at threads top level
					AND pepnum2020 = v_pepnum2020; -- IN (SELECT DISTINCT PEP FROM allpepstillBDFL ); -- WHERE PEP <> -1AND PEP = 227);
					
-- MAIN SCRIPT FOR MATCHING... get child rows
--  This takes too long so I may use Java or python for this task
INSERT INTO allmessagesforinfluenceanalysis (threadlevel, pep, peptype, pepnum2020,peptype2020, messageid, emailmessageid, author, authorsrole, 
															authorsrole2020, clusterbysenderfullname, sendername,	subject, 
															inreplyto, inReplyToUserUsingClusteredSender, inReplyToUserUsingClusteredSenderRole	)
				-- first try this
				SELECT a.pep, a.peptype, a.pepnum2020, a.peptype2020, a.messageid, a.emailmessageid, 
					a.author, a.authorsrole, a.authorsrole2020, a.clusterbysenderfullname, 
					a.sendername, a.subject, a.inreplyto, a.inReplyToUserUsingClusteredSender, a.inReplyToUserUsingClusteredSenderRole	 
					FROM allmessages a -- , allmessagesforinfluenceanalysis b
					INNER JOIN allmessagesforinfluenceanalysis b USING(subject) 
					LIMIT 1000;
					-- WHERE a.subject = b.subject
					-- LIMIT 1000;
					WHERE b.inreplyto IS NULL -- top level

-- I have used Python for the above task
SELECT * from allmessagesforinfluenceanalysis
WHERE threadlevel = 2
DELETE from allmessagesforinfluenceanalysis WHERE threadlevel = 2


SELECT @curRank := @curRank + 1 AS rank, subject, COUNT(messageid) AS cnt, (SELECT @curRank := 0) r
from allmessagesforinfluenceanalysis
WHERE matchedusingsubject = 1
GROUP BY subject
ORDER BY cnt DESC

-- some subjects are very high ...they are automated emails, thus need not be considered
ALTER TABLE allmessagesforinfluenceanalysis ADD COLUMN consider INT;
ALTER TABLE `allmessagesforinfluenceanalysis` ADD INDEX consider_index (consider);
UPDATE allmessagesforinfluenceanalysis SET consider = 1;


-- Finally looking at the subjects from the above query, there seems to be many subjects which do not concern PEP decision-making, e.g. Thoughts about Python
UPDATE allmessagesforinfluenceanalysis SET consider = 0 WHERE matchedusingsubject = 1;
------------------------------------
-- just additional script which are really not used				 	
	
-- add peptype and state
-- not needed
-- update allmessagesforinfluenceanalysis SELECT state FROM pepstates_danieldata_datetimestamp WHERE PEP = m.pep)	

-- set peps which were decided by bdfl delegate
alter TABLE allmessagesforinfluenceanalysis
set bdfl_delegate = SELECT DISTINCT pep FROM pepdetails WHERE LENGTH (bdfl_delegatecorrected) > 0 );


SELECT DISTINCT PEP, state, DATE2, peptype, (SELECT COUNT(messageid) FROM allmessages 
	WHERE pepnum2020 = m.pep) AS summessages
	FROM allpepstillBDFL
				
SELECT DISTINCT PEP, state, DATE2, peptype from allpepstillBDFL