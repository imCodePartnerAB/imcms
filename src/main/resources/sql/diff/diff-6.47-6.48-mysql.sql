# restored image cache table

SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 48;

CREATE TABLE if not exists imcms_text_doc_images_cache (
  id           varchar(40)  NOT NULL,
  resource     varchar(255) NOT NULL,
  cache_type   smallint     NOT NULL,
  file_size    integer      NOT NULL,
  frequency    integer      NOT NULL,
  format       varchar(4)   NOT NULL,
  rotate_angle smallint     NOT NULL,
  width        integer      NOT NULL,
  height       integer      NOT NULL,
  crop_x1      integer      NOT NULL,
  crop_y1      integer      NOT NULL,
  crop_x2      integer      NOT NULL,
  crop_y2      integer      NOT NULL,
  created_dt   timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,

  CONSTRAINT imcms_text_doc_images_cache_pk PRIMARY KEY (id)
)
  ENGINE = 'InnoDB'
  DEFAULT CHARSET = 'utf8';

UPDATE database_version
SET
  major = @schema_version__major_new,
  minor = @schema_version__minor_new;
