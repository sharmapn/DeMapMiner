select * from allmessages_ideas
where messageid = 522

SELECT pep, titleinfirststate from pepstates_danieldata_wide where pep IS NOT NULL

SELECT pep, titleinfirststate, title from pepstates_danieldata_wide 

select levenshtein_distance('while','dev while i was') 
from labels
limit 1;

SELECT * FROM ALLMESSages where messageid = 293884;

select * from allmessages_dev
where subject like '%resolution%'



select * from allmessages_ideas where subject like (select * from pepdetails where length(title) < 12)

SELECT a.*
  FROM allmessages_ideas a
  JOIN pepdetails b ON a.subject LIKE CONCAT('%', b.name, '%')
  
  select * from allmessages_ideas where subject like 
  
  select count(*) from allmessages_dev where pep <> originalPEPNumber;

update allmessages_dev set pep = originalPEPNumber;
update allmessages_ideas set pep = originalPEPNumber;