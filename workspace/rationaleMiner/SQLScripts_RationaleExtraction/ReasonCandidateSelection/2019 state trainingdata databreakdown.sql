select state, count(*) from trainingdata
group by state

-- breakdown of ground truth
select state, count(distinct pep), count(*) from trainingdata
where (state = 'accepted' or state = 'rejected' or state = 'final') and consider =1
group by state

-- breakdown of state commit data
select  state, count(distinct pep) 
from pepstates_danieldata_datetimestamp
where state = 'accepted' or state = 'rejected' or state = 'final'
group by state
order by state

select  pep, state 
from pepstates_danieldata_datetimestamp
where state = 'accepted' or state = 'rejected' or state = 'final'
group by pep
order by pep

select state, pep,causesentence from trainingdata where state = 'final'
select distinct pep from trainingdata where state = 'final'