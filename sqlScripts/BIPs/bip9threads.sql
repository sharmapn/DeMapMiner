
-- April 17 2021. This is the thread for all BIPs. We added BIP 91 and BIP 148 as well as per Rewat REquest

-- FOR ALL threads in bip 9 use 'allmessages' table
-- FOR all threads in BIP 9 but ignoring proposal numbers in quoted text use the 'allmessages_iq' table

-- see bip 9 subjects which are assigned -1 --- and needs to be assigned
-- see bip 9 subjects which are assigned -1 --- and needs to be assigned
SELECT subject, TotalMessagesForThisBIP, TotalMessagesAllBIPs, TotalPeopleThisBIP, TotalPeopleAllBIPs, startdate, enddate, daysdiscussed, unasssigned,  bips, firstauthor, senders FROM (
	SELECT * FROM (
		SELECT subject, COUNT(DISTINCT m.emailmessageid) as TotalMessagesForThisBIP, MIN(m.datetimestamp) as startdate, MAX(m.datetimestamp) AS enddate, 
		DATEDIFF(MAX(m.datetimestamp), MIN(m.datetimestamp) ) as daysdiscussed
	--	GROUP_CONCAT(distinct m.bip) bips
		-- d.*, -- m.subject, COUNT(m.messageid) as cnt, MIN(m.datetimestamp) as st, MAX(m.datetimestamp) AS en, DATEDIFF(MAX(m.datetimestamp), MIN(m.datetimestamp) ) as dif, 
		--        GROUP_CONCAT(distinct m.bip) bips
		-- DISTINCT subject
		-- , d.subject, COUNT(*)
		FROM allmessages m -- a 
	--	JOIN tempdata d ON (m.subject = d.subject)
		WHERE bip = 148 -- 91 -- 342 -- 9. 141, 341, 342 -- change as per requirement
		GROUP BY m.subject
		ORDER BY daysdiscussed desc
		) AS tbA
		-- We join with how many times this particular person has been replied to
								-- inReplyToUserUsingClusteredSender
							--	DESC pepthreadsbyperson2020;
		LEFT JOIN (	
			SELECT subject as sub2, COUNT(DISTINCT emailmessageid)  AS unasssigned FROM allmessages 
			WHERE bip = -1
			GROUP BY subject
		) AS tbC
		ON (tbA.subject = tbC.sub2)
		
		-- the number of people involved for this BIP for this subject
		LEFT JOIN (	
			SELECT subject as sub6, COUNT(DISTINCT sendername) as TotalPeopleThisBIP FROM allmessages
			WHERE BIP = 148 -- 91 -- 342 -- 9
			-- we just dont check from te same bip, but from all bips that have this subject			 
			GROUP BY subject
		) AS tbG
		ON (tbA.subject = tbG.sub6)
		
		-- the number of people involved in all BIPs for this subject
		LEFT JOIN (	
			SELECT subject as sub4, COUNT(DISTINCT sendername) as TotalPeopleAllBIPs FROM allmessages
			-- we just dont check from te same bip, but from all bips that have this subject			 
			GROUP BY subject
		) AS tbE
		ON (tbA.subject = tbE.sub4)
		
		-- the number of messages with this subject in all BIPs 
		LEFT JOIN (	                  -- maybe just here we dont need DISTINCT
			SELECT subject as sub5, COUNT(emailmessageid) as TotalMessagesAllBIPs FROM allmessages
			-- we just dont check from te same bip, but from all bips that have this subject			 
			GROUP BY subject
		) AS tbF
		ON (tbA.subject = tbF.sub5)
		
		-- Bips with messages with the same subject 
		LEFT JOIN (
	 		SELECT subject AS sub3, GROUP_CONCAT(distinct bip) bips FROM allmessages
	 		GROUP BY subject
	 	) AS tbD	
		ON (tbA.subject = tbD.sub3)
		
		
		-- Author of first mesage in this thread
		LEFT JOIN (
	 		SELECT subject AS sub8, sendername as firstauthor FROM allmessages
	 		GROUP BY subject 
	 		ORDER BY datetimestamp asc
	 	) AS tbI	
		ON (tbA.subject = tbI.sub8)
		
		-- lets see the different people writing these mesages 
		LEFT JOIN (
	 		SELECT subject AS sub7, GROUP_CONCAT(distinct sendername) senders FROM allmessages
	 		WHERE sendername IN (SELECT sendername FROM top10members) -- LIKE '%gregory%' OR sendername LIKE '%james%'
	 		GROUP BY subject
	 	) AS tbH	
		ON (tbA.subject = tbH.sub7)
		
		ORDER BY TotalMessagesForThisBIP DESC -- startdate ASC -- TotalMessages DESC 
) AS t


SELECT * FROM allmessages WHERE subject LIKE '%[bitcoin-dev] BIP 9 style version bits for txns%'
-- '%[bitcoin-dev] BIP draft: Extended block header hardfork%'
-- '%[bitcoin-dev] User Activated Soft Fork Split Protection%'
ORDER BY dateTIMESTAMP ASC

SELECT * FROM bipstates_danieldata_datetimestamp WHERE bip = 9

-- check for those not in quotes
SELECT subject, TotalMessagesForThisBIP, TotalMessagesAllBIPs, TotalPeopleThisBIP, TotalPeopleAllBIPs, startdate, enddate, daysdiscussed, unasssigned,  bips, firstauthor, senders FROM (
	SELECT * FROM (
		SELECT subject, COUNT(DISTINCT m.emailmessageid) as TotalMessagesForThisBIP, MIN(m.datetimestamp) as startdate, MAX(m.datetimestamp) AS enddate, 
		DATEDIFF(MAX(m.datetimestamp), MIN(m.datetimestamp) ) as daysdiscussed
	--	GROUP_CONCAT(distinct m.bip) bips
		-- d.*, -- m.subject, COUNT(m.messageid) as cnt, MIN(m.datetimestamp) as st, MAX(m.datetimestamp) AS en, DATEDIFF(MAX(m.datetimestamp), MIN(m.datetimestamp) ) as dif, 
		--        GROUP_CONCAT(distinct m.bip) bips
		-- DISTINCT subject
		-- , d.subject, COUNT(*)
		FROM allmessages_iq m -- a 
	--	JOIN tempdata d ON (m.subject = d.subject)
		WHERE bip = 9 -- 9. 141, 341, 342 -- change as per requirement
		GROUP BY m.subject
		ORDER BY daysdiscussed desc
		) AS tbA
		-- We join with how many times this particular person has been replied to
								-- inReplyToUserUsingClusteredSender
							--	DESC pepthreadsbyperson2020;
		LEFT JOIN (	
			SELECT subject as sub2, COUNT(DISTINCT emailmessageid)  AS unasssigned FROM allmessages_iq 
			WHERE bip = -1
			GROUP BY subject
		) AS tbC
		ON (tbA.subject = tbC.sub2)
		
		-- the number of people involved for this BIP for this subject
		LEFT JOIN (	
			SELECT subject as sub6, COUNT(DISTINCT sendername) as TotalPeopleThisBIP FROM allmessages_iq
			WHERE BIP = 9
			-- we just dont check from te same bip, but from all bips that have this subject			 
			GROUP BY subject
		) AS tbG
		ON (tbA.subject = tbG.sub6)
		
		-- the number of people involved in all BIPs for this subject
		LEFT JOIN (	
			SELECT subject as sub4, COUNT(DISTINCT sendername) as TotalPeopleAllBIPs FROM allmessages_iq
			-- we just dont check from te same bip, but from all bips that have this subject			 
			GROUP BY subject
		) AS tbE
		ON (tbA.subject = tbE.sub4)
		
		-- the number of messages with this subject in all BIPs 
		LEFT JOIN (	                  -- maybe just here we dont need DISTINCT
			SELECT subject as sub5, COUNT(emailmessageid) as TotalMessagesAllBIPs FROM allmessages_iq
			-- we just dont check from te same bip, but from all bips that have this subject			 
			GROUP BY subject
		) AS tbF
		ON (tbA.subject = tbF.sub5)
		
		-- Bips with messages with the same subject 
		LEFT JOIN (
	 		SELECT subject AS sub3, GROUP_CONCAT(distinct bip) bips FROM allmessages_iq
	 		GROUP BY subject
	 	) AS tbD	
		ON (tbA.subject = tbD.sub3)
		
		
		-- Author of first mesage in this thread
		LEFT JOIN (
	 		SELECT subject AS sub8, sendername as firstauthor FROM allmessages_iq
	 		GROUP BY subject 
	 		ORDER BY datetimestamp asc
	 	) AS tbI	
		ON (tbA.subject = tbI.sub8)
		
		-- lets see the different people writing these mesages 
		LEFT JOIN (
	 		SELECT subject AS sub7, GROUP_CONCAT(distinct sendername) senders FROM allmessages_iq
	 		WHERE sendername IN (SELECT sendername FROM top10members) -- LIKE '%gregory%' OR sendername LIKE '%james%'
	 		GROUP BY subject
	 	) AS tbH	
		ON (tbA.subject = tbH.sub7)
		
		ORDER BY TotalMessagesForThisBIP DESC -- startdate ASC -- TotalMessages DESC 
) AS t