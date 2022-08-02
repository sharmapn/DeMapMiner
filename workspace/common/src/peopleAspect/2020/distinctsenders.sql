select cluster, sendername, count(*) emails
from distinctsenders
group by cluster
order by  emails DESC

select *
from distinctsenders
WHERE cluster = 1
group by cluster
order by  emails desc