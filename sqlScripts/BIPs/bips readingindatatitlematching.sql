SELECT * FROM bipstates_danieldata_datetimestamp

SELECT bip, COUNT(*) 
FROM allmessages
-- WHERE folder NOT LIKE '%bitcoin-dev%'
GROUP BY bip

SELECT * FROM allmessages WHERE bip = 144;

SELECT * FROM bipstates_danieldata_datetimestamp
SELECT * FROM bipdetails

ALTER TABLE allmessages RENAME COLUMN bipnum2020 TO  bipnum2021;
--backup the original bip numer
UPDATE allmessages SET originalbipnumber = bip;

-- now we set the bip number to that of those which are replies
UPDATE allmessages a, allmessages b SET b.bip = a.bip
WHERE b.emailmessageid = a.inreplyto AND b.bip = -1;
-- so 319 records assigned
/* Affected rows: 319  Found rows: 0  Warnings: 0  Duration for 1 query: 0.640 sec. */	 

TRUNCATE allmessages;