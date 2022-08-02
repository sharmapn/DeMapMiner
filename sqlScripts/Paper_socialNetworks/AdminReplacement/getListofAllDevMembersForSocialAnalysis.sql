select distinct(messageid), sendername, inreplytouser, datetimestamp, subject from
allmessages
where folder = 'c:\\datasets\\python-dev'
and sendername <> 'guido.van.rossum' 
and inReplyToUser <> 'guido.van.rossum'
INTO OUTFILE 'c:\\scripts\\socialNetworkAllDevMailingListWithoutAdmin_28-04-2017.txt';