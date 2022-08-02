SELECT * FROM allmessages
WHERE email LIKE '%Assuming no material objection appear to the new syntax and semantics, I can approve the PEP later this week%' 

SELECT bip, email, DATE2, datetimestamp, folder, sendername, authorsrole, subject 
FROM allmessages 
-- LIMIT 100
INTO OUTFILE 'c:\\scripts\\Bips\\alldata.txt';

DROP  TABLE allmessages_4bips;
CREATE TABLE allmessages_4bips
AS 
SELECT bip, messageid, email, datetimestamp, DATE2, sendername, authorsrole, REPLACE(    REPLACE(SUBSTRING_INDEX(email, "From: ", 1),'\r',''), '\n', '') AS froml
FROM allmessages
WHERE bip = 9 or bip = 141 or bip = 341 or bip = 342;

SELECT pep, messageid, datetimestamp, email, sendername FROM allmessages where pep = 308 
and subject LIKE '%pep 308 vote%' LIMIT 50