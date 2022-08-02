select * 
from distinctsenders 
WHERE clusteredBySenderName IS NULL AND clusterBySenderName IS NULL AND senderFirstName IS NULL AND senderLastName IS NULL  
order by totalMessageCount DESC LIMIT 1

select id from distinctsenders where trim(sendername) = '';

create table distinctsendersfirstnames as 
SELECT DISTINCT (SENDERFIRSTNAME) as SENDERFIRSTNAME from distinctsenders
order by SENDERFIRSTNAME

ALTER TABLE distinctsendersfirstname ADD INDEX `firstname` (`senderfirstname`(20));