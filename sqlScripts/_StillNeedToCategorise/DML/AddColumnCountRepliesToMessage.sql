-- ALTER TABLE `all_distinctmessages`
--    ADD COLUMN `countRepliesToThisMessage` INT NULL DEFAULT NULL;

update all_distinctmessages AS a, (SELECT count(emailMessageID) as j from all_distinctmessages as c WHERE inReplyTo = emailMessageID) AS b
set a.countRepliesToThisMessage = b.j
where a.emailMessageId = c.emailMessageID;
  
-- UPDATE skills AS s, (SELECT id  FROM skills WHERE type = 'Programming') AS p
-- SET s.type = 'Development' 
-- WHERE s.id = p.id;