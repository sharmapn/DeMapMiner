select * from peps.relations where relations.arg1 like '%pep%'
                                or relations.arg2 like '%pep%'
                                or relations.relation like '%accept%';
                                
                              --  select kw.keyword, count(*)
-- from t cross join
--     keywords kw
  --   on concat(', ', t.title, ',') like concat(', ', kw.keyword, ',')
     
   --  http://stackoverflow.com/questions/11209297/how-to-find-most-common-words-in-a-mysql-database-table-column