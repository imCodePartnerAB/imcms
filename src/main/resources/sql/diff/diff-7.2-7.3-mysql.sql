SET @schema_version__major_new = 7;
SET @schema_version__minor_new = 3;

CREATE TABLE template_template_group(
    template_id INT NOT NULL,
    group_id    INT NOT NULL,
    PRIMARY KEY (template_id, group_id),
    CONSTRAINT template_template_group_FK_template_id_template FOREIGN KEY (template_id) REFERENCES template (template_id),
    CONSTRAINT template_template_group_FK_group_id_template_group FOREIGN KEY (group_id) REFERENCES template_group (group_id)
);

INSERT INTO template_template_group
SELECT template_id, template_group_id FROM template WHERE template_group_id IS NOT NULL;

ALTER TABLE template DROP FOREIGN KEY fk__template__template_groups;
ALTER TABLE template DROP COLUMN template_group_id;

UPDATE database_version
SET major = @schema_version__major_new,
    minor = @schema_version__minor_new;