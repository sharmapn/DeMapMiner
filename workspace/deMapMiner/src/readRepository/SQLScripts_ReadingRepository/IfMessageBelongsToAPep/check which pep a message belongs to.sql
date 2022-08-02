
-- get how many different peps mentioned in that message by seeing how many peps have thuis message assigned to them
select count(messageID) from allmessages where messageID = 1345151;

-- for all messages in that thread, check whih pep number is prominent


-- get all parents and childs message of this message
-- see which peps numbers are in parent and which ones in child
select *, emailMessageID, inReplyTo from allmessages where messageID = 138933;

-- get parents
select pep, email from allmessages where emailMessageID = (select inReplyTo from allmessages where messageID = 138933);
-- get childs
select pep, email from allmessages where inReplyTo = (select emailMessageID from allmessages where messageID = 138933);

-- once you get these check which pep numbers are mentioned and then decide 
-- example message of wrong pep for label capture
select * from allmessages where pep = 308 and folder ='C:\\datasets\\python-dev' and email like '%tweaked some text%'

SELECT senderName,emailMessageID, inReplyTo,
(SELECT senderName from allmessages where m.inReplyTo = emailMessageID) 
from allmessages m
WHERE inReplyTo IS NOT NULL
LIMIT 5