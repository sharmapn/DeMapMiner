update trainingdata set consider =1;

select id, pep, label, messageid, causesentence, consider
from trainingdata where pep = 386 and consider = 1