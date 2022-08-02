SELECT peptype, COUNT(pep) FROM bdflddpeps
GROUP BY peptype
"peptype"	"COUNT(pep)"
"Informational"	"10"
"Process"	"6"
"Standards Track"	"35"

SELECT pep, peptype FROM bdflddpeps

SELECT pep, folder, COUNT(messageid) FROM allmessages 
WHERE pep IN (SELECT pep FROM bdflddpeps) AND folder IS NOT NULL
AND (folder = 'C:\\datasets\\python-distutils-sig' OR folder = 'C:\\datasets\\python-dev')
GROUP BY pep, folder


SELECT * FROM pepeditors