INSERT INTO manualreasonextraction (proposal, messageid, author,dateTimeStamp)
select distinct a.PEP,  a.messageid, a.authorsrole, a.dateTimeStamp 
from allmessages as a 
where a.PEP = 201
AND a.messageid NOT IN (select distinct b.messageid 
                 from manualreasonextraction as b 
                 where b.proposal = 201);
                 
-- DELETE from manualreasonextraction where proposal = 201  and termsMatched IS NULL
-- SELECT TO SEE IF NUMBER ROWS IS Correct

DELETE from manualreasonextraction where termsMatched IS NULL;
CALL `insertEmptyReasosnRows`()