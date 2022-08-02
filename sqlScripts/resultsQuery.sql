select distinct state, subject from allmessages_subjectsonly where state = 'consensus'

select *  from allmessages_subjectsonly where state = 'consensus'

select * from allmessages where pep = 1; messageid = 1243411

select pep, messageid, date, label, currentsentence, clausie from results where label = 'accepted' order by pep asc
select * from results where clausie IS NOT NULL order by date -- 'accepted' order by pep asc

select distinct pep from results order by pep

select count(messageid) from allmessages where datetimestampstring IS NULL
-- 1405424 null, 79,235 were populated
update allmessages set datetimestampstring = NULL;
-- update allmessages set datetimestamp = NULL;
(SELECT distinct(pep) from pepdetails order by pep )
UNION
(SELECT -1 from DUAL )