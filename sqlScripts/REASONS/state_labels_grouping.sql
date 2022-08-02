select state, label, count(*) as cnt 
from trainingdata
where state like '%accepted%' or state like '%rejected%'
group by state, label
order by state, cnt desc