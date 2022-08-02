-- all data for paper
SELECT folder, COUNT(DISTINCT messageid) 
FROM allmessages
where DATE2 < '2018-07-12' -- BDFL steps down
-- AND pep <> -1 
GROUP BY folder

"C:\datasets-additional\python-3000"	"15949"
"C:\datasets\core-workflow"	"1"
"C:\datasets\Datetime-SIG"	"1"
"C:\datasets\import-sig"	"1"
"C:\datasets\python-announce-list"	"11948"
"C:\datasets\python-bugs-list"	"370770"
"C:\datasets\python-checkins"	"156152"
"C:\datasets\python-committers"	"5613"
"C:\datasets\python-dev"	"185384"
"C:\datasets\python-distutils-sig"	"32223"
"C:\datasets\python-ideas"	"51920"
"C:\datasets\python-lists"	"702810"
"C:\datasets\python-patches"	"21319"

-- for all messges - not only those which have peps
SELECT folder, COUNT(DISTINCT messageid) 
FROM allmessages
where DATE2 < '2018-07-12' -- BDFL steps down
AND pep <> -1 
GROUP BY folder

"C:\datasets-additional\python-3000"	"3804"
"C:\datasets\core-workflow"	"1"
"C:\datasets\Datetime-SIG"	"1"
"C:\datasets\import-sig"	"1"
"C:\datasets\python-announce-list"	"353"
"C:\datasets\python-bugs-list"	"9995"
"C:\datasets\python-checkins"	"10290"
"C:\datasets\python-committers"	"424"
"C:\datasets\python-dev"	"36147"
"C:\datasets\python-distutils-sig"	"5351"
"C:\datasets\python-ideas"	"8695"
"C:\datasets\python-lists"	"13987"
"C:\datasets\python-patches"	"1221"

-- for all peps in that period
SELECT folder, COUNT(DISTINCT pep) 
FROM allmessages
where DATE2 < '2018-07-12' -- BDFL steps down
AND pep <> -1 
GROUP BY folder

"C:\datasets-additional\python-3000"	"133"
"C:\datasets\core-workflow"	"1"
"C:\datasets\Datetime-SIG"	"1"
"C:\datasets\import-sig"	"1"
"C:\datasets\python-announce-list"	"230"
"C:\datasets\python-bugs-list"	"327"
"C:\datasets\python-checkins"	"433"
"C:\datasets\python-committers"	"152"
"C:\datasets\python-dev"	"454"
"C:\datasets\python-distutils-sig"	"97"
"C:\datasets\python-ideas"	"279"
"C:\datasets\python-lists"	"325"
"C:\datasets\python-patches"	"99"

-- all peps by peptype
SELECT p.type, COUNT(DISTINCT am.pep) 
FROM allmessages am
LEFT JOIN
    pepdetails2020 p ON p.pep = am.pep
where DATE2 < '2018-07-12' -- BDFL steps down
AND am.pep <> -1 
GROUP BY p.type


SELECT pep, authorCorrected,bdfl_delegateCorrected,type,created 
from pepdetails2020
WHERE LENGTH(bdfl_delegatecorrected) > 0
ORDER BY pep desc;