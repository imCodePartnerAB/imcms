SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 23;

DROP TABLE menus;
DROP TABLE menus_history;

CREATE TABLE imcms_menu (
  id             INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  no             INT UNSIGNED NOT NULL,
  doc_id         INT          NOT NULL,
  doc_version_no INT          NOT NULL,
  CONSTRAINT imcms_menu_fk_doc_id_no_imcms_doc_versions UNIQUE KEY (doc_id, doc_version_no, no),
  FOREIGN KEY (doc_id, doc_version_no) REFERENCES imcms_doc_versions (doc_id, no)
    ON DELETE CASCADE
);

CREATE TABLE imcms_menu_history (
  id             INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  no             INT UNSIGNED NOT NULL,
  doc_id         INT          NOT NULL,
  doc_version_no INT          NOT NULL,
  modified_date  DATETIME     NOT NULL,
  user_id        INT UNSIGNED NOT NULL
);

#
# CREATE TABLE imcms_menu_item (
#   id      INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
#   menu_id INT UNSIGNED,
#   parent_item_id INT UNSIGNED,
#   sort_order INT UNSIGNED,
#   CONSTRAINT imcms_menu_item_fk_id_menu FOREIGN KEY (menu_id) REFERENCES imcms_menu (id),
#   CONSTRAINT imcms_menu_item_fk_id_imcms_menu_item FOREIGN KEY (parent_item_id) REFERENCES imcms_menu_item (id)
#
# );
#
# CREATE TABLE imcms_menu_item_history (
#   id      INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
#   menu_id INT UNSIGNED,
#   parent_item_id INT UNSIGNED,
#   sort_order INT UNSIGNED
# );

UPDATE database_version
SET
  major = @schema_version__major_new,
  minor = @schema_version__minor_new;