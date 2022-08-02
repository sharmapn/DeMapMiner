select * from manualreasonextraction where proposal > 370 order by proposal;
select distinct(proposal) from manualreasonextraction;
-- delete from manualreasonextraction where proposal is null;

select * from manualreasonextraction where messageid = (select max(messageid) from manualreasonextraction);
ALTER TABLE `manualreasonextraction` 
	ADD INDEX `messageID` (`messageID`),
	ADD INDEX `proposal` (`proposal`);
	
select * from manualreasonextraction where messageid = 14143  order by proposal, messageid ;	
select * from allmessages where messageid = 328159; -- 14143;
select * from allmessages where pep = 308; -- 14143;
SELECT pep,messageid,date2, email from allmessages where pep = 3119 order by messageid asc;
select distinct(level) from manualreasonextraction; 
select proposal, messageID, count(*) as cnt from manualreasonextraction group by messageID order by cnt desc;
select * from manualreasonextraction where proposal = 448 order by messageid; 
-- where messageID = 25487; -- 147552;
select count(*) from manualreasonextraction;
select pep, count(*) as cnt from allmessages  group by pep;
SELECT * from allmessages where pep = 220; -- and messageid = 282974
SELECT proposal,messageid,date2, sentence,termsMatched, level from manualreasonextraction where proposal >= 3119 order by proposal asc, messageid asc, date2 asc
select distinct(proposal) from manualreasonextraction;
-- delete  from manualreasonextraction where proposal >=448; -- 335;
select * from manualreasonextraction where proposal >=448 ; 
select * from manualreasonextraction where author LIKE '%%';
	