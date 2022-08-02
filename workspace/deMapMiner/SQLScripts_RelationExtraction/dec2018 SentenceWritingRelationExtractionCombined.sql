select pep, messageid, from extractedrelations_clausie
where (isfirstparagraph =1 OR islastparagraph =1) 
and (arg1 like '%pep%' or arg2 like '%pep%')

select * from trainingdata where pep = 279
-- hard to capture when pep is referenced by some other name
-- Comments from the Community:  The response to the generator comprehension proposal has been mostly favorable. 
-- BDFL Pronouncements  1.  The new built-in function is ACCEPTED. 

-- we can look for sentences with these terms and get teh triples, but is there a way to get these terms also automatically 'BDFL pronouncement'