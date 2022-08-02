select * from pythonmembers_coredevelopers
-- set all to core developers as initial list in this table were core developers, others were added later
update pythonmembers_coredevelopers set name_corrected = replace(name,'.',' ');

-- now addting pep editors
Chris Angelico,Anthony Baxter,Georg Brandl,Brett Cannon,David Goodger,Jesse Noller,Berker Peksag,Guido van Rossum,Barry Warsaw
insert into pythonmembers_pepeditors(name) values ('Chris Angelico');
insert into pythonmembers_pepeditors(name) values ('Anthony Baxter');
insert into pythonmembers_pepeditors(name) values ('Georg Brandl');
insert into pythonmembers_pepeditors(name) values ('Brett Cannon');
insert into pythonmembers_pepeditors(name) values ('David Goodger');
insert into pythonmembers_pepeditors(name) values ('Jesse Noller');
insert into pythonmembers_pepeditors(name) values ('Berker Peksag');
insert into pythonmembers_pepeditors(name) values ('Guido van Rossum');
insert into pythonmembers_pepeditors(name) values ('Barry Warsaw');

update pythonmembers_pepeditors set name_corrected = lower(name);
select distinct(authorsrole) from allmessages where pep = 308;	
-- will give all combinations for thsi pep, entre table will be expensive
SELECT count(messageid) as cnt from allmessages 
where pep = 274 
and authorsrole like '%bdfl%' 
AND datetimestamp BETWEEN '2001-10-25 13:28:07.0' and '2012-04-09 16:42:21.0'
select name_corrected from pythonmembers_coredevelopers;

select * from pepstates_danieldata_datetimestamp;
pepdetails

select messageid, count(pep) as cnt from allmessages group by messageid having cnt >1;\

select * from trainingdata order by pep;
select name_corrected from pythonmembers_coredevelopers;


select pep, folder,authorsrole, count(messageid) as cnt 
from allmessages
where pep = 308
group by folder, authorsrole

select pep, authorsrole, count(messageid) as cnt 
from allmessages
where pep = 308
group by authorsrole

-- pep, author, senderfullname 
select author, count(messageid) as cnt from allmessages
where pep = 308 
and authorsrole = 'othercommunitymember'
group by author
order by cnt desc

select distinct(author) as au from allmessages where pep = 308  and authorsrole = 'othercommunitymember' order by au

select name_corrected from pythonmembers_coredevelopers
select name_corrected from pythonmembers_pepeditors

select * from  distinctsenders where cluster = 1 -- sendername like 'Brett Cannon'