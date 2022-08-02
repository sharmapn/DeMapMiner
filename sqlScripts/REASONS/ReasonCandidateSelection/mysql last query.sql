
set global log_output = 'table';
set global general_log = 'on';
select * from mysql.general_log;
-- SELECT * FROM  mysql.general_log  WHERE command_type ='Query' LIMIT total;

select event_time as time,    user_host,    thread_id,
    server_id,    command_type,    argument
from mysql.general_log
where user_host like 'root%'
order by event_time desc
limit 1;