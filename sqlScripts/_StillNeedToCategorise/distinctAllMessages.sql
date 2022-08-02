create table all_distinctmessages as
(select * from allmessages group by messageID) 