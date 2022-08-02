select * from labels where idea like '%consensus%' and tocheck=1

select pep, messageid, authorrole, label,subject, relation, object,currentsentence, clausie from results_postprocessed_dec2018b 
where clausie like '%consensus%' order by pep asc

select pep, messageid, date,messagesubject, label, subject, relation, object, currentsentence, clausie, linenumber from results

select * from labels  where  idea = 'consensus'
where subject is null
select * from labels where linenumber = 814
select * from results