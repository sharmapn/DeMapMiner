

SELECT pep FROM pepdetails WHERE created < '2018-07-11'

SELECT COUNT(DISTINCT messageid) FROM allmessages
WHERE pep IN (SELECT pep AS c FROM allpepstillbdfl)

SELECT pep, author FROM pepdetails WHERE length(bdfl_delegate) >0

-- clusterbysenderfullname, 

SELECT pep FROM pepdetails WHERE length(bdfl_delegate) > 0
AND pep NOT IN (
					SELECT distinct pep FROM allmessages
					WHERE pep IN (SELECT pep FROM pepdetails WHERE length(bdfl_delegate) >0)
					AND authorsrole = 'bdfl_delegate'
					)
-- 405, 459, 503, 544

-- 453 ..martin needs to be corrected
SELECT pep, author, bdfl_delegate FROM pepdetails WHERE pep = 453
SELECT pep, email, messageid, clusterbysenderfullname, authorsrole 
FROM allmessages WHERE pep = 453 
-- AND clusterbysenderfullname = 'Martin von Loewis'
ORDER BY clusterbysenderfullname

SELECT * FROM pepdetails WHERE author LIKE '%martin%' OR bdfl_delegate LIKE '%martin%'