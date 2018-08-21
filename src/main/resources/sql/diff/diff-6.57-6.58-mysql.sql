# new column for text field filtering policy

SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 58;

ALTER TABLE imcms_text_doc_texts
  ADD COLUMN html_filtering_policy varchar(32) not null default 'UNSET';

ALTER TABLE imcms_text_doc_texts_history
  ADD COLUMN html_filtering_policy varchar(32) not null default 'UNSET';


UPDATE database_version
SET major = @schema_version__major_new,
    minor = @schema_version__minor_new;
