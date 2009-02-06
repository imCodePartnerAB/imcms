
-- Changes for v 6.0

-- Current schema version
SET @database_version__major__current = 5;
SET @database_version__minor__current = 3;

-- New schema version
SET @database_version__major__new = 6;
SET @database_version__minor__new = 0;

-- Delete browsers related data from database
/*
DROP TABLE browser_docs;
DROP TABLE browsers;
DELETE FROM meta WHERE doc_type = 6;
DELETE FROM doc_types WHERE doc_type = 6;
DELETE FROM doc_permissions WHERE doc_type NOT IN (2,5,7,8);

DROP menus_history;
DROP childs_history;
*/

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
);

-- for every document create version 1
-- tag all documents as published
-- TODO: tag all unpublished documents as archived?

INSERT INTO meta_version (
  meta_id, version, version_tag
) SELECT meta_id, 1, 'PUBLISHED' FROM meta;

ALTER TABLE texts 
    ADD COLUMN meta_version INT NOT NULL DEFAULT 1;

    -- DROP INDEX ux__texts__meta_id__name__language_id; 
    
-- drop default 1

-- alter history

ALTER TABLE images 
    ADD COLUMN meta_version INT NOT NULL DEFAULT 1;
    -- DROP INDEX ux__images__meta_id__name__language_id;
    
-- drop default 1

-- alter history

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
CREATE TABLE new__childs (
  id int auto_increment PRIMARY KEY,
  to_meta_id int(11) NOT NULL,
  manual_sort_order int(11) NOT NULL,
  tree_sort_index varchar(64) NOT NULL,
  menu_id int(11) NOT NULL
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- DROP childs_history

INSERT INTO new__childs 
  (menu_id, manual_sort_order, tree_sort_index, to_meta_id)
SELECT
  menu_id, manual_sort_order, tree_sort_index, to_meta_id
FROM 
  childs;

DROP TABLE childs;
RENAME TABLE new__childs TO childs;

ALTER TABLE childs
  ADD FOREIGN KEY fk__childs__menus (menu_id) REFERENCES menus (menu_id),
  ADD UNIQUE INDEX ux__childs__menu_id__meta_id (menu_id, to_meta_id);


--
-- Includes table
--
CREATE TABLE includes_new (
  id int auto_increment PRIMARY KEY,
  meta_id int NULL,
  include_id int NOT NULL,
  included_meta_id int NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO includes_new (meta_id, include_id, included_meta_id)
SELECT meta_id, include_id, included_meta_id FROM includes;

DROP TABLE includes;
RENAME TABLE includes_new TO includes;

ALTER TABLE includes ADD UNIQUE INDEX ux__includes__meta_id__include_id(meta_id, include_id);
ALTER TABLE includes ADD FOREIGN KEY fk__includes__meta (meta_id) REFERENCES meta (meta_id) ON DELETE CASCADE;
ALTER TABLE includes ADD FOREIGN KEY fk__includes__included_document (included_meta_id) REFERENCES meta (meta_id);


--
-- text_docs (template names) table
--

CREATE TABLE text_docs_tmp (
  id int auto_increment PRIMARY KEY,
  meta_id int(11) NULL,
  template_name varchar(255) NOT NULL,
  group_id int(11) NOT NULL default '1',
  default_template_1 varchar(255) default NULL,
  default_template_2 varchar(255) default NULL,
  default_template varchar(255) default NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO text_docs_tmp (meta_id, template_name, group_id, default_template_1, default_template_2, default_template)
SELECT meta_id, template_name, group_id, default_template_1, default_template_2, default_template FROM text_docs;

DROP TABLE text_docs;
RENAME TABLE text_docs_tmp TO text_docs;
ALTER TABLE text_docs ADD FOREIGN KEY fk__text_docs__meta (meta_id) REFERENCES meta (meta_id) ON DELETE CASCADE;


--
-- Table new_doc_permission_sets_ex
--

CREATE TABLE new_doc_permission_sets_ex_temp (
  id int auto_increment PRIMARY KEY,
  meta_id int(11) NOT NULL,
  set_id int(11) NOT NULL,
  permission_id int(11) NOT NULL,
  permission_data int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO new_doc_permission_sets_ex_temp (
  meta_id,
  set_id,
  permission_id,
  permission_data
) SELECT 
  meta_id, set_id, permission_id, permission_data
FROM new_doc_permission_sets_ex;

DROP TABLE new_doc_permission_sets_ex;
RENAME TABLE new_doc_permission_sets_ex_temp TO new_doc_permission_sets_ex;

ALTER TABLE new_doc_permission_sets_ex
  ADD UNIQUE INDEX ux__new_doc_permission_sets_ex__1 (meta_id, set_id, permission_id, permission_data),
  ADD FOREIGN KEY  fk__new_doc_permission_sets_ex__meta (meta_id) REFERENCES meta (meta_id) ON DELETE CASCADE,
  ADD FOREIGN KEY  fk__new_doc_permission_sets_ex__permission_sets (set_id) REFERENCES permission_sets (set_id);



CREATE TABLE doc_permission_sets_ex_temp (
  id int auto_increment PRIMARY KEY,
  meta_id int(11) NOT NULL,
  set_id int(11) NOT NULL,
  permission_id int(11) NOT NULL,
  permission_data int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO doc_permission_sets_ex_temp (
  meta_id,
  set_id,
  permission_id,
  permission_data
) SELECT
  meta_id, set_id, permission_id, permission_data
FROM doc_permission_sets_ex;

DROP TABLE doc_permission_sets_ex;
RENAME TABLE doc_permission_sets_ex_temp TO doc_permission_sets_ex;

ALTER TABLE doc_permission_sets_ex
  ADD UNIQUE INDEX ux__doc_permission_sets_ex__1 (meta_id, set_id, permission_id, permission_data),
  ADD FOREIGN KEY  fk__doc_permission_sets_ex__meta (meta_id) REFERENCES meta (meta_id) ON DELETE CASCADE,
  ADD FOREIGN KEY  fk__doc_permission_sets_ex__permission_sets (set_id) REFERENCES permission_sets (set_id);


CREATE TABLE new_doc_permission_sets_temp (
  id int auto_increment PRIMARY KEY,
  meta_id int(11) NOT NULL,
  set_id int(11) NOT NULL,
  permission_id int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


INSERT INTO new_doc_permission_sets_temp (
  meta_id,
  set_id,
  permission_id
) SELECT
  meta_id, set_id, permission_id
FROM new_doc_permission_sets;

DROP TABLE new_doc_permission_sets;
RENAME TABLE new_doc_permission_sets_temp TO new_doc_permission_sets;

ALTER TABLE new_doc_permission_sets
  ADD UNIQUE INDEX ux__new_doc_permission_sets__meta_id__set_id (meta_id, set_id),
  ADD FOREIGN KEY  fk__new_doc_permission_sets__meta (meta_id) REFERENCES meta (meta_id) ON DELETE CASCADE,
  ADD FOREIGN KEY  fk__new_doc_permission_sets__permission_sets (set_id) REFERENCES permission_sets (set_id);

--
-- Update permissions:
--

CREATE TABLE doc_permission_sets_temp (
  id int auto_increment PRIMARY KEY,
  meta_id int(11) NOT NULL,
  set_id int(11) NOT NULL,
  permission_id int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO doc_permission_sets_temp (
  meta_id,
  set_id,
  permission_id
) SELECT 
  meta_id, set_id, permission_id
FROM doc_permission_sets;

DROP TABLE doc_permission_sets;
RENAME TABLE doc_permission_sets_temp TO doc_permission_sets;

ALTER TABLE doc_permission_sets
  ADD UNIQUE INDEX ux__doc_permission_sets__meta_id__set_id (meta_id, set_id),
  ADD FOREIGN KEY  fk__doc_permission_sets__meta (meta_id) REFERENCES meta (meta_id) ON DELETE CASCADE,
  ADD FOREIGN KEY  fk__doc_permission_sets__permission_sets (set_id) REFERENCES permission_sets (set_id);


--
-- File upload table:
--
CREATE TABLE fileupload_docs_temp (
  id int auto_increment PRIMARY KEY,
  meta_id int NOT NULL,
  variant_name varchar(100) NOT NULL,
  filename varchar(255) NOT NULL,
  mime varchar(50) NOT NULL,
  created_as_image int(11) NOT NULL,
  default_variant tinyint(1) NOT NULL default '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO fileupload_docs_temp (
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
RENAME TABLE fileupload_docs_temp TO fileupload_docs;

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