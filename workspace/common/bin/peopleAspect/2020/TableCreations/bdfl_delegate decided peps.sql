-- total of 52 PEPs that were decided by bdfl delegate during bdfl
-- but only 40 that were acc or rej

SELECT pep, authorCorrected,bdfl_delegateCorrected,type,created 
from pepdetails2020
WHERE LENGTH(bdfl_delegatecorrected) > 0
-- AND pep IN (SELECT pep FROM allpepstillBDFL)
ORDER BY pep DESC ;

SELECT * FROM pepdetails

--main table
-- DROP TABLE bdflddpeps;
CREATE TABLE bdflddpeps AS  -- bdfl delegate decided peps
	SELECT pep, bdfl_delegate, bdfl_delegatecorrected, created, TYPE AS peptype 
	FROM pepdetails
	WHERE created < '2018-07-12'   
	AND LENGTH(bdfl_delegatecorrected) > 0
	ORDER BY pep;
--just try any date

SELECT bdfl_delegate, COUNT(DISTINCT pep) AS cnt
FROM pepdetails
WHERE created <= '2018-12-07'   
AND LENGTH(bdfl_delegate) > 0
group BY bdfl_delegate
ORDER BY cnt DESC


-- only 40 peps during bdfl that were acc or rej
SELECT pep, title, authorcorrected, TYPE, bdfl_delegatecorrected, created, authorrole, bdfldelegaterole, createdyear
FROM pepdetails 
WHERE LENGTH (bdfl_delegatecorrected) > 0

SELECT * 
FROM (SELECT DISTINCT PEP, state, DATE2, peptype						
	FROM pepstates_danieldata_datetimestamp  
	WHERE (STATE LIKE '%acc%' OR state LIKE '%rej%') 
	AND PEP IN (SELECT pep FROM pepdetails WHERE LENGTH (bdfl_delegatecorrected) > 0 ) 
	order by DATE2 ASC)
	AS tbA
LEFT JOIN ( SELECT pep, author, bdfl_delegatecorrected FROM pepdetails ) as tbC
	ON tbA.pep = tbC.pep
ORDER BY tbA.pep;
/* Affected rows: 0  Found rows: 40  Warnings: 0  Duration for 1 query: 0.016 sec. */