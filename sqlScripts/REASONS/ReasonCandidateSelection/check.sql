select * from trainingdata 
where state = 'accepted' or state = 'rejected'
order by pep, state

select * from pepstates_danieldata_datetimestamp 
where state = 'accepted' 
order by pep

select distinct proposal from autoextractedreasoncandidatesentences
select * from pepstates_danieldata_datetimestamp
SELECT DISTINCT pep,email from pepstates_danieldata_datetimestamp where state = 'accepted' order by pep asc
SELECT DISTINCT pep,email from pepstates_danieldata_datetimestamp where email like '%accepted%' order by pep asc