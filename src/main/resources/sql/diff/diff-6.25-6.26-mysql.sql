# changing type and values for image column "format"
SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 26;

ALTER TABLE `imcms_text_doc_images`
  ADD COLUMN `format_tmp` VARCHAR(4) NOT NULL;

UPDATE `imcms_text_doc_images`
SET `format_tmp` = 'JPEG'
WHERE `format` = 0;
UPDATE `imcms_text_doc_images`
SET `format_tmp` = 'BMP'
WHERE `format` = 1;
UPDATE `imcms_text_doc_images`
SET `format_tmp` = 'GIF'
WHERE `format` = 2;
UPDATE `imcms_text_doc_images`
SET `format_tmp` = 'JPEG'
WHERE `format` = 3;
UPDATE `imcms_text_doc_images`
SET `format_tmp` = 'PNG'
WHERE `format` = 4;
UPDATE `imcms_text_doc_images`
SET `format_tmp` = 'PSD'
WHERE `format` = 5;
UPDATE `imcms_text_doc_images`
SET `format_tmp` = 'SVG'
WHERE `format` = 6;
UPDATE `imcms_text_doc_images`
SET `format_tmp` = 'TIFF'
WHERE `format` = 7;
UPDATE `imcms_text_doc_images`
SET `format_tmp` = 'XCF'
WHERE `format` = 8;
UPDATE `imcms_text_doc_images`
SET `format_tmp` = 'PICT'
WHERE `format` = 9;

ALTER TABLE `imcms_text_doc_images`
  DROP COLUMN `format`;
ALTER TABLE `imcms_text_doc_images`
  CHANGE `format_tmp` `format` VARCHAR(4) NOT NULL;

UPDATE database_version
SET
  major = @schema_version__major_new,
  minor = @schema_version__minor_new;
