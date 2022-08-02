SELECT pepType, COUNT(DISTINCT(pep)) 
from allmessages 
WHERE senderName = 'guido.van.rossum' AND PEP != -1
group by pepType;