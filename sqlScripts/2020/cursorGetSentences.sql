-- sentiment analysis

-- DROP TABLE sentimentanalysis;
-- CREATE TABLE sentimentanalysis AS
-- ALTER TABLE sentimentanalysis ADD column cleaned_msg TEXT 
-- ALTER TABLE sentimentanalysis ADD column sentiment TEXT

-- dec 20 commente the line below
-- we dont need this for this snaalysis
-- AND LENGTH(sendername) > 0 AND LENGTH(inReplyToUser) > 0 

TRUNCATE sentimentsentences;

DELIMITER $$
-- DROP PROCEDURE getPEPList;
CREATE PROCEDURE getPEPList ()
BEGIN
	DECLARE finished INTEGER DEFAULT 0; DECLARE v_pep INTEGER DEFAULT 0; DECLARE v_messageId INTEGER DEFAULT 0; DECLARE v_state TEXT(15); DECLARE v_date DATE;
	DECLARE curPEPList 
			CURSOR FOR 
				SELECT DISTINCT PEP, state, DATE2 FROM pepstates_danieldata_datetimestamp WHERE (STATE LIKE '%acc%' OR state LIKE '%rej%') order by DATE2 asc;
				DECLARE CONTINUE HANDLER 
        		FOR NOT FOUND SET finished = 1;
OPEN curPEPList;

getpepsstate: 
		LOOP
			FETCH curPEPList INTO v_pep, v_state, v_date;
			IF finished = 1 THEN 
				LEAVE getpepsstate;
			END IF;
			call createSentenceListForEachPEPState (v_pep,v_state, v_date);
		END LOOP getpepsstate;
		CLOSE curPEPList;

END$$
DELIMITER ;

DELIMITER $$
-- DROP PROCEDURE createSentenceListForEachPEPState;
CREATE PROCEDURE createSentenceListForEachPEPState (IN v_pep INT(10), IN v_state TEXT(15), IN v_date2 DATE)
BEGIN
	DECLARE finished INTEGER DEFAULT 0; DECLARE v_messageId INTEGER DEFAULT 0; DECLARE p_state TEXT;
	DECLARE v_author TEXT(30); DECLARE v_authorsrole TEXT(30);
--	SET p_state 
	DECLARE curgetMessageIds 
			CURSOR FOR 
				SELECT messageid, author, authorsrole, DATE2, datetimestamp
--				PEP
--				, (SELECT state FROM pepstates_danieldata_datetimestamp WHERE PEP = m.PEP AND STATE LIKE 'acc' OR state LIKE '%rej%' order by DATE2 asc LIMIT 1)
--				author, inReplyToUserUsingClusteredSender, 
				-- (select authorsrole FROM commonroles WHERE author = m.author ),  
--				authorsrole, (select PEPCount FROM commonroles WHERE author = m.author ) AS pepinvolvementcount, -- add number of peps the member has contributed in
--				analysewords
				FROM allmessages m
				WHERE PEP = v_pep -- IN (SELECT PEP FROM accrejpeps)
				--	AND state like '%acc%' or '%rej%'
				-- WHICH DATES WE WANT THE DATA
				-- 1. all dates
				-- AND DATE2 < (SELECT DATE2 FROM accrejpeps WHERE PEP = m.PEP)
				-- 2. before and including day state is committed
				AND DATE2 <= v_date2 -- (SELECT DATE2 FROM pepstates_danieldata_datetimestamp WHERE PEP = m.PEP AND (STATE LIKE 'acc' OR state LIKE '%rej%') order by DATE2 asc LIMIT 1)
				-- 3. only discussion one week before the day state is committed
				--	AND DATE2 BETWEEN 
				--		(SELECT DATE2 FROM pepstates_danieldata_datetimestamp WHERE PEP = m.PEP AND STATE LIKE 'acc' OR state LIKE '%rej%' order by DATE2 asc LIMIT 1) -7
				--		AND (SELECT DATE2 FROM pepstates_danieldata_datetimestamp WHERE PEP = m.PEP AND STATE LIKE 'acc' OR state LIKE '%rej%' order by DATE2 asc LIMIT 1)	
				AND folder LIKE '%dev%'
				-- we want to see direct connections at the moment
				-- we dont need this for this snaalysis
				-- AND LENGTH(sendername) > 0 AND LENGTH(inReplyToUser) > 0 
				-- AND (sendername in (select distinct(sendername) from alldevelopers) -- atleast one member is a developer
				-- OR inReplyToUser in (select distinct(sendername) from alldevelopers))
				ORDER BY sendername;
				-- declare NOT FOUND handler
				DECLARE CONTINUE HANDLER 
        		FOR NOT FOUND SET finished = 1;
	-- INTO OUTFILE 'c:\\scripts\\SNA2020\\SNAAllDevelopersAfterClustering_19-12-2020_h.txt';
OPEN curgetMessageIds;

getMessageIds: 
		LOOP
			FETCH curgetMessageIds INTO v_messageId, v_author, v_authorsrole,v_date2, v_datetimestamp;
			IF finished = 1 THEN 
				LEAVE getMessageIds;
			END IF;
			-- build email list
			-- SET emailList = CONCAT(emailAddress,";",emailList);		
			-- SELECT @id := cust_id FROM customers WHERE cust_id='customer name';
			INSERT INTO sentimentsentences(id, pep, messageid, date2, datetimestamp sentence, state, 		author, 						authorsrole,				datecommitted,	isEnglishOrCode,islastparagraph, isfirstparagraph, msgSubject )			
			SELECT id, proposal, messageid, mdate AS date2, v_datetimestamp AS datetimestamp, sentence, v_state AS state,  v_author AS author, v_authorsrole AS authorsrole, v_date2 AS datecommitted,  isEnglishOrCode,islastparagraph, isfirstparagraph, msgSubject 
				FROM allsentences 
				WHERE messageid = v_messageId 
				AND proposal = v_pep AND isEnglishOrCode = 1 AND (islastparagraph =1 or isfirstparagraph =1) 
				ORDER BY proposal ASC;
        		
		END LOOP getMessageIds;
		CLOSE curgetMessageIds;

END$$
DELIMITER ;


DELIMITER $$

-- DROP PROCEDURE createSentenceList;
-- DROP PROCEDURE insertSentence;
CREATE PROCEDURE insertSentence () -- IN v_pep INT(10), IN v_state TEXT(15), IN v_date2 DATE
BEGIN
	SELECT NOW();
	
END$$
DELIMITER ;
-- CALL insertSentence();

-- You can test the createEmailList stored procedure using the following script:
-- SET @emailList = ""; 
-- CALL createEmailList(@emailList); 
-- SELECT @emailList;

-- CALL createSentenceList(308);
-- CALL getPEPList();
-- TRUNCATE sentimentsentences

-- SELECT * FROM sentimentsentences
--	where authorsrole LIKE '%core%' 
--	AND pep = 308
--	AND state LIKE '%rej%'
--	ORDER BY pep asc

		-- CREATE TABLE sentimentsentences AS
-- SELECT proposal, msgAuthorRole, messageid, sentence,isEnglishOrCode,islastparagraph, isfirstparagraph, msgSubject
-- from allsentences
-- WHERE -- messageid = v_messageId
-- -- and
-- proposal = 308 and isEnglishOrCode = 1
-- and (islastparagraph =1 or isfirstparagraph =1)
-- 			order by proposal ASC;

-- INSERT INTO sentimentsentences (pep,  messageid, sentence) -- state,
-- SELECT proposal,  messageid, sentence
-- -- ,msgAuthorRole, isEnglishOrCode,islastparagraph, isfirstparagraph, msgSubject
-- from allsentences
-- WHERE -- messageid = v_messageId
-- -- and
-- proposal = 308 and isEnglishOrCode = 1
-- and (islastparagraph =1 or isfirstparagraph =1)
-- 			order by proposal ASC;


-- SELECT messageid, author, authorsrole
-- FROM allmessages 
-- LIMIT 500

SELECT * FROM allsentences LIMIT 5




