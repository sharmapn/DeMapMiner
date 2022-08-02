-- dec 2018

SELECT DISTINCT arg1, relation, arg2 from labelextraction_sub 
where arg2 LIKE 'pep' or arg2 LIKE 'pep'

-- we can do verb-verb pairs but once extracted, would be hard to match in sentences 

-- triples we can approach in 2 ways, 
-- 1) elimination one by one aftre checking each state
-- 2) getting the distinct combinations and order by frequence and removing them from top

-- First way - Elimination -- we can maybe implement elimination
SELECT arg1, relation, arg2, count(*) as count 
from  labelextraction_sub -- extractedrelations_clausie  -- labelextraction_sub
-- what of we jus imit by pep
where arg1 LIKE '%pep%'	-- and ( relation like '%reject%' OR arg2 like '%reject%')
and (relation not like '%reject%'  and arg2 not like '%reject%') and (relation not like '%draft%'  and arg2 not like '%draft%')
and (relation not like '%accept%'  and arg2 not like '%accept%') and (relation not like '%final%'  and arg2 not like '%final%')
and (relation not like '%withraw%'  and arg2 not like '%withdraw%') and (relation not like '%supercede%'  and arg2 not like '%supercede%')
and (relation not like '%close%'  and arg2 not like '%close%') and (relation not like '%incomplete%'  and arg2 not like '%incomplete%')
and (relation not like '%active%'  and arg2 not like '%active%') and (relation not like '%pending%'  and arg2 not like '%pending%')
-- our addeed
and (relation not like '%propose%'  and arg2 not like '%propose%') and (relation not like '%propose%'  and arg2 not like '%propose%')
and (relation not like '%vote%'  and arg2 not like '%vote%') and (relation not like '%voting%'  and arg2 not like '%voting%')
and (relation not like '%poll%'  and arg2 not like '%poll%')--  and (relation not like '%propose%'  and arg2 not like '%propose%')
-- eliminate one by one 
-- and(      
--		 (arg1 LIKE '%pep%' and (relation like '%propose%' or arg2 like '%propose%') )	OR  (arg1 LIKE '%pep%' and (relation like '%reject%'  or arg2 like '%reject%') )	
-- )
group by arg1, relation, arg2
order by count desc