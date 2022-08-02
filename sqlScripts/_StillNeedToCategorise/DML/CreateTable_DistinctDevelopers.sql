create table distinctDevelopers 
as select distinct (name) as senderName from allmessages

-- alter table distinctDevelopers
ADD COLUMN	`allPostsByThisAuthor` INT(11) NULL DEFAULT NULL,
ADD COLUMN	`allPostsByThisAuthorInUniquePeps` INT(11) NULL DEFAULT NULL,
ADD COLUMN	`allSeedingPostsByThisAuthor` INT(11) NULL DEFAULT NULL,
ADD COLUMN	`maxPostsInAPepByThisAuthor` INT(11) NULL DEFAULT NULL,
ADD COLUMN	`minPostsInAPepByThisAuthor` INT(11) NULL DEFAULT NULL;