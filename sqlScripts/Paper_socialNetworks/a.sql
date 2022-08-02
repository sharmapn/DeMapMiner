select distinct(pep) from allmessages -- allmessages -- 466
INNER JOIN pepdetails
USING (pep)
-- select distinct(pep) from allmessages

-- select distinct(pep) from pepdetails

select distinct(pep) from pepdetails-- allmessages -- 466
where pep NOT IN (select distinct(pep) from allmessages)