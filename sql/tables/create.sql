CREATE DATABANK mimer_blobs

CREATE TABLE meta (
	meta_id int NOT NULL ,
	description varchar (80) NOT NULL ,
	doc_type int NOT NULL ,
	meta_headline varchar (255) NOT NULL ,
	meta_text varchar (1000) NOT NULL ,
	meta_image varchar (255) NOT NULL ,
	owner_id int NOT NULL ,
	permissions int NOT NULL ,
	shared int NOT NULL ,
	expand int NOT NULL ,
	show_meta int NOT NULL ,
	help_text_id int NOT NULL ,
	archive int NOT NULL ,
	status_id int NOT NULL ,
	lang_prefix varchar (3) NOT NULL ,
	classification varchar (200) NOT NULL ,
	date_created timestamp NOT NULL ,
	date_modified timestamp NOT NULL ,
	sort_position int NOT NULL ,
	menu_position int NOT NULL ,
	disable_search int NOT NULL ,
	target varchar (10) NOT NULL ,
	frame_name varchar (20) NOT NULL ,
	activate int NOT NULL ,
	activated_datetime timestamp,
	archived_datetime timestamp,
	PRIMARY KEY (meta_id)
);

CREATE TABLE browsers (
	browser_id int NOT NULL ,
	name varchar (50) NOT NULL ,
	user_agent varchar (50) NOT NULL ,
	browser_value smallint NOT NULL ,
	PRIMARY KEY (browser_id)
);

CREATE TABLE childs (
	meta_id int NOT NULL ,
	to_meta_id int NOT NULL ,
	menu_sort int NOT NULL ,
	manual_sort_order int NOT NULL ,
	PRIMARY KEY (meta_id, to_meta_id, menu_sort) ,
	FOREIGN KEY (meta_id) REFERENCES meta (meta_id)
);

CREATE TABLE classification (
	class_id int NOT NULL ,
	code varchar (30) NOT NULL ,
	PRIMARY KEY (class_id)
);

CREATE TABLE doc_permissions (
	permission_id int NOT NULL ,
	doc_type int NOT NULL ,
	lang_prefix varchar (3) NOT NULL ,
	description varchar (50) NOT NULL ,
	PRIMARY KEY (permission_id, doc_type, lang_prefix)
);

CREATE TABLE doc_types (
	doc_type int NOT NULL ,
	lang_prefix varchar (3) NOT NULL ,
	type varchar (50),
	PRIMARY KEY (doc_type, lang_prefix)
);

CREATE TABLE fileupload_docs (
	meta_id int NOT NULL ,
	filename varchar (50) NOT NULL ,
	mime varchar (50) NOT NULL ,
	PRIMARY KEY (meta_id)
);

CREATE TABLE ip_accesses (
	ip_access_id int NOT NULL ,
	user_id int NOT NULL ,
	ip_start decimal(18, 0) NOT NULL ,
	ip_end decimal(18, 0) NOT NULL
);

CREATE TABLE lang_prefixes (
	lang_id int NOT NULL ,
	lang_prefix char (3),
	PRIMARY KEY (lang_id)
);

CREATE TABLE languages (
	lang_prefix varchar (3) NOT NULL ,
	user_prefix varchar (3) NOT NULL ,
	language varchar (30),
	PRIMARY KEY (lang_prefix, user_prefix)
);

CREATE TABLE main_log (
	log_datetime timestamp ,
	event varchar (255)
);

CREATE TABLE mime_types (
	mime_id int NOT NULL ,
	mime_name varchar (50) NOT NULL ,
	mime varchar (50) NOT NULL ,
	lang_prefix varchar (3) NOT NULL ,
	PRIMARY KEY (mime_id,lang_prefix)
);

CREATE TABLE permission_sets (
	set_id int NOT NULL ,
	description varchar (30) NOT NULL ,
	PRIMARY KEY (set_id)
);

CREATE TABLE permissions (
	permission_id smallint NOT NULL ,
	lang_prefix varchar (3) NOT NULL ,
	description varchar (50) NOT NULL ,
	PRIMARY KEY (permission_id,lang_prefix)
);

CREATE TABLE readrunner_user_data (
	user_id int NOT NULL ,
	uses int ,
	max_uses int ,
	max_uses_warning_threshold int ,
	expiry_date timestamp ,
	expiry_date_warning_threshold int ,
	expiry_date_warning_sent int NOT NULL,
	PRIMARY KEY (user_id)
);

CREATE TABLE roles (
	role_id int NOT NULL ,
	role_name char (25) NOT NULL ,
	permissions int NOT NULL ,
	admin_role int NOT NULL ,
	PRIMARY KEY (role_id)
);

CREATE TABLE roles_permissions (
	permission_id int NOT NULL ,
	lang_prefix varchar (3) NOT NULL ,
	description varchar (40) NOT NULL ,
	PRIMARY KEY (permission_id,lang_prefix)
);

CREATE TABLE sections (
	section_id int NOT NULL ,
	section_name varchar (50) NOT NULL ,
	PRIMARY KEY (section_id)
);

CREATE TABLE sort_by (
	sort_by_id int NOT NULL ,
	sort_by_type varchar (30) NOT NULL ,
	PRIMARY KEY (sort_by_id)
);

CREATE TABLE stats (
	name varchar (120) NOT NULL ,
	num int NOT NULL ,
	PRIMARY KEY (name)
);

CREATE TABLE sys_types (
	type_id smallint NOT NULL ,
	name varchar (50) ,
	PRIMARY KEY (type_id)
);

CREATE TABLE templategroups (
	group_id int NOT NULL ,
	group_name varchar (50) NOT NULL ,
	PRIMARY KEY (group_id)
);

CREATE TABLE templates (
	template_id int NOT NULL ,
	template_name varchar (80) NOT NULL ,
	simple_name varchar (80) NOT NULL ,
	lang_prefix varchar (3) NOT NULL ,
	no_of_txt int ,
	no_of_img int ,
	no_of_url int ,
	PRIMARY KEY (template_id)
);

CREATE TABLE track_log (
	user_id smallint ,
	log_datetime timestamp ,
	from_meta_id int ,
	to_meta_id int ,
	cookie_id int
);

CREATE TABLE user_flags (
	user_flag_id int NOT NULL ,
	name varchar (64) NOT NULL ,
	type int NOT NULL ,
	description varchar (256) NOT NULL ,
	PRIMARY KEY (user_flag_id)
);

CREATE TABLE user_types (
	user_type int NOT NULL ,
	type_name char (30) ,
	lang_prefix char (3) NOT NULL ,
	PRIMARY KEY (user_type,lang_prefix)
);

CREATE TABLE users (
	user_id int NOT NULL ,
	login_name varchar (50) NOT NULL ,
	login_password varchar (15) NOT NULL ,
	first_name varchar (25) NOT NULL ,
	last_name varchar (30) NOT NULL ,
	title varchar (30) NOT NULL ,
	company varchar (30) NOT NULL ,
	address varchar (40) NOT NULL ,
	city varchar (30) NOT NULL ,
	zip varchar (15) NOT NULL ,
	country varchar (30) NOT NULL ,
	county_council varchar (30) NOT NULL ,
	email varchar (50) NOT NULL ,
	external int NOT NULL ,
	last_page int NOT NULL ,
	archive_mode int NOT NULL ,
	lang_id int NOT NULL ,
	user_type int NOT NULL ,
	active int NOT NULL ,
	create_date timestamp NOT NULL ,
	PRIMARY KEY (user_id)
);

CREATE TABLE browser_docs (
	meta_id int NOT NULL ,
	to_meta_id int NOT NULL ,
	browser_id int NOT NULL ,
	PRIMARY KEY (meta_id, to_meta_id, browser_id) ,
	FOREIGN KEY (meta_id) REFERENCES meta (meta_id)
);

CREATE TABLE display_name (
	sort_by_id int NOT NULL ,
	lang_id int NOT NULL ,
	display_name varchar (30) NOT NULL ,
    PRIMARY KEY (sort_by_id, lang_id) ,
    FOREIGN KEY (lang_id) REFERENCES lang_prefixes (lang_id) ,
    FOREIGN KEY (sort_by_id) REFERENCES sort_by (sort_by_id)
);

CREATE TABLE doc_permission_sets (
	meta_id int NOT NULL ,
	set_id int NOT NULL ,
	permission_id int NOT NULL ,
	PRIMARY KEY (meta_id, set_id) ,
    FOREIGN KEY (set_id) REFERENCES permission_sets (set_id) ,
    FOREIGN KEY (meta_id) REFERENCES meta (meta_id)
);

CREATE TABLE doc_permission_sets_ex (
	meta_id int NOT NULL ,
	set_id int NOT NULL ,
	permission_id int NOT NULL ,
	permission_data int NOT NULL ,
	PRIMARY KEY (meta_id, set_id, permission_id, permission_data) ,
    FOREIGN KEY (meta_id,set_id) REFERENCES doc_permission_sets (meta_id,set_id) ,
    FOREIGN KEY (set_id) REFERENCES permission_sets (set_id)
);

CREATE TABLE frameset_docs (
	meta_id int NOT NULL ,
	frame_set text ,
	PRIMARY KEY (meta_id) ,
	FOREIGN KEY (meta_id) REFERENCES meta (meta_id)
);

CREATE TABLE images (
	meta_id int NOT NULL ,
	width int NOT NULL ,
	height int NOT NULL ,
	border int NOT NULL ,
	v_space int NOT NULL ,
	h_space int NOT NULL ,
	name int NOT NULL ,
	image_name varchar (40) NOT NULL ,
	target varchar (15) NOT NULL ,
	target_name varchar (80) NOT NULL ,
	align varchar (15) NOT NULL ,
	alt_text varchar (255) NOT NULL ,
	low_scr varchar (255) NOT NULL ,
	imgurl varchar (255) NOT NULL ,
	linkurl varchar (255) NOT NULL ,
	PRIMARY KEY (meta_id,name) ,
	FOREIGN KEY (meta_id) REFERENCES meta (meta_id)
);

CREATE TABLE includes (
	meta_id int NOT NULL ,
	include_id int NOT NULL ,
	included_meta_id int NOT NULL ,
    PRIMARY KEY (meta_id, include_id) ,
	FOREIGN KEY (meta_id) REFERENCES meta (meta_id) ,
	FOREIGN KEY (included_meta_id) REFERENCES meta (meta_id)
);

CREATE TABLE meta_classification (
	meta_id int NOT NULL ,
	class_id int NOT NULL ,
	PRIMARY KEY (meta_id, class_id) ,
    FOREIGN KEY (meta_id) REFERENCES meta (meta_id) ,
    FOREIGN KEY (class_id) REFERENCES classification (class_id)
);

CREATE TABLE meta_section (
	meta_id int NOT NULL ,
	section_id int NOT NULL ,
    PRIMARY KEY (meta_id,section_id) ,
    FOREIGN KEY (meta_id) REFERENCES meta (meta_id) ,
    FOREIGN KEY (section_id) REFERENCES sections (section_id)
);

CREATE TABLE new_doc_permission_sets (
	meta_id int NOT NULL ,
	set_id int NOT NULL ,
	permission_id int NOT NULL ,
	PRIMARY KEY (meta_id,set_id) ,
    FOREIGN KEY (meta_id) REFERENCES meta (meta_id) ,
    FOREIGN KEY (set_id) REFERENCES permission_sets (set_id)
);

CREATE TABLE new_doc_permission_sets_ex (
	meta_id int NOT NULL ,
	set_id int NOT NULL ,
	permission_id int NOT NULL ,
	permission_data int NOT NULL ,
	PRIMARY KEY (meta_id,set_id,permission_id,permission_data) ,
    FOREIGN KEY (meta_id,set_id) REFERENCES new_doc_permission_sets (meta_id,set_id) ,
    FOREIGN KEY (set_id) REFERENCES permission_sets (set_id)
);

CREATE TABLE phones (
	phone_id int NOT NULL ,
	number varchar (25) NOT NULL ,
	user_id int NOT NULL ,
	phonetype_id int NOT NULL ,
	PRIMARY KEY (phone_id,user_id) ,
    FOREIGN KEY (user_id) REFERENCES users (user_id)
);

CREATE TABLE phonetypes (
	phonetype_id int NOT NULL ,
	typename varchar (12) NOT NULL ,
	lang_id int NOT NULL ,
	PRIMARY KEY (phonetype_id,lang_id) ,
    FOREIGN KEY (lang_id) REFERENCES lang_prefixes (lang_id)
);

CREATE TABLE roles_rights (
	role_id int NOT NULL ,
	meta_id int NOT NULL ,
	set_id smallint NOT NULL ,
	PRIMARY KEY (role_id, meta_id) ,
    FOREIGN KEY (meta_id) REFERENCES meta (meta_id) ,
    FOREIGN KEY (role_id) REFERENCES roles (role_id)
);

CREATE TABLE sys_data (
	sys_id smallint NOT NULL ,
	type_id smallint NOT NULL ,
	sysdata_value varchar (80) ,
	PRIMARY KEY (sys_id,type_id) ,
    FOREIGN KEY (type_id) REFERENCES sys_types (type_id)
);

CREATE TABLE templates_cref (
	group_id int NOT NULL ,
	template_id int NOT NULL ,
	PRIMARY KEY (group_id,template_id) ,
    FOREIGN KEY (group_id) REFERENCES templategroups (group_id) ,
    FOREIGN KEY (template_id) REFERENCES templates (template_id)
);

CREATE TABLE text_docs (
	meta_id int NOT NULL ,
	template_id int NOT NULL ,
	group_id int NOT NULL ,
	sort_order int NOT NULL ,
	default_template_1 int NOT NULL ,
	default_template_2 int NOT NULL ,
	PRIMARY KEY (meta_id) ,
    FOREIGN KEY (meta_id) REFERENCES meta (meta_id) ,
    FOREIGN KEY (template_id) REFERENCES templates (template_id)
);

CREATE TABLE texts (
	meta_id int NOT NULL ,
	name int NOT NULL ,
	text ntext NOT NULL ,
	type int ,
	counter int NOT NULL ,
	PRIMARY KEY (counter) ,
    FOREIGN KEY (meta_id) REFERENCES meta (meta_id)
);

CREATE TABLE url_docs (
	meta_id int NOT NULL ,
	frame_name varchar (80) NOT NULL ,
	target varchar (15) NOT NULL ,
	url_ref varchar (255) NOT NULL ,
	url_txt varchar (255) NOT NULL ,
	lang_prefix varchar (3) NOT NULL ,
	PRIMARY KEY (meta_id,lang_prefix) ,
    FOREIGN KEY (meta_id) REFERENCES meta (meta_id)
);

CREATE TABLE user_flags_crossref (
	user_id int NOT NULL ,
	user_flag_id int NOT NULL ,
	PRIMARY KEY (user_id,user_flag_id) ,
    FOREIGN KEY (user_flag_id) REFERENCES user_flags (user_flag_id) ,
    FOREIGN KEY (user_id) REFERENCES users (user_id)
);

CREATE TABLE user_rights (
	user_id int NOT NULL ,
	meta_id int NOT NULL ,
	permission_id smallint NOT NULL ,
	PRIMARY KEY (user_id,meta_id,permission_id) ,
    FOREIGN KEY (meta_id) REFERENCES meta (meta_id) ,
    FOREIGN KEY (user_id) REFERENCES users (user_id)
);

CREATE TABLE user_roles_crossref (
	user_id int NOT NULL ,
	role_id int NOT NULL ,
	PRIMARY KEY (user_id,role_id) ,
    FOREIGN KEY (role_id) REFERENCES roles (role_id) ,
    FOREIGN KEY (user_id) REFERENCES users (user_id)
);

CREATE TABLE useradmin_role_crossref (
	user_id int NOT NULL ,
	role_id int NOT NULL ,
	PRIMARY KEY (user_id,role_id) ,
    FOREIGN KEY (role_id) REFERENCES roles (role_id) ,
    FOREIGN KEY (user_id) REFERENCES users (user_id)
);

