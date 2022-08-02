
-- main last query for datediff range
select  datediff,count(*) as counter 
from datedifftally
-- where containsreason = 1 
group by datediff 
order by datediff ASC
--, counter DESC

-- feb 2020
SELECT id, DATEDIFF 
-- ,count(*) as counter
from datedifftally
-- where toconsiderwhilereportingresults = 1 
group by datediff 
order by datediff asc, id DESC

-- UPDATE trainingdata SET DATEDIFF = NULL;

DESCRIBE trainingdata

select
  concat(21 * round(datediff / 21), '-', 21 * round(datediff / 21) + 20) as `range`,
  count(*) as `number of users`
from datedifftally
-- where containsreason = 1 
group by 1
order by datediff;


select t.range AS 'RANGE', count(*) AS 'counter'
from (  
	  select case    
	    when datediff between 0 and 20 then '0-20'  
	    when datediff between 21 and 41 then ' 21-41'  
	    when datediff between 42 and 62 then ' 42-62'  
	    when datediff between 63 and 83 then ' 63-83'  
	    when datediff between 84 and 104 then ' 84-104'  
	    when datediff between 105 and 135 then ' 105-135'  
	    else '136-156'   
	   end  case  
	  from datedifftally
	  -- where containsreason = 1 
  ) t  
group by t.datediff


0

select 
case
when datediff between 0 and 20 then ' 0-20'
when datediff BETWEEN 20 AND 40 then ' 21-41'
when datediff BETWEEN 40 AND 60 then ' 42-62'
when datediff BETWEEN 60 AND 80 then ' 63-83'
when datediff BETWEEN 80 AND 100 then ' 84-104'
when datediff BETWEEN 100 AND 120 then ' 105-135'
else '136-156'
end; as 'test',
count(*) as 'number of users'
from new_table
group by test

DESCRIBE autoextractedreasoncandidatesentences

SELECT * FROM autoextractedreasoncandidatesentences
LIMIT 5

select round(datediff / 10), count(*) 
from autoextractedreasoncandidatesentences 
where datediff < 40 
group by round(datediff / 10)
union
select '40+', count(*) 
from autoextractedreasoncandidatesentences 
where datediff >= 40;

SELECT proposal, messageid, DATEDIFF, sentence, containsreason 
FROM autoextractedreasoncandidatesentences_main
where containsreason = 1 
group BY sentence



SELECT proposal, messageid, DATEDIFF, sentence, containsreason  
FROM autoextractedreasoncandidatesentences
where containsreason = 1 
group BY sentence

