INSERT INTO meta (meta_id, description, doc_type, meta_headline, meta_text, meta_image, owner_id, permissions, shared, expand, show_meta, help_text_id, archive, status_id, lang_prefix, classification, date_created, date_modified, sort_position, menu_position, disable_search, activated_datetime, archived_datetime, target,  frame_name, activate)
 VALUES           (9001,    '',          2,        'Testsida',  '',        '',         1,        0,           0,      1,      0,         1,            0,       1,         'se',        '',             CURRENT_TIMESTAMP,    CURRENT_TIMESTAMP,     1,             1,             0,              CURRENT_TIMESTAMP,   null,        '_self',  '',         1);
INSERT INTO text_docs VALUES (9001, 1, 0, 1, -1, -1);

INSERT INTO meta (meta_id, description, doc_type, meta_headline, meta_text, meta_image, owner_id, permissions, shared, expand, show_meta, help_text_id, archive, status_id, lang_prefix, classification, date_created, date_modified, sort_position, menu_position, disable_search, activated_datetime, archived_datetime, target,  frame_name, activate)
 VALUES           (9002,    '',          2,        'Testsida',  '',        '',         1,        0,           0,      1,      0,         1,            0,       1,         'se',        '',             CURRENT_TIMESTAMP,    CURRENT_TIMESTAMP,     1,             1,             0,              CURRENT_TIMESTAMP,   null,        '_self',  '',         1);
INSERT INTO text_docs VALUES (9002, 1, 0, 1, -1, -1);

INSERT INTO childs (meta_id, to_meta_id, menu_sort, manual_sort_order)
 VALUES (9001, 9002,1,500);

INSERT INTO meta (meta_id, description, doc_type, meta_headline, meta_text, meta_image, owner_id, permissions, shared, expand, show_meta, help_text_id, archive, status_id, lang_prefix, classification, date_created, date_modified, sort_position, menu_position, disable_search, activated_datetime, archived_datetime, target,  frame_name, activate)
 VALUES           (9003,    '',          8,        'Testsida',  '',        '',         1,        0,           0,      1,      0,         1,            0,       1,         'se',        '',             CURRENT_TIMESTAMP,    CURRENT_TIMESTAMP,     1,             1,             0,              CURRENT_TIMESTAMP,   null,        '_self',  '',         1);
INSERT INTO fileupload_docs VALUES (9003, 'testfilename.txt', 'text/plain');

INSERT INTO meta (meta_id, description, doc_type, meta_headline, meta_text, meta_image, owner_id, permissions, shared, expand, show_meta, help_text_id, archive, status_id, lang_prefix, classification, date_created, date_modified, sort_position, menu_position, disable_search, activated_datetime, archived_datetime, target,  frame_name, activate)
 VALUES           (9999,    '',          2,        'Testsida',  '',        '',         1,        0,           0,      1,      0,         1,            0,       1,         'se',        '',             CURRENT_TIMESTAMP,    CURRENT_TIMESTAMP,     1,             1,             0,              CURRENT_TIMESTAMP,   null,        '_self',  '',         1);
INSERT INTO text_docs VALUES (9999, 1, 0, 1, -1, -1);

INSERT INTO users VALUES (3,'TestUser', 'TestUser', 'TestUser', 'TestUser','','','','','','','','',0,1001,0,1,1,1,CURRENT_TIMESTAMP);
INSERT INTO roles VALUES(3, 'TestRole', 1, 0);
INSERT INTO phones VALUES (1, '666-666', 3, 0);
INSERT INTO user_roles_crossref VALUES(3,3);

INSERT INTO roles_rights VALUES(3,9001,1);
INSERT INTO doc_permission_sets VALUES(9001,1,8);
INSERT INTO doc_permission_sets_ex VALUES(9001,1,8,2);
