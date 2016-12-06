ALTER TABLE `database_version`
    DROP PRIMARY KEY,
  ADD COLUMN `client` int(11) DEFAULT 0,
  ADD PRIMARY KEY (`major`,`minor`,`client`);

UPDATE database_version SET major = 4, minor = 18, client = 0;
