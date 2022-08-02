-- change allmessages to allpepNumbers to do for developers only

ALTER TABLE allmessages
	ADD COLUMN `inReplyToUser` TEXT NULL AFTER `inReplyTo`;

update allmessages SET inReplyTo = TRIM(inReplyTo);
update allmessages SET emailMessageID = TRIM(emailMessageID);

UPDATE allmessages
SET inReplyToUser = NULL

UPDATE allmessages a1
JOIN allmessages a2 ON a2.emailMessageID = a1.inReplyTo
SET a1.inReplyToUser = a2.senderName
WHERE a1.inReplyTo IS NOT NULL

-- updated to cater for the new column  'inReplyToUserUsingClusteredSender'
UPDATE allmessages a1
JOIN allmessages a2 ON a2.emailMessageID = a1.inReplyTo
SET a1.inReplyToUserUsingClusteredSender = a2.clusterBySenderFullName
WHERE a1.inReplyTo IS NOT NULL


