SELECT COUNT(*) FROM allsentences
SELECT DISTINCT proposal FROM allsentences
DESC allsentences

SELECT * from allsentences where  LOWER(sentence) LIKE '%accept%';

select messageID, proposal,  sentence, authorrole, msgsubject 
from allsentences 
WHERE 
proposal IN (289,308,313,318,336,340,345,376,388,389,391,414,428,435,441,450,465,484,488,498,485,471,505,3003,3103,3131,3144)
AND 
LOWER(sentence) LIKE '%based%' and LOWER(sentence) LIKE '%on%'  and LOWER(sentence) LIKE '%pep%'  

SELECT * FROM initialsentences
-- WHERE proposal IN (289,308,313,318,336,340,345,376,388,389,391,414,428,435,441,450,465,484,488,498,485,471,505,3003,3103,3131,3144)

SELECT pep, label, subject, relation, ns FROM initialsentences
WHERE length(subject) > 0 

