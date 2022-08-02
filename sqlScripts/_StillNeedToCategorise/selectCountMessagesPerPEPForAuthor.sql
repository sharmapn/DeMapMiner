-- SELECT pepType, COUNT(DISTINCT(pep)) 
select pep, count(messageid)
from allmessages 
WHERE senderName = 'a.cavallo' 
group by pep