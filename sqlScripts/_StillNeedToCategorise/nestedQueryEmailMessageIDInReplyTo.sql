 SELECT t1.emailMessageID, 
           t1.date2,
           t1.subject,
           t1.senderName
FROM allmessages t1
WHERE YEAR(date2) = 2017
AND MONTH(date2) = 1
AND t1.emailMessageID = t1.inReplyTo