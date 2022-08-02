select * from distinctsenders -- where id = 59995
order by totalmessagecount desc
limit 500

-- while polupating other details of this table
-- if enmail address = 'report@bugs.python.org'
--	get the sendename and sendernlastname from that row in 'allmesaages' table
--	then set the name, firstname, lastname acco

-- when selecting for ld matching dont select report@bugs.python.org, python-checkins@python.org, 
-- noreply@sourceforge.net,, python-dev@python.org, python@mrabarnett.plus.com
-- buildbot@python.org is just one user using it

update distinctsenders set clustered = 1 where emailaddress = 'report@bugs.python.org';
update distinctsenders set clustered = 1 where emailaddress = 'python-checkins@python.org';
update distinctsenders set clustered = 1 where emailaddress = 'noreply@sourceforge.net';
update distinctsenders set clustered = 1 where emailaddress = 'python-dev@python.org';
update distinctsenders set clustered = 1 where emailaddress = 'python@mrabarnett.plus.com';

select distinct (sendername)
from allmessages 
where emailaddress = 'report@bugs.python.org';