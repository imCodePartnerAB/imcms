DROP FUNCTION IF EXISTS imcms_schema_is_table_exists;
DROP FUNCTION IF EXISTS imcms_schema_is_column_exists;
DROP PROCEDURE IF EXISTS imcms_schema_check_table_exists;
DROP PROCEDURE IF EXISTS imcms_schema_check_version;
DROP PROCEDURE IF EXISTS imcms_schema_set_version;
DROP PROCEDURE IF EXISTS imcms_schema_update_6_13_to_6_14;

DELIMITER $$

CREATE FUNCTION imcms_schema_is_table_exists(expected_table_name VARCHAR(1024)) RETURNS BOOLEAN
BEGIN
  RETURN EXISTS(
      SELECT * FROM information_schema.TABLES
      WHERE table_schema = database() AND table_name = expected_table_name
  );
END;
$$

CREATE PROCEDURE imcms_schema_check_table_exists(expected_table_name VARCHAR(1024))
BEGIN
  IF NOT imcms_schema_is_table_exists(expected_table_name) THEN
    SELECT `error: table does not exists`;
  END IF;
END;
$$

CREATE FUNCTION imcms_schema_is_column_exists(expected_table_name VARCHAR(1024), expected_column_name VARCHAR(1024)) RETURNS BOOLEAN
READS SQL DATA
BEGIN
  -- CALL imcms_schema_check_table_exists(expected_table_name);
  -- leads to [HY000][1415] Not allowed to return a result set from a function error at runtime.
  -- Workaround:
  DECLARE dummy VARCHAR(1024);

  IF NOT imcms_schema_is_table_exists(expected_table_name) THEN
    SELECT `error: table does not exists` INTO dummy;
  END IF;

  RETURN EXISTS(
      SELECT *
      FROM information_schema.COLUMNS
      WHERE table_schema = database()
            AND table_name = expected_table_name
            AND column_name = expected_column_name
  );
END;
$$

CREATE PROCEDURE imcms_schema_check_version(expected_major INT, expected_minor INT)
BEGIN
  DECLARE actual_major, actual_minor INT;

  IF NOT (actual_major = expected_major AND actual_minor = expected_minor) THEN
    SELECT `error: unexpected schema version`;
  END IF;
END;
$$

CREATE PROCEDURE imcms_schema_set_version(new_major INT, new_minor INT)
BEGIN
  UPDATE database_version SET major = new_major, minor = new_minor;
END;
$$

CREATE PROCEDURE imcms_schema_update_6_13_to_6_14()
BEGIN
  CALL imcms_schema_check_version(6, 13);

  IF NOT imcms_schema_is_column_exists('imcms_text_doc_images', 'resize') THEN
    ALTER TABLE imcms_text_doc_images
    ADD COLUMN resize INT NOT NULL DEFAULT 0;
  END IF;

  IF NOT imcms_schema_is_column_exists('imcms_text_doc_images_history', 'resize') THEN
    ALTER TABLE imcms_text_doc_images_history
    ADD COLUMN resize INT NOT NULL DEFAULT 0;
  END IF;

  CALL imcms_schema_set_version(6, 14);
END;
$$

DELIMITER ;

CALL imcms_schema_update_6_13_to_6_14;