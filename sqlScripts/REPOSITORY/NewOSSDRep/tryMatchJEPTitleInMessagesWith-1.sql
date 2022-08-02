select jep,title from jepdetails

select jep, email, subject from allmessages where jep = -1 limit 500;

SELECT jep, title
  FROM TABLE jepdetails
  JOIN TABLE allmessages ON allmessages.email LIKE '%'+ jepdetails.title +'%';
  
-- use mysql console for both of these sql statements 
-- check in email subject
create or replace table allmessagesNegative1
as 
SELECT jep,subject,email FROM allmessages where JEP = -1;

create table jepTitlesInMessageSubject0
as 
SELECT a.jep,b.title,a.subject,a.email
FROM allmessagesNegative1 a
INNER JOIN jepdetails b ON a.subject LIKE CONCAT('%', b.title, '%'); -- ON a.email LIKE ;
-- where a.jep = -1; 
-- limit 5;

select distinct(title) from jepTitlesInMessageSubject0;

update allmessagesNegative1 a JOIN jepdetails b ON a.subject LIKE CONCAT('%', b.title, '%')
	SET a.jep = b.jep

UPDATE tweets JOIN users ON tweets.user_id = users.user_id
  SET tweets.spam = 1
WHERE users.name = 'SUSPENDED'

-- check in email body
create table jepTitlesInMessageBody
as 
SELECT a.jep,b.title,a.subject,a.email
FROM allmessagesNegative1 a
INNER JOIN jepdetails b ON a.email LIKE CONCAT('%', b.title, '%');