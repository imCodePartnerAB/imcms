INSERT INTO imcms_text_doc_menus
  (`id`, `doc_id`, `doc_version_no`, `no`, `sort_order`)
VALUES
  (1, 1001, 0, 1, 1),
  (2, 1001, 0, 2, 1),
  (3, 1001, 0, 3, 1);


INSERT INTO imcms_text_doc_menu_items
  (`menu_id`, `to_doc_id`, `manual_sort_order`, `tree_sort_index`)
VALUES
  (1, 1002, 3, 3),
  (2, 1003, 3, 3),
  (3, 1004, 3, 3);