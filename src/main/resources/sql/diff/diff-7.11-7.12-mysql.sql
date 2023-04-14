SET @schema_version__major_new = 7;
SET @schema_version__minor_new = 12;

ALTER TABLE import_template_references
	DROP FOREIGN KEY import_template_references_FK_linked_entity_id_templates;
ALTER TABLE import_category_references
	DROP FOREIGN KEY import_category_references_FK_linked_entity_id_categories;
ALTER TABLE import_category_type_references
	DROP FOREIGN KEY import_category_type_FK_linked_entity_id_category_types;
ALTER TABLE import_role_references
	DROP FOREIGN KEY import_role_references_FK_linked_entity_id_roles;


UPDATE database_version
SET major = @schema_version__major_new,
    minor = @schema_version__minor_new;
