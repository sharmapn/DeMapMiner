-- mARCH 08 2021..we combine the results
-- now sepration by dm sub-phase	
			
SELECT FOLDER, COUNT(*) FROM communicationsforseeders GROUP BY folder
SELECT * FROM communicationsforseeders LIMIT 10
SELECT * FROM communicationsforseeders WHERE folder IS NULL
SELECT folder, COUNT(*) FROM allmessages WHERE folder IS NULL
-- pep summaries have folder as null
SELECT * FROM allmessages WHERE folder IS NULL ORDER BY pep asc
SELECT DISTINCT folder FROM communicationsforseeders
-- add folder in two places to get groups by folder from the same query
				-- folder

SELECT * from communicationsforseeders				
				
SELECT author, -- substring(folder,13), 
	CASE folder --  substring(folder,13) 
		WHEN 'python-ideas' THEN 'idea_generation'
		WHEN 'python-dev' THEN 'idea_discussion' WHEN 'python-checkins' THEN 'idea_discussion' 
		WHEN 'python-bugs-list' THEN 'idea_discussion' WHEN 'python-committers' THEN 'idea_discussion' 
		WHEN 'python-announce-list' THEN 'idea_discussion' WHEN 'python-distutils-sig' THEN 'idea_discussion'
		WHEN 'python-3000' THEN 'idea_discussion' WHEN 'python-patches' THEN 'idea_discussion' 
		WHEN 'python-lists' THEN 'user_support'
	END AS DMsubphase,
	COUNT( pep) AS pepInvolvement, SUM(totalmsgs) AS TotalMsgByPerson, 
	-- SUM(totalmsgs)/COUNT(pep) AS AVEG, 
	SUM(msgs_not_replies) AS TotalTopLevelMsgsByPerson, 
	-- SUM(msgs_not_replies)/COUNT(pep) AS AVEG2, 
	SUM(msgs_are_replies) AS TotalRepliesByPersonToDiffMsgs, 
	-- SUM(msgs_are_replies)/COUNT(pep) AS AVEG3,
	SUM(totalinreplyto) AS TotalRepliesToThisPerson 
	-- SUM(totalinreplyto)/COUNT(pep) AS AVEG4,
	-- SUM(summessages) AS TotalMsgsForPEP -- , dominance_index
from communicationsforseeders
-- WHERE author IN (SELECT author FROM top10authors)
WHERE (author IN (SELECT author FROM top10authors) OR author = 'proposalauthor'  OR author = 'bdfl_delegate' )
	AND folder IS NOT NULL
	GROUP BY author, DMsubphase -- folder -- , peptype -- , state -- , peptype, state
	ORDER BY pepInvolvement DESC, TotalMsgByPerson DESC -- AUTHOR -- SUM_totalmsgs DESC -- (SELECT AUTHOR FROM communicationsforseeders ORDER BY SUM(totalmsgs) ) DESC 
	
--- 09 March 2021 combine the above query with thread involvement
-- removes instances of 'where pep = ' for all peps rather than just the 52
SELECT * 					-- clusterbysenderfullname,
	FROM (-- social discussion metrics
		SELECT author AS m, -- substring(folder,13), 
			-- CASE substring(folder,13)
			-- WHEN 'python-ideas' THEN 'idea_generation' WHEN 'python-dev' THEN 'idea_discussion' WHEN 'python-checkins' THEN 'idea_discussion'
			-- WHEN 'python-bugs-list' THEN 'idea_discussion' WHEN 'python-committers' THEN 'idea_discussion'
			-- WHEN 'python-announce-list' THEN 'idea_discussion' WHEN 'python-distutils-sig' THEN 'idea_discussion'
			-- WHEN 'additional\\python-3000' THEN 'idea_discussion' WHEN 'python-lists' THEN 'user_support'
			-- END AS DMsubphase,
			peptype,
			COUNT(distinct pep) AS pepInvolvement, SUM(totalmsgs) AS TotalMsgByPerson, 		
			SUM(msgs_not_replies) AS TotalTopLevelMsgsByPerson, 
			SUM(msgs_are_replies) AS TotalRepliesByPersonToDiffMsgs, 
			SUM(totalinreplyto) AS TotalRepliesToThisPerson 
			from communicationsforseeders
			-- WHERE author IN (SELECT author FROM top10authors)
			WHERE (author IN (SELECT author FROM top10authors) OR author = 'proposalauthor'  OR author = 'bdfl_delegate' )
				AND folder IS NOT NULL
				-- ONLY IF U WANT THE 52 PEPS
				-- AND pep IN (SELECT pep FROM bdflddpeps) -- (SELECT pep FROM pepdetails WHERE created < '2018-07-11' AND LENGTH(bdfl_delegate)>0 )
				GROUP BY author, peptype -- folder -- , peptype -- , state -- , peptype, state
				ORDER BY pepInvolvement DESC, TotalMsgByPerson DESC
		) AS tbA
	-- We join with how many times this particular person has been replied to
							-- inReplyToUserUsingClusteredSender
						--	DESC pepthreadsbyperson2020;
	LEFT JOIN (	
		SELECT clusterbysenderfullname AS m, COUNT(DISTINCT threadid) AS threadCNT, 
			peptype2020
			-- CASE substring(folder,13)
			-- WHEN 'python-ideas' THEN 'idea_generation' WHEN 'python-dev' THEN 'idea_discussion' WHEN 'python-checkins' THEN 'idea_discussion'
			-- WHEN 'python-bugs-list' THEN 'idea_discussion' WHEN 'python-committers' THEN 'idea_discussion'
			-- WHEN 'python-announce-list' THEN 'idea_discussion' WHEN 'python-distutils-sig' THEN 'idea_discussion'
			-- WHEN 'additional\\python-3000' THEN 'idea_discussion' WHEN 'python-lists' THEN 'user_support'
-- 			END AS DMsubphase		 
			FROM pepthreadsbyperson2020 
			WHERE clusterbysenderfullname IN (SELECT author FROM top10authors) -- OR authorsrole = 'proposalauthor'  OR authorsrole = 'bdfl_delegate' 
			-- where threadlevel < 3  ...for firsta nd second level results
				-- ONLY IF U WANT THE 52 PEPS
			-- AND pepnum2020 IN (SELECT pep FROM bdflddpeps) -- (SELECT pep FROM pepdetails WHERE created < '2018-07-11' AND LENGTH(bdfl_delegate)>0 )
			GROUP BY clusterbysenderfullname, peptype2020
			-- ORDER BY threadCNT DESC
		UNION
			SELECT authorsrole AS m, COUNT(DISTINCT threadid) AS threadCNT, peptype2020
			-- CASE substring(folder,13)
			-- WHEN 'python-ideas' THEN 'idea_generation' WHEN 'python-dev' THEN 'idea_discussion' WHEN 'python-checkins' THEN 'idea_discussion'
			-- WHEN 'python-bugs-list' THEN 'idea_discussion' WHEN 'python-committers' THEN 'idea_discussion'
			-- WHEN 'python-announce-list' THEN 'idea_discussion' WHEN 'python-distutils-sig' THEN 'idea_discussion'
			--	WHEN 'additional\\python-3000' THEN 'idea_discussion' WHEN 'python-lists' THEN 'user_support'
			-- END AS DMsubphase 
			FROM pepthreadsbyperson2020_PABD 
			-- where threadlevel < 3  ...for firsta nd second level results
			WHERE authorsrole = 'proposalAuthor' OR authorsrole = 'bdfl_delegate'
			-- ONLY IF U WANT THE 52 PEPS
			-- AND pepnum2020 IN (SELECT pep FROM bdflddpeps) -- (SELECT pep FROM pepdetails WHERE created < '2018-07-11' AND LENGTH(bdfl_delegate)>0 )
			GROUP BY authorsrole, peptype2020
		ORDER BY threadCNT DESC
		) as tbC
	-- ON tbA.clusterbysenderfullname = tbC.inReplyToUserUsingClusteredSender;
	ON tbA.m = tbC.m 
	-- AND tbA.DMsubphase = tbC.DMsubphase	
	AND tbA.peptype = tbC.peptype2020
	ORDER BY tbA.pepinvolvement desc
	-- ORDER BY tbA.DMsubphase, tbA.totalmsgbyperson desc;


-- old queries below

	
-- above query for bar chart
-- CREATE TABLE tstret as
SELECT author, -- substring(folder,13), 
	CASE substring(folder,13) 
		WHEN 'python-ideas' THEN 'idea_generation'
		WHEN 'python-dev' THEN 'idea_discussion' WHEN 'python-checkins' THEN 'idea_discussion' 
		WHEN 'python-bugs-list' THEN 'idea_discussion' WHEN 'python-committers' THEN 'idea_discussion' 
		WHEN 'python-announce-list' THEN 'idea_discussion' WHEN 'python-distutils-sig' THEN 'idea_discussion'
		WHEN 'additional\\python-3000' THEN 'idea_discussion' 
		WHEN 'python-lists' THEN 'user_support'
	END AS DMsubphase,
	-- COUNT(distinct pep) AS pepInvolvement, 
	SUM(totalmsgs) AS TotalMsgByPerson, 
	-- SUM(totalmsgs)/COUNT(pep) AS AVEG, 
	SUM(msgs_not_replies) AS TotalTopLevelMsgsByPerson, 
	-- SUM(msgs_not_replies)/COUNT(pep) AS AVEG2, 
	SUM(msgs_are_replies) AS TotalRepliesByPersonToDiffMsgs, 
	-- SUM(msgs_are_replies)/COUNT(pep) AS AVEG3,
	SUM(totalinreplyto) AS TotalRepliesToThisPerson 
	-- SUM(totalinreplyto)/COUNT(pep) AS AVEG4,
	-- SUM(summessages) AS TotalMsgsForPEP -- , dominance_index
from communicationsforseeders
WHERE 
-- author IN (SELECT author FROM top10authors)
 (author  LIKE '%guido%' or author LIKE 'nick co%' or author LIKE '%warsaw%'  or author = 'proposalauthor'  OR author = 'bdfl_delegate'  )  -- IN (SELECT author FROM top10authors)
	AND PEP IN (SELECT pep FROM bdflddpeps) -- for only the 50 peps from bdfl delegate select this line
	GROUP BY author, DMsubphase -- folder -- , peptype -- , state -- , peptype, state
	ORDER BY author ASC, DMsubphase ASC




-- now for the above query we only want to see those peps where the bdfl delegate made the decision
SELECT pep, bdfl_delegatecorrected, author, authorrole, bdfldelegaterole 
FROM pepdetails
WHERE LENGTH (bdfl_delegatecorrected) > 0
-- note can also be after PEP 572 
-- and also those PEPs which were not accepted or rejected
-- INSERT INTO top10authors (author) VALUES ('bdfl_delegate');

-- modified above query only for those peps with a delegate
				-- peptype,
SELECT author,  COUNT(pep) AS pepInvolvement, state, SUM(totalmsgs) AS TotalMsgByPerson, SUM(totalmsgs)/COUNT(pep) AS AVEG, 
	SUM(msgs_not_replies) AS TotalTopLevelMsgsByPerson, SUM(msgs_not_replies)/COUNT(pep) AS AVEG2, 
	SUM(msgs_are_replies) AS TotalRepliesByPersonToDiffMsgs, SUM(msgs_are_replies)/COUNT(pep) AS AVEG3,
	SUM(totalinreplyto) AS TotalRepliesToThisPerson, SUM(totalinreplyto)/COUNT(pep) AS AVEG4,
	SUM(summessages) AS TotalMsgsForPEP -- , dominance_index
from communicationsforseeders
WHERE (author IN (SELECT author FROM top10authors) OR author = 'bdfl_delegate' )
	AND PEP IN (SELECT pep FROM pepdetails WHERE LENGTH (bdfl_delegatecorrected) > 0 ) --   AND created < '2017-07-12'  )
GROUP BY author -- , peptype -- , state
ORDER BY pepInvolvement DESC, TotalMsgsForPEP DESC 

-- just to see which peps were rejected by bdfl delegate
SELECT * 
FROM (SELECT DISTINCT PEP, state, DATE2, peptype						
	FROM pepstates_danieldata_datetimestamp  
	WHERE (STATE LIKE '%acc%' OR state LIKE '%rej%') 
	AND PEP IN (SELECT pep FROM pepdetails WHERE LENGTH (bdfl_delegatecorrected) > 0 ) 
	order by DATE2 ASC)
	AS tbA
LEFT JOIN ( SELECT pep, author, bdfl_delegatecorrected FROM pepdetails ) as tbC
	ON tbA.pep = tbC.pep
ORDER BY tbA.pep;


SELECT * 
FROM pepdetails
WHERE state LIKE '%rej%' 
AND LENGTH (bdfl_delegatecorrected) > 0




-- SELECT * FROM pepstates_danieldata_datetimestamp

-- we have to add the proposalauthor values to the above two query results
SELECT authorsrole, COUNT(distinct pepnum2020)
FROM allmessages
WHERE pepnum2020 <> -1
GROUP BY authorsrole

SELECT pepnum2020, pep, messageid, fromline, senderemail, author, sendername,  senderfullname, clusterbysenderfullname, authorsrole, inreplytouserusingclusteredsenderrole
from allmessages 
-- giodo
-- WHERE (fromline LIKE '%guido%' OR fromline LIKE '%gvr%') -- put 'fromline' instead of author.... 'guido.van.rossum'
-- raymond hettinger
WHERE (fromline LIKE '%raymond%' OR fromline LIKE '%rhettinger%')
-- LIMIT 20
and pepnum2020 = 308
-- not all messages of raymond were assigned as from proposl author.
-- thus we have to assign emauils from the cluster...reflect the cluster

SELECT * FROM distinctsenders WHERE emailaddress LIKE '%hettinger%'
-- rhettinger@users.sourceforge.net, rhettinger

-- get all clusters details for cluster 1

-- now main dominance index clculation for each pepe for all peps
SELECT author,  avg(dominance_index) AS dom, sum(dominance_index)/248 AS sumdom
FROM communicationsforseeders
GROUP BY author, peptype
ORDER BY sumdom ASC
-- author, peptype, 

SELECT DISTINCT PEP, state, DATE2, peptype,
					(SELECT COUNT(messageid) FROM allmessages WHERE pep = m.pep AND (folder LIKE '%dev%' OR folder LIKE '%ideas%')) AS summessages
					FROM pepstates_danieldata_datetimestamp m 
					WHERE (STATE LIKE '%acc%' OR state LIKE '%rej%') order by DATE2 asc;



-- testing
SELECT pep, state, COUNT(state) AS totalstates 
FROM pepstates_danieldata_datetimestamp
-- WHERE pep = 308
-- AND (folder LIKE '%dev%' OR folder LIKE '%ideas%')
GROUP BY state  
ORDER BY totalstates DESC



