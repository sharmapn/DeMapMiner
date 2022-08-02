select type, count(*) FROM pepdetails group by type order by pep
select count(distinct(pep)) from pepdetails;
update pepdetails set type = trim(type);