SET @schema_version__major_new = 7;
SET @schema_version__minor_new = 9;

CREATE TABLE imcms_html_meta_tags
(
	id   INT AUTO_INCREMENT PRIMARY KEY,
	name VARCHAR(255) NOT NULL UNIQUE
);

INSERT INTO imcms_html_meta_tags
VALUES (null, 'author'),
       (null, 'description'),
       (null, 'keywords'),
       (null, 'robots');

CREATE TABLE imcms_doc_metadata
(
	imcms_doc_i18n_meta_id INT           NOT NULL,
	meta_tag_id            INT           NOT NULL,
	content                VARCHAR(2048) NULL,

	CONSTRAINT imcms_doc_metadata_FK_meta_tags
		FOREIGN KEY (meta_tag_id) REFERENCES imcms_html_meta_tags (id),

	CONSTRAINT imcms_doc_metadata_FK_imcms_doc_i18n_meta
		FOREIGN KEY (imcms_doc_i18n_meta_id) REFERENCES imcms_doc_i18n_meta (id)
);

UPDATE database_version
SET major = @schema_version__major_new,
    minor = @schema_version__minor_new;