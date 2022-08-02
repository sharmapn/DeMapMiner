select * from allmessages_checkins 
where pep = 308
and email like '%Status: Active%'   -- active comes before draft stage
and email NOT LIKE '%! Status: Active%'
order by date2 asc
-- limit 5;

select * from allmessages_checkins 
where pep = 391
-- and (email like '%Status: Accepted%' or email like '%Status: Rejected%')  -- active comes before draft stage
-- and (email NOT LIKE '%! Status: Accepted%' and email NOT LIKE '%! Status: Rejected%')
order by date2 asc;

select * from pepstates where email = 'Status : ACTIVE' OR email = 'Status : DRAFT'
order by pep asc, date2 asc;

