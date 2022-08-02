
SELECT * FROM sentimentsentences LIMIT 5;
SELECT pep, authorsrole, state, COUNT(sentiment) AS cnt, 
SUM(if(sentiment = 'positive', 1, 0)) AS positive, SUM(if(sentiment = 'negative', 1, 0)) AS negative, SUM(if(sentiment = 'neutral', 1, 0)) AS neutral
FROM sentimentsentences
GROUP BY pep, authorsrole, state
ORDER BY state ASC, pep ASC, cnt DESC

-- main check that makes the difference. we manually check the last sentenvces in all messages of the core developers
-- to see if they need to be corrected/cleaned manually 
								-- authorsrole,
SELECT id, state, pep, author, messageid, datetimestamp,  sentence, datecommitted,  sentiment
FROM sentimentsentences
WHERE id IN (								
	SELECT max(id)  -- the largest id of records with the same messageid
	FROM sentimentsentences
	WHERE authorsrole not like '%other%' AND tocheck = 1
	GROUP BY state, pep,  messageid
	) 
ORDER BY state, pep asc, author, datetimestamp ASC, messageid


-- added author in Group by clause so that we can get the max id of messages from each member --- thus the last message of each member
-- DROP VIEW onepermessage;
CREATE table onepermessage AS	
	SELECT id, state, pep,  datetimestamp, author, messageid, sentence, datecommitted,  sentiment
	FROM sentimentsentences
	WHERE id IN (								
		SELECT max(id)  -- the largest id of records with the same messageid
		FROM sentimentsentences
		WHERE authorsrole not like '%other%' AND tocheck = 1
		GROUP BY state, pep, messageid
		) 
	ORDER BY state, pep asc, datetimestamp ASC, author,  messageid;

-- now second phase is to query the view
SELECT id, state, pep,  datetimestamp, author, messageid, sentence, datecommitted,  sentiment
	FROM sentimentsentences
	WHERE id IN (								
		SELECT max(id)  -- the largest id of records with the same messageid
		FROM onepermessage
--		WHERE authorsrole not like '%other%' AND tocheck = 1
		GROUP BY state, pep, author
		)
ORDER BY state asc, pep asc, datetimestamp ASC, author,  messageid;


	
SELECT email FROM allmessages WHERE messageid = 55090;



-- data cleaning. once we have the initial results, we found that we have to clean that data
-- so we add a column named tocheck, so that we can ignore some records

ALTER TABLE sentimentsentences ADD tocheck INT ;
UPDATE sentimentsentences set tocheck = 1; 

ALTER TABLE sentimentsentences ADD DATE2 DATE;
-- this has now been implemented as part of the procedure                       
UPDATE sentimentsentences 
	INNER JOIN allmessages
	    ON sentimentsentences.messageid = allmessages.messageid	
			SET sentimentsentences.date2 = allmessages.date2;
			
ALTER TABLE sentimentsentences ADD datetimestamp TIMESTAMP;
-- this has now been implemented as part of the procedure                       
UPDATE sentimentsentences 
	INNER JOIN allmessages
	    ON sentimentsentences.messageid = allmessages.messageid	
			SET sentimentsentences.datetimestamp = allmessages.datetimestamp;


			
-- delete duplicate rows, which have the same datetimestamp and author and importantly sentence			

-- first need autoincrement clumn
-- ALTER TABLE `sentimentsentences` ADD `id` INT NOT NULL AUTO_INCREMENT;
-- was already there

SELECT calc_id FROM (
    SELECT MAX(id) AS calc_id
    FROM my_table
    GROUP BY identField1, identField2
    HAVING COUNT(id) > 1
  ) m

SELECT pep, state, sentence, author, datetimestamp, messageid FROM sentimentsentences
WHERE id IN (
	SELECT calc_id FROM (
    SELECT -- pep, state, sentence, author, datetimestamp, messageid, 
	 	MAX(id) AS calc_id
	 -- id, pep, state, datetimestamp, author, sentence -- MAX(pep) AS calc_id
    FROM sentimentsentences
    WHERE PEP = 201
    GROUP BY pep, sentence -- ,  datetimestamp
    HAVING COUNT(id) > 1
    ) m
   ORDER BY state, pep ASC, messageid
)

 Select count(*) 
 FROM sentimentsentences
        GROUP BY pep, sentence 
          HAVING COUNT(*) > 1
 ORDER BY pep, state

UPDATE sentimentsentences set tocheck = 0 WHERE sentence LIKE '%Nick Coghlan ncoghlan at gmail com Brisbane, Australia%'; -- 565 roes affected
-- just checking if the above update worked
SELECT pep, state, author, authorsrole, sentence, datecommitted, messageid, sentiment,  tocheck
FROM sentimentsentences WHERE tocheck = 0

UPDATE sentimentsentences set tocheck = 0 WHERE sentence LIKE '%Guido van Rossum home page%';
