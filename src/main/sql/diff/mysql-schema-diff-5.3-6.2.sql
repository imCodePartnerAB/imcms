
-- New schema version to assign after upgrade
SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 2;







-- fix i18n language => imcms_doc_languages !!!

CREATE TABLE imcms_doc_keywords (

    id int AUTO_INCREMENT,
    doc_id int NOT NULL,
    value varchar(128) NOT NULL,

    CONSTRAINT pk__imcms_doc_keywords PRIMARY KEY (id),
    CONSTRAINT uk__imcms_doc_keywords__doc_id__value UNIQUE KEY (doc_id, value),
    CONSTRAINT fk__imcms_doc_keywords__meta FOREIGN KEY (doc_id) REFERENCES meta (meta_id) ON DELETE CASCADE

) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- imcms_doc_enabled_languages
CREATE TABLE imcms_doc_languages (

    id int AUTO_INCREMENT,
    doc_id int NOT NULL,
    language_id smallint NOT NULL,

    CONSTRAINT pk__imcms_doc_languages PRIMARY KEY (id),
    CONSTRAINT uk__imcms_doc_languages__doc_id__language_id UNIQUE KEY (doc_id, language_id),
    CONSTRAINT fk__imcms_doc_languages__meta FOREIGN KEY (doc_id) REFERENCES meta (meta_id) ON DELETE CASCADE,
    CONSTRAINT fk__imcms_doc_languages__i18n_languages FOREIGN KEY (language_id) REFERENCES i18n_languages (language_id) ON DELETE RESTRICT

) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- select enabled languages from i8n_metas



--
-- Document version support.
--

CREATE TABLE imcms_doc_versions (
    id int AUTO_INCREMENT,
    doc_id int NOT NULL,
    no int NOT NULL,
    created_by INT NULL,
    created_dt datetime NOT NULL,
    -- modified by, dt, etc

    CONSTRAINT pk__imcms_doc_versions PRIMARY KEY (id),
    CONSTRAINT uk__imcms_doc_versions__doc_id__no UNIQUE KEY (doc_id, no),
    CONSTRAINT fk__imcms_doc_versions__meta FOREIGN KEY (doc_id) REFERENCES meta (meta_id) ON DELETE CASCADE,
    CONSTRAINT fk__imcms_doc_versions__user FOREIGN KEY (created_by) REFERENCES users (user_id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO
    imcms_doc_versions (doc_id, no, created_by, created_dt)
SELECT
    meta_id, 0, owner_id, date_created
FROM
    meta;


CREATE TABLE imcms_doc_default_version (
    id int AUTO_INCREMENT,
    doc_id int NOT NULL,
    version_no int NOT NULL DEFAULT 0,

    CONSTRAINT pk__imcms_doc_default_version PRIMARY KEY (id),
    CONSTRAINT uk__imcms_doc_default_version__doc_id__version_no UNIQUE KEY (doc_id, version_no),
    CONSTRAINT fk__imcms_doc_default_version__doc_versions FOREIGN KEY (doc_id, version_no) REFERENCES imcms_doc_versions (doc_id, no) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO imcms_doc_default_version (doc_id, version_no)
SELECT doc_id, no
FROM imcms_doc_versions;

--
-- Document labels
--
-- add ref to version
CREATE TABLE imcms_doc_labels (

    id int AUTO_INCREMENT,
    doc_id int NOT NULL,
    doc_version_no int NOT NULL,
    language_id smallint NOT NULL,
    headline varchar(256) NULL,
    menu_image_url varchar(256) NULL,
    menu_text varchar(1024) NULL,

    CONSTRAINT pk__imcms_doc_labels PRIMARY KEY (id),
    CONSTRAINT uk__imcms_doc_labels__doc_id__doc_version_no__language_id UNIQUE KEY (doc_id, doc_version_no, language_id),
    CONSTRAINT fk__imcms_doc_labels__meta FOREIGN KEY (doc_id) REFERENCES meta (meta_id) ON DELETE CASCADE,
    CONSTRAINT fk__imcms_doc_labels__i18n_languages FOREIGN KEY (language_id) REFERENCES i18n_languages (language_id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO imcms_doc_labels (
    doc_id,
    doc_version_no,
    language_id,
    headline,
    menu_image_url,
    menu_text
) SELECT
    meta_id,
    0,
    language_id,
    meta_headline,
    meta_image,
    meta_text
FROM i18n_meta;

DROP TABLE i18n_meta;


--
-- Content loops support
--
CREATE TABLE imcms_text_doc_content_loops (
    id int AUTO_INCREMENT,
    doc_id int NOT NULL,
    doc_version_no int NOT NULL,
    no int NOT NULL,

    CONSTRAINT pk__imcms_text_doc_content_loops PRIMARY KEY (id),
    UNIQUE KEY uk__imcms_text_doc_content_loops (doc_id, doc_version_no, no),
    CONSTRAINT fk__imcms_text_doc_content_loops__imcms_doc_versions FOREIGN KEY (doc_id, doc_version_no) REFERENCES imcms_doc_versions (doc_id, no) ON DELETE CASCADE

) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- Contents are never deleted physically - they are disabled.
CREATE TABLE imcms_text_doc_contents (
  id int NOT NULL AUTO_INCREMENT,
  doc_id int NOT NULL,
  doc_version_no int NOT NULL,
  loop_no int DEFAULT NULL,
  no int NOT NULL,
  order_no int NOT NULL,
  enabled tinyint NOT NULL DEFAULT TRUE,
        
  CONSTRAINT pk__imcms_text_doc_contents PRIMARY KEY (id),
  UNIQUE KEY uk__imcms_text_doc_contents (doc_id, doc_version_no, loop_no, no)
  -- ,
  -- CONSTRAINT fk__imcms_text_doc_contents__imcms_text_doc_content_loops FOREIGN KEY (loop_id) REFERENCES imcms_text_doc_content_loops (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- drop old content and loops tables


-- text documents texts
CREATE TABLE imcms_text_doc_texts (
    id int NOT NULL AUTO_INCREMENT,
    doc_id int default NULL,
    doc_version_no int NOT NULL,
    no int NOT NULL,
    text longtext NOT NULL,
    type int default NULL,
    language_id smallint NOT NULL,
    loop_no int DEFAULT NULL,
    loop_content_index int DEFAULT NULL,

    CONSTRAINT pk__imcms_text_doc_texts PRIMARY KEY (id),
    UNIQUE KEY uk__imcms_text_doc_texts__text (doc_id, doc_version_no, no, language_id, loop_no, loop_content_index),
    CONSTRAINT fk__imcms_text_doc_texts__languages FOREIGN KEY (language_id) REFERENCES i18n_languages (language_id),
    CONSTRAINT fk__imcms_text_doc_texts__meta FOREIGN KEY (doc_id) REFERENCES meta (meta_id) ON DELETE CASCADE,
    CONSTRAINT fk__imcms_text_doc_texts__doc_version FOREIGN KEY (doc_id, doc_version_no) REFERENCES imcms_doc_versions (doc_id, no) ON DELETE CASCADE
                
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


--
-- Remove duplicates from texts table before copying
--
DELETE FROM texts
USING texts, texts AS self
WHERE texts.counter < self.counter
AND texts.meta_id = self.meta_id AND texts.name = self.name AND texts.language_id = self.language_id;


INSERT INTO imcms_text_doc_texts (
    doc_id,
    doc_version_no,
    no,
    text,
    type,
    language_id,
    loop_no,
    loop_content_index
) SELECT
    meta_id, 0, name, text, type, language_id, NULL, NULL
FROM texts;


CREATE TABLE imcms_text_doc_texts_history (
    id int NOT NULL AUTO_INCREMENT,
    doc_id int default NULL,
    doc_version_no int DEFAULT NULL,
    no int NOT NULL,
    text longtext NOT NULL,
    type int default NULL,
    language_id smallint NOT NULL,
    loop_no int DEFAULT NULL,
    loop_content_index int DEFAULT NULL,
    modified_datetime datetime NOT NULL,
    user_id int DEFAULT NULL,

    CONSTRAINT pk__imcms_text_doc_texts_history PRIMARY KEY (id),
    CONSTRAINT fk__imcms_text_doc_texts_history__i18n_languages FOREIGN KEY (language_id) REFERENCES i18n_languages (language_id),
    CONSTRAINT fk__imcms_text_doc_texts_history__meta FOREIGN KEY (doc_id) REFERENCES meta (meta_id) ON DELETE CASCADE,
    -- CONSTRAINT fk__imcms_text_doc_texts_history__users FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE SET NULL,
    CONSTRAINT fk__imcms_text_doc_texts_history__doc_versions FOREIGN KEY (doc_id, doc_version_no) REFERENCES imcms_doc_versions (doc_id, no) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


INSERT INTO imcms_text_doc_texts_history (
    doc_id,
    doc_version_no,
    no,
    text,
    type,
    language_id,
    loop_no,
    loop_content_index,
    modified_datetime,
    user_id
) SELECT
    meta_id,
    0,
    name,
    text,
    type,
    language_id,
    null,
    null,
    modified_datetime,
    user_id
FROM texts_history;

DROP TABLE texts_history;


-- Images
CREATE TABLE imcms_text_doc_images (
  id int NOT NULL AUTO_INCREMENT,
  doc_id int DEFAULT NULL,
  doc_version_no int NOT NULL,
  width int NOT NULL,
  height int NOT NULL,
  border int NOT NULL,
  v_space int NOT NULL,
  h_space int NOT NULL,
  no int NOT NULL,
  image_name varchar(40) NOT NULL DEFAULT '',
  target varchar(15) NOT NULL,
  align varchar(15) NOT NULL,
  alt_text varchar(255) NOT NULL,
  low_scr varchar(255) NOT NULL,
  imgurl varchar(255) NOT NULL,
  linkurl varchar(255) NOT NULL,
  type int NOT NULL,
  language_id smallint(6) NOT NULL,
  loop_no int DEFAULT NULL,
  loop_content_index int DEFAULT NULL,
        
  CONSTRAINT pk__imcms_text_doc_images PRIMARY KEY (id),
  UNIQUE KEY uk__imcms_text_doc_images__image (doc_id,doc_version_no,no,language_id,loop_no,loop_content_index),
  CONSTRAINT fk__imcms_text_doc_images__i18n_languages FOREIGN KEY (language_id) REFERENCES i18n_languages (language_id),
  CONSTRAINT fk__imcms_text_doc_images__meta FOREIGN KEY (doc_id) REFERENCES meta (meta_id) ON DELETE CASCADE,
  CONSTRAINT fk__imcms_text_doc_images__doc_version FOREIGN KEY (doc_id, doc_version_no) REFERENCES imcms_doc_versions (doc_id, no) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


INSERT INTO imcms_text_doc_images (
  id,
  doc_id,
  doc_version_no,
  width,
  height,
  border,
  v_space,
  h_space,
  no,
  image_name,
  target,
  align,
  alt_text,
  low_scr,
  imgurl,
  linkurl,
  type,
  language_id,

  loop_no,
  loop_content_index

) SELECT
  image_id,
  meta_id,
  0,
  width,
  height,
  border,
  v_space,
  h_space,
  name,
  image_name,
  target,
  align,
  alt_text,
  low_scr,
  imgurl,
  linkurl,
  type,
  language_id,
  NULL,
  NULL
FROM images;


CREATE TABLE imcms_text_doc_images_history (
  id int NOT NULL AUTO_INCREMENT,
  doc_id int DEFAULT NULL,
  doc_version_no int NOT NULL,
  width int NOT NULL,
  height int NOT NULL,
  border int NOT NULL,
  v_space int NOT NULL,
  h_space int NOT NULL,
  no int NOT NULL,
  image_name varchar(40) NOT NULL DEFAULT '',
  target varchar(15) NOT NULL,
  align varchar(15) NOT NULL,
  alt_text varchar(255) NOT NULL,
  low_scr varchar(255) NOT NULL,
  imgurl varchar(255) NOT NULL,
  linkurl varchar(255) NOT NULL,
  type int NOT NULL,
  language_id smallint(6) NOT NULL,
  loop_no int DEFAULT NULL,
  loop_content_index int DEFAULT NULL,
  modified_datetime datetime NOT NULL,
  user_id int DEFAULT NULL,  

  CONSTRAINT pk__imcms_text_doc_images_history PRIMARY KEY (id),
  CONSTRAINT fk__imcms_text_doc_images_history__i18n_languages FOREIGN KEY (language_id) REFERENCES i18n_languages (language_id),
  CONSTRAINT fk__imcms_text_doc_images_history__meta FOREIGN KEY (doc_id) REFERENCES meta (meta_id) ON DELETE CASCADE,
  CONSTRAINT fk__imcms_text_doc_images_history__doc_version FOREIGN KEY (doc_id, doc_version_no) REFERENCES imcms_doc_versions (doc_id, no) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



-- ********************************************************************
-- FROM 5.3 - 6.0
--

-- Delete unused tables and related data
DROP TABLE browser_docs;
DROP TABLE browsers;

DELETE FROM meta WHERE doc_type = 6;
DELETE FROM doc_types WHERE doc_type = 6;
DELETE FROM doc_permissions WHERE doc_type NOT IN (2,5,7,8);

-- Text docuemnt menu items
--
CREATE TABLE __childs (
  id int auto_increment PRIMARY KEY,
  to_meta_id int(11) NOT NULL,
  manual_sort_order int(11) NOT NULL,
  tree_sort_index varchar(64) NOT NULL,
  menu_id int(11) NOT NULL
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO __childs
  (menu_id, manual_sort_order, tree_sort_index, to_meta_id)
SELECT
  menu_id, manual_sort_order, tree_sort_index, to_meta_id
FROM
  childs;

DROP TABLE childs;
RENAME TABLE __childs TO childs;

ALTER TABLE childs
  ADD CONSTRAINT fk__childs__menus FOREIGN KEY  (menu_id) REFERENCES menus (menu_id),
  ADD CONSTRAINT uk__childs__menu_id__meta_id UNIQUE INDEX  (menu_id, to_meta_id);


--
-- Includes table
--
CREATE TABLE __includes (
  id int auto_increment PRIMARY KEY,
  meta_id int NULL,
  include_id int NOT NULL,
  included_meta_id int NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO __includes (meta_id, include_id, included_meta_id)
SELECT meta_id, include_id, included_meta_id FROM includes;

DROP TABLE includes;
RENAME TABLE __includes TO includes;

ALTER TABLE includes ADD UNIQUE INDEX ux__includes__meta_id__include_id(meta_id, include_id);
ALTER TABLE includes ADD FOREIGN KEY fk__includes__meta (meta_id) REFERENCES meta (meta_id) ON DELETE CASCADE;
ALTER TABLE includes ADD FOREIGN KEY fk__includes__included_document (included_meta_id) REFERENCES meta (meta_id);


--
-- text_docs (template names) table
--

CREATE TABLE __text_docs (
  id int auto_increment PRIMARY KEY,
  meta_id int(11) NULL,
  template_name varchar(255) NOT NULL,
  group_id int(11) NOT NULL default '1',
  default_template_1 varchar(255) default NULL,
  default_template_2 varchar(255) default NULL,
  default_template varchar(255) default NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO __text_docs (meta_id, template_name, group_id, default_template_1, default_template_2, default_template)
SELECT meta_id, template_name, group_id, default_template_1, default_template_2, default_template FROM text_docs;

DROP TABLE text_docs;
RENAME TABLE __text_docs TO text_docs;
ALTER TABLE text_docs ADD FOREIGN KEY fk__text_docs__meta (meta_id) REFERENCES meta (meta_id) ON DELETE CASCADE;


--
-- Table new_doc_permission_sets_ex
--

CREATE TABLE __new_doc_permission_sets_ex (
  id int auto_increment PRIMARY KEY,
  meta_id int(11) NOT NULL,
  set_id int(11) NOT NULL,
  permission_id int(11) NOT NULL,
  permission_data int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO __new_doc_permission_sets_ex (
  meta_id,
  set_id,
  permission_id,
  permission_data
) SELECT
  meta_id, set_id, permission_id, permission_data
FROM new_doc_permission_sets_ex;

DROP TABLE new_doc_permission_sets_ex;
RENAME TABLE __new_doc_permission_sets_ex TO new_doc_permission_sets_ex;

ALTER TABLE new_doc_permission_sets_ex
  ADD UNIQUE INDEX ux__new_doc_permission_sets_ex__1 (meta_id, set_id, permission_id, permission_data),
  ADD FOREIGN KEY  fk__new_doc_permission_sets_ex__meta (meta_id) REFERENCES meta (meta_id) ON DELETE CASCADE,
  ADD FOREIGN KEY  fk__new_doc_permission_sets_ex__permission_sets (set_id) REFERENCES permission_sets (set_id);



CREATE TABLE __doc_permission_sets_ex (
  id int auto_increment PRIMARY KEY,
  meta_id int(11) NOT NULL,
  set_id int(11) NOT NULL,
  permission_id int(11) NOT NULL,
  permission_data int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO __doc_permission_sets_ex (
  meta_id,
  set_id,
  permission_id,
  permission_data
) SELECT
  meta_id, set_id, permission_id, permission_data
FROM doc_permission_sets_ex;

DROP TABLE doc_permission_sets_ex;
RENAME TABLE __doc_permission_sets_ex TO doc_permission_sets_ex;

ALTER TABLE doc_permission_sets_ex
  ADD UNIQUE INDEX ux__doc_permission_sets_ex__1 (meta_id, set_id, permission_id, permission_data),
  ADD FOREIGN KEY  fk__doc_permission_sets_ex__meta (meta_id) REFERENCES meta (meta_id) ON DELETE CASCADE,
  ADD FOREIGN KEY  fk__doc_permission_sets_ex__permission_sets (set_id) REFERENCES permission_sets (set_id);


CREATE TABLE __new_doc_permission_sets (
  id int auto_increment PRIMARY KEY,
  meta_id int(11) NOT NULL,
  set_id int(11) NOT NULL,
  permission_id int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


INSERT INTO __new_doc_permission_sets (
  meta_id,
  set_id,
  permission_id
) SELECT
  meta_id, set_id, permission_id
FROM new_doc_permission_sets;

DROP TABLE new_doc_permission_sets;
RENAME TABLE __new_doc_permission_sets TO new_doc_permission_sets;

ALTER TABLE new_doc_permission_sets
  ADD UNIQUE INDEX ux__new_doc_permission_sets__meta_id__set_id (meta_id, set_id),
  ADD FOREIGN KEY  fk__new_doc_permission_sets__meta (meta_id) REFERENCES meta (meta_id) ON DELETE CASCADE,
  ADD FOREIGN KEY  fk__new_doc_permission_sets__permission_sets (set_id) REFERENCES permission_sets (set_id);

--
-- Update permissions:
--

CREATE TABLE __doc_permission_sets (
  id int auto_increment PRIMARY KEY,
  meta_id int(11) NOT NULL,
  set_id int(11) NOT NULL,
  permission_id int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO __doc_permission_sets (
  meta_id,
  set_id,
  permission_id
) SELECT
  meta_id, set_id, permission_id
FROM doc_permission_sets;

DROP TABLE doc_permission_sets;
RENAME TABLE __doc_permission_sets TO doc_permission_sets;

ALTER TABLE doc_permission_sets
  ADD UNIQUE INDEX ux__doc_permission_sets__meta_id__set_id (meta_id, set_id),
  ADD FOREIGN KEY  fk__doc_permission_sets__meta (meta_id) REFERENCES meta (meta_id) ON DELETE CASCADE,
  ADD FOREIGN KEY  fk__doc_permission_sets__permission_sets (set_id) REFERENCES permission_sets (set_id);


--
-- File upload table:
--
CREATE TABLE __fileupload_docs (
  id int auto_increment PRIMARY KEY,
  meta_id int NOT NULL,
  variant_name varchar(100) NOT NULL,
  filename varchar(255) NOT NULL,
  mime varchar(50) NOT NULL,
  created_as_image int(11) NOT NULL,
  default_variant tinyint(1) NOT NULL default '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO __fileupload_docs (
  meta_id,
  variant_name,
  filename,
  mime,
  created_as_image,
  default_variant
) SELECT
  meta_id,
  variant_name,
  filename,
  mime,
  created_as_image,
  default_variant
FROM fileupload_docs;

DROP TABLE fileupload_docs;
RENAME TABLE __fileupload_docs TO fileupload_docs;

ALTER TABLE fileupload_docs
  ADD UNIQUE INDEX ux__fileupload_docs__meta_id__variant_name (meta_id, variant_name),
  ADD FOREIGN KEY fk__fileupload_docs__meta(meta_id) REFERENCES meta(meta_id) ON DELETE CASCADE;


CREATE TABLE imcms_text_doc_menus (
  id int NOT NULL AUTO_INCREMENT,
  doc_id int NOT NULL,
  doc_version_no int NOT NULL,
  no int NOT NULL,
  sort_order int NOT NULL,

  CONSTRAINT pk__imcms_text_doc_menus PRIMARY KEY (id),
  UNIQUE KEY uk__imcms_text_doc_menus__doc_id__doc_version_no__no (doc_id, doc_version_no, no),
  CONSTRAINT fk__imcms_text_doc_menus__meta FOREIGN KEY (doc_id) REFERENCES meta (meta_id) ON DELETE CASCADE,
  CONSTRAINT fk__imcms_text_doc_menus__doc_version FOREIGN KEY (doc_id, doc_version_no) REFERENCES imcms_doc_versions (doc_id, no) ON DELETE CASCADE

) ENGINE=InnoDB DEFAULT CHARSET=utf8;


INSERT INTO imcms_text_doc_menus (
  id,
  doc_id,
  doc_version_no,
  no,
  sort_order
)
SELECT
  menu_id,
  meta_id,
  0,
  menu_index,
  sort_order
FROM
  menus;


CREATE TABLE imcms_text_doc_menu_items (
  id int NOT NULL AUTO_INCREMENT,
  menu_id int NOT NULL,        
  doc_id int NOT NULL,
  manual_sort_order int NOT NULL,
  tree_sort_index varchar(64) NOT NULL,

  CONSTRAINT pk__imcms_text_doc_menu_items PRIMARY KEY (id),
  UNIQUE KEY uk__imcms_text_doc_menu_items__menu_id__doc_id (menu_id, doc_id),
  CONSTRAINT fk__imcms_text_doc_menu_items__menu FOREIGN KEY (menu_id) REFERENCES imcms_text_doc_menus (id) ON DELETE CASCADE

) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO imcms_text_doc_menu_items (
  menu_id,
  doc_id,
  manual_sort_order,
  tree_sort_index
)
SELECT
  menu_id, to_meta_id, manual_sort_order, tree_sort_index
FROM
  childs;

-- drop childs, drop menus
--
-- Update schema version
--
UPDATE database_version
SET
  major = @schema_version__major_new,
  minor = @schema_version__minor_new;