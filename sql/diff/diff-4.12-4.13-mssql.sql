ALTER TABLE images ADD gen_file nvarchar(255);
ALTER TABLE images_history ADD gen_file nvarchar(255);

UPDATE database_version SET major = 4, minor = 13;

