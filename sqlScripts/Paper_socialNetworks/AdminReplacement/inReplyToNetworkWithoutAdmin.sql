select sendername, inReplyToUser
from allmessages 
where sendername in (select distinct(sendername) from alldevelopers where sendername <> 'guido.van.rossum' and sendername <> 'gvanrossum') 
OR inReplyToUser in (select distinct(sendername) from alldevelopers where sendername <> 'guido.van.rossum' and sendername <> 'gvanrossum')
INTO OUTFILE 'c:\\scripts\\socialNetworkWithoutAdmin_23-04-2017.txt';
-- limit 10