dROP TABLE COMPANIES;
CREATE TABLE companies (
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  Company LONGTEXT,
  Commercial_Entity LONGTEXT,
  Location LONGTEXT,
  Funding_Source LONGTEXT,
  Developers LONGTEXT
);
INSERT INTO companies
  (Company,Commercial_Entity,Location,Funding_Source,Developers)
VALUES
  ('Chaincode Labs','No','New York','Financed by the two founders, Alex Morcos and Suhas Daftuar','The company has 6 dedicated Bitcoin developers (Alex Morcos, Suhas Daftuar, John Newbury, Russ Yanofsky, Marco Falke & Carl Dong) – Source: https://chaincode.com/#team'),
  ('MIT DCI','No','Boston','Donations','MIT has 2 dedicated Bitcoin developers (Wladimir van der Laan & Cory Field) and 4 or 5 researchers – Source: https://dci.mit.edu/team'),
  ('DG Lab','Yes','Tokyo','Listed Japanese investment company','The company has 2 to 4 Bitcoin developers'),
  ('Square Crypto','No','San Francisco','Financed by listed American payment company Square Inc','4 developers all building Rust Lightning. The company also gives 3 Bitcoin developer grants'),
  ('Blockstream','Yes','Victoria (Canada)','VC backed','The company has 2 dedicated Bitcoin developers (Pieter Wuille & Andrew Chow), 3 dedicated Lightning developers (Rusty Russel, Christian Decker & Lisa Neigut) (C-Lightning) and 3 cryptographers (Andrew Poelstra, Tim Ruffing & Jonas Nick)'),
  ('Lightning Labs','Yes','San Francisco','VC backed','The company has at least 8 developers working on the open source Lightning software (LND and Loopd) (Olaoluwa Osuntokun, Conner Fromknecht, Alex Bosworth, Johan Halseth, Joost Jager, Wilmer Paulino, Carla Kirk-Cohen & Oliver Gugger) – Source: https://lightning.engineering/team'),
  ('','','','',''),
  ('Acinq','Yes','Paris','VC backed','The company has at least 4 open source Bitcoin/Lightning developers'),
  ('Bitfinex','Yes','','Retained earnings','The company funds 3 developers working on RGB (A tool to issue tokens on Lightning)'),
  ('Xapo','Yes','San Francisco','VC backed','The company has 2 dedicated Bitcoin developers'),
  ('OKCoin','Yes','San Fran','Retained earnings','Currently provides one Bitcoin developer a grant'),
  ('Bull Bitcoin','Yes','Canada','','Currently funds one Bitcoin developer building Cyphernode'),
  ('BitMEX','Yes','Seychelles','Retained earnings','Currently provides one Bitcoin developer a grant & has one part time contractor who contributes to Bitcoin Core'),
  ('Crypto Advance','Yes','Munich','Private donor','Funds the open source development work of Stepan Snigirev'),
  ('Hardcore Fund','No','','Donations','Provides small grants to 2 Bitcoin developers'),
  ('Bitmain','Yes','China','Retained earnings','Organisation does not appear to be actively funding development. In the past Bitmain supported at least 2 developers.'),
  ('Blockchain.info','Yes','London','VC backed','Organisation does not appear to be actively funding development. In the past Blockchain.info supported at least 1 developer'),
  ('Bitcoin Foundation','No','London','Donations','Organisation does not appear to be actively funding development. In the past this was one of the only organisations funding development'),
  ('BTCC','Yes','China','Retained earnings','Organisation does not appear to be actively funding development '),
  ('BitPay','Yes','US','VC backed','Organisation does not appear to be actively funding development. In the past Jeff Garzik was sponsored to work on Bitcoin development');
