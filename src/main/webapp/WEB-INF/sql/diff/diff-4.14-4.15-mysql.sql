ALTER TABLE images ADD resize integer;
ALTER TABLE images_history ADD resize integer;

UPDATE images SET resize = 0;
UPDATE images_history SET resize = 0;

-- 1-default, 2-force, 3-less_than, 4-greater_than, 5-percent
ALTER TABLE images MODIFY resize integer NOT NULL;
ALTER TABLE images_history MODIFY resize integer NOT NULL;

UPDATE database_version SET major = 4, minor = 15;