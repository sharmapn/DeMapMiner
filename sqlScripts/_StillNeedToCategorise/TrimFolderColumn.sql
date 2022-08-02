UPDATE allmessages set emailMessageID = TRIM(emailMessageID);
UPDATE allmessages set messageID = TRIM(messageID);
select count(DISTINCT(messageID)),count(DISTINCT(emailMessageID)), count(*) from allmessages