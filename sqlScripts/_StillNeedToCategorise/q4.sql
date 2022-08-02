SELECT pep, COUNT(DISTINCT(messageID)) as count 
from allmessages 
WHERE senderName = 'Aaron J Reichow' Group By pep