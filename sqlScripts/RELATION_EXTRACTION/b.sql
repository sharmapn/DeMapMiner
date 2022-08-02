-- select max(messageid) from extractedrelations_clausie where pep = 42;
select * from allmessages where messageid = 8437;
select * from extractedrelations_clausie where messageid = 16011;
-- delete  from extractedrelations_clausie where pep = 308;
select * from extractedrelations_clausie where arg2 = ' ' limit 10;
select * from extractedrelations_clausie where pep = 308 limit 100;
select * from extractedrelations_clausie where sentence like '%http%' limit 10;
where pep = 308;