# if image url starts with slash
# then remove the slash and set type to 0

SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 49;

UPDATE imcms_text_doc_images
SET
  imcms_text_doc_images.imgurl = SUBSTRING(imcms_text_doc_images.imgurl, 2),
  imcms_text_doc_images.type   = 0
WHERE
  imcms_text_doc_images.imgurl LIKE "/%";

UPDATE database_version
SET
  major = @schema_version__major_new,
  minor = @schema_version__minor_new;