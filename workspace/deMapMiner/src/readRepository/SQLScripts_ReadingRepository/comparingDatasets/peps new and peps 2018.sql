-- create temp tables
create table tempCountPeps as
select pep, count(messageid) as pepsNew_MessageIDCount from allmessages group by pep order by pep;
create table tempCountPeps2018 as
select pep, count(messageid) as pepsNew_MessageIDCount from peps_2018.allmessages group by pep order by pep;
-- now select
select a.pep, a.pepsNew_MessageIDCount as new, b.pepsNew_MessageIDCount as old
from tempCountPeps2018 a
LEFT JOIN tempCountPeps b ON a.pep = b.pep
group by pep order by pep;
-- now join on commit messages