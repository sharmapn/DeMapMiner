select pep, originalpepnumber, subject from allmessages where pep <> originalpepnumber limit 5;

select messageid, pep, originalpepnumber, subject, email from allmessages where pep = -1 and email like '%After a long discussion%'
-- returns 90,910
select messageid, pep, originalpepnumber, subject, email from allmessages where pep = 308 and email like '%After a long discussion%'