select count(*) from allmessages where pep = 201 or pep = 202

select pep,date,label,currentsentence from results_postprocessed;

select pep,arg1,relation,arg2,sentence from extractedrelations_clausie_reasontriples
where arg1 like '%accept%' OR arg2 like '%accept%';