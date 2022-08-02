alter table allmessages add column fromLine varchar(200); 
-- as SUBSTRING_INDEX(email,'\n',1)

update allmessages set fromLine = SUBSTRING_INDEX(email,'\n',1)