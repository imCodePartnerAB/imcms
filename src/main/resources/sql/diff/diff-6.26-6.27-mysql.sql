SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 27;

UPDATE doc_types SET doc_type = 0 WHERE doc_type = 8; #FILE
UPDATE doc_types SET doc_type = 1 WHERE doc_type = 7; #HTML
UPDATE doc_types SET doc_type = 3 WHERE doc_type = 5; #URL

UPDATE meta SET doc_type = 0 WHERE doc_type = 8; #FILE
UPDATE meta SET doc_type = 1 WHERE doc_type = 7; #HTML
UPDATE meta SET doc_type = 3 WHERE doc_type = 5; #URL

UPDATE database_version
SET
  major = @schema_version__major_new,
  minor = @schema_version__minor_new;


