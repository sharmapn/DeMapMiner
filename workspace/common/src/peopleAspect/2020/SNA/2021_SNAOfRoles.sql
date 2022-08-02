-- this is the main script for sna or roles. It has all scripts and does not need any other python or java scripts

DROP TABLE pepinvolvement;
CREATE TABLE pepinvolvement AS 
	SELECT author AS clusterBySenderFullName,  COUNT(distinct pep) AS pepcount -- peptype,
   FROM communicationsforseeders -- allmessages
	WHERE pep IN (SELECT PEP FROM allpepstillbdfl) 
	GROUP BY clusterBySenderFullName -- , peptype
	ORDER BY pepcount desc
	LIMIT 20; -- 554; -- 79; -- just so we can get the bdfl_delegate
-- do same for the 52 peps decided by bdfl delegate
-- DROP TABLE pepinvolvement_bdfld;
CREATE TABLE pepinvolvement_bdfld AS 
	SELECT author AS clusterBySenderFullName,  COUNT(distinct pep) AS pepcount -- peptype,
   FROM communicationsforseeders -- allmessages
	WHERE pep IN (SELECT PEP FROM bdflddpeps) 
	GROUP BY clusterBySenderFullName -- , peptype
	ORDER BY pepcount desc
	LIMIT 20;	
	
--	SELECT * FROM pepinvolvement
-- DROP TABLE 2021_SNAofroles;
CREATE TABLE 2021_SNAofroles AS -- pep, messageid,
	select  clusterBySenderFullName, "," AS a , inReplyToUserUsingClusteredSender, "," AS b, 
				-- (select authorsrole FROM commonroles WHERE author = m.author ),  
				authorsrole," ," AS c, inReplyToUserUsingClusteredSenderRole, ",  " AS d,
				(select PEPCount FROM pepinvolvement WHERE clusterBySenderFullName = m.clusterBySenderFullName ) AS pepinvolvementcount -- add number of peps the member has contributed in
		from allmessages m
		WHERE PEPnum2020 IN (SELECT PEP FROM allpepstillbdfl) 
				AND PEPnum2020 NOT IN (SELECT pep FROM bdflddpeps) --  WHERE LENGTH (bdfl_delegatecorrected) > 0 ) 
		-- AND DATE2 < (SELECT DATE2 FROM accrejpeps WHERE PEP = m.PEP)
		-- jan 2021 we consider all lailing lists
		-- AND folder LIKE '%dev%'
		-- we want to see direct connections at the moment
		-- AND clusterBySenderFullName IN (select clusterBySenderFullName FROM pepinvolvement)
		AND LENGTH(clusterBySenderFullName) > 0 
		AND LENGTH(inReplyToUser) > 0 
		-- AND (sendername in (select distinct(sendername) from alldevelopers) -- atleast one member is a developer
		-- OR inReplyToUser in (select distinct(sendername) from alldevelopers))
		order by clusterBySenderFullName;
		
		-- we had to rund some more data cleaning

-- DROP TABLE 2021_SNAofroles_bdfld;
CREATE TABLE 2021_SNAofroles_bdfld as
	select clusterBySenderFullName, " , " AS a, inReplyToUserUsingClusteredSender, "," AS b, 
				-- (select authorsrole FROM commonroles WHERE author = m.author ),  
				authorsrole, " ," AS c, inReplyToUserUsingClusteredSenderRole, ",  " AS d,
				(select PEPCount FROM pepinvolvement_bdfld WHERE clusterBySenderFullName = m.clusterBySenderFullName ) AS pepinvolvementcount -- add number of peps the member has contributed in
		from allmessages m
		WHERE PEPnum2020 IN (SELECT pep FROM bdflddpeps)  -- (SELECT PEP FROM allpepstillbdfl) -- (SELECT pep FROM bdflddpeps)
		-- AND DATE2 < (SELECT DATE2 FROM accrejpeps WHERE PEP = m.PEP)
		-- jan 2021 we consider all lailing lists
		-- AND folder LIKE '%dev%'
		-- we want to see direct connections at the moment
		AND LENGTH(clusterBySenderFullName) > 0 
		AND LENGTH(inReplyToUser) > 0 
		-- AND clusterBySenderFullName IN (select clusterBySenderFullName FROM pepinvolvement)
		-- AND (sendername in (select distinct(sendername) from alldevelopers) -- atleast one member is a developer
		-- OR inReplyToUser in (select distinct(sendername) from alldevelopers))
		order by clusterBySenderFullName;
		
	-- 10 march 2021...we need to assign the pep counts again to the three roles, as previusly assignment was not done or roles, but on the person
	UPDATE 2021_SNAofroles SET clusterBySenderFullName = 'proposalAuthor'  WHERE authorsrole = 'proposalAuthor' ;
	UPDATE 2021_SNAofroles_bdfld SET clusterBySenderFullName = 'proposalAuthor'  WHERE authorsrole = 'proposalAuthor' ;
	-- PEP Involvement we only set this one..in nodexl we wil use this as the Node A's property
	UPDATE 2021_snaofroles a INNER JOIN pepinvolvement b ON a.clusterBySenderFullName = b.clusterBySenderFullName SET pepinvolvementcount = b.pepcount; 
	UPDATE 2021_SNAofroles_bdfld a INNER JOIN pepinvolvement b ON a.clusterBySenderFullName = b.clusterBySenderFullName SET pepinvolvementcount = b.pepcount;
	
	--	= (select PEPCount FROM pepinvolvement WHERE author = m.clusterBySenderFullName ) -- AS pepinvolvementcount
		
	-- we need to do this for BDFL and proposal author
	-- UPDATE 2021_SNAofroles SET pepinvolvementcount = 454  WHERE clusterBySenderFullName = 'BDFL';
	-- UPDATE 2021_SNAofroles_bdfld a
	-- INNER JOIN pepinvolvement b ON a.clusterBySenderFullName = b.clusterBySenderFullName
	-- SET pepinvolvementcount = b.pepcount;
	-- UPDATE 2021_SNAofroles SET pepinvolvementcount = 443  WHERE clusterBySenderFullName = 'PEP Author';	

	-- now we update the names so that they are meningful in the SNA diagram		
	-- first to correct errors in assignment of roles by earlier sciprts
	UPDATE 2021_SNAofroles SET clusterBySenderFullName = 'BDFL'  						WHERE clusterBySenderFullName = 'guido van rossum';	
	UPDATE 2021_SNAofroles SET inReplyToUserUsingClusteredSender = 'BDFL'  			WHERE inReplyToUserUsingClusteredSender = 'guido van rossum';		
	UPDATE 2021_SNAofroles_bdfld SET clusterBySenderFullName = 'BDFL'  				WHERE clusterBySenderFullName = 'guido van rossum';	
	UPDATE 2021_SNAofroles_bdfld SET inReplyToUserUsingClusteredSender = 'BDFL'  	WHERE inReplyToUserUsingClusteredSender = 'guido van rossum';
-- now we set the node values for the nodes which are important roles		
-- easy way to do this .. we just set the authorsrole and inReplyToUserUsingClusteredSenderrole field 
	-- to the actual author unless they are proposalauthor or the bdfl_delegate or bdfl -- as thats what we a re interested in
	UPDATE 2021_SNAofroles SET clusterBySenderFullName = 'PEP Author'  WHERE authorsrole = 'proposalAuthor' ;
	UPDATE 2021_SNAofroles SET clusterBySenderFullName = 'BDFL Delegate'  WHERE authorsrole = 'bdfl_delegate'; 
	-- UPDATE 2021_SNAofroles SET clusterBySenderFullName = 'BDFL'  WHERE authorsrole = 'bdfl';	
	UPDATE 2021_SNAofroles SET inReplyToUserUsingClusteredSender = 'PEP Author'  WHERE inReplyToUserUsingClusteredSenderRole = 'proposalAuthor' ;
	UPDATE 2021_SNAofroles SET inReplyToUserUsingClusteredSender = 'BDFL Delegate'   WHERE inReplyToUserUsingClusteredSenderRole = 'bdfl_delegate' ;
	UPDATE 2021_SNAofroles SET inReplyToUserUsingClusteredSender = 'BDFL'  WHERE inReplyToUserUsingClusteredSenderRole = 'bdfl';
	
	UPDATE 2021_SNAofroles_bdfld SET clusterBySenderFullName = 'PEP Author'  WHERE authorsrole = 'proposalAuthor' ;
	UPDATE 2021_SNAofroles_bdfld SET clusterBySenderFullName = 'BDFL Delegate'  WHERE authorsrole = 'bdfl_delegate'; 
	-- UPDATE 2021_SNAofroles_bdfld SET clusterBySenderFullName = 'BDFL'  WHERE authorsrole = 'bdfl';	
	UPDATE 2021_SNAofroles_bdfld SET inReplyToUserUsingClusteredSender = 'PEP Author'   WHERE inReplyToUserUsingClusteredSenderRole = 'proposalAuthor' ;
	UPDATE 2021_SNAofroles_bdfld SET inReplyToUserUsingClusteredSender = 'BDFL Delegate'   WHERE inReplyToUserUsingClusteredSenderRole = 'bdfl_delegate' ;
	UPDATE 2021_SNAofroles_bdfld SET inReplyToUserUsingClusteredSender = 'BDFL'  WHERE inReplyToUserUsingClusteredSenderRole = 'bdfl';
	
	-- we need to do this for BDFL and proposal author
	-- UPDATE 2021_SNAofroles SET pepinvolvementcount = 454  WHERE clusterBySenderFullName = 'BDFL';
	-- UPDATE 2021_SNAofroles SET pepinvolvementcount = 443  WHERE clusterBySenderFullName = 'PEP Author';
	-- UPDATE 2021_SNAofroles_bdfld SET pepinvolvementcount = 51  WHERE clusterBySenderFullName = 'BDFL Delegate';
	-- UPDATE 2021_SNAofroles_bdfld SET pepinvolvementcount = 443  WHERE clusterBySenderFullName = 'PEP Author';
		 
SELECT * FROM 2021_SNAofroles -- LIMIT 20
	WHERE inReplyToUserUsingClusteredSender <> 'PEP Author' and inReplyToUserUsingClusteredSenderRole <> 'PEP Author'
-- ORDER BY pepinvolvementcount desc
-- WHERE clusterBySenderFullName = 'BDFL';	
	INTO OUTFILE 'c:\\scripts\\SNA2020\\SNA_2021_AllDevelopers_10-03-2021_a.txt';
/* Affected rows: 39,705  Found rows: 0  Warnings: 0  Duration for 1 query: 2.403 sec. */
SELECT * FROM 2021_SNAofroles_bdfld
	WHERE inReplyToUserUsingClusteredSender <> 'PEP Author' and inReplyToUserUsingClusteredSenderRole <> 'PEP Author'
		INTO OUTFILE 'c:\\scripts\\SNA2020\\SNA_2021_OnlyBDFLDelegate_10-03-2021_a.txt';
/* Affected rows: 6,895  Found rows: 0  Warnings: 0  Duration for 1 query: 0.328 sec. */