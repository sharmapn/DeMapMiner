select distinct pep from pepstates_danieldata_datetimestamp

-- SELCT ALL THOSE WHICH ARE STILL ACTIVE OR DRAFT
SELECT * FROM pepstates_danieldata_datetimestamp 
WHERE PEP IN   -- SELECT all PEP Details
(
	select DISTINCT pep from pepstates_danieldata_datetimestamp 
	where (email LIKE '%REJECTED%' 
	OR email LIKE '%ACCEPTED%' 	OR email LIKE '%FINAL%' 			OR email LIKE '%CLOSEED%' 
	OR email LIKE '%WITHDRAWN%' 	OR email LIKE '%PROVISIONAL%' 	OR email LIKE '%PENDING%' 
	OR email LIKE '%POSTPONED%' 	OR email LIKE '%REPLACED%'		   OR email LIKE '%DEFERRED%'
	OR email LIKE '%INCOMPLETE%'  OR email LIKE '%SUPER%' )
		AND pep NOT IN 
			(select distinct pep from pepstates_danieldata_datetimestamp where email LIKE '%ACTIVE%' OR email LIKE '%DRAFT%' )
)
ORDER BY PEP ASC;

-- First attempt
SELECT * FROM pepstates_danieldata_datetimestamp 
WHERE PEP IN   -- SELECT all PEP Details
(
	 select distinct pep from pepstates_danieldata_datetimestamp where email LIKE '%ACTIVE' OR email LIKE '%DRAFT' 
	 	AND pep NOT IN 
			(select DISTINCT pep from pepstates_danieldata_datetimestamp 
				where email LIKE '%REJECTED' 
				OR email LIKE '%ACCEPTED' 	  OR email LIKE '%FINAL' 			OR email LIKE '%CLOSEED' 
				OR email LIKE '%WITHDRAWN'   OR email LIKE '%PROVISIONAL' 	OR email LIKE '%PENDING' 
				OR email LIKE '%POSTPONED'   OR email LIKE '%REPLACED'		OR email LIKE '%DEFERRED'
				OR email LIKE '%INCOMPLETE'  OR email LIKE '%SUPER') ORDER BY pep ASC
)
ORDER BY PEP ASC;
-- second attempt
create view allotherstates as
select distinct pep from pepstates_danieldata_datetimestamp where email LIKE '%ACTIVE' OR email LIKE '%DRAFT';
create view firststates as
select DISTINCT pep from pepstates_danieldata_datetimestamp 
				where email LIKE '%REJECTED' 
				OR email LIKE '%ACCEPTED' 	  OR email LIKE '%FINAL' 			OR email LIKE '%CLOSEED' 
				OR email LIKE '%WITHDRAWN'   OR email LIKE '%PROVISIONAL' 	OR email LIKE '%PENDING' 
				OR email LIKE '%POSTPONED'   OR email LIKE '%REPLACED'		OR email LIKE '%DEFERRED'
				OR email LIKE '%INCOMPLETE'  OR email LIKE '%SUPER';
-- second attempt part 2
create view unfinished as
SELECT PEP FROM allotherstates
    where pep not in 
(SELECT PEP FROM firststates) 
order by pep

-- returns 73, i thinka  lot of peps states have not been updated in git
SELECT distinct pep, email 
FROM pepstates_danieldata_datetimestamp 
WHERE PEP IN (SELECT pep FROM unfinished)
order by pep asc

select * from allmessages where pep = 510 and email like '%Accepted%'
select * FROM pepstates_danieldata_datetimestamp 

-- PART B NOW TO GET REAONS WE HAVE JUST EXTRATED USING STATE ANALYSIS
select * from results_postprocessed
-- returns 148 rows
select pep, messageid, label, subject, relation, object, currentsentence, clausie from results_postprocessed
where (clausie like '%consensus%' or clausie like '%majority%' or clausie like '%support%')
-- there would be repeated sentences from teh above query so we wont have reasons for all those pepes returned
-- therefore we only slect the distinct sentences and select the first ones
select distinct currentsentence from results_postprocessed 
where (clausie like '%consensus%' or clausie like '%majority%' or clausie like '%support%')
order by currentsentence
-- returned 101 rows
-- this is best query returning first instance of sentence in two or more peps
select distinct pep from
(  -- by grouping we only select the best
	select  pep,currentsentence,clausie, subject, relation, object, 
	(	select replace(email,'Status : ','') 
		from pepstates_danieldata_datetimestamp 
		where pep = results_postprocessed.pep and (email like '%accepted%' or email like '%rejected%') LIMIT 1
	) 
	from results_postprocessed 
	where (clausie like '%consensus%' or clausie like '%majority%' or clausie like '%support%')
	group by currentsentence 
	order by pep asc
) as t

-- from above we select distinct peps


-- Now manual reason extraction
select * from trainingdata

select id,pep, state, causesentence,effectsentence, label
from trainingdata where state = 'accepted' or state = 'rejected'
order by pep asc

select distinct pep from
(
	select  pep,label, causesentence, causecategory from trainingdata 
	where (label = 'accepted' or label = 'rejected')
	group by causesentence 
	order by pep asc
) as t

-- in training data table we will store the manually extracted reasons and also the reasons extracted vio substate extraction
-- update trainingdata set fromsubstateextraction = 0;
update trainingdata set label = replace(label,'Community',''); -- replace cummnity consensus to just consensus
update trainingdata set label = trim(label);
