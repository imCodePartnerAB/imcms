CREATE TABLE IF NOT EXISTS meta (
  meta_id int NOT NULL PRIMARY KEY  
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS users (
  user_id int NOT NULL PRIMARY KEY  
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `imcms_doc_versions` (
  `id` int AUTO_INCREMENT PRIMARY KEY,
  `doc_id` int NOT NULL,
  `no` int NOT NULL,
  `created_by` int NULL,
  `created_dt` datetime NOT NULL,
  `modified_by` int NULL,
  `modified_dt` datetime NULL,

  CONSTRAINT `uk__imcms_doc_versions__doc_id__no` UNIQUE KEY (`doc_id`,`no`),
  CONSTRAINT `fk__imcms_doc_versions__meta` FOREIGN KEY (`doc_id`) REFERENCES `meta` (`meta_id`) ON DELETE CASCADE,
  CONSTRAINT `fk__imcms_doc_versions__user1` FOREIGN KEY (`created_by`) REFERENCES `users` (`user_id`) ON DELETE SET NULL,
  CONSTRAINT `fk__imcms_doc_versions__user2` FOREIGN KEY (`modified_by`) REFERENCES `users` (`user_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `imcms_text_doc_content_loops` (
  `id` int AUTO_INCREMENT PRIMARY KEY,
  `doc_id` int NOT NULL,
  `doc_version_no` int NOT NULL,
  `no` int NOT NULL,

  CONSTRAINT `uk__imcms_text_doc_content_loops` UNIQUE KEY (`doc_id`,`doc_version_no`,`no`),
  CONSTRAINT `fk__imcms_text_doc_content_loops__imcms_doc_versions` FOREIGN KEY (`doc_id`, `doc_version_no`) REFERENCES `imcms_doc_versions` (`doc_id`, `no`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS `imcms_text_doc_contents` (
  `id` int AUTO_INCREMENT PRIMARY KEY,
  `doc_id` int NOT NULL,
  `doc_version_no` int NOT NULL,
  `loop_no` int DEFAULT NULL,
  `no` int NOT NULL,
  `order_no` int NOT NULL,
  `enabled` tinyint NOT NULL DEFAULT true,

  CONSTRAINT `uk__imcms_text_doc_contents__doc_id__doc_version_no__loop_no__no` UNIQUE KEY (`doc_id`,`doc_version_no`,`loop_no`,`no`),
  CONSTRAINT `fk__imcms_text_doc_contents__imcms_doc_versions` FOREIGN KEY (`doc_id`, `doc_version_no`) REFERENCES `imcms_doc_versions` (`doc_id`, `no`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DELETE FROM meta;
DELETE FROM users;

INSERT INTO meta VALUES(1001);
INSERT INTO users VALUES(0);
INSERT INTO imcms_doc_versions (doc_id, no, created_dt) VALUES (1001, 0, 0);


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