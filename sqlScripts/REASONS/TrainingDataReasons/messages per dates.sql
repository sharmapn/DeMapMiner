SELECT pep,  date2,datetimestamp, email,authorsRole from allmessages where pep = 479
and DATEDIFF('2015-01-19',date2) > 14 
and authorsRole NOT LIKE '%other%' 
order by datetimestamp desc;