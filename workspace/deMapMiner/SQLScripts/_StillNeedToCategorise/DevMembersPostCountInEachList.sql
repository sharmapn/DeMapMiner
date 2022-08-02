-- run for each developer
-- actually just for the top 20 in each list
INSERT INTO memberPostCount (sendername, folder, postCount)
select sendername, folder, count(distinct(messageid))
from allmessages
where sendername = 'barry.warsaw'
group by sendername, folder
-- limit 10

