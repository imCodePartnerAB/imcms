ALTER TABLE url_docs
   ALTER COLUMN url_ref NVARCHAR(2000) NOT NULL;

UPDATE database_version SET major = 4, minor = 17;