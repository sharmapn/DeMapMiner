-- all communications in python lists
-- (this is also part 1 of the admin replacement query, part 2 is below somewhere)
select distinct(messageid), clusterBySenderFullName, inReplyToUserUsingClusteredSender, date2, subject                                                                                                                       
from allmessages_lists
-- where
-- we will just select from python lists so dont need the clause below  
-- (clusterBySenderFullName in (select clusterBySenderFullName from alldevelopers) OR inReplyToUserUsingClusteredSender in (select clusterBySenderFullName from alldevelopers))
-- we want tp include the following cases/users
--	AND clusterBySenderFullName IS NOT NULL AND inReplyToUserUsingClusteredSender IS NOT NULL
--	folder = 'C:\\datasets\\python-lists'
INTO OUTFILE 'C:\\scripts\\SQLResults\\SocialNetworkAnalysis\\Analysis\\SocialRoles\\SNA_AllMemebers__PythonListOnly_withNulls_withAdmin__14-06-2017.txt';