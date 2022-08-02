
CREATE INDEX lab ON autoextractedreasoncandidatesentences (label(8));
CREATE INDEX loc ON autoextractedreasoncandidatesentences (location(8));

CREATE INDEX sentindex ON autoextractedreasoncandidatesentences (sentence(25));
CREATE INDEX datevalindex ON autoextractedreasoncandidatesentences (datevalue);
CREATE INDEX tpindex ON autoextractedreasoncandidatesentences (totalprobability);