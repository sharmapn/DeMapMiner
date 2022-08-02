
CREATE TABLE accrejpeps2 AS
SELECT * 
FROM (SELECT DISTINCT PEP, state, DATE2, peptype						
	FROM pepstates_danieldata_datetimestamp  
	WHERE (STATE LIKE '%acc%' OR state LIKE '%rej%') 
	-- AND PEP IN (SELECT pep FROM pepdetails WHERE LENGTH (bdfl_delegatecorrected) > 0 ) 
	order by DATE2 ASC)
	AS tbA
LEFT JOIN ( SELECT pep AS P2, author, bdfl_delegatecorrected FROM pepdetails ) as tbC
	ON tbA.pep = tbC.p2
ORDER BY tbA.pep;

SELECT * FROM accrejpeps2;

SELECT pepnum2020, COUNT(messageid) 
FROM allmessages 
-- SELECT peptype
WHERE PEP IN (SELECT DISTINCT PEP FROM accrejpeps2)-- ONLY 245 peps
WHERE folder like '%dev%' ONE BY one
	AND authorsrole LIKE '%bdfl%' OR authorsrole LIKE '%proposal%'
GROUP BY authorsrole, peptype

-- DROP PROCEDURE getPEPListForEachMember_PEPTypeDMSuphase;
-- DROP PROCEDURE createEmailInvolvementForEachMember_PEPTypeDMSuphase;
-- CALL getPEPListForEachMember_PEPTypeDMSuphase();

TRUNCATE rolecommunicationsForEachMember_PEPTypeDMSubphase;

DELIMITER $$
CREATE PROCEDURE getPEPListForEachMember_PEPTypeDMSuphase()
BEGIN
	DECLARE finished INTEGER DEFAULT 0; DECLARE v_pep INTEGER DEFAULT 0; DECLARE v_messageId INTEGER DEFAULT 0; 
	DECLARE v_state TEXT(15); DECLARE v_date DATE; DECLARE v_peptype TEXT(15); 
	DECLARE curPEPList 
			CURSOR FOR 				
			 -- SELECT PEP, state, DATE2, peptype 
			 --	FROM (
						--	SELECT DISTINCT PEP, state, DATE2, peptype						
						--		FROM pepstates_danieldata_datetimestamp  
						--		WHERE PEP IN (
								SELECT PEP from allpepstillbdfl -- )
								WHERE PEP NOT IN (SELECT pep FROM bdflddpeps)  -- (SELECT PEP FROM allpepstillbdfl) -- (SELECT pep FROM bdflddpeps)
								-- (STATE LIKE '%acc%' OR state LIKE '%rej%') 
								-- AND PEP IN (SELECT pep FROM pepdetails WHERE LENGTH (bdfl_delegatecorrected) > 0 ) 
				--				order by DATE2 ASC)
				--		AS tbA
				--	LEFT JOIN ( SELECT pep AS P2, author, bdfl_delegatecorrected FROM pepdetails ) as tbC
				--		ON tbA.pep = tbC.p2
				--	ORDER BY tbA.pep;				 
				-- for those peps decided by bdfl delegate
				-- PEP IN (SELECT DISTINCT pep FROM pepdetails WHERE LENGTH (bdfl_delegatecorrected) > 0 )
				-- order by DATE2 asc;				
				DECLARE CONTINUE HANDLER 
        		FOR NOT FOUND SET finished = 1;
OPEN curPEPList;

getpepsstate: 
		LOOP
			FETCH curPEPList INTO v_pep; -- , v_state, v_date, v_peptype;
			IF finished = 1 THEN 
				LEAVE getpepsstate;
			END IF;
			call createEmailInvolvementForEachMember_PEPTypeDMSuphase(v_pep); -- ,v_state, v_date,v_peptype);
		END LOOP getpepsstate;
		CLOSE curPEPList;

END$$
DELIMITER ;


DELIMITER $$
-- SELECT DISTINCT lastdir FROM allmessages;
-- Alter table allmessages add INDEX `authorsrole_index` (`authorsrole`(20));
-- SELECT * FROM allmessages WHERE lastdir IS NULL LIMIT 20;

-- from previous script
-- CREATE or replace VIEW top10authors AS
--	SELECT DISTINCT AUTHOR, SUM(totalmsgs) AS S FROM communicationsforseeders group by author ORDER BY s DESC LIMIT 20;

CREATE PROCEDURE createEmailInvolvementForEachMember_PEPTypeDMSuphase(IN v_pep INT(10)) -- , IN v_state TEXT(15), IN v_date2 DATE, IN v_peptype TEXT(15))
BEGIN
	DECLARE finished INTEGER DEFAULT 0; DECLARE v_cnt INTEGER DEFAULT 0; DECLARE p_state TEXT; DECLARE vv_pep INTEGER;
	DECLARE v_authorsrole TEXT(30); DECLARE vv_peptype TEXT(30); DECLARE vv_lastdir TEXT(20); DECLARE v_author TEXT(30); 
--	SET p_state 
	DECLARE curgetMessages 
			CURSOR FOR 
				SELECT pepnum2020, peptype2020, clusterbysenderfullname, authorsrole, folder, COUNT(messageid) 				
				FROM allmessages -- FROM allmessages_bdfldelegatedecided2020 m
				WHERE pepnum2020 = v_pep -- IN (SELECT PEP FROM accrejpeps)				
				-- AND LENGTH(authorsrole) > 0 AND LENGTH(inReplyToUserUsingClusteredSenderrole) > 0 	
				-- WHERE -- lastdir = 'python-dev' -- ONE BY one
				-- AND 
				-- AND (authorsrole = 'bdfl' OR authorsrole = 'bdfl_delegate' OR authorsrole = 'proposalAuthor'
				AND clusterbysenderfullname IN (SELECT author FROM top10authors ORDER BY s desc)										
				GROUP BY pepnum2020, clusterbysenderfullname, folder;			
				-- ORDER BY authorsrole;
				-- declare NOT FOUND handler
				DECLARE CONTINUE HANDLER 
        		FOR NOT FOUND SET finished = 1;
OPEN curgetMessages;

getMessages: 
		LOOP
			FETCH curgetMessages INTO vv_pep, vv_peptype, v_author, v_authorsrole,  vv_lastdir, v_cnt; -- , v_cnt; 
			IF finished = 1 THEN 
				LEAVE getMessages;
			END IF;
			INSERT INTO rolecommunicationsforeachmember_peptypedmsubphase(pep, author, authorsrole, peptype, lastdir,  cnt) -- , cnt)
			VALUES (vv_pep, v_author, v_authorsrole, vv_peptype, vv_lastdir, v_cnt); -- , v_cnt);
		END LOOP getMessages;
		CLOSE curgetMessages;

END$$
DELIMITER ;
-- DESC rolecommunicationsforeachmember_peptypedmsubphase;
-- DESC rolecommunicationsForSNA;
SELECT DISTINCT authorsrole FROM allmessages;
-- ALTER table rolecommunicationsforeachmember_peptypedmsubphase DROP column inreplytouserclusteredsenderrole;
SELECT pep, authorsrole, peptype,lastdir,cnt 
FROM rolecommunicationsforeachmember_peptypedmsubphase
order BY pep asc

SELECT DISTINCT lastdir FROM rolecommunicationsforeachmember_peptypedmsubphase
UPDATE rolecommunicationsforeachmember_peptypedmsubphase SET lastdir = REPLACE (lastdir,'C:\\datasets\\','');
UPDATE rolecommunicationsforeachmember_peptypedmsubphase SET lastdir = REPLACE (lastdir,'C:\\datasets-additional\\','');

DESC communicationsforseeders
-- main query for this script whocjh works on the results


SELECT peptype, author, 
	-- authorsrole, -- lastdir, 
	-- if you dont want by DM subphase, comment this
	CASE lastdir
		WHEN 'python-ideas' THEN 'idea_generation'
		WHEN 'python-dev' THEN 'idea_discussion' WHEN 'python-checkins' THEN 'idea_discussion' 
		WHEN 'python-bugs-list' THEN 'idea_discussion' WHEN 'python-committers' THEN 'idea_discussion' 
		WHEN 'python-announce-list' THEN 'idea_discussion' WHEN 'python-distutils-sig' THEN 'idea_discussion'
		WHEN 'python-3000' THEN 'idea_discussion' WHEN 'python-patches' THEN 'idea_discussion'
		WHEN 'python-lists' THEN 'user_support'
	END AS DMsubphase,
SUM(cnt) AS cnt
FROM rolecommunicationsforeachmember_peptypedmsubphase
-- WHERE (lastdir='python-ideas' OR  lastdir='python-dev' OR lastdir='python-lists' OR  lastdir='python-checkins' OR  lastdir='python-committers' 
--		OR  lastdir='python-bugs-list' OR  lastdir = 'python-distutils-sig') 
GROUP BY peptype, author ASC -- , DMsubphase -- , authorsrole,  lastdir
order BY peptype DESC, cnt desc 

SELECT * FROM rolecommunicationsforeachmember_peptypedmsubphase WHERE lastdir IS NULL

--- debug wih thos query...why so less num messages
SELECT pep, pepnum2020, author, peptype, peptype2020, authorsrole,authorsrole2020, lastdir, COUNT(messageid) AS cnt 				
	FROM allmessages -- FROM allmessages_bdfldelegatedecided2020 m
	WHERE pepnum2020 = 308 -- IN (SELECT PEP FROM accrejpeps)				
	-- AND LENGTH(authorsrole) > 0 AND LENGTH(inReplyToUserUsingClusteredSenderrole) > 0 	
	-- WHERE -- lastdir = 'python-dev' -- ONE BY one
	-- AND 
--	AND (authorsrole = 'bdfl' OR authorsrole = 'bdfl_delegate' OR authorsrole = 'proposalAuthor')
	GROUP BY pepnum2020, author, authorsrole, lastdir			
	ORDER BY cnt desc;
	
-- ALTER TABLE allmessages ADD COLUMN authorsrole2020 TEXT;	
-- update allmessages set authorsrole2020 = authorsrole;	
-- Alter table allmessages add INDEX `authorsrole2020_index` (`authorsrole2020`(20));

-- ALTER TABLE allmessages ADD COLUMN peptype2020 TEXT;	
-- update allmessages set peptype2020 = peptype;	
-- Alter table allmessages add INDEX `peptype2020_index` (`peptype2020`(20));

-- these two sql are not executed yet
-- update allmessages set allmessages.pepType2020 = (select type from pepDetails where pepDetails.pep = allmessages.pepnum2020);
-- note pep 0 is not assigned to any pep type
-- UPDATE allmessages set pepType2020=left(pepType2020,1);



SELECT pep, pepType2020, left(pepType2020,1) FROM allmessages where pep >0 LIMIT 3;

SELECT pep, peptype, pepType2020, author, authorsrole 
FROM allmessages 
WHERE author LIKE 'guido'  AND pep <> -1
LIMIT 50 












