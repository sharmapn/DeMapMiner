select distinct(senderFullName), senderemail, fromLine, email from  allmessages
where senderemail like '%@(none%'
limit 50;