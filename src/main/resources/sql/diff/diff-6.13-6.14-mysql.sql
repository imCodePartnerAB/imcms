DELIMITER $$

CREATE PROCEDURE imcms_schema_update_6_13_to_6_14()
BEGIN
  DECLARE schema_version VARCHAR(8);
  DECLARE column_exists BOOLEAN;
  DECLARE error_message_text VARCHAR(1024);

  -- check schema version equals to 6.13
  SELECT concat(major, '.', minor) INTO schema_version FROM database_version;
  IF NOT schema_version <=> '6.13' THEN
    SET error_message_text = concat('Invalid schema version. Expected: 6.13, actual: ', ifnull(schema_version, 'null'));
    SIGNAL SQLSTATE 'ERROR' SET MESSAGE_TEXT = error_message_text;
  END IF;

  -- add column imcms_text_doc_images.resize if not exists
  SET column_exists = EXISTS(
      SELECT 1
      FROM information_schema.COLUMNS
      WHERE table_schema = database()
            AND table_name = 'imcms_text_doc_images'
            AND column_name = 'resize'
  );

  IF NOT column_exists THEN
    ALTER TABLE imcms_text_doc_images
    ADD COLUMN resize INT NOT NULL DEFAULT 0;
  END IF;

  -- add column imcms_text_doc_images_history.resize if not exists
  SET column_exists = EXISTS(
      SELECT 1
      FROM information_schema.COLUMNS
      WHERE table_schema = database()
            AND table_name = 'imcms_text_doc_images_history'
            AND column_name = 'resize'
  );

  IF NOT column_exists THEN
    ALTER TABLE imcms_text_doc_images_history
    ADD COLUMN resize INT NOT NULL DEFAULT 0;
  END IF;

  -- update schema version to 6.14
  UPDATE database_version SET major = 6, minor = 14;
END;
$$

DELIMITER ;

CALL imcms_schema_update_6_13_to_6_14;
DROP PROCEDURE imcms_schema_update_6_13_to_6_14;