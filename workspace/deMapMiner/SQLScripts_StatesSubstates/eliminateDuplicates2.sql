select pep, count(*) as states from pepstates_danieldata 
group by pep
-- where pep = 308
order by pep  -- datetimestamp
-- analysis
select * from pepstates_danieldata where pep = 257
order by datetimestamp

select * from pepstates_danieldata_datetimestamp where pep = 308
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



-- main query to delete ..have to make sure the first instance of each state is not deleted..workd fine now
DELETE t1 FROM pepstates_danieldata_datetimestamp_newcopy t1
        INNER JOIN
    pepstates_danieldata_datetimestamp_newcopy t2 
WHERE
    t1.id <> t2.id AND t1.email = t2.email
	 and t1.pep= t2.pep and t1.dateTimeStamp > t2.dateTimeStamp;

-- check for duplicates after deleting ...find duplicates for each pep, can use in daneildata tableor danieldatatimestamp table
SELECT pep,email, 
COUNT(email) AS NumOccurrences
FROM pepstates_danieldata_datetimestamp_newcopy
GROUP BY pep,email
HAVING ( COUNT(email) > 1 )

-- now we left with some peps which will have duplicate states with the same timestamp
-- so we can try edleting as follows..delete the biggger id one..or smaller..doesnt matter which one
-- should deleet 104 rows for data we have today on dec 2017
DELETE t1 FROM pepstates_danieldata_datetimestamp_newcopy t1
        INNER JOIN
    pepstates_danieldata_datetimestamp_newcopy t2 
WHERE
    t1.id > t2.id AND t1.email = t2.email
	 and t1.pep= t2.pep and t1.dateTimeStamp = t2.dateTimeStamp  ;
	 
	 -- 56 rows deleted, all good now