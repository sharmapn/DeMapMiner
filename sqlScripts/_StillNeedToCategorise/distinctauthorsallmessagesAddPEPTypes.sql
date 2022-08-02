ALTER TABLE `distinctauthorsallmessages`
	ADD COLUMN `allPostsByThisAuthorInInformationalPEPs` INT(11) NULL DEFAULT NULL AFTER `allPostsByThisAuthor`,
	ADD COLUMN `allPostsByThisAuthorInProcessPEPs` INT(11) NULL DEFAULT NULL AFTER `allPostsByThisAuthorInInformationalPEPs`,
	ADD COLUMN `allPostsByThisAuthorInStandardPEPs` INT(11) NULL DEFAULT NULL AFTER `allPostsByThisAuthorInProcessPEPs`;
