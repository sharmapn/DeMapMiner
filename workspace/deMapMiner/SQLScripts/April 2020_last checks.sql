


SELECT * 
from allmessages
WHERE folder = 'C:\\datasets\\python-distutils-sig'
AND PEP <> -1
LIMIT 5

SELECT pep, COUNT(DISTINCT messageid)  AS CNT
from allmessages
WHERE folder = 'C:\\datasets\\python-distutils-sig'
AND PEP <> -1
GROUP BY PEP
ORDER BY cnt desc
--LIMIT 5

SELECT pep, COUNT(DISTINCT pep) 
FROM allmessages 
WHERE folder = 'C:\\datasets\\import-sig'

-- SELECT PEP, STATE, dmconcept, label, folder, causesentence, effectsentence, notes, consider, toconsiderwhilereportingresults
SELECT PEP, STATE, dmconcept, label, folder, causesentence, effectsentence, notes, consider, toconsiderwhilereportingresults
FROM trainingdata 
WHERE PEP IN
(
	SELECT pep
	from allmessages
	WHERE folder = 'C:\\datasets\\python-distutils-sig'
	AND PEP <> -1
)
ORDER BY pep



--chapter 5.1 
 -- who is the decsion maker in the particpants
 
 -- pre@k and ndcg@k
 
 
 -- reasons bui and video


select pep, state,label, dmconcept, causesentence, effectsentence
 FROM trainingdata 
WHERE label LIKE '%bdfl pronouncement%'
order BY label

SELECT * FROM trainingdata
 

pep = 391