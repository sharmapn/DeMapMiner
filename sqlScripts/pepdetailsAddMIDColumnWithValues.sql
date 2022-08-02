select * from pepdetails;
update pepdetails set messageid = (6000000+pep);
update pepdetails set messageid = (6000000+pep);
select * from
	(SELECT pep,messageid,analysewords,date2, folder, file,msgNumInFile,email,subject,dateTimeStamp
	FROM allmessages WHERE pep=308 
	UNION 
	SELECT pep, messageID, pepsummary, null, null, null, null, null, null, null
	from pepdetails where pep = 308) t
order by dateTimeStamp asc