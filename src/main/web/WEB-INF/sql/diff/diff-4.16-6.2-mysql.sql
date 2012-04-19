-- New schema version to assign after upgrade
SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 2;

SET @doc_language_id_en = 1;
SET @doc_language_id_sw = 2;
SET @doc_language_id = @doc_language_id_en;

SET @doc_version_no = 0;

--
-- Languages support: 
--
CREATE TABLE `imcms_languages` (
  `id` int AUTO_INCREMENT PRIMARY KEY,
  `code` varchar(3) NOT NULL COMMENT 'Language code.',
  `name` varchar(128) NOT NULL COMMENT 'Language name in english.',
  `native_name` varchar(128) DEFAULT NULL COMMENT 'Language native name e.g Svenska, Suomi, etc.',
  `enabled` tinyint NOT NULL DEFAULT true COMMENT 'Language enabled status.',
  
  CONSTRAINT `uk__imcms_languages__code` UNIQUE KEY (`code`),
  CONSTRAINT `uk__imcms_languages__name` UNIQUE KEY (`name`),
  CONSTRAINT `uk__imcms_languages__native_name` UNIQUE KEY (`native_name`)  
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- Add languages
INSERT INTO imcms_languages
  (`id`, `code`, `name`, `native_name`, `enabled`)
VALUES
  (@doc_language_id_en, 'en', 'English', 'English', true),
  (@doc_language_id_sw, 'sv', 'Swedish', 'Svenska', true);


-- default language id
INSERT INTO sys_types (
  `type_id`, `name`
) VALUES (
  8, 'DefaultLanguageId'
);

INSERT INTO sys_data (
  `type_id`, `value`
) VALUES (
  8, @doc_language_id
);

--
-- Doc languages
-- 
-- enabled document languages
CREATE TABLE `imcms_doc_languages` (
  `id` int AUTO_INCREMENT PRIMARY KEY,
  `doc_id` int NOT NULL,
  `language_id` int NOT NULL,
  
  CONSTRAINT `uk__imcms_doc_languages__doc_id__language_id` UNIQUE KEY (`doc_id`,`language_id`),
  CONSTRAINT `fk__imcms_doc_languages__meta` FOREIGN KEY (`doc_id`) REFERENCES `meta` (`meta_id`) ON DELETE CASCADE,
  CONSTRAINT `fk__imcms_doc_languages__languages` FOREIGN KEY (`language_id`) REFERENCES `imcms_languages` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


INSERT INTO `imcms_doc_languages` (`doc_id`, `language_id`)
SELECT `meta_id`, @doc_language_id FROM `meta`;

--
-- Keywords
--
CREATE TABLE `imcms_doc_keywords` (
  `id` int AUTO_INCREMENT PRIMARY KEY,
  `doc_id` int NOT NULL,
  `value` varchar(128) NOT NULL,
  
  CONSTRAINT `uk__imcms_doc_keywords__doc_id__value` UNIQUE KEY  (`doc_id`,`value`),
  CONSTRAINT `fk__imcms_doc_keywords__meta` FOREIGN KEY (`doc_id`) REFERENCES `meta` (`meta_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- Move keywords to new keyword table
INSERT INTO `imcms_doc_keywords` (`doc_id`, `value`)
SELECT mc.meta_id, c.code FROM meta_classification mc join classification c on mc.class_id = c.class_id;

--
-- Version support
--
CREATE TABLE `imcms_doc_versions` (
  `id` int AUTO_INCREMENT PRIMARY KEY,
  `doc_id` int NOT NULL,
  `no` int NOT NULL,
  `created_by` int NULL,
  `created_dt` datetime NOT NULL,
  `modified_by` int NULL,
  `modified_dt` datetime NULL,
  
  CONSTRAINT `uk__imcms_doc_versions__doc_id__no` UNIQUE KEY (`doc_id`,`no`),
  CONSTRAINT `fk__imcms_doc_versions__meta` FOREIGN KEY (`doc_id`) REFERENCES `meta` (`meta_id`) ON DELETE CASCADE,
  CONSTRAINT `fk__imcms_doc_versions__user1` FOREIGN KEY (`created_by`) REFERENCES `users` (`user_id`) ON DELETE SET NULL,
  CONSTRAINT `fk__imcms_doc_versions__user2` FOREIGN KEY (`modified_by`) REFERENCES `users` (`user_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


INSERT INTO `imcms_doc_versions` (
  `doc_id`,
  `no`,
  `created_by`,
  `created_dt`,
  `modified_by`,
  `modified_dt`
) SELECT
  `meta_id`,
  @doc_version_no,
  `owner_id`,
  `date_created`,
  `owner_id`,
  `date_modified`
FROM `meta`;

-- in 5.5->5.6 default no is moved into the meta table
CREATE TABLE imcms_doc_default_version (
    id int AUTO_INCREMENT PRIMARY KEY,
    doc_id int NOT NULL,
    no int NOT NULL DEFAULT 0,

    CONSTRAINT uk__imcms_doc_default_version__doc_id__version_no UNIQUE KEY (doc_id, no),
    CONSTRAINT fk__imcms_doc_default_version__doc_versions FOREIGN KEY (doc_id,no) REFERENCES imcms_doc_versions (doc_id, no) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


INSERT INTO `imcms_doc_default_version` (
  `doc_id`,
  `no`
) SELECT
  `meta_id`,
  @doc_version_no
FROM `meta`;


--
-- Document labels
--
CREATE TABLE `imcms_doc_labels` (
  `id` int AUTO_INCREMENT PRIMARY KEY,
  `doc_id` int NOT NULL,
  `doc_version_no` int NOT NULL,
  `language_id` int NOT NULL,
  `headline` varchar(256) DEFAULT NULL,
  `menu_image_url` varchar(256) DEFAULT NULL,
  `menu_text` varchar(1024) DEFAULT NULL,
  
  CONSTRAINT `fk__imcms_doc_labels__doc_id__doc_version_no__language_id` UNIQUE KEY (doc_id, doc_version_no, language_id),  
  CONSTRAINT `fk__imcms_doc_labels__doc_versions` FOREIGN KEY (doc_id,doc_version_no) REFERENCES imcms_doc_versions (doc_id, no) ON DELETE CASCADE,
  CONSTRAINT `fk__imcms_doc_labels__languages` FOREIGN KEY (`language_id`) REFERENCES `imcms_languages` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


INSERT INTO `imcms_doc_labels` (
  `doc_id`,
  `doc_version_no`,
  `language_id`,

  `headline`,
  `menu_text`,
  `menu_image_url`
) SELECT
  `meta_id`,
  @doc_version_no,
  @doc_language_id,

  `meta_headline`,
  `meta_text`,
  `meta_image`
FROM `meta`;


ALTER TABLE meta
  DROP COLUMN meta_headline,
  DROP COLUMN meta_text,
  DROP COLUMN meta_image,
  -- todo: move to properties.
  ADD COLUMN  `disabled_language_show_rule` varchar(32) NOT NULL DEFAULT 'DO_NOT_SHOW';
  --  COMMENT 'Possible values: DO_NOT_SHOW, SHOW_IN_DEFAULT_LANGUAGE';



--
-- Content loops
--
CREATE TABLE `imcms_text_doc_content_loops` (
  `id` int AUTO_INCREMENT PRIMARY KEY,
  `doc_id` int NOT NULL,
  `doc_version_no` int NOT NULL,
  `no` int NOT NULL,
  
  CONSTRAINT `uk__imcms_text_doc_content_loops` UNIQUE KEY (`doc_id`,`doc_version_no`,`no`),
  CONSTRAINT `fk__imcms_text_doc_content_loops__imcms_doc_versions` FOREIGN KEY (`doc_id`, `doc_version_no`) REFERENCES `imcms_doc_versions` (`doc_id`, `no`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `imcms_text_doc_contents` (
  `id` int AUTO_INCREMENT PRIMARY KEY,
  `doc_id` int NOT NULL,
  `doc_version_no` int NOT NULL,
  `loop_no` int DEFAULT NULL,
  `no` int NOT NULL,
  `order_no` int NOT NULL,
  `enabled` tinyint NOT NULL DEFAULT true,
  
  CONSTRAINT `uk__imcms_text_doc_contents__doc_id__doc_version_no__loop_no__no` UNIQUE KEY (`doc_id`,`doc_version_no`,`loop_no`,`no`),
  CONSTRAINT `fk__imcms_text_doc_contents__imcms_doc_versions` FOREIGN KEY (`doc_id`, `doc_version_no`) REFERENCES `imcms_doc_versions` (`doc_id`, `no`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



--
-- Table new_doc_permission_sets_ex
--

CREATE TABLE __new_doc_permission_sets_ex (
  id int AUTO_INCREMENT PRIMARY KEY,
  meta_id int NOT NULL,
  set_id int NOT NULL,
  permission_id int NOT NULL,
  permission_data int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO __new_doc_permission_sets_ex (
  meta_id,
  set_id,
  permission_id,
  permission_data
) SELECT
  meta_id, set_id, permission_id, permission_data
FROM new_doc_permission_sets_ex;

DROP TABLE new_doc_permission_sets_ex;
RENAME TABLE __new_doc_permission_sets_ex TO new_doc_permission_sets_ex;

ALTER TABLE new_doc_permission_sets_ex
  ADD CONSTRAINT uk__new_doc_permission_sets_ex__1 UNIQUE KEY (meta_id, set_id, permission_id, permission_data),
  ADD CONSTRAINT fk__new_doc_permission_sets_ex__meta FOREIGN KEY (meta_id) REFERENCES meta (meta_id) ON DELETE CASCADE,
  ADD CONSTRAINT fk__new_doc_permission_sets_ex__permission_sets FOREIGN KEY (set_id) REFERENCES permission_sets (set_id);



CREATE TABLE __doc_permission_sets_ex (
  id int AUTO_INCREMENT PRIMARY KEY,
  meta_id int NOT NULL,
  set_id int NOT NULL,
  permission_id int NOT NULL,
  permission_data int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO __doc_permission_sets_ex (
  meta_id,
  set_id,
  permission_id,
  permission_data
) SELECT
  meta_id, set_id, permission_id, permission_data
FROM doc_permission_sets_ex;

DROP TABLE doc_permission_sets_ex;
RENAME TABLE __doc_permission_sets_ex TO doc_permission_sets_ex;

ALTER TABLE doc_permission_sets_ex
  ADD CONSTRAINT uk__doc_permission_sets_ex__1 UNIQUE KEY (meta_id, set_id, permission_id, permission_data),
  ADD CONSTRAINT fk__doc_permission_sets_ex__meta FOREIGN KEY (meta_id) REFERENCES meta (meta_id) ON DELETE CASCADE,
  ADD CONSTRAINT fk__doc_permission_sets_ex__permission_sets FOREIGN KEY (set_id) REFERENCES permission_sets (set_id);


CREATE TABLE __new_doc_permission_sets (
  id int AUTO_INCREMENT PRIMARY KEY,
  meta_id int NOT NULL,
  set_id int NOT NULL,
  permission_id int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


INSERT INTO __new_doc_permission_sets (
  meta_id,
  set_id,
  permission_id
) SELECT
  meta_id, set_id, permission_id
FROM new_doc_permission_sets;

DROP TABLE new_doc_permission_sets;
RENAME TABLE __new_doc_permission_sets TO new_doc_permission_sets;

ALTER TABLE new_doc_permission_sets
  ADD CONSTRAINT uk__new_doc_permission_sets__meta_id__set_id UNIQUE KEY (meta_id, set_id),
  ADD CONSTRAINT fk__new_doc_permission_sets__meta FOREIGN KEY (meta_id) REFERENCES meta (meta_id) ON DELETE CASCADE,
  ADD CONSTRAINT fk__new_doc_permission_sets__permission_sets FOREIGN KEY (set_id) REFERENCES permission_sets (set_id);

--
-- Update permissions:
--

CREATE TABLE __doc_permission_sets (
  id int AUTO_INCREMENT PRIMARY KEY,
  meta_id int NOT NULL,
  set_id int NOT NULL,
  permission_id int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO __doc_permission_sets (
  meta_id,
  set_id,
  permission_id
) SELECT
  meta_id, set_id, permission_id
FROM doc_permission_sets;

DROP TABLE doc_permission_sets;
RENAME TABLE __doc_permission_sets TO doc_permission_sets;

ALTER TABLE doc_permission_sets
  ADD CONSTRAINT uk__doc_permission_sets__meta_id__set_id UNIQUE KEY (meta_id, set_id),
  ADD CONSTRAINT fk__doc_permission_sets__meta FOREIGN KEY (meta_id) REFERENCES meta (meta_id) ON DELETE CASCADE,
  ADD CONSTRAINT fk__doc_permission_sets__permission_sets FOREIGN KEY (set_id) REFERENCES permission_sets (set_id);


--
-- File upload table:
--
CREATE TABLE __fileupload_docs (
  id int AUTO_INCREMENT PRIMARY KEY,
  meta_id int NOT NULL,
  doc_version_no int NOT NULL,
  variant_name varchar(100) NOT NULL,
  filename varchar(255) NOT NULL,
  mime varchar(50) NOT NULL,
  created_as_image int NOT NULL,
  default_variant tinyint(1) NOT NULL default '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


INSERT INTO __fileupload_docs (
  meta_id,
  doc_version_no,
  variant_name,
  filename,
  mime,
  created_as_image,
  default_variant
) SELECT
  meta_id,
  0,
  variant_name,
  filename,
  mime,
  created_as_image,
  default_variant
FROM fileupload_docs;

DROP TABLE fileupload_docs;
RENAME TABLE __fileupload_docs TO fileupload_docs;

ALTER TABLE fileupload_docs
  ADD CONSTRAINT uk__fileupload_docs__1 UNIQUE KEY (meta_id, doc_version_no, variant_name),
  ADD CONSTRAINT fk__fileupload_docs__doc_version FOREIGN KEY (meta_id, doc_version_no) REFERENCES imcms_doc_versions (doc_id, no) ON DELETE CASCADE;


--
-- HTML DOCS
--
CREATE TABLE `imcms_html_docs` (
  `id` int AUTO_INCREMENT PRIMARY KEY,
  `doc_id` int NOT NULL,
  `doc_version_no` int NOT NULL,
  `html` longtext,
  
  CONSTRAINT uk__imcms_html_docs__doc_id__doc_version_no UNIQUE KEY (doc_id, doc_version_no),
  CONSTRAINT fk__imcms_html_docs__doc_version FOREIGN KEY (doc_id, doc_version_no) REFERENCES imcms_doc_versions (doc_id, no) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO imcms_html_docs(
  doc_id,
  doc_version_no,
  html
)
SELECT
  meta_id,
  @doc_version_no,
  frame_set
FROM `frameset_docs`;

--
-- URL Documents
--
CREATE TABLE `imcms_url_docs` (
  `id` int AUTO_INCREMENT PRIMARY KEY,
  `doc_id` int NOT NULL,
  `doc_version_no` int NOT NULL,
  `frame_name` varchar(80) NOT NULL,
  `target` varchar(15) NOT NULL,
  `url_ref` varchar(255) NOT NULL,
  `url_txt` varchar(255) NOT NULL,
  `lang_prefix` varchar(3) NOT NULL,
  CONSTRAINT uk__imcms_url_docs__doc_id__doc_version_no UNIQUE KEY (doc_id, doc_version_no),
  CONSTRAINT fk__imcms_url_docs__doc_version FOREIGN KEY (doc_id, doc_version_no) REFERENCES imcms_doc_versions (doc_id, no) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


INSERT INTO `imcms_url_docs` (
  `doc_id`,
  `doc_version_no`,
  `frame_name`,
  `target`,
  `url_ref`,
  `url_txt`,
  `lang_prefix`
)
SELECT 
  meta_id,
  @doc_version_no,
  `frame_name`,
  `target`,
  `url_ref`,
  `url_txt`,
  `lang_prefix`
FROM `url_docs`;


--
-- Menus
--
CREATE TABLE imcms_text_doc_menus (
  id int NOT NULL AUTO_INCREMENT PRIMARY KEY,
  doc_id int NOT NULL,
  doc_version_no int NOT NULL,
  no int NOT NULL,
  sort_order int NOT NULL,

  CONSTRAINT uk__imcms_text_doc_menus__doc_id__doc_version_no__no UNIQUE KEY (doc_id, doc_version_no, no),
  CONSTRAINT fk__imcms_text_doc_menus__doc_version FOREIGN KEY (doc_id, doc_version_no) REFERENCES imcms_doc_versions (doc_id, no) ON DELETE CASCADE

) ENGINE=InnoDB DEFAULT CHARSET=utf8;


INSERT INTO imcms_text_doc_menus (
  id,
  doc_id,
  doc_version_no,
  no,
  sort_order
) SELECT
  menu_id,
  meta_id,
  @doc_version_no,
  menu_index,
  sort_order
FROM
  menus;


CREATE TABLE imcms_text_doc_menu_items (
  id int NOT NULL AUTO_INCREMENT PRIMARY KEY,
  menu_id int NOT NULL,
  to_doc_id int NOT NULL,
  manual_sort_order int NOT NULL,
  tree_sort_index varchar(64) NOT NULL,

  CONSTRAINT uk__imcms_text_doc_menu_items__menu_id__doc_id UNIQUE KEY (menu_id, to_doc_id),
  CONSTRAINT fk__imcms_text_doc_menu_items__menu FOREIGN KEY (menu_id) REFERENCES imcms_text_doc_menus (id) ON DELETE CASCADE

) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO imcms_text_doc_menu_items (
  menu_id,
  to_doc_id,
  manual_sort_order,
  tree_sort_index
)
SELECT
  menu_id, to_meta_id, manual_sort_order, tree_sort_index
FROM
  childs;


CREATE TABLE `imcms_text_doc_menus_history` (
  id int AUTO_INCREMENT PRIMARY KEY,
  menu_id int NOT NULL,  
  doc_id int NOT NULL,
  doc_version_no int NOT NULL,
  no int NOT NULL,
  sort_order int NOT NULL,
  
  `modified_dt` datetime NOT NULL,
  `user_id` int NOT NULL,
  
  CONSTRAINT fk__imcms_text_doc_menus_history__doc_versions FOREIGN KEY (doc_id, doc_version_no) REFERENCES imcms_doc_versions (doc_id, no) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `imcms_text_doc_menu_items_history` (
  id int AUTO_INCREMENT PRIMARY KEY,
  menu_id int NULL,
  to_doc_id int NOT NULL,
  manual_sort_order int NOT NULL,
  tree_sort_index varchar(64) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


INSERT INTO imcms_text_doc_menus_history (
  menu_id, doc_id, doc_version_no, no, sort_order, modified_dt, user_id
)
SELECT menu_id, meta_id, @doc_version_no, menu_index, sort_order, modified_datetime, user_id
FROM menus_history;

INSERT INTO imcms_text_doc_menu_items_history (
  menu_id, to_doc_id, manual_sort_order, tree_sort_index
)
SELECT menu_id, to_meta_id, manual_sort_order, tree_sort_index
FROM childs_history;


-- text documents texts
CREATE TABLE imcms_text_doc_texts (
    id int AUTO_INCREMENT PRIMARY KEY,
    doc_id int default NULL,
    doc_version_no int NOT NULL,
    no int NOT NULL,
    text longtext NOT NULL,
    type int default NULL,
    language_id int NOT NULL,
    content_loop_no int DEFAULT NULL,
    content_no int DEFAULT NULL,

    CONSTRAINT uk__imcms_text_doc_texts__text UNIQUE KEY (doc_id, doc_version_no, no, language_id, content_loop_no, content_no),
    CONSTRAINT fk__imcms_text_doc_texts__content FOREIGN KEY (doc_id, doc_version_no, content_loop_no, content_no) REFERENCES imcms_text_doc_contents (`doc_id`, `doc_version_no`, `loop_no`, `no`) ON DELETE CASCADE,
    CONSTRAINT fk__imcms_text_doc_texts__languages FOREIGN KEY (language_id) REFERENCES imcms_languages (id),
    CONSTRAINT fk__imcms_text_doc_texts__doc_version FOREIGN KEY (doc_id, doc_version_no) REFERENCES imcms_doc_versions (doc_id, no) ON DELETE CASCADE

) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- todo: Delete duplicates from text table
-- DELETE FROM texts l USING texts l, texts r WHERE l.meta_id = r.meta_id and l.name = r.name and l.counter < r.counter;

INSERT INTO imcms_text_doc_texts (
    doc_id,
    doc_version_no,
    no,
    text,
    type,
    language_id,
    content_loop_no,
    content_no
)
SELECT
    meta_id, @doc_version_no, name, text, type, @doc_language_id, NULL, NULL
FROM texts;
  

CREATE TABLE imcms_text_doc_texts_history (
    id int AUTO_INCREMENT PRIMARY KEY,
    doc_id int default NULL,
    doc_version_no int DEFAULT NULL,
    no int NOT NULL,
    text longtext NOT NULL,
    type int default NULL,
    language_id int NOT NULL,
    content_loop_no int DEFAULT NULL,
    content_no int DEFAULT NULL,
    modified_dt datetime NOT NULL,
    user_id int DEFAULT NULL,

    CONSTRAINT fk__imcms_text_doc_texts_history__languages FOREIGN KEY (language_id) REFERENCES imcms_languages (id),
    CONSTRAINT fk__imcms_text_doc_texts_history__content FOREIGN KEY (doc_id, doc_version_no, content_loop_no, content_no) REFERENCES imcms_text_doc_contents (`doc_id`, `doc_version_no`, `loop_no`, `no`) ON DELETE CASCADE,
    CONSTRAINT fk__imcms_text_doc_texts_history__users FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE SET NULL,
    CONSTRAINT fk__imcms_text_doc_texts_history__doc_versions FOREIGN KEY (doc_id, doc_version_no) REFERENCES imcms_doc_versions (doc_id, no) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


INSERT INTO imcms_text_doc_texts_history (
    doc_id,
    doc_version_no,
    no,
    text,
    type,
    language_id,
    content_loop_no,
    content_no,
    modified_dt,
    user_id
) SELECT
    meta_id,
    @doc_version_no,
    name,
    text,
    type,
    @doc_language_id,
    null,
    null,
    modified_datetime,
    user_id
FROM texts_history;

--
-- Images
--
CREATE TABLE imcms_text_doc_images (
  id int AUTO_INCREMENT PRIMARY KEY,
  doc_id int DEFAULT NULL,
  doc_version_no int NOT NULL,
  width int NOT NULL,
  height int NOT NULL,
  border int NOT NULL,
  v_space int NOT NULL,
  h_space int NOT NULL,
  no int NOT NULL,
  image_name varchar(40) NOT NULL DEFAULT '',
  target varchar(15) NOT NULL,
  align varchar(15) NOT NULL,
  alt_text varchar(255) NOT NULL,
  low_scr varchar(255) NOT NULL,
  imgurl varchar(255) NOT NULL,
  linkurl varchar(255) NOT NULL,
  type int NOT NULL,
  language_id int NOT NULL,
  archive_image_id bigint,
  format smallint NOT NULL, 
  rotate_angle smallint NOT NULL, 
  crop_x1 integer NOT NULL, 
  crop_y1 integer NOT NULL, 
  crop_x2 integer NOT NULL, 
  crop_y2 integer NOT NULL, 
  content_loop_no int DEFAULT NULL,
  content_no int DEFAULT NULL,


  CONSTRAINT uk__imcms_text_doc_images__image UNIQUE KEY (doc_id,doc_version_no,no,language_id,content_loop_no,content_no),
  CONSTRAINT fk__imcms_text_doc_images__languages FOREIGN KEY (language_id) REFERENCES imcms_languages (id),
  CONSTRAINT fk__imcms_text_doc_images__content FOREIGN KEY (doc_id, doc_version_no, content_loop_no, content_no) REFERENCES imcms_text_doc_contents (`doc_id`, `doc_version_no`, `loop_no`, `no`) ON DELETE CASCADE,  
  CONSTRAINT fk__imcms_text_doc_images__doc_version FOREIGN KEY (doc_id, doc_version_no) REFERENCES imcms_doc_versions (doc_id, no) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


INSERT INTO imcms_text_doc_images (
  doc_id,
  doc_version_no,
  width,
  height,
  border,
  v_space,
  h_space,
  no,
  image_name,
  target,
  align,
  alt_text,
  low_scr,
  imgurl,
  linkurl,
  type,
  language_id,
  archive_image_id, 
  format, 
  rotate_angle, 
  crop_x1, 
  crop_y1, 
  crop_x2, 
  crop_y2, 

  content_loop_no,
  content_no

) SELECT
  meta_id,
  @doc_version_no,
  width,
  height,
  border,
  v_space,
  h_space,
  name,
  image_name,
  target,
  align,
  alt_text,
  low_scr,
  imgurl,
  linkurl,
  type,
  @doc_language_id,
  archive_image_id, 
  format, 
  rotate_angle, 
  crop_x1, 
  crop_y1, 
  crop_x2, 
  crop_y2, 
  NULL,
  NULL
FROM images;


CREATE TABLE imcms_text_doc_images_history (
  id int AUTO_INCREMENT PRIMARY KEY,
  doc_id int DEFAULT NULL,
  doc_version_no int NOT NULL,
  width int NOT NULL,
  height int NOT NULL,
  border int NOT NULL,
  v_space int NOT NULL,
  h_space int NOT NULL,
  no int NOT NULL,
  image_name varchar(40) NOT NULL DEFAULT '',
  target varchar(15) NOT NULL,
  align varchar(15) NOT NULL,
  alt_text varchar(255) NOT NULL,
  low_scr varchar(255) NOT NULL,
  imgurl varchar(255) NOT NULL,
  linkurl varchar(255) NOT NULL,
  type int NOT NULL,
  language_id int NOT NULL,
  archive_image_id bigint, 
  format smallint NOT NULL, 
  rotate_angle smallint NOT NULL, 
  crop_x1 integer NOT NULL, 
  crop_y1 integer NOT NULL, 
  crop_x2 integer NOT NULL, 
  crop_y2 integer NOT NULL, 
  content_loop_no int DEFAULT NULL,
  content_no int DEFAULT NULL,
  modified_dt datetime NOT NULL,
  user_id int DEFAULT NULL,

  CONSTRAINT fk__imcms_text_doc_images_history__languages FOREIGN KEY (language_id) REFERENCES imcms_languages (id),
  CONSTRAINT fk__imcms_text_doc_images_history__users FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE SET NULL,
  CONSTRAINT fk__imcms_text_doc_images_history__content FOREIGN KEY (doc_id, doc_version_no, content_loop_no, content_no) REFERENCES imcms_text_doc_contents (`doc_id`, `doc_version_no`, `loop_no`, `no`) ON DELETE CASCADE,   
  CONSTRAINT fk__imcms_text_doc_images_history__doc_version FOREIGN KEY (doc_id, doc_version_no) REFERENCES imcms_doc_versions (doc_id, no) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO imcms_text_doc_images_history (
  doc_id,
  doc_version_no,
  width,
  height,
  border,
  v_space,
  h_space,
  no,
  image_name,
  target,
  align,
  alt_text,
  low_scr,
  imgurl,
  linkurl,
  type,
  language_id,
  archive_image_id,
  format, 
  rotate_angle,
  crop_x1,
  crop_y1,
  crop_x2,
  crop_y2,
  content_loop_no,
  content_no,
  modified_dt,
  user_id
) SELECT
  `meta_id`,
  @doc_version_no,
  `width`,
  `height`,
  `border`,
  `v_space`,
  `h_space`,
  `name`,
  `image_name`,
  `target`,
  `align`,
  `alt_text`,
  `low_scr`,
  `imgurl`,
  `linkurl`,
  `type`,
  @doc_language_id,
  archive_image_id,
  format, 
  rotate_angle, 
  crop_x1, 
  crop_y1, 
  crop_x2, 
  crop_y2, 
  NULL,
  NULL,
  `modified_datetime`,
  `user_id`
FROM `images_history`;  

CREATE TABLE imcms_text_doc_images_cache (
    id varchar(40) NOT NULL,
    resource varchar(255) NOT NULL, 
    cache_type smallint NOT NULL,
    file_size integer NOT NULL, 
    frequency integer NOT NULL, 
    format smallint NOT NULL,
    rotate_angle smallint NOT NULL, 
    width integer NOT NULL, 
    height integer NOT NULL, 
    crop_x1 integer NOT NULL, 
    crop_y1 integer NOT NULL, 
    crop_x2 integer NOT NULL, 
    crop_y2 integer NOT NULL, 
    created_dt timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP, 
    
    CONSTRAINT imcms_text_doc_images_cache_pk PRIMARY KEY (id)
) ENGINE='InnoDB' DEFAULT CHARSET='utf8';

INSERT INTO imcms_text_doc_images_cache (
    id, 
    resource, 
    cache_type, 
    file_size, 
    frequency, 
    format, 
    rotate_angle, 
    width, 
    height, 
    crop_x1, 
    crop_y1, 
    crop_x2, 
    crop_y2, 
    created_dt
) SELECT 
    id, 
    resource, 
    cache_type, 
    file_size, 
    frequency, 
    format, 
    rotate_angle, 
    width, 
    height, 
    crop_x1, 
    crop_y1, 
    crop_x2, 
    crop_y2, 
    created_dt
FROM images_cache WHERE meta_id <= 0;

DROP TABLE images_cache;

--
-- Includes table
--
CREATE TABLE __includes (
  id int AUTO_INCREMENT PRIMARY KEY,
  meta_id int NULL,
  include_id int NOT NULL,
  included_meta_id int NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO __includes (meta_id, include_id, included_meta_id)
SELECT meta_id, include_id, included_meta_id FROM includes;

DROP TABLE includes;
RENAME TABLE __includes TO includes;

ALTER TABLE includes
  ADD CONSTRAINT uk__includes__meta_id__include_id UNIQUE KEY (meta_id, include_id),
  ADD CONSTRAINT fk__includes__meta FOREIGN KEY (meta_id) REFERENCES meta (meta_id) ON DELETE CASCADE,
  ADD CONSTRAINT fk__includes__included_document FOREIGN KEY (meta_id) REFERENCES meta (meta_id);


-- archive xxx - new in RB 4

-- category_roles ADDED ???
-- category_types Y 4.11 is_image_archive
-- childs Y 5.x added id, meta_id renamed to doc_id - revert.
-- childs_history NO, but ??? no track info
-- classification  deleted
-- doc_permission_sets  added id
-- doc_permission_sets_ex added id
-- fileupload_docs - added id, doc_version_no
-- frameset_docs added doc_version_no, ADD id???
-- image_categories new  in RB4
-- imcms languages - new in trunk
-- images : new in trunk id, doc_version_no, language_id, content_loop_no, content_no
-- images_history -//-
-- includes id
-- menus, id, doc_version_no
-- meta: headline, text, image - gone;
-- meta_classification - DEL
-- new_doc_permission_sets ADDED id - remove?
-- new_doc_permission_sets_ex ADDED id - remove?
-- text_docs ADDED id - remove?
-- url_docs - doc_version_no


--
-- Delete deprecated
--
DROP TABLE browser_docs;
DROP TABLE browsers;

DELETE FROM meta WHERE doc_type = 6;
DELETE FROM doc_types WHERE doc_type = 6;
DELETE FROM doc_permissions WHERE doc_type NOT IN (2,5,7,8);

CREATE TABLE archive_category_roles (
    category_id integer NOT NULL, 
    role_id integer NOT NULL, 
    created_dt timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP, 
    CONSTRAINT archive_category_roles_pk PRIMARY KEY (category_id, role_id), 
    CONSTRAINT archive_category_roles_category_id_fk FOREIGN KEY (category_id) REFERENCES categories(category_id), 
    CONSTRAINT archive_category_roles_role_id_fk FOREIGN KEY (role_id) REFERENCES roles(role_id)
) ENGINE='InnoDB' DEFAULT CHARACTER SET='utf8';

CREATE TABLE archive_image_categories (
    image_id bigint NOT NULL, 
    category_id integer NOT NULL, 
    created_dt timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP, 
    CONSTRAINT archive_image_categories_pk PRIMARY KEY (image_id, category_id), 
    CONSTRAINT archive_image_categories_image_id_fk FOREIGN KEY (image_id) REFERENCES archive_images(id), 
    CONSTRAINT archive_image_categories_category_id_fk FOREIGN KEY (category_id) REFERENCES categories(category_id)
) ENGINE='InnoDB' DEFAULT CHARACTER SET='utf8';

INSERT INTO archive_category_roles SELECT * FROM category_roles;
INSERT INTO archive_image_categories SELECT * FROM image_categories;

DROP TABLE category_roles;
DROP TABLE image_categories;

UPDATE archive_images SET format = 
    CASE WHEN format >= 0 AND format <= 3 THEN format + 1
         WHEN format >= 6 AND format <= 10 THEN format - 1
         WHEN format = 4 THEN 10 
         WHEN format = 5 THEN 11
         ELSE format
    END;

ALTER TABLE archive_images MODIFY COLUMN format smallint  NOT NULL COMMENT '1-BMP, 2-GIF, 3-JPEG, 4-PNG, 5-PSD, 6-SVG, 7-TIFF, 8-XCF, 9-PICT, 10-PDF, 11-PS';

-- Drop old tables
-- DELETE FROM `meta_classification`;
-- DELETE FROM `classification`;
--
-- DROP TABLE `meta_classification`;
-- DROP TABLE `classification`;
--
-- -- Drop renamed tables
-- DROP TABLE `frameset_docs`;
--
-- DROP TABLE `childs`;
-- DROP TABLE `childs_history`;
--
-- DROP TABLE `menus`;
-- DROP TABLE `menus_history`;
--
-- DROP TABLE `texts`;
-- DROP TABLE `texts_history`;
--
--
-- DROP TABLE `images`;
-- DROP TABLE `images_history`;


--
-- Update schema version
--
UPDATE database_version
SET
  major = @schema_version__major_new,
  minor = @schema_version__minor_new;
