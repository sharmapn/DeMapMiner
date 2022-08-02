--ALTER TABLE peps.allpeps ADD PRIMARY KEY (messageid);
ALTER TABLE peps.allpeps add column pkId INT NOT NULL AUTO_INCREMENT FIRST, ADD primary KEY pkId(pkId)