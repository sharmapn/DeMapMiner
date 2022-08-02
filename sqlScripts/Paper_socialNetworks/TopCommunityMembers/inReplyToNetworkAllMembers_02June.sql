select clusterBySenderFullName, inReplyToUserUsingClusteredSender
from allmessages 
-- where sendername in (select distinct(sendername) from alldevelopers) 
-- OR inReplyToUser in (select distinct(sendername) from alldevelopers)
order by sendername
INTO OUTFILE 'c:\\scripts\\socialNetworkAllMembersAfterClustering_30-05-2017.txt';

-- limit 10