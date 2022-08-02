select distinct(pep) FROM pepstates_danieldata_datetimestamp order by pep desc
where pep = 3142;

select pep, folder, count(messageid) from allmessages 
group by pep, folder;

select folder, count(messageid) from allmessages where pep = 3130 group by folder