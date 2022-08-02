create table distinctsenders(
id int,
emailaddress tinytext,
senderName tinytext
totalMessageCount int,
senderFirstName tinytext,
senderLastName tinytext,
);

create table distinctDevSenders(
id int,
emailaddress tinytext,
senderName tinytext,
totalMessageCount int,
senderFirstName tinytext,
senderLastName tinytext
);

Alter table allmessages add INDEX `senderFullName` (`senderFullName`(40));
Alter table allmessages add INDEX `senderemail` (`senderemail`(40));

ALTER TABLE allmessages
ADD COLUMN `senderFirstName` TINYTEXT AFTER `senderFullName`,
ADD COLUMN `senderLastName` TINYTEXT AFTER `senderFullName`;


-- time consuming way
-- insert into distinctsenders (emailaddress,senderName)
-- select distinct(senderFullName),senderemail from  allmessages
-- group by senderemail limit 2;
-- main one used
insert into distinctsenders (emailaddress)
select distinct(senderemail) from  allmessages;
-- if stuffed up
--m delete from distinctsenders where id IS NULL;
-- only developers
insert into distinctDevSenders (emailaddress)
select distinct(senderemail) from  allmessages
where folder = 'C:\\datasets\\python-dev';

select emailaddress from distinctsenders2
limit 5

-- insert into distinctsenders (senderName)
select distinct(senderFullName) 
from allmessages a -- , distinctsenders d
where senderemail = 'barryw@python.org'
LIMIT 5

select * from distinctsenders
order by emailaddress

update allmessages set senderFullName = NULL;
update allmessages set senderemail = NULL;

-- below code is hardly used..only if i mess up and need to strat from teh beginning
update distinctsenders set id = NULL;
update distinctsenders set senderName = NULL;
update distinctsenders set totalMessageCount = NULL;

-- THIS IS MAIN LIST OF EMAIL UPDATES
-- these emaiol address gives error so need to remove some eklemenets
-- can put one of these just before populating the distinct senders table so wont need both in that case.

-- only need thsi 4 lines - but lately included them in java
-- UPDATE distinctsenders SET emailaddress=REPLACE(emailaddress,'\\','');
-- UPDATE allmessages SET senderemail=REPLACE(senderemail,'\\','');
-- remove single quotes
-- UPDATE distinctsenders SET emailaddress=REPLACE(emailaddress,'\'','');
-- UPDATE allmessages SET senderemail=REPLACE(senderemail,'\'','');

-- dont need these below as removing ' and / caters for all

-- UPDATE distinctsenders SET emailaddress=REPLACE(emailaddress,'don\'tspamme','dontspamme');
-- UPDATE allmessages SET senderemail=REPLACE(senderemail,'don\'tspamme','dontspamme');
-- don't@
-- UPDATE distinctsenders SET emailaddress=REPLACE(emailaddress,'don\'t@','dont@');
-- UPDATE allmessages SET senderemail=REPLACE(senderemail,'don\'t@','dont@');
-- 'espam-s'
-- UPDATE distinctsenders SET emailaddress=REPLACE(emailaddress,'espam-s\'','espam-s');
-- UPDATE allmessages SET senderemail=REPLACE(senderemail,'espam-s\'','espam-s');

-- raffaelcavallaro@pas-d'espam-sil-vous-plait-mac.com
-- UPDATE distinctsenders SET emailaddress=REPLACE(emailaddress,'\'espam','espam');
-- UPDATE allmessages SET senderemail=REPLACE(senderemail,'\'espam','espam');


-- remove single quotes
-- UPDATE distinctsenders SET emailaddress = REPLACE(emailaddress, '\'' , '');
-- UPDATE distinctsenders SET senderName = REPLACE(senderName, '\'' , senderName);
-- UPDATE allmessages SET senderemail		 = REPLACE(senderemail, '\'' , senderemail);
-- UPDATE allmessages SET senderFullName	 = REPLACE(senderFullName, '\'' , senderFullName);

-- delete FROM DISTINCTSENDERS where emailaddress like '%raffaelcavallaro@pas-draffaelcavallaro@'%
-- delete FROM DISTINCTSENDERS where emailaddress like '%stostoniets11@telenet.nothing%';
-- delete FROM DISTINCTSENDERS where emailaddress like '%Suzie@FloridaPanhandlerSuzie%';
-- delete FROM DISTINCTSENDERS where emailaddress like '%uus.Jason@msn.coms%';