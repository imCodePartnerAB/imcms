--
-- Table users
--
CREATE TABLE users (
  user_id int NOT NULL auto_increment,
  login_name varchar(128) NOT NULL,
  login_password varchar(15) NOT NULL,
  first_name varchar(64) NOT NULL,
  last_name varchar(64) NOT NULL,
  title varchar(64) NOT NULL,
  company varchar(64) NOT NULL,
  address varchar(128) NOT NULL,
  city varchar(64) NOT NULL,
  zip varchar(64) NOT NULL,
  country varchar(64) NOT NULL,
  county_council varchar(128) NOT NULL,
  email varchar(128) NOT NULL,
  external int NOT NULL,
  active int NOT NULL default '1',
  create_date datetime NOT NULL,
  language varchar(3) NOT NULL,
  session_id varchar(128) default NULL,
  CONSTRAINT pk__users PRIMARY KEY (user_id),
  CONSTRAINT uk__users__login_name UNIQUE KEY (login_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table meta
--

CREATE TABLE meta (
  meta_id int NOT NULL auto_increment,
  doc_type int NOT NULL,
  owner_id int NOT NULL,
  permissions int NOT NULL,
  shared int NOT NULL,
  show_meta int NOT NULL,
  lang_prefix varchar(3) NOT NULL,
  date_created datetime NOT NULL,
  date_modified datetime NOT NULL,
  disable_search int NOT NULL,
  target varchar(10) NOT NULL,
  activate int NOT NULL,
  archived_datetime datetime default NULL,
  publisher_id int default NULL,
  status int NOT NULL,
  publication_start_datetime datetime default NULL,
  publication_end_datetime datetime default NULL,
  missing_i18n_show_rule varchar(32) default 'DO_NOT_SHOW',
  CONSTRAINT pk__meta PRIMARY KEY (meta_id),
  CONSTRAINT fk__meta__owner_id__users FOREIGN KEY (owner_id) REFERENCES users (user_id),
  CONSTRAINT fk__meta__publisher_id__users FOREIGN KEY (publisher_id) REFERENCES users (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Meta version table
--
CREATE TABLE meta_version (
  id int NOT NULL auto_increment PRIMARY KEY,
  meta_id int NOT NULL,
  version int NOT NULL,
  version_tag varchar(12) NOT NULL,
  user_id int NULL,
  created_dt datetime NOT NULL,
  CONSTRAINT uk__meta_version__meta_id__version UNIQUE KEY (meta_id, version),
  CONSTRAINT fk__meta_version__user FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE SET NULL,
  CONSTRAINT fk__meta_version__meta FOREIGN KEY (meta_id) REFERENCES meta (meta_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table category_types
--

CREATE TABLE category_types (
  category_type_id int NOT NULL auto_increment,
  name varchar(128) NOT NULL,
  max_choices int NOT NULL default '0',
  inherited tinyint(1) NOT NULL,
  CONSTRAINT pk__category_types PRIMARY KEY  (category_type_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table categories
--

CREATE TABLE categories (
  category_id int NOT NULL auto_increment,
  category_type_id int NOT NULL,
  name varchar(128) NOT NULL,
  description varchar(500) default NULL,
  image varchar(255) NOT NULL,
  CONSTRAINT pk__categories PRIMARY KEY (category_id),
  CONSTRAINT fk__categories__category_types FOREIGN KEY (category_type_id) REFERENCES category_types (category_type_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


--
-- Table database_version
--

CREATE TABLE database_version (
  major int NOT NULL,
  minor int NOT NULL,
  CONSTRAINT pk__database_version PRIMARY KEY (major, minor)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table doc_permissions
--

CREATE TABLE doc_permissions (
  permission_id int NOT NULL,
  doc_type int NOT NULL,
  lang_prefix varchar(3) NOT NULL,
  description varchar(50) NOT NULL,
  CONSTRAINT pk__doc_permissions PRIMARY KEY  (permission_id, doc_type,lang_prefix)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table doc_types
--

CREATE TABLE doc_types (
  doc_type int NOT NULL,
  lang_prefix varchar(3) NOT NULL default 'swe',
  type varchar(50) default NULL,
  CONSTRAINT pk__doc_types PRIMARY KEY  (doc_type,lang_prefix)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table document_categories
--

CREATE TABLE document_categories (
  meta_id int NOT NULL,
  category_id int NOT NULL,
  CONSTRAINT pk__document_categories PRIMARY KEY  (meta_id, category_id),
  CONSTRAINT fk__document_categories__categories FOREIGN KEY (category_id) REFERENCES categories (category_id),
  CONSTRAINT fk__document_categories__meta FOREIGN KEY (meta_id) REFERENCES meta (meta_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table document_properties
--

CREATE TABLE document_properties (
  id int NOT NULL auto_increment,
  meta_id int NOT NULL,
  key_name varchar(255) NOT NULL,
  value varchar(255) NOT NULL,
  CONSTRAINT pk__document_properties PRIMARY KEY  (id),
  CONSTRAINT uk__document_properties__meta_id__key_name UNIQUE KEY (meta_id,key_name),
  CONSTRAINT fk__document_properties__meta FOREIGN KEY (meta_id) REFERENCES meta (meta_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table document_search_log
--

CREATE TABLE document_search_log (
  datetime datetime NOT NULL,
  term varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table fileupload_docs
--

CREATE TABLE fileupload_docs (
  meta_id int NOT NULL,
  variant_name varchar(100) NOT NULL,
  filename varchar(255) NOT NULL,
  mime varchar(50) NOT NULL,
  created_as_image int NOT NULL,
  default_variant tinyint(1) NOT NULL default '0',
  CONSTRAINT pk__fileupload_docs PRIMARY KEY (meta_id,variant_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table frameset_docs
--

CREATE TABLE frameset_docs (
  meta_id int NOT NULL,
  frame_set longtext,
  CONSTRAINT pk__frameset_docs PRIMARY KEY  (meta_id),
  CONSTRAINT fk__frameset_docs__meta FOREIGN KEY (meta_id) REFERENCES meta (meta_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table i18n_languages
--

CREATE TABLE i18n_languages (
  language_id smallint NOT NULL auto_increment,
  code varchar(3) NOT NULL COMMENT 'Language code',
  name varchar(128) NOT NULL,
  native_name varchar(128) default NULL,
  is_default boolean NOT NULL default false COMMENT 'Default language flag for application. Only one language can be set as default.',
  is_enabled boolean NOT NULL default true COMMENT 'Language status for application. Reserved for future use.',
  CONSTRAINT pk__i18n_languages PRIMARY KEY (language_id)

) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table keywords
--

CREATE TABLE keywords (
  keyword_id int NOT NULL auto_increment,
  meta_id int default NULL,          
  language_id smallint NULL, 
  value varchar(128) NOT NULL,
  CONSTRAINT pk__keywords PRIMARY KEY (keyword_id),
  CONSTRAINT fk__keywords__meta FOREIGN KEY(meta_id) REFERENCES meta (meta_id) ON DELETE CASCADE,
  CONSTRAINT fk__keywords__i18n_languages FOREIGN KEY(language_id) REFERENCES i18n_languages (language_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table i18n_meta
--

CREATE TABLE i18n_meta (
  i18n_meta_id int NOT NULL auto_increment,
  language_id smallint(6) default NULL,
  meta_id int default NULL,
  meta_enabled tinyint(1) NOT NULL default '0',
  meta_headline varchar(255) default NULL,
  meta_text varchar(1000) default NULL,
  meta_image varchar(255) default NULL,

  CONSTRAINT pk__i18n_meta PRIMARY KEY (i18n_meta_id),
  CONSTRAINT uk__i18n_meta__meta_id__language_id UNIQUE KEY (meta_id, language_id),
  CONSTRAINT fk__i18n_meta__meta FOREIGN KEY (meta_id) REFERENCES meta (meta_id) ON DELETE CASCADE,
  CONSTRAINT fk__i18n_meta__language FOREIGN KEY (language_id) REFERENCES i18n_languages (language_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table images
--

CREATE TABLE images (
  image_id int NOT NULL auto_increment,
  meta_id int NULL,
  meta_version INT NOT NULL,
  width int NOT NULL,
  height int NOT NULL,
  border int NOT NULL,
  v_space int NOT NULL,
  h_space int NOT NULL,
  name int NOT NULL,
  image_name varchar(40) NOT NULL default '',
  target varchar(15) NOT NULL,
  align varchar(15) NOT NULL,
  alt_text varchar(255) NOT NULL,
  low_scr varchar(255) NOT NULL,
  imgurl varchar(255) NOT NULL,
  linkurl varchar(255) NOT NULL,
  type int NOT NULL,
  language_id smallint(6) NOT NULL,

  CONSTRAINT pk__images PRIMARY KEY  (image_id),
  CONSTRAINT fk__images__meta FOREIGN KEY  (meta_id) REFERENCES meta (meta_id) ON DELETE CASCADE,
  CONSTRAINT fk__images__i18n_languages FOREIGN KEY (language_id) REFERENCES i18n_languages (language_id),
  CONSTRAINT uk__images__meta_id__meta_version__name__language_id UNIQUE KEY (meta_id, meta_version, name, language_id),
  CONSTRAINT fk__images__meta_id__meta_version FOREIGN KEY (meta_id, meta_version) REFERENCES meta_version (meta_id, version) ON DELETE CASCADE

) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE images_history (
  id int auto_increment PRIMARY KEY,
  meta_id int NOT NULL,
  width int NOT NULL,
  height int NOT NULL,
  border int NOT NULL,
  v_space int NOT NULL,
  h_space int NOT NULL,
  name int NOT NULL,
  image_name varchar(40) NOT NULL,
  target varchar(15) NOT NULL,
  align varchar(15) NOT NULL,
  alt_text varchar(255) NOT NULL,
  low_scr varchar(255) NOT NULL,
  imgurl varchar(255) NOT NULL,
  linkurl varchar(255) NOT NULL,
  type int NOT NULL,
  modified_datetime datetime NOT NULL,
  user_id int NULL,
  
  CONSTRAINT fk__images_history__users FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE SET NULL,
  CONSTRAINT fk__images_history__meta FOREIGN KEY (meta_id) REFERENCES meta (meta_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table includes
--

CREATE TABLE includes (
  id int auto_increment,
  meta_id int NOT NULL,
  include_id int NOT NULL,
  included_meta_id int NOT NULL,

  CONSTRAINT pk__includes PRIMARY KEY (id),
  CONSTRAINT uk__includes__meta_id__include_id UNIQUE KEY (meta_id, include_id),
  CONSTRAINT fk__includes__meta_id__meta FOREIGN KEY (meta_id) REFERENCES meta (meta_id) ON DELETE CASCADE,
  CONSTRAINT fk__includes__included_meta_id__meta FOREIGN KEY (included_meta_id) REFERENCES meta (meta_id)
  
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table ip_accesses
--

CREATE TABLE ip_accesses (
  ip_access_id int NOT NULL auto_increment,
  user_id int NOT NULL,
  ip_start decimal(18,0) NOT NULL,
  ip_end decimal(18,0) NOT NULL,
  CONSTRAINT pk__ip_accesses PRIMARY KEY (ip_access_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table lang_prefixes
--

CREATE TABLE lang_prefixes (
  lang_id int NOT NULL,
  lang_prefix char(3) default NULL,
  CONSTRAINT pk__lang_prefixes PRIMARY KEY  (lang_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table languages
--

CREATE TABLE languages (
  lang_prefix varchar(3) NOT NULL,
  user_prefix varchar(3) NOT NULL,
  language varchar(30) default NULL,
  CONSTRAINT pk__languages PRIMARY KEY  (lang_prefix,user_prefix)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table menus
--

CREATE TABLE menus (
  menu_id int NOT NULL auto_increment,
  meta_id int NOT NULL,
  menu_index int NOT NULL,
  sort_order int NOT NULL,
  CONSTRAINT pk__menus PRIMARY KEY  (menu_id),
  CONSTRAINT uk__menus__meta_id__menu_index UNIQUE KEY (meta_id, menu_index),
  CONSTRAINT fk__menus__meta FOREIGN KEY (meta_id) REFERENCES meta (meta_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table childs
--
CREATE TABLE childs (
  id int auto_increment, 
  to_meta_id int NOT NULL,
  manual_sort_order int NOT NULL,
  tree_sort_index varchar(64) NOT NULL,
  menu_id int NOT NULL,

  CONSTRAINT pk__childs PRIMARY KEY (id),
  CONSTRAINT uk__childs__menu_id__to_meta_id UNIQUE KEY (menu_id, to_meta_id),
  CONSTRAINT fk__childs__menus FOREIGN KEY (menu_id) REFERENCES menus (menu_id) ON DELETE CASCADE,
  CONSTRAINT fk__childs__meta FOREIGN KEY (to_meta_id) REFERENCES meta (meta_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table mime_types
--

CREATE TABLE mime_types (
  mime_id int NOT NULL,
  mime_name varchar(50) NOT NULL,
  mime varchar(50) NOT NULL,
  lang_prefix varchar(3) NOT NULL default 'swe',
  CONSTRAINT pk__mime_types PRIMARY KEY (mime_id, lang_prefix)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table permission_sets
--

CREATE TABLE permission_sets (
  set_id int NOT NULL,
  description varchar(30) NOT NULL,
  CONSTRAINT pk__permission_sets PRIMARY KEY (set_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table permissions
--

CREATE TABLE permissions (
  permission_id smallint(6) NOT NULL,
  lang_prefix varchar(3) NOT NULL default 'swe',
  description varchar(50) NOT NULL,
  CONSTRAINT pk__permissions PRIMARY KEY  (permission_id,lang_prefix)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table doc_permission_sets
--

CREATE TABLE doc_permission_sets (
  id int auto_increment,
  meta_id int NOT NULL,
  set_id int NOT NULL,
  permission_id int NOT NULL,
  CONSTRAINT pk__doc_permission_sets PRIMARY KEY (id),
  CONSTRAINT uk__doc_permission_sets__meta_id__set_id UNIQUE KEY (meta_id,set_id),
  CONSTRAINT fk__doc_permission_sets__meta FOREIGN KEY (meta_id) REFERENCES meta (meta_id) ON DELETE CASCADE,
  CONSTRAINT fk__doc_permission_sets__permission_sets FOREIGN KEY (set_id) REFERENCES permission_sets (set_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table new_doc_permission_sets
--

CREATE TABLE new_doc_permission_sets (
  id int auto_increment, 
  meta_id int NOT NULL,
  set_id int NOT NULL,
  permission_id int NOT NULL,

  CONSTRAINT pk__new_doc_permission_sets PRIMARY KEY (id),
  CONSTRAINT uk__new_doc_permission_sets__meta_id__set_id UNIQUE INDEX (meta_id, set_id),
  CONSTRAINT fk__new_doc_permission_sets__meta FOREIGN KEY (meta_id) REFERENCES meta (meta_id) ON DELETE CASCADE,
  CONSTRAINT fk__new_doc_permission_sets__permission_sets FOREIGN KEY (set_id) REFERENCES permission_sets (set_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table new_doc_permission_sets_ex
--

CREATE TABLE new_doc_permission_sets_ex (
  id int auto_increment,
  meta_id int NOT NULL,
  set_id int NOT NULL,
  permission_id int NOT NULL,
  permission_data int NOT NULL,

  CONSTRAINT pk__new_doc_permission_sets_ex PRIMARY KEY (id),
  CONSTRAINT uk__new_doc_permission_sets_ex__1 UNIQUE KEY (meta_id, set_id, permission_id, permission_data),
  CONSTRAINT fk__new_doc_permission_sets_ex__meta FOREIGN KEY (meta_id) REFERENCES meta (meta_id) ON DELETE CASCADE,
  CONSTRAINT fk__new_doc_permission_sets_ex__permission_sets FOREIGN KEY (set_id) REFERENCES permission_sets (set_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table doc_permission_sets_ex
--

CREATE TABLE doc_permission_sets_ex (
  id int auto_increment,
  meta_id int NOT NULL,
  set_id int NOT NULL,
  permission_id int NOT NULL,
  permission_data int NOT NULL,

  CONSTRAINT pk__doc_permission_sets_ex PRIMARY KEY (id),
  CONSTRAINT uk__doc_permission_sets_ex__1 UNIQUE KEY (meta_id, set_id, permission_id, permission_data),
  CONSTRAINT fk__doc_permission_sets_ex__meta FOREIGN KEY (meta_id) REFERENCES meta (meta_id) ON DELETE CASCADE,
  CONSTRAINT fk__doc_permission_sets_ex__permission_sets FOREIGN KEY (set_id) REFERENCES permission_sets (set_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table phones
--

CREATE TABLE phones (
  phone_id int NOT NULL auto_increment,
  number varchar(25) NOT NULL,
  user_id int NOT NULL,
  phonetype_id int NOT NULL default '0',
  CONSTRAINT pk__phones PRIMARY KEY  (phone_id, user_id),
  CONSTRAINT fk__phones__users FOREIGN KEY (user_id) REFERENCES users (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table phonetypes
--

CREATE TABLE phonetypes (
  phonetype_id int NOT NULL,
  typename varchar(12) NOT NULL,
  lang_id int NOT NULL,
  CONSTRAINT pk__phonetypes PRIMARY KEY (phonetype_id,lang_id),
  CONSTRAINT fk__phonetypes__lang_prefixes FOREIGN KEY (lang_id) REFERENCES lang_prefixes (lang_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table profiles
--

CREATE TABLE profiles (
  profile_id int NOT NULL auto_increment,
  name varchar(255) NOT NULL,
  document_name varchar(255) NOT NULL,
  CONSTRAINT pk__profiles PRIMARY KEY  (profile_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table roles
--

CREATE TABLE roles (
  role_id int NOT NULL auto_increment,
  role_name varchar(60) NOT NULL,
  permissions int NOT NULL default '0',
  admin_role int NOT NULL default '0',
  CONSTRAINT pk__roles PRIMARY KEY  (role_id),
  CONSTRAINT uk__roles__role_name UNIQUE KEY  (role_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table roles_rights
--

CREATE TABLE roles_rights (
  role_id int NOT NULL,
  meta_id int NOT NULL,
  set_id smallint(6) NOT NULL,
  CONSTRAINT pk__roles_rights PRIMARY KEY (role_id,meta_id),
  CONSTRAINT fk__roles_rights__meta FOREIGN KEY (meta_id) REFERENCES meta (meta_id) ON DELETE CASCADE,
  CONSTRAINT fk__roles_rights__roles FOREIGN KEY (role_id) REFERENCES roles (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table stats
--

CREATE TABLE stats (
  name varchar(120) NOT NULL,
  num int NOT NULL,
  CONSTRAINT pk__stats PRIMARY KEY (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table sys_types
--

CREATE TABLE sys_types (
  type_id smallint(6) NOT NULL,
  name varchar(50) default NULL,
  CONSTRAINT pk__sys_types PRIMARY KEY  (type_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table sys_data
--

CREATE TABLE sys_data (
  sys_id smallint(6) NOT NULL auto_increment,
  type_id smallint(6) NOT NULL,
  value varchar(1000) default NULL,
  CONSTRAINT pk__sys_data PRIMARY KEY (sys_id,type_id),
  CONSTRAINT pk__sys_data__sys_types FOREIGN KEY (type_id) REFERENCES sys_types (type_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table templategroups
--

CREATE TABLE templategroups (
  group_id int NOT NULL auto_increment,
  group_name varchar(50) NOT NULL,
  CONSTRAINT pk__templategroups PRIMARY KEY  (group_id),
  CONSTRAINT uk_templategroups__group_name UNIQUE KEY (group_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table templates_cref
--

CREATE TABLE templates_cref (
  group_id int NOT NULL,
  template_name varchar(255) NOT NULL,
  CONSTRAINT pk__templates_cref PRIMARY KEY  (group_id,template_name),
  CONSTRAINT fk__templates_cref__templategroups FOREIGN KEY (group_id) REFERENCES templategroups (group_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table text_docs
--

CREATE TABLE text_docs (
  id int auto_increment,
  meta_id int NOT NULL,
  template_name varchar(255) NOT NULL,
  group_id int NOT NULL default '1',
  default_template_1 varchar(255) default NULL,
  default_template_2 varchar(255) default NULL,
  default_template varchar(255) default NULL,
  CONSTRAINT pk__text_docs PRIMARY KEY  (id),
  CONSTRAINT uk__text_docs__meta_id UNIQUE KEY (meta_id),
  CONSTRAINT fk__text_docs__meta FOREIGN KEY (meta_id) REFERENCES meta (meta_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table texts
--
    
CREATE TABLE texts (
  counter int NOT NULL auto_increment,
  meta_id int default NULL,
  meta_version INT NOT NULL,
  name int NOT NULL,
  text longtext NOT NULL,
  type int default NULL,
  language_id smallint(6) NOT NULL,
  CONSTRAINT pk__texts PRIMARY KEY  (counter),
  CONSTRAINT fk__texts__i18n_languages FOREIGN KEY (language_id) REFERENCES i18n_languages (language_id),
  CONSTRAINT fk__texts__meta FOREIGN KEY (meta_id) REFERENCES meta (meta_id) ON DELETE CASCADE,
  CONSTRAINT uk__texts__meta_id__meta_version__name__language_id UNIQUE KEY (meta_id, meta_version, name, language_id),
  CONSTRAINT fk__texts__meta_id__meta_version FOREIGN KEY (meta_id, meta_version) REFERENCES meta_version (meta_id, version) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table texts_history
--

CREATE TABLE texts_history (
  counter int NOT NULL auto_increment,
  meta_id int default NULL,
  meta_version INT NOT NULL,
  name int NOT NULL,
  text longtext NOT NULL,
  type int default NULL,
  modified_datetime datetime NOT NULL,
  user_id int default NULL,
  language_id smallint(6) NOT NULL,
  CONSTRAINT pk__texts_history PRIMARY KEY (counter),

  CONSTRAINT fk__texts_history__i18n_languages FOREIGN KEY  (language_id) REFERENCES i18n_languages (language_id),
  CONSTRAINT fk__texts_history__meta FOREIGN KEY  (meta_id) REFERENCES meta (meta_id) ON DELETE CASCADE,
  CONSTRAINT fk__texts_history__users FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE SET NULL

) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table url_docs
--

CREATE TABLE url_docs (
  meta_id int NOT NULL,
  frame_name varchar(80) NOT NULL,
  target varchar(15) NOT NULL,
  url_ref varchar(255) NOT NULL,
  url_txt varchar(255) NOT NULL,
  lang_prefix varchar(3) NOT NULL,
  CONSTRAINT pk__url_docs PRIMARY KEY  (meta_id,lang_prefix),
  CONSTRAINT fk__url_docs__meta FOREIGN KEY (meta_id) REFERENCES meta (meta_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table user_rights
--

CREATE TABLE user_rights (
  user_id int NOT NULL,
  meta_id int NOT NULL,
  permission_id smallint(6) NOT NULL,
  CONSTRAINT pk__user_rights PRIMARY KEY  (user_id,meta_id,permission_id),
  CONSTRAINT fk__user_rights__meta FOREIGN KEY (meta_id) REFERENCES meta (meta_id) ON DELETE CASCADE,
  CONSTRAINT fk__user_rights__users FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table user_roles_crossref
--

CREATE TABLE user_roles_crossref (
  user_id int NOT NULL,
  role_id int NOT NULL,
  CONSTRAINT pk__user_roles_crossref PRIMARY KEY  (user_id,role_id),
  CONSTRAINT fk__user_roles_crossref__roles FOREIGN KEY (role_id) REFERENCES roles (role_id),
  CONSTRAINT fk__user_roles_crossref__users FOREIGN KEY (user_id) REFERENCES users (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table useradmin_role_crossref
--

CREATE TABLE useradmin_role_crossref (
  user_id int NOT NULL,
  role_id int NOT NULL,
  CONSTRAINT pk__useradmin_role_crossref PRIMARY KEY  (user_id,role_id),
  CONSTRAINT fk__useradmin_role_crossref__users FOREIGN KEY (user_id) REFERENCES users (user_id),
  CONSTRAINT fk__useradmin_role_crossref__roles FOREIGN KEY (role_id) REFERENCES roles (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


--
-- Table menus_history
--

CREATE TABLE menus_history (
  menu_id int NOT NULL,
  meta_id int NOT NULL,
  menu_index int NOT NULL,
  sort_order int NOT NULL,
  modified_datetime datetime NOT NULL,
  user_id int NOT NULL,
  PRIMARY KEY  (menu_id),
  KEY menus_history_FK_meta_id_meta (meta_id),
  CONSTRAINT menus_history_FK_meta_id_meta FOREIGN KEY (meta_id) REFERENCES meta (meta_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table childs_history
--

CREATE TABLE childs_history (
  menu_id int NOT NULL,
  to_meta_id int NOT NULL,
  manual_sort_order int NOT NULL,
  tree_sort_index varchar(64) NOT NULL,
  PRIMARY KEY  (menu_id,to_meta_id),
  KEY childs_history_FK_to_meta_id_meta (to_meta_id),
  CONSTRAINT childs_history_FK_menu_id_menus_history FOREIGN KEY (menu_id) REFERENCES menus_history (menu_id),
  CONSTRAINT childs_history_FK_to_meta_id_meta FOREIGN KEY (to_meta_id) REFERENCES meta (meta_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



--
-- Tables for content loop data
--

CREATE TABLE text_doc_content_loops (
  id int auto_increment PRIMARY KEY,
  meta_id int NOT NULL,
  meta_version int NOT NULL,
  loop_index int NOT NULL,
  base_index int NOT NULL,
  CONSTRAINT fk__text_doc_content_loops__meta_id__meta_version FOREIGN KEY (meta_id, meta_version) REFERENCES meta_version (meta_id, version) ON DELETE CASCADE,
  CONSTRAINT uk__text_doc_content_loops__meta_id__meta_version__loop_index UNIQUE KEY (meta_id, meta_version, loop_index)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


--
-- Tables for contents
--

CREATE TABLE text_doc_contents (
  id int auto_increment PRIMARY KEY,
  loop_id int,
  sequence_index int NOT NULL,
  order_index int NOT NULL,

  CONSTRAINT uk__loop_id__sequence_index UNIQUE KEY (loop_id, sequence_index),
  CONSTRAINT uk__loop_id__order_index UNIQUE KEY (loop_id, order_index),
  CONSTRAINT fk__text_doc_contents__text_doc_content_loops FOREIGN KEY (loop_id) REFERENCES text_doc_content_loops (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;