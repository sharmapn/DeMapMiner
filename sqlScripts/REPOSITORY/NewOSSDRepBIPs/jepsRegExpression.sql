CREATE TABLE jeps 	(`email` varchar(20));
	
INSERT INTO jeps	(`email`)VALUES	('jep 123 store'),	('129387 jep 13 store'),	('12as9387 store jep 5');
INSERT INTO jeps 	(`email`) VALUES 	('abc store'),	('129387 store'),	('12as9387 store');


SELECT '129387 store' REGEXP '^[0-9]* store$';
SELECT * FROM  `jeps`  WHERE  `email` REGEXP '^[0-9]* store';

create table jepTest 
as 
	SELECT jep,messageid, POSITION('jep' IN email) as pos, 
	SUBSTR(email,POSITION('jep' IN email)-20,40) AS jepText
-- 	SUBSTR(email,POSITION('jep' IN email),-10) AS jepTextBefore
	FROM allmessages 
	where jep = -1
	having pos >0;
-- limit 5;

SELECT * FROM jeptest WHERE jepText REGEXP '[0-9]'

create table jepTestSubject 
as 
	SELECT jep,messageid, POSITION('jep' IN subject) as pos, 
	SUBSTR(subject,POSITION('jep' IN subject)-20,40) AS jepText
-- 	SUBSTR(email,POSITION('jep' IN email),-10) AS jepTextBefore
	FROM allmessages 
	where jep = -1
	having pos >0;
SELECT * FROM jepTestSubject WHERE jepText REGEXP '[0-9]' AND jeptext like '%jep%'