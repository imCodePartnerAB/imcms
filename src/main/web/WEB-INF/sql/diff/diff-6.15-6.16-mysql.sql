-- normalizes imcms_text_doc_contents table
-- creates view on imcms_text_doc_contents

SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 16;


ALTER TABLE fileupload_docs
DROP FOREIGN KEY fk__fileupload_docs__doc_version;
ALTER TABLE fileupload_docs
CHANGE COLUMN meta_id doc_id INT(11) NOT NULL ;
ALTER TABLE fileupload_docs
ADD CONSTRAINT fk__fileupload_docs__doc_version
  FOREIGN KEY (doc_id , doc_version_no)
  REFERENCES imcms_doc_versions (doc_id , no)
  ON DELETE CASCADE;

--
-- Update schema version
--
UPDATE database_version
SET
  major = @schema_version__major_new,
  minor = @schema_version__minor_new;



