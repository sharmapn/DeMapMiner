select distinct(messageid) from allsentences;
select * from allsentences where sentence like '%creates%';
sentencecounterinparagraph> 2;
 where messageid = 90410 ;
select * from allmessages where messageid = 12394;
SELECT max(pep) as max FROM allsentences;
SELECT max(messageid) as maxmid FROM allsentences where pep= 1;

select pep, messageid,paragraphcounter, sentencecounterinparagraph 
from allsentences where sentencecounterinparagraph> 0
group by pep, messageid,paragraphcounter, sentencecounterinparagraph
order by pep, messageid,paragraphcounter, sentencecounterinparagraph;

select * from allsentences  where messageid = 12394 ;