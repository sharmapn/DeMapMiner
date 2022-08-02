create database jeps_2018;

select * from allmessages 

select jep, count(messageid) from allmessages
group by jep


select max(date2) from allmessages

select jep,  count(messageid)
from allmessages
where jep > -1
group by jep

select folder, count(messageid) as cnt
from allmessages 
where jep > -1
group by folder
order by cnt desc 

create table sub as
select jep, messageid, subject from allmessages where subject like '%jep%';

alter table allmessages add column lastdir text, add index (lastdirec);
-- ALTER TABLE mytable ADD INDEX (myarguments);
ALTER TABLE allmessages ADD INDEX lastdirectory (`lastdir`(15));
select folder, replace(folder,"C:\\OSSDRepositories\\JEPs\\","") as test from allmessages limit 5;
update allmessages set lastdir = replace(folder,"C:\\OSSDRepositories\\JEPs\\","") 

SELECT messageid, jep, subject from allmessages 
where jep = -1 and subject IS NOT NULL and lastdir = '2d-dev'

select * from proposaldetails_titlesforeachpep_debug
select count(*) from proposaldetails_titlesforeachpep_debug
select count(*) from allmessages where jep <> originaljepnumber
SELECT * FROM allmessages where messageid = 4123;
select * from sub;
SELECT * from jepdetails;

ALTER TABLE `allmessages`
ADD COLUMN `lastdir` TEXT NULL AFTER `folder`;

select min(messageid) from allmessages 
where messageid = 103124
select * from allmessages where messageid = 1

ALTER TABLE allmessages ADD INDEX `lastdirectory` (`lastdir`)

select * from allmessages where jep > 0 and
email like '%consensus%'
