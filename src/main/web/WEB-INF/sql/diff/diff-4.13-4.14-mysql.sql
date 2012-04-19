ALTER TABLE images_cache ADD meta_id integer;
ALTER TABLE images_cache ADD no integer;
ALTER TABLE images_cache ADD file_no nvarchar(100);
-- 1-default, 2-force, 3-less_than, 4-greater_than, 5-percent
ALTER TABLE images_cache ADD resize integer;

UPDATE database_version SET major = 4, minor = 14;