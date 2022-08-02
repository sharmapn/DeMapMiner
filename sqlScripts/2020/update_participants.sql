
DROP PROCEDURE get_to_update;
DROP PROCEDURE update_tablevalues;
CALL get_to_update();

DELIMITER $$
CREATE PROCEDURE get_to_update ()
BEGIN
	DECLARE finished INTEGER DEFAULT 0; DECLARE v_pep INTEGER DEFAULT 0; DECLARE v_peptype TEXT(15); DECLARE v_peptype2020 TEXT(20);
	DECLARE v_author TEXT(15); DECLARE v_author2020 TEXT(20); DECLARE v_pepnum2020 INTEGER DEFAULT 0; 
	DECLARE curPEPList 
			CURSOR FOR 
			SELECT pep, pepnum2020, peptype, peptype2020, author, authorsrole2020 
				FROM allmessages -- temptest
					WHERE pep = -1 AND pepnum2020 > 0 AND authorsrole2020 IS NULL; 		
				DECLARE CONTINUE HANDLER 
        		FOR NOT FOUND SET finished = 1;
OPEN curPEPList;

getpepsstate: 
		LOOP
			FETCH curPEPList INTO v_pep, v_pepnum2020, v_peptype, v_peptype2020, v_author, v_author2020;
			IF finished = 1 THEN 
				LEAVE getpepsstate;
			END IF;
			-- update for each row
			call update_tablevalues(v_pep, v_pepnum2020, v_peptype, v_peptype2020, v_author, v_author2020);
		END LOOP getpepsstate;
		CLOSE curPEPList;

END$$
DELIMITER ;

DELIMITER $$
CREATE PROCEDURE update_tablevalues (IN v_pep INT(10), IN v_pepnum2020 INT(10), IN v_peptype TEXT, IN v_peptype2020 TEXT, IN v_author TEXT, IN v_author2020 TEXT)
BEGIN
	DECLARE finished INTEGER DEFAULT 0; DECLARE vv_pep INTEGER DEFAULT 0; DECLARE vv_peptype TEXT(15); DECLARE vv_peptype2020 TEXT(20);
	DECLARE vv_author TEXT(15); DECLARE vv_authors2020 TEXT(20); DECLARE vv_authorsrole2020 TEXT(20); DECLARE vv_pepnum2020 INTEGER DEFAULT 0; 
--	SET p_state 
	DECLARE curgetMessages 
			CURSOR FOR 
				-- this query is just to get the authorsrole and later use it to set authorsrole2020 
				SELECT pep, pepnum2020, peptype, peptype2020, author, authorsrole2020 
					FROM allmessages -- table was temptest
							WHERE pepnum2020 = v_pepnum2020 AND pep = v_pepnum2020 AND author = v_author 
								AND pep <> -1 LIMIT 1;  -- just to be sure
				-- declare NOT FOUND handler
				DECLARE CONTINUE HANDLER 
        		FOR NOT FOUND SET finished = 1;
	-- INTO OUTFILE 'c:\\scripts\\SNA2020\\SNAAllDevelopersAfterClustering_19-12-2020_h.txt';
OPEN curgetMessages;

getMessages: 
		LOOP
			FETCH curgetMessages INTO vv_pep, vv_pepnum2020, vv_peptype, vv_peptype2020, vv_author, vv_authorsrole2020 ; 
			IF finished = 1 THEN 
				LEAVE getMessages;
			END IF;
			-- set the authorsrole2020
			UPDATE allmessages SET authorsrole2020 = vv_authorsrole2020 -- table was temptest
				WHERE author = vv_author
					AND pepnum2020 = vv_pepnum2020 AND authorsrole2020 IS NULL
					AND pep = -1 AND pepnum2020 > 0;			

		END LOOP getMessages;
		CLOSE curgetMessages;

END$$
DELIMITER ;

-- The above procedures were created as the implmentation of update query (attemptem below) was complicated and not working
-- UPDATE temptest AS t1,
-- (SELECT	authorsrole2020 FROM temptest WHERE pepnum2020 = t1.pepnum2020 AND pep = -1 AND pepnum2020 <> -1 LIMIT 1) as t2
-- SET t2.authorsrole2020 = t1.authorsrole2020 
-- WHERE t1.pepnum2020 = t2.pepnum2020
-- 	AND t2.pep = -1 AND t2.pepnum2020 <> -1;

DROP TABLE temptest;
CREATE TABLE temptest AS
SELECT pep, pepnum2020, author, peptype, peptype2020, authorsrole,authorsrole2020, lastdir, COUNT(messageid) AS cnt 				
	FROM allmessages -- FROM allmessages_bdfldelegatedecided2020 m
	WHERE pepnum2020 = 308 -- IN (SELECT PEP FROM accrejpeps)				
	-- AND LENGTH(authorsrole) > 0 AND LENGTH(inReplyToUserUsingClusteredSenderrole) > 0 	
	-- WHERE -- lastdir = 'python-dev' -- ONE BY one
	-- AND 
--	AND (authorsrole = 'bdfl' OR authorsrole = 'bdfl_delegate' OR authorsrole = 'proposalAuthor')
	GROUP BY pepnum2020, author, authorsrole, lastdir			
	ORDER BY cnt desc;

SELECT * FROM temptest 
-- where author LIKE '%guido%' 
ORDER BY author;


-- some rows remain unassigned `authorsrole2020'
SELECT pep, pepnum2020, peptype, peptype2020, clusterbysenderfullname, author, authorsrole2020 
	FROM allmessages -- temptest
	WHERE pep = -1 AND pepnum2020 > 0 AND authorsrole2020 IS NULL; 