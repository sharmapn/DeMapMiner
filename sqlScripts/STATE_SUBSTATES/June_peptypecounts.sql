

SELECT type, count(pep)
FROM pepdetails
WHERE pep IN
(
	SELECT DISTINCT pep 
	FROM results_postprocessed
)
GROUP BY TYPE

"Informational"	"63"
"Process"	"41"
"Standards Track"	"356"
