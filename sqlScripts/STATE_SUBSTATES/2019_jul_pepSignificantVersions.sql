select distinct email, pep, date2 from pepstates_danieldata
where  (DATE2 BETWEEN '2012-01-30 14:15:55' AND '2019-09-29 10:15:55')
group by email

select distinct email, pep, date2 from pepstates_danieldata
where email like '%active%'
order by date2 desc

SELECT EMAIL, pep, max(date2)
from pepstates_danieldata_datetimestamp_new2019
where email IN (select distinct email from pepstates_danieldata_datetimestamp_new2019)
group by email


SELECT MAX(DATE2) FROM pepstates_danieldata_datetimestamp_new2019

SELECT * FROM pepstates_danieldata_datetimestamp