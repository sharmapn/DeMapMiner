--  select max(messageid) from allmessages;

delete from allmessages where messageid >= 2000000
 
select max(date2)
from allmessages
where folder = 'c:\\datasets\\python-patches'
-- group by folder