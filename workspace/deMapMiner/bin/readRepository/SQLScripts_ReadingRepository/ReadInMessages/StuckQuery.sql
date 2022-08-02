-- IN CASE OF DATEERROR
 select * from allmessages where messageID = 1719450

-- IN CASE OF GCC ERROR
-- run this query to get the first mesage id to start from
-- code in eclipse will delete th rest of records inserted from this file till eror
select folder, count(messageid) from allmessages group by folder;
distinct(folder) 
-- EXPLAIN
select min(messageID) from allmessages where folder = 'C:\\datasets\\python-checkins' and file = '2013-November.txt'
DELETE FROM allmessages where messageID > 824311
-- minus 1 from the result of above
																-- and messageID < 1000000
-- DELETE FROM allmessages where messageID > 366436  and messageID < 1000000  -- if commit messages already read in and we dont want to delete it

-- select MAX(messageID) from allmessages WHERE  messageID > 1000000