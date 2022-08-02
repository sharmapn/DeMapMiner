update all_distinctmessages s
set s.countRepliesToThisMessage = (SELECT COUNT(messageID) FROM all_distinctmessages_2 WHERE inReplyTo = s.emailMessageID)
-- where s.messageID = p.messageID



-- UPDATE skills AS s, (SELECT id  FROM skills WHERE type = 'Programming') AS p
-- SET s.type = 'Development' 
-- WHERE s.id = p.id;