select pep,date,email 
from danieldata
-- where email like '%super%'
order by pep,date

select pep,datetimestamp,email 
from pepstates_danieldata_datetimestamp -- 
-- where email like '%super%'
order by pep,datetimestamp


DELETE t1 FROM pepstates_danieldata_datetimestamp t1
        INNER JOIN
    pepstates_danieldata_datetimestamp t2 
WHERE
    t1.pep = t2.pep AND t1.email = t2.email;