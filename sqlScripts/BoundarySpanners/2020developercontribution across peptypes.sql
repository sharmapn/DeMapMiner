SELECT DISTINCT(clusterBySenderFullName) FROM alldevelopers order by senderName ASC

SELECT pep, authorCorrected,bdfl_delegateCorrected,type from pepdetails
SELECT * FROM pepdetailsold 
WHERE created  IS NULL
ORDER BY created DESC

-- some date swere null
SELECT pep, state, DATE2 FROM pepstates_danieldata_datetimestamp --_alltilldec2019 
WHERE -- (state LIKE '%draft%' OR state LIKE '%active%')
-- AND 
pep IN
(
	SELECT pep FROM pepdetailsold 
	WHERE created  IS NULL
)	

SELECT * FROM pepstates_danieldata_datetimestamp 


-- these set were not si we just set them to year 2000

"205"	"FINAL"	"2007-04-15"
"207"	"FINAL"	"2001-01-19"
"200"	"FINAL"	"2000-11-02"
"207"	"INCOMPLETE"	"2000-07-15"
"205"	"INCOMPLETE"	"2000-07-14"
"200"	"INCOMPLETE"	"2000-07-13"

so we basically SET pep 200, 205 AND 207 created DATE AS '2000-07-13'

SELECT * FROM coredevelopers 
WHERE coredeveloper LIKE '%oliphant%'
order by coredeveloper ASC

SELECT DISTINCT pep from pepdetailsold

SELECT * FROM PEPEDITORS

SELECT DISTINCT(clusterBySenderFullName) 
FROM alldevelopers 
WHERE clusterBySenderFullName LIKE '%karthik%'
order by clusterBySenderFullName ASC

SELECT * FROM pepdetails WHERE author LIKE '%oliphant%'

SELECT * FROM pepdetails WHERE PEP =1

SELECT *  FROM pepdetailsold 
WHERE pep IN
(4	,11	,103	,160	,205	,206	,211	,222	,229	,241	,244	,247	,262	,271	,272	,276	,286	,315	,321	,331	,335	,347	,353	,
379	,380	,381	,382	,383	,384	,393	,406	,434	,443	,465	,473	,482	,484	,497	,502	,505	,513	,517	,521	,522	,
533	,534	,539	,541	,542	,544	,754	,3101	,3102	,3115	,3119	,3120	,3121	,3123	,3131	,3136	,3140	,3152	)

-- UPDATE pepdetailsold SET authorcorrected = author
WHERE pep IN
(4	,11	,103	,160	,205	,206	,211	,222	,229	,241	,244	,247	,262	,271	,272	,276	,286	,315	,321	,331	,335	,347	,353	,
379	,380	,381	,382	,383	,384	,393	,406	,434	,443	,465	,473	,482	,484	,497	,502	,505	,513	,517	,521	,522	,
533	,534	,539	,541	,542	,544	,754	,3101	,3102	,3115	,3119	,3120	,3121	,3123	,3131	,3136	,3140	,3152	)


SELECT * FROM pepdetails 
WHERE author LIKE '%Nathaniel%'

Nathaniel J. Smith




