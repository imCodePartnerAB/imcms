SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 73;

ALTER TABLE imcms_template_group_crossref
  DROP FOREIGN KEY imcms_template_group_crossref_group_id_fk_imcms_template_group;
ALTER TABLE imcms_template_group_crossref
  ADD CONSTRAINT imcms_template_group_crossref_group_id_fk_imcms_template_group
FOREIGN KEY (group_id) REFERENCES imcms_template_group (group_id)
  ON DELETE CASCADE
  ON UPDATE CASCADE;


ALTER TABLE imcms_template_group_crossref
  DROP FOREIGN KEY imcms_template_group_crossref_template_name_fk_template;
ALTER TABLE imcms_template_group_crossref
  ADD CONSTRAINT imcms_template_group_crossref_template_name_fk_template
FOREIGN KEY (template_name) REFERENCES template (template_name)
  ON DELETE CASCADE
  ON UPDATE CASCADE;


UPDATE database_version
SET major = @schema_version__major_new,
    minor = @schema_version__minor_new;