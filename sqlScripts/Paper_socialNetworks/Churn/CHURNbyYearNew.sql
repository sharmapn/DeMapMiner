SELECT yearlastpostdate, COUNT(sendername) -- , yearlastpostdate -- yearlastpostdate,
-- max(date2),min(date2), DATEDIFF(CURDATE(),max(date2)) as c, COUNT(messageid) as d, folder,
-- YEAR(max(date2)) as ymax,YEAR(min(date2)) as ymin
from distinctauthorspepnumbers
-- where folder = 'c:\\datasets\\python-dev'
group by yearlastpostdate -- , yearlastpostdate