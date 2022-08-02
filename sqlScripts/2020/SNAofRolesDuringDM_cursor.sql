-- sentiment analysis
-- this is the main script containing sql for roles analysis

-- DROP TABLE sentimentanalysis;
-- CREATE TABLE sentimentanalysis AS
-- ALTER TABLE sentimentanalysis ADD column cleaned_msg TEXT 
-- ALTER TABLE sentimentanalysis ADD column sentiment TEXT

-- do we need this line of code which we want to see direct connections at the moment
-- AND LENGTH(sendername) > 0 AND LENGTH(inReplyToUser) > 0 

-- DROP PROCEDURE getPEPList_Roles;
-- DROP PROCEDURE createEmailInvolvementForEachRole;
-- CALL getPEPList_Roles();


-- DROP PROCEDURE createSentenceListForEachPEPState;

TRUNCATE rolecommunicationsforsna;

DELIMITER $$

CREATE PROCEDURE getPEPList_Roles ()
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
			call createEmailInvolvementForEachRole (v_pep,v_state, v_date);
		END LOOP getpepsstate;
		CLOSE curPEPList;

END$$
DELIMITER ;

DELIMITER $$

CREATE PROCEDURE createEmailInvolvementForEachRole (IN v_pep INT(10), IN v_state TEXT(15), IN v_date2 DATE)
BEGIN
	DECLARE finished INTEGER DEFAULT 0; DECLARE v_cnt INTEGER DEFAULT 0; DECLARE p_state TEXT; DECLARE vv_pep INTEGER;
	DECLARE v_authorsRole TEXT(30); DECLARE v_inReplyToUserUsingClusteredSenderRole TEXT(30);
--	SET p_state 
	DECLARE curgetMessages 
			CURSOR FOR 
				SELECT pep, authorsRole, inReplyToUserUsingClusteredSenderRole, 
				-- COUNT(messageid) AS cnt, -- messageid, author, authorsrole, DATE2, datetimestamp
--				PEP
--				, (SELECT state FROM pepstates_danieldata_datetimestamp WHERE PEP = m.PEP AND STATE LIKE 'acc' OR state LIKE '%rej%' order by DATE2 asc LIMIT 1)
--				author, inReplyToUserUsingClusteredSender, 
				-- (select authorsrole FROM commonroles WHERE author = m.author ),  
--				authorsrole, (select PEPCount FROM commonroles WHERE author = m.author ) AS pepinvolvementcount, -- add number of peps the member has contributed in
--				analysewords
				(select PEPCount FROM commonroles WHERE author = m.author ) AS cnt 
				FROM allmessages m
				WHERE PEPnum2020 = v_pep -- IN (SELECT PEP FROM accrejpeps)
				--	AND state like '%acc%' or '%rej%'
				-- WHICH DATES WE WANT THE DATA
				-- 1. all dates
				-- AND DATE2 < (SELECT DATE2 FROM accrejpeps WHERE PEP = m.PEP)
				-- 2. before and including day state is committed
				-- jan 2021 we do not limit dates
				-- AND DATE2 <= v_date2 -- (SELECT DATE2 FROM pepstates_danieldata_datetimestamp WHERE PEP = m.PEP AND (STATE LIKE 'acc' OR state LIKE '%rej%') order by DATE2 asc LIMIT 1)
				-- 3. only discussion one week before the day state is committed
				--	AND DATE2 BETWEEN 
				--		(SELECT DATE2 FROM pepstates_danieldata_datetimestamp WHERE PEP = m.PEP AND STATE LIKE 'acc' OR state LIKE '%rej%' order by DATE2 asc LIMIT 1) -7
				--		AND (SELECT DATE2 FROM pepstates_danieldata_datetimestamp WHERE PEP = m.PEP AND STATE LIKE 'acc' OR state LIKE '%rej%' order by DATE2 asc LIMIT 1)	
				-- jan 2021 we consider all mailing lists
				-- AND folder LIKE '%dev%'
				-- we want to see direct connections at the moment
				AND LENGTH(authorsRole) > 0 AND LENGTH(inReplyToUserUsingClusteredSenderRole) > 0 
				-- AND (sendername in (select distinct(sendername) from alldevelopers) -- atleast one member is a developer
				-- OR inReplyToUser in (select distinct(sendername) from alldevelopers))
				-- GROUP BY 
				ORDER BY authorsRole;
				-- declare NOT FOUND handler
				DECLARE CONTINUE HANDLER 
        		FOR NOT FOUND SET finished = 1;
	-- INTO OUTFILE 'c:\\scripts\\SNA2020\\SNAAllDevelopersAfterClustering_19-12-2020_h.txt';
OPEN curgetMessages;

getMessages: 
		LOOP
			FETCH curgetMessages INTO vv_pep, v_authorsRole, v_inReplyToUserUsingClusteredSenderRole, v_cnt; 
			IF finished = 1 THEN 
				LEAVE getMessages;
			END IF;
			-- build email list
			-- SET emailList = CONCAT(emailAddress,";",emailList);		
			-- SELECT @id := cust_id FROM customers WHERE cust_id='customer name';
			INSERT INTO rolecommunicationsForSNA(pep, authorsrole, inReplyToUserUsingClusteredSenderRole, cnt)
			VALUES (vv_pep, v_authorsRole, v_inReplyToUserUsingClusteredSenderRole, v_cnt);			

		END LOOP getMessages;
		CLOSE curgetMessages;

END$$
DELIMITER ;

SELECT pep, authorsrole,",", inReplyToUserUsingClusteredSenderRole, ",", cnt 
FROM rolecommunicationsforsna

SELECT authorsrole,",", inReplyToUserUsingClusteredSenderRole, ",", cnt 
FROM rolecommunicationsforsna
INTO OUTFILE 'c:\\scripts\\SNA2020\\SNAAllDevelopersRoles_24-12-2020_i.txt';


-------------------------------
-- SNA during DM of 245 PEPs 
-- based on all discussions before decision was made, and all discussions happenning a wekk b4 decision

-- Only the acc or rej peps and state change date
-- Jan 2021 we now use all 466 peps till bdfl
-- CREATE table accrejpeps AS
-- SELECT DISTINCT PEP, state, date2
-- FROM pepstates_danieldata_datetimestamp
-- WHERE (state like '%acc%' or state like '%rej%')
-- ORDER BY pep ASC, state

-- easy query for debugging below sql

SELECT author, clusterBySenderFullName, inReplyToUser, inReplyToUserUsingClusteredSender
from allmessages m
WHERE PEP = 308

-- main query to get the SN for one PEP
-- modified jan 20 2021
select clusterBySenderFullName,"," , inReplyToUserUsingClusteredSender, "," , 
			-- (select authorsrole FROM commonroles WHERE author = m.author ),  
			authorsrole,",", 
			(select PEPCount FROM commonroles WHERE author = m.author ) AS pepinvolvementcount -- add number of peps the member has contributed in
	from allmessages m
	WHERE PEPnum2020 IN (SELECT PEP FROM allpepstillbdfl) 
			AND PEP NOT IN (SELECT pep FROM pepdetails WHERE LENGTH (bdfl_delegatecorrected) > 0 ) 
	-- AND DATE2 < (SELECT DATE2 FROM accrejpeps WHERE PEP = m.PEP)
	-- jan 2021 we consider all lailing lists
	-- AND folder LIKE '%dev%'
	-- we want to see direct connections at the moment
	AND LENGTH(clusterBySenderFullName) > 0 
	AND LENGTH(inReplyToUser) > 0 
	-- AND (sendername in (select distinct(sendername) from alldevelopers) -- atleast one member is a developer
	-- OR inReplyToUser in (select distinct(sendername) from alldevelopers))
	order by clusterBySenderFullName
INTO OUTFILE 'c:\\scripts\\SNA2020\\SNAAllDevelopers_20-01-2021_b.txt';
-- limit 10

-- for all peps decided by bdfl delegates, use the below query
-- main query to get the SN for one PEP
-- modified jan 20 2021
-- 	AND (authorsrole = 'proposalauthor' OR authorsrole = 'bdfl_delegate')
select clusterBySenderFullName,"," , inReplyToUserUsingClusteredSender, "," , 
			-- (select authorsrole FROM commonroles WHERE author = m.author ),  
			authorsrole,",", 
			(select PEPCount FROM commonroles WHERE author = m.author ) AS pepinvolvementcount -- add number of peps the member has contributed in
	from allmessages m
	WHERE PEPnum2020 IN (SELECT pep FROM pepdetails WHERE LENGTH (bdfl_delegatecorrected) > 0 )  -- (SELECT PEP FROM allpepstillbdfl) -- (SELECT pep FROM bdflddpeps)
	-- AND DATE2 < (SELECT DATE2 FROM accrejpeps WHERE PEP = m.PEP)
	-- jan 2021 we consider all lailing lists
	-- AND folder LIKE '%dev%'
	-- we want to see direct connections at the moment
	AND LENGTH(clusterBySenderFullName) > 0 
	AND LENGTH(inReplyToUser) > 0 
	-- AND (sendername in (select distinct(sendername) from alldevelopers) -- atleast one member is a developer
	-- OR inReplyToUser in (select distinct(sendername) from alldevelopers))
	order by clusterBySenderFullName
INTO OUTFILE 'c:\\scripts\\SNA2020\\SNA_allBDFLDelegate_20-01-2021_c.txt';
-- limit 10


DESCRIBE ALLmessages







