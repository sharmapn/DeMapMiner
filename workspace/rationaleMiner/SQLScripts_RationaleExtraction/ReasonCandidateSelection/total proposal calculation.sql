select state, count(pep) from trainingdata
group by state

-- only those proposals which have a manual reason sentence = 91 peps
select DISTINCT pep  from trainingdata -- state, causesentence
where state = 'accepted' 
and consider =1
and LENGTH(causesentence) >0
order by pep

-- 124 rows
select distinct pep from pepstates_danieldata_datetimestamp where state = 'accepted'

select * from trainingdata where pep = 201;