select count(*) from allmessages 
where senderemail like '%hettinger%'
-- limit 5;

SELECT id,sendername, emailaddress, senderFirstName, senderLastName, cluster,
Levenshtein('rhettinger', lower(CONCAT(SUBSTRING(senderfirstname,1,1),senderLastName))) AS 'sendernameVs1stLetterFirstNameRestLName', 
'SENDERNAME VS FIRST LETTER F&L NAME MATCH' AS 'match' 
FROM distinctsenders 
HAVING sendernameVs1stLetterFirstNameRestLName <2;

SELECT id,sendername, emailaddress, senderFirstName, senderLastName, 
Levenshtein('rhettinger', lower(CONCAT(SUBSTRING(senderfirstname,1,1),senderLastName))) AS 'sendernameVs1stLetterFirstNameRestLName' 
FROM distinctsenders 
WHERE clusteredBySenderName IS NULL 
AND clusterBySenderName IS NULL 
AND sendername NOT IN (select senderfirstname from distinctsendersfirstname WHERE senderfirstname IS NOT NULL) 
HAVING sendernameVs1stLetterFirstNameRestLName <1;

select id,sendername, emailaddress,senderFirstName, senderLastName, senderEmailFirstSegment, totalMessageCount 
from distinctsenders 
WHERE clusteredBySenderName IS NULL AND clusterBySenderName IS NULL AND senderFirstName IS NULL AND senderLastName IS NULL 
order by totalMessageCount DESC LIMIT 1