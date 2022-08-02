UPDATE extractedrelations_clausie SET relation = REPLACE(relation, '(', ''); -- 0 rows affected
UPDATE extractedrelations_clausie SET relation = REPLACE(relation, ')', '');	-- 367,770 rows affected
UPDATE extractedrelations_clausie SET arg1 = REPLACE(arg1, '(', '');
UPDATE extractedrelations_clausie SET arg2 = REPLACE(arg2, ')', '');

select * from extractedrelations_clausie where arg1 like '%pep%' or arg2 like '%pep%';

select * from labelextraction;
SELECT DISTINCT arg1, relation, arg2 from labelextraction order by arg1, relation, arg2


SELECT id,arg1, relation, arg2 from extractedrelations_clausie where arg2 LIKE '%pep%';

SELECT DISTINCT arg1, relation, arg2 from labelextraction_sub where arg1 LIKE 'pep' order by arg1, relation, arg2
SELECT arg1, relation, arg2, count(*) as count 
from labelextraction_sub 
where arg1 LIKE '%pep%'
group by arg1, relation, arg2
order by arg1 asc, count desc

select max(pep) from extractedrelations_clausie

select * from allmessages where messageid = 1209726;