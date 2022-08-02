SELECT emailMessageID as m,messageID, date2, senderName, subject, folder,
(SELECT count(emailMessageID) from all_distinctmessages WHERE inReplyTo = m)
from all_distinctmessages 
WHERE YEAR(date2) = 2016 
AND MONTH(date2) = 1 
AND inReplyTo IS NULL 
-- and subject like '%PEP 511%'
order by dateTimeStamp