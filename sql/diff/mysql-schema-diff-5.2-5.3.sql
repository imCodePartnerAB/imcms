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
create table IF NOT EXISTS contents (
  content_id int auto_increment primary key,
  content_no int not null,
  meta_id int not null,
  base_index int not null,

  unique index ux__meta_id__content_no (meta_id, content_no)
);

create table IF NOT EXISTS content_loops (
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

-- cleanup
delete from i18n_meta where not exists (select * from meta where meta_id = i18n_meta.meta_id);

ALTER TABLE i18n_meta
  ADD CONSTRAINT uk__i18n_meta__meta_id__language_id UNIQUE INDEX (meta_id, language_id),
  ADD CONSTRAINT fk__i18n_meta__meta FOREIGN KEY (meta_id) REFERENCES meta (meta_id),
  ADD CONSTRAINT fk__i18n_meta__language FOREIGN KEY (language_id) REFERENCES i18n_languages (language_id);

--
-- Update schema version
--
UPDATE database_version
SET
  major = @database_version__major__new,
  minor = @database_version__minor__new;