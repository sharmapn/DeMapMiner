-- sentiment analysis

-- the way we are doing this, we have to redo for the three peps whch have both accepted and rejected states
-- OR we can do the accepted and rejected poeps seprately
-- set @user = 123456;
-- set @group = (select GROUP from USER where User = @user);
-- select * from USER where GROUP = @group;

-- SET @startdate = (SELECT DATE2 FROM pepstates_danieldata_datetimestamp WHERE PEP = PEP AND STATE LIKE 'acc' OR state LIKE '%rej%' order by DATE2 asc LIMIT 1);
-- SET @enddate = (SELECT DATE2 FROM pepstates_danieldata_datetimestamp WHERE PEP = m.PEP AND STATE LIKE 'acc' OR state LIKE '%rej%' order by DATE2 asc LIMIT 1) -7;

-- DROP TABLE sentimentanalysis;
-- CREATE TABLE sentimentanalysis AS
-- ALTER TABLE sentimentanalysis ADD column cleaned_msg TEXT 
-- ALTER TABLE sentimentanalysis ADD column sentiment TEXT

DELIMITER $$
CREATE PROCEDURE createSentenceList () -- (INOUT emailList varchar(4000))
BEGIN
	DECLARE finished INTEGER DEFAULT 0;
-- "," , "," , 
	DEClARE curgetMessageIds 
			CURSOR FOR 
				select PEP, (SELECT state FROM pepstates_danieldata_datetimestamp WHERE PEP = m.PEP AND STATE LIKE 'acc' OR state LIKE '%rej%' order by DATE2 asc LIMIT 1)
				author, inReplyToUserUsingClusteredSender, 
				-- (select authorsrole FROM commonroles WHERE author = m.author ),  
				authorsrole, (select PEPCount FROM commonroles WHERE author = m.author ) AS pepinvolvementcount, -- add number of peps the member has contributed in
				analysewords
				from allmessages m
				WHERE PEP = 308 -- IN (SELECT PEP FROM accrejpeps)
				--	AND state like '%acc%' or '%rej%'
				-- WHICH DATES WE WANT THE DATA
				-- 1. all dates
				-- AND DATE2 < (SELECT DATE2 FROM accrejpeps WHERE PEP = m.PEP)
				-- 2. before and including day state is committed
				AND DATE2 <= (SELECT DATE2 FROM pepstates_danieldata_datetimestamp WHERE PEP = m.PEP AND (STATE LIKE 'acc' OR state LIKE '%rej%') order by DATE2 asc LIMIT 1)
				-- 3. only discussion one week before the day state is committed
				--	AND DATE2 BETWEEN 
				--		(SELECT DATE2 FROM pepstates_danieldata_datetimestamp WHERE PEP = m.PEP AND STATE LIKE 'acc' OR state LIKE '%rej%' order by DATE2 asc LIMIT 1) -7
				--		AND (SELECT DATE2 FROM pepstates_danieldata_datetimestamp WHERE PEP = m.PEP AND STATE LIKE 'acc' OR state LIKE '%rej%' order by DATE2 asc LIMIT 1)	
				AND folder LIKE '%dev%'
				-- we want to see direct connections at the moment
				AND LENGTH(sendername) > 0 
				AND LENGTH(inReplyToUser) > 0 
				-- AND (sendername in (select distinct(sendername) from alldevelopers) -- atleast one member is a developer
				-- OR inReplyToUser in (select distinct(sendername) from alldevelopers))
				order by sendername;
				-- declare NOT FOUND handler
				DECLARE CONTINUE HANDLER 
        		FOR NOT FOUND SET finished = 1;
	-- INTO OUTFILE 'c:\\scripts\\SNA2020\\SNAAllDevelopersAfterClustering_19-12-2020_h.txt';

OPEN curgetMessageIds;

getMessageIds: 
		LOOP
			FETCH curgetMessageIds INTO messageIds;
			IF finished = 1 THEN 
				LEAVE getMessageIds;
			END IF;
			-- build email list
			-- SET emailList = CONCAT(emailAddress,";",emailList);
			SELECT proposal, msgAuthorRole, messageid, sentence,isEnglishOrCode,islastparagraph, isfirstparagraph, msgSubject 
			from allsentences where proposal = pep and isEnglishOrCode = 1 
			and (islastparagraph =1 or isfirstparagraph =1) 
			order by proposal ASC
	END LOOP getMessageIds;
	CLOSE curgetMessageIds;

END$$
DELIMITER ;

-- You can test the createEmailList stored procedure using the following script:
-- SET @emailList = ""; 
-- CALL createEmailList(@emailList); 
-- SELECT @emailList;


-- DESC allmessages
-- DESC pepstates_danieldata_datetimestamp

-- DESC allsentences
-- SELECT COUNT(*) FROM allsentences
-- SELECT * FROM allsentences LIMIT 5
-- SELECT DISTINCT(lastdir) FROM allsentences
-- "C:\datasets\python-ideas"
-- "C:\datasets\python-dev"
-- "C:\datasets\python-announce-list"
-- "C:\datasets\python-checkins"
-- "C:\datasets\python-lists"
-- "C:\datasets\python-patches"
-- "C:\datasets\python-bugs-list"
-- "C:\datasets-additional\python-3000"
-- "C:\datasets\python-distutils-sig"
-- "C:\datasets\python-committers"
-- 
-- -- this table is not extrated properly in terms of data
-- -- CHECK THIS IS A GOOD MESSAGE for veryfying allsentences are extracted properly
-- -- https://mail.python.org/pipermail/python-ideas/2009-April/003905.html
-- 
-- SELECT proposal, msgAuthorRole, messageid, sentence,isEnglishOrCode,islastparagraph, isfirstparagraph, msgSubject
-- from allsentences where proposal =308 and isEnglishOrCode = 1
-- and (islastparagraph =1 or isfirstparagraph =1)
-- order by proposal asc
