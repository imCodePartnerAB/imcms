-- Renames imcms_doc_labels to imcms_doc_i18n_meta

SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 5;

CREATE TABLE `imcms_doc_i18n_meta` (
  `id` int AUTO_INCREMENT PRIMARY KEY,
  `doc_id` int NOT NULL,
  `language_id` int NOT NULL,
  `headline` varchar(256) DEFAULT NULL,
  `menu_image_url` varchar(256) DEFAULT NULL,
  `menu_text` varchar(1024) DEFAULT NULL,

  CONSTRAINT `uk__imcms_doc_i18n_meta__doc_id__language_id` UNIQUE KEY (doc_id, language_id),
  CONSTRAINT `fk__imcms_doc_i18n_meta__languages` FOREIGN KEY (`language_id`) REFERENCES `imcms_languages` (`id`)  
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO imcms_doc_i18n_meta  (id, doc_id, language_id, headline, menu_image_url, menu_text)
SELECT id, doc_id, language_id, headline, menu_image_url, menu_text FROM imcms_doc_labels;

DROP TABLE imcms_doc_labels;
--
-- Update schema version
--
UPDATE database_version
SET
  major = @schema_version__major_new,
  minor = @schema_version__minor_new;



