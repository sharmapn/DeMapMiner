

-- 10 march 2021
SELECT * FROM  2021_SNAofroles WHERE clusterBySenderFullName like '%guido%'
		SELECT * FROM allmessages WHERE pep = 205 AND messageid = 57708
		
		SELECT * FROM allmessages WHERE inreplyto LIKE '%Your%' LIMIT 20
		
--		In-Reply-To: Your message of "Sat, 16 Dec 2000 13:32:30 +0100."
--             <200012161232.NAA01779@loewis.home.cs.tu-berlin.de> 
		-- email field, start at : In-Reply-To:
		--              end '>'
		-- UPDATE allmessages set fromLine = substr(SUBSTRING_INDEX(email,'In-Reply-To:',1), POSITION("From:" IN email)) 
		--	WHERE pep = 205 AND messageid = 57708 
		SELECT SUBSTR(SUBSTRING_INDEX(email,'In-Reply-To:',1), POSITION("From:" IN email)) 
		FROM allmessages
			WHERE pep = 205 AND messageid = 57708 	
			
		SELECT POSITION("In-Reply-To:" IN email) AS ref, 
			LOCATE('<',email, POSITION("In-Reply-To:" IN email)) AS st, -- locate '<' in email starting from next argument
			LOCATE('>',email, POSITION("In-Reply-To:" IN email)) AS en,	-- locate '>' in email starting from next argument
			SUBSTR(email, LOCATE('<',email, POSITION("In-Reply-To:" IN email)) + 1 , 
				LOCATE('>',email, POSITION("In-Reply-To:" IN email)) - LOCATE('<',email, POSITION("In-Reply-To:" IN email))  -1) AS output
			-- SUBSTRING_INDEX(email, "In-Reply-To:" , 1)
		FROM allmessages
			WHERE pep = 205 AND messageid = 57708 	
		
		DROP TABLE ttestw;
		CREATE TABLE ttestw as
		SELECT pep, messageid, email, inreplyto, inReplyToUserUsingClusteredSenderRole, authorsrole, clusterBySenderFullName, inreplytouser 
		FROM allmessages	WHERE inreplyto LIKE '% %' AND pep <> -1 
		-- only 3692 rows... will check later
		
		-- now we try on all messages who have this error
		CREATE TABLE crt as	
		-- 137  rows left...
		SELECT email, -- POSITION("In-Reply-To:" IN email) AS ref, 
			LOCATE('<',email, POSITION("In-Reply-To:" IN email)) AS st, -- locate '<' in email starting from next argument
			LOCATE('>',email, POSITION("In-Reply-To:" IN email)) AS en,	-- locate '>' in email starting from next argument
			SUBSTR(email, LOCATE('<',email, POSITION("In-Reply-To:" IN email)) + 1 , 
				LOCATE('>',email, POSITION("In-Reply-To:" IN email)) - LOCATE('<',email, POSITION("In-Reply-To:" IN email))  -1) AS output
			-- SUBSTRING_INDEX(email, "In-Reply-To:" , 1)
		FROM allmessages
			WHERE inreplyto LIKE '%Message from%' AND pep <> -1 -- pep = 205 AND messageid = 57708 
			-- then use "Message from"	
		
		SELECT * FROM crt	
		-- now we update 
		-- now we try on all messages who have this error
		SET AUTOCOMMIT=false;	
		UPDATE allmessages SET inreplyto = 
			SUBSTR(email, LOCATE('<',email, POSITION("In-Reply-To:" IN email)) + 1 , 
				LOCATE('>',email, POSITION("In-Reply-To:" IN email)) - LOCATE('<',email, POSITION("In-Reply-To:" IN email))  -1) 
			-- SUBSTRING_INDEX(email, "In-Reply-To:" , 1)
			WHERE inreplyto LIKE '%Your%' AND pep <> -1 -- pep = 205 AND messageid = 57708 				

		SELECT pep, messageid, email, inreplyto  FROM allmessages WHERE inreplyto LIKE '% %' AND pep <> -1
		
		SELECT COUNT(messageid) FROM allmessages WHERE (clusterBySenderFullName = inreplytouser or
		inReplyToUserUsingClusteredSenderRole = authorsrole)
		AND pep <> -1 -- 136,329
		-- 141,757
		
		DROP TABLE rty;
		CREATE TABLE rty as
		select pep, messageid, email, inreplyto, inReplyToUserUsingClusteredSenderRole, authorsrole, clusterBySenderFullName, inreplytouser 
		FROM allmessages WHERE (clusterBySenderFullName = inreplytouser or
		inReplyToUserUsingClusteredSenderRole = authorsrole )
		AND pep <> -1 AND inreplyto IS NOT NULL;
		-- LIMIT 1000
		SELECT * FROM rty where authorsrole LIKE '%author%' AND pep <>  -1 LIMIT 5;
		
		SELECT pep, messageid, clusterBySenderFullName FROM allmessages WHERE emailmessageid  = 'CADiSq7eGLpSsFfuVDoWCeHUGzr8+VVnP5Q99QLJLfhPyCzfvSw@mail.gmail.com'