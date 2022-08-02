-- Now why was I only focussing on acc rej peps. 
-- So now I focus on all peps created before bdfl resigned, not only 245 peps
CREATE TABLE allpepstillBDFL AS
SELECT * 
FROM (SELECT DISTINCT PEP, state, DATE2, peptype						
	FROM pepstates_danieldata_datetimestamp  
	-- not only accrejpeps
	-- WHERE (STATE LIKE '%acc%' OR state LIKE '%rej%') 
	-- AND PEP IN (SELECT pep FROM pepdetails WHERE LENGTH (bdfl_delegatecorrected) > 0 ) 
	WHERE pep > 0
	order by DATE2 desc)
	AS tbA
LEFT JOIN ( SELECT pep AS P2, author, bdfl_delegatecorrected FROM pepdetails WHERE created <= '2018-07-12') as tbC
	ON tbA.pep = tbC.p2
	GROUP BY pep
ORDER BY tbA.pep;