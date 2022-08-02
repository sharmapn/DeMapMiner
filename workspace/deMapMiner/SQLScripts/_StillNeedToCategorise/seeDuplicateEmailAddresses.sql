select count(*) from distinctsenders
where cluster >0
-- order by cluster desc

CREATE TABLE distinctsendersMEM (
	`id` INT(11),
	`emailaddress` VARCHAR(100) ,
	`senderName` VARCHAR(100)  ,
	`totalSenderCount` INT(11) ,
	`totalMessageCount` INT(11),
	`senderFirstName` VARCHAR(50) ,
	`senderLastName` VARCHAR(50)  ,
	`senderEmailFirstSegment` VARCHAR(50)  ,
	`cluster` INT(11) ,
	`clustered` INT(11) 
	) 
ENGINE=MEMORY 
AS 
SELECT * FROM distinctsenders
