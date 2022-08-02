-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server version:               5.6.25 - MySQL Community Server (GPL)
-- Server OS:                    Win64
-- HeidiSQL Version:             9.4.0.5125
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


create database bips;
use bips;

-- create table test(id integer);


-- Dumping structure for table peps_new.alldeveloperposts
CREATE TABLE IF NOT EXISTS `alldeveloperposts` (
  `bip` int(11) DEFAULT NULL,
  `messageID` int(11) DEFAULT NULL,
  `date2` date DEFAULT NULL,
  `bipType` text,
  `dateTimeStamp` timestamp NULL DEFAULT NULL,
  `email` longtext,
  `analyseWords` longtext,
  `Author` mediumtext,
  `subject` mediumtext,
  `location` text,
  `line` longtext,
  `folder` text,
  `file` text,
  `from` text,
  `emailMessageId` tinytext,
  `wordsList` text,
  `inReplyTo` text,
  `inReplyToUser` text,
  `references` text,
  `link` tinytext,
  `senderName` tinytext,
  `name` tinytext,
  `required` tinyint(4) DEFAULT NULL,
  `hasbipTitleOnly` tinyint(4) DEFAULT NULL,
  `countRepliesToThisMessage` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
-- Dumping structure for table bips_new.alldevelopers
CREATE TABLE IF NOT EXISTS `alldevelopers` (
  `clusterBySenderFullName` tinytext
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
-- Dumping structure for table bips_new.alldeveloperswithoutadmin
CREATE TABLE IF NOT EXISTS `alldeveloperswithoutadmin` (
  `clusterBySenderFullName` tinytext
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
-- Dumping structure for table bips_new.alldeveloperswithoutadminandmartin
CREATE TABLE IF NOT EXISTS `alldeveloperswithoutadminandmartin` (
  `clusterBySenderFullName` tinytext
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
-- Dumping structure for table bips_new.alldevelopers_b4clustering
CREATE TABLE IF NOT EXISTS `alldevelopers_b4clustering` (
  `sendername` tinytext,
  `allPostsByThisAuthor` int(11) DEFAULT NULL,
  `allUniquePostsByThisAuthor` int(11) DEFAULT NULL,
  `allPostsByThisAuthorInInformationalbips` int(11) DEFAULT NULL,
  `allPostsByThisAuthorInProcessbips` int(11) DEFAULT NULL,
  `allPostsByThisAuthorInStandardbips` int(11) DEFAULT NULL,
  `allPostsByThisAuthorInTotalUniquebips` int(11) DEFAULT NULL,
  `allPostsByThisAuthorInUniqueStandardbips` int(11) DEFAULT NULL,
  `allPostsByThisAuthorInUniqueProcessbips` int(11) DEFAULT NULL,
  `allPostsByThisAuthorInUniqueInformationalbips` int(11) DEFAULT NULL,
  `allPostsByThisAuthorInUniquebipsEqualToNULL` int(11) DEFAULT NULL,
  `allPostsByThisAuthorInbipTypeEqualToNULL` int(11) DEFAULT NULL,
  `allUniquePostsByThisAuthorInStandardbips` int(11) DEFAULT NULL,
  `allUniquePostsByThisAuthorInProcessbips` int(11) DEFAULT NULL,
  `allUniquePostsByThisAuthorInInformationalbips` int(11) DEFAULT NULL,
  `totalNumberOfUniqueNULLbipTypeInWhichAuthorPosted` int(11) DEFAULT NULL,
  `totalNumberOfUniqueStandardbipTypeInWhichAuthorPosted` int(11) DEFAULT NULL,
  `totalNumberOfUniqueProcessbipTypeInWhichAuthorPosted` int(11) DEFAULT NULL,
  `totalNumberOfUniqueInformationalbipTypeInWhichAuthorPosted` int(11) DEFAULT NULL,
  `allSeedingPostsByThisAuthor` int(11) DEFAULT NULL,
  `maxPostsInAbipByThisAuthor` int(11) DEFAULT NULL,
  `minPostsInAbipByThisAuthor` int(11) DEFAULT NULL,
  `firstPostDate` date DEFAULT NULL,
  `lastPostDate` date DEFAULT NULL,
  `YearFirstPostDate` int(11) DEFAULT NULL,
  `YearLastPostDate` int(11) DEFAULT NULL,
  `daysTillLastPost` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
-- Dumping structure for table bips_new.allmembers
CREATE TABLE IF NOT EXISTS `allmembers` (
  `clusterbysenderfullname` tinytext,
  `ideas` int(11) DEFAULT NULL,
  `dev` int(11) DEFAULT NULL,
  `lists` int(11) DEFAULT NULL,
  `commits` int(11) DEFAULT NULL,
  `checkins` int(11) DEFAULT NULL,
  `patches` int(11) DEFAULT NULL,
  `bugs_list` int(11) DEFAULT NULL,
  `number_oflists_postedin` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
-- Dumping structure for table bips_new.allmessages
CREATE TABLE IF NOT EXISTS `allmessages` (
  `bip` int(11) DEFAULT NULL,
  `originalbipNumber` int(11) DEFAULT NULL,
  `messageID` int(11) DEFAULT NULL,
  `date2` date DEFAULT NULL,
  `bipType` text,
  `dateTimeStamp` timestamp NULL DEFAULT NULL,
  `dateTimeStampString` text,
  `email` longtext,
  `analyseWords` longtext,
  `Author` mediumtext,
  `subject` mediumtext,
  `location` text,
  `line` longtext,
  `folder` text,
  `file` text,
  `from` text,
  `emailMessageId` tinytext,
  `wordsList` text,
  `inReplyTo` text,
  `inReplyToUser` text,
  `references` text,
  `link` tinytext,
  `senderName` tinytext,
  `name` tinytext,
  `required` tinyint(4) DEFAULT NULL,
  `hasbipTitleOnly` tinyint(4) DEFAULT NULL,
  `countRepliesToThisMessage` int(11) DEFAULT NULL,
  `fromLine` text,
  `senderemail` tinytext,
  `senderemailProcessed` tinytext,
  `senderEmailFirstSegment` tinytext,
  `senderFullName` tinytext,
  `senderFirstName` tinytext,
  `senderLastName` tinytext,
  `clusterBySenderFullName` tinytext,
  `inReplyToUserUsingClusteredSender` tinytext,
  `statusFrom` tinytext,
  `statusTo` tinytext,
  `statusChanged` int(11) DEFAULT NULL,
  `IdentifierCount` int(11) DEFAULT NULL,
  KEY `bip` (`bip`),
  KEY `subject` (`subject`(50)),
  KEY `folder` (`folder`(20)),
  KEY `file` (`file`(15)),
  KEY `senderName` (`senderName`(20)),
  KEY `messageId_Index` (`messageID`),
  KEY `emailMessageId` (`emailMessageId`(80)),
  KEY `inReplyTo` (`inReplyTo`(80)),
  KEY `senderFullName` (`senderFullName`(40)),
  KEY `senderemail` (`senderemail`(40)),
  KEY `senderEmailFirstSegment` (`senderEmailFirstSegment`(20)),
  KEY `senderFirstName` (`senderFirstName`(15)),
  KEY `senderLastName` (`senderLastName`(15)),
  KEY `inReplyToUserUsingClusteredSender` (`inReplyToUserUsingClusteredSender`(30)),
  KEY `clusterBySenderFullName` (`clusterBySenderFullName`(30))
) ENGINE=MyISAM DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

-- Data exporting was unselected.
-- Dumping structure for table bips_new.allmessagesbck
CREATE TABLE IF NOT EXISTS `allmessagesbck` (
  `bip` int(11) DEFAULT NULL,
  `originalbipNumber` int(11) DEFAULT NULL,
  `messageID` int(11) DEFAULT NULL,
  `date2` date DEFAULT NULL,
  `bipType` text,
  `dateTimeStamp` timestamp NULL DEFAULT NULL,
  `email` longtext,
  `analyseWords` longtext,
  `Author` mediumtext,
  `subject` mediumtext,
  `location` text,
  `line` longtext,
  `folder` text,
  `file` text,
  `from` text,
  `emailMessageId` tinytext,
  `wordsList` text,
  `inReplyTo` text,
  `inReplyToUser` text,
  `references` text,
  `link` tinytext,
  `senderName` tinytext,
  `name` tinytext,
  `required` tinyint(4) DEFAULT NULL,
  `hasbipTitleOnly` tinyint(4) DEFAULT NULL,
  `countRepliesToThisMessage` int(11) DEFAULT NULL,
  `fromLine` text,
  `senderemail` tinytext,
  `senderemailProcessed` tinytext,
  `senderEmailFirstSegment` tinytext,
  `senderFullName` tinytext,
  `senderFirstName` tinytext,
  `senderLastName` tinytext,
  `clusterBySenderFullName` tinytext,
  KEY `bip` (`bip`),
  KEY `subject` (`subject`(50)),
  KEY `folder` (`folder`(20)),
  KEY `file` (`file`(15)),
  KEY `senderName` (`senderName`(20)),
  KEY `messageId_Index` (`messageID`),
  KEY `emailMessageId` (`emailMessageId`(80)),
  KEY `inReplyTo` (`inReplyTo`(80)),
  KEY `senderFullName` (`senderFullName`(40)),
  KEY `senderemail` (`senderemail`(40)),
  KEY `senderEmailFirstSegment` (`senderEmailFirstSegment`(20)),
  KEY `senderFirstName` (`senderFirstName`(15)),
  KEY `senderLastName` (`senderLastName`(15))
) ENGINE=MyISAM DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

-- Data exporting was unselected.
-- Dumping structure for table bips_new.allmessages_announce_list
CREATE TABLE IF NOT EXISTS `allmessages_announce_list` (
  `bip` int(11) DEFAULT NULL,
  `originalbipNumber` int(11) DEFAULT NULL,
  `messageID` int(11) DEFAULT NULL,
  `date2` date DEFAULT NULL,
  `bipType` text,
  `dateTimeStamp` timestamp NULL DEFAULT NULL,
  `email` longtext,
  `analyseWords` longtext,
  `Author` mediumtext,
  `subject` mediumtext,
  `location` text,
  `line` longtext,
  `folder` text,
  `file` text,
  `from` text,
  `emailMessageId` tinytext,
  `wordsList` text,
  `inReplyTo` text,
  `inReplyToUser` text,
  `references` text,
  `link` tinytext,
  `senderName` tinytext,
  `name` tinytext,
  `required` tinyint(4) DEFAULT NULL,
  `hasbipTitleOnly` tinyint(4) DEFAULT NULL,
  `countRepliesToThisMessage` int(11) DEFAULT NULL,
  `fromLine` text,
  `senderemail` tinytext,
  `senderemailProcessed` tinytext,
  `senderEmailFirstSegment` tinytext,
  `senderFullName` tinytext,
  `senderFirstName` tinytext,
  `senderLastName` tinytext,
  `clusterBySenderFullName` tinytext,
  `inReplyToUserUsingClusteredSender` tinytext,
  KEY `bip` (`bip`),
  KEY `subject` (`subject`(50)),
  KEY `folder` (`folder`(20)),
  KEY `file` (`file`(15)),
  KEY `senderName` (`senderName`(20)),
  KEY `messageId_Index` (`messageID`),
  KEY `emailMessageId` (`emailMessageId`(80)),
  KEY `inReplyTo` (`inReplyTo`(80)),
  KEY `senderFullName` (`senderFullName`(40)),
  KEY `senderemail` (`senderemail`(40)),
  KEY `senderEmailFirstSegment` (`senderEmailFirstSegment`(20)),
  KEY `senderFirstName` (`senderFirstName`(15)),
  KEY `senderLastName` (`senderLastName`(15)),
  KEY `inReplyToUserUsingClusteredSender` (`inReplyToUserUsingClusteredSender`(30)),
  KEY `clusterBySenderFullName` (`clusterBySenderFullName`(30))
) ENGINE=MyISAM DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

-- Data exporting was unselected.
-- Dumping structure for table bips_new.allmessages_authors
CREATE TABLE IF NOT EXISTS `allmessages_authors` (
  `bip` int(11) DEFAULT NULL,
  `originalbipNumber` int(11) DEFAULT NULL,
  `messageID` int(11) DEFAULT NULL,
  `date2` date DEFAULT NULL,
  `bipType` text,
  `dateTimeStamp` timestamp NULL DEFAULT NULL,
  `email` longtext,
  `analyseWords` longtext,
  `Author` mediumtext,
  `subject` mediumtext,
  `location` text,
  `line` longtext,
  `folder` text,
  `file` text,
  `from` text,
  `emailMessageId` tinytext,
  `wordsList` text,
  `inReplyTo` text,
  `inReplyToUser` text,
  `references` text,
  `link` tinytext,
  `senderName` tinytext,
  `name` tinytext,
  `required` tinyint(4) DEFAULT NULL,
  `hasbipTitleOnly` tinyint(4) DEFAULT NULL,
  `countRepliesToThisMessage` int(11) DEFAULT NULL,
  `fromLine` text,
  `senderemail` tinytext,
  `senderemailProcessed` tinytext,
  `senderEmailFirstSegment` tinytext,
  `senderFullName` tinytext,
  `senderFirstName` tinytext,
  `senderLastName` tinytext,
  `clusterBySenderFullName` tinytext,
  `inReplyToUserUsingClusteredSender` tinytext,
  KEY `bip` (`bip`),
  KEY `subject` (`subject`(50)),
  KEY `folder` (`folder`(20)),
  KEY `file` (`file`(15)),
  KEY `senderName` (`senderName`(20)),
  KEY `messageId_Index` (`messageID`),
  KEY `emailMessageId` (`emailMessageId`(80)),
  KEY `inReplyTo` (`inReplyTo`(80)),
  KEY `senderFullName` (`senderFullName`(40)),
  KEY `senderemail` (`senderemail`(40)),
  KEY `senderEmailFirstSegment` (`senderEmailFirstSegment`(20)),
  KEY `senderFirstName` (`senderFirstName`(15)),
  KEY `senderLastName` (`senderLastName`(15)),
  KEY `inReplyToUserUsingClusteredSender` (`inReplyToUserUsingClusteredSender`(30)),
  KEY `clusterBySenderFullName` (`clusterBySenderFullName`(30))
) ENGINE=MyISAM DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

-- Data exporting was unselected.
-- Dumping structure for table bips_new.allmessages_bugs_list
CREATE TABLE IF NOT EXISTS `allmessages_bugs_list` (
  `bip` int(11) DEFAULT NULL,
  `originalbipNumber` int(11) DEFAULT NULL,
  `messageID` int(11) DEFAULT NULL,
  `date2` date DEFAULT NULL,
  `bipType` text,
  `dateTimeStamp` timestamp NULL DEFAULT NULL,
  `email` longtext,
  `analyseWords` longtext,
  `Author` mediumtext,
  `subject` mediumtext,
  `location` text,
  `line` longtext,
  `folder` text,
  `file` text,
  `from` text,
  `emailMessageId` tinytext,
  `wordsList` text,
  `inReplyTo` text,
  `inReplyToUser` text,
  `references` text,
  `link` tinytext,
  `senderName` tinytext,
  `name` tinytext,
  `required` tinyint(4) DEFAULT NULL,
  `hasbipTitleOnly` tinyint(4) DEFAULT NULL,
  `countRepliesToThisMessage` int(11) DEFAULT NULL,
  `fromLine` text,
  `senderemail` tinytext,
  `senderemailProcessed` tinytext,
  `senderEmailFirstSegment` tinytext,
  `senderFullName` tinytext,
  `senderFirstName` tinytext,
  `senderLastName` tinytext,
  `clusterBySenderFullName` tinytext,
  `inReplyToUserUsingClusteredSender` tinytext,
  KEY `bip` (`bip`),
  KEY `subject` (`subject`(50)),
  KEY `folder` (`folder`(20)),
  KEY `file` (`file`(15)),
  KEY `senderName` (`senderName`(20)),
  KEY `messageId_Index` (`messageID`),
  KEY `emailMessageId` (`emailMessageId`(80)),
  KEY `inReplyTo` (`inReplyTo`(80)),
  KEY `senderFullName` (`senderFullName`(40)),
  KEY `senderemail` (`senderemail`(40)),
  KEY `senderEmailFirstSegment` (`senderEmailFirstSegment`(20)),
  KEY `senderFirstName` (`senderFirstName`(15)),
  KEY `senderLastName` (`senderLastName`(15)),
  KEY `inReplyToUserUsingClusteredSender` (`inReplyToUserUsingClusteredSender`(30)),
  KEY `clusterBySenderFullName` (`clusterBySenderFullName`(30))
) ENGINE=MyISAM DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

-- Data exporting was unselected.
-- Dumping structure for table bips_new.allmessages_checkins
CREATE TABLE IF NOT EXISTS `allmessages_checkins` (
  `bip` int(11) DEFAULT NULL,
  `originalbipNumber` int(11) DEFAULT NULL,
  `messageID` int(11) DEFAULT NULL,
  `date2` date DEFAULT NULL,
  `bipType` text,
  `dateTimeStamp` timestamp NULL DEFAULT NULL,
  `email` longtext,
  `analyseWords` longtext,
  `Author` mediumtext,
  `subject` mediumtext,
  `location` text,
  `line` longtext,
  `folder` text,
  `file` text,
  `from` text,
  `emailMessageId` tinytext,
  `wordsList` text,
  `inReplyTo` text,
  `inReplyToUser` text,
  `references` text,
  `link` tinytext,
  `senderName` tinytext,
  `name` tinytext,
  `required` tinyint(4) DEFAULT NULL,
  `hasbipTitleOnly` tinyint(4) DEFAULT NULL,
  `countRepliesToThisMessage` int(11) DEFAULT NULL,
  `fromLine` text,
  `senderemail` tinytext,
  `senderemailProcessed` tinytext,
  `senderEmailFirstSegment` tinytext,
  `senderFullName` tinytext,
  `senderFirstName` tinytext,
  `senderLastName` tinytext,
  `clusterBySenderFullName` tinytext,
  `inReplyToUserUsingClusteredSender` tinytext,
  KEY `bip` (`bip`),
  KEY `subject` (`subject`(50)),
  KEY `folder` (`folder`(20)),
  KEY `file` (`file`(15)),
  KEY `senderName` (`senderName`(20)),
  KEY `messageId_Index` (`messageID`),
  KEY `emailMessageId` (`emailMessageId`(80)),
  KEY `inReplyTo` (`inReplyTo`(80)),
  KEY `senderFullName` (`senderFullName`(40)),
  KEY `senderemail` (`senderemail`(40)),
  KEY `senderEmailFirstSegment` (`senderEmailFirstSegment`(20)),
  KEY `senderFirstName` (`senderFirstName`(15)),
  KEY `senderLastName` (`senderLastName`(15)),
  KEY `inReplyToUserUsingClusteredSender` (`inReplyToUserUsingClusteredSender`(30)),
  KEY `clusterBySenderFullName` (`clusterBySenderFullName`(30))
) ENGINE=MyISAM DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

-- Data exporting was unselected.
-- Dumping structure for table bips_new.allmessages_committers
CREATE TABLE IF NOT EXISTS `allmessages_committers` (
  `bip` int(11) DEFAULT NULL,
  `originalbipNumber` int(11) DEFAULT NULL,
  `messageID` int(11) DEFAULT NULL,
  `date2` date DEFAULT NULL,
  `bipType` text,
  `dateTimeStamp` timestamp NULL DEFAULT NULL,
  `email` longtext,
  `analyseWords` longtext,
  `Author` mediumtext,
  `subject` mediumtext,
  `location` text,
  `line` longtext,
  `folder` text,
  `file` text,
  `from` text,
  `emailMessageId` tinytext,
  `wordsList` text,
  `inReplyTo` text,
  `inReplyToUser` text,
  `references` text,
  `link` tinytext,
  `senderName` tinytext,
  `name` tinytext,
  `required` tinyint(4) DEFAULT NULL,
  `hasbipTitleOnly` tinyint(4) DEFAULT NULL,
  `countRepliesToThisMessage` int(11) DEFAULT NULL,
  `fromLine` text,
  `senderemail` tinytext,
  `senderemailProcessed` tinytext,
  `senderEmailFirstSegment` tinytext,
  `senderFullName` tinytext,
  `senderFirstName` tinytext,
  `senderLastName` tinytext,
  `clusterBySenderFullName` tinytext,
  `inReplyToUserUsingClusteredSender` tinytext,
  KEY `bip` (`bip`),
  KEY `subject` (`subject`(50)),
  KEY `folder` (`folder`(20)),
  KEY `file` (`file`(15)),
  KEY `senderName` (`senderName`(20)),
  KEY `messageId_Index` (`messageID`),
  KEY `emailMessageId` (`emailMessageId`(80)),
  KEY `inReplyTo` (`inReplyTo`(80)),
  KEY `senderFullName` (`senderFullName`(40)),
  KEY `senderemail` (`senderemail`(40)),
  KEY `senderEmailFirstSegment` (`senderEmailFirstSegment`(20)),
  KEY `senderFirstName` (`senderFirstName`(15)),
  KEY `senderLastName` (`senderLastName`(15)),
  KEY `inReplyToUserUsingClusteredSender` (`inReplyToUserUsingClusteredSender`(30)),
  KEY `clusterBySenderFullName` (`clusterBySenderFullName`(30))
) ENGINE=MyISAM DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

-- Data exporting was unselected.
-- Dumping structure for table bips_new.allmessages_dev
CREATE TABLE IF NOT EXISTS `allmessages_dev` (
  `bip` int(11) DEFAULT NULL,
  `originalbipNumber` int(11) DEFAULT NULL,
  `messageID` int(11) DEFAULT NULL,
  `date2` date DEFAULT NULL,
  `bipType` text,
  `dateTimeStamp` timestamp NULL DEFAULT NULL,
  `email` longtext,
  `analyseWords` longtext,
  `Author` mediumtext,
  `subject` mediumtext,
  `location` text,
  `line` longtext,
  `folder` text,
  `file` text,
  `from` text,
  `emailMessageId` tinytext,
  `wordsList` text,
  `inReplyTo` text,
  `inReplyToUser` text,
  `references` text,
  `link` tinytext,
  `senderName` tinytext,
  `name` tinytext,
  `required` tinyint(4) DEFAULT NULL,
  `hasbipTitleOnly` tinyint(4) DEFAULT NULL,
  `countRepliesToThisMessage` int(11) DEFAULT NULL,
  `fromLine` text,
  `senderemail` tinytext,
  `senderemailProcessed` tinytext,
  `senderEmailFirstSegment` tinytext,
  `senderFullName` tinytext,
  `senderFirstName` tinytext,
  `senderLastName` tinytext,
  `clusterBySenderFullName` tinytext,
  `inReplyToUserUsingClusteredSender` tinytext,
  `IdentifierCount` int(11) DEFAULT NULL,
  KEY `bip` (`bip`),
  KEY `subject` (`subject`(50)),
  KEY `folder` (`folder`(20)),
  KEY `file` (`file`(15)),
  KEY `senderName` (`senderName`(20)),
  KEY `messageId_Index` (`messageID`),
  KEY `emailMessageId` (`emailMessageId`(80)),
  KEY `inReplyTo` (`inReplyTo`(80)),
  KEY `senderFullName` (`senderFullName`(40)),
  KEY `senderemail` (`senderemail`(40)),
  KEY `senderEmailFirstSegment` (`senderEmailFirstSegment`(20)),
  KEY `senderFirstName` (`senderFirstName`(15)),
  KEY `senderLastName` (`senderLastName`(15)),
  KEY `inReplyToUserUsingClusteredSender` (`inReplyToUserUsingClusteredSender`(30)),
  KEY `clusterBySenderFullName` (`clusterBySenderFullName`(30))
) ENGINE=MyISAM DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

-- Data exporting was unselected.
-- Dumping structure for table bips_new.allmessages_distutils_sig
CREATE TABLE IF NOT EXISTS `allmessages_distutils_sig` (
  `bip` int(11) DEFAULT NULL,
  `originalbipNumber` int(11) DEFAULT NULL,
  `messageID` int(11) DEFAULT NULL,
  `date2` date DEFAULT NULL,
  `bipType` text,
  `dateTimeStamp` timestamp NULL DEFAULT NULL,
  `email` longtext,
  `analyseWords` longtext,
  `Author` mediumtext,
  `subject` mediumtext,
  `location` text,
  `line` longtext,
  `folder` text,
  `file` text,
  `from` text,
  `emailMessageId` tinytext,
  `wordsList` text,
  `inReplyTo` text,
  `inReplyToUser` text,
  `references` text,
  `link` tinytext,
  `senderName` tinytext,
  `name` tinytext,
  `required` tinyint(4) DEFAULT NULL,
  `hasbipTitleOnly` tinyint(4) DEFAULT NULL,
  `countRepliesToThisMessage` int(11) DEFAULT NULL,
  `fromLine` text,
  `senderemail` tinytext,
  `senderemailProcessed` tinytext,
  `senderEmailFirstSegment` tinytext,
  `senderFullName` tinytext,
  `senderFirstName` tinytext,
  `senderLastName` tinytext,
  `clusterBySenderFullName` tinytext,
  `inReplyToUserUsingClusteredSender` tinytext,
  KEY `bip` (`bip`),
  KEY `subject` (`subject`(50)),
  KEY `folder` (`folder`(20)),
  KEY `file` (`file`(15)),
  KEY `senderName` (`senderName`(20)),
  KEY `messageId_Index` (`messageID`),
  KEY `emailMessageId` (`emailMessageId`(80)),
  KEY `inReplyTo` (`inReplyTo`(80)),
  KEY `senderFullName` (`senderFullName`(40)),
  KEY `senderemail` (`senderemail`(40)),
  KEY `senderEmailFirstSegment` (`senderEmailFirstSegment`(20)),
  KEY `senderFirstName` (`senderFirstName`(15)),
  KEY `senderLastName` (`senderLastName`(15)),
  KEY `inReplyToUserUsingClusteredSender` (`inReplyToUserUsingClusteredSender`(30)),
  KEY `clusterBySenderFullName` (`clusterBySenderFullName`(30))
) ENGINE=MyISAM DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

-- Data exporting was unselected.
-- Dumping structure for table bips_new.allmessages_ideas
CREATE TABLE IF NOT EXISTS `allmessages_ideas` (
  `bip` int(11) DEFAULT NULL,
  `originalbipNumber` int(11) DEFAULT NULL,
  `messageID` int(11) DEFAULT NULL,
  `date2` date DEFAULT NULL,
  `bipType` text,
  `dateTimeStamp` timestamp NULL DEFAULT NULL,
  `email` longtext,
  `analyseWords` longtext,
  `Author` mediumtext,
  `subject` mediumtext,
  `location` text,
  `line` longtext,
  `folder` text,
  `file` text,
  `from` text,
  `emailMessageId` tinytext,
  `wordsList` text,
  `inReplyTo` text,
  `inReplyToUser` text,
  `references` text,
  `link` tinytext,
  `senderName` tinytext,
  `name` tinytext,
  `required` tinyint(4) DEFAULT NULL,
  `hasbipTitleOnly` tinyint(4) DEFAULT NULL,
  `countRepliesToThisMessage` int(11) DEFAULT NULL,
  `fromLine` text,
  `senderemail` tinytext,
  `senderemailProcessed` tinytext,
  `senderEmailFirstSegment` tinytext,
  `senderFullName` tinytext,
  `senderFirstName` tinytext,
  `senderLastName` tinytext,
  `clusterBySenderFullName` tinytext,
  `inReplyToUserUsingClusteredSender` tinytext,
  `IdentifierCount` int(11) DEFAULT NULL,
  KEY `bip` (`bip`),
  KEY `subject` (`subject`(50)),
  KEY `folder` (`folder`(20)),
  KEY `file` (`file`(15)),
  KEY `senderName` (`senderName`(20)),
  KEY `messageId_Index` (`messageID`),
  KEY `emailMessageId` (`emailMessageId`(80)),
  KEY `inReplyTo` (`inReplyTo`(80)),
  KEY `senderFullName` (`senderFullName`(40)),
  KEY `senderemail` (`senderemail`(40)),
  KEY `senderEmailFirstSegment` (`senderEmailFirstSegment`(20)),
  KEY `senderFirstName` (`senderFirstName`(15)),
  KEY `senderLastName` (`senderLastName`(15)),
  KEY `inReplyToUserUsingClusteredSender` (`inReplyToUserUsingClusteredSender`(30)),
  KEY `clusterBySenderFullName` (`clusterBySenderFullName`(30))
) ENGINE=MyISAM DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

-- Data exporting was unselected.
-- Dumping structure for table bips_new.allmessages_lists
CREATE TABLE IF NOT EXISTS `allmessages_lists` (
  `bip` int(11) DEFAULT NULL,
  `originalbipNumber` int(11) DEFAULT NULL,
  `messageID` int(11) DEFAULT NULL,
  `date2` date DEFAULT NULL,
  `bipType` text,
  `dateTimeStamp` timestamp NULL DEFAULT NULL,
  `email` longtext,
  `analyseWords` longtext,
  `Author` mediumtext,
  `subject` mediumtext,
  `location` text,
  `line` longtext,
  `folder` text,
  `file` text,
  `from` text,
  `emailMessageId` tinytext,
  `wordsList` text,
  `inReplyTo` text,
  `inReplyToUser` text,
  `references` text,
  `link` tinytext,
  `senderName` tinytext,
  `name` tinytext,
  `required` tinyint(4) DEFAULT NULL,
  `hasbipTitleOnly` tinyint(4) DEFAULT NULL,
  `countRepliesToThisMessage` int(11) DEFAULT NULL,
  `fromLine` text,
  `senderemail` tinytext,
  `senderemailProcessed` tinytext,
  `senderEmailFirstSegment` tinytext,
  `senderFullName` tinytext,
  `senderFirstName` tinytext,
  `senderLastName` tinytext,
  `clusterBySenderFullName` tinytext,
  `inReplyToUserUsingClusteredSender` tinytext,
  KEY `bip` (`bip`),
  KEY `subject` (`subject`(50)),
  KEY `folder` (`folder`(20)),
  KEY `file` (`file`(15)),
  KEY `senderName` (`senderName`(20)),
  KEY `messageId_Index` (`messageID`),
  KEY `emailMessageId` (`emailMessageId`(80)),
  KEY `inReplyTo` (`inReplyTo`(80)),
  KEY `senderFullName` (`senderFullName`(40)),
  KEY `senderemail` (`senderemail`(40)),
  KEY `senderEmailFirstSegment` (`senderEmailFirstSegment`(20)),
  KEY `senderFirstName` (`senderFirstName`(15)),
  KEY `senderLastName` (`senderLastName`(15)),
  KEY `inReplyToUserUsingClusteredSender` (`inReplyToUserUsingClusteredSender`(30)),
  KEY `clusterBySenderFullName` (`clusterBySenderFullName`(30))
) ENGINE=MyISAM DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

-- Data exporting was unselected.
-- Dumping structure for table bips_new.allmessages_patches
CREATE TABLE IF NOT EXISTS `allmessages_patches` (
  `bip` int(11) DEFAULT NULL,
  `originalbipNumber` int(11) DEFAULT NULL,
  `messageID` int(11) DEFAULT NULL,
  `date2` date DEFAULT NULL,
  `bipType` text,
  `dateTimeStamp` timestamp NULL DEFAULT NULL,
  `email` longtext,
  `analyseWords` longtext,
  `Author` mediumtext,
  `subject` mediumtext,
  `location` text,
  `line` longtext,
  `folder` text,
  `file` text,
  `from` text,
  `emailMessageId` tinytext,
  `wordsList` text,
  `inReplyTo` text,
  `inReplyToUser` text,
  `references` text,
  `link` tinytext,
  `senderName` tinytext,
  `name` tinytext,
  `required` tinyint(4) DEFAULT NULL,
  `hasbipTitleOnly` tinyint(4) DEFAULT NULL,
  `countRepliesToThisMessage` int(11) DEFAULT NULL,
  `fromLine` text,
  `senderemail` tinytext,
  `senderemailProcessed` tinytext,
  `senderEmailFirstSegment` tinytext,
  `senderFullName` tinytext,
  `senderFirstName` tinytext,
  `senderLastName` tinytext,
  `clusterBySenderFullName` tinytext,
  `inReplyToUserUsingClusteredSender` tinytext,
  KEY `bip` (`bip`),
  KEY `subject` (`subject`(50)),
  KEY `folder` (`folder`(20)),
  KEY `file` (`file`(15)),
  KEY `senderName` (`senderName`(20)),
  KEY `messageId_Index` (`messageID`),
  KEY `emailMessageId` (`emailMessageId`(80)),
  KEY `inReplyTo` (`inReplyTo`(80)),
  KEY `senderFullName` (`senderFullName`(40)),
  KEY `senderemail` (`senderemail`(40)),
  KEY `senderEmailFirstSegment` (`senderEmailFirstSegment`(20)),
  KEY `senderFirstName` (`senderFirstName`(15)),
  KEY `senderLastName` (`senderLastName`(15)),
  KEY `inReplyToUserUsingClusteredSender` (`inReplyToUserUsingClusteredSender`(30)),
  KEY `clusterBySenderFullName` (`clusterBySenderFullName`(30))
) ENGINE=MyISAM DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

-- Data exporting was unselected.
-- Dumping structure for table bips_new.allmessages_biptitleonly_copy
CREATE TABLE IF NOT EXISTS `allmessages_biptitleonly_copy` (
  `bip` int(11) DEFAULT NULL,
  `originalbipNumber` int(11) DEFAULT NULL,
  `messageID` int(11) DEFAULT NULL,
  `date2` date DEFAULT NULL,
  `bipType` text,
  `dateTimeStamp` timestamp NULL DEFAULT NULL,
  `email` longtext,
  `analyseWords` longtext,
  `Author` mediumtext,
  `subject` mediumtext,
  `location` text,
  `line` longtext,
  `folder` text,
  `file` text,
  `statusChanged` tinyint(4) DEFAULT NULL,
  `statusFrom` text,
  `statusTo` text,
  `type` text,
  `from` text,
  `emailMessageId` tinytext,
  `wordsList` text,
  `inReplyTo` text,
  `references` text,
  `link` tinytext,
  `senderName` tinytext,
  `required` tinyint(4) DEFAULT NULL,
  `hasbipTitleOnly` tinyint(4) DEFAULT NULL,
  KEY `bip` (`bip`),
  KEY `subject` (`subject`(50)),
  KEY `messageId_Index` (`messageID`),
  KEY `emailMessageId` (`emailMessageId`(80))
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

-- Data exporting was unselected.
-- Dumping structure for table bips_new.allmessages_subjectsonly
CREATE TABLE IF NOT EXISTS `allmessages_subjectsonly` (
  `bip` int(11) DEFAULT NULL,
  `messageid` int(11) DEFAULT NULL,
  `date2` date DEFAULT NULL,
  `subject` mediumtext,
  `state` tinytext
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
-- Dumping structure for table bips_new.allmessages_withbipnumbers
CREATE TABLE IF NOT EXISTS `allmessages_withbipnumbers` (
  `bip` int(11) DEFAULT NULL,
  `originalbipNumber` int(11) DEFAULT NULL,
  `messageID` int(11) DEFAULT NULL,
  `date2` date DEFAULT NULL,
  `bipType` text,
  `dateTimeStamp` timestamp NULL DEFAULT NULL,
  `email` longtext,
  `analyseWords` longtext,
  `Author` mediumtext,
  `subject` mediumtext,
  `location` text,
  `line` longtext,
  `folder` text,
  `file` text,
  `from` text,
  `emailMessageId` tinytext,
  `wordsList` text,
  `inReplyTo` text,
  `inReplyToUser` text,
  `references` text,
  `link` tinytext,
  `senderName` tinytext,
  `name` tinytext,
  `required` tinyint(4) DEFAULT NULL,
  `hasbipTitleOnly` tinyint(4) DEFAULT NULL,
  `countRepliesToThisMessage` int(11) DEFAULT NULL,
  `fromLine` text,
  `senderemail` tinytext,
  `senderemailProcessed` tinytext,
  `senderEmailFirstSegment` tinytext,
  `senderFullName` tinytext,
  `senderFirstName` tinytext,
  `senderLastName` tinytext,
  `clusterBySenderFullName` tinytext,
  `inReplyToUserUsingClusteredSender` tinytext
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
-- Dumping structure for table bips_new.allbipnumbers_b4clustering
CREATE TABLE IF NOT EXISTS `allbipnumbers_b4clustering` (
  `bip` int(11) DEFAULT NULL,
  `messageID` int(11) DEFAULT NULL,
  `date2` date DEFAULT NULL,
  `bipType` text,
  `dateTimeStamp` timestamp NULL DEFAULT NULL,
  `email` longtext,
  `analyseWords` longtext,
  `Author` mediumtext,
  `subject` mediumtext,
  `location` text,
  `line` longtext,
  `folder` text,
  `file` text,
  `statusChanged` tinyint(4) DEFAULT NULL,
  `statusFrom` text,
  `statusTo` text,
  `type` text,
  `from` text,
  `emailMessageId` tinytext,
  `wordsList` text,
  `inReplyTo` text,
  `inReplyToUser` text,
  `references` text,
  `link` tinytext,
  `senderName` tinytext,
  `name` tinytext,
  `required` tinyint(4) DEFAULT NULL,
  `hasbipTitleOnly` tinyint(4) DEFAULT NULL,
  KEY `bip` (`bip`),
  KEY `subject` (`subject`(50)),
  KEY `folder` (`folder`(20)),
  KEY `file` (`file`(15)),
  KEY `senderName` (`senderName`(20)),
  KEY `messageId_Index` (`messageID`),
  KEY `emailMessageId` (`emailMessageId`(80))
) ENGINE=MyISAM DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

-- Data exporting was unselected.
-- Dumping structure for table bips_new.allbips_ideasbiptitles
CREATE TABLE IF NOT EXISTS `allbips_ideasbiptitles` (
  `date` text,
  `location` text,
  `line` longtext,
  `email` longtext,
  `id` int(11) DEFAULT '0',
  `bip` int(11) DEFAULT NULL,
  `date2` date DEFAULT NULL,
  `bipType` text,
  `folder` text,
  `file` text,
  `statusChanged` tinyint(4) DEFAULT NULL,
  `statusFrom` text,
  `statusTo` text,
  `Author` mediumtext,
  `type` text,
  `from` text,
  `messageID` int(11) DEFAULT NULL,
  `wordsList` text,
  `inReplyTo` text,
  `references` text,
  `emailMessageId` tinytext,
  `link` tinytext,
  `analyseWords` longtext,
  `subject` mediumtext,
  `senderName` tinytext,
  `required` tinyint(4) DEFAULT NULL,
  `pythonarchive` tinyint(4) DEFAULT NULL,
  `gmane` tinyint(4) DEFAULT NULL,
  `md5` text,
  `sha1` text,
  `sha2` text,
  KEY `bip` (`bip`),
  KEY `subject` (`subject`(50)),
  KEY `messageId_Index` (`messageID`),
  KEY `emailMessageId` (`emailMessageId`(80))
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

-- Data exporting was unselected.
-- Dumping structure for table bips_new.authormatching
CREATE TABLE IF NOT EXISTS `authormatching` (
  `sendername` tinytext,
  `match` tinytext,
  `true` bit(1) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
-- Dumping structure for table bips_new.causalrelations
CREATE TABLE IF NOT EXISTS `causalrelations` (
  `arg1` text,
  `relation` text,
  `arg2` text,
  `sentence` text,
  `bip` int(11) DEFAULT NULL,
  `messageID` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

-- Data exporting was unselected.
-- Dumping structure for procedure bips_new.ClusterEmailSenders
DELIMITER //
CREATE DEFINER=`root`@`localhost` PROCEDURE `ClusterEmailSenders`(IN EMAIL VARCHAR(255))
BEGIN
 	DECLARE maxID INT;
 
 SELECT * FROM distinctsenders WHERE emailAddress = 'pankaj@gmail.com';
 
 SELECT max(cluster) from distinctsenders INTO maxID;
 SET maxID = MAXid+1;
 
 UPDATE distinctsenders SET clustered = 1, cluster = maxID WHERE emailAddress = 'pankaj@gmail.com';
 
 END//
DELIMITER ;

-- Dumping structure for table bips_new.clustersdevsenders
CREATE TABLE IF NOT EXISTS `clustersdevsenders` (
  `id` int(11) DEFAULT NULL,
  `emailaddress` tinytext,
  `senderName` tinytext,
  `totalMessageCount` int(11) DEFAULT NULL,
  `senderFirstName` tinytext,
  `senderLastName` tinytext
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

-- Data exporting was unselected.
-- Dumping structure for table bips_new.coredevelopers
CREATE TABLE IF NOT EXISTS `coredevelopers` (
  `coredeveloper` tinytext
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
-- Dumping structure for table bips_new.danieldata
CREATE TABLE IF NOT EXISTS `danieldata` (
  `bip` int(11) DEFAULT NULL,
  `messageID` int(11) DEFAULT NULL,
  `date` date DEFAULT NULL,
  `yearDate2` int(11) DEFAULT NULL,
  `dateTime` datetime DEFAULT NULL,
  `dateTimeStamp` timestamp NULL DEFAULT NULL,
  `email` longtext,
  `analyseWords` longtext,
  `Author` mediumtext,
  `subject` mediumtext,
  `location` text,
  `line` longtext,
  `folder` text,
  `file` text,
  `statusChanged` tinyint(4) DEFAULT NULL,
  `statusFrom` text,
  `statusTo` text,
  `type` text,
  `from` text,
  `emailMessageId` tinytext,
  `wordsList` text,
  `inReplyTo` text,
  `references` text,
  `link` tinytext,
  `senderName` tinytext,
  `required` tinyint(4) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

-- Data exporting was unselected.
-- Dumping structure for table bips_new.distinctauthorbipnumbers
CREATE TABLE IF NOT EXISTS `distinctauthorbipnumbers` (
  `senderName` tinytext,
  `allPostsByThisAuthor` int(11) DEFAULT NULL,
  `allUniquePostsByThisAuthor` int(11) DEFAULT NULL,
  `allPostsByThisAuthorInInformationalbips` int(11) DEFAULT NULL,
  `allPostsByThisAuthorInProcessbips` int(11) DEFAULT NULL,
  `allPostsByThisAuthorInStandardbips` int(11) DEFAULT NULL,
  `allPostsByThisAuthorInTotalUniquebips` int(11) DEFAULT NULL,
  `allPostsByThisAuthorInUniqueStandardbips` int(11) DEFAULT NULL,
  `allPostsByThisAuthorInUniqueProcessbips` int(11) DEFAULT NULL,
  `allPostsByThisAuthorInUniqueInformationalbips` int(11) DEFAULT NULL,
  `allPostsByThisAuthorInUniquebipsEqualToNULL` int(11) DEFAULT NULL,
  `allPostsByThisAuthorInbipTypeEqualToNULL` int(11) DEFAULT NULL,
  `allUniquePostsByThisAuthorInStandardbips` int(11) DEFAULT NULL,
  `allUniquePostsByThisAuthorInProcessbips` int(11) DEFAULT NULL,
  `allUniquePostsByThisAuthorInInformationalbips` int(11) DEFAULT NULL,
  `totalNumberOfUniqueNULLbipTypeInWhichAuthorPosted` int(11) DEFAULT NULL,
  `totalNumberOfUniqueStandardbipTypeInWhichAuthorPosted` int(11) DEFAULT NULL,
  `totalNumberOfUniqueProcessbipTypeInWhichAuthorPosted` int(11) DEFAULT NULL,
  `totalNumberOfUniqueInformationalbipTypeInWhichAuthorPosted` int(11) DEFAULT NULL,
  `allSeedingPostsByThisAuthor` int(11) DEFAULT NULL,
  `maxPostsInAbipByThisAuthor` int(11) DEFAULT NULL,
  `minPostsInAbipByThisAuthor` int(11) DEFAULT NULL,
  `firstPostDate` date DEFAULT NULL,
  `lastPostDate` date DEFAULT NULL,
  `YearFirstPostDate` int(11) DEFAULT NULL,
  `YearLastPostDate` int(11) DEFAULT NULL,
  `daysTillLastPost` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
-- Dumping structure for table bips_new.distinctauthorsallmessages
CREATE TABLE IF NOT EXISTS `distinctauthorsallmessages` (
  `senderName` tinytext,
  `allPostsByThisAuthor` int(11) DEFAULT NULL,
  `allPostsByThisAuthorInInformationalbips` int(11) DEFAULT NULL,
  `allPostsByThisAuthorInProcessbips` int(11) DEFAULT NULL,
  `allPostsByThisAuthorInStandardbips` int(11) DEFAULT NULL,
  `allPostsByThisAuthorInUniquebips` int(11) DEFAULT NULL,
  `allSeedingPostsByThisAuthor` int(11) DEFAULT NULL,
  `maxPostsInAbipByThisAuthor` int(11) DEFAULT NULL,
  `minPostsInAbipByThisAuthor` int(11) DEFAULT NULL,
  `firstPostDate` date DEFAULT NULL,
  `lastPostDate` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
-- Dumping structure for table bips_new.distinctauthorsbipnumbers_oldbeforeclustering
CREATE TABLE IF NOT EXISTS `distinctauthorsbipnumbers_oldbeforeclustering` (
  `senderName` tinytext,
  `allPostsByThisAuthor` int(11) DEFAULT NULL,
  `allPostsByThisAuthorInInformationalbips` int(11) DEFAULT NULL,
  `allPostsByThisAuthorInProcessbips` int(11) DEFAULT NULL,
  `allPostsByThisAuthorInStandardbips` int(11) DEFAULT NULL,
  `allPostsByThisAuthorInTotalUniquebips` int(11) DEFAULT NULL,
  `allPostsByThisAuthorInUniqueStandardbips` int(11) DEFAULT NULL,
  `allPostsByThisAuthorInUniqueProcessbips` int(11) DEFAULT NULL,
  `allPostsByThisAuthorInUniqueInformationalbips` int(11) DEFAULT NULL,
  `allPostsByThisAuthorInUniquebipsEqualToNULL` int(11) DEFAULT NULL,
  `allSeedingPostsByThisAuthor` int(11) DEFAULT NULL,
  `maxPostsInAbipByThisAuthor` int(11) DEFAULT NULL,
  `minPostsInAbipByThisAuthor` int(11) DEFAULT NULL,
  `firstPostDate` date DEFAULT NULL,
  `lastPostDate` date DEFAULT NULL,
  `YearFirstPostDate` int(11) DEFAULT NULL,
  `YearLastPostDate` int(11) DEFAULT NULL,
  `daysTillLastPost` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
-- Dumping structure for view bips_new.distinctclusters
-- Creating temporary table to overcome VIEW dependency errors
CREATE TABLE `distinctclusters` (
	`cluster` INT(11) NULL,
	`members` BIGINT(21) NOT NULL
) ENGINE=MyISAM;

-- Dumping structure for table bips_new.distinctdevsenders
CREATE TABLE IF NOT EXISTS `distinctdevsenders` (
  `id` int(11) DEFAULT NULL,
  `emailaddress` tinytext,
  `senderName` tinytext,
  `totalMessageCount` int(11) DEFAULT NULL,
  `senderFirstName` tinytext,
  `senderLastName` tinytext,
  `cluster` int(11) DEFAULT NULL,
  `clustered` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
-- Dumping structure for table bips_new.distinctsenders
CREATE TABLE IF NOT EXISTS `distinctsenders` (
  `id` int(11) NOT NULL,
  `emailaddress` tinytext,
  `senderName` tinytext,
  `totalSenderCount` int(11) DEFAULT NULL,
  `totalMessageCount` int(11) DEFAULT NULL,
  `senderFirstName` tinytext,
  `senderLastName` tinytext,
  `senderEmailFirstSegment` tinytext,
  `cluster` int(11) DEFAULT NULL,
  `clustered` int(11) DEFAULT NULL,
  `clusteredBySenderName` int(11) DEFAULT NULL,
  `clusterBySenderName` int(11) DEFAULT NULL,
  `reflected` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `emailaddress` (`emailaddress`(20)),
  KEY `senderName` (`senderName`(30)),
  KEY `senderFirstName` (`senderFirstName`(30)),
  KEY `senderLastName` (`senderLastName`(30)),
  KEY `senderEmailFirstSegment` (`senderEmailFirstSegment`(30)),
  KEY `cluster` (`cluster`),
  KEY `clusteredBySenderName` (`clusteredBySenderName`),
  KEY `clusterBySenderName` (`clusterBySenderName`),
  KEY `reflected` (`reflected`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
-- Dumping structure for table bips_new.distinctsendersfirstname
CREATE TABLE IF NOT EXISTS `distinctsendersfirstname` (
  `SENDERFIRSTNAME` tinytext,
  KEY `firstname` (`SENDERFIRSTNAME`(20))
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
-- Dumping structure for table bips_new.distinctsenders_4_junealldone
CREATE TABLE IF NOT EXISTS `distinctsenders_4_junealldone` (
  `id` int(11) NOT NULL,
  `emailaddress` tinytext,
  `senderName` tinytext,
  `totalSenderCount` int(11) DEFAULT NULL,
  `totalMessageCount` int(11) DEFAULT NULL,
  `senderFirstName` tinytext,
  `senderLastName` tinytext,
  `senderEmailFirstSegment` tinytext,
  `cluster` int(11) DEFAULT NULL,
  `clustered` int(11) DEFAULT NULL,
  `clusteredBySenderName` int(11) DEFAULT NULL,
  `clusterBySenderName` int(11) DEFAULT NULL,
  `reflected` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `emailaddress` (`emailaddress`(20)),
  KEY `senderName` (`senderName`(30)),
  KEY `senderFirstName` (`senderFirstName`(30)),
  KEY `senderLastName` (`senderLastName`(30)),
  KEY `senderEmailFirstSegment` (`senderEmailFirstSegment`(30)),
  KEY `cluster` (`cluster`),
  KEY `clusteredBySenderName` (`clusteredBySenderName`),
  KEY `clusterBySenderName` (`clusterBySenderName`),
  KEY `reflected` (`reflected`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

-- Data exporting was unselected.
-- Dumping structure for table bips_new.distinctsenders_copyall_done
CREATE TABLE IF NOT EXISTS `distinctsenders_copyall_done` (
  `id` int(11) NOT NULL,
  `emailaddress` tinytext,
  `senderName` tinytext,
  `totalSenderCount` int(11) DEFAULT NULL,
  `totalMessageCount` int(11) DEFAULT NULL,
  `senderFirstName` tinytext,
  `senderLastName` tinytext,
  `senderEmailFirstSegment` tinytext,
  `cluster` int(11) DEFAULT NULL,
  `clustered` int(11) DEFAULT NULL,
  `clusteredBySenderName` int(11) DEFAULT NULL,
  `clusterBySenderName` int(11) DEFAULT NULL,
  `reflected` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `emailaddress` (`emailaddress`(20)),
  KEY `senderName` (`senderName`(30)),
  KEY `senderFirstName` (`senderFirstName`(30)),
  KEY `senderLastName` (`senderLastName`(30)),
  KEY `senderEmailFirstSegment` (`senderEmailFirstSegment`(30)),
  KEY `cluster` (`cluster`),
  KEY `clusteredBySenderName` (`clusteredBySenderName`),
  KEY `clusterBySenderName` (`clusterBySenderName`),
  KEY `reflected` (`reflected`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

-- Data exporting was unselected.
-- Dumping structure for view bips_new.duplicatemessageid
-- Creating temporary table to overcome VIEW dependency errors
CREATE TABLE `duplicatemessageid` (
	`bip` INT(11) NOT NULL,
	`messageid` INT(11) NULL,
	`timestamp` TIMESTAMP NULL,
	`NumOccurrences` BIGINT(21) NOT NULL
) ENGINE=MyISAM;

-- Dumping structure for table bips_new.errors
CREATE TABLE IF NOT EXISTS `errors` (
  `messageid` int(11) DEFAULT NULL,
  `AUTHOR` mediumtext,
  `senderName` tinytext,
  `emailmessageid` tinytext,
  `inreplyto` text,
  `inreplytouser` text
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
-- Dumping structure for table bips_new.errors2
CREATE TABLE IF NOT EXISTS `errors2` (
  `messageid` int(11) DEFAULT NULL,
  `AUTHOR` mediumtext,
  `senderName` tinytext,
  `emailmessageid` tinytext,
  `inreplyto` text,
  `inreplytouser` text
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
-- Dumping structure for table bips_new.ideatitles_matching
CREATE TABLE IF NOT EXISTS `ideatitles_matching` (
  `cluster` int(11) DEFAULT NULL,
  `editcluster` int(11) DEFAULT NULL,
  `bip` int(11) DEFAULT NULL,
  `messageID` int(11) DEFAULT NULL,
  `originalMessageSubject` text,
  `processedSubject` text,
  `author` text,
  `email` longtext
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
-- Dumping structure for table bips_new.keywordmatching
CREATE TABLE IF NOT EXISTS `keywordmatching` (
  `bip` int(11) DEFAULT NULL,
  `messageID` int(11) DEFAULT NULL,
  `label` text,
  `subject` text,
  `relation` text,
  `object` text,
  `ps` text,
  `currentSentence` text,
  `ns` text,
  `pp` longtext,
  `ep` longtext,
  `np` longtext,
  `message_subject` text,
  `author` text,
  KEY `keywords_bip` (`bip`),
  KEY `label` (`label`(15))
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

-- Data exporting was unselected.
-- Dumping structure for table bips_new.keywords_twotermsmatched
CREATE TABLE IF NOT EXISTS `keywords_twotermsmatched` (
  `bip` int(11) DEFAULT NULL,
  `messageID` int(11) DEFAULT NULL,
  `label` text,
  `subject` text,
  `relation` text,
  `object` text,
  `ps` text,
  `currentSentence` text,
  `ns` text,
  `email` longtext,
  `pp` longtext,
  `ep` longtext,
  `np` longtext,
  `message_subject` text,
  `author` text,
  KEY `keywords_bip` (`bip`),
  KEY `label` (`label`(15))
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

-- Data exporting was unselected.
-- Dumping structure for table bips_new.labels
CREATE TABLE IF NOT EXISTS `labels` (
  `lineNumber` int(11) DEFAULT NULL,
  `comment` tinytext,
  `idea` tinytext,
  `subject` tinytext,
  `verb` tinytext,
  `object` tinytext,
  `sentence` text
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
-- Dumping structure for table bips_new.labelslesserstatesreasonlabels
CREATE TABLE IF NOT EXISTS `labelslesserstatesreasonlabels` (
  `Label` text,
  `LesserState` text,
  `Reason` text
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
-- Dumping structure for function bips_new.levenshtein_distance
DELIMITER //
CREATE DEFINER=`root`@`localhost` FUNCTION `levenshtein_distance`(s1 VARCHAR(255), s2 VARCHAR(255)) RETURNS int(11)
    DETERMINISTIC
BEGIN

  DECLARE s1_len, s2_len, i, j, c, c_temp, cost INT;

  DECLARE s1_char CHAR;

  DECLARE cv0, cv1 VARBINARY(256);

  SET s1_len = CHAR_LENGTH(s1), s2_len = CHAR_LENGTH(s2), cv1 = '\0', j = 1, i = 1, c = 0;

  IF s1 = s2 THEN

    RETURN 0;

  ELSEIF s1_len = 0 THEN

    RETURN s2_len;

  ELSEIF s2_len = 0 THEN

    RETURN s1_len;

  ELSE

    WHILE j <= s2_len DO

      SET cv1 = CONCAT(cv1, UNHEX(HEX(j))), j = j + 1;

    END WHILE;

    WHILE i <= s1_len DO

      SET s1_char = SUBSTRING(s1, i, 1), c = i, cv0 = UNHEX(HEX(i)), j = 1;

      WHILE j <= s2_len DO

        SET c = c + 1;

        IF s1_char = SUBSTRING(s2, j, 1) THEN SET cost = 0; ELSE SET cost = 1; END IF;

        SET c_temp = CONV(HEX(SUBSTRING(cv1, j, 1)), 16, 10) + cost;

        IF c > c_temp THEN SET c = c_temp; END IF;

        SET c_temp = CONV(HEX(SUBSTRING(cv1, j+1, 1)), 16, 10) + 1;

        IF c > c_temp THEN SET c = c_temp; END IF;

        SET cv0 = CONCAT(cv0, UNHEX(HEX(c))), j = j + 1;

      END WHILE;

      SET cv1 = cv0, i = i + 1;

    END WHILE;

  END IF;

  RETURN c;

END//
DELIMITER ;

-- Dumping structure for table bips_new.links
CREATE TABLE IF NOT EXISTS `links` (
  `link` text,
  `checked` int(11) DEFAULT NULL,
  `containsPython` tinyint(4) DEFAULT NULL,
  `checkedForPython` tinyint(4) DEFAULT NULL,
  KEY `link` (`link`(50))
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

-- Data exporting was unselected.
-- Dumping structure for table bips_new.martin
CREATE TABLE IF NOT EXISTS `martin` (
  `messageid` int(11) DEFAULT NULL,
  `sendername` tinytext,
  `firstline` longtext
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
-- Dumping structure for table bips_new.memberpostcount
CREATE TABLE IF NOT EXISTS `memberpostcount` (
  `clusterBySenderFullName` tinytext,
  `folder` tinytext,
  `postCount` int(11) DEFAULT NULL,
  `subjects` int(11) DEFAULT NULL,
  `bips` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
-- Dumping structure for table bips_new.memberpostcount_all
CREATE TABLE IF NOT EXISTS `memberpostcount_all` (
  `clusterBySenderFullName` tinytext,
  `folder` tinytext,
  `postCount` int(11) DEFAULT NULL,
  `subjects` int(11) DEFAULT NULL,
  `bips` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

-- Data exporting was unselected.
-- Dumping structure for table bips_new.memberpostcount_allcrosscutting
CREATE TABLE IF NOT EXISTS `memberpostcount_allcrosscutting` (
  `clusterBySenderFullName` tinytext,
  `folder` tinytext,
  `postCount` int(11) DEFAULT NULL,
  `subjects` int(11) DEFAULT NULL,
  `bips` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

-- Data exporting was unselected.
-- Dumping structure for table bips_new.bipdetails
CREATE TABLE IF NOT EXISTS `bipdetails` (
  `bip` int(11) DEFAULT NULL,
  `title` text,
  `author` text,
  `authorCorrected` text,
  `type` text,
  `bdfl_delegate` text,
  `bdfl_delegateCorrected` text,
  `created` date DEFAULT NULL,
  `createdYear` int(11) DEFAULT NULL,
  `bipSummary` text,
  `python_version` text,
  KEY `bip_index` (`bip`),
  KEY `title` (`title`(50))
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

-- Data exporting was unselected.
-- Dumping structure for table bips_new.bipdetails_authorcount
CREATE TABLE IF NOT EXISTS `bipdetails_authorcount` (
  `author` tinytext
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
-- Dumping structure for table bips_new.bipdetails_titlesforeachbip
CREATE TABLE IF NOT EXISTS `bipdetails_titlesforeachbip` (
  `bip` int(11) DEFAULT NULL,
  `additional_titles` tinytext,
  `original_title_in_ideaslist` tinytext
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
-- Dumping structure for table bips_new.bipdetails_withoutcreateddate
CREATE TABLE IF NOT EXISTS `bipdetails_withoutcreateddate` (
  `bip` int(11) DEFAULT NULL,
  `title` text,
  `author` text,
  `authorCorrected` text,
  `type` text,
  `bdfl_delegate` text,
  `bdfl_delegateCorrected` text,
  `created` date DEFAULT NULL,
  `bipSummary` text,
  `python_version` text,
  KEY `bip_index` (`bip`),
  KEY `title` (`title`(50))
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

-- Data exporting was unselected.
-- Dumping structure for table bips_new.bipstates
CREATE TABLE IF NOT EXISTS `bipstates` (
  `bip` int(11) DEFAULT NULL,
  `bipTitle` tinytext,
  `entireMessage` text,
  `messageID` int(11) DEFAULT NULL,
  `date2` date DEFAULT NULL,
  `bipType` text,
  `dateTimeStamp` timestamp NULL DEFAULT NULL,
  `email` longtext,
  `analyseWords` longtext,
  `Author` mediumtext,
  `subject` mediumtext,
  `location` text,
  `line` longtext,
  `folder` text,
  `file` text,
  `from` text,
  `emailMessageId` tinytext,
  `wordsList` text,
  `inReplyTo` text,
  `inReplyToUser` text,
  `references` text,
  `link` tinytext,
  `senderName` tinytext,
  `name` tinytext,
  `required` tinyint(4) DEFAULT NULL,
  `hasbipTitleOnly` tinyint(4) DEFAULT NULL,
  `countRepliesToThisMessage` int(11) DEFAULT NULL,
  `fromLine` text,
  `senderemail` tinytext,
  `senderemailProcessed` tinytext,
  `senderEmailFirstSegment` tinytext,
  `senderFullName` tinytext,
  `senderFirstName` tinytext,
  `senderLastName` tinytext,
  `clusterBySenderFullName` tinytext,
  `inReplyToUserUsingClusteredSender` tinytext,
  `statusFrom` tinytext,
  `statusTo` tinytext,
  `statusChanged` int(11) DEFAULT NULL,
  KEY `bip` (`bip`),
  KEY `subject` (`subject`(50)),
  KEY `folder` (`folder`(20)),
  KEY `file` (`file`(15)),
  KEY `senderName` (`senderName`(20)),
  KEY `messageId_Index` (`messageID`),
  KEY `emailMessageId` (`emailMessageId`(80)),
  KEY `inReplyTo` (`inReplyTo`(80)),
  KEY `senderFullName` (`senderFullName`(40)),
  KEY `senderemail` (`senderemail`(40)),
  KEY `senderEmailFirstSegment` (`senderEmailFirstSegment`(20)),
  KEY `senderFirstName` (`senderFirstName`(15)),
  KEY `senderLastName` (`senderLastName`(15)),
  KEY `inReplyToUserUsingClusteredSender` (`inReplyToUserUsingClusteredSender`(30)),
  KEY `clusterBySenderFullName` (`clusterBySenderFullName`(30))
) ENGINE=MyISAM DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

-- Data exporting was unselected.
-- Dumping structure for table bips_new.bipstates_danieldata
CREATE TABLE IF NOT EXISTS `bipstates_danieldata` (
  `bip` int(11) DEFAULT NULL,
  `bipTitle` tinytext,
  `entireMessage` text,
  `messageID` int(11) DEFAULT NULL,
  `date2` date DEFAULT NULL,
  `bipType` text,
  `dateTimeStamp` timestamp NULL DEFAULT NULL,
  `email` longtext,
  `analyseWords` longtext,
  `Author` mediumtext,
  `subject` mediumtext,
  `location` text,
  `line` longtext,
  `folder` text,
  `file` text,
  `from` text,
  `emailMessageId` tinytext,
  `wordsList` text,
  `inReplyTo` text,
  `inReplyToUser` text,
  `references` text,
  `link` tinytext,
  `senderName` tinytext,
  `name` tinytext,
  `required` tinyint(4) DEFAULT NULL,
  `hasbipTitleOnly` tinyint(4) DEFAULT NULL,
  `countRepliesToThisMessage` int(11) DEFAULT NULL,
  `fromLine` text,
  `senderemail` tinytext,
  `senderemailProcessed` tinytext,
  `senderEmailFirstSegment` tinytext,
  `senderFullName` tinytext,
  `senderFirstName` tinytext,
  `senderLastName` tinytext,
  `clusterBySenderFullName` tinytext,
  `inReplyToUserUsingClusteredSender` tinytext,
  `statusFrom` tinytext,
  `statusTo` tinytext,
  `statusChanged` int(11) DEFAULT NULL,
  `IdentifierCount` int(11) DEFAULT NULL,
  KEY `bip` (`bip`),
  KEY `subject` (`subject`(50)),
  KEY `folder` (`folder`(20)),
  KEY `file` (`file`(15)),
  KEY `senderName` (`senderName`(20)),
  KEY `messageId_Index` (`messageID`),
  KEY `emailMessageId` (`emailMessageId`(80)),
  KEY `inReplyTo` (`inReplyTo`(80)),
  KEY `senderFullName` (`senderFullName`(40)),
  KEY `senderemail` (`senderemail`(40)),
  KEY `senderEmailFirstSegment` (`senderEmailFirstSegment`(20)),
  KEY `senderFirstName` (`senderFirstName`(15)),
  KEY `senderLastName` (`senderLastName`(15)),
  KEY `inReplyToUserUsingClusteredSender` (`inReplyToUserUsingClusteredSender`(30)),
  KEY `clusterBySenderFullName` (`clusterBySenderFullName`(30))
) ENGINE=MyISAM DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

-- Data exporting was unselected.
-- Dumping structure for table bips_new.bipstates_danieldata_datetimestamp
CREATE TABLE IF NOT EXISTS `bipstates_danieldata_datetimestamp` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `bip` int(11) DEFAULT NULL,
  `bipTitle` tinytext,
  `entireMessage` text,
  `messageID` int(11) DEFAULT NULL,
  `date2` date DEFAULT NULL,
  `bipType` text,
  `dateTimeStamp` timestamp NULL DEFAULT NULL,
  `email` longtext,
  `analyseWords` longtext,
  `Author` mediumtext,
  `subject` mediumtext,
  `location` text,
  `line` longtext,
  `folder` text,
  `file` text,
  `from` text,
  `emailMessageId` tinytext,
  `wordsList` text,
  `inReplyTo` text,
  `inReplyToUser` text,
  `references` text,
  `link` tinytext,
  `senderName` tinytext,
  `name` tinytext,
  `required` tinyint(4) DEFAULT NULL,
  `hasbipTitleOnly` tinyint(4) DEFAULT NULL,
  `countRepliesToThisMessage` int(11) DEFAULT NULL,
  `fromLine` text,
  `senderemail` tinytext,
  `senderemailProcessed` tinytext,
  `senderEmailFirstSegment` tinytext,
  `senderFullName` tinytext,
  `senderFirstName` tinytext,
  `senderLastName` tinytext,
  `clusterBySenderFullName` tinytext,
  `inReplyToUserUsingClusteredSender` tinytext,
  `statusFrom` tinytext,
  `statusTo` tinytext,
  `statusChanged` int(11) DEFAULT NULL,
  `IdentifierCount` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `bip` (`bip`),
  KEY `subject` (`subject`(50)),
  KEY `folder` (`folder`(20)),
  KEY `file` (`file`(15)),
  KEY `senderName` (`senderName`(20)),
  KEY `messageId_Index` (`messageID`),
  KEY `emailMessageId` (`emailMessageId`(80)),
  KEY `inReplyTo` (`inReplyTo`(80)),
  KEY `senderFullName` (`senderFullName`(40)),
  KEY `senderemail` (`senderemail`(40)),
  KEY `senderEmailFirstSegment` (`senderEmailFirstSegment`(20)),
  KEY `senderFirstName` (`senderFirstName`(15)),
  KEY `senderLastName` (`senderLastName`(15)),
  KEY `inReplyToUserUsingClusteredSender` (`inReplyToUserUsingClusteredSender`(30)),
  KEY `clusterBySenderFullName` (`clusterBySenderFullName`(30))
) ENGINE=MyISAM AUTO_INCREMENT=1809 DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

-- Data exporting was unselected.
-- Dumping structure for table bips_new.bipstates_danieldata_datetimestamp_copy
CREATE TABLE IF NOT EXISTS `bipstates_danieldata_datetimestamp_copy` (
  `bip` int(11) DEFAULT NULL,
  `bipTitle` tinytext,
  `entireMessage` text,
  `messageID` int(11) DEFAULT NULL,
  `date2` date DEFAULT NULL,
  `bipType` text,
  `dateTimeStamp` timestamp NULL DEFAULT NULL,
  `email` longtext,
  `analyseWords` longtext,
  `Author` mediumtext,
  `subject` mediumtext,
  `location` text,
  `line` longtext,
  `folder` text,
  `file` text,
  `from` text,
  `emailMessageId` tinytext,
  `wordsList` text,
  `inReplyTo` text,
  `inReplyToUser` text,
  `references` text,
  `link` tinytext,
  `senderName` tinytext,
  `name` tinytext,
  `required` tinyint(4) DEFAULT NULL,
  `hasbipTitleOnly` tinyint(4) DEFAULT NULL,
  `countRepliesToThisMessage` int(11) DEFAULT NULL,
  `fromLine` text,
  `senderemail` tinytext,
  `senderemailProcessed` tinytext,
  `senderEmailFirstSegment` tinytext,
  `senderFullName` tinytext,
  `senderFirstName` tinytext,
  `senderLastName` tinytext,
  `clusterBySenderFullName` tinytext,
  `inReplyToUserUsingClusteredSender` tinytext,
  `statusFrom` tinytext,
  `statusTo` tinytext,
  `statusChanged` int(11) DEFAULT NULL,
  `IdentifierCount` int(11) DEFAULT NULL,
  KEY `bip` (`bip`),
  KEY `subject` (`subject`(50)),
  KEY `folder` (`folder`(20)),
  KEY `file` (`file`(15)),
  KEY `senderName` (`senderName`(20)),
  KEY `messageId_Index` (`messageID`),
  KEY `emailMessageId` (`emailMessageId`(80)),
  KEY `inReplyTo` (`inReplyTo`(80)),
  KEY `senderFullName` (`senderFullName`(40)),
  KEY `senderemail` (`senderemail`(40)),
  KEY `senderEmailFirstSegment` (`senderEmailFirstSegment`(20)),
  KEY `senderFirstName` (`senderFirstName`(15)),
  KEY `senderLastName` (`senderLastName`(15)),
  KEY `inReplyToUserUsingClusteredSender` (`inReplyToUserUsingClusteredSender`(30)),
  KEY `clusterBySenderFullName` (`clusterBySenderFullName`(30))
) ENGINE=MyISAM DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

-- Data exporting was unselected.
-- Dumping structure for table bips_new.bipstates_danieldata_wide
CREATE TABLE IF NOT EXISTS `bipstates_danieldata_wide` (
  `bip` int(11) DEFAULT NULL,
  `finaltitle` text,
  `titleinFirstState` varchar(255) DEFAULT NULL,
  `titleInActiveState` varchar(255) DEFAULT NULL,
  `titleInDraftState` varchar(255) DEFAULT NULL,
  `titleInAcceptedState` varchar(255) DEFAULT NULL,
  `titleInRejectedState` varchar(255) DEFAULT NULL,
  `titleInFinalState` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
-- Dumping structure for table bips_new.bipstates_wide
CREATE TABLE IF NOT EXISTS `bipstates_wide` (
  `bip` int(11) DEFAULT NULL,
  `finaltitle` text,
  `titleinFirstState` varchar(255) DEFAULT NULL,
  `titleInActiveState` varchar(255) DEFAULT NULL,
  `titleInDraftState` varchar(255) DEFAULT NULL,
  `titleInAcceptedState` varchar(255) DEFAULT NULL,
  `titleInRejectedState` varchar(255) DEFAULT NULL,
  `titleInFinalState` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
-- Dumping structure for table bips_new.reasonlabels
CREATE TABLE IF NOT EXISTS `reasonlabels` (
  `lineNumber` int(11) DEFAULT NULL,
  `comment` tinytext,
  `idea` tinytext,
  `subject` tinytext,
  `verb` tinytext,
  `object` tinytext,
  `sentence` text
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

-- Data exporting was unselected.
-- Dumping structure for table bips_new.relations
CREATE TABLE IF NOT EXISTS `relations` (
  `arg1` text,
  `relation` text,
  `arg2` text,
  `sentence` text,
  `folder` tinytext,
  `mid` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

-- Data exporting was unselected.
-- Dumping structure for table bips_new.relations_clausie
CREATE TABLE IF NOT EXISTS `relations_clausie` (
  `arg1` text,
  `relation` text,
  `arg2` text,
  `sentence` text,
  `bip` int(11) DEFAULT NULL,
  `messageID` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

-- Data exporting was unselected.
-- Dumping structure for table bips_new.results
CREATE TABLE IF NOT EXISTS `results` (
  `bip` int(11) NOT NULL,
  `messageID` int(11) DEFAULT NULL,
  `date` date DEFAULT NULL,
  `dateTimeStamp` timestamp NULL DEFAULT NULL,
  `messageSubject` tinytext,
  `label` text,
  `subject` text,
  `relation` text,
  `object` text,
  `currentSentence` text,
  `clausie` text,
  `clausieInCST` text,
  `ollie` text,
  `reverb` text,
  `author` text,
  `folder` tinytext,
  `file` tinytext,
  `numberOfbipsMentionedInMessage` int(11) DEFAULT NULL,
  `repeatedLabel` text,
  `bipType` tinytext,
  `role` text,
  `allReasons` text,
  `reasonInSentence` text,
  `reasonTermsInMatchedTriple` text,
  `reasonTermsInPreviousSentence` text,
  `reasonTermsInNextSentence` text,
  `reasonTermsInNearbySentencesParagraphs` text,
  `reasonTriplesInSameSentence` text,
  `reasonTriplesInPreviousSentence` text,
  `reasonTriplesInNextSentence` text,
  `reasonTriplesInPrevParagraph` text,
  `reasonTriplesInNextParagraph` text,
  `reasonTriplesInNearbySentencesParagraphs` text,
  `ps` text,
  `ns` text,
  `pp` longtext,
  `ep` longtext,
  `np` longtext,
  `identifierCount` int(11) DEFAULT NULL,
  KEY `clausieIDX` (`clausie`(20)),
  KEY `bip` (`bip`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

-- Data exporting was unselected.
-- Dumping structure for table bips_new.results_bckdec
CREATE TABLE IF NOT EXISTS `results_bckdec` (
  `bip` int(11) NOT NULL,
  `messageID` int(11) DEFAULT NULL,
  `date` date DEFAULT NULL,
  `dateTimeStamp` timestamp NULL DEFAULT NULL,
  `messageSubject` tinytext,
  `label` text,
  `subject` text,
  `relation` text,
  `object` text,
  `currentSentence` text,
  `clausie` text,
  `clausieInCST` text,
  `ollie` text,
  `reverb` text,
  `author` text,
  `folder` tinytext,
  `file` tinytext,
  `numberOfbipsMentionedInMessage` int(11) DEFAULT NULL,
  `repeatedLabel` text,
  `bipType` tinytext,
  `role` text,
  `allReasons` text,
  `reasonInSentence` text,
  `reasonTermsInMatchedTriple` text,
  `reasonTermsInNearbySentencesParagraphs` text,
  `reasonTriplesInNearbySentencesParagraphs` text,
  `ps` text,
  `ns` text,
  `pp` longtext,
  `ep` longtext,
  `np` longtext,
  `identifierCount` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

-- Data exporting was unselected.
-- Dumping structure for table bips_new.results_postprocessed
CREATE TABLE IF NOT EXISTS `results_postprocessed` (
  `bip` int(11) NOT NULL,
  `messageID` int(11) DEFAULT NULL,
  `messageSubject` text,
  `numberOfbipsMentionedInMessage` int(11) DEFAULT NULL,
  `author` text,
  `folder` tinytext,
  `file` tinytext,
  `date` date DEFAULT NULL,
  `timestamp` timestamp NULL DEFAULT NULL,
  `label` text,
  `subject` text,
  `relation` text,
  `object` text,
  `currentSentence` text,
  `reverb` text,
  `clausie` text,
  `ollie` text,
  `repeatedLabel` text,
  `role` text,
  `allReasons` text,
  `reasonsInNearbyStates` text,
  `reasonsInSentence` text,
  `reasonTermsInMatchedTriple` text,
  `reasonTermsInNearbySentencesParagraphs` text,
  `reasonTriplesInNearbySentencesParagraphs` text,
  `reasonTriplesInNextParagraph` text,
  `reasonTermsFoundinDeletedLabels` text,
  `reasonTriplesInSameSentence` text,
  `reasonTriplesInPreviousSentence` text,
  `reasonTriplesInNextSentence` text,
  `reasonTriplesInPrevParagraph` text,
  `reasonTermsInPreviousSentence` text,
  `reasonTermsInNextSentence` text,
  `ps` text,
  `ns` text,
  `pp` mediumtext,
  `ep` mediumtext,
  `np` mediumtext
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

-- Data exporting was unselected.
-- Dumping structure for view bips_new.summaries
-- Creating temporary table to overcome VIEW dependency errors
CREATE TABLE `summaries` (
	`bip` INT(11) NULL,
	`messageid` INT(11) NULL,
	`date2` DATE NULL,
	`email` LONGTEXT NULL COLLATE 'latin1_swedish_ci',
	`subject` MEDIUMTEXT NULL COLLATE 'latin1_swedish_ci',
	`identifiercount` INT(11) NULL
) ENGINE=MyISAM;

-- Dumping structure for table bips_new.test
CREATE TABLE IF NOT EXISTS `test` (
  `clusterbysenderfullname` tinytext,
  `folder` text
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
-- Dumping structure for procedure bips_new.test
DELIMITER //
CREATE DEFINER=`root`@`localhost` PROCEDURE `test`()
BEGIN
	DECLARE i INTEGER;
	DECLARE n TEXT;
	DECLARE m TEXT;
	DECLARE curs1 CURSOR FOR SELECT `bip`,'title' FROM bipdetails;
	DECLARE curs2 CURSOR FOR SELECT `bipstates` FROM bipstates WHERE bip = i;
	
		OPEN curs1;
		FETCH curs1 INTO i,n;
		SELECT i,n;		
		-- for each bip select 		
			OPEN curs2;
			FETCH curs2 INTO m;
			SELECT m;
			CLOSE curs2;
		
		CLOSE curs1;
END//
DELIMITER ;

-- Dumping structure for table bips_new.trackmessageprocessing
CREATE TABLE IF NOT EXISTS `trackmessageprocessing` (
  `bip` int(11) DEFAULT NULL,
  `MessageSubjectContainsOnlyAnotherbip` tinyint(4) DEFAULT NULL,
  `messageSubject` text,
  `messageID` int(11) DEFAULT NULL,
  `date` timestamp NULL DEFAULT NULL,
  `messageEmpty` tinyint(4) DEFAULT NULL,
  `proceed` tinyint(4) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
-- Dumping structure for view bips_new.distinctclusters
-- Removing temporary table and create final VIEW structure
DROP TABLE IF EXISTS `distinctclusters`;
CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` VIEW `distinctclusters` AS (
	select cluster, count(id) as members-- , max(sendername), sum(totalMessageCount)
	from distinctsenders
	WHERE cluster IS NOT NULL 
	group by cluster
	order by totalMessageCount DESC 
) ;

-- Dumping structure for view bips_new.duplicatemessageid
-- Removing temporary table and create final VIEW structure
DROP TABLE IF EXISTS `duplicatemessageid`;
CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` VIEW `duplicatemessageid` AS SELECT bip,messageid,timestamp, COUNT(messageid) AS NumOccurrences
	    FROM results_postprocessed
	    GROUP BY bip,messageid
	    HAVING NumOccurrences > 1 ;

-- Dumping structure for view bips_new.summaries
-- Removing temporary table and create final VIEW structure
DROP TABLE IF EXISTS `summaries`;
CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` VIEW `summaries` AS select bip, messageid, date2, email, subject, identifiercount from allmessages where identifiercount > 5 and (subject like '%summary%' or subject like '%summaries%') ;

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
