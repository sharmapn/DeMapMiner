-- selects (clusterBySenderFullName, folder, postCount, subjects, peps) from one table at a time
-- keep changing tablename from which to select from

-- can insert into either of two tables prominent members = memberpostcount
-- 			  all members       = allmemberpostcounr

INSERT INTO allmemberPostCount (clusterBySenderFullName, folder, postCount, subjects, peps)
select * from
	(select clusterBySenderFullName, folder, count(distinct(messageid)) as posts , count(distinct(subject)) as subjects, count(distinct(pep)) as peps
	from allmessages_lists		-- keep changing tis tablename to get data for popultating table  allmessages_announce_list
	where clusterBySenderFullName IS NOT NULL
	-- where clusterBySenderFullName = 'barry.warsaw'
	group by clusterBySenderFullName
	order by posts desc
	-- limit 20   --use limit of you want to limit to 20 members, if you want all, dont limit
	) as t
UNION
	select clusterBySenderFullName, folder, count(distinct(messageid)) as posts , count(distinct(subject)) as subjects, count(distinct(pep)) as peps
	from allmessages_lists		-- keep changing tis tablename to get data for popultating table
	where clusterBySenderFullName IS NOT NULL
	AND clusterBySenderFullName IN('guido van rossum','Martin von Loewis', 'nick coghlan', 'Greg Ewing', 'Brett Cannon', 'Barry A Warsaw', 'Terry Reedy',
												'skip', 'Paul Moore', 'Antoine Pitrou', 'rhettinger', 'aahz', 'tim peters')
	group by clusterBySenderFullName;



