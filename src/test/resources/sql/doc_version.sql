-- Doc id 1001 has three versions 1,2,3 created by an admin
INSERT INTO imcms_doc_versions (doc_id, no, created_by, created_dt, modified_by, modified_dt) VALUES
  (1001, 0, 0, now(), 0, now()),
  (1001, 1, 0, now(), 0, now()),
  (1001, 2, 0, now(), 0, now());


INSERT INTO meta (
  meta_id,
  activate,
  date_created,
  owner_id,
  default_version_no,
  disabled_language_show_rule,
  doc_type,
  lang_prefix,
  shared,
  show_meta,
  date_modified,
  permissions,
  disable_search,
  target
) VALUES (
  1001, 0, now(), 0, 0, 'DO_NOT_SHOW', 0, '', 0, 0, now(), 0, 0, ''
);  