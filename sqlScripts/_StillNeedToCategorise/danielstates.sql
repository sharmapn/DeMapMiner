SELECT pep, email,dateTimeStamp 
from allmessages  -- allpeps 
-- WHERE pep = 160 
WHERE messageID BETWEEN 5000000 AND 6000000 
order by dateTimeStamp