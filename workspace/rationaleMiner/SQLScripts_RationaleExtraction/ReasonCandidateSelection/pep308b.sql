-- SET @term = '%accept%';    
select * from extractedrelations_clausie
where arg1 like  '%accept%' or relation like  '%accept%'  or arg2 like  '%accept%' 
order by pep;

select * from pepdetails