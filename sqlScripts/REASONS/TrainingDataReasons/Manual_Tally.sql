select  label, causecategory, causesubcategory,count(pep) as Total from trainingdata
group by label,causecategory, causesubcategory 

update trainingdata set label    = NULL where label = '';
update trainingdata set causecategory    = NULL where causecategory = '';
update trainingdata set causesubcategory = NULL where causesubcategory = '';

select * from trainingdata where label like 'With%';
update trainingdata set label = 'Withdrawn' where label like 'With%';
select * from trainingdata where label like 'Defer%';
update trainingdata set label = 'Deferred' where label like 'Defer%';

select count(distinct(pep)) from trainingdata
-- 333
select pep, label,causecategory, causesubcategory, causesentence,effectsentence 
from trainingdata order by label, causecategory,causesubcategory, pep