-- while reading in data, analysewords is set to null, not sure why, 
-- so this scrtip[t will populate the column with email column...
select count(*) 
from allmessages 
where folder = 'C:\\datasets\\python-checkins' and (analysewords IS NULL OR analysewords = '');

update allmessages
SET analysewords = email
where folder = 'C:\\datasets\\python-checkins' and (analysewords IS NULL OR analysewords = '');
