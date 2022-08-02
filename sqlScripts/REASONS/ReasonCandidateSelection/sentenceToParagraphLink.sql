
select id, proposal, messageid, sentence, paragraphCounter,
(	select paragraph 
	from allparagraphs p 
	where p.proposal = s.proposal 
	and   p.messageid = s.messageid
	and   p.paragraphCounter = s.paragraphCounter) as para 
from allsentences s limit 5
--  no data in allparagraphs table - so maybe we can construct the paragraphs by looking at sentences with the same paragraph number

SELECT sentence from allsentences 
where proposal = 7 and messageid = 28617 and paragraphCounter = 2

SELECT id, proposal, messageid, sentence, paragraphCounter,
( SELECT sentence from allsentences  )
from allsentences 
where  -- proposal = 308 and
sentence like '% pep %' and    -- we don't consider instances like 'pep308.txt' therefore we look for where sentence contains term 'pep' on its own
isenglishorcode = 1 
and (lastdir like '%dev%' or lastdir like '%idea%') 
order by proposal asc, messageid asc
LIMIT 7

update allsentences set entireparagraph  
