select pep, count(*)  as records
from pepstates_danieldata_datetimestamp
group by pep

create table tempCountPepsStates as
select pep, count(messageid) as pepsNew_StatesCount from peps_new.pepstates_danieldata_datetimestamp group by pep order by pep;
create table tempCountPeps2018States as
select pep, count(messageid) as pepsNew_StatesCount from peps_2018.pepstates_danieldata_datetimestamp group by pep order by pep;


-- assuming peps 2018 has ,ore peps amd states we use left join using it
select a.pep, a.pepsNew_StatesCount as new, b.pepsNew_StatesCount as old
from tempCountPeps2018States a
LEFT JOIN tempCountPepsStates b ON a.pep = b.pep
group by pep order by pep;
-- new
select * from peps_2018.pepstates_danieldata_datetimestamp order by pep asc, datetimestamp asc
-- new
select * from peps_new.pepstates_danieldata_datetimestamp  order by pep desc, datetimestamp asc
