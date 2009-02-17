
-- Changes for v 6.0

-- Current schema version
SET @database_version__major__current = 5;
SET @database_version__minor__current = 3;

-- New schema version
SET @database_version__major__new = 6;
SET @database_version__minor__new = 0;

-- Delete unused tables and related data
DROP TABLE images_history;
DROP TABLE childs_history;
DROP TABLE menus_history;

DROP TABLE browser_docs;
DROP TABLE browsers;

DELETE FROM meta WHERE doc_type = 6;
DELETE FROM doc_types WHERE doc_type = 6;
DELETE FROM doc_permissions WHERE doc_type NOT IN (2,5,7,8);

--
-- Meta version table
--
CREATE TABLE meta_version (
  id int NOT NULL auto_increment,
  meta_id int NOT NULL,
  version int NOT NULL,
  version_tag varchar(12) NOT NULL,
  CONSTRAINT pk__meta_version PRIMARY KEY (id), 
  CONSTRAINT fk__meta_version__meta FOREIGN KEY (meta_id) REFERENCES meta (meta_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- for every document create version 1
-- tag all documents as published
-- TODO?: tag all unpublished documents as archived?
INSERT INTO meta_version (
  meta_id, version, version_tag
) SELECT meta_id, 1, 'PUBLISHED' FROM meta;

--
-- Recreate table texts:
--
CREATE TABLE __texts (
  counter int NOT NULL auto_increment PRIMARY KEY,
  meta_id int default NULL,
  meta_version INT NOT NULL,
  name int NOT NULL,
  text longtext NOT NULL,
  type int default NULL,
  language_id smallint(6) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO __texts (
  counter,
  meta_id,
  meta_version,
  name,
  text,
  type,
  language_id
) SELECT
  counter,
  meta_id,
  1,
  name,
  text,
  type,
  language_id
FROM texts;

DROP TABLE texts;
RENAME TABLE __texts to texts;

ALTER TABLE texts 
  ADD CONSTRAINT fk__texts__i18n_languages FOREIGN KEY (language_id) REFERENCES i18n_languages (language_id),
  ADD CONSTRAINT fk__texts__meta FOREIGN KEY (meta_id) REFERENCES meta (meta_id) ON DELETE CASCADE,
  ADD CONSTRAINT uk__texts__meta_id__meta_version__name__language_id UNIQUE KEY (meta_id, meta_version, name, language_id);

--
-- Recreate table texts_history:
--
CREATE TABLE __texts_history (
  counter int NOT NULL auto_increment PRIMARY KEY,
  meta_id int default NULL,
  meta_version INT NOT NULL,
  name int NOT NULL,
  text longtext NOT NULL,
  type int default NULL,
  modified_datetime datetime NOT NULL,
  user_id int default NULL,
  language_id smallint(6) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO __texts_history (
  counter,
  meta_id,
  meta_version,
  name,
  text,
  type,
  modified_datetime,
  user_id,
  language_id
) SELECT
  counter,
  meta_id,
  1,
  name,
  text,
  type,
  modified_datetime,
  user_id,
  language_id
FROM texts_history;

DROP TABLE texts_history;
RENAME TABLE __texts_history TO texts_history;

ALTER TABLE texts_history
  ADD CONSTRAINT fk__texts_history__i18n_languages FOREIGN KEY (language_id) REFERENCES i18n_languages (language_id),
  ADD CONSTRAINT fk__texts_history__meta FOREIGN KEY (meta_id) REFERENCES meta (meta_id) ON DELETE CASCADE,
  ADD CONSTRAINT fk__texts_history__users FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE SET NULL;
    
--
-- Recreate table images:
--
CREATE TABLE __images (
  image_id int NOT NULL auto_increment PRIMARY KEY,
  meta_id int NULL,
  meta_version INT NOT NULL,
  width int NOT NULL,
  height int NOT NULL,
  border int NOT NULL,
  v_space int NOT NULL,
  h_space int NOT NULL,
  name int NOT NULL,
  image_name varchar(40) NOT NULL default '',
  target varchar(15) NOT NULL,
  align varchar(15) NOT NULL,
  alt_text varchar(255) NOT NULL,
  low_scr varchar(255) NOT NULL,
  imgurl varchar(255) NOT NULL,
  linkurl varchar(255) NOT NULL,
  type int NOT NULL,
  language_id smallint(6) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO __images (
  image_id,
  meta_id,
  meta_version,
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
  language_id
) SELECT 
  image_id,
  meta_id,
  1,
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
  language_id
FROM images;

DROP TABLE images;
RENAME TABLE __images TO images;

ALTER TABLE images
  ADD CONSTRAINT fk__images__meta FOREIGN KEY  (meta_id) REFERENCES meta (meta_id) ON DELETE CASCADE,
  ADD CONSTRAINT fk__images__i18n_languages FOREIGN KEY (language_id) REFERENCES i18n_languages (language_id),
  ADD CONSTRAINT uk__images__meta_id__meta_version__name__language_id UNIQUE KEY (meta_id, meta_version, name, language_id);


-- contents becoms text_doc_content_loops
CREATE TABLE text_doc_content_loops (
  id int auto_increment PRIMARY KEY,
  meta_id int NOT NULL,
  loop_index int NOT NULL,
  base_index int NOT NULL,
  FOREIGN KEY fk__text_doc_content_loops__meta (meta_id) REFERENCES meta (meta_id) ON DELETE CASCADE,
  UNIQUE INDEX ux__text_doc_content_loops__meta_id__loop_index (meta_id, loop_index)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO text_doc_content_loops (meta_id, loop_index, base_index)
SELECT meta_id, content_no, base_index
FROM contents;

-- content_loops becoms text_doc_contents
CREATE TABLE text_doc_contents (
  id int auto_increment PRIMARY KEY,
  loop_id int NOT NULL,
  sequence_index int NOT NULL,
  order_index int NOT NULL,

  UNIQUE INDEX ux__loop_id__sequence_index (loop_id, sequence_index),
  UNIQUE INDEX ux__loop_id__order_index (loop_id, order_index),
  FOREIGN KEY fk__text_doc_contents__text_doc_content_loops (loop_id) REFERENCES text_doc_content_loops (id) ON DELETE CASCADE

) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO text_doc_contents (loop_id, sequence_index, order_index)
SELECT content_id, loop_index, order_index
FROM content_loops;

DROP TABLE content_loops;
DROP TABLE contents;

--
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
  ADD FOREIGN KEY fk__childs__menus (menu_id) REFERENCES menus (menu_id),
  ADD UNIQUE INDEX ux__childs__menu_id__meta_id (menu_id, to_meta_id);


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


--
-- Update schema version
--
UPDATE database_version
SET
  major = @database_version__major__new,
  minor = @database_version__minor__new;