select distinct(jep) from allmessages;
select * from allmessages where jep = -1 and (email like '%jep%' or subject like '%jep%') limit 50;

select jep,count(*) as cnt from allmessages group by jep order by cnt desc;		-- folder
select jep,count(*) as cnt from allmessages group by jep order by jep asc;

select folder,count(*) as cnt from allmessages where jep <> -1 group by folder order by cnt desc;

select * from jepdetails;

select * from allmessages where messageid = 15783;
select * from allmessages where jep = 223 AND email LIKE '%%' order by date2 asc;

select * from allmessages where jep = -1 and email REGEXP  'jep + ^[0-9]'  limit 1;	--           '%jep%' or subject like '%jep%'
-- '%[0-9] store%'