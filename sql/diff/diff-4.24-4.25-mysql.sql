ALTER TABLE `meta`
	ADD COLUMN `export_allowed` boolean default true,
	ADD COLUMN `exported`       boolean default false;

UPDATE database_version
SET major = 4,
    minor = 25;
