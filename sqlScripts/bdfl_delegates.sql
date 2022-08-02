select TRIM(bdfl_delegate) as delegate, COUNT(bdfl_delegate) as cnt 
from pepdetails
where bdfl_delegate IS NOT NULL
group by delegate
order by delegate asc
-- where author like '%martin%'