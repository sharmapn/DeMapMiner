-- when selecting for ld matching dont select report@bugs.python.org, python-checkins@python.org, 
-- noreply@sourceforge.net,, python-dev@python.org, python@mrabarnett.plus.com

-- update distinctsenders set sendername = 'Laurentiu C Badea', senderLastName = 'Badea\\' where id = 2138;

-- buildbot@python.org is just one user using it
-- update distinctsenders set cluster=NULL, clustered=NULL,clusterBySenderName = NULL, clusteredBySenderName = NULL;	
update distinctsenders set clustered = NULL, cluster = NULL;   -- set unclustered for all
update distinctsenders set clusterBySenderName = NULL, clusteredBySenderName = NULL;	
update distinctsenders set clustered = 2 where emailaddress = 'report@bugs.python.org';
update distinctsenders set clustered = 2 where emailaddress = 'python-checkins@python.org';
update distinctsenders set clustered = 2 where emailaddress = 'noreply@sourceforge.net';
update distinctsenders set clustered = 2 where emailaddress = 'python-dev@python.org';
update distinctsenders set clustered = 2 where emailaddress = 'python@mrabarnett.plus.com';
update distinctsenders set clustered = 2 where emailaddress = 'buildbot@python.org';

-- select count(*) from distinctsenders where clustered = 1