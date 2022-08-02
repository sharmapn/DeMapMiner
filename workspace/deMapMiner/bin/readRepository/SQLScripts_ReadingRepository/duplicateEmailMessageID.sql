select count(distinct messageid) from allmessages where pep <> -1
select count(messageid) from allmessages; -- 1501517
select count(emailmessageid) from allmessages -- 1500704
select count(distinct messageid) from allmessages; -- 1485033
select count(distinct emailmessageid) from allmessages -- 1433795
select max(messageid) from results where pep = 253 and messageid < 5000000

select count(*) from allmessages where pep <> originalpepnumber;

select count(emailmessageid) as dup
from allmessages 
group by pep

SELECT pep, emailmessageid, COUNT(*) c FROM allmessages 
where pep > -1
GROUP BY pep, emailmessageid HAVING c > 1;