DROP TABLE IF EXISTS `doc_types`;

CREATE TABLE `doc_types` (
  `doc_type` int(11) NOT NULL,
  `lang_prefix` varchar(3) NOT NULL DEFAULT 'swe',
  `type` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`doc_type`,`lang_prefix`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `doc_types` (`doc_type`, `lang_prefix`, `type`)
VALUES
	(2,'eng','Text page'),
	(2,'swe','Textsida'),
	(5,'eng','External link'),
	(5,'swe','Extern l�nk'),
	(7,'eng','HTML-document'),
	(7,'swe','HTML-dokument'),
	(8,'eng','File'),
	(8,'swe','Fil');


DROP TABLE IF EXISTS `imcms_text_doc_menus`;

CREATE TABLE `imcms_text_doc_menus` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `doc_id` int(11) NOT NULL,
  `doc_version_no` int(11) NOT NULL,
  `no` int(11) NOT NULL,
  `sort_order` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk__imcms_text_doc_menus__doc_id__doc_version_no__no` (`doc_id`,`doc_version_no`,`no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `imcms_text_doc_menu_items`;

CREATE TABLE `imcms_text_doc_menu_items` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `menu_id` int(11) NOT NULL,
  `to_doc_id` int(11) NOT NULL,
  `manual_sort_order` int(11) NOT NULL,
  `tree_sort_index` varchar(64) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk__imcms_text_doc_menu_items__menu_id__doc_id` (`menu_id`,`to_doc_id`),
  CONSTRAINT `fk__imcms_text_doc_menu_items__menu` FOREIGN KEY (`menu_id`) REFERENCES `imcms_text_doc_menus` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

insert into imcms_text_doc_menus (doc_id, doc_version_no, no, sort_order)
values (1001, 1, 1, 1), (1002, 1, 1, 1), (1003, 1, 1, 1);

insert into imcms_text_doc_menu_items (menu_id, to_doc_id, manual_sort_order, tree_sort_index)
values (1, 1001, 1, 1), (2, 1001, 1, 1), (3, 1001, 1, 1);




DROP TABLE IF EXISTS `mime_types`;

CREATE TABLE `mime_types` (
  `mime_id` int(11) NOT NULL,
  `mime_name` varchar(50) NOT NULL,
  `mime` varchar(50) NOT NULL,
  `lang_prefix` varchar(3) NOT NULL DEFAULT 'swe',
  PRIMARY KEY (`mime_id`,`lang_prefix`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


INSERT INTO `mime_types` (`mime_id`, `mime_name`, `mime`, `lang_prefix`)
VALUES
	(0,'Other...','other','eng'),
	(0,'Annan...','other','swe'),
	(1,'Plain text','text/plain','eng'),
	(1,'Vanlig text','text/plain','swe'),
	(2,'HTML-document','text/html','eng'),
	(2,'HTML-dokument','text/html','swe'),
	(3,'Binary file','application/octet-stream','eng'),
	(3,'Bin�rfil','application/octet-stream','swe'),
	(4,'Shockwave Flash','application/x-shockwave-flash','eng'),
	(4,'Shockwave Flash','application/x-shockwave-flash','swe'),
	(5,'Shockwave Director','application/x-director','eng'),
	(5,'Shockwave Director','application/x-director','swe'),
	(6,'PNG-image','image/png','eng'),
	(6,'PNG-bild','image/png','swe'),
	(7,'GIF-image','image/gif','eng'),
	(7,'GIF-bild','image/gif','swe'),
	(8,'JPEG-image','image/jpeg','eng'),
	(8,'JPEG-bild','image/jpeg','swe'),
	(9,'Adobe Acrobat-document','application/pdf','eng'),
	(9,'Adobe Acrobat-dokument','application/pdf','swe'),
	(10,'Wav-sound','audio/x-wav','eng'),
	(10,'Wav-ljud','audio/x-wav','swe'),
	(11,'Zip-file','application/zip','eng'),
	(11,'Zip-fil','application/zip','swe'),
	(12,'AVI-movie','video/x-msvideo','eng'),
	(12,'AVI-film','video/x-msvideo','swe'),
	(13,'Quicktime-movie','video/quicktime','eng'),
	(13,'Quicktime-film','video/quicktime','swe'),
	(14,'MPEG-movie','video/mpeg','eng'),
	(14,'MPEG-film','video/mpeg','swe'),
	(15,'MS Word-document','application/msword','eng'),
	(15,'MS Word-dokument','application/msword','swe'),
	(16,'MS Excel-document','application/vnd.ms-excel','eng'),
	(16,'MS Excel-dokument','application/vnd.ms-excel','swe'),
	(17,'MS Powerpoint-document','application/vnd.ms-powerpoint','eng'),
	(17,'MS Powerpoint-dokument','application/vnd.ms-powerpoint','swe');

DROP TABLE IF EXISTS `roles`;

CREATE TABLE `roles` (
  `role_id` int(11) NOT NULL,
  `role_name` varchar(60) NOT NULL,
  `permissions` int(11) NOT NULL DEFAULT '0',
  `admin_role` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`role_id`),
  UNIQUE KEY `UQ_roles__role_name` (`role_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `roles` (`role_id`, `role_name`, `permissions`, `admin_role`)
VALUES
	(0,'Superadmin',0,1),
	(1,'Useradmin',0,2),
	(2,'Users',1,0);


DROP TABLE IF EXISTS `roles_rights`;

CREATE TABLE `roles_rights` (
  `role_id` int(11) NOT NULL,
  `meta_id` int(11) NOT NULL,
  `set_id` smallint(6) NOT NULL,
  PRIMARY KEY (`role_id`,`meta_id`),
  KEY `roles_rights_FK_meta_id_meta` (`meta_id`),
  CONSTRAINT `roles_rights_FK_role_id_roles` FOREIGN KEY (`role_id`) REFERENCES `roles` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `roles_rights` (`role_id`, `meta_id`, `set_id`)
VALUES
	(2,1001,3);