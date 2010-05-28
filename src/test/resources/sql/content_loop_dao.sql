INSERT INTO imcms_text_doc_content_loops (doc_id, doc_version_no, no) VALUES
  -- empty loop
  (1001, 0, 0),
  -- loop with single content
  (1001, 0, 1),
  -- loop with three contents ordered asc
  (1001, 0, 2),
  -- loop with three contents ordered desc -->
  (1001, 0, 3);

    
INSERT INTO imcms_text_doc_contents (doc_id, doc_version_no, loop_no, no, order_no, enabled) VALUES
  (1001, 0, 1, 0, 0, true),

  (1001, 0, 2, 0, 0, true),
  (1001, 0, 2, 1, 1, true),
  (1001, 0, 2, 2, 2, true),

  (1001, 0, 3, 0, 2, true),
  (1001, 0, 3, 1, 1, true),
  (1001, 0, 3, 2, 0, true);