-- select senderName 
-- from distinctauthorsallmessages 
-- where senderName like 'gui%' AND senderName IS NOT NULL Order By SenderName ASC

SELECT pep, COUNT(DISTINCT(messageID)) from allmessages 
WHERE senderName = 'guido at python.org'
-- and pep != -1
Group by pep