select distinct(folder) from allmessages;

select PEP, MESSAGEid,date2, folder, file, msgNumInFile
from allmessages 
where folder LIKE '%python-dev%' 
and (file = '2012-May.txt') -- or file = '2006-December.txt')
order by messageid asc LIMIT 500;

-- messageCounterinFileTracker
alter table allmessages add column msgNumInFile INT;