-- First recursive query for reason extraction

-- Note try query near states/substate (<14 days of each state/substate) and where author is core developer/bdfl/pep author
--  have to update the extractedrelations_clausie table with author and date from allmessage stable using messageid

-- if you want to integrate paragrapgh or line number to limit the serach space, use those fields in this table

SET @term = '%accept%';  
select * from extractedrelations_clausie 
where arg1 like '%pep%' and (relation like @term  OR arg2 like @term ) limit 50

select * from extractedrelations_clausie 
where (
arg1 like '%pep%' and (relation like '%accept%' or arg2 like '%accept%')   -- either relation or arg2 has a state
-- and (relation like '%because%' or arg2 like '%because%') -- either relation or arg2 hasa reason identifier
and (relation like '%consensus%' or arg2 like '%consensus%') -- either relation or arg2 hasa reason
limit 50

select * from allmessages where pep = -1 and email like '%After a long discussion%'