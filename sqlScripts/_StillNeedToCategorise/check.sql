SELECT pepType, COUNT(DISTINCT(pep)) 
from allmessages 
WHERE senderName = 'a.cavallo' 
group by pepType