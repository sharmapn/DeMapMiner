-- run against peps_2026 (extended corpus) after the Sept 2026 controversial-mechanism run
SELECT 'total' AS k, COUNT(*) AS n, SUM(controversial) AS controversial, ROUND(100*SUM(controversial)/COUNT(*),1) AS pct FROM influence_candidates;

-- per mechanism, overall and by era
SELECT m.mech,
       SUM(FIND_IN_SET(m.mech, c.influence_types)>0) AS n,
       ROUND(100*SUM(FIND_IN_SET(m.mech, c.influence_types)>0)/COUNT(*),2) AS pct_all,
       ROUND(100*SUM(FIND_IN_SET(m.mech, c.influence_types)>0 AND c.governance_era='bdfl_era')/SUM(c.governance_era='bdfl_era'),2) AS pct_bdfl,
       ROUND(100*SUM(FIND_IN_SET(m.mech, c.influence_types)>0 AND c.governance_era='post_bdfl_era')/SUM(c.governance_era='post_bdfl_era'),2) AS pct_sc
FROM influence_candidates c
JOIN (SELECT 'unilateral' mech UNION SELECT 'corporate_interest' UNION SELECT 'gatekeeping' UNION SELECT 'exit_threat' UNION SELECT 'incivility' UNION SELECT 'backchannel' UNION SELECT 'procedural_control') m
GROUP BY m.mech ORDER BY n DESC;

-- controversial share by role
SELECT author_role, COUNT(*) n, ROUND(100*SUM(controversial)/COUNT(*),1) pct_controversial,
  ROUND(100*SUM(FIND_IN_SET('unilateral',influence_types)>0)/COUNT(*),2) unilateral,
  ROUND(100*SUM(FIND_IN_SET('corporate_interest',influence_types)>0)/COUNT(*),2) corporate,
  ROUND(100*SUM(FIND_IN_SET('gatekeeping',influence_types)>0)/COUNT(*),2) gatekeeping,
  ROUND(100*SUM(FIND_IN_SET('exit_threat',influence_types)>0)/COUNT(*),2) exit_threat,
  ROUND(100*SUM(FIND_IN_SET('incivility',influence_types)>0)/COUNT(*),2) incivility,
  ROUND(100*SUM(FIND_IN_SET('backchannel',influence_types)>0)/COUNT(*),2) backchannel,
  ROUND(100*SUM(FIND_IN_SET('procedural_control',influence_types)>0)/COUNT(*),2) procedural
FROM influence_candidates GROUP BY author_role ORDER BY n DESC;

-- by era
SELECT governance_era, COUNT(*) n, ROUND(100*SUM(controversial)/COUNT(*),1) pct_controversial FROM influence_candidates GROUP BY governance_era;

-- by outcome
SELECT final_decision, COUNT(*) n, ROUND(100*SUM(controversial)/COUNT(*),1) pct_controversial,
  ROUND(100*SUM(FIND_IN_SET('unilateral',influence_types)>0)/COUNT(*),2) unilateral,
  ROUND(100*SUM(FIND_IN_SET('gatekeeping',influence_types)>0)/COUNT(*),2) gatekeeping,
  ROUND(100*SUM(FIND_IN_SET('incivility',influence_types)>0)/COUNT(*),2) incivility,
  ROUND(100*SUM(FIND_IN_SET('exit_threat',influence_types)>0)/COUNT(*),2) exit_threat,
  ROUND(100*SUM(FIND_IN_SET('procedural_control',influence_types)>0)/COUNT(*),2) procedural
FROM influence_candidates GROUP BY final_decision ORDER BY n DESC;

-- by decision phase
SELECT decision_phase, COUNT(*) n, ROUND(100*SUM(controversial)/COUNT(*),1) pct_controversial FROM influence_candidates GROUP BY decision_phase ORDER BY n DESC;

-- proposals with the most controversial candidates (and their density)
SELECT proposal_number, final_decision, COUNT(*) n, SUM(controversial) contro, ROUND(100*SUM(controversial)/COUNT(*),1) pct,
  SUM(FIND_IN_SET('unilateral',influence_types)>0) uni, SUM(FIND_IN_SET('gatekeeping',influence_types)>0) gate,
  SUM(FIND_IN_SET('exit_threat',influence_types)>0) exitt, SUM(FIND_IN_SET('incivility',influence_types)>0) inciv,
  SUM(FIND_IN_SET('backchannel',influence_types)>0) back, SUM(FIND_IN_SET('procedural_control',influence_types)>0) proc,
  SUM(FIND_IN_SET('corporate_interest',influence_types)>0) corp
FROM influence_candidates GROUP BY proposal_number, final_decision HAVING n >= 200 ORDER BY contro DESC LIMIT 15;

-- venue (mailing list vs discourse) within SC era: message_id >= 10066940 are new rows; discourse rows have author_email like '%discuss.python.org'
SELECT CASE WHEN author_email LIKE '%discuss.python.org' THEN 'discourse' ELSE 'mailing list' END venue, COUNT(*) n,
  ROUND(100*SUM(controversial)/COUNT(*),1) pct_controversial,
  ROUND(100*SUM(FIND_IN_SET('incivility',influence_types)>0)/COUNT(*),2) incivility,
  ROUND(100*SUM(FIND_IN_SET('procedural_control',influence_types)>0)/COUNT(*),2) procedural,
  ROUND(100*SUM(FIND_IN_SET('unilateral',influence_types)>0)/COUNT(*),2) unilateral
FROM influence_candidates WHERE governance_era='post_bdfl_era' GROUP BY venue;
