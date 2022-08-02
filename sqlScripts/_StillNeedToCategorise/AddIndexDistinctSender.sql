ALTER TABLE distinctsenders ADD INDEX `emailaddress` (`emailaddress`(20));
ALTER TABLE distinctsenders ADD INDEX `senderName` (`senderName`(30));
ALTER TABLE distinctsenders ADD INDEX `senderFirstName` (`senderFirstName`(30));
ALTER TABLE distinctsenders ADD INDEX `senderLastName` (`senderLastName`(30));
ALTER TABLE distinctsenders ADD INDEX `senderEmailFirstSegment` (`senderEmailFirstSegment`(30));