SET @schema_version__major_new = 7;
SET @schema_version__minor_new = 11;

ALTER TABLE meta
	ADD COLUMN imported int default 0;

CREATE TABLE import_template_references
(
	id               INT(11)      NOT NULL UNIQUE AUTO_INCREMENT,
	name             VARCHAR(128) NOT NULL UNIQUE,
	linked_entity_id INT(11),
	KEY import_template_references_FK_linked_entity_id_templates (linked_entity_id),
	CONSTRAINT import_template_references_FK_linked_entity_id_templates FOREIGN KEY (linked_entity_id) REFERENCES template (template_id)
);

CREATE TABLE import_category_references
(
	id               INT(11)      NOT NULL UNIQUE AUTO_INCREMENT,
	name             VARCHAR(128) NOT NULL UNIQUE,
	linked_entity_id INT(11),
	KEY import_category_references_FK_linked_entity_id_categories (linked_entity_id),
	CONSTRAINT import_category_references_FK_linked_entity_id_categories FOREIGN KEY (linked_entity_id) REFERENCES categories (category_id)
);

CREATE TABLE import_category_type_references
(
	id               INT(11)      NOT NULL UNIQUE AUTO_INCREMENT,
	name             VARCHAR(128) NOT NULL UNIQUE,
	linked_entity_id INT(11),
	KEY imprt_ctgry_type_references_FK_linked_entity_id_category_types (linked_entity_id),
	CONSTRAINT import_category_type_FK_linked_entity_id_category_types FOREIGN KEY (linked_entity_id) REFERENCES category_types (category_type_id)
);

CREATE TABLE import_role_references
(
	id               INT(11)      NOT NULL UNIQUE AUTO_INCREMENT,
	name             VARCHAR(128) NOT NULL UNIQUE,
	linked_entity_id INT(11),
	KEY import_role_references_FK_linked_entity_id_roles (linked_entity_id),
	CONSTRAINT import_role_references_FK_linked_entity_id_roles FOREIGN KEY (linked_entity_id) REFERENCES roles (role_id)
);

create table basic_import_documents_info
(
	id      INT(11) NOT NULL UNIQUE,
	meta_id INT(11),
	status  VARCHAR(128)
);


UPDATE database_version
SET major = @schema_version__major_new,
    minor = @schema_version__minor_new;
