-- march 07 2021
-- grouped by pep type
-- comment / uncomment the following lines in three places if 	we want only those decided by the bdfl
-- AND pep NOT IN (SELECT pep FROM bdflddpeps) -- AND PEPnum2020 NOT IN (SELECT pep FROM bdflddpeps) 
SELECT distinct folder FROM communicationsforseeders
UPDATE communicationsforseeders SET folder = REPLACE (folder,'C:\\datasets\\','');
UPDATE communicationsforseeders SET folder = REPLACE (folder,'C:\\datasets-additional\\','');



SELECT * 					-- clusterbysenderfullname,
	FROM (-- social discussion metrics
		SELECT author AS m, peptype, folder,
					COUNT(distinct pep) AS pepInvolvement, SUM(totalmsgs) AS TotalMsgByPerson, 	-- SUM(totalmsgs)/COUNT(pep) AS AVEG, 
					SUM(msgs_not_replies) AS TotalTopLevelMsgsByPerson, 	-- SUM(msgs_not_replies)/COUNT(pep) AS AVEG2, 
					SUM(msgs_are_replies) AS TotalRepliesByPersonToDiffMsgs, 	-- SUM(msgs_are_replies)/COUNT(pep) AS AVEG3,
					SUM(totalinreplyto) AS TotalRepliesToThisPerson 	-- SUM(totalinreplyto)/COUNT(pep) AS AVEG4,
					-- SUM(summessages) AS TotalMsgsForPEP -- , dominance_index
					from communicationsforseeders
				-- WHERE author IN (SELECT author FROM top10authors)
					WHERE (author IN (SELECT author FROM top10authors) OR author = 'proposalauthor'  OR author = 'bdfl_delegate' )
					-- add for 51 peps by bdfl delegate
					AND pep NOT IN (SELECT pep FROM bdflddpeps) -- AND PEPnum2020 NOT IN (SELECT pep FROM bdflddpeps) 
					GROUP BY author, peptype, folder -- , peptype -- , state -- , peptype, state
					ORDER BY pepInvolvement DESC, TotalMsgByPerson DESC
		) AS tbA
	-- We join with how many times this particular person has been replied to
							-- inReplyToUserUsingClusteredSender
	LEFT JOIN (	
		SELECT clusterbysenderfullname AS m, COUNT(DISTINCT threadid) AS threadCNT, peptype2020 , folder 
			FROM pepthreadsbyperson2020 
			-- add for 51 peps by bdfl delegate
			WHERE pepnum2020 NOT IN (SELECT pep FROM bdflddpeps) -- AND PEPnum2020 NOT IN (SELECT pep FROM bdflddpeps) 
			-- where threadlevel < 3  ...for firsta nd second level results
			GROUP BY clusterbysenderfullname, peptype2020
			-- ORDER BY threadCNT DESC
		UNION
			SELECT authorsrole AS m, COUNT(DISTINCT threadid) AS threadCNT, peptype2020 , folder
			FROM pepthreadsbyperson2020_PABD 
			-- where threadlevel < 3  ...for firsta nd second level results
			WHERE authorsrole = 'proposalAuthor' OR authorsrole = 'bdfl_delegate'
			-- add for 51 peps by bdfl delegate
			AND pepnum2020 NOT IN (SELECT pep FROM bdflddpeps) -- AND PEPnum2020 NOT IN (SELECT pep FROM bdflddpeps) 
			GROUP BY authorsrole , peptype2020, folder
		ORDER BY threadCNT DESC
		) as tbC
	-- ON tbA.clusterbysenderfullname = tbC.inReplyToUserUsingClusteredSender;
	ON tbA.m = tbC.m 
   AND tbA.peptype = tbC.peptype2020
	ORDER BY tbA.pepInvolvement desc;
