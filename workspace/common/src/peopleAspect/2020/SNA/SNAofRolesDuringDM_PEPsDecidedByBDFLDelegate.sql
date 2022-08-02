-- SNA for roles for controversial PEPs
-- this is the main script containing sql for roles analysis for controversial peps
-- Jan 2021 .. we add another line so that we select communications for only those peps

-- note I am now using 'pepnum2020' filed instead of 'pep' field as the new field considers inreplytomessages of messgaes which are replies to messgaes with pep numbers
-- AND were previously NOT assigned, i.e. were WITH a -1 AS pep 

-- Note change the first query parameyters to lok at controversial peps or thiose by the bdfl delegate

-- Note if we want to see roles in the SNA graph, we need to lok at proposal author and bdfl delegate role
	-- we might as well create a table
	CREATE TABLE allmessages_bdfldelegatedecided2020 AS SELECT * FROM allmessages 
	WHERE PEPnum2020 IN (SELECT DISTINCT pep FROM pepdetails WHERE LENGTH (bdfl_delegatecorrected) > 0 );
	-- Then we assign the roles to the message sender and reciver -- but only proposal author and the bdfl delegate.
	-- update field of `authorsrole', `inReplyToUserUsingClusteredSender' to the role if they are from teh above role
	
	--first test some sql
	-- inReplyToUserUsingClusteredSenderrole = authorsrole  WHERE authorsrole = 'bdfl_delegate';
	SELECT author, clusterBySenderFullName FROM allmessages_bdfldelegatedecided2020 WHERE authorsrole <> 'proposalAuthor' AND authorsrole <> 'bdfl_delegate' AND authorsrole <> 'bdfl';
	SELECT inReplyToUserUsingClusteredSender, inReplyToUserUsingClusteredSenderrole FROM allmessages_bdfldelegatedecided2020 
		WHERE inReplyToUserUsingClusteredSenderrole <> 'proposalAuthor' AND inReplyToUserUsingClusteredSenderrole <> 'bdfl_delegate' 
			AND inReplyToUserUsingClusteredSenderrole <> 'bdfl';	
	-- SELECT DISTINCT authorsrole from allmessages_bdfldelegatedecided2020;
	-- SELECT DISTINCT inReplyToUserUsingClusteredSenderrole from allmessages_bdfldelegatedecided2020;
	
	-- easy way to do this .. we just set the authorsrole and inReplyToUserUsingClusteredSenderrole field 
	-- to the actual author unless they are proposalauthor or the bdfl_delegate or bdfl -- as thats what we a re interested in
	UPDATE allmessages_bdfldelegatedecided2020 SET authorsrole = clusterBySenderFullName  WHERE authorsrole <> 'proposalAuthor' AND authorsrole <> 'bdfl_delegate' AND authorsrole <> 'bdfl';
	UPDATE allmessages_bdfldelegatedecided2020 SET inReplyToUserUsingClusteredSenderrole = inReplyToUserUsingClusteredSender  
		WHERE inReplyToUserUsingClusteredSenderrole <> 'proposalAuthor' OR inReplyToUserUsingClusteredSenderrole <> 'bdfl_delegate';
	

-- do we need this line of code which we want to see direct connections at the moment
-- AND LENGTH(sendername) > 0 AND LENGTH(inReplyToUser) > 0 

-- DROP PROCEDURE getPEPList_BDFLDecidedPEPs;
-- DROP PROCEDURE createEmailInvolvementForEacMember_BDFLDecidedPEPs;
-- CALL getPEPList_BDFLDecidedPEPs();

TRUNCATE rolecommunicationsForSNA;

DELIMITER $$

CREATE PROCEDURE getPEPList_BDFLDecidedPEPs()
BEGIN
	DECLARE finished INTEGER DEFAULT 0; DECLARE v_pep INTEGER DEFAULT 0; DECLARE v_messageId INTEGER DEFAULT 0; DECLARE v_state TEXT(15); DECLARE v_date DATE;
	DECLARE curPEPList 
			CURSOR FOR 
				SELECT DISTINCT PEP, state, DATE2 FROM pepstates_danieldata_datetimestamp 
				WHERE -- (STATE LIKE '%acc%' OR state LIKE '%rej%') 
				-- for those peps decided by bdfl delegate
				PEP IN (SELECT DISTINCT pep FROM pepdetails WHERE LENGTH (bdfl_delegatecorrected) > 0 )
				order by DATE2 asc;				
				DECLARE CONTINUE HANDLER 
        		FOR NOT FOUND SET finished = 1;
OPEN curPEPList;

getpepsstate: 
		LOOP
			FETCH curPEPList INTO v_pep, v_state, v_date;
			IF finished = 1 THEN 
				LEAVE getpepsstate;
			END IF;
			call createEmailInvolvementForEacMember_BDFLDecidedPEPs(v_pep,v_state, v_date);
		END LOOP getpepsstate;
		CLOSE curPEPList;

END$$
DELIMITER ;

DELIMITER $$

CREATE PROCEDURE createEmailInvolvementForEacMember_BDFLDecidedPEPs(IN v_pep INT(10), IN v_state TEXT(15), IN v_date2 DATE)
BEGIN
	DECLARE finished INTEGER DEFAULT 0; DECLARE v_cnt INTEGER DEFAULT 0; DECLARE p_state TEXT; DECLARE vv_pep INTEGER;
	DECLARE v_author TEXT(30); DECLARE v_inReplyToUserUsingClusteredSender TEXT(30);
--	SET p_state 
	DECLARE curgetMessages 
			CURSOR FOR 
				SELECT pepNum2020, authorsrole, inReplyToUserUsingClusteredSenderrole 
				-- COUNT(messageid) AS cnt, -- messageid, author, authorsrole, DATE2, datetimestamp
--				PEP
--				, (SELECT state FROM pepstates_danieldata_datetimestamp WHERE PEP = m.PEP AND STATE LIKE 'acc' OR state LIKE '%rej%' order by DATE2 asc LIMIT 1)
--				author, inReplyToUserUsingClusteredSender, 
				-- (select authorsrole FROM commonroles WHERE author = m.author ),  
--				authorsrole, (select PEPCount FROM commonroles WHERE author = m.author ) AS pepinvolvementcount, -- add number of peps the member has contributed in
--				analysewords

				-- (select PEPCount FROM commonroles WHERE author = m.author ) AS cnt 
				FROM allmessages_bdfldelegatedecided2020 m
				WHERE pepnum2020 = v_pep -- IN (SELECT PEP FROM accrejpeps)
				--	AND state like '%acc%' or '%rej%'
				-- WHICH DATES WE WANT THE DATA
				-- 1. all dates
				-- AND DATE2 < (SELECT DATE2 FROM accrejpeps WHERE PEP = m.PEP)
				-- 2. before and including day state is committed
				-- AND DATE2 <= v_date2 -- (SELECT DATE2 FROM pepstates_danieldata_datetimestamp WHERE PEP = m.PEP AND (STATE LIKE 'acc' OR state LIKE '%rej%') order by DATE2 asc LIMIT 1)
				-- 3. only discussion one week before the day state is committed
				--	AND DATE2 BETWEEN 
				--		(SELECT DATE2 FROM pepstates_danieldata_datetimestamp WHERE PEP = m.PEP AND STATE LIKE 'acc' OR state LIKE '%rej%' order by DATE2 asc LIMIT 1) -7
				--		AND (SELECT DATE2 FROM pepstates_danieldata_datetimestamp WHERE PEP = m.PEP AND STATE LIKE 'acc' OR state LIKE '%rej%' order by DATE2 asc LIMIT 1)	
				-- AND folder LIKE '%dev%'
				-- we want to see direct connections at the moment
				AND LENGTH(authorsrole) > 0 AND LENGTH(inReplyToUserUsingClusteredSenderrole) > 0 
				-- AND (sendername in (select distinct(sendername) from alldevelopers) -- atleast one member is a developer
				-- OR inReplyToUser in (select distinct(sendername) from alldevelopers))
				-- GROUP BY 
				ORDER BY authorsrole;
				-- declare NOT FOUND handler
				DECLARE CONTINUE HANDLER 
        		FOR NOT FOUND SET finished = 1;
	-- INTO OUTFILE 'c:\\scripts\\SNA2020\\SNAAllDevelopersAfterClustering_19-12-2020_h.txt';
OPEN curgetMessages;

getMessages: 
		LOOP
			FETCH curgetMessages INTO vv_pep, v_author, v_inReplyToUserUsingClusteredSender; -- , v_cnt; 
			IF finished = 1 THEN 
				LEAVE getMessages;
			END IF;
			-- build email list
			-- SET emailList = CONCAT(emailAddress,";",emailList);		
			-- SELECT @id := cust_id FROM customers WHERE cust_id='customer name';
			INSERT INTO rolecommunicationsForSNA(pep, authorsrole, inReplyToUserUsingClusteredSenderRole) -- , cnt)
			VALUES (vv_pep, v_author, v_inReplyToUserUsingClusteredSender); -- , v_cnt);			

		END LOOP getMessages;
		CLOSE curgetMessages;

END$$
DELIMITER ;

-- DESC rolecommunicationsForSNA;

SELECT pep, authorsrole,",", inReplyToUserUsingClusteredSenderRole, ",", cnt 
FROM rolecommunicationsforsna

SELECT authorsrole,",", inReplyToUserUsingClusteredSenderRole, ",", cnt 
FROM rolecommunicationsforsna
INTO OUTFILE 'c:\\scripts\\SNA2020\\SNAAllDevelopersRoles_05-01-2021_k.txt';

