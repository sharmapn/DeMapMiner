CREATE TABLE allsentences (
    id int NOT NULL AUTO_INCREMENT,
    sentence LONGTEXT NOT NULL,
    messageid INTEGER,
    reasonTerms INTEGER,
    reasonIdentifierTerms INTEGER,
    stateSubStateTerms INTEGER,
    entityTerms INTEGER,
    decisionTerms INTEGER,
    specialTerms INTEGER,
    authorRole INTEGER,
    dateDiff INTEGER,
    messageSubjectDecisionTerms INTEGER,
    messageSubjectStateSubStateTerms INTEGER,
    messageSubjectProposalIdentifier INTEGER,    
    PRIMARY KEY (id)
); 