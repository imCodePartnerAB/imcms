SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 38;

ALTER TABLE template
  DROP PRIMARY KEY,
  DROP COLUMN id,
  MODIFY template_name VARCHAR(64) NOT NULL
  COLLATE utf8_swedish_ci,
  ADD PRIMARY KEY (template_name);

INSERT INTO template (template_name, is_hidden) VALUES ('demo', 0)
ON DUPLICATE KEY UPDATE template_name = template_name;

insert into template (template_name, is_hidden)
  select
    tg.template_name,
    0
  from templates_cref tg
ON DUPLICATE KEY UPDATE template.template_name = tg.template_name;

ALTER TABLE templategroups
  RENAME imcms_template_group;

ALTER TABLE templates_cref
  RENAME imcms_template_group_crossref,
  DROP PRIMARY KEY,
  MODIFY template_name VARCHAR(64) NOT NULL
  COLLATE utf8_swedish_ci,
  ADD PRIMARY KEY (group_id, template_name),
  ADD CONSTRAINT imcms_template_group_crossref_group_id_fk_imcms_template_group
FOREIGN KEY (group_id) REFERENCES imcms_template_group (group_id)
  ON DELETE CASCADE,
  ADD CONSTRAINT imcms_template_group_crossref_template_name_fk_template
FOREIGN KEY (template_name) REFERENCES template (template_name)
  ON DELETE CASCADE;

ALTER TABLE text_docs
  MODIFY template_name VARCHAR(64) NOT NULL
  COLLATE utf8_swedish_ci,
  MODIFY children_template_name VARCHAR(64) NOT NULL
  COLLATE utf8_swedish_ci,
  DROP COLUMN group_id,
  ADD CONSTRAINT text_docs_template_name_fk_template
FOREIGN KEY (template_name) REFERENCES template (template_name)
  ON DELETE CASCADE,
  ADD CONSTRAINT text_docs_children_template_name_fk_template
FOREIGN KEY (children_template_name) REFERENCES template (template_name)
  ON DELETE CASCADE;

UPDATE database_version
SET
  major = @schema_version__major_new,
  minor = @schema_version__minor_new;
