-- all churn scripts
-- the group2 function would update the year firstpostdate and lastpostdate

-- now just run this script to retrieve all count by last post date/ firstpostdate
-- just change yearlastpostdate to yearfirstpostdate
SELECT yearfirstpostdate, COUNT(sendername) -- , yearlastpostdate -- yearlastpostdate,
-- max(date2),min(date2), DATEDIFF(CURDATE(),max(date2)) as c, COUNT(messageid) as d, folder,
-- YEAR(max(date2)) as ymax,YEAR(min(date2)) as ymin
from alldevelopers
-- where folder = 'c:\\datasets\\python-dev'
group by yearfirstpostdate -- , yearfirstpostdate

-- this is already done, so just compare
-- update danieldata set yeardate2 = year(date2)
