-- reflect clusters to main table
-- drop distinctclusters;
create view distinctclusters as
(
	select cluster, count(id) as members-- , max(sendername), sum(totalMessageCount)
	from distinctsenders
	WHERE cluster IS NOT NULL 
	group by cluster
	order by totalMessageCount DESC 
);

	select cluster, max(sendername), count(id) as emailaddresses-- , max(sendername), sum(totalMessageCount)
	from distinctsenders
	WHERE cluster IS NOT NULL 
	group by cluster
	order by emailaddresses DESC 
	
	select count(distinct(cluster)) from distinctsenders;
	
	-- if sendername is atleast 3 characters then use sendername match, else use email first segment
	
	select * from distinctsenders where cluster= 1081;
	
	select * from distinctsenders
	where cluster = 69
		order by totalMessageCount DESC 

-- Now for each cluster, find the similar sendernames
-- for each cluster, get the sendername/s and make them part 



select distinct(cluster) as cluster,totalMessageCount, MAX(LENGTH(sendername)) sendername
-- (SELECT sendername FROM distinctsenders  WHERE LENGTH(sendername) = (SELECT max(LENGTH(sendername)) FROM distinctsenders)) AS MAX
from distinctsenders 
WHERE cluster IS NOT NULL AND clustered IS NOT NULL and clusteredbysendername IS NOT NULL 
group by cluster
order by totalMessageCount DESC 

-- QUICK AND EASY QUERY TO CHECK REFLECTION
select senderFullname,senderemail, clusterBySenderFullName, count(*) cnt
from allmessages
where clusterBySenderFullName IS NOT NULL
group by senderFullname
order by cnt desc;