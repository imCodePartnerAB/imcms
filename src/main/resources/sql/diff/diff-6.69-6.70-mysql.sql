SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 70;

ALTER TABLE `imcms_template_group`
    RENAME `template_group`;

ALTER TABLE `template`
    ADD COLUMN `template_group_id` INT(11) null,
    ADD CONSTRAINT fk__template__template_groups FOREIGN KEY (template_group_id) REFERENCES template_group (group_id);

UPDATE `template` t
    INNER JOIN `imcms_template_group_crossref` ref
    ON t.template_name = ref.template_name
SET t.template_group_id = ref.group_id;

DROP TABLE IF EXISTS `imcms_template_group_crossref`;

UPDATE database_version
SET major = @schema_version__major_new,
    minor = @schema_version__minor_new;