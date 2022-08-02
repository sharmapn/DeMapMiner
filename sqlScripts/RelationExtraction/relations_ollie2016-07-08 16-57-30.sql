Select arg1, relation, arg2, sentence from peps.relations_ollie
USE peps;

CREATE TABLE `relations_ollie` (
  `arg1` text,
  `relation` text,
  `arg2` text,
  `sentence` text
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


