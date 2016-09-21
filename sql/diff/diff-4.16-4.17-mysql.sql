ALTER TABLE url_docs
  MODIFY COLUMN url_ref TEXT NOT NULL;

UPDATE database_version SET major = 4, minor = 17;