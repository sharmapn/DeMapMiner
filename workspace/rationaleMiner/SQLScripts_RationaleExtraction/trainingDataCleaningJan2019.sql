select id, pep, state, causesentence, label from trainingdata
where (state = 'accepted' or state = 'rejected')
 and label like '%comm%'
order by pep asc

select distinct label from  trainingdata
order by label

select label, state, count(pep) as cnt
from trainingdata
where (state = 'accepted' or state = 'rejected')
-- and label like '%comm%'
group by label, state
order by label, state
-- cnt desc

-- import substate data into trainingdata, those that are not in trainingdata
select * from results_postprocessed 
where clausie ='accepted' or clausie ='rejected'

select * from trainingdata where manuallyExtracted = 0;
-- update trainingdata set manuallyExtracted = 1;
-- DELETE from trainingdata where manuallyExtracted = 0

-- MAIN INSERT STATEMENT feb 2019
insert into trainingdata (pep,messageid, causesentence,manuallyExtracted,state)
	select  pep,messageid,currentsentence,0,
	(	select replace(email,'Status : ','') 
		from pepstates_danieldata_datetimestamp 
		where pep = results_postprocessed.pep and (email like '%accepted%' or email like '%rejected%') LIMIT 1
	) 
	from results_postprocessed 
	where (clausie like '%consensus%' or clausie like '%majority%' or clausie like '%support%')
	group by currentsentence 
	order by pep asc
	-- limit 1
	
select id,pep, state, manuallyExtracted,label, causesentence,effectsentence,consider from trainingdata 	
order by pep asc, state, label
-- update trainingdata set consider = 1
select * from trainingdata where consider = 0

select * pepstates_danieldata_datetimestamp where pep = 211
select * from allsentences where proposal = 8 limit 5
select * from pepstates_danieldata_datetimestamp where pep = 211

select * from allmessages  where messageid = 25423

SELECT pep, state, causesentence from trainingdata where pep = 221 order by pep;

select * from pepstates_danieldata_datetimestamp where pep = 221

select * from autoextractedreasoncandidatesentences_dec
select distinct label from autoextractedreasoncandidatesentences_dec

