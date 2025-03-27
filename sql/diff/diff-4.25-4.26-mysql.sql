ALTER TABLE fileupload_docs
    MODIFY COLUMN mime VARCHAR(255) NOT NULL;

UPDATE database_version
SET major = 4,
    minor = 25;
