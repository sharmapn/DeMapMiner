select * from results_bckdec where pep =308 AND clausie <> '' order by datetimestamp;

select pep, messageid,datetimestamp,messagesubject, currentsentence, clausie from results   WHERE pep = 8 AND clausie <> '' order by datetimestamp;

select * from pepstates_danieldata where pep =294;
select * from allmessages where pep =8 and messageid = 30563;
select * from trackmessageprocessing where pep = 8 order by messageid desc;
select * from results_bck where pep =6;

select messageid from allmessages where pep =219 order by messageid desc limit 100;

select pep, count(*) as numMessages from allmessages 
group by pep order by pep asc;

select pep, count(messageid) as numMessages from trackmessageprocessing 
group by pep order by pep asc;

select messageid from allmessages where pep = 8 order by messageid desc limit 100;

select pep, messageid,datetimestamp,messagesubject, currentsentence, clausie from results   WHERE  clausie <> '' and pep =8  order by pep, messageid;

select pep, messageid,datetimestamp,messagesubject, currentsentence, clausie from results   WHERE  pep =8 order by pep, datetimestamp;

delete from results where pep =432;
delete from trackmessageprocessing where pep = 432;