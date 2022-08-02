select author, authorrole from results
select distinct messageid from results
select distinct pep from results
select * from results where LENGTH(clausie) >0 ORDER BY DATETIMESTAMP ASC; -- _bckdec
select count(*) from results where isfirstparagraph = 1
SELECT date2,datetimestamp, author, analysewords, messageID, subject,folder,file, required, date2 as date3,  
inReplyTo,IdentifierCount from pepstates_danieldata_datetimestamp

SELECT * FROM LABELS where subject = 'pep' and verb = 'open';
idea = 'updated'

select * from autoextractedreasoncandidatesentences_dec where proposal = 279 and datediff > - 10 order by datediff asc;
select results_postprocessed_dec2018

select pep, date, timestamp,currentsentence,  clausie from results_postprocessed_dec2018 
where pep = 279 and
LENGTH(clausie) >0 ORDER BY pep, TIMESTAMP ASC;

-- select all distinct states
create table statesubstatelearning as
select distinct clausie from results_postprocessed_dec2018; 
-- have them as each column 
-- do ml to see if we can predict

select * from results where pep = 279;
select * from results_dec2018b where messageid > 5000000 order by clausie;
select * from pepstates_danieldata_datetimestamp  order by state asc
select state, count(*) from pepstates_danieldata_datetimestamp 
group by state order by state

select pep, count(state) as cntstates from pepstates_danieldata_datetimestamp
group by pep
select pep, count(messageid) as cnt from allmessages
where pep > -1
group by pep order by cnt desc

select * from results_postprocessed_dec2018b 
-- where messageid > 5000000 and messageid < 6000000 
order by label