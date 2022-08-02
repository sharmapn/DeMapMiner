select dmconcept, label, count(*) as cnt,  peptype, state, count(distinct pep), GROUP_CONCAT(DISTINCT pep ORDER BY pep ASC SEPARATOR ', ') AS peps
from trainingdata
where consider = 1
and (STATE LIKE '%ACCEPT%' OR state like '%Rejected%')
group by dmconcept, label, state, peptype
order by dmconcept asc,  cnt DESC, peptype
-- label ASC,

-- Main top level query
-- just counts not peps
-- count(*) as cnt = number of reason sentences for peps

 --,  GROUP_CONCAT(DISTINCT pep ORDER BY pep ASC SEPARATOR ', ') AS peps
SELECT * FROM 
(                    -- 
	select dmconcept, label, peptype, state,  count(distinct pep) AS dis
	from trainingdata
	where consider = 1
	and (STATE LIKE '%ACCEPT%' OR state like '%Rejected%')
	group by dmconcept, label, state, peptype
	order by dmconcept asc,  dis DESC, peptype
) AS t
ORDER BY dmconcept, label, peptype desc, state asc
-- label ASC,