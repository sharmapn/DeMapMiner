select distinct(label) from results; 

keywordmatching;

select * from results where reasontermsinnextsentence <> '';

delete email from keywordmatching;

delete from results where pep >= 3131;

 select distinct(pep) from results;
 
 select distinct(pep) from pepdetails order by pep desc limit 10;