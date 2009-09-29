CREATE TABLE archive_images (
    id bigint NOT NULL AUTO_INCREMENT, 
    image_nm varchar(255) NOT NULL DEFAULT '', 
    format smallint NOT NULL COMMENT '0-BMP, 1-GIF, 2-JPEG, 3-PNG, 4-PDF, 5-PS, 6-PSD, 7-SVG, 8-TIFF, 9-XCF, 10-PICT', 
    width integer NOT NULL, 
    height integer NOT NULL, 
    file_size integer NOT NULL COMMENT 'in bytes', 
    uploaded_by varchar(130) NOT NULL DEFAULT '', 
    users_id integer NOT NULL, 
    status smallint NOT NULL DEFAULT 0 COMMENT '0-uploaded, 1-active, 2-archived', 
    created_dt timestamp NOT NULL DEFAULT 0, 
    updated_dt timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, 
    license_dt date, 
    license_end_dt date, 
    publish_dt date, 
    archive_dt date, 
    publish_end_dt date, 
    CONSTRAINT archive_images_pk PRIMARY KEY (id), 
    CONSTRAINT archive_images_users_id_fk FOREIGN KEY (users_id) REFERENCES users(user_id)
) ENGINE='InnoDB' DEFAULT CHARACTER SET='utf8';

CREATE TABLE archive_exif (
    image_id bigint NOT NULL, 
    exif_type smallint NOT NULL DEFAULT 0 COMMENT '0-original, 1-changed', 
    resolution integer NOT NULL COMMENT 'in DPI (dots per inch)', 
    description varchar(255) NOT NULL DEFAULT '', 
    artist varchar(255) NOT NULL DEFAULT '', 
    copyright varchar(255) NOT NULL DEFAULT '', 
    created_dt timestamp NOT NULL DEFAULT 0, 
    updated_dt timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, 
    CONSTRAINT archive_exif_pk PRIMARY KEY (image_id, exif_type), 
    CONSTRAINT archive_image_id_fk FOREIGN KEY (image_id) REFERENCES archive_images(id)
) ENGINE='InnoDB' DEFAULT CHARACTER SET='utf8';

CREATE TABLE archive_keywords (
    id bigint NOT NULL AUTO_INCREMENT, 
    keyword_nm varchar(50) NOT NULL, 
    created_dt timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP, 
    CONSTRAINT archive_keywords_pk PRIMARY KEY (id)
) ENGINE='InnoDB' DEFAULT CHARACTER SET='utf8';

CREATE TABLE archive_image_keywords (
    image_id bigint NOT NULL, 
    keyword_id bigint NOT NULL, 
    created_dt timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP, 
    CONSTRAINT archive_image_keywords_pk PRIMARY KEY (image_id, keyword_id), 
    CONSTRAINT archive_image_keywords_image_id_fk FOREIGN KEY (image_id) REFERENCES archive_images(id), 
    CONSTRAINT archive_image_keywords_keyword_id_fk FOREIGN KEY (keyword_id) REFERENCES archive_keywords(id)
) ENGINE='InnoDB' DEFAULT CHARACTER SET='utf8';

CREATE TABLE category_roles (
    category_id integer NOT NULL, 
    role_id integer NOT NULL, 
    created_dt timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP, 
    CONSTRAINT category_roles_pk PRIMARY KEY (category_id, role_id), 
    CONSTRAINT category_roles_category_id_fk FOREIGN KEY (category_id) REFERENCES categories(category_id), 
    CONSTRAINT category_roles_role_id_fk FOREIGN KEY (role_id) REFERENCES roles(role_id)
) ENGINE='InnoDB' DEFAULT CHARACTER SET='utf8';

CREATE TABLE image_categories (
    image_id bigint NOT NULL, 
    category_id integer NOT NULL, 
    created_dt timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP, 
    CONSTRAINT image_categories_pk PRIMARY KEY (image_id, category_id), 
    CONSTRAINT image_categories_image_id_fk FOREIGN KEY (image_id) REFERENCES archive_images(id), 
    CONSTRAINT image_categories_category_id_fk FOREIGN KEY (category_id) REFERENCES categories(category_id)
) ENGINE='InnoDB' DEFAULT CHARACTER SET='utf8';

CREATE TABLE archive_libraries (
    id integer NOT NULL AUTO_INCREMENT, 
    library_nm varchar(120) NOT NULL, 
    folder_nm varchar(255) NOT NULL, 
    filepath varchar(255), 
    library_type smallint NOT NULL DEFAULT 0 COMMENT '0-standard, 1-old library', 
    created_dt timestamp NOT NULL DEFAULT 0, 
    updated_dt timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, 
    CONSTRAINT archive_libraries_pk PRIMARY KEY (id), 
    CONSTRAINT archive_libraries_folder_nm_filepath_unq UNIQUE (folder_nm, filepath)
) ENGINE='InnoDB' DEFAULT CHARACTER SET='utf8';

CREATE TABLE archive_library_roles (
    library_id integer NOT NULL, 
    role_id integer NOT NULL, 
    permissions integer NOT NULL, 
    created_dt timestamp NOT NULL DEFAULT 0, 
    updated_dt timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, 
    CONSTRAINT archive_library_roles_pk PRIMARY KEY (library_id, role_id), 
    CONSTRAINT archive_library_roles_library_id_fk FOREIGN KEY (library_id) REFERENCES archive_libraries(id), 
    CONSTRAINT archive_library_roles_role_id_fk FOREIGN KEY (role_id) REFERENCES roles(role_id)
) ENGINE='InnoDB' DEFAULT CHARACTER SET='utf8';

ALTER TABLE users ADD COLUMN remember_cd varchar(40) AFTER session_id;
ALTER TABLE users ADD CONSTRAINT users_remember_cd_unq UNIQUE (remember_cd);

ALTER TABLE images ADD COLUMN archive_image_id bigint;
ALTER TABLE images_history ADD COLUMN archive_image_id bigint;

ALTER TABLE category_types ADD COLUMN is_image_archive tinyint(1) NOT NULL DEFAULT FALSE COMMENT 'is image category type, can be used in image archive'  AFTER inherited;
