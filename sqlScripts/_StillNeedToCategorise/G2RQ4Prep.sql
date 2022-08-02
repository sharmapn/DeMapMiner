-- This selects the parent threads

SELECT date2, subject,folder, inReplyTo, emailMessageID
from allmessages
WHERE YEAR(date2) = 2017 AND MONTH(date2) = 1 
AND folder like '%python-dev%' 
AND inReplyTo IS NULL
ORDER BY date2
LIMIT 50