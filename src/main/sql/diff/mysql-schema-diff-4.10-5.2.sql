-- Current schema version
SET @database_version__major__current = 4;
SET @database_version__minor__current = 10;

-- New schema version
SET @database_version__major__new = 5;
SET @database_version__minor__new = 2;

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
  (1, 'sv', 'Swedish', 'Svenska', true, true);


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
  CONSTRAINT ux__i18n_meta__meta_id__language_id UNIQUE KEY  (meta_id, language_id),
  CONSTRAINT fk__i18n_meta__meta FOREIGN KEY (meta_id) REFERENCES meta (meta_id),
  CONSTRAINT fk__i18n_meta__i18n_languages FOREIGN KEY   (language_id) REFERENCES i18n_languages (language_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- Copy i18n-ed data from meta table
INSERT INTO i18n_meta (language_id, meta_id, meta_enabled, meta_headline, meta_text, meta_image)
SELECT 1, meta_id, true, meta_headline, meta_text, meta_image
FROM meta;

-- Drop columns from meta
ALTER TABLE meta
  DROP COLUMN meta_headline,
  DROP COLUMN meta_text,
  DROP COLUMN meta_image;


  
-- Add disabled (at the document access level) language show rule 
ALTER TABLE meta
  ADD COLUMN missing_i18n_show_rule varchar(32) default 'DO_NOT_SHOW';

--
-- Receate images table
--
CREATE TABLE images_new AS SELECT * FROM images;
DROP TABLE images;
RENAME TABLE images_new TO images;

ALTER TABLE images
  ADD COLUMN image_id int auto_increment NOT NULL PRIMARY KEY,
  ADD COLUMN language_id smallint NOT NULL default 1,

  ADD CONSTRAINT fk__images__meta FOREIGN KEY  (meta_id) REFERENCES meta (meta_id),
  ADD CONSTRAINT fk__images__i18n_languages FOREIGN KEY  (language_id) REFERENCES i18n_languages (language_id),
  ADD CONSTRAINT ux__images__meta_id__name__language_id UNIQUE INDEX  (meta_id, name, language_id);

ALTER TABLE images ALTER COLUMN language_id DROP DEFAULT;

--
-- Receate texts table
--
-- Delete duplicates if any
DELETE FROM texts l USING texts l, texts r WHERE l.meta_id = r.meta_id and l.name = r.name and l.counter < r.counter;
--
-- Workaround for when you get error on the above on MySQL, will probably work all around but is untested by Anton.
-- DELETE l FROM texts l INNER JOIN texts r WHERE l.meta_id = r.meta_id and l.name = r.name and l.counter < r.counter;

CREATE TABLE texts_new AS SELECT * FROM texts;
DROP TABLE texts;
RENAME TABLE texts_new TO texts;

ALTER TABLE texts
  ADD COLUMN language_id smallint NOT NULL default 1,
  MODIFY COLUMN counter int auto_increment PRIMARY KEY;

ALTER TABLE texts
  ADD CONSTRAINT fk__texts__meta FOREIGN KEY (meta_id) REFERENCES meta (meta_id),
  ADD CONSTRAINT fk__texts__i18n_languages FOREIGN KEY (language_id) REFERENCES i18n_languages(language_id),
  ADD CONSTRAINT ux__texts__meta_id__name__language_id UNIQUE INDEX  (meta_id, name, language_id);

ALTER TABLE texts ALTER COLUMN language_id DROP DEFAULT;

-- TODO: Recreate instead of alter 
ALTER TABLE texts_history
  ADD COLUMN language_id smallint NOT NULL default 1,
  ADD CONSTRAINT fk__texts_history__i18n_languages FOREIGN KEY (language_id) references i18n_languages(language_id);

ALTER TABLE texts_history ALTER COLUMN language_id DROP DEFAULT;


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
  CONSTRAINT fk__keywords__meta FOREIGN KEY  (meta_id) REFERENCES meta (meta_id),
  CONSTRAINT fk__keywords__i18n_languages FOREIGN KEY  (language_id) REFERENCES i18n_languages (language_id)
);


-- Move keywords to new keyword table 
INSERT INTO keywords (meta_id, language_id, value)
SELECT mc.meta_id, 1, c.code FROM meta_classification mc join classification c on mc.class_id = c.class_id;

-- Drop old tables
DELETE FROM meta_classification;
DELETE FROM classification;

DROP TABLE meta_classification;
DROP TABLE classification;


-- Update schema version
UPDATE database_version
SET
  major = @database_version__major__new,
  minor = @database_version__minor__new;
