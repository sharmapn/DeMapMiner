select pep,count(*) 
from extractedrelations_clausie
group by pep

select * from extractedrelations_clausie where pep = 308
delete from extractedrelations_clausie where pep = 308