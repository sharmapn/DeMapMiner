
DELIMITER //
CREATE PROCEDURE ClusterEmailSenders(IN EMAIL VARCHAR(255))
 BEGIN
 	DECLARE maxID INT;
 
 -- SELECT * FROM distinctsenders WHERE emailAddress = 'pankaj@gmail.com';
 
	 select distinct (senderfirstname) AS MATCHING ,id,sendername, 
	 emailaddress, senderFirstName, senderLastName,senderEmailFirstSegment
--	 levenshtein_distance(EMAIL,senderfirstname)  as levenshtein_distance,
	 from distinctsenders WHERE clustered IS NULL;
--	 order by levenshtein_distance asc limit 15;
	 
	 SELECT max(cluster) from distinctsenders INTO maxID;
	 SET maxID = MAXid+1;
	 
	 UPDATE distinctsenders SET clustered = 1, cluster = maxID WHERE emailAddress = 'pankaj@gmail.com';
	 
 END //

DELIMITER ;

