select max(messageid) from allmessages
select * from allmessages where sendername IS NULL;
select * from allmessages where sendername like '%guido%' limit 5; 
select * from allmessages where sendername is null;
select count(*) from allmessages where datetimestamp is null;
  
SELECT count(*) from allmessages where senderFullName IS NULL
-- TOTAL 908332
select * from allmessages where fromline NOT like '% at %' AND fromline NOT like '%@%';


insert into distinctsenders (emailaddress) select distinct(senderemail) from  allmessages; 

create table distinctsenders(id int, emailaddress tinytext, senderName tinytext );