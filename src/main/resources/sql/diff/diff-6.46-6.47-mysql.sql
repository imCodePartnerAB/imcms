# texts repair may be needed

SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 47;

delete from imcms_text_doc_texts
where id not in (
  select id
  from (
         select MAX(t.id) as id
         from imcms_text_doc_texts t
         group by t.doc_id, t.doc_version_no, t.`index`, t.language_id, t.loop_index, t.loop_entry_index
       ) help_me
);

UPDATE database_version
SET
  major = @schema_version__major_new,
  minor = @schema_version__minor_new;
