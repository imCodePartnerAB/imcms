CREATE TABLE IF NOT EXISTS `imcms_languages` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `code` varchar(3) NOT NULL COMMENT 'Language code.',
  `name` varchar(128) NOT NULL COMMENT 'Language name in english.',
  `native_name` varchar(128) DEFAULT NULL COMMENT 'Language native name e.g Svenska, Suomi, etc.',
  `enabled` tinyint(4) NOT NULL DEFAULT '1' COMMENT 'Language enabled status.',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk__imcms_languages__code` (`code`),
  UNIQUE KEY `uk__imcms_languages__name` (`name`),
  UNIQUE KEY `uk__imcms_languages__native_name` (`native_name`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS `sys_types`(
  `type_id` smallint(6) NOT NULL,
  `name` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS `sys_data` (
  `sys_id` smallint(6) NOT NULL AUTO_INCREMENT,
  `type_id` smallint(6) NOT NULL,
  `value` varchar(1000) DEFAULT NULL,
  PRIMARY KEY (`sys_id`,`type_id`),
  KEY `sys_data_FK_type_id_sys_types` (`type_id`),
  CONSTRAINT `sys_data_FK_type_id_sys_types` FOREIGN KEY (`type_id`) REFERENCES `sys_types` (`type_id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;


DELETE FROM sys_data;
DELETE FROM sys_types;
DELETE FROM imcms_languages;

INSERT INTO sys_types (type_id, name) VALUES (8, 'LanguageId');
INSERT INTO sys_data (sys_id, type_id, value) VALUES (8, 8, 1);
INSERT INTO imcms_languages
  (`id`, `code`, `name`, `native_name`, `enabled`)
VALUES
  (1, 'en', 'English', 'English', true),
  (2, 'sv', 'Swedish', 'Svenska', true);


