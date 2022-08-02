-- exact problem we face is shown by this query
select * from distinctsenders
-- where clustered = 1
where sendername like '%hettinger%'   -- guido, where sendername like '%warsaw' or  sendername like '%barryw'
or  emailaddress like '%hettinger%'	 -- guido, where emailaddress like '%warsaw' or  emailaddress like '%barryw'
order by cluster desc, totalmessagecount, sendername;

-- populate teh frstname in distinctsenders with the most common firstname ending in all messages with that email
select RIGHT(email,10) as t, count(*) as co
from allmessages 
where senderemail = 'rhettinger@users.sourceforge.net'
-- limit 5;
 group by t
 order by co ;
