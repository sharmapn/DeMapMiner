-- this is the main script for sna or roles. It has all scripts and does not need any other python or java scripts

-- we try again in feb 2021

SELECT bip, emailmessageid, inreplyto , inreplytouser, sendername
FROM allmessages 
WHERE bip IN (9, 141, 341,342)
LIMIT 20

-- update this field, using previously used script
UPDATE allmessages a1
JOIN allmessages a2 ON a2.emailMessageID = a1.inReplyTo
SET a1.inReplyToUser = a2.senderName
WHERE a1.inReplyTo IS NOT NULL


DROP TABLE bipinvolvement;
CREATE TABLE bipinvolvement AS 
	SELECT senderName, COUNT(distinct bip) AS bipcount 
	FROM allmessages
	WHERE bip IN (9,141, 341, 342)
	GROUP BY senderName
	ORDER BY bipcount desc
	LIMIT 20;
--	SELECT * FROM pepinvolvement
-- DROP TABLE SNAofmembers4BIPs;
CREATE TABLE SNAofmembers4BIPs AS
	select senderName, "," AS a , inReplyToUser, "," AS b, 
				-- (select authorsrole FROM commonroles WHERE author = m.author ),  
				authorsrole," ," AS c
				-- , inReplyToUser, ",  " AS d
-- 			(select PEPCount FROM pepinvolvement WHERE clusterBySenderFullName = m.author ) AS pepinvolvementcount -- add number of peps the member has contributed in
		from allmessages m
		WHERE bip = 342 -- IN (9,141, 341, 342)
		-- PEPnum2020 IN (SELECT PEP FROM allpepstillbdfl) 
				-- AND PEPnum2020 NOT IN (SELECT pep FROM pepdetails WHERE LENGTH (bdfl_delegatecorrected) > 0 ) 
		-- AND DATE2 < (SELECT DATE2 FROM accrejpeps WHERE PEP = m.PEP)
		-- jan 2021 we consider all lailing lists
		-- AND folder LIKE '%dev%'
		-- we want to see direct connections at the moment
		AND LENGTH(senderName) > 0 
	--	AND LENGTH(inReplyToUser) > 0 
		-- AND (sendername in (select distinct(sendername) from alldevelopers) -- atleast one member is a developer
		-- OR inReplyToUser in (select distinct(sendername) from alldevelopers))
		order by senderName
		INTO OUTFILE 'c:\\scripts\\BIPs\\BIPSSNA_2021_AllDevelopers_18-02-2021_bip342.txt';

SELECT * FROM SNAofmembers4BIPs
	WHERE bip = 9
	INTO OUTFILE 'c:\\scripts\\BIPs\\BIPSSNA_2021_AllDevelopers_18-02-2021_a.txt';

SELECT * FROM allmessages WHERE bip = 9
AND sendername = '=?UTF-8?B?Sm9yZ2UgVGltw7Nu?='

-- backup
-- ALTER TABLE allmessages ADD COLUMN oldsendername TEXT(50);
-- UPDATE allmessages SET oldsendername = sendername;

SELECT * from allmessages WHERE sendername = '=?UTF-8?B?Sm9yZ2UgVGltw7Nu?=';
SELECT * from allmessages WHERE sendername = '=?UTF-8?Q?Hampus_Sj=C3=B6berg?=';
-- correct these

UPDATE allmessages SET sendername = TRIM(sendername);
UPDATE allmessages SET inreplytouser = TRIM(inreplytouser);

UPDATE allmessages SET sendername = 'Jorge Timón' WHERE sendername = '=?UTF-8?B?Sm9yZ2UgVGltw7Nu?=';
UPDATE allmessages SET inreplytouser = 'Jorge Timón' WHERE inreplytouser = '=?UTF-8?B?Sm9yZ2UgVGltw7Nu?=';
UPDATE allmessages SET sendername = 'Hampus Sjöberg' WHERE sendername = '=?UTF-8?Q?Hampus_Sj=C3=B6berg?=';
UPDATE allmessages SET inreplytouser = 'Hampus Sjöberg' WHERE inreplytouser = '=?UTF-8?Q?Hampus_Sj=C3=B6berg?=';


-- DROP TABLE 2021_SNAorroles_bdfld;
CREATE TABLE 2021_SNAofroles_bdfld as
	select clusterBySenderFullName, " , " AS a, inReplyToUserUsingClusteredSender, "," AS b, 
				-- (select authorsrole FROM commonroles WHERE author = m.author ),  
				authorsrole, " ," AS c, inReplyToUserUsingClusteredSenderRole, ",  " AS d,
				(select PEPCount FROM pepinvolvement WHERE clusterBySenderFullName = m.author ) AS pepinvolvementcount -- add number of peps the member has contributed in
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
		order by clusterBySenderFullName;
		
	-- first to correct errors in assignment of roles by earlier sciprts
	UPDATE 2021_SNAofroles SET clusterBySenderFullName = 'bdfl'  						WHERE clusterBySenderFullName = 'guido van rossum';	
	UPDATE 2021_SNAofroles SET inReplyToUserUsingClusteredSender = 'bdfl'  			WHERE inReplyToUserUsingClusteredSender = 'guido van rossum';		
	UPDATE 2021_SNAofroles_bdfld SET clusterBySenderFullName = 'bdfl'  				WHERE clusterBySenderFullName = 'guido van rossum';	
	UPDATE 2021_SNAofroles_bdfld SET inReplyToUserUsingClusteredSender = 'bdfl'  	WHERE inReplyToUserUsingClusteredSender = 'guido van rossum';
-- now we set the node values for the nodes which are important roles		
-- easy way to do this .. we just set the authorsrole and inReplyToUserUsingClusteredSenderrole field 
	-- to the actual author unless they are proposalauthor or the bdfl_delegate or bdfl -- as thats what we a re interested in
	UPDATE 2021_SNAofroles SET clusterBySenderFullName = authorsrole  WHERE authorsrole = 'proposalAuthor' ;
	UPDATE 2021_SNAofroles SET clusterBySenderFullName = authorsrole  WHERE authorsrole = 'bdfl_delegate'; 
	UPDATE 2021_SNAofroles SET clusterBySenderFullName = authorsrole  WHERE authorsrole = 'bdfl';	
	UPDATE 2021_SNAofroles SET inReplyToUserUsingClusteredSender = inReplyToUserUsingClusteredSenderRole  WHERE inReplyToUserUsingClusteredSenderRole = 'proposalAuthor' ;
	UPDATE 2021_SNAofroles SET inReplyToUserUsingClusteredSender = inReplyToUserUsingClusteredSenderRole  WHERE inReplyToUserUsingClusteredSenderRole = 'bdfl_delegate' ;
	UPDATE 2021_SNAofroles SET inReplyToUserUsingClusteredSender = inReplyToUserUsingClusteredSenderRole  WHERE inReplyToUserUsingClusteredSenderRole = 'bdfl';
	
	UPDATE 2021_SNAofroles_bdfld SET clusterBySenderFullName = authorsrole  WHERE authorsrole = 'proposalAuthor' ;
	UPDATE 2021_SNAofroles_bdfld SET clusterBySenderFullName = authorsrole  WHERE authorsrole = 'bdfl_delegate'; 
	UPDATE 2021_SNAofroles_bdfld SET clusterBySenderFullName = authorsrole  WHERE authorsrole = 'bdfl';	
	UPDATE 2021_SNAofroles_bdfld SET inReplyToUserUsingClusteredSender = inReplyToUserUsingClusteredSenderRole  WHERE inReplyToUserUsingClusteredSenderRole = 'proposalAuthor' ;
	UPDATE 2021_SNAofroles_bdfld SET inReplyToUserUsingClusteredSender = inReplyToUserUsingClusteredSenderRole  WHERE inReplyToUserUsingClusteredSenderRole = 'bdfl_delegate' ;
	UPDATE 2021_SNAofroles_bdfld SET inReplyToUserUsingClusteredSender = inReplyToUserUsingClusteredSenderRole  WHERE inReplyToUserUsingClusteredSenderRole = 'bdfl';
		
SELECT * FROM 2021_SNAofroles
	INTO OUTFILE 'c:\\scripts\\SNA2020\\SNA_2021_AllDevelopers_20-01-2021_b.txt';
/* Affected rows: 39,705  Found rows: 0  Warnings: 0  Duration for 1 query: 2.403 sec. */
SELECT * FROM 2021_SNAofroles_bdfld
		INTO OUTFILE 'c:\\scripts\\SNA2020\\SNA_2021_OnlyBDFLDelegate_20-01-2021_c.txt';
/* Affected rows: 6,895  Found rows: 0  Warnings: 0  Duration for 1 query: 0.328 sec. */