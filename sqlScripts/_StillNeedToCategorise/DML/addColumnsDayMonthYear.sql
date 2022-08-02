-- ALTER TABLE `all_distinctmessages`
--	ADD COLUMN `day` INT NULL DEFAULT NULL AFTER `date2`,
--	ADD COLUMN `month` INT NULL DEFAULT NULL AFTER `day`,
--	ADD COLUMN `year` INT NULL DEFAULT NULL AFTER `month`;
UPDATE all_distinctmessages set day = DATE(date2);
UPDATE all_distinctmessages set month = MONTH(date2);
UPDATE all_distinctmessages set year = YEAR(date2);