SELECT COUNT(DISTINCT(messageID)) c, count(countRepliesToThisMessage) as d, senderName 
from allmessages WHERE inReplyTo IS NULL
group by sendername
order by c desc