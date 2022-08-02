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