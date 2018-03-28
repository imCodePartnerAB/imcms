-- Remove version support from labels

SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 4;

--
-- Document i18n meta
--
CREATE TABLE `imcms_doc_labels__new` (
  `id` int AUTO_INCREMENT PRIMARY KEY,
  `doc_id` int NOT NULL,
  `language_id` int NOT NULL,
  `headline` varchar(256) DEFAULT NULL,
  `menu_image_url` varchar(256) DEFAULT NULL,
  `menu_text` varchar(1024) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


INSERT INTO imcms_doc_labels__new (doc_id, language_id, headline, menu_image_url, menu_text)
SELECT doc_id, language_id, headline, menu_image_url, menu_text FROM imcms_doc_labels
WHERE doc_version_no = 0;

DROP TABLE imcms_doc_labels;
RENAME TABLE imcms_doc_labels__new TO imcms_doc_labels;

ALTER TABLE imcms_doc_labels
  ADD CONSTRAINT `uk__imcms_doc_labels__doc_id__language_id` UNIQUE KEY (doc_id, language_id),
  ADD CONSTRAINT `fk__imcms_doc_labels__languages` FOREIGN KEY (`language_id`) REFERENCES `imcms_languages` (`id`);

--
-- Update schema version
--
UPDATE database_version
SET
  major = @schema_version__major_new,
  minor = @schema_version__minor_new;



