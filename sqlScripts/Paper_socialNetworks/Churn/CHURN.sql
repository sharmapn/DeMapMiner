SELECT sendername, max(date2),min(date2), DATEDIFF(CURDATE(),max(date2)) as c, COUNT(messageid) as d, folder,
YEAR(max(date2)) as ymax,YEAR(min(date2)) as ymin
from allmessages
where folder = 'c:\\datasets\\python-dev'
group by sendername
having c > 365
order by d
limit 50