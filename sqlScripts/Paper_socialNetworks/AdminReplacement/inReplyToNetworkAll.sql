select sendername, inReplyToUser
from allmessages 
where sendername in (select distinct(sendername) from alldevelopers) 
OR inReplyToUser in (select distinct(sendername) from alldevelopers)
order by sendername
INTO OUTFILE 'c:\\scripts\\socialNetworkAllDevelopers_23-04-2017.txt';
-- limit 10