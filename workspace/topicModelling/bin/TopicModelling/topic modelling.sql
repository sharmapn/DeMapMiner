--  topic  modelling
delete from allsentences
delete from allparagraphs
select * from allsentences
select * from allparagraphs

select * from allmessages limit 5
select * from allsentences where isenglishorcode = 0 limit 5
select  from allsentences limit 5

select distinct LOWER(senderfirstname) 
INTO OUTFILE 'C:\\DeMap_Miner\\workspaces\\DeMapMiner3\\mallet\\stoplists\\fname.txt'
from allmessages where pep > 0;


select distinct LOWER(senderlastname) 
INTO OUTFILE 'C:\\DeMap_Miner\\workspaces\\DeMapMiner3\\mallet\\stoplists\\lname.txt' 
from allmessages where pep > 0; 

select * from allsentences limit 10
-- update allsentences set lastdir = replace(lastdir,'C:\\datasets\\','');

select author, count(pep)
from allmessages where pep > 0
group by author

alter table allmessages add column lastdir text, add index (lastdirec);
-- ALTER TABLE mytable ADD INDEX (myarguments);
ALTER TABLE allmessages ADD INDEX lastdirectory (`lastdir`(15));
-- select folder, replace(folder,"C:\\OSSDRepositories\\JEPs\\","") as test from allmessages limit 5;
update allmessages set lastdir = replace(folder,"C:\\datasets\\","") 

SET @rank := 0;
select 
	*,
	@rank := @rank + 1 AS rank 
from topicmodel
-- where term = 'poll'
order by topicscommon desc, totalcount desc