-- ALTER TABLE `allmessages` ADD COLUMN `pepType` TEXT NULL DEFAULT NULL AFTER `date2`;
-- ALTER TABLE `allmessages_peptitleonly` ADD COLUMN `pepType` TEXT NULL DEFAULT NULL AFTER `date2`;
-- ALTER TABLE `allpeps_ideaspeptitles` ADD COLUMN `pepType` TEXT NULL DEFAULT NULL AFTER `date2`;

-- have to do this to remove spaces
-- UPDATE pepdetails set type = TRIM(type);
update allpeps set allpeps.pepType =(select type from pepDetails where pepDetails.pep = allpeps.pep )