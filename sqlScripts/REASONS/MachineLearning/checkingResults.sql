select distinct(pep) from trainingdata where label like '%accepted%' 
and LENGTH(causesentence) >0;
 pep = 201;
update trainingdata set effectmessageid = 275160 where pep = 201;
update trainingdata set causemessageid = 197081 where pep = 411 
and causemessageid = 275160;
delete from trainingdata where pep = 201 and length(causesentence) <1;
-- This proposal has been accepted by the BDFL.

select * from pepdetails where pep = 203


-- delete unnecessary rows
delete from trainingdata where pep = 415 and causemessageid = 385446;
update trainingdata set causemessageid = 135109 where pep = 352;
select * from allmessages where messageid = 334802;

select * from allmessages where pep = -1  and subject like '%Unifying try-except and try-finally%' 
and folder = 'C:\\datasets\\python-dev';
and date2 = '2005-05-04' 

and author like '%Guido%'

select * from allmessages where pep = 440 and folder = 'C:\\datasets\\python-dev' -- and email like '%anybody against%'
-- and author = 'Guido van Rossum' 
-- and subject = '[Python-Dev] PEP 371: Additional Discussion'
and email like '%can be resolved%';
commit;
-- message not foudn so inserting

select * from trainingdata where pep = 3139;

insert into allmessages 
select *
from allmessages
where messageid = 132886