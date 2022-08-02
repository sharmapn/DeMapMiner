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
  `entireParagraph` text,
  `prevParagraph` text,
  `nextParagraph` text,
  `messageID` int(11) DEFAULT NULL,
  `arg1_processed` text,
  `relation_processed` text,
  `arg2_processed` text,
  PRIMARY KEY (`id`),
  KEY `messageID` (`messageID`)
) ENGINE=InnoDB AUTO_INCREMENT=48 DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

select * from extractedrelations_clausie limit 50;	-- where nextsentence IS NOT NULL 

select * from allmessages where messageid = 238;
