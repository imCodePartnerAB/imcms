-- 3 menus on doc 1001
-- menu with no
--   0 has 0 menu items
--   1 has 1 menu item
--   2 has 2 menu items
--   3 has 3 menu items

INSERT INTO imcms_text_doc_menus
  (`id`, `doc_id`, `doc_version_no`, `no`, `sort_order`)
VALUES
  (1, 1001, 0, 0, 1),
  (2, 1001, 0, 1, 1),
  (3, 1001, 0, 2, 1),
  (4, 1001, 0, 3, 1);

INSERT INTO imcms_text_doc_menu_items
  (`menu_id`, `to_doc_id`, `manual_sort_order`, `tree_sort_index`)
VALUES
  (2, 1002, 3, 3),

  (3, 1003, 3, 3),
  (3, 1004, 3, 3),

  (4, 1005, 3, 3),
  (4, 1006, 3, 3),
  (4, 1007, 3, 3);