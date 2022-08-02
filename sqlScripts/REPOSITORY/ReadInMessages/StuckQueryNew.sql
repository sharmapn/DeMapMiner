select max(messageid) from allmessages
select folder, file from allmessages where messageid = 1545116
select min(messageid) from allmessages where folder = 'C:\\datasets\\python-lists' and file = '2014-January.txt'
DELETE FROM allmessages where messageID >= 1543295;

select count(messageid) as cnt, folder from allmessages
group by folder
select count(messageid) as cnt from allmessages;

select date2, datetimestamp from allmessages where folder like '%author%';

select folder, file,count(messageid)
from allmessages
group by folder, file;

select distinct(folder) from allmessages;