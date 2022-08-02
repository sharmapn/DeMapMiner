select *  -- pep,messageid, date2, datetimestamp 
from allmessages where pep = 308 and YEAR(date2) = 2001 AND DAY(date2) = 17 and MONTH(date2) = 10
order by date2 asc limit 400

(SELECT * 
from allmessages 
WHERE  pep = 308 and YEAR(date2) = 2003 AND DAY(date2) = 07 and MONTH(date2) = 02
order by date2) 
UNION   
-- only has messages from lists with pep title matching in subject
(
SELECT *
from allpeps_ideaspeptitles 
WHERE YEAR(date2) = 2003 AND DAY(date2) = 07 and MONTH(date2) = 02 
and subject like '%conditional expression%' 
order by date2
) 

-- i want to know if these messages are in any way part of the all messages table
-- they could be but not assigned a pep number 
-- REASON FOUND - PEP numbers have not been assigned to python lists based on the pep tile in subject
select originalPEPNumber,pep,date2, messageid, folder, subject, email  -- pep,messageid, date2, datetimestamp 
from allmessages 
where  -- pep = 308 -- or pep = -1 
-- and  
-- and 
YEAR(date2) = 2003 AND DAY(date2) = 07 and MONTH(date2) = 02
and subject like '%conditional expression%' 
order by date2, messageid
---- and folder = 'C:\\datasets\\python-lists' and file= '2001-October.txt'


select * from allmessages 
where folder = 'C:\\datasets\\python-lists' 
and pep <> originalPEPNumber;

select *  -- pep,messageid, date2, datetimestamp 
from allmessages 
-- where -- pep = 308 and 
where folder = 'lists' and
file = '2001-October.txt' and
subject like '%conditional expression%' 

select *  -- pep,messageid, date2, datetimestamp 
from allmessages 
where pep = 308 and 
folder = 'C:\\datasets\\python-lists'
LIMIT 1

(SELECT * 
from allmessages 
WHERE  pep = 308 
 and YEAR(date2) = 2001 AND DAY(date2) = 18 and MONTH(date2) = 10
order by date2) 
UNION   
-- only has messages from lists with pep title matching in subject
(
SELECT pep, messageid,folder, file,subject
from allpeps_ideaspeptitles 
WHERE pep = 308 
 and YEAR(date2) = 2001 AND DAY(date2) = 18 and MONTH(date2) = 10 
order by date2)

SELECT *
from pepstates_danieldata where pep = 308 limit 100


(SELECT pep, messageid, date2, subject, folder,file,emailmessageid
from allmessages 
where pep = 308
and YEAR(date2) = 2003 AND DAY(date2) = 07 and MONTH(date2) = 02 order by messageid
order by pep
and subject like '%conditional expression%' 
-- and  folder = 'C:\\datasets\\python-lists' 
order by date2) 
UNION   
-- only has messages from lists with pep title matching in subject
(
SELECT *
from allpeps_ideaspeptitles 
WHERE pep = 308 and YEAR(date2) = 2003 AND DAY(date2) = 07 and MONTH(date2) = 02 
and subject like '%conditional expression%' 
order by date2
) 

(SELECT subject,senderName,date2, analyseWords from allmessages WHERE pep = 308 order by date2) UNION   
(SELECT subject,senderName,date2, analyseWords from allpeps_ideaspeptitles WHERE pep = 308 order by date2)

select * from allmessages
where pep = 308 and YEAR(date2) = 2003 AND DAY(date2) = 07 and MONTH(date2) = 02 
-- and subject like '%For review: PEP 308 - If-then-else expression%' 
order by messageid;
