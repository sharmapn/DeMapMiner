-- this table is hardly used in our study, so not created yet

create table allMembers
AS select DISTINCT(sendername) from allmessages;

alter table allMembers
ADD COLUMN 	`allPostsByThisAuthor` INT(11) NULL DEFAULT NULL,
ADD COLUMN 	`allUniquePostsByThisAuthor` INT(11) NULL DEFAULT NULL,
ADD COLUMN 	`allPostsByThisAuthorInInformationalPEPs` INT(11) NULL DEFAULT NULL,
ADD COLUMN 	`allPostsByThisAuthorInProcessPEPs` INT(11) NULL DEFAULT NULL,
ADD COLUMN 	`allPostsByThisAuthorInStandardPEPs` INT(11) NULL DEFAULT NULL,
ADD COLUMN 	`allPostsByThisAuthorInTotalUniquePeps` INT(11) NULL DEFAULT NULL,
ADD COLUMN 	`allPostsByThisAuthorInUniqueStandardPeps` INT(11) NULL DEFAULT NULL,
ADD COLUMN 	`allPostsByThisAuthorInUniqueProcessPeps` INT(11) NULL DEFAULT NULL,
ADD COLUMN 	`allPostsByThisAuthorInUniqueInformationalPeps` INT(11) NULL DEFAULT NULL,
ADD COLUMN 	`allPostsByThisAuthorInUniquePepsEqualToNULL` INT(11) NULL DEFAULT NULL,
ADD COLUMN 	`allSeedingPostsByThisAuthor` INT(11) NULL DEFAULT NULL,
ADD COLUMN 	`maxPostsInAPepByThisAuthor` INT(11) NULL DEFAULT NULL,
ADD COLUMN 	`minPostsInAPepByThisAuthor` INT(11) NULL DEFAULT NULL,
ADD COLUMN 	`firstPostDate` DATE NULL DEFAULT NULL,
ADD COLUMN 	`lastPostDate` DATE NULL DEFAULT NULL,
ADD COLUMN 	`YearFirstPostDate` INT(11) NULL DEFAULT NULL,
ADD COLUMN 	`YearLastPostDate` INT(11) NULL DEFAULT NULL,
ADD COLUMN 	`daysTillLastPost` INT(11) NULL DEFAULT NULL;