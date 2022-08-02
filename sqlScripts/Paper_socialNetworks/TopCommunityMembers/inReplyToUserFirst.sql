SELECT senderName,emailMessageID, inReplyTo,
(SELECT senderName from allmessages where m.inReplyTo = emailMessageID) 
from allmessages m
WHERE inReplyTo IS NOT NULL
LIMIT 5

ALTER TABLE `allmessages`
	ADD COLUMN `inReplyToUser` TEXT NULL AFTER `inReplyTo`;



select sendername, inreplyto, inreplytouser from allmessages limit 50