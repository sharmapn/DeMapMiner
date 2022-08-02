update allpeps 
SET dateTime = NULL 
where messageID < 100000
-- select count(*) FROM allpeps where messageID < 100000