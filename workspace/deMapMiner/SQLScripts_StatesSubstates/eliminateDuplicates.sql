select pep, count(*) as states from pepstates_danieldata 
group by pep
-- where pep = 308
order by pep  -- datetimestamp

select * from pepstates_danieldata where pep = 257
order by datetimestamp

select * from pepstates_danieldata_datetimestamp where pep = 0
order by datetimestamp

-- See count for each state
SELECT email, count(*) as NUM FROM pepstates_danieldata GROUP BY pep
-- TO FIND WHICH VALUES IN EACH PEP ARE DUPLICATES
SELECT pep,email, count(*) as NUM FROM pepstates_danieldata GROUP BY pep,email
HAVING NUM>1 
SELECT pep,email, count(*) as NUM FROM pepstates_danieldata_datetimestamp GROUP BY pep,email
HAVING NUM>1

select * from
(select distinct(pep) from pepstates_danieldata_datetimestamp
UNION 
select distinct(pep)  from pepstates_danieldata) t
order by t.pep

													-- CHANGE HERE	
SELECT pep,email, count(*) as NUM FROM pepstates_danieldata_datetimestamp where pep in (select distinct(pep) from pepstates_danieldata_datetimestamp) GROUP BY pep,email

SELECT pepstates_danieldata_datetimestamp.pep as p, pepstates_danieldata_datetimestamp.email as a, pepstates_danieldata.email as b
FROM  pepstates_danieldata
LEFT JOIN pepstates_danieldata_datetimestamp 
ON pepstates_danieldata.pep = pepstates_danieldata_datetimestamp.pep
order by p, a,b;