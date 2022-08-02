select distinct (sendername), levenshtein_distance('Guido',sendername) as t 
from distinctsenders  
where sendername IS NOT NULL 
order by t asc 
limit 15