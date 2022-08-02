select * from labels where idea like '%no_one%'
select * from results where label like '%pref%'
select * from labels where idea like '%bdfl%'
select * from labels where idea like '%poll%'
select distinct linenumber from labels where idea like '%bdfl%'
select * from labels where subject = 'i' or object = 'i'

-- changed no_on_spoke_for to n_support 783 

insert into labels (linenumber,idea, subject, verb, object, sentence, tocheck) 
values(2001,'bdfl_override_community_consensus','no explanation','offered','guido_decide_his_favourite','this became a reason for Guido to just decide on his original favorite',1);

-- jan 2019
-- pep 255 poll sentnce An informal poll of PythonLabs indicates a split on this subject, perhaps setting Jeremy up as a Sandra Day O'Conner swing vote.  But
-- who said this was a democracy anyway? :)
-- adding '"An informal poll of PythonLabs"	"indicates"	"a split on this subject"'
insert into labels (linenumber,idea, subject, verb, object, sentence, tocheck) 
values(2001,'poll_split','poll','indicates','split','An informal poll of PythonLabs indicates a split on this subject, perhaps setting Jeremy up as a Sandra Day O\'Conner swing vote.',1);
-- pep 308 poll, yes there is a poll also
insert into labels (linenumber,idea, subject, verb, object, sentence, tocheck) 
values(2001,'poll','i','took','split','I took a quick simple poll (of those still bothering to follow the PEP 308 discussion). ',1);

insert into labels (linenumber,idea, subject, verb, object, sentence, tocheck) 
values(2003,'poll_results','i','closed','poll','I have closed the issue of file name formatting thanks to the informal poll results being very clear on the
preferred format and also closed the idea of embedding the optimization level in the bytecode file metadata (that can be another PEP if someone
cares to write that one)',1);

-- "I" "do n't know" "the state of community consensus regarding this PEP"
insert into labels (linenumber,idea, subject, verb, object, sentence, tocheck) 
values(2004,'consensus_not_known','i','do_nt_know','community_consensus','I\'ve not asked for a vote and so I don\'t know the state of community consensus regarding this PEP.)',1);

insert into labels (linenumber,idea, subject, verb, object, sentence, tocheck) 
values(2005,'vote','you','please','indicate_vote','So, can you please indicate your vote for or against incorporating PEP 391 into Python? )',1);

insert into labels (linenumber,idea, subject, verb, object, sentence, tocheck) 
values(2006,'poll_results','i','had','votes_results','I had 19 people casting votes, with the following results:',1);

insert into labels (linenumber,idea, subject, verb, object, sentence, tocheck) 
values(2007,'poll_results','i','have','poll_results','I have closed the issue of file name formatting thanks to the informal poll results being very clear on the
preferred format and also closed the idea of embedding the optimization level in the bytecode file metadata (that can be another PEP if someone
cares to write that one)',1);

insert into labels (linenumber,idea, subject, verb, object, sentence, tocheck) 
values(2008,'poll','poll','shows','proposal','A quick poll during my keynote presentation at PyCon 2007 shows this proposal has no popular support.',1);
-- Before I send generator comprehension portion to Guido for 
-- pronouncement, I would like to get all of your votes +1 or -1 on just =
-- the part about Generator Comprehensions.
insert into labels (linenumber,idea, subject, verb, object, sentence, tocheck) 
values(2009,'vote','i','would_like','your_votes','Before I send generator comprehension portion to Guido for pronouncement, I would like to get all of your votes +1 or -1 on just 
							the part about Generator Comprehensions',1);
-- "the PEP"	"was rejected"	"due to a lack of an overwhelming majority"
insert into labels (linenumber,idea, subject, verb, object, sentence, tocheck) 
values(2011,'no_majority','pep','rejected','lack_majority','Accordingly, the PEP was  rejected due to a lack of an overwhelming majority for change.',1);

insert into labels (linenumber,idea, subject, verb, object, sentence, tocheck) 
values(2013,'private_discussion','it','became','private_discussion','Peter Moody actually addressed all my comments from last year (alas, I 
forgot that python-ideas got dropped from the latter part of the email chain, so it became a private discussion between Peter, Guido and myself)..',1);

-- Below is a new PEP based on discussions from the stdlib-sig, which proposes to add the argparse module to the standard library in Python 2.7 and 3.2.
-- Pep_idea "a new PEP" "be based" "on discussions from the stdlib-sig"
insert into labels (linenumber,idea, subject, verb, object, sentence, tocheck) 
values(2014,'pep_idea','new_pep','based','discussions_stdlib-sig','Below is a new PEP based on discussions from the stdlib-sig, 
which proposes to add the argparse module to the standard library in Python 2.7 and 3.2.',1);

-- "this PEP"	"be discussed"	"for one more round here then rejected by the highest authority so we can move on to the final changes those are the next PEP"
insert into labels (linenumber,idea, subject, verb, object, sentence, tocheck) 
values(2015,'another_round_discussion','pep','be_discussed','one_more_round','So I am proposing to see this PEP discussed for one more round here,
then rejected or approved by the highest authority  :) so we can move on to the final changes we are planning
on PEP 345 and PEP 376 (those are the next PEP we want to propose here)',1);
-- "This PEP"	"is made"	"obsolete by the acceptance + _ + of pep 461 +"
insert into labels (linenumber,idea, subject, verb, object, sentence, tocheck) 
values(2017,'alternative_pep','pep','made','obsolete_acceptance','This PEP is made obsolete by the `acceptance
+<https://mail.python.org/pipermail/python-dev/2014-March/133621.html>_+of :pep:`461`, which introduces a more extended formatting language for
+bytes objects in conjunction with the modulo operator.',1);

-- Substantially all of its benefits were subsumed by generator expressions coupled with the dict() constructor.
insert into labels (linenumber,idea, subject, verb, object, sentence, tocheck) 
values(2018,'alternative_pep','its_benefits','were_coupled','with-the','Substantially all of its benefits were subsumed by generator expressions coupled with the dict() constructor.',1);
-- pep 274 
insert into labels (linenumber,idea, subject, verb, object, sentence, tocheck) 
values(2019,'alternative_pep','it','was_needed','no_longer','After genexps were introduced, it was no longer needed.',1);

-- +This PEP is rejected by its author, because it has been superseded by the new +importlib.
-- "it"	"has been superseded"	"by the new + importlib"
insert into labels (linenumber,idea, subject, verb, object, sentence, tocheck) 
values(2020,'alternative_pep','it','has_been_superseded','the_new','This PEP is rejected by its author, because it has been superseded by the new +importlib.',1);

-- After an off-list discussion with Victor I have decided to reject PEP 410.
insert into labels (linenumber,idea, subject, verb, object, sentence, tocheck) 
values(2020,'private_discussion','i','have_decided','off-list discussion','This PEP is rejected by its author, because it has been superseded by the new +importlib.',1);
-- "the majority of Python users" "deem" "this to be a nice-to-have feature"
insert into labels (linenumber,idea, subject, verb, object, sentence, tocheck) 
values(2021,'majority','majority_users','deem','this_nice_feature','While the majority of Python users deem this to be a nice-to-have feature, 
the community has been unable to reach a consensus on the proper syntax after more than two years of intensive debate (the PEP was introduced in early April 2003).',1);

-- "little interest"	"there seemed"	"from the Python community 
insert into labels (linenumber,idea, subject, verb, object, sentence, tocheck) 
values(2025,'no_support','little_interest','seemed','python_community',' However, there seemed little interest from the Python community,
and time was lacking, so this PEP has been deferred to some future Python release..',1);

-- "This PEP"	"has been withdrawn"	"in favor of PEP 3141"
insert into labels (linenumber,idea, subject, verb, object, sentence, tocheck) 
values(2026,'alternate_pep','pep','has_withdrawn','in_favor_pep',' This PEP has been withdrawn in favor of PEP 3141.',1);

-- Don't worry about PEP 241 - it has been superceded by PEP 314.
-- "it"	"has been superceded"	"by PEP 314"
insert into labels (linenumber,idea, subject, verb, object, sentence, tocheck) 
values(2027,'alternate_pep','it','has_superceded','by_pep','  Don\'t worry about PEP 241 - it has been superceded by PEP 314. ',1);

-- There was no opposition to the proposal but only mild interest in using =
-- it, not enough to justify adding the module to the standard library. =
-- Instead, it will be made available as a separate distribution item at the Numerical Python site.
-- "mild interest in using = it not enough to justify adding the module to the standard library = Instead"	"There was"
insert into labels (linenumber,idea, subject, verb, object, sentence, tocheck) 
values(2028,'not_enough_support','mild_interest','not_enough','was','  There was no opposition to the proposal but only mild interest in using  
				it, not enough to justify adding the module to the standard library. ',1);

-- No-one spoke up for it (and some against), so it is now rejected.
-- "No-one"	"spoke"	"for it it is now rejected"
insert into labels (linenumber,idea, subject, verb, object, sentence, tocheck) 
values(2029,'not_enough_support','no_one','spoke','for_it','No-one spoke up for it (and some against), so it is now rejected.',1);

-- If anyone has objections to Michael Hudson's PEP 264: 
--    Future statements in simulated shells
--    http://python.sourceforge.net/peps/pep-0264.html
-- raise them now. 
-- "anyone"	"has"	"objections to Michael Hudson 's PEP 264"
insert into labels (linenumber,idea, subject, verb, object, sentence, tocheck) 
values(2030,'lazy_consensus','anyone','has','objections_pep','If anyone has objections to Michael Hudson\'s PEP 264: ',1);

-- +This PEP has been rejected. It has failed to generate sufficient
-- +community support in the six years since its proposal.
-- "It"	"has failed"	"to generate sufficient + community support in the six years since its proposal"
insert into labels (linenumber,idea, subject, verb, object, sentence, tocheck) 
values(2031,'not_enough_support','it','has','failed','This PEP has been rejected. It has failed to generate sufficient +community support in the six years since its proposal. ',1);

-- This PEP is rejected for failure to generate significant interest..
-- "This PEP"	"is rejected"	"to generate significant interest"
insert into labels (linenumber,idea, subject, verb, object, sentence, tocheck) 
values(2032,'not_enough_support','pep','rejected','significant_interest','This PEP is rejected for failure to generate significant interest. ',1);

-- After nine months, no support has grown beyond the original poster. 
-- "no support"	"has grown"	"beyond the original poster"
insert into labels (linenumber,idea, subject, verb, object, sentence, tocheck) 
values(2033,'not_enough_support','no_support','has_grown','beyond_the','After nine months, no support has grown beyond the original poster.  ',1);

-- Does anybody see any last-minute show-stopping problems with it? 
-- "anybody"	"see"	"any last-minute show-stopping problems with it"
insert into labels (linenumber,idea, subject, verb, object, sentence, tocheck) 
values(2034,'lazy_consensus','anybody','see','any_problems_with_it','Does anybody see any last-minute show-stopping problems with it?',1);

-- Also, the PEP has generated no widespread interest.
-- "the PEP"	"has generated"	"no widespread interest Also"
insert into labels (linenumber,idea, subject, verb, object, sentence, tocheck) 
values(2035,'not_enough_support','pep','generated','widespread_interest','Also, the PEP has generated no widespread interest.',1);

-- If no one has any other arguments to the contrary, I'll run with the new API naming scheme.
-- "no one"	"has"	"any other arguments to the contrary"
insert into labels (linenumber,idea, subject, verb, object, sentence, tocheck) 
values(2036,'lazy_consensus','no_one','has','any_arguments','If no one has any other arguments to the contrary, I\'ll run with the new API naming scheme.',1);

-- this one was unextractable
-- There have been some comments in favour of the proposal, no +objections to the proposal as a whole, and some questions and
-- +objections about specific details.
insert into labels (linenumber,idea, subject, verb, object, sentence, tocheck) 
values(2037,'lazy_consensus','no_one','has','any_arguments','There have been some comments in favour of the proposal, no +objections to the proposal as a whole, and some questions and 
 +objections about specific details.',1);
 
 -- Here is the last call for any comments or arguments against approval, before Guido marks the PEP accepted (or changes his mind :-)
 -- "the last call for any comments against approval before Guido marks the PEP accepted or changes his mind"	"is"	"Here"
insert into labels (linenumber,idea, subject, verb, object, sentence, tocheck) 
values(2038,'lazy_consensus','last_call_for_comments','is','here','Here is the last call for any comments or arguments against approval, before Guido marks the PEP accepted (or changes his mind :-).',1);

-- If anyone has any issues with the PEP, please speak up before then!
-- "anyone"	"has"	"any issues with the PEP"
insert into labels (linenumber,idea, subject, verb, object, sentence, tocheck) 
values(2039,'lazy_consensus','anyone','has','any_isues','If anyone has any issues with the PEP, please speak up before then!',1);

-- If there are no major objections, I intend to take it to python-dev in a day or two for discussion and a ruling.
-- "no major objections"	"there are"
insert into labels (linenumber,idea, subject, verb, object, sentence, tocheck) 
values(2040,'lazy_consensus','no_major_objections','there are','','If there are no major objections, I intend to take it to python-dev in a day or two for discussion and a ruling.',1);

-- There wasn't enough support in favor, the +feature to be removed isn't all that harmful, and there are some use +cases that would become harder.
-- "enough support in favor"	"There was n't"	"the + feature to be removed is n't all that harmful"
insert into labels (linenumber,idea, subject, verb, object, sentence, tocheck) 
values(2041,'lazy_consensus','enough_support_in_favor','there was','feature','There wasn\'t enough support in favor, the +feature to be removed isn\'t all that harmful, and there are some use +cases that would become harder.',1);

-- If there's no further opposition, I'd like to mark this PEP accepted (or let someone else do it) in 24 hours, so that the implementation can be integrated before Sunday.
-- "no further opposition"	"there 's"
insert into labels (linenumber,idea, subject, verb, object, sentence, tocheck) 
values(2042,'lazy_consensus','no_further_opposition','there','','If there\'s no further opposition, I\'d like to mark this PEP accepted (or let someone else do it) in 24 hours, so that the implementation can be integrated before Sunday.',1);

-- I haven't seen any strong objections, so I would like to go ahead and
-- "I"	"have n't seen"	"any strong objections"
insert into labels (linenumber,idea, subject, verb, object, sentence, tocheck) 
values(2043,'lazy_consensus','i','have_seen','any_objections', 'I haven\'t seen any strong objections, so I would like to go ahead and',1);

-- "election method"	"to use"	"which to vote on the PEP"
insert into labels (linenumber,idea, subject, verb, object, sentence, tocheck) 
values(2044,'voting_method_enquiry','election_method','to_use','vote_pep', 'Now we just have to figure out which election method to use to vote for
which election method to use to vote on the PEP and we\'ll be done! ',1);

-- "anyone"	"have"	"actual experience with approval voting"
insert into labels (linenumber,idea, subject, verb, object, sentence, tocheck) 
values(2046,'voting_method_enquiry','anyone','have','approval voting', 'Does anyone have actual experience with both Condorcet and approval voting? ! ',1);

-- I don't see how approval voting is going to help us. 

select distinct idea from labels where idea like '%lazy%'
select * from labels where idea = 'no consensus'
select * from labels where idea like '%lazy_consensus%'
select *  from allmessages where pep = 3155 and email like '%any strong objections%';
select *  from allmessages where pep = 433 and subject like '%second draft%'
select * from results;
select * from labels where tocheck=0;
select * from labels where idea like '%voting%'
select distinct idea from labels where idea like '%voting%'
select * from allmessages where pep = 308 and email like '%anybody%' and email like '%have%' and email like '%voting%' order by date2
select * from allmessages where pep = 308 and email like '%Does anyone have actual experience with both Condorce%'
select * from results
select * from results_jan where messageid = 1194492
select * from results_jan where clausie like '%round%'
-- delete from results_jan where pep = 308 and messageid = 261760;
insert into results_jan select * from results where pep = 308 and messageid = 261760;