select senderName, count(DISTINCT(messageID)) c from allpeps
where messageID < 100000
group by senderName
order by c DESC