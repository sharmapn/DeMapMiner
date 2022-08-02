CREATE TABLE `alldevelopers` (
	`sendername` TINYTEXT NULL,
	`allPostsByThisAuthor` INT(11) NULL DEFAULT NULL,
	`allPostsByThisAuthorInInformationalPEPs` INT(11) NULL DEFAULT NULL,
	`allPostsByThisAuthorInProcessPEPs` INT(11) NULL DEFAULT NULL,
	`allPostsByThisAuthorInStandardPEPs` INT(11) NULL DEFAULT NULL,
	`allPostsByThisAuthorInTotalUniquePeps` INT(11) NULL DEFAULT NULL,
	`allPostsByThisAuthorInUniqueStandardPeps` INT(11) NULL DEFAULT NULL,
	`allPostsByThisAuthorInUniqueProcessPeps` INT(11) NULL DEFAULT NULL,
	`allPostsByThisAuthorInUniqueInformationalPeps` INT(11) NULL DEFAULT NULL,
	`allPostsByThisAuthorInUniquePepsEqualToNULL` INT(11) NULL DEFAULT NULL,
	`allSeedingPostsByThisAuthor` INT(11) NULL DEFAULT NULL,
	`maxPostsInAPepByThisAuthor` INT(11) NULL DEFAULT NULL,
	`minPostsInAPepByThisAuthor` INT(11) NULL DEFAULT NULL,
	`firstPostDate` DATE NULL DEFAULT NULL,
	`lastPostDate` DATE NULL DEFAULT NULL,
	`YearFirstPostDate` INT(11) NULL DEFAULT NULL,
	`YearLastPostDate` INT(11) NULL DEFAULT NULL,
	`daysTillLastPost` INT(11) NULL DEFAULT NULL
)
COLLATE='latin1_swedish_ci'
ENGINE=InnoDB
;