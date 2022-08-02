ALTER TABLE `distinctauthorspepnumbers`
	ADD COLUMN `firstPostDate` DATE NULL DEFAULT NULL AFTER `minPostsInAPepByThisAuthor`,
	ADD COLUMN `lastPostDate` DATE NULL DEFAULT NULL AFTER `firstPostDate`;
	ADD COLUMN `YearFirstPostDate` INT NULL DEFAULT NULL AFTER `lastPostDate`,
	ADD COLUMN `YearLastPostDate` INT NULL DEFAULT NULL AFTER `YearFirstPostDate`;
	ADD COLUMN `daysTillLastPost` INT(11) NULL DEFAULT NULL AFTER `YearLastPostDate`;
