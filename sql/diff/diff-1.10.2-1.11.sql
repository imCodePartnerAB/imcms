ALTER TABLE fileupload_docs ADD variant_name VARCHAR(100) NOT NULL DEFAULT ''
ALTER TABLE fileupload_docs ADD default_variant BIT NOT NULL DEFAULT 0
ALTER TABLE fileupload_docs DROP CONSTRAINT PK_fileupload_docs
ALTER TABLE fileupload_docs ADD CONSTRAINT PK_fileupload_docs PRIMARY KEY ( meta_id, variant_name )

-- 2004-07-27 Kreiger
