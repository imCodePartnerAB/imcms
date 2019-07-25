SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 71;

ALTER TABLE `text_docs`
  DROP FOREIGN KEY `text_docs_template_name_fk_template`;
ALTER TABLE `text_docs`
  ADD CONSTRAINT text_docs_template_name_fk_template
FOREIGN KEY (template_name) REFERENCES template (template_name)
  ON DELETE CASCADE
  ON UPDATE CASCADE;


ALTER TABLE `text_docs`
  DROP FOREIGN KEY `text_docs_children_template_name_fk_template`;
ALTER TABLE `text_docs`
  ADD CONSTRAINT text_docs_children_template_name_fk_template
FOREIGN KEY (children_template_name) REFERENCES template (template_name)
  ON DELETE CASCADE
  ON UPDATE CASCADE;

UPDATE database_version
SET major = @schema_version__major_new,
    minor = @schema_version__minor_new;

