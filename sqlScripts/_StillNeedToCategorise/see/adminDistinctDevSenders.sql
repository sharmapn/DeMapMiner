update distinctdevsenders
set cluster= null, clustered = null;

select * from distinctsenders
order by cluster ASC