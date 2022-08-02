-- only developers..thats why selected from dev folder and pep = -1
create table allmessages_withpepnumbers
as select * from allmessages where 
folder = 'C:\\datasets\\python-dev'
and pep <> -1;

create table distinctAuthorPepNumbers as 
	select distinct (clusterBySenderFullName) as senderName from allmessages_withpepnumbers;

alter table distinctAuthorPepNumbers
	-- ADD COLUMN	`senderName` TINYTEXT NULL,
ADD COLUMN	`allPostsByThisAuthor` INT(11) NULL DEFAULT NULL,
ADD COLUMN	`allUniquePostsByThisAuthor` INT(11) NULL DEFAULT NULL,
ADD COLUMN	`allPostsByThisAuthorInInformationalPEPs` INT(11) NULL DEFAULT NULL,
ADD COLUMN	`allPostsByThisAuthorInProcessPEPs` INT(11) NULL DEFAULT NULL,
ADD COLUMN	`allPostsByThisAuthorInStandardPEPs` INT(11) NULL DEFAULT NULL,
ADD COLUMN	`allPostsByThisAuthorInTotalUniquePeps` INT(11) NULL DEFAULT NULL,
ADD COLUMN	`allPostsByThisAuthorInUniqueStandardPeps` INT(11) NULL DEFAULT NULL,
ADD COLUMN	`allPostsByThisAuthorInUniqueProcessPeps` INT(11) NULL DEFAULT NULL,
ADD COLUMN	`allPostsByThisAuthorInUniqueInformationalPeps` INT(11) NULL DEFAULT NULL,
ADD COLUMN	`allPostsByThisAuthorInUniquePepsEqualToNULL` INT(11) NULL DEFAULT NULL,
ADD COLUMN	`allPostsByThisAuthorInPepTypeEqualToNULL` INT(11) NULL DEFAULT NULL,
ADD COLUMN	`allUniquePostsByThisAuthorInStandardPeps` INT(11) NULL DEFAULT NULL,
ADD COLUMN	`allUniquePostsByThisAuthorInProcessPeps` INT(11) NULL DEFAULT NULL,
ADD COLUMN	`allUniquePostsByThisAuthorInInformationalPeps` INT(11) NULL DEFAULT NULL,
ADD COLUMN	`totalNumberOfUniqueNULLPEPTypeInWhichAuthorPosted` INT(11) NULL DEFAULT NULL,
ADD COLUMN	`totalNumberOfUniqueStandardPEPTypeInWhichAuthorPosted` INT(11) NULL DEFAULT NULL,
ADD COLUMN	`totalNumberOfUniqueProcessPEPTypeInWhichAuthorPosted` INT(11) NULL DEFAULT NULL,
ADD COLUMN	`totalNumberOfUniqueInformationalPEPTypeInWhichAuthorPosted` INT(11) NULL DEFAULT NULL,
ADD COLUMN	`allSeedingPostsByThisAuthor` INT(11) NULL DEFAULT NULL,
ADD COLUMN	`maxPostsInAPepByThisAuthor` INT(11) NULL DEFAULT NULL,
ADD COLUMN	`minPostsInAPepByThisAuthor` INT(11) NULL DEFAULT NULL,
ADD COLUMN	`firstPostDate` DATE NULL DEFAULT NULL,
ADD COLUMN	`lastPostDate` DATE NULL DEFAULT NULL,
ADD COLUMN	`YearFirstPostDate` INT(11) NULL DEFAULT NULL,
ADD COLUMN	`YearLastPostDate` INT(11) NULL DEFAULT NULL,
ADD COLUMN	`daysTillLastPost` INT(11) NULL DEFAULT NULL;

-- NOW USE THE GROUP 2 FUNCTIONA JAVA CODE AND THEN run teh code below
	
-- first and lastdate for each member
update distinctauthorpepnumbers
 JOIN allmessages
       ON allmessages.sendername = distinctauthorpepnumbers.sendername
set firstpostdate = (select min(date2) from allmessages)

update distinctauthorpepnumbers
 JOIN allmessages
       ON allmessages.sendername = distinctauthorpepnumbers.sendername
set lastpostdate = (select max(date2) from allmessages)

-- get the totals
SELECT yearlastpostdate, COUNT(sendername) -- , yearlastpostdate -- yearlastpostdate,
-- max(date2),min(date2), DATEDIFF(CURDATE(),max(date2)) as c, COUNT(messageid) as d, folder,
-- YEAR(max(date2)) as ymax,YEAR(min(date2)) as ymin
from distinctauthorpepnumbers
-- where folder = 'c:\\datasets\\python-dev'
group by yearlastpostdate; -- , yearlastpostdate

-- FINAL PART
-- all churn scripts
-- the group2 function would update the year firstpostdate and lastpostdate

-- if get stuck or have to try multiple times
update distinctauthorpepnumbers set firstPostDate = NULL, lastPostDate =NULL, YearFirstPostDate =NULL,YearLastPostDate=NULL,daysTillLastPost=NULL;

-- now just run this script to retrieve all count by last post date/ firstpostdate
-- just change yearlastpostdate to yearfirstpostdate
SELECT yearfirstpostdate, COUNT(sendername) -- , yearlastpostdate -- yearlastpostdate,
-- max(date2),min(date2), DATEDIFF(CURDATE(),max(date2)) as c, COUNT(messageid) as d, folder,
-- YEAR(max(date2)) as ymax,YEAR(min(date2)) as ymin
from distinctauthorpepnumbers
-- where folder = 'c:\\datasets\\python-dev'
group by yearfirstpostdate -- , yearfirstpostdate

-- this is already done, so just compare
-- update danieldata set yeardate2 = year(date2)
