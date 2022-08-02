-- select * from states_reason_reasonsublabels
insert into states_reason_reasonsublabels (state, stateNotes) VALUES ('Have to relook later','More work is required to discover the reasons');
insert into states_reason_reasonsublabels (state, reason) VALUES ('Accepted', 'None');
insert into states_reason_reasonsublabels (state, reason) VALUES ('Accepted', 'Not Stated');
insert into states_reason_reasonsublabels (state, reason) VALUES ('Accepted', 'Basic');
insert into states_reason_reasonsublabels (state, reason) VALUES ('Accepted', 'No Community Status');
-- Accepted Consensus related
insert into states_reason_reasonsublabels (state, reason, reasonNotes, subreason, subReasonNotes) VALUES ('Accepted', 
'Community Consensus','Proposal accepted because there was consensus in teh community that they should accept',
'Consensus','Community was in consensus');
insert into states_reason_reasonsublabels (state, reason, reasonNotes, subreason, subReasonNotes) VALUES ('Accepted', 
'Community Consensus','Proposal accepted because there was consensus in teh community that they should accept',
'Lazy Consensus','(Also called lazy approval.) A decision-making policy which assumes general consent if no responses are posted within a defined period. For example, Im going to commit this by lazy consensus if no-one objects within the next three days. ');
insert into states_reason_reasonsublabels (state, reason, reasonNotes) VALUES ('Accepted', 
'Majority Approval','Also called lazy majority approaval.) Refers to a vote (sense 1) which has completed with at least three binding +1 votes and more +1 votes than -1 votes. ( I.e. , a simple majority with a minimum quorum of three positive votes.)');
-- insert into states_reason_reasonsublabels (state, reason) VALUES ('Accepted', 'No Community Consensus');
-- insert into states_reason_reasonsublabels (state, reason) VALUES ('Accepted', 'Little interest');
insert into states_reason_reasonsublabels (state, reason, reasonNotes) VALUES ('Accepted','BDFL Pronouncement over Community Split','There was a disagreement and the BDFL/Delegate resolved it by accepting');
insert into states_reason_reasonsublabels (state, reason, reasonNotes) VALUES ('Accepted','BDFL Pronouncement over Consensus','There was a community consensus against the proposal (pep or syntax) but BDFL overuled and accepted');

-- Rejected
insert into states_reason_reasonsublabels (state, reason) VALUES ('Rejected', 'None');
insert into states_reason_reasonsublabels (state, reason) VALUES ('Rejected', 'Not Stated');
insert into states_reason_reasonsublabels (state, reason) VALUES ('Rejected', 'Basic');
insert into states_reason_reasonsublabels (state, reason) VALUES ('Rejected', 'No Community Status');
insert into states_reason_reasonsublabels (state, reason, reasonNotes) VALUES ('Rejected', 'Community Consensus','Rejected because everyone (maybe prominent ones only) community agreed it should be');
insert into states_reason_reasonsublabels (state, reason, reasonNotes) VALUES ('Rejected', 'No Community Consensus','Community couldnt reach a consensus - there was a disagreement');
insert into states_reason_reasonsublabels (state, reason, reasonNotes) VALUES ('Rejected', 'Little/No interest in the commmunity','Not much support');
-- Rejected Consensus related
insert into states_reason_reasonsublabels (state, reason, reasonNotes, subreason, subReasonNotes) VALUES ('Rejected', 
'Community Consensus','Proposal rejected because there was consensus in teh community that they should reject',
'Consensus','Community was in consensus');
insert into states_reason_reasonsublabels (state, reason, reasonNotes, subreason, subReasonNotes) VALUES ('Rejected', 
'Community Consensus','Proposal rejected because there was consensus in teh community that they should reject',
'Lazy Consensus','(Also called lazy approval.) A decision-making policy which assumes general consent if no responses are posted within a defined period. For example, Im going to commit this by lazy consensus if no-one objects within the next three days. ');
insert into states_reason_reasonsublabels (state, reason, reasonNotes) VALUES ('Rejected', 
'Majority DisApproval','Also called lazy majority approval.) Refers to a vote (sense 1) which has completed with at least three binding +1 votes and more +1 votes than -1 votes. ( I.e. , a simple majority with a minimum quorum of three positive votes.)');
insert into states_reason_reasonsublabels (state, reason) VALUES ('Rejected', 'BDFL Pronouncement over Community Split');
insert into states_reason_reasonsublabels (state, reason) VALUES ('Rejected', 'BDFL pronouncement over Consensus');
insert into states_reason_reasonsublabels (state, reason) VALUES ('Rejected', 'BDFL Decree'); -- what was the community view?
insert into states_reason_reasonsublabels (state, reason) VALUES ('Rejected', 'BDFL pronouncement decided obsolete'); -- what was the community view?
insert into states_reason_reasonsublabels (state, reason) VALUES ('Rejected', 'Superseded');
insert into states_reason_reasonsublabels (state, reason,reasonNotes) VALUES ('Rejected', 'Problems with PEP','also proposals which are not clear or not good enough'); -- who decided this..BDFL or community?
insert into states_reason_reasonsublabels (state, reason) VALUES ('Rejected', 'Obsolete');
insert into states_reason_reasonsublabels (state, reason) VALUES ('Rejected', 'Author not reachable');
insert into states_reason_reasonsublabels (state, reason) VALUES ('Rejected', 'Self rejected by Author');
insert into states_reason_reasonsublabels (state, reason) VALUES ('Rejected', 'PEP benefits marginal'); 
insert into states_reason_reasonsublabels (state, reason) VALUES ('Rejected', 'PEP benefits marginal');
insert into states_reason_reasonsublabels (state, reason) VALUES ('Rejected', 'Requires Significant changes');	

-- QUESTION: For rejected, does Little interest mean the same thing as rejected by lazy consensus?

-- concepts borrowed from http://www.apache.org/foundation/glossary.html#LazyConsensus
-- and https://blogs.apache.org/comdev/entry/how_apache_projects_use_consensus
-- here https://guides.shiftbase.net/lazy-majority-vote/ and
-- MAIN ONE -- http://oss-watch.ac.uk/resources/meritocraticgovernancevoting
-- Type	Description	Duration
-- Lazy consensus -	An action with lazy consensus is implicitly allowed, unless a binding -1 vote is received. Depending on the type of action, a vote will then be called. Note that even though a binding -1 is required to prevent the action, all community members are encouraged to cast a -1 vote with supporting argument. Committers are expected to evaluate the argument and, if necessary, support it with a binding -1.	N/A
-- Lazy majority -	A lazy majority vote requires more binding +1 votes than binding -1 votes.	72 hours
-- Consensus approval - Consensus approval requires three binding +1 votes and no binding -1 votes.	72 hours
-- Unanimous consensus - All of the binding votes that are cast are to be +1 and there can be no binding vetoes (-1).	120 hours
-- 2/3 majority - Some strategic actions require a 2/3 majority of PMC members; in addition, 2/3 of the binding votes cast must be +1. Such actions typically affect the foundation of the project (e.g. adopting a new codebase to replace an existing product).	120 hours

-- withdrawn , superseded is a reason for withdrawal, but there aint any reasons for superseded...its straightforwrd I guess
insert into states_reason_reasonsublabels (state, reason) VALUES ('Withdrawn', 'Superseded');
insert into states_reason_reasonsublabels (state, reason) VALUES ('Withdrawn', 'Authors request'); -- but why -- 379
insert into states_reason_reasonsublabels (state, reason) VALUES ('Withdrawn', 'No Longer Valid'); -- pep 369
insert into states_reason_reasonsublabels (state, reason,reasonNotes) VALUES ('Withdrawn', 'No Community Consensus','Community couldnt reach a consensus - there was a disagreement'); -- pep 103
insert into states_reason_reasonsublabels (state, reason,reasonNotes) VALUES ('Withdrawn', 'Little interest in the commmunity','Not much support');
insert into states_reason_reasonsublabels (state, reason) VALUES ('Withdrawn', 'BDFL Didnt like');  -- pep 359
-- deferred
insert into states_reason_reasonsublabels (state, reason) VALUES ('Deferred', 'Lack of champion');
insert into states_reason_reasonsublabels (state, reason) VALUES ('Deferred', 'Waiting for update from author');	-- 233
insert into states_reason_reasonsublabels (state, reason) VALUES ('Deferred', 'Implementation too complex');	-- 431
insert into states_reason_reasonsublabels (state, reason) VALUES ('Deferred', 'Requires Significant changes');	-- 256
insert into states_reason_reasonsublabels (state, reason) VALUES ('Deferred', 'May Never get finished'); -- pep 210
insert into states_reason_reasonsublabels (state, reason,reasonNotes) VALUES ('Deferred', 'Little interest in the commmunity','Not much support');	-- pep 222
-- This may be important, but for draft I have to really search this out
insert into states_reason_reasonsublabels (state, reason) VALUES ('Draft', '');
insert into states_reason_reasonsublabels (state, reason) VALUES ('Active', '');
-- superseded would have no further reason
insert into states_reason_reasonsublabels (state, reason) VALUES ('Superseded', '');



