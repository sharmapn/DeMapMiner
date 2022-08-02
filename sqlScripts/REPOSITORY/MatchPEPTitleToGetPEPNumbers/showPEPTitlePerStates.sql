DELIMITER $$
USE `peps_new`$$
DROP PROCEDURE IF EXISTS `showsPEPTitlesPerStates2`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `test`()
BEGIN
	DECLARE i INTEGER;
	DECLARE n TEXT;
	DECLARE m TEXT;
	DECLARE curs1 CURSOR FOR SELECT `pep`,'title' FROM pepdetails;
	DECLARE curs2 CURSOR FOR SELECT `pepstates` FROM pepstates WHERE pep = i;
	
		OPEN curs1;
		FETCH curs1 INTO i,n;
		SELECT i,n;		
		-- for each pep select 		
			OPEN curs2;
			FETCH curs2 INTO m;
			SELECT m;
			CLOSE curs2;
		
		CLOSE curs1;
END$$
DELIMITER ;