INSERT INTO memberPostCount (sendername, folder, postCount, subjects, peps)
select sendername, folder, count(distinct(messageid)) as posts , count(distinct(subject)) as subjects, count(distinct(pep)) as peps
from allmessages_lists			-- keep changing tis tablename to get data for popultating table
-- where sendername = 'barry.warsaw'
group by sendername
order by posts desc
limit 20 