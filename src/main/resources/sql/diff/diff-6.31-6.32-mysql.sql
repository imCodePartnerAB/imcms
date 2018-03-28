# removing no more used things and created new document restricted permissions table
START TRANSACTION;

SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 32;

ALTER TABLE `meta`
  DROP COLUMN `lang_prefix`;

ALTER TABLE `meta`
  DROP COLUMN `activate`;

DROP TABLE `doc_permission_sets`;
DROP TABLE `new_doc_permission_sets`;
DROP TABLE `doc_permission_sets_ex`;
DROP TABLE `new_doc_permission_sets_ex`;
DROP TABLE `permission_sets`;

CREATE TABLE `imcms_doc_restricted_permissions` (

  meta_id       INT         NOT NULL,
  permission    VARCHAR(16) NOT NULL,
  edit_text     TINYINT(1)  NOT NULL,
  edit_menu     TINYINT(1)  NOT NULL,
  edit_image    TINYINT(1)  NOT NULL,
  edit_loop     TINYINT(1)  NOT NULL,
  edit_doc_info TINYINT(1)  NOT NULL,

  UNIQUE KEY (meta_id, permission),
  CONSTRAINT fk__imcms_doc_restricted_permissions__meta FOREIGN KEY (meta_id) REFERENCES meta (meta_id)
    ON DELETE CASCADE

)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

UPDATE database_version
SET
  major = @schema_version__major_new,
  minor = @schema_version__minor_new;

COMMIT;
