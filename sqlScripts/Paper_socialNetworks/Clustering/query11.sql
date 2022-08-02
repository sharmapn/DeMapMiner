select cluster,totalMessageCount, MAX(LENGTH(sendername)) maxSendernameLength 
from distinctsenders 
WHERE cluster IS NOT NULL AND clustered IS NOT NULL and clusteredbysendername IS NOT NULL
group by cluster 
order by totalMessageCount DESC LIMIT 50;

SELECT CLUSTER, CLUSTERBYSENDERNAME FROM DISTINCTSENDERS WHERE EMailaddress = 'bbfoto2002@netscape.net';

select count(*) from distinctsenders 
where cluster = 10315;