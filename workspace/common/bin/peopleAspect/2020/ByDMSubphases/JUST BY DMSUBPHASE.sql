-- march 13 2021
-- NOT grouped by pep type
-- If you need overall numbers for all 466 PEPs
-- REMOVE LINE FOR BDFL DELEGATE DECIDED PEPS IN THREE PLACES if...3 PLACES CHANGE TO 'NOT IN'

SELECT distinct folder FROM communicationsforseeders
UPDATE communicationsforseeders SET folder = REPLACE (folder,'C:\\datasets\\','');
UPDATE communicationsforseeders SET folder = REPLACE (folder,'C:\\datasets-additional\\','');
UPDATE pepthreadsbyperson2020 SET folder = REPLACE (folder,'C:\\datasets\\','');
UPDATE pepthreadsbyperson2020 SET folder = REPLACE (folder,'C:\\datasets-additional\\','');
UPDATE pepthreadsbyperson2020_PABD SET folder = REPLACE (folder,'C:\\datasets\\','');
UPDATE pepthreadsbyperson2020_PABD SET folder = REPLACE (folder,'C:\\datasets-additional\\','');

DROP TABLE finalsocialrolesbydmsubphases_bdfld;
-- CREATE TABLE finalsocialrolesbydmsubphases as -- for those by bdfl
CREATE TABLE finalsocialrolesbydmsubphases_bdfld as
SELECT * 					-- clusterbysenderfullname,
	FROM (-- social discussion metrics
		SELECT author AS m, folder as foldera,
					COUNT(distinct pep) AS pepInvolvement, SUM(totalmsgs) AS TotalMsgByPerson, 	-- SUM(totalmsgs)/COUNT(pep) AS AVEG, 
					SUM(msgs_not_replies) AS TotalTopLevelMsgsByPerson, 	-- SUM(msgs_not_replies)/COUNT(pep) AS AVEG2, 
					SUM(msgs_are_replies) AS TotalRepliesByPersonToDiffMsgs, 	-- SUM(msgs_are_replies)/COUNT(pep) AS AVEG3,
					SUM(totalinreplyto) AS TotalRepliesToThisPerson 	-- SUM(totalinreplyto)/COUNT(pep) AS AVEG4,
					-- SUM(summessages) AS TotalMsgsForPEP -- , dominance_index
					from communicationsforseeders
				-- WHERE author IN (SELECT author FROM top10authors)
					WHERE (author IN (SELECT author FROM top10authors) OR author = 'proposalauthor'  OR author = 'bdfl_delegate' )
					-- for 451 peps decided by bdfl, JUST CHANGE TO 'NOT IN'	 or IN as neded				
					-- add for 51 peps by bdfl delegate
					AND pep IN (SELECT pep FROM bdflddpeps) -- AND PEPnum2020 NOT IN (SELECT pep FROM bdflddpeps) 
					GROUP BY author,  folder -- , peptype -- , state -- , peptype, state
					ORDER BY pepInvolvement DESC, TotalMsgByPerson DESC
		) AS tbA
	-- We join with how many times this particular person has been replied to
							-- inReplyToUserUsingClusteredSender
	LEFT JOIN (	
		SELECT clusterbysenderfullname AS n, COUNT(DISTINCT threadid) AS threadCNT , peptype2020, folder
			FROM pepthreadsbyperson2020 
			-- add for 51 peps by bdfl delegate
			WHERE pepnum2020 IN (SELECT pep FROM bdflddpeps) -- AND PEPnum2020 NOT IN (SELECT pep FROM bdflddpeps)
			-- where threadlevel < 3  ...for firsta nd second level results
			GROUP BY clusterbysenderfullname, peptype2020, folder
			-- ORDER BY threadCNT DESC
		UNION
			SELECT authorsrole AS m, COUNT(DISTINCT threadid) AS threadCNT, peptype2020, folder
			FROM pepthreadsbyperson2020_PABD 
			-- where threadlevel < 3  ...for firsta nd second level results
			WHERE authorsrole = 'proposalAuthor' OR authorsrole = 'bdfl_delegate'
			-- add for 51 peps by bdfl delegate
			AND pepnum2020 IN (SELECT pep FROM bdflddpeps) -- AND PEPnum2020 NOT IN (SELECT pep FROM bdflddpeps)
			GROUP BY authorsrole, folder
		ORDER BY threadCNT DESC
		) as tbC
	-- ON tbA.clusterbysenderfullname = tbC.inReplyToUserUsingClusteredSender;
	ON tbA.m = tbC.n
	AND tbA.foldera= tbC.folder
	ORDER BY tbA.pepInvolvement desc;

--- 13 march nopefully final 
SELECT * from finalsocialrolesbydmsubphases

SELECT m, 
	-- if you dont want by DM subphase, comment this
	CASE foldera
		WHEN 'python-ideas' THEN 'idea_generation'
		WHEN 'python-dev' THEN 'idea_discussion' WHEN 'python-checkins' THEN 'idea_discussion' 
		WHEN 'python-bugs-list' THEN 'idea_discussion' WHEN 'python-committers' THEN 'idea_discussion' 
		WHEN 'python-announce-list' THEN 'idea_discussion' WHEN 'python-distutils-sig' THEN 'idea_discussion'
		WHEN 'python-3000' THEN 'idea_discussion' WHEN 'python-patches' THEN 'idea_discussion'
		WHEN 'python-lists' THEN 'user_support'
	END AS DMsubphase, 
	 pepinvolvement, totalmsgbyperson, totaltoplevelmsgsbyperson, totalrepliesbypersontodiffmsgs, totalrepliestothisperson, threadcnt 
	FROM finalsocialrolesbydmsubphases_bdfld -- _bdfld -- chose from either for y
--	WHERE pairwise IN (SELECT pairwise FROM top10pairwise)
--		AND (author = 'Guido van Rossum' OR author = 'Nick Coghlan' OR author = 'proposalAuthor')  -- author = 'bdfl_delegate'
--		AND PEP NOT IN (SELECT PEP FROM bdflddpeps)
	-- AND pep IN (SELECT pep FROM pepdetails WHERE LENGTH (bdfl_delegatecorrected) > 0 ) -- if you only want the peps decided by bdfl delegate
--	WHERE peptypea = 'Informational'  -- 'Process' -- 'Standards Track' -- Informational
   GROUP BY  m, DMsubphase -- peptypea, 
	-- ORDER BY summsg desc
	ORDER BY pepinvolvement desc,m


