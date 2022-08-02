-- finding the reasons for each label type
 -- bdfl_acceptance or rejkection is part of accepted or rejected repectively and therfore 
 -- these are states rather than reasons. which makes senese and wrangling/disagreement/no consensus becomes a reason

select pep, clausie, allreasons, reasonInSentence,reasonTermsInMatchedTriple,reasonTermsInNearbySentencesParagraphs,reasonTriplesInNearbySentencesParagraphs
from results
where clausie like '%bdfl%' 
AND pep > 100
order by pep, clausie

insert into labelslesserstatesreasonlabels (label, lesserState) values ('accepted','bdfl_pronouncement_accepted');
insert into labelslesserstatesreasonlabels (label, lesserState) values ('accepted','co_bdfl_delegate_accepted_pep');
insert into labelslesserstatesreasonlabels (label, reason) values ('accepted','positive_feedback');
insert into labelslesserstatesreasonlabels (label, reason) values ('accepted','favourable_feedback');
insert into labelslesserstatesreasonlabels (label, reason) values ('accepted','rough_consensus');
insert into labelslesserstatesreasonlabels (label, reason) values ('accepted','consensus');
insert into labelslesserstatesreasonlabels (label, reason) values ('accepted','bdfl pronouncement');
insert into labelslesserstatesreasonlabels (label, reason) values ('accepted','consensus');
insert into labelslesserstatesreasonlabels (label, reason) values ('accepted','majority');
insert into labelslesserstatesreasonlabels (label, reason) values ('accepted','poll result');
insert into labelslesserstatesreasonlabels (label, reason) values ('accepted','vote result');
insert into labelslesserstatesreasonlabels (label, reason) values ('accepted','debate');

insert into labelslesserstatesreasonlabels (label, lesserState) values ('rejected','bdfl_pronouncement_rejected');
insert into labelslesserstatesreasonlabels (label, reason) values ('rejected','no_support');
insert into labelslesserstatesreasonlabels (label, reason) values ('rejected','no_consensus');
insert into labelslesserstatesreasonlabels (label, reason) values ('rejected','superseded');
insert into labelslesserstatesreasonlabels (label, reason) values ('rejected','poor syntax');
insert into labelslesserstatesreasonlabels (label, reason) values ('rejected','difficulty_of_implementation');
insert into labelslesserstatesreasonlabels (label, reason) values ('rejected','no_one_spoke_for');
insert into labelslesserstatesreasonlabels (label, reason) values ('rejected','no_popular_support');
insert into labelslesserstatesreasonlabels (label, reason) values ('rejected','poll result');
insert into labelslesserstatesreasonlabels (label, reason) values ('rejected','vote result');
insert into labelslesserstatesreasonlabels (label, reason) values ('rejected','no majority support');

insert into labelslesserstatesreasonlabels (label, reason) values ('draft','idea_positive');
insert into labelslesserstatesreasonlabels (label, reason) values ('open','');
insert into labelslesserstatesreasonlabels (label, reason) values ('active','');
insert into labelslesserstatesreasonlabels (label, reason) values ('pending','');
insert into labelslesserstatesreasonlabels (label, reason) values ('close', ''); 
insert into labelslesserstatesreasonlabels (label, reason) values ('final','');
insert into labelslesserstatesreasonlabels (label, reason) values ('defer','');
insert into labelslesserstatesreasonlabels (label, reason) values ('replace','');
insert into labelslesserstatesreasonlabels (label, reason) values ('postponed','');
insert into labelslesserstatesreasonlabels (label, reason) values ('incomplete',''); 
insert into labelslesserstatesreasonlabels (label, reason) values ('superseded',''); 
insert into labelslesserstatesreasonlabels (label, reason) values ('vote','no_consensus'); 
insert into labelslesserstatesreasonlabels (label, reason) values ('vote','community_consensus_unknown'); 
insert into labelslesserstatesreasonlabels (label, reason) values ('poll','no_consensus');

-- added after reanalysis of 29 peps of all reasons
insert into labelslesserstatesreasonlabels (label, reason) values ('rejected','consensus');
insert into labelslesserstatesreasonlabels (label, reason) values ('accepted','bdfl_accepts_over_negative_feedback');
insert into labelslesserstatesreasonlabels (label, reason) values ('accepted','vote results');
insert into labelslesserstatesreasonlabels (label, reason) values ('accepted','bdfl_chooses_over_community_choice');
insert into labelslesserstatesreasonlabels (label, reason) values ('accepted','discussion');
insert into labelslesserstatesreasonlabels (label, reason) values ('updated','feedback');
insert into labelslesserstatesreasonlabels (label, reason) values ('vote','state of community consensus');
insert into labelslesserstatesreasonlabels (label, reason) values ('accepted','no objections');
insert into labelslesserstatesreasonlabels (label, reason) values ('poll','controversy');
insert into labelslesserstatesreasonlabels (label, reason) values ('updated','discussion');
insert into labelslesserstatesreasonlabels (label, reason) values ('draft','discussion');
insert into labelslesserstatesreasonlabels (label, reason) values ('accepted','nothing outstanding');
insert into labelslesserstatesreasonlabels (label, reason) values ('accepted','no objections');
insert into labelslesserstatesreasonlabels (label, reason) values ('final','completed implementation ');
insert into labelslesserstatesreasonlabels (label, reason) values ('ready for review','completed implementation ');

-- no reasons captured for withdrawn, superceded, %poll%, %vot%


select * from labelslesserstatesreasonlabels order by label asc;

-- find multiples labels captured same message
select pep, messageid, clausie  
from results 
where clausie IS NOT NULL 
AND pep > 100
order by pep,messageid, clausie

select * from results where pep = 308 and length(clausie) >0 order by datetimestamp
select * from results_postprocessed where pep = 308  order by timestamp
-- 