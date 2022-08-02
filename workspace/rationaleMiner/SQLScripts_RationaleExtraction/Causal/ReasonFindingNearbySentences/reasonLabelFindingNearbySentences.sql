select messageid, pep,subject, relation,object, clausie,ps,currentsentence,ns 
from results
where clausie like '%consensus%' -- and pep = 433
order by pep, datetimestamp;

select * from results; -- labels
select * from allmessages where messageid =  1328547; -- 202041; -- 391730

select * from labels where subject like '%thread%'
select pep from pepdetails order by pep desc limit 20;
delete from results where pep = 431;