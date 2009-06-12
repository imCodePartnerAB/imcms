-- Any customer product using this schema version before the fix date shoud
-- apply this script.  

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