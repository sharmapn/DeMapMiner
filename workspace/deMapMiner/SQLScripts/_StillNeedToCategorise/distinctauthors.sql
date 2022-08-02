create table distinctauthors as
select distinct(author) from allmessages 
-- order by author asc