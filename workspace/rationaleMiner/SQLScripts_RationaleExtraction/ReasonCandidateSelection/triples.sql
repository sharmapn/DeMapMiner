select pep, count(messageid) as cnt from extractedrelations_clausie
group by pep

select * from extractedrelations_clausie where arg1 like '%pep%' and (relation like '%accept%' OR arg2 like '%accept%') limit 50

//FI
select * from extractedrelations_clausie where arg2 like '%accepted%' and relation like '%because%' limit 5
-- and arg2 like '%accept%' limit 5

select * from allmessages where messageid = 297528;