select distinct(senderemail), senderFullName, fromline, email 
from allmessages where senderFullName = 'Paul Rubin'
limit 20

update distinctsenders d
set totalSenderCount = (
	select count(distinct(senderFullName)) 
	from allmessages a
	where a.senderemail = d.emailaddress )
	);
-- group by senderemail

-- having c>1;
order by c desc
limit 1;


SELECT senderFullName,senderfirstname, senderlastname, count(distinct(senderFullName)), length(senderFullName) as 'len'
from allmessages where senderEmail = 'aahz@pythoncraft.com';

select distinct (sendername)
from allmessages 
where senderemail = 'gh@ghaering.de';

SELECT senderFullName,senderfirstname, senderlastname, count(distinct(messageid)), length(senderFullName) as len  -- count(distinct(messageid)),
from allmessages where senderEmail = 'aahz@pythoncraft.com'
group by senderFullName
ORDER BY len desc;

SELECT senderFullName,senderfirstname, senderlastname, count(*) -- count(distinct(messageid)),
from allmessages 
where senderEmail = 'aahz@pythoncraft.com'
group by senderFullName,messageid
-- ORDER BY len desc;

SELECT * from distinctsenders
order by id asc -- totalmessagecount desc
limit 20 

select * from allmessages where senderemail like 'bw%'
limit 2;

select * from distinctsenders where clustered = 1;
-- where senderEmailFirstSegment is NOT NULL