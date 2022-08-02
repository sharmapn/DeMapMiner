select senderemail from 
where senderemail like '%\'%'
order by senderemail
limit 2000


select senderemail
from allmessages
where senderemail like '%tspamme\'%'
limit 50;

SELECT senderemail from distinctsenders where totalMessageCount IS NULL
limit 5;

-- REMOVE SOME SPAM text within table column

-- UPDATE distinctsenders SET senderemail=REPLACE(senderemail,'don\'tspamme','');
UPDATE allmessages SET senderemail=REPLACE(senderemail,'don\'tspamme','dontspamme');
UPDATE distinctsenders SET senderemail=REPLACE(senderemail,'don\'tspamme','dontspamme');
UPDATE allmessages SET senderemail=REPLACE(senderemail,'\/#NOSPAM#\\','');
UPDATE distinctsenders SET senderemail=REPLACE(senderemail,'\/#NOSPAM#\\','');

UPDATE distinctsenders SET senderemail=REPLACE(senderemail,'\/#NOSPAM#\/','');
-- UPDATE distinctsenders SET senderemail=REPLACE(senderemail,'.NOSPAM.ME','');
-- UPDATE distinctsenders SET senderemail=REPLACE(senderemail,'.#NOSPAM.ME#','');
UPDATE distinctsenders SET senderemail=REPLACE(senderemail,'.#//NOSPAM.ME//#','');
UPDATE distinctsenders SET senderemail=REPLACE(senderemail,'#//N0SPAM//#','');

-- UPDATE distinctsenders SET senderemail=REPLACE(senderemail,'#NOSPAM#','');
-- UPDATE distinctsenders SET senderemail=REPLACE(senderemail,'#$#NOSPAMMINGME#$$%#','');
-- UPDATE distinctsenders SET senderemail=REPLACE(senderemail,'.#$#NOSPAM.ME#$$\%','');

-- UPDATE ALLMESSAGES 
UPDATE allmessages SET senderemail=REPLACE(senderemail,'\/#NOSPAM#\\','');

UPDATE allmessages SET senderemail=REPLACE(senderemail,'\/#NOSPAM#\\','');
UPDATE allmessages SET senderemail=REPLACE(senderemail,'\/#NOSPAM#\/','');
--UPDATE allmessages SET senderemail=REPLACE(senderemail,'.NOSPAM.ME','');
--UPDATE allmessages SET senderemail=REPLACE(senderemail,'.#NOSPAM.ME#','');
UPDATE allmessages SET senderemail=REPLACE(senderemail,'.#//NOSPAM.ME//#','');
UPDATE allmessages SET senderemail=REPLACE(senderemail,'#//N0SPAM//#','');
-- UPDATE allmessages SET senderemail=REPLACE(senderemail,'#NOSPAM#','');
-- UPDATE allmessages SET senderemail=REPLACE(senderemail,'#$#NOSPAMMINGME#$$%#','');
-- UPDATE allmessages SET senderemail=REPLACE(senderemail,'.#$#NOSPAM.ME#$$\%','');

UPDATE distinctsenders SET senderemail=REPLACE(senderemail,'#//#','');

SELECT * from distinctsenders where senderemail LIKE 'dialtone%';
-- where id is null

-- i stuffed up so correcting
UPDATE distinctsenders SET emailaddress = 'NOSPAM@fusion.net.nz' WHERE emailaddress = '@fusion.net.nz';
UPDATE allmessages SET senderemail= 'NOSPAM@fusion.net.nz' WHERE senderemail = '@fusion.net.nz';
