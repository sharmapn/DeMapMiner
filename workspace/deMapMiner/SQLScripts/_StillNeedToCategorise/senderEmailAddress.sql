alter table allmessages add column senderemail tinytext; 
-- as SUBSTRING_INDEX(email,'\n',1)

-- update allmessages set fromLine = SUBSTRING_INDEX(email,'\n',1)