update all_distinctmessages
set countRepliesToThisMessage = (SELECT COUNT(messageID) FROM (select * FROM all_distinctmessages) as dm WHERE inReplyTo = dm.emailMessageID)
-- where s.messageID = p.messageID