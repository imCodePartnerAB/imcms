-- Changes for v 5.3

-- Current schema version
SET @database_version__major__current = 5;
SET @database_version__minor__current = 2;

-- New schema version
SET @database_version__major__new = 5;
SET @database_version__minor__new = 3;

--
-- Tables for content loop data
--
﻿create table contents (
  content_id int auto_increment primary key,
  content_no int not null,
  meta_id int not null,
  base_index int not null,

  unique index ux__meta_id__content_no (meta_id, content_no)
);

create table content_loops (
  loop_id int auto_increment primary key,
  content_id int not null,
  loop_index int not null,
  order_index int not null,

  unique index ux__content_id__loop_index (content_id, loop_index),
  unique index ux__content_id__order_index (content_id, order_index),
  unique index ux__content_id__loop_index__order_index (content_id, loop_index, order_index),
  foreign key fk__content_loops__contents (content_id) references contents (content_id)
);

--
-- Fixes table keys
--

--
-- Recreate table i18n_meta
--
CREATE TABLE i18n_meta_new (
  i18n_meta_id int(11) NOT NULL auto_increment PRIMARY KEY,
  language_id smallint(6) default NULL,
  meta_id int(11) default NULL,
  meta_enabled tinyint(1) NOT NULL default '0',
  meta_headline varchar(255) default NULL,
  meta_text varchar(1000) default NULL,
  meta_image varchar(255) default NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO i18n_meta_new SELECT * FROM i18n_meta;
DROP TABLE i18n_meta;
RENAME TABLE i18n_meta_new TO i18n_meta;

ALTER TABLE i18n_meta ADD UNIQUE INDEX ux__i18n_meta__meta_id__language_id(meta_id, language_id);
ALTER TABLE i18n_meta ADD FOREIGN KEY fk__i18n_meta__meta (meta_id) REFERENCES meta (meta_id) ON DELETE CASCADE;
ALTER TABLE i18n_meta ADD FOREIGN KEY fk__i18n_meta__language (language_id) REFERENCES i18n_languages (language_id);


-- 
-- Recreate table keywords
--

CREATE TABLE keywords_new (
  keyword_id int NOT NULL auto_increment PRIMARY KEY,
  meta_id int default NULL,
  language_id smallint NULL,
  value varchar(128) NOT NULL
);

INSERT INTO keywords_new SELECT * FROM keywords;
DROP TABLE keywords;
RENAME TABLE keywords_new TO keywords;

ALTER TABLE keywords ADD FOREIGN KEY fk__keywords__meta (meta_id) REFERENCES meta (meta_id) ON DELETE CASCADE;
ALTER TABLE keywords ADD FOREIGN KEY fk__keywords__i18n_languages (language_id) REFERENCES i18n_languages (language_id);


-- 
-- Recreate table texts
--
CREATE TABLE texts_new (
  meta_id int(11) default NULL,
  name int(11) NOT NULL,
  text longtext NOT NULL,
  type int(11) default NULL,
  counter int(11) NOT NULL auto_increment PRIMARY KEY,
  language_id smallint(6) NOT NULL default '1'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


INSERT INTO texts_new SELECT * FROM texts;
DROP TABLE texts;
RENAME TABLE texts_new TO texts;

ALTER TABLE texts ADD FOREIGN KEY fk__texts__i18n_languages (language_id) REFERENCES i18n_languages (language_id);
ALTER TABLE texts ADD FOREIGN KEY fk__texts__meta (meta_id) REFERENCES meta (meta_id) ON DELETE CASCADE;
ALTER TABLE texts ADD UNIQUE INDEX ux__texts__meta_id__name__language_id (meta_id, name, language_id);

--
-- Recreate table texts_history
--

CREATE TABLE texts_history_new (
  meta_id int(11) default NULL,
  name int(11) NOT NULL,
  text longtext NOT NULL,
  type int(11) default NULL,
  modified_datetime datetime NOT NULL,
  user_id int(11) default NULL,
  counter int(11) NOT NULL auto_increment PRIMARY KEY,
  language_id smallint(6) NOT NULL

) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO texts_history_new SELECT * FROM texts_history;
DROP TABLE texts_history;
RENAME TABLE texts_history_new TO texts_history;

ALTER TABLE texts_history ADD FOREIGN KEY fk__texts_history__i18n_languages (language_id) REFERENCES i18n_languages (language_id);
ALTER TABLE texts_history ADD FOREIGN KEY fk__texts_history__meta (meta_id) REFERENCES meta (meta_id) ON DELETE CASCADE;
ALTER TABLE texts_history ADD FOREIGN KEY fk__texts_history__users (user_id) REFERENCES users (user_id) ON DELETE SET NULL;


--
-- Recreate table images
--

/* does not work
CREATE TABLE images_new (
  meta_id int(11) NULL,
  width int(11) NOT NULL,
  height int(11) NOT NULL,
  border int(11) NOT NULL,
  v_space int(11) NOT NULL,
  h_space int(11) NOT NULL,
  name int(11) NOT NULL,
  image_name varchar(40) NOT NULL default '',
  target varchar(15) NOT NULL,
  align varchar(15) NOT NULL,
  alt_text varchar(255) NOT NULL,
  low_scr varchar(255) NOT NULL,
  imgurl varchar(255) NOT NULL,
  linkurl varchar(255) NOT NULL,
  type int(11) NOT NULL,
  language_id smallint(6) NOT NULL,
  image_id int(11) NOT NULL auto_increment PRIMARY KEY
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO images_new SELECT * FROM images;
DROP TABLE images;
RENAME TABLE images_new TO images;

ALTER TABLE images ADD FOREIGN KEY nnn (language_id) REFERENCES i18n_languages (language_id);
ALTER TABLE images ADD FOREIGN KEY fk__images__meta (meta_id) REFERENCES meta (meta_id) ON DELETE CASCADE;
ALTER TABLE images ADD UNIQUE INDEX ux__images__meta_id__name__language_id (meta_id, name, language_id);
*/

--
-- Update schema version
--
UPDATE database_version
SET
  major = @database_version__major__new,
  minor = @database_version__minor__new;