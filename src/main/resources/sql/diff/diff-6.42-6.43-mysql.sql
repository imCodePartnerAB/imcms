# creating common content data in working version from minimum version if not exist

SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 43;

INSERT INTO imcms_doc_i18n_meta (doc_id, language_id, headline, menu_image_url, menu_text)
  SELECT
    doc_id,
    language_id,
    headline,
    menu_image_url,
    menu_text
  FROM imcms_doc_i18n_meta outer_common_content
  WHERE outer_common_content.version_no != 0
        AND outer_common_content.version_no = (
    SELECT MIN(inner_common_content.version_no)
    FROM imcms_doc_i18n_meta inner_common_content
    WHERE outer_common_content.doc_id = inner_common_content.doc_id
    GROUP BY inner_common_content.doc_id
  );

UPDATE database_version
SET
  major = @schema_version__major_new,
  minor = @schema_version__minor_new;