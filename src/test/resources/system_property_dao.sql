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

INSERT INTO sys_types (type_id, name) VALUES (1, 'startDocument'), (8, 'languageId');
INSERT INTO sys_data (sys_id, type_id, value) VALUES (1,1,1001), (8, 8, 1);


