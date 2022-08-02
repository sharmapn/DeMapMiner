
SELECT * FROM
	(	
		SELECT * FROM
			(
				select pep, email
				from pepstates_danieldata_datetimestamp		
				ORDER BY datetimestamp ASC
			) AS t
		group by pep
	) AS r
	WHERE
	email NOT LIKE '%ACTIVE%'
	AND email NOT LIKE '%DRAFT%'
	AND email NOT LIKE '%INCOMPLETE%'
	AND email NOT LIKE '%DEFERRED%'
ORDER BY pep


SELECT *
-- id, *, pep, state, DATE2, datetimestamp
FROM pepstates_danieldata_datetimestamp
WHERE pep = 435
ORDER BY datetimestamp ASC