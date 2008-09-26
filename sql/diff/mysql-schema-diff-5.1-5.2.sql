-- Changes for v 5.2
--
-- Replace i18n_keywords with new table
--

-- Current schema version
SET @database_version__major__current = 5;
SET @database_version__minor__current = 1;

-- New schema version
SET @database_version__major__new = 5;
SET @database_version__minor__new = 2;


CREATE TABLE keywords (
  keyword_id int NOT NULL auto_increment,
  meta_id int NULL,
  language_id smallint NULL,
  value varchar(128) NOT NULL,
  PRIMARY KEY (keyword_id),
  CONSTRAINT fk__keywords__meta FOREIGN KEY(meta_id) REFERENCES meta (meta_id),
  CONSTRAINT fk__keywords__i18n_languages FOREIGN KEY(language_id) REFERENCES i18n_languages (language_id)
);


INSERT INTO keywords (
  meta_id, language_id, value
)
SELECT
  m.meta_id, m.language_id, k.keyword_value
FROM
  i18n_meta m
INNER JOIN
  i18n_keywords k
ON
  m.i18n_meta_id = k.i18n_meta_id;

DROP TABLE i18n_keywords;
DROP TABLE meta_classification;
DROP TABLE classification;

--
-- Update schema version
--
UPDATE database_version
SET
  major = @database_version__major__new,
  minor = @database_version__minor__new;