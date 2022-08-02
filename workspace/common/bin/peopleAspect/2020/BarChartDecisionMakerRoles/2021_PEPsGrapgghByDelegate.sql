
-- BDFL as decider
SELECT TYPE AS ptype, count(pep) AS c FROM pepdetails
WHERE created < '2018-07-11'
AND length(bdfl_delegate)=0
GROUP BY ptype
"ptype"	"c"
"Informational"	"52"
"Process"	"36"
"Standards Track"	"327"

-- BDFL as Proposal author
SELECT TYPE AS ptype, count(pep) AS c FROM pepdetails
WHERE created < '2018-07-11'
AND author LIKE '%guido%'
GROUP BY ptype
"ptype"	"c"
"Informational"	"7"
"Process"	"4"
"Standards Track"	"30"

SELECT TYPE AS ptype, count(pep) AS c FROM pepdetails
WHERE created < '2018-07-12'
AND length(bdfl_delegate) > 0
GROUP BY ptype

SELECT pep, created FROM pepdetails
ORDER BY pep ASC

SELECT * FROM pepdetails
ORDER BY pep ASC

-- UPDATE pepdetails SET created = '2018-02-05' WHERE pep = 571
