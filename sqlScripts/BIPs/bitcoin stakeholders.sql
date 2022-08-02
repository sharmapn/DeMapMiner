CREATE TABLE stakeholders (
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  Stakeholder VARCHAR(255),
  Description VARCHAR(255)
);
INSERT INTO stakeholders
  (Stakeholder,Description)
VALUES
  ('Academia ','Universities that have set up centers to research and support the growth of Bitcoin, for example MIT''s Digital Currency Initiative (DCI), which supports bitcoin core developers. See also Research and Development Initiatives. '),
  ('Associations ',' Trade and industry associations that represent companies that use the technology underlying bitcoin (Blockchain). For example UK Digital Currency Association (UKDCA) and Australian Digital Currency Commerce Association (ADCCA). '),
  ('BitCoin Advocacy Groups ',' Non-profit groups who provide research and education on Cryptocurrencies. For example Bitcoin Foundation and Coin Center. '),
  ('Customers ',' People who buy goods using Bitcoin. '),
  ('BitCoin ATMs ',' Providers of BTMs which are modelled after the ATMs (or cash machines). '),
  ('BitCoin Developers ',' The developers of the core code. '),
  ('Freelancers ',' A growing group of people who prefer to be paid in Bitcoin. They may be in full-time employment or take gigs or contracts via Bitcoin freelancer job sites. '),
  ('Investors ',' People and organisations that invest in Bitcoin with the expectation of gaining a profit. '),
  ('Bitcoin Lenders ',' Users of peer to peer Bitcoin lending sites who lend Bitcoins for an agreed interest rate. For example see Bitbond. '),
  ('Merchants ',' Businesses that accept or use Bitcoin. '),
  ('Miners ',' Individuals, groups and companies who provide computer power to mine and record the transactions within the Blockchain. They receive mining rewards (Bitcoins) in exchange for the resources they provide. '),
  ('Mining Hardware providers ',' Providers of mining hardware, for example Bitmain. '),
  ('Mining Pools ',' Miners work together in a pool sharing block rewards among the miners. Examples are AntPool and BitFury Pool '),
  ('Research and Development initiatives ',' Groups involved in research and development of the Blockchain for example IC3 The Initiative For CryptoCurrencies & Contracts '),
  ('Speculators ',' Short term investors in Bitcoin who take larger risks in the hope of quick gains. '),
  ('Tax regulators ',' E.g. IRS - country tax collection departments are interested in BitCoin in relation to tax obligations for example the IRS has said that income from mining could constitute self-employment income and therefore be subject to tax. '),
  ('Traders ',' Those who buy and sell Bitcoin as agents, hedgers or speculators. '),
  ('Government Treasuries ',' E.g. U.S. Treasury - BitCoin is not controlled or backed by any institution or country as such treasuries are making decisions on the status of stakeholders involved in Bitcoin. For example the status of miner as Money Transmitters. '),
  ('Wallets ',' Providers of Bitcoin wallets for example Coinbase, Blockchain.info or StrongCoin.');
