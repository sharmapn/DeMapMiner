CREATE TABLE `links` (
  `link` text,
  `checked` int(11) DEFAULT NULL,
  `containsPython` tinyint(4) DEFAULT NULL,
  `checkedForPython` tinyint(4) DEFAULT NULL,
  KEY `link` (`link`(50))
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
