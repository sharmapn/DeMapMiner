update distinctsenders set reflected =NULL;
UPDATE ALLMESsAGES set clusterBySenderFullName = NULL where clusterBySenderFullName IS NOT NULL;

select * from distinctsenders where id in (10417);
select * from distinctsenders where clusterBySenderName in (18192);
select count(*) from distinctsenders where clusteredBySenderName is null;

select count(*) from allmessages where senderemail = 'bruno@modulix';

UPDATE distinctsenders set clusteredBySenderName =2 where clusteredBySenderName IS NULL;
UPDATE distinctsenders set clusterBySenderName =2 where clusterBySenderName IS NULL;

-- 11997
SELECT cluster, SUM(totalMessageCount) sum, MAX(LENGTH(sendername)) maxSendernameLength 
FROM distinctsenders 
WHERE cluster = 11997;
-- AND reflected IS NULL 
-- GROUP by cluster 
-- ORDER by sum DESC 

select sendername as max from distinctsenders WHERE LENGTH(sendername) = 13 and cluster = 11997 ORDER BY max LIMIT 1
select * from distinctsenders WHERE cluster = 11997
select * from distinctsenders WHERE sendername like '%mental%';

select clusterbysendername,  count(clusterbysendername) as cnt  
from distinctsenders where cluster = 11997 
group by clusterbysendername order by cnt desc

update allmessages set clusterBySenderFullName = 'laureote-loic'
where senderemail = 'laureote-loic@hotmail.fr';


select * from distinctsenders where cluster is null
select * from distinctsenders where sendername = 'Brian';

-- check if clusterbysendername is done
SELECT *
FROM distinctsenders 
WHERE clusterbysendername IS NULL AND senderfirstname IS NULL AND senderlastname IS NULL 
AND clusteredbysendername <> 2;

SELECT count(cluster)
FROM distinctsenders 
WHERE cluster IS NOT NULL AND clustered = 1 
AND reflected IS NULL;