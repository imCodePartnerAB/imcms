SET @schema_version__major_new = 7;
SET @schema_version__minor_new = 9;

ALTER TABLE meta
    ADD COLUMN cache_for_unauthorized int NOT NULL default true,
    ADD COLUMN cache_for_authorized int NOT NULL default false;

UPDATE meta SET cache_for_unauthorized = true, cache_for_authorized = false WHERE meta_id IN (SELECT meta_id FROM text_docs);

UPDATE database_version
SET major = @schema_version__major_new,
    minor = @schema_version__minor_new;