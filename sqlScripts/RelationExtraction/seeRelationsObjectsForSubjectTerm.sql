SET @term = '%poll%';    

SELECT * from labelextraction_sub where arg1 like @term or arg2 like @term
union
SELECT * from labelextraction_ob where arg1 like @term or arg2 like @term