select distinct(messageid), sendername, inreplytouser, datetimestamp, subject from
allmessages
where folder = 'c:\\datasets\\python-dev'
and sendername <> 'guido.van.rossum' 
and inReplyToUser <> 'guido.van.rossum'
and inReplyToUser IS NOT NULL -- choose when not needed..not needed when loking for admin replacement
INTO OUTFILE 'c:\\scripts\\socialNetworkAllDevMailingListWithoutNULLsAndAdmin_28-04-2017.txt';