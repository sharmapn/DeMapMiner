-- bdfl for boundary spanners dataset dates = '2017-03-01' 


SELECT * FROM pepdetails

SELECT pep, bdfl_delegate, bdfl_delegatecorrected, created, TYPE AS t 
FROM pepdetails
WHERE created < '2017-03-01'   
AND LENGTH(bdfl_delegate) > 0
ORDER BY pep
--just try any date

SELECT bdfl_delegate, COUNT(DISTINCT pep) AS cnt
FROM pepdetails
WHERE created <= '2017-03-01'   
AND LENGTH(bdfl_delegate) > 0
group BY bdfl_delegate
ORDER BY cnt desc