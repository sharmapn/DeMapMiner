-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server version:               5.6.25 - MySQL Community Server (GPL)
-- Server OS:                    Win64
-- HeidiSQL Version:             10.3.0.5771
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


-- Dumping database structure for peps_new
CREATE DATABASE IF NOT EXISTS `bips` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `bips`;

-- Dumping structure for table bips_new.2020_memberroles
CREATE TABLE IF NOT EXISTS `2020_memberroles` (
  `author` mediumtext,
  `authorsrole` mediumtext
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.2021_snaofroles
CREATE TABLE IF NOT EXISTS `2021_snaofroles` (
  `clusterBySenderFullName` tinytext,
  `a` varchar(1) CHARACTER SET utf8mb4 NOT NULL DEFAULT '',
  `inReplyToUserUsingClusteredSender` tinytext,
  `b` varchar(1) CHARACTER SET utf8mb4 NOT NULL DEFAULT '',
  `authorsrole` mediumtext,
  `c` varchar(2) CHARACTER SET utf8mb4 NOT NULL DEFAULT '',
  `inReplyToUserUsingClusteredSenderRole` tinytext,
  `d` varchar(3) CHARACTER SET utf8mb4 NOT NULL DEFAULT '',
  `bipinvolvementcount` bigint(21) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.2021_snaofroles_bdfld
CREATE TABLE IF NOT EXISTS `2021_snaofroles_bdfld` (
  `clusterBySenderFullName` tinytext,
  `a` varchar(3) CHARACTER SET utf8mb4 NOT NULL DEFAULT '',
  `inReplyToUserUsingClusteredSender` tinytext,
  `b` varchar(1) CHARACTER SET utf8mb4 NOT NULL DEFAULT '',
  `authorsrole` mediumtext,
  `c` varchar(2) CHARACTER SET utf8mb4 NOT NULL DEFAULT '',
  `inReplyToUserUsingClusteredSenderRole` tinytext,
  `d` varchar(3) CHARACTER SET utf8mb4 NOT NULL DEFAULT '',
  `bipinvolvementcount` bigint(21) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.accrejbips
CREATE TABLE IF NOT EXISTS `accrejbips` (
  `bip` int(11) DEFAULT NULL,
  `state` longtext,
  `date2` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.accrejbips2
CREATE TABLE IF NOT EXISTS `accrejbips2` (
  `bip` int(11) DEFAULT NULL,
  `state` longtext,
  `DATE2` date DEFAULT NULL,
  `biptype` text,
  `P2` int(11) DEFAULT NULL,
  `author` text,
  `bdfl_delegatecorrected` text
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.agentdetail
CREATE TABLE IF NOT EXISTS `agentdetail` (
  `acceptance_stalled` text,
  `acceptance_stalled_Days` int(11) DEFAULT NULL,
  `accepted` text,
  `accepted_Days` int(11) DEFAULT NULL,
  `active` text,
  `active_Days` int(11) DEFAULT NULL,
  `another_round_discussion` text,
  `another_round_discussion_Days` int(11) DEFAULT NULL,
  `approval_to_vote` text,
  `approval_to_vote_Days` int(11) DEFAULT NULL,
  `assigned_bip_number` text,
  `assigned_bip_number_Days` int(11) DEFAULT NULL,
  `bdfl_allocated` text,
  `bdfl_allocated_Days` int(11) DEFAULT NULL,
  `bdfl_asked_volunteer` text,
  `bdfl_asked_volunteer_Days` int(11) DEFAULT NULL,
  `bdfl_delegate_accepted_bip` text,
  `bdfl_delegate_accepted_bip_Days` int(11) DEFAULT NULL,
  `bdfl_delegate_reviewed` text,
  `bdfl_delegate_reviewed_Days` int(11) DEFAULT NULL,
  `bdfl_pronouncement` text,
  `bdfl_pronouncement_Days` int(11) DEFAULT NULL,
  `bdfl_pronouncement_accepted` text,
  `bdfl_pronouncement_accepted_Days` int(11) DEFAULT NULL,
  `bdfl_pronouncement_rejected` text,
  `bdfl_pronouncement_rejected_Days` int(11) DEFAULT NULL,
  `benefits_marginal` text,
  `benefits_marginal_Days` int(11) DEFAULT NULL,
  `call_for_poll` text,
  `call_for_poll_Days` int(11) DEFAULT NULL,
  `check_in_bip` text,
  `check_in_bip_Days` int(11) DEFAULT NULL,
  `closed` text,
  `closed_Days` int(11) DEFAULT NULL,
  `comments_addressed` text,
  `comments_addressed_Days` int(11) DEFAULT NULL,
  `complementary_unofficial_vote_results` text,
  `complementary_unofficial_vote_results_Days` int(11) DEFAULT NULL,
  `complementary_vote` text,
  `complementary_vote_Days` int(11) DEFAULT NULL,
  `complimentary_vote_results` text,
  `complimentary_vote_results_Days` int(11) DEFAULT NULL,
  `consensus` text,
  `consensus_Days` int(11) DEFAULT NULL,
  `consensus_including_bdfl` text,
  `consensus_including_bdfl_Days` int(11) DEFAULT NULL,
  `co_bdfl_delegate_accepted_bip` text,
  `co_bdfl_delegate_accepted_bip_Days` int(11) DEFAULT NULL,
  `decided_bdfl_consensus` text,
  `decided_bdfl_consensus_Days` int(11) DEFAULT NULL,
  `deferred` text,
  `deferred_Days` int(11) DEFAULT NULL,
  `discussion` text,
  `discussion_Days` int(11) DEFAULT NULL,
  `discussion_stalled` text,
  `discussion_stalled_Days` int(11) DEFAULT NULL,
  `draft` text,
  `draft_Days` int(11) DEFAULT NULL,
  `feedback_received` text,
  `feedback_received_Days` int(11) DEFAULT NULL,
  `final` text,
  `final_Days` int(11) DEFAULT NULL,
  `finished` text,
  `finished_Days` int(11) DEFAULT NULL,
  `hope_rough_consensus` text,
  `hope_rough_consensus_Days` int(11) DEFAULT NULL,
  `idea_adapted` text,
  `idea_adapted_Days` int(11) DEFAULT NULL,
  `idea_approved` text,
  `idea_approved_Days` int(11) DEFAULT NULL,
  `idea_postponed` text,
  `idea_postponed_Days` int(11) DEFAULT NULL,
  `idea_received_positively` text,
  `idea_received_positively_Days` int(11) DEFAULT NULL,
  `idea_rejected` text,
  `idea_rejected_Days` int(11) DEFAULT NULL,
  `implemented` text,
  `implemented_Days` int(11) DEFAULT NULL,
  `incomplete` text,
  `incomplete_Days` int(11) DEFAULT NULL,
  `informal_poll` text,
  `informal_poll_Days` int(11) DEFAULT NULL,
  `inviting_votes` text,
  `inviting_votes_Days` int(11) DEFAULT NULL,
  `issue_raised` text,
  `issue_raised_Days` int(11) DEFAULT NULL,
  `member_volunteers_bdfl_delegate` text,
  `member_volunteers_bdfl_delegate_Days` int(11) DEFAULT NULL,
  `no_consensus` text,
  `no_consensus_Days` int(11) DEFAULT NULL,
  `no_objections` text,
  `no_objections_Days` int(11) DEFAULT NULL,
  `no_one_spoke_for` text,
  `no_one_spoke_for_Days` int(11) DEFAULT NULL,
  `no_remaining_controversy` text,
  `no_remaining_controversy_Days` int(11) DEFAULT NULL,
  `no_support` text,
  `no_support_Days` int(11) DEFAULT NULL,
  `official_complementary_confirm` text,
  `official_complementary_confirm_Days` int(11) DEFAULT NULL,
  `open` text,
  `open_Days` int(11) DEFAULT NULL,
  `bip_needs_work` text,
  `bip_needs_work_Days` int(11) DEFAULT NULL,
  `poll` text,
  `poll_Days` int(11) DEFAULT NULL,
  `poll_result` text,
  `poll_result_Days` int(11) DEFAULT NULL,
  `positive_feedback` text,
  `positive_feedback_Days` int(11) DEFAULT NULL,
  `positive_feedback_again` text,
  `positive_feedback_again_Days` int(11) DEFAULT NULL,
  `postponed` text,
  `postponed_Days` int(11) DEFAULT NULL,
  `pre_bip_changed` text,
  `pre_bip_changed_Days` int(11) DEFAULT NULL,
  `previous_version_rejected` text,
  `previous_version_rejected_Days` int(11) DEFAULT NULL,
  `ready_bdfl_review` text,
  `ready_bdfl_review_Days` int(11) DEFAULT NULL,
  `ready_review` text,
  `ready_review_Days` int(11) DEFAULT NULL,
  `rejected` text,
  `rejected_Days` int(11) DEFAULT NULL,
  `rejected_by_author` text,
  `rejected_by_author_Days` int(11) DEFAULT NULL,
  `rejection_notice` text,
  `rejection_notice_Days` int(11) DEFAULT NULL,
  `renamed` text,
  `renamed_Days` int(11) DEFAULT NULL,
  `replaced` text,
  `replaced_Days` int(11) DEFAULT NULL,
  `requests_feedback` text,
  `requests_feedback_Days` int(11) DEFAULT NULL,
  `request_bip_number` text,
  `request_bip_number_Days` int(11) DEFAULT NULL,
  `reviewed` text,
  `reviewed_Days` int(11) DEFAULT NULL,
  `revived` text,
  `revived_Days` int(11) DEFAULT NULL,
  `rough_consensus` text,
  `rough_consensus_Days` int(11) DEFAULT NULL,
  `serious_issues` text,
  `serious_issues_Days` int(11) DEFAULT NULL,
  `superseded` text,
  `superseded_Days` int(11) DEFAULT NULL,
  `uncontroversial` text,
  `uncontroversial_Days` int(11) DEFAULT NULL,
  `unofficial_vote` text,
  `unofficial_vote_Days` int(11) DEFAULT NULL,
  `updated` text,
  `updated_Days` int(11) DEFAULT NULL,
  `vote_after_discussion` text,
  `vote_after_discussion_Days` int(11) DEFAULT NULL,
  `vote_result` text,
  `vote_result_Days` int(11) DEFAULT NULL,
  `vote_showed` text,
  `vote_showed_Days` int(11) DEFAULT NULL,
  `vote_summary` text,
  `vote_summary_Days` int(11) DEFAULT NULL,
  `vote_users_wanted` text,
  `vote_users_wanted_Days` int(11) DEFAULT NULL,
  `willing_bdfl` text,
  `willing_bdfl_Days` int(11) DEFAULT NULL,
  `withdrawal` text,
  `withdrawal_Days` int(11) DEFAULT NULL,
  `withdrawal_notice` text,
  `withdrawal_notice_Days` int(11) DEFAULT NULL,
  `withdrawn` text,
  `withdrawn_Days` int(11) DEFAULT NULL,
  `wrote_bip` text,
  `wrote_bip_Days` int(11) DEFAULT NULL,
  `ISAccepted` int(11) DEFAULT NULL,
  `ISRejected` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.alldeveloperposts
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
  `originalbipNumberNum` int(11) DEFAULT NULL,
  `messageID` int(11) DEFAULT NULL,
  `date2` date DEFAULT NULL,
  `bipType` text,
  `dateTimeStamp` timestamp NULL DEFAULT NULL,
  `dateTimeStampString` text,
  `email` longtext,
  `analyseWords` longtext,
  `corefMsg` longtext,
  `Author` mediumtext,
  `subject` mediumtext,
  `location` text,
  `line` longtext,
  `folder` text,
  `file` text,
  `from` text,
  `emailMessageId` tinytext,
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
  `msgNumInFile` int(11) DEFAULT NULL,
  `processedSubject` mediumtext,
  `authorsrole` mediumtext,
  `ifProposalAtTheEndofMessage` tinyint(4) DEFAULT NULL,
  `processMessage` tinyint(4) DEFAULT NULL,
  `pTitleMatchedinMsgSub` tinyint(4) DEFAULT NULL,
  `pNumMatchedinMsgSub` tinyint(4) DEFAULT NULL,
  `pNumMatchedinMsgBody` tinyint(4) DEFAULT NULL,
  `messageType` tinytext,
  `lastdir` text,
  `inReplyToUserUsingClusteredSenderRole` tinytext,
  `bipnum2020` int(11) DEFAULT NULL,
  `authorsrole2020` text,
  `biptype2020` text,
  `bipnum2021` int(11) DEFAULT NULL,
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
  KEY `clusterBySenderFullName` (`clusterBySenderFullName`(30)),
  KEY `processedSubject` (`processedSubject`(40)),
  KEY `lastdirectory` (`lastdir`(15)),
  KEY `date2` (`date2`),
  KEY `bipnum2020_index` (`bipnum2020`),
  KEY `lastdir_index` (`lastdir`(40)),
  KEY `authorsrole_index` (`authorsrole`(20)),
  KEY `authorsrole2020_index` (`authorsrole2020`(20)),
  KEY `biptype2020_index` (`biptype2020`(20)),
  KEY `bipnum2021_index` (`bipnum2021`)
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

-- Dumping structure for table bips_new.allmessagesforinfluenceanalysis
CREATE TABLE IF NOT EXISTS `allmessagesforinfluenceanalysis` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `bip` int(11) DEFAULT NULL,
  `biptype` text,
  `bipnum2020` int(11) DEFAULT NULL,
  `biptype2020` text,
  `messageid` int(11) DEFAULT NULL,
  `emailmessageid` tinytext,
  `author` mediumtext,
  `authorsrole` mediumtext,
  `authorsrole2020` text,
  `clusterbysenderfullname` tinytext,
  `sendername` tinytext,
  `subject` mediumtext,
  `inreplyto` text,
  `inReplyToUserUsingClusteredSender` tinytext,
  `inReplyToUserUsingClusteredSenderRole` tinytext,
  `threadlevel` int(11) DEFAULT NULL,
  `threadid` int(11) DEFAULT NULL,
  `matchedusingsubject` int(11) DEFAULT NULL,
  `consider` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `bipnum2020_index` (`bipnum2020`),
  KEY `emailmessageid_index` (`emailmessageid`(15)),
  KEY `inreplyto_index` (`inreplyto`(15)),
  KEY `inReplyToUserUsingClusteredSender_index` (`inReplyToUserUsingClusteredSender`(15)),
  KEY `inReplyToUserUsingClusteredSenderRole_index` (`inReplyToUserUsingClusteredSenderRole`(15)),
  KEY `clusterbysenderfullname_index` (`clusterbysenderfullname`(15)),
  KEY `biptype2020_index` (`biptype2020`(15)),
  KEY `subject_index` (`subject`(30)),
  KEY `threadlevel_index` (`threadlevel`),
  KEY `threadid_index` (`threadid`),
  KEY `matchedusingsubject_index` (`matchedusingsubject`),
  KEY `consider_index` (`consider`)
) ENGINE=InnoDB AUTO_INCREMENT=444850 DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.allmessagesmatchingsubjects
CREATE TABLE IF NOT EXISTS `allmessagesmatchingsubjects` (
  `proposal` int(11) DEFAULT NULL,
  `messageID` int(11) DEFAULT NULL,
  `messageIDsOfMessagesWithSimilarMessageSubjects` int(11) DEFAULT NULL,
  `subject` text,
  `processedSubject` text,
  KEY `messageID` (`messageID`),
  KEY `messageIDsOfMessagesWithSimilarMessageSubjects` (`messageIDsOfMessagesWithSimilarMessageSubjects`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.allmessages_2019
CREATE TABLE IF NOT EXISTS `allmessages_2019` (
  `bip` int(11) DEFAULT NULL,
  `originalbipNumber` int(11) DEFAULT NULL,
  `originalbipNumberNum` int(11) DEFAULT NULL,
  `messageID` int(11) DEFAULT NULL,
  `date2` date DEFAULT NULL,
  `bipType` text,
  `dateTimeStamp` timestamp NULL DEFAULT NULL,
  `dateTimeStampString` text,
  `email` longtext,
  `analyseWords` longtext,
  `corefMsg` longtext,
  `Author` mediumtext,
  `subject` mediumtext,
  `location` text,
  `line` longtext,
  `folder` text,
  `file` text,
  `from` text,
  `emailMessageId` tinytext,
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
  `msgNumInFile` int(11) DEFAULT NULL,
  `processedSubject` mediumtext,
  `authorsrole` mediumtext,
  `ifProposalAtTheEndofMessage` tinyint(4) DEFAULT NULL,
  `processMessage` tinyint(4) DEFAULT NULL,
  `pTitleMatchedinMsgSub` tinyint(4) DEFAULT NULL,
  `pNumMatchedinMsgSub` tinyint(4) DEFAULT NULL,
  `pNumMatchedinMsgBody` tinyint(4) DEFAULT NULL,
  `messageType` tinytext,
  `lastdir` text
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.allmessages_2019b
CREATE TABLE IF NOT EXISTS `allmessages_2019b` (
  `bip` int(11) DEFAULT NULL,
  `originalbipNumber` int(11) DEFAULT NULL,
  `originalbipNumberNum` int(11) DEFAULT NULL,
  `messageID` int(11) DEFAULT NULL,
  `date2` date DEFAULT NULL,
  `bipType` text,
  `dateTimeStamp` timestamp NULL DEFAULT NULL,
  `dateTimeStampString` text,
  `email` longtext,
  `analyseWords` longtext,
  `corefMsg` longtext,
  `Author` mediumtext,
  `subject` mediumtext,
  `location` text,
  `line` longtext,
  `folder` text,
  `file` text,
  `from` text,
  `emailMessageId` tinytext,
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
  `msgNumInFile` int(11) DEFAULT NULL,
  `processedSubject` mediumtext,
  `authorsrole` mediumtext,
  `ifProposalAtTheEndofMessage` tinyint(4) DEFAULT NULL,
  `processMessage` tinyint(4) DEFAULT NULL,
  `pTitleMatchedinMsgSub` tinyint(4) DEFAULT NULL,
  `pNumMatchedinMsgSub` tinyint(4) DEFAULT NULL,
  `pNumMatchedinMsgBody` tinyint(4) DEFAULT NULL,
  `messageType` tinytext,
  `lastdir` text,
  KEY `index_author_allmessages_2019b` (`Author`(20)),
  KEY `index_authorsrole_allmessages_2019b` (`authorsrole`(10))
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

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

-- Dumping structure for table bips_new.allmessages_bdfldelegatedecided2020
CREATE TABLE IF NOT EXISTS `allmessages_bdfldelegatedecided2020` (
  `bip` int(11) DEFAULT NULL,
  `originalbipNumber` int(11) DEFAULT NULL,
  `originalbipNumberNum` int(11) DEFAULT NULL,
  `messageID` int(11) DEFAULT NULL,
  `date2` date DEFAULT NULL,
  `bipType` text,
  `dateTimeStamp` timestamp NULL DEFAULT NULL,
  `dateTimeStampString` text,
  `email` longtext,
  `analyseWords` longtext,
  `corefMsg` longtext,
  `Author` mediumtext,
  `subject` mediumtext,
  `location` text,
  `line` longtext,
  `folder` text,
  `file` text,
  `from` text,
  `emailMessageId` tinytext,
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
  `msgNumInFile` int(11) DEFAULT NULL,
  `processedSubject` mediumtext,
  `authorsrole` mediumtext,
  `ifProposalAtTheEndofMessage` tinyint(4) DEFAULT NULL,
  `processMessage` tinyint(4) DEFAULT NULL,
  `pTitleMatchedinMsgSub` tinyint(4) DEFAULT NULL,
  `pNumMatchedinMsgSub` tinyint(4) DEFAULT NULL,
  `pNumMatchedinMsgBody` tinyint(4) DEFAULT NULL,
  `messageType` tinytext,
  `lastdir` text,
  `inReplyToUserUsingClusteredSenderRole` tinytext,
  `bipnum2020` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

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
  `originalbipNumberNum` int(11) DEFAULT NULL,
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
  `msgNumInFile` int(11) DEFAULT NULL,
  `processedSubject` mediumtext,
  `authorsrole` mediumtext,
  `ifProposalAtTheEndofMessage` tinyint(4) DEFAULT NULL,
  `processMessage` tinyint(4) DEFAULT NULL,
  `pTitleMatchedinMsgSub` tinyint(4) DEFAULT NULL,
  `pNumMatchedinMsgSub` tinyint(4) DEFAULT NULL,
  `pNumMatchedinMsgBody` tinyint(4) DEFAULT NULL,
  `messageType` tinytext,
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
  KEY `clusterBySenderFullName` (`clusterBySenderFullName`(30)),
  KEY `processedSubject` (`processedSubject`(40))
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

-- Dumping structure for table bips_new.allmessages_biptitleonly
CREATE TABLE IF NOT EXISTS `allmessages_biptitleonly` (
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

-- Dumping structure for table bips_new.allparagraphs
CREATE TABLE IF NOT EXISTS `allparagraphs` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `proposal` int(11) DEFAULT NULL,
  `messageid` int(11) DEFAULT NULL,
  `paragraph` longtext NOT NULL,
  `isCorefParagraph` tinyint(4) NOT NULL,
  `previousSentence` longtext,
  `nextSentence` text,
  `msgSubject` text,
  `entireParagraph` text,
  `previousParagraph` text,
  `nextParagraph` text,
  `locationMatched` tinytext,
  `label` text,
  `msgAuthorRole` text,
  `isEnglishOrCode` tinyint(4) DEFAULT NULL,
  `isFirstParagraph` tinyint(4) DEFAULT NULL,
  `isLastParagraph` tinyint(4) DEFAULT NULL,
  `paragraphCounter` int(11) DEFAULT NULL,
  `sentenceCounterinParagraph` int(11) DEFAULT NULL,
  `sentenceCounterinMessage` int(11) DEFAULT NULL,
  `reasonTerms` text,
  `reasonIdentifierTerms` int(11) DEFAULT NULL,
  `stateSubStateTerms` int(11) DEFAULT NULL,
  `entityTerms` int(11) DEFAULT NULL,
  `decisionTerms` int(11) DEFAULT NULL,
  `specialTerms` int(11) DEFAULT NULL,
  `authorRole` int(11) DEFAULT NULL,
  `dateDiff` int(11) DEFAULT NULL,
  `messageSubjectDecisionTerms` int(11) DEFAULT NULL,
  `messageSubjectStateSubStateTerms` int(11) DEFAULT NULL,
  `messageSubjectProposalIdentifier` int(11) DEFAULT NULL,
  `reasonsTermsFoundCount` int(11) DEFAULT NULL,
  `reasonsIdentifierTermsCount` int(11) DEFAULT NULL,
  `statesSubstatesCount` int(11) DEFAULT NULL,
  `identifiersTermCount` int(11) DEFAULT NULL,
  `entitiesTermCount` int(11) DEFAULT NULL,
  `specialTermCount` int(11) DEFAULT NULL,
  `decisionTermCount` int(11) DEFAULT NULL,
  `negationTermCount` int(11) DEFAULT NULL,
  `positiveWordCount` int(11) DEFAULT NULL,
  `negativeWordCount` int(11) DEFAULT NULL,
  `messageSubjectStatesSubstatesListCount` int(11) DEFAULT NULL,
  `messageSubjectEntitiesTermListCount` int(11) DEFAULT NULL,
  `messageSubjectDecisionTermListCount` int(11) DEFAULT NULL,
  `messageSubjectVerbListCount` int(11) DEFAULT NULL,
  `messageSubjectProposalIdentifiersTermListCount` int(11) DEFAULT NULL,
  `messageSubjectSpecialTermListCount` int(11) DEFAULT NULL,
  `containsReason` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `proposal` (`proposal`),
  KEY `messageid` (`messageid`)
) ENGINE=InnoDB AUTO_INCREMENT=187156 DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

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

-- Dumping structure for table bips_new.allbipstillbdfl
CREATE TABLE IF NOT EXISTS `allbipstillbdfl` (
  `bip` int(11) DEFAULT NULL,
  `state` longtext,
  `DATE2` date DEFAULT NULL,
  `biptype` text,
  `P2` int(11) DEFAULT NULL,
  `author` text,
  `bdfl_delegatecorrected` text
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

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

-- Dumping structure for table bips_new.allsentences
CREATE TABLE IF NOT EXISTS `allsentences` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `proposal` int(11) DEFAULT NULL,
  `messageid` int(11) DEFAULT NULL,
  `sentence` longtext NOT NULL,
  `lastdir` mediumtext NOT NULL,
  `isCorefParagraph` tinyint(4) NOT NULL,
  `previousSentence` longtext,
  `nextSentence` text,
  `msgSubject` text,
  `entireParagraph` text,
  `previousParagraph` text,
  `nextParagraph` text,
  `locationMatched` tinytext,
  `label` text,
  `msgAuthorRole` text,
  `isEnglishOrCode` tinyint(4) DEFAULT NULL,
  `isFirstParagraph` tinyint(4) DEFAULT NULL,
  `isLastParagraph` tinyint(4) DEFAULT NULL,
  `paragraphCounter` int(11) DEFAULT NULL,
  `sentenceCounterinParagraph` int(11) DEFAULT NULL,
  `sentenceCounterinMessage` int(11) DEFAULT NULL,
  `reasonTerms` text,
  `reasonIdentifierTerms` int(11) DEFAULT NULL,
  `stateSubStateTerms` int(11) DEFAULT NULL,
  `entityTerms` int(11) DEFAULT NULL,
  `decisionTerms` int(11) DEFAULT NULL,
  `specialTerms` int(11) DEFAULT NULL,
  `authorRole` int(11) DEFAULT NULL,
  `dateDiff` int(11) DEFAULT NULL,
  `messageSubjectDecisionTerms` int(11) DEFAULT NULL,
  `messageSubjectStateSubStateTerms` int(11) DEFAULT NULL,
  `messageSubjectProposalIdentifier` int(11) DEFAULT NULL,
  `reasonsTermsFoundCount` int(11) DEFAULT NULL,
  `reasonsIdentifierTermsCount` int(11) DEFAULT NULL,
  `statesSubstatesCount` int(11) DEFAULT NULL,
  `identifiersTermCount` int(11) DEFAULT NULL,
  `entitiesTermCount` int(11) DEFAULT NULL,
  `specialTermCount` int(11) DEFAULT NULL,
  `decisionTermCount` int(11) DEFAULT NULL,
  `negationTermCount` int(11) DEFAULT NULL,
  `positiveWordCount` int(11) DEFAULT NULL,
  `negativeWordCount` int(11) DEFAULT NULL,
  `messageSubjectStatesSubstatesListCount` int(11) DEFAULT NULL,
  `messageSubjectEntitiesTermListCount` int(11) DEFAULT NULL,
  `messageSubjectDecisionTermListCount` int(11) DEFAULT NULL,
  `messageSubjectVerbListCount` int(11) DEFAULT NULL,
  `messageSubjectProposalIdentifiersTermListCount` int(11) DEFAULT NULL,
  `messageSubjectSpecialTermListCount` int(11) DEFAULT NULL,
  `containsReason` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `proposal` (`proposal`),
  KEY `messageid` (`messageid`),
  KEY `paragraphCounter` (`paragraphCounter`)
) ENGINE=InnoDB AUTO_INCREMENT=3079215 DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.allsentences_old
CREATE TABLE IF NOT EXISTS `allsentences_old` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `proposal` int(11) DEFAULT NULL,
  `messageid` int(11) DEFAULT NULL,
  `sentence` longtext NOT NULL,
  `lastdir` mediumtext NOT NULL,
  `isCorefParagraph` tinyint(4) NOT NULL,
  `previousSentence` longtext,
  `nextSentence` text,
  `msgSubject` text,
  `entireParagraph` text,
  `previousParagraph` text,
  `nextParagraph` text,
  `locationMatched` tinytext,
  `label` text,
  `msgAuthorRole` text,
  `isEnglishOrCode` tinyint(4) DEFAULT NULL,
  `isFirstParagraph` tinyint(4) DEFAULT NULL,
  `isLastParagraph` tinyint(4) DEFAULT NULL,
  `paragraphCounter` int(11) DEFAULT NULL,
  `sentenceCounterinParagraph` int(11) DEFAULT NULL,
  `sentenceCounterinMessage` int(11) DEFAULT NULL,
  `reasonTerms` text,
  `reasonIdentifierTerms` int(11) DEFAULT NULL,
  `stateSubStateTerms` int(11) DEFAULT NULL,
  `entityTerms` int(11) DEFAULT NULL,
  `decisionTerms` int(11) DEFAULT NULL,
  `specialTerms` int(11) DEFAULT NULL,
  `authorRole` int(11) DEFAULT NULL,
  `dateDiff` int(11) DEFAULT NULL,
  `messageSubjectDecisionTerms` int(11) DEFAULT NULL,
  `messageSubjectStateSubStateTerms` int(11) DEFAULT NULL,
  `messageSubjectProposalIdentifier` int(11) DEFAULT NULL,
  `reasonsTermsFoundCount` int(11) DEFAULT NULL,
  `reasonsIdentifierTermsCount` int(11) DEFAULT NULL,
  `statesSubstatesCount` int(11) DEFAULT NULL,
  `identifiersTermCount` int(11) DEFAULT NULL,
  `entitiesTermCount` int(11) DEFAULT NULL,
  `specialTermCount` int(11) DEFAULT NULL,
  `decisionTermCount` int(11) DEFAULT NULL,
  `negationTermCount` int(11) DEFAULT NULL,
  `positiveWordCount` int(11) DEFAULT NULL,
  `negativeWordCount` int(11) DEFAULT NULL,
  `messageSubjectStatesSubstatesListCount` int(11) DEFAULT NULL,
  `messageSubjectEntitiesTermListCount` int(11) DEFAULT NULL,
  `messageSubjectDecisionTermListCount` int(11) DEFAULT NULL,
  `messageSubjectVerbListCount` int(11) DEFAULT NULL,
  `messageSubjectProposalIdentifiersTermListCount` int(11) DEFAULT NULL,
  `messageSubjectSpecialTermListCount` int(11) DEFAULT NULL,
  `containsReason` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `proposal` (`proposal`),
  KEY `messageid` (`messageid`)
) ENGINE=InnoDB AUTO_INCREMENT=340054 DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.authorandrole
CREATE TABLE IF NOT EXISTS `authorandrole` (
  `author` mediumtext,
  `authorsrole` mediumtext,
  KEY `index_author_authorandrole` (`author`(20)),
  KEY `index_authorsrole_authorandrole` (`authorsrole`(10))
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.authormatching
CREATE TABLE IF NOT EXISTS `authormatching` (
  `sendername` tinytext,
  `match` tinytext,
  `true` bit(1) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.autoextractedreasoncandidatemessages
CREATE TABLE IF NOT EXISTS `autoextractedreasoncandidatemessages` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `proposal` int(11) DEFAULT NULL,
  `messageID` int(11) DEFAULT NULL,
  `messageSubject` mediumtext,
  `dateValue` date DEFAULT NULL,
  `dateOfStateMessage` date DEFAULT NULL,
  `datediff` int(11) DEFAULT NULL,
  `datediffWeight` int(11) DEFAULT NULL,
  `authorRole` mediumtext,
  `authorRoleWeight` int(11) DEFAULT NULL,
  `message` mediumtext,
  `firstparagraph` mediumtext,
  `lastparagraph` mediumtext,
  `isenglishorcode` tinyint(4) DEFAULT NULL,
  `termsMatched` mediumtext,
  `probability` mediumtext,
  `label` mediumtext,
  `location` text,
  `messageLocationProbabilityScore` double DEFAULT NULL,
  `messageSubjectHintProbablityScore` double DEFAULT NULL,
  `dateDiffProbability` double DEFAULT NULL,
  `authorRoleProbability` double DEFAULT NULL,
  `messageTypeIsReasonMessageProbabilityScore` double DEFAULT NULL,
  `sameMsgSubAsStateTripleProbabilityScore` double DEFAULT NULL,
  `messageSubjectStatesSubstatesList` text,
  `messageSubjectDecisionTermList` text,
  `messageSubjectVerbList` text,
  `messageSubjectIdentifiersTermList` text,
  `messageSubjectEntitiesTermList` text,
  `messageSubjectSpecialTermList` text,
  `messageSubjectContainsIdentifierTerms` tinyint(4) DEFAULT NULL,
  `messageSubjectContainsStatesSubstates` tinyint(4) DEFAULT NULL,
  `messageSubjectContainsDecisionTerms` tinyint(4) DEFAULT NULL,
  `messageSubjectContainsEntitiesTerms` tinyint(4) DEFAULT NULL,
  `messageSubjectContainsSpecialTerms` tinyint(4) DEFAULT NULL,
  `messageContainsSpecialTerm` tinyint(4) DEFAULT NULL,
  `messageContainsSpecialTermProbabilityScore` double DEFAULT NULL,
  `messageSubjectNegationFound` tinyint(4) DEFAULT NULL,
  `messageSubjectProposalIdentifiersTermListCount` int(11) DEFAULT NULL,
  `messageSubjectDecisionTermListCount` int(11) DEFAULT NULL,
  `messageSubjectEntitiesTermListCount` int(11) DEFAULT NULL,
  `messageSubjectSpecialTermListCount` int(11) DEFAULT NULL,
  `messageSubjectStatesSubstatesListCount` int(11) DEFAULT NULL,
  `messageSubjectVerbListCount` int(11) DEFAULT NULL,
  `entireMessageCandidateReasonMatchedCount` int(11) DEFAULT NULL,
  `messageType` tinytext,
  `reasonLabelFoundUsingTripleExtraction` tinyint(4) DEFAULT NULL,
  `messageTypeIsReasonMessage` tinyint(4) DEFAULT NULL,
  `SameMsgSubAsStateTriple` tinyint(4) DEFAULT NULL,
  `sentenceLocationInFirstLastParagraph` tinyint(4) DEFAULT NULL,
  `containsReason` int(11) DEFAULT NULL,
  `containsReasonMultiClass` int(11) DEFAULT NULL,
  `messsageTypeIsReasonMessageProbabilityScore` double DEFAULT NULL,
  `reasonLabelFoundUsingTripleExtractionProbabilityScore` tinyint(4) DEFAULT NULL,
  `totalProbability` double DEFAULT NULL,
  `totalProbabilityOfSentences` double DEFAULT NULL,
  `totalProbabilityOfMessages` double DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `messageID` (`messageID`),
  KEY `proposal` (`proposal`)
) ENGINE=InnoDB AUTO_INCREMENT=64317 DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.autoextractedreasoncandidatemessages_firstmainrun
CREATE TABLE IF NOT EXISTS `autoextractedreasoncandidatemessages_firstmainrun` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `proposal` int(11) DEFAULT NULL,
  `messageID` int(11) DEFAULT NULL,
  `messageSubject` mediumtext,
  `dateValue` date DEFAULT NULL,
  `dateOfStateMessage` date DEFAULT NULL,
  `datediff` int(11) DEFAULT NULL,
  `datediffWeight` int(11) DEFAULT NULL,
  `authorRole` mediumtext,
  `authorRoleWeight` int(11) DEFAULT NULL,
  `message` mediumtext,
  `firstparagraph` mediumtext,
  `lastparagraph` mediumtext,
  `isenglishorcode` tinyint(4) DEFAULT NULL,
  `termsMatched` mediumtext,
  `probability` mediumtext,
  `label` mediumtext,
  `location` text,
  `messageLocationProbabilityScore` double DEFAULT NULL,
  `messageSubjectHintProbablityScore` double DEFAULT NULL,
  `dateDiffProbability` double DEFAULT NULL,
  `authorRoleProbability` double DEFAULT NULL,
  `messageTypeIsReasonMessageProbabilityScore` double DEFAULT NULL,
  `sameMsgSubAsStateTripleProbabilityScore` double DEFAULT NULL,
  `messageSubjectStatesSubstatesList` text,
  `messageSubjectDecisionTermList` text,
  `messageSubjectVerbList` text,
  `messageSubjectIdentifiersTermList` text,
  `messageSubjectEntitiesTermList` text,
  `messageSubjectSpecialTermList` text,
  `messageSubjectContainsIdentifierTerms` tinyint(4) DEFAULT NULL,
  `messageSubjectContainsStatesSubstates` tinyint(4) DEFAULT NULL,
  `messageSubjectContainsDecisionTerms` tinyint(4) DEFAULT NULL,
  `messageSubjectContainsEntitiesTerms` tinyint(4) DEFAULT NULL,
  `messageSubjectContainsSpecialTerms` tinyint(4) DEFAULT NULL,
  `messageContainsSpecialTerm` tinyint(4) DEFAULT NULL,
  `messageContainsSpecialTermProbabilityScore` double DEFAULT NULL,
  `messageSubjectNegationFound` tinyint(4) DEFAULT NULL,
  `messageSubjectProposalIdentifiersTermListCount` int(11) DEFAULT NULL,
  `messageSubjectDecisionTermListCount` int(11) DEFAULT NULL,
  `messageSubjectEntitiesTermListCount` int(11) DEFAULT NULL,
  `messageSubjectSpecialTermListCount` int(11) DEFAULT NULL,
  `messageSubjectStatesSubstatesListCount` int(11) DEFAULT NULL,
  `messageSubjectVerbListCount` int(11) DEFAULT NULL,
  `entireMessageCandidateReasonMatchedCount` int(11) DEFAULT NULL,
  `messageType` tinytext,
  `reasonLabelFoundUsingTripleExtraction` tinyint(4) DEFAULT NULL,
  `messageTypeIsReasonMessage` tinyint(4) DEFAULT NULL,
  `SameMsgSubAsStateTriple` tinyint(4) DEFAULT NULL,
  `sentenceLocationInFirstLastParagraph` tinyint(4) DEFAULT NULL,
  `containsReason` int(11) DEFAULT NULL,
  `containsReasonMultiClass` int(11) DEFAULT NULL,
  `messsageTypeIsReasonMessageProbabilityScore` double DEFAULT NULL,
  `reasonLabelFoundUsingTripleExtractionProbabilityScore` tinyint(4) DEFAULT NULL,
  `totalProbability` double DEFAULT NULL,
  `totalProbabilityOfSentences` double DEFAULT NULL,
  `totalProbabilityOfMessages` double DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `messageID` (`messageID`),
  KEY `proposal` (`proposal`)
) ENGINE=InnoDB AUTO_INCREMENT=82586 DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT COMMENT='first run';

-- Data exporting was unselected.

-- Dumping structure for table bips_new.autoextractedreasoncandidateparagraphs
CREATE TABLE IF NOT EXISTS `autoextractedreasoncandidateparagraphs` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `proposal` int(11) DEFAULT NULL,
  `messageID` int(11) DEFAULT NULL,
  `messageSubject` mediumtext,
  `dateValue` date DEFAULT NULL,
  `dateOfStateMessage` date DEFAULT NULL,
  `datediff` int(11) DEFAULT NULL,
  `datediffWeight` int(11) DEFAULT NULL,
  `authorRole` mediumtext,
  `authorRoleWeight` int(11) DEFAULT NULL,
  `paragraph` mediumtext,
  `firstparagraph` mediumtext,
  `lastparagraph` mediumtext,
  `isenglishorcode` tinyint(4) DEFAULT NULL,
  `termsMatched` mediumtext,
  `probability` mediumtext,
  `label` mediumtext,
  `location` text,
  `paragraphInFirstParagraph` tinyint(4) DEFAULT NULL,
  `paragraphInLastParagraph` tinyint(4) DEFAULT NULL,
  `paragraphHintProbablity` double DEFAULT NULL,
  `ParagraphLocationHintProbability` double DEFAULT NULL,
  `messageLocationProbabilityScore` double DEFAULT NULL,
  `messageSubjectHintProbablityScore` double DEFAULT NULL,
  `negationTermPenalty` double DEFAULT NULL,
  `dateDiffProbability` double DEFAULT NULL,
  `authorRoleProbability` double DEFAULT NULL,
  `nearbyParagraphsProbabilityScore` double DEFAULT NULL,
  `messageTypeIsReasonMessageProbabilityScore` double DEFAULT NULL,
  `sameMsgSubAsStateTripleProbabilityScore` double DEFAULT NULL,
  `paragraphLocationInMessageProbabilityScore` double DEFAULT NULL,
  `prevParagraphSpecialTermProbabilityScore` double DEFAULT NULL,
  `messageSubjectStatesSubstatesList` text,
  `messageSubjectDecisionTermList` text,
  `messageSubjectVerbList` text,
  `messageSubjectIdentifiersTermList` text,
  `messageSubjectEntitiesTermList` text,
  `messageSubjectSpecialTermList` text,
  `messageSubjectContainsIdentifierTerms` tinyint(4) DEFAULT NULL,
  `messageSubjectContainsStatesSubstates` tinyint(4) DEFAULT NULL,
  `messageSubjectContainsDecisionTerms` tinyint(4) DEFAULT NULL,
  `messageSubjectContainsEntitiesTerms` tinyint(4) DEFAULT NULL,
  `messageSubjectContainsSpecialTerms` tinyint(4) DEFAULT NULL,
  `messageContainsSpecialTerm` tinyint(4) DEFAULT NULL,
  `messageContainsSpecialTermProbabilityScore` double DEFAULT NULL,
  `messageSubjectNegationFound` tinyint(4) DEFAULT NULL,
  `messageSubjectProposalIdentifiersTermListCount` int(11) DEFAULT NULL,
  `messageSubjectDecisionTermListCount` int(11) DEFAULT NULL,
  `messageSubjectEntitiesTermListCount` int(11) DEFAULT NULL,
  `messageSubjectSpecialTermListCount` int(11) DEFAULT NULL,
  `messageSubjectStatesSubstatesListCount` int(11) DEFAULT NULL,
  `messageSubjectVerbListCount` int(11) DEFAULT NULL,
  `currParagraphReasonsTermsFoundList` text,
  `currParagraphReasonsIdentifierTermsList` text,
  `currParagraphStatesSubstatesList` text,
  `currParagraphVerbList` text,
  `currParagraphIdentifiersTermList` text,
  `currParagraphEntitiesTermList` text,
  `currParagraphSpecialTermList` text,
  `currParagraphDecisionTermList` text,
  `currParagraphNegationTermList` text,
  `currParagraphPositiveWordList` text,
  `currParagraphNegativeWordList` text,
  `currParagraphContainsReasonsTerms` tinyint(4) DEFAULT NULL,
  `currParagraphContainsStatesSubstates` tinyint(4) DEFAULT NULL,
  `currParagraphContainsIdentifierTerms` tinyint(4) DEFAULT NULL,
  `currParagraphContainsEntitiesTerms` tinyint(4) DEFAULT NULL,
  `currParagraphContainsReasonsIdentifierTerms` tinyint(4) DEFAULT NULL,
  `currParagraphReasonsTermsFoundCount` int(11) DEFAULT NULL,
  `currParagraphReasonsIdentifierTermsCount` int(11) DEFAULT NULL,
  `currParagraphStatesSubstatesCount` int(11) DEFAULT NULL,
  `currParagraphIdentifiersTermCount` int(11) DEFAULT NULL,
  `currParagraphEntitiesTermCount` int(11) DEFAULT NULL,
  `currParagraphSpecialTermCount` int(11) DEFAULT NULL,
  `currParagraphDecisionTermCount` int(11) DEFAULT NULL,
  `currParagraphNegationTermCount` int(11) DEFAULT NULL,
  `currParagraphPositiveWordCount` int(11) DEFAULT NULL,
  `currParagraphNegativeWordCount` int(11) DEFAULT NULL,
  `entireParagraph` text,
  `restOfParagraph` text,
  `nearbyParagraphsStateTermList` text,
  `nearbyParagraphsEntityTermList` text,
  `nearbyParagraphsIdentifiersTermList` text,
  `nearbyParagraphsSpecialTermList` text,
  `nearbyParagraphsDecisionTermList` text,
  `nearbyParagraphsReasonsTermsList` text,
  `nearbyParagraphsContainsStateTerms` int(11) DEFAULT NULL,
  `nearbyParagraphsContainsIdentifierTerms` int(11) DEFAULT NULL,
  `nearbyParagraphsContainsEntitiesTerms` int(11) DEFAULT NULL,
  `nearbyParagraphsContainsSpecialTerms` int(11) DEFAULT NULL,
  `nearbyParagraphsContainsDecisionTerms` int(11) DEFAULT NULL,
  `nearbyParagraphsStateTermsCount` int(11) DEFAULT NULL,
  `nearbyParagraphsEntityTermsCount` int(11) DEFAULT NULL,
  `nearbyParagraphsIdentifiersTermsCount` int(11) DEFAULT NULL,
  `nearbyParagraphsDecisionTermsCount` int(11) DEFAULT NULL,
  `nearbyParagraphsSpecialTermsCount` int(11) DEFAULT NULL,
  `nearbyParagraphsReasonsTermsCount` int(11) DEFAULT NULL,
  `entireMessageCandidateReasonMatchedCount` int(11) DEFAULT NULL,
  `prevParagraphSpecialTermsCount` int(11) DEFAULT NULL,
  `messageType` tinytext,
  `reasonLabelFoundUsingTripleExtraction` tinyint(4) DEFAULT NULL,
  `messageTypeIsReasonMessage` tinyint(4) DEFAULT NULL,
  `SameMsgSubAsStateTriple` tinyint(4) DEFAULT NULL,
  `sentenceLocationInFirstLastParagraph` tinyint(4) DEFAULT NULL,
  `containsReason` int(11) DEFAULT NULL,
  `containsReasonMultiClass` int(11) DEFAULT NULL,
  `messsageTypeIsReasonMessageProbabilityScore` double DEFAULT NULL,
  `RMPS` double DEFAULT NULL,
  `reasonLabelFoundUsingTripleExtractionProbabilityScore` tinyint(4) DEFAULT NULL,
  `totalProbability` double DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `messageID` (`messageID`),
  KEY `proposal` (`proposal`)
) ENGINE=InnoDB AUTO_INCREMENT=111975 DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.autoextractedreasoncandidateparagraphs_main
CREATE TABLE IF NOT EXISTS `autoextractedreasoncandidateparagraphs_main` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `proposal` int(11) DEFAULT NULL,
  `messageID` int(11) DEFAULT NULL,
  `messageSubject` mediumtext,
  `dateValue` date DEFAULT NULL,
  `dateOfStateMessage` date DEFAULT NULL,
  `datediff` int(11) DEFAULT NULL,
  `datediffWeight` int(11) DEFAULT NULL,
  `authorRole` mediumtext,
  `authorRoleWeight` int(11) DEFAULT NULL,
  `paragraph` mediumtext,
  `firstparagraph` mediumtext,
  `lastparagraph` mediumtext,
  `isenglishorcode` tinyint(4) DEFAULT NULL,
  `termsMatched` mediumtext,
  `probability` mediumtext,
  `label` mediumtext,
  `location` text,
  `paragraphInFirstParagraph` tinyint(4) DEFAULT NULL,
  `paragraphInLastParagraph` tinyint(4) DEFAULT NULL,
  `paragraphHintProbablity` double DEFAULT NULL,
  `ParagraphLocationHintProbability` double DEFAULT NULL,
  `messageLocationProbabilityScore` double DEFAULT NULL,
  `messageSubjectHintProbablityScore` double DEFAULT NULL,
  `negationTermPenalty` double DEFAULT NULL,
  `dateDiffProbability` double DEFAULT NULL,
  `authorRoleProbability` double DEFAULT NULL,
  `nearbyParagraphsProbabilityScore` double DEFAULT NULL,
  `messageTypeIsReasonMessageProbabilityScore` double DEFAULT NULL,
  `sameMsgSubAsStateTripleProbabilityScore` double DEFAULT NULL,
  `paragraphLocationInMessageProbabilityScore` double DEFAULT NULL,
  `prevParagraphSpecialTermProbabilityScore` double DEFAULT NULL,
  `messageSubjectStatesSubstatesList` text,
  `messageSubjectDecisionTermList` text,
  `messageSubjectVerbList` text,
  `messageSubjectIdentifiersTermList` text,
  `messageSubjectEntitiesTermList` text,
  `messageSubjectSpecialTermList` text,
  `messageSubjectContainsIdentifierTerms` tinyint(4) DEFAULT NULL,
  `messageSubjectContainsStatesSubstates` tinyint(4) DEFAULT NULL,
  `messageSubjectContainsDecisionTerms` tinyint(4) DEFAULT NULL,
  `messageSubjectContainsEntitiesTerms` tinyint(4) DEFAULT NULL,
  `messageSubjectContainsSpecialTerms` tinyint(4) DEFAULT NULL,
  `messageContainsSpecialTerm` tinyint(4) DEFAULT NULL,
  `messageContainsSpecialTermProbabilityScore` double DEFAULT NULL,
  `messageSubjectNegationFound` tinyint(4) DEFAULT NULL,
  `messageSubjectProposalIdentifiersTermListCount` int(11) DEFAULT NULL,
  `messageSubjectDecisionTermListCount` int(11) DEFAULT NULL,
  `messageSubjectEntitiesTermListCount` int(11) DEFAULT NULL,
  `messageSubjectSpecialTermListCount` int(11) DEFAULT NULL,
  `messageSubjectStatesSubstatesListCount` int(11) DEFAULT NULL,
  `messageSubjectVerbListCount` int(11) DEFAULT NULL,
  `currParagraphReasonsTermsFoundList` text,
  `currParagraphReasonsIdentifierTermsList` text,
  `currParagraphStatesSubstatesList` text,
  `currParagraphVerbList` text,
  `currParagraphIdentifiersTermList` text,
  `currParagraphEntitiesTermList` text,
  `currParagraphSpecialTermList` text,
  `currParagraphDecisionTermList` text,
  `currParagraphNegationTermList` text,
  `currParagraphPositiveWordList` text,
  `currParagraphNegativeWordList` text,
  `currParagraphContainsReasonsTerms` tinyint(4) DEFAULT NULL,
  `currParagraphContainsStatesSubstates` tinyint(4) DEFAULT NULL,
  `currParagraphContainsIdentifierTerms` tinyint(4) DEFAULT NULL,
  `currParagraphContainsEntitiesTerms` tinyint(4) DEFAULT NULL,
  `currParagraphContainsReasonsIdentifierTerms` tinyint(4) DEFAULT NULL,
  `currParagraphReasonsTermsFoundCount` int(11) DEFAULT NULL,
  `currParagraphReasonsIdentifierTermsCount` int(11) DEFAULT NULL,
  `currParagraphStatesSubstatesCount` int(11) DEFAULT NULL,
  `currParagraphIdentifiersTermCount` int(11) DEFAULT NULL,
  `currParagraphEntitiesTermCount` int(11) DEFAULT NULL,
  `currParagraphSpecialTermCount` int(11) DEFAULT NULL,
  `currParagraphDecisionTermCount` int(11) DEFAULT NULL,
  `currParagraphNegationTermCount` int(11) DEFAULT NULL,
  `currParagraphPositiveWordCount` int(11) DEFAULT NULL,
  `currParagraphNegativeWordCount` int(11) DEFAULT NULL,
  `entireParagraph` text,
  `restOfParagraph` text,
  `nearbyParagraphsStateTermList` text,
  `nearbyParagraphsEntityTermList` text,
  `nearbyParagraphsIdentifiersTermList` text,
  `nearbyParagraphsSpecialTermList` text,
  `nearbyParagraphsDecisionTermList` text,
  `nearbyParagraphsReasonsTermsList` text,
  `nearbyParagraphsContainsStateTerms` int(11) DEFAULT NULL,
  `nearbyParagraphsContainsIdentifierTerms` int(11) DEFAULT NULL,
  `nearbyParagraphsContainsEntitiesTerms` int(11) DEFAULT NULL,
  `nearbyParagraphsContainsSpecialTerms` int(11) DEFAULT NULL,
  `nearbyParagraphsContainsDecisionTerms` int(11) DEFAULT NULL,
  `nearbyParagraphsStateTermsCount` int(11) DEFAULT NULL,
  `nearbyParagraphsEntityTermsCount` int(11) DEFAULT NULL,
  `nearbyParagraphsIdentifiersTermsCount` int(11) DEFAULT NULL,
  `nearbyParagraphsDecisionTermsCount` int(11) DEFAULT NULL,
  `nearbyParagraphsSpecialTermsCount` int(11) DEFAULT NULL,
  `nearbyParagraphsReasonsTermsCount` int(11) DEFAULT NULL,
  `entireMessageCandidateReasonMatchedCount` int(11) DEFAULT NULL,
  `prevParagraphSpecialTermsCount` int(11) DEFAULT NULL,
  `messageType` tinytext,
  `reasonLabelFoundUsingTripleExtraction` tinyint(4) DEFAULT NULL,
  `messageTypeIsReasonMessage` tinyint(4) DEFAULT NULL,
  `SameMsgSubAsStateTriple` tinyint(4) DEFAULT NULL,
  `sentenceLocationInFirstLastParagraph` tinyint(4) DEFAULT NULL,
  `containsReason` int(11) DEFAULT NULL,
  `containsReasonMultiClass` int(11) DEFAULT NULL,
  `messsageTypeIsReasonMessageProbabilityScore` double DEFAULT NULL,
  `RMPS` double DEFAULT NULL,
  `reasonLabelFoundUsingTripleExtractionProbabilityScore` tinyint(4) DEFAULT NULL,
  `totalProbability` double DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `messageID` (`messageID`),
  KEY `proposal` (`proposal`)
) ENGINE=InnoDB AUTO_INCREMENT=85991 DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.autoextractedreasoncandidatesentences
CREATE TABLE IF NOT EXISTS `autoextractedreasoncandidatesentences` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `proposal` int(11) DEFAULT NULL,
  `messageID` int(11) DEFAULT NULL,
  `messageSubject` mediumtext,
  `dateValue` date DEFAULT NULL,
  `dateOfStateMessage` date DEFAULT NULL,
  `datediff` int(11) DEFAULT NULL,
  `datediffWeight` int(11) DEFAULT NULL,
  `authorRole` mediumtext,
  `authorRoleWeight` int(11) DEFAULT NULL,
  `sentence` mediumtext,
  `isenglishorcode` tinyint(4) DEFAULT NULL,
  `termsMatched` mediumtext,
  `probability` mediumtext,
  `label` mediumtext,
  `location` text,
  `firstparagraph` mediumtext,
  `lastparagraph` mediumtext,
  `sentenceInFirstParagraph` tinyint(4) DEFAULT NULL,
  `sentenceInLastParagraph` tinyint(4) DEFAULT NULL,
  `sentenceHintProbablity` double DEFAULT NULL,
  `sentenceLocationHintProbability` double DEFAULT NULL,
  `messageLocationProbabilityScore` double DEFAULT NULL,
  `messageSubjectHintProbablityScore` double DEFAULT NULL,
  `negationTermPenalty` double DEFAULT NULL,
  `dateDiffProbability` double DEFAULT NULL,
  `authorRoleProbability` double DEFAULT NULL,
  `restOfParagraphProbabilityScore` double DEFAULT NULL,
  `messageTypeIsReasonMessageProbabilityScore` double DEFAULT NULL,
  `prevParagraphSpecialTermProbabilityScore` double DEFAULT NULL,
  `sameMsgSubAsStateTripleProbabilityScore` double DEFAULT NULL,
  `sentenceLocationInMessageProbabilityScore` double DEFAULT NULL,
  `messageSubjectStatesSubstatesList` text,
  `messageSubjectDecisionTermList` text,
  `messageSubjectVerbList` text,
  `messageSubjectIdentifiersTermList` text,
  `messageSubjectEntitiesTermList` text,
  `messageSubjectSpecialTermList` text,
  `messageSubjectContainsIdentifierTerms` tinyint(4) DEFAULT NULL,
  `messageSubjectContainsStatesSubstates` tinyint(4) DEFAULT NULL,
  `messageSubjectContainsDecisionTerms` tinyint(4) DEFAULT NULL,
  `messageSubjectContainsEntitiesTerms` tinyint(4) DEFAULT NULL,
  `messageSubjectContainsSpecialTerms` tinyint(4) DEFAULT NULL,
  `messageContainsSpecialTerm` tinyint(4) DEFAULT NULL,
  `messageContainsSpecialTermProbabilityScore` double DEFAULT NULL,
  `messageSubjectNegationFound` tinyint(4) DEFAULT NULL,
  `messageSubjectProposalIdentifiersTermListCount` int(11) DEFAULT NULL,
  `messageSubjectDecisionTermListCount` int(11) DEFAULT NULL,
  `messageSubjectEntitiesTermListCount` int(11) DEFAULT NULL,
  `messageSubjectSpecialTermListCount` int(11) DEFAULT NULL,
  `messageSubjectStatesSubstatesListCount` int(11) DEFAULT NULL,
  `messageSubjectVerbListCount` int(11) DEFAULT NULL,
  `currSentenceReasonsTermsFoundList` text,
  `currSentenceReasonsIdentifierTermsList` text,
  `currSentenceStatesSubstatesList` text,
  `currSentenceVerbList` text,
  `currSentenceIdentifiersTermList` text,
  `currSentenceEntitiesTermList` text,
  `currSentenceSpecialTermList` text,
  `currSentenceDecisionTermList` text,
  `currSentenceNegationTermList` text,
  `currSentencePositiveWordList` text,
  `currSentenceNegativeWordList` text,
  `currSentenceContainsReasonsTerms` tinyint(4) DEFAULT NULL,
  `currSentenceContainsStatesSubstates` tinyint(4) DEFAULT NULL,
  `currSentenceContainsIdentifierTerms` tinyint(4) DEFAULT NULL,
  `currSentenceContainsEntitiesTerms` tinyint(4) DEFAULT NULL,
  `currSentenceContainsReasonsIdentifierTerms` tinyint(4) DEFAULT NULL,
  `currSentenceReasonsTermsFoundCount` int(11) DEFAULT NULL,
  `currSentenceReasonsIdentifierTermsCount` int(11) DEFAULT NULL,
  `currSentenceStatesSubstatesCount` int(11) DEFAULT NULL,
  `currSentenceIdentifiersTermCount` int(11) DEFAULT NULL,
  `currSentenceEntitiesTermCount` int(11) DEFAULT NULL,
  `currSentenceSpecialTermCount` int(11) DEFAULT NULL,
  `currSentenceDecisionTermCount` int(11) DEFAULT NULL,
  `currSentenceNegationTermCount` int(11) DEFAULT NULL,
  `currSentencePositiveWordCount` int(11) DEFAULT NULL,
  `currSentenceNegativeWordCount` int(11) DEFAULT NULL,
  `entireParagraph` text,
  `restOfParagraph` text,
  `restOfParagraphStateTermList` text,
  `restOfParagraphEntityTermList` text,
  `restOfParagraphIdentifiersTermList` text,
  `restOfParagraphSpecialTermList` text,
  `restOfParagraphDecisionTermList` text,
  `restOfParagraphReasonsTermsList` text,
  `restOfParagraphContainsStateTerms` int(11) DEFAULT NULL,
  `restOfParagraphContainsIdentifierTerms` int(11) DEFAULT NULL,
  `restOfParagraphContainsEntitiesTerms` int(11) DEFAULT NULL,
  `restOfParagraphContainsSpecialTerms` int(11) DEFAULT NULL,
  `restOfParagraphContainsDecisionTerms` int(11) DEFAULT NULL,
  `restOfParagraphStateTermsCount` int(11) DEFAULT NULL,
  `restOfParagraphEntityTermsCount` int(11) DEFAULT NULL,
  `restOfParagraphIdentifiersTermsCount` int(11) DEFAULT NULL,
  `restOfParagraphDecisionTermsCount` int(11) DEFAULT NULL,
  `restOfParagraphSpecialTermsCount` int(11) DEFAULT NULL,
  `restOfParagraphReasonsTermsCount` int(11) DEFAULT NULL,
  `prevParagraphSpecialTermsCount` int(11) DEFAULT NULL,
  `entireMessageCandidateReasonMatchedCount` int(11) DEFAULT NULL,
  `messageType` tinytext,
  `reasonLabelFoundUsingTripleExtraction` tinyint(4) DEFAULT NULL,
  `messageTypeIsReasonMessage` tinyint(4) DEFAULT NULL,
  `SameMsgSubAsStateTriple` tinyint(4) DEFAULT NULL,
  `rankBySQL` int(11) DEFAULT NULL,
  `rankBySystem` int(11) DEFAULT NULL,
  `sentenceLocationInFirstLastParagraph` tinyint(4) DEFAULT NULL,
  `containsReason` int(11) DEFAULT NULL,
  `containsReasonnew` int(11) DEFAULT NULL,
  `containsReasonMultiClass` int(11) DEFAULT NULL,
  `messsageTypeIsReasonMessageProbabilityScore` double DEFAULT NULL,
  `RMPS` double DEFAULT NULL,
  `totalProbability` double DEFAULT NULL,
  `reasonLabelFoundUsingTripleExtractionProbabilityScore` tinyint(4) DEFAULT NULL,
  `ORIGsentenceHintProbablity` double DEFAULT NULL,
  `ORIGsentenceLocationHintProbability` double DEFAULT NULL,
  `ORIGrestOfParagraphProbabilityScore` double DEFAULT NULL,
  `ORIGmessageLocationProbabilityScore` double DEFAULT NULL,
  `ORIGmessageSubjectHintProbablityScore` double DEFAULT NULL,
  `ORIGdateDiffProbability` double DEFAULT NULL,
  `ORIGauthorRoleProbability` double DEFAULT NULL,
  `ORIGmessageTypeIsReasonMessageProbabilityScore` double DEFAULT NULL,
  `ORIGsentenceLocationInMessageProbabilityScore` double DEFAULT NULL,
  `ORIGsameMsgSubAsStateTripleProbabilityScore` double DEFAULT NULL,
  `ORIGreasonLabelFoundUsingTripleExtractionProbabilityScore` double DEFAULT NULL,
  `ORIGmessageContainsSpecialTermProbabilityScore` double DEFAULT NULL,
  `ORIGprevParagraphSpecialTermProbabilityScore` double DEFAULT NULL,
  `ORIGnegationTermPenalty` double DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `messageID` (`messageID`),
  KEY `proposal` (`proposal`),
  KEY `shp` (`sentenceHintProbablity`),
  KEY `slhp` (`sentenceLocationHintProbability`),
  KEY `ropps` (`restOfParagraphProbabilityScore`),
  KEY `mlps` (`messageLocationProbabilityScore`),
  KEY `mshps` (`messageSubjectHintProbablityScore`),
  KEY `ddps` (`dateDiffProbability`),
  KEY `arp` (`authorRoleProbability`),
  KEY `mtirmps` (`messageTypeIsReasonMessageProbabilityScore`),
  KEY `slimps` (`sentenceLocationInMessageProbabilityScore`),
  KEY `smsastps` (`sameMsgSubAsStateTripleProbabilityScore`),
  KEY `rlfuteps` (`reasonLabelFoundUsingTripleExtractionProbabilityScore`),
  KEY `mcstps` (`messageContainsSpecialTermProbabilityScore`),
  KEY `ppstps` (`prevParagraphSpecialTermProbabilityScore`),
  KEY `ntp` (`negationTermPenalty`),
  KEY `lab` (`label`(8)),
  KEY `loc` (`location`(8)),
  KEY `sentindex` (`sentence`(25)),
  KEY `datevalindex` (`dateValue`),
  KEY `tpindex` (`totalProbability`)
) ENGINE=InnoDB AUTO_INCREMENT=12067 DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.autoextractedreasoncandidatesentences_main
CREATE TABLE IF NOT EXISTS `autoextractedreasoncandidatesentences_main` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `proposal` int(11) DEFAULT NULL,
  `messageID` int(11) DEFAULT NULL,
  `messageSubject` mediumtext,
  `dateValue` date DEFAULT NULL,
  `dateOfStateMessage` date DEFAULT NULL,
  `datediff` int(11) DEFAULT NULL,
  `datediffWeight` int(11) DEFAULT NULL,
  `authorRole` mediumtext,
  `authorRoleWeight` int(11) DEFAULT NULL,
  `sentence` mediumtext,
  `isenglishorcode` tinyint(4) DEFAULT NULL,
  `termsMatched` mediumtext,
  `probability` mediumtext,
  `label` mediumtext,
  `location` text,
  `firstparagraph` mediumtext,
  `lastparagraph` mediumtext,
  `sentenceInFirstParagraph` tinyint(4) DEFAULT NULL,
  `sentenceInLastParagraph` tinyint(4) DEFAULT NULL,
  `sentenceHintProbablity` double DEFAULT NULL,
  `sentenceLocationHintProbability` double DEFAULT NULL,
  `messageLocationProbabilityScore` double DEFAULT NULL,
  `messageSubjectHintProbablityScore` double DEFAULT NULL,
  `negationTermPenalty` double DEFAULT NULL,
  `dateDiffProbability` double DEFAULT NULL,
  `authorRoleProbability` double DEFAULT NULL,
  `restOfParagraphProbabilityScore` double DEFAULT NULL,
  `messageTypeIsReasonMessageProbabilityScore` double DEFAULT NULL,
  `prevParagraphSpecialTermProbabilityScore` double DEFAULT NULL,
  `sameMsgSubAsStateTripleProbabilityScore` double DEFAULT NULL,
  `sentenceLocationInMessageProbabilityScore` double DEFAULT NULL,
  `messageSubjectStatesSubstatesList` text,
  `messageSubjectDecisionTermList` text,
  `messageSubjectVerbList` text,
  `messageSubjectIdentifiersTermList` text,
  `messageSubjectEntitiesTermList` text,
  `messageSubjectSpecialTermList` text,
  `messageSubjectContainsIdentifierTerms` tinyint(4) DEFAULT NULL,
  `messageSubjectContainsStatesSubstates` tinyint(4) DEFAULT NULL,
  `messageSubjectContainsDecisionTerms` tinyint(4) DEFAULT NULL,
  `messageSubjectContainsEntitiesTerms` tinyint(4) DEFAULT NULL,
  `messageSubjectContainsSpecialTerms` tinyint(4) DEFAULT NULL,
  `messageContainsSpecialTerm` tinyint(4) DEFAULT NULL,
  `messageContainsSpecialTermProbabilityScore` double DEFAULT NULL,
  `messageSubjectNegationFound` tinyint(4) DEFAULT NULL,
  `messageSubjectProposalIdentifiersTermListCount` int(11) DEFAULT NULL,
  `messageSubjectDecisionTermListCount` int(11) DEFAULT NULL,
  `messageSubjectEntitiesTermListCount` int(11) DEFAULT NULL,
  `messageSubjectSpecialTermListCount` int(11) DEFAULT NULL,
  `messageSubjectStatesSubstatesListCount` int(11) DEFAULT NULL,
  `messageSubjectVerbListCount` int(11) DEFAULT NULL,
  `currSentenceReasonsTermsFoundList` text,
  `currSentenceReasonsIdentifierTermsList` text,
  `currSentenceStatesSubstatesList` text,
  `currSentenceVerbList` text,
  `currSentenceIdentifiersTermList` text,
  `currSentenceEntitiesTermList` text,
  `currSentenceSpecialTermList` text,
  `currSentenceDecisionTermList` text,
  `currSentenceNegationTermList` text,
  `currSentencePositiveWordList` text,
  `currSentenceNegativeWordList` text,
  `currSentenceContainsReasonsTerms` tinyint(4) DEFAULT NULL,
  `currSentenceContainsStatesSubstates` tinyint(4) DEFAULT NULL,
  `currSentenceContainsIdentifierTerms` tinyint(4) DEFAULT NULL,
  `currSentenceContainsEntitiesTerms` tinyint(4) DEFAULT NULL,
  `currSentenceContainsReasonsIdentifierTerms` tinyint(4) DEFAULT NULL,
  `currSentenceReasonsTermsFoundCount` int(11) DEFAULT NULL,
  `currSentenceReasonsIdentifierTermsCount` int(11) DEFAULT NULL,
  `currSentenceStatesSubstatesCount` int(11) DEFAULT NULL,
  `currSentenceIdentifiersTermCount` int(11) DEFAULT NULL,
  `currSentenceEntitiesTermCount` int(11) DEFAULT NULL,
  `currSentenceSpecialTermCount` int(11) DEFAULT NULL,
  `currSentenceDecisionTermCount` int(11) DEFAULT NULL,
  `currSentenceNegationTermCount` int(11) DEFAULT NULL,
  `currSentencePositiveWordCount` int(11) DEFAULT NULL,
  `currSentenceNegativeWordCount` int(11) DEFAULT NULL,
  `entireParagraph` text,
  `restOfParagraph` text,
  `restOfParagraphStateTermList` text,
  `restOfParagraphEntityTermList` text,
  `restOfParagraphIdentifiersTermList` text,
  `restOfParagraphSpecialTermList` text,
  `restOfParagraphDecisionTermList` text,
  `restOfParagraphReasonsTermsList` text,
  `restOfParagraphContainsStateTerms` int(11) DEFAULT NULL,
  `restOfParagraphContainsIdentifierTerms` int(11) DEFAULT NULL,
  `restOfParagraphContainsEntitiesTerms` int(11) DEFAULT NULL,
  `restOfParagraphContainsSpecialTerms` int(11) DEFAULT NULL,
  `restOfParagraphContainsDecisionTerms` int(11) DEFAULT NULL,
  `restOfParagraphStateTermsCount` int(11) DEFAULT NULL,
  `restOfParagraphEntityTermsCount` int(11) DEFAULT NULL,
  `restOfParagraphIdentifiersTermsCount` int(11) DEFAULT NULL,
  `restOfParagraphDecisionTermsCount` int(11) DEFAULT NULL,
  `restOfParagraphSpecialTermsCount` int(11) DEFAULT NULL,
  `restOfParagraphReasonsTermsCount` int(11) DEFAULT NULL,
  `prevParagraphSpecialTermsCount` int(11) DEFAULT NULL,
  `entireMessageCandidateReasonMatchedCount` int(11) DEFAULT NULL,
  `messageType` tinytext,
  `reasonLabelFoundUsingTripleExtraction` tinyint(4) DEFAULT NULL,
  `messageTypeIsReasonMessage` tinyint(4) DEFAULT NULL,
  `SameMsgSubAsStateTriple` tinyint(4) DEFAULT NULL,
  `rankBySQL` int(11) DEFAULT NULL,
  `rankBySystem` int(11) DEFAULT NULL,
  `sentenceLocationInFirstLastParagraph` tinyint(4) DEFAULT NULL,
  `containsReason` int(11) DEFAULT NULL,
  `containsReasonMultiClass` int(11) DEFAULT NULL,
  `messsageTypeIsReasonMessageProbabilityScore` double DEFAULT NULL,
  `RMPS` double DEFAULT NULL,
  `totalProbability` double DEFAULT NULL,
  `reasonLabelFoundUsingTripleExtractionProbabilityScore` tinyint(4) DEFAULT NULL,
  `ORIGsentenceHintProbablity` double DEFAULT NULL,
  `ORIGsentenceLocationHintProbability` double DEFAULT NULL,
  `ORIGrestOfParagraphProbabilityScore` double DEFAULT NULL,
  `ORIGmessageLocationProbabilityScore` double DEFAULT NULL,
  `ORIGmessageSubjectHintProbablityScore` double DEFAULT NULL,
  `ORIGdateDiffProbability` double DEFAULT NULL,
  `ORIGauthorRoleProbability` double DEFAULT NULL,
  `ORIGmessageTypeIsReasonMessageProbabilityScore` double DEFAULT NULL,
  `ORIGsentenceLocationInMessageProbabilityScore` double DEFAULT NULL,
  `ORIGsameMsgSubAsStateTripleProbabilityScore` double DEFAULT NULL,
  `ORIGreasonLabelFoundUsingTripleExtractionProbabilityScore` double DEFAULT NULL,
  `ORIGmessageContainsSpecialTermProbabilityScore` double DEFAULT NULL,
  `ORIGprevParagraphSpecialTermProbabilityScore` double DEFAULT NULL,
  `ORIGnegationTermPenalty` double DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `messageID` (`messageID`),
  KEY `proposal` (`proposal`),
  KEY `shp` (`sentenceHintProbablity`),
  KEY `slhp` (`sentenceLocationHintProbability`),
  KEY `ropps` (`restOfParagraphProbabilityScore`),
  KEY `mlps` (`messageLocationProbabilityScore`),
  KEY `mshps` (`messageSubjectHintProbablityScore`),
  KEY `ddps` (`dateDiffProbability`),
  KEY `arp` (`authorRoleProbability`),
  KEY `mtirmps` (`messageTypeIsReasonMessageProbabilityScore`),
  KEY `slimps` (`sentenceLocationInMessageProbabilityScore`),
  KEY `smsastps` (`sameMsgSubAsStateTripleProbabilityScore`),
  KEY `rlfuteps` (`reasonLabelFoundUsingTripleExtractionProbabilityScore`),
  KEY `mcstps` (`messageContainsSpecialTermProbabilityScore`),
  KEY `ppstps` (`prevParagraphSpecialTermProbabilityScore`),
  KEY `ntp` (`negationTermPenalty`)
) ENGINE=InnoDB AUTO_INCREMENT=101619 DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.autoextractedreasoncandidatesentences_mainoneused_may
CREATE TABLE IF NOT EXISTS `autoextractedreasoncandidatesentences_mainoneused_may` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `proposal` int(11) DEFAULT NULL,
  `messageID` int(11) DEFAULT NULL,
  `messageSubject` mediumtext,
  `dateValue` date DEFAULT NULL,
  `dateOfStateMessage` date DEFAULT NULL,
  `datediff` int(11) DEFAULT NULL,
  `datediffWeight` int(11) DEFAULT NULL,
  `authorRole` mediumtext,
  `authorRoleWeight` int(11) DEFAULT NULL,
  `sentence` mediumtext,
  `isenglishorcode` tinyint(4) DEFAULT NULL,
  `termsMatched` mediumtext,
  `probability` mediumtext,
  `label` mediumtext,
  `location` text,
  `firstparagraph` mediumtext,
  `lastparagraph` mediumtext,
  `sentenceInFirstParagraph` tinyint(4) DEFAULT NULL,
  `sentenceInLastParagraph` tinyint(4) DEFAULT NULL,
  `sentenceHintProbablity` double DEFAULT NULL,
  `sentenceLocationHintProbability` double DEFAULT NULL,
  `messageLocationProbabilityScore` double DEFAULT NULL,
  `messageSubjectHintProbablityScore` double DEFAULT NULL,
  `negationTermPenalty` double DEFAULT NULL,
  `dateDiffProbability` double DEFAULT NULL,
  `authorRoleProbability` double DEFAULT NULL,
  `restOfParagraphProbabilityScore` double DEFAULT NULL,
  `messageTypeIsReasonMessageProbabilityScore` double DEFAULT NULL,
  `prevParagraphSpecialTermProbabilityScore` double DEFAULT NULL,
  `sameMsgSubAsStateTripleProbabilityScore` double DEFAULT NULL,
  `sentenceLocationInMessageProbabilityScore` double DEFAULT NULL,
  `messageSubjectStatesSubstatesList` text,
  `messageSubjectDecisionTermList` text,
  `messageSubjectVerbList` text,
  `messageSubjectIdentifiersTermList` text,
  `messageSubjectEntitiesTermList` text,
  `messageSubjectSpecialTermList` text,
  `messageSubjectContainsIdentifierTerms` tinyint(4) DEFAULT NULL,
  `messageSubjectContainsStatesSubstates` tinyint(4) DEFAULT NULL,
  `messageSubjectContainsDecisionTerms` tinyint(4) DEFAULT NULL,
  `messageSubjectContainsEntitiesTerms` tinyint(4) DEFAULT NULL,
  `messageSubjectContainsSpecialTerms` tinyint(4) DEFAULT NULL,
  `messageContainsSpecialTerm` tinyint(4) DEFAULT NULL,
  `messageContainsSpecialTermProbabilityScore` double DEFAULT NULL,
  `messageSubjectNegationFound` tinyint(4) DEFAULT NULL,
  `messageSubjectProposalIdentifiersTermListCount` int(11) DEFAULT NULL,
  `messageSubjectDecisionTermListCount` int(11) DEFAULT NULL,
  `messageSubjectEntitiesTermListCount` int(11) DEFAULT NULL,
  `messageSubjectSpecialTermListCount` int(11) DEFAULT NULL,
  `messageSubjectStatesSubstatesListCount` int(11) DEFAULT NULL,
  `messageSubjectVerbListCount` int(11) DEFAULT NULL,
  `currSentenceReasonsTermsFoundList` text,
  `currSentenceReasonsIdentifierTermsList` text,
  `currSentenceStatesSubstatesList` text,
  `currSentenceVerbList` text,
  `currSentenceIdentifiersTermList` text,
  `currSentenceEntitiesTermList` text,
  `currSentenceSpecialTermList` text,
  `currSentenceDecisionTermList` text,
  `currSentenceNegationTermList` text,
  `currSentencePositiveWordList` text,
  `currSentenceNegativeWordList` text,
  `currSentenceContainsReasonsTerms` tinyint(4) DEFAULT NULL,
  `currSentenceContainsStatesSubstates` tinyint(4) DEFAULT NULL,
  `currSentenceContainsIdentifierTerms` tinyint(4) DEFAULT NULL,
  `currSentenceContainsEntitiesTerms` tinyint(4) DEFAULT NULL,
  `currSentenceContainsReasonsIdentifierTerms` tinyint(4) DEFAULT NULL,
  `currSentenceReasonsTermsFoundCount` int(11) DEFAULT NULL,
  `currSentenceReasonsIdentifierTermsCount` int(11) DEFAULT NULL,
  `currSentenceStatesSubstatesCount` int(11) DEFAULT NULL,
  `currSentenceIdentifiersTermCount` int(11) DEFAULT NULL,
  `currSentenceEntitiesTermCount` int(11) DEFAULT NULL,
  `currSentenceSpecialTermCount` int(11) DEFAULT NULL,
  `currSentenceDecisionTermCount` int(11) DEFAULT NULL,
  `currSentenceNegationTermCount` int(11) DEFAULT NULL,
  `currSentencePositiveWordCount` int(11) DEFAULT NULL,
  `currSentenceNegativeWordCount` int(11) DEFAULT NULL,
  `entireParagraph` text,
  `restOfParagraph` text,
  `restOfParagraphStateTermList` text,
  `restOfParagraphEntityTermList` text,
  `restOfParagraphIdentifiersTermList` text,
  `restOfParagraphSpecialTermList` text,
  `restOfParagraphDecisionTermList` text,
  `restOfParagraphReasonsTermsList` text,
  `restOfParagraphContainsStateTerms` int(11) DEFAULT NULL,
  `restOfParagraphContainsIdentifierTerms` int(11) DEFAULT NULL,
  `restOfParagraphContainsEntitiesTerms` int(11) DEFAULT NULL,
  `restOfParagraphContainsSpecialTerms` int(11) DEFAULT NULL,
  `restOfParagraphContainsDecisionTerms` int(11) DEFAULT NULL,
  `restOfParagraphStateTermsCount` int(11) DEFAULT NULL,
  `restOfParagraphEntityTermsCount` int(11) DEFAULT NULL,
  `restOfParagraphIdentifiersTermsCount` int(11) DEFAULT NULL,
  `restOfParagraphDecisionTermsCount` int(11) DEFAULT NULL,
  `restOfParagraphSpecialTermsCount` int(11) DEFAULT NULL,
  `restOfParagraphReasonsTermsCount` int(11) DEFAULT NULL,
  `prevParagraphSpecialTermsCount` int(11) DEFAULT NULL,
  `entireMessageCandidateReasonMatchedCount` int(11) DEFAULT NULL,
  `messageType` tinytext,
  `reasonLabelFoundUsingTripleExtraction` tinyint(4) DEFAULT NULL,
  `messageTypeIsReasonMessage` tinyint(4) DEFAULT NULL,
  `SameMsgSubAsStateTriple` tinyint(4) DEFAULT NULL,
  `rankBySQL` int(11) DEFAULT NULL,
  `rankBySystem` int(11) DEFAULT NULL,
  `sentenceLocationInFirstLastParagraph` tinyint(4) DEFAULT NULL,
  `containsReason` int(11) DEFAULT NULL,
  `containsReasonnew` int(11) DEFAULT NULL,
  `containsReasonMultiClass` int(11) DEFAULT NULL,
  `messsageTypeIsReasonMessageProbabilityScore` double DEFAULT NULL,
  `RMPS` double DEFAULT NULL,
  `totalProbability` double DEFAULT NULL,
  `reasonLabelFoundUsingTripleExtractionProbabilityScore` tinyint(4) DEFAULT NULL,
  `ORIGsentenceHintProbablity` double DEFAULT NULL,
  `ORIGsentenceLocationHintProbability` double DEFAULT NULL,
  `ORIGrestOfParagraphProbabilityScore` double DEFAULT NULL,
  `ORIGmessageLocationProbabilityScore` double DEFAULT NULL,
  `ORIGmessageSubjectHintProbablityScore` double DEFAULT NULL,
  `ORIGdateDiffProbability` double DEFAULT NULL,
  `ORIGauthorRoleProbability` double DEFAULT NULL,
  `ORIGmessageTypeIsReasonMessageProbabilityScore` double DEFAULT NULL,
  `ORIGsentenceLocationInMessageProbabilityScore` double DEFAULT NULL,
  `ORIGsameMsgSubAsStateTripleProbabilityScore` double DEFAULT NULL,
  `ORIGreasonLabelFoundUsingTripleExtractionProbabilityScore` double DEFAULT NULL,
  `ORIGmessageContainsSpecialTermProbabilityScore` double DEFAULT NULL,
  `ORIGprevParagraphSpecialTermProbabilityScore` double DEFAULT NULL,
  `ORIGnegationTermPenalty` double DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `messageID` (`messageID`),
  KEY `proposal` (`proposal`),
  KEY `shp` (`sentenceHintProbablity`),
  KEY `slhp` (`sentenceLocationHintProbability`),
  KEY `ropps` (`restOfParagraphProbabilityScore`),
  KEY `mlps` (`messageLocationProbabilityScore`),
  KEY `mshps` (`messageSubjectHintProbablityScore`),
  KEY `ddps` (`dateDiffProbability`),
  KEY `arp` (`authorRoleProbability`),
  KEY `mtirmps` (`messageTypeIsReasonMessageProbabilityScore`),
  KEY `slimps` (`sentenceLocationInMessageProbabilityScore`),
  KEY `smsastps` (`sameMsgSubAsStateTripleProbabilityScore`),
  KEY `rlfuteps` (`reasonLabelFoundUsingTripleExtractionProbabilityScore`),
  KEY `mcstps` (`messageContainsSpecialTermProbabilityScore`),
  KEY `ppstps` (`prevParagraphSpecialTermProbabilityScore`),
  KEY `ntp` (`negationTermPenalty`),
  KEY `lab` (`label`(8)),
  KEY `loc` (`location`(8)),
  KEY `sentindex` (`sentence`(25)),
  KEY `datevalindex` (`dateValue`),
  KEY `tpindex` (`totalProbability`)
) ENGINE=InnoDB AUTO_INCREMENT=105101 DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT COMMENT='This is the main used used till may 2020';

-- Data exporting was unselected.

-- Dumping structure for table bips_new.autoextractedreasoncandidatesentences_nodupsranked_msg
CREATE TABLE IF NOT EXISTS `autoextractedreasoncandidatesentences_nodupsranked_msg` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `proposal` int(11) DEFAULT NULL,
  `messageID` int(11) DEFAULT NULL,
  `messageSubject` mediumtext,
  `dateValue` date DEFAULT NULL,
  `dateOfStateMessage` date DEFAULT NULL,
  `datediff` int(11) DEFAULT NULL,
  `datediffWeight` int(11) DEFAULT NULL,
  `authorRole` mediumtext,
  `authorRoleWeight` int(11) DEFAULT NULL,
  `sentence` mediumtext,
  `isenglishorcode` tinyint(4) DEFAULT NULL,
  `termsMatched` mediumtext,
  `probability` mediumtext,
  `label` mediumtext,
  `location` text,
  `firstparagraph` mediumtext,
  `lastparagraph` mediumtext,
  `sentenceInFirstParagraph` tinyint(4) DEFAULT NULL,
  `sentenceInLastParagraph` tinyint(4) DEFAULT NULL,
  `sentenceHintProbablity` double DEFAULT NULL,
  `sentenceLocationHintProbability` double DEFAULT NULL,
  `messageLocationProbabilityScore` double DEFAULT NULL,
  `messageSubjectHintProbablityScore` double DEFAULT NULL,
  `negationTermPenalty` double DEFAULT NULL,
  `dateDiffProbability` double DEFAULT NULL,
  `authorRoleProbability` double DEFAULT NULL,
  `restOfParagraphProbabilityScore` double DEFAULT NULL,
  `messageTypeIsReasonMessageProbabilityScore` double DEFAULT NULL,
  `prevParagraphSpecialTermProbabilityScore` double DEFAULT NULL,
  `sameMsgSubAsStateTripleProbabilityScore` double DEFAULT NULL,
  `sentenceLocationInMessageProbabilityScore` double DEFAULT NULL,
  `messageSubjectStatesSubstatesList` text,
  `messageSubjectDecisionTermList` text,
  `messageSubjectVerbList` text,
  `messageSubjectIdentifiersTermList` text,
  `messageSubjectEntitiesTermList` text,
  `messageSubjectSpecialTermList` text,
  `messageSubjectContainsIdentifierTerms` tinyint(4) DEFAULT NULL,
  `messageSubjectContainsStatesSubstates` tinyint(4) DEFAULT NULL,
  `messageSubjectContainsDecisionTerms` tinyint(4) DEFAULT NULL,
  `messageSubjectContainsEntitiesTerms` tinyint(4) DEFAULT NULL,
  `messageSubjectContainsSpecialTerms` tinyint(4) DEFAULT NULL,
  `messageContainsSpecialTerm` tinyint(4) DEFAULT NULL,
  `messageContainsSpecialTermProbabilityScore` double DEFAULT NULL,
  `messageSubjectNegationFound` tinyint(4) DEFAULT NULL,
  `messageSubjectProposalIdentifiersTermListCount` int(11) DEFAULT NULL,
  `messageSubjectDecisionTermListCount` int(11) DEFAULT NULL,
  `messageSubjectEntitiesTermListCount` int(11) DEFAULT NULL,
  `messageSubjectSpecialTermListCount` int(11) DEFAULT NULL,
  `messageSubjectStatesSubstatesListCount` int(11) DEFAULT NULL,
  `messageSubjectVerbListCount` int(11) DEFAULT NULL,
  `currSentenceReasonsTermsFoundList` text,
  `currSentenceReasonsIdentifierTermsList` text,
  `currSentenceStatesSubstatesList` text,
  `currSentenceVerbList` text,
  `currSentenceIdentifiersTermList` text,
  `currSentenceEntitiesTermList` text,
  `currSentenceSpecialTermList` text,
  `currSentenceDecisionTermList` text,
  `currSentenceNegationTermList` text,
  `currSentencePositiveWordList` text,
  `currSentenceNegativeWordList` text,
  `currSentenceContainsReasonsTerms` tinyint(4) DEFAULT NULL,
  `currSentenceContainsStatesSubstates` tinyint(4) DEFAULT NULL,
  `currSentenceContainsIdentifierTerms` tinyint(4) DEFAULT NULL,
  `currSentenceContainsEntitiesTerms` tinyint(4) DEFAULT NULL,
  `currSentenceContainsReasonsIdentifierTerms` tinyint(4) DEFAULT NULL,
  `currSentenceReasonsTermsFoundCount` int(11) DEFAULT NULL,
  `currSentenceReasonsIdentifierTermsCount` int(11) DEFAULT NULL,
  `currSentenceStatesSubstatesCount` int(11) DEFAULT NULL,
  `currSentenceIdentifiersTermCount` int(11) DEFAULT NULL,
  `currSentenceEntitiesTermCount` int(11) DEFAULT NULL,
  `currSentenceSpecialTermCount` int(11) DEFAULT NULL,
  `currSentenceDecisionTermCount` int(11) DEFAULT NULL,
  `currSentenceNegationTermCount` int(11) DEFAULT NULL,
  `currSentencePositiveWordCount` int(11) DEFAULT NULL,
  `currSentenceNegativeWordCount` int(11) DEFAULT NULL,
  `entireParagraph` text,
  `restOfParagraph` text,
  `restOfParagraphStateTermList` text,
  `restOfParagraphEntityTermList` text,
  `restOfParagraphIdentifiersTermList` text,
  `restOfParagraphSpecialTermList` text,
  `restOfParagraphDecisionTermList` text,
  `restOfParagraphReasonsTermsList` text,
  `restOfParagraphContainsStateTerms` int(11) DEFAULT NULL,
  `restOfParagraphContainsIdentifierTerms` int(11) DEFAULT NULL,
  `restOfParagraphContainsEntitiesTerms` int(11) DEFAULT NULL,
  `restOfParagraphContainsSpecialTerms` int(11) DEFAULT NULL,
  `restOfParagraphContainsDecisionTerms` int(11) DEFAULT NULL,
  `restOfParagraphStateTermsCount` int(11) DEFAULT NULL,
  `restOfParagraphEntityTermsCount` int(11) DEFAULT NULL,
  `restOfParagraphIdentifiersTermsCount` int(11) DEFAULT NULL,
  `restOfParagraphDecisionTermsCount` int(11) DEFAULT NULL,
  `restOfParagraphSpecialTermsCount` int(11) DEFAULT NULL,
  `restOfParagraphReasonsTermsCount` int(11) DEFAULT NULL,
  `prevParagraphSpecialTermsCount` int(11) DEFAULT NULL,
  `entireMessageCandidateReasonMatchedCount` int(11) DEFAULT NULL,
  `messageType` tinytext,
  `reasonLabelFoundUsingTripleExtraction` tinyint(4) DEFAULT NULL,
  `messageTypeIsReasonMessage` tinyint(4) DEFAULT NULL,
  `SameMsgSubAsStateTriple` tinyint(4) DEFAULT NULL,
  `rank` int(11) DEFAULT NULL COMMENT 'WAS RANKBYSQL B4',
  `rankBySystem` int(11) DEFAULT NULL,
  `sentenceLocationInFirstLastParagraph` tinyint(4) DEFAULT NULL,
  `containsReason` int(11) DEFAULT NULL,
  `containsReasonMultiClass` int(11) DEFAULT NULL,
  `messsageTypeIsReasonMessageProbabilityScore` double DEFAULT NULL,
  `RMPS` double DEFAULT NULL,
  `totalProbability` double DEFAULT NULL,
  `reasonLabelFoundUsingTripleExtractionProbabilityScore` tinyint(4) DEFAULT NULL,
  `ORIGsentenceHintProbablity` double DEFAULT NULL,
  `ORIGsentenceLocationHintProbability` double DEFAULT NULL,
  `ORIGrestOfParagraphProbabilityScore` double DEFAULT NULL,
  `ORIGmessageLocationProbabilityScore` double DEFAULT NULL,
  `ORIGmessageSubjectHintProbablityScore` double DEFAULT NULL,
  `ORIGdateDiffProbability` double DEFAULT NULL,
  `ORIGauthorRoleProbability` double DEFAULT NULL,
  `ORIGmessageTypeIsReasonMessageProbabilityScore` double DEFAULT NULL,
  `ORIGsentenceLocationInMessageProbabilityScore` double DEFAULT NULL,
  `ORIGsameMsgSubAsStateTripleProbabilityScore` double DEFAULT NULL,
  `ORIGreasonLabelFoundUsingTripleExtractionProbabilityScore` double DEFAULT NULL,
  `ORIGmessageContainsSpecialTermProbabilityScore` double DEFAULT NULL,
  `ORIGprevParagraphSpecialTermProbabilityScore` double DEFAULT NULL,
  `ORIGnegationTermPenalty` double DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `messageID` (`messageID`),
  KEY `proposal` (`proposal`),
  KEY `shp` (`sentenceHintProbablity`),
  KEY `slhp` (`sentenceLocationHintProbability`),
  KEY `ropps` (`restOfParagraphProbabilityScore`),
  KEY `mlps` (`messageLocationProbabilityScore`),
  KEY `mshps` (`messageSubjectHintProbablityScore`),
  KEY `ddps` (`dateDiffProbability`),
  KEY `arp` (`authorRoleProbability`),
  KEY `mtirmps` (`messageTypeIsReasonMessageProbabilityScore`),
  KEY `slimps` (`sentenceLocationInMessageProbabilityScore`),
  KEY `smsastps` (`sameMsgSubAsStateTripleProbabilityScore`),
  KEY `rlfuteps` (`reasonLabelFoundUsingTripleExtractionProbabilityScore`),
  KEY `mcstps` (`messageContainsSpecialTermProbabilityScore`),
  KEY `ppstps` (`prevParagraphSpecialTermProbabilityScore`),
  KEY `ntp` (`negationTermPenalty`),
  KEY `lab` (`label`(8)),
  KEY `loc` (`location`(8)),
  KEY `sentindex` (`sentence`(25)),
  KEY `datevalindex` (`dateValue`),
  KEY `tpindex` (`totalProbability`)
) ENGINE=InnoDB AUTO_INCREMENT=43948 DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.autoextractedreasoncandidatesentences_nodupsranked_msg_b2
CREATE TABLE IF NOT EXISTS `autoextractedreasoncandidatesentences_nodupsranked_msg_b2` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `proposal` int(11) DEFAULT NULL,
  `messageID` int(11) DEFAULT NULL,
  `messageSubject` mediumtext,
  `dateValue` date DEFAULT NULL,
  `dateOfStateMessage` date DEFAULT NULL,
  `datediff` int(11) DEFAULT NULL,
  `datediffWeight` int(11) DEFAULT NULL,
  `authorRole` mediumtext,
  `authorRoleWeight` int(11) DEFAULT NULL,
  `sentence` mediumtext,
  `isenglishorcode` tinyint(4) DEFAULT NULL,
  `termsMatched` mediumtext,
  `probability` mediumtext,
  `label` mediumtext,
  `location` text,
  `firstparagraph` mediumtext,
  `lastparagraph` mediumtext,
  `sentenceInFirstParagraph` tinyint(4) DEFAULT NULL,
  `sentenceInLastParagraph` tinyint(4) DEFAULT NULL,
  `sentenceHintProbablity` double DEFAULT NULL,
  `sentenceLocationHintProbability` double DEFAULT NULL,
  `messageLocationProbabilityScore` double DEFAULT NULL,
  `messageSubjectHintProbablityScore` double DEFAULT NULL,
  `negationTermPenalty` double DEFAULT NULL,
  `dateDiffProbability` double DEFAULT NULL,
  `authorRoleProbability` double DEFAULT NULL,
  `restOfParagraphProbabilityScore` double DEFAULT NULL,
  `messageTypeIsReasonMessageProbabilityScore` double DEFAULT NULL,
  `prevParagraphSpecialTermProbabilityScore` double DEFAULT NULL,
  `sameMsgSubAsStateTripleProbabilityScore` double DEFAULT NULL,
  `sentenceLocationInMessageProbabilityScore` double DEFAULT NULL,
  `messageSubjectStatesSubstatesList` text,
  `messageSubjectDecisionTermList` text,
  `messageSubjectVerbList` text,
  `messageSubjectIdentifiersTermList` text,
  `messageSubjectEntitiesTermList` text,
  `messageSubjectSpecialTermList` text,
  `messageSubjectContainsIdentifierTerms` tinyint(4) DEFAULT NULL,
  `messageSubjectContainsStatesSubstates` tinyint(4) DEFAULT NULL,
  `messageSubjectContainsDecisionTerms` tinyint(4) DEFAULT NULL,
  `messageSubjectContainsEntitiesTerms` tinyint(4) DEFAULT NULL,
  `messageSubjectContainsSpecialTerms` tinyint(4) DEFAULT NULL,
  `messageContainsSpecialTerm` tinyint(4) DEFAULT NULL,
  `messageContainsSpecialTermProbabilityScore` double DEFAULT NULL,
  `messageSubjectNegationFound` tinyint(4) DEFAULT NULL,
  `messageSubjectProposalIdentifiersTermListCount` int(11) DEFAULT NULL,
  `messageSubjectDecisionTermListCount` int(11) DEFAULT NULL,
  `messageSubjectEntitiesTermListCount` int(11) DEFAULT NULL,
  `messageSubjectSpecialTermListCount` int(11) DEFAULT NULL,
  `messageSubjectStatesSubstatesListCount` int(11) DEFAULT NULL,
  `messageSubjectVerbListCount` int(11) DEFAULT NULL,
  `currSentenceReasonsTermsFoundList` text,
  `currSentenceReasonsIdentifierTermsList` text,
  `currSentenceStatesSubstatesList` text,
  `currSentenceVerbList` text,
  `currSentenceIdentifiersTermList` text,
  `currSentenceEntitiesTermList` text,
  `currSentenceSpecialTermList` text,
  `currSentenceDecisionTermList` text,
  `currSentenceNegationTermList` text,
  `currSentencePositiveWordList` text,
  `currSentenceNegativeWordList` text,
  `currSentenceContainsReasonsTerms` tinyint(4) DEFAULT NULL,
  `currSentenceContainsStatesSubstates` tinyint(4) DEFAULT NULL,
  `currSentenceContainsIdentifierTerms` tinyint(4) DEFAULT NULL,
  `currSentenceContainsEntitiesTerms` tinyint(4) DEFAULT NULL,
  `currSentenceContainsReasonsIdentifierTerms` tinyint(4) DEFAULT NULL,
  `currSentenceReasonsTermsFoundCount` int(11) DEFAULT NULL,
  `currSentenceReasonsIdentifierTermsCount` int(11) DEFAULT NULL,
  `currSentenceStatesSubstatesCount` int(11) DEFAULT NULL,
  `currSentenceIdentifiersTermCount` int(11) DEFAULT NULL,
  `currSentenceEntitiesTermCount` int(11) DEFAULT NULL,
  `currSentenceSpecialTermCount` int(11) DEFAULT NULL,
  `currSentenceDecisionTermCount` int(11) DEFAULT NULL,
  `currSentenceNegationTermCount` int(11) DEFAULT NULL,
  `currSentencePositiveWordCount` int(11) DEFAULT NULL,
  `currSentenceNegativeWordCount` int(11) DEFAULT NULL,
  `entireParagraph` text,
  `restOfParagraph` text,
  `restOfParagraphStateTermList` text,
  `restOfParagraphEntityTermList` text,
  `restOfParagraphIdentifiersTermList` text,
  `restOfParagraphSpecialTermList` text,
  `restOfParagraphDecisionTermList` text,
  `restOfParagraphReasonsTermsList` text,
  `restOfParagraphContainsStateTerms` int(11) DEFAULT NULL,
  `restOfParagraphContainsIdentifierTerms` int(11) DEFAULT NULL,
  `restOfParagraphContainsEntitiesTerms` int(11) DEFAULT NULL,
  `restOfParagraphContainsSpecialTerms` int(11) DEFAULT NULL,
  `restOfParagraphContainsDecisionTerms` int(11) DEFAULT NULL,
  `restOfParagraphStateTermsCount` int(11) DEFAULT NULL,
  `restOfParagraphEntityTermsCount` int(11) DEFAULT NULL,
  `restOfParagraphIdentifiersTermsCount` int(11) DEFAULT NULL,
  `restOfParagraphDecisionTermsCount` int(11) DEFAULT NULL,
  `restOfParagraphSpecialTermsCount` int(11) DEFAULT NULL,
  `restOfParagraphReasonsTermsCount` int(11) DEFAULT NULL,
  `prevParagraphSpecialTermsCount` int(11) DEFAULT NULL,
  `entireMessageCandidateReasonMatchedCount` int(11) DEFAULT NULL,
  `messageType` tinytext,
  `reasonLabelFoundUsingTripleExtraction` tinyint(4) DEFAULT NULL,
  `messageTypeIsReasonMessage` tinyint(4) DEFAULT NULL,
  `SameMsgSubAsStateTriple` tinyint(4) DEFAULT NULL,
  `rank` int(11) DEFAULT NULL COMMENT 'WAS RANKBYSQL B4',
  `rankBySystem` int(11) DEFAULT NULL,
  `sentenceLocationInFirstLastParagraph` tinyint(4) DEFAULT NULL,
  `containsReason` int(11) DEFAULT NULL,
  `containsReasonMultiClass` int(11) DEFAULT NULL,
  `messsageTypeIsReasonMessageProbabilityScore` double DEFAULT NULL,
  `RMPS` double DEFAULT NULL,
  `totalProbability` double DEFAULT NULL,
  `reasonLabelFoundUsingTripleExtractionProbabilityScore` tinyint(4) DEFAULT NULL,
  `ORIGsentenceHintProbablity` double DEFAULT NULL,
  `ORIGsentenceLocationHintProbability` double DEFAULT NULL,
  `ORIGrestOfParagraphProbabilityScore` double DEFAULT NULL,
  `ORIGmessageLocationProbabilityScore` double DEFAULT NULL,
  `ORIGmessageSubjectHintProbablityScore` double DEFAULT NULL,
  `ORIGdateDiffProbability` double DEFAULT NULL,
  `ORIGauthorRoleProbability` double DEFAULT NULL,
  `ORIGmessageTypeIsReasonMessageProbabilityScore` double DEFAULT NULL,
  `ORIGsentenceLocationInMessageProbabilityScore` double DEFAULT NULL,
  `ORIGsameMsgSubAsStateTripleProbabilityScore` double DEFAULT NULL,
  `ORIGreasonLabelFoundUsingTripleExtractionProbabilityScore` double DEFAULT NULL,
  `ORIGmessageContainsSpecialTermProbabilityScore` double DEFAULT NULL,
  `ORIGprevParagraphSpecialTermProbabilityScore` double DEFAULT NULL,
  `ORIGnegationTermPenalty` double DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `messageID` (`messageID`),
  KEY `proposal` (`proposal`),
  KEY `shp` (`sentenceHintProbablity`),
  KEY `slhp` (`sentenceLocationHintProbability`),
  KEY `ropps` (`restOfParagraphProbabilityScore`),
  KEY `mlps` (`messageLocationProbabilityScore`),
  KEY `mshps` (`messageSubjectHintProbablityScore`),
  KEY `ddps` (`dateDiffProbability`),
  KEY `arp` (`authorRoleProbability`),
  KEY `mtirmps` (`messageTypeIsReasonMessageProbabilityScore`),
  KEY `slimps` (`sentenceLocationInMessageProbabilityScore`),
  KEY `smsastps` (`sameMsgSubAsStateTripleProbabilityScore`),
  KEY `rlfuteps` (`reasonLabelFoundUsingTripleExtractionProbabilityScore`),
  KEY `mcstps` (`messageContainsSpecialTermProbabilityScore`),
  KEY `ppstps` (`prevParagraphSpecialTermProbabilityScore`),
  KEY `ntp` (`negationTermPenalty`),
  KEY `lab` (`label`(8)),
  KEY `loc` (`location`(8)),
  KEY `sentindex` (`sentence`(25)),
  KEY `datevalindex` (`dateValue`),
  KEY `tpindex` (`totalProbability`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.autoextractedreasoncandidatesentences_nodupsranked_sent
CREATE TABLE IF NOT EXISTS `autoextractedreasoncandidatesentences_nodupsranked_sent` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `proposal` int(11) DEFAULT NULL,
  `messageID` int(11) DEFAULT NULL,
  `messageSubject` mediumtext,
  `dateValue` date DEFAULT NULL,
  `dateOfStateMessage` date DEFAULT NULL,
  `datediff` int(11) DEFAULT NULL,
  `datediffWeight` int(11) DEFAULT NULL,
  `authorRole` mediumtext,
  `authorRoleWeight` int(11) DEFAULT NULL,
  `sentence` mediumtext,
  `isenglishorcode` tinyint(4) DEFAULT NULL,
  `termsMatched` mediumtext,
  `probability` mediumtext,
  `label` mediumtext,
  `location` text,
  `firstparagraph` mediumtext,
  `lastparagraph` mediumtext,
  `sentenceInFirstParagraph` tinyint(4) DEFAULT NULL,
  `sentenceInLastParagraph` tinyint(4) DEFAULT NULL,
  `sentenceHintProbablity` double DEFAULT NULL,
  `sentenceLocationHintProbability` double DEFAULT NULL,
  `messageLocationProbabilityScore` double DEFAULT NULL,
  `messageSubjectHintProbablityScore` double DEFAULT NULL,
  `negationTermPenalty` double DEFAULT NULL,
  `dateDiffProbability` double DEFAULT NULL,
  `authorRoleProbability` double DEFAULT NULL,
  `restOfParagraphProbabilityScore` double DEFAULT NULL,
  `messageTypeIsReasonMessageProbabilityScore` double DEFAULT NULL,
  `prevParagraphSpecialTermProbabilityScore` double DEFAULT NULL,
  `sameMsgSubAsStateTripleProbabilityScore` double DEFAULT NULL,
  `sentenceLocationInMessageProbabilityScore` double DEFAULT NULL,
  `messageSubjectStatesSubstatesList` text,
  `messageSubjectDecisionTermList` text,
  `messageSubjectVerbList` text,
  `messageSubjectIdentifiersTermList` text,
  `messageSubjectEntitiesTermList` text,
  `messageSubjectSpecialTermList` text,
  `messageSubjectContainsIdentifierTerms` tinyint(4) DEFAULT NULL,
  `messageSubjectContainsStatesSubstates` tinyint(4) DEFAULT NULL,
  `messageSubjectContainsDecisionTerms` tinyint(4) DEFAULT NULL,
  `messageSubjectContainsEntitiesTerms` tinyint(4) DEFAULT NULL,
  `messageSubjectContainsSpecialTerms` tinyint(4) DEFAULT NULL,
  `messageContainsSpecialTerm` tinyint(4) DEFAULT NULL,
  `messageContainsSpecialTermProbabilityScore` double DEFAULT NULL,
  `messageSubjectNegationFound` tinyint(4) DEFAULT NULL,
  `messageSubjectProposalIdentifiersTermListCount` int(11) DEFAULT NULL,
  `messageSubjectDecisionTermListCount` int(11) DEFAULT NULL,
  `messageSubjectEntitiesTermListCount` int(11) DEFAULT NULL,
  `messageSubjectSpecialTermListCount` int(11) DEFAULT NULL,
  `messageSubjectStatesSubstatesListCount` int(11) DEFAULT NULL,
  `messageSubjectVerbListCount` int(11) DEFAULT NULL,
  `currSentenceReasonsTermsFoundList` text,
  `currSentenceReasonsIdentifierTermsList` text,
  `currSentenceStatesSubstatesList` text,
  `currSentenceVerbList` text,
  `currSentenceIdentifiersTermList` text,
  `currSentenceEntitiesTermList` text,
  `currSentenceSpecialTermList` text,
  `currSentenceDecisionTermList` text,
  `currSentenceNegationTermList` text,
  `currSentencePositiveWordList` text,
  `currSentenceNegativeWordList` text,
  `currSentenceContainsReasonsTerms` tinyint(4) DEFAULT NULL,
  `currSentenceContainsStatesSubstates` tinyint(4) DEFAULT NULL,
  `currSentenceContainsIdentifierTerms` tinyint(4) DEFAULT NULL,
  `currSentenceContainsEntitiesTerms` tinyint(4) DEFAULT NULL,
  `currSentenceContainsReasonsIdentifierTerms` tinyint(4) DEFAULT NULL,
  `currSentenceReasonsTermsFoundCount` int(11) DEFAULT NULL,
  `currSentenceReasonsIdentifierTermsCount` int(11) DEFAULT NULL,
  `currSentenceStatesSubstatesCount` int(11) DEFAULT NULL,
  `currSentenceIdentifiersTermCount` int(11) DEFAULT NULL,
  `currSentenceEntitiesTermCount` int(11) DEFAULT NULL,
  `currSentenceSpecialTermCount` int(11) DEFAULT NULL,
  `currSentenceDecisionTermCount` int(11) DEFAULT NULL,
  `currSentenceNegationTermCount` int(11) DEFAULT NULL,
  `currSentencePositiveWordCount` int(11) DEFAULT NULL,
  `currSentenceNegativeWordCount` int(11) DEFAULT NULL,
  `entireParagraph` text,
  `restOfParagraph` text,
  `restOfParagraphStateTermList` text,
  `restOfParagraphEntityTermList` text,
  `restOfParagraphIdentifiersTermList` text,
  `restOfParagraphSpecialTermList` text,
  `restOfParagraphDecisionTermList` text,
  `restOfParagraphReasonsTermsList` text,
  `restOfParagraphContainsStateTerms` int(11) DEFAULT NULL,
  `restOfParagraphContainsIdentifierTerms` int(11) DEFAULT NULL,
  `restOfParagraphContainsEntitiesTerms` int(11) DEFAULT NULL,
  `restOfParagraphContainsSpecialTerms` int(11) DEFAULT NULL,
  `restOfParagraphContainsDecisionTerms` int(11) DEFAULT NULL,
  `restOfParagraphStateTermsCount` int(11) DEFAULT NULL,
  `restOfParagraphEntityTermsCount` int(11) DEFAULT NULL,
  `restOfParagraphIdentifiersTermsCount` int(11) DEFAULT NULL,
  `restOfParagraphDecisionTermsCount` int(11) DEFAULT NULL,
  `restOfParagraphSpecialTermsCount` int(11) DEFAULT NULL,
  `restOfParagraphReasonsTermsCount` int(11) DEFAULT NULL,
  `prevParagraphSpecialTermsCount` int(11) DEFAULT NULL,
  `entireMessageCandidateReasonMatchedCount` int(11) DEFAULT NULL,
  `messageType` tinytext,
  `reasonLabelFoundUsingTripleExtraction` tinyint(4) DEFAULT NULL,
  `messageTypeIsReasonMessage` tinyint(4) DEFAULT NULL,
  `SameMsgSubAsStateTriple` tinyint(4) DEFAULT NULL,
  `rank` int(11) DEFAULT NULL COMMENT 'WAS RANKBYSQL B4',
  `rankBySystem` int(11) DEFAULT NULL,
  `sentenceLocationInFirstLastParagraph` tinyint(4) DEFAULT NULL,
  `containsReason` int(11) DEFAULT NULL,
  `containsReasonMultiClass` int(11) DEFAULT NULL,
  `messsageTypeIsReasonMessageProbabilityScore` double DEFAULT NULL,
  `RMPS` double DEFAULT NULL,
  `totalProbability` double DEFAULT NULL,
  `reasonLabelFoundUsingTripleExtractionProbabilityScore` tinyint(4) DEFAULT NULL,
  `ORIGsentenceHintProbablity` double DEFAULT NULL,
  `ORIGsentenceLocationHintProbability` double DEFAULT NULL,
  `ORIGrestOfParagraphProbabilityScore` double DEFAULT NULL,
  `ORIGmessageLocationProbabilityScore` double DEFAULT NULL,
  `ORIGmessageSubjectHintProbablityScore` double DEFAULT NULL,
  `ORIGdateDiffProbability` double DEFAULT NULL,
  `ORIGauthorRoleProbability` double DEFAULT NULL,
  `ORIGmessageTypeIsReasonMessageProbabilityScore` double DEFAULT NULL,
  `ORIGsentenceLocationInMessageProbabilityScore` double DEFAULT NULL,
  `ORIGsameMsgSubAsStateTripleProbabilityScore` double DEFAULT NULL,
  `ORIGreasonLabelFoundUsingTripleExtractionProbabilityScore` double DEFAULT NULL,
  `ORIGmessageContainsSpecialTermProbabilityScore` double DEFAULT NULL,
  `ORIGprevParagraphSpecialTermProbabilityScore` double DEFAULT NULL,
  `ORIGnegationTermPenalty` double DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `messageID` (`messageID`),
  KEY `proposal` (`proposal`),
  KEY `shp` (`sentenceHintProbablity`),
  KEY `slhp` (`sentenceLocationHintProbability`),
  KEY `ropps` (`restOfParagraphProbabilityScore`),
  KEY `mlps` (`messageLocationProbabilityScore`),
  KEY `mshps` (`messageSubjectHintProbablityScore`),
  KEY `ddps` (`dateDiffProbability`),
  KEY `arp` (`authorRoleProbability`),
  KEY `mtirmps` (`messageTypeIsReasonMessageProbabilityScore`),
  KEY `slimps` (`sentenceLocationInMessageProbabilityScore`),
  KEY `smsastps` (`sameMsgSubAsStateTripleProbabilityScore`),
  KEY `rlfuteps` (`reasonLabelFoundUsingTripleExtractionProbabilityScore`),
  KEY `mcstps` (`messageContainsSpecialTermProbabilityScore`),
  KEY `ppstps` (`prevParagraphSpecialTermProbabilityScore`),
  KEY `ntp` (`negationTermPenalty`),
  KEY `lab` (`label`(8)),
  KEY `loc` (`location`(8)),
  KEY `sentindex` (`sentence`(25)),
  KEY `datevalindex` (`dateValue`),
  KEY `tpindex` (`totalProbability`)
) ENGINE=InnoDB AUTO_INCREMENT=96495 DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.autoextractedreasoncandidatesentences_nodupsranked_sent_b2
CREATE TABLE IF NOT EXISTS `autoextractedreasoncandidatesentences_nodupsranked_sent_b2` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `proposal` int(11) DEFAULT NULL,
  `messageID` int(11) DEFAULT NULL,
  `messageSubject` mediumtext,
  `dateValue` date DEFAULT NULL,
  `dateOfStateMessage` date DEFAULT NULL,
  `datediff` int(11) DEFAULT NULL,
  `datediffWeight` int(11) DEFAULT NULL,
  `authorRole` mediumtext,
  `authorRoleWeight` int(11) DEFAULT NULL,
  `sentence` mediumtext,
  `isenglishorcode` tinyint(4) DEFAULT NULL,
  `termsMatched` mediumtext,
  `probability` mediumtext,
  `label` mediumtext,
  `location` text,
  `firstparagraph` mediumtext,
  `lastparagraph` mediumtext,
  `sentenceInFirstParagraph` tinyint(4) DEFAULT NULL,
  `sentenceInLastParagraph` tinyint(4) DEFAULT NULL,
  `sentenceHintProbablity` double DEFAULT NULL,
  `sentenceLocationHintProbability` double DEFAULT NULL,
  `messageLocationProbabilityScore` double DEFAULT NULL,
  `messageSubjectHintProbablityScore` double DEFAULT NULL,
  `negationTermPenalty` double DEFAULT NULL,
  `dateDiffProbability` double DEFAULT NULL,
  `authorRoleProbability` double DEFAULT NULL,
  `restOfParagraphProbabilityScore` double DEFAULT NULL,
  `messageTypeIsReasonMessageProbabilityScore` double DEFAULT NULL,
  `prevParagraphSpecialTermProbabilityScore` double DEFAULT NULL,
  `sameMsgSubAsStateTripleProbabilityScore` double DEFAULT NULL,
  `sentenceLocationInMessageProbabilityScore` double DEFAULT NULL,
  `messageSubjectStatesSubstatesList` text,
  `messageSubjectDecisionTermList` text,
  `messageSubjectVerbList` text,
  `messageSubjectIdentifiersTermList` text,
  `messageSubjectEntitiesTermList` text,
  `messageSubjectSpecialTermList` text,
  `messageSubjectContainsIdentifierTerms` tinyint(4) DEFAULT NULL,
  `messageSubjectContainsStatesSubstates` tinyint(4) DEFAULT NULL,
  `messageSubjectContainsDecisionTerms` tinyint(4) DEFAULT NULL,
  `messageSubjectContainsEntitiesTerms` tinyint(4) DEFAULT NULL,
  `messageSubjectContainsSpecialTerms` tinyint(4) DEFAULT NULL,
  `messageContainsSpecialTerm` tinyint(4) DEFAULT NULL,
  `messageContainsSpecialTermProbabilityScore` double DEFAULT NULL,
  `messageSubjectNegationFound` tinyint(4) DEFAULT NULL,
  `messageSubjectProposalIdentifiersTermListCount` int(11) DEFAULT NULL,
  `messageSubjectDecisionTermListCount` int(11) DEFAULT NULL,
  `messageSubjectEntitiesTermListCount` int(11) DEFAULT NULL,
  `messageSubjectSpecialTermListCount` int(11) DEFAULT NULL,
  `messageSubjectStatesSubstatesListCount` int(11) DEFAULT NULL,
  `messageSubjectVerbListCount` int(11) DEFAULT NULL,
  `currSentenceReasonsTermsFoundList` text,
  `currSentenceReasonsIdentifierTermsList` text,
  `currSentenceStatesSubstatesList` text,
  `currSentenceVerbList` text,
  `currSentenceIdentifiersTermList` text,
  `currSentenceEntitiesTermList` text,
  `currSentenceSpecialTermList` text,
  `currSentenceDecisionTermList` text,
  `currSentenceNegationTermList` text,
  `currSentencePositiveWordList` text,
  `currSentenceNegativeWordList` text,
  `currSentenceContainsReasonsTerms` tinyint(4) DEFAULT NULL,
  `currSentenceContainsStatesSubstates` tinyint(4) DEFAULT NULL,
  `currSentenceContainsIdentifierTerms` tinyint(4) DEFAULT NULL,
  `currSentenceContainsEntitiesTerms` tinyint(4) DEFAULT NULL,
  `currSentenceContainsReasonsIdentifierTerms` tinyint(4) DEFAULT NULL,
  `currSentenceReasonsTermsFoundCount` int(11) DEFAULT NULL,
  `currSentenceReasonsIdentifierTermsCount` int(11) DEFAULT NULL,
  `currSentenceStatesSubstatesCount` int(11) DEFAULT NULL,
  `currSentenceIdentifiersTermCount` int(11) DEFAULT NULL,
  `currSentenceEntitiesTermCount` int(11) DEFAULT NULL,
  `currSentenceSpecialTermCount` int(11) DEFAULT NULL,
  `currSentenceDecisionTermCount` int(11) DEFAULT NULL,
  `currSentenceNegationTermCount` int(11) DEFAULT NULL,
  `currSentencePositiveWordCount` int(11) DEFAULT NULL,
  `currSentenceNegativeWordCount` int(11) DEFAULT NULL,
  `entireParagraph` text,
  `restOfParagraph` text,
  `restOfParagraphStateTermList` text,
  `restOfParagraphEntityTermList` text,
  `restOfParagraphIdentifiersTermList` text,
  `restOfParagraphSpecialTermList` text,
  `restOfParagraphDecisionTermList` text,
  `restOfParagraphReasonsTermsList` text,
  `restOfParagraphContainsStateTerms` int(11) DEFAULT NULL,
  `restOfParagraphContainsIdentifierTerms` int(11) DEFAULT NULL,
  `restOfParagraphContainsEntitiesTerms` int(11) DEFAULT NULL,
  `restOfParagraphContainsSpecialTerms` int(11) DEFAULT NULL,
  `restOfParagraphContainsDecisionTerms` int(11) DEFAULT NULL,
  `restOfParagraphStateTermsCount` int(11) DEFAULT NULL,
  `restOfParagraphEntityTermsCount` int(11) DEFAULT NULL,
  `restOfParagraphIdentifiersTermsCount` int(11) DEFAULT NULL,
  `restOfParagraphDecisionTermsCount` int(11) DEFAULT NULL,
  `restOfParagraphSpecialTermsCount` int(11) DEFAULT NULL,
  `restOfParagraphReasonsTermsCount` int(11) DEFAULT NULL,
  `prevParagraphSpecialTermsCount` int(11) DEFAULT NULL,
  `entireMessageCandidateReasonMatchedCount` int(11) DEFAULT NULL,
  `messageType` tinytext,
  `reasonLabelFoundUsingTripleExtraction` tinyint(4) DEFAULT NULL,
  `messageTypeIsReasonMessage` tinyint(4) DEFAULT NULL,
  `SameMsgSubAsStateTriple` tinyint(4) DEFAULT NULL,
  `rank` int(11) DEFAULT NULL COMMENT 'WAS RANKBYSQL B4',
  `rankBySystem` int(11) DEFAULT NULL,
  `sentenceLocationInFirstLastParagraph` tinyint(4) DEFAULT NULL,
  `containsReason` int(11) DEFAULT NULL,
  `containsReasonMultiClass` int(11) DEFAULT NULL,
  `messsageTypeIsReasonMessageProbabilityScore` double DEFAULT NULL,
  `RMPS` double DEFAULT NULL,
  `totalProbability` double DEFAULT NULL,
  `reasonLabelFoundUsingTripleExtractionProbabilityScore` tinyint(4) DEFAULT NULL,
  `ORIGsentenceHintProbablity` double DEFAULT NULL,
  `ORIGsentenceLocationHintProbability` double DEFAULT NULL,
  `ORIGrestOfParagraphProbabilityScore` double DEFAULT NULL,
  `ORIGmessageLocationProbabilityScore` double DEFAULT NULL,
  `ORIGmessageSubjectHintProbablityScore` double DEFAULT NULL,
  `ORIGdateDiffProbability` double DEFAULT NULL,
  `ORIGauthorRoleProbability` double DEFAULT NULL,
  `ORIGmessageTypeIsReasonMessageProbabilityScore` double DEFAULT NULL,
  `ORIGsentenceLocationInMessageProbabilityScore` double DEFAULT NULL,
  `ORIGsameMsgSubAsStateTripleProbabilityScore` double DEFAULT NULL,
  `ORIGreasonLabelFoundUsingTripleExtractionProbabilityScore` double DEFAULT NULL,
  `ORIGmessageContainsSpecialTermProbabilityScore` double DEFAULT NULL,
  `ORIGprevParagraphSpecialTermProbabilityScore` double DEFAULT NULL,
  `ORIGnegationTermPenalty` double DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `messageID` (`messageID`),
  KEY `proposal` (`proposal`),
  KEY `shp` (`sentenceHintProbablity`),
  KEY `slhp` (`sentenceLocationHintProbability`),
  KEY `ropps` (`restOfParagraphProbabilityScore`),
  KEY `mlps` (`messageLocationProbabilityScore`),
  KEY `mshps` (`messageSubjectHintProbablityScore`),
  KEY `ddps` (`dateDiffProbability`),
  KEY `arp` (`authorRoleProbability`),
  KEY `mtirmps` (`messageTypeIsReasonMessageProbabilityScore`),
  KEY `slimps` (`sentenceLocationInMessageProbabilityScore`),
  KEY `smsastps` (`sameMsgSubAsStateTripleProbabilityScore`),
  KEY `rlfuteps` (`reasonLabelFoundUsingTripleExtractionProbabilityScore`),
  KEY `mcstps` (`messageContainsSpecialTermProbabilityScore`),
  KEY `ppstps` (`prevParagraphSpecialTermProbabilityScore`),
  KEY `ntp` (`negationTermPenalty`),
  KEY `lab` (`label`(8)),
  KEY `loc` (`location`(8)),
  KEY `sentindex` (`sentence`(25)),
  KEY `datevalindex` (`dateValue`),
  KEY `tpindex` (`totalProbability`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.autoextractedreasoncandidatesentences_thesissubmitted
CREATE TABLE IF NOT EXISTS `autoextractedreasoncandidatesentences_thesissubmitted` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `proposal` int(11) DEFAULT NULL,
  `messageID` int(11) DEFAULT NULL,
  `messageSubject` mediumtext,
  `dateValue` date DEFAULT NULL,
  `dateOfStateMessage` date DEFAULT NULL,
  `datediff` int(11) DEFAULT NULL,
  `datediffWeight` int(11) DEFAULT NULL,
  `authorRole` mediumtext,
  `authorRoleWeight` int(11) DEFAULT NULL,
  `sentence` mediumtext,
  `isenglishorcode` tinyint(4) DEFAULT NULL,
  `termsMatched` mediumtext,
  `probability` mediumtext,
  `label` mediumtext,
  `location` text,
  `firstparagraph` mediumtext,
  `lastparagraph` mediumtext,
  `sentenceInFirstParagraph` tinyint(4) DEFAULT NULL,
  `sentenceInLastParagraph` tinyint(4) DEFAULT NULL,
  `sentenceHintProbablity` double DEFAULT NULL,
  `sentenceLocationHintProbability` double DEFAULT NULL,
  `messageLocationProbabilityScore` double DEFAULT NULL,
  `messageSubjectHintProbablityScore` double DEFAULT NULL,
  `negationTermPenalty` double DEFAULT NULL,
  `dateDiffProbability` double DEFAULT NULL,
  `authorRoleProbability` double DEFAULT NULL,
  `restOfParagraphProbabilityScore` double DEFAULT NULL,
  `messageTypeIsReasonMessageProbabilityScore` double DEFAULT NULL,
  `prevParagraphSpecialTermProbabilityScore` double DEFAULT NULL,
  `sameMsgSubAsStateTripleProbabilityScore` double DEFAULT NULL,
  `sentenceLocationInMessageProbabilityScore` double DEFAULT NULL,
  `messageSubjectStatesSubstatesList` text,
  `messageSubjectDecisionTermList` text,
  `messageSubjectVerbList` text,
  `messageSubjectIdentifiersTermList` text,
  `messageSubjectEntitiesTermList` text,
  `messageSubjectSpecialTermList` text,
  `messageSubjectContainsIdentifierTerms` tinyint(4) DEFAULT NULL,
  `messageSubjectContainsStatesSubstates` tinyint(4) DEFAULT NULL,
  `messageSubjectContainsDecisionTerms` tinyint(4) DEFAULT NULL,
  `messageSubjectContainsEntitiesTerms` tinyint(4) DEFAULT NULL,
  `messageSubjectContainsSpecialTerms` tinyint(4) DEFAULT NULL,
  `messageContainsSpecialTerm` tinyint(4) DEFAULT NULL,
  `messageContainsSpecialTermProbabilityScore` double DEFAULT NULL,
  `messageSubjectNegationFound` tinyint(4) DEFAULT NULL,
  `messageSubjectProposalIdentifiersTermListCount` int(11) DEFAULT NULL,
  `messageSubjectDecisionTermListCount` int(11) DEFAULT NULL,
  `messageSubjectEntitiesTermListCount` int(11) DEFAULT NULL,
  `messageSubjectSpecialTermListCount` int(11) DEFAULT NULL,
  `messageSubjectStatesSubstatesListCount` int(11) DEFAULT NULL,
  `messageSubjectVerbListCount` int(11) DEFAULT NULL,
  `currSentenceReasonsTermsFoundList` text,
  `currSentenceReasonsIdentifierTermsList` text,
  `currSentenceStatesSubstatesList` text,
  `currSentenceVerbList` text,
  `currSentenceIdentifiersTermList` text,
  `currSentenceEntitiesTermList` text,
  `currSentenceSpecialTermList` text,
  `currSentenceDecisionTermList` text,
  `currSentenceNegationTermList` text,
  `currSentencePositiveWordList` text,
  `currSentenceNegativeWordList` text,
  `currSentenceContainsReasonsTerms` tinyint(4) DEFAULT NULL,
  `currSentenceContainsStatesSubstates` tinyint(4) DEFAULT NULL,
  `currSentenceContainsIdentifierTerms` tinyint(4) DEFAULT NULL,
  `currSentenceContainsEntitiesTerms` tinyint(4) DEFAULT NULL,
  `currSentenceContainsReasonsIdentifierTerms` tinyint(4) DEFAULT NULL,
  `currSentenceReasonsTermsFoundCount` int(11) DEFAULT NULL,
  `currSentenceReasonsIdentifierTermsCount` int(11) DEFAULT NULL,
  `currSentenceStatesSubstatesCount` int(11) DEFAULT NULL,
  `currSentenceIdentifiersTermCount` int(11) DEFAULT NULL,
  `currSentenceEntitiesTermCount` int(11) DEFAULT NULL,
  `currSentenceSpecialTermCount` int(11) DEFAULT NULL,
  `currSentenceDecisionTermCount` int(11) DEFAULT NULL,
  `currSentenceNegationTermCount` int(11) DEFAULT NULL,
  `currSentencePositiveWordCount` int(11) DEFAULT NULL,
  `currSentenceNegativeWordCount` int(11) DEFAULT NULL,
  `entireParagraph` text,
  `restOfParagraph` text,
  `restOfParagraphStateTermList` text,
  `restOfParagraphEntityTermList` text,
  `restOfParagraphIdentifiersTermList` text,
  `restOfParagraphSpecialTermList` text,
  `restOfParagraphDecisionTermList` text,
  `restOfParagraphReasonsTermsList` text,
  `restOfParagraphContainsStateTerms` int(11) DEFAULT NULL,
  `restOfParagraphContainsIdentifierTerms` int(11) DEFAULT NULL,
  `restOfParagraphContainsEntitiesTerms` int(11) DEFAULT NULL,
  `restOfParagraphContainsSpecialTerms` int(11) DEFAULT NULL,
  `restOfParagraphContainsDecisionTerms` int(11) DEFAULT NULL,
  `restOfParagraphStateTermsCount` int(11) DEFAULT NULL,
  `restOfParagraphEntityTermsCount` int(11) DEFAULT NULL,
  `restOfParagraphIdentifiersTermsCount` int(11) DEFAULT NULL,
  `restOfParagraphDecisionTermsCount` int(11) DEFAULT NULL,
  `restOfParagraphSpecialTermsCount` int(11) DEFAULT NULL,
  `restOfParagraphReasonsTermsCount` int(11) DEFAULT NULL,
  `prevParagraphSpecialTermsCount` int(11) DEFAULT NULL,
  `entireMessageCandidateReasonMatchedCount` int(11) DEFAULT NULL,
  `messageType` tinytext,
  `reasonLabelFoundUsingTripleExtraction` tinyint(4) DEFAULT NULL,
  `messageTypeIsReasonMessage` tinyint(4) DEFAULT NULL,
  `SameMsgSubAsStateTriple` tinyint(4) DEFAULT NULL,
  `rankBySQL` int(11) DEFAULT NULL,
  `rankBySystem` int(11) DEFAULT NULL,
  `sentenceLocationInFirstLastParagraph` tinyint(4) DEFAULT NULL,
  `containsReason` int(11) DEFAULT NULL,
  `containsReasonnew` int(11) DEFAULT NULL,
  `containsReasonMultiClass` int(11) DEFAULT NULL,
  `messsageTypeIsReasonMessageProbabilityScore` double DEFAULT NULL,
  `RMPS` double DEFAULT NULL,
  `totalProbability` double DEFAULT NULL,
  `reasonLabelFoundUsingTripleExtractionProbabilityScore` tinyint(4) DEFAULT NULL,
  `ORIGsentenceHintProbablity` double DEFAULT NULL,
  `ORIGsentenceLocationHintProbability` double DEFAULT NULL,
  `ORIGrestOfParagraphProbabilityScore` double DEFAULT NULL,
  `ORIGmessageLocationProbabilityScore` double DEFAULT NULL,
  `ORIGmessageSubjectHintProbablityScore` double DEFAULT NULL,
  `ORIGdateDiffProbability` double DEFAULT NULL,
  `ORIGauthorRoleProbability` double DEFAULT NULL,
  `ORIGmessageTypeIsReasonMessageProbabilityScore` double DEFAULT NULL,
  `ORIGsentenceLocationInMessageProbabilityScore` double DEFAULT NULL,
  `ORIGsameMsgSubAsStateTripleProbabilityScore` double DEFAULT NULL,
  `ORIGreasonLabelFoundUsingTripleExtractionProbabilityScore` double DEFAULT NULL,
  `ORIGmessageContainsSpecialTermProbabilityScore` double DEFAULT NULL,
  `ORIGprevParagraphSpecialTermProbabilityScore` double DEFAULT NULL,
  `ORIGnegationTermPenalty` double DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `messageID` (`messageID`),
  KEY `proposal` (`proposal`),
  KEY `shp` (`sentenceHintProbablity`),
  KEY `slhp` (`sentenceLocationHintProbability`),
  KEY `ropps` (`restOfParagraphProbabilityScore`),
  KEY `mlps` (`messageLocationProbabilityScore`),
  KEY `mshps` (`messageSubjectHintProbablityScore`),
  KEY `ddps` (`dateDiffProbability`),
  KEY `arp` (`authorRoleProbability`),
  KEY `mtirmps` (`messageTypeIsReasonMessageProbabilityScore`),
  KEY `slimps` (`sentenceLocationInMessageProbabilityScore`),
  KEY `smsastps` (`sameMsgSubAsStateTripleProbabilityScore`),
  KEY `rlfuteps` (`reasonLabelFoundUsingTripleExtractionProbabilityScore`),
  KEY `mcstps` (`messageContainsSpecialTermProbabilityScore`),
  KEY `ppstps` (`prevParagraphSpecialTermProbabilityScore`),
  KEY `ntp` (`negationTermPenalty`),
  KEY `lab` (`label`(8)),
  KEY `loc` (`location`(8)),
  KEY `sentindex` (`sentence`(25)),
  KEY `datevalindex` (`dateValue`),
  KEY `tpindex` (`totalProbability`)
) ENGINE=InnoDB AUTO_INCREMENT=105101 DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.bdflddbips
CREATE TABLE IF NOT EXISTS `bdflddbips` (
  `bip` int(11) DEFAULT NULL,
  `bdfl_delegate` text,
  `bdfl_delegatecorrected` text,
  `created` date DEFAULT NULL,
  `biptype` text
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

-- Dumping structure for table bips_new.checksorting
CREATE TABLE IF NOT EXISTS `checksorting` (
  `proposal` int(11) DEFAULT NULL,
  `state` text,
  `messageID` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

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

-- Dumping structure for table bips_new.columnvalues
CREATE TABLE IF NOT EXISTS `columnvalues` (
  `id` int(11) NOT NULL DEFAULT '0',
  `bip` int(11) DEFAULT NULL,
  `dd` int(11) DEFAULT NULL,
  `ddw` int(11) DEFAULT NULL,
  `sentence` mediumtext,
  `label` mediumtext,
  `sifp` tinyint(4) DEFAULT NULL,
  `silp` tinyint(4) DEFAULT NULL,
  `shp` double DEFAULT NULL,
  `slps` double DEFAULT NULL,
  `mlps` double DEFAULT NULL,
  `mshps` double DEFAULT NULL,
  `ntp` double DEFAULT NULL,
  `ddp` double DEFAULT NULL,
  `arp` double DEFAULT NULL,
  `ropps` double DEFAULT NULL,
  `mtrmps` double DEFAULT NULL,
  `ppstps` double DEFAULT NULL,
  `smstps` double DEFAULT NULL,
  `slimps` double DEFAULT NULL,
  `mcstps` double DEFAULT NULL,
  `mtirm` tinyint(4) DEFAULT NULL,
  `smsst` tinyint(4) DEFAULT NULL,
  `cr` int(11) DEFAULT NULL,
  `tp` double DEFAULT NULL,
  `rlfuteps` tinyint(4) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.communicationsforseeders
CREATE TABLE IF NOT EXISTS `communicationsforseeders` (
  `state` text,
  `bip` int(11) DEFAULT NULL,
  `biptype` text,
  `author` text,
  `totalmsgs` int(11) DEFAULT NULL,
  `msgs_not_replies` int(11) DEFAULT NULL,
  `msgs_are_replies` int(11) DEFAULT NULL,
  `author_again` text,
  `totalInReplyTo` int(11) DEFAULT NULL,
  `summessages` int(11) DEFAULT NULL,
  `dominance_index` float DEFAULT NULL,
  `threadsinvolvement_topLevel` int(11) DEFAULT NULL,
  `threadsinvolvement_secondLevelPosts` int(11) DEFAULT NULL,
  `allthreadsinvolvement` int(11) DEFAULT NULL,
  `folder` tinytext
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.communicationsforsna
CREATE TABLE IF NOT EXISTS `communicationsforsna` (
  `state` text,
  `bip` int(11) DEFAULT NULL,
  `author` text,
  `inReplyToUserUsingClusteredSender` text,
  `cnt` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.comparebaseline
CREATE TABLE IF NOT EXISTS `comparebaseline` (
  `field` text,
  `acc_sent` int(11) DEFAULT NULL,
  `rej_sent` int(11) DEFAULT NULL,
  `acc_msg` int(11) DEFAULT NULL,
  `rej_msg` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.coredevelopers
CREATE TABLE IF NOT EXISTS `coredevelopers` (
  `coredeveloper` tinytext,
  `github` tinytext,
  `dateadded` date DEFAULT NULL
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

-- Dumping structure for table bips_new.datedifftally
CREATE TABLE IF NOT EXISTS `datedifftally` (
  `id` int(11) DEFAULT NULL,
  `datediff` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.debug_coref
CREATE TABLE IF NOT EXISTS `debug_coref` (
  `proposal` int(11) DEFAULT NULL,
  `messageid` int(11) DEFAULT NULL,
  `corefSubject` tinytext,
  `isPrevSentOrSentOrPara` int(11) DEFAULT NULL,
  `text` mediumtext,
  `coref` mediumtext
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

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

-- Dumping structure for table bips_new.drafts
CREATE TABLE IF NOT EXISTS `drafts` (
  `bip` int(11) DEFAULT NULL,
  `originalbipNumber` int(11) DEFAULT NULL,
  `originalbipNumberNum` int(11) DEFAULT NULL,
  `messageID` int(11) DEFAULT NULL,
  `date2` date DEFAULT NULL,
  `bipType` text,
  `dateTimeStamp` timestamp NULL DEFAULT NULL,
  `dateTimeStampString` text,
  `email` longtext,
  `analyseWords` longtext,
  `corefMsg` longtext,
  `Author` mediumtext,
  `subject` mediumtext,
  `location` text,
  `line` longtext,
  `folder` text,
  `file` text,
  `from` text,
  `emailMessageId` tinytext,
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
  `msgNumInFile` int(11) DEFAULT NULL,
  `processedSubject` mediumtext,
  `authorsrole` mediumtext,
  `ifProposalAtTheEndofMessage` tinyint(4) DEFAULT NULL,
  `processMessage` tinyint(4) DEFAULT NULL,
  `pTitleMatchedinMsgSub` tinyint(4) DEFAULT NULL,
  `pNumMatchedinMsgSub` tinyint(4) DEFAULT NULL,
  `pNumMatchedinMsgBody` tinyint(4) DEFAULT NULL,
  `messageType` tinytext,
  `clausie` text
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.dynamicweightallocation
CREATE TABLE IF NOT EXISTS `dynamicweightallocation` (
  `label` text,
  `sentenceHintProbablity` double DEFAULT NULL,
  `sentenceLocationHintProbability` double DEFAULT NULL,
  `restOfParagraphProbabilityScore` double DEFAULT NULL,
  `messageLocationProbabilityScore` double DEFAULT NULL,
  `messageSubjectHintProbablityScore` double DEFAULT NULL,
  `dateDiffProbability` double DEFAULT NULL,
  `authorRoleProbability` double DEFAULT NULL,
  `messageTypeIsReasonMessageProbabilityScore` double DEFAULT NULL,
  `sentenceLocationInMessageProbabilityScore` double DEFAULT NULL,
  `sameMsgSubAsStateTripleProbabilityScore` double DEFAULT NULL,
  `reasonLabelFoundUsingTripleExtractionProbabilityScore` double DEFAULT NULL,
  `messageContainsSpecialTermProbabilityScore` double DEFAULT NULL,
  `prevParagraphSpecialTermProbabilityScore` double DEFAULT NULL,
  `negationTermPenalty` double DEFAULT NULL,
  `top5` int(11) DEFAULT NULL,
  `top10` int(11) DEFAULT NULL,
  `top15` int(11) DEFAULT NULL,
  `top30` int(11) DEFAULT NULL,
  `top50` int(11) DEFAULT NULL,
  `top100` int(11) DEFAULT NULL,
  `outsidetop100` int(11) DEFAULT NULL,
  `notfound` int(11) DEFAULT NULL,
  `totalProbability` double DEFAULT NULL,
  `run` int(11) NOT NULL AUTO_INCREMENT,
  `evalLevel` text,
  PRIMARY KEY (`run`)
) ENGINE=InnoDB AUTO_INCREMENT=802 DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.dynamicweightallocation_alloptimisedresults_feb2020
CREATE TABLE IF NOT EXISTS `dynamicweightallocation_alloptimisedresults_feb2020` (
  `label` text,
  `sentenceHintProbablity` double DEFAULT NULL,
  `sentenceLocationHintProbability` double DEFAULT NULL,
  `restOfParagraphProbabilityScore` double DEFAULT NULL,
  `messageLocationProbabilityScore` double DEFAULT NULL,
  `messageSubjectHintProbablityScore` double DEFAULT NULL,
  `dateDiffProbability` double DEFAULT NULL,
  `authorRoleProbability` double DEFAULT NULL,
  `messageTypeIsReasonMessageProbabilityScore` double DEFAULT NULL,
  `sentenceLocationInMessageProbabilityScore` double DEFAULT NULL,
  `sameMsgSubAsStateTripleProbabilityScore` double DEFAULT NULL,
  `reasonLabelFoundUsingTripleExtractionProbabilityScore` double DEFAULT NULL,
  `messageContainsSpecialTermProbabilityScore` double DEFAULT NULL,
  `prevParagraphSpecialTermProbabilityScore` double DEFAULT NULL,
  `negationTermPenalty` double DEFAULT NULL,
  `top5` int(11) DEFAULT NULL,
  `top10` int(11) DEFAULT NULL,
  `top15` int(11) DEFAULT NULL,
  `top30` int(11) DEFAULT NULL,
  `top50` int(11) DEFAULT NULL,
  `top100` int(11) DEFAULT NULL,
  `outsidetop100` int(11) DEFAULT NULL,
  `notfound` int(11) DEFAULT NULL,
  `totalProbability` double DEFAULT NULL,
  `run` int(11) NOT NULL AUTO_INCREMENT,
  `evalLevel` text,
  PRIMARY KEY (`run`)
) ENGINE=InnoDB AUTO_INCREMENT=93751 DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.dynamicweightallocation_bckfeb2020
CREATE TABLE IF NOT EXISTS `dynamicweightallocation_bckfeb2020` (
  `label` text,
  `sentenceHintProbablity` double DEFAULT NULL,
  `sentenceLocationHintProbability` double DEFAULT NULL,
  `restOfParagraphProbabilityScore` double DEFAULT NULL,
  `messageLocationProbabilityScore` double DEFAULT NULL,
  `messageSubjectHintProbablityScore` double DEFAULT NULL,
  `dateDiffProbability` double DEFAULT NULL,
  `authorRoleProbability` double DEFAULT NULL,
  `messageTypeIsReasonMessageProbabilityScore` double DEFAULT NULL,
  `sentenceLocationInMessageProbabilityScore` double DEFAULT NULL,
  `sameMsgSubAsStateTripleProbabilityScore` double DEFAULT NULL,
  `reasonLabelFoundUsingTripleExtractionProbabilityScore` double DEFAULT NULL,
  `messageContainsSpecialTermProbabilityScore` double DEFAULT NULL,
  `prevParagraphSpecialTermProbabilityScore` double DEFAULT NULL,
  `negationTermPenalty` double DEFAULT NULL,
  `top5` int(11) DEFAULT NULL,
  `top10` int(11) DEFAULT NULL,
  `top15` int(11) DEFAULT NULL,
  `top30` int(11) DEFAULT NULL,
  `top50` int(11) DEFAULT NULL,
  `top100` int(11) DEFAULT NULL,
  `outsidetop100` int(11) DEFAULT NULL,
  `notfound` int(11) DEFAULT NULL,
  `totalProbability` double DEFAULT NULL,
  `run` int(11) NOT NULL AUTO_INCREMENT,
  `evalLevel` text,
  PRIMARY KEY (`run`)
) ENGINE=InnoDB AUTO_INCREMENT=93751 DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.dynamicweightallocation_finalfeb12h
CREATE TABLE IF NOT EXISTS `dynamicweightallocation_finalfeb12h` (
  `label` text,
  `sentenceHintProbablity` double DEFAULT NULL,
  `sentenceLocationHintProbability` double DEFAULT NULL,
  `restOfParagraphProbabilityScore` double DEFAULT NULL,
  `messageLocationProbabilityScore` double DEFAULT NULL,
  `messageSubjectHintProbablityScore` double DEFAULT NULL,
  `dateDiffProbability` double DEFAULT NULL,
  `authorRoleProbability` double DEFAULT NULL,
  `messageTypeIsReasonMessageProbabilityScore` double DEFAULT NULL,
  `sentenceLocationInMessageProbabilityScore` double DEFAULT NULL,
  `sameMsgSubAsStateTripleProbabilityScore` double DEFAULT NULL,
  `reasonLabelFoundUsingTripleExtractionProbabilityScore` double DEFAULT NULL,
  `messageContainsSpecialTermProbabilityScore` double DEFAULT NULL,
  `prevParagraphSpecialTermProbabilityScore` double DEFAULT NULL,
  `negationTermPenalty` double DEFAULT NULL,
  `top5` int(11) DEFAULT NULL,
  `top10` int(11) DEFAULT NULL,
  `top15` int(11) DEFAULT NULL,
  `top30` int(11) DEFAULT NULL,
  `top50` int(11) DEFAULT NULL,
  `top100` int(11) DEFAULT NULL,
  `outsidetop100` int(11) DEFAULT NULL,
  `notfound` int(11) DEFAULT NULL,
  `totalProbability` double DEFAULT NULL,
  `run` int(11) NOT NULL AUTO_INCREMENT,
  `evalLevel` text,
  PRIMARY KEY (`run`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.dynamicweightallocation_jan2020_removefeatures
CREATE TABLE IF NOT EXISTS `dynamicweightallocation_jan2020_removefeatures` (
  `label` text,
  `sentenceHintProbablity` double DEFAULT NULL,
  `sentenceLocationHintProbability` double DEFAULT NULL,
  `restOfParagraphProbabilityScore` double DEFAULT NULL,
  `messageLocationProbabilityScore` double DEFAULT NULL,
  `messageSubjectHintProbablityScore` double DEFAULT NULL,
  `dateDiffProbability` double DEFAULT NULL,
  `authorRoleProbability` double DEFAULT NULL,
  `messageTypeIsReasonMessageProbabilityScore` double DEFAULT NULL,
  `sentenceLocationInMessageProbabilityScore` double DEFAULT NULL,
  `sameMsgSubAsStateTripleProbabilityScore` double DEFAULT NULL,
  `reasonLabelFoundUsingTripleExtractionProbabilityScore` double DEFAULT NULL,
  `messageContainsSpecialTermProbabilityScore` double DEFAULT NULL,
  `prevParagraphSpecialTermProbabilityScore` double DEFAULT NULL,
  `negationTermPenalty` double DEFAULT NULL,
  `top5` int(11) DEFAULT NULL,
  `top10` int(11) DEFAULT NULL,
  `top15` int(11) DEFAULT NULL,
  `top30` int(11) DEFAULT NULL,
  `top50` int(11) DEFAULT NULL,
  `top100` int(11) DEFAULT NULL,
  `outsidetop100` int(11) DEFAULT NULL,
  `notfound` int(11) DEFAULT NULL,
  `totalProbability` double DEFAULT NULL,
  `run` int(11) NOT NULL AUTO_INCREMENT,
  `evalLevel` text,
  PRIMARY KEY (`run`)
) ENGINE=InnoDB AUTO_INCREMENT=85 DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT COMMENT='jan 2020 ...finalised values for feature removal';

-- Data exporting was unselected.

-- Dumping structure for table bips_new.dynamicweightallocation_jan28allsentencebased
CREATE TABLE IF NOT EXISTS `dynamicweightallocation_jan28allsentencebased` (
  `label` text,
  `sentenceHintProbablity` double DEFAULT NULL,
  `sentenceLocationHintProbability` double DEFAULT NULL,
  `restOfParagraphProbabilityScore` double DEFAULT NULL,
  `messageLocationProbabilityScore` double DEFAULT NULL,
  `messageSubjectHintProbablityScore` double DEFAULT NULL,
  `dateDiffProbability` double DEFAULT NULL,
  `authorRoleProbability` double DEFAULT NULL,
  `messageTypeIsReasonMessageProbabilityScore` double DEFAULT NULL,
  `sentenceLocationInMessageProbabilityScore` double DEFAULT NULL,
  `sameMsgSubAsStateTripleProbabilityScore` double DEFAULT NULL,
  `reasonLabelFoundUsingTripleExtractionProbabilityScore` double DEFAULT NULL,
  `messageContainsSpecialTermProbabilityScore` double DEFAULT NULL,
  `prevParagraphSpecialTermProbabilityScore` double DEFAULT NULL,
  `negationTermPenalty` double DEFAULT NULL,
  `top5` int(11) DEFAULT NULL,
  `top10` int(11) DEFAULT NULL,
  `top15` int(11) DEFAULT NULL,
  `top30` int(11) DEFAULT NULL,
  `top50` int(11) DEFAULT NULL,
  `top100` int(11) DEFAULT NULL,
  `outsidetop100` int(11) DEFAULT NULL,
  `notfound` int(11) DEFAULT NULL,
  `totalProbability` double DEFAULT NULL,
  `run` int(11) NOT NULL AUTO_INCREMENT,
  `evalLevel` text,
  PRIMARY KEY (`run`)
) ENGINE=InnoDB AUTO_INCREMENT=53107 DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

-- Data exporting was unselected.

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

-- Dumping structure for table bips_new.evalresults
CREATE TABLE IF NOT EXISTS `evalresults` (
  `bip` int(11) NOT NULL,
  `messageid` int(11) DEFAULT NULL,
  `date` date DEFAULT NULL,
  `currentsentence` text,
  `label` text,
  `subject` text,
  `relation` text,
  `object` text,
  `clausie` text
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.evalresultsjuly2019
CREATE TABLE IF NOT EXISTS `evalresultsjuly2019` (
  `bip` int(11) NOT NULL,
  `messageid` int(11) DEFAULT NULL,
  `date` date DEFAULT NULL,
  `currentsentence` text,
  `label` text,
  `subject` text,
  `relation` text,
  `object` text,
  `clausie` text
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.evalresultspost
CREATE TABLE IF NOT EXISTS `evalresultspost` (
  `bip` int(11) NOT NULL,
  `messageid` int(11) DEFAULT NULL,
  `date` date DEFAULT NULL,
  `currentsentence` text,
  `label` text,
  `subject` text,
  `relation` text,
  `object` text,
  `clausie` text
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.evalresultspostjuly2019
CREATE TABLE IF NOT EXISTS `evalresultspostjuly2019` (
  `bip` int(11) NOT NULL,
  `messageid` int(11) DEFAULT NULL,
  `date` date DEFAULT NULL,
  `currentsentence` text,
  `label` text,
  `subject` text,
  `relation` text,
  `object` text,
  `clausie` text
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.exams
CREATE TABLE IF NOT EXISTS `exams` (
  `pkey` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(15) DEFAULT NULL,
  `exam` int(11) DEFAULT NULL,
  `score` int(11) DEFAULT NULL,
  PRIMARY KEY (`pkey`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.extractedrelations_clausie
CREATE TABLE IF NOT EXISTS `extractedrelations_clausie` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `bip` int(11) DEFAULT NULL,
  `messageID` int(11) DEFAULT NULL,
  `msgSubject` text,
  `msgAuthorRole` text,
  `paragraphCounter` int(11) DEFAULT NULL,
  `isCorefParagraph` tinyint(4) DEFAULT NULL,
  `sentenceCounter` int(11) DEFAULT NULL,
  `isFirstParagraph` tinyint(4) DEFAULT NULL,
  `isLastParagraph` tinyint(4) DEFAULT NULL,
  `LineNumber` int(11) DEFAULT NULL,
  `arg1` text,
  `relation` text,
  `arg2` text,
  `sentence` text,
  `isEnglish` tinyint(4) DEFAULT NULL,
  `previousSentence` text,
  `nextSentence` text,
  `entireParagraph` text,
  `prevParagraph` text,
  `nextParagraph` text,
  `arg1_processed` text,
  `relation_processed` text,
  `arg2_processed` text,
  `allVerbsInMsgSub` text,
  `allVerbsInSub` text,
  `allVerbsInRel` text,
  `allVerbsInObj` text,
  `allNounsInMsgSub` text,
  `allNounsInSub` text,
  `allNounsInRel` text,
  `allNounsInObj` text,
  PRIMARY KEY (`id`),
  KEY `messageID` (`messageID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.extractedrelations_clausied30bips
CREATE TABLE IF NOT EXISTS `extractedrelations_clausied30bips` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `bip` int(11) DEFAULT NULL,
  `messageID` int(11) DEFAULT NULL,
  `msgSubject` text,
  `msgAuthorRole` text,
  `paragraphCounter` int(11) DEFAULT NULL,
  `isCorefParagraph` tinyint(4) DEFAULT NULL,
  `sentenceCounter` int(11) DEFAULT NULL,
  `isFirstParagraph` tinyint(4) DEFAULT NULL,
  `isLastParagraph` tinyint(4) DEFAULT NULL,
  `LineNumber` int(11) DEFAULT NULL,
  `arg1` text,
  `relation` text,
  `arg2` text,
  `sentence` text,
  `isEnglish` tinyint(4) DEFAULT NULL,
  `previousSentence` text,
  `nextSentence` text,
  `entireParagraph` text,
  `prevParagraph` text,
  `nextParagraph` text,
  `arg1_processed` text,
  `relation_processed` text,
  `arg2_processed` text,
  `allVerbsInMsgSub` text,
  `allVerbsInSub` text,
  `allVerbsInRel` text,
  `allVerbsInObj` text,
  `allNounsInMsgSub` text,
  `allNounsInSub` text,
  `allNounsInRel` text,
  `allNounsInObj` text,
  PRIMARY KEY (`id`),
  KEY `messageID` (`messageID`)
) ENGINE=InnoDB AUTO_INCREMENT=733182 DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.extractedrelations_clausie_mainfirst
CREATE TABLE IF NOT EXISTS `extractedrelations_clausie_mainfirst` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `bip` int(11) DEFAULT NULL,
  `messageID` int(11) DEFAULT NULL,
  `msgSubject` text,
  `msgAuthorRole` text,
  `paragraphCounter` int(11) DEFAULT NULL,
  `isCorefParagraph` tinyint(4) DEFAULT NULL,
  `sentenceCounter` int(11) DEFAULT NULL,
  `isFirstParagraph` tinyint(4) DEFAULT NULL,
  `isLastParagraph` tinyint(4) DEFAULT NULL,
  `LineNumber` int(11) DEFAULT NULL,
  `arg1` text,
  `relation` text,
  `arg2` text,
  `sentence` text,
  `isEnglish` tinyint(4) DEFAULT NULL,
  `previousSentence` text,
  `nextSentence` text,
  `entireParagraph` text,
  `prevParagraph` text,
  `nextParagraph` text,
  `arg1_processed` text,
  `relation_processed` text,
  `arg2_processed` text,
  `allVerbsInMsgSub` text,
  `allVerbsInSub` text,
  `allVerbsInRel` text,
  `allVerbsInObj` text,
  `allNounsInMsgSub` text,
  `allNounsInSub` text,
  `allNounsInRel` text,
  `allNounsInObj` text,
  PRIMARY KEY (`id`),
  KEY `messageID` (`messageID`)
) ENGINE=InnoDB AUTO_INCREMENT=2533986 DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.extractedrelations_clausie_reasontriples
CREATE TABLE IF NOT EXISTS `extractedrelations_clausie_reasontriples` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `arg1` text,
  `relation` text,
  `arg2` text,
  `sentence` text,
  `previousSentence` text,
  `nextSentence` text,
  `isCorefParagraph` text,
  `bip` int(11) DEFAULT NULL,
  `entireParagraph` longtext,
  `prevParagraph` longtext,
  `nextParagraph` longtext,
  `messageID` int(11) DEFAULT NULL,
  `arg1_processed` text,
  `relation_processed` text,
  `arg2_processed` text,
  PRIMARY KEY (`id`),
  KEY `messageID` (`messageID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.foo
CREATE TABLE IF NOT EXISTS `foo` (
  `person` text,
  `groupname` text,
  `age` int(11) DEFAULT NULL
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

-- Dumping structure for table bips_new.initialsentences
CREATE TABLE IF NOT EXISTS `initialsentences` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `bip` int(11) DEFAULT NULL,
  `messageID` int(11) DEFAULT NULL,
  `label` text,
  `subject` text,
  `dateTimeStamp` timestamp NULL DEFAULT NULL,
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
  PRIMARY KEY (`id`),
  KEY `keywords_bip` (`bip`),
  KEY `label` (`label`(15))
) ENGINE=InnoDB AUTO_INCREMENT=18701 DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.jan2020messgepostbyrole
CREATE TABLE IF NOT EXISTS `jan2020messgepostbyrole` (
  `bip` int(11) DEFAULT NULL,
  `nummsgs` bigint(21) NOT NULL DEFAULT '0'
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

-- Dumping structure for table bips_new.keywordmatching29bips
CREATE TABLE IF NOT EXISTS `keywordmatching29bips` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `bip` int(11) DEFAULT NULL,
  `messageID` int(11) DEFAULT NULL,
  `label` text,
  `subject` text,
  `dateTimeStamp` timestamp NULL DEFAULT NULL,
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
  PRIMARY KEY (`id`),
  KEY `keywords_bip` (`bip`),
  KEY `label` (`label`(15))
) ENGINE=InnoDB AUTO_INCREMENT=1171 DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.keywordmatchingremainingbips
CREATE TABLE IF NOT EXISTS `keywordmatchingremainingbips` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `bip` int(11) DEFAULT NULL,
  `messageID` int(11) DEFAULT NULL,
  `label` text,
  `subject` text,
  `dateTimeStamp` timestamp NULL DEFAULT NULL,
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
  PRIMARY KEY (`id`),
  KEY `keywords_bip` (`bip`),
  KEY `label` (`label`(15))
) ENGINE=InnoDB AUTO_INCREMENT=128819 DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

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
  `sentence` text,
  `toCheck` tinyint(4) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.labelslesserstatesreasonlabels
CREATE TABLE IF NOT EXISTS `labelslesserstatesreasonlabels` (
  `Label` text,
  `LesserState` text,
  `Entity` text,
  `Reason` text
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.links
CREATE TABLE IF NOT EXISTS `links` (
  `link` text,
  `checked` int(11) DEFAULT NULL,
  `containsPython` tinyint(4) DEFAULT NULL,
  `checkedForPython` tinyint(4) DEFAULT NULL,
  KEY `link` (`link`(50))
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.manualreasonextraction
CREATE TABLE IF NOT EXISTS `manualreasonextraction` (
  `proposal` int(11) DEFAULT NULL,
  `messageID` int(11) DEFAULT NULL,
  `dateTimeStamp` timestamp NULL DEFAULT NULL,
  `author` mediumtext,
  `sentence` mediumtext,
  `termsMatched` mediumtext,
  `level` mediumtext,
  `reason` tinyint(4) DEFAULT NULL,
  KEY `messageID` (`messageID`),
  KEY `proposal` (`proposal`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

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

-- Dumping structure for table bips_new.msg
CREATE TABLE IF NOT EXISTS `msg` (
  `rank` double DEFAULT NULL,
  `RM` tinyint(4) DEFAULT NULL,
  `MT` tinytext,
  `proposal` int(11) DEFAULT NULL,
  `messageid` int(11) DEFAULT NULL,
  `dateValue` date DEFAULT NULL,
  `termsMatched` mediumtext,
  `messagesubject` mediumtext,
  `datediff` int(11) DEFAULT NULL,
  `message` longtext,
  `TotalProbability` double DEFAULT NULL,
  `label` mediumtext,
  `authorRole` mediumtext,
  `location` text,
  `tp` double DEFAULT NULL,
  `messageTypeIsReasonMessageProbabilityScore` double DEFAULT NULL,
  `messageLocationProbabilityScore` double DEFAULT NULL,
  `messageSubjectHintProbablityScore` double DEFAULT NULL,
  `dateDiffProbability` double DEFAULT NULL,
  `authorRoleProbability` double DEFAULT NULL,
  `sameMsgSubAsStateTripleProbabilityScore` double DEFAULT NULL,
  `messageContainsSpecialTermProbabilityScore` double DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.onepermessage
CREATE TABLE IF NOT EXISTS `onepermessage` (
  `id` int(11) DEFAULT NULL,
  `state` tinytext,
  `bip` int(11) DEFAULT NULL,
  `datetimestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `author` text,
  `messageid` int(11) DEFAULT NULL,
  `sentence` longtext NOT NULL,
  `datecommitted` date DEFAULT NULL,
  `sentiment` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.bipcountsbysubphases
CREATE TABLE IF NOT EXISTS `bipcountsbysubphases` (
  `bip` int(11) DEFAULT NULL,
  `biptype` tinytext,
  `msgcount` int(11) DEFAULT NULL,
  `pairwise` tinytext,
  `folder` tinytext,
  `subphase` tinytext
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.bipcountsbysubphases_2021
CREATE TABLE IF NOT EXISTS `bipcountsbysubphases_2021` (
  `bip` int(11) DEFAULT NULL,
  `biptype` tinytext,
  `msgcount` int(11) DEFAULT NULL,
  `pairwise` tinytext,
  `folder` tinytext,
  `subphase` tinytext
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.bipcountsrq2bysubphasesorig
CREATE TABLE IF NOT EXISTS `bipcountsrq2bysubphasesorig` (
  `bip` int(11) DEFAULT NULL,
  `biptype` tinytext,
  `msgcount` int(11) DEFAULT NULL,
  `pairwise` tinytext,
  `folder` tinytext
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.bipcountsrq2bysubphases_detailed
CREATE TABLE IF NOT EXISTS `bipcountsrq2bysubphases_detailed` (
  `bip` int(11) DEFAULT NULL,
  `biptype` tinytext,
  `msgcount` int(11) DEFAULT NULL,
  `pairwise` tinytext,
  `folder` tinytext
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.bipcountsrq2bysubphases_detailed_2021
CREATE TABLE IF NOT EXISTS `bipcountsrq2bysubphases_detailed_2021` (
  `bip` int(11) DEFAULT NULL,
  `biptype` tinytext,
  `msgcount` int(11) DEFAULT NULL,
  `pairwise` tinytext,
  `folder` tinytext,
  `author` tinytext
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.bipcountsrq2bysubphases_overall
CREATE TABLE IF NOT EXISTS `bipcountsrq2bysubphases_overall` (
  `bip` int(11) DEFAULT NULL,
  `biptype` tinytext,
  `msgcount` int(11) DEFAULT NULL,
  `pairwise` tinytext,
  `folder` tinytext
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.bipcountsrq2bysubphases_overall_2021
CREATE TABLE IF NOT EXISTS `bipcountsrq2bysubphases_overall_2021` (
  `bip` int(11) DEFAULT NULL,
  `biptype` tinytext,
  `msgcount` int(11) DEFAULT NULL,
  `pairwise` tinytext,
  `folder` tinytext,
  `author` tinytext
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.bipdetails
CREATE TABLE IF NOT EXISTS `bipdetails` (
  `bip` int(11) DEFAULT NULL,
  `title` text CHARACTER SET latin1,
  `author` text CHARACTER SET latin1,
  `authorCorrected` text CHARACTER SET latin1,
  `authorEmail` text CHARACTER SET latin1,
  `type` text CHARACTER SET latin1,
  `bdfl_delegate` text CHARACTER SET latin1,
  `bdfl_delegateCorrected` text CHARACTER SET latin1,
  `created` date DEFAULT NULL,
  `authorrole` tinytext,
  `bdfldelegaterole` tinytext,
  `createdYear` int(11) DEFAULT NULL,
  `bipurl` tinytext CHARACTER SET latin1,
  `bipSummary` longtext CHARACTER SET latin1,
  `python_version` text CHARACTER SET latin1,
  `messageid` int(11) DEFAULT NULL,
  KEY `bip_index` (`bip`),
  KEY `title` (`title`(50))
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.bipdetails2020
CREATE TABLE IF NOT EXISTS `bipdetails2020` (
  `bip` int(11) DEFAULT NULL,
  `title` text CHARACTER SET latin1,
  `author` text CHARACTER SET latin1,
  `authorCorrected` text CHARACTER SET latin1,
  `authorEmail` text CHARACTER SET latin1,
  `type` text CHARACTER SET latin1,
  `bdfl_delegate` text CHARACTER SET latin1,
  `bdfl_delegateCorrected` text CHARACTER SET latin1,
  `created` date DEFAULT NULL,
  `authorrole` tinytext,
  `bdfldelegaterole` tinytext,
  `createdYear` int(11) DEFAULT NULL,
  `bipurl` tinytext CHARACTER SET latin1,
  `bipSummary` longtext CHARACTER SET latin1,
  `python_version` text CHARACTER SET latin1,
  `messageid` int(11) DEFAULT NULL,
  KEY `bip_index` (`bip`),
  KEY `title` (`title`(50))
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.bipdetailsold
CREATE TABLE IF NOT EXISTS `bipdetailsold` (
  `bip` int(11) DEFAULT NULL,
  `title` text,
  `author` text,
  `authorCorrected` text,
  `authorEmail` text,
  `authorRole` text,
  `bdfldelegaterole` text,
  `type` text,
  `bdfl_delegate` text,
  `bdfl_delegateCorrected` text,
  `created` date DEFAULT NULL,
  `createdYear` int(11) DEFAULT NULL,
  `bipurl` tinytext,
  `bipSummary` longtext,
  `python_version` text,
  `messageid` int(11) DEFAULT NULL,
  KEY `bip_index` (`bip`),
  KEY `title` (`title`(50))
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.bipdetails_authorcount
CREATE TABLE IF NOT EXISTS `bipdetails_authorcount` (
  `author` tinytext
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.bipdetails_copy
CREATE TABLE IF NOT EXISTS `bipdetails_copy` (
  `bip` int(11) DEFAULT NULL,
  `title` text CHARACTER SET latin1,
  `author` text CHARACTER SET latin1,
  `authorCorrected` text CHARACTER SET latin1,
  `authorEmail` text CHARACTER SET latin1,
  `type` text CHARACTER SET latin1,
  `bdfl_delegate` text CHARACTER SET latin1,
  `bdfl_delegateCorrected` text CHARACTER SET latin1,
  `created` date DEFAULT NULL,
  `createdYear` int(11) DEFAULT NULL,
  `bipurl` tinytext CHARACTER SET latin1,
  `bipSummary` longtext CHARACTER SET latin1,
  `python_version` text CHARACTER SET latin1,
  `messageid` int(11) DEFAULT NULL,
  KEY `bip_index` (`bip`),
  KEY `title` (`title`(50))
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

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

-- Dumping structure for table bips_new.bipdmcontributionsbyrole
CREATE TABLE IF NOT EXISTS `bipdmcontributionsbyrole` (
  `bip` int(11) DEFAULT NULL,
  `rolecheckingfor` text,
  `biptype` text,
  `author` text,
  `delegate` text
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.bipeditors
CREATE TABLE IF NOT EXISTS `bipeditors` (
  `bipeditor` tinytext,
  `dateadded` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.bipinvolvement
CREATE TABLE IF NOT EXISTS `bipinvolvement` (
  `clusterBySenderFullName` tinytext,
  `bipcount` bigint(21) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

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
  `messageID_allmessages` int(11) DEFAULT NULL,
  `date2` date DEFAULT NULL,
  `bipType` text,
  `dateTimeStamp` timestamp NULL DEFAULT NULL,
  `email` longtext,
  `state` longtext,
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
) ENGINE=MyISAM AUTO_INCREMENT=91765 DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC COMMENT='was bipstates_2019dec\r\nhas all data till dec 2019';

-- Data exporting was unselected.

-- Dumping structure for table bips_new.bipstates_danieldata_datetimestamp_alltilldec2019
CREATE TABLE IF NOT EXISTS `bipstates_danieldata_datetimestamp_alltilldec2019` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `bip` int(11) DEFAULT NULL,
  `bipTitle` tinytext,
  `entireMessage` text,
  `messageID` int(11) DEFAULT NULL,
  `messageID_allmessages` int(11) DEFAULT NULL,
  `date2` date DEFAULT NULL,
  `bipType` text,
  `dateTimeStamp` timestamp NULL DEFAULT NULL,
  `email` longtext,
  `state` longtext,
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
) ENGINE=MyISAM AUTO_INCREMENT=1238 DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC COMMENT='was bipstates_2019dec\r\nhas all data till dec 2019';

-- Data exporting was unselected.

-- Dumping structure for table bips_new.bipstates_danieldata_datetimestamp_b4duplicatedeletion
CREATE TABLE IF NOT EXISTS `bipstates_danieldata_datetimestamp_b4duplicatedeletion` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `bip` int(11) DEFAULT NULL,
  `bipTitle` tinytext,
  `entireMessage` text,
  `messageID` int(11) DEFAULT NULL,
  `messageID_allmessages` int(11) DEFAULT NULL,
  `date2` date DEFAULT NULL,
  `bipType` text,
  `dateTimeStamp` timestamp NULL DEFAULT NULL,
  `email` longtext,
  `state` longtext,
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
) ENGINE=MyISAM AUTO_INCREMENT=91499 DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC COMMENT='was bipstates_2019dec\r\nhas all data till dec 2019';

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
  `authorrole` mediumtext,
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

-- Dumping structure for table bips_new.bipstates_danieldata_datetimestamp_firstoneused
CREATE TABLE IF NOT EXISTS `bipstates_danieldata_datetimestamp_firstoneused` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `bip` int(11) DEFAULT NULL,
  `bipTitle` tinytext,
  `entireMessage` text,
  `messageID` int(11) DEFAULT NULL,
  `messageID_allmessages` int(11) DEFAULT NULL,
  `date2` date DEFAULT NULL,
  `bipType` text,
  `dateTimeStamp` timestamp NULL DEFAULT NULL,
  `email` longtext,
  `state` longtext,
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
) ENGINE=MyISAM AUTO_INCREMENT=91767 DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC COMMENT='the first dataset';

-- Data exporting was unselected.

-- Dumping structure for table bips_new.bipstates_danieldata_datetimestamp_new2019
CREATE TABLE IF NOT EXISTS `bipstates_danieldata_datetimestamp_new2019` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `bip` int(11) DEFAULT NULL,
  `bipTitle` tinytext,
  `entireMessage` text,
  `messageID` int(11) DEFAULT NULL,
  `messageID_allmessages` int(11) DEFAULT NULL,
  `date2` date DEFAULT NULL,
  `bipType` text,
  `dateTimeStamp` timestamp NULL DEFAULT NULL,
  `email` longtext,
  `state` longtext,
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
  `hasjepTitleOnly` tinyint(4) DEFAULT NULL,
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
  KEY `jep` (`bip`),
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
) ENGINE=MyISAM AUTO_INCREMENT=3145 DEFAULT CHARSET=latin1 ROW_FORMAT=DYNAMIC;

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

-- Dumping structure for table bips_new.bipstates_old
CREATE TABLE IF NOT EXISTS `bipstates_old` (
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

-- Dumping structure for table bips_new.bipthreadsbyperson2020
CREATE TABLE IF NOT EXISTS `bipthreadsbyperson2020` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `threadlevel` varchar(1) CHARACTER SET utf8mb4 NOT NULL DEFAULT '',
  `bipnum2020` int(11) DEFAULT NULL,
  `biptype2020` text,
  `messageid` int(11) DEFAULT NULL,
  `author` mediumtext,
  `clusterbysenderfullname` tinytext,
  `emailmessageid` tinytext,
  `inreplyto` text,
  `subject` mediumtext,
  `authorsrole2020` text,
  `threadid` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=162505 DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.proposaldetails
CREATE TABLE IF NOT EXISTS `proposaldetails` (
  `bip` int(11) DEFAULT NULL,
  `title` text,
  `author` text,
  `authorCorrected` text,
  `authorEmail` text,
  `type` text,
  `bdfl_delegate` text,
  `bdfl_delegateCorrected` text,
  `created` date DEFAULT NULL,
  `createdYear` int(11) DEFAULT NULL,
  `bipurl` tinytext,
  `bipSummary` longtext,
  `python_version` text,
  `messageid` int(11) DEFAULT NULL,
  KEY `bip_index` (`bip`),
  KEY `title` (`title`(50))
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.proposaldetails_titlesforeachbip_debug
CREATE TABLE IF NOT EXISTS `proposaldetails_titlesforeachbip_debug` (
  `proposal` int(11) DEFAULT NULL,
  `matchedUsing` text,
  `firstTitle` text,
  `finalTitle` text,
  `subject` text,
  `matchedLocation` text,
  `messageid` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.pythonmembers_coredevelopers
CREATE TABLE IF NOT EXISTS `pythonmembers_coredevelopers` (
  `name` tinytext,
  `name_corrected` tinytext
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.pythonmembers_bipeditors
CREATE TABLE IF NOT EXISTS `pythonmembers_bipeditors` (
  `name` tinytext,
  `name_corrected` tinytext
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.reasonlabels
CREATE TABLE IF NOT EXISTS `reasonlabels` (
  `lineNumber` int(11) DEFAULT NULL,
  `comment` tinytext,
  `idea` tinytext,
  `subject` tinytext,
  `verb` tinytext,
  `object` tinytext,
  `sentence` text,
  `toCheck` tinyint(4) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.reasonstallyoverall_old
CREATE TABLE IF NOT EXISTS `reasonstallyoverall_old` (
  `dmconcept` tinytext,
  `label` text,
  `cnt` bigint(21) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.reasonstally_old
CREATE TABLE IF NOT EXISTS `reasonstally_old` (
  `dmconcept` tinytext,
  `label` text,
  `state` tinytext,
  `biptype` tinytext,
  `cntbips` bigint(21) NOT NULL DEFAULT '0',
  `bips` text CHARACTER SET utf8mb4,
  `lblcount` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.reasonstypetallyoverall_old
CREATE TABLE IF NOT EXISTS `reasonstypetallyoverall_old` (
  `biptype` tinytext,
  `dmconcept` tinytext,
  `label` text,
  `cnt` bigint(21) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.reasonstypetally_old
CREATE TABLE IF NOT EXISTS `reasonstypetally_old` (
  `biptype` tinytext,
  `dmconcept` tinytext,
  `label` text,
  `state` tinytext,
  `cntbips` bigint(21) NOT NULL DEFAULT '0',
  `bips` text CHARACTER SET utf8mb4,
  `lblcount` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

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

-- Dumping structure for table bips_new.remaingconcepts
CREATE TABLE IF NOT EXISTS `remaingconcepts` (
  `allverbsinmsgsub` text,
  `allnounsinmsgsub` text
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

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
  `authorold` text,
  `authorRole` text,
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
  `labelFoundInMsgSubject` tinyint(4) DEFAULT NULL,
  `isFirstParagraph` tinyint(4) DEFAULT NULL,
  `isLastParagraph` tinyint(4) DEFAULT NULL,
  `ns` text,
  `pp` longtext,
  `ep` longtext,
  `np` longtext,
  `identifierCount` int(11) DEFAULT NULL,
  `lineNumber` int(11) DEFAULT NULL,
  `paragraphCounter` int(11) DEFAULT NULL,
  `sentenceCounterInMessage` int(11) DEFAULT NULL,
  `sentenceCounterInParagraph` int(11) DEFAULT NULL,
  `author` text,
  KEY `clausieIDX` (`clausie`(20)),
  KEY `bip` (`bip`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT COMMENT='main table one used for substate analysis';

-- Data exporting was unselected.

-- Dumping structure for table bips_new.resultsnew
CREATE TABLE IF NOT EXISTS `resultsnew` (
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
  `authorRole` text,
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
  `labelFoundInMsgSubject` tinyint(4) DEFAULT NULL,
  `isFirstParagraph` tinyint(4) DEFAULT NULL,
  `isLastParagraph` tinyint(4) DEFAULT NULL,
  `ns` text,
  `pp` longtext,
  `ep` longtext,
  `np` longtext,
  `identifierCount` int(11) DEFAULT NULL,
  `lineNumber` int(11) DEFAULT NULL,
  `paragraphCounter` int(11) DEFAULT NULL,
  `sentenceCounterInMessage` int(11) DEFAULT NULL,
  `sentenceCounterInParagraph` int(11) DEFAULT NULL,
  KEY `clausieIDX` (`clausie`(20)),
  KEY `bip` (`bip`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.resultsold
CREATE TABLE IF NOT EXISTS `resultsold` (
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
  `authorRole` text,
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
  `labelFoundInMsgSubject` tinyint(4) DEFAULT NULL,
  `isFirstParagraph` tinyint(4) DEFAULT NULL,
  `isLastParagraph` tinyint(4) DEFAULT NULL,
  `ns` text,
  `pp` longtext,
  `ep` longtext,
  `np` longtext,
  `identifierCount` int(11) DEFAULT NULL,
  `lineNumber` int(11) DEFAULT NULL,
  `paragraphCounter` int(11) DEFAULT NULL,
  `sentenceCounterInMessage` int(11) DEFAULT NULL,
  `sentenceCounterInParagraph` int(11) DEFAULT NULL,
  KEY `clausieIDX` (`clausie`(20)),
  KEY `bip` (`bip`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.results_b4feb2020run
CREATE TABLE IF NOT EXISTS `results_b4feb2020run` (
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
  `authorRole` text,
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
  `labelFoundInMsgSubject` tinyint(4) DEFAULT NULL,
  `isFirstParagraph` tinyint(4) DEFAULT NULL,
  `isLastParagraph` tinyint(4) DEFAULT NULL,
  `ns` text,
  `pp` longtext,
  `ep` longtext,
  `np` longtext,
  `identifierCount` int(11) DEFAULT NULL,
  `lineNumber` int(11) DEFAULT NULL,
  `paragraphCounter` int(11) DEFAULT NULL,
  `sentenceCounterInMessage` int(11) DEFAULT NULL,
  `sentenceCounterInParagraph` int(11) DEFAULT NULL,
  KEY `clausieIDX` (`clausie`(20)),
  KEY `bip` (`bip`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT COMMENT='main table one used for substate analysis';

-- Data exporting was unselected.

-- Dumping structure for table bips_new.results_first
CREATE TABLE IF NOT EXISTS `results_first` (
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
  `authorRole` text,
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
  `labelFoundInMsgSubject` tinyint(4) DEFAULT NULL,
  `isFirstParagraph` tinyint(4) DEFAULT NULL,
  `isLastParagraph` tinyint(4) DEFAULT NULL,
  `ns` text,
  `pp` longtext,
  `ep` longtext,
  `np` longtext,
  `identifierCount` int(11) DEFAULT NULL,
  `lineNumber` int(11) DEFAULT NULL,
  `paragraphCounter` int(11) DEFAULT NULL,
  `sentenceCounterInMessage` int(11) DEFAULT NULL,
  `sentenceCounterInParagraph` int(11) DEFAULT NULL,
  KEY `clausieIDX` (`clausie`(20)),
  KEY `bip` (`bip`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT COMMENT='main first one used';

-- Data exporting was unselected.

-- Dumping structure for table bips_new.results_mar2020
CREATE TABLE IF NOT EXISTS `results_mar2020` (
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
  `authorRole` text,
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
  `labelFoundInMsgSubject` tinyint(4) DEFAULT NULL,
  `isFirstParagraph` tinyint(4) DEFAULT NULL,
  `isLastParagraph` tinyint(4) DEFAULT NULL,
  `ns` text,
  `pp` longtext,
  `ep` longtext,
  `np` longtext,
  `identifierCount` int(11) DEFAULT NULL,
  `lineNumber` int(11) DEFAULT NULL,
  `paragraphCounter` int(11) DEFAULT NULL,
  `sentenceCounterInMessage` int(11) DEFAULT NULL,
  `sentenceCounterInParagraph` int(11) DEFAULT NULL,
  KEY `clausieIDX` (`clausie`(20)),
  KEY `bip` (`bip`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT COMMENT='main table one used for substate analysis';

-- Data exporting was unselected.

-- Dumping structure for table bips_new.results_postprocessed
CREATE TABLE IF NOT EXISTS `results_postprocessed` (
  `bip` int(11) NOT NULL,
  `messageID` int(11) DEFAULT NULL,
  `messageSubject` text,
  `numberOfbipsMentionedInMessage` int(11) DEFAULT NULL,
  `author` text,
  `authorRole` text,
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
  `np` mediumtext,
  `labelFoundInMsgSubject` tinyint(4) DEFAULT NULL,
  `isFirstParagraph` tinyint(4) DEFAULT NULL,
  `isLastParagraph` tinyint(4) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT COMMENT='main one for diagram';

-- Data exporting was unselected.

-- Dumping structure for table bips_new.results_postprocessed_b4june2020
CREATE TABLE IF NOT EXISTS `results_postprocessed_b4june2020` (
  `bip` int(11) NOT NULL,
  `messageID` int(11) DEFAULT NULL,
  `messageSubject` text,
  `numberOfbipsMentionedInMessage` int(11) DEFAULT NULL,
  `author` text,
  `authorRole` text,
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
  `np` mediumtext,
  `labelFoundInMsgSubject` tinyint(4) DEFAULT NULL,
  `isFirstParagraph` tinyint(4) DEFAULT NULL,
  `isLastParagraph` tinyint(4) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT COMMENT='main one for diagram';

-- Data exporting was unselected.

-- Dumping structure for table bips_new.results_postprocessed_bck
CREATE TABLE IF NOT EXISTS `results_postprocessed_bck` (
  `bip` int(11) NOT NULL,
  `messageID` int(11) DEFAULT NULL,
  `messageSubject` text,
  `numberOfbipsMentionedInMessage` int(11) DEFAULT NULL,
  `author` text,
  `authorRole` text,
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
  `np` mediumtext,
  `labelFoundInMsgSubject` tinyint(4) DEFAULT NULL,
  `isFirstParagraph` tinyint(4) DEFAULT NULL,
  `isLastParagraph` tinyint(4) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.results_postprocessed_firstmajor
CREATE TABLE IF NOT EXISTS `results_postprocessed_firstmajor` (
  `bip` int(11) NOT NULL,
  `messageID` int(11) DEFAULT NULL,
  `messageSubject` text,
  `numberOfbipsMentionedInMessage` int(11) DEFAULT NULL,
  `author` text,
  `authorRole` text,
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
  `np` mediumtext,
  `labelFoundInMsgSubject` tinyint(4) DEFAULT NULL,
  `isFirstParagraph` tinyint(4) DEFAULT NULL,
  `isLastParagraph` tinyint(4) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.results_postprocessed_mainoneusedforresults
CREATE TABLE IF NOT EXISTS `results_postprocessed_mainoneusedforresults` (
  `bip` int(11) NOT NULL,
  `messageID` int(11) DEFAULT NULL,
  `messageSubject` text,
  `numberOfbipsMentionedInMessage` int(11) DEFAULT NULL,
  `author` text,
  `authorRole` text,
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
  `np` mediumtext,
  `labelFoundInMsgSubject` tinyint(4) DEFAULT NULL,
  `isFirstParagraph` tinyint(4) DEFAULT NULL,
  `isLastParagraph` tinyint(4) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT COMMENT='main one for diagram';

-- Data exporting was unselected.

-- Dumping structure for table bips_new.results_postprocessed_mar2020_main
CREATE TABLE IF NOT EXISTS `results_postprocessed_mar2020_main` (
  `bip` int(11) NOT NULL,
  `messageID` int(11) DEFAULT NULL,
  `messageSubject` text,
  `numberOfbipsMentionedInMessage` int(11) DEFAULT NULL,
  `author` text,
  `authorRole` text,
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
  `np` mediumtext,
  `labelFoundInMsgSubject` tinyint(4) DEFAULT NULL,
  `isFirstParagraph` tinyint(4) DEFAULT NULL,
  `isLastParagraph` tinyint(4) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT COMMENT='main one for diagram';

-- Data exporting was unselected.

-- Dumping structure for table bips_new.results_postprocessed_premar2020
CREATE TABLE IF NOT EXISTS `results_postprocessed_premar2020` (
  `bip` int(11) NOT NULL,
  `messageID` int(11) DEFAULT NULL,
  `messageSubject` text,
  `numberOfbipsMentionedInMessage` int(11) DEFAULT NULL,
  `author` text,
  `authorRole` text,
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
  `np` mediumtext,
  `labelFoundInMsgSubject` tinyint(4) DEFAULT NULL,
  `isFirstParagraph` tinyint(4) DEFAULT NULL,
  `isLastParagraph` tinyint(4) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.results_postprocessed_thesissubmittedjuly2020
CREATE TABLE IF NOT EXISTS `results_postprocessed_thesissubmittedjuly2020` (
  `bip` int(11) NOT NULL,
  `messageID` int(11) DEFAULT NULL,
  `messageSubject` text,
  `numberOfbipsMentionedInMessage` int(11) DEFAULT NULL,
  `author` text,
  `authorRole` text,
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
  `np` mediumtext,
  `labelFoundInMsgSubject` tinyint(4) DEFAULT NULL,
  `isFirstParagraph` tinyint(4) DEFAULT NULL,
  `isLastParagraph` tinyint(4) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT COMMENT='main one for diagram';

-- Data exporting was unselected.

-- Dumping structure for table bips_new.rolecommunicationsforeachmember_biptypedmsubphase
CREATE TABLE IF NOT EXISTS `rolecommunicationsforeachmember_biptypedmsubphase` (
  `bip` int(11) DEFAULT NULL,
  `authorsrole` text,
  `author` text,
  `inReplyToUserUsingClusteredSenderRole` text,
  `biptype` tinytext,
  `lastdir` tinytext,
  `cnt` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.rolecommunicationsforsna
CREATE TABLE IF NOT EXISTS `rolecommunicationsforsna` (
  `bip` int(11) DEFAULT NULL,
  `authorsrole` text,
  `inReplyToUserUsingClusteredSenderRole` text,
  `cnt` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.rolesmsgcount
CREATE TABLE IF NOT EXISTS `rolesmsgcount` (
  `bip` int(11) DEFAULT NULL,
  `state` tinytext,
  `bipeditor` int(11) DEFAULT NULL,
  `coredev` int(11) DEFAULT NULL,
  `others` int(11) DEFAULT NULL,
  `bipauthor` int(11) DEFAULT NULL,
  `bdfl` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='(bip,state, authorsrole, msgcount) ';

-- Data exporting was unselected.

-- Dumping structure for table bips_new.rolespersonmsgcount
CREATE TABLE IF NOT EXISTS `rolespersonmsgcount` (
  `bip` int(11) DEFAULT NULL,
  `state` tinytext,
  `authorsrole` tinytext,
  `author` tinytext,
  `msgcountfordates` int(11) DEFAULT NULL,
  `msgcountalldates` int(11) DEFAULT NULL,
  `msgcountbeforestate` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.rounds
CREATE TABLE IF NOT EXISTS `rounds` (
  `bip` int(11) DEFAULT NULL,
  `originalbipNumber` int(11) DEFAULT NULL,
  `originalbipNumberNum` int(11) DEFAULT NULL,
  `messageID` int(11) DEFAULT NULL,
  `date2` date DEFAULT NULL,
  `bipType` text,
  `dateTimeStamp` timestamp NULL DEFAULT NULL,
  `dateTimeStampString` text,
  `email` longtext,
  `analyseWords` longtext,
  `corefMsg` longtext,
  `Author` mediumtext,
  `subject` mediumtext,
  `location` text,
  `line` longtext,
  `folder` text,
  `file` text,
  `from` text,
  `emailMessageId` tinytext,
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
  `msgNumInFile` int(11) DEFAULT NULL,
  `processedSubject` mediumtext,
  `authorsrole` mediumtext,
  `ifProposalAtTheEndofMessage` tinyint(4) DEFAULT NULL,
  `processMessage` tinyint(4) DEFAULT NULL,
  `pTitleMatchedinMsgSub` tinyint(4) DEFAULT NULL,
  `pNumMatchedinMsgSub` tinyint(4) DEFAULT NULL,
  `pNumMatchedinMsgBody` tinyint(4) DEFAULT NULL,
  `messageType` tinytext
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.sentimentsentences
CREATE TABLE IF NOT EXISTS `sentimentsentences` (
  `id` int(11) DEFAULT NULL,
  `bip` int(11) DEFAULT NULL,
  `state` tinytext,
  `date2` date DEFAULT NULL,
  `author` text,
  `authorsrole` text,
  `messageid` int(11) DEFAULT NULL,
  `datecommitted` date DEFAULT NULL,
  `sentimentVal` float DEFAULT NULL,
  `sentiment` varchar(50) DEFAULT NULL,
  `sentence` longtext NOT NULL,
  `isEnglishOrCode` tinyint(4) DEFAULT NULL,
  `islastparagraph` tinyint(4) DEFAULT NULL,
  `isfirstparagraph` tinyint(4) DEFAULT NULL,
  `msgSubject` text,
  `tocheck` int(11) DEFAULT NULL,
  `datetimestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.states_reason_reasonsublabels
CREATE TABLE IF NOT EXISTS `states_reason_reasonsublabels` (
  `state` text,
  `stateNotes` text,
  `reason` text,
  `reasonNotes` text,
  `subReason` text,
  `subReasonNotes` text,
  `Entity` text
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.subjectmatch2020
CREATE TABLE IF NOT EXISTS `subjectmatch2020` (
  `abip` int(11) DEFAULT NULL,
  `abipnum2020` int(11) DEFAULT NULL,
  `amessageid` int(11) DEFAULT NULL,
  `bbip` int(11) DEFAULT NULL,
  `bbipnum2020` int(11) DEFAULT NULL,
  `subject` mediumtext,
  `bmessageid` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.subjects2020
CREATE TABLE IF NOT EXISTS `subjects2020` (
  `bipnum2020` int(11) DEFAULT NULL,
  `biptype2020` text,
  `subject` mediumtext,
  `clusterbysenderfullname` tinytext,
  `authorsrole2020` text,
  `cnt` bigint(21) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.tempcountbips
CREATE TABLE IF NOT EXISTS `tempcountbips` (
  `bip` int(11) DEFAULT NULL,
  `bipsNew_MessageIDCount` bigint(21) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.tempcountbips2018
CREATE TABLE IF NOT EXISTS `tempcountbips2018` (
  `bip` int(11) DEFAULT NULL,
  `bipsNew_MessageIDCount` bigint(21) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.temptest
CREATE TABLE IF NOT EXISTS `temptest` (
  `bip` int(11) DEFAULT NULL,
  `bipnum2020` int(11) DEFAULT NULL,
  `author` mediumtext,
  `biptype` text,
  `biptype2020` text,
  `authorsrole` mediumtext,
  `authorsrole2020` text,
  `lastdir` text,
  `cnt` bigint(21) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.test
CREATE TABLE IF NOT EXISTS `test` (
  `clusterbysenderfullname` tinytext,
  `folder` text
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.threadstest2020
CREATE TABLE IF NOT EXISTS `threadstest2020` (
  `emailmessageid` tinytext,
  `inreplyto` text,
  `bipnum2020` int(11) DEFAULT NULL,
  `biptype2020` text,
  `subject` mediumtext,
  `author` mediumtext,
  `clusterbysenderfullname` tinytext,
  `authorsrole2020` text,
  `firstlevelreplies` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.tmpresult
CREATE TABLE IF NOT EXISTS `tmpresult` (
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
  `authorRole` text,
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
  `labelFoundInMsgSubject` tinyint(4) DEFAULT NULL,
  `isFirstParagraph` tinyint(4) DEFAULT NULL,
  `isLastParagraph` tinyint(4) DEFAULT NULL,
  `ns` text,
  `pp` longtext,
  `ep` longtext,
  `np` longtext,
  `identifierCount` int(11) DEFAULT NULL,
  `lineNumber` int(11) DEFAULT NULL,
  `paragraphCounter` int(11) DEFAULT NULL,
  `sentenceCounterInMessage` int(11) DEFAULT NULL,
  `sentenceCounterInParagraph` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.top10pairwise
CREATE TABLE IF NOT EXISTS `top10pairwise` (
  `pairwise` tinytext,
  `author` tinytext,
  `summsg` decimal(32,0) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.topicmodel
CREATE TABLE IF NOT EXISTS `topicmodel` (
  `term` text,
  `topicscommon` int(11) DEFAULT NULL,
  `totalcount` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.topicmodellingignoredterms
CREATE TABLE IF NOT EXISTS `topicmodellingignoredterms` (
  `ignoredTerm` tinytext
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.topicmodellingsentences
CREATE TABLE IF NOT EXISTS `topicmodellingsentences` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `proposal` int(11) DEFAULT NULL,
  `messageid` int(11) DEFAULT NULL,
  `sentence` mediumtext,
  `lemmasentence` mediumtext,
  `ignoredTerms` mediumtext,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1724 DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.topicmodellingsentences_copy
CREATE TABLE IF NOT EXISTS `topicmodellingsentences_copy` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `proposal` int(11) DEFAULT NULL,
  `messageid` int(11) DEFAULT NULL,
  `sentence` mediumtext,
  `lemmasentence` mediumtext,
  `ignoredTerms` mediumtext,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=192441 DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.trackforeachproposal
CREATE TABLE IF NOT EXISTS `trackforeachproposal` (
  `proposal` int(11) DEFAULT NULL,
  `state` text,
  `matchedInProposal` tinyint(4) DEFAULT NULL,
  `preAtTop5` double DEFAULT NULL,
  `preAtTop10` double DEFAULT NULL,
  `preAtTop15` double DEFAULT NULL,
  `preAtTop30` double DEFAULT NULL,
  `preAtTop50` double DEFAULT NULL,
  `preAtTop100` double DEFAULT NULL,
  `preOutsideTop100` double DEFAULT NULL,
  `recAtTop5` double DEFAULT NULL,
  `recAtTop10` double DEFAULT NULL,
  `recAtTop15` double DEFAULT NULL,
  `recAtTop30` double DEFAULT NULL,
  `recAtTop50` double DEFAULT NULL,
  `recAtTop100` double DEFAULT NULL,
  `recOutsideTop100` double DEFAULT NULL,
  `sumPreAtTop5` double DEFAULT NULL,
  `sumPreAtTop10` double DEFAULT NULL,
  `sumPreAtTop15` double DEFAULT NULL,
  `sumPreAtTop30` double DEFAULT NULL,
  `sumPreAtTop50` double DEFAULT NULL,
  `sumPreOutsideTop100` double DEFAULT NULL,
  `preAtK` double DEFAULT NULL,
  `recAtK` double DEFAULT NULL,
  `dcg` double DEFAULT NULL,
  `ndcg` double DEFAULT NULL,
  `sum_dcg` double DEFAULT NULL,
  `sum_ndcg` double DEFAULT NULL,
  `finalAvgPre` double DEFAULT NULL,
  `sumOfAllPre` double DEFAULT NULL,
  `idcg` double DEFAULT NULL,
  `evalLevel` text,
  `rank` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.trackforeachproposal_main
CREATE TABLE IF NOT EXISTS `trackforeachproposal_main` (
  `proposal` int(11) DEFAULT NULL,
  `state` text,
  `matchedInProposal` tinyint(4) DEFAULT NULL,
  `preAtTop5` double DEFAULT NULL,
  `preAtTop10` double DEFAULT NULL,
  `preAtTop15` double DEFAULT NULL,
  `preAtTop30` double DEFAULT NULL,
  `preAtTop50` double DEFAULT NULL,
  `preAtTop100` double DEFAULT NULL,
  `preOutsideTop100` double DEFAULT NULL,
  `recAtTop5` double DEFAULT NULL,
  `recAtTop10` double DEFAULT NULL,
  `recAtTop15` double DEFAULT NULL,
  `recAtTop30` double DEFAULT NULL,
  `recAtTop50` double DEFAULT NULL,
  `recAtTop100` double DEFAULT NULL,
  `recOutsideTop100` double DEFAULT NULL,
  `sumPreAtTop5` double DEFAULT NULL,
  `sumPreAtTop10` double DEFAULT NULL,
  `sumPreAtTop15` double DEFAULT NULL,
  `sumPreAtTop30` double DEFAULT NULL,
  `sumPreAtTop50` double DEFAULT NULL,
  `sumPreOutsideTop100` double DEFAULT NULL,
  `preAtK` double DEFAULT NULL,
  `recAtK` double DEFAULT NULL,
  `dcg` double DEFAULT NULL,
  `ndcg` double DEFAULT NULL,
  `sum_dcg` double DEFAULT NULL,
  `sum_ndcg` double DEFAULT NULL,
  `finalAvgPre` double DEFAULT NULL,
  `sumOfAllPre` double DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.trackforeachrow
CREATE TABLE IF NOT EXISTS `trackforeachrow` (
  `proposal` int(11) DEFAULT NULL,
  `state` text,
  `messageID` int(11) DEFAULT NULL,
  `ind` int(11) DEFAULT NULL,
  `rank` int(11) DEFAULT NULL,
  `rankByTP` int(11) DEFAULT NULL,
  `preAtK` double DEFAULT NULL,
  `recAtK` double DEFAULT NULL,
  `dcg` double DEFAULT NULL,
  `idcg` double DEFAULT NULL,
  `ndcg` double DEFAULT NULL,
  `precision_ForAVGPreMean` double DEFAULT NULL,
  `deno` double DEFAULT NULL,
  `deno2` double DEFAULT NULL,
  `currentRecall_ForAVGPreMean` double DEFAULT NULL,
  `previousSumPreAtTop10` double DEFAULT NULL,
  `previousSumRecAtTop10` double DEFAULT NULL,
  `matchedAtIndex` tinyint(4) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.trackforeachrow_main
CREATE TABLE IF NOT EXISTS `trackforeachrow_main` (
  `proposal` int(11) DEFAULT NULL,
  `state` text,
  `messageID` int(11) DEFAULT NULL,
  `ind` int(11) DEFAULT NULL,
  `rank` int(11) DEFAULT NULL,
  `rankByTP` int(11) DEFAULT NULL,
  `preAtK` double DEFAULT NULL,
  `recAtK` double DEFAULT NULL,
  `dcg` double DEFAULT NULL,
  `idcg` double DEFAULT NULL,
  `ndcg` double DEFAULT NULL,
  `precision_ForAVGPreMean` double DEFAULT NULL,
  `deno` double DEFAULT NULL,
  `deno2` double DEFAULT NULL,
  `currentRecall_ForAVGPreMean` double DEFAULT NULL,
  `previousSumPreAtTop10` double DEFAULT NULL,
  `previousSumRecAtTop10` double DEFAULT NULL,
  `matchedAtIndex` tinyint(4) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.trackmessageprocessing
CREATE TABLE IF NOT EXISTS `trackmessageprocessing` (
  `bip` int(11) DEFAULT NULL,
  `MessageSubjectContainsOnlyAnotherbip` tinyint(4) DEFAULT NULL,
  `messageSubject` text,
  `messageID` int(11) DEFAULT NULL,
  `date` timestamp NULL DEFAULT NULL,
  `messageEmpty` tinyint(4) DEFAULT NULL,
  `proceed` tinyint(4) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.trackmessageprocessing_main
CREATE TABLE IF NOT EXISTS `trackmessageprocessing_main` (
  `bip` int(11) DEFAULT NULL,
  `MessageSubjectContainsOnlyAnotherbip` tinyint(4) DEFAULT NULL,
  `messageSubject` text,
  `messageID` int(11) DEFAULT NULL,
  `date` timestamp NULL DEFAULT NULL,
  `messageEmpty` tinyint(4) DEFAULT NULL,
  `proceed` tinyint(4) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.trackmessagesplit
CREATE TABLE IF NOT EXISTS `trackmessagesplit` (
  `messageid` int(11) DEFAULT NULL,
  `bip` int(11) DEFAULT NULL,
  `splitmarker` text,
  `splitforbip` int(11) DEFAULT NULL,
  `anycandidatefound` tinyint(1) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.trackranking
CREATE TABLE IF NOT EXISTS `trackranking` (
  `proposal` int(11) DEFAULT NULL,
  `label` text,
  `location` text,
  `messageid` text,
  `ranking` int(11) DEFAULT NULL,
  `sentenceormessage` text,
  `totalprobability` double DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='check if ranking of sql schemes is correct - as sentence scheme maybe not functioning properly';

-- Data exporting was unselected.

-- Dumping structure for table bips_new.trackrankingforsentences
CREATE TABLE IF NOT EXISTS `trackrankingforsentences` (
  `proposal` int(11) DEFAULT NULL,
  `trainingsentence` text,
  `matched` int(11) DEFAULT NULL,
  `rankedsentence` text,
  `messageid` text,
  `evalLevel` text,
  `rank` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT COMMENT='sentences matching - some sentences were not being matched';

-- Data exporting was unselected.

-- Dumping structure for table bips_new.trackrankingforsentences_unmatchedsent
CREATE TABLE IF NOT EXISTS `trackrankingforsentences_unmatchedsent` (
  `proposal` int(11) DEFAULT NULL,
  `trainingsentence` text,
  `matched` int(11) DEFAULT NULL,
  `rankedsentence` text,
  `messageid` text,
  `evalLevel` text,
  `rank` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='sentences matching - some sentences were not being matched';

-- Data exporting was unselected.

-- Dumping structure for table bips_new.tracksentencemultiplebips
CREATE TABLE IF NOT EXISTS `tracksentencemultiplebips` (
  `messageid` int(11) DEFAULT NULL,
  `originalbip` int(11) DEFAULT NULL,
  `sentence` text,
  `path` int(11) DEFAULT NULL,
  `anycandidatefound` tinyint(1) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.trainingdata
CREATE TABLE IF NOT EXISTS `trainingdata` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `bip` int(11) DEFAULT NULL,
  `state` tinytext,
  `dmconcept` tinytext,
  `label` text,
  `folder` tinytext,
  `file` tinytext,
  `msgNumberInFile` int(11) DEFAULT NULL,
  `messageID` int(11) DEFAULT NULL,
  `causeMessageID` int(11) DEFAULT NULL,
  `causeParagraphNum` int(11) DEFAULT NULL,
  `causeSentenceNum` int(11) DEFAULT NULL,
  `effectMessageID` int(11) DEFAULT NULL,
  `effectParagraphNum` int(11) DEFAULT NULL,
  `effectSentenceNum` int(11) DEFAULT NULL,
  `causeSentence` text,
  `effectSentence` text,
  `Notes` text,
  `fromsubstateextraction` tinyint(4) DEFAULT NULL,
  `effectSentence_cleaned` text,
  `causeSentence_cleaned` text,
  `communityReviewMessageID` int(11) DEFAULT NULL,
  `communityReview` text,
  `proposalAuthorReviewMessageID` int(11) DEFAULT NULL,
  `proposalAuthorReview` text,
  `bdfldelegatePronouncementMessageID` int(11) DEFAULT NULL,
  `consider` int(11) DEFAULT NULL,
  `bdfldelegatePronouncement` text,
  `extractedMessageid` int(11) DEFAULT NULL,
  `datediff` int(11) DEFAULT NULL,
  `manuallyExtracted` tinyint(4) DEFAULT NULL,
  `allbips` longtext,
  `biptype` tinytext,
  PRIMARY KEY (`id`),
  KEY `bip` (`bip`)
) ENGINE=InnoDB AUTO_INCREMENT=991 DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT COMMENT='trainingdata_feb2020';

-- Data exporting was unselected.

-- Dumping structure for table bips_new.trainingdata_15dec2019
CREATE TABLE IF NOT EXISTS `trainingdata_15dec2019` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `bip` int(11) DEFAULT NULL,
  `state` tinytext,
  `dmconcept` tinytext,
  `label` text,
  `folder` tinytext,
  `file` tinytext,
  `msgNumberInFile` int(11) DEFAULT NULL,
  `messageID` int(11) DEFAULT NULL,
  `causeMessageID` int(11) DEFAULT NULL,
  `causeParagraphNum` int(11) DEFAULT NULL,
  `causeSentenceNum` int(11) DEFAULT NULL,
  `effectMessageID` int(11) DEFAULT NULL,
  `effectParagraphNum` int(11) DEFAULT NULL,
  `effectSentenceNum` int(11) DEFAULT NULL,
  `causeSentence` text,
  `effectSentence` text,
  `Notes` text,
  `fromsubstateextraction` tinyint(4) DEFAULT NULL,
  `effectSentence_cleaned` text,
  `causeSentence_cleaned` text,
  `communityReviewMessageID` int(11) DEFAULT NULL,
  `communityReview` text,
  `proposalAuthorReviewMessageID` int(11) DEFAULT NULL,
  `proposalAuthorReview` text,
  `bdfldelegatePronouncementMessageID` int(11) DEFAULT NULL,
  `consider` int(11) DEFAULT NULL,
  `bdfldelegatePronouncement` text,
  `extractedMessageid` int(11) DEFAULT NULL,
  `datediff` int(11) DEFAULT NULL,
  `manuallyExtracted` tinyint(4) DEFAULT NULL,
  `allbips` longtext,
  `biptype` tinytext,
  PRIMARY KEY (`id`),
  KEY `bip` (`bip`)
) ENGINE=InnoDB AUTO_INCREMENT=991 DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.trainingdata_bckupb4mergefeb2019
CREATE TABLE IF NOT EXISTS `trainingdata_bckupb4mergefeb2019` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `bip` int(11) DEFAULT NULL,
  `state` tinytext,
  `manuallyExtracted` tinytext,
  `folder` tinytext,
  `file` tinytext,
  `msgNumberInFile` int(11) DEFAULT NULL,
  `messageID` int(11) DEFAULT NULL,
  `causeMessageID` int(11) DEFAULT NULL,
  `causeParagraphNum` int(11) DEFAULT NULL,
  `causeSentenceNum` int(11) DEFAULT NULL,
  `effectMessageID` int(11) DEFAULT NULL,
  `effectParagraphNum` int(11) DEFAULT NULL,
  `effectSentenceNum` int(11) DEFAULT NULL,
  `causeSentence` text,
  `effectSentence` text,
  `label` text,
  `causeSubCategory` tinytext,
  `Notes` text,
  `fromsubstateextraction` tinyint(4) DEFAULT NULL,
  `effectSentence_cleaned` text,
  `causeSentence_cleaned` text,
  `communityReviewMessageID` int(11) DEFAULT NULL,
  `communityReview` text,
  `proposalAuthorReviewMessageID` int(11) DEFAULT NULL,
  `proposalAuthorReview` text,
  `bdfldelegatePronouncementMessageID` int(11) DEFAULT NULL,
  `consider` int(11) DEFAULT NULL,
  `bdfldelegatePronouncement` text,
  `extractedMessageid` int(11) DEFAULT NULL,
  `datediff` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `bip` (`bip`)
) ENGINE=InnoDB AUTO_INCREMENT=541 DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.trainingdata_bck_10dec2019
CREATE TABLE IF NOT EXISTS `trainingdata_bck_10dec2019` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `bip` int(11) DEFAULT NULL,
  `state` tinytext,
  `dmconcept` tinytext,
  `label` text,
  `folder` tinytext,
  `file` tinytext,
  `msgNumberInFile` int(11) DEFAULT NULL,
  `messageID` int(11) DEFAULT NULL,
  `causeMessageID` int(11) DEFAULT NULL,
  `causeParagraphNum` int(11) DEFAULT NULL,
  `causeSentenceNum` int(11) DEFAULT NULL,
  `effectMessageID` int(11) DEFAULT NULL,
  `effectParagraphNum` int(11) DEFAULT NULL,
  `effectSentenceNum` int(11) DEFAULT NULL,
  `causeSentence` text,
  `effectSentence` text,
  `Notes` text,
  `fromsubstateextraction` tinyint(4) DEFAULT NULL,
  `effectSentence_cleaned` text,
  `causeSentence_cleaned` text,
  `communityReviewMessageID` int(11) DEFAULT NULL,
  `communityReview` text,
  `proposalAuthorReviewMessageID` int(11) DEFAULT NULL,
  `proposalAuthorReview` text,
  `bdfldelegatePronouncementMessageID` int(11) DEFAULT NULL,
  `consider` int(11) DEFAULT NULL,
  `bdfldelegatePronouncement` text,
  `extractedMessageid` int(11) DEFAULT NULL,
  `datediff` int(11) DEFAULT NULL,
  `manuallyExtracted` tinyint(4) DEFAULT NULL,
  `allbips` longtext,
  `biptype` tinytext,
  PRIMARY KEY (`id`),
  KEY `bip` (`bip`)
) ENGINE=InnoDB AUTO_INCREMENT=979 DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.trainingdata_copy30april
CREATE TABLE IF NOT EXISTS `trainingdata_copy30april` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `bip` int(11) DEFAULT NULL,
  `label` tinytext,
  `folder` tinytext,
  `file` tinytext,
  `msgNumberInFile` int(11) DEFAULT NULL,
  `messageID` int(11) DEFAULT NULL,
  `causeMessageID` int(11) DEFAULT NULL,
  `causeParagraphNum` int(11) DEFAULT NULL,
  `causeSentenceNum` int(11) DEFAULT NULL,
  `effectMessageID` int(11) DEFAULT NULL,
  `effectParagraphNum` int(11) DEFAULT NULL,
  `effectSentenceNum` int(11) DEFAULT NULL,
  `causeSentence` text,
  `effectSentence` text,
  `causeCategory` text,
  `Notes` text,
  `effectSentence_cleaned` text,
  `causeSentence_cleaned` text,
  `communityReviewMessageID` int(11) DEFAULT NULL,
  `communityReview` text,
  `proposalAuthorReviewMessageID` int(11) DEFAULT NULL,
  `proposalAuthorReview` text,
  `bdfldelegatePronouncementMessageID` int(11) DEFAULT NULL,
  `bdfldelegatePronouncement` text,
  PRIMARY KEY (`id`),
  KEY `bip` (`bip`)
) ENGINE=InnoDB AUTO_INCREMENT=351 DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.trainingdata_copy5may
CREATE TABLE IF NOT EXISTS `trainingdata_copy5may` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `bip` int(11) DEFAULT NULL,
  `label` tinytext,
  `folder` tinytext,
  `file` tinytext,
  `msgNumberInFile` int(11) DEFAULT NULL,
  `messageID` int(11) DEFAULT NULL,
  `causeMessageID` int(11) DEFAULT NULL,
  `causeParagraphNum` int(11) DEFAULT NULL,
  `causeSentenceNum` int(11) DEFAULT NULL,
  `effectMessageID` int(11) DEFAULT NULL,
  `effectParagraphNum` int(11) DEFAULT NULL,
  `effectSentenceNum` int(11) DEFAULT NULL,
  `causeSentence` text,
  `effectSentence` text,
  `causeCategory` text,
  `Notes` text,
  `effectSentence_cleaned` text,
  `causeSentence_cleaned` text,
  `communityReviewMessageID` int(11) DEFAULT NULL,
  `communityReview` text,
  `proposalAuthorReviewMessageID` int(11) DEFAULT NULL,
  `proposalAuthorReview` text,
  `bdfldelegatePronouncementMessageID` int(11) DEFAULT NULL,
  `bdfldelegatePronouncement` text,
  PRIMARY KEY (`id`),
  KEY `bip` (`bip`)
) ENGINE=InnoDB AUTO_INCREMENT=351 DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.trainingdata_copyaug2018
CREATE TABLE IF NOT EXISTS `trainingdata_copyaug2018` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `bip` int(11) DEFAULT NULL,
  `label` tinytext,
  `folder` tinytext,
  `file` tinytext,
  `msgNumberInFile` int(11) DEFAULT NULL,
  `messageID` int(11) DEFAULT NULL,
  `causeMessageID` int(11) DEFAULT NULL,
  `causeParagraphNum` int(11) DEFAULT NULL,
  `causeSentenceNum` int(11) DEFAULT NULL,
  `effectMessageID` int(11) DEFAULT NULL,
  `effectParagraphNum` int(11) DEFAULT NULL,
  `effectSentenceNum` int(11) DEFAULT NULL,
  `causeSentence` text,
  `effectSentence` text,
  `causeCategory` text,
  `causeSubCategory` tinytext,
  `Notes` text,
  `effectSentence_cleaned` text,
  `causeSentence_cleaned` text,
  `communityReviewMessageID` int(11) DEFAULT NULL,
  `communityReview` text,
  `proposalAuthorReviewMessageID` int(11) DEFAULT NULL,
  `proposalAuthorReview` text,
  `bdfldelegatePronouncementMessageID` int(11) DEFAULT NULL,
  `bdfldelegatePronouncement` text,
  PRIMARY KEY (`id`),
  KEY `bip` (`bip`)
) ENGINE=InnoDB AUTO_INCREMENT=461 DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.trainingdata_correcteddata_12may2020
CREATE TABLE IF NOT EXISTS `trainingdata_correcteddata_12may2020` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `bip` int(11) DEFAULT NULL,
  `state` tinytext,
  `dmconcept` tinytext,
  `label` text,
  `folder` tinytext,
  `file` tinytext,
  `msgNumberInFile` int(11) DEFAULT NULL,
  `messageID` int(11) DEFAULT NULL,
  `causeMessageID` int(11) DEFAULT NULL,
  `causeParagraphNum` int(11) DEFAULT NULL,
  `causeSentenceNum` int(11) DEFAULT NULL,
  `effectMessageID` int(11) DEFAULT NULL,
  `effectParagraphNum` int(11) DEFAULT NULL,
  `effectSentenceNum` int(11) DEFAULT NULL,
  `causeSentence` text,
  `effectSentence` text,
  `Notes` text,
  `fromsubstateextraction` tinyint(4) DEFAULT NULL,
  `effectSentence_cleaned` text,
  `causeSentence_cleaned` text,
  `communityReviewMessageID` int(11) DEFAULT NULL,
  `communityReview` text,
  `proposalAuthorReviewMessageID` int(11) DEFAULT NULL,
  `proposalAuthorReview` text,
  `bdfldelegatePronouncementMessageID` int(11) DEFAULT NULL,
  `consider` int(11) DEFAULT NULL,
  `bdfldelegatePronouncement` text,
  `extractedMessageid` int(11) DEFAULT NULL,
  `datediff` int(11) DEFAULT NULL,
  `manuallyExtracted` tinyint(4) DEFAULT NULL,
  `allbips` longtext,
  `biptype` tinytext,
  `toconsiderwhilereportingresults` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `bip` (`bip`)
) ENGINE=InnoDB AUTO_INCREMENT=1058 DEFAULT CHARSET=latin1 COMMENT='based on best previous set - the gold set - trainingdata_correcteddata_12may2020';

-- Data exporting was unselected.

-- Dumping structure for table bips_new.trainingdata_goldstandardmay2020
CREATE TABLE IF NOT EXISTS `trainingdata_goldstandardmay2020` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `bip` int(11) DEFAULT NULL,
  `state` tinytext,
  `dmconcept` tinytext,
  `label` text,
  `folder` tinytext,
  `file` tinytext,
  `msgNumberInFile` int(11) DEFAULT NULL,
  `messageID` int(11) DEFAULT NULL,
  `causeMessageID` int(11) DEFAULT NULL,
  `causeParagraphNum` int(11) DEFAULT NULL,
  `causeSentenceNum` int(11) DEFAULT NULL,
  `effectMessageID` int(11) DEFAULT NULL,
  `effectParagraphNum` int(11) DEFAULT NULL,
  `effectSentenceNum` int(11) DEFAULT NULL,
  `causeSentence` text,
  `effectSentence` text,
  `Notes` text,
  `fromsubstateextraction` tinyint(4) DEFAULT NULL,
  `effectSentence_cleaned` text,
  `causeSentence_cleaned` text,
  `communityReviewMessageID` int(11) DEFAULT NULL,
  `communityReview` text,
  `proposalAuthorReviewMessageID` int(11) DEFAULT NULL,
  `proposalAuthorReview` text,
  `bdfldelegatePronouncementMessageID` int(11) DEFAULT NULL,
  `consider` int(11) DEFAULT NULL,
  `bdfldelegatePronouncement` text,
  `extractedMessageid` int(11) DEFAULT NULL,
  `datediff` int(11) DEFAULT NULL,
  `manuallyExtracted` tinyint(4) DEFAULT NULL,
  `allbips` longtext,
  `biptype` tinytext,
  PRIMARY KEY (`id`),
  KEY `bip` (`bip`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT COMMENT='trainingdata_feb2020';

-- Data exporting was unselected.

-- Dumping structure for table bips_new.trainingdata_june2020
CREATE TABLE IF NOT EXISTS `trainingdata_june2020` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `bip` int(11) DEFAULT NULL,
  `state` tinytext,
  `dmscheme` tinytext,
  `reason` text,
  `folder` tinytext,
  `file` tinytext,
  `msgNumberInFile` int(11) DEFAULT NULL,
  `messageID` int(11) DEFAULT NULL,
  `causeMessageID` int(11) DEFAULT NULL,
  `causeParagraphNum` int(11) DEFAULT NULL,
  `causeSentenceNum` int(11) DEFAULT NULL,
  `effectMessageID` int(11) DEFAULT NULL,
  `effectParagraphNum` int(11) DEFAULT NULL,
  `effectSentenceNum` int(11) DEFAULT NULL,
  `causeSentence` text,
  `effectSentence` text,
  `Notes` text,
  `fromsubstateextraction` tinyint(4) DEFAULT NULL,
  `effectSentence_cleaned` text,
  `causeSentence_cleaned` text,
  `communityReviewMessageID` int(11) DEFAULT NULL,
  `communityReview` text,
  `proposalAuthorReviewMessageID` int(11) DEFAULT NULL,
  `proposalAuthorReview` text,
  `bdfldelegatePronouncementMessageID` int(11) DEFAULT NULL,
  `consider` int(11) DEFAULT NULL,
  `bdfldelegatePronouncement` text,
  `extractedMessageid` int(11) DEFAULT NULL,
  `datediff` int(11) DEFAULT NULL,
  `manuallyExtracted` tinyint(4) DEFAULT NULL,
  `allbips` longtext,
  `biptype` tinytext,
  `toconsiderwhilereportingresults` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `bip` (`bip`)
) ENGINE=InnoDB AUTO_INCREMENT=1060 DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.trainingdata_mymanuallyextracted_aug2018
CREATE TABLE IF NOT EXISTS `trainingdata_mymanuallyextracted_aug2018` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `bip` int(11) DEFAULT NULL,
  `label` tinytext,
  `folder` tinytext,
  `file` tinytext,
  `msgNumberInFile` int(11) DEFAULT NULL,
  `messageID` int(11) DEFAULT NULL,
  `causeMessageID` int(11) DEFAULT NULL,
  `causeParagraphNum` int(11) DEFAULT NULL,
  `causeSentenceNum` int(11) DEFAULT NULL,
  `effectMessageID` int(11) DEFAULT NULL,
  `effectParagraphNum` int(11) DEFAULT NULL,
  `effectSentenceNum` int(11) DEFAULT NULL,
  `causeSentence` text,
  `effectSentence` text,
  `causeCategory` text,
  `causeSubCategory` tinytext,
  `Notes` text,
  `effectSentence_cleaned` text,
  `causeSentence_cleaned` text,
  `communityReviewMessageID` int(11) DEFAULT NULL,
  `communityReview` text,
  `proposalAuthorReviewMessageID` int(11) DEFAULT NULL,
  `proposalAuthorReview` text,
  `bdfldelegatePronouncementMessageID` int(11) DEFAULT NULL,
  `bdfldelegatePronouncement` text,
  PRIMARY KEY (`id`),
  KEY `bip` (`bip`)
) ENGINE=InnoDB AUTO_INCREMENT=461 DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.trainingdata_sept2020
CREATE TABLE IF NOT EXISTS `trainingdata_sept2020` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `bip` int(11) DEFAULT NULL,
  `state` tinytext,
  `dmscheme` tinytext,
  `reason` text,
  `folder` tinytext,
  `authorRole` tinytext,
  `delegateRole` tinytext,
  `file` tinytext,
  `msgNumberInFile` int(11) DEFAULT NULL,
  `messageID` int(11) DEFAULT NULL,
  `causeMessageID` int(11) DEFAULT NULL,
  `causeParagraphNum` int(11) DEFAULT NULL,
  `causeSentenceNum` int(11) DEFAULT NULL,
  `effectMessageID` int(11) DEFAULT NULL,
  `effectParagraphNum` int(11) DEFAULT NULL,
  `effectSentenceNum` int(11) DEFAULT NULL,
  `causeSentence` text,
  `effectSentence` text,
  `Notes` text,
  `fromsubstateextraction` tinyint(4) DEFAULT NULL,
  `effectSentence_cleaned` text,
  `causeSentence_cleaned` text,
  `communityReviewMessageID` int(11) DEFAULT NULL,
  `communityReview` text,
  `proposalAuthorReviewMessageID` int(11) DEFAULT NULL,
  `proposalAuthorReview` text,
  `bdfldelegatePronouncementMessageID` int(11) DEFAULT NULL,
  `consider` int(11) DEFAULT NULL,
  `bdfldelegatePronouncement` text,
  `extractedMessageid` int(11) DEFAULT NULL,
  `datediff` int(11) DEFAULT NULL,
  `manuallyExtracted` tinyint(4) DEFAULT NULL,
  `allbips` longtext,
  `biptype` tinytext,
  `toconsiderwhilereportingresults` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `bip` (`bip`)
) ENGINE=InnoDB AUTO_INCREMENT=1060 DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.trainingdata_thesis_submitted
CREATE TABLE IF NOT EXISTS `trainingdata_thesis_submitted` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `bip` int(11) DEFAULT NULL,
  `state` tinytext,
  `dmconcept` tinytext,
  `label` text,
  `folder` tinytext,
  `file` tinytext,
  `msgNumberInFile` int(11) DEFAULT NULL,
  `messageID` int(11) DEFAULT NULL,
  `causeMessageID` int(11) DEFAULT NULL,
  `causeParagraphNum` int(11) DEFAULT NULL,
  `causeSentenceNum` int(11) DEFAULT NULL,
  `effectMessageID` int(11) DEFAULT NULL,
  `effectParagraphNum` int(11) DEFAULT NULL,
  `effectSentenceNum` int(11) DEFAULT NULL,
  `causeSentence` text,
  `effectSentence` text,
  `Notes` text,
  `fromsubstateextraction` tinyint(4) DEFAULT NULL,
  `effectSentence_cleaned` text,
  `causeSentence_cleaned` text,
  `communityReviewMessageID` int(11) DEFAULT NULL,
  `communityReview` text,
  `proposalAuthorReviewMessageID` int(11) DEFAULT NULL,
  `proposalAuthorReview` text,
  `bdfldelegatePronouncementMessageID` int(11) DEFAULT NULL,
  `consider` int(11) DEFAULT NULL,
  `bdfldelegatePronouncement` text,
  `extractedMessageid` int(11) DEFAULT NULL,
  `datediff` int(11) DEFAULT NULL,
  `manuallyExtracted` tinyint(4) DEFAULT NULL,
  `allbips` longtext,
  `biptype` tinytext,
  PRIMARY KEY (`id`),
  KEY `bip` (`bip`)
) ENGINE=InnoDB AUTO_INCREMENT=991 DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT COMMENT='trainingdata_feb2020';

-- Data exporting was unselected.

-- Dumping structure for table bips_new.tstret
CREATE TABLE IF NOT EXISTS `tstret` (
  `author` text,
  `DMsubphase` varchar(15) CHARACTER SET utf8mb4 DEFAULT NULL,
  `TotalMsgByPerson` decimal(32,0) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.xb
CREATE TABLE IF NOT EXISTS `xb` (
  `bip` int(11) DEFAULT NULL,
  `state` longtext,
  `combined` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.xd
CREATE TABLE IF NOT EXISTS `xd` (
  `bip` int(11) DEFAULT NULL,
  `state` longtext,
  `combined` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.xe
CREATE TABLE IF NOT EXISTS `xe` (
  `bip` int(11) DEFAULT NULL,
  `state` longtext,
  `combined` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.xf
CREATE TABLE IF NOT EXISTS `xf` (
  `bip` int(11) DEFAULT NULL,
  `state` longtext,
  `combined` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for table bips_new.xg
CREATE TABLE IF NOT EXISTS `xg` (
  `bip` int(11) DEFAULT NULL,
  `state` longtext,
  `combined` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.

-- Dumping structure for view bips_new.allmessages_child
-- Creating temporary table to overcome VIEW dependency errors
CREATE TABLE `allmessages_child` (
	`bip` INT(11) NULL,
	`originalbipNumber` INT(11) NULL,
	`originalbipNumberNum` INT(11) NULL,
	`messageID` INT(11) NULL,
	`date2` DATE NULL,
	`bipType` TEXT NULL COLLATE 'latin1_swedish_ci',
	`dateTimeStamp` TIMESTAMP NULL,
	`dateTimeStampString` TEXT NULL COLLATE 'latin1_swedish_ci',
	`email` LONGTEXT NULL COLLATE 'latin1_swedish_ci',
	`analyseWords` LONGTEXT NULL COLLATE 'latin1_swedish_ci',
	`corefMsg` LONGTEXT NULL COLLATE 'latin1_swedish_ci',
	`Author` MEDIUMTEXT NULL COLLATE 'latin1_swedish_ci',
	`subject` MEDIUMTEXT NULL COLLATE 'latin1_swedish_ci',
	`location` TEXT NULL COLLATE 'latin1_swedish_ci',
	`line` LONGTEXT NULL COLLATE 'latin1_swedish_ci',
	`folder` TEXT NULL COLLATE 'latin1_swedish_ci',
	`file` TEXT NULL COLLATE 'latin1_swedish_ci',
	`from` TEXT NULL COLLATE 'latin1_swedish_ci',
	`emailMessageId` TINYTEXT NULL COLLATE 'latin1_swedish_ci',
	`inReplyTo` TEXT NULL COLLATE 'latin1_swedish_ci',
	`inReplyToUser` TEXT NULL COLLATE 'latin1_swedish_ci',
	`references` TEXT NULL COLLATE 'latin1_swedish_ci',
	`link` TINYTEXT NULL COLLATE 'latin1_swedish_ci',
	`senderName` TINYTEXT NULL COLLATE 'latin1_swedish_ci',
	`name` TINYTEXT NULL COLLATE 'latin1_swedish_ci',
	`required` TINYINT(4) NULL,
	`hasbipTitleOnly` TINYINT(4) NULL,
	`countRepliesToThisMessage` INT(11) NULL,
	`fromLine` TEXT NULL COLLATE 'latin1_swedish_ci',
	`senderemail` TINYTEXT NULL COLLATE 'latin1_swedish_ci',
	`senderemailProcessed` TINYTEXT NULL COLLATE 'latin1_swedish_ci',
	`senderEmailFirstSegment` TINYTEXT NULL COLLATE 'latin1_swedish_ci',
	`senderFullName` TINYTEXT NULL COLLATE 'latin1_swedish_ci',
	`senderFirstName` TINYTEXT NULL COLLATE 'latin1_swedish_ci',
	`senderLastName` TINYTEXT NULL COLLATE 'latin1_swedish_ci',
	`clusterBySenderFullName` TINYTEXT NULL COLLATE 'latin1_swedish_ci',
	`inReplyToUserUsingClusteredSender` TINYTEXT NULL COLLATE 'latin1_swedish_ci',
	`IdentifierCount` INT(11) NULL,
	`msgNumInFile` INT(11) NULL,
	`processedSubject` MEDIUMTEXT NULL COLLATE 'latin1_swedish_ci',
	`authorsrole` MEDIUMTEXT NULL COLLATE 'latin1_swedish_ci',
	`ifProposalAtTheEndofMessage` TINYINT(4) NULL,
	`processMessage` TINYINT(4) NULL,
	`pTitleMatchedinMsgSub` TINYINT(4) NULL,
	`pNumMatchedinMsgSub` TINYINT(4) NULL,
	`pNumMatchedinMsgBody` TINYINT(4) NULL,
	`messageType` TINYTEXT NULL COLLATE 'latin1_swedish_ci',
	`lastdir` TEXT NULL COLLATE 'latin1_swedish_ci'
) ENGINE=MyISAM;

-- Dumping structure for view bips_new.allmessages_child_first
-- Creating temporary table to overcome VIEW dependency errors
CREATE TABLE `allmessages_child_first` (
	`bip` INT(11) NULL,
	`originalbipNumber` INT(11) NULL,
	`originalbipNumberNum` INT(11) NULL,
	`messageID` INT(11) NULL,
	`date2` DATE NULL,
	`bipType` TEXT NULL COLLATE 'latin1_swedish_ci',
	`dateTimeStamp` TIMESTAMP NULL,
	`dateTimeStampString` TEXT NULL COLLATE 'latin1_swedish_ci',
	`email` LONGTEXT NULL COLLATE 'latin1_swedish_ci',
	`analyseWords` LONGTEXT NULL COLLATE 'latin1_swedish_ci',
	`corefMsg` LONGTEXT NULL COLLATE 'latin1_swedish_ci',
	`Author` MEDIUMTEXT NULL COLLATE 'latin1_swedish_ci',
	`subject` MEDIUMTEXT NULL COLLATE 'latin1_swedish_ci',
	`location` TEXT NULL COLLATE 'latin1_swedish_ci',
	`line` LONGTEXT NULL COLLATE 'latin1_swedish_ci',
	`folder` TEXT NULL COLLATE 'latin1_swedish_ci',
	`file` TEXT NULL COLLATE 'latin1_swedish_ci',
	`from` TEXT NULL COLLATE 'latin1_swedish_ci',
	`emailMessageId` TINYTEXT NULL COLLATE 'latin1_swedish_ci',
	`inReplyTo` TEXT NULL COLLATE 'latin1_swedish_ci',
	`inReplyToUser` TEXT NULL COLLATE 'latin1_swedish_ci',
	`references` TEXT NULL COLLATE 'latin1_swedish_ci',
	`link` TINYTEXT NULL COLLATE 'latin1_swedish_ci',
	`senderName` TINYTEXT NULL COLLATE 'latin1_swedish_ci',
	`name` TINYTEXT NULL COLLATE 'latin1_swedish_ci',
	`required` TINYINT(4) NULL,
	`hasbipTitleOnly` TINYINT(4) NULL,
	`countRepliesToThisMessage` INT(11) NULL,
	`fromLine` TEXT NULL COLLATE 'latin1_swedish_ci',
	`senderemail` TINYTEXT NULL COLLATE 'latin1_swedish_ci',
	`senderemailProcessed` TINYTEXT NULL COLLATE 'latin1_swedish_ci',
	`senderEmailFirstSegment` TINYTEXT NULL COLLATE 'latin1_swedish_ci',
	`senderFullName` TINYTEXT NULL COLLATE 'latin1_swedish_ci',
	`senderFirstName` TINYTEXT NULL COLLATE 'latin1_swedish_ci',
	`senderLastName` TINYTEXT NULL COLLATE 'latin1_swedish_ci',
	`clusterBySenderFullName` TINYTEXT NULL COLLATE 'latin1_swedish_ci',
	`inReplyToUserUsingClusteredSender` TINYTEXT NULL COLLATE 'latin1_swedish_ci',
	`IdentifierCount` INT(11) NULL,
	`msgNumInFile` INT(11) NULL,
	`processedSubject` MEDIUMTEXT NULL COLLATE 'latin1_swedish_ci',
	`authorsrole` MEDIUMTEXT NULL COLLATE 'latin1_swedish_ci',
	`ifProposalAtTheEndofMessage` TINYINT(4) NULL,
	`processMessage` TINYINT(4) NULL,
	`pTitleMatchedinMsgSub` TINYINT(4) NULL,
	`pNumMatchedinMsgSub` TINYINT(4) NULL,
	`pNumMatchedinMsgBody` TINYINT(4) NULL,
	`messageType` TINYTEXT NULL COLLATE 'latin1_swedish_ci',
	`lastdir` TEXT NULL COLLATE 'latin1_swedish_ci'
) ENGINE=MyISAM;

-- Dumping structure for view bips_new.allmessages_parent
-- Creating temporary table to overcome VIEW dependency errors
CREATE TABLE `allmessages_parent` (
	`bip` INT(11) NULL,
	`originalbipNumber` INT(11) NULL,
	`originalbipNumberNum` INT(11) NULL,
	`messageID` INT(11) NULL,
	`date2` DATE NULL,
	`bipType` TEXT NULL COLLATE 'latin1_swedish_ci',
	`dateTimeStamp` TIMESTAMP NULL,
	`dateTimeStampString` TEXT NULL COLLATE 'latin1_swedish_ci',
	`email` LONGTEXT NULL COLLATE 'latin1_swedish_ci',
	`analyseWords` LONGTEXT NULL COLLATE 'latin1_swedish_ci',
	`corefMsg` LONGTEXT NULL COLLATE 'latin1_swedish_ci',
	`Author` MEDIUMTEXT NULL COLLATE 'latin1_swedish_ci',
	`subject` MEDIUMTEXT NULL COLLATE 'latin1_swedish_ci',
	`location` TEXT NULL COLLATE 'latin1_swedish_ci',
	`line` LONGTEXT NULL COLLATE 'latin1_swedish_ci',
	`folder` TEXT NULL COLLATE 'latin1_swedish_ci',
	`file` TEXT NULL COLLATE 'latin1_swedish_ci',
	`from` TEXT NULL COLLATE 'latin1_swedish_ci',
	`emailMessageId` TINYTEXT NULL COLLATE 'latin1_swedish_ci',
	`inReplyTo` TEXT NULL COLLATE 'latin1_swedish_ci',
	`inReplyToUser` TEXT NULL COLLATE 'latin1_swedish_ci',
	`references` TEXT NULL COLLATE 'latin1_swedish_ci',
	`link` TINYTEXT NULL COLLATE 'latin1_swedish_ci',
	`senderName` TINYTEXT NULL COLLATE 'latin1_swedish_ci',
	`name` TINYTEXT NULL COLLATE 'latin1_swedish_ci',
	`required` TINYINT(4) NULL,
	`hasbipTitleOnly` TINYINT(4) NULL,
	`countRepliesToThisMessage` INT(11) NULL,
	`fromLine` TEXT NULL COLLATE 'latin1_swedish_ci',
	`senderemail` TINYTEXT NULL COLLATE 'latin1_swedish_ci',
	`senderemailProcessed` TINYTEXT NULL COLLATE 'latin1_swedish_ci',
	`senderEmailFirstSegment` TINYTEXT NULL COLLATE 'latin1_swedish_ci',
	`senderFullName` TINYTEXT NULL COLLATE 'latin1_swedish_ci',
	`senderFirstName` TINYTEXT NULL COLLATE 'latin1_swedish_ci',
	`senderLastName` TINYTEXT NULL COLLATE 'latin1_swedish_ci',
	`clusterBySenderFullName` TINYTEXT NULL COLLATE 'latin1_swedish_ci',
	`inReplyToUserUsingClusteredSender` TINYTEXT NULL COLLATE 'latin1_swedish_ci',
	`IdentifierCount` INT(11) NULL,
	`msgNumInFile` INT(11) NULL,
	`processedSubject` MEDIUMTEXT NULL COLLATE 'latin1_swedish_ci',
	`authorsrole` MEDIUMTEXT NULL COLLATE 'latin1_swedish_ci',
	`ifProposalAtTheEndofMessage` TINYINT(4) NULL,
	`processMessage` TINYINT(4) NULL,
	`pTitleMatchedinMsgSub` TINYINT(4) NULL,
	`pNumMatchedinMsgSub` TINYINT(4) NULL,
	`pNumMatchedinMsgBody` TINYINT(4) NULL,
	`messageType` TINYTEXT NULL COLLATE 'latin1_swedish_ci',
	`lastdir` TEXT NULL COLLATE 'latin1_swedish_ci'
) ENGINE=MyISAM;

-- Dumping structure for view bips_new.allmessages_parent_first
-- Creating temporary table to overcome VIEW dependency errors
CREATE TABLE `allmessages_parent_first` (
	`bip` INT(11) NULL,
	`originalbipNumber` INT(11) NULL,
	`originalbipNumberNum` INT(11) NULL,
	`messageID` INT(11) NULL,
	`date2` DATE NULL,
	`bipType` TEXT NULL COLLATE 'latin1_swedish_ci',
	`dateTimeStamp` TIMESTAMP NULL,
	`dateTimeStampString` TEXT NULL COLLATE 'latin1_swedish_ci',
	`email` LONGTEXT NULL COLLATE 'latin1_swedish_ci',
	`analyseWords` LONGTEXT NULL COLLATE 'latin1_swedish_ci',
	`corefMsg` LONGTEXT NULL COLLATE 'latin1_swedish_ci',
	`Author` MEDIUMTEXT NULL COLLATE 'latin1_swedish_ci',
	`subject` MEDIUMTEXT NULL COLLATE 'latin1_swedish_ci',
	`location` TEXT NULL COLLATE 'latin1_swedish_ci',
	`line` LONGTEXT NULL COLLATE 'latin1_swedish_ci',
	`folder` TEXT NULL COLLATE 'latin1_swedish_ci',
	`file` TEXT NULL COLLATE 'latin1_swedish_ci',
	`from` TEXT NULL COLLATE 'latin1_swedish_ci',
	`emailMessageId` TINYTEXT NULL COLLATE 'latin1_swedish_ci',
	`inReplyTo` TEXT NULL COLLATE 'latin1_swedish_ci',
	`inReplyToUser` TEXT NULL COLLATE 'latin1_swedish_ci',
	`references` TEXT NULL COLLATE 'latin1_swedish_ci',
	`link` TINYTEXT NULL COLLATE 'latin1_swedish_ci',
	`senderName` TINYTEXT NULL COLLATE 'latin1_swedish_ci',
	`name` TINYTEXT NULL COLLATE 'latin1_swedish_ci',
	`required` TINYINT(4) NULL,
	`hasbipTitleOnly` TINYINT(4) NULL,
	`countRepliesToThisMessage` INT(11) NULL,
	`fromLine` TEXT NULL COLLATE 'latin1_swedish_ci',
	`senderemail` TINYTEXT NULL COLLATE 'latin1_swedish_ci',
	`senderemailProcessed` TINYTEXT NULL COLLATE 'latin1_swedish_ci',
	`senderEmailFirstSegment` TINYTEXT NULL COLLATE 'latin1_swedish_ci',
	`senderFullName` TINYTEXT NULL COLLATE 'latin1_swedish_ci',
	`senderFirstName` TINYTEXT NULL COLLATE 'latin1_swedish_ci',
	`senderLastName` TINYTEXT NULL COLLATE 'latin1_swedish_ci',
	`clusterBySenderFullName` TINYTEXT NULL COLLATE 'latin1_swedish_ci',
	`inReplyToUserUsingClusteredSender` TINYTEXT NULL COLLATE 'latin1_swedish_ci',
	`IdentifierCount` INT(11) NULL,
	`msgNumInFile` INT(11) NULL,
	`processedSubject` MEDIUMTEXT NULL COLLATE 'latin1_swedish_ci',
	`authorsrole` MEDIUMTEXT NULL COLLATE 'latin1_swedish_ci',
	`ifProposalAtTheEndofMessage` TINYINT(4) NULL,
	`processMessage` TINYINT(4) NULL,
	`pTitleMatchedinMsgSub` TINYINT(4) NULL,
	`pNumMatchedinMsgSub` TINYINT(4) NULL,
	`pNumMatchedinMsgBody` TINYINT(4) NULL,
	`messageType` TINYTEXT NULL COLLATE 'latin1_swedish_ci',
	`lastdir` TEXT NULL COLLATE 'latin1_swedish_ci'
) ENGINE=MyISAM;

-- Dumping structure for view bips_new.allotherstates
-- Creating temporary table to overcome VIEW dependency errors
CREATE TABLE `allotherstates` (
	`bip` INT(11) NULL
) ENGINE=MyISAM;

-- Dumping structure for view bips_new.cdev
-- Creating temporary table to overcome VIEW dependency errors
CREATE TABLE `cdev` (
	`clusterBySenderFullName` TINYTEXT NULL COLLATE 'latin1_swedish_ci'
) ENGINE=MyISAM;

-- Dumping structure for view bips_new.commonroles
-- Creating temporary table to overcome VIEW dependency errors
CREATE TABLE `commonroles` (
	`author` TINYTEXT NULL COLLATE 'latin1_swedish_ci',
	`authorsrole` TINYTEXT NULL COLLATE 'latin1_swedish_ci',
	`bipCount` BIGINT(21) NOT NULL
) ENGINE=MyISAM;

-- Dumping structure for view bips_new.distinctclusters
-- Creating temporary table to overcome VIEW dependency errors
CREATE TABLE `distinctclusters` (
	`cluster` INT(11) NULL,
	`members` BIGINT(21) NOT NULL
) ENGINE=MyISAM;

-- Dumping structure for view bips_new.duplicatemessageid
-- Creating temporary table to overcome VIEW dependency errors
CREATE TABLE `duplicatemessageid` (
	`bip` INT(11) NOT NULL,
	`messageid` INT(11) NULL,
	`timestamp` TIMESTAMP NULL,
	`NumOccurrences` BIGINT(21) NOT NULL
) ENGINE=MyISAM;

-- Dumping structure for view bips_new.email_a
-- Creating temporary table to overcome VIEW dependency errors
CREATE TABLE `email_a` (
	`bip` INT(11) NULL,
	`dateTIMESTAMP` TIMESTAMP NULL,
	`state` LONGTEXT NULL COLLATE 'latin1_swedish_ci',
	`ptype` TEXT NULL COLLATE 'latin1_swedish_ci'
) ENGINE=MyISAM;

-- Dumping structure for view bips_new.email_f
-- Creating temporary table to overcome VIEW dependency errors
CREATE TABLE `email_f` (
	`bip` INT(11) NULL,
	`dateTIMESTAMP` TIMESTAMP NULL,
	`state` LONGTEXT NULL COLLATE 'latin1_swedish_ci',
	`ptype` TEXT NULL COLLATE 'latin1_swedish_ci'
) ENGINE=MyISAM;

-- Dumping structure for view bips_new.firststates
-- Creating temporary table to overcome VIEW dependency errors
CREATE TABLE `firststates` (
	`bip` INT(11) NULL
) ENGINE=MyISAM;

-- Dumping structure for view bips_new.ones
-- Creating temporary table to overcome VIEW dependency errors
CREATE TABLE `ones` 
) ENGINE=MyISAM;

-- Dumping structure for view bips_new.biptypecount
-- Creating temporary table to overcome VIEW dependency errors
CREATE TABLE `biptypecount` (
	`bip` INT(11) NULL,
	`ptype` TEXT NULL COLLATE 'latin1_swedish_ci'
) ENGINE=MyISAM;

-- Dumping structure for view bips_new.rp_bdflp
-- Creating temporary table to overcome VIEW dependency errors
CREATE TABLE `rp_bdflp` (
	`bip` INT(11) NOT NULL,
	`TIMESTAMP` TIMESTAMP NULL,
	`clausie` TEXT NULL COLLATE 'latin1_swedish_ci',
	`ptype` TEXT NULL COLLATE 'latin1_swedish_ci'
) ENGINE=MyISAM;

-- Dumping structure for view bips_new.rp_both
-- Creating temporary table to overcome VIEW dependency errors
CREATE TABLE `rp_both` (
	`bip` INT(11) NOT NULL,
	`clausie` TEXT NULL COLLATE 'latin1_swedish_ci',
	`ptype` TEXT NULL COLLATE 'latin1_swedish_ci'
) ENGINE=MyISAM;

-- Dumping structure for view bips_new.rp_both_s
-- Creating temporary table to overcome VIEW dependency errors
CREATE TABLE `rp_both_s` (
	`bip` INT(11) NOT NULL,
	`clausie` TEXT NULL COLLATE 'latin1_swedish_ci',
	`ptype` TEXT NULL COLLATE 'latin1_swedish_ci'
) ENGINE=MyISAM;

-- Dumping structure for view bips_new.rp_consensus
-- Creating temporary table to overcome VIEW dependency errors
CREATE TABLE `rp_consensus` (
	`bip` INT(11) NOT NULL,
	`TIMESTAMP` TIMESTAMP NULL,
	`clausie` TEXT NULL COLLATE 'latin1_swedish_ci',
	`ptype` TEXT NULL COLLATE 'latin1_swedish_ci'
) ENGINE=MyISAM;

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

-- Dumping structure for view bips_new.top10authors
-- Creating temporary table to overcome VIEW dependency errors
CREATE TABLE `top10authors` (
	`AUTHOR` TEXT NULL COLLATE 'latin1_swedish_ci',
	`S` DECIMAL(32,0) NULL
) ENGINE=MyISAM;

-- Dumping structure for view bips_new.unfinished
-- Creating temporary table to overcome VIEW dependency errors
CREATE TABLE `unfinished` (
	`bip` INT(11) NULL
) ENGINE=MyISAM;

-- Dumping structure for procedure bips_new.createEmailInvolvementForEachMember_bipTypeDMSuphase
DELIMITER //
CREATE PROCEDURE `createEmailInvolvementForEachMember_bipTypeDMSuphase`(IN v_bip INT(10), IN v_state TEXT(15), IN v_date2 DATE, IN v_biptype TEXT(15))
BEGIN
	DECLARE finished INTEGER DEFAULT 0; DECLARE v_cnt INTEGER DEFAULT 0; DECLARE p_state TEXT; DECLARE vv_bip INTEGER;
	DECLARE v_authorsrole TEXT(30); DECLARE vv_biptype TEXT(30); DECLARE vv_lastdir TEXT(20); DECLARE v_author TEXT(30); 
--	SET p_state 
	DECLARE curgetMessages 
			CURSOR FOR 
				SELECT bipnum2020, biptype2020, author, authorsrole, lastdir, COUNT(messageid) 				
				FROM allmessages -- FROM allmessages_bdfldelegatedecided2020 m
				WHERE bipnum2020 = v_bip -- IN (SELECT bip FROM accrejbips)				
				-- AND LENGTH(authorsrole) > 0 AND LENGTH(inReplyToUserUsingClusteredSenderrole) > 0 	
				-- WHERE -- lastdir = 'python-dev' -- ONE BY one
				-- AND 
				AND (authorsrole = 'bdfl' OR authorsrole = 'bdfl_delegate' OR authorsrole = 'proposalAuthor'
				OR author IN (SELECT author FROM top10authors ORDER BY s desc)	
					)				
				GROUP BY bipnum2020, authorsrole, lastdir			
				ORDER BY authorsrole;
				-- declare NOT FOUND handler
				DECLARE CONTINUE HANDLER 
        		FOR NOT FOUND SET finished = 1;
OPEN curgetMessages;

getMessages: 
		LOOP
			FETCH curgetMessages INTO vv_bip, vv_biptype, v_author, v_authorsrole,  vv_lastdir, v_cnt; -- , v_cnt; 
			IF finished = 1 THEN 
				LEAVE getMessages;
			END IF;
			INSERT INTO rolecommunicationsforeachmember_biptypedmsubphase(bip, author, authorsrole, biptype, lastdir,  cnt) -- , cnt)
			VALUES (vv_bip, v_author, v_authorsrole, vv_biptype, vv_lastdir, v_cnt); -- , v_cnt);
		END LOOP getMessages;
		CLOSE curgetMessages;

END//
DELIMITER ;

-- Dumping structure for procedure bips_new.createEmailInvolvementForEachPerson
DELIMITER //
CREATE PROCEDURE `createEmailInvolvementForEachPerson`(IN v_bip INT(10), IN v_state TEXT(15), IN v_date2 DATE)
BEGIN
	DECLARE finished INTEGER DEFAULT 0; DECLARE v_cnt INTEGER DEFAULT 0; DECLARE p_state TEXT; DECLARE vv_bip INTEGER;
	DECLARE v_author TEXT(30); DECLARE v_inReplyToUserUsingClusteredSender TEXT(30);
--	SET p_state 
	DECLARE curgetMessages 
			CURSOR FOR 
				SELECT bip, author, inReplyToUserUsingClusteredSender, 
				-- COUNT(messageid) AS cnt, -- messageid, author, authorsrole, DATE2, datetimestamp
--				bip
--				, (SELECT state FROM bipstates_danieldata_datetimestamp WHERE bip = m.bip AND STATE LIKE 'acc' OR state LIKE '%rej%' order by DATE2 asc LIMIT 1)
--				author, inReplyToUserUsingClusteredSender, 
				-- (select authorsrole FROM commonroles WHERE author = m.author ),  
--				authorsrole, (select bipCount FROM commonroles WHERE author = m.author ) AS bipinvolvementcount, -- add number of bips the member has contributed in
--				analysewords
				(select bipCount FROM commonroles WHERE author = m.author ) AS cnt 
				FROM allmessages m
				WHERE bip = v_bip -- IN (SELECT bip FROM accrejbips)
				--	AND state like '%acc%' or '%rej%'
				-- WHICH DATES WE WANT THE DATA
				-- 1. all dates
				-- AND DATE2 < (SELECT DATE2 FROM accrejbips WHERE bip = m.bip)
				-- 2. before and including day state is committed
				AND DATE2 <= v_date2 -- (SELECT DATE2 FROM bipstates_danieldata_datetimestamp WHERE bip = m.bip AND (STATE LIKE 'acc' OR state LIKE '%rej%') order by DATE2 asc LIMIT 1)
				-- 3. only discussion one week before the day state is committed
				--	AND DATE2 BETWEEN 
				--		(SELECT DATE2 FROM bipstates_danieldata_datetimestamp WHERE bip = m.bip AND STATE LIKE 'acc' OR state LIKE '%rej%' order by DATE2 asc LIMIT 1) -7
				--		AND (SELECT DATE2 FROM bipstates_danieldata_datetimestamp WHERE bip = m.bip AND STATE LIKE 'acc' OR state LIKE '%rej%' order by DATE2 asc LIMIT 1)	
				AND folder LIKE '%dev%'
				-- we want to see direct connections at the moment
				AND LENGTH(author) > 0 AND LENGTH(inReplyToUserUsingClusteredSender) > 0 
				-- AND (sendername in (select distinct(sendername) from alldevelopers) -- atleast one member is a developer
				-- OR inReplyToUser in (select distinct(sendername) from alldevelopers))
				-- GROUP BY 
				ORDER BY author;
				-- declare NOT FOUND handler
				DECLARE CONTINUE HANDLER 
        		FOR NOT FOUND SET finished = 1;
	-- INTO OUTFILE 'c:\\scripts\\SNA2020\\SNAAllDevelopersAfterClustering_19-12-2020_h.txt';
OPEN curgetMessages;

getMessages: 
		LOOP
			FETCH curgetMessages INTO vv_bip, v_author, v_inReplyToUserUsingClusteredSender, v_cnt; 
			IF finished = 1 THEN 
				LEAVE getMessages;
			END IF;
			-- build email list
			-- SET emailList = CONCAT(emailAddress,";",emailList);		
			-- SELECT @id := cust_id FROM customers WHERE cust_id='customer name';
			INSERT INTO communicationsForSNA(state, bip, author, inReplyToUserUsingClusteredSender, cnt)
			VALUES (v_state, vv_bip, v_author, v_inReplyToUserUsingClusteredSender, v_cnt);			

		END LOOP getMessages;
		CLOSE curgetMessages;

END//
DELIMITER ;

-- Dumping structure for procedure bips_new.createEmailInvolvementForEachPerson_SeedersReplyers
DELIMITER //
CREATE PROCEDURE `createEmailInvolvementForEachPerson_SeedersReplyers`(IN v_bip INT(10), IN v_state TEXT(15), IN v_date2 DATE, IN v_biptype TEXT (20), IN v_summessages INT(20))
BEGIN
	DECLARE finished INTEGER DEFAULT 0; DECLARE v_cnt INTEGER DEFAULT 0; DECLARE p_state TEXT; DECLARE vv_bip INTEGER;
	DECLARE v_author TEXT(30); DECLARE v_inReplyToUserUsingClusteredSender TEXT(30); DECLARE v_SeederCount INTEGER;
	DECLARE v_totalmsgs INTEGER; DECLARE v_msgs_not_replies INTEGER; DECLARE v_msgs_are_replies INTEGER;
	DECLARE v_author_again TEXT(30); DECLARE v_folder_again TEXT(30); DECLARE v_totalInReplyTo TEXT(30); DECLARE v_folder TEXT(30); 
	DECLARE v_dominance_index FLOAT;
--	SET p_state 
	DECLARE curgetMessages 
			CURSOR FOR 
				SELECT * 
				FROM (SELECT bipnum2020, folder, clusterbysenderfullname, COUNT(messageid) AS totalmsgs, 
				  			SUM(CASE WHEN inReplyTo IS NULL THEN 1 ELSE 0 END) As msg_which_are_not_replies, -- inReplyToUserUsingClusteredSender
				  			SUM(CASE WHEN inReplyTo IS NOT NULL THEN 1 ELSE 0 END) AS msg_which_are_replies
				  			 
						FROM allmessages
						WHERE bipnum2020 = v_bip 
						-- 3 Jan 2021 we consider messages from all lists
						-- AND (folder LIKE '%dev%' OR folder LIKE '%ideas%')
						GROUP BY clusterbysenderfullname, folder
						ORDER BY msg_which_are_not_replies DESC) AS tbA
				-- We join with how many times this particular person has been replied to
				LEFT JOIN (SELECT inReplyToUserUsingClusteredSender, folder, COUNT(inReplyTo) AS totalinReplyTo  -- inReplyTo, 
								FROM allmessages
								WHERE bipnum2020 = v_bip
								-- 3 Jan 2021 we consider messages from all lists
								-- AND (folder LIKE '%dev%' OR folder LIKE '%ideas%')
								GROUP BY inReplyToUserUsingClusteredSender, folder  
								ORDER BY totalinReplyTo desc) as tbC
				ON tbA.clusterbysenderfullname = tbC.inReplyToUserUsingClusteredSender
				AND tbA.folder = tbC.folder;
				-- declare NOT FOUND handler
				DECLARE CONTINUE HANDLER 
        		FOR NOT FOUND SET finished = 1;
	-- INTO OUTFILE 'c:\\scripts\\SNA2020\\SNAAllDevelopersAfterClustering_19-12-2020_h.txt';
OPEN curgetMessages;

getMessages: 
		LOOP
			FETCH curgetMessages INTO vv_bip, v_folder, v_author, v_totalmsgs, v_msgs_not_replies,v_msgs_are_replies, v_author_again, v_folder_again, v_totalInReplyTo; 
			IF finished = 1 THEN 
				LEAVE getMessages;
			END IF;
			-- SELECT LN(v_totalmsgs/v_summessages) * (v_totalmsgs/v_summessages) INTO v_dominance_index;
			-- build email list
			-- SET emailList = CONCAT(emailAddress,";",emailList);		
			-- SELECT @id := cust_id FROM customers WHERE cust_id='customer name';
			INSERT INTO communicationsForSeeders(state, bip, author, totalmsgs, msgs_not_replies,msgs_are_replies, author_again, totalInReplyTo, biptype, summessages,dominance_index, folder)
			VALUES (v_state, vv_bip, v_author, v_totalmsgs, v_msgs_not_replies , v_msgs_are_replies, v_author_again, v_totalInReplyTo, v_biptype, v_summessages, v_dominance_index, v_folder);			

		END LOOP getMessages;
		CLOSE curgetMessages;

END//
DELIMITER ;

-- Dumping structure for procedure bips_new.createEmailInvolvementForEachRole
DELIMITER //
CREATE PROCEDURE `createEmailInvolvementForEachRole`(IN v_bip INT(10), IN v_state TEXT(15), IN v_date2 DATE)
BEGIN
	DECLARE finished INTEGER DEFAULT 0; DECLARE v_cnt INTEGER DEFAULT 0; DECLARE p_state TEXT; DECLARE vv_bip INTEGER;
	DECLARE v_authorsRole TEXT(30); DECLARE v_inReplyToUserUsingClusteredSenderRole TEXT(30);
--	SET p_state 
	DECLARE curgetMessages 
			CURSOR FOR 
				SELECT bip, authorsRole, inReplyToUserUsingClusteredSenderRole, 
				-- COUNT(messageid) AS cnt, -- messageid, author, authorsrole, DATE2, datetimestamp
--				bip
--				, (SELECT state FROM bipstates_danieldata_datetimestamp WHERE bip = m.bip AND STATE LIKE 'acc' OR state LIKE '%rej%' order by DATE2 asc LIMIT 1)
--				author, inReplyToUserUsingClusteredSender, 
				-- (select authorsrole FROM commonroles WHERE author = m.author ),  
--				authorsrole, (select bipCount FROM commonroles WHERE author = m.author ) AS bipinvolvementcount, -- add number of bips the member has contributed in
--				analysewords
				(select bipCount FROM commonroles WHERE author = m.author ) AS cnt 
				FROM allmessages m
				WHERE bip = v_bip -- IN (SELECT bip FROM accrejbips)
				--	AND state like '%acc%' or '%rej%'
				-- WHICH DATES WE WANT THE DATA
				-- 1. all dates
				-- AND DATE2 < (SELECT DATE2 FROM accrejbips WHERE bip = m.bip)
				-- 2. before and including day state is committed
				AND DATE2 <= v_date2 -- (SELECT DATE2 FROM bipstates_danieldata_datetimestamp WHERE bip = m.bip AND (STATE LIKE 'acc' OR state LIKE '%rej%') order by DATE2 asc LIMIT 1)
				-- 3. only discussion one week before the day state is committed
				--	AND DATE2 BETWEEN 
				--		(SELECT DATE2 FROM bipstates_danieldata_datetimestamp WHERE bip = m.bip AND STATE LIKE 'acc' OR state LIKE '%rej%' order by DATE2 asc LIMIT 1) -7
				--		AND (SELECT DATE2 FROM bipstates_danieldata_datetimestamp WHERE bip = m.bip AND STATE LIKE 'acc' OR state LIKE '%rej%' order by DATE2 asc LIMIT 1)	
				AND folder LIKE '%dev%'
				-- we want to see direct connections at the moment
				AND LENGTH(authorsRole) > 0 AND LENGTH(inReplyToUserUsingClusteredSenderRole) > 0 
				-- AND (sendername in (select distinct(sendername) from alldevelopers) -- atleast one member is a developer
				-- OR inReplyToUser in (select distinct(sendername) from alldevelopers))
				-- GROUP BY 
				ORDER BY authorsRole;
				-- declare NOT FOUND handler
				DECLARE CONTINUE HANDLER 
        		FOR NOT FOUND SET finished = 1;
	-- INTO OUTFILE 'c:\\scripts\\SNA2020\\SNAAllDevelopersAfterClustering_19-12-2020_h.txt';
OPEN curgetMessages;

getMessages: 
		LOOP
			FETCH curgetMessages INTO vv_bip, v_authorsRole, v_inReplyToUserUsingClusteredSenderRole, v_cnt; 
			IF finished = 1 THEN 
				LEAVE getMessages;
			END IF;
			-- build email list
			-- SET emailList = CONCAT(emailAddress,";",emailList);		
			-- SELECT @id := cust_id FROM customers WHERE cust_id='customer name';
			INSERT INTO rolecommunicationsForSNA(bip, authorsrole, inReplyToUserUsingClusteredSenderRole, cnt)
			VALUES (vv_bip, v_authorsRole, v_inReplyToUserUsingClusteredSenderRole, v_cnt);			

		END LOOP getMessages;
		CLOSE curgetMessages;

END//
DELIMITER ;

-- Dumping structure for procedure bips_new.createEmailInvolvementForEacMember_BDFLDecidedbips
DELIMITER //
CREATE PROCEDURE `createEmailInvolvementForEacMember_BDFLDecidedbips`(IN v_bip INT(10), IN v_state TEXT(15), IN v_date2 DATE)
BEGIN
	DECLARE finished INTEGER DEFAULT 0; DECLARE v_cnt INTEGER DEFAULT 0; DECLARE p_state TEXT; DECLARE vv_bip INTEGER;
	DECLARE v_author TEXT(30); DECLARE v_inReplyToUserUsingClusteredSender TEXT(30);
--	SET p_state 
	DECLARE curgetMessages 
			CURSOR FOR 
				SELECT bipNum2020, authorsrole, inReplyToUserUsingClusteredSenderrole 
				-- COUNT(messageid) AS cnt, -- messageid, author, authorsrole, DATE2, datetimestamp
--				bip
--				, (SELECT state FROM bipstates_danieldata_datetimestamp WHERE bip = m.bip AND STATE LIKE 'acc' OR state LIKE '%rej%' order by DATE2 asc LIMIT 1)
--				author, inReplyToUserUsingClusteredSender, 
				-- (select authorsrole FROM commonroles WHERE author = m.author ),  
--				authorsrole, (select bipCount FROM commonroles WHERE author = m.author ) AS bipinvolvementcount, -- add number of bips the member has contributed in
--				analysewords

				-- (select bipCount FROM commonroles WHERE author = m.author ) AS cnt 
				FROM allmessages_bdfldelegatedecided2020 m
				WHERE bipnum2020 = v_bip -- IN (SELECT bip FROM accrejbips)
				--	AND state like '%acc%' or '%rej%'
				-- WHICH DATES WE WANT THE DATA
				-- 1. all dates
				-- AND DATE2 < (SELECT DATE2 FROM accrejbips WHERE bip = m.bip)
				-- 2. before and including day state is committed
				-- AND DATE2 <= v_date2 -- (SELECT DATE2 FROM bipstates_danieldata_datetimestamp WHERE bip = m.bip AND (STATE LIKE 'acc' OR state LIKE '%rej%') order by DATE2 asc LIMIT 1)
				-- 3. only discussion one week before the day state is committed
				--	AND DATE2 BETWEEN 
				--		(SELECT DATE2 FROM bipstates_danieldata_datetimestamp WHERE bip = m.bip AND STATE LIKE 'acc' OR state LIKE '%rej%' order by DATE2 asc LIMIT 1) -7
				--		AND (SELECT DATE2 FROM bipstates_danieldata_datetimestamp WHERE bip = m.bip AND STATE LIKE 'acc' OR state LIKE '%rej%' order by DATE2 asc LIMIT 1)	
				-- AND folder LIKE '%dev%'
				-- we want to see direct connections at the moment
				AND LENGTH(authorsrole) > 0 AND LENGTH(inReplyToUserUsingClusteredSenderrole) > 0 
				-- AND (sendername in (select distinct(sendername) from alldevelopers) -- atleast one member is a developer
				-- OR inReplyToUser in (select distinct(sendername) from alldevelopers))
				-- GROUP BY 
				ORDER BY authorsrole;
				-- declare NOT FOUND handler
				DECLARE CONTINUE HANDLER 
        		FOR NOT FOUND SET finished = 1;
	-- INTO OUTFILE 'c:\\scripts\\SNA2020\\SNAAllDevelopersAfterClustering_19-12-2020_h.txt';
OPEN curgetMessages;

getMessages: 
		LOOP
			FETCH curgetMessages INTO vv_bip, v_author, v_inReplyToUserUsingClusteredSender; -- , v_cnt; 
			IF finished = 1 THEN 
				LEAVE getMessages;
			END IF;
			-- build email list
			-- SET emailList = CONCAT(emailAddress,";",emailList);		
			-- SELECT @id := cust_id FROM customers WHERE cust_id='customer name';
			INSERT INTO rolecommunicationsForSNA(bip, authorsrole, inReplyToUserUsingClusteredSenderRole) -- , cnt)
			VALUES (vv_bip, v_author, v_inReplyToUserUsingClusteredSender); -- , v_cnt);			

		END LOOP getMessages;
		CLOSE curgetMessages;

END//
DELIMITER ;

-- Dumping structure for procedure bips_new.createEmailInvolvementForEacMember_Controversialbips
DELIMITER //
CREATE PROCEDURE `createEmailInvolvementForEacMember_Controversialbips`(IN v_bip INT(10), IN v_state TEXT(15), IN v_date2 DATE)
BEGIN
	DECLARE finished INTEGER DEFAULT 0; DECLARE v_cnt INTEGER DEFAULT 0; DECLARE p_state TEXT; DECLARE vv_bip INTEGER;
	DECLARE v_author TEXT(30); DECLARE v_inReplyToUserUsingClusteredSender TEXT(30);
--	SET p_state 
	DECLARE curgetMessages 
			CURSOR FOR 
				SELECT bipNum2020, clusterBySenderFullName, inReplyToUserUsingClusteredSender 
				-- COUNT(messageid) AS cnt, -- messageid, author, authorsrole, DATE2, datetimestamp
--				bip
--				, (SELECT state FROM bipstates_danieldata_datetimestamp WHERE bip = m.bip AND STATE LIKE 'acc' OR state LIKE '%rej%' order by DATE2 asc LIMIT 1)
--				author, inReplyToUserUsingClusteredSender, 
				-- (select authorsrole FROM commonroles WHERE author = m.author ),  
--				authorsrole, (select bipCount FROM commonroles WHERE author = m.author ) AS bipinvolvementcount, -- add number of bips the member has contributed in
--				analysewords

				-- (select bipCount FROM commonroles WHERE author = m.author ) AS cnt 
				FROM allmessages m
				WHERE bipnum2020 = v_bip -- IN (SELECT bip FROM accrejbips)
				--	AND state like '%acc%' or '%rej%'
				-- WHICH DATES WE WANT THE DATA
				-- 1. all dates
				-- AND DATE2 < (SELECT DATE2 FROM accrejbips WHERE bip = m.bip)
				-- 2. before and including day state is committed
				-- AND DATE2 <= v_date2 -- (SELECT DATE2 FROM bipstates_danieldata_datetimestamp WHERE bip = m.bip AND (STATE LIKE 'acc' OR state LIKE '%rej%') order by DATE2 asc LIMIT 1)
				-- 3. only discussion one week before the day state is committed
				--	AND DATE2 BETWEEN 
				--		(SELECT DATE2 FROM bipstates_danieldata_datetimestamp WHERE bip = m.bip AND STATE LIKE 'acc' OR state LIKE '%rej%' order by DATE2 asc LIMIT 1) -7
				--		AND (SELECT DATE2 FROM bipstates_danieldata_datetimestamp WHERE bip = m.bip AND STATE LIKE 'acc' OR state LIKE '%rej%' order by DATE2 asc LIMIT 1)	
				-- AND folder LIKE '%dev%'
				-- we want to see direct connections at the moment
				AND LENGTH(clusterBySenderFullName) > 0 AND LENGTH(inReplyToUserUsingClusteredSender) > 0 
				-- AND (sendername in (select distinct(sendername) from alldevelopers) -- atleast one member is a developer
				-- OR inReplyToUser in (select distinct(sendername) from alldevelopers))
				-- GROUP BY 
				ORDER BY clusterBySenderFullName;
				-- declare NOT FOUND handler
				DECLARE CONTINUE HANDLER 
        		FOR NOT FOUND SET finished = 1;
	-- INTO OUTFILE 'c:\\scripts\\SNA2020\\SNAAllDevelopersAfterClustering_19-12-2020_h.txt';
OPEN curgetMessages;

getMessages: 
		LOOP
			FETCH curgetMessages INTO vv_bip, v_author, v_inReplyToUserUsingClusteredSender; -- , v_cnt; 
			IF finished = 1 THEN 
				LEAVE getMessages;
			END IF;
			-- build email list
			-- SET emailList = CONCAT(emailAddress,";",emailList);		
			-- SELECT @id := cust_id FROM customers WHERE cust_id='customer name';
			INSERT INTO communicationsForSNA(bip, author, inReplyToUserUsingClusteredSender) -- , cnt)
			VALUES (vv_bip, v_author, v_inReplyToUserUsingClusteredSender); -- , v_cnt);			

		END LOOP getMessages;
		CLOSE curgetMessages;

END//
DELIMITER ;

-- Dumping structure for procedure bips_new.createEmailInvolvementForProposalAuthorAndBDFLD_SeedersReplyers
DELIMITER //
CREATE PROCEDURE `createEmailInvolvementForProposalAuthorAndBDFLD_SeedersReplyers`(IN v_bip INT(10), IN v_state TEXT(15), IN v_date2 DATE, IN v_biptype TEXT (20), IN v_summessages INT(20))
BEGIN
	DECLARE finished INTEGER DEFAULT 0; DECLARE v_cnt INTEGER DEFAULT 0; DECLARE p_state TEXT; DECLARE vv_bip INTEGER;
	DECLARE v_author TEXT(30); DECLARE v_inReplyToUserUsingClusteredSender TEXT(30); DECLARE v_SeederCount INTEGER;
	DECLARE v_totalmsgs INTEGER; DECLARE v_msgs_not_replies INTEGER; DECLARE v_msgs_are_replies INTEGER;
	DECLARE v_author_again TEXT(30); DECLARE v_totalInReplyTo TEXT(30); DECLARE v_folder TEXT(30); DECLARE v_folder_again TEXT(30);
	DECLARE v_dominance_index FLOAT;
--	SET p_state 
	DECLARE curgetMessages 
			CURSOR FOR 
				SELECT * 					-- clusterbysenderfullname,
				FROM (SELECT bipnum2020, folder, authorsrole, COUNT(messageid) AS totalmsgs, 
				  			SUM(CASE WHEN inReplyTo IS NULL THEN 1 ELSE 0 END) As msg_which_are_not_replies, -- inReplyToUserUsingClusteredSender
				  			SUM(CASE WHEN inReplyTo IS NOT NULL THEN 1 ELSE 0 END) AS msg_which_are_replies
						FROM allmessages
						WHERE bipnum2020 = v_bip 
						-- 4 Jan 2021 we consider messages from all lists
						-- we add the proposalauthor
						-- GROUP BY clusterbysenderfullname
						AND (authorsrole = 'proposalauthor' OR authorsrole = 'bdfl_delegate')
						ORDER BY msg_which_are_not_replies DESC) AS tbA
				-- We join with how many times this particular person has been replied to
										-- inReplyToUserUsingClusteredSender
				LEFT JOIN (SELECT authorsrole, folder, COUNT(inReplyTo) AS totalinReplyTo  -- inReplyTo, 
								FROM allmessages
								WHERE bipnum2020 = v_bip
								-- 3 Jan 2021 we consider messages from all lists
								-- AND (folder LIKE '%dev%' OR folder LIKE '%ideas%')
								GROUP BY inReplyToUserUsingClusteredSenderRole, folder  
								AND (authorsrole = 'proposalauthor' OR authorsrole = 'bdfl_delegate')
								ORDER BY totalinReplyTo desc) as tbC
				-- ON tbA.clusterbysenderfullname = tbC.inReplyToUserUsingClusteredSender;
				ON tbA.authorsrole = tbC.authorsrole
				AND tbA.folder = tbC.folder;
				-- declare NOT FOUND handler
				DECLARE CONTINUE HANDLER 
        		FOR NOT FOUND SET finished = 1;
	-- INTO OUTFILE 'c:\\scripts\\SNA2020\\SNAAllDevelopersAfterClustering_19-12-2020_h.txt';
OPEN curgetMessages;

getMessages: 
		LOOP
			FETCH curgetMessages INTO vv_bip, v_folder, v_author, v_totalmsgs, v_msgs_not_replies,v_msgs_are_replies, v_author_again, v_folder_again, v_totalInReplyTo; 
			IF finished = 1 THEN 
				LEAVE getMessages;
			END IF;
			-- SELECT LN(v_totalmsgs/v_summessages) * (v_totalmsgs/v_summessages) INTO v_dominance_index;
			-- build email list
			-- SET emailList = CONCAT(emailAddress,";",emailList);		
			-- SELECT @id := cust_id FROM customers WHERE cust_id='customer name';
			INSERT INTO communicationsForSeeders(state, bip, author, totalmsgs, msgs_not_replies,msgs_are_replies, author_again, totalInReplyTo, biptype, summessages,folder)
			VALUES (v_state, vv_bip, v_author, v_totalmsgs, v_msgs_not_replies , v_msgs_are_replies, v_author_again, v_totalInReplyTo, v_biptype, v_summessages, v_folder);			

		END LOOP getMessages;
		CLOSE curgetMessages;

END//
DELIMITER ;

-- Dumping structure for procedure bips_new.createEmailInvolvementForProposalAuthor_SeedersReplyers
DELIMITER //
CREATE PROCEDURE `createEmailInvolvementForProposalAuthor_SeedersReplyers`(IN v_bip INT(10), IN v_state TEXT(15), IN v_date2 DATE, IN v_biptype TEXT (20), IN v_summessages INT(20))
BEGIN
	DECLARE finished INTEGER DEFAULT 0; DECLARE v_cnt INTEGER DEFAULT 0; DECLARE p_state TEXT; DECLARE vv_bip INTEGER;
	DECLARE v_author TEXT(30); DECLARE v_inReplyToUserUsingClusteredSender TEXT(30); DECLARE v_SeederCount INTEGER;
	DECLARE v_totalmsgs INTEGER; DECLARE v_msgs_not_replies INTEGER; DECLARE v_msgs_are_replies INTEGER;
	DECLARE v_author_again TEXT(30); DECLARE v_totalInReplyTo TEXT(30); 
	DECLARE v_dominance_index FLOAT;
--	SET p_state 
	DECLARE curgetMessages 
			CURSOR FOR 
				SELECT * 					-- clusterbysenderfullname,
				FROM (SELECT bipnum2020, authorsrole, COUNT(messageid) AS totalmsgs, 
				  			SUM(CASE WHEN inReplyTo IS NULL THEN 1 ELSE 0 END) As msg_which_are_not_replies, -- inReplyToUserUsingClusteredSender
				  			SUM(CASE WHEN inReplyTo IS NOT NULL THEN 1 ELSE 0 END) AS msg_which_are_replies
						FROM allmessages
						WHERE bipnum2020 = v_bip 
						-- 4 Jan 2021 we consider messages from all lists
						-- we add the proposalauthor
						-- GROUP BY clusterbysenderfullname
						AND authorsrole = 'proposalauthor'
						ORDER BY msg_which_are_not_replies DESC) AS tbA
				-- We join with how many times this particular person has been replied to
										-- inReplyToUserUsingClusteredSender
				LEFT JOIN (SELECT authorsrole, COUNT(inReplyTo) AS totalinReplyTo  -- inReplyTo, 
								FROM allmessages
								WHERE bipnum2020 = v_bip
								-- 3 Jan 2021 we consider messages from all lists
								-- AND (folder LIKE '%dev%' OR folder LIKE '%ideas%')
								-- GROUP BY inReplyToUserUsingClusteredSender  
								AND authorsrole = 'proposalauthor'
								ORDER BY totalinReplyTo desc) as tbC
				-- ON tbA.clusterbysenderfullname = tbC.inReplyToUserUsingClusteredSender;
				ON tbA.authorsrole = tbC.authorsrole;
				-- declare NOT FOUND handler
				DECLARE CONTINUE HANDLER 
        		FOR NOT FOUND SET finished = 1;
	-- INTO OUTFILE 'c:\\scripts\\SNA2020\\SNAAllDevelopersAfterClustering_19-12-2020_h.txt';
OPEN curgetMessages;

getMessages: 
		LOOP
			FETCH curgetMessages INTO vv_bip, v_author, v_totalmsgs, v_msgs_not_replies,v_msgs_are_replies, v_author_again, v_totalInReplyTo; 
			IF finished = 1 THEN 
				LEAVE getMessages;
			END IF;
			SELECT LN(v_totalmsgs/v_summessages) * (v_totalmsgs/v_summessages) INTO v_dominance_index;
			-- build email list
			-- SET emailList = CONCAT(emailAddress,";",emailList);		
			-- SELECT @id := cust_id FROM customers WHERE cust_id='customer name';
			INSERT INTO communicationsForSeeders(state, bip, author, totalmsgs, msgs_not_replies,msgs_are_replies, author_again, totalInReplyTo, biptype, summessages,dominance_index)
			VALUES (v_state, vv_bip, v_author, v_totalmsgs, v_msgs_not_replies , v_msgs_are_replies, v_author_again, v_totalInReplyTo, v_biptype, v_summessages, v_dominance_index);			

		END LOOP getMessages;
		CLOSE curgetMessages;

END//
DELIMITER ;

-- Dumping structure for procedure bips_new.createSentenceList
DELIMITER //
CREATE PROCEDURE `createSentenceList`(IN v_bip INT(10), IN v_state TEXT(15), IN v_date2 DATE)
BEGIN
	DECLARE finished INTEGER DEFAULT 0;
	DECLARE v_messageId INTEGER DEFAULT 0;
-- "," , "," , 
	DEClARE curgetMessageIds 
			CURSOR FOR 
				SELECT messageid
--				bip
--				, (SELECT state FROM bipstates_danieldata_datetimestamp WHERE bip = m.bip AND STATE LIKE 'acc' OR state LIKE '%rej%' order by DATE2 asc LIMIT 1)
--				author, inReplyToUserUsingClusteredSender, 
				-- (select authorsrole FROM commonroles WHERE author = m.author ),  
--				authorsrole, (select bipCount FROM commonroles WHERE author = m.author ) AS bipinvolvementcount, -- add number of bips the member has contributed in
--				analysewords
				from allmessages m
				WHERE bip = v_bip -- IN (SELECT bip FROM accrejbips)
				--	AND state like '%acc%' or '%rej%'
				-- WHICH DATES WE WANT THE DATA
				-- 1. all dates
				-- AND DATE2 < (SELECT DATE2 FROM accrejbips WHERE bip = m.bip)
				-- 2. before and including day state is committed
				AND DATE2 <= (SELECT DATE2 FROM bipstates_danieldata_datetimestamp WHERE bip = m.bip AND (STATE LIKE 'acc' OR state LIKE '%rej%') order by DATE2 asc LIMIT 1)
				-- 3. only discussion one week before the day state is committed
				--	AND DATE2 BETWEEN 
				--		(SELECT DATE2 FROM bipstates_danieldata_datetimestamp WHERE bip = m.bip AND STATE LIKE 'acc' OR state LIKE '%rej%' order by DATE2 asc LIMIT 1) -7
				--		AND (SELECT DATE2 FROM bipstates_danieldata_datetimestamp WHERE bip = m.bip AND STATE LIKE 'acc' OR state LIKE '%rej%' order by DATE2 asc LIMIT 1)	
				AND folder LIKE '%dev%'
				-- we want to see direct connections at the moment
				AND LENGTH(sendername) > 0 
				AND LENGTH(inReplyToUser) > 0 
				-- AND (sendername in (select distinct(sendername) from alldevelopers) -- atleast one member is a developer
				-- OR inReplyToUser in (select distinct(sendername) from alldevelopers))
				order by sendername;
				-- declare NOT FOUND handler
				DECLARE CONTINUE HANDLER 
        		FOR NOT FOUND SET finished = 1;
	-- INTO OUTFILE 'c:\\scripts\\SNA2020\\SNAAllDevelopersAfterClustering_19-12-2020_h.txt';

OPEN curgetMessageIds;

getMessageIds: 
		LOOP
			FETCH curgetMessageIds INTO v_messageId;
			IF finished = 1 THEN 
				LEAVE getMessageIds;
			END IF;
			-- build email list
			-- SET emailList = CONCAT(emailAddress,";",emailList);
			INSERT INTO sentimentsentences (bip,  messageid, sentence) -- state,
			SELECT proposal,  messageid, sentence -- ,msgAuthorRole, isEnglishOrCode,islastparagraph, isfirstparagraph, msgSubject 
			from allsentences 
			WHERE messageid = v_messageId 
			and proposal = v_bip and isEnglishOrCode = 1 
			and (islastparagraph =1 or isfirstparagraph =1) 
			order by proposal ASC;
		END LOOP getMessageIds;
		CLOSE curgetMessageIds;

END//
DELIMITER ;

-- Dumping structure for procedure bips_new.createThreadInvolvementForEachPerson
DELIMITER //
CREATE PROCEDURE `createThreadInvolvementForEachPerson`(IN v_bipnum2020 INT(10), IN v_messageid INT(10), IN v_author TEXT(15), 
																		 IN v_clusterbysenderfullname TEXT(20), IN v_emailmessageid TEXT (20), IN v_subject TEXT (20), IN v_threadid INT(10),
																		 IN v_authorsrole2020 TEXT(20), IN v_biptype2020 TEXT(20), IN v_threadLevel INT(10))
BEGIN
	DECLARE finished INTEGER DEFAULT 0; 
	DECLARE vv_bipnum2020 INTEGER DEFAULT 0; DECLARE vv_messageId INTEGER DEFAULT 0; DECLARE vv_author TEXT(15); DECLARE vv_date DATE;
	DECLARE vv_clusterbysenderfullname TEXT(15); DECLARE vv_emailmessageid TEXT(15); DECLARE vv_subject TEXT(15);
	DECLARE vv_inreplyto TEXT(15); DECLARE vv_authorsrole2020 TEXT(15);
--	SET p_state 
	DECLARE curgetMessages 
			CURSOR FOR -- DISTINCT
				SELECT bipnum2020, messageid, author, clusterbysenderfullname, emailmessageid, subject, inreplyto, authorsrole2020  -- , authorsrole2020 -- ,  COUNT(messageid) AS cnt
					FROM allmessages 
					WHERE (inreplyto = v_emailmessageid )
					-- this would be too much noise in the data
			--		OR subject = v_subject ) -- we first look at threads top level
					AND bipnum2020 = v_bipnum2020; -- IN (SELECT DISTINCT bip FROM allbipstillBDFL ); -- WHERE bip <> -1AND bip = 227);
				-- declare NOT FOUND handler
				DECLARE CONTINUE HANDLER 
        		FOR NOT FOUND SET finished = 1;
	-- INTO OUTFILE 'c:\\scripts\\SNA2020\\SNAAllDevelopersAfterClustering_19-12-2020_h.txt';
OPEN curgetMessages;

getMessages: 
		LOOP
			FETCH curgetMessages INTO vv_bipnum2020,vv_messageid, vv_author, vv_clusterbysenderfullname,  vv_emailmessageid, vv_subject, vv_inreplyto, vv_authorsrole2020 ; 
			IF finished = 1 THEN 
				LEAVE getMessages;
			END IF;
			-- INSERT INTO SAME TABLE 
			INSERT INTO bipthreadsbyperson2020(threadlevel, bipnum2020,messageid, author, clusterbysenderfullname,  emailmessageid, subject, threadid, inreplyto, authorsrole2020, biptype2020 )
				VALUES (v_threadLevel, vv_bipnum2020,vv_messageid, vv_author, vv_clusterbysenderfullname, vv_emailmessageid, vv_subject, v_threadid, vv_inreplyto, vv_authorsrole2020, v_biptype2020 );			
			
			
		END LOOP getMessages;
		CLOSE curgetMessages;

END//
DELIMITER ;

-- Dumping structure for procedure bips_new.createThreadInvolvementForEachPerson_from_secondlevel
DELIMITER //
CREATE PROCEDURE `createThreadInvolvementForEachPerson_from_secondlevel`(IN v_bipnum2020 INT(10), IN v_messageid INT(10), IN v_author TEXT(15), 
																		 IN v_clusterbysenderfullname TEXT(20), IN v_emailmessageid TEXT (20), IN v_subject TEXT (20), IN v_threadid INT(10),
																		 IN v_authorsrole2020 TEXT(20), IN v_biptype2020 TEXT(20), IN v_threadLevel INT(10))
BEGIN
	DECLARE finished INTEGER DEFAULT 0; 
	DECLARE vv_bipnum2020 INTEGER DEFAULT 0; DECLARE vv_messageId INTEGER DEFAULT 0; DECLARE vv_author TEXT(15); DECLARE vv_date DATE;
	DECLARE vv_clusterbysenderfullname TEXT(15); DECLARE vv_emailmessageid TEXT(15); DECLARE vv_subject TEXT(15);
	DECLARE vv_inreplyto TEXT(15); DECLARE vv_authorsrole2020 TEXT(15);
--	SET p_state 
	DECLARE curgetMessages 
			CURSOR FOR -- DISTINCT
				SELECT bipnum2020, messageid, author, clusterbysenderfullname, emailmessageid, subject, inreplyto, authorsrole2020  -- , authorsrole2020 -- ,  COUNT(messageid) AS cnt
					FROM allmessages 
					WHERE (inreplyto = v_emailmessageid )
					-- this would be too much noise in the data
			--		OR subject = v_subject ) -- we first look at threads top level
					AND bipnum2020 = v_bipnum2020; -- IN (SELECT DISTINCT bip FROM allbipstillBDFL ); -- WHERE bip <> -1AND bip = 227);
				-- declare NOT FOUND handler
				DECLARE CONTINUE HANDLER 
        		FOR NOT FOUND SET finished = 1;
	-- INTO OUTFILE 'c:\\scripts\\SNA2020\\SNAAllDevelopersAfterClustering_19-12-2020_h.txt';
OPEN curgetMessages;

getMessages: 
		LOOP
			FETCH curgetMessages INTO vv_bipnum2020,vv_messageid, vv_author, vv_clusterbysenderfullname,  vv_emailmessageid, vv_subject, vv_inreplyto, vv_authorsrole2020 ; 
			IF finished = 1 THEN 
				LEAVE getMessages;
			END IF;
			-- INSERT INTO SAME TABLE 
			INSERT INTO bipthreadsbyperson2020(threadlevel, bipnum2020,messageid, author, clusterbysenderfullname,  emailmessageid, subject, threadid, inreplyto, authorsrole2020, biptype2020 )
				VALUES (v_threadLevel, vv_bipnum2020,vv_messageid, vv_author, vv_clusterbysenderfullname, vv_emailmessageid, vv_subject, v_threadid, vv_inreplyto, vv_authorsrole2020, v_biptype2020 );			
			-- the the intyegral part ...now it will go on in recursion
			-- call getThreads_Per_Message_recursive_from_secondlevel(v_threadLevel);
		END LOOP getMessages;
		CLOSE curgetMessages;

END//
DELIMITER ;

-- Dumping structure for procedure bips_new.createThreadInvolvementForEachPerson_recursive_from_2ndlevel
DELIMITER //
CREATE PROCEDURE `createThreadInvolvementForEachPerson_recursive_from_2ndlevel`(IN v_bipnum2020 INT(10), IN v_messageid INT(10), IN v_author TEXT(15), 
																		 IN v_clusterbysenderfullname TEXT(20), IN v_emailmessageid TEXT (20), IN v_subject TEXT (20), IN v_threadid INT(10),
																		 IN v_authorsrole2020 TEXT(20), IN v_biptype2020 TEXT(20), IN v_threadLevel INT(10))
BEGIN
	DECLARE finished INTEGER DEFAULT 0; 
	DECLARE vv_bipnum2020 INTEGER DEFAULT 0; DECLARE vv_messageId INTEGER DEFAULT 0; DECLARE vv_author TEXT(15); DECLARE vv_date DATE;
	DECLARE vv_clusterbysenderfullname TEXT(15); DECLARE vv_emailmessageid TEXT(15); DECLARE vv_subject TEXT(15);
	DECLARE vv_inreplyto TEXT(15); DECLARE vv_authorsrole2020 TEXT(15);
--	SET p_state 
	DECLARE curgetMessages 
			CURSOR FOR -- DISTINCT
				SELECT bipnum2020, messageid, author, clusterbysenderfullname, emailmessageid, subject, inreplyto, authorsrole2020  -- , authorsrole2020 -- ,  COUNT(messageid) AS cnt
					FROM allmessages 
					WHERE (inreplyto = v_emailmessageid )
					-- this would be too much noise in the data
			--		OR subject = v_subject ) -- we first look at threads top level
					AND bipnum2020 = v_bipnum2020; -- IN (SELECT DISTINCT bip FROM allbipstillBDFL ); -- WHERE bip <> -1AND bip = 227);
				-- declare NOT FOUND handler
				DECLARE CONTINUE HANDLER 
        		FOR NOT FOUND SET finished = 1;
	-- INTO OUTFILE 'c:\\scripts\\SNA2020\\SNAAllDevelopersAfterClustering_19-12-2020_h.txt';
OPEN curgetMessages;

getMessages: 
		LOOP
			FETCH curgetMessages INTO vv_bipnum2020,vv_messageid, vv_author, vv_clusterbysenderfullname,  vv_emailmessageid, vv_subject, vv_inreplyto, vv_authorsrole2020 ; 
			IF finished = 1 THEN 
				LEAVE getMessages;
			END IF;
			-- INSERT INTO SAME TABLE 
			INSERT INTO bipthreadsbyperson2020(threadlevel, bipnum2020,messageid, author, clusterbysenderfullname,  emailmessageid, subject, threadid, inreplyto, authorsrole2020, biptype2020 )
				VALUES (v_threadLevel, vv_bipnum2020,vv_messageid, vv_author, vv_clusterbysenderfullname, vv_emailmessageid, vv_subject, v_threadid, vv_inreplyto, vv_authorsrole2020, v_biptype2020 );			
			-- the the intyegral part ...now it will go on in recursion
			call getThreads_Per_Message_recursive_from_2ndlevel(v_threadLevel);
		END LOOP getMessages;
		CLOSE curgetMessages;

END//
DELIMITER ;

-- Dumping structure for procedure bips_new.createThreadInvolvementForEachPerson_recursive_from_secondlevel
DELIMITER //
CREATE PROCEDURE `createThreadInvolvementForEachPerson_recursive_from_secondlevel`(IN v_bipnum2020 INT(10), IN v_messageid INT(10), IN v_author TEXT(15), 
																		 IN v_clusterbysenderfullname TEXT(20), IN v_emailmessageid TEXT (20), IN v_subject TEXT (20), IN v_threadid INT(10),
																		 IN v_authorsrole2020 TEXT(20), IN v_biptype2020 TEXT(20), IN v_threadLevel INT(10))
BEGIN
	DECLARE finished INTEGER DEFAULT 0; 
	DECLARE vv_bipnum2020 INTEGER DEFAULT 0; DECLARE vv_messageId INTEGER DEFAULT 0; DECLARE vv_author TEXT(15); DECLARE vv_date DATE;
	DECLARE vv_clusterbysenderfullname TEXT(15); DECLARE vv_emailmessageid TEXT(15); DECLARE vv_subject TEXT(15);
	DECLARE vv_inreplyto TEXT(15); DECLARE vv_authorsrole2020 TEXT(15);
--	SET p_state 
	DECLARE curgetMessages 
			CURSOR FOR -- DISTINCT
				SELECT bipnum2020, messageid, author, clusterbysenderfullname, emailmessageid, subject, inreplyto, authorsrole2020  -- , authorsrole2020 -- ,  COUNT(messageid) AS cnt
					FROM allmessages 
					WHERE (inreplyto = v_emailmessageid )
					-- this would be too much noise in the data
			--		OR subject = v_subject ) -- we first look at threads top level
					AND bipnum2020 = v_bipnum2020; -- IN (SELECT DISTINCT bip FROM allbipstillBDFL ); -- WHERE bip <> -1AND bip = 227);
				-- declare NOT FOUND handler
				DECLARE CONTINUE HANDLER 
        		FOR NOT FOUND SET finished = 1;
	-- INTO OUTFILE 'c:\\scripts\\SNA2020\\SNAAllDevelopersAfterClustering_19-12-2020_h.txt';
OPEN curgetMessages;

getMessages: 
		LOOP
			FETCH curgetMessages INTO vv_bipnum2020,vv_messageid, vv_author, vv_clusterbysenderfullname,  vv_emailmessageid, vv_subject, vv_inreplyto, vv_authorsrole2020 ; 
			IF finished = 1 THEN 
				LEAVE getMessages;
			END IF;
			-- INSERT INTO SAME TABLE 
			INSERT INTO bipthreadsbyperson2020(threadlevel, bipnum2020,messageid, author, clusterbysenderfullname,  emailmessageid, subject, threadid, inreplyto, authorsrole2020, biptype2020 )
				VALUES (v_threadLevel, vv_bipnum2020,vv_messageid, vv_author, vv_clusterbysenderfullname, vv_emailmessageid, vv_subject, v_threadid, vv_inreplyto, vv_authorsrole2020, v_biptype2020 );			
			-- the the intyegral part ...now it will go on in recursion
			-- call getThreads_Per_Message_recursive_from_secondlevel(v_threadLevel);
		END LOOP getMessages;
		CLOSE curgetMessages;

END//
DELIMITER ;

-- Dumping structure for procedure bips_new.for_loop_example
DELIMITER //
CREATE PROCEDURE `for_loop_example`()
wholeblock:BEGIN
  DECLARE x INT;
  DECLARE str VARCHAR(255);
  SET x = -5;
  SET str = '';
  loop_label: LOOP
    IF x > 0 THEN
      LEAVE loop_label;
    END IF;
    SET str = CONCAT(str,x,',');
    SET x = x + 1;
    ITERATE loop_label;
  END LOOP;
  SELECT str;
END//
DELIMITER ;

-- Dumping structure for procedure bips_new.getbipList
DELIMITER //
CREATE PROCEDURE `getbipList`()
BEGIN
	DECLARE finished INTEGER DEFAULT 0; DECLARE v_bip INTEGER DEFAULT 0; DECLARE v_messageId INTEGER DEFAULT 0; DECLARE v_state TEXT(15); DECLARE v_date DATE;
	DECLARE curbipList 
			CURSOR FOR 
				SELECT DISTINCT bip, state, DATE2 FROM bipstates_danieldata_datetimestamp WHERE (STATE LIKE '%acc%' OR state LIKE '%rej%') order by DATE2 asc;
				DECLARE CONTINUE HANDLER 
        		FOR NOT FOUND SET finished = 1;
OPEN curbipList;

getbipsstate: 
		LOOP
			FETCH curbipList INTO v_bip, v_state, v_date;
			IF finished = 1 THEN 
				LEAVE getbipsstate;
			END IF;
			call createSentenceListForEachbipState (v_bip,v_state, v_date);
		END LOOP getbipsstate;
		CLOSE curbipList;

END//
DELIMITER ;

-- Dumping structure for procedure bips_new.getbipListForEachMember_bipTypeDMSuphase
DELIMITER //
CREATE PROCEDURE `getbipListForEachMember_bipTypeDMSuphase`()
BEGIN
	DECLARE finished INTEGER DEFAULT 0; DECLARE v_bip INTEGER DEFAULT 0; DECLARE v_messageId INTEGER DEFAULT 0; 
	DECLARE v_state TEXT(15); DECLARE v_date DATE; DECLARE v_biptype TEXT(15); 
	DECLARE curbipList 
			CURSOR FOR 				
				SELECT bip, state, DATE2, biptype 
					FROM (SELECT DISTINCT bip, state, DATE2, biptype						
								FROM bipstates_danieldata_datetimestamp  
								WHERE (STATE LIKE '%acc%' OR state LIKE '%rej%') 
								-- AND bip IN (SELECT bip FROM bipdetails WHERE LENGTH (bdfl_delegatecorrected) > 0 ) 
								order by DATE2 ASC)
						AS tbA
					LEFT JOIN ( SELECT bip AS P2, author, bdfl_delegatecorrected FROM bipdetails ) as tbC
						ON tbA.bip = tbC.p2
					ORDER BY tbA.bip;				 
				-- for those bips decided by bdfl delegate
				-- bip IN (SELECT DISTINCT bip FROM bipdetails WHERE LENGTH (bdfl_delegatecorrected) > 0 )
				-- order by DATE2 asc;				
				DECLARE CONTINUE HANDLER 
        		FOR NOT FOUND SET finished = 1;
OPEN curbipList;

getbipsstate: 
		LOOP
			FETCH curbipList INTO v_bip, v_state, v_date, v_biptype;
			IF finished = 1 THEN 
				LEAVE getbipsstate;
			END IF;
			call createEmailInvolvementForEachMember_bipTypeDMSuphase(v_bip,v_state, v_date,v_biptype);
		END LOOP getbipsstate;
		CLOSE curbipList;

END//
DELIMITER ;

-- Dumping structure for procedure bips_new.getbipList_BDFLDecidedbips
DELIMITER //
CREATE PROCEDURE `getbipList_BDFLDecidedbips`()
BEGIN
	DECLARE finished INTEGER DEFAULT 0; DECLARE v_bip INTEGER DEFAULT 0; DECLARE v_messageId INTEGER DEFAULT 0; DECLARE v_state TEXT(15); DECLARE v_date DATE;
	DECLARE curbipList 
			CURSOR FOR 
				SELECT DISTINCT bip, state, DATE2 FROM bipstates_danieldata_datetimestamp 
				WHERE -- (STATE LIKE '%acc%' OR state LIKE '%rej%') 
				-- for those bips decided by bdfl delegate
				bip IN (SELECT DISTINCT bip FROM bipdetails WHERE LENGTH (bdfl_delegatecorrected) > 0 )
				order by DATE2 asc;				
				DECLARE CONTINUE HANDLER 
        		FOR NOT FOUND SET finished = 1;
OPEN curbipList;

getbipsstate: 
		LOOP
			FETCH curbipList INTO v_bip, v_state, v_date;
			IF finished = 1 THEN 
				LEAVE getbipsstate;
			END IF;
			call createEmailInvolvementForEacMember_BDFLDecidedbips(v_bip,v_state, v_date);
		END LOOP getbipsstate;
		CLOSE curbipList;

END//
DELIMITER ;

-- Dumping structure for procedure bips_new.getbipList_Controversialbips
DELIMITER //
CREATE PROCEDURE `getbipList_Controversialbips`()
BEGIN
	DECLARE finished INTEGER DEFAULT 0; DECLARE v_bip INTEGER DEFAULT 0; DECLARE v_messageId INTEGER DEFAULT 0; DECLARE v_state TEXT(15); DECLARE v_date DATE;
	DECLARE curbipList 
			CURSOR FOR 
				SELECT DISTINCT bip, state, DATE2 FROM bipstates_danieldata_datetimestamp 
				WHERE -- (STATE LIKE '%acc%' OR state LIKE '%rej%') 
				-- bip IN (204, 240, 256, 216, 284, 308, 313, 348, 3128, 3108)
				-- for those bips decided by bdfl delegate
				bip IN (SELECT DISTINCT bip FROM bipdetails WHERE LENGTH (bdfl_delegatecorrected) > 0 )
				order by DATE2 asc;				
				DECLARE CONTINUE HANDLER 
        		FOR NOT FOUND SET finished = 1;
OPEN curbipList;

getbipsstate: 
		LOOP
			FETCH curbipList INTO v_bip, v_state, v_date;
			IF finished = 1 THEN 
				LEAVE getbipsstate;
			END IF;
			call createEmailInvolvementForEacMember_Controversialbips(v_bip,v_state, v_date);
		END LOOP getbipsstate;
		CLOSE curbipList;

END//
DELIMITER ;

-- Dumping structure for procedure bips_new.getbipList_Person
DELIMITER //
CREATE PROCEDURE `getbipList_Person`()
BEGIN
	DECLARE finished INTEGER DEFAULT 0; DECLARE v_bip INTEGER DEFAULT 0; DECLARE v_messageId INTEGER DEFAULT 0; DECLARE v_state TEXT(15); DECLARE v_date DATE;
	DECLARE curbipList 
			CURSOR FOR 
				SELECT DISTINCT bip, state, DATE2 FROM bipstates_danieldata_datetimestamp WHERE (STATE LIKE '%acc%' OR state LIKE '%rej%') order by DATE2 asc;
				DECLARE CONTINUE HANDLER 
        		FOR NOT FOUND SET finished = 1;
OPEN curbipList;

getbipsstate: 
		LOOP
			FETCH curbipList INTO v_bip, v_state, v_date;
			IF finished = 1 THEN 
				LEAVE getbipsstate;
			END IF;
			call createEmailInvolvementForEachPerson (v_bip,v_state, v_date);
		END LOOP getbipsstate;
		CLOSE curbipList;

END//
DELIMITER ;

-- Dumping structure for procedure bips_new.getbipList_Roles
DELIMITER //
CREATE PROCEDURE `getbipList_Roles`()
BEGIN
	DECLARE finished INTEGER DEFAULT 0; DECLARE v_bip INTEGER DEFAULT 0; DECLARE v_messageId INTEGER DEFAULT 0; DECLARE v_state TEXT(15); DECLARE v_date DATE;
	DECLARE curbipList 
			CURSOR FOR 
				SELECT DISTINCT bip, state, DATE2 FROM bipstates_danieldata_datetimestamp WHERE (STATE LIKE '%acc%' OR state LIKE '%rej%') order by DATE2 asc;
				DECLARE CONTINUE HANDLER 
        		FOR NOT FOUND SET finished = 1;
OPEN curbipList;

getbipsstate: 
		LOOP
			FETCH curbipList INTO v_bip, v_state, v_date;
			IF finished = 1 THEN 
				LEAVE getbipsstate;
			END IF;
			call createEmailInvolvementForEachRole (v_bip,v_state, v_date);
		END LOOP getbipsstate;
		CLOSE curbipList;

END//
DELIMITER ;

-- Dumping structure for procedure bips_new.getbipList_Roles_Controversialbips
DELIMITER //
CREATE PROCEDURE `getbipList_Roles_Controversialbips`()
BEGIN
	DECLARE finished INTEGER DEFAULT 0; DECLARE v_bip INTEGER DEFAULT 0; DECLARE v_messageId INTEGER DEFAULT 0; DECLARE v_state TEXT(15); DECLARE v_date DATE;
	DECLARE curbipList 
			CURSOR FOR 
				SELECT DISTINCT bip, state, DATE2 FROM bipstates_danieldata_datetimestamp 
				WHERE -- (STATE LIKE '%acc%' OR state LIKE '%rej%') 
				bip IN (204, 240, 256, 216, 284, 308, 313, 348, 3128, 3108)
				order by DATE2 asc;				
				DECLARE CONTINUE HANDLER 
        		FOR NOT FOUND SET finished = 1;
OPEN curbipList;

getbipsstate: 
		LOOP
			FETCH curbipList INTO v_bip, v_state, v_date;
			IF finished = 1 THEN 
				LEAVE getbipsstate;
			END IF;
			call createEmailInvolvementForEachMember_Controversialbips(v_bip,v_state, v_date);
		END LOOP getbipsstate;
		CLOSE curbipList;

END//
DELIMITER ;

-- Dumping structure for procedure bips_new.getbipSeeders_Replyers
DELIMITER //
CREATE PROCEDURE `getbipSeeders_Replyers`()
BEGIN
	DECLARE finished INTEGER DEFAULT 0; DECLARE v_bip INTEGER DEFAULT 0; DECLARE v_messageId INTEGER DEFAULT 0; DECLARE v_state TEXT(15); DECLARE v_date DATE;
	DECLARE v_biptype TEXT(15); DECLARE v_summessages INTEGER;
	DECLARE curbipList 
			CURSOR FOR 
			-- this is only for 245 acc rej bips
			--	SELECT DISTINCT bip, state, DATE2, biptype,							-- AND (folder LIKE '%dev%' OR folder LIKE '%ideas%')
			--		(SELECT COUNT(messageid) FROM allmessages WHERE bipnum2020 = m.bip ) AS summessages
			--		FROM bipstates_danieldata_datetimestamp m 
			--		WHERE (STATE LIKE '%acc%' OR state LIKE '%rej%') order by DATE2 asc;
			-- 12 jan 2021...we include all 466 bips rather than the 245 bips
			SELECT DISTINCT bip, state, DATE2, biptype, (SELECT COUNT(messageid) FROM allmessages WHERE bipnum2020 = m.bip) AS summessages
				from allbipstillBDFL m 								 
				ORDER BY DATE2 asc; 		
				DECLARE CONTINUE HANDLER 
        		FOR NOT FOUND SET finished = 1;
OPEN curbipList;

getbipsstate: 
		LOOP
			FETCH curbipList INTO v_bip, v_state, v_date, v_biptype, v_summessages;
			IF finished = 1 THEN 
				LEAVE getbipsstate;
			END IF;
			call createEmailInvolvementForEachPerson_SeedersReplyers (v_bip,v_state, v_date, v_biptype,  v_summessages);
		END LOOP getbipsstate;
		CLOSE curbipList;

END//
DELIMITER ;

-- Dumping structure for procedure bips_new.getbipSeeders_Replyers2
DELIMITER //
CREATE PROCEDURE `getbipSeeders_Replyers2`()
BEGIN
	DECLARE finished INTEGER DEFAULT 0; DECLARE v_bip INTEGER DEFAULT 0; DECLARE v_messageId INTEGER DEFAULT 0; DECLARE v_state TEXT(15); DECLARE v_date DATE;
	DECLARE v_biptype TEXT(15); DECLARE v_summessages INTEGER;
	DECLARE curbipList 
			CURSOR FOR 
			-- this is only for 245 acc rej bips
			--	SELECT DISTINCT bip, state, DATE2, biptype,							-- AND (folder LIKE '%dev%' OR folder LIKE '%ideas%')
			--		(SELECT COUNT(messageid) FROM allmessages WHERE bipnum2020 = m.bip ) AS summessages
			--		FROM bipstates_danieldata_datetimestamp m 
			--		WHERE (STATE LIKE '%acc%' OR state LIKE '%rej%') order by DATE2 asc;
			-- 12 jan 2021...we include all 466 bips rather than the 245 bips
			SELECT DISTINCT bip, state, DATE2, biptype, (SELECT COUNT(messageid) FROM allmessages WHERE bipnum2020 = m.bip) AS summessages
				from allbipstillBDFL m order by DATE2 asc; 		
				DECLARE CONTINUE HANDLER 
        		FOR NOT FOUND SET finished = 1;
OPEN curbipList;

getbipsstate: 
		LOOP
			FETCH curbipList INTO v_bip, v_state, v_date, v_biptype, v_summessages;
			IF finished = 1 THEN 
				LEAVE getbipsstate;
			END IF;
			call createEmailInvolvementForEachPerson_SeedersReplyers (v_bip,v_state, v_date, v_biptype,  v_summessages);
		END LOOP getbipsstate;
		CLOSE curbipList;

END//
DELIMITER ;

-- Dumping structure for procedure bips_new.getbipSeeders_Replyers_ForProposalAuthorAndBDFLD
DELIMITER //
CREATE PROCEDURE `getbipSeeders_Replyers_ForProposalAuthorAndBDFLD`()
BEGIN
	DECLARE finished INTEGER DEFAULT 0; DECLARE v_bip INTEGER DEFAULT 0; DECLARE v_messageId INTEGER DEFAULT 0; DECLARE v_state TEXT(15); DECLARE v_date DATE;
	DECLARE v_biptype TEXT(15); DECLARE v_summessages INTEGER;
	DECLARE curbipList 
			CURSOR FOR 
				-- SELECT DISTINCT bip, state, DATE2, biptype,
				--	(SELECT COUNT(messageid) FROM allmessages WHERE bipnum2020 = m.bip AND (folder LIKE '%dev%' OR folder LIKE '%ideas%')) AS summessages
				--	FROM bipstates_danieldata_datetimestamp m 
				--	WHERE (STATE LIKE '%acc%' OR state LIKE '%rej%') order by DATE2 asc;
				-- 12 jan 2021...we include all 466 bips rather than the 245 bips
				SELECT DISTINCT bip, state, DATE2, biptype, (SELECT COUNT(messageid) FROM allmessages WHERE bipnum2020 = m.bip) AS summessages
					from allbipstillBDFL m order by DATE2 asc; 
				DECLARE CONTINUE HANDLER 
        		FOR NOT FOUND SET finished = 1;
OPEN curbipList;

getbipsstate: 
		LOOP
			FETCH curbipList INTO v_bip, v_state, v_date, v_biptype, v_summessages;
			IF finished = 1 THEN 
				LEAVE getbipsstate;
			END IF;
			call createEmailInvolvementForProposalAuthorAndBDFLD_SeedersReplyers (v_bip,v_state, v_date, v_biptype,  v_summessages);
		END LOOP getbipsstate;
		CLOSE curbipList;

END//
DELIMITER ;

-- Dumping structure for procedure bips_new.getThreads_Per_Message
DELIMITER //
CREATE PROCEDURE `getThreads_Per_Message`()
BEGIN
	DECLARE finished INTEGER DEFAULT 0; DECLARE v_bipnum2020 INTEGER DEFAULT 0; DECLARE v_messageId INTEGER DEFAULT 0; DECLARE v_author TEXT(15); DECLARE v_date DATE;
	DECLARE v_clusterbysenderfullname TEXT(15); DECLARE v_emailmessageid TEXT(15); DECLARE v_subject TEXT(15); DECLARE v_threadid INTEGER;
	DECLARE v_inreplyto TEXT(15); DECLARE v_authorsrole2020 TEXT(20); 	DECLARE v_biptype2020 TEXT(20);  
	DECLARE curbipList 
			CURSOR FOR 
			-- get all threads at top level, and then in the next procedure we get replies to these threads
			SELECT bipnum2020, messageid, author, clusterbysenderfullname, emailmessageid, subject, threadid, authorsrole2020, biptype2020 -- , inreplyto,  
				FROM bipthreadsbyperson2020; 		
				DECLARE CONTINUE HANDLER 
        		FOR NOT FOUND SET finished = 1;
OPEN curbipList;

getbipsstate: 
		LOOP
			FETCH curbipList INTO v_bipnum2020,v_messageid, v_author, v_clusterbysenderfullname,  v_emailmessageid, v_subject, v_threadid, v_authorsrole2020, v_biptype2020;
			IF finished = 1 THEN 
				LEAVE getbipsstate;
			END IF;
			call createThreadInvolvementForEachPerson (v_bipnum2020,v_messageid, v_author, v_clusterbysenderfullname,  v_emailmessageid, 
																		v_subject, v_threadid, v_authorsrole2020, v_biptype2020, 2); -- pass 2 as threadlevel, we start at 1, which is the top level alrady explored
		END LOOP getbipsstate;
		CLOSE curbipList;

END//
DELIMITER ;

-- Dumping structure for procedure bips_new.getThreads_Per_Message_from_secondlevel
DELIMITER //
CREATE PROCEDURE `getThreads_Per_Message_from_secondlevel`(IN v_threadlevel INT(10))
BEGIN
	DECLARE finished INTEGER DEFAULT 0; DECLARE v_bipnum2020 INTEGER DEFAULT 0; DECLARE v_messageId INTEGER DEFAULT 0; DECLARE v_author TEXT(15); DECLARE v_date DATE;
	DECLARE v_clusterbysenderfullname TEXT(15); DECLARE v_emailmessageid TEXT(15); DECLARE v_subject TEXT(15); DECLARE v_threadid INTEGER;
	DECLARE v_inreplyto TEXT(15); DECLARE v_authorsrole2020 TEXT(20); 	DECLARE v_biptype2020 TEXT(20);  
	DECLARE curbipList 
			CURSOR FOR 
			-- get all threads at top level, and then in the next procedure we get replies to these threads
			SELECT bipnum2020, messageid, author, clusterbysenderfullname, emailmessageid, subject, threadid, authorsrole2020, biptype2020 -- , inreplyto,  
				FROM bipthreadsbyperson2020 WHERE threadlevel = v_threadlevel; 		
				DECLARE CONTINUE HANDLER 
        		FOR NOT FOUND SET finished = 1;
OPEN curbipList;

getbipsstate: 
		LOOP
			FETCH curbipList INTO v_bipnum2020,v_messageid, v_author, v_clusterbysenderfullname,  v_emailmessageid, v_subject, v_threadid, v_authorsrole2020, v_biptype2020;
			IF finished = 1 THEN 
				LEAVE getbipsstate;
			END IF;
			call createThreadInvolvementForEachPerson_recursive_from_secondlevel (v_bipnum2020,v_messageid, v_author, v_clusterbysenderfullname,  v_emailmessageid, 
																		v_subject, v_threadid, v_authorsrole2020, v_biptype2020, v_threadlevel+1); -- add 1 to threadlevel, 
		END LOOP getbipsstate;
		CLOSE curbipList;

END//
DELIMITER ;

-- Dumping structure for procedure bips_new.getThreads_Per_Message_recursive_from_firstlevel
DELIMITER //
CREATE PROCEDURE `getThreads_Per_Message_recursive_from_firstlevel`(IN v_threadlevel INT(10))
BEGIN
	DECLARE finished INTEGER DEFAULT 0; DECLARE v_bipnum2020 INTEGER DEFAULT 0; DECLARE v_messageId INTEGER DEFAULT 0; DECLARE v_author TEXT(15); DECLARE v_date DATE;
	DECLARE v_clusterbysenderfullname TEXT(15); DECLARE v_emailmessageid TEXT(15); DECLARE v_subject TEXT(15); DECLARE v_threadid INTEGER;
	DECLARE v_inreplyto TEXT(15); DECLARE v_authorsrole2020 TEXT(20); 	DECLARE v_biptype2020 TEXT(20);  
	DECLARE curbipList 
			CURSOR FOR 
			-- get all threads at top level, and then in the next procedure we get replies to these threads
			SELECT bipnum2020, messageid, author, clusterbysenderfullname, emailmessageid, subject, threadid, authorsrole2020, biptype2020 -- , inreplyto,  
				FROM bipthreadsbyperson2020 WHERE threadlevel = v_threadlevel; 		
				DECLARE CONTINUE HANDLER 
        		FOR NOT FOUND SET finished = 1;
OPEN curbipList;

getbipsstate: 
		LOOP
			FETCH curbipList INTO v_bipnum2020,v_messageid, v_author, v_clusterbysenderfullname,  v_emailmessageid, v_subject, v_threadid, v_authorsrole2020, v_biptype2020;
			IF finished = 1 THEN 
				LEAVE getbipsstate;
			END IF;
			call createThreadInvolvementForEachPerson_recursive_from_2ndlevel (v_bipnum2020,v_messageid, v_author, v_clusterbysenderfullname,  v_emailmessageid, 
																		v_subject, v_threadid, v_authorsrole2020, v_biptype2020, v_threadlevel+1); -- add 1 as threadlevel, 
		END LOOP getbipsstate;
		CLOSE curbipList;

END//
DELIMITER ;

-- Dumping structure for procedure bips_new.getThreads_Per_Message_recursive_from_secondlevel
DELIMITER //
CREATE PROCEDURE `getThreads_Per_Message_recursive_from_secondlevel`(IN v_threadlevel INT(10))
BEGIN
	DECLARE finished INTEGER DEFAULT 0; DECLARE v_bipnum2020 INTEGER DEFAULT 0; DECLARE v_messageId INTEGER DEFAULT 0; DECLARE v_author TEXT(15); DECLARE v_date DATE;
	DECLARE v_clusterbysenderfullname TEXT(15); DECLARE v_emailmessageid TEXT(15); DECLARE v_subject TEXT(15); DECLARE v_threadid INTEGER;
	DECLARE v_inreplyto TEXT(15); DECLARE v_authorsrole2020 TEXT(20); 	DECLARE v_biptype2020 TEXT(20);  
	DECLARE curbipList 
			CURSOR FOR 
			-- get all threads at top level, and then in the next procedure we get replies to these threads
			SELECT bipnum2020, messageid, author, clusterbysenderfullname, emailmessageid, subject, threadid, authorsrole2020, biptype2020 -- , inreplyto,  
				FROM bipthreadsbyperson2020 WHERE threadlevel = v_threadlevel; 		
				DECLARE CONTINUE HANDLER 
        		FOR NOT FOUND SET finished = 1;
OPEN curbipList;

getbipsstate: 
		LOOP
			FETCH curbipList INTO v_bipnum2020,v_messageid, v_author, v_clusterbysenderfullname,  v_emailmessageid, v_subject, v_threadid, v_authorsrole2020, v_biptype2020;
			IF finished = 1 THEN 
				LEAVE getbipsstate;
			END IF;
			call createThreadInvolvementForEachPerson_recursive_from_secondlevel (v_bipnum2020,v_messageid, v_author, v_clusterbysenderfullname,  v_emailmessageid, 
																		v_subject, v_threadid, v_authorsrole2020, v_biptype2020, v_threadlevel+1); -- add 1 to threadlevel, 
		END LOOP getbipsstate;
		CLOSE curbipList;

END//
DELIMITER ;

-- Dumping structure for procedure bips_new.get_to_update
DELIMITER //
CREATE PROCEDURE `get_to_update`()
BEGIN
	DECLARE finished INTEGER DEFAULT 0; DECLARE v_bip INTEGER DEFAULT 0; DECLARE v_biptype TEXT(15); DECLARE v_biptype2020 TEXT(20);
	DECLARE v_author TEXT(15); DECLARE v_author2020 TEXT(20); DECLARE v_bipnum2020 INTEGER DEFAULT 0; 
	DECLARE curbipList 
			CURSOR FOR 
			SELECT bip, bipnum2020, biptype, biptype2020, author, authorsrole2020 
				FROM allmessages -- temptest
					WHERE bip = -1 AND bipnum2020 > 0 AND authorsrole2020 IS NULL; 		
				DECLARE CONTINUE HANDLER 
        		FOR NOT FOUND SET finished = 1;
OPEN curbipList;

getbipsstate: 
		LOOP
			FETCH curbipList INTO v_bip, v_bipnum2020, v_biptype, v_biptype2020, v_author, v_author2020;
			IF finished = 1 THEN 
				LEAVE getbipsstate;
			END IF;
			-- update for each row
			call update_tablevalues(v_bip, v_bipnum2020, v_biptype, v_biptype2020, v_author, v_author2020);
		END LOOP getbipsstate;
		CLOSE curbipList;

END//
DELIMITER ;

-- Dumping structure for procedure bips_new.insertEmptyReasosnRows
DELIMITER //
CREATE PROCEDURE `insertEmptyReasosnRows`()
wholeblock:BEGIN
  DECLARE x INT;
  SET x = 0;

  loop_label: LOOP
    IF x > 5000 THEN
      LEAVE loop_label;
    END IF;
    SELECT x;
 --   SET str = CONCAT(str,x,',');
    INSERT INTO manualreasonextraction (proposal, messageid, author,dateTimeStamp)
	 	select distinct a.bip,  a.messageid, a.authorsrole, a.dateTimeStamp 
	 	from allmessages as a 
	 	where a.bip = x
	 	AND a.messageid NOT IN (select distinct b.messageid 
                 from manualreasonextraction as b 
                 where b.proposal = x);
    SET x = x + 1;
    ITERATE loop_label;
  END LOOP;
  SELECT str;
END//
DELIMITER ;

-- Dumping structure for procedure bips_new.insertSentence
DELIMITER //
CREATE PROCEDURE `insertSentence`()
BEGIN
	SELECT NOW();
	
END//
DELIMITER ;

-- Dumping structure for procedure bips_new.test
DELIMITER //
CREATE PROCEDURE `test`()
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

-- Dumping structure for procedure bips_new.update_tablevalues
DELIMITER //
CREATE PROCEDURE `update_tablevalues`(IN v_bip INT(10), IN v_bipnum2020 INT(10), IN v_biptype TEXT, IN v_biptype2020 TEXT, IN v_author TEXT, IN v_author2020 TEXT)
BEGIN
	DECLARE finished INTEGER DEFAULT 0; DECLARE vv_bip INTEGER DEFAULT 0; DECLARE vv_biptype TEXT(15); DECLARE vv_biptype2020 TEXT(20);
	DECLARE vv_author TEXT(15); DECLARE vv_authors2020 TEXT(20); DECLARE vv_authorsrole2020 TEXT(20); DECLARE vv_bipnum2020 INTEGER DEFAULT 0; 
--	SET p_state 
	DECLARE curgetMessages 
			CURSOR FOR 
				-- this query is just to get the authorsrole and later use it to set authorsrole2020 
				SELECT bip, bipnum2020, biptype, biptype2020, author, authorsrole2020 
					FROM allmessages -- table was temptest
							WHERE bipnum2020 = v_bipnum2020 AND bip = v_bipnum2020 AND author = v_author 
								AND bip <> -1 LIMIT 1;  -- just to be sure
				-- declare NOT FOUND handler
				DECLARE CONTINUE HANDLER 
        		FOR NOT FOUND SET finished = 1;
	-- INTO OUTFILE 'c:\\scripts\\SNA2020\\SNAAllDevelopersAfterClustering_19-12-2020_h.txt';
OPEN curgetMessages;

getMessages: 
		LOOP
			FETCH curgetMessages INTO vv_bip, vv_bipnum2020, vv_biptype, vv_biptype2020, vv_author, vv_authorsrole2020 ; 
			IF finished = 1 THEN 
				LEAVE getMessages;
			END IF;
			-- set the authorsrole2020
			UPDATE allmessages SET authorsrole2020 = vv_authorsrole2020 -- table was temptest
				WHERE author = vv_author
					AND bipnum2020 = vv_bipnum2020 AND authorsrole2020 IS NULL
					AND bip = -1 AND bipnum2020 > 0;			

		END LOOP getMessages;
		CLOSE curgetMessages;

END//
DELIMITER ;

-- Dumping structure for function bips_new.levenshtein_distance
DELIMITER //
CREATE FUNCTION `levenshtein_distance`(s1 VARCHAR(255), s2 VARCHAR(255)) RETURNS int(11)
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

-- Dumping structure for view bips_new.allmessages_child
-- Removing temporary table and create final VIEW structure
DROP TABLE IF EXISTS `allmessages_child`;
CREATE ALGORITHM=UNDEFINED SQL SECURITY DEFINER VIEW `allmessages_child` AS SELECT * FROM allmessages 
WHERE inReplyTo  IS NOT NULL -- only those which we can match
AND bip = -1 ;

-- Dumping structure for view bips_new.allmessages_child_first
-- Removing temporary table and create final VIEW structure
DROP TABLE IF EXISTS `allmessages_child_first`;
CREATE ALGORITHM=UNDEFINED SQL SECURITY DEFINER VIEW `allmessages_child_first` AS SELECT * FROM allmessages 
WHERE DATE2 < '2015-06-04' 
AND inReplyTo  IS NOT NULL -- only those which we can match
AND bip = -1 ;

-- Dumping structure for view bips_new.allmessages_parent
-- Removing temporary table and create final VIEW structure
DROP TABLE IF EXISTS `allmessages_parent`;
CREATE ALGORITHM=UNDEFINED SQL SECURITY DEFINER VIEW `allmessages_parent` AS SELECT * FROM allmessages 
WHERE emailmessageid  IS NOT NULL -- only those which we can match
AND bip <> -1 ;

-- Dumping structure for view bips_new.allmessages_parent_first
-- Removing temporary table and create final VIEW structure
DROP TABLE IF EXISTS `allmessages_parent_first`;
CREATE ALGORITHM=UNDEFINED SQL SECURITY DEFINER VIEW `allmessages_parent_first` AS SELECT * FROM allmessages 
WHERE DATE2 < '2015-06-04'
AND emailmessageid  IS NOT NULL -- only those which we can match
AND bip <> -1 ;

-- Dumping structure for view bips_new.allotherstates
-- Removing temporary table and create final VIEW structure
DROP TABLE IF EXISTS `allotherstates`;
CREATE ALGORITHM=UNDEFINED SQL SECURITY DEFINER VIEW `allotherstates` AS select distinct bip from bipstates_danieldata_datetimestamp where email LIKE '%ACTIVE' OR email LIKE '%DRAFT' ;

-- Dumping structure for view bips_new.cdev
-- Removing temporary table and create final VIEW structure
DROP TABLE IF EXISTS `cdev`;
CREATE ALGORITHM=UNDEFINED SQL SECURITY DEFINER VIEW `cdev` AS SELECT coredeveloper as clusterBySenderFullName fROM coredevelopers ;

-- Dumping structure for view bips_new.commonroles
-- Removing temporary table and create final VIEW structure
DROP TABLE IF EXISTS `commonroles`;
CREATE ALGORITHM=UNDEFINED SQL SECURITY DEFINER VIEW `commonroles` AS select author, authorsrole, count(distinct bip) as bipCount 
	from rolespersonmsgcount
	-- IMPORTANT. For acceptance and rejection 
	-- WHERE state LIKE '%rej%' -- for overall data comment this line
	group by author
	having bipCount > 10
	ORDER BY bipcount DESC ;

-- Dumping structure for view bips_new.distinctclusters
-- Removing temporary table and create final VIEW structure
DROP TABLE IF EXISTS `distinctclusters`;
CREATE ALGORITHM=UNDEFINED SQL SECURITY DEFINER VIEW `distinctclusters` AS (
	select cluster, count(id) as members-- , max(sendername), sum(totalMessageCount)
	from distinctsenders
	WHERE cluster IS NOT NULL 
	group by cluster
	order by totalMessageCount DESC 
) ;

-- Dumping structure for view bips_new.duplicatemessageid
-- Removing temporary table and create final VIEW structure
DROP TABLE IF EXISTS `duplicatemessageid`;
CREATE ALGORITHM=UNDEFINED SQL SECURITY DEFINER VIEW `duplicatemessageid` AS SELECT bip,messageid,timestamp, COUNT(messageid) AS NumOccurrences
	    FROM results_postprocessed
	    GROUP BY bip,messageid
	    HAVING NumOccurrences > 1 ;

-- Dumping structure for view bips_new.email_a
-- Removing temporary table and create final VIEW structure
DROP TABLE IF EXISTS `email_a`;
CREATE ALGORITHM=UNDEFINED SQL SECURITY DEFINER VIEW `email_a` AS SELECT distinct bip, dateTIMESTAMP, state, 
(SELECT type From bipdetails p  WHERE bipstates_danieldata_datetimestamp.bip = p.bip LIMIT 1) as ptype
FROM bipstates_danieldata_datetimestamp
WHERE state LIKE '%accepted%'  AND bip <> -1
-- OR clausie LIKE '%poll%' OR clausie LIKE '%vot%'
ORDER BY bip, dateTIMESTAMP DESC ;

-- Dumping structure for view bips_new.email_f
-- Removing temporary table and create final VIEW structure
DROP TABLE IF EXISTS `email_f`;
CREATE ALGORITHM=UNDEFINED SQL SECURITY DEFINER VIEW `email_f` AS SELECT distinct bip, dateTIMESTAMP, state, 
(SELECT type From bipdetails p  WHERE bipstates_danieldata_datetimestamp.bip = p.bip LIMIT 1) as ptype
FROM bipstates_danieldata_datetimestamp
WHERE state LIKE '%final%'  AND bip <> -1
-- OR clausie LIKE '%poll%' OR clausie LIKE '%vot%'
ORDER BY bip, dateTIMESTAMP DESC ;

-- Dumping structure for view bips_new.firststates
-- Removing temporary table and create final VIEW structure
DROP TABLE IF EXISTS `firststates`;
CREATE ALGORITHM=UNDEFINED SQL SECURITY DEFINER VIEW `firststates` AS select DISTINCT bip from bipstates_danieldata_datetimestamp 
				where email LIKE '%REJECTED' 
				OR email LIKE '%ACCEPTED' 	  OR email LIKE '%FINAL' 			OR email LIKE '%CLOSEED' 
				OR email LIKE '%WITHDRAWN'   OR email LIKE '%PROVISIONAL' 	OR email LIKE '%PENDING' 
				OR email LIKE '%POSTPONED'   OR email LIKE '%REPLACED'		OR email LIKE '%DEFERRED'
				OR email LIKE '%INCOMPLETE'  OR email LIKE '%SUPER' ;

-- Dumping structure for view bips_new.ones
-- Removing temporary table and create final VIEW structure
DROP TABLE IF EXISTS `ones`;
CREATE ALGORITHM=UNDEFINED SQL SECURITY DEFINER VIEW `ones` AS SELECT  sentence, 
containsreason -- only include while creating views, not when creating csv file
authorroleweight,datediffweight,
currSentencereasonstermsfoundcount,currSentencereasonsIdentifierTermsCount,currSentencestatesSubstatesCount,currSentenceidentifiersTermCount, 
currSentenceentitiesTermCount,currSentencespecialTermCount,currSentencedecisionTermCount,
messageSubjectStatesSubstatesListCount,messageSubjectEntitiesTermListCount,messageSubjectDecisionTermListCount,messageSubjectProposalIdentifiersTermListCount,messageSubjectSpecialTermListCount,
restOfParagraphStateTermsCount, restOfParagraphIdentifiersTermsCount,restOfParagraphEntityTermsCount,restOfParagraphDecisionTermsCount, restOfParagraphSpecialTermsCount, restOfParagraphReasonsTermsCount,
messsageTypeIsReasonMessage,sentenceLocationInFirstLastParagraph,SameMsgSubAsStateTriple
FROM autoextractedreasoncandidatesentences_novaCCEPTED
WHERE proposal<5000 and label like '%accepted%' and location = 'sentence'
and containsreason =1
ORDER BY proposal ;

-- Dumping structure for view bips_new.biptypecount
-- Removing temporary table and create final VIEW structure
DROP TABLE IF EXISTS `biptypecount`;
CREATE ALGORITHM=UNDEFINED SQL SECURITY DEFINER VIEW `biptypecount` AS SELECT DISTINCT bip, (SELECT type From bipdetails p  WHERE bipstates_danieldata_datetimestamp.bip = p.bip LIMIT 1) as ptype
FROM bipstates_danieldata_datetimestamp
WHERE bip < 8000 AND bip <> -1 ;

-- Dumping structure for view bips_new.rp_bdflp
-- Removing temporary table and create final VIEW structure
DROP TABLE IF EXISTS `rp_bdflp`;
CREATE ALGORITHM=UNDEFINED SQL SECURITY DEFINER VIEW `rp_bdflp` AS SELECT distinct bip, TIMESTAMP, clausie, 
(SELECT type From bipdetails p  WHERE results_postprocessed.bip = p.bip LIMIT 1) as ptype
FROM results_postprocessed 
WHERE clausie LIKE '%bdfl pronouncement%' OR clausie LIKE '%bdfl delegate%'
ORDER BY bip, TIMESTAMP desc ;

-- Dumping structure for view bips_new.rp_both
-- Removing temporary table and create final VIEW structure
DROP TABLE IF EXISTS `rp_both`;
CREATE ALGORITHM=UNDEFINED SQL SECURITY DEFINER VIEW `rp_both` AS SELECT bip, clausie, ptype
FROM rp_bdflp  
WHERE clausie LIKE '%bdfl pronouncement%' OR clausie LIKE '%bdfl delegate%'
 AND rp_bdflp.bip IN
   	(SELECT bip
		FROM rp_consensus
		WHERE clausie LIKE '%consensus%') ;

-- Dumping structure for view bips_new.rp_both_s
-- Removing temporary table and create final VIEW structure
DROP TABLE IF EXISTS `rp_both_s`;
CREATE ALGORITHM=UNDEFINED SQL SECURITY DEFINER VIEW `rp_both_s` AS SELECT bip, clausie, ptype
FROM rp_bdflp  
WHERE clausie LIKE '%bdfl pronouncement%' OR clausie LIKE '%bdfl delegate%' 
AND ptype LIKE '%standards%'
 AND rp_bdflp.bip IN
   	(SELECT bip
		FROM rp_consensus
		WHERE clausie LIKE '%consensus%')
		AND ptype LIKE '%standards%' ;

-- Dumping structure for view bips_new.rp_consensus
-- Removing temporary table and create final VIEW structure
DROP TABLE IF EXISTS `rp_consensus`;
CREATE ALGORITHM=UNDEFINED SQL SECURITY DEFINER VIEW `rp_consensus` AS SELECT distinct bip, TIMESTAMP, clausie, 
(SELECT type From bipdetails p  WHERE results_postprocessed.bip = p.bip LIMIT 1) as ptype
FROM results_postprocessed
WHERE clausie LIKE '%consensus%'  
-- OR clausie LIKE '%poll%' OR clausie LIKE '%vot%'
ORDER BY bip, TIMESTAMP DESC ;

-- Dumping structure for view bips_new.summaries
-- Removing temporary table and create final VIEW structure
DROP TABLE IF EXISTS `summaries`;
CREATE ALGORITHM=UNDEFINED SQL SECURITY DEFINER VIEW `summaries` AS select bip, messageid, date2, email, subject, identifiercount from allmessages where identifiercount > 5 and (subject like '%summary%' or subject like '%summaries%') ;

-- Dumping structure for view bips_new.top10authors
-- Removing temporary table and create final VIEW structure
DROP TABLE IF EXISTS `top10authors`;
CREATE ALGORITHM=UNDEFINED SQL SECURITY DEFINER VIEW `top10authors` AS SELECT DISTINCT AUTHOR, SUM(totalmsgs) AS S FROM communicationsforseeders group by author ORDER BY s DESC LIMIT 20 ;

-- Dumping structure for view bips_new.unfinished
-- Removing temporary table and create final VIEW structure
DROP TABLE IF EXISTS `unfinished`;
CREATE ALGORITHM=UNDEFINED SQL SECURITY DEFINER VIEW `unfinished` AS SELECT bip FROM allotherstates
    where bip not in 
(SELECT bip FROM firststates) 
order by bip ;

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
