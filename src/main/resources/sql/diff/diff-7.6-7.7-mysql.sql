SET @schema_version__major_new = 7;
SET @schema_version__minor_new = 7;

alter table users
	add column 2fa_enabled       boolean default true not null after external,
	add column one_time_password varchar(255) unicode after 2fa_enabled;

UPDATE database_version
SET major = @schema_version__major_new,
    minor = @schema_version__minor_new;