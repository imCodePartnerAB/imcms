INSERT INTO imcms_languages
  (`id`, `code`, `name`, `native_name`, `enabled`)
VALUES
  (1, 'en', 'English', 'English', true),
  (2, 'sv', 'Swedish', 'Svenska', true);
  
INSERT INTO sys_types (type_id, name) VALUES (8, 'DefaultLanguageId');
INSERT INTO sys_data (type_id, value) VALUES (8, 1);


