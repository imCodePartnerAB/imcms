-- Available languages
DROP TABLE IF EXISTS `i18n_available_languages`;

create table i18n_available_languages (
    language_id smallint not null auto_increment,
    language_code_iso_639_1 varchar(2) not null comment 'Language code in ISO 639-1 format',
    language_name varchar(128) not null,
    language_native_name varchar(128) null,
    primary key pk__i18n_available_languages(language_id)
); -- store type

-- unique name, native name

insert into i18n_available_languages (language_code_iso_639_1, language_name, language_native_name)
values ('en', 'English', 'English');

insert into i18n_available_languages (language_code_iso_639_1, language_name, language_native_name)
values ('sv', 'Swedish', 'Svenska');

insert into i18n_available_languages (language_code_iso_639_1, language_name, language_native_name)
values ('de', 'German', 'Deutsch');

insert into i18n_available_languages (language_code_iso_639_1, language_name, language_native_name)
values ('ru', 'Russian', 'Russkij');

-- Instance languages contains chosen subset from available languages
DROP TABLE IF EXISTS `i18n_languages`;

create table i18n_languages (
    language_id smallint,
    system_default  boolean not null default false,
    primary key pk__i18n_languages(language_id),
    foreign key fk__i18n_languages__i18n_available_languages(language_id) references i18n_available_languages(language_id)
); -- store type

insert into i18n_languages (language_id) select language_id from i18n_available_languages;

update i18n_languages set system_default = true where language_id =1;

create or replace view i18n_languages_v as
    select
        il.language_id,
        al.language_code_iso_639_1,
        al.language_name,
        al.language_native_name,
        il.system_default
    from i18n_languages il inner join i18n_available_languages al
    on il.language_id = al.language_id;
    -- union english/default lang?

/*
create table settings (
    default_language_id number,
    foreign key fk__settings__language (default_language_id) refernces ... 
); -- store type
*/


alter table texts add column language_id smallint not null default 1;
alter table images add column language_id smallint null;
alter table images add column image_id int auto_increment not null primary key;


alter table meta add column missing_i18n_show_rule varchar(32) default 'DO_NOT_SHOW';
alter table meta add constraint chk__meta__missing_i18n_show_rule check missing_i18n_show_rule in ('SHOW_IN_DEFAULT_LANGUAGE', 'DO_NOT_SHOW');

-- add uniqeu ix: meta, ..., lang

alter table texts_history add column language_id smallint null;

alter table texts add foreign key fk__texts__i18n_languages(language_id) references i18n_languages(language_id);
alter table texts_history add foreign key fk__texts_history__i18n_languages(language_id) references i18n_languages(language_id);

-- contains i18n-ed part of meta
drop table if exists i18n_meta_part;
create table i18n_meta_part(
    part_id int auto_increment not null,
    language_id smallint,
    meta_id int,
    meta_enabled boolean not null default false,
    meta_headline varchar(255) null,
    meta_text varchar(1000) null,
    meta_image varchar(255) null,
    primary key pk__i18n_meta_part(part_id),
    foreign key fk__i18n_meta_part__i18n_languages(language_id) references i18n_languages(language_id),
    unique index uix__i18n_meta_part__language_id__meta_id(language_id, meta_id)
); -- type;

-- contains i18n-ed part of meta
drop table if exists i18n_keywords;
create table i18n_keywords(
    keyword_id int auto_increment not null,
    keyword_value varchar(128),
    primary key pk__i18n_meta_keywords(keyword_id)
); -- type;

-- contains i18n-ed part of meta
drop table if exists i18n_meta_keywords;
create table i18n_meta_keywords(
    meta_id int not null,
    keyword_id int not null
); -- type;


delete from database_version;
insert into database_version values (5, 0);
commit;


--
-- Dumping data for table `text_docs`
--

/*!40000 ALTER TABLE `text_docs` DISABLE KEYS */;
INSERT INTO `text_docs` (`meta_id`,`template_name`,`group_id`,`default_template_1`,`default_template_2`,`default_template`) VALUES
 (1001,'demo',0,NULL,NULL,NULL);
/*!40000 ALTER TABLE `text_docs` ENABLE KEYS */;

--
-- Dumping data for table `texts`
--

/*!40000 ALTER TABLE `texts` DISABLE KEYS */;
INSERT INTO `texts` (`meta_id`,`name`,`text`,`type`,`counter`, language_id) VALUES
 (1001,1,'<h3>Welcome to imCMS</h3>',1,1,1),
 (1001,2,'<a href=\"<?imcms:contextpath?>/login/\" title=\"Link to login-page\"><b>Log in!</b></a><br><br><br><a href=\"<?imcms:contextpath?>/servlet/SearchDocuments\" target=\"_blank\" title=\"Link to search-page\"><b>Search-page</b></a><br>Opens in new window.<br><br><a href=\"http://doc.imcms.net//\" target=\"_blank\" title=\"Link to documentation-site\"><b>Documentation</b></a><br>External site, opens in new window.<br><br><a href=\"http://www.imcms.net\" target=\"_blank\" title=\"Link to www.imcms.net\"><b>More about imCMS</b></a><br>imCMS Product-site. External site, opens in new window.<br><br><a href=\"<?imcms:contextpath?>/imcms/docs/\" target=\"_blank\" title=\"Link to included documentation.\"><b>Included documentation.</b></a><br>For administrators and developers, in english. Opens in new window.<br><br><a href=\"<?imcms:contextpath?>/imcms/docs/apisamples/\" target=\"_blank\" title=\"Link to API-samples\"><b>API examples</b></a><br>Only for developers, in english. Opens in new window.<br>Note! Only to be used in test environment - not sites for public use.',1,2,1);
/*!40000 ALTER TABLE `texts` ENABLE KEYS */;


/*!40000 ALTER TABLE `images` DISABLE KEYS */;
INSERT INTO `images` (`image_id`, `language_id`, `meta_id`,`width`,`height`,`border`,`v_space`,`h_space`,`name`,`image_name`,`target`,`align`,`alt_text`,`low_scr`,`imgurl`,`linkurl`,`type`) VALUES
 (1,1,1001,0,0,0,0,0,3,'','_blank','top','','','imCMSpower.gif','http://www.imcms.net',0);
/*!40000 ALTER TABLE `images` ENABLE KEYS */;

/*
create table i18n_images(
    image_id int not null,
    language_id
)
*/