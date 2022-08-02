SELECT bip, COUNT(messageid)  AS cnt
FROM allmessages
GROUP BY bip
ORDER BY cnt desc