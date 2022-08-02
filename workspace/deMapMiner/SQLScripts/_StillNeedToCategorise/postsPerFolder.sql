select folder, count(distinct(messageid)), min(date2), max(date2),min(datetimestamp), max(datetimestamp)
from allmessages
group by folder