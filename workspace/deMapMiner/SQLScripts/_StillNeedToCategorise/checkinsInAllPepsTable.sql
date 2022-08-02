select * from allpeps 
where email like '%-Status: %' 
AND folder like '%checkins%'
AND PEP <> 0
LIMIT 100