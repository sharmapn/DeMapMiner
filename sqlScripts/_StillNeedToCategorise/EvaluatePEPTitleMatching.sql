select email, pep t, subject, (select concat (title,'-' ,author) from pepdetails where pep = t limit 1) , author
-- select * 
from allmessages_peptitleonly
order by t, dateTimeStamp

-- select author from allpeps where subject like 