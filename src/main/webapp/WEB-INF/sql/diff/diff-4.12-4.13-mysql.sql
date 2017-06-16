ALTER TABLE images ADD COLUMN gen_file varchar(255);
ALTER TABLE images_history ADD COLUMN gen_file varchar(255);

UPDATE database_version SET major = 4, minor = 13;