-- bdfl pep suthor for rq 2.1 in thesis
SELECT TYPE, COUNT(distinct pep) FROM pepdetails
WHERE created < '2017-03-01'
AND author LIKE '%guido%' 
GROUP BY TYPE
"TYPE"	"COUNT(pep)"
"Informational"	"7"
"Process"	"4"
"Standards Track"	"29"

-- get totals peps discuseed
SELECT TYPE, COUNT(distinct pep) FROM pepdetails
WHERE created < '2017-03-01'
GROUP BY TYPE
-- 428

"TYPE"	"COUNT(distinct pep)"
"Informational"	"57"
"Process"	"40"
"Standards Track"	"331"


WHERE pep NOT IN (12, 13, 487, 519, 526, 527, 528, 529, 543, 547, 548, 549, 550, 551, 552, 553, 554, 555,
					 556, 557, 558, 559, 560, 561, 562, 563, 564, 565, 566, 567, 568, 569, 570, 571, 572, 573, 574, 575, 
					 576, 577, 578, 579, 580, 581, 582, 801, 3143, 8000, 8001, 8002, 8010, 8011, 8012, 8013, 8014, 8015)
					 
SELECT * FROM pepDMContributionsByRole