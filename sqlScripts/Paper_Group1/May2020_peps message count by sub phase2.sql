SELECT * FROM pepdetails
WHERE pep =541

-- May 2020, we update the PEPDecision column
-- decsion state for a pep is the last state either of which accepted, rejected or final
SELECT * 
FROM pepstates_danieldata_datetimestamp_alltilldec2019
WHERE PEP = 484 
AND (email LIKE '%accepted%' OR email LIKE '%rejected%' OR email LIKE '%final%')
ORDER BY datetimestamp DESC
LIMIT 1

SELECT * FROM distinctauthorpepnumbers

CREATE TABLE pepDMContributionsByRole (PEP INTEGER);
-- pep, peptype, role, author,delegate
ALTER TABLE pepDMContributionsByRole ADD peptype TEXT;
ALTER TABLE pepDMContributionsByRole ADD rolecheckingfor TEXT;
ALTER TABLE pepDMContributionsByRole ADD author TEXT;
ALTER TABLE pepDMContributionsByRole ADD delegate TEXT;

SELECT * FROM pepeditors;

SELECT * FROM pepdmcontributionsbyrole
ORDER BY pep

TRUNCATE TABLE pepdmcontributionsbyrole

SELECT * FROM pepdetails WHERE pep = 160

SELECT coredeveloper, dateadded 
FROM coredevelopers 
WHERE coredeveloper LIKE 'mangoba%'
order by coredeveloper asc

SELECT COUNT(DISTINCT pep) FROM pepdmcontributionsbyrole

-- just grouping..not checking, checking will be doen later

-- just count for authors first
SELECT rolecheckingfor, peptype, author,  COUNT(DISTINCT pep) AS cnt, GROUP_CONCAT(DISTINCT pep ORDER BY pep ASC SEPARATOR ', ') AS peps
FROM pepdmcontributionsbyrole
WHERE author IN ('allmembers','coredevs','pepeditors','bdfl')
GROUP by rolecheckingfor, peptype, author
ORDER BY rolecheckingfor 
-- LIMIT 9 -- dont want to see unwanted data

SELECT rolecheckingfor, peptype, COUNT(DISTINCT pep) AS cnt, delegate, GROUP_CONCAT(DISTINCT pep ORDER BY pep ASC SEPARATOR ', ') AS peps
FROM pepdmcontributionsbyrole
WHERE delegate IN ('allmembers','coredevs','pepeditors','bdfl')
AND LENGTH(delegate) > 0
GROUP by rolecheckingfor, peptype, delegate
ORDER BY rolecheckingfor
-- LIMIT 9

-- checking repetituion for pep author
SELECT DISTINCT pep, author, peptype 
-- , GROUP_CONCAT(DISTINCT pep ORDER BY pep ASC SEPARATOR ', ') AS peps
FROM pepdmcontributionsbyrole
WHERE author IN ('allmembers','coredevs','pepeditors','bdfl')
ORDER BY pep ASC

-- checking repetituion for delegate
SELECT DISTINCT pep, delegate, peptype 
-- , GROUP_CONCAT(DISTINCT pep ORDER BY pep ASC SEPARATOR ', ') AS peps
FROM pepdmcontributionsbyrole
WHERE delegate IN ('allmembers','coredevs','pepeditors','bdfl')
ORDER BY pep asc