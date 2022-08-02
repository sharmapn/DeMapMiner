CREATE INDEX id
ON extractedrelations_clausie (id);

ALTER TABLE extractedrelations_clausie ADD id INT PRIMARY KEY AUTO_INCREMENT;

ALTER TABLE extractedrelations_clausie ADD INDEX `arg1` (`arg1`(30));
ALTER TABLE extractedrelations_clausie ADD INDEX `arg2` (`arg2`(30));