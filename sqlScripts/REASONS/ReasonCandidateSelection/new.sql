select id,state, label, pep,messageid, causesentence, effectsentence, consider from trainingdata 
where pep = 3138
order by pep

select * from autoextractedreasoncandidatemessages where currentsentence like '%I just pushed%'

select distinct proposal from autoextractedreasoncandidatemessages

select * from autoextractedreasoncandidatemessages where proposal = 240

select * from allmessages where pep = 508 and email like '%gone through%'
where messageid = 409846
limit 5
select * from allmessages where pep = -1 and email like '%gone through%'
select * from allmessages where pep= 507 and messageid = 412186
select * from allmessages where messageid = 203932

select * from trainingdata where pep = 201

select proposal,messageid, datediff, sentence,reasonLabelFoundUsingTripleExtractionProbabilityScore  from autoextractedreasoncandidatesentences 
where reasonLabelFoundUsingTripleExtractionProbabilityScore > 0
order by proposal

select proposal,messageid, datediff, sentence,SameMsgSubAsStateTriple  from autoextractedreasoncandidatesentences 
where SameMsgSubAsStateTriple > 0
order by proposal



SELECT * FROM RESULTS _postprocessedpeps_newpeps_new