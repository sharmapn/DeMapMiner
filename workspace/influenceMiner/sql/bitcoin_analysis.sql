USE bips_2026;
-- 0. corpus coverage
SELECT COUNT(DISTINCT messageID) messages, COUNT(DISTINCT CASE WHEN bip>0 THEN messageID END) bip_msgs, COUNT(DISTINCT CASE WHEN bip>0 THEN bip END) bips,
       COUNT(DISTINCT clusterBySenderFullName) senders FROM allmessages;
-- 1. candidates overall
SELECT COUNT(*) n, COUNT(DISTINCT proposal_number) bips, COUNT(DISTINCT author_name) actors, SUM(controversial) contro, ROUND(100*SUM(controversial)/COUNT(*),1) pct_contro,
       ROUND(AVG(LENGTH(influence_types)-LENGTH(REPLACE(influence_types,',',''))+1),2) types_per_cand FROM influence_candidates;
-- 2. mechanism shares (all 20)
SELECT m.mech, SUM(FIND_IN_SET(m.mech, c.influence_types)>0) n, ROUND(100*SUM(FIND_IN_SET(m.mech, c.influence_types)>0)/COUNT(*),2) pct
FROM influence_candidates c JOIN (SELECT 'strategic' mech UNION SELECT 'operational' UNION SELECT 'functional' UNION SELECT 'tactical' UNION SELECT 'authority' UNION SELECT 'compatibility' UNION SELECT 'security' UNION SELECT 'standards' UNION SELECT 'ecosystem' UNION SELECT 'economic' UNION SELECT 'organizational' UNION SELECT 'coalition' UNION SELECT 'user_demand' UNION SELECT 'unilateral' UNION SELECT 'corporate_interest' UNION SELECT 'gatekeeping' UNION SELECT 'exit_threat' UNION SELECT 'incivility' UNION SELECT 'backchannel' UNION SELECT 'procedural_control') m
GROUP BY m.mech ORDER BY n DESC;
-- 3. direction / scope / phase / outcome
SELECT influence_direction, COUNT(*) n, ROUND(100*COUNT(*)/(SELECT COUNT(*) FROM influence_candidates),1) pct FROM influence_candidates GROUP BY 1 ORDER BY n DESC;
SELECT influence_scope, COUNT(*) n, ROUND(100*COUNT(*)/(SELECT COUNT(*) FROM influence_candidates),1) pct FROM influence_candidates GROUP BY 1 ORDER BY n DESC;
SELECT decision_phase, COUNT(*) n, ROUND(100*COUNT(*)/(SELECT COUNT(*) FROM influence_candidates),1) pct FROM influence_candidates GROUP BY 1 ORDER BY n DESC;
SELECT final_decision, COUNT(*) n, COUNT(DISTINCT proposal_number) bips, ROUND(100*SUM(influence_direction='supporting')/COUNT(*),1) supp, ROUND(100*SUM(influence_direction='blocking')/COUNT(*),1) blk, ROUND(100*SUM(controversial)/COUNT(*),1) contro FROM influence_candidates GROUP BY 1 ORDER BY n DESC;
-- 4. roles
SELECT author_role, COUNT(*) n, ROUND(100*COUNT(*)/(SELECT COUNT(*) FROM influence_candidates),1) pct, COUNT(DISTINCT author_name) people, ROUND(AVG(influence_score),2) avg_score,
  ROUND(100*SUM(FIND_IN_SET('authority',influence_types)>0)/COUNT(*),1) authority, ROUND(100*SUM(FIND_IN_SET('security',influence_types)>0)/COUNT(*),1) security,
  ROUND(100*SUM(FIND_IN_SET('economic',influence_types)>0)/COUNT(*),1) economic, ROUND(100*SUM(FIND_IN_SET('ecosystem',influence_types)>0)/COUNT(*),1) ecosystem,
  ROUND(100*SUM(controversial)/COUNT(*),1) contro FROM influence_candidates GROUP BY 1 ORDER BY n DESC;
-- 5. top actors
SELECT author_name, author_role, COUNT(*) n, COUNT(DISTINCT proposal_number) bips, ROUND(AVG(influence_score),2) avg_score FROM influence_candidates GROUP BY 1,2 ORDER BY n DESC LIMIT 15;
-- 6. eras: Gavin (2011-06..2014-04), block-size war (2014-04..2017-08), Wladimir post-war (2017-09..2022-01), post-lead (2022-02..)
SELECT CASE WHEN message_date < '2014-04-08' THEN '1 gavin' WHEN message_date < '2017-08-24' THEN '2 blocksize war' WHEN message_date < '2022-02-01' THEN '3 wladimir post-war' ELSE '4 post-lead' END era,
  COUNT(*) n, COUNT(DISTINCT proposal_number) bips, COUNT(DISTINCT author_name) actors,
  ROUND(100*SUM(FIND_IN_SET('operational',influence_types)>0)/COUNT(*),1) operational, ROUND(100*SUM(FIND_IN_SET('functional',influence_types)>0)/COUNT(*),1) functional,
  ROUND(100*SUM(FIND_IN_SET('ecosystem',influence_types)>0)/COUNT(*),1) ecosystem, ROUND(100*SUM(FIND_IN_SET('compatibility',influence_types)>0)/COUNT(*),1) compatibility,
  ROUND(100*SUM(FIND_IN_SET('security',influence_types)>0)/COUNT(*),1) security, ROUND(100*SUM(FIND_IN_SET('economic',influence_types)>0)/COUNT(*),1) economic,
  ROUND(100*SUM(FIND_IN_SET('authority',influence_types)>0)/COUNT(*),1) authority, ROUND(100*SUM(FIND_IN_SET('strategic',influence_types)>0)/COUNT(*),1) strategic,
  ROUND(100*SUM(FIND_IN_SET('coalition',influence_types)>0)/COUNT(*),1) coalition, ROUND(100*SUM(FIND_IN_SET('standards',influence_types)>0)/COUNT(*),1) standards,
  ROUND(100*SUM(FIND_IN_SET('tactical',influence_types)>0)/COUNT(*),1) tactical, ROUND(100*SUM(FIND_IN_SET('user_demand',influence_types)>0)/COUNT(*),1) user_demand,
  ROUND(100*SUM(FIND_IN_SET('organizational',influence_types)>0)/COUNT(*),1) organizational,
  ROUND(100*SUM(controversial)/COUNT(*),1) contro,
  ROUND(100*SUM(FIND_IN_SET('unilateral',influence_types)>0)/COUNT(*),2) unilateral, ROUND(100*SUM(FIND_IN_SET('gatekeeping',influence_types)>0)/COUNT(*),2) gatekeeping,
  ROUND(100*SUM(FIND_IN_SET('exit_threat',influence_types)>0)/COUNT(*),2) exit_threat, ROUND(100*SUM(FIND_IN_SET('incivility',influence_types)>0)/COUNT(*),2) incivility,
  ROUND(100*SUM(FIND_IN_SET('backchannel',influence_types)>0)/COUNT(*),2) backchannel, ROUND(100*SUM(FIND_IN_SET('procedural_control',influence_types)>0)/COUNT(*),2) procedural,
  ROUND(100*SUM(FIND_IN_SET('corporate_interest',influence_types)>0)/COUNT(*),2) corporate
FROM influence_candidates WHERE message_date IS NOT NULL GROUP BY era;
-- 7. yearly
SELECT YEAR(message_date) y, COUNT(*) n, COUNT(DISTINCT proposal_number) bips, COUNT(DISTINCT author_name) actors, ROUND(100*SUM(controversial)/COUNT(*),1) pct_contro,
  ROUND(100*SUM(author_role='lead maintainer')/COUNT(*),1) lead, ROUND(100*SUM(author_role='maintainer')/COUNT(*),1) maint, ROUND(100*SUM(author_role='BIP editor')/COUNT(*),1) editor,
  ROUND(100*SUM(author_role='core developer')/COUNT(*),1) core, ROUND(100*SUM(author_role='proposal author')/COUNT(*),1) author, ROUND(100*SUM(author_role='community member')/COUNT(*),1) community
FROM influence_candidates WHERE message_date IS NOT NULL GROUP BY y ORDER BY y;
-- 8. top BIP per year and most contested BIPs
SELECT y, proposal_number, n FROM (SELECT YEAR(message_date) y, proposal_number, COUNT(*) n, ROW_NUMBER() OVER (PARTITION BY YEAR(message_date) ORDER BY COUNT(*) DESC) rk FROM influence_candidates WHERE message_date IS NOT NULL GROUP BY y, proposal_number) t WHERE rk<=2 ORDER BY y, rk;
SELECT proposal_number, final_decision, COUNT(*) n, COUNT(DISTINCT author_name) actors, SUM(controversial) contro, ROUND(100*SUM(controversial)/COUNT(*),1) pct FROM influence_candidates GROUP BY 1,2 HAVING n>=150 ORDER BY n DESC LIMIT 25;
-- 9. controversial per role
SELECT author_role, COUNT(*) n, ROUND(100*SUM(FIND_IN_SET('unilateral',influence_types)>0)/COUNT(*),2) unilateral, ROUND(100*SUM(FIND_IN_SET('corporate_interest',influence_types)>0)/COUNT(*),2) corporate,
  ROUND(100*SUM(FIND_IN_SET('gatekeeping',influence_types)>0)/COUNT(*),2) gatekeeping, ROUND(100*SUM(FIND_IN_SET('exit_threat',influence_types)>0)/COUNT(*),2) exit_threat,
  ROUND(100*SUM(FIND_IN_SET('incivility',influence_types)>0)/COUNT(*),2) incivility, ROUND(100*SUM(FIND_IN_SET('backchannel',influence_types)>0)/COUNT(*),2) backchannel,
  ROUND(100*SUM(FIND_IN_SET('procedural_control',influence_types)>0)/COUNT(*),2) procedural FROM influence_candidates GROUP BY 1 ORDER BY n DESC;
-- 10. alignment
SELECT influence_direction, SUM(aligns_with_outcome) aligned, COUNT(*) n, ROUND(100*SUM(aligns_with_outcome)/COUNT(*),1) pct FROM influence_candidates WHERE final_decision NOT IN ('unknown') GROUP BY 1;
-- 11. days-before-decision histogram (two-week bins, +-26 weeks)
SELECT FLOOR(-days_before_decision/14) bin, COUNT(*) n FROM influence_candidates WHERE days_before_decision IS NOT NULL AND ABS(days_before_decision) <= 182 GROUP BY bin ORDER BY bin;
-- 12. phase x mechanism
SELECT decision_phase, COUNT(*) n, ROUND(100*SUM(FIND_IN_SET('operational',influence_types)>0)/COUNT(*),1) operational, ROUND(100*SUM(FIND_IN_SET('functional',influence_types)>0)/COUNT(*),1) functional,
  ROUND(100*SUM(FIND_IN_SET('ecosystem',influence_types)>0)/COUNT(*),1) ecosystem, ROUND(100*SUM(FIND_IN_SET('compatibility',influence_types)>0)/COUNT(*),1) compatibility,
  ROUND(100*SUM(FIND_IN_SET('security',influence_types)>0)/COUNT(*),1) security, ROUND(100*SUM(FIND_IN_SET('economic',influence_types)>0)/COUNT(*),1) economic,
  ROUND(100*SUM(FIND_IN_SET('authority',influence_types)>0)/COUNT(*),1) authority, ROUND(100*SUM(FIND_IN_SET('strategic',influence_types)>0)/COUNT(*),1) strategic,
  ROUND(100*SUM(FIND_IN_SET('coalition',influence_types)>0)/COUNT(*),1) coalition, ROUND(100*SUM(FIND_IN_SET('standards',influence_types)>0)/COUNT(*),1) standards,
  ROUND(100*SUM(FIND_IN_SET('tactical',influence_types)>0)/COUNT(*),1) tactical, ROUND(100*SUM(FIND_IN_SET('user_demand',influence_types)>0)/COUNT(*),1) user_demand,
  ROUND(100*SUM(FIND_IN_SET('organizational',influence_types)>0)/COUNT(*),1) organizational
FROM influence_candidates GROUP BY 1 ORDER BY n DESC;
-- 13. outcome x mechanism
SELECT final_decision, COUNT(*) n, ROUND(100*SUM(FIND_IN_SET('operational',influence_types)>0)/COUNT(*),1) operational, ROUND(100*SUM(FIND_IN_SET('functional',influence_types)>0)/COUNT(*),1) functional,
  ROUND(100*SUM(FIND_IN_SET('ecosystem',influence_types)>0)/COUNT(*),1) ecosystem, ROUND(100*SUM(FIND_IN_SET('compatibility',influence_types)>0)/COUNT(*),1) compatibility,
  ROUND(100*SUM(FIND_IN_SET('security',influence_types)>0)/COUNT(*),1) security, ROUND(100*SUM(FIND_IN_SET('economic',influence_types)>0)/COUNT(*),1) economic,
  ROUND(100*SUM(FIND_IN_SET('authority',influence_types)>0)/COUNT(*),1) authority, ROUND(100*SUM(FIND_IN_SET('strategic',influence_types)>0)/COUNT(*),1) strategic,
  ROUND(100*SUM(FIND_IN_SET('coalition',influence_types)>0)/COUNT(*),1) coalition, ROUND(100*SUM(FIND_IN_SET('standards',influence_types)>0)/COUNT(*),1) standards,
  ROUND(100*SUM(FIND_IN_SET('tactical',influence_types)>0)/COUNT(*),1) tactical, ROUND(100*SUM(FIND_IN_SET('user_demand',influence_types)>0)/COUNT(*),1) user_demand,
  ROUND(100*SUM(FIND_IN_SET('organizational',influence_types)>0)/COUNT(*),1) organizational
FROM influence_candidates GROUP BY 1 ORDER BY n DESC;
