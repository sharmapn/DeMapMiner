-- 2021 may sentiment analysis ..we START WITH pep 572

SELECT DISTINCT proposal FROM allsentences

SELECT * FROM allsentences -- LIMIT 100
SELECT * FROM allmessages
WHERE pep = 572 AND email LIKE '%decision%'