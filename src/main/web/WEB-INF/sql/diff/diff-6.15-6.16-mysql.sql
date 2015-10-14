-- normalizes imcms_text_doc_contents table
-- creates view on imcms_text_doc_contents

SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 16;


DROP PROCEDURE IF EXISTS dropForeignKeysFromTable;
DROP PROCEDURE IF EXISTS migration_6_16;

DELIMITER ///

CREATE PROCEDURE dropForeignKeysFromTable()
  BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE dropCommand VARCHAR(255);
    DECLARE createCommand VARCHAR(255);
    DECLARE dropCur CURSOR FOR
      SELECT DISTINCT
        CONCAT("ALTER TABLE `", `K`.`TABLE_NAME`, "` DROP FOREIGN KEY `", `K`.`CONSTRAINT_NAME`, "`;") 'DROP'
      FROM `information_schema`.`KEY_COLUMN_USAGE` `K`
        LEFT JOIN `information_schema`.`REFERENTIAL_CONSTRAINTS` `C` USING (`CONSTRAINT_NAME`)
      WHERE `K`.`REFERENCED_TABLE_SCHEMA` = SCHEMA()
            AND `K`.`REFERENCED_TABLE_NAME` = "fileupload_docs";


    DECLARE createCur CURSOR FOR
      SELECT DISTINCT *
      FROM `temp_fk_view`;

    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

    IF exists(SELECT *
              FROM information_schema.columns
              WHERE table_name = 'fileupload_docs' AND column_name = 'meta_id' AND table_schema = schema())
    THEN


      SET @query = "DROP TABLE IF EXISTS temp_fk_view;";
      SELECT @query;
      PREPARE stmt FROM @query;
      EXECUTE stmt;
      DEALLOCATE PREPARE stmt;
      SET @query = "CREATE TABLE temp_fk_view (`query` varchar(21844));";
      SELECT @query;
      PREPARE stmt FROM @query;
      EXECUTE stmt;
      DEALLOCATE PREPARE stmt;
      SET @query = "INSERT INTO temp_fk_view SELECT DISTINCT CONCAT('ALTER TABLE `',`K`.`TABLE_NAME`,'` ADD CONSTRAINT ','`fk_',`K`.`TABLE_NAME`,'_',`K`.`REFERENCED_TABLE_NAME`,'1','` FOREIGN KEY (`',`K`.`COLUMN_NAME`,'`) REFERENCES ','`',`K`.`REFERENCED_TABLE_SCHEMA`,'`.`',`K`.`REFERENCED_TABLE_NAME`,'` (`',IF(`K`.`REFERENCED_COLUMN_NAME`='meta_id', 'doc_id', `K`.`REFERENCED_COLUMN_NAME`),'`) ON DELETE ',`C`.`DELETE_RULE`,' ON UPDATE ',`C`.`UPDATE_RULE`,';') 'CREATE' FROM `information_schema`.`KEY_COLUMN_USAGE` `K` LEFT JOIN `information_schema`.`REFERENTIAL_CONSTRAINTS` `C` USING (`CONSTRAINT_NAME`) WHERE `K`.`REFERENCED_TABLE_SCHEMA` = SCHEMA() AND `K`.`REFERENCED_TABLE_NAME` = 'fileupload_docs';";
      SELECT @query;
      PREPARE stmt FROM @query;
      EXECUTE stmt;
      DEALLOCATE PREPARE stmt;

      OPEN dropCur;

      read_loop: LOOP
        FETCH dropCur
        INTO dropCommand;
        IF done
        THEN
          LEAVE read_loop;
        END IF;

        SET @sdropCommand = dropCommand;

        PREPARE dropClientUpdateKeyStmt FROM @sdropCommand;

        EXECUTE dropClientUpdateKeyStmt;

        DEALLOCATE PREPARE dropClientUpdateKeyStmt;
      END LOOP;

      CLOSE dropCur;

      DROP TABLE IF EXISTS `fileupload_docs_temporary`;

      CREATE TABLE `fileupload_docs_temporary` (
        `id`               INT(11)      NOT NULL AUTO_INCREMENT,
        `doc_id`           INT(11)      NOT NULL,
        `doc_version_no`   INT(11)      NOT NULL,
        `variant_name`     VARCHAR(100) NOT NULL,
        `filename`         VARCHAR(255) NOT NULL,
        `mime`             VARCHAR(50)  NOT NULL,
        `created_as_image` INT(11)      NOT NULL,
        `default_variant`  TINYINT(1)   NOT NULL DEFAULT '0',
        PRIMARY KEY (`id`),
        UNIQUE KEY `uk__fileupload_docs__1` (`doc_id`, `doc_version_no`, `variant_name`),
        CONSTRAINT `fk__fileupload_docs__doc_version` FOREIGN KEY (`doc_id`, `doc_version_no`)
        REFERENCES `imcms_doc_versions` (`doc_id`, `no`)
          ON DELETE CASCADE
      )
        ENGINE = INNODB
        AUTO_INCREMENT = 46
        DEFAULT CHARSET = UTF8;


      INSERT INTO fileupload_docs_temporary
        SELECT
          fd.id,
          (fd.meta_id) 'doc_id',
          fd.doc_version_no,
          fd.variant_name,
          fd.filename,
          fd.mime,
          fd.created_as_image,
          fd.default_variant
        FROM fileupload_docs fd;

      DROP TABLE fileupload_docs;

      RENAME TABLE fileupload_docs_temporary TO fileupload_docs;

      SET done = FALSE;


      OPEN createCur;

      write_loop: LOOP
        FETCH createCur
        INTO createCommand;
        IF done
        THEN
          LEAVE write_loop;
        END IF;

        SET @screateCommand = createCommand;

        PREPARE createClientUpdateKeyStmt FROM @screateCommand;

        EXECUTE createClientUpdateKeyStmt;

        DEALLOCATE PREPARE createClientUpdateKeyStmt;
      END LOOP;

      CLOSE createCur;
    END IF;
  END///


CREATE PROCEDURE migration_6_16
  (
  )
  BEGIN
    IF exists(SELECT *
              FROM information_schema.TABLE_CONSTRAINTS
              WHERE
                CONSTRAINT_TYPE = 'FOREIGN KEY'
                AND TABLE_SCHEMA = SCHEMA()
                AND TABLE_NAME = 'fileupload_docs'
                AND CONSTRAINT_NAME = 'fk__fileupload_docs__doc_version')
    THEN
      ALTER TABLE fileupload_docs
      DROP FOREIGN KEY fk__fileupload_docs__doc_version;
    END IF;
  END/// -- end of stored procedure block

DELIMITER ;
-- switch delimiters again

CALL migration_6_16();
CALL dropForeignKeysFromTable();

DROP PROCEDURE IF EXISTS migration_6_16;
DROP PROCEDURE IF EXISTS dropForeignKeysFromTable;

--
-- Update schema version
--
UPDATE database_version
SET
  major = @schema_version__major_new,
  minor = @schema_version__minor_new;
