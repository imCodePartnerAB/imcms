CREATE TABLE images_cache (
    id varchar(40) NOT NULL,
    meta_id integer NOT NULL, 
    image_index integer NOT NULL,
    resource nvarchar(255) NOT NULL, 
    cache_type smallint NOT NULL, -- '1-path, 2-file document, 3-URL', 
    file_size integer NOT NULL, 
    frequency integer NOT NULL, 
    format smallint NOT NULL, -- '1-BMP, 2-GIF, 3-JPEG, 4-PNG, 5-PSD, 6-SVG, 7-TIFF, 8-XCF, 9-PICT' 
    rotate_angle smallint NOT NULL, 
    width integer NOT NULL, 
    height integer NOT NULL, 
    crop_x1 integer NOT NULL, 
    crop_y1 integer NOT NULL, 
    crop_x2 integer NOT NULL, 
    crop_y2 integer NOT NULL, 
    created_dt datetime NOT NULL DEFAULT getdate(), 
    
    CONSTRAINT images_cache_pk PRIMARY KEY (id, meta_id, image_index)
);

ALTER TABLE images ADD format smallint; -- '1-BMP, 2-GIF, 3-JPEG, 4-PNG, 5-PSD, 6-SVG, 7-TIFF, 8-XCF, 9-PICT'
ALTER TABLE images ADD rotate_angle smallint;
ALTER TABLE images ADD crop_x1 integer;
ALTER TABLE images ADD crop_y1 integer;
ALTER TABLE images ADD crop_x2 integer;
ALTER TABLE images ADD crop_y2 integer;

ALTER TABLE images_history ADD format smallint; -- '1-BMP, 2-GIF, 3-JPEG, 4-PNG, 5-PSD, 6-SVG, 7-TIFF, 8-XCF, 9-PICT'
ALTER TABLE images_history ADD rotate_angle smallint;
ALTER TABLE images_history ADD crop_x1 integer;
ALTER TABLE images_history ADD crop_y1 integer;
ALTER TABLE images_history ADD crop_x2 integer;
ALTER TABLE images_history ADD crop_y2 integer;

EXEC('UPDATE images SET format = 0, rotate_angle = 0, crop_x1 = -1, crop_y1 = -1, crop_x2 = -1, crop_y2 = -1');
EXEC('UPDATE images_history SET format = 0, rotate_angle = 0, crop_x1 = -1, crop_y1 = -1, crop_x2 = -1, crop_y2 = -1');

ALTER TABLE images ALTER COLUMN format smallint NOT NULL;
ALTER TABLE images ALTER COLUMN rotate_angle smallint NOT NULL;
ALTER TABLE images ALTER COLUMN crop_x1 integer NOT NULL;
ALTER TABLE images ALTER COLUMN crop_y1 integer NOT NULL;
ALTER TABLE images ALTER COLUMN crop_x2 integer NOT NULL;
ALTER TABLE images ALTER COLUMN crop_y2 integer NOT NULL;

ALTER TABLE images_history ALTER COLUMN format smallint NOT NULL;
ALTER TABLE images_history ALTER COLUMN rotate_angle smallint NOT NULL;
ALTER TABLE images_history ALTER COLUMN crop_x1 integer NOT NULL;
ALTER TABLE images_history ALTER COLUMN crop_y1 integer NOT NULL;
ALTER TABLE images_history ALTER COLUMN crop_x2 integer NOT NULL;
ALTER TABLE images_history ALTER COLUMN crop_y2 integer NOT NULL;
