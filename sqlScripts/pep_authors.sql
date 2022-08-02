select TRIM(author) as ath, COUNT(author) as cnt 
from pepdetails
where author IS NOT NULL
group by ath
order by ath asc