SELECT proposal,messageid,datetimestamp, sentence,termsMatched, probability, reason, author 
from autoextractedreasoncandidatesentences
where proposal = 308
order by datetimestamp asc;

select * from allmessages where pep = 308 and email like '%accepted%' and messageid = 136039;

select * from extractedrelations_clausie where isEnglish = false;