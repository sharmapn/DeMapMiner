select * from results
-- drop table drafts;
create table drafts as
select *, '' as clausie from allmessages 
where -- subject like '%draft%'
-- or subject like '%rough draft%'
-- or subject like '%initial draft%'
-- subject like '%first draft%'
   subject like '%second draft%' or subject like '%2nd draft%'
or subject like '%third draft%'  or subject like '%3rd draft%'
or subject like '%fourth draft%' or subject like '%4th dracft%'
or subject like '%fifth draft%'  or subject like '%5th draft%'
or subject like '%round 2%' or subject like '%round two%'
or subject like '%round 3&' or subject like '%round three%'
or subject like '%round 4%' or subject like '%round four%'
or subject like '%round 5%' or subject like '%round five%'
or subject like '%round 6%' or subject like '%round six%'
or subject like '%round 7%' or subject like '%round seven%';

select * from drafts;

-- alter table drafts add column clausie(text);
update drafts set clausie = '2nd Draft'  where subject like '%second draft%' or subject like '%2nd draft%' or subject like '%round 2%' or subject like '%round two%';
update drafts set clausie = '3rd Draft'   where subject like '%third draft%'  or subject like '%3rd draft%' or subject like '%round 3' or subject like '%round three%';
update drafts set clausie = '4th Draft'  where subject like '%fourth draft%' or subject like '%4th draft%' or subject like '%round 4%' or subject like '%round four%';
update drafts set clausie = '5th Draft'   where subject like '%fifth draft%'  or subject like '%5th draft%' or subject like '%round 5%' or subject like '%round five%';
update drafts set clausie = '6th Draft'   where subject like '%sixth draft%'  or subject like '%6th draft%' or subject like '%round 6%' or subject like '%round six%';
update drafts set clausie = '7th Draft' where subject like '%seventh draft%' or subject like '%7th draft%' or subject like '%round 7%' or subject like '%round seven%';
-- main insert
insert into results_jan (pep,messageid, date, datetimestamp,subject, currentSentence,author,        clausie) 
						select pep,messageid, date2,datetimestamp,subject, subject,        senderfullname,clausie from drafts where length(clausie) > 0 and pep > 0; 
-- pep = 308 and messageid = 261760;

select * from results_jan where clausie like '%vot%' 
-- manually change results from teh above query, change all vot to official votes

select pep,messageid,email, subject from drafts
where pep > 0;


pep = 488 and email like '% informal poll %'

select distinct email from pepstates_danieldata_datetimestamp
 where email like '%prov%'

select * from pepstates_danieldata_datetimestamp where pep = 289 order by date2

select DISTINCT pep from pepstates_danieldata_datetimestamp 
where (email LIKE '%rejected%' 
OR email LIKE '%WITHDRAW%' 
OR email LIKE '%POSTPONED%' 
OR email LIKE '%INCOMPLETE%' 
OR email LIKE '%DEFERRED%')
	AND pep IN 
		(select pep from pepstates_danieldata_datetimestamp where email LIKE '%accepted%');