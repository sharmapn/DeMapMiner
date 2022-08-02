  select * from distinctsenders
  where  sendername like '%admin%' or clusterbysendername = 109
  order by totalmessagecount desc;  -- emailaddress like '%spam%' or
  
  -- check for which spam terms if removed would still have someterms in name and email as spam creaes a cluster on its own.
  