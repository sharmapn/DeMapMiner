SELECT * FROM allmessages LIMIT 1000
SELECT distinct(clusterbysenderfullname) FROM allmessages LIMIT 1000


-- GET members BY NUMBER of DISTINCT messages 

SELECT distinct(clusterbysenderfullname) 
FROM allmessages 

INSERT INTO roles (role) VALUES ('user');
INSERT INTO roles (role) VALUES ('developer');