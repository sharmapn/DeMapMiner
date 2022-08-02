/* CREATE TABLE */
-- DROP TABLE roles;
CREATE TABLE roles(
id VARCHAR(100),
RankByCommits VARCHAR(100),
Developer VARCHAR(100),
Description VARCHAR(100),
Role VARCHAR(100),
Funding VARCHAR(100),
Details VARCHAR(200),
Reference VARCHAR(100),
bitcoinj VARCHAR(100),
BFGMiner VARCHAR(100),
Tools VARCHAR(100),
Libbitcoin VARCHAR(100),
Gentoo VARCHAR(100),
Supybot VARCHAR(100)
);

/* INSERT QUERY NO: 1 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'', '\N', 'Satoshi Nakamoto', '', '', '', '', 'Author', 'No', 'No', 'No', 'No', 'No', 'No'
);

/* INSERT QUERY NO: 2 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'1', '\N', 'Wladimir van der Laan', 'Bitcoin Core maintainer', 'Core maintainer', 'MIT DCI', ' Satoshi client maintainer.', 'Lead', 'No', 'No', 'No', 'No', 'No', 'No'
);

/* INSERT QUERY NO: 3 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'2', '\N', 'Pieter Wuille', 'Bitcoin Core maintainer', 'Core maintainer', 'Blockstream', ' Satoshi client developer and maintainer of the network graphs http://bitcoin.sipa.be', 'Yes', 'No', 'No', 'No', 'No', 'No', 'No'
);

/* INSERT QUERY NO: 4 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'3', '\N', 'Marco Falke', 'Bitcoin Core maintainer', 'Core maintainer', 'Chaincode', '', 'Yes', 'No', 'No', 'No', 'No', 'No', 'No'
);

/* INSERT QUERY NO: 5 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'4', '\N', 'Michael Ford', 'Bitcoin Core maintainer', 'Core maintainer', 'BitMEX', '', '', '', '', '', '', '', ''
);

/* INSERT QUERY NO: 6 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'5', '\N', 'Jonas Schnelli', 'Bitcoin Core maintainer', 'Core maintainer', 'Formerly Bitmain', '', 'Yes', 'No', 'No', 'No', 'No', 'No', 'No'
);

/* INSERT QUERY NO: 7 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'6', '\N', 'Samuel Dobson', 'Bitcoin Core maintainer', 'Core maintainer', 'John Pfeffer', '', 'Yes', 'No', 'No', 'No', 'No', 'No', 'No'
);

/* INSERT QUERY NO: 8 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'8', '1', 'Matt Corallo', 'Bitcoin developer (now working on Rust Lightning)', 'Developer', 'Square Crypto (formerly Chaincode and Blockstream)', ' Satoshi client and Bitcoinj developer.', 'Yes', 'Yes', 'No', 'No', 'No', 'No', 'No'
);

/* INSERT QUERY NO: 9 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'9', '2', 'Cory Fields', 'Bitcoin developer', 'Developer', 'MIT DCI', '', 'Yes', 'No', 'No', 'No', 'No', 'No', 'No'
);

/* INSERT QUERY NO: 10 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'10', '3', 'Practical Swift', 'Bitcoin developer', 'Developer', '', '', 'Yes', 'No', 'No', 'No', 'No', 'No', 'No'
);

/* INSERT QUERY NO: 11 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'11', '4', 'John Newbery', 'Bitcoin developer', 'Developer', 'Chaincode', '', 'Yes', 'No', 'No', 'No', 'No', 'No', 'No'
);

/* INSERT QUERY NO: 12 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'12', '5', 'Gavin Andresen', 'Bitcoin developer', 'Developer', 'Formerly the Bitcoin Foundation and MIT DCI', ' (Profile ) Former Satoshi client maintainer. He previously worked at Silicon Graphics and now runs his own company.', 'Retired', 'No', 'No', 'Author', 'No', 'No', 'No'
);

/* INSERT QUERY NO: 13 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'13', '6', 'Luke-Jr', 'Bitcoin developer', 'Developer', 'Hardcore Fund', '', '', '', '', '', '', '', ''
);

/* INSERT QUERY NO: 14 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'14', '7', 'Russell Yanofsky', 'Bitcoin developer', 'Developer', 'Chaincode', '', '', '', '', '', '', '', ''
);

/* INSERT QUERY NO: 15 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'15', '8', 'Andrew Chow', 'Bitcoin developer', 'Developer', 'Blockstream', '', 'Yes', 'No', 'No', 'No', 'No', 'No', 'No'
);

/* INSERT QUERY NO: 16 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'16', '9', 'Jo?o Barbosa', 'Bitcoin developer', 'Developer', 'Formerly Bitmain', '', 'Yes', 'No', 'No', 'No', 'No', 'No', 'No'
);

/* INSERT QUERY NO: 17 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'17', '10', 'Suhas Daftuar', 'Bitcoin developer', 'Developer', 'Chaincode', '', 'Yes', 'No', 'No', 'No', 'No', 'No', 'No'
);

/* INSERT QUERY NO: 18 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'18', '11', 'Alex Morcos', 'Bitcoin developer', 'Developer', 'Chaincode', '', 'Yes', 'No', 'No', 'No', 'No', 'No', 'No'
);

/* INSERT QUERY NO: 19 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'19', '12', 'Hennadii Stepanov', 'Bitcoin developer', 'Developer', 'Cardcoin & Payvant', '', '', '', '', '', '', '', ''
);

/* INSERT QUERY NO: 20 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'20', '13', 'Jorge Timon', 'Bitcoin developer', 'Developer', 'Formerly Blockstream', '', 'Yes', 'No', 'No', 'No', 'No', 'No', 'No'
);

/* INSERT QUERY NO: 21 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'21', '14', 'Gregory Maxwell', 'Bitcoin developer', 'Developer', 'Formerly Blocksteam', '', 'Yes', 'No', 'No', 'No', 'No', 'No', 'No'
);

/* INSERT QUERY NO: 22 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'22', '15', 'Gregory Sanders', 'Bitcoin developer', 'Developer', 'Blockstream', '', '', '', '', '', '', '', ''
);

/* INSERT QUERY NO: 23 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'23', '16', 'Karl-Johan Alm', 'Bitcoin developer', 'Developer', 'DG Lab', '', '', '', '', '', '', '', ''
);

/* INSERT QUERY NO: 24 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'24', '17', 'Ben Woosley', 'Bitcoin developer', 'Developer', 'Hardcore Fund', '', '', '', '', '', '', '', ''
);

/* INSERT QUERY NO: 25 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'25', '18', 'Peter Todd', 'Bitcoin developer', 'Developer', '"Has previously received funding from BTCC', ' Bitfinex & Chaincode"', ' Bitcoin developer. Involved with Bitcoin related startup Coinkite and DarkWallet.', '', '', '', '', '', ''
);

/* INSERT QUERY NO: 26 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'26', '19', 'James O?Beirne', 'Bitcoin developer', 'Developer', 'Currently moving from Chaincode to DG Lab', '', '', '', '', '', '', '', ''
);

/* INSERT QUERY NO: 27 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'27', '20', 'Sjors Provoost', 'Bitcoin developer', 'Developer', '"Formerly funded by blockchain.info', ' part time on open source projects for BitMEX"', '', '', '', '', '', '', ''
);

/* INSERT QUERY NO: 28 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'28', '21', 'Patrick Strateman', 'Bitcoin developer', 'Developer', 'Formerly Blockstream', '" Bitcoin developer', ' creator of Intersango', ' member of Bitcoin Consultancy and creator of Python Bitcoin implementation."', 'Yes', 'No', 'No', 'No', 'Author'
);

/* INSERT QUERY NO: 29 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'29', '22', 'Carl Dong', 'Bitcoin developer', 'Developer', 'Chaincode', '', '', '', '', '', '', '', ''
);

/* INSERT QUERY NO: 30 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'30', '23', 'Jon Atack', 'Bitcoin developer', 'Developer', 'Square Crypto grant', '', '', '', '', '', '', '', ''
);

/* INSERT QUERY NO: 31 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'31', '24', 'Anthony Towns', 'Bitcoin developer', 'Developer', 'Xapo', '', '', '', '', '', '', '', ''
);

/* INSERT QUERY NO: 32 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'32', '25', 'Jeremy Rubin', 'Bitcoin developer', 'Developer', 'Formerly MIT DCI & Chaincode', '', '', '', '', '', '', '', ''
);

/* INSERT QUERY NO: 33 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'33', '26', 'Nicolas Dorier', 'Bitcoin and BTCPAY developer', 'Developer', 'DG Lab', '', '', '', '', '', '', '', ''
);

/* INSERT QUERY NO: 34 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'34', '27', 'David Harding', 'Bitcoin developer', 'Developer', '', '', '', '', '', '', '', '', ''
);

/* INSERT QUERY NO: 35 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'36', '\N', 'Fabian Jahr', 'Bitcoin developer', 'Developer', 'OKCoin', '', '', '', '', '', '', '', ''
);

/* INSERT QUERY NO: 36 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'37', '\N', 'Kukks', 'BTCPAY developer', 'Developer', 'BTSE', '', '', '', '', '', '', '', ''
);

/* INSERT QUERY NO: 37 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'38', '\N', 'Amiti Uttarwar', 'Bitcoin developer', 'Developer', 'Xapo', '', '', '', '', '', '', '', ''
);

/* INSERT QUERY NO: 38 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'39', '\N', '', '', '', '', '', '', '', '', '', '', '', ''
);

/* INSERT QUERY NO: 39 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'40', '\N', 'Andreas Schildbach (Profile) ', 'Bitcoin developer', 'Developer', '', ' original developer of Bitcoin Wallet for Android (Google Code Project).', 'No', 'Yes', 'No', 'No', 'No', 'No', 'No'
);

/* INSERT QUERY NO: 40 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'41', '\N', 'Amir Taaki (Wikipedia) aka genjix ', 'Bitcoin developer', 'Developer', '', '" creator of the BIP process', ' Libbitcoin C++ developer toolkit', ' Obelisk blockchain server (later BS)', ' SX bitcoin command line tool (later BX)', ' Darkwallet', ' Darkmarket (later OpenBazaar)', ' Darkleaks', ' Freecoin and well as inactive/defunct projects such as the Britcoin exchange'
);

/* INSERT QUERY NO: 41 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'42', '\N', 'Art Forz ', '', '', '', ' developed the first GPU miner and at one time his GPU mining farm (the ArtFarm) was mining over a third of all blocks.', '', '', '', '', '', '', ''
);

/* INSERT QUERY NO: 42 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'43', '\N', 'Gary Rowe (Profile ) ', '', '', '', ' Contributor to the MultiBit (http://multibit.org) and BitCoinJ (http://code.google.com/p/bitcoinj/) projects. Working on various Bitcoin based businesses.', '', '', '', '', '', '', ''
);

/* INSERT QUERY NO: 43 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'44', '\N', '', '', '', '', '', '', '', '', '', '', '', ''
);

/* INSERT QUERY NO: 44 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'45', '\N', 'Hal Finney ', '', '', '', ' one of the creators of PGP and one of the earliest contributors to the Bitcoin project. First to identify a type of double-spending attack that now bears his name -- the Finney attack.', '', '', '', '', '', '', ''
);

/* INSERT QUERY NO: 45 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'46', '\N', 'James McCarthy aka Nefario ', '', '', '', ' creator of the first bitcoin stock exchange GLBSE', '', '', '', '', '', '', ''
);

/* INSERT QUERY NO: 46 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'47', '\N', 'Jed McCaleb ', '', '', '', ' cofounder of Stellar.org and original developer of MtGox. Previously created eDonkey2000.', '', '', '', '', '', '', ''
);

/* INSERT QUERY NO: 47 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'48', '\N', 'Jeff Garzik ', '', '', '', '" Satoshi client core developer', ' GPU poold software and the founder of Bitcoin Watch. Works for BitPay."', 'Retired', 'No', 'Author', 'No', 'No', 'No'
);

/* INSERT QUERY NO: 48 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'49', '\N', 'Luke Dashjr aka Luke-Jr ', '', '', '', '" Eligius founder', ' maintains BFGMiner and maintainer of bitcoind/Bitcoin-Qt stable branches."', 'Yes', 'No', 'Lead', 'No', '', 'Yes'
);

/* INSERT QUERY NO: 49 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'50', '\N', 'Mark Karpeles (Wikipedia) aka MagicalTux ', '', '', '', ' Former owner of MtGox and this wiki.', '', '', '', '', '', '', ''
);

/* INSERT QUERY NO: 50 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'51', '\N', 'Martti Malmi aka Sirius ', '', '', '', ' Former Bitcoin developer. Operates the domain names bitcoin.org and bitcointalk.org.', '', '', '', '', '', '', ''
);

/* INSERT QUERY NO: 51 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'52', '\N', 'Michael Hendrix aka mndrix ', '', '', '', ' creator of the now defunct CoinPal and CoinCard services', '', '', '', '', '', '', ''
);

/* INSERT QUERY NO: 52 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'53', '\N', 'Mike Hearn ', 'Developer', 'Developer', '', '(Profile) Google engineer who works on Gmail and developed BitCoinJ (http://code.google.com/p/bitcoinj/) and Lighthouse.', 'No', 'Author', 'No', 'No', 'No', 'No', 'No'
);

/* INSERT QUERY NO: 53 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'54', '\N', 'Nils Schneider aka tcatm ', '', '', '', '" Bitcoin developer', ' owner of BitcoinWatch', ' creator and owner of BitcoinCharts', ' GPU mining software and JS web interface."', '', '', '', ''
);

/* INSERT QUERY NO: 54 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'55', '\N', 'Patrick McFarland aka Diablo-D3 ', '', '', '', '" DiabloMiner author', ' and former BitcoinTalk forum moderator."', '', '', '', '', '', ''
);

/* INSERT QUERY NO: 55 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'56', '\N', 'Stefan Thomas aka justmoon ', '', '', '', ' creator of the (We Use Coins) site/video and WebCoin.', '', '', '', '', '', '', ''
);

/* INSERT QUERY NO: 56 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'57', '\N', 'Tamas Blummer aka grau ', '', '', '', '" author of Bits of Proof', ' the enterprise-ready implementation of the Bitcoin protocol. http://bitsofproof.com"', '', '', '', '', '', ''
);

/* INSERT QUERY NO: 57 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'58', '\N', 'Trace Mayer ', '', '', '', ' Host of the (Bitcoin Knowledge Podcast) where the top people in Bitcoin are interviewed', '', '', '', '', '', '', ''
);

/* INSERT QUERY NO: 58 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'59', '\N', 'Vladimir Marchenko ', '', '', '', '" (Profile)', ' runs Marchenko Ltd which sells mining contracts', ' previously developed the figator.org search engine."', '', '', '', '', ''
);

/* INSERT QUERY NO: 59 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'74', '\N', 'fanquake', '', '', '', '', 'Yes', 'No', 'No', 'No', 'No', 'No', 'No'
);

/* INSERT QUERY NO: 60 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'78', '\N', 'Russell Yanofsky', '', '', '', '', 'Yes', 'No', 'No', 'No', 'No', 'No', 'No'
);

/* INSERT QUERY NO: 61 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'79', '\N', 'paveljanik', '', '', '', '', 'Yes', 'No', 'No', 'No', 'No', 'No', 'No'
);

/* INSERT QUERY NO: 62 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'80', '\N', 'cozz', '', '', '', '', 'Yes', 'No', 'No', 'No', 'No', 'No', 'No'
);

/* INSERT QUERY NO: 63 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'84', '\N', 'Kalle Alm', '', '', '', '', 'Yes', 'No', 'No', 'No', 'No', 'No', 'No'
);

/* INSERT QUERY NO: 64 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'85', '\N', 'Eric Lombrozo', '', '', '', '', 'Yes', 'No', 'No', 'No', 'No', 'No', 'No'
);

/* INSERT QUERY NO: 65 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'87', '\N', 'Johnson Lau', '', '', '', '', 'Yes', 'No', 'No', 'No', 'No', 'No', 'No'
);

/* INSERT QUERY NO: 66 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'88', '\N', 'Giel van Schijndel', '', '', '', '', '', 'No', 'No', 'No', 'No', 'No', 'No'
);

/* INSERT QUERY NO: 67 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'89', '\N', 'rebroad', '', '', '', '', 'Yes', 'No', 'No', 'No', 'No', 'No', 'No'
);

/* INSERT QUERY NO: 68 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'90', '\N', 'sirius-m', '', '', '', '', 'Retired', 'No', 'No', 'No', 'No', 'No', 'No'
);

/* INSERT QUERY NO: 69 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'91', '\N', 'Chris Moore', '', '', '', '', '', 'No', 'No', 'No', 'No', 'No', 'No'
);

/* INSERT QUERY NO: 70 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'92', '\N', 'Nils Schneider', '', '', '', '', '', 'No', 'No', 'No', 'No', 'No', 'Yes'
);

/* INSERT QUERY NO: 71 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'94', '\N', 'Diapolo', '', '', '', '', '', 'No', 'No', 'No', 'No', 'No', 'No'
);

/* INSERT QUERY NO: 72 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'97', '\N', 'devrandom', '', '', '', '', 'No', 'Yes', 'No', 'No', 'No', 'No', 'No'
);

/* INSERT QUERY NO: 73 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'98', '\N', 'Daniel Folkinshteyn', '', '', '', '', 'No', 'No', 'No', 'No', 'No', 'No', 'Yes'
);

/* INSERT QUERY NO: 74 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'100', '\N', 'Eric Voskuil', '', '', '', '', 'No', 'No', 'No', 'No', 'Lead', 'No', 'No'
);

/* INSERT QUERY NO: 75 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'101', '\N', 'Phillip Mienk', '', '', '', '', 'No', 'No', 'No', 'No', 'Yes', 'No', 'No'
);

/* INSERT QUERY NO: 76 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'102', '\N', 'William Swanson', '', '', '', '', 'No', 'No', 'No', 'No', 'Yes', 'No', 'No'
);

/* INSERT QUERY NO: 77 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'103', '\N', 'Neill Miller', '', '', '', '', 'No', 'No', 'No', 'No', 'Yes', 'No', 'No'
);

/* INSERT QUERY NO: 78 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'104', '\N', 'Pablo Castellano', '', '', '', '', 'No', 'No', 'No', 'No', 'Yes', 'No', 'No'
);

/* INSERT QUERY NO: 79 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'105', '\N', 'Noel Maersk', '', '', '', '', 'No', 'No', 'No', 'No', 'Yes', 'No', 'No'
);

/* INSERT QUERY NO: 80 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'106', '\N', 'Kamil Domanski', '', '', '', '', '', 'No', 'No', 'No', '', 'No', 'No'
);

/* INSERT QUERY NO: 81 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'107', '\N', 'bobalot', '', '', '', '', 'No', 'No', 'No', 'No', '', 'No', 'No'
);

/* INSERT QUERY NO: 82 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'108', '\N', 'drwasho', '', '', '', '', 'No', 'No', 'No', 'No', '', 'No', 'No'
);

/* INSERT QUERY NO: 83 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'109', '\N', 'saracen', '', '', '', '', '', 'No', 'No', 'No', 'No', 'No', 'No'
);

/* INSERT QUERY NO: 84 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'110', '\N', 'Matt Giuca', '', '', '', '', '', 'No', 'No', 'Yes', 'No', 'No', 'No'
);

/* INSERT QUERY NO: 85 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'111', '\N', 'Con Kolivas', '', '', '', '', 'No', 'No', 'Yes', 'No', 'No', 'No', 'No'
);

/* INSERT QUERY NO: 86 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'112', '\N', 'Mizery De Aria', '', '', '', '', 'No', 'No', 'No', 'No', 'No', 'Author', 'No'
);

/* INSERT QUERY NO: 87 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'113', '\N', 'Laurent Bachelier', '', '', '', '', 'No', 'No', 'No', 'No', 'No', 'Yes', 'No'
);

/* INSERT QUERY NO: 88 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'114', '\N', 'Florian Schmaus', '', '', '', '', 'No', 'No', 'No', 'No', 'No', 'Yes', 'No'
);

/* INSERT QUERY NO: 89 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'115', '\N', 'BioMike', '', '', '', '', 'No', 'No', 'No', 'No', 'No', 'Yes', 'No'
);

/* INSERT QUERY NO: 90 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'116', '\N', 'Marius Hanne', '', '', '', '', '', 'No', 'No', 'No', 'No', 'No', 'No'
);

/* INSERT QUERY NO: 91 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'117', '\N', 'David Francois', '', '', '', '', '', 'No', 'No', 'No', 'No', 'No', 'No'
);

/* INSERT QUERY NO: 92 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'118', '\N', 'Fabian H jr.', '', '', '', '', '', 'No', 'No', 'No', 'No', 'No', 'No'
);

/* INSERT QUERY NO: 93 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'119', '\N', 'ovdeathiam', '', '', '', '', '', 'No', 'No', 'No', 'No', 'No', 'No'
);

/* INSERT QUERY NO: 94 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'120', '\N', 'Michal Zima', '', '', '', '', '', 'No', 'No', 'No', 'No', 'No', 'No'
);

/* INSERT QUERY NO: 95 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'121', '\N', 'Venkatesh Srinivas', '', '', '', '', '', 'No', 'No', 'No', 'No', 'No', 'No'
);

/* INSERT QUERY NO: 96 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'122', '\N', 'Vegard Nossum', '', '', '', '', '', 'No', 'No', 'No', 'No', 'No', 'No'
);

/* INSERT QUERY NO: 97 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'123', '\N', 'JoelKatz', '', '', '', '', '', 'No', 'No', 'No', 'No', 'No', 'No'
);

/* INSERT QUERY NO: 98 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'124', '\N', 'Joerie de Gram', '', '', '', '', '', 'No', 'No', 'No', 'No', 'No', 'No'
);

/* INSERT QUERY NO: 99 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'125', '\N', 'Johannes Henninger', '', '', '', '', '', 'No', 'No', 'No', 'No', 'No', 'No'
);

/* INSERT QUERY NO: 100 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'126', '\N', 'Jeroenz0r', '', '', '', '', '', 'No', 'No', 'No', 'No', 'No', 'No'
);

/* INSERT QUERY NO: 101 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'127', '\N', 'Han Lin Yap', '', '', '', '', '', 'No', 'No', 'No', 'No', 'No', 'No'
);

/* INSERT QUERY NO: 102 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'128', '\N', 'Abraham Jewowich', '', '', '', '', '', 'No', 'No', 'No', 'No', 'No', 'No'
);

/* INSERT QUERY NO: 103 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'129', '\N', 'Michael Bemmerl', '', '', '', '', '', 'No', 'No', 'No', 'No', 'No', 'No'
);

/* INSERT QUERY NO: 104 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'130', '\N', 'Eric Hosmer', '', '', '', '', '', 'No', 'No', 'No', 'No', 'No', 'No'
);

/* INSERT QUERY NO: 105 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'131', '\N', 'Dawid Spiechowicz', '', '', '', '', '', 'No', 'No', 'No', 'No', 'No', 'No'
);

/* INSERT QUERY NO: 106 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'132', '\N', 'Stephane Gimenez', '', '', '', '', '', 'No', 'No', 'No', 'No', 'No', 'No'
);

/* INSERT QUERY NO: 107 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'133', '\N', 'Patrick Varilly', '', '', '', '', '', 'No', 'No', 'No', 'No', 'No', 'No'
);

/* INSERT QUERY NO: 108 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'134', '\N', 'Jay Weisskopf', '', '', '', '', '', 'No', 'No', 'No', 'No', 'No', 'No'
);

/* INSERT QUERY NO: 109 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'135', '\N', 'Dylan Noblesmith', '', '', '', '', '', 'No', 'No', 'No', 'No', 'No', 'No'
);

/* INSERT QUERY NO: 110 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'136', '\N', 'James Burkle', '', '', '', '', '', 'No', 'No', 'No', 'No', 'No', 'No'
);

/* INSERT QUERY NO: 111 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'137', '\N', 'Jordan Lewis', '', '', '', '', '', 'No', 'No', 'No', 'No', 'No', 'No'
);

/* INSERT QUERY NO: 112 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'138', '\N', 'Dean Lee', '', '', '', '', '', 'No', 'No', 'No', 'No', 'No', 'No'
);

/* INSERT QUERY NO: 113 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'139', '\N', 'xHire', '', '', '', '', '', 'No', 'No', 'No', 'No', 'No', 'No'
);

/* INSERT QUERY NO: 114 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'140', '\N', 'HostFat', '', '', '', '', '', 'No', 'No', 'No', 'No', 'No', 'No'
);

/* INSERT QUERY NO: 115 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'141', '\N', 'Jakob Kramer', '', '', '', '', '', 'No', 'No', 'No', 'No', 'No', 'No'
);

/* INSERT QUERY NO: 116 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'142', '\N', 'ariel', '', '', '', '', '', 'No', 'No', 'No', 'No', 'No', 'No'
);

/* INSERT QUERY NO: 117 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'143', '\N', 'dabaopku', '', '', '', '', '', 'No', 'No', 'No', 'No', 'No', 'No'
);

/* INSERT QUERY NO: 118 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'144', '\N', 'Federico Faggiano', '', '', '', '', '', 'No', 'No', 'No', 'No', 'No', 'No'
);

/* INSERT QUERY NO: 119 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'145', '\N', 'm0ray', '', '', '', '', '', 'No', 'No', 'No', 'No', 'No', 'No'
);

/* INSERT QUERY NO: 120 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'146', '\N', 'Danube', '', '', '', '', '', 'No', 'No', 'No', 'No', 'No', 'No'
);

/* INSERT QUERY NO: 121 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'147', '\N', 'Carlos Pizarro', '', '', '', '', '', 'No', 'No', 'No', 'No', 'No', 'No'
);

/* INSERT QUERY NO: 122 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'148', '\N', 'Blitzboom', '', '', '', '', '', 'No', 'No', 'No', 'No', 'No', 'No'
);

/* INSERT QUERY NO: 123 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'149', '\N', 'Daniel Holbert', '', '', '', '', '', 'No', 'No', 'No', 'No', 'No', 'No'
);

/* INSERT QUERY NO: 124 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'150', '\N', 'Jaromil', '', '', '', '', '', 'No', 'No', 'No', '', 'No', 'No'
);

/* INSERT QUERY NO: 125 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'151', '\N', 'Carlo Alberto Ferraris', '', '', '', '', '', 'No', 'No', 'No', 'No', 'No', 'No'
);

/* INSERT QUERY NO: 126 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'152', '\N', 'Forrest Voight', '', '', '', '', '', 'No', 'No', 'No', 'No', 'No', 'No'
);

/* INSERT QUERY NO: 127 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'153', '\N', 'Amir Yalon', '', '', '', '', '', 'No', 'No', 'No', 'No', 'No', 'No'
);

/* INSERT QUERY NO: 128 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'154', '\N', 'John Maguire', '', '', '', '', '', 'No', 'No', 'No', 'No', 'No', 'No'
);

/* INSERT QUERY NO: 129 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'155', '\N', 'Ricardo M. Correia', '', '', '', '', '', 'No', 'No', 'No', 'No', 'No', 'No'
);

/* INSERT QUERY NO: 130 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'156', '\N', 'Dan Helfman', '', '', '', '', '', 'No', 'No', 'No', 'No', 'No', 'No'
);

/* INSERT QUERY NO: 131 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'157', '\N', 'gjs278', '', '', '', '', '', 'No', 'No', 'No', 'No', 'No', 'No'
);

/* INSERT QUERY NO: 132 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'158', '\N', 'Dan Loewenherz', '', '', '', '', '', 'No', 'No', 'No', 'No', 'No', 'No'
);

/* INSERT QUERY NO: 133 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'159', '\N', 'Eric Swanson', '', '', '', '', '', 'No', 'No', 'No', 'No', 'No', 'No'
);

/* INSERT QUERY NO: 134 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'160', '\N', 'Santiago M. Mola', '', '', '', '', '', 'No', 'No', 'No', 'No', 'No', 'No'
);

/* INSERT QUERY NO: 135 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'161', '\N', 'Shane Wegner', '', '', '', '', '', 'No', 'No', 'No', 'No', 'No', 'No'
);

/* INSERT QUERY NO: 136 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'162', '\N', 'Sven Slootweg', '', '', '', '', '', 'No', 'No', 'No', 'No', 'No', 'No'
);

/* INSERT QUERY NO: 137 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'163', '\N', 'ojab', '', '', '', '', '', 'No', 'No', 'No', 'No', 'No', 'No'
);

/* INSERT QUERY NO: 138 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'164', '\N', 'sandos', '', '', '', '', '', 'No', 'No', 'No', 'No', 'No', 'No'
);

/* INSERT QUERY NO: 139 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'165', '\N', 'Witchspace', '', '', '', '', '', 'No', 'No', 'No', 'No', 'No', 'No'
);

/* INSERT QUERY NO: 140 */
INSERT INTO roles(id, RankByCommits, Developer, Description, Role, Funding, Details, Reference, bitcoinj, BFGMiner, Tools, Libbitcoin, Gentoo, Supybot)
VALUES
(
'166', '\N', 'laszlo', '', '', '', '', '', 'No', 'No', 'No', 'No', 'No', 'No'
);

