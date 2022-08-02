select id,sendername, emailaddress,senderFirstName, senderLastName, senderEmailFirstSegment, totalMessageCount 
	 from distinctsenders 
	-- WHERE clustered IS NULL order by totalMessageCount DESC 
	where clustered =1
	
	select cluster, id,sendername, emailaddress,senderFirstName, senderLastName, senderEmailFirstSegment, totalMessageCount 
	from distinctsenders 
	where clustered=1
	ORDER BY CLUSTER;
--	WHERE cluster=1; -- IS NULL order by totalMessageCount DESC

	SELECT max(cluster) from distinctsenders;
	
select * from distinctsenders
-- where clustered = 1
where sendername like '%guido%'   -- where sendername like '%warsaw' or  sendername like '%barryw'
or  emailaddress like '%guido%'	 -- where emailaddress like '%warsaw' or  emailaddress like '%barryw'
order by cluster desc, totalmessagecount, sendername;
-- order by cluster asc

select * from distinctsenders 
ORDER by totalmessagecount desc 
limit 50;
-- where clustered = NULL

select count(*) from distinctsenders where clustered IS NULL;