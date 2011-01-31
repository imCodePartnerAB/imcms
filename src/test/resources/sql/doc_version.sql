-- Doc id 1001 has three versions 1,2,3 created by an admin
INSERT INTO imcms_doc_versions (doc_id, no, created_by, created_dt, modified_by, modified_dt) VALUES
  (1001, 0, 0, now(), 0, now()),
  (1001, 1, 0, now(), 0, now()),
  (1001, 2, 0, now(), 0, now());