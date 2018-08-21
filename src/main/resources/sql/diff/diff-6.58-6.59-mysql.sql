# text type "CLEAN_HTML" removed

SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 59;


UPDATE imcms_text_doc_texts
SET `type` = 'EDITOR'
WHERE `type` = 'CLEAN_HTML';

UPDATE imcms_text_doc_texts_history
SET `type` = 'EDITOR'
WHERE `type` = 'CLEAN_HTML';


UPDATE database_version
SET major = @schema_version__major_new,
    minor = @schema_version__minor_new;
