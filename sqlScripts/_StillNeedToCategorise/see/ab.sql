select id,sendername, emailaddress, senderFirstName, senderLastName,totalMessageCount 
from distinctdevsenders 
WHERE clustered IS NULL 
order by totalMessageCount 
limit 20