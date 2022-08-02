update distinctsenders set cluster=NULL, clustered=NULL,clusterBySenderName = NULL, clusteredBySenderName = NULL;	

select id,sendername,emailaddress,cluster, clusteredbysendername from distinctsenders 
order by clusteredbysendername desc;