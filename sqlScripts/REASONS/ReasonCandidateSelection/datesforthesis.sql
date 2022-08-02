SELECT MAX(DATE2) FROM allmessages WHERE pep = 572
LIMIT 1

-- DISTINT PEP FINAL CONSIDERATION
SELECT DISTINCT PEP from  pepstates_danieldata_datetimestamp

SELECT * FROM allmessages WHERE pep = 572

SELECT MAX(DATE2) from pepstates_danieldata_datetimestamp

SELECT COUNT(messageid) 
FROM allmessages
WHERE DATE2 <= '2018-07-11'

-- then try UNIQUE
SELECT COUNT(DISTINCT messageid) 
FROM allmessages
WHERE DATE2 <= '2018-07-11'

-- then try WITHOUT -1 IN pep NUMBER
SELECT COUNT(DISTINCT messageid) 
FROM allmessages
WHERE DATE2 <= '2018-07-11'
and PEP <> -1


-- see the ones we captured without pep title
-- then try WITHOUT -1 IN pep NUMBER 
SELECT COUNT(DISTINCT messageid) 
FROM allmessages
WHERE DATE2 <= '2018-07-11'
and PEP <> -1
AND originalPEPNumberNum <> PEP

-- then we add the additional messages for pep 572
-- AND we have TO say ALL this