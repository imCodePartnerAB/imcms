# inserting menu item data from 'imcms_text_doc_menu_items' table to 'imcms_menu_item' table
# when 'tree_sort_index' field is empty

SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 45;

DROP PROCEDURE IF EXISTS temp_main_procedure;

DELIMITER temp_delim;

CREATE PROCEDURE temp_main_procedure()
  BEGIN
    -- declare variables
    DECLARE v_finished INTEGER DEFAULT 0;
    DECLARE v_menu_id INTEGER DEFAULT 0;
    DECLARE empty_reg_exp VARCHAR(255) DEFAULT '^[ ]*$';

    -- declare cursor
    DECLARE menu_items_cursor CURSOR FOR
      SELECT DISTINCT menu_id
      FROM imcms_text_doc_menu_items
      WHERE tree_sort_index COLLATE utf8_general_ci REGEXP empty_reg_exp;

    -- declare NOT FOUND handler
    DECLARE CONTINUE HANDLER
    FOR NOT FOUND SET v_finished = 1;

    OPEN menu_items_cursor;

    loop_label: LOOP
      FETCH menu_items_cursor
      INTO v_menu_id;

      IF v_finished = 1
      THEN
        LEAVE loop_label;
      END IF;

      SET @maxSortOrderByMenuId := (
        SELECT MAX(sort_order)
        FROM imcms_menu_item
        WHERE menu_id = v_menu_id
              AND parent_item_id IS NULL
      );

      IF (@maxSortOrderByMenuId IS NULL)
      THEN
        SET @maxSortOrderByMenuId = 0;
      END IF;

      SET @rownum := 0;

      INSERT INTO imcms_menu_item (id, menu_id, parent_item_id, sort_order, document_id)
        SELECT
          id,
          v_menu_id,
          NULL,
          (@rownum := @rownum + 1) + @maxSortOrderByMenuId,
          to_doc_id
        FROM imcms_text_doc_menu_items
        WHERE menu_id = v_menu_id
              AND to_doc_id <> 0
              AND tree_sort_index COLLATE utf8_general_ci REGEXP empty_reg_exp;

    END LOOP loop_label;

    CLOSE menu_items_cursor;

  END;

temp_delim;

DELIMITER ;

CALL temp_main_procedure();

DROP PROCEDURE temp_main_procedure;

UPDATE database_version
SET
  major = @schema_version__major_new,
  minor = @schema_version__minor_new;