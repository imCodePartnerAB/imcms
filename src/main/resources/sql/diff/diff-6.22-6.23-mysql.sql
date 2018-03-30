SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 23;

CREATE TABLE imcms_menu (
  id             INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  no             INT UNSIGNED NOT NULL,
  doc_id         INT          NOT NULL,
  doc_version_no INT          NOT NULL,
  CONSTRAINT imcms_menu_fk_doc_id_no_imcms_doc_versions UNIQUE KEY (doc_id, doc_version_no, no),
  FOREIGN KEY imcms_menu_fk_doc_id_doc_version_no_imcms_doc_versions (doc_id, doc_version_no) REFERENCES imcms_doc_versions (doc_id, no)
    ON DELETE CASCADE
);

CREATE TABLE imcms_menu_item (
  id             INT UNSIGNED          AUTO_INCREMENT PRIMARY KEY,
  menu_id        INT UNSIGNED,
  parent_item_id INT UNSIGNED,
  sort_order     INT UNSIGNED NOT NULL DEFAULT 1,
  document_id    INT          NOT NULL,
  CONSTRAINT imcms_menu_item_fk_id_menu FOREIGN KEY (menu_id) REFERENCES imcms_menu (id)
    ON DELETE CASCADE,
  CONSTRAINT imcms_menu_item_fk_meta_id_meta FOREIGN KEY (document_id) REFERENCES meta (meta_id)
    ON DELETE CASCADE,
  CONSTRAINT imcms_menu_item_fk_id_imcms_menu_item FOREIGN KEY (parent_item_id) REFERENCES imcms_menu_item (id)
    ON DELETE CASCADE
);

UPDATE database_version
SET
  major = @schema_version__major_new,
  minor = @schema_version__minor_new;