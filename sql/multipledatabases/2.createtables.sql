CREATE TABLE meta (
	meta_id INT NOT NULL ,
	description VARCHAR (80) NOT NULL ,
	doc_type INT NOT NULL ,
	meta_headline VARCHAR (255) NOT NULL ,
	meta_text VARCHAR (1000) NOT NULL ,
	meta_image VARCHAR (255) NOT NULL ,
	owner_id INT NOT NULL ,
	permissions INT NOT NULL ,
	shared INT NOT NULL ,
	expand INT NOT NULL ,
	show_meta INT NOT NULL ,
	help_text_id INT NOT NULL ,
	archive INT NOT NULL ,
	status_id INT NOT NULL ,
	lang_prefix VARCHAR (3) NOT NULL ,
	classification VARCHAR (200) NOT NULL ,
	date_created TIMESTAMP NOT NULL ,
	date_modified TIMESTAMP NOT NULL ,
	sort_position INT NOT NULL ,
	menu_position INT NOT NULL ,
	disable_search INT NOT NULL ,
	target VARCHAR (10) NOT NULL ,
	frame_name VARCHAR (20) NOT NULL ,
	activate INT NOT NULL ,
	activated_datetime TIMESTAMP,
	archived_datetime TIMESTAMP,
	PRIMARY KEY (meta_id)
);

CREATE TABLE browsers (
	browser_id INT NOT NULL ,
	name VARCHAR (50) NOT NULL ,
	user_agent VARCHAR (50) NOT NULL ,
	browser_value SMALLINT NOT NULL ,
	PRIMARY KEY (browser_id)
);

CREATE TABLE childs (
	meta_id INT NOT NULL ,
	to_meta_id INT NOT NULL ,
	menu_sort INT NOT NULL ,
	manual_sort_order INT NOT NULL ,
	PRIMARY KEY (meta_id, to_meta_id, menu_sort) ,
	FOREIGN KEY (meta_id) REFERENCES meta (meta_id)
);

CREATE TABLE classification (
	class_id INT NOT NULL ,
	code VARCHAR (30) NOT NULL ,
	PRIMARY KEY (class_id)
);

CREATE TABLE doc_permissions (
	permission_id INT NOT NULL ,
	doc_type INT NOT NULL ,
	lang_prefix VARCHAR (3) NOT NULL ,
	description VARCHAR (50) NOT NULL ,
	PRIMARY KEY (permission_id, doc_type, lang_prefix)
);

CREATE TABLE doc_types (
	doc_type INT NOT NULL ,
	lang_prefix VARCHAR (3) NOT NULL ,
	type VARCHAR (50),
	PRIMARY KEY (doc_type, lang_prefix)
);

CREATE TABLE fileupload_docs (
	meta_id INT NOT NULL ,
	filename VARCHAR (50) NOT NULL ,
	mime VARCHAR (50) NOT NULL ,
	PRIMARY KEY (meta_id)
);

CREATE TABLE ip_accesses (
	ip_access_id INT NOT NULL ,
	user_id INT NOT NULL ,
	ip_start DECIMAL(18, 0) NOT NULL ,
	ip_end DECIMAL(18, 0) NOT NULL
);

CREATE TABLE lang_prefixes (
	lang_id INT NOT NULL ,
	lang_prefix VARCHAR (3),
	PRIMARY KEY (lang_id)
);

CREATE TABLE languages (
	lang_prefix VARCHAR (3) NOT NULL ,
	user_prefix VARCHAR (3) NOT NULL ,
	language VARCHAR (30),
	PRIMARY KEY (lang_prefix, user_prefix)
);

CREATE TABLE main_log (
	log_datetime TIMESTAMP ,
	event VARCHAR (255)
);

CREATE TABLE mime_types (
	mime_id INT NOT NULL ,
	mime_name VARCHAR (50) NOT NULL ,
	mime VARCHAR (50) NOT NULL ,
	lang_prefix VARCHAR (3) NOT NULL ,
	PRIMARY KEY (mime_id,lang_prefix)
);

CREATE TABLE permission_sets (
	set_id INT NOT NULL ,
	description VARCHAR (30) NOT NULL ,
	PRIMARY KEY (set_id)
);

CREATE TABLE permissions (
	permission_id SMALLINT NOT NULL ,
	lang_prefix VARCHAR (3) NOT NULL ,
	description VARCHAR (50) NOT NULL ,
	PRIMARY KEY (permission_id,lang_prefix)
);

CREATE TABLE readrunner_user_data (
	user_id INT NOT NULL ,
	uses INT ,
	max_uses INT ,
	max_uses_warning_threshold INT ,
	expiry_date TIMESTAMP ,
	expiry_date_warning_threshold INT ,
	expiry_date_warning_sent INT NOT NULL,
	PRIMARY KEY (user_id)
);

CREATE TABLE roles (
	role_id INT NOT NULL ,
	role_name VARCHAR (25) NOT NULL ,
	permissions INT NOT NULL ,
	admin_role INT NOT NULL ,
	PRIMARY KEY (role_id)
);

CREATE TABLE roles_permissions (
	permission_id INT NOT NULL ,
	lang_prefix VARCHAR (3) NOT NULL ,
	description VARCHAR (40) NOT NULL ,
	PRIMARY KEY (permission_id,lang_prefix)
);

CREATE TABLE sections (
	section_id INT NOT NULL ,
	section_name VARCHAR (50) NOT NULL ,
	PRIMARY KEY (section_id)
);

CREATE TABLE sort_by (
	sort_by_id INT NOT NULL ,
	sort_by_type VARCHAR (30) NOT NULL ,
	PRIMARY KEY (sort_by_id)
);

CREATE TABLE stats (
	name VARCHAR (120) NOT NULL ,
	num INT NOT NULL ,
	PRIMARY KEY (name)
);

CREATE TABLE sys_types (
	type_id SMALLINT NOT NULL ,
	name VARCHAR (50) ,
	PRIMARY KEY (type_id)
);

CREATE TABLE templategroups (
	group_id INT NOT NULL ,
	group_name VARCHAR (50) NOT NULL ,
	PRIMARY KEY (group_id)
);

CREATE TABLE templates (
	template_id INT NOT NULL ,
	template_name VARCHAR (80) NOT NULL ,
	simple_name VARCHAR (80) NOT NULL ,
	lang_prefix VARCHAR (3) NOT NULL ,
	no_of_txt INT ,
	no_of_img INT ,
	no_of_url INT ,
	PRIMARY KEY (template_id)
);

CREATE TABLE track_log (
	user_id SMALLINT ,
	log_datetime TIMESTAMP ,
	from_meta_id INT ,
	to_meta_id INT ,
	cookie_id INT
);

CREATE TABLE user_flags (
	user_flag_id INT NOT NULL ,
	name VARCHAR (64) NOT NULL ,
	type INT NOT NULL ,
	description VARCHAR (255) NOT NULL ,
	PRIMARY KEY (user_flag_id)
);

CREATE TABLE user_types (
	user_type INT NOT NULL ,
	type_name VARCHAR (30) ,
	lang_prefix VARCHAR (3) NOT NULL ,
	PRIMARY KEY (user_type,lang_prefix)
);

CREATE TABLE users (
	user_id INT NOT NULL ,
	login_name VARCHAR (50) NOT NULL ,
	login_password VARCHAR (15) NOT NULL ,
	first_name VARCHAR (25) NOT NULL ,
	last_name VARCHAR (30) NOT NULL ,
	title VARCHAR (30) NOT NULL ,
	company VARCHAR (30) NOT NULL ,
	address VARCHAR (40) NOT NULL ,
	city VARCHAR (30) NOT NULL ,
	zip VARCHAR (15) NOT NULL ,
	country VARCHAR (30) NOT NULL ,
	county_council VARCHAR (30) NOT NULL ,
	email VARCHAR (50) NOT NULL ,
	external INT NOT NULL ,
	last_page INT NOT NULL ,
	archive_mode INT NOT NULL ,
	lang_id INT NOT NULL ,
	user_type INT NOT NULL ,
	active INT NOT NULL ,
	create_date TIMESTAMP NOT NULL ,
	PRIMARY KEY (user_id)
);

CREATE TABLE browser_docs (
	meta_id INT NOT NULL ,
	to_meta_id INT NOT NULL ,
	browser_id INT NOT NULL ,
	PRIMARY KEY (meta_id, to_meta_id, browser_id) ,
	FOREIGN KEY (meta_id) REFERENCES meta (meta_id)
);

CREATE TABLE display_name (
	sort_by_id INT NOT NULL ,
	lang_id INT NOT NULL ,
	display_name VARCHAR (30) NOT NULL ,
    PRIMARY KEY (sort_by_id, lang_id) ,
    FOREIGN KEY (lang_id) REFERENCES lang_prefixes (lang_id) ,
    FOREIGN KEY (sort_by_id) REFERENCES sort_by (sort_by_id)
);

CREATE TABLE doc_permission_sets (
	meta_id INT NOT NULL ,
	set_id INT NOT NULL ,
	permission_id INT NOT NULL ,
	PRIMARY KEY (meta_id, set_id) ,
    FOREIGN KEY (set_id) REFERENCES permission_sets (set_id) ,
    FOREIGN KEY (meta_id) REFERENCES meta (meta_id)
);

CREATE TABLE doc_permission_sets_ex (
	meta_id INT NOT NULL ,
	set_id INT NOT NULL ,
	permission_id INT NOT NULL ,
	permission_data INT NOT NULL ,
	PRIMARY KEY (meta_id, set_id, permission_id, permission_data) ,
    FOREIGN KEY (meta_id,set_id) REFERENCES doc_permission_sets (meta_id,set_id) ,
    FOREIGN KEY (set_id) REFERENCES permission_sets (set_id)
);

CREATE TABLE frameset_docs (
	meta_id INT NOT NULL ,
	frame_set VARCHAR(15000) ,
	PRIMARY KEY (meta_id) ,
	FOREIGN KEY (meta_id) REFERENCES meta (meta_id)
);

CREATE TABLE images (
	meta_id INT NOT NULL ,
	width INT NOT NULL ,
	height INT NOT NULL ,
	border INT NOT NULL ,
	v_space INT NOT NULL ,
	h_space INT NOT NULL ,
	name INT NOT NULL ,
	image_name VARCHAR (40) NOT NULL ,
	target VARCHAR (15) NOT NULL ,
	target_name VARCHAR (80) NOT NULL ,
	align VARCHAR (15) NOT NULL ,
	alt_text VARCHAR (255) NOT NULL ,
	low_scr VARCHAR (255) NOT NULL ,
	imgurl VARCHAR (255) NOT NULL ,
	linkurl VARCHAR (255) NOT NULL ,
	PRIMARY KEY (meta_id,name) ,
	FOREIGN KEY (meta_id) REFERENCES meta (meta_id)
);

CREATE TABLE includes (
	meta_id INT NOT NULL ,
	include_id INT NOT NULL ,
	included_meta_id INT NOT NULL ,
    PRIMARY KEY (meta_id, include_id) ,
	FOREIGN KEY (meta_id) REFERENCES meta (meta_id) ,
	FOREIGN KEY (included_meta_id) REFERENCES meta (meta_id)
);

CREATE TABLE meta_classification (
	meta_id INT NOT NULL ,
	class_id INT NOT NULL ,
	PRIMARY KEY (meta_id, class_id) ,
    FOREIGN KEY (meta_id) REFERENCES meta (meta_id) ,
    FOREIGN KEY (class_id) REFERENCES classification (class_id)
);

CREATE TABLE meta_section (
	meta_id INT NOT NULL ,
	section_id INT NOT NULL ,
    PRIMARY KEY (meta_id,section_id) ,
    FOREIGN KEY (meta_id) REFERENCES meta (meta_id) ,
    FOREIGN KEY (section_id) REFERENCES sections (section_id)
);

CREATE TABLE new_doc_permission_sets (
	meta_id INT NOT NULL ,
	set_id INT NOT NULL ,
	permission_id INT NOT NULL ,
	PRIMARY KEY (meta_id,set_id) ,
    FOREIGN KEY (meta_id) REFERENCES meta (meta_id) ,
    FOREIGN KEY (set_id) REFERENCES permission_sets (set_id)
);

CREATE TABLE new_doc_permission_sets_ex (
	meta_id INT NOT NULL ,
	set_id INT NOT NULL ,
	permission_id INT NOT NULL ,
	permission_data INT NOT NULL ,
	PRIMARY KEY (meta_id,set_id,permission_id,permission_data) ,
    FOREIGN KEY (meta_id,set_id) REFERENCES new_doc_permission_sets (meta_id,set_id) ,
    FOREIGN KEY (set_id) REFERENCES permission_sets (set_id)
);

CREATE TABLE phones (
	phone_id INT NOT NULL ,
	number VARCHAR (25) NOT NULL ,
	user_id INT NOT NULL ,
	phonetype_id INT NOT NULL ,
	PRIMARY KEY (phone_id,user_id) ,
    FOREIGN KEY (user_id) REFERENCES users (user_id)
);

CREATE TABLE phonetypes (
	phonetype_id INT NOT NULL ,
	typename VARCHAR (12) NOT NULL ,
	lang_id INT NOT NULL ,
	PRIMARY KEY (phonetype_id,lang_id) ,
    FOREIGN KEY (lang_id) REFERENCES lang_prefixes (lang_id)
);

CREATE TABLE roles_rights (
	role_id INT NOT NULL ,
	meta_id INT NOT NULL ,
	set_id SMALLINT NOT NULL ,
	PRIMARY KEY (role_id, meta_id) ,
    FOREIGN KEY (meta_id) REFERENCES meta (meta_id) ,
    FOREIGN KEY (role_id) REFERENCES roles (role_id)
);

CREATE TABLE sys_data (
	sys_id SMALLINT NOT NULL ,
	type_id SMALLINT NOT NULL ,
	sysdata_value VARCHAR (80) ,
	PRIMARY KEY (sys_id,type_id) ,
    FOREIGN KEY (type_id) REFERENCES sys_types (type_id)
);

CREATE TABLE templates_cref (
	group_id INT NOT NULL ,
	template_id INT NOT NULL ,
	PRIMARY KEY (group_id,template_id) ,
    FOREIGN KEY (group_id) REFERENCES templategroups (group_id) ,
    FOREIGN KEY (template_id) REFERENCES templates (template_id)
);

CREATE TABLE text_docs (
	meta_id INT NOT NULL ,
	template_id INT NOT NULL ,
	group_id INT NOT NULL ,
	sort_order INT NOT NULL ,
	default_template_1 INT NOT NULL ,
	default_template_2 INT NOT NULL ,
	PRIMARY KEY (meta_id) ,
    FOREIGN KEY (meta_id) REFERENCES meta (meta_id) ,
    FOREIGN KEY (template_id) REFERENCES templates (template_id)
);

CREATE TABLE texts (
	meta_id INT NOT NULL ,
	name INT NOT NULL ,
	text NCHAR VARYING(5000) NOT NULL ,
	type INT ,
	counter INT NOT NULL ,
	PRIMARY KEY (counter) ,
    FOREIGN KEY (meta_id) REFERENCES meta (meta_id)
);

CREATE TABLE url_docs (
	meta_id INT NOT NULL ,
	frame_name VARCHAR (80) NOT NULL ,
	target VARCHAR (15) NOT NULL ,
	url_ref VARCHAR (255) NOT NULL ,
	url_txt VARCHAR (255) NOT NULL ,
	lang_prefix VARCHAR (3) NOT NULL ,
	PRIMARY KEY (meta_id,lang_prefix) ,
    FOREIGN KEY (meta_id) REFERENCES meta (meta_id)
);

CREATE TABLE user_flags_crossref (
	user_id INT NOT NULL ,
	user_flag_id INT NOT NULL ,
	PRIMARY KEY (user_id,user_flag_id) ,
    FOREIGN KEY (user_flag_id) REFERENCES user_flags (user_flag_id) ,
    FOREIGN KEY (user_id) REFERENCES users (user_id)
);

CREATE TABLE user_rights (
	user_id INT NOT NULL ,
	meta_id INT NOT NULL ,
	permission_id SMALLINT NOT NULL ,
	PRIMARY KEY (user_id,meta_id,permission_id) ,
    FOREIGN KEY (meta_id) REFERENCES meta (meta_id) ,
    FOREIGN KEY (user_id) REFERENCES users (user_id)
);

CREATE TABLE user_roles_crossref (
	user_id INT NOT NULL ,
	role_id INT NOT NULL ,
	PRIMARY KEY (user_id,role_id) ,
    FOREIGN KEY (role_id) REFERENCES roles (role_id) ,
    FOREIGN KEY (user_id) REFERENCES users (user_id)
);

CREATE TABLE useradmin_role_crossref (
	user_id INT NOT NULL ,
	role_id INT NOT NULL ,
	PRIMARY KEY (user_id,role_id) ,
    FOREIGN KEY (role_id) REFERENCES roles (role_id) ,
    FOREIGN KEY (user_id) REFERENCES users (user_id)
);

