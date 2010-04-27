-- MySQL dump 10.13  Distrib 5.1.38, for apple-darwin9.5.0 (i386)
--
-- Host: localhost    Database: imcms_rb4
-- ------------------------------------------------------
-- Server version	5.1.38

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `archive_exif`
--

DROP TABLE IF EXISTS `archive_exif`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `archive_exif` (
  `image_id` bigint(20) NOT NULL,
  `exif_type` smallint(6) NOT NULL DEFAULT '0' COMMENT '0-original, 1-changed',
  `resolution` int(11) NOT NULL COMMENT 'in DPI (dots per inch)',
  `description` varchar(255) NOT NULL DEFAULT '',
  `artist` varchar(255) NOT NULL DEFAULT '',
  `copyright` varchar(255) NOT NULL DEFAULT '',
  `created_dt` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updated_dt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`image_id`,`exif_type`),
  CONSTRAINT `archive_image_id_fk` FOREIGN KEY (`image_id`) REFERENCES `archive_images` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `archive_exif`
--

LOCK TABLES `archive_exif` WRITE;
/*!40000 ALTER TABLE `archive_exif` DISABLE KEYS */;
/*!40000 ALTER TABLE `archive_exif` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `archive_image_keywords`
--

DROP TABLE IF EXISTS `archive_image_keywords`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `archive_image_keywords` (
  `image_id` bigint(20) NOT NULL,
  `keyword_id` bigint(20) NOT NULL,
  `created_dt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`image_id`,`keyword_id`),
  KEY `archive_image_keywords_keyword_id_fk` (`keyword_id`),
  CONSTRAINT `archive_image_keywords_image_id_fk` FOREIGN KEY (`image_id`) REFERENCES `archive_images` (`id`),
  CONSTRAINT `archive_image_keywords_keyword_id_fk` FOREIGN KEY (`keyword_id`) REFERENCES `archive_keywords` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `archive_image_keywords`
--

LOCK TABLES `archive_image_keywords` WRITE;
/*!40000 ALTER TABLE `archive_image_keywords` DISABLE KEYS */;
/*!40000 ALTER TABLE `archive_image_keywords` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `archive_images`
--

DROP TABLE IF EXISTS `archive_images`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `archive_images` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `image_nm` varchar(255) NOT NULL DEFAULT '',
  `format` smallint(6) NOT NULL COMMENT '0-BMP, 1-GIF, 2-JPEG, 3-PNG, 4-PDF, 5-PS, 6-PSD, 7-SVG, 8-TIFF, 9-XCF, 10-PICT',
  `width` int(11) NOT NULL,
  `height` int(11) NOT NULL,
  `file_size` int(11) NOT NULL COMMENT 'in bytes',
  `uploaded_by` varchar(130) NOT NULL DEFAULT '',
  `users_id` int(11) NOT NULL,
  `status` smallint(6) NOT NULL DEFAULT '0' COMMENT '0-uploaded, 1-active, 2-archived',
  `created_dt` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updated_dt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `license_dt` date DEFAULT NULL,
  `license_end_dt` date DEFAULT NULL,
  `publish_dt` date DEFAULT NULL,
  `archive_dt` date DEFAULT NULL,
  `publish_end_dt` date DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `archive_images_users_id_fk` (`users_id`),
  CONSTRAINT `archive_images_users_id_fk` FOREIGN KEY (`users_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `archive_images`
--

LOCK TABLES `archive_images` WRITE;
/*!40000 ALTER TABLE `archive_images` DISABLE KEYS */;
/*!40000 ALTER TABLE `archive_images` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `archive_keywords`
--

DROP TABLE IF EXISTS `archive_keywords`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `archive_keywords` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `keyword_nm` varchar(50) NOT NULL,
  `created_dt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `archive_keywords`
--

LOCK TABLES `archive_keywords` WRITE;
/*!40000 ALTER TABLE `archive_keywords` DISABLE KEYS */;
/*!40000 ALTER TABLE `archive_keywords` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `archive_libraries`
--

DROP TABLE IF EXISTS `archive_libraries`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `archive_libraries` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `library_nm` varchar(120) NOT NULL,
  `folder_nm` varchar(255) NOT NULL,
  `filepath` varchar(255) DEFAULT NULL,
  `library_type` smallint(6) NOT NULL DEFAULT '0' COMMENT '0-standard, 1-old library',
  `created_dt` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updated_dt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `archive_libraries_folder_nm_filepath_unq` (`folder_nm`,`filepath`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `archive_libraries`
--

LOCK TABLES `archive_libraries` WRITE;
/*!40000 ALTER TABLE `archive_libraries` DISABLE KEYS */;
/*!40000 ALTER TABLE `archive_libraries` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `archive_library_roles`
--

DROP TABLE IF EXISTS `archive_library_roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `archive_library_roles` (
  `library_id` int(11) NOT NULL,
  `role_id` int(11) NOT NULL,
  `permissions` int(11) NOT NULL,
  `created_dt` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updated_dt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`library_id`,`role_id`),
  KEY `archive_library_roles_role_id_fk` (`role_id`),
  CONSTRAINT `archive_library_roles_library_id_fk` FOREIGN KEY (`library_id`) REFERENCES `archive_libraries` (`id`),
  CONSTRAINT `archive_library_roles_role_id_fk` FOREIGN KEY (`role_id`) REFERENCES `roles` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `archive_library_roles`
--

LOCK TABLES `archive_library_roles` WRITE;
/*!40000 ALTER TABLE `archive_library_roles` DISABLE KEYS */;
/*!40000 ALTER TABLE `archive_library_roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `browser_docs`
--

DROP TABLE IF EXISTS `browser_docs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `browser_docs` (
  `meta_id` int(11) NOT NULL,
  `to_meta_id` int(11) NOT NULL,
  `browser_id` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`meta_id`,`to_meta_id`,`browser_id`),
  CONSTRAINT `browser_docs_FK_meta_id_meta` FOREIGN KEY (`meta_id`) REFERENCES `meta` (`meta_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `browser_docs`
--

LOCK TABLES `browser_docs` WRITE;
/*!40000 ALTER TABLE `browser_docs` DISABLE KEYS */;
/*!40000 ALTER TABLE `browser_docs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `browsers`
--

DROP TABLE IF EXISTS `browsers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `browsers` (
  `browser_id` int(11) NOT NULL,
  `name` varchar(50) NOT NULL,
  `user_agent` varchar(50) NOT NULL,
  `value` smallint(6) NOT NULL DEFAULT '1',
  PRIMARY KEY (`browser_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `browsers`
--

LOCK TABLES `browsers` WRITE;
/*!40000 ALTER TABLE `browsers` DISABLE KEYS */;
INSERT INTO `browsers` VALUES (1,'Internet Explorer','%MSIE%',2),(2,'Netscape','Mozilla%(%;%[UIN][);]%',2),(3,'Internet Explorer 3','%MSIE 3%',4),(4,'Internet Explorer 4','%MSIE 4%',4),(5,'Internet Explorer 5','%MSIE 5%',4),(6,'Internet Explorer 6','%MSIE 6%',4),(7,'Netscape 3','Mozilla/3%(%;%[UIN][ );]%',4),(8,'Netscape 4','Mozilla/4%(%;%[UIN][ );]%',4),(9,'Netscape 6','Mozilla/5%(%;%[UIN][ );]%',4),(10,'Netscape 7','Mozilla%/5;%netscape/7%',4),(11,'Windows','%win%',1),(12,'Macintosh','%mac%',1),(13,'Windows Internet Explorer','%MSIE%win%',3),(14,'Windows Internet Explorer 3','%MSIE 3%win%',5),(15,'Windows Internet Explorer 4','%MSIE 4%win%',5),(16,'Windows Internet Explorer 5.0','%MSIE 5.0%win%',5),(17,'Windows Internet Explorer 5.5','%MSIE 5.5%win%',5),(18,'Windows Internet Explorer 6','%MSIE 6%win%',5),(19,'Windows Netscape','Mozilla%(%win%;%[UIN][ );]%',3),(20,'Windows Netscape 3','Mozilla/3%(%win%;%[UIN][ );]%',5),(21,'Windows Netscape 4','Mozilla/4%(%win%;%[UIN][ );]%',5),(22,'Windows Netscape 6','Mozilla/5%(%win%;%[UIN][ );]%',5),(23,'Windows Netscape 7','Mozilla/5%(%win%netscape/7%',5),(24,'Macintosh Internet Explorer','%MSIE%mac%',3),(25,'Macintosh Internet Explorer 3','%MSIE 3%mac%',5),(26,'Macintosh Internet Explorer 4','%MSIE 4%mac%',5),(27,'Macintosh Internet Explorer 5','%MSIE 5%mac%',5),(28,'Macintosh Netscape','Mozilla%(%mac%;%[UIN][ );]%',3),(29,'Macintosh Netscape 3','Mozilla/3%(%mac%;%[UIN][ );]%',5),(30,'Macintosh Netscape 4','Mozilla/4%(%mac%;%[UIN][ );]%',5),(31,'Macintosh Netscape 6','Mozilla/5%(%mac%;%[UIN][ );]%',5);
/*!40000 ALTER TABLE `browsers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `categories`
--

DROP TABLE IF EXISTS `categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `categories` (
  `category_id` int(11) NOT NULL AUTO_INCREMENT,
  `category_type_id` int(11) NOT NULL,
  `name` varchar(128) NOT NULL,
  `description` varchar(500) DEFAULT NULL,
  `image` varchar(255) NOT NULL,
  PRIMARY KEY (`category_id`),
  KEY `categories_FK_category_type_id_category_types` (`category_type_id`),
  CONSTRAINT `categories_FK_category_type_id_category_types` FOREIGN KEY (`category_type_id`) REFERENCES `category_types` (`category_type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categories`
--

LOCK TABLES `categories` WRITE;
/*!40000 ALTER TABLE `categories` DISABLE KEYS */;
/*!40000 ALTER TABLE `categories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `category_roles`
--

DROP TABLE IF EXISTS `category_roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `category_roles` (
  `category_id` int(11) NOT NULL,
  `role_id` int(11) NOT NULL,
  `created_dt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`category_id`,`role_id`),
  KEY `category_roles_role_id_fk` (`role_id`),
  CONSTRAINT `category_roles_category_id_fk` FOREIGN KEY (`category_id`) REFERENCES `categories` (`category_id`),
  CONSTRAINT `category_roles_role_id_fk` FOREIGN KEY (`role_id`) REFERENCES `roles` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `category_roles`
--

LOCK TABLES `category_roles` WRITE;
/*!40000 ALTER TABLE `category_roles` DISABLE KEYS */;
/*!40000 ALTER TABLE `category_roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `category_types`
--

DROP TABLE IF EXISTS `category_types`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `category_types` (
  `category_type_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(128) NOT NULL,
  `max_choices` int(11) NOT NULL DEFAULT '0',
  `inherited` tinyint(1) NOT NULL,
  `is_image_archive` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'is image category type, can be used in image archive',
  PRIMARY KEY (`category_type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `category_types`
--

LOCK TABLES `category_types` WRITE;
/*!40000 ALTER TABLE `category_types` DISABLE KEYS */;
/*!40000 ALTER TABLE `category_types` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `childs`
--

DROP TABLE IF EXISTS `childs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `childs` (
  `to_meta_id` int(11) NOT NULL,
  `manual_sort_order` int(11) NOT NULL,
  `tree_sort_index` varchar(64) NOT NULL,
  `menu_id` int(11) NOT NULL,
  PRIMARY KEY (`to_meta_id`,`menu_id`),
  KEY `childs_FK_menu_id_menus` (`menu_id`),
  CONSTRAINT `childs_FK_menu_id_menus` FOREIGN KEY (`menu_id`) REFERENCES `menus` (`menu_id`),
  CONSTRAINT `childs_FK_to_meta_id_meta` FOREIGN KEY (`to_meta_id`) REFERENCES `meta` (`meta_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `childs`
--

LOCK TABLES `childs` WRITE;
/*!40000 ALTER TABLE `childs` DISABLE KEYS */;
/*!40000 ALTER TABLE `childs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `childs_history`
--

DROP TABLE IF EXISTS `childs_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `childs_history` (
  `menu_id` int(11) NOT NULL,
  `to_meta_id` int(11) NOT NULL,
  `manual_sort_order` int(11) NOT NULL,
  `tree_sort_index` varchar(64) NOT NULL,
  PRIMARY KEY (`menu_id`,`to_meta_id`),
  KEY `childs_history_FK_to_meta_id_meta` (`to_meta_id`),
  CONSTRAINT `childs_history_FK_menu_id_menus_history` FOREIGN KEY (`menu_id`) REFERENCES `menus_history` (`menu_id`),
  CONSTRAINT `childs_history_FK_to_meta_id_meta` FOREIGN KEY (`to_meta_id`) REFERENCES `meta` (`meta_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `childs_history`
--

LOCK TABLES `childs_history` WRITE;
/*!40000 ALTER TABLE `childs_history` DISABLE KEYS */;
/*!40000 ALTER TABLE `childs_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `classification`
--

DROP TABLE IF EXISTS `classification`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `classification` (
  `class_id` int(11) NOT NULL AUTO_INCREMENT,
  `code` varchar(128) NOT NULL,
  PRIMARY KEY (`class_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `classification`
--

LOCK TABLES `classification` WRITE;
/*!40000 ALTER TABLE `classification` DISABLE KEYS */;
/*!40000 ALTER TABLE `classification` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `database_version`
--

DROP TABLE IF EXISTS `database_version`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `database_version` (
  `major` int(11) NOT NULL,
  `minor` int(11) NOT NULL,
  PRIMARY KEY (`major`,`minor`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `database_version`
--

LOCK TABLES `database_version` WRITE;
/*!40000 ALTER TABLE `database_version` DISABLE KEYS */;
INSERT INTO `database_version` VALUES (4,11);
/*!40000 ALTER TABLE `database_version` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `doc_permission_sets`
--

DROP TABLE IF EXISTS `doc_permission_sets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `doc_permission_sets` (
  `meta_id` int(11) NOT NULL,
  `set_id` int(11) NOT NULL,
  `permission_id` int(11) NOT NULL,
  PRIMARY KEY (`meta_id`,`set_id`),
  KEY `doc_permission_sets_FK_set_id_permission_sets` (`set_id`),
  CONSTRAINT `doc_permission_sets_FK_meta_id_meta` FOREIGN KEY (`meta_id`) REFERENCES `meta` (`meta_id`),
  CONSTRAINT `doc_permission_sets_FK_set_id_permission_sets` FOREIGN KEY (`set_id`) REFERENCES `permission_sets` (`set_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `doc_permission_sets`
--

LOCK TABLES `doc_permission_sets` WRITE;
/*!40000 ALTER TABLE `doc_permission_sets` DISABLE KEYS */;
/*!40000 ALTER TABLE `doc_permission_sets` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `doc_permission_sets_ex`
--

DROP TABLE IF EXISTS `doc_permission_sets_ex`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `doc_permission_sets_ex` (
  `meta_id` int(11) NOT NULL,
  `set_id` int(11) NOT NULL,
  `permission_id` int(11) NOT NULL,
  `permission_data` int(11) NOT NULL,
  PRIMARY KEY (`meta_id`,`set_id`,`permission_id`,`permission_data`),
  KEY `doc_permission_sets_ex_FK_set_id_permission_sets` (`set_id`),
  CONSTRAINT `doc_permission_sets_ex_FK_meta_id_set_id_doc_permission_sets` FOREIGN KEY (`meta_id`, `set_id`) REFERENCES `doc_permission_sets` (`meta_id`, `set_id`),
  CONSTRAINT `doc_permission_sets_ex_FK_set_id_permission_sets` FOREIGN KEY (`set_id`) REFERENCES `permission_sets` (`set_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `doc_permission_sets_ex`
--

LOCK TABLES `doc_permission_sets_ex` WRITE;
/*!40000 ALTER TABLE `doc_permission_sets_ex` DISABLE KEYS */;
/*!40000 ALTER TABLE `doc_permission_sets_ex` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `doc_permissions`
--

DROP TABLE IF EXISTS `doc_permissions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `doc_permissions` (
  `permission_id` int(11) NOT NULL,
  `doc_type` int(11) NOT NULL,
  `lang_prefix` varchar(3) NOT NULL,
  `description` varchar(50) NOT NULL,
  PRIMARY KEY (`permission_id`,`doc_type`,`lang_prefix`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `doc_permissions`
--

LOCK TABLES `doc_permissions` WRITE;
/*!40000 ALTER TABLE `doc_permissions` DISABLE KEYS */;
INSERT INTO `doc_permissions` VALUES (65536,2,'eng','Edit texts'),(65536,2,'swe','�ndra text'),(65536,5,'eng','Edit'),(65536,5,'swe','Redigera'),(65536,6,'eng','Edit'),(65536,6,'swe','Redigera'),(65536,7,'eng','Edit'),(65536,7,'swe','Redigera'),(65536,8,'eng','Edit'),(65536,8,'swe','Redigera'),(65536,101,'eng','Edit'),(65536,101,'swe','Redigera'),(65536,102,'eng','Edit'),(65536,102,'swe','Redigera'),(131072,2,'eng','Edit pictures'),(131072,2,'swe','�ndra bild'),(262144,2,'eng','Edit menus'),(262144,2,'swe','�ndra meny'),(524288,2,'eng','Change template'),(524288,2,'swe','�ndra utseende'),(1048576,2,'eng','Change include'),(1048576,2,'swe','�ndra include');
/*!40000 ALTER TABLE `doc_permissions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `doc_types`
--

DROP TABLE IF EXISTS `doc_types`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `doc_types` (
  `doc_type` int(11) NOT NULL,
  `lang_prefix` varchar(3) NOT NULL DEFAULT 'swe',
  `type` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`doc_type`,`lang_prefix`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `doc_types`
--

LOCK TABLES `doc_types` WRITE;
/*!40000 ALTER TABLE `doc_types` DISABLE KEYS */;
INSERT INTO `doc_types` VALUES (2,'eng','Text page'),(2,'swe','Textsida'),(5,'eng','External link'),(5,'swe','Extern l�nk'),(6,'eng','Browser controlled link'),(6,'swe','Browserkontroll'),(7,'eng','HTML-document'),(7,'swe','HTML-dokument'),(8,'eng','File'),(8,'swe','Fil');
/*!40000 ALTER TABLE `doc_types` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `document_categories`
--

DROP TABLE IF EXISTS `document_categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `document_categories` (
  `meta_id` int(11) NOT NULL,
  `category_id` int(11) NOT NULL,
  PRIMARY KEY (`meta_id`,`category_id`),
  KEY `document_categories_FK_category_id_categories` (`category_id`),
  CONSTRAINT `document_categories_FK_category_id_categories` FOREIGN KEY (`category_id`) REFERENCES `categories` (`category_id`),
  CONSTRAINT `document_categories_FK_meta_id_meta` FOREIGN KEY (`meta_id`) REFERENCES `meta` (`meta_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `document_categories`
--

LOCK TABLES `document_categories` WRITE;
/*!40000 ALTER TABLE `document_categories` DISABLE KEYS */;
/*!40000 ALTER TABLE `document_categories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `document_properties`
--

DROP TABLE IF EXISTS `document_properties`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `document_properties` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `meta_id` int(11) NOT NULL,
  `key_name` varchar(255) NOT NULL,
  `value` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UQ_document_properties__meta_id__key_name` (`meta_id`,`key_name`),
  CONSTRAINT `document_properties_FK_meta_id_meta` FOREIGN KEY (`meta_id`) REFERENCES `meta` (`meta_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `document_properties`
--

LOCK TABLES `document_properties` WRITE;
/*!40000 ALTER TABLE `document_properties` DISABLE KEYS */;
/*!40000 ALTER TABLE `document_properties` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `document_search_log`
--

DROP TABLE IF EXISTS `document_search_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `document_search_log` (
  `datetime` datetime NOT NULL,
  `term` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `document_search_log`
--

LOCK TABLES `document_search_log` WRITE;
/*!40000 ALTER TABLE `document_search_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `document_search_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fileupload_docs`
--

DROP TABLE IF EXISTS `fileupload_docs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fileupload_docs` (
  `meta_id` int(11) NOT NULL,
  `variant_name` varchar(100) NOT NULL,
  `filename` varchar(255) NOT NULL,
  `mime` varchar(50) NOT NULL,
  `created_as_image` int(11) NOT NULL,
  `default_variant` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`meta_id`,`variant_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fileupload_docs`
--

LOCK TABLES `fileupload_docs` WRITE;
/*!40000 ALTER TABLE `fileupload_docs` DISABLE KEYS */;
/*!40000 ALTER TABLE `fileupload_docs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `frameset_docs`
--

DROP TABLE IF EXISTS `frameset_docs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `frameset_docs` (
  `meta_id` int(11) NOT NULL,
  `frame_set` longtext,
  PRIMARY KEY (`meta_id`),
  CONSTRAINT `frameset_docs_FK_meta_id_meta` FOREIGN KEY (`meta_id`) REFERENCES `meta` (`meta_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `frameset_docs`
--

LOCK TABLES `frameset_docs` WRITE;
/*!40000 ALTER TABLE `frameset_docs` DISABLE KEYS */;
/*!40000 ALTER TABLE `frameset_docs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `image_categories`
--

DROP TABLE IF EXISTS `image_categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `image_categories` (
  `image_id` bigint(20) NOT NULL,
  `category_id` int(11) NOT NULL,
  `created_dt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`image_id`,`category_id`),
  KEY `image_categories_category_id_fk` (`category_id`),
  CONSTRAINT `image_categories_image_id_fk` FOREIGN KEY (`image_id`) REFERENCES `archive_images` (`id`),
  CONSTRAINT `image_categories_category_id_fk` FOREIGN KEY (`category_id`) REFERENCES `categories` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `image_categories`
--

LOCK TABLES `image_categories` WRITE;
/*!40000 ALTER TABLE `image_categories` DISABLE KEYS */;
/*!40000 ALTER TABLE `image_categories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `images`
--

DROP TABLE IF EXISTS `images`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `images` (
  `meta_id` int(11) NOT NULL,
  `width` int(11) NOT NULL,
  `height` int(11) NOT NULL,
  `border` int(11) NOT NULL,
  `v_space` int(11) NOT NULL,
  `h_space` int(11) NOT NULL,
  `name` int(11) NOT NULL,
  `image_name` varchar(40) NOT NULL,
  `target` varchar(15) NOT NULL,
  `align` varchar(15) NOT NULL,
  `alt_text` varchar(255) NOT NULL,
  `low_scr` varchar(255) NOT NULL,
  `imgurl` varchar(255) NOT NULL,
  `linkurl` varchar(255) NOT NULL,
  `type` int(11) NOT NULL,
  `format` smallint(6) NOT NULL,
  `rotate_angle` smallint(6) NOT NULL,
  `crop_x1` int(11) NOT NULL,
  `crop_y1` int(11) NOT NULL,
  `crop_x2` int(11) NOT NULL,
  `crop_y2` int(11) NOT NULL,
  `archive_image_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`meta_id`,`name`),
  CONSTRAINT `images_FK_meta_id_meta` FOREIGN KEY (`meta_id`) REFERENCES `meta` (`meta_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `images`
--

LOCK TABLES `images` WRITE;
/*!40000 ALTER TABLE `images` DISABLE KEYS */;
INSERT INTO `images` VALUES (1001,0,0,0,0,0,3,'','_blank','top','','','imCMSpower.gif','http://www.imcms.net',0,0,0,-1,-1,-1,-1,NULL);
/*!40000 ALTER TABLE `images` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `images_cache`
--

DROP TABLE IF EXISTS `images_cache`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `images_cache` (
  `id` varchar(40) NOT NULL,
  `meta_id` int(11) NOT NULL,
  `image_index` int(11) NOT NULL,
  `resource` varchar(255) NOT NULL,
  `cache_type` smallint(6) NOT NULL,
  `file_size` int(11) NOT NULL,
  `frequency` int(11) NOT NULL,
  `format` smallint(6) NOT NULL,
  `rotate_angle` smallint(6) NOT NULL,
  `width` int(11) NOT NULL,
  `height` int(11) NOT NULL,
  `crop_x1` int(11) NOT NULL,
  `crop_y1` int(11) NOT NULL,
  `crop_x2` int(11) NOT NULL,
  `crop_y2` int(11) NOT NULL,
  `created_dt` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`,`meta_id`,`image_index`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `images_cache`
--

LOCK TABLES `images_cache` WRITE;
/*!40000 ALTER TABLE `images_cache` DISABLE KEYS */;
/*!40000 ALTER TABLE `images_cache` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `images_history`
--

DROP TABLE IF EXISTS `images_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `images_history` (
  `meta_id` int(11) NOT NULL,
  `width` int(11) NOT NULL,
  `height` int(11) NOT NULL,
  `border` int(11) NOT NULL,
  `v_space` int(11) NOT NULL,
  `h_space` int(11) NOT NULL,
  `name` int(11) NOT NULL,
  `image_name` varchar(40) NOT NULL,
  `target` varchar(15) NOT NULL,
  `align` varchar(15) NOT NULL,
  `alt_text` varchar(255) NOT NULL,
  `low_scr` varchar(255) NOT NULL,
  `imgurl` varchar(255) NOT NULL,
  `linkurl` varchar(255) NOT NULL,
  `type` int(11) NOT NULL,
  `modified_datetime` datetime NOT NULL,
  `user_id` int(11) NOT NULL,
  `counter` int(11) NOT NULL AUTO_INCREMENT,
  `format` smallint(6) NOT NULL,
  `rotate_angle` smallint(6) NOT NULL,
  `crop_x1` int(11) NOT NULL,
  `crop_y1` int(11) NOT NULL,
  `crop_x2` int(11) NOT NULL,
  `crop_y2` int(11) NOT NULL,
  `archive_image_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`counter`),
  KEY `images_history_FK_meta_id_meta` (`meta_id`),
  KEY `images_history_FK_user_id_users` (`user_id`),
  CONSTRAINT `images_history_FK_meta_id_meta` FOREIGN KEY (`meta_id`) REFERENCES `meta` (`meta_id`),
  CONSTRAINT `images_history_FK_user_id_users` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `images_history`
--

LOCK TABLES `images_history` WRITE;
/*!40000 ALTER TABLE `images_history` DISABLE KEYS */;
/*!40000 ALTER TABLE `images_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `includes`
--

DROP TABLE IF EXISTS `includes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `includes` (
  `meta_id` int(11) NOT NULL,
  `include_id` int(11) NOT NULL,
  `included_meta_id` int(11) NOT NULL,
  PRIMARY KEY (`meta_id`,`include_id`),
  KEY `includes_FK_included_meta_id_meta` (`included_meta_id`),
  CONSTRAINT `includes_FK_included_meta_id_meta` FOREIGN KEY (`included_meta_id`) REFERENCES `meta` (`meta_id`),
  CONSTRAINT `includes_FK_meta_id_meta` FOREIGN KEY (`meta_id`) REFERENCES `meta` (`meta_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `includes`
--

LOCK TABLES `includes` WRITE;
/*!40000 ALTER TABLE `includes` DISABLE KEYS */;
/*!40000 ALTER TABLE `includes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ip_accesses`
--

DROP TABLE IF EXISTS `ip_accesses`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ip_accesses` (
  `ip_access_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `ip_start` decimal(18,0) NOT NULL,
  `ip_end` decimal(18,0) NOT NULL,
  PRIMARY KEY (`ip_access_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ip_accesses`
--

LOCK TABLES `ip_accesses` WRITE;
/*!40000 ALTER TABLE `ip_accesses` DISABLE KEYS */;
/*!40000 ALTER TABLE `ip_accesses` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lang_prefixes`
--

DROP TABLE IF EXISTS `lang_prefixes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `lang_prefixes` (
  `lang_id` int(11) NOT NULL,
  `lang_prefix` char(3) DEFAULT NULL,
  PRIMARY KEY (`lang_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lang_prefixes`
--

LOCK TABLES `lang_prefixes` WRITE;
/*!40000 ALTER TABLE `lang_prefixes` DISABLE KEYS */;
INSERT INTO `lang_prefixes` VALUES (1,'swe'),(2,'eng');
/*!40000 ALTER TABLE `lang_prefixes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `languages`
--

DROP TABLE IF EXISTS `languages`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `languages` (
  `lang_prefix` varchar(3) NOT NULL,
  `user_prefix` varchar(3) NOT NULL,
  `language` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`lang_prefix`,`user_prefix`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `languages`
--

LOCK TABLES `languages` WRITE;
/*!40000 ALTER TABLE `languages` DISABLE KEYS */;
INSERT INTO `languages` VALUES ('eng','eng','English'),('eng','swe','Engelska'),('swe','eng','Swedish'),('swe','swe','Svenska');
/*!40000 ALTER TABLE `languages` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `menus`
--

DROP TABLE IF EXISTS `menus`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `menus` (
  `menu_id` int(11) NOT NULL AUTO_INCREMENT,
  `meta_id` int(11) NOT NULL,
  `menu_index` int(11) NOT NULL,
  `sort_order` int(11) NOT NULL,
  PRIMARY KEY (`menu_id`),
  UNIQUE KEY `UQ_menus__meta_id__menu_index` (`meta_id`,`menu_index`),
  CONSTRAINT `menus_FK_meta_id_meta` FOREIGN KEY (`meta_id`) REFERENCES `meta` (`meta_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `menus`
--

LOCK TABLES `menus` WRITE;
/*!40000 ALTER TABLE `menus` DISABLE KEYS */;
/*!40000 ALTER TABLE `menus` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `menus_history`
--

DROP TABLE IF EXISTS `menus_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `menus_history` (
  `menu_id` int(11) NOT NULL,
  `meta_id` int(11) NOT NULL,
  `menu_index` int(11) NOT NULL,
  `sort_order` int(11) NOT NULL,
  `modified_datetime` datetime NOT NULL,
  `user_id` int(11) NOT NULL,
  PRIMARY KEY (`menu_id`),
  KEY `menus_history_FK_meta_id_meta` (`meta_id`),
  CONSTRAINT `menus_history_FK_meta_id_meta` FOREIGN KEY (`meta_id`) REFERENCES `meta` (`meta_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `menus_history`
--

LOCK TABLES `menus_history` WRITE;
/*!40000 ALTER TABLE `menus_history` DISABLE KEYS */;
/*!40000 ALTER TABLE `menus_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `meta`
--

DROP TABLE IF EXISTS `meta`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `meta` (
  `meta_id` int(11) NOT NULL AUTO_INCREMENT,
  `doc_type` int(11) NOT NULL,
  `meta_headline` varchar(255) NOT NULL,
  `meta_text` varchar(1000) NOT NULL,
  `meta_image` varchar(255) NOT NULL,
  `owner_id` int(11) NOT NULL,
  `permissions` int(11) NOT NULL,
  `shared` int(11) NOT NULL,
  `show_meta` int(11) NOT NULL,
  `lang_prefix` varchar(3) NOT NULL,
  `date_created` datetime NOT NULL,
  `date_modified` datetime NOT NULL,
  `disable_search` int(11) NOT NULL,
  `target` varchar(10) NOT NULL,
  `activate` int(11) NOT NULL,
  `archived_datetime` datetime DEFAULT NULL,
  `publisher_id` int(11) DEFAULT NULL,
  `status` int(11) NOT NULL,
  `publication_start_datetime` datetime DEFAULT NULL,
  `publication_end_datetime` datetime DEFAULT NULL,
  PRIMARY KEY (`meta_id`),
  KEY `meta_FK_owner_id_users` (`owner_id`),
  KEY `meta_FK_publisher_id_users` (`publisher_id`),
  CONSTRAINT `meta_FK_publisher_id_users` FOREIGN KEY (`publisher_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `meta_FK_owner_id_users` FOREIGN KEY (`owner_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1002 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `meta`
--

LOCK TABLES `meta` WRITE;
/*!40000 ALTER TABLE `meta` DISABLE KEYS */;
INSERT INTO `meta` VALUES (1001,2,'Start page','','',1,0,0,0,'eng','2010-04-07 16:25:53','2010-04-07 16:25:53',0,'_self',1,NULL,NULL,2,'2010-04-07 16:25:53',NULL);
/*!40000 ALTER TABLE `meta` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `meta_classification`
--

DROP TABLE IF EXISTS `meta_classification`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `meta_classification` (
  `meta_id` int(11) NOT NULL,
  `class_id` int(11) NOT NULL,
  PRIMARY KEY (`meta_id`,`class_id`),
  KEY `meta_classification_FK_class_id_classification` (`class_id`),
  CONSTRAINT `meta_classification_FK_class_id_classification` FOREIGN KEY (`class_id`) REFERENCES `classification` (`class_id`),
  CONSTRAINT `meta_classification_FK_meta_id_meta` FOREIGN KEY (`meta_id`) REFERENCES `meta` (`meta_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `meta_classification`
--

LOCK TABLES `meta_classification` WRITE;
/*!40000 ALTER TABLE `meta_classification` DISABLE KEYS */;
/*!40000 ALTER TABLE `meta_classification` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `meta_section`
--

DROP TABLE IF EXISTS `meta_section`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `meta_section` (
  `meta_id` int(11) NOT NULL,
  `section_id` int(11) NOT NULL,
  PRIMARY KEY (`meta_id`,`section_id`),
  KEY `meta_section_FK_section_id_sections` (`section_id`),
  CONSTRAINT `meta_section_FK_section_id_sections` FOREIGN KEY (`section_id`) REFERENCES `sections` (`section_id`),
  CONSTRAINT `meta_section_FK_meta_id_meta` FOREIGN KEY (`meta_id`) REFERENCES `meta` (`meta_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `meta_section`
--

LOCK TABLES `meta_section` WRITE;
/*!40000 ALTER TABLE `meta_section` DISABLE KEYS */;
/*!40000 ALTER TABLE `meta_section` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mime_types`
--

DROP TABLE IF EXISTS `mime_types`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mime_types` (
  `mime_id` int(11) NOT NULL,
  `mime_name` varchar(50) NOT NULL,
  `mime` varchar(50) NOT NULL,
  `lang_prefix` varchar(3) NOT NULL DEFAULT 'swe',
  PRIMARY KEY (`mime_id`,`lang_prefix`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mime_types`
--

LOCK TABLES `mime_types` WRITE;
/*!40000 ALTER TABLE `mime_types` DISABLE KEYS */;
INSERT INTO `mime_types` VALUES (0,'Other...','other','eng'),(0,'Annan...','other','swe'),(1,'Plain text','text/plain','eng'),(1,'Vanlig text','text/plain','swe'),(2,'HTML-document','text/html','eng'),(2,'HTML-dokument','text/html','swe'),(3,'Binary file','application/octet-stream','eng'),(3,'Bin�rfil','application/octet-stream','swe'),(4,'Shockwave Flash','application/x-shockwave-flash','eng'),(4,'Shockwave Flash','application/x-shockwave-flash','swe'),(5,'Shockwave Director','application/x-director','eng'),(5,'Shockwave Director','application/x-director','swe'),(6,'PNG-image','image/png','eng'),(6,'PNG-bild','image/png','swe'),(7,'GIF-image','image/gif','eng'),(7,'GIF-bild','image/gif','swe'),(8,'JPEG-image','image/jpeg','eng'),(8,'JPEG-bild','image/jpeg','swe'),(9,'Adobe Acrobat-document','application/pdf','eng'),(9,'Adobe Acrobat-dokument','application/pdf','swe'),(10,'Wav-sound','audio/x-wav','eng'),(10,'Wav-ljud','audio/x-wav','swe'),(11,'Zip-file','application/zip','eng'),(11,'Zip-fil','application/zip','swe'),(12,'AVI-movie','video/x-msvideo','eng'),(12,'AVI-film','video/x-msvideo','swe'),(13,'Quicktime-movie','video/quicktime','eng'),(13,'Quicktime-film','video/quicktime','swe'),(14,'MPEG-movie','video/mpeg','eng'),(14,'MPEG-film','video/mpeg','swe'),(15,'MS Word-document','application/msword','eng'),(15,'MS Word-dokument','application/msword','swe'),(16,'MS Excel-document','application/vnd.ms-excel','eng'),(16,'MS Excel-dokument','application/vnd.ms-excel','swe'),(17,'MS Powerpoint-document','application/vnd.ms-powerpoint','eng'),(17,'MS Powerpoint-dokument','application/vnd.ms-powerpoint','swe');
/*!40000 ALTER TABLE `mime_types` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `new_doc_permission_sets`
--

DROP TABLE IF EXISTS `new_doc_permission_sets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `new_doc_permission_sets` (
  `meta_id` int(11) NOT NULL,
  `set_id` int(11) NOT NULL,
  `permission_id` int(11) NOT NULL,
  PRIMARY KEY (`meta_id`,`set_id`),
  KEY `ndps_ps` (`set_id`),
  CONSTRAINT `ndps_meta` FOREIGN KEY (`meta_id`) REFERENCES `meta` (`meta_id`),
  CONSTRAINT `ndps_ps` FOREIGN KEY (`set_id`) REFERENCES `permission_sets` (`set_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `new_doc_permission_sets`
--

LOCK TABLES `new_doc_permission_sets` WRITE;
/*!40000 ALTER TABLE `new_doc_permission_sets` DISABLE KEYS */;
/*!40000 ALTER TABLE `new_doc_permission_sets` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `new_doc_permission_sets_ex`
--

DROP TABLE IF EXISTS `new_doc_permission_sets_ex`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `new_doc_permission_sets_ex` (
  `meta_id` int(11) NOT NULL,
  `set_id` int(11) NOT NULL,
  `permission_id` int(11) NOT NULL,
  `permission_data` int(11) NOT NULL,
  PRIMARY KEY (`meta_id`,`set_id`,`permission_id`,`permission_data`),
  KEY `ndpse_ps` (`set_id`),
  CONSTRAINT `ndpse_ndps` FOREIGN KEY (`meta_id`, `set_id`) REFERENCES `new_doc_permission_sets` (`meta_id`, `set_id`),
  CONSTRAINT `ndpse_ps` FOREIGN KEY (`set_id`) REFERENCES `permission_sets` (`set_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `new_doc_permission_sets_ex`
--

LOCK TABLES `new_doc_permission_sets_ex` WRITE;
/*!40000 ALTER TABLE `new_doc_permission_sets_ex` DISABLE KEYS */;
/*!40000 ALTER TABLE `new_doc_permission_sets_ex` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `permission_sets`
--

DROP TABLE IF EXISTS `permission_sets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `permission_sets` (
  `set_id` int(11) NOT NULL,
  `description` varchar(30) NOT NULL,
  PRIMARY KEY (`set_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `permission_sets`
--

LOCK TABLES `permission_sets` WRITE;
/*!40000 ALTER TABLE `permission_sets` DISABLE KEYS */;
INSERT INTO `permission_sets` VALUES (0,'Full'),(1,'Begr�nsad 1'),(2,'Begr�nsad 2'),(3,'L�s');
/*!40000 ALTER TABLE `permission_sets` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `permissions`
--

DROP TABLE IF EXISTS `permissions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `permissions` (
  `permission_id` smallint(6) NOT NULL,
  `lang_prefix` varchar(3) NOT NULL DEFAULT 'swe',
  `description` varchar(50) NOT NULL,
  PRIMARY KEY (`permission_id`,`lang_prefix`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `permissions`
--

LOCK TABLES `permissions` WRITE;
/*!40000 ALTER TABLE `permissions` DISABLE KEYS */;
INSERT INTO `permissions` VALUES (1,'eng','Edit headline'),(1,'swe','�ndra rubrik'),(2,'eng','Edit docinfo'),(2,'swe','�ndra dokinfo'),(4,'eng','Edit permissions'),(4,'swe','�ndra r�ttigheter f�r roller'),(8,'eng','Create document'),(8,'swe','Skapa dokument');
/*!40000 ALTER TABLE `permissions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `phones`
--

DROP TABLE IF EXISTS `phones`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `phones` (
  `phone_id` int(11) NOT NULL AUTO_INCREMENT,
  `number` varchar(25) NOT NULL,
  `user_id` int(11) NOT NULL,
  `phonetype_id` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`phone_id`,`user_id`),
  KEY `phones_FK_user_id_users` (`user_id`),
  CONSTRAINT `phones_FK_user_id_users` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `phones`
--

LOCK TABLES `phones` WRITE;
/*!40000 ALTER TABLE `phones` DISABLE KEYS */;
/*!40000 ALTER TABLE `phones` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `phonetypes`
--

DROP TABLE IF EXISTS `phonetypes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `phonetypes` (
  `phonetype_id` int(11) NOT NULL,
  `typename` varchar(12) NOT NULL,
  `lang_id` int(11) NOT NULL,
  PRIMARY KEY (`phonetype_id`,`lang_id`),
  KEY `phonetypes_FK_lang_id_lang_prefixes` (`lang_id`),
  CONSTRAINT `phonetypes_FK_lang_id_lang_prefixes` FOREIGN KEY (`lang_id`) REFERENCES `lang_prefixes` (`lang_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `phonetypes`
--

LOCK TABLES `phonetypes` WRITE;
/*!40000 ALTER TABLE `phonetypes` DISABLE KEYS */;
INSERT INTO `phonetypes` VALUES (0,'Annat',1),(0,'Other',2),(1,'Bostad',1),(1,'Home',2),(2,'Arbete',1),(2,'Work',2),(3,'Mobil',1),(3,'Mobile',2),(4,'Fax',1),(4,'Fax',2);
/*!40000 ALTER TABLE `phonetypes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `profiles`
--

DROP TABLE IF EXISTS `profiles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `profiles` (
  `profile_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `document_name` varchar(255) NOT NULL,
  PRIMARY KEY (`profile_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `profiles`
--

LOCK TABLES `profiles` WRITE;
/*!40000 ALTER TABLE `profiles` DISABLE KEYS */;
/*!40000 ALTER TABLE `profiles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `roles` (
  `role_id` int(11) NOT NULL AUTO_INCREMENT,
  `role_name` varchar(60) NOT NULL,
  `permissions` int(11) NOT NULL DEFAULT '0',
  `admin_role` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`role_id`),
  UNIQUE KEY `UQ_roles__role_name` (`role_name`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roles`
--

LOCK TABLES `roles` WRITE;
/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
INSERT INTO `roles` VALUES (0,'Superadmin',0,1),(1,'Useradmin',0,2),(2,'Users',1,0);
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roles_rights`
--

DROP TABLE IF EXISTS `roles_rights`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `roles_rights` (
  `role_id` int(11) NOT NULL,
  `meta_id` int(11) NOT NULL,
  `set_id` smallint(6) NOT NULL,
  PRIMARY KEY (`role_id`,`meta_id`),
  KEY `roles_rights_FK_meta_id_meta` (`meta_id`),
  CONSTRAINT `roles_rights_FK_meta_id_meta` FOREIGN KEY (`meta_id`) REFERENCES `meta` (`meta_id`),
  CONSTRAINT `roles_rights_FK_role_id_roles` FOREIGN KEY (`role_id`) REFERENCES `roles` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roles_rights`
--

LOCK TABLES `roles_rights` WRITE;
/*!40000 ALTER TABLE `roles_rights` DISABLE KEYS */;
INSERT INTO `roles_rights` VALUES (2,1001,3);
/*!40000 ALTER TABLE `roles_rights` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sections`
--

DROP TABLE IF EXISTS `sections`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sections` (
  `section_id` int(11) NOT NULL AUTO_INCREMENT,
  `section_name` varchar(50) NOT NULL,
  PRIMARY KEY (`section_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sections`
--

LOCK TABLES `sections` WRITE;
/*!40000 ALTER TABLE `sections` DISABLE KEYS */;
/*!40000 ALTER TABLE `sections` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `stats`
--

DROP TABLE IF EXISTS `stats`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `stats` (
  `name` varchar(120) NOT NULL,
  `num` int(11) NOT NULL,
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `stats`
--

LOCK TABLES `stats` WRITE;
/*!40000 ALTER TABLE `stats` DISABLE KEYS */;
/*!40000 ALTER TABLE `stats` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_data`
--

DROP TABLE IF EXISTS `sys_data`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sys_data` (
  `sys_id` smallint(6) NOT NULL AUTO_INCREMENT,
  `type_id` smallint(6) NOT NULL,
  `value` varchar(1000) DEFAULT NULL,
  PRIMARY KEY (`sys_id`,`type_id`),
  KEY `sys_data_FK_type_id_sys_types` (`type_id`),
  CONSTRAINT `sys_data_FK_type_id_sys_types` FOREIGN KEY (`type_id`) REFERENCES `sys_types` (`type_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_data`
--

LOCK TABLES `sys_data` WRITE;
/*!40000 ALTER TABLE `sys_data` DISABLE KEYS */;
INSERT INTO `sys_data` VALUES (0,0,'1001'),(1,1,'1'),(2,2,'2010-04-07 16:25:53'),(3,3,''),(4,4,''),(5,5,''),(6,6,''),(7,7,'');
/*!40000 ALTER TABLE `sys_data` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_types`
--

DROP TABLE IF EXISTS `sys_types`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sys_types` (
  `type_id` smallint(6) NOT NULL,
  `name` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_types`
--

LOCK TABLES `sys_types` WRITE;
/*!40000 ALTER TABLE `sys_types` DISABLE KEYS */;
INSERT INTO `sys_types` VALUES (0,'StartDocument'),(1,'SessionCounter'),(2,'SessionCounterDate'),(3,'SystemMessage'),(4,'ServerMaster'),(5,'ServerMasterAddress'),(6,'WebMaster'),(7,'WebMasterAddress');
/*!40000 ALTER TABLE `sys_types` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `templategroups`
--

DROP TABLE IF EXISTS `templategroups`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `templategroups` (
  `group_id` int(11) NOT NULL AUTO_INCREMENT,
  `group_name` varchar(50) NOT NULL,
  PRIMARY KEY (`group_id`),
  UNIQUE KEY `UQ_templategroups__group_name` (`group_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `templategroups`
--

LOCK TABLES `templategroups` WRITE;
/*!40000 ALTER TABLE `templategroups` DISABLE KEYS */;
INSERT INTO `templategroups` VALUES (0,'normal');
/*!40000 ALTER TABLE `templategroups` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `templates_cref`
--

DROP TABLE IF EXISTS `templates_cref`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `templates_cref` (
  `group_id` int(11) NOT NULL,
  `template_name` varchar(255) NOT NULL,
  PRIMARY KEY (`group_id`,`template_name`),
  CONSTRAINT `templates_cref_FK_group_id_templategroups` FOREIGN KEY (`group_id`) REFERENCES `templategroups` (`group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `templates_cref`
--

LOCK TABLES `templates_cref` WRITE;
/*!40000 ALTER TABLE `templates_cref` DISABLE KEYS */;
INSERT INTO `templates_cref` VALUES (0,'demo');
/*!40000 ALTER TABLE `templates_cref` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `text_docs`
--

DROP TABLE IF EXISTS `text_docs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `text_docs` (
  `meta_id` int(11) NOT NULL,
  `template_name` varchar(255) NOT NULL,
  `group_id` int(11) NOT NULL DEFAULT '1',
  `default_template_1` varchar(255) DEFAULT NULL,
  `default_template_2` varchar(255) DEFAULT NULL,
  `default_template` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`meta_id`),
  CONSTRAINT `text_docs_FK_meta_id_meta` FOREIGN KEY (`meta_id`) REFERENCES `meta` (`meta_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `text_docs`
--

LOCK TABLES `text_docs` WRITE;
/*!40000 ALTER TABLE `text_docs` DISABLE KEYS */;
INSERT INTO `text_docs` VALUES (1001,'demo',0,NULL,NULL,NULL);
/*!40000 ALTER TABLE `text_docs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `texts`
--

DROP TABLE IF EXISTS `texts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `texts` (
  `meta_id` int(11) NOT NULL,
  `name` int(11) NOT NULL,
  `text` longtext NOT NULL,
  `type` int(11) DEFAULT NULL,
  `counter` int(11) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`counter`),
  KEY `texts_FK_meta_id_meta` (`meta_id`),
  CONSTRAINT `texts_FK_meta_id_meta` FOREIGN KEY (`meta_id`) REFERENCES `meta` (`meta_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `texts`
--

LOCK TABLES `texts` WRITE;
/*!40000 ALTER TABLE `texts` DISABLE KEYS */;
INSERT INTO `texts` VALUES (1001,1,'<h3>Welcome to imCMS</h3>',1,1),(1001,2,'<a href=\"<?imcms:contextpath?>/login/\" title=\"Link to login-page\"><b>Log in!</b></a><br><br><br><a href=\"<?imcms:contextpath?>/servlet/SearchDocuments\" target=\"_blank\" title=\"Link to search-page\"><b>Search-page</b></a><br>Opens in new window.<br><br><a href=\"http://doc.imcms.net//\" target=\"_blank\" title=\"Link to documentation-site\"><b>Documentation</b></a><br>External site, opens in new window.<br><br><a href=\"http://www.imcms.net\" target=\"_blank\" title=\"Link to www.imcms.net\"><b>More about imCMS</b></a><br>imCMS Product-site. External site, opens in new window.<br><br><a href=\"<?imcms:contextpath?>/imcms/docs/\" target=\"_blank\" title=\"Link to included documentation.\"><b>Included documentation.</b></a><br>For administrators and developers, in english. Opens in new window.<br><br><a href=\"<?imcms:contextpath?>/imcms/docs/apisamples/\" target=\"_blank\" title=\"Link to API-samples\"><b>API examples</b></a><br>Only for developers, in english. Opens in new window.<br>Note! Only to be used in test environment - not sites for public use.',1,2);
/*!40000 ALTER TABLE `texts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `texts_history`
--

DROP TABLE IF EXISTS `texts_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `texts_history` (
  `meta_id` int(11) NOT NULL,
  `name` int(11) NOT NULL,
  `text` longtext NOT NULL,
  `type` int(11) DEFAULT NULL,
  `modified_datetime` datetime NOT NULL,
  `user_id` int(11) NOT NULL,
  `counter` int(11) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`counter`),
  KEY `texts_history_FK_meta_id_meta` (`meta_id`),
  KEY `texts_history_FK_user_id_users` (`user_id`),
  CONSTRAINT `texts_history_FK_user_id_users` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `texts_history_FK_meta_id_meta` FOREIGN KEY (`meta_id`) REFERENCES `meta` (`meta_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `texts_history`
--

LOCK TABLES `texts_history` WRITE;
/*!40000 ALTER TABLE `texts_history` DISABLE KEYS */;
INSERT INTO `texts_history` VALUES (1001,1,'<h3>Welcome to imCMS</h3>',1,'2010-04-07 16:25:53',1,1),(1001,2,'<a href=\"<?imcms:contextpath?>/login/\" title=\"Link to login-page\"><b>Log in!</b></a><br><br><br><a href=\"<?imcms:contextpath?>/servlet/SearchDocuments\" target=\"_blank\" title=\"Link to search-page\"><b>Search-page</b></a><br>Opens in new window.<br><br><a href=\"http://doc.imcms.net//\" target=\"_blank\" title=\"Link to documentation-site\"><b>Documentation</b></a><br>External site, opens in new window.<br><br><a href=\"http://www.imcms.net\" target=\"_blank\" title=\"Link to www.imcms.net\"><b>More about imCMS</b></a><br>imCMS Product-site. External site, opens in new window.<br><br><a href=\"<?imcms:contextpath?>/imcms/docs/\" target=\"_blank\" title=\"Link to included documentation.\"><b>Included documentation.</b></a><br>For administrators and developers, in english. Opens in new window.<br><br><a href=\"<?imcms:contextpath?>/imcms/docs/apisamples/\" target=\"_blank\" title=\"Link to API-samples\"><b>API examples</b></a><br>Only for developers, in english. Opens in new window.<br>Note! Only to be used in test environment - not sites for public use.',1,'2010-04-07 16:25:53',1,2);
/*!40000 ALTER TABLE `texts_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `url_docs`
--

DROP TABLE IF EXISTS `url_docs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `url_docs` (
  `meta_id` int(11) NOT NULL,
  `frame_name` varchar(80) NOT NULL,
  `target` varchar(15) NOT NULL,
  `url_ref` varchar(255) NOT NULL,
  `url_txt` varchar(255) NOT NULL,
  `lang_prefix` varchar(3) NOT NULL,
  PRIMARY KEY (`meta_id`,`lang_prefix`),
  CONSTRAINT `url_docs_FK_meta_id_meta` FOREIGN KEY (`meta_id`) REFERENCES `meta` (`meta_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `url_docs`
--

LOCK TABLES `url_docs` WRITE;
/*!40000 ALTER TABLE `url_docs` DISABLE KEYS */;
/*!40000 ALTER TABLE `url_docs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_rights`
--

DROP TABLE IF EXISTS `user_rights`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_rights` (
  `user_id` int(11) NOT NULL,
  `meta_id` int(11) NOT NULL,
  `permission_id` smallint(6) NOT NULL,
  PRIMARY KEY (`user_id`,`meta_id`,`permission_id`),
  KEY `user_rights_FK_meta_id_meta` (`meta_id`),
  CONSTRAINT `user_rights_FK_meta_id_meta` FOREIGN KEY (`meta_id`) REFERENCES `meta` (`meta_id`),
  CONSTRAINT `user_rights_FK_user_id_users` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_rights`
--

LOCK TABLES `user_rights` WRITE;
/*!40000 ALTER TABLE `user_rights` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_rights` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_roles_crossref`
--

DROP TABLE IF EXISTS `user_roles_crossref`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_roles_crossref` (
  `user_id` int(11) NOT NULL,
  `role_id` int(11) NOT NULL,
  PRIMARY KEY (`user_id`,`role_id`),
  KEY `user_roles_crossref_FK_role_id_roles` (`role_id`),
  CONSTRAINT `user_roles_crossref_FK_role_id_roles` FOREIGN KEY (`role_id`) REFERENCES `roles` (`role_id`),
  CONSTRAINT `user_roles_crossref_FK_user_id_users` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_roles_crossref`
--

LOCK TABLES `user_roles_crossref` WRITE;
/*!40000 ALTER TABLE `user_roles_crossref` DISABLE KEYS */;
INSERT INTO `user_roles_crossref` VALUES (1,0),(2,2);
/*!40000 ALTER TABLE `user_roles_crossref` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `useradmin_role_crossref`
--

DROP TABLE IF EXISTS `useradmin_role_crossref`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `useradmin_role_crossref` (
  `user_id` int(11) NOT NULL,
  `role_id` int(11) NOT NULL,
  PRIMARY KEY (`user_id`,`role_id`),
  KEY `useradmin_role_crossref_FK_role_id_roles` (`role_id`),
  CONSTRAINT `useradmin_role_crossref_FK_user_id_users` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `useradmin_role_crossref_FK_role_id_roles` FOREIGN KEY (`role_id`) REFERENCES `roles` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `useradmin_role_crossref`
--

LOCK TABLES `useradmin_role_crossref` WRITE;
/*!40000 ALTER TABLE `useradmin_role_crossref` DISABLE KEYS */;
/*!40000 ALTER TABLE `useradmin_role_crossref` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `users` (
  `user_id` int(11) NOT NULL AUTO_INCREMENT,
  `login_name` varchar(128) NOT NULL,
  `login_password` varchar(15) NOT NULL,
  `first_name` varchar(64) NOT NULL,
  `last_name` varchar(64) NOT NULL,
  `title` varchar(64) NOT NULL,
  `company` varchar(64) NOT NULL,
  `address` varchar(128) NOT NULL,
  `city` varchar(64) NOT NULL,
  `zip` varchar(64) NOT NULL,
  `country` varchar(64) NOT NULL,
  `county_council` varchar(128) NOT NULL,
  `email` varchar(128) NOT NULL,
  `external` int(11) NOT NULL,
  `active` int(11) NOT NULL DEFAULT '1',
  `create_date` datetime NOT NULL,
  `language` varchar(3) NOT NULL,
  `session_id` varchar(128) DEFAULT NULL,
  `remember_cd` varchar(40) DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `UQ__users__login_name` (`login_name`),
  UNIQUE KEY `users_remember_cd_unq` (`remember_cd`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'admin','admin','Admin','Super','','','','','','','','',0,1,'2010-04-07 16:25:53','eng',NULL,NULL),(2,'user','user','User','Extern','','','','','','','','',0,1,'2010-04-07 16:25:53','eng',NULL,NULL);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2010-04-07 16:29:31
