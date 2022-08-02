create table allDevelopers
AS select DISTINCT(clusterBySenderFullName) from allmessages
where folder = 'C:\\datasets\\python-dev'
and pep != -1; 		-- developers would normally post a message with atleast a pep number -- those who dont may be just community members

-- QUERIES
-- ONLY DEVELOPERS
select distinct(messageid), clusterBySenderFullName, inReplyToUserUsingClusteredSender, date2, subject                                                                                                                       
from allmessages 
where (clusterBySenderFullName in (select author from alldevelopers) OR inReplyToUserUsingClusteredSender in (select author from alldevelopers))
AND folder = 'C:\\datasets\\python-dev'
INTO OUTFILE 'C:\\scripts\\SQLResults\\SocialNetworkAnalysis\\Analysis\\TopCommunityMembers\\socialNetworkAllDevelopers__developersOnly_04-06-2017.txt';

-- All developers in other lists
select distinct(messageid), clusterBySenderFullName, inReplyToUserUsingClusteredSender, date2, subject                                                                                                                       
from allmessages 
where (clusterBySenderFullName in (select author from alldevelopers) OR inReplyToUserUsingClusteredSender in (select author from alldevelopers))
-- AND folder = 'C:\\datasets\\python-dev'
INTO OUTFILE 'C:\\scripts\\SQLResults\\SocialNetworkAnalysis\\Analysis\\TopCommunityMembers\\socialNetworkAllDevelopers__alldevelopersInOtherLists_04-06-2017.txt';