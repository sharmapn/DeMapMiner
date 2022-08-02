-- to see guido cluster
select id,sendername, emailaddress,senderFirstName, senderLastName, senderEmailFirstSegment, totalMessageCount , cluster
from distinctsenders
WHERE clustered =1 order by cluster desc, totalMessageCount DESC;

-- see how many individuals use the same email address
select * from distinctsenders order by emailaddress asc; -- totalsendercount desc;

-- SEE WHERE STUCK
select id,sendername, emailaddress,senderFirstName, senderLastName, senderEmailFirstSegment, totalMessageCount 
from distinctsenders 
WHERE clustered IS NULL order by totalMessageCount DESC limit 1

update distinctsenders set cluster = NULL, clustered = NULL where cluster = 1;  -- 1, 12624

update distinctsenders set sendername = 'Laurentiu C Badea', senderLastName = 'Badea' where id = 2138;

update distinctsenders set sendername = 'gender' where id = 14432;


