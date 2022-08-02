CREATE TABLE `allmessages` (
	`PEP` INT(11) NULL DEFAULT NULL,
	`messageID` INT(11) NULL DEFAULT NULL,
	`date2` DATE NULL DEFAULT NULL,
	`pepType` TEXT NULL,
	`dateTimeStamp` TIMESTAMP NULL DEFAULT NULL,
	`email` LONGTEXT NULL,
	`analyseWords` LONGTEXT NULL,
	`Author` MEDIUMTEXT NULL,
	`subject` MEDIUMTEXT NULL,
	`location` TEXT NULL,
	`line` LONGTEXT NULL,
	`folder` TEXT NULL,
	`file` TEXT NULL,
	`statusChanged` TINYINT(4) NULL DEFAULT NULL,
	`statusFrom` TEXT NULL,
	`statusTo` TEXT NULL,
	`type` TEXT NULL,
	`from` TEXT NULL,
	`emailMessageId` TINYTEXT NULL,
	`wordsList` TEXT NULL,
	`inReplyTo` TEXT NULL,
	`references` TEXT NULL,
	`link` TINYTEXT NULL,
	`senderName` TINYTEXT NULL,
	`required` TINYINT(4) NULL DEFAULT NULL,
	`hasPEPTitleOnly` TINYINT(4) NULL DEFAULT NULL,
	INDEX `PEP` (`PEP`),
	INDEX `subject` (`subject`(50)),
	INDEX `folder` (`folder`(20)),
	INDEX `file` (`file`(15)),
	INDEX `senderName` (`senderName`(20)),
	INDEX `messageId_Index` (`messageID`),
	INDEX `emailMessageId` (`emailMessageId`(80))
)
COLLATE='latin1_swedish_ci'
ENGINE=MYISAM
ROW_FORMAT=COMPACT
;
