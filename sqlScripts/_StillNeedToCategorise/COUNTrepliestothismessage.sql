select folder, countRepliesToThisMessage, count(countRepliesToThisMessage) 
from allmessages
group by folder, countRepliesToThisMessage
