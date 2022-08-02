select * from extractedrelations_clausie where arg1 like '%jep%' or arg2 like '%jep%';
-- only verbs
select distinct(relation) from extractedrelations_clausie where arg1 like '%jep%' or arg2 like '%jep%';