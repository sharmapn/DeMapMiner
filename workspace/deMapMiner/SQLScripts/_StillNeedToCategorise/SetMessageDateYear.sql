
-- alter table allmessages_bugs_list add dateYear integer;

update allmessages_bugs_list set dateYear = YEAR(date2);

-- keep changing to ideas, list, etc
select dateYear, count(distinct(sendername)) as pythonpatches
from allmessages_bugs_list
group by dateYear