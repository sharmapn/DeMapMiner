select * from pepdetails  where pep = '%%' order by pep 

select pep, author, authorCorrected, authorEmail from pepdetails where pep = 448;

select author, sendername, fromline, senderemail, senderemailprocessed, senderemailfirstsegment,senderfullname, senderfirstname, senderlastname, clusterbysenderfullname 
from allmessages where pep = 448 
-- and from like '%holger@merlinux.eu&g%'

-- match author and senderemail in allmessages with authorCorrected, authorEmail in pepdetails  

alter table allmessages 
	ADD COLUMN authorsrole MEDIUMTEXT;
	-- after update
select pep, author, authorsrole from allmessages where pep = 201 and authorsrole NOT LIKE '%other%' LIMIT 5000;

select pep, authorsrole, count(*) as numMsg from allmessages where pep = 201 group by pep, authorsrole;
select pep, authorsrole, count(*) as numMsg from allmessages group by pep, authorsrole;