
INSERT INTO clustersdevsenders (id, emailaddress, sendername,totalMessageCount,senderFirstName, senderLastName)
SELECT id, emailaddress, sendername,totalMessageCount,senderFirstName, senderLastName
FROM   distinctdevsenders
WHERE  id IN (4,23,93,138)