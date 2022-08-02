select *  
from distinctsenders 
where emailaddress = 'mensanator@aol.compost'
-- where emailaddress like '%spam%' 
-- where sendername like '%admin%'
and senderfirstname IS NULL and senderlastname IS NULL
order by cluster asc;
-- totalMessageCount desc;
select * from allmessages a 
where  a.senderemail = 'mensanator@aol.compost';

select *  
from distinctsenders 
where senderfirstname IS NULL and senderlastname IS NULL;