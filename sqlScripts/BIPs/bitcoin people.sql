

SELECT * from autoextractedreasoncandidatesentences

drop table autoextractedreasoncandidateparagraphs_main;

-- DROP TABLE people;
SELECT * FROM people
SELECT * FROM stakeholders
SELECT * FROM companies
TRUNCATE bipstates_danieldata_datetimestamp;

SELECT sendername, authorsrole, COUNT(messageid) AS cnt
FROM allmessages 
group BY sendername
ORDER BY cnt desc

SELECT * FROM allmessages WHERE sendername = 'Pieter Wuille' limit 50

SELECT * 
FROM bipstates_danieldata_datetimestamp
ORDER BY bip ASC

SELECT state, COUNT(DISTINCT bip) AS countbips
FROM bipstates_danieldata_datetimestamp
GROUP BY state
ORDER BY countbips DESC

SELECT bip, datetimestamp,"notfound" AS author, state
FROM bipstates_danieldata_datetimestamp
ORDER BY bip asc

SELECT bip, COUNT(messageid) 
FROM allmessages 
group BY bip

-- set the roles
SELECT distinct developer, description FROM people ORDER BY developer ASC
-- see which roles have not been assigned
SELECT bip, author, subject, sendername, fromline, authorsrole 
FROM allmessages
WHERE authorsrole IS NULL 
-- group by above
-- main query for Rewat to set
SELECT sendername, author,  REPLACE(    REPLACE(SUBSTRING_INDEX(email, "From: ", 1),'\r',''), '\n', '') AS froml, COUNT(*) AS cnt 
FROM allmessages
WHERE authorsrole IS NULL
GROUP BY sendername
-- , author, froml
ORDER BY cnt DESC

SELECT * FROM allmessages WHERE sendername = 'Thomas Voegtlin'

-- updates
UPDATE allmessages SET sendername = 'Russell O’ Connor', role = '' WHERE sendername = 'ZmnSCPxj';

-- see duplicates
SELECT COUNT(*) c, developer 
FROM people 
GROUP BY developer 
HAVING c > 1

SELECT * FROM allmessages
SELECT bip, DATE2, author, subject, sendername, fromline, authorsrole FROM allmessages
WHERE sendername IN (SELECT developer FROM people) 
-- description
-- 21,614 rows
UPDATE allmessages
       JOIN people
       ON allmessages.sendername = people.developer
SET    allmessages.authorsrole = people.description;
/* Affected rows: 7,774  */
-- now lets scheck which ones are not assigned
SELECT sendername, COUNT(messageid)   AS cnt
-- bip, DATE2, author, subject, sendername, fromline, authorsrole 
FROM allmessages
WHERE authorsrole IS null
GROUP BY sendername
ORDER BY sendername asc
-- ORDER BY cnt desc

UPDATE bipstates_danieldata_datetimestamp SET state = NULL;
-- UPDATE bipstates_danieldata_datetimestamp SET state = REPLACE(email,'Status :','')

select distinct(company) as company FROM  companies;
ORDER BY developer ASC;

select distinct(developer) as developer from people where description like '%Bitcoin developer%' 

SELECT rankbycommits, developer FROM people ORDER BY developer asc;

CREATE TABLE people (
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  RankByCommits INT,
  Developer LONGTEXT,
  Description VARCHAR(255),
  Funding VARCHAR(255),
  Details longtext,
  Reference VARCHAR(255),
  bitcoinj VARCHAR(255),
  BFGMiner VARCHAR(255),
  Tools VARCHAR(255),
  Libbitcoin VARCHAR(255),
  Gentoo VARCHAR(255),
  Supybot VARCHAR(255)
);
INSERT INTO people
  (RankByCommits,Developer,Description,Funding,Details,Reference,bitcoinj,BFGMiner,Tools,Libbitcoin,Gentoo,Supybot)
VALUES
  (NULL,'Wladimir van der Laan','Bitcoin Core maintainer','MIT DCI',' Satoshi client maintainer.','','','','','','',''),
  (NULL,'Pieter Wuille','Bitcoin Core maintainer','Blockstream',' Satoshi client developer and maintainer of the network graphs http://bitcoin.sipa.be','','','','','','',''),
  (NULL,'Marco Falke','Bitcoin Core maintainer','Chaincode','','','','','','','',''),
  (NULL,'Michael Ford','Bitcoin Core maintainer','BitMEX','','','','','','','',''),
  (NULL,'Jonas Schnelli','Bitcoin Core maintainer','Formerly Bitmain','','','','','','','',''),
  (NULL,'Samuel Dobson','Bitcoin Core maintainer','John Pfeffer','','','','','','','',''),
  (NULL,'','','','','','','','','','',''),
  (1,'Matt Corallo','Bitcoin developer (now working on Rust Lightning)','Square Crypto (formerly Chaincode and Blockstream)',' Satoshi client and Bitcoinj developer.','','','','','','',''),
  (2,'Cory Fields','Bitcoin developer','MIT DCI','','','','','','','',''),
  (3,'Practical Swift','Bitcoin developer','','','','','','','','',''),
  (4,'John Newbery','Bitcoin developer','Chaincode','','','','','','','',''),
  (5,'Gavin Andresen','Bitcoin developer','Formerly the Bitcoin Foundation and MIT DCI',' (Profile ) Former Satoshi client maintainer. He previously worked at Silicon Graphics and now runs his own company.','','','','','','',''),
  (6,'Luke-Jr','Bitcoin developer','Hardcore Fund','','','','','','','',''),
  (7,'Russell Yanofsky','Bitcoin developer','Chaincode','','','','','','','',''),
  (8,'Andrew Chow','Bitcoin developer','Blockstream','','','','','','','',''),
  (9,'João Barbosa','Bitcoin developer','Formerly Bitmain','','','','','','','',''),
  (10,'Suhas Daftuar','Bitcoin developer','Chaincode','','','','','','','',''),
  (11,'Alex Morcos','Bitcoin developer','Chaincode','','','','','','','',''),
  (12,'Hennadii Stepanov','Bitcoin developer','Cardcoin & Payvant','','','','','','','',''),
  (13,'Jorge Timon','Bitcoin developer','Formerly Blockstream','','','','','','','',''),
  (14,'Gregory Maxwell','Bitcoin developer','Formerly Blocksteam','','','','','','','',''),
  (15,'Gregory Sanders','Bitcoin developer','Blockstream','','','','','','','',''),
  (16,'Karl-Johan Alm','Bitcoin developer','DG Lab','','','','','','','',''),
  (17,'Ben Woosley','Bitcoin developer','Hardcore Fund','','','','','','','',''),
  (18,'Peter Todd','Bitcoin developer','Has previously received funding from BTCC, Bitfinex & Chaincode',' Bitcoin developer. Involved with Bitcoin related startup Coinkite and DarkWallet.','','','','','','',''),
  (19,'James O’Beirne','Bitcoin developer','Currently moving from Chaincode to DG Lab','','','','','','','',''),
  (20,'Sjors Provoost','Bitcoin developer','Formerly funded by blockchain.info, part time on open source projects for BitMEX','','','','','','','',''),
  (21,'Patrick Strateman','Bitcoin developer','Formerly Blockstream',' Bitcoin developer, creator of Intersango, member of Bitcoin Consultancy and creator of Python Bitcoin implementation.','','','','','','',''),
  (22,'Carl Dong','Bitcoin developer','Chaincode','','','','','','','',''),
  (23,'Jon Atack','Bitcoin developer','Square Crypto grant','','','','','','','',''),
  (24,'Anthony Towns','Bitcoin developer','Xapo','','','','','','','',''),
  (25,'Jeremy Rubin','Bitcoin developer','Formerly MIT DCI & Chaincode','','','','','','','',''),
  (26,'Nicolas Dorier','Bitcoin and BTCPAY developer','DG Lab','','','','','','','',''),
  (27,'David Harding','Bitcoin developer','','','','','','','','',''),
  (NULL,'','','','','','','','','','',''),
  (NULL,'Fabian Jahr','Bitcoin developer','OKCoin','','','','','','','',''),
  (NULL,'Kukks','BTCPAY developer','BTSE','','','','','','','',''),
  (NULL,'Amiti Uttarwar','Bitcoin developer','Xapo','','','','','','','',''),
  (NULL,'','','','','','','','','','',''),
  (NULL,'Andreas Schildbach (Profile) ','Bitcoin developer','',' original developer of Bitcoin Wallet for Android (Google Code Project).','','','','','','',''),
  (NULL,'Amir Taaki (Wikipedia) aka genjix ','Bitcoin developer','',' creator of the BIP process, Libbitcoin C++ developer toolkit, Obelisk blockchain server (later BS), SX bitcoin command line tool (later BX), Darkwallet, Darkmarket (later OpenBazaar), Darkleaks, Freecoin and well as inactive/defunct projects such as the Britcoin exchange, Intersango exchange, Spesmilo RPC client, Bitcoin Consultancy, Bitcoin Media Blog (author), Vibanko web wallet provider, GLBSE exchange client, Kartludox Bitcoin poker client, Pastecoin and Python bindings for Bitcoin.','','','','','','',''),
  (NULL,'Art Forz ','','',' developed the first GPU miner and at one time his GPU mining farm (the ArtFarm) was mining over a third of all blocks.','','','','','','',''),
  (NULL,'Gary Rowe (Profile ) ','','',' Contributor to the MultiBit (http://multibit.org) and BitCoinJ (http://code.google.com/p/bitcoinj/) projects. Working on various Bitcoin based businesses.','','','','','','',''),
  (NULL,'','','','','','','','','','',''),
  (NULL,'Hal Finney ','','',' one of the creators of PGP and one of the earliest contributors to the Bitcoin project. First to identify a type of double-spending attack that now bears his name -- the Finney attack.','','','','','','',''),
  (NULL,'James McCarthy aka Nefario ','','',' creator of the first bitcoin stock exchange GLBSE','','','','','','',''),
  (NULL,'Jed McCaleb ','','',' cofounder of Stellar.org and original developer of MtGox. Previously created eDonkey2000.','','','','','','',''),
  (NULL,'Jeff Garzik ','','',' Satoshi client core developer, GPU poold software and the founder of Bitcoin Watch. Works for BitPay.','','','','','','',''),
  (NULL,'Luke Dashjr aka Luke-Jr ','','',' Eligius founder, maintains BFGMiner and maintainer of bitcoind/Bitcoin-Qt stable branches.','','','','','','',''),
  (NULL,'Mark Karpeles (Wikipedia) aka MagicalTux ','','',' Former owner of MtGox and this wiki.','','','','','','',''),
  (NULL,'Martti Malmi aka Sirius ','','',' Former Bitcoin developer. Operates the domain names bitcoin.org and bitcointalk.org.','','','','','','',''),
  (NULL,'Michael Hendrix aka mndrix ','','',' creator of the now defunct CoinPal and CoinCard services','','','','','','',''),
  (NULL,'Mike Hearn ','','','(Profile) Google engineer who works on Gmail and developed BitCoinJ (http://code.google.com/p/bitcoinj/) and Lighthouse.','','','','','','',''),
  (NULL,'Nils Schneider aka tcatm ','','',' Bitcoin developer, owner of BitcoinWatch, creator and owner of BitcoinCharts, GPU mining software and JS web interface.','','','','','','',''),
  (NULL,'Patrick McFarland aka Diablo-D3 ','','',' DiabloMiner author, and former BitcoinTalk forum moderator.','','','','','','',''),
  (NULL,'Stefan Thomas aka justmoon ','','',' creator of the (We Use Coins) site/video and WebCoin.','','','','','','',''),
  (NULL,'Tamas Blummer aka grau ','','',' author of Bits of Proof, the enterprise-ready implementation of the Bitcoin protocol. http://bitsofproof.com','','','','','','',''),
  (NULL,'Trace Mayer ','','',' Host of the (Bitcoin Knowledge Podcast) where the top people in Bitcoin are interviewed','','','','','','',''),
  (NULL,'Vladimir Marchenko ','','',' (Profile), runs Marchenko Ltd which sells mining contracts, previously developed the figator.org search engine.','','','','','','',''),
  (NULL,'','','','','','','','','','',''),
  (NULL,'','','','','','','','','','',''),
  (NULL,'Satoshi Nakamoto','','','','Author','No','No','No','No','No','No'),
  (NULL,'Wladimir J. van der Laan','','','','Lead','No','No','No','No','No','No'),
  (NULL,'Pieter Wuille','','','','Yes','No','No','No','No','No','No'),
  (NULL,'Gavin Andresen','','','','Retired','No','No','Author','No','No','No'),
  (NULL,'Cory Fields','','','','Yes','No','No','No','No','No','No'),
  (NULL,'Matt Corallo','','','','Yes','Yes','No','No','No','No','No'),
  (NULL,'Jonas Schnelli','','','','Yes','No','No','No','No','No','No'),
  (NULL,'Marco Falke','','','','Yes','No','No','No','No','No','No'),
  (NULL,'Luke Dashjr','','','','Yes','No','Lead','No','','Yes','Yes'),
  (NULL,'John Newbery','','','','Yes','No','No','No','No','No','No'),
  (NULL,'Alex Morcos','','','','Yes','No','No','No','No','No','No'),
  (NULL,'Suhas Daftuar','','','','Yes','No','No','No','No','No','No'),
  (NULL,'fanquake','','','','Yes','No','No','No','No','No','No'),
  (NULL,'Jorge Timón','','','','Yes','No','No','No','No','No','No'),
  (NULL,'Gregory Maxwell','','','','Yes','No','No','No','No','No','No'),
  (NULL,'practicalswift','','','','Yes','No','No','No','No','No','No'),
  (NULL,'Russell Yanofsky','','','','Yes','No','No','No','No','No','No'),
  (NULL,'paveljanik','','','','Yes','No','No','No','No','No','No'),
  (NULL,'cozz','','','','Yes','No','No','No','No','No','No'),
  (NULL,'Patrick Strateman','','','','Yes','No','No','No','Author','No','No'),
  (NULL,'Andrew Chow','','','','Yes','No','No','No','No','No','No'),
  (NULL,'João Barbosa','','','','Yes','No','No','No','No','No','No'),
  (NULL,'Kalle Alm','','','','Yes','No','No','No','No','No','No'),
  (NULL,'Eric Lombrozo','','','','Yes','No','No','No','No','No','No'),
  (NULL,'Samuel Dobson','','','','Yes','No','No','No','No','No','No'),
  (NULL,'Johnson Lau','','','','Yes','No','No','No','No','No','No'),
  (NULL,'Giel van Schijndel','','','','','No','No','No','No','No','No'),
  (NULL,'rebroad','','','','Yes','No','No','No','No','No','No'),
  (NULL,'sirius-m','','','','Retired','No','No','No','No','No','No'),
  (NULL,'Chris Moore','','','','','No','No','No','No','No','No'),
  (NULL,'Nils Schneider','','','','','No','No','No','No','No','Yes'),
  (NULL,'Jeff Garzik','','','','Retired','No','Author','No','No','No','No'),
  (NULL,'Diapolo','','','','','No','No','No','No','No','No'),
  (NULL,'Mike Hearn','','','','No','Author','No','No','No','No','No'),
  (NULL,'Andreas Schildbach','','','','No','Yes','No','No','No','No','No'),
  (NULL,'devrandom','','','','No','Yes','No','No','No','No','No'),
  (NULL,'Daniel Folkinshteyn','','','','No','No','No','No','No','No','Yes'),
  (NULL,'Amir Taaki','','','','No','No','No','No','Author','No','No'),
  (NULL,'Eric Voskuil','','','','No','No','No','No','Lead','No','No'),
  (NULL,'Phillip Mienk','','','','No','No','No','No','Yes','No','No'),
  (NULL,'William Swanson','','','','No','No','No','No','Yes','No','No'),
  (NULL,'Neill Miller','','','','No','No','No','No','Yes','No','No'),
  (NULL,'Pablo Castellano','','','','No','No','No','No','Yes','No','No'),
  (NULL,'Noel Maersk','','','','No','No','No','No','Yes','No','No'),
  (NULL,'Kamil Domanski','','','','','No','No','No','','No','No'),
  (NULL,'bobalot','','','','No','No','No','No','','No','No'),
  (NULL,'drwasho','','','','No','No','No','No','','No','No'),
  (NULL,'saracen','','','','','No','No','No','No','No','No'),
  (NULL,'Matt Giuca','','','','','No','No','Yes','No','No','No'),
  (NULL,'Con Kolivas','','','','No','No','Yes','No','No','No','No'),
  (NULL,'Mizery De Aria','','','','No','No','No','No','No','Author','No'),
  (NULL,'Laurent Bachelier','','','','No','No','No','No','No','Yes','No'),
  (NULL,'Florian Schmaus','','','','No','No','No','No','No','Yes','No'),
  (NULL,'BioMike','','','','No','No','No','No','No','Yes','No'),
  (NULL,'Marius Hanne','','','','','No','No','No','No','No','No'),
  (NULL,'David Francois','','','','','No','No','No','No','No','No'),
  (NULL,'Fabian H jr.','','','','','No','No','No','No','No','No'),
  (NULL,'ovdeathiam','','','','','No','No','No','No','No','No'),
  (NULL,'Michal Zima','','','','','No','No','No','No','No','No'),
  (NULL,'Venkatesh Srinivas','','','','','No','No','No','No','No','No'),
  (NULL,'Vegard Nossum','','','','','No','No','No','No','No','No'),
  (NULL,'JoelKatz','','','','','No','No','No','No','No','No'),
  (NULL,'Joerie de Gram','','','','','No','No','No','No','No','No'),
  (NULL,'Johannes Henninger','','','','','No','No','No','No','No','No'),
  (NULL,'Jeroenz0r','','','','','No','No','No','No','No','No'),
  (NULL,'Han Lin Yap','','','','','No','No','No','No','No','No'),
  (NULL,'Abraham Jewowich','','','','','No','No','No','No','No','No'),
  (NULL,'Michael Bemmerl','','','','','No','No','No','No','No','No'),
  (NULL,'Eric Hosmer','','','','','No','No','No','No','No','No'),
  (NULL,'Dawid Spiechowicz','','','','','No','No','No','No','No','No'),
  (NULL,'Stéphane Gimenez','','','','','No','No','No','No','No','No'),
  (NULL,'Patrick Varilly','','','','','No','No','No','No','No','No'),
  (NULL,'Jay Weisskopf','','','','','No','No','No','No','No','No'),
  (NULL,'Dylan Noblesmith','','','','','No','No','No','No','No','No'),
  (NULL,'James Burkle','','','','','No','No','No','No','No','No'),
  (NULL,'Jordan Lewis','','','','','No','No','No','No','No','No'),
  (NULL,'Dean Lee','','','','','No','No','No','No','No','No'),
  (NULL,'xHire','','','','','No','No','No','No','No','No'),
  (NULL,'HostFat','','','','','No','No','No','No','No','No'),
  (NULL,'Jakob Kramer','','','','','No','No','No','No','No','No'),
  (NULL,'ariel','','','','','No','No','No','No','No','No'),
  (NULL,'dabaopku','','','','','No','No','No','No','No','No'),
  (NULL,'Federico Faggiano','','','','','No','No','No','No','No','No'),
  (NULL,'m0ray','','','','','No','No','No','No','No','No'),
  (NULL,'Danube','','','','','No','No','No','No','No','No'),
  (NULL,'Carlos Pizarro','','','','','No','No','No','No','No','No'),
  (NULL,'Blitzboom','','','','','No','No','No','No','No','No'),
  (NULL,'Daniel Holbert','','','','','No','No','No','No','No','No'),
  (NULL,'Jaromil','','','','','No','No','No','','No','No'),
  (NULL,'Carlo Alberto Ferraris','','','','','No','No','No','No','No','No'),
  (NULL,'Forrest Voight','','','','','No','No','No','No','No','No'),
  (NULL,'Amir Yalon','','','','','No','No','No','No','No','No'),
  (NULL,'John Maguire','','','','','No','No','No','No','No','No'),
  (NULL,'Ricardo M. Correia','','','','','No','No','No','No','No','No'),
  (NULL,'Dan Helfman','','','','','No','No','No','No','No','No'),
  (NULL,'gjs278','','','','','No','No','No','No','No','No'),
  (NULL,'Dan Loewenherz','','','','','No','No','No','No','No','No'),
  (NULL,'Eric Swanson','','','','','No','No','No','No','No','No'),
  (NULL,'Santiago M. Mola','','','','','No','No','No','No','No','No'),
  (NULL,'Shane Wegner','','','','','No','No','No','No','No','No'),
  (NULL,'Sven Slootweg','','','','','No','No','No','No','No','No'),
  (NULL,'ojab','','','','','No','No','No','No','No','No'),
  (NULL,'sandos','','','','','No','No','No','No','No','No'),
  (NULL,'Witchspace','','','','','No','No','No','No','No','No'),
  (NULL,'laszlo','','','','','No','No','No','No','No','No');
