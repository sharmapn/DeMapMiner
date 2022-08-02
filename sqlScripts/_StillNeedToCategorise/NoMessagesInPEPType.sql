-- select distinct(folder) from allpeps; -- where folder like 'C:\data\%' ;

SELECT folder, COUNT(*) 
from allpeps
-- WHERE folder like 'C:\data\%'
group by folder