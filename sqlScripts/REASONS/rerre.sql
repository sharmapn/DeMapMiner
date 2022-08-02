select causesentence from trainingdata_mymanuallyextracted_aug2018;

select * from trainingdata where pep = 259;

select count(pep) from allmessages where pep <> originalPEPNumberNum;
select authorsrole from allmessages where pep <> originalPEPNumberNum limit 50;

select * from allmessages where messageid = 385424;