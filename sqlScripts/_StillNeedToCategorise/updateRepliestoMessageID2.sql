-- update all_distinctmessages
-- set countRepliesToThisMessage = (SELECT COUNT(messageID) as c  FROM all_distinctmessages WHERE inReplyTo = emailMessageID) 

UPDATE all_distinctmessages AS a, (SELECT COUNT(messageID) as ct FROM all_distinctmessages WHERE inReplyTo = emailMessageID) AS p
SET a.countRepliesToThisMessage = p.ct

-- UPDATE skills AS s, (SELECT id  FROM skills WHERE type = 'Programming') AS p
-- SET s.type = 'Development' 
-- WHERE s.id = p.id;