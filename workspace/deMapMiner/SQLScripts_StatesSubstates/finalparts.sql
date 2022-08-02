select * from allmessages where pep = 308 and email like '%figure out which election method to use to vote for%'

select * from labels where idea like '%voting%'

select * from labels where idea like '%voting%'

select distinct idea from labels

select pep, messageid,date, currentsentence,label, subject, relation, object, clausie  FROM results
where pep=308 and messageid = 261760 and label = 'consensus'

select pep, messageid, currentsentence, label, subject, relation, object, clausie
from results_jan where label like '%consensus%';