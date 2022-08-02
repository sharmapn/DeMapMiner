SELECT pep, authorCorrected,bdfl_delegateCorrected,type,created FROM pepdetailsold
WHERE author LIKE '%Aprano%'

SELECT * FROM pepdetailsold
WHERE author LIKE '%yama%'

SELECT  * 
FROM alldevelopers 
WHERE clusterBySenderFullName LIKE '%yamamoto%'
order by clusterBySenderFullName 


SELECT  * FROM alldevelopers 
order by clusterBySenderFullName 


INSERT INTO alldevelopers (clusterBySenderFullName) VALUES ('Talin');
-- IN alldevelopers i CHANGED greg ewing TO gregory ewing, 
-- same for Andrew Kuchling to A M Kuchling
-- changed Hirokazu Yamamoto to  'Masayuki Yamamoto'
-- changed ... to Martin v Lewis
-- changed ... to Steven D Aprano

-- these were added to develiopers 
INSERT INTO alldevelopers (clusterBySenderFullName) VALUES ('oleg broytman');
INSERT INTO alldevelopers (clusterBySenderFullName) VALUES ('greg wilson');
INSERT INTO alldevelopers (clusterBySenderFullName) VALUES ('frederic giacometti');
INSERT INTO alldevelopers (clusterBySenderFullName) VALUES ('jim althoff');
INSERT INTO alldevelopers (clusterBySenderFullName) VALUES ('christian r reis');
INSERT INTO alldevelopers (clusterBySenderFullName) VALUES ('andrew mcclelland');
INSERT INTO alldevelopers (clusterBySenderFullName) VALUES ('björn lindqvist');
INSERT INTO alldevelopers (clusterBySenderFullName) VALUES ('jervis whitley');
INSERT INTO alldevelopers (clusterBySenderFullName) VALUES ('joshua landau');
INSERT INTO alldevelopers (clusterBySenderFullName) VALUES ('sebastian kreft');
INSERT INTO alldevelopers (clusterBySenderFullName) VALUES ('christopher barker');
INSERT INTO alldevelopers (clusterBySenderFullName) VALUES ('james polley');
INSERT INTO alldevelopers (clusterBySenderFullName) VALUES ('ed schofield');
INSERT INTO alldevelopers (clusterBySenderFullName) VALUES ('mike g miller');
INSERT INTO alldevelopers (clusterBySenderFullName) VALUES ('philipp angerer');
INSERT INTO alldevelopers (clusterBySenderFullName) VALUES ('markus meskanen');
INSERT INTO alldevelopers (clusterBySenderFullName) VALUES ('matt chisholm');

-- this entry was nade to core developers
Travis E. Oliphant
PEP Author Unmatched 357 travis oliphant
 
-- some dates in the new dataset 'pepdetails' is missing
SELECT * from pepdetailsold WHERE pep = 571 
 
 
SELECT * FROM pepdetails WHERE pep = 458
 
SELECT pep fROM pepdetails
LEFT JOIN
    pepdetailsold USING (pep)
WHERE
    pepdetailsold.pep IS NULL;
    
-- who are in coredev but not in dev - they should be

CREATE VIEW cdev AS 
SELECT coredeveloper as clusterBySenderFullName fROM coredevelopers;

SELECT clusterBySenderFullName fROM cdev
LEFT JOIN
    alldevelopers USING (clusterBySenderFullName)
WHERE
    alldevelopers.clusterBySenderFullName IS NULL;    