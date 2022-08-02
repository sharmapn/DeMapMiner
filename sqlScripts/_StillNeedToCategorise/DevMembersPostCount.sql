SELECT sendername, COUNT(DISTINCT(messageid)) as s from allmessages
where folder = 'c:\\datasets\\python-dev'
group by sendername
order by s desc
-- limit 100