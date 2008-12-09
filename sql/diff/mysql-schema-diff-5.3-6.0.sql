﻿-- Changes for v 6.0

-- Current schema version
SET @database_version__major__current = 5;
SET @database_version__minor__current = 3;

-- New schema version
SET @database_version__major__new = 6;
SET @database_version__minor__new = 0;

-- Delete browsers related data from database
DROP TABLE browser_docs;
DROP TABLE browsers;
DELETE FROM meta WHERE doc_type = 6;
DELETE FROM doc_types WHERE doc_type = 6;
DELETE FROM doc_permissions WHERE doc_type NOT IN (2,5,7,8);

/*
CREATE TABLE new__meta (
  id int auto_increment PRIMARY KEY,
  meta_id int(11) NOT NULL,
  meta_version int(11) NOT NULL DEFAULT 0,
  doc_type int(11) NOT NULL,
  owner_id int(11) NOT NULL,
  permissions int(11) NOT NULL,
  shared int(11) NOT NULL,
  show_meta int(11) NOT NULL,
  lang_prefix varchar(3) NOT NULL,
  date_created datetime NOT NULL,
  date_modified datetime NOT NULL,
  disable_search int(11) NOT NULL,
  target varchar(10) NOT NULL,
  activate int(11) NOT NULL,
  archived_datetime datetime default NULL,
  publisher_id int(11) default NULL,
  status int(11) NOT NULL,
  publication_start_datetime datetime default NULL,
  publication_end_datetime datetime default NULL,
  missing_i18n_show_rule varchar(32) default 'DO_NOT_SHOW'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO new__meta (
  meta_id,
  meta_version,
  doc_type,
  owner_id,
  permissions,
  shared,
  show_meta,
  lang_prefix,
  date_created,
  date_modified,
  disable_search,
  target,
  activate,
  archived_datetime,
  publisher_id,
  status,
  publication_start_datetime,
  publication_end_datetime,
  missing_i18n_show_rule
) SELECT
  meta_id,
  meta_version,
  doc_type,
  owner_id,
  permissions,
  shared,
  show_meta,
  lang_prefix,
  date_created,
  date_modified,
  disable_search,
  target,
  activate,
  archived_datetime,
  publisher_id,
  status,
  publication_start_datetime,
  publication_end_datetime,
  missing_i18n_show_rule
FROM meta;

*/

ALTER TABLE meta
  ADD COLUMN doc_id int NULL AFTER meta_id,
  ADD COLUMN doc_version int NOT NULL DEFAULT 1 AFTER doc_id,
  ADD UNIQUE INDEX ux__meta__doc_id__doc_version (doc_id, doc_version);

UPDATE meta SET doc_id = meta_id;

ALTER TABLE meta MODIFY COLUMN doc_id int NOT NULL;

/*
alter table meta add column id int not null default 1;
alter table meta add column version int not null default 1;

create table id_generators (
  id int auto_increment PRIMARY KEY,
  generator_name varchar(128) NOT NULL,
  generator_value int NOT NULL,
  UNIQUE INDEX ux__generators__generator_name (generator_name)
);

INSERT INTO id_generators (generator_name, generator_value) VALUES ('meta', 1);
*/

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
ALTER TABLE includes ADD FOREIGN KEY fk__includes__included_meta (included_meta_id) REFERENCES meta (meta_id);


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