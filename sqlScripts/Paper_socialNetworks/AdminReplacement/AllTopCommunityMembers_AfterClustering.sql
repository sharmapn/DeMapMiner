create table allDevelopers
AS select DISTINCT(clusterBySenderFullName) from allmessages
where folder = 'C:\\datasets\\python-dev'
and clusterBySenderFullName IS NOT NULL
and pep != -1; 		-- developers would normally post a message with atleast a pep number -- those who dont may be just community members

-- create table without admin
create table alldeveloperswithoutadmin
AS select DISTINCT(clusterBySenderFullName) from allmessages
where folder = 'C:\\datasets\\python-dev'
and clusterBySenderFullName IS NOT NULL
and clusterBySenderFullName <> 'guido van rossum'
and pep != -1; 
-- 
create table alldeveloperswithoutadminAndMartin
AS select DISTINCT(clusterBySenderFullName) from allmessages
where folder = 'C:\\datasets\\python-dev'
and clusterBySenderFullName IS NOT NULL
and clusterBySenderFullName <> 'guido van rossum'
and clusterBySenderFullName <> 'Martin von Loewis'
and pep != -1; 

-- QUERIES
-- ONLY DEVELOPERS -- USED FOR TOP COMMUNITY MEMBER ANALYSIS AND ADMIN REPLACEMENT PART 1 
-- (this is also part 1 of the admin replacement query, part 2 is below somewhere)
select distinct(messageid), clusterBySenderFullName, inReplyToUserUsingClusteredSender, date2, subject                                                                                                                       
from allmessages 
where (clusterBySenderFullName in (select clusterBySenderFullName from alldevelopers) OR inReplyToUserUsingClusteredSender in (select clusterBySenderFullName from alldevelopers))
	AND clusterBySenderFullName IS NOT NULL AND inReplyToUserUsingClusteredSender IS NOT NULL
	-- AND folder = 'C:\\datasets\\python-dev'   --DO WE WANT TO INCLUDE TOP DEVELOPERS/DECISION MAKERS DISCUSSION IN DEV OR ALL LISTS
INTO OUTFILE 'C:\\scripts\\SNA_AllDevelopersOnly__alllists_withoutNulls_withAdmin__04-06-2017.txt';
-- this is just a sanity check to see if it matters if these communications messages ahev pep numbers or not
select distinct(messageid), clusterBySenderFullName, inReplyToUserUsingClusteredSender, date2, subject                                                                                                                       
from allmessages 
where (clusterBySenderFullName in (select clusterBySenderFullName from alldevelopers) OR inReplyToUserUsingClusteredSender in (select clusterBySenderFullName from alldevelopers))
	AND clusterBySenderFullName IS NOT NULL AND inReplyToUserUsingClusteredSender IS NOT NULL
	-- AND folder = 'C:\\datasets\\python-dev'	--DO WE WANT TO INCLUDE TOP DEVELOPERS/DECISION MAKERS DISCUSSION IN DEV OR ALL LISTS
	AND pep != -1
INTO OUTFILE 'C:\\scripts\\SQLResults\\SocialNetworkAnalysis\\Analysis\\TopCommunityMembers\\SNA_AllDevelopersOnly__PythonDevOnly_withoutNulls_withAdmin_MessagesWithPEPNumbersOnly_28-06-2017.txt';

	-- tHE SAME ABOVE developers CONTRIBUTION in other lists
	select distinct(messageid), clusterBySenderFullName, inReplyToUserUsingClusteredSender, date2, subject                                                                                                                       
	from allmessages 
	where (clusterBySenderFullName in (select clusterBySenderFullName from alldevelopers) OR inReplyToUserUsingClusteredSender in (select clusterBySenderFullName from alldevelopers))
	-- AND folder = 'C:\\datasets\\python-dev'
	INTO OUTFILE 'C:\\scripts\\SQLResults\\SocialNetworkAnalysis\\Analysis\\TopCommunityMembers\\socialNetworkAllDevelopers__alldevelopersInOtherLists_04-06-2017.txt';

-- Admin replacement, without admin (part 2 of admin replacement)
select distinct(messageid), clusterBySenderFullName, inReplyToUserUsingClusteredSender, date2, subject                                                                                                                       
from allmessages 
where (  
			(clusterBySenderFullName in (select clusterBySenderFullName from alldeveloperswithoutadmin ) 
				AND inReplyToUserUsingClusteredSender <> 'guido van rossum') 
		OR 
			(inReplyToUserUsingClusteredSender in (select clusterBySenderFullName from alldeveloperswithoutadmin) 
				AND clusterBySenderFullName <> 'guido van rossum')  			
		)
		AND clusterBySenderFullName IS NOT NULL AND inReplyToUserUsingClusteredSender IS NOT NULL
--		AND folder = 'C:\\datasets\\python-dev'   --DO WE WANT TO INCLUDE TOP DEVELOPERS/DECISION MAKERS DISCUSSION IN DEV OR ALL LISTS
INTO OUTFILE 'C:\\scripts\\SNA_AllDevelopersOnly__ALLLISTS_withoutNulls_withoutAdmin_04-06-2017.txt';

-- Admin replacement, without admin and Martin (part 2 of admin replacement)
select distinct(messageid), clusterBySenderFullName, inReplyToUserUsingClusteredSender, date2, subject                                                                                                                       
from allmessages 
where (  																														-- Martin von Loewis
			(clusterBySenderFullName in (select clusterBySenderFullName from alldeveloperswithoutadminandmartin ) 
				AND inReplyToUserUsingClusteredSender <> 'guido van rossum' AND inReplyToUserUsingClusteredSender <> 'Martin von Loewis') 
		OR 
			(inReplyToUserUsingClusteredSender in (select clusterBySenderFullName from alldeveloperswithoutadminandmartin) 
				AND clusterBySenderFullName <> 'guido van rossum' AND clusterBySenderFullName <> 'Martin von Loewis')  			
		)
		AND clusterBySenderFullName IS NOT NULL AND inReplyToUserUsingClusteredSender IS NOT NULL
--		AND folder = 'C:\\datasets\\python-dev'	--DO WE WANT TO INCLUDE TOP DEVELOPERS/DECISION MAKERS DISCUSSION IN DEV OR ALL LISTS
INTO OUTFILE 'C:\\scripts\\SNA_AllDevelopersOnly__ALLLISTS_withoutNulls_withoutAdmin_withoutMartin_26-06-2017.txt';