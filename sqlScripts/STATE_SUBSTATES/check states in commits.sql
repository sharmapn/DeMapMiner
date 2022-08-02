select PEP, DATETIMESTAMP, EMAIL from pepstates_danieldata_datetimestamp
where pep IN (select distinct(pep) from pepstates_danieldata_datetimestamp
where state like '%complete%')
ORDER BY PEP, DATETIMESTAMP 