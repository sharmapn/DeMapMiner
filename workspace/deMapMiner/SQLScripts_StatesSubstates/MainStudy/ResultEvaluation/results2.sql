select * from results where pep = 308 
and clausie is not null 
order by date asc
SELECT date2, author, analysewords, statusFrom, statusTo, statusChanged,  messageID, subject,folder,file, required, date2, inReplyTo 
from pepstates_danieldata 
WHERE pep = 289 order by date2

select *
from (
	SELECT date2, clusterBySenderFullName, analysewords, statusFrom, statusTo, statusChanged,  messageID, subject,folder,file, required, inReplyTo 
	from allmessages 			  WHERE pep = 289
	UNION 
	SELECT date2, author,  						analysewords, statusFrom, statusTo, statusChanged,  messageID, subject,folder,file, required, inReplyTo 
	from pepstates_danieldata WHERE pep = 289 
) a
order by date2 asc
		
				