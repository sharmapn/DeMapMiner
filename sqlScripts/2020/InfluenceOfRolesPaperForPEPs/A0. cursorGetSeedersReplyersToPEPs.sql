-- this is the first script and starting point for email contribution analysis
-- it is also main script containing sql for roles analysis
-- ACCOMPANYING SCRIPT which has pre-processing for getting results from the results table 
-- are in a script with same name + "...b4 run and see reults.sql"

-- DROP TABLE sentimentanalysis;
-- CREATE TABLE sentimentanalysis AS
-- ALTER TABLE sentimentanalysis ADD column cleaned_msg TEXT 
-- ALTER TABLE sentimentanalysis ADD column sentiment TEXT

-- do we need this line of code which we want to see direct connections at the moment
-- AND LENGTH(sendername) > 0 AND LENGTH(inReplyToUser) > 0 

-- DROP PROCEDURE getPEPSeeders_Replyers;
-- DROP PROCEDURE createEmailInvolvementForEachPerson_SeedersReplyers;
-- CALL getPEPSeeders_Replyers();

-- COLUMN EXPLANATIONS
-- totalmsgs = total messages by this author for the current PEP, regardless of state (accepted or Rejected)
-- msgs_not_replies = number of the msgs from above which were not  a reply to any messgage
-- msgs_are_replies = number of the msgs from above which were a reply to any messgage
-- totalInReplyTo  = number of replies received by this author for all his messages for the current pep
-- summessages = all messages written for the current pep, by all members writing messages for the pep. This is used to calculate proportions


-- TRUNCATE communicationsForSeeders;


-- Either we consider is all 466 peps decided during bdfl
-- XXXX in code allows only consider all by bdfl delegates during above period = 52 PEPs 
-- drop TABLE communicationsForSeedersbdfld like communicationsforseeders;
DELIMITER $$
CREATE PROCEDURE getPEPSeeders_Replyers ()
BEGIN
	DECLARE finished INTEGER DEFAULT 0; DECLARE v_pep INTEGER DEFAULT 0; DECLARE v_messageId INTEGER DEFAULT 0; DECLARE v_state TEXT(15); DECLARE v_date DATE;
	DECLARE v_peptype TEXT(15); DECLARE v_summessages INTEGER;
	DECLARE curPEPList 
			CURSOR FOR 
			-- this is only for 245 acc rej peps
			--	SELECT DISTINCT PEP, state, DATE2, peptype,							-- AND (folder LIKE '%dev%' OR folder LIKE '%ideas%')
			--		(SELECT COUNT(messageid) FROM allmessages WHERE pepnum2020 = m.pep ) AS summessages
			--		FROM pepstates_danieldata_datetimestamp m 
			--		WHERE (STATE LIKE '%acc%' OR state LIKE '%rej%') order by DATE2 asc;
			-- 12 jan 2021...we include all 466 PEPs rather than the 245 PEPs
			SELECT DISTINCT PEP, state, DATE2, peptype, (SELECT COUNT(messageid) FROM allmessages WHERE pepnum2020 = m.pep) AS summessages
				from allpepstillBDFL m 								 
				ORDER BY DATE2 asc; 		
				DECLARE CONTINUE HANDLER 
        		FOR NOT FOUND SET finished = 1;
OPEN curPEPList;

getpepsstate: 
		LOOP
			FETCH curPEPList INTO v_pep, v_state, v_date, v_peptype, v_summessages;
			IF finished = 1 THEN 
				LEAVE getpepsstate;
			END IF;
			call createEmailInvolvementForEachPerson_SeedersReplyers (v_pep,v_state, v_date, v_peptype,  v_summessages);
		END LOOP getpepsstate;
		CLOSE curPEPList;

END$$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE createEmailInvolvementForEachPerson_SeedersReplyers (IN v_pep INT(10), IN v_state TEXT(15), IN v_date2 DATE, IN v_peptype TEXT (20), IN v_summessages INT(20))
BEGIN
	DECLARE finished INTEGER DEFAULT 0; DECLARE v_cnt INTEGER DEFAULT 0; DECLARE p_state TEXT; DECLARE vv_pep INTEGER;
	DECLARE v_author TEXT(30); DECLARE v_inReplyToUserUsingClusteredSender TEXT(30); DECLARE v_SeederCount INTEGER;
	DECLARE v_totalmsgs INTEGER; DECLARE v_msgs_not_replies INTEGER; DECLARE v_msgs_are_replies INTEGER;
	DECLARE v_author_again TEXT(30); DECLARE v_folder_again TEXT(30); DECLARE v_totalInReplyTo TEXT(30); DECLARE v_folder TEXT(30); 
	DECLARE v_dominance_index FLOAT;
--	SET p_state 
	DECLARE curgetMessages 
			CURSOR FOR 
				SELECT * 
				FROM (SELECT pepnum2020, folder, clusterbysenderfullname, COUNT(messageid) AS totalmsgs, 
				  			SUM(CASE WHEN inReplyTo IS NULL THEN 1 ELSE 0 END) As msg_which_are_not_replies, -- inReplyToUserUsingClusteredSender
				  			SUM(CASE WHEN inReplyTo IS NOT NULL THEN 1 ELSE 0 END) AS msg_which_are_replies
				  			 
						FROM allmessages
						WHERE pepnum2020 = v_pep 
						-- 3 Jan 2021 we consider messages from all lists
						-- AND (folder LIKE '%dev%' OR folder LIKE '%ideas%')
						GROUP BY clusterbysenderfullname, folder
						ORDER BY msg_which_are_not_replies DESC) AS tbA
				-- We join with how many times this particular person has been replied to
				LEFT JOIN (SELECT inReplyToUserUsingClusteredSender, folder, COUNT(inReplyTo) AS totalinReplyTo  -- inReplyTo, 
								FROM allmessages
								WHERE pepnum2020 = v_pep
								-- 3 Jan 2021 we consider messages from all lists
								-- AND (folder LIKE '%dev%' OR folder LIKE '%ideas%')
								GROUP BY inReplyToUserUsingClusteredSender, folder  
								ORDER BY totalinReplyTo desc) as tbC
				ON tbA.clusterbysenderfullname = tbC.inReplyToUserUsingClusteredSender
				AND tbA.folder = tbC.folder;
				-- declare NOT FOUND handler
				DECLARE CONTINUE HANDLER 
        		FOR NOT FOUND SET finished = 1;
	-- INTO OUTFILE 'c:\\scripts\\SNA2020\\SNAAllDevelopersAfterClustering_19-12-2020_h.txt';
OPEN curgetMessages;

getMessages: 
		LOOP
			FETCH curgetMessages INTO vv_pep, v_folder, v_author, v_totalmsgs, v_msgs_not_replies,v_msgs_are_replies, v_author_again, v_folder_again, v_totalInReplyTo; 
			IF finished = 1 THEN 
				LEAVE getMessages;
			END IF;
			-- SELECT LN(v_totalmsgs/v_summessages) * (v_totalmsgs/v_summessages) INTO v_dominance_index;
			-- build email list
			-- SET emailList = CONCAT(emailAddress,";",emailList);		
			-- SELECT @id := cust_id FROM customers WHERE cust_id='customer name';
			INSERT INTO communicationsForSeeders(state, pep, author, totalmsgs, msgs_not_replies,msgs_are_replies, author_again, totalInReplyTo, peptype, summessages,dominance_index, folder)
			VALUES (v_state, vv_pep, v_author, v_totalmsgs, v_msgs_not_replies , v_msgs_are_replies, v_author_again, v_totalInReplyTo, v_peptype, v_summessages, v_dominance_index, v_folder);			

		END LOOP getMessages;
		CLOSE curgetMessages;

END$$
DELIMITER ;

SELECT * from communicationsforseeders;
