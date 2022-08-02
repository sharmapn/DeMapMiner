
-- main query to trasnfer state data
UPDATE results set label = SUBSTRING( currentsentence,10)
WHERE messageid > 5000000 AND messageid < 6000000


SELECT SUBSTRING( currentsentence,10)
FROM results
WHERE messageid > 5000000 AND messageid < 6000000