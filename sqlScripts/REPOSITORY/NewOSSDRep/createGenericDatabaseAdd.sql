-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server version:               5.6.38-log - MySQL Community Server (GPL)
-- Server OS:                    Win64
-- HeidiSQL Version:             9.4.0.5125
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;

-- Dumping structure for table peps_new.extractedrelations_clausie
DROP TABLE IF EXISTS `extractedrelations_clausie`;
CREATE TABLE IF NOT EXISTS `extractedrelations_clausie` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `arg1` text,
  `relation` text,
  `arg2` text,
  `sentence` text,
  `previousSentence` text,
  `nextSentence` text,
  `pep` int(11) DEFAULT NULL,
  `messageID` int(11) DEFAULT NULL,
  `arg1_processed` text,
  `relation_processed` text,
  `arg2_processed` text,
  PRIMARY KEY (`id`),
  KEY `messageID` (`messageID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

-- Data exporting was unselected.
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
