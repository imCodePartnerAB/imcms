-- Changes for v 5.2
--
-- Replace i18n_keywords with new table
--

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

-- DELETE FROM classification;
-- DELETE FROM meta_classification;

DROP TABLE meta_classification;
DROP TABLE classification;
