-- select sendername from alldevelopers where sendername like 'm%'
select * from allmessages 
where email like 'From martin@v.loewis.de%' 
-- and sendername = 'martin'

-- update allmessages set sendername = 'martin.v.loewis'
-- where email like 'From martin@v.loewis.de%' 