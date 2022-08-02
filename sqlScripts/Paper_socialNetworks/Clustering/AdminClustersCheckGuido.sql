select * from distinctsenders
-- where clustered = 1
where sendername like '%hettinger%'   -- guido, where sendername like '%warsaw' or  sendername like '%barryw'
or  emailaddress like '%hettinger%'	 -- guido, where emailaddress like '%warsaw' or  emailaddress like '%barryw'
order by cluster desc, totalmessagecount, sendername;
-- order by cluster asc

select * from distinctsenders 
ORDER by totalmessagecount desc 
limit 50;
-- where clustered = NULL

-- check rhettinger

-- exact problem we face is shown by this query
select * from distinctsenders
-- where clustered = 1
where sendername like '%hettinger%'   -- hettinger, guido, where sendername like '%warsaw' or  sendername like '%barryw'
or  emailaddress like '%hettinger%'	 -- hettinger, guido, where emailaddress like '%warsaw' or  emailaddress like '%barryw'
order by cluster desc, totalmessagecount, sendername;
-- doesnt work for AHettinger@Prominic.NET and Andrew M Hettinger

select * from distinctsenders
where clusterBySenderName = 2;

-- populate teh frstname in distinctsenders with the most common firstname ending in all messages with that email
-- select 

SELECT COUNT(*) from distinctsenders where clusteredbysendername is null
and senderFirstName is null  and senderLastName is null;

SELECT id,sendername, emailaddress, senderFirstName, senderLastName, 
Levenshtein('rhettinger', CONCAT(SUBSTRING(senderfirstname,1,1),senderLastName)) AS 'sendernameVs1stLetterFirstNameRestLName' 
FROM distinctsenders 
HAVING sendernameVs1stLetterFirstNameRestLName <2;

select emailaddress, senderFirstName,senderLastName, CONCAT(SUBSTRING(LOWER(senderfirstname),1,1),LOWER(senderLastName)) AS c,
Levenshtein('rhettinger',lower(CONCAT(SUBSTRING(senderfirstname,1,1),senderLastName))) as 'ld' 
from distinctsenders
where senderLastName LIKE '%Hettinger%';

-- CHECK length sendername > 3 
-- if sendername is atleast 3 characters then use sendername match, else use email first segment
SELECT id,sendername, emailaddress, senderFirstName, senderLastName,	-- not sure why i put in front DISTINCT (sendername) AS MATCHING,
NULL AS 'DUMMY', Levenshtein('skip',LOWER(sendername)) AS LevenshteinDistance
FROM distinctsenders 
WHERE clustered IS NULL AND LENGTH(sendername) > 3
HAVING LevenshteinDistance <2 

1. Make everything smallcase
 all names, except original
2. then run the clustering algorithm

SELECT max(clusteredBySenderName) from distinctsenders

select count(distinct(clusterbysendername)) from distinctsenders;

-- get subclusters
select *  -- min(clusterbysendername),
from distinctsenders
where cluster=44
-- 
select clusterbysendername,  count(clusterbysendername) as cnt  -- min(clusterbysendername),
from distinctsenders
where sendername like '%hettinger%' -- where cluster=44
or  emailaddress like '%hettinger%'
group by clusterbysendername
order by cnt desc;