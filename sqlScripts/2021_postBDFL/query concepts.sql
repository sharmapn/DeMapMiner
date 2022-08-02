-- new sterring council model
-- DESC allmessages
SELECT a.proposal, a.messageid, a.sentence, a.lastdir,u1.date2, u1.author  
FROM allsentences a 
INNER JOIN allmessages u1 ON (a.messageid = u1.messageid)
WHERE a.messageid > 10000000 AND DATE2 > '11-07-2018' AND pep > 572
AND (sentence LIKE '%objection%' OR sentence LIKE '%consensus%')


SELECT m.*, u1.name, u2.name
FROM maintable m 
INNER JOIN users u1 ON (m.userid1 = u1.userid)