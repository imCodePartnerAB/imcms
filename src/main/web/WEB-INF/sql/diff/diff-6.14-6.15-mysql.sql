-- normalizes imcms_text_doc_contents table
-- creates view on imcms_text_doc_contents

SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 15;

DROP PROCEDURE IF EXISTS migration_6_15;
DROP PROCEDURE IF EXISTS dropForeignKeysFromTable;

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
            AND `K`.`REFERENCED_TABLE_NAME` = "imcms_text_doc_contents";

    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

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
  END///

DELIMITER #

CREATE PROCEDURE migration_6_15
  (
  )
  BEGIN
    IF exists(SELECT *
              FROM information_schema.columns
              WHERE table_name = 'imcms_text_doc_content_loops' AND column_name = 'version')
    THEN
      ALTER TABLE imcms_text_doc_content_loops
      DROP COLUMN version;

      DROP TABLE IF EXISTS imcms_text_doc_contents_new;

      CREATE TABLE imcms_text_doc_contents_new (
        id      SERIAL,
        loop_id INT     NOT NULL,
        no      INT     NOT NULL,
        ix      INT     NOT NULL,
        enabled BOOLEAN NOT NULL DEFAULT TRUE,
        CONSTRAINT fk__content_loop FOREIGN KEY (loop_id) REFERENCES imcms_text_doc_content_loops (id),
        KEY ix__content_order (ix)
      )
        ENGINE = InnoDB
        DEFAULT CHARSET = utf8;

      INSERT INTO imcms_text_doc_contents_new
        SELECT
          c.id,
          l.id,
          c.no,
          c.ix,
          c.enabled
        FROM imcms_text_doc_content_loops l JOIN
          imcms_text_doc_contents c
            ON l.doc_id = c.doc_id AND
               l.doc_version_no = c.doc_version_no AND
               l.no = c.loop_no;

      DROP TABLE imcms_text_doc_contents;
      RENAME TABLE imcms_text_doc_contents_new TO imcms_text_doc_contents;
    END IF;

    IF exists(SELECT *
              FROM information_schema.TABLE_CONSTRAINTS
              WHERE
                CONSTRAINT_TYPE = 'FOREIGN KEY'
                AND TABLE_SCHEMA = SCHEMA()
                AND TABLE_NAME = 'imcms_text_doc_images_history'
                AND CONSTRAINT_NAME = 'fk__imcms_text_doc_images_history__content')
    THEN
      -- An attempt to drop both foreign key and an index in one statement leads to server crash and data corruption.
      ALTER TABLE imcms_text_doc_images_history
      DROP FOREIGN KEY fk__imcms_text_doc_images_history__content;
    END IF;

    IF exists(SELECT *
              FROM information_schema.TABLE_CONSTRAINTS
              WHERE
                CONSTRAINT_TYPE = 'FOREIGN KEY'
                AND TABLE_SCHEMA = SCHEMA()
                AND TABLE_NAME = 'imcms_text_doc_texts_history'
                AND CONSTRAINT_NAME = 'fk__imcms_text_doc_texts_history__content')
    THEN
      -- An attempt to drop both indexed and key in one statement leads to server crash and data corruption.
      ALTER TABLE imcms_text_doc_texts_history
      DROP FOREIGN KEY fk__imcms_text_doc_texts_history__content;
    END IF;

  END# -- end of stored procedure block

DELIMITER ;
-- switch delimiters again
CALL dropForeignKeysFromTable();
CALL migration_6_15();

DROP PROCEDURE IF EXISTS dropForeignKeysFromTable;
DROP PROCEDURE IF EXISTS migration_6_15;

DROP VIEW IF EXISTS imcms_text_doc_contents_v;

CREATE VIEW imcms_text_doc_contents_v AS
  SELECT
    l.doc_id,
    l.doc_version_no,
    l.no      AS loop_no,
    c.no      AS content_no,
    c.ix      AS content_ix,
    c.enabled AS content_enabled
  FROM imcms_text_doc_content_loops l JOIN
    imcms_text_doc_contents c
      ON l.id = c.loop_id;

--
-- Update schema version
--
UPDATE database_version
SET
  major = @schema_version__major_new,
  minor = @schema_version__minor_new;



