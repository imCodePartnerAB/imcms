--
-- Removes ambigous tables and view.
--

-- Current schema version
SET @database_version__major__current = 5;
SET @database_version__minor__current = 0;

-- New schema version
SET @database_version__major__new = 5;
SET @database_version__minor__new = 1;

--
-- The only i18n_%language% table which will stay.
--
CREATE TABLE i18n_languages_new (
  language_id smallint NOT NULL auto_increment,
  code varchar(3) NOT NULL COMMENT 'Language code.',
  name varchar(128) NOT NULL,
  native_name varchar(128) default NULL,
  is_default boolean NOT NULL default false COMMENT 'Default language flag for application. Only one language can be set as default.',
  is_enabled boolean NOT NULL default true COMMENT 'Language status for application. Reserved for future use.',
  PRIMARY KEY (language_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


INSERT INTO i18n_languages_new (
  language_id,
  code,
  name,
  native_name,
  is_default,
  is_enabled
)
SELECT
  language_id,
  language_code_iso_639_1,
  language_name,
  language_native_name,
  system_default,
  true
FROM
  i18n_languages_v;

--
-- Drop all old i18n_%language% tables
-- For some reasons table renaming operation fails when execut the following:
-- DROP TABLE i18n_languages;
-- RENAME TABLE i18n_languages_work TO i18n_languages;
--

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE i18n_available_languages;
DROP VIEW i18n_languages_v;

-- workaroud
-- RENAME TABLE i18n_languages TO i18n_languages_old;
DROP TABLE i18n_languages;
RENAME TABLE i18n_languages_new TO i18n_languages;

-- DROP TABLE i18n_languages_old;

SET FOREIGN_KEY_CHECKS = 1;

--
-- Adds foreign keys
--
ALTER TABLE i18n_meta
ADD CONSTRAINT fk__i18n_meta__i18n_languages FOREIGN KEY (language_id) REFERENCES i18n_languages (language_id);

ALTER TABLE texts
ADD CONSTRAINT fk__texts__i18n_languages FOREIGN KEY (language_id) REFERENCES i18n_languages (language_id);

ALTER TABLE texts_history
ADD CONSTRAINT fk__texts_history__i18n_languages FOREIGN KEY (language_id) REFERENCES i18n_languages (language_id);

ALTER TABLE images
ADD CONSTRAINT fk__images__i18n_languages FOREIGN KEY  (language_id) REFERENCES i18n_languages (language_id);



--
-- Update schema version
--
UPDATE database_version
SET
  major = @database_version__major__new,
  minor = @database_version__minor__new;
  