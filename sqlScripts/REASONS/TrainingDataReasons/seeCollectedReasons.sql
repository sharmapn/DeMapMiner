select pep,
(select type  from pepdetails where pepdetails.pep = trainingdata.pep) as 'Type', 
label, causesentence, effectsentence,causecategory,communityReview, proposalAuthorReview,bdfldelegatePronouncement 
from trainingdata order by pep