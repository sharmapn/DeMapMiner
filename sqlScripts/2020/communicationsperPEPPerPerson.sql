-- sentiment analysis
-- this is the main script containing sql for roles analysis

-- DROP TABLE sentimentanalysis;
-- CREATE TABLE sentimentanalysis AS
-- ALTER TABLE sentimentanalysis ADD column cleaned_msg TEXT 
-- ALTER TABLE sentimentanalysis ADD column sentiment TEXT

-- do we need this line of code which we want to see direct connections at the moment
-- AND LENGTH(sendername) > 0 AND LENGTH(inReplyToUser) > 0 

-- DROP PROCEDURE getPEPList_Person;
-- DROP PROCEDURE createEmailInvolvementForEachPerson;
-- CALL getPEPList_Person();


-- DROP PROCEDURE createSentenceListForEachPEPState;

TRUNCATE communicationsforsna;

DELIMITER $$

CREATE PROCEDURE getPEPList_Person ()
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
			call createEmailInvolvementForEachPerson (v_pep,v_state, v_date);
		END LOOP getpepsstate;
		CLOSE curPEPList;

END$$
DELIMITER ;

DELIMITER $$

CREATE PROCEDURE createEmailInvolvementForEachPerson (IN v_pep INT(10), IN v_state TEXT(15), IN v_date2 DATE)
BEGIN
	DECLARE finished INTEGER DEFAULT 0; DECLARE v_cnt INTEGER DEFAULT 0; DECLARE p_state TEXT; DECLARE vv_pep INTEGER;
	DECLARE v_author TEXT(30); DECLARE v_inReplyToUserUsingClusteredSender TEXT(30);
--	SET p_state 
	DECLARE curgetMessages 
			CURSOR FOR 
				SELECT pep, author, inReplyToUserUsingClusteredSender, 
				-- COUNT(messageid) AS cnt, -- messageid, author, authorsrole, DATE2, datetimestamp
--				PEP
--				, (SELECT state FROM pepstates_danieldata_datetimestamp WHERE PEP = m.PEP AND STATE LIKE 'acc' OR state LIKE '%rej%' order by DATE2 asc LIMIT 1)
--				author, inReplyToUserUsingClusteredSender, 
				-- (select authorsrole FROM commonroles WHERE author = m.author ),  
--				authorsrole, (select PEPCount FROM commonroles WHERE author = m.author ) AS pepinvolvementcount, -- add number of peps the member has contributed in
--				analysewords
				(select PEPCount FROM commonroles WHERE author = m.author ) AS cnt 
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
				AND LENGTH(author) > 0 AND LENGTH(inReplyToUserUsingClusteredSender) > 0 
				-- AND (sendername in (select distinct(sendername) from alldevelopers) -- atleast one member is a developer
				-- OR inReplyToUser in (select distinct(sendername) from alldevelopers))
				-- GROUP BY 
				ORDER BY author;
				-- declare NOT FOUND handler
				DECLARE CONTINUE HANDLER 
        		FOR NOT FOUND SET finished = 1;
	-- INTO OUTFILE 'c:\\scripts\\SNA2020\\SNAAllDevelopersAfterClustering_19-12-2020_h.txt';
OPEN curgetMessages;

getMessages: 
		LOOP
			FETCH curgetMessages INTO vv_pep, v_author, v_inReplyToUserUsingClusteredSender, v_cnt; 
			IF finished = 1 THEN 
				LEAVE getMessages;
			END IF;
			-- build email list
			-- SET emailList = CONCAT(emailAddress,";",emailList);		
			-- SELECT @id := cust_id FROM customers WHERE cust_id='customer name';
			INSERT INTO communicationsForSNA(state, pep, author, inReplyToUserUsingClusteredSender, cnt)
			VALUES (v_state, vv_pep, v_author, v_inReplyToUserUsingClusteredSender, v_cnt);			

		END LOOP getMessages;
		CLOSE curgetMessages;

END$$
DELIMITER ;

SELECT state, pep, author,",", inReplyToUserUsingClusteredSender, ",", COUNT(*) AS cnt
FROM communicationsforsna
GROUP BY pep, author
ORDER BY pep ASC, cnt DESC

-- new query for paper.
-- each members communication with another member and the number of PEPs in which they communicated
SELECT LOWER(author),LOWER(inReplyToUserUsingClusteredSender), COUNT(DISTINCT PEP) AS cntPEP
FROM communicationsforsna
GROUP BY author, inReplyToUserUsingClusteredSender
HAVING cntPEP >=10
ORDER BY cntPEP DESC

-- we can update table and combine names


SELECT author,",", inReplyToUserUsingClusteredSender, ",", cnt 
FROM communicationsforsna
INTO OUTFILE 'c:\\scripts\\SNA2020\\SNAAllDevelopersRoles_24-12-2020_i.txt';










