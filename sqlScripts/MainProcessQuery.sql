select * from results order by datetimestamp 

select * from ( 
	SELECT date2,datetimestamp, clusterBySenderFullName as author, analysewords, statusFrom, statusTo, statusChanged,  messageID, subject,folder,file, required, date2 as date3,  
	inReplyTo,IdentifierCount from allmessages WHERE pep = 303 
	UNION  
	SELECT date2,datetimestamp, author, analysewords, statusFrom, statusTo, statusChanged,  messageID, subject,folder,file, required, date2 as date3,  
	inReplyTo,IdentifierCount from pepstates_danieldata_datetimestamp WHERE pep = 303 
) a order by date2 asc;