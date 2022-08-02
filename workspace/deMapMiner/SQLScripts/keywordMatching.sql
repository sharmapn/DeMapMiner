select * from pepdetails where label like '%rant%'
delete from keywordmatching where label = 'rant';

CREATE UNIQUE INDEX keywords ON keywordmatching (label(15));
keywordmatching
CREATE INDEX keywords_pep ON keywordmatching (pep);

select * from keywordMatching order by pep asc
delete * from keywordMatching 
select label, COUNT(messageID) from keywordMatching group by label;

select * from allmessages where subject like '%import-or%'
-- messageid = 168994
select * from allmessages_ideas 
-- where messageid = 521
where email like '%rejected%' or email like '%support%' or email like '%majority%';

select messageID, processedSubject from ideatitles_matching where processedSubject = ""

select pep, messageid, label, ps, currentsentence, ns, message_subject, author
from keywordmatching 
where pep > 4 and label = 'rough consensus'
-- limit 100
select * from ideatitles_matching order by processedSubject desc
select * from allmessages where messageID = 27214 
-- 44988

select distinct label from keywordmatching29peps order by label
select label, count(*) from keywordmatching29peps group by label

SELECT 1+1;
SELECT DISTINCT label  FROM  keywordmatching29peps ;

select pep, folder,count(*) as cnt from allmessages where pep= -1  group by pep,folder order by cnt desc; -- and folder <> '%list%'