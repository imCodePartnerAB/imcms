-- Drops tables which are not in use any more
DROP TABLE IF EXISTS meta_section;
DROP TABLE IF EXISTS sections;

--
-- Add version support to content loops
--  
CREATE TABLE __text_doc_content_loops (
  id int auto_increment PRIMARY KEY,
  old_id int NOT NULL,
  meta_id int NOT NULL,
  meta_version int NOT NULL,
  loop_index int NOT NULL,
  base_index int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO __text_doc_content_loops (
	old_id, meta_id, meta_version, loop_index, base_index
) SELECT 
    l.id, l.meta_id, v.version, l.loop_index, l.base_index 
  FROM 
    text_doc_content_loops l 
  JOIN 
  	meta_version v 
  ON 
   	l.meta_id = v.meta_id;
    	
CREATE TABLE __text_doc_contents (
  id int auto_increment PRIMARY KEY,
  loop_id int,
  sequence_index int NOT NULL,
  order_index int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO __text_doc_contents (
    loop_id, sequence_index, order_index
) SELECT 
	l.id, c.sequence_index, c.order_index 
  FROM 
    text_doc_contents c 
  JOIN 
    __text_doc_content_loops l 
  ON c.loop_id = l.old_id;
  
  
DROP TABLE text_doc_contents;
DROP TABLE text_doc_content_loops;

RENAME TABLE __text_doc_contents TO text_doc_contents;
RENAME TABLE __text_doc_content_loops TO text_doc_content_loops;

ALTER TABLE meta_version
    ADD CONSTRAINT uk__meta_version__meta_id__version UNIQUE KEY (meta_id, version);

ALTER TABLE texts
    CONSTRAINT fk__texts__meta_id__meta_version FOREIGN KEY (meta_id, meta_version) REFERENCES meta_version (meta_id, version) ON DELETE CASCADE;
 
ALTER TABLE images
    CONSTRAINT fk__images__meta_id__meta_version FOREIGN KEY (meta_id, meta_version) REFERENCES meta_version (meta_id, version) ON DELETE CASCADE;
        

ALTER TABLE text_doc_content_loops
    DROP COLUMN old_id,
    ADD CONSTRAINT fk__text_doc_content_loops__meta_id__meta_version FOREIGN KEY (meta_id, meta_version) REFERENCES meta_version (meta_id, version) ON DELETE CASCADE,
    ADD CONSTRAINT uk__text_doc_content_loops__meta_id__meta_version__loop_index UNIQUE KEY (meta_id, meta_version, loop_index);
  
ALTER TABLE text_doc_contents
    ADD CONSTRAINT uk__loop_id__sequence_index UNIQUE KEY (loop_id, sequence_index),
    ADD CONSTRAINT uk__loop_id__order_index UNIQUE KEY (loop_id, order_index),
    ADD CONSTRAINT fk__text_doc_contents__text_doc_content_loops FOREIGN KEY (loop_id) REFERENCES text_doc_content_loops (id) ON DELETE CASCADE;