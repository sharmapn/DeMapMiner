-- feb 2019
-- current tabgle 'pepstates_danieldata_datetimestamp' is now the changed old dataset

select * from pepstates_danieldata_datetimestamp order by pep, datetimestamp
select distinct pep from pepstates_danieldata_datetimestamp

select pep, count(email) from pepstates_danieldata_datetimestamp
where length(email) >0
group by pep
order by pep asc

-- returns 18 peps which dont have entry in the new dataset
-- 1 pep '487' which is new in the new dataset
-- so for timebeing we use old dataset
SELECT distinct pep
FROM pepstates_danieldata_datetimestamp
LEFT JOIN pepstates_danieldata_datetimestamp_new2019 USING (pep)
WHERE pepstates_danieldata_datetimestamp_new2019.pep IS NULL;
