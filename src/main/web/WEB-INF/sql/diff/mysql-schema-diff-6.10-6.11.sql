SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 11;

ALTER TABLE archive_exif CHANGE resolution x_resolution INT(11) DEFAULT NULL;
ALTER TABLE archive_exif ADD COLUMN y_resolution INT(11) DEFAULT NULL AFTER x_resolution;
ALTER TABLE archive_exif ADD COLUMN manufacturer VARCHAR(255) DEFAULT NULL;
ALTER TABLE archive_exif ADD COLUMN model VARCHAR(255) DEFAULT NULL;
ALTER TABLE archive_exif ADD COLUMN compression VARCHAR(255) DEFAULT NULL;
ALTER TABLE archive_exif ADD COLUMN exposure DOUBLE DEFAULT NULL;
ALTER TABLE archive_exif ADD COLUMN exposure_program VARCHAR(255) DEFAULT NULL;
ALTER TABLE archive_exif ADD COLUMN fstop FLOAT DEFAULT NULL;
ALTER TABLE archive_exif ADD COLUMN flash INT(11) DEFAULT NULL;
ALTER TABLE archive_exif ADD COLUMN focal_length FLOAT DEFAULT NULL;
ALTER TABLE archive_exif ADD COLUMN color_space VARCHAR(255) DEFAULT NULL;
ALTER TABLE archive_exif ADD COLUMN resolution_unit SMALLINT(1) DEFAULT NULL;
ALTER TABLE archive_exif ADD COLUMN pixel_x_dimension INT(11) DEFAULT NULL;
ALTER TABLE archive_exif ADD COLUMN pixel_y_dimension INT(11) DEFAULT NULL;
ALTER TABLE archive_exif ADD COLUMN date_original TIMESTAMP NULL DEFAULT NULL;
ALTER TABLE archive_exif ADD COLUMN date_digitized TIMESTAMP NULL DEFAULT NULL;
ALTER TABLE archive_exif ADD COLUMN ISO INT(11) DEFAULT NULL;




--
-- Update schema version
--
UPDATE database_version
SET
  major = @schema_version__major_new,
  minor = @schema_version__minor_new;



