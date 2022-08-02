SELECT t1.senderName AS lev1, t2.senderName as lev2, t3.senderName as lev3, t4.senderName as lev4
FROM allmessages AS t1
LEFT JOIN allmessages AS t2 ON t2.inReplyTo = t1.emailMessageID
LEFT JOIN allmessages AS t3 ON t3.inReplyTo = t2.emailMessageID
LEFT JOIN allmessages AS t4 ON t4.inReplyTo = t3.emailMessageID
WHERE t1.inReplyTo IS NULL
AND YEAR(t1.date2) = 2017 AND MONTH(t1.date2) = 1 ;