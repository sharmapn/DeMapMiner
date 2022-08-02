
-- we want the last occrences in each pep

CREATE or replace VIEW rp_bdflp as
SELECT distinct pep, TIMESTAMP, clausie, 
(SELECT type From pepdetails p  WHERE results_postprocessed.pep = p.pep LIMIT 1) as ptype
FROM results_postprocessed 
WHERE clausie LIKE '%bdfl pronouncement%' OR clausie LIKE '%bdfl delegate%'
ORDER BY pep, TIMESTAMP desc


-- SELECT id,email,(SELECT name From Names WHERE Names.id=Users.id) as name
-- FROM Users

CREATE or replace VIEW rp_consensus as
SELECT distinct pep, TIMESTAMP, clausie, 
(SELECT type From pepdetails p  WHERE results_postprocessed.pep = p.pep LIMIT 1) as ptype
FROM results_postprocessed
WHERE clausie LIKE '%consensus%'  
-- OR clausie LIKE '%poll%' OR clausie LIKE '%vot%'
ORDER BY pep, TIMESTAMP DESC

	-- combines with above .so not used in calculation..but just for getting numbers
	CREATE or replace VIEW rp_pv as
	SELECT distinct pep
	-- , TIMESTAMP, clausie, 
	-- (SELECT type From pepdetails p  WHERE results_postprocessed.pep = p.pep LIMIT 1) as ptype
	FROM results_postprocessed
	WHERE clausie LIKE '%poll%' OR clausie LIKE '%vot%'
	ORDER BY pep, TIMESTAMP desc
 
-- main query see which peps have both
CREATE or replace VIEW rp_both as 
SELECT pep, clausie, ptype
FROM rp_bdflp  
WHERE clausie LIKE '%bdfl pronouncement%' OR clausie LIKE '%bdfl delegate%'
 AND rp_bdflp.pep IN
   	(SELECT pep
		FROM rp_consensus
		WHERE clausie LIKE '%consensus%')
		
-- second main query minus 
CREATE or replace VIEW rp_both as 
SELECT pep
FROM rp_bdflp  
-- WHERE clausie LIKE '%bdfl pronouncement%' OR clausie LIKE '%bdfl delegate%'
 LEFT JOIN
   	rp_consensus USING (PEP)
   	WHERE
    	rp_consensus.PEP IS NULL;
		--WHERE clausie LIKE '%consensus%')		
		
SELECT 
    id
FROM
    t1
LEFT JOIN
    t2 USING (id)
WHERE
    t2.id IS NULL;		
		
		
-- last check to see if these peps actually have both
SELECT pep, TIMESTAMP, clausie
FROM results_postprocessed
WHERE pep IN
	(SELECT pep
		FROM rp_both)	
ORDER BY pep, TIMESTAMP asc