create table allpepnumbers
as select * from allmessages where 
pep <> -1;