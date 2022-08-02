-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server version:               5.6.25 - MySQL Community Server (GPL)
-- Server OS:                    Win64
-- HeidiSQL Version:             9.4.0.5151
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


-- Dumping database structure for eps_new
CREATE DATABASE IF NOT EXISTS `eps` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `eps_new`;

-- Dumping structure for table eps_new.alldeveloperposts
CREATE TABLE IF NOT EXISTS `alldeveloperposts` (
  `ep` int(11) DEFAULT NULL,
  `messageID` int(11) DEFAULT NULL,
  `date2` date DEFAULT NULL,
  `epType` text,
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
  `hasepTitleOnly` tinyint(4) DEFAULT NULL,
  `countRepliesToThisMessage` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
-- Dumping structure for table eps_new.alldevelopers
CREATE TABLE IF NOT EXISTS `alldevelopers` (
  `clusterBySenderFullName` tinytext
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
-- Dumping structure for table eps_new.alldeveloperswithoutadmin
CREATE TABLE IF NOT EXISTS `alldeveloperswithoutadmin` (
  `clusterBySenderFullName` tinytext
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
-- Dumping structure for table eps_new.alldeveloperswithoutadminandmartin
CREATE TABLE IF NOT EXISTS `alldeveloperswithoutadminandmartin` (
  `clusterBySenderFullName` tinytext
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
-- Dumping structure for table eps_new.alldevelopers_b4clustering
CREATE TABLE IF NOT EXISTS `alldevelopers_b4clustering` (
  `sendername` tinytext,
  `allPostsByThisAuthor` int(11) DEFAULT NULL,
  `allUniquePostsByThisAuthor` int(11) DEFAULT NULL,
  `allPostsByThisAuthorInInformationaleps` int(11) DEFAULT NULL,
  `allPostsByThisAuthorInProcesseps` int(11) DEFAULT NULL,
  `allPostsByThisAuthorInStandardeps` int(11) DEFAULT NULL,
  `allPostsByThisAuthorInTotalUniqueeps` int(11) DEFAULT NULL,
  `allPostsByThisAuthorInUniqueStandardeps` int(11) DEFAULT NULL,
  `allPostsByThisAuthorInUniqueProcesseps` int(11) DEFAULT NULL,
  `allPostsByThisAuthorInUniqueInformationaleps` int(11) DEFAULT NULL,
  `allPostsByThisAuthorInUniqueepsEqualToNULL` int(11) DEFAULT NULL,
  `allPostsByThisAuthorInepTypeEqualToNULL` int(11) DEFAULT NULL,
  `allUniquePostsByThisAuthorInStandardeps` int(11) DEFAULT NULL,
  `allUniquePostsByThisAuthorInProcesseps` int(11) DEFAULT NULL,
  `allUniquePostsByThisAuthorInInformationaleps` int(11) DEFAULT NULL,
  `totalNumberOfUniqueNULLepTypeInWhichAuthorPosted` int(11) DEFAULT NULL,
  `totalNumberOfUniqueStandardepTypeInWhichAuthorPosted` int(11) DEFAULT NULL,
  `totalNumberOfUniqueProcessepTypeInWhichAuthorPosted` int(11) DEFAULT NULL,
  `totalNumberOfUniqueInformationalepTypeInWhichAuthorPosted` int(11) DEFAULT NULL,
  `allSeedingPostsByThisAuthor` int(11) DEFAULT NULL,
  `maxPostsInAepByThisAuthor` int(11) DEFAULT NULL,
  `minPostsInAepByThisAuthor` int(11) DEFAULT NULL,
  `firstPostDate` date DEFAULT NULL,
  `lastPostDate` date DEFAULT NULL,
  `YearFirstPostDate` int(11) DEFAULT NULL,
  `YearLastPostDate` int(11) DEFAULT NULL,
  `daysTillLastPost` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
-- Dumping structure for table eps_new.allmembers
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
-- Dumping structure for table eps_new.allmessages
CREATE TABLE IF NOT EXISTS `allmessages` (
  `ep` int(11) DEFAULT NULL,
  `originalepNumber` int(11) DEFAULT NULL,
  `messageID` int(11) DEFAULT NULL,
  `date2` date DEFAULT NULL,
  `epType` text,
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
  `hasepTitleOnly` tinyint(4) DEFAULT NULL,
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
  KEY `ep` (`ep`),
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
-- Dumping structure for table eps_new.allmessagesbck
CREATE TABLE IF NOT EXISTS `allmessagesbck` (
  `ep` int(11) DEFAULT NULL,
  `originalepNumber` int(11) DEFAULT NULL,
  `messageID` int(11) DEFAULT NULL,
  `date2` date DEFAULT NULL,
  `epType` text,
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
  `hasepTitleOnly` tinyint(4) DEFAULT NULL,
  `countRepliesToThisMessage` int(11) DEFAULT NULL,
  `fromLine` text,
  `senderemail` tinytext,
  `senderemailProcessed` tinytext,
  `senderEmailFirstSegment` tinytext,
  `senderFullName` tinytext,
  `senderFirstName` tinytext,
  `senderLastName` tinytext,
  `clusterBySenderFullName` tinytext,
  KEY `ep` (`ep`),
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
-- Dumping structure for table eps_new.allmessages_announce_list
CREATE TABLE IF NOT EXISTS `allmessages_announce_list` (
  `ep` int(11) DEFAULT NULL,
  `originalepNumber` int(11) DEFAULT NULL,
  `messageID` int(11) DEFAULT NULL,
  `date2` date DEFAULT NULL,
  `epType` text,
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
  `hasepTitleOnly` tinyint(4) DEFAULT NULL,
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
  KEY `ep` (`ep`),
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
-- Dumping structure for table eps_new.allmessages_authors
CREATE TABLE IF NOT EXISTS `allmessages_authors` (
  `ep` int(11) DEFAULT NULL,
  `originalepNumber` int(11) DEFAULT NULL,
  `messageID` int(11) DEFAULT NULL,
  `date2` date DEFAULT NULL,
  `epType` text,
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
  `hasepTitleOnly` tinyint(4) DEFAULT NULL,
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
  KEY `ep` (`ep`),
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
-- Dumping structure for table eps_new.allmessages_bugs_list
CREATE TABLE IF NOT EXISTS `allmessages_bugs_list` (
  `ep` int(11) DEFAULT NULL,
  `originalepNumber` int(11) DEFAULT NULL,
  `messageID` int(11) DEFAULT NULL,
  `date2` date DEFAULT NULL,
  `epType` text,
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
  `hasepTitleOnly` tinyint(4) DEFAULT NULL,
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
  KEY `ep` (`ep`),
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
-- Dumping structure for table eps_new.allmessages_checkins
CREATE TABLE IF NOT EXISTS `allmessages_checkins` (
  `ep` int(11) DEFAULT NULL,
  `originalepNumber` int(11) DEFAULT NULL,
  `messageID` int(11) DEFAULT NULL,
  `date2` date DEFAULT NULL,
  `epType` text,
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
  `hasepTitleOnly` tinyint(4) DEFAULT NULL,
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
  KEY `ep` (`ep`),
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
-- Dumping structure for table eps_new.allmessages_committers
CREATE TABLE IF NOT EXISTS `allmessages_committers` (
  `ep` int(11) DEFAULT NULL,
  `originalepNumber` int(11) DEFAULT NULL,
  `messageID` int(11) DEFAULT NULL,
  `date2` date DEFAULT NULL,
  `epType` text,
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
  `hasepTitleOnly` tinyint(4) DEFAULT NULL,
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
  KEY `ep` (`ep`),
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
-- Dumping structure for table eps_new.allmessages_dev
CREATE TABLE IF NOT EXISTS `allmessages_dev` (
  `ep` int(11) DEFAULT NULL,
  `originalepNumber` int(11) DEFAULT NULL,
  `messageID` int(11) DEFAULT NULL,
  `date2` date DEFAULT NULL,
  `epType` text,
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
  `hasepTitleOnly` tinyint(4) DEFAULT NULL,
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
  KEY `ep` (`ep`),
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
-- Dumping structure for table eps_new.allmessages_distutils_sig
CREATE TABLE IF NOT EXISTS `allmessages_distutils_sig` (
  `ep` int(11) DEFAULT NULL,
  `originalepNumber` int(11) DEFAULT NULL,
  `messageID` int(11) DEFAULT NULL,
  `date2` date DEFAULT NULL,
  `epType` text,
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
  `hasepTitleOnly` tinyint(4) DEFAULT NULL,
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
  KEY `ep` (`ep`),
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
-- Dumping structure for table eps_new.allmessages_ideas
CREATE TABLE IF NOT EXISTS `allmessages_ideas` (
  `ep` int(11) DEFAULT NULL,
  `originalepNumber` int(11) DEFAULT NULL,
  `messageID` int(11) DEFAULT NULL,
  `date2` date DEFAULT NULL,
  `epType` text,
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
  `hasepTitleOnly` tinyint(4) DEFAULT NULL,
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
  KEY `ep` (`ep`),
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
-- Dumping structure for table eps_new.allmessages_lists
CREATE TABLE IF NOT EXISTS `allmessages_lists` (
  `ep` int(11) DEFAULT NULL,
  `originalepNumber` int(11) DEFAULT NULL,
  `messageID` int(11) DEFAULT NULL,
  `date2` date DEFAULT NULL,
  `epType` text,
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
  `hasepTitleOnly` tinyint(4) DEFAULT NULL,
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
  KEY `ep` (`ep`),
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
-- Dumping structure for table eps_new.allmessages_patches
CREATE TABLE IF NOT EXISTS `allmessages_patches` (
  `ep` int(11) DEFAULT NULL,
  `originalepNumber` int(11) DEFAULT NULL,
  `messageID` int(11) DEFAULT NULL,
  `date2` date DEFAULT NULL,
  `epType` text,
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
  `hasepTitleOnly` tinyint(4) DEFAULT NULL,
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
  KEY `ep` (`ep`),
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
-- Dumping structure for table eps_new.allmessages_eptitleonly_copy
CREATE TABLE IF NOT EXISTS `allmessages_eptitleonly_copy` (
  `ep` int(11) DEFAULT NULL,
  `originalepNumber` int(11) DEFAULT NULL,
  `messageID` int(11) DEFAULT NULL,
  `date2` date DEFAULT NULL,
  `epType` text,
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
  `hasepTitleOnly` tinyint(4) DEFAULT NULL,
  KEY `ep` (`ep`),
  KEY `subject` (`subject`(50)),
  KEY `messageId_Index` (`messageID`),
  KEY `emailMessageId` (`emailMessageId`(80))
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

-- Data exporting was unselected.
-- Dumping structure for table eps_new.allmessages_withepnumbers
CREATE TABLE IF NOT EXISTS `allmessages_withepnumbers` (
  `ep` int(11) DEFAULT NULL,
  `originalepNumber` int(11) DEFAULT NULL,
  `messageID` int(11) DEFAULT NULL,
  `date2` date DEFAULT NULL,
  `epType` text,
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
  `hasepTitleOnly` tinyint(4) DEFAULT NULL,
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
-- Dumping structure for table eps_new.allepnumbers_b4clustering
CREATE TABLE IF NOT EXISTS `allepnumbers_b4clustering` (
  `ep` int(11) DEFAULT NULL,
  `messageID` int(11) DEFAULT NULL,
  `date2` date DEFAULT NULL,
  `epType` text,
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
  `hasepTitleOnly` tinyint(4) DEFAULT NULL,
  KEY `ep` (`ep`),
  KEY `subject` (`subject`(50)),
  KEY `folder` (`folder`(20)),
  KEY `file` (`file`(15)),
  KEY `senderName` (`senderName`(20)),
  KEY `messageId_Index` (`messageID`),
  KEY `emailMessageId` (`emailMessageId`(80))
) ENGINE=MyISAM DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

-- Data exporting was unselected.
-- Dumping structure for table eps_new.alleps_ideaseptitles
CREATE TABLE IF NOT EXISTS `alleps_ideaseptitles` (
  `date` text,
  `location` text,
  `line` longtext,
  `email` longtext,
  `id` int(11) DEFAULT '0',
  `ep` int(11) DEFAULT NULL,
  `date2` date DEFAULT NULL,
  `epType` text,
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
  KEY `ep` (`ep`),
  KEY `subject` (`subject`(50)),
  KEY `messageId_Index` (`messageID`),
  KEY `emailMessageId` (`emailMessageId`(80))
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

-- Data exporting was unselected.
-- Dumping structure for table eps_new.authormatching
CREATE TABLE IF NOT EXISTS `authormatching` (
  `sendername` tinytext,
  `match` tinytext,
  `true` bit(1) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
-- Dumping structure for procedure eps_new.ClusterEmailSenders
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

-- Dumping structure for table eps_new.clustersdevsenders
CREATE TABLE IF NOT EXISTS `clustersdevsenders` (
  `id` int(11) DEFAULT NULL,
  `emailaddress` tinytext,
  `senderName` tinytext,
  `totalMessageCount` int(11) DEFAULT NULL,
  `senderFirstName` tinytext,
  `senderLastName` tinytext
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

-- Data exporting was unselected.
-- Dumping structure for table eps_new.coredevelopers
CREATE TABLE IF NOT EXISTS `coredevelopers` (
  `coredeveloper` tinytext
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
-- Dumping structure for table eps_new.danieldata
CREATE TABLE IF NOT EXISTS `danieldata` (
  `ep` int(11) DEFAULT NULL,
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
-- Dumping structure for table eps_new.distinctauthorepnumbers
CREATE TABLE IF NOT EXISTS `distinctauthorepnumbers` (
  `senderName` tinytext,
  `allPostsByThisAuthor` int(11) DEFAULT NULL,
  `allUniquePostsByThisAuthor` int(11) DEFAULT NULL,
  `allPostsByThisAuthorInInformationaleps` int(11) DEFAULT NULL,
  `allPostsByThisAuthorInProcesseps` int(11) DEFAULT NULL,
  `allPostsByThisAuthorInStandardeps` int(11) DEFAULT NULL,
  `allPostsByThisAuthorInTotalUniqueeps` int(11) DEFAULT NULL,
  `allPostsByThisAuthorInUniqueStandardeps` int(11) DEFAULT NULL,
  `allPostsByThisAuthorInUniqueProcesseps` int(11) DEFAULT NULL,
  `allPostsByThisAuthorInUniqueInformationaleps` int(11) DEFAULT NULL,
  `allPostsByThisAuthorInUniqueepsEqualToNULL` int(11) DEFAULT NULL,
  `allPostsByThisAuthorInepTypeEqualToNULL` int(11) DEFAULT NULL,
  `allUniquePostsByThisAuthorInStandardeps` int(11) DEFAULT NULL,
  `allUniquePostsByThisAuthorInProcesseps` int(11) DEFAULT NULL,
  `allUniquePostsByThisAuthorInInformationaleps` int(11) DEFAULT NULL,
  `totalNumberOfUniqueNULLepTypeInWhichAuthorPosted` int(11) DEFAULT NULL,
  `totalNumberOfUniqueStandardepTypeInWhichAuthorPosted` int(11) DEFAULT NULL,
  `totalNumberOfUniqueProcessepTypeInWhichAuthorPosted` int(11) DEFAULT NULL,
  `totalNumberOfUniqueInformationalepTypeInWhichAuthorPosted` int(11) DEFAULT NULL,
  `allSeedingPostsByThisAuthor` int(11) DEFAULT NULL,
  `maxPostsInAepByThisAuthor` int(11) DEFAULT NULL,
  `minPostsInAepByThisAuthor` int(11) DEFAULT NULL,
  `firstPostDate` date DEFAULT NULL,
  `lastPostDate` date DEFAULT NULL,
  `YearFirstPostDate` int(11) DEFAULT NULL,
  `YearLastPostDate` int(11) DEFAULT NULL,
  `daysTillLastPost` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
-- Dumping structure for table eps_new.distinctauthorsallmessages
CREATE TABLE IF NOT EXISTS `distinctauthorsallmessages` (
  `senderName` tinytext,
  `allPostsByThisAuthor` int(11) DEFAULT NULL,
  `allPostsByThisAuthorInInformationaleps` int(11) DEFAULT NULL,
  `allPostsByThisAuthorInProcesseps` int(11) DEFAULT NULL,
  `allPostsByThisAuthorInStandardeps` int(11) DEFAULT NULL,
  `allPostsByThisAuthorInUniqueeps` int(11) DEFAULT NULL,
  `allSeedingPostsByThisAuthor` int(11) DEFAULT NULL,
  `maxPostsInAepByThisAuthor` int(11) DEFAULT NULL,
  `minPostsInAepByThisAuthor` int(11) DEFAULT NULL,
  `firstPostDate` date DEFAULT NULL,
  `lastPostDate` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
-- Dumping structure for table eps_new.distinctauthorsepnumbers_oldbeforeclustering
CREATE TABLE IF NOT EXISTS `distinctauthorsepnumbers_oldbeforeclustering` (
  `senderName` tinytext,
  `allPostsByThisAuthor` int(11) DEFAULT NULL,
  `allPostsByThisAuthorInInformationaleps` int(11) DEFAULT NULL,
  `allPostsByThisAuthorInProcesseps` int(11) DEFAULT NULL,
  `allPostsByThisAuthorInStandardeps` int(11) DEFAULT NULL,
  `allPostsByThisAuthorInTotalUniqueeps` int(11) DEFAULT NULL,
  `allPostsByThisAuthorInUniqueStandardeps` int(11) DEFAULT NULL,
  `allPostsByThisAuthorInUniqueProcesseps` int(11) DEFAULT NULL,
  `allPostsByThisAuthorInUniqueInformationaleps` int(11) DEFAULT NULL,
  `allPostsByThisAuthorInUniqueepsEqualToNULL` int(11) DEFAULT NULL,
  `allSeedingPostsByThisAuthor` int(11) DEFAULT NULL,
  `maxPostsInAepByThisAuthor` int(11) DEFAULT NULL,
  `minPostsInAepByThisAuthor` int(11) DEFAULT NULL,
  `firstPostDate` date DEFAULT NULL,
  `lastPostDate` date DEFAULT NULL,
  `YearFirstPostDate` int(11) DEFAULT NULL,
  `YearLastPostDate` int(11) DEFAULT NULL,
  `daysTillLastPost` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
-- Dumping structure for view eps_new.distinctclusters
-- Creating temporary table to overcome VIEW dependency errors
CREATE TABLE `distinctclusters` (
	`cluster` INT(11) NULL,
	`members` BIGINT(21) NOT NULL
) ENGINE=MyISAM;

-- Dumping structure for table eps_new.distinctdevsenders
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
-- Dumping structure for table eps_new.distinctsenders
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
-- Dumping structure for table eps_new.distinctsendersamem
CREATE TABLE IF NOT EXISTS `distinctsendersamem` (
  `id` int(11) DEFAULT NULL,
  `emailaddress` varchar(100) DEFAULT NULL,
  `senderName` varchar(100) DEFAULT NULL,
  `totalSenderCount` int(11) DEFAULT NULL,
  `totalMessageCount` int(11) DEFAULT NULL,
  `senderFirstName` varchar(50) DEFAULT NULL,
  `senderLastName` varchar(50) DEFAULT NULL,
  `senderEmailFirstSegment` varchar(50) DEFAULT NULL,
  `cluster` int(11) DEFAULT NULL,
  `clustered` int(11) DEFAULT NULL
) ENGINE=MEMORY DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
-- Dumping structure for table eps_new.distinctsendersfirstname
CREATE TABLE IF NOT EXISTS `distinctsendersfirstname` (
  `SENDERFIRSTNAME` tinytext,
  KEY `firstname` (`SENDERFIRSTNAME`(20))
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
-- Dumping structure for table eps_new.distinctsenders_4_junealldone
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
-- Dumping structure for table eps_new.distinctsenders_copyall_done
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
-- Dumping structure for table eps_new.errors
CREATE TABLE IF NOT EXISTS `errors` (
  `messageid` int(11) DEFAULT NULL,
  `AUTHOR` mediumtext,
  `senderName` tinytext,
  `emailmessageid` tinytext,
  `inreplyto` text,
  `inreplytouser` text
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
-- Dumping structure for table eps_new.errors2
CREATE TABLE IF NOT EXISTS `errors2` (
  `messageid` int(11) DEFAULT NULL,
  `AUTHOR` mediumtext,
  `senderName` tinytext,
  `emailmessageid` tinytext,
  `inreplyto` text,
  `inreplytouser` text
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
-- Dumping structure for table eps_new.labels
CREATE TABLE IF NOT EXISTS `labels` (
  `generalOrepSpecific` tinytext,
  `type` int(11) DEFAULT NULL,
  `epNumber` int(11) DEFAULT NULL,
  `epTitle` tinytext,
  `idea` tinytext,
  `subject` tinytext,
  `verb` tinytext,
  `object` tinytext
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
-- Dumping structure for function eps_new.levenshtein_distance
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

-- Dumping structure for table eps_new.martin
CREATE TABLE IF NOT EXISTS `martin` (
  `messageid` int(11) DEFAULT NULL,
  `sendername` tinytext,
  `firstline` longtext
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
-- Dumping structure for table eps_new.memberpostcount
CREATE TABLE IF NOT EXISTS `memberpostcount` (
  `clusterBySenderFullName` tinytext,
  `folder` tinytext,
  `postCount` int(11) DEFAULT NULL,
  `subjects` int(11) DEFAULT NULL,
  `eps` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
-- Dumping structure for table eps_new.memberpostcount_all
CREATE TABLE IF NOT EXISTS `memberpostcount_all` (
  `clusterBySenderFullName` tinytext,
  `folder` tinytext,
  `postCount` int(11) DEFAULT NULL,
  `subjects` int(11) DEFAULT NULL,
  `eps` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

-- Data exporting was unselected.
-- Dumping structure for table eps_new.memberpostcount_allcrosscutting
CREATE TABLE IF NOT EXISTS `memberpostcount_allcrosscutting` (
  `clusterBySenderFullName` tinytext,
  `folder` tinytext,
  `postCount` int(11) DEFAULT NULL,
  `subjects` int(11) DEFAULT NULL,
  `eps` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

-- Data exporting was unselected.
-- Dumping structure for table eps_new.epdetails
CREATE TABLE IF NOT EXISTS `epdetails` (
  `ep` int(11) DEFAULT NULL,
  `title` text,
  `author` text,
  `authorCorrected` text,
  `type` text,
  `bdfl_delegate` text,
  `bdfl_delegateCorrected` text,
  `created` date DEFAULT NULL,
  `createdYear` int(11) DEFAULT NULL,
  `epSummary` text,
  `python_version` text,
  KEY `ep_index` (`ep`),
  KEY `title` (`title`(50))
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

-- Data exporting was unselected.
-- Dumping structure for table eps_new.epdetails_authorcount
CREATE TABLE IF NOT EXISTS `epdetails_authorcount` (
  `author` tinytext
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
-- Dumping structure for table eps_new.epdetails_titlesforeachep
CREATE TABLE IF NOT EXISTS `epdetails_titlesforeachep` (
  `ep` int(11) DEFAULT NULL,
  `additional_titles` tinytext,
  `original_title_in_ideaslist` tinytext
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
-- Dumping structure for table eps_new.epdetails_withoutcreateddate
CREATE TABLE IF NOT EXISTS `epdetails_withoutcreateddate` (
  `ep` int(11) DEFAULT NULL,
  `title` text,
  `author` text,
  `authorCorrected` text,
  `type` text,
  `bdfl_delegate` text,
  `bdfl_delegateCorrected` text,
  `created` date DEFAULT NULL,
  `epSummary` text,
  `python_version` text,
  KEY `ep_index` (`ep`),
  KEY `title` (`title`(50))
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

-- Data exporting was unselected.
-- Dumping structure for table eps_new.epstates
CREATE TABLE IF NOT EXISTS `epstates` (
  `ep` int(11) DEFAULT NULL,
  `epTitle` tinytext,
  `entireMessage` text,
  `messageID` int(11) DEFAULT NULL,
  `date2` date DEFAULT NULL,
  `epType` text,
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
  `hasepTitleOnly` tinyint(4) DEFAULT NULL,
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
  KEY `ep` (`ep`),
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
-- Dumping structure for table eps_new.epstates_danieldata
CREATE TABLE IF NOT EXISTS `epstates_danieldata` (
  `ep` int(11) DEFAULT NULL,
  `epTitle` tinytext,
  `entireMessage` text,
  `messageID` int(11) DEFAULT NULL,
  `date2` date DEFAULT NULL,
  `epType` text,
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
  `hasepTitleOnly` tinyint(4) DEFAULT NULL,
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
  KEY `ep` (`ep`),
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
-- Dumping structure for table eps_new.epstates_danieldata_wide
CREATE TABLE IF NOT EXISTS `epstates_danieldata_wide` (
  `ep` int(11) DEFAULT NULL,
  `finaltitle` text,
  `titleinFirstState` varchar(255) DEFAULT NULL,
  `titleInActiveState` varchar(255) DEFAULT NULL,
  `titleInDraftState` varchar(255) DEFAULT NULL,
  `titleInAcceptedState` varchar(255) DEFAULT NULL,
  `titleInRejectedState` varchar(255) DEFAULT NULL,
  `titleInFinalState` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
-- Dumping structure for table eps_new.epstates_wide
CREATE TABLE IF NOT EXISTS `epstates_wide` (
  `ep` int(11) DEFAULT NULL,
  `finaltitle` text,
  `titleinFirstState` varchar(255) DEFAULT NULL,
  `titleInActiveState` varchar(255) DEFAULT NULL,
  `titleInDraftState` varchar(255) DEFAULT NULL,
  `titleInAcceptedState` varchar(255) DEFAULT NULL,
  `titleInRejectedState` varchar(255) DEFAULT NULL,
  `titleInFinalState` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
-- Dumping structure for table eps_new.relations
CREATE TABLE IF NOT EXISTS `relations` (
  `arg1` text,
  `relation` text,
  `arg2` text,
  `sentence` text
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

-- Data exporting was unselected.
-- Dumping structure for table eps_new.relations_clausie
CREATE TABLE IF NOT EXISTS `relations_clausie` (
  `arg1` text,
  `relation` text,
  `arg2` text,
  `sentence` text,
  `ep` int(11) DEFAULT NULL,
  `messageID` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

-- Data exporting was unselected.
-- Dumping structure for table eps_new.results
CREATE TABLE IF NOT EXISTS `results` (
  `ep` int(11) NOT NULL,
  `messageID` int(11) DEFAULT NULL,
  `author` text,
  `folder` tinytext,
  `file` tinytext,
  `date` date DEFAULT NULL,
  `messageSubject` tinytext,
  `numberOfepsMentionedInMessage` int(11) DEFAULT NULL,
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
-- Dumping structure for table eps_new.results34
CREATE TABLE IF NOT EXISTS `results34` (
  `ep` int(11) NOT NULL,
  `messageID` int(11) DEFAULT NULL,
  `author` text,
  `folder` tinytext,
  `file` tinytext,
  `date` date DEFAULT NULL,
  `messageSubject` tinytext,
  `numberOfepsMentionedInMessage` int(11) DEFAULT NULL,
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
  `reason` text,
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
-- Dumping structure for table eps_new.results_postprocessed
CREATE TABLE IF NOT EXISTS `results_postprocessed` (
  `ep` int(11) NOT NULL,
  `messageID` int(11) DEFAULT NULL,
  `author` text,
  `folder` tinytext,
  `file` tinytext,
  `date` date DEFAULT NULL,
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
  `reasonsInSentence` text,
  `reasonTermsInMatchedTriple` text,
  `reasonTermsInNearbySentencesParagraphs` text,
  `reasonTriplesInNearbySentencesParagraphs` text,
  `reasonTermsFoundinDeletedLabels` text,
  `ps` text,
  `ns` text,
  `pp` mediumtext,
  `ep` mediumtext,
  `np` mediumtext
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

-- Data exporting was unselected.
-- Dumping structure for procedure eps_new.test
DELIMITER //
CREATE DEFINER=`root`@`localhost` PROCEDURE `test`()
BEGIN
	DECLARE i INTEGER;
	DECLARE n TEXT;
	DECLARE m TEXT;
	DECLARE curs1 CURSOR FOR SELECT `ep`,'title' FROM epdetails;
	DECLARE curs2 CURSOR FOR SELECT `epstates` FROM epstates WHERE ep = i;
	
		OPEN curs1;
		FETCH curs1 INTO i,n;
		SELECT i,n;		
		-- for each ep select 		
			OPEN curs2;
			FETCH curs2 INTO m;
			SELECT m;
			CLOSE curs2;
		
		CLOSE curs1;
END//
DELIMITER ;

-- Dumping structure for table eps_new.test
CREATE TABLE IF NOT EXISTS `test` (
  `clusterbysenderfullname` tinytext,
  `folder` text
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
-- Dumping structure for view eps_new.distinctclusters
-- Removing temporary table and create final VIEW structure
DROP TABLE IF EXISTS `distinctclusters`;
CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `distinctclusters` AS (
	select cluster, count(id) as members-- , max(sendername), sum(totalMessageCount)
	from distinctsenders
	WHERE cluster IS NOT NULL 
	group by cluster
	order by totalMessageCount DESC 
) ;

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
