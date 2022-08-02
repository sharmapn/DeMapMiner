-- main one
select pep, COUNT(*) as PEPCount from extractedrelations_clausie group by pep order by PEPCount desc
select max(pep) from extractedrelations_clausie  where arg1 like '%pep%' or arg2 like '%pep%';
SELECT distinct(pep) from allmessages_dev where pep <> -1
select * from extractedrelations_clausie;
select count(*) from allmessages where pep =0 and folder like '%dev%'
select * from allmessages where messageid = 2558;
SELECT distinct(pep) from extractedrelations_clausie ;
select avg(SELECT count(*) as nu from extractedrelations_clausie group by messageid) from dual;
-- average Messages pep PEP = 2155
select AVG(sub.midCount)
from(
    select messageid, COUNT(*) midCount from extractedrelations_clausie group by pep
)as sub
-- average triples per message= 30
select AVG(sub.midCount)
from(
    select messageid, COUNT(*) midCount from extractedrelations_clausie group by messageid
)as sub
-- main query
select pep, COUNT(*) PEPCount from extractedrelations_clausie group by pep order by PEPCount desc
-- average triples per PEP = 2163, 9729 with corrected code
select AVG(sub.PEPCount)
from(
    select pep, COUNT(*) PEPCount from extractedrelations_clausie group by pep
)as sub
-- important main query
SET @term = '%accept%';    
select * from extractedrelations_clausie
where arg1 like  @term or relation like  @term  or arg2 like  @term  
order by pep;

-- extract from alllists
-- perform on email rather than analyse words
