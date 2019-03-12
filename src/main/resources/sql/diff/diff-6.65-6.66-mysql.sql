SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 66;

DROP TABLE IF EXISTS `imcms_text_doc_images_history`;

CREATE TABLE `imcms_text_doc_images_history` (
  `id`               int(11)      NOT NULL AUTO_INCREMENT,
  `align`            varchar(255)          DEFAULT NULL,
  `all_languages`    tinyint(4)            DEFAULT NULL,
  `alt_text`         varchar(255)          DEFAULT NULL,
  `archive_image_id` bigint(20)            DEFAULT NULL,
  `border`           int(11)      NOT NULL,
  `crop_x1`          int(11)      NOT NULL,
  `crop_x2`          int(11)      NOT NULL,
  `crop_y1`          int(11)      NOT NULL,
  `crop_y2`          int(11)      NOT NULL,
  `format`           varchar(255) NOT NULL,
  `gen_file`         varchar(255)          DEFAULT NULL,
  `height`           int(11)      NOT NULL,
  `in_text`          tinyint(4)            DEFAULT NULL,
  `index`            int(11)               DEFAULT NULL,
  `linkurl`          varchar(255)          DEFAULT NULL,
  `loop_entry_index` int(11)               DEFAULT NULL,
  `loop_index`       int(11)               DEFAULT NULL,
  `low_scr`          varchar(255)          DEFAULT NULL,
  `image_name`       varchar(255)          DEFAULT NULL,
  `resize`           int(11)      NOT NULL,
  `rotate_angle`     smallint(6)  NOT NULL,
  `bottom_space`     int(11)               DEFAULT NULL,
  `left_space`       int(11)               DEFAULT NULL,
  `right_space`      int(11)               DEFAULT NULL,
  `top_space`        int(11)               DEFAULT NULL,
  `target`           varchar(255)          DEFAULT NULL,
  `type`             int(11)      NOT NULL,
  `imgurl`           varchar(255)          DEFAULT NULL,
  `width`            int(11)      NOT NULL,
  `language_id`      int(11)      NOT NULL,
  `doc_id`           int(11)      NOT NULL,
  `doc_version_no`   int(11)      NOT NULL,
  `modified_at`      datetime              DEFAULT NULL,
  `user_id`          int(11)               DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk__imcms_text_doc_images_history__languages` (`language_id`),
  KEY `fk__imcms_text_doc_images_history__doc_version` (`doc_id`, `doc_version_no`),
  CONSTRAINT `fk__imcms_text_doc_images_images__doc_versio` FOREIGN KEY (`doc_id`, `doc_version_no`) REFERENCES `imcms_doc_versions` (`doc_id`, `no`)
    ON DELETE CASCADE,
  CONSTRAINT `fk__imcms_text_doc_images_history__languages` FOREIGN KEY (`language_id`) REFERENCES `imcms_languages` (`id`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

UPDATE database_version
SET major = @schema_version__major_new,
    minor = @schema_version__minor_new;