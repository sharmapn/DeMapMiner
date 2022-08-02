
CREATE OR REPLACE VIEW peptypecount as
SELECT DISTINCT pep, (SELECT type From pepdetails p  WHERE pepstates_danieldata_datetimestamp.pep = p.pep LIMIT 1) as ptype
FROM pepstates_danieldata_datetimestamp
WHERE pep < 8000 AND pep <> -1

SELECT ptype, COUNT(pep)
FROM peptypecount
GROUP BY ptype

-- main query
SELECT state, peptype, COUNT(DISTINCT pep) 
-- (SELECT DISTINCT state FROM pepstates_danieldata_datetimestamp p WHERE pepstates_danieldata_datetimestamp.pep = p.pep LIMIT 1) AS state
FROM pepstates_danieldata_datetimestamp
WHERE pep < 8000 AND pep <> -1
GROUP BY state, peptype 
ORDER BY state asc

-- SELECT state FROM pepstates_danieldata_datetimestamp