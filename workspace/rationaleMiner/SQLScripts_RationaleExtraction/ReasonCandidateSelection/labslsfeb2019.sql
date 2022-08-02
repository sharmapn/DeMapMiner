select * from results
where label like '%consensus%' 

select proposal, messageid from autoextractedreasoncandidatemessages 
where proposal = 308 and messageid < 6000000
order by messageid desc

select count(distinct(ignoredterm)) from topicmodellingignoredterms