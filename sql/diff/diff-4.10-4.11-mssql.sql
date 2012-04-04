-- Extends image handling and adds support for image archive

-- --------------
-- IMAGE HANDLING
-- --------------
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

-- -------------
-- IMAGE ARCHIVE
-- -------------
CREATE TABLE archive_images (
    id int identity NOT NULL,
    image_nm nvarchar(255) NOT NULL DEFAULT '',
    format smallint NOT NULL,
    width integer NOT NULL,
    height integer NOT NULL,
    file_size integer NOT NULL,
    uploaded_by nvarchar(130) NOT NULL DEFAULT '',
    users_id integer NOT NULL,
    status smallint NOT NULL DEFAULT 0,
    created_dt datetime NOT NULL DEFAULT GETDATE(),
    updated_dt datetime NOT NULL DEFAULT GETDATE(),
    license_dt datetime,
    license_end_dt datetime,
    publish_dt datetime,
    archive_dt datetime,
    publish_end_dt datetime,
    CONSTRAINT archive_images_pk PRIMARY KEY (id),
    CONSTRAINT archive_images_users_id_fk FOREIGN KEY (users_id) REFERENCES users(user_id)
);

CREATE TABLE archive_exif (
    image_id int NOT NULL,
    exif_type smallint NOT NULL DEFAULT 0,
    resolution integer NOT NULL,
    description nvarchar(255) NOT NULL DEFAULT '',
    artist nvarchar(255) NOT NULL DEFAULT '',
    copyright nvarchar(255) NOT NULL DEFAULT '',
    created_dt datetime NOT NULL DEFAULT GETDATE(),
    updated_dt datetime NOT NULL DEFAULT GETDATE(),
    CONSTRAINT archive_exif_pk PRIMARY KEY (image_id, exif_type),
    CONSTRAINT archive_image_id_fk FOREIGN KEY (image_id) REFERENCES archive_images(id)
);

CREATE TABLE archive_keywords (
    id int identity,
    keyword_nm nvarchar(50) NOT NULL,
    created_dt datetime NOT NULL DEFAULT GETDATE(),
    CONSTRAINT archive_keywords_pk PRIMARY KEY (id)
);

CREATE TABLE archive_image_keywords (
    image_id int NOT NULL,
    keyword_id int NOT NULL,
    created_dt datetime NOT NULL DEFAULT GETDATE(),
    CONSTRAINT archive_image_keywords_pk PRIMARY KEY (image_id, keyword_id),
    CONSTRAINT archive_image_keywords_image_id_fk FOREIGN KEY (image_id) REFERENCES archive_images(id),
    CONSTRAINT archive_image_keywords_keyword_id_fk FOREIGN KEY (keyword_id) REFERENCES archive_keywords(id)
);

CREATE TABLE category_roles (
    category_id integer NOT NULL,
    role_id integer NOT NULL,
    created_dt datetime NOT NULL DEFAULT GETDATE(),
    CONSTRAINT category_roles_pk PRIMARY KEY (category_id, role_id),
    CONSTRAINT category_roles_category_id_fk FOREIGN KEY (category_id) REFERENCES categories(category_id),
    CONSTRAINT category_roles_role_id_fk FOREIGN KEY (role_id) REFERENCES roles(role_id)
);

CREATE TABLE image_categories (
    image_id int NOT NULL,
    category_id integer NOT NULL,
    created_dt datetime NOT NULL DEFAULT GETDATE(),
    CONSTRAINT image_categories_pk PRIMARY KEY (image_id, category_id),
    CONSTRAINT image_categories_image_id_fk FOREIGN KEY (image_id) REFERENCES archive_images(id),
    CONSTRAINT image_categories_category_id_fk FOREIGN KEY (category_id) REFERENCES categories(category_id)
);

CREATE TABLE archive_libraries (
    id int identity NOT NULL ,
    library_nm nvarchar(120) NOT NULL,
    folder_nm nvarchar(255) NOT NULL,
    filepath nvarchar(255),
    library_type smallint NOT NULL DEFAULT 0,
    created_dt datetime NOT NULL DEFAULT GETDATE(),
    updated_dt datetime NOT NULL DEFAULT GETDATE(),
    CONSTRAINT archive_libraries_pk PRIMARY KEY (id)--,
    --CONSTRAINT archive_libraries_folder_nm_filepath_unq UNIQUE (folder_nm, filepath)
);

CREATE TABLE archive_library_roles (
    library_id integer NOT NULL,
    role_id integer NOT NULL,
    permissions integer NOT NULL,
    created_dt datetime NOT NULL DEFAULT GETDATE(),
    updated_dt datetime NOT NULL DEFAULT GETDATE(),
    CONSTRAINT archive_library_roles_pk PRIMARY KEY (library_id, role_id),
    CONSTRAINT archive_library_roles_library_id_fk FOREIGN KEY (library_id) REFERENCES archive_libraries(id),
    CONSTRAINT archive_library_roles_role_id_fk FOREIGN KEY (role_id) REFERENCES roles(role_id)
);

ALTER TABLE users ADD remember_cd varchar(40);
-- ALTER TABLE users ADD CONSTRAINT users_remember_cd_unq UNIQUE (remember_cd);

ALTER TABLE images ADD archive_image_id bigint;
ALTER TABLE images_history ADD archive_image_id bigint;

ALTER TABLE category_types ADD is_image_archive tinyint NOT NULL DEFAULT 0;

UPDATE database_version SET major = 4, minor = 11;
