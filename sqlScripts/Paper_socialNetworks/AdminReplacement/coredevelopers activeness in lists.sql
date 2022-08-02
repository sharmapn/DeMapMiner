-- this script was used to find the postage pattern

SELECT * FROM
	(select clusterBySenderFullName, count(DISTINCT(messageid)) as cnt, year(date2) as year, folder as folder
	from allmessages
	-- where folder = 'C:\\datasets\\python-dev'
	-- and clusterBySenderFullName IS NOT NULL
	where clusterBySenderFullName IN ('guido van rossum','Martin von Loewis', 'nick coghlan', 'Greg Ewing', 'Brett Cannon', 'Barry A Warsaw', 'Terry Reedy',
													'skip', 'Paul Moore', 'Antoine Pitrou', 'rhettinger', 'aahz', 'tim peters')
							
	group by clusterBySenderFullName, year, folder
	) as inn
	-- where year = 2000
	WHERE clusterBySenderFullName IN ('Martin von Loewis')
	GROUP BY Year,clusterBySenderFullName, folder;
	INTO OUTFILE 'c:\\scripts\\fewProminentDevelopersActivenessInAlllists_23-04-20172.txt';
-- order by cnt desc
-- limit 20;