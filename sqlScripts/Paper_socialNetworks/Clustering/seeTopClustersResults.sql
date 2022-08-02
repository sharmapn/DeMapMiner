-- see top clusters by 
select cluster, sendername, count(*) emails
from distinctsenders
group by cluster
order by  emails desc

-- see top clusters by sendername
select clusterbysendername, sendername, count(*) emails
from distinctsenders
group by clusterbysendername
order by  emails desc