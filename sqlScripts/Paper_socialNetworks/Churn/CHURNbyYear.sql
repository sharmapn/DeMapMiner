SELECT COUNT(sendername), 
-- max(date2),min(date2), DATEDIFF(CURDATE(),max(date2)) as c, COUNT(messageid) as d, folder,
YEAR(max(date2)) as ymax -- ,YEAR(min(date2)) as ymin -- , YEAR(date2) as ye
from allmessages
where folder = 'c:\\datasets\\python-dev'
group by ymax
-- having c > 365
-- order by d
limit 50