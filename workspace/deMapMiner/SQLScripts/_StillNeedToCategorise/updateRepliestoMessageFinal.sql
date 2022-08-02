-- run below in command prompt
-- ALTER TABLE alldeveloperposts add column countRepliesToThisMessage INTEGER; 
-- ALTER TABLE allmessages add column countRepliesToThisMessage INTEGER; 

UPDATE alldeveloperposts 
AS a, (SELECT COUNT(messageID) as ct, emailMessageID FROM alldeveloperposts WHERE inReplyTo = a.emailMessageID) AS p
SET a.countRepliesToThisMessage = p.ct;

-- LIMIT 2

-- UPDATE skills AS s, (SELECT id  FROM skills WHERE type = 'Programming') AS p
-- SET s.type = 'Development' 
-- WHERE s.id = p.id;