-- updated to cater for the new column  'inReplyToUserUsingClusteredSender'
UPDATE allmessages a1
INNER JOIN allmessages a2 ON a2.emailMessageID = a1.inReplyTo
SET a1.inReplyToUserUsingClusteredSender = a2.clusterBySenderFullName
WHERE a1.inReplyTo IS NOT NULL

ALTER TABLE allmessages ADD INDEX `inReplyToUserUsingClusteredSender` (`inReplyToUserUsingClusteredSender`(30));
ALTER TABLE allmessages ADD INDEX `clusterBySenderFullName` (`clusterBySenderFullName`(30));

SELECT inreplytouser, inReplyToUserUsingClusteredSender from allmessages
limit 50;

SELECT sendername, cluster, clusterbysendername
from distinctsenders
where emailaddress = 'amy.berry@cox.net';

select sendername 
from distinctsenders
where cluster = 12194;

select count(*) from allmessages where senderemail = 'amy.berry@cox.net';
select * from distinctsenders where emailaddress ='duikboot@localhost.localdomain';
select * from distinctsenders where clusterbysendername = 3868;
select * from distinctsenders where cluster = 10417;
-- main one
select * from distinctsenders where sendername = 'Brian' order by cluster;

select cluster,sendername, count(*) as members
from distinctsenders
group by cluster
order by members desc;

select * from distinctsenders where cluster = 3108;