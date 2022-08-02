select max(messageid) from allmessages ;

delete from allmessages where messageid >= 3000000;

select * from allmessages where folder like '%new%'; //messageid >= 3000000;

select * from allmessages where messageid >= 3000000 and date2 NOT NULL;
select pep,count(messageid) as cnt from allmessages where messageid >= 3000000 group by pep order by pep desc;