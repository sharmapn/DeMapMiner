select folder,sendername, count(distinct(messageid))
from allmessages
-- where sendername = 'barry.warsaw'
where sendername in (select distinct(sendername) from memberpostcount)
group by folder,sendername