-- BEFORE CLUSTERING
-- update distinctsenders set cluster=NULL, clustered=NULL,clusterBySenderName = NULL, clusteredBySenderName = NULL;	
 update distinctsenders set clustered = NULL, cluster = NULL;   -- set unclustered for all
update distinctsenders set clusterBySenderName = NULL, clusteredBySenderName = NULL;	
update distinctsenders set clustered = 2, clusteredBySenderName = 2 where emailaddress = 'report@bugs.python.org';
update distinctsenders set clustered = 2, clusteredBySenderName = 2 where emailaddress = 'python-checkins@python.org';
update distinctsenders set clustered = 2, clusteredBySenderName = 2 where emailaddress = 'noreply@sourceforge.net';
update distinctsenders set clustered = 2, clusteredBySenderName = 2 where emailaddress = 'python-dev@python.org';
update distinctsenders set clustered = 2, clusteredBySenderName = 2 where emailaddress = 'python@mrabarnett.plus.com';
update distinctsenders set clustered = 2, clusteredBySenderName = 2 where emailaddress = 'buildbot@python.org';
update distinctsenders set clustered = 2, clusteredBySenderName = 2 where trim(sendername) = '';
update distinctsenders set clustered = 2, clusteredBySenderName = 2 where sendername IS NULL;

-- CHECK CLUSTERING RESULTS
-- exact problem we face is shown by this query

select * from distinctsenders
-- where clustered = 1
where sendername like '%hettinger%'  -- OR sendername like '%raymond%'    -- hettinger, guido, where sendername like '%warsaw' or  sendername like '%barryw'
or  emailaddress like '%hettinger%'	 -- OR emailaddress like '%raymond%'  -- hettinger, guido, where emailaddress like '%warsaw' or  emailaddress like '%barryw'
order by cluster desc, totalmessagecount, sendername;
-- doesnt work for AHettinger@Prominic.NET and Andrew M Hettinger - thats good

	-- MAIN QUERY WHICH WILL SHOW BIGGEST CLUSTERS..JUST HAVE TO CHECK WHICH CLUSTER HAS Most senders..look for single sendername
	select cluster, max(sendername), count(id) as emailaddresses -- , max(sendername), sum(totalMessageCount)
	from distinctsenders
	WHERE cluster IS NOT NULL 
	group by cluster
	order by emailaddresses DESC ;
	-- now group for clustering by sendername
	select clusterbysendername, sendername, max(length(sendername)), count(id) as emailaddresses -- , max(sendername), sum(totalMessageCount)
	from distinctsenders
	WHERE clusterbysendername IS NOT NULL 
	group by clusterbysendername
	order by emailaddresses DESC ;
	
	select * from distinctsenders where cluster= 1522; 
	select * from distinctsenders where clusterbysendername= 510 order by totalmessagecount desc;   
	

-- select count(*) from distinctsenders where clustered = 1
 SELECT max(clusteredBySenderName) from distinctsenders
 select distinct(clusterbysendername) from distinctsenders;
 
select count(*) from distinctsenders where cluster is null; -- check while processing is being done to see how many rows left
select count(*) from distinctsenders where clusterbysendername is null; 
select count(cluster) from distinctsenders where cluster is not null;
select * from distinctsenders where emailaddress = 'esr@thyrsus.com'; -- where cluster is null;

-- sendername query

SELECT id,sendername, emailaddress, senderFirstName, senderLastName, clusteredBySenderName, clusterBySenderName,
Levenshtein('rhettinger', lower(CONCAT(SUBSTRING(senderfirstname,1,1),trim(senderLastName)))) AS 'sendernameVs1stLetterFirstNameRestLName' 
FROM distinctsenders 
-- WHERE clusteredBySenderName IS NULL AND clusterBySenderName IS NULL 
HAVING sendernameVs1stLetterFirstNameRestLName <2;

create table distinctsendersfirstname as 
SELECT DISTINCT (SENDERFIRSTNAME) as SENDERFIRSTNAME from distinctsenders
order by SENDERFIRSTNAME

select * from distinctsendersfirstname order by SENDERFIRSTNAME;
-- where SENDERFIRSTNAME = 'rhettinger';

select distinct(cluster),totalMessageCount
from distinctsenders 
WHERE cluster IS NOT NULL AND clustered IS NOT NULL and clusteredbysendername IS NOT NULL 
order by totalMessageCount DESC;

select * from distinctsenders where cluster =44  or clusterbysendername = 4;

select COUNT(*) from distinctsenders where reflected IS NULL;

UPDATE ALLMESsAGES set clusterBySenderFullName = NULL where clusterBySenderFullName IS NOT NULL;
update distinctsenders set reflected =NULL;
-- select count(*) from ALLMESsAGES where clusterBySenderFullName IS NOT NULL;


-- LAST STEP 
UPDATE allmessages a1
JOIN allmessages a2 ON a2.emailMessageID = a1.inReplyTo
SET a1.inReplyToUserUsingClusteredSender = a2.clusterBySenderFullName
WHERE a1.inReplyTo IS NOT NULL

SELECT COUNT(inReplyToUserUsingClusteredSender) FROM ALLMESSAGES
WHERE inReplyToUserUsingClusteredSender IS NOT NULL;