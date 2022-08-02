CREATE TABLE foo (person text, groupname text, age int);
INSERT INTO foo VALUES('Paul',2,36);
INSERT INTO foo VALUES('Laura',2,39);
INSERT INTO foo VALUES('Joe',2,36);
INSERT INTO foo VALUES('Bob',1,32);
INSERT INTO foo VALUES('Jill',1,34);
INSERT INTO foo VALUES('Shawn',1,42);
INSERT INTO foo VALUES('Jake',2,29);
INSERT INTO foo VALUES('James',2,15);
INSERT INTO foo VALUES('Fred',1,12);
INSERT INTO foo VALUES('Chuck',3,112);


SELECT a.person, a.groupname, a.age 
FROM foo AS a 
WHERE a.age >= (SELECT MIN(b.age)
                FROM foo AS b 
                WHERE (SELECT COUNT(*)
                       FROM foo AS c
                       WHERE c.groupname = b.groupname AND c.age >= b.age) <= 2
                GROUP BY b.groupname)
ORDER BY a.groupname ASC, a.age DESC;