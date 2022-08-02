select * from topicModellingSentences limit 10
ALTER TABLE topicmodellingsentences ADD `id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY

SELECT id, sentence from topicModellingSentences 
where proposal = 272

select * from trainingdata where pep = 285