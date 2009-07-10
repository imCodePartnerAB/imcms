-- Database schema version
SET @database_version__major = 6;
SET @database_version__minor = 1;

-- Change meta_version engine type
CREATE TABLE __meta_version (
  id int NOT NULL auto_increment PRIMARY KEY,
  meta_id int NOT NULL,
  version int NOT NULL,
  version_tag varchar(12) NOT NULL,
  user_id int NULL,
  created_dt datetime NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO __meta_version (meta_id, version, version_tag, user_id, created_dt)
SELECT meta_id, version, version_tag, user_id, created_dt FROM meta_version;

DROP TABLE meta_version;
RENAME TABLE __meta_version TO meta_version;

DELETE FROM meta_version WHERE meta_id NOT IN 
	(SELECT meta_id FROM meta);	
	
ALTER TABLE meta_version
  ADD CONSTRAINT fk__meta_version__user FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE SET NULL,
  ADD CONSTRAINT fk__meta_version__meta FOREIGN KEY (meta_id) REFERENCES meta (meta_id) ON DELETE CASCADE;

  
-- Adds working version (as latest) to all documents which do not have it 
INSERT INTO meta_version (
    meta_id, 
    version, 
    version_tag, 
    user_id, 
    created_dt
) SELECT 
      mv.meta_id, 
      mv.max_version + 1, 
      'WORKING', 
      1, 
      now() 
  FROM meta_version v 
  JOIN (SELECT meta_id, max(version) AS max_version 
        FROM meta_version 
        GROUP BY meta_id) mv 
  ON v.meta_id = mv.meta_id AND v.version = mv.max_version 
  WHERE v.version_tag <> 'WORKING';

-- Insert latest, non working text version as working  		  
INSERT INTO texts (
    meta_id, 
    meta_version, 
    name, 
    language_id,
    text, 
    type     
) SELECT 
      meta_id, 
      (SELECT version FROM meta_version WHERE meta_id = t.meta_id AND version_tag = 'WORKING') AS version,  
      name, 
      language_id, 
      text, 
      type
  FROM texts t 
  WHERE (meta_id, meta_version, name, language_id) 
  IN (SELECT t.meta_id, t.max_version, t.name, t.language_id
      FROM meta_version v 
      JOIN (SELECT meta_id, max(meta_version) AS max_version, name, language_id 
            FROM texts 
            GROUP BY meta_id, name, language_id) t 
      ON v.meta_id = t.meta_id AND v.version = t.max_version 
      WHERE v.version_tag <> 'WORKING');
      
-- update tests history
INSERT INTO texts_history (
    meta_id, 
    meta_version, 
    name, 
    language_id,
    text, 
    type,
    modified_datetime,
    user_id
) SELECT   
    t.meta_id, 
    t.meta_version, 
    t.name, 
    t.language_id,
    t.text, 
    t.type,
    now(),
    1
FROM texts t
LEFT JOIN texts_history h
ON t.meta_id = h.meta_id AND t.meta_version = h.meta_version AND t.name = h.name AND t.language_id = h.language_id 
WHERE h.meta_id IS NULL;
      
-- Insert latest, non working image version as working  		  
INSERT INTO images (
    meta_id, 
    meta_version, 
    name, 
    language_id,   
    width,
    height,
    border,
    v_space,
    h_space,
    image_name,
    target,
    align,
    alt_text,
    low_scr,
    imgurl,
    linkurl,
    type
) SELECT 
      meta_id, 
      (SELECT version FROM meta_version WHERE meta_id = i.meta_id AND version_tag = 'WORKING') AS version,  
        name, 
        language_id, 
	    width,
	    height,
	    border,
		v_space,
		h_space,
		image_name,
		target,
		align,
		alt_text,
		low_scr,
		imgurl,
		linkurl,
		type
  FROM images i 
  WHERE (meta_id, meta_version, name, language_id) 
  IN (SELECT i.meta_id, i.max_version, i.name, i.language_id
      FROM meta_version v 
      JOIN (SELECT meta_id, max(meta_version) AS max_version, name, language_id 
            FROM images 
            GROUP BY meta_id, name, language_id) i 
      ON v.meta_id = i.meta_id AND v.version = i.max_version 
      WHERE v.version_tag <> 'WORKING');

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
    ADD CONSTRAINT fk__texts__meta_id__meta_version FOREIGN KEY (meta_id, meta_version) REFERENCES meta_version (meta_id, version) ON DELETE CASCADE;
 
ALTER TABLE images
    ADD CONSTRAINT fk__images__meta_id__meta_version FOREIGN KEY (meta_id, meta_version) REFERENCES meta_version (meta_id, version) ON DELETE CASCADE;
        

ALTER TABLE text_doc_content_loops
    DROP COLUMN old_id;

ALTER TABLE text_doc_content_loops
    ADD CONSTRAINT fk__text_doc_content_loops__meta_id__meta_version FOREIGN KEY (meta_id, meta_version) REFERENCES meta_version (meta_id, version) ON DELETE CASCADE;

ALTER TABLE text_doc_content_loops
    ADD CONSTRAINT uk__text_doc_content_loops__meta_id__meta_version__loop_index UNIQUE KEY (meta_id, meta_version, loop_index);
  
ALTER TABLE text_doc_contents
    ADD CONSTRAINT uk__loop_id__sequence_index UNIQUE KEY (loop_id, sequence_index),
    ADD CONSTRAINT uk__loop_id__order_index UNIQUE KEY (loop_id, order_index),
    ADD CONSTRAINT fk__text_doc_contents__text_doc_content_loops FOREIGN KEY (loop_id) REFERENCES text_doc_content_loops (id) ON DELETE CASCADE;

--
-- Update schema version
--
UPDATE database_version
SET
  major = @database_version__major__new,
  minor = @database_version__minor__new;   
/*    
-- Refactor i18n_meta table:
CREATE TABLE __i18n_meta (
  id int NOT NULL auto_increment PRIMARY KEY,
  language_id int default NULL,
  meta_id int default NULL,
  -- meta_version -- or meta_id != document_id
  enabled tinyint(1) NOT NULL default '0',
  headline varchar(255) default NULL,
  text varchar(1000) default NULL,
  image varchar(255) default NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;   

meta:
  id <- meta_id for related tables???
  meta_id will become document_id 1001, 1002, .. n
  
meta_version:
  id 
  meta_id
  meta_version  


  CONSTRAINT uk__i18n_meta__meta_id__language_id UNIQUE KEY (meta_id, language_id),
  CONSTRAINT fk__i18n_meta__meta FOREIGN KEY (meta_id) REFERENCES meta (meta_id) ON DELETE CASCADE,
  CONSTRAINT fk__i18n_meta__language FOREIGN KEY (language_id) REFERENCES i18n_languages (language_id)

 */
