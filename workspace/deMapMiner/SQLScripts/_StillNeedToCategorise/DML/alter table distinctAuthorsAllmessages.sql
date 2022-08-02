alter table distinctAuthorsAllmessages
ADD COLUMN	`allPostsByThisAuthor` INT(11) NULL DEFAULT NULL,
ADD COLUMN	`allPostsByThisAuthorInUniquePeps` INT(11) NULL DEFAULT NULL,
ADD COLUMN	`allSeedingPostsByThisAuthor` INT(11) NULL DEFAULT NULL,
ADD COLUMN	`maxPostsInAPepByThisAuthor` INT(11) NULL DEFAULT NULL,
ADD COLUMN	`minPostsInAPepByThisAuthor` INT(11) NULL DEFAULT NULL;