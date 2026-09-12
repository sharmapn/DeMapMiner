USE bips_2026;
-- landmark acts per year: decision-closing sentences by decision-makers
SELECT y, proposal_number, author_name, author_role, influence_score, influence_types, LEFT(sentence,210) s FROM (
 SELECT YEAR(message_date) y, proposal_number, author_name, author_role, influence_score, influence_types, sentence,
        ROW_NUMBER() OVER (PARTITION BY YEAR(message_date) ORDER BY influence_score DESC, LENGTH(sentence)) rk
 FROM influence_candidates WHERE message_date IS NOT NULL AND author_role IN ('lead maintainer','maintainer','BIP editor')
   AND (FIND_IN_SET('unilateral',influence_types)>0 OR FIND_IN_SET('gatekeeping',influence_types)>0 OR FIND_IN_SET('exit_threat',influence_types)>0 OR FIND_IN_SET('backchannel',influence_types)>0)
   AND LENGTH(sentence) BETWEEN 30 AND 210) t WHERE rk<=5 ORDER BY y, rk;
-- highest-scoring examples per base mechanism (two each, different actors)
SELECT mech, proposal_number, author_name, author_role, influence_score, LEFT(sentence,200) s FROM (
 SELECT m.mech, c.proposal_number, c.author_name, c.author_role, c.influence_score, c.sentence,
        ROW_NUMBER() OVER (PARTITION BY m.mech ORDER BY c.influence_score DESC, RAND(11)) rk
 FROM influence_candidates c JOIN (SELECT 'strategic' mech UNION SELECT 'operational' UNION SELECT 'functional' UNION SELECT 'tactical' UNION SELECT 'authority' UNION SELECT 'compatibility' UNION SELECT 'security' UNION SELECT 'standards' UNION SELECT 'ecosystem' UNION SELECT 'economic' UNION SELECT 'organizational' UNION SELECT 'coalition' UNION SELECT 'user_demand') m ON FIND_IN_SET(m.mech, c.influence_types)>0
 WHERE LENGTH(c.sentence) BETWEEN 50 AND 200 AND c.influence_direction <> 'neutral') t WHERE rk<=6 ORDER BY mech, rk;
-- controversial examples
SELECT mech, proposal_number, author_name, author_role, YEAR(message_date) y, influence_score, LEFT(sentence,210) s FROM (
 SELECT m.mech, c.proposal_number, c.author_name, c.author_role, c.message_date, c.influence_score, c.sentence,
        ROW_NUMBER() OVER (PARTITION BY m.mech ORDER BY c.influence_score DESC, RAND(5)) rk
 FROM influence_candidates c JOIN (SELECT 'unilateral' mech UNION SELECT 'corporate_interest' UNION SELECT 'gatekeeping' UNION SELECT 'exit_threat' UNION SELECT 'incivility' UNION SELECT 'backchannel' UNION SELECT 'procedural_control') m ON FIND_IN_SET(m.mech, c.influence_types)>0
 WHERE LENGTH(c.sentence) BETWEEN 40 AND 210) t WHERE rk<=10 ORDER BY mech, rk;
-- role x era (lead maintainer share etc.)
SELECT CASE WHEN message_date < '2014-04-08' THEN '1' WHEN message_date < '2017-08-24' THEN '2' WHEN message_date < '2022-02-01' THEN '3' ELSE '4' END era, author_role, COUNT(*) n FROM influence_candidates WHERE message_date IS NOT NULL GROUP BY 1,2 ORDER BY 1, n DESC;
-- direction by role
SELECT author_role, COUNT(*) n, ROUND(100*SUM(influence_direction='supporting')/COUNT(*),1) supp, ROUND(100*SUM(influence_direction='blocking')/COUNT(*),1) blk, ROUND(100*SUM(influence_direction='revising')/COUNT(*),1) rev FROM influence_candidates GROUP BY 1 ORDER BY n DESC;
-- preference overlap
SELECT COUNT(*) FROM information_schema.tables WHERE table_schema='bips_2026' AND table_name='preference_candidates';
-- decision-date coverage
SELECT SUM(days_before_decision IS NOT NULL) known, COUNT(*) n, SUM(days_before_decision BETWEEN 0 AND 14) two_weeks_before, SUM(days_before_decision BETWEEN -14 AND -1) two_weeks_after FROM influence_candidates;
