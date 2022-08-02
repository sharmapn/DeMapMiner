-- select distinct(sendername) from allmessages where sendername like 'g%'
create table martin
select distinct(messageid), sendername , SUBSTRING_INDEX(email,'\n',1)
from allmessages 
-- where email like 'From martin%'
where email like 'From martin%'
-- and sendername = 'martin.von.loewis'

-- set the above to 'martin.sjögren'