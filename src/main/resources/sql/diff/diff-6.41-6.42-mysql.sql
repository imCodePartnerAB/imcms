# moving old data of "imcms_text_doc_menus" and "imcms_text_doc_menu_items" tables
# to new "imcms_menu" and "imcms_menu_item" tables respectively

SET @schema_version__major_new = 6;
SET @schema_version__minor_new = 42;

DROP PROCEDURE IF EXISTS temp_main_procedure;
DROP PROCEDURE IF EXISTS temp_insert_menu_items_procedure;

DELETE FROM imcms_menu;
DELETE FROM imcms_menu_item;

SET max_sp_recursion_depth = 255;

DELIMITER temp_delim;

CREATE PROCEDURE temp_main_procedure()
  BEGIN
    -- declare variables
    DECLARE v_finished INTEGER DEFAULT 0;
    DECLARE v_id INTEGER DEFAULT 0;
    DECLARE v_menu_id INTEGER DEFAULT 0;
    DECLARE v_tree_sort_index VARCHAR(255) DEFAULT '';
    DECLARE integer_reg_exp VARCHAR(255) DEFAULT '^[0-9]+$';

    -- declare cursor
    DECLARE menu_items_cursor CURSOR FOR
      SELECT
        id,
        menu_id,
        tree_sort_index
      FROM imcms_text_doc_menu_items
      WHERE tree_sort_index COLLATE utf8_general_ci REGEXP integer_reg_exp;

    -- declare NOT FOUND handler
    DECLARE CONTINUE HANDLER
    FOR NOT FOUND SET v_finished = 1;

    INSERT INTO imcms_menu (id, no, doc_id, doc_version_no)
      SELECT
        id,
        no,
        doc_id,
        doc_version_no
      FROM imcms_text_doc_menus;

    INSERT INTO imcms_menu_item (id, menu_id, parent_item_id, sort_order, document_id)
      SELECT
        id,
        menu_id,
        NULL,
        CAST(tree_sort_index AS UNSIGNED),
        to_doc_id
      FROM imcms_text_doc_menu_items
      WHERE tree_sort_index COLLATE utf8_general_ci REGEXP integer_reg_exp;

    OPEN menu_items_cursor;

    loop_label: LOOP
      FETCH menu_items_cursor
      INTO v_id, v_menu_id, v_tree_sort_index;

      IF v_finished = 1
      THEN
        LEAVE loop_label;
      END IF;

      CALL temp_insert_menu_items_procedure(v_id, v_menu_id, v_tree_sort_index);

    END LOOP loop_label;

    CLOSE menu_items_cursor;

  END;

temp_delim;

CREATE PROCEDURE temp_insert_menu_items_procedure(
  IN in_parent_id       INTEGER,
  IN in_menu_id         INTEGER,
  IN in_tree_sort_index VARCHAR(255)
)

  BEGIN

    -- declare variables
    DECLARE v_finished INTEGER DEFAULT 0;
    DECLARE v_parent_id INTEGER DEFAULT 0;
    DECLARE v_tree_sort_index VARCHAR(255) DEFAULT '';
    DECLARE tree_sort_reg_exp VARCHAR(255) DEFAULT CONCAT('^', in_tree_sort_index, '.', '[0-9]+$');

    -- declare cursor
    DECLARE menu_items_cursor CURSOR FOR
      SELECT
        id,
        tree_sort_index
      FROM imcms_text_doc_menu_items
      WHERE menu_id = in_menu_id
            AND tree_sort_index COLLATE utf8_general_ci REGEXP tree_sort_reg_exp;

    -- declare NOT FOUND handler
    DECLARE CONTINUE HANDLER
    FOR NOT FOUND SET v_finished = 1;

    SET @rownum := 0;

    INSERT INTO imcms_menu_item (id, menu_id, parent_item_id, sort_order, document_id)
      SELECT
        id,
        NULL,
        in_parent_id,
        @rownum := @rownum + 1,
        to_doc_id
      FROM imcms_text_doc_menu_items
      WHERE menu_id = in_menu_id
            AND tree_sort_index COLLATE utf8_general_ci REGEXP tree_sort_reg_exp
      ORDER BY tree_sort_index;

    OPEN menu_items_cursor;

    loop_label: LOOP
      FETCH menu_items_cursor
      INTO v_parent_id, v_tree_sort_index;

      IF v_finished = 1
      THEN
        LEAVE loop_label;
      END IF;

      CALL temp_insert_menu_items_procedure(v_parent_id, in_menu_id, v_tree_sort_index);

    END LOOP loop_label;

    CLOSE menu_items_cursor;

  END;

temp_delim;

DELIMITER ;

CALL temp_main_procedure();

DROP PROCEDURE temp_main_procedure;
DROP PROCEDURE temp_insert_menu_items_procedure;

UPDATE database_version
SET
  major = @schema_version__major_new,
  minor = @schema_version__minor_new;