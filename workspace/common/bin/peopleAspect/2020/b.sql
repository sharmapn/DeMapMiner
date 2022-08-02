SELECT * FROM results_postprocessed

SELECT folder, COUNT(pep) FROM results
-- _postprocessed
GROUP BY folder