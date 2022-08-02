select pep, fromline, 
-- senderemail, senderemailprocessed, senderemailfirstsegment, 
author, senderfullname, senderfirstname, senderlastname, clusterbysenderfullname
from allmessages where pep = 308;