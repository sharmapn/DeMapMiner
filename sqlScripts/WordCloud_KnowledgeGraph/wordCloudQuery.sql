select causesentence from trainingdata;

ALTER TABLE `allmessages`
ADD COLUMN `originalPEPNumberNum` INT(11) NULL DEFAULT NULL AFTER `originalPEPNumber`;

update allmessages set originalPEPNumberNum= pep;