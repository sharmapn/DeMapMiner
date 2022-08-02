select * from distinctdevsenders
where emailaddress= 'torvalds@cc.Helsinki.FI';

SELECT senderFullName,senderfirstname, senderlastname, count(distinct(messageid)) 
from allmessages 
where senderEmail = 'torvalds@cc.Helsinki.FI' 
and folder = 'C:\\datasets\\python-dev';  
