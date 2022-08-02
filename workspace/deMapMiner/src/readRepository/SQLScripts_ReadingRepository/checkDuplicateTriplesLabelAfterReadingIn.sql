-- find if there are duplicate triples for the same label when lables are read into db from input file


select   linenumber,idea,
         subject,verb, object,
         count(*)
from     labels
group by idea,subject, verb, object
having   count(*) > 1