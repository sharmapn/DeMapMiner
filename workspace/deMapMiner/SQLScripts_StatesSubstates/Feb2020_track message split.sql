-- DROP TABLE trackmessagesplit
create TABLE trackmessagesplit
(
	messageid INT,
	pep INT,
	splitmarker TEXT, 
	-- can be 'smp', 'tab' or multiple 'peps'
	splitforpep INT,
	anycandidatefound boolean
)

SELECT * FROM trackmessagesplit

-- track for each sentence if we found mltiple peps
-- only other peps and multi peps
-- DROP TABLE tracksentencemultiplePEPs
create TABLE tracksentencemultiplePEPs
(
	messageid INT,
	originalpep INT,
	sentence TEXT, 
	-- can be 'smp', 'tab' or multiple 'peps'
	path INT,
	anycandidatefound boolean
)

