select * 
from distinctsenders 
where cluster = 1783; -- IS NULL

select * from distinctsenders
where cluster in 
(
select clusterbysendername from distinctsenders
where sendername = 'Aahz')
;

select sendername, clusterbysendername -- ,  count(clusterbysendername) as cnt  
from distinctsenders where cluster = (select cluster from distinctsenders
													where sendername like '%hettinger%' LIMIT 1) 
group by clusterbysendername;
-- order by cnt desc;

-- improvements
-- correct for aahz
select * from distinctsenders
where sendername = 'gh' -- where sendername like '%Merwok%'

select * 
from distinctsenders 
where cluster in(113,6827,30749) or clusterbysendername = 11;

-- select * from distinctsenders where emailaddress = 'gh'

SELECT * -- count(distinct(messageid)) 
from allmessages 
where senderemail = 'merwok@netwok.org';