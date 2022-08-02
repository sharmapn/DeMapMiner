create table authormatching 
as select distinct(sendername) as sendername from allmessages

ALTER TABLE `authormatching`
	 ADD COLUMN `match` TINYTEXT NULL AFTER `sendername`,
	 ADD COLUMN `true` BIT NULL AFTER `match`;