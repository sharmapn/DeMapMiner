select pep, label, causecategory, causesubcategory, causesentence, effectsentence,  communityreview, proposalauthorreview, bdfldelegatepronouncement
from trainingdata
order by label, causecategory, causesubcategory asc