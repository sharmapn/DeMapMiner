update allmessages set fromLine = SUBSTRING_INDEX(email,'\n',1)
-- HAve to drop from field as not used
select allmessages.from 
from allmessages 
where allmessages.from IS NOT NULL
LIMIT 20

-- CORRECTED
 select email, SUBSTRING_INDEX(email,'Date:',1)
 from allmessages
 limit 5

-- SELECT POSITION("From:" IN email)
-- from allmessages
-- LIMIT 5; 

select substr(SUBSTRING_INDEX(email,'Date:',1), POSITION("From:" IN email))  
from allmessages
LIMIT 5; 

-- main
update allmessages set fromLine = substr(SUBSTRING_INDEX(email,'Date:',1), POSITION("From:" IN email))  

select fromline from allmessages
limit 50

-- senders email address
alter table allmessages add column senderemail tinytext; 