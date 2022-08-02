-- Jan 2019, Main extraction for remaining states/substates

select distinct allverbsinmsgsub,allnounsinmsgsub from extractedrelations_clausie
where allnounsinmsgsub like '%pep%'
order by allverbsinmsgsub asc

select distinct allverbsinmsgsub,allnounsinmsgsub from extractedrelations_clausie
where allnounsinmsgsub like '%proposal%'
order by allverbsinmsgsub asc

-- First example, using term 'alternative' MAIN ONE 
select distinct allverbsinmsgsub,allnounsinmsgsub from extractedrelations_clausie
where allnounsinmsgsub like '%pep%'
order by allverbsinmsgsub asc
-- recursive level one
select pep, arg1,relation, arg2,sentence from extractedrelations_clausie
where arg1 like '%alternative pep%' or arg2 like '%alternative pep%'
or arg1 like '%pep alternative%' or arg2 like '%pep alternative%'
order by arg1, relation,arg2 asc

-- First example, using term 'alternative'
select distinct allverbsinmsgsub,allnounsinmsgsub from extractedrelations_clausie
where allnounsinmsgsub like '%draft%'
order by allverbsinmsgsub asc
-- recursive level one
select pep, arg1,relation, arg2,sentence from extractedrelations_clausie
where arg1 like '%draft pep%' or arg2 like '%draft pep%'
or arg1 like '%pep draft%' or arg2 like '%pep draft%'
order by arg1, relation,arg2 asc