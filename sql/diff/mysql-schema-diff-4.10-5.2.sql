-- Current schema version
SET @database_version__major__current = 4;
SET @database_version__minor__current = 10;

-- New schema version
SET @database_version__major__new = 5;
SET @database_version__minor__new = 2;

-- Predefined i18n language indentitity
SET @language_id = 1;
SET @language_code = 'sv';
SET @language_name = 'Swedish';
SET @language_native_name = 'Svenska';

-- Create i18n_languages
CREATE TABLE i18n_languages (
  language_id smallint NOT NULL auto_increment,
  code varchar(3) NOT NULL COMMENT 'Language code',
  name varchar(128) NOT NULL,
  native_name varchar(128) default NULL,
  is_default boolean NOT NULL default false COMMENT 'Default language flag for application. Only one language can be set as default.',
  is_enabled boolean NOT NULL default true COMMENT 'Language status for application. Reserved for future use.',
  PRIMARY KEY (language_id)

) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Add default language to i18n_language table 
INSERT INTO i18n_languages
  (language_id, code, name, native_name, is_default, is_enabled)
VALUES
  (@language_id, @language_code, @language_name, @language_native_name, true, true);


-- I18n meta table
CREATE TABLE i18n_meta (
  i18n_meta_id int(11) NOT NULL auto_increment,
  language_id smallint(6) default NULL,
  meta_id int(11) default NULL,
  meta_enabled tinyint(1) NOT NULL default '0',
  meta_headline varchar(255) default NULL,
  meta_text varchar(1000) default NULL,
  meta_image varchar(255) default NULL,
  PRIMARY KEY  (i18n_meta_id),
  UNIQUE KEY ux__i18n_meta_part__language_id__meta_id (language_id,meta_id),
  CONSTRAINT i18n_meta_ibfk_1 FOREIGN KEY (language_id) REFERENCES i18n_languages (language_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- Copy i18n-ed data from meta table
INSERT INTO i18n_meta (language_id, meta_id, meta_enabled, meta_headline, meta_text, meta_image)
SELECT @language_id, meta_id, true, meta_headline, meta_text, meta_image
FROM meta;

﻿-- Drop columns from meta
ALTER TABLE meta
  DROP COLUMN meta_headline,
  DROP COLUMN meta_text,
  DROP COLUMN meta_image;
  
-- Add disabled (at the document access level) language show rule 
ALTER TABLE meta
  ADD COLUMN missing_i18n_show_rule varchar(32) default 'DO_NOT_SHOW';
-- Not in use
-- ADD CONSTRAINT chk__meta__missing_i18n_show_rule check missing_i18n_show_rule in ('SHOW_IN_DEFAULT_LANGUAGE', 'DO_NOT_SHOW');

-- Add surrogate primary key column to images table
ALTER TABLE images 
  ADD COLUMN image_id int auto_increment NOT NULL PRIMARY KEY;

CREATE UNIQUE INDEX ux__images__meta_id__name ON images (meta_id, name);

--
-- Add i18n support to related tables
--
ALTER TABLE texts ADD COLUMN language_id smallint NOT NULL default @language_id;
ALTER TABLE images ADD COLUMN language_id smallint NOT NULL default @language_id;
ALTER TABLE texts_history ADD COLUMN language_id smallint NOT NULL default @language_id__default;

ALTER TABLE texts ALTER COLUMN language_id DROP DEFAULT;
ALTER TABLE texts_history ALTER COLUMN language_id DROP DEFAULT;
ALTER TABLE images ALTER COLUMN language_id DROP DEFAULT;

--
-- Add forign keys to altered tables
--
ALTER TABLE texts ADD FOREIGN KEY fk__texts__i18n_languages(language_id) REFERENCES i18n_languages(language_id);
ALTER TABLE texts_history ADD FOREIGN KEY fk__texts_history__i18n_languages(language_id) references i18n_languages(language_id);
ALTER TABLE images ADD FOREIGN KEY fk__images__i18n_languages(language_id) references i18n_languages(language_id);


--
-- Add i18n support to keywords
--

-- Create new table for keywords
CREATE TABLE keywords (
  keyword_id int NOT NULL auto_increment,
  meta_id int NULL,
  language_id smallint NULL,
  value varchar(128) NOT NULL,
  PRIMARY KEY (keyword_id),
  CONSTRAINT fk__keywords__meta FOREIGN KEY(meta_id) REFERENCES meta (meta_id),
  CONSTRAINT fk__keywords__i18n_languages FOREIGN KEY(language_id) REFERENCES i18n_languages (language_id)
);


-- Move keywords to new keyword table 
INSERT INTO keywords (meta_id, language_id, value)
SELECT @language_id, mc.meta_id, c.code FROM meta_classification mc join classification c on mc.class_id = c.class_id

-- Drop old tables
DROP TABLE meta_classification;
DROP TABLE classification;


-- Update schema version
UPDATE database_version
SET
  major = @database_version__major__new,
  minor = @database_version__minor__new;