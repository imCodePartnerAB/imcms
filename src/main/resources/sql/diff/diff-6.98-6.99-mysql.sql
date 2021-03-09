SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 99;

CREATE TABLE imcms_last_time_use (
  id int NOT NULL PRIMARY KEY default 1,
  time_last_reindex datetime,
  time_last_remove_public_cache datetime,
  time_last_remove_static_cache datetime,
  time_last_remove_other_cache datetime,
  time_last_build_cache datetime
);

INSERT INTO imcms_last_time_use (id) VALUES (1);

UPDATE database_version
SET major = @schema_version__major_new,
    minor = @schema_version__minor_new;